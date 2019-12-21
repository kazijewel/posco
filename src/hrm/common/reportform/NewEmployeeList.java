package hrm.common.reportform;

import java.io.File;
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
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;
@SuppressWarnings("serial")
public class NewEmployeeList extends Window {

	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;
	private Label lblDivision;
	private Label lblDate;
	private Label lblEmpType;
	private ComboBox cmbEmpType;
	private PopupDateField dDate;	
	private CheckBox chkDepartmentAll = new CheckBox("All");
	private CheckBox chkUnitAll = new CheckBox("All");
	private CheckBox chkSectionAll = new CheckBox("All");
	private CheckBox chkEmployeeTypeAll = new CheckBox("All");
	private ComboBox cmbDepartment,cmbUnit;
	private ComboBox cmbSection;
	private SimpleDateFormat dFormat1 = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dMonthFormat = new SimpleDateFormat("MMMMM-yyyy");
	ArrayList<Component> allComp = new ArrayList<Component>();
	private CommonMethod cm;
	private String menuId = "";
	private SimpleDateFormat dFormat2=new SimpleDateFormat("");
	private PopupDateField ToDate;
	private SimpleDateFormat dMonthFormat2 =new SimpleDateFormat("yyyy-MM-dd");
	ArrayList<Component> allComp2 = new ArrayList<Component>();
	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"/*,"Excel"*/});
	private static final List<String> typeofReport=Arrays.asList(new String[]{"With Gross","Without Gross"});
	private OptionGroup opgTypeOfReport;
	public NewEmployeeList(SessionBean sessionBean,String menuId)
	{
		this.sessionBean=sessionBean;
		this.setCaption("NEW EMPLOYEE LIST :: "+sessionBean.getCompany());
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		buildMainLayout();
		setContent(mainLayout);
		cmbUnitDataLoad();
		setEventAction();
		focusMove();
		cmbDepartment.setEnabled(false);
		cmbSection.setEnabled(false);
		cmbEmpType.setEnabled(false);
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
			String sql = "select distinct vUnitId,vUnitName from tbEmpOfficialPersonalInfo where bStatus=1 and dJoiningDate between '"+dFormat.format(dDate.getValue())+"' and '"+dFormat.format(ToDate.getValue())+ "' order by vUnitName";
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
			String sql = "select distinct vDepartmentId,vDepartmentName from tbEmpOfficialPersonalInfo where bStatus=1 and vUnitId like '"+unitId+"' and dJoiningDate between '"+dFormat.format(dDate.getValue())+"' and '"+dFormat.format(ToDate.getValue())+ "' order by vDepartmentName";
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
			String sql="select distinct vSectionId,vSectionName from tbEmpOfficialPersonalInfo where bStatus=1 and vUnitId like '"+unitId+"' and vDepartmentId like '"+deptId+"' and dJoiningDate between '"+dFormat.format(dDate.getValue())+"' and '"+dFormat.format(ToDate.getValue())+ "'"+
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
	private void cmbEmpTypeData()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		String unitId="%",deptId="%",secId="%";
		if(!chkUnitAll.booleanValue())
		{
			unitId=cmbUnit.getValue().toString();
		}
		if(!chkDepartmentAll.booleanValue())
		{
			deptId=cmbDepartment.getValue().toString();
		}
		if(!chkSectionAll.booleanValue())
		{
			secId=cmbSection.getValue().toString();
		}
		try
		{
			String sql=" select Distinct 0,vEmployeeType from dbo.tbEmpOfficialPersonalInfo " +
					" where bStatus=1 and vUnitId like '"+unitId+"' and vDepartmentId like '"+deptId+"'  " +
					"and vSectionId like '"+secId+"' and " +
					" dJoiningDate between '"+dFormat.format(dDate.getValue())+"' and '"+dFormat.format(ToDate.getValue())+ "' order by vEmployeeType";

			List <?> lst=session.createSQLQuery(sql).list();

			if(!lst.isEmpty())
			{
				Iterator <?> itr=lst.iterator();
				while(itr.hasNext())
				{
					Object[] element=(Object[])itr.next();
					cmbEmpType.addItem(element[1]);
					cmbEmpType.setItemCaption(element[1], element[1].toString());
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbEmpTypeData", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void setEventAction()
	{

		dDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				chkUnitAll.setValue(false);
				cmbUnit.removeAllItems();
				if(dDate.getValue()!=null)
				{
					cmbUnitDataLoad();
				}
			}
		});

		ToDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				chkUnitAll.setValue(false);
				cmbUnit.removeAllItems();
				if(ToDate.getValue()!=null)
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
				if(dDate.getValue()!=null && ToDate.getValue()!=null)
				{
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
				cmbEmpType.removeAllItems();
				chkEmployeeTypeAll.setValue(false);
				cmbEmpType.setEnabled(false);
				if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
				{
					if(cmbSection.getValue()!=null)
					{
						chkSectionAll.setValue(false);
						cmbEmpTypeData();
						cmbEmpType.setEnabled(true);
					}
				}
			}
		});

		chkSectionAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				cmbEmpType.removeAllItems();
				chkEmployeeTypeAll.setValue(false);
				cmbEmpType.setEnabled(false);
				if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
				{
					if(chkSectionAll.booleanValue())
					{
						cmbSection.setValue(null);
						cmbSection.setEnabled(false);
						cmbEmpType.setEnabled(true);
						cmbEmpTypeData();
					}
					else
					{
						cmbSection.setEnabled(true);
						cmbEmpType.setEnabled(false);
					}
				}
				else{
					chkSectionAll.setValue(false);
					showNotification("Warning..","Select Department Please!",Notification.TYPE_HUMANIZED_MESSAGE);
				}
			}
		});
		chkEmployeeTypeAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				
				if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
				{
					if(chkEmployeeTypeAll.booleanValue())
					{	
						cmbEmpType.setValue(null);
						cmbEmpType.setEnabled(false);
					}
					else
					{
						cmbEmpType.setEnabled(true);
					}
				}
				else
				{
					chkEmployeeTypeAll.setValue(false);
					showNotification("Warning..","Select Section Please!",Notification.TYPE_HUMANIZED_MESSAGE);
				}
			}
		});

		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(dDate.getValue()!=null)
				{
					if(cmbUnit.getValue()!=null || chkUnitAll.booleanValue())
					{
						if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
						{
							if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
							{
								if(cmbEmpType.getValue()!=null || chkEmployeeTypeAll.booleanValue())
								{
									reportShow();
								}
								else
								{
									showNotification("Select Employee Type",Notification.TYPE_WARNING_MESSAGE);
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
				else
				{
					showNotification("Select Date",Notification.TYPE_WARNING_MESSAGE);
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
		String unitId="%",deptId="%",secId="%",empType="%";
		if(!chkUnitAll.booleanValue())
		{
			unitId=cmbUnit.getValue().toString();
		}
		if(!chkDepartmentAll.booleanValue())
		{
			deptId=cmbDepartment.getValue().toString();
		}
		if(!chkSectionAll.booleanValue())
		{
			secId=cmbSection.getValue().toString();
		}
		if(!chkEmployeeTypeAll.booleanValue())
		{
			empType=cmbEmpType.getValue().toString();
		}

		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		String query=null,Rpt="";

		try
		{	

			query="select a.vEmployeeId,a.vEmployeeCode,a.vFingerId,a.vEmployeeName,a.vSectionName,"+
					" a.vEmployeeType,ISNULL(ss.mBasic,0)mBasic,ISNULL(ss.mHouseRent,0)mHouseRent,ISNULL(ss.mMobileAllowance,0)mMobileAllowance,a.vSectionId,a.vUnitId,a.vUnitName,vDesignationId,vDesignationName, "+
					" a.vDepartmentId,a.vDepartmentName,dJoiningDate from tbEmpOfficialpersonalInfo a "+
					" inner join tbEmpSalaryStructure ss on ss.vEmployeeId=a.vEmployeeId "+
					" where a.bStatus=1 and a.vUnitId like '"+unitId+"' and a.vDepartmentId like '"+deptId+"' and a.vSectionId like '"+secId+"' and a.vEmployeeType like '"+empType+"' "+
					" and a.dJoiningDate between '"+sessionBean.dfDb.format(dDate.getValue())+"' and '"+sessionBean.dfDb.format(ToDate.getValue())+"' "+
					" order by a.vUnitName,a.vDepartmentName,substring(a.vEmployeeCode,3,10)";			
			
			System.out.println(query);
			
			if(opgTypeOfReport.getValue().toString().equalsIgnoreCase("With Gross"))
			{
				Rpt="report/account/hrmModule/rptNewEmployeeListWithGross.jasper";
			}
			else
			{
				Rpt="report/account/hrmModule/rptNewEmployeeListWithoutGross.jasper";
			}
			if(queryValueCheck(query))
			{
				HashMap <String, Object> hm = new HashMap <String, Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("username", sessionBean.getUserName()+" "+sessionBean.getUserIp());
				hm.put("SysDate",reportTime.getTime);
				hm.put("dDate","From "+dFormat1.format(dDate.getValue())+" To "+dFormat1.format(ToDate.getValue()));
				hm.put("developer", "Developed by: E-Vision Software Ltd. ||  Mob:01755-506044 || www.eslctg.com");
				hm.put("sql", query);


				Window win = new ReportViewer(hm,Rpt,
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
			System.out.println(exp);
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
		allComp.add(dDate);
		allComp.add(cmbDepartment);
		allComp.add(cmbSection);
		allComp.add(cmbEmpType);
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
		setHeight("300px");

		// lblDate
		lblDate = new Label("Date :");
		lblDate.setImmediate(false);
		lblDate.setWidth("100.0%");
		lblDate.setHeight("-1px");
		mainLayout.addComponent(lblDate,"top:10.0px; left:30px;");

		// dDate
		dDate = new PopupDateField();
		dDate.setImmediate(true);
		dDate.setHeight("-1px");
		dDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dDate.setDateFormat("dd-MM-yyyy");
		dDate.setValue(new java.util.Date());
		mainLayout.addComponent(dDate, "top:08.0px; left:140px;");

		ToDate = new PopupDateField();
		ToDate.setImmediate(true);
		ToDate.setHeight("-1px");
		ToDate.setResolution(PopupDateField.RESOLUTION_DAY);
		ToDate.setDateFormat("dd-MM-yyyy");
		ToDate.setValue(new java.util.Date());
		mainLayout.addComponent(ToDate, "top:08.0px;left:260px;");

		// cmbSection
		cmbUnit=new ComboBox();
		cmbUnit.setImmediate(true);
		cmbUnit.setNullSelectionAllowed(false);
		cmbUnit.setWidth("300px");
		mainLayout.addComponent(new Label("Project : "), "top:40px; left:30.0px;");
		mainLayout.addComponent(cmbUnit, "top:38px; left:140.0px;");
		
		chkUnitAll.setImmediate(true);
		mainLayout.addComponent(chkUnitAll,"top:40px; left:445px;");
		chkUnitAll.setVisible(false);
		
		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("300px");
		mainLayout.addComponent(new Label("Department : "), "top:70px; left:30.0px;");
		mainLayout.addComponent(cmbDepartment, "top:68px; left:140.0px;");

		chkDepartmentAll = new CheckBox("All");
		chkDepartmentAll.setImmediate(true);
		mainLayout.addComponent(chkDepartmentAll,"top:70px; left:445px;");

		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("300px");
		mainLayout.addComponent(new Label("Section : "), "top:100px; left:30.0px;");
		mainLayout.addComponent(cmbSection, "top:98px; left:140.0px;");

		chkSectionAll = new CheckBox("All");
		chkSectionAll.setImmediate(true);
		mainLayout.addComponent(chkSectionAll,"top:100px; left:445px;");

		//lblEmpType
		lblEmpType=new Label("Employee Type : ");
		mainLayout.addComponent(lblEmpType, "top:130px;left:30px;");

		//cmbEmpType
		cmbEmpType=new ComboBox();
		cmbEmpType.setImmediate(true);
		cmbEmpType.setWidth("150px");
		cmbEmpType.setHeight("-1px");
		mainLayout.addComponent(cmbEmpType, "top:128px;left:140px;");

		chkEmployeeTypeAll = new CheckBox("All");
		chkEmployeeTypeAll.setImmediate(true);
		chkEmployeeTypeAll.setWidth("-1px");
		chkEmployeeTypeAll.setHeight("-1px");
		mainLayout.addComponent(chkEmployeeTypeAll,"top:130px; left:295px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:160px;left:140px;");
		RadioBtnGroup.setVisible(false);
		
		opgTypeOfReport = new OptionGroup("",typeofReport);
		opgTypeOfReport.setImmediate(true);
		opgTypeOfReport.setStyleName("horizontal");
		opgTypeOfReport.setValue("With Gross");
		mainLayout.addComponent(opgTypeOfReport, "top:160px;left:140px;");
	

		//mainLayout.addComponent(new Label("_________________________________________________________________________________________"), "top:150.0px;right:20.0px;left:20.0px;");		
		mainLayout.addComponent(cButton,"bottom:15px; left:140px");
		return mainLayout;
	}
}