package hrm.common.reportform;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.ReportDate;
import com.common.share.ReportOption;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
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

@SuppressWarnings("serial")
public class RptDeleteMonthlySalary extends Window {

	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblSalaryMonth;
	private Label lblSection;
	
	private ComboBox cmbSalaryMonth;

	private Label lblEmpName;
	private ComboBox cmbEmpName;
	private CheckBox chkEmpName;

	private ComboBox cmbSection;
	private CheckBox chkSectionAll;

	private PopupDateField dSalaryMonth;

	//private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dMonthFormat = new SimpleDateFormat("MMMMM-yyyy");

	ArrayList<Component> allComp = new ArrayList<Component>();

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	

	private OptionGroup RadioBtnGroup;
	private static final List<String> group=Arrays.asList(new String[]{"PDF","Other"});

	SimpleDateFormat dRptFormat = new SimpleDateFormat("dd-MM-yyyy");
	SimpleDateFormat dMonthlyFormat = new SimpleDateFormat("MMMMM");
	SimpleDateFormat dYearFormat = new SimpleDateFormat("yyyy");

	public RptDeleteMonthlySalary(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setCaption("MONTHLY SALARY :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);

		cmbSalaryMonthData();	
		setEventAction();
		focusMove();
	}
	
	private void cmbSalaryMonthData()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select vMonthName,vYear,dSalaryDate from tbUdMonthlySalary  order by dSalaryDate desc";
			List <?> list=session.createSQLQuery(query).list();	

			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbSalaryMonth.addItem( element[2]);
				cmbSalaryMonth.setItemCaption( element[2], element[0].toString()+"-"+element[1].toString());
			}
		}
		catch(Exception exp)                  
		{
			showNotification("cmbSalaryMonthData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();
		}
	}
	

	private void cmbSectionData() 
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vsectionID,vSectionName from tbUdMonthlySalary  " +
					"where  vMonthName='"+dMonthlyFormat.format(cmbSalaryMonth.getValue())+"' and vyear='"+dYearFormat.format(cmbSalaryMonth.getValue())+"'  " +
					"order by vSectionName";

			System.out.println("Shehab"+query);
			List <?> list=session.createSQLQuery(query).list();	

			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbSection.addItem(element[0]);
				cmbSection.setItemCaption(element[0], element[1].toString()+"("+element[1].toString()+")");
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbSectionData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void cmbEmpNameDataAdd()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select vEmployeeID,vEmployeeCode from tbUDMonthlySalary where vMonthName='"+dMonthlyFormat.format(cmbSalaryMonth.getValue())+"' and vyear='"+dYearFormat.format(cmbSalaryMonth.getValue())+"' and vSectionID like '"+(cmbSection.getValue() != null?cmbSection.getValue().toString():"%")+"'";
			
			System.out.println("Shehab"+query);
			
			List <?> lst=session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				for(Iterator <?> itr=lst.iterator();itr.hasNext();)
				{
					Object [] element=(Object[]) itr.next();
					cmbEmpName.addItem(element[0]);
					cmbEmpName.setItemCaption(element[0], element[1].toString());
				}
			}
			else
			{
				showNotification("Warning", "No Employee Found!!!", Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbEmployeeNameDataAdd",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	
	
	
	

	public void setEventAction()
	{
		
		cmbSalaryMonth.addListener(new ValueChangeListener()
		{	
			public void valueChange(ValueChangeEvent event) 
			{
				cmbSection.removeAllItems();
				if(cmbSalaryMonth.getValue()!=null)
				{
					cmbSectionData();
				}
			}
		});
		

		cmbSection.addListener(new ValueChangeListener()
		{	
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbSection.getValue()!=null)
				{
					cmbEmpNameDataAdd();
				}
			}
		});

		chkSectionAll.addListener(new ValueChangeListener()
		{	
			public void valueChange(ValueChangeEvent event)
			{
				if(chkSectionAll.booleanValue())
				{
					cmbSection.setValue(null);
					cmbSection.setEnabled(false);
					cmbSectionData();
					
				}
				else
				{
					cmbSection.setEnabled(true);
				}
			}
		});

		chkEmpName.addListener(new ValueChangeListener()
		{	
			public void valueChange(ValueChangeEvent event)
			{
				if(chkEmpName.booleanValue())
				{
					cmbEmpName.setValue(null);
					cmbEmpName.setEnabled(false);
					cmbEmpNameDataAdd();
				}
				else
				{
					cmbEmpName.setEnabled(true);
				}
			}
		});
		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbSalaryMonth.getValue()!=null)
				{
					if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
					{
						if(cmbEmpName.getValue()!=null || chkEmpName.booleanValue())
						{
							reportShow();
						}
						else
						{
							showNotification("Warning!","Please Select Employee Name",Notification.TYPE_WARNING_MESSAGE);
							cmbEmpName.focus();
						}
					}
					else
					{
						showNotification("Warning!","Please Select Section",Notification.TYPE_WARNING_MESSAGE);
						cmbSection.focus();
					}
				}
				else
				{
					showNotification("Warning!","Please Select Salary Month",Notification.TYPE_WARNING_MESSAGE);
					cmbSalaryMonth.focus();
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
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		String Section="";
		String Employee="";
		
		if(cmbSection.getValue()!=null)
		{
			Section=cmbSection.getValue().toString();
		}
		else
		{
			Section="%";
		}
		
		if(cmbEmpName.getValue()!=null)
		{
			Employee=cmbEmpName.getValue().toString();
		}
		else
		{
			Employee="%";
		}

		/*String query="select ms.vEmployeeCode,ms.vFingerId,ms.vEmployeeName,ms.vDesignationName,ms.vSectionName,ms.iTotalMonthDays," +
				"ms.iTotalHoliday,ms.iTotalLeaveDays,ms.iTotalTourDays,ms.iTotalAbsentDays,ms.iTotalPresentDays,ms.mBasic," +
				"ms.mHouseRent,ms.mMobileBill,ms.mConveyance,ms.mOtherAllowance,ms.mGross,ms.mAdvanceSalary,ms.mProvidentFund," +
				"ms.mRevenueStamp,'Bank' Remarks from tbMonthlySalary as ms inner join tbEmployeeInfo ein on ms.vEmployeeID = ein.vEmployeeId " +
				"where vMonthName=DATENAME(MM,'"+dFormat.format(dSalaryMonth.getValue())+"') and vYear=YEAR('"+dFormat.format(dSalaryMonth.getValue())+"') " +
				"and ms.vSectionID like '"+(cmbSection.getValue() != null?cmbSection.getValue().toString():"%")+"' and ms.vEmployeeType "+
				"like '"+(cmbEmpName.getValue()!=null?cmbEmpName.getValue().toString():"%")+"'and ISNULL(ein.accountNo,'')!='' order by vSectionName,vEmployeeCode";
		
		String query="select vEmployeeCode,vFingerId,vEmployeeName,vEmployeeType,mProvidentFund,vLoanNo,vSectionName,"
				+ "vUserIP,vUserName,iTotalAbsentDays,iTotalHoliday,iTotalLeaveDays,iTotalMonthDays,iTotalPresentDays,"
				+ "iTotalTourDays,mGross,mHouseRent,mAdvanceSalary,mBasic,mConveyance,mInsurance,mMedicalAllowance,"
				+ "mMobileBill,mOtherAllowance,dEntryTime,vTransactionNo,iOTHour,iOTMin,vAccountNo,vBankName,"
				+ "vBranchName,vDesignationName,UDFlag,vDeleteusername,mRevenueStamp,mIncomeTax,dSalaryDate from "
				+ "tbUDMonthlySalary where vMonthName='"+dMonthlyFormat.format(cmbSalaryMonth.getValue())+"'and vYear='"+dYearFormat.format(cmbSalaryMonth.getValue())+"' and vSectionID like '"+Section+"' and "
				+ "vEmployeeID like '"+Employee+"'order by vEmployeeCode";
*/

		String query="select vEmployeeCode,vFingerId,vEmployeeName,vEmployeeType,mProvidentFund,vLoanNo,vSectionName,"
				+ " vUserIP,vUserName,iTotalAbsentDays,iTotalHoliday,iTotalLeaveDays,iTotalMonthDays,iTotalPresentDays,"
				+ " iTotalTourDays,mGross,mHouseRent,mAdvanceSalary,mBasic,mConveyance,mInsurance,mMedicalAllowance,"
				+ " mMobileBill,mOtherAllowance,dEntryTime,vTransactionNo,iOTHour,iOTMin,vAccountNo,vBankName,"
				+ " vBranchName,vDesignationName,UDFlag,vDeleteusername,mRevenueStamp,mIncomeTax,dSalaryDate from "
				+ " tbUDMonthlySalary";
		System.out.println("soma"+query);

		if(queryValueCheck(query))
		{
			try
			{
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			//hm.put("section",cmbSection.getItemCaption(cmbSection.getValue()));
				//hm.put("month",dMonthFormat.format(dSalaryMonth.getValue()));
				//hm.put("SysDate",reportTime.getTime);
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/account/hrmModule/rptSalaryDelete.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",RadioBtn.Radio);

				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			catch(Exception exp)
			{
				showNotification("reportView "+exp,Notification.TYPE_ERROR_MESSAGE);
			}
		}
		else
		{
			showNotification("Warning","There are no Data",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private boolean queryValueCheck(String sql)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
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
		setWidth("450px");
		setHeight("210px");
		
		lblSalaryMonth = new Label("Month :");
		lblSalaryMonth.setImmediate(false);
		lblSalaryMonth.setWidth("100.0%");
		lblSalaryMonth.setHeight("-1px");
		mainLayout.addComponent(lblSalaryMonth,"top:10.0px; left:30.0px;");


		// lblSalaryMonth
		cmbSalaryMonth = new ComboBox();
		cmbSalaryMonth.setImmediate(false);
		cmbSalaryMonth.setWidth("150px");
		cmbSalaryMonth.setHeight("-1px");
		mainLayout.addComponent(cmbSalaryMonth,"top:08.0px; left:130.0px;");

		// lblSection
		lblSection = new Label("Division Name :");
		lblSection.setImmediate(false);
		lblSection.setWidth("100.0%");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection,"top:40.0px; left:30.0px;");

		// cmbSection
		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("260px");
		cmbSection.setHeight("-1px");
		cmbSection.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbSection, "top:38.0px; left:130.0px;");

		chkSectionAll = new CheckBox("All");
		chkSectionAll.setImmediate(true);
		mainLayout.addComponent(chkSectionAll, "top:40.0px; left:395.0px;");

		lblEmpName = new Label("Employee ID :");
		mainLayout.addComponent(lblEmpName, "top:70.0px; left:30.0px;");

		cmbEmpName= new ComboBox();
		cmbEmpName.setWidth("260px");
		cmbEmpName.setHeight("-1px");
		cmbEmpName.setImmediate(true);
		mainLayout.addComponent(cmbEmpName, "top:68.0px;left:130.0px;");

		// chkEmpAll
		chkEmpName = new CheckBox("All");
		chkEmpName.setImmediate(true);
		chkEmpName.setWidth("-1px");
		chkEmpName.setHeight("-1px");
		mainLayout.addComponent(chkEmpName, "top:68.0px;left:395.0px;");
	
		RadioBtnGroup = new OptionGroup("",group);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:100.0px;left:130.0px;");

		mainLayout.addComponent(new Label("_________________________________________________________________________________________"), "top:105.0px;right:20.0px;left:20.0px;");		
		mainLayout.addComponent(cButton,"top:125.opx; left:140.0px");

		return mainLayout;
	}
}