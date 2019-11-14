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
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class RptMonthlyAttendance extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	ArrayList<Component> allComp = new ArrayList<Component>();

	private ComboBox cmbsection;
	
	private Label lblEmployee;
	private ComboBox cmbEmployee;
	private CheckBox chkempall;
	
	private Label lblMonth;
	private PopupDateField dMonth;

	private OptionGroup opgEmployee;
	private List<?> lstEmployee = Arrays.asList(new String[]{"Employee ID","Proximity ID","Employee Name"});

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();
	
	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});

	public RptMonthlyAttendance(SessionBean sessionBean) 
	{
		this.sessionBean=sessionBean;
		this.setCaption("MONTHLY EMPLOYEE ATTENDANCE :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);
		addSectionName();
		setEventAction();
		focusMove();
	}

	public void setEventAction()
	{
		dMonth.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbsection.removeAllItems();
				if(dMonth.getValue()!=null)
				{
					addSectionName();
				}
			}
		});
		
		cmbsection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmployee.removeAllItems();
				if(cmbsection.getValue()!=null)
				{
					addemployeeName();
				}
			}
		});
		
		chkempall.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(chkempall.booleanValue())
				{
					cmbEmployee.setValue(null);
					cmbEmployee.setEnabled(false);
				}
				else
					cmbEmployee.setEnabled(true);
			}
		});
		
		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				String section = "";
				String employeeName="";
				if(cmbsection.getValue()!=null ) 
				{
					section=cmbsection.getValue().toString().trim();
					if(chkempall.booleanValue()==true)
					{
						employeeName = "%";
					}
					else
					{
						employeeName = cmbEmployee.getValue().toString().trim(); 
					}

					reportShow(section,employeeName);
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

		opgEmployee.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmployee.removeAllItems();
				addemployeeName();
			}
		});
	}

	public void addSectionName()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vSectionId,vSectionName from tbEmployeeAttendanceFinal " +
					"where Month(dDate)=Month('"+dFormat.format(dMonth.getValue())+"') and " +
					"year(dDate)=year('"+dFormat.format(dMonth.getValue())+"') order by vSectionName";
			
			Iterator <?> itr=session.createSQLQuery(query).list().iterator();
			while(itr.hasNext())
			{
				Object [] element=(Object[])itr.next();
				cmbsection.addItem(element[0]);
				cmbsection.setItemCaption(element[0], element[1].toString());
			}
		}
		
		catch(Exception exp)
		{
			showNotification("addSectionName : ",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	
	public void addemployeeName()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "SELECT distinct eopi.vEmployeeId,vEmployeeCode,cast(eopi.vEmployeeCode as int) from tbEmpOfficialPersonalInfo eopi "
					+ "inner join tbEmpSectionInfo esin on eopi.vEmployeeID = esin.vEmployeeID where "
					+ "esin.vSectionId='"+cmbsection.getValue()+"' and eopi.bStatus = 1 order by cast(eopi.vEmployeeCode as int)";
			lblEmployee.setValue("Employee ID :");
			
			if(opgEmployee.getValue()=="Employee Name")
			{
				query = "SELECT distinct eopi.vEmployeeId,eopi.vEmployeeName from tbEmpOfficialPersonalInfo eopi "
					+ "inner join tbEmpSectionInfo esin on eopi.vEmployeeID = esin.vEmployeeID where "
					+ "esin.vSectionId='"+cmbsection.getValue()+"' and eopi.bStatus = 1 order by eopi.vEmployeeName";
				lblEmployee.setValue("Employee Name :");
			}
			
			else if(opgEmployee.getValue()=="Proximity ID")
			{
				query = "SELECT distinct eopi.vEmployeeId,vProximityID from tbEmpOfficialPersonalInfo eopi "
					+ "inner join tbEmpSectionInfo esin on eopi.vEmployeeID = esin.vEmployeeID where "
					+ "esin.vSectionId='"+cmbsection.getValue()+"' and eopi.bStatus = 1 order by eopi.vProximityID";
				lblEmployee.setValue("Proximity ID :");
			}
			List <?> list = session.createSQLQuery(query).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbEmployee.addItem(element[0]);
				cmbEmployee.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("addemployeeName",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void reportShow(String sectionName,String employeeName)
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		try
		{	
			String query = "select * from funMonthlyEmployeeAttendance('"+dFormat.format(dMonth.getValue())+"','"+employeeName+"','"+sectionName+"') order by vEmployeeCode";
			if(queryValueCheck(query))
			{
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("username", sessionBean.getUserName()+" "+sessionBean.getUserIp());
				hm.put("SysDate",reportTime.getTime);
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("month",new SimpleDateFormat("MMMMMM").format(dMonth.getValue()));
				hm.put("year",new SimpleDateFormat("yy").format(dMonth.getValue()));
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"hrm/jasper/Attendance/rptMonthlyAttendence.jasper",
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
		allComp.add(cmbEmployee);
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
		setWidth("460px");
		setHeight("300px");
		
		lblMonth = new Label("Month :");
		lblMonth.setImmediate(false);
		lblMonth.setWidth("-1px");
		lblMonth.setHeight("-1px");
		mainLayout.addComponent(lblMonth, "top:40.0px; left:30.0px;");

		dMonth = new PopupDateField();
		dMonth.setImmediate(true);
		dMonth.setWidth("140px");
		dMonth.setDateFormat("MMMMM-yyyy");
		dMonth.setValue(new java.util.Date());
		dMonth.setResolution(PopupDateField.RESOLUTION_MONTH);
		mainLayout.addComponent(dMonth, "top:38.0px; left:130.0px;");
		
		cmbsection=new ComboBox();
		cmbsection.setImmediate(true);
		cmbsection.setWidth("260.0px");
		cmbsection.setHeight("24.0px");
		mainLayout.addComponent(new Label("Section :"), "top:70.0px; left:30.0px;");
		mainLayout.addComponent(cmbsection, "top:68.0px;left:130.0px;");

		opgEmployee=new OptionGroup("",lstEmployee);
		opgEmployee.select("Employee ID");
		opgEmployee.setImmediate(true);
		opgEmployee.setStyleName("horizontal");
		mainLayout.addComponent(opgEmployee, "top:100.0px; left:50.0px;");
		
		// lblCategory
		lblEmployee = new Label();
		lblEmployee.setImmediate(false);
		lblEmployee.setWidth("100.0%");
		lblEmployee.setHeight("-1px");
		lblEmployee.setValue("Employee ID :");
		mainLayout.addComponent(lblEmployee,"top:130.0px; left:30.0px;");

		// cmbEmployee
		cmbEmployee = new ComboBox();
		cmbEmployee.setImmediate(false);
		cmbEmployee.setWidth("260px");
		cmbEmployee.setHeight("-1px");
		cmbEmployee.setNullSelectionAllowed(true);
		cmbEmployee.setImmediate(true);
		cmbEmployee.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbEmployee, "top:128.0px; left:130.0px;");

		//sectionAll
		chkempall = new CheckBox("All");
		chkempall.setHeight("-1px");
		chkempall.setWidth("-1px");
		chkempall.setImmediate(true);
		mainLayout.addComponent(chkempall, "top:130.0px; left:396.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:170.0px;left:130.0px;");
		
		mainLayout.addComponent(new Label("______________________________________________________________________________"), "top:200.0px; left:20.0px; right:20.0px;");
		mainLayout.addComponent(cButton,"top:230.opx; left:150.0px");
		return mainLayout;
	}
}
