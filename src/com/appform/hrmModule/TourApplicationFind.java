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
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class TourApplicationFind extends Window
{
	private AbsoluteLayout mainLayout=new AbsoluteLayout();
	private TextField txtTransactionID;
	private PopupDateField dTourFrom;
	private PopupDateField dTourTo;
	private Label lblEmployeeID;
	private ComboBox cmbEmployeeID;
	private Table table;
	private ArrayList<Label> tblblTransactionID = new ArrayList<Label>();
	private ArrayList<Label> tblblEmployeeID = new ArrayList<Label>();
	private ArrayList<Label> tblblEmployeeName = new ArrayList<Label>();
	private ArrayList<PopupDateField> tbDTourFrom = new ArrayList<PopupDateField>();
	private ArrayList<PopupDateField> tbDTourTo = new ArrayList<PopupDateField>();

	SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd");

	@SuppressWarnings("unused")
	private SessionBean sessionBean;

	public TourApplicationFind(SessionBean sessionBean,TextField txtTransactionID)
	{
		this.txtTransactionID = txtTransactionID;
		this.sessionBean=sessionBean;
		this.setCaption("TOUR APPLICATION FIND WINDOW :: "+sessionBean.getCompany());
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
		dTourFrom.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				tableclear();
				cmbEmployeeID.removeAllItems();
				if(dTourFrom.getValue()!=null && dTourTo.getValue()!=null)
				{
					employeeValueAdd();
					tableDataAdd();
				}
			}
		});

		dTourTo.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				tableclear();
				cmbEmployeeID.removeAllItems();
				if(dTourFrom.getValue()!=null && dTourTo.getValue()!=null)
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
			String query = "select vEmployeeID,vEmployeeCode,vEmployeeName from tbTourApplication where " +
					"dTourFrom>='"+dbDateFormat.format(dTourFrom.getValue())+"' " +
					"and dTourTo<='"+dbDateFormat.format(dTourTo.getValue())+"' order by vEmployeeCode";			
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
			String query = "select vTransactionID,vEmployeeCode,vEmployeeName,dTourFrom,dTourTo from " +
					"tbTourApplication where dTourFrom>='"+dbDateFormat.format(dTourFrom.getValue())+"' " +
					"and dTourTo<='"+dbDateFormat.format(dTourTo.getValue())+"' and " +
					"vEmployeeID like '"+employeeCode+"' order by vEmployeeCode,dTourFrom";
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
					tbDTourFrom.get(i).setReadOnly(false);
					tbDTourFrom.get(i).setValue(element[3]);
					tbDTourFrom.get(i).setReadOnly(true);
					tbDTourTo.get(i).setReadOnly(false);
					tbDTourTo.get(i).setValue(element[4]);
					tbDTourTo.get(i).setReadOnly(true);
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

		tbDTourFrom.add(ar, new PopupDateField());
		tbDTourFrom.get(ar).setResolution(PopupDateField.RESOLUTION_DAY);
		tbDTourFrom.get(ar).setDateFormat("dd-MM-yyyy");
		tbDTourFrom.get(ar).setImmediate(true);
		tbDTourFrom.get(ar).setWidth("100%");
		tbDTourFrom.get(ar).setReadOnly(true);

		tbDTourTo.add(ar, new PopupDateField());
		tbDTourTo.get(ar).setResolution(PopupDateField.RESOLUTION_DAY);
		tbDTourTo.get(ar).setDateFormat("dd-MM-yyyy");
		tbDTourTo.get(ar).setImmediate(true);
		tbDTourTo.get(ar).setWidth("100%");
		tbDTourTo.get(ar).setReadOnly(true);

		table.addItem(new Object[]{tblblTransactionID.get(ar),tblblEmployeeID.get(ar),
				tblblEmployeeName.get(ar),tbDTourFrom.get(ar),tbDTourTo.get(ar)},ar);
	}

	private void tableclear()
	{
		for(int i=0; i<tblblEmployeeID.size(); i++)
		{
			tblblTransactionID.get(i).setValue("");
			tblblEmployeeID.get(i).setValue("");
			tblblEmployeeName.get(i).setValue("");
			tbDTourFrom.get(i).setReadOnly(false);
			tbDTourFrom.get(i).setValue(null);
			tbDTourFrom.get(i).setReadOnly(true);
			tbDTourTo.get(i).setReadOnly(false);
			tbDTourTo.get(i).setValue(null);
			tbDTourTo.get(i).setReadOnly(true);
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

		dTourFrom = new PopupDateField();
		dTourFrom.setWidth("120px");
		dTourFrom.setHeight("-1px");
		dTourFrom.setImmediate(true);
		dTourFrom.setDateFormat("dd-MM-yyyy");
		dTourFrom.setResolution(PopupDateField.RESOLUTION_DAY);
		dTourFrom.setValue(new Date());
		mainLayout.addComponent(new Label("From : "), "top:20.0px; left:20.0px;");
		mainLayout.addComponent(dTourFrom, "top:18.0px; left:100.0px;");

		dTourTo = new PopupDateField();
		dTourTo.setWidth("120px");
		dTourTo.setHeight("-1px");
		dTourTo.setImmediate(true);
		dTourTo.setDateFormat("dd-MM-yyyy");
		dTourTo.setResolution(PopupDateField.RESOLUTION_DAY);
		dTourTo.setValue(new Date());
		mainLayout.addComponent(new Label("To : "), "top:20.0px; left:240.0px;");
		mainLayout.addComponent(dTourTo, "top:18.0px; left:280.0px;");
		
		lblEmployeeID = new Label("Employee ID : ");
		mainLayout.addComponent(lblEmployeeID, "top:50.0px; left:20.0px");

		cmbEmployeeID = new ComboBox();
		cmbEmployeeID.setImmediate(true);
		cmbEmployeeID.setWidth("250.0px");
		mainLayout.addComponent(cmbEmployeeID, "top:48.0px; left:100.0px;");

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

		table.addContainerProperty("Tour From", PopupDateField.class, new PopupDateField());
		table.setColumnWidth("Tour From", 100);

		table.addContainerProperty("Tour To", PopupDateField.class, new PopupDateField());
		table.setColumnWidth("Tour From", 100);

		table.setColumnAlignments(new String[]{Table.ALIGN_RIGHT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,
				Table.ALIGN_CENTER,Table.ALIGN_CENTER});
		table.setColumnCollapsed("Transaction ID", true);
		mainLayout.addComponent(table, "top:80.0px; left:20.0px;");

		return mainLayout;
	}
}