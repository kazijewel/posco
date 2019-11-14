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
import com.common.share.ReportOption;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
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

public class RptSectionWiseDailyLateAttendance extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	ArrayList<Component> allComp = new ArrayList<Component>();

	private Label lblSection;
	private ComboBox cmbSection;
	private CheckBox chkSectionAll;

	private Label lblDate;
	private PopupDateField dDate;

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});

	SimpleDateFormat dfYMD = new SimpleDateFormat("yyyy-MM-dd");

	public RptSectionWiseDailyLateAttendance(SessionBean sessionBean) 
	{
		this.sessionBean=sessionBean;
		this.setCaption("DAILY ATTENDANCE(Section Wise) :: "+sessionBean.getCompany());
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
				if(cmbSection.getValue()!=null || chkSectionAll.booleanValue()==true)
				{
					if(chkSectionAll.booleanValue()==true)
					{section = "%";}
					else
					{section = (cmbSection.getValue().toString());}

					reportShow(section);
				}
				else
				{
					showNotification("Warning","Select Section",Notification.TYPE_WARNING_MESSAGE);
					cmbSection.focus();
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

		chkSectionAll.addListener(new Listener()
		{
			public void componentEvent(Event event)
			{
				if(chkSectionAll.booleanValue()==true)
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
	}

	public void addSectionName()
	{
		cmbSection.removeAllItems();
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			List list = session.createSQLQuery(" SELECT AutoID,SectionName from tbSectionInfo order by AutoID ").list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbSection.addItem(element[0]);
				cmbSection.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void reportShow(Object sectionId)
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		String query=null;

		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			HashMap hm = new HashMap();
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());

			hm.put("username", sessionBean.getUserName()+" "+sessionBean.getUserIp());

			hm.put("SysDate",reportTime.getTime);
			hm.put("logo", sessionBean.getCompanyLogo());
			
			query=  " select a.* from (select EmpID,vFigId,vEmployeeName,sectionIdDb,sectionName,vDeginationName,dAttDate," +
					" ShiftStartDateTime,tIntime,CONVERT(Time,CONVERT(varchar,(case when (DATEDIFF(ss,ShiftStartDateTime," +
					" tIntime))<0 then '0' else (DATEDIFF(ss,ShiftStartDateTime,tIntime)) end)/3600)+':'+CONVERT(varchar," +
					" ((case when (DATEDIFF(ss,ShiftStartDateTime,tIntime))<0 then '0' else (DATEDIFF(ss," +
					" ShiftStartDateTime,tIntime)) end)%3600)/60)+':'+CONVERT(varchar,(case when (DATEDIFF(ss," +
					" ShiftStartDateTime,tIntime))<0 then '0' else (DATEDIFF(ss,ShiftStartDateTime,tIntime)) " +
					" end)%60),105) as lateTime from [funAttendanceReport] ('"+dfYMD.format(dDate.getValue())+"' ,'"+sectionId+"','%') group by EmpID,vFigId," +
					" vEmployeeName,sectionIdDb,sectionName, vDeginationName,ShiftStartDateTime,tIntime,dAttDate) as a" +
					" where a.lateTime != '00:00:00.0000000' and DATENAME(DW,a.dAttDate)!='Friday' order by a.sectionIdDb ";

			System.out.println(query);
			if(queryValueCheck(query))
			{
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/account/hrmModule/rptDailyLateList.jasper",
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
		allComp.add(cmbSection);
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
		setWidth("450px");
		setHeight("250px");

		// lblAsOnDate
		lblDate = new Label("Date :");
		lblDate.setImmediate(true);
		lblDate.setWidth("100%");
		lblDate.setHeight("-1px");
		mainLayout.addComponent(lblDate, "top:30.0px; left:50.0px;");

		// asOnDate
		dDate = new PopupDateField();
		dDate.setWidth("110px");
		dDate.setDateFormat("dd-MM-yyyy");
		dDate.setValue(new java.util.Date());
		dDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dDate, "top:28.0px; left:150.0px;");

		// lblCategory
		lblSection = new Label();
		lblSection.setImmediate(false);
		lblSection.setWidth("100.0%");
		lblSection.setHeight("-1px");
		lblSection.setValue("Section Name :");
		mainLayout.addComponent(lblSection,"top:70.0px; left:50.0px;");

		// cmbSection
		cmbSection = new ComboBox();
		cmbSection.setImmediate(false);
		cmbSection.setWidth("230px");
		cmbSection.setHeight("-1px");
		cmbSection.setNullSelectionAllowed(true);
		cmbSection.setImmediate(true);
		mainLayout.addComponent(cmbSection, "top:68.0px; left:150.0px;");

		//chkSectionAll
		chkSectionAll = new CheckBox("All");
		chkSectionAll.setHeight("-1px");
		chkSectionAll.setWidth("-1px");
		chkSectionAll.setImmediate(true);
		mainLayout.addComponent(chkSectionAll, "top:70.0px; left:385.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:110.0px;left:150.0px;");

		mainLayout.addComponent(cButton,"top:150.opx; left:140.0px");

		return mainLayout;
	}
}
