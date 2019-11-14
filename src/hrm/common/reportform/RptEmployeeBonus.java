package hrm.common.reportform;

import java.io.File;
import java.io.IOException;
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
import com.common.share.SalaryExcelReport;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class RptEmployeeBonus extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	ArrayList<Component> allComp = new ArrayList<Component>();

	private InlineDateField dYear;
	private Label lblOccasion;
	private ComboBox cmbOccasion;
	private Label lblUnit;
	private ComboBox cmbUnit;
	private CheckBox chkUnitAll,chkDepartmentAll;
	private Label lblSection;
	private ComboBox cmbSection,cmbDepartment;
	private CheckBox chkSectionAll;
	private ComboBox cmbEmployeeType;
	private CheckBox chkEmployeeTypeAll;

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other","Excel"});
	private SimpleDateFormat dFormat=new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dYearFormat=new SimpleDateFormat("yyyy");


	TextField txtPath=new TextField();
	
	private CommonMethod cm;
	private String menuId = "";
	public RptEmployeeBonus(SessionBean sessionBean,String menuId) 
	{
		this.sessionBean=sessionBean;
		this.setCaption("Festival Bonus :: "+sessionBean.getCompany());
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		buildMainLayout();
		setContent(mainLayout);
		cmbUnitData();
		//addOccasionName();
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
	public void setEventAction()
	{
		dYear.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbUnit.removeAllItems();
				cmbUnitData();
			}
		});

		cmbUnit.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				
				if(cmbUnit.getValue()!=null)
				{
					addDepartmentName();
				}
			}
		});
		cmbDepartment.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				
				if(cmbDepartment.getValue()!=null)
				{
					addSectionName();
				}
			}
		});

		chkDepartmentAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				
				if(cmbUnit.getValue()!=null)
				{
					if(chkDepartmentAll.booleanValue())
					{
						cmbDepartment.setEnabled(false);
						cmbDepartment.setValue(null);
						addSectionName();
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
		cmbSection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				
				if(cmbSection.getValue()!=null)
				{
					cmbEmployeeTypeDataAdd();
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
						cmbSection.setEnabled(false);
						cmbSection.setValue(null);
						cmbEmployeeTypeDataAdd();
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
		cmbEmployeeType.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {

				if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
				{
					if(cmbEmployeeType.getValue()!=null)
					{
						addOccasionName();
					}
				}
			
			}
		});
		chkEmployeeTypeAll.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
				{
					if(chkEmployeeTypeAll.booleanValue())
					{
						cmbEmployeeType.setEnabled(false);
						cmbEmployeeType.setValue(null);
						addOccasionName();
					}
					else
					{
						cmbEmployeeType.setEnabled(true);
					}
				}
				else
				{
					chkEmployeeTypeAll.setValue(false);
				}
			}
		});

		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbUnit.getValue()!=null || chkUnitAll.booleanValue())
				{
					if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
					{
						if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
						{
							if(cmbEmployeeType.getValue()!=null || chkEmployeeTypeAll.booleanValue())
							{
								if(cmbOccasion.getValue()!=null)
								{
									reportShow();
								}
								else
								{
									showNotification("Warning","Select Occasion",Notification.TYPE_WARNING_MESSAGE);
								}
							}
							else
							{
								showNotification("Warning","Select Employee Type",Notification.TYPE_WARNING_MESSAGE);
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
					showNotification("Warning","Select Project",Notification.TYPE_WARNING_MESSAGE);
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

	public void cmbUnitData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> list = session.createSQLQuery(" SELECT distinct vUnitId,vUnitName from " +
					"tbEmployeeBonus where YEAR(dBonusDate)=YEAR('"+dFormat.format(dYear.getValue())+"') " +
					"order by vUnitName").list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbUnit.addItem(element[0]);
				cmbUnit.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbUnitData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void addDepartmentName()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql=" SELECT distinct vDepartmentID,vDepartmentName from tbEmployeeBonus " +
					"where YEAR(dBonusDate)=YEAR('"+dFormat.format(dYear.getValue())+"') " +
					"and vUnitId like '"+(cmbUnit.getValue()==null?"%":cmbUnit.getValue())+"' " +
					"order by vDepartmentName";
			List <?> list = session.createSQLQuery(sql).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbDepartment.addItem(element[0]);
				cmbDepartment.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("addDepartmentName",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	public void addSectionName()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql=" SELECT distinct vSectionID,vSectionName from tbEmployeeBonus " +
					" where YEAR(dBonusDate)=YEAR('"+dFormat.format(dYear.getValue())+"') " +
					" and vUnitId like '"+(cmbUnit.getValue()!=null?cmbUnit.getValue().toString():"%")+"' " +
					" and vDepartmentId like '"+(chkDepartmentAll.booleanValue()?"%":cmbDepartment.getValue()==null?"%":cmbDepartment.getValue())+"' " +
					" order by vSectionName";
			List <?> list = session.createSQLQuery(sql).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbSection.addItem(element[0]);
				cmbSection.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("addSectionName",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void cmbEmployeeTypeDataAdd()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vEmployeeType,vEmployeeType from tbEmpOfficialPersonalInfo " +
					"where vUnitId='"+cmbUnit.getValue()+"' " +
					" and vDepartmentId like '"+(chkDepartmentAll.booleanValue()?"%":cmbDepartment.getValue()==null?"%":cmbDepartment.getValue())+"' "+
					" and vSectionId like '"+(chkSectionAll.booleanValue()?"%":cmbSection.getValue()==null?"%":cmbSection.getValue())+"' ";
			List <?> lst=session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				for(Iterator <?> itr=lst.iterator();itr.hasNext();)
				{
					Object [] element = (Object[]) itr.next();
					cmbEmployeeType.addItem(element[0]);
					cmbEmployeeType.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch (Exception exp)
		{
			showNotification("cmbEmployeeTypeDataAdd", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	public void addOccasionName()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql=" SELECT distinct vOccasion,vOccasion from tbEmployeeBonus " +
					" where YEAR(dBonusDate)=YEAR('"+dFormat.format(dYear.getValue())+"') " +
					" and vUnitId like '"+(cmbUnit.getValue()!=null?cmbUnit.getValue().toString():"%")+"' " +
					" and vDepartmentId like '"+(chkDepartmentAll.booleanValue()?"%":cmbDepartment.getValue()==null?"%":cmbDepartment.getValue())+"' "+
					" and vSectionId like '"+(chkSectionAll.booleanValue()?"%":cmbSection.getValue()==null?"%":cmbSection.getValue())+"' "+
					" and vEmployeeType like '"+(chkEmployeeTypeAll.booleanValue()?"%":cmbEmployeeType.getValue()==null?"%":cmbEmployeeType.getValue())+"'";
			List <?> list = session.createSQLQuery(sql).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbOccasion.addItem(element[0]);
				cmbOccasion.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("addOccasionName",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void reportShow()
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		
		String query="select a.dGenerateDate,a.dBonusDate,a.vEmployeeID,a.vEmployeeCode,a.vProximityID,a.vEmployeeName,a.vDesignationID,a.vDesignationName," +
				"a.vUnitId,a.vUnitName,a.vSectionID,a.vSectionName,a.mGrossSalary,a.dJoiningDate,(lengthOfService/365) losYear,(lengthOfService%365)/30 losMonth," +
				"(lengthOfService%365)%30 losDay,lengthOfService,mBonusAmt,vOccasion,a.vUserName,a.vUserIP,a.dEntryTime,a.vDepartmentId,a.vDepartmentName " +
				"from tbEmployeeBonus a " +
				"inner join tbDesignationInfo b on a.vDesignationID=b.vDesignationId " +
				"where YEAR(dBonusDate)=YEAR('"+dFormat.format(dYear.getValue())+"') " +
				"and vUnitId like '"+(cmbUnit.getValue()!=null?cmbUnit.getValue().toString():"%")+"' "+
				"and vDepartmentId like '"+(chkDepartmentAll.booleanValue()?"%":cmbDepartment.getValue()==null?"%":cmbDepartment.getValue())+"' " +
				"and vSectionID like '"+(chkSectionAll.booleanValue()?"%":cmbSection.getValue()==null?"%":cmbSection.getValue())+"' " +
				"and a.vEmployeeType like '"+(chkEmployeeTypeAll.booleanValue()?"%":cmbEmployeeType.getValue()==null?"%":cmbEmployeeType.getValue())+"' "+
				"and vOccasion='"+cmbOccasion.getValue().toString()+"' " +
				"order by a.vDepartmentName,a.vSectionName,b.iDesignationSerial,a.dJoiningDate";
		
		/*query=" select dGenerateDate,dBonusDate,vEmployeeID,vEmployeeCode,vProximityID,vEmployeeName,vDesignationID," +
				"vDesignationName,vUnitID,vUnitName,vSectionID,vSectionName,mGrossSalary,dJoiningDate," +
				"(lengthOfService/365) losYear,(lengthOfService%365)/30 losMonth,(lengthOfService%365)%30 losDay," +
				"lengthOfService,mBonusAmt,vOccasion,vUserName,vUserIP,dEntryTime from tbEmployeeBonus where " +
				"YEAR(dBonusDate)=YEAR('"+dFormat.format(dYear.getValue())+"') and vUnitID like '"+Dept+"' " +
				"and vSectionID like '"+Section+"' and vOccasion='"+cmbOccasion.getValue().toString()+"' order " +
				"by vUnitName,vSectionName,vEmployeeCode";*/
		
		System.out.println("reportShow: "+query);
		
		if(queryValueCheck(query))
		{
			try
			{
				if(RadioBtnGroup.getValue()=="Excel")
				{
					String loc = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/temp/attendanceFolder";

					String fname = "Employee_Bonus.xls";
					String url = getWindow().getApplication().getURL()+"VAADIN/themes/temp/attendanceFolder/"+fname;

					File inFile;
					String header[]=new String[3];
					header[0]="For : "+cmbOccasion.getValue().toString();
					header[1]="Year : "+dYearFormat.format(dYear.getValue());
					header[2]="Unit Name : "+cmbUnit.getItemCaption(cmbUnit.getValue());
					String reportName = "BONUS REPORT";
					String detailQuery[]=new String[1];
					String GroupQuery[]=new String[1];
					String [] signatureOption = new String [0];
					int rowWidth=0;
					inFile=new File("D://Tomcat 7.0/webapps/report/TechniPlex/hrmReportExl/EmployeeBonusReport.xls");
					
					detailQuery[0]="";
					
					rowWidth=13;
					new SalaryExcelReport(sessionBean, loc, url, fname, header, inFile, "EMPLOYEE_BONUS", 
							reportName, 2, GroupQuery, 2, detailQuery, rowWidth,9,signatureOption);

					Window window = new Window();
					getApplication().addWindow(window);
					getWindow().open(new ExternalResource(url),"_blank",500,200,Window.BORDER_NONE);
				}
				else
				{
					HashMap <String,Object> hm = new HashMap <String,Object> ();
					hm.put("company", sessionBean.getCompany());
					hm.put("address", sessionBean.getCompanyAddress());
					hm.put("phone", sessionBean.getCompanyContact());
					hm.put("username", sessionBean.getUserName()+" "+sessionBean.getUserIp());
					hm.put("SysDate",reportTime.getTime);
					hm.put("logo",sessionBean.getCompanyLogo());
					hm.put("sql", query);
					hm.put("bonusYear", dYearFormat.format(dYear.getValue()));

					Window win = new ReportViewer(hm,"report/account/hrmModule/rptEmployeeBonus.jasper",
							this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
							this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
							this.getWindow().getApplication().getURL()+"VAADIN/applet",RadioBtn.Radio);

					win.setCaption("Project Report");
					this.getParent().getWindow().addWindow(win);
				}
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			catch(Exception exp)
			{
				showNotification("reportView "+exp,Notification.TYPE_ERROR_MESSAGE);
			}
		}
		else
		{
			showNotification("Warning","There are no Data",Notification.TYPE_WARNING_MESSAGE);
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

	private void focusMove()
	{
		allComp.add(cmbOccasion);
		allComp.add(cmbSection);
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
		setWidth("480px");
		setHeight("290px");

		dYear = new InlineDateField();
		dYear.setResolution(InlineDateField.RESOLUTION_YEAR);
		dYear.setDateFormat("yyyy");
		dYear.setValue(new java.util.Date());
		dYear.setImmediate(true);
		mainLayout.addComponent(new Label("Year : "), "top:30.0px; left:30.0px;");
		mainLayout.addComponent(dYear, "top:28.0px; left:150.0px;");

		// lblCategory
		lblUnit = new Label();
		lblUnit.setImmediate(false);
		lblUnit.setWidth("100.0%");
		lblUnit.setHeight("-1px");
		lblUnit.setValue("Project :");
		mainLayout.addComponent(lblUnit,"top:60.0px; left:30.0px;");

		// cmbSection
		cmbUnit = new ComboBox();
		cmbUnit.setImmediate(false);
		cmbUnit.setWidth("250px");
		cmbUnit.setHeight("-1px");
		cmbUnit.setNullSelectionAllowed(true);
		cmbUnit.setImmediate(true);
		mainLayout.addComponent(cmbUnit, "top:58.0px; left:150.0px;");
		
		chkUnitAll = new CheckBox("All");
		chkUnitAll.setImmediate(true);
		chkUnitAll.setWidth("-1px");
		chkUnitAll.setHeight("-1px");
		//mainLayout.addComponent(chkUnitAll, "top:60.0px;left:400.0px;");
		
		mainLayout.addComponent(new Label("Department :"),"top:90px; left:30.0px;");

		// cmbDepartment
		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(false);
		cmbDepartment.setWidth("250px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setNullSelectionAllowed(true);
		cmbDepartment.setImmediate(true);
		mainLayout.addComponent(cmbDepartment, "top:88px; left:150.0px;");
		
		chkDepartmentAll  = new CheckBox("All");
		chkDepartmentAll.setImmediate(true);
		chkDepartmentAll.setWidth("-1px");
		chkDepartmentAll.setHeight("-1px");
		mainLayout.addComponent(chkDepartmentAll, "top:90.0px;left:400.0px;");
		
		// lblCategory
		lblSection = new Label();
		lblSection.setImmediate(false);
		lblSection.setWidth("100.0%");
		lblSection.setHeight("-1px");
		lblSection.setValue("Section :");
		mainLayout.addComponent(lblSection,"top:120px; left:30.0px;");

		// cmbSection
		cmbSection = new ComboBox();
		cmbSection.setImmediate(false);
		cmbSection.setWidth("250px");
		cmbSection.setHeight("-1px");
		cmbSection.setNullSelectionAllowed(true);
		cmbSection.setImmediate(true);
		mainLayout.addComponent(cmbSection, "top:118px; left:150.0px;");
		
		chkSectionAll  = new CheckBox("All");
		chkSectionAll.setImmediate(true);
		chkSectionAll.setWidth("-1px");
		chkSectionAll.setHeight("-1px");
		mainLayout.addComponent(chkSectionAll, "top:120px;left:400.0px;");
		
		cmbEmployeeType= new ComboBox();
		cmbEmployeeType.setInvalidAllowed(false);
		cmbEmployeeType.setWidth("250px");
		cmbEmployeeType.setHeight("-1px");
		cmbEmployeeType.setImmediate(true);
		mainLayout.addComponent(new Label("Employee Type : "), "top:150px; left:30.0px;");
		mainLayout.addComponent(cmbEmployeeType, "top:148px;left:150.0px;");

		// chkEmployeeTypeAll
		chkEmployeeTypeAll = new CheckBox("All");
		chkEmployeeTypeAll.setImmediate(true);
		chkEmployeeTypeAll.setWidth("-1px");
		chkEmployeeTypeAll.setHeight("-1px");
		mainLayout.addComponent(chkEmployeeTypeAll, "top:150px;left:400.0px;");

		// lblCategory
		lblOccasion = new Label();
		lblOccasion.setImmediate(false);
		lblOccasion.setWidth("100.0%");
		lblOccasion.setHeight("-1px");
		lblOccasion.setValue("Occasion Name :");
		mainLayout.addComponent(lblOccasion,"top:180px; left:30.0px;");

		cmbOccasion = new ComboBox();
		cmbOccasion.setImmediate(false);
		cmbOccasion.setWidth("250px");
		cmbOccasion.setHeight("-1px");
		cmbOccasion.setNullSelectionAllowed(true);
		cmbOccasion.setImmediate(true);
		mainLayout.addComponent(cmbOccasion, "top:178px; left:150.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:180.0px;left:150.0px;");
		RadioBtnGroup.setVisible(false);
		
		mainLayout.addComponent(cButton,"bottom:15px; left:165.0px");
		return mainLayout;
	}
}
