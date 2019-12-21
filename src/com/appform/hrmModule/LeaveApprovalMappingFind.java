package com.appform.hrmModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class LeaveApprovalMappingFind extends Window
{
	private AbsoluteLayout mainLayout=new AbsoluteLayout();
	private TextField txtTransactionID;
	private PopupDateField dFrom;
	private PopupDateField dTo;
	private Label lblDepartment;
	private ComboBox cmbDepartment;
	private Table table;
	private ArrayList<Label> tblblTransactionID = new ArrayList<Label>();
	private ArrayList<Label> tblblDepartment = new ArrayList<Label>();
	private ArrayList<Label> tblblPrimary = new ArrayList<Label>();
	private ArrayList<Label> tblblHR = new ArrayList<Label>();
	private ArrayList<Label> tblblFinal = new ArrayList<Label>();

	SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd");

	@SuppressWarnings("unused")
	private SessionBean sessionBean;

	public LeaveApprovalMappingFind(SessionBean sessionBean,TextField txtTransactionID)
	{
		this.txtTransactionID = txtTransactionID;
		this.sessionBean=sessionBean;
		this.setCaption("LEAVE APPROVAL MAPPING FIND :: "+sessionBean.getCompany());
		this.center();
		this.setWidth("600px");
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setModal(true);
		this.setResizable(false);
		this.setStyleName("cwindow");

		buildMainLayout();
		setContent(mainLayout);
		tableInitialise();
		setEventAction();
		cmbDepartmentData();
	}

	public void setEventAction()
	{	

		cmbDepartment.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				tableclear();
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
					txtTransactionID.setValue(tblblTransactionID.get(Integer.valueOf(event.getItemId().toString())).getValue().toString());
					windowClose();
				}
			}
		});
	}

	private void cmbDepartmentData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select distinct vDepartmentId,vDepartmentName from tbLeaveApprovalMapping order by vDepartmentName";			
			System.out.println("cmbDepartmentData: "+query);
			
			List <?> lst = session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				for(Iterator <?> itr = lst.iterator(); itr.hasNext();)
				{
					Object [] element = (Object [])itr.next();
					cmbDepartment.addItem(element[0]);
					cmbDepartment.setItemCaption(element[0], element[1]+"");
				}
			}
		}
		catch (Exception exp)
		{
			showNotification("cmbDepartmentData", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}

	private void tableDataAdd()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String employeeCode = "%";
			if(cmbDepartment.getValue()!=null)
				employeeCode=cmbDepartment.getValue().toString();
			String query = "select vTransactionId,vDepartmentName,vDesignationNamePrimary,vDesignationNameFinal,vDesignationNameHR from tbLeaveApprovalMapping where vDepartmentId='"+cmbDepartment.getValue()+"'";
			List <?> lst = session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				int i=0;
				for(Iterator <?> itr=lst.iterator();itr.hasNext();)
				{
					Object [] element = (Object[]) itr.next();
					tblblTransactionID.get(i).setValue(element[0].toString());
					tblblDepartment.get(i).setValue(element[1].toString());
					tblblPrimary.get(i).setValue(element[2].toString());
					tblblFinal.get(i).setValue(element[3]);
					tblblHR.get(i).setValue(element[4]);
		
					if(tblblTransactionID.size()-1==i)
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
		for(int i=0;i<5;i++)
		{
			tableRowAdd(i);
		}
	}

	public void tableRowAdd(final int ar)
	{

		tblblTransactionID.add(ar, new Label(""));
		tblblTransactionID.get(ar).setWidth("100%");
		tblblTransactionID.get(ar).setHeight("22px");
		tblblTransactionID.get(ar).setImmediate(true);
		
		tblblDepartment.add(ar, new Label(""));
		tblblDepartment.get(ar).setWidth("100%");
		tblblDepartment.get(ar).setHeight("22px");
		tblblDepartment.get(ar).setImmediate(true);

		tblblPrimary.add(ar, new Label(""));
		tblblPrimary.get(ar).setWidth("100%");
		tblblPrimary.get(ar).setHeight("22px");
		tblblPrimary.get(ar).setImmediate(true);
		

		tblblHR.add(ar, new Label(""));
		tblblHR.get(ar).setWidth("100%");
		tblblHR.get(ar).setImmediate(true);
		
		
		tblblFinal.add(ar, new Label(""));
		tblblFinal.get(ar).setWidth("100%");
		tblblFinal.get(ar).setImmediate(true);
		

		table.addItem(new Object[]{tblblTransactionID.get(ar),tblblDepartment.get(ar),
				tblblPrimary.get(ar),tblblHR.get(ar),tblblFinal.get(ar)},ar);
	}

	private void tableclear()
	{
		for(int i=0; i<tblblDepartment.size(); i++)
		{
			tblblTransactionID.get(i).setValue("");
			tblblDepartment.get(i).setValue("");
			tblblPrimary.get(i).setValue("");
			tblblHR.get(i).setValue("");
			tblblFinal.get(i).setValue("");
			
		}
	}

	private void windowClose()
	{
		this.close();
	}

	private AbsoluteLayout buildMainLayout()
	{
		mainLayout=new AbsoluteLayout();
		mainLayout.setWidth("580.0px");
		mainLayout.setHeight("300px");

		dFrom = new PopupDateField();
		dFrom.setWidth("120px");
		dFrom.setHeight("-1px");
		dFrom.setImmediate(true);
		dFrom.setDateFormat("dd-MM-yyyy");
		dFrom.setResolution(PopupDateField.RESOLUTION_DAY);
		dFrom.setValue(new Date());
	   //mainLayout.addComponent(new Label("From : "), "top:20.0px; left:20.0px;");
		mainLayout.addComponent(dFrom, "top:18.0px; left:100.0px;");
		dFrom.setVisible(false);

		dTo = new PopupDateField();
		dTo.setWidth("120px");
		dTo.setHeight("-1px");
		dTo.setImmediate(true);
		dTo.setDateFormat("dd-MM-yyyy");
		dTo.setResolution(PopupDateField.RESOLUTION_DAY);
		dTo.setValue(new Date());
		//mainLayout.addComponent(new Label("To : "), "top:20.0px; left:240.0px;");
		mainLayout.addComponent(dTo, "top:18.0px; left:280.0px;");
		dTo.setVisible(false);
		
		lblDepartment = new Label("Department : ");
		mainLayout.addComponent(lblDepartment, "top:50.0px; left:20.0px");

		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("250.0px");
		mainLayout.addComponent(cmbDepartment, "top:48.0px; left:100.0px;");

		table = new Table();
		table.setWidth("98%");
		table.setHeight("200.0px");
		table.setImmediate(true);
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("Transaction ID", Label.class, new Label());
		table.setColumnWidth("Transaction ID", 80);

		table.addContainerProperty("Department", Label.class, new Label());
		table.setColumnWidth("Department", 150);

		table.addContainerProperty("Primary Approve", Label.class, new Label());
		table.setColumnWidth("Primary Approve", 165);
		
		table.addContainerProperty("HR Approve", Label.class, new Label());
		table.setColumnWidth("HR Approve", 165);

		table.addContainerProperty("Final Approve", Label.class, new Label());
		table.setColumnWidth("Final Approve", 165);


		table.setColumnAlignments(new String[]{Table.ALIGN_RIGHT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,
				Table.ALIGN_CENTER,Table.ALIGN_CENTER});
		table.setColumnCollapsed("Transaction ID", true);
		table.setColumnCollapsed("Department", true);
		
		mainLayout.addComponent(table, "top:80.0px; left:20.0px;");

		return mainLayout;
	}
}