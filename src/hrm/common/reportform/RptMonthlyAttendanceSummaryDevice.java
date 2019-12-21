package hrm.common.reportform;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class RptMonthlyAttendanceSummaryDevice extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	ArrayList<Component> allComp = new ArrayList<Component>();

	private Label lblSection;
	private ComboBox cmbUnit;
	private ComboBox cmbDepartment,cmbMonth,cmbSection;
	private CheckBox chkDepartmentAll,chkSectionAll;
	private Label lblDate;

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();
	private OptionGroup RadioBtnGroup;
	private static final List<String> lstType=Arrays.asList(new String[]{"PDF","Others"});

	private OptionGroup reportType;
	SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat dMonthFormat = new SimpleDateFormat("MM");
	SimpleDateFormat dYearFormat = new SimpleDateFormat("yyyy");
	SimpleDateFormat dfMonthYear = new SimpleDateFormat("MMMMM-yyyy");

	private CommonMethod cm;
	private String menuId = "";
	public RptMonthlyAttendanceSummaryDevice(SessionBean sessionBean,String menuId) 
	{
		this.sessionBean=sessionBean;
		this.setCaption("MONTHLY ATTENDANCE SUMMARY :: "+sessionBean.getCompany());
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		this.setWidth("475px");
		this.setHeight("300px");
		buildMainLayout();
		setContent(mainLayout);
		setEventAction();
		focusMove();
		authenticationCheck();
		cmbMonthDataLoad();
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
	private void cmbMonthDataLoad()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct CONVERT(date, (SELECT DATEADD(s,-1,DATEADD(mm, DATEDIFF(m,0,dDate)+1,0)))) dDate,CONVERT(varchar,DATENAME(MONTH,dDate)+'-'+DATENAME(YEAR,dDate))monthyear from tbEmployeeAttendanceFinal order by dDate desc";
			List <?> list = session.createSQLQuery(query).list();

			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbMonth.addItem(element[0]);
				cmbMonth.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbMonthDataLoad",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	public void setEventAction()
	{
		cmbMonth.addListener(new ValueChangeListener()
		{	
			public void valueChange(ValueChangeEvent event)
			{
				cmbUnit.removeAllItems();
				if(cmbMonth.getValue()!=null)
					cmbUnitdataload();
			}
		});

		cmbUnit.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbUnit.getValue()!=null)
				{
					cmbDepartmentdataload();
				}
			}
		});
		cmbDepartment.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
				if(cmbDepartment.getValue()!=null)
				{
					cmbSectiondataload();
				}
			}
		});
		chkDepartmentAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSection.removeAllItems();
				if(cmbUnit.getValue()!=null)
				{
					if(chkDepartmentAll.booleanValue())
					{
						cmbDepartment.setValue(null);
						cmbDepartment.setEnabled(false);
						cmbSectiondataload();
					}
					else
					{
						cmbDepartment.setEnabled(true);
					}
				}
				else
				{
					chkDepartmentAll.setValue(false);
				}
			}
		});
		chkSectionAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				
				if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
				{
					if(chkSectionAll.booleanValue())
					{
						cmbSection.setValue(null);
						cmbSection.setEnabled(false);
					}
					else
					{
						cmbSection.setEnabled(true);
					}
				}
				else
				{
					chkSectionAll.setValue(false);
				}
			}
		});

		cButton.btnPreview.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(cmbMonth.getValue()!=null)
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
								showNotification("Warning","Please Select Section!!!", Notification.TYPE_WARNING_MESSAGE);
							}
						}
						else
						{
							showNotification("Warning","Please Select Department!!!", Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else
					{
						showNotification("Warning","Please Select Project!!!", Notification.TYPE_WARNING_MESSAGE);
					}

				}
			}
		});

		cButton.btnExit.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				close();
			}
		});
	}

	private void cmbUnitdataload()
	{
		cmbUnit.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql="select distinct vUnitId,vUnitName from tbEmployeeAttendanceFinal " +
					"where MONTH(dDate)=MONTH('"+cmbMonth.getValue()+"') and YEAR(dDate)=YEAR('"+cmbMonth.getValue()+"') order by vUnitName";
			Iterator <?> itr=session.createSQLQuery(sql).list().iterator();
			while(itr.hasNext())
			{

				Object[] element=(Object[])itr.next();
				cmbUnit.addItem(element[0]);
				cmbUnit.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbUnitdataload",exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void cmbDepartmentdataload()
	{
		cmbDepartment.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql="select distinct vDepartmentId,vDepartmentName from tbEmployeeAttendanceFinal " +
					"where MONTH(dDate)=MONTH('"+cmbMonth.getValue()+"') and YEAR(dDate)=YEAR('"+cmbMonth.getValue()+"') and " +
					"vUnitId='"+cmbUnit.getValue().toString()+"' order by vDepartmentName";
			Iterator <?> itr=session.createSQLQuery(sql).list().iterator();

			System.out.println("Department"+sql);	

			while(itr.hasNext())
			{
				Object[] element=(Object[])itr.next();
				cmbDepartment.addItem(element[0]);
				cmbDepartment.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbDepartmentdataload",exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void cmbSectiondataload()
	{
		cmbSection.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql="select distinct vSectionId,vSectionName from tbEmployeeAttendanceFinal " 
					+" where MONTH(dDate)=MONTH('"+cmbMonth.getValue()+"') and YEAR(dDate)=YEAR('"+cmbMonth.getValue()+"') "
					+" and vUnitId='"+cmbUnit.getValue().toString()+"' "
					+" and vDepartmentId like '"+(chkDepartmentAll.booleanValue()?"%":cmbDepartment.getValue())+"' "
					+ " order by vSectionName";
			Iterator <?> itr=session.createSQLQuery(sql).list().iterator();

			System.out.println("Section"+sql);	

			while(itr.hasNext())
			{
				Object[] element=(Object[])itr.next();
				cmbSection.addItem(element[0]);
				cmbSection.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbSectiondataload",exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void reportShow()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{

			String query="select vEmployeeCode,vEmployeeName,vDesignation,mDays as iMonthDay,totalHoliday,(mDays-totalHoliday)iWorkingDay,iPresentCount," +
					" iNotCount,iCLeaveCount,iSkLeaveCount,iEarLeaveCount,iMLeaveCount,iAbsentCount,tourCount,iLateCount,(FLOOR(iLateCount/3))lateCountOfAbsent, " +
					" vDepartmentId,vDepartmentName,vUnitId,vUnitName,vSectionId,vSection from funMonthlyEmployeeAttendance(" +
					"'"+cmbMonth.getValue()+"'," +
					"'"+cmbUnit.getValue().toString()+"'," +
					"'"+(chkSectionAll.booleanValue()?"%":cmbSection.getValue())+"','"+(chkDepartmentAll.booleanValue()?"%":cmbDepartment.getValue())+"'," +
					"'"+"%"+"')  " +
					" order by vSection,iRank,dJoinDate";
			
			System.out.println("reportShow: "+query);

			if(queryValueCheck(query))
			{
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("username", sessionBean.getUserName()+" "+sessionBean.getUserIp());
				hm.put("SysDate",reportTime.getTime);
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("date", cmbMonth.getValue());
				hm.put("sql", query);
				
				Window win = new ReportViewer(hm,"report/account/hrmModule/rptMonthlyAttendanceSummaryDevice.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",true);

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
		allComp.add(cmbSection);
		allComp.add(cButton.btnPreview);
		new FocusMoveByEnter(this,allComp);
	}

	private AbsoluteLayout buildMainLayout()
	{
		mainLayout=new AbsoluteLayout();
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");

		cmbMonth = new ComboBox();
		cmbMonth.setWidth("120px");
		cmbMonth.setHeight("-1px");
		cmbMonth.setNullSelectionAllowed(false);
		cmbMonth.setImmediate(true);
		cmbMonth.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Month :"), "top:40px; left:30px;");
		mainLayout.addComponent(cmbMonth, "top:38px; left:140px;");

		cmbUnit=new ComboBox();
		cmbUnit.setImmediate(true);
		cmbUnit.setWidth("260px");
		mainLayout.addComponent(new Label("Project : "), "top:70.0px;left:30.0px;");
		mainLayout.addComponent(cmbUnit, "top:68.0px;left:140.0px;");

		cmbDepartment=new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("260px");
		mainLayout.addComponent(new Label("Department : "), "top:100.0px;left:30.0px;");
		mainLayout.addComponent(cmbDepartment, "top:98.0px;left:140.0px;");

		chkDepartmentAll=new CheckBox("All");
		chkDepartmentAll.setImmediate(true);
		mainLayout.addComponent(chkDepartmentAll, "top:100.0px;left:405.0px;");
		
		cmbSection=new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("260px");
		mainLayout.addComponent(new Label("Section : "), "top:130px;left:30.0px;");
		mainLayout.addComponent(cmbSection, "top:128px;left:140.0px;");

		chkSectionAll=new CheckBox("All");
		chkSectionAll.setImmediate(true);
		mainLayout.addComponent(chkSectionAll, "top:130px;left:405.0px;");

		reportType=new OptionGroup("",lstType);
		reportType.setStyleName("horizontal");
		reportType.setImmediate(true);
		reportType.select("PDF");
		mainLayout.addComponent(reportType, "top:160.0px;left:140.0px;");
		reportType.setVisible(false);

		//mainLayout.addComponent(new Label("____________________________________________________________________________________________"), "top:180.0px;right:20.0px;left:20.0px;");
		mainLayout.addComponent(cButton, "bottom:20px;left:160.0px;");
		return mainLayout;
	}
}
