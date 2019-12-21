	package hrm.common.reportform;

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

public class RptMonthlySalaryByEmployeeType extends Window {

		private SessionBean sessionBean;
		private AbsoluteLayout mainLayout;

		private Label lblSection;
		private Label lblSalaryMonth;

		private ComboBox cmbSection;
		private CheckBox chkSectionAll;
		private PopupDateField dSalaryMonth;

		private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
		ArrayList<Component> allComp = new ArrayList<Component>();

		CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
		private ReportDate reportTime = new ReportDate();
		
		private OptionGroup RadioBtnEmployeeType;
		private static final List<String> type2=Arrays.asList(new String[]{"Officer","Staff & Workers"});

		private OptionGroup RadioBtnGroup;
		private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});

		SimpleDateFormat dRptFormat = new SimpleDateFormat("dd-MM-yyyy");


		public RptMonthlySalaryByEmployeeType(SessionBean sessionBean)
		{
			this.sessionBean=sessionBean;
			this.setCaption("MONTHLY SALARY BY EMPLOYEE TYPE :: "+sessionBean.getCompany());
			this.setResizable(false);

			buildMainLayout();
			setContent(mainLayout);
			cmbSectionAddData();
			setEventAction();
			focusMove();
		}

		public void cmbSectionAddData()
		{
			cmbSection.removeAllItems();
			Session session=SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String query="select distinct  vSectionID,vSectionName from tbMonthlySalary where vSalaryMonth=DATENAME(MM,'"+dFormat.format(dSalaryMonth.getValue())+"')  order by vSectionName";
				
				
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
					{
						cmbSectionAddData();
					}
				}
			});

			chkSectionAll.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					if(chkSectionAll.booleanValue())
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


			cButton.btnPreview.addListener( new Button.ClickListener() 
			{
				public void buttonClick(ClickEvent event) 
				{
					if(cmbSection.getValue()!=null || chkSectionAll.booleanValue()==true)
					{
						if(dSalaryMonth.getValue()!=null)
						{
								/*if(RadioBtnEmployeeType.getValue()!=null)
								{*/
									getAlldata();
							/*	}
								else
								{
									showNotification("Select Employee Type",Notification.TYPE_WARNING_MESSAGE);
								}*/								
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

			cButton.btnExit.addListener( new Button.ClickListener() 
			{
				public void buttonClick(ClickEvent event) 
				{
					close();
				}
			});
		}
		
		private void getAlldata()
		{
			String sectionName = "%";

			if(cmbSection.getValue()!=null)
				sectionName = cmbSection.getValue().toString();
			    

			reportShow(sectionName);
		}

		private void reportShow(Object sectionName)
		{
			ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
			String query=null;
			String rptName="";
			try
			{
				HashMap <String,Object>  hm = new HashMap <String,Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone",sessionBean.getCompanyContact());
				hm.put("Month",dFormat.format(dSalaryMonth.getValue()));
				hm.put("username", sessionBean.getUserName()+" "+sessionBean.getUserIp());
				hm.put("SysDate",reportTime.getTime);
				hm.put("logo", sessionBean.getCompanyLogo());
				//hm.put("developer", sessionBean.getDeveloperAddress());

				{
					if(RadioBtnEmployeeType.getValue()=="Officer")
					{
					query=" select (select dJoiningDate from tbEmpOfficialPersonalInfo ei where ei.vEmployeeId = ms.vEmployeeID )dJoinningDate,ms.vEmployeeName,ms.vDesignation,vSectionID,vSectionName,iTotalDay,iHoliday,iWorkingDay,iAbsentDay,iPresentDay,iLeaveDay,iLeaveWithoutPay,ROUND(mBasic,0)Basic,ROUND(mHouseRent,0)HouseRent," +
							"ROUND(mConveyance,0)Conveyance,ROUND(mMedicalAllowance,0)Medical,ROUND(mGross,0)Gross," +
							"ROUND(mPresentBonus,0)PresentBonus,(ROUND(mGross,0)+ROUND(mPresentBonus,0)+ ROUND(mTiffinAllowance,0))GrossPayable," +
							"Round((mGross/iTotalDay)*iAbsentDay,0)AbsentAmount,Round((mGross*5)/100,0)ProvidentFund," +
							"ROUND(mLoanAmount,0)LoanAmount,ROUND(mAdvanceSalary,0)AdvanceSalary," +
							"ROUND(mTiffinAllowance,0)Tiffin,ROUND((mMobileAllowance),0)MobileAllowance,Isnull(ROUND((mMobileAllowanceExtra),0),0)MobileAllowanceExtra,ROUND(mMealCharge,0)MealCharge," +
							"ROUND(mCompansation,0)Compensation,ROUND(mRevenueStamp,0)RevenueStamp," +
							"ROUND(((mGross/iTotalDay)*iAbsentDay)+((mGross*5)/100)+mLoanAmount+mAdvanceSalary+mMealCharge+mCompansation+Isnull((mMobileAllowanceExtra),0)+mRevenueStamp,0)TotalDeduction," +
							"round((((mGross+mPresentBonus+mTiffinAllowance)-(+mMealCharge+mLoanAmount+mAdvanceSalary+mCompansation+Isnull((mMobileAllowanceExtra),0)+round((mGross/iTotalDay)*iAbsentDay,0)+round(mGross*5/100,0)))-mRevenueStamp),0) PayableAmount,ms.vEmployeeCode,ms.vEmployeeType" +
							" , vSalaryMonth,vSectionName,vSalaryYear,ms.vUserName,mIncomeTax,st.vServiceType from tbMonthlySalary ms INNER JOIN tbEmpOfficialPersonalInfo st ON st.vEmployeeId=ms.vEmployeeID INNER JOIN tbDesignationInfo di ON di.vDesignation=ms.vDesignation where vSalaryMonth=DATENAME(MM,'"+dFormat.format(dSalaryMonth.getValue())+"') and " +
							"vSalaryYear=YEAR('"+dFormat.format(dSalaryMonth.getValue())+"') and " +
							"ms.vEmployeeType like '%' and vSectionID like'"+(cmbSection.getValue()!=null?cmbSection.getValue().toString():"%")+"' and st.vServiceType in('Officer','Management') order by SUBSTRING(vSectionName, 1, 1) ASC, di.iDesignationSerial, ms.dJoiningDate ASC";

					rptName="rptMonthlySalary 1.jasper";
										
					}
					else
					{
						query=" select (select dJoiningDate from tbEmpOfficialPersonalInfo ei where ei.vEmployeeId = ms.vEmployeeID )dJoinningDate,ms.vEmployeeName,ms.vDesignation,vSectionID,vSectionName,iTotalDay,iHoliday,iWorkingDay,iAbsentDay,iPresentDay,iLeaveDay,iLeaveWithoutPay,ROUND(mBasic,0)Basic,ROUND(mHouseRent,0)HouseRent," +
								"ROUND(mConveyance,0)Conveyance,ROUND(mMedicalAllowance,0)Medical,ROUND(mGross,0)Gross," +
								"ROUND(mPresentBonus,0)PresentBonus,(ROUND(mGross,0)+ROUND(mPresentBonus,0)+ ROUND(mTiffinAllowance,0))GrossPayable," +
								"Round((mGross/iTotalDay)*iAbsentDay,0)AbsentAmount,Round((mGross*5)/100,0)ProvidentFund," +
								"ROUND(mLoanAmount,0)LoanAmount,ROUND(mAdvanceSalary,0)AdvanceSalary," +
								"ROUND(mTiffinAllowance,0)Tiffin,ROUND((mMobileAllowance),0)MobileAllowance,Isnull(ROUND((mMobileAllowanceExtra),0),0)MobileAllowanceExtra,ROUND(mMealCharge,0)MealCharge," +
								"ROUND(mCompansation,0)Compensation,ROUND(mRevenueStamp,0)RevenueStamp," +
								"ROUND(((mGross/iTotalDay)*iAbsentDay)+((mGross*5)/100)+mLoanAmount+mAdvanceSalary+mMealCharge+mCompansation+Isnull((mMobileAllowanceExtra),0)+mRevenueStamp,0)TotalDeduction," +
								"round((((mGross+mPresentBonus+mTiffinAllowance)-(+mMealCharge+mLoanAmount+mAdvanceSalary+mCompansation+Isnull((mMobileAllowanceExtra),0)+round((mGross/iTotalDay)*iAbsentDay,0)+round(mGross*5/100,0)))-mRevenueStamp),0) PayableAmount,ms.vEmployeeCode,ms.vEmployeeType" +
								" , vSalaryMonth,vSectionName,vSalaryYear,ms.vUserName,mIncomeTax,st.vServiceType from tbMonthlySalary ms INNER JOIN tbEmpOfficialPersonalInfo st ON st.vEmployeeId=ms.vEmployeeID INNER JOIN tbDesignationInfo di ON di.vDesignation=ms.vDesignation where vSalaryMonth=DATENAME(MM,'"+dFormat.format(dSalaryMonth.getValue())+"') and " +
								"vSalaryYear=YEAR('"+dFormat.format(dSalaryMonth.getValue())+"') and " +
								"ms.vEmployeeType like '%' and vSectionID like'"+(cmbSection.getValue()!=null?cmbSection.getValue().toString():"%")+"' and st.vServiceType not in('Officer','Management') order by SUBSTRING(vSectionName, 1, 1) ASC, di.iDesignationSerial, ms.dJoiningDate ASC";
						
						rptName="rptMonthlySalary 1.jasper";
					}
				}
				System.out.println("REport"+query);

				if(queryValueCheck(query))
				{
					hm.put("sql", query);

					Window win = new ReportViewer(hm,"report/account/hrmModule/"+rptName,
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
			setWidth("430px");
			setHeight("270px");
			
			// optionGroup
			RadioBtnEmployeeType = new OptionGroup("",type2);
			RadioBtnEmployeeType.setImmediate(true);
			RadioBtnEmployeeType.setStyleName("horizontal");
			RadioBtnEmployeeType.setValue("Officer");
			mainLayout.addComponent(RadioBtnEmployeeType, "top:15.0px;left:130.0px;");

			// lblSalaryMonth
			lblSalaryMonth = new Label("Month :");
			lblSalaryMonth.setImmediate(false);
			lblSalaryMonth.setWidth("100.0%");
			lblSalaryMonth.setHeight("-1px");
			mainLayout.addComponent(lblSalaryMonth,"top:50.0px; left:30.0px;");

			// dSalaryMonth
			dSalaryMonth = new PopupDateField();
			dSalaryMonth.setImmediate(true);
			dSalaryMonth.setWidth("140px");
			dSalaryMonth.setHeight("-1px");
			dSalaryMonth.setDateFormat("MMMMM-yyyy");
			dSalaryMonth.setResolution(PopupDateField.RESOLUTION_MONTH);
			dSalaryMonth.setValue(new java.util.Date());
			mainLayout.addComponent(dSalaryMonth, "top:48.0px; left:130.0px;");

			// lblSection
			lblSection = new Label("Division Name :");
			lblSection.setImmediate(false);
			lblSection.setWidth("100.0%");
			lblSection.setHeight("-1px");
			mainLayout.addComponent(lblSection,"top:80.0px; left:30.0px;");

			// cmbSection
			cmbSection = new ComboBox();
			cmbSection.setImmediate(true);
			cmbSection.setWidth("200px");
			cmbSection.setHeight("-1px");
			cmbSection.setNullSelectionAllowed(true);
			mainLayout.addComponent(cmbSection, "top:78.0px; left:130.0px;");

			// chkEmployeeAll
			chkSectionAll = new CheckBox("All");
			chkSectionAll.setImmediate(true);
			chkSectionAll.setWidth("-1px");
			chkSectionAll.setHeight("-1px");
			mainLayout.addComponent(chkSectionAll, "top:78.0px;left:330.0px;");

			//lblEmpType
		/*	lblEmpType=new Label("Employee Type : ");
			mainLayout.addComponent(lblEmpType, "top:105.0px;left:30.0px;");*/

			//cmbEmpType
		/*	cmbEmpType=new ComboBox();
			cmbEmpType.setImmediate(true);
			cmbEmpType.addItem("Permanent");
			cmbEmpType.addItem("Temporary");
			cmbEmpType.addItem("Provisionary");
			cmbEmpType.addItem("Casual");
			cmbEmpType.setWidth("200px");
			cmbEmpType.setHeight("-1px");
			mainLayout.addComponent(cmbEmpType, "top:93.0px;left:130.0px;");*/
			

			// optionGroup
			RadioBtnGroup = new OptionGroup("",type1);
			RadioBtnGroup.setImmediate(true);
			RadioBtnGroup.setStyleName("horizontal");
			RadioBtnGroup.setValue("PDF");
			mainLayout.addComponent(RadioBtnGroup, "top:110.0px;left:130.0px;");

			mainLayout.addComponent(new Label("_________________________________________________________________________________________"), "top:144.0px;right:20.0px;left:20.0px;");		
			mainLayout.addComponent(cButton,"top:170.opx; left:140.0px");

			return mainLayout;
		}

}
