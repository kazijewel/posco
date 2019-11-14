package com.appform.hrmModule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

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
public class GroupInformation extends Window 
{
	CommonButton cButton = new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "","","Exit");

	private TextField txtFindId = new TextField();
	private TextField txtIdBack = new TextField();

	boolean isUpdate=false;
	boolean isNew=false;

	private AbsoluteLayout mainLayout;

	private Label lblCommon;

	private TextRead txtGroupId;
	private TextField txtGroupName;

	private Label lblIsActive;
	private NativeSelect cmbIsActive;

	SessionBean sessionBean;

	ArrayList<Component> allComp = new ArrayList<Component>();
	private static final String[] status = new String[] { "Inactive", "Active" };

	private boolean isFind = false;

	public GroupInformation(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setResizable(false);
		this.setCaption("GROUP INFORMATION :: "+sessionBean.getCompany());

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

				txtGroupId.setValue(selectMaxId());
				txtGroupName.focus();
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

		txtGroupName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtGroupName.getValue().toString().isEmpty())
				{
					if(duplicateMainGroup() && !isFind)
					{
						showNotification("Warning!","Group name already exist.",Notification.TYPE_WARNING_MESSAGE);
						txtGroupName.setValue("");
						txtGroupName.focus();
					}
				}
			}
		});
	}

	private void formValidation()
	{
		if(!txtGroupName.getValue().toString().isEmpty())
		{
			saveButtonEvent();
		}
		else
		{
			showNotification("Warning!","Provide Group Name.", Notification.TYPE_WARNING_MESSAGE);
			txtGroupName.focus();
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
				String query = "select * from tbGroupInfo where vGroupName" +
						" like '"+txtGroupName.getValue().toString()+"'";

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
		Window win = new HrmSetupFindWindow(sessionBean, txtFindId, "GROUP");
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
			String query = " Select isnull(max(cast(SUBSTRING(vGroupId,4,10) as int)),0)+1 from tbGroupInfo ";

			Iterator <?> iter = session.createSQLQuery(query).list().iterator();

			if (iter.hasNext()) 
			{
				String srt = iter.next().toString();

				maxId = "GRP"+srt;
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		finally{session.close();}

		return maxId;
	}

	private void findInitialise(String desigId) 
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "select vGroupId,vGroupName,isActive from tbGroupInfo Where vGroupId = '"+desigId+"'";

			List <?> led = session.createSQLQuery(sql).list();

			if (led.iterator().hasNext()) 
			{
				Object[] element = (Object[]) led.iterator().next();

				txtGroupId.setValue(element[0].toString());
				txtGroupName.setValue(element[1].toString());
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
		if(!txtGroupId.getValue().toString().isEmpty())
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
			String Insert = " INSERT into tbGroupInfo (vGroupId,vGroupName," +
					" isActive,vUserName,vUserIp,dEntryTime) values(" +
					" '"+selectMaxId()+"'," +
					" '"+txtGroupName.getValue().toString().trim()+"'," +
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
			String query = " Select * from tbUdGroupInfo where vGroupId = " +
					"'"+txtFindId.getValue().toString()+"' and vUdFlag = 'New' ";

			Iterator <?> iter = session.createSQLQuery(query).list().iterator();

			if(!iter.hasNext())
			{
				//Insert new value for first time update
				String Insert = " INSERT into tbUdGroupInfo (vGroupId,vGroupName," +
						" isActive,vUserName,vUserIp,dEntryTime,vUdFlag)" +
						" select vGroupId,vGroupName," +
						" isActive,vUserName,vUserIp,dEntryTime,'New' from tbGroupInfo" +
						" where vGroupId = '"+txtFindId.getValue().toString()+"' ";
				session.createSQLQuery(Insert).executeUpdate();
			}

			// Main table update
			String update = "UPDATE tbGroupInfo set" +
					" vGroupName = '"+txtGroupName.getValue().toString().trim()+"' ," +
					" isActive = '"+cmbIsActive.getValue().toString()+"' " +
					" where vGroupId = '"+txtFindId.getValue().toString()+"' ";
			session.createSQLQuery(update).executeUpdate();

			//Insert update or changes data
			String Insert = " INSERT into tbUdGroupInfo (vGroupId,vGroupName," +
					" isActive,vUserName,vUserIp,dEntryTime,vUdFlag) values(" +
					" '"+txtFindId.getValue().toString()+"'," +
					" '"+txtGroupName.getValue().toString().trim()+"'," +
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

	private void focusEnter()
	{
		allComp.add(txtGroupName);
		allComp.add(cmbIsActive);

		allComp.add(cButton.btnSave);

		new FocusMoveByEnter(this,allComp);
	}

	private void txtClear()
	{
		txtGroupId.setValue("");
		txtGroupName.setValue("");
		cmbIsActive.setValue(1);
	}

	public void txtInit(boolean t)
	{
		txtGroupId.setEnabled(!t);
		txtGroupName.setEnabled(!t);
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
		mainLayout.setWidth("550px");
		mainLayout.setHeight("160px");
		mainLayout.setMargin(false);

		lblCommon = new Label("Group Id :");
		lblCommon.setImmediate(false);
		lblCommon.setWidth("-1px");
		lblCommon.setHeight("-1px");
		mainLayout.addComponent(lblCommon, "top:20.0px; left:50.0px;");

		txtGroupId = new TextRead();
		txtGroupId.setImmediate(true);
		txtGroupId.setWidth("60px");
		txtGroupId.setHeight("24px");
		mainLayout.addComponent(txtGroupId, "top:18.0px;left:180.5px;");

		lblCommon = new Label("Group Name :");
		mainLayout.addComponent(lblCommon, "top:50.0px; left:50.0px;");

		txtGroupName = new TextField();
		txtGroupName.setImmediate(true);
		txtGroupName.setWidth("320px");
		txtGroupName.setHeight("-1px");
		mainLayout.addComponent(txtGroupName, "top:48.0px; left:180.0px;");

		lblIsActive = new Label("Status :");
		lblIsActive.setImmediate(true);
		lblIsActive.setWidth("-1px");
		lblIsActive.setHeight("-1px");
		mainLayout.addComponent(lblIsActive, "top:80.0px; left:50.0px;");

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
		mainLayout.addComponent(cmbIsActive,"top:78.0px; left:180.0px;");

		mainLayout.addComponent(cButton,"top:117px; left:18px;");

		return mainLayout;
	}
}
