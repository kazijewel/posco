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
import com.common.share.AmountField;
import com.common.share.CommaSeparator;
import com.common.share.CommonButton;
import com.common.share.CommonMethod;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.ReportDate;
import com.common.share.ReportOption;
import com.common.share.ReportViewer;
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
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class LoanApplicationForm extends Window
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;
	public Table table = new Table();
	public ArrayList<Label> tbllblSerial = new ArrayList<Label>();
	public ArrayList<Label> tbllblDate = new ArrayList<Label>();
	public ArrayList<Label> tbllblWeekDay = new ArrayList<Label>();
	public ArrayList<ComboBox> tblCmbNatureOfLeave = new ArrayList<ComboBox>();
	public ArrayList<CheckBox> tblChkEnjoyed = new ArrayList<CheckBox>();

	HashMap <String,Object> hDateDayList = new HashMap <String,Object> ();
	private static final String[] adjustType = new String[] {"Monthly Salary"};

	private OptionGroup ogSelectEmployee;
	private List<String> lst = Arrays.asList(new String[]{"Employee ID","Finger ID","Employee Name"});

	private Label lblApplicationDate;
	private PopupDateField dApplicationDate;

	private Label lblLoanNo;
	private TextRead txtLoanNo;

	private Label lblEmployeeId;
	private ComboBox cmbEmployeeId;

	private Label lblEmployeeName;
	private TextRead txtEmployeeName;

	private Label lblFingerId;
	private TextRead txtFingerId;

	private Label lblUnit;
	private ComboBox cmbUnit;
	
	private Label lblSection;
	private TextRead txtSection;

	private Label lblDesignation;
	private TextRead txtDesignation;
	private TextRead txtDepartment;

	private Label lblJoiningDate;
	private PopupDateField dJoiningDate;

	private Label lblGrossSalary;
	private TextRead txtGrossSalary;

	private Label lblLoanType;
	private ComboBox cmbLoanType;
	private static final String[] loanType = new String[] {"Salary Loan"};

	private Label lblLoanAmount;
	private AmountCommaSeperator txtLoanAmount;

	private Label lblRateOfInterest;
	private AmountField txtRateOfInterest;
	private Label lblRateOfInterestP;

	private Label lblInterestAmount;
	private TextRead txtInterestAmount;

	private Label lblGrossLoanAmount;
	private TextRead txtGrossLoanAmount;

	private Label lblLoanPurpose;
	private TextField txtLoanPurpose;

	private Label lblNoOfInstallment;
	private AmountField txtNoOfInstallment;

	private Label lblAmountOfInstallment;
	private TextRead txtAmountOfInstallment;

	private Label lblPaymentStart;
	private PopupDateField dPaymentStart;

	private Label lblAdjustType;
	private ComboBox cmbAdjustType;

	private Label lblLoanSanction;
	private Label lblSanctionDate;
	private PopupDateField dSanctionDate;

	private Label lblSanctionAmount;
	private AmountCommaSeperator txtSanctionAmount;

	private TextRead txtFindLoanNo = new TextRead();
	private TextRead txtFindDate = new TextRead();

	private String updateID="";

	private ArrayList<Component> allComp = new ArrayList<Component>(); 

	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");

	private DecimalFormat df = new DecimalFormat("#0.00");

	private boolean isUpdate=false;
	private boolean pendingLoan = false;
	private boolean activeLoan = false;

	private String isSanction = "";

	String empId = "";
	String departmentId = "";
	String sectionId = "";
	String designationId = "";

	String findApplicationDate = "";	
	private ReportDate reportTime = new ReportDate();
	boolean isPreview;
	private CommonButton cButton = new CommonButton( "New",  "Save",  "Edit",  "",  "Refresh",  "Find", "", "Preview","","Exit");

	TextField txtPath=new TextField();
	private CommonMethod cm;
	private String menuId = "";
	public LoanApplicationForm(SessionBean sessionBean,boolean isPreview,String menuId)
	{
		this.sessionBean=sessionBean;
		this.isPreview=isPreview;
		this.setCaption("LOAN APPLICATION :: "+sessionBean.getCompany());
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		buildMainLayout();
		setContent(mainLayout);
		btnIni(true);
		componentIni(false);
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
				cmbAddEmployeeData();
			}
		});

		cButton.btnNew.addListener(new ClickListener()
		{	
			public void buttonClick(ClickEvent event)
			{
				isSanction = "";
				pendingLoan = false;
				activeLoan = false;
				txtClear();
				componentIni(true);
				btnIni(false);
				cmbLoanType.setValue("Salary Loan");
				cmbUnit.focus();	
				txtLoanNo.setValue(autoId());
				
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
				isUpdate = false;
				isSanction = "";
				pendingLoan = false;
				activeLoan = false;
				txtClear();
				componentIni(false);
				btnIni(true);
			}
		});

		cButton.btnEdit.addListener(new ClickListener()
		{	
			public void buttonClick(ClickEvent event)
			{
				if(!txtLoanNo.getValue().toString().equals("") && cmbEmployeeId.getValue()!=null)
				{
					isUpdate=true;
					componentIni(true);
					btnIni(false);
					if(isSanction.equals("1"))
					{
						txtSanctionAmount.setEnabled(false);
						dSanctionDate.setEnabled(false);
					}
					else
					{
						txtSanctionAmount.setEnabled(true);
						dSanctionDate.setEnabled(true);
					}
				}
				else
				{
					showNotification("There are nothing to edit", Notification.TYPE_WARNING_MESSAGE);
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
		txtLoanAmount.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtRateOfInterest.getValue().toString().isEmpty() && !txtLoanAmount.getValue().toString().isEmpty())
				{
					double Loan = Double.parseDouble(txtLoanAmount.getValue().toString().replace(",", ""));
					double interest = Double.parseDouble(txtRateOfInterest.getValue().toString().replace(",", ""));
					double totalInterest = (Loan*interest)/100;

					txtInterestAmount.setValue(new CommaSeparator().setComma(totalInterest));
					txtGrossLoanAmount.setValue(new CommaSeparator().setComma(totalInterest+Loan));
				}
			}
		});

		txtRateOfInterest.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtLoanAmount.getValue().toString().isEmpty())
				{
					double Loan = Double.parseDouble(txtLoanAmount.getValue().toString().replace(",", ""));
					double interest = Double.parseDouble(txtRateOfInterest.getValue().toString().replace(",", ""));
					double totalInterest = (Loan*interest)/100;

					txtInterestAmount.setValue(new CommaSeparator().setComma(totalInterest));
					txtGrossLoanAmount.setValue(new CommaSeparator().setComma(totalInterest+Loan));
				}
			}
		});

		txtNoOfInstallment.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtNoOfInstallment.getValue().toString().trim().isEmpty())
				{
					if(Double.parseDouble(txtNoOfInstallment.getValue().toString().trim())<=0)
					{
						txtNoOfInstallment.setValue("");
						txtNoOfInstallment.focus();
					}
					else if(!txtGrossLoanAmount.getValue().toString().isEmpty() && Double.parseDouble(txtNoOfInstallment.getValue().toString().trim())>0)
					{
						double grossAmount = Double.parseDouble(txtGrossLoanAmount.getValue().toString().replace(",", ""));
						double noOfInstall = Double.parseDouble(txtNoOfInstallment.getValue().toString().replace(",", ""));

						txtAmountOfInstallment.setValue(new CommaSeparator().setComma(grossAmount/noOfInstall));
					}
				}
			}
		});

		txtSanctionAmount.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtSanctionAmount.getValue().toString().isEmpty())
				{
					if(!txtRateOfInterest.getValue().toString().isEmpty())
					{
						double Loan = Double.parseDouble(txtSanctionAmount.getValue().toString().replace(",", ""));
						double interest = Double.parseDouble(txtRateOfInterest.getValue().toString().replace(",", ""));
						double totalInterest = (Loan*interest)/100;

						txtInterestAmount.setValue(new CommaSeparator().setComma(totalInterest));
						txtGrossLoanAmount.setValue(new CommaSeparator().setComma(totalInterest+Loan));
					}

					if(!txtGrossLoanAmount.getValue().toString().isEmpty())
					{
						double grossAmount = Double.parseDouble(txtGrossLoanAmount.getValue().toString().replace(",", ""));
						double noOfInstall = Double.parseDouble(txtNoOfInstallment.getValue().toString().replace(",", ""));

						txtAmountOfInstallment.setValue(new CommaSeparator().setComma(grossAmount/noOfInstall));
					}
				}
			}
		});
		cButton.btnPreview.addListener(new ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				if(cmbUnit.getValue()!=null)
				{
					if(cmbEmployeeId.getValue()!=null)
					{
						if(!txtLoanNo.getValue().toString().isEmpty())
						{
							reportpreview();
						}
						else
						{
							showNotification("Warning!","loan no not found!",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else
					{
						showNotification("Warning!","Employee id not found!",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else{
					showNotification("Warning!","Unit not found!",Notification.TYPE_WARNING_MESSAGE);
				}
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
			String query="select distinct vUnitId,vUnitName from tbEmpOfficialPersonalInfo order by vUnitName";
			
			System.out.println("Unit"+query);
			
			List <?> list=session.createSQLQuery(query).list();	

			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbUnit.addItem(element[0].toString());
				cmbUnit.setItemCaption(element[0].toString(), element[1].toString());
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
			String query = "Select vEmployeeID,vFingerId,vEmployeeCode,vEmployeeName FROM tbEmpOfficialPersonalInfo " +
					"where vUnitId='"+cmbUnit.getValue().toString()+"'and ISNULL(vProximityId,'')!='' and bStatus=1 "
					+ "order by vEmployeeName,vFingerId,vProximityId";

			List <?> list = session.createSQLQuery(query).list();

			
			System.out.println("EMP "+query);
			
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
		Window win = new LoanAppFind(sessionBean, txtFindLoanNo, txtFindDate);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if (txtFindLoanNo.getValue().toString().length() > 0)
				{
					txtClear();
					findInitialise(txtFindLoanNo.getValue().toString() , txtFindDate.getValue());
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
			String query = " Select cast(isnull(max(cast(replace(vLoanNo, '', '')as int))+1, 1)as varchar) as loanId from tbLoanApplication ";

			Iterator <?> iter = session.createSQLQuery(query).list().iterator();
     System.out.println("AUTO "+query);

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
	private void reportpreview()
	{
		try
		{
			HashMap <String,Object> hm = new HashMap <String,Object> ();
			hm.put("company", cmbUnit.getItemCaption(cmbUnit.getValue()));
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("userName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			System.out.println(""+sessionBean.getUserName()+" "+sessionBean.getUserIp());
			System.out.println(""+sessionBean.getCompanyAddress());
			hm.put("SysDate",reportTime.getTime);
			hm.put("logo", txtPath.getValue().toString().isEmpty()?"0":txtPath.getValue().toString());

			String query = " SELECT * from funLoanApplicationInfo ('"+cmbEmployeeId.getValue().toString()+"','"+txtLoanNo.getValue()+"') "
					+ " where vUnitId='"+cmbUnit.getValue().toString()+"'";
			System.out.println("Report: "+query);
			if(queryValueCheck(query))
			{
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/account/hrmModule/rptLoanApplication.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",true);

				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);
			}
			else
			{
				showNotification("Warning","There are no Data",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private boolean queryValueCheck(String sql)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try 
		{
			Iterator <?> iter = session.createSQLQuery(sql).list().iterator();

			if (iter.hasNext()) 
			{
				return true;
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		finally{session.close();}
		return false;
	}
	private void findInitialise(String LoanId, Object findDate) 
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();

		try 
		{
			String sql="select iAutoId,vLoanNo,dApplicationDate,vAutoEmployeeId,vLoanType,mLoanAmount,mInterestRate, " +
					"mInterestAmount,mGrossAmount,vLoanPurpose,mInstallmentNo,mAmountPerInstall,dPaymentStart, " +
					"vAdjustType,dSanctionDate,mSanctionAmount,iSanctionStatus,iLoanStatus,mLoanBalance,vUserId, " +
					"vUserIp,dEntryTime,(select vUnitId from tbEmpOfficialPersonalInfo where vEmployeeId=la.vAutoEmployeeId)vUnitId, "+
					"(select vUnitName from tbEmpOfficialPersonalInfo where vEmployeeId=la.vAutoEmployeeId)vUnitName "+
					"from tbLoanApplication la where vLoanNo = '"+LoanId+"' and dApplicationDate = '"+findDate+"' ";
			List <?> list = session.createSQLQuery(sql).list();
		   
			System.out.println("AUTO "+sql);
			
			
			if(list.iterator().hasNext())
			{
				Object[] element = (Object[]) list.iterator().next();

				txtLoanNo.setValue(element[1].toString());
				dApplicationDate.setValue((element[2]));
				cmbEmployeeId.setValue(element[3]);
				cmbLoanType.setValue(element[4]);
				txtLoanAmount.setValue(df.format(Double.parseDouble(element[5].toString())));
				txtRateOfInterest.setValue(df.format(Double.parseDouble(element[6].toString())));
				txtInterestAmount.setValue(new CommaSeparator().setComma(Double.parseDouble(element[7].toString())));
				txtGrossLoanAmount.setValue(new CommaSeparator().setComma(Double.parseDouble(element[8].toString())));
				txtLoanPurpose.setValue(element[9].toString());
				txtNoOfInstallment.setValue(df.format(Double.parseDouble(element[10].toString())));
				txtAmountOfInstallment.setValue(new CommaSeparator().setComma(Double.parseDouble(element[11].toString())));
				dPaymentStart.setValue(element[12]);
				cmbAdjustType.setValue(element[13]);

				if(element[14].toString().equals("1900-01-01 00:00:00.0"))
				{
					dSanctionDate.setValue(new java.util.Date());
				}
				else
				{
					dSanctionDate.setValue(element[14]);
				}
				if(Double.parseDouble(element[15].toString())!=0)
				{
					txtSanctionAmount.setValue(df.format(Double.parseDouble(element[15].toString())));
				}

				updateID = element[0].toString();
				findApplicationDate = element[2].toString();
				isSanction = element[16].toString();
				cmbUnit.setValue(element[22].toString());
				cmbUnit.setItemCaption(element[22].toString(), element[23].toString());
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
			String query = " SELECT E.vEmployeeName, E.vEmployeeCode,E.vFingerId,E.vGender,S.vSectionId, S.vSectionName, " +
					"D.vDesignationId, D.vDesignation,E.dJoiningDate," +
					"(ES.mBasic+Es.mHouseRent+ES.mMedicalAllowance+ES.mConveyanceAllowance)as mGross," +
					"E.vProximityID,S.vDepartmentName FROM tbEmpOfficialPersonalInfo AS E INNER JOIN tbEmpOfficialPersonalInfo AS S ON" +
					" E.vEmployeeId = S.vEmployeeId INNER JOIN tbEmpDesignationInfo AS D ON " +
					"E.vEmployeeId = D.vEmployeeId inner join tbEmpSalaryStructure ES on E.vEmployeeId = ES.vEmployeeId" +
					" WHERE E.vUnitId='"+cmbUnit.getValue().toString()+"' and E.vEmployeeId = '"+employeeId+"' ORDER BY E.vEmployeeType ";

		List <?> list = session.createSQLQuery(query).list();
		
			System.out.println("Employee set qury :" + query);

			if(list.iterator().hasNext())
			{
				Object[] element = (Object[]) list.iterator().next();
				if(ogSelectEmployee.getValue()=="Employee ID")
				{
					txtFingerId.setValue(element[0]);
					txtEmployeeName.setValue(element[2]);
				}
				if(ogSelectEmployee.getValue()=="Finger ID")
				{
					txtFingerId.setValue(element[0]);
					txtEmployeeName.setValue(element[1]);
				}
				if(ogSelectEmployee.getValue()=="Employee Name")
				{
					txtFingerId.setValue(element[2]);
					txtEmployeeName.setValue(element[1]);
				}

				txtSection.setValue(element[5]);
				txtDesignation.setValue(element[7]);

				dJoiningDate.setValue(element[8]);
				txtGrossSalary.setValue(new CommaSeparator().setComma(Double.parseDouble(element[9].toString())));
				txtDepartment.setValue(element[11].toString());
			}

			String pending = " select * from tbLoanApplication where vAutoEmployeeId = '"+employeeId+"' and iLoanStatus = '1' " ;

			List <?> pendingList = session.createSQLQuery(pending).list();

			if(pendingList.iterator().hasNext())
			{
				pendingLoan = true;
			}
			else
			{
				pendingLoan = false;
			}

			String active = " select * from tbLoanApplication where vAutoEmployeeId = '"+employeeId+"' and iLoanStatus = '1' and iSanctionStatus='1' " ;

			List <?> activeLoanList = session.createSQLQuery(active).list();

			System.out.println("Employee loan :" + active);
			
			if(activeLoanList.iterator().hasNext())
			{
				activeLoan = true;
			}
			else
			{
			
				activeLoan = false;
			}
		}
		catch(Exception ex)
		{
			showNotification("employeeSetData", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private boolean chkLoanApplication(String query)
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> lst=session.createSQLQuery(query).list();
			if(!lst.isEmpty())
				return true;
		}
		catch(Exception exp)
		{
			showNotification("formValidation", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
		return false;
	}

	private void formValidation()
	{
		if(cmbEmployeeId.getValue()!=null)
		{
			if(cmbLoanType.getValue()!=null)
			{
				if(!txtLoanAmount.getValue().toString().isEmpty())
				{
					if(!txtRateOfInterest.getValue().toString().equals(""))
					{
						if(!txtLoanPurpose.getValue().toString().equals(""))
						{
							if(!txtNoOfInstallment.getValue().toString().equals(""))
							{
								if(!txtSanctionAmount.getValue().toString().equals(""))
								{
									if(isUpdate)
									{
										String query="select * from tbLoanApplication where mLoanBalance=mSanctionAmount and vLoanNo = '"+txtFindLoanNo.getValue().toString()+"' and dApplicationDate = '"+txtFindDate.getValue()+"' ";
										if(chkLoanApplication(query))
										{
											saveBtnAction();
										}
										else
										{
											showNotification("Warning", "Please don't try to Update!!!", Notification.TYPE_WARNING_MESSAGE);
										}
									}
									else
									{
										saveBtnAction();
									}
								}
								else
								{
									showNotification("Warning!","Please Provide Sanction Amount", Notification.TYPE_WARNING_MESSAGE);
									txtSanctionAmount.focus();
								}
							}
							else
							{
								showNotification("Warning!","Please Provide No of Loan Installment", Notification.TYPE_WARNING_MESSAGE);
								txtNoOfInstallment.focus();
							}
						}
						else
						{
							showNotification("Warning!","Please Provide Loan Purpose", Notification.TYPE_WARNING_MESSAGE);
							txtLoanPurpose.focus();
						}
					}
					else
					{
						showNotification("Warning!","Please Provide Rate of Interest", Notification.TYPE_WARNING_MESSAGE);
						txtRateOfInterest.focus();
					}
				}
				else
				{
					showNotification("Warning!","Please Provide Loan Amount", Notification.TYPE_WARNING_MESSAGE);
					txtLoanAmount.focus();
				}
			}
			else
			{
				showNotification("Warning!","Select Loan Type", Notification.TYPE_WARNING_MESSAGE);
				cmbLoanType.focus();
			}
		}
		else
		{
			showNotification("Warning!","Select Employee Name", Notification.TYPE_WARNING_MESSAGE);
			cmbEmployeeId.focus();
		}
	}

	private void saveBtnAction()
	{
		if(isUpdate)
		{
			if(checkLoanAmount())
			{

				if(!txtNoOfInstallment.getValue().toString().equals("") && Double.parseDouble(txtNoOfInstallment.getValue().toString())<=10000)
				{

					if(!txtSanctionAmount.getValue().toString().equals("") && Double.parseDouble(txtSanctionAmount.getValue().toString())>0)
					{
						if(!activeLoan)
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
							showNotification("Warning!","This employee has a unbalanced active loan",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else
					{
						showNotification("Warning!","Provide sanction amount",Notification.TYPE_WARNING_MESSAGE);
					}
				
				}
				else{
					showNotification("Warning!","Please reduce your installment!",Notification.TYPE_WARNING_MESSAGE);
				}
			
			}
			else{
				showNotification("Warning!","Your loan amount is over!",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else
		{
			if(!pendingLoan)
			{
				if(checkLoanAmount())
				{
					if(!txtNoOfInstallment.getValue().toString().equals("") && Double.parseDouble(txtNoOfInstallment.getValue().toString())<=300)
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
					else{
						showNotification("Warning!","Please reduce your installment!",Notification.TYPE_WARNING_MESSAGE);
					}

				
				}
				else
				{
					showNotification("Warning!","Your loan amount is over!",Notification.TYPE_WARNING_MESSAGE);
				}
			}
			else
			{
				showNotification("Warning!","This employee has a pending loan request",Notification.TYPE_WARNING_MESSAGE);
			}
		}
	}
	private boolean checkLoanAmount()
	{

		double basicAmount,returnAmount;
		basicAmount=Double.parseDouble(txtGrossSalary.getValue().toString().replaceAll(",","").isEmpty()?"0.00":txtGrossSalary.getValue().toString().replaceAll(",",""));
		returnAmount=basicAmount*100;
		
		if(!txtLoanAmount.getValue().toString().replaceAll(",","").equals("") && Double.parseDouble(txtLoanAmount.getValue().replaceAll(",","").toString())<returnAmount)
		{
			return true;
		}
	
		return false;
	}
	private void insertData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		String UnitName = cmbUnit.getItemCaption(cmbUnit.getValue());
		String EmployeeName = cmbEmployeeId.getItemCaption(cmbEmployeeId.getValue());
		String EmployeeId = txtEmployeeName.getValue().toString().trim();
		String FingerId = txtFingerId.getValue().toString().trim();

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
			String insertQuery = "Insert into tbLoanApplication (vLoanNo,dApplicationDate,vAutoEmployeeId,vEmployeeId,"
					+ "vEmployeeName,vFingerId,vProximityID,vUnitId,vUnitName,vSectionId,vSectionName,vDesignationId,vDesignationName,"
					+ "dJoiningDate,mGrossSalary,vLoanType,mLoanAmount," +
					"mInterestRate,mInterestAmount,mGrossAmount,vLoanPurpose,mInstallmentNo,mAmountPerInstall,"
					+ "dPaymentStart,vAdjustType,dSanctionDate,mSanctionAmount,iSanctionStatus,iLoanStatus," +
					"mLoanBalance,vUserId,vUserIp,dEntryTime) values (" +
					" '"+autoId()+"', " +
					"'"+dateFormat.format(dApplicationDate.getValue())+"'," +
					" '"+cmbEmployeeId.getValue().toString()+"'," +
					" '"+EmployeeId+"'," +
					" '"+EmployeeName+"'," +
					" '"+FingerId+"'," +
					"(select vProximityID from tbEmpOfficialPersonalInfo where vEmployeeId='"+cmbEmployeeId.getValue().toString().trim()+"')," +
					"(select vUnitId from tbEmpOfficialPersonalInfo where vEmployeeId='"+cmbEmployeeId.getValue().toString().trim()+"')," +
					"''," +
					"''," +					
					"'"+txtSection.getValue().toString()+"'," +
					"(select vDesignationId from tbEmpDesignationInfo where vEmployeeId='"+cmbEmployeeId.getValue().toString().trim()+"')," +
					"'"+txtDesignation.getValue().toString()+"'," +
					" '"+dateFormat.format(dJoiningDate.getValue())+"'," +
					"'"+txtGrossSalary.getValue().toString().replace(",", "")+"', " +
					" '"+cmbLoanType.getValue().toString()+"', " +
					"'"+txtLoanAmount.getValue().toString().replace(",", "")+"'," +
					" '"+txtRateOfInterest.getValue().toString()+"'," +
					" '"+txtInterestAmount.getValue().toString().replace(",", "")+"'," +
					" '"+txtGrossLoanAmount.getValue().toString().replace(",", "")+"', " +
					"'"+txtLoanPurpose.getValue().toString()+"'," +
					" '"+txtNoOfInstallment.getValue().toString()+"', " +
					"'"+txtAmountOfInstallment.getValue().toString().replace(",", "")+"', " +
					"'"+dateFormat.format(dPaymentStart.getValue())+"'," +
					" 'Monthly Salary', '"+dateFormat.format(dSanctionDate.getValue())+"', " +
					"'"+txtSanctionAmount.getValue().toString().replace(",", "")+"', '1', '1', '"+txtGrossLoanAmount.getValue()+"'," +
					" '"+sessionBean.getUserId()+"', '"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP) ";
			System.out.println("Insert Query :" + insertQuery);		
			session.createSQLQuery(insertQuery).executeUpdate();
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
		String EmployeeName = cmbEmployeeId.getItemCaption(cmbEmployeeId.getValue());
		String EmployeeId = txtEmployeeName.getValue().toString().trim();
		String FingerId = txtFingerId.getValue().toString().trim();
		String updateQuery = "";

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
			updateQuery = "UPDATE tbLoanApplication set " +
					" vLoanNo = '"+txtLoanNo.getValue().toString()+"' ," +
					" vAutoEmployeeId = '"+cmbEmployeeId.getValue().toString().trim()+"' ,"+
					" vEmployeeId = '"+EmployeeId+"'," +
					" vEmployeeName ='"+EmployeeName+"'," +
					" vFingerId ='"+FingerId+"'," +
					" vProximityID = (select vProximityID from tbEmpOfficialPersonalInfo where vEmployeeId='"+cmbEmployeeId.getValue().toString().trim()+"'),"+
					"vUnitId =(select vUnitId from tbEmpOfficialPersonalInfo where vEmployeeId='"+cmbEmployeeId.getValue().toString().trim()+"')," +
					" vSectionId = (select vSectionId from tbEmpOfficialPersonalInfo where vEmployeeId='"+cmbEmployeeId.getValue().toString().trim()+"')," +
					" vDesignationId = (select vDesignationId from tbEmpDesignationInfo where vEmployeeId='"+cmbEmployeeId.getValue().toString().trim()+"')," +
					" dJoiningDate = '"+dateFormat.format(dJoiningDate.getValue())+"' ," +
					" mGrossSalary = '"+txtGrossSalary.getValue().toString()+"' ," +
					" vLoanType = '"+cmbLoanType.getValue()+"' ," +
					" mLoanAmount = '"+txtLoanAmount.getValue()+"' ," +
					" mInterestRate = '"+txtRateOfInterest.getValue()+"' ," +
					" mInterestAmount = '"+txtInterestAmount.getValue()+"' ," +
					" mGrossAmount = '"+txtGrossLoanAmount.getValue()+"' ," +
					" vLoanPurpose = '"+txtLoanPurpose.getValue()+"' ," +
					" mInstallmentNo = '"+txtNoOfInstallment.getValue()+"' ," +
					" mAmountPerInstall = '"+txtAmountOfInstallment.getValue()+"' , " +
					" dPaymentStart = '"+dateFormat.format(dPaymentStart.getValue())+"' ," +
					" vAdjustType = 'Monthly Salary' ," +
					" dSanctionDate = '"+dateFormat.format(dSanctionDate.getValue())+"' ," +
					" mSanctionAmount = '"+txtSanctionAmount.getValue().toString()+"' ," +
					" iSanctionStatus = '1' ," +
					" iLoanStatus = '1' ," +
					" mLoanBalance = '"+txtGrossLoanAmount.getValue()+"' ," +
					" dEntryTime=CURRENT_TIMESTAMP, vUserIp='"+sessionBean.getUserIp()+"', " +
					" vUserId='"+sessionBean.getUserId()+"'" +
					" where iAutoId='"+updateID+"' ";
		session.createSQLQuery(updateQuery).executeUpdate();
		
		
		System.out.println("Update Query :" + updateQuery);


			tx.commit();
			showNotification("All Information Updated Successfully");
		}
		catch(Exception ex)
		{
			tx.rollback();
			showNotification("updateData", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
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
		cmbUnit.setEnabled(t);
		ogSelectEmployee.setEnabled(t);
		if(isUpdate==true)
		{dApplicationDate.setEnabled(!t);}
		else
		{dApplicationDate.setEnabled(t);}
		txtLoanNo.setEnabled(t);
		cmbEmployeeId.setEnabled(t);
		txtEmployeeName.setEnabled(t);
		txtFingerId.setEnabled(t);
		txtSection.setEnabled(t);
		txtDepartment.setEnabled(t);
		txtDesignation.setEnabled(t);
		txtGrossSalary.setEnabled(t);
		cmbLoanType.setEnabled(t);
		txtLoanAmount.setEnabled(t);
		txtRateOfInterest.setEnabled(t);
		txtInterestAmount.setEnabled(t);
		txtGrossLoanAmount.setEnabled(t);
		txtLoanPurpose.setEnabled(t);
		txtNoOfInstallment.setEnabled(t);
		txtAmountOfInstallment.setEnabled(t);
		dPaymentStart.setEnabled(t);
		cmbAdjustType.setEnabled(t);

		txtSanctionAmount.setEnabled(t);
		dSanctionDate.setEnabled(t);

		if(isSanction.equals("1"))
		{
			
			cmbEmployeeId.setEnabled(false);
			txtLoanAmount.setEnabled(false);
			txtRateOfInterest.setEnabled(false);
			txtNoOfInstallment.setEnabled(false);
			cmbAdjustType.setEnabled(false);
		}
	}

	private void txtClear()
	{
		dApplicationDate.setValue(new java.util.Date());
		txtLoanNo.setValue("");		
		cmbEmployeeId.setValue(null);
		txtEmployeeName.setValue("");
		txtFingerId.setValue("");
		txtSection.setValue("");
		txtDepartment.setValue("");
		txtDesignation.setValue("");
		dJoiningDate.setValue(new java.util.Date());
		txtGrossSalary.setValue("");
		txtLoanAmount.setValue("");
		txtRateOfInterest.setValue("");
		txtInterestAmount.setValue("");
		txtGrossLoanAmount.setValue("");
		txtLoanPurpose.setValue("");
		txtNoOfInstallment.setValue("");
		txtAmountOfInstallment.setValue("");
		dPaymentStart.setValue(new java.util.Date());
		cmbAdjustType.setValue(null);

		txtSanctionAmount.setValue("");
		dSanctionDate.setValue(new java.util.Date());
	}

	private void focusEnter()
	{
		allComp.add(dApplicationDate);
		allComp.add(cmbUnit);
		allComp.add(cmbEmployeeId);
		allComp.add(txtLoanAmount);
		allComp.add(txtRateOfInterest);
		allComp.add(txtLoanPurpose);
		allComp.add(txtNoOfInstallment);
		allComp.add(dSanctionDate);
		allComp.add(txtSanctionAmount);
		allComp.add(dPaymentStart);
		allComp.add(cmbAdjustType);

		allComp.add(cButton.btnSave);

		new FocusMoveByEnter(this,allComp);
	}

	public AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("780px");
		mainLayout.setHeight("390px");

		// lblApplicationDate
		lblApplicationDate = new Label("Application Date : ");
		lblApplicationDate.setImmediate(false);
		lblApplicationDate.setWidth("-1px");
		lblApplicationDate.setHeight("-1px");
		mainLayout.addComponent(lblApplicationDate, "top:15.0px; left:30.0px;");

		// dApplicationDate
		dApplicationDate = new PopupDateField();
		dApplicationDate.setImmediate(false);
		dApplicationDate.setWidth("110px");
		dApplicationDate.setHeight("-1px");
		dApplicationDate.setValue(new java.util.Date());
		dApplicationDate.setDateFormat("dd-MM-yyyy");
		dApplicationDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dApplicationDate, "top:13.0px; left:140.0px;");

		// lblLoanNo
		lblLoanNo = new Label("Loan No :");
		lblLoanNo.setImmediate(false);
		lblLoanNo.setWidth("-1px");
		lblLoanNo.setHeight("-1px");
		mainLayout.addComponent(lblLoanNo, "top:40.0px; left:30.0px;");

		// txtLoanNo
		txtLoanNo = new TextRead();
		txtLoanNo.setImmediate(true);
		txtLoanNo.setWidth("70px");
		txtLoanNo.setHeight("22px");
		mainLayout.addComponent(txtLoanNo, "top:38.0px; left:140.0px;");

		// lblLoanType
		lblLoanType = new Label("Loan Type :");
		lblLoanType.setImmediate(false);
		lblLoanType.setWidth("-1px");
		lblLoanType.setHeight("-1px");
		mainLayout.addComponent(lblLoanType, "top:65.0px; left:30.0px;");

		// cmbLoanType
		cmbLoanType = new ComboBox();
		cmbLoanType.setImmediate(true);
		cmbLoanType.setWidth("130px");
		cmbLoanType.setHeight("-1px");
		mainLayout.addComponent(cmbLoanType, "top:63.0px; left:140.0px;");
		for(int i=0; i<loanType.length; i++)
		{cmbLoanType.addItem(loanType[i]);}
		cmbLoanType.setValue("Salary Loan");
		

		// lblEmployeeName
		lblUnit = new Label("Project Name :");
		lblUnit.setImmediate(false);
		lblUnit.setWidth("-1px");
		lblUnit.setHeight("-1px");
		mainLayout.addComponent(lblUnit, "top:90.0px; left:30.0px;");
		
		// cmbEmployeeId
		cmbUnit = new ComboBox();
		cmbUnit.setImmediate(true);
		cmbUnit.setWidth("230px");
		cmbUnit.setHeight("-1px");
		cmbUnit.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbUnit, "top:88.0px; left:140.0px;");
	
		
		//RadioButtonType	
		ogSelectEmployee = new OptionGroup("",lst);
		ogSelectEmployee.setImmediate(true);
		ogSelectEmployee.setStyleName("horizontal");
		ogSelectEmployee.select("Employee Name");
		mainLayout.addComponent(ogSelectEmployee, "top:115.0px;left:25.0px;");

		// lblEmployeeId
		lblEmployeeId = new Label("Employee ID :");
		lblEmployeeId.setImmediate(false);
		lblEmployeeId.setWidth("-1px");
		lblEmployeeId.setHeight("-1px");
		mainLayout.addComponent(lblEmployeeId, "top:140.0px; left:30.0px;");

		// cmbEmployeeId
		cmbEmployeeId = new ComboBox();
		cmbEmployeeId.setImmediate(true);
		cmbEmployeeId.setWidth("230px");
		cmbEmployeeId.setHeight("-1px");
		cmbEmployeeId.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbEmployeeId, "top:138.0px; left:140.0px;");

		// lblEmployeeName
		lblEmployeeName = new Label("Employee Name :");
		lblEmployeeName.setImmediate(false);
		lblEmployeeName.setWidth("-1px");
		lblEmployeeName.setHeight("-1px");
		mainLayout.addComponent(lblEmployeeName, "top:165.0px; left:30.0px;");

		// txtEmployeeName
		txtEmployeeName = new TextRead();
		txtEmployeeName.setImmediate(true);
		txtEmployeeName.setWidth("110px");
		txtEmployeeName.setHeight("22px");
		mainLayout.addComponent(txtEmployeeName, "top:163.0px; left:141.0px;");

		// lblFingerId
		lblFingerId = new Label("Finger ID :");
		lblFingerId.setImmediate(false);
		lblFingerId.setWidth("-1px");
		lblFingerId.setHeight("-1px");
		mainLayout.addComponent(lblFingerId, "top:190.0px; left:30.0px;");

		// txtFingerId
		txtFingerId = new TextRead();
		txtFingerId.setImmediate(true);
		txtFingerId.setWidth("110px");
		txtFingerId.setHeight("22px");
		mainLayout.addComponent(txtFingerId, "top:188.0px; left:141.0px;");
		
		txtDepartment = new TextRead();
		txtDepartment.setImmediate(true);
		txtDepartment.setWidth("220px");
		txtDepartment.setHeight("22px");
		mainLayout.addComponent(new Label("Department :"), "top:215.0px; left:30.0px;");
		mainLayout.addComponent(txtDepartment, "top:213.0px; left:141.0px;");

		// lblSection
		lblSection = new Label("Section Name :");
		lblSection.setImmediate(false);
		lblSection.setWidth("-1px");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection, "top:240px; left:30.0px;");

		// txtSection
		txtSection = new TextRead();
		txtSection.setImmediate(true);
		txtSection.setWidth("220px");
		txtSection.setHeight("22px");
		mainLayout.addComponent(txtSection, "top:238px; left:141.0px;");

		// lblDesignation
		lblDesignation = new Label("Designation :");
		lblDesignation.setImmediate(false);
		lblDesignation.setWidth("-1px");
		lblDesignation.setHeight("-1px");
		mainLayout.addComponent(lblDesignation, "top:265px; left:30.0px;");

		// txtDesignation
		txtDesignation = new TextRead();
		txtDesignation.setImmediate(true);
		txtDesignation.setWidth("220px");
		txtDesignation.setHeight("22px");
		mainLayout.addComponent(txtDesignation, "top:263px; left:141.0px;");

		// lblJoiningDate
		lblJoiningDate = new Label("Joining Date : ");
		lblJoiningDate.setImmediate(false);
		lblJoiningDate.setWidth("-1px");
		lblJoiningDate.setHeight("-1px");
		mainLayout.addComponent(lblJoiningDate, "top:290px; left:30.0px;");

		// dJoiningDate
		dJoiningDate = new PopupDateField();
		dJoiningDate.setImmediate(true);
		dJoiningDate.setWidth("110px");
		dJoiningDate.setHeight("-1px");
		dJoiningDate.setValue(new java.util.Date());
		dJoiningDate.setDateFormat("dd-MM-yyyy");
		dJoiningDate.setEnabled(false);
		dJoiningDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dJoiningDate, "top:288px; left:140.0px;");

		// lblGrossSalary
		lblGrossSalary = new Label("Gross Salary :");
		lblGrossSalary.setImmediate(false);
		lblGrossSalary.setWidth("-1px");
		lblGrossSalary.setHeight("-1px");
		mainLayout.addComponent(lblGrossSalary, "top:315px; left:30.0px;");

		// txtGrossSalary
		txtGrossSalary = new TextRead(1);
		txtGrossSalary.setImmediate(true);
		txtGrossSalary.setWidth("100px");
		txtGrossSalary.setHeight("24px");
		mainLayout.addComponent(txtGrossSalary, "top:313px; left:140.0px;");

		// lblLoanAmount
		lblLoanAmount = new Label("Loan Amount :");
		lblLoanAmount.setImmediate(false);
		lblLoanAmount.setWidth("-1px");
		lblLoanAmount.setHeight("-1px");
		mainLayout.addComponent(lblLoanAmount, "top:15.0px; left:400.0px;");

		// txtLoanAmount
		txtLoanAmount = new AmountCommaSeperator();
		txtLoanAmount.setImmediate(true);
		txtLoanAmount.setWidth("110px");
		txtLoanAmount.setHeight("-1px");
		mainLayout.addComponent(txtLoanAmount, "top:13.0px; left:545.0px;");

		// lblRateOfInterest
		lblRateOfInterest = new Label("Rate of Interest :");
		lblRateOfInterest.setImmediate(false);
		lblRateOfInterest.setWidth("-1px");
		lblRateOfInterest.setHeight("-1px");
		mainLayout.addComponent(lblRateOfInterest, "top:40.0px; left:400.0px;");

		// txtRateOfInterest
		txtRateOfInterest = new AmountField();
		txtRateOfInterest.setImmediate(true);
		txtRateOfInterest.setWidth("50px");
		txtRateOfInterest.setHeight("-1px");
		mainLayout.addComponent(txtRateOfInterest, "top:38.0px; left:545.0px;");

		// lblRateOfInterestP
		lblRateOfInterestP = new Label("<html> <b>%</b> </html>",Label.CONTENT_XHTML);
		lblRateOfInterestP.setImmediate(false);
		lblRateOfInterestP.setWidth("-1px");
		lblRateOfInterestP.setHeight("-1px");
		mainLayout.addComponent(lblRateOfInterestP, "top:40.0px; left:600.0px;");

		// lblInterestAmount
		lblInterestAmount = new Label("Interest Amount :");
		lblInterestAmount.setImmediate(false);
		lblInterestAmount.setWidth("-1px");
		lblInterestAmount.setHeight("-1px");
		mainLayout.addComponent(lblInterestAmount, "top:65.0px; left:400.0px;");

		// txtInterestAmount
		txtInterestAmount = new TextRead(1);
		txtInterestAmount.setImmediate(true);
		txtInterestAmount.setWidth("110px");
		txtInterestAmount.setHeight("24px");
		mainLayout.addComponent(txtInterestAmount, "top:63.0px; left:545.0px;");

		// lblGrossLoanAmount
		lblGrossLoanAmount = new Label("Gross Amount :");
		lblGrossLoanAmount.setImmediate(true);
		lblGrossLoanAmount.setWidth("-1px");
		lblGrossLoanAmount.setHeight("-1px");
		mainLayout.addComponent(lblGrossLoanAmount, "top:90.0px; left:400.0px;");

		// txtPurposeOfLeave
		txtGrossLoanAmount = new TextRead(1);
		txtGrossLoanAmount.setImmediate(true);
		txtGrossLoanAmount.setWidth("110px");
		txtGrossLoanAmount.setHeight("24px");
		mainLayout.addComponent(txtGrossLoanAmount, "top:88.0px; left:545.0px;");
		
		
		
		

		// lblLoanPurpose
		lblLoanPurpose = new Label("Purpose :");
		lblLoanPurpose.setImmediate(false);
		lblLoanPurpose.setWidth("-1px");
		lblLoanPurpose.setHeight("-1px");
		mainLayout.addComponent(lblLoanPurpose, "top:115.0px; left:400.0px;");

		// txtLoanPurpose
		txtLoanPurpose = new TextField();
		txtLoanPurpose.setImmediate(true);
		txtLoanPurpose.setWidth("200px");
		txtLoanPurpose.setHeight("48px");
		mainLayout.addComponent(txtLoanPurpose, "top:113.0px; left:545.0px;");

		// lblNoOfInstallment
		lblNoOfInstallment = new Label("No of Installment :");
		lblNoOfInstallment.setImmediate(false);
		lblNoOfInstallment.setWidth("-1px");
		lblNoOfInstallment.setHeight("-1px");
		mainLayout.addComponent(lblNoOfInstallment, "top:165.0px; left:400.0px;");

		// txtNoOfInstallment
		txtNoOfInstallment = new AmountField();
		txtNoOfInstallment.setImmediate(true);
		txtNoOfInstallment.setWidth("50px");
		txtNoOfInstallment.setHeight("-1px");
		mainLayout.addComponent(txtNoOfInstallment, "top:163.0px; left:545.0px;");

		// lblAmountOfInstallment
		lblAmountOfInstallment = new Label("Amount per Installment :");
		lblAmountOfInstallment.setImmediate(false);
		lblAmountOfInstallment.setWidth("-1px");
		lblAmountOfInstallment.setHeight("-1px");
		mainLayout.addComponent(lblAmountOfInstallment, "top:190.0px; left:400.0px;");

		// txtAmountOfInstallment
		txtAmountOfInstallment = new TextRead(1);
		txtAmountOfInstallment.setImmediate(true);
		txtAmountOfInstallment.setWidth("110px");
		txtAmountOfInstallment.setHeight("-1px");
		mainLayout.addComponent(txtAmountOfInstallment, "top:188.0px; left:545.0px;");	

		
		// lblLoanSanction
		lblLoanSanction = new Label("<html><font color='#74078B'> <strong>Sanction Details</strong></font></html>",Label.CONTENT_XHTML);
		lblLoanSanction.setImmediate(false);
		lblLoanSanction.setWidth("-1px");
		lblLoanSanction.setHeight("-1px");
		mainLayout.addComponent(lblLoanSanction, "top:215.0px; left:450.0px;");

		// lblSanctionDate
		lblSanctionDate = new Label("Sanction Date :");
		lblSanctionDate.setImmediate(false);
		lblSanctionDate.setWidth("-1px");
		lblSanctionDate.setHeight("-1px");
		mainLayout.addComponent(lblSanctionDate, "top:240.0px; left:400.0px;");

		// dSanctionDate
		dSanctionDate = new PopupDateField();
		dSanctionDate.setImmediate(true);
		dSanctionDate.setWidth("110px");
		dSanctionDate.setHeight("-1px");
		dSanctionDate.setValue(new java.util.Date());
		dSanctionDate.setDateFormat("dd-MM-yyyy");
		dSanctionDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dSanctionDate, "top:238.0px; left:545.0px;");

		// lblSanctionAmount
		lblSanctionAmount = new Label("Sanction Amount :");
		lblSanctionAmount.setImmediate(false);
		lblSanctionAmount.setWidth("-1px");
		lblSanctionAmount.setHeight("-1px");
		mainLayout.addComponent(lblSanctionAmount, "top:265.0px; left:400.0px;");

		// txtSanctionAmount
		txtSanctionAmount = new AmountCommaSeperator();
		txtSanctionAmount.setImmediate(true);
		txtSanctionAmount.setWidth("100px");
		txtSanctionAmount.setHeight("-1px");
		mainLayout.addComponent(txtSanctionAmount, "top:263.0px; left:545.0px;");

		// lblPaymentStart
		lblPaymentStart = new Label("Payment Start :");
		lblPaymentStart.setImmediate(false);
		lblPaymentStart.setWidth("-1px");
		lblPaymentStart.setHeight("-1px");
		mainLayout.addComponent(lblPaymentStart, "top:290.0px; left:400.0px;");

		// dPaymentStart
		dPaymentStart = new PopupDateField();
		dPaymentStart.setImmediate(true);
		dPaymentStart.setWidth("110px");
		dPaymentStart.setHeight("-1px");
		dPaymentStart.setValue(new java.util.Date());
		dPaymentStart.setDateFormat("dd-MM-yyyy");
		dPaymentStart.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dPaymentStart, "top:288.0px; left:545.0px;");

		cmbAdjustType = new ComboBox();
		for(int i=0;i<adjustType.length;i++)
		{cmbAdjustType.addItem(adjustType[i]);}
		
		mainLayout.addComponent(cButton, "top:340.0px; left:100.0px;");
		
		
		return mainLayout;
	}
}
