package com.appform.hrmModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.appform.hrmModule.MonthlySalaryGenerate.Worker1;
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
import com.vaadin.ui.Button;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Component.Listener;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class MonthlySalaryGenerateMachine extends Window
{
	private AbsoluteLayout mainLayout;
	private SessionBean sessionBean;

	private Label lblGenerateDate;
	private PopupDateField dGenerateDate;

	private Label lblUnit;
	private ComboBox cmbUnit;
			
	private Label lblSection;
	private ComboBox cmbSection,cmbDepartment;
	private CheckBox chkSectionAll,chkDepartmentAll;

	private Label lblemployeeName;
	private ComboBox cmbEmployeeName;
	private CheckBox chkEmployeeAll;

	private Label lblSalaryMonth;
	private PopupDateField dSalaryMonth;

	private Label lblDayOfMonth;
	private TextRead txtDayOfMonth;

	private Label lblFridayOfMonth;
	private TextRead txtFridayOfMonth;

	private Label lblHolidayOfMonth;
	private TextRead txtHolidayOfMonth;

	private Label lblWorkingDay;
	private TextRead txtWorkingDay;

	private Label lblRevenueStamp;
	private AmountField txtRevenueStamp;

	private ProgressIndicator PI;
	private Worker1 worker1;

	private int a;
	
	private ArrayList<Component> allComp = new ArrayList<Component>();	

	private CommonButton cButton = new CommonButton("", "Save", "", "", "", "", "", "", "", "");

	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");

	private SimpleDateFormat dMonth = new SimpleDateFormat("MMMMM");

	String username = "";
	int clickCount=0;
	private CommonMethod cm;
	private String menuId = "";
	private int start = 0;

	public MonthlySalaryGenerateMachine(SessionBean sessionBean,String menuId) 
	{
		this.sessionBean= sessionBean;
		this.setCaption("GENERATE MONTHLY SALARY (MACHINE) :: " +sessionBean.getCompany());
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		buildMainLayout();
		setContent(mainLayout);

		componentIni(true);

		setEventAction();
		cmbUnitData();

		authenticationCheck();
		focusEnter();
		dGenerateDate.focus();
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

	private void setEventAction()
	{
		cButton.btnSave.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(start == 1)
				{
					formValidation();
				}
				else
				{
					showNotification("Warning!","Before generate salary please confirm following:" +
							"\n1.Check Short View Of Attendance. \n2.Check Short View Of Overtime. \n3.Check Leave Application.",
							Notification.TYPE_WARNING_MESSAGE);
				}
				start = 1;
			}
		});

		cmbUnit.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbUnit.getValue()!=null)
				{
					cmbDepartmentData();
				}
			}
		});
		cmbDepartment.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbDepartment.getValue()!=null)
				{
					cmbSectionData();
				}
			}
		});

		chkDepartmentAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(cmbUnit.getValue()!=null)
				{
					if(chkDepartmentAll.booleanValue())
					{
						cmbDepartment.setEnabled(false);
						cmbDepartment.setValue(null);
						cmbSectionData();
					}
					else
					{
						cmbDepartment.setEnabled(true);
					}
				}
				else
				{
					chkDepartmentAll.setValue(false);
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
		chkSectionAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
				{
					if(chkSectionAll.booleanValue())
					{
						cmbSection.setEnabled(false);
						cmbSection.setValue(null);
						cmbEmployeeNameDataAdd();
					}
					else
					{
						cmbSection.setEnabled(true);
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
				if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
				{
					if(chkEmployeeAll.booleanValue())
					{
						cmbEmployeeName.setEnabled(false);
						cmbEmployeeName.setValue(null);
					}
					else
					{
						cmbEmployeeName.setEnabled(true);
					}
				}
				else
				{
					chkEmployeeAll.setValue(false);
				}
			}
		});

		dSalaryMonth.addListener(new Listener()
		{
			public void componentEvent(Event event)
			{
				if(dSalaryMonth.getValue()!=null)
				{
					findTotalDays();
				}
			}
		});
	}
	private void cmbUnitData() 
	{
		
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			
		/*	'"+dFormat.format(dGenerateDate.getValue())+"'*/
			
			String query="select distinct vUnitId,vUnitName from tbEmpOfficialPersonalInfo where bStatus=1 order by vUnitName";
			
			System.out.println("Unit"+query);
			
			List <?> list=session.createSQLQuery(query).list();	

			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbUnit.addItem(element[0]);
				cmbUnit.setItemCaption(element[0], (String) element[1]);
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbUnitData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void cmbEmployeeNameDataAdd()
	{
		cmbEmployeeName.removeAllItems();
		
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select vEmployeeId,vEmployeeName,vEmployeeCode from tbEmpOfficialPersonalInfo where bStatus=1 "
					+ " and vUnitId like '"+cmbUnit.getValue().toString()+"'   "
					+ " and vDepartmentId like '"+(cmbDepartment.getValue()==null?"%":cmbDepartment.getValue())+"'   "
					+ " and vSectionId like '"+(cmbSection.getValue()==null?"%":cmbSection.getValue())+"'   "
					+ " order by vEmployeeName";
	
			System.out.println("Employee"+query);
			
			List <?> lst=session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				for(Iterator <?> itr=lst.iterator();itr.hasNext();)
				{
					Object [] element=(Object[]) itr.next();
					cmbEmployeeName.addItem(element[0]);
					cmbEmployeeName.setItemCaption(element[0], (String)element[2]+">>"+element[1].toString());
				}
			}
			else
			{
				showNotification("Warning", "No Employee Found!!!", Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbEmployeeNameDataAdd",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void formValidation()
	{
		
		if(cmbUnit.getValue()!=null)
		{
			if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
			{
				if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
				{
					if(cmbEmployeeName.getValue()!=null || chkEmployeeAll.booleanValue())
					{
						if(dSalaryMonth.getValue()!=null)
						{
							if(!txtDayOfMonth.getValue().toString().equals(""))
							{
								if(!txtRevenueStamp.getValue().toString().equals(""))
								{
									generateAction();
								}
								else
								{
									showNotification("Warning!","Provide Revenue Stamp Amount",Notification.TYPE_WARNING_MESSAGE);
									txtRevenueStamp.focus();
								}
							}
							else
							{
								showNotification("Warning!","Please Select Month Properly",Notification.TYPE_WARNING_MESSAGE);
								dSalaryMonth.focus();
							}
						}
						else
						{
							showNotification("Warning!","Please Select Salary Month",Notification.TYPE_WARNING_MESSAGE);
							dSalaryMonth.focus();
						}
					}
					else
					{
						showNotification("Warning!","Please Select Employee Name",Notification.TYPE_WARNING_MESSAGE);
						cmbEmployeeName.focus();
					}
				}
				else
				{
					showNotification("Warning!","Please Select Section",Notification.TYPE_WARNING_MESSAGE);
					cmbSection.focus();
				}
			}
			else
			{
				showNotification("Warning!","Please Select Department",Notification.TYPE_WARNING_MESSAGE);
				cmbDepartment.focus();
			}
		}
		else
		{
			showNotification("Warning!","Please Select Project ",Notification.TYPE_WARNING_MESSAGE);
			cmbUnit.focus();
		}
	}

	private boolean chkSalary(String query)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> empList = session.createSQLQuery(query).list();
			if(!empList.isEmpty())
				return true;
		}
		catch (Exception exp)
		{
			showNotification("", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
		return false;
	}

	private void generateAction()
	{
		String unit="%",department="%",section="%",employee="%";

		unit = cmbUnit.getValue().toString();
		
		if(!chkDepartmentAll.booleanValue())
		{
			department = cmbDepartment.getValue().toString();
		}
		if(!chkSectionAll.booleanValue())
		{
			section = cmbSection.getValue().toString();
		}
		if(!chkEmployeeAll.booleanValue())
		{
			employee=cmbEmployeeName.getValue().toString();
		}

		String empQuery = "select vEmployeeId,vEmployeeName from tbEmpOfficialPersonalInfo ein where vUnitId like'"+unit+"' and vSectionId like'"+section+"' and vDepartmentId like '"+department+"' "
						+ " and bStatus=1 order by ein.vEmployeeName";

		System.out.println("empo "+empQuery);
		
		if(chkSalary(empQuery))
		{
			String query = "select vEmployeeId from tbMonthlySalary where MONTH(dSalaryDate) = MONTH('"+dFormat.format(dSalaryMonth.getValue())+"') " +
					"and YEAR(dSalaryDate) = YEAR('"+dFormat.format(dSalaryMonth.getValue())+"') and vUnitId like'"+unit+"' " +
					"and vDepartmentId like '"+department+"' and vSectionId like '"+section+"' and vEmployeeID like '"+employee+"'";

			System.out.println("Chksalary "+query);
			
			if(!chkSalary(query))
			{
				MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to generate salary?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
				mb.show(new EventListener()
				{
					public void buttonClicked(ButtonType buttonType)
					{
						if(buttonType == ButtonType.YES)
						{
							if(clickCount==0)
							{
								salaryGenerate();
								
								worker1 = new Worker1();
								worker1.start();
								PI.setEnabled(true);
								PI.setValue(0f);
								cButton.btnSave.setEnabled(false);
								componentIni(false);
							}
							else
							{
								showNotification("Warning", "Please Wait!!!", Notification.TYPE_WARNING_MESSAGE);
							}
						}
					}
				});
			}
			else
			{
				showNotification("Warning!","Salary already generate for month "+dMonth.format(dSalaryMonth.getValue())+" ", Notification.TYPE_WARNING_MESSAGE);
				txtClear();
				componentIni(true);
			}
		}
		else
		{
			showNotification("There are no Employee in this Department",Notification.TYPE_WARNING_MESSAGE);
			txtClear();
			componentIni(true);
		}
	}


	private void cmbDepartmentData() 
	{
		cmbDepartment.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
/*			String query="select distinct vSectionId,vSectionName from tbEmpSectionInfo  where order by vSectionName";
*/		
			String query="select distinct vDepartmentId,vDepartmentName from tbEmpOfficialPersonalInfo  where bStatus=1 and  vUnitId='"+cmbUnit.getValue().toString()+"'  order by vDepartmentName";
			
			System.out.println("Section"+query);
			
			List <?> list=session.createSQLQuery(query).list();	

			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbDepartment.addItem(element[0]);
				cmbDepartment.setItemCaption(element[0], (String) element[1]);
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbSectionData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void cmbSectionData() 
	{
		cmbSection.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
/*			String query="select distinct vSectionId,vSectionName from tbEmpSectionInfo  where order by vSectionName";
*/		
			String query="select distinct vSectionId,vSectionName from tbEmpOfficialPersonalInfo  where bStatus=1 "
					+ " and vUnitId='"+cmbUnit.getValue().toString()+"'   "
					+ " and vDepartmentId like '"+(cmbDepartment.getValue()==null?"%":cmbDepartment.getValue())+"'   "
					+ " order by vSectionName";
			
			System.out.println("Section"+query);
			
			List <?> list=session.createSQLQuery(query).list();	

			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbSection.addItem(element[0]);
				cmbSection.setItemCaption(element[0], (String) element[1]);
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbSectionData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void findTotalDays()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		String applyDateFrom="";
		String applyDateTo="";
		int totalHoliday = 0; 

		try
		{
			applyDateFrom = dFormat.format(dSalaryMonth.getValue())+"";
			applyDateTo = dFormat.format(dSalaryMonth.getValue())+"";

			System.out.println("applyDateFrom: "+applyDateFrom+"\napplyDateTo: "+applyDateTo);

			String holiday = " select 0 as zero,COUNT(dDate) as holiDay from tbHoliday where MONTH(dDate)= MONTH('"+applyDateFrom+"') AND YEAR(dDate)= YEAR('"+applyDateFrom+"') and DATENAME(W,dDate)!='Friday'";
			List <?> holidayList = session.createSQLQuery(holiday).list();
			if(holidayList.iterator().hasNext())
			{
				Object[] element = (Object[]) holidayList.iterator().next();

				totalHoliday = Integer.parseInt(element[1].toString());
			}


			String query = "select datediff(day, '"+applyDateFrom+"', dateadd(month, 1, '"+applyDateFrom+"'))" +
					" as totalDays,CONVERT(date,DATEADD(dd,-day('"+applyDateFrom+"')+1,'"+applyDateFrom+"'),105)" +
					" as firstDay,CONVERT(date,DATEADD(dd,-day(dateadd(mm,1,'"+applyDateFrom+"'))," +
					" dateadd(mm,1,'"+applyDateFrom+"')),105) as lastDay";

			List <?> list = session.createSQLQuery(query).list();

			if(list.iterator().hasNext())
			{
				Object[] element = (Object[]) list.iterator().next();

				int totalDays = Integer.parseInt(element[0].toString());
				int totalFriday = fridayCount(element[1].toString(), element[2].toString());
				int totalWorkingday = totalDays-(totalHoliday+totalFriday);

				txtDayOfMonth.setValue(totalDays);
				txtFridayOfMonth.setValue(totalFriday);
				txtHolidayOfMonth.setValue(totalHoliday);
				txtWorkingDay.setValue(totalWorkingday);
			}
		}
		catch (Exception ex)
		{
			showNotification("findTotalDays", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
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
			showNotification("fridayCount", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}

		return numFriday;  
	}

	private void salaryGenerate()
	{
		clickCount=1;

		String unit =cmbUnit.getValue().toString();
		String department = "%";
		String section = "%";
		String employee="%";
		if(!chkDepartmentAll.booleanValue())
		{
			department = cmbDepartment.getValue().toString();
		}
		if(!chkSectionAll.booleanValue())
		{
			section = cmbSection.getValue().toString();
		}
		if(!chkEmployeeAll.booleanValue())
		{
			employee=cmbEmployeeName.getValue().toString();
		}

		
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{
			String insertQuery = "exec prcCalcMonthlySalaryMachine '"+dFormat.format(dGenerateDate.getValue())+"','"+dFormat.format(dSalaryMonth.getValue())+"'," +
					"'"+unit+"','"+department+"','"+section+"','"+employee+"',"+txtRevenueStamp.getValue()+",'"+sessionBean.getUserId()+"'," +
					"'"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"' ";
			System.out.println("salaryGenerate: "+insertQuery);
			
			session.createSQLQuery(insertQuery).executeUpdate();
			
			tx.commit();
		}
		catch(Exception ex)
		{
			tx.rollback();
			showNotification("salaryGenerate", ex.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}

	private void focusEnter()
	{
		allComp.add(dGenerateDate);		
		allComp.add(cmbUnit);
		allComp.add(cmbDepartment);
		allComp.add(cmbSection);
		allComp.add(cmbEmployeeName);
		allComp.add(dSalaryMonth);
		allComp.add(txtRevenueStamp);
		allComp.add(cButton.btnSave);

		new FocusMoveByEnter(this,allComp);
	}

	private void txtClear()
	{
		dGenerateDate.setValue(new java.util.Date());
		cmbUnit.setValue(null);
		cmbSection.setValue(null);
		cmbDepartment.setValue(null);
		cmbEmployeeName.setValue(null);
		dSalaryMonth.setValue(null);
		txtDayOfMonth.setValue("");
		txtFridayOfMonth.setValue("");
		txtHolidayOfMonth.setValue("");
		txtWorkingDay.setValue("");
		chkEmployeeAll.setValue(false);
		chkSectionAll.setValue(false);
		txtRevenueStamp.setValue("");
		start = 1;
		clickCount=0;
	}

	private void componentIni(boolean b) 
	{
		dGenerateDate.setEnabled(b);	
		cmbUnit.setEnabled(b);
		cmbSection.setEnabled(b);
		cmbDepartment.setEnabled(b);
		cmbEmployeeName.setEnabled(b);
		dSalaryMonth.setEnabled(b);
		txtDayOfMonth.setEnabled(b);
		txtFridayOfMonth.setEnabled(b);
		txtHolidayOfMonth.setEnabled(b);
		txtWorkingDay.setEnabled(b);
		chkEmployeeAll.setEnabled(b);
		chkSectionAll.setEnabled(b);
		txtRevenueStamp.setEnabled(b);
	}

	private AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("530px");
		setHeight("400px");

		// lblGenerateDate
		lblGenerateDate = new Label("Generate Date :");
		lblGenerateDate.setImmediate(false);
		lblGenerateDate.setWidth("-1px");
		lblGenerateDate.setHeight("-1px");
		mainLayout.addComponent(lblGenerateDate, "top:20.0px;left:30.0px;");

		// dGenerateDate
		dGenerateDate = new PopupDateField();
		dGenerateDate.setImmediate(true);
		dGenerateDate.setWidth("-1px");
		dGenerateDate.setHeight("-1px");
		dGenerateDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dGenerateDate.setDateFormat("dd-MM-yyyy");
		dGenerateDate.setValue(new java.util.Date());
		mainLayout.addComponent(dGenerateDate, "top:18.0px;left:150.0px;");

		// lblUnit
		lblUnit = new Label("Project :");
		lblUnit.setImmediate(false);
		lblUnit.setWidth("-1px");
		lblUnit.setHeight("-1px");
		mainLayout.addComponent(lblUnit, "top:45.0px;left:30.0px;");

		// cmbSection
		cmbUnit= new ComboBox();
		cmbUnit.setInvalidAllowed(false);
		cmbUnit.setWidth("320px");
		cmbUnit.setHeight("-1px");
		cmbUnit.setImmediate(true);
		mainLayout.addComponent(cmbUnit, "top:43.0px;left:150.0px;");
		
		mainLayout.addComponent(new Label("Department : "), "top:70.0px;left:30.0px;");

		// cmbDepartment
		cmbDepartment= new ComboBox();
		cmbDepartment.setInvalidAllowed(false);
		cmbDepartment.setWidth("320px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setImmediate(true);
		cmbDepartment.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbDepartment, "top:68.0px;left:150.0px;");

		// chkDepartmentAll
		chkDepartmentAll = new CheckBox("All");
		chkDepartmentAll.setImmediate(true);
		chkDepartmentAll.setWidth("-1px");
		chkDepartmentAll.setHeight("-1px");
		mainLayout.addComponent(chkDepartmentAll, "top:70.0px;left:473.0px;");

		// lblSection
		lblSection = new Label("Section :");
		lblSection.setImmediate(false);
		lblSection.setWidth("-1px");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection, "top:95px;left:30.0px;");

		// cmbSection
		cmbSection= new ComboBox();
		cmbSection.setInvalidAllowed(false);
		cmbSection.setWidth("320px");
		cmbSection.setHeight("-1px");
		cmbSection.setImmediate(true);
		mainLayout.addComponent(cmbSection, "top:93px;left:150.0px;");

		// chkSectionAll
		chkSectionAll = new CheckBox("All");
		chkSectionAll.setImmediate(true);
		chkSectionAll.setWidth("-1px");
		chkSectionAll.setHeight("-1px");
		mainLayout.addComponent(chkSectionAll, "top:95px;left:473.0px;");

		lblemployeeName = new Label("Employee ID :");
		mainLayout.addComponent(lblemployeeName, "top:120px; left:30.0px;");

		cmbEmployeeName= new ComboBox();
		cmbEmployeeName.setInvalidAllowed(false);
		cmbEmployeeName.setWidth("320px");
		cmbEmployeeName.setHeight("-1px");
		cmbEmployeeName.setImmediate(true);
		cmbEmployeeName.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbEmployeeName, "top:118px;left:150.0px;");

		// chkSectionAll
		chkEmployeeAll = new CheckBox("All");
		chkEmployeeAll.setImmediate(true);
		chkEmployeeAll.setWidth("-1px");
		chkEmployeeAll.setHeight("-1px");
		mainLayout.addComponent(chkEmployeeAll, "top:120px;left:473.0px;");

		// lblSalaryMonth
		lblSalaryMonth = new Label("Salary of Month :");
		lblSalaryMonth.setImmediate(false);
		lblSalaryMonth.setWidth("-1px");
		lblSalaryMonth.setHeight("-1px");
		mainLayout.addComponent(lblSalaryMonth, "top:145px;left:30.0px;");

		// dSalaryMonth
		dSalaryMonth = new PopupDateField();
		dSalaryMonth.setImmediate(true);
		dSalaryMonth.setWidth("150px");
		dSalaryMonth.setHeight("-1px");
		dSalaryMonth.setResolution(PopupDateField.RESOLUTION_MONTH);
		dSalaryMonth.setDateFormat("MMMMM-yyyy");
		//		dSalaryMonth.setValue(new java.util.Date());
		mainLayout.addComponent(dSalaryMonth, "top:143px;left:150.0px;");

		// lblDayOfMonth
		lblDayOfMonth = new Label("Total Days :");
		lblDayOfMonth.setImmediate(false);
		lblDayOfMonth.setWidth("-1px");
		lblDayOfMonth.setHeight("-1px");
		mainLayout.addComponent(lblDayOfMonth, "top:170px;left:30.0px;");

		// txtDayOfMonth
		txtDayOfMonth= new TextRead(1);
		txtDayOfMonth.setWidth("50px");
		txtDayOfMonth.setImmediate(true);
		txtDayOfMonth.setHeight("24px");
		mainLayout.addComponent(txtDayOfMonth, "top:168px;left:150.0px;");

		// lblFridayOfMonth
		lblFridayOfMonth = new Label("No of Fridays :");
		lblFridayOfMonth.setImmediate(false);
		lblFridayOfMonth.setWidth("-1px");
		lblFridayOfMonth.setHeight("-1px");
		mainLayout.addComponent(lblFridayOfMonth, "top:195px;left:30.0px;");

		// txtFridayOfMonth
		txtFridayOfMonth = new TextRead(1);
		txtFridayOfMonth.setWidth("50px");
		txtFridayOfMonth.setImmediate(true);
		txtFridayOfMonth.setHeight("24px");
		mainLayout.addComponent(txtFridayOfMonth, "top:193px;left:150.0px;");

		// lblHolidayOfMonth
		lblHolidayOfMonth = new Label("No of Holidays :");
		lblHolidayOfMonth.setImmediate(false);
		lblHolidayOfMonth.setWidth("-1px");
		lblHolidayOfMonth.setHeight("-1px");
		mainLayout.addComponent(lblHolidayOfMonth, "top:220px;left:30.0px;");

		// txtHolidayOfMonth
		txtHolidayOfMonth = new TextRead(1);
		txtHolidayOfMonth.setWidth("50px");
		txtHolidayOfMonth.setImmediate(true);
		txtHolidayOfMonth.setHeight("24px");
		mainLayout.addComponent(txtHolidayOfMonth, "top:218px;left:150.0px;");

		// lblWorkingDay
		lblWorkingDay = new Label("No of Workingdays :");
		lblWorkingDay.setImmediate(false);
		lblWorkingDay.setWidth("-1px");
		lblWorkingDay.setHeight("-1px");
		mainLayout.addComponent(lblWorkingDay, "top:245px;left:30.0px;");

		// txtWorkingDay
		txtWorkingDay = new TextRead(1);
		txtWorkingDay.setWidth("50px");
		txtWorkingDay.setImmediate(true);
		txtWorkingDay.setHeight("24px");
		mainLayout.addComponent(txtWorkingDay, "top:243px;left:150.0px;");

		// lblRevenueStamp
		lblRevenueStamp = new Label("Revenue Stamp :");
		lblRevenueStamp.setImmediate(false);
		lblRevenueStamp.setWidth("-1px");
		lblRevenueStamp.setHeight("-1px");
		mainLayout.addComponent(lblRevenueStamp, "top:270px;left:30.0px;");

		// txtRevenueStamp
		txtRevenueStamp = new AmountField();
		txtRevenueStamp.setWidth("50px");
		txtRevenueStamp.setImmediate(true);
		txtRevenueStamp.setHeight("-1px");
		mainLayout.addComponent(txtRevenueStamp, "top:268px;left:150.0px;");

		// CommonButton
		cButton.btnSave.setCaption("Generate");
		cButton.btnSave.setWidth("100.0px");
		mainLayout.addComponent(cButton,"bottom:15px;left:110.0px;");

		// PI
		PI=new ProgressIndicator();
		PI.setWidth("130px");
		PI.setImmediate(true);
		PI.setEnabled(false);
		mainLayout.addComponent(PI,"bottom:15px;left:330.0px;");

		return mainLayout;
	}

	public class Worker1 extends Thread 
	{
		int current = 1;
		public final static int MAX = 10;
		public void run() 
		{
			for (; current <= MAX; current++) 
			{
				try
				{
					Thread.sleep(1000);
				}
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
				synchronized (getApplication()) 
				{
					prosessed();
				}
			}
			txtClear();
			componentIni(true);
			showNotification("Message!","Salary Generated Successfully",Notification.TYPE_TRAY_NOTIFICATION);
		}
		public int getCurrent() 
		{
			return current;
		}
	}
	public void prosessed() 
	{
		int i = worker1.getCurrent();
		if (i == Worker1.MAX)
		{
			PI.setEnabled(false);
			cButton.btnSave.setEnabled(true);
			PI.setValue(1f);
		}
		else
		{
			PI.setValue((float) i / Worker1.MAX);
		}
	}
}
