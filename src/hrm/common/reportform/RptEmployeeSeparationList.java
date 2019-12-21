package hrm.common.reportform;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

import com.common.share.CommonButton;
import com.common.share.CommonMethod;
import com.common.share.FocusMoveByEnter;
import com.common.share.GenerateExcelReport;
import com.common.share.ReportDate;
import com.common.share.ReportOption;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ExternalResource;
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
public class RptEmployeeSeparationList extends Window {

	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblSection;
	private Label lblSalaryMonth;

	private Label lblSeparationType;
	private ComboBox cmbSeparationType,cmbUnit;

	private ComboBox cmbSection,cmbDepartment;
	private CheckBox chkSectionAll,chkUnitAll,chkDepartmentAll;
	private CheckBox chkSeparationTypeAll;
	private ComboBox dSalaryMonth;

	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
	ArrayList<Component> allComp = new ArrayList<Component>();

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other","Excel"});

	SimpleDateFormat dRptFormat = new SimpleDateFormat("dd-MM-yyyy");
	SimpleDateFormat dMonthYear = new SimpleDateFormat("MMMMM-yyyy");
	TextField txtPath=new TextField();
	TextField txtAddress=new TextField();
	private CommonMethod cm;
	private String menuId = "";
	public RptEmployeeSeparationList(SessionBean sessionBean,String menuId)
	{
		this.sessionBean=sessionBean;
		this.setCaption("EMPLOYEE SEPARATION LIST :: "+sessionBean.getCompany());
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		buildMainLayout();
		setContent(mainLayout);

		salaryDateLoad();
		//cmbUnitDataLoad();
		//cmbSeparationDataLoad();
		//cmbSectionAddData();
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
	private void salaryDateLoad()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();

		try
		{
			String query="select distinct convert(date,DATEADD(s,-1,DATEADD(mm, DATEDIFF(m,0,dSalaryDate)+1,0)))dSalaryDate,vSalaryMonth,vSalaryYear " +
					"from tbMonthlySalary order by dSalaryDate desc";
			System.out.println("salaryDateLoad :"+query);
			
			List <?> list=session.createSQLQuery(query).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				dSalaryMonth.addItem(element[0]);
				dSalaryMonth.setItemCaption(element[0],element[1]+"-"+element[2]);
			}
		}
		catch(Exception exp)
		{
			showNotification("salaryDateLoad",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			session.close();
		}
	}
	private void txtPathDataLoad() {
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		
		try
		{
			String sql = "select vUnitId,imageLoc,address from tbUnitInfo where vUnitId='"+cmbUnit.getValue().toString()+"' ";
			
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator();iter.hasNext();)
			{
				Object element[] = (Object[]) iter.next();
				txtPath.setValue(element[1].toString());
				txtAddress.setValue(element[2].toString());
				
				System.out.println("Id : "+element[0]+" Path  :"+element[1]);
				
			}
		}
		catch(Exception exp)
		{
			showNotification(exp+" Image path set :",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	
	private void cmbSeparationDataLoad() {
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "select distinct 0,vEmployeeStatus from tbEmpOfficialPersonalInfo where bStatus=0 " +
					"and MONTH(vStatusDate)=MONTH('"+dSalaryMonth.getValue()+"') and YEAR(vStatusDate)=YEAR('"+dSalaryMonth.getValue()+"')";
			System.out.println("cmbSeparationDataLoad: "+sql);
			
			List<?> list = session.createSQLQuery(sql).list();
			cmbSeparationType.removeAllItems();
			for(Iterator<?> iter = list.iterator();iter.hasNext();)
			{
				Object element[] = (Object[]) iter.next();
				cmbSeparationType.addItem(element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification(exp+"cmbSeparationDataLoad",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void cmbUnitDataLoad() {
		String separation="%";
		if(!chkSeparationTypeAll.booleanValue())
		{
			separation=cmbSeparationType.getValue().toString();
		}
		
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "select distinct b.vUnitId,b.vUnitName from tbMonthlySalary a " +
					"inner join tbEmpOfficialPersonalInfo b on a.vEmployeeID=b.vEmployeeId where bStatus=0 and vEmployeeStatus like '"+separation+"' " +
					"and MONTH(vStatusDate)=MONTH('"+dSalaryMonth.getValue()+"') and YEAR(vStatusDate)=YEAR('"+dSalaryMonth.getValue()+"') " +
					"and MONTH(a.dSalaryDate)=MONTH('"+dSalaryMonth.getValue()+"') and YEAR(a.dSalaryDate)=YEAR('"+dSalaryMonth.getValue()+"')";
			System.out.println("cmbUnitDataLoad: "+sql);
			
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
			showNotification(exp+"cmbUnitDataLoad",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	public void cmbDepartmentAddData()
	{
		String unit="%",separation="%";
		if(!chkSeparationTypeAll.booleanValue())
		{
			separation=cmbSeparationType.getValue().toString();
		}
		if(!chkUnitAll.booleanValue())
		{
			unit=cmbUnit.getValue().toString();
		}
		
		cmbDepartment.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct b.vDepartmentId,b.vDepartmentName from tbMonthlySalary a " +
					"inner join tbEmpOfficialPersonalInfo b on a.vEmployeeID=b.vEmployeeId where bStatus=0 and vEmployeeStatus like '"+separation+"'" +
					"and MONTH(vStatusDate)=MONTH('"+dSalaryMonth.getValue()+"') and YEAR(vStatusDate)=YEAR('"+dSalaryMonth.getValue()+"') " +
					"and MONTH(a.dSalaryDate)=MONTH('"+dSalaryMonth.getValue()+"') and YEAR(a.dSalaryDate)=YEAR('"+dSalaryMonth.getValue()+"') " +
					"and a.vUnitId like '"+unit+"'";
		
			List <?> list=session.createSQLQuery(query).list();
		
			for(Iterator <?> iter=list.iterator();iter.hasNext();){

				Object[] element = (Object[]) iter.next();
				cmbDepartment.addItem(element[0]);
				cmbDepartment.setItemCaption(element[0], (String) element[1]);
			}
			System.out.println("cmbDepartment: "+query);
		}
		catch(Exception exp){
			showNotification("cmbDepartmentAddData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	public void cmbSectionAddData()
	{
		String unit="%",separation="%";
		if(!chkSeparationTypeAll.booleanValue())
		{
			separation=cmbSeparationType.getValue().toString();
		}
		if(!chkUnitAll.booleanValue())
		{
			unit=cmbUnit.getValue().toString();
		}
		
		cmbSection.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct b.vSectionId,b.vSectionName from tbMonthlySalary a " +
					" inner join tbEmpOfficialPersonalInfo b on a.vEmployeeID=b.vEmployeeId where bStatus=0 and vEmployeeStatus like '"+separation+"'" +
					" and MONTH(vStatusDate)=MONTH('"+dSalaryMonth.getValue()+"') and YEAR(vStatusDate)=YEAR('"+dSalaryMonth.getValue()+"') " +
					" and MONTH(a.dSalaryDate)=MONTH('"+dSalaryMonth.getValue()+"') and YEAR(a.dSalaryDate)=YEAR('"+dSalaryMonth.getValue()+"') " +
					" and a.vDepartmentId like '"+(chkDepartmentAll.booleanValue()?"%":cmbDepartment.getValue()==null?"%":cmbDepartment.getValue())+"' " +
					" and a.vUnitId like '"+unit+"'";
		
			List <?> list=session.createSQLQuery(query).list();
		
			for(Iterator <?> iter=list.iterator();iter.hasNext();){

				Object[] element = (Object[]) iter.next();
				cmbSection.addItem(element[0]);
				cmbSection.setItemCaption(element[0], (String) element[1]);
			}
			System.out.println("Division Data: "+query);
		}
		catch(Exception exp){
			showNotification("cmbSectionAddData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void setEventAction()
	{		
		dSalaryMonth.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSeparationType.removeAllItems();
				if(dSalaryMonth.getValue()!=null)
				{
					cmbSeparationDataLoad();
				}
			}
		});
		cmbSeparationType.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(dSalaryMonth.getValue()!=null)
				{
					if(cmbSeparationType.getValue()!=null)
					{
						cmbUnit.removeAllItems();
						
						cmbUnitDataLoad();
					}
				}
			}
		});
		chkSeparationTypeAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(dSalaryMonth.getValue()!=null)
				{
					if(chkSeparationTypeAll.booleanValue())
					{
						cmbUnit.removeAllItems();
						cmbSeparationType.setValue(null);
						cmbSeparationType.setEnabled(false);
						cmbUnitDataLoad();
					}
					else
					{
						cmbSeparationType.setEnabled(true);
					}
				}
				else
				{
					chkSeparationTypeAll.setValue(false);
				}
			}
		});
		
		cmbUnit.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbSeparationType.getValue()!=null || chkSeparationTypeAll.booleanValue())
				{
					cmbDepartment.removeAllItems();				
					if(cmbUnit.getValue()!=null)
					{
						cmbDepartmentAddData();
						//txtPathDataLoad();
					 }				
					
					else{
						
						cmbDepartment.setValue(null);
					}
				}
			
				
			}
		});

		chkUnitAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{

				if(cmbSeparationType.getValue()!=null || chkSeparationTypeAll.booleanValue())
				{
					if(chkUnitAll.booleanValue())
					{
						cmbUnit.setEnabled(false);
						cmbUnit.setValue(null);
						
						cmbDepartmentAddData();
						//txtPathDataLoad();
					}				
					
					else				
					
					{
						cmbUnit.setEnabled(true);
					}
				}
				else
				{
					chkUnitAll.setValue(false);
				}
			
			}
		});
		cmbDepartment.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbSeparationType.getValue()!=null || chkSeparationTypeAll.booleanValue())
				{
					cmbSection.removeAllItems();				
					if(cmbDepartment.getValue()!=null)
					{
						cmbSectionAddData();
					 }				
					
					else{
						
						cmbDepartment.setValue(null);
					}
				}
			
				
			}
		});

		chkDepartmentAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{

				if(cmbSeparationType.getValue()!=null || chkSeparationTypeAll.booleanValue())
				{
					if(chkDepartmentAll.booleanValue())
					{
						cmbDepartment.setEnabled(false);
						cmbDepartment.setValue(null);
						
						cmbSectionAddData();
					}				
					
					else				
					
					{
						cmbDepartment.setEnabled(true);
					}
				}
				else
				{
					chkDepartmentAll.setValue(false);
				}
			
			}
		});
		
		chkSectionAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
				{
					if(chkSectionAll.booleanValue())
					{
						cmbSection.setEnabled(false);
						cmbSection.setValue(null);
					}				
					
					else				
					
					{
						cmbSection.setEnabled(true);
					}
				}
				else
				{
					chkSectionAll.setValue(false);
				}
			}
		});
		

		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(dSalaryMonth.getValue()!=null)
				{
					if(cmbSeparationType.getValue()!=null || chkSeparationTypeAll.booleanValue()==true)
					{
						if(cmbUnit.getValue()!=null || chkUnitAll.booleanValue()==true)
						{
							if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue()==true)
							{
								if(cmbSection.getValue()!=null || chkSectionAll.booleanValue()==true)
								{
									reportShow();
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
							showNotification("Select Project",Notification.TYPE_WARNING_MESSAGE);
						}
					}	
					else
					{
						showNotification("Select Separation Type",Notification.TYPE_WARNING_MESSAGE);
					}			
				}
				else
				{
					showNotification("Select Month",Notification.TYPE_WARNING_MESSAGE);
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
		String query=null;
		String rptName="";
		Session session=SessionFactoryUtil.getInstance().openSession();
		try
		{
			HashMap <String,Object>  hm = new HashMap <String,Object> ();
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone",sessionBean.getCompanyContact());
			hm.put("Month",dFormat.format(dSalaryMonth.getValue()));
			hm.put("user", sessionBean.getUserName()+" "+sessionBean.getUserIp());
			hm.put("SysDate",reportTime.getTime);
			hm.put("logo",sessionBean.getCompanyLogo());

			{
				
				query="select *,vEmployeeStatus,vStatusDate from funMonthlySalarySeparationList (" +
						"'"+(chkUnitAll.booleanValue()?"%":cmbUnit.getValue()==null?"%":cmbUnit.getValue())+"', " +
						"'"+dFormat.format(dSalaryMonth.getValue())+"',"+
						"'"+(chkDepartmentAll.booleanValue()?"%":cmbDepartment.getValue()==null?"%":cmbDepartment.getValue())+"'," +
						"'"+(chkSectionAll.booleanValue()?"%":cmbSection.getValue()==null?"%":cmbSection.getValue())+"',"+
					    " '%') a " +
						" inner join tbEmpOfficialPersonalInfo b on a.vEmployeeID=b.vEmployeeId and b.bStatus=0 "+
						" and vEmployeeStatus like '"+(chkSeparationTypeAll.booleanValue()?"%":cmbSeparationType.getValue()==null?"%":cmbSeparationType.getValue())+"' " +
						" order by a.vDepartmentName,a.vSectionName,a.iRank,a.dJoiningDate";
				
				rptName="RptMonthlySalarySeparation.jasper";
			}

			System.out.println("SALARY "+query);

			if(queryValueCheck(query))
			{
				hm.put("sql", query);
				
				Window win = new ReportViewer(hm,"report/account/hrmModule/"+rptName,
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
			this.getParent().showNotification("Error "+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private boolean queryValueCheck(String sql)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
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
		allComp.add(cmbSection);
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
		setWidth("500px");
		setHeight("270px");

		// lblSalaryMonth
		lblSalaryMonth = new Label("Month :");
		lblSalaryMonth.setImmediate(false);
		lblSalaryMonth.setWidth("100.0%");
		lblSalaryMonth.setHeight("-1px");
		mainLayout.addComponent(lblSalaryMonth,"top:10.0px; left:30.0px;");

		// dSalaryMonth
		dSalaryMonth = new ComboBox();
		dSalaryMonth.setImmediate(true);
		dSalaryMonth.setWidth("140px");
		dSalaryMonth.setHeight("-1px");
		mainLayout.addComponent(dSalaryMonth, "top:08.0px; left:130.0px;");
		
		
		//lblSeparationType
		lblSeparationType=new Label("Separation Type : ");
		mainLayout.addComponent(lblSeparationType, "top:40.0px;left:30.0px;");

		//cmbSeparationType
		cmbSeparationType=new ComboBox();
		cmbSeparationType.setImmediate(true);
		cmbSeparationType.setWidth("250.0px");
		cmbSeparationType.setHeight("-1px");
		mainLayout.addComponent(cmbSeparationType, "top:38.0px;left:130.0px;");
		
		chkSeparationTypeAll = new CheckBox("All");
		chkSeparationTypeAll.setImmediate(true);
		chkSeparationTypeAll.setWidth("-1px");
		chkSeparationTypeAll.setHeight("-1px");
		mainLayout.addComponent(chkSeparationTypeAll, "top:40.0px;left:390.0px;");
		
		cmbUnit=new ComboBox();
		cmbUnit.setWidth("250.0px");
		cmbUnit.setHeight("-1px");
		cmbUnit.setImmediate(true);
		mainLayout.addComponent(new Label("Project : "), "top:70.0px;left:30.0px;");
		mainLayout.addComponent(cmbUnit, "top:68.0px;left:130.0px");

		// chkUnitAll
		chkUnitAll = new CheckBox("All");
		chkUnitAll.setImmediate(true);
		chkUnitAll.setWidth("-1px");
		chkUnitAll.setHeight("-1px");
		mainLayout.addComponent(chkUnitAll, "top:70.0px;left:390.0px;");
		
		mainLayout.addComponent(new Label("Department :"),"top:100.0px; left:30.0px;");

		// cmbDepartment
		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("250.0px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbDepartment, "top:98.0px; left:130.0px;");

		// chkEmployeeAll
		chkDepartmentAll = new CheckBox("All");
		chkDepartmentAll.setImmediate(true);
		chkDepartmentAll.setWidth("-1px");
		chkDepartmentAll.setHeight("-1px");
		mainLayout.addComponent(chkDepartmentAll, "top:100.0px;left:390.0px;");
		

		// lblSection
		lblSection = new Label("Section :");
		lblSection.setImmediate(false);
		lblSection.setWidth("100.0%");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection,"top:130px; left:30.0px;");

		// cmbSection
		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("250.0px");
		cmbSection.setHeight("-1px");
		cmbSection.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbSection, "top:128px; left:130.0px;");

		// chkEmployeeAll
		chkSectionAll = new CheckBox("All");
		chkSectionAll.setImmediate(true);
		chkSectionAll.setWidth("-1px");
		chkSectionAll.setHeight("-1px");
		mainLayout.addComponent(chkSectionAll, "top:130px;left:390.0px;");


		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		//mainLayout.addComponent(RadioBtnGroup, "top:130.0px;left:130.0px;");

		//mainLayout.addComponent(new Label("_________________________________________________________________________________________"), "top:150.0px;right:20.0px;left:20.0px;");		
		mainLayout.addComponent(cButton,"bottom:15px; left:140.0px");

		return mainLayout;
	}
}