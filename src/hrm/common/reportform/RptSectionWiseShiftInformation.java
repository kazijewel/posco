package hrm.common.reportform;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.hibernate.Session;
import com.common.share.CommonButton;
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
public class RptSectionWiseShiftInformation extends Window 
{
	private ReportDate reportTime;
	private SessionBean sessionBean;
	public AbsoluteLayout mainLayout;
	private OptionGroup opgStatus = new OptionGroup();

	private ComboBox cmbDepartmentName;
	private CheckBox chkDepartmentName;
	private ComboBox cmbSectionName;
	private CheckBox ChkSectionName;
	private static final List<String>  aictiveType   = Arrays.asList(new String[]{"Active","Inactive","All"});

	ArrayList<Component> allComp = new ArrayList<Component>();

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");

	public RptSectionWiseShiftInformation(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setCaption("SECTION WISE SHIFT REPORT :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);
		cmbDepartmentData();
		setEventAction();
		focusMove();
	}


	public void setEventAction()
	{

		cmbDepartmentName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSectionName.removeAllItems();
				if(cmbDepartmentName.getValue()!=null)
				{
					cmbSectionData();
				}
			}
		});

		chkDepartmentName.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				cmbSectionName.removeAllItems();
				if(chkDepartmentName.booleanValue())
				{
					cmbDepartmentName.setValue(null);
					cmbDepartmentName.setEnabled(false);
					cmbSectionData();
				}
				else
				{
					cmbDepartmentName.setEnabled(true);
				}
			}
		});

		ChkSectionName.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(ChkSectionName.booleanValue())
				{
					cmbSectionName.setValue(null);
					cmbSectionName.setEnabled(false);
				}
				else
				{
					cmbSectionName.setEnabled(true);
				}
			}
		});


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
	}

	private void formValidation()
	{
		if(cmbDepartmentName.getValue()!=null || chkDepartmentName.booleanValue())
		{
			if(cmbSectionName.getValue()!=null || ChkSectionName.booleanValue())
			{			
				reportShow();
			}
			else
			{
				showNotification("Warning","Select Section Name!!!",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else
		{
			showNotification("Warning","Select Department Name!!!",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	public AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);

		setWidth("450px");
		setHeight("230px");

		cmbDepartmentName = new ComboBox();
		cmbDepartmentName.setImmediate(true);
		cmbDepartmentName.setWidth("250px");
		cmbDepartmentName.setHeight("-1px");
		mainLayout.addComponent(new Label("Unit Name : "), "top:40.0px; left:30.0px;");
		mainLayout.addComponent(cmbDepartmentName, "top:38.0px; left:140.0px;");

		chkDepartmentName = new CheckBox("All");
		chkDepartmentName.setImmediate(true);
		chkDepartmentName.setWidth("-1px");
		chkDepartmentName.setHeight("-1px");
		mainLayout.addComponent(chkDepartmentName,"top:40px; left:395px;");

		cmbSectionName = new ComboBox();
		cmbSectionName.setImmediate(true);
		cmbSectionName.setWidth("250px");
		cmbSectionName.setHeight("-1px");
		mainLayout.addComponent(new Label("Division Name : "), "top:70.0px; left:30.0px;");
		mainLayout.addComponent(cmbSectionName, "top:68.0px; left:140.0px;");

		ChkSectionName = new CheckBox("All");
		ChkSectionName.setImmediate(true);
		ChkSectionName.setWidth("-1px");
		ChkSectionName.setHeight("-1px");
		mainLayout.addComponent(ChkSectionName,"top:70px; left:395px;");

		opgStatus = new OptionGroup("",aictiveType);
		opgStatus.setImmediate(true);
		opgStatus.setWidth("250px");
		opgStatus.setHeight("-1px");
		opgStatus.setStyleName("horizontal");
		opgStatus.setValue("Active");
		mainLayout.addComponent(new Label("Status Type"), "top:100.0px; left:30.0px;");
		mainLayout.addComponent(opgStatus, "top:98.0px; left:140.0px;");

		mainLayout.addComponent(cButton, "top:150.0px;left:140.0px;");

		return mainLayout;
	}


	private void cmbDepartmentData()
	{
		cmbDepartmentName.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql="select distinct vUnitID,vUnitName from tbUnit_Section_Wise_ShiftInfo order by vUnitName";

			List <?> lst=session.createSQLQuery(sql).list();

			if(!lst.isEmpty())
			{
				Iterator <?> itr=lst.iterator();
				while(itr.hasNext())
				{
					Object[] element=(Object[])itr.next();
					cmbDepartmentName.addItem(element[0]);
					cmbDepartmentName.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbDepartmentData", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void cmbSectionData()
	{
		cmbSectionName.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		
		try
		{
			String sql="select distinct vSectionId,vSectionName from tbUnit_Section_Wise_ShiftInfo where "
					+ "vUnitID like '"+(cmbDepartmentName.getValue()!=null?cmbDepartmentName.getValue():"%")+"' order by vSectionName";

			List <?> lst=session.createSQLQuery(sql).list();

			if(!lst.isEmpty())
			{
				Iterator <?> itr=lst.iterator();
				while(itr.hasNext())
				{
					Object[] element=(Object[])itr.next();
					cmbSectionName.addItem(element[0]);
					cmbSectionName.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbSectionData", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void focusMove()
	{
		allComp.add(cmbSectionName);
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
		try
		{
			String IsActiveValue = "1";
			if(opgStatus.getValue().toString().equals("Inactive"))
			{IsActiveValue = "0";}

			else if(opgStatus.getValue().toString().equals("All"))
			{IsActiveValue = "%";}
			
			String query = "select * from tbUnit_Section_Wise_ShiftInfo where isActive like '"+IsActiveValue+"' and " +
					"vUnitID like '"+(cmbDepartmentName.getValue()!=null?cmbDepartmentName.getValue():"%")+"' " +
					"and vSectionId like '"+(cmbSectionName.getValue()!=null?cmbSectionName.getValue():"%")+"' order by " +
					"vUnitName,vSectionName";

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

				Window win = new ReportViewer(hm,"report/account/hrmModule/rptSectionWiseShiftInfo.jasper",
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

