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
public class RptLeaveRegisterIndividual extends Window
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;
	private CommonMethod cm;
	private String menuId = "";
	private OptionGroup opgReport;
	
	private ComboBox cmbEmployee,cmbUnit;
	private ComboBox cmbSection,cmbDepartment;
	private CheckBox chkDepartment = new CheckBox("All");
	private CheckBox chkSectionAll = new CheckBox("All");
	private CheckBox chkEmployeeAll = new CheckBox("All");
	private CheckBox chkUnitAll = new CheckBox("All");
	
	public SimpleDateFormat dFdmy = new SimpleDateFormat("dd-MMM-yyyy");

	private Label lblComboLabel ;

	private static final List<String> type1 = Arrays.asList(new String[]{"PDF","Other"});

	boolean isPreview=false;

	private ReportDate reportTime = new ReportDate();

	private CommonButton cButton= new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");


	ArrayList<Component> allComp = new ArrayList<Component>();
	
	public RptLeaveRegisterIndividual(SessionBean sessionBean,String menuId)
	{
		this.sessionBean=sessionBean;
		this.setCaption("INDIVIDUAL LEAVE REGISTER :: "+sessionBean.getCompany());
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		buildMainLayout();
		setBtnAction();
		setContent(mainLayout);
		cmbUnitDataLoad();
		focusMove();
		cmbUnit.focus();
		cmbDepartment.setEnabled(false);
		cmbSection.setEnabled(false);
		cmbEmployee.setEnabled(false);
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

	private void focusMove()
	{
		allComp.add(cmbUnit);
		allComp.add(cmbSection);
		allComp.add(cmbEmployee);
		allComp.add(cButton.btnPreview);
		new FocusMoveByEnter(this,allComp);
	}
	private void cmbUnitDataLoad() {
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "select vUnitId,vUnitName from tbEmpOfficialPersonalInfo order by vUnitName";
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
	private void setBtnAction()
	{
		cButton.btnPreview.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event)
			{
				if(cmbUnit.getValue()!=null || chkUnitAll.booleanValue())
				{
					if(cmbDepartment.getValue()!=null || chkDepartment.booleanValue()==true)
					{
						if(cmbSection.getValue()!=null || chkSectionAll.booleanValue()==true)
						{
							if(cmbEmployee.getValue()!=null || chkEmployeeAll.booleanValue()==true)
							{
								reportView();
							}
							else
							{
								getParent().showNotification("Please Select "+lblComboLabel.getValue().toString()+"", Notification.TYPE_WARNING_MESSAGE);
							}
						}
						else
						{
							getParent().showNotification("Please Select Section", Notification.TYPE_WARNING_MESSAGE);
						}

					}
					else
					{
						getParent().showNotification("Please Select Department", Notification.TYPE_WARNING_MESSAGE);
					}

				}
				else
				{
					getParent().showNotification("Please Select Project", Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		cButton.btnExit.addListener(new ClickListener() 
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
				cmbDepartment.setEnabled(false);
				chkDepartment.setValue(false);
				if(cmbUnit.getValue()!=null)
				{
					chkUnitAll.setValue(false);
					cmbDepartment.setEnabled(true);
					cmbDepartmentDataLoad();
				}
			}
		});
		chkUnitAll.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				cmbDepartment.removeAllItems();
				cmbDepartment.setEnabled(false);
				chkDepartment.setValue(false);
				if(chkUnitAll.booleanValue())
				{
					cmbUnit.setEnabled(false);
					cmbUnit.setValue(null);
					cmbDepartment.setEnabled(true);
					cmbDepartmentDataLoad();
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
				cmbSection.setEnabled(false);
				chkSectionAll.setValue(false);
				if(cmbUnit.getValue()!=null || chkUnitAll.booleanValue())
				{
					if(cmbDepartment.getValue()!=null)
					{
						chkDepartment.setValue(false);
						cmbSection.setEnabled(true);
						cmbSectionDataLoad();
					}
				}
			}
		});
		chkDepartment.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				cmbSection.removeAllItems();
				cmbSection.setEnabled(false);
				chkSectionAll.setValue(false);
				if(cmbUnit.getValue()!=null || chkUnitAll.booleanValue())
				{
					if(chkDepartment.booleanValue())
					{
						cmbDepartment.setEnabled(false);
						cmbDepartment.setValue(null);
						cmbSection.setEnabled(true);
						cmbSectionDataLoad();
					}
					else
					{
						cmbDepartment.setEnabled(true);
						cmbSection.setEnabled(false);
					}
				}
				else
				{
					chkDepartment.setValue(false);
					showNotification("Warning..","Select Project Please!",Notification.TYPE_HUMANIZED_MESSAGE);
				}
			}
		});
		cmbSection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmployee.removeAllItems();
				if(cmbUnit.getValue()!=null || chkUnitAll.booleanValue())
				{
					if(cmbSection.getValue()!=null)
					{
						cmbEmployee.setEnabled(true);
						cmbEmployeeNameDataAdd();
					}
				}
			}
		});
		chkSectionAll.addListener(new ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				cmbEmployee.removeAllItems();
				if(cmbDepartment.getValue()!=null || chkDepartment.booleanValue())
				{
					if(chkSectionAll.booleanValue())
					{
						cmbSection.setEnabled(false);
						cmbSection.setValue(null);
						cmbEmployee.setEnabled(true);
						cmbEmployeeNameDataAdd();
					}
					else
					{
						cmbSection.setEnabled(true);
					}
				}
				else
				{
					chkSectionAll.setValue(false);
					showNotification("Warning..","Select Department Please!",Notification.TYPE_HUMANIZED_MESSAGE);
				}
			}
		});
		
		chkEmployeeAll.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				if(chkEmployeeAll.booleanValue())
				{
					cmbEmployee.setEnabled(false);
					cmbEmployee.setValue(null);
				}
				else
				{
					cmbEmployee.setEnabled(true);
				}
			}
		});
	}
	private void cmbDepartmentDataLoad()
	{
		String unitId="%";
		if(cmbUnit.getValue()!=null)
		{
			unitId=cmbUnit.getValue().toString();
		}
		try{
			String sql="select distinct vDepartmentId,(select distinct vDepartmentName from tbDepartmentInfo "
					+ "where vDepartmentId=a.vDepartmentId)vDepartmentName "
					+ "from tbLeaveEntitlement a where vUnitId like '"+unitId+"' order by vDepartmentName";
			System.out.println("cmbDepartmentDataLoad: "+sql);
			
			Iterator<?> iter=dbService(sql);
			while(iter.hasNext())
			{
				Object[] element=(Object[])iter.next();
				cmbDepartment.addItem(element[0]);
				cmbDepartment.setItemCaption(element[0], element[1].toString());
			}
		}catch(Exception exp)
		{
			showNotification("Error..to department Load",""+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private void cmbSectionDataLoad()
	{
		String deptId="%",unitId="%";
		if(cmbDepartment.getValue()!=null)
		{
			deptId=cmbDepartment.getValue().toString();
		}
		if(cmbUnit.getValue()!=null)
		{
			unitId=cmbUnit.getValue().toString();
		}
		try{
			String sql="select distinct vSectionId,vSectionName from tbEmpOfficialPersonalInfo " +
					"where vUnitId like '"+unitId+"' and vDepartmentId like '"+deptId+"'  order by vSectionName";
			System.out.println("cmbSectionDataLoad: "+sql);
			
			Iterator<?> iter=dbService(sql);			
			while(iter.hasNext())
			{
				Object[] element=(Object[])iter.next();
				cmbSection.addItem(element[0]);
				cmbSection.setItemCaption(element[0], element[1].toString());
			}
		}catch(Exception exp)
		{
			showNotification("Error..to Section Load",""+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	private void cmbEmployeeNameDataAdd()
	{
		cmbEmployee.removeAllItems();
		String query="";
		String deptId="%",secId="%",desId="%",unitId="%";
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
		try
		{
			query="select vEmployeeId,vEmployeeCode,vEmployeeName from tbEmpOfficialPersonalInfo " +
					"where vUnitId like '"+unitId+"' and vDepartmentId like '"+deptId+"' and vSectionId like '"+secId+"' " +
					"and vDesignationId like '"+desId+"' order by vEmployeeCode";
			
			System.out.println("cmbEmployeeNameDataAdd: "+query);
			
			Iterator<?> iter=dbService(query);

			while(iter.hasNext())
			{
				Object [] element=(Object[])iter.next();
				cmbEmployee.addItem(element[0]);
				cmbEmployee.setItemCaption(element[0], element[1]+"-"+element[2]);
			}
		}
		catch (Exception exp)
		{
			showNotification("cmbEmployeeNameDataAdd", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
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
	private void reportView()
	{
		ReportOption RadioBtn= new ReportOption(opgReport.getValue().toString());

		try
		{
			HashMap <String,Object> hm = new HashMap <String,Object> ();
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			hm.put("SysDate",reportTime.getTime);
			hm.put("path", "./report/account/hrmModule/");
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("Unit",cmbUnit.getItemCaption(cmbUnit.getValue()));
			
			
			String strDetails="";

			String subQuery="select dSanctionFrom,dSanctionTo,mTotalDays,iApprovedFlag from tbEmpLeaveApplicationInfo "
			+ "where vEmployeeId ='"+cmbEmployee.getValue()+"' and vLeaveTypeID = '1' and iApprovedFlag = '1' "
			+ "and dEntitleFromDate=(select distinct dEntitleFromDate from tbLeaveEntitlement where vEmployeeId ='"+cmbEmployee.getValue()+"' and vLeaveTypeID = '1' and vStatus='1') "
			+ "and dEntitleToDate=(select distinct dEntitleToDate from tbLeaveEntitlement where vEmployeeId ='"+cmbEmployee.getValue()+"' and vLeaveTypeID = '1' and vStatus='1') "
			+ "order by dSanctionFrom desc";
			
			System.out.println("Preview Query :" +subQuery);

			String subQuerySL="select dSanctionFrom,dSanctionTo,mTotalDays,iApprovedFlag from tbEmpLeaveApplicationInfo "
					+ "where vEmployeeId ='"+cmbEmployee.getValue()+"' and vLeaveTypeID = '2' and iApprovedFlag = '1' "
					+ "and dEntitleFromDate=(select distinct dEntitleFromDate from tbLeaveEntitlement where vEmployeeId ='"+cmbEmployee.getValue()+"' and vLeaveTypeID = '2' and vStatus='1') "
					+ "and dEntitleToDate=(select distinct dEntitleToDate from tbLeaveEntitlement where vEmployeeId ='"+cmbEmployee.getValue()+"' and vLeaveTypeID = '2' and vStatus='1') "
					+ "order by dSanctionFrom desc";					
			
			System.out.println("Preview Query :" +subQuerySL);

			String subQueryEL="select dSanctionFrom,dSanctionTo,mTotalDays,iApprovedFlag from tbEmpLeaveApplicationInfo "
					+ "where vEmployeeId ='"+cmbEmployee.getValue()+"' and vLeaveTypeID = '3' and iApprovedFlag = '1' "
					+ "and dEntitleFromDate=(select distinct dEntitleFromDate from tbLeaveEntitlement where vEmployeeId ='"+cmbEmployee.getValue()+"' and vLeaveTypeID = '3' and vStatus='1') "
					+ "and dEntitleToDate=(select distinct dEntitleToDate from tbLeaveEntitlement where vEmployeeId ='"+cmbEmployee.getValue()+"' and vLeaveTypeID = '3' and vStatus='1') "
					+ "order by dSanctionFrom desc";
						
			System.out.println("Preview Query :" +subQueryEL);

			String query=" select * from funIndividualLeaveRegister('"+cmbEmployee.getValue()+"')";
			
			System.out.println("Preview Query :" +query);

			if(queryValueCheck(subQuery))
			{
				strDetails="Enjoyed Leave Details :";
				hm.put("subSql", subQuery);
			}
			else
			{
				hm.put("subSql", "");
			}

			if(queryValueCheck(subQuerySL))
			{
				strDetails="Enjoyed Leave Details :";
				hm.put("subSqlSL", subQuerySL);
			}
			else
			{
				hm.put("subSqlSL", "");
			}


			if(queryValueCheck(subQueryEL))
			{
				strDetails="Enjoyed Leave Details :";
				hm.put("subSqlEL", subQueryEL);
			}
			else
			{
				hm.put("subSqlEL", "");
			}

			if(queryValueCheck(query))
			{
				hm.put("pDetails", strDetails);
				hm.put("sql", query);
				Window win = new ReportViewer(hm,"report/account/hrmModule/rptLeaveRegisterIndividual.jasper",
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
			this.getParent().showNotification("reportpreview",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		setWidth("500px");
		setHeight("240px");

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

		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("250.0px");
		cmbDepartment.setHeight("-1px");
		mainLayout.addComponent(new Label("Department :"), "top:40px; left:30.0px;");
		mainLayout.addComponent(cmbDepartment, "top:38px; left:120.0px;");
		

		chkDepartment = new CheckBox("All");
		chkDepartment.setImmediate(true);
		chkDepartment.setWidth("-1px");
		chkDepartment.setHeight("-1px");
		mainLayout.addComponent(chkDepartment,"top:40px; left:370px;");
		

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

		cmbEmployee = new ComboBox();
		cmbEmployee.setImmediate(true);
		cmbEmployee.setWidth("250.0px");
		cmbEmployee.setHeight("-1px");
		mainLayout.addComponent(new Label("Employee"),"top:100px; left:30px");
		mainLayout.addComponent(cmbEmployee, "top:98px; left:120.0px;");
		cmbEmployee.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		chkEmployeeAll = new CheckBox("All");
		chkEmployeeAll.setImmediate(true);
		chkEmployeeAll.setWidth("-1px");
		chkEmployeeAll.setHeight("-1px");
		mainLayout.addComponent(chkEmployeeAll,"top:100px; left:370px;");
		chkEmployeeAll.setVisible(false);
		
		opgReport = new OptionGroup("",type1);
		opgReport.setImmediate(true);
		opgReport.setStyleName("horizontal");
		opgReport.setValue("PDF");
		mainLayout.addComponent(opgReport, "top:200px;left:200.0px;");
		opgReport.setVisible(false);

		cButton.btnPreview.setImmediate(true);
		mainLayout.addComponent(cButton, "bottom:15px; left:160.0px;");

		return mainLayout;
	}
}
