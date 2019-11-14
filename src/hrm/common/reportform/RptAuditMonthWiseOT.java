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
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class RptAuditMonthWiseOT extends Window
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblMonth;
	private Label lblSection;

	private PopupDateField dMonth;
	private ComboBox cmbDepartment;
	private ComboBox cmbSection;

	ArrayList<Component> allComp = new ArrayList<Component>();

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
	private SimpleDateFormat yearMonthFormat = new SimpleDateFormat("MMMMM-yyyy");
	private ReportDate reportTime = new ReportDate();

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other","Excel"});
	private CommonMethod cm;
	private String menuId = "";
	public RptAuditMonthWiseOT(SessionBean sessionBean,String menuId)
	{
		this.sessionBean=sessionBean;
		this.setCaption("MONTH WISE OT :: "+sessionBean.getCompany());
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		buildMainLayout();
		setContent(mainLayout);
		cmbDepartmentAddData();
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
	public void cmbDepartmentAddData()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> list=session.createSQLQuery("select distinct vDepartmentID,vDepartmentName from tbAuditSalary where " +
					"vMonthName=DateName(MM,'"+dateformat.format(dMonth.getValue())+"')  and " +
					"iyear='"+yearFormat.format(dMonth.getValue())+"' and iTotalOTHour>0 order by " +
					"vDepartmentName").list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbDepartment.addItem(element[0]);
				cmbDepartment.setItemCaption(element[0], (String) element[1]);
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbDepartmentAddData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	
	public void cmbSectionAddData()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> list=session.createSQLQuery("select distinct vSectionID,vSectionName from tbAuditSalary where " +
					"vDepartmentID='"+cmbDepartment.getValue()+"' and vMonthName=DateName(MM,'"+dateformat.format(dMonth.getValue())+"') and " +
					"iyear='"+yearFormat.format(dMonth.getValue())+"' and iTotalOTHour>0 order by vSectionName").list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbSection.addItem(element[0]);
				cmbSection.setItemCaption(element[0], (String) element[1]);
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbSectionAddData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void setEventAction()
	{
		dMonth.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbDepartment.removeAllItems();
				if(dMonth.getValue()!=null)
				{
					cmbDepartmentAddData();
				}
			}
		});
		
		cmbDepartment.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSection.removeAllItems();
				if(cmbDepartment.getValue()!=null)
				{
					cmbSectionAddData();
				}
			}
		});
		
		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbSection.getValue()!=null)
				{
					getAllData();
				}
				else
				{
					showNotification("Warniung!","Select Section Name",Notification.TYPE_WARNING_MESSAGE);
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

	private void getAllData()
	{
		String sectionValue = "";
		sectionValue = cmbSection.getValue().toString();
		reportShow(sectionValue);
	}

	private void reportShow(Object Section)
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		String query=null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();

		try
		{
			query=  " select distinct ts.iYear,ts.vMonthName,ts.vEmployeeID,ts.vProximityID,ts.vEmployeeCode,ts.vEmployeeName,ts.vDesignationName,"
					+ "ts.mGross,ts.mOTRate,ts.vDepartmentID,ts.vDepartmentName,ts.vSectionID,ts.vSectionName,ts.itotalOTHour,ts.itotalOTMin,ts.iExtraOT,0 as FridayDuty,"
					+ "0 as FridayOTTime,0 as FridayAllowance,0 as NormalOTTime,round(itotalOTHour*mOTRate+(mOTRate/60*iTotalOTMin),0) as otAmt,"
					+ "ROUND(mGross+(itotalOTHour*mOTRate+(mOTRate/60*iTotalOTMin)),0)as TotalAmount,"
					+ "ROUND(mGross+(itotalOTHour*mOTRate+(mOTRate/60*iTotalOTMin)),0)as PayableAmount,"
					+ "''Signature,''Remarks from tbAuditSalary ts inner join tbEmployeeInfo ein on ein.vEmployeeId=ts.vEmployeeID where ein.OtStatus=1 and " +
					"ts.iYear= year('"+dateformat.format(dMonth.getValue())+"') and " +
					"ts.vMonthName= DateName(mm,'"+dateformat.format(dMonth.getValue())+"') and " +
					"ts.vSectionID='"+Section+"' order by ts.vEmployeeCode";

			if(queryValueCheck(query))
			{

				if(RadioBtnGroup.getValue()=="Excel")
				{
					String loc = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/temp/attendanceFolder";

					String fname = "Month_Wise_OT.xls";
					String url = getWindow().getApplication().getURL()+"VAADIN/themes/temp/attendanceFolder/"+fname;

					File inFile; 
					String header[]=new String[2];
					String reportName = "SECTION WISE MONTHLY OT";
					String detailQuery[]=new String[1];
					String GroupQuery[]=new String[1];
					String signatureOption [] = {"Prepared By HR Officer","Checked By HR Executive","Manager (HR & Admin)","Manager (Accounts & Finance)","Approved By"};
					int rowWidth=0;
					
					header[0]="Department Name : "+cmbDepartment.getItemCaption(cmbDepartment.getValue())+"   Section Name : "+cmbSection.getItemCaption(cmbSection.getValue());
					header[1]="Month : "+yearMonthFormat.format(dMonth.getValue());

					inFile=new File("D://Tomcat 7.0/webapps/report/uptd/hrmReportExl/AuditMonthWiseOT.xls");

					detailQuery[0]=	" select distinct ts.vEmployeeCode,ts.vProximityID,ts.vEmployeeName,ts.vDesignationName,"
							+ "cast(ts.mGross as float) mGross, 0 as FridayDuty,0 as FridayOTHrs,0 as FridayAllowance,"
							+ "cast(ts.iTotalOTHour as varchar)+':'+cast(ts.iTotalOTMin  as varchar) totalOTHrs,"
							+ "cast(ts.mOTRate as varchar) mOTRate,round(cast((itotalOTHour*mOTRate)+mOTRate/60*itotalOTMin as float), 0) OTAmount,"
							+ "0 as iExtraOT,round(cast((itotalOTHour*mOTRate)+mOTRate/60*itotalOTMin as float),0) TotalAmount,0 as Extra,"
							+ "round(cast((itotalOTHour*mOTRate)+mOTRate/60*itotalOTMin as float),0) payableAmt,'' Signature,"
							+ "'' Remarks from tbAuditSalary ts inner join tbEmployeeInfo ein on ein.vEmployeeId=ts.vEmployeeID"
							+ " where ein.OtStatus=1 and " +
							" ts.iYear= year('"+dateformat.format(dMonth.getValue())+"') and " +
							" ts.vMonthName= DateName(mm,'"+dateformat.format(dMonth.getValue())+"') and " +
							" ts.vSectionID='"+Section+"' order by ts.vEmployeeCode";

					rowWidth=10;
					new SalaryExcelReport(sessionBean, loc, url, fname, header, inFile, "Audit_Section_Wise_Monthly_OT", 
							reportName, 2, GroupQuery, 2, detailQuery, rowWidth,8,signatureOption);

					
					Window window = new Window();
					getApplication().addWindow(window);
					getWindow().open(new ExternalResource(url),"_blank",500,200,Window.BORDER_NONE);
				}

				else
				{
					HashMap <String,Object> hm = new HashMap <String,Object> ();
					hm.put("company", sessionBean.getCompany());
					hm.put("address", sessionBean.getCompanyAddress());
					hm.put("phone", sessionBean.getCompanyContact());
					hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
					hm.put("SysDate",reportTime.getTime);
					hm.put("logo", sessionBean.getCompanyLogo());
					hm.put("sql", query);

					Window win = new ReportViewer(hm,"report/account/hrmModule/rptAuditMonthlyOTPermanent.jasper",
							this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
							this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
							this.getWindow().getApplication().getURL()+"VAADIN/applet",RadioBtn.Radio);

					win.setCaption("Project Report");
					this.getParent().getWindow().addWindow(win);
				}
			}
			else
			{
				showNotification("Warning","There are no Data",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception exp){
			this.getParent().showNotification("Error "+exp,Notification.TYPE_ERROR_MESSAGE);}
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

	@SuppressWarnings("unused")
	private void focusMove()
	{
		allComp.add(dMonth);
		allComp.add(cmbSection);
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
		setHeight("220px");

		// lblMonth
		lblMonth = new Label("Month :");
		lblMonth.setImmediate(false);
		lblMonth.setWidth("100.0%");
		lblMonth.setHeight("-1px");
		mainLayout.addComponent(lblMonth,"top:10.0px; left:20.0px;");

		// dMonth
		dMonth = new PopupDateField();
		dMonth.setImmediate(true);
		dMonth.setWidth("100px");
		dMonth.setHeight("-1px");
		dMonth.setDateFormat("MMM-yyyy");
		dMonth.setValue(new java.util.Date());
		dMonth.setResolution(PopupDateField.RESOLUTION_MONTH);
		mainLayout.addComponent(dMonth, "top:08.0px; left:130.0px;");

		cmbDepartment = new ComboBox();
		cmbDepartment.setWidth("260px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setNullSelectionAllowed(true);
		cmbDepartment.setImmediate(true);
		mainLayout.addComponent(new Label("Department Name : "), "top:40.0px; left:20.0px;");
		mainLayout.addComponent(cmbDepartment, "top:38.0px; left:130.0px;");
		
		// lblSection
		lblSection = new Label("Section Name : ");
		lblSection.setImmediate(false);
		lblSection.setWidth("100.0%");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection,"top:70.0px; left:20.0px;");

		// cmbSection
		cmbSection = new ComboBox();
		cmbSection.setWidth("260px");
		cmbSection.setHeight("-1px");
		cmbSection.setNullSelectionAllowed(true);
		cmbSection.setImmediate(true);
		mainLayout.addComponent(cmbSection, "top:68.0px; left:130.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:100.0px;left:130.0px;");

		mainLayout.addComponent(new Label("______________________________________________________________________"), "top:120.0px;left:20.0px;right:20.0px;");
		mainLayout.addComponent(cButton,"top:140.opx; left:140.0px");
		return mainLayout;
	}
}
