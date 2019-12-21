package hrm.common.reportform;

import java.io.IOException;
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
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class RptMonthWiseEmployeeTour extends Window {

	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblCommon=new Label(""),lblFrom=new Label(""),lblTo=new Label("");

	private Label lblEmpType;
	private ComboBox cmbEmpType,cmbSection;
	private CheckBox chkEmployeeType;

	private ComboBox cmbUnit,cmbDepartment;
	private CheckBox chkSectionAll;
	private PopupDateField dDateMonth,dDate;
	private CheckBox chkAllUnit,chkDepartmentAll;
	//private PopupDateField dSalaryMonth;
	private SimpleDateFormat dSqlFormat = new SimpleDateFormat("yyyy-MM-dd");
	ArrayList<Component> allComp = new ArrayList<Component>();
	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	private OptionGroup OGBankType;
	private static final List<String> type =Arrays.asList(new String[]{"Bank","Non-Bank"});

	private OptionGroup RadioBtnGroup;
	private static final List<String> group=Arrays.asList(new String[]{"PDF","Other"});
	private String EmpId="";
	String emp = "",empDes="";
	private ComboBox cmbMonthYear;
	private PopupDateField dFrom,dTo;
	private OptionGroup opgReportType;
	private List<?> listReportType = Arrays.asList(new String[]{"As on date","Date between","Month wise"});
	private CommonMethod cm;
	private String menuId = "";
	public RptMonthWiseEmployeeTour(SessionBean sessionBean,String menuId)
	{
		this.sessionBean=sessionBean;
		this.setCaption("MONTHLY WISE TOUR APPLICATION :: "+sessionBean.getCompany());
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		buildMainLayout();
		setContent(mainLayout);
		setEventAction();
		focusMove();
		allTrueFalse();
		cmbMonthYearDataLoad();
		lblCommon.setValue("Date :");
		dDate.setVisible(true);
		dDate.setValue(new java.util.Date());
		cmbUnitDataLoad();
		authenticationCheck();
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
	private void cmbMonthYearDataLoad()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		try{
			/*String sql="select distinct CONVERT(varchar(50),CONVERT(varchar(10),YEAR(dApplicationDate))+'-'+CONVERT(varchar(10),MONTH(dApplicationDate)))numberOfMonthYear,"+
					" CONVERT(varchar(100),DATENAME(MONTH,dApplicationDate)+'-'+CONVERT(varchar(50),YEAR(dApplicationDate)))monthYear,"+
					" YEAR(dApplicationDate),MONTH(dApplicationDate) from tbEmpLeaveApplicationInfo "+
					" order by YEAR(dApplicationDate)desc,MONTH(dApplicationDate)desc";*/
			String sql="select distinct CONVERT(date, (SELECT DATEADD(s,-1,DATEADD(mm, DATEDIFF(m,0,dApplicationDate)+1,0))))numberOfMonthYear," +
					"CONVERT(varchar(100),DATENAME(MONTH,dApplicationDate)+'-'+CONVERT(varchar(50),YEAR(dApplicationDate)))monthYear, YEAR(dApplicationDate)," +
					"MONTH(dApplicationDate) from tbTourApplication  order by YEAR(dApplicationDate)desc,MONTH(dApplicationDate)desc";
			
			System.out.println("cmbMonthYearDataLoad: "+sql);
			Iterator<?> iter=session.createSQLQuery(sql).list().iterator();
			while(iter.hasNext())
			{
				Object[] element=(Object[])iter.next();
				cmbMonthYear.addItem(element[0]);
				cmbMonthYear.setItemCaption(element[0], element[1].toString());
			}
		}catch(Exception exp)
		{
			showNotification("Month year data Load!"+exp,Notification.TYPE_WARNING_MESSAGE);
		}
	}
	private void cmbUnitDataLoad() 
	{
		cmbUnit.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql="";
			
			if(opgReportType.getValue().toString().equals("As on date"))
			{
				sql = "select distinct (select vUnitId from tbEmpOfficialPersonalInfo where vEmployeeId=ta.vEmployeeId)vUnitId,(select vUnitName from tbEmpOfficialPersonalInfo where vEmployeeId=ta.vEmployeeId)vUnitName from tbTourApplication ta where dApplicationDate<='"+dSqlFormat.format(dDate.getValue())+"'";
			}
			else if(opgReportType.getValue().toString().equals("Date between"))
			{
				sql = "select distinct (select vUnitId from tbEmpOfficialPersonalInfo where vEmployeeId=ta.vEmployeeId)vUnitId,(select vUnitName from tbEmpOfficialPersonalInfo where vEmployeeId=ta.vEmployeeId)vUnitName from tbTourApplication ta where " +
						"dApplicationDate between '"+dSqlFormat.format(dFrom.getValue())+"' and '"+dSqlFormat.format(dTo.getValue())+"' ";
			}
			else
			{
				if(cmbMonthYear.getValue()!=null)
				{
					sql = "select distinct (select vUnitId from tbEmpOfficialPersonalInfo where vEmployeeId=ta.vEmployeeId)vUnitId,(select vUnitName from tbEmpOfficialPersonalInfo where vEmployeeId=ta.vEmployeeId)vUnitName from tbTourApplication ta where " +
							"YEAR(dApplicationDate)=YEAR('"+dSqlFormat.format(cmbMonthYear.getValue())+"') " +
							"and MONTH(dApplicationDate)=MONTH('"+cmbMonthYear.getValue()+"') ";
				}				
			} 
			System.out.println("cmbUnitDataLoad: "+sql);
			
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator();iter.hasNext();)
			{
				Object element[] = (Object[]) iter.next();
				cmbUnit.addItem(element[0].toString());
				cmbUnit.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void cmbDepartmentDataLoad()
	{
		cmbDepartment.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql="";
			
			if(opgReportType.getValue().toString().equals("As on date"))
			{
				sql = "select distinct epo.vDepartmentId,epo.vDepartmentName from tbTourApplication ta inner join tbEmpOfficialPersonalInfo epo on epo.vEmployeeId=ta.vEmployeeId where ta.dApplicationDate<='"+dSqlFormat.format(dDate.getValue())+"' " +
					" and epo.vUnitId like '"+(cmbUnit.getValue()==null?"%":cmbUnit.getValue())+"' order by epo.vDepartmentName";
			}
			else if(opgReportType.getValue().toString().equals("Date between"))
			{
				sql = "select distinct epo.vDepartmentId,epo.vDepartmentName from tbTourApplication ta inner join tbEmpOfficialPersonalInfo epo on epo.vEmployeeId=ta.vEmployeeId where ta.dApplicationDate between '"+dSqlFormat.format(dFrom.getValue())+"' and '"+dSqlFormat.format(dTo.getValue())+"' order by epo.vDepartmentName";
			}
			else
			{
				if(cmbMonthYear.getValue()!=null)
				{
					sql = "select distinct epo.vDepartmentId,epo.vDepartmentName from tbTourApplication ta inner join tbEmpOfficialPersonalInfo epo on epo.vEmployeeId=ta.vEmployeeId where  " +
							" YEAR(ta.dApplicationDate)=YEAR('"+cmbMonthYear.getValue()+"') " +
							" and MONTH(ta.dApplicationDate)=MONTH('"+cmbMonthYear.getValue()+"') and epo.vUnitId like '"+(cmbUnit.getValue()==null?"%":cmbUnit.getValue())+"' " +
							" order by epo.vDepartmentName";
				}				
			} 
			System.out.println("cmbDepartment: "+sql);
			
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator();iter.hasNext();)
			{
				Object element[] = (Object[]) iter.next();
				cmbDepartment.addItem(element[0].toString());
				cmbDepartment.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp){
			showNotification("Dept :",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	public void cmbSectionDataLoad()
	{
		cmbSection.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql="";
			
			if(opgReportType.getValue().toString().equals("As on date"))
			{
				sql = "select distinct epo.vSectionId,epo.vSectionName from tbTourApplication ta inner join tbEmpOfficialPersonalInfo epo on epo.vEmployeeId=ta.vEmployeeId where ta.dApplicationDate<='"+dSqlFormat.format(dDate.getValue())+"' " +
					" and epo.vUnitId like '"+(cmbUnit.getValue()==null?"%":cmbUnit.getValue())+"' and epo.vDepartmentId like '"+(cmbDepartment.getValue()==null?"%":cmbDepartment.getValue())+"'  order by epo.vSectionName";
			}
			else if(opgReportType.getValue().toString().equals("Date between"))
			{
				sql = "select distinct epo.vSectionId,epo.vSectionName from tbTourApplication ta inner join tbEmpOfficialPersonalInfo epo on epo.vEmployeeId=ta.vEmployeeId where ta.dApplicationDate between '"+dSqlFormat.format(dFrom.getValue())+"' and '"+dSqlFormat.format(dTo.getValue())+"' and epo.vUnitId like '"+(cmbUnit.getValue()==null?"%":cmbUnit.getValue())+"' and epo.vDepartmentId like '"+(cmbDepartment.getValue()==null?"%":cmbDepartment.getValue())+"' order by epo.vSectionName";
			}
			else
			{
				if(cmbMonthYear.getValue()!=null)
				{
					sql = "select distinct epo.vSectionId,epo.vSectionName from tbTourApplication ta inner join tbEmpOfficialPersonalInfo epo  on epo.vEmployeeId=ta.vEmployeeId where  " +
							" YEAR(ta.dApplicationDate)=YEAR('"+cmbMonthYear.getValue()+"') " +
							" and MONTH(ta.dApplicationDate)=MONTH('"+cmbMonthYear.getValue()+"') " +
							" and epo.vUnitId like '"+(cmbUnit.getValue()==null?"%":cmbUnit.getValue())+"' and epo.vDepartmentId like '"+(cmbDepartment.getValue()==null?"%":cmbDepartment.getValue())+"' order by epo.vSectionName";
				}				
			} 
			System.out.println("cmbSection: "+sql);
			
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator();iter.hasNext();)
			{
				Object element[] = (Object[]) iter.next();
				cmbSection.addItem(element[0].toString());
				cmbSection.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp){
			showNotification("Dept :",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void setEventAction()
	{
		cmbMonthYear.addListener(new ValueChangeListener()
		{	
			public void valueChange(ValueChangeEvent event)
			{
				cmbUnit.removeAllItems();
				if(cmbMonthYear.getValue()!=null)
				{
					cmbUnitDataLoad();
				}
			}
		});
		dDate.addListener(new ValueChangeListener()
		{	
			public void valueChange(ValueChangeEvent event)
			{
				cmbUnit.removeAllItems();
				if(dDate.getValue()!=null)
				{
					cmbUnitDataLoad();
				}
			}
		});
		dDate.addListener(new ValueChangeListener()
		{	
			public void valueChange(ValueChangeEvent event)
			{
				cmbUnit.removeAllItems();
				if(dDate.getValue()!=null)
				{
					cmbUnitDataLoad();
				}
			}
		});
		dFrom.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				cmbUnit.removeAllItems();
				if(dFrom.getValue()!=null)
				{
					cmbUnitDataLoad();
				}
			}
		});
		dTo.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				cmbUnit.removeAllItems();
				if(dTo.getValue()!=null)
				{
					cmbUnitDataLoad();
				}
			}
		});
		cmbUnit.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbDepartment.removeAllItems();
				chkDepartmentAll.setValue(false);
				cmbDepartment.setEnabled(false);
				if(cmbUnit.getValue()!=null)
				{
					chkAllUnit.setValue(false);
					cmbDepartment.setEnabled(true);
					cmbDepartmentDataLoad();
				}
			}
		});
		chkAllUnit.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				cmbSection.removeAllItems();
				chkSectionAll.setValue(false);
				cmbSection.setEnabled(false);
				if(chkAllUnit.booleanValue())
				{
					cmbUnit.setValue(null);
					cmbUnit.setEnabled(false);
					cmbSection.setEnabled(true);
					cmbSectionDataLoad();
				}
				else
				{
					cmbUnit.setEnabled(true);
					cmbSection.setEnabled(false);
				}
			}
		});

		cmbDepartment.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSection.removeAllItems();
				chkSectionAll.setValue(false);
				cmbSection.setEnabled(false);
				if(cmbUnit.getValue()!=null || chkAllUnit.booleanValue())
				{
					if(cmbDepartment.getValue()!=null)
					{
						chkDepartmentAll.setValue(false);
						cmbSectionDataLoad();
						cmbSection.setEnabled(true);
					}
				}
			}
		});

		chkDepartmentAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				cmbSection.removeAllItems();
				chkSectionAll.setValue(false);
				cmbSection.setEnabled(false);
				if(cmbUnit.getValue()!=null || chkAllUnit.booleanValue())
				{
					if(chkDepartmentAll.booleanValue())
					{
						cmbDepartment.setValue(null);
						cmbDepartment.setEnabled(false);
						cmbSection.setEnabled(true);
						cmbSectionDataLoad();
					}
					else
					{
						cmbDepartment.setEnabled(true);
						cmbSection.setEnabled(false);
					}
				}
				else{
					chkDepartmentAll.setValue(false);
					showNotification("Warning..","Select Project Please!",Notification.TYPE_HUMANIZED_MESSAGE);
				}
			}
		});

	/*	cmbSection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmpType.removeAllItems();
				chkEmployeeType.setValue(false);
				cmbEmpType.setEnabled(false);
				if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
				{
					if(cmbSection.getValue()!=null)
					{
						chkSectionAll.setValue(false);
						cmb
						cmbEmpType.setEnabled(true);
					}
				}
			}
		});*/

		chkSectionAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
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
				else{
					chkSectionAll.setValue(false);
					showNotification("Warning..","Select Department Please!",Notification.TYPE_HUMANIZED_MESSAGE);
				}
			}
		});

		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbMonthYear.getValue()!=null || dDate.getValue()!=null || dFrom.getValue()!=null || dTo.getValue()!=null)
				{
					if(cmbUnit.getValue()!=null || chkAllUnit.booleanValue())
					{
						if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
						{
							reportShow();
						}
						else
						{
							showNotification("Warning..","Select Section Name",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else
					{
						showNotification("Warning..","Select Unit",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					showNotification("Warning..","Select "+lblCommon.getValue()+"  ",Notification.TYPE_WARNING_MESSAGE);
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
		opgReportType.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				allTrueFalse();
				if(opgReportType.getValue().toString().equals("As on date"))
				{
					lblCommon.setValue("Date :");
					dDate.setVisible(true);
				}
				else if(opgReportType.getValue().toString().equals("Date between"))
				{
					lblFrom.setValue("From :");
					lblTo.setValue("To :");
					dFrom.setVisible(true);
					dFrom.setValue(new java.util.Date());
					dTo.setVisible(true);
					dTo.setValue(new java.util.Date());
				}
				else
				{
					lblCommon.setValue("Month :");
					cmbMonthYear.setVisible(true);
					cmbMonthYearDataLoad();
				}
			}
		});

	}
	/*private void dDateDataLoad()
	{

		Session session=SessionFactoryUtil.getInstance().openSession();
		try{
			String sql="select distinct dApplicationDate,CONVERT(varchar,dApplicationDate,105)bDate, "+
					" YEAR(dApplicationDate),MONTH(dApplicationDate) from tbEmpLeaveApplicationInfo "+
					" where vStatus='Tour' order by YEAR(dApplicationDate)desc,MONTH(dApplicationDate)desc,dApplicationDate desc";
			Iterator<?> iter=session.createSQLQuery(sql).list().iterator();
			System.out.println(sql);
			while(iter.hasNext())
			{
				Object[] element=(Object[])iter.next();
				dDate.addItem(element[0]);
				dDate.setItemCaption(element[0],element[1].toString());
			}
		}catch(Exception exp)
		{
			showNotification("Date data Load!"+exp,Notification.TYPE_WARNING_MESSAGE);
		}

	}*/
	public void allTrueFalse()
	{
		lblCommon.setValue("");
		lblFrom.setValue("");
		lblTo.setValue("");
		cmbMonthYear.setValue(null);
		cmbMonthYear.setVisible(false);
		dDate.setValue(null);
		dDate.setVisible(false);
		dFrom.setVisible(false);
		dTo.setVisible(false);
	}
	private void reportShow()
	{
		String query="";
		try
		{
			String secId="%",caption="";
			if(cmbSection.getValue()!=null)
			{
				secId=cmbSection.getValue().toString();
			}
			String branchId="%",deptId="%";
			if(cmbUnit.getValue()!=null)
			{
				branchId=cmbUnit.getValue().toString();
			}
			if(cmbDepartment.getValue()!=null)
			{
				deptId=cmbDepartment.getValue().toString();
			}
			String type="";
			
			if(opgReportType.getValue().toString().equals("As on date"))
			{
				if(dDate.getValue()!=null)
				{
					query="select a.vEmployeeId,(select vEmployeeCode from tbEmpOfficialPersonalInfo where vEmployeeId=a.vEmployeeId)empCode," +
							" a.vEmployeeName,epo.vSectionId,epo.vSectionName,epo.vDepartmentId,epo.vDepartmentName,a.vDesignationId,b.vDesignation,a.vDepartmentID," +
							" a.vDepartmentName,(select dJoiningDate from tbEmpOfficialPersonalInfo where vEmployeeId=a.vEmployeeId)dJoiningDate," +
							" a.iTotalDays,a.dTourFrom,a.dTourTo,a.vPurposeOfLeave,a.vVisitingAddress," +
							" (a.vUserName+';'+a.vUserIp+';'+CONVERT(varchar,a.dEntryTime,104)+' '+CONVERT(varchar,a.dEntryTime,108))entryTime," +
							" iRank,epo.vUnitId,epo.vUnitName  from tbTourApplication a inner join tbDesignationInfo b on b.vDesignationId=a.vDesignationId inner join tbEmpOfficialPersonalInfo epo on epo.vEmployeeId=a.vEmployeeId " +
							" where  a.dApplicationDate<='"+dSqlFormat.format(dDate.getValue())+"' " +
							" and epo.vUnitId like '"+branchId+"' " +
							" and epo.vDepartmentID like '"+deptId+"' " +
							" and epo.vSectionId like '"+secId+"' " +
							" order by epo.vUnitName,epo.vDepartmentName,epo.vSectionName,iRank,epo.dJoiningDate,dTourFrom,dTourTo";
				}
				
			}
			else if(opgReportType.getValue().toString().equals("Date between"))
			{
				query="select a.vEmployeeId,(select vEmployeeCode from tbEmpOfficialPersonalInfo where vEmployeeId=a.vEmployeeId)empCode," +
						" a.vEmployeeName,epo.vSectionId,epo.vSectionName,epo.vDepartmentId,epo.vDepartmentName,a.vDesignationId,b.vDesignation,a.vDepartmentID," +
						" a.vDepartmentName,(select dJoiningDate from tbEmpOfficialPersonalInfo where vEmployeeId=a.vEmployeeId)dJoiningDate," +
						" a.iTotalDays,a.dTourFrom,a.dTourTo,a.vPurposeOfLeave,a.vVisitingAddress," +
						" (a.vUserName+';'+a.vUserIp+';'+CONVERT(varchar,a.dEntryTime,104)+' '+CONVERT(varchar,a.dEntryTime,108))entryTime," +
						" iRank,epo.vUnitId,epo.vUnitName  from tbTourApplication a inner join tbDesignationInfo b on b.vDesignationId=a.vDesignationId inner join tbEmpOfficialPersonalInfo epo on epo.vEmployeeId=a.vEmployeeId " +
						" where  epo.dApplicationDate between '"+dSqlFormat.format(dFrom.getValue())+"' and '"+dSqlFormat.format(dTo.getValue())+"' " +
						" and epo.vUnitId like '"+branchId+"' " +
						" and epo.vDepartmentID like '"+deptId+"' " +
						" and epo.vSectionId like '"+secId+"' " +
						" order by epo.vUnitName,epo.vDepartmentName,epo.vSectionName,iRank,epo.dJoiningDate,dTourFrom,dTourTo";
				
			}
			else if(opgReportType.getValue().toString().equals("Month wise"))
			{
				if(cmbMonthYear.getValue()!=null)
				{
					query="select a.vEmployeeId,(select vEmployeeCode from tbEmpOfficialPersonalInfo where vEmployeeId=a.vEmployeeId)empCode," +
							" a.vEmployeeName,epo.vSectionId,epo.vSectionName,epo.vDepartmentId,epo.vDepartmentName,a.vDesignationId,b.vDesignation,a.vDepartmentID," +
							" a.vDepartmentName,(select dJoiningDate from tbEmpOfficialPersonalInfo where vEmployeeId=a.vEmployeeId)dJoiningDate," +
							" a.iTotalDays,a.dTourFrom,a.dTourTo,a.vPurposeOfLeave,a.vVisitingAddress," +
							" (a.vUserName+';'+a.vUserIp+';'+CONVERT(varchar,a.dEntryTime,104)+' '+CONVERT(varchar,a.dEntryTime,108))entryTime," +
							" iRank,epo.vUnitId,epo.vUnitName  from tbTourApplication a inner join tbDesignationInfo b on b.vDesignationId=a.vDesignationId inner join tbEmpOfficialPersonalInfo epo on epo.vEmployeeId=a.vEmployeeId " +
							" where  YEAR(epo.dApplicationDate)=YEAR('"+cmbMonthYear+"') and MONTH(epo.dApplicationDate)=MONTH('"+cmbMonthYear+"') " +
							" and epo.vUnitId like '"+branchId+"' " +
							" and epo.vDepartmentID like '"+deptId+"' " +
							" and epo.vSectionId like '"+secId+"' " +
							" order by epo.vUnitName,epo.vDepartmentName,epo.vSectionName,iRank,epo.dJoiningDate,dTourFrom,dTourTo";
				}
			}
			
			/*String query="select ela.vEmployeeId,(select vEmployeeCode from tbEmpOfficialPersonalInfo where vEmployeeId=ela.vEmployeeId)empCode,"+
					" ela.vEmployeeName,ela.vSectionId,ela.vSectionName,ela.vSectionId,ela.vSectionName,ela.vDesignationId,ela.vDesignation, "+
					" ela.vUnitId,ela.vUnitName,(select dJoiningDate from tbEmpOfficialPersonalInfo where vEmployeeId=ela.vEmployeeId)dJoiningDate, "+
					" ela.mTotalDays,ela.dLeaveFrom,ela.dLeaveTo,ela.vLeavePupose,ela.vLeaveAddress, "+
					" (ela.vUserName+';'+ela.vUserIp+';'+CONVERT(varchar,ela.dEntryTime,104)+' '+CONVERT(varchar,ela.dEntryTime,108))entryTime,iRank "+ 
					" from tbEmpLeaveApplicationInfo ela inner join tbDesignationInfo dsi on dsi.vDesignationId=ela.vDesignationId " +
					" where "+type+" "+
					" and vUnitId like '"+branchId+"' " +
					" and vSectionId like '"+secId+"' " +
					" order by vUnitName,ela.vSectionName,iRank,dJoiningDate,dLeaveFrom,dLeaveTo";*/
	
					System.out.println("reportShow: "+query);

				if(queryValueCheck(query))
				{
					if(opgReportType.getValue().toString().equals("As on date"))
					{
						if(dDate.getValue()!=null)
						{
							caption="Date : "+sessionBean.dfBd.format(dDate.getValue());
						}
					}
					else if(opgReportType.getValue().toString().equals("Date between"))
					{
						caption="From : "+sessionBean.dfBd.format(dFrom.getValue()) +" To :"+sessionBean.dfBd.format(dTo.getValue());
					}
					else if(opgReportType.getValue().toString().equals("Month wise"))
					{
						if(cmbMonthYear.getValue()!=null)
						{
							caption="Month :"+cmbMonthYear.getItemCaption(cmbMonthYear.getValue());
						}
					}
					HashMap <String,Object> hm = new HashMap <String,Object> ();
					hm.put("company", sessionBean.getCompany());
					hm.put("address", sessionBean.getCompanyAddress());
					hm.put("phone", sessionBean.getCompanyContact());
					hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
					hm.put("SysDate",reportTime.getTime);
					hm.put("logo", sessionBean.getCompanyLogo());
					hm.put("developer", "Developed by: E-Vision Software Ltd. || Mob:01755-506044 || www.eslctg.com");
					hm.put("reportType", caption);
					//hm.put("devloperLogo", "hrm/jasper/attendance/esl.png");
					hm.put("sql", query);
	
					Window win = new ReportViewer(hm,"report/account/hrmModule/rptMonthWiseEmployeeTour.jasper",
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
			showNotification("reportView "+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private boolean queryValueCheck(String sql)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try 
		{
			List <?> lst = session.createSQLQuery(sql).list();

			if (!lst.isEmpty()) 
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
		allComp.add(cmbMonthYear);
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
		setWidth("500px");
		setHeight("250px");

		opgReportType = new OptionGroup("",listReportType);
		opgReportType.setImmediate(true);
		opgReportType.setStyleName("horizontal");
		opgReportType.setValue("As on date");
		mainLayout.addComponent(opgReportType,"top:10px; left:130px");

		// lblCommon

		lblCommon.setImmediate(false);
		lblCommon.setWidth("100.0%");
		lblCommon.setHeight("-1px");
		mainLayout.addComponent(lblCommon,"top:40px; left:30.0px;");


		cmbMonthYear=new ComboBox();
		cmbMonthYear.setImmediate(true);
		cmbMonthYear.setNullSelectionAllowed(false);
		cmbMonthYear.setWidth("140px");
		cmbMonthYear.setHeight("-1px");
		mainLayout.addComponent(cmbMonthYear, "top:38px; left:130.0px;");

		dDate=new PopupDateField();
		dDate.setWidth("110px");
		dDate.setHeight("-1px");
		dDate.setImmediate(true);
		dDate.setValue(new java.util.Date());
		dDate.setDateFormat("dd-MM-yyyy");
		dDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dDate,"top:38px; left:130px");
		

		dFrom=new PopupDateField();
		dFrom.setWidth("110px");
		dFrom.setHeight("-1px");
		dFrom.setImmediate(true);
		dFrom.setValue(new java.util.Date());
		dFrom.setDateFormat("dd-MM-yyyy");
		dFrom.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(lblFrom,"top:38px; left:30px");
		mainLayout.addComponent(dFrom,"top:38px; left:130px");

		dTo=new PopupDateField();
		dTo.setWidth("110px");
		dTo.setHeight("-1px");
		dTo.setImmediate(true);
		dTo.setValue(new java.util.Date());
		dTo.setDateFormat("dd-MM-yyyy");
		dTo.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(lblTo,"top:38px; left:245px");
		mainLayout.addComponent(dTo,"top:38px; left:280px");


		cmbUnit=new ComboBox();
		cmbUnit.setImmediate(true);
		cmbUnit.setNullSelectionAllowed(false);
		cmbUnit.setWidth("260px");
		cmbUnit.setHeight("-1px");
		mainLayout.addComponent(new Label("Project : "), "top:70px; left:30.0px;");
		mainLayout.addComponent(cmbUnit, "top:68px; left:130px;");

		chkAllUnit = new CheckBox("All");
		chkAllUnit.setImmediate(true);
		mainLayout.addComponent(chkAllUnit,"top:70px; left:395px");
		chkAllUnit.setVisible(false);


		// cmbSection
		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("260px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setNullSelectionAllowed(true);
		mainLayout.addComponent(new Label("Department :"),"top:100px; left:30.0px;");
		mainLayout.addComponent(cmbDepartment, "top:98px; left:130.0px;");

		chkDepartmentAll = new CheckBox("All");
		chkDepartmentAll.setImmediate(true);
		mainLayout.addComponent(chkDepartmentAll, "top:100px; left:395.0px;");
		
		// cmbSection
		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("260px");
		cmbSection.setHeight("-1px");
		cmbSection.setNullSelectionAllowed(true);
		mainLayout.addComponent(new Label("Section :"),"top:130px; left:30.0px;");
		mainLayout.addComponent(cmbSection, "top:128px; left:130.0px;");

		chkSectionAll = new CheckBox("All");
		chkSectionAll.setImmediate(true);
		mainLayout.addComponent(chkSectionAll, "top:130px; left:395.0px;");

		RadioBtnGroup = new OptionGroup("",group);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		//mainLayout.addComponent(RadioBtnGroup, "top:150px;left:130.0px;");

		//mainLayout.addComponent(new Label("_________________________________________________________________________________________"), "bottom:40px;right:20.0px;left:20.0px;");		
		mainLayout.addComponent(cButton,"bottom:15px; left:130px");

		return mainLayout;
	}
}