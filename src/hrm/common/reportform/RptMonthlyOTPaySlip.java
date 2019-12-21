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

public class RptMonthlyOTPaySlip extends Window
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblSection;
	private Label lblSalaryMonth;
	private Label lblEmployeeName;

	private ComboBox cmbSection;
	private PopupDateField dSalaryMonth;
	private ComboBox cmbEmployeeName;

	private CheckBox chkEmployeeName;
	private ReportDate reportTime = new ReportDate();

	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dMonthFormat = new SimpleDateFormat("MMMMM-yyyy");

	ArrayList<Component> allComp = new ArrayList<Component>();

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");

	private static final List<String> type=Arrays.asList(new String []{"PDF","Other"});
	private OptionGroup rpttype;

	public RptMonthlyOTPaySlip(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setCaption("MONTHLY OT PAY SLIP :: "+sessionBean.getCompany());
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
			List list=session.createSQLQuery("select Distinct ms.vSectionId,si.SectionName from tbMonthlySalaryDetails ms inner join tbEmployeeInfo ei on ei.vSectionId=ms.vSectionId inner " +
			"join tbSectionInfo si on ms.vSectionId=si.AutoID where MONTH(dSalaryOfMonth)=MONTH('"+dFormat.format(dSalaryMonth.getValue())+"') and Year(dSalaryOfMonth)=Year('"+dFormat.format(dSalaryMonth.getValue())+"') and ei.OtStatus='1'").list();
			
			for(Iterator iter=list.iterator();iter.hasNext();)
			{
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
		chkEmployeeName.addListener(new Listener()
		{
			public void componentEvent(Event event)
			{
				if(chkEmployeeName.booleanValue()==true)
				{
					cmbEmployeeName.setEnabled(false);
					cmbEmployeeName.setValue(null);
				}
				else
				{
					cmbEmployeeName.setEnabled(true);
				}
			}
		});
		
		dSalaryMonth.addListener(new Listener()
		{
			public void componentEvent(Event event)
			{
				cmbSection.removeAllItems();
				cmbEmployeeName.removeAllItems();
				cmbSectionAddData();
			}
		});

		cmbSection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbSection.getValue()!=null)
				{employeeSetData(cmbSection.getValue().toString());}
			}
		});

		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbSection.getValue()!=null)
				{
					if(cmbEmployeeName.getValue()!=null || chkEmployeeName.booleanValue()==true)
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
						showNotification("Select Employee Name",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					showNotification("Select Section Name",Notification.TYPE_WARNING_MESSAGE);
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

	public void employeeSetData(String sectionId)
	{
		cmbEmployeeName.removeAllItems();
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery(" select employeeCode,vEmployeeName from tbEmployeeInfo ei inner join tbMonthlySalaryDetails md on " +
					"ei.employeeCode=md.vEmployeeId where MONTH(dSalaryOfMonth)=MONTH('"+dFormat.format(dSalaryMonth.getValue())+"') and YEAR(dSalaryOfMonth)=" +
					"YEAR('"+dFormat.format(dSalaryMonth.getValue())+"') and ei.OtStatus='1' and mOtAmount>0 and md.vSectionId='"+cmbSection.getValue()+"'").list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbEmployeeName.addItem(element[0]);
				cmbEmployeeName.setItemCaption(element[0], (String) element[1]);
			}
		}
		catch(Exception exp){
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void reportShow()
	{
		String Employee ="";
		if(chkEmployeeName.booleanValue()==true)
		{
			Employee = "%";
		}
		else
		{
			Employee = cmbEmployeeName.getValue().toString();
		}

		String query=null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			HashMap hm = new HashMap();

			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			hm.put("section",cmbSection.getItemCaption(cmbSection.getValue()));
			hm.put("month",dMonthFormat.format(dSalaryMonth.getValue()));
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("SysDate",reportTime.getTime);

			/*
			query =" select b.employeeCode,b.vEmployeeName,c.designationName,d.SectionName,a.iTotalDays,a.iNoOfFridays," +
					" a.iNoOfHolidays,(a.iTotalDays-a.iNoOfFridays-a.iNoOfHolidays)as WD,a.iLeaveDay,a.iTourDay,a.iOffDay," +
					" a.iAbsentDay,iPresentDays" +
					" as PD,a.mBasic,a.mHouseRent,a.mMedicalAllowance,a.mConveyance,b.mSpecial,a.mOtherAllowance," +
					" a.vOtHour,case when (a.mOtAmount-FLOOR(a.mOtAmount))>=.5 then CEILING(a.mOtAmount) else FLOOR(a.mOtAmount) " +
					" end as mOtAmount,a.mFestivalBonusAmt,case when(a.mBasic+a.mHouseRent+a.mMedicalAllowance+a.mConveyance+ b.mSpecial+" +
					"a.mOtherAllowance+a.mOtAmount+a.mFestivalBonusAmt)-FLOOR(a.mBasic+a.mHouseRent+a.mMedicalAllowance+a.mConveyance+ " +
					"b.mSpecial+a.mOtherAllowance+a.mOtAmount+a.mFestivalBonusAmt)>=.5 then CEILING(a.mBasic+a.mHouseRent+a.mMedicalAllowance+" +
					"a.mConveyance+ b.mSpecial+a.mOtherAllowance+a.mOtAmount+a.mFestivalBonusAmt) else FLOOR(a.mBasic+a.mHouseRent+" +
					"a.mMedicalAllowance+a.mConveyance+ b.mSpecial+a.mOtherAllowance+ a.mOtAmount+a.mFestivalBonusAmt)  end as TotalEarn," +
					"a.mAbsentAmount," +
					" a.mAdvanceSalary,a.mLoanDeduction,a.mProvidentFund,a.mKFund,a.mRevenueStamp,(a.mAbsentAmount+" +
					"a.mAdvanceSalary+" +
					" a.mLoanDeduction+a.mProvidentFund+a.mKFund+a.mRevenueStamp) as Deduction,case when " +
					"((a.mBasic+a.mHouseRent+a.mMedicalAllowance +a.mConveyance+ b.mSpecial+a.mOtherAllowance+" +
					"a.mOtAmount+a.mFestivalBonusAmt)-(a.mAbsentAmount+a.mAdvanceSalary+ a.mLoanDeduction+" +
					"a.mProvidentFund+a.mRevenueStamp))-FLOOR((a.mBasic+a.mHouseRent+a.mMedicalAllowance +" +
					"a.mConveyance+b.mSpecial+a.mOtherAllowance+a.mOtAmount+a.mFestivalBonusAmt)-" +
					"(a.mAbsentAmount+a.mAdvanceSalary+ a.mLoanDeduction+a.mProvidentFund+a.mRevenueStamp))>=.5 then" +
					" CEILING((a.mBasic+a.mHouseRent+a.mMedicalAllowance +a.mConveyance+b.mSpecial+a.mOtherAllowance+" +
					"a.mOtAmount+a.mFestivalBonusAmt)- (a.mAbsentAmount+a.mAdvanceSalary+ " +
					"a.mLoanDeduction+a.mProvidentFund+a.mRevenueStamp)) else " +
					"FLOOR((a.mBasic+a.mHouseRent+a.mMedicalAllowance +a.mConveyance+b.mSpecial+a.mOtherAllowance+" +
					"a.mOtAmount+a.mFestivalBonusAmt)-(a.mAbsentAmount+a.mAdvanceSalary+ a.mLoanDeduction+" +
					"a.mProvidentFund+a.mRevenueStamp)) end as NetPay,isnull(bn.bankName,'') bankName,isnull(a.vAccountNo,'') vAccountNo from tbMonthlySalaryDetails" +
					" as a left join tbEmployeeInfo as b on a.vEmployeeId=b.employeeCode left join tbDesignationInfo as c on" +
					" a.idesignationId=c.designationId left join tbSectionInfo as d on a.vSectionId=d.AutoID left join tbBankName" +
					" as bn on bn.id=a.vBankId where a.vSectionId='"+cmbSection.getValue().toString()+"' and a.vEmployeeId like '"+Employee+"'" +
					" and MONTH(a.dSalaryOfMonth)=MONTH('"+dFormat.format(dSalaryMonth.getValue())+"')" +
					" order by convert(int, a.vEmployeeId) ";
					
					 */

			query="  select b.employeeCode,b.vEmployeeName,c.designationName,d.SectionName,a.iTotalDays," +
					"a.iNoOfFridays, a.iNoOfHolidays,(a.iTotalDays-a.iNoOfFridays-a.iNoOfHolidays) as " +
					"WD,a.iLeaveDay,a.iTourDay,a.iOffDay, a.iAbsentDay,iPresentDays as PD,a.mBasic,a.OTRate,a.vOtHour," +
					"case when (a.mOtAmount-FLOOR(a.mOtAmount))>=.5 then CEILING(a.mOtAmount) else FLOOR(a.mOtAmount)  " +
					"end as mOtAmount,dbo.number(case when (a.mOtAmount-FLOOR(a.mOtAmount))>=.5 then CEILING(a.mOtAmount) " +
					"else FLOOR(a.mOtAmount)  end ) InWord from tbMonthlySalaryDetails as a left join tbEmployeeInfo as " +
					"b on a.vEmployeeId=b.employeeCode left join tbDesignationInfo as c on a.idesignationId=c.designationId " +
					"left join tbSectionInfo as d on a.vSectionId=d.AutoID left join tbBankName as bn on bn.id=a.vBankId " +
					"where a.vSectionId='"+cmbSection.getValue().toString()+"' and a.vEmployeeId like '"+Employee+"'" +
					" and MONTH(a.dSalaryOfMonth)=MONTH('"+dFormat.format(dSalaryMonth.getValue())+"')" +
					" order by convert(int, a.vEmployeeId) ";
			
			if(queryValueCheck(query))
			{
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/account/hrmModule/rptEmployeeOTPaySlip.jasper",
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
		mainLayout.addComponent(lblSalaryMonth,"top:20.0px; left:30.0px;");

		// dSalaryMonth
		dSalaryMonth = new PopupDateField();
		dSalaryMonth.setImmediate(true);
		dSalaryMonth.setWidth("140px");
		dSalaryMonth.setHeight("-1px");
		dSalaryMonth.setDateFormat("MMMMM-yyyy");
		dSalaryMonth.setResolution(PopupDateField.RESOLUTION_MONTH);
		dSalaryMonth.setValue(new java.util.Date());
		mainLayout.addComponent(dSalaryMonth, "top:18.0px; left:130.0px;");

		// lblSection
		lblSection = new Label("Section Name :");
		lblSection.setImmediate(false);
		lblSection.setWidth("100.0%");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection,"top:45.0px; left:30.0px;");

		// cmbSection
		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("180px");
		cmbSection.setHeight("-1px");
		cmbSection.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbSection, "top:43.0px; left:130.0px;");

		// lblEmployeeName
		lblEmployeeName = new Label("Employee Name :");
		lblEmployeeName.setImmediate(false);
		lblEmployeeName.setWidth("100.0%");
		lblEmployeeName.setHeight("-1px");
		mainLayout.addComponent(lblEmployeeName,"top:70.0px; left:30.0px;");

		// cmbEmployeeName
		cmbEmployeeName = new ComboBox();
		cmbEmployeeName.setImmediate(true);
		cmbEmployeeName.setWidth("220px");
		cmbEmployeeName.setHeight("-1px");
		cmbEmployeeName.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbEmployeeName, "top:68.0px; left:130.0px;");

		chkEmployeeName = new CheckBox("All");
		chkEmployeeName.setImmediate(true);
		chkEmployeeName.setHeight("-1px");
		chkEmployeeName.setWidth("-1px");
		mainLayout.addComponent(chkEmployeeName, "top:70.0px; left:355.0px;");

		rpttype=new OptionGroup("",type);
		rpttype.setImmediate(true);
		rpttype.select("Other");
		rpttype.setStyleName("horizontal");
		mainLayout.addComponent(rpttype, "top:90.0px; left:130.0px");

		mainLayout.addComponent(cButton,"top:120.opx; left:120.0px");

		return mainLayout;
	}
}