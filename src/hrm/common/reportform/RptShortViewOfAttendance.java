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
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

public class RptShortViewOfAttendance extends Window{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	ArrayList<Component> allComp = new ArrayList<Component>();

	private ComboBox cmbUnit,cmbDepartmentName,cmbSectionName;

	private Label lblEmployee;
	private ComboBox cmbEmployee;
	private CheckBox chkDepartmentAll,chkSectionAll,chkEmployeeAll;

	private Label lblMonth;
	private ComboBox cmbDate;

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dfMonth=new SimpleDateFormat("MM");
	private SimpleDateFormat dfYear=new SimpleDateFormat("yyyy");
	SimpleDateFormat dfMonthYear = new SimpleDateFormat("MMMMM-yyyy");

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});
	private CommonMethod cm;
	private String menuId = "";
	public RptShortViewOfAttendance(SessionBean sessionBean,String menuId) 
	{
		this.sessionBean=sessionBean;
		this.setCaption("SHORT VIEW OF ATTENDANCE :: "+sessionBean.getCompany());
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		buildMainLayout();
		setContent(mainLayout);
		cmbAttandanceDateData();
		//addUnitData(); 
		setEventAction();
		focusMove();
		authenticationCheck();
	}
	private void cmbAttandanceDateData()
	{
		cmbDate.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct 0,CONVERT(date, (SELECT DATEADD(s,-1,DATEADD(mm, DATEDIFF(m,0,dDate)+1,0)))) dDate from tbEmployeeAttendanceFinal " +
					"order by dDate desc";
			
			System.out.println("cmbAttandanceDateData: "+query);
			
			List <?> list = session.createSQLQuery(query).list();
			if(!list.isEmpty())
			{
				for(Iterator <?> iter=list.iterator();iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();
					cmbDate.addItem(element[1]);
					cmbDate.setItemCaption(element[1], dfMonthYear.format(element[1]));
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbAttandanceDateData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			session.close();
		}
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
				{
					cmbEmployee.removeAllItems();
					addUnitData();
				}
			}
		});
		cmbUnit.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbDepartmentName.removeAllItems();				
				chkDepartmentAll.setValue(false);
				cmbDepartmentName.setEnabled(true);
				if(cmbUnit.getValue()!=null)
				{
					addDepartmentData();
				}
			}
		});
		cmbDepartmentName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSectionName.removeAllItems();
				chkSectionAll.setValue(false);
				cmbSectionName.setEnabled(true);
				if(cmbUnit.getValue()!=null)
				{
					if(cmbDepartmentName.getValue()!=null)
					{
						addSectionData();
					}
				}
			}
		});
		chkDepartmentAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(chkDepartmentAll.booleanValue())
				{
					cmbDepartmentName.setValue(null);
					cmbDepartmentName.setEnabled(false);
					if(cmbDate.getValue()!=null)
					{
						if(cmbUnit.getValue()!=null)
						{
							addSectionData();
						}
					}
				}
				else
					cmbDepartmentName.setEnabled(true);
			}
		});		

		cmbSectionName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbDate.getValue()!=null)
				{
					if(cmbUnit.getValue()!=null)
					{
						if(cmbDepartmentName.getValue()!=null || chkDepartmentAll.booleanValue())
						{
							if(cmbSectionName.getValue()!=null)
							{
								cmbEmployee.removeAllItems();
								addEmployeeName();
							}
						}
					}
				}
			}
		});		
		chkSectionAll.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(chkSectionAll.booleanValue())
				{
					cmbSectionName.setEnabled(false);
					if(cmbUnit.getValue()!=null)
					{
						if(cmbDepartmentName.getValue()!=null || chkDepartmentAll.booleanValue())
						{
							addEmployeeName();
						}
					}
				}
				else
				{
					cmbSectionName.setEnabled(true);
				}
			}
		});

		/*chkSectionAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(chkSectionAll.booleanValue())
				{
					cmbSectionName.setValue(null);
					cmbSectionName.setEnabled(false);
					if(cmbDate.getValue()!=null)
					{
						if(cmbUnit.getValue()!=null)
						{
							cmbEmployee.removeAllItems();
							addEmployeeName();
						}
					}
				}
				else
					cmbSectionName.setEnabled(true);
			}
		});	
		*/
		
		
		chkEmployeeAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(chkEmployeeAll.booleanValue())
				{
					cmbEmployee.setValue(null);
					cmbEmployee.setEnabled(false);
				}
				else
					cmbEmployee.setEnabled(true);
			}
		});
		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbUnit.getValue()!=null) 
				{
					if(cmbDepartmentName.getValue()!=null || chkDepartmentAll.booleanValue()) 
					{
						if(cmbSectionName.getValue()!=null || chkSectionAll.booleanValue()) 
						{
							if(cmbEmployee.getValue()!=null || chkEmployeeAll.booleanValue()) 
							{
								reportShow();
							}
							else
							{
								showNotification("Warning","Select Employee",Notification.TYPE_WARNING_MESSAGE);
							}
						}
						else
						{
							showNotification("Warning","Select Section",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else
					{
						showNotification("Warning","Select Department",Notification.TYPE_WARNING_MESSAGE);
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
	}
	public void addUnitData()
	{
		cmbUnit.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vUnitId,vUnitName from tbEmployeeAttendanceFinal where MONTH(dDate)=MONTH('"+cmbDate.getValue()+"') "+
					" order by vUnitName ";

			Iterator <?> itr=session.createSQLQuery(query).list().iterator();
			while(itr.hasNext())
			{
				Object [] element=(Object[])itr.next();
				cmbUnit.addItem(element[0]);
				cmbUnit.setItemCaption(element[0], element[1].toString());
			}
		}

		catch(Exception exp)
		{
			showNotification("addDepartmentData : ",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	public void addDepartmentData()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vDepartmentId,vDepartmentName from tbEmployeeAttendanceFinal where MONTH(dDate)=MONTH('"+cmbDate.getValue()+"')"+
					" and vUnitId='"+cmbUnit.getValue().toString()+"' "+
					" order by vDepartmentName";
			System.out.println("addDepartmentData: "+query);
			
			Iterator <?> itr=session.createSQLQuery(query).list().iterator();
			while(itr.hasNext())
			{
				Object [] element=(Object[])itr.next();
				cmbDepartmentName.addItem(element[0]);
				cmbDepartmentName.setItemCaption(element[0], element[1].toString());
			}
		}

		catch(Exception exp)
		{
			showNotification("addDepartmentData : ",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	public void addSectionData()
	{
		String dept="%";
		if(!chkDepartmentAll.booleanValue())
		{
			dept=cmbDepartmentName.getValue().toString();
		}
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vSectionId,vSectionName from tbEmployeeAttendanceFinal where MONTH(dDate)=MONTH('"+cmbDate.getValue()+"')"+
					" and vUnitId='"+cmbUnit.getValue().toString()+"' and vDepartmentId like'"+dept+"' "+
					" order by vSectionName";
			System.out.println("addSectionData: "+query);

			Iterator <?> itr=session.createSQLQuery(query).list().iterator();
			while(itr.hasNext())
			{
				Object [] element=(Object[])itr.next();
				cmbSectionName.addItem(element[0]);
				cmbSectionName.setItemCaption(element[0], element[1].toString());
			}
		}

		catch(Exception exp)
		{
			showNotification("addDepartmentData : ",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void addEmployeeName()
	{
		String dept="%",secId="%",empId="%";
		
		if(!chkDepartmentAll.booleanValue())
		{
			dept=cmbDepartmentName.getValue().toString();
		}
		if(cmbSectionName.getValue()!=null)
		{
			secId=cmbSectionName.getValue().toString();
		}
		if(!chkEmployeeAll.booleanValue())
		{
			//empId=cmbEmployee.getValue().toString();
		}
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		
		try
		{
			String query = "select distinct vEmployeeID,vEmployeeCode,vEmployeeName from tbEmployeeAttendanceFinal " +
					"where MONTH(dDate)=MONTH('"+cmbDate.getValue()+"') and vUnitId='"+cmbUnit.getValue().toString()+"' " +
					"and vDepartmentId like '"+dept+"' and vSectionId like '"+secId+"' and vEmployeeID like '"+empId+"' " +
					"order by vEmployeeCode asc";
			System.out.println("addEmployeeName: "+query);
			
			List <?> list = session.createSQLQuery(query).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbEmployee.addItem(element[0]);
				cmbEmployee.setItemCaption(element[0], element[1]+"-> "+element[2]);
			}
		}
		catch(Exception exp)
		{
			showNotification("addEmployeeName",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void reportShow()
	{
		String dept="%",secId="%",empId="%",report = "";
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		
		
		
		if(!chkDepartmentAll.booleanValue())
		{
			dept=cmbDepartmentName.getValue().toString();
		}
		if(!chkSectionAll.booleanValue())
		{
			secId=cmbSectionName.getValue().toString();
		}
		if(!chkEmployeeAll.booleanValue())
		{
			empId=cmbEmployee.getValue().toString();
		}
		
		if(dfMonth.format(cmbDate.getValue()).equalsIgnoreCase("02"))
		{
			int year=0;
			year=Integer.parseInt(dfYear.format(cmbDate.getValue()));
			if(year%4 == 0)
		    {
		        if( year%100 == 0)
		        {
		            if ( year%400 == 0)
		            {
				    	report="report/account/hrmModule/RptShortViewOfAttendance29.jasper";
					}
		            else
		            {
				    	report="report/account/hrmModule/RptShortViewOfAttendance28.jasper";
					}
		        }
		        else
		        {
			    	report="report/account/hrmModule/RptShortViewOfAttendance28.jasper";
				}
		    }
		    else			
			{
		    	report="report/account/hrmModule/RptShortViewOfAttendance28.jasper";
			}
		}
		
		
		else if(dfMonth.format(cmbDate.getValue()).equalsIgnoreCase("01"))
		{
			report="report/account/hrmModule/RptShortViewOfAttendance31.jasper";
		}
		else if(dfMonth.format(cmbDate.getValue()).equalsIgnoreCase("03"))
		{
			report="report/account/hrmModule/RptShortViewOfAttendance31.jasper";
		}		
		else if(dfMonth.format(cmbDate.getValue()).equalsIgnoreCase("05"))
		{
			report="report/account/hrmModule/RptShortViewOfAttendance31.jasper";
		}
		else if(dfMonth.format(cmbDate.getValue()).equalsIgnoreCase("07"))
		{
			report="report/account/hrmModule/RptShortViewOfAttendance31.jasper";
		}
		else if(dfMonth.format(cmbDate.getValue()).equalsIgnoreCase("08"))
		{
			report="report/account/hrmModule/RptShortViewOfAttendance31.jasper";
		}
		else if(dfMonth.format(cmbDate.getValue()).equalsIgnoreCase("10"))
		{
			report="report/account/hrmModule/RptShortViewOfAttendance31.jasper";
		}
		else if(dfMonth.format(cmbDate.getValue()).equalsIgnoreCase("12"))
		{
			report="report/account/hrmModule/RptShortViewOfAttendance31.jasper";
		}
		
		else 
		{
			report="report/account/hrmModule/RptShortViewOfAttendance30.jasper";
		}

		
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		try
		{			
			String query = "select * from RptShortViewOfAttendance('"+cmbDate.getValue()+"','"+cmbUnit.getValue().toString()+"','"+dept+"','"+secId+"','"+empId+"') " +
					"order by vDepartmentName,SUBSTRING(vEmployeeCode,3,100) asc";

			System.out.println("report : "+query);

			if(queryValueCheck(query)) 
			{
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("username", sessionBean.getUserName()+" "+sessionBean.getUserIp());
				hm.put("SysDate",reportTime.getTime);
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("date", cmbDate.getValue());
				hm.put("month",new SimpleDateFormat("MMMMMM").format(cmbDate.getValue()));
				hm.put("year",new SimpleDateFormat("yyyy").format(cmbDate.getValue()));
				hm.put("sql", query);
				Window win = new ReportViewer(hm,report,
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
		allComp.add(cmbDepartmentName);
		allComp.add(cmbSectionName);
		allComp.add(cmbEmployee);
		allComp.add(cButton.btnPreview);
		new FocusMoveByEnter(this,allComp);
	}

	private AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		setWidth("460px");
		setHeight("300px");

		lblMonth = new Label("Month :");
		lblMonth.setImmediate(false);
		lblMonth.setWidth("-1px");
		lblMonth.setHeight("-1px");
		mainLayout.addComponent(lblMonth, "top:30.0px; left:30.0px;");

		cmbDate = new ComboBox();
		cmbDate.setImmediate(false);
		cmbDate.setWidth("140px");
		cmbDate.setHeight("-1px");
		cmbDate.setNullSelectionAllowed(true);
		cmbDate.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbDate, "top:28.0px; left:130.0px;");

		cmbUnit=new ComboBox();
		cmbUnit.setImmediate(true);
		cmbUnit.setWidth("260.0px");
		cmbUnit.setHeight("24.0px");
		cmbUnit.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Project :"), "top:60.0px; left:30.0px;");
		mainLayout.addComponent(cmbUnit, "top:58.0px;left:130.0px;");

		cmbDepartmentName=new ComboBox();
		cmbDepartmentName.setImmediate(true);
		cmbDepartmentName.setWidth("260.0px");
		cmbDepartmentName.setHeight("24.0px");
		cmbDepartmentName.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Department :"), "top:90.0px; left:30.0px;");
		mainLayout.addComponent(cmbDepartmentName, "top:88.0px;left:130.0px;");

		chkDepartmentAll=new CheckBox("All");
		chkDepartmentAll.setImmediate(true);
		mainLayout.addComponent(chkDepartmentAll,"top:90.0px; left:395px");

		cmbSectionName=new ComboBox();
		cmbSectionName.setImmediate(true);
		cmbSectionName.setWidth("260.0px");
		cmbSectionName.setHeight("24.0px");
		cmbSectionName.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Section :"), "top:120.0px; left:30.0px;");
		mainLayout.addComponent(cmbSectionName, "top:118.0px;left:130.0px;");

		chkSectionAll=new CheckBox("All");
		chkSectionAll.setImmediate(true);
		mainLayout.addComponent(chkSectionAll,"top:120.0px; left:395px");

		lblEmployee = new Label();
		lblEmployee.setImmediate(false);
		lblEmployee.setWidth("100.0%");
		lblEmployee.setHeight("-1px");
		lblEmployee.setValue("Employee ID :");
		mainLayout.addComponent(lblEmployee,"top:150.0px; left:30.0px;");

		cmbEmployee = new ComboBox();
		cmbEmployee.setImmediate(false);
		cmbEmployee.setWidth("260px");
		cmbEmployee.setHeight("-1px");
		cmbEmployee.setNullSelectionAllowed(true);
		cmbEmployee.setImmediate(true);
		cmbEmployee.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbEmployee, "top:148.0px; left:130.0px;");

		chkEmployeeAll = new CheckBox("All");
		chkEmployeeAll.setHeight("-1px");
		chkEmployeeAll.setWidth("-1px");
		chkEmployeeAll.setImmediate(true);
		mainLayout.addComponent(chkEmployeeAll, "top:150.0px; left:396.0px;");

		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:180.0px;left:130.0px;");

		mainLayout.addComponent(new Label("______________________________________________________________________________"), "top:200.0px; left:20.0px; right:20.0px;");
		mainLayout.addComponent(cButton,"top:230.opx; left:150.0px");
		return mainLayout;
	}
}
