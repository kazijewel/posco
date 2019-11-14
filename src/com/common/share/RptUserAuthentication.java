package com.common.share;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.CommonMethod;
import com.common.share.ReportDate;
import com.common.share.ReportOption;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Component.Listener;
import com.vaadin.ui.Window.Notification;

public class RptUserAuthentication extends Window
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;


	private OptionGroup RadioBtnGroup;

	private Label lblGroup;
	private ComboBox cmbGroup;
	private CheckBox chkGroup;

	private String menuId = "";
	private CommonMethod cm;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});
	CommonButton button = new CommonButton("", "", "", "","","","","Preview","","Exit");
	private ReportDate reportTime = new ReportDate();

	public RptUserAuthentication(SessionBean sessionBean,String menuId)
	{
		this.sessionBean = sessionBean;
		this.menuId=menuId;
		this.setCaption("USER AUTHENTICATION REPORT :: "+this.sessionBean.getCompany());
		this.setResizable(false);
		this.menuId = menuId;
		cm = new CommonMethod(sessionBean);
		buildMainLayout();
		setContent(mainLayout);
		btnAction();
		cmbAddGroupData();
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
			{button.btnSave.setVisible(false);}
			if(!cm.isEdit)
			{button.btnEdit.setVisible(false);}
			if(!cm.isDelete)
			{button.btnDelete.setVisible(false);}
			if(!cm.isPreview)
			{button.btnPreview.setVisible(false);}
		}
		}
	}
	private void btnAction()
	{
		button.btnPreview.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if (cmbGroup.getValue()!=null || chkGroup.booleanValue()==true)
				{
						previewBtnAction();
				}
				else 
				{
					showNotification("Please Select User Name", Notification.TYPE_WARNING_MESSAGE);
					cmbGroup.focus();
				}
			}


		});

		chkGroup.addListener(new Listener()
		{
			public void componentEvent(Event event)
			{
				if(chkGroup.booleanValue()==true)
				{
					cmbGroup.setValue(null);
					cmbGroup.setEnabled(false);
				}
				else
				{
					cmbGroup.setEnabled(true);
				}
			}
		});


		button.btnExit.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});
	}

	public void cmbAddGroupData()
	{
		cmbGroup.removeAllItems();
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery("select vUserId,vUserName from tbUserInfo where vUserType not in ('Admin','Super Admin') order by vUserName").list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbGroup.addItem(element[0].toString());
				cmbGroup.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void previewBtnAction()
	{
		String queryAll = null;
		String Division ="";
		try
		{
			if(chkGroup.booleanValue()==true)
			{
				Division="%";
			}
			else
			{
				Division=cmbGroup.getValue().toString();
			}
			ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());

			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			HashMap<String, Object> hm = new HashMap<String, Object>();

			queryAll = "select * from (select distinct abc.vCompanyUserName,vUserType,a.dEntryTime,b.vModuleId," +
					"abc.vModuleName,abc.vManuType,vMenuId,vMenuCaption,iSave,iEdit,iDelete,iPreview,Block," +
					"(case when abc.vManuType='SETUP' then '1' when abc.vManuType='TRANSACTION' then '2' else '3' end) vManuTypeId " +
					"from (select vUserId,vCompanyUserName,vMenuId,vMenuCaption,0 iSave,0 iEdit,0 iDelete,0 iPreview,'Block' Block," +
					"vModuleName,vManuType from dbo.tbUserAuthentication union select vUserId,vUserName,vMenuId,vMenuName,iSave,iEdit," +
					"iDelete,iPreview,'Lock',vModuleName,vManuType from dbo.tbUserAccess )abc inner join tbUserInfo a on a.vUserId=abc.vUserId " +
					"inner join tbUserDetails b on b.vModuleName=abc.vModuleName where abc.vUserId like '"+Division+"' and vUserType!='Admin') xyz " +
					"order by vCompanyUserName,xyz.vModuleId,xyz.vManuTypeId,Block";
			
			System.out.println("Report Query: "+queryAll);

			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("companyId", sessionBean.getCompanyId());
			hm.put("company", sessionBean.getCompany());
			hm.put("UserIp", sessionBean.getUserIp());
			hm.put("UserName", sessionBean.getUserName());
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("SysDate",reportTime.getTime);
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("logoTik", "D:/Tomcat 7.0/webapps/report/posco/tick.png");
			hm.put("logoCross", "D:/Tomcat 7.0/webapps/report/posco/cross.png");
			

			hm.put("sql", queryAll);

			Window win = new ReportViewer(hm,"report/setup/rptUserAuthentication.jasper",
					this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
					this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
					this.getWindow().getApplication().getURL()+"VAADIN/applet",RadioBtn.Radio);

			win.setCaption("Project Report");
			this.getParent().getWindow().addWindow(win);
		}
		catch(Exception exp)
		{
			System.out.println(exp);
		}
	}

	private boolean queryValueCheck(String sql)
	{
		Transaction tx = null;

		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			Iterator iter = session.createSQLQuery(sql).list().iterator();

			if (iter.hasNext()) 
			{
				return true;
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		return false;
	}

	private AbsoluteLayout buildMainLayout() 
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("420px");
		setHeight("200px");

		lblGroup = new Label("User Name :");
		lblGroup.setImmediate(true);
		lblGroup.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblGroup, "top:20px;left:30px;");

		cmbGroup=new ComboBox();
		cmbGroup.setImmediate(true);
		cmbGroup.setWidth("200px");
		cmbGroup.setHeight("-1px");
		cmbGroup.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbGroup, "top:18px;left:150px;");

		//CategoryAll
		chkGroup = new CheckBox("All");
		chkGroup.setHeight("-1px");
		chkGroup.setWidth("-1px");
		chkGroup.setImmediate(true);
		mainLayout.addComponent(chkGroup, "top:20px; left:350.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:70.0px;left:145.0px;");		

		//CButton
		mainLayout.addComponent(button, "top:100.0px;left:130.0px;");

		return mainLayout;
	}
}
