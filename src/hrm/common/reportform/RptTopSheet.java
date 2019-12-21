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
public class RptTopSheet extends Window {

	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblSection;
	private Label lblSalaryMonth;

	private Label lblEmpType;
	//private ComboBox cmbEmpType;
	private CheckBox chkEmployeeType;

	private ComboBox cmbSection;
	private CheckBox chkSectionAll;

	private PopupDateField dSalaryMonth;

	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dMonthFormat = new SimpleDateFormat("MMMMM-yyyy");
	private SimpleDateFormat dMonth = new SimpleDateFormat("MMMMM");
	private SimpleDateFormat dYearFormat = new SimpleDateFormat("yyyy");

	ArrayList<Component> allComp = new ArrayList<Component>();

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	private OptionGroup OGBankType;
	private static final List<String> type =Arrays.asList(new String[]{"Bank","Non-Bank"});

	private OptionGroup RadioBtnGroup;
	private static final List<String> group=Arrays.asList(new String[]{"PDF","Other"});

	SimpleDateFormat dRptFormat = new SimpleDateFormat("dd-MM-yyyy");

	public RptTopSheet(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setCaption("TOP SHEET:: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);

		cmbSectionAddData();
		setEventAction();
		focusMove();
	}

	public void cmbSectionAddData()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vSectionId,vSectionName from tbMonthlySalary where " +
					"vMonthName=DATENAME(MM,'"+dFormat.format(dSalaryMonth.getValue())+"') " +
					"and vYear=YEAR('"+dFormat.format(dSalaryMonth.getValue())+"') order by vSectionName";
			List <?> list=session.createSQLQuery(query).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();){

				Object[] element = (Object[]) iter.next();
				cmbSection.addItem(element[0]);
				cmbSection.setItemCaption(element[0], (String) element[1]);
			}
		}
		catch(Exception exp){
			showNotification("cmbSectionAddData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	
	public void setEventAction()
	{
		dSalaryMonth.addListener(new ValueChangeListener()
		{	
			public void valueChange(ValueChangeEvent event)
			{
				cmbSection.removeAllItems();
				if(dSalaryMonth.getValue()!=null)
					cmbSectionAddData();
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
				}
				else
				{
					cmbSection.setEnabled(true);
				}
			}
		});


		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(dSalaryMonth.getValue()!=null)
				{
					if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
					{
						
							reportShow();
					}
					else
					{
						showNotification("Select Section Name",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					showNotification("Select Month",Notification.TYPE_WARNING_MESSAGE);
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

		String query="select distinct temp.vSectionID,temp.vSectionName,(select  ISNULL(COUNT(a.vEmployeeID),0) "
				+ "  from tbMonthlySalary a inner join tbEmployeeInfo b on a.vEmployeeID=b.vEmployeeId where "
				+ " a.vSectionID like temp.vSectionID and vMonthName='"+dMonth.format(dSalaryMonth.getValue())+"' and a.vEmployeeType='Permanent' and vYear='"+dYearFormat.format(dSalaryMonth.getValue())+"' ) "
				+ " permanentemployee ,(select SUM( (mBasic+mHouseRent+mMobileBill+mConveyance+mOtherAllowance)-(((mGross/iTotalMonthDays)*iTotalAbsentDays)+mAdvanceSalary+mProvidentFund+mRevenueStamp))  "
				+ " from  tbMonthlySalary where vSectionID like temp.vSectionID and vMonthName like '"+dMonth.format(dSalaryMonth.getValue())+"' and vEmployeeType like "
				+ " 'Permanent' and vYear='"+dYearFormat.format(dSalaryMonth.getValue())+"' )as permanentamount,(select COUNT(vEmployeeID) from tbMonthlySalary where "
				+ " vMonthName='"+dMonth.format(dSalaryMonth.getValue())+"' and vEmployeeType='Temporary' and vYear='"+dYearFormat.format(dSalaryMonth.getValue())+"' ) temporarayemployee, "
				+ "((ISNULL(sum((mBasic+mHouseRent+mMobileBill+mConveyance+mOtherAllowance)-(((mGross/iTotalMonthDays)*iTotalAbsentDays)+mAdvanceSalary+mProvidentFund+mRevenueStamp)) ,0)-( select ISNULL"
			    + "(sum((mBasic+mHouseRent+mMobileBill+mConveyance+mOtherAllowance)-(((mGross/iTotalMonthDays)*iTotalAbsentDays)+mAdvanceSalary+mProvidentFund+mRevenueStamp)) ,0)  from tbMonthlySalary where vSectionID=temp.vSectionID and "
				+ "vMonthName= DATENAME(MONTH,DATEADD(MM,-1,'"+dFormat.format(dSalaryMonth.getValue())+"') ) and vYear=year(DATEADD(MM,-1,'"+dFormat.format(dSalaryMonth.getValue())+"')))))increseDecrese ,"
				+ "(select ISNULL(SUM( (mBasic+mHouseRent+mMobileBill+mConveyance+mOtherAllowance)-(((mGross/iTotalMonthDays)*iTotalAbsentDays)+mAdvanceSalary+mProvidentFund+mRevenueStamp)),0)  "
				+ "  from  tbMonthlySalary where vSectionID like temp.vSectionID and vMonthName like '"+dMonth.format(dSalaryMonth.getValue())+"' and vEmployeeType like 'Temporary' and vYear='"+dYearFormat.format(dSalaryMonth.getValue())+"' )as "
				+ "temporaryamount,(select COUNT(vEmployeeID) from tbMonthlySalary where vMonthName='"+dMonth.format(dSalaryMonth.getValue())+"' and vEmployeeType='Probationary'  "
				+ "and vYear='"+dYearFormat.format(dSalaryMonth.getValue())+"')provisinaryemployee, ISNULL((select SUM( (mBasic+mHouseRent+mMobileBill+mConveyance+mOtherAllowance)-(((mGross/iTotalMonthDays)*iTotalAbsentDays)+mAdvanceSalary+mProvidentFund+mRevenueStamp)) "
				+ " from  tbMonthlySalary where "
				+ " vSectionID like temp.vSectionID and vMonthName like '"+dMonth.format(dSalaryMonth.getValue())+"' and vEmployeeType like 'Probationary' and vYear='"+dYearFormat.format(dSalaryMonth.getValue())+"' ),0)as "
				+ "provitionaryamount,(select COUNT(vEmployeeID) from tbMonthlySalary where vMonthName='"+dMonth.format(dSalaryMonth.getValue())+"'"
				+ " and vEmployeeType='Casual' and vYear='"+dYearFormat.format(dSalaryMonth.getValue())+"')casualemployee, isnull((select SUM( (mBasic+mHouseRent+mMobileBill+mConveyance+mOtherAllowance)-(((mGross/iTotalMonthDays)*iTotalAbsentDays)+mAdvanceSalary+mProvidentFund+mRevenueStamp)) "
				+ "  from  tbMonthlySalary where vSectionID like temp.vSectionID and vMonthName like '"+dMonth.format(dSalaryMonth.getValue())+"' and vEmployeeType like 'Casual' and vYear='"+dYearFormat.format(dSalaryMonth.getValue())+"' )  ,0) as casualamount,COUNT(vEmployeeID) totalemployee,  "
				+ "ISNULL(sum((mBasic+mHouseRent+mMobileBill+mConveyance+mOtherAllowance)-(((mGross/iTotalMonthDays)*iTotalAbsentDays)+mAdvanceSalary+mProvidentFund+mRevenueStamp)) ,0) totalamount,( select COUNT(vEmployeeID) "
				+ " from tbMonthlySalary  where vSectionID=temp.vSectionID and vMonthName= DATENAME(MONTH,DATEADD(MM,-1,'"+dFormat.format(dSalaryMonth.getValue())+"') ) and vYear=year(DATEADD(MM,-1,'"+dFormat.format(dSalaryMonth.getValue())+"'))) previousmonthemployee, "
				+ "( select ISNULL(sum((mBasic+mHouseRent+mMobileBill+mConveyance+mOtherAllowance)-(((mGross/iTotalMonthDays)*iTotalAbsentDays)+mAdvanceSalary+mProvidentFund+mRevenueStamp)) ,0) "
				+ "  from tbMonthlySalary where vSectionID=temp.vSectionID and vMonthName= DATENAME(MONTH,DATEADD(MM,-1,'"+dFormat.format(dSalaryMonth.getValue())+"') ) and vYear=year(DATEADD(MM,-1,'"+dFormat.format(dSalaryMonth.getValue())+"'))) previousmonthsalary "
				+ " from tbMonthlySalary temp where vMonthName='"+dMonth.format(dSalaryMonth.getValue())+"' and vYear='"+dYearFormat.format(dSalaryMonth.getValue())+"'  group by temp.vSectionID,temp.vSectionName  ";
					
				/*"select distinct temp.vSectionID,temp.vSectionName,(select  ISNULL(COUNT(a.vEmployeeID),0)from tbMonthlySalary"
				+ " a inner join tbEmployeeInfo b on a.vEmployeeID=b.vEmployeeId where a.vSectionID like temp.vSectionID"
				+ " and vMonthName='"+dMonth.format(dSalaryMonth.getValue())+"' and a.vEmployeeType='Permanent'and vYear='"+dYearFormat.format(dSalaryMonth.getValue())+"') permanentemployee ,"
				+ "(select SUM( (mBasic+mHouseRent+mMobileBill+mConveyance+mOtherAllowance)-(((mGross/iTotalMonthDays)*iTotalAbsentDays)+mAdvanceSalary+mProvidentFund+"
				+ "mRevenueStamp))  from  tbMonthlySalary where vSectionID like temp.vSectionID and vMonthName like '"+dMonth.format(dSalaryMonth.getValue())+"' and "
				+ "vEmployeeType like 'Permanent' and vYear='"+dYearFormat.format(dSalaryMonth.getValue())+"' ) as permanentamount,"
				+ "(select COUNT(vEmployeeID) from tbMonthlySalary where vMonthName='"+dMonth.format(dSalaryMonth.getValue())+"' and vEmployeeType='Temporary' "
				+ "and vYear='"+dMonth.format(dSalaryMonth.getValue())+"' ) temporarayemployee,"
				+ "(select ISNULL(SUM( (mBasic+mHouseRent+mMobileBill+mConveyance+mOtherAllowance)-(((mGross/iTotalMonthDays)*iTotalAbsentDays)+"
				+ "mAdvanceSalary+mProvidentFund+mRevenueStamp)),0)   from  tbMonthlySalary "+
				"where vSectionID like temp.vSectionID and vMonthName like '"+dMonth.format(dSalaryMonth.getValue())+"' and vEmployeeType like 'Temporary'"
				+ " and vYear='"+dMonth.format(dSalaryMonth.getValue())+"' )as temporaryamount,(select COUNT(vEmployeeID) "
				+ "from tbMonthlySalary where vMonthName='"+dMonth.format(dSalaryMonth.getValue())+"' "+
				"and vEmployeeType='Probationary' and vYear='"+dYearFormat.format(dSalaryMonth.getValue())+"')provisinaryemployee,"
				+ "ISNULL((select SUM( (mBasic+mHouseRent+mMobileBill+mConveyance+mOtherAllowance)-(((mGross/iTotalMonthDays)*iTotalAbsentDays)"
				+ "+mAdvanceSalary+mProvidentFund+mRevenueStamp)) "+
				" from  tbMonthlySalary where vSectionID like temp.vSectionID and vMonthName like '"+dMonth.format(dSalaryMonth.getValue())+"' and vEmployeeType"
				+ " like 'Probationary' and vYear='"+dYearFormat.format(dSalaryMonth.getValue())+"' ),0) as provitionaryamount,"
				+ "(select COUNT(vEmployeeID) from tbMonthlySalary where vMonthName='"+dMonth.format(dSalaryMonth.getValue())+"' and "+
				"vEmployeeType='Casual' and vYear='"+dYearFormat.format(dSalaryMonth.getValue())+"')casualemployee, isnull((select SUM( (mBasic+mHouseRent+mMobileBill+mConveyance+mOtherAllowance)-"
				+ "(((mGross/iTotalMonthDays)*iTotalAbsentDays)+mAdvanceSalary+mProvidentFund+mRevenueStamp)) "+ 
				"from  tbMonthlySalary where vSectionID like temp.vSectionID and vMonthName like '"+dMonth.format(dSalaryMonth.getValue())+"' "+ 
				"and vEmployeeType like 'Casual' and vYear='"+dYearFormat.format(dSalaryMonth.getValue())+"' )  ,0) as casualamount,COUNT(vEmployeeID) totalemployee,  "+
				"ISNULL(sum((mBasic+mHouseRent+mMobileBill+mConveyance+mOtherAllowance)-(((mGross/iTotalMonthDays)*iTotalAbsentDays)+mAdvanceSalary+mProvidentFund+mRevenueStamp)) ,0) "+ 
				"totalamount from tbMonthlySalary temp where vMonthName='"+dMonth.format(dSalaryMonth.getValue())+"' and vYear='"+dYearFormat.format(dSalaryMonth.getValue())+"' group by temp.vSectionID,temp.vSectionName "; 
		
		*/
		
		         System.out.println("Query"+query);

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

				Window win = new ReportViewer(hm,"report/account/hrmModule/RptTopSheet.jasper",
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
		setHeight("250px");

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
		lblSection = new Label("Section Name :");
		lblSection.setImmediate(false);
		lblSection.setWidth("100.0%");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection,"top:60.0px; left:30.0px;");

		// cmbSection
		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("260px");
		cmbSection.setHeight("-1px");
		cmbSection.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbSection, "top:58.0px; left:130.0px;");

		chkSectionAll = new CheckBox("All");
		chkSectionAll.setImmediate(true);
		mainLayout.addComponent(chkSectionAll, "top:60.0px; left:395.0px;");

		// optionGroup
		OGBankType = new OptionGroup("",type);
		OGBankType.setImmediate(true);
		OGBankType.setStyleName("horizontal");
		OGBankType.setValue("Bank");
		mainLayout.addComponent(OGBankType, "top:95.0px;left:130.0px;");

		RadioBtnGroup = new OptionGroup("",group);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:110.0px;left:130.0px;");

		mainLayout.addComponent(new Label("_________________________________________________________________________________________"), "top:135.0px;right:20.0px;left:20.0px;");		
		mainLayout.addComponent(cButton,"top:155.opx; left:140.0px");

		return mainLayout;
	}
}
