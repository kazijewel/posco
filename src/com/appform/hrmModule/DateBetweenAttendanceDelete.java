package com.appform.hrmModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.CommonMethod;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class DateBetweenAttendanceDelete extends Window
{
	private AbsoluteLayout mainLayout;
	private SessionBean sessionBean;

	private Label lblUnit;
	private ComboBox cmbUnit;
	private CheckBox chkUnitAll;
	private PopupDateField dFromDate,dToDate;

	private Label lblSection;
	private ComboBox cmbSection,cmbDepartment;
	private CheckBox chkSectionAll,chkDepartmentAll;

	private Label lblemployeeName;
	private ComboBox cmbEmployeeName;
	private CheckBox chkEmployeeAll;
	private ProgressIndicator PI;
	private Worker1 worker1;
	private ArrayList<Component> allComp = new ArrayList<Component>();	
	private CommonButton cButton = new CommonButton("", "", "", "Delete", "", "", "", "", "", "");
	private SimpleDateFormat dMonth = new SimpleDateFormat("MMMMM");


	String department = "";
	String section = "";
	String Unit = "";
	String employee = "";
	
	String username = "";
	private CommonMethod cm;
	private String menuId = "";
	public DateBetweenAttendanceDelete(SessionBean sessionBean,String menuId) 
	{
		this.sessionBean= sessionBean;
		this.setCaption("DATE BETWEEN ATTENDANCE DELETE :: " +sessionBean.getCompany());
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
		dFromDate.focus();
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
		dFromDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbUnit.removeAllItems();
				if(dFromDate.getValue()!=null)
				{
					cmbDepartment.removeAllItems();
					cmbDepartment.setEnabled(true);
					chkDepartmentAll.setValue(false);
					cmbSection.setEnabled(true);
					cmbSection.removeAllItems();
					chkSectionAll.setValue(false);
					cmbEmployeeName.setEnabled(true);
					cmbEmployeeName.removeAllItems();
					chkEmployeeAll.setValue(false);
					cmbUnitData();
				}
			}
		});
		dToDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbUnit.removeAllItems();
				if(dToDate.getValue()!=null)
				{
					cmbDepartment.setEnabled(true);
					cmbDepartment.removeAllItems();
					chkDepartmentAll.setValue(false);
					cmbSection.setEnabled(true);
					cmbSection.removeAllItems();
					chkSectionAll.setValue(false);
					cmbEmployeeName.setEnabled(true);
					cmbEmployeeName.removeAllItems();
					chkEmployeeAll.setValue(false);
					cmbUnitData();
				}
			}
		});
		cmbUnit.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbUnit.getValue()!=null)
				{
					cmbDepartment.setEnabled(true);
					chkDepartmentAll.setValue(false);
					cmbSection.setEnabled(true);
					cmbSection.removeAllItems();
					chkSectionAll.setValue(false);
					cmbEmployeeName.setEnabled(true);
					cmbEmployeeName.removeAllItems();
					chkEmployeeAll.setValue(false);
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
					cmbSection.setEnabled(true);
					chkSectionAll.setValue(false);
					cmbEmployeeName.setEnabled(true);
					cmbEmployeeName.removeAllItems();
					chkEmployeeAll.setValue(false);
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
						chkSectionAll.setValue(false);
						cmbSection.setEnabled(true);
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
					chkEmployeeAll.setValue(false);
					cmbEmployeeName.setEnabled(true);
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
						chkEmployeeAll.setValue(false);
						cmbEmployeeName.setEnabled(true);
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
		
		cButton.btnDelete.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				formValidation();
			}
		});
		
	}

	private void formValidation()
	{
		if(dFromDate.getValue()!=null)
		{
			if(dToDate.getValue()!=null)
			{
				if(cmbUnit.getValue()!=null || chkUnitAll.booleanValue())
				{
					if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
					{
						if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
						{
							if(cmbEmployeeName.getValue()!=null || chkEmployeeAll.booleanValue())
							{
								deleteAttendance();
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
					showNotification("Warning!","Please Select Unit",Notification.TYPE_WARNING_MESSAGE);
					cmbUnit.focus();
				}
			}
			else
			{
				showNotification("Warning!","Please Select To Date",Notification.TYPE_WARNING_MESSAGE);
				dToDate.focus();
			}
		}
		else
		{
			showNotification("Warning!","Please Select From Date",Notification.TYPE_WARNING_MESSAGE);
			dFromDate.focus();
		}
	}

	private boolean chkSalary(String query)
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> empList = session.createSQLQuery(query).list();
			if(!empList.isEmpty())
				return true;
		}
		catch (Exception exp)
		{

		}
		finally{session.close();}
		return false;
	}

	private void deleteAttendance()
	{
		department = "%";
		section = "%";
		Unit = "%";
		employee = "%";
		
		String query=null;

		Unit = cmbUnit.getValue().toString();
		
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
		
		query = "select vEmployeeId from tbMonthlySalary "
				+ "where (vSalaryMonth= '"+dMonth.format(dFromDate.getValue())+"' "
				+ "AND vSalaryYear= '"+sessionBean.dfYear.format(dFromDate.getValue())+"' )"
				+ "or (vSalaryMonth= '"+dMonth.format(dToDate.getValue())+"' "
				+ "AND vSalaryYear= '"+sessionBean.dfYear.format(dToDate.getValue())+"' )"
				+ "AND vUnitID like '"+Unit+"' and vDepartmentID like '"+department+"' and vSectionID like '"+section+"' "
				+ "and vEmployeeID like '"+employee+"'";
		System.out.println("chkSalary: "+query);
		
		if(!chkSalary(query))
		{
			MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to delete Attendance?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						Session session=SessionFactoryUtil.getInstance().openSession();
						Transaction tx=session.beginTransaction();
						try
						{
							String queryUpdateDelete = "insert into tbUDEmployeeAttendance (dDate,vEmployeeID,vEmployeeCode,vFingerID,"
									+ "vEmployeeName,vDesignationID,vDesignation,vUnitID,vUnitName,vDepartmentID,vDepartmentName,dAttDate,"
									+ "dAttInTime,dAttOutTime,permittedBy,vReason,vShiftID,udFlag,vPAFlag,vUserID,vUserIP,dEntryTime) "
									+ "select dDate,vEmployeeID,vEmployeeCode,vFingerID,vEmployeeName,vDesignationID,vDesignationName,vUnitID,"
									+ "vUnitName,vDepartmentId,vDepartmentName,dDate,dInTimeSecond,dOutTimeSecond,'DateBetweenAttendanceDelete',"
									+ "'DateBetweenAttendanceDelete',vShiftID,'DELETE',vAttendFlag,vUserID,vUserIp,dEntryTime "
									+ "from tbEmployeeAttendanceFinal "
									+ "where vUnitId like '"+cmbUnit.getValue().toString()+"' "
									+ "and vDepartmentId like '"+(cmbDepartment.getValue()==null?"%":cmbDepartment.getValue())+"' "
									+ "and vSectionId like '"+(cmbSection.getValue()==null?"%":cmbSection.getValue())+"' "
									+ "and dDate between '"+sessionBean.dfDb.format(dFromDate.getValue())+"' "
									+ "and '"+sessionBean.dfDb.format(dToDate.getValue())+"' ";
							
							System.out.println("queryDelete: "+queryUpdateDelete);								
							session.createSQLQuery(queryUpdateDelete).executeUpdate();
							/*Delete Data Insert*/
							
							String queryDelete="delete from tbEmployeeAttendanceFinal "
									+ "where vUnitId like '"+cmbUnit.getValue().toString()+"' "
									+ "and vDepartmentId like '"+(cmbDepartment.getValue()==null?"%":cmbDepartment.getValue())+"' "
									+ "and vSectionId like '"+(cmbSection.getValue()==null?"%":cmbSection.getValue())+"' "
									+ "and dDate between '"+sessionBean.dfDb.format(dFromDate.getValue())+"' "
									+ "and '"+sessionBean.dfDb.format(dToDate.getValue())+"' ";
							
							System.out.println("queryDelete: "+queryDelete);
							session.createSQLQuery(queryDelete).executeUpdate(); 
							
							tx.commit();
						}
						catch (Exception exp)
						{
							tx.rollback();
							showNotification("deleteAttendance",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
						}
						finally{session.close();}

						worker1 = new Worker1();
						worker1.start();
						PI.setEnabled(true);
						PI.setValue(0f);
						cButton.btnDelete.setEnabled(false);
						componentIni(false);
					}
				}
			});
		}
		else
		{
			showNotification("Warning!","Salary Already Generated!!!", Notification.TYPE_WARNING_MESSAGE);
			/*txtClear();
			componentIni(true);*/
		}
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
			showNotification("Addtendance Deleted Successfully");
			txtClear();
			componentIni(true);
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
			cButton.btnDelete.setEnabled(true);
			PI.setValue(1f);
		}
		else
		{
			PI.setValue((float) i / Worker1.MAX);
		}
	}
	
	private void cmbUnitData()
	{
		cmbUnit.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vUnitId,vUnitName from tbEmployeeAttendanceFinal "
					+ "where dDate between '"+sessionBean.dfDb.format(dFromDate.getValue())+"' "
					+ "and '"+sessionBean.dfDb.format(dToDate.getValue())+"' "
					+ "order by vUnitName";
			
			System.out.println("cmbUnitData: "+query);
			
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
	private void cmbDepartmentData()
	{
		cmbDepartment.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vDepartmentId,vDepartmentName from tbEmployeeAttendanceFinal "
					+ "where vUnitId='"+cmbUnit.getValue().toString()+"' "
					+ "and dDate between '"+sessionBean.dfDb.format(dFromDate.getValue())+"' "
					+ "and '"+sessionBean.dfDb.format(dToDate.getValue())+"' "
					+ "order by vDepartmentName";
			
			System.out.println("cmbDepartmentData: "+query);
			
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
			showNotification("cmbDepartmentData",exp+"",Notification.TYPE_ERROR_MESSAGE);
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
			String query="select distinct vSectionId,vSectionName from tbEmployeeAttendanceFinal "
					+ "where vUnitId='"+cmbUnit.getValue().toString()+"' "
					+ "and vDepartmentId like '"+(cmbDepartment.getValue()==null?"%":cmbDepartment.getValue())+"' "
					+ "and dDate between '"+sessionBean.dfDb.format(dFromDate.getValue())+"' "
					+ "and '"+sessionBean.dfDb.format(dToDate.getValue())+"' "
					+ "order by vSectionName";
			
			System.out.println("cmbSectionData: "+query);
			
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
	private void cmbEmployeeNameDataAdd()
	{
		cmbEmployeeName.removeAllItems();
		
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vEmployeeId,vEmployeeName,vEmployeeCode from tbEmployeeAttendanceFinal "
					+ "where vUnitId like '"+cmbUnit.getValue().toString()+"' "
					+ "and vDepartmentId like '"+(cmbDepartment.getValue()==null?"%":cmbDepartment.getValue())+"' "
					+ "and vSectionId like '"+(cmbSection.getValue()==null?"%":cmbSection.getValue())+"' "
					+ "and dDate between '"+sessionBean.dfDb.format(dFromDate.getValue())+"' "
					+ "and '"+sessionBean.dfDb.format(dToDate.getValue())+"' "
					+ "order by vEmployeeName";
	
			System.out.println("cmbEmployeeNameDataAdd: "+query);
			
			List <?> lst=session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				for(Iterator <?> itr=lst.iterator();itr.hasNext();)
				{
					Object [] element=(Object[]) itr.next();
					cmbEmployeeName.addItem(element[0]);
					cmbEmployeeName.setItemCaption(element[0], element[2]+"-"+element[1]);
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

	private void focusEnter()
	{
		allComp.add(dFromDate);
		allComp.add(dToDate);
		allComp.add(cmbDepartment);
		allComp.add(cmbSection);
		allComp.add(cmbEmployeeName);
		allComp.add(cButton.btnDelete);
		new FocusMoveByEnter(this,allComp);
	}

	private void txtClear()
	{
		dFromDate.setValue(new java.util.Date());
		dToDate.setValue(new java.util.Date());
		cmbUnit.setValue(null);
		cmbDepartment.setValue(null);
		cmbSection.setValue(null);
		cmbEmployeeName.setValue(null);
		chkEmployeeAll.setValue(false);
		chkDepartmentAll.setValue(false);
		chkSectionAll.setValue(false);
		chkUnitAll.setValue(false);
	}

	private void componentIni(boolean b) 
	{
		dFromDate.setEnabled(b);
		dToDate.setEnabled(b);
		cmbUnit.setEnabled(b);
		cmbDepartment.setEnabled(b);
		cmbSection.setEnabled(b);
		cmbEmployeeName.setEnabled(b);
		chkEmployeeAll.setEnabled(b);
		chkDepartmentAll.setEnabled(b);
		chkSectionAll.setEnabled(b);
		chkUnitAll.setEnabled(b);
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
		setHeight("280px");

		mainLayout.addComponent(new Label("Date :"),"top:40.0px; left:30px;");
		
		dFromDate = new PopupDateField();
		dFromDate.setImmediate(true);
		dFromDate.setHeight("-1px");
		dFromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dFromDate.setDateFormat("dd-MM-yyyy");
		dFromDate.setValue(new java.util.Date());
		mainLayout.addComponent(dFromDate, "top:38.0px; left:140px;");

		dToDate = new PopupDateField();
		dToDate.setImmediate(true);
		dToDate.setHeight("-1px");
		dToDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dToDate.setDateFormat("dd-MM-yyyy");
		dToDate.setValue(new java.util.Date());
		mainLayout.addComponent(dToDate, "top:38.0px;left:260px;");
		
		lblUnit = new Label("Project :");
		lblUnit.setImmediate(false);
		lblUnit.setWidth("-1px");
		lblUnit.setHeight("-1px");
		mainLayout.addComponent(lblUnit, "top:70.0px;left:30.0px;");
		
		cmbUnit= new ComboBox();
		cmbUnit.setWidth("320px");
		cmbUnit.setHeight("-1px");
		cmbUnit.setImmediate(true);
		mainLayout.addComponent(cmbUnit, "top:68.0px;left:140px;");
		
		chkUnitAll = new CheckBox("All");
		chkUnitAll.setImmediate(true);
		chkUnitAll.setWidth("-1px");
		chkUnitAll.setHeight("-1px");
		//mainLayout.addComponent(chkUnitAll, "top:70.0px;left:473.0px;");
		
		mainLayout.addComponent(new Label("Department :"), "top:100.0px;left:30.0px;");
		
		cmbDepartment= new ComboBox();
		cmbDepartment.setWidth("320px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setImmediate(true);
		mainLayout.addComponent(cmbDepartment, "top:98.0px;left:140px;");
		
		chkDepartmentAll = new CheckBox("All");
		chkDepartmentAll.setImmediate(true);
		chkDepartmentAll.setWidth("-1px");
		chkDepartmentAll.setHeight("-1px");
		mainLayout.addComponent(chkDepartmentAll, "top:100.0px;left:463.0px;");
		
		lblSection = new Label("Section :");
		lblSection.setImmediate(false);
		lblSection.setWidth("-1px");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection, "top:130px;left:30.0px;");
		
		cmbSection= new ComboBox();
		cmbSection.setWidth("320px");
		cmbSection.setHeight("-1px");
		cmbSection.setImmediate(true);
		mainLayout.addComponent(cmbSection, "top:128px;left:140px;");
		
		chkSectionAll = new CheckBox("All");
		chkSectionAll.setImmediate(true);
		chkSectionAll.setWidth("-1px");
		chkSectionAll.setHeight("-1px");
		mainLayout.addComponent(chkSectionAll, "top:130px;left:463.0px;");

		lblemployeeName = new Label("Employee ID :");
		mainLayout.addComponent(lblemployeeName, "top:160px; left:30.0px;");

		cmbEmployeeName= new ComboBox();
		cmbEmployeeName.setWidth("320px");
		cmbEmployeeName.setHeight("-1px");
		cmbEmployeeName.setImmediate(true);
		cmbEmployeeName.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbEmployeeName, "top:158px;left:140px;");
		
		chkEmployeeAll = new CheckBox("All");
		chkEmployeeAll.setImmediate(true);
		chkEmployeeAll.setWidth("-1px");
		chkEmployeeAll.setHeight("-1px");
		mainLayout.addComponent(chkEmployeeAll, "top:160px;left:463.0px;");
		
		mainLayout.addComponent(cButton,"bottom:15px;left:140px;");
		
		PI=new ProgressIndicator();
		PI.setWidth("130px");
		PI.setImmediate(true);
		PI.setEnabled(false);
		mainLayout.addComponent(PI,"bottom:20px;left:330.0px;");
		
		return mainLayout;
	}
}
