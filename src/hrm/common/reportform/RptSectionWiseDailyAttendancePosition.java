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

public class RptSectionWiseDailyAttendancePosition extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	ArrayList<Component> allComp = new ArrayList<Component>();

	private Label lblDate;
	private PopupDateField dDate;

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});

	SimpleDateFormat dfYMD = new SimpleDateFormat("yyyy-MM-dd");

	public RptSectionWiseDailyAttendancePosition(SessionBean sessionBean) 
	{
		this.sessionBean=sessionBean;
		this.setCaption("DAILY ATTENDANCE POSITION :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);
		setEventAction();
		focusMove();
	}

	public void setEventAction()
	{
		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				
					reportShow();
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
			hm.put("date", new SimpleDateFormat().format(dDate.getValue()));

			hm.put("username", sessionBean.getUserName()+" "+sessionBean.getUserIp());

			hm.put("SysDate",reportTime.getTime);
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("date", new SimpleDateFormat("dd-MM-yyyy").format(dDate.getValue()));

			/*query="select vProximityId,iFingerID,vEmployeeName,vDesignationId,(select designationName from tbDesignationInfo where "
				 +"designationId=ein.vDesignationId ) as Designation,(select SectionName from tbSectionInfo where "
				 +"vSectionId=ein.vSectionId) as Section from tbEmployeeInfo ein where vProximityId not in "
				 +"( select ein.vProximityId from tbEmployeeInfo ein inner join "
				 +"tbEmployeeAttendance ea on ein.vProximityId=ea.vEmployeeId inner join tbDesignationInfo din on "
				 +"din.designationId=ein.vDesignationId where Convert(date,ea.dAttDate,105) = '"+dfYMD.format(dDate.getValue())+"') and vSectionId like '"+sectionId+"' order by vDesignationId";*/

			/*query="select distinct sein.SectionName,COUNT(vProximityId) totalEmp,(select COUNT(empCode) from funCalcEmployeeAttendance"+
				  "('"+dfYMD.format(dDate.getValue())+"','"+dfYMD.format(dDate.getValue())+"','%','"+sectionId+"') fcea where fcea.sectionId=ein.vSectionId) totalPre,"+
				  "ISNULL((select COUNT(vEmployeeId) from tbEmployeeLeave el where "+
				  "el.vSectionID=ein.vSectionId and el.dSenctionFrom<='"+dfYMD.format(dDate.getValue())+"' and dSenctionTo>='"+dfYMD.format(dDate.getValue())+"' group by "+
				  "el.vSectionID,el.vEmployeeId),0) totalLeave from tbEmployeeInfo ein inner join tbSectionInfo sein on ein.vSectionId=sein.AutoID "+
				  "where ein.vsectionId like '"+sectionId+"' group by vProximityId,vSectionId,SectionName";*/

			  query="select distinct vSectionId,(select sinf.SectionName from tbSectionInfo sinf where " +
					"sinf.AutoID=ein.vSectionId) sectionName,COUNT(ein.iFingerID) totalEmp," +
					"(select COUNT(fea.vFigId) from [funAttendanceReport] ('"+dfYMD.format(dDate.getValue())+"','%','%') " +
					"fea where ein.vSectionId=fea.sectionIdDb)totalPre,(select count(el.vEmployeeId) " +
					"from tbEmployeeLeave el where el.vSectionID=ein.vSectionId and el.dSenctionFrom<='"+dfYMD.format(dDate.getValue())+"' " +
					"and dSenctionTo>='"+dfYMD.format(dDate.getValue())+"') totalLeave from tbEmployeeInfo ein group by  ein.vSectionId";

			System.out.println(query);
			if(queryValueCheck(query))
			{
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/account/hrmModule/rptDailyAttendencePosition.jasper",
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
		setWidth("350px");
		setHeight("200px");

		// lblAsOnDate
		lblDate = new Label("Date :");
		lblDate.setImmediate(true);
		lblDate.setWidth("100%");
		lblDate.setHeight("-1px");
		mainLayout.addComponent(lblDate, "top:30.0px; left:80.0px;");

		// asOnDate
		dDate = new PopupDateField();
		dDate.setWidth("110px");
		dDate.setDateFormat("dd-MM-yyyy");
		dDate.setValue(new java.util.Date());
		dDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dDate, "top:28.0px; left:120.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:60.0px;left:120.0px;");

		mainLayout.addComponent(cButton,"top:90.opx; left:90.0px");

		return mainLayout;
	}
}
