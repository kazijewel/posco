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
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class RptEmployeeAttendance extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	ArrayList<Component> allComp = new ArrayList<Component>();

	private Label lblSection;
	private ComboBox cmbSection;

	private Label lblEmployee;
	private ComboBox cmbEmployee;
	private CheckBox EmpAll;

	private Label lblMonth;
	private PopupDateField dMonth;

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});

	public RptEmployeeAttendance(SessionBean sessionBean) 
	{
		this.sessionBean=sessionBean;
		this.setCaption("Employee wise Monthly Attendance :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		cmbSectionAddData();
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
				if(cmbSection.getValue()!=null )
				{
					if(cmbEmployee.getValue()!=null || EmpAll.booleanValue()==true)
					{
						reportShow();
					}
					else
					{
						showNotification("Warning","Select Employee!!!",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					showNotification("Warning","Select Section!!!",Notification.TYPE_WARNING_MESSAGE);
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

		dMonth.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(dMonth.getValue()!=null)
				{
					cmbSection.removeAllItems();
					cmbEmployee.removeAllItems();
					cmbSectionAddData();
				}
			}
		});

		cmbSection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbSection.getValue()!=null)
				{
					cmbEmployee.removeAllItems();
					addEmployeeName();
				}
			}
		});

		EmpAll.addListener(new Listener()
		{
			public void componentEvent(Event event)
			{
				if(EmpAll.booleanValue()==true)
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
	}

	public void cmbSectionAddData()
	{
		cmbSection.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> list=session.createSQLQuery("SELECT Distinct vSectionId,vSectionName from tbEmployeeAttendanceFinal " +
					"where MONTH(dDate)=MONTH('"+dFormat.format(dMonth.getValue())+"') and " +
					"YEAR(dDate)=YEAR('"+dFormat.format(dMonth.getValue())+"') order by vSectionName ").list();
			if(!list.isEmpty())
			{
				for(Iterator <?> iter=list.iterator();iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();
					cmbSection.addItem(element[0]);
					cmbSection.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("cmbSectionAddData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void addEmployeeName()
	{
		cmbEmployee.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> list = session.createSQLQuery("SELECT Distinct vEmployeeId,vEmployeeName,vFingerID from " +
					"tbEmployeeAttendanceFinal where MONTH(dDate)=MONTH('"+dFormat.format(dMonth.getValue())+"') and " +
					"YEAR(dDate)=YEAR('"+dFormat.format(dMonth.getValue())+"') and " +
					"vSectionId='"+cmbSection.getValue()+"' order by vFingerID").list();

			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbEmployee.addItem(element[0]);
				cmbEmployee.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("addEmployeeName",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void reportShow()
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		try
		{
			String query = "SELECT  vEmployeeID,vEmployeeCode,vProximityID,vFingerID,vEmployeeName,vEmployeeType,vContactNo,"
					+ "dJoinDate,vShiftId,vShiftName,vDesignationID,vDesignation,iDesignationSerial,vSectionID,vSection,"
					+ "dTxtDate,dInTime,dOutTime,iHour,iMin,iSec,iOTHour,iOTMin,iOTSec,otStatus,vTxtStatus,vTxtRemarks,"
					+ "iTotalHour,iTotalMin,iTotalSec,iTotalOTHour,iTotalOTMin,iTotalOTSec,iTotalFridayOTHour,"
					+ "iTotalFridayOTMin,iTotalFridayOTSec,skLeaveCount,clLeaveCount,elLeaveCount,mlLeaveCount,holidayCount,"
					+ "holidayDuty,presentCount,absentCount,notCount,lateCount,lateCountOfAbsent,iTotalFixedOTHour,iTotalFixedOTMin,iTotalFixedOTSec FROM funMonthlyEmployeeAttendance"
					+ "('"+dFormat.format(dMonth.getValue())+"','"+(cmbEmployee.getValue()!=null?cmbEmployee.getValue():"%")+"',"
					+ "'"+cmbSection.getValue()+"') order by vEmployeeName,vEmployeeID,dTxtDate";
			System.out.println("OKKK"+query);
			
			if(queryValueCheck(query))
			{
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("username", sessionBean.getUserName()+" "+sessionBean.getUserIp());
				hm.put("SysDate",reportTime.getTime);
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/account/hrmModule/rptMonthlyAttendence.jasper",
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
			this.getParent().showNotification("reportShow "+exp,Notification.TYPE_ERROR_MESSAGE);
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
		setWidth("400px");
		setHeight("250px");

		lblMonth = new Label("Month :");
		lblMonth.setImmediate(false);
		lblMonth.setWidth("-1px");
		lblMonth.setHeight("-1px");
		mainLayout.addComponent(lblMonth, "top:30.0px; left:30.0px;");

		dMonth = new PopupDateField();
		dMonth.setImmediate(true);
		dMonth.setWidth("140px");
		dMonth.setDateFormat("MMMMM-yyyy");
		dMonth.setValue(new java.util.Date());
		dMonth.setResolution(PopupDateField.RESOLUTION_MONTH);
		mainLayout.addComponent(dMonth, "top:28.0px; left:130.0px;");

		// lblCategory
		lblSection = new Label();
		lblSection.setImmediate(false);
		lblSection.setWidth("100.0%");
		lblSection.setHeight("-1px");
		lblSection.setValue("Division Name :");
		mainLayout.addComponent(lblSection,"top:55.0px; left:30.0px;");

		// cmbEmployee
		cmbSection = new ComboBox();
		cmbSection.setImmediate(false);
		cmbSection.setWidth("200px");
		cmbSection.setHeight("-1px");
		cmbSection.setNullSelectionAllowed(true);
		cmbSection.setImmediate(true);
		mainLayout.addComponent(cmbSection, "top:53.0px; left:130.0px;");


		lblEmployee = new Label();
		lblEmployee.setImmediate(false);
		lblEmployee.setWidth("100.0%");
		lblEmployee.setHeight("-1px");
		lblEmployee.setValue("Employee Name :");
		mainLayout.addComponent(lblEmployee,"top:80.0px; left:30.0px;");

		// cmbEmployee
		cmbEmployee = new ComboBox();
		cmbEmployee.setImmediate(false);
		cmbEmployee.setWidth("200px");
		cmbEmployee.setHeight("-1px");
		cmbEmployee.setNullSelectionAllowed(true);
		cmbEmployee.setImmediate(true);
		mainLayout.addComponent(cmbEmployee, "top:78.0px; left:130.0px;");

		//EmpAll
		EmpAll = new CheckBox("All");
		EmpAll.setHeight("-1px");
		EmpAll.setWidth("-1px");
		EmpAll.setImmediate(true);
		mainLayout.addComponent(EmpAll, "top:78.0px; left:336.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:105.0px;left:130.0px;");

		mainLayout.addComponent(cButton,"top:135.opx; left:120.0px");

		return mainLayout;
	}
}
