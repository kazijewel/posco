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
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class RptTourApplication extends Window {

	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblFromDate;

	private Label lblEmpID;
	private ComboBox cmbEmpID;


	private PopupDateField dTourFrom;
	private PopupDateField dTourTo;

	private OptionGroup opgEmployee;
	private List<?> lstEmployee = Arrays.asList(new String[]{"Employee ID","Employee Name"});

	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");

	ArrayList<Component> allComp = new ArrayList<Component>();

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});
	private CommonMethod cm;
	private String menuId = "";
	public RptTourApplication(SessionBean sessionBean,String menuId)
	{
		this.sessionBean=sessionBean;
		this.setCaption("TOUR APPLICATION :: "+sessionBean.getCompany());
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		buildMainLayout();
		setContent(mainLayout);
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

	private void EmployeeDataAdd()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		cmbEmpID.removeAllItems();
		try
		{
			String query = "select vEmployeeID,vEmployeeCode from tbTourApplication where " +
					"dTourFrom >='"+dFormat.format(dTourFrom.getValue())+"' and " +
					"dTourTo<='"+dFormat.format(dTourTo.getValue())+"'and vApprovedFlag ='1' " +
					"order by vEmployeeCode";
			lblEmpID.setValue("Employee ID :");

			if(opgEmployee.getValue()=="Employee Name")
			{
				query = "select vEmployeeID,vEmployeeName from tbTourApplication where " +
						"dTourFrom >='"+dFormat.format(dTourFrom.getValue())+"' and " +
						"dTourTo<='"+dFormat.format(dTourTo.getValue())+"'and vApprovedFlag ='1' " +
						"order by vEmployeeName";
				lblEmpID.setValue("Employee Name :");
			}

			System.out.println("EmployeeDataAdd: "+query);
			
			List <?> lst=session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				Iterator <?> itr=lst.iterator();
				while(itr.hasNext())
				{
					Object [] element=(Object[])itr.next();
					cmbEmpID.addItem(element[0]);
					cmbEmpID.setItemCaption(element[0], element[1].toString());
				}
			}
			else
				showNotification("Warning","No Employee Found!!!",Notification.TYPE_WARNING_MESSAGE);

		}
		catch (Exception exp)
		{
			showNotification("EmployeeDataAdd",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void setEventAction()
	{
		dTourFrom.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(dTourFrom.getValue()!=null)
				{
					EmployeeDataAdd();
				}
			}
		});

		dTourTo.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmpID.removeAllItems();
				if(dTourTo.getValue()!=null)
				{
					EmployeeDataAdd();
				}
			}
		});

		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(dTourFrom.getValue()!=null && dTourTo.getValue()!=null)
				{
					if(cmbEmpID.getValue()!=null)
						reportShow();
				}
			}
		});

		opgEmployee.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				EmployeeDataAdd();
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
			String query="select dApplicationDate,vEmployeeCode,vSectionName,vDepartmentName," +
					"dTourFrom,dTourTo,vEmployeeName,vDesignationName,vPurposeOfLeave," +
					"vVisitingAddress,iTotalDays from tbTourApplication where dTourFrom >='"+dFormat.format(dTourFrom.getValue())+"'" +
					" and dTourTo<='"+dFormat.format(dTourTo.getValue())+"' and vEmployeeID='"+cmbEmpID.getValue().toString()+"' order by vEmployeeCode";

			System.out.println("Query"+query);

			if(queryValueCheck(query))
			{
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
				hm.put("fromDate",dTourFrom.getValue());
				hm.put("toDate",dTourTo.getValue());
				hm.put("SysDate",reportTime.getTime);
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("sql", query);
				Window win = new ReportViewer(hm,"report/account/hrmModule/rptOfficeTourInformation.jasper",
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
		Session session=SessionFactoryUtil.getInstance().openSession();
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
		allComp.add(dTourFrom);
		allComp.add(dTourTo);
		allComp.add(cmbEmpID);
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
		setWidth("475px");
		setHeight("240px");

		// lblFromDate
		lblFromDate = new Label("From Date :");
		lblFromDate.setImmediate(false);
		lblFromDate.setWidth("100.0%");
		lblFromDate.setHeight("-1px");
		mainLayout.addComponent(lblFromDate,"top:10.0px; left:30.0px;");

		// dFrom
		dTourFrom = new PopupDateField();
		dTourFrom.setImmediate(true);
		dTourFrom.setWidth("110px");
		dTourFrom.setHeight("-1px");
		dTourFrom.setDateFormat("dd-MM-yyyy");
		dTourFrom.setResolution(PopupDateField.RESOLUTION_DAY);
		dTourFrom.setValue(new java.util.Date());
		mainLayout.addComponent(dTourFrom, "top:08.0px; left:140.0px;");

		dTourTo=new PopupDateField();
		dTourTo.setImmediate(true);
		dTourTo.setWidth("110px");
		dTourTo.setHeight("-1px");
		dTourTo.setDateFormat("dd-MM-yyyy");
		dTourTo.setResolution(PopupDateField.RESOLUTION_DAY);
		dTourTo.setValue(new java.util.Date());
		mainLayout.addComponent(new Label("To Date :"), "top:40.0px; left:30.0px;");
		mainLayout.addComponent(dTourTo, "top:38.0px; left:140.0px;");

		opgEmployee=new OptionGroup("",lstEmployee);
		opgEmployee.select("Employee ID");
		opgEmployee.setImmediate(true);
		opgEmployee.setStyleName("horizontal");
		mainLayout.addComponent(opgEmployee, "top:70.0px; left:50.0px;");

		//lblEmpID
		lblEmpID=new Label("Employee ID : ");
		mainLayout.addComponent(lblEmpID, "top:100.0px;left:30.0px;");

		//cmbEmpID
		cmbEmpID=new ComboBox();
		cmbEmpID.setImmediate(true);
		cmbEmpID.setWidth("260px");
		cmbEmpID.setHeight("-1px");
		cmbEmpID.select("Employee ID");
		cmbEmpID.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbEmpID, "top:98.0px;left:140.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:130.0px;left:140.0px;");

		mainLayout.addComponent(new Label("_________________________________________________________________________"), "top:145.0px;right:20.0px;left:20.0px;");		
		mainLayout.addComponent(cButton,"top:165.opx; left:160.0px");
		return mainLayout;
	}
}