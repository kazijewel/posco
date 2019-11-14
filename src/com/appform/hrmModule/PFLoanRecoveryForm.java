package com.appform.hrmModule;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.AmountCommaSeperator;
import com.common.share.CommaSeparator;
import com.common.share.CommonButton;
import com.common.share.CommonMethod;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class PFLoanRecoveryForm extends Window
{
	private CommonButton cButton = new CommonButton( "New",  "Save",  "Edit",  "",  "Refresh",  "Find", "", "","","Exit");
	private SessionBean sessionBean;

	private AbsoluteLayout mainLayout;

	public Table table = new Table();
	public ArrayList<Label> tbllblSerial = new ArrayList<Label>();
	public ArrayList<Label> tbllblDate = new ArrayList<Label>();
	public ArrayList<Label> tbllblWeekDay = new ArrayList<Label>();
	public ArrayList<ComboBox> tblCmbNatureOfLeave = new ArrayList<ComboBox>();
	public ArrayList<CheckBox> tblChkEnjoyed = new ArrayList<CheckBox>();

	HashMap <String,Object> hDateDayList = new HashMap <String,Object> ();

	private static final String[] adjustType = new String[] {"Cash","Cheque","PF"};

	private Label lblRecoveryDate;
	private PopupDateField dRecoveryDate;

	private OptionGroup ogSelectEmployee;
	private List<String> lst = Arrays.asList(new String[]{"Employee ID","Finger ID","Employee Name"});

	private Label lblEmployeeId;
	private ComboBox cmbEmployeeId;

	private Label lblEmployeeName;
	private TextRead txtEmployeeName,txtDepartment;

	private Label lblFingerId;
	private TextRead txtFingerId;

	private Label lblSection;
	private TextRead txtSection;

	private Label lblDesignation;
	private TextRead txtDesignation;

	private Label lblUnit;
	private ComboBox cmbUnit;
	
	
	private Label lblLoanInfo;
	private Label lblLoanNo;
	private ComboBox cmbLoanNo;

	private Label lblLoanBalance;
	private TextRead txtLoanBalance;

	private Label lblRecoveryAmount;
	private AmountCommaSeperator txtRecoveryAmount;

	private Label lblAdjustType;
	private ComboBox cmbAdjustType;

	private TextRead txtFindTranId = new TextRead();
	private TextRead txtFindDate = new TextRead();

	private String updateID = "";

	private ArrayList<Component> allComp = new ArrayList<Component>(); 

	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");

	private DecimalFormat df = new DecimalFormat("#0.00");

	private boolean isUpdate=false;

	String empId = "";
	String Unitid = "";
	String departmentID = "";
	String sectionId = "";
	String designationId = "";
	String loanType = "";

	String findApplicationDate = "";	

	double previousRecovery;
	
	TextArea txtRemarks;
	
	private CommonMethod cm;
	private String menuId = "";
	public PFLoanRecoveryForm(SessionBean sessionBean,String menuId)
	{
		this.sessionBean=sessionBean;
		this.setCaption("PF LOAN RECOVERY :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);	
		btnIni(true);
		componentIni(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		setBtnAction();
		cButton.btnNew.focus();
		focusEnter();
		cmbUnitDataAdd();
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
	private void setBtnAction()
	{
		ogSelectEmployee.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(ogSelectEmployee.getValue().equals("Employee ID"))
				{
					txtFingerId.setValue("");
					txtEmployeeName.setValue("");
					cmbAddEmployeeData();
				}
				if(ogSelectEmployee.getValue().equals("Finger ID"))
				{
					txtFingerId.setValue("");
					txtEmployeeName.setValue("");
					cmbAddEmployeeData();
				}
				if(ogSelectEmployee.getValue().equals("Employee Name"))
				{
					txtFingerId.setValue("");
					txtEmployeeName.setValue("");
					cmbAddEmployeeData();
				}
			}
		});

		cButton.btnNew.addListener(new ClickListener()
		{	
			public void buttonClick(ClickEvent event)
			{
				txtClear();
				componentIni(true);
				btnIni(false);
				cmbUnit.focus();
				cmbEmployeeId.focus();
			}
		});

		cButton.btnSave.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				formValidation();
			}
		});

		cmbUnit.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbUnit.getValue()!=null)
				{
				cmbAddEmployeeData();
				}
				
			}
		});

		
		
		cmbEmployeeId.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbEmployeeId.getValue()!=null)
				{
					employeeSetData(cmbEmployeeId.getValue().toString());
					employeeLoanData(cmbEmployeeId.getValue().toString());
				}
			}
		});

		cButton.btnFind.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				findButtonEvent();
			}
		});

		cButton.btnRefresh.addListener(new ClickListener()
		{	
			public void buttonClick(ClickEvent event)
			{
				isUpdate=false;
				txtClear();
				componentIni(false);
				btnIni(true);
			}
		});

		cButton.btnEdit.addListener(new ClickListener()
		{	
			public void buttonClick(ClickEvent event)
			{
				if(cmbLoanNo.getValue()!=null && cmbEmployeeId.getValue()!=null)
				{
					isUpdate=true;
					componentIni(true);
					btnIni(false);
				}
				else
				{
					getParent().showNotification("There are nothing to edit", Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		cmbLoanNo.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbLoanNo.getValue()!=null)
				{
					setLoanData();
				}
			}
		});

		cButton.btnExit.addListener(new ClickListener()
		{	
			public void buttonClick(ClickEvent event)
			{
				close();
			}
		});
	}
	private void cmbUnitDataAdd() 
	{
		cmbUnit.removeAllItems();

		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{						
			String query="select distinct epo.vUnitId,epo.vUnitName from tbPFLoanApplication la inner join tbEmpOfficialPersonalInfo epo on epo.vEmployeeId=la.vAutoEmployeeId order by epo.vUnitName";
			
			System.out.println("Unit"+query);
			
			List <?> list=session.createSQLQuery(query).list();	

			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbUnit.addItem(element[0].toString());
				cmbUnit.setItemCaption(element[0].toString(),element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbUnitData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void cmbAddEmployeeData()
	{
		cmbEmployeeId.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select vAutoEmployeeId,epo.vFingerId,epo.vEmployeeCode,epo.vEmployeeName from tbPFLoanApplication la inner join tbEmpOfficialPersonalInfo epo on epo.vEmployeeId=la.vAutoEmployeeId where epo.vUnitId='"+cmbUnit.getValue()+"' and iLoanStatus = '1'";

			
			System.out.println("Employee"+query);
			

			List <?> list = session.createSQLQuery(query).list();

			if(!list.isEmpty())
			{
				if(ogSelectEmployee.getValue().toString().equals("Finger ID"))
				{
					for(Iterator <?> itr=list.iterator();itr.hasNext();)
					{
						Object [] element=(Object[])itr.next();
						lblFingerId.setValue("Employee Name : ");
						txtFingerId.setWidth("250px");
						lblEmployeeId.setValue("Finger ID : ");
						cmbEmployeeId.setWidth("140px");
						lblEmployeeName.setValue("Employee ID : ");
						txtEmployeeName.setWidth("140px");
						cmbEmployeeId.addItem(element[0]);
						cmbEmployeeId.setItemCaption(element[0], element[1].toString());
					}
				}
				if(ogSelectEmployee.getValue().toString().equals("Employee ID"))
				{
					for(Iterator <?> itr=list.iterator();itr.hasNext();)
					{
						Object [] element=(Object[])itr.next();
						lblFingerId.setValue("Employee Name : ");
						txtFingerId.setWidth("250px");
						lblEmployeeId.setValue("Employee ID : ");
						cmbEmployeeId.setWidth("140px");
						lblEmployeeName.setValue("Finger ID : ");
						txtEmployeeName.setWidth("140px");
						cmbEmployeeId.addItem(element[0]);
						cmbEmployeeId.setItemCaption(element[0], element[2].toString());
					}
				}
				if(ogSelectEmployee.getValue().toString().equals("Employee Name"))
				{
					for(Iterator <?> itr=list.iterator();itr.hasNext();)
					{
						Object [] element=(Object[])itr.next();
						lblEmployeeName.setValue("Employee ID : ");
						txtEmployeeName.setWidth("140px");
						lblEmployeeId.setValue("Employee Name : ");
						cmbEmployeeId.setWidth("250px");
						lblFingerId.setValue("Finger ID : ");
						txtFingerId.setWidth("140px");
						cmbEmployeeId.addItem(element[0]); 
						cmbEmployeeId.setItemCaption(element[0], element[3].toString());
					}
				}
			}
		}
		catch(Exception ex)
		{
			showNotification("cmbAddEmployeeData", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void findButtonEvent() 
	{
		Window win = new LoanRecoveryFind(sessionBean, txtFindTranId, txtFindDate);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if (txtFindTranId.getValue().toString().length() > 0)
				{
					txtClear();
					findInitialise(txtFindTranId.getValue().toString() , txtFindDate.getValue());
				}
			}
		});

		this.getParent().addWindow(win);
	}

	private Object autoId()
	{
		Object autoDate = null;
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try 
		{
			String query = " Select cast(isnull(max(cast(replace(vTransactionId, '', '')as int))+1, 1)as varchar) as TransactionId from tbPFLoanRecoveryInfo ";
			Iterator <?> iter = session.createSQLQuery(query).list().iterator();

			if (iter.hasNext()) 
			{
				autoDate = iter.next();
			}
		} 
		catch (Exception ex) 
		{
			showNotification("Warning", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
		return autoDate;
	}

	private void findInitialise(String TranId, Object findDate) 
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();

		try
		{
			String sql = "SELECT vAutoEmployeeId,vLoanNo," +
					"(select mLoanBalance from tbPFLoanApplication where vLoanNo=LRin.vLoanNo and vAutoEmployeeId=LRin.vAutoEmployeeId)+mRecoveryAmount," +
					"mRecoveryAmount,vAdjustType,vRemarks," +
					"(select vUnitId from tbEmpOfficialPersonalInfo where vEmployeeId=LRin.vAutoEmployeeId)vUnitId, "+
					"(select vUnitName from tbEmpOfficialPersonalInfo where vEmployeeId=LRin.vAutoEmployeeId)vUnitName "+
					" from tbPFLoanRecoveryInfo LRin where vTransactionId = '"+TranId+"' and dRecoveryDate = '"+(findDate)+"' ";
			
			System.out.println("Find Initialize :" + sql);
			
			List <?> list = session.createSQLQuery(sql).list();

			if(list.iterator().hasNext())
			{
				Object[] element = (Object[]) list.iterator().next();
				txtRemarks.setValue(element[5]);
				cmbEmployeeId.setValue(element[0]);
				cmbLoanNo.setValue(element[1].toString());

				txtLoanBalance.setValue(new CommaSeparator().setComma(Double.parseDouble(element[2].toString())));
				txtRecoveryAmount.setValue(df.format(Double.parseDouble(element[3].toString())));

				cmbAdjustType.setValue(element[4].toString());

				previousRecovery = Double.parseDouble(element[3].toString());
				updateID = TranId;
				findApplicationDate = findDate.toString();
				cmbUnit.setValue(element[6].toString());
				cmbUnit.setItemCaption(element[6].toString(), element[7].toString());
			}
		}
		catch (Exception exp)
		{
			showNotification("findInitialise", exp + "",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void employeeSetData(String employeeId)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select vAutoEmployeeId,epo.vFingerId,epo.vEmployeeCode,epo.vEmployeeName,epo.vSectionName,"
					+ " epo.vDesignationName,epo.vDepartmentName from tbPFLoanApplication la "
					+ " inner join tbEmpOfficialPersonalInfo epo on epo.vEmployeeId=la.vAutoEmployeeId "
					+ " WHERE epo.vUnitid='"+cmbUnit.getValue().toString()+"' and vAutoEmployeeId = '"+employeeId+"' ";
			
			System.out.println("Set Query :" + query);

			List <?> list = session.createSQLQuery(query).list();

			if(list.iterator().hasNext())
			{
				Object[] element = (Object[]) list.iterator().next();

				if(ogSelectEmployee.getValue()=="Employee ID")
				{
					txtFingerId.setValue(element[3]);
					txtEmployeeName.setValue(element[1]);
				}
				if(ogSelectEmployee.getValue()=="Finger ID")
				{
					txtFingerId.setValue(element[3]);
					txtEmployeeName.setValue(element[2]);
				}
				if(ogSelectEmployee.getValue()=="Employee Name")
				{
					txtFingerId.setValue(element[1]);
					txtEmployeeName.setValue(element[2]);		
				}

				txtSection.setValue(element[4]);
				txtDesignation.setValue(element[5]);
				txtDepartment.setValue(element[6]);
			}
		}
		catch(Exception ex)
		{
			showNotification("employeeSetData", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void employeeLoanData(String empId)
	{
		cmbLoanNo.removeAllItems();
		txtLoanBalance.setValue("");
		txtRecoveryAmount.setValue("");
		cmbAdjustType.setValue(null);

		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = " select la.dApplicationDate,vLoanNo from tbPFLoanApplication la inner join tbEmpOfficialPersonalInfo epo on epo.vEmployeeId=la.vAutoEmployeeId where epo.vUnitId='"+cmbUnit.getValue().toString()+"' and vAutoEmployeeId='"+empId+"' and iLoanStatus='1' ";
			
			System.out.println("Employee :" + query);
			
			List <?> list = session.createSQLQuery(query).list();

			for (Iterator <?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element =  (Object[]) iter.next();	
				cmbLoanNo.addItem(element[1]);
				cmbLoanNo.setItemCaption(element[1], element[1].toString());	
			}
		}
		catch(Exception ex)
		{
			showNotification("employeeLoanData", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void setLoanData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "SELECT mLoanBalance,mAmountPerInstall,iSanctionStatus,vLoanType from "
					+ " tbPFLoanApplication la inner join tbEmpOfficialPersonalInfo epo on epo.vEmployeeId=la.vEmployeeId where epo.vUnitId='"+cmbUnit.getValue().toString()+"' and "
					+ " vLoanNo = '"+cmbLoanNo.getValue().toString()+"' ";
		
			
			
			System.out.println("Set loan :" + query);
			
			
			List <?> list = session.createSQLQuery(query).list();

			
			
			if(list.iterator().hasNext())
			{
				Object[] element = (Object[]) list.iterator().next();
				if(element[2].toString().equals("1"))
				{
					txtLoanBalance.setValue(new CommaSeparator().setComma(Double.parseDouble(element[0].toString())));
					txtRecoveryAmount.setValue(df.format(Double.parseDouble(element[1].toString())));
					loanType = element[3].toString();
				}
				else
				{
					cmbLoanNo.setValue(null);
					txtLoanBalance.setValue("");
					txtRecoveryAmount.setValue("");
					showNotification("Warning!", "Loan is not sanctioned yet", Notification.TYPE_WARNING_MESSAGE);
				}
			}
		}
		catch(Exception ex)
		{
			showNotification("setLoanData", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void formValidation()
	{
		if(cmbUnit.getValue()!=null)
		{
		if(cmbEmployeeId.getValue()!=null)
		{
			if(cmbLoanNo.getValue()!=null)
			{
				if(!txtRecoveryAmount.getValue().toString().isEmpty())
				{
					if(cmbAdjustType.getValue()!=null)
					{
						saveBtnAction();
					}
					else
					{
						showNotification("Warning!","Select Adjust Type", Notification.TYPE_WARNING_MESSAGE);
						cmbAdjustType.focus();
					}
				}
				else
				{
					showNotification("Warning!","Provide Recovery Amount", Notification.TYPE_WARNING_MESSAGE);
					txtRecoveryAmount.focus();
				}
			}
			else
			{
				showNotification("Warning!","Select Loan No", Notification.TYPE_WARNING_MESSAGE);
				cmbLoanNo.focus();
			}
		}
		else
		{
			showNotification("Warning!","Select Employee Name", Notification.TYPE_WARNING_MESSAGE);
			cmbEmployeeId.focus();
		}
		}
		else
		{
			showNotification("Warning!","Select Unit Name", Notification.TYPE_WARNING_MESSAGE);
			cmbUnit.focus();
		}
	}

	private void saveBtnAction()
	{
		double balance = Double.parseDouble(txtLoanBalance.getValue().toString().replace(",", ""))-Double.parseDouble(txtRecoveryAmount.getValue().toString().replace(",", ""));

		if(isUpdate)
		{
			MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to update information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						updateData();
						isUpdate = false;
						componentIni(false);
						btnIni(true);
						txtClear();
						cButton.btnNew.focus();
					}
				}
			});
		}
		else
		{
			if(balance>=0)
			{
				MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to save all information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
				mb.show(new EventListener()
				{
					public void buttonClicked(ButtonType buttonType)
					{
						if(buttonType == ButtonType.YES)
						{
							insertData();
							isUpdate = false;				
							componentIni(false);
							btnIni(true);
							txtClear();
							cButton.btnNew.focus();
						}
					}
				});
			}
			else
			{
				showNotification("Warning!","Recovery amount is greater than balance amount",Notification.TYPE_WARNING_MESSAGE);
			}
		}
	}

	private void insertData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();

		String insertQuery = ""; 
		String updateQuery = "";
		String loanStatus = "1";
		
		String UnitName = cmbUnit.getItemCaption(cmbUnit.getValue());
		String EmployeeName = cmbEmployeeId.getItemCaption(cmbEmployeeId.getValue());
		String EmployeeId = txtEmployeeName.getValue().toString().trim();
		String FingerId = txtFingerId.getValue().toString().trim();

		double balance = Double.parseDouble(txtLoanBalance.getValue().toString().replaceAll(",", ""))-Double.parseDouble(txtRecoveryAmount.getValue().toString().replaceAll(",", ""));
		System.out.println("Balance: "+balance);

		if(balance==0)
		{loanStatus= "0";}
		
		if(ogSelectEmployee.getValue()=="Employee ID")
		{
			EmployeeId = cmbEmployeeId.getItemCaption(cmbEmployeeId.getValue());
			FingerId = txtEmployeeName.getValue().toString().trim();
			EmployeeName = txtFingerId.getValue().toString().trim();
		}
		else if(ogSelectEmployee.getValue()=="Finger ID")
		{
			FingerId = cmbEmployeeId.getItemCaption(cmbEmployeeId.getValue());
			EmployeeId = txtEmployeeName.getValue().toString().trim();
			EmployeeName = txtFingerId.getValue().toString().trim();
		}

		try
		{
			insertQuery = " Insert into tbPFLoanRecoveryInfo (vTransactionId,dRecoveryDate,vAutoEmployeeId,vEmployeeId,vEmployeeName,vFingerId,vProximityId," +
					"vUnitId,vUnitName,vSectionId,vSectionName,vDesignationId,vDesignationName,vLoanNo,mLoanBalance,mRecoveryAmount,vLoanType,vAdjustType,vRecoverFrom,vUserId," +
					"vUserIp,dEntryTime,vRemarks) values (" +
					" '"+autoId()+"', "
					+ "'"+dateFormat.format(dRecoveryDate.getValue())+"',"+
					" '"+cmbEmployeeId.getValue()+"',"+
					" '"+EmployeeId+"'," +
					" '"+EmployeeName+"'," +
					" '"+FingerId+"'," +
					"''," +
					"''," +
					"'"+UnitName+"',"+
					"''," +
					"'"+txtSection.getValue().toString()+"'," +
					"''," +
					"'"+txtDesignation.getValue().toString()+"'," +
					" '"+cmbLoanNo.getValue().toString()+"',"
					+ "'0.0', " +
					" '"+txtRecoveryAmount.getValue().toString().replace(",", "")+"', " +
					" '"+loanType+"', '"+cmbAdjustType.getValue().toString()+"', 'From Loan Recovery', " +
					" '"+sessionBean.getUserName()+"', '"+sessionBean.getUserIp()+"', CURRENT_TIMESTAMP,'"+txtRemarks.getValue()+"') ";
			
			System.out.println("Insert Query :" + insertQuery);

			session.createSQLQuery(insertQuery).executeUpdate();

			updateQuery = "update tbPFLoanApplication set mLoanBalance='"+balance+"',iLoanStatus='"+loanStatus+"' where" +
					" vLoanNo='"+cmbLoanNo.getValue().toString()+"' and" +
					" vEmployeeId='"+txtEmployeeName.getValue()+"' ";

			session.createSQLQuery(updateQuery).executeUpdate();

			tx.commit();
			showNotification("All Information Save Successfully");
		}
		catch(Exception ex)
		{
			tx.rollback();
			showNotification("insertData", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void updateData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		String updateQuery = "";
		String updateQueryBalance = "";

		double updateBalance = previousRecovery-Double.parseDouble(txtRecoveryAmount.getValue().toString().replace(",", ""));

		try
		{
			updateQuery = " UPDATE tbPFLoanRecoveryInfo set " +
					" mRecoveryAmount = '"+txtRecoveryAmount.getValue().toString()+"' ," +
					" vAdjustType = '"+cmbAdjustType.getValue().toString()+"' ," +
					" dEntryTime=CURRENT_TIMESTAMP, vUserIp='"+sessionBean.getUserIp()+"'," +
					" vUserId='"+sessionBean.getUserName()+"'," +
					" vRemarks='"+txtRemarks.getValue()+"'" +
					" where vTransactionId='"+updateID+"' and dRecoveryDate = '"+findApplicationDate+"' ";

			session.createSQLQuery(updateQuery).executeUpdate();

			updateQueryBalance = " UPDATE tbPFLoanApplication set mLoanBalance=mLoanBalance+'"+updateBalance+"' where" +
					" vLoanNo='"+cmbLoanNo.getValue().toString()+"' and vAutoEmployeeId='"+empId+"' ";

			session.createSQLQuery(updateQueryBalance).executeUpdate();

			tx.commit();
			showNotification("All Information Update Successfully");
		}
		catch(Exception ex)
		{
			tx.rollback();
			showNotification("updateData", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
		showNotification("All Information Update Successfully");
	}

	private void btnIni(boolean t) 
	{
		cButton.btnNew.setEnabled(t);
		cButton.btnEdit.setEnabled(t);
		cButton.btnSave.setEnabled(!t);
		cButton.btnRefresh.setEnabled(!t);
		cButton.btnDelete.setEnabled(t);
		cButton.btnFind.setEnabled(t);
	}

	private void componentIni(boolean t)
	{
		
		
		dRecoveryDate.setEnabled(t);
		cmbUnit.setEnabled(t);
		if(isUpdate==true)
		{
			cmbEmployeeId.setEnabled(!t);
			cmbLoanNo.setEnabled(!t);
		}
		else
		{
			cmbEmployeeId.setEnabled(t);
			cmbLoanNo.setEnabled(t);
		}

		txtEmployeeName.setEnabled(t);
		txtFingerId.setEnabled(t);
		txtSection.setEnabled(t);
		txtDesignation.setEnabled(t);
		txtDepartment.setEnabled(t);

		txtLoanBalance.setEnabled(t);
		txtRecoveryAmount.setEnabled(t);
		cmbAdjustType.setEnabled(t);
		txtRemarks.setEnabled(t);
	}

	private void txtClear()
	{	
		
		dRecoveryDate.setValue(new java.util.Date());
		cmbUnit.setValue(null);
		cmbEmployeeId.setValue(null);
		txtEmployeeName.setValue("");
		txtFingerId.setValue("");
		txtSection.setValue("");
		txtDesignation.setValue("");
		txtDepartment.setValue("");

		cmbLoanNo.setValue(null);
		txtLoanBalance.setValue("");
		txtRecoveryAmount.setValue("");
		cmbAdjustType.setValue(null);
		txtRemarks.setValue("");
	}

	private void focusEnter()
	{
	
		allComp.add(dRecoveryDate);
		allComp.add(cmbUnit);
		allComp.add(cmbEmployeeId);
		allComp.add(cmbLoanNo);
		allComp.add(txtRecoveryAmount);
		allComp.add(cmbAdjustType);
		allComp.add(txtRemarks);
		allComp.add(cButton.btnSave);
		new FocusMoveByEnter(this,allComp);
	}

	public AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("630px");
		mainLayout.setHeight("500px");

		// lblRecoveryDate
		lblRecoveryDate = new Label("Recovery Date : ");
		lblRecoveryDate.setImmediate(false);
		lblRecoveryDate.setWidth("-1px");
		lblRecoveryDate.setHeight("-1px");
		mainLayout.addComponent(lblRecoveryDate, "top:20.0px; left:70.0px;");

		// dRecoveryDate
		dRecoveryDate = new PopupDateField();
		dRecoveryDate.setImmediate(false);
		dRecoveryDate.setWidth("110px");
		dRecoveryDate.setHeight("-1px");
		dRecoveryDate.setValue(new java.util.Date());
		dRecoveryDate.setDateFormat("dd-MM-yyyy");
		dRecoveryDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dRecoveryDate, "top:19.0px; left:200.0px;");

		// lblEmployeeID
		lblUnit = new Label("Project Name :");
		lblUnit.setImmediate(false);
		lblUnit.setWidth("-1px");
		lblUnit.setHeight("-1px");
		mainLayout.addComponent(lblUnit, "top:50.0px; left:70.0px;");

		// cmbEmployeeId
		cmbUnit = new ComboBox();
		cmbUnit.setImmediate(true);
		cmbUnit.setWidth("230px");
		cmbUnit.setHeight("-1px");
		cmbUnit.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbUnit, "top:48.0px; left:200.0px;");


		//RadioButtonType	
		ogSelectEmployee = new OptionGroup("",lst);
		ogSelectEmployee.setImmediate(true);
		ogSelectEmployee.setStyleName("horizontal");
		ogSelectEmployee.select("Employee Name");
		mainLayout.addComponent(ogSelectEmployee, "top:80.0px;left:100.0px;");

		
		// lblEmployeeID
		lblEmployeeId = new Label("Employee ID :");
		lblEmployeeId.setImmediate(false);
		lblEmployeeId.setWidth("-1px");
		lblEmployeeId.setHeight("-1px");
		mainLayout.addComponent(lblEmployeeId, "top:110.0px; left:70.0px;");

		// cmbEmployeeId
		cmbEmployeeId = new ComboBox();
		cmbEmployeeId.setImmediate(true);
		cmbEmployeeId.setWidth("230px");
		cmbEmployeeId.setHeight("-1px");
		cmbEmployeeId.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbEmployeeId, "top:108.0px; left:200.0px;");

		// lblEmployeeName
		lblEmployeeName = new Label("Employee Name :");
		lblEmployeeName.setImmediate(false);
		lblEmployeeName.setWidth("-1px");
		lblEmployeeName.setHeight("-1px");
		mainLayout.addComponent(lblEmployeeName, "top:135px; left:70.0px;");

		// txtEmployeeName
		txtEmployeeName = new TextRead();
		txtEmployeeName.setImmediate(true);
		txtEmployeeName.setWidth("110px");
		txtEmployeeName.setHeight("22px");
		mainLayout.addComponent(txtEmployeeName, "top:133px; left:201.0px;");

		// lblFingerId
		lblFingerId = new Label("Finger ID :");
		lblFingerId.setImmediate(false);
		lblFingerId.setWidth("-1px");
		lblFingerId.setHeight("-1px");
		mainLayout.addComponent(lblFingerId, "top:160px; left:70.0px;");

		// txtFingerId
		txtFingerId = new TextRead();
		txtFingerId.setImmediate(true);
		txtFingerId.setWidth("110px");
		txtFingerId.setHeight("22px");
		mainLayout.addComponent(txtFingerId, "top:158px; left:201.0px;");

	
		mainLayout.addComponent(new Label("Department :"), "top:185px; left:70.0px;");

		// txtDepartment
		txtDepartment = new TextRead();
		txtDepartment.setImmediate(true);
		txtDepartment.setWidth("220px");
		txtDepartment.setHeight("22px");
		mainLayout.addComponent(txtDepartment, "top:183px; left:201.0px;");
		
		// lblSection
		lblSection = new Label("Section :");
		lblSection.setImmediate(false);
		lblSection.setWidth("-1px");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection, "top:210px; left:70.0px;");

		// txtSection
		txtSection = new TextRead();
		txtSection.setImmediate(true);
		txtSection.setWidth("220px");
		txtSection.setHeight("22px");
		mainLayout.addComponent(txtSection, "top:208px; left:201.0px;");

		// lblDesignation
		lblDesignation = new Label("Designation :");
		lblDesignation.setImmediate(false);
		lblDesignation.setWidth("-1px");
		lblDesignation.setHeight("-1px");
		mainLayout.addComponent(lblDesignation, "top:235px; left:70.0px;");

		// txtDesignation
		txtDesignation = new TextRead();
		txtDesignation.setImmediate(true);
		txtDesignation.setWidth("220px");
		txtDesignation.setHeight("22px");
		mainLayout.addComponent(txtDesignation, "top:233px; left:201.0px;");

		// lblLoanInfo
		lblLoanInfo = new Label("<html><font color='#74078B'><b><u>Loan Details</u></b></font></html>",Label.CONTENT_XHTML);
		lblLoanInfo.setImmediate(false);
		lblLoanInfo.setWidth("-1px");
		lblLoanInfo.setHeight("-1px");
		mainLayout.addComponent(lblLoanInfo, "top:260.0px; left:150.0px;");

		// lblLoanNo
		lblLoanNo = new Label("Loan No :");
		lblLoanNo.setImmediate(false);
		lblLoanNo.setWidth("-1px");
		lblLoanNo.setHeight("-1px");
		mainLayout.addComponent(lblLoanNo, "top:285px; left:70.0px;");

		// cmbLoanNo
		cmbLoanNo = new ComboBox();
		cmbLoanNo.setImmediate(true);
		cmbLoanNo.setWidth("80px");
		cmbLoanNo.setHeight("-1px");
		mainLayout.addComponent(cmbLoanNo, "top:283px; left:200.0px;");

		// lblLoanBalance
		lblLoanBalance = new Label("Loan Balance :");
		lblLoanBalance.setImmediate(false);
		lblLoanBalance.setWidth("-1px");
		lblLoanBalance.setHeight("-1px");
		mainLayout.addComponent(lblLoanBalance, "top:310px; left:70.0px;");

		// txtLoanBalance
		txtLoanBalance = new TextRead(1);
		txtLoanBalance.setImmediate(true);
		txtLoanBalance.setWidth("100px");
		txtLoanBalance.setHeight("-1px");
		mainLayout.addComponent(txtLoanBalance, "top:308px; left:200.0px;");

		// lblRecoveryAmount
		lblRecoveryAmount = new Label("Recovery Amount :");
		lblRecoveryAmount.setImmediate(false);
		lblRecoveryAmount.setWidth("-1px");
		lblRecoveryAmount.setHeight("-1px");
		mainLayout.addComponent(lblRecoveryAmount, "top:335px; left:70.0px;");

		// txtRecoveryAmount
		txtRecoveryAmount = new AmountCommaSeperator();
		txtRecoveryAmount.setImmediate(true);
		txtRecoveryAmount.setWidth("100px");
		txtRecoveryAmount.setHeight("-1px");
		mainLayout.addComponent(txtRecoveryAmount, "top:333px; left:200.0px;");

		// lblAdjustType
		lblAdjustType = new Label("Adjust Type :");
		lblAdjustType.setImmediate(false);
		lblAdjustType.setWidth("-1px");
		lblAdjustType.setHeight("-1px");
		mainLayout.addComponent(lblAdjustType, "top:360px; left:70.0px;");

		// cmbAdjustType
		cmbAdjustType = new ComboBox();
		cmbAdjustType.setImmediate(true);
		cmbAdjustType.setWidth("130px");
		cmbAdjustType.setHeight("-1px");
		cmbAdjustType.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbAdjustType, "top:358px; left:200.0px;");
		for(int i=0;i<adjustType.length;i++)
		{cmbAdjustType.addItem(adjustType[i]);}
		
		txtRemarks=new TextArea();
		txtRemarks.setWidth("160");
		txtRemarks.setHeight("40");
		mainLayout.addComponent(new Label("Remarks"), "top:385px; left:70.0px;");
		mainLayout.addComponent(txtRemarks, "top:383px; left:200.0px;");
		
		

		mainLayout.addComponent(cButton, "bottom:15px; left:50px;");
		return mainLayout;
	}
}
