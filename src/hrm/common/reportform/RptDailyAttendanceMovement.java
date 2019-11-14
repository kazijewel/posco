package hrm.common.reportform;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.ReportDate;
import com.common.share.ReportOption;
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
import com.vaadin.ui.Window.Notification;

public class RptDailyAttendanceMovement extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	ArrayList<Component> allComp = new ArrayList<Component>();

	/*private Label lblEmployee;
	private ComboBox cmbEmployee;
	private CheckBox chkSectionAll;*/
	
	private Label lblSectionName;
	private ComboBox cmbSectionName;
	//private CheckBox chkSectionAll;
	
	private Label lblMonth;
	private PopupDateField dDate;

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();
	
	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});

	public RptDailyAttendanceMovement(SessionBean sessionBean) 
	{
		this.sessionBean=sessionBean;
		this.setCaption("Section Wise Daily Movement) :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);


		addSectionName();
		setEventAction();
		focusMove();
	}

	public void setEventAction()
	{
		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				String section = "";
				if(cmbSectionName.getValue()!=null)   //|| chkSectionAll.booleanValue()==true)
				{
					/*if(chkSectionAll.booleanValue()==true)
					{section = "%";}
					else
					{*/
						section = cmbSectionName.getValue().toString();  //}

					reportShow(section);
				}
				else
				{
					showNotification("Warning","Select Section",Notification.TYPE_WARNING_MESSAGE);
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

	/*	chkSectionAll.addListener(new Listener()
		{
			public void componentEvent(Event event)
			{
				if(chkSectionAll.booleanValue()==true)
				{
					cmbSectionName.setValue(null);
					cmbSectionName.setEnabled(false);
				}
				else
				{
					cmbSectionName.setEnabled(true);
				}
			}
		});*/
		
		dDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(dDate.getValue()!=null)
				{
					cmbSectionName.removeAllItems();
					addSectionName();
				}
			}
		});
		
	}

	public void addSectionName()
	{
		cmbSectionName.removeAllItems();
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			List list = session.createSQLQuery("SELECT Distinct vSectionId,vSectionName from tbEmployeeAttendance where dAttDate='"+dFormat.format(dDate.getValue())+"'  order by vSectionId").list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbSectionName.addItem(element[0]);
				cmbSectionName.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void reportShow(Object Section)
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		String query=null;
		String TotalOT=null;
		String mm=null;
		String hh=null;
		String ss=null;
		String activeFlag = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
		
			HashMap hm = new HashMap();
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			//			hm.put("phone", sessionBean.getCompanyPhone());
			//			hm.put("email", sessionBean.getCompanyEmail());
			//			hm.put("fax", sessionBean.getCompanyFax());
			hm.put("username", sessionBean.getUserName()+" "+sessionBean.getUserIp());
			//			hm.put("userIp", sessionBean.getUserIp());
			hm.put("SysDate",reportTime.getTime);
			hm.put("logo", sessionBean.getCompanyLogo());
	    	
			query = "SELECT * FROM funDailyAttMovement('"+dFormat.format(dDate.getValue())+"','"+Section+"') order  by SectionID";
			
			/*query="SELECT *,SUM(DATEDIFF(SS,'00:00:00',otHour))/3600 hh,SUM(DATEDIFF(SS,'00:00:00',otHour))%3600/60 mm," +
					"SUM(DATEDIFF(SS,'00:00:00',otHour))%60 ss FROM funMonthlyAttend('"+dFormat.format(dDate.getValue())+"','"+cmbEmployee.getValue()+"') " +
					"group by dEveryDay,dWeekName,vTxtFigId,vEmployeeName,vDeginationName,dJoiningDate,sectionName," +
					"tIntime,tOutTime,tHour,otHour,Status,Remarks,pd,fd,ad,sl,cl,ml,al,hd,Late" ;*/
			if(queryValueCheck(query))
			{
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/account/hrmModule/rptDailyAttendenceMovement.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",RadioBtn.Radio);

				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);
			}
			else
			{
				showNotification("Warning","There are no Data",Notification.TYPE_WARNING_MESSAGE);
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

	private void focusMove()
	{
		allComp.add(cmbSectionName);
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
		setHeight("220px");
		
		lblSectionName = new Label();
		lblSectionName.setImmediate(false);
		lblSectionName.setWidth("100.0%");
		lblSectionName.setHeight("-1px");
		lblSectionName.setValue("Division Name :");
		mainLayout.addComponent(lblSectionName,"top:45.0px; left:30.0px;");

		// cmbEmployee
		cmbSectionName = new ComboBox();
		cmbSectionName.setImmediate(false);
		cmbSectionName.setWidth("200px");
		cmbSectionName.setHeight("-1px");
		cmbSectionName.setNullSelectionAllowed(true);
		cmbSectionName.setImmediate(true);
		mainLayout.addComponent(cmbSectionName, "top:43.0px; left:130.0px;");

	    //sectionAll
		/*chkSectionAll = new CheckBox("All");
		chkSectionAll.setHeight("-1px");
		chkSectionAll.setWidth("-1px");
		chkSectionAll.setImmediate(true);
		mainLayout.addComponent(chkSectionAll, "top:18.0px; left:336.0px;");
		*/
		lblMonth = new Label("Date :");
		lblMonth.setImmediate(false);
		lblMonth.setWidth("-1px");
		lblMonth.setHeight("-1px");
		mainLayout.addComponent(lblMonth, "top:20.0px; left:40.0px;");

		dDate = new PopupDateField();
		dDate.setImmediate(true);
		dDate.setWidth("140px");
		dDate.setDateFormat("dd-MM-yyyy");
		dDate.setValue(new java.util.Date());
		dDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dDate, "top:18.0px; left:130.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:80.0px;left:130.0px;");

		mainLayout.addComponent(cButton,"top:130.opx; left:120.0px");

		return mainLayout;
	}
}
