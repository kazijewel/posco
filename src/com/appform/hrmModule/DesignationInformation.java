package com.appform.hrmModule;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Button.ClickShortcut;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class DesignationInformation extends Window 
{
	CommonButton cButton = new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "","","Exit");

	private TextField txtFindId = new TextField();
	private TextField txtIdBack = new TextField();

	boolean isUpdate=false;
	boolean isNew=false;

	private AbsoluteLayout mainLayout;

	private Label lblCommon;

	private TextRead txtDesignationId;
	private TextField txtDesignation;
	private AmountField txtDesignationSerial;
	private ComboBox cmbDesignationRank;

	private Label lblIsActive;
	private NativeSelect cmbIsActive;
	public NativeButton btnPlusRank;

	SessionBean sessionBean;
	private CommonMethod cm;
	private String menuId = "";
	ArrayList<Component> allComp = new ArrayList<Component>();
	private static final String[] status = new String[] { "Inactive", "Active" };
	//private static final List<String> rank =Arrays.asList( new String[] {"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19"});

	private boolean isFind = false;

	public DesignationInformation(SessionBean sessionBean,String menuId)
	{
		this.sessionBean=sessionBean;
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		this.setCaption("DESIGNATION INFORMATION :: "+sessionBean.getCompany());

		buildMainLayout();
		setContent(mainLayout);

		txtInit(true);
		btnIni(true);

		focusEnter();
		btnAction();

		authenticationCheck();
		setButtonShortCut();
		addRank();
		cButton.btnNew.focus();
	}
	public void addRank()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql="";
			
			sql = "SELECT vRankId,iRankSerial FROM tbDesignationRankInfo "
					+ "where isActive = 1 order by iRankSerial";
			
			List <?> lst = session.createSQLQuery(sql).list();
			
			//System.out.println("addRank: "+sql);
			
			if(!lst.isEmpty())
			{
				Iterator <?> itr=lst.iterator();
				while(itr.hasNext())
				{
					Object[] element=(Object[])itr.next();
					cmbDesignationRank.addItem(element[1]);
					cmbDesignationRank.setItemCaption(element[1], element[1].toString());
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("addRank", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
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

				txtDesignationId.setValue(selectMaxId());
				txtDesignation.focus();
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

		txtDesignation.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtDesignation.getValue().toString().isEmpty())
				{
					if(duplicateMainGroup() && !isFind)
					{
						showNotification("Warning!","Designation already exist.",Notification.TYPE_WARNING_MESSAGE);
						txtDesignation.setValue("");
						txtDesignation.focus();
					}
				}
			}
		});

		txtDesignationSerial.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtDesignationSerial.getValue().toString().isEmpty())
				{
					if(duplicateSerial() && !isFind)
					{
						showNotification("Warning!","Serial already assign in another designation.",Notification.TYPE_WARNING_MESSAGE);
						txtDesignationSerial.setValue("");
						txtDesignationSerial.focus();
					}
				}
			}
		});
		btnPlusRank.addListener(new ClickListener()
		{			
			public void buttonClick(ClickEvent event)
			{
				rankLink();
			}
		});
	}
	public void rankLink()
	{
		Window win = new DesignationRankInfo(sessionBean);
		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				cmbDesignationRank.removeAllItems();
				addRank();
			}
		});
		this.getParent().addWindow(win);
	}
	private void formValidation()
	{
		if(!txtDesignation.getValue().toString().isEmpty())
		{
			if(cmbDesignationRank.getValue()!=null)
			{
				saveButtonEvent();
			}
			else
			{
				showNotification("Warning!","Provide Rank.", Notification.TYPE_WARNING_MESSAGE);
				cmbDesignationRank.focus();
			}
		}
		else
		{
			showNotification("Warning!","Provide Designation.", Notification.TYPE_WARNING_MESSAGE);
			txtDesignation.focus();
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
				String query = "select * from tbDesignationInfo where vDesignation" +
						" like '"+txtDesignation.getValue().toString()+"'";

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
				String query = "select * from tbDesignationInfo where iDesignationSerial" +
						" = '"+txtDesignationSerial.getValue().toString()+"'";
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
		Window win = new HrmSetupFindWindow(sessionBean, txtFindId, "DESIGNATION");
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
			String query = " Select isnull(max(cast(SUBSTRING(vDesignationId,4,10) as int)),0)+1 from tbDesignationInfo ";
			Iterator <?> iter = session.createSQLQuery(query).list().iterator();

			if (iter.hasNext()) 
			{
				String srt = iter.next().toString();

				maxId = "DES"+srt;
				txtDesignationSerial.setValue(srt);
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
			String sql = "select vDesignationId,vDesignation,iDesignationSerial,isActive,iRank from tbDesignationInfo Where vDesignationId = '"+desigId+"'";
			System.out.println("findInitialise "+sql);

			List <?> led = session.createSQLQuery(sql).list();

			if (led.iterator().hasNext()) 
			{
				Object[] element = (Object[]) led.iterator().next();

				txtDesignationId.setValue(element[0].toString());
				txtDesignation.setValue(element[1].toString());
				txtDesignationSerial.setValue(element[2].toString());
				cmbIsActive.setValue(element[3]);
				cmbDesignationRank.setValue(element[4]);
				System.out.println("iRank "+element[4]);
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
		if(!txtDesignationId.getValue().toString().isEmpty())
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
			String Insert = " INSERT into tbDesignationInfo (vDesignationId,vDesignation," +
					" iDesignationSerial,iRank,isActive,vUserName,vUserIp,dEntryTime) values(" +
					" '"+selectMaxId()+"'," +
					" '"+txtDesignation.getValue().toString().trim()+"'," +
					" '"+txtDesignationSerial.getValue().toString().trim()+"'," +
					" '"+cmbDesignationRank.getValue().toString().trim()+"'," +
					" '"+cmbIsActive.getValue().toString()+"'," +
					" '"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP) "; 
			System.out.println("insertData: "+Insert);
			
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
			String query = " Select * from tbUdDesignationInfo where vDesignationId = '"+txtFindId.getValue().toString()+"'" +
					" and vUdFlag = 'New' ";

			Iterator <?> iter = session.createSQLQuery(query).list().iterator();

			if(!iter.hasNext())
			{
				//Insert new value for first time update
				String Insert = " INSERT into tbUdDesignationInfo (vDesignationId,vDesignation," +
						" iDesignationSerial,iRank,isActive,vUserName,vUserIp,dEntryTime,vUdFlag)" +
						" select vDesignationId,vDesignation,iDesignationSerial,iRank,isActive," +
						" vUserName,vUserIp,dEntryTime,'New' from tbDesignationInfo" +
						" where vDesignationId = '"+txtFindId.getValue().toString()+"' ";
				session.createSQLQuery(Insert).executeUpdate();
			}

			// Main table update
			String update = "UPDATE tbDesignationInfo set" +
					" vDesignation = '"+txtDesignation.getValue().toString().trim()+"' ," +
					" iDesignationSerial = '"+txtDesignationSerial.getValue().toString().trim()+"' ," +
					" iRank = '"+cmbDesignationRank.getValue().toString().trim()+"' ," +
					" isActive = '"+cmbIsActive.getValue().toString()+"' " +
					" where vDesignationId = '"+txtFindId.getValue().toString()+"' ";
			session.createSQLQuery(update).executeUpdate();

			//Insert update or changes data
			String Insert = " INSERT into tbUdDesignationInfo (vDesignationId,vDesignation," +
					" iDesignationSerial,iRank,isActive,vUserName,vUserIp,dEntryTime,vUdFlag) values(" +
					" '"+txtFindId.getValue().toString()+"'," +
					" '"+txtDesignation.getValue().toString().trim()+"'," +
					" '"+txtDesignationSerial.getValue().toString().trim()+"'," +
					" '"+cmbDesignationRank.getValue().toString().trim()+"'," +
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
			showNotification("Error to Update",exp+"",Notification.TYPE_ERROR_MESSAGE);
			return false;
		}
		finally{session.close();}
	}

	private void focusEnter()
	{
		allComp.add(txtDesignation);
		allComp.add(txtDesignationSerial);
		allComp.add(cmbDesignationRank);
		allComp.add(cmbIsActive);

		allComp.add(cButton.btnSave);

		new FocusMoveByEnter(this,allComp);
	}

	private void txtClear()
	{
		txtDesignationId.setValue("");
		txtDesignation.setValue("");
		txtDesignationSerial.setValue("");
		cmbDesignationRank.setValue(null);
		cmbIsActive.setValue(1);
	}

	public void txtInit(boolean t)
	{
		txtDesignationId.setEnabled(!t);
		txtDesignation.setEnabled(!t);
		txtDesignationSerial.setEnabled(!t);
		cmbDesignationRank.setEnabled(!t);
		btnPlusRank.setEnabled(!t);
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
		mainLayout.setHeight("210px");
		mainLayout.setMargin(false);

		lblCommon = new Label("Designation Id :");
		lblCommon.setImmediate(false);
		lblCommon.setWidth("-1px");
		lblCommon.setHeight("-1px");
		mainLayout.addComponent(lblCommon, "top:20.0px; left:50.0px;");

		txtDesignationId = new TextRead();
		txtDesignationId.setImmediate(true);
		txtDesignationId.setWidth("60px");
		txtDesignationId.setHeight("24px");
		mainLayout.addComponent(txtDesignationId, "top:18.0px;left:180.5px;");

		lblCommon = new Label("Designation :");
		mainLayout.addComponent(lblCommon, "top:50.0px; left:50.0px;");

		txtDesignation = new TextField();
		txtDesignation.setImmediate(true);
		txtDesignation.setWidth("320px");
		txtDesignation.setHeight("-1px");
		mainLayout.addComponent(txtDesignation, "top:48.0px; left:180.0px;");

		lblCommon = new Label("Designation Serial :");
		mainLayout.addComponent(lblCommon, "top:80.0px; left:50.0px;");
		txtDesignationSerial = new AmountField();
		txtDesignationSerial.setImmediate(true);
		txtDesignationSerial.setWidth("40px");
		txtDesignationSerial.setHeight("-1px");
		mainLayout.addComponent(txtDesignationSerial, "top:78.0px; left:180.0px;");

		lblCommon = new Label("Grade :");
		mainLayout.addComponent(lblCommon, "top:110.0px; left:50.0px;");
		cmbDesignationRank = new ComboBox();
		cmbDesignationRank.setImmediate(true);
		cmbDesignationRank.setWidth("80px");
		cmbDesignationRank.setHeight("-1px");
		mainLayout.addComponent(cmbDesignationRank, "top:108.0px; left:180.0px;");

		btnPlusRank = new NativeButton();
		btnPlusRank.setIcon(new ThemeResource("../icons/add.png"));
		btnPlusRank.setImmediate(true);
		btnPlusRank.setWidth("28px");
		btnPlusRank.setHeight("24px");
		mainLayout.addComponent(btnPlusRank, "top:108.0px; left:260.0px;");
		
		lblIsActive = new Label("Status :");
		lblIsActive.setImmediate(true);
		lblIsActive.setWidth("-1px");
		lblIsActive.setHeight("-1px");
		mainLayout.addComponent(lblIsActive, "top:140.0px; left:50.0px;");

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
		mainLayout.addComponent(cmbIsActive,"top:138.0px; left:180.0px;");

		mainLayout.addComponent(cButton,"top:170px; left:18px;");

		return mainLayout;
	}
}