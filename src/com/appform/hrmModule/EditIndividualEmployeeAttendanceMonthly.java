package com.appform.hrmModule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.vaadin.autoreplacefield.NumberField;

import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.SessionFactoryUtil;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.common.share.SessionBean;
import java.text.SimpleDateFormat;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

@SuppressWarnings("serial")
public class EditIndividualEmployeeAttendanceMonthly extends Window 
{
	private AbsoluteLayout mainLayout;

	private SessionBean sessionBean;
	private Label lblMonth = new Label();
	private PopupDateField dMonth = new PopupDateField();

	private Label lblSection = new Label();
	private ComboBox cmbSection = new ComboBox();

	private List<?> employeeList = Arrays.asList(new Object[]{"Employee ID","Finger ID","Employee Name"});
	private OptionGroup opgEmployee;
	private Label lblEmployee = new Label();
	private ComboBox cmbEmployee = new ComboBox();

	private Table table = new Table();
	private ArrayList<PopupDateField> tbDate = new ArrayList<PopupDateField>();
	private ArrayList<Label> tbLblDayName = new ArrayList<Label>();
	private ArrayList<Label> tbLblStatus = new ArrayList<Label>();
	private ArrayList<NumberField> tbInHour = new ArrayList<NumberField>();
	private ArrayList<NumberField> tbInMin = new ArrayList<NumberField>();
	private ArrayList<NumberField> tbInSec = new ArrayList<NumberField>();
	private ArrayList<NumberField> tbOutHour = new ArrayList<NumberField>();
	private ArrayList<NumberField> tbOutMin = new ArrayList<NumberField>();
	private ArrayList<NumberField> tbOutSec = new ArrayList<NumberField>();
	private ArrayList<TextField> tbTxtReason = new ArrayList<TextField>();
	private ArrayList<TextField> tbTxtPermittedBy = new ArrayList<TextField>();

	private CommonButton cButton = new CommonButton("New", "Save", "", "","Refresh","","","","","Exit");
	ArrayList<Component> allComp = new ArrayList<Component>();
	private SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dbYearFormat = new SimpleDateFormat("yyyy");
	private SimpleDateFormat dbMonthFormat = new SimpleDateFormat("MM");

	private String notify="";

	LinkedHashMap<String, String> hmEmployeeInfo=new LinkedHashMap<String, String>();

	public EditIndividualEmployeeAttendanceMonthly(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("EDIT MONTHLY EMPLOYEE ATTENDANCE (INDIVIDUAL) :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);
		tableInitialize();

		btnIni(true);
		componentIni(true);
		cButtonAction();
		focusEnter();
		authenticationCheck();
		cmbSectionValueAdd();

		cButton.btnNew.focus();
	}

	private void authenticationCheck()
	{
		if(!sessionBean.isSubmitable()){
			cButton.btnSave.setVisible(false);
		}

		if(!sessionBean.isUpdateable()){
			cButton.btnEdit.setVisible(false);
		}

		if(!sessionBean.isDeleteable()){
			cButton.btnDelete.setVisible(false);
		}
	}

	private void cButtonAction()
	{
		dMonth.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSection.removeAllItems();
				if(dMonth.getValue()!=null)
				{
					cmbSectionValueAdd();
				}
			}
		});

		cmbSection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				opgEmployee.setValue("Employee ID");
				cmbEmployee.removeAllItems();
				if(cmbSection.getValue()!=null)
				{
					cmbEmployeeAddData(opgEmployee.getValue().toString());
				}
			}
		});

		opgEmployee.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				String type=opgEmployee.getValue().toString();
				cmbEmployee.removeAllItems();
				cmbEmployeeAddData(type);
			}
		});

		cmbEmployee.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				tableclear();
				addTableData();
				addValueToHashMap();
			}
		});

		cButton.btnNew.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				newButtonEvent();
			}
		});

		cButton.btnSave.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(formValidation())
					saveButtonAction();
				else
					showNotification("Warning",notify,Notification.TYPE_WARNING_MESSAGE);
			}
		});

		cButton.btnRefresh.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				refreshButtonEvent();
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

	private void cmbSectionValueAdd()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vSectionID,vSectionName from tbEmployeeAttendanceFinal where " +
					"MONTH(dDate)='"+dbMonthFormat.format(dMonth.getValue())+"' " +
					"and Year(dDate)='"+dbYearFormat.format(dMonth.getValue())+"' order by vSectionName";
			List<?> lst=session.createSQLQuery(query).list();
			for(Iterator<?> itr=lst.iterator();itr.hasNext();)
			{
				Object[] element=(Object[])itr.next();
				cmbSection.addItem(element[0]);
				cmbSection.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbSectionValueAdd",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void cmbEmployeeAddData(String type)
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="";
			if(type=="Employee ID")
			{
				query="select distinct vEmployeeID,vEmployeeCode,vEmployeeName from tbEmployeeAttendanceFinal where " +
						"MONTH(dDate)='"+dbMonthFormat.format(dMonth.getValue())+"' " +
						"and Year(dDate)='"+dbYearFormat.format(dMonth.getValue())+"' " +
						"and vSectionID = '"+cmbSection.getValue()+"' order by vEmployeeName";
				lblEmployee.setValue("Employee ID : ");
			}
			else if(type=="Finger ID")
			{
				query="select distinct vEmployeeID,vFingerID,vEmployeeName from tbEmployeeAttendanceFinal where " +
						"MONTH(dDate)='"+dbMonthFormat.format(dMonth.getValue())+"' " +
						"and Year(dDate)='"+dbYearFormat.format(dMonth.getValue())+"' " +
						"and vSectionID = '"+cmbSection.getValue()+"' order by vEmployeeName";
				lblEmployee.setValue("Finger ID : ");
			}
			else if(type=="Employee Name")
			{
				query="select distinct vEmployeeID,vEmployeeName from tbEmployeeAttendanceFinal where " +
						"MONTH(dDate)='"+dbMonthFormat.format(dMonth.getValue())+"' " +
						"and Year(dDate)='"+dbYearFormat.format(dMonth.getValue())+"' " +
						"and vSectionID = '"+cmbSection.getValue()+"' order by vEmployeeName";
				lblEmployee.setValue("Employee Name : ");
			}

			List<?> lst=session.createSQLQuery(query).list();
			for(Iterator<?> itr=lst.iterator();itr.hasNext();)
			{
				Object[] element=(Object[])itr.next();
				cmbEmployee.addItem(element[0]);
				cmbEmployee.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbEmployeeAddData",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void addTableData()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select dTxtDate,CONVERT(varchar,DATENAME(WEEKDAY,dTxtDate)) vTxtDay,DATEPART(HH,dInTime) InHour,DATEPART(MI,dInTime) InMin,"
					+ " DATEPART(SS,dInTime) InSec,DATEPART(HH,dOutTime) outHour,DATEPART(MI,dOutTime) outMin,"
					+ " DATEPART(SS,dOutTime) outSec,ISNULL((case when vTxtStatus like '%Holiday%' Then 'H' when "
					+ " vTxtStatus like 'Present' then 'P' when vTxtStatus like '%Leave%' then 'L' when "
					+ " vTxtStatus like '%Absent%' then 'A' end),'A') TxtStatus from funMonthlyEmployeeAttendance"
					+ " ('"+dbDateFormat.format(dMonth.getValue())+"','"+cmbEmployee.getValue()+"',"
					+ " '"+cmbSection.getValue()+"') order by dTxtDate";
			List<?> lst = session.createSQLQuery(query).list();
			int i=0;
			for(Iterator<?> itr=lst.iterator();itr.hasNext();)
			{
				Object [] element = (Object [])itr.next();
				tbDate.get(i).setReadOnly(false);
				tbDate.get(i).setValue(element[0]);
				tbDate.get(i).setReadOnly(true);
				tbLblDayName.get(i).setValue(element[1]);
				tbLblStatus.get(i).setValue(element[8]);

				tbInHour.get(i).setValue(element[2]!=null?element[2]:"");
				tbInMin.get(i).setValue(element[3]!=null?element[3]:"");
				tbInSec.get(i).setValue(element[4]!=null?element[4]:"");

				tbOutHour.get(i).setValue(element[5]!=null?element[5]:"");
				tbOutMin.get(i).setValue(element[6]!=null?element[6]:"");
				tbOutSec.get(i).setValue(element[7]!=null?element[7]:"");

				if(element[8].toString().equalsIgnoreCase("H"))
				{
					tbLblDayName.get(i).setStyleName("lbStyleGreen");
					tbLblStatus.get(i).setStyleName("lbStyleGreen");
				}
				else if(element[8].toString().equalsIgnoreCase("L"))
				{
					tbLblDayName.get(i).setStyleName("lbStyleOliveGreen");
					tbLblStatus.get(i).setStyleName("lbStyleOliveGreen");
				}
				else if(element[8].toString().equalsIgnoreCase("A"))
				{
					tbLblDayName.get(i).setStyleName("lbStyleMaroon");
					tbLblStatus.get(i).setStyleName("lbStyleMaroon");
				}
				else if(element[8].toString().equalsIgnoreCase("P"))
				{
					tbLblDayName.get(i).setStyleName("lbStyleNormal");
					tbLblStatus.get(i).setStyleName("lbStyleNormal");
				}
				if(i==tbDate.size()-1)
					tableRowAdd(i+1);
				i++;
			}
		}
		catch (Exception exp)
		{
			showNotification("addTableData", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void addValueToHashMap()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select ein.employeeCode,ein.iFingerID,ein.vProximityId,ein.vEmployeeName,ein.vDesignationId," +
					" din.designationName,otStatus,vFloor from tbEmployeeInfo ein inner join tbDesignationInfo din on " +
					" ein.vDesignationId=din.designationId where ein.vEmployeeId = '"+cmbEmployee.getValue()+"'";
			List<?> lstInfo=session.createSQLQuery(query).list();
			if(!lstInfo.isEmpty())
			{
				Object [] element = (Object[])lstInfo.iterator().next(); 
				hmEmployeeInfo.put("1", element[0].toString());
				hmEmployeeInfo.put("2", element[1].toString());
				hmEmployeeInfo.put("3", element[2].toString());
				hmEmployeeInfo.put("4", element[3].toString());
				hmEmployeeInfo.put("5", element[4].toString());
				hmEmployeeInfo.put("6", element[5].toString());
				hmEmployeeInfo.put("7", element[6].toString());
				hmEmployeeInfo.put("8", element[7].toString());
			}
		}
		catch (Exception exp)
		{
			showNotification("addValueToHashMap", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void newButtonEvent() 
	{
		dMonth.focus();
		componentIni(false);
		btnIni(false);
	}

	private boolean formValidation()
	{
		boolean ret=false;
		int count=0;
		for(int i=0;i<tbDate.size();i++)
		{
			if(tbDate.get(i).getValue()!=null)
			{
				if(tbTxtReason.get(i).getValue().toString().trim().length()>0)
				{
					if(tbTxtPermittedBy.get(i).getValue().toString().trim().length()>0)
					{
						if(tbInHour.get(i).getValue().toString().trim().length()>0 && 
								tbInMin.get(i).getValue().toString().trim().length()>0 && 
								tbInSec.get(i).getValue().toString().trim().length()>0 && 
								tbInMin.get(i).getValue().toString().trim().length()>0 && 
								tbOutMin.get(i).getValue().toString().trim().length()>0 && 
								tbOutSec.get(i).getValue().toString().trim().length()>0)
						{
							ret=true;
							break;
						}
						else
						{
							notify="Please Provide Attendance Time!!!";
						}
					}
					else
					{
						notify="Please Provide Reason!!!";
						tbTxtPermittedBy.get(i).focus();
					}
				}
				else
				{
					notify="Please Provide Permitted By!!!";
					tbTxtReason.get(i).focus();
				}
				count++;
			}
		}
		if(count==0)
			notify="No Data Found!!!";
		return ret;
	}

	private void saveButtonAction()
	{
		try
		{
			MessageBox mb = new MessageBox(getParent(), "Are You Sure?", MessageBox.Icon.QUESTION, "Do You Want to Save Information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "NO"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType==ButtonType.YES)
					{
						insertData();
					}
				}
			});
		}
		catch(Exception ex)
		{
			showNotification("saveButtonAction", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void refreshButtonEvent() 
	{
		txtClear();
		componentIni(true);
		btnIni(true);
	}

	private void insertData()
	{
		/*--------------------------------------------------------Attendance---------------------------------------------------------*/			
		String query="";
		String deleteQuery = "";
		String InTimeOne="";
		String OutTimeOne="";
		int workingTime=0;
		int actualWorkingTime = 0;
		String AttendanceFlag="";
		String shiftID = "";
		String shiftName = "";

		for(int i=0;i<tbDate.size();i++)
		{
			if(tbDate.get(i).getValue()!=null)
			{
				if(tbTxtReason.get(i).getValue().toString().trim().length()>0 && tbTxtPermittedBy.get(i).getValue().toString().trim().length()>0)
				{
					Session session=SessionFactoryUtil.getInstance().openSession();
					Transaction tx=session.beginTransaction();
					try
					{
						InTimeOne=dbDateFormat.format(tbDate.get(i).getValue())+" "+tbInHour.get(i).getValue().toString()+":"+tbInMin.get(i).getValue().toString()+":"+tbInSec.get(i).getValue().toString();
						OutTimeOne=dbDateFormat.format(tbDate.get(i).getValue())+" "+tbOutHour.get(i).getValue().toString()+":"+tbOutMin.get(i).getValue().toString()+":"+tbOutSec.get(i).getValue().toString();

						String shiftQuery = "select vShiftID,vShiftName from [dbo].[funGetEmployeeShift]('"+hmEmployeeInfo.get("8")+"','"+InTimeOne+"')";
						System.out.println("shiftQuery"+shiftQuery);
						List <?> lstShift = session.createSQLQuery(shiftQuery).list();
						for(Iterator <?> itr = lstShift.iterator();itr.hasNext();)
						{
							Object [] element = (Object[])itr.next();
							shiftID = element[0].toString();
							shiftName = element[1].toString();
						}

						String holidayCheck = "select dDate from tbHoliday where dDate='"+tbDate.get(i).getValue()+"'";
						System.out.println("holidayCheck"+holidayCheck);
						List<?> lstHoliday=session.createSQLQuery(holidayCheck).list();
						if(!lstHoliday.isEmpty())
							AttendanceFlag="HP";
						else
						{
							String actualWorkingHour = "select DATEDIFF(SS,dshiftStart,dshiftEnd) from tbGroupShiftInfo where vShiftID = '"+shiftID+"'";
							System.out.println("actualWorkingHour"+actualWorkingHour);
							actualWorkingTime = Integer.parseInt(session.createSQLQuery(actualWorkingHour).list().iterator().next().toString());

							String workingHourCheck = "select DATEDIFF(SS,'"+InTimeOne+"','"+OutTimeOne+"')";
							System.out.println("workingHourCheck"+workingHourCheck);
							workingTime=Integer.parseInt(session.createSQLQuery(workingHourCheck).list().iterator().next().toString());

							if(workingTime>=actualWorkingTime)
								AttendanceFlag="PR";
							else
								AttendanceFlag="SA";
						}
						/*--------------------------------------------------------Attendance EDIT & Delete---------------------------------------------------------*/
						String checkQuery="select * from tbEmployeeAttendanceFinal where dDate = '"+dbDateFormat.format(tbDate.get(i).getValue())+"'";
						System.out.println("checkQuery"+checkQuery);
						List<?> chkList = session.createSQLQuery(checkQuery).list();
						if(!chkList.isEmpty())
						{
							String updateQuery1 = "Insert into tbUDEmployeeAttendance(dDate,vEmployeeId,vEmployeeCode,iFingerId,vEmployeeName,"
									+ " vDesignationID,vDesignation,vSectionId,vSectionName,dAttDate,dAttInTime,dAttOutTime,permittedBy,vReason,"
									+ " vShiftId,vShiftName,udFlag,vPAFlag,vUserId,vUserIP,dEntryTime) select dDate,vEmployeeID,vEmployeeCode,Isnull(vFingerID,'') vFingerID,"
									+ " vEmployeeName,vDesignationID,vDesignationName,vSectionId,vSectionName,dDate,dInTimeFirst,dOutTimeFirst,"
									+ " '','',vShiftID,vShiftName,'UPDATE',vAttendFlag,Isnull(vUserId,'')vUserId,Isnull(vUserIp,'')vUserIp,Isnull(dEntryTime,CURRENT_TIMESTAMP)dEntryTime from tbEmployeeAttendanceFinal where dDate "
									+ " ='"+dbDateFormat.format(tbDate.get(i).getValue())+"' and vEmployeeID = '"+cmbEmployee.getValue()+"'";
							session.createSQLQuery(updateQuery1).executeUpdate();
							
							deleteQuery = "delete from tbEmployeeAttendanceFinal where dDate = '"+dbDateFormat.format(tbDate.get(i).getValue())+"' " +
									"and vEmployeeID = '"+cmbEmployee.getValue()+"'";
							session.createSQLQuery(deleteQuery).executeUpdate();
						}
						/*--------------------------------------------------------Attendance EDIT & Delete---------------------------------------------------------*/

						query="Insert into tbEmployeeAttendanceFinal (dDate,vEmployeeID,vEmployeeCode,vProximityID,vFingerID,"
								+ " vEmployeeName,vSectionId,vSectionName,vDesignationID,vDesignationName,iDesignationSerial,vShiftID,"
								+ " vShiftName,dInTimeFirst,dOutTimeFirst,vEditFlag,vAttendFlag,bOtStatus,vUserId,vUserIp,dEntryTime) "
								+ " values ('"+dbDateFormat.format(tbDate.get(i).getValue())+"',"
								+ "'"+cmbEmployee.getValue()+"',"
								+ "'"+hmEmployeeInfo.get("1")+"',"
								+ "'"+hmEmployeeInfo.get("3")+"',"
								+ "'"+hmEmployeeInfo.get("2")+"',"
								+ "'"+hmEmployeeInfo.get("4")+"',"
								+ "'"+cmbSection.getValue()+"',"
								+ "'"+cmbSection.getItemCaption(cmbSection.getValue())+"',"
								+ "'"+hmEmployeeInfo.get("5")+"',"
								+ "'"+hmEmployeeInfo.get("6")+"',"
								+ "'0','"+shiftID+"','"+shiftName+"',"
								+ "'"+InTimeOne+"','"+OutTimeOne+"',"
								+ "'Edited','"+AttendanceFlag+"','"+hmEmployeeInfo.get("7")+"',"
								+ "'"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',GETDATE())";
						session.createSQLQuery(query).executeUpdate();
						/*--------------------------------------------------------Attendance EDIT---------------------------------------------------------*/

						String updateQuery = "Insert into tbUDEmployeeAttendance(dDate,vEmployeeId,vEmployeeCode,iFingerId,"
								+ " vEmployeeName,vDesignationID,vDesignation,vSectionId,vSectionName,dAttDate,dAttInTime,"
								+ " dAttOutTime,permittedBy,vReason,vShiftId,vShiftName,udFlag,vPAFlag,vUserId,vUserIP,dEntryTime) "
								+ " select dDate,vEmployeeID,vEmployeeCode,vFingerID,vEmployeeName,vDesignationID,vDesignationName,"
								+ " vSectionId,vSectionName,dDate,dInTimeFirst,dOutTimeFirst,'"+tbTxtPermittedBy.get(i).getValue()+"',"
								+ " '"+tbTxtReason.get(i).getValue()+"',vShiftID,vShiftName,'OLD',vAttendFlag,vUserId,vUserIp,dEntryTime "
								+ " from tbEmployeeAttendanceFinal where dDate = '"+dbDateFormat.format(tbDate.get(i).getValue())+"' and vEmployeeID = '"+cmbEmployee.getValue()+"'";
						System.out.println("updateQuery"+updateQuery);
						session.createSQLQuery(updateQuery).executeUpdate();
						/*--------------------------------------------------------Attendance EDIT---------------------------------------------------------*/
						tx.commit();
					}
					catch (Exception exp)
					{
						tx.rollback();
						showNotification("insertData", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
					}
					finally{session.close();}
				}
			}
		}
		/*--------------------------------------------------------------------------Attendance--------------------------------------------------------------------------------------*/

		/*--------------------------------------------------------Salary Calculation---------------------------------------------------------*/
		Session session=SessionFactoryUtil.getInstance().openSession();
		Transaction tx=session.beginTransaction();
		try
		{
			String SalaryCheck="select * from tbMonthlySalary where vEmployeeID='"+cmbEmployee.getValue()+"' " +
					"and MONTH(dSalaryDate)='"+dbMonthFormat.format(dMonth.getValue())+"' and " +
					"YEAR(dSalaryDate)='"+dbYearFormat.format(dMonth.getValue())+"'";
			List<?> lstSalary = session.createSQLQuery(SalaryCheck).list();
			if(!lstSalary.isEmpty())
			{
				String curDateQuery="select convert(varchar,convert(date,GETDATE())) Date1";
				List<?> lstCurDate=session.createSQLQuery(curDateQuery).list();
				String Date = lstCurDate.iterator().next().toString();

				String revenueStampQuery = "select mRevenueStamp from tbMonthlySalary "
						+ " where vEmployeeID='"+cmbEmployee.getValue()+"' and MONTH(dSalaryDate)='"+dbMonthFormat.format(dMonth.getValue())+"' and "
						+ " YEAR(dSalaryDate)='"+dbYearFormat.format(dMonth.getValue())+"'";
				List <?> lstRevenue = session.createSQLQuery(revenueStampQuery).list();
				String revenueStamp = lstRevenue.iterator().next().toString();

				String updateSalaryQuery="Insert into tbUDMonthlySalary (dGenerateDate,dSalaryDate,vMonthName,vYear,vEmployeeID,"
						+ " vEmployeeCode,vFingerId,vProximityID,vEmployeeName,vDesignationID,vDesignationName,vSectionID,"
						+ " vSectionName,iTotalMonthDays,iTotalHoliday,iTotalPresentDays,iTotalAbsentDays,iTotalLeaveDays,"
						+ " iTotalTourDays,mGross,mBasic,mHouseRent,mMedicalAllowance,mConveyance,mMobileBill,mOtherAllowance,"
						+ " mProvidentFund,iOTHour,iOTMin,vLoanNo,vTransactionNo,mAdvanceSalary,mRevenueStamp,vBankID,"
						+ " vBankName,vBranchID,vBranchName,vAccountNo,mIncomeTax,mInsurance,vUserIP,vUserName,dEntryTime,"
						+ " vEmployeeType,UDFlag) select dGenerateDate,dSalaryDate,vMonthName,vYear,vEmployeeID,vEmployeeCode,"
						+ " vFingerId,vProximityID,vEmployeeName,vDesignationID,vDesignationName,vSectionID,vSectionName,"
						+ " iTotalMonthDays,iTotalHoliday,iTotalPresentDays,iTotalAbsentDays,iTotalLeaveDays,iTotalTourDays,"
						+ " mGross,mBasic,mHouseRent,mMedicalAllowance,mConveyance,mMobileBill,mOtherAllowance,mProvidentFund,"
						+ " iOTHour,iOTMin,vLoanNo,vTransactionNo,mAdvanceSalary,mRevenueStamp,vBankID,vBankName,vBranchID,"
						+ " vBranchName,vAccountNo,mIncomeTax,mInsurance,vUserIP,vUserName,dEntryTime,vEmployeeType,'OLD' from "
						+ " tbMonthlySalary where vEmployeeID='"+cmbEmployee.getValue()+"' "
						+ " and MONTH(dSalaryDate)='"+dbMonthFormat.format(dMonth.getValue())+"' and "
						+ " YEAR(dSalaryDate)='"+dbYearFormat.format(dMonth.getValue())+"'";
				session.createSQLQuery(updateSalaryQuery).executeUpdate();

				String deleteSalaryQuery = "delete from tbMonthlySalary where vEmployeeID='"+cmbEmployee.getValue()+"' " +
						"and MONTH(dSalaryDate)='"+dbMonthFormat.format(dMonth.getValue())+"' and " +
						"YEAR(dSalaryDate)='"+dbYearFormat.format(dMonth.getValue())+"'";
				session.createSQLQuery(deleteSalaryQuery).executeUpdate();

				String salaryGenerateQuery = "exec prcCalcMonthlySalary '"+Date+"'," +
						"'"+dbDateFormat.format(dMonth.getValue())+"','"+cmbEmployee.getValue()+"',"
						+ "'"+cmbSection.getValue()+"','"+revenueStamp+"'," +
						"'"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"'";
				session.createSQLQuery(salaryGenerateQuery).executeUpdate();

				String updateSalaryQuery1="Insert into tbUDMonthlySalary (dGenerateDate,dSalaryDate,vMonthName,vYear,vEmployeeID,"
						+ " vEmployeeCode,vFingerId,vProximityID,vEmployeeName,vDesignationID,vDesignationName,vSectionID,"
						+ " vSectionName,iTotalMonthDays,iTotalHoliday,iTotalPresentDays,iTotalAbsentDays,iTotalLeaveDays,"
						+ " iTotalTourDays,mGross,mBasic,mHouseRent,mMedicalAllowance,mConveyance,mMobileBill,mOtherAllowance,"
						+ " mProvidentFund,iOTHour,iOTMin,vLoanNo,vTransactionNo,mAdvanceSalary,mRevenueStamp,vBankID,"
						+ " vBankName,vBranchID,vBranchName,vAccountNo,mIncomeTax,mInsurance,vUserIP,vUserName,dEntryTime,"
						+ " vEmployeeType,UDFlag) select dGenerateDate,dSalaryDate,vMonthName,vYear,vEmployeeID,vEmployeeCode,"
						+ " vFingerId,vProximityID,vEmployeeName,vDesignationID,vDesignationName,vSectionID,vSectionName,"
						+ " iTotalMonthDays,iTotalHoliday,iTotalPresentDays,iTotalAbsentDays,iTotalLeaveDays,iTotalTourDays,"
						+ " mGross,mBasic,mHouseRent,mMedicalAllowance,mConveyance,mMobileBill,mOtherAllowance,mProvidentFund,"
						+ " iOTHour,iOTMin,vLoanNo,vTransactionNo,mAdvanceSalary,mRevenueStamp,vBankID,vBankName,vBranchID,"
						+ " vBranchName,vAccountNo,mIncomeTax,mInsurance,vUserIP,vUserName,dEntryTime,vEmployeeType,'UPDATE' from "
						+ " tbMonthlySalary where vEmployeeID='"+cmbEmployee.getValue()+"' "
						+ " and MONTH(dSalaryDate)='"+dbMonthFormat.format(dMonth.getValue())+"' and "
						+ " YEAR(dSalaryDate)='"+dbYearFormat.format(dMonth.getValue())+"'";
				session.createSQLQuery(updateSalaryQuery1).executeUpdate();
			}
			/*--------------------------------------------------------Salary Calculation---------------------------------------------------------*/

			txtClear();
			componentIni(true);
			btnIni(true);
			showNotification("All Information Saved Successfully.");
			tx.commit();
		}
		catch (Exception exp)
		{
			tx.rollback();
			showNotification("insertData", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void tableInitialize()
	{
		table.setColumnCollapsingAllowed(true);
		table.setWidth("98%");
		table.setHeight("293px");
		table.setImmediate(true);

		table.addContainerProperty("date", PopupDateField.class , new PopupDateField());
		table.setColumnWidth("date",80);

		table.addContainerProperty("Day Name", Label.class , new Label());
		table.setColumnWidth("Day Name",80);

		table.addContainerProperty("Status", Label.class, new Label());
		table.setColumnWidth("Status", 50);

		table.addContainerProperty("In (HH)", NumberField.class , new NumberField());
		table.setColumnWidth("In (HH)",35);

		table.addContainerProperty("In (MM)", NumberField.class , new NumberField());
		table.setColumnWidth("In (MM)",35);

		table.addContainerProperty("In (SS)", NumberField.class , new NumberField());
		table.setColumnWidth("In (SS)",35);

		table.addContainerProperty("Out (HH)", NumberField.class , new NumberField());
		table.setColumnWidth("Out (HH)",35);

		table.addContainerProperty("Out (MM)", NumberField.class , new NumberField());
		table.setColumnWidth("Out (MM)",35);

		table.addContainerProperty("Out (SS)", NumberField.class , new NumberField());
		table.setColumnWidth("Out (SS)",35);

		table.addContainerProperty("Reason", TextField.class , new TextField());
		table.setColumnWidth("Reason",170);

		table.addContainerProperty("Permitted By", TextField.class , new TextField());
		table.setColumnWidth("Permitted By",170);

		table.setColumnAlignments(new String[] {Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_CENTER,
				Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,
				Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT});

		rowAddInTable();
	}

	public void rowAddInTable()
	{
		for(int i=0;i<10;i++)
		{
			tableRowAdd(i);
		}
	}

	public void tableRowAdd(final int ar)
	{
		tbDate.add(ar, new PopupDateField());
		tbDate.get(ar).setWidth("100%");
		tbDate.get(ar).setHeight("20.0px");
		tbDate.get(ar).setDateFormat("dd-MM-yyyy");
		tbDate.get(ar).setResolution(PopupDateField.RESOLUTION_DAY);
		tbDate.get(ar).setImmediate(true);
		tbDate.get(ar).setReadOnly(true);

		tbLblDayName.add(ar, new Label());
		tbLblDayName.get(ar).setWidth("100%");
		tbLblDayName.get(ar).setImmediate(true);

		tbLblStatus.add(ar, new Label());
		tbLblStatus.get(ar).setWidth("100%");
		tbLblStatus.get(ar).setImmediate(true);

		tbInHour.add(ar, new NumberField());
		tbInHour.get(ar).setImmediate(true);
		tbInHour.get(ar).setWidth("100%");
		tbInHour.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(tbInHour.get(ar).getValue().toString().length()>0)
				{
					checkTime(ar);
					if(Integer.parseInt(tbInHour.get(ar).getValue().toString())>23)
					{
						tbInHour.get(ar).setValue("");
					}
				}
			}
		});

		tbInMin.add(ar, new NumberField());
		tbInMin.get(ar).setImmediate(true);
		tbInMin.get(ar).setWidth("100%");
		tbInMin.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(tbInMin.get(ar).getValue().toString().length()>0)
				{
					checkTime(ar);
					if(Integer.parseInt(tbInMin.get(ar).getValue().toString())>59)
					{
						tbInMin.get(ar).setValue("");
					}
				}
			}
		});

		tbInSec.add(ar, new NumberField());
		tbInSec.get(ar).setImmediate(true);
		tbInSec.get(ar).setWidth("100%");
		tbInSec.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(tbInSec.get(ar).getValue().toString().length()>0)
				{
					checkTime(ar);
					if(Integer.parseInt(tbInSec.get(ar).getValue().toString())>59)
					{
						tbInSec.get(ar).setValue("");
					}
				}
			}
		});

		tbOutHour.add(ar, new NumberField());
		tbOutHour.get(ar).setImmediate(true);
		tbOutHour.get(ar).setWidth("100%");
		tbOutHour.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(tbOutHour.get(ar).getValue().toString().length()>0)
				{
					checkTime(ar);
					if(Integer.parseInt(tbOutHour.get(ar).getValue().toString())>23)
					{
						tbOutHour.get(ar).setValue("");
					}
				}
			}
		});

		tbOutMin.add(ar, new NumberField());
		tbOutMin.get(ar).setImmediate(true);
		tbOutMin.get(ar).setWidth("100%");
		tbOutMin.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(tbOutMin.get(ar).getValue().toString().length()>0)
				{
					checkTime(ar);
					if(Integer.parseInt(tbOutMin.get(ar).getValue().toString())>59)
					{
						tbOutMin.get(ar).setValue("");
					}
				}
			}
		});

		tbOutSec.add(ar, new NumberField());
		tbOutSec.get(ar).setImmediate(true);
		tbOutSec.get(ar).setWidth("100%");
		tbOutSec.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(tbOutSec.get(ar).getValue().toString().length()>0)
				{
					checkTime(ar);
					if(Integer.parseInt(tbOutSec.get(ar).getValue().toString())>59)
					{
						tbOutSec.get(ar).setValue("");
					}
				}
			}
		});

		tbTxtReason.add(ar, new TextField());
		tbTxtReason.get(ar).setImmediate(true);
		tbTxtReason.get(ar).setWidth("100%");

		tbTxtPermittedBy.add(ar, new TextField());
		tbTxtPermittedBy.get(ar).setImmediate(true);
		tbTxtPermittedBy.get(ar).setWidth("100%");

		table.addItem(new Object[]{tbDate.get(ar),tbLblDayName.get(ar),tbLblStatus.get(ar),tbInHour.get(ar),
				tbInMin.get(ar),tbInSec.get(ar),tbOutHour.get(ar),tbOutMin.get(ar),tbOutSec.get(ar),
				tbTxtReason.get(ar),tbTxtPermittedBy.get(ar)},ar);
	}

	private void checkTime(int ind)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			if(tbInHour.get(ind).getValue().toString().trim().length()>0 && tbInMin.get(ind).toString().trim().length()>0 &&  
					tbOutHour.get(ind).getValue().toString().trim().length()>0 && tbOutMin.get(ind).getValue().toString().trim().length()>0)
			{
				String InTimeOne = dbDateFormat.format(tbDate.get(ind).getValue())+" "+tbInHour.get(ind).getValue().toString()+":"+tbInMin.get(ind).getValue().toString()+":00";
				String OutTimeOne = dbDateFormat.format(tbDate.get(ind).getValue())+" "+tbOutHour.get(ind).getValue().toString()+":"+tbOutMin.get(ind).getValue().toString()+":00";

				String workingHourCheck = "select DATEDIFF(SS,'"+InTimeOne+"','"+OutTimeOne+"')";
				int workingTime=Integer.parseInt(session.createSQLQuery(workingHourCheck).list().iterator().next().toString());

				if(workingTime>84600)
				{
					tbOutHour.get(ind).focus();
					tbOutHour.get(ind).setValue("");
					tbOutMin.get(ind).setValue("");
					showNotification("Warning", "Total duration can't exceed 23:30 hours!!!", Notification.TYPE_WARNING_MESSAGE);
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("checkTime", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}

	private void btnIni(boolean t)
	{
		cButton.btnNew.setEnabled(t);
		cButton.btnSave.setEnabled(!t);
		cButton.btnRefresh.setEnabled(!t);
	}

	private void componentIni(boolean b) 
	{
		dMonth.setEnabled(!b);
		cmbSection.setEnabled(!b);
		opgEmployee.setEnabled(!b);
		cmbEmployee.setEnabled(!b);
		table.setEnabled(!b);
	}

	private void txtClear()
	{
		cmbSection.setValue(null);
		cmbEmployee.setValue(null);
		tableclear();
	}

	private void tableclear()
	{
		for(int i=0; i<tbDate.size(); i++)
		{
			tbDate.get(i).setReadOnly(false);
			tbDate.get(i).setValue(null);
			tbDate.get(i).setReadOnly(true);

			tbLblDayName.get(i).setValue("");
			tbLblStatus.get(i).setValue("");
			tbInHour.get(i).setValue("");
			tbInMin.get(i).setValue("");
			tbInSec.get(i).setValue("");

			tbOutHour.get(i).setValue("");
			tbOutMin.get(i).setValue("");
			tbOutSec.get(i).setValue("");

			tbTxtReason.get(i).setValue("");
			tbTxtPermittedBy.get(i).setValue("");
		}
	}

	private void focusEnter()
	{
		allComp.add(dMonth);
		allComp.add(cmbSection);
		allComp.add(cmbEmployee);

		for(int i=0; i<tbDate.size();i++)
		{
			allComp.add(tbInHour.get(i));
			allComp.add(tbInMin.get(i));
			allComp.add(tbOutHour.get(i));
			allComp.add(tbOutMin.get(i));
			allComp.add(tbTxtReason.get(i));
			allComp.add(tbTxtPermittedBy.get(i));
		}

		allComp.add(cButton.btnSave);
		new FocusMoveByEnter(this,allComp);
	}

	private AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);

		mainLayout.setWidth("963px");
		mainLayout.setHeight("420px");

		lblMonth = new Label("Month :");
		lblMonth.setWidth("-1px");
		lblMonth.setHeight("-1px");
		mainLayout.addComponent(lblMonth, "top:10.0px;left:20.0px;");

		dMonth = new PopupDateField();
		dMonth.setValue(new Date());
		dMonth.setWidth("130px");
		dMonth.setHeight("24px");
		dMonth.setResolution(PopupDateField.RESOLUTION_MONTH);
		dMonth.setDateFormat("MMMMM-yyyy");
		dMonth.setInvalidAllowed(false);
		dMonth.setInputPrompt("Month");
		dMonth.setImmediate(true);
		mainLayout.addComponent(dMonth, "top:08.0px;left:140.0px;");

		lblSection = new Label("Section Name : ");
		lblSection.setWidth("-1px");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection, "top:35.0px;left:20.0px;");

		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("240px");
		cmbSection.setHeight("24px");
		cmbSection.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbSection, "top:33.0px; left:140.0px;");

		opgEmployee = new OptionGroup("", employeeList);
		opgEmployee.setImmediate(true);
		opgEmployee.setValue("Employee ID");
		opgEmployee.setStyleName("horizontal");
		mainLayout.addComponent(opgEmployee, "top:10.0px; left:570.0px;");

		lblEmployee = new Label("Employee ID : ");
		lblEmployee.setWidth("-1px");
		lblEmployee.setHeight("-1px");
		mainLayout.addComponent(lblEmployee, "top:35.0px;left:450.0px;");

		cmbEmployee = new ComboBox();
		cmbEmployee.setImmediate(true);
		cmbEmployee.setWidth("320px");
		cmbEmployee.setHeight("24px");
		cmbEmployee.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbEmployee, "top:35.0px; left:570.0px;");

		mainLayout.addComponent(table, "top:70.0px; left:20.0px;");
		mainLayout.addComponent(cButton, "top:380.0px; left:330.0px;");
		return mainLayout;
	}
}
