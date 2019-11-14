package com.appform.hrmModule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import com.common.share.*;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;

import java.awt.event.FocusListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import javax.swing.JOptionPane;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Window.Notification;
//Developed by Sabrina Alam
public class EditEmployeeAttendanceOld extends Window 
{
	private AbsoluteLayout mainLayout;
	private Label lblDate = new Label("Date");
	private PopupDateField dDate = new PopupDateField();

	private Label lblDeptName = new Label("");
	private ComboBox cmbDeptName = new ComboBox();

	private Label lblPermitted = new Label("");
	private ComboBox cmbPermit = new ComboBox();

	private Label lblReason = new Label("");

	private TextField txtPermit = new TextField("");

	private TextRead txtColor = new TextRead("");
	private Label lblCl= new Label("");

	private TextRead txtColorp = new TextRead("");
	private Label lblClp= new Label("");

	private OptionGroup RadioBtnGroup;
	private static final List<String> type2 = Arrays.asList(new String[] {"Employee ID","Proximity ID","Employee Name"});

	private Label lblEmployee = new Label("");
	private Label lblProx = new Label("");
	private Label lblFinger = new Label("");
	private ComboBox cmbEmployeeName = new ComboBox();
	private CheckBox chkPresent = new CheckBox();
	private CheckBox chkAbsent = new CheckBox();

	private SessionBean sessionBean;

	String computerName = "";
	String userName = "";
	String year = "";
	String deptID = "";
	String strEmpDeptID ="";
	String strEmpID ="";
	String strDeptID ="";
	int i=0;
	int j=0;
	String FingerID ="";
	boolean t;

	private boolean isUpdate=false;
	private boolean isFind= false;

	private TextRead trIDDeptID = new TextRead("");
	private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy");
	private SimpleDateFormat Hourformat = new SimpleDateFormat("HH");
	private SimpleDateFormat Minformat = new SimpleDateFormat("mm");
	private SimpleDateFormat Secformat = new SimpleDateFormat("ss");
	private SimpleDateFormat DBdateformat = new SimpleDateFormat("yyyy-MM-dd");
	private Table table = new Table();
	private ArrayList<Label> lbSl = new ArrayList<Label>();
	private ArrayList<Label> lblempID = new ArrayList<Label>();
	private ArrayList<Label> lbProxID = new ArrayList<Label>();
	private ArrayList<Label> lbEmployeeName = new ArrayList<Label>();
	private ArrayList<Label> lbDesignation = new ArrayList<Label>();
	private ArrayList<TextRead> lbAttendDate = new ArrayList<TextRead>();
	private ArrayList<PopupDateField>InDate	= new ArrayList<PopupDateField>();
	private ArrayList<TimeField> InTime1 = new ArrayList<TimeField>();
	private ArrayList<TimeField> InTime2 = new ArrayList<TimeField>();
	private ArrayList<TimeField> InTime3 = new ArrayList<TimeField>();
	private ArrayList<PopupDateField>OutDate = new ArrayList<PopupDateField>(); 
	private ArrayList<TimeField> OutTime1 = new ArrayList<TimeField>();
	private ArrayList<TimeField> OutTime2 = new ArrayList<TimeField>();
	private ArrayList<TimeField> OutTime3 = new ArrayList<TimeField>();
	private ArrayList<TextField> InAmPm = new ArrayList<TextField>();
	private ArrayList<TextField> OutAmPm = new ArrayList<TextField>();
	private ArrayList<NativeButton> Close = new ArrayList<NativeButton>();
	private ArrayList<NativeButton> PermitClose = new ArrayList<NativeButton>();
	private ArrayList<Label> lblPermitBy = new ArrayList<Label>();
	private ArrayList<TextField> txtReason = new ArrayList<TextField>();
	private ArrayList<Label> lbSecId = new ArrayList<Label>();
	private ArrayList<Label> lbSecName = new ArrayList<Label>();

	private ArrayList<TimeField> OutTime = new ArrayList<TimeField>();

	private CommonButton button = new CommonButton("New", "Save", "", "","Refresh","","","","","Exit");
	ArrayList<Component> allComp = new ArrayList<Component>();
	private DecimalFormat Deci=new DecimalFormat("#00:00");

	public EditEmployeeAttendanceOld(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("EDIT EMPLOYEE ATTENDANCE :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);
		tableInitialize();

		btnIni(true);
		componentIni(true);
		buttonAction();

		departMentdataAdd();
		addCmbPermitData();

		focusEnter();
		authenticationCheck();

		button.btnNew.focus();
	}

	private void authenticationCheck()
	{
		if(!sessionBean.isSubmitable()){
			button.btnSave.setVisible(false);
		}

		if(!sessionBean.isUpdateable()){
			button.btnEdit.setVisible(false);
		}

		if(!sessionBean.isDeleteable()){
			button.btnDelete.setVisible(false);
		}
	}

	private void buttonAction()
	{
		cmbPermit.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbPermit.getValue()!=null)
				{
					tablePermitDataAdd(cmbPermit.getValue().toString());
					cmbPermit.setValue(null);
				}
			}
		});

		button.btnNew.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate = false;
				isFind = false;
				newButtonEvent();
				chkPresent.setValue(true);
				RadioBtnGroup.setValue("Name");
			}
		});

		button.btnFind.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isFind = true;
			}
		});

		button.btnEdit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate = true;
				updateAction();
			}
		});

		chkPresent.addListener(new Listener() {
			public void componentEvent(Event event)
			{
				RadioBtnGroup.select("Employee Name");
				cmbPermit.setValue(null);
				cmbEmployeeName.setValue(null);

				for(int i =0; i<lbProxID.size(); i++)
				{
					lblempID.get(i).setValue("");
					lbProxID.get(i).setValue("");
					lbEmployeeName.get(i).setValue("");
					lbDesignation.get(i).setValue("");
					lblPermitBy.get(i).setValue("");
					txtReason.get(i).setValue("");
					//amtHour.get(i).setValue("");
					lblPermitBy.get(i).setValue("");
					InTime1.get(i).setValue("");
					InTime2.get(i).setValue("");
					InTime3.get(i).setValue("");
					InAmPm.get(i).setValue("");
					OutAmPm.get(i).setValue("");
					OutTime1.get(i).setValue("");
					OutTime2.get(i).setValue("");
					OutTime3.get(i).setValue("");
					lbSecId.get(i).setValue("");
					lbSecName.get(i).setValue("");
					lbAttendDate.get(i).setValue("");
					PermitClose.get(i).setVisible(false);
				}
				if(chkPresent.booleanValue()==true)
				{
					RadioBtnGroup.select("Employee ID");
					chkAbsent.setValue(false);
				}
				else
				{
					chkAbsent.setValue(true);
				}
			}
		});

		chkAbsent.addListener(new Listener() {
			public void componentEvent(Event event)
			{
				RadioBtnGroup.select("Employee Name");
				cmbPermit.setValue(null);
				cmbEmployeeName.setValue(null);

				for(int i =0; i<lbProxID.size(); i++)
				{
					lblempID.get(i).setValue("");
					lbProxID.get(i).setValue("");
					lbEmployeeName.get(i).setValue("");
					lbDesignation.get(i).setValue("");
					lblPermitBy.get(i).setValue("");
					txtReason.get(i).setValue("");
					//amtHour.get(i).setValue("");
					lblPermitBy.get(i).setValue("");
					InTime1.get(i).setValue("");
					InTime2.get(i).setValue("");
					InTime3.get(i).setValue("");
					InAmPm.get(i).setValue("");
					OutAmPm.get(i).setValue("");
					OutTime1.get(i).setValue("");
					OutTime2.get(i).setValue("");
					OutTime3.get(i).setValue("");
					lbSecId.get(i).setValue("");
					lbSecName.get(i).setValue("");
					lbAttendDate.get(i).setValue("");
					PermitClose.get(i).setVisible(false);
				}
				if(chkAbsent.booleanValue()==true)
				{  
					RadioBtnGroup.select("Employee ID");
					chkPresent.setValue(false);
				}
				else
				{
					chkPresent.setValue(true);
				}
			}
		});

		cmbDeptName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				RadioBtnGroup.select("Employee Name");
				cmbPermit.setValue(null);
				cmbEmployeeName.setValue(null);

				for(int i =0; i<lbProxID.size(); i++)
				{
					lblempID.get(i).setValue("");
					lbProxID.get(i).setValue("");
					lbEmployeeName.get(i).setValue("");
					lbDesignation.get(i).setValue("");
					lblPermitBy.get(i).setValue("");
					txtReason.get(i).setValue("");
					//amtHour.get(i).setValue("");
					lblPermitBy.get(i).setValue("");
					InTime1.get(i).setValue("");
					InTime2.get(i).setValue("");
					InTime3.get(i).setValue("");
					InAmPm.get(i).setValue("");
					OutAmPm.get(i).setValue("");
					OutTime1.get(i).setValue("");
					OutTime2.get(i).setValue("");
					OutTime3.get(i).setValue("");
					lbSecId.get(i).setValue("");
					lbSecName.get(i).setValue("");
					lbAttendDate.get(i).setValue("");
					PermitClose.get(i).setVisible(false);
				}
				if(cmbDeptName.getValue()!=null){

					RadioBtnGroup.select("Employee ID");
				}
			}
		});

		dDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{

				RadioBtnGroup.select("Employee Name");
				cmbDeptName.setValue(null);
				cmbPermit.setValue(null);
				cmbEmployeeName.setValue(null);

				for(int i =0; i<lbProxID.size(); i++)
				{
					lblempID.get(i).setValue("");
					lbProxID.get(i).setValue("");
					lbEmployeeName.get(i).setValue("");
					lbDesignation.get(i).setValue("");
					lblPermitBy.get(i).setValue("");
					txtReason.get(i).setValue("");
					//amtHour.get(i).setValue("");
					lblPermitBy.get(i).setValue("");
					InTime1.get(i).setValue("");
					InTime2.get(i).setValue("");
					InTime3.get(i).setValue("");
					InAmPm.get(i).setValue("");
					OutAmPm.get(i).setValue("");
					OutTime1.get(i).setValue("");
					OutTime2.get(i).setValue("");
					OutTime3.get(i).setValue("");
					lbSecId.get(i).setValue("");
					lbSecName.get(i).setValue("");
					lbAttendDate.get(i).setValue("");
					PermitClose.get(i).setVisible(false);
				}

				/*if(chkPresent.booleanValue())
				{
					chkAbsent.setValue(true);
					if(RadioBtnGroup.getValue().toString().equals("Proximity ID")){
						RadioBtnGroup.setValue("Name");
					}
					else{
						RadioBtnGroup.setValue("Proximity ID");
					}
				}
				else{
					chkPresent.setValue(true);
				}
				if(chkAbsent.booleanValue())
				{
					chkPresent.setValue(true);
					if(RadioBtnGroup.getValue().toString().equals("Proximity ID")){
						RadioBtnGroup.setValue("Name");
					}
					else{
						RadioBtnGroup.setValue("Employee Name");
					}
				}*/
				/*else{
					chkAbsent.setValue(true);
				}*/
			}
		});

		RadioBtnGroup.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbDeptName.getValue()!=null){
					if(chkPresent.booleanValue()==true)
					{
						if(RadioBtnGroup.getValue().toString().equals("Employee ID"))
						{
							lblFinger.setVisible(true);
							String query = "select empCode,empID from funCalcEmployeeAttendance('"+DBdateformat.format(dDate.getValue())+"'," +
									"'"+DBdateformat.format(dDate.getValue())+"','%','"+cmbDeptName.getValue()+"')";
							addCmbEmployeeData(query);
							lblFinger.setVisible(true);
							lblProx.setVisible(false);
							lblEmployee.setVisible(false);
						}
						else if(RadioBtnGroup.getValue().toString().equals("Proximity ID"))
						{
							lblFinger.setVisible(true);
							String query = "select empCode,empCode from funCalcEmployeeAttendance('"+DBdateformat.format(dDate.getValue())+"'," +
									"'"+DBdateformat.format(dDate.getValue())+"','%','"+cmbDeptName.getValue()+"')";
							addCmbEmployeeData(query);
							lblProx.setVisible(true);
							lblFinger.setVisible(false);
							lblEmployee.setVisible(false);
						}
						else if(RadioBtnGroup.getValue().toString().equals("Employee Name") )
						{
							String query = "select empCode,empName from funCalcEmployeeAttendance('"+DBdateformat.format(dDate.getValue())+"'," +
									"'"+DBdateformat.format(dDate.getValue())+"','%','"+cmbDeptName.getValue()+"')";
							addCmbEmployeeData(query);
							lblFinger.setVisible(false);
							lblProx.setVisible(false);
							lblEmployee.setVisible(true);
						}
					}

					if(chkAbsent.booleanValue()==true)
					{
						if(RadioBtnGroup.getValue().toString().equals("Employee ID"))
						{
							lblFinger.setVisible(true);
							String query = "select vProximityId,employeeCode from tbEmployeeInfo where vProximityId not in (select empCode from funCalcEmployeeAttendance('"+DBdateformat.format(dDate.getValue())+"'," +
									"'"+DBdateformat.format(dDate.getValue())+"','%','"+cmbDeptName.getValue()+"')) and vSectionId='"+cmbDeptName.getValue()+"'";
							addCmbEmployeeData(query);
							lblFinger.setVisible(true);
							lblProx.setVisible(false);
							lblEmployee.setVisible(false);
						}
						else if(RadioBtnGroup.getValue().toString().equals("Proximity ID"))
						{
							lblFinger.setVisible(true);
							String query ="select vProximityId,vProximityId from tbEmployeeInfo where vProximityId not in (select empCode from funCalcEmployeeAttendance('"+DBdateformat.format(dDate.getValue())+"'," +
									"'"+DBdateformat.format(dDate.getValue())+"','%','"+cmbDeptName.getValue()+"')) and vSectionId='"+cmbDeptName.getValue()+"'";
							addCmbEmployeeData(query);
							lblProx.setVisible(true);
							lblFinger.setVisible(false);
							lblEmployee.setVisible(false);
						}
						else if(RadioBtnGroup.getValue().toString().equals("Employee Name") )
						{
							String query = "select vProximityId,vEmployeeName from tbEmployeeInfo where vProximityId not in (select empCode from funCalcEmployeeAttendance('"+DBdateformat.format(dDate.getValue())+"'," +
									"'"+DBdateformat.format(dDate.getValue())+"','%','"+cmbDeptName.getValue()+"')) and vSectionId='"+cmbDeptName.getValue()+"'";
							addCmbEmployeeData(query);
							lblFinger.setVisible(false);
							lblProx.setVisible(false);
							lblEmployee.setVisible(true);
						}

					}
				}
			}
		});

		cmbEmployeeName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbEmployeeName.getValue()!=null)
				{
					tableDataAdd(cmbEmployeeName.getValue().toString());
				}
			}
		});

		button.btnSave.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				formValidation();
			}
		});

		button.btnRefresh.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate = false;
				isFind=false;
				refreshButtonEvent();
			}
		});

		button.btnExit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});
	}

	private void tableInitialize()
	{

		table.setColumnCollapsingAllowed(true);
		table.setWidth("98%");
		table.setHeight("335px");
		table.setPageLength(0);

		table.addContainerProperty("Del", NativeButton.class , new NativeButton());
		table.setColumnWidth("Del",30);

		table.addContainerProperty("SL #", Label.class , new Label());
		table.setColumnWidth("SL #",20);

		table.addContainerProperty("Employee ID", Label.class, new Label());
		table.setColumnWidth("Employee ID", 100);

		table.addContainerProperty("Proximity ID", Label.class , new Label());
		table.setColumnWidth("Proximity ID",80);

		table.addContainerProperty("Employee Name", Label.class , new Label());
		table.setColumnWidth("Employee Name",170);

		table.addContainerProperty("Designation", Label.class , new Label());
		table.setColumnWidth("Designation",120);	

		table.addContainerProperty("Attend date", TextRead.class , new TextRead());
		table.setColumnWidth("Attend date",100);

		table.addContainerProperty("In Date", PopupDateField.class, new PopupDateField());
		table.setColumnWidth("In Date", 110);

		table.addContainerProperty("HH(IN)", TimeField.class , new TimeField());
		table.setColumnWidth("HH(IN)",28);

		table.addContainerProperty("Min(IN)", TimeField.class , new TimeField());
		table.setColumnWidth("Min(IN)",28);

		table.addContainerProperty("Sec(IN)", TimeField.class , new TimeField());
		table.setColumnWidth("Sec(IN)",28);

		table.addContainerProperty("TF(IN)", TextField.class , new TextField());
		table.setColumnWidth("TF(IN)",28);

		table.addContainerProperty("Out Date", PopupDateField.class, new PopupDateField());
		table.setColumnWidth("Out Date", 110);

		table.addContainerProperty("HH(OUT)", TimeField.class , new TimeField());
		table.setColumnWidth("HH(OUT)",28);

		table.addContainerProperty("Min(OUT)", TimeField.class , new TimeField());
		table.setColumnWidth("Min(OUT)",28);

		table.addContainerProperty("Sec(OUT)", TimeField.class , new TimeField());
		table.setColumnWidth("Sec(OUT)",28);

		table.addContainerProperty("TF(OUT)", TextField.class , new TextField());
		table.setColumnWidth("TF(OUT)",28);

		/*table.addContainerProperty("Tol. Hr", Label.class , new Label());
		table.setColumnWidth("Tot. Hr",40);*/

		table.addContainerProperty("Permitted By", Label.class , new Label());
		table.setColumnWidth("Permitted By",120);

		table.addContainerProperty("p", NativeButton.class , new NativeButton());
		table.setColumnWidth("p",20);

		table.addContainerProperty("Reason", TextField.class , new TextField());
		table.setColumnWidth("Reason",120);	

		table.addContainerProperty("sId", Label.class , new Label());
		table.setColumnWidth("sId",50);

		table.addContainerProperty("sName", Label.class , new Label());
		table.setColumnWidth("sName",120);

		table.setColumnCollapsed("Sec(IN)", true);
		table.setColumnCollapsed("Sec(OUT)", true);
		table.setColumnCollapsed("p", true);
		table.setColumnCollapsed("Attend date", true);
		table.setColumnCollapsed("sId", true);
		table.setColumnCollapsed("sName", true);

		table.setColumnAlignments(new String[] {Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,
				Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_RIGHT,Table.ALIGN_CENTER,
				Table.ALIGN_RIGHT,Table.ALIGN_RIGHT,Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,
				Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER //,Table.ALIGN_CENTER
				,Table.ALIGN_CENTER,Table.ALIGN_CENTER});

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
		Close.add(ar, new NativeButton(""));
		Close.get(ar).setWidth("100%");
		Close.get(ar).setImmediate(true);
		Close.get(ar).setIcon(new ThemeResource("../icons/closeGreen.png"));
		Close.get(ar).addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				lblempID.get(ar).setValue("");
				lbProxID.get(ar).setValue("");
				lbEmployeeName.get(ar).setValue("");
				lbDesignation.get(ar).setValue("");
				lblPermitBy.get(ar).setValue("");
				txtReason.get(ar).setValue("");
				//amtHour.get(ar).setValue("");
				lblPermitBy.get(ar).setValue("");
				txtReason.get(ar).setValue("");
				InTime1.get(ar).setValue("");
				InTime2.get(ar).setValue("");
				InTime3.get(ar).setValue("");
				InAmPm.get(ar).setValue("");
				OutAmPm.get(ar).setValue("");
				OutTime1.get(ar).setValue("");
				OutTime2.get(ar).setValue("");
				OutTime3.get(ar).setValue("");
			}
		});

		lbSl.add(ar, new Label(""));
		lbSl.get(ar).setWidth("100%");
		lbSl.get(ar).setHeight("20px");
		lbSl.get(ar).setValue(ar+1);

		lblempID.add(ar,new Label(""));
		lblempID.get(ar).setImmediate(true);
		lblempID.get(ar).setWidth("100%");
		lblempID.get(ar).setHeight("20px");

		lbProxID.add(ar, new Label(""));
		lbProxID.get(ar).setWidth("100%");
		lbProxID.get(ar).setImmediate(true);
		lbProxID.get(ar).setHeight("-1px");

		lbEmployeeName.add(ar, new Label(""));
		lbEmployeeName.get(ar).setWidth("100%");
		lbEmployeeName.get(ar).setImmediate(true);

		lbDesignation.add(ar, new Label(""));
		lbDesignation.get(ar).setWidth("100%");
		lbDesignation.get(ar).setImmediate(true);

		lbAttendDate.add(ar, new TextRead(""));
		lbAttendDate.get(ar).setWidth("100%");
		lbAttendDate.get(ar).setImmediate(true);

		InDate.add(ar,new PopupDateField());
		InDate.get(ar).setWidth("110px");
		InDate.get(ar).setDateFormat("dd-MM-yyyy");
		InDate.get(ar).setResolution(PopupDateField.RESOLUTION_DAY);
		InDate.get(ar).setValue(new Date());
		InDate.get(ar).setImmediate(true);

		InTime1.add(ar, new TimeField());
		InTime1.get(ar).setWidth("28px");
		//InTime1.get(ar).setHeight("15px");
		InTime1.get(ar).setInputPrompt("hh");
		InTime1.get(ar).setImmediate(true);
		InTime1.get(ar).setStyleName("Intime");
		InTime1.get(ar).setMaxLength(2);
		InTime1.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!InTime1.get(ar).getValue().toString().isEmpty())
				{			
					if(Double.parseDouble(InTime1.get(ar).getValue().toString())>12)
					{
						InTime1.get(ar).setValue("");
						InAmPm.get(ar).setValue("PM");
					}
					else{
						InTime1.get(ar).setValue(InTime1.get(ar).getValue().toString());
					}
				}
			}
		});

		InTime2.add(ar, new TimeField());
		InTime2.get(ar).setWidth("28px");
		//InTime2.get(ar).setHeight("15px");
		InTime2.get(ar).setInputPrompt("mm");
		InTime2.get(ar).setImmediate(true);
		InTime2.get(ar).setStyleName("Intime");
		InTime2.get(ar).setMaxLength(2);
		InTime2.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!InTime2.get(ar).getValue().toString().isEmpty())
				{			
					if(Double.parseDouble(InTime2.get(ar).getValue().toString())>=60)
					{
						InTime2.get(ar).setValue("");
					}
					else{
						InTime2.get(ar).setValue(InTime2.get(ar).getValue().toString());
					}
				}
			}
		});

		InTime3.add(ar, new TimeField());
		InTime3.get(ar).setWidth("28px");
		//InTime3.get(ar).setHeight("15px");
		InTime3.get(ar).setInputPrompt("ss");
		InTime3.get(ar).setImmediate(true);
		InTime3.get(ar).setStyleName("Intime");
		InTime3.get(ar).setMaxLength(2);
		InTime3.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!InTime3.get(ar).getValue().toString().isEmpty())
				{			
					if(Double.parseDouble(InTime3.get(ar).getValue().toString())>=60)
					{
						InTime3.get(ar).setValue("");
					}
					else{
						InTime3.get(ar).setValue(InTime3.get(ar).getValue().toString());
					}
				}
			}
		});
		InTime3.get(ar).setEnabled(false);

		InAmPm.add(ar, new TextField(""));
		InAmPm.get(ar).setWidth("28px");
		//InAmPm.get(ar).setHeight("-1px");
		InAmPm.get(ar).setInputPrompt("AM");
		InAmPm.get(ar).setImmediate(true);
		InAmPm.get(ar).setMaxLength(2);
		InAmPm.get(ar).setTextChangeEventMode(TextChangeEventMode.EAGER);
		InAmPm.get(ar).addListener(new TextChangeListener() {
			public void textChange(TextChangeEvent event)
			{
				if(event.getText().equalsIgnoreCase("A"))
				{
					InAmPm.get(ar).setValue("AM");
				}
				if(event.getText().equalsIgnoreCase("P"))
				{
					InAmPm.get(ar).setValue("PM");
				}
			}

		});

		OutDate.add(ar,new PopupDateField());
		OutDate.get(ar).setWidth("110px");
		OutDate.get(ar).setDateFormat("dd-MM-yyyy");
		OutDate.get(ar).setResolution(PopupDateField.RESOLUTION_DAY);
		OutDate.get(ar).setValue(new Date());
		OutDate.get(ar).setImmediate(true);

		OutTime1.add(ar, new TimeField());
		OutTime1.get(ar).setWidth("28px");
		//OutTime1.get(ar).setHeight("15px");
		OutTime1.get(ar).setInputPrompt("hh");
		OutTime1.get(ar).setImmediate(true);
		OutTime1.get(ar).addStyleName("Outtime");
		OutTime1.get(ar).setMaxLength(2);
		OutTime1.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!OutTime1.get(ar).getValue().toString().isEmpty())
				{			
					if(Double.parseDouble(OutTime1.get(ar).getValue().toString())>=24)
					{
						OutTime1.get(ar).setValue("");
					}
					else{
						OutTime1.get(ar).setValue(OutTime1.get(ar).getValue().toString());
					}
				}
			}
		});

		OutTime2.add(ar, new TimeField());
		OutTime2.get(ar).setWidth("28px");
		//OutTime2.get(ar).setHeight("22px");
		OutTime2.get(ar).setInputPrompt("mm");
		OutTime2.get(ar).setImmediate(true);
		OutTime2.get(ar).setStyleName("Outtime");
		OutTime2.get(ar).setMaxLength(2);
		OutTime2.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!OutTime2.get(ar).getValue().toString().isEmpty())
				{			
					if(Double.parseDouble(OutTime2.get(ar).getValue().toString())>=60)
					{
						OutTime2.get(ar).setValue("");
					}
					else{
						OutTime2.get(ar).setValue(OutTime2.get(ar).getValue().toString());
					}
				}
			}
		});

		OutTime3.add(ar, new TimeField());
		OutTime3.get(ar).setWidth("28px");
		//OutTime3.get(ar).setHeight("22px");
		OutTime3.get(ar).setInputPrompt("ss");
		OutTime3.get(ar).setImmediate(true);
		OutTime3.get(ar).setStyleName("Outtime");
		OutTime3.get(ar).setMaxLength(2);
		OutTime3.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!OutTime3.get(ar).getValue().toString().isEmpty())
				{			
					if(Double.parseDouble(OutTime3.get(ar).getValue().toString())>=60)
					{
						OutTime3.get(ar).setValue("");
					}
					else{
						OutTime3.get(ar).setValue(OutTime3.get(ar).getValue().toString());
					}
				}
			}
		});
		OutTime3.get(ar).setEnabled(false);

		OutAmPm.add(ar, new TextField(""));
		OutAmPm.get(ar).setWidth("28px");
		//OutAmPm.get(ar).setHeight("-1px");
		OutAmPm.get(ar).setInputPrompt("PM");
		OutAmPm.get(ar).setImmediate(true);
		OutAmPm.get(ar).setStyleName("timeCombo");
		OutAmPm.get(ar).setMaxLength(2);
		OutAmPm.get(ar).setTextChangeEventMode(TextChangeEventMode.EAGER);
		OutAmPm.get(ar).addListener(new TextChangeListener() {
			public void textChange(TextChangeEvent event)
			{
				if(event.getText().equalsIgnoreCase("A"))
				{
					OutAmPm.get(ar).setValue("AM");
				}
				if(event.getText().equalsIgnoreCase("P"))
				{
					OutAmPm.get(ar).setValue("PM");
				}
			}

		});

		/*amtHour.add(ar, new Label());
		amtHour.get(ar).setWidth("100%");
		amtHour.get(ar).setImmediate(true);
		amtHour.get(ar).setStyleName("amount");*/

		lblPermitBy.add(ar, new Label(""));
		lblPermitBy.get(ar).setWidth("100%");
		lblPermitBy.get(ar).setImmediate(true);
		lblPermitBy.get(ar).setHeight("-1px");
		lblPermitBy.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!lblPermitBy.get(ar).getValue().toString().isEmpty())
				{		
					table.setColumnCollapsed("p", false);
					lblClp.setVisible(true);
					txtColorp.setVisible(true);
					PermitClose.get(ar).setVisible(true);
				}
				else{
					//table.setColumnCollapsed("p", true);
					PermitClose.get(ar).setVisible(false);	
				}
			}
		});

		PermitClose.add(ar, new NativeButton(""));
		PermitClose.get(ar).setWidth("20px");
		PermitClose.get(ar).setImmediate(true);
		PermitClose.get(ar).setVisible(false);
		PermitClose.get(ar).setIcon(new ThemeResource("../icons/cross.png"));
		PermitClose.get(ar).setStyleName(Button.STYLE_LINK);
		PermitClose.get(ar).addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				lblPermitBy.get(ar).setValue("");
			}
		});

		txtReason.add(ar, new TextField(""));
		txtReason.get(ar).setWidth("100%");
		txtReason.get(ar).setImmediate(true);

		lbSecId.add(ar, new Label(""));
		lbSecId.get(ar).setWidth("100%");
		lbSecId.get(ar).setImmediate(true);
		lbSecId.get(ar).setHeight("-1px");

		lbSecName.add(ar, new Label(""));
		lbSecName.get(ar).setWidth("100%");
		lbSecName.get(ar).setImmediate(true);
		lbSecName.get(ar).setHeight("-1px");


		table.addItem(new Object[]{Close.get(ar),lbSl.get(ar),lblempID.get(ar),lbProxID.get(ar),lbEmployeeName.get(ar),
				lbDesignation.get(ar),lbAttendDate.get(ar),InDate.get(ar),InTime1.get(ar),InTime2.get(ar),InTime3.get(ar),
				InAmPm.get(ar),OutDate.get(ar),OutTime1.get(ar),OutTime2.get(ar),OutTime3.get(ar),OutAmPm.get(ar),//amtHour.get(ar),
				lblPermitBy.get(ar),PermitClose.get(ar),txtReason.get(ar),lbSecId.get(ar),lbSecName.get(ar)},ar);
	}

	private void btnIni(boolean t)
	{
		button.btnNew.setEnabled(t);
		button.btnEdit.setEnabled(t);
		button.btnSave.setEnabled(!t);
		button.btnRefresh.setEnabled(!t);
		button.btnDelete.setEnabled(t);
		button.btnFind.setEnabled(t);;
	}

	private void componentIni(boolean b) 
	{
		dDate.setEnabled(!b);
		cmbEmployeeName.setEnabled(!b);
		cmbPermit.setEnabled(!b);
		RadioBtnGroup.setEnabled(!b);

		if(isFind==false)
		{
			cmbDeptName.setEnabled(!b);
		}
		else
		{
			cmbDeptName.setEnabled(b);
		}
		table.setEnabled(!b);
	}

	private void txtClear()
	{
		dDate.setValue(new java.util.Date());
		cmbDeptName.setValue(null);
		cmbPermit.setValue(null);
		cmbEmployeeName.setValue(null);

		for(int i =0; i<lbProxID.size(); i++)
		{
			lblempID.get(i).setValue("");
			lbProxID.get(i).setValue("");
			lbEmployeeName.get(i).setValue("");
			lbDesignation.get(i).setValue("");
			lblPermitBy.get(i).setValue("");
			txtReason.get(i).setValue("");
			//amtHour.get(i).setValue("");
			lblPermitBy.get(i).setValue("");
			InTime1.get(i).setValue("");
			InTime2.get(i).setValue("");
			InTime3.get(i).setValue("");
			InAmPm.get(i).setValue("");
			OutAmPm.get(i).setValue("");
			OutTime1.get(i).setValue("");
			OutTime2.get(i).setValue("");
			OutTime3.get(i).setValue("");
			lbSecId.get(i).setValue("");
			lbSecName.get(i).setValue("");
			lbAttendDate.get(i).setValue("");
			PermitClose.get(i).setVisible(false);
		}
	}

	private void tableclear()
	{
		for(int i =0; i<lbProxID.size(); i++)
		{
			lbProxID.get(i).setValue("");
			lbEmployeeName.get(i).setValue("");
			lbDesignation.get(i).setValue("");
			//amtHour.get(i).setValue("");
			lblPermitBy.get(i).setValue("");
			txtReason.get(i).setValue("");
			InAmPm.get(i).setValue("");
			InTime1.get(i).setValue("");
			InTime2.get(i).setValue("");
			InTime3.get(i).setValue("");
			OutAmPm.get(i).setValue("");
			OutTime1.get(i).setValue("");
			OutTime2.get(i).setValue("");
			OutTime3.get(i).setValue("");
			lbSecId.get(i).setValue("");
			lbSecName.get(i).setValue("");
			lbAttendDate.get(i).setValue("");
			PermitClose.get(i).setVisible(false);
		}
	}

	private void focusEnter()
	{
		allComp.add(dDate);
		allComp.add(cmbDeptName);
		allComp.add(RadioBtnGroup);
		allComp.add(cmbEmployeeName);
		allComp.add(cmbPermit);

		for(int i=0; i<lbEmployeeName.size();i++)
		{
			allComp.add(InTime1.get(i));
			allComp.add(InTime2.get(i));
			allComp.add(InTime3.get(i));
			allComp.add(InAmPm.get(i));
			allComp.add(OutTime1.get(i));
			allComp.add(OutTime2.get(i));
			allComp.add(OutTime3.get(i));
			allComp.add(OutAmPm.get(i));
			allComp.add(txtReason.get(i));
		}

		allComp.add(button.btnSave);

		new FocusMoveByEnter(this,allComp);
	}

	public boolean validTableSelect()
	{
		boolean ret = false;
		for(int i=0; i<lbProxID.size(); i++)
		{
			if(!lbEmployeeName.get(i).getValue().toString().isEmpty() || 
					!lbDesignation.get(i).getValue().toString().isEmpty()|| 
					!InTime1.get(i).getValue().toString().isEmpty() || 
					!InTime2.get(i).getValue().toString().isEmpty() || 
					!InTime3.get(i).getValue().toString().isEmpty() ||
					!InAmPm.get(i).getValue().toString().isEmpty()|| 
					!OutTime1.get(i).getValue().toString().isEmpty() ||
					!OutTime2.get(i).getValue().toString().isEmpty() || 
					!OutTime3.get(i).getValue().toString().isEmpty() || 
					!OutTime3.get(i).getValue().toString().isEmpty() || 
					!OutAmPm.get(i).getValue().toString().isEmpty() || 
					//!amtHour.get(i).getValue().toString().isEmpty() ||
					!txtReason.get(i).getValue().toString().isEmpty())
			{
				ret = true;
				break;
			}
		}
		return ret;
	}

	private void departMentdataAdd()
	{
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			String query = "select AutoID, SectionName from tbSectionInfo";
			List list = session.createSQLQuery(query).list();

			for (Iterator iter = list.iterator(); iter.hasNext();)
			{
				Object[] element =  (Object[]) iter.next();	
				cmbDeptName.addItem(element[0]);
				cmbDeptName.setItemCaption(element[0], element[1].toString());	
			}
			tx.commit();
		}

		catch(Exception ex){
			this.getParent().showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void addCmbEmployeeData(String query)
	{

		cmbEmployeeName.removeAllItems();
		System.out.println(query);

		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx;

		tx = session.beginTransaction();

		List list = session.createSQLQuery(query).list();

		if(!list.isEmpty())
		{
			for(Iterator iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbEmployeeName.addItem(element[0].toString());
				cmbEmployeeName.setItemCaption(element[0], element[1].toString());
			}
		}
		else
			showNotification("Warning", "No Employee Found!!!", Notification.TYPE_WARNING_MESSAGE);
	}

	private void addCmbPermitData()
	{
		String query="";
		cmbPermit.removeAllItems();
		System.out.println(query);

		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx;
		tx = session.beginTransaction();
		query = "select vEmployeeId,vEmployeeName from tbEmployeeInfo order by vEmployeeId";
		List list = session.createSQLQuery(query).list();

		for(Iterator iter = list.iterator(); iter.hasNext();)
		{
			Object[] element = (Object[]) iter.next();

			cmbPermit.addItem(element[0].toString());
			cmbPermit.setItemCaption(element[0], element[1].toString());
		}
	}

	private void updateAction() 
	{
		System.out.println("Update");
		if (!lbProxID.get(0).getValue().toString().isEmpty()) 
		{
			btnIni(false);
			componentIni(false);
		} 
		else
		{
			this.getParent().showNotification("Update Failed","There are no data for update.",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void refreshButtonEvent() 
	{
		componentIni(true);
		btnIni(true);
		txtClear();	
	}

	private void tableDataAdd(String employee )
	{
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			if(chkPresent.booleanValue()){
				//String query =  "SELECT * from  [funDailyAttEdit]('"+DBdateformat.format(dDate.getValue())+"','"+cmbEmployeeName.getValue()+"') ";
				String query="select empID,empCode,empName,designation,ISNULL(CONVERT(date,inTime),'1900-01-01') InDate,ISNULL(DATEPART(HH,inTime),0) InHour,ISNULL(DATEPART(MI,inTime),0) InMin,"+
						"ISNULL(DATEPART(SS,inTime),0) InSec,ISNULL(CONVERT(date,outTimeMax),'1900-01-01') OutDate,ISNULL(DATEPART(HH,outTimeMax),0) OutHour,ISNULL(DATEPART(MI,outTimeMax),0) "+ 
						"OutMin,ISNULL(DATEPART(SS,outTimeMax),0) OutSec from funCalcEmployeeAttendance('"+DBdateformat.format(dDate.getValue())+"'," +
						"'"+DBdateformat.format(dDate.getValue())+"','"+cmbEmployeeName.getValue().toString()+"','"+cmbDeptName.getValue().toString()+"')" +
						" where Date='"+DBdateformat.format(dDate.getValue())+"'";

				List list = session.createSQLQuery(query).list();

				if(!list.isEmpty())
				{

					for(Iterator iter = list.iterator(); iter.hasNext();)
					{				  
						Object[] element = (Object[]) iter.next();

						//int i=0;
						for(int j=0;j<lbProxID.size();j++)
						{
							if(lbProxID.get(j).getValue().toString().isEmpty())
							{
								i=j;
								t=true;
								break;
							}
							if(!lbProxID.get(j).getValue().toString().isEmpty())
							{
								if(lbProxID.get(j).getValue().toString().equals(element[1].toString()))
								{
									t=false;
									break;
								}
							}
						}
						if(t){
							lblempID.get(i).setValue(element[0]);
							lbProxID.get(i).setValue(element[1]);
							lbEmployeeName.get(i).setValue(element[2]);
							lbDesignation.get(i).setValue(element[3]);
							InDate.get(i).setValue(element[4]);

							int hour=Integer.parseInt(element[5].toString());
							if(hour<=12)
							{
								String strhour;
								if(hour==0)
									strhour="12";
								else if(hour<10)
									strhour="0"+Integer.toString(hour);
								else
									strhour=Integer.toString(hour);
								InTime1.get(i).setValue(strhour);
							}
							else
							{
								String strhour;
								if(hour-12<10)
									strhour="0"+Integer.toString(hour-12);
								else
									strhour=Integer.toString(hour-12);
								InTime1.get(i).setValue(strhour);
							}

							int min=Integer.parseInt(element[6].toString());
							String strmin=Integer.toString(min);
							if(min<10)
								strmin="0"+strmin;
							InTime2.get(i).setValue(strmin);

							int sec=Integer.parseInt(element[7].toString());
							String strsec=Integer.toString(sec);
							if(sec<10)
								strsec="0"+strsec;
							InTime3.get(i).setValue(strsec);

							if(Integer.parseInt(element[5].toString())<12)
								InAmPm.get(i).setValue("AM");
							else
								InAmPm.get(i).setValue("PM");

							OutDate.get(i).setValue(element[8]);

							int outhour=Integer.parseInt(element[9].toString());
							if(outhour<=12)
							{
								String strhour;
								if(hour==0)
									strhour="12";
								else
									if(outhour<10)
										strhour="0"+Integer.toString(outhour);
									else
										strhour=Integer.toString(outhour);
								OutTime1.get(i).setValue(strhour);
							}
							else
							{
								String strhour;
								if(outhour-12<10)
									strhour="0"+Integer.toString(outhour-12);
								else
									strhour=Integer.toString(outhour);
								OutTime1.get(i).setValue(strhour);
							}

							int outmin=Integer.parseInt(element[10].toString());
							String stroutmin=Integer.toString(outmin);
							if(outmin<10)
								stroutmin="0"+stroutmin;
							OutTime2.get(i).setValue(stroutmin);

							int outsec=Integer.parseInt(element[11].toString());
							String stroutsec=Integer.toString(outsec);
							if(outsec<10)
								stroutsec="0"+stroutsec;
							OutTime3.get(i).setValue(stroutsec);

							if(Integer.parseInt(element[9].toString())<12)
								OutAmPm.get(i).setValue("AM");
							else
								OutAmPm.get(i).setValue("PM");
							lbSecId.get(i).setValue(cmbDeptName.getValue());
							lbSecName.get(i).setValue(cmbDeptName.getItemCaption(cmbDeptName.getValue()));
							System.out.println(element[9].toString()+" "+element[10].toString()+" "+element[11].toString());
							//lblPermitBy.get(i).setValue(element[11]);
							//txtReason.get(i).setValue(element[12]);
							//lbAttendDate.get(i).setValue(element[13]);
							//lbSecId.get(i).setValue(cmbDeptName.getValue());
							//lbSecName.get(i).setValue(cmbDeptName.getItemCaption(cmbDeptName.getValue()));

							/*if(!InTime1.get(i).getValue().toString().equals("") && 
							!InTime2.get(i).getValue().toString().equals("") && 
							!InTime3.get(i).getValue().toString().equals("") && 
							!InAmPm.get(i).getValue().toString().equals("") && 
							!OutTime1.get(i).getValue().toString().equals("") &&
							!OutTime2.get(i).getValue().toString().equals("") &&
							!OutTime3.get(i).getValue().toString().equals("") &&
							!OutAmPm.get(i).getValue().toString().equals(""))

					{
							String SinTime1 = InTime1.get(i).getValue().toString();
							String SinTime2 = InTime2.get(i).getValue().toString();
							String SinTime3 = InTime1.get(i).getValue().toString();
							String SInAmPm  =  InAmPm.get(i).getValue().toString();

							String SOutTime1 = OutTime1.get(i).getValue().toString();
							String SOutTime2 = OutTime2.get(i).getValue().toString();
							String SOutTime3 = OutTime3.get(i).getValue().toString();
							String SOutAmPm  = OutAmPm.get(i).getValue().toString();

Iterator iterH = session.createSQLQuery(" select SUBSTRING(CONVERT(varchar,(CONVERT(time," +
"(CONVERT(varchar,DATEDIFF(S,cast('"+SinTime1+"'+':'+'"+SinTime2+"'+':'+'"+SinTime3+"'+'"+SInAmPm+"' as time),cast('"+SOutTime1+"'+':'+'"+SOutTime2+"'+':'+'"+SOutTime3+"'+'"+SOutAmPm+"' as time))/3600)+':'+" +
"CONVERT(varchar,DATEDIFF(S,cast('"+SinTime1+"'+':'+'"+SinTime2+"'+':'+'"+SinTime3+"'+'"+SInAmPm+"' as time),cast('"+SOutTime1+"'+':'+'"+SOutTime2+"'+':'+'"+SOutTime3+"'+'"+SOutAmPm+"' as time))%3600/60)+':'+" +
"CONVERT(varchar,DATEDIFF(S,cast('"+SinTime1+"'+':'+'"+SinTime2+"'+':'+'"+SinTime3+"'+'"+SInAmPm+"' as time),cast('"+SOutTime1+"'+':'+'"+SOutTime2+"'+':'+'"+SOutTime3+"'+'"+SOutAmPm+"' as time))%60))))),1,8)").list().iterator();

							amtHour.get(i).setValue(iterH.next().toString());
							}
					else{
						amtHour.get(i).setValue("00:00:00");
					}*/

							if((i)==lbProxID.size()-1)
							{
								tableRowAdd(i+1);
							}
							i++;
						}
						else
						{
							showNotification("Warning","This Employee is already exists!!!",Notification.TYPE_WARNING_MESSAGE);
						}
					}
				}
				else
				{
					tableclear();
					this.getParent().showNotification("Warning!","No employee found", Notification.TYPE_WARNING_MESSAGE);
				}
			}

			if(chkAbsent.booleanValue())
			{
				/*String query =  " select ei.iFingerID,ei.vEmployeeName,(select designationName from tbDesignationInfo" +
						" where designationId=ei.vDesignationId ) from tbEmployeeInfo ei where ei.iFingerID='"+cmbEmployeeName.getValue()+"' ";*/
				String query="select employeeCode,vProximityId,vEmployeeName,(select designationName from tbDesignationInfo where designationId=ei.vDesignationId) designation," +
						" from tbEmployeeInfo ei where ei.vProximityId='"+cmbEmployeeName.getValue().toString()+"'";

				List list = session.createSQLQuery(query).list();

				if(!list.isEmpty())
				{

					for(Iterator iter = list.iterator(); iter.hasNext();)
					{				  
						Object[] element = (Object[]) iter.next();
						//int i=0;
						for(int j=0;j<lbProxID.size();j++)
						{
							if(lbProxID.get(j).getValue().toString().isEmpty()){

								i=j;
								t=true;
								break;
							}
							if(!lbProxID.get(j).getValue().toString().isEmpty())
							{
								if(lbProxID.get(j).getValue().toString().equals(element[1].toString()))
								{
									t=false;
									break;
								}
							}
						}
						if(t){
							lblempID.get(i).setValue(element[0]);
							lbProxID.get(i).setValue(element[1]);
							lbEmployeeName.get(i).setValue(element[2]);
							lbDesignation.get(i).setValue(element[3]);
							lbAttendDate.get(i).setValue(DBdateformat.format(dDate.getValue()));
							lbSecId.get(i).setValue(cmbDeptName.getValue());
							lbSecName.get(i).setValue(cmbDeptName.getItemCaption(cmbDeptName.getValue()));
							if((i)==lbProxID.size()-1)
							{
								tableRowAdd(i+1);
							}
							i++;
						}
						else
						{
							showNotification("Warning","This Employee is already exists!!!",Notification.TYPE_WARNING_MESSAGE);
						}
					}
				}
				else
				{
					tableclear();
					this.getParent().showNotification("Warning!","No employee found", Notification.TYPE_WARNING_MESSAGE);
				}
			}
		}
		catch (Exception ex)
		{
			//this.getParent().showNotification("Khan", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		//cmbEmployeeName.setValue(null);
	}

	private void tablePermitDataAdd(String Permit )
	{
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			String query =  "select 0,ei.vEmployeeName from tbEmployeeInfo ei where ei.vEmployeeId='"+Permit+"' ";

			List list = session.createSQLQuery(query).list();

			if(!list.isEmpty())
			{
				for(Iterator iter = list.iterator(); iter.hasNext();)
				{				  
					Object[] element = (Object[]) iter.next();
					//int i=0;
					for(int j=0;j<lbProxID.size();j++)
					{
						if(lblPermitBy.get(j).getValue().toString().isEmpty()){

							i=j;
							t=true;
							break;
						}
					}
					if(t)
					{
						if(!lblempID.get(i).getValue().toString().isEmpty())
						{
							lblPermitBy.get(i).setValue(element[1]);

							if(i==lbProxID.size()-1)
							{
								tableRowAdd(i+1);
							}
							i++;
						}
						else
							showNotification("Warning","No Employee Name Found in the table!!!",Notification.TYPE_WARNING_MESSAGE);
					}
				}
			}
			else
			{
				tableclear();
				this.getParent().showNotification("Warning!","No employee found", Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch (Exception ex)
		{
			this.getParent().showNotification("Khan", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void newButtonEvent() 
	{
		cmbDeptName.focus();
		componentIni(false);
		btnIni(false);
		txtClear();
	}

	private void saveButtonAction()
	{
		try
		{
			if (sessionBean.isUpdateable())
			{	
				/*if(chkPresent.booleanValue())
				{
					MessageBox mb = new MessageBox(getParent(), "", MessageBox.Icon.QUESTION, "Do you want to Update Product info?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
					mb.show(new EventListener()
					{
						public void buttonClicked(ButtonType buttonType)
						{
							if(buttonType == ButtonType.YES)
							{
								{
									updateData();
									isUpdate = false;
									btnIni(true);
									componentIni(true);
									txtClear();
									button.btnNew.focus();
								}
							}
						}
					});																	
				}
				if(chkAbsent.booleanValue())
				{*/
				MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to save information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
				mb.show(new EventListener()
				{
					public void buttonClicked(ButtonType buttonType)
					{
						if(buttonType == ButtonType.YES)
						{
							Transaction tx;
							Session session = SessionFactoryUtil.getInstance().getCurrentSession();
							tx = session.beginTransaction();
							insertData(tx,session);
						}
					}
				});
				/*}*/
			}
		}
		catch(Exception ex)
		{
			this.getParent().showNotification("Error.", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void insertData (Transaction tx, Session session)
	{
		//System.out.println("Enter into the INSERTDATA METHOD");

		try
		{
			session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String toDate = DBdateformat.format(dDate.getValue());
			String chkquery = "";
			String query = "";
			String query1 = "";
			String query2 = "";
			String query3 = "";
			String pFlag = "Absent";
			/*if(chkAbsent.booleanValue()){*/
			for(int i=0; i<lbProxID.size(); i++)
			{
				if(!lblempID.get(i).getValue().toString().isEmpty())
				{
					if(InDate.get(i).getValue()!=null && !InTime1.get(i).getValue().toString().isEmpty() && !InTime2.get(i).getValue().toString().isEmpty() &&
							!InTime3.get(i).getValue().toString().isEmpty() && !InAmPm.get(i).getValue().toString().isEmpty() && 
							OutDate.get(i).getValue()!=null && !OutTime1.get(i).getValue().toString().isEmpty() && !OutTime2.get(i).getValue().toString().isEmpty() &&
							!OutTime3.get(i).getValue().toString().isEmpty() && !OutAmPm.get(i).getValue().toString().isEmpty() &&
							!lblPermitBy.get(i).getValue().toString().isEmpty() && !txtReason.get(i).getValue().toString().isEmpty())
					{
						//FingerID=lbEmployeeName.get(i).getValue().toString();


						/*chkquery="select Date,empID,empCode,empName,designation,sectionId,Section,Date,inTime,"+
								"outTimeMax from funCalcEmployeeAttendance('"+DBdateformat.format(dDate.getValue())+"','"+DBdateformat.format(dDate.getValue())+"','"+lbProxID.get(i).getValue().toString().trim()+"','"+lbSecId.get(i).getValue().toString().trim()+"')";*/

						String intime=InTime1.get(i).getValue().toString()+":"+InTime2.get(i).getValue().toString()+":"+InTime3.get(i).getValue();

						if(InAmPm.get(i).getValue().toString().equalsIgnoreCase("PM"))
							intime=Integer.toString(Integer.parseInt(InTime1.get(i).getValue().toString())+12)+":"+InTime2.get(i).getValue().toString()+":"+InTime3.get(i).getValue();


						String outtime=OutTime1.get(i).getValue().toString()+":"+OutTime2.get(i).getValue().toString()+":"+OutTime3.get(i).getValue();

						if(OutAmPm.get(i).getValue().toString().equalsIgnoreCase("PM"))
							outtime=Integer.toString(Integer.parseInt(OutTime1.get(i).getValue().toString())+12)+":"+OutTime2.get(i).getValue().toString()+":"+OutTime3.get(i).getValue();

						if(chkPresent.booleanValue())
						{
							pFlag="Present";
						}
						query = "insert into tbUDEmployeeAttendance select Date,empID,empCode,empName,designation,sectionId,Section,Date,inTime,"+
								"outTimeMax,'','',0,'New','"+pFlag+"','"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',GETDATE() from funCalcEmployeeAttendance"+
								"('"+DBdateformat.format(dDate.getValue())+"','"+DBdateformat.format(dDate.getValue())+"','"+lbProxID.get(i).getValue().toString().trim()+"','"+lbSecId.get(i).getValue().toString().trim()+"')";
						System.out.println("query"+query);
						session.createSQLQuery(query).executeUpdate();						

						query1="insert into tbEmployeeAttendanceIn values('"+DBdateformat.format(InDate.get(i).getValue())+"','"+lbProxID.get(i).getValue().toString().trim()+"'," +
								"'"+lbEmployeeName.get(i).getValue().toString()+"','"+lbDesignation.get(i).getValue().toString()+"','"+lbSecId.get(i).getValue().toString()+"'," +
								"'"+lbSecName.get(i).getValue().toString()+"','"+DBdateformat.format(InDate.get(i).getValue())+"','"+DBdateformat.format(InDate.get(i).getValue())+" " +
								intime+"','"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',getdate(),'0')";
						System.out.println("query2"+query1);
						session.createSQLQuery(query1).executeUpdate();

						query2="insert into tbEmployeeAttendanceOut values('"+DBdateformat.format(OutDate.get(i).getValue())+"','"+lbProxID.get(i).getValue().toString().trim()+"'," +
								"'"+lbEmployeeName.get(i).getValue().toString()+"','"+lbDesignation.get(i).getValue().toString()+"','"+lbSecId.get(i).getValue().toString()+"'," +
								"'"+lbSecName.get(i).getValue().toString()+"','"+DBdateformat.format(OutDate.get(i).getValue())+"','"+DBdateformat.format(OutDate.get(i).getValue())+" " +
								outtime+"','"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',getdate(),'0')";
						System.out.println("query2"+query2);
						session.createSQLQuery(query2).executeUpdate();

						query3= "insert into tbUDEmployeeAttendance values('"+DBdateformat.format(dDate.getValue())+"','"+lblempID.get(i).getValue().toString()+"','"+lbProxID.get(i).getValue().toString().trim()+"'," +
								"'"+lbEmployeeName.get(i).getValue().toString()+"','"+lbDesignation.get(i).getValue().toString()+"','"+lbSecId.get(i).getValue().toString()+"','"+lbSecName.get(i).getValue().toString()+"'," +
								"'"+DBdateformat.format(InDate.get(i).getValue())+"','"+DBdateformat.format(InDate.get(i).getValue())+" "+intime+"','"+DBdateformat.format(OutDate.get(i).getValue())+" "+outtime+"','"+lblPermitBy.get(i).getValue().toString()+"','"+txtReason.get(i).getValue().toString()+"'," +
								"'0','Update','"+pFlag+"','"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',getdate())";
						System.out.println("query3"+query3);
						session.createSQLQuery(query3).executeUpdate();

					}
					/*}*/
				}
			}

			tx.commit();
			this.getParent().showNotification("All Information Save Successfully");
			isUpdate=false;
			isFind = false;
			txtClear();
			componentIni(true);
			btnIni(true);
		}
		catch(Exception ex)
		{
			this.getParent().showNotification("Error", ex.toString(), Notification.TYPE_WARNING_MESSAGE);
			tx.rollback();
		}
	}



	/*private void updateData()
	{
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			if(chkPresent.booleanValue()){
				for(int i=0; i<lbProxID.size(); i++)
				{
					if(!lbEmployeeName.get(i).getValue().toString().isEmpty())
					{
						if(!InTime1.get(i).getValue().toString().isEmpty() && !InTime2.get(i).getValue().toString().isEmpty() &&
								!InTime3.get(i).getValue().toString().isEmpty() && !InAmPm.get(i).getValue().toString().isEmpty() && 
								!OutTime1.get(i).getValue().toString().isEmpty() && !OutTime2.get(i).getValue().toString().isEmpty() &&
								!OutTime3.get(i).getValue().toString().isEmpty() && !OutAmPm.get(i).getValue().toString().isEmpty() &&
								!lblPermitBy.get(i).getValue().toString().isEmpty() && !txtReason.get(i).getValue().toString().isEmpty())

						{
							String updateData ="UPDATE tbEmployeeAttendance set" +
									" dAttInTime=(select cast('"+InTime1.get(i).getValue()+"'+':'+'"+InTime2.get(i).getValue()+"'+':'+'"+InTime3.get(i).getValue()+"'+'"+InAmPm.get(i).getValue()+"' as time)), " +
									" dAttOutTime=(select cast('"+OutTime1.get(i).getValue()+"'+':'+'"+OutTime2.get(i).getValue()+"'+':'+'"+OutTime3.get(i).getValue()+"'+'"+OutAmPm.get(i).getValue()+"' as time)), " +
									" dAttDate='"+lbAttendDate.get(i).getValue()+"',"+ 
									" vUserId='"+sessionBean.getUserId()+"', " +
									" vUserIP= '"+sessionBean.getUserIp()+"'," +
									" dEntryTime=CURRENT_TIMESTAMP," +
									" vStatus='Update'," +
									" vPermitBy='"+lblPermitBy.get(i).getValue().toString()+"', " +
									" vReason='"+txtReason.get(i).getValue().toString()+"'" +
									" where vEmployeeId='"+(lbProxID.get(i).getValue().toString())+"' and " +
									"dAttDate='"+DBdateformat.format(dDate.getValue())+"' ";

							System.out.println("UpdateProduct: "+updateData);
							session.createSQLQuery(updateData).executeUpdate();
						}
					}
				}
			}
			tx.commit();
			this.getParent().showNotification("All information update successfully.");
			componentIni(true);
			btnIni(true);
		}
		catch(Exception exp)
		{
			tx.rollback();
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}*/
	private void formValidation()
	{
		if(cmbDeptName.getValue()!=null)
		{
			if(cmbEmployeeName.getValue()!=null)
			{
				if(validTableSelect())
				{
					saveButtonAction();	
				}
				else
				{
					showNotification("Warning!","Provide All Data To Table ",Notification.TYPE_WARNING_MESSAGE);
				}
			}
			else
			{
				getParent().showNotification("Warning,","Select Employee Name.", Notification.TYPE_WARNING_MESSAGE);
				cmbEmployeeName.focus();
			}
		}
		else
		{
			getParent().showNotification("Warning,","Select Section Name.", Notification.TYPE_WARNING_MESSAGE);
			cmbDeptName.focus();
		}
	}

	private AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);

		setWidth("1180px");
		setHeight("560px");

		lblDate = new Label();
		lblDate.setImmediate(true);
		lblDate.setWidth("-1px");
		lblDate.setHeight("-1px");
		lblDate.setValue("Date :");
		mainLayout.addComponent(lblDate, "top:20.0px;left:20.0px;");

		dDate = new PopupDateField();
		dDate.setValue(new java.util.Date());
		dDate.setWidth("110px");
		dDate.setHeight("24px");
		dDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dDate.setDateFormat("dd-MM-yyyy");
		dDate.setInvalidAllowed(false);
		dDate.setImmediate(true);
		mainLayout.addComponent(dDate, "top:18.0px;left:120.0px;");

		lblDeptName = new Label();
		lblDeptName.setImmediate(true);
		lblDeptName.setWidth("-1px");
		lblDeptName.setHeight("-1px");
		lblDeptName.setValue("Section Name :");
		mainLayout.addComponent(lblDeptName, "top:45.0px;left:20.0px;");

		cmbDeptName = new ComboBox();
		cmbDeptName.setImmediate(true);
		cmbDeptName.setWidth("210px");
		cmbDeptName.setHeight("24px");
		cmbDeptName.setImmediate(true);
		cmbDeptName.setNullSelectionAllowed(false);
		mainLayout.addComponent(cmbDeptName, "top:43.0px;left:120.0px;");

		chkPresent = new CheckBox("Present");
		chkPresent.setImmediate(true);
		chkPresent.setWidth("-1");
		chkPresent.setHeight("-1px");

		mainLayout.addComponent(chkPresent, "top:20.0px;left:400.0px;");

		chkAbsent = new CheckBox("Absent");
		chkAbsent.setImmediate(true);
		chkAbsent.setWidth("-1");
		chkAbsent.setHeight("-1px");
		mainLayout.addComponent(chkAbsent, "top:20.0px;left:470.0px;");

		RadioBtnGroup = new OptionGroup("",type2);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		mainLayout.addComponent(RadioBtnGroup, "top:45.0px;left:400.0px;");

		lblEmployee = new Label();
		lblEmployee.setImmediate(true);
		lblEmployee.setWidth("-1px");
		lblEmployee.setHeight("-1px");
		lblEmployee.setValue("Employee Name :");
		lblEmployee.setVisible(true);
		mainLayout.addComponent(lblEmployee, "top:70.0px;left:420.0px;");

		lblFinger = new Label();
		lblFinger.setImmediate(true);
		lblFinger.setWidth("-1px");
		lblFinger.setHeight("-1px");
		lblFinger.setValue("Employee ID :");
		lblFinger.setVisible(false);
		mainLayout.addComponent(lblFinger, "top:70.0px;left:420.0px;");

		lblProx=new Label();
		lblProx.setImmediate(t);
		lblProx.setValue("Proximity ID : ");
		lblProx.setVisible(false);
		mainLayout.addComponent(lblProx, "top:70.0px;left:420.0px;");

		cmbEmployeeName = new ComboBox();
		cmbEmployeeName.setImmediate(true);
		cmbEmployeeName.setWidth("250px");
		cmbEmployeeName.setHeight("-1px");
		mainLayout.addComponent(cmbEmployeeName, "top:68.0px;left:520.0px;");

		mainLayout.addComponent(table, "top:100.0px;left:20.0px;");

		/*txtColor = new TextRead();
		txtColor.setImmediate(true);
		txtColor.setWidth("163px");
		txtColor.setHeight("20px");
		txtColor.setStyleName("txtcolor");
		mainLayout.addComponent(txtColor, "top:101px;left:628px;");*/

		/*lblCl = new Label("<font color='#fff'><b><Strong>In Time<Strong></b></font>");
		lblCl.setImmediate(true);
		lblCl.setWidth("95px");
		lblCl.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblCl, "top:104px;left:685px;");*/

		/*txtColor = new TextRead();
		txtColor.setImmediate(true);
		txtColor.setWidth("163px");
		txtColor.setHeight("20px");
		txtColor.setStyleName("txtcolor");
		mainLayout.addComponent(txtColor, "top:101px;left:915px;");*/

		/*lblCl = new Label("<font color='#fff'><b><Strong>Out Time<Strong></b></font>");
		lblCl.setImmediate(true);
		lblCl.setWidth("60px");
		lblCl.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblCl, "top:104px;left:945px;");*/

		lblCl = new Label("<font color='#A00324'><b><Strong>Use 12-Hour Format<Strong></b></font>");
		lblCl.setImmediate(true);
		lblCl.setWidth("-1px");
		lblCl.setHeight("-1px");
		lblCl.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblCl, "top:80px;left:1000px;");

		/*txtColorp = new TextRead();
		txtColorp.setImmediate(true);
		txtColorp.setWidth("164px");
		txtColorp.setHeight("20px");
		txtColorp.setVisible(false);
		txtColorp.setStyleName("txtcolor");
		mainLayout.addComponent(txtColorp, "top:101px;left:834px;");

		lblClp = new Label("<font color='#fff'><b><Strong>Permitted By<Strong></b></font>");
		lblClp.setImmediate(true);
		lblClp.setWidth("95px");
		lblClp.setVisible(false);
		lblClp.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblClp, "top:104px;left:864px;");*/

		lblPermitted = new Label();
		lblPermitted.setImmediate(true);
		lblPermitted.setWidth("-1px");
		lblPermitted.setHeight("-1px");
		lblPermitted.setValue("Permitted By:");
		lblPermitted.setVisible(true);
		mainLayout.addComponent(lblPermitted, "top:70.0px;left:20.0px;");

		cmbPermit = new ComboBox();
		cmbPermit.setImmediate(true);
		cmbPermit.setWidth("250px");
		cmbPermit.setHeight("-1px");
		mainLayout.addComponent(cmbPermit, "top:68.0px;left:120.0px;");

		mainLayout.addComponent(button, "top:530.0px;left:420.0px;");
		
		return mainLayout;
	}
}
