package hrm.common.reportform;

import java.text.SimpleDateFormat;
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
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class RptLoanRegister extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblSectionName;
	private ComboBox cmbSectionName,cmbUnit,cmbDepartment;
	private CheckBox chkSectionAll,chkDepartmentAll;

	private Label lblAsOnDate;
	private PopupDateField dAsOnDate;

	private CommonButton cButton = new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");

	private Label lblLoanStatus;
	OptionGroup radioLoanStatus;
	private static final List<String>loanStatus  = Arrays.asList(new String[] {"Active", "All" });

	private SimpleDateFormat dDbFormat = new SimpleDateFormat("yyyy-MM-dd");
	private ReportDate reportTime = new ReportDate();

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});

	TextField txtPath=new TextField();
	TextField txtAddress=new TextField();
	private CommonMethod cm;
	private String menuId = "";
	public RptLoanRegister(SessionBean sessionBean,String menuId) 
	{
		this.sessionBean=sessionBean;
		this.setCaption("LOAN SUMMARY :: "+sessionBean.getCompany());
		this.setWidth("500px");
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		buildMainLayout();
		setContent(mainLayout);

		cmbUnitDataLoad();
		//cmbAddSectionName();
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
			String sql = "select vUnitId,vUnitName from tbEmpOfficialPersonalInfo";
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
				cmbDepartment.removeAllItems();
				if(cmbUnit.getValue()!=null)
				{
					cmbAddDepartmentName();
				}
			}
		});
		cmbDepartment.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
				if(cmbDepartment.getValue()!=null)
				{
					cmbAddSectionName();
				}
				
			}
		});
		chkDepartmentAll.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
				if(cmbUnit.getValue()!=null)
				{
					if(chkDepartmentAll.booleanValue())
					{
						cmbDepartment.setValue(null);
						cmbDepartment.setEnabled(false);
						cmbAddSectionName();
					}
					else
					{
						cmbDepartment.setEnabled(true);
					}
				}
			}
		});
		cButton.btnPreview.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbUnit.getValue()!=null)
				{
					if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue()==true)
					{
						if(cmbSectionName.getValue()!=null || chkSectionAll.booleanValue()==true)
						{
							if(dAsOnDate.getValue()!=null)
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
							showNotification("Warning","Select Section Name",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else
					{
						showNotification("Warning","Select Department Name",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					showNotification("Warning","Select Project Name",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		chkSectionAll.addListener(new Listener()
		{
			public void componentEvent(Event event)
			{
				if(chkSectionAll.booleanValue()==true)
				{
					cmbSectionName.setEnabled(false);
					cmbSectionName.setValue(null);
				}
				else
				{
					cmbSectionName.setEnabled(true);
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
		setWidth("450px");
		setHeight("280px");
		
		cmbUnit=new ComboBox();
		cmbUnit.setWidth("260px");
		cmbUnit.setHeight("-1px");
		cmbUnit.setImmediate(true);
		mainLayout.addComponent(new Label("Project : "), "top:10.0px;left:30.0px;");
		mainLayout.addComponent(cmbUnit, "top:8.0px;left:135.0px");
		
		mainLayout.addComponent(new Label("Department :"),"top:40.0px; left:30.0px;");

		// cmbSectionName
		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("260px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbDepartment, "top:38.0px; left:135.0px;");
		
		// chkSectionAll
		chkDepartmentAll = new CheckBox("All");
		chkDepartmentAll.setImmediate(true);
		chkDepartmentAll.setHeight("-1px");
		chkDepartmentAll.setWidth("-1px");
		mainLayout.addComponent(chkDepartmentAll, "top:40.0px; left:400.0px;");
		
		// lblSectionName
		lblSectionName = new Label("Section :");
		lblSectionName.setImmediate(false);
		lblSectionName.setWidth("100.0%");
		lblSectionName.setHeight("-1px");
		mainLayout.addComponent(lblSectionName,"top:70px; left:30.0px;");

		// cmbSectionName
		cmbSectionName = new ComboBox();
		cmbSectionName.setImmediate(true);
		cmbSectionName.setWidth("260px");
		cmbSectionName.setHeight("-1px");
		cmbSectionName.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbSectionName, "top:68px; left:135.0px;");

		// chkSectionAll
		chkSectionAll = new CheckBox("All");
		chkSectionAll.setImmediate(true);
		chkSectionAll.setHeight("-1px");
		chkSectionAll.setWidth("-1px");
		mainLayout.addComponent(chkSectionAll, "top:70px; left:400.0px;");

		// lblLoanStatus
		lblLoanStatus = new Label("Loan Status :");
		lblLoanStatus.setImmediate(false);
		lblLoanStatus.setWidth("100.0%");
		lblLoanStatus.setHeight("-1px");
		mainLayout.addComponent(lblLoanStatus,"top:100px; left:30.0px;");

		radioLoanStatus= new OptionGroup("",loanStatus);
		radioLoanStatus.setImmediate(true);
		radioLoanStatus.setWidth("-1px");
		radioLoanStatus.setHeight("-1px");
		radioLoanStatus.setStyleName("horizontal");
		radioLoanStatus.setImmediate(true);
		radioLoanStatus.select("Active");
		mainLayout.addComponent(radioLoanStatus, "top:100px; left:135.0px;");

		// lblAsOnDate
		lblAsOnDate = new Label("As on Date :");
		lblAsOnDate.setImmediate(false);
		lblAsOnDate.setWidth("100.0%");
		lblAsOnDate.setHeight("-1px");
		mainLayout.addComponent(lblAsOnDate,"top:130px; left:30.0px;");

		// dAsOnDate
		dAsOnDate = new PopupDateField();
		dAsOnDate.setImmediate(true);
		dAsOnDate.setWidth("110px");
		dAsOnDate.setHeight("-1px");
		dAsOnDate.setDateFormat("dd-MM-yyyy");
		dAsOnDate.setValue(new java.util.Date());
		dAsOnDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dAsOnDate, "top:128px; left:135.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:130.0px;left:135.0px;");
		RadioBtnGroup.setVisible(false);

		//mainLayout.addComponent(new Label("___________________________________________________________________________"), "top:140.0px;left:20.0px;right:20.0px;");
		mainLayout.addComponent(cButton,"bottom:10px; left:130.0px");

		return mainLayout;
	}

	private void cmbAddDepartmentName()
	{
		cmbDepartment.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = " select distinct epo.vDepartmentId,epo.vDepartmentName from tbLoanApplication la inner join tbEmpOfficialPersonalInfo epo on epo.vEmployeeId=la.vAutoEmployeeId where epo.vUnitId='"+cmbUnit.getValue()+"' order by epo.vDepartmentName";
			System.out.println(sql);
			List <?> list = session.createSQLQuery(sql).list();	
			for (Iterator <?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element =  (Object[]) iter.next();	

				cmbDepartment.addItem(element[0]);
				cmbDepartment.setItemCaption(element[0], element[1].toString());	
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}	

	private void cmbAddSectionName()
	{
		cmbSectionName.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = " select distinct epo.vSectionid,epo.vSectionname from tbLoanApplication la inner join tbEmpOfficialPersonalInfo epo on epo.vEmployeeId=la.vAutoEmployeeId where epo.vUnitId='"+cmbUnit.getValue()+"' and epo.vDepartmentId like '"+(chkDepartmentAll.booleanValue()==true?"%":cmbDepartment.getValue()==null?"%":cmbDepartment.getValue())+"' order by epo.vSectionName";
			List <?> list = session.createSQLQuery(sql).list();	
			for (Iterator <?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element =  (Object[]) iter.next();	

				cmbSectionName.addItem(element[0]);
				cmbSectionName.setItemCaption(element[0], element[1].toString());	
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}	

	private void reportpreview()
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		String query = "";
		String section = "",deptId="%";

		if(chkSectionAll.booleanValue()==true)
		{
			section= "%";
		}
		else
		{
			section= cmbSectionName.getValue().toString();
		}
		if(cmbDepartment.getValue()!=null)
		{
			deptId=cmbDepartment.getValue().toString();
		}
		try
		{
			HashMap <String,Object> hm = new HashMap <String,Object> ();
			hm.put("company", sessionBean.getCompany());
			hm.put("address", txtAddress.getValue().toString());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("asOnDate", dAsOnDate.getValue());
			hm.put("userName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			hm.put("SysDate",reportTime.getTime);
			hm.put("logo", sessionBean.getCompanyLogo());

			if(radioLoanStatus.getValue().toString().equals("Active"))
			{
				query = " select ei.vEmployeeId,ei.vEmployeeCode,ei.vEmployeeName,si.vSectionName,di.vDesignation,ei.dJoiningDate," +
						" la.dApplicationDate,la.mGrossAmount,la.mLoanBalance as Balance,la.vUnitId,la.vUnitName," +
						" (select iRank from tbDesignationInfo where vDesignationId=di.vDesignationId)iRank,ei.vDepartmentId,ei.vDepartmentName " +
						" from tbLoanApplication as la" +
						" inner join tbEmpOfficialPersonalInfo as ei on la.vAutoEmployeeId=ei.vEmployeeId inner join tbEmpSectionInfo as si" +
						" on ei.vEmployeeId=si.vEmployeeId inner join tbEmpDesignationInfo as di on ei.vEmployeeId=di.vEmployeeId" +
						" where la.dApplicationDate<='"+dDbFormat.format(dAsOnDate.getValue())+"' " +
						" and ei.vSectionId like '"+section+"' and ei.vDepartmentId like '"+deptId+"' and la.iSanctionStatus='1' and ei.vUnitid='"+cmbUnit.getValue()+"' and la.mLoanBalance>0 " +
						" order by ei.vUnitName,ei.vDepartmentName,ei.vSectionName,iRank,dJoiningDate";
			}
			else
			{
				query = " select ei.vEmployeeId,ei.vEmployeeCode,ei.vEmployeeName,si.vSectionName,di.vDesignation,ei.dJoiningDate," +
						" la.dApplicationDate,la.mGrossAmount,la.mLoanBalance as Balance,la.vUnitId,la.vUnitName, " +
						" (select iRank from tbDesignationInfo where vDesignationId=di.vDesignationId)iRank,ei.vDepartmentId,ei.vDepartmentName " +
						" from tbLoanApplication as la" +
						" inner join tbEmpOfficialPersonalInfo as ei on la.vAutoEmployeeId=ei.vEmployeeId inner join tbEmpSectionInfo as si" +
						" on ei.vEmployeeId=si.vEmployeeId inner join tbEmpDesignationInfo as di on ei.vEmployeeId=di.vEmployeeId" +
						" where la.dApplicationDate<='"+dDbFormat.format(dAsOnDate.getValue())+"' " +
						" and ei.vSectionId like '"+section+"' and ei.vDepartmentId like '"+deptId+"' and la.iSanctionStatus='1' and ei.vUnitid='"+cmbUnit.getValue()+"' " +
						" order by ei.vUnitName,ei.vDepartmentName,ei.vSectionName,iRank,dJoiningDate ";
			}
			
			System.out.println("Report Query: "+query);
			
			if(queryValueCheck(query))
			{
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/account/hrmModule/rptLoanRegister.jasper",
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
}
