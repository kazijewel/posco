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
import com.common.share.ReportOption;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;


@SuppressWarnings("serial")
public class RptEditEmployeeInformation extends Window {

	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblEmployee;
	
	private ComboBox cmbEmployee;

	private PopupDateField dSalaryMonth;

	private OptionGroup RadioBtnStatus;
	private static final List<String> status=Arrays.asList(new String[]{"Active","Left","All"});
	ArrayList<Component> allComp = new ArrayList<Component>();
	private OptionGroup opgEmployee;
	private List<?> lstEmployee = Arrays.asList(new String[]{"Employee ID","Proximity ID","Employee Name"});

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();
	private CommonMethod cm;
	private String menuId = "";
	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});

	public RptEditEmployeeInformation(SessionBean sessionBean,String menuId)
	{
		this.sessionBean=sessionBean;
		this.setCaption("EDIT EMPLOYEE INFORMATION :: "+sessionBean.getCompany());
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		buildMainLayout();
		setContent(mainLayout);
		EmployeeDataAdd();
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

	private void EmployeeDataAdd()
	{
		String status="%";
		if(RadioBtnStatus.getValue().equals("Active"))
		{
			status="1";
		}
		else if(RadioBtnStatus.getValue().equals("Left"))
		{
			status="0";
		}
		else
		{
			status="%";
		}
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select vEmployeeId,vEmployeeCode,vEmployeeName from tbEmpOfficialPersonalInfo where bStatus like '"+status+"' " +
					"order by vEmployeeCode";
			
			System.out.println("EmployeeDataAdd: "+query);
			
			List <?> lst=session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				Iterator <?> itr=lst.iterator();
				while(itr.hasNext())
				{
					Object [] element=(Object[])itr.next();
					cmbEmployee.addItem(element[0]);
					cmbEmployee.setItemCaption(element[0], element[1]+">>"+element[2]);
				}
			}
			else
				showNotification("Warning","No Employee Found!!!",Notification.TYPE_WARNING_MESSAGE);
		}
		catch (Exception exp)
		{
			showNotification("EmployeeDataAdd",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void setEventAction()
	{
		RadioBtnStatus.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmployee.removeAllItems();
				EmployeeDataAdd();
			}
		});

		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbEmployee.getValue()!=null)
				{
					reportShow();
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
		ReportDate reportTime = new ReportDate();

		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		
		try
		{
			String query=" select vEmployeeID,vEmployeeCode,vProximityId,vEmployeeName,vUnitId,vUnitName,vDepartmentId,vDepartmentName,"+
			" vSectionId,vSectionName,vDesignationId,vDesignationName,vGender,vEmployeeType,vEmployeeStatus,bStatus,iOtEnable,"+
			" (select mBasic from tbEmpSalaryStructure where vEmployeeId=ein.vEmployeeId)mBasic,"+
			" (select mHouseRent from tbEmpSalaryStructure where vEmployeeId=ein.vEmployeeId)mHouseRent,"+
			" (select mMedicalAllowance from tbEmpSalaryStructure where vEmployeeId=ein.vEmployeeId)mMedicalAllowance,"+
			" (select mConveyanceAllowance from tbEmpSalaryStructure where vEmployeeId=ein.vEmployeeId)mConveyanceAllowance,"+
			" (select mOtherAllowance from tbEmpSalaryStructure where vEmployeeId=ein.vEmployeeId)mOtherAllowance,"+
			" (select mSpecialAllowance from tbEmpSalaryStructure where vEmployeeId=ein.vEmployeeId)mSpecialAllowance,"+
			" (select mIncomeTax from tbEmpSalaryStructure where vEmployeeId=ein.vEmployeeId)mIncomeTax,"+
			" (select mProvidentFund from tbEmpSalaryStructure where vEmployeeId=ein.vEmployeeId)mProvidentFund,"+
			" vBankId,vBankName,vBranchId,vBranchName,vAccountNo,FridayStatus,'Present' as vUDFlag,vUserName,dEntryTime,vUserIp "+
			" from tbEmpOfficialPersonalInfo ein where vEmployeeId = '"+cmbEmployee.getValue()+"' "+
			" union all select vEmployeeID,vEmployeeCode,vProximityId,vEmployeeName,"+
			" (select vUnitId from tbEmpOfficialPersonalInfo  where vEmployeeId=uein.vEmployeeId)vUnitId,"+
			" (select vUnitName from tbEmpOfficialPersonalInfo  where vEmployeeId=uein.vEmployeeId)vUnitName,"+
			" (select vDepartmentId from tbEmpOfficialPersonalInfo  where vEmployeeId=uein.vEmployeeId)vDepartmentId,"+
			" (select vDepartmentName from tbEmpOfficialPersonalInfo  where vEmployeeId=uein.vEmployeeId)vDepartmentName,"+
			" (select vSectionId from tbEmpOfficialPersonalInfo  where vEmployeeId=uein.vEmployeeId)vSectionId,"+
			" (select vSectionName from tbEmpOfficialPersonalInfo  where vEmployeeId=uein.vEmployeeId)vSectionName,"+
			" (select vDesignationId from tbEmpOfficialPersonalInfo  where vEmployeeId=uein.vEmployeeId)vDesignationId,"+
			" (select vDesignationName from tbEmpOfficialPersonalInfo  where vEmployeeId=uein.vEmployeeId)vDesignationName,"+
			" vGender,vEmployeeType,vEmployeeStatus,bStatus,iOtEnable,mBasic,mHouseRent,mMedicalAllowance,mConveyanceAllowance,mOtherAllowance,"+
			" mSpecialAllowance,mIncomeTax,mProvidentFund,vBankId,vBankName,vBranchId,vBranchName,vAccountNo,"+
			" iFridayStatus as FridayStatus,'Old' vUDFlag,vUserName,dEntryTime,vUserIp from  tbUdEmployeeInformation uein "+
			" where vEmployeeId = '"+cmbEmployee.getValue()+"' order by  vEmployeeName,vEmployeeID,vUDFlag desc,dEntryTime desc";

			
			System.out.println("reportShow: "+query);
			
			if(queryValueCheck(query))
			{				
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				

				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("developer",sessionBean.getDeveloperAddress());
				hm.put("userName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
				
				//hm.put("Department",firstTab.cmbDepartment.getItemCaption(firstTab.cmbDepartment.getValue()));
				hm.put("SysDate",reportTime.getTime);
				hm.put("sql", query);
				
				Window win = new ReportViewer(hm,"report/account/hrmModule/rptEditEmployeeInformation.jasper",
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
			showNotification("reportShow "+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private boolean queryValueCheck(String sql)
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try 
		{
			List <?> lst = session.createSQLQuery(sql).list();
			if (!lst.isEmpty()) 
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
		allComp.add(dSalaryMonth);
		allComp.add(cButton.btnPreview);

		new FocusMoveByEnter(this,allComp);
	}

	private AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("450px");
		setHeight("260px");
		
		RadioBtnStatus = new OptionGroup("",status);
		RadioBtnStatus.setImmediate(true);
		RadioBtnStatus.setStyleName("horizontal");
		RadioBtnStatus.setValue("Active");
		mainLayout.addComponent(RadioBtnStatus, "top:20.0px;left:150.0px;");
		
		opgEmployee=new OptionGroup("",lstEmployee);
		opgEmployee.select("Employee ID");
		opgEmployee.setImmediate(true);
		opgEmployee.setStyleName("horizontal");
		mainLayout.addComponent(opgEmployee, "top:50.0px; left:50.0px;");
		opgEmployee.setVisible(false);

		//lblEmpType
		lblEmployee=new Label("Employee : ");
		mainLayout.addComponent(lblEmployee, "top:80.0px;left:20.0px;");

		//cmbEmpType
		cmbEmployee=new ComboBox();
		cmbEmployee.setImmediate(true);
		cmbEmployee.setWidth("260px");
		cmbEmployee.setHeight("-1px");
		cmbEmployee.setNullSelectionAllowed(true);
		cmbEmployee.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbEmployee, "top:78.0px; left:130.0px;");

		
		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:110.0px;left:130.0px;");
		RadioBtnGroup.setVisible(false);

		//mainLayout.addComponent(new Label("_________________________________________________________________________________________"), "top:130.0px;right:20.0px;left:20.0px;");		
		mainLayout.addComponent(cButton,"top:160.opx; left:175.0px");
		return mainLayout;
	}
}