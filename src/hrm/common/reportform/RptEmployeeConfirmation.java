package hrm.common.reportform;

import java.text.SimpleDateFormat;
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
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
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
public class RptEmployeeConfirmation extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	
	private ComboBox cmbSection,cmbUnit,cmbDepartment;
	CheckBox chkSectionAll=new CheckBox("All");
	private CheckBox chkUnitAll = new CheckBox("All");
	private CheckBox chkDepartmentAll = new CheckBox("All");
	private Label lblFromDate;
	private PopupDateField dFromDate;

	private Label lblToDate;
	private PopupDateField dToDate;
	
	private Label lblMonth;
	private PopupDateField dMonth;

	private OptionGroup opgTimeSelect;
	private List<?> timeSelect = Arrays.asList(new String[]{"Monthly","Between Date"});

	//private Label lblLoanType;
	//private ComboBox cmbLoanType;
	//private static final String[] loanType = new String[] {"Advanced","Salary Loan","PF Loan"};
	//private CheckBox chkLoanTypeAll;

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});

	private CommonButton cButton = new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	private SimpleDateFormat dFormatSql = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dFormatBangla = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat dDbMonth = new SimpleDateFormat("MMMMMMMMM-yyyy");

	TextField txtPath=new TextField();
	TextField txtAddress=new TextField();
	private CommonMethod cm;
	private String menuId = "";
	public RptEmployeeConfirmation(SessionBean sessionBean,String menuId) 
	{
		this.sessionBean=sessionBean;
		this.setCaption("EMPLOYEE CONFIRMATION :: "+sessionBean.getCompany());
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
		cmbUnit.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "select distinct vUnitId,vUnitName from tbEmpOfficialPersonalInfo where bStatus=1 order by vUnitName";
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
	private void cmbDepartmentData()
	{
		cmbDepartment.removeAllItems();
		String unitId="%";
		if(!chkUnitAll.booleanValue())
		{
			unitId=cmbUnit.getValue().toString();
		}
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "select distinct vDepartmentId,vDepartmentName from tbEmpOfficialPersonalInfo where bStatus=1 and vUnitId like '"+unitId+"' order by vDepartmentName";
			List <?> lst=session.createSQLQuery(sql).list();

			if(!lst.isEmpty())
			{
				Iterator <?> itr=lst.iterator();
				while(itr.hasNext())
				{
					Object[] element=(Object[])itr.next();
					cmbDepartment.addItem(element[0]);
					cmbDepartment.setItemCaption(element[0], element[1].toString());
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
		cmbSection.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		String unitId="%",deptId="%";
		if(!chkUnitAll.booleanValue())
		{
			unitId=cmbUnit.getValue().toString();
		}
		if(!chkDepartmentAll.booleanValue())
		{
			deptId=cmbDepartment.getValue().toString();
		}
		try
		{
			String sql="select distinct vSectionId,vSectionName from tbEmpOfficialPersonalInfo where bStatus=1 and vUnitId like '"+unitId+"' and vDepartmentId like '"+deptId+"' "+
					" order by vSectionName";
			List <?> lst=session.createSQLQuery(sql).list();

			if(!lst.isEmpty())
			{
				Iterator <?> itr=lst.iterator();
				while(itr.hasNext())
				{
					Object[] element=(Object[])itr.next();
					cmbSection.addItem(element[0]);
					cmbSection.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbSectionData", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
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
				chkDepartmentAll.setValue(false);
				cmbDepartment.setEnabled(false);
				if(cmbUnit.getValue()!=null)
				{
					chkUnitAll.setValue(false);
					cmbDepartment.setEnabled(true);
					cmbDepartmentData();
				}
			}
		});
		chkUnitAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				cmbDepartment.removeAllItems();
				chkDepartmentAll.setValue(false);
				cmbDepartment.setEnabled(false);
				if(chkUnitAll.booleanValue())
				{
					cmbUnit.setValue(null);
					cmbUnit.setEnabled(false);
					cmbDepartment.setEnabled(true);
					cmbDepartmentData();
				}
				else
				{
					cmbUnit.setEnabled(true);
					cmbDepartment.setEnabled(false);
				}
			
			

			}
		});
		cmbDepartment.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSection.removeAllItems();
				chkSectionAll.setValue(false);
				cmbSection.setEnabled(false);
				if(cmbUnit.getValue()!=null || chkUnitAll.booleanValue())
				{
					if(cmbDepartment.getValue()!=null)
					{
						chkDepartmentAll.setValue(false);
						cmbSectionData();
						cmbSection.setEnabled(true);
					}
				}
			}
		});

		chkDepartmentAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				cmbSection.removeAllItems();
				chkSectionAll.setValue(false);
				cmbSection.setEnabled(false);
				if(cmbUnit.getValue()!=null || chkUnitAll.booleanValue())
				{
					if(chkDepartmentAll.booleanValue())
					{
						cmbDepartment.setValue(null);
						cmbDepartment.setEnabled(false);
						cmbSection.setEnabled(true);
						cmbSectionData();
					}
					else
					{
						cmbDepartment.setEnabled(true);
						cmbSection.setEnabled(false);
					}
				}
				else{
					chkDepartmentAll.setValue(false);
					showNotification("Warning..","Select Project Please!",Notification.TYPE_HUMANIZED_MESSAGE);
				}
			}
		});

		cmbSection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				
				if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
				{
					if(cmbSection.getValue()!=null)
					{
						chkSectionAll.setValue(false);
						
					}
				}
			}
		});

		chkSectionAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
				{
					if(chkSectionAll.booleanValue())
					{
						cmbSection.setValue(null);
						cmbSection.setEnabled(false);
	
					}
					else
					{
						cmbSection.setEnabled(true);
					}
				}
				else{
					chkSectionAll.setValue(false);
					showNotification("Warning..","Select Department Please!",Notification.TYPE_HUMANIZED_MESSAGE);
				}
			}
		});
		cButton.btnPreview.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbUnit.getValue()!=null || chkUnitAll.booleanValue())
				{
					if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
					{
						if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
						{
							if(opgTimeSelect.getValue().toString().equals("Monthly"))
							{
								if(dMonth.getValue()!=null)
								{
									reportpreview();
								}
							}
							else
							{
								if(dFromDate.getValue()!=null && dToDate.getValue()!=null)
								{
									reportpreview();
								}
								else
								{
									showNotification("Warning","Select Date Range",Notification.TYPE_WARNING_MESSAGE);
								}
							}
						}
						else
						{
							showNotification("Select Section",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else
					{
						showNotification("Select Department",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					showNotification("Select Project!",Notification.TYPE_WARNING_MESSAGE);
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
		opgTimeSelect.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				opgTimeSelectAction();
			}
		});
	}
	public void opgActionWiseVisible(boolean b)
	{
		lblMonth.setVisible(b);
	    dMonth.setVisible(b);
	    lblFromDate.setVisible(!b);
	    dFromDate.setVisible(!b);
	    lblToDate.setVisible(!b);
	    dToDate.setVisible(!b);
	}
	private void opgTimeSelectAction(){
		if(opgTimeSelect.getValue().toString()=="Monthly"){
			opgActionWiseVisible(true);
			refreshWork();
		}
		else{
			opgActionWiseVisible(false);
			refreshWork();
		}
	}
	public void refreshWork(){
		dFromDate.setValue(new Date());
		dToDate.setValue(new Date());
		dMonth.setValue(new Date());
	}
	
	private void reportpreview()
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		String loanType= "",report="",query="",dDate="";
		
		
		/*if(opgTimeSelect.getValue().toString().equals("Monthly")){
			String fromDate = dFormatSql.format(dMonth.getValue())+ " 00:00:00";
		}
		else{
			String fromDate = dFormatSql.format(dFromDate.getValue())+ " 00:00:00";
			String toDate = dFormatSql.format(dToDate.getValue())+ " 23:59:59";	
		}*/
		try
		{
			//"Monthly","Between Date
			if(opgTimeSelect.getValue().toString().equals("Monthly"))
			{
				dDate="Month: "+dDbMonth.format(dMonth.getValue());
				report="report/account/hrmModule/RptMonthlyEmployeeConfirmationList.jasper";
				query="select vEmployeeCode,vEmployeeName,vDesignationName,vUnitName,vSectionName,vEmployeeType,dJoiningDate,iProbationPeriod,vConfirmationDate," +
						"bStatus,(select iRank from tbDesignationInfo where vDesignationId=a.vDesignationId)iRank,vDepartmentId,vDepartmentName " +
						"from tbEmpOfficialPersonalInfo a " +
						"where bStatus=1 and month(vConfirmationDate) = month('"+dFormatSql.format(dMonth.getValue())+"') " +
						"and YEAR(vConfirmationDate) = YEAR('"+dFormatSql.format(dMonth.getValue())+"') " +
						"and vUnitId like '"+(cmbUnit.getValue()!=null?cmbUnit.getValue():"")+"' and vDepartmentId like '"+(cmbDepartment.getValue()!=null?cmbDepartment.getValue():"%")+"' " +
						"and vSectionId like '"+(cmbSection.getValue()!=null?cmbSection.getValue():"%")+"' " +
						"order by vUnitName,vDepartmentName,vSectionName,iRank,dJoiningDate";
			}
			else if(opgTimeSelect.getValue().toString().equals("Between Date"))
			{
				dDate="Date: "+dFormatBangla.format(dFromDate.getValue())+" to: "+dFormatBangla.format(dToDate.getValue());
				report="report/account/hrmModule/RptMonthlyEmployeeConfirmationList.jasper";
				//report="report/account/hrmModule/RptDateBetweenEmployeeConfirmationList.jasper";
				query="select vEmployeeCode,vEmployeeName,vDesignationName,vUnitName,vSectionName,vEmployeeType,dJoiningDate,iProbationPeriod,vConfirmationDate," +
						"bStatus,(select iRank from tbDesignationInfo where vDesignationId=a.vDesignationId)iRank,vDepartmentId,vDepartmentName " +
						"from tbEmpOfficialPersonalInfo a " +
						"where bStatus=1 and vConfirmationDate  between '"+dFormatSql.format(dFromDate.getValue())+"' and '"+dFormatSql.format(dToDate.getValue())+"' " +
						"and vUnitId like '"+(cmbUnit.getValue()!=null?cmbUnit.getValue():"")+"' and vDepartmentId like '"+(cmbDepartment.getValue()!=null?cmbDepartment.getValue():"%")+"' " +
						"and vSectionId like '"+(cmbSection.getValue()!=null?cmbSection.getValue():"%")+"' " +
						"order by vUnitName,vDepartmentName,vSectionName,iRank,dJoiningDate";
			}
			System.out.println("Report Query: "+query); 

			if(queryValueCheck(query))
			{
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("userName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());

				hm.put("dDate", dDate);
				//hm.put("fromDate", dFromDate.getValue());
				//hm.put("toDate", dToDate.getValue());
				hm.put("SysDate",reportTime.getTime);
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("branch", "Project Name : "+cmbUnit.getItemCaption(cmbUnit.getValue().toString()));
				hm.put("sql", query);

				Window win = new ReportViewer(hm,report,
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
	
	private AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("490px");
		setHeight("280px");

		opgTimeSelect=new OptionGroup("",timeSelect);
		opgTimeSelect.select("Monthly");
		opgTimeSelect.setImmediate(true);
		opgTimeSelect.setStyleName("horizontal");
		mainLayout.addComponent(opgTimeSelect, "top:10.0px; left:150px;");

		cmbUnit=new ComboBox();
		cmbUnit.setWidth("250.0px");
		cmbUnit.setHeight("-1px");
		cmbUnit.setImmediate(true);
		mainLayout.addComponent(new Label("Project : "), "top:40.0px;left:30.0px;");
		mainLayout.addComponent(cmbUnit, "top:38.0px;left:135.0px");
		
		chkUnitAll=new CheckBox("All");
		chkUnitAll.setImmediate(true);
		mainLayout.addComponent(chkUnitAll,"top:40px; left:385px;");
		chkUnitAll.setVisible(false);
		
		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("250px");
		mainLayout.addComponent(new Label("Department : "), "top:70px; left:30.0px;");
		mainLayout.addComponent(cmbDepartment, "top:68px; left:135px;");

		chkDepartmentAll = new CheckBox("All");
		chkDepartmentAll.setImmediate(true);
		mainLayout.addComponent(chkDepartmentAll,"top:70px; left:385px;");
		
		cmbSection=new ComboBox();
		cmbSection.setWidth("250.0px");
		cmbSection.setHeight("-1px");
		cmbSection.setImmediate(true);
		mainLayout.addComponent(new Label("Section : "), "top:100px;left:30.0px;");
		mainLayout.addComponent(cmbSection, "top:98px;left:135.0px");
		chkSectionAll.setImmediate(true);
		mainLayout.addComponent(chkSectionAll, "top:100px;left:385.0px");

		// lblFromDate
		lblFromDate = new Label("From Date :");
		lblFromDate.setImmediate(false);
		lblFromDate.setWidth("100.0%");
		lblFromDate.setHeight("-1px");
		mainLayout.addComponent(lblFromDate,"top:130px; left:30.0px;");
		lblFromDate.setVisible(false);
		// dFromDate
		dFromDate = new PopupDateField();
		dFromDate.setImmediate(true);
		dFromDate.setWidth("110px");
		dFromDate.setHeight("-1px");
		dFromDate.setDateFormat("dd-MM-yyyy");
		dFromDate.setValue(new java.util.Date());
		dFromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dFromDate, "top:128px; left:135.0px;");
		dFromDate.setVisible(false);
		//lblMonth
		lblMonth = new Label("Month :");
		lblMonth.setImmediate(false);
		lblMonth.setWidth("100.0%");
		lblMonth.setHeight("-1px");
		mainLayout.addComponent(lblMonth,"top:130px; left:30.0px;");
		
		// dMonth
		dMonth = new PopupDateField();
		dMonth.setImmediate(true);
		dMonth.setWidth("150px");
		dMonth.setHeight("-1px");
		dMonth.setDateFormat("MMMMMMMMM-yyyy");
		dMonth.setValue(new java.util.Date());
		dMonth.setResolution(PopupDateField.RESOLUTION_MONTH);
		mainLayout.addComponent(dMonth, "top:128px; left:135.0px;");
		
		// lblToDate
		lblToDate = new Label("To");
		lblToDate.setImmediate(false);
		lblToDate.setWidth("100.0%");
		lblToDate.setHeight("-1px");
		mainLayout.addComponent(lblToDate,"top:130px; left:250.0px;");
		lblToDate.setVisible(false);

		// dToDate
		dToDate = new PopupDateField();
		dToDate.setImmediate(true);
		dToDate.setWidth("110px");
		dToDate.setHeight("-1px");
		dToDate.setDateFormat("dd-MM-yyyy");
		dToDate.setValue(new java.util.Date());
		dToDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dToDate, "top:128px; left:268.0px;");
		dToDate.setVisible(false);
		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:140px;left:150px;");
		RadioBtnGroup.setVisible(false);
		//mainLayout.addComponent(new Label("_______________________________________________________________________________"), "top:170.0px;left:20.0px;right:20.0px;");
		mainLayout.addComponent(cButton,"top:200.opx; left:130.0px");

		return mainLayout;
	}
}
