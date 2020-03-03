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
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class RptLeaveApplication extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;
	private CommonMethod cm;
	private String menuId = "";
	private ComboBox cmbSection,cmbAppDate;
	private ComboBox cmbDepartment,cmbUnit;
	private CheckBox chkDepartmentAll;
	private CheckBox chkSectionAll;
	private ComboBox cmbEmployee;
	private CheckBox chkAllEmp;
	private CheckBox chkAllUnit;
	private Label lblDate,lblAppDate;
	private PopupDateField dDate;
	private OptionGroup opgEmployee;
	//private static final List<String> Optiontype=Arrays.asList(new String[]{"Employee ID"/*,"Finger/Proximity ID"*/,"Employee Name"});
	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();
	private OptionGroup RadioBtnGroup;
	private SimpleDateFormat dbFormat=new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dYearFormat=new SimpleDateFormat("yyyy-MM-dd");
	public RptLeaveApplication(SessionBean sessionBean,String menuId) 
	{
		this.sessionBean=sessionBean;
		this.setCaption("LEAVE APPLICATION FORM :: "+sessionBean.getCompany());
		this.setWidth("500px");
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
			String sql = "select distinct epo.vUnitId,epo.vUnitName from tbEmpLeaveApplicationInfo lai " +
					" inner join tbEmpOfficialPersonalInfo epo on epo.vEmployeeId=lai.vEmployeeId order by epo.vUnitName";
			List<?> list = session.createSQLQuery(sql).list();
			System.out.println(sql);
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
				chkDepartmentAll.setValue(false);
				cmbDepartment.setEnabled(false);
				if(cmbUnit.getValue()!=null)
				{
					cmbDepartment.setEnabled(true);
					cmbDepartmentData();
				}
			}
		});
		chkAllUnit.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				cmbDepartment.removeAllItems();
				chkDepartmentAll.setValue(false);
				cmbDepartment.setEnabled(false);
				if(chkAllUnit.booleanValue())
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
				if(cmbUnit.getValue()!=null || chkAllUnit.booleanValue())
				{
					if(cmbDepartment.getValue()!=null)
					{
						chkDepartmentAll.setValue(false);
						cmbSection.setEnabled(true);
						cmbSectionData();
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
				if(cmbUnit.getValue()!=null || chkAllUnit.booleanValue())
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
				else
				{
					chkDepartmentAll.setValue(false);
					showNotification("Warning..","Select Unit Please!",Notification.TYPE_HUMANIZED_MESSAGE);
				}

			}
		});

		cmbSection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmployee.removeAllItems();
				chkAllEmp.setValue(false);
				cmbEmployee.setEnabled(false);
				if(cmbUnit.getValue()!=null)
				{
					if(cmbSection.getValue()!=null)
					{
						chkSectionAll.setValue(false);
						addEmployeeName();
						cmbEmployee.setEnabled(true);
					}
					else
					{
						cmbEmployee.setEnabled(false);
					}
				}
			}
		});

		chkSectionAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				cmbEmployee.removeAllItems();
				chkAllEmp.setValue(false);
				cmbEmployee.setEnabled(false);
				if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
				{
					if(chkSectionAll.booleanValue())
					{
						cmbSection.setValue(null);
						cmbSection.setEnabled(false);
						cmbEmployee.setEnabled(true);
						addEmployeeName();
					}
					else
					{
						cmbSection.setEnabled(true);
						cmbEmployee.setEnabled(false);
					}
				}
				else{
					chkSectionAll.setValue(false);
					showNotification("Warning..","Select Department Please!",Notification.TYPE_HUMANIZED_MESSAGE);
				}
			}
		});

		opgEmployee.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmployee.removeAllItems();
				if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
				{
					addEmployeeName();
				}
			}
		});
		chkAllEmp.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				cmbAppDate.removeAllItems();
				if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
				{
					if(chkAllEmp.booleanValue())
					{
						cmbEmployee.setValue(null);
						cmbEmployee.setEnabled(false);
						cmbAppDateAdd();
					}
					else
					{
						cmbEmployee.setEnabled(true);
					}
				}
				else{
					chkAllEmp.setValue(false);
					showNotification("Warning..","Select Section Please!",Notification.TYPE_HUMANIZED_MESSAGE);
				}
			}
		});
		cmbEmployee.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				cmbAppDate.removeAllItems();
				if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
				{
					if(cmbEmployee.getValue()!=null)
					{
						chkAllEmp.setValue(false);
						cmbAppDateAdd();
					}
				}
			}
		});
		cButton.btnPreview.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{if(cmbUnit.getValue()!=null || chkAllUnit.booleanValue())
			{
				if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
				{

					if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
					{
						if(cmbEmployee.getValue()!=null || chkAllEmp.booleanValue())
						{
							if(cmbAppDate.getValue()!=null)
							{
								reportpreview();
							}
							else
							{
								showNotification("Warning","Select application date!",Notification.TYPE_WARNING_MESSAGE);
								cmbAppDate.focus();
							}
						}
						else
						{
							showNotification("Warning","Select Employee",Notification.TYPE_WARNING_MESSAGE);
							cmbEmployee.focus();
						}
					}
					else
					{
						showNotification("Warning","Select Section",Notification.TYPE_WARNING_MESSAGE);
						cmbSection.focus();
					}

				}
				else
				{
					showNotification("Warning","Select Department",Notification.TYPE_WARNING_MESSAGE);
					cmbDepartment.focus();
				}
			}
			else
			{
				showNotification("Warning","Select Unit",Notification.TYPE_WARNING_MESSAGE);
				cmbSection.focus();
			}}
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
		setWidth("500px");
		setHeight("270px");

		cmbUnit=new ComboBox();
		cmbUnit.setImmediate(true);
		cmbUnit.setNullSelectionAllowed(false);
		cmbUnit.setWidth("250px");
		mainLayout.addComponent(new Label("Project : "), "top:10px; left:30.0px;");
		mainLayout.addComponent(cmbUnit, "top:08px; left:140.0px;");

		chkAllUnit = new CheckBox("All");
		chkAllUnit.setImmediate(true);
		mainLayout.addComponent(chkAllUnit,"top:10px; left:395px;");

		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("250px");
		cmbDepartment.setNullSelectionAllowed(false);
		mainLayout.addComponent(new Label("Department : "), "top:40px; left:30.0px;");
		mainLayout.addComponent(cmbDepartment, "top:38px; left:140.0px;");

		chkDepartmentAll = new CheckBox("All");
		chkDepartmentAll.setImmediate(true);
		mainLayout.addComponent(chkDepartmentAll,"top:40px; left:395px;");

		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("250px");
		cmbSection.setNullSelectionAllowed(false);
		mainLayout.addComponent(new Label("Section : "), "top:70px; left:30.0px;");
		mainLayout.addComponent(cmbSection, "top:68px; left:140.0px;");

		chkSectionAll = new CheckBox("All");
		chkSectionAll.setImmediate(true);
		mainLayout.addComponent(chkSectionAll,"top:70px; left:395px;");

		opgEmployee=new OptionGroup("");
		opgEmployee.setImmediate(true);
		opgEmployee.setValue("Employee ID");
		opgEmployee.setStyleName("horizontal");
		mainLayout.addComponent(opgEmployee, "top:100px;left:140px;");
		opgEmployee.setVisible(false);

		cmbEmployee=new ComboBox();
		cmbEmployee.setWidth("250.0px");
		cmbEmployee.setHeight("-1px");
		cmbEmployee.setImmediate(true);
		cmbEmployee.setNullSelectionAllowed(false);
		cmbEmployee.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Employee : "), "top:100px;left:30px;");
		mainLayout.addComponent(cmbEmployee, "top:98px;left:140px;");

		chkAllEmp=new CheckBox("All");
		chkAllEmp.setImmediate(true);
		//mainLayout.addComponent(chkAllEmp, "top:130px;left:395px");

		// optionGroup
		RadioBtnGroup = new OptionGroup("");
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:160px;left:170.0px;");
		RadioBtnGroup.setVisible(false);
		// lblAppDate
		lblAppDate = new Label("Application Date :");
		lblAppDate.setImmediate(false);
		lblAppDate.setWidth("100.0%");
		lblAppDate.setHeight("-1px");
		mainLayout.addComponent(lblAppDate,"top:130px; left:30.0px;");

		// cmbAppDate
		cmbAppDate = new ComboBox();
		cmbAppDate.setImmediate(true);
		cmbAppDate.setWidth("115px");
		cmbAppDate.setHeight("-1px");
		cmbAppDate.setNullSelectionAllowed(false);
		mainLayout.addComponent(cmbAppDate, "top:128px; left:140px;");

		mainLayout.addComponent(cButton,"bottom:15px; left:140px");
		return mainLayout;
	}

	private void cmbDepartmentData()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		String unitId="%";
		if(cmbUnit.getValue()!=null)
		{
			unitId=cmbUnit.getValue().toString();
		}
		try
		{
			String sql="select distinct epo.vDepartmentId,epo.vDepartmentName from " +
					" tbEmpLeaveApplicationInfo lai inner join tbEmpOfficialPersonalInfo epo on epo.vEmployeeId=lai.vEmployeeId " +
					" where epo.vUnitId like '"+unitId+"' order by epo.vDepartmentName";
			System.out.println(sql);
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
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		String deptId="%",unitId="%";
		if(cmbUnit.getValue()!=null)
		{
			unitId=cmbUnit.getValue().toString();
		}
		if(cmbDepartment.getValue()!=null)
		{
			deptId=cmbDepartment.getValue().toString();
		}
		try
		{
			String sql="select distinct epo.vSectionId,epo.vSectionName from tbEmpLeaveApplicationInfo lai " +
					" inner join tbEmpOfficialPersonalInfo epo on epo.vEmployeeId=lai.vEmployeeId where epo.vUnitId like '"+unitId+"' " +
					" and epo.vDepartmentId like '"+deptId+"' order by epo.vSectionName";
			List <?> lst=session.createSQLQuery(sql).list();
			System.out.println(sql);
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
			showNotification("CmbSectionDataLoad", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void addEmployeeName()
	{
		String deptId="%",secId="%",unitId="%";
		if(cmbUnit.getValue()!=null)
		{
			unitId=cmbUnit.getValue().toString();
		}
		if(cmbDepartment.getValue()!=null)
		{
			deptId=cmbDepartment.getValue().toString();
		}
		if(cmbSection.getValue()!=null)
		{
			secId=cmbSection.getValue().toString();
		}
		String sql="select epo.vEmployeeId,epo.vEmployeeCode,epo.vEmployeeName from tbEmpLeaveApplicationInfo lai inner join " +
				" tbEmpOfficialPersonalInfo epo on epo.vEmployeeId=lai.vEmployeeId where epo.bStatus=1 and " +
				" epo.vUnitId like '"+unitId+"' and epo.vDepartmentId like '"+deptId+"' and epo.vSectionId like '"+secId+"' order by vEmployeeName ";  

		System.out.println("EMP"+sql);
		
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> lst=session.createSQLQuery(sql).list();
			if(!lst.isEmpty())
			{
				Iterator <?> itr=lst.iterator();
				while(itr.hasNext())
				{
					Object[] element=(Object[])itr.next();
					cmbEmployee.addItem(element[0]);
					cmbEmployee.setItemCaption(element[0], element[1]+">>"+element[2]);
				
				}
			}
			else
				showNotification("Warning", "No Employee Found!!!", Notification.TYPE_WARNING_MESSAGE);
		}
		catch(Exception exp)
		{
			showNotification("addEmployeeName", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}	

	private void cmbAppDateAdd()
	{

		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		String deptId="%",secId="%",unitId="%",empId="%";
		if(cmbUnit.getValue()!=null)
		{
			unitId=cmbUnit.getValue().toString();
		}
		if(cmbDepartment.getValue()!=null)
		{
			deptId=cmbDepartment.getValue().toString();
		}
		if(cmbSection.getValue()!=null)
		{
			secId=cmbSection.getValue().toString();
		}
		if(cmbEmployee.getValue()!=null)
		{
			empId=cmbEmployee.getValue().toString();
		}
		try
		{
			String str = " SELECT  vLeaveId,CONVERT(varchar,eli.dApplicationDate,105) as appDate from tbEmpLeaveApplicationInfo eli "
					+ " inner join tbEmpOfficialPersonalInfo epo on epo.vEmployeeId=eli.vEmployeeId " +
					" Where epo.vUnitId like '"+unitId+"' and epo.vDepartmentId like '"+deptId+"' and " +
					" epo.vSectionId like '"+secId+"' and  epo.vEmployeeId like '"+empId+"'  " +
					" order by eli.dApplicationDate desc ";
			System.out.println(str);
			List <?> list = session.createSQLQuery(str).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbAppDate.addItem(element[0]);
				cmbAppDate.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbAppDateAdd",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void reportpreview()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		String deptId="%",secId="%",unitId="%";
		try
		{
			if(cmbDepartment.getValue()!=null)
			{
				deptId=cmbDepartment.getValue().toString();
			}
			if(cmbSection.getValue()!=null)
			{
				secId=cmbSection.getValue().toString();
			}
			if(cmbUnit.getValue()!=null)
			{
				unitId=cmbUnit.getValue().toString();
			}
			String query = " select * from funLeaveApplicationReport('"+cmbAppDate.getValue()+"','"+cmbEmployee.getValue()+"')";
			System.out.println("reportpreview: "+query);
			
			if(queryValueCheck(query))
			{
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("userName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
				hm.put("path", "report/account/hrmModule/");
				hm.put("SysDate",reportTime.getTime);
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("Unit",sessionBean.getCompany());
				hm.put("developer", sessionBean.getDeveloperAddress());
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/account/hrmModule/rptLeaveApplicationFormPOSCO.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",true);

				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);
			}
			else
			{
				showNotification("There are no data!",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception exp)
		{
			showNotification("reportpreview",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
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
