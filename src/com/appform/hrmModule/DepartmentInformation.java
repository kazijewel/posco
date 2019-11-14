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
public class DepartmentInformation extends Window 
{
	CommonButton cButton = new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "","","Exit");

	private TextField txtFindId = new TextField();

	boolean isUpdate=false;
	boolean isNew=false;

	private AbsoluteLayout mainLayout;

	private Label lblCommon;

	private AmountField txtDepartmentSerial;
	private TextRead txtDepartmentID;
	private TextField txtDepartmentName;

	private Label lblIsActive;
	private NativeSelect cmbIsActive;

	private TextField txtPartyIdBack = new TextField();

	SessionBean sessionBean;
	private CommonMethod cm;
	private String menuId = "";
	ArrayList<Component> allComp = new ArrayList<Component>();
	private static final String[] unitStatus = new String[] { "Inactive", "Active" };

	private boolean isFind = false;
	
	public DepartmentInformation(SessionBean sessionBean,String menuId)
	{
		this.sessionBean = sessionBean;
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		this.setCaption("DEPARTMENT INFORMATION :: "+sessionBean.getCompany());

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

				txtDepartmentID.setValue(selectAutoId());
				txtDepartmentSerial.setValue(maxSerialID());
				txtDepartmentName.focus();
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

		txtDepartmentName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtDepartmentName.getValue().toString().isEmpty())
				{
					if(duplicateDepartmentName() && !isFind)
					{
						showNotification("Warning!","Department Name already exist.",Notification.TYPE_WARNING_MESSAGE);
						txtDepartmentName.setValue("");
						txtDepartmentName.focus();
					}
				}
			}
		});

		txtDepartmentSerial.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtDepartmentSerial.getValue().toString().isEmpty())
				{
					if(duplicateDepartmentSerial() && !isFind)
					{
						showNotification("Warning!","Department Serial already exist.",Notification.TYPE_WARNING_MESSAGE);
						txtDepartmentSerial.setValue("");
						txtDepartmentSerial.focus();
					}
				}
			}
		});
	}

	private void formValidation()
	{
		if(!txtDepartmentName.getValue().toString().isEmpty())
		{
			saveButtonEvent();
		}
		else
		{
			showNotification("Warning!","Provide Department Name.", Notification.TYPE_WARNING_MESSAGE);
			txtDepartmentName.focus();
		}
	}

	private boolean duplicateDepartmentName()
	{
		boolean ret = false;
		if(!isUpdate)
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String query = "select * from tbDepartmentInfo where vDepartmentName" +
						" like '"+txtDepartmentName.getValue().toString()+"'";

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

	private boolean duplicateDepartmentSerial()
	{
		boolean ret = false;
		if(!isUpdate)
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String query = "select * from tbDepartmentInfo where iDepartmentSerial" +
						" like '"+txtDepartmentSerial.getValue().toString()+"'";

				Iterator <?> iter = session.createSQLQuery(query).list().iterator();

				System.out.println("SECTION"+query);
				
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
		Window win = new DepartmentFindWindow(sessionBean, txtFindId, "DEPARTMENT");
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
			String query = "select ISNULL(MAX(CAST(Substring(vDepartmentId,5,LEN(vDepartmentId)-4) as int)),0)+1 from tbDepartmentInfo";

			Iterator <?> iter = session.createSQLQuery(query).list().iterator();
			
			System.out.println("SECTION 2"+query);

			if (iter.hasNext()) 
			{
				unitId = "DEPT"+iter.next().toString();
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		finally{session.close();}

		return unitId;
	}

	private String maxSerialID()
	{
		String serialId = "";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = " Select isnull(max(iAutoId),0)+1 from tbDepartmentInfo ";
			Iterator <?> iter = session.createSQLQuery(query).list().iterator();
			
			System.out.println("SECTION "+query);

			if (iter.hasNext()) 
			{
				serialId = iter.next().toString();
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}

		return serialId;
	}

	private void findInitialise(String sectionId) 
	{
		String sql = null;
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try 
		{
			sql = "select vDepartmentId,vDepartmentName,isActive,iDepartmentSerial,DepartmentPrefix from tbDepartmentInfo Where vDepartmentId = '"+sectionId+"'";
		
			System.out.println("SECCCC "+sql);

			List <?> led = session.createSQLQuery(sql).list();

			if (led.iterator().hasNext()) 
			{
				Object[] element = (Object[]) led.iterator().next();

				txtDepartmentID.setValue(element[0].toString());
				txtDepartmentName.setValue(element[1].toString());
				cmbIsActive.setValue(element[2]);
				txtDepartmentSerial.setValue(element[3].toString());
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
		if(!txtDepartmentID.getValue().toString().isEmpty())
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
			String sectionId=selectAutoId();
			String caption="Saved";
			if(isUpdate)
			{
				sectionId=txtDepartmentID.getValue().toString();
				String sqlDel="delete from tbDepartmentInfo where vDepartmentId='"+sectionId+"'";
				session.createSQLQuery(sqlDel).executeUpdate();
				caption="Updated";
			}
			
			String Insert = " INSERT into tbDepartmentInfo (vDepartmentId,vDepartmentName,iDepartmentSerial, " +
					" isActive,vUserName,vUserIp,dEntryTime,DepartmentPrefix) values(" +
					" '"+sectionId+"'," +
					" '"+txtDepartmentName.getValue().toString().trim()+"'," +
					" '"+txtDepartmentSerial.getValue().toString().trim()+"'," +
					" '"+cmbIsActive.getValue().toString()+"'," +
					" '"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP,'') ";
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
		allComp.add(txtDepartmentName);
		allComp.add(cmbIsActive);

		allComp.add(cButton.btnSave);

		new FocusMoveByEnter(this,allComp);
	}

	private void txtClear()
	{
		txtDepartmentID.setValue("");
		txtDepartmentSerial.setValue("");
		txtDepartmentName.setValue("");
		cmbIsActive.setValue(1);
	}

	public void txtInit(boolean t)
	{
		txtDepartmentID.setEnabled(!t);
		txtDepartmentSerial.setEnabled(!t);
		txtDepartmentName.setEnabled(!t);
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

		lblCommon = new Label("Department ID:");
		lblCommon.setImmediate(false);
		lblCommon.setWidth("-1px");
		lblCommon.setHeight("-1px");
		mainLayout.addComponent(lblCommon, "top:20.0px; left:50.0px;");

		txtDepartmentID = new TextRead();
		txtDepartmentID.setImmediate(true);
		txtDepartmentID.setWidth("60px");
		txtDepartmentID.setHeight("24px");
		mainLayout.addComponent(txtDepartmentID, "top:18.0px;left:180.5px;");

		lblCommon = new Label("Department Name :");
		mainLayout.addComponent(lblCommon, "top:50.0px; left:50.0px;");

		txtDepartmentName = new TextField();
		txtDepartmentName.setImmediate(true);
		txtDepartmentName.setWidth("320px");
		txtDepartmentName.setHeight("-1px");
		mainLayout.addComponent(txtDepartmentName, "top:48.0px; left:180.0px;");

		lblCommon = new Label("Department Serial:");
		//mainLayout.addComponent(lblCommon, "top:80.0px; left:50.0px;");

		txtDepartmentSerial = new AmountField();
		txtDepartmentSerial.setImmediate(true);
		txtDepartmentSerial.setWidth("40px");
		txtDepartmentSerial.setHeight("-1px");
		mainLayout.addComponent(txtDepartmentSerial, "top:78.0px;left:180.5px;");
		txtDepartmentSerial.setVisible(false);

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
