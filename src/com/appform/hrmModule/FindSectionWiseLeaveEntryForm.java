package com.appform.hrmModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class FindSectionWiseLeaveEntryForm extends Window 
{
	@SuppressWarnings("unused")
	private SessionBean sessionBean;	
	private AbsoluteLayout mainLayout;

	private Label lblSectionname;
	private Label lblWorkingMonth;
	private ComboBox cmbSectionName;
	private PopupDateField dWorkingMonth=new PopupDateField();

	private Table table = new Table();
	private ArrayList<Label> lbSL = new ArrayList<Label>();
	private ArrayList<PopupDateField> lblMonthName = new ArrayList<PopupDateField>();
	private ArrayList<Label> lblSectionID = new ArrayList<Label>();
	private ArrayList<Label> lblSectionName = new ArrayList<Label>();

	ArrayList<Component> allComp = new ArrayList<Component>();
	private CommonButton cButton = new CommonButton( "",  "",  "",  "",  "",  "Find", "", "","","");

	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");

	TextRead strMonth=new TextRead("");
	TextRead strSectionID=new TextRead("");
	TextRead strDepartmentID=new TextRead("");

	public FindSectionWiseLeaveEntryForm(SessionBean sessionBean, TextRead strMonth, TextRead strSectionID)
	{		
		this.strMonth = strMonth;
		this.strSectionID = strSectionID;
		this.sessionBean = sessionBean;
		this.setCaption("FIND SECTION WISE LEAVE ENTRY FORM :: "+sessionBean.getCompany());
		this.center();
		this.setWidth("570px");
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setModal(true);
		this.setResizable(false);
		this.setStyleName("cwindow");

		buildMainLayout();
		setContent(mainLayout);
		sectionDataAdd();
		
		tableinitialization();
		setEventActions();
		focusEnter();
	}

	private void focusEnter()
	{
		allComp.add(cmbSectionName);
		allComp.add(dWorkingMonth);
		allComp.add(cButton.btnFind);

		new FocusMoveByEnter(this,allComp);
	}

	private void tableinitialization()
	{
		table.setColumnCollapsingAllowed(true);
		table.setWidth("98%");
		table.setHeight("235px");
		table.setPageLength(0);

		table.addContainerProperty("SL#", Label.class , new Label());
		table.setColumnWidth("SL#",20);

		table.addContainerProperty("Application Date", PopupDateField.class , new PopupDateField());
		table.setColumnWidth("Application Date",120);

		table.addContainerProperty("Section ID", Label.class , new Label());
		table.setColumnWidth("Section ID",50);

		table.addContainerProperty("Section Name", Label.class , new Label());
		table.setColumnWidth("Section Name",280);
		
		table.setColumnCollapsed("Section ID", true);

		rowAddinTable();	
	}

	public void rowAddinTable()
	{
		for(int i=0; i<8; i++)
		{
			tableRowAdd(i);
		}
	}

	public void tableRowAdd(final int ar)
	{
		table.setSelectable(true);
		table.setImmediate(true);
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);		

		lbSL.add(ar, new Label(""));
		lbSL.get(ar).setWidth("100%");
		lbSL.get(ar).setHeight("20px");
		lbSL.get(ar).setValue(ar+1);

		lblMonthName.add(ar,new PopupDateField());
		lblMonthName.get(ar).setDateFormat("dd-MM-yyyy");
		lblMonthName.get(ar).setResolution(PopupDateField.RESOLUTION_MONTH);
		lblMonthName.get(ar).setReadOnly(true);
		lblMonthName.get(ar).setWidth("100%");
		lblMonthName.get(ar).setHeight("20px");

		lblSectionID.add(ar, new Label(""));
		lblSectionID.get(ar).setWidth("100%");
		lblSectionID.get(ar).setHeight("20.0px");

		lblSectionName.add(ar, new Label(""));
		lblSectionName.get(ar).setWidth("100%");
		lblSectionName.get(ar).setHeight("20px");

		table.addItem(new Object[]{lbSL.get(ar),lblMonthName.get(ar),lblSectionID.get(ar),lblSectionName.get(ar)},ar);
	}

	private void sectionDataAdd()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select distinct el.vSectionID,sinf.SectionName from tbEmployeeLeave el inner join " +
					"tbSectionInfo sinf on el.vSectionID=sinf.AutoID where dApplicationDate = " +
					"'"+dFormat.format(dWorkingMonth.getValue())+"' order by sinf.SectionName";

			List <?> list = session.createSQLQuery(query).list();

			for (Iterator <?> iter = list.iterator(); iter.hasNext();)
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
		finally{session.close();}
	}

	private void setEventActions()
	{
		dWorkingMonth.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(dWorkingMonth.getValue()!=null)
				{
					sectionDataAdd();
				}
			}
		});

		cButton.btnFind.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbSectionName.getValue()!=null)
				{
					findButtonEvent();
				}
				else
				{
					showNotification("Warning","Please Select Section Name!!!", Notification.TYPE_WARNING_MESSAGE);
					cmbSectionName.focus();
				}
			}
		});

		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					lblMonthName.get(Integer.valueOf(event.getItemId().toString())).setReadOnly(false);
					strMonth.setValue(dFormat.format(lblMonthName.get(Integer.valueOf(event.getItemId().toString())).getValue()));
					lblMonthName.get(Integer.valueOf(event.getItemId().toString())).setReadOnly(true);
					strSectionID.setValue(lblSectionID.get(Integer.valueOf(event.getItemId().toString())).getValue().toString());
					windowClose();
				}
			}
		});
	}

	private void findButtonEvent()
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try
		{
			String Findquery = "select distinct el.dApplicationDate,el.vSectionID,sinf.SectionName from tbEmployeeLeave " +
					"el inner join tbSectionInfo sinf on el.vSectionID=sinf.AutoID where vSectionID = '"+cmbSectionName.getValue().toString()+"' " +
					"and dApplicationDate = '"+dFormat.format(dWorkingMonth.getValue())+"' order by sinf.SectionName";

			List <?> list = session.createSQLQuery(Findquery).list();

			if(!list.isEmpty())
			{
				tableclear();
				int i=0;
				for(Iterator <?> iter = list.iterator(); iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();
					lblMonthName.get(i).setReadOnly(false);
					lblMonthName.get(i).setValue(element[0]);
					lblMonthName.get(i).setReadOnly(true);
					lblSectionID.get(i).setValue(element[1].toString());
					lblSectionName.get(i).setValue(element[2].toString());

					if(i==lblSectionName.size()-1)
					{
						tableRowAdd(i+1);
					}
					i++;
				}
			}
			else
			{
				tableclear();
				showNotification("No data found!", Notification.TYPE_WARNING_MESSAGE); 
			}
		}
		catch (Exception ex)
		{
			showNotification("findButtonEvent", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void tableclear()
	{
		for(int i=0; i<lbSL.size(); i++)
		{
			lblMonthName.get(i).setReadOnly(false);
			lblMonthName.get(i).setValue(null);
			lblMonthName.get(i).setReadOnly(true);
			lblSectionID.get(i).setValue("");
			lblSectionName.get(i).setValue("");
		}
	}

	private void windowClose()
	{
		this.close();
	}

	private AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);

		setWidth("530px");
		setHeight("355px");

		lblWorkingMonth = new Label("Application Date :");
		lblWorkingMonth.setImmediate(true);
		lblWorkingMonth.setWidth("-1px");
		lblWorkingMonth.setHeight("-1px");
		mainLayout.addComponent(lblWorkingMonth, "top:20.0px;left:20.0px;");

		dWorkingMonth.setValue(new java.util.Date());
		dWorkingMonth.setDateFormat("dd-MM-yyyy");
		dWorkingMonth.setResolution(PopupDateField.RESOLUTION_DAY);
		dWorkingMonth.setInvalidAllowed(false);
		dWorkingMonth.setImmediate(true);
		dWorkingMonth.setWidth("130px");
		mainLayout.addComponent(dWorkingMonth, "top:18.0px;left:140.0px;");

		lblSectionname = new Label();
		lblSectionname.setImmediate(true);
		lblSectionname.setWidth("-1px");
		lblSectionname.setHeight("-1px");
		lblSectionname.setValue("Section Name  :");
		mainLayout.addComponent(lblSectionname, "top:45.0px;left:20.0px;");

		cmbSectionName = new ComboBox();
		cmbSectionName.setImmediate(true);
		cmbSectionName.setWidth("260px");
		cmbSectionName.setHeight("24px");
		cmbSectionName.setImmediate(true);
		cmbSectionName.setNullSelectionAllowed(false);
		mainLayout.addComponent(cmbSectionName, "top:43.0px;left:140.0px;");

		cButton.btnFind.setWidth("80px");
		cButton.btnFind.setHeight("28px");
		mainLayout.addComponent(cButton.btnFind, "top:45.0px;left:440.0px;");

		mainLayout.addComponent(table, "top:75.0px;left:15.0px;");

		return mainLayout;
	}
}
