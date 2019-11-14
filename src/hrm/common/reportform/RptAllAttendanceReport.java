package hrm.common.reportform;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;


import com.common.share.CommonButton;
import com.common.share.PreviewOption;
import com.common.share.ReportDate;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Component.Listener;
import com.vaadin.ui.Window.Notification;
//Author By Sabrina Alam
//Date:03.06.2015
public class RptAllAttendanceReport extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	// Main Panel
	private Label lblReportName;
	private Panel leftPanel;
	private OptionGroup RadioButtonReportName;
	private static final List<String> groupButtonReportName=Arrays.asList(new String[]{"Attendance","Absent","Late In"});

	// previewPanel
	private Panel previewPanel;
	private Label lblPreviewOption;

	private Label lblFromDate;
	private Label lblAsonDate;
	private Label lblDate;
	private PopupDateField dfromDate;

	private Label lbltoDate;
	private PopupDateField dtoDate;

	private Label lblMonth;
	private PopupDateField dMonth;

	private Label lblYear;
	private PopupDateField dYear;

	private Label lblSection;
	private ComboBox cmbSection;
	private CheckBox chkSection;

	private Label lblShift;
	private ComboBox cmbShift;
	private CheckBox chkShift;

	private Label lblSR;
	private ComboBox cmbSR;
	private CheckBox chkSr;

	private Label lblCat;
	private ComboBox cmbCat;
	private CheckBox chkCat;

	private Label lblOfficeLocation;
	private ComboBox cmbOfficeLocation;
	private CheckBox chkOfficeLocation;

	// start report criteria

	private Label lblReportCriteria;

	// PartyDateBetweenPanel
	private Panel PartyDateBetweenPanel;
	private FormLayout fLayoutPartyDateBetween = new FormLayout();

	// SummaryPanel
	private Panel PartySummaryPanel;
	private FormLayout fLayoutPartySummary = new FormLayout();

	// TargetAchievePanel
	private Panel PartyTargetAchievePanel;
	private FormLayout fLayoutPartyTargetAchieve = new FormLayout();

	private Panel PartyDuePanel;
	private FormLayout fLayoutPartyDue = new FormLayout();

	private Label lblTargetAchieve;

	// end report criteria

	// CategoryPanel
	private Panel CategoryPanel;
	private Label lblCategory;

	private OptionGroup RadioButtonAttendance;
	private static final List<String> groupButtonAttendance=Arrays.asList(new String[]{"Daily Attendance","Attendance Summary"});

	private OptionGroup RadioButtonAbsent;
	private static final List<String> groupButtonAbsent=Arrays.asList(new String[]{"Daily Absent"});

	private OptionGroup RadioButtonLate;
	private static final List<String> groupButtonLate=Arrays.asList(new String[]{"Daily Late In"});

	// date Formats
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat yformat = new SimpleDateFormat("yyyy");
	private SimpleDateFormat dFFiscal = new SimpleDateFormat("yyyy/MM/dd");
	private SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat ym = new SimpleDateFormat("yyyy-MM-01");
	private SimpleDateFormat m = new SimpleDateFormat("MM");
	private SimpleDateFormat d = new SimpleDateFormat("dd");
	private SimpleDateFormat mFormat = new SimpleDateFormat("MMMM");

	private PreviewOption po = new PreviewOption();

	private ReportDate reportTime = new ReportDate();

	private HorizontalLayout hButtonLayout = new HorizontalLayout();
	private CommonButton cButton = new CommonButton("", "", "", "","","","","Preview","Print","Exit");
	private NativeButton btnPrint = new NativeButton("Print");

	public RptAllAttendanceReport(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("ATTENDANCE REPORT :: "+sessionBean.getCompany());
		this.setWidth("850px");
		this.setHeight("320px");
		this.setResizable(false);
		buildMainLayout();
		setContent(mainLayout);
		PartyInvisiblerData(false);
		cmbAddSectionData();
		cmbShiftAddData();
		setEventAction();
		SetRadio();
	}

	public void setEventAction()
	{
		RadioButtonReportName.addListener(new ValueChangeListener() 
		{

			public void valueChange(ValueChangeEvent event) 
			{ 
				SetRadio();
				ClearData();
				if(RadioButtonReportName.getValue().toString().equals("Attendance"))
				{
					SetRadio();
					ClearData();
					PartyInvisiblerData(false);
					optionGroupVisible(true,false,false);

					RadioButtonAttendance.addListener(new ValueChangeListener() 
					{
						public void valueChange(ValueChangeEvent event) 
						{
							ClearData();
							PartyInvisiblerData(false);
							if(RadioButtonAttendance.getValue().toString().equals("Daily Attendance") )

							{
								ClearData();
								PartyInvisiblerData(false);
								PartyWise(true,true,true,true,true,true,false,false,false,true,true,false,false,false);
							}

							if(RadioButtonAttendance.getValue().toString().equals("Attendance Summary"))
							{
								ClearData();
								PartyInvisiblerData(false);
								PartyWise(true,true,true,true,false,true,false,false,true,true,true,false,false,false);
							}
						}
					});
				}

				if(RadioButtonReportName.getValue().toString().equals("Absent"))
				{
					PartyInvisiblerData(false);
					optionGroupVisible(false,true,false);

					RadioButtonAbsent.addListener(new ValueChangeListener() 
					{

						public void valueChange(ValueChangeEvent event) 
						{
							SetRadio();
							ClearData();
							PartyInvisiblerData(false);
							if(RadioButtonAbsent.getValue().toString().equals("Daily Absent") )
							{
								PartyInvisiblerData(false);
								ProductWise(true,true,true,true,false,false,false,false,false);
							}
						}
					});

				}

				if(RadioButtonReportName.getValue().toString().equals("Late In"))
				{
					SetRadio();
					ClearData();
					PartyInvisiblerData(false);
					optionGroupVisible(false,false,true);

					RadioButtonLate.addListener(new ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) 
						{
							if(event.getProperty().toString().equals("Daily Late In"))
							{
								ClearData();
								PartyInvisiblerData(false);
								SRWise(true,true,false,false,true,true,true,false,false,false,false);
							}
						}
					});
				}

			}
		});

		chkShift.addListener(new Listener()
		{
			public void componentEvent(Event event)
			{
				if(chkShift.booleanValue()==true)
				{
					cmbShift.setValue(null);
					cmbShift.setEnabled(false);
				}
				else
				{
					cmbShift.setEnabled(true);
				}
			}
		});

		chkSection.addListener(new Listener()
		{
			public void componentEvent(Event event)
			{
				if(chkSection.booleanValue()==true)
				{

					cmbSection.setValue(null);
					cmbSection.setEnabled(false);
				}
				else
				{
					cmbSection.setEnabled(true);
				}
			}
		});

		cButton.btnPreview.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(RadioButtonReportName.getValue().toString().equals("Attendance"))
				{
					if(RadioButtonAttendance.getValue().toString().equals("Daily Attendance")){
						/*if(cmbSection.getValue()!=null || chkSection.booleanValue()==true ){
							if(chkShift.booleanValue()==true || cmbShift.getValue()!=null){*/
						previewReport();

						/*}else{
								getParent().showNotification("Warning","Please Select Shift",Notification.TYPE_WARNING_MESSAGE);
								cmbShift.focus();
							}
						}else{
							getParent().showNotification("Warning","Please Select Section",Notification.TYPE_WARNING_MESSAGE);
							cmbSection.focus();
						}*/
					}
				}
			}
		});

		cButton.btnExit.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});
	}


	public void cmbAddSectionData()
	{
		cmbSection.removeAllItems();
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery("select AutoID,SectionName from tbSectionInfo order by AutoID").list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbSection.addItem(element[0].toString());
				cmbSection.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void cmbShiftAddData()
	{
		Transaction tx=null;
		try
		{

			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery("select vShiftId,vShiftName from tbshiftInformation order by vShiftId ").list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbShift.addItem(element[0].toString());
				cmbShift.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error:",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void addOfficeInformation()
	{
		cmbOfficeLocation.removeAllItems();
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery(" select vDepoId,vDepoName from tbDepoInformation order by iAutoId ").list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbOfficeLocation.addItem(element[0]);
				cmbOfficeLocation.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("830px");
		mainLayout.setHeight("280px");
		mainLayout.setMargin(false);


		/*------------------------------Left Select Main Panel---------------------------------------------*/

		leftPanel = new Panel();
		leftPanel.setWidth("150px");
		leftPanel.setHeight("140px");
		leftPanel.setImmediate(true);
		leftPanel.setStyleName("radius");
		mainLayout.addComponent(leftPanel, "top:20px;left:20px;");

		lblReportName = new Label("<font color='#06854E' size='2px'><b>Report Name</b></font>");
		lblReportName.setImmediate(true);
		lblReportName.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblReportName, "top:30px;left:55px;");

		RadioButtonReportName = new OptionGroup("",groupButtonReportName);
		RadioButtonReportName.setImmediate(true);
		RadioButtonReportName.setStyleName("vertical");
		mainLayout.addComponent(RadioButtonReportName,"top:55.0px;left:30.0px");

		/*-------------------------Preview Option Panel-----------------------------------------*/

		previewPanel = new Panel();
		previewPanel.setWidth("150px");
		previewPanel.setHeight("100px");
		previewPanel.setImmediate(true);
		previewPanel.setStyleName("radius");

		po.setImmediate(true);
		previewPanel.addComponent(po);
		mainLayout.addComponent(previewPanel, "top:165px;left:20px;");

		lblPreviewOption = new Label("<font color='#06854E' size='2px'><b>Report Preview</b></font>");
		lblPreviewOption.setImmediate(true);
		lblPreviewOption.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblPreviewOption, "top:170px;left:42px;");

		//------------------------------------ For Category Panel-------------------------------------------

		CategoryPanel = new Panel();
		CategoryPanel.setWidth("220px");
		CategoryPanel.setHeight("245px");
		CategoryPanel.setImmediate(true);
		CategoryPanel.setStyleName("radius");
		mainLayout.addComponent(CategoryPanel, "top:20px;left:190px;");

		lblCategory = new Label("<font color='#06854E'><b>Report Category</b></font>");
		lblCategory.setImmediate(true);
		lblCategory.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblCategory, "top:30px;left:250px;");

		// ------------------------------- Radio Button ---------------------------------------------

		RadioButtonAttendance = new OptionGroup("",groupButtonAttendance);
		RadioButtonAttendance.setImmediate(true);
		RadioButtonAttendance.setVisible(false);
		mainLayout.addComponent(RadioButtonAttendance, "top:55px;left:200px;");

		RadioButtonAbsent = new OptionGroup("",groupButtonAbsent);
		RadioButtonAbsent.setImmediate(true);
		RadioButtonAbsent.setVisible(false);
		mainLayout.addComponent(RadioButtonAbsent, "top:55px;left:200px;");

		RadioButtonLate = new OptionGroup("",groupButtonLate);
		RadioButtonLate.setImmediate(true);
		RadioButtonLate.setVisible(false);
		mainLayout.addComponent(RadioButtonLate, "top:55px;left:200px;");


		/*--------------------------------- For Party Date Between --------------------------------*/

		PartyDateBetweenPanel = new Panel();
		PartyDateBetweenPanel.setImmediate(true);
		PartyDateBetweenPanel.setWidth("400px");
		PartyDateBetweenPanel.setHeight("245px");
		PartyDateBetweenPanel.setStyleName("radius");
		mainLayout.addComponent(PartyDateBetweenPanel, "top:20px;left:430px;");

		lblSection = new Label("<font color='#9110BB'><b>Section :</b></font>");
		lblSection.setImmediate(true);
		lblSection.setContentMode(Label.CONTENT_XHTML);
		lblSection.setVisible(false);
		mainLayout.addComponent(lblSection, "top:80px;left:450px;");

		cmbSection=new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("200px");
		cmbSection.setHeight("-1px");
		cmbSection.setNullSelectionAllowed(true);
		cmbSection.setVisible(false);
		mainLayout.addComponent(cmbSection, "top:78px;left:550px;");

		//CategoryAll
		chkSection = new CheckBox("All");
		chkSection.setHeight("-1px");
		chkSection.setWidth("-1px");
		chkSection.setImmediate(true);
		chkSection.setVisible(false);
		mainLayout.addComponent(chkSection, "top:78px; left:766.0px;");

		lblShift = new Label("<font color='#9110BB'><b>Shift :</b></font>");
		lblShift.setImmediate(true);
		lblShift.setContentMode(Label.CONTENT_XHTML);
		lblShift.setVisible(false);
		mainLayout.addComponent(lblShift, "top:105px;left:450px;");

		cmbShift=new ComboBox();
		cmbShift.setImmediate(true);
		cmbShift.setWidth("200px");
		cmbShift.setHeight("-1px");
		cmbShift.setNullSelectionAllowed(true);
		cmbShift.setVisible(false);
		mainLayout.addComponent(cmbShift, "top:103px;left:550px;");

		chkShift = new CheckBox("All");
		chkShift.setHeight("-1px");
		chkShift.setWidth("-1px");
		chkShift.setImmediate(true);
		chkShift.setVisible(false);
		mainLayout.addComponent(chkShift, "top:103px; left:766.0px;");

		lblFromDate = new Label("<font color='#9110BB'><b>From Date</b></font>");
		lblFromDate.setImmediate(true);
		lblFromDate.setContentMode(Label.CONTENT_XHTML);
		lblFromDate.setVisible(false);
		mainLayout.addComponent(lblFromDate, "top:130px;left:450px;");

		lblDate = new Label("<font color='#9110BB'><b>Date</b></font>");
		lblDate.setImmediate(true);
		lblDate.setContentMode(Label.CONTENT_XHTML);
		lblDate.setVisible(false);
		mainLayout.addComponent(lblDate, "top:130px;left:450px;");

		lblAsonDate = new Label("<font color='#9110BB'><b>As on Date</b></font>");
		lblAsonDate.setImmediate(true);
		lblAsonDate.setContentMode(Label.CONTENT_XHTML);
		lblAsonDate.setVisible(false);
		mainLayout.addComponent(lblAsonDate, "top:130px;left:450px;");

		dfromDate = new PopupDateField();
		dfromDate.setImmediate(true);
		dfromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dfromDate.setDateFormat("dd-MM-yyyy");
		dfromDate.setValue(new java.util.Date());
		dfromDate.setVisible(false);
		mainLayout.addComponent(dfromDate, "top:128px;left:550px;");

		lblMonth = new Label("<font color='#9110BB'><b>Month :</b></font>");
		lblMonth.setImmediate(true);
		lblMonth.setContentMode(Label.CONTENT_XHTML);
		lblMonth.setVisible(false);
		mainLayout.addComponent(lblMonth, "top:130px;left:450px;");

		dMonth = new PopupDateField();
		dMonth.setImmediate(true);
		dMonth.setResolution(PopupDateField.RESOLUTION_MONTH);
		dMonth.setDateFormat("MMMM");
		dMonth.setValue(new java.util.Date());
		dMonth.setVisible(false);
		mainLayout.addComponent(dMonth, "top:128px;left:550px;");

		lblYear = new Label("<font color='#9110BB'><b>Year :</b></font>");
		lblYear.setImmediate(true);
		lblYear.setContentMode(Label.CONTENT_XHTML);
		lblYear.setVisible(false);
		mainLayout.addComponent(lblYear, "top:130px;left:450px;");

		dYear = new PopupDateField();
		dYear.setImmediate(true);
		dYear.setResolution(PopupDateField.RESOLUTION_YEAR);
		dYear.setDateFormat("yyyy");
		dYear.setValue(new java.util.Date());
		dYear.setVisible(false);
		mainLayout.addComponent(dYear, "top:128px;left:550px;");

		lbltoDate = new Label("<font color='#9110BB'><b>To Date :</b></font>");
		lbltoDate.setImmediate(true);
		lbltoDate.setContentMode(Label.CONTENT_XHTML);
		lbltoDate.setVisible(false);
		mainLayout.addComponent(lbltoDate, "top:155px;left:450px;");

		dtoDate = new PopupDateField();
		dtoDate.setImmediate(true);
		dtoDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dtoDate.setDateFormat("dd-MM-yyyy");
		dtoDate.setValue(new java.util.Date());
		dtoDate.setVisible(false);
		mainLayout.addComponent(dtoDate, "top:153px;left:550px;");

		lblSR = new Label("<font color='#9110BB'><b>TSO Name :</b></font>");
		lblSR.setImmediate(true);
		lblSR.setContentMode(Label.CONTENT_XHTML);
		lblSR.setVisible(false);
		mainLayout.addComponent(lblSR, "top:105px;left:450px;");

		cmbSR=new ComboBox();
		cmbSR.setImmediate(true);
		cmbSR.setWidth("200px");
		cmbSR.setHeight("-1px");
		cmbSR.setNullSelectionAllowed(true);
		cmbSR.setVisible(false);
		mainLayout.addComponent(cmbSR, "top:103px;left:550px;");

		//CategoryAll
		chkSr = new CheckBox("All");
		chkSr.setHeight("-1px");
		chkSr.setWidth("-1px");
		chkSr.setImmediate(true);
		chkSr.setVisible(false);
		mainLayout.addComponent(chkSr, "top:103px; left:766.0px;");

		lblCat = new Label("<font color='#9110BB'><b>Category :</b></font>");
		lblCat.setImmediate(true);
		lblCat.setContentMode(Label.CONTENT_XHTML);
		lblCat.setVisible(false);
		mainLayout.addComponent(lblCat, "top:105px;left:450px;");

		cmbCat=new ComboBox();
		cmbCat.setImmediate(true);
		cmbCat.setWidth("200px");
		cmbCat.setHeight("-1px");
		cmbCat.setNullSelectionAllowed(true);
		cmbCat.setVisible(false);
		mainLayout.addComponent(cmbCat, "top:103px;left:550px;");

		chkCat = new CheckBox("All");
		chkCat.setHeight("-1px");
		chkCat.setWidth("-1px");
		chkCat.setImmediate(true);
		chkCat.setVisible(false);
		mainLayout.addComponent(chkCat, "top:103px; left:766.0px;");

		lblOfficeLocation = new Label("<font color='#9110BB'><b>Office :</b></font>");
		lblOfficeLocation.setImmediate(true);
		lblOfficeLocation.setContentMode(Label.CONTENT_XHTML);
		lblOfficeLocation.setVisible(false);
		mainLayout.addComponent(lblOfficeLocation, "top:80px;left:450px;");

		cmbOfficeLocation=new ComboBox();
		cmbOfficeLocation.setImmediate(true);
		cmbOfficeLocation.setWidth("200px");
		cmbOfficeLocation.setHeight("-1px");
		cmbOfficeLocation.setNullSelectionAllowed(true);
		cmbOfficeLocation.setVisible(false);
		mainLayout.addComponent(cmbOfficeLocation, "top:78px;left:550px;");

		//CategoryAll
		chkOfficeLocation = new CheckBox("All");
		chkOfficeLocation.setHeight("-1px");
		chkOfficeLocation.setWidth("-1px");
		chkOfficeLocation.setImmediate(true);
		chkOfficeLocation.setVisible(false);
		mainLayout.addComponent(chkOfficeLocation, "top:78px; left:766.0px;");

		/*btnPrint.setWidth("90px");
		btnPrint.setHeight("28px");
		btnPrint.setIcon(new ThemeResource("../icons/print.png"));
		mainLayout.addComponent(btnPrint, "top:230px;left:490px;");
		 */
		/*---------------------------------------- For hButtonLayout-------------------------------------------*/

		lblReportCriteria = new Label("<font color='#06854E' size='2px'><b>Report Criteria</b></font>");
		lblReportCriteria.setImmediate(true);
		lblReportCriteria.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblReportCriteria, "top:30px;left:580px;");

		mainLayout.addComponent(cButton,"top:230px;left:590px;");

		return mainLayout;
	}

	private void ClearData()
	{
		cmbSection.setValue(null);
		cmbShift.setValue(null);
		dfromDate.setValue(new java.util.Date());
		dtoDate.setValue(new java.util.Date());
		chkSection.setValue(false);
		chkShift.setValue(false);
		cmbSR.setValue(null);
		dMonth.setValue(new java.util.Date());
		dYear.setValue(new java.util.Date());
		chkCat.setValue(false);
		cmbCat.setValue(null);
		chkOfficeLocation.setValue(false);
		cmbOfficeLocation.setValue(null);
		chkSr.setValue(false);
	}


	private void PartyInvisiblerData(boolean t)
	{
		cmbSection.setVisible(t);
		cmbShift.setVisible(t);
		dfromDate.setVisible(t);
		dtoDate.setVisible(t);
		lblShift.setVisible(t);
		lblSection.setVisible(t);
		lblFromDate.setVisible(t);
		lbltoDate.setVisible(t);
		lblAsonDate.setVisible(t);
		lblYear.setVisible(t);
		dMonth.setVisible(t);
		lblMonth.setVisible(t);
		dYear.setVisible(t);
		chkSection.setVisible(t);
		chkShift.setVisible(t);
		lblSR.setVisible(t);
		cmbSR.setVisible(t);
		lblCat.setVisible(t);
		cmbCat.setVisible(t);
		chkCat.setVisible(t);
		lblOfficeLocation.setVisible(t);
		cmbOfficeLocation.setVisible(t);
		chkOfficeLocation.setVisible(t);
		chkSr.setVisible(t);
	}

	private void optionGroupVisible(boolean a,boolean b,boolean c)

	{
		RadioButtonAttendance.setVisible(a);
		RadioButtonAbsent.setVisible(b);
		RadioButtonLate.setVisible(c);

	}
	private void SetRadio()

	{
		RadioButtonAttendance.setValue(false);
		RadioButtonAbsent.setValue(false);
		RadioButtonLate.setValue(false);
	}

	private void PartyWise(boolean a,boolean b,boolean c,boolean d,boolean e,boolean f,boolean g,boolean h,boolean i,boolean j,boolean k,boolean l,boolean m,boolean n)

	{
		lblSection.setVisible(a);
		cmbSection.setVisible(b);
		lblShift.setVisible(c);
		cmbShift.setVisible(d);
		lblDate.setVisible(e);
		dfromDate.setVisible(f);
		lbltoDate.setVisible(g);
		dtoDate.setVisible(h);
		lblAsonDate.setVisible(i);
		chkSection.setVisible(j);
		chkShift.setVisible(k);
		lblOfficeLocation.setVisible(l);
		cmbOfficeLocation.setVisible(m);
		chkOfficeLocation.setVisible(n);
	}

	private void SRWise(boolean a,boolean b,boolean c,boolean d,boolean e,boolean f,boolean g,boolean h,boolean i,boolean j,boolean k)
	{

		lblMonth.setVisible(a);
		dMonth.setVisible(b);
		lblSR.setVisible(c);
		cmbSR.setVisible(d);
		lblSection.setVisible(e);
		cmbSection.setVisible(f);
		chkSection.setVisible(g);
		lblCat.setVisible(h);
		cmbCat.setVisible(i);
		chkCat.setVisible(j);
		chkSr.setVisible(k);
	}

	private void ProductWise(boolean a,boolean b,boolean c,boolean d,boolean e,boolean f,boolean g,boolean h,boolean i)

	{
		lblFromDate.setVisible(a);
		dfromDate.setVisible(b);
		lbltoDate.setVisible(c);
		dtoDate.setVisible(d);
		lblAsonDate.setVisible(e);
		lblMonth.setVisible(f);
		dMonth.setVisible(g);
		lblYear.setVisible(h);
		dYear.setVisible(i);
	}

	private void PartyWiseCollection(boolean a,boolean b,boolean c,boolean d,boolean e,boolean f,boolean g,boolean h,boolean i,boolean j,boolean k,boolean l,boolean m)

	{
		lblOfficeLocation.setVisible(a);
		cmbOfficeLocation.setVisible(b);
		chkOfficeLocation.setVisible(c);
		lblMonth.setVisible(d);
		dMonth.setVisible(e);
		lblYear.setVisible(f);
		dYear.setVisible(g);
		lblSection.setVisible(h);
		cmbSection.setVisible(i);
		lblShift.setVisible(j);
		cmbShift.setVisible(k);
		chkSection.setVisible(l);
		chkShift.setVisible(m);
	}

	private void previewReport()
	{
		String query=null;
		String report = "";
		String section ="";
		String shift ="";
		String Category ="";
		String SR ="";
		String Office ="";

		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			HashMap hm = new HashMap();
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("UserName", sessionBean.getUserName()+" "+sessionBean.getUserIp());
			hm.put("date", dfromDate.getValue());
			hm.put("ToDate", dtoDate.getValue());
			hm.put("month", dMonth.getValue());
			hm.put("year", dYear.getValue());
			hm.put("SysDate",reportTime.getTime);
			hm.put("logo", sessionBean.getCompanyLogo());


			if(RadioButtonReportName.getValue().toString().equals("Attendance") ){

				if(chkSection.booleanValue()==true)
				{
					section="%";
				}
				else
				{
					section=cmbSection.getValue().toString();
				}

				if(chkShift.booleanValue()==true)
				{
					shift="%";
				}
				else
				{
					shift=cmbShift.getValue().toString();
				}

				if(RadioButtonAttendance.getValue().toString().equals("Daily Attendance"))
				{
					query=" select ea.vEmployeeId,ea.vEmployeeName,ea.vDesignation,ea.vSectionName,ea.dAttInTime,si.vShiftName," +
							"si.tShiftStart,si.tShiftEnd,ea.dAttOutTime,SUBSTRING(CONVERT(varchar,(CONVERT(time,(convert(varchar(5)," +
							"DateDiff(s, ea.dAttInTime,ea.dAttOutTime)/3600)+':'+convert(varchar(5),DateDiff" +
							"(s, dAttInTime,dAttOutTime)%3600/60)+':'+convert(varchar(5),(DateDiff(s, dAttInTime,dAttOutTime)%60)))))),1,8) " +
							"totalTime from tbEmployeeAttendance ea left join tbEmployeeInfo ei on ei.vProximityId=ea.vEmployeeId ";

					report="report/account/hrmModule/rptDailyAttendence.jasper";

				}
			}

			if(queryValueCheck(query))
			{
				hm.put("sql", query);
				Window win = new ReportViewer(hm,report,
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",po.actionCheck);
				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);
			}
			else
			{
				showNotification("Warning!","There are no Data",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error "+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}	

	private boolean queryValueCheck(String sql)
	{
		Transaction tx = null;

		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			Iterator iter = session.createSQLQuery(sql).list().iterator();

			if (iter.hasNext()) 
			{
				return true;
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		return false;
	}

}
