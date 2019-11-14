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
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class RptDesignationList extends Window
{
	private SessionBean sessionBean;
	public AbsoluteLayout mainLayout;
	private CommonMethod cm;
	private String menuId = "";
	private ComboBox cmbDesignation;

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1 = Arrays.asList(new String[]{"PDF","Other"});

	private ReportDate reportTime = new ReportDate();

	CommonButton cButton = new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	
	public RptDesignationList(SessionBean sessionBean,String menuId)
	{
		this.sessionBean=sessionBean;
		this.setCaption("Designation List :: "+sessionBean.getCompany());
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		buildMainLayout();
		setContent(mainLayout);

		addDesignation();
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

	private void addDesignation()
	{
		cmbDesignation.addItem("All Designation");
		cmbDesignation.setValue("All Designation");
		cmbDesignation.setEnabled(false);
	}

	public void setEventAction()
	{
		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbDesignation.getValue()!=null)
				{
					String sectionId = "";
					sectionId = cmbDesignation.getValue().toString();
					reportShow(sectionId);
				}
				else
				{
					showNotification("Select Section Name",Notification.TYPE_WARNING_MESSAGE);
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
	}

	private void reportShow(Object sectionId)
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		try
		{
			HashMap<Object, Object> hm = new HashMap<Object, Object>();
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			hm.put("SysDate",reportTime.getTime);
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("developer", "Developed by: E-Vision Software Ltd. ||  Mob:01755-506044 || www.eslctg.com");

			String query = " select * from tbDesignationInfo order by iRank";

			if(queryValueCheck(query))
			{
				hm.put("sql", query);
				Window win = new ReportViewer(hm,"report/account/hrmModule/RptDesignationList.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",true);
				win.setStyleName("cwindow");
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
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try 
		{
			Iterator<?> iter = session.createSQLQuery(sql).list().iterator();
			if(iter.hasNext())
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

	public AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);

		setWidth("400px");
		setHeight("180px");

		cmbDesignation = new ComboBox();
		cmbDesignation.setImmediate(true);
		cmbDesignation.setWidth("200px");
		cmbDesignation.setHeight("-1px");
		mainLayout.addComponent(new Label("Designation"), "top:30px; left:50px;");
		mainLayout.addComponent(cmbDesignation, "top:28.0px; left:140.0px;");


		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:60.0px;left:140.0px;");
		RadioBtnGroup.setVisible(false);

		mainLayout.addComponent(cButton, "top:90.0px;left:120.0px;");

		return mainLayout;
	}
}
