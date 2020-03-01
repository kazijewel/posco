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
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class RptEmployeeAnnualEvaluationSheet extends Window
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;
	private CommonMethod cm;
	private String menuId = "";
	private OptionGroup opgReport;

	private ComboBox cmbEmployee,cmbUnit;
	private ComboBox cmbSection,cmbDepartment,cmbDesignation,cmbReviewer;
	private CheckBox chkDepartment = new CheckBox("All");
	private CheckBox chkSectionAll = new CheckBox("All");
	private CheckBox chkEmployeeAll = new CheckBox("All");
	private CheckBox chkDesignationAll = new CheckBox("All");
	private CheckBox chkUnitAll = new CheckBox("All");
	private TextField txtWarningLetter,txtReviewerTital;
	private PopupDateField dDate;
	
	public SimpleDateFormat dFdmy = new SimpleDateFormat("dd-MMM-yyyy");

	private Label lblComboLabel ;

	private static final List<String> type1 = Arrays.asList(new String[]{"PDF","Other"});

	boolean isPreview=false;

	private ReportDate reportTime = new ReportDate();

	private CommonButton cButton= new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");


	ArrayList<Component> allComp = new ArrayList<Component>();
	
	public RptEmployeeAnnualEvaluationSheet(SessionBean sessionBean,String menuId)
	{
		this.sessionBean=sessionBean;
		this.setCaption("EMPLOYEE ANNUAL EVLUATION SHEET :: "+sessionBean.getCompany());
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		buildMainLayout();
		setBtnAction();
		setContent(mainLayout);
		cmbUnitDataLoad();
		cmManagerDataLoad();
		focusMove();
		cmbUnit.focus();
		cmbDepartment.setEnabled(false);
		cmbSection.setEnabled(false);
		cmbDesignation.setEnabled(false);
		cmbEmployee.setEnabled(false);
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

	private void focusMove()
	{
		allComp.add(cmbUnit);
		allComp.add(cmbSection);
		allComp.add(cmbEmployee);
		allComp.add(cButton.btnPreview);
		new FocusMoveByEnter(this,allComp);
	}
	private void cmManagerDataLoad() {
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "select distinct vEmployeeId,vEmployeeName from tbEmpOfficialPersonalInfo where vEmployeeCode like '%PK%' order by vEmployeeName";
			List<?> list = session.createSQLQuery(sql).list();
			cmbReviewer.removeAllItems();
			for(Iterator<?> iter = list.iterator();iter.hasNext();)
			{
				Object element[] = (Object[]) iter.next();
				cmbReviewer.addItem(element[0].toString());
				cmbReviewer.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification(exp+"cmManagerDataLoad",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	
	public void cmbReviewerDataLoad()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "select distinct vDesignationId,vDesignationName from tbEmpOfficialPersonalInfo where vEmployeeId='"+cmbEmployee.getValue()+"'";
			List<?> list = session.createSQLQuery(sql).list();
			txtReviewerTital.setValue("");
			for(Iterator<?> iter = list.iterator();iter.hasNext();)
			{
				Object element[] = (Object[]) iter.next();
				txtReviewerTital.setValue(element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification(exp+"cmbReviewerDataLoad",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void cmbUnitDataLoad() {
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "select vUnitId,vUnitName from tbEmpOfficialPersonalInfo order by vUnitName";
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
	private void setBtnAction()
	{
		cButton.btnPreview.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event)
			{
				if(cmbUnit.getValue()!=null || chkUnitAll.booleanValue())
				{
					if(cmbDepartment.getValue()!=null || chkDepartment.booleanValue()==true)
					{
						if(cmbSection.getValue()!=null || chkSectionAll.booleanValue()==true)
						{
							if(cmbDesignation.getValue()!=null || chkDesignationAll.booleanValue())
							{
								if(cmbEmployee.getValue()!=null || chkEmployeeAll.booleanValue()==true)
								{
									if(cmbReviewer.getValue()!=null)
									{
										if(!txtWarningLetter.getValue().toString().isEmpty())
										{
											reportView();
										}
										else
										{
											getParent().showNotification("Please Select Provide Worning Letter", Notification.TYPE_WARNING_MESSAGE);
										}
									}
									else
									{
										getParent().showNotification("Please Select Reviewer", Notification.TYPE_WARNING_MESSAGE);
									}
								}
								else
								{
									getParent().showNotification("Please Select "+lblComboLabel.getValue().toString()+"", Notification.TYPE_WARNING_MESSAGE);
								}
							}
							else
							{
								getParent().showNotification("Please Select Designation", Notification.TYPE_WARNING_MESSAGE);
							}
						}
						else
						{
							getParent().showNotification("Please Select Section", Notification.TYPE_WARNING_MESSAGE);
						}

					}
					else
					{
						getParent().showNotification("Please Select Department", Notification.TYPE_WARNING_MESSAGE);
					}

				}
				else
				{
					getParent().showNotification("Please Select Project", Notification.TYPE_WARNING_MESSAGE);
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
		cmbUnit.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbDepartment.removeAllItems();
				cmbDepartment.setEnabled(false);
				chkDepartment.setValue(false);
				if(cmbUnit.getValue()!=null)
				{
					chkUnitAll.setValue(false);
					cmbDepartment.setEnabled(true);
					cmbDepartmentDataLoad();
				}
			}
		});
		chkUnitAll.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				cmbDepartment.removeAllItems();
				cmbDepartment.setEnabled(false);
				chkDepartment.setValue(false);
				if(chkUnitAll.booleanValue())
				{
					cmbUnit.setEnabled(false);
					cmbUnit.setValue(null);
					cmbDepartment.setEnabled(true);
					cmbDepartmentDataLoad();
				}
				else
				{
					cmbUnit.setEnabled(true);
					cmbDepartment.setEnabled(false);
				}
			
			}
		});
		cmbDepartment.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSection.removeAllItems();
				cmbSection.setEnabled(false);
				chkSectionAll.setValue(false);
				if(cmbUnit.getValue()!=null || chkUnitAll.booleanValue())
				{
					if(cmbDepartment.getValue()!=null)
					{
						chkDepartment.setValue(false);
						cmbSection.setEnabled(true);
						cmbSectionDataLoad();
					}
				}
			}
		});
		chkDepartment.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				cmbSection.removeAllItems();
				cmbSection.setEnabled(false);
				chkSectionAll.setValue(false);
				if(cmbUnit.getValue()!=null || chkUnitAll.booleanValue())
				{
					if(chkDepartment.booleanValue())
					{
						cmbDepartment.setEnabled(false);
						cmbDepartment.setValue(null);
						cmbSection.setEnabled(true);
						cmbSectionDataLoad();
					}
					else
					{
						cmbDepartment.setEnabled(true);
						cmbSection.setEnabled(false);
					}
				}
				else
				{
					chkDepartment.setValue(false);
					showNotification("Warning..","Select Project Please!",Notification.TYPE_HUMANIZED_MESSAGE);
				}
			}
		});
		cmbSection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbDesignation.removeAllItems();
				cmbDesignation.setEnabled(false);
				chkDesignationAll.setValue(false);
				if(cmbUnit.getValue()!=null || chkUnitAll.booleanValue())
				{
					if(cmbSection.getValue()!=null)
					{
						chkSectionAll.setValue(false);
						cmbDesignation.setEnabled(true);
						cmbDesignationDataLoad();
					}
				}
			}
		});
		chkSectionAll.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				cmbDesignation.removeAllItems();
				cmbDesignation.setEnabled(false);
				chkDesignationAll.setValue(false);
				if(cmbDepartment.getValue()!=null || chkDepartment.booleanValue())
				{
					if(chkSectionAll.booleanValue())
					{
						cmbSection.setEnabled(false);
						cmbSection.setValue(null);
						cmbDesignation.setEnabled(true);
						cmbDesignationDataLoad();
					}
					else
					{
						cmbSection.setEnabled(true);
						cmbDesignation.setEnabled(false);
					}
				}
				else
				{
					chkSectionAll.setValue(false);
					showNotification("Warning..","Select Department Please!",Notification.TYPE_HUMANIZED_MESSAGE);
				}
			}
		});
		cmbDesignation.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				cmbEmployee.removeAllItems();
				cmbEmployee.setEnabled(false);
				if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
				{
					if(cmbDesignation.getValue()!=null)
					{
						cmbEmployee.setEnabled(true);
						chkDesignationAll.setValue(false);
						cmbEmployeeNameDataAdd();
					}
				}
			}
		});
		chkDesignationAll.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				cmbEmployee.removeAllItems();
				cmbEmployee.setEnabled(false);
				if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
				{
					if(chkDesignationAll.booleanValue())
					{
						cmbDesignation.setEnabled(false);
						cmbDesignation.setValue(null);
						cmbEmployee.setEnabled(true);
						cmbEmployeeNameDataAdd();
					}
					else
					{
						cmbDesignation.setEnabled(true);
						cmbEmployee.setEnabled(false);
					}
				}
				else
				{
					chkDesignationAll.setValue(false);
					showNotification("Warning..","Select Section Please!",Notification.TYPE_HUMANIZED_MESSAGE);
				}
			}
		});
		chkEmployeeAll.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				if(chkEmployeeAll.booleanValue())
				{
					cmbEmployee.setEnabled(false);
					cmbEmployee.setValue(null);
				}
				else
				{
					cmbEmployee.setEnabled(true);
				}
			}
		});
		cmbReviewer.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbReviewer.getValue()!=null)
				{
					cmbReviewerDataLoad();
				}
			}
		});
	}
	private void cmbDepartmentDataLoad()
	{
		String unitId="%";
		if(cmbUnit.getValue()!=null)
		{
			unitId=cmbUnit.getValue().toString();
		}
		try{
			String sql="select distinct vDepartmentId,vDepartmentName from tbEmpOfficialPersonalInfo " +
					"where vUnitId like '"+unitId+"' order by vDepartmentName";
			//System.out.println("cmbDepartmentDataLoad: "+sql);
			
			Iterator<?> iter=dbService(sql);
			while(iter.hasNext())
			{
				Object[] element=(Object[])iter.next();
				cmbDepartment.addItem(element[0]);
				cmbDepartment.setItemCaption(element[0], element[1].toString());
			}
		}catch(Exception exp)
		{
			showNotification("Error..to department Load",""+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private void cmbSectionDataLoad()
	{
		String deptId="%",unitId="%";
		if(cmbDepartment.getValue()!=null)
		{
			deptId=cmbDepartment.getValue().toString();
		}
		if(cmbUnit.getValue()!=null)
		{
			unitId=cmbUnit.getValue().toString();
		}
		try{
			String sql="select distinct vSectionId,vSectionName from tbEmpOfficialPersonalInfo " +
					"where vUnitId like '"+unitId+"' and vDepartmentId like '"+deptId+"'  order by vSectionName";
			//System.out.println("cmbSectionDataLoad: "+sql);
			
			Iterator<?> iter=dbService(sql);			
			while(iter.hasNext())
			{
				Object[] element=(Object[])iter.next();
				cmbSection.addItem(element[0]);
				cmbSection.setItemCaption(element[0], element[1].toString());
			}
		}catch(Exception exp)
		{
			showNotification("Error..to Section Load",""+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private void cmbDesignationDataLoad()
	{
		String deptId="%";
		String empType="%";
		String secId="%",unitId="%";
		if(cmbDepartment.getValue()!=null)
		{
			deptId=cmbDepartment.getValue().toString();
		}
		if(cmbSection.getValue()!=null)
		{
			secId=cmbSection.getValue().toString();
		}
		if(cmbUnit.getValue()!=null)
		{
			unitId=cmbUnit.getValue().toString();
		}
		try{
			String sql="select distinct vDesignationId,vDesignationName from tbEmpOfficialPersonalInfo " +
					"where vUnitId like '"+unitId+"' and vDepartmentId like '"+deptId+"' and vSectionId like '"+secId+"' " +
					"order by vDesignationName";
			//System.out.println("cmbDesignationDataLoad: "+sql);
			
			Iterator<?> iter=dbService(sql);
			while(iter.hasNext())
			{
				Object[] element=(Object[])iter.next();
				cmbDesignation.addItem(element[0]);
				cmbDesignation.setItemCaption(element[0], element[1].toString());
			}
		}catch(Exception exp)
		{
			showNotification("Error..to Designation Load",""+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private void cmbEmployeeNameDataAdd()
	{
		cmbEmployee.removeAllItems();
		String query="";
		String deptId="%",secId="%",desId="%",unitId="%";
		if(cmbDepartment.getValue()!=null)
		{
			deptId=cmbDepartment.getValue().toString();
		}
		if(cmbSection.getValue()!=null)
		{
			secId=cmbSection.getValue().toString();
		}
		if(cmbDesignation.getValue()!=null)
		{
			desId=cmbDesignation.getValue().toString();
		}
		if(cmbUnit.getValue()!=null)
		{
			unitId=cmbUnit.getValue().toString();
		}
		try
		{
			query="select vEmployeeId,vEmployeeCode,vEmployeeName from tbEmpOfficialPersonalInfo " +
					"where vUnitId like '"+unitId+"' and vDepartmentId like '"+deptId+"' and vSectionId like '"+secId+"' " +
					"and vDesignationId like '"+desId+"' order by vEmployeeCode";
			
			//System.out.println("cmbEmployeeNameDataAdd: "+query);
			
			Iterator<?> iter=dbService(query);

			while(iter.hasNext())
			{
				Object [] element=(Object[])iter.next();
				cmbEmployee.addItem(element[0]);
				cmbEmployee.setItemCaption(element[0], element[1]+"-"+element[2]);
			}
		}
		catch (Exception exp)
		{
			showNotification("cmbEmployeeNameDataAdd", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}
	public Iterator<?> dbService(String sql)
	{
		Iterator<?> iter=null;
		Session session=null;
		try{
			session=SessionFactoryUtil.getInstance().openSession();
			iter=session.createSQLQuery(sql).list().iterator();
		}catch(Exception exp)
		{
			showNotification(""+exp);
		}
		return iter;
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
	private void reportView()
	{
		String rptName="";
		String query=null;
		String empId="%";
		
		if(!chkEmployeeAll.booleanValue())
		{
			empId=cmbEmployee.getValue().toString();
		}
		try
		{
			query = "select * from funEmployeeAnnualEvaluationSheet "
					+ "("
						+ "'"+sessionBean.dfDb.format(dDate.getValue())+"',"
						+ "'"+empId+"'" 
					+ ")";
			
			System.out.println("reportView: "+query);

			rptName="RptEmployeeAnnualEvaluationSheet.jasper";

			if(queryValueCheck(query))
			{
				HashMap <String, Object> hm = new HashMap <String, Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("dDate",dFdmy.format(dDate.getValue()));
				hm.put("WarningLetter", txtWarningLetter.getValue());
				hm.put("Reviewer", cmbReviewer.getItemCaption(cmbReviewer.getValue()));
				hm.put("ReviewerTital", txtReviewerTital.getValue());
				hm.put("developer", sessionBean.getDeveloperAddress());
				hm.put("UserName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
				hm.put("SysDate",reportTime.getTime);
				
				hm.put("sql", query);
				
				Window win = new ReportViewer(hm,"report/account/hrmModule/"+rptName,
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",true);

				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);
			}
			else
			{
				showNotification("Warning..","No data found!",Notification.TYPE_WARNING_MESSAGE);
			}
		}

		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
			System.out.println(exp);
		}
	}

	private AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		setWidth("500px");
		setHeight("370px");

		cmbUnit=new ComboBox();
		cmbUnit.setWidth("250.0px");
		cmbUnit.setHeight("-1px");
		cmbUnit.setImmediate(true);
		mainLayout.addComponent(new Label("Project : "), "top:10.0px;left:30.0px;");
		mainLayout.addComponent(cmbUnit, "top:8.0px;left:120.0px");

		chkUnitAll = new CheckBox("All");
		chkUnitAll.setImmediate(true);
		chkUnitAll.setWidth("-1px");
		chkUnitAll.setHeight("-1px");
		mainLayout.addComponent(chkUnitAll,"top:10px; left:370px;");

		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("250.0px");
		cmbDepartment.setHeight("-1px");
		mainLayout.addComponent(new Label("Department :"), "top:40px; left:30.0px;");
		mainLayout.addComponent(cmbDepartment, "top:38px; left:120.0px;");
		

		chkDepartment = new CheckBox("All");
		chkDepartment.setImmediate(true);
		chkDepartment.setWidth("-1px");
		chkDepartment.setHeight("-1px");
		mainLayout.addComponent(chkDepartment,"top:40px; left:370px;");
		

		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("250.0px");
		cmbSection.setHeight("-1px");
		mainLayout.addComponent(new Label("Section :"), "top:70px; left:30.0px;");
		mainLayout.addComponent(cmbSection, "top:68px; left:120.0px;");

		chkSectionAll = new CheckBox("All");
		chkSectionAll.setImmediate(true);
		chkSectionAll.setWidth("-1px");
		chkSectionAll.setHeight("-1px");
		mainLayout.addComponent(chkSectionAll,"top:70px; left:370px;");

		cmbDesignation = new ComboBox();
		cmbDesignation.setImmediate(true);
		cmbDesignation.setWidth("250.0px");
		cmbDesignation.setHeight("-1px");
		mainLayout.addComponent(new Label("Designation :"), "top:100px; left:30.0px;");
		mainLayout.addComponent(cmbDesignation, "top:98px; left:120.0px;");

		chkDesignationAll = new CheckBox("All");
		chkDesignationAll.setImmediate(true);
		chkDesignationAll.setWidth("-1px");
		chkDesignationAll.setHeight("-1px");
		mainLayout.addComponent(chkDesignationAll,"top:100px; left:370px;");

		cmbEmployee = new ComboBox();
		cmbEmployee.setImmediate(true);
		cmbEmployee.setWidth("250.0px");
		cmbEmployee.setHeight("-1px");
		mainLayout.addComponent(new Label("Employee"),"top:130px; left:30px");
		mainLayout.addComponent(cmbEmployee, "top:128px; left:120.0px;");
		cmbEmployee.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		chkEmployeeAll = new CheckBox("All");
		chkEmployeeAll.setImmediate(true);
		chkEmployeeAll.setWidth("-1px");
		chkEmployeeAll.setHeight("-1px");
		mainLayout.addComponent(chkEmployeeAll,"top:130px; left:370px;");
		chkEmployeeAll.setVisible(false);
		
		cmbReviewer = new ComboBox();
		cmbReviewer.setImmediate(true);
		cmbReviewer.setWidth("250.0px");
		cmbReviewer.setHeight("-1px");
		mainLayout.addComponent(new Label("Reviewer"),"top:160px; left:30px");
		mainLayout.addComponent(cmbReviewer, "top:158px; left:120.0px;");
		cmbReviewer.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		
		txtReviewerTital = new TextField();
		txtReviewerTital.setImmediate(true);
		txtReviewerTital.setWidth("210.0px");
		txtReviewerTital.setHeight("-1px");
		mainLayout.addComponent(new Label("Reviewer Tital"),"top:190px; left:30px");
		mainLayout.addComponent(txtReviewerTital, "top:188px; left:120.0px;");
		txtReviewerTital.setEnabled(false);

		txtWarningLetter = new TextField();
		txtWarningLetter.setImmediate(true);
		txtWarningLetter.setWidth("60.0px");
		txtWarningLetter.setHeight("-1px");
		mainLayout.addComponent(new Label("Warning Letter"),"top:220px; left:30px");
		mainLayout.addComponent(txtWarningLetter, "top:218px; left:120.0px;");
		
		dDate=new PopupDateField();
		dDate.setImmediate(true);
		dDate.setWidth("110px");
		dDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dDate.setDateFormat("dd-MM-yyyy");
		dDate.setValue(new java.util.Date());
		mainLayout.addComponent(new Label("Date: "),"top:250px; left:30.0px;");
		mainLayout.addComponent(dDate, "top:248px; left:120.0px;");

		opgReport = new OptionGroup("",type1);
		opgReport.setImmediate(true);
		opgReport.setStyleName("horizontal");
		opgReport.setValue("PDF");
		mainLayout.addComponent(opgReport, "top:270px;left:250.0px;");
		opgReport.setVisible(false);

		cButton.btnPreview.setImmediate(true);
		mainLayout.addComponent(cButton, "bottom:15px; left:170.0px;");

		return mainLayout;
	}
}
