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
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class RptAppointmentLetter extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblSection;
	private Label lblEmployeeName;
	private Label lblAsOnDate;

	private ComboBox cmbUnit;
	private ComboBox cmbSection,cmbDepartment;
	private CheckBox chkDepartmentAll;
	private CheckBox chkSectionAll;
	private CheckBox chkUnitAll;

	private ComboBox cmbEmployeeName;
	private PopupDateField asOnDate;
	
	ArrayList<Component> allComp = new ArrayList<Component>();

	private OptionGroup opgEmployee;
	private List<?> lstEmployee = Arrays.asList(new String[]{"Employee ID","Employee Name"});


	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});
	private OptionGroup RadioBtnStatus;
	private static final List<String> status=Arrays.asList(new String[]{"Active","InActive","All"});
	private OptionGroup RadioBtnGroup2;
	private static final List<String> type2=Arrays.asList(new String[]{"Application Form","Appointment Letter"});

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();
	private CommonMethod cm;
	private String menuId = "";
	public RptAppointmentLetter(SessionBean sessionBean,String menuId)
	{
		this.sessionBean=sessionBean;
		this.setCaption("APPLICATION & APPOINTMENT LETTER :: "+sessionBean.getCompany());
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		buildMainLayout();
		setContent(mainLayout);

		cmbUnitDataLoad();
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
	private void cmbUnitDataLoad() {
		cmbUnit.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "select distinct vUnitId,vUnitName from tbEmpOfficialPersonalInfo order by vUnitName";
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
			String sql = "select distinct vDepartmentId,vDepartmentName from tbEmpOfficialPersonalInfo where vUnitId like '"+unitId+"' order by vDepartmentName";
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
			String sql="select distinct vSectionId,vSectionName from tbEmpOfficialPersonalInfo where vUnitId like '"+unitId+"' and vDepartmentId like '"+deptId+"' order by vSectionName";
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
	public void setEventAction()
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
				cmbEmployeeName.removeAllItems();
				cmbEmployeeName.setEnabled(false);
				if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
				{
					if(cmbSection.getValue()!=null)
					{
						chkSectionAll.setValue(false);
						EmployeeDataAdd();
						cmbEmployeeName.setEnabled(true);
					}
				}
			}
		});

		chkSectionAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				cmbEmployeeName.removeAllItems();
				cmbEmployeeName.setEnabled(false);
				if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
				{
					if(chkSectionAll.booleanValue())
					{
						cmbSection.setValue(null);
						cmbSection.setEnabled(false);
						cmbEmployeeName.setEnabled(true);
						EmployeeDataAdd();
					}
					else
					{
						cmbSection.setEnabled(true);
						cmbEmployeeName.setEnabled(false);
					}
				}
				else{
					chkSectionAll.setValue(false);
					showNotification("Warning..","Select Department Please!",Notification.TYPE_HUMANIZED_MESSAGE);
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
								if(cmbEmployeeName.getValue()!=null)
								{
									if(asOnDate.getValue()!=null)
									{
										reportShow();
									}
									else
									{
										showNotification("Warning","Select Date",Notification.TYPE_WARNING_MESSAGE);
									}
								}
								else
								{
									showNotification("Warning","Select Employee Name",Notification.TYPE_WARNING_MESSAGE);
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

		cButton.btnExit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});
		RadioBtnStatus.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmployeeName.removeAllItems();
				if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
				{
					EmployeeDataAdd();
				}
			}
		});

		opgEmployee.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				cmbEmployeeName.removeAllItems();
				EmployeeDataAdd();
			}
		});
	}
	private void EmployeeDataAdd()
	{
		String status="%";
		if(RadioBtnStatus.getValue().equals("Active"))
		{
			status="1";
		}
		else if(RadioBtnStatus.getValue().equals("InActive"))
		{
			status="0";
		}
		else
		{
			status="%";
		}
		cmbEmployeeName.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vEmployeeId,vemployeeCode from tbEmpOfficialPersonalInfo where vUnitId like '"+(cmbUnit.getValue()==null?"%":cmbUnit.getValue().toString())+"' and  vSectionId like '"+(cmbSection.getValue()==null?"%":cmbSection.getValue().toString())+"' and vDepartmentId like '"+(cmbDepartment.getValue()==null?"%":cmbDepartment.getValue().toString())+"' and bStatus like '"+status+"' ";
			lblEmployeeName.setValue("Employee ID :");

			if(opgEmployee.getValue()=="Employee Name")
			{
				query="select distinct vEmployeeId,vEmployeeName from tbEmpOfficialPersonalInfo where vUnitId like '"+(cmbUnit.getValue()==null?"%":cmbUnit.getValue().toString())+"' and  vSectionId like '"+(cmbSection.getValue()==null?"%":cmbSection.getValue().toString())+"' and vDepartmentId like '"+(cmbDepartment.getValue()==null?"%":cmbDepartment.getValue().toString())+"' and bStatus like '"+status+"' ";
				
				lblEmployeeName.setValue("Employee Name :");
			}
			
			if(opgEmployee.getValue()=="Employee Name")
			{
				query+=" Order by vEmployeeName";
			}
			else
			{
				query+=" Order by vemployeeCode";
			}
			
			List <?> lst=session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				Iterator <?> itr=lst.iterator();
				while(itr.hasNext())
				{
					Object [] element=(Object[])itr.next();
					cmbEmployeeName.addItem(element[0]);
					cmbEmployeeName.setItemCaption(element[0], element[1].toString());
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

	private void reportShow()
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		String query=null;
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
			String ReportValue="";
			if(RadioBtnGroup2.getValue().equals("Appointment Letter"))
			{
				query = "select epo.vEmployeeId,epo.vEmployeeCode,epo.vEmployeeName,epo.vUnitId,epo.vUnitName,epo.vSectionId,epo.vSectionName,"+
						" epo.dJoiningDate,edi.vDesignationId,edi.vDesignation,CONVERT(date,GETDATE())dDate from tbEmpOfficialPersonalInfo epo "+ 
						" inner join tbEmpSectionInfo esi on esi.vEmployeeId=epo.vEmployeeId "+
						" inner join tbEmpDesignationInfo edi on epo.vEmployeeId=edi.vEmployeeId "+
						" where epo.vUnitId like '"+unitId+"' and epo.vDepartmentId like '"+deptId+"' and epo.vSectionId like '"+secId+"' and epo.vEmployeeId like '"+(cmbEmployeeName.getValue()==null?"%":cmbEmployeeName.getValue().toString())+"' "+
						" order by epo.vEmployeeCode ";

				ReportValue="report/account/hrmModule/rptAppointmentLetter.jasper";
			}
			else
			{
				query = "select epo.vEmployeeId,epo.vEmployeeCode,epo.vEmployeeName,epo.vUnitId,epo.vUnitName,epo.vSectionId,epo.vSectionName,"+
						" epo.dJoiningDate,edi.vDesignationId,edi.vDesignation,CONVERT(date,GETDATE())dDate,epo.vFatherName,epo.vMotherName,epo.vPresentAddress,epo.vPermanentAddress,epo.vEmailAddress,"+
						" epo.dDateOfBirth,epo.vNationality,epo.vReligion,epo.vContactNo,epo.vBloodGroup from tbEmpOfficialPersonalInfo epo "+ 
						" inner join tbEmpSectionInfo esi on esi.vEmployeeId=epo.vEmployeeId "+
						" inner join tbEmpDesignationInfo edi on epo.vEmployeeId=edi.vEmployeeId "+
						" where epo.vUnitId like '"+unitId+"' and epo.vDepartmentId like '"+deptId+"' and epo.vSectionId like '"+secId+"' and epo.vEmployeeId like '"+(cmbEmployeeName.getValue()==null?"%":cmbEmployeeName.getValue().toString())+"' "+
						" order by epo.vEmployeeCode ";

				ReportValue="report/account/hrmModule/rptApplicationForm.jasper";
			}
			if(queryValueCheck(query))
			{
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("UserName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
				hm.put("SysDate",reportTime.getTime);
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("dDate", asOnDate.getValue());
				hm.put("sql", query);

				Window win = new ReportViewer(hm,ReportValue,
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
			showNotification("reportShow "+exp,Notification.TYPE_ERROR_MESSAGE);
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
		allComp.add(cmbUnit);
		allComp.add(cmbSection);
		allComp.add(cmbEmployeeName);
		allComp.add(asOnDate);
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
		setHeight("380px");

		lblAsOnDate = new Label("Date :");
		lblAsOnDate.setImmediate(true);
		lblAsOnDate.setWidth("100%");
		lblAsOnDate.setHeight("-1px");
		mainLayout.addComponent(lblAsOnDate, "top:30.0px; left:30.0px;");

		// asOnDate
		asOnDate = new PopupDateField();
		asOnDate.setWidth("110px");
		asOnDate.setDateFormat("dd-MM-yyyy");
		asOnDate.setValue(new java.util.Date());
		asOnDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(asOnDate, "top:28.0px; left:150.0px;");

		// cmbSection
		cmbUnit = new ComboBox();
		cmbUnit.setImmediate(false);
		cmbUnit.setWidth("200px");
		cmbUnit.setHeight("-1px");
		cmbUnit.setNullSelectionAllowed(true);
		cmbUnit.setImmediate(true);
		mainLayout.addComponent(new Label("Project Name : "), "top:60.0px; left:30.0px;");
		mainLayout.addComponent(cmbUnit, "top:58.0px; left:150.0px;");
		
		chkUnitAll = new CheckBox("All");
		chkUnitAll.setImmediate(true);
		mainLayout.addComponent(chkUnitAll,"top:60px; left:353px;");
		chkUnitAll.setVisible(false);
		
		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("200px");
		mainLayout.addComponent(new Label("Department : "), "top:90px; left:30.0px;");
		mainLayout.addComponent(cmbDepartment, "top:88px; left:150px;");

		chkDepartmentAll = new CheckBox("All");
		chkDepartmentAll.setImmediate(true);
		mainLayout.addComponent(chkDepartmentAll,"top:90px; left:353px;");

		// lblSection
		lblSection = new Label("Section :");
		lblSection.setImmediate(false);
		lblSection.setWidth("100.0%");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection,"top:120px; left:30.0px;");

		// cmbSection
		cmbSection = new ComboBox();
		cmbSection.setImmediate(false);
		cmbSection.setWidth("200px");
		cmbSection.setHeight("-1px");
		cmbSection.setNullSelectionAllowed(true);
		cmbSection.setImmediate(true);
		mainLayout.addComponent(cmbSection, "top:118px; left:150.0px;");
		
		chkSectionAll = new CheckBox("All");
		chkSectionAll.setImmediate(true);
		mainLayout.addComponent(chkSectionAll,"top:120px; left:353px;");
		
		RadioBtnStatus = new OptionGroup("",status);
		RadioBtnStatus.setImmediate(true);
		RadioBtnStatus.setStyleName("horizontal");
		RadioBtnStatus.setValue("Active");
		mainLayout.addComponent(RadioBtnStatus, "top:150px;left:50.0px;");

		opgEmployee=new OptionGroup("",lstEmployee);
		opgEmployee.select("Employee ID");
		opgEmployee.setImmediate(true);
		opgEmployee.setStyleName("horizontal");
		mainLayout.addComponent(opgEmployee, "top:180px; left:50.0px;");

		// lblEmployeeName
		lblEmployeeName = new Label("Employee ID :");
		lblEmployeeName.setImmediate(false);
		lblEmployeeName.setWidth("100.0%");
		lblEmployeeName.setHeight("-1px");
		mainLayout.addComponent(lblEmployeeName,"top:210px; left:30.0px;");

		// cmbEmployeeName
		cmbEmployeeName = new ComboBox();
		cmbEmployeeName.setImmediate(false);
		cmbEmployeeName.setWidth("200px");
		cmbEmployeeName.setHeight("-1px");
		cmbEmployeeName.setNullSelectionAllowed(true);
		cmbEmployeeName.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		cmbEmployeeName.setImmediate(true);
		mainLayout.addComponent(cmbEmployeeName, "top:208px; left:150.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:210.0px;left:150.0px;");
		RadioBtnGroup.setVisible(false);

		RadioBtnGroup2 = new OptionGroup("",type2);
		RadioBtnGroup2.setImmediate(true);
		RadioBtnGroup2.setStyleName("horizontal");
		RadioBtnGroup2.setValue("Application Form");
		mainLayout.addComponent(RadioBtnGroup2, "top:270px;left:150.0px;");
		mainLayout.addComponent(cButton,"bottom:15px; left:130.0px");
		return mainLayout;
	}
}
