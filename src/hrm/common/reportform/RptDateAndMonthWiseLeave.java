package hrm.common.reportform;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

import com.common.share.CommonButton;
import com.common.share.CommonMethod;
import com.common.share.ReportDate;
import com.common.share.ReportOption;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class RptDateAndMonthWiseLeave extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	
	private ComboBox cmbSection,cmbUnit;
	CheckBox chkSectionAll=new CheckBox("All");
	private Label lblFromDate;
	private PopupDateField dFromDate;

	private Label lblToDate;
	private PopupDateField dToDate;
	
	private Label lblMonth;
	private PopupDateField dMonth;
	private ComboBox cmbDepartment;
	private CheckBox chkDepartmentAll;
	private ComboBox cmbEmployee;
	private CheckBox chkEmployeeAll;

	private OptionGroup opgTimeSelect;
	private List<?> timeSelect = Arrays.asList(new String[]{"Monthly","Between Date"});

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});

	private CommonButton cButton = new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	private SimpleDateFormat dDbFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dFormatMontherYear = new SimpleDateFormat("MMMMM-yyyy");
	private SimpleDateFormat dFormatBangla = new SimpleDateFormat("dd-MM-yyyy");

	TextField txtPath=new TextField();
	private CommonMethod cm;
	private String menuId = "";
	public RptDateAndMonthWiseLeave(SessionBean sessionBean,String menuId) 
	{
		this.sessionBean=sessionBean;
		this.setCaption("DATE AND MONTH WISE LEAVE :: "+sessionBean.getCompany());
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		buildMainLayout();
		setContent(mainLayout);
		cmbUnitDataLoad();
		//cmbAddEmployeeName();
		setEventAction();
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

	private void cmbEmployeeDataLoad() {
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "select distinct epo.vEmployeeId,epo.vEmployeeCode,epo.vEmployeeName from tbEmpLeaveApplicationInfo eli "
					+ "inner join tbEmpOfficialPersonalInfo epo on epo.vEmployeeId=eli.vEmployeeId "
					+ "where epo.vUnitId like '"+cmbUnit.getValue()+"' "
					+ "and vDepartmentId like '"+(cmbDepartment.getValue()==null?"%":cmbDepartment.getValue())+"' "
					+ "and epo.vSectionId like '"+(cmbSection.getValue()==null?"%":chkSectionAll.getValue())+"'" +
					" order by epo.vEmployeeName";
			System.out.println("cmbEmployeeDataLoad: "+sql);
			
			List<?> list = session.createSQLQuery(sql).list();
			cmbEmployee.removeAllItems();
			for(Iterator<?> iter = list.iterator();iter.hasNext();)
			{ 
				Object element[] = (Object[]) iter.next();
				cmbEmployee.addItem(element[0].toString());
				cmbEmployee.setItemCaption(element[0].toString(), element[1]+"-"+element[2]);
			}
		}
		catch(Exception exp)
		{
			showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void cmbSectionDataLoad() {
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "select distinct epo.vSectionId,epo.vSectionName from tbEmpLeaveApplicationInfo eli inner join tbEmpOfficialPersonalInfo epo on epo.vEmployeeId=eli.vEmployeeId" +
					" where epo.vUnitId like '"+cmbUnit.getValue()+"' and vDepartmentId like '"+(cmbDepartment.getValue()==null?"%":cmbDepartment.getValue())+"' " +
					" order by epo.vSectionName";
			List<?> list = session.createSQLQuery(sql).list();
			cmbSection.removeAllItems();
			for(Iterator<?> iter = list.iterator();iter.hasNext();)
			{ 
				Object element[] = (Object[]) iter.next();
				cmbSection.addItem(element[0].toString());
				cmbSection.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void cmbDepartmentDataLoad() {
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "select distinct epo.vDepartmentId,epo.vDepartmentName from tbEmpLeaveApplicationInfo eli inner join tbEmpOfficialPersonalInfo epo on epo.vEmployeeId=eli.vEmployeeId " +
					" where epo.vUnitId like '"+cmbUnit.getValue()+"' " +
					" order by epo.vDepartmentName";
			List<?> list = session.createSQLQuery(sql).list();
			cmbDepartment.removeAllItems();
			for(Iterator<?> iter = list.iterator();iter.hasNext();)
			{ 
				Object element[] = (Object[]) iter.next();
				cmbDepartment.addItem(element[0].toString());
				cmbDepartment.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void cmbUnitDataLoad() {
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "select distinct epo.vUnitId,epo.vUnitName from tbEmpLeaveApplicationInfo eli inner join tbEmpOfficialPersonalInfo epo on epo.vEmployeeId=eli.vEmployeeId order by epo.vUnitName";
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
			showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void setEventAction()
	{
		cmbUnit.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(cmbUnit.getValue()!=null)
				{
					cmbDepartment.removeAllItems();
					cmbDepartmentDataLoad();
				}
			}
		});
		cmbDepartment.addListener(new ValueChangeListener() {			
			public void valueChange(ValueChangeEvent event) {
				if(cmbDepartment.getValue()!=null)
				{
					cmbSection.removeAllItems();
					cmbSectionDataLoad();
				}
			}
		});
		chkDepartmentAll.addListener(new ClickListener() {			
			public void buttonClick(ClickEvent event) {
				if(cmbUnit.getValue()!=null)
				{
					if(chkDepartmentAll.booleanValue())
					{
						cmbDepartment.setValue(null);
						cmbDepartment.setEnabled(false);
						cmbSection.removeAllItems();
						cmbSectionDataLoad();
					}
					else
					{
						cmbDepartment.setEnabled(true);
					}
				}
			}
		});

		chkSectionAll.addListener(new ClickListener() {			
			public void buttonClick(ClickEvent event) {
				if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
				{
					if(chkSectionAll.booleanValue())
					{
						cmbSection.setValue(null);
						cmbSection.setEnabled(false);
						cmbEmployeeDataLoad();
					}
					else
					{
						cmbSection.setEnabled(true);
					}
				}
			}
		});
		cmbEmployee.addListener(new ValueChangeListener() {			
			public void valueChange(ValueChangeEvent event) {
				if(cmbUnit.getValue()!=null)
				{
					if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
					{
						if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
						{
							if(cmbEmployee.getValue()!=null)
							{
								System.out.println("LOL");		
							}
						}
					}
				}
			}
		});
		chkEmployeeAll.addListener(new ClickListener() {			
			public void buttonClick(ClickEvent event) {
				if(cmbUnit.getValue()!=null )
				{
					if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
					{
						if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
						{
							if(chkEmployeeAll.booleanValue())
							{
								cmbEmployee.setValue(null);
								cmbEmployee.setEnabled(false);						
							}
							else
							{
								cmbEmployee.setEnabled(true);
							}
						}
					}
				}
			}
		});
		cButton.btnPreview.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbUnit.getValue()!=null)
				{
					if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
					{
						if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
						{
							if(cmbEmployee.getValue()!=null || chkEmployeeAll.booleanValue())
							{
								if(opgTimeSelect.getValue().toString().equals("Monthly"))
								{
									if(dMonth.getValue()!=null)
									{
										reportpreview();
									}
								}
								else
								{
									if(dFromDate.getValue()!=null && dToDate.getValue()!=null)
									{
										reportpreview();
									}
									else
									{
										showNotification("Warning","Select Date Range",Notification.TYPE_WARNING_MESSAGE);
									}
								}
							}
						}
						else
						{
							showNotification("Warning","Select Section Name",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else
					{
						showNotification("Warning","Select Department Name",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					showNotification("Warning","Select Project Name",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});
		
		cButton.btnExit.addListener(new Button.ClickListener() 
		{	
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});
		
		opgTimeSelect.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				opgTimeSelectAction();
			}
		});
		
		chkSectionAll.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				if(chkSectionAll.booleanValue()){
					cmbSection.setValue(null);
					cmbSection.setEnabled(false);
				}
				else{
					cmbSection.setEnabled(true);
				}
			}
		});
	}
	public void opgActionWiseVisible(boolean b)
	{
		lblMonth.setVisible(b);
	    dMonth.setVisible(b);
	    lblFromDate.setVisible(!b);
	    dFromDate.setVisible(!b);
	    lblToDate.setVisible(!b);
	    dToDate.setVisible(!b);
	}
	private void opgTimeSelectAction(){
		if(opgTimeSelect.getValue().toString()=="Monthly"){
			opgActionWiseVisible(true);
			refreshWork();
		}
		else{
			opgActionWiseVisible(false);
			refreshWork();
		}
	}
	public void refreshWork(){
		dFromDate.setValue(new Date());
		dToDate.setValue(new Date());
		dMonth.setValue(new Date());
	}
	
	private void reportpreview()
	{
		String report="",query="",dDateMonth="";
		
		try
		{
			if(opgTimeSelect.getValue().toString().equals("Monthly"))
			{
				report="report/account/hrmModule/RptDateAndMonthWiseLeave.jasper";
				query="select distinct epo.vEmployeeCode," +
						"epo.vEmployeeName,epo.vSectionName,epo.vEmployeeType,epo.vDesignationId,epo.vDesignationName,a.vLeaveTypeId,a.vLeaveType,a.dLeaveFrom,a.dLeaveTo," +
						"(select iRank from tbDesignationInfo where vDesignationId=epo.vDesignationId)iDesignationSerial," +
						"epo.dJoiningDate,DATEDIFF(DAY,dLeaveFrom,dLeaveTo)+1 iDays,epo.vDepartmentId,epo.vDepartmentName  " +
						"from tbEmpLeaveApplicationInfo a inner join tbEmpOfficialPersonalInfo epo on epo.vEmployeeId=a.vEmployeeId " +
						"inner join tbEmpLeaveApplicationDetails b on a.vLeaveId=b.vLeaveId " +
						"where epo.vUnitId like '"+(cmbUnit.getValue()!=null?cmbUnit.getValue():"")+"' " +
						"and epo.vDepartmentId like '"+(cmbDepartment.getValue()!=null?cmbDepartment.getValue():"%")+"' " +
						"and epo.vSectionId like '"+(cmbSection.getValue()!=null?cmbSection.getValue():"%")+"' " +
						"and epo.vEmployeeId like '"+(cmbEmployee.getValue()!=null?cmbEmployee.getValue():"%")+"' " +
						"and MONTH(b.dLeaveDate)=MONTH('"+dDbFormat.format(dMonth.getValue())+"') " +
						"and YEAR(b.dLeaveDate)=YEAR('"+dDbFormat.format(dMonth.getValue())+"')  and a.iApprovedFlag=1 " +
						"order by epo.vDepartmentName ";
				
				dDateMonth="Date: "+dFormatMontherYear.format(dMonth.getValue());
			}
			else if(opgTimeSelect.getValue().toString().equals("Between Date"))
			{
				report="report/account/hrmModule/RptDateAndMonthWiseLeave.jasper";
				query="select distinct epo.vEmployeeCode," +
						"epo.vEmployeeName,epo.vSectionName,epo.vEmployeeType,epo.vDesignationId,epo.vDesignationName,a.vLeaveTypeId,a.vLeaveType,a.dLeaveFrom,a.dLeaveTo," +
						"(select iRank from tbDesignationInfo where vDesignationId=epo.vDesignationId)iDesignationSerial," +
						"epo.dJoiningDate,DATEDIFF(DAY,dLeaveFrom,dLeaveTo)+1 iDays,epo.vDepartmentId,epo.vDepartmentName  " +
						"from tbEmpLeaveApplicationInfo a inner join tbEmpOfficialPersonalInfo epo on epo.vEmployeeId=a.vEmployeeId " +
						"inner join tbEmpLeaveApplicationDetails b on a.vLeaveId=b.vLeaveId " +
						"where epo.vUnitId like '"+(cmbUnit.getValue()!=null?cmbUnit.getValue():"")+"' " +
						"and epo.vDepartmentId like '"+(cmbDepartment.getValue()!=null?cmbDepartment.getValue():"%")+"' " +
						"and epo.vSectionId like '"+(cmbSection.getValue()!=null?cmbSection.getValue():"%")+"' " +
						"and epo.vEmployeeId like '"+(cmbEmployee.getValue()!=null?cmbEmployee.getValue():"%")+"' " +
						"and b.dLeaveDate between '"+dDbFormat.format(dFromDate.getValue())+"' and '"+dDbFormat.format(dToDate.getValue())+"' and a.iApprovedFlag=1 " +
						"order by vDepartmentName ";
				
				dDateMonth="From : "+dFormatBangla.format(dFromDate.getValue())+"   To : "+dFormatBangla.format(dToDate.getValue());
			}
			System.out.println("Report Query: "+query);
			
			if(queryValueCheck(query))
			{
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("userName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
				
				hm.put("dDate", dMonth.getValue());
				hm.put("fromDate", dMonth.getValue());
				
				hm.put("dDateMonth", dDateMonth);
				
				hm.put("SysDate",reportTime.getTime);
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("sql", query);

				Window win = new ReportViewer(hm,report,
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
			this.getParent().showNotification("Error",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
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
	
	private AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("490px");
		setHeight("300px");

		opgTimeSelect=new OptionGroup("",timeSelect);
		opgTimeSelect.select("Monthly");
		opgTimeSelect.setImmediate(true);
		opgTimeSelect.setStyleName("horizontal");
		mainLayout.addComponent(opgTimeSelect, "top:10.0px; left:150px;");

		cmbUnit=new ComboBox();
		cmbUnit.setWidth("260px");
		cmbUnit.setHeight("-1px");
		cmbUnit.setImmediate(true);
		mainLayout.addComponent(new Label("Project : "), "top:40.0px;left:20px;");
		mainLayout.addComponent(cmbUnit, "top:38.0px;left:130px");
		
		cmbDepartment = new ComboBox();
		cmbDepartment.setWidth("260px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setNullSelectionAllowed(true);
		cmbDepartment.setImmediate(true);
		mainLayout.addComponent(new Label("Department :"), "top:70px;left:20.0px;");
		mainLayout.addComponent(cmbDepartment, "top:68.0px; left:130.0px;");
		
		chkDepartmentAll=new CheckBox("All");
		chkDepartmentAll.setWidth("-1px");
		chkDepartmentAll.setHeight("-1px");
		chkDepartmentAll.setImmediate(true);
		mainLayout.addComponent(chkDepartmentAll,"top:70; left:393px");

		// lblSectionName
		

		// cmbSectionName
		cmbSection = new ComboBox();
		cmbSection.setWidth("260px");
		cmbSection.setHeight("-1px");
		cmbSection.setNullSelectionAllowed(true);
		cmbSection.setImmediate(true);
		mainLayout.addComponent(new Label("Section :"),"top:100px; left:20.0px;");
		mainLayout.addComponent(cmbSection, "top:98px; left:130.0px;");
		
		chkSectionAll=new CheckBox("All");
		chkSectionAll.setWidth("-1px");
		chkSectionAll.setHeight("-1px");
		chkSectionAll.setImmediate(true);
		mainLayout.addComponent(chkSectionAll,"top:100; left:393px");
		
		cmbEmployee = new ComboBox();
		cmbEmployee.setWidth("260px");
		cmbEmployee.setHeight("-1px");
		cmbEmployee.setNullSelectionAllowed(true);
		cmbEmployee.setImmediate(true);
		mainLayout.addComponent(new Label("Employee :"),"top:130px; left:20.0px;");
		mainLayout.addComponent(cmbEmployee, "top:130px; left:130.0px;");
		
		chkEmployeeAll=new CheckBox("All");
		chkEmployeeAll.setWidth("-1px");
		chkEmployeeAll.setHeight("-1px");
		chkEmployeeAll.setImmediate(true);
		mainLayout.addComponent(chkEmployeeAll,"top:130px; left:393px");
		
		// lblFromDate
		lblFromDate = new Label("From Date :");
		lblFromDate.setImmediate(false);
		lblFromDate.setWidth("100.0%");
		lblFromDate.setHeight("-1px");
		mainLayout.addComponent(lblFromDate,"top:160px; left:20px;");
		lblFromDate.setVisible(false);
		// dFromDate
		dFromDate = new PopupDateField();
		dFromDate.setImmediate(true);
		dFromDate.setWidth("110px");
		dFromDate.setHeight("-1px");
		dFromDate.setDateFormat("dd-MM-yyyy");
		dFromDate.setValue(new java.util.Date());
		dFromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dFromDate, "top:158px; left:130px;");
		dFromDate.setVisible(false);
		//lblMonth
		lblMonth = new Label("Month :");
		lblMonth.setImmediate(false);
		lblMonth.setWidth("100.0%");
		lblMonth.setHeight("-1px");
		mainLayout.addComponent(lblMonth,"top:160px; left:20px;");
		
		// dMonth
		dMonth = new PopupDateField();
		dMonth.setImmediate(true);
		dMonth.setWidth("150px");
		dMonth.setHeight("-1px");
		dMonth.setDateFormat("MMMMM-yyyy");
		dMonth.setValue(new java.util.Date());
		dMonth.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dMonth, "top:158px; left:130px;");
		
		// lblToDate
		lblToDate = new Label("To");
		lblToDate.setImmediate(false);
		lblToDate.setWidth("100.0%");
		lblToDate.setHeight("-1px");
		mainLayout.addComponent(lblToDate,"top:160px; left:250.0px;");
		lblToDate.setVisible(false);

		// dToDate
		dToDate = new PopupDateField();
		dToDate.setImmediate(true);
		dToDate.setWidth("110px");
		dToDate.setHeight("-1px");
		dToDate.setDateFormat("dd-MM-yyyy");
		dToDate.setValue(new java.util.Date());
		dToDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dToDate, "top:158px; left:268.0px;");
		dToDate.setVisible(false);
		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:190px;left:150px;");
		RadioBtnGroup.setVisible(false);

	//	mainLayout.addComponent(new Label("_______________________________________________________________________________"), "top:170.0px;left:20.0px;right:20.0px;");
		mainLayout.addComponent(cButton,"bottom:15.0px; left:130.0px");

		return mainLayout;
	}
}
