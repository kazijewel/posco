package com.appform.hrmModule;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.AmountField;
import com.common.share.CommonButton;
import com.common.share.CommonMethod;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class CheckNoEntry extends Window {
	private CommonButton cButton = new CommonButton("New", "Save", "Edit", "","Refresh", "Find", "", "", "", "Exit");
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;
	private DecimalFormat twoDigit = new DecimalFormat("#0.00");
	private DecimalFormat twoDigit1 = new DecimalFormat("#0");
	private Label lblYear = new Label("Year :");
	private boolean isFind = false;

	private TextRead txtTransactionId;
	
	private Label lblFiscalYear,lblTaxYear,lblPaymentType,lblChallanDDno,lblChechChallanDate,lblAmount;
	private TextField txtFiscalYear,txtChallanDDno;
	private TextRead txtTaxYear;
	private ComboBox cmbPaymentType;	
	private PopupDateField dChechChallanDate;
	private AmountField txtAmount;
	
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private DecimalFormat df = new DecimalFormat("#0");
	private boolean isUpdate = false;
	
	private TextRead trnId = new TextRead("");
	

	String transactionId = "";
	private CommonMethod cm;
	private ArrayList<Component> allComp = new ArrayList<Component>(); 
	private String menuId = "";
	public CheckNoEntry(SessionBean sessionBean,String menuId) 
	{
		this.sessionBean = sessionBean;
		this.setCaption(" CHEQUE NO ENTRY :: " + sessionBean.getCompany());
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		buildMainLayout();
		setContent(mainLayout);
		btnIni(true);
		componentIni(true);
		setBtnAction();
		focusEnter();
		authenticationCheck();
	}
	private void authenticationCheck()
	{
		cm.checkFormAction(menuId);
		if(!sessionBean.isSuperAdmin())
		{
		if(!sessionBean.isAdmin())
		{
			if(!cm.isSave)
			{cButton.btnSave.setVisible(false);}
			if(!cm.isEdit)
			{cButton.btnEdit.setVisible(false);}
			if(!cm.isDelete)
			{cButton.btnDelete.setVisible(false);}
			if(!cm.isPreview)
			{cButton.btnPreview.setVisible(false);}
		}
		}
	}
	private void focusEnter()
	{
		allComp.add(txtFiscalYear);
		allComp.add(txtTaxYear);
		allComp.add(cmbPaymentType);
		allComp.add(txtChallanDDno);
		allComp.add(dChechChallanDate);
		allComp.add(txtAmount);
		
		allComp.add(cButton.btnSave);

		new FocusMoveByEnter(this,allComp);
	}
	private String selectTransactionId()
	{
		String DepartmentId = "";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select isnull(max(cast(SUBSTRING(vTransactionId,5,LEN(vTransactionId)) as int)),0)+1 from tbCheckDetails ";
			Iterator<?> iter = session.createSQLQuery(query).list().iterator();
			if(iter.hasNext())
			{
				DepartmentId = "TRNS"+iter.next().toString();
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		finally
		{
			session.close();
		}

		return DepartmentId;
	}
	private void setBtnAction() 
	{
		cButton.btnNew.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				txtClear();
				isUpdate = false;
				isFind = false;
				txtFiscalYear.focus();
				newButtonEvent();
				txtTransactionId.setValue(selectTransactionId());
			}
		});

		txtFiscalYear.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if (!txtFiscalYear.getValue().toString().isEmpty()) 
				{
					if(!isFind)
					{
						txtTaxYear.setValue(TexYear());
					}					
				}
			}
		});

		cButton.btnFind.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				isFind = true;
				findButtonEvent();
			}
		});

		cButton.btnRefresh.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				isUpdate = false;
				txtClear();
				isFind = false;
				refreshButtonEvent();

			}
		});

		cButton.btnSave.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) 
			{
				formValidation();
			}
		});

		cButton.btnEdit.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				if(!txtFiscalYear.getValue().toString().trim().isEmpty())
				{
					btnIni(false);
					componentIni(false);
					isUpdate = true;
					isFind = false;
				}
				else
				{
					showNotification("Update Failed","There are no data for update.",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		cButton.btnExit.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				close();
			}
		});
	}

	private void findButtonEvent() {

		Window win = new ChechnoFind(sessionBean, trnId);
		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() {
			public void windowClose(CloseEvent e) {
				if (trnId.getValue().toString().length() > 0) 
				{
					transactionId = trnId.getValue().toString();
					findInitialize(transactionId);
				}
			}
		});

		this.getParent().addWindow(win);
	}

	private void findInitialize(String transactionId) {

		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try {

			String sql = "select distinct vFiscalYearId,vTaxYear,mPaymentType,mCheckNo,mCheckDate,mChallanNo,mChallanDate,mAmount from tbCheckDetails where vTransactionId='"+transactionId+"'  ";
			System.out.println("findInitialize: " + sql);
			List<?> list = session.createSQLQuery(sql).list();

			if (!list.isEmpty()) 
			{
				if (list.iterator().hasNext()) 
				{
					Object[] element = (Object[]) list.iterator().next();
					txtTransactionId.setValue(transactionId);
					
					txtFiscalYear.setValue(element[0]);
					txtTaxYear.setValue(element[1]);
					cmbPaymentType.setValue(element[2]);
					if(element[2].toString().equalsIgnoreCase("Cheque/DD No")) {
						txtChallanDDno.setValue(element[3]);
						dChechChallanDate.setValue(element[4]);
					}
					else {
						txtChallanDDno.setValue(element[5]);
						dChechChallanDate.setValue(element[6]);
					}
					txtAmount.setValue(df.format(element[7]));
				}
			} 
			else {
				showNotification("Warning!", "DD NO  ",Notification.TYPE_WARNING_MESSAGE);
			}

		} 
		catch (Exception ex) {
			showNotification("findInitialize", ex.toString(),Notification.TYPE_ERROR_MESSAGE);
		} 
		finally {
			session.close();
		}
	}

	private String TexYear() {
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();

		String sql1 = "select cast(cast(Substring('"
				+ txtFiscalYear.getValue().toString()
				+ "',1,4) as int)+1 as varchar(120))+'-'+cast(cast(Substring('"
				+ txtFiscalYear.getValue().toString()
				+ "',1,4) as int)+2 as varchar(120))";

		String Tax = "0";

		Iterator iter = session.createSQLQuery(sql1).list().iterator();
		if (iter.hasNext()) {
			return iter.next().toString();
		}

		System.out.println("Tax" + Tax);
		return Tax;
	}

	private void refreshButtonEvent() {
		componentIni(true);
		btnIni(true);
		txtClear();

	}

	private void newButtonEvent() {
		componentIni(false);
		btnIni(false);
		txtClear();

	}

	private void formValidation() 
	{
		if (!txtFiscalYear.getValue().toString().isEmpty()) 
		{
			if (!txtTaxYear.getValue().toString().isEmpty()) 
			{
				if (cmbPaymentType.getValue() != null) 
				{
					if (!txtChallanDDno.getValue().toString().isEmpty()) 
					{
						if (!dChechChallanDate.getValue().toString().isEmpty()) 
						{
							if (!txtAmount.getValue().toString().isEmpty()) 
							{
								if (!txtTransactionId.getValue().toString().isEmpty()) 
								{
									saveButtonEvent();
								} 
								else {
									showNotification("Warning!", "Provide TransactionId",Notification.TYPE_WARNING_MESSAGE);
								}
							} 
							else {
								showNotification("Warning!", "Provide  Amount",Notification.TYPE_WARNING_MESSAGE);
								txtAmount.focus();
							}
						} 
						else {
							showNotification("Warning!", "Provide Date",Notification.TYPE_WARNING_MESSAGE);
							dChechChallanDate.focus();
						}
					} 
					else {
						showNotification("Warning!","Provide Cheque/DD/Challan No",Notification.TYPE_WARNING_MESSAGE);
						txtChallanDDno.focus();
					}
				} 
				else {
					showNotification("Warning!", "Provide Payment Amount ",Notification.TYPE_WARNING_MESSAGE);
					cmbPaymentType.focus();
				}
			}
			else {
				showNotification("Warning!", "Provide Tax Year ",Notification.TYPE_WARNING_MESSAGE);
			}
		} 
		else {
			showNotification("Warning!", "Provide Fiscal  Year",Notification.TYPE_WARNING_MESSAGE);
			txtFiscalYear.focus();
		}
	}

	private void saveButtonEvent() 
	{
		if (isUpdate) 
		{
			MessageBox mb = new MessageBox(getParent(), "Are you sure?",MessageBox.Icon.QUESTION,"Do you want to update information?",new MessageBox.ButtonConfig(MessageBox.ButtonType.YES,"Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener() 
			{
				public void buttonClicked(ButtonType buttonType) {
					if (buttonType == ButtonType.YES) 
					{
						if (isUpdate) 
						{
							updateData();
							btnIni(true);
							componentIni(true);
							txtClear();
							cButton.btnNew.focus();
						}
						isUpdate = false;
						isFind = false;
					}
				}
			});
		} 
		else 
		{
			MessageBox mb = new MessageBox(getParent(), "Are you sure?",MessageBox.Icon.QUESTION,"Do you want to save information?",new MessageBox.ButtonConfig(MessageBox.ButtonType.YES,"Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener() 
			{
				public void buttonClicked(ButtonType buttonType) 
				{
					if (buttonType == ButtonType.YES) 
					{
						insertData();
						btnIni(true);
						componentIni(true);
						txtClear();
						cButton.btnNew.focus();
					}
				}
			});
		}
	}

	private void insertData() 
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try 
		{
			if (cmbPaymentType.getValue() == "Cheque/DD No") 
			{
				String Insert = "Insert into tbCheckDetails(vTransactionId,vFiscalYearId,vTaxYear,mPaymentType,mCheckNo,mCheckDate,mChallanNo,mChallanDate," +
						"mAmount,userIp,userName,dEntryTime) " +
						"values(" +
						"'"+txtTransactionId.getValue().toString()+"'," +
						"'"+txtFiscalYear.getValue().toString()+"'," +
						"'"+txtTaxYear.getValue().toString()+ "'," +
						"'"+cmbPaymentType.getValue().toString()+"'," +"'"
						+txtChallanDDno.getValue().toString()
						+"',"
						+"'"
						+dateFormat.format(dChechChallanDate.getValue())
						+"','','','"
						+txtAmount.getValue().toString()
						+"',"
						+"'"
						+sessionBean.getUserIp()
						+"','"
						+sessionBean.getUserName() + "',CURRENT_TIMESTAMP)";

				session.createSQLQuery(Insert).executeUpdate();
			} 
			else 
			{
				String InsertTwo = "Insert into tbCheckDetails(vTransactionId,vFiscalYearId,vTaxYear,mPaymentType,mCheckNo,mCheckDate,mChallanNo,mChallanDate," +
						"mAmount,userIp,userName,dEntryTime) " +
						"values(" +
						"'"+ txtTransactionId.getValue().toString()+ "',"+
						"'"+ txtFiscalYear.getValue().toString()+ "',"+
						"'"+ txtTaxYear.getValue().toString()+ "',"+
						"'"+ cmbPaymentType.getValue().toString()
						+ "',"
						+ "'','','"
						+ txtChallanDDno.getValue().toString()
						+ "',"
						+ "'"
						+ dateFormat.format(dChechChallanDate.getValue())
						+ "',"
						+ "'"
						+ txtAmount.getValue().toString()
						+ "',"
						+ "'"
						+ sessionBean.getUserIp()
						+ "',"
						+ "'"
						+ sessionBean.getUserName()
						+ "',"
						+ "CURRENT_TIMESTAMP)";

				session.createSQLQuery(InsertTwo).executeUpdate();
			}
			tx.commit();
			showNotification("All information saved successfully.");
		} 
		catch (Exception exp) {
			showNotification("insertData", exp + "",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		} 
		finally {
			session.close();
		}
	}

	private void updateData() {
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		String sql="";
		try 
		{
			String sqlDelete="delete from tbCheckDetails where vTransactionId='"+txtTransactionId.getValue()+"'";
			session.createSQLQuery(sqlDelete).executeUpdate();
			
			if (cmbPaymentType.getValue() == "Cheque/DD No") 
			{
				String Insert = "Insert into tbCheckDetails(vTransactionId,vFiscalYearId,vTaxYear,mPaymentType,mCheckNo,mCheckDate,mChallanNo,mChallanDate," +
						"mAmount,userIp,userName,dEntryTime) " +
						"values(" +
						"'"+txtTransactionId.getValue().toString()+"'," +
						"'"+txtFiscalYear.getValue().toString()+"'," +
						"'"+txtTaxYear.getValue().toString()+ "'," +
						"'"+cmbPaymentType.getValue().toString()+"'," +"'"
						+txtChallanDDno.getValue().toString()
						+"',"
						+"'"
						+dateFormat.format(dChechChallanDate.getValue())
						+"','','','"
						+txtAmount.getValue().toString()
						+"',"
						+"'"
						+sessionBean.getUserIp()
						+"','"
						+sessionBean.getUserName() + "',CURRENT_TIMESTAMP)";

				session.createSQLQuery(Insert).executeUpdate();
			} 
			else 
			{
				String InsertTwo = "Insert into tbCheckDetails(vTransactionId,vFiscalYearId,vTaxYear,mPaymentType,mCheckNo,mCheckDate,mChallanNo,mChallanDate," +
						"mAmount,userIp,userName,dEntryTime) " +
						"values(" +
						"'"+ txtTransactionId.getValue().toString()+ "',"+
						"'"+ txtFiscalYear.getValue().toString()+ "',"+
						"'"+ txtTaxYear.getValue().toString()+ "',"+
						"'"+ cmbPaymentType.getValue().toString()
						+ "',"
						+ "'','','"
						+ txtChallanDDno.getValue().toString()
						+ "',"
						+ "'"
						+ dateFormat.format(dChechChallanDate.getValue())
						+ "',"
						+ "'"
						+ txtAmount.getValue().toString()
						+ "',"
						+ "'"
						+ sessionBean.getUserIp()
						+ "',"
						+ "'"
						+ sessionBean.getUserName()
						+ "',"
						+ "CURRENT_TIMESTAMP)";

				session.createSQLQuery(InsertTwo).executeUpdate();
			}
			tx.commit();
			showNotification("All information update successfully.");
		} 
		catch (Exception exp) {
			showNotification("insertData", exp + "",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		} 
		finally {
			session.close();
		}
	}

	private void btnIni(boolean t) {
		cButton.btnNew.setEnabled(t);
		cButton.btnEdit.setEnabled(t);
		cButton.btnSave.setEnabled(!t);
		cButton.btnRefresh.setEnabled(!t);
		cButton.btnDelete.setEnabled(t);
		cButton.btnFind.setEnabled(t);
	}

	private void componentIni(boolean t) {
		txtFiscalYear.setEnabled(!t);
		txtTaxYear.setEnabled(!t);
		cmbPaymentType.setEnabled(!t);
		txtChallanDDno.setEnabled(!t);
		dChechChallanDate.setEnabled(!t);
		txtAmount.setEnabled(!t);
	}

	private void txtClear() {
		txtTransactionId.setValue("");
		txtFiscalYear.setValue("");
		txtTaxYear.setValue("");
		cmbPaymentType.setValue(null);
		txtChallanDDno.setValue("");
		dChechChallanDate.setValue(new java.util.Date());
		txtAmount.setValue("");
	}

	public AbsoluteLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("600px");
		mainLayout.setHeight("280px");
		

		txtTransactionId = new TextRead();
		txtTransactionId.setImmediate(true);
		txtTransactionId.setWidth("110px");
		txtTransactionId.setHeight("22px");
		mainLayout.addComponent(new Label("TransactionId: "), "top:20.0px; left:350.0px;");
		mainLayout.addComponent(txtTransactionId, "top:18.0px; left:440.0px;");

		// lblEmployeeName
		lblFiscalYear = new Label("Fiscal Year    :");
		lblFiscalYear.setImmediate(false);
		lblFiscalYear.setWidth("-1px");
		lblFiscalYear.setHeight("-1px");
		mainLayout.addComponent(lblFiscalYear, "top:20.0px; left:45.0px;");

		// txtEmployeeName
		txtFiscalYear = new TextField();
		txtFiscalYear.setImmediate(true);
		txtFiscalYear.setWidth("110px");
		txtFiscalYear.setHeight("-1px");
		mainLayout.addComponent(txtFiscalYear, "top:18.0px; left:170.0px;");

		// lblEmployeeName
		lblTaxYear = new Label("Tax Year    :");
		lblTaxYear.setImmediate(false);
		lblTaxYear.setWidth("-1px");
		lblTaxYear.setHeight("-1px");
		mainLayout.addComponent(lblTaxYear, "top:50.0px; left:45.0px;");

		// txtEmployeeName
		txtTaxYear = new TextRead();
		txtTaxYear.setImmediate(true);
		txtTaxYear.setWidth("110px");
		txtTaxYear.setHeight("22px");
		mainLayout.addComponent(txtTaxYear, "top:48.0px; left:170.0px;");

		// lblEmployeeId
		lblPaymentType = new Label("Payment Type     :");
		lblPaymentType.setImmediate(false);
		lblPaymentType.setWidth("-1px");
		lblPaymentType.setHeight("-1px");
		mainLayout.addComponent(lblPaymentType, "top:80.0px; left:45.0px;");

		// cmbEmployeeID
		cmbPaymentType = new ComboBox();
		cmbPaymentType.setImmediate(true);
		cmbPaymentType.setWidth("150px");
		cmbPaymentType.setHeight("-1px");
		cmbPaymentType.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbPaymentType, "top:78.0px; left:170.0px;");

		cmbPaymentType.addItem("Cheque/DD No");
		cmbPaymentType.addItem("Challan No");
		
		
		// lblEmployeeName
		lblChallanDDno = new Label("Cheque/DD/Challan No :");
		lblChallanDDno.setImmediate(false);
		lblChallanDDno.setWidth("-1px");
		lblChallanDDno.setHeight("-1px");
		mainLayout.addComponent(lblChallanDDno, "top:110.0px; left:45.0px;");

		// txtEmployeeName
		txtChallanDDno = new TextField();
		txtChallanDDno.setImmediate(true);
		txtChallanDDno.setWidth("230px");
		txtChallanDDno.setHeight("-1px");
		mainLayout.addComponent(txtChallanDDno, "top:108.0px; left:170.0px;");

		// lblChechChallanDate
		lblChechChallanDate = new Label(" Date   : ");
		lblChechChallanDate.setImmediate(false);
		lblChechChallanDate.setWidth("-1px");
		lblChechChallanDate.setHeight("-1px");
		mainLayout.addComponent(lblChechChallanDate,"top:145.0px; left:45.0px;");

		// dChechChallanDate
		dChechChallanDate = new PopupDateField();
		dChechChallanDate.setImmediate(false);
		dChechChallanDate.setWidth("130px");
		dChechChallanDate.setHeight("-1px");
		dChechChallanDate.setValue(new java.util.Date());
		dChechChallanDate.setDateFormat("dd-MM-yyyy");
		dChechChallanDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dChechChallanDate, "top:143.0px; left:170.0px;");

		// lblEtin
		lblAmount = new Label("Amount   :");
		lblAmount.setImmediate(false);
		lblAmount.setWidth("-1px");
		lblAmount.setHeight("-1px");
		mainLayout.addComponent(lblAmount, "top:175.0px; left:45.0px;");

		// txtSanctionAmount
		txtAmount = new AmountField();
		txtAmount.setImmediate(true);
		txtAmount.setWidth("130px");
		txtAmount.setHeight("-1px");
		mainLayout.addComponent(txtAmount, "top:173.0px; left:170.0px;");

		mainLayout.addComponent(cButton, "top:210.0px; left:30.0px;");
		return mainLayout;
	}
}
