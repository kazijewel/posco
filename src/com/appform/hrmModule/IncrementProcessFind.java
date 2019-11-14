package com.appform.hrmModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.hibernate.Session;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class IncrementProcessFind extends Window
{
	private AbsoluteLayout mainLayout=new AbsoluteLayout();
	private Label IncrementDate,UnitID,lblUnitName,DepartmentID,lblDepartmentName,IncrementId,EmployeeNameId;
	private ComboBox cmbUnitName,cmbDepartmentName;
	private CheckBox chkDepartmentAll;
	private Table table;
	private ArrayList<Label> tblblSl = new ArrayList<Label>();
	private ArrayList<Label> tblblUnitID = new ArrayList<Label>();
	private ArrayList<Label> tblblUnit = new ArrayList<Label>();
	private ArrayList<Label> tblblDepartmentID = new ArrayList<Label>();
	private ArrayList<Label> tblblDepartment = new ArrayList<Label>();
	private ArrayList<Label> tblblEmployeeId= new ArrayList<Label>();
	private ArrayList<Label> tblblemployeeCode= new ArrayList<Label>();
	private ArrayList<Label> tblblEmployeeName = new ArrayList<Label>();
	private ArrayList<Label> tblblEmployeeType = new ArrayList<Label>();
	private ArrayList<Label> tblblIncrementId = new ArrayList<Label>();
	private ArrayList<Label> tblblIncrementType = new ArrayList<Label>();
	private ArrayList<Label> tblblIncrementDate = new ArrayList<Label>();
	private ArrayList<Label> tblblMonth = new ArrayList<Label>();
	private ArrayList<Label> tblblYear = new ArrayList<Label>();
	
	SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd");

	@SuppressWarnings("unused")
	private SessionBean sessionBean;

	public IncrementProcessFind(SessionBean sessionBean,Label IncrementDate,Label DepartmentID,Label IncrementId,Label EmployeeNameId)
	{
		this.IncrementDate = IncrementDate;
		this.DepartmentID = DepartmentID;
		this.IncrementId = IncrementId;
		this.EmployeeNameId = EmployeeNameId;
		this.sessionBean=sessionBean;
		this.setCaption("INCREMENT PROCESS FIND :: "+sessionBean.getCompany());
		this.center();
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setModal(true);
		this.setResizable(false);
		this.setStyleName("cwindow");
		buildMainLayout();
		setContent(mainLayout);
		tableInitialise();
		setEventAction();
		//DepartmentValueAdd();
		UnitValueAdd();
		//tableDataAdd();
	}

	public void setEventAction()
	{	
		cmbUnitName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				tableclear();
				if(cmbUnitName.getValue()!=null)
				{
					DepartmentValueAdd(cmbUnitName.getValue().toString());
				}
			}
		});
		cmbDepartmentName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				tableclear();
				if(cmbUnitName.getValue()!=null)
				{
					if(cmbDepartmentName.getValue()!=null)
					{
						tableDataAdd();
					}
				}
			}
		});
		chkDepartmentAll.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				tableclear();
				if(cmbUnitName.getValue()!=null)
				{
					if(chkDepartmentAll.booleanValue())
					{
						cmbDepartmentName.setValue(null);
						cmbDepartmentName.setEnabled(false);
						tableDataAdd();
					}
					else
					{
						cmbDepartmentName.setEnabled(true);
					}
				}
			}
		});

		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					IncrementDate.setValue(tblblIncrementDate.get(Integer.valueOf(event.getItemId().toString())).getValue().toString());
					DepartmentID.setValue(tblblDepartmentID.get(Integer.valueOf(event.getItemId().toString())).getValue().toString());
					IncrementId.setValue(tblblIncrementId.get(Integer.valueOf(event.getItemId().toString())).getValue().toString());
					EmployeeNameId.setValue(tblblEmployeeId.get(Integer.valueOf(event.getItemId().toString())).getValue().toString());
					/*System.out.println(EmployeeNameId.getValue().toString());
					System.out.println(IncrementType.getValue().toString());*/
					windowClose();
				}
			}
		});
	}

	private void UnitValueAdd()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select distinct vUnitID, vUnitName from tbSalaryIncrement";
			//System.out.println("UnitValueAdd: "+query);
			
			List <?> lst = session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				for(Iterator <?> itr = lst.iterator(); itr.hasNext();)
				{
					Object [] element = (Object [])itr.next();
					cmbUnitName.addItem(element[0]);
					cmbUnitName.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch (Exception exp)
		{
			showNotification("UnitValueAdd", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}

	private void DepartmentValueAdd(String unitId)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select distinct vDepartmentID, vDepartmentName from tbSalaryIncrement where vUnitId like '"+unitId+"'";
			List <?> lst = session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				for(Iterator <?> itr = lst.iterator(); itr.hasNext();)
				{
					Object [] element = (Object [])itr.next();
					cmbDepartmentName.addItem(element[0]);
					cmbDepartmentName.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch (Exception exp)
		{
			showNotification("DepartmentValueAdd", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}

	private void tableDataAdd()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String unitId="%",DepartmentID = "%";
			
			if(cmbUnitName.getValue()!=null)
			{
				unitId=cmbUnitName.getValue().toString();
			}
			if(!chkDepartmentAll.booleanValue())
			{
				DepartmentID=cmbDepartmentName.getValue().toString();
			}
			
			String query ="select distinct vDepartmentID, vDepartmentName,employeeCode,"
					+ "vEmployeeName,vEmployeeType,vIncrementType,dDate, CONVERT(varchar,DATENAME(MM,dDate)) vMonthName,"
					+ " YEAR(dDate) iYear,vEmployeeId,vIncrementId,vUnitId,vUnitName from tbSalaryIncrement " +
					" where vUnitId like '"+unitId+"' and vDepartmentID like '"+DepartmentID+"' order by dDate asc,vDepartmentName ";
		      System.out.println("TableValueAdd: "+query);
		      
		      
			List <?> lst = session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				int i=0;
				for(Iterator <?> itr=lst.iterator();itr.hasNext();)
				{
					Object [] element = (Object[]) itr.next();
					tblblDepartmentID.get(i).setValue(element[0].toString());
					tblblDepartment.get(i).setValue(element[1].toString());
					tblblemployeeCode.get(i).setValue(element[2].toString());
					tblblEmployeeName.get(i).setValue(element[3].toString());
					tblblEmployeeType.get(i).setValue(element[4].toString());
					tblblIncrementType.get(i).setValue(element[5].toString());
					tblblIncrementDate.get(i).setValue(element[6].toString());
					tblblMonth.get(i).setValue(element[7].toString());
					tblblYear.get(i).setValue(element[8].toString());
					tblblEmployeeId.get(i).setValue(element[9].toString());
					tblblIncrementId.get(i).setValue(element[10].toString());
					tblblUnitID.get(i).setValue(element[11]);
					tblblUnit.get(i).setValue(element[12]);
						
					if(tblblIncrementDate.size()-1==i)
						tableRowAdd(i+1);
					i++;
				}
			}
		}
		catch (Exception exp)
		{
			showNotification("tableDataAdd", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}
	
	public void tableInitialise()
	{
		for(int i=0;i<10;i++)
		{
			tableRowAdd(i);
		}
	}

	public void tableRowAdd(final int ar)
	{
		tblblSl.add(ar, new Label(""));
		tblblSl.get(ar).setWidth("100%");
		tblblSl.get(ar).setImmediate(true);
		tblblSl.get(ar).setValue(ar+1);
		tblblSl.get(ar).setHeight("20px");
		
		tblblemployeeCode.add(ar, new Label(""));
		tblblemployeeCode.get(ar).setWidth("100%");
		tblblemployeeCode.get(ar).setImmediate(true);
		
		tblblEmployeeName.add(ar, new Label(""));
		tblblEmployeeName.get(ar).setWidth("100%");
		tblblEmployeeName.get(ar).setImmediate(true);
		
		tblblUnitID.add(ar, new Label(""));
		tblblUnitID.get(ar).setWidth("100%");
		tblblUnitID.get(ar).setImmediate(true);
		
		tblblUnit.add(ar, new Label(""));
		tblblUnit.get(ar).setWidth("100%");
		tblblUnit.get(ar).setImmediate(true);
		
		tblblDepartmentID.add(ar, new Label(""));
		tblblDepartmentID.get(ar).setWidth("100%");
		tblblDepartmentID.get(ar).setImmediate(true);
		
		tblblDepartment.add(ar, new Label(""));
		tblblDepartment.get(ar).setWidth("100%");
		tblblDepartment.get(ar).setImmediate(true);

		tblblEmployeeType.add(ar, new Label(""));
		tblblEmployeeType.get(ar).setWidth("100%");
		tblblEmployeeType.get(ar).setImmediate(true);

		tblblIncrementType.add(ar, new Label(""));
		tblblIncrementType.get(ar).setWidth("100%");
		tblblIncrementType.get(ar).setImmediate(true);
		
		tblblIncrementDate.add(ar, new Label(""));
		tblblIncrementDate.get(ar).setWidth("100%");
		tblblIncrementDate.get(ar).setImmediate(true);
		
		tblblMonth.add(ar, new Label(""));
		tblblMonth.get(ar).setWidth("100%");
		tblblMonth.get(ar).setImmediate(true);
		
		tblblYear.add(ar, new Label(""));
		tblblYear.get(ar).setWidth("100%");
		tblblYear.get(ar).setImmediate(true);
		
		tblblEmployeeId.add(ar, new Label(""));
		tblblEmployeeId.get(ar).setWidth("100%");
		tblblEmployeeId.get(ar).setImmediate(true);
		
		tblblIncrementId.add(ar, new Label(""));
		tblblIncrementId.get(ar).setWidth("100%");
		tblblIncrementId.get(ar).setImmediate(true);
		
		table.addItem(new Object[]{tblblSl.get(ar),tblblemployeeCode.get(ar),tblblEmployeeName.get(ar),
				tblblUnitID.get(ar),tblblUnit.get(ar),tblblDepartmentID.get(ar),tblblDepartment.get(ar),
				tblblEmployeeType.get(ar),tblblIncrementType.get(ar),
				tblblIncrementDate.get(ar),tblblMonth.get(ar),tblblYear.get(ar),
				tblblEmployeeId.get(ar),tblblIncrementId.get(ar)},ar);
	}

	private void tableclear()
	{
		for(int i=0; i<tblblIncrementDate.size(); i++)
		{
			tblblemployeeCode.get(i).setValue("");
			tblblEmployeeName.get(i).setValue("");
			tblblUnitID.get(i).setValue("");
			tblblUnit.get(i).setValue("");
			tblblDepartmentID.get(i).setValue("");
			tblblDepartment.get(i).setValue("");
			tblblIncrementDate.get(i).setValue("");
			tblblEmployeeType.get(i).setValue("");
			tblblIncrementType.get(i).setValue("");
			tblblMonth.get(i).setValue("");
			tblblYear.get(i).setValue("");
			tblblEmployeeId.get(i).setValue("");
			tblblIncrementId.get(i).setValue("");
				
		}
	}
	
	private void windowClose()
	{
		this.close();
	}

	private AbsoluteLayout buildMainLayout()
	{
		mainLayout=new AbsoluteLayout();
		mainLayout.setWidth("830.0px");
		mainLayout.setHeight("450.0px");
		
		lblUnitName = new Label("Project : ");
		mainLayout.addComponent(lblUnitName, "top:15.0px; left:160.0px");
		
		cmbUnitName = new ComboBox();
		cmbUnitName.setImmediate(true);
		cmbUnitName.setWidth("260.0px");
		cmbUnitName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbUnitName, "top:13.0px; left:265.0px;");
		
		lblDepartmentName = new Label("Department : ");
		mainLayout.addComponent(lblDepartmentName, "top:45.0px; left:160.0px");
		
		cmbDepartmentName = new ComboBox();
		cmbDepartmentName.setImmediate(true);
		cmbDepartmentName.setWidth("260.0px");
		cmbDepartmentName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbDepartmentName, "top:43.0px; left:265.0px;");
		
		chkDepartmentAll =new CheckBox("All");
		chkDepartmentAll.setImmediate(true);
		mainLayout.addComponent(chkDepartmentAll,"top:45.0px; left:525");
		
		table = new Table();
		table.setWidth("98%");
		table.setHeight("300.0px");
		
		table.setImmediate(true);
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("SL#", Label.class, new Label());
		table.setColumnWidth("SL#", 20);

		table.addContainerProperty("Emp ID", Label.class, new Label());
		table.setColumnWidth("Emp ID", 60);
		
		table.addContainerProperty("Employee Name", Label.class, new Label());
		table.setColumnWidth("Employee Name", 130);
		
		table.addContainerProperty("Unit ID", Label.class, new Label());
		table.setColumnWidth("Unit ID", 60);

		table.addContainerProperty("Unit Name", Label.class, new Label());
		table.setColumnWidth("Unit Name", 190);
		
		table.addContainerProperty("Division ID", Label.class, new Label());
		table.setColumnWidth("Division ID", 60);

		table.addContainerProperty("Division Name", Label.class, new Label());
		table.setColumnWidth("Division Name", 100);

		table.addContainerProperty("Employee Type", Label.class, new Label());
		table.setColumnWidth("Employee Type", 80);
		
		table.addContainerProperty("Increment Type", Label.class, new Label());
		table.setColumnWidth("Increment Type", 140);

		table.addContainerProperty("Increment Date", Label.class, new Label());
		table.setColumnWidth("Increment Date", 90);

		table.addContainerProperty("Month", Label.class, new Label());
		table.setColumnWidth("Month", 60);

		table.addContainerProperty("Year", Label.class, new Label());
		table.setColumnWidth("Year", 50);

		table.addContainerProperty("empID", Label.class, new Label());
		table.setColumnWidth("empID", 100);

		table.addContainerProperty("IncId", Label.class, new Label());
		table.setColumnWidth("IncId", 50);

		table.setColumnAlignments(new String[]{
				Table.ALIGN_RIGHT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,
				Table.ALIGN_LEFT,Table.ALIGN_LEFT,
				Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,
				Table.ALIGN_LEFT,Table.ALIGN_CENTER,Table.ALIGN_LEFT,Table.ALIGN_LEFT});
		
		//table.setColumnCollapsed("Increment Date", true);
		table.setColumnCollapsed("Unit ID", true);
		table.setColumnCollapsed("Unit Name", true);
		table.setColumnCollapsed("Division ID", true);
		table.setColumnCollapsed("Employee Type", true);
		table.setColumnCollapsed("empID", true);
		table.setColumnCollapsed("IncId", true);
		mainLayout.addComponent(table, "top:90.0px; left:20.0px;");

		return mainLayout;
	}
}