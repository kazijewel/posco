package hrm.common.reportform;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.ReportDate;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Component.Listener;

public class RptDailyEmployeeEntertainment extends Window {

	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblSection;
	private Label lblDate;

	private ComboBox cmbSection;
	private CheckBox chkSection;
	private PopupDateField dDate;

	private static final List<String> type=Arrays.asList(new String []{"PDF","Other"});

	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dForm = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat dMonthFormat = new SimpleDateFormat("MMMMM-yyyy");

	ArrayList<Component> allComp = new ArrayList<Component>();
	private ReportDate reportTime = new ReportDate();

	private OptionGroup rpttype;
	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");

	public RptDailyEmployeeEntertainment(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setCaption("MONTHLY WORKING SUMMARY :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);
		setEventAction();
		focusMove();
	}

	public void cmbSectionAddData()
	{
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery(" select distinct ea.vSectionId,vSectionName from tbEmployeeAttendance " +
					"ea inner join tbEmployeeInfo ei on ei.iFingerID=ea.vEmployeeId " +
					"where vShiftId!='3' and InOutFlag='OUT' and dAttInTime>='17:30:00' and dAttDate='"+dFormat.format(dDate.getValue())+"' ").list();

			for(Iterator iter=list.iterator();iter.hasNext();){

				Object[] element = (Object[]) iter.next();
				cmbSection.addItem(element[0]);
				cmbSection.setItemCaption(element[0], (String) element[1]);
			}
		}
		catch(Exception exp){
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public void setEventAction()
	{
		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbSection.getValue()!=null || chkSection.booleanValue())
				{
					if(dDate.getValue()!=null)
					{
						reportShow();
					}
					else
					{
						showNotification("Select Month",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					showNotification("Select Section Name",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		dDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(dDate.getValue()!=null)
				{
					cmbSection.removeAllItems();
					cmbSectionAddData();
				}
			}
		});

		chkSection.addListener(new Listener()
		{
			public void componentEvent(Event event)
			{
				if(chkSection.booleanValue()==true)
				{
					cmbSection.setEnabled(false);
					cmbSection.setValue(null);
				}
				else
				{
					cmbSection.setEnabled(true);
				}
			}
		});

		cButton.btnExit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});
	}

	private void reportShow()
	{
		String query=null;
		String SectionTotalOT="";
		Double Salary=0.00;
		String section="";
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			/*List lst = session.createSQLQuery("select dbo.sumOfTotalOT('"+cmbSection.getValue()+"'),0").list();
			for(Iterator iter=lst.iterator();iter.hasNext();)
			{  
			Object[] element = (Object[]) iter.next();
			SectionTotalOT= element[0].toString();
			}*/

			if(chkSection.booleanValue())
			{
				section="%";
			}
			else
			{
				section=cmbSection.getValue().toString();
			}

			Object[] element ;

			HashMap hm = new HashMap();
			System.out.println("SectionTotalOT"+hm.put("SectionTotalOT",SectionTotalOT));

			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			hm.put("section",cmbSection.getItemCaption(cmbSection.getValue()));
			hm.put("SysDate",reportTime.getTime);
			hm.put("date",dForm.format(dDate.getValue()));
			hm.put("logo", sessionBean.getCompanyLogo());
			//hm.put("SectionTotalOT",SectionTotalOT);

			query = " select distinct ei.employeeCode,ea.vEmployeeId,ea.vEmployeeName,vSectionName,vDesignation," +
					" dAttDate,dAttInTime,vShiftId,InOutFlag from tbEmployeeAttendance ea inner join tbEmployeeInfo " +
					" ei on ei.iFingerID=ea.vEmployeeId where vShiftId!='3' and InOutFlag='OUT' and dAttInTime>='17:30:00' " +
					" and dAttDate='"+dFormat.format(dDate.getValue())+"' and ea.vSectionId like '"+section+"' ";

			/*	Salary=(Double) element[21];*/

			System.out.println("Query"+query);

			if(queryValueCheck(query))
			{
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/account/hrmModule/rptEmployeeEntertainment.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",(rpttype.isSelected("PDF")?true:false));

				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);
			}
			else
			{
				showNotification("Warning","There are no Data",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception exp){
			this.getParent().showNotification("Error "+exp,Notification.TYPE_ERROR_MESSAGE);}
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

	private void focusMove()
	{
		allComp.add(cmbSection);
		allComp.add(dDate);
		allComp.add(cButton.btnPreview);

		new FocusMoveByEnter(this,allComp);
	}

	private AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("400px");
		setHeight("200px");

		// lblDate
		lblDate = new Label("Month :");
		lblDate.setImmediate(false);
		lblDate.setWidth("100.0%");
		lblDate.setHeight("-1px");
		mainLayout.addComponent(lblDate,"top:30.0px; left:30.0px;");

		// dDate
		dDate = new PopupDateField();
		dDate.setImmediate(true);
		dDate.setWidth("140px");
		dDate.setHeight("-1px");
		dDate.setDateFormat("dd-MM-yyyy");
		dDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dDate.setValue(new java.util.Date());
		mainLayout.addComponent(dDate, "top:28.0px; left:130.0px;");

		// lblSection
		lblSection = new Label("Division Name :");
		lblSection.setImmediate(false);
		lblSection.setWidth("100.0%");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection,"top:60.0px; left:30.0px;");

		// cmbSection
		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("200px");
		cmbSection.setHeight("-1px");
		cmbSection.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbSection, "top:58.0px; left:130.0px;");

		chkSection = new CheckBox();
		chkSection.setImmediate(true);
		chkSection.setWidth("200px");
		chkSection.setHeight("-1px");
		mainLayout.addComponent(chkSection, "top:58.0px; left:330.0px;");

		rpttype=new OptionGroup("",type);
		rpttype.setImmediate(true);
		rpttype.setStyleName("horizontal");
		rpttype.select("Other");
		mainLayout.addComponent(rpttype, "top:90.0px; left:130.0px");

		mainLayout.addComponent(cButton,"top:110.opx; left:120.0px");

		return mainLayout;
	}
}