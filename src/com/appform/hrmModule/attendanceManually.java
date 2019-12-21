package com.appform.hrmModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.AmountField;
import com.common.share.CommonButton;
import com.common.share.CommonMethod;
import com.common.share.MessageBox;
import com.common.share.MessageBox.EventListener;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.MessageBox.ButtonType;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class attendanceManually extends Window
{
	SessionBean sessionbean;
	private AbsoluteLayout mainlayout;
	private ComboBox cmbDepartment,cmbSection;
	
	private ComboBox cmbEmployee;
	private CheckBox chkAllDepartment,chkAllSection,chkAllEmp;
	private PopupDateField dMonth;
	private ComboBox cmbUnit;	
	private Table table=new Table();
	private ArrayList<NativeButton> btnDel = new ArrayList<NativeButton>();
	private ArrayList<Label> lblSl = new ArrayList<Label>();
	private ArrayList<Label> lblAutoEmployeeID = new ArrayList<Label>();
	private ArrayList<Label> lblEmployeeID = new ArrayList<Label>();
	private ArrayList<Label> lblFingerID = new ArrayList<Label>();
	private ArrayList<Label> lblEmployeeName = new ArrayList<Label>();
	private ArrayList<Label> lblDesignationID = new ArrayList<Label>();
	private ArrayList<Label> lblDesignation = new ArrayList<Label>();
	private ArrayList<Label> lblServiceType = new ArrayList<Label>();
	private ArrayList<Label> lblJoiningDate = new ArrayList<Label>();
	private ArrayList<Label> lblUnitID = new ArrayList<Label>();
	private ArrayList<Label> lblUnitName = new ArrayList<Label>();
	private ArrayList<Label> lblDepartmentID = new ArrayList<Label>();
	private ArrayList<Label> lblDepartmentName = new ArrayList<Label>();
	private ArrayList<Label> lblSectionID = new ArrayList<Label>();
	private ArrayList<Label> lblSectionName = new ArrayList<Label>();
	private ArrayList<Label> lblMonthDay = new ArrayList<Label>();
	private ArrayList<AmountField> txtHoliday = new ArrayList<AmountField>();
	private ArrayList<Label> lblWorkingDay = new ArrayList<Label>();
	private ArrayList<AmountField> txtHolidayDuty = new ArrayList<AmountField>();
	private ArrayList<AmountField> txtPresentDay = new ArrayList<AmountField>();
	private ArrayList<Label> txtAbsentDay = new ArrayList<Label>();
	private ArrayList<AmountField> txtLeaveWithPay = new ArrayList<AmountField>();
	private ArrayList<AmountField> txtLeaveWithoutPay = new ArrayList<AmountField>();
	private ArrayList<AmountField> txtOTHour = new ArrayList<AmountField>();
	private ArrayList<AmountField> txtOTMin = new ArrayList<AmountField>();
	private ArrayList<AmountField> txtMealDay = new ArrayList<AmountField>();
	private ArrayList<AmountField> txtlateatt = new ArrayList<AmountField>();

	private SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dateFormatBangla=new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat MonthFormat=new SimpleDateFormat("MMMMM-yyyy");
	
	private SimpleDateFormat dFYear=new SimpleDateFormat("yyyy");
	private SimpleDateFormat dfMonth=new SimpleDateFormat("MM");
	private CommonButton cButton=new CommonButton("New", "Save", "", "", "Refresh", "", "", "", "", "Exit");

	boolean isSave=false;
	boolean isUpdate=false;
	boolean isRefresh=false;
	boolean isFind=false;
	int index=0;
	String Noti="";
	private CommonMethod cm;
	private String menuId = "";
	public attendanceManually(SessionBean sessionBean,String menuId)
	{
		this.sessionbean=sessionBean;
		this.setCaption("MONTHLY ATTENDANCE INFORMATION :: "+sessionbean.getCompany());
		buildMainLayout();
		this.setContent(mainlayout);
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		componentEnable(true);
		btnEnable(true);
		setEventAction();
		cmbUnitDataLoad();
		authenticationCheck();
	}
	private void authenticationCheck()
	{
		cm.checkFormAction(menuId);
		if(!sessionbean.isSuperAdmin())
		{
		if(!sessionbean.isAdmin())
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
	private void cmbUnitDataLoad()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql="select distinct vUnitId,vUnitName from tbUnitInfo order by vUnitId";
			
			System.out.println("unit"+sql);
			
			List <?> lst=session.createSQLQuery(sql).list();
			if(!lst.isEmpty())
			{
				Iterator <?> itr=lst.iterator();
				while(itr.hasNext())
				{
					Object[] element=(Object[])itr.next();
					cmbUnit.addItem(element[0]);
					cmbUnit.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("CmbUnitDataLoad", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	

	private void cmbDepartmentDataLoad()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql="select distinct vDepartmentId,vDepartmentName from tbEmpOfficialPersonalInfo "
					+ "where vUnitId ='"+cmbUnit.getValue().toString()+"' order by vDepartmentName";

			System.out.println("section"+sql);
			
			List <?> lst=session.createSQLQuery(sql).list();
			if(!lst.isEmpty())
			{
				Iterator <?> itr=lst.iterator();
				while(itr.hasNext())
				{
					Object[] element=(Object[])itr.next();
					cmbDepartment.addItem(element[0]);
					cmbDepartment.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbDepartmentDataLoad", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void cmbSectionDataLoad()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		String dept="%";
		if(!chkAllDepartment.booleanValue())
		{
			dept=cmbDepartment.getValue().toString();
		}
		try
		{
			String sql="select distinct vSectionId,vSectionName from tbEmpOfficialPersonalInfo " +
					" where vUnitId ='"+cmbUnit.getValue().toString()+"' and vDepartmentId like'"+dept+"' " +
					" order by vSectionName ";

			System.out.println("section"+sql);
			
			List <?> lst=session.createSQLQuery(sql).list();
			if(!lst.isEmpty())
			{
				Iterator <?> itr=lst.iterator();
				while(itr.hasNext())
				{
					Object[] element=(Object[])itr.next();
					cmbSection.addItem(element[0]);
					cmbSection.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbDepartmentDataLoad", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void setEventAction()
	{
		dMonth.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				chkAllEmp.setValue(false);
				tableclear();
			}
		});
		
		cmbUnit.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmployee.removeAllItems();
				cmbDepartment.removeAllItems();				
				cmbEmployee.setEnabled(true);
				cmbEmployee.setValue(null);
				chkAllEmp.setValue(false);
				tableclear();
				if(cmbUnit.getValue()!=null)
				cmbDepartmentDataLoad();
				
			}
		});

		cmbDepartment.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSection.removeAllItems();
				cmbEmployee.removeAllItems();
				cmbEmployee.setEnabled(true);
				cmbEmployee.setValue(null);
				chkAllEmp.setValue(false);
				tableclear();
				if(cmbUnit.getValue()!=null)
				{
					if(cmbDepartment.getValue()!=null)
					{
						cmbSectionDataLoad();
					}
				}
			}
		});

		chkAllDepartment.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(chkAllDepartment.booleanValue())
				{
					cmbDepartment.setEnabled(false);
					cmbSection.removeAllItems();
					cmbEmployee.removeAllItems();
					cmbEmployee.setEnabled(true);
					cmbEmployee.setValue(null);
					chkAllEmp.setValue(false);
					tableclear();
					if(cmbUnit.getValue()!=null)
					{
						cmbSectionDataLoad();
					}
				}
				else
				{
					cmbDepartment.setEnabled(true);
				}
				
			}
		});

		cmbSection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmployee.removeAllItems();
				cmbEmployee.setEnabled(true);
				cmbEmployee.setValue(null);
				chkAllEmp.setValue(false);
				tableclear();
				if(cmbUnit.getValue()!=null)
				{
					if(cmbDepartment.getValue()!=null || chkAllDepartment.booleanValue())
					{
						if(cmbSection.getValue()!=null)
						{
							addEmployeeName();
						}
					}
				}
			}
		});

		chkAllSection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(chkAllSection.booleanValue())
				{
					cmbSection.setEnabled(false);
					cmbEmployee.removeAllItems();
					cmbEmployee.setEnabled(true);
					cmbEmployee.setValue(null);
					chkAllEmp.setValue(false);
					tableclear();
					if(cmbUnit.getValue()!=null)
					{
						if(cmbDepartment.getValue()!=null || chkAllDepartment.booleanValue())
						{
							addEmployeeName();
						}
					}
				}
				else
				{
					cmbSection.setEnabled(true);
				}
			}
		});

		cmbEmployee.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbUnit.getValue()!=null)
				{
					if(cmbDepartment.getValue()!=null || chkAllDepartment.booleanValue())
					{
						if(cmbSection.getValue()!=null || chkAllSection.booleanValue())
						{
							tableValueAdd();
						}
					}
				}
			}
		});

		chkAllEmp.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbUnit.getValue()!=null)
				{
					if(cmbDepartment.getValue()!=null || chkAllDepartment.booleanValue())
					{
						if(cmbSection.getValue()!=null || chkAllSection.booleanValue())
						{
							if(chkAllEmp.booleanValue())
							{
								cmbEmployee.setValue(null);
								cmbEmployee.setEnabled(false);
								tableValueAdd();
							}
							else
							{
								cmbEmployee.setEnabled(true);
							}
						}
					}
				}
				else
				{
					if(!isSave && !isRefresh)
					{
						chkAllEmp.setValue(false);
						showNotification("Warning", "Please Select Section Name!!!", Notification.TYPE_WARNING_MESSAGE);
					}
				}
			}
		});

		cButton.btnNew.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				isSave=false;
				isRefresh=false;
				txtclear();
				componentEnable(false);
				btnEnable(false);
				index=0;
			}
		});

		cButton.btnSave.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(chkTableData())
				{
					isSave=true;
					saveButtonEvent();
				}

				else
					showNotification("Warning", Noti, Notification.TYPE_WARNING_MESSAGE);
			}
		});

		cButton.btnRefresh.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				isRefresh=true;
				txtclear();
				componentEnable(true);
				btnEnable(true);
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

	private void addEmployeeName()
	{		
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		String dept="%",section="%";
		if(!chkAllDepartment.booleanValue())
		{
			dept=cmbDepartment.getValue().toString();
		}
		if(!chkAllSection.booleanValue())
		{
			section=cmbSection.getValue().toString();
		}
		
		try
		{

			String sql="select vEmployeeId,vEmployeeCode,vFingerId,vEmployeeName,vUnitId,vUnitName from tbEmpOfficialPersonalInfo  " +
						"where bStatus = 1 and vUnitId='"+cmbUnit.getValue()+"' and  vDepartmentId like '"+dept+"' " +
						"and  vSectionId like '"+section+"' order by vEmployeeCode";
			
			System.out.println("addEmployeeName: "+sql);
			List <?> lst=session.createSQLQuery(sql).list();
			if(!lst.isEmpty())
			{
				Iterator <?> itr=lst.iterator();
				while(itr.hasNext())
				{
					Object[] element=(Object[])itr.next();
					cmbEmployee.addItem(element[0]);
					cmbEmployee.setItemCaption(element[0], element[1]+"->"+element[3]);
				}
			}
			else
				showNotification("Warning", "No Employee Found!!!", Notification.TYPE_WARNING_MESSAGE);
		}
		catch(Exception exp)
		{
			showNotification("addEmployeeName", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private boolean chkTableData()
	{
		boolean ret=false;
		int count=0,count1=0;
		for(int tbin=0;tbin<lblAutoEmployeeID.size();tbin++)
		{
			if(!lblAutoEmployeeID.get(tbin).getValue().toString().trim().isEmpty())
			{
				count++;
				if(!txtPresentDay.get(tbin).getValue().toString().trim().isEmpty())
				{
					count1++;
					
				}
				else
				{
					txtPresentDay.get(tbin).focus();
					Noti="Provide Present Days!!!";
					break;
				}
			}
			else
			{
				Noti="No Data Found!!!";
			}
		}
		if(count==count1)
		{
			ret=true;
		}
		return ret;
	}

	//Edit For Salry 24-07-18
	
	private boolean chkData(String chkQuery)
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> lst1=session.createSQLQuery(chkQuery).list();
			if(!lst1.isEmpty())
				return true;
		}
		catch (Exception exp)
		{
			showNotification("chkData", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
		return false;
	}

	private void saveButtonEvent()
	{
		String dept="%",section="%",emp="%";
		if(!chkAllDepartment.booleanValue())
		{
			dept=cmbDepartment.getValue().toString();
		}
		if(!chkAllSection.booleanValue())
		{
			section=cmbSection.getValue().toString();
		}
		if(!chkAllEmp.booleanValue())
		{
			emp=cmbEmployee.getValue().toString();
		}
		String salaryData = "select * from tbMonthlySalary " +
				"where vUnitId='"+cmbUnit.getValue().toString()+"' and vDepartmentId like '"+dept+"' and vSectionID like '"+section+"' " +
				"and MONTH(dSalaryDate)=MONTH('"+dateFormat.format(dMonth.getValue())+"') " +
				"and YEAR(dSalaryDate)=YEAR('"+dateFormat.format(dMonth.getValue())+"') " +
				"and vEmployeeID like '"+emp+"' ";

		System.out.println("Salary Data: "+salaryData);
		
		if(!chkData(salaryData))
		{
			MessageBox msgbox=new MessageBox(getParent(), "Are You Sure?", MessageBox.Icon.QUESTION, "Do You Want to Save All Information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			msgbox.setStyleName("cwindowMB");
			msgbox.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType==ButtonType.YES)
					{
						insertdata();
						txtclear();
						componentEnable(true);
						btnEnable(true);
						Notification n=new Notification("All Information Save Successfully!","",Notification.TYPE_TRAY_NOTIFICATION);
						n.setPosition(Notification.POSITION_TOP_RIGHT);
						showNotification(n);
					}
				}
			});
		}
		else
		{
			showNotification("Warning", "Salary Already Generated for the Month of "+MonthFormat.format(dMonth.getValue())+"!!!", Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void deleteData(Session session, String employeeId)
	{
		String udQuery = "insert into tbUDAttendanceSummary (dDate,vEmployeeId,vEmployeeCode,vFingerId,vEmployeeName,vDesignationId,"
				+ " vDesignationName,vDepartmentId,vDepartmentName,vSectionId,vSectioNname,iTotalMonth,iHoliday,iTotalWorkingDay,iPresentDay,iAbsentDay,iHolidayDuty,"
				+ " iLeaveWithPay,iLeaveWithoutPay,iOtHour,iOtMinute,vUDFlag,vUserName,vUserIP,dEntryTime,iLateDay) select dDate,vEmployeeId,"
				+ " vEmployeeCode,vFingerId,vEmployeeName,vDesignationId,vDesignationName,vDepartmentId,vDepartmentName,vSectionId,vSectioNname,iTotalMonth,iHoliday,"
				+ " iTotalWorkingDay,iPresentDay,iAbsentDay,iHolidayDuty,iLeaveWithPay,iLeaveWithoutPay,iOtHour,iOtMinute,'UPDATE',vUserName,"
				+ " vUserIP,dEntryTime,iLateDay from tbAttendanceSummary where MONTH(dDate) = MONTH('"+dateFormat.format(dMonth.getValue())+"') "
				+ " and YEAR(dDate) = YEAR('"+dateFormat.format(dMonth.getValue())+"') and vEmployeeID = '"+employeeId+"'";
		session.createSQLQuery(udQuery).executeUpdate();
	
		System.out.println("Insert"+udQuery);
		String deleteData="delete from tbAttendanceSummary where Month(dDate)=MONTH('"+dateFormat.format(dMonth.getValue())+"')" +
				" and Year(dDate)=YEAR('"+dateFormat.format(dMonth.getValue())+"') and vEmployeeID = '"+employeeId+"'";
		session.createSQLQuery(deleteData).executeUpdate();
		
		
		System.out.println("Delete Query : "+deleteData);
	}

	private void insertdata()
	{
		String Sql="";
		for(int i=0;i<lblAutoEmployeeID.size();i++) 
		{
			if(!lblAutoEmployeeID.get(i).getValue().toString().isEmpty() && !txtPresentDay.get(i).getValue().toString().isEmpty() )
			{		
				Session session=SessionFactoryUtil.getInstance().openSession();
					Transaction tx=session.beginTransaction();
					try
					{
						String checkQuery = "select * from tbAttendanceSummary where MONTH(dDate) = MONTH('"+dateFormat.format(dMonth.getValue())+"') and "
								+ "YEAR(dDate) = YEAR('"+dateFormat.format(dMonth.getValue())+"') and vEmployeeID = '"+lblAutoEmployeeID.get(i).getValue().toString()+"' ";
						if(chkData(checkQuery))
						{
							deleteData(session,lblAutoEmployeeID.get(i).getValue().toString());
						}
									
						Sql="insert into tbAttendanceSummary (dDate,vEmployeeId,vEmployeeCode,vFingerId,vEmployeeName,vDesignationId,vDesignationName,"
								+ "vUnitId,vUnitName,vDepartmentId,vDepartmentName,vSectionId,vSectioNname,iTotalMonth,iHoliday,iTotalWorkingDay,iPresentDay,iAbsentDay,iHolidayDuty,iLeaveWithPay,"
								+ " iLeaveWithoutPay,iOtHour,iOtMinute,iOTFlag,iMealDay,vUserName,vUserIP,dEntryTime,iLateDay) values "
								+ " ('"+dateFormat.format(dMonth.getValue())+"',"
								+ " '"+lblAutoEmployeeID.get(i).getValue().toString()+"',"
								+ " '"+lblEmployeeID.get(i).getValue().toString()+"',"
								+ " '"+lblFingerID.get(i).getValue().toString()+"',"
								+ " '"+lblEmployeeName.get(i).getValue().toString()+"',"
								+ " '"+lblDesignationID.get(i).getValue().toString()+"',"
								+ " '"+lblDesignation.get(i).getValue().toString()+"',"						
								+ " '"+lblUnitID.get(i).getValue().toString()+"',"
								+ " '"+lblUnitName.get(i).getValue().toString()+"',"
								+ " '"+lblDepartmentID.get(i).getValue().toString()+"',"
								+ " '"+lblDepartmentName.get(i).getValue().toString()+"',"
								+ " '"+lblSectionID.get(i).getValue().toString()+"',"
								+ " '"+lblSectionName.get(i).getValue().toString()+"',"
								+ " '"+lblMonthDay.get(i).getValue()+"',"
								+ " '"+txtHoliday.get(i).getValue()+"',"
								+ " '"+lblWorkingDay.get(i).getValue()+"',"
								+ " '"+txtPresentDay.get(i).getValue()+"',"
								+ " '"+txtAbsentDay.get(i).getValue()+"',"
								+ " '"+txtHolidayDuty.get(i).getValue()+"',"
								+ " '"+txtLeaveWithPay.get(i).getValue()+"',"
								+ " '"+txtLeaveWithoutPay.get(i).getValue()+"',"
								+ " '"+(!txtOTHour.get(i).getValue().toString().isEmpty()?txtOTHour.get(i).getValue().toString():"0")+"',"
								+ " '"+(!txtOTMin.get(i).getValue().toString().isEmpty()?txtOTMin.get(i).getValue().toString():"0")+"',"
								+ " (select iOtEnable from tbEmpOfficialPersonalInfo where vEmployeeID = '"+lblAutoEmployeeID.get(i).getValue().toString()+"'),"
								+"  '"+(!txtMealDay.get(i).getValue().toString().isEmpty()?txtMealDay.get(i).getValue().toString():"0")+"',"
								+ " '"+sessionbean.getUserName()+"','"+sessionbean.getUserIp()+"',getdate(),'"+txtlateatt.get(i).getValue().toString()+"')";
						
					    System.out.println("insert"+Sql);
						
						session.createSQLQuery(Sql).executeUpdate();
					
						
						tx.commit();
					}
					catch (Exception exp)
					{
						tx.rollback();
						showNotification("insertdata", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
					}
					finally{session.close();}
			//	}
			}
		}
	}

	private void tableValueAdd()
	{
		String dept="%", section="%";
		if(!chkAllDepartment.booleanValue())
		{
			dept=cmbDepartment.getValue().toString();
		}
		if(!chkAllSection.booleanValue())
		{
			section=cmbSection.getValue().toString();
		}
		tableclear();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql="select vEmployeeId,vEmployeeCode,vFingerId,vEmployeeName,vDesignationId,vDesignation,vServiceType,vUnitId,vUnitName,vSectionId,vSectionName," +
					"totalMonthDay,totalHoliday,iHolidayDuty,iPresentDay,iAbsentDay,iLeaveWithPay,iLeaveWithoutPay,iOtHour,iOtMinute,iMealDay,iLateday,dJoiningDate, " +
					"vDepartmentId,vDepartmentName from funMonthlyAttendance("
					+ "'"+cmbUnit.getValue()+"',"
					+ "'"+dept+"',"
					+ "'"+section+"',"
					+ "'"+dateFormat.format(dMonth.getValue())+"',"
					+ "'"+(cmbEmployee.getValue()!=null?cmbEmployee.getValue():"%")+"'"
					+ ")";
			
			System.out.println("tableValueAdd"+sql);
			
			List <?> lst=session.createSQLQuery(sql).list();
			if(!lst.isEmpty())
			{
				Iterator <?> itr=lst.iterator();
				boolean checkData=false;
				while(itr.hasNext())
				{
					Object [] element=(Object[])itr.next();
					boolean check=false;
					for(int chkindex=0;chkindex<lblAutoEmployeeID.size();chkindex++)
					{
						if(lblAutoEmployeeID.get(chkindex).getValue().toString().equalsIgnoreCase(element[0].toString()))
						{
							check=true;
							index=chkindex;
							break;
						}
						else if(lblAutoEmployeeID.get(chkindex).getValue().toString().isEmpty())
						{
							check=false;
							index=chkindex;
							break;
						}
						else
						{
							check=true;
						}
					}
					if(!check)
					{
						if(index==lblEmployeeID.size()-1)
							tableRowAdd(index+1);
						lblAutoEmployeeID.get(index).setValue(element[0].toString());
						lblEmployeeID.get(index).setValue(element[1].toString());
						lblFingerID.get(index).setValue(element[2].toString());
						lblEmployeeName.get(index).setValue(element[3].toString());
						lblDesignationID.get(index).setValue(element[4].toString());
						lblDesignation.get(index).setValue(element[5].toString());
						lblServiceType.get(index).setValue(element[6].toString());

						if(lblServiceType.get(index).getValue().toString().equalsIgnoreCase("Worker"))
						{
							txtMealDay.get(index).setEnabled(false);
						}
						
						lblUnitID.get(index).setValue(element[7].toString());
						lblUnitName.get(index).setValue(element[8].toString());
						lblDepartmentID.get(index).setValue(element[23].toString());
						lblDepartmentName.get(index).setValue(element[24].toString());
						lblSectionID.get(index).setValue(element[9].toString());
						lblSectionName.get(index).setValue(element[10].toString());
						lblMonthDay.get(index).setValue(element[11].toString());
						txtHoliday.get(index).setValue(element[12].toString());
						int workingDay = Integer.parseInt(lblMonthDay.get(index).getValue().toString()) - Integer.parseInt(txtHoliday.get(index).getValue().toString());
						lblWorkingDay.get(index).setValue(Integer.toString(workingDay));
						if(Integer.parseInt(element[13].toString()) > 0)
							txtHolidayDuty.get(index).setValue(element[13].toString());
						if(Integer.parseInt(element[14].toString()) > 0)
							txtPresentDay.get(index).setValue(element[14].toString());
						if(Integer.parseInt(element[15].toString()) > 0)
							txtAbsentDay.get(index).setValue(element[15].toString());
						if(Integer.parseInt(element[16].toString()) > 0)
							txtLeaveWithPay.get(index).setValue(element[16].toString());
						if(Integer.parseInt(element[17].toString()) > 0)
							txtLeaveWithoutPay.get(index).setValue(element[17].toString());
						if(Integer.parseInt(element[18].toString()) > 0)
							txtOTHour.get(index).setValue(element[18].toString());
						if(Integer.parseInt(element[19].toString()) > 0)
							txtOTMin.get(index).setValue(element[19].toString());
						if(Integer.parseInt(element[20].toString()) > 0)
							txtMealDay.get(index).setValue(element[20].toString());
						if(Integer.parseInt(element[21].toString()) > 0)
							txtlateatt.get(index).setValue(element[21].toString());
						
						lblJoiningDate.get(index).setValue(dateFormatBangla.format(element[22]));

						index++;
					}
					checkData=check;
				}
				if(checkData)
				{
					showNotification("Warning", "Employee is already Found in the list!!!", Notification.TYPE_WARNING_MESSAGE);
				}
			}
			else
				showNotification("Warning", "No Data Found!!!", Notification.TYPE_WARNING_MESSAGE);

		}
		catch(Exception exp)
		{
			showNotification("TableValueAdd", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void txtclear()
	{
		cmbUnit.setValue(null);
		cmbDepartment.setValue(null);
		cmbSection.setValue(null);
		cmbEmployee.setValue(null);
		chkAllDepartment.setValue(false);
		chkAllSection.setValue(false);
		chkAllEmp.setValue(false);
		
		tableclear();
	}

	private void tableclear()
	{
		for(int i=0;i<lblAutoEmployeeID.size();i++)
		{
			lblAutoEmployeeID.get(i).setValue("");
			lblEmployeeID.get(i).setValue("");
			lblFingerID.get(i).setValue("");
			lblEmployeeName.get(i).setValue("");
			lblDesignationID.get(i).setValue("");
			lblDesignation.get(i).setValue("");
			lblServiceType.get(i).setValue("");
			lblJoiningDate.get(i).setValue("");
			lblUnitID.get(i).setValue("");
			lblUnitName.get(i).setValue("");
			lblDepartmentID.get(i).setValue("");
			lblDepartmentName.get(i).setValue("");
			lblSectionID.get(i).setValue("");
			lblSectionName.get(i).setValue("");
			lblMonthDay.get(i).setValue("");
			txtHoliday.get(i).setValue("");
			lblWorkingDay.get(i).setValue("");
			txtPresentDay.get(i).setValue("");
			txtLeaveWithPay.get(i).setValue("");
			txtAbsentDay.get(i).setValue("");
			txtLeaveWithoutPay.get(i).setValue("");
			txtHolidayDuty.get(i).setValue("");		
			txtOTHour.get(i).setValue("");
			txtOTMin.get(i).setValue("");
			txtMealDay.get(i).setValue("");
			txtlateatt.get(i).setValue("");
		}
	}

	private void componentEnable(boolean b)
	{
		cmbUnit.setEnabled(!b);
		cmbDepartment.setEnabled(!b);
		cmbSection.setEnabled(!b);
		cmbEmployee.setEnabled(!b);
		chkAllDepartment.setEnabled(!b);
		chkAllSection.setEnabled(!b);
		chkAllEmp.setEnabled(!b);
		dMonth.setEnabled(!b);
		table.setEnabled(!b);
	}

	private void btnEnable(boolean b)
	{
		cButton.btnNew.setEnabled(b);
		cButton.btnSave.setEnabled(!b);
		cButton.btnEdit.setEnabled(b);
		cButton.btnDelete.setEnabled(b);
		cButton.btnRefresh.setEnabled(!b);
		cButton.btnFind.setEnabled(b);
	}

	private void tableinitialize()
	{
		for(int i=0;i<10;i++)
			tableRowAdd(i);
	}

	private boolean dayCalculation(int tbIndex,String flag)
	{
		boolean ret = true;
		int monthDay = Integer.parseInt(!lblMonthDay.get(tbIndex).getValue().toString().trim().isEmpty()?lblMonthDay.get(tbIndex).getValue().toString().trim():"0");
		int holiday = Integer.parseInt(!txtHoliday.get(tbIndex).getValue().toString().trim().isEmpty()?txtHoliday.get(tbIndex).getValue().toString().trim():"0");
		lblWorkingDay.get(tbIndex).setValue(monthDay-holiday);
		
		int workingDay = Integer.parseInt(!lblWorkingDay.get(tbIndex).getValue().toString().trim().isEmpty()?lblWorkingDay.get(tbIndex).getValue().toString().trim():"0");
		int holidayDuty = Integer.parseInt(!txtHolidayDuty.get(tbIndex).getValue().toString().trim().isEmpty()?txtHolidayDuty.get(tbIndex).getValue().toString().trim():"0");
		int present = Integer.parseInt(!txtPresentDay.get(tbIndex).getValue().toString().trim().isEmpty()?txtPresentDay.get(tbIndex).getValue().toString().trim():"0");
		int leave = Integer.parseInt(!txtLeaveWithPay.get(tbIndex).getValue().toString().trim().isEmpty()?txtLeaveWithPay.get(tbIndex).getValue().toString().trim():"0");
		int leaveWithout = Integer.parseInt(!txtLeaveWithoutPay.get(tbIndex).getValue().toString().trim().isEmpty()?txtLeaveWithoutPay.get(tbIndex).getValue().toString().trim():"0");
		
		
		int absent = workingDay - (present+leave+leaveWithout);

		if(absent >= 0)
		{
			txtAbsentDay.get(tbIndex).setValue(Integer.toString(absent));
		}

		else
		{
			/*if(flag == "HD")
			{
				txtPresentDay.get(tbIndex).setValue("");
				txtPresentDay.get(tbIndex).focus();
			}*/
			if(flag == "PD")
			{
				txtPresentDay.get(tbIndex).setValue("");
				txtPresentDay.get(tbIndex).focus();
			}

			else if(flag == "LD")
			{
				txtLeaveWithPay.get(tbIndex).setValue("");
				txtLeaveWithPay.get(tbIndex).focus();
			}

			else if(flag == "LWP")
			{
				txtLeaveWithoutPay.get(tbIndex).setValue("");
				txtLeaveWithoutPay.get(tbIndex).focus();
			}
		}

		if(holidayDuty > holiday)
		{
			txtHolidayDuty.get(tbIndex).setValue("");
			txtHolidayDuty.get(tbIndex).focus();
			ret = false;
			showNotification("Warning", "Please check Holiday Duty < Holiday!!!", Notification.TYPE_WARNING_MESSAGE);
		}
		return ret;
	}

	private void tableRowAdd(final int ar)
	{
		lblSl.add(ar, new Label());
		lblSl.get(ar).setWidth("100%");
		lblSl.get(ar).setValue(ar+1);

		lblAutoEmployeeID.add(ar, new Label());
		lblAutoEmployeeID.get(ar).setWidth("100%");

		lblEmployeeID.add(ar, new Label());
		lblEmployeeID.get(ar).setWidth("100%");

		lblFingerID.add(ar, new Label());
		lblFingerID.get(ar).setWidth("100%");

		lblEmployeeName.add(ar, new Label());
		lblEmployeeName.get(ar).setWidth("100%");

		lblDesignationID.add(ar, new Label());
		lblDesignationID.get(ar).setWidth("100%");

		lblDesignation.add(ar, new Label());
		lblDesignation.get(ar).setWidth("100%");

		lblServiceType.add(ar, new Label());
		lblServiceType.get(ar).setWidth("100%");

		lblJoiningDate.add(ar, new Label());
		lblJoiningDate.get(ar).setWidth("100%");

		lblUnitID.add(ar, new Label());
		lblUnitID.get(ar).setWidth("100%");

		lblUnitName.add(ar, new Label());
		lblUnitName.get(ar).setWidth("100%");

		
		lblDepartmentID.add(ar, new Label());
		lblDepartmentID.get(ar).setWidth("100%");

		lblDepartmentName.add(ar, new Label());
		lblDepartmentName.get(ar).setWidth("100%");

		
		lblSectionID.add(ar, new Label());
		lblSectionID.get(ar).setWidth("100%");

		lblSectionName.add(ar, new Label());
		lblSectionName.get(ar).setWidth("100%");

		lblMonthDay.add(ar, new Label());
		lblMonthDay.get(ar).setWidth("100%");
		lblMonthDay.get(ar).setImmediate(true);

		txtHoliday.add(ar, new AmountField());
		txtHoliday.get(ar).setWidth("100%");
		txtHoliday.get(ar).setImmediate(true);
		txtHoliday.get(ar).addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(!lblAutoEmployeeID.get(ar).getValue().toString().isEmpty())
				{
					if(!txtHoliday.get(ar).getValue().toString().isEmpty())
					{
						if(dayCalculation(ar,"HD"))
						{
							txtPresentDay.get(ar).focus();
						}
						else
						{
							txtHoliday.get(ar).focus();
						}
					}
				}
			}
		});

		lblWorkingDay.add(ar, new Label());
		lblWorkingDay.get(ar).setWidth("100%");
		lblWorkingDay.get(ar).setImmediate(true);

		txtHolidayDuty.add(ar, new AmountField());
		txtHolidayDuty.get(ar).setWidth("100%");
		txtHolidayDuty.get(ar).setImmediate(true);
		txtHolidayDuty.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!lblAutoEmployeeID.get(ar).getValue().toString().isEmpty())
				{
					if(!txtHolidayDuty.get(ar).getValue().toString().isEmpty())
					{
						if(dayCalculation(ar,"HDD"))
						{
							txtPresentDay.get(ar).focus();
						}
						else
						{
							txtHolidayDuty.get(ar).focus();
						}
					}
				}
			}
		});

		txtPresentDay.add(ar, new AmountField());
		txtPresentDay.get(ar).setWidth("100%");
		txtPresentDay.get(ar).setImmediate(true);
		txtPresentDay.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!lblAutoEmployeeID.get(ar).getValue().toString().isEmpty())
				{
					if(!txtPresentDay.get(ar).getValue().toString().isEmpty())
					{
						if(dayCalculation(ar,"PD"))
						{
							if(lblServiceType.get(ar).getValue().toString().equalsIgnoreCase("Management") || 
									lblServiceType.get(ar).getValue().toString().equalsIgnoreCase("Officer") ||
									lblServiceType.get(ar).getValue().toString().equalsIgnoreCase("Staff"))
							{
								txtMealDay.get(ar).setValue(txtPresentDay.get(ar).getValue());
							}
							else
							{
								txtMealDay.get(ar).setValue("0");

							}
							txtLeaveWithPay.get(ar).focus();
						}
						else
						{
							txtPresentDay.get(ar).setValue("");
							txtPresentDay.get(ar).focus();
						}
					}
				}
			}
		});

		txtAbsentDay.add(ar, new Label());
		txtAbsentDay.get(ar).setWidth("100%");
		txtAbsentDay.get(ar).setImmediate(true);
		txtLeaveWithPay.add(ar, new AmountField());
		txtLeaveWithPay.get(ar).setWidth("100%");
		txtLeaveWithPay.get(ar).setImmediate(true);
		txtLeaveWithPay.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!lblAutoEmployeeID.get(ar).getValue().toString().isEmpty())
				{
					if(!txtLeaveWithPay.get(ar).getValue().toString().isEmpty())
					{
						if(dayCalculation(ar,"LD"))
						{
							txtLeaveWithoutPay.get(ar).focus();
							
						}
						else
						{
							txtLeaveWithPay.get(ar).focus();
							txtLeaveWithPay.get(ar).setValue("0");
						}
					}
				}
			}
		});

		txtLeaveWithoutPay.add(ar, new AmountField());
		txtLeaveWithoutPay.get(ar).setWidth("100%");
		txtLeaveWithoutPay.get(ar).setImmediate(true);
		txtLeaveWithoutPay.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!lblAutoEmployeeID.get(ar).getValue().toString().isEmpty())
				{
					if(!txtLeaveWithoutPay.get(ar).getValue().toString().isEmpty())
					{
						if(dayCalculation(ar,"LWP"))
						{
							txtOTHour.get(ar).focus();
						}
						else
						{
							txtLeaveWithoutPay.get(ar).setValue("");
							txtLeaveWithoutPay.get(ar).focus();
						}
					}
				}
			}
		});

		txtOTHour.add(ar, new AmountField());
		txtOTHour.get(ar).setWidth("100%");
		txtOTHour.get(ar).setImmediate(true);
		txtOTHour.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!lblAutoEmployeeID.get(ar).getValue().toString().isEmpty())
				{
					if(!txtOTHour.get(ar).getValue().toString().isEmpty())
					{
						txtOTMin.get(ar).focus();
					}
				}
			}
		});

		txtOTMin.add(ar, new AmountField());
		txtOTMin.get(ar).setWidth("100%");
		txtOTMin.get(ar).setImmediate(true);
		txtOTMin.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!lblAutoEmployeeID.get(ar).getValue().toString().isEmpty())
				{
					if(!txtOTMin.get(ar).getValue().toString().isEmpty())
					{
						if(!lblAutoEmployeeID.get(ar+1).getValue().toString().isEmpty())
						{
							txtHolidayDuty.get(ar+1).focus();
						}
					}
				}
			}
		});

		txtMealDay.add(ar, new AmountField());
		txtMealDay.get(ar).setWidth("100%");
		txtMealDay.get(ar).setImmediate(true);
		
		txtlateatt.add(ar, new AmountField());
		txtlateatt.get(ar).setWidth("100%");
		txtlateatt.get(ar).setImmediate(true);

		btnDel.add(ar, new NativeButton());
		btnDel.get(ar).setWidth("100%");
		btnDel.get(ar).setIcon(new ThemeResource("../icons/cancel.png"));
		btnDel.get(ar).setStyleName("Transparent");
		btnDel.get(ar).addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				lblAutoEmployeeID.get(ar).setValue("");
				lblEmployeeID.get(ar).setValue("");
				lblFingerID.get(ar).setValue("");
				lblEmployeeName.get(ar).setValue("");
				lblDesignationID.get(ar).setValue("");
				lblDesignation.get(ar).setValue("");
				lblServiceType.get(ar).setValue("");
				lblJoiningDate.get(ar).setValue("");
				lblUnitID.get(ar).setValue("");
				lblUnitName.get(ar).setValue("");
				lblDepartmentID.get(ar).setValue("");
				lblDepartmentName.get(ar).setValue("");
				lblSectionID.get(ar).setValue("");
				lblSectionName.get(ar).setValue("");
				lblMonthDay.get(ar).setValue("");
				txtHoliday.get(ar).setValue("");
				lblWorkingDay.get(ar).setValue("");
				txtPresentDay.get(ar).setValue("");
				txtLeaveWithPay.get(ar).setValue("");
				txtAbsentDay.get(ar).setValue("");
				txtLeaveWithoutPay.get(ar).setValue("");
				txtHolidayDuty.get(ar).setValue("");
				txtOTHour.get(ar).setValue("");
				txtOTMin.get(ar).setValue("");
				txtMealDay.get(ar).setValue("");

				for(int tbIndex=ar;tbIndex<lblAutoEmployeeID.size();tbIndex++)
				{
					if(tbIndex+1<lblAutoEmployeeID.size())
					{
						if(!lblAutoEmployeeID.get(tbIndex+1).getValue().toString().trim().equals(""))
						{
							lblAutoEmployeeID.get(tbIndex).setValue(lblAutoEmployeeID.get(tbIndex+1).getValue().toString().trim());
							lblEmployeeID.get(tbIndex).setValue(lblEmployeeID.get(tbIndex+1).getValue().toString().trim());
							lblFingerID.get(tbIndex).setValue(lblFingerID.get(tbIndex+1).getValue().toString().trim());
							lblEmployeeName.get(tbIndex).setValue(lblEmployeeName.get(tbIndex+1).getValue().toString().trim());
							lblDesignationID.get(tbIndex).setValue(lblDesignationID.get(tbIndex+1).getValue().toString().trim());
							lblDesignation.get(tbIndex).setValue(lblDesignation.get(tbIndex+1).getValue().toString().trim());
							lblServiceType.get(tbIndex).setValue(lblServiceType.get(tbIndex+1).getValue().toString().trim());
							lblJoiningDate.get(tbIndex).setValue(lblJoiningDate.get(tbIndex+1).getValue().toString().trim());
							lblUnitID.get(tbIndex).setValue(lblUnitID.get(tbIndex+1).getValue().toString().trim());
							lblUnitName.get(tbIndex).setValue(lblUnitName.get(tbIndex+1).getValue().toString().trim());							
							lblDepartmentID.get(tbIndex).setValue(lblDepartmentID.get(tbIndex+1).getValue().toString().trim());
							lblDepartmentName.get(tbIndex).setValue(lblDepartmentName.get(tbIndex+1).getValue().toString().trim());							
							lblSectionID.get(tbIndex).setValue(lblSectionID.get(tbIndex+1).getValue().toString().trim());
							lblSectionName.get(tbIndex).setValue(lblSectionName.get(tbIndex+1).getValue().toString().trim());
							lblMonthDay.get(tbIndex).setValue(lblMonthDay.get(tbIndex+1).getValue().toString().trim());
							txtHoliday.get(tbIndex).setValue(txtHoliday.get(tbIndex+1).getValue().toString().trim());
							lblWorkingDay.get(tbIndex).setValue(lblWorkingDay.get(tbIndex+1).getValue().toString().trim());
							txtPresentDay.get(tbIndex).setValue(txtPresentDay.get(tbIndex+1).getValue().toString().trim());
							txtLeaveWithPay.get(tbIndex).setValue(txtLeaveWithPay.get(tbIndex+1).getValue().toString().trim());
							txtAbsentDay.get(tbIndex).setValue(txtAbsentDay.get(tbIndex+1).getValue().toString().trim());
							txtLeaveWithoutPay.get(tbIndex).setValue(txtLeaveWithoutPay.get(tbIndex+1).getValue().toString().trim());						
							txtHolidayDuty.get(tbIndex).setValue(txtHolidayDuty.get(tbIndex).getValue().toString().trim());
							txtOTHour.get(tbIndex).setValue(txtOTHour.get(tbIndex+1).getValue());
							txtOTMin.get(tbIndex).setValue(txtOTMin.get(tbIndex+1).getValue());
							txtMealDay.get(tbIndex).setValue(txtMealDay.get(tbIndex+1).getValue());	
							lblAutoEmployeeID.get(tbIndex+1).setValue("");
							lblEmployeeID.get(tbIndex+1).setValue("");
							lblFingerID.get(tbIndex+1).setValue("");
							lblEmployeeName.get(tbIndex+1).setValue("");
							lblDesignationID.get(tbIndex+1).setValue("");
							lblDesignation.get(tbIndex+1).setValue("");
							lblServiceType.get(tbIndex+1).setValue("");
							lblJoiningDate.get(tbIndex+1).setValue("");
							lblUnitID.get(tbIndex+1).setValue("");
							lblUnitName.get(tbIndex+1).setValue("");						
							lblDepartmentID.get(tbIndex+1).setValue("");
							lblDepartmentName.get(tbIndex+1).setValue("");					
							lblSectionID.get(tbIndex+1).setValue("");
							lblSectionName.get(tbIndex+1).setValue("");
							lblMonthDay.get(tbIndex+1).setValue("");
							txtHoliday.get(tbIndex+1).setValue("");
							lblWorkingDay.get(tbIndex+1).setValue("");
							txtPresentDay.get(tbIndex+1).setValue("");
							txtLeaveWithPay.get(tbIndex+1).setValue("");
							txtAbsentDay.get(tbIndex+1).setValue("");
							txtLeaveWithoutPay.get(tbIndex+1).setValue("");
							txtHolidayDuty.get(tbIndex+1).setValue("");
							txtOTHour.get(tbIndex+1).setValue("");
							txtOTMin.get(tbIndex+1).setValue("");
							txtMealDay.get(tbIndex+1).setValue("");
							txtlateatt.get(tbIndex+1).setValue("");
							index--;
						}
					}
				}
			}
		});

		table.addItem(new Object[]{lblSl.get(ar),lblAutoEmployeeID.get(ar),lblEmployeeID.get(ar),lblFingerID.get(ar),lblEmployeeName.get(ar),
				lblDesignationID.get(ar),lblDesignation.get(ar),lblServiceType.get(ar),lblJoiningDate.get(ar),lblUnitID.get(ar),lblUnitName.get(ar),
				lblDepartmentID.get(ar),lblDepartmentName.get(ar),lblSectionID.get(ar),lblSectionName.get(ar),lblMonthDay.get(ar),txtHoliday.get(ar),
				lblWorkingDay.get(ar),txtPresentDay.get(ar),txtLeaveWithPay.get(ar),txtAbsentDay.get(ar),txtLeaveWithoutPay.get(ar),txtHolidayDuty.get(ar),
				txtOTHour.get(ar),txtOTMin.get(ar),txtMealDay.get(ar),txtlateatt.get(ar),btnDel.get(ar)}, ar);
	}

	public AbsoluteLayout buildMainLayout()
	{
		mainlayout=new AbsoluteLayout();
		mainlayout.setWidth("1100.0px");
		mainlayout.setHeight("520.0px");

		cmbUnit=new ComboBox();
		cmbUnit.setWidth("290.0px");
		cmbUnit.setHeight("-1px");
		cmbUnit.setImmediate(true);
		cmbUnit.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainlayout.addComponent(new Label("Project Name : "), "top:20.0px;left:20.0px;");
		mainlayout.addComponent(cmbUnit, "top:18.0px;left:140.0px");

		cmbDepartment=new ComboBox();
		cmbDepartment.setWidth("290.0px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setImmediate(true);
		cmbDepartment.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainlayout.addComponent(new Label("Department Name : "), "top:50.0px;left:20.0px;");
		mainlayout.addComponent(cmbDepartment, "top:48.0px;left:140.0px");

		chkAllDepartment=new CheckBox("All");
		chkAllDepartment.setImmediate(true);
		mainlayout.addComponent(chkAllDepartment, "top:50.0px;left:430.0px");

		cmbSection=new ComboBox();
		cmbSection.setWidth("290.0px");
		cmbSection.setHeight("-1px");
		cmbSection.setImmediate(true);
		cmbSection.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainlayout.addComponent(new Label("Section Name : "), "top:80.0px;left:20.0px;");
		mainlayout.addComponent(cmbSection, "top:78.0px;left:140.0px");

		chkAllSection=new CheckBox("All");
		chkAllSection.setImmediate(true);
		mainlayout.addComponent(chkAllSection, "top:80.0px;left:430.0px");

		dMonth=new PopupDateField();
		dMonth.setImmediate(true);
		dMonth.setWidth("140px");
		dMonth.setResolution(PopupDateField.RESOLUTION_MONTH);
		dMonth.setDateFormat("MMMMM-yyyy");
		dMonth.setValue(new Date());
		mainlayout.addComponent(new Label("Month : "), "top:20.0px;left:475.0px;");
		mainlayout.addComponent(dMonth, "top:18.0px;left:560.0px;");

		cmbEmployee=new ComboBox();
		cmbEmployee.setWidth("290.0px");
		cmbEmployee.setHeight("-1px");
		cmbEmployee.setImmediate(true);
		cmbEmployee.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainlayout.addComponent(new Label("Employee : "), "top:50.0px;left:475.0px;");
		mainlayout.addComponent(cmbEmployee, "top:48.0px;left:560.0px;");

		chkAllEmp=new CheckBox("All");
		chkAllEmp.setImmediate(true);
		mainlayout.addComponent(chkAllEmp, "top:50.0px;left:860.0px");

		table.setWidth("99%");
		table.setHeight("310.0px");
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("SL", Label.class, new Label());
		table.setColumnWidth("SL", 20);

		table.addContainerProperty("Emp ID", Label.class, new Label());
		table.setColumnWidth("Emp ID", 50);

		table.addContainerProperty("Emp.ID", Label.class, new Label());
		table.setColumnWidth("Emp.ID",50);

		table.addContainerProperty("Finger ID", Label.class, new Label());
		table.setColumnWidth("Finger ID", 70);

		table.addContainerProperty("Employee Name", Label.class, new Label());
		table.setColumnWidth("Employee Name", 120);

		table.addContainerProperty("Designation ID", Label.class, new Label());
		table.setColumnWidth("Designation ID", 30);

		table.addContainerProperty("Designation", Label.class, new Label());
		table.setColumnWidth("Designation", 130);

		table.addContainerProperty("Service Status", Label.class, new Label());
		table.setColumnWidth("Service Status", 100);

		table.addContainerProperty("Joining Date", Label.class, new Label());
		table.setColumnWidth("Joining Date", 90);

		table.addContainerProperty("Unit ID", Label.class, new Label());
		table.setColumnWidth("Unit ID", 20);

		table.addContainerProperty("Unit Name", Label.class, new Label());
		table.setColumnWidth("Unit Name", 200);
		
		table.addContainerProperty("Department ID", Label.class, new Label());
		table.setColumnWidth("Department ID", 30);

		table.addContainerProperty("Department Name", Label.class, new Label());
		table.setColumnWidth("Department Name", 150);
		
		table.addContainerProperty("Section ID", Label.class, new Label());
		table.setColumnWidth("Section ID", 30);

		table.addContainerProperty("Section Name", Label.class, new Label());
		table.setColumnWidth("Section Name", 150);

		table.addContainerProperty("MD", Label.class, new Label());
		table.setColumnWidth("MD", 30);

		table.addContainerProperty("HD", AmountField.class, new AmountField());
		table.setColumnWidth("HD", 30);

		table.addContainerProperty("WD", Label.class, new Label());
		table.setColumnWidth("WD", 30);

		table.addContainerProperty("PD", AmountField.class, new AmountField());
		table.setColumnWidth("PD", 30);
		
		table.addContainerProperty("Leave", AmountField.class, new AmountField());
		table.setColumnWidth("Leave", 30);

		table.addContainerProperty("Abs Day", Label.class, new Label());
		table.setColumnWidth("Abs Day", 30);

	
		table.addContainerProperty("LWP", AmountField.class, new AmountField());
		table.setColumnWidth("LWP", 40);

		table.addContainerProperty("HDD", AmountField.class, new AmountField());
		table.setColumnWidth("HDD", 30);

		table.addContainerProperty("OT HH", AmountField.class, new AmountField());
		table.setColumnWidth("OT HH", 40);

		table.addContainerProperty("OT MM", AmountField.class, new AmountField());
		table.setColumnWidth("OT MM", 40);

		table.addContainerProperty("M.D", AmountField.class, new AmountField());
		table.setColumnWidth("M.D", 40);
		
		table.addContainerProperty("Late Att.", AmountField.class, new AmountField());
		table.setColumnWidth("Late Att.", 40);

		table.addContainerProperty("Remove", NativeButton.class, new NativeButton());
		table.setColumnWidth("Remove", 45);

		table.setColumnCollapsed("Emp ID", true);
		table.setColumnCollapsed("Finger ID", true);
		table.setColumnCollapsed("Designation ID", true);
		table.setColumnCollapsed("Department ID", true);
		table.setColumnCollapsed("Department Name", true);
		table.setColumnCollapsed("Section ID", true);
		table.setColumnCollapsed("Section Name", true);
		table.setColumnCollapsed("Unit ID", true);
		table.setColumnCollapsed("Unit Name", true);
		table.setColumnCollapsed("M.D", true);
		table.setColumnCollapsed("Service Status", true); 
		
		table.setStyleName("wordwrap-headers");

		table.setColumnAlignments(new String[]{Table.ALIGN_RIGHT,Table.ALIGN_LEFT,Table.ALIGN_CENTER,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,
				Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_CENTER,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_RIGHT,Table.ALIGN_RIGHT,Table.ALIGN_RIGHT,
				Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,
				Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER});
		mainlayout.addComponent(table, "top:120.0px;left:20.0px");
		tableinitialize();
		mainlayout.addComponent(new Label("MD = Month Day || HD = Holiday || WD = Working Day || HDD = Holiday Duty || PD = Present Day || Abs Day = Absent Day || Leave = Leave With Pay || LWP = Leave Without Pay || OT HH = OverTime Hour || OT MM = Overtime Minute || Late Att.=Late Attendence"), "top:430.0px; left:20.0px;");
		mainlayout.addComponent(cButton, "top:470.0px;left:390.0px;");
		return mainlayout;
	}
}
