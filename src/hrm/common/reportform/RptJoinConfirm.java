package hrm.common.reportform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

import com.common.share.AmountField;
import com.common.share.CommonButton;
import com.common.share.CommonMethod;
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
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class RptJoinConfirm extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;
	private CommonMethod cm;
	private String menuId = "";
	private Label lblDate;

	private ComboBox cmbDepartment,cmbUnit;
	private ComboBox cmbSection;


	private CheckBox chkDepartmentAll;
	private CheckBox chkSectionAll;
	private ComboBox cmbEmployeeName;
	private CheckBox chkEmployeeAll;
	private CheckBox chkEmployeeTypeAll;

	private ComboBox cmbEmpType;
	private OptionGroup opgEmployee;
	private ComboBox cmbJobDuration;
	private PopupDateField dAsOnDate;
	private AmountField txtNumberOfYear;
	ArrayList<Component> allComp = new ArrayList<Component>();
	private CheckBox chkUnitAll = new CheckBox("All");

	private static final List<String> reportType = Arrays.asList(new String[] {"Joining Date", "Confirmation Date"});
	private static final List<String> employee = Arrays.asList(new String[]{"Employee ID"/*,"Finger/Proximity ID"*/,"Employee Name"});
	private static final List<String> typeReport=Arrays.asList(new String[]{"PDF","Other","Excel"});
	private static final List<String> typeActivity=Arrays.asList(new String[]{"Active","Inactive","All"});

	private ReportDate reportTime;
	private OptionGroup opgReportType;
	private OptionGroup opgJoinConfirm;
	private OptionGroup opgActivity;

	Label lblTimeName=new Label();

	String employeeType="";
	String emplyeSection="";
	String employeeValue="";
	String frameName="";
	private String stAIctive = "1";
	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");

	public RptJoinConfirm(SessionBean sessionBean,String menuId)
	{
		this.sessionBean=sessionBean;
		this.setCaption("JOB DURATION :: "+sessionBean.getCompany());
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		buildMainLayout();
		setContent(mainLayout);
		setEventAction();
		cmbUnitDataLoad();
		cmbDepartment.setEnabled(false);
		cmbSection.setEnabled(false);
		cmbEmpType.setEnabled(false);
		cmbEmployeeName.setEnabled(false);
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
					" where vUnitId like '"+unitId+"' and vDepartmentId like '"+deptId+"'  " +
					"and vSectionId like '"+secId+"' order by vEmployeeType";

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
	private void cmbEmployeeNameDataAdd()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
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
		try
		{
			String query= " select vEmployeeId,vFingerId,vEmployeeName,vEmployeeCode,vProximityId from tbEmpOfficialPersonalInfo " +
					" where vUnitId like '"+unitId+"' and vDepartmentId like '"+deptId+"' and vSectionId like '"+secId+"' and " +
					" vEmployeeType like '"+empType+"' ";
			if(opgEmployee.getValue().toString().equals("Employee ID"))
			{
				query += "order by vEmployeeCode";
				List <?> lst=session.createSQLQuery(query).list();

				if(!lst.isEmpty())
				{
					for(Iterator <?> itr=lst.iterator();itr.hasNext();)
					{
						Object [] element=(Object[])itr.next();
						cmbEmployeeName.addItem(element[0]);
						cmbEmployeeName.setItemCaption(element[0], element[3].toString());
					}
				}
			}

			/*	if(opgEmployee.getValue().toString().equals("Finger/Proximity ID"))
			{
				List <?> lst=session.createSQLQuery(query).list();
				if(!lst.isEmpty())
				{
					for(Iterator <?> itr=lst.iterator();itr.hasNext();)
					{
						Object [] element=(Object[])itr.next();
						cmbEmployeeName.addItem(element[0]);
						cmbEmployeeName.setItemCaption(element[0], element[1].toString()+"/"+element[4].toString());
					}
				}
			}*/

			if(opgEmployee.getValue().toString().equals("Employee Name"))
			{
				query += "order by vEmployeeName";
				List <?> lst=session.createSQLQuery(query).list();

				if(!lst.isEmpty())
				{
					for(Iterator <?> itr=lst.iterator();itr.hasNext();)
					{
						Object [] element=(Object[])itr.next();
						cmbEmployeeName.addItem(element[0]);
						cmbEmployeeName.setItemCaption(element[0], element[2].toString());
					}
				}
			}
			System.out.println(query);
		}
		catch (Exception exp)
		{
			showNotification("cmbEmployeeNameDataAdd", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	public void setEventAction()
	{
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
							if(cmbEmpType.getValue()!=null || chkEmployeeTypeAll.booleanValue())
							{
								if(cmbEmployeeName.getValue()!=null || chkEmployeeAll.booleanValue())
								{
									if(cmbJobDuration.getValue()!=null && !txtNumberOfYear.getValue().toString().isEmpty())
									{
										reportShow();
									}
									else
									{
										showNotification("Warning","Select Job Duration and Number of Years",Notification.TYPE_WARNING_MESSAGE);
										cmbJobDuration.focus();
									}
								}
								else
								{
									showNotification("Warning","Select Employee Name",Notification.TYPE_WARNING_MESSAGE);
									cmbEmployeeName.focus();
								}
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
		});

		cButton.btnExit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
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
				cmbEmployeeName.removeAllItems();
				chkEmployeeAll.setValue(false);
				cmbEmployeeName.setEnabled(false);
				if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
				{
					if(chkEmployeeTypeAll.booleanValue())
					{	
						cmbEmpType.setValue(null);
						cmbEmpType.setEnabled(false);
						cmbEmployeeName.setEnabled(true);
						cmbEmployeeNameDataAdd();
					}
					else
					{
						cmbEmpType.setEnabled(true);
						cmbEmployeeName.setEnabled(false);
					}
				}
				else
				{
					chkEmployeeTypeAll.setValue(false);
					showNotification("Warning..","Select Section Please!",Notification.TYPE_HUMANIZED_MESSAGE);
				}
			}
		});
		cmbEmpType.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmployeeName.removeAllItems();
				chkEmployeeAll.setValue(false);
				cmbEmployeeName.setEnabled(false);
				if(cmbEmpType.getValue()!=null || chkEmployeeTypeAll.booleanValue())
				{
					if(cmbEmpType.getValue()!=null)
					{
						chkEmployeeTypeAll.setValue(false);
						cmbEmpTypeData();
						cmbEmployeeName.setEnabled(true);
						cmbEmployeeNameDataAdd();
					}
				}
			}
		});


		opgEmployee.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmployeeName.removeAllItems();
				chkEmployeeAll.setValue(false);
				cmbEmployeeName.setEnabled(false);
				if(cmbEmpType.getValue()!=null || chkEmployeeTypeAll.booleanValue())
				{
					if(opgEmployee.getValue()!=null)
					{
						cmbEmployeeNameDataAdd();
						cmbEmployeeName.setEnabled(true);
					}
				}
			}
		});

		chkEmployeeAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(cmbEmpType.getValue()!=null || chkEmployeeTypeAll.booleanValue())
				{
					employeeAction();
				}
				else{
					chkEmployeeAll.setValue(false);
					showNotification("Warning..","Select Employee Type Please!",Notification.TYPE_HUMANIZED_MESSAGE);
				}
			}
		});
		cmbJobDuration.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(cmbJobDuration.getValue()!=null)
				{
					txtNumberOfYear.focus();
				}
			}
		});
		opgActivity.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(event.getProperty().toString()=="Active")
				{
					stAIctive = "1";
				}
				else if(event.getProperty().toString()=="Inactive")
				{
					stAIctive = "0";
				}
				else
				{
					stAIctive = "%";
				}
			}
		});
	}

	private void employeeAction()
	{
		if(chkEmployeeAll.booleanValue()==true)
		{
			cmbEmployeeName.setValue(null);
			cmbEmployeeName.setEnabled(false);
		}
		else
		{
			cmbEmployeeName.setEnabled(true);
		}
	}
	

	private void reportShow(){

		reportTime = new ReportDate();
		String query=null;
		String report="";
		String limitedType="";
		String deptId="%",secId="%",empId="%",empType="%",unitId="%";
		if(cmbJobDuration.getValue().toString().equals("Equal to"))
		{
			limitedType="=";
		}
		else if(cmbJobDuration.getValue().toString().equals("Not equal to"))
		{
			limitedType="!=";
		}
		else if(cmbJobDuration.getValue().toString().equals("Less then"))
		{
			limitedType="<";
		}
		else if(cmbJobDuration.getValue().toString().equals("Greater then"))
		{
			limitedType=">";
		}
		else if(cmbJobDuration.getValue().toString().equals("Less then or equal to"))
		{
			limitedType="<=";
		}
		else if(cmbJobDuration.getValue().toString().equals("Greater then or equal to"))
		{
			limitedType=">=";
		}

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
		if(!chkEmployeeAll.booleanValue())
		{
			empId=cmbEmployeeName.getValue().toString();
		}
		
		try{

			HashMap <String, Object> hm = new HashMap <String, Object> ();
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("logo", sessionBean.getCompanyLogo());
			//hm.put("developer", sessionBean.getDeveloperAddress());
			hm.put("UserName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			hm.put("SysDate",reportTime.getTime);
			hm.put("jobDuration","Job Duration :"+ cmbJobDuration.getItemCaption(cmbJobDuration.getValue())+" "+txtNumberOfYear.getValue().toString()+" "+lblTimeName.getValue());
			hm.put("activity", "Service Status :"+opgActivity.getValue());
			hm.put("branch", "Project Name : "+cmbUnit.getItemCaption(cmbUnit.getValue().toString()));
			hm.put("developer", "Developed by: E-Vision Software Ltd. ||  Mob:01755-506044 || www.eslctg.com");

			if(opgJoinConfirm.getValue().toString().equals("Joining Date"))
			{
				query  = "select * from funJobDuration('"+unitId+"','"+deptId+"','"+secId+"','"+empType+"','"+empId+"','"+stAIctive+"','"+opgJoinConfirm.getValue().toString()+"','"+sessionBean.dfDb.format(dAsOnDate.getValue())+"') where iYear"+limitedType+""+(txtNumberOfYear.getValue().toString().isEmpty()?"0":txtNumberOfYear.getValue().toString())+" order by vUnitName,vDepartmentName,vSectionName,iRank,dJoiningDate";

				report="report/account/hrmModule/rptJobDurationJoiningDate.jasper";
				System.out.println("JoinD :"+query);	
			}
			else if(opgJoinConfirm.getValue().toString().equals("Confirmation Date"))
			{
				query  = "select * from funJobDuration('"+unitId+"','"+deptId+"','"+secId+"','"+empType+"','"+empId+"','"+stAIctive+"','"+opgJoinConfirm.getValue().toString()+"','"+sessionBean.dfDb.format(dAsOnDate.getValue())+"') where iYear"+limitedType+""+(txtNumberOfYear.getValue().toString().isEmpty()?"0":txtNumberOfYear.getValue().toString())+" order by vUnitName,vDepartmentName,vSectionName,iRank,dJoiningDate";
				report="report/account/hrmModule/rptJobDurationConfirmationDate.jasper";
				System.out.println("ConfirmD :"+query);
			}
			System.out.println("Report Query: "+query);
			if(queryValueCheck(query))
			{
				System.out.println("queryValueCheck(query) : "+query);
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
			this.getParent().showNotification("Error "+exp,Notification.TYPE_ERROR_MESSAGE);
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

	public AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);

		setWidth("500px");
		setHeight("430px");

		cmbUnit=new ComboBox();
		cmbUnit.setImmediate(true);
		cmbUnit.setNullSelectionAllowed(false);
		cmbUnit.setWidth("300px");
		mainLayout.addComponent(new Label("Project : "), "top:20px; left:30.0px;");
		mainLayout.addComponent(cmbUnit, "top:18px; left:140.0px;");
		
		chkUnitAll = new CheckBox("All");
		chkUnitAll.setImmediate(true);
		mainLayout.addComponent(chkUnitAll,"top:20px; left:445px;");
		chkUnitAll.setVisible(false);

		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("300px");
		cmbDepartment.setHeight("-1px");
		mainLayout.addComponent(new Label("Department : "), "top:50px; left:30.0px;");
		mainLayout.addComponent(cmbDepartment, "top:48px; left:140.0px;");

		chkDepartmentAll = new CheckBox("All");
		chkDepartmentAll.setImmediate(true);
		mainLayout.addComponent(chkDepartmentAll,"top:50px; left:445px;");

		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("270px");
		mainLayout.addComponent(new Label("Section : "), "top:80px; left:30.0px;");
		mainLayout.addComponent(cmbSection, "top:78px; left:140.0px;");

		chkSectionAll = new CheckBox("All");
		chkSectionAll.setImmediate(true);
		mainLayout.addComponent(chkSectionAll,"top:80px; left:415px;");

		cmbEmpType = new ComboBox();
		cmbEmpType.setImmediate(true);
		cmbEmpType.setWidth("120px");
		mainLayout.addComponent(new Label("Employee Type : "), "top:110px; left:30.0px;");
		mainLayout.addComponent(cmbEmpType, "top:108px; left:140.0px;");

		chkEmployeeTypeAll = new CheckBox("All");
		chkEmployeeTypeAll.setImmediate(true);
		mainLayout.addComponent(chkEmployeeTypeAll,"top:110px; left:265px;");

		opgEmployee = new OptionGroup("",employee);
		opgEmployee.setImmediate(true);
		opgEmployee.setStyleName("horizontal");
		opgEmployee.setValue("Employee ID");
		mainLayout.addComponent(opgEmployee, "top:140px; left:30px;");

		cmbEmployeeName = new ComboBox();
		cmbEmployeeName.setImmediate(true);
		cmbEmployeeName.setWidth("200px");
		mainLayout.addComponent(new Label("Employee : "), "top:170px; left:30.0px;");
		mainLayout.addComponent(cmbEmployeeName, "top:168px; left:140.0px;");

		chkEmployeeAll = new CheckBox("All");
		chkEmployeeAll.setImmediate(true);
		mainLayout.addComponent(chkEmployeeAll,"top:168px; left:345px;");

		cmbJobDuration=new ComboBox();
		cmbJobDuration.setImmediate(true);
		cmbJobDuration.setWidth("190px");
		mainLayout.addComponent(new Label("Job Duration :"),"top:200px; left:30px");
		mainLayout.addComponent(cmbJobDuration,"top:198px; left:140px");
		cmbJobDuration.addItem("Equal to");
		cmbJobDuration.addItem("Not equal to");
		cmbJobDuration.addItem("Less then");
		cmbJobDuration.addItem("Greater then");
		cmbJobDuration.addItem("Less then or equal to");
		cmbJobDuration.addItem("Greater then or equal to");


		txtNumberOfYear=new AmountField();
		txtNumberOfYear.setWidth("40px");
		txtNumberOfYear.setHeight("21px");
		txtNumberOfYear.setImmediate(true);
		lblTimeName.setValue("years");
		mainLayout.addComponent(txtNumberOfYear,"top:200px; left:332px");
		mainLayout.addComponent(lblTimeName,"top:198px; left:374");

		opgJoinConfirm=new OptionGroup("",reportType);
		opgJoinConfirm.setImmediate(true);
		opgJoinConfirm.setStyleName("horizontal");
		opgJoinConfirm.setValue("Joining Date");
		mainLayout.addComponent(new Label("Based on :"),"top:230px; left:30px");
		mainLayout.addComponent(opgJoinConfirm, "top:228px; left:140.0px;");

		lblDate=new Label("Till as on : ");
		mainLayout.addComponent(lblDate,"top:260px; left:30.0px;");

		dAsOnDate=new PopupDateField();
		dAsOnDate.setImmediate(true);
		dAsOnDate.setWidth("110px");
		dAsOnDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dAsOnDate.setDateFormat("dd-MM-yyyy");
		dAsOnDate.setValue(new java.util.Date());
		mainLayout.addComponent(dAsOnDate, "top:258px; left:140.0px;");

		opgActivity=new OptionGroup("",typeActivity);
		opgActivity.setImmediate(true);
		opgActivity.setStyleName("horizontal");
		opgActivity.setValue("Active");
		mainLayout.addComponent(new Label("Service Status :"),"top:290px; left:30px");
		mainLayout.addComponent(opgActivity, "top:288px; left:140.0px;");

		opgReportType=new OptionGroup("",typeReport);
		opgReportType.setImmediate(true);
		opgReportType.setStyleName("horizontal");
		opgReportType.setValue("PDF");
		mainLayout.addComponent(opgReportType,"top:320px; left:140px");
		opgReportType.setVisible(false);


		mainLayout.addComponent(cButton, "bottom:10px;left:160.0px;");

		return mainLayout;
	}
}
