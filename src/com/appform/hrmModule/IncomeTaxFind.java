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
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class IncomeTaxFind extends Window 
{
	
	@SuppressWarnings("unused")
	private SessionBean sessionBean;	
	private AbsoluteLayout mainLayout;
	private ComboBox cmbEmployeeId ;
	private Label lblEmployeeName;
	private Label lblFiscalYear;
	private ComboBox cmbFiscalYear;

	private Table table = new Table();
	private ArrayList<Label> lbSL = new ArrayList<Label>();
	private ArrayList<Label> lbEmployeeId = new ArrayList<Label>();
	private ArrayList<Label> lbAppDate = new ArrayList<Label>();
	private ArrayList<Label> lbDesignation = new ArrayList<Label>();
	private ArrayList<Label> lbEmpName = new ArrayList<Label>();
	
	private CheckBox chkEmployee;
	ArrayList<Component> allComp = new ArrayList<Component>();
	private CommonButton cButton = new CommonButton( "",  "",  "",  "",  "",  "Find", "", "","","");
	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	String autoId = "";
	String empId = "";
	String findDate = "";

	private TextRead txtEmpId = new TextRead();
	private TextRead txtFiscalYearFind = new TextRead();

	public IncomeTaxFind(SessionBean sessionBean, TextRead txtAuto,TextRead txtFiscalYearFind)
	{		
		this.txtEmpId = txtAuto;
        this.txtFiscalYearFind=txtFiscalYearFind;	
        this.sessionBean = sessionBean;
		this.setCaption("FIND INCOME TAX INFO :: "+sessionBean.getCompany());
		this.center();
		this.setWidth("570px");
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setModal(true);
		this.setResizable(false);
		this.setStyleName("cwindow");
		
		buildMainLayout();
		setContent(mainLayout);
		tableinitialization();
		setEventActions();
		TaxLoad();
		cmbFiscalYear.focus();
		focusEnter();
	}
	private void TaxLoad()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		try{
			String sql="select distinct 0,dFiscalYear from tbIncomeTaxInfo";
			Iterator<?> iter=session.createSQLQuery(sql).list().iterator();
			while(iter.hasNext())
			{
				Object element[]=(Object[])iter.next();
				cmbFiscalYear.addItem(element[1]);
			}
		}catch(Exception exp)
		{
			showNotification("Employee :",Notification.TYPE_WARNING_MESSAGE);
		}
	}
	
	private void cmbEmployeeDataLoad()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		try{
			String sql="select vEmployeeID,employeeCode from tbIncomeTaxInfo " +
					"where dFiscalYear = '"+cmbFiscalYear.getItemCaption(cmbFiscalYear.getValue())+"'";
			
			Iterator<?> iter=session.createSQLQuery(sql).list().iterator();
			while(iter.hasNext())
			{
				Object element[]=(Object[])iter.next();
				cmbEmployeeId.addItem(element[0]);
				cmbEmployeeId.setItemCaption(element[0], element[1].toString());
			}
		}catch(Exception exp)
		{
			showNotification("Employee :",Notification.TYPE_WARNING_MESSAGE);
		}
	}
	private void setEventActions()
	{	
		cmbFiscalYear.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(cmbFiscalYear.getValue()!=null)
				{
					cmbEmployeeId.removeAllItems();
			     	cmbEmployeeDataLoad();
				}
			}
		});

		cButton.btnFind.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbFiscalYear.getValue()!=null)
				{
					if(cmbEmployeeId.getValue()!=null || chkEmployee.booleanValue())
					{
						findButtonEvent();
					}
					else 
					{
						showNotification("Please Select Fiscal Year ", Notification.TYPE_WARNING_MESSAGE);
						cmbFiscalYear.focus();
					}
				}
				else 
				{
					showNotification("Please Select Employee ID ", Notification.TYPE_WARNING_MESSAGE);
					cmbEmployeeId.focus();
				}
			}
		});

		chkEmployee.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbFiscalYear.getValue()!=null)
				{
					if(chkEmployee.booleanValue())
					{
						cmbEmployeeId.setValue(null);
						cmbEmployeeId.setEnabled(false);					
					}
					else
					{
						cmbEmployeeId.setEnabled(true);
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
					autoId = lbEmployeeId.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					txtEmpId.setValue(autoId);
					txtFiscalYearFind.setValue(cmbFiscalYear.getValue().toString());
					windowClose();
				}
			}
		});
	}

	private void focusEnter()
	{		
		allComp.add(cmbFiscalYear);
		allComp.add(cmbEmployeeId);
		allComp.add(cButton.btnFind);
		new FocusMoveByEnter(this,allComp);
	}

	private void tableinitialization()
	{
		table.setColumnCollapsingAllowed(true);
		table.setWidth("98%");
		table.setHeight("230px");
		table.setPageLength(0);

		table.addContainerProperty("SL #", Label.class , new Label());
		table.setColumnWidth("SL #",20);	
		
		table.addContainerProperty("Emp ID", Label.class , new Label());
		table.setColumnWidth("Emp ID",45);
		
		table.addContainerProperty("Tax. Date", Label.class , new Label());
		table.setColumnWidth("Tax. Date",65);
		
		table.addContainerProperty("Emp Name", Label.class , new Label());
		table.setColumnWidth("Emp Name",150);
		
		table.addContainerProperty("Designation", Label.class , new Label());
		table.setColumnWidth("Designation",160);
	
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

		lbEmployeeId.add(ar,new Label());
		lbEmployeeId.get(ar).setWidth("100%");
		lbEmployeeId.get(ar).setHeight("14px");

		lbAppDate.add(ar, new Label(""));
		lbAppDate.get(ar).setWidth("100%");
		lbAppDate.get(ar).setHeight("14px");
		lbAppDate.get(ar).setStyleName("appDate");
		
		lbEmpName.add(ar, new Label(""));
		lbEmpName.get(ar).setWidth("100%");
		
		lbDesignation.add(ar, new Label(""));
		lbDesignation.get(ar).setWidth("100%");

		table.addItem(new Object[]{lbSL.get(ar),lbEmployeeId.get(ar),lbAppDate.get(ar),lbEmpName.get(ar),lbDesignation.get(ar)},ar);
	}

	private void findButtonEvent()
	{
		String Employee="%";
		if(!chkEmployee.booleanValue())
		{
			Employee=cmbEmployeeId.getValue().toString();
		}
		String Fiscal =cmbFiscalYear.getValue().toString();
				
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String Findquery = "select vEmployeeId,dDate,vEmployeeName,vDesignationName,vStaffType from tbIncomeTaxInfo " +
					"where vEmployeeId like '"+Employee+"' and dFiscalYear = '"+Fiscal+"' order by dDate ";
						
			System.out.println("Find Query :" + Findquery);
			List <?> list = session.createSQLQuery(Findquery).list();
			
			if(!list.isEmpty())
			{
				tableclear();
				int i=0;
				for(Iterator <?> iter = list.iterator(); iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();
					lbEmployeeId.get(i).setValue(element[0]);
					lbAppDate.get(i).setValue((element[1]));
					lbEmpName.get(i).setValue(element[3].toString());
					lbDesignation.get(i).setValue(element[2].toString());
					
					if((i)==lbAppDate.size()-1)
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
		finally{session.close();}
	}

	private void tableclear()
	{
		for(int i=0; i<lbSL.size(); i++)
		{
			lbEmployeeId.get(i).setValue("");
			lbAppDate.get(i).setValue("");
			lbDesignation.get(i).setValue("");
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

		setWidth("560px");
		setHeight("400px");
	
		// lblEmployeeName
		lblFiscalYear = new Label("Fiscal Year :");
		lblFiscalYear.setImmediate(false);
		lblFiscalYear.setWidth("-1px");
		lblFiscalYear.setHeight("-1px");
		mainLayout.addComponent(lblFiscalYear, "top:15.0px; left:15.0px;");

		// txtEmployeeName
		cmbFiscalYear = new ComboBox();
		cmbFiscalYear.setImmediate(true);
		cmbFiscalYear.setWidth("110px");
		cmbFiscalYear.setHeight("22px");
		mainLayout.addComponent(cmbFiscalYear, "top:13.0px; left:130.0px;");	

		lblEmployeeName = new Label();
		lblEmployeeName.setImmediate(true);
		lblEmployeeName.setWidth("-1px");
		lblEmployeeName.setHeight("-1px");
		lblEmployeeName.setValue("Employee ID :");
		mainLayout.addComponent(lblEmployeeName, "top:40.0px;left:15.0px;");

		cmbEmployeeId = new ComboBox();
		cmbEmployeeId.setImmediate(true);
		cmbEmployeeId.setWidth("210px");
		cmbEmployeeId.setHeight("24px");
		cmbEmployeeId.setImmediate(true);
		cmbEmployeeId.setNullSelectionAllowed(false);
		mainLayout.addComponent(cmbEmployeeId, "top:38.0px;left:130.0px;");

		chkEmployee = new CheckBox("All");
		chkEmployee.setImmediate(false);
		chkEmployee.setWidth("-1px");
		chkEmployee.setHeight("24px");
		chkEmployee.setImmediate(true);
		mainLayout.addComponent(chkEmployee, "top:40.0px;left:345.0px;");

		cButton.btnFind.setWidth("80px");
		cButton.btnFind.setHeight("28px");
		mainLayout.addComponent(cButton.btnFind, "top:40.0px;left:430.0px;");

		mainLayout.addComponent(table, "top:100.0px;left:15.0px;");
		return mainLayout;
	}
}
