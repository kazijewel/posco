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
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class OverTimeRequestFormFind extends Window
{
	private AbsoluteLayout mainLayout=new AbsoluteLayout();
	private TextField txtTransactionID;
	private PopupDateField dFrom;
	private PopupDateField dTo;
	private Label lblEmployeeID;
	private ComboBox cmbEmployeeID;
	private Table table;
	private ArrayList<Label> tblblTransactionID = new ArrayList<Label>();
	private ArrayList<Label> tblblEmployeeID = new ArrayList<Label>();
	private ArrayList<Label> tblblEmployeeName = new ArrayList<Label>();
	private ArrayList<Label> tbdDate = new ArrayList<Label>();
	private ArrayList<Label> tbOverTime = new ArrayList<Label>();

	SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat dfBangla = new SimpleDateFormat("dd-MM-yyyy");

	@SuppressWarnings("unused")
	private SessionBean sessionBean;

	public OverTimeRequestFormFind(SessionBean sessionBean,TextField txtTransactionID)
	{
		this.txtTransactionID = txtTransactionID;
		this.sessionBean=sessionBean;
		this.setCaption("OVER TIME REQUEST FORM FIND :: "+sessionBean.getCompany());
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
		employeeValueAdd();
		tableDataAdd();
	}

	public void setEventAction()
	{	
		dFrom.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				tableclear();
				cmbEmployeeID.removeAllItems();
				if(dFrom.getValue()!=null && dTo.getValue()!=null)
				{
					employeeValueAdd();
					tableDataAdd();
				}
			}
		});

		dTo.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				tableclear();
				cmbEmployeeID.removeAllItems();
				if(dFrom.getValue()!=null && dTo.getValue()!=null)
				{
					employeeValueAdd();
					tableDataAdd();
				}
			}
		});

		cmbEmployeeID.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				tableclear();
				if(cmbEmployeeID.getValue()!=null)
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

	private void employeeValueAdd()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select vEmployeeID,(select vEmployeeCode from tbEmpOfficialPersonalInfo where vEmployeeId=ot.vEmployeeId)vEmployeeCode,vEmployeeName from tbOTRequest ot where " +
					" dRequestDate>='"+dbDateFormat.format(dFrom.getValue())+"' " +
					" and dRequestDate<='"+dbDateFormat.format(dTo.getValue())+"' order by vEmployeeCode";			
			System.out.println("employeeValueAdd: "+query);
			
			List <?> lst = session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				for(Iterator <?> itr = lst.iterator(); itr.hasNext();)
				{
					Object [] element = (Object [])itr.next();
					cmbEmployeeID.addItem(element[0]);
					cmbEmployeeID.setItemCaption(element[0], element[1]+"-"+element[2]);
				}
			}
		}
		catch (Exception exp)
		{
			showNotification("employeeValueAdd", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
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
			if(cmbEmployeeID.getValue()!=null)
				employeeCode=cmbEmployeeID.getValue().toString();
			String query = "select vTransactionID,(select vEmployeeCode from tbEmpOfficialPersonalInfo where vEmployeeId=ot.vEmployeeId)vEmployeeCode,"
					+ " vEmployeeName,CONVERT(varchar(50),dTimeTotal,108)dTimeTotal,dRequestDate from " +
					" tbOTRequest ot where dRequestDate>='"+dbDateFormat.format(dFrom.getValue())+"' " +
					" and dRequestDate<='"+dbDateFormat.format(dTo.getValue())+"' and " +
					" vEmployeeID like '"+employeeCode+"' order by dRequestDate desc";
			System.out.println("tableDataAdd: "+query);
			
			List <?> lst = session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				int i=0;
				for(Iterator <?> itr=lst.iterator();itr.hasNext();)
				{
					Object [] element = (Object[]) itr.next();
					tblblTransactionID.get(i).setValue(element[0].toString());
					tblblEmployeeID.get(i).setValue(element[1].toString());
					tblblEmployeeName.get(i).setValue(element[2].toString());
					tbOverTime.get(i).setValue(element[3]);
					tbdDate.get(i).setValue(dfBangla.format(element[4]));
		
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
		for(int i=0;i<6;i++)
		{
			tableRowAdd(i);
		}
	}

	public void tableRowAdd(final int ar)
	{

		tblblTransactionID.add(ar, new Label(""));
		tblblTransactionID.get(ar).setWidth("100%");
		tblblTransactionID.get(ar).setImmediate(true);
		
		tblblEmployeeID.add(ar, new Label(""));
		tblblEmployeeID.get(ar).setWidth("100%");
		tblblEmployeeID.get(ar).setHeight("21px");
		tblblEmployeeID.get(ar).setImmediate(true);

		tblblEmployeeName.add(ar, new Label(""));
		tblblEmployeeName.get(ar).setWidth("100%");
		tblblEmployeeName.get(ar).setImmediate(true);

		tbdDate.add(ar, new Label(""));
		tbdDate.get(ar).setWidth("100%");
		tbdDate.get(ar).setImmediate(true);
		

		tbOverTime.add(ar, new Label(""));
		tbOverTime.get(ar).setWidth("100%");
		tbOverTime.get(ar).setImmediate(true);
		

		table.addItem(new Object[]{tblblTransactionID.get(ar),tblblEmployeeID.get(ar),
				tblblEmployeeName.get(ar),tbdDate.get(ar),tbOverTime.get(ar)},ar);
	}

	private void tableclear()
	{
		for(int i=0; i<tblblEmployeeID.size(); i++)
		{
			tblblTransactionID.get(i).setValue("");
			tblblEmployeeID.get(i).setValue("");
			tblblEmployeeName.get(i).setValue("");
			tbdDate.get(i).setValue("");
			tbOverTime.get(i).setValue("");
			
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
		mainLayout.setHeight("350.0px");

		dFrom = new PopupDateField();
		dFrom.setWidth("120px");
		dFrom.setHeight("-1px");
		dFrom.setImmediate(true);
		dFrom.setDateFormat("dd-MM-yyyy");
		dFrom.setResolution(PopupDateField.RESOLUTION_DAY);
		dFrom.setValue(new Date());
		mainLayout.addComponent(new Label("From : "), "top:20.0px; left:20.0px;");
		mainLayout.addComponent(dFrom, "top:18.0px; left:100.0px;");

		dTo = new PopupDateField();
		dTo.setWidth("120px");
		dTo.setHeight("-1px");
		dTo.setImmediate(true);
		dTo.setDateFormat("dd-MM-yyyy");
		dTo.setResolution(PopupDateField.RESOLUTION_DAY);
		dTo.setValue(new Date());
		mainLayout.addComponent(new Label("To : "), "top:20.0px; left:240.0px;");
		mainLayout.addComponent(dTo, "top:18.0px; left:280.0px;");
		
		lblEmployeeID = new Label("Employee ID : ");
		mainLayout.addComponent(lblEmployeeID, "top:50.0px; left:20.0px");

		cmbEmployeeID = new ComboBox();
		cmbEmployeeID.setImmediate(true);
		cmbEmployeeID.setWidth("250.0px");
		mainLayout.addComponent(cmbEmployeeID, "top:48.0px; left:100.0px;");
		cmbEmployeeID.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);

		table = new Table();
		table.setWidth("99%");
		table.setHeight("200.0px");
		table.setImmediate(true);
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("Transaction ID", Label.class, new Label());
		table.setColumnWidth("Transaction ID", 100);

		table.addContainerProperty("Emp. ID", Label.class, new Label());
		table.setColumnWidth("Emp. ID", 70);

		table.addContainerProperty("Employee Name", Label.class, new Label());
		table.setColumnWidth("Employee Name", 220);

		table.addContainerProperty("Date", Label.class, new Label());
		table.setColumnWidth("Date", 75);

		table.addContainerProperty("Over Time", Label.class, new Label());
		table.setColumnWidth("Over Time", 120);


		table.setColumnAlignments(new String[]{Table.ALIGN_RIGHT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_CENTER,Table.ALIGN_CENTER});
		
		table.setColumnCollapsed("Transaction ID", true);
		mainLayout.addComponent(table, "top:80.0px; left:10px;");

		return mainLayout;
	}
}