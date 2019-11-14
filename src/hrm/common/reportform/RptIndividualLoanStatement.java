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
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class RptIndividualLoanStatement extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblEmployeeName;
	private ComboBox cmbEmployeeName,cmbUnit;

	private Label lblFromDate;
	private PopupDateField dFromDate;

	private Label lblToDate;
	private PopupDateField dToDate;

	private OptionGroup opgEmployee;
	private List<?> lstEmployee = Arrays.asList(new String[]{"Employee ID","Finger ID","Employee Name"});

	private Label lblLoanType;
	private ComboBox cmbLoanType;
	private static final String[] loanType = new String[] {"Salary Loan"};
	private CheckBox chkLoanTypeAll;

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});

	private CommonButton cButton = new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	private SimpleDateFormat dDbFormat = new SimpleDateFormat("yyyy-MM-dd");
	private CommonMethod cm;
	private String menuId = "";
	public RptIndividualLoanStatement(SessionBean sessionBean,String menuId) 
	{
		this.sessionBean=sessionBean;
		this.setCaption("INDIVIDUAL LOAN STATEMENT :: "+sessionBean.getCompany());
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		buildMainLayout();
		setContent(mainLayout);
		cmbUnitDataLoad();
		//cmbAddEmployeeName();
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
				if(cmbUnit.getValue()!=null)
				{
					cmbAddEmployeeName();
				}
				else{
					cmbEmployeeName.setValue(null);
				}
			}
		});
		cButton.btnPreview.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbUnit.getValue()!=null)
				{
					if(cmbEmployeeName.getValue()!=null)
					{
						if(dFromDate.getValue()!=null && dToDate.getValue()!=null)
						{
							if(cmbLoanType.getValue()!=null || chkLoanTypeAll.booleanValue()==true)
							{
								reportpreview();
							}
							else
							{
								showNotification("Warning","Select Loan Type",Notification.TYPE_WARNING_MESSAGE);
							}
						}
						else
						{
							showNotification("Warning","Select Date Range",Notification.TYPE_WARNING_MESSAGE);
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

		chkLoanTypeAll.addListener(new Listener()
		{
			public void componentEvent(Event event)
			{
				if(chkLoanTypeAll.booleanValue()==true)
				{
					cmbLoanType.setEnabled(false);
					cmbLoanType.setValue(null);
				}
				else
				{
					cmbLoanType.setEnabled(true);
				}
			}
		});

		opgEmployee.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmployeeName.removeAllItems();
				cmbAddEmployeeName();
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
		setWidth("490px");
		setHeight("280px");

		opgEmployee=new OptionGroup("",lstEmployee);
		opgEmployee.select("Employee ID");
		opgEmployee.setImmediate(true);
		opgEmployee.setStyleName("horizontal");
		mainLayout.addComponent(opgEmployee, "top:10.0px; left:50.0px;");

		cmbUnit=new ComboBox();
		cmbUnit.setWidth("250.0px");
		cmbUnit.setHeight("-1px");
		cmbUnit.setImmediate(true);
		mainLayout.addComponent(new Label("Project : "), "top:40.0px;left:30.0px;");
		mainLayout.addComponent(cmbUnit, "top:38.0px;left:135.0px");
		
		// lblEmployeeName
		lblEmployeeName = new Label("Employee Name :");
		lblEmployeeName.setImmediate(false);
		lblEmployeeName.setWidth("100.0%");
		lblEmployeeName.setHeight("-1px");
		mainLayout.addComponent(lblEmployeeName,"top:70.0px; left:30.0px;");

		// cmbEmployeeName
		cmbEmployeeName = new ComboBox();
		cmbEmployeeName.setImmediate(true);
		cmbEmployeeName.setWidth("250.0px");
		cmbEmployeeName.setHeight("-1px");
		cmbEmployeeName.setNullSelectionAllowed(true);
		cmbEmployeeName.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbEmployeeName, "top:68.0px; left:135.0px;");

		// lblFromDate
		lblFromDate = new Label("From Date :");
		lblFromDate.setImmediate(false);
		lblFromDate.setWidth("100.0%");
		lblFromDate.setHeight("-1px");
		mainLayout.addComponent(lblFromDate,"top:100.0px; left:30.0px;");

		// dFromDate
		dFromDate = new PopupDateField();
		dFromDate.setImmediate(true);
		dFromDate.setWidth("110px");
		dFromDate.setHeight("-1px");
		dFromDate.setDateFormat("dd-MM-yyyy");
		dFromDate.setValue(new java.util.Date());
		dFromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dFromDate, "top:98.0px; left:135.0px;");

		// lblToDate
		lblToDate = new Label("To");
		lblToDate.setImmediate(false);
		lblToDate.setWidth("100.0%");
		lblToDate.setHeight("-1px");
		mainLayout.addComponent(lblToDate,"top:100.0px; left:250.0px;");

		// dToDate
		dToDate = new PopupDateField();
		dToDate.setImmediate(true);
		dToDate.setWidth("110px");
		dToDate.setHeight("-1px");
		dToDate.setDateFormat("dd-MM-yyyy");
		dToDate.setValue(new java.util.Date());
		dToDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dToDate, "top:98.0px; left:268.0px;");

		// lblLoanType
		lblLoanType = new Label("Loan Type :");
		lblLoanType.setImmediate(false);
		lblLoanType.setWidth("100.0%");
		lblLoanType.setHeight("-1px");
		mainLayout.addComponent(lblLoanType,"top:130.0px; left:30.0px;");

		// cmbLoanType
		cmbLoanType = new ComboBox();
		cmbLoanType.setImmediate(true);
		cmbLoanType.setWidth("150px");
		cmbLoanType.setHeight("-1px");
		cmbLoanType.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbLoanType, "top:128.0px; left:135.0px;");
		cmbLoanType.addItem("Salary Loan");
		cmbLoanType.setValue("Salary Loan");

		// chkLoanTypeAll
		chkLoanTypeAll = new CheckBox("All");
		chkLoanTypeAll.setImmediate(true);
		chkLoanTypeAll.setHeight("-1px");
		chkLoanTypeAll.setWidth("-1px");
		//mainLayout.addComponent(chkLoanTypeAll, "top:128.0px; left:290.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:150.0px;left:135.0px;");
		RadioBtnGroup.setVisible(false);

		//mainLayout.addComponent(new Label("_______________________________________________________________________________"), "top:170.0px;left:20.0px;right:20.0px;");
		mainLayout.addComponent(cButton,"top:190.opx; left:130.0px");

		return mainLayout;
	}

	private void cmbAddEmployeeName()
	{
		cmbEmployeeName.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String querySection = " Select b.vAutoEmployeeId, a.vEmployeeCode from tbEmpOfficialPersonalInfo as a inner join " +
					"tbLoanApplication as b on a.vEmployeeId=b.vAutoEmployeeId "
					+ " where a.vUnitId='"+cmbUnit.getValue()+"' order by b.iAutoId ";
			lblEmployeeName.setValue("Employee ID :");
			
			if(opgEmployee.getValue()=="Employee Name")
			{
				querySection = "Select b.vAutoEmployeeId, a.vEmployeeName from tbEmpOfficialPersonalInfo as a inner join " +
					"tbLoanApplication as b on a.vEmployeeId=b.vAutoEmployeeId "
					+ " where a.vUnitId='"+cmbUnit.getValue()+"' order by b.iAutoId";
				lblEmployeeName.setValue("Employee Name :");
			}
			
			else if(opgEmployee.getValue()=="Finger ID")
			{
				querySection = "Select b.vAutoEmployeeId, a.vFingerId from tbEmpOfficialPersonalInfo as a inner join " +
					"tbLoanApplication as b on a.vEmployeeId=b.vAutoEmployeeId "
					+ " where a.vUnitId='"+cmbUnit.getValue()+"' order by b.iAutoId";
				lblEmployeeName.setValue("Finger ID :");
			}
			System.out.println("Employee: "+querySection);
			List <?> list = session.createSQLQuery(querySection).list();	
			for (Iterator <?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element =  (Object[]) iter.next();	

				cmbEmployeeName.addItem(element[0]);
				cmbEmployeeName.setItemCaption(element[0], element[1].toString());	
			}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}		

	private void reportpreview()
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		String loanType= "";

		String fromDate = dDbFormat.format(dFromDate.getValue())+ " 00:00:00";
		String toDate = dDbFormat.format(dToDate.getValue())+ " 23:59:59";

		if(chkLoanTypeAll.booleanValue()==true)
		{
			loanType= "%";
		}
		else
		{
			loanType= cmbLoanType.getValue().toString();
		}

		try
		{
			HashMap <String,Object> hm = new HashMap <String,Object> ();
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("userName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());

			hm.put("fromDate", dFromDate.getValue());
			hm.put("toDate", dToDate.getValue());
			hm.put("SysDate",reportTime.getTime);
			hm.put("logo", sessionBean.getCompanyLogo());

			String query = " select * from  funIndividualLoanStatementNew('"+cmbEmployeeName.getValue().toString()+"','"+fromDate+"','"+toDate+"','"+loanType+"','"+cmbUnit.getValue()+"') ";
					//+ " where vUnitId='"+cmbUnit.getValue()+"'";
			

			System.out.println("Report Query: "+query);
			
			if(queryValueCheck(query))
			{
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/account/hrmModule/rptIndividualLoanStatement.jasper",
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
