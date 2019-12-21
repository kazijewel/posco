package com.appform.hrmModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.MessageBox;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.common.share.CommonMethod;
import com.common.share.FocusMoveByEnter;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class DeleteMonthlySalary extends Window
{
	private AbsoluteLayout mainLayout;
	private SessionBean sessionBean;

	private Label lblUnit;
	private ComboBox cmbUnit;
	private CheckBox chkUnitAll;

	private Label lblSection;
	private ComboBox cmbSection,cmbDepartment;
	private CheckBox chkSectionAll,chkDepartmentAll;

	private Label lblemployeeName;
	private ComboBox cmbEmployeeName;
	private CheckBox chkEmployeeAll;
	private Label lblSalaryMonth;
	private ComboBox cmbSalaryMonth;
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
	String vMonthName="";
	String yearName="";
	int clickCount=0;
	private CommonMethod cm;
	private String menuId = "";
	public DeleteMonthlySalary(SessionBean sessionBean,String menuId) 
	{
		this.sessionBean= sessionBean;
		this.setCaption("DELETE MONTHLY SALARY :: " +sessionBean.getCompany());
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		buildMainLayout();
		setContent(mainLayout);
		componentIni(true);
		cmbSalaryMonthData();
		setEventAction();
		authenticationCheck();
		focusEnter();
		cmbSalaryMonth.focus();
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
		cmbSalaryMonth.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbUnit.removeAllItems();
				if(cmbSalaryMonth.getValue()!=null)
				{
					StringTokenizer strToken=new StringTokenizer(cmbSalaryMonth.getItemCaption(cmbSalaryMonth.getValue()), "- ");
					vMonthName=strToken.nextToken();
					yearName=strToken.nextToken();
					cmbUnitData();
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

		
	}

	private void cmbSalaryMonthData()
	{
		cmbSalaryMonth.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vSalaryMonth,vSalaryYear,DATEADD(s,-1,DATEADD(mm, DATEDIFF(m,0,dSalaryDate)+1,0)) dSalaryDate from tbMonthlySalary  order by dSalaryDate desc";
			List <?> list=session.createSQLQuery(query).list();	

			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbSalaryMonth.addItem( element[2]);
				cmbSalaryMonth.setItemCaption( element[2], element[0].toString()+"-"+element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbSalaryMonthData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void formValidation()
	{
		if(cmbSalaryMonth.getValue()!=null)
		{
			if(cmbUnit.getValue()!=null || chkUnitAll.booleanValue())
			{
				if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
				{
					if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
					{
						if(cmbEmployeeName.getValue()!=null || chkEmployeeAll.booleanValue())
						{
							deleteSalary();
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
			showNotification("Warning!","Please Select Salary Month",Notification.TYPE_WARNING_MESSAGE);
			cmbSalaryMonth.focus();
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

	private void deleteSalary()
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


		query = "select vEmployeeId from tbMonthlySalary where vSalaryMonth= '"+vMonthName+"' AND vSalaryYear= '"+yearName+"' " +
				"AND vUnitID like '"+Unit+"' " +
				"and vDepartmentID like '"+department+"' " +
				"and vSectionID like '"+section+"' " +
				"and vEmployeeID like '"+employee+"'";
		
		if(chkSalary(query))
		{
			clickCount=0;
			System.out.println("Click Count 1: "+clickCount);
			MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to delete salary?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						if(clickCount==0)
						{
							clickCount++;
							Session session=SessionFactoryUtil.getInstance().openSession();
							Transaction tx=session.beginTransaction();
							try
							{
								String deleteTracking="insert into tbUdMonthlySalary(" +
										"dGenerateDate,dSalaryDate,vSalaryMonth,vSalaryYear,vEmployeeID,vEmployeeCode,vFingerId,vProximityID,vEmployeeName," +
										"vDesignationId,vDesignationName,vEmployeeType,dJoiningDate,vUnitId,vUnitName,vDepartmentId,vDepartmentName,vSectionID,vSectionName," +
										"iTotalDay,iHoliday,iWorkingDay,iPresentDay,iAbsentDay,iLeaveDay,iLeaveWithoutPay,mBasic,mHouseRent,mMobileAllowance,mGrossSalary," +
										"mPerDaySalary,mSalaryTaka,iHolidayOTHr,iReplaceOTHr,iHolidayNetOTHr,iWorkingDayNetOTHr,mPerHrOTRate,mOtTaka,mTotalPayable," +
										"mIncomeTax,mNetPayableTaka,mRevenueStamp,vMoneyTransferType,vBranchId,vBranchName,vBankId,vBankName,vAccountNo,vRoutingNo," +
										"mOtherEarning,mOtherDeduction,vUDFlag,vUserId,vUserName,vUserIP,dEntryTime" +
										") " +
										"select dGenerateDate,dSalaryDate,vSalaryMonth,vSalaryYear,vEmployeeID,vEmployeeCode,vFingerId,vProximityID,vEmployeeName," +
										"vDesignationId,vDesignationName,vEmployeeType,dJoiningDate,vUnitId,vUnitName,vDepartmentId,vDepartmentName,vSectionID,vSectionName," +
										"iTotalDay,iHoliday,iWorkingDay,iPresentDay,iAbsentDay,iLeaveDay,iLeaveWithoutPay,mBasic,mHouseRent,mMobileAllowance,mGrossSalary," +
										"mPerDaySalary,mSalaryTaka,iHolidayOTHr,iReplaceOTHr,iHolidayNetOTHr,iWorkingDayNetOTHr,mPerHrOTRate,mOtTaka,mTotalPayable," +
										"mIncomeTax,mNetPayableTaka,mRevenueStamp,vMoneyTransferType,vBranchId,vBranchName,vBankId,vBankName,vAccountNo,vRoutingNo," +
										"mOtherEarning,mOtherDeduction,'DELETE'," +
										"vUserId,vUserName,vUserIP,dEntryTime from tbMonthlySalary " +
										"where vSalaryMonth= '"+vMonthName+"' AND vSalaryYear= '"+yearName+"' and vUnitID like '"+Unit+"' and vDepartmentID like '"+department+"' " +
										"AND vSectionID like '"+section+"' and vEmployeeID like '"+employee+"'";
								session.createSQLQuery(deleteTracking).executeUpdate();

								String deleteQuery="delete from tbMonthlySalary where vSalaryMonth='"+vMonthName+"' AND vSalaryYear='"+yearName+"' and vUnitID like '"+Unit+"' " +
										"and vDepartmentID like '"+department+"' AND vSectionID like '"+section+"' and vEmployeeID like '"+employee+"'"; 
								
								session.createSQLQuery(deleteQuery).executeUpdate();
								tx.commit();
							}
							catch (Exception exp)
							{
								tx.rollback();
								showNotification("deleteSalary",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
							}
							finally{session.close();}

							worker1 = new Worker1();
							worker1.start();
							PI.setEnabled(true);
							PI.setValue(0f);
							cButton.btnDelete.setEnabled(false);
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
			showNotification("Warning!","Salary not found for month "+dMonth.format(cmbSalaryMonth.getValue())+" ", Notification.TYPE_WARNING_MESSAGE);
			txtClear();
			componentIni(true);
		}
	}

	private void cmbUnitData()
	{
		cmbUnit.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vUnitId,vUnitName from tbMonthlySalary where vSalaryMonth='"+vMonthName+"' and vSalaryYear='"+yearName+"' " +
					"order by vUnitName";
			
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
			String query="select distinct vDepartmentId,vDepartmentName from tbMonthlySalary  where vUnitId='"+cmbUnit.getValue().toString()+"' " +
					"and vSalaryMonth='"+vMonthName+"' and vSalaryYear='"+yearName+"' " +
					"order by vDepartmentName";
			
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
			String query="select distinct vSectionId,vSectionName from tbMonthlySalary where vUnitId='"+cmbUnit.getValue().toString()+"' " +
					"and vDepartmentId like '"+(cmbDepartment.getValue()==null?"%":cmbDepartment.getValue())+"' " +
					"and vSalaryMonth='"+vMonthName+"' and vSalaryYear='"+yearName+"' " +
					"order by vSectionName";
			
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
	private void cmbEmployeeNameDataAdd()
	{
		cmbEmployeeName.removeAllItems();
		
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select vEmployeeId,vEmployeeName,vEmployeeCode from tbMonthlySalary where vUnitId like '"+cmbUnit.getValue().toString()+"' " +
					"and vDepartmentId like '"+(cmbDepartment.getValue()==null?"%":cmbDepartment.getValue())+"' " +
					"and vSectionId like '"+(cmbSection.getValue()==null?"%":cmbSection.getValue())+"' " +
					"and vSalaryMonth='"+vMonthName+"' and vSalaryYear='"+yearName+"' " +
					"order by vEmployeeName";
	
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

	private void focusEnter()
	{
		allComp.add(cmbSalaryMonth);
		allComp.add(cmbDepartment);
		allComp.add(cmbSection);
		allComp.add(cmbEmployeeName);
		allComp.add(cButton.btnDelete);
		new FocusMoveByEnter(this,allComp);
	}

	private void txtClear()
	{
		cmbSalaryMonth.setValue(null);
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
		cmbSalaryMonth.setEnabled(b);
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

		// lblSalaryMonth
		lblSalaryMonth = new Label("Salary of Month :");
		lblSalaryMonth.setImmediate(false);
		lblSalaryMonth.setWidth("-1px");
		lblSalaryMonth.setHeight("-1px");
		mainLayout.addComponent(lblSalaryMonth, "top:40.0px;left:30.0px;");

		// cmbSalaryMonth
		cmbSalaryMonth = new ComboBox();
		cmbSalaryMonth.setImmediate(true);
		cmbSalaryMonth.setWidth("150px");
		cmbSalaryMonth.setHeight("-1px");
		//		cmbSalaryMonth.setValue(new java.util.Date());
		mainLayout.addComponent(cmbSalaryMonth, "top:38.0px;left:150.0px;");

		// lblUnit
		lblUnit = new Label("Project :");
		lblUnit.setImmediate(false);
		lblUnit.setWidth("-1px");
		lblUnit.setHeight("-1px");
		mainLayout.addComponent(lblUnit, "top:70.0px;left:30.0px;");

		// cmbUnit
		cmbUnit= new ComboBox();
		cmbUnit.setWidth("320px");
		cmbUnit.setHeight("-1px");
		cmbUnit.setImmediate(true);
		mainLayout.addComponent(cmbUnit, "top:68.0px;left:150.0px;");

		// chkUnitAll
		chkUnitAll = new CheckBox("All");
		chkUnitAll.setImmediate(true);
		chkUnitAll.setWidth("-1px");
		chkUnitAll.setHeight("-1px");
		//mainLayout.addComponent(chkUnitAll, "top:70.0px;left:473.0px;");
		
		mainLayout.addComponent(new Label("Department :"), "top:100.0px;left:30.0px;");

		// cmbSection
		cmbDepartment= new ComboBox();
		cmbDepartment.setWidth("320px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setImmediate(true);
		mainLayout.addComponent(cmbDepartment, "top:98.0px;left:150.0px;");
		
		chkDepartmentAll = new CheckBox("All");
		chkDepartmentAll.setImmediate(true);
		chkDepartmentAll.setWidth("-1px");
		chkDepartmentAll.setHeight("-1px");
		mainLayout.addComponent(chkDepartmentAll, "top:100.0px;left:473.0px;");

		// lblSection
		lblSection = new Label("Section :");
		lblSection.setImmediate(false);
		lblSection.setWidth("-1px");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection, "top:130px;left:30.0px;");

		// cmbSection
		cmbSection= new ComboBox();
		cmbSection.setWidth("320px");
		cmbSection.setHeight("-1px");
		cmbSection.setImmediate(true);
		mainLayout.addComponent(cmbSection, "top:128px;left:150.0px;");

		// chkSectionAll
		chkSectionAll = new CheckBox("All");
		chkSectionAll.setImmediate(true);
		chkSectionAll.setWidth("-1px");
		chkSectionAll.setHeight("-1px");
		mainLayout.addComponent(chkSectionAll, "top:130px;left:473.0px;");

		lblemployeeName = new Label("Employee ID :");
		mainLayout.addComponent(lblemployeeName, "top:160px; left:30.0px;");

		cmbEmployeeName= new ComboBox();
		cmbEmployeeName.setWidth("320px");
		cmbEmployeeName.setHeight("-1px");
		cmbEmployeeName.setImmediate(true);
		cmbEmployeeName.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbEmployeeName, "top:158px;left:150.0px;");

		// chkSectionAll
		chkEmployeeAll = new CheckBox("All");
		chkEmployeeAll.setImmediate(true);
		chkEmployeeAll.setWidth("-1px");
		chkEmployeeAll.setHeight("-1px");
		mainLayout.addComponent(chkEmployeeAll, "top:160px;left:473.0px;");

		// CommonButton
		mainLayout.addComponent(cButton,"bottom:15px;left:150px;");

		// PI
		PI=new ProgressIndicator();
		PI.setWidth("130px");
		PI.setImmediate(true);
		PI.setEnabled(false);
		mainLayout.addComponent(PI,"bottom:20px;left:330.0px;");

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
			showNotification("Salary Deleted Successfully");
			cmbSalaryMonthData();
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
}
