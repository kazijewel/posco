package com.appform.hrmModule;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.AmountField;
import com.common.share.CommaSeparator;
import com.common.share.CommonButton;
import com.common.share.CommonMethod;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.ReportDate;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class IncomeTax extends Window
{
	private CommonButton cButton = new CommonButton( "New",  "Save",  "Edit",  "",  "Refresh",  "Find", "", "Preview","","Exit");
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;	
	public Table table = new Table();
	
	private ArrayList<Label> lblSl = new ArrayList<Label>();
	private ArrayList<Label> lbltbChequeDD = new ArrayList<Label>();
	private ArrayList<Label> lbltbcheckDate = new ArrayList<Label>();
	private ArrayList<Label> lbltbChallanNo = new ArrayList<Label>();
	private ArrayList<Label> lbltbChallanDate = new ArrayList<Label>();
	private ArrayList<AmountField> txtAmount = new ArrayList<AmountField>();
	
	public DecimalFormat df = new DecimalFormat("#0.00"); 
	
	private Label /*lblInsentive,*/lblBasic,lblFestivalBonus,lblPaidBy,lblEmployeeId,lblEmployeeName,
				/*lblEducationAllowance,*/lblLeaveEncashment,lblOther,lblDesignation,lblJoiningDate,lblEarning,lblDepartment/*,lblEtin*/,lblPFComContribution,
				lblTotalEarning,/*lblBppf,*/lblAllowance,lblHR,lblDesktop,lblTotalActualInvesment,lblMedical,
				lblactual,lblOwnAndComContribution,lblLife,lblStock,lblDPS,lblSaving,
				lblTotalTax,lblTaxRebate,lblAdvance,lblNetTaxPay,lblMinimum;
	
	private AmountField /*txtInsentive,*/txtBasic,txtFestivalBonus,/*txtEducationAllowance,*/txtLeaveEncashment,txtPFComContribution,/*txtBppf,*/txtHR,
				txtFirst,txtFirstTax,txtFirstTk,txtNextOne,txtNextOneTk,txtTaxOne,txtDesktop,txtTotalActualInvesment,txtMedical,txtOwnAndComContribution,txtLife,txtStock,
				txtDPS,txtSaving,txtNextTwo,txtTaxTwo,txtNextTwoTk,txtNextThree,txtTaxThree,txtNextThreeTk,txtTotalTax,txtTaxRebate,txtAdvanceIncomeTax,
				txtNetTaxPay,txtMinimum,txtOther;
	
	private PopupDateField dApplicationDate;
	HashMap <String,Object> hDateDayList = new HashMap <String,Object> ();
	
	private ComboBox cmbEmployeeId;
	private TextField txtFiscalYear;	
	
	private PopupDateField dJoiningDate;	

	private TextRead txtEmployeeName,txtTaxYear,txtDesignation,txtDepartment,/*txtEtin,*/txtTotalEarning,txtSection;
	private TextRead txtEmployeeIDFind = new TextRead();
	private TextRead txtFiscalYearFind = new TextRead();
	
	private ArrayList<Component> allComp = new ArrayList<Component>(); 
	private SimpleDateFormat dFormatSql = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dFormatBangla = new SimpleDateFormat("dd-MM-yyyy");
	
	private ReportDate reportTime = new ReportDate();
	
	private ComboBox cmbUnit;
	
	boolean isSave=false;
	boolean isUpdate=false;
	boolean isRefresh=false;
	boolean isFind=false;

	String empId = "";
	String departmentId = "";
	String designationId = "";
	String findApplicationDate = "";
	private CommonMethod cm;
	private String menuId = "";
	public IncomeTax(SessionBean sessionBean,String menuId)
	{
		this.sessionBean=sessionBean;
		this.setCaption("TAX ASSESSMENT :: "+sessionBean.getCompany());
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		buildMainLayout();
		setContent(mainLayout);
		componentIni(false);
		btnIni(true);
		cButton.btnNew.focus();
		setBtnAction();
		focusEnter();
		tableinitialize();
		cmbUnitDataLoad();
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
	private void setBtnAction()
	{
		txtFiscalYear.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(!txtFiscalYear.getValue().toString().isEmpty())
				{										
					txtTaxYear.setValue(TexYear());
					tableclear();
					tableDataLoad();
				}
			}
		});	

		cmbUnit.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbUnit.getValue()!=null)
				{
					cmbEmployeeId.removeAllItems();
					cmbAddEmployeeDataLoad();
				}
			}
		});

		cmbEmployeeId.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtFiscalYear.getValue().toString().isEmpty())
				{
					if(cmbEmployeeId.getValue()!=null)
					{
						employeeDataSet(cmbEmployeeId.getValue().toString());
						TotanEarningsCalculation();
						ActualInvestotal();
						totalTax();
						netTaxPayable();
					}
				}
			}
		});
		
		txtBasic.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if (!txtBasic.getValue().toString().isEmpty()) {
					TotanEarningsCalculation();
					totalTax();
					netTaxPayable();
				}
			}
		});

		txtFestivalBonus.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if (!txtFestivalBonus.getValue().toString().isEmpty()) {
					TotanEarningsCalculation();
					//totalTax();
					netTaxPayable();
				}
			}
		});

		/*txtInsentive.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if (!txtInsentive.getValue().toString().isEmpty()) {
					TotanEarningsCalculation();
					//totalTax();
					netTaxPayable();
				}

			}
		});*/
		/*txtEducationAllowance.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if (!txtEducationAllowance.getValue().toString().isEmpty()) {
					TotanEarningsCalculation();
					//totalTax();
					netTaxPayable();
				}
			}
		});*/

		txtLeaveEncashment.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if (!txtLeaveEncashment.getValue().toString().isEmpty()) {
					TotanEarningsCalculation();
					//totalTax();
					netTaxPayable();
				}
			}
		});

		txtPFComContribution.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if (!txtPFComContribution.getValue().toString().isEmpty()) {
					TotanEarningsCalculation();
					//totalTax();
					netTaxPayable();
				}
			}
		});

		txtOther.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if (!txtOther.getValue().toString().isEmpty()) {
					TotanEarningsCalculation();
					//totalTax();
					netTaxPayable();
				}
			}
		});

		/*txtBppf.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if (!txtBppf.getValue().toString().isEmpty()) {
					TotanEarningsCalculation();
					//totalTax();
					netTaxPayable();
				}

			}
		});*/
		txtTotalEarning.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				totalTax();
			}
		});

		
		
		
		
		txtOwnAndComContribution.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
		         if(!txtOwnAndComContribution.getValue().toString().isEmpty()){
		        	 ActualInvestotal();
		        	 netTaxPayable();
				}
			}
		});

		txtLife.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtLife.getValue().toString().isEmpty()){
					ActualInvestotal();
					netTaxPayable();
				}}
		});

		txtStock.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
               if(!txtStock.getValue().toString().isEmpty()){
					ActualInvestotal();		
					netTaxPayable();	
				}
			}
		});

		txtDPS.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
	            if(!txtDPS.getValue().toString().isEmpty()){
	            	ActualInvestotal();		
					netTaxPayable();		
				}
			}
		});

		txtSaving.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
	            if(!txtSaving.getValue().toString().isEmpty()){
					ActualInvestotal();		
					netTaxPayable();		
				}						
			}
		});
	
		txtDesktop.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
	            if(!txtDesktop.getValue().toString().isEmpty()){
					ActualInvestotal();
					netTaxPayable();
				}
			}
		});

		txtTotalActualInvesment.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
	            if(!txtTotalActualInvesment.getValue().toString().isEmpty()){
					ActualInvestotal();		
					netTaxPayable();		
				}
			}
		});

		txtFirst.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
	            if(!txtFirst.getValue().toString().isEmpty()){
	            	totalTax();				
				}
			}
		});
		
		txtFirstTax.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				 if(!txtFirstTax.getValue().toString().isEmpty()){
					totalTax();
					netTaxPayable();
				}
			}
		});

		txtFirstTk.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				 if(!txtFirstTk.getValue().toString().isEmpty()){
					totalTax();		
					netTaxPayable();			
				}
			}
		});

		txtNextOne.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				 if(!txtNextOne.getValue().toString().isEmpty()){
					totalTax();
					netTaxPayable();
				 }	
			}
		});
		txtTaxOne.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				 if(!txtTaxOne.getValue().toString().isEmpty()){
					totalTax();
					netTaxPayable();
				 }
			}
		});

		txtNextOneTk.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				 if(!txtNextOneTk.getValue().toString().isEmpty()){
					totalTax();
					netTaxPayable();
				 }
			}
		});

		txtNextTwo.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				 if(!txtNextTwo.getValue().toString().isEmpty()){
					 totalTax();
					netTaxPayable();
				 }	
			}
		});
		
		txtTaxTwo.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				 if(!txtTaxTwo.getValue().toString().isEmpty()){
					totalTax();
					netTaxPayable();
				 }	
			}
		});

		txtNextTwoTk.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				 if(!txtNextTwoTk.getValue().toString().isEmpty()){
					totalTax();	
					netTaxPayable();		
				 }
			}
		});

		txtNextThree.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				 if(!txtNextThree.getValue().toString().isEmpty()){
					totalTax();
					netTaxPayable();
				 }
			}
		});
		txtTaxThree.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				 if(!txtTaxThree.getValue().toString().isEmpty()){
					totalTax();
					netTaxPayable();
				 }	
			}
		});

		txtNextThreeTk.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if (!txtNextThreeTk.getValue().toString().isEmpty()) {
					totalTax();
					netTaxPayable();
				}
			}
		});

		txtTotalTax.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				totalTax();
			}
		});
		txtTaxRebate.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if (!txtTaxRebate.getValue().toString().isEmpty()) {
					//totalTax();
					netTaxPayable();
				}

			}
		});

		txtAdvanceIncomeTax.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if (!txtAdvanceIncomeTax.getValue().toString().isEmpty()) {
					//totalTax();
					netTaxPayable();
				}
			}
		});

		txtNetTaxPay.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if (!txtNetTaxPay.getValue().toString().isEmpty()) {
					//totalTax();
					netTaxPayable();
				}
			}
		});

		txtMinimum.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if (!txtMinimum.getValue().toString().isEmpty()) {
					//totalTax();
					//netTaxPayable();
				}
			}
		});

		
		

		cButton.btnNew.addListener(new ClickListener()		
		{
			public void buttonClick(ClickEvent event)
			{	
				isSave=false;
				isRefresh=false;				
				componentIni(true);				
				btnIni(false);
				txtClear();
				txtFiscalYear.focus();			
			}
		});
		cButton.btnSave.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				formValidation();
			}
		});

		cButton.btnFind.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				findButtonEvent();
			}
		});

		cButton.btnRefresh.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				isUpdate = false;
				componentIni(false);
				txtClear();
				btnIni(true);
			}
		});

		cButton.btnEdit.addListener(new ClickListener()
		{	
			public void buttonClick(ClickEvent event)
			{
				if(cmbEmployeeId.getValue()!=null)
				{
					isUpdate=true;
					componentIni(true);
					btnIni(false);
					cmbUnit.setEnabled(false);
					cmbEmployeeId.setEnabled(false);
				}
				else
				{
					showNotification("There are nothing to edit", Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		cButton.btnExit.addListener(new ClickListener()
		{	
			public void buttonClick(ClickEvent event)
			{
				close();
			}
		});
		cButton.btnPreview.addListener(new ClickListener()
		{	
			public void buttonClick(ClickEvent event)
			{
				if(txtFiscalYear.getValue()!=null)
				{
					if(cmbEmployeeId.getValue()!=null)
					{
						reportShow();
					}
				}
			}
		});
		
	}
	
	public void netTaxPayable()
	{
		double totalTax=Double.parseDouble(txtTotalTax.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtTotalTax.getValue().toString().trim().replaceAll(",", ""));
		double taxRebate=Double.parseDouble(txtTaxRebate.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtTaxRebate.getValue().toString().trim().replaceAll(",", ""));
		double advIncomeTax=Double.parseDouble(txtAdvanceIncomeTax.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtAdvanceIncomeTax.getValue().toString().trim().replaceAll(",", ""));
		double netTaxPayable=totalTax-taxRebate-advIncomeTax;
		txtNetTaxPay.setValue(new CommaSeparator().setComma(netTaxPayable));
		
		txtMinimum.setValue("0");
		if(netTaxPayable<5000)
		{
			txtMinimum.setValue("5000");
		}
		
	}
    public void totalTax()
    {
		/*txtFirstTk.setValue("0.00");
		txtNextOneTk.setValue("0.00");
		txtNextTwoTk.setValue("0.00");
		txtNextThreeTk.setValue("0.00");*/
		
		double FirstTaxTk=0.00;
		double TaxOneTk=0.00;
		double TaxTwoTk=0.00;
		double TaxThreeTk=0.00;
    	double TotalTax=0.00;
		double taxableAmount=0.00;
		
    	double taxBasic=Double.parseDouble(txtBasic.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtBasic.getValue().toString().trim().replaceAll(",", ""));
    	double taxFestivalBonus=Double.parseDouble(txtFestivalBonus.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtFestivalBonus.getValue().toString().trim().replaceAll(",", ""));
    	double tatIncentiveBonus=0; //Double.parseDouble(txtInsentive.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtInsentive.getValue().toString().trim().replaceAll(",", ""));
    	double tatbppf=0; //=Double.parseDouble(txtBppf.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtBppf.getValue().toString().trim().replaceAll(",", ""));
    	
    	/*if(tatbppf>50000)
    	{
    		tatbppf=tatbppf-50000;
    	}*/
    	
    	double taxAmount=taxBasic+taxFestivalBonus+tatIncentiveBonus+tatbppf;
		//System.out.println("Total TI: "+taxAmount);
		
		double First=Double.parseDouble(txtFirst.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtFirst.getValue().toString().trim().replaceAll(",", ""));
		double FirstTax=Double.parseDouble(txtFirstTax.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtFirstTax.getValue().toString().trim().replaceAll(",", ""));
		
		double NextOne=Double.parseDouble(txtNextOne.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtNextOne.getValue().toString().trim().replaceAll(",", ""));	
		double TaxOne=Double.parseDouble(txtTaxOne.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtTaxOne.getValue().toString().trim().replaceAll(",", ""));	
		
		double NextTwo=Double.parseDouble(txtNextTwo.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtNextTwo.getValue().toString().trim().replaceAll(",", ""));
		double TaxTwo=Double.parseDouble(txtTaxTwo.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtTaxTwo.getValue().toString().trim().replaceAll(",", ""));
		
		double NextThree=Double.parseDouble(txtNextThree.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtNextThree.getValue().toString().trim().replaceAll(",", ""));	
		double TaxThree=Double.parseDouble(txtTaxThree.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtTaxThree.getValue().toString().trim().replaceAll(",", ""));	

		//----------- First -----------//
		if(taxAmount>=First)
		{
			taxableAmount=First;
			FirstTaxTk = Double.parseDouble(df.format(taxableAmount*FirstTax/100));			
			txtFirstTk.setValue(new CommaSeparator().setComma(FirstTaxTk));
			taxAmount=taxAmount-taxableAmount;
			System.out.println("taxAmount1: "+taxAmount);
			
			//----------- NextOne -----------//
			if(taxAmount>0)
			{
				if(taxAmount>=NextOne)
				{
					taxableAmount=NextOne;
				}
				else{
					taxableAmount=taxAmount;
				}
				TaxOneTk = Double.parseDouble(df.format(taxableAmount*TaxOne/100));			
				txtNextOneTk.setValue(new CommaSeparator().setComma(TaxOneTk));
				taxAmount=taxAmount-taxableAmount;
				System.out.println("taxAmount2: "+taxAmount);
				
				
				//----------- NextTwo -----------//
				if(taxAmount>0)
				{
					if(taxAmount>=NextTwo)
					{
						taxableAmount=NextTwo;
					}
					else{
						taxableAmount=taxAmount;
					}
					TaxTwoTk = Double.parseDouble(df.format(taxableAmount*TaxTwo/100));			
					txtNextTwoTk.setValue(new CommaSeparator().setComma(TaxTwoTk));
					taxAmount=taxAmount-taxableAmount;
					System.out.println("taxAmount3: "+taxAmount);
					

					//----------- NextThree -----------//
					if(taxAmount>0)
					{
						if(taxAmount>=NextThree)
						{
							taxableAmount=NextThree;
						}
						else{
							taxableAmount=taxAmount;
						}
						TaxThreeTk = Double.parseDouble(df.format(taxableAmount*TaxThree/100));			
						txtNextThreeTk.setValue(new CommaSeparator().setComma(TaxThreeTk));
						taxAmount=taxAmount-taxableAmount;
						System.out.println("taxAmount4: "+taxAmount);
					
					}else{
						txtNextThreeTk.setValue("0.0");
					}
				}
				else{
					txtNextTwoTk.setValue("0.0");
					txtNextThreeTk.setValue("0.0");
				}
			}
			else{
				txtNextOneTk.setValue("0.0");
				txtNextTwoTk.setValue("0.0");
				txtNextThreeTk.setValue("0.0");
			}
		}
		else
		{
			txtFirstTk.setValue("0.0");
			txtNextOneTk.setValue("0.0");
			txtNextTwoTk.setValue("0.0");
			txtNextThreeTk.setValue("0.0");
		}

		
		FirstTaxTk=Double.parseDouble(txtFirstTk.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtFirstTk.getValue().toString().trim().replaceAll(",", ""));
		TaxOneTk=Double.parseDouble(txtNextOneTk.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtNextOneTk.getValue().toString().trim().replaceAll(",", ""));
		TaxTwoTk=Double.parseDouble(txtNextTwoTk.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtNextTwoTk.getValue().toString().trim().replaceAll(",", ""));
		TaxThreeTk=Double.parseDouble(txtNextThreeTk.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtNextThreeTk.getValue().toString().trim().replaceAll(",", ""));
		
		
		TotalTax =(FirstTaxTk+TaxOneTk+TaxTwoTk+TaxThreeTk);	
		txtTotalTax.setValue(new CommaSeparator().setComma(TotalTax));
			
	}

	
	/*public void RebeatTaxLessPart() {
		double Rebeat = Double.parseDouble(txtTaxRebate.getValue().toString().trim().replaceAll(",","").isEmpty()?"0.00":txtTaxRebate.getValue().toString().trim().replaceAll(",", ""));
		double Advance = Double.parseDouble(txtAdvanceIncomeTax.getValue().toString().trim().replaceAll(",", "").isEmpty() ? "0.00":txtAdvanceIncomeTax.getValue().toString().trim().replaceAll(",", ""));
		double NetPAy = Double.parseDouble(txtNetTaxPay.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtNetTaxPay.getValue().toString().trim().replaceAll(",", ""));
		double minimum = 0.00;

		if (!txtTaxRebate.getValue().toString().isEmpty()) {
			
		}
		txtMinimum.setValue(new CommaSeparator().setComma(NetPAy));
	}*/

    public void ActualInvestotal()
    {
		double TotalActualInvestotal=0.00;
		double TotalRebate=0.00;
		double Contr=Double.parseDouble(txtOwnAndComContribution.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtOwnAndComContribution.getValue().toString().trim().replaceAll(",", ""));
		double Life=Double.parseDouble(txtLife.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtLife.getValue().toString().trim().replaceAll(",", ""));
		double Stock=Double.parseDouble(txtStock.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtStock.getValue().toString().trim().replaceAll(",", ""));	
		double DPS=Double.parseDouble(txtDPS.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtDPS.getValue().toString().trim().replaceAll(",", ""));	
		double Saving=Double.parseDouble(txtSaving.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtSaving.getValue().toString().trim().replaceAll(",", ""));	
		double Desktop=Double.parseDouble(txtDesktop.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtDesktop.getValue().toString().trim().replaceAll(",", ""));			
		TotalActualInvestotal =(Contr+Life+Stock+DPS+Saving+Desktop);				
		txtTotalActualInvesment.setValue(new CommaSeparator().setComma(TotalActualInvestotal));
		
		TotalRebate = Double.parseDouble(df.format(TotalActualInvestotal*15/100));			
		txtTaxRebate.setValue(new CommaSeparator().setComma(Math.round(TotalRebate)));			
     }

     public void TotanEarningsCalculation()
     {		
		double ToTAlErn=0.00	;
		double Basic=Double.parseDouble(txtBasic.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtBasic.getValue().toString().trim().replaceAll(",", ""));
		double Fesitival=Double.parseDouble(txtFestivalBonus.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtFestivalBonus.getValue().toString().trim().replaceAll(",", ""));	
		double Insentive=0; //Double.parseDouble(txtInsentive.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtInsentive.getValue().toString().trim().replaceAll(",", ""));	
		double Educa=0; //Double.parseDouble(txtEducationAllowance.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtEducationAllowance.getValue().toString().trim().replaceAll(",", ""));	
		double LeaveEncash=Double.parseDouble(txtLeaveEncashment.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtLeaveEncashment.getValue().toString().trim().replaceAll(",", ""));			
		double pfOTwo=Double.parseDouble(txtPFComContribution.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtPFComContribution.getValue().toString().trim().replaceAll(",", ""));	
		double Other=Double.parseDouble(txtOther.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtOther.getValue().toString().trim().replaceAll(",", ""));	
		double BFF=0; //Double.parseDouble(txtBppf.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtBppf.getValue().toString().trim().replaceAll(",", ""));	
		
		ToTAlErn =(Basic+Fesitival+Insentive+Educa+LeaveEncash+pfOTwo+Other+BFF);	
		txtTotalEarning.setValue(new CommaSeparator().setComma(ToTAlErn));	
     }
		
	public void cmbUnitDataLoad()
	{	
		String sql="select distinct vUnitId,vUnitName from tbEmpOfficialPersonalInfo order by vUnitName";
		try{
			Session session=SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			Iterator<?> iter=session.createSQLQuery(sql).list().iterator();

			cmbUnit.removeAllItems();
			while(iter.hasNext())
			{
				Object[] element=(Object[])iter.next();
				cmbUnit.addItem(element[0]);
				cmbUnit.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbUnitDataLoad: "+exp);
		}
	}

	private String TexYear()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();

		String sql1="select cast(cast(Substring('"+txtFiscalYear.getValue().toString()+"',1,4) as int)+1 as varchar(120))+'-'+cast(cast(Substring('"+txtFiscalYear.getValue().toString()+"',1,4) as int)+2 as varchar(120))";
	
		String Tax="0";

		Iterator iter=session.createSQLQuery(sql1).list().iterator();
		if(iter.hasNext()){
			return iter.next().toString();
		}	
		
		System.out.println("Tax"+Tax);
		
		return Tax;
	}
		
	private void cmbAddEmployeeDataLoad()
	{
		String unit="";
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		
		unit=cmbUnit.getValue().toString();
		
		try
		{
			String query = "select distinct vEmployeeId,vEmployeeCode from tbEmpOfficialPersonalInfo where vUnitId like '"+unit+"' order by vEmployeeCode ";
			
			System.out.println("cmbAddEmployeeData:"+query);
			
			List <?> list = session.createSQLQuery(query).list();

			if(!list.isEmpty())
			{
				for(Iterator <?> itr=list.iterator();itr.hasNext();)
				{
					Object [] element=(Object[])itr.next();
					cmbEmployeeId.addItem(element[0]);
					cmbEmployeeId.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch(Exception ex)
		{
			showNotification("cmbAddEmployeeDataLoad", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	 }

		private void findButtonEvent() 
			{
				Window win = new IncomeTaxFind(sessionBean, txtEmployeeIDFind,txtFiscalYearFind);
				win.addListener(new Window.CloseListener() 
				{
					public void windowClose(CloseEvent e) 
					{
						if (txtEmployeeIDFind.getValue().toString().length() > 0)
						{
							txtClear();
							findInitialise(txtEmployeeIDFind.getValue().toString(),txtFiscalYearFind.getValue().toString());
						}
					}
				});

				this.getParent().addWindow(win);
			}
	 	
		private void findInitialise(String EmpID,String FiscalYearID) 
		{
			
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();

			try 
			{
				String sql = "select dDate,dFiscalYear,vServiceStatus,vStaffType,vEmployeeId,"						
						+ "LifeInsurence,StockAndShare,DPSaps,SavingCertificate,"
						+ "DesktopPurchase,mTotal,mFirst,mFirstSub1,mFirstSub2,mNext,mNextSub1,mNextSub2,mNextSecond,"
						+ "mNextSecondSub1,mNextSecondSub2,mNextThird,mNextThirdSub1,mNextThirdSub2,mTotalTax,mTaxRebate,"
						+ "mAdvanceTax,mNetTaxPayable,mMinimumTax from tbIncomeTaxInfo where "
						+ "vEmployeeId like '"+EmpID+"'"
						+ " and dFiscalYear like '"+FiscalYearID+"'";

				List <?> list = session.createSQLQuery(sql).list();
				System.out.println("StaffType2"+sql);

				if(list.iterator().hasNext())
				{
					Object[] element = (Object[]) list.iterator().next();

					dApplicationDate.setValue(element[0]);
					txtFiscalYear.setValue(element[1]);

					cmbUnit.setValue(element[2]);
					cmbEmployeeId.setValue(element[4]);

					txtLife.setValue(new CommaSeparator().setComma(Double.parseDouble(element[5].toString())));
					txtStock.setValue(new CommaSeparator().setComma(Double.parseDouble(element[6].toString())));
					txtDPS.setValue(new CommaSeparator().setComma(Double.parseDouble(element[7].toString())));
					txtSaving.setValue(new CommaSeparator().setComma(Double.parseDouble(element[8].toString())));
					txtDesktop.setValue(new CommaSeparator().setComma(Double.parseDouble(element[9].toString())));
	
					txtTotalActualInvesment.setValue(new CommaSeparator().setComma(Double.parseDouble(element[10].toString())));
					txtFirst.setValue(new CommaSeparator().setComma(Double.parseDouble(element[11].toString())));
					txtFirstTax.setValue(new CommaSeparator().setComma(Double.parseDouble(element[12].toString())));					
					txtFirstTax.setValue(new CommaSeparator().setComma(Double.parseDouble(element[13].toString())));
				
					txtNextOne.setValue(new CommaSeparator().setComma(Double.parseDouble(element[14].toString())));
					txtTaxOne.setValue(new CommaSeparator().setComma(Double.parseDouble(element[15].toString())));
					txtNextOneTk.setValue(new CommaSeparator().setComma(Double.parseDouble(element[16].toString())));

					txtNextTwo.setValue(new CommaSeparator().setComma(Double.parseDouble(element[17].toString())));
					txtTaxTwo.setValue(new CommaSeparator().setComma(Double.parseDouble(element[18].toString())));
					txtNextTwoTk.setValue(new CommaSeparator().setComma(Double.parseDouble(element[19].toString())));
							
					txtNextThree.setValue(new CommaSeparator().setComma(Double.parseDouble(element[20].toString())));
					txtTaxThree.setValue(new CommaSeparator().setComma(Double.parseDouble(element[21].toString())));
					txtNextThreeTk.setValue(new CommaSeparator().setComma(Double.parseDouble(element[22].toString())));
					txtTotalTax.setValue(new CommaSeparator().setComma(Double.parseDouble(element[23].toString())));					
					
					txtTaxRebate.setValue(new CommaSeparator().setComma(Double.parseDouble(element[24].toString())));
					txtAdvanceIncomeTax.setValue(new CommaSeparator().setComma(Double.parseDouble(element[25].toString())));
					txtNetTaxPay.setValue(new CommaSeparator().setComma(Double.parseDouble(element[26].toString())));
					txtMinimum.setValue(new CommaSeparator().setComma(Double.parseDouble(element[27].toString())));

					
				}
			}
			catch (Exception exp)
			{
				showNotification("findInitialise", exp + "",Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}


		}
	 

	private void employeeDataSet(String employeeId)
	{
		String fiscalYearFrom,fiscalYearTo;

		fiscalYearFrom=txtFiscalYear.getValue().toString().substring(0, txtFiscalYear.getValue().toString().indexOf("-"))+"-07-01";
		fiscalYearTo=txtFiscalYear.getValue().toString().substring(txtFiscalYear.getValue().toString().indexOf("-")+1, txtFiscalYear.getValue().toString().length())+"-06-30";

		/*System.out.println("fiscalYearFrom: "+fiscalYearFrom);
		System.out.println("fiscalYearTo: "+fiscalYearTo);*/
		
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select vEmployeeId,employeeCode,vEmployeeName,vDepartmentId,vDepartmentName,vDesignationId,vDesignation,dJoiningDate,0 vTin," +
					"mBasic,mFestivalBonus,mInsentive,mEducationAllowance,mLeaveEncashment,mComContributes,mOtherAllowance,Total,mHouseRent,mMedicalAllowance," +
					"mOwnComContributes,mInocomeTAx,vSectionId,vSectionName " +
					"from funIncomeTax('"+fiscalYearFrom+"','"+fiscalYearTo+"','"+cmbEmployeeId.getValue().toString()+"')";
			
			
			System.out.println("employeeDataSet: " + query);
			List <?> list = session.createSQLQuery(query).list();


			if(list.iterator().hasNext())
			{
				Object[] element = (Object[]) list.iterator().next();
				
				txtEmployeeName.setValue(element[2]);	
				txtDepartment.setValue(element[4]);			
				txtDesignation.setValue(element[6]);
				dJoiningDate.setValue(element[7]);
				//txtEtin.setValue(element[8]);
				
                txtBasic.setValue(new CommaSeparator().setComma(Double.parseDouble(element[9].toString().trim().replaceAll(",", ""))));
				txtFestivalBonus.setValue(new CommaSeparator().setComma(Double.parseDouble(element[10].toString().trim().replaceAll(",", ""))));
				//txtInsentive.setValue(new CommaSeparator().setComma(Double.parseDouble(element[11].toString().trim().replaceAll(",", ""))));
				//txtEducationAllowance.setValue(new CommaSeparator().setComma(Double.parseDouble(element[12].toString().trim().replaceAll(",", ""))));
				txtLeaveEncashment.setValue(new CommaSeparator().setComma(Double.parseDouble(element[13].toString().trim().replaceAll(",", ""))));

				txtPFComContribution.setValue(new CommaSeparator().setComma(Double.parseDouble(element[14].toString().trim().replaceAll(",", ""))));
				txtOther.setValue(new CommaSeparator().setComma(Double.parseDouble(element[15].toString().trim().replaceAll(",", ""))));
				txtTotalEarning.setValue(new CommaSeparator().setComma(Double.parseDouble(element[16].toString().trim().replaceAll(",", ""))));
				txtHR.setValue(new CommaSeparator().setComma(Double.parseDouble(element[17].toString().trim().replaceAll(",", ""))));
				txtMedical.setValue(new CommaSeparator().setComma(Double.parseDouble(element[18].toString().trim().replaceAll(",", ""))));
				txtOwnAndComContribution.setValue(new CommaSeparator().setComma(Double.parseDouble(element[19].toString().trim().replaceAll(",", ""))));
				txtAdvanceIncomeTax.setValue(new CommaSeparator().setComma(Double.parseDouble(element[20].toString().trim().replaceAll(",", ""))));
				txtSection.setValue(element[21]);
			}
		
		}
		catch(Exception ex)
		{
			showNotification("employeeDataSet", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void formValidation()
	{
		if(!txtFiscalYear.getValue().toString().isEmpty())
		{
			if(!txtTaxYear.getValue().toString().equals(""))
			{
				if(cmbUnit.getValue()!=null)
				{
					if(cmbEmployeeId.getValue()!=null)
					{
						if(!txtBasic.getValue().toString().equals(""))
						{
							if(!txtFestivalBonus.getValue().toString().isEmpty())
							{

								if(!txtPFComContribution.getValue().toString().equals(""))
								{								
									if(!txtOther.getValue().toString().isEmpty())
									{
										if(!txtTotalEarning.getValue().toString().equals(""))
										{
											if(!txtHR.getValue().toString().equals(""))
											{
												if(!txtMedical.getValue().toString().equals(""))
												{
												SaveButtonEvent();
												
												}
										     
												else{

													showNotification("Warning!","Provide Medical", Notification.TYPE_WARNING_MESSAGE);
													txtMedical.focus();

												}
												}

											else{

												showNotification("Warning!","Provide HR", Notification.TYPE_WARNING_MESSAGE);
												txtHR.focus();

											}
											}

										else
										{
											showNotification("Warning!","Provide Total Earning", Notification.TYPE_WARNING_MESSAGE);

										}
									   }																							


									else
									{
										showNotification("Warning!","Provide Other Alloawance ", Notification.TYPE_WARNING_MESSAGE);
										txtOther.focus();

									}
								}


								else
								{
									showNotification("Warning!","Provide Contribution ", Notification.TYPE_WARNING_MESSAGE);
									txtPFComContribution.focus();
								}
							}

							else
							{
								showNotification("Warning!","Provide Festival Bonus ", Notification.TYPE_WARNING_MESSAGE);
								txtFestivalBonus.focus();
							}
						}


						else
						{
							showNotification("Warning!","Provide  Basic", Notification.TYPE_WARNING_MESSAGE);
							txtBasic.focus();
						}
					}

					else
					{
						showNotification("Warning!","Provide Employee Id ", Notification.TYPE_WARNING_MESSAGE);
						cmbEmployeeId.focus();
					}
				}
				else
				{
					showNotification("Warning!","Provide EmployeeType", Notification.TYPE_WARNING_MESSAGE);
					cmbUnit.focus();
				}
			}

			else
			{
				showNotification("Warning!","Provide tax Year ", Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else
		{
			showNotification("Warning!","Provide Fiscal Year ", Notification.TYPE_WARNING_MESSAGE);
			txtFiscalYear.focus();

		}
	}						

	private void SaveButtonEvent()
	{
		if(isUpdate)
		{
			MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to update information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{																
						updateData();					
						//reportShow();
						//txtClear();						
						componentIni(false);
						btnIni(true);
						isUpdate = false;
						showNotification("All information are updated successfully",Notification.TYPE_HUMANIZED_MESSAGE);

					}
				}
			});
		}
		
		else
		{
			MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to save information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						insertData();
					    //txtClear();
						btnIni(true);
						componentIni(false);							
						isUpdate = false;
						//showNotification("All information are saved successfully",Notification.TYPE_HUMANIZED_MESSAGE);
					}
				}
			});
		}
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
			

				Window win = new ReportViewer(hm,"report/account/hrmModule/rptIndivitualIncomeTax.jasper",
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
	private void updateData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		String empCode = cmbEmployeeId.getItemCaption(cmbEmployeeId.getValue());
		String empName = txtEmployeeName.getValue().toString().trim();

		String empType="";
		String empTypeSub="";

		empType=cmbUnit.getValue().toString();
		
		
		double mActualNetTaxPayable=0.0,mBPPFLess=0.0;
		double NetTaxPayable=Double.parseDouble(txtNetTaxPay.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtNetTaxPay.getValue().toString().trim().replaceAll(",", ""));	
		double MinimumTax=Double.parseDouble(txtMinimum.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtMinimum.getValue().toString().trim().replaceAll(",", ""));	
		double BPPF=0; //Double.parseDouble(txtBppf.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtBppf.getValue().toString().trim().replaceAll(",", ""));	
		
		mActualNetTaxPayable=NetTaxPayable;
		if(NetTaxPayable<MinimumTax)
		{
			mActualNetTaxPayable=MinimumTax;
		}
		mBPPFLess=BPPF;
		if(BPPF>=50000)
		{
			mBPPFLess=BPPF-50000;;
		}
		
		try
		{
			String sql="delete from tbIncomeTaxInfo where vEmployeeId='"+cmbEmployeeId.getValue()+"' " +
					"and dFiscalYear='"+txtFiscalYear.getValue()+"' and dTaxYear='"+txtTaxYear.getValue()+"' ";
			session.createSQLQuery(sql).executeUpdate();

			String insertQuery="Insert into tbIncomeTaxInfo (dDate,dFiscalYear,dTaxYear,vServiceStatus,vStaffType,vEmployeeId,employeeCode,vEmployeeName,vDepartmentID," +
					"vDepartmentName,vDesignationId,vDesignationName,dJoiningDate,ETinNo," +
					"mBasic,mFestivalBonus,mInsentiveBonus,EducationAllowance,mleaveEncashment,mContributes,mOther,mBPPF,mTotalEarning,mHouseRent,mMedicalAllowance," +
					"mCoContributes,LifeInsurence,StockAndShare,DPSaps,SavingCertificate,DesktopPurchase,mTotal,mFirst,mFirstSub1,mFirstSub2,mNext,mNextSub1,mNextSub2," +
					"mNextSecond,mNextSecondSub1,mNextSecondSub2,mNextThird,mNextThirdSub1,mNextThirdSub2,mTotalTax,mTaxRebate,mAdvanceTax,mNetTaxPayable,mMinimumTax," +
					"vUserIp,vUserName,dEntryTime,mActualNetTaxPayable,mBPPFLess) values "					
					+ " ('"+dFormatSql.format(dApplicationDate.getValue())+"',"
					+ "'"+txtFiscalYear.getValue().toString()+"',"
					+ "'"+txtTaxYear.getValue().toString()+"',"
					+ "'"+empType+"',"
					+ "'"+empTypeSub+"',"
					+ "'"+cmbEmployeeId.getValue().toString()+"',"
					+ " '"+empCode+"', '"+empName+"',"
					+ "(select vUnitID from tbEmpOfficialPersonalInfo where vEmployeeId='"+cmbEmployeeId.getValue().toString().trim()+"'),"
					+ " '"+txtDepartment.getValue().toString()+"', "
					+ " (select vDesignationId from tbEmpDesignationInfo where vEmployeeId='"+cmbEmployeeId.getValue().toString().trim()+"'),"
					+ "'"+txtDesignation.getValue().toString()+"',"
					+ " '"+dFormatSql.format(dJoiningDate.getValue())+"',"
					+ "'"+0+"',"
					+ "'"+(txtBasic.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtBasic.getValue().toString().trim().replaceAll(",", ""))+"',"
					+ "'"+(txtFestivalBonus.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtFestivalBonus.getValue().toString().trim().replaceAll(",", ""))+"',"
					+ "'"+0+"',"
					+ "'"+0+"',"
					+ "'"+(txtLeaveEncashment.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtLeaveEncashment.getValue().toString().trim().replaceAll(",", ""))+"',"
					+ "'"+(txtPFComContribution.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtPFComContribution.getValue().toString().trim().replaceAll(",", ""))+"',"
					+ "'"+(txtOther.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtOther.getValue().toString().trim().replaceAll(",", ""))+"',"
					+ "'"+0+"',"
					+ "'"+(txtTotalEarning.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtTotalEarning.getValue().toString().trim().replaceAll(",", ""))+"',"
					+ "'"+(txtHR.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtHR.getValue().toString().trim().replaceAll(",", ""))+"',"
					+ "'"+(txtMedical.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtMedical.getValue().toString().trim().replaceAll(",", ""))+"',"
					+ "'"+(txtOwnAndComContribution.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtOwnAndComContribution.getValue().toString().trim().replaceAll(",", ""))+"',"
					+ "'"+(txtLife.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtLife.getValue().toString().trim().replaceAll(",", ""))+"',"
					+ "'"+(txtStock.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtStock.getValue().toString().trim().replaceAll(",", ""))+"',"
					+ "'"+(txtDPS.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtDPS.getValue().toString().trim().replaceAll(",", ""))+"',"
					+ "'"+(txtSaving.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtSaving.getValue().toString().trim().replaceAll(",", ""))+"',"
					+ "'"+(txtDesktop.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtDesktop.getValue().toString().trim().replaceAll(",", ""))+"',"
					+ "'"+(txtTotalActualInvesment.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtTotalActualInvesment.getValue().toString().trim().replaceAll(",", ""))+"',"
					+ "'"+(txtFirst.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtFirst.getValue().toString().trim().replaceAll(",", ""))+"',"
					+ "'"+(txtFirstTax.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtFirstTax.getValue().toString().trim().replaceAll(",", ""))+"',"
					+ "'"+(txtFirstTk.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtFirstTk.getValue().toString().trim().replaceAll(",", ""))+"',"
					+ "'"+(txtNextOne.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtNextOne.getValue().toString().trim().replaceAll(",", ""))+"',"
					+ "'"+(txtTaxOne.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtTaxOne.getValue().toString().trim().replaceAll(",", ""))+"',"
					+ "'"+(txtNextOneTk.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtNextOneTk.getValue().toString().trim().replaceAll(",", ""))+"',"
					+ "'"+(txtNextTwo.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtNextTwo.getValue().toString().trim().replaceAll(",", ""))+"',"
					+ "'"+(txtTaxTwo.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtTaxTwo.getValue().toString().trim().replaceAll(",", ""))+"',"
					+ "'"+(txtNextTwoTk.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtNextTwoTk.getValue().toString().trim().replaceAll(",", ""))+"',"
					+ "'"+(txtNextThree.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtNextThree.getValue().toString().trim().replaceAll(",", ""))+"',"
					+ "'"+(txtTaxThree.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtTaxThree.getValue().toString().trim().replaceAll(",", ""))+"',"
					+ "'"+(txtNextThreeTk.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtNextThreeTk.getValue().toString().trim().replaceAll(",", ""))+"',"
					+ "'"+(txtTotalTax.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtTotalTax.getValue().toString().trim().replaceAll(",", ""))+"',"
					+ "'"+(txtTaxRebate.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtTaxRebate.getValue().toString().trim().replaceAll(",", ""))+"',"
					+ "'"+(txtAdvanceIncomeTax.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtAdvanceIncomeTax.getValue().toString().trim().replaceAll(",", ""))+"',"
					+ "'"+(txtNetTaxPay.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtNetTaxPay.getValue().toString().trim().replaceAll(",", ""))+"',"
					+ "'"+(txtMinimum.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtMinimum.getValue().toString().trim().replaceAll(",", ""))+"',"
					+ "'"+sessionBean.getUserName().replaceAll(",", "")+"',"
					+ "'"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP,'"+mActualNetTaxPayable+"','"+mBPPFLess+"')";
				session.createSQLQuery(insertQuery).executeUpdate();
				
				

				/*String insertUdQuery="Insert into tbUdIncomeTaxInfo (dDate,dFiscalYear,dTaxYear,vServiceStatus,vStaffType,vEmployeeId,employeeCode,vEmployeeName,vDepartmentID," +
						"vDepartmentName,vDesignationId,vDesignationName,dJoiningDate,ETinNo," +
						"mBasic,mFestivalBonus,mInsentiveBonus,EducationAllowance,mleaveEncashment,mContributes,mOther,mBPPF,mTotalEarning,mHouseRent,mMedicalAllowance," +
						"mCoContributes,LifeInsurence,StockAndShare,DPSaps,SavingCertificate,DesktopPurchase,mTotal,mFirst,mFirstSub1,mFirstSub2,mNext,mNextSub1,mNextSub2," +
						"mNextSecond,mNextSecondSub1,mNextSecondSub2,mNextThird,mNextThirdSub1,mNextThirdSub2,mTotalTax,mTaxRebate,mAdvanceTax,mNetTaxPayable,mMinimumTax," +
						"vUserIp,vUserName,dEntryTime,mActualNetTaxPayable,mBPPFLess) " +
						"values " +
						"select dDate,dFiscalYear,dTaxYear,vServiceStatus,vStaffType,vEmployeeId,employeeCode,vEmployeeName,vDepartmentID," +
						"vDepartmentName,vDesignationId,vDesignationName,dJoiningDate,ETinNo," +
						"mBasic,mFestivalBonus,mInsentiveBonus,EducationAllowance,mleaveEncashment,mContributes,mOther,mBPPF,mTotalEarning,mHouseRent,mMedicalAllowance," +
						"mCoContributes,LifeInsurence,StockAndShare,DPSaps,SavingCertificate,DesktopPurchase,mTotal,mFirst,mFirstSub1,mFirstSub2,mNext,mNextSub1,mNextSub2," +
						"mNextSecond,mNextSecondSub1,mNextSecondSub2,mNextThird,mNextThirdSub1,mNextThirdSub2,mTotalTax,mTaxRebate,mAdvanceTax,mNetTaxPayable,mMinimumTax," +
						"vUserIp,vUserName,dEntryTime,mActualNetTaxPayable,mBPPFLess from tbUdIncomeTaxInfo where vEmployeeId='"+cmbEmployeeId.getValue()+"' " +
					"and dFiscalYear='"+txtFiscalYear.getValue()+"' and dTaxYear='"+txtTaxYear.getValue()+"'";
					session.createSQLQuery(insertQuery).executeUpdate();*/
					
					
				tx.commit();
				Notification n=new Notification("All Information Updated Successfully!","",Notification.TYPE_TRAY_NOTIFICATION);
				n.setPosition(Notification.POSITION_TOP_RIGHT);
				showNotification(n);
				
		}
		catch(Exception ex)
		{
			showNotification("insertData", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
		finally{session.close();}
	}
	
	private void insertData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		String empCode = cmbEmployeeId.getItemCaption(cmbEmployeeId.getValue());
		String empName = txtEmployeeName.getValue().toString().trim();

		String empType="";
		String empTypeSub="";

		empType=cmbUnit.getValue().toString();
		
		double mActualNetTaxPayable=0.0,mBPPFLess=0.0;
		double NetTaxPayable=Double.parseDouble(txtNetTaxPay.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtNetTaxPay.getValue().toString().trim().replaceAll(",", ""));	
		double MinimumTax=Double.parseDouble(txtMinimum.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtMinimum.getValue().toString().trim().replaceAll(",", ""));	
		//double BPPF=Double.parseDouble(txtBppf.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtBppf.getValue().toString().trim().replaceAll(",", ""));	
		
		mActualNetTaxPayable=NetTaxPayable;
		if(NetTaxPayable<MinimumTax)
		{
			mActualNetTaxPayable=MinimumTax;
		}
		/*mBPPFLess=BPPF;
		if(BPPF>=50000)
		{
			mBPPFLess=BPPF-50000;;
		}*/
		
		try
		{
			String sql="select * from tbIncomeTaxInfo where vEmployeeId='"+cmbEmployeeId.getValue()+"' " +
					"and dFiscalYear='"+txtFiscalYear.getValue()+"' and dTaxYear='"+txtTaxYear.getValue()+"' ";
			if(!chkData(sql))
			{
				String insertQuery="Insert into tbIncomeTaxInfo (dDate,dFiscalYear,dTaxYear,vServiceStatus,vStaffType,vEmployeeId,employeeCode,vEmployeeName,vDepartmentID," +
						"vDepartmentName,vDesignationId,vDesignationName,dJoiningDate,ETinNo," +
						"mBasic,mFestivalBonus,mInsentiveBonus,EducationAllowance,mleaveEncashment,mContributes,mOther,mBPPF,mTotalEarning,mHouseRent,mMedicalAllowance," +
						"mCoContributes,LifeInsurence,StockAndShare,DPSaps,SavingCertificate,DesktopPurchase,mTotal,mFirst,mFirstSub1,mFirstSub2,mNext,mNextSub1,mNextSub2," +
						"mNextSecond,mNextSecondSub1,mNextSecondSub2,mNextThird,mNextThirdSub1,mNextThirdSub2,mTotalTax,mTaxRebate,mAdvanceTax,mNetTaxPayable,mMinimumTax," +
						"vUserIp,vUserName,dEntryTime,mActualNetTaxPayable,mBPPFLess) values "					
						+ " ('"+dFormatSql.format(dApplicationDate.getValue())+"',"
						+ "'"+txtFiscalYear.getValue().toString()+"',"
						+ "'"+txtTaxYear.getValue().toString()+"',"
						+ "'"+empType+"',"
						+ "'"+empTypeSub+"',"
						+ "'"+cmbEmployeeId.getValue().toString()+"',"
						+ " '"+empCode+"', '"+empName+"',"
						+ "(select vUnitID from tbEmpOfficialPersonalInfo where vEmployeeId='"+cmbEmployeeId.getValue().toString().trim()+"'),"
						+ " '"+txtDepartment.getValue().toString()+"', "
						+ " (select vDesignationId from tbEmpDesignationInfo where vEmployeeId='"+cmbEmployeeId.getValue().toString().trim()+"'),"
						+ "'"+txtDesignation.getValue().toString()+"',"
						+ " '"+dFormatSql.format(dJoiningDate.getValue())+"',"
						+ "'"+0+"',"
						+ "'"+(txtBasic.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtBasic.getValue().toString().trim().replaceAll(",", ""))+"',"
						+ "'"+(txtFestivalBonus.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtFestivalBonus.getValue().toString().trim().replaceAll(",", ""))+"',"
						+ "'"+0+"',"
						+ "'"+0+"',"
						+ "'"+(txtLeaveEncashment.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtLeaveEncashment.getValue().toString().trim().replaceAll(",", ""))+"',"
						+ "'"+(txtPFComContribution.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtPFComContribution.getValue().toString().trim().replaceAll(",", ""))+"',"
						+ "'"+(txtOther.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtOther.getValue().toString().trim().replaceAll(",", ""))+"',"
						+ "'"+0+"',"
						+ "'"+(txtTotalEarning.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtTotalEarning.getValue().toString().trim().replaceAll(",", ""))+"',"
						+ "'"+(txtHR.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtHR.getValue().toString().trim().replaceAll(",", ""))+"',"
						+ "'"+(txtMedical.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtMedical.getValue().toString().trim().replaceAll(",", ""))+"',"
						+ "'"+(txtOwnAndComContribution.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtOwnAndComContribution.getValue().toString().trim().replaceAll(",", ""))+"',"
						+ "'"+(txtLife.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtLife.getValue().toString().trim().replaceAll(",", ""))+"',"
						+ "'"+(txtStock.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtStock.getValue().toString().trim().replaceAll(",", ""))+"',"
						+ "'"+(txtDPS.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtDPS.getValue().toString().trim().replaceAll(",", ""))+"',"
						+ "'"+(txtSaving.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtSaving.getValue().toString().trim().replaceAll(",", ""))+"',"
						+ "'"+(txtDesktop.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtDesktop.getValue().toString().trim().replaceAll(",", ""))+"',"
						+ "'"+(txtTotalActualInvesment.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtTotalActualInvesment.getValue().toString().trim().replaceAll(",", ""))+"',"
						+ "'"+(txtFirst.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtFirst.getValue().toString().trim().replaceAll(",", ""))+"',"
						+ "'"+(txtFirstTax.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtFirstTax.getValue().toString().trim().replaceAll(",", ""))+"',"
						+ "'"+(txtFirstTk.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtFirstTk.getValue().toString().trim().replaceAll(",", ""))+"',"
						+ "'"+(txtNextOne.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtNextOne.getValue().toString().trim().replaceAll(",", ""))+"',"
						+ "'"+(txtTaxOne.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtTaxOne.getValue().toString().trim().replaceAll(",", ""))+"',"
						+ "'"+(txtNextOneTk.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtNextOneTk.getValue().toString().trim().replaceAll(",", ""))+"',"
						+ "'"+(txtNextTwo.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtNextTwo.getValue().toString().trim().replaceAll(",", ""))+"',"
						+ "'"+(txtTaxTwo.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtTaxTwo.getValue().toString().trim().replaceAll(",", ""))+"',"
						+ "'"+(txtNextTwoTk.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtNextTwoTk.getValue().toString().trim().replaceAll(",", ""))+"',"
						+ "'"+(txtNextThree.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtNextThree.getValue().toString().trim().replaceAll(",", ""))+"',"
						+ "'"+(txtTaxThree.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtTaxThree.getValue().toString().trim().replaceAll(",", ""))+"',"
						+ "'"+(txtNextThreeTk.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtNextThreeTk.getValue().toString().trim().replaceAll(",", ""))+"',"
						+ "'"+(txtTotalTax.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtTotalTax.getValue().toString().trim().replaceAll(",", ""))+"',"
						+ "'"+(txtTaxRebate.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtTaxRebate.getValue().toString().trim().replaceAll(",", ""))+"',"
						+ "'"+(txtAdvanceIncomeTax.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtAdvanceIncomeTax.getValue().toString().trim().replaceAll(",", ""))+"',"
						+ "'"+(txtNetTaxPay.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtNetTaxPay.getValue().toString().trim().replaceAll(",", ""))+"',"
						+ "'"+(txtMinimum.getValue().toString().trim().replaceAll(",", "").isEmpty()?"0.00":txtMinimum.getValue().toString().trim().replaceAll(",", ""))+"',"
						+ "'"+sessionBean.getUserName().replaceAll(",", "")+"',"
						+ "'"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP,'"+mActualNetTaxPayable+"','"+mBPPFLess+"')";
					
				System.out.println("insertQuery: "+insertQuery);
					
				session.createSQLQuery(insertQuery).executeUpdate();
				session.clear();
				tx.commit();
				Notification n=new Notification("All Information Save Successfully!","",Notification.TYPE_TRAY_NOTIFICATION);
				n.setPosition(Notification.POSITION_TOP_RIGHT);
				showNotification(n);
			}
			else
			{
				showNotification("Warning!","Data already Inserted for this Employee", Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception ex)
		{
			showNotification("insertData", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
		finally{session.close();}
	}
	
	private boolean chkData(String query)
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> empList = session.createSQLQuery(query).list();
			if(!empList.isEmpty())
				return true;
		}
		catch (Exception exp)
		{
			showNotification("chkData", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
		return false;
	}
	private void tableDataLoad()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{	
			String sql ="select distinct mCheckNo,mCheckDate,mChallanNo,mChallanDate,mAmount from tbcheckDetails chk " +
		     		/*"inner join tbIncomeTaxInfo inc on chk.vFiscalYearId=inc.dFiscalYear " + update by didar 24-10-2018*/
		     		"where chk.vFiscalYearId like '"+txtFiscalYear.getValue().toString()+"'";
			System.out.println("tableDataLoad: "+sql);
			
			List <?> list = session.createSQLQuery(sql).list();
			int i = 0;
			if(!list.isEmpty())
			{
				for (Iterator <?> iter = list.iterator(); iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();
					lbltbChequeDD.get(i).setValue(element[0]);
					lbltbcheckDate.get(i).setValue(dFormatBangla.format(element[1]));
					lbltbChallanNo.get(i).setValue(element[2]);
					lbltbChallanDate.get(i).setValue(dFormatBangla.format(element[3]));
					txtAmount.get(i).setValue(df.format(element[4]));	

					if(lbltbChequeDD.size()-1==i)
					{
						tableRowAdd(i+1);
					}
					i++;
				}
			}
			else
			{
				showNotification("Warning","There are no Data in cheque /challan Table",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception ex)
		{
			System.out.print(ex);
		}
		finally
		{
			session.close();
		}
	}	
	
	private void tableclear()
	{
		for(int i=0; i<lbltbChequeDD.size(); i++)
		{
			lbltbChequeDD.get(i).setValue("");
			lbltbcheckDate.get(i).setValue("");
			lbltbChallanNo.get(i).setValue("");
			lbltbChallanDate.get(i).setValue("");
			txtAmount.get(i).setValue("");
		}
	}
	private void btnIni(boolean t)
	{
		cButton.btnNew.setEnabled(t);
		cButton.btnEdit.setEnabled(t);
		cButton.btnSave.setEnabled(!t);
		cButton.btnRefresh.setEnabled(!t);
		cButton.btnDelete.setEnabled(t);
		cButton.btnFind.setEnabled(t);
	}

	private void tableinitialize()
	{
		for(int i=0;i<5;i++)
			tableRowAdd(i);
	}

	private void tableRowAdd(final int ar)
	{
		lblSl.add(new Label());
		lblSl.get(ar).setWidth("100%");
		lblSl.get(ar).setHeight("20px");
		lblSl.get(ar).setImmediate(true);
		lblSl.get(ar).setValue(ar+1);

		lbltbChequeDD.add(new Label());
		lbltbChequeDD.get(ar).setWidth("100%");
		lbltbChequeDD.get(ar).setImmediate(true);

		lbltbcheckDate.add(new Label());
		lbltbcheckDate.get(ar).setWidth("100%");
		lbltbcheckDate.get(ar).setImmediate(true);

		lbltbChallanNo.add(ar, new Label());
		lbltbChallanNo.get(ar).setWidth("100%");
		lbltbChallanNo.get(ar).setImmediate(true);

		lbltbChallanDate.add(ar, new Label());
		lbltbChallanDate.get(ar).setWidth("100%");
		lbltbChallanDate.get(ar).setImmediate(true);

		txtAmount.add(ar, new AmountField());
		txtAmount.get(ar).setWidth("100%");
		txtAmount.get(ar).setImmediate(true);

		table.addItem(new Object[] {lblSl.get(ar),lbltbChequeDD.get(ar),lbltbcheckDate.get(ar),lbltbChallanNo.get(ar),
				lbltbChallanDate.get(ar),txtAmount.get(ar)}, ar);
	}

	private void componentIni(boolean t)
	{
		table.setEnabled(t);

		if(isUpdate==true)
		{dApplicationDate.setEnabled(!t);}
		else
		{dApplicationDate.setEnabled(t);}
		txtTaxYear.setEnabled(t);
		txtFiscalYear.setEnabled(t);
		cmbUnit.setEnabled(t);
		//ogSelectEmployee.setEnabled(t);			
		cmbEmployeeId.setEnabled(t);
		txtEmployeeName.setEnabled(t);
		txtDepartment.setEnabled(t);
		txtSection.setEnabled(t);
		txtDesignation.setEnabled(t);
		dJoiningDate.setEnabled(t);
		//txtEtin.setEnabled(t);
		txtBasic.setEnabled(t);
		txtFestivalBonus.setEnabled(t);
		//txtEducationAllowance.setEnabled(t);
		txtLeaveEncashment.setEnabled(t);	
		txtPFComContribution.setEnabled(t);
		txtOther.setEnabled(t);   	
		//txtBppf.setEnabled(t);
		txtTotalEarning.setEnabled(t);
		txtHR.setEnabled(t);
		txtMedical.setEnabled(t);
		txtOwnAndComContribution.setEnabled(t);
		txtLife.setEnabled(t);
		txtStock.setEnabled(t);
		txtDPS.setEnabled(t);
		txtSaving.setEnabled(t);
		txtDesktop.setEnabled(t);
		txtTotalActualInvesment.setEnabled(t);
		txtFirst.setEnabled(t);
		txtFirstTax.setEnabled(t);
		txtFirstTk.setEnabled(t);	
		txtNextOne.setEnabled(t);
		txtTaxOne.setEnabled(t);
		txtNextOneTk.setEnabled(t);
		txtNextTwo.setEnabled(t);
		txtTaxTwo.setEnabled(t);
		txtNextTwoTk.setEnabled(t);	
		txtNextThree.setEnabled(t);
		txtTaxThree.setEnabled(t);
		txtNextThreeTk.setEnabled(t);	
		txtTotalTax.setEnabled(t);
		txtTaxRebate.setEnabled(t);
		txtAdvanceIncomeTax.setEnabled(t);
		txtNetTaxPay.setEnabled(t);
		txtMinimum.setEnabled(t);
	}

	private void txtClear()
	{
		dApplicationDate.setValue(new java.util.Date());
		txtTaxYear.setValue("");
		txtFiscalYear.setValue("");			
		cmbUnit.setValue(null);
		cmbEmployeeId.setValue(null);
		txtEmployeeName.setValue("");
		txtDepartment.setValue("");
		txtSection.setValue("");
		txtDesignation.setValue("");
		dJoiningDate.setValue(new java.util.Date());
		//txtEtin.setValue("");
		txtBasic.setValue("");
		txtFestivalBonus.setValue("");
		//txtEducationAllowance.setValue("");
		txtLeaveEncashment.setValue("");
		txtPFComContribution.setValue("");
		txtOther.setValue(""); 	
		//txtBppf.setValue("");
		txtTotalEarning.setValue("");
		txtHR.setValue("");
		txtMedical.setValue("");
		txtOwnAndComContribution.setValue("");
		txtLife.setValue("");
		txtStock.setValue("");
		txtDPS.setValue("");
		txtSaving.setValue("");
		txtDesktop.setValue("");
		txtTotalActualInvesment.setValue("");
		txtFirst.setValue("250000");
		txtFirstTax.setValue("0");
		txtFirstTk.setValue("");	
		txtNextOne.setValue("400000");
		txtTaxOne.setValue("10");
		txtNextOneTk.setValue("");
		txtNextTwo.setValue("500000");
		txtTaxTwo.setValue("15");
		txtNextTwoTk.setValue("");	
		txtNextThree.setValue("600000");
		txtTaxThree.setValue("20");
		txtNextThreeTk.setValue("");	
		txtTotalTax.setValue("");
		txtTaxRebate.setValue("");
		txtAdvanceIncomeTax.setValue("");
		txtNetTaxPay.setValue("");;
		txtMinimum.setValue("");
	}

	private void focusEnter()
	{
		allComp.add(dApplicationDate);
		allComp.add(txtTaxYear);
		allComp.add(txtFiscalYear);
		allComp.add(cmbUnit);
		allComp.add(cmbEmployeeId);
		allComp.add(txtEmployeeName);
		allComp.add(txtDepartment);
		allComp.add(txtDesignation);
		allComp.add(dJoiningDate);
		//allComp.add(txtEtin);
		allComp.add(txtBasic);
		allComp.add(txtFestivalBonus);
		//allComp.add(txtEducationAllowance);
		allComp.add(txtLeaveEncashment);
		allComp.add(txtPFComContribution);
		allComp.add(txtOther); 	
		//allComp.add(txtBppf);
		allComp.add(txtTotalEarning);
		allComp.add(txtHR);
		allComp.add(txtMedical);
		allComp.add(txtOwnAndComContribution);
		allComp.add(txtLife);
		allComp.add(txtStock);
		allComp.add(txtDPS);
		allComp.add(txtSaving);
		allComp.add(txtDesktop);
		allComp.add(txtTotalActualInvesment);
		allComp.add(txtFirst);
		allComp.add(txtFirstTax);
		allComp.add(txtFirstTk);	
		allComp.add(txtNextOne);
		allComp.add(txtTaxOne);
		allComp.add(txtNextOneTk);
		allComp.add(txtNextTwo);
		allComp.add(txtTaxTwo);
		allComp.add(txtNextTwoTk);	
		allComp.add(txtNextThree);
		allComp.add(txtTaxThree);
		allComp.add(txtNextThreeTk);	
		allComp.add(txtTotalTax);
		allComp.add(txtTaxRebate);
		allComp.add(txtAdvanceIncomeTax);
		allComp.add(txtNetTaxPay);
		allComp.add(txtMinimum);

		new FocusMoveByEnter(this,allComp);
	}

	public AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("870px");
		mainLayout.setHeight("600px");

		dApplicationDate = new PopupDateField();
		dApplicationDate.setImmediate(false);
		dApplicationDate.setWidth("110px");
		dApplicationDate.setHeight("-1px");
		dApplicationDate.setValue(new java.util.Date());
		dApplicationDate.setDateFormat("dd-MM-yyyy");
		dApplicationDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(new Label(" Date : "), "top:15.0px; left:45.0px;");
		mainLayout.addComponent(dApplicationDate, "top:13.0px; left:160.0px;");

		txtFiscalYear = new TextField();
		txtFiscalYear.setImmediate(true);
		txtFiscalYear.setWidth("110px");
		txtFiscalYear.setHeight("22px");
		mainLayout.addComponent(new Label("Fiscal Year :"), "top:40.0px; left:45.0px;");
		mainLayout.addComponent(txtFiscalYear, "top:38.0px; left:161.0px;");	

		txtTaxYear = new TextRead();
		txtTaxYear.setImmediate(true);
		txtTaxYear.setWidth("110px");
		txtTaxYear.setHeight("-1px");
		mainLayout.addComponent(new Label("Tax Year :"), "top:65.0px; left:45.0px;");
		mainLayout.addComponent(txtTaxYear, "top:63.0px; left:161.0px;");

		cmbUnit = new ComboBox();
		cmbUnit.setImmediate(true);
		cmbUnit.setWidth("230px");
		cmbUnit.setHeight("-1px");
		cmbUnit.setNullSelectionAllowed(true);
		mainLayout.addComponent(new Label("Project : "), "top:105.0px; left:45.0px;");
		mainLayout.addComponent(cmbUnit, "top:103.0px; left:160.0px;");
		
		lblEmployeeId = new Label("Employee ID :");
		lblEmployeeId.setImmediate(false);
		lblEmployeeId.setWidth("-1px");
		lblEmployeeId.setHeight("-1px");
		mainLayout.addComponent(lblEmployeeId, "top:130.0px; left:45.0px;");

		// cmbEmployeeId
		cmbEmployeeId = new ComboBox();
		cmbEmployeeId.setImmediate(true);
		cmbEmployeeId.setWidth("230px");
		cmbEmployeeId.setHeight("-1px");
		cmbEmployeeId.setFilteringMode(cmbEmployeeId.FILTERINGMODE_CONTAINS);
		cmbEmployeeId.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbEmployeeId, "top:128.0px; left:160.0px;");

		// lblEmployeeName
		lblEmployeeName = new Label("Employee Name :");
		lblEmployeeName.setImmediate(false);
		lblEmployeeName.setWidth("-1px");
		lblEmployeeName.setHeight("-1px");
		mainLayout.addComponent(lblEmployeeName, "top:155.0px; left:45.0px;");

		// txtEmployeeName
		txtEmployeeName = new TextRead();
		txtEmployeeName.setImmediate(true);
		txtEmployeeName.setWidth("230px");
		txtEmployeeName.setHeight("22px");
		mainLayout.addComponent(txtEmployeeName, "top:153.0px; left:161.0px;");

		// lblDepartment
		lblDepartment = new Label("Department :");
		lblDepartment.setImmediate(false);
		lblDepartment.setWidth("-1px");
		lblDepartment.setHeight("-1px");
		mainLayout.addComponent(lblDepartment, "top:180.0px; left:45.0px;");

		// txtDepartment
		txtDepartment = new TextRead();
		txtDepartment.setImmediate(true);
		txtDepartment.setWidth("230px");
		txtDepartment.setHeight("22px");
		mainLayout.addComponent(txtDepartment, "top:178.0px; left:161.0px;");
		
		txtSection = new TextRead();
		txtSection.setImmediate(true);
		txtSection.setWidth("230px");
		txtSection.setHeight("22px");
		mainLayout.addComponent(new Label("Section :"), "top:205px; left:45px;");
		mainLayout.addComponent(txtSection, "top:203px; left:161.0px;");

		// lblDesignation
		lblDesignation = new Label("Designation :");
		lblDesignation.setImmediate(false);
		lblDesignation.setWidth("-1px");
		lblDesignation.setHeight("-1px");
		mainLayout.addComponent(lblDesignation, "top:230px; left:45.0px;");

		// txtDesignation
		txtDesignation = new TextRead();
		txtDesignation.setImmediate(true);
		txtDesignation.setWidth("230px");
		txtDesignation.setHeight("22px");
		mainLayout.addComponent(txtDesignation, "top:228px; left:161.0px;");

		// lblJoiningDate
		lblJoiningDate = new Label("Joining Date : ");
		lblJoiningDate.setImmediate(false);
		lblJoiningDate.setWidth("-1px");
		lblJoiningDate.setHeight("-1px");
		mainLayout.addComponent(lblJoiningDate, "top:255px; left:45.0px;");

		// dJoiningDate
		dJoiningDate = new PopupDateField();
		dJoiningDate.setImmediate(true);
		dJoiningDate.setWidth("110px");
		dJoiningDate.setHeight("-1px");
		dJoiningDate.setValue(new java.util.Date());
		dJoiningDate.setDateFormat("dd-MM-yyyy");
		dJoiningDate.setEnabled(false);
		dJoiningDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dJoiningDate, "top:253px; left:160.0px;");

		// lblEtin
		/*lblEtin = new Label("e-TIN :");
		lblEtin.setImmediate(false);
		lblEtin.setWidth("-1px");
		lblEtin.setHeight("-1px");
		mainLayout.addComponent(lblEtin, "top:260.0px; left:45.0px;");

		// txtSanctionAmount
		txtEtin = new TextRead();
		txtEtin.setImmediate(true);
		txtEtin.setWidth("130px");
		txtEtin.setHeight("22px");
		mainLayout.addComponent(txtEtin, "top:258.0px; left:160.0px;");*/

		// lblLoanSanction
		lblEarning= new Label("<html><font color='#74078B'> <strong> 1. Earnings  :</strong></font></html>",Label.CONTENT_XHTML);
		lblEarning.setImmediate(false);
		lblEarning.setWidth("-1px");
		lblEarning.setHeight("-1px");
		mainLayout.addComponent(lblEarning, "top:270.0px; left:85.0px;");

		// lblEtin
		lblBasic = new Label("Basic :");
		lblBasic.setImmediate(false);
		lblBasic.setWidth("-1px");
		lblBasic.setHeight("-1px");
		mainLayout.addComponent(lblBasic, "top:295.0px; left:45.0px;");

		// txtSanctionAmount
		txtBasic = new AmountField();
		txtBasic.setImmediate(true);
		txtBasic.setWidth("110px");
		txtBasic.setHeight("-1px");
		mainLayout.addComponent(txtBasic, "top:293.0px; left:160.0px;");

		// lblEtin		
		lblFestivalBonus = new Label("Festival Bonus :");
		lblFestivalBonus.setImmediate(false);
		lblFestivalBonus.setWidth("-1px");
		lblFestivalBonus.setHeight("-1px");
		mainLayout.addComponent(lblFestivalBonus, "top:320.0px; left:45.0px;");

		// txtSanctionAmount
		txtFestivalBonus = new AmountField();
		txtFestivalBonus.setImmediate(true);
		txtFestivalBonus.setWidth("110px");
		txtFestivalBonus.setHeight("-1px");
		mainLayout.addComponent(txtFestivalBonus, "top:318.0px; left:160.0px;");

		// lblEtin
		/*lblInsentive = new Label("Insentive Bonus :");
		lblInsentive.setImmediate(false);
		lblInsentive.setWidth("-1px");
		lblInsentive.setHeight("-1px");
		mainLayout.addComponent(lblInsentive, "top:365.0px; left:45.0px;");

		// txtSanctionAmount
		txtInsentive = new AmountField();
		txtInsentive.setImmediate(true);
		txtInsentive.setWidth("110px");
		txtInsentive.setHeight("-1px");
		mainLayout.addComponent(txtInsentive, "top:363.0px; left:160.0px;");*/

		// lblEtin
		/*lblEducationAllowance = new Label("Edu. Allowance :");
		lblEducationAllowance.setImmediate(false);
		lblEducationAllowance.setWidth("-1px");
		lblEducationAllowance.setHeight("-1px");
		mainLayout.addComponent(lblEducationAllowance, "top:390.0px; left:45.0px;");

		// txtSanctionAmount
		txtEducationAllowance = new AmountField();
		txtEducationAllowance.setImmediate(true);
		txtEducationAllowance.setWidth("110px");
		txtEducationAllowance.setHeight("-1px");
		mainLayout.addComponent(txtEducationAllowance, "top:388.0px; left:160.0px;");*/

		// lblEtin
		lblLeaveEncashment = new Label("Leave Encashment :");
		lblLeaveEncashment.setImmediate(false);
		lblLeaveEncashment.setWidth("-1px");
		lblLeaveEncashment.setHeight("-1px");
		mainLayout.addComponent(lblLeaveEncashment, "top:345.0px; left:45.0px;");

		// txtSanctionAmount
		txtLeaveEncashment = new AmountField();
		txtLeaveEncashment.setImmediate(true);
		txtLeaveEncashment.setWidth("110px");
		txtLeaveEncashment.setHeight("-1px");
		mainLayout.addComponent(txtLeaveEncashment, "top:343.0px; left:160.0px;");
				

		lblPFComContribution = new Label("P.F.Co.'s Cont.:");
		lblPFComContribution.setImmediate(false);
		lblPFComContribution.setWidth("-1px");
		lblPFComContribution.setHeight("-1px");
		mainLayout.addComponent(lblPFComContribution, "top:370.0px; left:45.0px;");
	
		// txtSanctionAmount
		txtPFComContribution  = new AmountField();
		txtPFComContribution.setImmediate(true);
		txtPFComContribution.setWidth("110px");
		txtPFComContribution.setHeight("-1px");
		mainLayout.addComponent(txtPFComContribution, "top:368.0px; left:160.0px;");
		

		// lblEtin
		lblOther = new Label("Other :");
		lblOther.setImmediate(false);
		lblOther.setWidth("-1px");
		lblOther.setHeight("-1px");
		mainLayout.addComponent(lblOther, "top:395.0px; left:45.0px;");

		// txtSanctionAmount
		txtOther = new AmountField();
		txtOther.setImmediate(true);
		txtOther.setWidth("110px");
		txtOther.setHeight("-1px");
		mainLayout.addComponent(txtOther, "top:393.0px; left:160.0px;");

		// lblEtin
		/*lblBppf = new Label("B.P.P.F(5%) :");
		lblBppf.setImmediate(false);
		lblBppf.setWidth("-1px");
		lblBppf.setHeight("-1px");
		mainLayout.addComponent(lblBppf, "top:490.0px; left:45.0px;");

		// txtSanctionAmount
		txtBppf = new AmountField();
		txtBppf.setImmediate(true);
		txtBppf.setWidth("110px");
		txtBppf.setHeight("-1px");
		mainLayout.addComponent(txtBppf, "top:488.0px; left:160.0px;");*/

		// lblEtin
		lblTotalEarning = new Label("Total Earning :");
		lblTotalEarning.setImmediate(false);
		lblTotalEarning.setWidth("-1px");
		lblTotalEarning.setHeight("-1px");
		mainLayout.addComponent(lblTotalEarning, "top:420.0px; left:45.0px;");

		// txtSanctionAmount
		txtTotalEarning = new TextRead(1);
		txtTotalEarning.setImmediate(true);
		txtTotalEarning.setWidth("110px");
		txtTotalEarning.setHeight("-1px");
		mainLayout.addComponent(txtTotalEarning, "top:418.0px; left:160.0px;");

		//==============================================================


		lblAllowance = new Label("<html><font color='#74078B'> <strong> 2. Allowance (Non Taxable Income ) :</strong></font></html>",Label.CONTENT_XHTML);
		lblAllowance.setImmediate(false);
		lblAllowance.setWidth("-1px");
		lblAllowance.setHeight("-1px");
		mainLayout.addComponent(lblAllowance, "top:445.0px; left:85.0px;");

		lblHR = new Label("H/R :");
		lblHR.setImmediate(false);
		lblHR.setWidth("-1px");
		lblHR.setHeight("-1px");
		mainLayout.addComponent(lblHR, "top:470.0px; left:45.0px;");

		// txtSanctionAmount
		txtHR = new AmountField();
		txtHR.setImmediate(true);
		txtHR.setWidth("110px");
		txtHR.setHeight("-1px");
		mainLayout.addComponent(txtHR, "top:468.0px; left:160.0px;");

		// lblEtin
		lblMedical = new Label("Medical :");
		lblMedical.setImmediate(false);
		lblMedical.setWidth("-1px");
		lblMedical.setHeight("-1px");
		mainLayout.addComponent(lblMedical, "top:495.0px; left:45.0px;");

		// txtSanctionAmount
		txtMedical = new AmountField();
		txtMedical.setImmediate(true);
		txtMedical.setWidth("110px");
		txtMedical.setHeight("-1px");
		mainLayout.addComponent(txtMedical, "top:493.0px; left:160.0px;");

		// lblLoanSanction
		lblactual = new Label("<html><font color='#74078B'> <strong> 3.Actual Investment :</strong></font></html>",Label.CONTENT_XHTML);
		lblactual.setImmediate(false);
		lblactual.setWidth("-1px");
		lblactual.setHeight("-1px");
		mainLayout.addComponent(lblactual, "top:15.0px; left:400.0px;");

		// lblEtin
		lblOwnAndComContribution = new Label("Own +Co.'s Contribution :");
		lblOwnAndComContribution.setImmediate(false);
		lblOwnAndComContribution.setWidth("-1px");
		lblOwnAndComContribution.setHeight("-1px");
		mainLayout.addComponent(lblOwnAndComContribution, "top:40.0px; left:530.0px;");

		// txtSanctionAmount
		txtOwnAndComContribution = new AmountField();
		txtOwnAndComContribution.setImmediate(true);
		txtOwnAndComContribution.setWidth("110px");
		txtOwnAndComContribution.setHeight("-1px");
		mainLayout.addComponent(txtOwnAndComContribution, "top:38.0px; left:710.0px;");

		// lblEtin
		lblLife = new Label("Life Insurance policy Paid :");
		lblLife.setImmediate(false);
		lblLife.setWidth("-1px");
		lblLife.setHeight("-1px");
		mainLayout.addComponent(lblLife, "top:65.0px; left:530.0px;");

		// txtSanctionAmount
		txtLife = new AmountField();
		txtLife.setImmediate(true);
		txtLife.setWidth("110px");
		txtLife.setHeight("-1px");
		mainLayout.addComponent(txtLife, "top:63.0px; left:710.0px;");

		// lblEtin
		lblStock = new Label("Stock and Share(I.P.O) :");
		lblStock.setImmediate(false);
		lblStock.setWidth("-1px");
		lblStock.setHeight("-1px");
		mainLayout.addComponent(lblStock, "top:90.0px; left:530.0px;");

		// txtSanctionAmount
		txtStock = new AmountField();
		txtStock.setImmediate(true);
		txtStock.setWidth("110px");
		txtStock.setHeight("-1px");
		mainLayout.addComponent(txtStock, "top:88.0px; left:710.0px;");

		// lblEtin
		lblDPS = new Label("DPS/APS :");
		lblDPS.setImmediate(false);
		lblDPS.setWidth("-1px");
		lblDPS.setHeight("-1px");
		mainLayout.addComponent(lblDPS, "top:115.0px; left:530.0px;");

		// txtSanctionAmount
		txtDPS = new AmountField();
		txtDPS.setImmediate(true);
		txtDPS.setWidth("110px");
		txtDPS.setHeight("-1px");
		mainLayout.addComponent(txtDPS, "top:113.0px; left:710.0px;");

		// lblEtin
		lblSaving = new Label("Saving Certificate/Sancoy :");
		lblSaving.setImmediate(false);
		lblSaving.setWidth("-1px");
		lblSaving.setHeight("-1px");
		mainLayout.addComponent(lblSaving, "top:140.0px; left:530.0px;");

		// txtSanctionAmount
		txtSaving = new AmountField();
		txtSaving.setImmediate(true);
		txtSaving.setWidth("110px");
		txtSaving.setHeight("-1px");
		mainLayout.addComponent(txtSaving, "top:138.0px; left:710.0px;");

		lblDesktop = new Label("Desktop/Laptop Purchase:");
		lblDesktop.setImmediate(false);
		lblDesktop.setWidth("-1px");
		lblDesktop.setHeight("-1px");
		mainLayout.addComponent(lblDesktop, "top:165.0px; left:530.0px;");

		txtDesktop = new AmountField();
		txtDesktop.setImmediate(true);
		txtDesktop.setWidth("110px");
		txtDesktop.setHeight("-1px");
		mainLayout.addComponent(txtDesktop, "top:163.0px; left:710.0px;");

		lblTotalActualInvesment = new Label("Total :");
		lblTotalActualInvesment.setImmediate(false);
		lblTotalActualInvesment.setWidth("-1px");
		lblTotalActualInvesment.setHeight("-1px");
		mainLayout.addComponent(lblTotalActualInvesment, "top:190.0px; left:530.0px;");

		txtTotalActualInvesment = new AmountField();
		txtTotalActualInvesment.setImmediate(true);
		txtTotalActualInvesment.setWidth("110px");
		txtTotalActualInvesment.setHeight("-1px");
		mainLayout.addComponent(txtTotalActualInvesment, "top:188.0px; left:710.0px;");

		//------------------------ IncomeTaxFirst ------------------------//
		mainLayout.addComponent(new Label("<html><font color='#74078B'> <strong> 4.Tax Against Income :</strong></font></html>",Label.CONTENT_XHTML), "top:215.0px; left:400.0px;");

		txtFirst = new AmountField();
		txtFirst.setImmediate(true);
		txtFirst.setWidth("100px");
		txtFirst.setHeight("-1px");
		txtFirst.setValue("250000");
		mainLayout.addComponent(new Label("First :"), "top:240.0px; left:400.0px;");
		mainLayout.addComponent(txtFirst, "top:238.0px; left:440.0px;");

		txtFirstTax = new AmountField();
		txtFirstTax.setImmediate(true);
		txtFirstTax.setWidth("50px");
		txtFirstTax.setHeight("-1px");
		txtFirstTax.setValue("0");
		mainLayout.addComponent(new Label("Tax "), "top:240.0px; left:540.0px;");
		mainLayout.addComponent(txtFirstTax, "top:238.0px; left:580.0px;");
		
		mainLayout.addComponent(new Label("<html> <b>%</b> </html>",Label.CONTENT_XHTML),  "top:240.0px; left:630.0px;");
		mainLayout.addComponent(new Label("<html> <b>Tk</b>  </html>",Label.CONTENT_XHTML),  "top:240.0px; left:670.0px;");

		txtFirstTk = new AmountField();
		txtFirstTk.setImmediate(true);
		txtFirstTk.setWidth("110px");
		txtFirstTk.setHeight("-1px");
		mainLayout.addComponent(txtFirstTk,  "top:238.0px; left:710.0px;");
		//------------------------ IncomeTaxFirst ------------------------//


		//------------------------ IncomeTaxNextOne ------------------------//
		txtNextOne = new AmountField();
		txtNextOne.setImmediate(true);
		txtNextOne.setWidth("100px");
		txtNextOne.setHeight("-1px");
		txtNextOne.setValue("400000");
		mainLayout.addComponent(new Label("Next :"),  "top:265.0px; left:400.0px;");
		mainLayout.addComponent(txtNextOne, "top:263.0px; left:440.0px;");
		mainLayout.addComponent(new Label("Tax "), "top:265.0px; left:540.0px;");

		txtTaxOne = new AmountField();
		txtTaxOne.setImmediate(true);
		txtTaxOne.setWidth("50px");
		txtTaxOne.setHeight("-1px");
		txtTaxOne.setValue("10");
		mainLayout.addComponent(txtTaxOne,  "top:263.0px; left:580.0px;");
		mainLayout.addComponent(new Label("<html> <b>%</b> </html>",Label.CONTENT_XHTML),  "top:265.0px; left:630.0px;");
		mainLayout.addComponent(new Label("<html> <b>Tk</b> </html>",Label.CONTENT_XHTML),  "top:265.0px; left:670.0px;");

		txtNextOneTk = new AmountField();
		txtNextOneTk.setImmediate(true);
		txtNextOneTk.setWidth("110px");
		txtNextOneTk.setHeight("-1px");
		mainLayout.addComponent(txtNextOneTk, "top:263.0px; left:710.0px;");
		//------------------------ IncomeTaxNextOne ------------------------//
		

		//------------------------ IncomeTaxNextTwo ------------------------//
		txtNextTwo = new AmountField();
		txtNextTwo.setImmediate(true);
		txtNextTwo.setWidth("100px");
		txtNextTwo.setHeight("-1px");
		txtNextTwo.setValue("500000");
		mainLayout.addComponent(new Label("Next :"),  "top:290.0px; left:400.0px;");
		mainLayout.addComponent(txtNextTwo, "top:288.0px; left:440.0px;");
		mainLayout.addComponent(new Label("Tax "), "top:290.0px; left:540.0px;");

		txtTaxTwo = new AmountField();
		txtTaxTwo.setImmediate(true);
		txtTaxTwo.setWidth("50px");
		txtTaxTwo.setHeight("-1px");
		txtTaxTwo.setValue("15");
		mainLayout.addComponent(txtTaxTwo,  "top:288.0px; left:580.0px;");
		mainLayout.addComponent(new Label("<html> <b>%</b> </html>",Label.CONTENT_XHTML),  "top:290.0px; left:630.0px;");
		mainLayout.addComponent(new Label("<html> <b>Tk</b> </html>",Label.CONTENT_XHTML),  "top:290.0px; left:670.0px;");

		txtNextTwoTk = new AmountField();
		txtNextTwoTk.setImmediate(true);
		txtNextTwoTk.setWidth("110px");
		txtNextTwoTk.setHeight("-1px");
		mainLayout.addComponent(txtNextTwoTk,  "top:288.0px; left:710.0px;");
		//------------------------ IncomeTaxNextTwo ------------------------//


		//------------------------ IncomeTaxNextThree ------------------------//
		txtNextThree = new AmountField();
		txtNextThree.setImmediate(true);
		txtNextThree.setWidth("100px");
		txtNextThree.setHeight("-1px");
		txtNextThree.setValue("600000");
		mainLayout.addComponent(new Label("Next :"), "top:315.0px; left:400.0px;");
		mainLayout.addComponent(txtNextThree,  "top:313.0px; left:440.0px;");
		mainLayout.addComponent(new Label("Tax "),  "top:315.0px; left:540.0px;");

		txtTaxThree = new AmountField();
		txtTaxThree.setImmediate(true);
		txtTaxThree.setWidth("50px");
		txtTaxThree.setHeight("-1px");
		txtTaxThree.setValue("20");
		mainLayout.addComponent(txtTaxThree,  "top:313.0px; left:580.0px;");
		mainLayout.addComponent(new Label("%"),  "top:315.0px; left:630.0px;");
		mainLayout.addComponent(new Label("Tk"),  "top:315.0px; left:670.0px;");

		txtNextThreeTk = new AmountField();
		txtNextThreeTk.setImmediate(true);
		txtNextThreeTk.setWidth("110px");
		txtNextThreeTk.setHeight("-1px");
		mainLayout.addComponent(txtNextThreeTk, "top:313.0px; left:710.0px;");
		//------------------------ IncomeTaxNextThree ------------------------//	
	
		// lblLoanNo
		lblTotalTax = new Label("Total Tax :");
		lblTotalTax.setImmediate(false);
		lblTotalTax.setWidth("-1px");
		lblTotalTax.setHeight("-1px");
		mainLayout.addComponent(lblTotalTax,  "top:340.0px; left:530.0px;");

		// txtLoanNo
		txtTotalTax = new AmountField();
		txtTotalTax.setImmediate(true);
		txtTotalTax.setWidth("110px");
		txtTotalTax.setHeight("22px");
		mainLayout.addComponent(txtTotalTax,  "top:338.0px; left:710.0px;");

		// lblLoanType
		lblTaxRebate = new Label("Tax Rebate :");
		lblTaxRebate.setImmediate(false);
		lblTaxRebate.setWidth("-1px");
		lblTaxRebate.setHeight("-1px");
		mainLayout.addComponent(lblTaxRebate,  "top:365.0px; left:530.0px;");

		// txtLoanType
		txtTaxRebate = new AmountField();
		txtTaxRebate.setImmediate(true);
		txtTaxRebate.setWidth("110px");
		txtTaxRebate.setHeight("-1px");
		mainLayout.addComponent(txtTaxRebate,  "top:363.0px; left:710.0px;");	

		// lblLoanAmount
		lblAdvance = new Label("Advance Income Tax (AIT) :");
		lblAdvance.setImmediate(false);
		lblAdvance.setWidth("-1px");
		lblAdvance.setHeight("-1px");
		mainLayout.addComponent(lblAdvance,  "top:390.0px; left:530.0px;");

		// txtLoanAmount
		txtAdvanceIncomeTax = new AmountField();
		txtAdvanceIncomeTax.setImmediate(true);
		txtAdvanceIncomeTax.setWidth("110px");
		txtAdvanceIncomeTax.setHeight("-1px");
		mainLayout.addComponent(txtAdvanceIncomeTax,  "top:388.0px; left:710.0px;");

		// lblRateOfInterest
		lblNetTaxPay = new Label("Net Tax Payable :");
		lblNetTaxPay.setImmediate(false);
		lblNetTaxPay.setWidth("-1px");
		lblNetTaxPay.setHeight("-1px");
		mainLayout.addComponent(lblNetTaxPay,  "top:415.0px; left:530.0px;");

		// txtRateOfInterest
		txtNetTaxPay = new AmountField();
		txtNetTaxPay.setImmediate(true);
		txtNetTaxPay.setWidth("110px");
		txtNetTaxPay.setHeight("-1px");
		mainLayout.addComponent(txtNetTaxPay,  "top:413.0px; left:710.0px;");

		// lblRateOfInterest
		lblMinimum = new Label("Minimum Tax :");
		lblMinimum.setImmediate(false);
		lblMinimum.setWidth("-1px");
		lblMinimum.setHeight("-1px");
		mainLayout.addComponent(lblMinimum,  "top:440.0px; left:530.0px;");
		
		// txtRateOfInterest
		txtMinimum = new AmountField();
		txtMinimum.setImmediate(true);
		txtMinimum.setWidth("110px");
		txtMinimum.setHeight("-1px");
		mainLayout.addComponent(txtMinimum,  "top:438.0px; left:710.0px;");

		// lblLoanSanction
		lblPaidBy = new Label("<html><font color='#74078B'> <strong> 5. Paid by company with following cheque /challan : </strong></font></html>",Label.CONTENT_XHTML);
		lblPaidBy.setImmediate(false);
		lblPaidBy.setWidth("-1px");
		lblPaidBy.setHeight("-1px");
		//mainLayout.addComponent(lblPaidBy, "top:535.0px; left:50.0px;");

		mainLayout.addComponent(cButton, "top:540.0px; left:140.0px;");

		table.setWidth("780");
		table.setHeight("150.0px");
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("SL", Label.class, new Label());
		table.setColumnWidth("SL", 20);

		table.addContainerProperty("Cheque/DD No.", Label.class, new Label());
		table.setColumnWidth("Cheque/DD No.", 150);

		table.addContainerProperty("Date", Label.class, new Label());
		table.setColumnWidth("Date", 110);

		table.addContainerProperty("Challan No.", Label.class, new Label());
		table.setColumnWidth("Challan No.", 150);

		table.addContainerProperty("Challan Date", Label.class, new Label());
		table.setColumnWidth("Challan Date",110);

		table.addContainerProperty("Amount", AmountField.class, new AmountField());
		table.setColumnWidth("Amount", 120);
		//mainLayout.addComponent(table, "top:560.0px;left:50.0px");

		table.setStyleName("wordwrap-headers");		
		//mainLayout.addComponent(cButton, "top:725.0px; left:140.0px;");
		return mainLayout;
	}
}
