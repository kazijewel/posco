package hrm.common.reportform;

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
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class RptShiftInformation extends Window 
{
	private ReportDate reportTime;
	private SessionBean sessionBean;
	public AbsoluteLayout mainLayout;
	private OptionGroup opgStatus = new OptionGroup();


	private ComboBox cmbShiftName;
	private CheckBox ChkShiftName;
	private static final List<String>  aictiveType   = Arrays.asList(new String[]{"Active","Inactive","All"});

	ArrayList<Component> allComp = new ArrayList<Component>();

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private CommonMethod cm;
	private String menuId = "";
	public RptShiftInformation(SessionBean sessionBean,String menuId)
	{
		this.sessionBean=sessionBean;
		this.setCaption("SHIFT INFORMATION REPORT :: "+sessionBean.getCompany());
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		buildMainLayout();
		setContent(mainLayout);
		cmbShiftDataAdd();
		setEventAction();
		focusMove();
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

	public void setEventAction()
	{
		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				formValidation();
			}
		});

		cButton.btnExit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

		opgStatus.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbShiftDataAdd();
			}
		});
		
		ChkShiftName.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event)
			{
				if(ChkShiftName.booleanValue())
				{
					cmbShiftName.setValue(null);
					cmbShiftName.setEnabled(false);
				}
				else
				{
					cmbShiftName.setEnabled(true);
				}

			}
		});
	}

	private void formValidation()
	{
		if(cmbShiftName.getValue()!=null || ChkShiftName.booleanValue())
		{			
			reportShow();
		}
		else
		{
			showNotification("Warning","Select Shift Name",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	public AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);

		setWidth("450px");
		setHeight("230px");

		opgStatus = new OptionGroup("",aictiveType);
		opgStatus.setImmediate(true);
		opgStatus.setWidth("250px");
		opgStatus.setHeight("-1px");
		opgStatus.setStyleName("horizontal");
		opgStatus.setValue("Active");
		mainLayout.addComponent(new Label("Status Type : "), "top:50.0px; left:30.0px;");
		mainLayout.addComponent(opgStatus, "top:50.0px; left:120.0px;");

		cmbShiftName = new ComboBox();
		cmbShiftName.setImmediate(true);
		cmbShiftName.setWidth("250px");
		cmbShiftName.setHeight("-1px");
		mainLayout.addComponent(new Label("Shift Name : "), "top:80.0px; left:30.0px;");
		mainLayout.addComponent(cmbShiftName, "top:78.0px; left:120.0px;");

		ChkShiftName = new CheckBox("All");
		ChkShiftName.setImmediate(true);
		ChkShiftName.setWidth("-1px");
		ChkShiftName.setHeight("-1px");
		mainLayout.addComponent(ChkShiftName,"top:80px; left:375px;");

		mainLayout.addComponent(cButton, "top:150.0px;left:140.0px;");

		return mainLayout;
	}

	private void cmbShiftDataAdd()
	{
		cmbShiftName.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String status = "1";
			if(opgStatus.getValue()=="Inactive")
			{
				status = "0";
			}
			else if(opgStatus.getValue()=="All")
			{
				status = "%";
			}
			String sql="select distinct vShiftId,vShiftName from tbShiftInfo where isActive like '"+status+"'";

			List <?> lst=session.createSQLQuery(sql).list();

			if(!lst.isEmpty())
			{
				Iterator <?> itr=lst.iterator();
				while(itr.hasNext())
				{
					Object[] element=(Object[])itr.next();
					cmbShiftName.addItem(element[0]);
					cmbShiftName.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbShiftDataAdd", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void focusMove()
	{
		allComp.add(cmbShiftName);
		allComp.add(cButton.btnPreview);
		new FocusMoveByEnter(this,allComp);
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

	private void reportShow()
	{
		reportTime = new ReportDate();
		String IsActiveValue = "";
		String ShiftValue="";
		
		try
		{	
			if(cmbShiftName.getValue()!=null)
			{
				ShiftValue=cmbShiftName.getValue().toString();			
			}	
			else
			{
				ShiftValue="%";
			}

			if(opgStatus.getValue().toString().equals("Active")){
				IsActiveValue = "1";}

			else if(opgStatus.getValue().toString().equals("Inactive")){
				IsActiveValue = "0";}

			else if(opgStatus.getValue().toString().equals("All")){
				IsActiveValue = "%";}
			
			String query = "select vShiftName,dShiftStart,dShiftEnd,dLateInLimit,dEarlyOutLimit,isActive,vUserName," +
					"vUserIp,dEntryTime from tbShiftInfo where isActive like '"+IsActiveValue+"' and vShiftId like '"+ShiftValue+"' order by vShiftName";
			
			System.out.println("Shehab"+query);

			if(queryValueCheck(query))
			{
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				hm.put("company",sessionBean.getCompany());
				hm.put("address",sessionBean.getCompanyAddress());
				hm.put("phone",sessionBean.getCompanyContact());
				hm.put("username",sessionBean.getUserName()+"  "+sessionBean.getUserIp());
				hm.put("SysDate",reportTime.getTime);
				hm.put("developer", "Developed by: E-Vision Software Ltd. || Mob:01755-506044 || www.eslctg.com");
				hm.put("logo",sessionBean.getCompanyLogo());
				//hm.put("devloperLogo", "hrm/jasper/attendance/esl.png");
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/account/hrmModule/rptShiftInfo.jasper",
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
		catch(Exception exp){this.getParent().showNotification("Error "+exp,Notification.TYPE_ERROR_MESSAGE);}
	}	
}

