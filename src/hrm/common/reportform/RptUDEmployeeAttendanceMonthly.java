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

public class RptUDEmployeeAttendanceMonthly extends Window {

	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblSection;
	private Label lblMonth;

	private Label lblEmpID;
	private ComboBox cmbEmpID;

	private ComboBox cmbSection;
	private PopupDateField dMonth;

	//private CheckBox chkallemp;
	//private CheckBox chkallsection;

	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dMonthFormat = new SimpleDateFormat("dd-MM-yyyy");

	ArrayList<Component> allComp = new ArrayList<Component>();

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});

	public RptUDEmployeeAttendanceMonthly(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setCaption("SECTION WISE MONTHLY ATTENDANCE UPDATE :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);

		cmbSectionAddData();
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
			List list=session.createSQLQuery(" SELECT Distinct vSectionId,vSectionName from tbUdEmployeeAttendance where  MONTH(dAttDate)=MONTH('"+dFormat.format(dMonth.getValue())+"') and YEAR(dAttDate)=YEAR('"+dFormat.format(dMonth.getValue())+"') order by vSectionId ").list();
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

	private void EmployeeDataAdd(String Section)
	{

		cmbEmpID.removeAllItems();
		Transaction tx=null;

		try
		{
			String query=" select Distinct iFingerId,vEmployeeName from tbUdEmployeeAttendance where vSectionId like '"+Section+"' ";
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List lst=session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				Iterator itr=lst.iterator();
				while(itr.hasNext())
				{
					Object [] element=(Object[])itr.next();
					cmbEmpID.addItem(element[0]);
					cmbEmpID.setItemCaption(element[0], element[1].toString());
				}
			}
			else
				showNotification("Warning","No Employee Found!!!",Notification.TYPE_WARNING_MESSAGE);

		}
		catch (Exception exp)
		{

			showNotification("EmployeeDataAdd",exp.toString(),Notification.TYPE_ERROR_MESSAGE);

		}

	}

	public void setEventAction()
	{
		
		dMonth.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(dMonth.getValue()!=null)
				{
					cmbSection.removeAllItems();
					cmbEmpID.removeAllItems();
					cmbSectionAddData();
				}
			}
		});
		
		
		cmbSection.addListener(new ValueChangeListener()
		{

			public void valueChange(ValueChangeEvent event)
			{

				if(cmbSection.getValue()!=null)
					EmployeeDataAdd(cmbSection.getValue().toString());

			}
		});

/*		chkallsection.addListener(new ValueChangeListener()
		{

			@Override
			public void valueChange(ValueChangeEvent event)
			{

				if(chkallsection.booleanValue())
				{
					cmbSection.setValue(null);
					cmbSection.setEnabled(false);
					EmployeeDataAdd("%");
				}
				else
				{
					cmbSection.setEnabled(true);
					cmbEmpID.removeAllItems();
				}

			}
		});*/

		/*chkallemp.addListener(new ValueChangeListener()
		{

			@Override
			public void valueChange(ValueChangeEvent event)
			{

				if(chkallemp.booleanValue())
				{
					cmbEmpID.setValue(null);
					cmbEmpID.setEnabled(false);
				}
				else
					cmbEmpID.setEnabled(true);
			}
		});*/

		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(dMonth.getValue()!=null)
				{
					if(cmbSection.getValue()!=null) //|| chkallsection.booleanValue())
					{
						if(cmbEmpID.getValue()!=null) //|| chkallemp.booleanValue())
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
		String section="";
		String employee="";
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			/*if(chkallsection.booleanValue())
				section="%";
			else*/
				section=cmbSection.getValue().toString();

			/*if(chkallemp.booleanValue())
				employee="%";
			else*/
				employee=cmbEmpID.getValue().toString();

			HashMap hm = new HashMap();

			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			hm.put("section",cmbSection.getItemCaption(cmbSection.getValue()));
			hm.put("from",dMonth.getValue());
			hm.put("SysDate",reportTime.getTime);
			hm.put("logo", sessionBean.getCompanyLogo());

			query="select dAttDate,ua.vEmployeeId,ua.iFingerId,ua.vEmployeeName,vDesignation,ua.vSectionId," +
					"vSectionName,dAttInTime,dAttOutTime,permittedBy,vReason,udFlag,vPAFlag,vUserId,vUserIP,dEntryTime," +
					"(case when dAttInTime='1900-01-01 00:00:00' or dAttOutTime='1900-01-01 00:00:00' then '00:00:00' end) as tot," +
					"dJoiningDate from tbUdEmployeeAttendance ua inner join tbEmployeeInfo ei on ei.employeeCode=ua.vEmployeeId  " +
					"where ua.vSectionId like '"+section+"' "+
					"and ua.iFingerId like '"+employee+"' and month(dAttDate)=month('"+dFormat.format(dMonth.getValue())+"') " +
					"order by ua.vSectionId,vEmployeeId,dAttDate";

			System.out.println("Query"+query);

			if(queryValueCheck(query))
			{
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/account/hrmModule/rptMonthlyEditAttendence.jasper",
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
		allComp.add(dMonth);
		allComp.add(cmbSection);
		allComp.add(cmbEmpID);
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
		setWidth("475px");
		setHeight("270px");

		// lblFromDate
		lblMonth = new Label("Month :");
		lblMonth.setImmediate(false);
		lblMonth.setWidth("100.0%");
		lblMonth.setHeight("-1px");
		mainLayout.addComponent(lblMonth,"top:30.0px; left:50.0px;");

		// dFrom
		dMonth = new PopupDateField();
		dMonth.setImmediate(true);
		dMonth.setWidth("160px");
		dMonth.setHeight("-1px");
		dMonth.setDateFormat("MMMM-yyyy");
		dMonth.setResolution(PopupDateField.RESOLUTION_MONTH);
		dMonth.setValue(new java.util.Date());
		mainLayout.addComponent(dMonth, "top:28.0px; left:140.0px;");

		// lblSection
		lblSection = new Label("Division Name :");
		lblSection.setImmediate(false);
		lblSection.setWidth("100.0%");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection,"top:60.0px; left:50.0px;");

		// cmbSection
		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("260px");
		cmbSection.setHeight("-1px");
		cmbSection.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbSection, "top:58.0px; left:140.0px;");

		/*chkallsection=new CheckBox("All");
		chkallsection.setImmediate(true);
		mainLayout.addComponent(chkallsection, "top:60.0px;left:415.0px;");
*/
		//lblEmpID
		lblEmpID=new Label("Employee Name : ");
		mainLayout.addComponent(lblEmpID, "top:90.0px;left:50.0px;");

		//cmbEmpID
		cmbEmpID=new ComboBox();
		cmbEmpID.setImmediate(true);
		cmbEmpID.setWidth("260px");
		cmbEmpID.setHeight("-1px");
		mainLayout.addComponent(cmbEmpID, "top:88.0px;left:140.0px;");

		/*chkallemp=new CheckBox("All");
		chkallemp.setImmediate(true);
		mainLayout.addComponent(chkallemp, "top:90.0px;left:415.0px;");
*/
		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:120.0px;left:140.0px;");

		mainLayout.addComponent(new Label("_________________________________________________________________________"), "top:165.0px;right:20.0px;left:20.0px;");		
		mainLayout.addComponent(cButton,"top:150.opx; left:160.0px");

		return mainLayout;
	}
}