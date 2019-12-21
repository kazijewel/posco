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
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class RptEditMonthlySalary extends Window {

	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblSection;
	private Label lblSalaryMonth;

	private ComboBox cmbUnit;
	private ComboBox cmbSectionName,cmbDepartmentName;
	private CheckBox chkSectionAll,chkDepartmentAll;
	private CheckBox chkEmployeeTypeAll;
	private ComboBox cmbMonth;
	private PopupDateField dSalaryMonth;

	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dMonthFormat = new SimpleDateFormat("MMMMM-yyyy");

	ArrayList<Component> allComp = new ArrayList<Component>();

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});
	private CommonMethod cm;
	private String menuId = "";
	public RptEditMonthlySalary(SessionBean sessionBean,String menuId)
	{
		this.sessionBean=sessionBean;
		this.setCaption("EDIT MONTHLY SALARY :: "+sessionBean.getCompany());
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		buildMainLayout();
		setContent(mainLayout);

		cmbMonthDataLoad();
		//cmbSectionAddData();
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
			String query="select distinct CONVERT(date, (SELECT DATEADD(s,-1,DATEADD(mm, DATEDIFF(m,0,dSalaryDate)+1,0)))) dSalaryDate,CONVERT(varchar,DATENAME(MONTH,dSalaryDate)+'-'+DATENAME(YEAR,dSalaryDate))monthyear from tbUdMonthlySalary order by dSalaryDate desc";
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
			String query="select distinct vUnitId,vUnitName from tbUdMonthlySalary where MONTH(dSalaryDate)=MONTH('"+cmbMonth.getValue()+"') and YEAR(dSalaryDate)=YEAR('"+cmbMonth.getValue()+"') "+
					" order by vUnitName ";

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
			String query="select distinct vDepartmentId,vDepartmentName from tbUdMonthlySalary where MONTH(dSalaryDate)=MONTH('"+cmbMonth.getValue()+"') and YEAR(dSalaryDate)=YEAR('"+cmbMonth.getValue()+"') "+
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
			String query="select distinct vSectionId,vSectionName from tbUdMonthlySalary where MONTH(dSalaryDate)=MONTH('"+cmbMonth.getValue()+"') and YEAR(dSalaryDate)=YEAR('"+cmbMonth.getValue()+"') "+
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
	
		chkSectionAll.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				
				if(cmbDepartmentName.getValue()!=null || chkDepartmentAll.booleanValue())
				{
					if(chkSectionAll.booleanValue())
					{
						cmbSectionName.setEnabled(false);
						cmbSectionName.setValue(null);
						
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
		
								reportShow();
				
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
		try
		{
			HashMap <String,Object> hm = new HashMap <String,Object> ();
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			hm.put("section",cmbSectionName.getItemCaption(cmbSectionName.getValue()));
			hm.put("month",dMonthFormat.format(dSalaryMonth.getValue()));
			hm.put("SysDate",reportTime.getTime);
			hm.put("userName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			hm.put("logo", sessionBean.getCompanyLogo());

			String query="select vUnitId,vUnitName,vEmployeeID,vEmployeeName,vDesignationID,vDesignation,iTotalDay,iHoliday,iWorkingDay,iAbsentDay,iPresentDay,iLeaveDay,iLeaveWithoutPay,round(mBasic,0)mBasic,"
					+ "round(mHouseRent,0)mHouseRent,round(mConveyance,0)mConveyance,round(mMedicalAllowance,0)mMedicalAllowance,"
					+ "round(mGross,0)mGross,round(mPresentBonus,0)mPresentBonus,iAbsentDay,"
					+ "round((mGross/iTotalDay)*iAbsentDay,0)mAbsentAmount,round(mGross*5/100,0)ProvidentFund,"
					+ "round(mGross+mPresentBonus+mTiffinAllowance,0)GrossPayable,"
					+ "round((((mGross+mPresentBonus+mTiffinAllowance)-(+mMealCharge+mLoanAmount+mAdvanceSalary+mCompansation+round((mGross/iTotalDay)*iAbsentDay,0)+round(mGross*5/100,0)))-mRevenueStamp),0)SalaryPayable,"
					+ "round(mLoanAmount,0)mLoanAmount,round(mAdvanceSalary,0)mAdvanceSalary,round(mTiffinAllowance,0)mTiffinAllowance,"
					+ "round(mMealCharge,0)mMealCharge,round(mCompansation,0)mCompansation,mRevenueStamp,vSalaryMonth,vSalaryYear,vUDFlag,"
					+ "vUserName,vUserIP,dEntryTime from tbUDMonthlySalary where "
					+ "vUnitId='"+cmbUnit.getValue()+"' and vSalaryMonth=DATENAME(MM,'"+dFormat.format(dSalaryMonth.getValue())+"') and vSalaryYear=YEAR('"+dFormat.format(dSalaryMonth.getValue())+"') "
					+ " and vDepartmentId like '"+(cmbDepartmentName.getValue()==null?"%":cmbDepartmentName.getValue())+"' "
					+ " and vSectionId like '"+(cmbSectionName.getValue()==null?"%":cmbSectionName.getValue())+"' "
					+ " order by vEmployeeName,dEntryTime desc";
			System.out.println("Report Query EditMonthlySalary: "+query);
			if(queryValueCheck(query))
			{
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/account/hrmModule/rptEditMonthlySalary.jasper",
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
		catch(Exception exp){
			this.getParent().showNotification("Error "+exp,Notification.TYPE_ERROR_MESSAGE);}
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