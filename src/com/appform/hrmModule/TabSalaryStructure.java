package com.appform.hrmModule;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

import com.common.share.AmountCommaSeperator;
import com.common.share.CommaSeparator;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class TabSalaryStructure extends VerticalLayout
{
	public AbsoluteLayout mainLayout;

	public Label lblBankName;
	public Label lblBranchName;
	public Label lblAccountNo,lblRoutingNo;

	public AmountCommaSeperator txtGrossAmount;
	public AmountCommaSeperator txtBasicAdd;
	public AmountCommaSeperator txtHouseRentAdd;
	public AmountCommaSeperator txtMedicalAllowanceAdd;
	public AmountCommaSeperator txtClinicalAllowanceAdd;
//	public AmountCommaSeperator txtNonPracticeAllowanceAdd;
	public AmountCommaSeperator txtSpecialAllowanceAdd;
	public AmountCommaSeperator txtOtherAllowanceAdd;
	public AmountCommaSeperator txtDearnessAllowanceAdd;
	public AmountCommaSeperator txtConveyanceAllowanceAdd;
	public AmountCommaSeperator txtAttendanceBonusAdd;
	public AmountCommaSeperator txtTiffinAllowanceAdd;
	public AmountCommaSeperator txtMobileAllowanceAdd;

	public AmountCommaSeperator txtRoomChargeLess;
	public AmountCommaSeperator txtIncomeTaxLess;
	public AmountCommaSeperator txtProvidentFundLess;

	public AmountCommaSeperator txtKallanFundLess;
	public AmountCommaSeperator txtKhichuriMealLess;

	public TextField txtAccountNo,txtRoutingNo;

	public TextRead txtTotalGross;

	public ComboBox cmbBankName;
	public ComboBox cmbBranchName;
	private static final List<String> type=Arrays.asList(new String[]{"Bank A/C","BFTN","Cash"/*,"Mobile A/C"*/});
	public OptionGroup opgBank;
	ArrayList<Component> allComp = new ArrayList<Component>();

	private DecimalFormat dff = new DecimalFormat("#0");

	public TabSalaryStructure() 
	{
		buildMainLayout();
		addComponent(mainLayout);

		addBankName();
		addBranchName();

		setEventAction();
		opgBank.setValue("Bank A/C");
		allTrueFalse(false);
		cmbBankName.setVisible(true);
		lblBankName.setVisible(true);
		cmbBranchName.setVisible(true);
		lblBranchName.setVisible(true);
		txtAccountNo.setVisible(true);
		lblAccountNo.setVisible(true);
		lblRoutingNo.setVisible(true);
		txtRoutingNo.setVisible(true);
	}
	public void allTrueFalse(boolean x)
	{
		cmbBankName.setVisible(x);
		cmbBranchName.setVisible(x);
		lblAccountNo.setVisible(x);
		lblBankName.setVisible(x);
		lblBranchName.setVisible(x);
		txtAccountNo.setVisible(x);
		lblRoutingNo.setVisible(x);
		txtRoutingNo.setVisible(x);
	}
	public void addBankName()
	{
		cmbBankName.removeAllItems();

		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select vBankId,bankName from tbBankName order by bankName";
			List <?> list = session.createSQLQuery(query).list();	
			for(Iterator <?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbBankName.addItem(element[0].toString());
				cmbBankName.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp){}
		finally{session.close();}
	}

	public void addBranchName()
	{
		cmbBranchName.removeAllItems();

		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select id,branchName from tbBankBranch order by branchName";
			List <?> list = session.createSQLQuery(query).list();	
			for(Iterator <?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbBranchName.addItem(element[0].toString());
				cmbBranchName.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp){}
		finally{session.close();}
	}

	private void setEventAction()
	{
		txtGrossAmount.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				grossAmountCalculation();
				totalSalaryCalculation();
			}
		});
		txtBasicAdd.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				totalSalaryCalculation();
			}
		});
		txtHouseRentAdd.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				totalSalaryCalculation();
			}
		});
		txtMedicalAllowanceAdd.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				totalSalaryCalculation();
			}
		});
		txtClinicalAllowanceAdd.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				totalSalaryCalculation();
			}
		});
	/*	txtNonPracticeAllowanceAdd.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				totalSalaryCalculation();
			}
		});*/
		txtOtherAllowanceAdd.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				totalSalaryCalculation();
			}
		});
		txtDearnessAllowanceAdd.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				totalSalaryCalculation();
			}
		});
		txtConveyanceAllowanceAdd.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				totalSalaryCalculation();
			}
		});
		txtSpecialAllowanceAdd.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				totalSalaryCalculation();
			}
		});
		txtRoomChargeLess.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				totalSalaryCalculation();
			}
		});
		txtIncomeTaxLess.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				totalSalaryCalculation();
			}
		});
		txtProvidentFundLess.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				totalSalaryCalculation();
			}
		});
		txtKallanFundLess.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				totalSalaryCalculation();
			}
		});
		txtKhichuriMealLess.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				totalSalaryCalculation();
			}
		});
		txtAttendanceBonusAdd.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				totalSalaryCalculation();
			}
		});
		txtTiffinAllowanceAdd.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				totalSalaryCalculation();
			}
		});
		opgBank.addListener(new ValueChangeListener() {	
			
			public void valueChange(ValueChangeEvent event) {
				allTrueFalse(false);
				if(opgBank.getValue().toString().equals("Cash"))
				{
					allTrueFalse(false);
				}
				if(opgBank.getValue().toString().equals("Bank A/C"))
				{
					allTrueFalse(true);
					lblAccountNo.setValue("Account No. :");
				}
				if(opgBank.getValue().toString().equals("Mobile A/C"))
				{
					allTrueFalse(true);
					lblAccountNo.setValue("Mobile Account No.:");
				}
				if(opgBank.getValue().toString().equals("BFTN"))
				{
					allTrueFalse(true);
					lblAccountNo.setValue("Account No. :");
				}
			}
	});
	}
	private void grossAmountCalculation()
	{
		double gross = Double.parseDouble(txtGrossAmount.getValue().toString().isEmpty()?"0":txtGrossAmount.getValue().toString().replaceAll(",", "").trim());
		if(!txtGrossAmount.getValue().isEmpty()){
			double basic=0,house=0,medical=0,conv=0;
			/*basic=(60/100)*gross;
			house=(30/100)*gross;
			medical=(6/100)*gross;
			conv=(4/100)*gross;*/
			
			//basic=0.6*gross;
			//house=0.3*gross;
			//medical=0.06*gross;
			//conv=0.04*gross;
			
			//txtBasicAdd.setValue(new CommaSeparator().setComma(basic));
			//txtHouseRentAdd.setValue(new CommaSeparator().setComma(house));
			//txtMedicalAllowanceAdd.setValue(new CommaSeparator().setComma(medical));
			//txtConveyanceAllowanceAdd.setValue(new CommaSeparator().setComma(conv));
			
			System.out.println("txtGrossAmount: "+txtGrossAmount.getValue()+" basic: "+basic+" house: "+house+" medical: "+medical+" conv"+conv);
			
		}
		
	}

	private void totalSalaryCalculation()
	{
		//double basic = Double.parseDouble(txtBasicAdd.getValue().toString().isEmpty()?"0":txtBasicAdd.getValue().toString().replaceAll(",", "").trim());
		//double house = Double.parseDouble(txtHouseRentAdd.getValue().toString().isEmpty()?"0":txtHouseRentAdd.getValue().toString().replaceAll(",", "").trim());
		//double medical = Double.parseDouble(txtMedicalAllowanceAdd.getValue().toString().isEmpty()?"0":txtMedicalAllowanceAdd.getValue().toString().replaceAll(",", "").trim());
		//double clinic = Double.parseDouble(txtClinicalAllowanceAdd.getValue().toString().isEmpty()?"0":txtClinicalAllowanceAdd.getValue().toString().replaceAll(",", "").trim());
	//	double nonPrac = Double.parseDouble(txtNonPracticeAllowanceAdd.getValue().toString().isEmpty()?"0":txtNonPracticeAllowanceAdd.getValue().toString().replaceAll(",", "").trim());
		//double special = Double.parseDouble(txtSpecialAllowanceAdd.getValue().toString().isEmpty()?"0":txtSpecialAllowanceAdd.getValue().toString().replaceAll(",", "").trim());
		//double other = Double.parseDouble(txtOtherAllowanceAdd.getValue().toString().isEmpty()?"0":txtOtherAllowanceAdd.getValue().toString().replaceAll(",", "").trim());
		//double dearness = Double.parseDouble(txtDearnessAllowanceAdd.getValue().toString().isEmpty()?"0":txtDearnessAllowanceAdd.getValue().toString().replaceAll(",", "").trim());
		//double convence = Double.parseDouble(txtConveyanceAllowanceAdd.getValue().toString().isEmpty()?"0":txtConveyanceAllowanceAdd.getValue().toString().replaceAll(",", "").trim());
		//double attenBonus = Double.parseDouble(txtAttendanceBonusAdd.getValue().toString().isEmpty()?"0":txtAttendanceBonusAdd.getValue().toString().replaceAll(",", "").trim());
		//double tiffin = Double.parseDouble(txtTiffinAllowanceAdd.getValue().toString().isEmpty()?"0":txtTiffinAllowanceAdd.getValue().toString().replaceAll(",", "").trim());
		//double mobile = Double.parseDouble(txtMobileAllowanceAdd.getValue().toString().isEmpty()?"0":txtMobileAllowanceAdd.getValue().toString().replaceAll(",", "").trim());

		/*double room = Double.parseDouble(txtRoomChargeLess.getValue().toString().isEmpty()?"0":txtRoomChargeLess.getValue().toString().replaceAll(",", "").trim());
		double income = Double.parseDouble(txtIncomeTaxLess.getValue().toString().isEmpty()?"0":txtIncomeTaxLess.getValue().toString().replaceAll(",", "").trim());
		double provident = Double.parseDouble(txtProvidentFundLess.getValue().toString().isEmpty()?"0":txtProvidentFundLess.getValue().toString().replaceAll(",", "").trim());
		double kallan = Double.parseDouble(txtKallanFundLess.getValue().toString().isEmpty()?"0":txtKallanFundLess.getValue().toString().replaceAll(",", "").trim());
		double khichuri = Double.parseDouble(txtKhichuriMealLess.getValue().toString().isEmpty()?"0":txtKhichuriMealLess.getValue().toString().replaceAll(",", "").trim());*/

		//double gross = (basic+house+medical+clinic+/*nonPrac+*/special+other+dearness+convence+attenBonus+tiffin);

		txtTotalGross.setValue(new CommaSeparator().setComma(Double.parseDouble(txtGrossAmount.getValue().toString().isEmpty()?"0":txtGrossAmount.getValue().toString().replaceAll(",", ""))));
	}

/*	public void houseRentCalculation()
	{
		double basic = Double.parseDouble(txtBasicAdd.getValue().toString().isEmpty()?"0":txtBasicAdd.getValue().toString().replaceAll(",", "").trim());

		txtHouseRentAdd.setValue(dff.format((60*basic)/100));
		txtConveyanceAllowanceAdd.setValue(dff.format((30*basic)/100));
	}*/

	public AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);

		setWidth("100%");
		setHeight("100%");

		txtGrossAmount = new AmountCommaSeperator();
		txtGrossAmount.setImmediate(true);
		txtGrossAmount.setWidth("120px");
		txtGrossAmount.setHeight("-1px");

		txtBasicAdd = new AmountCommaSeperator();
		txtBasicAdd.setImmediate(true);
		txtBasicAdd.setWidth("120px");
		txtBasicAdd.setHeight("-1px");

		txtHouseRentAdd = new AmountCommaSeperator();
		txtHouseRentAdd.setImmediate(true);
		txtHouseRentAdd.setWidth("120px");
		txtHouseRentAdd.setHeight("-1px");

		txtMedicalAllowanceAdd = new AmountCommaSeperator();
		txtMedicalAllowanceAdd.setImmediate(true);
		txtMedicalAllowanceAdd.setWidth("120px");
		txtMedicalAllowanceAdd.setHeight("-1px");

		txtClinicalAllowanceAdd = new AmountCommaSeperator();
		txtClinicalAllowanceAdd.setImmediate(true);
		txtClinicalAllowanceAdd.setWidth("120px");
		txtClinicalAllowanceAdd.setHeight("-1px");

/*		txtNonPracticeAllowanceAdd = new AmountCommaSeperator();
		txtNonPracticeAllowanceAdd.setImmediate(true);
		txtNonPracticeAllowanceAdd.setWidth("120px");
		txtNonPracticeAllowanceAdd.setHeight("-1px");
*/
		txtOtherAllowanceAdd = new AmountCommaSeperator();
		txtOtherAllowanceAdd.setImmediate(true);
		txtOtherAllowanceAdd.setWidth("120px");
		txtOtherAllowanceAdd.setHeight("-1px");

		txtDearnessAllowanceAdd = new AmountCommaSeperator();
		txtDearnessAllowanceAdd.setImmediate(true);
		txtDearnessAllowanceAdd.setWidth("120px");
		txtDearnessAllowanceAdd.setHeight("-1px");

		txtConveyanceAllowanceAdd = new AmountCommaSeperator();
		txtConveyanceAllowanceAdd.setImmediate(true);
		txtConveyanceAllowanceAdd.setWidth("120px");
		txtConveyanceAllowanceAdd.setHeight("-1px");

		txtSpecialAllowanceAdd = new AmountCommaSeperator();
		txtSpecialAllowanceAdd.setImmediate(true);
		txtSpecialAllowanceAdd.setWidth("120px");
		txtSpecialAllowanceAdd.setHeight("-1px");

		txtAttendanceBonusAdd = new AmountCommaSeperator();
		txtAttendanceBonusAdd.setImmediate(true);
		txtAttendanceBonusAdd.setWidth("120px");
		txtAttendanceBonusAdd.setHeight("-1px");

		txtTiffinAllowanceAdd = new AmountCommaSeperator();
		txtTiffinAllowanceAdd.setImmediate(true);
		txtTiffinAllowanceAdd.setWidth("120px");
		txtTiffinAllowanceAdd.setHeight("-1px");

		txtMobileAllowanceAdd = new AmountCommaSeperator();
		txtMobileAllowanceAdd.setImmediate(true);
		txtMobileAllowanceAdd.setWidth("120px");
		txtMobileAllowanceAdd.setHeight("-1px");

		txtRoomChargeLess = new AmountCommaSeperator();
		txtRoomChargeLess.setImmediate(true);
		txtRoomChargeLess.setWidth("120px");
		txtRoomChargeLess.setHeight("-1px");

		txtIncomeTaxLess = new AmountCommaSeperator();
		txtIncomeTaxLess.setImmediate(true);
		txtIncomeTaxLess.setWidth("120px");
		txtIncomeTaxLess.setHeight("-1px");

		txtProvidentFundLess = new AmountCommaSeperator();
		txtProvidentFundLess.setImmediate(true);
		txtProvidentFundLess.setWidth("120px");
		txtProvidentFundLess.setHeight("-1px");

		txtKallanFundLess = new AmountCommaSeperator();
		txtKallanFundLess.setImmediate(true);
		txtKallanFundLess.setWidth("120px");
		txtKallanFundLess.setHeight("-1px");

		txtKhichuriMealLess = new AmountCommaSeperator();
		txtKhichuriMealLess.setImmediate(true);
		txtKhichuriMealLess.setWidth("120px");
		txtKhichuriMealLess.setHeight("-1px");

		opgBank=new OptionGroup("",type);
		opgBank.setHeight("-1px");
		opgBank.setImmediate(true);
		opgBank.setStyleName("horizontal");

		lblBankName = new Label("Bank Name :");

		cmbBankName = new ComboBox();
		cmbBankName.setImmediate(true);
		cmbBankName.setWidth("200px");
		cmbBankName.setHeight("-1px");

		lblBranchName=new Label("Branch Name :");

		cmbBranchName = new ComboBox();
		cmbBranchName.setImmediate(true);
		cmbBranchName.setWidth("200px");
		cmbBranchName.setHeight("-1px");

		lblAccountNo=new Label("Account No. :");
		lblRoutingNo=new Label("Routing No. :");

		txtAccountNo = new TextField();
		txtAccountNo.setImmediate(true);
		txtAccountNo.setWidth("180px");
		txtAccountNo.setHeight("-1px");

		txtTotalGross = new TextRead(1);
		txtTotalGross.setImmediate(true);
		txtTotalGross.setWidth("120px");
		txtTotalGross.setHeight("24px");
		txtTotalGross.setStyleName("tcoloum");

		mainLayout.addComponent(new Label("<b><Font size='3px'><u>Addition</u></b></font>",Label.CONTENT_XHTML),"top:20.0px;left:140.0px;");

		//Addition
		mainLayout.addComponent(new Label("Gross :"),"top:50.0px;left:30.0px;");
		mainLayout.addComponent(txtGrossAmount,"top:48.0px;left:187.0px;");
		txtGrossAmount.setImmediate(true);
		
		mainLayout.addComponent(new Label("Basic :"),"top:75.0px;left:30.0px;");
		mainLayout.addComponent(txtBasicAdd,"top:73.0px;left:187.0px;");
		//mainLayout.addComponent(new Label("60% of Gross :"),"top:75.0px;left:310.0px;");

		mainLayout.addComponent(new Label("House Rent :"),"top:100.0px;left:30.0px;");
		mainLayout.addComponent(txtHouseRentAdd,"top:98.0px;left:187.0px;");
		//mainLayout.addComponent(new Label("30% of Gross :"),"top:100.0px;left:310.0px;");

		/*mainLayout.addComponent(new Label("Medical Allowance :"),"top:125.0px;left:30.0px;");
		mainLayout.addComponent(txtMedicalAllowanceAdd,"top:123.0px;left:187.0px;");*/
		//mainLayout.addComponent(new Label("6% of Gross :"),"top:125.0px;left:310.0px;");

	/*	mainLayout.addComponent(new Label("Clinical Allowance :"),"top:125.0px;left:30.0px;");
		mainLayout.addComponent(txtClinicalAllowanceAdd,"top:123.0px;left:187.0px;");*/

		/*mainLayout.addComponent(new Label("Non-Practice Allowance :"),"top:150.0px;left:30.0px;");
		mainLayout.addComponent(txtNonPracticeAllowanceAdd,"top:148.0px;left:187.0px;");*/

		/*mainLayout.addComponent(new Label("Conveyeance Allowance :"),"top:150.0px;left:30.0px;");
		mainLayout.addComponent(txtConveyanceAllowanceAdd,"top:148.0px;left:187.0px;");*/
		//mainLayout.addComponent(new Label("4% of Gross :"),"top:150.0px;left:310.0px;");
		
		mainLayout.addComponent(new Label("Mobile Allowance :"),"top:125.0px;left:30.0px;");
		mainLayout.addComponent(txtMobileAllowanceAdd,"top:123.0px;left:187.0px;");
		
		/*mainLayout.addComponent(new Label("Mobile Allowance :"),"top:150.0px;left:30.0px;");
		mainLayout.addComponent(txtMobileAllowanceAdd,"top:148.0px;left:187.0px;");*/
		

/*		mainLayout.addComponent(new Label("Dearness Allowance :"),"top:175.0px;left:30.0px;");
		mainLayout.addComponent(txtDearnessAllowanceAdd,"top:173.0px;left:187.0px;");*/

		/*mainLayout.addComponent(new Label("Special Allowance :"),"top:200.0px;left:30.0px;");
		mainLayout.addComponent(txtSpecialAllowanceAdd,"top:198.0px;left:187.0px;");*/

		//mainLayout.addComponent(new Label("Attendance Bonus :"),"top:200.0px;left:30.0px;");
		//mainLayout.addComponent(txtAttendanceBonusAdd,"top:198.0px;left:187.0px;");

		//mainLayout.addComponent(new Label("Tiffin Allowance :"),"top:225.0px;left:30.0px;");
		//mainLayout.addComponent(txtTiffinAllowanceAdd,"top:226.0px;left:187.0px;");
		
		
		//Deduction
		mainLayout.addComponent(new Label("<b><Font size='3px'><u>Deduction</u></b></font>",Label.CONTENT_XHTML),"top:20.0px;left:530.0px;");

		//mainLayout.addComponent(new Label("Room Charge :"),"top:50.0px;left:445.0px;");
		mainLayout.addComponent(txtRoomChargeLess,"top:48.0px;left:570.0px;");
		txtRoomChargeLess.setVisible(false);

		mainLayout.addComponent(new Label("Income Tax :"),"top:50.0px;left:445.0px;");
		mainLayout.addComponent(txtIncomeTaxLess,"top:48.0px;left:570.0px;");
		txtIncomeTaxLess.setVisible(true);

		mainLayout.addComponent(new Label("Provident Fund(%) :"),"top:75.0px;left:445.0px;");
		mainLayout.addComponent(txtProvidentFundLess,"top:73.0px;left:570.0px;");

	/*	mainLayout.addComponent(new Label("Kallan Fund :"),"top:125.0px;left:445.0px;");
		mainLayout.addComponent(txtKallanFundLess,"top:123.0px;left:570.0px;");*/

	/*	mainLayout.addComponent(new Label("Meal Charge :"),"top:100.0px;left:445.0px;");
		mainLayout.addComponent(txtKhichuriMealLess,"top:98.0px;left:570.0px;");*/
		
		//mainLayout.addComponent(new Label("Mobile Ceiling :"),"top:100.0px;left:445.0px;");
		//mainLayout.addComponent(txtMobileAllowanceAdd,"top:98.0px;left:570.0px;");
		
		mainLayout.addComponent(new Label("Payment Type : "),"top:100px; left:445px");
		mainLayout.addComponent(opgBank,"top:100px; left:570px");
				
		mainLayout.addComponent(lblBankName ,"top:125.0px;left:445.0px;");
		mainLayout.addComponent(cmbBankName,"top:123.0px;left:570.0px;");

		mainLayout.addComponent(lblBranchName,"top:150.0px;left:445.0px;");
		mainLayout.addComponent(cmbBranchName,"top:148.0px;left:570.0px;");
		
		mainLayout.addComponent(lblAccountNo,"top:175px;left:445.0px;");
		mainLayout.addComponent(txtAccountNo, "top:173px;left:570.0px;");
		

		txtRoutingNo = new TextField();
		txtRoutingNo.setImmediate(true);
		txtRoutingNo.setWidth("180px");
		txtRoutingNo.setHeight("-1px");
		mainLayout.addComponent(lblRoutingNo,"top:200px;left:445.0px;");
		mainLayout.addComponent(txtRoutingNo, "top:198px;left:570.0px;");

		mainLayout.addComponent(new Label("Total Salary :",Label.CONTENT_XHTML),"top:322.0px;left:30.0px;");
		mainLayout.addComponent(txtTotalGross,"top:320.0px;left:187.0px;");

		return mainLayout;
	}
}