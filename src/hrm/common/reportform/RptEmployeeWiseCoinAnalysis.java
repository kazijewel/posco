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
import com.vaadin.ui.Button.ClickListener;

public class RptEmployeeWiseCoinAnalysis extends Window
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblMonth;
	private Label lblSection;
	private Label lblEmployee;

	private PopupDateField dMonth;
	private ComboBox cmbSection;
	private ComboBox cmbEmployee;
	private CheckBox chkEmployeeAll;

	ArrayList<Component> allComp = new ArrayList<Component>();

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private SimpleDateFormat monthformat = new SimpleDateFormat("MMMMMM");
	private SimpleDateFormat yearformat = new SimpleDateFormat("yyyy");
	private ReportDate reportTime = new ReportDate();

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});

	public RptEmployeeWiseCoinAnalysis(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setCaption("EMPLOYEE WISE COIN ANALYSIS :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);

		cmbSectionAddData();
		setEventAction();
	}

	public void cmbSectionAddData()
	{
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery(" SELECT AutoID,SectionName from tbSectionInfo order by AutoID ").list();
			for(Iterator iter=list.iterator();iter.hasNext();)
			{

				Object[] element = (Object[]) iter.next();
				cmbSection.addItem(element[0]);
				cmbSection.setItemCaption(element[0], (String) element[1]);
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public void cmbEmpoyeeAddData()
	{

		Transaction tx=null;
		try
		{
			cmbEmployee.removeAllItems();
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery("select vProximityId,vEmployeeName from tbEmployeeInfo where vSectionId='"+cmbSection.getValue()+"'").list();
			for(Iterator iter=list.iterator();iter.hasNext();)
			{

				Object[] element = (Object[]) iter.next();
				cmbEmployee.addItem(element[0]);
				cmbEmployee.setItemCaption(element[0], (String) element[1]);
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public void setEventAction()
	{
		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbSection.getValue()!=null)
				{
					if(cmbEmployee.getValue()!=null || chkEmployeeAll.booleanValue()==true)
					{
						getAllData();
					}
					else
					{
						showNotification("Warniung","Select Employee Name or Click the Check Box!!!",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
					showNotification("Warning", "Select Section Name!!!", Notification.TYPE_WARNING_MESSAGE);
			}
		});

		cButton.btnExit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

		chkEmployeeAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(chkEmployeeAll.booleanValue()==true)
				{	
					cmbEmployee.setValue(null);
					cmbEmployee.setEnabled(false);
				}
				else
				{
					cmbEmployee.setEnabled(true);
				}
			}
		});

		cmbSection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbSection.getValue()!=null)
				{
					cmbEmpoyeeAddData();
				}
			}
		});
	}

	private void getAllData()
	{
		String sectionValue = cmbSection.getValue().toString();
		String employeeValue="";
		if(chkEmployeeAll.booleanValue()==true)
		{
			employeeValue = "%";
		}
		else
		{
			employeeValue = cmbEmployee.getValue().toString();
		}

		reportShow(sectionValue,employeeValue);
	}

	private void reportShow(String Section,String Employee)
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		String query=null;
		String ID="";
		Date JOD=null;
		String Deg="";
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			/*List lst = session.createSQLQuery("select employeeCode,di.designationName,dJoiningDate from tbEmployeeInfo ei inner join tbDesignationInfo di on ei.vDesignationId=di.designationId  where ei.vProximityId='"+cmbEmployee.getValue()+"'").list();
			for(Iterator iter=lst.iterator();iter.hasNext();)
			{  
				Object[] element = (Object[]) iter.next();
				ID= element[0].toString();
				JOD=(Date) element[2];
				Deg=element[1].toString();
			}*/

			HashMap hm = new HashMap();

			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			hm.put("SysDate",reportTime.getTime);
			hm.put("section",cmbSection.getItemCaption(cmbSection.getValue()));
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("month", new SimpleDateFormat("MMMMM").format(dMonth.getValue())+","+new SimpleDateFormat("yyyy").format(dMonth.getValue()));
			/*hm.put("ID",ID);
			hm.put("PID",cmbEmployee.getValue());
			hm.put("JOD", JOD);
			hm.put("Deg",Deg);*/
			

			//query = " select * from funMonthWiseOtStatement('"+dateformat.format(dMonth.getValue())+"','"+Section+"') ";

			/*query="select ts.year,ts.vMonthName,ts.empId,ts.empCode,ts.empName,ts.designation,ts.Gross,ts.FridayAllowance,ts.otHour, "+
					"ts.otRate,funma.Section,funMA.fridayOTHour from tbSalary ts inner join funCalcMonthlyAttendance('"+dateformat.format(dMonth.getValue())+"','%','"+Section+"') funMA on ts.empCode=funMA.empCode "+
					"where funMA.txtDate=dateadd(dd,-datepart(dd,'"+dateformat.format(dMonth.getValue())+"'),dateadd(mm,1,'"+dateformat.format(dMonth.getValue())+"')) and ts.year= year('"+dateformat.format(dMonth.getValue())+"') and ts.vMonthName= DateName(mm,'"+dateformat.format(dMonth.getValue())+"')";*/

			query="select * from dbo.funEmpCoinAnalysis('"+Section+"','"+Employee+"','"+monthformat.format(dMonth.getValue())+"','"+yearformat.format(dMonth.getValue())+"')";

			if(queryValueCheck(query))
			{
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/account/hrmModule/EmployeeWiseCoinAnalysis.jasper",
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
		setHeight("250px");

		// lblSection
		lblSection = new Label("Division Name :");
		lblSection.setImmediate(true);
		lblSection.setWidth("100.0%");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection,"top:30.0px; left:30.0px;");

		// cmbSection
		cmbSection = new ComboBox();
		cmbSection.setImmediate(false);
		cmbSection.setWidth("200px");
		cmbSection.setHeight("-1px");
		cmbSection.setNullSelectionAllowed(true);
		cmbSection.setImmediate(true);
		mainLayout.addComponent(cmbSection, "top:28.0px; left:130.0px;");

		lblEmployee = new Label("Employee Name :");
		lblEmployee.setImmediate(true);
		lblEmployee.setWidth("100.0%");
		lblEmployee.setHeight("-1px");
		mainLayout.addComponent(lblEmployee,"top:60.0px; left:30.0px;");

		// cmbSection
		cmbEmployee = new ComboBox();
		cmbEmployee.setImmediate(true);
		cmbEmployee.setWidth("200px");
		cmbEmployee.setHeight("-1px");
		cmbEmployee.setNullSelectionAllowed(true);
		cmbEmployee.setImmediate(true);
		mainLayout.addComponent(cmbEmployee, "top:58.0px; left:130.0px;");

		// lblMonth
		lblMonth = new Label("Month :");
		lblMonth.setImmediate(true);
		lblMonth.setWidth("100.0%");
		lblMonth.setHeight("-1px");
		mainLayout.addComponent(lblMonth,"top:90.0px; left:30.0px;");

		// dMonth
		dMonth = new PopupDateField();
		dMonth.setImmediate(true);
		dMonth.setWidth("100px");
		dMonth.setHeight("-1px");
		dMonth.setDateFormat("MMM-yyyy");
		dMonth.setValue(new java.util.Date());
		dMonth.setResolution(PopupDateField.RESOLUTION_MONTH);
		mainLayout.addComponent(dMonth, "top:88.0px; left:130.0px;");

		// chkEmployeeAll
		chkEmployeeAll = new CheckBox("All");
		chkEmployeeAll.setHeight("-1px");
		chkEmployeeAll.setWidth("-1px");
		chkEmployeeAll.setImmediate(true);
		mainLayout.addComponent(chkEmployeeAll, "top:60.0px; left:336.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:120.0px;left:130.0px;");

		mainLayout.addComponent(cButton,"top:150.opx; left:120.0px");

		return mainLayout;
	}
}
