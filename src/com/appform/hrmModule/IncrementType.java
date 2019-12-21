package com.appform.hrmModule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

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
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class IncrementType extends Window 
{
	CommonButton cButton = new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "","","Exit");

	private TextField txtFindId = new TextField();
	boolean isUpdate = false;
	boolean isNew = false;
	private AbsoluteLayout mainLayout;
	private Label lblIncrementId;
	private Label lblIncrementName;
	private TextRead txtIncrementID;
	private TextField txtIncrementName;
	private TextField txtPartyIdBack = new TextField();

	SessionBean sessionBean;
	private CommonMethod cm;
	private String menuId = "";
	ArrayList<Component> allComp = new ArrayList<Component>();
	private boolean isFind = false;

	public IncrementType(SessionBean sessionBean,String menuId)
	{
		this.sessionBean=sessionBean;
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		this.setCaption("INCREMENT TYPE :: "+sessionBean.getCompany());
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
				txtIncrementID.setValue(selectHeadId());
				//txtIncrementID.;
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
				updateButtonEvent();
			}
		});

		cButton.btnRefresh.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isFind = false;
				isUpdate = false;
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

		txtIncrementName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtIncrementName.getValue().toString().trim().isEmpty())
				{
					if(duplicateIncrementName())
					{
						showNotification("Warning!","Increment already exist.",Notification.TYPE_WARNING_MESSAGE);
						txtIncrementName.setValue("");
						txtIncrementName.focus();
					}
				}
			}
		});

	}

	private void formValidation()
	{
		if(!txtIncrementName.getValue().toString().isEmpty())
		{
			saveButtonEvent();
		}
		else
		{
			showNotification("Warning!","Provide Increment Name.", Notification.TYPE_WARNING_MESSAGE);
			txtIncrementName.focus();
		}
	}
	private boolean duplicateIncrementName()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select vIncrementType from tbIncrementName where vIncrementType " +
					" like '"+txtIncrementName.getValue().toString().trim()+"'";
			Iterator<?> iter = session.createSQLQuery(query).list().iterator();
			if(iter.hasNext() && !isFind) 
			{
				return true;
			}
		}
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		finally
		{
			session.close();
		}
		return false;
	}

	private void findButtonEvent() 
	{
		Window win = new IncrementFind(sessionBean, txtFindId, "INCREMENT");
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if (txtFindId.getValue().toString().length() > 0)
				{
					txtClear();
					txtIncrementID.setValue(txtFindId.getValue().toString());
					findInitialise(txtFindId.getValue().toString());
				}
			}
		});
		this.getParent().addWindow(win);	
	}

	private String selectHeadId()
	{
		String incrementId = "";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "Select isnull(max(cast(SUBSTRING(vIncrementId,4,LEN(vIncrementId)) as int)),0)+1 from tbIncrementName ";
			Iterator<?> iter = session.createSQLQuery(query).list().iterator();
			if(iter.hasNext())
			{
				incrementId = "INC"+iter.next().toString();
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		finally
		{
			session.close();
		}

		return incrementId;
	}

	private void findInitialise(String incrementId) 
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "select vIncrementId,vIncrementType from tbIncrementName where" +
					"  vIncrementId = '"+txtIncrementID.getValue().toString().trim()+"'";
			List<?> led = session.createSQLQuery(sql).list();
			if(led.iterator().hasNext())
			{
				Object[] element = (Object[]) led.iterator().next();

				txtIncrementID.setValue(element[0].toString());
				txtIncrementName.setValue(element[1].toString());
			}
			isFind = false;
		}
		catch (Exception exp) 
		{
			showNotification("findInitialise", exp + "",Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			session.close();
		}
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
		if(!txtIncrementID.getValue().toString().trim().isEmpty())
		{
			btnIni(false);
			txtInit(false);
			isUpdate = true;
			isFind = false;
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
			String Insert = " INSERT into tbIncrementName (vIncrementId,vIncrementType, " +
					" vUserName,vUserIp,dEntryTime) values(" +
					" '"+selectHeadId()+"', " +
					" '"+txtIncrementName.getValue().toString().trim()+"'," +
					" '"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP) ";
			session.createSQLQuery(Insert).executeUpdate();

			tx.commit();
			
			Notification n=new Notification("All Information Save Successfully!","",Notification.TYPE_TRAY_NOTIFICATION);
			n.setPosition(Notification.POSITION_TOP_RIGHT);
			showNotification(n);
		}
		catch(Exception exp)
		{
			showNotification("insertData",exp+"",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
		finally
		{
			session.close();
		}
	}

	public boolean updateData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{

			String udInsertQuery = "insert into tbUdIncrementType (vIncrementId,vIncrementType, " +
					"vUDFlag,vUserName,vUserIp,dEntryTime) select vIncrementId,vIncrementType," +
					"'OLD',vUserName,vUserIp,dEntryTime from tbIncrementName where " +
					"vIncrementId = '"+txtIncrementID.getValue().toString().trim()+"'";
			session.createSQLQuery(udInsertQuery).executeUpdate();
			
			System.out.print(udInsertQuery);

			// Main table update
			String updateUnit = "UPDATE tbIncrementName set" +
					" vIncrementType = '"+txtIncrementName.getValue().toString().trim()+"'," +
					" vUserName = '"+sessionBean.getUserName()+"'," +
					" vUserIp = '"+sessionBean.getUserIp()+"'," +
					" dEntryTime = GETDATE()" +
					" where vIncrementId = '"+txtFindId.getValue().toString()+"' ";

			System.out.println(updateUnit);

			session.createSQLQuery(updateUnit).executeUpdate();

			udInsertQuery = "insert into tbUdIncrementType (vIncrementId,vIncrementType," +
					"vUDFlag,vUserName,vUserIp,dEntryTime) select vIncrementId,vIncrementType," +
					"'UPDATE',vUserName,vUserIp,dEntryTime from tbIncrementName where " +
					"vIncrementId = '"+txtIncrementID.getValue().toString().trim()+"'";
			session.createSQLQuery(udInsertQuery).executeUpdate();
			
			
			System.out.print(udInsertQuery);
			txtFindId.setValue("");
			Notification n=new Notification("All Information Updated Successfully!","",Notification.TYPE_TRAY_NOTIFICATION);
			n.setPosition(Notification.POSITION_TOP_RIGHT);
			showNotification(n);
			tx.commit();
		}
		catch(Exception exp)
		{
			tx.rollback();
			showNotification("updateData",exp+"",Notification.TYPE_ERROR_MESSAGE);
			return false;
		}
		finally
		{
			session.close();
		}
		return true;
	}

	private void focusEnter()
	{
		allComp.add(txtIncrementName);
		allComp.add(cButton.btnSave);
		new FocusMoveByEnter(this,allComp);
	}

	private void txtClear()
	{
		txtIncrementID.setValue("");
		txtIncrementName.setValue("");
	}

	public void txtInit(boolean t)
	{
		txtIncrementID.setEnabled(!t);
		txtIncrementName.setEnabled(!t);
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

		lblIncrementId = new Label("Increment ID:");
		lblIncrementId.setImmediate(false);
		lblIncrementId.setWidth("-1px");
		lblIncrementId.setHeight("-1px");
		mainLayout.addComponent(lblIncrementId, "top:40.0px; left:50.0px;");

		txtIncrementID = new TextRead();
		txtIncrementID.setImmediate(true);
		txtIncrementID.setWidth("60px");
		txtIncrementID.setHeight("24px");
		mainLayout.addComponent(txtIncrementID, "top:38.0px;left:180.5px;");

		lblIncrementName = new Label("Increment Type :");
		mainLayout.addComponent(lblIncrementName, "top:80.0px; left:50.0px;");

		txtIncrementName = new TextField();
		txtIncrementName.setImmediate(true);
		txtIncrementName.setWidth("320px");
		txtIncrementName.setHeight("-1px");
		mainLayout.addComponent(txtIncrementName, "top:78.0px; left:180.0px;");

		mainLayout.addComponent(cButton,"top:150px; left:18px;");
		return mainLayout;
	}
}
