package com.appform.hrmModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.CommonMethod;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.TimeField;
import com.common.share.MessageBox.EventListener;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.MessageBox.ButtonType;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class DateBetweenAttendanceManuallyMachine extends Window
{
	SessionBean sessionbean;
	private AbsoluteLayout mainlayout;
	private ComboBox cmbUnit;
	private ComboBox cmbDepartment,cmbSection,cmbEmployee;
	private CheckBox chkDepartmentAll,chkSectionAll,chkEmployeeAll;
	private PopupDateField fromdate;
	private PopupDateField todate;
	private TimeField InHrs=new TimeField();
	private TimeField InMin=new TimeField();
	private TextField InTF=new TextField();
	private TimeField outHrs=new TimeField();
	private TimeField outMin=new TimeField();
	private TextField outTF=new TextField();
	private CheckBox chkPermitAll=new CheckBox();
	private TextField txtReasonForAll=new TextField();

	private Table table=new Table();
	private ArrayList<NativeButton> btnDel=new ArrayList<NativeButton>();
	private ArrayList<Label> lblSl=new ArrayList<Label>();
	private ArrayList<Label> lblAutoEmployeeID=new ArrayList<Label>();
	private ArrayList<Label> lblEmployeeID=new ArrayList<Label>();
	private ArrayList<Label> lblProximityID=new ArrayList<Label>();
	private ArrayList<Label> lblEmployeeName=new ArrayList<Label>();
	private ArrayList<Label> lblDesignationID=new ArrayList<Label>();
	private ArrayList<Label> lblDesignation=new ArrayList<Label>();
	private ArrayList<Label> lblUnitID=new ArrayList<Label>();
	private ArrayList<Label> lblUnitName=new ArrayList<Label>();
	private ArrayList<Label> lblDepartmentID=new ArrayList<Label>();
	private ArrayList<Label> lblDepartmentName=new ArrayList<Label>();
	private ArrayList<Label> lblSectionID=new ArrayList<Label>();
	private ArrayList<Label> lblSectionName=new ArrayList<Label>();
	private ArrayList<TextField> txtReason=new ArrayList<TextField>();
	private ArrayList<CheckBox> chkPermit=new ArrayList<CheckBox>();
	

	private CommonButton cButton=new CommonButton("New", "Save", "", "", "Refresh", "", "", "", "", "Exit");

	private SimpleDateFormat dfDateFormate=new SimpleDateFormat("yyyy-MM-dd");

	boolean isSave=false;
	boolean isRefresh=false;
	int index=0;
	String notifi="";
	private CommonMethod cm;
	private String menuId = "";
	private ArrayList<Component> allComp=new ArrayList<Component>();

	public DateBetweenAttendanceManuallyMachine(SessionBean sessionBean,String menuId)
	{
		this.sessionbean=sessionBean;
		this.setCaption("DATE BETWEEN ATTENDANCE MANUALLY(MACHINE) :: "+sessionbean.getCompany());
		buildMainLayout();
		this.setContent(mainlayout);
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		componentEnable(true);
		btnEnable(true);
		setEventAction();
		cmbUnitDataLoad();
		FocusEnter();
		cmbDepartment.focus();
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
	private void FocusEnter()
	{
		allComp.add(cmbDepartment);
		allComp.add(cmbSection);
		allComp.add(cmbEmployee);
		allComp.add(fromdate);
		allComp.add(todate);
		allComp.add(InHrs);
		allComp.add(InMin);
		allComp.add(InMin);
		allComp.add(InTF);
		allComp.add(outHrs);
		allComp.add(outMin);
		allComp.add(outTF);
		allComp.add(txtReasonForAll);
		for(int ind=0;ind<lblProximityID.size();ind++)
			allComp.add(txtReason.get(ind));
		new FocusMoveByEnter(this, allComp);
	}

	private void cmbUnitDataLoad()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql="select distinct vUnitId,vUnitName from tbEmpOfficialPersonalInfo  order by vUnitName";
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
			showNotification("cmbUnitDataLoad", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			session.close();
		}
	}

	private void cmbDepartmentDataLoad()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql="select distinct vDepartmentId,vDepartmentName from tbEmpOfficialPersonalInfo  where vUnitId like '"+cmbUnit.toString()+"' " +
					"order by vDepartmentName";
			System.out.println("cmbDepartmentDataLoad: "+sql);
			
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
		finally
		{
			session.close();
		}
	}
	private void cmbSectionDataLoad()
	{
		String dept="%";
		if(!chkDepartmentAll.booleanValue())
		{
			dept=cmbDepartment.getValue().toString();
		}
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql="select distinct vSectionId,vSectionName from tbEmpOfficialPersonalInfo  " +
					"where vUnitId like '"+cmbUnit.toString()+"' and vDepartmentId like '"+dept+"' " +
					"order by vSectionName";
			System.out.println("cmbDepartmentDataLoad: "+sql);
			
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
		finally
		{
			session.close();
		}
	}

	private void employeeDataLoad()
	{
		String unit="",dept="%",section="%";
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		
		unit=cmbUnit.getValue().toString();
		if(!chkDepartmentAll.booleanValue())
		{
			dept=cmbDepartment.getValue().toString();
		}
		if(!chkSectionAll.booleanValue())
		{
			section=cmbSection.getValue().toString();
		}
		
		try
		{
			String sql="select distinct vEmployeeId,vEmployeeCode,vEmployeeName from tbEmpOfficialPersonalInfo " +
					"where bStatus='1' and vUnitId like '"+unit+"' and vDepartmentId like '"+dept+"' and vSectionId like '"+section+"'";
			System.out.println("employeeDataLoad: "+sql);
			
			List <?> lst=session.createSQLQuery(sql).list();
			cmbEmployee.removeAllItems();
			if(!lst.isEmpty())
			{
				Iterator <?> itr=lst.iterator();
				while(itr.hasNext())
				{
					Object[] element=(Object[])itr.next();
					cmbEmployee.addItem(element[0]);
					cmbEmployee.setItemCaption(element[0], element[1]+"-"+element[2]);
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("employeeDataLoad", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			session.close();
		}
	}
	private void setEventAction()
	{
		cmbUnit.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbDepartment.removeAllItems();
				chkDepartmentAll.setValue(false);
				if(cmbUnit.getValue()!=null)
				{
					cmbDepartmentDataLoad();
					cmbDepartment.setValue(null);
					chkDepartmentAll.setValue(false);
					
					cmbEmployee.removeAllItems();
					cmbEmployee.setEnabled(true);
					chkEmployeeAll.setValue(false);
					tableclear();
				}
			}
		});
		
		txtReasonForAll.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				for(int i=0;i<txtReason.size();i++)
				{
					if(!lblAutoEmployeeID.get(i).getValue().toString().isEmpty())
					{
						txtReason.get(i).setValue(txtReasonForAll.getValue());
					}
				}
			}
		});

		cmbDepartment.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbUnit.getValue()!=null)
				{
					if(cmbDepartment.getValue()!=null)
					{
						cmbSection.removeAllItems();
						cmbSection.setEnabled(true);
						chkSectionAll.setValue(false);

						cmbEmployee.removeAllItems();
						cmbEmployee.setEnabled(true);
						chkEmployeeAll.setValue(false);
						tableclear();
						cmbSectionDataLoad();
						index=0;
					}
				}
			}
		});

		chkDepartmentAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbUnit.getValue()!=null)
				{
					cmbSection.setEnabled(true);
					chkSectionAll.setValue(false);
					cmbEmployee.setEnabled(true);
					chkEmployeeAll.setValue(false);
					if(chkDepartmentAll.booleanValue())
					{
						cmbDepartment.setValue(null);
						cmbDepartment.setEnabled(false);
						tableclear();
						cmbSectionDataLoad();
						index=0;
					}
					else
					{
						cmbDepartment.setEnabled(true);
						cmbEmployee.setEnabled(true);
						cmbEmployee.removeAllItems();
						chkEmployeeAll.setValue(false);
						tableclear();
					}
				}
			}
		});

		cmbSection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbUnit.getValue()!=null)
				{
					if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
					{
						if(cmbSection.getValue()!=null)
						{
							cmbEmployee.removeAllItems();
							cmbEmployee.setEnabled(true);
							chkEmployeeAll.setValue(false);
							tableclear();
							employeeDataLoad();
							index=0;
						}
					}
				}
			}
		});

		chkSectionAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbUnit.getValue()!=null)
				{
					if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
					{
						cmbEmployee.setEnabled(true);
						chkEmployeeAll.setValue(false);
						if(chkSectionAll.booleanValue())
						{
							cmbSection.setValue(null);
							cmbSection.setEnabled(false);
							tableclear();
							employeeDataLoad();
							index=0;
						}
						else
						{
							cmbSection.setEnabled(true);
							cmbEmployee.setEnabled(true);
							cmbEmployee.removeAllItems();
							chkEmployeeAll.setValue(false);
							tableclear();
						}
					}
				}
			}
		});
		
		cmbEmployee.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbUnit.getValue()!=null)
				{
					if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
					{
						if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
						{
							if(cmbEmployee.getValue()!=null || chkEmployeeAll.booleanValue())
							{
								tableValueAdd();
							}
						}
					}
				}
			}
		});

		chkEmployeeAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbUnit.getValue()!=null)
				{
					if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
					{
						if(chkEmployeeAll.booleanValue())
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
					else
					{
						if(!isSave && !isRefresh)
						{
							chkEmployeeAll.setValue(false);
							showNotification("Warning", "Please Select Section Name!!!", Notification.TYPE_WARNING_MESSAGE);
						}
					}
				}
			}
		});
		
		InHrs.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!InHrs.getValue().toString().isEmpty())	
				{
					if(Integer.parseInt(InHrs.getValue().toString())>12)
					{
						InHrs.setValue("");
						showNotification("Warning", "Provide In Hour!!!",Notification.TYPE_WARNING_MESSAGE);
					}
				}				
			}
		});

		InMin.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!InMin.getValue().toString().isEmpty())
				{
					if(Integer.parseInt(InMin.getValue().toString())>59)
					{
						InMin.setValue("");
						showNotification("Warning", "Provide In Min.!!!",Notification.TYPE_WARNING_MESSAGE);
					}
				}
			}
		});

		InTF.addListener(new TextChangeListener()
		{
			public void textChange(TextChangeEvent event)
			{

				if(event.getText().equalsIgnoreCase("a"))
				{
					InTF.setValue("AM");
				}

				if(event.getText().equalsIgnoreCase("p"))
				{
					InTF.setValue("PM");
				}
			}
		});

		outHrs.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!outHrs.getValue().toString().isEmpty())	
				{
					if(Integer.parseInt(outHrs.getValue().toString())>12)
					{
						outHrs.setValue("");
						showNotification("Warning", "Provide Out Hour!!!",Notification.TYPE_WARNING_MESSAGE);
					}
				}				
			}
		});

		outMin.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!outMin.getValue().toString().isEmpty())
				{
					if(Integer.parseInt(outMin.getValue().toString())>59)
					{
						outMin.setValue("");
						showNotification("Warning", "Provide Out Min.!!!",Notification.TYPE_WARNING_MESSAGE);
					}
				}
			}
		});

		outTF.addListener(new TextChangeListener()
		{
			public void textChange(TextChangeEvent event)
			{
				if(event.getText().equalsIgnoreCase("a"))
				{
					outTF.setValue("AM");
				}

				if(event.getText().equalsIgnoreCase("p"))
				{
					outTF.setValue("PM");
				}
			}
		});

		chkPermitAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(chkPermitAll.booleanValue())
				{
					for(int i=0;i<lblEmployeeID.size();i++)
					{
						if(!lblProximityID.get(i).getValue().toString().isEmpty())
						{
							chkPermit.get(i).setValue(true);
						}
					}
				}
				else
				{
					for(int i=0;i<lblEmployeeID.size();i++)
					{
						if(!lblProximityID.get(i).getValue().toString().isEmpty())
						{
							chkPermit.get(i).setValue(false);
						}
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
				componentEnable(false);
				btnEnable(false);
				index=0;
			}
		});

		cButton.btnSave.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(chkRow())
				{
					if(fromdate.getValue()!=null)
					{
						if(todate.getValue()!=null)
						{
							if(InHrs.getValue().toString().trim().length()>0)
							{
								if(InMin.getValue().toString().trim().length()>0)
								{
									if(InTF.getValue().toString().trim().length()>0)
									{
										if(outHrs.getValue().toString().trim().length()>0)
										{
											if(outMin.getValue().toString().trim().length()>0)
											{	
												if(outTF.getValue().toString().trim().equals("AM") || outTF.getValue().toString().trim().equals("PM"))
												{
													if(chkTableData())
													{
														isSave=true;
														saveButtonEvent();
													}
													else
													{
														showNotification("Warning", notifi, Notification.TYPE_WARNING_MESSAGE);
													}
												}
												else
												{
													outTF.setValue("");
													outTF.focus();
													showNotification("Warning", "Please Enter Out Time Format [AM/PM]!!!", Notification.TYPE_WARNING_MESSAGE);
												}
											}
											else
											{
												outMin.focus();
												showNotification("Warning", "Please Enter Out Min.!!!", Notification.TYPE_WARNING_MESSAGE);
											}
										}
										else
										{
											outHrs.focus();
											showNotification("Warning", "Please Enter Out Hour!!!", Notification.TYPE_WARNING_MESSAGE);
										}
									}
									else
									{
										InTF.focus();
										showNotification("Warning", "Please Enter In Time Format [AM/PM]!!!", Notification.TYPE_WARNING_MESSAGE);
									}
								}
								else
								{
									outMin.focus();
									showNotification("Warning", "Please Enter In Min.!!!", Notification.TYPE_WARNING_MESSAGE);
								}
							}
							else
							{
								InHrs.focus();
								showNotification("Warning", "Please Enter In Hour!!!", Notification.TYPE_WARNING_MESSAGE);
							}
						}
						else
						{
							showNotification("Warning", "Please Enter To Date!!!", Notification.TYPE_WARNING_MESSAGE);
							todate.focus();
						}
					}
					else
					{
						showNotification("Warning", "Please Enter From Date!!!", Notification.TYPE_WARNING_MESSAGE);
						fromdate.focus();
					}
				}
				else
				{
					showNotification("Warning", notifi, Notification.TYPE_WARNING_MESSAGE);
				}
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

	private boolean chkRow()
	{
		boolean ret=false;
		int count=0;
		for(int tbIndex=0;tbIndex<lblAutoEmployeeID.size();tbIndex++)
		{
			if(lblAutoEmployeeID.get(tbIndex).getValue().toString().trim().length()>0)
			{
				count++;
				ret=true;
			}
			else if(count==0)
			{
				ret=false;
				notifi="No Data Found!!!";
			}
		}
		return ret;
	}

	@SuppressWarnings("unused")
	private boolean chkTableData()
	{
		boolean ret=false;
		int count=0;
		for(int tbIndex=0;tbIndex<lblAutoEmployeeID.size();tbIndex++)
		{
			if(lblAutoEmployeeID.get(tbIndex).getValue().toString().trim().length()>0)
			{
				if(txtReason.get(tbIndex).getValue().toString().trim().length()>0)
				{
					if(chkPermit.get(tbIndex).booleanValue())
					{
						if(!checkSalary(tbIndex))
							ret=true;
						else
							notifi = "Salary already generated!!!";
					}
					else
					{
						chkPermit.get(tbIndex).focus();
						ret=false;
						notifi="Please Check the Apply Option!!!";
						break;
					}
				}
				else
				{
					txtReason.get(tbIndex).focus();
					ret=false;
					notifi="Please Provide Reason!!!";
					break;
				}
				count++;
			}
		}
		return ret;
	}

	private void saveButtonEvent()
	{
		MessageBox msgbox=new MessageBox(getParent(), "Are You Sure?", MessageBox.Icon.QUESTION, "Do You Want to Update All Information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
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
				}
			}
		});
	}
	
	private void insertdata()
	{
		String sql="";
		String sqlManually="";
		Session session=SessionFactoryUtil.getInstance().openSession();
		Transaction tx=session.beginTransaction();
		try
		{
			String inhour=InHrs.getValue().toString().trim();
			String inMin=InMin.getValue().toString().trim();
			String inTime="";
			if(InTF.getValue().toString().trim().equals("PM"))
				inTime=Integer.toString(Integer.parseInt(inhour)+12)+":"+inMin+":00";
			else
				inTime=inhour+":"+inMin+":00";

			String outhour=outHrs.getValue().toString().trim();
			String OutMin=outMin.getValue().toString().trim();
			String outTime="";
			if(outTF.getValue().toString().trim().equals("PM"))
				outTime=Integer.toString(Integer.parseInt(outhour)+12)+":"+OutMin+":00";
			else
				outTime=outhour+":"+OutMin+":00";

			for(int i=0;i<lblAutoEmployeeID.size();i++) 
			{
				if(!lblAutoEmployeeID.get(i).getValue().toString().isEmpty())
				{
					if(chkPermit.get(i).booleanValue())
					{
						sql="exec prcMonthlyEmployeeAttendanceDateBetween '"+dfDateFormate.format(fromdate.getValue())+"'," +
								"'"+dfDateFormate.format(todate.getValue())+"','"+dfDateFormate.format(fromdate.getValue())+" "+inTime+"'," +
								"'"+dfDateFormate.format(fromdate.getValue())+" "+outTime+"','"+lblAutoEmployeeID.get(i).getValue().toString()+"'," +
								"'"+lblDesignationID.get(i).getValue().toString()+"','"+lblUnitID.get(i).getValue().toString()+"'," +
								"'"+lblDepartmentID.get(i).getValue().toString()+"','"+lblSectionID.get(i).getValue().toString()+"','"+sessionbean.getUserId()+"','"+sessionbean.getUserIp()+"'," +
								"'"+(txtReason.get(i).getValue().toString().trim().isEmpty()?"":txtReason.get(i).getValue().toString().trim())+"'";
						
						System.out.print("prcMonthlyEmployeeAttendance: "+sql);
						
						session.createSQLQuery(sql).executeUpdate();
						session.clear();

						sqlManually="insert into tbAttendanceManuallyDateBetween (vEmployeeID,vEmployeeCode,vProximityID,vEmployeeName," +
								"vdesignationID,vDepartmentId,vSectionID,dAttFromDate,inTime,dAtttoDate,outTime,vReason,userName,userIP," +
								"EntryTime,vUnitID) values('"+lblAutoEmployeeID.get(i).getValue()+"','"+lblEmployeeID.get(i).getValue()+"','"+lblProximityID.get(i).getValue()+"'," +
								"'"+lblEmployeeName.get(i).getValue()+"','"+lblDesignationID.get(i).getValue()+"'," +
								"'"+lblDepartmentID.get(i).getValue()+"','"+lblSectionID.get(i).getValue()+"','"+dfDateFormate.format(fromdate.getValue())+"'," +
								"'"+dfDateFormate.format(fromdate.getValue())+" "+inTime+"','"+dfDateFormate.format(todate.getValue())+"'," +
								"'"+dfDateFormate.format(todate.getValue())+" "+outTime+"','"+(txtReason.get(i).getValue().toString().trim().isEmpty()?"":txtReason.get(i).getValue().toString().trim())+"','"+sessionbean.getUserName()+"'," +
								"'"+sessionbean.getUserIp()+"',getdate(),'"+lblUnitID.get(i).getValue().toString()+"')";
						System.out.print("tbAttendanceManuallyDateBetween: "+sqlManually);
						
						session.createSQLQuery(sqlManually).executeUpdate();
					}
				}
			}
			Notification n=new Notification("All Information Save Successfully!","",Notification.TYPE_TRAY_NOTIFICATION);
			n.setPosition(Notification.POSITION_TOP_RIGHT);
			showNotification(n);
			tx.commit();
		}
		catch (Exception exp)
		{
			tx.rollback();
			showNotification("InsertDate", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			session.close();
		}
	}

	private boolean checkSalary(int index)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select vEmployeeID from tbMonthlySalary " +
					"where vEmployeeID = '"+lblAutoEmployeeID.get(index).getValue().toString()+"' " +
					"and ((MONTH(dSalaryDate) = MONTH('"+dfDateFormate.format(fromdate.getValue())+"') " +
					"and YEAR(dSalaryDate) = YEAR('"+dfDateFormate.format(fromdate.getValue())+"')) " +
					"or (MONTH(dSalaryDate) = MONTH('"+dfDateFormate.format(todate.getValue())+"') " +
					"and YEAR(dSalaryDate) = YEAR('"+dfDateFormate.format(todate.getValue())+"')))";
			System.out.println("checkSalary: "+query);
			
			List <?> lst = session.createSQLQuery(query).list();
			if(!lst.isEmpty())
				return true;
		}
		catch (Exception exp)
		{
			showNotification("checkSalary", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
		return false;
	}

	private void tableValueAdd()
	{
		String unit="",dept="%",section="%",emp="%";
		
		unit=cmbUnit.getValue().toString();
		if(!chkDepartmentAll.booleanValue())
		{
			dept=cmbDepartment.getValue().toString();
		}
		if(!chkSectionAll.booleanValue())
		{
			section=cmbSection.getValue().toString();
		}
		if(!chkEmployeeAll.booleanValue())
		{
			emp=cmbEmployee.getValue().toString();
		}
		
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{

			String sql="select a.vEmployeeId,vEmployeeCode,vProximityId,a.vEmployeeName,vDesignationId," +
					"vDesignationName,vUnitId,vUnitName,vDepartmentID,vDepartmentName,vSectionID,vSectionName,iOtEnable,FridayStatus," +
					"'' iRank,dJoiningDate " +
					"from tbEmpOfficialPersonalInfo a " +
					"where vUnitId='"+unit+"' and vDepartmentId like '"+dept+"' and vSectionId like '"+section+"' and a.vEmployeeId like '"+emp+"' and bStatus=1 " +
					"order by SUBSTRING(vEmployeeCode,3,100) asc ";
			
			System.out.println("tableValueAdd: "+sql);
			
			List <?> lst=session.createSQLQuery(sql).list();
			if(!lst.isEmpty())
			{
				int chkExists=0;
				Iterator <?> itr=lst.iterator();
				while(itr.hasNext())
				{
					Object [] element=(Object[])itr.next();
					boolean check=false;
					for(int chkindex=0;chkindex<lblEmployeeID.size();chkindex++)
					{
						if(lblAutoEmployeeID.get(chkindex).getValue().equals(element[0].toString()))
						{
							check=true;
							break;
						}
					}
					if(!check)
					{
						chkExists=1;
						lblAutoEmployeeID.get(index).setValue(element[0].toString());
						lblEmployeeID.get(index).setValue(element[1].toString());
						lblProximityID.get(index).setValue(element[2].toString());
						lblEmployeeName.get(index).setValue(element[3].toString());
						lblDesignationID.get(index).setValue(element[4].toString());
						lblDesignation.get(index).setValue(element[5].toString());
						lblUnitID.get(index).setValue(element[6].toString());
						lblUnitName.get(index).setValue(element[7].toString());
						lblDepartmentID.get(index).setValue(element[8].toString());
						lblDepartmentName.get(index).setValue(element[9].toString());
						lblSectionID.get(index).setValue(element[10].toString());
						lblSectionName.get(index).setValue(element[11].toString());

						if(index==lblEmployeeID.size()-1)
							tableRowAdd(index+1);

						index++;
					}
					else
					{
						chkExists=0;
					}
				}
				if(chkExists==0)
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
		finally
		{
			session.close();
		}
	}

	private void txtclear()
	{
		cmbUnit.setValue(null);
		chkDepartmentAll.setValue(false);
		cmbDepartment.setValue(null);
		chkSectionAll.setValue(false);
		cmbSection.setValue(null);
		cmbEmployee.setValue(null);
		chkEmployeeAll.setValue(false);
		//opgEmployee.select(null);
		InHrs.setValue("");
		InMin.setValue("");
		InTF.setValue("");
		InTF.setInputPrompt("AM");
		outHrs.setValue("");
		outMin.setValue("");
		outTF.setValue("");
		outTF.setInputPrompt("PM");
		txtReasonForAll.setValue("");
		chkPermitAll.setValue(false);
		tableclear();
	}

	private void tableclear()
	{
		for(int i=0;i<lblEmployeeID.size();i++)
		{
			lblAutoEmployeeID.get(i).setValue("");
			lblEmployeeID.get(i).setValue("");
			lblProximityID.get(i).setValue("");
			lblEmployeeName.get(i).setValue("");
			lblDesignationID.get(i).setValue("");
			lblDesignation.get(i).setValue("");
			lblUnitID.get(i).setValue("");
			lblUnitName.get(i).setValue("");
			lblDepartmentID.get(i).setValue("");
			lblDepartmentName.get(i).setValue("");
			lblSectionID.get(i).setValue("");
			lblSectionName.get(i).setValue("");
			txtReason.get(i).setValue("");
			chkPermit.get(i).setValue(false);
		}
	}

	private void componentEnable(boolean b)
	{
		cmbUnit.setEnabled(!b);
		cmbDepartment.setEnabled(!b);
		chkDepartmentAll.setEnabled(!b);
		cmbSection.setEnabled(!b);
		chkSectionAll.setEnabled(!b);

		cmbEmployee.setEnabled(!b);
		chkEmployeeAll.setEnabled(!b);
		fromdate.setEnabled(!b);
		todate.setEnabled(!b);
		InHrs.setEnabled(!b);
		InMin.setEnabled(!b);
		InTF.setEnabled(!b);
		outHrs.setEnabled(!b);
		outMin.setEnabled(!b);
		outTF.setEnabled(!b);
		txtReasonForAll.setEnabled(!b);
		chkPermitAll.setEnabled(!b);
		table.setEnabled(!b);
	}

	private void btnEnable(boolean b)
	{
		cButton.btnNew.setEnabled(b);
		cButton.btnSave.setEnabled(!b);
		cButton.btnRefresh.setEnabled(!b);
	}

	public void tableinitialize()
	{
		for(int i=0;i<12;i++)
			tableRowAdd(i);
	}

	public void tableRowAdd(final int ar)
	{
		btnDel.add(ar, new NativeButton());
		btnDel.get(ar).setWidth("100%");
		btnDel.get(ar).setIcon(new ThemeResource("../icons/cancel.png"));
		btnDel.get(ar).addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				//if(!lblProximityID.get(ar).getValue().toString().equals(""))
				index=index-1;
				lblAutoEmployeeID.get(ar).setValue("");
				lblEmployeeID.get(ar).setValue("");
				lblProximityID.get(ar).setValue("");
				lblEmployeeName.get(ar).setValue("");
				lblDesignationID.get(ar).setValue("");
				lblDesignation.get(ar).setValue("");
				lblUnitID.get(ar).setValue("");
				lblUnitName.get(ar).setValue("");
				lblDepartmentID.get(ar).setValue("");
				lblDepartmentName.get(ar).setValue("");
				lblSectionID.get(ar).setValue("");
				lblSectionName.get(ar).setValue("");
				txtReason.get(ar).setValue("");
				chkPermit.get(ar).setValue(false);

				for(int tbIndex=ar;tbIndex<lblProximityID.size();tbIndex++)
				{
					if(tbIndex+1<lblProximityID.size())
					{
						if(!lblProximityID.get(tbIndex+1).getValue().toString().equals(""))
						{
							lblAutoEmployeeID.get(tbIndex).setValue(lblAutoEmployeeID.get(tbIndex+1).getValue().toString().trim());
							lblEmployeeID.get(tbIndex).setValue(lblEmployeeID.get(tbIndex+1).getValue().toString().trim());
							lblProximityID.get(tbIndex).setValue(lblProximityID.get(tbIndex+1).getValue().toString().trim());
							lblEmployeeName.get(tbIndex).setValue(lblEmployeeName.get(tbIndex+1).getValue().toString().trim());
							lblDesignationID.get(tbIndex).setValue(lblDesignationID.get(tbIndex+1).getValue().toString().trim());
							lblDesignation.get(tbIndex).setValue(lblDesignation.get(tbIndex+1).getValue().toString().trim());
							lblUnitID.get(tbIndex).setValue(lblUnitID.get(tbIndex+1).getValue().toString().trim());
							lblUnitName.get(tbIndex).setValue(lblUnitName.get(tbIndex+1).getValue().toString().trim());
							lblDepartmentID.get(tbIndex).setValue(lblDepartmentID.get(tbIndex+1).getValue().toString().trim());
							lblDepartmentName.get(tbIndex).setValue(lblDepartmentName.get(tbIndex+1).getValue().toString().trim());
							lblSectionID.get(tbIndex).setValue(lblSectionID.get(tbIndex+1).getValue().toString().trim());
							lblSectionName.get(tbIndex).setValue(lblSectionName.get(tbIndex+1).getValue().toString().trim());
							txtReason.get(tbIndex).setValue(txtReason.get(tbIndex+1).getValue().toString().trim());
							chkPermit.get(tbIndex).setValue(chkPermit.get(tbIndex+1).getValue());

							lblAutoEmployeeID.get(tbIndex+1).setValue("");
							lblEmployeeID.get(tbIndex+1).setValue("");
							lblProximityID.get(tbIndex+1).setValue("");
							lblEmployeeName.get(tbIndex+1).setValue("");
							lblDesignationID.get(tbIndex+1).setValue("");
							lblDesignation.get(tbIndex+1).setValue("");
							lblUnitID.get(tbIndex+1).setValue("");
							lblUnitName.get(tbIndex+1).setValue("");
							lblDepartmentID.get(tbIndex+1).setValue("");
							lblDepartmentName.get(tbIndex+1).setValue("");
							lblSectionID.get(tbIndex+1).setValue("");
							lblSectionName.get(tbIndex+1).setValue("");
							txtReason.get(tbIndex+1).setValue("");
							chkPermit.get(tbIndex+1).setValue(false);
						}
					}
				}
			}
		});

		lblSl.add(ar, new Label());
		lblSl.get(ar).setWidth("100%");
		lblSl.get(ar).setHeight("20px");
		lblSl.get(ar).setValue(ar+1);

		lblAutoEmployeeID.add(ar, new Label());
		lblAutoEmployeeID.get(ar).setWidth("100%");
		lblAutoEmployeeID.get(ar).setHeight("20px");

		lblEmployeeID.add(ar, new Label());
		lblEmployeeID.get(ar).setWidth("100%");

		lblProximityID.add(ar, new Label());
		lblProximityID.get(ar).setWidth("100%");

		lblEmployeeName.add(ar, new Label());
		lblEmployeeName.get(ar).setWidth("100%");

		lblDesignationID.add(ar, new Label());
		lblDesignationID.get(ar).setWidth("100%");

		lblDesignation.add(ar, new Label());
		lblDesignation.get(ar).setWidth("100%");

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

		txtReason.add(ar, new TextField());
		txtReason.get(ar).setWidth("100%");

		chkPermit.add(ar, new CheckBox());
		chkPermit.get(ar).setWidth("100%");

		table.addItem(new Object[]{btnDel.get(ar),lblSl.get(ar),lblAutoEmployeeID.get(ar),lblEmployeeID.get(ar),lblProximityID.get(ar),lblEmployeeName.get(ar),
				lblDesignationID.get(ar),lblDesignation.get(ar),lblUnitID.get(ar),lblUnitName.get(ar),lblDepartmentID.get(ar),lblDepartmentName.get(ar),
				lblSectionID.get(ar),lblSectionName.get(ar),txtReason.get(ar),chkPermit.get(ar)}, ar);
	}

	public AbsoluteLayout buildMainLayout()
	{
		mainlayout=new AbsoluteLayout();
		mainlayout.setWidth("1050.0px");
		mainlayout.setHeight("530.0px");

		cmbUnit=new ComboBox();
		cmbUnit.setWidth("290.0px");
		cmbUnit.setHeight("-1px");
		cmbUnit.setImmediate(true);
		cmbUnit.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		cmbUnit.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainlayout.addComponent(new Label("Project : "), "top:10.0px;left:30.0px;");
		mainlayout.addComponent(cmbUnit, "top:08.0px;left:110.0px");

		cmbDepartment=new ComboBox();
		cmbDepartment.setWidth("290.0px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setImmediate(true);
		cmbDepartment.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		cmbDepartment.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainlayout.addComponent(new Label("Department : "), "top:35.0px;left:30.0px;");
		mainlayout.addComponent(cmbDepartment, "top:33.0px;left:110.0px");

		chkDepartmentAll=new CheckBox("All");
		chkDepartmentAll.setImmediate(true);
		mainlayout.addComponent(chkDepartmentAll, "top:35.0px; left:405.0px;");

		cmbSection=new ComboBox();
		cmbSection.setWidth("290.0px");
		cmbSection.setHeight("-1px");
		cmbSection.setImmediate(true);
		cmbSection.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		cmbSection.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainlayout.addComponent(new Label("Section : "), "top:60.0px;left:30.0px;");
		mainlayout.addComponent(cmbSection, "top:58.0px;left:110.0px;");

		chkSectionAll=new CheckBox("All");
		chkSectionAll.setImmediate(true);
		mainlayout.addComponent(chkSectionAll, "top:60.0px;left:405.0px");

		cmbEmployee=new ComboBox();
		cmbEmployee.setWidth("290.0px");
		cmbEmployee.setHeight("-1px");
		cmbEmployee.setImmediate(true);
		cmbEmployee.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		cmbEmployee.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainlayout.addComponent(new Label("Employee: "), "top:10.0px;left:460.0px;");
		mainlayout.addComponent(cmbEmployee, "top:08.0px;left:535.0px;");

		chkEmployeeAll=new CheckBox("All");
		chkEmployeeAll.setImmediate(true);
		mainlayout.addComponent(chkEmployeeAll, "top:08.0px; left:830.0px;");				

		fromdate=new PopupDateField();
		fromdate.setImmediate(true);
		fromdate.setWidth("110.0px");
		fromdate.setDateFormat("dd-MM-yyyy");
		fromdate.setValue(new Date());
		fromdate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainlayout.addComponent(new Label("From Date : "), "top:35.0px;left:460.0px;");
		mainlayout.addComponent(fromdate, "top:33.0px;left:535.0px;");

		InHrs=new TimeField();
		InHrs.setWidth("25.0px");
		mainlayout.addComponent(new Label("In Time : "), "top:35.0px;left:655.0px;");
		mainlayout.addComponent(InHrs, "top:33.0px;left:720.0px;");
		mainlayout.addComponent(new Label(" : "),"top:33.0px;left:747.0px;");

		InMin=new TimeField();
		InMin.setWidth("25.0px");
		mainlayout.addComponent(InMin, "top:33.0px;left:751.0px;");

		InTF=new TextField();
		InTF.setWidth("28.0px");
		InTF.setInputPrompt("AM");
		InTF.setTextChangeEventMode(TextChangeEventMode.EAGER);
		mainlayout.addComponent(InTF, "top:33.0px;left:780.0px;");

		todate=new PopupDateField();
		todate.setImmediate(true);
		todate.setWidth("110.0px");
		todate.setDateFormat("dd-MM-yyyy");
		todate.setValue(new Date());
		todate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainlayout.addComponent(new Label("To Date : "), "top:60.0px;left:460.0px;");
		mainlayout.addComponent(todate, "top:58.0px;left:535.0px;");
		
		outHrs=new TimeField();
		outHrs.setWidth("25.0px");
		mainlayout.addComponent(new Label("Out Time : "), "top:60.0px;left:655.0px;");
		mainlayout.addComponent(outHrs, "top:58.0px;left:720.0px;");
		mainlayout.addComponent(new Label(" : "),"top:58.0px;left:747.0px;");

		outMin=new TimeField();
		outMin.setWidth("25.0px");
		mainlayout.addComponent(outMin, "top:58.0px;left:751.0px;");

		outTF=new TextField();
		outTF.setWidth("28.0px");
		outTF.setInputPrompt("PM");
		outTF.setTextChangeEventMode(TextChangeEventMode.EAGER);
		mainlayout.addComponent(outTF, "top:58.0px;left:780.0px;");

		txtReasonForAll.setWidth("140px");
		txtReasonForAll.setHeight("-1px");
		mainlayout.addComponent(txtReasonForAll, "top:58.0px;left:810.0px");
		
		chkPermitAll=new CheckBox("Apply All");
		chkPermitAll.setImmediate(true);
		mainlayout.addComponent(chkPermitAll, "top:60.0px;left:972.0px");
		
		table.setWidth("990.0px");
		table.setHeight("360.0px");

		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("Del", NativeButton.class, new NativeButton());
		table.setColumnWidth("Del", 30);

		table.addContainerProperty("SL", Label.class, new Label());
		table.setColumnWidth("SL", 25);

		table.addContainerProperty("EMP ID", Label.class, new Label());
		table.setColumnWidth("EMP ID", 80);

		table.addContainerProperty("Employee ID", Label.class, new Label());
		table.setColumnWidth("Employee ID",100);

		table.addContainerProperty("Proximity ID", Label.class, new Label());
		table.setColumnWidth("Proximity ID", 100);

		table.addContainerProperty("Employee Name", Label.class, new Label());
		table.setColumnWidth("Employee Name", 180);

		table.addContainerProperty("Designation ID", Label.class, new Label());
		table.setColumnWidth("Designation ID", 30);

		table.addContainerProperty("Designation", Label.class, new Label());
		table.setColumnWidth("Designation", 180);

		table.addContainerProperty("Project ID", Label.class, new Label());
		table.setColumnWidth("Project ID", 30);

		table.addContainerProperty("Project Name", Label.class, new Label());
		table.setColumnWidth("Project Name", 180);

		table.addContainerProperty("Department ID", Label.class, new Label());
		table.setColumnWidth("Department ID", 30);

		table.addContainerProperty("Department Name", Label.class, new Label());
		table.setColumnWidth("Department Name", 180);

		table.addContainerProperty("Section ID", Label.class, new Label());
		table.setColumnWidth("Section ID", 30);

		table.addContainerProperty("Section Name", Label.class, new Label());
		table.setColumnWidth("Section Name", 180);

		table.addContainerProperty("Reason", TextField.class, new TextField());
		table.setColumnWidth("Reason", 140);

		table.addContainerProperty("Apply", CheckBox.class, new CheckBox());
		table.setColumnWidth("Apply", 40);

		table.setColumnCollapsed("Designation", true);
		table.setColumnCollapsed("Project ID", true);
		table.setColumnCollapsed("Designation ID", true);
		table.setColumnCollapsed("Unit ID", true);
		table.setColumnCollapsed("Department ID", true);
		table.setColumnCollapsed("Section ID", true);
		table.setColumnCollapsed("Section Name", true);
		table.setColumnCollapsed("EMP ID", true);
		table.setColumnCollapsed("Proximity ID", true);

		table.setColumnAlignments(new String[]{Table.ALIGN_CENTER,Table.ALIGN_RIGHT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,
				Table.ALIGN_LEFT,Table.ALIGN_RIGHT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,
				Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_CENTER,Table.ALIGN_CENTER});
		mainlayout.addComponent(table, "top:90.0px;left:30.0px");
		tableinitialize();

		mainlayout.addComponent(cButton, "top:480.0px;left:380.0px;");
		return mainlayout;
	}
}
