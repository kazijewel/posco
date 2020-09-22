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
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class RptEmployeeList extends Window 
{
	private ReportDate reportTime;
	private SessionBean sessionBean;
	public AbsoluteLayout mainLayout;
	private CommonMethod cm;
	private String menuId = "";
	private ComboBox cmbEmpType,cmbUnit;	
	private ComboBox cmbSection;
	private ComboBox cmbDepartment;
	private ComboBox cmbDesignation;
	private ComboBox cmbReligion;

	private OptionGroup opgStatus = new OptionGroup();
	private OptionGroup opgReportView;

	private CheckBox chkUnitAll = new CheckBox("All");
	private CheckBox chkEmployeeTypeAll = new CheckBox("All");
	private CheckBox chkSectionAll = new CheckBox("All");
	private CheckBox chkDesignationAll = new CheckBox("All");
	private CheckBox chkReligionAll = new CheckBox("All");
	private CheckBox chkUnit = new CheckBox("All");
	private CheckBox chkDepartmentAll = new CheckBox("All");

	private static final List<String> aictiveType = Arrays.asList(new String[]{"Active","Inactive","All"});
	private static final List<String> reportView = Arrays.asList(new String[]{"PDF","Excel"});
	private static final List<String> EmployeeType = Arrays.asList(new String[]{"Regular", "Temporary"});
	private static final List<String> religion = Arrays.asList(new String[] {"Islam","Hindu","Buddism","Cristian","Other"});

	ArrayList<Component> allComp = new ArrayList<Component>();
	private String stAIctive = "";

	CommonButton cButton = new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	public RptEmployeeList(SessionBean sessionBean,String menuId)
	{
		this.sessionBean = sessionBean;
		this.setCaption("EMPLOYEE LIST :: "+sessionBean.getCompany());
		this.setResizable(false);
		this.menuId = menuId;
		cm = new CommonMethod(sessionBean);
		buildMainLayout();
		setContent(mainLayout);
		cmbUnitDataLoad();
		setEventAction();
		focusMove();
		cmbUnit.focus();
		cmbDepartment.setEnabled(false);
		cmbSection.setEnabled(false);
		cmbEmpType.setEnabled(false);
		cmbDesignation.setEnabled(false);
		cmbReligion.setEnabled(false);
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

	private void cmbUnitDataLoad() 
	{
		cmbUnit.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "select distinct vUnitId,vUnitName from tbEmpOfficialPersonalInfo order by vUnitName";
			List<?> list = session.createSQLQuery(sql).list();
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
	private void cmbDesignationDataLoad()
	{
		cmbDesignation.removeAllItems();
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
			empType=cmbEmpType.getItemCaption(cmbEmpType.getValue());
		}
		try{
			String sql="select distinct epo.vDesignationId,epo.vDesignationName from tbEmpOfficialPersonalInfo epo inner join tbDesignationInfo des on des.vDesignationId=epo.vDesignationId where  vUnitId like '"+unitId+"' and vEmployeeType like '"+empType+"' and vDepartmentId like '"+deptId+"' and vSectionId like '"+secId+"' order by epo.vDesignationName";
			Iterator<?> iter=dbService(sql);
			System.out.println("Designation :"+sql);
			while(iter.hasNext())
			{
				Object[] element=(Object[])iter.next();
				cmbDesignation.addItem(element[0]);
				cmbDesignation.setItemCaption(element[0], element[1].toString());
			}
		}catch(Exception exp)
		{
			showNotification("Error..to Designation Load",""+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private void cmbReligionDataLoad()
	{
		String unitId="%",deptId="%",secId="%",empType="%",desId="%";
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
			empType=cmbEmpType.getItemCaption(cmbEmpType.getValue());
		}
		if(!chkDesignationAll.booleanValue())
		{
			desId=cmbDesignation.getValue().toString();
		}
		try{
			String sql="select distinct 0,vReligion from tbEmpOfficialPersonalInfo where  vUnitId like '"+unitId+"' and vEmployeeType like '"+empType+"' " +
					" and vDepartmentId like '"+deptId+"' and vSectionId like '"+secId+"' and vDesignationId like '"+desId+"' order by vReligion";
			Iterator<?> iter=dbService(sql);
			System.out.println("Religion :"+sql);
			while(iter.hasNext())
			{
				Object[] element=(Object[])iter.next();
				cmbReligion.addItem(element[1].toString());
			}
		}catch(Exception exp)
		{
			showNotification("Error..to Religion Load",""+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}
	public void setEventAction()
	{
		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				formValidation();
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
				cmbDesignation.removeAllItems();
				chkDesignationAll.setValue(false);
				cmbDesignation.setEnabled(false);
				if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
				{
					if(chkEmployeeTypeAll.booleanValue())
					{	
						cmbEmpType.setValue(null);
						cmbEmpType.setEnabled(false);
						cmbDesignation.setEnabled(true);
						cmbDesignationDataLoad();
					}
					else
					{
						cmbEmpType.setEnabled(true);
						cmbDesignation.setEnabled(false);
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
				cmbDesignation.removeAllItems();
				chkDesignationAll.setValue(false);
				cmbDesignation.setEnabled(false);
				if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
				{
					if(cmbEmpType.getValue()!=null)
					{
						chkEmployeeTypeAll.setValue(false);
						cmbDesignationDataLoad();
						cmbDesignation.setEnabled(true);
					}
				}
			}
		});
		cmbDesignation.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				cmbReligion.removeAllItems();
				cmbReligion.setEnabled(false);
				chkReligionAll.setValue(false);
				if(cmbEmpType.getValue()!=null || chkEmployeeTypeAll.booleanValue())
				{
					if(cmbDesignation.getValue()!=null)
					{
						cmbReligion.setEnabled(true);
						chkDesignationAll.setValue(false);
						cmbReligionDataLoad();
					}
				}
			}
		});
		chkDesignationAll.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				cmbReligion.removeAllItems();
				cmbReligion.setEnabled(false);
				chkReligionAll.setValue(false);
				if(cmbEmpType.getValue()!=null || chkEmployeeTypeAll.booleanValue())
				{
					if(chkDesignationAll.booleanValue())
					{
						cmbDesignation.setEnabled(false);
						cmbDesignation.setValue(null);
						cmbReligion.setEnabled(true);
						cmbReligionDataLoad();
					}
					else
					{
						cmbDesignation.setEnabled(true);
						cmbReligion.setEnabled(false);
					}
				}
			}
		});
		chkReligionAll.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(cmbDesignation.getValue()!=null || chkDesignationAll.booleanValue())
				{
					if(chkReligionAll.booleanValue())
					{
						cmbReligion.setEnabled(false);
						cmbReligion.setValue(null);
					}
					else
					{
						cmbReligion.setEnabled(true);
					}
				}
			}
		});

		opgStatus.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(event.getProperty().toString()=="Active")
				{
					stAIctive = "1";
					System.out.println("value >>:"+stAIctive);
				}
				else if(event.getProperty().toString()=="Inactive")
				{
					stAIctive = "0";
					System.out.println("value >> :"+stAIctive);
				}
				else
				{
					stAIctive = "%";
					System.out.println("value >> :"+stAIctive);
				}
			}
		});
	}
	private void formValidation()
	{
		if(cmbUnit.getValue()!=null || chkUnitAll.booleanValue())
		{
			if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
			{
				if(cmbSection.getValue()!=null || chkSectionAll.booleanValue()==true)
				{
					if(cmbDesignation.getValue()!=null || chkDesignationAll.booleanValue()==true)
					{
						if(cmbEmpType.getValue()!=null || chkEmployeeTypeAll.booleanValue()==true)
						{
							if(cmbReligion.getValue()!=null || chkReligionAll.booleanValue()==true)
							{
								if(!opgStatus.equals(""))
								{
									getAllData();
								}
								else
								{
									showNotification("Warning","Select Activity",Notification.TYPE_WARNING_MESSAGE);	
								}
							}
							else
							{
								showNotification("Warning","Select Religion",Notification.TYPE_WARNING_MESSAGE);
								cmbReligion.focus();
							}
						}
						else
						{
							showNotification("Warning","Select Employee Type",Notification.TYPE_WARNING_MESSAGE);
							cmbEmpType.focus();
						}
					}
					else
					{
						showNotification("Warning","Select Designation",Notification.TYPE_WARNING_MESSAGE);
						cmbDesignation.focus();
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
				cmbUnit.focus();
			}
		}
		else
		{
			showNotification("Warning","Select Unit",Notification.TYPE_WARNING_MESSAGE);
			cmbUnit.focus();
		}
	}
	public Iterator<?> dbService(String sql)
	{
		Iterator<?> iter=null;
		Session session=null;
		try{
			session=SessionFactoryUtil.getInstance().openSession();
			iter=session.createSQLQuery(sql).list().iterator();
		}catch(Exception exp)
		{
			showNotification(""+exp);
		}
		return iter;
	}
	public AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);

		setWidth("500px");
		setHeight("350px");

		cmbUnit=new ComboBox();
		cmbUnit.setWidth("250.0px");
		cmbUnit.setHeight("-1px");
		cmbUnit.setImmediate(true);
		mainLayout.addComponent(new Label("Project : "), "top:10.0px;left:30.0px;");
		mainLayout.addComponent(cmbUnit, "top:8.0px;left:120.0px");
		
		chkUnitAll = new CheckBox("All");
		chkUnitAll.setImmediate(true);
		chkUnitAll.setWidth("-1px");
		chkUnitAll.setHeight("-1px");
		mainLayout.addComponent(chkUnitAll,"top:10px; left:370px;");
		chkUnitAll.setVisible(false);
		
		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("250.0px");
		cmbDepartment.setHeight("-1px");
		mainLayout.addComponent(new Label("Department :"), "top:40px; left:30.0px;");
		mainLayout.addComponent(cmbDepartment, "top:38px; left:120.0px;");

		chkDepartmentAll = new CheckBox("All");
		chkDepartmentAll.setImmediate(true);
		chkDepartmentAll.setWidth("-1px");
		chkDepartmentAll.setHeight("-1px");
		mainLayout.addComponent(chkDepartmentAll,"top:40px; left:370px;");
		
		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("250.0px");
		cmbSection.setHeight("-1px");
		mainLayout.addComponent(new Label("Section :"), "top:70px; left:30.0px;");
		mainLayout.addComponent(cmbSection, "top:68px; left:120.0px;");

		chkSectionAll = new CheckBox("All");
		chkSectionAll.setImmediate(true);
		chkSectionAll.setWidth("-1px");
		chkSectionAll.setHeight("-1px");
		mainLayout.addComponent(chkSectionAll,"top:70px; left:370px;");

		cmbEmpType = new ComboBox();
		cmbEmpType.setImmediate(true);
		cmbEmpType.setWidth("250.0px");
		cmbEmpType.setHeight("-1px");
		mainLayout.addComponent(new Label("Employee Type :"), "top:100px; left:30.0px;");
		mainLayout.addComponent(cmbEmpType, "top:98px; left:120.0px;");

		chkEmployeeTypeAll = new CheckBox("All");
		chkEmployeeTypeAll.setImmediate(true);
		chkEmployeeTypeAll.setWidth("-1px");
		chkEmployeeTypeAll.setHeight("-1px");
		mainLayout.addComponent(chkEmployeeTypeAll,"top:100px; left:370px;");


		cmbDesignation = new ComboBox();
		cmbDesignation.setImmediate(true);
		cmbDesignation.setWidth("250.0px");
		cmbDesignation.setHeight("-1px");
		mainLayout.addComponent(new Label("Designation :"), "top:130px; left:30.0px;");
		mainLayout.addComponent(cmbDesignation, "top:128px; left:120.0px;");

		chkDesignationAll = new CheckBox("All");
		chkDesignationAll.setImmediate(true);
		chkDesignationAll.setWidth("-1px");
		chkDesignationAll.setHeight("-1px");
		mainLayout.addComponent(chkDesignationAll,"top:128px; left:370px;");

		cmbReligion = new ComboBox();
		cmbReligion.setImmediate(true);
		cmbReligion.setWidth("250.0px");
		cmbReligion.setHeight("-1px");
		mainLayout.addComponent(new Label("Religion :"), "top:160px; left:30.0px;");
		mainLayout.addComponent(cmbReligion, "top:158px; left:120.0px;");

		chkReligionAll = new CheckBox("All");
		chkReligionAll.setImmediate(true);
		chkReligionAll.setWidth("-1px");
		chkReligionAll.setHeight("-1px");
		mainLayout.addComponent(chkReligionAll,"top:160px; left:370px;");

		opgStatus = new OptionGroup("",aictiveType);
		opgStatus.setImmediate(true);
		opgStatus.setWidth("250px");
		opgStatus.setHeight("-1px");
		opgStatus.setValue("Active");
		opgStatus.setStyleName("horizontal");
		mainLayout.addComponent(new Label("Service Status Type : "), "top:190px; left:30px;");
		mainLayout.addComponent(opgStatus, "top:188px; left:147px;");

		opgReportView = new OptionGroup("",reportView);
		opgReportView.setImmediate(true);
		opgReportView.setStyleName("horizontal");
		opgReportView.setValue("PDF");
		mainLayout.addComponent(opgReportView, "top:250px;left:110.0px;");
		mainLayout.addComponent(cButton, "bottom:15px;left:120.0px;");

		return mainLayout;
	}
	private void focusMove()
	{
		allComp.add(cmbUnit);
		allComp.add(cmbEmpType);
		allComp.add(cmbSection);
		allComp.add(cmbDesignation);
		allComp.add(cmbReligion);
		allComp.add(cButton.btnPreview);
		new FocusMoveByEnter(this,allComp);
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

	private void getAllData()
	{
		String unitValue = "%";
		String empTypeValue = "%";
		String sectionValue = "%";
		String designationValue = "%";
		String religionValue = "%";
		String statusValue = "%";
		String deptValue="%";

		if(!chkUnitAll.booleanValue()){unitValue = cmbUnit.getValue().toString();}
		if(!chkEmployeeTypeAll.booleanValue()){empTypeValue = cmbEmpType.getItemCaption(cmbEmpType.getValue().toString());}
		if(!chkDepartmentAll.booleanValue()){deptValue = cmbDepartment.getValue().toString();}
		if(!chkSectionAll.booleanValue()){sectionValue = cmbSection.getValue().toString();}
		if(!chkDesignationAll.booleanValue()){designationValue = cmbDesignation.getValue().toString();}
		if(!chkReligionAll.booleanValue()){religionValue = cmbReligion.getItemCaption(cmbReligion.getValue().toString());}

		if(opgStatus.getValue().toString().equals("Active")){statusValue = "1";}
		else if(opgStatus.getValue().toString().equals("Inactive")){statusValue = "0";}
		else if(opgStatus.getValue().toString().equals("All")){statusValue = "%";}

		reportShow(unitValue,empTypeValue,deptValue,sectionValue,designationValue,religionValue,statusValue);
	}


	private void reportShow(
			Object unitValue,Object empTypeValue,Object deptValue,Object sectionValue,
			Object designationValue,Object religionvalue,Object statusValue)
	{
		reportTime = new ReportDate();
		ReportOption RadioBtn = new ReportOption(opgReportView.getValue().toString());
		Session session=SessionFactoryUtil.getInstance().openSession();
		
		try
		{
			String query="select vEmployeeId,vEmployeeCode,dJoiningDate,dValidDate,dStatusDate,vEmployeeName,vFamilyName,vGivenName,vGender,dDateOfBirth,"
					+ "vEducationDetails,vDesignationName,vLevelOfEnglish,vCareerPeriod,vCompanyPeriod,vContactNo,vEmailAddress,"
					+ "vUnitName,vDepartmentId,vDepartmentName,vSectionName,vEmployeeStatus "
					+ "from funEmployeeDetails"
					+ "("
						+ "'"+unitValue+"','"+deptValue+"','"+sectionValue+"','"+designationValue+"','"+empTypeValue+"','"+religionvalue+"',"
						+ "'%','%','"+statusValue+"'"
					+ ") order by vUnitName,vDepartmentName,vSectionName,dJoiningDate";
			System.out.println("reportShow: "+query);

			if(queryValueCheck(query))
			{
				if(opgReportView.getValue()=="Excel")
				{
					String loc = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/temp/attendanceFolder";
					String fname = "EmployeeLis.xls";
					String url = getWindow().getApplication().getURL()+"VAADIN/themes/temp/attendanceFolder/"+fname;
					
					String strColName[]={"SL#","vEmployeeCode","vEmployeeName","vFamilyName","vGivenName","vDesignationName","dJoiningDate",
							"dValidDate","dStatusDate","vGender","dDateOfBirth","vEducationDetails","vLevelOfEnglish","vCareerPeriod",
							"vCompanyPeriod","vContactNo","vEmailAddress","vEmployeeStatus",
							"vServiceType","vFatherName","vMotherName","vPresentAddress","vPermanentAddress","vBloodGroup",
							"vBankName","vBranchName","vAccountNo","mBasic","mHouseRent","mMobileAllowance","vLevelOfEnglish",
							"Signature"};
					
					String Header="Employee List";
					String exelSql="";
					
					exelSql = "select distinct vUnitId,vDepartmentId,vUnitName,vDepartmentName from tbEmpOfficialPersonalInfo "
							+ "where vUnitId like '"+unitValue+"' "
							+ "and vDepartmentId like '"+deptValue+"' "
							+ "and vSectionId like '"+sectionValue+"' "
							+ "and vDesignationId like '"+designationValue+"' "
							+ "and vReligion like '"+religionvalue+"' "
							+ "and vEmployeeType like '"+empTypeValue+"' "
							+ "and bStatus like '"+statusValue+"' ";
					
					System.out.println("exelSql: "+exelSql);
					
					List <?> lst1=session.createSQLQuery(exelSql).list();
							
					String detailQuery[]=new String[lst1.size()];
					String [] signatureOption = {"HEAD OF HR","HEAD OF ACCOUNTS","GROUP C.E.O / CHAIRMAN"};
					//String [] signatureOption = new String [0];
					String [] groupItem=new String[lst1.size()];
					Object [][] GroupElement=new Object[lst1.size()][];
					String [] GroupColName=new String[0];
					int countInd=0;
					
					for(Iterator<?> iter=lst1.iterator(); iter.hasNext();)
					{
						Object [] element = (Object[])iter.next();
						groupItem[countInd]="Project Name  : "+element[2].toString()+"                                                Department Name : "+element[3].toString();
						GroupElement[countInd]=new Object [] {(Object)"",(Object)"Project Name : ",element[2],(Object)"Department Name : ",element[3]};
					
						detailQuery[countInd]="select vEmployeeCode,vEmployeeName,vFamilyName,vGivenName,vDesignationName,dJoiningDate,"
							+ "dValidDate,dStatusDate,vGender,dDateOfBirth,vEducationDetails,vLevelOfEnglish,vCareerPeriod,"
							+ "vCompanyPeriod,vContactNo,vEmailAddress,vEmployeeStatus,"
							+ "vServiceType,vFatherName,vMotherName,vPresentAddress,vPermanentAddress,vBloodGroup,vBankName,vBranchName,"
							+ "vAccountNo,CAST(ISNULL(mBasic,0) as FLOAT)mBasic,CAST(ISNULL(mHouseRent,0) as FLOAT)mHouseRent,CAST(ISNULL(mMobileAllowance,0) as FLOAT)mMobileAllowance,vLevelOfEnglish "
							+ "from funEmployeeDetails"
							+ "("
								+ "'"+element[0].toString()+"','"+element[1].toString()+"','"+sectionValue+"','"+designationValue+"','"+empTypeValue+"','"+religionvalue+"',"
								+ "'%','%','"+statusValue+"'"
							+ ") order by vUnitName,vDepartmentName,vSectionName,dJoiningDate";
							
						System.out.println("Details query :"+detailQuery[countInd]);
						countInd++;
						
					}
					
					new GenerateExcelReport(sessionBean, loc, url, fname, "Employee Lis ", "Employee Lis",
							Header, strColName, 2, groupItem, GroupColName, GroupElement, 1, detailQuery, 0, 0, "A4",
							"Landscape",signatureOption,cmbUnit.getItemCaption(cmbUnit.getValue()));
					
					Window window = new Window();
					getApplication().addWindow(window);
					getWindow().open(new ExternalResource(url),"_blank",500,200,Window.BORDER_NONE);
				}
				else
				{
					HashMap <String,Object>  hm = new HashMap <String,Object> ();
					hm.put("company", sessionBean.getCompany());
					hm.put("address", sessionBean.getCompanyAddress());
					hm.put("phone",sessionBean.getCompanyContact());
					hm.put("username", sessionBean.getUserName()+" "+sessionBean.getUserIp());
					hm.put("SysDate",reportTime.getTime);
					hm.put("logo", sessionBean.getCompanyLogo());
					hm.put("Unit", cmbUnit.getItemCaption(cmbUnit.getValue()));
					hm.put("status", opgStatus.getValue().toString());
					hm.put("sql", query);

					Window win = new ReportViewer(hm,"report/account/hrmModule/rptEmployeeList.jasper",
							this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
							this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
							this.getWindow().getApplication().getURL()+"VAADIN/applet",RadioBtn.Radio);

					win.setCaption("Unit Report");
					this.getParent().getWindow().addWindow(win);
				}
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
}