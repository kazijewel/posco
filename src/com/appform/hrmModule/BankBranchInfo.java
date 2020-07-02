package com.appform.hrmModule;

import java.util.ArrayList;
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
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

public class BankBranchInfo extends Window 
{
	CommonButton cButton = new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "","","Exit");

	private TextField txtFindId = new TextField();
	//private TextField txtPartyIdBack = new TextField();
	boolean isUpdate = false;
	boolean isNew = false;

	private AbsoluteLayout mainLayout;

	private Label lblCommon;

	private TextRead txtBranchID;
	private TextField txtBranchName;

	SessionBean sessionBean;
	private String  menuId = "";
	private CommonMethod cm;

	ArrayList<Component> allComp = new ArrayList<Component>();

	private boolean isFind = false;
	
	public BankBranchInfo(SessionBean sessionBean, String menuId)
	{
		this.sessionBean=sessionBean;
		this.setResizable(false);
		this.setCaption("BRANCH INFORMATION :: "+sessionBean.getCompany());
		this.menuId = menuId;
		cm = new CommonMethod(sessionBean);
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

				txtBranchID.setValue(selectHeadId());
				txtBranchName.focus();
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

		txtBranchName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtBranchName.getValue().toString().isEmpty())
				{
					if(duplicateDepartmentName() && !isFind)
					{
						showNotification("Warning!","Branch Name already exist.",Notification.TYPE_WARNING_MESSAGE);
						txtBranchName.setValue("");
						txtBranchName.focus();
					}
				}
			}
		});
	}

	private void formValidation()
	{
		if(!txtBranchName.getValue().toString().isEmpty())
		{
			saveButtonEvent();
		}
		else
		{
			showNotification("Warning!","Provide Branch Name.", Notification.TYPE_WARNING_MESSAGE);
			txtBranchName.focus();
		}
	}

	private boolean duplicateDepartmentName()
	{
		boolean ret = false;
		if(!isUpdate)
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			Transaction tx = session.beginTransaction();

			try
			{
				String query = "select * from tbBankBranch where branchName" +
						" like '"+txtBranchName.getValue().toString()+"'";

				Iterator iter = session.createSQLQuery(query).list().iterator();

				if (iter.hasNext()) 
				{
					ret = true;
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
		}
		return ret;
	}

	private void findButtonEvent() 
	{
		Window win = new BranchInfoFindWindow(sessionBean, txtFindId);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if (txtFindId.getValue().toString().length() > 0)
				{
					txtClear();
					//txtPartyIdBack.setValue(txtFindId.getValue().toString());
					findInitialise(txtFindId.getValue().toString());
				}
			}
		});
		this.getParent().addWindow(win);	
	}

	private String selectHeadId()
	{
		String DepartmentId = "";
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{
			String query = " Select isnull(max(cast(id as int)),0)+1 from tbBankBranch ";

			Iterator iter = session.createSQLQuery(query).list().iterator();

			if (iter.hasNext()) 
			{
				DepartmentId = iter.next().toString();
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

		return DepartmentId;
	}

	private void findInitialise(String BranchId) 
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();

		String sql = null;
		try 
		{
			sql = "select id,branchName from tbBankBranch Where id = '"+BranchId+"'";
			System.out.println(sql);

			List led = session.createSQLQuery(sql).list();

			if (led.iterator().hasNext()) 
			{
				Object[] element = (Object[]) led.iterator().next();

				txtBranchID.setValue(element[0].toString());
				txtBranchName.setValue(element[1].toString());
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
		if(!txtBranchID.getValue().toString().isEmpty())
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
			String Insert = " INSERT into tbBankBranch(id,branchName,vUserName,vUserIp,dEntryTime) values(" +
					" '"+selectHeadId()+"'," +
					" '"+txtBranchName.getValue().toString().trim()+"'," +
					" '"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP) ";
			session.createSQLQuery(Insert).executeUpdate();

			tx.commit();
			showNotification("All information saved successfully.");
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

			// Main table update
			String updateUnit = "UPDATE tbBankBranch set" +
					" branchName = '"+txtBranchName.getValue().toString().trim()+"'" +
					" where id = '"+txtFindId.getValue().toString()+"' ";

			System.out.println("updateUnit: "+updateUnit);
			session.createSQLQuery(updateUnit).executeUpdate();

			txtFindId.setValue("");

			showNotification("All information update successfully.");

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
		allComp.add(txtBranchName);
		allComp.add(cButton.btnSave);

		new FocusMoveByEnter(this,allComp);
	}

	private void txtClear()
	{
		txtBranchID.setValue("");
		txtBranchName.setValue("");
	}

	public void txtInit(boolean t)
	{
		//txtBranchID.setEnabled(!t);
		txtBranchName.setEnabled(!t);
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
		mainLayout.setWidth("570px");
		mainLayout.setHeight("200px");
		mainLayout.setMargin(false);

		lblCommon = new Label("Branch ID:");
		lblCommon.setImmediate(false);
		lblCommon.setWidth("-1px");
		lblCommon.setHeight("-1px");
		mainLayout.addComponent(lblCommon, "top:40.0px; left:50.0px;");

		txtBranchID = new TextRead();
		txtBranchID.setImmediate(true);
		txtBranchID.setWidth("60px");
		txtBranchID.setHeight("24px");
		txtBranchID.setEnabled(false);
		mainLayout.addComponent(txtBranchID, "top:38.0px;left:180.5px;");

		lblCommon = new Label("Branch Name:");
		mainLayout.addComponent(lblCommon, "top:80.0px; left:50.0px;");

		txtBranchName = new TextField();
		txtBranchName.setImmediate(true);
		txtBranchName.setWidth("320px");
		txtBranchName.setHeight("-1px");
		mainLayout.addComponent(txtBranchName, "top:78.0px; left:180.0px;");

		mainLayout.addComponent(cButton,"top:150px; left:18px;");

		return mainLayout;
	}
}
