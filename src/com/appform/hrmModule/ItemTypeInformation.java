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
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Button.ClickShortcut;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class ItemTypeInformation extends Window 
{
	CommonButton cButton = new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "","","Exit");

	private TextField txtFindId = new TextField();
	boolean isUpdate = false;
	boolean isNew = false;
	private AbsoluteLayout mainLayout;
	private Label lblCommon;
	private TextRead txtItemTypeID;
	private TextField txtItemTypeName;
	private TextField txtPartyIdBack = new TextField();
	private ComboBox cmbUnit;
	

	private Label lblIsActive;
	private NativeSelect cmbIsActive;

	SessionBean sessionBean;
	private CommonMethod cm;
	private String menuId = "";

	private boolean isFind = false;
	String unitList[]={"Pcs","No","Box","Packet","Set","Inch","Yard","Sft.","Ft.","Cft.","Rft.","Mtr.","Ltr.","Kgs","Pound","Lbs","gm","Reel","Roll","Coli","Pair","Crt.","Ctn.","Clip","Doz.","Bottle","Can"};
	
	ArrayList<Component> allComp = new ArrayList<Component>();
	private static final String[] status = new String[] { "Inactive", "Active" };
	
	
	public ItemTypeInformation(SessionBean sessionBean,String menuId)
	{
		this.sessionBean=sessionBean;
		this.setResizable(false);
		this.setCaption("ITEM INFORMATION :: "+sessionBean.getCompany());
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
				txtItemTypeID.setValue(selectHeadId());
				txtItemTypeName.focus();
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

		txtItemTypeName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtItemTypeName.getValue().toString().trim().isEmpty())
				{
					if(duplicateItemTypeName())
					{
						showNotification("Warning!","ItemType already exist.",Notification.TYPE_WARNING_MESSAGE);
						txtItemTypeName.setValue("");
						txtItemTypeName.focus();
					}
				}
			}
		});
	}

	private void formValidation()
	{
		if(!txtItemTypeName.getValue().toString().isEmpty())
		{
			saveButtonEvent();
		}
		else
		{
			showNotification("Warning!","Provide ItemType Name.", Notification.TYPE_WARNING_MESSAGE);
			txtItemTypeName.focus();
		}
	}

	private boolean duplicateItemTypeName()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select vItemTypeName from tbItemTypeInfo where vItemTypeName" +
					" like '"+txtItemTypeName.getValue().toString().trim()+"'";
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
		Window win = new ItemTypeFindWindow(sessionBean, txtFindId, "Item");
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

	private String selectHeadId()
	{
		String ItemTypeId = "";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = " Select isnull(max(cast(SUBSTRING(vItemTypeId,5,LEN(vItemTypeId)) as int)),0)+1 from tbItemTypeInfo ";
			Iterator<?> iter = session.createSQLQuery(query).list().iterator();
			if(iter.hasNext())
			{
				ItemTypeId = "ITEM"+iter.next().toString();
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

		return ItemTypeId;
	}

	private void findInitialise(String ItemTypeId) 
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "select vItemTypeId,vItemTypeName,vUnit,isActive from tbItemTypeInfo Where" +
					" vItemTypeId = '"+ItemTypeId+"'";
			List<?> led = session.createSQLQuery(sql).list();
			if(led.iterator().hasNext())
			{
				Object[] element = (Object[]) led.iterator().next();

				txtItemTypeID.setValue(element[0].toString());
				txtItemTypeName.setValue(element[1].toString());
				cmbUnit.setValue(element[2].toString());
				cmbIsActive.setValue(element[3]);
				
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
		if(!txtItemTypeID.getValue().toString().trim().isEmpty())
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
			String Insert = " INSERT into tbItemTypeInfo (vItemTypeId,vItemTypeName, " +
					" vUnit,isActive,vUserName,vUserIp,dEntryTime) values(" +
					" '"+selectHeadId()+"'," +
					" '"+txtItemTypeName.getValue().toString().trim()+"'," +
					" '"+cmbUnit.getValue()+"'," +
					" '"+cmbIsActive.getValue()+"'," +
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

			String udInsertQuery = "insert into tbUDItemTypeInfo (vItemTypeId,vItemTypeName,vUnit,isActive," +
					"vUDFlag,vUserName,vUserIp,dEntryTime) select vItemTypeId,vItemTypeName,vUnit,isActive," +
					"'OLD',vUserName,vUserIp,dEntryTime from tbItemTypeInfo where " +
					"vItemTypeId = '"+txtItemTypeID.getValue().toString().trim()+"'";
			session.createSQLQuery(udInsertQuery).executeUpdate();

			// Main table update
			String updateUnit = "UPDATE tbItemTypeInfo set" +
					" vItemTypeName = '"+txtItemTypeName.getValue().toString().trim()+"'," +
					" vUnit = '"+cmbUnit.getValue()+"'," +
					" isActive = '"+cmbIsActive.getValue()+"'," +
					" vUserName = '"+sessionBean.getUserName()+"'," +
					" vUserIp = '"+sessionBean.getUserIp()+"'," +
					" dEntryTime = GETDATE()" +
					" where vItemTypeId = '"+txtFindId.getValue().toString()+"' ";

			session.createSQLQuery(updateUnit).executeUpdate();

			udInsertQuery = "insert into tbUDItemTypeInfo (vItemTypeId,vItemTypeName,vUnit,isActive," +
					"vUDFlag,vUserName,vUserIp,dEntryTime) select vItemTypeId,vItemTypeName,vUnit,isActive," +
					"'UPDATE',vUserName,vUserIp,dEntryTime from tbItemTypeInfo where " +
					"vItemTypeId = '"+txtItemTypeID.getValue().toString().trim()+"'";
			session.createSQLQuery(udInsertQuery).executeUpdate();

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
		allComp.add(txtItemTypeName);
		allComp.add(cmbUnit);
		allComp.add(cButton.btnSave);
		new FocusMoveByEnter(this,allComp);
	}

	private void txtClear()
	{
		txtItemTypeID.setValue("");
		txtItemTypeName.setValue("");
		cmbUnit.setValue(null);
		cmbUnit.setValue(1);
	}

	public void txtInit(boolean t)
	{
		txtItemTypeID.setEnabled(!t);
		txtItemTypeName.setEnabled(!t);
		cmbIsActive.setEnabled(!t);
		cmbUnit.setEnabled(!t);
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

		lblCommon = new Label("Item ID:");
		lblCommon.setImmediate(false);
		lblCommon.setWidth("-1px");
		lblCommon.setHeight("-1px");
		mainLayout.addComponent(lblCommon, "top:20.0px; left:50.0px;");

		txtItemTypeID = new TextRead();
		txtItemTypeID.setImmediate(true);
		txtItemTypeID.setWidth("60px");
		txtItemTypeID.setHeight("24px");
		mainLayout.addComponent(txtItemTypeID, "top:18.0px;left:180.5px;");

		lblCommon = new Label("Item Name :");
		mainLayout.addComponent(lblCommon, "top:50.0px; left:50.0px;");

		txtItemTypeName = new TextField();
		txtItemTypeName.setImmediate(true);
		txtItemTypeName.setWidth("320px");
		txtItemTypeName.setHeight("-1px");
		mainLayout.addComponent(txtItemTypeName, "top:48.0px; left:180.0px;");

		lblCommon = new Label("Unit :");
		mainLayout.addComponent(lblCommon, "top:80.0px; left:50.0px;");
		cmbUnit=new ComboBox();
		cmbUnit.setWidth("100px");
		cmbUnit.setHeight("-1px");
		cmbUnit.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbUnit, "top:78.0px; left:180.0px;");
		for(int i=0;i<unitList.length;i++)
		{
			cmbUnit.addItem(unitList[i]);
		}
		
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
		
		

		mainLayout.addComponent(cButton,"top:150px; left:18px;");
		return mainLayout;
	}
}
