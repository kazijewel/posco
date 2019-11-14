package com.appform.hrmModule;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.AmountField;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.TimeField;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

public class EditMonthlySalarySam extends Window {

	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;
	private Table table= new Table();

	//----------------------------------<Form Varriables>--------------------------------------------

	private Label lblHead= new Label();
	private ComboBox cmbSection;
	private ComboBox cmbEmployee;

	private TextRead txtColor;
	private TextRead txtDesignation  = new TextRead();
	private TextRead txtName         = new TextRead();
	private TextRead txtTotalDays    = new TextRead();
	private TextRead txtFriDays      = new TextRead();
	private TextRead txtHoliDays     = new TextRead();
	private TextRead txtWorkDays     = new TextRead();
	private TextRead txtAbsentAmt     = new TextRead();
	private TextRead txtBasic         = new TextRead();
	private TextRead txtHouseRent     = new TextRead();
	private TextRead txtOtherAll      = new TextRead();
	private TextRead txtOtAmount      = new TextRead();
	private TextRead txtFestivalBonus = new TextRead();
	private TextRead txtRevenueStamp   = new TextRead();

	private AmountField txtPresentDays   = new AmountField();
	private AmountField txtLeaveDays  = new AmountField();
	private AmountField txtTourDays   = new AmountField();
	private AmountField txtOffDays    = new AmountField();
	private AmountField txtAbsentDays = new AmountField();
	private AmountField txtLoanDeduction  = new AmountField();
	private AmountField txtAdvanceSal  = new AmountField();
	private AmountField txtInsurance  = new AmountField();
	private AmountField txtIncomeTax  = new AmountField();

	private TimeField txtOtHour        = new TimeField();
	private TimeField txtOtMinute      = new TimeField();
	private TimeField txtOtSec         = new TimeField();

	private PopupDateField dWorkingDate ;
	private OptionGroup RadioEmployee;

	private boolean isSave=false;
	private boolean isRefresh=false;
	private boolean isUpdate=false;
	private boolean isFind= false;

	int TD,FD,HD,LD,TrD,AD,totalPresent = 0;
	String AttendAD="";
	String AttendLD="";
	String AttendTourD="";
	String AttendHH="";
	String AttendMM="";
	String AttendSS="";

	private ArrayList <Label> lblLoantranId      = new ArrayList <Label>();
	private ArrayList <Label> lblLoanNo          = new ArrayList <Label>();
	private ArrayList <Label> lblOldLoanAmt      = new ArrayList <Label>();
	private ArrayList <Label> lblTempWorkingDays = new ArrayList <Label>();
	private ArrayList <Label> lblEmployeeId      = new ArrayList<Label>();
	ArrayList<Component> allComp = new ArrayList<Component>();

	CommonButton button = new CommonButton("New", "Save", "Edit", "","Refresh","","","","","Exit");
	private static final List<String> Optiontype=Arrays.asList(new String[]
			{"Employee ID","Proximity ID","Finger ID","Employee Name"});

	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
	public DecimalFormat df = new DecimalFormat("#0.00"); 

	public EditMonthlySalarySam(SessionBean sessionBean) 
	{
		this.sessionBean=sessionBean;
		this.setResizable(false);
		this.setCaption("MONTHLY SALARY EDIT :: " + sessionBean.getCompany());

		buildMainLayout();
		setContent(mainLayout);
		buttonAction();
		componentIniRefresh(true);
		btnIni(true);
		focusMove();
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

	//	----------------------------------------------------Button Initialiazation-----------------------------------------------------------

	private void buttonAction()
	{
		dWorkingDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(dWorkingDate.getValue()!=null)
				{
					cmbSection.removeAllItems();
					departMentdataAdd();
				}
			}
		});

		button.btnNew.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isSave=false;
				isRefresh=false;
				isUpdate = false;
				isFind = false;
				newButtonEvent();
			}
		});

		button.btnEdit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate = true;
				componentIni(true);
			}
		});

		cmbSection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbSection.getValue()!=null)
				{
					RadioEmployee.select(null);
					cmbEmployee.removeAllItems();
					RadioEmployee.select("Finger ID");
				}
			}
		});

		txtAbsentDays.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtAbsentDays.getValue().toString().isEmpty())
				{
					if(isUpdate)
					{
						if(txtAbsentDays.getValue().toString().equals(AttendAD))
						{
							CheckPresent();
						}
						else
						{
							txtAbsentDays.setValue("");
							showNotification("Warning", "You Should Edit Employee Attendance First!!!", Notification.TYPE_WARNING_MESSAGE);
							txtAbsentDays.setValue(AttendAD);
						}
					}
				}

			}
		});

		txtOtHour.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtOtHour.getValue().toString().isEmpty())
				{
					if(isUpdate)
					{
						if(txtOtHour.getValue().toString().equals(AttendHH))
						{
							double OtAmount=0.0;
							OtAmount=(((Double.parseDouble(txtBasic.getValue().toString())/
									Double.parseDouble(txtTotalDays.getValue().toString()))/8)*2)*
									((Integer.parseInt(txtOtHour.getValue().toString())*3600)+
									(Integer.parseInt(txtOtMinute.getValue().toString())*18)+
									Integer.parseInt(txtOtSec.getValue().toString()))/3600;
							        txtOtAmount.setValue(OtAmount);
						}
						
						else
						{
							txtOtHour.setValue("");
							showNotification("Warning", "You Should Edit Employee Attendance First!!!", Notification.TYPE_WARNING_MESSAGE);
							txtOtHour.setValue(AttendHH);
						}
					}
				}

			}
		});

		txtOtMinute.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtOtMinute.getValue().toString().isEmpty())
				{
					if(isUpdate)
					{
						if(txtOtMinute.getValue().toString().equals(AttendMM))
						{
							double OtAmount=0.0;
							
							OtAmount=(((Double.parseDouble(txtBasic.getValue().toString())/
									    Double.parseDouble(txtTotalDays.getValue().toString()))/8)*2)*
									 ((Integer.parseInt(txtOtHour.getValue().toString())*3600)+
									  (Integer.parseInt(txtOtMinute.getValue().toString())*18)+
									   Integer.parseInt(txtOtSec.getValue().toString()))/3600;
							
							txtOtAmount.setValue(OtAmount);
						}
						else
						{
							txtOtMinute.setValue("");
							showNotification("Warning", "You Should Edit Employee Attendance First!!!", Notification.TYPE_WARNING_MESSAGE);
							txtOtMinute.setValue(AttendMM);
						}
					}
				}

			}
		});

		txtOtSec.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtOtSec.getValue().toString().isEmpty())
				{

					if(isUpdate)
					{
						if(txtOtSec.getValue().toString().equals(AttendSS))
						{
							double OtAmount=0.0;
							OtAmount=(((Double.parseDouble(txtBasic.getValue().toString())/
									Double.parseDouble(txtTotalDays.getValue().toString()))/8)*2)*
									((Integer.parseInt(txtOtHour.getValue().toString())*3600)+
									(Integer.parseInt(txtOtMinute.getValue().toString())*18)+
									Integer.parseInt(txtOtSec.getValue().toString()))/3600;
							txtOtAmount.setValue(OtAmount);
						}
						else
						{
							txtOtSec.setValue("");
							showNotification("Warning", "You Should Edit Employee Attendance First!!!", Notification.TYPE_WARNING_MESSAGE);
							txtOtSec.setValue(AttendSS);
						}
					}

				}

			}
		});

		txtLeaveDays.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtLeaveDays.getValue().toString().isEmpty())
				{
					if(isUpdate)
					{
						if(txtLeaveDays.getValue().toString().equals(AttendLD))
						{
							CheckPresent();
						}
						else
						{
							txtLeaveDays.setValue("");
							showNotification("Warning", "You Should Edit Employee Attendance First!!!", Notification.TYPE_WARNING_MESSAGE);
							txtLeaveDays.setValue(AttendLD);
						}
					}
				}

			}
		});

		txtTourDays.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtTourDays.getValue().toString().isEmpty())
				{
					if(isUpdate)
					{
						if(txtTourDays.getValue().toString().equals(AttendTourD))
						{
							CheckPresent();
						}
						else
						{
							txtTourDays.setValue("");
							showNotification("Warning", "You Should Edit Employee Attendance First!!!", Notification.TYPE_WARNING_MESSAGE);
							txtTourDays.setValue(AttendTourD);
						}
					}
				}

			}
		});


		RadioEmployee.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbSection.getValue()!=null)
				{
					String sql="";

					if(RadioEmployee.getValue()=="Employee ID")
					{
						sql="select iFingerID,employeeCode from tbEmployeeInfo ei inner join tbMonthlySalaryDetails ms on ei.employeeCode=ms.vEmployeeId where ms.vSectionId='"+cmbSection.getValue().toString()+"'  and iStatus='1'";
					}

					else if(RadioEmployee.getValue()=="Proximity ID")
					{
						sql="select iFingerID,vProximityId from tbEmployeeInfo ei inner join tbMonthlySalaryDetails ms on ei.employeeCode=ms.vEmployeeId where ms.vSectionId='"+cmbSection.getValue().toString()+"'  and iStatus='1'";
					}

					else if(RadioEmployee.getValue()=="Finger ID")
					{
						sql="select iFingerID,iFingerID from tbEmployeeInfo ei inner join tbMonthlySalaryDetails ms on ei.employeeCode=ms.vEmployeeId where ms.vSectionId='"+cmbSection.getValue().toString()+"'  and iStatus='1'";
					}

					else if(RadioEmployee.getValue()=="Employee Name")
					{
						sql="select iFingerID,vEmployeeName from tbEmployeeInfo ei inner join tbMonthlySalaryDetails ms on ei.employeeCode=ms.vEmployeeId where ms.vSectionId='"+cmbSection.getValue().toString()+"'  and iStatus='1'";
					}

					if(!sql.equals(""))
					{
						Transaction tx=null;
						try
						{
							Session session=SessionFactoryUtil.getInstance().getCurrentSession();
							tx=session.beginTransaction();
							List lst=session.createSQLQuery(sql).list();

							if(!lst.isEmpty())
							{
								Iterator itr=lst.iterator();
								while(itr.hasNext())
								{
									Object[] element=(Object[])itr.next();
									cmbEmployee.addItem(element[0]);
									cmbEmployee.setItemCaption(element[0], element[1].toString());
								}
							}

							else
								showNotification("Warning", "No Employee Found!!!", Notification.TYPE_WARNING_MESSAGE);
						}

						catch(Exception exp)
						{
							showNotification("RadioBtnGroup", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
						}
					}
				}

				else
				{
					if(!isSave && !isRefresh)
					{
						RadioEmployee.select(null);
						showNotification("Warning", "Please Select Section Name!!!", Notification.TYPE_WARNING_MESSAGE);
					}
				}
			}
		});

		cmbEmployee.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbEmployee.getValue()!=null)
				{
					SetMonthlySalary();
					SetAttendanceDetails();
					//btnIni(false);
				}
			}
		});

		button.btnRefresh.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate =false;
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

	//-----------------------------------------------------------------------------------------------------------------------------


	private AbsoluteLayout buildMainLayout() 
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);
		mainLayout.setWidth("800px");
		mainLayout.setHeight("470px");

		dWorkingDate = new PopupDateField();
		dWorkingDate.setImmediate(true);
		dWorkingDate.setWidth("140px");
		dWorkingDate.setDateFormat("MMMMM-yyyy");
		dWorkingDate.setValue(new java.util.Date());
		dWorkingDate.setResolution(PopupDateField.RESOLUTION_MONTH);
		mainLayout.addComponent(new Label("Date : "), "top:20.0px;left:30.0px;");
		mainLayout.addComponent(dWorkingDate, "top:18.0px; left:120.0px;");

		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("220px");
		cmbSection.setHeight("24px");
		cmbSection.setNullSelectionAllowed(true);
		cmbSection.setNewItemsAllowed(false);
		cmbSection.setInputPrompt("Section Name");
		mainLayout.addComponent(new Label("Section Name : "), "top:20.0px;left:300.0px;");
		mainLayout.addComponent(cmbSection, "top:18.0px; left:400.0px;");

		RadioEmployee=new OptionGroup("",Optiontype);
		RadioEmployee.setImmediate(true);
		RadioEmployee.setStyleName("horizontal");
		mainLayout.addComponent(RadioEmployee, "top:45.0px;left:25.0px;");

		cmbEmployee = new ComboBox();
		cmbEmployee.setImmediate(true);
		cmbEmployee.setWidth("220px");
		cmbEmployee.setHeight("24px");
		cmbEmployee.setNullSelectionAllowed(true);
		cmbEmployee.setInputPrompt("Employee Name");
		mainLayout.addComponent(cmbEmployee, "top:43.0px; left:400.0px;");

		lblHead = new Label("<font color='#0000f0'><b><Strong>Attendance Details<Strong></b></font>");
		lblHead.setImmediate(true);
		lblHead.setWidth("-1px");
		lblHead.setHeight("-1px");
		lblHead.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblHead, "top:120px;left:300px;");

		txtTotalDays = new TextRead(1);
		txtTotalDays.setImmediate(true);
		txtTotalDays.setWidth("30px");
		txtTotalDays.setHeight("17px");
		mainLayout.addComponent(new Label("Total Days : "), "top:145.0px;left:20.0px;");
		mainLayout.addComponent(txtTotalDays, "top:146.0px; left:115.0px;");

		txtFriDays = new TextRead(1);
		txtFriDays.setImmediate(true);
		txtFriDays.setWidth("30px");
		txtFriDays.setHeight("17px");
		mainLayout.addComponent(new Label("Total Fridays : "), "top:145.0px;left:160.0px;");
		mainLayout.addComponent(txtFriDays, "top:146.0px; left:280.0px;");

		txtHoliDays = new TextRead(1);
		txtHoliDays.setImmediate(true);
		txtHoliDays.setWidth("28px");
		txtHoliDays.setHeight("17px");
		mainLayout.addComponent(new Label("Total Holidays : "), "top:170.0px;left:20.0px;");
		mainLayout.addComponent(txtHoliDays, "top:171.0px; left:115.0px;");

		txtWorkDays = new TextRead(1);
		txtWorkDays.setImmediate(true);
		txtWorkDays.setWidth("28px");
		txtWorkDays.setHeight("17px");
		mainLayout.addComponent(new Label("Total Working Days : "), "top:170.0px;left:160.0px;");
		mainLayout.addComponent(txtWorkDays, "top:171.0px; left:280.0px;");

		txtLeaveDays = new AmountField();
		txtLeaveDays.setImmediate(true);
		txtLeaveDays.setWidth("29px");
		txtLeaveDays.setHeight("17px");
		txtLeaveDays.setMaxLength(2);
		mainLayout.addComponent(new Label("Total Leave days : "), "top:145.0px;left:320.0px;");
		mainLayout.addComponent(txtLeaveDays, "top:146.0px; left:430.0px;");

		txtTourDays = new AmountField();
		txtTourDays.setImmediate(true);
		txtTourDays.setWidth("29px");
		txtTourDays.setHeight("17px");
		txtTourDays.setMaxLength(2);
		mainLayout.addComponent(new Label("Total Tour Days : "), "top:170.0px;left:320.0px;");
		mainLayout.addComponent(txtTourDays, "top:171.0px; left:430.0px;");

		/*txtOffDays = new AmountField();
		txtOffDays.setImmediate(true);
		txtOffDays.setWidth("28px");
		txtOffDays.setHeight("15px");
		txtOffDays.setStyleName("Intime");
		mainLayout.addComponent(new Label("Total Off Days : "), "top:145.0px;left:160.0px;");
		mainLayout.addComponent(txtOffDays, "top:146.0px; left:280.0px;");*/

		txtPresentDays = new AmountField();
		txtPresentDays.setImmediate(true);
		txtPresentDays.setWidth("28px");
		txtPresentDays.setHeight("17px");
		mainLayout.addComponent(new Label("Total Present days : "), "top:170.0px;left:480.0px;");
		mainLayout.addComponent(txtPresentDays, "top:171.0px; left:600.0px;");

		txtAbsentDays = new AmountField();
		txtAbsentDays.setImmediate(true);
		txtAbsentDays.setWidth("29px");
		txtAbsentDays.setHeight("17px");
		txtAbsentDays.setMaxLength(2);
		mainLayout.addComponent(new Label("Total Absent Days : "), "top:145.0px;left:480.0px;");
		mainLayout.addComponent(txtAbsentDays, "top:146.0px; left:600.0px;");

		lblHead = new Label("<font color='#0000f0'><b><Strong>Earning Details<Strong></b></font>");
		lblHead.setImmediate(true);
		lblHead.setWidth("-1px");
		lblHead.setHeight("-1px");
		lblHead.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblHead, "top:220px;left:90.0px;");

		txtBasic = new TextRead(1);
		txtBasic.setImmediate(true);
		txtBasic.setWidth("120px");
		txtBasic.setHeight("22px");
		mainLayout.addComponent(new Label("Basic :"), "top:245.0px;left:20.0px;");
		mainLayout.addComponent(txtBasic, "top:243.0px; left:130.0px;");

		txtHouseRent = new TextRead(1);
		txtHouseRent.setImmediate(true);
		txtHouseRent.setWidth("120px");
		txtHouseRent.setHeight("22px");
		mainLayout.addComponent(new Label("House Rent :"), "top:270.0px;left:20.0px;");
		mainLayout.addComponent(txtHouseRent, "top:268.0px; left:130.0px;");

		txtOtherAll = new TextRead(1);
		txtOtherAll.setImmediate(true);
		txtOtherAll.setWidth("120px");
		txtOtherAll.setHeight("22px");
		mainLayout.addComponent(new Label("Other Allowance :"), "top:295.0px;left:20.0px;");
		mainLayout.addComponent(txtOtherAll, "top:293.0px; left:130.0px;");

		txtFestivalBonus = new TextRead(1);
		txtFestivalBonus.setImmediate(true);
		txtFestivalBonus.setWidth("120px");
		txtFestivalBonus.setHeight("22px");
		mainLayout.addComponent(new Label("Festival Bonus :"), "top:320.0px;left:20.0px;");
		mainLayout.addComponent(txtFestivalBonus, "top:318.0px; left:130.0px;");

		txtOtHour = new TimeField();
		txtOtHour.setImmediate(true);
		txtOtHour.setWidth("35px");
		txtOtHour.setHeight("17px");                                    
		txtOtHour.setStyleName("time");
		txtOtHour.setInputPrompt("HH");
		mainLayout.addComponent(new Label("Total OT :"), "top:345.0px;left:20.0px;");
		mainLayout.addComponent(txtOtHour, "top:343.0px;left:131.0px;");
		mainLayout.addComponent(new Label("<b><Strong>:<Strong></b>",Label.CONTENT_XHTML), "top:344.0px;left:166.0px;");

		txtOtMinute = new TimeField();
		txtOtMinute.setImmediate(true);
		txtOtMinute.setWidth("35px");
		txtOtMinute.setHeight("17px");
		txtOtMinute.setStyleName("time");
		txtOtMinute.setInputPrompt("MM");
		mainLayout.addComponent(txtOtMinute, "top:343.0px;left:171.0px;");
		mainLayout.addComponent(new Label("<b><Strong>:<Strong></b>",Label.CONTENT_XHTML), "top:344.0px;left:206.0px;");

		txtOtSec = new TimeField();
		txtOtSec.setImmediate(true);
		txtOtSec.setWidth("35px");
		txtOtSec.setHeight("17px");
		txtOtSec.setStyleName("time");
		txtOtSec.setInputPrompt("SS");
		mainLayout.addComponent(txtOtSec, "top:343.0px;left:211.0px;");

		txtOtAmount = new TextRead(1);
		txtOtAmount.setImmediate(true);
		txtOtAmount.setWidth("120px");
		txtOtAmount.setHeight("22px");
		mainLayout.addComponent(new Label("Total OT Amount :"), "top:367.0px;left:20.0px;");
		mainLayout.addComponent(txtOtAmount, "top:365.0px; left:130.0px;");

		lblHead = new Label("<font color='#0000f0'><b><Strong>Deduction Details<Strong></b></font>");
		lblHead.setImmediate(true);
		lblHead.setWidth("-1px");
		lblHead.setHeight("-1px");
		lblHead.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblHead, "top:220px;left:370.0px;");

		txtAbsentAmt = new TextRead(1);
		txtAbsentAmt.setImmediate(true);
		txtAbsentAmt.setWidth("120px");
		txtAbsentAmt.setHeight("22px");
		mainLayout.addComponent(new Label("Absent Amount :"), "top:245.0px;left:300.0px;");
		mainLayout.addComponent(txtAbsentAmt, "top:243.0px; left:430.0px;");

		txtLoanDeduction = new AmountField();
		txtLoanDeduction.setImmediate(true);
		txtLoanDeduction.setWidth("120px");
		txtLoanDeduction.setHeight("22px");
		mainLayout.addComponent(new Label("Loan Amount :"), "top:270.0px;left:300.0px;");
		mainLayout.addComponent(txtLoanDeduction, "top:268.0px; left:430.0px;");

		txtAdvanceSal= new AmountField();
		txtAdvanceSal.setImmediate(true);
		txtAdvanceSal.setWidth("120px");
		txtAdvanceSal.setHeight("22px");
		mainLayout.addComponent(new Label("Advance Salary :"), "top:295.0px;left:300.0px;");
		mainLayout.addComponent(txtAdvanceSal, "top:293.0px; left:430.0px;");

		txtInsurance = new AmountField();
		txtInsurance.setImmediate(true);
		txtInsurance.setWidth("120px");
		txtInsurance.setHeight("22px");
		mainLayout.addComponent(new Label("Insurance :"), "top:320.0px;left:300.0px;");
		mainLayout.addComponent(txtInsurance, "top:318.0px; left:430.0px;");

		txtIncomeTax = new AmountField();
		txtIncomeTax.setImmediate(true);
		txtIncomeTax.setWidth("120px");
		txtIncomeTax.setHeight("22px");
		mainLayout.addComponent(new Label("Income Tax :"), "top:345.0px;left:300.0px;");
		mainLayout.addComponent(txtIncomeTax, "top:343.0px; left:430.0px;");

		txtRevenueStamp = new TextRead(1);
		txtRevenueStamp.setImmediate(true);
		txtRevenueStamp.setWidth("120px");
		txtRevenueStamp.setHeight("22px");
		mainLayout.addComponent(new Label("Revenue Stamp :"), "top:370.0px;left:300.0px;");
		mainLayout.addComponent(txtRevenueStamp, "top:368.0px; left:430.0px;");

		mainLayout.addComponent(button,"top:430.0px; left:210.0px");
		return mainLayout;
	}


	//-----------------------------------------------------All Methods-------------------------------------------------------------

	private void newButtonEvent() 
	{
		dWorkingDate.focus();
		componentIni(false);
		btnIni(false);
		txtClear();
	}

	private void departMentdataAdd()
	{
		try
		{
			cmbSection.removeAllItems();
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			List list=session.createSQLQuery("select Distinct ms.vSectionId,si.SectionName from " +
					" tbMonthlySalaryDetails ms inner join tbSectionInfo si on ms.vSectionId=si.AutoID where " +
					" MONTH(dSalaryOfMonth)=MONTH('"+dFormat.format(dWorkingDate.getValue())+"') and " +
					" Year(dSalaryOfMonth)=Year('"+dFormat.format(dWorkingDate.getValue())+"') ").list();

			for (Iterator iter = list.iterator(); iter.hasNext();)
			{
				Object[] element =  (Object[]) iter.next();	
				cmbSection.addItem(element[0]);
				cmbSection.setItemCaption(element[0], element[1].toString());	
			}
			tx.commit();
		}

		catch(Exception ex){
			this.getParent().showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void SetMonthlySalary()
	{
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery("select iTotalDays,iNoOfFridays,iNoOfHolidays,iLeaveDay,iTourDay," +
					"iOffDay,iNoOfWorkingDays,iAbsentDay,mBasic,mHouseRent,mOtherAllowance,mFestivalBonusAmt," +
					"vOtHour,mOtAmount,mAbsentAmount,mAdvanceSalary,mLoanDeduction,mRevenueStamp,'0.00','0.00','0.00' from " +
					"tbMonthlySalaryDetails where MONTH(dSalaryOfMonth)=MONTH('"+dFormat.format(dWorkingDate.getValue())+"') and year(dSalaryOfMonth)" +
					"=year('"+dFormat.format(dWorkingDate.getValue())+"') and vSectionId='"+cmbSection.getValue()+"' " +
					"and vEmployeeId='"+cmbEmployee.getValue()+"'").list();

			Iterator iter=list.iterator();

			int i = 0;
			for(; iter.hasNext();)
			{
				int Present=0;
				Object[] element = (Object[]) iter.next();

				txtTotalDays.setValue(element[0]);
				txtFriDays.setValue(element[1]);
				txtHoliDays.setValue(element[2]);
				txtWorkDays.setValue(element[5]);
				txtLeaveDays.setValue(element[3]);
				txtTourDays.setValue(element[4]);
				txtAbsentDays.setValue(element[7]);
				txtBasic.setValue(df.format(Double.parseDouble(element[8].toString())));
				txtHouseRent.setValue(df.format(Double.parseDouble(element[9].toString())));
				txtOtherAll.setValue(df.format(Double.parseDouble(element[10].toString())));
				txtFestivalBonus.setValue(df.format(Double.parseDouble(element[11].toString())));
				txtOtAmount.setValue(df.format(Double.parseDouble(element[13].toString())));
				txtAbsentAmt.setValue(df.format(Double.parseDouble(element[14].toString())));
				//	txt.setValue(element[15]);
				txtLoanDeduction.setValue(df.format(Double.parseDouble(element[16].toString())));
				txtRevenueStamp.setValue(df.format(Double.parseDouble(element[17].toString())));
				txtAdvanceSal.setValue(df.format(Double.parseDouble(element[18].toString())));
				txtIncomeTax.setValue(df.format(Double.parseDouble(element[19].toString())));
				txtInsurance.setValue(df.format(Double.parseDouble(element[20].toString())));

				TD  = Integer.parseInt(element[0].toString());
				FD  = Integer.parseInt(element[1].toString());
				HD  = Integer.parseInt(element[2].toString());
				LD  = Integer.parseInt(element[3].toString());
				TrD = Integer.parseInt(element[4].toString());
				AD  = Integer.parseInt(element[7].toString());

				/*		System.out.println(TD);
				System.out.println(FD);
				System.out.println(HD);
				System.out.println(LD);
				System.out.println(TrD);
				System.out.println(AD);*/

				Present=TD-(FD+HD+LD+TrD+AD);
				txtPresentDays.setValue(Present);
				//System.out.println(Present);

				String s=element[12].toString();
				StringTokenizer st;
				st=new StringTokenizer(s,":");

				String HH=st.nextToken().toString();
				String MM=st.nextToken().toString();
				String SS=st.nextToken().toString();

				if(HH.length()<2){ txtOtHour.setValue("0"+HH); }  else { txtOtHour.setValue(HH); }
				if(MM.length()<2){ txtOtMinute.setValue("0"+MM);} else { txtOtMinute.setValue(MM); }
				if(SS.length()<2){ txtOtSec.setValue("0"+SS);} else { txtOtSec.setValue(SS); }
			}

		}

		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void SetAttendanceDetails()
	{
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery(" SELECT SUM(ad) ad,(SUM(sl)+SUM(cl)+SUM(ml)+SUM(al)) ld,0,overTimeHH," +
					"overTimeMM,overTimeSS FROM funMonthlyAttAllEmp ('"+dFormat.format(dWorkingDate.getValue())+"','"+cmbSection.getValue()+"','"+cmbEmployee.getValue()+"') group by overTimeHH,overTimeMM,overTimeSS").list();

			Iterator iter=list.iterator();

			int i = 0;
			for(; iter.hasNext();)
			{
				int Present=0;
				Object[] element = (Object[]) iter.next();

				AttendAD    = element[0].toString();
				AttendLD    = element[1].toString();
				AttendTourD = element[2].toString();
				AttendHH    = element[3].toString();
				AttendMM    = element[4].toString();
				AttendSS    = element[5].toString();
			}

		}

		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public int checkAbsent()
	{
		int TotalAbsent = 0;
		Transaction tx = null;

		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String query = "SELECT SUM(ad) FROM funMonthlyAttend ('"+dFormat.format(dWorkingDate.getValue())+"','"+cmbEmployee.getValue()+"')";

			Iterator iter = (Iterator) session.createSQLQuery(query);

			TotalAbsent = (Integer) iter.next();
		} 

		catch (Exception ex) 
		{
			System.out.print(ex);
		}

		return TotalAbsent;
	}

	private int checkLeave()
	{
		int TotalLeave = 0;
		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String query = "SELECT (SUM(sl)+SUM(cl)+SUM(ml)+SUM(al)) as ld FROM funMonthlyAttend('"+dFormat.format(dWorkingDate.getValue())+"','"+cmbEmployee.getValue()+"')";

			Iterator iter = session.createSQLQuery(query).list().iterator();

			if (iter.hasNext()) 
			{
				TotalLeave = (Integer) iter.next();
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}

		return TotalLeave;
	}

	public void focusMove()
	{
		allComp.add(dWorkingDate);
		allComp.add(cmbSection);
		allComp.add(cmbEmployee);
		allComp.add(txtLeaveDays);
		allComp.add(txtAbsentDays);
		allComp.add(txtTourDays);
		allComp.add(button.btnSave);
		allComp.add(dWorkingDate);

		new FocusMoveByEnter(this,allComp);
	}

	private void btnIni(boolean t)
	{
		button.btnNew.setEnabled(t);
		button.btnEdit.setEnabled(!t);
		button.btnSave.setEnabled(t);
		button.btnRefresh.setEnabled(!t);
		button.btnDelete.setEnabled(t);
		button.btnFind.setEnabled(t);;
	}

	private void componentIni(boolean b) 
	{
		dWorkingDate.setEnabled(!b);
		cmbSection.setEnabled(!b);
		RadioEmployee.setEnabled(!b);
		cmbEmployee.setEnabled(!b);
		txtTotalDays.setEnabled(b);
		txtTourDays.setEnabled(b);
		txtFriDays.setEnabled(b);
		txtFestivalBonus.setEnabled(b);
		txtLeaveDays.setEnabled(b);
		txtLoanDeduction.setEnabled(b);
		txtWorkDays.setEnabled(b);
		txtAbsentAmt.setEnabled(b);
		txtAbsentDays.setEnabled(b);
		txtAdvanceSal.setEnabled(b);
		txtPresentDays.setEnabled(b);
		txtHoliDays.setEnabled(b);
		txtHouseRent.setEnabled(b);
		txtOffDays.setEnabled(b);
		txtOtAmount.setEnabled(b);
		txtOtherAll.setEnabled(b);
		txtOtHour.setEnabled(b);
		txtOtMinute.setEnabled(b);
		txtOtSec.setEnabled(b);
		txtBasic.setEnabled(b);
		txtInsurance.setEnabled(b);
		txtIncomeTax.setEnabled(b);
		txtRevenueStamp.setEnabled(b);
	}

	private void componentIniEdi(boolean b) 
	{
		dWorkingDate.setEnabled(b);
		cmbSection.setEnabled(b);
		RadioEmployee.setEnabled(b);
		cmbEmployee.setEnabled(b);
		txtTotalDays.setEnabled(!b);
		txtTourDays.setEnabled(!b);
		txtFriDays.setEnabled(!b);
		txtFestivalBonus.setEnabled(!b);
		txtLeaveDays.setEnabled(!b);
		txtLoanDeduction.setEnabled(!b);
		txtWorkDays.setEnabled(!b);
		txtAbsentAmt.setEnabled(!b);
		txtAbsentDays.setEnabled(!b);
		txtAdvanceSal.setEnabled(!b);
		txtPresentDays.setEnabled(!b);
		txtHoliDays.setEnabled(!b);
		txtHouseRent.setEnabled(!b);
		txtOffDays.setEnabled(!b);
		txtOtAmount.setEnabled(!b);
		txtOtherAll.setEnabled(!b);
		txtOtHour.setEnabled(!b);
		txtOtMinute.setEnabled(!b);
		txtOtSec.setEnabled(!b);
		txtBasic.setEnabled(!b);
		txtInsurance.setEnabled(!b);
		txtIncomeTax.setEnabled(!b);
		txtRevenueStamp.setEnabled(!b);
	}

	private void componentIniRefresh(boolean b) 
	{
		dWorkingDate.setEnabled(!b);
		cmbSection.setEnabled(!b);
		RadioEmployee.setEnabled(!b);
		cmbEmployee.setEnabled(!b);
		txtTotalDays.setEnabled(!b);
		txtTourDays.setEnabled(!b);
		txtFriDays.setEnabled(!b);
		txtFestivalBonus.setEnabled(!b);
		txtLeaveDays.setEnabled(!b);
		txtLoanDeduction.setEnabled(!b);
		txtWorkDays.setEnabled(!b);
		txtAbsentAmt.setEnabled(!b);
		txtAbsentDays.setEnabled(!b);
		txtAdvanceSal.setEnabled(!b);
		txtPresentDays.setEnabled(!b);
		txtHoliDays.setEnabled(!b);
		txtHouseRent.setEnabled(!b);
		txtOffDays.setEnabled(!b);
		txtOtAmount.setEnabled(!b);
		txtOtherAll.setEnabled(!b);
		txtOtHour.setEnabled(!b);
		txtOtMinute.setEnabled(!b);
		txtOtSec.setEnabled(!b);
		txtBasic.setEnabled(!b);
		txtInsurance.setEnabled(!b);
		txtIncomeTax.setEnabled(!b);
		txtRevenueStamp.setEnabled(!b);
	}

	private void txtClear()
	{
		dWorkingDate.setValue(new java.util.Date());
		cmbSection.setValue(null);
		RadioEmployee.setValue(null);
		cmbEmployee.setValue(null);
		txtTotalDays.setValue("");
		txtTourDays.setValue("");
		txtFriDays.setValue("");
		txtFestivalBonus.setValue("");
		txtLeaveDays.setValue("");
		txtLoanDeduction.setValue("");
		txtWorkDays.setValue("");
		txtAbsentAmt.setValue("");
		txtAbsentDays.setValue("");
		txtAdvanceSal.setValue("");
		txtPresentDays.setValue("");
		txtHoliDays.setValue("");
		txtHouseRent.setValue("");
		txtOffDays.setValue("");
		txtOtAmount.setValue("");
		txtOtherAll.setValue("");
		txtOtHour.setValue("");
		txtOtMinute.setValue("");
		txtOtSec.setValue("");
		txtBasic.setValue("");
		txtInsurance.setValue("");
		txtIncomeTax.setValue("");
		txtRevenueStamp.setValue("");
	}

	private void refreshButtonEvent() 
	{
		componentIniRefresh(true);
		btnIni(true);
		txtClear();	
	}

	public void CheckPresent()
	{
		TD  = Integer.parseInt("0"+txtTotalDays.getValue().toString());
		FD  = Integer.parseInt("0"+txtFriDays.getValue().toString());
		HD  = Integer.parseInt("0"+txtHoliDays.getValue().toString());
		LD  = Integer.parseInt("0"+txtLeaveDays.getValue().toString());
		TrD = Integer.parseInt("0"+txtTourDays.getValue().toString());
		AD  = Integer.parseInt("0"+txtAbsentDays.getValue().toString());

	  /*System.out.println(TD);
		System.out.println(FD);
		System.out.println(HD);
		System.out.println(LD);
		System.out.println(TrD);
		System.out.println(AD);*/

		totalPresent=TD-(FD+HD+LD+TrD+AD);
		//System.out.println(totalPresent);
		txtPresentDays.setValue(totalPresent);
	}
	//------------------------------------------------------------------------------------------------------------------
}
