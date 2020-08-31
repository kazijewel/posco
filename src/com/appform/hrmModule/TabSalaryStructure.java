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
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
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

	//public AmountCommaSeperator txtGrossAmount;
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
	
	public NativeButton btnPlusBank,btnPlusBranch;
	
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
		lblAccountNo.setVisible(x);
		txtAccountNo.setVisible(x);
		
		lblBankName.setVisible(x);	
		cmbBankName.setVisible(x);
		btnPlusBank.setVisible(x);
		
		lblBranchName.setVisible(x);
		cmbBranchName.setVisible(x);
		btnPlusBranch.setVisible(x);
		
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
		txtBasicAdd.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				grossAmountCalculation();
			}
		});
		txtHouseRentAdd.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				grossAmountCalculation();
			}
		});
		txtMedicalAllowanceAdd.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				grossAmountCalculation();
			}
		});
		txtClinicalAllowanceAdd.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				grossAmountCalculation();
			}
		});
	/*	txtNonPracticeAllowanceAdd.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				grossAmountCalculation();
			}
		});*/
		txtOtherAllowanceAdd.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				grossAmountCalculation();
			}
		});
		txtDearnessAllowanceAdd.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				grossAmountCalculation();
			}
		});
		txtConveyanceAllowanceAdd.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				grossAmountCalculation();
			}
		});
		txtSpecialAllowanceAdd.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				grossAmountCalculation();
			}
		});
		txtRoomChargeLess.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				grossAmountCalculation();
			}
		});
		txtIncomeTaxLess.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				grossAmountCalculation();
			}
		});
		txtProvidentFundLess.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				grossAmountCalculation();
			}
		});
		txtKallanFundLess.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				grossAmountCalculation();
			}
		});
		txtKhichuriMealLess.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				grossAmountCalculation();
			}
		});
		txtAttendanceBonusAdd.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				grossAmountCalculation();
			}
		});
		txtTiffinAllowanceAdd.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				grossAmountCalculation();
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
		double basic = Double.parseDouble(txtBasicAdd.getValue().toString().isEmpty()?"0":txtBasicAdd.getValue().toString().replaceAll(",", "").trim());
		double house = Double.parseDouble(txtHouseRentAdd.getValue().toString().isEmpty()?"0":txtHouseRentAdd.getValue().toString().replaceAll(",", "").trim());
		double mobile = Double.parseDouble(txtMobileAllowanceAdd.getValue().toString().isEmpty()?"0":txtMobileAllowanceAdd.getValue().toString().replaceAll(",", "").trim());
		double totalSalary=0.0;
		totalSalary=basic+house+mobile;
		txtTotalGross.setValue(totalSalary);
		
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
		
		btnPlusBank = new NativeButton();
		btnPlusBank.setIcon(new ThemeResource("../icons/add.png"));
		btnPlusBank.setImmediate(true);
		btnPlusBank.setWidth("28px");
		btnPlusBank.setHeight("24px");

		btnPlusBranch = new NativeButton();
		btnPlusBranch.setIcon(new ThemeResource("../icons/add.png"));
		btnPlusBranch.setImmediate(true);
		btnPlusBranch.setWidth("28px");
		btnPlusBranch.setHeight("24px");

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
		
		mainLayout.addComponent(new Label("Basic :"),"top:50.0px;left:30.0px;");
		mainLayout.addComponent(txtBasicAdd,"top:48.0px;left:187.0px;");
		//mainLayout.addComponent(new Label("60% of Gross :"),"top:75.0px;left:310.0px;");

		mainLayout.addComponent(new Label("House Rent :"),"top:75.0px;left:30.0px;");
		mainLayout.addComponent(txtHouseRentAdd,"top:73.0px;left:187.0px;");
		
		mainLayout.addComponent(new Label("Mobile Allowance :"),"top:100.0px;left:30.0px;");
		mainLayout.addComponent(txtMobileAllowanceAdd,"top:98.0px;left:187.0px;");		
		
		//Deduction
		mainLayout.addComponent(new Label("<b><Font size='3px'><u>Deduction</u></b></font>",Label.CONTENT_XHTML),"top:20.0px;left:530.0px;");

		//mainLayout.addComponent(new Label("Room Charge :"),"top:50.0px;left:445.0px;");
		mainLayout.addComponent(txtRoomChargeLess,"top:48.0px;left:570.0px;");
		txtRoomChargeLess.setVisible(false);

		mainLayout.addComponent(new Label("Income Tax :"),"top:50.0px;left:445.0px;");
		mainLayout.addComponent(txtIncomeTaxLess,"top:48.0px;left:570.0px;");
		txtIncomeTaxLess.setVisible(true);

		mainLayout.addComponent(new Label("Provident Fund :"),"top:75.0px;left:445.0px;");
		mainLayout.addComponent(txtProvidentFundLess,"top:73.0px;left:570.0px;");
		
		mainLayout.addComponent(new Label("Payment Type : "),"top:100px; left:445px");
		mainLayout.addComponent(opgBank,"top:100px; left:570px");
				
		mainLayout.addComponent(lblBankName ,"top:125.0px;left:445.0px;");
		mainLayout.addComponent(cmbBankName,"top:123.0px;left:570.0px;");		
		mainLayout.addComponent(btnPlusBank, "top:123.0px;left:775.0px;");

		mainLayout.addComponent(lblBranchName,"top:150.0px;left:445.0px;");
		mainLayout.addComponent(cmbBranchName,"top:148.0px;left:570.0px;");	
		mainLayout.addComponent(btnPlusBranch, "top:148.0px;left:775.0px;");
		
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