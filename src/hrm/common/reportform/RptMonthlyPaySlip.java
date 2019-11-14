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
public class RptMonthlyPaySlip extends Window
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblUnit;
	private Label lblSection;
	private Label lblSalaryMonth;

	private ComboBox cmbUnit;
	private ComboBox cmbSection,cmbDepartment;
	private ComboBox cmbSalaryMonth;
	private ComboBox cmbEmployeeName;

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
	public RptMonthlyPaySlip(SessionBean sessionBean,String menuId)
	{
		this.sessionBean=sessionBean;
		this.setCaption("MONTHLY PAY SLIP :: "+sessionBean.getCompany());
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		buildMainLayout();
		setContent(mainLayout);
		salaryDateLoad();
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
				cmbSalaryMonth.addItem(element[0]);
				cmbSalaryMonth.setItemCaption(element[0],element[1]+"-"+element[2]);
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
	private void cmbUnitDataLoad() {
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "select distinct  vUnitId,vUnitName from tbMonthlySalary order by vUnitName";
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
			showNotification(exp+"PLease Select Unit Name",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	public void cmbDepartmentAddData()
	{
		cmbDepartment.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct  vDepartmentID,vDepartmentName from tbMonthlySalary where "+
					" YEAR(dSalaryDate)=YEAR('"+dFormat.format(cmbSalaryMonth.getValue())+"') " +
					" and MONTH(dSalaryDate)=MONTH('"+dFormat.format(cmbSalaryMonth.getValue())+"') "
					+ " and vUnitId='"+cmbUnit.getValue().toString()+"' order by vDepartmentName";
		
			List <?> list=session.createSQLQuery(query).list();
		
			for(Iterator <?> iter=list.iterator();iter.hasNext();){

				Object[] element = (Object[]) iter.next();
				cmbDepartment.addItem(element[0]);
				cmbDepartment.setItemCaption(element[0], (String) element[1]);
			}
			System.out.println("Department Data: "+query);
		}
		catch(Exception exp){
			showNotification("cmbDepartment",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	
	public void cmbSectionAddData()
	{
		cmbSection.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct  vSectionID,vSectionName from tbMonthlySalary where "+
					" YEAR(dSalaryDate)=YEAR('"+dFormat.format(cmbSalaryMonth.getValue())+"') " +
					" and MONTH(dSalaryDate)=MONTH('"+dFormat.format(cmbSalaryMonth.getValue())+"') "+
					" and vUnitId='"+cmbUnit.getValue().toString()+"' "+
					" and vDepartmentId like '"+(chkDepartmentAll.booleanValue()?"%":cmbDepartment.getValue()==null?"%":cmbDepartment.getValue())+"' "+
					" order by vSectionName";
		
			List <?> list=session.createSQLQuery(query).list();
		
			for(Iterator <?> iter=list.iterator();iter.hasNext();){

				Object[] element = (Object[]) iter.next();
				cmbSection.addItem(element[0]);
				cmbSection.setItemCaption(element[0], (String) element[1]);
			}
			System.out.println("section Data: "+query);
		}
		catch(Exception exp){
			showNotification("cmbSectionAddData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void employeeSetData()
	{
		cmbEmployeeName.removeAllItems();
		
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select vEmployeeId,vEmployeeCode,vEmployeeName,vProximityId from tbMonthlySalary " +
					" where vUnitId='"+cmbUnit.getValue().toString()+"' "+
					" and vDepartmentId like '"+(chkDepartmentAll.booleanValue()?"%":cmbDepartment.getValue()==null?"%":cmbDepartment.getValue())+"' "+
					" and vSectionId like '"+(chkSectionAll.booleanValue()?"%":cmbSection.getValue()==null?"%":cmbSection.getValue())+"' "+
					" and YEAR(dSalaryDate)=YEAR('"+dFormat.format(cmbSalaryMonth.getValue())+"') " +
					" and MONTH(dSalaryDate)=MONTH('"+dFormat.format(cmbSalaryMonth.getValue())+"') " +
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

	public void setEventAction()
	{
		cmbSalaryMonth.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbUnit.removeAllItems();
				if(cmbSalaryMonth.getValue()!=null)
				{
					
					cmbUnitDataLoad();
				}
			}
		});
		
		cmbUnit.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbDepartment.removeAllItems();				
				chkDepartmentAll.setValue(false);
				cmbDepartment.setEnabled(true);
				if(cmbUnit.getValue()!=null)
				{
					cmbDepartmentAddData();
				}
			}
		});
		cmbDepartment.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSection.removeAllItems();
				chkSectionAll.setValue(false);
				cmbSection.setEnabled(true);
				if(cmbUnit.getValue()!=null)
				{
					if(cmbDepartment.getValue()!=null)
					{
						cmbSectionAddData();
					}
				}
			}
		});
		chkDepartmentAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSection.removeAllItems();
				chkSectionAll.setValue(false);
				cmbSection.setEnabled(true);
				if(cmbUnit.getValue()!=null)
				{
					if(chkDepartmentAll.booleanValue())
					{
						cmbDepartment.setValue(null);
						cmbDepartment.setEnabled(false);
						cmbSectionAddData();
					}
					else
						cmbDepartment.setEnabled(true);
					
				}
				else
					chkDepartmentAll.setValue(false);
			}
		});		

		cmbSection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{

				cmbEmployeeName.removeAllItems();
				chkEmployeeName.setValue(false);
				cmbEmployeeName.setEnabled(true);
				if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
				{
					if(cmbSection.getValue()!=null)
					{
						cmbEmployeeName.removeAllItems();
						employeeSetData();
					}
				}
			}
		});		
		chkSectionAll.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				cmbEmployeeName.removeAllItems();
				chkEmployeeName.setValue(false);
				cmbEmployeeName.setEnabled(true);
				if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
				{
					if(chkSectionAll.booleanValue())
					{
						cmbSection.setEnabled(false);
						cmbSection.setValue(null);
						employeeSetData();
					}
					else
					{
						cmbSection.setEnabled(true);
					}
				}
				else
					chkSectionAll.setValue(false);
			}
		});
		chkEmployeeName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
				{
					if(chkEmployeeName.booleanValue())
					{
						cmbEmployeeName.setValue(null);
						cmbEmployeeName.setEnabled(false);
					}
					else
						cmbEmployeeName.setEnabled(true);
				}
				else
				{
					chkEmployeeName.setValue(false);
				}
			}
		});
		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbUnit.getValue()!=null) 
				{
					if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue()) 
					{
						if(cmbSection.getValue()!=null || chkSectionAll.booleanValue()) 
						{
							if(cmbEmployeeName.getValue()!=null || chkEmployeeName.booleanValue()) 
							{
								reportShow();
							}
							else
							{
								showNotification("Warning","Select Employee",Notification.TYPE_WARNING_MESSAGE);
							}
						}
						else
						{
							showNotification("Warning","Select Section",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else
					{
						showNotification("Warning","Select Department",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					showNotification("Warning","Select Project Name",Notification.TYPE_WARNING_MESSAGE);
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
			
			String query="select * from funPaySlip ('"+cmbUnit.getValue().toString()+"', "
					+ " '"+cmbSalaryMonth.getValue()+"','"+(chkDepartmentAll.booleanValue()?"%":cmbDepartment.getValue()==null?"%":cmbDepartment.getValue())+"',"
					+ " '"+(chkSectionAll.booleanValue()?"%":cmbSection.getValue()==null?"%":cmbSection.getValue())+"',"
					+ " '"+(chkEmployeeName.booleanValue()?"%":cmbEmployeeName.getValue()==null?"%":cmbEmployeeName.getValue())+"') " +
					"order by vSectionName,iRank,dJoiningDate";
			System.out.println("reportShow: "+query);
			

			if(queryValueCheck(query))
			{
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
				hm.put("section",cmbSection.getItemCaption(cmbSection.getValue()));
				hm.put("month",fMonth.format(cmbSalaryMonth.getValue()));
				hm.put("year",fYear.format(cmbSalaryMonth.getValue()));
				hm.put("SysDate",reportTime.getTime);
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/account/hrmModule/rptpayslip.jasper",
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
		allComp.add(cmbSalaryMonth);
		allComp.add(cmbDepartment);
		allComp.add(cmbSection);
		allComp.add(cmbEmployeeName);
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

		/*	RadioBtnGroup1 = new OptionGroup("",type2);
		RadioBtnGroup1.setImmediate(true);
		RadioBtnGroup1.setStyleName("horizontal");
		RadioBtnGroup1.setValue("PDF");
		RadioBtnGroup1.select("English");
		mainLayout.addComponent(RadioBtnGroup1, "top:20.0px;left:130.0px;");*/

		// lblSalaryMonth
		lblSalaryMonth = new Label("Month :");
		lblSalaryMonth.setImmediate(false);
		lblSalaryMonth.setWidth("100.0%");
		lblSalaryMonth.setHeight("-1px");
		mainLayout.addComponent(lblSalaryMonth,"top:50.0px; left:30.0px;");

		// cmbSalaryMonth
		cmbSalaryMonth = new ComboBox();
		cmbSalaryMonth.setImmediate(true);
		cmbSalaryMonth.setWidth("160px");
		cmbSalaryMonth.setHeight("-1px");
		cmbSalaryMonth.setNullSelectionAllowed(true);
		cmbSalaryMonth.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbSalaryMonth, "top:48.0px; left:130.0px;");

		lblUnit = new Label("Project :");
		lblUnit.setImmediate(false);
		lblUnit.setHeight("-1px");
		mainLayout.addComponent(lblUnit,"top:80.0px; left:30.0px;");

		// cmbSection
		cmbUnit = new ComboBox();
		cmbUnit.setImmediate(true);
		cmbUnit.setWidth("260px");
		cmbUnit.setHeight("-1px");
		cmbUnit.setNullSelectionAllowed(true);
		cmbUnit.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbUnit, "top:78.0px; left:130.0px;");
		
		mainLayout.addComponent(new Label("Department :"),"top:110.0px; left:30.0px;");

		// cmbSection
		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("260px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setNullSelectionAllowed(true);
		cmbDepartment.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbDepartment, "top:108.0px; left:130.0px;");

		chkDepartmentAll = new CheckBox("All");
		chkDepartmentAll.setHeight("-1px");
		chkDepartmentAll.setWidth("-1px");
		chkDepartmentAll.setImmediate(true);
		mainLayout.addComponent(chkDepartmentAll, "top:110.0px; left:395.0px;");

		// lblSection
		lblSection = new Label("Section Name :");
		lblSection.setImmediate(false);
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection,"top:140px; left:30.0px;");

		// cmbSection
		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("260px");
		cmbSection.setHeight("-1px");
		cmbSection.setNullSelectionAllowed(true);
		cmbSection.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbSection, "top:138px; left:130.0px;");

		chkSectionAll = new CheckBox("All");
		chkSectionAll.setHeight("-1px");
		chkSectionAll.setWidth("-1px");
		chkSectionAll.setImmediate(true);
		mainLayout.addComponent(chkSectionAll, "top:140px; left:395.0px;");

		// cmbEmployeeName
		cmbEmployeeName = new ComboBox();
		cmbEmployeeName.setImmediate(true);
		cmbEmployeeName.setWidth("260px");
		cmbEmployeeName.setHeight("-1px");
		cmbEmployeeName.setNullSelectionAllowed(true);
		cmbEmployeeName.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Employee ID: "),"top:170px; left:30.0px;");
		mainLayout.addComponent(cmbEmployeeName, "top:168px; left:130.0px;");

		chkEmployeeName = new CheckBox("All");
		chkEmployeeName.setImmediate(true);
		chkEmployeeName.setHeight("-1px");
		chkEmployeeName.setWidth("-1px");
		mainLayout.addComponent(chkEmployeeName, "top:170px; left:395.0px;");

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