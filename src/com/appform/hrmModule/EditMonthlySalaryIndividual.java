package com.appform.hrmModule;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.AmountField;
import com.common.share.CommaSeparator;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.MessageBox.EventListener;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.TimeField;
import com.common.share.MessageBox.ButtonType;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class EditMonthlySalaryIndividual extends Window
{

	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblHead= new Label();
	private ComboBox cmbSection;
	private Label lblEmployee;
	private ComboBox cmbEmployee;

	private TextRead txtTotalDays    = new TextRead();
	private AmountField txtHoliDays     = new AmountField();
	private TextRead txtWorkDays     = new TextRead();
	private TextRead txtAbsentAmt     = new TextRead();
	private TextRead txtBasic         = new TextRead();
	private TextRead txtHouseRent     = new TextRead();
	private TextRead txtOtherAll      = new TextRead();
	private TextRead txtOtAmount      = new TextRead();
	private TextRead txtRevenueStamp   = new TextRead();

	private AmountField txtPresentDays   = new AmountField();
	private AmountField txtLeaveDays  = new AmountField();
	private AmountField txtTourDays   = new AmountField();
	private AmountField txtOffDays    = new AmountField();
	private AmountField txtAbsentDays = new AmountField();
	private AmountField txtLoanDeduction  = new AmountField();
	private AmountField txtInsurance  = new AmountField();
	private AmountField txtIncomeTax  = new AmountField();

	private TimeField txtOtHour        = new TimeField();
	private TimeField txtOtMinute      = new TimeField();
	private TimeField txtOtSec         = new TimeField();

	private PopupDateField dWorkingDate;
	private OptionGroup RadioEmployee;
	double GrossAmt = 0.0;
	String Notify = "";
	
	ArrayList<Component> allComp = new ArrayList<Component>();

	CommonButton button = new CommonButton("New", "Save", "", "","Refresh","","","","","Exit");
	private static final List<String> Optiontype=Arrays.asList(new String[]
			{"Employee ID","Proximity ID","Finger ID","Employee Name"});

	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
	public DecimalFormat df = new DecimalFormat("#0.00"); 
	private CommaSeparator Comma = new CommaSeparator();

	boolean Click = true;

	public EditMonthlySalaryIndividual(SessionBean sessionBean) 
	{
		this.sessionBean=sessionBean;
		this.setResizable(false);
		this.setCaption("EDIT MONTHLY SALARY :: " + sessionBean.getCompany());

		buildMainLayout();
		setContent(mainLayout);
		buttonAction();
		componentIni(true);
		btnIni(true);
		focusMove();
		authenticationCheck();
		departMentdataAdd();
	}

	private void componentIni(boolean b) 
	{
		dWorkingDate.setEnabled(!b);
		cmbSection.setEnabled(!b);
		RadioEmployee.setEnabled(!b);
		cmbEmployee.setEnabled(!b);
		txtTotalDays.setEnabled(!b);
		txtTourDays.setEnabled(!b);
		txtLeaveDays.setEnabled(!b);
		txtLoanDeduction.setEnabled(!b);
		txtWorkDays.setEnabled(!b);
		txtAbsentAmt.setEnabled(!b);
		txtAbsentDays.setEnabled(!b);
		txtPresentDays.setEnabled(!b);
		txtHoliDays.setEnabled(!b);
		txtHouseRent.setEnabled(!b);
		txtOffDays.setEnabled(!b);
		txtOtAmount.setEnabled(!b);
		txtOtherAll.setEnabled(!b);
		txtOtHour.setEnabled(!b);
		txtOtMinute.setEnabled(!b);
		txtOtSec.setEnabled(!b);
		txtBasic.setEnabled(!b);
		txtInsurance.setEnabled(!b);
		txtIncomeTax.setEnabled(!b);
		txtRevenueStamp.setEnabled(!b);
	}

	private void btnIni(boolean t)
	{
		button.btnNew.setEnabled(t);
		button.btnSave.setEnabled(!t);
		button.btnEdit.setEnabled(t);
		button.btnRefresh.setEnabled(!t);
		button.btnDelete.setEnabled(t);
		button.btnFind.setEnabled(t);;
	}

	public void focusMove()
	{
		allComp.add(dWorkingDate);
		allComp.add(cmbSection);
		allComp.add(cmbEmployee);
		allComp.add(txtLeaveDays);
		allComp.add(txtAbsentDays);
		allComp.add(txtTourDays);
		allComp.add(button.btnSave);
		allComp.add(dWorkingDate);

		new FocusMoveByEnter(this,allComp);
	}

	private void authenticationCheck()
	{
		if(!sessionBean.isSubmitable())
		{
			button.btnSave.setVisible(false);
		}
	}

	private void buttonAction()
	{
		dWorkingDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSection.removeAllItems();
				if(dWorkingDate.getValue()!=null)
				{
					departMentdataAdd();
				}
			}
		});

		cmbSection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmployee.removeAllItems();
				if(cmbSection.getValue()!=null)
				{
					cmbEmployeeDataAdd();
				}
			}
		});

		RadioEmployee.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmployee.removeAllItems();
				if(cmbSection.getValue()!=null)
				{
					cmbEmployeeDataAdd();
				}
			}
		});

		cmbEmployee.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				txtValueClear();
				if(cmbEmployee.getValue()!=null)
				{
					SetMonthlySalary();
				}
			}
		});

		txtOtHour.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtAbsentDays.getValue().toString().trim().isEmpty() && !txtLeaveDays.getValue().toString().trim().isEmpty()
						&& !txtTourDays.getValue().toString().trim().isEmpty() && !txtPresentDays.getValue().toString().trim().isEmpty()
						&& !txtOtHour.getValue().toString().trim().isEmpty() && !txtOtMinute.getValue().toString().trim().isEmpty())
				{
					commonCalculationMethod("");
				}
			}
		});

		txtOtMinute.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtAbsentDays.getValue().toString().trim().isEmpty() && !txtLeaveDays.getValue().toString().trim().isEmpty()
						&& !txtTourDays.getValue().toString().trim().isEmpty() && !txtPresentDays.getValue().toString().trim().isEmpty()
						&& !txtOtHour.getValue().toString().trim().isEmpty() && !txtOtMinute.getValue().toString().trim().isEmpty())
				{
					commonCalculationMethod("");
				}
			}
		});

		txtLeaveDays.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtAbsentDays.getValue().toString().trim().isEmpty() && !txtLeaveDays.getValue().toString().trim().isEmpty()
						&& !txtTourDays.getValue().toString().trim().isEmpty() && !txtPresentDays.getValue().toString().trim().isEmpty()
						&& !txtOtHour.getValue().toString().trim().isEmpty() && !txtOtMinute.getValue().toString().trim().isEmpty())
				{
					commonCalculationMethod("LD");
				}
			}
		});

		txtTourDays.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtAbsentDays.getValue().toString().trim().isEmpty() && !txtLeaveDays.getValue().toString().trim().isEmpty()
						&& !txtTourDays.getValue().toString().trim().isEmpty() && !txtPresentDays.getValue().toString().trim().isEmpty()
						&& !txtOtHour.getValue().toString().trim().isEmpty() && !txtOtMinute.getValue().toString().trim().isEmpty())
				{
					commonCalculationMethod("TD");
				}
			}
		});

		txtAbsentDays.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtAbsentDays.getValue().toString().trim().isEmpty() && !txtLeaveDays.getValue().toString().trim().isEmpty()
						&& !txtTourDays.getValue().toString().trim().isEmpty() && !txtPresentDays.getValue().toString().trim().isEmpty()
						&& !txtOtHour.getValue().toString().trim().isEmpty() && !txtOtMinute.getValue().toString().trim().isEmpty())
				{
					commonCalculationMethod("AD");
				}
			}
		});

		txtPresentDays.addListener(new ValueChangeListener()
		{	
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtAbsentDays.getValue().toString().trim().isEmpty() && !txtLeaveDays.getValue().toString().trim().isEmpty()
						&& !txtTourDays.getValue().toString().trim().isEmpty() && !txtPresentDays.getValue().toString().trim().isEmpty()
						&& !txtOtHour.getValue().toString().trim().isEmpty() && !txtOtMinute.getValue().toString().trim().isEmpty())
				{
					commonCalculationMethod("PD");
				}
			}
		});

		txtHoliDays.addListener(new ValueChangeListener()
		{	
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtAbsentDays.getValue().toString().trim().isEmpty() && !txtLeaveDays.getValue().toString().trim().isEmpty()
						&& !txtTourDays.getValue().toString().trim().isEmpty() && !txtPresentDays.getValue().toString().trim().isEmpty()
						&& !txtOtHour.getValue().toString().trim().isEmpty() && !txtOtMinute.getValue().toString().trim().isEmpty() 
						&& !txtHoliDays.getValue().toString().trim().isEmpty())
				{
					txtWorkDays.setValue(Integer.toString(Integer.parseInt(txtTotalDays.getValue().toString().trim())-Integer.parseInt(txtHoliDays.getValue().toString().trim())));
					txtAbsentDays.setValue(Integer.toString(Integer.parseInt(txtWorkDays.getValue().toString().trim())-Integer.parseInt(txtPresentDays.getValue().toString().trim())-Integer.parseInt(txtLeaveDays.getValue().toString().trim())));
				}
			}
		});

		button.btnNew.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				newButtonEvent();
			}
		});

		button.btnSave.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				saveButtonEvent();
			}
		});

		button.btnRefresh.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				refreshButtonEvent();
			}
		});

		button.btnExit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});
	}

	private void departMentdataAdd()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query ="select Distinct vSectionId,vSectionName from tbMonthlySalary where " +
					"MONTH(dSalaryDate)=MONTH('"+dFormat.format(dWorkingDate.getValue())+"') and " +
					"Year(dSalaryDate)=Year('"+dFormat.format(dWorkingDate.getValue())+"') order by vSectionName";
			System.out.println(query);
			List <?> list=session.createSQLQuery(query).list();

			if(!list.isEmpty())
			{
				for (Iterator <?> iter = list.iterator(); iter.hasNext();)
				{
					Object[] element =  (Object[]) iter.next();	
					cmbSection.addItem(element[0]);
					cmbSection.setItemCaption(element[0], element[1].toString());	
				}
			}
			else
			{
				showNotification("Warning", "Generate Salary At First!!!", Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception ex)
		{
			showNotification("departMentdataAdd", ex.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}

	private void cmbEmployeeDataAdd()
	{
		String sql="select vEmployeeID,vEmployeeCode from tbMonthlySalary " +
				"where vSectionId='"+cmbSection.getValue().toString()+"' order by vFingerID";
		lblEmployee.setValue("Employee ID : ");

		if(RadioEmployee.getValue()=="Proximity ID")
		{
			sql="select vEmployeeID,vProximityID from tbMonthlySalary " +
					"where vSectionId='"+cmbSection.getValue().toString()+"' order by vFingerID";
			lblEmployee.setValue("Proximity ID : ");
		}

		else if(RadioEmployee.getValue()=="Finger ID")
		{
			sql="select vEmployeeID,vFingerID from tbMonthlySalary " +
					"where vSectionId='"+cmbSection.getValue().toString()+"' order by vFingerID";
			lblEmployee.setValue("Finger ID : ");
		}

		else if(RadioEmployee.getValue()=="Employee Name")
		{
			sql="select vEmployeeID,vEmployeeName from tbMonthlySalary " +
					"where vSectionId='"+cmbSection.getValue().toString()+"' order by vFingerID";
			lblEmployee.setValue("Employee Name : ");
		}
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> lst=session.createSQLQuery(sql).list();

			if(!lst.isEmpty())
			{
				Iterator <?> itr=lst.iterator();
				while(itr.hasNext())
				{
					Object[] element=(Object[])itr.next();
					cmbEmployee.addItem(element[0]);
					cmbEmployee.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("RadioBtnGroup", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}

	private void SetMonthlySalary()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select iTotalMonthDays,iTotalHoliday,iTotalLeaveDays,iTotalTourDays,iTotalPresentDays," +
					"iTotalAbsentDays,mBasic,mHouseRent,mOtherAllowance,iOTHour,iOTMin,mAdvanceSalary,mRevenueStamp," +
					"mIncomeTax,mInsurance,mGross from tbMonthlySalary where MONTH(dSalaryDate)=MONTH('"+dFormat.format(dWorkingDate.getValue())+"') " +
					"and year(dSalaryDate)=year('"+dFormat.format(dWorkingDate.getValue())+"') and " +
					"vSectionId='"+cmbSection.getValue()+"' and vEmployeeId='"+cmbEmployee.getValue()+"'";
			System.out.println(query);
			List <?> list=session.createSQLQuery(query).list();

			for(Iterator <?> iter=list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				GrossAmt = Double.parseDouble(element[15].toString());

				String HH=element[9].toString();
				String MM=element[10].toString();
				String SS="00";

				if(HH.length()<2){ txtOtHour.setValue("0"+HH); }  else { txtOtHour.setValue(HH); }
				if(MM.length()<2){ txtOtMinute.setValue("0"+MM);} else { txtOtMinute.setValue(MM); }
				if(SS.length()<2){ txtOtSec.setValue("0"+SS);} else { txtOtSec.setValue(SS); }

				txtTotalDays.setValue(element[0].toString());
				txtHoliDays.setValue(element[1].toString());
				txtWorkDays.setValue(Integer.parseInt(element[0].toString())-Integer.parseInt(element[1].toString()));
				txtLeaveDays.setValue(element[2].toString());
				txtTourDays.setValue(element[3].toString());
				txtAbsentDays.setValue(element[5].toString());
				txtPresentDays.setValue(element[4].toString());
				txtBasic.setValue(df.format(Double.parseDouble(element[6].toString())));
				txtHouseRent.setValue(df.format(Double.parseDouble(element[7].toString())));
				txtOtherAll.setValue(df.format(Double.parseDouble(element[8].toString())));
				txtLoanDeduction.setValue(df.format(Double.parseDouble(element[11].toString())));
				txtRevenueStamp.setValue(df.format(Double.parseDouble(element[12].toString())));
				txtIncomeTax.setValue(df.format(Double.parseDouble(element[13].toString())));
				txtInsurance.setValue(df.format(Double.parseDouble(element[14].toString())));
			}
		}
		catch(Exception exp)
		{
			showNotification("SetMonthlySalary",exp+"",Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}

	private void commonCalculationMethod(String flag)
	{
		int workingDay = Integer.parseInt(txtWorkDays.getValue().toString().trim());
		int leaveDay = Integer.parseInt(txtLeaveDays.getValue().toString().trim());
		int tourDay = Integer.parseInt(txtTourDays.getValue().toString().trim());
		int absentDay = 0;
		int presentDay = 0;

		if(flag == "LD" || flag == "TD" || flag == "PD")
		{
			presentDay = Integer.parseInt(txtPresentDays.getValue().toString().trim());
			absentDay = workingDay-(presentDay+leaveDay+tourDay);
			txtAbsentDays.setValue(Integer.toString(absentDay));
			txtAbsentAmt.setValue(Comma.setComma(absentDay*(GrossAmt/Double.parseDouble(txtTotalDays.getValue().toString().trim()))));
		}

		else if(flag == "AD")
		{
			absentDay = Integer.parseInt(txtAbsentDays.getValue().toString().trim());
			presentDay = workingDay-(absentDay+leaveDay+tourDay);
			txtPresentDays.setValue(Integer.toString(presentDay));
			txtAbsentAmt.setValue(Comma.setComma(absentDay*(GrossAmt/Double.parseDouble(txtTotalDays.getValue().toString().trim()))));
		}

		System.out.println("workingDay "+workingDay+" leaveDay "+leaveDay+" tourDay "+tourDay+" absentDay "+absentDay+" presentDay "+presentDay);

		int otHour = Integer.parseInt(txtOtHour.getValue().toString().trim());
		int otMin = Integer.parseInt(txtOtMinute.getValue().toString().trim());

		txtOtAmount.setValue(Comma.setComma((otHour*(GrossAmt/240))+(otMin*(GrossAmt/14400))));
	}

	private void txtClear()
	{
		cmbSection.setValue(null);
		cmbEmployee.setValue(null);
		txtValueClear();
	}

	private void txtValueClear()
	{
		txtTotalDays.setValue("");
		txtTourDays.setValue("");
		txtLeaveDays.setValue("");
		txtLoanDeduction.setValue("");
		txtWorkDays.setValue("");
		txtAbsentAmt.setValue("");
		txtAbsentDays.setValue("");
		txtPresentDays.setValue("");
		txtHoliDays.setValue("");
		txtHouseRent.setValue("");
		txtOffDays.setValue("");
		txtOtAmount.setValue("");
		txtOtherAll.setValue("");
		txtOtHour.setValue("");
		txtOtMinute.setValue("");
		txtOtSec.setValue("");
		txtBasic.setValue("");
		txtInsurance.setValue("");
		txtIncomeTax.setValue("");
		txtRevenueStamp.setValue("");
	}

	private void newButtonEvent() 
	{
		dWorkingDate.focus();
		componentIni(false);
		btnIni(false);
		txtClear();
	}

	private void saveButtonEvent()
	{
		if(formValidation())
		{
			MessageBox mb = new MessageBox(getParent(), "Are You Sure?", MessageBox.Icon.QUESTION, "Do You Want to Save Information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{	
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						insertData();
					}
				}
			});
		}
		else
			showNotification("Warning", Notify, Notification.TYPE_WARNING_MESSAGE);
	}

	private boolean formValidation()
	{
		if(!txtTotalDays.getValue().toString().trim().isEmpty())
		{
			if(!txtHoliDays.getValue().toString().trim().isEmpty())
			{
				if(!txtWorkDays.getValue().toString().trim().isEmpty())
				{
					if(!txtLeaveDays.getValue().toString().trim().isEmpty())
					{
						if(!txtTourDays.getValue().toString().trim().isEmpty())
						{
							if(!txtAbsentDays.getValue().toString().trim().isEmpty())
							{
								if(!txtPresentDays.getValue().toString().trim().isEmpty())
								{
									return true;
								}
								else
								{
									txtPresentDays.focus();
									Notify = "Provide Present Days!!!";
								}
							}
							else
							{
								txtAbsentDays.focus();
								Notify = "Provide Absent Days!!!";
							}
						}
						else
						{
							txtTourDays.focus();
							Notify = "Provide Tour Days!!!";
						}
					}
					else
					{
						txtLeaveDays.focus();
						Notify = "Provide Leave Days!!!";
					}
				}
			}
		}
		return false;
	}

	private void insertData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{
			String updateQuery = "insert into tbUDMonthlySalary (dGenerateDate,dSalaryDate,vMonthName,vYear," +
					"vEmployeeID,vEmployeeCode,vFingerId,vProximityID,vEmployeeName,vDesignationID," +
					"vDesignationName,vSectionID,vSectionName,iTotalMonthDays,iTotalHoliday,iTotalPresentDays," +
					"iTotalAbsentDays,iTotalLeaveDays,iTotalTourDays,mGross,mBasic,mHouseRent,mMedicalAllowance," +
					"mConveyance,mMobileBill,mOtherAllowance,mProvidentFund,iOTHour,iOTMin,vLoanNo," +
					"vTransactionNo,mAdvanceSalary,mRevenueStamp,vBankID,vBankName,vBranchID,vBranchName," +
					"vAccountNo,mIncomeTax,mInsurance,vUserIP,vUserName,dEntryTime,vEmployeeType,UDFlag) " +
					"select dGenerateDate,dSalaryDate,vMonthName,vYear,vEmployeeID,vEmployeeCode,vFingerId," +
					"vProximityID,vEmployeeName,vDesignationID,vDesignationName,vSectionID,vSectionName," +
					"iTotalMonthDays,iTotalHoliday,iTotalPresentDays,iTotalAbsentDays,iTotalLeaveDays," +
					"iTotalTourDays,mGross,mBasic,mHouseRent,mMedicalAllowance,mConveyance,mMobileBill," +
					"mOtherAllowance,mProvidentFund,iOTHour,iOTMin,vLoanNo,vTransactionNo,mAdvanceSalary," +
					"mRevenueStamp,vBankID,vBankName,vBranchID,vBranchName,vAccountNo,mIncomeTax,mInsurance," +
					"vUserIP,vUserName,dEntryTime,vEmployeeType,'OLD' from tbMonthlySalary where " +
					"vEmployeeID = '"+cmbEmployee.getValue()+"'";
			
			String query = "update tbMonthlySalary set iTotalPresentDays='"+txtPresentDays.getValue().toString().trim()+"'," +
					"iTotalAbsentDays='"+txtAbsentDays.getValue().toString().trim()+"'," +
					"iTotalLeaveDays='"+txtLeaveDays.getValue().toString().trim()+"'," +
					"iTotalTourDays='"+txtTourDays.getValue().toString().trim()+"'," +
					"iOTHour='"+txtOtHour.getValue().toString().trim()+"'," +
					"iOTMin='"+txtOtMinute.getValue().toString().trim()+"'," +
					"mAdvanceSalary='"+txtLoanDeduction.getValue().toString().trim()+"'," +
					"mIncomeTax='"+txtIncomeTax.getValue().toString().trim()+"'," +
					"mInsurance='"+txtInsurance.getValue().toString().trim()+"'," +
					"vUserIP='"+sessionBean.getUserIp()+"'," +
					"vUserName='"+sessionBean.getUserName()+"'," +
					"dEntryTime=GETDATE() where vEmployeeId = '"+cmbEmployee.getValue()+"'";
			
			session.createSQLQuery(updateQuery).executeUpdate();
			session.createSQLQuery(query).executeUpdate();
			tx.commit();
			txtClear();
			componentIni(true);
			btnIni(true);
			showNotification("All Information Saved Successfully");
		}
		catch (Exception exp)
		{
			tx.rollback();
			showNotification("insertData", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}

	private void refreshButtonEvent() 
	{
		componentIni(true);
		btnIni(true);
		txtClear();	
	}

	private AbsoluteLayout buildMainLayout() 
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);
		mainLayout.setWidth("800px");
		mainLayout.setHeight("470px");

		dWorkingDate = new PopupDateField();
		dWorkingDate.setImmediate(true);
		dWorkingDate.setWidth("140px");
		dWorkingDate.setDateFormat("MMMMM-yyyy");
		dWorkingDate.setValue(new java.util.Date());
		dWorkingDate.setResolution(PopupDateField.RESOLUTION_MONTH);
		mainLayout.addComponent(new Label("Date : "), "top:20.0px;left:30.0px;");
		mainLayout.addComponent(dWorkingDate, "top:18.0px; left:120.0px;");

		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("220px");
		cmbSection.setHeight("24px");
		cmbSection.setInputPrompt("Section Name");
		mainLayout.addComponent(new Label("Section Name : "), "top:45.0px;left:30.0px;");
		mainLayout.addComponent(cmbSection, "top:43.0px; left:120.0px;");

		RadioEmployee=new OptionGroup("",Optiontype);
		RadioEmployee.setImmediate(true);
		RadioEmployee.setStyleName("horizontal");
		RadioEmployee.select("Employee ID");
		mainLayout.addComponent(RadioEmployee, "top:20.0px;left:400.0px;");

		lblEmployee = new Label("Employee ID : ");
		mainLayout.addComponent(lblEmployee, "top:45.0px; left:400.0px;");

		cmbEmployee = new ComboBox();
		cmbEmployee.setImmediate(true);
		cmbEmployee.setWidth("220px");
		cmbEmployee.setHeight("24px");
		cmbEmployee.setNullSelectionAllowed(true);
		cmbEmployee.setInputPrompt("Employee Name");
		mainLayout.addComponent(cmbEmployee, "top:43.0px; left:500.0px;");

		lblHead = new Label("<font color='#0000f0'><b><Strong>Attendance Details<Strong></b></font>");
		lblHead.setImmediate(true);
		lblHead.setWidth("-1px");
		lblHead.setHeight("-1px");
		lblHead.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblHead, "top:120px;left:300px;");

		txtTotalDays = new TextRead(1);
		txtTotalDays.setImmediate(true);
		txtTotalDays.setWidth("30px");
		txtTotalDays.setHeight("17px");
		mainLayout.addComponent(new Label("Total Days : "), "top:145.0px;left:20.0px;");
		mainLayout.addComponent(txtTotalDays, "top:146.0px; left:115.0px;");

		txtHoliDays = new AmountField();
		txtHoliDays.setImmediate(true);
		txtHoliDays.setWidth("28px");
		txtHoliDays.setHeight("17px");
		mainLayout.addComponent(new Label("Total Holidays : "), "top:170.0px;left:20.0px;");
		mainLayout.addComponent(txtHoliDays, "top:171.0px; left:115.0px;");

		txtWorkDays = new TextRead(1);
		txtWorkDays.setImmediate(true);
		txtWorkDays.setWidth("28px");
		txtWorkDays.setHeight("17px");
		mainLayout.addComponent(new Label("Total Working Days : "), "top:145.0px;left:160.0px;");
		mainLayout.addComponent(txtWorkDays, "top:146.0px; left:280.0px;");

		txtLeaveDays = new AmountField();
		txtLeaveDays.setImmediate(true);
		txtLeaveDays.setWidth("29px");
		txtLeaveDays.setHeight("17px");
		txtLeaveDays.setMaxLength(2);
		mainLayout.addComponent(new Label("Total Leave days : "), "top:170.0px;left:160.0px;");
		mainLayout.addComponent(txtLeaveDays, "top:171.0px; left:280.0px;");

		txtTourDays = new AmountField();
		txtTourDays.setImmediate(true);
		txtTourDays.setWidth("29px");
		txtTourDays.setHeight("17px");
		txtTourDays.setMaxLength(2);
		mainLayout.addComponent(new Label("Total Tour Days : "), "top:145.0px;left:320.0px;");
		mainLayout.addComponent(txtTourDays, "top:146.0px; left:430.0px;");

		txtAbsentDays = new AmountField();
		txtAbsentDays.setImmediate(true);
		txtAbsentDays.setWidth("29px");
		txtAbsentDays.setHeight("17px");
		txtAbsentDays.setMaxLength(2);
		mainLayout.addComponent(new Label("Total Absent Days : "), "top:170.0px;left:320.0px;");
		mainLayout.addComponent(txtAbsentDays, "top:171.0px; left:430.0px;");

		txtPresentDays = new AmountField();
		txtPresentDays.setImmediate(true);
		txtPresentDays.setWidth("28px");
		txtPresentDays.setHeight("17px");
		mainLayout.addComponent(new Label("Total Present days : "), "top:145.0px;left:480.0px;");
		mainLayout.addComponent(txtPresentDays, "top:146.0px; left:600.0px;");

		lblHead = new Label("<font color='#0000f0'><b><Strong>Earning Details<Strong></b></font>");
		lblHead.setImmediate(true);
		lblHead.setWidth("-1px");
		lblHead.setHeight("-1px");
		lblHead.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblHead, "top:220px;left:90.0px;");

		txtBasic = new TextRead(1);
		txtBasic.setImmediate(true);
		txtBasic.setWidth("120px");
		txtBasic.setHeight("22px");
		mainLayout.addComponent(new Label("Basic :"), "top:245.0px;left:20.0px;");
		mainLayout.addComponent(txtBasic, "top:243.0px; left:130.0px;");

		txtHouseRent = new TextRead(1);
		txtHouseRent.setImmediate(true);
		txtHouseRent.setWidth("120px");
		txtHouseRent.setHeight("22px");
		mainLayout.addComponent(new Label("House Rent :"), "top:270.0px;left:20.0px;");
		mainLayout.addComponent(txtHouseRent, "top:268.0px; left:130.0px;");

		txtOtherAll = new TextRead(1);
		txtOtherAll.setImmediate(true);
		txtOtherAll.setWidth("120px");
		txtOtherAll.setHeight("22px");
		mainLayout.addComponent(new Label("Other Allowance :"), "top:295.0px;left:20.0px;");
		mainLayout.addComponent(txtOtherAll, "top:293.0px; left:130.0px;");

		txtOtHour = new TimeField();
		txtOtHour.setImmediate(true);
		txtOtHour.setWidth("35px");
		txtOtHour.setHeight("17px");                                    
		txtOtHour.setStyleName("time");
		txtOtHour.setInputPrompt("HH");
		mainLayout.addComponent(new Label("Total OT :"), "top:320.0px;left:20.0px;");
		mainLayout.addComponent(txtOtHour, "top:320.0px;left:131.0px;");
		mainLayout.addComponent(new Label("<b><Strong>:<Strong></b>",Label.CONTENT_XHTML), "top:320.0px;left:166.0px;");

		txtOtMinute = new TimeField();
		txtOtMinute.setImmediate(true);
		txtOtMinute.setWidth("35px");
		txtOtMinute.setHeight("17px");
		txtOtMinute.setStyleName("time");
		txtOtMinute.setInputPrompt("MM");
		mainLayout.addComponent(txtOtMinute, "top:320.0px;left:171.0px;");
		mainLayout.addComponent(new Label("<b><Strong>:<Strong></b>",Label.CONTENT_XHTML), "top:320.0px;left:206.0px;");

		txtOtSec = new TimeField();
		txtOtSec.setImmediate(true);
		txtOtSec.setWidth("35px");
		txtOtSec.setHeight("17px");
		txtOtSec.setStyleName("time");
		txtOtSec.setInputPrompt("SS");
		mainLayout.addComponent(txtOtSec, "top:320.0px;left:211.0px;");

		txtOtAmount = new TextRead(1);
		txtOtAmount.setImmediate(true);
		txtOtAmount.setWidth("120px");
		txtOtAmount.setHeight("22px");
		mainLayout.addComponent(new Label("Total OT Amount :"), "top:345.0px;left:20.0px;");
		mainLayout.addComponent(txtOtAmount, "top:343.0px; left:130.0px;");

		lblHead = new Label("<font color='#0000f0'><b><Strong>Deduction Details<Strong></b></font>");
		lblHead.setImmediate(true);
		lblHead.setWidth("-1px");
		lblHead.setHeight("-1px");
		lblHead.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblHead, "top:220px;left:370.0px;");

		txtAbsentAmt = new TextRead(1);
		txtAbsentAmt.setImmediate(true);
		txtAbsentAmt.setWidth("120px");
		txtAbsentAmt.setHeight("22px");
		mainLayout.addComponent(new Label("Absent Amount :"), "top:245.0px;left:300.0px;");
		mainLayout.addComponent(txtAbsentAmt, "top:243.0px; left:430.0px;");

		txtLoanDeduction = new AmountField();
		txtLoanDeduction.setImmediate(true);
		txtLoanDeduction.setWidth("120px");
		txtLoanDeduction.setHeight("22px");
		mainLayout.addComponent(new Label("Loan Amount :"), "top:270.0px;left:300.0px;");
		mainLayout.addComponent(txtLoanDeduction, "top:268.0px; left:430.0px;");

		txtInsurance = new AmountField();
		txtInsurance.setImmediate(true);
		txtInsurance.setWidth("120px");
		txtInsurance.setHeight("22px");
		mainLayout.addComponent(new Label("Insurance :"), "top:295.0px;left:300.0px;");
		mainLayout.addComponent(txtInsurance, "top:293.0px; left:430.0px;");

		txtIncomeTax = new AmountField();
		txtIncomeTax.setImmediate(true);
		txtIncomeTax.setWidth("120px");
		txtIncomeTax.setHeight("22px");
		mainLayout.addComponent(new Label("Income Tax :"), "top:320.0px;left:300.0px;");
		mainLayout.addComponent(txtIncomeTax, "top:318.0px; left:430.0px;");

		txtRevenueStamp = new TextRead(1);
		txtRevenueStamp.setImmediate(true);
		txtRevenueStamp.setWidth("120px");
		txtRevenueStamp.setHeight("22px");
		mainLayout.addComponent(new Label("Revenue Stamp :"), "top:345.0px;left:300.0px;");
		mainLayout.addComponent(txtRevenueStamp, "top:343.0px; left:430.0px;");

		mainLayout.addComponent(button,"top:415.0px; left:210.0px");
		return mainLayout;
	}
}
