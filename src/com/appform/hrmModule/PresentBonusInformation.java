package com.appform.hrmModule;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.AmountField;
import com.common.share.CommonButton;
import com.common.share.CommonMethod;
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
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class PresentBonusInformation extends Window 
{
	CommonButton cButton = new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "","","Exit");

	private TextField txtFindId = new TextField();
	private TextField txtIdBack = new TextField();

	boolean isUpdate=false;
	boolean isNew=false;

	private AbsoluteLayout mainLayout;

	private Label lblCommon;

	private PopupDateField dDate;
	private TextRead PresentBonusId;
	private AmountField txtPresentBonus;

	private Label lblIsActive;
	private NativeSelect cmbIsActive;

	SessionBean sessionBean;
	private CommonMethod cm;
	private String menuId = "";
	ArrayList<Component> allComp = new ArrayList<Component>();
	private static final String[] status = new String[] { "Inactive", "Active" };

	private boolean isFind = false;

	private DecimalFormat dfAmount = new DecimalFormat("#0.00");
	private SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");

	public PresentBonusInformation(SessionBean sessionBean,String menuId)
	{
		this.sessionBean = sessionBean;
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		this.setCaption("PRESENT BONUS INFORMATION :: "+sessionBean.getCompany());

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

				PresentBonusId.setValue(selectMaxId());
				txtPresentBonus.focus();
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

		txtPresentBonus.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtPresentBonus.getValue().toString().isEmpty())
				{
					if(duplicateMainGroup() && !isFind)
					{
						showNotification("Warning!","Meal charge already exist.",Notification.TYPE_WARNING_MESSAGE);
						txtPresentBonus.setValue("");
						txtPresentBonus.focus();
					}
				}
			}
		});
	}

	private void formValidation()
	{
		if(!txtPresentBonus.getValue().toString().isEmpty())
		{
			saveButtonEvent();
		}
		else
		{
			showNotification("Warning!","Provide Meal Charge.", Notification.TYPE_WARNING_MESSAGE);
			txtPresentBonus.focus();
		}
	}

	private String selectMaxId()
	{
		String maxId = "";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try 
		{
			String query = "Select isnull(max(cast(SUBSTRING(vPresentBonusID,4,10) as int)),0)+1 from tbPresentBonusInfo ";

			Iterator <?> iter = session.createSQLQuery(query).list().iterator();

			if (iter.hasNext()) 
			{
				String srt = iter.next().toString();

				maxId = "PR-"+srt;
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		finally{session.close();}

		return maxId;
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
				String query = "select * from tbPresentBonusInfo where mPresentBonus" +
						" like '"+txtPresentBonus.getValue().toString()+"'";

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
		Window win = new PresentBonusFind(sessionBean, txtFindId, "PRESENTBONUS");
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

	private void findInitialise(String mealchargeId) 
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "select vPresentBonusID,mPresentBonus,isActive from tbPresentBonusInfo Where vPresentBonusID = '"+mealchargeId+"'";

			List <?> led = session.createSQLQuery(sql).list();

			if (led.iterator().hasNext()) 
			{
				Object[] element = (Object[]) led.iterator().next();

				PresentBonusId.setValue(element[0].toString());
				txtPresentBonus.setValue(dfAmount.format(element[1]));
				cmbIsActive.setValue(element[2]);
			}
		}
		catch (Exception exp) 
		{
			showNotification("Error", exp + "",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void refreshButtonEvent() 
	{
		txtInit(true);
		btnIni(true);
		txtClear();
		isNew = false;
	}

	private void updateButtonEvent()
	{
		if(!txtPresentBonus.getValue().toString().isEmpty())
		{
			isUpdate = true;
			isFind = false;
			btnIni(false);
			txtInit(false);
		}
		else
		{
			showNotification("Update Failed","There are no data for update.",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void saveButtonEvent()
	{
		if(isUpdate)
		{
			MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to update information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.setStyleName("cwindowMB");
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
			mb.setStyleName("cwindowMB");
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
			String Insert = " INSERT into tbPresentBonusInfo (dDate,vPresentBonusID,mPresentBonus," +
					" isActive,vUserName,vUserIp,dEntryTime) values(" +
					" '"+dateFormat.format(dDate.getValue())+"'," +
					" '"+selectMaxId()+"'," +
					" '"+txtPresentBonus.getValue().toString().trim()+"'," +
					" '"+cmbIsActive.getValue().toString()+"'," +
					" '"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP) ";
			session.createSQLQuery(Insert).executeUpdate();

			tx.commit();
			Notification n=new Notification("All Information Save Successfully!","",Notification.TYPE_TRAY_NOTIFICATION);
			n.setPosition(Notification.POSITION_TOP_RIGHT);
			showNotification(n);
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
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
			String query = " Select * from tbUdPresentBonusInfo where vPresentBonusId = " +
					"'"+txtFindId.getValue().toString()+"' and vUdFlag = 'New' ";
			System.out.println("shehab"+query);

			Iterator <?> iter = session.createSQLQuery(query).list().iterator();

			if(!iter.hasNext())
			{
				//Insert new value for first time update
				String Insert = " INSERT into tbUdPresentBonusInfo (dDate,vPresentBonusId,mPresentBonus," +
						" isActive,vUserName,vUserIp,dEntryTime,vUdFlag)" +
						" select dDate,vPresentBonusId,mPresentBonus," +
						" isActive,vUserName,vUserIp,dEntryTime,'New' from tbUdPresentBonusInfo" +
						" where vPresentBonusId = '"+txtFindId.getValue().toString()+"' ";
				
				System.out.println("shehab1"+Insert);
				session.createSQLQuery(Insert).executeUpdate();
			}

			// Main table update
			String update = "UPDATE tbPresentBonusInfo set" +
					" dDate = '"+dateFormat.format(dDate.getValue())+"' ," +
					" mPresentBonus = '"+txtPresentBonus.getValue()+"' ," +
					" isActive = '"+cmbIsActive.getValue().toString()+"' " +
					" where vPresentBonusId = '"+txtFindId.getValue().toString()+"' ";
			System.out.println("shehab2"+update);
			session.createSQLQuery(update).executeUpdate();

			//Insert update or changes data
			String Insert = " INSERT into tbUdPresentBonusInfo (dDate,vPresentBonusId,mPresentBonus," +
					" isActive,vUserName,vUserIp,dEntryTime,vUdFlag) values(" +
					"'"+dateFormat.format(dDate.getValue())+"' ," +
					" '"+txtFindId.getValue().toString()+"'," +
					" '"+txtPresentBonus.getValue().toString().trim()+"'," +
					" '"+cmbIsActive.getValue().toString()+"'," +
					" '"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"'," +
					" CURRENT_TIMESTAMP,'Update') ";
			
			System.out.println("shehab3"+Insert);
			session.createSQLQuery(Insert).executeUpdate();

			txtFindId.setValue("");
			Notification n=new Notification("All Information Updated Successfully!","",Notification.TYPE_TRAY_NOTIFICATION);
			n.setPosition(Notification.POSITION_TOP_RIGHT);
			showNotification(n);
			
			tx.commit();
			
			return true;
		}
		
		catch(Exception exp)
		{
			tx.rollback();
			showNotification("Error to Update",exp+"",Notification.TYPE_ERROR_MESSAGE);
			return false;
		}
		finally{session.close();}
	}

	private void focusEnter()
	{
		allComp.add(txtPresentBonus);
		allComp.add(cmbIsActive);

		allComp.add(cButton.btnSave);

		new FocusMoveByEnter(this,allComp);
	}

	private void txtClear()
	{
		dDate.setValue(new Date());
		PresentBonusId.setValue("");
		txtPresentBonus.setValue("");
		cmbIsActive.setValue(1);
	}

	public void txtInit(boolean t)
	{
		
		dDate.setEnabled(!t);
		PresentBonusId.setEnabled(!t);
		txtPresentBonus.setEnabled(!t);
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
		mainLayout.setWidth("580px");
		mainLayout.setHeight("200px");
		mainLayout.setMargin(false);

		lblCommon = new Label("Date :");
		lblCommon.setImmediate(false);
		lblCommon.setWidth("-1px");
		lblCommon.setHeight("-1px");
		mainLayout.addComponent(lblCommon, "top:20.0px; left:50.0px;");

		dDate=new PopupDateField();
		dDate.setImmediate(true);
		dDate.setWidth("110px");
		dDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dDate.setDateFormat("dd-MM-yyyy");
		dDate.setValue(new Date());
		mainLayout.addComponent(dDate, "top:18.0px; left:180.5px;");

		lblCommon = new Label("Present Bonus Id :");
		lblCommon.setImmediate(false);
		lblCommon.setWidth("-1px");
		lblCommon.setHeight("-1px");
		mainLayout.addComponent(lblCommon, "top:50.0px; left:50.0px;");

		PresentBonusId = new TextRead();
		PresentBonusId.setImmediate(true);
		PresentBonusId.setWidth("60px");
		PresentBonusId.setHeight("24px");
		mainLayout.addComponent(PresentBonusId, "top:48.0px;left:180.5px;");

		lblCommon = new Label("Present Bonus :");
		mainLayout.addComponent(lblCommon, "top:80.0px; left:50.0px;");

		txtPresentBonus = new AmountField();
		txtPresentBonus.setImmediate(true);
		txtPresentBonus.setWidth("120px");
		txtPresentBonus.setHeight("-1px");
		mainLayout.addComponent(txtPresentBonus, "top:78.0px; left:180.0px;");

		lblCommon = new Label("TK/-");
		mainLayout.addComponent(lblCommon, "top:78.0px; left:310.0px;");

		lblIsActive = new Label("Status :");
		lblIsActive.setImmediate(true);
		lblIsActive.setWidth("-1px");
		lblIsActive.setHeight("-1px");
		mainLayout.addComponent(lblIsActive, "top:110.0px; left:50.0px;");

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
		mainLayout.addComponent(cmbIsActive,"top:108.0px; left:180.0px;");

		mainLayout.addComponent(cButton,"top:160px; left:18px;");

		return mainLayout;
	}
}
