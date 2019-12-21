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
import com.vaadin.ui.AbstractSelect.Filtering;
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
public class RptDailyAttendance  extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	ArrayList<Component> allComp = new ArrayList<Component>();

	private Label lblMonth;
	private PopupDateField dDate;
	private ComboBox cmbUnit;
	private ComboBox cmbDepartmentName;
	private ComboBox cmbDesignation,cmbDate;
	private ComboBox cmbSectionName;
	private CheckBox chkDepartment;
	private CheckBox chkSection;
	private CheckBox chkDesignation;
	private OptionGroup opgOrderBy;
	private static final List<String> orderType = Arrays.asList(new String[]{"Employee ID","Employee Name"});
	private static final List<String> attType = Arrays.asList(new String[]{"Present employee","All employee"});
	private OptionGroup opgAttendance;

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	private SimpleDateFormat dDBDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dDateFormat = new SimpleDateFormat("dd-MM-yy");

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});
	private CommonMethod cm;
	private String menuId = "";
	public RptDailyAttendance(SessionBean sessionBean,String menuId) 
	{
		this.sessionBean=sessionBean;
		this.setCaption("DAILY ATTENDANCE STATEMENT :: "+sessionBean.getCompany());
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		buildMainLayout();
		setContent(mainLayout);
		addDate();
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
		cmbDate.addListener(new ValueChangeListener()
		{	
			public void valueChange(ValueChangeEvent event)
			{
				cmbUnit.removeAllItems();
				if(cmbDate.getValue()!=null)
					addUnitName();
			}
		});
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
				if(cmbUnit.getValue()!=null)
				{
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
				else
				{
					chkDepartment.setValue(false);
					
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
				else
				{
					chkSection.setValue(false);
				}
			}
		});
		chkDesignation.addListener(new ValueChangeListener()
		{	
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbSectionName.getValue()!=null || chkSection.booleanValue())
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
				else
				{
					chkDesignation.setValue(false);
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
	private void addDate()
	{
		cmbDate.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct dDate,ISNULL(dDate,'1900-01-01')dates from tbEmployeeAttendanceFinal order by dDate desc";
			List <?> list = session.createSQLQuery(query).list();

			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbDate.addItem(element[0]);
				cmbDate.setItemCaption(element[0], sessionBean.dfBd.format(element[1]));
			}
		}
		catch(Exception exp)
		{
			showNotification("addUnitName",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	public void addUnitName()
	{
		cmbUnit.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select vUnitId,vUnitName from tbEmployeeAttendanceFinal  where dDate='"+cmbDate.getValue()+"' order by vUnitName";
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
			String query="select distinct vDepartmentId,vDepartmentName from tbEmployeeAttendanceFinal "+
					" where vUnitId like '"+cmbUnit.getValue().toString()+"' and dDate='"+cmbDate.getValue()+"' "+
					" order by vDepartmentName";
			List <?> list = session.createSQLQuery(query).list();

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
			String query="select distinct vSectionId,vSectionName from tbEmployeeAttendanceFinal "+
					" where vUnitId='"+cmbUnit.getValue().toString()+"' "
				  + " and vDepartmentId like '"+(chkDepartment.booleanValue()?"%":cmbDepartmentName.getValue())+"' and dDate='"+cmbDate.getValue()+"' "+
					" order by vSectionName";
			List <?> list = session.createSQLQuery(query).list();

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
			String query="select ds.vDesignationId,ds.vDesignation from tbEmployeeAttendanceFinal eaf inner join " +
					" tbDesignationInfo ds on ds.vDesignationId=eaf.vDesignationID "+
					" where dDate='"+cmbDate.getValue()+"' and vUnitId='"+cmbUnit.getValue().toString()+"' " +
					" and vDepartmentId like '"+(chkDepartment.booleanValue()?"%":cmbDepartmentName.getValue())+"' "
				  + " and vSectionId like '"+(chkSection.booleanValue()?"%":cmbSectionName.getValue())+"' order by ds.iDesignationSerial";
			List <?> list = session.createSQLQuery(query).list();

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
		String query=null,logo="",isPresent="",checkPresent="0";
		try
		{
			
			
			
			query="select * from funDailyAttendanceForAttendanceReport('"+cmbDate.getValue()+"',"
					+ " '"+cmbDate.getValue()+"',"
					+ " '"+cmbUnit.getValue()+"',"
					+ " '"+(chkDepartment.booleanValue()?"%":cmbDepartmentName.getValue())+"',"
					+ " '"+(chkSection.booleanValue()?"%":cmbSectionName.getValue())+"',"
					+ " '"+(chkDesignation.booleanValue()?"%":cmbDesignation.getValue())+"','%')  "
					+ " "+(opgAttendance.getValue().toString().equals("All employee")?"where iPresentCount like '%' ":"where iPresentCount>0 ")+" "
					+ " order by vUnitName,vDepartmentName,iRank,djoiningDate";
			System.out.println(query);
			if(queryValueCheck(query))
			{

				HashMap <String, Object> hm = new HashMap<String, Object>();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("username", sessionBean.getUserName()+" "+sessionBean.getUserIp());
				hm.put("SysDate",reportTime.getTime);
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("date", cmbDate.getItemCaption(cmbDate.getValue()));
				hm.put("type", opgAttendance.getValue().toString());
				hm.put("sql", query);
				hm.put("isPresent",checkPresent);

				Window win = new ReportViewer(hm,"report/account/hrmModule/rptDailyAttendence.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",RadioBtn.Radio);

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
		allComp.add(cmbDate);
		allComp.add(cmbUnit);
		allComp.add(cmbDepartmentName);
		allComp.add(cmbSectionName);
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
		setWidth("460px");
		setHeight("300px");

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
		//mainLayout.addComponent(dDate, "top:08.0px; left:130.0px;");

		cmbDate = new ComboBox();
		cmbDate.setWidth("110px");
		cmbDate.setHeight("-1px");
		cmbDate.setNullSelectionAllowed(false);
		cmbDate.setImmediate(true);
		cmbDate.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbDate, "top:08.0px; left:130.0px;");

		cmbUnit = new ComboBox();
		cmbUnit.setWidth("260px");
		cmbUnit.setHeight("-1px");
		cmbUnit.setNullSelectionAllowed(false);
		cmbUnit.setImmediate(true);
		mainLayout.addComponent(new Label("Project Name : "), "top:40.0px; left:20.0px;");
		mainLayout.addComponent(cmbUnit, "top:38.0px; left:130.0px;");

		// cmbEmployee
		cmbDepartmentName = new ComboBox();
		cmbDepartmentName.setWidth("260px");
		cmbDepartmentName.setHeight("-1px");
		cmbDepartmentName.setNullSelectionAllowed(true);
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
		cmbSectionName.setNullSelectionAllowed(true);
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
		cmbDesignation.setNullSelectionAllowed(true);
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
		//mainLayout.addComponent(new Label("Order By : "), "top:160.0px; left:20.0px;");
		//mainLayout.addComponent(opgOrderBy, "top:160.0px; left:130.0px;");

		opgAttendance = new OptionGroup("",attType);
		opgAttendance.setImmediate(true);
		opgAttendance.setValue("Present employee");
		opgAttendance.setStyleName("horizontal");
		//mainLayout.addComponent(new Label("Order By : "), "top:160.0px; left:20.0px;");
		mainLayout.addComponent(opgAttendance, "top:160px; left:130.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:160px;left:130.0px;");
		RadioBtnGroup.setVisible(false);


		//mainLayout.addComponent(new Label("___________________________________________________________________________________________"), "top:210.0px;left:20.0px;right:20.0px;");
		mainLayout.addComponent(cButton,"bottom:20px; left:140.0px");

		return mainLayout;
	}
}
