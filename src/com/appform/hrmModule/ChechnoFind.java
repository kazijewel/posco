package com.appform.hrmModule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

import com.common.share.*;
import com.vaadin.ui.TextField;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;


@SuppressWarnings("serial")
public class ChechnoFind extends Window 
{
	private AbsoluteLayout mainLayout;
	
	private InlineDateField dYear = new InlineDateField();
	private Label lbEmp = new Label("Payment Type :");	
	private ComboBox cmbPaymentType = new ComboBox();
	private CheckBox ChkType=new CheckBox("ALL");	
	private Label lblFiscalYear;
	private ComboBox cmbFiscalYear;
	
	private TextRead transactionId;
	
	@SuppressWarnings("unused")
	private SessionBean sessionBean;

	String computerName = "";
	String userName = "";
	String year = "";
	String EmpID = "";
	String deptID = "";
	String receiptempID = "";
	String receiptEmptID = "";
	
	private static final String[] PaymentType = new String[] {"Cheque/DD No","Challan No"};	
	
//	private TextRead txtCheck;
	
	private DecimalFormat twoDigit = new DecimalFormat("#0.00");
	private DecimalFormat twoDigit1 = new DecimalFormat("#0");
	private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");

	private Table table = new Table();
	private ArrayList<Label> lbSl = new ArrayList<Label>();
	private ArrayList<Label> lbliAuto = new ArrayList<Label>();	
	private ArrayList<Label> lblPaymentType=new ArrayList<Label>();
	private ArrayList<Label> lbFiscalYear = new ArrayList<Label>();
	private ArrayList<Label> lbTAXYear = new ArrayList<Label>();
	private ArrayList<Label> lbChallanNo = new ArrayList<Label>();
	private ArrayList<Label> lbCheckNO = new ArrayList<Label>();
	private ArrayList<Label> amtAmount = new ArrayList<Label>();

	private ArrayList<Label> lbTransactionId = new ArrayList<Label>();
	
	
	public ChechnoFind(SessionBean sessionBean, TextRead transactionId)
	{
				
		this.transactionId = transactionId;
		
		this.sessionBean=sessionBean;
		computerName = sessionBean.getUserName();
		userName = sessionBean.getUserName();
		this.setCaption("FIND CHEQUE NO  :: "+sessionBean.getCompany());
		this.setResizable(false);
		buildMainLayout();
		setContent(mainLayout);
		tableInitialize();
		cmbFiscalYearDataLoad();
		btnAction();
	}
	public void cmbFiscalYearDataLoad()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		try {
			String sql="select distinct vFiscalYearId,0 from tbCheckDetails";
			Iterator<?>iter=session.createSQLQuery(sql).list().iterator();
			while(iter.hasNext())
			{
				Object element[]=(Object[]) iter.next();
				cmbFiscalYear.addItem(element[0]);
			}
		} 
		catch (Exception exp) {
			showNotification(exp+"cmbFiscalYearDataLoad",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{
			session.close();
		}
	}
	private void btnAction()
	{
		cmbPaymentType.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbPaymentType.getValue()!=null)
				{			
			        tableclear();
					tableDataAdding();
				}
			}
		});

		ChkType.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event)
			{
				boolean bln = ChkType.booleanValue();
				if(bln==true)
				{
					cmbPaymentType.setValue(null);
					cmbPaymentType.setEnabled(false);					
					tableDataAdding();					
				}
				else
				{
					tableclear();
					cmbPaymentType.setEnabled(true);
				}
			}
		});


		table.addListener(new ItemClickListener()
		{
			public void itemClick(ItemClickEvent event)
			{
				if(event.isDoubleClick())
				{
					receiptempID =lbTransactionId.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					transactionId.setValue(receiptempID);												
					windowClose();
				}				
			}
		});
	}

	private void windowClose()
	{
		this.close();
	}

	private void tableclear()
	{
		for(int i=0; i<lbliAuto.size(); i++)
		{
			lblPaymentType.get(i).setValue("");
			lbFiscalYear.get(i).setValue("");
			lbTAXYear.get(i).setValue("");
			lbChallanNo.get(i).setValue("");
			lbCheckNO.get(i).setValue("");
			amtAmount.get(i).setValue("");
			lbTransactionId.get(i).setValue("");
		}
	}

	
	private void tableInitialize()
	{
		table.setColumnCollapsingAllowed(true);

		table.setWidth("99%");
		table.setHeight("200px");

		table.addContainerProperty("SL #", Label.class , new Label());
		table.setColumnWidth("SL #",20);

		table.addContainerProperty("PAYMENT TYPE", Label.class , new Label());
		table.setColumnWidth("PAYMENT TYPE",100);
		
		table.addContainerProperty("FISCAL YEAR", Label.class , new Label());
		table.setColumnWidth("FISCAL YEAR",70);

		table.addContainerProperty("TAX YEAR", Label.class, new Label());
		table.setColumnWidth("TAX YEAR",70);

		table.addContainerProperty("CHALLAN NO", Label.class , new Label());
		table.setColumnWidth("CHALLAN NO",120);

		table.addContainerProperty("CHEQUE NO", Label.class , new Label());
		table.setColumnWidth("CHEQUE NO",120);	

		table.addContainerProperty("Amount", Label.class , new Label());
		table.setColumnWidth("Amount",100);	

		table.addContainerProperty("Trans ID", Label.class , new Label());
		table.setColumnWidth("Trans ID",100);	

		table.setColumnCollapsed("Trans ID", true);

		rowAddinTable();
	}

	public void rowAddinTable()
	{
		for(int i=0;i<10;i++)
		{
			tableRowAdd(i);
		}
	}

	private void tableDataAdding() {
		String Payment = "";

		if (ChkType.booleanValue() == true) {
			Payment = "%";
		} else {
			Payment = cmbPaymentType.getValue().toString();
		}

		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try 
		{
			String query = "select mPaymentType,vFiscalYearId,vTaxYear,isnull(mChallanNo,'')mChallanNo,isnull(mCheckNo,'')mCheckNo,mAmount,vTransactionId " +
					"from tbCheckDetails where vFiscalYearId like '"+cmbFiscalYear.getValue().toString()+"' and mPaymentType like '"+Payment+"' ";

			System.out.println("table Value " + query);
			List<?> list = session.createSQLQuery(query).list();

			if (!list.isEmpty()) 
			{
				int i = 0;
				for (Iterator<?> iter = list.iterator(); iter.hasNext();) {
					Object[] element = (Object[]) iter.next();

					lblPaymentType.get(i).setValue(element[0]);
					lbFiscalYear.get(i).setValue(element[1]);
					lbTAXYear.get(i).setValue(element[2]);
					lbChallanNo.get(i).setValue(element[3]);
					lbCheckNO.get(i).setValue(element[4]);
					amtAmount.get(i).setValue(twoDigit.format(element[5]));
					lbTransactionId.get(i).setValue(element[6]);

					if ((i) == lbliAuto.size() - 1) {
						tableRowAdd(i + 1);
					}
					i++;
				}
			} 
			else {
				tableclear();
				showNotification("Data not Found !!",Notification.TYPE_WARNING_MESSAGE);
			}
		} 
		catch (Exception ex) {
			showNotification("tableDataAdding", ex.toString(),Notification.TYPE_ERROR_MESSAGE);
		} 
		finally {
			session.close();
		}
	}

	public void tableRowAdd(final int ar)
	{
		lbSl.add(ar, new Label(""));
		lbSl.get(ar).setWidth("100%");
		lbSl.get(ar).setHeight("20px");
		lbSl.get(ar).setValue(ar+1);

		lblPaymentType.add(ar, new Label(""));
		lblPaymentType.get(ar).setWidth("100%");
		lblPaymentType.get(ar).setHeight("20px");


		lbFiscalYear.add(ar, new Label(""));
		lbFiscalYear.get(ar).setWidth("100%");
		lbFiscalYear.get(ar).setImmediate(true);
		lbFiscalYear.get(ar).setHeight("23px");

		lbTAXYear.add(ar, new Label());
		lbTAXYear.get(ar).setWidth("100%");
		lbTAXYear.get(ar).setImmediate(true);
		lbTAXYear.get(ar).setHeight("20px");

		lbChallanNo.add(ar, new Label());
		lbChallanNo.get(ar).setWidth("100%");
		lbChallanNo.get(ar).setImmediate(true);
		lbChallanNo.get(ar).setHeight("20px");

		lbCheckNO.add(ar, new Label());
		lbCheckNO.get(ar).setWidth("100%");
		lbCheckNO.get(ar).setImmediate(true);
		lbCheckNO.get(ar).setHeight("20px");

		amtAmount.add(ar, new Label());
		amtAmount.get(ar).setWidth("100%");
		amtAmount.get(ar).setImmediate(true);
		amtAmount.get(ar).setHeight("20px");
		

		lbTransactionId.add(ar, new Label(""));
		lbTransactionId.get(ar).setWidth("100%");
		lbTransactionId.get(ar).setImmediate(true);
		lbTransactionId.get(ar).setHeight("23px");

		table.addItem(new Object[]{lbSl.get(ar),lblPaymentType.get(ar),lbFiscalYear.get(ar),lbTAXYear.get(ar),lbChallanNo.get(ar),lbCheckNO.get(ar),
				amtAmount.get(ar),lbTransactionId.get(ar)},ar);
	}

	private AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);

		setWidth("740px");
		setHeight("350px");

		// lblEmployeeName
		lblFiscalYear = new Label("Fiscal Year :");
		lblFiscalYear.setImmediate(false);
		lblFiscalYear.setWidth("-1px");
		lblFiscalYear.setHeight("-1px");
		mainLayout.addComponent(lblFiscalYear, "top:15.0px; left:10.0px;");

		// txtEmployeeName
		cmbFiscalYear =new ComboBox();
		cmbFiscalYear.setImmediate(true);
		cmbFiscalYear.setWidth("110px");
		cmbFiscalYear.setHeight("-1px");
		mainLayout.addComponent(cmbFiscalYear, "top:13.0px; left:150.0px;");	

		lbEmp = new Label("Payment Type :");
		lbEmp.setImmediate(true);
		lbEmp.setWidth("-1px");
		lbEmp.setHeight("-1px");
		mainLayout.addComponent(lbEmp, "top:45.0px;left:10.0px;");

		cmbPaymentType = new ComboBox();
		cmbPaymentType.setImmediate(true);
		cmbPaymentType.setWidth("170px");
		cmbPaymentType.setHeight("24px");
		cmbPaymentType.setImmediate(true);
		cmbPaymentType.setNullSelectionAllowed(false);
		mainLayout.addComponent(cmbPaymentType, "top:43.0px;left:150.0px;");
		for(int i=0;i<PaymentType.length; i++){
			cmbPaymentType.addItem(PaymentType[i]);
		}

		
		ChkType = new CheckBox("All");
		ChkType.setImmediate(true);
		ChkType.setWidth("-1px");
		ChkType.setHeight("-1px");
		mainLayout.addComponent(ChkType,"top:45px; left:360px;");
		
		mainLayout.addComponent(table, "top:80.0px;left:20.0px;");
		return mainLayout;
	}
}
