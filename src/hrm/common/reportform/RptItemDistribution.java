package hrm.common.reportform;

import java.util.Arrays;
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
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Window;

public class RptItemDistribution extends Window{
	private AbsoluteLayout mainLayout;
	private SessionBean sessionBean;
	
	//private CheckBox checkAll;
	private ComboBox cmbUnit,cmbItemName;
	//private PopupDateField date;
	CommonButton cButton=new CommonButton("","","","","","","","Preview","","Exit");
	private static final List<String> reportView = Arrays.asList(new String[]{"PDF","Other"});
	//SimpleDateFormat dFormat=new SimpleDateFormat("yyyy-MM-dd");
	private OptionGroup btnRadio;
	ReportDate reportTime=new ReportDate();
	private CommonMethod cm;
	private String menuId = "";
	
	public RptItemDistribution(SessionBean sessionBean,String menuId)
	{
		this.sessionBean=sessionBean;
		this.center();
		this.setResizable(false);
		this.setStyleName("cwindow");
		this.setCaption("ITEM DISTRIBUTION ::"+sessionBean.getCompany());
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		
		this.setWidth("400px");
		this.setHeight("220px");
		this.setContent(addLayout());
		setEventAction();
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
	private void setEventAction()
	{
		cButton.btnPreview.addListener(new ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				if(cmbItemName.getValue()!=null)
				{
					reportShow();
				}
			}
		});
		cButton.btnExit.addListener(new ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				close();
			}
		});
		cmbUnit.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(cmbUnit.getValue()!=null)
				{
					cmbItemName.removeAllItems();
					cmbItemNameDataLoad();
				}
			}
		});
	}
	private void cmbUnitDataLoad()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql="select distinct vUnitId,vUnitName from tbItemDistribution order by vUnitName";
			Iterator<?> iter=session.createSQLQuery(sql).list().iterator();
			while(iter.hasNext())
			{
				Object element[]=(Object[])iter.next();
				cmbUnit.addItem(element[0]);
				cmbUnit.setItemCaption(element[0], element[1].toString());
				
			}
		}catch(Exception exp)
		{
			showNotification("cmbUnitDataLoad :"+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private void cmbItemNameDataLoad()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql="select distinct iTransactionId,vItemName from tbItemDistribution where vUnitId ='"+cmbUnit.getValue()+"' order by vItemName";
			Iterator<?> iter=session.createSQLQuery(sql).list().iterator();
			while(iter.hasNext())
			{
				Object element[]=(Object[])iter.next();
				cmbItemName.addItem(element[0]);
				cmbItemName.setItemCaption(element[0], element[1].toString());
				
			}
		}catch(Exception exp)
		{
			showNotification("cmbItemNameDataLoad :"+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private void reportShow()
	{
		String item=cmbItemName.getValue().toString();
		String report="";
		
		reportTime = new ReportDate();
		ReportOption RadioBtn = new ReportOption(btnRadio.getValue().toString());

		try
		{
			String query = "select id.vItemId,id.vItemName,id.vUnit,id.vUnitName,id.vVenue,id.iTransactionId,id.dDate,id.vTime,"+
					" id.vSectionId,id.vSectionName,epo.vEmployeeId,epo.vEmployeeCode,epo.vEmployeeName,di.vDesignationId,di.vDesignation,"+
					" epo.dJoiningDate,id.mQty,id.vRemarks,id.vDepartmentId,id.vDepartmentName from tbItemDistribution id "+
					" inner join tbEmpOfficialPersonalInfo epo on epo.vEmployeeId=id.vEmpId "+
					" inner join tbDesignationInfo di on di.vDesignationId=id.vDesignationId "+
					" where id.vUnitId like '"+(cmbUnit.getValue()==null?"%":cmbUnit.getValue().toString())+"' and id.iTransactionId like '"+(cmbItemName.getValue()==null?"%":cmbItemName.getValue().toString())+"' and vFlag=1 order by id.vUnit,id.vSectionName,di.iRank,epo.dJoiningDate";
			
			System.out.println("ShowReport: "+query);
			report="report/account/hrmModule/rptItemDistribution.jasper";
			
			if(queryValueCheck(query))
			{
				HashMap <String,Object>  hm = new HashMap <String,Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone",sessionBean.getCompanyContact());
				hm.put("username", sessionBean.getUserName()+" "+sessionBean.getUserIp());
				hm.put("SysDate",reportTime.getTime);
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("developer", "Software Solution by : E-Vision Software Ltd.|| helpline : 01755-506044 || www.eslctg.com");
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
			this.getParent().showNotification("Error "+exp,Notification.TYPE_ERROR_MESSAGE);
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
	private AbsoluteLayout addLayout() {
		mainLayout=new AbsoluteLayout();
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		
		cmbUnit=new ComboBox();
		cmbUnit.setWidth("230px");
		cmbUnit.setHeight("-1px");
		cmbUnit.setImmediate(true);
		mainLayout.addComponent(new Label("Project :"),"top:30px; left:30px");
		mainLayout.addComponent(cmbUnit,"top:28px; left:100px");
		
		cmbItemName=new ComboBox();
		cmbItemName.setWidth("230px");
		cmbItemName.setHeight("-1px");
		cmbItemName.setImmediate(true);
		mainLayout.addComponent(new Label("Item Name :"),"top:60px; left:30px");
		mainLayout.addComponent(cmbItemName,"top:58px; left:100px");
		
		/*checkAll=new CheckBox("All");
		checkAll.setImmediate(true);
		mainLayout.addComponent(checkAll,"top:60px; left:333px");*/
		
		btnRadio=new OptionGroup("",reportView);
		btnRadio.setImmediate(true);
		btnRadio.setValue("PDF");
		btnRadio.setStyleName("horizontal");
		mainLayout.addComponent(btnRadio,"top:100px; left:130px");
		btnRadio.setVisible(false);
		//mainLayout.addComponent(new Label("<b>________________________________________________________________________________</b>",Label.CONTENT_XHTML),"top:110px; left:5px; right:5px");
		
		mainLayout.addComponent(cButton,"top:140px; left:130px");
		return mainLayout;
	}
}
