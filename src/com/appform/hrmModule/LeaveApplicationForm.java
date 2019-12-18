package com.appform.hrmModule;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Transaction;
import org.hibernate.classic.Session;

import com.common.share.CommonButton;
import com.common.share.CommonMethod;
import com.common.share.FocusMoveByEnter;
import com.common.share.ReportDate;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.MessageBox;
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

@SuppressWarnings("serial")
public class LeaveApplicationForm extends Window 
{
	private CommonButton cButton;
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private TextRead txtReferenceNo;

	private Label lblappdate;
	private PopupDateField dApplicationDate;

	private Label lblUnit;
	private ComboBox cmbUnit;
	
	private Label lblSection;
	private ComboBox cmbSection,cmbDepartment;
	private CheckBox chkAllSection;
	private CheckBox chkDepartmentAll;
	private OptionGroup ogSelectEmployee;
	private List<String> lst = Arrays.asList(new String[]{"Employee ID","Finger ID","Employee Name"});

	private Label lblEmployeeName;
	private ComboBox cmbEmployeeName;

	private Label lblFingerId;
	private TextRead txtFingerId;

	private Label lblProximityId;
	private TextRead txtProximityId;

	private Label lblDesignation;
	private TextRead txtDesignation;

	private Label lblJoiningDate;
	private PopupDateField dJoiningDate;

	private Label lblConfirmationDate;
	private PopupDateField dConfirmationDate;

	private Label lblEmployeeType;
	private TextRead txtEmployeeType;

	private Label lblLeaveBalance;

	private Label lblCasualLeave;
	private TextRead txtCasualLeave;

	private Label lblSickLeave;
	private TextRead txtSickLeave;

	private Label lblAnualLeave;
	private TextRead txtAnualLeave;

	private Label lblLeaveType;
	private ComboBox cmbLeaveType,cmbELType;
	private TextField txtLeaveType;

	private Label lblPaymentType;
	private OptionGroup ogPaymentType;

	private List<String> paymentTypeName = Arrays.asList(new String[]{"With Pay","Without Pay"/*,"Special"*/});

	private Label lblLeaveFrom;
	private PopupDateField dLeaveFrom;

	private Label lblLeaveTo;
	private PopupDateField dLeaveTo;

	private Label lblPurposeOfLeave;
	private TextField txtPurposeOfLeave;

	private Label lblLeaveAddress;
	private TextField txtLeaveAddress;

	private Label lblMobileNo;
	private TextField txtMobileNo;

	private Label lblDuration;
	private TextRead txtDuration;
	private Label lblDays;

	private Label lblFriday;
	private TextRead txtFriday=new TextRead();

	public Table table = new Table();
	public ArrayList<Label> tbllblEmployeeName = new ArrayList<Label>();
	public ArrayList<Label> tbllblSerial = new ArrayList<Label>();
	public ArrayList<Label> tbdDate = new ArrayList<Label>();
	public ArrayList<Label> tbdEntryDate = new ArrayList<Label>();
	public ArrayList<Label> tbllblWeekDay = new ArrayList<Label>();
	public ArrayList<Label> tblAdjustedType = new ArrayList<Label>();

	private TextField txtFindId = new TextField();

	private SimpleDateFormat dfReference = new SimpleDateFormat("yy");

	private boolean isFind = false;
	private boolean isUpdate = false;

	private String designationId = "";


	private String existLeaveDays = "";
	private CommonMethod cm;
	private String menuId = "";
	private ArrayList<Component> allComp = new ArrayList<Component>();
	boolean switchUser=false;
	int countLeave=0;
	

	private SimpleDateFormat dbMonthFormat = new SimpleDateFormat("MM");
	private SimpleDateFormat dbYearFormat = new SimpleDateFormat("yyyy");
	
	
	public LeaveApplicationForm(SessionBean sessionBean,String menuId,boolean switchUser) 
	{
		this.sessionBean=sessionBean;
		this.setCaption("LEAVE APPLICATION FORM :: "+sessionBean.getCompany());
		this.setResizable(false);
		if(switchUser==false)
		{cButton = new CommonButton("New", "Save", "Edit", "Delete", "Refresh", "Find", "", "Preview", "", "Exit");}
		else{cButton = new CommonButton( "New",  "Save",  "",  "",  "Refresh",  "Find", "", "Preview","","Exit");}
		
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		buildMainLayout();
		setContent(mainLayout);
		
		tableInitialise();
		cmbLeaveTypeAdd();
		txtInit(true);
		btnIni(true);
		setEventAction();
		cmbUnitDataAdd();
		focusEnter();
		if(switchUser==false)
		{
			authenticationCheck();
		}
		cButton.btnNew.focus();
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

	private boolean chkSalary(String query)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> lst = session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				return true;
			}
		}
		catch (Exception exp)
		{
			showNotification("chkSalary", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
		return false;
	}
	private void setEventAction()
	{
		cButton.btnNew.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				isFind = false;
				isUpdate = false;

				txtInit(false);
				btnIni(false);
				cButton.btnRefresh.setEnabled(true);
				mainClear();

				txtReferenceNo.setValue(selectMaxId());
			}
		});

		cButton.btnSave.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				formValidation();
			}
		});

		cButton.btnDelete.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(cmbEmployeeName.getValue()!=null)
				{
					if(cmbLeaveType.getValue()!=null || !txtLeaveType.getValue().toString().trim().isEmpty())
					{
						if(!txtPurposeOfLeave.getValue().toString().isEmpty())
						{
							if(!txtDuration.getValue().toString().isEmpty())
							{
								if(!txtMobileNo.getValue().toString().isEmpty())
								{	
									if(isFind || isUpdate)
									{
										MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to Delete information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
										mb.show(new EventListener()
										{
											public void buttonClicked(ButtonType buttonType)
											{
												if(buttonType == ButtonType.YES)
												{
													String query = "select top(5) * from tbMonthlySalary " +
															"where vEmployeeID='"+cmbEmployeeName.getValue()+"' " +
															"and ((MONTH(dSalaryDate) = '"+dbMonthFormat.format(dLeaveFrom.getValue())+"' and YEAR(dSalaryDate) = '"+dbYearFormat.format(dLeaveFrom.getValue())+"') " +
															"or (MONTH(dSalaryDate) = '"+dbMonthFormat.format(dLeaveTo.getValue())+"' and YEAR(dSalaryDate) = '"+dbYearFormat.format(dLeaveTo.getValue())+"'))";
													System.out.println("Check Salary: "+query);
													
													if(!chkSalary(query))
													{
														if(isDelete())
														{
															txtInit(true);
															btnIni(true);
															txtClear();

															isFind = false;
															isUpdate = false;

															Notification n=new Notification("All Information Delete Successfully!","",Notification.TYPE_TRAY_NOTIFICATION);
															n.setPosition(Notification.POSITION_TOP_RIGHT);
															showNotification(n);
														}
													}
													else
													{
														showNotification("Warning", "Salary Already Generated for this Month!!!", Notification.TYPE_WARNING_MESSAGE);
													}
												}
											}
										});
									}
								}
							}  
						}
					}
				}
			}
		});

		cButton.btnEdit.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				updateButtonAction();
				//cmbSection.setEnabled(false);
			}
		});

		cButton.btnRefresh.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				isFind = false;
				isUpdate = false;

				txtInit(true);
				btnIni(true);
				mainClear();
				cmbUnit.setEnabled(false);
				cmbSection.setEnabled(false);
				txtReferenceNo.setValue("");
				cButton.btnNew.focus();
			}
		});

		cButton.btnFind.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				isFind = true;
				findButtonEvent();
			}
		});

		cButton.btnExit.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				close();
			}
		});
		cmbUnit.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{  
				cmbDepartment.removeAllItems();
				chkDepartmentAll.setValue(false);
				if(cmbUnit.getValue()!=null)
				{
					cmbDepartmentDataAdd();
				}
				
				
			}
		});
		cmbDepartment.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{  
				cmbSection.removeAllItems();
				chkAllSection.setValue(false);
				if(cmbDepartment.getValue()!=null)
				{
					cmbSectionDataAdd();
				}
				
				
			}
		});
		chkDepartmentAll.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				cmbSection.removeAllItems();
				chkAllSection.setValue(false);
				if(cmbUnit.getValue()!=null)
				{
					if(chkDepartmentAll.booleanValue())
					{
						cmbDepartment.setValue(null);
						cmbDepartment.setEnabled(false);
						cmbSectionDataAdd();
					}
					else
					{
						cmbDepartment.setEnabled(true);
					}
				}
			}
		});
		
		cmbSection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbSection.getValue()!=null)
				{
					cmbEmployeeNameDataAdd();
				}
			}
		});

		cmbEmployeeName.addListener(new ValueChangeListener()
		{	
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbEmployeeName.getValue()!=null)				
				{
					//txtClear();
					employeeData();
					cmbELType.setValue(null);
					
				}
			}
		});

		ogSelectEmployee.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmployeeNameDataAdd();
			}
		});



		dApplicationDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				getLeaveId();
			}
		});
		
		cmbLeaveType.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbLeaveType.getValue()!=null)
				{
					dLeaveTo.setEnabled(true);
					if(dLeaveFrom.getValue()!=null && dLeaveTo.getValue()!=null)
					{
						try
						{
							findDayDiffernce();//dateDayAddTable();
						}
						catch (Exception e)
						{/*System.out.println("Error "+e);*/}
					}
					
					if(cmbLeaveType.getValue()!=null && isFind==false)
					{
						if(cmbLeaveType.getItemCaption(cmbLeaveType.getValue()).equals("Annual Leave"))
						{						
							cmbELType.setEnabled(true);
							int iELeave=0;
							iELeave=Integer.parseInt(txtAnualLeave.getValue()+"");
							System.out.println("cmbLeaveType iELeave: "+iELeave);
							
							if(iELeave>0)
							{
								cmbELType.setValue("Advance");
							}
							else
							{
								cmbELType.setValue("Current");
							}
						}
						else
						{
							cmbELType.setValue(null);
							cmbELType.setEnabled(false);
						}
					}
					
				}
			}
		});

		dLeaveFrom.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				tableClear();
				selectLeaveDays();

				if(cmbLeaveType.getValue()!=null && isFind==false)
				{
					if(cmbLeaveType.getItemCaption(cmbLeaveType.getValue()).equals("Annual Leave"))
					{						
						cmbELType.setEnabled(true);
						int iELeave=0,iDuration;
						iELeave=Integer.parseInt(txtAnualLeave.getValue()+"");
						iDuration=Integer.parseInt(txtDuration.getValue()+"");
						
						if(iELeave<iDuration)
						{
							cmbELType.setValue("Advance");
						}
						else
						{
							cmbELType.setValue("Current");
						}
					}
					else
					{
						cmbELType.setValue(null);
						cmbELType.setEnabled(false);
					}
				}
			}
		});

		dLeaveTo.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				tableClear();
				System.out.println("dLeaveTo");
				selectLeaveDays();

				if(cmbLeaveType.getValue()!=null && isFind==false)
				{
					if(cmbLeaveType.getItemCaption(cmbLeaveType.getValue()).equals("Annual Leave"))
					{						
						cmbELType.setEnabled(true);
						int iELeave=0,iDuration;
						iELeave=Integer.parseInt(txtAnualLeave.getValue()+"");
						iDuration=Integer.parseInt(txtDuration.getValue()+"");
						
						if(iELeave<iDuration)
						{
							cmbELType.setValue("Advance");
						}
						else
						{
							cmbELType.setValue("Current");
						}
					}
					else
					{
						cmbELType.setValue(null);
						cmbELType.setEnabled(false);
					}
				}
			}
		});

		ogPaymentType.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				LeaveTypeChange();
				if(dLeaveFrom.getValue()!=null && dLeaveTo.getValue()!=null)
				{
					try
					{
						findDayDiffernce(); //dateDayAddTable();
					}
					catch (Exception e)
					{/*System.out.println("Error "+e);*/}
				}
			}
		});

		cButton.btnPreview.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(cmbEmployeeName.getValue()!=null)
				{
					reportPreview();
				}
				else
				{
					showNotification("Warning!","No data to preview.",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		chkAllSection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmployeeName.removeAllItems();
				if(chkAllSection.booleanValue())
				{
					cmbSection.setValue(null);
					cmbSection.setEnabled(false);

					cmbEmployeeNameDataAdd();
				}
				else
				{
					cmbSection.setEnabled(true);
				}
			}
		});
		txtMobileNo.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtMobileNo.toString().isEmpty())					
				{
					String MobileNumber=txtMobileNo.getValue().toString();
					
					int Lentgh=MobileNumber.length();
					if(Lentgh<11){
						
						showNotification("Mobile Number Must be  Give Eleven Digit !!");
						txtMobileNo.setValue("");
					}
				}
			
			}
		});

	}

	private void LeaveTypeChange()
	{
		//"Without Pay","Special"
		
		dLeaveFrom.setValue(new Date());
		dLeaveTo.setValue(null);
		tableClear();
		if(ogPaymentType.getValue()=="With Pay")
		{
			cmbLeaveType.setVisible(true);
			txtLeaveType.setValue("");
			txtLeaveType.setVisible(false);
		}
		else if(ogPaymentType.getValue()=="Without Pay")
		{
			cmbLeaveType.setValue(null);
			cmbLeaveType.setVisible(false);
			txtLeaveType.setValue("Without Pay");
			txtLeaveType.setVisible(true);
		}
		/*else if(ogPaymentType.getValue()=="Special")
		{
			cmbLeaveType.setValue(null);
			cmbLeaveType.setVisible(false);
			txtLeaveType.setValue("Special");
			txtLeaveType.setVisible(true);
		}*/
	}
	private void cmbUnitDataAdd()
	{
		cmbUnit.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			
			String query="select distinct vUnitId,vUnitName from tbEmpOfficialPersonalInfo where bStatus=1  order by vUnitName";

			
			System.out.println("unit"+query);
			
			List <?> lst=session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				for(Iterator <?> itr=lst.iterator();itr.hasNext();)
				{
					Object [] element=(Object[])itr.next();
					cmbUnit.addItem(element[0]);
					cmbUnit.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch (Exception exp)
		{
			showNotification("cmbUnitDataAdd", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void cmbDepartmentDataAdd()
	{
	    cmbDepartment.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vDepartmentId,vDepartmentName from tbEmpOfficialPersonalInfo where bStatus=1 and vUnitId like '"+(cmbUnit.getValue()==null?"%":cmbUnit.getValue())+"' order by vDepartmentName";
		
			System.out.println("section"+query);
			
			List <?> lst=session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				for(Iterator <?> itr=lst.iterator();itr.hasNext();)
				{
					Object [] element=(Object[])itr.next();
					cmbDepartment.addItem(element[0]);
					cmbDepartment.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch (Exception exp)
		{
			showNotification("cmbSectionDataAdd", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void cmbSectionDataAdd()
	{
	    cmbSection.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			
			String query="select distinct vSectionId,vSectionName from tbEmpOfficialPersonalInfo where vUnitId like '"+(cmbUnit.getValue()==null?"%":cmbUnit.getValue())+"' and vDepartmentId like '"+(cmbDepartment.getValue()==null?"%":cmbDepartment.getValue())+"' and  bStatus=1 order by vSectionName";
		
			System.out.println("section"+query);
			
			List <?> lst=session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				for(Iterator <?> itr=lst.iterator();itr.hasNext();)
				{
					Object [] element=(Object[])itr.next();
					cmbSection.addItem(element[0]);
					cmbSection.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch (Exception exp)
		{
			showNotification("cmbSectionDataAdd", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void cmbLeaveTypeAdd()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select vLeaveTypeId,vLeaveTypeName from tbLeaveType order by iAutoId";
		
			
			List <?> lst=session.createSQLQuery(query).list();

			
			if(!lst.isEmpty())
			{
				for(Iterator <?> itr=lst.iterator();itr.hasNext();)
				{
					Object [] element=(Object[])itr.next();
					cmbLeaveType.addItem(element[0]);
					cmbLeaveType.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch (Exception exp)
		{
			showNotification("cmbLeaveTypeAdd", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void employeeData()
	{
		if(checkPendingLeave() || isFind)
		{
			cmbEmployeeRelatedData();
		}
		else
		{
			if(cmbEmployeeName.getValue()!=null)
			{
				showNotification("Warning!",""+(cmbEmployeeName.getItemCaption(cmbEmployeeName.getValue()).toString())+"" +
						" has a pending leave.",Notification.TYPE_WARNING_MESSAGE);
				cmbEmployeeName.setValue(null);
			}
		}
	}

	private boolean checkPendingLeave()
	{
		boolean ret = false;

		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try 
		{
			String query = "select * from tbEmpLeaveApplicationInfo where vEmployeeId =" +
					" '"+(cmbEmployeeName.getValue()==null?"":cmbEmployeeName.getValue().toString().toString())+"'" +
					" and iApprovedFlag=0";

			Iterator <?> iter = session.createSQLQuery(query).list().iterator();
			if(!iter.hasNext() || isFind)
			{
				ret = true;
			}
		}
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		finally{session.close();}

		return ret;
	}

	private void updateButtonAction()
	{
		if(countLeave==0)
		{
			isUpdate = true;
			isFind = false;

			txtInit(false);
			btnIni(false);
		}
		else
		{
			showNotification("Warning!","Leave is approved, try new one or Delete",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void getLeaveId()
	{
		if(!isFind)
		{
			txtReferenceNo.setValue(selectMaxId());
		}
	}

	private void findButtonEvent() 
	{
		Window win = new LeaveApplicationFormFind(sessionBean, txtFindId, "SALARY REGISTER");
		win.addListener(new Window.CloseListener()
		{
			public void windowClose(CloseEvent e) 
			{
				if(txtFindId.getValue().toString().length()>0)
				{
					findInitialize(txtFindId.getValue().toString());
					chkAllSection.setValue(true);
					cmbSection.setEnabled(false);
				}
			}
		});
		this.getParent().addWindow(win);
	}

	private void findInitialize(String findId)
	{
		countLeave=0;
		Session session = SessionFactoryUtil.getInstance().openSession();
		try
		{
			String findQuery = "select ela.dApplicationDate,epo.vUnitId,epo.vUnitName,epo.vDepartmentId,epo.vDepartmentName,"+
			" epo.vSectionId,epo.vSectionName,epo.vEmployeeId,ela.vLeaveId,ela.vPaymentFlag,ela.vLeaveTypeId,"+
			" ela.vLeaveType,ela.dLeaveFrom,ela.dLeaveTo,ela.vLeavePupose,ela.vLeaveAddress,"+
			" ela.vMobileNo,CONVERT(decimal(18,0),ela.mTotalDays)mTotalDays,CONVERT(decimal(18,0),ela.mFridays)mFridays,"+
			" iPrimary,iFinal,iApprovedFlag from tbEmpLeaveApplicationInfo ela"+
			" inner join tbEmpOfficialPersonalInfo epo on epo.vEmployeeId=ela.vEmployeeId"+
			" where ela.vLeaveId='"+findId+"'";

			System.out.println("Find Leave :" +findQuery);			
			List <?> list = session.createSQLQuery(findQuery).list();
			
			if(!list.isEmpty())
			{
				Iterator<?> iter=list.iterator();
				if(iter.hasNext())
				{
					Object[] element=(Object[])iter.next();
					dApplicationDate.setValue(element[0]);
					cmbUnit.setValue(element[1]);
					cmbDepartment.setValue(element[3]);
					cmbSection.setValue(element[5]);
					cmbEmployeeName.setValue(element[7]);
					txtReferenceNo.setValue(element[8]);
					ogPaymentType.setValue(element[9]);
					cmbLeaveType.setValue(element[10]);
					dLeaveFrom.setValue(element[12]);
					dLeaveTo.setValue(element[13]);
					txtPurposeOfLeave.setValue(element[14]);
					txtLeaveAddress.setValue(element[15]);
					txtMobileNo.setValue(element[16]);
					txtDuration.setValue(element[17]);
					txtFriday.setValue(element[18]);
			
					if(element[19].equals(1)){countLeave++;}
					if(element[20].equals(1)){countLeave++;}
					if(element[21].equals(2)){countLeave++;}
					
					dLeaveTo.setEnabled(false);
				}
			}
			
			String details="select ed.vEmployeeName,ed.dLeaveDate,eli.mTotalDays,ed.vAdjustedType,eli.dApplicationDate from tbEmpLeaveApplicationDetails ed "+
			" inner join tbEmpLeaveApplicationInfo eli on eli.vLeaveId=ed.vLeaveId "+
			" where eli.vLeaveId='"+findId+"'";
			
			Iterator<?> iter=session.createSQLQuery(details).list().iterator();
			int i=0;
			while(iter.hasNext())
			{
				Object[] element=(Object[])iter.next();
				tbllblEmployeeName.get(i).setValue(element[0]);
				tbdDate.get(i).setValue(sessionBean.dfBd.format(element[1]));
				tbllblWeekDay.get(i).setValue(element[2]);
				tblAdjustedType.get(i).setValue(element[3]);
				tbdEntryDate.get(i).setValue(sessionBean.dfBd.format(element[4]));
				
				if(i==tbllblEmployeeName.size()-1)
				{
					tableRowAdd(i+1);
				}
				i++;
			}
		}
		catch (Exception ex)
		{
			showNotification("Warning", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void formValidation()
	{
		if(cmbEmployeeName.getValue()!=null)
		{
			if(cmbLeaveType.getValue()!=null || !txtLeaveType.getValue().toString().trim().isEmpty())
			{
				if(!txtPurposeOfLeave.getValue().toString().isEmpty())
				{
					if(!txtDuration.getValue().toString().isEmpty())
					{
						if(!txtMobileNo.getValue().toString().isEmpty())
						{									
						   if(checkExistingLeave() || isUpdate)
						    {
							   String query = "select vEmployeeID from tbMonthlySalary " +
										"where vEmployeeID='"+cmbEmployeeName.getValue()+"' " +
										"and ((MONTH(dSalaryDate) = '"+dbMonthFormat.format(dLeaveFrom.getValue())+"' and YEAR(dSalaryDate) = '"+dbYearFormat.format(dLeaveFrom.getValue())+"') " +
										"or (MONTH(dSalaryDate) = '"+dbMonthFormat.format(dLeaveTo.getValue())+"' and YEAR(dSalaryDate) = '"+dbYearFormat.format(dLeaveTo.getValue())+"'))";
								System.out.println("Check Salary: "+query);
								
								if(!chkSalary(query))
								{
								    saveButtonAction();
								}
								else
								{
									showNotification("Warning", "Salary Already Generated for this Month!!!", Notification.TYPE_WARNING_MESSAGE);
								}
					        }
							else
							{
								showNotification("Warning!",""+cmbEmployeeName.getItemCaption(cmbEmployeeName.getValue()).
										toString()+" is already applied for leave during these days - "+existLeaveDays,Notification.TYPE_WARNING_MESSAGE);
							}
					}
					else
				     {
					
				     	showNotification("Warning!","Select Eleven Digit Mobile Number Please !.",Notification.TYPE_WARNING_MESSAGE);
							
				     } 
				   
					}
					else
					{
						showNotification("Warning!","Select duration of leave.",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					txtPurposeOfLeave.focus();
					showNotification("Warning!","Provide purpose of leave.",Notification.TYPE_WARNING_MESSAGE);
				}
			}
			else
			{
				cmbLeaveType.focus();
				showNotification("Warning!","Select leave type.",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else
		{
			cmbEmployeeName.focus();
			showNotification("Warning!","Select employee name.",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private boolean checkExistingLeave()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();

		String leaveDays = "";
		existLeaveDays = "";

		try
		{
			String query = "Select dLeaveDate,0 from tbEmpLeaveApplicationDetails where  vEmployeeId =" +
					" '"+cmbEmployeeName.getValue().toString()+"' and dLeaveDate between" +
					" '"+sessionBean.dfDb.format(dLeaveFrom.getValue())+"' and" +
					" '"+sessionBean.dfDb.format(dLeaveTo.getValue())+"' order by dLeaveDate";
		
			System.out.println("existLeave"+query);
			
			
			List <?> lst=session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				for(Iterator <?> itr=lst.iterator(); itr.hasNext();)
				{
					Object [] element = (Object[])itr.next();
					leaveDays = leaveDays + sessionBean.dfBd.format(element[0]).toString() + ", ";
				}
				existLeaveDays = leaveDays.substring(0, leaveDays.length()-2);
				return false;
			}
		}
		catch (Exception exp)
		{
			showNotification("cmbSectionDataAdd", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}

		return true;
	}

	private String selectMaxId()
	{
	
		String maxId = "";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "Select isnull(max(cast(SUBSTRING(vLeaveId,4,LEN(vLeaveId)) as int)),0)+1 from tbEmpLeaveApplicationInfo ";
		
			Iterator <?> iter = session.createSQLQuery(query).list().iterator();
			if (iter.hasNext())
			{
				String srt = iter.next().toString();
				maxId = dfReference.format(dApplicationDate.getValue())+"-"+srt;
			}
		}
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		finally{session.close();}
		return maxId;
	}

	
	private void cmbEmployeeNameDataAdd()
	{
		cmbEmployeeName.removeAllItems();

		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			
			String query ="select vEmployeeId,vFingerId,vEmployeeCode,vEmployeeName,vUnitId,vUnitName from tbEmpOfficialPersonalInfo "
					+ " where vUnitId ='"+(cmbUnit.getValue()==null?"%":cmbUnit.getValue())+"' and vSectionId like "
			        + " '"+(cmbSection.getValue()==null?"%":cmbSection.getValue())+"' and vDepartmentId like '"+(chkDepartmentAll.booleanValue()==true?"%":cmbDepartment.getValue())+"' "
			        + " and bStatus = 1 order by vEmployeeName";

		
			System.out.println("employeeNameAdd"+query);
			
			List <?> lst = session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				if(ogSelectEmployee.getValue().toString().equals("Finger ID"))
				{
					for(Iterator <?> itr=lst.iterator();itr.hasNext();)
					{
						Object [] element=(Object[])itr.next();
						lblFingerId.setValue("Employee Name : ");
						txtFingerId.setWidth("250px");
						lblEmployeeName.setValue("Finger ID : ");
						cmbEmployeeName.setWidth("140px");
						lblProximityId.setValue("Employee ID : ");
						txtProximityId.setWidth("140px");
						cmbEmployeeName.addItem(element[0].toString());
						cmbEmployeeName.setItemCaption(element[0].toString(), element[1].toString());
					}
				}
				if(ogSelectEmployee.getValue().toString().equals("Employee ID"))
				{
					for(Iterator <?> itr=lst.iterator();itr.hasNext();)
					{
						Object [] element=(Object[])itr.next();
						lblFingerId.setValue("Employee Name : ");
						txtFingerId.setWidth("250px");
						lblEmployeeName.setValue("Employee ID : ");
						cmbEmployeeName.setWidth("140px");
						lblProximityId.setValue("Finger ID : ");
						txtProximityId.setWidth("140px");
						cmbEmployeeName.addItem(element[0].toString());
						cmbEmployeeName.setItemCaption(element[0].toString(), element[2].toString());
					}
				}
				if(ogSelectEmployee.getValue().toString().equals("Employee Name"))
				{
					for(Iterator <?> itr=lst.iterator();itr.hasNext();)
					{
						Object [] element=(Object[])itr.next();
						lblFingerId.setValue("Finger ID : ");
						txtFingerId.setWidth("140px");
						lblEmployeeName.setValue("Employee Name : ");
						cmbEmployeeName.setWidth("250px");
						lblProximityId.setValue("Employee ID : ");
						txtProximityId.setWidth("140px");
						cmbEmployeeName.addItem(element[0].toString()); 
						cmbEmployeeName.setItemCaption(element[0].toString(), element[3].toString());
					}
				}
			}
		}
		catch (Exception exp)
		{
			showNotification("cmbEmployeeNameDataAdd", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	
	}

	private void cmbEmployeeRelatedData()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = " select vEmployeeId,vEmployeeName,vFingerId,vDesignation,dJoiningDate,vConfirmDate,iCasualLeaveBalance," +
					" iSickLeaveBalance,iEarnLeaveBalance,vDesignationId,vEmployeeType,vUnitId,vUnitName,vContactNo  from funLeaveBalanceDetails(" +
					" '%','"+(cmbEmployeeName.getValue()==null?"%":cmbEmployeeName.getValue())+"',"
					+ "'"+sessionBean.dfDb.format(dApplicationDate.getValue())+"')";

			System.out.println("empRelatedData "+query);
			
			List <?> lst=session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				for(Iterator <?> itr=lst.iterator();itr.hasNext();)
				{
					Object [] element=(Object[])itr.next();
					if(ogSelectEmployee.getValue()=="Employee ID")
					{
						txtFingerId.setValue(element[1]);
						txtProximityId.setValue(element[2]);
						txtDesignation.setValue(element[3]);
					}
					if(ogSelectEmployee.getValue()=="Finger ID")
					{
						txtFingerId.setValue(element[1]);
						txtProximityId.setValue(element[0]);
						txtDesignation.setValue(element[3]);
					}
					if(ogSelectEmployee.getValue()=="Employee Name")
					{
						txtFingerId.setValue(element[2]);
						txtProximityId.setValue(element[0]);
						txtDesignation.setValue(element[3]);
					}

					dJoiningDate.setReadOnly(false);
					dJoiningDate.setValue(element[4]);
					dJoiningDate.setReadOnly(true);

					if(!element[5].toString().equals(""))
					{
						dConfirmationDate.setReadOnly(false);
						Date date = sessionBean.dfBd.parse(element[5].toString());
						dConfirmationDate.setValue(date);
						dConfirmationDate.setReadOnly(true);
					}

					txtEmployeeType.setValue(element[10].toString());

					txtCasualLeave.setValue(element[6].toString());
					txtSickLeave.setValue(element[7].toString());
					txtAnualLeave.setValue(element[8].toString());

					designationId = element[9].toString();
					txtMobileNo.setValue(element[13]);
				}
			}
		}
		catch (Exception exp)
		{
			showNotification("cmbEmployeeRelatedData", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void tableInitialise()
	{
		for(int i=0;i<4;i++)
		{
			tableRowAdd(i);
		}
	}

	private void selectLeaveDays()
	{
		if(cmbEmployeeName.getValue()!=null)
		{
			if(dLeaveFrom.getValue()!=null && dLeaveTo.getValue()!=null)
			{
				if((cmbLeaveType.getValue()!=null) || !txtLeaveType.getValue().toString().trim().isEmpty())
				{
					System.out.println("Done");
					findDayDiffernce();
				}
				else
				{
					//dLeaveTo.setValue(null);
					showNotification("Warning!","Select leave type.",Notification.TYPE_WARNING_MESSAGE);
					cmbLeaveType.focus();
				}
			}
			else
			{

			}
		}
		else
		{
			//dLeaveTo.setValue(null);
			/*showNotification("Warning!","Select employee name.",Notification.TYPE_WARNING_MESSAGE);
			cmbEmployeeName.focus();*/
		}
	}

	private void findDayDiffernce()
	{
		if(!isFind)
		{

			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String applyDateFrom = sessionBean.dfDb.format(dLeaveFrom.getValue());
				String applyDateTo = sessionBean.dfDb.format(dLeaveTo.getValue());

				String query = "select 0 as c, DATEDIFF(DAY,'"+applyDateFrom+"','"+applyDateTo+"')+1 as day";

				List <?> list = session.createSQLQuery(query).list();

				if(list.iterator().hasNext())
				{
					Object[] element = (Object[]) list.iterator().next();
					int totalDays = Integer.parseInt(element[1].toString());
					if(ogPaymentType.getValue()=="With Pay")
					{
						System.out.println("findDifference");
						
						int checkLeaveBalance = 0;
						if(cmbLeaveType.getValue()!=null)
						{
							if (cmbLeaveType.getValue().toString().trim().equalsIgnoreCase("1"))
							{
								
								checkLeaveBalance = Integer.parseInt(txtCasualLeave.getValue().toString());
								if(totalDays<=checkLeaveBalance)
								{
									leaveValidation(totalDays,applyDateFrom,applyDateTo);
								}
								else
								{
									showNotification("Warning", "Leave Balance Exceeded!!", Notification.TYPE_WARNING_MESSAGE);
									dLeaveTo.setValue(null);
								}
							}

							else if(cmbLeaveType.getValue().toString().trim().equalsIgnoreCase("2"))
							{
								checkLeaveBalance = Integer.parseInt(txtSickLeave.getValue().toString());
								if(totalDays<=checkLeaveBalance)
								{
									leaveValidation(totalDays,applyDateFrom,applyDateTo);
								}
								else
								{
									showNotification("Warning", "Leave Balance Exceeded!!", Notification.TYPE_WARNING_MESSAGE);
									dLeaveTo.setValue(null);
								}
							}

							else if(cmbLeaveType.getValue().toString().trim().equalsIgnoreCase("3"))
							{
								checkLeaveBalance = Integer.parseInt(txtAnualLeave.getValue().toString());
								leaveValidation(totalDays,applyDateFrom,applyDateTo);
								
								//21-09-2019 Because the enjoy EL Before Confirmation
								/*if(totalDays<=checkLeaveBalance)
								{
									leaveValidation(totalDays,applyDateFrom,applyDateTo);
								}
								else
								{
									showNotification("Warning", "Leave Balance Exceeded!!", Notification.TYPE_WARNING_MESSAGE);
									dLeaveTo.setValue(null);
								}*/
							}
							
							else if(cmbLeaveType.getValue().toString().trim().equalsIgnoreCase("4"))
							{
								dateDayAddTable(totalDays);
								txtDuration.setValue(totalDays);
								int nmFriday = fridayCount(applyDateFrom, applyDateTo);
								txtFriday.setValue(nmFriday);
							}
						}
						else
						{
							showNotification("Warning", "Please Select Leave Type!!!", Notification.TYPE_WARNING_MESSAGE);
							dLeaveFrom.setValue(new Date());
							dLeaveTo.setValue(null);
						}
					}
					else
					{
						leaveValidation(totalDays,applyDateFrom,applyDateTo);
					}
				}
			}
			catch (Exception ex)
			{
				this.getParent().showNotification("findDayDiffernce", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		
		}
	}

	private void leaveValidation(int totalDays, String applyDateFrom, String applyDateTo)
	{
		if(!isFind)
		{
			if(totalDays <= 0)
			{
				txtDuration.setValue("");
				txtFriday.setValue("");
				dLeaveTo.setValue(null);
				tableClear();
				getParent().showNotification("Warning!","Select valid date range.", Notification.TYPE_WARNING_MESSAGE);
			}
			else if(totalDays>0)
			{
				System.out.println("Done");
				int nmFriday = fridayCount(applyDateFrom, applyDateTo);
				txtDuration.setValue(totalDays);
				txtFriday.setValue(nmFriday);

				dateDayAddTable(totalDays);
			}
			else
			{
				dLeaveTo.setValue(null);
				txtDuration.setValue("");
				txtFriday.setValue("");
				if(dLeaveFrom.getValue()!=null && dLeaveTo.getValue()!=null)
				{
					getParent().showNotification("Warning!","Leave From Date must be less than Leave To Date.",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		}
	}

	private int fridayCount(String fromDate, String toDate)
	{
		int numFriday = 0;
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();

		try
		{
			String query = "Select 0 as nf, datediff(day, -3, '"+toDate+"')/7-datediff(day, -2, '"+fromDate+"')/7 as NF";
			List <?> list = session.createSQLQuery(query).list();

			if(list.iterator().hasNext())
			{
				Object[] element = (Object[]) list.iterator().next();
				numFriday = Integer.parseInt(element[1].toString());
			}
		}
		catch (Exception ex)
		{
			this.getParent().showNotification("fridayCount", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
		return numFriday;  
	}

	private void dateDayAddTable(int totalDays)
	{
		List<String> dates = new ArrayList<String>();
		List<String> entryDates = new ArrayList<String>();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String start_Day = new SimpleDateFormat("yyyy-MM-dd").format(dLeaveFrom.getValue());

			int i=0;
			while (i<totalDays)
			{
				Object tbDateQuery = (Object)session.createSQLQuery("select CONVERT(date,DATEADD(dd,"+i+",'"+start_Day+"'))").list().iterator().next();
				dates.add(sessionBean.dfBd.format(tbDateQuery));
				entryDates.add(sessionBean.dfDb.format(tbDateQuery));
				i++;
			}
		}
		catch (Exception ex)
		{
			this.getParent().showNotification("findDateList",ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}

		tableClear();
		if(dates.size()!=0)
		{
			for(int ind=0; ind<dates.size(); ind++)
			{
				if((ind)==tbdDate.size())
				{
					tableRowAdd(ind);
				}

				String date = dates.get(ind);
				System.out.println("findDayName1 : "+date.toString());
				String dna = findDayName(date.toString());
				String empName = "";
				if(ogSelectEmployee.getValue()=="Employee ID")
				{
					empName = txtFingerId.getValue().toString().trim();
				}
				if(ogSelectEmployee.getValue()=="Finger ID")
				{
					empName=txtFingerId.getValue().toString().trim();
				}
				if(ogSelectEmployee.getValue()=="Employee Name")
				{
					empName=cmbEmployeeName.getItemCaption(cmbEmployeeName.getValue());
				}

				tbllblEmployeeName.get(ind).setValue(empName);

				tbdDate.get(ind).setValue(date);
				tbdEntryDate.get(ind).setValue(entryDates.get(ind));

				tbllblWeekDay.get(ind).setValue(dna);

				if(ogPaymentType.getValue().toString().equals("With Pay"))
				{
					tblAdjustedType.get(ind).setValue("Leave");
				}
				else if(ogPaymentType.getValue().toString().equals("Without Pay"))
				{
					tblAdjustedType.get(ind).setValue("Absent");
				}
				else if(ogPaymentType.getValue().toString().equals("Special"))
				{
					tblAdjustedType.get(ind).setValue("Leave");
				}
			}
		}
	}

	private String findDayName(String sDate)
	{
		String finalDay = "";
		try
		{
			String inputDate = sDate;
			SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
			Date dt1 = format1.parse(inputDate);
			DateFormat format2 = new SimpleDateFormat("EEEE");
			finalDay = format2.format(dt1);
		}
		catch (Exception ex)
		{
			this.getParent().showNotification("findDayName", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		return finalDay;
	}

	private void saveButtonAction()
	{
		String query = "select vEmployeeID from tbMonthlySalary " +
				"where vEmployeeID='"+cmbEmployeeName.getValue()+"' " +
				"and ((MONTH(dSalaryDate) = '"+dbMonthFormat.format(dLeaveFrom.getValue())+"' and YEAR(dSalaryDate) = '"+dbYearFormat.format(dLeaveFrom.getValue())+"') " +
				"or (MONTH(dSalaryDate) = '"+dbMonthFormat.format(dLeaveTo.getValue())+"' and YEAR(dSalaryDate) = '"+dbYearFormat.format(dLeaveTo.getValue())+"'))";
		System.out.println("Check Salary: "+query);
		
		if(!chkSalary(query))
		{
			if(isUpdate)
			{
				MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to update information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
				mb.show(new EventListener()
				{
					public void buttonClicked(ButtonType buttonType)
					{
						if(buttonType == ButtonType.YES)
						{
							if(isDelete())
							{
								insertData();
							}

							txtInit(true);
							btnIni(true);

							isUpdate = false;

							Notification n=new Notification("All Information Updated Successfully!","",Notification.TYPE_TRAY_NOTIFICATION);
							n.setPosition(Notification.POSITION_TOP_RIGHT);
							showNotification(n);
						}
					}
				});
			}
			else
			{
				MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to save information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
				mb.show(new EventListener()
				{
					public void buttonClicked(ButtonType buttonType)
					{
						if(buttonType == ButtonType.YES)
						{
							insertData();

							txtInit(true);
							btnIni(true);

							isUpdate = false;
						
							Notification n=new Notification("All Information Save Successfully!","",Notification.TYPE_TRAY_NOTIFICATION);
							n.setPosition(Notification.POSITION_TOP_RIGHT);
							showNotification(n);
						}
					}
				});
			}
		}
		else
		{
			showNotification("Warning", "Salary Already Generated for this Month!!!", Notification.TYPE_WARNING_MESSAGE);
		}
		
	}

	private boolean isDelete()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		String leaveId = txtReferenceNo.getValue().toString();
		try
		{
			String empName = "";
			if(ogSelectEmployee.getValue()=="Employee ID")
			{
				empName = txtFingerId.getValue().toString().trim();
			}
			if(ogSelectEmployee.getValue()=="Finger ID")
			{
				empName=txtFingerId.getValue().toString().trim();
			}
			if(ogSelectEmployee.getValue()=="Employee Name")
			{
				empName=cmbEmployeeName.getItemCaption(cmbEmployeeName.getValue());
			}

			String InsertUpdate = "INSERT into tbUdEmpLeaveApplicationInfo (vLeaveId,dApplicationDate,vUnitId,vUnitName,vSectionId,vSectionName," +
					" vDesignationId,vDesignation,vEmployeeType,vEmployeeId,vEmployeeName,vLeaveTypeId,vLeaveType,dLeaveFrom,dLeaveTo," +
					" vLeavePupose,vLeaveAddress,dSanctionFrom,dSanctionTo,mTotalDays,mFridays,mAdjustDays,vPaymentFlag,iApprovedFlag,"+
					" vApprovedBy,vMobileNo,vUdFlag,vUserName,vUserIp,dEntryTime) values (" +
					" '"+leaveId+"'," +
					" '"+sessionBean.dfDb.format(dApplicationDate.getValue())+"'," +
					" '"+cmbUnit.getValue().toString()+"'," +
					" '"+(cmbUnit.getItemCaption(cmbUnit.getValue()).toString())+"'," +
					" '"+cmbSection.getValue().toString()+"'," +
					" '"+(cmbSection.getItemCaption(cmbSection.getValue()).toString())+"'," +
					" '"+designationId+"'," +
					" '"+txtDesignation.getValue().toString()+"'," +
					" '"+txtEmployeeType.getValue().toString()+"'," +
					" '"+cmbEmployeeName.getValue().toString()+"'," +
					" '"+empName+"'," +
					" '"+(cmbLeaveType.getValue()!=null?cmbLeaveType.getValue().toString():txtLeaveType.getValue().toString())+"'," +
					" '"+(cmbLeaveType.getValue()!=null?cmbLeaveType.getItemCaption(cmbLeaveType.getValue()).toString():txtLeaveType.getValue().toString())+"'," +
					" '"+sessionBean.dfDb.format(dLeaveFrom.getValue())+"'," +
					" '"+sessionBean.dfDb.format(dLeaveTo.getValue())+"'," +
					" '"+(txtPurposeOfLeave.getValue().toString().isEmpty()?"":txtPurposeOfLeave.getValue().toString())+"'," +
					" '"+(txtLeaveAddress.getValue().toString().isEmpty()?"":txtLeaveAddress.getValue().toString())+"'," +
					" '"+sessionBean.dfDb.format(dLeaveFrom.getValue())+"'," +
					" '"+sessionBean.dfDb.format(dLeaveTo.getValue())+"'," +
					" '"+txtDuration.getValue().toString()+"'," +
					" '"+(txtFriday.getValue().toString().isEmpty()?"0":txtFriday.getValue().toString())+"'," +
					" '0'," +
					" '"+ogPaymentType.getValue().toString()+"'," +
					" '0'," +
					" ''," +
					" '"+(txtMobileNo.getValue().toString().isEmpty()?"0":txtMobileNo.getValue().toString())+"'," +
					" 'DELETE'," +
					" '"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP) ";
			session.createSQLQuery(InsertUpdate).executeUpdate();

			// delete data from main table to insert update data
			String deleteInfo = "delete from tbEmpLeaveApplicationInfo where vLeaveId = '"+leaveId+"'";
			session.createSQLQuery(deleteInfo).executeUpdate();
			String deleteDetails = "delete from tbEmpLeaveApplicationDetails where vLeaveId = '"+leaveId+"'";
			session.createSQLQuery(deleteDetails).executeUpdate();
			tx.commit();
		}
		catch (Exception e)
		{
			tx.rollback();
			showNotification("isDelete "+e,"",Notification.TYPE_WARNING_MESSAGE);
			return false;
		}
		finally{session.close();}
		return true;
	}

	private void insertData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		String leaveId = "";
		if(!isUpdate)
		{
			leaveId = selectMaxId();
		}
		else
		{
			leaveId = txtReferenceNo.getValue().toString();
		}

		try
		{
			String Insert = "INSERT into tbEmpLeaveApplicationInfo (vLeaveId,dApplicationDate,vSectionId,"+
					" vSectionName,vDesignationId,vDesignation,vEmployeeType,vEmployeeId,vEmployeeName,vLeaveTypeId,vLeaveType,dLeaveFrom,"+
					" dLeaveTo,vLeavePupose,vLeaveAddress,dSanctionFrom,dSanctionTo,mTotalDays,mFridays,mAdjustDays,vPaymentFlag,iApprovedFlag,"+
					" vApprovedBy,vMobileNo,vUserName,vUserIp,dEntryTime,vUnitId,vUnitName,iPrimary,iFinal,iHR) values (" +
					" '"+leaveId+"'," +
					" '"+sessionBean.dfDb.format(dApplicationDate.getValue())+"'," +
			     	" ''," +
					" ''," +
					" '"+designationId+"'," +
					" '"+txtDesignation.getValue().toString()+"'," +
					" '"+txtEmployeeType.getValue().toString()+"'," +
					" '"+cmbEmployeeName.getValue().toString()+"'," +
					" (select vEmployeeName from tbEmpOfficialPersonalInfo where vEmployeeId = '"+cmbEmployeeName.getValue().toString()+"')," +
					" '"+(cmbLeaveType.getValue()!=null?cmbLeaveType.getValue().toString():txtLeaveType.getValue().toString())+"'," +
					" '"+(cmbLeaveType.getValue()!=null?cmbLeaveType.getItemCaption(cmbLeaveType.getValue()).toString():txtLeaveType.getValue().toString())+"'," +
					" '"+sessionBean.dfDb.format(dLeaveFrom.getValue())+"'," +
					" '"+sessionBean.dfDb.format(dLeaveTo.getValue())+"'," +
					" '"+(txtPurposeOfLeave.getValue().toString().isEmpty()?"":txtPurposeOfLeave.getValue().toString())+"'," +
					" '"+(txtLeaveAddress.getValue().toString().isEmpty()?"":txtLeaveAddress.getValue().toString())+"'," +
					" '"+sessionBean.dfDb.format(dLeaveFrom.getValue())+"'," +
					" '"+sessionBean.dfDb.format(dLeaveTo.getValue())+"'," +
					" '"+txtDuration.getValue().toString()+"'," +
					" '"+(txtFriday.getValue().toString().isEmpty()?"0":txtFriday.getValue().toString())+"'," +
					" '0'," +
					" '"+ogPaymentType.getValue().toString()+"'," +
					" '0'," +
					" ''," +
					" '"+(txtMobileNo.getValue().toString().isEmpty()?"0":txtMobileNo.getValue().toString())+"'," +
					" '"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, "+
					" '','',0,0,0)" ;

			System.out.println("inser11-1"+Insert);
			
			session.createSQLQuery(Insert).executeUpdate();
			for(int i = 0; i<tbllblWeekDay.size(); i++)
			{
				if(tbdDate.get(i).getValue()!=null)
				{
					String InsertTable = "INSERT into tbEmpLeaveApplicationDetails (vLeaveId,vLeaveTypeId,vLeaveTypeName," +
							" vEmployeeId,vEmployeeName,vUnitId,vUnitName,dLeaveDate,vPaymentFlag,iApprovedFlag,vAdjustedType,vAuthorisedBy," +
							" vRemarks,vCancelBy,vPcIp,iPrimary,iFinal,iHR) values (" +
							" '"+leaveId+"'," +
							" '"+(cmbLeaveType.getValue()!=null?cmbLeaveType.getValue().toString():txtLeaveType.getValue().toString())+"'," +
							" '"+(cmbLeaveType.getValue()!=null?cmbLeaveType.getItemCaption(cmbLeaveType.getValue()).toString():txtLeaveType.getValue().toString())+"'," +
							" '"+cmbEmployeeName.getValue().toString()+"'," +
							" (select vEmployeeName from tbEmpOfficialPersonalInfo where vEmployeeId = '"+cmbEmployeeName.getValue().toString()+"')," +
							" ''," +
							" ''," +
							" '"+tbdEntryDate.get(i).getValue()+"'," +
							" '"+ogPaymentType.getValue().toString()+"'," +
							" '0'," +
							" '"+tblAdjustedType.get(i).getValue().toString()+"'," +
							" '','','','',0,0,0) ";
					session.createSQLQuery(InsertTable).executeUpdate();
					
					System.out.println("insertt2"+InsertTable);
				}
			}

			tx.commit();
		}
		catch (Exception e)
		{
			tx.rollback();
			showNotification("Warning!","Error to save application "+e,Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}

private void tableRowAdd( final int ar)
	{
		tbllblSerial.add(ar,new Label());
		tbllblSerial.get(ar).setWidth("100%");
		tbllblSerial.get(ar).setValue(ar+1);

		tbllblEmployeeName.add(ar,new Label());
		tbllblEmployeeName.get(ar).setWidth("100%");
		tbllblEmployeeName.get(ar).setImmediate(true);

		tbdDate.add(ar,new Label());
		tbdDate.get(ar).setImmediate(true);
		tbdDate.get(ar).setWidth("100%");

		tbdEntryDate.add(ar,new Label());
		tbdEntryDate.get(ar).setImmediate(true);
		tbdEntryDate.get(ar).setWidth("100%");

		tbllblWeekDay.add(ar,new Label());
		tbllblWeekDay.get(ar).setWidth("100%");

		tblAdjustedType.add(ar,new Label());
		tblAdjustedType.get(ar).setWidth("100%");
		tblAdjustedType.get(ar).setImmediate(true);

		table.addItem(new Object[]{tbllblSerial.get(ar),tbllblEmployeeName.get(ar),tbdDate.get(ar),tbdEntryDate.get(ar),
		tbllblWeekDay.get(ar),tblAdjustedType.get(ar)},ar);
		
	}

	private AbsoluteLayout buildMainLayout() 
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("780.0px");
		mainLayout.setHeight("620.0px");
		mainLayout.setMargin(false);

		lblappdate = new Label("Application Date :");
		lblappdate.setWidth("-1px");
		lblappdate.setHeight("-1px");
		mainLayout.addComponent(lblappdate,"top:15.0px;left:30.0px");

		dApplicationDate = new PopupDateField();
		dApplicationDate.setImmediate(true);
		dApplicationDate.setWidth("110px");
		dApplicationDate.setHeight("-1px");
		dApplicationDate.setDateFormat("dd-MM-yyyy");
		dApplicationDate.setValue(new java.util.Date());
		dApplicationDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dApplicationDate,"top:13.0px;left:140.0px");

		lblUnit = new Label("Project Name :");
		lblUnit.setWidth("-1px");
		lblUnit.setHeight("-1px");
		mainLayout.addComponent(lblUnit,"top:42.0px;left:30.0px");

		cmbUnit = new ComboBox();
		cmbUnit.setImmediate(true);
		cmbUnit.setWidth("220px");
		cmbUnit.setHeight("-1px");
		mainLayout.addComponent(cmbUnit,"top:40.0px;left:140.0px");

		
		mainLayout.addComponent(new Label("Department :"),"top:70.0px;left:30.0px");

		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("220px");
		cmbDepartment.setHeight("-1px");
		mainLayout.addComponent(cmbDepartment,"top:68.0px;left:140.0px");

		chkDepartmentAll = new CheckBox("All");
		chkDepartmentAll.setImmediate(true);
		chkDepartmentAll.setHeight("-1px");
		chkDepartmentAll.setWidth("-1px");
		mainLayout.addComponent(chkDepartmentAll, "top:70.0px;left:365.0px");
		
		lblSection = new Label("Section Name :");
		lblSection.setWidth("-1px");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection,"top:100px;left:30.0px");
		
		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("220px");
		cmbSection.setHeight("-1px");
		mainLayout.addComponent(cmbSection,"top:98px;left:140.0px");

		chkAllSection = new CheckBox("All");
		chkAllSection.setImmediate(true);
		chkAllSection.setHeight("-1px");
		chkAllSection.setWidth("-1px");
		mainLayout.addComponent(chkAllSection, "top:100px;left:365.0px");

		ogSelectEmployee = new OptionGroup("",lst);
		ogSelectEmployee.setImmediate(true);
		ogSelectEmployee.setStyleName("horizontal");
		ogSelectEmployee.select("Employee ID");
		mainLayout.addComponent(ogSelectEmployee, "top:125px;left:25.0px;");

		lblEmployeeName = new Label("Employee Name :");
		lblEmployeeName.setImmediate(false);
		lblEmployeeName.setWidth("-1px");
		lblEmployeeName.setHeight("-1px");
		mainLayout.addComponent(lblEmployeeName, "top:150px; left:30.0px;");

		cmbEmployeeName = new ComboBox();
		cmbEmployeeName.setImmediate(true);
		cmbEmployeeName.setWidth("250px");
		cmbEmployeeName.setHeight("-1px");
		cmbEmployeeName.setNullSelectionAllowed(true);
		cmbEmployeeName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbEmployeeName, "top:148px; left:140.0px;");

		lblFingerId = new Label("Finger Id :");
		lblFingerId.setImmediate(false);
		lblFingerId.setWidth("-1px");
		lblFingerId.setHeight("-1px");
		mainLayout.addComponent(lblFingerId, "top:175px;left:30.0px;");

		txtFingerId = new TextRead();
		txtFingerId.setImmediate(false);
		txtFingerId.setWidth("140.0px");
		txtFingerId.setHeight("22px");
		mainLayout.addComponent(txtFingerId, "top:173px;left:140.0px;");

		lblProximityId = new Label("Employee ID :");
		lblProximityId.setImmediate(false);
		lblProximityId.setWidth("-1px");
		lblProximityId.setHeight("-1px");
		mainLayout.addComponent(lblProximityId, "top:200px; left:30.0px;");

		txtProximityId = new TextRead();
		txtProximityId.setImmediate(true);
		txtProximityId.setWidth("140px");
		txtProximityId.setHeight("22px");
		mainLayout.addComponent(txtProximityId, "top:198px; left:140.0px;");

		lblDesignation = new Label("Designation :");
		lblDesignation.setImmediate(false);
		lblDesignation.setWidth("-1px");
		lblDesignation.setHeight("-1px");
		mainLayout.addComponent(lblDesignation, "top:225px; left:30.0px;");

		txtDesignation = new TextRead();
		txtDesignation.setImmediate(true);
		txtDesignation.setWidth("250px");
		txtDesignation.setHeight("22px");
		mainLayout.addComponent(txtDesignation, "top:223px; left:141.0px;");

		lblJoiningDate = new Label("Joining Date : ");
		lblJoiningDate.setImmediate(false);
		lblJoiningDate.setWidth("-1px");
		lblJoiningDate.setHeight("-1px");
		mainLayout.addComponent(lblJoiningDate, "top:250px; left:30.0px;");

		dJoiningDate = new PopupDateField();
		dJoiningDate.setImmediate(true);
		dJoiningDate.setWidth("110px");
		dJoiningDate.setHeight("-1px");
		dJoiningDate.setReadOnly(true);
		dJoiningDate.setDateFormat("dd-MM-yyyy");
		dJoiningDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dJoiningDate, "top:248px; left:140.0px;");

		lblConfirmationDate = new Label("Confirmation Date : ");
		lblConfirmationDate.setImmediate(false);
		lblConfirmationDate.setWidth("-1px");
		lblConfirmationDate.setHeight("-1px");
		//mainLayout.addComponent(lblConfirmationDate, "top:275px; left:30.0px;");

		dConfirmationDate = new PopupDateField();
		dConfirmationDate.setImmediate(true);
		dConfirmationDate.setWidth("110px");
		dConfirmationDate.setReadOnly(true);
		dConfirmationDate.setHeight("-1px");
		dConfirmationDate.setDateFormat("dd-MM-yyyy");
		dConfirmationDate.setResolution(PopupDateField.RESOLUTION_DAY);
		//mainLayout.addComponent(dConfirmationDate,"top:273px; left:140.0px;");
		
		lblEmployeeType = new Label("Employee Type :");
		lblEmployeeType.setImmediate(false);
		lblEmployeeType.setWidth("-1px");
		lblEmployeeType.setHeight("-1px");
		mainLayout.addComponent(lblEmployeeType, "top:300px; left:30.0px;");

		txtEmployeeType = new TextRead();
		txtEmployeeType.setImmediate(true);
		txtEmployeeType.setWidth("140px");
		txtEmployeeType.setHeight("22px");
		mainLayout.addComponent(txtEmployeeType, "top:298px; left:140.0px;");

		lblLeaveBalance = new Label("<html> <b><u>Leave Balance</u></b></html>",Label.CONTENT_XHTML);
		lblLeaveBalance.setImmediate(false);
		lblLeaveBalance.setWidth("-1px");
		lblLeaveBalance.setHeight("-1px");
		mainLayout.addComponent(lblLeaveBalance, "top:325px; left:140.0px;");

		lblCasualLeave = new Label("CL :");
		lblCasualLeave.setImmediate(false);
		lblCasualLeave.setWidth("-1px");
		lblCasualLeave.setHeight("-1px");
		mainLayout.addComponent(lblCasualLeave, "top:350px; left:30.0px;");

		txtCasualLeave = new TextRead();
		txtCasualLeave.setImmediate(true);
		txtCasualLeave.setWidth("35px");
		txtCasualLeave.setStyleName("txtRightcoloum");
		txtCasualLeave.setHeight("22px");
		mainLayout.addComponent(txtCasualLeave, "top:350px; left:55.0px;");

		lblSickLeave = new Label("SL :");
		lblSickLeave.setImmediate(false);
		lblSickLeave.setWidth("-1px");
		lblSickLeave.setHeight("-1px");
		mainLayout.addComponent(lblSickLeave, "top:350px; left:95.0px;");

		txtSickLeave = new TextRead();
		txtSickLeave.setImmediate(true);
		txtSickLeave.setWidth("35px");
		txtSickLeave.setStyleName("txtRightcoloum");
		txtSickLeave.setHeight("22px");
		mainLayout.addComponent(txtSickLeave, "top:350px; left:120.0px;");

		lblAnualLeave = new Label("AL/EL :");
		lblAnualLeave.setImmediate(false);
		lblAnualLeave.setWidth("-1px");
		lblAnualLeave.setHeight("-1px");
		mainLayout.addComponent(lblAnualLeave, "top:350px; left:165.0px;");

		txtAnualLeave = new TextRead();
		txtAnualLeave.setImmediate(true);
		txtAnualLeave.setWidth("35px");
		txtAnualLeave.setStyleName("txtRightcoloum");
		txtAnualLeave.setHeight("22px");
		mainLayout.addComponent(txtAnualLeave, "top:350px; left:207.0px;");

		lblLeaveType = new Label("Reference No :");
		lblLeaveType.setImmediate(false);
		lblLeaveType.setWidth("-1px");
		lblLeaveType.setHeight("-1px");
		mainLayout.addComponent(lblLeaveType, "top:20.0px; left:430.0px;");

		txtReferenceNo = new TextRead();
		txtReferenceNo.setImmediate(true);
		txtReferenceNo.setWidth("150px");
		txtReferenceNo.setHeight("22px");
		mainLayout.addComponent(txtReferenceNo, "top:18.0px; left:530.5px;");

		lblPaymentType = new Label("Type : ");
		lblPaymentType.setImmediate(false);
		lblPaymentType.setWidth("-1px");
		lblPaymentType.setHeight("-1px");
		mainLayout.addComponent(lblPaymentType, "top:45.0px; left:430.0px;");

		ogPaymentType = new OptionGroup("",paymentTypeName);
		ogPaymentType.setStyleName("horizontal");
		ogPaymentType.setImmediate(true);
		ogPaymentType.setValue("With Pay");
		mainLayout.addComponent(ogPaymentType, "top:43.0px; left:530.0px;");

		lblLeaveType = new Label("Leave Type :");
		mainLayout.addComponent(lblLeaveType, "top:70.0px; left:430.0px;");

		cmbLeaveType = new ComboBox();
		cmbLeaveType.setImmediate(true);
		cmbLeaveType.setWidth("130px");
		cmbLeaveType.setHeight("-1px");
		mainLayout.addComponent(cmbLeaveType, "top:68.0px; left:530.0px;");

		cmbELType = new ComboBox();
		cmbELType.setImmediate(true);
		cmbELType.setWidth("100px");
		cmbELType.setHeight("-1px");
		mainLayout.addComponent(cmbELType, "top:68.0px; left:670.0px;");
		cmbELType.addItem("Advance");
		cmbELType.addItem("Current");

		txtLeaveType = new TextField();
		txtLeaveType.setImmediate(true);
		txtLeaveType.setWidth("130px");
		txtLeaveType.setVisible(false);
		mainLayout.addComponent(txtLeaveType, "top:68.0px; left:530.0px;");

		lblLeaveFrom = new Label("Leave From :");
		lblLeaveFrom.setImmediate(false);
		lblLeaveFrom.setWidth("-1px");
		lblLeaveFrom.setHeight("-1px");
		mainLayout.addComponent(lblLeaveFrom, "top:95.0px; left:430.0px;");

		dLeaveFrom = new PopupDateField();
		dLeaveFrom.setImmediate(true);
		dLeaveFrom.setWidth("110px");
		dLeaveFrom.setHeight("-1px");
		dLeaveFrom.setValue(new java.util.Date());
		dLeaveFrom.setDateFormat("dd-MM-yyyy");
		dLeaveFrom.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dLeaveFrom, "top:93.0px; left:530.0px;");

		lblLeaveTo = new Label("Leave To :");
		lblLeaveTo.setImmediate(false);
		lblLeaveTo.setWidth("-1px");
		lblLeaveTo.setHeight("-1px");
		mainLayout.addComponent(lblLeaveTo, "top:120.0px; left:430.0px;");

		dLeaveTo = new PopupDateField();
		dLeaveTo.setImmediate(true);
		dLeaveTo.setWidth("110px");
		dLeaveTo.setHeight("-1px");
		dLeaveTo.setDateFormat("dd-MM-yyyy");
		dLeaveTo.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dLeaveTo, "top:118.0px; left:530.0px;");

		lblPurposeOfLeave = new Label("Leave Purpose :");
		lblPurposeOfLeave.setImmediate(true);
		lblPurposeOfLeave.setWidth("-1px");
		lblPurposeOfLeave.setHeight("-1px");
		mainLayout.addComponent(lblPurposeOfLeave, "top:147.0px; left:430.0px;");

		txtPurposeOfLeave = new TextField();
		txtPurposeOfLeave.setImmediate(true);
		txtPurposeOfLeave.setWidth("220px");
		txtPurposeOfLeave.setHeight("60px");
		mainLayout.addComponent(txtPurposeOfLeave, "top:143.0px; left:530.0px;");

		lblLeaveAddress = new Label("Leave address :");
		lblLeaveAddress.setImmediate(false);
		lblLeaveAddress.setWidth("-1px");
		lblLeaveAddress.setHeight("-1px");
		mainLayout.addComponent(lblLeaveAddress, "top:208.0px; left:430.0px;");

		txtLeaveAddress = new TextField();
		txtLeaveAddress.setImmediate(true);
		txtLeaveAddress.setWidth("220px");
		txtLeaveAddress.setHeight("60px");
		mainLayout.addComponent(txtLeaveAddress, "top:204.0px; left:530.0px;");

		lblMobileNo = new Label("Mobile No :");
		lblMobileNo.setImmediate(false);
		lblMobileNo.setWidth("-1px");
		lblMobileNo.setHeight("-1px");
		mainLayout.addComponent(lblMobileNo, "top:268.0px; left:430.0px;");

		txtMobileNo = new TextField();
		txtMobileNo.setImmediate(true);
		txtMobileNo.setMaxLength(11);	
		txtMobileNo.setWidth("150px");
		txtMobileNo.setHeight("-1px");
		mainLayout.addComponent(txtMobileNo, "top:265.0px; left:530.0px;");

		lblDuration = new Label("Duration :");
		lblDuration.setImmediate(true);
		lblDuration.setWidth("-1px");
		lblDuration.setHeight("-1px");
		mainLayout.addComponent(lblDuration, "top:293.0px; left:430.0px;");

		txtDuration = new TextRead();
		txtDuration.setImmediate(true);
		txtDuration.setWidth("60px");
		txtDuration.setHeight("22px");
		txtDuration.setStyleName("txtRightcoloum");
		mainLayout.addComponent(txtDuration, "top:291.0px; left:530.0px;");

		lblDays = new Label("Days");
		lblDays.setImmediate(false);
		lblDays.setWidth("-1px");
		lblDays.setHeight("-1px");
		mainLayout.addComponent(lblDays, "top:293.0px; left:595.0px;");

		lblFriday = new Label("No of Friday :");
		lblFriday.setImmediate(false);
		lblFriday.setWidth("-1px");
		lblFriday.setHeight("-1px");
		mainLayout.addComponent(lblFriday, "top:318.0px; left:430.0px;");
	
		txtFriday = new TextRead();
		txtFriday.setImmediate(true);
		txtFriday.setWidth("60px");
		txtFriday.setHeight("22px");
		txtFriday.setStyleName("txtFriday");	
		mainLayout.addComponent(txtFriday, "top:316px; left:530.0px;");

		table.setWidth("598px");
		table.setHeight("190px");
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("SL#", Label.class , new Label());
		table.setColumnWidth("SL#",35);

		table.addContainerProperty("Employee Name", Label.class , new Label());
		table.setColumnWidth("Employee Name",170);

		table.addContainerProperty("Date", Label.class , new Label());
		table.setColumnWidth("Date",85);

		table.addContainerProperty("Entry Date", Label.class , new Label());
		table.setColumnWidth("Entry Date",85);

		table.addContainerProperty("Day", Label.class , new Label());
		table.setColumnWidth("Day",70);

		table.addContainerProperty("Adjusted Type", Label.class , new Label());
		table.setColumnWidth("Adjusted Type",140);

		table.setColumnCollapsed("Entry Date", true);
		mainLayout.addComponent(table,"top:375px;left:90.0px;");
		
		table.setStyleName("wordwrap-headers");
		
		mainLayout.addComponent(cButton, "top:580.0px; left:50.0px;");

		return mainLayout;
	}

	private void btnIni(boolean t)
	{
		cButton.btnNew.setEnabled(t);
		cButton.btnEdit.setEnabled(t);
		cButton.btnSave.setEnabled(!t);
		cButton.btnRefresh.setEnabled(t);
		cButton.btnDelete.setEnabled(t);
		cButton.btnPreview.setEnabled(t);
		cButton.btnFind.setEnabled(t);
	}

	public void txtInit(boolean t)
	{
		dApplicationDate.setEnabled(!t);
		cmbUnit.setEnabled(!t);		
		cmbDepartment.setEnabled(!t);
		chkDepartmentAll.setEnabled(!t);
		cmbSection.setEnabled(!t);
		chkAllSection.setEnabled(!t);
		ogSelectEmployee.setEnabled(!t);

		cmbEmployeeName.setEnabled(!t);
		txtFingerId.setEnabled(!t);
		txtProximityId.setEnabled(!t);
		txtDesignation.setEnabled(!t);
		dJoiningDate.setEnabled(!t);
		dConfirmationDate.setEnabled(!t);
		txtEmployeeType.setEnabled(!t);

		txtCasualLeave.setEnabled(!t);
		txtSickLeave.setEnabled(!t);
		txtAnualLeave.setEnabled(!t);

		txtReferenceNo.setEnabled(!t);
		cmbLeaveType.setEnabled(!t);
		cmbELType.setEnabled(!t);
		txtLeaveType.setEnabled(!t);
		ogPaymentType.setEnabled(!t);
		dLeaveFrom.setEnabled(!t);
		dLeaveTo.setEnabled(!t);

		txtPurposeOfLeave.setEnabled(!t);
		txtLeaveAddress.setEnabled(!t);
		txtMobileNo.setEnabled(!t);
		txtDuration.setEnabled(!t);
		txtFriday.setEnabled(!t);
		table.setEnabled(!t);
	}
	private void mainClear()
	{
		cmbUnit.setValue(null);
		cmbDepartment.setValue(null);
		cmbSection.setValue(null);
		chkDepartmentAll.setValue(false);
		chkAllSection.setValue(false);
		cmbEmployeeName.setValue(null);
		txtReferenceNo.setValue("");
		designationId = "";

		txtClear();
	}
	private void txtClear()
	{
		dApplicationDate.setValue(new Date());
		txtFingerId.setValue("");
		txtProximityId.setValue("");
		txtDesignation.setValue("");
		txtCasualLeave.setValue("");
		txtAnualLeave.setValue("");
		txtSickLeave.setValue("");
		cmbLeaveType.setValue(null);
		cmbELType.setValue(null);
		txtLeaveType.setValue("");
		dLeaveTo.setValue(null);
		txtPurposeOfLeave.setValue("");
		txtLeaveAddress.setValue("");
		txtMobileNo.setValue("");
		txtDuration.setValue("");
		txtFriday.setValue("");
		dJoiningDate.setReadOnly(false);
		dJoiningDate.setValue(null);
		dJoiningDate.setReadOnly(true);
		
		dConfirmationDate.setReadOnly(false);
		dConfirmationDate.setValue(null);
		dConfirmationDate.setReadOnly(true);
		txtEmployeeType.setValue("");
		tableClear();
	}

	private void tableClear()
	{
		for(int i = 0; i<tbdDate.size(); i++)
		{
			tbllblEmployeeName.get(i).setValue("");
			tbdDate.get(i).setValue(null);
			tbllblWeekDay.get(i).setValue("");
			tblAdjustedType.get(i).setValue("");
		
		}
	}

	private void focusEnter()
	{
		allComp.add(cmbUnit);
		allComp.add(cmbDepartment);
		allComp.add(cmbSection);
		allComp.add(ogSelectEmployee);
		allComp.add(cmbEmployeeName);

		allComp.add(cmbLeaveType);
		allComp.add(ogPaymentType);

		allComp.add(dLeaveFrom);
		allComp.add(dLeaveTo);

		allComp.add(txtPurposeOfLeave);
		allComp.add(txtLeaveAddress);
	
		allComp.add(txtMobileNo);

		allComp.add(cButton.btnSave);
		new FocusMoveByEnter(this,allComp);
	}

	private void reportPreview()
	{
		ReportDate reportTime = new ReportDate();
		
		try
		{
			HashMap <String,Object> hm = new HashMap <String,Object> ();
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("appDate", dApplicationDate.getValue());
			hm.put("userName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			hm.put("path", "./report/account/hrmModule/");
			hm.put("SysDate",reportTime.getTime);
			hm.put("logo", sessionBean.getCompanyLogo());
	//		hm.put("developer", sessionBean.getDeveloperAddress());
			String subReportQuery="select * from funLeaveBalanceDetails('%','"+cmbEmployeeName.getValue().toString()+"','"+sessionBean.dfDb.format(new Date())+"')";
			System.out.println(subReportQuery);
			hm.put("subsql", subReportQuery);

			String str1 = " select * from funLeaveApplication('"+txtReferenceNo.getValue().toString()+"','%','%') order by vEmployeeId ";
			System.out.println(str1);
			hm.put("sql", str1);

			Window win = new ReportViewer(hm,"report/account/hrmModule/rptLeaveApplicationFormPOSCO.jasper",
					this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
					this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
					this.getWindow().getApplication().getURL()+"VAADIN/applet",true);

			win.setCaption("Project Report");
			win.setStyleName("cwindow");
			this.getParent().getWindow().addWindow(win);
		}
		catch(Exception exp)
		{
			showNotification("Error",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}
}
