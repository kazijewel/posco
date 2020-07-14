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
import com.common.share.ReportOption;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class RptEditDeleteReplacementLeave extends Window
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblUnit;
	private Label lblSection;

	private ComboBox cmbEmployeeName;
	private ComboBox cmbApplicationDate;

	ArrayList<Component> allComp = new ArrayList<Component>();
	
	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});
	
	TextField txtPath=new TextField();
	
	private CommonMethod cm;
	private String menuId = "";
	public RptEditDeleteReplacementLeave(SessionBean sessionBean,String menuId)
	{
		this.sessionBean=sessionBean;
		this.setCaption("EDIT DELETE REPLACEMENT LEAVE :: "+sessionBean.getCompany());
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		buildMainLayout();
		setContent(mainLayout);
		setEventAction();
		employeeDataLoad();
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
	
	public void employeeDataLoad()
	{
		cmbEmployeeName.removeAllItems();		
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vEmployeeId,vEmployeeCode,vEmployeeName from "
					+ "("
						+ "select distinct vEmployeeId,vEmployeeCode,vEmployeeName from tbReplacementLeaveApplication a "
						+ "union all select distinct vEmployeeId,vEmployeeCode,vEmployeeName from tbUdReplacementLeaveApplication b "
					+ ") temp order by vEmployeeCode";		
			System.out.println("employeeDataLoad :"+query);
			
			List <?> list=session.createSQLQuery(query).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbEmployeeName.addItem(element[0]);
				cmbEmployeeName.setItemCaption(element[0], (element[1]+"-"+element[2]));
			}
		}
		catch(Exception exp){
			showNotification("employeeDataLoad",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	public void cmbApplicationDateDataLoad()
	{
		cmbApplicationDate.removeAllItems();
		
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();

		try
		{
			String query="select distinct vTransactionId,dApplicationDate from "
			+ "("
				+ "select distinct a.vTransactionId,CONVERT(varchar,a.dApplicationDate,105) as dApplicationDate "
				+ "from tbReplacementLeaveApplication a "
				+ "where a.vEmployeeId like '"+(cmbEmployeeName.getValue()==null?"%":cmbEmployeeName.getValue())+"' "
				+ "union all "
				+ "select distinct b.vTransactionId,CONVERT(varchar,b.dApplicationDate,105) as dApplicationDate "
				+ "from tbUdReplacementLeaveApplication b "
				+ "where b.vEmployeeId like '"+(cmbEmployeeName.getValue()==null?"%":cmbEmployeeName.getValue())+"' "
			+ ") temp order by vTransactionId desc ";
			
			System.out.println("cmbApplicationDateDataLoad :"+query);			
			
			List <?> list=session.createSQLQuery(query).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbApplicationDate.addItem(element[0]);
				cmbApplicationDate.setItemCaption(element[0],element[1]+"");
			}
		}
		catch(Exception exp){
			showNotification("employeeSetData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void setEventAction()
	{
		cmbEmployeeName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{				
				if(cmbEmployeeName.getValue()!=null)
				{
					cmbApplicationDateDataLoad();
				}
			}
		});

		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbEmployeeName.getValue()!=null )
				{
					if(cmbApplicationDate.getValue()!=null)
					{
						reportShow();
					}
					else
					{
						showNotification("Select Time of Work",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					showNotification("Select Employee Name",Notification.TYPE_WARNING_MESSAGE);
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

	private void reportShow()
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());

		try
		{			

			HashMap <String,Object>  hm = new HashMap <String,Object> ();
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone",sessionBean.getCompanyContact());
			hm.put("userName", sessionBean.getUserName()+" "+sessionBean.getUserIp());
			hm.put("SysDate",new java.util.Date());
			hm.put("developer", sessionBean.getDeveloperAddress());
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("empList", "EDIT DELETE OVER TIME REQUEST REPORT");

			String query="select distinct dApplicationDate,vEmployeeCode,vEmployeeName,vDesignationName,vDepartmentName,dReplacementLeaveFrom,"
					+ "dReplacementLeaveTo,vPurposeOfLeave,vApprovedBy,vVisitingAddress,UDFlag,dEntryTime,vUserIp,vUserName,vMobileNo from "
					+ "("
					+ "select dApplicationDate,vEmployeeCode,vEmployeeName,vDesignationName,vDepartmentName,dReplacementLeaveFrom,"
					+ "dReplacementLeaveTo,vPurposeOfLeave,isnull(vApprovedBy,'')vApprovedBy,vVisitingAddress,'NEW' UDFlag,"
					+ "dEntryTime,vUserIp,vUserName,vMobileNo from tbReplacementLeaveApplication a "
					+ "where a.vTransactionId='"+cmbApplicationDate.getValue()+"' and a.vEmployeeId='"+cmbEmployeeName.getValue()+"' "
					+ "union all "
					+ "select dApplicationDate,vEmployeeCode,vEmployeeName,vDesignationName,vDepartmentName,dReplacementLeaveFrom,"
					+ "dReplacementLeaveTo,vPurposeOfLeave,isnull(vApprovedBy,'')vApprovedBy,vVisitingAddress,UDFlag,"
					+ "dEntryTime,vUserIp,vUserName,vMobileNo from tbUdReplacementLeaveApplication b "
					+ "where b.vTransactionId='"+cmbApplicationDate.getValue()+"' and b.vEmployeeId='"+cmbEmployeeName.getValue()+"' "
					+ ") temp order by dEntryTime desc ";

			System.out.println("report :"+query);
			if(queryValueCheck(query))
			{
				hm.put("sql", query);
				Window win = new ReportViewer(hm,"report/account/hrmModule/RptEditDeleteReplacementLeave.jasper",
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
		{showNotification("reportShow "+exp,Notification.TYPE_ERROR_MESSAGE);}
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

	private void focusMove()
	{
		allComp.add(cmbEmployeeName);
		allComp.add(cmbApplicationDate);
		allComp.add(cButton.btnPreview);
		new FocusMoveByEnter(this,allComp);
	}

	private AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);
		
		setWidth("470px");
		setHeight("200px");
		
		cmbEmployeeName = new ComboBox();
		cmbEmployeeName.setImmediate(false);
		cmbEmployeeName.setWidth("260px");
		cmbEmployeeName.setHeight("-1px");
		cmbEmployeeName.setNullSelectionAllowed(true);
		cmbEmployeeName.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Employee ID: "),"top:30px; left:30.0px;");
		mainLayout.addComponent(cmbEmployeeName, "top:28px; left:130.0px;");
		
		cmbApplicationDate = new ComboBox();
		cmbApplicationDate.setImmediate(false);
		cmbApplicationDate.setWidth("260px");
		cmbApplicationDate.setHeight("-1px");
		cmbApplicationDate.setNullSelectionAllowed(true);
		cmbApplicationDate.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("App Date: "),"top:60px; left:30.0px;");
		mainLayout.addComponent(cmbApplicationDate, "top:58px; left:130.0px;");
		
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:170.0px;left:130.0px;");
		RadioBtnGroup.setVisible(false);

		//mainLayout.addComponent(new Label("_______________________________________________________________________________"), "top:200.0px;left:20.0px;right:20.0px;");
		mainLayout.addComponent(cButton,"bottom:15px; left:140.0px");
		return mainLayout;
	}
}