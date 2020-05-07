package com.appform.hrmModule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.hibernate.Session;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class MonthlyMobileAllowanceFind extends Window 
{
	@SuppressWarnings("unused")
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private ComboBox cmbMobileMonth;

	private Label lblDate ;
	private Label lblUnit;
	private ComboBox cmbUnit;
	private Label lblSection;
	private ComboBox cmbSection,cmbDepartment;

	private Label lblEmployee;
	private ComboBox cmbEmployee;
	private CheckBox chkEmployeeAll,chkDepartmentAll,chkSectionAll;

	private TextRead findId = new TextRead();
	private TextRead empID = new TextRead();

	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat FMonthName = new SimpleDateFormat("MMMMM");
	private SimpleDateFormat FYear = new SimpleDateFormat("yyyy");

	DecimalFormat dfZero=new DecimalFormat("#");

	private Table table = new Table();

	private ArrayList<Label> lbSl = new ArrayList<Label>();
	private ArrayList<Label> lblAutoId = new ArrayList<Label>();
	private ArrayList<Label> lbAutoEmployeeID = new ArrayList<Label>();
	private ArrayList<Label> lbEmployeeID = new ArrayList<Label>();
	private ArrayList<Label> lbEmployeeName = new ArrayList<Label>();
	private ArrayList<Label> lblDesignation = new ArrayList<Label>();
	private ArrayList<Label> dEntryDate = new ArrayList<Label>();
	private ArrayList<Label> lblBalance = new ArrayList<Label>();
	
	public MonthlyMobileAllowanceFind(SessionBean sessionBean, TextRead findId, TextRead empID)
	{
		this.findId = findId;
		this.empID = empID;
		this.sessionBean = sessionBean;

		this.setCaption("MONTHLY MOBILE ALLOWANCE FIND :: "+sessionBean.getCompany());
		this.setWidth("640px");
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);
		tableInitialize();
		cmbMobileMonthData();
		eventAction();
	}

	private void cmbMobileMonthData()
	{
		cmbMobileMonth.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct (DATEADD(s,-1,DATEADD(mm, DATEDIFF(m,0,dDate)+1,0))) dDate,"
					+ "MONTH(dDate)vMonth,YEAR(dDate)vYear from tbMonthlyMobileAllowance order by dDate desc";
			System.out.println("cmbMobileMonthData: "+query);
			
			List <?> list=session.createSQLQuery(query).list();	

			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbMobileMonth.addItem( element[0]);
				cmbMobileMonth.setItemCaption( element[0], FMonthName.format(element[0])+"-"+FYear.format(element[0]));
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbMobileMonthData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void cmbUnitData()
	{
		cmbUnit.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vUnitId,vUnitName from tbMonthlyMobileAllowance where MONTH(dDate)=MONTH('"+cmbMobileMonth.getValue()+"') "
					+ "and YEAR(dDate)=YEAR('"+cmbMobileMonth.getValue()+"') " +
					"order by vUnitName";
			
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
			finally{session.close();
		}
		
	}
	private void cmbDepartmentData() 
	{
		cmbDepartment.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vDepartmentId,vDepartmentName from tbMonthlyMobileAllowance  where vUnitId='"+cmbUnit.getValue().toString()+"' " +
					"and MONTH(dDate)=MONTH('"+cmbMobileMonth.getValue()+"') and YEAR(dDate)=YEAR('"+cmbMobileMonth.getValue()+"')  " +
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
			String query="select distinct vSectionId,vSectionName from tbMonthlyMobileAllowance where vUnitId='"+cmbUnit.getValue().toString()+"' " +
					"and vDepartmentId like '"+(cmbDepartment.getValue()==null?"%":cmbDepartment.getValue())+"' " +
					"and MONTH(dDate)=MONTH('"+cmbMobileMonth.getValue()+"') and YEAR(dDate)=YEAR('"+cmbMobileMonth.getValue()+"') order by vSectionName";
			
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
		cmbEmployee.removeAllItems();
		
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select vEmployeeId,vEmployeeName,vEmployeeCode from tbMonthlyMobileAllowance where vUnitId like '"+cmbUnit.getValue().toString()+"' " +
					"and vDepartmentId like '"+(cmbDepartment.getValue()==null?"%":cmbDepartment.getValue())+"' " +
					"and vSectionId like '"+(cmbSection.getValue()==null?"%":cmbSection.getValue())+"' " +
					"and MONTH(dDate)=MONTH('"+cmbMobileMonth.getValue()+"') and YEAR(dDate)=YEAR('"+cmbMobileMonth.getValue()+"') " +
					"order by vEmployeeName";
	
			System.out.println("Employee"+query);
			
			List <?> lst=session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				for(Iterator <?> itr=lst.iterator();itr.hasNext();)
				{
					Object [] element=(Object[]) itr.next();
					cmbEmployee.addItem(element[0]);
					cmbEmployee.setItemCaption(element[0], (String)element[2]+"-"+element[1].toString());
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
	
	private void eventAction()
	{
		

		cmbMobileMonth.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				tableClear();
				if(cmbMobileMonth.getValue()!=null)
				{
					cmbUnit.removeAllItems();
					cmbUnitData();
				}
			}
		});
		
		cmbUnit.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				tableClear();
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
				tableClear();
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
				tableClear();
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
				tableClear();
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
				tableClear();
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
				tableClear();
				if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
				{
					if(chkEmployeeAll.booleanValue())
					{
						cmbEmployee.setEnabled(false);
						cmbEmployee.setValue(null);
						tableDataLoad();
					}
					else
					{
						cmbEmployee.setEnabled(true);
					}
				}
				else
				{
					chkEmployeeAll.setValue(false);
				}
			}
		});
		cmbEmployee.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
				//tableClear();
				if(cmbEmployee.getValue()!=null)
				{
					tableDataLoad();
				}
			}
		});

		table.addListener(new ItemClickListener()
		{
			public void itemClick(ItemClickEvent event)
			{
				if(cmbUnit.getValue()!=null)
				{
					if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
					{
						if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
						{
							if(event.isDoubleClick())
							{
								findId.setValue(lblAutoId.get(Integer.valueOf(event.getItemId().toString())).getValue().toString());
								empID.setValue(lbAutoEmployeeID.get(Integer.valueOf(event.getItemId().toString())).getValue().toString());
								close();
							}
						}
						else
						{
							showNotification("Warning!","Select Section",Notification.TYPE_WARNING_MESSAGE);
							cmbSection.focus();
						}
					}
					else
					{
						showNotification("Warning!","Select Department",Notification.TYPE_WARNING_MESSAGE);
						cmbDepartment.focus();
					}
				}
				else
				{
					showNotification("Warning!","Select Project",Notification.TYPE_WARNING_MESSAGE);
					cmbUnit.focus();
				}
			}
		});
	}
	
	private void tableDataLoad()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select iAutoID,dDate,vEmployeeID,vEmployeeCode,vEmployeeName,vDesignationName,mMobileAllowance "
						+ "from tbMonthlyMobileAllowance "
						+ "where vUnitId='"+cmbUnit.getValue().toString()+"' and MONTH(dDate)=MONTH('"+cmbMobileMonth.getValue()+"') and YEAR(dDate)=YEAR('"+cmbMobileMonth.getValue()+"') and vSectionID like '"+(chkSectionAll.booleanValue()?"%":(cmbSection.getValue()==null?"%":cmbSection.getValue()))+"' "
						+ "and vDepartmentId like '"+(chkDepartmentAll.booleanValue()?"%":(cmbDepartment.getValue()==null?"%":cmbDepartment.getValue()))+"' "
						+ "and vEmployeeID like '"+(chkEmployeeAll.booleanValue()?"%":(cmbEmployee.getValue()==null?"%":cmbEmployee.getValue()))+"'";
			
			System.out.println("tableDataLoad : "+query);
			List <?> list = session.createSQLQuery(query).list();
			if(!list.isEmpty())
			{
				int i=0;
				for(Iterator <?> iter = list.iterator(); iter.hasNext();)
				{						  
					Object[] element = (Object[]) iter.next();
					lblAutoId.get(i).setValue(element[0]);
					dEntryDate.get(i).setValue(dFormat.format(element[1]));
					lbAutoEmployeeID.get(i).setValue(element[2]);
					lbEmployeeID.get(i).setValue(element[3]);
					lbEmployeeName.get(i).setValue(element[4]);
					lblDesignation.get(i).setValue(element[5]);
					lblBalance.get(i).setValue(dfZero.format(element[6]));

					if((i)==lbEmployeeID.size()-1) 
					{
						tableRowAdd(i+1);
					}
					i++;
				}
			}
			else
			{
				this.getParent().showNotification("Warning!","No data found.", Notification.TYPE_WARNING_MESSAGE); 
			}
		}
		catch (Exception ex)
		{
			this.getParent().showNotification(ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	

	private void tableClear()
	{
		for(int i=0; i<lbEmployeeID.size(); i++)
		{
			lblAutoId.get(i).setValue("");
			dEntryDate.get(i).setValue("");
			lbAutoEmployeeID.get(i).setValue("");
			lbEmployeeID.get(i).setValue("");
			lbEmployeeName.get(i).setValue("");
			lblDesignation.get(i).setValue("");
			lblBalance.get(i).setValue("");
		}
	}
	
	private void tableInitialize()
	{
		table.setColumnCollapsingAllowed(true);
		table.setSelectable(true);

		table.setWidth("98%");
		table.setHeight("270px");

		table.addContainerProperty("SL #", Label.class , new Label());
		table.setColumnWidth("SL #",20);

		table.addContainerProperty("Auto ID", Label.class , new Label());
		table.setColumnWidth("Auto ID",70);

		table.addContainerProperty("Date", Label.class , new Label());
		table.setColumnWidth("Date",70);

		table.addContainerProperty("Employee ID", Label.class, new Label());
		table.setColumnWidth("Employee ID",70);

		table.addContainerProperty("EMP ID", Label.class, new Label());
		table.setColumnWidth("EMP ID",50);

		table.addContainerProperty("Employee Name", Label.class , new Label());
		table.setColumnWidth("Employee Name",170);

		table.addContainerProperty("Designation", Label.class , new Label());
		table.setColumnWidth("Designation",170);

		table.addContainerProperty("Balance", Label.class , new Label());
		table.setColumnWidth("Balance",80);

		table.setColumnCollapsed("Auto ID", true);		
		table.setColumnCollapsed("Employee ID", true);

		rowAddinTable();
	}

	public void rowAddinTable()
	{
		for(int i=0;i<10;i++)
		{
			tableRowAdd(i);
		}
	}

	public void tableRowAdd(final int ar)
	{
		lbSl.add(ar, new Label(""));
		lbSl.get(ar).setWidth("100%");
		lbSl.get(ar).setHeight("14px");
		lbSl.get(ar).setValue(ar+1);

		lblAutoId.add(ar, new Label(""));
		lblAutoId.get(ar).setWidth("100%");

		dEntryDate.add(ar, new Label());
		dEntryDate.get(ar).setWidth("100%");
		dEntryDate.get(ar).setImmediate(true);

		lbAutoEmployeeID.add(ar, new Label(""));
		lbAutoEmployeeID.get(ar).setWidth("100%");
		lbAutoEmployeeID.get(ar).setImmediate(true);
		
		lbEmployeeID.add(ar, new Label(""));
		lbEmployeeID.get(ar).setWidth("100%");
		lbEmployeeID.get(ar).setImmediate(true);

		lbEmployeeName.add(ar, new Label(""));
		lbEmployeeName.get(ar).setWidth("100%");
		lbEmployeeName.get(ar).setImmediate(true);

		lblDesignation.add(ar, new Label(""));
		lblDesignation.get(ar).setWidth("100%");
		lblDesignation.get(ar).setImmediate(true);

		lblBalance.add(ar, new Label());
		lblBalance.get(ar).setWidth("100%");
		lblBalance.get(ar).setImmediate(true);

		table.addItem(new Object[]{lbSl.get(ar),lblAutoId.get(ar),dEntryDate.get(ar),lbAutoEmployeeID.get(ar),
				lbEmployeeID.get(ar),lbEmployeeName.get(ar),lblDesignation.get(ar),lblBalance.get(ar)},ar);
	}


	private AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);

		setWidth("670px");
		setHeight("485px");
		
		lblDate = new Label("Month: ");
		lblDate.setImmediate(false);
		lblDate.setWidth("-1px");
		lblDate.setHeight("-1px");
		mainLayout.addComponent(lblDate, "top:20.0px; left:40.0px;");
		

		cmbMobileMonth = new ComboBox();
		cmbMobileMonth.setImmediate(true);
		cmbMobileMonth.setWidth("150px");
		cmbMobileMonth.setHeight("-1px");
		mainLayout.addComponent(cmbMobileMonth, "top:18.0px; left:150.0px;");

		
		lblUnit = new Label("Project Name: ");
		lblUnit.setImmediate(false); 
		lblUnit.setWidth("-1px");
		lblUnit.setHeight("-1px");

		cmbUnit = new ComboBox();
		cmbUnit.setImmediate(true);
		cmbUnit.setWidth("280px");
		cmbUnit.setHeight("24px");
		cmbUnit.setNullSelectionAllowed(true);
		cmbUnit.setNewItemsAllowed(false);
		cmbUnit.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(lblUnit, "top:45px; left:40.0px;");
		mainLayout.addComponent(cmbUnit, "top:43px; left:150.0px;");
		

		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("280px");
		cmbDepartment.setHeight("24px");
		cmbDepartment.setNullSelectionAllowed(true);
		cmbDepartment.setNewItemsAllowed(false);
		cmbDepartment.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Department :"), "top:70px; left:40.0px;");
		mainLayout.addComponent(cmbDepartment, "top:68px; left:150.0px;");
		
		chkDepartmentAll=new CheckBox("All");
		chkDepartmentAll.setImmediate(true);
		chkDepartmentAll.setWidth("-1px");
		chkDepartmentAll.setHeight("-1px");
		mainLayout.addComponent(chkDepartmentAll, "top:70px; left:435px;");
		
		lblSection = new Label("Section Name:");
		lblSection.setImmediate(false); 
		lblSection.setWidth("-1px");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection, "top:95px; left:40.0px;");

		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("280px");
		cmbSection.setHeight("24px");
		cmbSection.setNullSelectionAllowed(true);
		cmbSection.setNewItemsAllowed(false);
		cmbSection.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbSection, "top:93px; left:150.0px;");
		
		chkSectionAll=new CheckBox("All");
		chkSectionAll.setImmediate(true);
		chkSectionAll.setWidth("-1px");
		chkSectionAll.setHeight("-1px");
		mainLayout.addComponent(chkSectionAll, "top:95px; left:435px;");

		lblEmployee = new Label("Employee ID " );
		lblEmployee.setImmediate(false); 
		lblEmployee.setWidth("-1px");
		lblEmployee.setHeight("-1px");
		mainLayout.addComponent(lblEmployee, "top:118px; left:40.0px;");

		cmbEmployee = new ComboBox();
		cmbEmployee.setImmediate(true);
		cmbEmployee.setWidth("280px");
		cmbEmployee.setHeight("24px");
		cmbEmployee.setNullSelectionAllowed(true);
		cmbEmployee.setNewItemsAllowed(false);
		cmbEmployee.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbEmployee, "top:120px; left:150.0px;");

		chkEmployeeAll = new CheckBox("All");
		chkEmployeeAll.setImmediate(true);
		chkEmployeeAll.setHeight("-1px");
		chkEmployeeAll.setWidth("-1px");
		mainLayout.addComponent(chkEmployeeAll, "top:120px; left:435.0px;");

		mainLayout.addComponent(table, "top:145px;left:20.0px;");

		return mainLayout;
	}
}
