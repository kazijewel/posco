package hrm.common.reportform;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.ReportDate;
import com.common.share.ReportOption;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
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

@SuppressWarnings("serial")
public class RptSectionWiseDailyAbsent extends Window 
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

	public RptSectionWiseDailyAbsent(SessionBean sessionBean) 
	{
		this.sessionBean=sessionBean;
		this.setCaption("DIVISION WISE DAILY ABSENT :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);
		addSectionName();
		setEventAction();
		focusMove();
		dDate.focus();
	}

	public void setEventAction()
	{
		dDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				addSectionName();
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

		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
				{
					String section = "%";

					if(cmbSection.getValue()!=null)
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
	}

	public void addSectionName()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vSectionId,vSectionName from tbEmpSectionInfo order by vSectionName";
			List <?> list = session.createSQLQuery(query).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbSection.addItem(element[0]);
				cmbSection.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("addSectionName",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void reportShow(Object sectionId)
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		try
		{

			String query="select * from tbEmployeeAttendanceFinal where dDate='"+dfYMD.format(dDate.getValue())+"'";
			if(queryValueCheck(query))
			{
				query="select dDate from tbHoliday where dDate='"+dfYMD.format(dDate.getValue())+"'";
				if(!queryValueCheck(query))
				{
					query="select ei.vProximityId,ei.vEmployeeCode,ei.vEmployeeName,din.vDesignation,esin.vSectionName from " +
							"tbEmpOfficialPersonalInfo ei inner join tbEmpSectionInfo esin on ei.vEmployeeId=esin.vEmployeeId " +
							"inner join tbEmpDesignationInfo din on ei.vEmployeeId=din.vEmployeeId where esin.vSectionId like " +
							"'"+sectionId+"' and (ei.vEmployeeID not in (select vEmployeeId from tbEmployeeAttendanceFinal where " +
							"dDate='"+dfYMD.format(dDate.getValue())+"')) and ei.bStatus=1 order by esin.vSectionName," +
							"cast(vEmployeeCode as int)";

					if(queryValueCheck(query))
					{
						HashMap <String,Object> hm = new HashMap <String,Object> ();
						hm.put("company", sessionBean.getCompany());
						hm.put("address", sessionBean.getCompanyAddress());
						hm.put("phone", sessionBean.getCompanyContact());
						hm.put("username", sessionBean.getUserName()+" "+sessionBean.getUserIp());
						hm.put("SysDate",reportTime.getTime);
						hm.put("logo", sessionBean.getCompanyLogo());
						hm.put("date", new SimpleDateFormat("dd-MM-yyyy").format(dDate.getValue()));
						hm.put("sql", query);

						Window win = new ReportViewer(hm,"hrm/jasper/Attendance/rptDailyAbsentRegister.jasper",
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
				else
				{
					showNotification("Warning","There are no Data",Notification.TYPE_WARNING_MESSAGE);
				}
			}
			else
			{
				showNotification("Warning","There are no Data",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception exp)
		{
			showNotification("reportShow "+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private boolean queryValueCheck(String sql)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();

		try 
		{
			Iterator <?> iter = session.createSQLQuery(sql).list().iterator();

			if (iter.hasNext()) 
			{
				return true;
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		finally{session.close();}
		return false;
	}

	private void focusMove()
	{
		allComp.add(dDate);
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
		setWidth("480px");
		setHeight("260px");

		// lblAsOnDate
		lblDate = new Label("Date :");
		lblDate.setWidth("100%");
		lblDate.setHeight("-1px");
		mainLayout.addComponent(lblDate, "top:30.0px; left:30.0px;");

		// asOnDate
		dDate = new PopupDateField();
		dDate.setWidth("110px");
		dDate.setDateFormat("dd-MM-yyyy");
		dDate.setValue(new java.util.Date());
		dDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dDate, "top:28.0px; left:150.0px;");

		// lblCategory
		lblSection = new Label();
		lblSection.setWidth("100.0%");
		lblSection.setHeight("-1px");
		lblSection.setValue("Division Name :");
		mainLayout.addComponent(lblSection,"top:70.0px; left:30.0px;");

		// cmbSection
		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("260px");
		cmbSection.setHeight("-1px");
		cmbSection.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbSection, "top:68.0px; left:150.0px;");

		//chkSectionAll
		chkSectionAll = new CheckBox("All");
		chkSectionAll.setHeight("-1px");
		chkSectionAll.setWidth("-1px");
		chkSectionAll.setImmediate(true);
		mainLayout.addComponent(chkSectionAll, "top:70.0px; left:415.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:110.0px;left:150.0px;");

		mainLayout.addComponent(new Label("__________________________________________________________________________________"), "top:120.0px;left:20.0px;right:20.0px;");
		mainLayout.addComponent(cButton,"top:150.opx; left:140.0px");
		return mainLayout;
	}
}
