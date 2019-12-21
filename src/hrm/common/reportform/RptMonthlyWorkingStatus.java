package hrm.common.reportform;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.ReportDate;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
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
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Component.Listener;

public class RptMonthlyWorkingStatus extends Window {

	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblSection;
	private Label lblSalaryMonth;

	private ComboBox cmbSection;
	private CheckBox chkSection;
	private PopupDateField dSalaryMonth;

	private static final List<String> type=Arrays.asList(new String []{"PDF","Other"});

	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dMonthFormat = new SimpleDateFormat("MMMMM-yyyy");

	ArrayList<Component> allComp = new ArrayList<Component>();
	private ReportDate reportTime = new ReportDate();

	private OptionGroup rpttype;
	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");

	public RptMonthlyWorkingStatus(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setCaption("MONTHLY WORKING SUMMARY :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);
		setEventAction();
		focusMove();
	}

	public void cmbSectionAddData()
	{
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery("select Distinct ms.vSectionId,si.SectionName from tbMonthlySalaryDetails ms inner join tbSectionInfo " +
					"si on ms.vSectionId=si.AutoID where MONTH(dSalaryOfMonth)=MONTH('"+dFormat.format(dSalaryMonth.getValue())+"') and Year(dSalaryOfMonth)=Year('"+dFormat.format(dSalaryMonth.getValue())+"')").list();
			
			for(Iterator iter=list.iterator();iter.hasNext();){

				Object[] element = (Object[]) iter.next();
				cmbSection.addItem(element[0]);
				cmbSection.setItemCaption(element[0], (String) element[1]);
			}
		}
		catch(Exception exp){
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public void setEventAction()
	{
		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbSection.getValue()!=null || chkSection.booleanValue())
				{
					if(dSalaryMonth.getValue()!=null)
					{
						reportShow();
					}
					else
					{
						showNotification("Select Month",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					showNotification("Select Section Name",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});
		
		dSalaryMonth.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(dSalaryMonth.getValue()!=null)
				{
					cmbSection.removeAllItems();
					cmbSectionAddData();
				}
			}
		});
		
		chkSection.addListener(new Listener()
		{
			public void componentEvent(Event event)
			{
				if(chkSection.booleanValue()==true)
				{
					cmbSection.setEnabled(false);
					cmbSection.setValue(null);
				}
				else
				{
					cmbSection.setEnabled(true);
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
		String query=null;
		String SectionTotalOT="";
		Double Salary=0.00;
		String section="";
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			/*List lst = session.createSQLQuery("select dbo.sumOfTotalOT('"+cmbSection.getValue()+"'),0").list();
			for(Iterator iter=lst.iterator();iter.hasNext();)
			{  
			Object[] element = (Object[]) iter.next();
			SectionTotalOT= element[0].toString();
			}*/
			
			if(chkSection.booleanValue())
			{
				section="%";
			}
			else
			{
				section=cmbSection.getValue().toString();
			}

			Object[] element ;

			HashMap hm = new HashMap();
			System.out.println("SectionTotalOT"+hm.put("SectionTotalOT",SectionTotalOT));

			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			hm.put("section",cmbSection.getItemCaption(cmbSection.getValue()));
			hm.put("SysDate",reportTime.getTime);
			hm.put("month",dMonthFormat.format(dSalaryMonth.getValue()));
			hm.put("logo", sessionBean.getCompanyLogo());
			//hm.put("SectionTotalOT",SectionTotalOT);

			query = " select b.employeeCode,b.iFingerID,b.vEmployeeName,c.designationName,d.SectionName,a.iTotalDays," +
					" a.iNoOfFridays,a.iNoOfHolidays,(a.iTotalDays-a.iNoOfFridays-a.iNoOfHolidays)as WD," +
					" a.iLeaveDay,a.iTourDay,a.iOffDay,a.iAbsentDay,a.iPresentDays" +
					" as PD from tbMonthlySalaryDetails as a left join tbEmployeeInfo as b on a.vEmployeeId=b.employeeCode" +
					" left join tbDesignationInfo as c on a.idesignationId=c.designationId left join tbSectionInfo" +
					" as d on a.vSectionId=d.AutoID left join tbBankName as bn on bn.id=a.vBankId where a.vSectionId like '"+section+"' "+
					" and MONTH(a.dSalaryOfMonth)=MONTH('"+dFormat.format(dSalaryMonth.getValue())+"') " +
					" and YEAR(a.dSalaryOfMonth)=YEAR('"+dFormat.format(dSalaryMonth.getValue())+"') " +
					" order by a.vSectionId,a.idesignationId,convert(int, b.employeeCode) ";

			/*	Salary=(Double) element[21];*/

			System.out.println("Query"+query);

			if(queryValueCheck(query))
			{
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/account/hrmModule/rptMonthlyWorkingSum.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",(rpttype.isSelected("PDF")?true:false));

				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);
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
		Transaction tx = null;

		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			Iterator iter = session.createSQLQuery(sql).list().iterator();

			if (iter.hasNext()) 
			{
				return true;
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		return false;
	}

	private void focusMove()
	{
		allComp.add(cmbSection);
		allComp.add(dSalaryMonth);
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
		setWidth("400px");
		setHeight("200px");

		// lblSalaryMonth
		lblSalaryMonth = new Label("Month :");
		lblSalaryMonth.setImmediate(false);
		lblSalaryMonth.setWidth("100.0%");
		lblSalaryMonth.setHeight("-1px");
		mainLayout.addComponent(lblSalaryMonth,"top:30.0px; left:30.0px;");

		// dSalaryMonth
		dSalaryMonth = new PopupDateField();
		dSalaryMonth.setImmediate(true);
		dSalaryMonth.setWidth("140px");
		dSalaryMonth.setHeight("-1px");
		dSalaryMonth.setDateFormat("MMMMM-yyyy");
		dSalaryMonth.setResolution(PopupDateField.RESOLUTION_MONTH);
		dSalaryMonth.setValue(new java.util.Date());
		mainLayout.addComponent(dSalaryMonth, "top:28.0px; left:130.0px;");
		
		// lblSection
		lblSection = new Label("Division Name :");
		lblSection.setImmediate(false);
		lblSection.setWidth("100.0%");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection,"top:60.0px; left:30.0px;");

		// cmbSection
		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("200px");
		cmbSection.setHeight("-1px");
		cmbSection.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbSection, "top:58.0px; left:130.0px;");
		
		chkSection = new CheckBox();
		chkSection.setImmediate(true);
		chkSection.setWidth("200px");
		chkSection.setHeight("-1px");
		mainLayout.addComponent(chkSection, "top:58.0px; left:330.0px;");

		rpttype=new OptionGroup("",type);
		rpttype.setImmediate(true);
		rpttype.setStyleName("horizontal");
		rpttype.select("Other");
		mainLayout.addComponent(rpttype, "top:90.0px; left:130.0px");

		mainLayout.addComponent(cButton,"top:110.opx; left:120.0px");

		return mainLayout;
	}
}