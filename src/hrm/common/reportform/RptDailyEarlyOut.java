package hrm.common.reportform;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class RptDailyEarlyOut extends Window
{
	private ComboBox cmbUnit;
	private ComboBox cmbDepartment,cmbDate,cmbSection;
	private CheckBox chkDepartmentAll,chkSectionAll;
	private PopupDateField dDate;
	private AbsoluteLayout mainLayout;
	SessionBean sessionbean;
	private OptionGroup btnCommon;
	private CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private OptionGroup reportType;
	private List<String> type=Arrays.asList(new String[]{"Late"/*,"Over Late"*/});
	private static final List<String> lstType=Arrays.asList(new String[]{"PDF","Others"});
	private ReportDate reportdate=new ReportDate();
	private ArrayList<Component> allComp=new ArrayList<Component>();
	SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
	private CommonMethod cm;
	private String menuId = "";
	public RptDailyEarlyOut(SessionBean sessionbean,String menuId)
	{
		this.sessionbean=sessionbean;
		this.setCaption("DAILY EARLY OUT EMPLOYEE LIST :: "+sessionbean.getCompany());
		this.setWidth("475px");
		this.setHeight("300px");
		this.setResizable(false);
		cm = new CommonMethod(sessionbean);
		this.menuId = menuId;
		buildMainLayout();
		this.setContent(mainLayout);
		setEventAction();
		addDate();
		focusMove();
		authenticationCheck();
	}
	private void authenticationCheck()
	{
		cm.checkFormAction(menuId);
		if(!sessionbean.isSuperAdmin())
		{
		if(!sessionbean.isAdmin())
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
		allComp.add(cmbDepartment);
		allComp.add(cmbSection);
		allComp.add(cButton.btnPreview);
		new FocusMoveByEnter(this, allComp);
	}

	private void cmbUnitdataload()
	{
		cmbUnit.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql="select distinct vUnitId,vUnitName from tbEmployeeAttendanceFinal " +
					"where CONVERT(date,dDate)='"+dateFormat.format(cmbDate.getValue())+"' order by vUnitName";
			Iterator <?> itr=session.createSQLQuery(sql).list().iterator();
			while(itr.hasNext())
			{

				Object[] element=(Object[])itr.next();
				cmbUnit.addItem(element[0]);
				cmbUnit.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbUnitdataload",exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void cmbDepartmentdataload()
	{
		cmbDepartment.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql="select distinct vDepartmentId,vDepartmentName from tbEmployeeAttendanceFinal " +
					"where CONVERT(date,dDate)='"+dateFormat.format(cmbDate.getValue())+"' and " +
					"vUnitId='"+cmbUnit.getValue().toString()+"' order by vDepartmentName";
			Iterator <?> itr=session.createSQLQuery(sql).list().iterator();

			System.out.println("Department"+sql);	

			while(itr.hasNext())
			{
				Object[] element=(Object[])itr.next();
				cmbDepartment.addItem(element[0]);
				cmbDepartment.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbDepartmentdataload",exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void cmbSectiondataload()
	{
		cmbSection.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql="select distinct vSectionId,vSectionName from tbEmployeeAttendanceFinal " 
					+" where CONVERT(date,dDate)='"+dateFormat.format(cmbDate.getValue())+"' "
					+" and vUnitId='"+cmbUnit.getValue().toString()+"' "
					+" and vDepartmentId like '"+(chkDepartmentAll.booleanValue()?"%":cmbDepartment.getValue())+"' "
					+ " order by vSectionName";
			Iterator <?> itr=session.createSQLQuery(sql).list().iterator();

			System.out.println("Section"+sql);	

			while(itr.hasNext())
			{
				Object[] element=(Object[])itr.next();
				cmbSection.addItem(element[0]);
				cmbSection.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbSectiondataload",exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void setEventAction()
	{
		cmbDate.addListener(new ValueChangeListener()
		{	
			public void valueChange(ValueChangeEvent event)
			{
				cmbUnit.removeAllItems();
				if(cmbDate.getValue()!=null)
					cmbUnitdataload();
			}
		});

		cmbUnit.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbUnit.getValue()!=null)
				{
					cmbDepartmentdataload();
				}
			}
		});
		cmbDepartment.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
				if(cmbDepartment.getValue()!=null)
				{
					cmbSectiondataload();
				}
			}
		});
		chkDepartmentAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSection.removeAllItems();
				if(cmbUnit.getValue()!=null)
				{
					if(chkDepartmentAll.booleanValue())
					{
						cmbDepartment.setValue(null);
						cmbDepartment.setEnabled(false);
						cmbSectiondataload();
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
		chkSectionAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				
				if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
				{
					if(chkSectionAll.booleanValue())
					{
						cmbSection.setValue(null);
						cmbSection.setEnabled(false);
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

		cButton.btnPreview.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(cmbDate.getValue()!=null)
				{

					if(cmbUnit.getValue()!=null)
					{
						if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
						{
							if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
							{
								reportShow();
							}
							else
							{
								showNotification("Warning","Please Select Section!!!", Notification.TYPE_WARNING_MESSAGE);
							}
						}
						else
						{
							showNotification("Warning","Please Select Department!!!", Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else
					{
						showNotification("Warning","Please Select Project!!!", Notification.TYPE_WARNING_MESSAGE);
					}

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
	}
	private void addDate()
	{
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
				cmbDate.setItemCaption(element[0], sessionbean.dfBd.format(element[1]));
			}
		}
		catch(Exception exp)
		{
			showNotification("addUnitName",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void reportShow()
	{
		ReportOption RadioBtn= new ReportOption(reportType.getValue().toString());
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		
		try
		{ 

			String query = "select * from funDailyEmployeeAttendance('"+cmbDate.getValue()+"','"+cmbUnit.getValue()+"',"
					+ " '"+(chkDepartmentAll.booleanValue()?"%":cmbDepartment.getValue())+"','"+(chkSectionAll.booleanValue()?"%":cmbSection.getValue())+"','%','%')"+
					" where iEarlyOutCount>0 order by  vDepartmentName,iRank,dJoiningDate";
			System.out.println(query);
			List <?> lst=session.createSQLQuery(query).list();

			System.out.println("Report Late In "+query);

			if(!lst.isEmpty())
			{
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				hm.put("company", sessionbean.getCompany());
				hm.put("address", sessionbean.getCompanyAddress());
				hm.put("phone", sessionbean.getCompanyContact());
				hm.put("developer", sessionbean.getDeveloperAddress());
				hm.put("username", sessionbean.getUserName()+" "+sessionbean.getUserIp());
				hm.put("SysDate",reportdate.getTime);
				hm.put("logo", sessionbean.getCompanyLogo());
				hm.put("date", cmbDate.getItemCaption(cmbDate.getValue()));
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/account/hrmModule/rptDailyEarlyOutAttendence.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",true);

				win.setCaption("Daily Early Out Report");
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
		finally{session.close();}
	}

	private AbsoluteLayout buildMainLayout()
	{
		mainLayout=new AbsoluteLayout();
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");

		btnCommon=new OptionGroup("",type);
		btnCommon.setImmediate(true);
		btnCommon.setValue("Late");
		btnCommon.setStyleName("horizontal");
		//mainLayout.addComponent(btnCommon,"top:10px; left:140px");

		dDate=new PopupDateField();
		dDate.setImmediate(true);
		dDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dDate.setWidth("110px");
		dDate.setDateFormat("dd-MM-yyyy");
		dDate.setValue(new Date());
		mainLayout.addComponent(new Label("Date : "), "top:40.0px;left:30.0px;");
		//mainLayout.addComponent(dDate, "top:38.0px;left:140.0px;");

		cmbDate = new ComboBox();
		cmbDate.setWidth("120px");
		cmbDate.setHeight("-1px");
		cmbDate.setNullSelectionAllowed(false);
		cmbDate.setImmediate(true);
		cmbDate.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbDate, "top:38px; left:140px;");

		cmbUnit=new ComboBox();
		cmbUnit.setImmediate(true);
		cmbUnit.setWidth("260px");
		mainLayout.addComponent(new Label("Project : "), "top:70.0px;left:30.0px;");
		mainLayout.addComponent(cmbUnit, "top:68.0px;left:140.0px;");

		cmbDepartment=new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("260px");
		mainLayout.addComponent(new Label("Department : "), "top:100.0px;left:30.0px;");
		mainLayout.addComponent(cmbDepartment, "top:98.0px;left:140.0px;");

		chkDepartmentAll=new CheckBox("All");
		chkDepartmentAll.setImmediate(true);
		mainLayout.addComponent(chkDepartmentAll, "top:100.0px;left:405.0px;");
		
		cmbSection=new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("260px");
		mainLayout.addComponent(new Label("Section : "), "top:130px;left:30.0px;");
		mainLayout.addComponent(cmbSection, "top:128px;left:140.0px;");

		chkSectionAll=new CheckBox("All");
		chkSectionAll.setImmediate(true);
		mainLayout.addComponent(chkSectionAll, "top:130px;left:405.0px;");

		reportType=new OptionGroup("",lstType);
		reportType.setStyleName("horizontal");
		reportType.setImmediate(true);
		reportType.select("PDF");
		mainLayout.addComponent(reportType, "top:160.0px;left:140.0px;");
		reportType.setVisible(false);

		//mainLayout.addComponent(new Label("____________________________________________________________________________________________"), "top:180.0px;right:20.0px;left:20.0px;");
		mainLayout.addComponent(cButton, "bottom:20px;left:160.0px;");
		return mainLayout;
	}
}
