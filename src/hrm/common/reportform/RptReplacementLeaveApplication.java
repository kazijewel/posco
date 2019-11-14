package hrm.common.reportform;

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
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class RptReplacementLeaveApplication extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblEmpName;
	private Label lblAppDate;

	private ComboBox cmbEmpName,cmbUnit;
	private ComboBox cmbAppDate;

	private CommonButton cButton = new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});
	private CommonMethod cm;
	private String menuId = "";
	public RptReplacementLeaveApplication(SessionBean sessionBean,String menuId) 
	{
		this.sessionBean=sessionBean;
		this.setCaption("REPLACEMENT LEAVE APPLICATION FORM :: "+sessionBean.getCompany());
		this.setWidth("550px");
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
	private void cmbUnitDataLoad() {
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "select distinct vUnitId,vUnitName from tbEmpOfficialPersonalInfo order by vUnitName";
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
		cmbUnit.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbUnit.getValue()!=null)
				{
					cmbAddEmployeeName();
				}
				else{
					cmbEmpName.setValue(null);
				}
			}
		});
		cmbEmpName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbAppDate.removeAllItems();
				if(cmbEmpName.getValue()!=null)
				{
					cmbAppDateAdd();
				}
			}
		});

		cButton.btnPreview.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbUnit.getValue()!=null)
				{
					if(cmbEmpName.getValue()!=null)
					{
						if(cmbAppDate.getValue()!=null)
						{
							reportpreview();
						}
						else
						{
							showNotification("Warning","Select Application Date",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else
					{
						showNotification("Warning","Select Employee Name",Notification.TYPE_WARNING_MESSAGE);
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
	}

	private AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("500px");
		setHeight("220px");

		cmbUnit=new ComboBox();
		cmbUnit.setWidth("280px");
		cmbUnit.setHeight("-1px");
		cmbUnit.setImmediate(true);
		mainLayout.addComponent(new Label("Project : "), "top:10.0px;left:30.0px;");
		mainLayout.addComponent(cmbUnit, "top:8.0px;left:135.0px");

		// lblEmpName
		lblEmpName = new Label("Employee Name :");
		lblEmpName.setImmediate(false);
		lblEmpName.setWidth("100.0%");
		lblEmpName.setHeight("-1px");
		mainLayout.addComponent(lblEmpName,"top:40.0px; left:30.0px;");

		// cmbEmpName
		cmbEmpName = new ComboBox();
		cmbEmpName.setImmediate(true);
		cmbEmpName.setWidth("280px");
		cmbEmpName.setHeight("-1px");
		cmbEmpName.setNullSelectionAllowed(true);
		cmbEmpName.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbEmpName, "top:38.0px; left:135.0px;");

		// lblAppDate
		lblAppDate = new Label("Application Date :");
		lblAppDate.setImmediate(false);
		lblAppDate.setWidth("100.0%");
		lblAppDate.setHeight("-1px");
		mainLayout.addComponent(lblAppDate,"top:70.0px; left:30.0px;");

		// cmbAppDate
		cmbAppDate = new ComboBox();
		cmbAppDate.setImmediate(true);
		cmbAppDate.setWidth("115px");
		cmbAppDate.setHeight("-1px");
		mainLayout.addComponent(cmbAppDate, "top:68.0px; left:135.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:110.0px;left:135.0px;");
		RadioBtnGroup.setVisible(false);
		mainLayout.addComponent(cButton,"top:140.opx; left:120.0px");
		return mainLayout;
	}

	private void cmbAddEmployeeName()
	{
		cmbEmpName.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = " Select distinct epo.vEmployeeId, epo.vEmployeeName,epo.vEmployeeCode from tbReplacementLeaveApplication eli inner join tbEmpOfficialPersonalInfo epo on epo.vEmployeeId=eli.vEmployeeId  "
					+ "  where epo.vUnitId='"+cmbUnit.getValue()+"' order by epo.vEmployeeName";
			
			System.out.println(sql);
			
			List <?> list = session.createSQLQuery(sql).list();	
			for (Iterator <?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element =  (Object[]) iter.next();	
				cmbEmpName.addItem(element[0]);
				cmbEmpName.setItemCaption(element[0],(String)element[2]+">>"+ element[1].toString());	
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbAddEmployeeName",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}	

	private void cmbAppDateAdd()
	{
	
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String str = " SELECT vTransactionId,CONVERT(varchar,eli.dApplicationDate,105) as appDate from tbReplacementLeaveApplication eli inner join tbEmpOfficialPersonalInfo epo on epo.vEmployeeId=eli.vEmployeeId " +
					" Where epo.vEmployeeId ='"+cmbEmpName.getValue()+"' and epo.vUnitId='"+cmbUnit.getValue().toString()+"' ";
			
			List <?> list = session.createSQLQuery(str).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbAppDate.addItem(element[0]);
				cmbAppDate.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbAppDateAdd",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void reportpreview()
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		try
		{
			HashMap <String,Object> hm = new HashMap <String,Object> ();
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("developer", sessionBean.getDeveloperAddress());
			hm.put("userName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			hm.put("path", "report/account/hrmModule/");
			hm.put("SysDate",reportTime.getTime);
			hm.put("logo", sessionBean.getCompanyLogo());
			

			String str1 = " select vEmployeeID,vEmployeeCode,vEmployeeName, "
					+ " (select vDepartmentName from tbEmpOfficialPersonalInfo where vEmployeeID=rla.vEmployeeID)vDepartmentName, "
					+ "(select vSectionName from tbEmpOfficialPersonalInfo where vEmployeeID=rla.vEmployeeID)vSectionName, "
					+ "(select vDesignationName from tbEmpOfficialPersonalInfo where vEmployeeID=rla.vEmployeeID)vDesignationName, "
					+ "(select vUnitName from tbEmpOfficialPersonalInfo where vEmployeeID=rla.vEmployeeID)vUnitName, "
					+ "iTotalDays,vMobileNo,vVisitingAddress,vPurposeOfLeave,dApplicationDate,dReplacementLeaveFrom,dReplacementLeaveTo  "
					+ "from tbReplacementLeaveApplication rla where vTransactionId like '"+(cmbAppDate.getValue()==null?"%":cmbAppDate.getValue())+"' ";
			
			if(queryValueCheck(str1))
			{
				hm.put("sql", str1);

				Window win = new ReportViewer(hm,"report/account/hrmModule/rptReplacementLeaveApplicationFormPOSCO.jasper",
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
			showNotification("reportpreview",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
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
}
