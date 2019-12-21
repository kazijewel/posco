package com.appform.hrmModule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.hibernate.Session;
import com.common.share.*;

import java.text.SimpleDateFormat;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class FindLeaveBalance extends Window 
{
	@SuppressWarnings("unused")
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lbYear;
	private InlineDateField dYear;

	private Label lbSectionName;
	private ComboBox cmbSectionName;

	String leaveId = "";

	private TextRead findId = new TextRead();
	private TextRead EmpId = new TextRead();

	private SimpleDateFormat dfYear = new SimpleDateFormat("yyyy");
	private Table table = new Table();

	private ArrayList<Label> lbSl = new ArrayList<Label>();
	private ArrayList<Label> lblLeaveId = new ArrayList<Label>();
	private ArrayList<Label> lblAutoEmpID = new ArrayList<Label>();
	private ArrayList<Label> lbEmployeeID = new ArrayList<Label>();
	private ArrayList<Label> lbEmployeeName = new ArrayList<Label>();
	private ArrayList<Label> amtCL = new ArrayList<Label>();
	private ArrayList<Label> amtSL = new ArrayList<Label>();
	private ArrayList<Label> amtAL = new ArrayList<Label>();

	public FindLeaveBalance(SessionBean sessionBean, TextRead findId,TextRead EmpId)
	{
		this.setCaption("FIND LEAVE BALANCE :: "+sessionBean.getCompany());
		this.setWidth("640px");
		this.setResizable(false);

		this.findId = findId;
		this.EmpId = EmpId;
		this.sessionBean = sessionBean;

		buildMainLayout();
		setContent(mainLayout);

		tableInitialize();

		sectionDataAdd();
		eventAction();
	}

	private void sectionDataAdd()
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();

		try
		{
			String query = "select vSectionId,(select vSectionName from tbSectionInfo si where" +
					" si.vSectionId=el.vSectionId) vSectionName from tbEmpLeaveInfo el";

			List<?> list = session.createSQLQuery(query).list();

			for (Iterator<?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element =  (Object[]) iter.next();	
				cmbSectionName.addItem(element[0]);
				cmbSectionName.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception ex)
		{
			showNotification("sectionDataAdd", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void eventAction()
	{
		cmbSectionName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbSectionName.getValue()!=null)
				{
					tableclear();
					tableDataAdding();
				}
			}
		});

		table.addListener(new ItemClickListener()
		{
			public void itemClick(ItemClickEvent event)
			{
				if(cmbSectionName.getValue()!=null)
				{
					if(event.isDoubleClick())
					{
						leaveId = lblLeaveId.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
						findId.setValue(leaveId);
						EmpId.setValue(lblAutoEmpID.get(Integer.valueOf(event.getItemId().toString())).getValue().toString());
						close();
					}
				}
				else
				{
					showNotification("Warning!","Select Section",Notification.TYPE_WARNING_MESSAGE);
					cmbSectionName.focus();
				}
			}
		});
	}

	private void tableclear()
	{
		for(int i=0; i<lbEmployeeID.size(); i++)
		{
			lbEmployeeID.get(i).setValue("");
			lbEmployeeName.get(i).setValue("");
			amtCL.get(i).setValue("");
			amtSL.get(i).setValue("");
			amtAL.get(i).setValue("");
		}
	}

	private void tableDataAdding()
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try
		{
			String query = "select vLeaveId,(select vFingerId from [dbo].[funEmployeeDetails](vEmployeeId))" +
					" vFingerId,(select vProximityId from [dbo].[funEmployeeDetails](vEmployeeId)) vProximityId," +
					" vEmployeeName,iCasualLeave,iSickLeave,iEarnLeave,vEmployeeID from tbEmpLeaveInfo where" +
					" vSectionId = '"+cmbSectionName.getValue().toString()+"' and vYear = '"+dfYear.format(dYear.getValue())+"'";

			System.out.println("Increment : "+query);
			List<?> list = session.createSQLQuery(query).list();

			if(!list.isEmpty())
			{
				int i=0;
				for(Iterator<?> iter = list.iterator(); iter.hasNext();)
				{						  
					Object[] element = (Object[]) iter.next();

					lblLeaveId.get(i).setValue(element[0]);
					lblAutoEmpID.get(i).setValue(element[7]);
					lbEmployeeID.get(i).setValue(element[1].toString()+" > "+element[2].toString());
					lbEmployeeName.get(i).setValue(element[3]);
					amtCL.get(i).setValue(element[4]);
					amtSL.get(i).setValue(element[5]);
					amtAL.get(i).setValue(element[6]);

					if((i)==lbEmployeeID.size()-1) 
					{
						tableRowAdd(i+1);
					}
					i++;
				}
			}
			else
			{
				showNotification("Warning!","No data found.", Notification.TYPE_WARNING_MESSAGE); 
			}
		}
		catch (Exception ex)
		{
			showNotification(ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);

		setWidth("590px");
		setHeight("380px");

		lbYear = new Label("Year :");
		lbYear.setImmediate(true);
		lbYear.setWidth("-1px");
		lbYear.setHeight("-1px");
		mainLayout.addComponent(lbYear, "top:20.0px;left:45.0px;");

		dYear = new InlineDateField();
		dYear.setImmediate(true);
		dYear.setValue(new java.util.Date());
		dYear.setWidth("110px");
		dYear.setHeight("-1px");
		dYear.setResolution(InlineDateField.RESOLUTION_YEAR);
		mainLayout.addComponent(dYear, "top:18.0px;left:85.0px;");

		lbSectionName = new Label("Section Name :");
		lbSectionName.setImmediate(true);
		lbSectionName.setWidth("-1px");
		lbSectionName.setHeight("-1px");
		mainLayout.addComponent(lbSectionName, "top:20.0px;left:220.0px;");

		cmbSectionName = new ComboBox();
		cmbSectionName.setImmediate(true);
		cmbSectionName.setWidth("250px");
		cmbSectionName.setHeight("-1px");
		cmbSectionName.setNewItemsAllowed(false);
		mainLayout.addComponent(cmbSectionName, "top:18.0px;left:320.0px;");

		mainLayout.addComponent(table, "top:60.0px;left:20.0px;");

		return mainLayout;
	}

	private void tableInitialize()
	{
		table.setColumnCollapsingAllowed(true);
		table.setSelectable(true);

		table.setWidth("98%");
		table.setHeight("230px");

		table.addContainerProperty("SL #", Label.class , new Label());
		table.setColumnWidth("SL #",20);

		table.addContainerProperty("Leave Id", Label.class , new Label());
		table.setColumnWidth("Leave Id",70);

		table.addContainerProperty("EMP ID", Label.class, new Label());
		table.setColumnWidth("EMP ID",130);

		table.addContainerProperty("Finger/Proximity Id", Label.class, new Label());
		table.setColumnWidth("Finger/Proximity Id",130);

		table.addContainerProperty("Employee Name", Label.class , new Label());
		table.setColumnWidth("Employee Name",210);

		table.addContainerProperty("CL", Label.class , new Label());
		table.setColumnWidth("CL",33);	

		table.addContainerProperty("SL", Label.class , new Label());
		table.setColumnWidth("SL",33);

		table.addContainerProperty("AL", Label.class , new Label());
		table.setColumnWidth("AL",33);	

		table.setColumnCollapsed("Leave Id", true);
		table.setColumnCollapsed("EMP ID", true);

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

		lblLeaveId.add(ar, new Label(""));
		lblLeaveId.get(ar).setWidth("100%");
		
		lblAutoEmpID.add(ar, new Label(""));
		lblAutoEmpID.get(ar).setWidth("100%");

		lbEmployeeID.add(ar, new Label(""));
		lbEmployeeID.get(ar).setWidth("100%");
		lbEmployeeID.get(ar).setImmediate(true);

		lbEmployeeName.add(ar, new Label(""));
		lbEmployeeName.get(ar).setWidth("100%");
		lbEmployeeName.get(ar).setImmediate(true);

		amtCL.add(ar, new Label());
		amtCL.get(ar).setWidth("100%");
		amtCL.get(ar).setImmediate(true);

		amtSL.add(ar, new Label());
		amtSL.get(ar).setWidth("100%");
		amtSL.get(ar).setImmediate(true);

		amtAL.add(ar, new Label());
		amtAL.get(ar).setWidth("100%");
		amtAL.get(ar).setImmediate(true);

		table.addItem(new Object[]{lbSl.get(ar),lblLeaveId.get(ar),lblAutoEmpID.get(ar),lbEmployeeID.get(ar),lbEmployeeName.get(ar),amtCL.get(ar),amtSL.get(ar),amtAL.get(ar)},ar);
	}
}
