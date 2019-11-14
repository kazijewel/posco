package com.appform.hrmModule;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.CommonMethod;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.ReportDate;
import com.common.share.ReportOption;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.data.Property.*;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;

public class EmployeeRequsitionForm extends Window{
	SessionBean sessionBean=new SessionBean();
	AbsoluteLayout mainLayout;
	ComboBox cmbDesignation,cmbUnit,cmbName,cmbDesignationRep,cmbDepartment;
	TextRead txtRequisitionNO;
	CheckBox chkNewPosition,chkApprovedPosition,chkReplacement;
	TextField txtNumber,txtReportToWhom;
	PopupDateField dRequisitionDate,dRequirement;
	OptionGroup opgEmployeeType;
	/*private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});
	private OptionGroup RadioBtnGroup=new OptionGroup("",type1);;*/
	
	private CommonButton cButton= new CommonButton( "New",  "Save",  "Edit",  "",  "Refresh",  "Find", "", "Preview","","Exit");

	FormLayout fLayout,fLayout2;
	List<String> TypeList=Arrays.asList(new String[]{"Regular","Temporary","Casual","Part Timer","Internship"});
	ArrayList<Component> allComponent=new ArrayList<Component>();
	boolean isUpdate=false;
	private ReportDate reportTime = new ReportDate();
	TextField txtPath=new TextField();
	TextField txtfind=new TextField();
	SimpleDateFormat ddbformate=new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat dformate=new SimpleDateFormat("dd-MM-yyyy");
	
	private SimpleDateFormat dDbFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dDbFormatReport = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat dDbMonth = new SimpleDateFormat("MMMMMMMMM-yyyy");
	private CommonMethod cm;
	private String menuId = "";
	public EmployeeRequsitionForm(SessionBean sessionBean,String menuId) {
		this.sessionBean=sessionBean;
		this.setImmediate(true);
		this.setHeight("500px");
		this.setWidth("750px");
		this.setCaption("EMPLOYEE REQUSITION FORM :: "+sessionBean.getCompany());
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		this.setContent(buildMainLayout());
		EvenAction();
		cmbEnable(false);
		cmpInit(false);
		btnInit(true);
		focusMoveByEnter();
		cmbDesignationLoad();
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
	private void focusMoveByEnter() {
		allComponent.add(cmbDesignation);
		allComponent.add(txtNumber);
		allComponent.add(cmbUnit);
		allComponent.add(cmbName);
		allComponent.add(txtReportToWhom);
		allComponent.add(txtRequisitionNO);
		allComponent.add(dRequisitionDate);
		allComponent.add(dRequirement);
		allComponent.add(cButton.btnSave);
		new FocusMoveByEnter(this,allComponent);
	}

	public void EvenAction(){
		chkNewPosition.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				if(chkNewPosition.booleanValue()){
					chkGroup(false);
					chkNewPosition.setValue(true);
					cmbEnable(false);
					cmbName.setValue(null);
					cmbDesignationRep.setValue(null);
					cmbDepartment.setValue(null);
					txtReportToWhom.setValue("");

				}
			}
		});
		chkApprovedPosition.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				if(chkApprovedPosition.booleanValue()){
					chkGroup(false);
					chkApprovedPosition.setValue(true);
					cmbEnable(false);
					cmbName.setValue(null);
					cmbDesignationRep.setValue(null);
					cmbDepartment.setValue(null);
					txtReportToWhom.setValue("");
				}
			}
		});
		chkReplacement.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				chkReplacement.setValue(true);
				chkReplacementEvent();
			}
		});
		cmbUnit.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(cmbUnit.getValue()!=null&&chkReplacement.booleanValue()){
					//for load new Employee name by changing Unit name. When checkbox replacement is selected 
					cmbEmployeeNameLoad(cmbUnit.getValue().toString());
				}
				else{
					cmbName.removeAllItems();
				}
			}
		});
		cmbName.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(cmbName.getValue()!=null){
					cmbDesignationRep.removeAllItems();
					cmbDepartment.removeAllItems();
					cmbNameSelectEvent(cmbName.getValue().toString());
				}
				else{
					cmbDesignationRep.setValue(null);
					cmbDepartment.setValue(null);
				}
			}
		});
		cButton.btnNew.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				newButtonEvent();
			}
		});
		cButton.btnSave.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				if(checkvalidation()){
					saveButtonEvent();
				}
			}
		});
		cButton.btnEdit.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				editButtonEvent();
			}
		});
		cButton.btnRefresh.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				refreshButtonEvent();
			}
		});
		cButton.btnExit.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				close();
			}
		});
		cButton.btnFind.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				findButtonEvent();
				cButton.btnEdit.setEnabled(true);
				cButton.btnPreview.setEnabled(true);
			}
		});
		
		cButton.btnPreview.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				txtPathDataLoad();
				reportpreview();
			}
		});

	}
	private void reportpreview()
	{
		//ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		String joinDate= "",report="",query="";
		
		try
		{
			
				report="report/account/hrmModule/RptEmployeeRequsitionForm.jasper";
				query="select vReQuisitionNo,vDesignationName,vEmployeeNumber,vUnitName," +
						"vEmployeeType,vRequitmentType,vRepEmployeeCode,vRepEmployeeName," +
						"vRepDesignationName,vRepDepartmentName,vReporttoWhom,dRequsitionDate," +
						"dRequirementDate,vUserName,vUserIp,dEntryTime from " +
						"tbEmployeeRequisitionForm where vReQuisitionNo like '"+txtRequisitionNO.getValue().toString()+"'";
			
			if(queryValueCheck(query))
			{	
				System.out.println("Report Query: "+query);
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone",sessionBean.getCompanyContact());
				hm.put("username", sessionBean.getUserName()+" "+sessionBean.getUserIp());
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
	private void txtPathDataLoad() {
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		
		try
		{
			String sql = "select vUnitId,imageLoc from tbUnitInfo where vUnitId='"+cmbUnit.getValue().toString()+"' ";
			
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator();iter.hasNext();)
			{
				Object element[] = (Object[]) iter.next();
				txtPath.setValue(element[1].toString());
				
				System.out.println("Id : "+element[0]+" Path  :"+element[1]);
				
			}
		}
		catch(Exception exp)
		{
			showNotification(exp+" Image path set :",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void chkReplacementEvent() {
		if(cmbUnit.getValue()!=null){/////dependency Unit Select 
			if(chkReplacement.booleanValue()){
				cmbEmployeeNameLoad(cmbUnit.getValue().toString());
				chkGroup(false);
				chkReplacement.setValue(true);
				cmbEnable(true);
			}
			else{
				cmbEnable(false);
				cmbName.removeAllItems();
			}
		}
		else{
			chkReplacement.setValue(false);
			showNotification(null,"Please Select Unit Name",Notification.TYPE_WARNING_MESSAGE);
		}

	}
	private void findButtonEvent(){
		EmployeeRequsitionFromFindWindow findwin=new EmployeeRequsitionFromFindWindow(sessionBean, txtfind);
		findwin.addListener(new CloseListener() {
			public void windowClose(CloseEvent e) {
				if(txtfind.getValue().toString().length()>0){
					txtClear();
					findInit(txtfind.getValue().toString());
				}
			}
		});
		getParent().addWindow(findwin);
	}
	protected void findInit(String RequsitionNo) {
		
		String sql="select vReQuisitionNo,vDesignationId,vEmployeeNumber," +
				"vUnitId,vEmployeeType,vRequitmentType,vRepEmployeeId," +
				"vRepEmployeeCode,vRepEmployeeName,vRepDesignationId,vRepDesignationName," +
				"vRepDepartmentId,vRepDepartmentName,vReporttoWhom,dRequsitionDate," +
				"dRequirementDate,vUserName,vUserIp,dEntryTime " +
				"from tbEmployeeRequisitionForm where vReQuisitionNo like '"+RequsitionNo+"'";

		/*String sql="select vDesignationId,vEmployeeNumber,vUnitId,vEmployeeType,vRequitmentType," +
				"vRepEmployeeId,vReporttoWhom,vReQuisitionNo,dRequsitionDate,dRequirementDate " +
				"from tbEmployeeRequisitionForm where vReQuisitionNo like '"+RequsitionNo+"'";*/
		Iterator<?> iter=dbService(sql);
		while(iter.hasNext()){
			Object element[]=(Object[]) iter.next();
			txtRequisitionNO.setValue(element[0].toString());
			cmbDesignation.setValue(element[1].toString());
			txtNumber.setValue(element[2].toString());
			cmbUnit.setValue(element[3].toString());
			opgEmployeeType.setValue(element[4].toString());
			if(element[5].toString().equals("Replacement")){
				chkReplacement.setValue(true);
				cmbEmployeeNameLoad(cmbUnit.getValue().toString());
				cmbDesignationRep.removeAllItems();
				cmbDepartment.removeAllItems();
				if(!cmbName.equals(element[6].toString())){
					cmbName.addItem(element[6].toString());
					cmbName.setItemCaption(element[6].toString(),element[8].toString()+"-Code:"+element[7].toString());
					cmbName.setValue(element[6].toString());
				}
				cmbDesignationRep.addItem(element[9].toString());
				cmbDesignationRep.setItemCaption(element[9].toString(),element[10].toString());
				cmbDepartment.addItem(element[11].toString());
				cmbDepartment.setItemCaption(element[11].toString(),element[12].toString());
				cmbDepartment.setValue(element[9]);
				cmbDesignationRep.setValue(element[11]);
			}
			if(element[5].toString().equals("NewPosition")){
				chkNewPosition.setValue(true);
			}
			if(element[5].toString().equals("ApprovedPosition")){
				chkApprovedPosition.setValue(true);
			}
			txtReportToWhom.setValue(element[13].toString());

			dRequisitionDate.setValue(element[14]);
			dRequirement.setValue(element[15]);
			isUpdate=true;
		}
	}
	private void getRequsitionNo(){
		String sql="select year(CURRENT_TIMESTAMP)as year,isnull(max(Cast(SUBSTRING(vReQuisitionNo," +
				"CHARINDEX('-',vReQuisitionNo)+1,LEN(vReQuisitionNo)-CHARINDEX('-',vReQuisitionNo))as int)),0)+1 " +
				"as id from tbEmployeeRequisitionForm";
		System.out.println(sql);
		Iterator<?> iter=dbService(sql);
		while(iter.hasNext()){
			Object element[]=(Object[]) iter.next();
			txtRequisitionNO.setValue(element[0].toString()+"-00"+element[1].toString());
		}
	}
	private void cmbNameSelectEvent(String name){
		String sql="select a.vDepartmentId,a.vDepartmentName,b.vDesignationId,b.vDesignation from " +
				"tbEmpOfficialPersonalInfo a inner join tbEmpDesignationInfo b on " +
				"a.vEmployeeId=b.vEmployeeId where a.vEmployeeId='"+name+"'";
		System.out.println(sql);
		Iterator<?> iter=dbService(sql);
		while(iter.hasNext()){
			Object element[]=(Object[]) iter.next();
			cmbDepartment.addItem(element[0].toString());
			cmbDepartment.setItemCaption(element[0].toString(),element[1].toString());
			cmbDesignationRep.addItem(element[2].toString());
			cmbDesignationRep.setItemCaption(element[2].toString(),element[3].toString());
			cmbDepartment.setValue(element[0]);
			cmbDesignationRep.setValue(element[2]);
		}
	}
	private void refreshButtonEvent(){
		txtClear();
		cmpInit(false);
		btnInit(true);
		cmbEnable(false);
		isUpdate=false;
	}
	public void newButtonEvent(){
		btnInit(false);
		cmpInit(true);
		txtClear();
		cButton.btnPreview.setEnabled(false);
		cmbDesignationLoad();
		cmbUnitDataLoad();
		cmbEnable(false);
		cmbDesignation.focus();
		getRequsitionNo();
	}
	private void saveButtonEvent(){
		MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, 
				isUpdate?"Do you want to Update  information?":"Do you want to save  information", 
						new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), 
						new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
		mb.show(new EventListener()
		{
			public void buttonClicked(ButtonType buttonType)
			{
				if(buttonType == ButtonType.YES)
				{
					if(isUpdate){
						Session session=SessionFactoryUtil.getInstance().openSession();
						Transaction tx=session.beginTransaction();
						if(deleteData(session,tx)){
							if(insertData(session, tx)){
								Notification n=new Notification("All Information Update Successfully!","",Notification.TYPE_TRAY_NOTIFICATION);
								n.setPosition(Notification.POSITION_TOP_RIGHT);
								showNotification(n);
								btnInit(true);
								cmpInit(false);
								cButton.btnEdit.setEnabled(false);
								cButton.btnPreview.setEnabled(true);
							}
						}
					}
					else{
						Session session=SessionFactoryUtil.getInstance().openSession();
						Transaction tx=session.beginTransaction();
						if(insertData(session,tx)){
							Notification n=new Notification("All Information Save Successfully!","",Notification.TYPE_TRAY_NOTIFICATION);
							n.setPosition(Notification.POSITION_TOP_RIGHT);
							showNotification(n);
							btnInit(true);
							cmpInit(false);
							cButton.btnEdit.setEnabled(false);
							cButton.btnPreview.setEnabled(true);
						}
					}
				}
			}
		});
	}
	private boolean deleteData(Session session,Transaction tx){
		try {
			String sql="delete from tbEmployeeRequisitionForm where vReQuisitionNo='"+txtRequisitionNO.getValue().toString()+"';";
			session.createSQLQuery(sql).executeUpdate();
			return true;
		}
		catch (Exception e) {
			showNotification(null,"deleteData"+e,Notification.TYPE_ERROR_MESSAGE);
		}
		return false;
	}
	public boolean insertData(Session session,Transaction tx){

		String  repEmployeeId=" ",repEmployeeCode=" ",repEmployeeName=" ",repDesignationid=" ",
				repDesignationName=" ",repDepartmantId=" ",repDepartmantname=" ",
				employeeType=" ",requirtmenttype=" ",type="new";
		try {
			if(chkReplacement.booleanValue()){
				Iterator<?> iter=null;
				String sql="select vEmployeeId,vEmployeeCode,vEmployeeName,vDepartmentId,vDepartmentName," +
						" vDesignationId,vDesignationName from tbEmpOfficialPersonalInfo where vEmployeeId='"+cmbName.getValue().toString()+"'";
				System.out.println(sql);
				iter=session.createSQLQuery(sql).list().iterator();
				while(iter.hasNext()){
					Object element[]=(Object[]) iter.next();
					repEmployeeId=element[0].toString();
					repEmployeeCode=element[1].toString();
					repEmployeeName=element[2].toString();
					repDepartmantId=element[3].toString();
					repDepartmantname=element[4].toString();
					repDesignationid=element[5].toString();
					repDesignationName=element[6].toString();
				}

			}
			employeeType=opgEmployeeType.getValue().toString();
			if(chkApprovedPosition.booleanValue()){
				requirtmenttype="ApprovedPosition";
			}
			if(chkNewPosition.booleanValue()){
				requirtmenttype="NewPosition";
			}
			if(chkReplacement.booleanValue()){
				requirtmenttype="Replacement";
			}
			if(isUpdate){
				type="Update";
			}

			String sqlinsert="insert into tbEmployeeRequisitionForm " +
					"(vReQuisitionNo,vDesignationId,vDesignationName,vEmployeeNumber,vUnitId," +
					"vUnitName,vEmployeeType,vRequitmentType,vRepEmployeeId,vRepEmployeeCode,vRepEmployeeName,vRepDesignationId," +
					"vRepDesignationName,vRepDepartmentId,vRepDepartmentName,vReporttoWhom," +
					"dRequsitionDate,dRequirementDate,vUserName," +
					"vUserIp,dEntryTime) " +
					"values('"+txtRequisitionNO.getValue().toString()+"','"+cmbDesignation.getValue().toString()+"'," +
					"'"+cmbDesignation.getItemCaption(cmbDesignation.getValue().toString())+"','"+txtNumber.getValue().toString()+"'," +
					"'"+cmbUnit.getValue().toString()+"','"+cmbUnit.getItemCaption(cmbUnit.getValue().toString())+"'," +
					"'"+employeeType+"','"+requirtmenttype+"'," +
					"'"+repEmployeeId+"','"+repEmployeeCode+"','"+repEmployeeName+"'," +
					"'"+repDesignationid+"','"+repDesignationName+"','"+repDepartmantId+"','"+repDepartmantname+"'," +
					"'"+txtReportToWhom.getValue().toString()+"'," +
					"'"+ddbformate.format(dRequisitionDate.getValue())+"'," +
					"'"+ddbformate.format(dRequirement.getValue())+"','"+sessionBean.getUserId()+"'," +
					"'"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP)";
			System.out.println(sqlinsert);
			session.createSQLQuery(sqlinsert).executeUpdate();

			String sqlUdinsert="insert into tbUdEmployeeRequisitionForm " +
					"(vReQuisitionNo,vDesignationId,vDesignationName,vEmployeeNumber,vUnitId," +
					"vUnitName,vEmployeeType,vRequitmentType,vRepEmployeeId,vRepEmployeeCode,vRepEmployeeName,vRepDesignationId," +
					"vRepDesignationName,vRepDepartmentId,vRepDepartmentName,vReporttoWhom," +
					"dRequsitionDate,dRequirementDate,vUserName," +
					"vUserIp,dEntryTime,vType )" +
					"values('"+txtRequisitionNO.getValue().toString()+"','"+cmbDesignation.getValue().toString()+"'," +
					"'"+cmbDesignation.getItemCaption(cmbDesignation.getValue().toString())+"','"+txtNumber.getValue().toString()+"'," +
					"'"+cmbUnit.getValue().toString()+"','"+cmbUnit.getItemCaption(cmbUnit.getValue().toString())+"'," +
					"'"+employeeType+"','"+requirtmenttype+"'," +
					"'"+repEmployeeId+"','"+repEmployeeCode+"','"+repEmployeeName+"'," +
					"'"+repDesignationid+"','"+repDesignationName+"','"+repDepartmantId+"','"+repDepartmantname+"'," +
					"'"+txtReportToWhom.getValue().toString()+"'," +
					"'"+ddbformate.format(dRequisitionDate.getValue())+"'," +
					"'"+ddbformate.format(dRequirement.getValue())+"','"+sessionBean.getUserId()+"'," +
					"'"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP,'"+type+"')";
			System.out.println(sqlUdinsert);
			session.createSQLQuery(sqlUdinsert).executeUpdate();
			tx.commit();
			return true;
		}
		catch (Exception e) {
			if(tx!=null){
				tx.rollback();
			}
			showNotification(null,"InsertData"+e,Notification.TYPE_ERROR_MESSAGE);
		}
		finally{
			if(session!=null){
				session.close();
			}
		}
		return false;
	}
	private void editButtonEvent(){
		if(txtRequisitionNO.getValue().toString().length()>0){
			isUpdate=true;
			btnInit(false);
			cmpInit(true);
			
			if(chkReplacement.booleanValue()){
				cmbEnable(true);
			}
		}
		else {
			showNotification(null,"There has not any Data for Edit",
					Notification.TYPE_WARNING_MESSAGE);
		}
		
	}
	private boolean checkQuery(String sql) {
		Iterator<?> iter=dbService(sql);
		if(iter.hasNext()){
			return true;
		}
		else{
			showNotification(null,"There has not any Employee in your selected unit ",
					Notification.TYPE_WARNING_MESSAGE);
		}
		return false;
	}
	private void cmbEmployeeNameLoad(String unit){
		cmbName.removeAllItems();
		String sql="select vEmployeeId,vEmployeeName,vEmployeeCode from tbEmpOfficialPersonalInfo  " +
				"where vUnitid='"+unit+"' order by vEmployeeName";
		if(checkQuery(sql)){
			Iterator<?> iter=dbService(sql);
			while(iter.hasNext()){
				Object element[]=(Object[]) iter.next();
				cmbName.addItem(element[0].toString());
				cmbName.setItemCaption(element[0].toString(),element[1].toString()+"-Code:"+element[2].toString());
			}
		}
	}
	private void cmbDesignationLoad(){
		cmbDesignation.removeAllItems();
		String sql="select distinct vDesignationId,vDesignation from tbEmpDesignationInfo " +
				"order by vDesignation";
		System.out.println(sql);
		Iterator<?> iter=dbService(sql);
		while(iter.hasNext()){
			Object element[]=(Object[]) iter.next();
			cmbDesignation.addItem(element[0].toString());
			cmbDesignation.setItemCaption(element[0].toString(),element[1].toString());
		}
	}
	private void cmbUnitDataLoad(){
		cmbUnit.removeAllItems();
		String sql="select distinct vUnitId,vUnitName from tbUnitInfo order by vUnitName";
		System.out.println(sql);
		Iterator<?> iter=dbService(sql);
		while(iter.hasNext()){
			Object element[]=(Object[]) iter.next();
			cmbUnit.addItem(element[0].toString());
			cmbUnit.setItemCaption(element[0].toString(),element[1].toString());
		}
	}
	public boolean checkvalidation(){
		if(cmbDesignation.getValue()!=null){
			if(!txtNumber.getValue().toString().isEmpty()){
				if(cmbUnit.getValue()!=null){
					if(chkApprovedPosition.booleanValue()||chkNewPosition.booleanValue()||chkReplacement.booleanValue()){
						if(chkReplacement.booleanValue()){
							if(cmbName.getValue()!=null){
								if(!txtReportToWhom.getValue().toString().isEmpty()){
									return true;
								}
								else{
									showNotification(null,"Please Insert Report to Whom",Notification.TYPE_WARNING_MESSAGE);
								}
							}
							else{
								showNotification(null,"Please Select Employee Name",Notification.TYPE_WARNING_MESSAGE);
							}
						}
						else{
							return true;
						}
					}
					else{
						showNotification(null,"Please Select Recruitment Type",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else{
					showNotification(null,"Please Select Unit",Notification.TYPE_WARNING_MESSAGE);
				}
			}
			else{
				showNotification(null,"Please Insert Employee Number ",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else{
			showNotification(null,"Please Select Designation ",Notification.TYPE_WARNING_MESSAGE);
		}
		return false;
	}
	private Iterator dbService(String sql){
		
		System.out.println(sql);
		Session session=null;
		Iterator iter=null;
		try {
			session=SessionFactoryUtil.getInstance().openSession();
			iter=session.createSQLQuery(sql).list().iterator();
		} 
		catch (Exception e) {
			showNotification(null,""+e,Notification.TYPE_ERROR_MESSAGE);
		}
		finally{
			if(session!=null){
				session.close();
			}
		}
		return iter;
	}
	private void btnInit(boolean b) {
		cButton.btnNew.setEnabled(b);
		cButton.btnSave.setEnabled(!b);
		cButton.btnEdit.setEnabled(b);
		cButton.btnRefresh.setEnabled(!b);
		cButton.btnFind.setEnabled(b);
		cButton.btnPreview.setEnabled(!b);
	}
	public void cmpInit(boolean b){
		cmbDesignation.setEnabled(b);
		cmbName.setEnabled(b);
		txtReportToWhom.setEnabled(b);
		txtNumber.setEnabled(b);
		cmbUnit.setEnabled(b);
		opgEmployeeType.setEnabled(b);
		chkApprovedPosition.setEnabled(b);
		chkNewPosition.setEnabled(b);
		chkReplacement.setEnabled(b);
		txtRequisitionNO.setEnabled(b);
		dRequisitionDate.setEnabled(b);
		dRequirement.setEnabled(b);
		
		cmbDesignationRep.setEnabled(false);
		cmbDepartment.setEnabled(false);
	}
	public void txtClear(){
		cmbDesignation.setValue(null);
		txtNumber.setValue("");
		cmbUnit.setValue(null);
		opgEmployeeType.setValue("Regular");
		chkGroup(false);
		txtReportToWhom.setValue("");
		txtRequisitionNO.setValue("");
		dRequisitionDate.setValue(new Date());
		dRequirement.setValue(new Date());
	}
	public void chkGroup(boolean b){
		chkApprovedPosition.setValue(b);
		chkNewPosition.setValue(b);
		chkReplacement.setValue(b);
	}
	public void cmbEnable(boolean b){
		cmbName.setEnabled(b);
		txtReportToWhom.setEnabled(b);
	}
	public AbsoluteLayout buildMainLayout(){
		mainLayout=new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");

		cmbDesignation=new ComboBox();
		cmbDesignation.setHeight("-1px");
		cmbDesignation.setWidth("250px");
		cmbDesignation.setImmediate(true);
		cmbDesignation.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		cmbDesignation.setNewItemsAllowed(false);
		mainLayout.addComponent(new Label("Designation :"),"top:40px;left:30px");
		mainLayout.addComponent(cmbDesignation,"top:38px;left:130px");

		txtNumber=new TextField();
		txtNumber.setHeight("-1px");
		txtNumber.setWidth("150px");
		txtNumber.setImmediate(true);
		mainLayout.addComponent(new Label("Number :"),"top:70px;left:30px");
		mainLayout.addComponent(txtNumber,"top:68px;left:130px");

		cmbUnit=new ComboBox();
		cmbUnit.setHeight("-1px");
		cmbUnit.setWidth("250px");
		cmbUnit.setImmediate(true);
		cmbUnit.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		cmbUnit.setNewItemsAllowed(false);
		mainLayout.addComponent(new Label("Project :"),"top:100px;left:30px");
		mainLayout.addComponent(cmbUnit,"top:98px;left:130px");

		opgEmployeeType=new OptionGroup("",TypeList);
		opgEmployeeType.setStyleName("horizontal");
		opgEmployeeType.select("Regular");
		mainLayout.addComponent(new Label("Employee Type :"),"top:130px;left:30px");
		mainLayout.addComponent(opgEmployeeType, "top:128.0px;left:130px;");

		///Recruitment//////////
		mainLayout.addComponent(new Label("Recruitment Type :"),"top:160px;left:30px");


		chkNewPosition=new CheckBox("New Position");
		chkNewPosition.setImmediate(true);
		mainLayout.addComponent(chkNewPosition,"top:180px;left:130px");

		chkApprovedPosition=new CheckBox("Approved Position");
		chkApprovedPosition.setImmediate(true);
		mainLayout.addComponent(chkApprovedPosition,"top:200px;left:130px");

		chkReplacement=new CheckBox("Replacement");
		chkReplacement.setImmediate(true);
		mainLayout.addComponent(chkReplacement,"top:220px;left:130px");


		fLayout=new FormLayout();
		fLayout.setHeight("200px");
		fLayout.setWidth("370");
		fLayout.setImmediate(true);

		cmbName=new ComboBox();
		//cmbName=new ComboBox("Name :");
		cmbName.setHeight("-1px");
		cmbName.setWidth("250px");
		cmbName.setImmediate(true);
		cmbName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		cmbName.setNewItemsAllowed(false);
		mainLayout.addComponent(new Label("Name :"),"top:250px;left:30px");
		mainLayout.addComponent(cmbName,"top:248px;left:130px");



		///////////Replecement////////////

		cmbDesignationRep=new ComboBox();
		//cmbDesignationRep=new ComboBox("Designation :");
		cmbDesignationRep.setHeight("-1px");
		cmbDesignationRep.setWidth("250px");
		cmbDesignationRep.setImmediate(true);
		cmbDesignationRep.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		cmbDesignationRep.setNewItemsAllowed(false);

		/*txtDesignationRep=new TextRead();
		txtDesignationRep.setHeight("-1px");
		txtDesignationRep.setWidth("250px");
		txtDesignationRep.setImmediate(true);*/
		mainLayout.addComponent(new Label("Designation :"),"top:280px;left:30px");
		mainLayout.addComponent(cmbDesignationRep,"top:278px;left:130px");
		//mainLayout.addComponent(txtDesignationRep,"top:278px;left:130px");


		//// Department /////////

		cmbDepartment=new ComboBox();
		//cmbDepartment=new ComboBox("Department :");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setWidth("250px");
		cmbDepartment.setImmediate(true);
		cmbDepartment.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		cmbDepartment.setNewItemsAllowed(false);

		/*txtDepartment=new TextRead();
		txtDepartment.setHeight("-1px");
		txtDepartment.setWidth("250px");
		txtDepartment.setImmediate(true);*/
		mainLayout.addComponent(new Label("Department :"),"top:310px;left:30px");
		mainLayout.addComponent(cmbDepartment,"top:308px;left:130px");
		//mainLayout.addComponent(txtDepartment,"top:308px;left:130px");



		txtReportToWhom=new TextField();
		//txtReportToWhom=new TextField("Report to Whom :");
		txtReportToWhom.setHeight("-1px");
		txtReportToWhom.setWidth("150px");
		txtReportToWhom.setImmediate(true);
		mainLayout.addComponent(new Label("Report to Whom :"),"top:340px;left:30px");
		mainLayout.addComponent(txtReportToWhom,"top:338px;left:130px");

		/////fLayout2
		fLayout2=new FormLayout();
		fLayout2.setHeight("130px");
		fLayout2.setWidth("280");
		fLayout2.setImmediate(true);



		txtRequisitionNO=new TextRead("Requisition Code");
		txtRequisitionNO.setHeight("-1px");
		txtRequisitionNO.setWidth("100px");
		txtRequisitionNO.setImmediate(true);

		dRequisitionDate=new PopupDateField("Requisition Date");
		dRequisitionDate.setHeight("-1px");
		dRequisitionDate.setWidth("100px");
		dRequisitionDate.setImmediate(true);
		dRequisitionDate.setDateFormat("dd-MM-yyyy");
		dRequisitionDate.setValue(new Date());
		dRequisitionDate.setResolution(PopupDateField.RESOLUTION_DAY);

		dRequirement=new PopupDateField("Requirement Date");
		dRequirement.setHeight("-1px");
		dRequirement.setWidth("100px");
		dRequirement.setImmediate(true);
		dRequirement.setDateFormat("dd-MM-yyyy");
		dRequirement.setValue(new Date());
		dRequirement.setResolution(PopupDateField.RESOLUTION_DAY);

		/*fLayout.addComponent(cmbName);
		fLayout.addComponent(cmbDesignationRep);
		fLayout.addComponent(cmbDepartment);
		fLayout.addComponent(txtReportToWhom);*/



		fLayout2.addComponent(txtRequisitionNO);
		fLayout2.addComponent(dRequisitionDate);
		fLayout2.addComponent(dRequirement);


		//mainLayout.addComponent(fLayout,"top:10px;left:470px");
		mainLayout.addComponent(fLayout2,"top:15px;left:500px");
		mainLayout.addComponent(cButton,"top:400px;left:80px;");
		return mainLayout;
	}

}
