package com.appform.hrmModule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.AmountField;
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
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Button.ClickShortcut;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;

public class DesignationRankInfo extends Window{
	
	private TextField txtFindId = new TextField();
	private TextField txtIdBack = new TextField();

	boolean isUpdate=false;
	boolean isNew=false;

	private AbsoluteLayout mainLayout;

	private Label lblCommon;

	private TextRead txtRankId;
	private AmountField txtRankSerial;
	private TextField txtRankRemark;

	private Label lblIsActive;
	private NativeSelect cmbIsActive;

	SessionBean sessionBean;

	ArrayList<Component> allComp = new ArrayList<Component>();
	private static final String[] status = new String[] { "Inactive", "Active" };

	private boolean isFind = false;
	
	CommonButton cButton = new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "","","Exit");

	
	public DesignationRankInfo(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setResizable(false);
		this.setCaption("DESIGNATION GRADE INFORMATION :: "+sessionBean.getCompany());

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
	private void txtClear()
	{
		txtRankId.setValue("");
		txtRankSerial.setValue("");
		txtRankRemark.setValue("");
		cmbIsActive.setValue(1);
	}

	private void focusEnter()
	{
		allComp.add(txtRankId);
		allComp.add(txtRankSerial);
		allComp.add(txtRankRemark);
		allComp.add(cmbIsActive);

		allComp.add(cButton.btnSave);

		new FocusMoveByEnter(this,allComp);
	}
	public void txtInit(boolean t)
	{
		txtRankId.setEnabled(!t);
		txtRankSerial.setEnabled(!t);
		txtRankRemark.setEnabled(!t);
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


	private void updateButtonEvent()
	{
		if(!txtRankSerial.getValue().toString().isEmpty())
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

	private void refreshButtonEvent() 
	{
		txtInit(true);
		btnIni(true);
		txtClear();
		isNew=false;
	}
	private void findButtonEvent() 
	{
		Window win = new HrmSetupFindWindow(sessionBean, txtFindId, "GRADE");
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
	private void findInitialise(String rankId) 
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try 
		{
			String sql = "select vRankId,iRankSerial,vRankName,isActive from tbDesignationRankInfo "
					+ "Where vRankId = '"+rankId+"'";
			//System.out.println(sql);

			List <?> led = session.createSQLQuery(sql).list();

			if (led.iterator().hasNext()) 
			{
				Object[] element = (Object[]) led.iterator().next();

				txtRankId.setValue(element[0].toString());
				txtRankSerial.setValue(element[1].toString());
				txtRankRemark.setValue(element[2].toString());
				cmbIsActive.setValue(element[3]);
			}

			isFind = false;
		}
		catch (Exception exp) 
		{
			showNotification("Error", exp + "",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private String selectMaxRankSerial()
	{
		String maxRankSerial = "";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try 
		{
			String query = " Select isnull(max(iRankSerial),0)+1 rankSerial from tbDesignationRankInfo ";
			Iterator <?> iter = session.createSQLQuery(query).list().iterator();

			if (iter.hasNext()) 
			{
				String srt = iter.next().toString();

				maxRankSerial =srt;
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		finally{session.close();}

		return maxRankSerial;
	}
	
	private String selectMaxId()
	{
		String maxId = "";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try 
		{
			String query = " Select isnull(max(cast(SUBSTRING(vRankId,4,10) as int)),0)+1 from tbDesignationRankInfo ";
			Iterator <?> iter = session.createSQLQuery(query).list().iterator();

			if (iter.hasNext()) 
			{
				String srt = iter.next().toString();

				maxId = "RNK"+srt;
				//txtRankSerial.setValue(srt);
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		finally{session.close();}

		return maxId;
	}
	private void insertData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{
			String Insert = " INSERT into tbDesignationRankInfo (vRankId,iRankSerial," +
					" vRankName,isActive,vUserName,vUserIp,dEntryTime) values(" +
					" '"+selectMaxId()+"'," +
					" '"+txtRankSerial.getValue().toString().trim()+"'," +
					" '"+txtRankRemark.getValue().toString().trim()+"'," +
					" '"+cmbIsActive.getValue().toString()+"'," +
					" '"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP) ";
			session.createSQLQuery(Insert).executeUpdate();

			tx.commit();
			showNotification("All information saved successfully.");
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
			String query = " Select * from tbUdDesignationRankInfo where vRankId = '"+txtFindId.getValue().toString()+"'" +
					" and vUdFlag = 'New' ";

			Iterator <?> iter = session.createSQLQuery(query).list().iterator();

			if(!iter.hasNext())
			{
				//Insert new value for first time update
				String Insert = " INSERT into tbUdDesignationRankInfo (vRankId,iRankSerial," +
						" vRankName,isActive,vUserName,vUserIp,dEntryTime,vUdFlag)" +
						" select vRankId,iRankSerial,vRankName,isActive," +
						" vUserName,vUserIp,dEntryTime,'New' from tbDesignationRankInfo" +
						" where vRankId = '"+txtFindId.getValue().toString()+"' ";
				session.createSQLQuery(Insert).executeUpdate();
			}

			// Main table update
			String update = "UPDATE tbDesignationRankInfo set" +
					" iRankSerial = '"+txtRankSerial.getValue().toString().trim()+"' ," +
					" vRankName = '"+txtRankRemark.getValue().toString().trim()+"' ," +
					" isActive = '"+cmbIsActive.getValue().toString()+"' " +
					" where vRankId = '"+txtFindId.getValue().toString()+"' ";
			session.createSQLQuery(update).executeUpdate();

			//Insert update or changes data
			String Insert = " INSERT into tbUdDesignationRankInfo (vRankId,iRankSerial," +
					" vRankName,isActive,vUserName,vUserIp,dEntryTime,vUdFlag) values(" +
					" '"+txtFindId.getValue().toString()+"'," +
					" '"+txtRankSerial.getValue().toString().trim()+"'," +
					" '"+txtRankRemark.getValue().toString().trim()+"'," +
					" '"+cmbIsActive.getValue().toString()+"'," +
					" '"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"'," +
					" CURRENT_TIMESTAMP,'Update') ";
			session.createSQLQuery(Insert).executeUpdate();

			txtFindId.setValue("");

			showNotification("All information update successfully.");

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
	private void formValidation()
	{
		if(!txtRankSerial.getValue().toString().isEmpty())
		{
			saveButtonEvent();
			/*if(!txtRankRemark.getValue().toString().isEmpty())
			{
				saveButtonEvent();
			}
			else
			{
				showNotification("Warning!","Provide Remark", Notification.TYPE_WARNING_MESSAGE);
				txtRankRemark.focus();
			}*/
		}
		else
		{
			showNotification("Warning!","Provide Rank Serial", Notification.TYPE_WARNING_MESSAGE);
			txtRankSerial.focus();
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

				txtRankId.setValue(selectMaxId());
				txtRankSerial.setValue(selectMaxRankSerial());
				txtRankRemark.focus();
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

		txtRankSerial.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtRankSerial.getValue().toString().isEmpty())
				{
					if(duplicateSerial() && !isFind)
					{
						showNotification("Warning!","Rank Serial already assign",Notification.TYPE_WARNING_MESSAGE);
						txtRankSerial.setValue("");
						txtRankSerial.focus();
					}
				}
			}
		});

		/*txtRankRemark.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtRankRemark.getValue().toString().isEmpty())
				{
					if(duplicateRankName() && !isFind)
					{
						showNotification("Warning!","Remark already exist.",Notification.TYPE_WARNING_MESSAGE);
						txtRankRemark.setValue("");
						txtRankRemark.focus();
					}
				}
			}
		});*/
		
	}
	
	private boolean duplicateRankName()
	{
		boolean ret = false;
		if(!isUpdate)
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String query = "select * from tbDesignationRankInfo where vRankName" +
						" like '"+txtRankRemark.getValue().toString()+"'";

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

	private boolean duplicateSerial()
	{
		boolean ret = false;
		if(!isUpdate)
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String query = "select * from tbDesignationRankInfo where iRankSerial" +
						" = '"+txtRankSerial.getValue().toString()+"'";
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
	
	private AbsoluteLayout buildMainLayout() 
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("550px");
		mainLayout.setHeight("210px");
		mainLayout.setMargin(false);

		lblCommon = new Label("Grade ID :");
		lblCommon.setImmediate(false);
		lblCommon.setWidth("-1px");
		lblCommon.setHeight("-1px");
		mainLayout.addComponent(lblCommon, "top:20.0px; left:50.0px;");

		txtRankId = new TextRead();
		txtRankId.setImmediate(true);
		txtRankId.setWidth("60px");
		txtRankId.setHeight("24px");
		mainLayout.addComponent(txtRankId, "top:18.0px;left:180.5px;");
		
		lblCommon = new Label("Grade Serial :");
		lblCommon.setImmediate(false);
		lblCommon.setWidth("-1px");
		lblCommon.setHeight("-1px");
		mainLayout.addComponent(lblCommon, "top:50.0px; left:50.0px;");

		txtRankSerial = new AmountField();
		txtRankSerial.setImmediate(true);
		txtRankSerial.setWidth("60px");
		txtRankSerial.setHeight("24px");
		mainLayout.addComponent(txtRankSerial, "top:48.0px;left:180.5px;");

		lblCommon = new Label("Remark :");
		mainLayout.addComponent(lblCommon, "top:80.0px; left:50.0px;");

		txtRankRemark = new TextField();
		txtRankRemark.setImmediate(true);
		txtRankRemark.setWidth("320px");
		txtRankRemark.setHeight("-1px");
		mainLayout.addComponent(txtRankRemark, "top:78.0px; left:180.0px;");

		/*lblCommon = new Label("Designation Serial :");
		mainLayout.addComponent(lblCommon, "top:80.0px; left:50.0px;");
		txtDesignationSerial = new AmountField();
		txtDesignationSerial.setImmediate(true);
		txtDesignationSerial.setWidth("40px");
		txtDesignationSerial.setHeight("-1px");
		mainLayout.addComponent(txtDesignationSerial, "top:78.0px; left:180.0px;");

		lblCommon = new Label("Rank :");
		mainLayout.addComponent(lblCommon, "top:110.0px; left:50.0px;");
		cmbDesignationRank = new ComboBox("",rank);
		cmbDesignationRank.setImmediate(true);
		cmbDesignationRank.setWidth("80px");
		cmbDesignationRank.setHeight("-1px");
		mainLayout.addComponent(cmbDesignationRank, "top:108.0px; left:180.0px;");

		btnPlusRank = new NativeButton();
		btnPlusRank.setIcon(new ThemeResource("../icons/add.png"));
		btnPlusRank.setImmediate(true);
		btnPlusRank.setWidth("28px");
		btnPlusRank.setHeight("24px");
		mainLayout.addComponent(btnPlusRank, "top:108.0px; left:260.0px;");*/
		
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
		
		mainLayout.addComponent(cButton,"top:140px; left:18px;");

		return mainLayout;
	}
}
