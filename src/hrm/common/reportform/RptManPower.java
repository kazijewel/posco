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
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class RptManPower extends Window 
{
	private SessionBean sessionBean;
	public AbsoluteLayout mainLayout;
	private CommonMethod cm;
	private String menuId = "";
	
	private ReportDate reportTime;
	private ComboBox cmbUnit;	
	private ComboBox cmbSection;
	private ComboBox cmbDepartment;
	private ComboBox cmbDesignation;
	
	private OptionGroup opgStatus = new OptionGroup();
	private CheckBox chkUnitAll = new CheckBox("All");
	private CheckBox chkSectionAll = new CheckBox("All");
	private CheckBox chkDesignationAll = new CheckBox("All");
	private CheckBox chkDepartmentAll = new CheckBox("All");

	private static final List<String> aictiveType = Arrays.asList(new String[]{"Active","Inactive","All"});
	ArrayList<Component> allComp = new ArrayList<Component>();
	private String stAIctive = "";

	CommonButton cButton = new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	
	public RptManPower(SessionBean sessionBean,String menuId)
	{
		this.sessionBean = sessionBean;
		this.setCaption("MAN POWER :: "+sessionBean.getCompany());
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
		cmbDesignation.setEnabled(false);
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
		
		try{
			String sql="select distinct epo.vDesignationId,epo.vDesignationName from tbEmpOfficialPersonalInfo epo inner join tbDesignationInfo des on des.vDesignationId=epo.vDesignationId where  vUnitId like '"+unitId+"'  and vDepartmentId like '"+deptId+"' and vSectionId like '"+secId+"' order by epo.vDesignationName";
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
				cmbDesignation.removeAllItems();
				chkDesignationAll.setValue(false);
				cmbDesignation.setEnabled(false);
				if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
				{
					if(cmbSection.getValue()!=null)
					{
						chkSectionAll.setValue(false);
						cmbDesignationDataLoad();
						cmbDesignation.setEnabled(true);
					}
				}
			}
		});

		chkSectionAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				cmbDesignation.removeAllItems();
				chkDesignationAll.setValue(false);
				cmbDesignation.setEnabled(false);
				if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
				{
					if(chkSectionAll.booleanValue())
					{
						cmbSection.setValue(null);
						cmbSection.setEnabled(false);
						cmbDesignation.setEnabled(true);
						cmbDesignationDataLoad();
					}
					else
					{
						cmbSection.setEnabled(true);
						cmbDesignation.setEnabled(false);
					}
				}
				else{
					chkSectionAll.setValue(false);
					showNotification("Warning..","Select Department Please!",Notification.TYPE_HUMANIZED_MESSAGE);
				}
			}
		});
		
		
		chkDesignationAll.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				
				if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
				{
					if(chkDesignationAll.booleanValue())
					{
						cmbDesignation.setEnabled(false);
						cmbDesignation.setValue(null);
					}
					else
					{
						cmbDesignation.setEnabled(true);
					}
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
				if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
				{
					if(cmbDesignation.getValue()!=null || chkDesignationAll.booleanValue())
					{
						getAllData();
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
		setHeight("280px");

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



		cmbDesignation = new ComboBox();
		cmbDesignation.setImmediate(true);
		cmbDesignation.setWidth("250.0px");
		cmbDesignation.setHeight("-1px");
		mainLayout.addComponent(new Label("Designation :"), "top:100px; left:30.0px;");
		mainLayout.addComponent(cmbDesignation, "top:98px; left:120.0px;");

		chkDesignationAll = new CheckBox("All");
		chkDesignationAll.setImmediate(true);
		chkDesignationAll.setWidth("-1px");
		chkDesignationAll.setHeight("-1px");
		mainLayout.addComponent(chkDesignationAll,"top:98px; left:370px;");

		opgStatus = new OptionGroup("",aictiveType);
		opgStatus.setImmediate(true);
		opgStatus.setStyleName("horizontal");
		opgStatus.setValue("Active");
		mainLayout.addComponent(opgStatus, "top:130px;left:110.0px;");
		
		mainLayout.addComponent(cButton, "bottom:15px;left:120.0px;");

		return mainLayout;
	}
	private void focusMove()
	{
		allComp.add(cmbUnit);
		allComp.add(cmbSection);
		allComp.add(cmbDesignation);
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
		if(!chkDepartmentAll.booleanValue()){deptValue = cmbDepartment.getValue().toString();}
		if(!chkSectionAll.booleanValue()){sectionValue = cmbSection.getValue().toString();}
		if(!chkDesignationAll.booleanValue()){designationValue = cmbDesignation.getValue().toString();}
		if(opgStatus.getValue().toString().equals("Active")){statusValue = "1";}
		else if(opgStatus.getValue().toString().equals("Inactive")){statusValue = "0";}
		else if(opgStatus.getValue().toString().equals("All")){statusValue = "%";}
		
		reportShow(unitValue,empTypeValue,deptValue,sectionValue,designationValue,religionValue,statusValue);
	}


	private void reportShow(Object unitValue,Object empTypeValue,Object deptValue,Object sectionValue,Object designationValue,Object religionvalue,Object statusValue)
	{

		Session session=SessionFactoryUtil.getInstance().openSession();
		String report = "";
		reportTime = new ReportDate();
		try
		{
			HashMap <String,Object>  hm = new HashMap <String,Object> ();
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone",sessionBean.getCompanyContact());
			hm.put("username", sessionBean.getUserName()+" "+sessionBean.getUserIp());
			hm.put("SysDate",reportTime.getTime);
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("Unit", cmbUnit.getItemCaption(cmbUnit.getValue()));
	

			String query="select * from funEmployeeDetails('"+unitValue+"','"+deptValue+"','"+sectionValue+"','"+designationValue+"','"+empTypeValue+"','"+religionvalue+"','%','%','"+statusValue+"') order by vUnitName,vDepartmentName,vSectionName,iRank,dJoiningDate";

			System.out.println("rePORT SHOW"+query);




			if(queryValueCheck(query))
			{

				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/account/hrmModule/rptManPower.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",true);

				win.setCaption("Unit Report");
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
}