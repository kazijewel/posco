package com.appform.hrmModule;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.AmountCommaSeperator;
import com.common.share.CommaSeparator;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Button.ClickShortcut;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class SalaryRegisterInformation extends Window 
{
	CommonButton cButton = new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "","","Exit");

	private TextField txtFindId = new TextField();
	private TextField txtIdBack = new TextField();

	boolean isUpdate = false;
	boolean isNew = false;

	private AbsoluteLayout mainLayout;

	private Label lblCommon;

	private TextRead txtRegisterId;
	private TextField txtRegisterName;

	private AmountCommaSeperator txtBasicAdd;
	private AmountCommaSeperator txtHouseRentAdd;
	private AmountCommaSeperator txtMedicalAllowanceAdd;
	private AmountCommaSeperator txtClinicalAllowanceAdd;
	private AmountCommaSeperator txtNonPracticeAllowanceAdd;
	private AmountCommaSeperator txtSpecialAllowanceAdd;
	private AmountCommaSeperator txtOtherAllowanceAdd;
	private AmountCommaSeperator txtDearnessAllowanceAdd;
	private AmountCommaSeperator txtConveyanceAllowanceAdd;
	private AmountCommaSeperator txtAttendanceBonusAdd;
	private AmountCommaSeperator txtTiffinAllowanceAdd;

	private AmountCommaSeperator txtRoomChargeLess;
	private AmountCommaSeperator txtIncomeTaxLess;
	private AmountCommaSeperator txtProvidentFundLess;
	private AmountCommaSeperator txtKallanFundLess;
	private AmountCommaSeperator txtKhichuriMealLess;

	private Label lblIsActive;
	private NativeSelect cmbIsActive;

	SessionBean sessionBean;

	ArrayList<Component> allComp = new ArrayList<Component>();
	private static final String[] status = new String[] { "Inactive", "Active" };

	private boolean isFind = false;

	private DecimalFormat df = new DecimalFormat("#0.00");
	private DecimalFormat dff = new DecimalFormat("#0");

	private TextRead txtGrossSalary;
	public TextRead txtTotalDeduction;

	public SalaryRegisterInformation(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setResizable(false);
		this.setCaption("SALARY STRUCTURE INFORMATION :: "+sessionBean.getCompany());

		buildMainLayout();
		setContent(mainLayout);

		txtInit(true);
		btnIni(true);

		focusEnter();
		btnAction();

		authenticationCheck();
		setButtonShortCut();

		cButton.btnNew.focus();
	}

	private void setButtonShortCut()
	{
		this.addAction(new ClickShortcut(cButton.btnSave, KeyCode.S, ModifierKey.ALT));
		this.addAction(new ClickShortcut(cButton.btnNew, KeyCode.N, ModifierKey.ALT));
		this.addAction(new ClickShortcut(cButton.btnRefresh, KeyCode.R, ModifierKey.ALT));
	}

	private void authenticationCheck()
	{
		if(!sessionBean.isSubmitable())
		{
			cButton.btnSave.setVisible(false);
		}
		if(!sessionBean.isUpdateable())
		{
			cButton.btnEdit.setVisible(false);
		}
		if(!sessionBean.isDeleteable())
		{
			cButton.btnDelete.setVisible(false);
		}
	}

	public void btnAction()
	{
		cButton.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isFind = false;
				isUpdate = false;

				txtInit(false);
				btnIni(false);
				txtClear();

				txtRegisterId.setValue(selectMaxId());
				txtRegisterName.focus();
			}
		});

		cButton.btnSave.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				formValidation();
			}
		});

		cButton.btnEdit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isFind = false;
				updateButtonEvent();
			}
		});

		cButton.btnRefresh.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isFind = false;
				isUpdate=false;
				refreshButtonEvent();
			}
		});

		cButton.btnFind.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isFind = true;
				findButtonEvent();
			}
		});

		cButton.btnExit.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				close();
			}
		});

		txtRegisterName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtRegisterName.getValue().toString().isEmpty())
				{
					if(duplicateMainGroup() && !isFind)
					{
						showNotification("Warning!","Structure name already exist.",Notification.TYPE_WARNING_MESSAGE);
						txtRegisterName.setValue("");
						txtRegisterName.focus();
					}
				}
			}
		});

		txtBasicAdd.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				grossCalculation();
				houseRentCalculation();
			}
		});
		txtHouseRentAdd.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				grossCalculation();
			}
		});
		txtMedicalAllowanceAdd.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				grossCalculation();
			}
		});
		txtClinicalAllowanceAdd.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				grossCalculation();
			}
		});
		txtNonPracticeAllowanceAdd.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				grossCalculation();
			}
		});
		txtOtherAllowanceAdd.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				grossCalculation();
			}
		});
		txtDearnessAllowanceAdd.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				grossCalculation();
			}
		});
		txtConveyanceAllowanceAdd.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				grossCalculation();
			}
		});
		txtSpecialAllowanceAdd.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				grossCalculation();
			}
		});
		txtRoomChargeLess.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				grossCalculation();
			}
		});
		txtIncomeTaxLess.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				grossCalculation();
			}
		});
		txtProvidentFundLess.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				grossCalculation();
			}
		});
		txtKallanFundLess.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				grossCalculation();
			}
		});
		txtKhichuriMealLess.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				grossCalculation();
			}
		});
		txtTiffinAllowanceAdd.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				grossCalculation();
			}
		});
	}

	private void grossCalculation()
	{
		double basic = Double.parseDouble(txtBasicAdd.getValue().toString().isEmpty()?"0":txtBasicAdd.getValue().toString().replaceAll(",", "").trim());
		double house = Double.parseDouble(txtHouseRentAdd.getValue().toString().isEmpty()?"0":txtHouseRentAdd.getValue().toString().replaceAll(",", "").trim());
		double medical = Double.parseDouble(txtMedicalAllowanceAdd.getValue().toString().isEmpty()?"0":txtMedicalAllowanceAdd.getValue().toString().replaceAll(",", "").trim());
		double clinic = Double.parseDouble(txtClinicalAllowanceAdd.getValue().toString().isEmpty()?"0":txtClinicalAllowanceAdd.getValue().toString().replaceAll(",", "").trim());
		double nonPrac = Double.parseDouble(txtNonPracticeAllowanceAdd.getValue().toString().isEmpty()?"0":txtNonPracticeAllowanceAdd.getValue().toString().replaceAll(",", "").trim());
		double special = Double.parseDouble(txtSpecialAllowanceAdd.getValue().toString().isEmpty()?"0":txtSpecialAllowanceAdd.getValue().toString().replaceAll(",", "").trim());
		double other = Double.parseDouble(txtOtherAllowanceAdd.getValue().toString().isEmpty()?"0":txtOtherAllowanceAdd.getValue().toString().replaceAll(",", "").trim());
		double dearness = Double.parseDouble(txtDearnessAllowanceAdd.getValue().toString().isEmpty()?"0":txtDearnessAllowanceAdd.getValue().toString().replaceAll(",", "").trim());
		double convence = Double.parseDouble(txtConveyanceAllowanceAdd.getValue().toString().isEmpty()?"0":txtConveyanceAllowanceAdd.getValue().toString().replaceAll(",", "").trim());
		double attenBonus = Double.parseDouble(txtAttendanceBonusAdd.getValue().toString().isEmpty()?"0":txtAttendanceBonusAdd.getValue().toString().replaceAll(",", "").trim());
		double tiffin = Double.parseDouble(txtTiffinAllowanceAdd.getValue().toString().isEmpty()?"0":txtTiffinAllowanceAdd.getValue().toString().replaceAll(",", "").trim());

		double room = Double.parseDouble(txtRoomChargeLess.getValue().toString().isEmpty()?"0":txtRoomChargeLess.getValue().toString().replaceAll(",", "").trim());
		double income = Double.parseDouble(txtIncomeTaxLess.getValue().toString().isEmpty()?"0":txtIncomeTaxLess.getValue().toString().replaceAll(",", "").trim());
		//double provident = Double.parseDouble(txtProvidentFundLess.getValue().toString().isEmpty()?"0":txtProvidentFundLess.getValue().toString().replaceAll(",", "").trim());
		double kallan = Double.parseDouble(txtKallanFundLess.getValue().toString().isEmpty()?"0":txtKallanFundLess.getValue().toString().replaceAll(",", "").trim());
		double khichuri = Double.parseDouble(txtKhichuriMealLess.getValue().toString().isEmpty()?"0":txtKhichuriMealLess.getValue().toString().replaceAll(",", "").trim());

		double totalSalary = (basic+house+medical+clinic+nonPrac+special+other+dearness+convence+attenBonus+tiffin);
		double totalDeduction = (room+income+/*provident+*/kallan+khichuri);

		txtGrossSalary.setValue(new CommaSeparator().setComma(totalSalary));
		txtTotalDeduction.setValue(new CommaSeparator().setComma(totalDeduction));
	}

	public void houseRentCalculation()
	{
		double basic = Double.parseDouble(txtBasicAdd.getValue().toString().isEmpty()?"0":txtBasicAdd.getValue().toString().replaceAll(",", "").trim());

		txtHouseRentAdd.setValue(dff.format((60*basic)/100));
		txtConveyanceAllowanceAdd.setValue(dff.format((30*basic)/100));
	}

	private void formValidation()
	{
		if(!txtRegisterName.getValue().toString().isEmpty())
		{
			saveButtonEvent();
		}
		else
		{
			getParent().showNotification("Warning!","Provide structure name.", Notification.TYPE_WARNING_MESSAGE);
			txtRegisterName.focus();
		}
	}

	private boolean duplicateMainGroup()
	{
		boolean ret = false;
		if(!isUpdate)
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String query = "select * from tbSalaryRegisterInfo where vRegisterName" +
						" like '"+txtRegisterName.getValue().toString()+"'";

				Iterator <?> iter = session.createSQLQuery(query).list().iterator();

				if (iter.hasNext()) 
				{
					ret = true;
				}
			}
			catch (Exception ex) 
			{
				System.out.print(ex);
			}
			finally{session.close();}
		}
		return ret;
	}

	private void findButtonEvent()
	{
		Window win = new HrmSetupFindWindow(sessionBean, txtFindId, "SALARY STRUCTURE");
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if (txtFindId.getValue().toString().length() > 0)
				{
					txtClear();
					txtIdBack.setValue(txtFindId.getValue().toString());
					findInitialise(txtFindId.getValue().toString());
				}
			}
		});
		this.getParent().addWindow(win);
	}

	private String selectMaxId()
	{
		String maxId = "";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = " Select isnull(max(cast(SUBSTRING(vRegisterId,4,10) as int)),0)+1 from tbSalaryRegisterInfo ";

			Iterator <?> iter = session.createSQLQuery(query).list().iterator();

			if (iter.hasNext())
			{
				String srt = iter.next().toString();
				maxId = "REG"+srt;
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		finally{session.close();}
		return maxId;
	}

	private void findInitialise(String registerId) 
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "select vRegisterId,vRegisterName,mBasic,mHouseRent,mMedicalAllowance,mClinicalAllowance," +
					" mNonPracticeAllowance,mSpecialAllowance,mOtherAllowance,mDearnessAllowance," +
					" mConveyanceAllowance,mAttendanceBonus,mRoomCharge,mIncomeTax,mProvidentFund,mKallanFund," +
					" mKhichuriMeal,isActive,mTiffinAllowance from tbSalaryRegisterInfo Where vRegisterId = '"+registerId+"'";
			List <?> led = session.createSQLQuery(sql).list();

			if (led.iterator().hasNext()) 
			{
				Object[] element = (Object[]) led.iterator().next();

				txtRegisterId.setValue(element[0].toString());
				txtRegisterName.setValue(element[1].toString());
				txtBasicAdd.setValue(df.format(element[2]));
				txtHouseRentAdd.setValue(df.format(element[3]));
				txtMedicalAllowanceAdd.setValue(df.format(element[4]));
				txtClinicalAllowanceAdd.setValue(df.format(element[5]));
				txtNonPracticeAllowanceAdd.setValue(df.format(element[6]));
				txtSpecialAllowanceAdd.setValue(df.format(element[7]));
				txtOtherAllowanceAdd.setValue(df.format(element[8]));
				txtDearnessAllowanceAdd.setValue(df.format(element[9]));
				txtConveyanceAllowanceAdd.setValue(df.format(element[10]));
				txtAttendanceBonusAdd.setValue(df.format(element[11]));

				txtRoomChargeLess.setValue(df.format(element[12]));
				txtIncomeTaxLess.setValue(df.format(element[13]));
				txtProvidentFundLess.setValue(df.format(element[14]));
				txtKallanFundLess.setValue(df.format(element[15]));
				txtKhichuriMealLess.setValue(df.format(element[16]));

				cmbIsActive.setValue(element[17]);

				txtTiffinAllowanceAdd.setValue(df.format(element[18]));
			}
		}
		catch (Exception exp) 
		{
			this.getParent().showNotification("Error", exp + "",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void refreshButtonEvent() 
	{
		txtInit(true);
		btnIni(true);
		txtClear();
		isNew=false;
	}

	private void updateButtonEvent()
	{
		if(!txtRegisterId.getValue().toString().isEmpty())
		{
			isUpdate = true;
			isFind = false;
			btnIni(false);
			txtInit(false);
		}
		else
		{
			this.getParent().showNotification("Update Failed","There are no data for update.",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void saveButtonEvent()
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
						if(isUpdate)
						{
							updateData();
							btnIni(true);
							txtInit(true);
							txtClear();
							cButton.btnNew.focus();
						}
						isUpdate=false;
						isFind = false;
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
						btnIni(true);
						txtInit(true);
						txtClear();

						cButton.btnNew.focus();
					}
				}
			});
		}
	}

	private void insertData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();

		try
		{
			String Insert = " INSERT into tbSalaryRegisterInfo (vRegisterId,vRegisterName,mBasic,mHouseRent," +
					" mMedicalAllowance,mClinicalAllowance,mNonPracticeAllowance,mSpecialAllowance,mOtherAllowance," +
					" mDearnessAllowance,mConveyanceAllowance,mAttendanceBonus,mTiffinAllowance,mRoomCharge,mIncomeTax," +
					" mProvidentFund,mKallanFund,mKhichuriMeal,isActive,vUserName,vUserIp,dEntryTime) values(" +
					" '"+selectMaxId()+"'," +
					" '"+txtRegisterName.getValue().toString().trim()+"'," +
					" '"+(txtBasicAdd.getValue().toString().isEmpty()?"0":txtBasicAdd.getValue().toString().replaceAll(",", "").trim())+"'," +
					" '"+(txtHouseRentAdd.getValue().toString().isEmpty()?"0":txtHouseRentAdd.getValue().toString().replaceAll(",", "").trim())+"'," +
					" '"+(txtMedicalAllowanceAdd.getValue().toString().isEmpty()?"0":txtMedicalAllowanceAdd.getValue().toString().replaceAll(",", "").trim())+"'," +
					" '"+(txtClinicalAllowanceAdd.getValue().toString().isEmpty()?"0":txtClinicalAllowanceAdd.getValue().toString().replaceAll(",", "").trim())+"'," +
					" '"+(txtNonPracticeAllowanceAdd.getValue().toString().isEmpty()?"0":txtNonPracticeAllowanceAdd.getValue().toString().replaceAll(",", "").trim())+"'," +
					" '"+(txtSpecialAllowanceAdd.getValue().toString().isEmpty()?"0":txtSpecialAllowanceAdd.getValue().toString().replaceAll(",", "").trim())+"'," +
					" '"+(txtOtherAllowanceAdd.getValue().toString().isEmpty()?"0":txtOtherAllowanceAdd.getValue().toString().replaceAll(",", "").trim())+"'," +
					" '"+(txtDearnessAllowanceAdd.getValue().toString().isEmpty()?"0":txtDearnessAllowanceAdd.getValue().toString().replaceAll(",", "").trim())+"'," +
					" '"+(txtConveyanceAllowanceAdd.getValue().toString().isEmpty()?"0":txtConveyanceAllowanceAdd.getValue().toString().replaceAll(",", "").trim())+"'," +
					" '"+(txtAttendanceBonusAdd.getValue().toString().isEmpty()?"0":txtAttendanceBonusAdd.getValue().toString().replaceAll(",", "").trim())+"'," +
					" '"+(txtTiffinAllowanceAdd.getValue().toString().isEmpty()?"0":txtTiffinAllowanceAdd.getValue().toString().replaceAll(",", "").trim())+"'," +
					" '"+(txtRoomChargeLess.getValue().toString().isEmpty()?"0":txtRoomChargeLess.getValue().toString().replaceAll(",", "").trim())+"'," +
					" '"+(txtIncomeTaxLess.getValue().toString().isEmpty()?"0":txtIncomeTaxLess.getValue().toString().replaceAll(",", "").trim())+"'," +
					" '"+(txtProvidentFundLess.getValue().toString().isEmpty()?"0":txtProvidentFundLess.getValue().toString().replaceAll(",", "").trim())+"'," +
					" '"+(txtKallanFundLess.getValue().toString().isEmpty()?"0":txtKallanFundLess.getValue().toString().replaceAll(",", "").trim())+"'," +
					" '"+(txtKhichuriMealLess.getValue().toString().isEmpty()?"0":txtKhichuriMealLess.getValue().toString().replaceAll(",", "").trim())+"'," +
					" '"+cmbIsActive.getValue().toString()+"'," +
					" '"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP) ";
			session.createSQLQuery(Insert).executeUpdate();

			tx.commit();
			this.getParent().showNotification("All information saved successfully.");
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
		finally{session.close();}
	}

	public boolean updateData() 
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();

		try
		{
			//Check existing for of update
			String query = " Select * from tbUdSalaryRegisterInfo where vRegisterId =" +
					" '"+txtFindId.getValue().toString()+"' and vUdFlag = 'New' ";
			Iterator <?> iter = session.createSQLQuery(query).list().iterator();

			if(!iter.hasNext())
			{
				//Insert new value for first time update
				String Insert = " INSERT into tbUdSalaryRegisterInfo (vRegisterId,vRegisterName,mBasic,mHouseRent," +
						" mMedicalAllowance,mClinicalAllowance,mNonPracticeAllowance,mSpecialAllowance,mOtherAllowance," +
						" mDearnessAllowance,mConveyanceAllowance,mAttendanceBonus,mTiffinAllowance,mRoomCharge,mIncomeTax," +
						" mProvidentFund,mKallanFund,mKhichuriMeal,isActive,vUserName,vUserIp,dEntryTime,vUdFlag)" +
						" select vRegisterId,vRegisterName,mBasic,mHouseRent,mMedicalAllowance,mClinicalAllowance," +
						" mNonPracticeAllowance,mSpecialAllowance,mOtherAllowance,mDearnessAllowance," +
						" mConveyanceAllowance,mAttendanceBonus,mTiffinAllowance,mRoomCharge,mIncomeTax,mProvidentFund,mKallanFund," +
						" mKhichuriMeal,isActive,vUserName,vUserIp,dEntryTime,'New' from tbSalaryRegisterInfo" +
						" where vRegisterId = '"+txtFindId.getValue().toString()+"' ";
				session.createSQLQuery(Insert).executeUpdate();
			}

			// Main table update
			String update = "UPDATE tbSalaryRegisterInfo set" +
					" vRegisterName = '"+txtRegisterName.getValue().toString().trim()+"'," +
					" mBasic = '"+(txtBasicAdd.getValue().toString().isEmpty()?"0":txtBasicAdd.getValue().toString().replaceAll(",", "").trim())+"'," +
					" mHouseRent = '"+(txtHouseRentAdd.getValue().toString().isEmpty()?"0":txtHouseRentAdd.getValue().toString().replaceAll(",", "").trim())+"'," +
					" mMedicalAllowance = '"+(txtMedicalAllowanceAdd.getValue().toString().isEmpty()?"0":txtMedicalAllowanceAdd.getValue().toString().replaceAll(",", "").trim())+"'," +
					" mClinicalAllowance = '"+(txtClinicalAllowanceAdd.getValue().toString().isEmpty()?"0":txtClinicalAllowanceAdd.getValue().toString().replaceAll(",", "").trim())+"'," +
					" mNonPracticeAllowance = '"+(txtNonPracticeAllowanceAdd.getValue().toString().isEmpty()?"0":txtNonPracticeAllowanceAdd.getValue().toString().replaceAll(",", "").trim())+"'," +
					" mSpecialAllowance = '"+(txtSpecialAllowanceAdd.getValue().toString().isEmpty()?"0":txtSpecialAllowanceAdd.getValue().toString().replaceAll(",", "").trim())+"'," +
					" mOtherAllowance = '"+(txtOtherAllowanceAdd.getValue().toString().isEmpty()?"0":txtOtherAllowanceAdd.getValue().toString().replaceAll(",", "").trim())+"'," +
					" mDearnessAllowance = '"+(txtDearnessAllowanceAdd.getValue().toString().isEmpty()?"0":txtDearnessAllowanceAdd.getValue().toString().replaceAll(",", "").trim())+"'," +
					" mConveyanceAllowance = '"+(txtConveyanceAllowanceAdd.getValue().toString().isEmpty()?"0":txtConveyanceAllowanceAdd.getValue().toString().replaceAll(",", "").trim())+"'," +
					" mAttendanceBonus = '"+(txtAttendanceBonusAdd.getValue().toString().isEmpty()?"0":txtAttendanceBonusAdd.getValue().toString().replaceAll(",", "").trim())+"'," +
					" mRoomCharge = '"+(txtRoomChargeLess.getValue().toString().isEmpty()?"0":txtRoomChargeLess.getValue().toString().replaceAll(",", "").trim())+"'," +
					" mIncomeTax = '"+(txtIncomeTaxLess.getValue().toString().isEmpty()?"0":txtIncomeTaxLess.getValue().toString().replaceAll(",", "").trim())+"'," +
					" mProvidentFund = '"+(txtProvidentFundLess.getValue().toString().isEmpty()?"0":txtProvidentFundLess.getValue().toString().replaceAll(",", "").trim())+"'," +
					" mKallanFund = '"+(txtKallanFundLess.getValue().toString().isEmpty()?"0":txtKallanFundLess.getValue().toString().replaceAll(",", "").trim())+"'," +
					" mKhichuriMeal = '"+(txtKhichuriMealLess.getValue().toString().isEmpty()?"0":txtKhichuriMealLess.getValue().toString().replaceAll(",", "").trim())+"'," +
					" mTiffinAllowance = '"+(txtTiffinAllowanceAdd.getValue().toString().isEmpty()?"0":txtTiffinAllowanceAdd.getValue().toString().replaceAll(",", "").trim())+"'," +
					" isActive = '"+cmbIsActive.getValue().toString()+"'," +
					" vUserName = '"+sessionBean.getUserName()+"'," +
					" vUserIp = '"+sessionBean.getUserIp()+"'," +
					" dEntryTime = CURRENT_TIMESTAMP " +
					" where vRegisterId = '"+txtFindId.getValue().toString()+"' ";
			session.createSQLQuery(update).executeUpdate();

			//Insert update or changes data
			String Insert = " INSERT into tbUdSalaryRegisterInfo (vRegisterId,vRegisterName,mBasic,mHouseRent," +
					" mMedicalAllowance,mClinicalAllowance,mNonPracticeAllowance,mSpecialAllowance,mOtherAllowance," +
					" mDearnessAllowance,mConveyanceAllowance,mAttendanceBonus,mTiffinAllowance,mRoomCharge,mIncomeTax," +
					" mProvidentFund,mKallanFund,mKhichuriMeal,isActive,vUserName,vUserIp,dEntryTime,vUdFlag) values(" +
					" '"+txtFindId.getValue().toString()+"'," +
					" '"+txtRegisterName.getValue().toString().trim()+"'," +
					" '"+(txtBasicAdd.getValue().toString().isEmpty()?"0":txtBasicAdd.getValue().toString().replaceAll(",", "").trim())+"'," +
					" '"+(txtHouseRentAdd.getValue().toString().isEmpty()?"0":txtHouseRentAdd.getValue().toString().replaceAll(",", "").trim())+"'," +
					" '"+(txtMedicalAllowanceAdd.getValue().toString().isEmpty()?"0":txtMedicalAllowanceAdd.getValue().toString().replaceAll(",", "").trim())+"'," +
					" '"+(txtClinicalAllowanceAdd.getValue().toString().isEmpty()?"0":txtClinicalAllowanceAdd.getValue().toString().replaceAll(",", "").trim())+"'," +
					" '"+(txtNonPracticeAllowanceAdd.getValue().toString().isEmpty()?"0":txtNonPracticeAllowanceAdd.getValue().toString().replaceAll(",", "").trim())+"'," +
					" '"+(txtSpecialAllowanceAdd.getValue().toString().isEmpty()?"0":txtSpecialAllowanceAdd.getValue().toString().replaceAll(",", "").trim())+"'," +
					" '"+(txtOtherAllowanceAdd.getValue().toString().isEmpty()?"0":txtOtherAllowanceAdd.getValue().toString().replaceAll(",", "").trim())+"'," +
					" '"+(txtDearnessAllowanceAdd.getValue().toString().isEmpty()?"0":txtDearnessAllowanceAdd.getValue().toString().replaceAll(",", "").trim())+"'," +
					" '"+(txtConveyanceAllowanceAdd.getValue().toString().isEmpty()?"0":txtConveyanceAllowanceAdd.getValue().toString().replaceAll(",", "").trim())+"'," +
					" '"+(txtAttendanceBonusAdd.getValue().toString().isEmpty()?"0":txtAttendanceBonusAdd.getValue().toString().replaceAll(",", "").trim())+"'," +
					" '"+(txtTiffinAllowanceAdd.getValue().toString().isEmpty()?"0":txtTiffinAllowanceAdd.getValue().toString().replaceAll(",", "").trim())+"'," +
					" '"+(txtRoomChargeLess.getValue().toString().isEmpty()?"0":txtRoomChargeLess.getValue().toString().replaceAll(",", "").trim())+"'," +
					" '"+(txtIncomeTaxLess.getValue().toString().isEmpty()?"0":txtIncomeTaxLess.getValue().toString().replaceAll(",", "").trim())+"'," +
					" '"+(txtProvidentFundLess.getValue().toString().isEmpty()?"0":txtProvidentFundLess.getValue().toString().replaceAll(",", "").trim())+"'," +
					" '"+(txtKallanFundLess.getValue().toString().isEmpty()?"0":txtKallanFundLess.getValue().toString().replaceAll(",", "").trim())+"'," +
					" '"+(txtKhichuriMealLess.getValue().toString().isEmpty()?"0":txtKhichuriMealLess.getValue().toString().replaceAll(",", "").trim())+"'," +
					" '"+cmbIsActive.getValue().toString()+"'," +
					" '"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP,'Update') ";
			session.createSQLQuery(Insert).executeUpdate();

			txtFindId.setValue("");

			this.getParent().showNotification("All information update successfully.");

			tx.commit();

			return true;
		}
		catch(Exception exp)
		{
			tx.rollback();
			this.getParent().showNotification("Error to Update",exp+"",Notification.TYPE_ERROR_MESSAGE);
			return false;
		}
		finally{session.close();}
	}

	private void focusEnter()
	{
		allComp.add(txtRegisterName);
		allComp.add(txtBasicAdd);
		allComp.add(txtHouseRentAdd);
		allComp.add(txtMedicalAllowanceAdd);
		allComp.add(txtClinicalAllowanceAdd);
		allComp.add(txtNonPracticeAllowanceAdd);
		allComp.add(txtOtherAllowanceAdd);
		allComp.add(txtDearnessAllowanceAdd);
		allComp.add(txtConveyanceAllowanceAdd);
		allComp.add(txtSpecialAllowanceAdd);
		allComp.add(txtAttendanceBonusAdd);
		allComp.add(txtTiffinAllowanceAdd);

		allComp.add(txtRoomChargeLess);
		allComp.add(txtIncomeTaxLess);
		allComp.add(txtProvidentFundLess);
		allComp.add(txtKallanFundLess);
		allComp.add(txtKhichuriMealLess);
		allComp.add(cmbIsActive);

		allComp.add(cButton.btnSave);

		new FocusMoveByEnter(this,allComp);
	}

	private void txtClear()
	{
		txtRegisterId.setValue("");
		txtRegisterName.setValue("");
		txtBasicAdd.setValue("");
		txtHouseRentAdd.setValue("");
		txtMedicalAllowanceAdd.setValue("");
		txtClinicalAllowanceAdd.setValue("");
		txtNonPracticeAllowanceAdd.setValue("");
		txtOtherAllowanceAdd.setValue("");
		txtDearnessAllowanceAdd.setValue("");
		txtConveyanceAllowanceAdd.setValue("");
		txtSpecialAllowanceAdd.setValue("");
		txtAttendanceBonusAdd.setValue("");
		txtTiffinAllowanceAdd.setValue("");
		txtGrossSalary.setValue("");

		txtRoomChargeLess.setValue("");
		txtIncomeTaxLess.setValue("");
		txtProvidentFundLess.setValue("");
		txtKallanFundLess.setValue("");
		txtKhichuriMealLess.setValue("");
		txtTotalDeduction.setValue("");

		cmbIsActive.setValue(1);
	}

	public void txtInit(boolean t)
	{
		txtRegisterId.setEnabled(!t);
		txtRegisterName.setEnabled(!t);
		txtBasicAdd.setEnabled(!t);
		txtHouseRentAdd.setEnabled(!t);
		txtMedicalAllowanceAdd.setEnabled(!t);
		txtClinicalAllowanceAdd.setEnabled(!t);
		txtNonPracticeAllowanceAdd.setEnabled(!t);
		txtOtherAllowanceAdd.setEnabled(!t);
		txtDearnessAllowanceAdd.setEnabled(!t);
		txtConveyanceAllowanceAdd.setEnabled(!t);
		txtSpecialAllowanceAdd.setEnabled(!t);
		txtAttendanceBonusAdd.setEnabled(!t);
		txtTiffinAllowanceAdd.setEnabled(!t);
		txtGrossSalary.setEnabled(!t);

		txtRoomChargeLess.setEnabled(!t);
		txtIncomeTaxLess.setEnabled(!t);
		txtProvidentFundLess.setEnabled(!t);
		txtKallanFundLess.setEnabled(!t);
		txtKhichuriMealLess.setEnabled(!t);
		txtTotalDeduction.setEnabled(!t);

		cmbIsActive.setEnabled(!t);
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

	private AbsoluteLayout buildMainLayout() 
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("560px");
		mainLayout.setHeight("425px");
		mainLayout.setMargin(false);

		lblCommon = new Label("Register Id :");
		lblCommon.setImmediate(false);
		lblCommon.setWidth("-1px");
		lblCommon.setHeight("-1px");
		mainLayout.addComponent(lblCommon, "top:20.0px; left:20.0px;");

		txtRegisterId = new TextRead();
		txtRegisterId.setImmediate(true);
		txtRegisterId.setWidth("60px");
		txtRegisterId.setHeight("23px");
		mainLayout.addComponent(txtRegisterId, "top:18.0px;left:165.5px;");

		lblCommon = new Label("Register Name :");
		mainLayout.addComponent(lblCommon, "top:45.0px; left:20.0px;");

		txtRegisterName = new TextField();
		txtRegisterName.setImmediate(true);
		txtRegisterName.setWidth("250px");
		txtRegisterName.setHeight("-1px");
		mainLayout.addComponent(txtRegisterName, "top:43.0px; left:165.0px;");

		lblCommon = new Label("Basic :");
		mainLayout.addComponent(lblCommon, "top:70.0px; left:20.0px;");

		txtBasicAdd = new AmountCommaSeperator();
		txtBasicAdd.setImmediate(true);
		txtBasicAdd.setWidth("100px");
		txtBasicAdd.setHeight("-1px");
		mainLayout.addComponent(txtBasicAdd, "top:68.0px; left:165.0px;");

		lblCommon = new Label("House Rent :");
		mainLayout.addComponent(lblCommon, "top:95.0px; left:20.0px;");

		txtHouseRentAdd = new AmountCommaSeperator();
		txtHouseRentAdd.setImmediate(true);
		txtHouseRentAdd.setWidth("100px");
		txtHouseRentAdd.setHeight("-1px");
		mainLayout.addComponent(txtHouseRentAdd, "top:93.0px; left:165.0px;");

		lblCommon = new Label("Medical Allowance :");
		mainLayout.addComponent(lblCommon, "top:120.0px; left:20.0px;");

		txtMedicalAllowanceAdd = new AmountCommaSeperator();
		txtMedicalAllowanceAdd.setImmediate(true);
		txtMedicalAllowanceAdd.setWidth("100px");
		txtMedicalAllowanceAdd.setHeight("-1px");
		mainLayout.addComponent(txtMedicalAllowanceAdd, "top:118.0px; left:165.0px;");

		lblCommon = new Label("Clinical Allowance :");
		mainLayout.addComponent(lblCommon, "top:145.0px; left:20.0px;");

		txtClinicalAllowanceAdd = new AmountCommaSeperator();
		txtClinicalAllowanceAdd.setImmediate(true);
		txtClinicalAllowanceAdd.setWidth("100px");
		txtClinicalAllowanceAdd.setHeight("-1px");
		mainLayout.addComponent(txtClinicalAllowanceAdd, "top:143.0px; left:165.0px;");

		lblCommon = new Label("Non-Practice Allowance :");
		mainLayout.addComponent(lblCommon, "top:170.0px; left:20.0px;");

		txtNonPracticeAllowanceAdd = new AmountCommaSeperator();
		txtNonPracticeAllowanceAdd.setImmediate(true);
		txtNonPracticeAllowanceAdd.setWidth("100px");
		txtNonPracticeAllowanceAdd.setHeight("-1px");
		mainLayout.addComponent(txtNonPracticeAllowanceAdd, "top:168.0px; left:165.0px;");

		lblCommon = new Label("Other Allowance :");
		mainLayout.addComponent(lblCommon, "top:195.0px; left:20.0px;");

		txtOtherAllowanceAdd = new AmountCommaSeperator();
		txtOtherAllowanceAdd.setImmediate(true);
		txtOtherAllowanceAdd.setWidth("100px");
		txtOtherAllowanceAdd.setHeight("-1px");
		mainLayout.addComponent(txtOtherAllowanceAdd, "top:193.0px; left:165.0px;");

		lblCommon = new Label("Dearness Allowance :");
		mainLayout.addComponent(lblCommon, "top:220.0px; left:20.0px;");

		txtDearnessAllowanceAdd = new AmountCommaSeperator();
		txtDearnessAllowanceAdd.setImmediate(true);
		txtDearnessAllowanceAdd.setWidth("100px");
		txtDearnessAllowanceAdd.setHeight("-1px");
		mainLayout.addComponent(txtDearnessAllowanceAdd, "top:218.0px; left:165.0px;");

		lblCommon = new Label("Conveyance Allowance :");
		mainLayout.addComponent(lblCommon, "top:245.0px; left:20.0px;");

		txtConveyanceAllowanceAdd = new AmountCommaSeperator();
		txtConveyanceAllowanceAdd.setImmediate(true);
		txtConveyanceAllowanceAdd.setWidth("100px");
		txtConveyanceAllowanceAdd.setHeight("-1px");
		mainLayout.addComponent(txtConveyanceAllowanceAdd, "top:243.0px; left:165.0px;");

		lblCommon = new Label("Special Allowance :");
		mainLayout.addComponent(lblCommon, "top:270.0px; left:20.0px;");

		txtSpecialAllowanceAdd = new AmountCommaSeperator();
		txtSpecialAllowanceAdd.setImmediate(true);
		txtSpecialAllowanceAdd.setWidth("100px");
		txtSpecialAllowanceAdd.setHeight("-1px");
		mainLayout.addComponent(txtSpecialAllowanceAdd, "top:268.0px; left:165.0px;");

		lblCommon = new Label("Attendance Bonus :");
		mainLayout.addComponent(lblCommon, "top:295.0px; left:20.0px;");

		txtAttendanceBonusAdd = new AmountCommaSeperator();
		txtAttendanceBonusAdd.setImmediate(true);
		txtAttendanceBonusAdd.setWidth("100px");
		txtAttendanceBonusAdd.setHeight("-1px");
		mainLayout.addComponent(txtAttendanceBonusAdd, "top:293.0px; left:165.0px;");

		lblCommon = new Label("Tiffin Allowance :");
		mainLayout.addComponent(lblCommon, "top:320.0px; left:20.0px;");

		txtTiffinAllowanceAdd = new AmountCommaSeperator();
		txtTiffinAllowanceAdd.setImmediate(true);
		txtTiffinAllowanceAdd.setWidth("100px");
		txtTiffinAllowanceAdd.setHeight("-1px");
		mainLayout.addComponent(txtTiffinAllowanceAdd, "top:318.0px; left:165.0px;");

		lblCommon = new Label("Gross Amount :");
		mainLayout.addComponent(lblCommon, "top:345.0px; left:20.0px;");

		txtGrossSalary = new TextRead();
		txtGrossSalary.setImmediate(true);
		txtGrossSalary.setWidth("100px");
		txtGrossSalary.setHeight("24px");
		txtGrossSalary.setStyleName("tcoloum");
		mainLayout.addComponent(txtGrossSalary, "top:343.0px; left:165.0px;");

		/*lblCommon = new Label("Room Charge :");
		mainLayout.addComponent(lblCommon, "top:95.0px; left:330.0px;");*/

		txtRoomChargeLess = new AmountCommaSeperator();
		txtRoomChargeLess.setImmediate(true);
		txtRoomChargeLess.setWidth("100px");
		txtRoomChargeLess.setHeight("-1px");
		txtRoomChargeLess.setVisible(false);
		mainLayout.addComponent(txtRoomChargeLess, "top:93.0px; left:440.0px;");

		/*lblCommon = new Label("Income Tax :");
		mainLayout.addComponent(lblCommon, "top:120.0px; left:330.0px;");*/

		txtIncomeTaxLess = new AmountCommaSeperator();
		txtIncomeTaxLess.setImmediate(true);
		txtIncomeTaxLess.setWidth("100px");
		txtIncomeTaxLess.setHeight("-1px");
		mainLayout.addComponent(txtIncomeTaxLess, "top:118.0px; left:440.0px;");
		txtIncomeTaxLess.setVisible(false);

		lblCommon = new Label("Provident Fund(%) :");
		mainLayout.addComponent(lblCommon, "top:145.0px; left:330.0px;");

		txtProvidentFundLess = new AmountCommaSeperator();
		txtProvidentFundLess.setImmediate(true);
		txtProvidentFundLess.setWidth("100px");
		txtProvidentFundLess.setHeight("-1px");
		mainLayout.addComponent(txtProvidentFundLess, "top:143.0px; left:440.0px;");

		lblCommon = new Label("Kallan Fund :");
		mainLayout.addComponent(lblCommon, "top:170.0px; left:330.0px;");

		txtKallanFundLess = new AmountCommaSeperator();
		txtKallanFundLess.setImmediate(true);
		txtKallanFundLess.setWidth("100px");
		txtKallanFundLess.setHeight("-1px");
		mainLayout.addComponent(txtKallanFundLess, "top:168.0px; left:440.0px;");

		lblCommon = new Label("Meal Charge :");
		mainLayout.addComponent(lblCommon, "top:195.0px; left:330.0px;");

		txtKhichuriMealLess = new AmountCommaSeperator();
		txtKhichuriMealLess.setImmediate(true);
		txtKhichuriMealLess.setWidth("100px");
		txtKhichuriMealLess.setHeight("-1px");
		mainLayout.addComponent(txtKhichuriMealLess, "top:193.0px; left:440.0px;");

		lblCommon = new Label("Deduction Amount :");
		mainLayout.addComponent(lblCommon, "top:220.0px; left:330.0px;");

		txtTotalDeduction = new TextRead(1);
		txtTotalDeduction.setImmediate(true);
		txtTotalDeduction.setWidth("100px");
		txtTotalDeduction.setHeight("24px");
		txtTotalDeduction.setStyleName("tcoloum");
		mainLayout.addComponent(txtTotalDeduction, "top:218.0px; left:440.5px;");

		lblIsActive = new Label("Status :");
		lblIsActive.setImmediate(true);
		lblIsActive.setWidth("-1px");
		lblIsActive.setHeight("-1px");
		mainLayout.addComponent(lblIsActive, "top:246.0px; left:330.0px;");

		cmbIsActive = new NativeSelect();
		cmbIsActive.setNullSelectionAllowed(false);
		cmbIsActive.setImmediate(true);
		cmbIsActive.setWidth("80px");
		cmbIsActive.setHeight("-1px");
		for (int i = 0; i < status.length; i++)
		{
			cmbIsActive.addItem(i);
			cmbIsActive.setItemCaption(i, status[i]);
		}
		cmbIsActive.setValue(1);
		mainLayout.addComponent(cmbIsActive,"top:244.0px; left:440.0px;");

		mainLayout.addComponent(cButton,"top:380px; left:21px;");

		return mainLayout;
	}
}