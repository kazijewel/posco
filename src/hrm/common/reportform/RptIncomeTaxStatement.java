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
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

public class RptIncomeTaxStatement extends Window{

	AbsoluteLayout mainLayout;
	private SessionBean sessionBean;
	private Label lblFiscalYear;
	private ComboBox cmbFiscalYear;
	private CheckBox chkEmpIDAll=new CheckBox("All");

	private ComboBox cmbEmployeeId;
	private OptionGroup option;
	private static final List options=Arrays.asList(new String[]{"PDF","Other"});
	CommonButton cButton=new CommonButton("","","","","","","","Preview","","Exit");
	private CommonMethod cm;
	private String menuId = "";
	private ReportDate reportTime = new ReportDate();
	public RptIncomeTaxStatement(SessionBean sessionBean,String menuId)
	{
		this.sessionBean=sessionBean;
		this.setWidth("500px");
		this.setHeight("230px");
		this.center();
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		this.setCaption("INCOME TAX STATEMENT:: "+sessionBean.getCompany());
		this.setStyleName("cwindow");
		this.setContent(buildMainLayout());
		TaxLoad();
		btnAction();
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
	private void btnAction()
	{
		cButton.btnExit.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				close();
			}
		});
		cmbFiscalYear.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(cmbFiscalYear.getValue()!=null)
				{
					cmbEmployeeId.removeAllItems();
					cmbEmployeeDataLoad();
				}
			}
		});
		chkEmpIDAll.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				if(chkEmpIDAll.booleanValue()==true)
				{
					cmbEmployeeId.setValue(null);
					cmbEmployeeId.setEnabled(false);
				}
				else{
					cmbEmployeeId.setEnabled(true);
				}
			}
		});

		cButton.btnPreview.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				if(cmbFiscalYear.getValue()!=null)
				{
					if(cmbEmployeeId.getValue()!=null || chkEmpIDAll.booleanValue())
					{
						reportShow();
					}
					else{
						showNotification("Please Select Employee ID",Notification.TYPE_HUMANIZED_MESSAGE);
					}
				}
				else{
					showNotification("Please Select Fiscal year",Notification.TYPE_HUMANIZED_MESSAGE);
				}
			}
		});
	}

	private void TaxLoad()
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

	private void cmbEmployeeDataLoad()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		try{
			String sql="select vEmployeeID,employeeCode from tbIncomeTaxInfo " +
					"where dFiscalYear = '"+cmbFiscalYear.getItemCaption(cmbFiscalYear.getValue())+"'";

			Iterator<?> iter=session.createSQLQuery(sql).list().iterator();
			while(iter.hasNext())
			{
				Object element[]=(Object[])iter.next();
				cmbEmployeeId.addItem(element[0]);
				cmbEmployeeId.setItemCaption(element[0], element[1].toString());
			}
		}catch(Exception exp)
		{
			showNotification("Employee :",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void reportShow()
	{
		String empId="%";	
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();

		if(!chkEmpIDAll.booleanValue())
		{
			empId=cmbEmployeeId.getValue().toString();
		}

		try
		{
			String sql="select distinct dFiscalYear,employeeCode,vEmployeeName,vDesignationName,ETinNo,mBasic,mHouseRent,mMedicalAllowance,mBPPF,EducationAllowance,mFestivalBonus," +
					"mInsentiveBonus,mleaveEncashment,mContributes,mBPPFLess,mOther," +
					"(mBasic+mHouseRent+mMedicalAllowance+mBPPF+EducationAllowance+mFestivalBonus+mInsentiveBonus+mleaveEncashment+mContributes+mOther)mTotalIncome," +
					"(mBasic+mBPPF+mFestivalBonus+mInsentiveBonus+mBPPFLess)TaxableIncome,mTotal,mTotalTax,mTaxRebate,mAdvanceTax,mActualNetTaxPayable " +
					"from tbIncomeTaxInfo " +
					"where vEmployeeId like '"+empId+"' and dFiscalYear= '"+cmbFiscalYear.getValue()+"'";

			System.out.println(" reportShow: "+sql);

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
				Window win = new ReportViewer(hm,"report/account/hrmModule/RptStatementOfIncome.jasper",
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

	private AbsoluteLayout buildMainLayout() {
		mainLayout=new AbsoluteLayout();

		// lblEmployeeName
		lblFiscalYear = new Label("Fiscal Year :");
		lblFiscalYear.setImmediate(false);
		lblFiscalYear.setWidth("-1px");
		lblFiscalYear.setHeight("-1px");
		mainLayout.addComponent(lblFiscalYear, "top:30.0px; left:45.0px;");

		// txtEmployeeName
		cmbFiscalYear= new ComboBox();
		cmbFiscalYear.setImmediate(true);
		cmbFiscalYear.setWidth("110px");
		cmbFiscalYear.setHeight("22px");
		mainLayout.addComponent(cmbFiscalYear, "top:28.0px; left:150.0px;");	


		cmbEmployeeId=new ComboBox();
		cmbEmployeeId.setWidth("210px");
		cmbEmployeeId.setImmediate(true);
		mainLayout.addComponent(new Label("Employee ID :"),"top:60px; left:45px");
		mainLayout.addComponent(cmbEmployeeId,"top:58px; left:150px");

		chkEmpIDAll=new CheckBox("All");
		chkEmpIDAll.setImmediate(true);
		mainLayout.addComponent(chkEmpIDAll,"top:58px; left:370px");


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
