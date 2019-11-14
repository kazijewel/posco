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
public class RptEmployeeSeparationform extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	
	private ComboBox cmbSection,cmbUnit,cmbSeparationType,cmbDepartment;
	CheckBox chkSectionAll=new CheckBox("All");
	CheckBox chkSeparationTypell=new CheckBox("All");
	private CheckBox chkDepartmentAll = new CheckBox("All");
	private Label lblFromDate;
	private PopupDateField dFromDate;

	private Label lblToDate;
	private PopupDateField dToDate;
	
	private Label lblMonth;
	private PopupDateField dMonth;

	private OptionGroup opgTimeSelect;
	private List<?> timeSelect = Arrays.asList(new String[]{"Monthly","Between Date"});

	//private Label lblLoanType;
	//private ComboBox cmbLoanType;
	//private static final String[] loanType = new String[] {"Advanced","Salary Loan","PF Loan"};
	//private CheckBox chkLoanTypeAll;

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});

	private CommonButton cButton = new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	private SimpleDateFormat dDbFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dDbMonth = new SimpleDateFormat("MMMMMMMMM-yyyy");
	

	TextField txtPath=new TextField();
	TextField txtAddress=new TextField();
	private CommonMethod cm;
	private String menuId = "";
	public RptEmployeeSeparationform(SessionBean sessionBean,String menuId) 
	{
		this.sessionBean=sessionBean;
		this.setCaption("Employee Separation :: "+sessionBean.getCompany());
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		buildMainLayout();
		setContent(mainLayout);
		cmbUnitDataLoad();
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
	private void cmbSeparationDataLoad() {
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		String sql="",section="%",deptId="%";
		if(!chkSectionAll.booleanValue())
		{
			section=cmbSection.getValue().toString();
		}
		if(!chkDepartmentAll.booleanValue())
		{
			deptId=cmbDepartment.getValue().toString();
		}
		try
		{
			if(opgTimeSelect.getValue().toString()=="Monthly")
			{
				sql = "select distinct 0,vEmployeeStatus from tbEmpOfficialPersonalInfo where bStatus=0 " +
						"and MONTH(vStatusDate) =MONTH('"+dDbFormat.format(dMonth.getValue())+"') " +
						"and YEAR(vStatusDate) =YEAR('"+dDbFormat.format(dMonth.getValue())+"') " +
						"and vUnitId='"+cmbUnit.getValue()+"'and vDepartmentId like '"+deptId+"' and vSectionId like '"+section+"' order by vEmployeeStatus ";
			}
			else
			{
				sql = "select distinct 0,vEmployeeStatus from tbEmpOfficialPersonalInfo where bStatus=0 and " +
						"vStatusDate between '"+dDbFormat.format(dFromDate.getValue())+"' and '"+dDbFormat.format(dToDate.getValue())+"' " +
						"and vUnitId='"+cmbUnit.getValue()+"' and vDepartmentId like '"+deptId+"' and vSectionId like '"+section+"' order by vEmployeeStatus ";
			}
			System.out.println("cmbSeparationDataLoad: "+sql);
			
			List<?> list = session.createSQLQuery(sql).list();
			cmbSeparationType.removeAllItems();
			for(Iterator<?> iter = list.iterator();iter.hasNext();)
			{
				Object element[] = (Object[]) iter.next();
				cmbSeparationType.addItem(element[1].toString());
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
		String sql="",unitId="%";
		try
		{
			if(opgTimeSelect.getValue().toString()=="Monthly")
			{
				sql = "select distinct vDepartmentId,vDepartmentName from tbEmpOfficialPersonalInfo where bStatus=0 " +
						"and MONTH(vStatusDate) =MONTH('"+dDbFormat.format(dMonth.getValue())+"') " +
						"and YEAR(vStatusDate) =YEAR('"+dDbFormat.format(dMonth.getValue())+"') " +
						"and vUnitId like '"+cmbUnit.getValue()+"' order by vDepartmentName ";
			}
			else
			{
				sql = "select distinct vDepartmentId,vDepartmentName from tbEmpOfficialPersonalInfo where bStatus=0 and " +
						"vStatusDate between '"+dDbFormat.format(dFromDate.getValue())+"' and '"+dDbFormat.format(dToDate.getValue())+"' " +
						"and vUnitId like '"+cmbUnit.getValue()+"' order by vDepartmentName ";
			}
			System.out.println("cmbSectionDataLoad: "+sql);
			
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
	private void cmbSectionDataLoad() {
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		String sql="",deptId="%";
		if(!chkDepartmentAll.booleanValue())
		{
			deptId=cmbDepartment.getValue().toString();
		}
		
		try
		{
			if(opgTimeSelect.getValue().toString()=="Monthly")
			{
				sql = "select distinct vSectionId,vSectionName from tbEmpOfficialPersonalInfo where bStatus=0 " +
						"and MONTH(vStatusDate) =MONTH('"+dDbFormat.format(dMonth.getValue())+"') " +
						"and YEAR(vStatusDate) =YEAR('"+dDbFormat.format(dMonth.getValue())+"') " +
						"and vUnitId='"+cmbUnit.getValue()+"' and vDepartmentId like '"+deptId+"' order by vSectionName ";
			}
			else
			{
				sql = "select distinct vSectionId,vSectionName from tbEmpOfficialPersonalInfo where bStatus=0 and " +
						"vStatusDate between '"+dDbFormat.format(dFromDate.getValue())+"' and '"+dDbFormat.format(dToDate.getValue())+"' " +
						"and vUnitId='"+cmbUnit.getValue()+"' and vDepartmentId like '"+deptId+"' order by vSectionName ";
			}
			System.out.println("cmbSectionDataLoad: "+sql);
			
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
	private void cmbUnitDataLoad() {
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		String sql="";
		
		try
		{
			if(opgTimeSelect.getValue().toString()=="Monthly")
			{
				sql = "select distinct vUnitId,vUnitName from tbEmpOfficialPersonalInfo where bStatus=0 " +
						"and MONTH(vStatusDate) =MONTH('"+dDbFormat.format(dMonth.getValue())+"') " +
						"and YEAR(vStatusDate) =YEAR('"+dDbFormat.format(dMonth.getValue())+"') order by vUnitName ";
			}
			else
			{
				sql = "select distinct vUnitId,vUnitName from tbEmpOfficialPersonalInfo where bStatus=0 and " +
						"vStatusDate between '"+dDbFormat.format(dFromDate.getValue())+"' and '"+dDbFormat.format(dToDate.getValue())+"' order by vUnitName";
			}
			System.out.println("cmbUnitDataLoad: "+sql);
			
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
		opgTimeSelect.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				opgTimeSelectAction();
			}
		});
		dMonth.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(opgTimeSelect.getValue().toString()=="Monthly")
				{
					if(dMonth.getValue()!=null)
					{
						cmbUnitDataLoad();
					}
				}
				else
				{
					if(dFromDate.getValue()!=null && dToDate.getValue()!=null)
					{
						cmbUnitDataLoad();
					}
				}
			}
		});
		dFromDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(opgTimeSelect.getValue().toString()=="Monthly")
				{
					if(dMonth.getValue()!=null)
					{
						cmbUnitDataLoad();
					}
				}
				else
				{
					if(dFromDate.getValue()!=null && dToDate.getValue()!=null)
					{
						cmbUnitDataLoad();
					}
				}
			}
		});
		dToDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(opgTimeSelect.getValue().toString()=="Monthly")
				{
					if(dMonth.getValue()!=null)
					{
						cmbUnitDataLoad();
					}
				}
				else
				{
					if(dFromDate.getValue()!=null && dToDate.getValue()!=null)
					{
						cmbUnitDataLoad();
					}
				}
			}
		});
		cmbUnit.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(cmbUnit.getValue()!=null)
				{
					cmbDepartmentDataLoad();
				}
			}
		});
		cmbDepartment.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(cmbUnit.getValue()!=null)
				{
					if(cmbDepartment.getValue()!=null)
					{
						cmbSectionDataLoad();
					}
				}
			}
		});
		chkDepartmentAll.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				if(chkDepartmentAll.booleanValue()){
					cmbDepartment.setValue(null);
					cmbDepartment.setEnabled(false);
					cmbSectionDataLoad();
				}
				else{
					cmbDepartment.setEnabled(true);
				}
			}
		});
		cmbSection.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(cmbUnit.getValue()!=null)
				{
					if(cmbSection.getValue()!=null)
					{
						cmbSeparationType.removeAllItems();
						cmbSeparationDataLoad();
					}
				}
			}
		});
		chkSectionAll.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				if(chkSectionAll.booleanValue()){
					cmbSection.setValue(null);
					cmbSection.setEnabled(false);
					cmbSeparationDataLoad();
				}
				else{
					cmbSection.setEnabled(true);
				}
			}
		});
		chkSeparationTypell.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				if(chkSeparationTypell.booleanValue()){
					cmbSeparationType.setValue(null);
					cmbSeparationType.setEnabled(false);
				}
				else{
					cmbSeparationType.setEnabled(true);
				}
			}
		});
		cButton.btnPreview.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbSeparationType.getValue()!=null || chkSeparationTypell.booleanValue())
				{
					if(cmbUnit.getValue()!=null)
					{
						if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
						{
							if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
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
				else
				{
					showNotification("Warning","Select Separation Type",Notification.TYPE_WARNING_MESSAGE);
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
	private void opgTimeSelectAction()
	{
		if(opgTimeSelect.getValue().toString()=="Monthly")
		{
			opgActionWiseVisible(true);
			dFromDate.setValue(new Date());
			dToDate.setValue(new Date());
			dMonth.setValue(new Date());
			
			cmbUnit.removeAllItems();
			chkSectionAll.setValue(false);
			cmbSection.setEnabled(true);
			cmbSection.removeAllItems();
			chkSeparationTypell.setValue(false);
			cmbSeparationType.setEnabled(true);
			cmbSeparationType.removeAllItems();
			if(dMonth.getValue()!=null)
			{
				cmbUnitDataLoad();
			}
		}
		else
		{
			opgActionWiseVisible(false);
			dFromDate.setValue(new Date());
			dToDate.setValue(new Date());
			dMonth.setValue(new Date());
			
			cmbUnit.removeAllItems();
			chkSectionAll.setValue(false);
			cmbSection.setEnabled(true);
			cmbSection.removeAllItems();
			chkSeparationTypell.setValue(false);
			cmbSeparationType.setEnabled(true);
			cmbSeparationType.removeAllItems();
			if(dFromDate.getValue()!=null && dToDate.getValue()!=null)
			{
				cmbUnitDataLoad();
			}
		}
	}
	
	private void reportpreview()
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		String loanType= "",report="",query="",vEmployeeStatus="%";
		if(!chkSeparationTypell.booleanValue())
		{
			vEmployeeStatus=cmbSeparationType.getValue().toString();
		}	
		
		try
		{
			//"Monthly","Between Date
			if(opgTimeSelect.getValue().toString().equals("Monthly"))
			{
				report="report/account/hrmModule/RptMonthlyEmployeeList.jasper";
				query="select vEmployeeCode,a.vEmployeeName,b.vDesignation,vUnitName,vSectionName,vEmployeeType,vStatusDate,bStatus," +
						"(select iRank from tbDesignationInfo where vDesignationId=b.vDesignationId)iRank,vEmployeeStatus,vDepartmentId,vDepartmentName " +
						"from tbEmpOfficialPersonalInfo a " +
						"inner join tbEmpDesignationInfo b on a.vEmployeeId=b.vEmployeeId " +
						"where bStatus=0 and YEAR(vStatusDate) = YEAR('"+dDbFormat.format(dMonth.getValue())+"') " +
						"and month(vStatusDate) = month('"+dDbFormat.format(dMonth.getValue())+"') " +
						"and vUnitId like '"+(cmbUnit.getValue()!=null?cmbUnit.getValue():"")+"' and vDepartmentId like '"+(cmbDepartment.getValue()!=null?cmbDepartment.getValue():"%")+"' " +
						"and vSectionId like '"+(cmbSection.getValue()!=null?cmbSection.getValue():"%")+"' and vEmployeeStatus like '"+vEmployeeStatus+"' " +
						"order by vUnitName,vDepartmentName,vSectionName,iRank,dJoiningDate";
			}
			else if(opgTimeSelect.getValue().toString().equals("Between Date"))
			{
				report="report/account/hrmModule/RptDateBetweenEmployeeList.jasper";
				query="select vEmployeeCode,a.vEmployeeName,b.vDesignation,vUnitName,vSectionName,vEmployeeType,vStatusDate,bStatus," +
						"(select iRank from tbDesignationInfo where vDesignationId=b.vDesignationId)iRank,vEmployeeStatus,vDepartmentId,vDepartmentName " +
						"from tbEmpOfficialPersonalInfo a " +
						"inner join tbEmpDesignationInfo b on a.vEmployeeId=b.vEmployeeId " +
						"where bStatus=0 and vStatusDate between '"+dDbFormat.format(dFromDate.getValue())+"' and '"+dDbFormat.format(dToDate.getValue())+"' " +
						"and vUnitId like '"+(cmbUnit.getValue()!=null?cmbUnit.getValue():"")+"' and vDepartmentId like '"+(cmbDepartment.getValue()!=null?cmbDepartment.getValue():"%")+"' " +
						"and vSectionId like '"+(cmbSection.getValue()!=null?cmbSection.getValue():"%")+"' and vEmployeeStatus like'"+vEmployeeStatus+"' " +
						"order by vUnitName,vDepartmentName,vSectionName,iRank,dJoiningDate";
			}
			
			System.out.println("Report Query: "+query);

			if(queryValueCheck(query))
			{
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				hm.put("company", cmbUnit.getItemCaption(cmbUnit.getValue()));
				hm.put("address", txtAddress.getValue().toString());
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("userName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
				hm.put("dMonth", dDbMonth.format(dMonth.getValue()).toString());
				hm.put("dDate", dFromDate.getValue());
				hm.put("fromDate", dFromDate.getValue());
				hm.put("toDate", dToDate.getValue());
				hm.put("SysDate",reportTime.getTime);
				hm.put("logo", txtPath.getValue().toString().isEmpty()?"0":txtPath.getValue().toString());
				hm.put("branch", "Project Name : "+cmbUnit.getItemCaption(cmbUnit.getValue().toString()));
				hm.put("sql", query);

				Window win = new ReportViewer(hm,report,
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
		setHeight("280px");

		opgTimeSelect=new OptionGroup("",timeSelect);
		opgTimeSelect.select("Monthly");
		opgTimeSelect.setImmediate(true);
		opgTimeSelect.setStyleName("horizontal");
		mainLayout.addComponent(opgTimeSelect, "top:10.0px; left:150px;");

		// lblFromDate
		lblFromDate = new Label("From Date :");
		lblFromDate.setImmediate(false);
		lblFromDate.setWidth("100.0%");
		lblFromDate.setHeight("-1px");
		mainLayout.addComponent(lblFromDate,"top:40.0px; left:30.0px;");
		lblFromDate.setVisible(false);
		// dFromDate
		dFromDate = new PopupDateField();
		dFromDate.setImmediate(true);
		dFromDate.setWidth("110px");
		dFromDate.setHeight("-1px");
		dFromDate.setDateFormat("dd-MM-yyyy");
		dFromDate.setValue(new java.util.Date());
		dFromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dFromDate, "top:38.0px; left:135.0px;");
		dFromDate.setVisible(false);
		//lblMonth
		lblMonth = new Label("Month :");
		lblMonth.setImmediate(false);
		lblMonth.setWidth("100.0%");
		lblMonth.setHeight("-1px");
		mainLayout.addComponent(lblMonth,"top:40.0px; left:30.0px;");
		
		// dMonth
		dMonth = new PopupDateField();
		dMonth.setImmediate(true);
		dMonth.setWidth("150px");
		dMonth.setHeight("-1px");
		dMonth.setDateFormat("MMMMMMMMM-yyyy");
		dMonth.setValue(new java.util.Date());
		dMonth.setResolution(PopupDateField.RESOLUTION_MONTH);
		mainLayout.addComponent(dMonth, "top:38.0px; left:135.0px;");
		
		// lblToDate
		lblToDate = new Label("To");
		lblToDate.setImmediate(false);
		lblToDate.setWidth("100.0%");
		lblToDate.setHeight("-1px");
		mainLayout.addComponent(lblToDate,"top:40.0px; left:250.0px;");
		lblToDate.setVisible(false);

		// dToDate
		dToDate = new PopupDateField();
		dToDate.setImmediate(true);
		dToDate.setWidth("110px");
		dToDate.setHeight("-1px");
		dToDate.setDateFormat("dd-MM-yyyy");
		dToDate.setValue(new java.util.Date());
		dToDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dToDate, "top:38.0px; left:268.0px;");
		dToDate.setVisible(false);
		
		cmbUnit=new ComboBox();
		cmbUnit.setWidth("250.0px");
		cmbUnit.setHeight("-1px");
		cmbUnit.setImmediate(true);
		cmbUnit.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Project : "), "top:70.0px;left:30.0px;");
		mainLayout.addComponent(cmbUnit, "top:68.0px;left:135.0px");
		
		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("250px");
		mainLayout.addComponent(new Label("Department : "), "top:100px; left:30.0px;");
		mainLayout.addComponent(cmbDepartment, "top:98px; left:135px;");

		chkDepartmentAll = new CheckBox("All");
		chkDepartmentAll.setImmediate(true);
		mainLayout.addComponent(chkDepartmentAll,"top:100px; left:385px;");
		
		cmbSection=new ComboBox();
		cmbSection.setWidth("250.0px");
		cmbSection.setHeight("-1px");
		cmbSection.setImmediate(true);
		mainLayout.addComponent(new Label("Section : "), "top:130.0px;left:30.0px;");
		mainLayout.addComponent(cmbSection, "top:128px;left:135.0px");
		chkSectionAll.setImmediate(true);
		mainLayout.addComponent(chkSectionAll, "top:130px;left:385.0px");

		cmbSeparationType=new ComboBox();
		cmbSeparationType.setWidth("250.0px");
		cmbSeparationType.setHeight("-1px");
		cmbSeparationType.setImmediate(true);
		cmbSeparationType.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Separation Type : "), "top:160px;left:30.0px;");
		mainLayout.addComponent(cmbSeparationType, "top:158px;left:135.0px");
		chkSeparationTypell.setImmediate(true);
		mainLayout.addComponent(chkSeparationTypell, "top:158px;left:385.0px");
		
		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:160px;left:150px;");

		RadioBtnGroup.setVisible(false);
		//mainLayout.addComponent(new Label("_______________________________________________________________________________"), "top:175.0px;left:20.0px;right:20.0px;");
		mainLayout.addComponent(cButton,"top:200.opx; left:135.0px");

		return mainLayout;
	}
}
