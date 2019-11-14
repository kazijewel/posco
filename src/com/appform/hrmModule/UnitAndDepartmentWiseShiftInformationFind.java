package com.appform.hrmModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

import com.common.share.FocusMoveByEnter;
import com.common.share.ReportDate;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class UnitAndDepartmentWiseShiftInformationFind extends Window 
{
	private AbsoluteLayout mainLayout;

	private Label lblCommon;

	private ComboBox cmbUnit;
	private ComboBox cmbDepartment;
	TextField txtDeptId;
	TextField txtSecID;
	TextField txtshiftID;
	private Table table;
	private ArrayList <Label> lblUnitID = new ArrayList<Label>();
	private ArrayList <Label> lblUnitName = new ArrayList<Label>();
	private ArrayList <Label> lblSecID = new ArrayList<Label>();
	private ArrayList <Label> lblSecName = new ArrayList<Label>();
	private ArrayList <Label> lblShiftID = new ArrayList<Label>();
	private ArrayList <Label> lblShiftName = new ArrayList<Label>();
	private ArrayList <Label> lblStartTime = new ArrayList<Label>();
	private ArrayList <Label> lblEndTime = new ArrayList<Label>();
	private ArrayList <Label> lblStatus = new ArrayList<Label>();

	SessionBean sessionBean;

	ArrayList<Component> allComp = new ArrayList<Component>();

	SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss aa");
	ReportDate reportTime = new ReportDate();

	public UnitAndDepartmentWiseShiftInformationFind(SessionBean sessionBean, TextField txtDeptId, TextField txtSecID, TextField txtshiftID)
	{
		this.sessionBean=sessionBean;
		this.txtDeptId = txtDeptId;
		this.txtSecID = txtSecID;
		this.txtshiftID = txtshiftID;
		this.setModal(true);
		this.setStyleName("cwindow");
		
		this.setResizable(false);
		this.setCaption("FIND UNIT /DIVISION WISE SHIFT INFORMATION :: "+sessionBean.getCompany());

		buildMainLayout();
		setContent(mainLayout);
		
		txtClear();

		focusEnter();
		btnAction();
		tableInitialize();

		cmbUnitDataAdd();
	}

	public void btnAction()
	{
		cmbUnit.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbDepartment.removeAllItems();
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
				tableClear();
				if(cmbDepartment.getValue()!=null)
				{
					tableDataAdd();
				}
			}
		});
		
		table.addListener(new ItemClickListener()
		{
			public void itemClick(ItemClickEvent event)
			{
				if(event.isDoubleClick())
				{
					txtDeptId.setValue(lblUnitID.get(Integer.parseInt(event.getItemId().toString())).getValue().toString());
					txtSecID.setValue(lblSecID.get(Integer.parseInt(event.getItemId().toString())).getValue().toString());
					txtshiftID.setValue(lblShiftID.get(Integer.parseInt(event.getItemId().toString())).getValue().toString());
					windowClose();
				}
			}
		});
	}
	
	private void windowClose()
	{
		this.close();
	}

	private void cmbUnitDataAdd()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select vUnitId,vUnitName from tbUnit_Section_Wise_ShiftInfo " +
					" order by vUnitName";
			List <?> lstDept = session.createSQLQuery(query).list();
			if(!lstDept.isEmpty())
			{
				for(Iterator <?> itr = lstDept.iterator(); itr.hasNext(); )
				{
					Object [] element = (Object[])itr.next();
					cmbUnit.addItem(element[0]);
					cmbUnit.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbUnitDataAdd", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}

	private void cmbDepartmentDataAdd()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select vDepartmentId,vDepartmentName from tbUnit_Section_Wise_ShiftInfo where vUnitId = '"+cmbUnit.getValue()+"' " +
					" order by vDepartmentName";
			List <?> lstDept = session.createSQLQuery(query).list();
			if(!lstDept.isEmpty())
			{
				for(Iterator <?> itr = lstDept.iterator(); itr.hasNext(); )
				{
					Object [] element = (Object[])itr.next();
					cmbDepartment.addItem(element[0]);
					cmbDepartment.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbDepartmentDataAdd", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}

	private void tableDataAdd()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select vUnitId,vUnitName,vDepartmentID,vDepartmentName,vShiftID,vShiftName," +
					" dShiftStart,dShiftEnd,isActive from tbUnit_Section_Wise_ShiftInfo where vUnitID = '"+cmbUnit.getValue()+"' " +
					" and vDepartmentID = '"+cmbDepartment.getValue()+"'";
			List <?> lstDept = session.createSQLQuery(query).list();
			if(!lstDept.isEmpty())
			{
				int i = 0;
				for(Iterator <?> itr = lstDept.iterator(); itr.hasNext(); )
				{
					Object [] element = (Object[])itr.next();
					lblUnitID.get(i).setValue(element[0]);
					lblUnitName.get(i).setValue(element[1]);
					lblSecID.get(i).setValue(element[2]);
					lblSecName.get(i).setValue(element[3]);
					lblShiftID.get(i).setValue(element[4]);
					lblShiftName.get(i).setValue(element[5]);
					lblStartTime.get(i).setValue(timeFormat.format(element[6]));
					lblEndTime.get(i).setValue(timeFormat.format(element[7]));
					if(element[8].toString().equals("1"))
						lblStatus.get(i).setValue("Active");
					else
						lblStatus.get(i).setValue("Inactive");
					i++;
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbShiftNameDataAdd", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}

	private void focusEnter()
	{
		allComp.add(cmbUnit);
		allComp.add(cmbDepartment);

		new FocusMoveByEnter(this,allComp);
	}

	private void txtClear()
	{
		cmbUnit.setValue(null);
		cmbDepartment.setValue(null);
	}

	private void tableClear()
	{
		for(int i=0; i<lblUnitID.size();i++)
		{
			lblUnitID.get(i).setValue("");
			lblUnitName.get(i).setValue("");
			lblSecID.get(i).setValue("");
			lblSecName.get(i).setValue("");
			lblShiftID.get(i).setValue("");
			lblShiftName.get(i).setValue("");
			lblStartTime.get(i).setValue("");
			lblEndTime.get(i).setValue("");
			lblStatus.get(i).setValue("");
		}
	}

	private AbsoluteLayout buildMainLayout() 
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("820px");
		mainLayout.setHeight("300px");
		mainLayout.setMargin(false);

		lblCommon = new Label("Project Name : ");
		mainLayout.addComponent(lblCommon, "top:20.0px; left:20.0px;");

		cmbUnit = new ComboBox();
		cmbUnit.setImmediate(true);
		cmbUnit.setWidth("270.0px");
		mainLayout.addComponent(cmbUnit, "top:18.0px; left:130.0px;");

		lblCommon = new Label("Department Name : ");
		mainLayout.addComponent(lblCommon, "top:20.0px; left:420.0px;");

		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("270.0px");
		mainLayout.addComponent(cmbDepartment, "top:18.0px; left:530.0px;");

		table = new Table();
		table.setWidth("99%");
		table.setHeight("210.0px");
		table.setColumnCollapsingAllowed(true);
		
		table.addContainerProperty("Project ID", Label.class, new Label());
		table.setColumnWidth("Project ID", 40);
		
		table.addContainerProperty("Project", Label.class, new Label());
		table.setColumnWidth("Project", 180);

		table.addContainerProperty("Department ID", Label.class, new Label());
		table.setColumnWidth("Department ID", 40);

		table.addContainerProperty("Department", Label.class, new Label());
		table.setColumnWidth("Department", 180);

		table.addContainerProperty("Shift ID", Label.class, new Label());
		table.setColumnWidth("Shift ID", 40);

		table.addContainerProperty("Shift", Label.class, new Label());
		table.setColumnWidth("Shift", 130);

		table.addContainerProperty("Start", Label.class, new Label());
		table.setColumnWidth("Start", 80);

		table.addContainerProperty("End", Label.class, new Label());
		table.setColumnWidth("END", 80);
		
		table.addContainerProperty("Status", Label.class, new Label());
		table.setColumnWidth("Status", 70);

		table.setColumnCollapsed("Unit ID", true);
		table.setColumnCollapsed("Division ID", true);
		table.setColumnCollapsed("Shift ID", true);
		
		mainLayout.addComponent(table, "top:60.0px; left:10.0px");
		
		return mainLayout;
	}
	
	private void tableInitialize()
	{
		for(int i=0;i<10;i++)
			tableRowAdd(i);
	}
	
	private void tableRowAdd(final int ar)
	{
		lblUnitID.add(new Label());
		lblUnitID.get(ar).setWidth("100%");
		lblUnitID.get(ar).setImmediate(true);
		
		lblUnitName.add(new Label());
		lblUnitName.get(ar).setWidth("100%");
		lblUnitName.get(ar).setHeight("16px");
		lblUnitName.get(ar).setImmediate(true);

		lblSecID.add(new Label());
		lblSecID.get(ar).setWidth("100%");
		lblSecID.get(ar).setImmediate(true);

		lblSecName.add(new Label());
		lblSecName.get(ar).setWidth("100%");
		lblSecName.get(ar).setImmediate(true);

		lblShiftID.add(new Label());
		lblShiftID.get(ar).setWidth("100%");
		lblShiftID.get(ar).setImmediate(true);

		lblShiftName.add(new Label());
		lblShiftName.get(ar).setWidth("100%");
		lblShiftName.get(ar).setImmediate(true);

		lblStartTime.add(new Label());
		lblStartTime.get(ar).setWidth("100%");
		lblStartTime.get(ar).setImmediate(true);

		lblEndTime.add(new Label());
		lblEndTime.get(ar).setWidth("100%");
		lblEndTime.get(ar).setImmediate(true);
		
		lblStatus.add(new Label());
		lblStatus.get(ar).setWidth("100%");
		lblStatus.get(ar).setImmediate(true);
		
		table.addItem(new Object[]{lblUnitID.get(ar),lblUnitName.get(ar),lblSecID.get(ar),lblSecName.get(ar),
				lblShiftID.get(ar),lblShiftName.get(ar),lblStartTime.get(ar),lblEndTime.get(ar),lblStatus.get(ar)},ar);
	}
}