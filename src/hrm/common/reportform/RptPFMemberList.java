package hrm.common.reportform;

import java.io.File;
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
import com.common.share.SalaryExcelReport;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;
@SuppressWarnings("serial")
public class RptPFMemberList extends Window {

	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;
	private Label lblDate=new Label("");
	private PopupDateField dAsOnDate,dMonth;	
	private ComboBox cmbUnit;
	private CheckBox chkUnitAll = new CheckBox("All");
	private CommonMethod cm;
	private String menuId = "";
	private SimpleDateFormat dFormat1 = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dMonthFormat = new SimpleDateFormat("MMMMM-yyyy");
	ArrayList<Component> allComp = new ArrayList<Component>();

	private SimpleDateFormat dFormat2=new SimpleDateFormat("");
	private SimpleDateFormat dMonthFormat2 =new SimpleDateFormat("yyyy-MM-dd");
	ArrayList<Component> allComp2 = new ArrayList<Component>();
	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	private OptionGroup opgDateType;
	private static final List<String> dateType=Arrays.asList(new String[]{"As on date","Monthly"});

	public RptPFMemberList(SessionBean sessionBean,String menuId)
	{
		this.sessionBean=sessionBean;
		this.setCaption("PF MEMBER LIST :: "+sessionBean.getCompany());
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		buildMainLayout();
		setContent(mainLayout);
		cmbUnitDataLoad();
		setEventAction();
		focusMove();
		allTrueFalse();
		lblDate.setValue("As on date :");
		dAsOnDate.setVisible(true);
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
			String sql = "select distinct vUnitId,vUnitName from tbEmpOfficialPersonalInfo epo "
					+ " inner join tbEmpNomineeInfo eni on eni.vEmployeeId=epo.vEmployeeId "
					+ "where bStatus=1 order by vUnitName";
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

	public void setEventAction()
	{

		dAsOnDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				chkUnitAll.setValue(false);
				cmbUnit.removeAllItems();
				if(dAsOnDate.getValue()!=null)
				{
					cmbUnit.setEnabled(true);
					cmbUnitDataLoad();
				}
			}
		});

		chkUnitAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{

				if(chkUnitAll.booleanValue())
				{
					cmbUnit.setValue(null);
					cmbUnit.setEnabled(false);
				}
				else
				{
					cmbUnit.setEnabled(true);
				}

			}
		});


		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(dAsOnDate.getValue()!=null || dMonth.getValue()!=null)
				{
					if(cmbUnit.getValue()!=null || chkUnitAll.booleanValue())
					{
						reportShow();
					}
					else
					{
						showNotification("Select Unit",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					showNotification("Select Date",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});
	
		dMonth.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				cmbUnit.removeAllItems();
				chkUnitAll.setValue(false);
				if(dMonth.getValue()!=null)
				{
					cmbUnit.setEnabled(true);
					cmbUnitDataLoad();
				}
			}
		});

		opgDateType.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				allTrueFalse();
				if(opgDateType.getValue()!=null)
				{
					if(opgDateType.getValue().toString().equals("As on date"))
					{
						lblDate.setValue("As on date :");
						dAsOnDate.setVisible(true);
					}
					else 
					{
						lblDate.setValue("Month :");
						dMonth.setVisible(true);
					}
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
		String branch="%",report="",date="";

		if(chkUnitAll.booleanValue()!=true){
			branch=cmbUnit.getValue().toString();
		}

		String query=null;

		try
		{	

			if(opgDateType.getValue().toString().equals("As on date"))
			{
				query="select * from funPFMemberList('"+sessionBean.dfBd.format(dAsOnDate.getValue())+"','"+branch+"','Date') order by vUnitName,iRank,dJoiningDate";
				report="report/account/hrmModule/rptPFMemberList.jasper";
				date="As on date :"+sessionBean.dfBd.format(dAsOnDate.getValue());
			}
			else
			{
				query="select * from funPFMemberList('"+sessionBean.dfBd.format(dMonth.getValue())+"','"+branch+"','Month') order by vUnitName,iRank,dJoiningDate";
				report="report/account/hrmModule/rptPFMemberListMonth.jasper";
				date="Month :"+sessionBean.dfMonth.format(dMonth.getValue())+"-"+sessionBean.dfYear.format(dMonth.getValue());
			}

			System.out.println(query);

			if(queryValueCheck(query))
			{
				HashMap <String, Object> hm = new HashMap <String, Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("username", sessionBean.getUserName()+" "+sessionBean.getUserIp());
				hm.put("SysDate",reportTime.getTime);
				hm.put("developer", "Software Solution by : E-Vision Software Ltd.|| helpline : 01755-506044 || www.eslctg.com");
				hm.put("date",date);
				hm.put("sql", query);


				Window win = new ReportViewer(hm,report,
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
			this.getParent().showNotification("Error",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
			System.out.println(exp);
		}
	}

	private boolean queryValueCheck(String sql)
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try 
		{
			List <?> lst = session.createSQLQuery(sql).list();
			if (!lst.isEmpty()) 
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
		allComp.add(dAsOnDate);
		allComp.add(cmbUnit);

		allComp.add(cButton.btnPreview);

		new FocusMoveByEnter(this,allComp);
	}
	private void allTrueFalse()
	{
		lblDate.setValue("");
		dAsOnDate.setVisible(false);
		dMonth.setVisible(false);
	}
	private AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("500px");
		setHeight("250px");

		// optionGroup
		opgDateType = new OptionGroup("",dateType);
		opgDateType.setImmediate(true);
		opgDateType.setStyleName("horizontal");
		opgDateType.setValue("As on date");
		mainLayout.addComponent(opgDateType, "top:10px;left:140px;");


		// lblDate
		lblDate.setImmediate(false);
		lblDate.setWidth("100.0%");
		lblDate.setHeight("-1px");
		lblDate.setValue("As on date :");
		mainLayout.addComponent(lblDate,"top:40px; left:30px;");

		// dAsOnDate
		dAsOnDate = new PopupDateField();
		dAsOnDate.setImmediate(true);
		dAsOnDate.setHeight("-1px");
		dAsOnDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dAsOnDate.setDateFormat("dd-MM-yyyy");
		dAsOnDate.setValue(new java.util.Date());
		mainLayout.addComponent(dAsOnDate, "top:38px; left:140px;");

		dMonth = new PopupDateField();
		dMonth.setImmediate(true);
		dMonth.setWidth("140px");
		dMonth.setHeight("-1px");
		dMonth.setResolution(PopupDateField.RESOLUTION_MONTH);
		dMonth.setDateFormat("MMMMMMMMM-yyyy");
		dMonth.setValue(new java.util.Date());
		mainLayout.addComponent(dMonth, "top:38px; left:140px;");

		cmbUnit = new ComboBox();
		cmbUnit.setImmediate(true);
		cmbUnit.setWidth("300px");
		mainLayout.addComponent(new Label("Project : "), "top:70px; left:30.0px;");
		mainLayout.addComponent(cmbUnit, "top:68px; left:140.0px;");

		chkUnitAll = new CheckBox("All");
		chkUnitAll.setImmediate(true);
		mainLayout.addComponent(chkUnitAll,"top:70px; left:445px;");





		//mainLayout.addComponent(new Label("_________________________________________________________________________________________"), "top:150.0px;right:20.0px;left:20.0px;");		
		mainLayout.addComponent(cButton,"bottom:15px; left:140px");
		return mainLayout;
	}
}