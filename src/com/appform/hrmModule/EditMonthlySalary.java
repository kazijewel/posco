package com.appform.hrmModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;
import com.vaadin.data.Property.ValueChangeListener;

@SuppressWarnings("serial")
public class EditMonthlySalary extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Table table= new Table();
	private PopupDateField dWorkingDate ;

	private Label lblDate ;
	private Label lblUnit;
	private ComboBox csbUnit;
	private Label lblSection;
	private ComboBox csbSection,csbDepartment;

	private Label lblEmployee;
	private ComboBox csbEmployee;
	private CheckBox chkEmployeeAll,chkDepartmentAll,chkSectionAll;

	private Label lblNoteSalary=new Label("HR: House Rent,  Conv.: Conveyance,  MA: Medical Allowance,  Attn. Bonus: Present Bonus ");
	private Label lblNoteSalary2=new Label("Late. Att: Late Attendance,  Avd. Sal: Advance Salary/Other");

	private ArrayList<NativeButton> btnDel=new ArrayList<NativeButton>();
	private ArrayList<Label> lblsa = new ArrayList<Label>();
	private ArrayList<Label> lblAutoEmployeeID=new ArrayList<Label>();
	private ArrayList<Label> lblEmployeeID=new ArrayList<Label>();
	private ArrayList<Label> lblFingerID = new ArrayList<Label>();
	private ArrayList<Label> lblEmployeeName = new ArrayList<Label>();
	private ArrayList<Label> lblDesignation = new ArrayList<Label>();

	private ArrayList<Label> lblBasic = new ArrayList<Label>();
	private ArrayList<Label> lblHouseRent = new ArrayList<Label>();
	private ArrayList<Label> lblConveyance = new ArrayList<Label>();
	private ArrayList<Label> lblMedicalAll = new ArrayList<Label>();
	private ArrayList<Label> lblOthersAll = new ArrayList<Label>();
	private ArrayList<Label> lblSpecialAll = new ArrayList<Label>();
	private ArrayList<Label> lblGross = new ArrayList<Label>();
	private ArrayList<Label> lblAttBonus = new ArrayList<Label>();
	private ArrayList<Label> lblAbsAmount = new ArrayList<Label>();
	private ArrayList<Label> lblLwpAmount = new ArrayList<Label>();
	private ArrayList<AmountCommaSeperator> txtLoan = new ArrayList<AmountCommaSeperator>();
	private ArrayList<AmountCommaSeperator> txtAdvSalary = new ArrayList<AmountCommaSeperator>();
	private ArrayList<Label> lblLateAttAmount = new ArrayList<Label>();
	/*private ArrayList<AmountCommaSeperator> txtCompensation = new ArrayList<AmountCommaSeperator>();
	private ArrayList<AmountCommaSeperator> txtMobileBillExtra = new ArrayList<AmountCommaSeperator>();*/
	private ArrayList<Label> lblIncomeTax = new ArrayList<Label>();
	private ArrayList<Label> txtTotal = new ArrayList<Label>();
	private ArrayList<Label> lblLoanId = new ArrayList<Label>();
	private ArrayList<Label> lblTransactionId = new ArrayList<Label>();

	ArrayList<Component> allComp = new ArrayList<Component>();	

	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat FMonthName = new SimpleDateFormat("MMMMM");
	private SimpleDateFormat FYear = new SimpleDateFormat("yyyy");

	CommonButton button = new CommonButton("New", "Save", "", "","Refresh","","","","","Exit");

	private Boolean isUpdate= false;
	boolean t;
	int i = 0;

	private CommaSeparator cs = new CommaSeparator();

	String empId="";
	String empCode="";
	String empFingerId="";
	String empProxId="";
	String empDesId="";

	String Notify="";
	private int ar;
	private CommonMethod cm;
	private String menuId = "";
	public EditMonthlySalary(SessionBean sessionBean,String menuId) 
	{
		this.sessionBean=sessionBean;
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		this.setCaption("MONTHLY SALARY EDIT :: " + sessionBean.getCompany());
		buildMainLayout();
		setContent(mainLayout);
		tableinitialise();
		componentIni(true);
		btnIni(true);
		SetEventAction();
		focusEnter();
		csbUnitData();
		authenticationCheck();
		button.btnNew.focus();
	}

	private void authenticationCheck()
	{
		cm.checkFormAction(menuId);
		if(!sessionBean.isSuperAdmin())
		{
		if(!sessionBean.isAdmin())
		{
			if(!cm.isSave)
			{button.btnSave.setVisible(false);}
			if(!cm.isEdit)
			{button.btnEdit.setVisible(false);}
			if(!cm.isDelete)
			{button.btnDelete.setVisible(false);}
			if(!cm.isPreview)
			{button.btnPreview.setVisible(false);}
		}
		}
	}

	private void SetEventAction()
	{
		button.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate = false;
				componentIni(false);
				btnIni(false);
				txtClear();
				dWorkingDate.focus();
			}
		});

		button.btnRefresh.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate = false;
				componentIni(true);
				btnIni(true);
				txtClear();
			}
		});

		button.btnSave.addListener( new Button.ClickListener() 
		{			
			public void buttonClick(ClickEvent event)
			{
				if(csbUnit.getValue()!=null )
				{
				if(csbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
				{
					if(csbSection.getValue()!=null || chkSectionAll.booleanValue())
					{
						if(!lblEmployeeName.get(0).toString().equals(""))
						{
							if(tableValidationCheck())
							{
								saveBtnAction(event);
							}
							else
							{
								showNotification("Warning", Notify, Notification.TYPE_WARNING_MESSAGE);
							}
						}
						else
						{
							showNotification("Warning","There are nothing to save!!!",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else
					{
						showNotification("Warning","Please Select Section!!!",Notification.TYPE_WARNING_MESSAGE);
					}			
				}
				else
				{
					showNotification("Warning","Please Select Department!!!",Notification.TYPE_WARNING_MESSAGE);
				}			
			}
			else
			{
				showNotification("Warning","Please select  Project!!!",Notification.TYPE_WARNING_MESSAGE);
			}
		}			
		});

		button.btnExit.addListener( new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				close();
			}
		});

		dWorkingDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				tableClear();
				if(dWorkingDate.getValue()!=null)
				{
					csbUnit.removeAllItems();
					if(dWorkingDate.getValue()!=null)
					{
						csbUnitData();
					}
				}
			}
		});
		csbUnit.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				tableClear();
				if(csbUnit.getValue()!=null)
				{
					csbDepartmentData();
				}
			}
		});
		csbDepartment.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				tableClear();
				if(csbDepartment.getValue()!=null)
				{
					csbSectionData();
				}
			}
		});

		chkDepartmentAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				tableClear();
				if(csbUnit.getValue()!=null)
				{
					if(chkDepartmentAll.booleanValue())
					{
						csbDepartment.setEnabled(false);
						csbDepartment.setValue(null);
						csbSectionData();
					}
					else
					{
						csbDepartment.setEnabled(true);
					}
				}
				else
				{
					chkDepartmentAll.setValue(false);
				}
			}
		});
		csbSection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				tableClear();
				if(csbSection.getValue()!=null)
				{
					csbEmployeeNameDataAdd();
				}
			}
		});
		chkSectionAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				tableClear();
				if(csbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
				{
					if(chkSectionAll.booleanValue())
					{
						csbSection.setEnabled(false);
						csbSection.setValue(null);
						csbEmployeeNameDataAdd();
					}
					else
					{
						csbSection.setEnabled(true);
					}
				}
				else
				{
					chkSectionAll.setValue(false);
				}
			}
		});

		chkEmployeeAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				tableClear();
				if(csbSection.getValue()!=null || chkSectionAll.booleanValue())
				{
					if(chkEmployeeAll.booleanValue())
					{
						csbEmployee.setEnabled(false);
						csbEmployee.setValue(null);
						addTableData();
					}
					else
					{
						csbEmployee.setEnabled(true);
					}
				}
				else
				{
					chkEmployeeAll.setValue(false);
				}
			}
		});
		csbEmployee.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
				tableClear();
				if(csbEmployee.getValue()!=null)
				{
					addTableData();
				}
			}
		});
	}

	private boolean tableValidationCheck()
	{
		boolean ret=false;
		for(int i=0;i<lblAutoEmployeeID.size();i++)
		{
			if(!lblAutoEmployeeID.get(i).getValue().toString().isEmpty())
			{
				if(!lblAbsAmount.get(i).getValue().toString().isEmpty() ||
						!txtLoan.get(i).getValue().toString().isEmpty() ||
						!txtAdvSalary.get(i).getValue().toString().isEmpty() ||
						!lblLateAttAmount.get(i).getValue().toString().isEmpty())
				{
					ret=true;
					break;
				}
				else
					Notify="Provide Salary Adjustment Amount!!!";
			}
			else
				Notify="No Employee Found!!!";
		}
		return ret;
	}

/*	private void addEmployeeData(String sectionID)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try 
		{
			List <?> list = session.createSQLQuery("select vEmployeeId,vEmployeeCode from tbMonthlySalary where vUnitId='"+csbUnit.getValue().toString()+"'" +
					" vSectionID " + "like '"+sectionID+"' and " +
					"vSalaryMonth=DateName(MM,'"+dFormat.format(dWorkingDate.getValue())+"') " +
					"and vSalaryYear=YEAR('"+dFormat.format(dWorkingDate.getValue())+"') order by vEmployeeId").list();
			
			
			System.out.println();
			
			for (Iterator <?> iter = list.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				csbEmployee.addItem(element[0].toString());
				csbEmployee.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch (Exception ex) 
		{
			showNotification("addEmployeeData", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

*/
	
	private void addTableData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String check = " SELECT * from tbMonthlySalary where vSalaryMonth='"+FMonthName.format(dWorkingDate.getValue())+"' and vSalaryYear = '"+FYear.format(dWorkingDate.getValue())+"'";
		
			System.out.println("Table Set :" +check);
			
			List <?> checkList = session.createSQLQuery(check).list();

			if(!checkList.isEmpty())
			{
				
				String sql = "select vEmployeeID,vEmployeeCode,vFingerId,vEmployeeName,vDesignation,mBasic,mHouseRent,mConveyance,mMedicalAllowance," +
						" mOtherAllowance,mSpecialAllowance,(mBasic+mHouseRent+mConveyance+mMedicalAllowance+mOtherAllowance+mSpecialAllowance)mGross," +
						" mPresentBonus,(Round((ISNULL(mGross,0)/ISNULL(iTotalDay,0))*ISNULL(iAbsentDay,0),0))absentAmount," +
						" (Round((ISNULL(mGross,0)/ISNULL(iTotalDay,0))*ISNULL(iLeaveWithoutPay,0),0))lwpAmount," +
						" mLoanAmount,mAdvanceSalary,(Round((ISNULL(mGross,0)/ISNULL(iTotalDay,0))*ISNULL(iLateDay,0),0))lateAmount,mIncomeTax," +
						" vLoanNo,vTransactionId from tbMonthlySalary " +
						" where vUnitId='"+csbUnit.getValue().toString()+"' and vSectionID like '"+(chkSectionAll.booleanValue()?"%":(csbSection.getValue()==null?"%":csbSection.getValue()))+"' " +
						" and vDepartmentId like '"+(chkDepartmentAll.booleanValue()?"%":(csbDepartment.getValue()==null?"%":csbDepartment.getValue()))+"' " +
						" and vEmployeeID like '"+(chkEmployeeAll.booleanValue()?"%":(csbEmployee.getValue()==null?"%":csbEmployee.getValue()))+"' and vSalaryMonth='"+FMonthName.format(dWorkingDate.getValue())+"' " +
						" and vSalaryYear='"+FYear.format(dWorkingDate.getValue())+"' " +
						" order by vEmployeeName";

				System.out.println("Data Set :" + sql);

				
				List <?> lst = session.createSQLQuery(sql).list();

				for (Iterator <?> iter = lst.iterator(); iter.hasNext();) 
				{
					if(i==lblEmployeeName.size()-1)
					{
						tableRowAdd(i+1);
					}

					Object[] element = (Object[]) iter.next();

					for(int j=0;j<lblAutoEmployeeID.size();j++)
					{
						if(lblAutoEmployeeID.get(j).getValue().toString().trim().isEmpty())
						{
							i=j;
							t=true;
							break;
						}
						if(!lblAutoEmployeeID.get(j).getValue().toString().trim().isEmpty())
						{
							if(lblAutoEmployeeID.get(j).getValue().toString().equals(element[0].toString()))
							{
								t=false;
								break;
							}
						}
					}

					if(t){
						//not in table
						lblAutoEmployeeID.get(i).setValue(element[0].toString());
						lblEmployeeID.get(i).setValue(element[1].toString());
						lblFingerID.get(i).setValue(element[2].toString());
						lblEmployeeName.get(i).setValue(element[3].toString());
						lblDesignation.get(i).setValue(element[4].toString());

						lblBasic.get(i).setValue(cs.setComma(Double.parseDouble(element[5].toString())));
						lblHouseRent.get(i).setValue(cs.setComma(Double.parseDouble(element[6].toString())));
						lblConveyance.get(i).setValue(cs.setComma(Double.parseDouble(element[7].toString())));
						lblMedicalAll.get(i).setValue(cs.setComma(Double.parseDouble(element[8].toString())));
						lblOthersAll.get(i).setValue(cs.setComma(Double.parseDouble(element[9].toString())));
						lblSpecialAll.get(i).setValue(cs.setComma(Double.parseDouble(element[10].toString())));
						lblGross.get(i).setValue(cs.setComma(Double.parseDouble(element[11].toString())));
						
						lblAttBonus.get(i).setValue(cs.setComma(Double.parseDouble(element[12].toString())));
						lblAbsAmount.get(i).setValue(cs.setComma(Double.parseDouble(element[13].toString())));
						lblLwpAmount.get(i).setValue(cs.setComma(Double.parseDouble(element[14].toString())));
						txtLoan.get(i).setValue(cs.setComma(Double.parseDouble(element[15].toString())));
						txtAdvSalary.get(i).setValue(cs.setComma(Double.parseDouble(element[16].toString())));
						lblLateAttAmount.get(i).setValue(cs.setComma(Double.parseDouble(element[17].toString())));
						//txtCompensation.get(i).setValue(cs.setComma(Double.parseDouble(element[14].toString())));
						lblIncomeTax.get(i).setValue(cs.setComma(Double.parseDouble(element[18].toString())));
						lblLoanId.get(i).setValue(cs.setComma(Double.parseDouble(element[19].toString())));
						lblTransactionId.get(i).setValue(cs.setComma(Double.parseDouble(element[20].toString())));
						//txtMobileBillExtra.get(i).setValue(cs.setComma(Double.parseDouble(element[17].toString())));
						i++;
					}


				}
				if(i == 0)
				{
					showNotification("Warning!","No Data found",Notification.TYPE_WARNING_MESSAGE);
				}
			}
			else
			{
				tableClear();
				showNotification("Warning!","No data found",Notification.TYPE_WARNING_MESSAGE);
			}
			if(!t)
			{
				showNotification("Warning","This Employee is already exists!!!",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception ex)
		{
			showNotification("addTableData",ex.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void csbUnitData() 
	{
		csbUnit.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			
		/*	'"+dFormat.format(dGenerateDate.getValue())+"'*/
			
			String query="select distinct vUnitId,vUnitName from tbMonthlySalary order by vUnitName";
			
			System.out.println("Unit"+query);
			
			List <?> list=session.createSQLQuery(query).list();	

			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				csbUnit.addItem(element[0]);
				csbUnit.setItemCaption(element[0], (String) element[1]);
			}
		}
		catch(Exception exp)
		{
			showNotification("csbUnitData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void csbDepartmentData() 
	{
		csbDepartment.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
/*			String query="select distinct vSectionId,vSectionName from tbEmpSectionInfo  where order by vSectionName";
*/		
			String query="select distinct vDepartmentId,vDepartmentName from tbMonthlySalary  where vUnitId='"+csbUnit.getValue().toString()+"'  order by vDepartmentName";
			
			System.out.println("Section"+query);
			
			List <?> list=session.createSQLQuery(query).list();	

			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				csbDepartment.addItem(element[0]);
				csbDepartment.setItemCaption(element[0], (String) element[1]);
			}
		}
		catch(Exception exp)
		{
			showNotification("csbSectionData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void csbSectionData() 
	{
		csbSection.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
/*			String query="select distinct vSectionId,vSectionName from tbEmpSectionInfo  where order by vSectionName";
*/		
			String query="select distinct vSectionId,vSectionName from tbMonthlySalary  where "
					+ " vUnitId='"+csbUnit.getValue().toString()+"'   "
					+ " and vDepartmentId like '"+(csbDepartment.getValue()==null?"%":csbDepartment.getValue())+"'   "
					+ " order by vSectionName";
			
			System.out.println("Section"+query);
			
			List <?> list=session.createSQLQuery(query).list();	

			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				csbSection.addItem(element[0]);
				csbSection.setItemCaption(element[0], (String) element[1]);
			}
		}
		catch(Exception exp)
		{
			showNotification("csbSectionData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void csbEmployeeNameDataAdd()
	{
		csbEmployee.removeAllItems();
		
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select vEmployeeId,vEmployeeName,vEmployeeCode from tbMonthlySalary where "
					+ " vUnitId like '"+csbUnit.getValue().toString()+"'   "
					+ " and vDepartmentId like '"+(csbDepartment.getValue()==null?"%":csbDepartment.getValue())+"'   "
					+ " and vSectionId like '"+(csbSection.getValue()==null?"%":csbSection.getValue())+"'   "
					+ " order by vEmployeeName";
	
			System.out.println("Employee"+query);
			
			List <?> lst=session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				for(Iterator <?> itr=lst.iterator();itr.hasNext();)
				{
					Object [] element=(Object[]) itr.next();
					csbEmployee.addItem(element[0]);
					csbEmployee.setItemCaption(element[0], (String)element[2]+">>"+element[1].toString());
				}
			}
			else
			{
				showNotification("Warning", "No Employee Found!!!", Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception exp)
		{
			showNotification("csbEmployeeNameDataAdd",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void saveBtnAction(ClickEvent e)
	{
		MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to update all information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
		mb.show(new EventListener()
		{
			public void buttonClicked(ButtonType buttonType)
			{
				if(buttonType == ButtonType.YES)
				{
					updateData();
					txtClear();
					componentIni(true);
					btnIni(true);
				}
			}
		});
	}

	private void updateData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{
			for(int i = 0;i<lblAutoEmployeeID.size();i++)
			{
				if(!lblAutoEmployeeID.get(i).getValue().toString().isEmpty())
				{
					if(!lblAbsAmount.get(i).getValue().toString().isEmpty() || !txtAdvSalary.get(i).getValue().toString().isEmpty() || !lblLateAttAmount.get(i).getValue().toString().isEmpty())
					{
						if(!txtLoan.get(i).getValue().toString().trim().isEmpty())
						{
							String updateLoanApplication = " UPDATE tbLoanApplication set mLoanBalance = mLoanBalance + (select mAdvanceSalary from tbMonthlySalary ts where "
								+ "ts.vUnitid='"+csbUnit.getValue().toString()+"' and ts.vEmployeeID = '"+lblAutoEmployeeID.get(i).getValue().toString()+"' and "+  
									"ts.vSalaryMonth='"+FMonthName.format(dWorkingDate.getValue())+"' and ts.vSalaryYear='"+FYear.format(dWorkingDate.getValue())+"')"+
									" where vAutoEmployeeId = '"+lblAutoEmployeeID.get(i).getValue().toString()+"' ";

							System.out.println("Update Loan Application :" +updateLoanApplication);

							session.createSQLQuery(updateLoanApplication).executeUpdate();

							String updateLoanRecovery = " UPDATE tbLoanRecoveryInfo set mRecoveryAmount = '"+(txtLoan.get(i).getValue().toString().trim().isEmpty()?0:txtLoan.get(i).getValue().toString().trim())+"' "+
									"where vAutoEmployeeId = '"+lblAutoEmployeeID.get(i).getValue().toString()+"' " +
									"and vLoanNo = '"+lblLoanId.get(i).getValue().toString()+"' " +
									"and vTransactionId = '"+lblTransactionId.get(i).getValue().toString()+"' ";

							session.createSQLQuery(updateLoanRecovery).executeUpdate();
							session.clear();
						}

						String udquery="insert into tbUDMonthlySalary(dGenerateDate,dSalaryDate,vSalaryMonth,vSalaryYear,vEmployeeID,vEmployeeCode,vFingerId,"
								+ " vProximityID,vEmployeeName,vDesignationID,vDesignation,vEmployeeType,vSectionID,vSectionName,vContactNo,dJoiningDate,iOtStatus,"
								+ " iTotalOTHour,iTotalOTMin,iTotalDay,iHoliday,iWorkingDay,iAbsentDay,iLeaveDay,iLeaveWithoutPay,iPresentDay,mBasic,mHouseRent,mConveyance,mMedicalAllowance,"
								+ " mGross,mPresentBonus,mLoanAmount,mAdvanceSalary,mTiffinAllowance,mMealCharge,mCompansation,mRevenueStamp,mMobileAllowance,mMobileAllowanceExtra,vUDFlag,vUserName,vUserIP,dEntryTime,vUnitId,vUnitName)"+
								"select dGenerateDate,dSalaryDate,vSalaryMonth,vSalaryYear,vEmployeeID,vEmployeeCode,vFingerId,vProximityID,"
								+ "vEmployeeName,vDesignationID,vDesignation,vEmployeeType,vSectionID,vSectionName,vContactNo,dJoiningDate,"
								+ "iOtStatus,iTotalOTHour,iTotalOTMin,iTotalDay,iHoliday,iWorkingDay,iAbsentDay,iLeaveDay,iLeaveWithoutPay,iPresentDay,mBasic,"
								+ "mHouseRent,mConveyance,mMedicalAllowance,mGross,mPresentBonus,mLoanAmount,mAdvanceSalary,mTiffinAllowance,"
								+ "mMealCharge,mCompansation,mRevenueStamp,mMobileAllowance,mMobileAllowanceExtra,'Old',vUserName,vUserIP,dEntryTime,vUnitId,vUnitName from tbMonthlySalary "
								+ "where vEmployeeID='"+lblAutoEmployeeID.get(i).getValue().toString()+"' " +
								"and vSalaryMonth='"+FMonthName.format(dWorkingDate.getValue())+"' and vSalaryYear='"+FYear.format(dWorkingDate.getValue())+"'";

						System.out.println("Update Monthly Salary UD :" +udquery);
						
						session.createSQLQuery(udquery).executeUpdate();
						session.clear();

						String query = "update tbMonthlySalary " +
								"set mLoanAmount='"+(txtLoan.get(i).getValue().toString().trim().isEmpty()?0:txtLoan.get(i).getValue().toString().trim())+"'," +
								"mAdvanceSalary='"+(txtAdvSalary.get(i).getValue().toString().trim().isEmpty()?0:txtAdvSalary.get(i).getValue().toString().trim())+"', " +
								"vUserName='"+sessionBean.getUserName()+"',vUserIP='"+sessionBean.getUserIp()+"',dEntryTime=GETDATE() where vUnitId='"+csbUnit.getValue().toString()+"' and vEmployeeID = '"+lblAutoEmployeeID.get(i).getValue().toString()+"' "
								+ "and vSalaryMonth='"+FMonthName.format(dWorkingDate.getValue())+"' and vSalaryYear='"+FYear.format(dWorkingDate.getValue())+"'";

						System.out.println("Update Monthly Salary :" +query);
						
						session.createSQLQuery(query).executeUpdate();
						session.clear();
					
						String udquery2="insert into tbUDMonthlySalary(dGenerateDate,dSalaryDate,vSalaryMonth,vSalaryYear,vEmployeeID,vEmployeeCode,vFingerId,vProximityID,"
								+ " vEmployeeName,vDesignationID,vDesignation,vEmployeeType,vSectionID,vSectionName,vContactNo,dJoiningDate,iOtStatus,iTotalOTHour,iTotalOTMin,"
								+ " iTotalDay,iHoliday,iWorkingDay,iAbsentDay,iLeaveDay,iLeaveWithoutPay,iPresentDay,mBasic,mHouseRent,mConveyance,mMedicalAllowance,mGross,mPresentBonus,"
								+ " mLoanAmount,mAdvanceSalary,mTiffinAllowance,mMealCharge,mCompansation,mRevenueStamp,mMobileAllowance,mMobileAllowanceExtra,vUDFlag,vUserName,vUserIP,dEntryTime,vUnitId,vUnitName)"
								+ " select dGenerateDate,dSalaryDate,vSalaryMonth,vSalaryYear,vEmployeeID,vEmployeeCode,vFingerId,vProximityID,"
								+ " vEmployeeName,vDesignationID,vDesignation,vEmployeeType,vSectionID,vSectionName,vContactNo,dJoiningDate,"
								+ " iOtStatus,iTotalOTHour,iTotalOTMin,iTotalDay,iHoliday,iWorkingDay,iAbsentDay,iLeaveDay,iLeaveWithoutPay,iPresentDay,mBasic,"
								+ " mHouseRent,mConveyance,mMedicalAllowance,mGross,mPresentBonus,mLoanAmount,mAdvanceSalary,mTiffinAllowance,"
								+ " mMealCharge,mCompansation,mRevenueStamp,mMobileAllowance,mMobileAllowanceExtra,'Update',vUserName,vUserIP,dEntryTime,vUnitId,vUnitName from tbMonthlySalary "
								+ " where vUnitId='"+csbUnit.getValue().toString()+"' and vEmployeeID='"+lblAutoEmployeeID.get(i).getValue().toString()+"' " +
								"and vSalaryMonth='"+FMonthName.format(dWorkingDate.getValue())+"' and vSalaryYear='"+FYear.format(dWorkingDate.getValue())+"'";
						
						session.createSQLQuery(udquery2).executeUpdate();
						session.clear();
				
					
						System.out.println("Update Monthly Salary 2:" +udquery2);
					}
				}
			}
			tx.commit();
			Notification n=new Notification("All Information Updated Successfully!","",Notification.TYPE_TRAY_NOTIFICATION);
			n.setPosition(Notification.POSITION_TOP_RIGHT);
			showNotification(n);
		}
		catch(Exception ex)
		{
			showNotification("updateData", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
		finally{session.close();}
	}

	private void focusEnter()
	{
		allComp.add(dWorkingDate);
		allComp.add(csbUnit);
		allComp.add(csbDepartment);
		allComp.add(csbSection);
		for(int i=0;i<lblAutoEmployeeID.size();i++)
		{
			allComp.add(txtLoan.get(i));
			allComp.add(txtAdvSalary.get(i));
			//allComp.add(lblLateAttAmount.get(i));
			//allComp.add(txtCompensation.get(i));
		}
		allComp.add(button.btnSave);
		new FocusMoveByEnter(this,allComp);
	}

	private void componentIni(boolean b) 
	{
		dWorkingDate.setEnabled(!b);
		csbUnit.setEnabled(!b);
		csbDepartment.setEnabled(!b);
		csbSection.setEnabled(!b);
		lblNoteSalary.setEnabled(!b);
		csbEmployee.setEnabled(!b);
		chkDepartmentAll.setEnabled(!b);
		chkSectionAll.setEnabled(!b);
		chkEmployeeAll.setEnabled(!b);
		lblNoteSalary2.setEnabled(!b);
		table.setEnabled(!b);
		chkEmployeeAll.setValue(false);
		if(isUpdate)
		{csbEmployee.setEnabled(false);}
	}

	private void btnIni(boolean t)
	{
		button.btnNew.setEnabled(t);
		button.btnEdit.setEnabled(t);
		button.btnSave.setEnabled(!t);
		button.btnRefresh.setEnabled(!t);
		button.btnDelete.setEnabled(t);
		button.btnFind.setEnabled(t);;
	}

	public void txtClear()
	{
		csbUnit.setValue(null);
		csbDepartment.setValue(null);
		csbSection.setValue(null);
		csbEmployee.setValue(null);
		chkDepartmentAll.setValue(false);
		chkSectionAll.setValue(false);
		chkEmployeeAll.setValue(false);
		tableClear();
	}

	private void tableClear()
	{
		for(int i=0; i<lblEmployeeName.size(); i++)
		{
			lblAutoEmployeeID.get(i).setValue("");
			lblEmployeeID.get(i).setValue("");
			lblFingerID.get(i).setValue("");
			lblEmployeeName.get(i).setValue("");
			lblDesignation.get(i).setValue("");
			lblBasic.get(i).setValue("");
			lblHouseRent.get(i).setValue("");
			lblConveyance.get(i).setValue("");
			lblMedicalAll.get(i).setValue("");
			lblOthersAll.get(i).setValue("");
			lblSpecialAll.get(i).setValue("");
			lblGross.get(i).setValue("");
			
			lblAttBonus.get(i).setValue("");
			lblAbsAmount.get(i).setValue("");
			lblLwpAmount.get(i).setValue("");
			txtLoan.get(i).setValue("");
			txtAdvSalary.get(i).setValue("");
			lblLateAttAmount.get(i).setValue("");
			/*txtCompensation.get(i).setValue("");
			txtMobileBillExtra.get(i).setValue("");*/
			lblIncomeTax.get(i).setValue("");
			txtTotal.get(i).setValue("");
			lblLoanId.get(i).setValue("");
			lblTransactionId.get(i).setValue("");
		}
	}

	private void tableinitialise()
	{
		for(int i=0;i<10;i++)
		{
			tableRowAdd(i);
		}
	}

	private void tableRowAdd( final int ar)
	{
		btnDel.add(ar, new NativeButton());
		btnDel.get(ar).setWidth("100%");
		btnDel.get(ar).setImmediate(true);
		btnDel.get(ar).setIcon(new ThemeResource("../icons/cancel.png"));
		btnDel.get(ar).addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				lblAutoEmployeeID.get(ar).setValue("");
				lblEmployeeID.get(ar).setValue("");
				lblFingerID.get(ar).setValue("");
				lblEmployeeName.get(ar).setValue("");
				lblDesignation.get(ar).setValue("");
				
				lblBasic.get(ar).setValue("");
				lblHouseRent.get(ar).setValue("");
				lblConveyance.get(ar).setValue("");
				lblMedicalAll.get(ar).setValue("");
				lblOthersAll.get(ar).setValue("");
				lblSpecialAll.get(ar).setValue("");
				lblAttBonus.get(ar).setValue("");
				lblGross.get(ar).setValue("");
				
				lblAbsAmount.get(ar).setValue("");
				lblLwpAmount.get(ar).setValue("");
				txtLoan.get(ar).setValue("");
				txtAdvSalary.get(ar).setValue("");
				lblLateAttAmount.get(ar).setValue("");
				/*txtCompensation.get(ar).setValue("");
				txtMobileBillExtra.get(ar).setValue("");*/
				lblIncomeTax.get(ar).setValue("");
				txtTotal.get(ar).setValue("");
				lblLoanId.get(ar).setValue("");
				lblTransactionId.get(ar).setValue("");

				for(int rowcount=ar;rowcount<=lblFingerID.size()-1;rowcount++)
				{
					if(rowcount+1<=lblFingerID.size()-1)
					{
						if(!lblFingerID.get(rowcount+1).getValue().toString().equals(""))
						{
							lblAutoEmployeeID.get(rowcount).setValue(lblAutoEmployeeID.get(rowcount+1).getValue().toString());
							lblEmployeeID.get(rowcount).setValue(lblEmployeeID.get(rowcount+1).getValue().toString());
							lblFingerID.get(rowcount).setValue(lblFingerID.get(rowcount+1).getValue().toString());
							lblEmployeeName.get(rowcount).setValue(lblEmployeeName.get(rowcount+1).getValue().toString());
							lblDesignation.get(rowcount).setValue(lblDesignation.get(rowcount+1).getValue().toString());
							
							lblBasic.get(rowcount).setValue(lblBasic.get(rowcount+1).getValue().toString());
							lblHouseRent.get(rowcount).setValue(lblHouseRent.get(rowcount+1).getValue().toString());
							lblConveyance.get(rowcount).setValue(lblConveyance.get(rowcount+1).getValue().toString());
							lblMedicalAll.get(rowcount).setValue(lblMedicalAll.get(rowcount+1).getValue().toString());
							lblOthersAll.get(rowcount).setValue(lblOthersAll.get(rowcount+1).getValue().toString());
							lblSpecialAll.get(rowcount).setValue(lblSpecialAll.get(rowcount+1).getValue().toString());
							lblGross.get(rowcount).setValue(lblGross.get(rowcount+1).getValue().toString());
							
							lblAttBonus.get(rowcount).setValue(lblAttBonus.get(rowcount+1).getValue().toString());
							lblAbsAmount.get(rowcount).setValue(lblAbsAmount.get(rowcount+1).getValue().toString());
							lblLwpAmount.get(rowcount).setValue(lblLwpAmount.get(rowcount+1).getValue().toString());
							txtLoan.get(rowcount).setValue(txtLoan.get(rowcount+1).getValue().toString());
							txtAdvSalary.get(rowcount).setValue(txtAdvSalary.get(rowcount+1).getValue().toString());
							lblLateAttAmount.get(rowcount).setValue(lblLateAttAmount.get(rowcount+1).getValue().toString());
							//txtCompensation.get(rowcount).setValue(txtCompensation.get(rowcount+1).getValue().toString());
							lblIncomeTax.get(rowcount).setValue(lblIncomeTax.get(rowcount+1).getValue().toString());
							txtTotal.get(rowcount).setValue(txtTotal.get(rowcount+1).getValue().toString());
							lblLoanId.get(rowcount).setValue(lblLoanId.get(rowcount+1).getValue().toString());
							lblTransactionId.get(rowcount).setValue(lblTransactionId.get(rowcount+1).getValue().toString());

							lblAutoEmployeeID.get(rowcount+1).setValue("");
							lblEmployeeID.get(rowcount+1).setValue("");
							lblFingerID.get(rowcount+1).setValue("");
							lblEmployeeName.get(rowcount+1).setValue("");
							lblDesignation.get(rowcount+1).setValue("");
							
							lblBasic.get(rowcount+1).setValue("");
							lblHouseRent.get(rowcount+1).setValue("");
							lblConveyance.get(rowcount+1).setValue("");
							lblMedicalAll.get(rowcount+1).setValue("");
							lblOthersAll.get(rowcount+1).setValue("");
							lblSpecialAll.get(rowcount+1).setValue("");
							lblGross.get(rowcount+1).setValue("");
							
							lblAttBonus.get(rowcount+1).setValue("");
							lblAbsAmount.get(rowcount+1).setValue("");
							lblLwpAmount.get(rowcount+1).setValue("");
							txtLoan.get(rowcount+1).setValue("");
							txtAdvSalary.get(rowcount+1).setValue("");
							lblLateAttAmount.get(rowcount+1).setValue("");
							//txtCompensation.get(rowcount+1).setValue("");
							//txtMobileBillExtra.get(rowcount+1).setValue("");
							lblIncomeTax.get(rowcount+1).setValue("");
							txtTotal.get(rowcount+1).setValue("");
							lblLoanId.get(rowcount+1).setValue("");
							lblTransactionId.get(rowcount+1).setValue("");
						}
					}
				}

			}
		});

		lblsa.add(ar,new Label());
		lblsa.get(ar).setWidth("100%");
		lblsa.get(ar).setHeight("16px");
		lblsa.get(ar).setValue(ar+1);

		lblAutoEmployeeID.add(ar, new Label());
		lblAutoEmployeeID.get(ar).setWidth("100%");

		lblEmployeeID.add(ar, new Label());
		lblEmployeeID.get(ar).setWidth("100%");

		lblFingerID.add(ar, new Label());
		lblFingerID.get(ar).setWidth("100%");

		lblEmployeeName.add(ar,new Label());
		lblEmployeeName.get(ar).setWidth("100%");

		lblDesignation.add(ar, new Label());
		lblDesignation.get(ar).setWidth("100%");

		lblBasic.add(ar, new Label());
		lblBasic.get(ar).setWidth("100%");

		lblHouseRent.add(ar, new Label());
		lblHouseRent.get(ar).setWidth("100%");

		lblConveyance.add(ar, new Label());
		lblConveyance.get(ar).setWidth("100%");

		lblMedicalAll.add(ar, new Label());
		lblMedicalAll.get(ar).setWidth("100%");

		lblOthersAll.add(ar, new Label());
		lblOthersAll.get(ar).setWidth("100%");

		lblSpecialAll.add(ar, new Label());
		lblSpecialAll.get(ar).setWidth("100%");

		lblGross.add(ar, new Label());
		lblGross.get(ar).setWidth("100%");

		lblAttBonus.add(ar, new Label());
		lblAttBonus.get(ar).setWidth("100%");
		lblAttBonus.get(ar).setImmediate(true);

		lblAbsAmount.add(ar, new Label());
		lblAbsAmount.get(ar).setWidth("100%");
		lblAbsAmount.get(ar).setImmediate(true);
		/*lblAbsAmount.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!lblAbsAmount.get(ar).getValue().toString().trim().equals(""))
				{
					TotalCalculation(ar);
					txtLoan.get(ar).focus();
				}			
			}
		});*/

		lblLwpAmount.add(ar, new Label());
		lblLwpAmount.get(ar).setWidth("100%");
		lblLwpAmount.get(ar).setImmediate(true);

		txtLoan.add(ar, new AmountCommaSeperator());
		txtLoan.get(ar).setWidth("100%");
		txtLoan.get(ar).setImmediate(true);
		txtLoan.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtLoan.get(ar).getValue().toString().trim().equals(""))
				{
					TotalCalculation(ar);
					txtAdvSalary.get(i).focus();
				}			
			}
		});

		txtAdvSalary.add(ar, new AmountCommaSeperator());
		txtAdvSalary.get(ar).setWidth("100%");
		txtAdvSalary.get(ar).setImmediate(true);
		txtAdvSalary.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtAdvSalary.get(ar).getValue().toString().trim().equals(""))
				{
					TotalCalculation(ar);
					//lblLateAttAmount.get(ar).focus();
					/*chkLoanPerInstallMent(ar);*/
				}			
			}
		});

		lblLateAttAmount.add(ar, new Label());
		lblLateAttAmount.get(ar).setWidth("100%");
		lblLateAttAmount.get(ar).setImmediate(true);
		/*lblLateAttAmount.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!lblLateAttAmount.get(ar).getValue().toString().trim().equals(""))
				{
					TotalCalculation(ar);
					//txtCompensation.get(ar).focus();
				}			
			}
		});*/

		/*txtCompensation.add(ar, new AmountCommaSeperator());
		txtCompensation.get(ar).setWidth("100%");
		txtCompensation.get(ar).setImmediate(true);
		txtCompensation.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtCompensation.get(ar).getValue().toString().trim().equals(""))
				{
					TotalCalculation(ar);
					txtAdvSalary.get(ar+1).focus();
				}			
			}
		});
		
		txtMobileBillExtra.add(ar, new AmountCommaSeperator());
		txtMobileBillExtra.get(ar).setWidth("100%");
		txtMobileBillExtra.get(ar).setImmediate(true);
		txtMobileBillExtra.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtMobileBillExtra.get(ar).getValue().toString().trim().equals(""))
				{
					TotalCalculation(ar);
				}			
			}
		});*/

		lblIncomeTax.add(ar, new Label());
		lblIncomeTax.get(ar).setWidth("100%");
		lblIncomeTax.get(ar).setImmediate(true);
		lblIncomeTax.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!lblIncomeTax.get(ar).getValue().toString().trim().equals(""))
				{
					TotalCalculation(ar);
				}			
			}
		});

		txtTotal.add(ar, new Label());
		txtTotal.get(ar).setWidth("100%");
		txtTotal.get(ar).setImmediate(true);


		lblLoanId.add(ar, new Label());
		lblLoanId.get(ar).setWidth("100%");

		lblTransactionId.add(ar, new Label());
		lblTransactionId.get(ar).setWidth("100%");
		TotalCalculation(ar);

		table.addItem(new Object[]{btnDel.get(ar),lblsa.get(ar),lblAutoEmployeeID.get(ar),lblEmployeeID.get(ar),lblFingerID.get(ar),
				lblEmployeeName.get(ar),lblDesignation.get(ar),lblBasic.get(ar),lblHouseRent.get(ar),lblConveyance.get(ar),lblMedicalAll.get(ar),
				lblOthersAll.get(ar),lblSpecialAll.get(ar),lblGross.get(ar),lblAttBonus.get(ar),lblAbsAmount.get(ar),lblLwpAmount.get(ar),txtLoan.get(ar),
				txtAdvSalary.get(ar),lblLateAttAmount.get(ar)/*,txtCompensation.get(ar),txtMobileBillExtra.get(ar)*/,lblIncomeTax.get(ar)
				,txtTotal.get(ar),lblLoanId.get(ar),lblTransactionId.get(ar)},ar);
	}

	private void TotalCalculation(int ar)
	{
		double gross = Double.parseDouble("0"+lblGross.get(ar).getValue().toString().replaceFirst(",", ""));
		double presentbonus = Double.parseDouble("0"+lblAttBonus.get(ar).getValue().toString().replaceFirst(",", ""));
		double absAmount = Double.parseDouble("0"+lblAbsAmount.get(ar).getValue().toString().replaceFirst(",", ""));
		double lwpAmount = Double.parseDouble("0"+lblLwpAmount.get(ar).getValue().toString().replaceFirst(",", ""));
		
		double loan = Double.parseDouble("0"+txtLoan.get(ar).getValue().toString().replaceFirst(",", ""));
		double advancedsalary = Double.parseDouble("0"+txtAdvSalary.get(ar).getValue().toString().replaceFirst(",", ""));

		double lateAmount = Double.parseDouble("0"+lblLateAttAmount.get(ar).getValue().toString().replaceFirst(",", ""));
		double incomeTax = Double.parseDouble("0"+lblIncomeTax.get(ar).getValue().toString().replaceFirst(",", ""));
		
		/*double compensation = Double.parseDouble("0"+txtCompensation.get(ar).getValue().toString().replaceFirst(",", ""));
		double extramobilebill = Double.parseDouble("0"+txtMobileBillExtra.get(ar).getValue().toString().replaceFirst(",", ""));*/
		
		txtTotal.get(ar).setValue(cs.setComma((gross+presentbonus)-(absAmount+lwpAmount+loan+advancedsalary+lateAmount+incomeTax)));
		System.out.println(txtTotal);
	}

	/*private void chkLoanPerInstallMent(int i)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select ISNULL(mAmountPerInstall,0) from tbLoanApplication where vLoanNo='"+txtLoan.get(i).getValue().toString().trim()+"' and vAutoEmployeeId='"+lblAutoEmployeeID.get(i).getValue().toString().trim()+"'";
			List <?> lst=session.createSQLQuery(query).list();
			double loanAmount=0.0;
			if(!lst.isEmpty())
			{
				loanAmount=Double.parseDouble(lst.iterator().next().toString().trim());
			}
			if(loanAmount<Double.parseDouble((txtAdvSalary.get(i).getValue().toString().trim().length()==0?"0":txtAdvSalary.get(i).getValue().toString().trim())))
			{
				txtAdvSalary.get(i).setValue(0);
				showNotification("Warning", "Amount is Greater than Loan Per Installment!!!", Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception exp)
		{
			showNotification("chkLoanPerInstallMent", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}*/


	private AbsoluteLayout buildMainLayout() 
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);
		mainLayout.setWidth("1200px");
		mainLayout.setHeight("550px");

		lblDate = new Label("Salary Month :");
		lblDate.setImmediate(false);
		lblDate.setWidth("-1px");
		lblDate.setHeight("-1px");
		mainLayout.addComponent(lblDate, "top:20.0px; left:40.0px;");

		dWorkingDate = new PopupDateField();
		dWorkingDate.setImmediate(true);
		dWorkingDate.setWidth("140px");
		dWorkingDate.setDateFormat("MMMMM-yyyy");
		dWorkingDate.setValue(new java.util.Date());
		dWorkingDate.setResolution(PopupDateField.RESOLUTION_MONTH);
		mainLayout.addComponent(dWorkingDate, "top:18.0px; left:150.0px;");

		
		lblUnit = new Label("Project :");
		lblUnit.setImmediate(false); 
		lblUnit.setWidth("-1px");
		lblUnit.setHeight("-1px");
		mainLayout.addComponent(lblUnit, "top:45px; left:40.0px;");

		csbUnit = new ComboBox();
		csbUnit.setImmediate(true);
		csbUnit.setWidth("280px");
		csbUnit.setHeight("24px");
		csbUnit.setNullSelectionAllowed(true);
		csbUnit.setNewItemsAllowed(false);
		csbUnit.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(csbUnit, "top:43px; left:150.0px;");
		
		mainLayout.addComponent(new Label("Department :"), "top:70px; left:40.0px;");

		csbDepartment = new ComboBox();
		csbDepartment.setImmediate(true);
		csbDepartment.setWidth("280px");
		csbDepartment.setHeight("24px");
		csbDepartment.setNullSelectionAllowed(true);
		csbDepartment.setNewItemsAllowed(false);
		csbDepartment.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(csbDepartment, "top:68px; left:150.0px;");
		
		chkDepartmentAll=new CheckBox("All");
		chkDepartmentAll.setImmediate(true);
		chkDepartmentAll.setWidth("-1px");
		chkDepartmentAll.setHeight("-1px");
		mainLayout.addComponent(chkDepartmentAll, "top:70px; left:435px;");
		
		lblSection = new Label("Section :");
		lblSection.setImmediate(false); 
		lblSection.setWidth("-1px");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection, "top:95px; left:40.0px;");

		csbSection = new ComboBox();
		csbSection.setImmediate(true);
		csbSection.setWidth("280px");
		csbSection.setHeight("24px");
		csbSection.setNullSelectionAllowed(true);
		csbSection.setNewItemsAllowed(false);
		csbSection.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(csbSection, "top:93px; left:150.0px;");
		
		chkSectionAll=new CheckBox("All");
		chkSectionAll.setImmediate(true);
		chkSectionAll.setWidth("-1px");
		chkSectionAll.setHeight("-1px");
		mainLayout.addComponent(chkSectionAll, "top:95px; left:435px;");

		lblNoteSalary.setStyleName("lbltxtColor");
		lblNoteSalary.setImmediate(true);
		mainLayout.addComponent(lblNoteSalary, "top:120px;left:550.0px;");

		lblEmployee = new Label("Employee ID :");
		lblEmployee.setImmediate(false); 
		lblEmployee.setWidth("-1px");
		lblEmployee.setHeight("-1px");
		mainLayout.addComponent(lblEmployee, "top:118px; left:40.0px;");

		csbEmployee = new ComboBox();
		csbEmployee.setImmediate(true);
		csbEmployee.setWidth("280px");
		csbEmployee.setHeight("24px");
		csbEmployee.setNullSelectionAllowed(true);
		csbEmployee.setNewItemsAllowed(false);
		csbEmployee.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(csbEmployee, "top:120px; left:150.0px;");

		chkEmployeeAll = new CheckBox("All");
		chkEmployeeAll.setImmediate(true);
		chkEmployeeAll.setHeight("-1px");
		chkEmployeeAll.setWidth("-1px");
		mainLayout.addComponent(chkEmployeeAll, "top:120px; left:435.0px;");

		lblNoteSalary2.setStyleName("lbltxtColor");
		lblNoteSalary2.setImmediate(true);
		mainLayout.addComponent(lblNoteSalary2, "top:95px;left:550.0px;");

		table.setWidth("1160px");
		table.setHeight("340px");
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("Del", NativeButton.class, new NativeButton());
		table.setColumnWidth("Del", 30);

		table.addContainerProperty("SL", Label.class, new Label());
		table.setColumnWidth("SL", 20);

		table.addContainerProperty("EMP ID", Label.class, new Label());
		table.setColumnWidth("EMP ID", 60);

		table.addContainerProperty("EMPLOYEE ID", Label.class, new Label());
		table.setColumnWidth("EMPLOYEE ID", 60);

		table.addContainerProperty("Finger ID", Label.class, new Label());
		table.setColumnWidth("Finger ID", 60);

		table.addContainerProperty("Employee Name", Label.class, new Label());
		table.setColumnWidth("Employee Name",  150);

		table.addContainerProperty("Designation", Label.class, new Label());
		table.setColumnWidth("Designation", 100);

		table.addContainerProperty("Basic", Label.class, new Label());
		table.setColumnWidth("Basic", 60);

		table.addContainerProperty("HR", Label.class, new Label());
		table.setColumnWidth("HR", 60);

		table.addContainerProperty("Conv.", Label.class, new Label());
		table.setColumnWidth("Conv.", 60);

		table.addContainerProperty("MA", Label.class, new Label());
		table.setColumnWidth("MA", 60);
		
		//--------as per decesion Nazim Sir and NK group (Other ->Mobile) 13-03-2019--------//
		table.addContainerProperty("Mobile", Label.class, new Label());
		table.setColumnWidth("Mobile", 60);
		//--------as per decesion Nazim Sir and NK group (Other ->Mobile) 13-03-2019--------//

		table.addContainerProperty("Sepcial", Label.class, new Label());
		table.setColumnWidth("Sepcial", 60);

		table.addContainerProperty("Gross", Label.class, new Label());
		table.setColumnWidth("Gross", 70);

		table.addContainerProperty("Attn. Bonus", Label.class, new Label());
		table.setColumnWidth("Attn. Bonus", 50);

		table.addContainerProperty("Abs. Amount", Label.class, new Label());
		table.setColumnWidth("Abs. Amount", 50);

		table.addContainerProperty("LWP. Amount", Label.class, new Label());
		table.setColumnWidth("LWP. Amount", 50);

		table.addContainerProperty("Loan", AmountCommaSeperator.class, new AmountCommaSeperator());
		table.setColumnWidth("Loan", 70);

		table.addContainerProperty("Adv. Sal.", AmountCommaSeperator.class, new AmountCommaSeperator());
		table.setColumnWidth("Adv. Sal.", 70);

		table.addContainerProperty("Late. Att", Label.class, new Label());
		table.setColumnWidth("Late. Att", 50);

		/*table.addContainerProperty("Comp.", AmountCommaSeperator.class, new AmountCommaSeperator());
		table.setColumnWidth("Comp.", 70);
		
		table.addContainerProperty("Mobile", AmountCommaSeperator.class, new AmountCommaSeperator());
		table.setColumnWidth("Mobile", 50);*/

		table.addContainerProperty("Income Tax", Label.class, new Label());
		table.setColumnWidth("Income Tax", 60);

		table.addContainerProperty("Total", Label.class, new Label());
		table.setColumnWidth("Total", 90);

		table.addContainerProperty("LoanId", Label.class, new Label());
		table.setColumnWidth("LoanId", 60);

		table.addContainerProperty("Transaction Id", Label.class, new Label());
		table.setColumnWidth("Transaction Id", 60);

		table.setColumnAlignments(new String[] {Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT, Table.ALIGN_LEFT,
				Table.ALIGN_LEFT, Table.ALIGN_RIGHT, Table.ALIGN_RIGHT,	Table.ALIGN_RIGHT, Table.ALIGN_RIGHT,Table.ALIGN_RIGHT,Table.ALIGN_RIGHT,
				Table.ALIGN_RIGHT,Table.ALIGN_RIGHT,Table.ALIGN_RIGHT,Table.ALIGN_RIGHT, 
				Table.ALIGN_CENTER, Table.ALIGN_CENTER, Table.ALIGN_CENTER, Table.ALIGN_CENTER,Table.ALIGN_LEFT,Table.ALIGN_CENTER,Table.ALIGN_RIGHT});

		table.setColumnCollapsed("EMP ID", true);
		table.setColumnCollapsed("Finger ID", true);

		mainLayout.addComponent(table,"top:150px; left:20.0px;");
		table.setColumnCollapsed("HR", true);
		table.setColumnCollapsed("Conv.", true);
		table.setColumnCollapsed("MA", true);
		table.setColumnCollapsed("Mobile", true);
		table.setColumnCollapsed("Sepcial", true);

		table.setColumnCollapsed("LoanId", true);
		table.setColumnCollapsed("Transaction Id", true);
		
		table.setStyleName("wordwrap-headers");
		
		mainLayout.addComponent(button,"bottom:15px; left:440.0px");
		return mainLayout;
	}
}