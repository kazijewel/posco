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
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class RptOverTimeRequest extends Window
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblUnit;
	private Label lblSection;

	private ComboBox cmbUnit;
	private ComboBox cmbSection,cmbDepartment;
	private ComboBox cmbEmployeeName;
	private ComboBox cmbTimeOfWork;

	private CheckBox chkSectionAll,chkDepartmentAll;
	private CheckBox chkEmployeeName;

	ArrayList<Component> allComp = new ArrayList<Component>();
	private PopupDateField dFrom,dTo;
	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});
	
	TextField txtPath=new TextField();
	
	private CommonMethod cm;
	private String menuId = "";
	public RptOverTimeRequest(SessionBean sessionBean,String menuId)
	{
		this.sessionBean=sessionBean;
		this.setCaption("OVER TIME REQUEST :: "+sessionBean.getCompany());
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		buildMainLayout();
		setContent(mainLayout);
		setEventAction();
		focusMove();
		authenticationCheck();
		cmbUnitDataLoad();
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
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "select distinct epo.vUnitId,epo.vUnitName from tbOTRequest ot "+
			" inner join tbEmpOfficialPersonalInfo epo on epo.vEmployeeId=ot.vEmployeeId "+
			" where ot.dRequestDate>='"+sessionBean.dfDb.format(dFrom.getValue())+"' "+
			" and ot.dRequestDate<='"+sessionBean.dfDb.format(dTo.getValue())+"'";
			
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
			String query="select distinct epo.vDepartmentId,epo.vDepartmentName from tbOTRequest ot "+
					" inner join tbEmpOfficialPersonalInfo epo on epo.vEmployeeId=ot.vEmployeeId "+
					" where ot.dRequestDate>='"+sessionBean.dfDb.format(dFrom.getValue())+"' "+
					" and ot.dRequestDate<='"+sessionBean.dfDb.format(dTo.getValue())+"' "
				  + " and epo.vUnitId like '"+(cmbUnit.getValue()==null?"%":cmbUnit.getValue())+"' order by epo.vDepartmentName";
		
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
			String query="select distinct epo.vSectionId,epo.vSectionName from tbOTRequest ot "+
					" inner join tbEmpOfficialPersonalInfo epo on epo.vEmployeeId=ot.vEmployeeId "+
					" where ot.dRequestDate>='"+sessionBean.dfDb.format(dFrom.getValue())+"' "+
					" and ot.dRequestDate<='"+sessionBean.dfDb.format(dTo.getValue())+"' "
				  + " and epo.vUnitId like '"+(cmbUnit.getValue()==null?"%":cmbUnit.getValue())+"' "
				  + " and epo.vDepartmentId like '"+(chkDepartmentAll.booleanValue()?"%":cmbDepartment.getValue()==null?"%":cmbDepartment.getValue())+"' order by epo.vSectionName";
		
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
			String query="select distinct epo.vEmployeeId,epo.vEmployeeCode,epo.vEmployeeName from tbOTRequest ot "+
					" inner join tbEmpOfficialPersonalInfo epo on epo.vEmployeeId=ot.vEmployeeId "+
					" where ot.dRequestDate>='"+sessionBean.dfDb.format(dFrom.getValue())+"' "+
					" and ot.dRequestDate<='"+sessionBean.dfDb.format(dTo.getValue())+"' "
				  + " and epo.vUnitId like '"+(cmbUnit.getValue()==null?"%":cmbUnit.getValue())+"' "
				  + " and epo.vDepartmentId like '"+(chkDepartmentAll.booleanValue()?"%":cmbDepartment.getValue()==null?"%":cmbDepartment.getValue())+"' "
				  + " and epo.vSectionId like '"+(chkSectionAll.booleanValue()?"%":cmbSection.getValue()==null?"%":cmbSection.getValue())+"' order by epo.vEmployeeName";
		
			System.out.println("employeeSetData :"+query);
			
			List <?> list=session.createSQLQuery(query).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbEmployeeName.addItem(element[0]);
				cmbEmployeeName.setItemCaption(element[0], (element[1]+"-"+element[2]));
			}
		}
		catch(Exception exp){
			showNotification("employeeSetData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	public void cmbTimeOfWorkData()
	{
		cmbTimeOfWork.removeAllItems();
		
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();

		try
		{
			String query="select distinct ot.vTransactionId,RIGHT(CONVERT(VARCHAR, dTimeFrom, 100), 7)+' - '+RIGHT(CONVERT(VARCHAR, dTimeTo, 100), 7) timeWork,dTimeFrom from tbOTRequest ot "+
					" inner join tbEmpOfficialPersonalInfo epo on epo.vEmployeeId=ot.vEmployeeId "+
					" where ot.dRequestDate>='"+sessionBean.dfDb.format(dFrom.getValue())+"' "+
					" and ot.dRequestDate<='"+sessionBean.dfDb.format(dTo.getValue())+"' "
				  + " and epo.vUnitId like '"+(cmbUnit.getValue()==null?"%":cmbUnit.getValue())+"' "
				  + " and epo.vDepartmentId like '"+(chkDepartmentAll.booleanValue()?"%":cmbDepartment.getValue()==null?"%":cmbDepartment.getValue())+"' "
				  + " and epo.vSectionId like '"+(chkSectionAll.booleanValue()?"%":cmbSection.getValue()==null?"%":cmbSection.getValue())+"' "
				  + " and epo.vEmployeeId like '"+(chkEmployeeName.booleanValue()?"%":cmbEmployeeName.getValue()==null?"%":cmbEmployeeName.getValue())+"' order by dTimeFrom desc";
		
			System.out.println("cmbTimeOfWorkData :"+query);
			
			
			List <?> list=session.createSQLQuery(query).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbTimeOfWork.addItem(element[0]);
				cmbTimeOfWork.setItemCaption(element[0], sessionBean.dfBd.format(element[2])+"# "+element[1]);
			}
		}
		catch(Exception exp){
			showNotification("employeeSetData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void setEventAction()
	{
		dFrom.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
				if(dFrom.getValue()!=null)
				{
					cmbUnitDataLoad();
				}
			}
		});
		dTo.addListener(new ValueChangeListener() {
					
			public void valueChange(ValueChangeEvent event) {
				if(dTo.getValue()!=null)
				{
					cmbUnitDataLoad();
				}
			}
		});
		cmbUnit.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				
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
				
				if(cmbDepartment.getValue()!=null)
				{
					cmbEmployeeName.setValue(null);
					chkSectionAll.setValue(false);
					cmbTimeOfWork.setValue(null);
					cmbSectionAddData();
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
						
						chkSectionAll.setValue(false);
						cmbEmployeeName.setValue(null);
						cmbTimeOfWork.setValue(null);
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
		cmbSection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				
				if(cmbSection.getValue()!=null)
				{
					cmbEmployeeName.setValue(null);
					cmbTimeOfWork.setValue(null);
					employeeSetData();
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
						cmbEmployeeName.setValue(null);
						cmbTimeOfWork.setValue(null);
						employeeSetData();
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
		cmbEmployeeName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				
				if(cmbEmployeeName.getValue()!=null)
				{
					cmbTimeOfWorkData();
				}
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
						cmbEmployeeName.setEnabled(false);
						cmbEmployeeName.setValue(null);
						cmbTimeOfWorkData();
					}
					else
					{
						cmbEmployeeName.setEnabled(true);
					}
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
				if(dFrom.getValue()!=null && dTo.getValue()!=null)
				{
					if(cmbUnit.getValue()!=null)
					{
						if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
						{
							if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
							{
								if(cmbEmployeeName.getValue()!=null || chkEmployeeName.booleanValue())
								{
									if(cmbTimeOfWork.getValue()!=null)
									{
										reportShow();
									}
									else
									{
										showNotification("Select Time of Work",Notification.TYPE_WARNING_MESSAGE);
									}
								}
								else
								{
									showNotification("Select Employee Name",Notification.TYPE_WARNING_MESSAGE);
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
						showNotification("Select Project",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					showNotification("Select Date!",Notification.TYPE_WARNING_MESSAGE);
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

		try
		{			

			HashMap <String,Object>  hm = new HashMap <String,Object> ();
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone",sessionBean.getCompanyContact());
			hm.put("userName", sessionBean.getUserName()+" "+sessionBean.getUserIp());
			hm.put("SysDate",new java.util.Date());
			hm.put("developer", sessionBean.getDeveloperAddress());
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("empList", "Employee's List ( Site Office )");

			String query="select *,case when iHoliday=1 and mTotalTimeHR>10 " +
			"then mTotalTimeHR-1 else mTotalTimeHR end hours, "+
			" (select vEmployeeCode from tbEmpOfficialPersonalInfo where vEmployeeId=ot.vEmployeeId)vEmployeeCode "+
			" from tbOTRequest ot "
			+ " where  vTransactionId like '"+cmbTimeOfWork.getValue()+"' and vEmployeeId like '"+cmbEmployeeName.getValue()+"' ";

			System.out.println("report :"+query);
			
			

			if(queryValueCheck(query))
			{
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/account/hrmModule/rptOverTimeRequestForm.jasper",
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
		allComp.add(cmbUnit);
		allComp.add(cmbDepartment);
		allComp.add(cmbSection);
		allComp.add(cmbEmployeeName);
		allComp.add(cmbTimeOfWork);
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
		setHeight("340px");
		
		dFrom=new PopupDateField();
		dFrom.setWidth("110px");
		dFrom.setHeight("-1px");
		dFrom.setImmediate(true);
		dFrom.setDateFormat("dd-MM-yyyy");
		dFrom.setValue(new java.util.Date());
		dFrom.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(new Label("From :"),"top:50px; left:30px");
		mainLayout.addComponent(dFrom,"top:48px; left:130px");
		
		dTo=new PopupDateField();
		dTo.setWidth("110px");
		dTo.setHeight("-1px");
		dTo.setImmediate(true);
		dTo.setDateFormat("dd-MM-yyyy");
		dTo.setValue(new java.util.Date());
		dTo.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(new Label("To :"),"top:50px; left:245px");
		mainLayout.addComponent(dTo,"top:48px; left:280px");


		lblUnit = new Label("Project :");
		lblUnit.setImmediate(false);
		lblUnit.setHeight("-1px");
		mainLayout.addComponent(lblUnit,"top:80.0px; left:30.0px;");

		// cmbSection
		cmbUnit = new ComboBox();
		cmbUnit.setImmediate(true);
		cmbUnit.setWidth("260px");
		cmbUnit.setHeight("-1px");
		cmbUnit.setNullSelectionAllowed(false);
		cmbUnit.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbUnit, "top:78.0px; left:130.0px;");
		
		mainLayout.addComponent(new Label("Department :"),"top:110.0px; left:30.0px;");

		// cmbDepartment
		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("260px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setNullSelectionAllowed(false);
		cmbDepartment.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbDepartment, "top:108.0px; left:130.0px;");

		chkDepartmentAll = new CheckBox("All");
		chkDepartmentAll.setHeight("-1px");
		chkDepartmentAll.setWidth("-1px");
		chkDepartmentAll.setImmediate(true);
		mainLayout.addComponent(chkDepartmentAll, "top:110.0px; left:395.0px;");

		// lblSection
		lblSection = new Label("Section :");
		lblSection.setImmediate(false);
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection,"top:140px; left:30.0px;");

		// cmbSection
		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("260px");
		cmbSection.setHeight("-1px");
		cmbSection.setNullSelectionAllowed(false);
		cmbSection.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbSection, "top:138px; left:130.0px;");

		chkSectionAll = new CheckBox("All");
		chkSectionAll.setHeight("-1px");
		chkSectionAll.setWidth("-1px");
		chkSectionAll.setImmediate(true);
		mainLayout.addComponent(chkSectionAll, "top:140px; left:395.0px;");

		// cmbEmployeeName
		cmbEmployeeName = new ComboBox();
		cmbEmployeeName.setImmediate(false);
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
		chkEmployeeName.setVisible(false);
		
		cmbTimeOfWork = new ComboBox();
		cmbTimeOfWork.setImmediate(false);
		cmbTimeOfWork.setWidth("260px");
		cmbTimeOfWork.setHeight("-1px");
		cmbTimeOfWork.setNullSelectionAllowed(true);
		cmbTimeOfWork.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Time of Work: "),"top:200px; left:30.0px;");
		mainLayout.addComponent(cmbTimeOfWork, "top:198px; left:130.0px;");

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