package hrm.common.reportform;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

import com.common.share.CommonButton;
import com.common.share.CommonMethod;
import com.common.share.ReportDate;
import com.common.share.ReportOption;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

public class RptIndivitualIncomeTax extends Window{
	AbsoluteLayout mainLayout;
	private SessionBean sessionBean;
	private Label lblFiscalYear;
	private ComboBox cmbFiscalYear;

	private ComboBox cmbEmployeeId;
	private OptionGroup option;
	private static final List options=Arrays.asList(new String[]{"PDF","Other"});
	CommonButton cButton=new CommonButton("","","","","","","","Preview","","Exit");
	private CommonMethod cm;
	private String menuId = "";
	private ReportDate reportTime = new ReportDate();
	public RptIndivitualIncomeTax(SessionBean sessionBean,String menuId)
	{
		this.sessionBean=sessionBean;
		this.setWidth("500px");
		this.setHeight("230px");
		this.center();
		this.setResizable(false);
		this.setCaption("INDIVITUAL INCOME TAX :: "+sessionBean.getCompany());
		this.setStyleName("cwindow");
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		this.setContent(buildMainLayout());
		btnAction();
		cmbFiscalYearDataLoad();
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
	private void cmbFiscalYearDataLoad()
	{

		Session session=SessionFactoryUtil.getInstance().openSession();
		try{
			String sql="select distinct 0,dFiscalYear from tbIncomeTaxInfo";
			Iterator<?> iter=session.createSQLQuery(sql).list().iterator();
			while(iter.hasNext())
			{
				Object element[]=(Object[])iter.next();
				cmbFiscalYear.addItem(element[1]);
			}
		}catch(Exception exp)
		{
			showNotification("Employee :",Notification.TYPE_WARNING_MESSAGE);
		}
	}
	private void btnAction()
	{
		cmbFiscalYear.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				cmbEmployeeId.removeAllItems();
				if(!cmbFiscalYear.getValue().toString().isEmpty())
				{				
					cmbEmployeeDataLoad();

				}
			}
		});	

		cButton.btnPreview.addListener(new ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if (cmbEmployeeId.getValue() != null) 
				{
					reportShow();
				} 
				else 
				{
					showNotification("Please Select JV Control Head",Notification.TYPE_HUMANIZED_MESSAGE);
				}
			}
		});


		cButton.btnExit.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {


				close();

			}
		});

	}

	private void reportShow()
	{

		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql="select Inc.vEmployeeId,Inc.employeeCode,Inc.vEmployeeName,Inc.vDesignationId,Inc.vDesignationName,"
					+ "Inc.dTaxYear,Inc.dFiscalYear,Inc.dDate,Inc.mFirst,Inc.mFirstSub1,Inc.mFirstSub2,inc.mNext,"
					+ "Inc.mNextSub1,Inc.mNextSub2,Inc.mNextSecond,Inc.mNextSecondSub1,inc.mNextSecondSub2,Inc.mNextThird,"
					+ "Inc.mNextThirdSub1,Inc.mNextThirdSub2,Inc.mBasic,Inc.mHouseRent,Inc.mMedicalAllowance,"
					+ "Inc.EducationAllowance,Inc.mFestivalBonus,Inc.mInsentiveBonus,0 mNetPayable,Inc.mContributes,"
					+ "Inc.mBPPF,mBPPFLess,Inc.mOther,isnull((Inc.mBasic+Inc.mHouseRent+Inc.mMedicalAllowance+Inc.EducationAllowance+Inc.mFestivalBonus+Inc.mInsentiveBonus+Inc.mContributes+Inc.mBPPF+Inc.mOther),0)TotalIncome,ISNULL((Inc.mBasic+Inc.mFestivalBonus+Inc.mInsentiveBonus+Inc.mBPPF),0)TaxTotalIncome,Inc.mCoContributes,"
					+ "Inc.LifeInsurence,Inc.StockAndShare,Inc.SavingCertificate,Inc.DPSaps,Inc.mTaxRebate,Inc.mAdvanceTax,"
					+ "Inc.DesktopPurchase,ISNULL((Inc.mCoContributes+Inc.LifeInsurence+Inc.StockAndShare+Inc.SavingCertificate+Inc.DPSaps+Inc.DesktopPurchase),0)TotalActualIncome,Inc.mNetTaxPayable,isnull((Inc.mFirstSub2+Inc.mNextSub2+Inc.mNextSecondSub2+Inc.mNextThirdSub2),0)TotalPayleTAx,"
					+ "cek.mCheckNo,cek.mCheckDate,cek.mChallanNo,cek.mChallanDate,cek.mAmount,Inc.mMinimumTax,Inc.mActualNetTaxPayable  " +
					"from tbIncomeTaxInfo Inc " +
					/*"left join tbLeaveEncashement lbe on Inc.vEmployeeId=lbe.vEmployeeId " +*/
					"Left join tbCheckDetails cek on Inc.dFiscalYear=cek.vFiscalYearId where inc.vEmployeeId like '"+cmbEmployeeId.getValue().toString()+"' ";

			System.out.println("reportShow: "+sql);

			if(queryValueCheck(sql))
			{
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone",sessionBean.getCompanyContact());
				hm.put("username", sessionBean.getUserName()+" "+sessionBean.getUserIp());
				hm.put("SysDate",reportTime.getTime);
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("section", cmbEmployeeId.getItemCaption(cmbEmployeeId.getValue()));
				hm.put("sql", sql);

				ReportOption RadioBtn= new ReportOption(option.getValue().toString());

				Window win = new ReportViewer(hm,"report/account/hrmModule/rptIndivitualIncomeTax.jasper",
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
		finally{session.close();}

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

	private void cmbEmployeeDataLoad()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		try{
			String sql="select vEmployeeId,employeeCode from tbIncomeTaxInfo  where dFiscalYear like '"+cmbFiscalYear.getValue().toString()+"'";
			Iterator<?> iter=session.createSQLQuery(sql).list().iterator();
			while(iter.hasNext())
			{
				Object element[]=(Object[])iter.next();
				cmbEmployeeId.addItem(element[0]);
				cmbEmployeeId.setItemCaption(element[0], element[1].toString());
			}
		}catch(Exception exp)
		{
			showNotification("Section :",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private AbsoluteLayout buildMainLayout() {
		mainLayout=new AbsoluteLayout();

		// lblEmployeeName
		lblFiscalYear = new Label("Fiscal Year :");
		lblFiscalYear.setImmediate(false);
		lblFiscalYear.setWidth("-1px");
		lblFiscalYear.setHeight("-1px");
		mainLayout.addComponent(lblFiscalYear, "top:30.0px; left:45.0px;");

		// txtEmployeeName
		cmbFiscalYear = new ComboBox();
		cmbFiscalYear.setImmediate(true);
		cmbFiscalYear.setWidth("110px");
		cmbFiscalYear.setHeight("-1px");
		mainLayout.addComponent(cmbFiscalYear, "top:28.0px; left:150.0px;");


		cmbEmployeeId=new ComboBox();
		cmbEmployeeId.setWidth("230px");
		cmbEmployeeId.setImmediate(true);
		mainLayout.addComponent(new Label("Employee ID :"),"top:60px; left:45px");
		mainLayout.addComponent(cmbEmployeeId,"top:58px; left:150px");


		option=new OptionGroup("",options);
		option.setImmediate(true);
		mainLayout.addComponent(option,"top:100px; left:160px");
		option.setStyleName("horizontal");
		option.setValue("PDF");

		mainLayout.addComponent(new Label("<b><font color=#000000>____________________________________________________________________________________</font></b>",Label.CONTENT_XHTML),"top:120px; left:5px; right:5px");
		mainLayout.addComponent(cButton,"top:145px; left:150px");
		cButton.setImmediate(true);
		return mainLayout;
	}


}
