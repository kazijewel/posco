package hrm.common.reportform;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

import com.common.share.CommonButton;
import com.common.share.CommonMethod;
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
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class RptPFLoanApplication extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblEmpName;
	private Label lblAppDate;

	private ComboBox cmbEmpName,cmbUnit;
	private ComboBox cmbAppDate;

	private OptionGroup opgEmployee;
	private List<?> lstEmployee = Arrays.asList(new String[]{"Employee ID","Finger ID","Employee Name"});

	private CommonButton cButton = new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");

	private SimpleDateFormat dFormFormat = new SimpleDateFormat("dd-MM-yyyy");
	private ReportDate reportTime = new ReportDate();

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});
	private CommonMethod cm;
	private String menuId = "";
	public RptPFLoanApplication(SessionBean sessionBean,String menuId) 
	{
		this.sessionBean=sessionBean;
		this.setCaption("PF LOAN APPLICATION :: "+sessionBean.getCompany());
		this.setWidth("500px");
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		buildMainLayout();
		setContent(mainLayout);

		cmbUnitDataLoad();
		//cmbAddEmployeeName();
		setEventAction();
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
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "select vUnitId,vUnitName from tbEmpOfficialPersonalInfo";
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
	private void setEventAction()
	{	

		cmbUnit.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbUnit.getValue()!=null)
				{
					cmbAddEmployeeName();
				}
				else{
					cmbEmpName.setValue(null);
				}
			}
		});	
		cmbEmpName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbEmpName.getValue()!=null)
				{cmbAddApplicationNo(cmbEmpName.getValue().toString());}
			}
		});

		cButton.btnPreview.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbUnit.getValue()!=null)
				{
					if(cmbEmpName.getValue()!=null)
					{
						if(cmbAppDate.getValue()!=null)
						{
							reportpreview();
						}
						else
						{
							showNotification("Warning","Select Application Date",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else
					{
						showNotification("Warning","Select Employee Name",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					showNotification("Warning","Select Unit Name",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		cButton.btnExit.addListener(new Button.ClickListener() 
		{	
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

		opgEmployee.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmpName.removeAllItems();
				cmbAddEmployeeName();
			}
		});
	}

	private AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("490px");
		setHeight("250px");

		opgEmployee=new OptionGroup("",lstEmployee);
		opgEmployee.select("Employee ID");
		opgEmployee.setImmediate(true);
		opgEmployee.setStyleName("horizontal");
		mainLayout.addComponent(opgEmployee, "top:10.0px; left:50.0px;");

		cmbUnit=new ComboBox();
		cmbUnit.setWidth("250.0px");
		cmbUnit.setHeight("-1px");
		cmbUnit.setImmediate(true);
		mainLayout.addComponent(new Label("Project : "), "top:40.0px;left:30.0px;");
		mainLayout.addComponent(cmbUnit, "top:38.0px;left:135.0px");
		
		// lblEmpName
		lblEmpName = new Label("Employee Name :");
		lblEmpName.setImmediate(false);
		lblEmpName.setWidth("100.0%");
		lblEmpName.setHeight("-1px");
		mainLayout.addComponent(lblEmpName,"top:70.0px; left:30.0px;");

		// cmbEmpName
		cmbEmpName = new ComboBox();
		cmbEmpName.setImmediate(true);
		cmbEmpName.setWidth("250.0px");
		cmbEmpName.setHeight("-1px");
		cmbEmpName.setNullSelectionAllowed(true);
		cmbEmpName.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbEmpName, "top:68.0px; left:135.0px;");

		// lblAppDate
		lblAppDate = new Label("Application Date :");
		lblAppDate.setImmediate(false);
		lblAppDate.setWidth("100.0%");
		lblAppDate.setHeight("-1px");
		mainLayout.addComponent(lblAppDate,"top:100.0px; left:30.0px;");

		// cmbAppDate
		cmbAppDate = new ComboBox();
		cmbAppDate.setImmediate(true);
		cmbAppDate.setWidth("110px");
		cmbAppDate.setHeight("-1px");
		mainLayout.addComponent(cmbAppDate, "top:98.0px; left:135.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:130.0px;left:135.0px;");
		RadioBtnGroup.setVisible(false);

		//mainLayout.addComponent(new Label("____________________________________________________________________________"), "top:140.0px;left:20.0px;right:20.0px;");
		mainLayout.addComponent(cButton,"top:160.opx; left:120.0px");

		return mainLayout;
	}

	private void cmbAddEmployeeName()
	{
		cmbEmpName.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String querySection = " Select b.vAutoEmployeeId, a.vEmployeeCode from tbEmpOfficialPersonalInfo as a inner join " +
					"tbPFLoanApplication as b on a.vEmployeeId=b.vAutoEmployeeId "
					+ " where a.vUnitId='"+cmbUnit.getValue()+"' order by b.iAutoId ";
			lblEmpName.setValue("Employee ID :");
			
			if(opgEmployee.getValue()=="Employee Name")
			{
				querySection = "Select b.vAutoEmployeeId, a.vEmployeeName from tbEmpOfficialPersonalInfo as a inner join " +
					"tbPFLoanApplication as b on a.vEmployeeId=b.vAutoEmployeeId "
					+ " where a.vUnitId='"+cmbUnit.getValue()+"' order by b.iAutoId";
				lblEmpName.setValue("Employee Name :");
			}
			
			else if(opgEmployee.getValue()=="Finger ID")
			{
				querySection = "Select b.vAutoEmployeeId, a.vFingerId from tbEmpOfficialPersonalInfo as a inner join " +
					"tbPFLoanApplication as b on a.vEmployeeId=b.vAutoEmployeeId "
					+ " where a.vUnitId='"+cmbUnit.getValue()+"' order by b.iAutoId";
				lblEmpName.setValue("Finger ID :");
			}
			System.out.println("Employee: "+querySection);
			List <?> list = session.createSQLQuery(querySection).list();	
			for (Iterator <?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element =  (Object[]) iter.next();	

				cmbEmpName.addItem(element[0]);
				cmbEmpName.setItemCaption(element[0], element[1].toString());	
			}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}	

	private void cmbAddApplicationNo(String empId)
	{
		cmbAppDate.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String querySection = " select vLoanNo,dApplicationDate from tbPFLoanApplication where vAutoEmployeeId='"+empId+"' ";
			List <?> list = session.createSQLQuery(querySection).list();	
			for (Iterator <?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element =  (Object[]) iter.next();	

				cmbAppDate.addItem(element[0]);
				cmbAppDate.setItemCaption(element[0], dFormFormat.format(element[1]));	
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void reportpreview()
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		try
		{
			HashMap <String,Object> hm = new HashMap <String,Object> ();
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("userName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			hm.put("SysDate",reportTime.getTime);
			hm.put("logo", sessionBean.getCompanyLogo());

			String query = " SELECT * from funPFLoanApplicationInfo ('"+cmbEmpName.getValue().toString()+"','"+(cmbAppDate.getValue())+"') "
					+ " where vUnitId='"+cmbUnit.getValue().toString()+"'";
			System.out.println("Report: "+query);
			if(queryValueCheck(query))
			{
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/account/hrmModule/rptPFLoanApplication.jasper",
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
			this.getParent().showNotification("Error",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
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
}
