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
public class RptEditDeleteOverTimeRequest extends Window
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblUnit;
	private Label lblSection;

	private ComboBox cmbEmployeeName;
	private ComboBox cmbTimeOfWork;

	ArrayList<Component> allComp = new ArrayList<Component>();
	
	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});
	
	TextField txtPath=new TextField();
	
	private CommonMethod cm;
	private String menuId = "";
	public RptEditDeleteOverTimeRequest(SessionBean sessionBean,String menuId)
	{
		this.sessionBean=sessionBean;
		this.setCaption("EDIT DELETE OVER TIME REQUEST :: "+sessionBean.getCompany());
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
				+ "	select distinct vEmployeeId,(select distinct vEmployeeCode from tbEmpOfficialPersonalInfo where vEmployeeId=a.vEmployeeId)vEmployeeCode, "
				+ "vEmployeeName from tbOTRequest a "
				+ "union all "
				+ "select distinct vEmployeeId,(select distinct vEmployeeCode from tbEmpOfficialPersonalInfo where vEmployeeId=b.vEmployeeId)vEmployeeCode, "
				+ "vEmployeeName from tbUdOTRequest b "
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
	public void cmbTimeOfWorkData()
	{
		cmbTimeOfWork.removeAllItems();
		
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();

		try
		{
			String query="select distinct vTransactionId,timeWork,dTimeFrom from "
			+ "("
				+ "select distinct a.vTransactionId,RIGHT(CONVERT(VARCHAR, dTimeFrom, 100), 7)+' - '+RIGHT(CONVERT(VARCHAR, dTimeTo, 100), 7) timeWork,"
				+ "dTimeFrom from tbOTRequest a where a.vEmployeeId like '"+(cmbEmployeeName.getValue()==null?"%":cmbEmployeeName.getValue())+"' "
				+ "union all "
				+ "select distinct b.vTransactionId,RIGHT(CONVERT(VARCHAR, dTimeFrom, 100), 7)+' - '+RIGHT(CONVERT(VARCHAR, dTimeTo, 100), 7) timeWork,"
				+ "dTimeFrom from tbUdOTRequest b where b.vEmployeeId like '"+(cmbEmployeeName.getValue()==null?"%":cmbEmployeeName.getValue())+"' "
			+ ") temp order by dTimeFrom desc ";
			
			System.out.println("cmbTimeOfWorkData :"+query);			
			
			List <?> list=session.createSQLQuery(query).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbTimeOfWork.addItem(element[0]);
				cmbTimeOfWork.setItemCaption(element[0], sessionBean.dfBd.format(element[2])+"# "+element[1]);
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
					cmbTimeOfWorkData();
				}
			}
		});

		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbEmployeeName.getValue()!=null )
				{
					if(cmbTimeOfWork.getValue()!=null)
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

			String query="select vEmployeeCode,vEmployeeName,vDesignationName,vDepartmentName,dRequestDate,vJobSite,vManger,dDateFrom,dTimeFrom,"
					+ "dDateTo,dTimeTo,mTotalTimeHR,vWorkRequest,iFinal,vShift,vApprovedBy,vUdFlag,dEntryTime,vUserId,vUserIp,vUserName from "
					+ "("
						+ "select (select distinct vEmployeeCode from tbEmpOfficialPersonalInfo where vEmployeeId=a.vEmployeeId)vEmployeeCode,"
						+ "vEmployeeName,vDesignationName,vDepartmentName,dRequestDate,vJobSite,vManger,dDateFrom,dTimeFrom,dDateTo,dTimeTo,"
						+ "case when iHoliday=1 and mTotalTimeHR>=10  then mTotalTimeHR-1 else mTotalTimeHR end mTotalTimeHR,vWorkRequest,"
						+ "case when iFinal=1 then 'Yes' else 'No' end iFinal,"
						+ "case when iHoliday=1 then 'Holiday' else 'Friday' end vShift,isnull(vApprovedBy,'')vApprovedBy,'NEW' vUdFlag,"
						+ "dEntryTime,vUserId,vUserIp,vUserName from tbOTRequest a "
						+ "where vTransactionId='"+cmbTimeOfWork.getValue()+"' and vEmployeeId='"+cmbEmployeeName.getValue()+"' "
						+ "union all "
						+ "select (select distinct vEmployeeCode from tbEmpOfficialPersonalInfo where vEmployeeId=b.vEmployeeId)vEmployeeCode,"
						+ "vEmployeeName,vDesignationName,vDepartmentName,dRequestDate,vJobSite,vManger,dDateFrom,dTimeFrom,dDateTo,dTimeTo,"
						+ "case when iHoliday=1 and mTotalTimeHR>=10 then mTotalTimeHR-1 else mTotalTimeHR end mTotalTimeHR,vWorkRequest,"
						+ "case when iFinal=1 then 'Yes' else 'No' end iFinal,"
						+ "case when iHoliday=1 then 'Holiday' else 'Friday' end vShift,isnull(vApprovedBy,''),vUdFlag,"
						+ "dEntryTime,vUserId,vUserIp,vUserName from tbUDOTRequest b "
						+ "where vTransactionId='"+cmbTimeOfWork.getValue()+"' and vEmployeeId='"+cmbEmployeeName.getValue()+"' "
					+ ") temp order by dEntryTime desc";

			System.out.println("report :"+query);
			if(queryValueCheck(query))
			{
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/account/hrmModule/RptEditDeleteOverTimeRequest.jasper",
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
		allComp.add(cmbTimeOfWork);
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
		
		cmbTimeOfWork = new ComboBox();
		cmbTimeOfWork.setImmediate(false);
		cmbTimeOfWork.setWidth("260px");
		cmbTimeOfWork.setHeight("-1px");
		cmbTimeOfWork.setNullSelectionAllowed(true);
		cmbTimeOfWork.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Time of Work: "),"top:60px; left:30.0px;");
		mainLayout.addComponent(cmbTimeOfWork, "top:58px; left:130.0px;");
		
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