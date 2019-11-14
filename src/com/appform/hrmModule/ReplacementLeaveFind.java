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
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class ReplacementLeaveFind extends Window
{
	private AbsoluteLayout mainLayout=new AbsoluteLayout();
	private TextField txtTransactionID;
	private PopupDateField dReplacementLeaveFrom;
	private PopupDateField dReplacementLeaveTo;
	private Label lblEmployeeID;
	private ComboBox cmbEmployeeID;
	private Table table;
	private ArrayList<Label> tblblTransactionID = new ArrayList<Label>();
	private ArrayList<Label> tblblEmployeeID = new ArrayList<Label>();
	private ArrayList<Label> tblblEmployeeName = new ArrayList<Label>();
	private ArrayList<PopupDateField> tbDReplacementLeaveFrom = new ArrayList<PopupDateField>();
	private ArrayList<PopupDateField> tbDReplacementLeaveTo = new ArrayList<PopupDateField>();

	SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd");

	@SuppressWarnings("unused")
	private SessionBean sessionBean;

	public ReplacementLeaveFind(SessionBean sessionBean,TextField txtTransactionID)
	{
		this.txtTransactionID = txtTransactionID;
		this.sessionBean=sessionBean;
		this.setCaption("REPLACEMENT LEAVE APPLICATION FIND WINDOW :: "+sessionBean.getCompany());
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
		dReplacementLeaveFrom.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				tableclear();
				cmbEmployeeID.removeAllItems();
				if(dReplacementLeaveFrom.getValue()!=null && dReplacementLeaveTo.getValue()!=null)
				{
					employeeValueAdd();
					tableDataAdd();
				}
			}
		});

		dReplacementLeaveTo.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				tableclear();
				cmbEmployeeID.removeAllItems();
				if(dReplacementLeaveFrom.getValue()!=null && dReplacementLeaveTo.getValue()!=null)
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
			String query = "select vEmployeeID,vEmployeeCode,vEmployeeName from tbReplacementLeaveApplication where " +
					"dReplacementLeaveFrom>='"+dbDateFormat.format(dReplacementLeaveFrom.getValue())+"' " +
					"and dReplacementLeaveTo<='"+dbDateFormat.format(dReplacementLeaveTo.getValue())+"' order by vEmployeeCode";			
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
			String query = "select vTransactionID,vEmployeeCode,vEmployeeName,dReplacementLeaveFrom,dReplacementLeaveTo from " +
					"tbReplacementLeaveApplication where dReplacementLeaveFrom>='"+dbDateFormat.format(dReplacementLeaveFrom.getValue())+"' " +
					"and dReplacementLeaveTo<='"+dbDateFormat.format(dReplacementLeaveTo.getValue())+"' and " +
					"vEmployeeID like '"+employeeCode+"' order by vEmployeeCode,dReplacementLeaveFrom";
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
					tbDReplacementLeaveFrom.get(i).setReadOnly(false);
					tbDReplacementLeaveFrom.get(i).setValue(element[3]);
					tbDReplacementLeaveFrom.get(i).setReadOnly(true);
					tbDReplacementLeaveTo.get(i).setReadOnly(false);
					tbDReplacementLeaveTo.get(i).setValue(element[4]);
					tbDReplacementLeaveTo.get(i).setReadOnly(true);
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
		for(int i=0;i<10;i++)
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
		tblblEmployeeID.get(ar).setImmediate(true);

		tblblEmployeeName.add(ar, new Label(""));
		tblblEmployeeName.get(ar).setWidth("100%");
		tblblEmployeeName.get(ar).setImmediate(true);

		tbDReplacementLeaveFrom.add(ar, new PopupDateField());
		tbDReplacementLeaveFrom.get(ar).setResolution(PopupDateField.RESOLUTION_DAY);
		tbDReplacementLeaveFrom.get(ar).setDateFormat("dd-MM-yyyy");
		tbDReplacementLeaveFrom.get(ar).setImmediate(true);
		tbDReplacementLeaveFrom.get(ar).setWidth("100%");
		tbDReplacementLeaveFrom.get(ar).setReadOnly(true);

		tbDReplacementLeaveTo.add(ar, new PopupDateField());
		tbDReplacementLeaveTo.get(ar).setResolution(PopupDateField.RESOLUTION_DAY);
		tbDReplacementLeaveTo.get(ar).setDateFormat("dd-MM-yyyy");
		tbDReplacementLeaveTo.get(ar).setImmediate(true);
		tbDReplacementLeaveTo.get(ar).setWidth("100%");
		tbDReplacementLeaveTo.get(ar).setReadOnly(true);

		table.addItem(new Object[]{tblblTransactionID.get(ar),tblblEmployeeID.get(ar),
				tblblEmployeeName.get(ar),tbDReplacementLeaveFrom.get(ar),tbDReplacementLeaveTo.get(ar)},ar);
	}

	private void tableclear()
	{
		for(int i=0; i<tblblEmployeeID.size(); i++)
		{
			tblblTransactionID.get(i).setValue("");
			tblblEmployeeID.get(i).setValue("");
			tblblEmployeeName.get(i).setValue("");
			tbDReplacementLeaveFrom.get(i).setReadOnly(false);
			tbDReplacementLeaveFrom.get(i).setValue(null);
			tbDReplacementLeaveFrom.get(i).setReadOnly(true);
			tbDReplacementLeaveTo.get(i).setReadOnly(false);
			tbDReplacementLeaveTo.get(i).setValue(null);
			tbDReplacementLeaveTo.get(i).setReadOnly(true);
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

		dReplacementLeaveFrom = new PopupDateField();
		dReplacementLeaveFrom.setWidth("120px");
		dReplacementLeaveFrom.setHeight("-1px");
		dReplacementLeaveFrom.setImmediate(true);
		dReplacementLeaveFrom.setDateFormat("dd-MM-yyyy");
		dReplacementLeaveFrom.setResolution(PopupDateField.RESOLUTION_DAY);
		dReplacementLeaveFrom.setValue(new Date());
		mainLayout.addComponent(new Label("From : "), "top:20.0px; left:20.0px;");
		mainLayout.addComponent(dReplacementLeaveFrom, "top:18.0px; left:100.0px;");

		dReplacementLeaveTo = new PopupDateField();
		dReplacementLeaveTo.setWidth("120px");
		dReplacementLeaveTo.setHeight("-1px");
		dReplacementLeaveTo.setImmediate(true);
		dReplacementLeaveTo.setDateFormat("dd-MM-yyyy");
		dReplacementLeaveTo.setResolution(PopupDateField.RESOLUTION_DAY);
		dReplacementLeaveTo.setValue(new Date());
		mainLayout.addComponent(new Label("To : "), "top:20.0px; left:240.0px;");
		mainLayout.addComponent(dReplacementLeaveTo, "top:18.0px; left:280.0px;");
		
		lblEmployeeID = new Label("Employee ID : ");
		mainLayout.addComponent(lblEmployeeID, "top:50.0px; left:20.0px");

		cmbEmployeeID = new ComboBox();
		cmbEmployeeID.setImmediate(true);
		cmbEmployeeID.setWidth("250.0px");
		mainLayout.addComponent(cmbEmployeeID, "top:48.0px; left:100.0px;");
		cmbEmployeeID.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);

		table = new Table();
		table.setWidth("98%");
		table.setHeight("200.0px");
		table.setImmediate(true);
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("Transaction ID", Label.class, new Label());
		table.setColumnWidth("Transaction ID", 100);

		table.addContainerProperty("Employee ID", Label.class, new Label());
		table.setColumnWidth("Employee ID", 100);

		table.addContainerProperty("Employee Name", Label.class, new Label());
		table.setColumnWidth("Employee Name", 200);

		table.addContainerProperty("Replacement By Holiday", PopupDateField.class, new PopupDateField());
		table.setColumnWidth("Replacement By Holiday", 80);

		table.addContainerProperty("Replacement To Working Day", PopupDateField.class, new PopupDateField());
		table.setColumnWidth("Replacement To Working Day", 80);

		table.setColumnAlignments(new String[]{Table.ALIGN_RIGHT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,
				Table.ALIGN_CENTER,Table.ALIGN_CENTER});
		table.setColumnCollapsed("Transaction ID", true);
		mainLayout.addComponent(table, "top:80.0px; left:20.0px;");
		table.setStyleName("wordwrap-headers");

		return mainLayout;
	}
}