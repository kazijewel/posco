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
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class RptEmployeeClearenceFrom extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;
	FormLayout fLayout=new FormLayout();
	
	private ComboBox cmbSection,cmbUnit,cmbEmployeeId,cmbDepartment;
	CheckBox chkSection=new CheckBox("All");
	private CheckBox chkDepartmentAll = new CheckBox("All");
	PopupDateField dResSubDate,dResEffDate;
	TextField txtAlternativecontactNo;
	
	private CommonButton cButton = new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	private SimpleDateFormat dDbFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dDbFormatReport = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat dDbMonth = new SimpleDateFormat("MMMMMMMMM-yyyy");
	
	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});
	
	private OptionGroup opgActive;
	List<String> isActive=Arrays.asList(new String[]{"Active","InActive"});
	private CommonMethod cm;
	private String menuId = "";
	TextField txtPath=new TextField();
	public RptEmployeeClearenceFrom(SessionBean sessionBean,String menuId) 
	{
		this.sessionBean=sessionBean;
		this.setCaption("Employee Clearence From :: "+sessionBean.getCompany());
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		buildMainLayout();
		setContent(mainLayout);
		cmbUnitDataLoad();  
		setEventAction();
		cmpEnable(false);
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
	private Iterator<?> dbService(String sql){
		Iterator<?> iter=null;
		Session session=null;
		try {
			session=SessionFactoryUtil.getInstance().openSession();
			iter=session.createSQLQuery(sql).list().iterator();
		} 
		catch (Exception e) {
			showNotification(null,e+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{
			if(session!=null){
				session.close();
			}
		}
		return iter;
	}

	private void cmbSectionDataLoad() {
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		String deptId="%";
		if(!chkDepartmentAll.booleanValue())
		{
			deptId=cmbDepartment.getValue().toString();
		}
		try
		{
			String sql = "select distinct vSectionId,vSectionName from tbEmpOfficialPersonalInfo " +
					"where vUnitId like '"+cmbUnit.getValue()+"' and vDepartmentId like '"+deptId+"' " +
					"order by vSectionName";
			List<?> list = session.createSQLQuery(sql).list();
			System.out.println(sql);
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
			String sql = "select distinct vDepartmentId,vDepartmentName from tbEmpOfficialPersonalInfo " +
					"where vUnitId like '"+cmbUnit.getValue()+"' " +
					"order by vDepartmentName";
			List<?> list = session.createSQLQuery(sql).list();
			System.out.println(sql);
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
			String sql = "select vUnitId,vUnitName from tbEmpOfficialPersonalInfo";
			List<?> list = session.createSQLQuery(sql).list();
			System.out.println(sql);
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
	private void EmployeeIdLoad() {
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		String deptId="%",secId="%";
		if(!chkDepartmentAll.booleanValue())
		{
			deptId=cmbDepartment.getValue().toString();
		}
		if(!chkSection.booleanValue())
		{
			secId=cmbSection.getValue().toString();
		}
		try
		{
			cmbEmployeeId.removeAllItems();
			String sql="";
			if(opgActive.getValue().equals("Active")){
				sql="select vEmployeeId,vEmployeeName,vEmployeeCode from tbEmpOfficialPersonalInfo where vDepartmentId like '"+deptId+"' and " +
						"vSectionId like '"+secId+"' and vUnitId like '"+cmbUnit.getValue()+"' and bStatus=1";
			}
			else{
				sql="select vEmployeeId,vEmployeeName,vEmployeeCode from tbEmpOfficialPersonalInfo where vDepartmentId like '"+deptId+"' and " +
						"vSectionId like '"+secId+"' and vUnitId like '"+cmbUnit.getValue()+"' and bStatus=0";
			}
			System.out.println(sql);
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator();iter.hasNext();)
			{
				Object element[] = (Object[]) iter.next();
				cmbEmployeeId.addItem(element[0].toString());
				cmbEmployeeId.setItemCaption(element[0].toString(),element[2]+"-"+ element[1].toString());
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
		cButton.btnPreview.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				if(checkValidation()){
					reportpreview();
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
		cmbUnit.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(cmbUnit.getValue()!=null)
				{
					cmbSection.removeAllItems();
					cmbDepartmentDataLoad();
					cmpEnable(false);
					cmbDepartment.setEnabled(true);
					chkDepartmentAll.setEnabled(true);
					chkDepartmentAll.setValue(false);
				}
				else{
					cmbDepartment.removeAllItems();
					cmpEnable(false);
					cmbDepartment.setEnabled(true);
					chkDepartmentAll.setEnabled(true);
					chkDepartmentAll.setValue(false);
				}
			}
		});
		
		cmbDepartment.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				chkSection.setValue(false);
				if(cmbDepartment.getValue()!=null){
					cmbSection.setEnabled(true);
					chkSection.setEnabled(true);
					cmbSectionDataLoad();
				}
				else{
					cmbSection.removeAllItems();
					cmbSection.setEnabled(false);
					chkSection.setEnabled(false);
				}
			}
		});
		chkDepartmentAll.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				chkSection.setValue(false);
				if(cmbUnit.getValue()!=null){
					if(chkDepartmentAll.booleanValue()){
						cmbDepartment.setValue(null);
						cmbDepartment.setEnabled(false);
						cmbSection.setEnabled(true);
						chkSection.setEnabled(true);
						cmbSectionDataLoad();
					}
					else{
						cmbDepartment.setEnabled(true);
						cmbSection.setEnabled(false);
						chkSection.setEnabled(false);
						cmbSection.removeAllItems();
					}
				}
				else{
					showNotification(null,"Please Select Project",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});
		cmbSection.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(cmbSection.getValue()!=null){
					EmployeeIdLoad();
					cmbEmployeeId.setEnabled(true);
				}
				else{
					cmbEmployeeId.removeAllItems();
					cmbEmployeeId.setEnabled(false);
				}
			}
		});
		chkSection.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue()){
					if(chkSection.booleanValue()){
						cmbSection.setValue(null);
						cmbSection.setEnabled(false);
						cmbEmployeeId.setEnabled(true);
						EmployeeIdLoad();
					}
					else{
						cmbSection.setEnabled(true);
						cmbEmployeeId.setEnabled(false);
						cmbEmployeeId.removeAllItems();
					}
				}
				else{
					showNotification(null,"Please Select Department",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});
		opgActive.addListener(new ValueChangeListener() {
		
			public void valueChange(ValueChangeEvent event) {
				valueClear();
			}
		});
		cmbEmployeeId.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				dResEffDate.setValue(new Date());
				dResSubDate.setValue(new Date());
				txtAlternativecontactNo.setValue("");
			}
		});
		
	}
	public void cmpEnable(boolean b){
		cmbSection.setEnabled(b);
		cmbEmployeeId.setEnabled(b);
		chkDepartmentAll.setEnabled(b);
		chkSection.setEnabled(b);
		dResEffDate.setValue(new Date());
		dResSubDate.setValue(new Date());
		txtAlternativecontactNo.setValue("");
		cmbEmployeeId.removeAllItems();
		
	}
	public void valueClear(){
		cmbUnit.setValue(null);
		cmbDepartment.setValue(null);
		cmbSection.setValue(null);
		chkSection.setValue(false);
		cmbEmployeeId.removeAllItems();
		cmpEnable(false);
	}
	
	public boolean checkValidation(){
		if(cmbUnit.getValue()!=null)
		{
			if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
			{
				if(cmbSection.getValue()!=null || chkSection.booleanValue())
				{
					if(cmbEmployeeId.getValue()!=null){
						return true;
					}
					else
					{
						showNotification("Warning","Select Employee Name Please",Notification.TYPE_WARNING_MESSAGE);
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
		return false;
	}
	private void reportpreview()
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		String joinDate= "",report="",query="";
		
		try
		{
				report="report/account/hrmModule/RptEmployeeClearenceForm.jasper";
				query="select vEmployeeCode,vEmployeeName,dJoiningDate,vContactNo," +
						"vDesignationName from tbEmpOfficialPersonalInfo  where " +
						"vEmployeeId like '"+cmbEmployeeId.getValue()+"'";
			
				
			System.out.println("Report Query: "+query);

			if(queryValueCheck(query))
			{
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("userName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
				System.out.println(sessionBean.getUserName()+"  "+sessionBean.getUserIp());
				hm.put("SysDat",reportTime.getTime);
				hm.put("ResSubDate",dDbFormatReport.format(dResSubDate.getValue()));
				hm.put("ResEffDate",dDbFormatReport.format(dResEffDate.getValue()));
				hm.put("AltContactNo", txtAlternativecontactNo.getValue().toString().trim().isEmpty()
						?"":txtAlternativecontactNo.getValue().toString());
				hm.put("logo", sessionBean.getCompanyLogo());
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
		setWidth("550px");
		setHeight("380px");
		
		fLayout=new FormLayout();
		fLayout.setHeight("250px");
		fLayout.setWidth("440px");
		fLayout.setImmediate(true);
		
		opgActive = new OptionGroup("",isActive);
		opgActive.setImmediate(true);
		opgActive.setStyleName("horizontal");
		opgActive.setValue("Active");
		mainLayout.addComponent(opgActive, "top:20px;left:208px;"); 
		
		
		// top-level component properties
		cmbUnit=new ComboBox("Project :");
		cmbUnit.setWidth("250.0px");
		cmbUnit.setHeight("-1px");
		cmbUnit.setImmediate(true);
		fLayout.addComponent(cmbUnit);
		
		cmbDepartment=new ComboBox("Department :");
		cmbDepartment.setWidth("250.0px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setImmediate(true);
		fLayout.addComponent(cmbDepartment);
		
		chkDepartmentAll.setImmediate(true);
		mainLayout.addComponent(chkDepartmentAll,"top:90.0px;left:480.0px");
		
		cmbSection=new ComboBox("Section :");
		cmbSection.setWidth("250.0px");
		cmbSection.setHeight("-1px");
		cmbSection.setImmediate(true);
		fLayout.addComponent(cmbSection);
		
		chkSection.setImmediate(true);
		mainLayout.addComponent(chkSection,"top:120px;left:480.0px");
		
		cmbEmployeeId=new ComboBox("Employee ID :");
		cmbEmployeeId.setWidth("250.0px");
		cmbEmployeeId.setHeight("-1px");
		cmbEmployeeId.setImmediate(true);
		fLayout.addComponent(cmbEmployeeId);
		
		dResSubDate=new PopupDateField("Resignation Submission Date :");
		dResSubDate.setImmediate(true);
		dResSubDate.setWidth("150px");
		dResSubDate.setHeight("-1px");
		dResSubDate.setDateFormat("dd-MM-yyyy");
		dResSubDate.setValue(new Date());
		dResSubDate.setResolution(PopupDateField.RESOLUTION_DAY);
		fLayout.addComponent(dResSubDate);
		
		dResEffDate=new PopupDateField("Resignation Effective Date :");
		dResEffDate.setImmediate(true);
		dResEffDate.setWidth("150px");
		dResEffDate.setHeight("-1px");
		dResEffDate.setDateFormat("dd-MM-yyyy");
		dResEffDate.setValue(new Date());
		dResEffDate.setResolution(PopupDateField.RESOLUTION_DAY);
		fLayout.addComponent(dResEffDate);
		
		txtAlternativecontactNo=new TextField("Alternative Contact No :");
		txtAlternativecontactNo.setImmediate(true);
		txtAlternativecontactNo.setHeight("-1px");
		txtAlternativecontactNo.setWidth("150px");
		fLayout.addComponent(txtAlternativecontactNo);
		
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:250px;left:208px;");
		RadioBtnGroup.setVisible(false);
		/*chkEmployeeId.setImmediate(true);
		mainLayout.addComponent(chkEmployeeId, "top:87.0px;left:395.0px");*/
		mainLayout.addComponent(fLayout,"top:40px;left:40px");
		mainLayout.addComponent(cButton,"top:290.opx; left:170.0px");

		return mainLayout;
	}
}
