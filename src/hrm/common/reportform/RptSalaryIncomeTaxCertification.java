package hrm.common.reportform;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.hibernate.Session;

import com.common.share.CommonButton;
import com.common.share.CommonMethod;
import com.common.share.FocusMoveByEnter;
import com.common.share.ReportDate;
import com.common.share.ReportOption;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractSelect.Filtering;
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
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class RptSalaryIncomeTaxCertification extends Window
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblUnit;
	private Label lblSection;
	private Label lblSalaryMonth;

	private ComboBox cmbApprovedBy;
	private ComboBox cmbSection,cmbDesignation;
	private ComboBox cmbSalaryMonth;
	private ComboBox cmbEmployeeName;
	
	private TextField txtTaxYear;

	private CheckBox chkSectionAll,chkDepartmentAll;
	private CheckBox chkEmployeeName;

	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat fMonth = new SimpleDateFormat("MMMMM");
	private SimpleDateFormat fYear = new SimpleDateFormat("yyyy");

	ArrayList<Component> allComp = new ArrayList<Component>();

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});

	private OptionGroup RadioBtnGroup1;
	TextField txtPath=new TextField();
	//private static final List<String> type2=Arrays.asList(new String[]{"English","Bangla"});
	private CommonMethod cm;
	private String menuId = "";
	static String EmployeeId="";
	static String EmployeeName="";
	private PopupDateField dDate=new PopupDateField();
	public RptSalaryIncomeTaxCertification(SessionBean sessionBean,String menuId)
	{
		this.sessionBean=sessionBean;
		this.setCaption("SALARY AND INCOME TAX CERTIFICATION :: "+sessionBean.getCompany());
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		buildMainLayout();
		setContent(mainLayout);
		setEventAction();
		focusMove();
		authenticationCheck();
		employeeSetData();
		employeeApproved();
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
	
	public void employeeSetData()
	{
		cmbEmployeeName.removeAllItems();
		
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select distinct vEmployeeId,vEmployeeCode,vEmployeeName from tbMonthlySalary " +
					/*" where vUnitId='"+cmbApprovedBy.getValue().toString()+"' "+
					" and vDepartmentId like '"+(chkDepartmentAll.booleanValue()?"%":cmbDesignation.getValue()==null?"%":cmbDesignation.getValue())+"' "+
					" and vSectionId like '"+(chkSectionAll.booleanValue()?"%":cmbSection.getValue()==null?"%":cmbSection.getValue())+"' "+
					" and YEAR(dSalaryDate)=YEAR('"+dFormat.format(cmbSalaryMonth.getValue())+"') " +
					" and MONTH(dSalaryDate)=MONTH('"+dFormat.format(cmbSalaryMonth.getValue())+"') " +*/
					" order by vEmployeeName";
			
			System.out.println("query :"+query);
			
			List <?> list=session.createSQLQuery(query).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbEmployeeName.addItem(element[0]);
				cmbEmployeeName.setItemCaption(element[0], (element[1]+">>"+element[2]));
			}
			
			
		}
		catch(Exception exp){
			showNotification("employeeSetData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	public void employeeApproved()
	{
		cmbApprovedBy.removeAllItems();
		
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select distinct vEmployeeId,vEmployeeCode,vEmployeeName from tbEmpOfficialPersonalInfo " +
					" order by vEmployeeName";
			
			System.out.println("query :"+query);
			
			List <?> list=session.createSQLQuery(query).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbApprovedBy.addItem(element[0]);
				cmbApprovedBy.setItemCaption(element[0], (element[1]+">>"+element[2]));
				
				
			}
		}
		catch(Exception exp){
			showNotification("employeeApproved",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	public void cmbEmployeeSetDesignation(String empId)
	{
		cmbDesignation.removeAllItems();
		
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select distinct vDesignationName from tbEmpOfficialPersonalInfo where vEmployeeId='"+empId+"' order by vDesignationName desc";
			
			System.out.println("query :"+query);
			
			List <?> list=session.createSQLQuery(query).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				//Object[] element = (Object[]) iter.next();
				cmbDesignation.addItem(iter.next().toString());
				//cmbDesignation.setItemCaption(element[0], (element[1]+">>"+element[2]));
			}
		}
		catch(Exception exp){
			showNotification("employeeSetData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void setEventAction()
	{
		cmbApprovedBy.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
				if(cmbApprovedBy.getValue()!=null)
				{
					cmbEmployeeSetDesignation(cmbApprovedBy.getValue().toString());
				}
			}
		});
		cmbEmployeeName.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
				if(cmbEmployeeName.getValue()!=null)
				{
					Session session=SessionFactoryUtil.getInstance().getCurrentSession();
					session.beginTransaction();
					String sql="select vEmployeeCode,vEmployeeName from tbMonthlySalary where vEmployeeId='"+cmbEmployeeName.getValue().toString()+"'";
					Iterator<?> iter=session.createSQLQuery(sql).list().iterator();
					Object[] element=(Object[])iter.next();
					EmployeeId=element[0].toString();
					EmployeeName=element[1].toString();
				}
			}
		});
		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbEmployeeName.getValue()!=null) 
				{
					if(cmbApprovedBy.getValue()!=null) 
					{
						if(cmbDesignation.getValue()!=null) 
						{
							reportShow();
						}
						else
						{
							showNotification("Warning","Select Designation ",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else
					{
						showNotification("Warning","Select Approved by",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					showNotification("Warning","Select Employee",Notification.TYPE_WARNING_MESSAGE);
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
		String report = "";
		try
		{
			
			
			String query = 
			
					"select * from funSalaryAIT('"+cmbEmployeeName.getValue()+"')";
			
			
			System.out.println("reportShow: "+query);
			

			if(queryValueCheck(query))
			{
				
				
				
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
				hm.put("SysDate",reportTime.getTime);
				hm.put("year",txtTaxYear.getValue().toString());
				hm.put("recvName",cmbApprovedBy.getItemCaption(cmbApprovedBy.getValue()).substring(cmbApprovedBy.getItemCaption(cmbApprovedBy.getValue()).indexOf(">>")+2, cmbApprovedBy.getItemCaption(cmbApprovedBy.getValue()).length()));
				hm.put("recvDesignation",cmbDesignation.getItemCaption(cmbDesignation.getValue()));
				hm.put("id",EmployeeId);
				hm.put("name",EmployeeName);
				hm.put("Date", sessionBean.dfBd.format(dDate.getValue()));
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/account/hrmModule/RptSalary&IncomeTaxCertification.jasper",
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
		setWidth("470px");
		setHeight("320px");
		
		dDate.setImmediate(true);
		dDate.setDateFormat("dd-MM-yyyy");
		dDate.setWidth("110px");
		dDate.setHeight("-1px");
		dDate.setValue(new java.util.Date());
		mainLayout.addComponent(new Label("Date :"),"top:30px; left:30px");
		mainLayout.addComponent(dDate,"top:30px; left:130px");
		
		
		// cmbEmployeeName
		cmbEmployeeName = new ComboBox();
		cmbEmployeeName.setImmediate(true);
		cmbEmployeeName.setWidth("260px");
		cmbEmployeeName.setHeight("-1px");
		cmbEmployeeName.setNullSelectionAllowed(true);
		cmbEmployeeName.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Employee ID: "),"top:60px; left:30.0px;");
		mainLayout.addComponent(cmbEmployeeName, "top:58px; left:130.0px;");
		
		txtTaxYear=new TextField();
		txtTaxYear.setWidth("150px");
		txtTaxYear.setHeight("-1px");
		txtTaxYear.setImmediate(true);
		mainLayout.addComponent(new Label("Tax Year :"),"top:90px; left:30px");
		mainLayout.addComponent(txtTaxYear,"top:88px; left:130px");

		// cmbSection
		cmbApprovedBy = new ComboBox();
		cmbApprovedBy.setImmediate(true);
		cmbApprovedBy.setWidth("260px");
		cmbApprovedBy.setHeight("-1px");
		cmbApprovedBy.setNullSelectionAllowed(true);
		cmbApprovedBy.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Approved By: "),"top:120px; left:30.0px;");
		mainLayout.addComponent(cmbApprovedBy, "top:118px; left:130.0px;");
		

		// cmbSection
		cmbDesignation = new ComboBox();
		cmbDesignation.setImmediate(true);
		cmbDesignation.setWidth("260px");
		cmbDesignation.setHeight("-1px");
		cmbDesignation.setNullSelectionAllowed(true);
		cmbDesignation.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Designation : "),"top:150px; left:30.0px;");
		mainLayout.addComponent(cmbDesignation, "top:148px; left:130.0px;");

		

		// optionGroup
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