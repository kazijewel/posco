package com.appform.hrmModule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
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
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class ProjectInformation extends Window 
{
	CommonButton cButton = new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "","","Exit");

	private TextField txtFindId = new TextField();

	boolean isUpdate=false;
	boolean isNew=false;

	private AbsoluteLayout mainLayout;

	private Label lblCommon;

	private TextRead txtUnitId;
	private TextField txtUnitName;

	private Label lblIsActive;
	private NativeSelect cmbIsActive;

	private TextField txtPartyIdBack = new TextField();

	SessionBean sessionBean;
	private CommonMethod cm;
	private String menuId = "";
	ArrayList<Component> allComp = new ArrayList<Component>();
	private static final String[] unitStatus = new String[] { "Inactive", "Active" };

	private boolean isFind = false;

	public ProjectInformation(SessionBean sessionBean,String menuId)
	{
		this.sessionBean = sessionBean;
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		this.setCaption("PROJECT INFORMATION :: "+sessionBean.getCompany());
		buildMainLayout();
		setContent(mainLayout);
		txtInit(true);
		btnIni(true);
		focusEnter();
		btnAction();
		setButtonShortCut();
		cButton.btnNew.focus();
		authenticationCheck();
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

				txtUnitId.setValue(selectAutoId());
				txtUnitName.focus();
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

		txtUnitName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtUnitName.getValue().toString().isEmpty())
				{
					if(duplicateUnitName() && !isFind)
					{
						showNotification("Warning!","Project Name already exist.",Notification.TYPE_WARNING_MESSAGE);
						txtUnitName.setValue("");
						txtUnitName.focus();
					}
				}
			}
		});
	}

	private void formValidation()
	{
		if(!txtUnitName.getValue().toString().isEmpty())
		{
			saveButtonEvent();
		}
		else
		{
			showNotification("Warning!","Provide Project Name.", Notification.TYPE_WARNING_MESSAGE);
			txtUnitName.focus();
		}
	}

	private boolean duplicateUnitName()
	{
		boolean ret = false;
		if(!isUpdate)
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String query = "select * from tbUnitInfo where vUnitName " +
						" like '"+txtUnitName.getValue().toString()+"'";

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
		Window win = new ProjectInfoFindWindow(sessionBean, txtFindId, "UNIT");
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

	private String selectAutoId()
	{
		String unitId = "";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = " Select ISNULL(MAX(CAST(vUnitId as bigint)),CAST(SUBSTRING(CONVERT(varchar,YEAR(getDate())),3,4)+'000' as bigint))+1 from tbUnitInfo ";

			Iterator <?> iter = session.createSQLQuery(query).list().iterator();
			
			System.out.println("SECTION 2"+query);

			if (iter.hasNext()) 
			{
				unitId = ""+iter.next().toString();
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		finally{session.close();}

		return unitId;
	}

	private void findInitialise(String unitId) 
	{
		String sql = null;
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try 
		{
			sql = "select vUnitId,vUnitName,isActive from tbUnitInfo where vUnitId='"+unitId+"' ";
		
			System.out.println("SECCCC "+sql);

			List <?> led = session.createSQLQuery(sql).list();

			if (led.iterator().hasNext()) 
			{
				Object[] element = (Object[]) led.iterator().next();

				txtUnitId.setValue(element[0].toString());
				txtUnitName.setValue(element[1].toString());
				cmbIsActive.setValue(element[2]);
			}

			isFind = false;
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
		isNew=false;
	}

	private void updateButtonEvent()
	{
		if(!txtUnitId.getValue().toString().isEmpty())
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
		String caption="Do you want to save information?";
		if(isUpdate)
		{
			caption="Do you want to update information?";
		}
		MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, caption, new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
		mb.setStyleName("cwindowMB");
		mb.show(new EventListener()
		{
			public void buttonClicked(ButtonType buttonType)
			{
				if(buttonType == ButtonType.YES)
				{
					Session session=SessionFactoryUtil.getInstance().openSession();
					Transaction tx=session.beginTransaction();
					insertData(session,tx);
					btnIni(true);
					txtInit(true);
					txtClear();
					cButton.btnNew.focus();
					isUpdate=false;
					isFind = false;
				}
			}
		});
	
	}

	private void insertData(Session session,Transaction tx)
	{
		try
		{
			String unitId=selectAutoId();
			String caption="Saved";
			if(isUpdate)
			{
				unitId=txtUnitId.getValue().toString();
				String sqlDel="delete from tbUnitInfo where vUnitId='"+unitId+"'";
				session.createSQLQuery(sqlDel).executeUpdate();
				caption="Updated";
			}
			
			String Insert = "insert into tbUnitInfo(vUnitId,vUnitName,phoneNo,fax,email,address,userId,userIp,entryTime,imageLoc,isActive,vUserName)"+
					" values('"+unitId+"','"+txtUnitName.getValue().toString()+"','','','','','"+sessionBean.getUserId()+"','',GETDATE(),"
				  + " '','"+cmbIsActive.getValue().toString()+"','"+sessionBean.getUserName()+"')";
			session.createSQLQuery(Insert).executeUpdate();

			tx.commit();
			
			Notification n=new Notification("All Information "+caption+" Successfully!","",Notification.TYPE_TRAY_NOTIFICATION);
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

	private void focusEnter()
	{
		allComp.add(txtUnitName);
		allComp.add(cmbIsActive);

		allComp.add(cButton.btnSave);

		new FocusMoveByEnter(this,allComp);
	}

	private void txtClear()
	{
		txtUnitId.setValue("");
		txtUnitName.setValue("");
		cmbIsActive.setValue(1);
	}

	public void txtInit(boolean t)
	{
		txtUnitId.setEnabled(!t);
		txtUnitName.setEnabled(!t);
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
		mainLayout.setHeight("220px");
		mainLayout.setMargin(false);

		lblCommon = new Label("Project ID:");
		lblCommon.setImmediate(false);
		lblCommon.setWidth("-1px");
		lblCommon.setHeight("-1px");
		mainLayout.addComponent(lblCommon, "top:20.0px; left:50.0px;");

		txtUnitId = new TextRead();
		txtUnitId.setImmediate(true);
		txtUnitId.setWidth("60px");
		txtUnitId.setHeight("24px");
		mainLayout.addComponent(txtUnitId, "top:18.0px;left:180.5px;");

		lblCommon = new Label("Project Name :");
		mainLayout.addComponent(lblCommon, "top:50.0px; left:50.0px;");

		txtUnitName = new TextField();
		txtUnitName.setImmediate(true);
		txtUnitName.setWidth("320px");
		txtUnitName.setHeight("-1px");
		mainLayout.addComponent(txtUnitName, "top:48.0px; left:180.0px;");

	

		lblIsActive = new Label("Status :");
		lblIsActive.setImmediate(true);
		lblIsActive.setWidth("-1px");
		lblIsActive.setHeight("-1px");
		mainLayout.addComponent(lblIsActive, "top:80px; left:50.0px;");

		cmbIsActive = new NativeSelect();
		cmbIsActive.setNullSelectionAllowed(false);
		cmbIsActive.setImmediate(true);
		cmbIsActive.setWidth("80px");
		cmbIsActive.setHeight("-1px");
		for (int i = 0; i < unitStatus.length; i++)
		{
			cmbIsActive.addItem(i);
			cmbIsActive.setItemCaption(i, unitStatus[i]);
		}
		cmbIsActive.setValue(1);
		mainLayout.addComponent(cmbIsActive,"top:78px; left:180.0px;");

		mainLayout.addComponent(cButton,"top:170px; left:18px;");

		return mainLayout;
	}
}
