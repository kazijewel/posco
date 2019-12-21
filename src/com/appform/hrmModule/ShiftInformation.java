package com.appform.hrmModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.CommonMethod;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.ReportDate;
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
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class ShiftInformation extends Window 
{
	CommonButton cButton = new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "","","Exit");

	private TextField txtFindId = new TextField();

	boolean isUpdate=false;
	boolean isNew=false;

	private AbsoluteLayout mainLayout;

	private Label lblCommon;

	private TextRead txtShiftId;
	private TextField txtShiftName;

	private InlineDateField dStartTime;
	private InlineDateField dStartTimeLimit;

	private InlineDateField dEndTime;
	private InlineDateField dEndTimeLimit;

	private TextField txtPartyIdBack = new TextField();

	SessionBean sessionBean;

	ArrayList<Component> allComp = new ArrayList<Component>();

	private Label lblIsActive;
	private NativeSelect cmbIsActive;
	private static final String[] status = new String[] { "Inactive", "Active" };

	SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss aa");
	ReportDate reportTime = new ReportDate();

	private boolean isFind = false;
	private CommonMethod cm;
	private String menuId = "";
	public ShiftInformation(SessionBean sessionBean,String menuId)
	{
		this.sessionBean=sessionBean;
		this.setResizable(false);
		this.setCaption("SHIFT INFORMATION :: "+sessionBean.getCompany());
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
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

				txtShiftId.setValue(selectShiftId());
				txtShiftName.focus();
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
				txtFindId.setValue("");
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

		txtShiftName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtShiftName.getValue().toString().isEmpty())
				{
					if(duplicateShift() && !isFind)
					{
						showNotification("Warning!","Shift Name already exist.",Notification.TYPE_WARNING_MESSAGE);
						txtShiftName.setValue("");
						txtShiftName.focus();
					}
				}
			}
		});
	}

	private void formValidation()
	{
		if(!txtShiftName.getValue().toString().isEmpty())
		{
			saveButtonEvent();
		}
		else
		{
			getParent().showNotification("Warning!","Provide Shift Name.", Notification.TYPE_WARNING_MESSAGE);
			txtShiftName.focus();
		}
	}

	private boolean duplicateShift()
	{
		boolean ret = false;
		if(!isUpdate)
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String query = "select * from tbShiftInfo where vShiftName" +
						" like '"+txtShiftName.getValue().toString()+"'";

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
		Window win = new HrmSetupFindWindow(sessionBean, txtFindId, "SHIFT");
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if (txtFindId.getValue().toString().length() > 0)
				{
					txtClear();
					txtPartyIdBack.setValue(txtFindId.getValue().toString());
					findInitialise(txtFindId.getValue().toString());
				}
			}
		});
		this.getParent().addWindow(win);	
	}

	private String selectShiftId()
	{
		String unitId = "";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();

		try 
		{
			String query = " Select isnull(max(cast(SUBSTRING(vShiftId,4,10) as int)),0)+1 from tbShiftInfo ";

			Iterator <?> iter = session.createSQLQuery(query).list().iterator();

			if (iter.hasNext()) 
			{
				unitId = "SFT"+iter.next().toString();
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		finally{session.close();}
		return unitId;
	}

	private void findInitialise(String mainGroupId) 
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "select vShiftId,vShiftName,dShiftStart,dShiftEnd,dLateInLimit,dEarlyOutLimit,isActive from" +
					" tbShiftInfo Where vShiftId = '"+mainGroupId+"'";
			System.out.println(sql);

			List <?> led = session.createSQLQuery(sql).list();

			if (led.iterator().hasNext()) 
			{
				Object[] element = (Object[]) led.iterator().next();

				txtShiftId.setValue(element[0].toString());
				txtShiftName.setValue(element[1].toString());
				dStartTime.setValue(element[2]);
				dEndTime.setValue(element[3]);
				dStartTimeLimit.setValue(element[4]);
				dEndTimeLimit.setValue(element[5]);
				cmbIsActive.setValue(element[6]);
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
		if(!txtShiftId.getValue().toString().isEmpty())
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
			String Insert = " INSERT into tbShiftInfo (vShiftId, vShiftName, dShiftStart, dShiftEnd, "
					+ "dLateInLimit, dEarlyOutLimit, isActive, vUserName, vUserIp, dEntryTime) values (" +
					" '"+selectShiftId()+"'," +
					" '"+txtShiftName.getValue().toString().trim()+"'," +
					" '"+timeFormat.format(dStartTime.getValue())+"'," +
					" '"+timeFormat.format(dEndTime.getValue())+"'," +
					" '"+timeFormat.format(dStartTimeLimit.getValue())+"'," +
					" '"+timeFormat.format(dEndTimeLimit.getValue())+"'," +
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
			String query = " Select * from tbUdShiftInfo where vShiftId =" +
					" '"+txtFindId.getValue().toString()+"' and vUdFlag = 'New' ";

			Iterator <?> iter = session.createSQLQuery(query).list().iterator();

			if(!iter.hasNext())
			{
				//Insert new value for first time update
				String Insert = " INSERT into tbUdShiftInfo (vShiftId, vShiftName, dShiftStart, dShiftEnd, " +
						" dLateInLimit, dEarlyOutLimit, isActive, vUserName, vUserIp, dEntryTime,vUdFlag)" +
						" select vShiftId, vShiftName, dShiftStart, dShiftEnd, dLateInLimit, dEarlyOutLimit, " +
						" isActive, vUserName, vUserIp, dEntryTime,'New' from tbShiftInfo where " +
						" vShiftId = '"+txtFindId.getValue().toString()+"' ";
				session.createSQLQuery(Insert).executeUpdate();
			}

			// Main table update
			String update = "UPDATE tbShiftInfo set" +
					" vShiftName = '"+txtShiftName.getValue().toString().trim()+"' ," +
					" dShiftStart = '"+timeFormat.format(dStartTime.getValue())+"', " +
					" dShiftEnd = '"+timeFormat.format(dEndTime.getValue())+"', " +
					" dLateInLimit = '"+timeFormat.format(dStartTimeLimit.getValue())+"', " +
					" dEarlyOutLimit = '"+timeFormat.format(dEndTimeLimit.getValue())+"', " +
					" isActive = '"+cmbIsActive.getValue().toString()+"' "+
					" where vShiftId = '"+txtFindId.getValue().toString()+"' ";
			session.createSQLQuery(update).executeUpdate();

			//Insert update or changes data
			String Insert = " INSERT into tbUdShiftInfo (vShiftId, vShiftName, dShiftStart, dShiftEnd, " +
						" dLateInLimit, dEarlyOutLimit, isActive, vUserName, vUserIp, dEntryTime,vUdFlag) " +
						" values (" +
						" '"+txtFindId.getValue().toString()+"'," +
						" '"+txtShiftName.getValue().toString().trim()+"'," +
						" '"+timeFormat.format(dStartTime.getValue())+"'," +
						" '"+timeFormat.format(dEndTime.getValue())+"'," +
						" '"+timeFormat.format(dStartTimeLimit.getValue())+"'," +
						" '"+timeFormat.format(dEndTimeLimit.getValue())+"'," +
						" '"+cmbIsActive.getValue().toString()+"'," +
						" '"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"'," +
						" CURRENT_TIMESTAMP,'Update') ";
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
			this.getParent().showNotification("Error to Update",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
		return false;
	}

	private void focusEnter()
	{
		allComp.add(txtShiftName);
		allComp.add(dStartTime);
		allComp.add(dEndTime);
		allComp.add(cmbIsActive);

		allComp.add(cButton.btnSave);

		new FocusMoveByEnter(this,allComp);
	}

	private void txtClear()
	{
		txtShiftId.setValue("");
		txtShiftName.setValue("");
		cmbIsActive.setValue(1);
	}

	public void txtInit(boolean t)
	{
		txtShiftId.setEnabled(!t);
		txtShiftName.setEnabled(!t);
		dStartTime.setEnabled(!t);
		dStartTimeLimit.setEnabled(!t);
		dEndTime.setEnabled(!t);
		dEndTimeLimit.setEnabled(!t);
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
		mainLayout.setWidth("630px");
		mainLayout.setHeight("280px");
		mainLayout.setMargin(false);

		lblCommon = new Label("Shift Id :");
		mainLayout.addComponent(lblCommon, "top:50.0px; left:20.0px;");

		txtShiftId = new TextRead();
		txtShiftId.setImmediate(true);
		txtShiftId.setWidth("60px");
		txtShiftId.setHeight("23px");
		mainLayout.addComponent(txtShiftId, "top:48.0px; left:132.0px;");

		lblCommon = new Label("Shift Name :");
		mainLayout.addComponent(lblCommon, "top:80.0px; left:20.0px;");

		txtShiftName = new TextField();
		txtShiftName.setImmediate(true);
		txtShiftName.setWidth("250px");
		txtShiftName.setHeight("-1px");
		mainLayout.addComponent(txtShiftName, "top:78.0px; left:130px;");

		lblCommon = new Label("Shift Start Time :");
		mainLayout.addComponent(lblCommon, "top:110.0px; left:20.0px;");

		dStartTime = new InlineDateField();
		dStartTime.setImmediate(true);
		dStartTime.setWidth("110px");
		dStartTime.setHeight("-1px");
		dStartTime.setDateFormat("hh:mm:ss");
		dStartTime.setValue(reportTime.getTime);
		dStartTime.setResolution(InlineDateField.RESOLUTION_SEC);
		mainLayout.addComponent(dStartTime, "top:110.0px; left:130px;");

		lblCommon = new Label("Late In Time Limit :");
		mainLayout.addComponent(lblCommon, "top:110.0px; left:320.0px;");

		dStartTimeLimit = new InlineDateField();
		dStartTimeLimit.setImmediate(true);
		dStartTimeLimit.setWidth("110px");
		dStartTimeLimit.setHeight("-1px");
		dStartTimeLimit.setDateFormat("hh:mm:ss");
		dStartTimeLimit.setValue(reportTime.getTime);
		dStartTimeLimit.setResolution(InlineDateField.RESOLUTION_SEC);
		mainLayout.addComponent(dStartTimeLimit, "top:110.0px; left:440px;");

		lblCommon = new Label("Shift End Time :");
		mainLayout.addComponent(lblCommon, "top:140.0px; left:20.0px;");

		dEndTime = new InlineDateField();
		dEndTime.setImmediate(true);
		dEndTime.setWidth("110px");
		dEndTime.setHeight("-1px");
		dEndTime.setResolution(InlineDateField.RESOLUTION_SEC);
		dEndTime.setDateFormat("hh:mm:ss");
		dEndTime.setValue(reportTime.getTime);
		mainLayout.addComponent(dEndTime, "top:140.0px; left:130.0px;");

		lblCommon = new Label("Early Out Time Limit :");
		mainLayout.addComponent(lblCommon, "top:140.0px; left:320.0px;");

		dEndTimeLimit = new InlineDateField();
		dEndTimeLimit.setImmediate(true);
		dEndTimeLimit.setWidth("110px");
		dEndTimeLimit.setHeight("-1px");
		dEndTimeLimit.setResolution(InlineDateField.RESOLUTION_SEC);
		dEndTimeLimit.setDateFormat("hh:mm:ss");
		dEndTimeLimit.setValue(reportTime.getTime);
		mainLayout.addComponent(dEndTimeLimit, "top:140.0px; left:440.0px;");

		lblIsActive = new Label("Status :");
		lblIsActive.setImmediate(false);
		lblIsActive.setWidth("-1px");
		lblIsActive.setHeight("-1px");
		mainLayout.addComponent(lblIsActive, "top:170.0px; left:20.0px;");

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
		mainLayout.addComponent(cmbIsActive,"top:168.0px; left:130.0px;");
		mainLayout.addComponent(cButton,"top:210px; left:58px;");
		return mainLayout;
	}
}