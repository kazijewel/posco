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
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class RptDailyAbsentReport  extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	ArrayList<Component> allComp = new ArrayList<Component>();

	private Label lblMonth;
	private PopupDateField dDate;
	private ComboBox cmbUnit;
	private ComboBox cmbDepartmentName,cmbSectionName;
	private ComboBox cmbDesignation;

	private CheckBox chkDepartment,chkSection;
	private CheckBox chkDesignation;
	private OptionGroup opgOrderBy;
	private ComboBox cmbDate;
	private static final List<String> orderType = Arrays.asList(new String[]{"Finger ID","Employee Name"});

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	private SimpleDateFormat dDBDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dDateFormat = new SimpleDateFormat("dd-MM-yy");

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});
	private CommonMethod cm;
	private String menuId = "";
	public RptDailyAbsentReport(SessionBean sessionBean,String menuId) 
	{
		this.sessionBean=sessionBean;
		this.setCaption("DAILY ABSENT STATEMENT:: "+sessionBean.getCompany());
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		buildMainLayout();
		setContent(mainLayout);
		addUnitName();
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
		cmbUnit.addListener(new ValueChangeListener()
		{	
			public void valueChange(ValueChangeEvent event)
			{
				
				if(cmbUnit.getValue()!=null)
					addDepartmentName();
			}
		});

		cmbDepartmentName.addListener(new ValueChangeListener()
		{	
			public void valueChange(ValueChangeEvent event)
			{
				
				if(cmbDepartmentName.getValue()!=null)
				{
					addSectionName();
				}
			}
		});
		chkDepartment.addListener(new ValueChangeListener()
		{	
			public void valueChange(ValueChangeEvent event)
			{
				cmbSectionName.removeAllItems();
				if(chkDepartment.booleanValue())
				{
					cmbDepartmentName.setValue(null);
					cmbDepartmentName.setEnabled(false);
					addSectionName();
				}
				else
				{
					cmbDepartmentName.setEnabled(true);
				}
			}
		});
		cmbSectionName.addListener(new ValueChangeListener()
		{	
			public void valueChange(ValueChangeEvent event)
			{
				
				if(cmbSectionName.getValue()!=null)
				{
					addDesignationName();
				}
			}
		});
		chkSection.addListener(new ValueChangeListener()
		{	
			public void valueChange(ValueChangeEvent event)
			{
				cmbDesignation.removeAllItems();
				if(cmbDepartmentName.getValue()!=null || chkDepartment.booleanValue())
				{
					if(chkSection.booleanValue())
					{
						cmbSectionName.setValue(null);
						cmbSectionName.setEnabled(false);
						addDesignationName();
					}
					else
					{
						cmbSectionName.setEnabled(true);
					}
				}
			}
		});
		chkDesignation.addListener(new ValueChangeListener()
		{	
			public void valueChange(ValueChangeEvent event)
			{
				if(chkDesignation.booleanValue())
				{
					cmbDesignation.setValue(null);
					cmbDesignation.setEnabled(false);
				}
				else
				{
					cmbDesignation.setEnabled(true);
				}
			}
		});


		cButton.btnPreview.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				if(dDate.getValue()!=null)
				{
					if(cmbUnit.getValue()!=null)
					{
						if(cmbDepartmentName.getValue()!=null || chkDepartment.booleanValue())
						{
							if(cmbSectionName.getValue()!=null || chkSection.booleanValue())
							{
								if(cmbDesignation.getValue()!=null || chkDesignation.booleanValue())
								{
									reportShow();
								}
								else
								{
									showNotification("Warning","Select Designation!!!",Notification.TYPE_WARNING_MESSAGE);
								}
							}
							else
							{
								showNotification("Warning","Select Section!!!",Notification.TYPE_WARNING_MESSAGE);
							}
						}
						else
						{
							showNotification("Warning","Select Department!!!",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else
					{
						showNotification("Warning","Select Project!!!",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					showNotification("Warning","Select Date!!!",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		cButton.btnExit.addListener( new ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});
	}

	public void addUnitName()
	{
		cmbUnit.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select vUnitId,vUnitName from tbEmpOfficialPersonalInfo where bStatus=1 order by vUnitName";
			List <?> list = session.createSQLQuery(query).list();

			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbUnit.addItem(element[0]);
				cmbUnit.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("addUnitName",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void addDepartmentName()
	{
		cmbDepartmentName.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vDepartmentId,vDepartmentName from tbEmpOfficialPersonalInfo where " +
					" bStatus=1 and vUnitId='"+cmbUnit.getValue().toString()+"' order by vDepartmentName";
			List <?> list = session.createSQLQuery(query).list();
			System.out.println("Secion :"+query);
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbDepartmentName.addItem(element[0]);
				cmbDepartmentName.setItemCaption(element[0], element[1].toString());
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
		cmbSectionName.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vSectionId,vSectionName from tbEmpOfficialPersonalInfo where " +
					" bStatus=1 and vUnitId='"+cmbUnit.getValue().toString()+"' "
				  + " and vDepartmentId like '"+(chkDepartment.booleanValue()?"%":cmbDepartmentName.getValue())+"' order by vSectionName";
			List <?> list = session.createSQLQuery(query).list();
			System.out.println("Secion :"+query);
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbSectionName.addItem(element[0]);
				cmbSectionName.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("addSectionName",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void addDesignationName()
	{
		cmbDesignation.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vDesignationId,vDesignationName from tbEmpOfficialPersonalInfo " +
					" where bStatus=1 and vUnitId = '"+cmbUnit.getValue().toString()+"' " +
					" and vDepartmentId like '"+(chkDepartment.booleanValue()?"%":cmbDepartmentName.getValue())+"'  " +
			        " and vSectionId like '"+(chkSection.booleanValue()?"%":cmbSectionName.getValue())+"' order by vDesignationName ";
			List <?> list = session.createSQLQuery(query).list();
			System.out.println("designation :"+query);
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbDesignation.addItem(element[0]);
				cmbDesignation.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("addDesignationName",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}


	private void reportShow()
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());


		String query=null;
		try
		{
			
			
			HashMap <String, Object> hm = new HashMap<String, Object>();
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("developer", sessionBean.getDeveloperAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("username", sessionBean.getUserName()+" "+sessionBean.getUserIp());
			hm.put("SysDate",reportTime.getTime);
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("date", dDateFormat.format(dDate.getValue()));
			
			query="select * from funDailyAbsent('"+cmbUnit.getValue()+"',"
					+ " '"+(chkDepartment.booleanValue()?"%":cmbDepartmentName.getValue())+"',"
					+ " '"+(chkSection.booleanValue()?"%":cmbSectionName.getValue())+"',"
					+ " '"+(chkDesignation.booleanValue()?"%":cmbDesignation.getValue())+"',"
					+ " '"+sessionBean.dfDb.format(dDate.getValue())+"') order by vUnitName,vDepartmentName,iRank,dJoiningDate";

			
			System.out.println(query);
			if(queryValueCheck(query))
			{
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/account/hrmModule/rptDailyAbsent.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",true);

				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);
				win.setStyleName("cwindow");
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
		allComp.add(dDate);
		allComp.add(cmbDepartmentName);
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
		setHeight("280px");

		lblMonth = new Label("Date :");
		lblMonth.setImmediate(false);
		lblMonth.setWidth("-1px");
		lblMonth.setHeight("-1px");
		mainLayout.addComponent(lblMonth, "top:10.0px; left:20.0px;");

		dDate = new PopupDateField();
		dDate.setImmediate(true);
		dDate.setWidth("110px");
		dDate.setDateFormat("dd-MM-yyyy");
		dDate.setValue(new java.util.Date());
		dDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dDate, "top:08.0px; left:130.0px;");

		cmbUnit = new ComboBox();
		cmbUnit.setWidth("260px");
		cmbUnit.setHeight("-1px");
		cmbUnit.setNullSelectionAllowed(false);
		cmbUnit.setImmediate(true);
		mainLayout.addComponent(new Label("Project : "), "top:40.0px; left:20.0px;");
		mainLayout.addComponent(cmbUnit, "top:38.0px; left:130.0px;");

		// cmbEmployee
		cmbDepartmentName = new ComboBox();
		cmbDepartmentName.setWidth("260px");
		cmbDepartmentName.setHeight("-1px");
		cmbDepartmentName.setNullSelectionAllowed(false);
		cmbDepartmentName.setImmediate(true);
		mainLayout.addComponent(new Label("Department : "), "top:70.0px; left:20.0px;");
		mainLayout.addComponent(cmbDepartmentName, "top:68.0px; left:130.0px;");
		
		
		chkDepartment = new CheckBox("All");
		chkDepartment.setHeight("-1px");
		chkDepartment.setWidth("-1px");
		chkDepartment.setImmediate(true);
		mainLayout.addComponent(chkDepartment, "top:68.0px; left:395.0px;");
		
		cmbSectionName = new ComboBox();
		cmbSectionName.setWidth("260px");
		cmbSectionName.setHeight("-1px");
		cmbSectionName.setNullSelectionAllowed(false);
		cmbSectionName.setImmediate(true);
		mainLayout.addComponent(new Label("Section : "), "top:100px; left:20.0px;");
		mainLayout.addComponent(cmbSectionName, "top:98px; left:130.0px;");
		
		
		chkSection = new CheckBox("All");
		chkSection.setHeight("-1px");
		chkSection.setWidth("-1px");
		chkSection.setImmediate(true);
		mainLayout.addComponent(chkSection, "top:100px; left:395.0px;");

		cmbDesignation = new ComboBox();
		cmbDesignation.setWidth("260px");
		cmbDesignation.setHeight("-1px");
		cmbDesignation.setNullSelectionAllowed(false);
		cmbDesignation.setImmediate(true);
		mainLayout.addComponent(new Label("Designation : "), "top:130px; left:20.0px;");
		mainLayout.addComponent(cmbDesignation, "top:128px; left:130.0px;");

		chkDesignation = new CheckBox("All");
		chkDesignation.setHeight("-1px");
		chkDesignation.setWidth("-1px");
		chkDesignation.setImmediate(true);
		mainLayout.addComponent(chkDesignation, "top:130px; left:395.0px;");


		opgOrderBy = new OptionGroup("",orderType);
		opgOrderBy.setImmediate(true);
		opgOrderBy.setValue("Finger ID");
		opgOrderBy.setStyleName("horizontal");
		//mainLayout.addComponent(new Label("Order By : "), "top:128.0px; left:20.0px;");
		//mainLayout.addComponent(opgOrderBy, "top:130.0px; left:130.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:130px;left:130.0px;");
		RadioBtnGroup.setVisible(false);


		//mainLayout.addComponent(new Label("___________________________________________________________________________________________"), "top:190.0px;left:20.0px;right:20.0px;");
		mainLayout.addComponent(cButton,"bottom:20px; left:140.0px");

		return mainLayout;
	}
}
