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
import com.common.share.CommonMethod;
import com.common.share.FocusMoveByEnter;
import com.common.share.GenerateExcelReport;
import com.common.share.ReportDate;
import com.common.share.ReportOption;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button.ClickEvent;


@SuppressWarnings("serial")
public class RptMonthlyHouseAllowanceSheet extends Window {

	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	
	private ComboBox cmbEmpType,cmbUnit;

	private ComboBox cmbSectionName,cmbDepartmentName;
	private CheckBox chkSectionAll,chkDepartmentAll;
	private CheckBox chkEmployeeTypeAll;
	private ComboBox cmbMonth;

	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
	ArrayList<Component> allComp = new ArrayList<Component>();

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();
	
	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other","Excel"});

	SimpleDateFormat dRptFormat = new SimpleDateFormat("dd-MM-yyyy");
	SimpleDateFormat dMonthYear = new SimpleDateFormat("MMMMM-yyyy");
	
	private CommonMethod cm;
	private String menuId = "";
	public RptMonthlyHouseAllowanceSheet(SessionBean sessionBean,String menuId)
	{
		this.sessionBean=sessionBean;
		this.setCaption("MONTHLY HOUSE ALLOWANCE SHEET :: "+sessionBean.getCompany());
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		buildMainLayout();
		setContent(mainLayout);
		cmbMonthDataLoad();
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
	private void cmbMonthDataLoad()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct dSalaryDate,CONVERT(varchar,DATENAME(MONTH,dSalaryDate)+'-'+DATENAME(YEAR,dSalaryDate))monthyear from tbMonthlySalary order by dSalaryDate desc";
			System.out.println("cmbMonthDataLoad: "+query);
			
			List <?> list = session.createSQLQuery(query).list();

			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbMonth.addItem(element[0]);
				cmbMonth.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbMonthDataLoad",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	public void addUnitData()
	{
		cmbUnit.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vUnitId,vUnitName from tbMonthlySalary where MONTH(dSalaryDate)=MONTH('"+cmbMonth.getValue()+"') " +
					"and YEAR(dSalaryDate)=YEAR('"+cmbMonth.getValue()+"') order by vUnitName ";

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
			String query="select distinct vDepartmentId,vDepartmentName from tbMonthlySalary " +
					" where MONTH(dSalaryDate)=MONTH('"+cmbMonth.getValue()+"') and YEAR(dSalaryDate)=YEAR('"+cmbMonth.getValue()+"') "+
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
			String query="select distinct vSectionId,vSectionName from tbMonthlySalary where MONTH(dSalaryDate)=MONTH('"+cmbMonth.getValue()+"') and YEAR(dSalaryDate)=YEAR('"+cmbMonth.getValue()+"') "+
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

	public void addEmployeeType()
	{
		String dept="%",secId="%";
		
		if(!chkDepartmentAll.booleanValue())
		{
			dept=cmbDepartmentName.getValue().toString();
		}
		if(!chkSectionAll.booleanValue())
		{
			secId=cmbSectionName.getValue().toString();
		}
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		
		try
		{
			String query = "select distinct vEmployeeType from tbMonthlySalary " +
					"where MONTH(dSalaryDate)=MONTH('"+cmbMonth.getValue()+"') and YEAR(dSalaryDate)=YEAR('"+cmbMonth.getValue()+"') " +
					"and vUnitId='"+cmbUnit.getValue().toString()+"' and vDepartmentId like '"+dept+"' and vSectionId like '"+secId+"' " +
					"order by vEmployeeType";
			System.out.println("addEmployeeType: "+query);
			
			List <?> list = session.createSQLQuery(query).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				cmbEmpType.addItem(iter.next().toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("addEmployeeType",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	public void setEventAction()
	{
		cmbMonth.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbUnit.removeAllItems();
				if(cmbMonth.getValue()!=null)
				{
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
				cmbSectionName.removeAllItems();
				chkSectionAll.setValue(false);
				cmbSectionName.setEnabled(true);
				if(cmbUnit.getValue()!=null)
				{
					if(chkDepartmentAll.booleanValue())
					{
						cmbDepartmentName.setValue(null);
						cmbDepartmentName.setEnabled(false);
						addSectionData();
					}
					else
						cmbDepartmentName.setEnabled(true);
					
				}
				else
					chkDepartmentAll.setValue(false);
			}
		});		

		cmbSectionName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{

				cmbEmpType.removeAllItems();
				chkEmployeeTypeAll.setValue(false);
				cmbEmpType.setEnabled(true);
				if(cmbDepartmentName.getValue()!=null || chkDepartmentAll.booleanValue())
				{
					if(cmbSectionName.getValue()!=null)
					{
						cmbEmpType.removeAllItems();
						addEmployeeType();
					}
				}
			}
		});		
		chkSectionAll.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				cmbEmpType.removeAllItems();
				chkEmployeeTypeAll.setValue(false);
				cmbEmpType.setEnabled(true);
				if(cmbDepartmentName.getValue()!=null || chkDepartmentAll.booleanValue())
				{
					if(chkSectionAll.booleanValue())
					{
						cmbSectionName.setEnabled(false);
						cmbSectionName.setValue(null);
						addEmployeeType();
					}
					else
					{
						cmbSectionName.setEnabled(true);
					}
				}
				else
					chkSectionAll.setValue(false);
			}
		});
		chkEmployeeTypeAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbSectionName.getValue()!=null || chkSectionAll.booleanValue())
				{
					if(chkEmployeeTypeAll.booleanValue())
					{
						cmbEmpType.setValue(null);
						cmbEmpType.setEnabled(false);
					}
					else
						cmbEmpType.setEnabled(true);
				}
				else
				{
					chkEmployeeTypeAll.setValue(false);
				}
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
							if(cmbEmpType.getValue()!=null || chkEmployeeTypeAll.booleanValue()) 
							{
								reportShow();
							}
							else
							{
								showNotification("Warning","Select Employee Type",Notification.TYPE_WARNING_MESSAGE);
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

	private void reportShow()
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		String query=null;
		String rptName="";
		Session session=SessionFactoryUtil.getInstance().openSession();
		
		try
		{
			query= "select vEmployeeId from tbMonthlySalary where MONTH(dSalaryDate)=MONTH('"+cmbMonth.getValue()+"') and YEAR(dSalaryDate)=YEAR('"+cmbMonth.getValue()+"') " +
					"and vUnitId like '"+(cmbUnit.getValue()!=null?cmbUnit.getValue().toString():"%")+"' " +
					"and vDepartmentId like '"+(cmbDepartmentName.getValue()!=null?cmbDepartmentName.getValue().toString():"%")+"' " +
					"and vSectionId like '"+(cmbSectionName.getValue()!=null?cmbSectionName.getValue().toString():"%")+"' " +
					"and vEmployeeType like '"+(cmbEmpType.getValue()!=null?cmbEmpType.getValue().toString():"%")+"' " +
					"order by vUnitId,vDepartmentName";
			
			rptName="rptMonthlyHouseAllowanceSheet.jasper";

			System.out.println("SALARY "+query);

			if(queryValueCheck(query))
			{
				query= "select vEmployeeCode,vEmployeeName,vDesignationName,vDepartmentName,dJoiningDate,mHouseRent,vSalaryMonth,vSalaryYear " +
						"from tbMonthlySalary where MONTH(dSalaryDate)=MONTH('"+cmbMonth.getValue()+"') and YEAR(dSalaryDate)=YEAR('"+cmbMonth.getValue()+"') " +
						"and vUnitId like '"+(cmbUnit.getValue()!=null?cmbUnit.getValue().toString():"%")+"' " +
						"and vDepartmentId like '"+(cmbDepartmentName.getValue()!=null?cmbDepartmentName.getValue().toString():"%")+"' " +
						"and vSectionId like '"+(cmbSectionName.getValue()!=null?cmbSectionName.getValue().toString():"%")+"' " +
						"and vEmployeeType like '"+(cmbEmpType.getValue()!=null?cmbEmpType.getValue().toString():"%")+"' " +
						"order by vUnitId,vDepartmentName,vSectionName,SUBSTRING(vEmployeeCode,3,15)";
				
				System.out.println("reportShow: "+query);
				
				//==========Didarul Alam Update By: 16-10-2018 Start==========//
				if(RadioBtnGroup.getValue()=="Excel")
				{
					String loc = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/temp/attendanceFolder";
					String fname = "MonthlySalary.xls";
					String url = getWindow().getApplication().getURL()+"VAADIN/themes/temp/attendanceFolder/"+fname;
					String strColName[]={"SL#","Code","Grade","Name","Designation","Joining Date","WD","HD","PD","LvD","Abs","LWP","NPD","LateD","Basic","House Rent","Conveyance","Medical Allowance","Mobile Allowance","Special Allowance","Gross Total","Attn. Bonus","Total Earning","Loan Amount","Advance Deduction","Late Attn. Deduction","Income Tex","Total Deduction","Net Pay","Signature"};
					String Header="For the Month Of: "+cmbMonth.getItemCaption(cmbMonth.getValue());
					String exelSql="";
					
					exelSql = "select distinct vSectionName,vEmployeeType,vSectionID,vUnitId,vDepartmentName,vDepartmentId from tbMonthlySalary " +
							"where vEmployeeType like '"+(cmbEmpType.getValue()!=null?cmbEmpType.getValue().toString():"%")+"' " +
							"and vUnitId like '"+cmbUnit.getValue().toString()+"' " +
							"and vDepartmentId like '"+(cmbDepartmentName.getValue()!=null?cmbDepartmentName.getValue().toString():"%")+"' " +
							"and vSectionID like '"+(cmbSectionName.getValue()!=null?cmbSectionName.getValue().toString():"%")+"' " +
							"and MONTH(dSalaryDate)=MONTH('"+cmbMonth.getValue()+"') " +
							"and YEAR(dSalaryDate)=YEAR('"+cmbMonth.getValue()+"') " +
							"order by vSectionID,vEmployeeType,vUnitId";
					
					System.out.println("exelSql: "+exelSql);
					
					List <?> lst1=session.createSQLQuery(exelSql).list();
							
					String detailQuery[]=new String[lst1.size()];
					String [] signatureOption = {"HEAD OF HR","HEAD OF ACCOUNTS","GROUP C.E.O / CHAIRMAN"};
					//String [] signatureOption = new String [0];
					String [] groupItem=new String[lst1.size()];
					Object [][] GroupElement=new Object[lst1.size()][];
					String [] GroupColName=new String[0];
					int countInd=0;
					
					for(Iterator<?> iter=lst1.iterator(); iter.hasNext();)
					{
						 Object [] element = (Object[])iter.next();
							groupItem[countInd]="Department  : "+element[4].toString()+"                                                Employee Type : "+element[1].toString();
							GroupElement[countInd]=new Object [] {(Object)"",(Object)"Department : ",element[4],(Object)"Employee Type : ",element[1]};
						
							detailQuery[countInd]="select vEmployeeCode,vGradeName,vEmployeeName,vDesignation,dJoiningDate," +
									"iWorkingDay,totalHoliday,iPresentDay,iLeaveDay,iAbsentDay,iLeaveWithoutPay,iNetPayableDays,iLateDay," +
									"CAST(ISNULL(mBasic,0) as FLOAT)mBasic,CAST(ISNULL(mHouseRent,0) as FLOAT)mHouseRent,CAST(ISNULL(mConveyance,0) as FLOAT)mConveyance," +
									"CAST(ISNULL(mMedicalAllowance,0) as FLOAT)mMedicalAllowance,CAST(ISNULL(mOtherAllowance,0) as FLOAT)mOtherAllowance," +
									"CAST(ISNULL(mSpecialAllowance,0) as FLOAT)mSpecialAllowance,CAST(ISNULL(Gross,0) as FLOAT)Gross,CAST(ISNULL(PresentBonus,0) as FLOAT)PresentBonus," +
									"CAST(ISNULL(totalEarning,0) as FLOAT)totalEarning,CAST(ISNULL(mLoanAmount,0)  as FLOAT)mLoanAmount, " +
									"CAST(ISNULL(mAdvanceSalary,0) as FLOAT)mAdvanceSalary,CAST(ISNULL(mLateAmount,0)  as FLOAT)mLateAmount,CAST(ISNULL(mIncomeTax,0) as FLOAT)mIncomeTax," +
									"CAST(ISNULL(Deduction,0) as FLOAT)Deduction,CAST(ISNULL(NetPay,0) as FLOAT)NetPay,'' Signature " +
									"from funMonthlySalary (" +
									"'"+element[3].toString()+"', " +
									"'"+cmbMonth.getValue()+"'," +
									"'"+element[5].toString()+"'," +
									"'"+element[2].toString()+"'," +
									"'"+element[1].toString()+"') " +
									"order by vSectionID,iRank,dJoiningDate ";
							
						System.out.println("Details query :"+detailQuery[countInd]);
						countInd++;
						
					}
					
					new GenerateExcelReport(sessionBean, loc, url, fname, "MONTHLY  SALARY", "MONTHLY  SALARY",
							Header, strColName, 2, groupItem, GroupColName, GroupElement, 1, detailQuery, 14, 25, "A4",
							"Landscape",signatureOption,cmbUnit.getItemCaption(cmbUnit.getValue()));
					
					Window window = new Window();
					getApplication().addWindow(window);
					getWindow().open(new ExternalResource(url),"_blank",500,200,Window.BORDER_NONE);
				}
				else
				{
					HashMap <String,Object>  hm = new HashMap <String,Object> ();
					hm.put("company", sessionBean.getCompany());
					hm.put("address", sessionBean.getCompanyAddress());
					hm.put("phone",sessionBean.getCompanyContact());
					hm.put("Month",cmbMonth.getItemCaption(cmbMonth.getValue()));
					hm.put("user", sessionBean.getUserName()+" "+sessionBean.getUserIp());
					hm.put("SysDate",reportTime.getTime);
					hm.put("logo", sessionBean.getCompanyLogo());
					hm.put("unit", cmbUnit.getItemCaption(cmbUnit.getValue()));
					hm.put("sql", query);
					
					Window win = new ReportViewer(hm,"report/account/hrmModule/"+rptName,
							this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
							this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
							this.getWindow().getApplication().getURL()+"VAADIN/applet",RadioBtn.Radio);
	
					win.setCaption("Project Report");
					this.getParent().getWindow().addWindow(win);
				}

				//========== Didarul Alam Update By: 16-10-2018 End ==========//
			}
			else
			{
				showNotification("Warning","There are no Data",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error "+exp,Notification.TYPE_ERROR_MESSAGE);
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
		allComp.add(cmbUnit);
		allComp.add(cmbDepartmentName);
		allComp.add(cmbSectionName);
		allComp.add(cmbEmpType);
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

		mainLayout.addComponent(new Label("Month :"), "top:30.0px; left:30.0px;");
		
		cmbMonth=new ComboBox();
		cmbMonth.setImmediate(true);
		cmbMonth.setWidth("140.0px");
		cmbMonth.setHeight("24.0px");
		mainLayout.addComponent(cmbMonth, "top:28px;left:130.0px;");

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


		cmbEmpType = new ComboBox();
		cmbEmpType.setImmediate(false);
		cmbEmpType.setWidth("260px");
		cmbEmpType.setHeight("-1px");
		cmbEmpType.setNullSelectionAllowed(true);
		cmbEmpType.setImmediate(true);
		mainLayout.addComponent(new Label("Employee Type :"), "top:150px; left:30.0px;");
		mainLayout.addComponent(cmbEmpType, "top:148.0px; left:130.0px;");

		chkEmployeeTypeAll = new CheckBox("All");
		chkEmployeeTypeAll.setHeight("-1px");
		chkEmployeeTypeAll.setWidth("-1px");
		chkEmployeeTypeAll.setImmediate(true);
		mainLayout.addComponent(chkEmployeeTypeAll, "top:150.0px; left:396.0px;");

		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:180.0px;left:130.0px;");
		RadioBtnGroup.setVisible(false);

		//mainLayout.addComponent(new Label("______________________________________________________________________________"), "top:200.0px; left:20.0px; right:20.0px;");
		mainLayout.addComponent(cButton,"bottom:20px; left:150.0px");
		return mainLayout;
	}
}