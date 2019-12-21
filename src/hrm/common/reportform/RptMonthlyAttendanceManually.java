package hrm.common.reportform;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

import com.common.share.CommonButton;
import com.common.share.CommonMethod;
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
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class RptMonthlyAttendanceManually extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	ArrayList<Component> allComp = new ArrayList<Component>();

	private Label lblDepartment;
	private ComboBox cmbDepartment,cmbSection,cmbUnit;
	private CheckBox chkDepartmentAll,chkSectionAll;
	private Label lblDate;
	private PopupDateField dDate;
	private ComboBox cmbDate;

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();
	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});

	SimpleDateFormat dfYMD = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat dMonthFormat = new SimpleDateFormat("MM");
	SimpleDateFormat dYearFormat = new SimpleDateFormat("yyyy");
	private CommonMethod cm;
	private String menuId = "";
	public RptMonthlyAttendanceManually(SessionBean sessionBean,String menuId) 
	{
		this.sessionBean=sessionBean;
		this.setCaption("MONTHLY ATTENDANCE MANUALLY :: "+sessionBean.getCompany());
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		buildMainLayout();
		setContent(mainLayout);
		setEventAction();
		focusMove();
		authenticationCheck();
		cmbDateDataLoad();
	}
	private void authenticationCheck()
	{
		cm.checkFormAction(menuId);
		if(!sessionBean.isSuperAdmin())
		{
		if(!sessionBean.isAdmin())
		{
			if(!cm.isSave)
			{cButton.btnSave.setVisible(false);}
			if(!cm.isEdit)
			{cButton.btnEdit.setVisible(false);}
			if(!cm.isDelete)
			{cButton.btnDelete.setVisible(false);}
			if(!cm.isPreview)
			{cButton.btnPreview.setVisible(false);}
		}
		}
	}
	private void cmbDateDataLoad() {
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "select CONVERT(date,(SELECT DATEADD(s,-1,DATEADD(mm, DATEDIFF(m,0,dDate)+1,0))))date,CONVERT(varchar,DATENAME(MONTH,dDate)+'-'+DATENAME(YEAR,dDate))monthYY from tbAttendanceSummary order by date desc";
			List<?> list = session.createSQLQuery(sql).list();
			cmbDate.removeAllItems();
			for(Iterator<?> iter = list.iterator();iter.hasNext();)
			{
				Object element[] = (Object[]) iter.next();
				cmbDate.addItem(element[0].toString());
				cmbDate.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification(exp+"cmbDateDataLoad ",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void cmbUnitDataLoad() {
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "select distinct vUnitId,vUnitName from tbAttendanceSummary where MONTH(dDate)=MONTH('"+cmbDate.getValue()+"') and YEAR(dDate)=YEAR('"+cmbDate.getValue()+"')";
			List<?> list = session.createSQLQuery(sql).list();
			cmbUnit.removeAllItems();
			for(Iterator<?> iter = list.iterator();iter.hasNext();)
			{
				Object element[] = (Object[]) iter.next();
				cmbUnit.addItem(element[0].toString());
				cmbUnit.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification(exp+"cmbUnitDataLoad ",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	
	public void setEventAction()
	{
		cmbDate.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
				if(cmbDate.getValue()!=null)
				{
					cmbUnitDataLoad();
				}
			}
		});
		cmbUnit.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbUnit.getValue()!=null)
				{
					cmbDepartmentData();
				}
				else{
					cmbDepartment.setValue(null);
				}
			}
		});
		cmbDepartment.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbUnit.getValue()!=null)
				{
					if(cmbDepartment.getValue()!=null)
					{
						cmbSectionData();
					}
				}
			}
		});
		chkDepartmentAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbUnit.getValue()!=null)
				{
					if(chkDepartmentAll.booleanValue())
					{
						cmbDepartment.setEnabled(false);
						cmbSectionData();
					}
					else
					{
						cmbDepartment.setEnabled(true);
					}
				}
			}
		});
		chkSectionAll.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(cmbUnit.getValue()!=null)
				{
					if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
					{
						cmbSection.setEnabled(false);
						cmbSectionData();
					}
					else
					{
						cmbSection.setEnabled(true);
					}
				}
			}
		});
		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbUnit.getValue()!=null)
				{
					if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
					{
						if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
						{
							reportShow();
						}
						else
						{
							showNotification("Warning","Select Section",Notification.TYPE_WARNING_MESSAGE);
							cmbDepartment.focus();
						}
					}
					else
					{
						showNotification("Warning","Select Department",Notification.TYPE_WARNING_MESSAGE);
						cmbDepartment.focus();
					}
				}
				else
				{
					showNotification("Warning","Select Project",Notification.TYPE_WARNING_MESSAGE);
					cmbUnit.focus();
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
		chkDepartmentAll.addListener(new Listener()
		{
			public void componentEvent(Event event)
			{
				if(chkDepartmentAll.booleanValue())
				{
					cmbDepartment.setValue(null);
					cmbDepartment.setEnabled(false);
				}
				else
				{
					cmbDepartment.setEnabled(true);
				}
			}
		});
	}

	public void cmbDepartmentData()
	{
		cmbDepartment.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vDepartmentId,vDepartmentName from tbAttendanceSummary "
					+ " where MONTH(dDate) = MONTH('"+cmbDate.getValue()+"') and "
					+ " YEAR(dDate) = YEAR('"+cmbDate.getValue()+"') and  vUnitId='"+cmbUnit.getValue().toString()+"' "
					+ " order by vDepartmentName";
			
			List <?> list = session.createSQLQuery(query).list();
			if(!list.isEmpty())
			{
				for(Iterator <?> iter=list.iterator();iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();
					cmbDepartment.addItem(element[0]);
					cmbDepartment.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbDepartmentData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			session.close();
		}
	}
	public void cmbSectionData()
	{
		String dept="%";
		if(!chkDepartmentAll.booleanValue())
		{
			dept=cmbDepartment.getValue().toString();
		}
		cmbSection.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vSectionId,vSectionName from tbAttendanceSummary " +
					" where MONTH(dDate) = MONTH('"+cmbDate.getValue()+"') " +
					" and YEAR(dDate) = YEAR('"+cmbDate.getValue()+"') and vUnitId='"+cmbUnit.getValue().toString()+"' " +
					" and vDepartmentId like '"+dept+"' order by vSectionName";
			
			List <?> list = session.createSQLQuery(query).list();
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
			showNotification("cmbDepartmentData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			session.close();
		}
	}

	private void reportShow()
	{
		String dept="%",section="%";
		if(!chkDepartmentAll.booleanValue())
		{
			dept=cmbDepartment.getValue().toString();
		}
		if(!chkSectionAll.booleanValue())
		{
			section=cmbSection.getValue().toString();
		}
		
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());

		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select ats.vUnitId,ats.vUnitName,ats.vEmployeeName,ats.vDesignationName,ats.vSectioNname,ats.vDepartmentName,iTotalMonth,iHoliday,iTotalWorkingDay,iPresentDay,iAbsentDay,iHolidayDuty,"
					+ " iLeaveWithPay,iLeaveWithoutPay,iOtHour,iOtMinute,ats.vDepartmentId,ats.vSectionId from tbAttendanceSummary ats "
					+ "inner join tbDesignationInfo ds on ats.vDesignationName=ds.vDesignation "
					+ "inner join tbEmpOfficialPersonalInfo toi on ats.vEmployeeName=toi.vEmployeeName  "
					+ "where ats.vUnitId ='"+cmbUnit.getValue().toString()+"' and ats.vDepartmentId like '"+dept+"' and ats.vSectionId like '"+section+"' "
					+ " and MONTH(dDate) = MONTH('"+cmbDate.getValue()+"') and YEAR(dDate) = YEAR('"+cmbDate.getValue()+"') "
					+ " order by vDepartmentName,vSectionName,ds.iRank,toi.dJoiningDate";
			
			System.out.println("Report: "+query);

			if(queryValueCheck(query))
			{
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				hm.put("company", cmbUnit.getItemCaption(cmbUnit.getValue()));
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("developer", sessionBean.getDeveloperAddress());
				hm.put("username", sessionBean.getUserName()+" "+sessionBean.getUserIp());
				hm.put("SysDate",reportTime.getTime);
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("date", dDate.getValue());
				hm.put("sql", query);
				
				Window win = new ReportViewer(hm,"report/account/hrmModule/rptMonthlyAttendanceSummary.jasper",
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
		finally
		{
			session.close();
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
		finally
		{
			session.close();
		}
		return false;
	}

	private void focusMove()
	{
		allComp.add(cmbDepartment);
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
		setHeight("250px");

		// lblAsOnDate
		lblDate = new Label("Month :");
		lblDate.setImmediate(true);
		lblDate.setWidth("100%");
		lblDate.setHeight("-1px");
		mainLayout.addComponent(lblDate, "top:10.0px; left:30.0px;");

		// asOnDate
		dDate = new PopupDateField();
		dDate.setWidth("130px");
		dDate.setDateFormat("MMMMM-yyyy");
		dDate.setValue(new java.util.Date());
		dDate.setResolution(PopupDateField.RESOLUTION_MONTH);
		mainLayout.addComponent(dDate, "top:8.0px; left:140.0px;");
		dDate.setVisible(false);
		
		cmbDate=new ComboBox();
		cmbDate.setWidth("140px");
		cmbDate.setHeight("-1px");
		cmbDate.setImmediate(true);
		mainLayout.addComponent(cmbDate, "top:8px;left:140.0px");
		
		cmbUnit=new ComboBox();
		cmbUnit.setWidth("250.0px");
		cmbUnit.setHeight("-1px");
		cmbUnit.setImmediate(true);
		mainLayout.addComponent(new Label("Project : "), "top:40.0px;left:30.0px;");
		mainLayout.addComponent(cmbUnit, "top:38.0px;left:140.0px");
		
		// lblCategory
		lblDepartment = new Label();
		lblDepartment.setImmediate(false);
		lblDepartment.setWidth("100.0%");
		lblDepartment.setHeight("-1px");
		lblDepartment.setValue("Department Name :");
		mainLayout.addComponent(lblDepartment,"top:70.0px; left:30.0px;");

		// cmbDepartment
		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(false);
		cmbDepartment.setWidth("260px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setNullSelectionAllowed(true);
		cmbDepartment.setImmediate(true);
		mainLayout.addComponent(cmbDepartment, "top:68.0px; left:140.0px;");

		//chkDepartmentAll
		chkDepartmentAll = new CheckBox("All");
		chkDepartmentAll.setHeight("-1px");
		chkDepartmentAll.setWidth("-1px");
		chkDepartmentAll.setImmediate(true);
		mainLayout.addComponent(chkDepartmentAll, "top:70.0px; left:405.0px;"); 
		

		// cmbSection
		cmbSection = new ComboBox();
		cmbSection.setImmediate(false);
		cmbSection.setWidth("260px");
		cmbSection.setHeight("-1px");
		cmbSection.setNullSelectionAllowed(true);
		cmbSection.setImmediate(true);
		mainLayout.addComponent(new Label("Section Name"),"top:100.0px; left:30.0px;");
		mainLayout.addComponent(cmbSection, "top:98.0px; left:140.0px;");

		//chkSectionAll
		chkSectionAll = new CheckBox("All");
		chkSectionAll.setHeight("-1px");
		chkSectionAll.setWidth("-1px");
		chkSectionAll.setImmediate(true);
		mainLayout.addComponent(chkSectionAll, "top:100.0px; left:405.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:130.0px;left:170.0px;");
		RadioBtnGroup.setVisible(false);

		//mainLayout.addComponent(new Label("_________________________________________________________________________________________"), "top:160.0px;left:20.0px;right:20.0px;");
		mainLayout.addComponent(cButton,"bottom:15px; left:160.0px");
		return mainLayout;
	}
}
