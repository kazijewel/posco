package com.common.share;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import acc.menuform.menu.AdminMenu;
import acc.menuform.menu.HrmAccountMenu;
import acc.menuform.menu.HrmMenu;

import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class UserAuthentication extends Window
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;
	private CommonButton cButton = new CommonButton("", "Save", "", "", "Refresh", "", "", "","","Exit");

	private Panel panelUp = new Panel();
	private Tree menuTree;
	private static final Object CAPTION_PROPERTY = "caption";

	private ComboBox cmbUserName;

	private Table table = new Table();
	private ArrayList<Label> tbLblMenuId = new ArrayList<Label>();
	private ArrayList<Label> tbLblModule = new ArrayList<Label>();
	private ArrayList<Label> tbLblType = new ArrayList<Label>();
	private ArrayList<Label> tbLblMenuCaption = new ArrayList<Label>();
	private ArrayList<CheckBox> tbChkBlock = new ArrayList<CheckBox>();
	private ArrayList<CheckBox> tbChkUnBlock = new ArrayList<CheckBox>();
	private ArrayList<CheckBox> tbChkSave = new ArrayList<CheckBox>();
	private ArrayList<CheckBox> tbChkEdit = new ArrayList<CheckBox>();
	private ArrayList<CheckBox> tbChkDelete = new ArrayList<CheckBox>();
	private ArrayList<CheckBox> tbChkPreview = new ArrayList<CheckBox>();

	private TextField permitForm  = new TextField();
	private TextField permitFormModule  = new TextField();
	private TextField permitFormType  = new TextField();
	private CommonMethod cm;
	private String menuId = "";

	public UserAuthentication(SessionBean sessionBean, String menuId)
	{
		this.sessionBean = sessionBean;
		this.setCaption("USER AUTHENTICATION :: "+sessionBean.getCompany());
		this.sessionBean.setAuthenticWindow(true);
		this.sessionBean.setPermitFormTxt(permitForm);
		this.sessionBean.setPermitFormTxtModule(permitFormModule);
		this.sessionBean.setPermitFormTxtType(permitFormType);
		this.setResizable(false);
		this.setStyleName("cwindow");
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;

		buildMainLayout();
		setContent(mainLayout);

		userAdd();

		setEventAction();
		addTreeProperty();

		authenticationCheck();
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

	private void setEventAction()
	{
		cmbUserName.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				tableClear();
				menuTree.removeAllItems();
				addParents();
				setFindData();
			}
		});

		permitForm.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbUserName.getValue()!=null)
				{
					if(!permitForm.getValue().toString().isEmpty())
					{
						for(int i=0; i<tbLblMenuCaption.size(); i++)
						{
							if(tbLblMenuCaption.get(i).getValue().toString().isEmpty())
							{
								if(tbLblMenuCaption.size()-1 == i)
								{
									tableRowAdd(i+1);
								}
								tbLblMenuCaption.get(i).setValue(permitForm.getValue());
								tbLblMenuId.get(i).setValue(permitForm.getDebugId());
								/*tbLblModule.get(i).setValue(permitFormModule.getValue());
								tbLblType.get(i).setValue(permitFormType.getValue());*/
								break;
							}
						}
					}
				}
				else
				{
					cmbUserName.focus();
					showNotification("Warning!","Select username.",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});
		
		permitFormModule.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbUserName.getValue()!=null)
				{
					if(!permitForm.getValue().toString().isEmpty())
					{
						for(int i=0; i<tbLblMenuCaption.size(); i++)
						{
							if(tbLblMenuCaption.get(i).getValue().toString().isEmpty())
							{
								tbLblModule.get(i-1).setValue(permitFormModule.getValue());
							}
						}
					}
				}
				else
				{
					cmbUserName.focus();
					showNotification("Warning!","Select username.",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});
		
		
		permitFormType.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbUserName.getValue()!=null)
				{
					if(!permitForm.getValue().toString().isEmpty())
					{
						for(int i=0; i<tbLblMenuCaption.size(); i++)
						{
							if(tbLblMenuCaption.get(i).getValue().toString().isEmpty())
							{
								tbLblType.get(i-1).setValue(permitFormType.getValue());
							}
						}
					}
				}
				else
				{
					cmbUserName.focus();
					showNotification("Warning!","Select username.",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		cButton.btnSave.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				formValidation();
			}
		});

		cButton.btnRefresh.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				cmbUserName.setValue(null);
			}
		});

		cButton.btnExit.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				close();
			}
		});
	}

	private void userAdd()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "";
			if(sessionBean.isSuperAdmin())
			{
				query = " select vUserId,vUserName from dbo.tbUserInfo order by vUserName ";
			}
			else
			{
				query = " select vUserId,vUserName from dbo.tbUserInfo where iUserType not in ('1','2') and vUserId != '"+sessionBean.getUserId()+"' order by vUserName ";
			}
			List<?> list = session.createSQLQuery(query).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbUserName.addItem(element[0]);
				cmbUserName.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception ex)
		{
			showNotification("Error to find user",""+ex,Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void formValidation()
	{
		if(cmbUserName.getValue()!=null)
		{
			if(chkTableData())
			{
				saveButtonEvent();
			}
			else
			{
				showNotification("Warning!","Select one checkbox from table.", Notification.TYPE_WARNING_MESSAGE);
				tbChkBlock.get(0).focus();
			}
		}
		else
		{
			showNotification("Warning!","Select username.", Notification.TYPE_WARNING_MESSAGE);
			cmbUserName.focus();
		}
	}

	private boolean chkTableData()
	{
		boolean ret = false;
		for(int i=0; i<tbLblMenuCaption.size(); i++)
		{
			if(!tbLblMenuCaption.get(i).getValue().toString().isEmpty())
			{
				if(tbChkBlock.get(i).booleanValue() || tbChkUnBlock.get(i).booleanValue() || tbChkSave.get(i).booleanValue()
						|| tbChkEdit.get(i).booleanValue() || tbChkDelete.get(i).booleanValue() || tbChkPreview.get(i).booleanValue())
				{
					ret = true;
					break;
				}
			}
		}
		return ret;
	}

	private void saveButtonEvent()
	{
		MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to save information?",
				new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
		mb.show(new EventListener()
		{
			public void buttonClicked(ButtonType buttonType)
			{
				if(buttonType == ButtonType.YES)
				{
					if(insertData())
					{
					cmbUserName.setValue(null);
					tableClear();
					
					Notification n=new Notification("All Information saved Successfully!","",Notification.TYPE_TRAY_NOTIFICATION);
					n.setPosition(Notification.POSITION_TOP_RIGHT);
					showNotification(n);
					}
				}
			}
		});
	}

	private boolean insertData()
	{
		boolean ret = false;
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{
			for(int ar = 0; ar<tbLblMenuId.size(); ar++)
			{
				if(!tbLblMenuCaption.get(ar).getValue().toString().isEmpty())
				{
					//for block and unblock
					if(tbChkBlock.get(ar).booleanValue())
					{
						String sqlCheck = "select vMenuId from dbo.tbUserAuthentication where vMenuId = "+
								"'"+tbLblMenuId.get(ar).getValue().toString()+"' and vUserId = '"+cmbUserName.getValue().toString()+"' ";
						Iterator<?> iter = session.createSQLQuery(sqlCheck).list().iterator();
						if(!iter.hasNext())
						{
							String sql = " INSERT INTO dbo.tbUserAuthentication(vType,vUserId,vCompanyUserName,vMenuId,vMenuCaption,vUserName,vUserIp,dEntryTime,vModuleName,vManuType) VALUES ("+
									" (select vUserType from tbUserInfo where vUserId like '"+cmbUserName.getValue().toString()+"'),"+
									" '"+cmbUserName.getValue().toString()+"',"+
									" '"+cmbUserName.getItemCaption(cmbUserName.getValue()).toString()+"',"+
									" '"+tbLblMenuId.get(ar).getValue().toString()+"'," +
									" '"+tbLblMenuCaption.get(ar).getValue().toString()+"',"+
									" '"+sessionBean.getUserName()+"', '"+sessionBean.getUserIp()+"', CURRENT_TIMESTAMP,'"+tbLblModule.get(ar).getValue().toString()+"','"+tbLblType.get(ar).getValue().toString()+"') ";
							session.createSQLQuery(sql).executeUpdate();

							String udSql = " INSERT INTO dbo.tbUdUserAuthentication(vType,vUserId,vCompanyUserName,vMenuId,vMenuCaption,vUserName,vUserIp,dEntryTime,vFlag,vModuleName,vManuType) VALUES ("+
									" (select vUserType from tbUserInfo where vUserId like '"+cmbUserName.getValue().toString()+"'),"+
									" '"+cmbUserName.getValue().toString()+"',"+
									" '"+cmbUserName.getItemCaption(cmbUserName.getValue()).toString()+"',"+
									" '"+tbLblMenuId.get(ar).getValue().toString()+"'," +
									" '"+tbLblMenuCaption.get(ar).getValue().toString()+"',"+
									" '"+sessionBean.getUserName()+"', '"+sessionBean.getUserIp()+"', CURRENT_TIMESTAMP, 'New','"+tbLblModule.get(ar).getValue().toString()+"','"+tbLblType.get(ar).getValue().toString()+"') ";
							session.createSQLQuery(udSql).executeUpdate();
						}
					}
					if(tbChkUnBlock.get(ar).booleanValue())
					{
						String udSql = " INSERT INTO dbo.tbUdUserAuthentication(vType,vUserId,vCompanyUserName,vMenuId,vMenuCaption,vUserName,vUserIp,dEntryTime,vFlag,vModuleName,vManuType) VALUES ("+
								" (select vUserType from tbUserInfo where vUserId like '"+cmbUserName.getValue().toString()+"'),"+
								" '"+cmbUserName.getValue().toString()+"',"+
								" '"+cmbUserName.getItemCaption(cmbUserName.getValue()).toString()+"',"+
								" '"+tbLblMenuId.get(ar).getValue().toString()+"'," +
								" '"+tbLblMenuCaption.get(ar).getValue().toString()+"',"+
								" '"+sessionBean.getUserName()+"', '"+sessionBean.getUserIp()+"', CURRENT_TIMESTAMP, 'Remove','"+tbLblModule.get(ar).getValue().toString()+"','"+tbLblType.get(ar).getValue().toString()+"') ";
						session.createSQLQuery(udSql).executeUpdate();

						String sql = " DELETE FROM dbo.tbUserAuthentication WHERE vUserId = '"+cmbUserName.getValue().toString()+"' " +
								" AND vMenuId = '"+tbLblMenuId.get(ar).getValue().toString()+"' ";
						session.createSQLQuery(sql).executeUpdate();
					}

					//for restriction in save/edit/delete/preview
					if(tbChkSave.get(ar).booleanValue() || tbChkEdit.get(ar).booleanValue() ||
							tbChkDelete.get(ar).booleanValue() || tbChkPreview.get(ar).booleanValue())
					{
						String sqlCheck = "select vMenuId from dbo.tbUserAccess where vMenuId = "+
								"'"+tbLblMenuId.get(ar).getValue().toString()+"' and vUserId = '"+cmbUserName.getValue().toString()+"' ";
						Iterator<?> iter = session.createSQLQuery(sqlCheck).list().iterator();
						if(!iter.hasNext())
						{
							String sqlUserAccess = " INSERT INTO dbo.tbUserAccess(vCompanyId,vUserId,vUserName,"+
									" vMenuId,vMenuName,iSave,iEdit,iDelete,iPreview,vAuthorBy,vUserIp,dEntryTime,vModuleName,vManuType) VALUES ("+
									" '1',"+
									" '"+cmbUserName.getValue().toString()+"',"+
									" '"+cmbUserName.getItemCaption(cmbUserName.getValue()).toString()+"',"+
									" '"+tbLblMenuId.get(ar).getValue().toString()+"'," +
									" '"+tbLblMenuCaption.get(ar).getValue().toString()+"',"+
									" '"+(tbChkSave.get(ar).booleanValue()?"1":"0")+"',"+
									" '"+(tbChkEdit.get(ar).booleanValue()?"1":"0")+"',"+
									" '"+(tbChkDelete.get(ar).booleanValue()?"1":"0")+"',"+
									" '"+(tbChkPreview.get(ar).booleanValue()?"1":"0")+"',"+
									" '"+sessionBean.getUserName()+"', '"+sessionBean.getUserIp()+"', CURRENT_TIMESTAMP,'"+tbLblModule.get(ar).getValue().toString()+"','"+tbLblType.get(ar).getValue().toString()+"') ";
							session.createSQLQuery(sqlUserAccess).executeUpdate();

							String udSqlUserAuthor = " INSERT INTO dbo.tbUdUserAccess(vCompanyId,vUserId,vUserName,"+
									" vMenuId,vMenuName,iSave,iEdit,iDelete,iPreview,vAuthorBy,vUserIp,dEntryTime,vFlag,vModuleName,vManuType) VALUES ("+
									" '1',"+
									" '"+cmbUserName.getValue().toString()+"',"+
									" '"+cmbUserName.getItemCaption(cmbUserName.getValue()).toString()+"',"+
									" '"+tbLblMenuId.get(ar).getValue().toString()+"'," +
									" '"+tbLblMenuCaption.get(ar).getValue().toString()+"',"+
									" '"+(tbChkSave.get(ar).booleanValue()?"1":"0")+"',"+
									" '"+(tbChkEdit.get(ar).booleanValue()?"1":"0")+"',"+
									" '"+(tbChkDelete.get(ar).booleanValue()?"1":"0")+"',"+
									" '"+(tbChkPreview.get(ar).booleanValue()?"1":"0")+"',"+
									" '"+sessionBean.getUserName()+"', '"+sessionBean.getUserIp()+"', CURRENT_TIMESTAMP, 'New','"+tbLblModule.get(ar).getValue().toString()+"','"+tbLblType.get(ar).getValue().toString()+"') ";
							session.createSQLQuery(udSqlUserAuthor).executeUpdate();
						}
					}
					if(!tbChkSave.get(ar).booleanValue() && !tbChkEdit.get(ar).booleanValue() &&
							!tbChkDelete.get(ar).booleanValue() && !tbChkPreview.get(ar).booleanValue() &&
							!tbChkBlock.get(ar).booleanValue() && !tbChkUnBlock.get(ar).booleanValue())
					{
						String udSqlUserAuthor = " INSERT INTO dbo.tbUdUserAccess(vCompanyId,vUserId,vUserName,"+
								" vMenuId,vMenuName,iSave,iEdit,iDelete,iPreview,vAuthorBy,vUserIp,dEntryTime,vFlag,vModuleName,vManuType) VALUES ("+
								" '1',"+
								" '"+cmbUserName.getValue().toString()+"',"+
								" '"+cmbUserName.getItemCaption(cmbUserName.getValue()).toString()+"',"+
								" '"+tbLblMenuId.get(ar).getValue().toString()+"'," +
								" '"+tbLblMenuCaption.get(ar).getValue().toString()+"',"+
								" '"+(tbChkSave.get(ar).booleanValue()?"1":"0")+"',"+
								" '"+(tbChkEdit.get(ar).booleanValue()?"1":"0")+"',"+
								" '"+(tbChkDelete.get(ar).booleanValue()?"1":"0")+"',"+
								" '"+(tbChkPreview.get(ar).booleanValue()?"1":"0")+"',"+
								" '"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"', CURRENT_TIMESTAMP, 'Remove','"+tbLblModule.get(ar).getValue().toString()+"','"+tbLblType.get(ar).getValue().toString()+"') ";
						session.createSQLQuery(udSqlUserAuthor).executeUpdate();

						String sql = " DELETE FROM dbo.tbUserAccess WHERE vUserId = '"+cmbUserName.getValue().toString()+"' " +
								" AND vMenuId = '"+tbLblMenuId.get(ar).getValue().toString()+"' ";
						session.createSQLQuery(sql).executeUpdate();
					}
				}
			}
			tx.commit();
			ret = true;
		}
		catch(Exception exp)
		{
			tx.rollback();
			showNotification("block info",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
		return ret;
	}

	private void addParents()
	{
		sessionBean.adminModule = false;
		sessionBean.hrmModule = false;
		sessionBean.setupModule=false;
		sessionBean.hrmAccountModule=false;
		
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String userSql = " Select vModuleId, vModuleName from dbo.tbUserDetails where vUserId = "+
					" '"+(cmbUserName.getValue()!=null?cmbUserName.getValue().toString():"")+"' order by vModuleId";
			System.out.println(userSql);
			List<?> lst = session.createSQLQuery(userSql).list();
			for(Iterator<?> iter = lst.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				int moduleId = Integer.parseInt(element[0].toString());
				if(moduleId==0)
				{
					sessionBean.setupModule = true;
				}

				if(moduleId==1)
				{
					sessionBean.hrmModule = true;
				}
				if(moduleId==2)
				{
					sessionBean.hrmAccountModule = true;
				}
			}
		}
		catch(Exception exp)
		{
			System.out.println("Error in moduleAccessCheck"+exp);
		}
		finally{session.close();};

		/*Object setupModule = null, lcModule = null, accountsModule = null,inventoryModule = null,AssetModule = null,transportModule = null;
		if(sessionBean.adminModule)
		{setupModule = addCaptionedItem("MASTER SETUP MODULE", null);}
		if(sessionBean.accountsModule)
		{accountsModule = addCaptionedItem("ACCOUNTS MODULE", null);}
		if(sessionBean.lcModule)
		{lcModule = addCaptionedItem("COMMERCIAL MODULE", null);}
		if(sessionBean.AssetModule)
		{AssetModule = addCaptionedItem("FIXED ASSET MODULE", null);}
		if(sessionBean.inventoryModule)
		{inventoryModule = addCaptionedItem("INVENTORY MODULE", null);}		
		if(sessionBean.transportModule)
		{transportModule = addCaptionedItem("TRANSPORT MODULE", null);}*/
		Object setupModule = null;
		Object hrmModule = null;
		Object accountsModule = null;
		Object hrmAccountModule=null;
		
		
		if(sessionBean.setupModule)
		{setupModule = addCaptionedItem("SETUP MODULE", null);}

		if(sessionBean.hrmModule)
		{hrmModule = addCaptionedItem("HR DEPARTMENT", null);}
		
		if(sessionBean.hrmAccountModule)
		{hrmAccountModule = addCaptionedItem("ACCOUNTS DEPARTMENT", null);}
		
		addChild(setupModule,hrmModule,accountsModule,hrmAccountModule);	
	}

	void addChild(Object setupModule, Object hrmModule, Object accountsModule,Object hrmAccountModule)
	{
		if(sessionBean.setupModule)
		{new AdminMenu(setupModule, menuTree, sessionBean, panelUp);}
		if(sessionBean.hrmModule)
		{new HrmMenu(hrmModule, menuTree, sessionBean, panelUp);}
		if(sessionBean.hrmAccountModule)
		{
			new HrmAccountMenu(hrmAccountModule, menuTree, sessionBean, panelUp);
		}
	}
	private void setFindData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "select vMenuId,vMenuCaption,0 iSave,0 iEdit,0 iDelete,0 iPreview,'Block' Block,vModuleName,vManuType from"+
					" dbo.tbUserAuthentication where vUserId = '"+(cmbUserName.getValue()!=null?cmbUserName.getValue().toString():"")+"'"+
					" union select vMenuId,vMenuName,iSave,iEdit,iDelete,iPreview,'Lock',vModuleName,vManuType from dbo.tbUserAccess"+
					" where vUserId = '"+(cmbUserName.getValue()!=null?cmbUserName.getValue().toString():"")+"' order by Block";
			List<?> lst = session.createSQLQuery(sql).list();
			tableClear();
			int i = 0 ;
			for(Iterator<?> iter = lst.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				if(element[6].toString().equals("Block"))
				{
					tbLblMenuId.get(i).setValue(element[0].toString());
					tbLblMenuCaption.get(i).setValue(element[1].toString());
					tbLblModule.get(i).setValue(element[7].toString());
					tbLblType.get(i).setValue(element[8].toString());
					tbChkBlock.get(i).setValue(true);
				}
				else
				{
					tbLblMenuId.get(i).setValue(element[0].toString());
					tbLblMenuCaption.get(i).setValue(element[1].toString());
					if(Integer.parseInt(element[2].toString())==1)
					{tbChkSave.get(i).setValue(true);}
					if(Integer.parseInt(element[3].toString())==1)
					{tbChkEdit.get(i).setValue(true);}
					if(Integer.parseInt(element[4].toString())==1)
					{tbChkDelete.get(i).setValue(true);}
					if(Integer.parseInt(element[5].toString())==1)
					{tbChkPreview.get(i).setValue(true);}
					tbLblModule.get(i).setValue(element[7].toString());
					tbLblType.get(i).setValue(element[8].toString());
				}

				if(tbLblMenuCaption.size()-1==i)
				{
					tableRowAdd(i+1);
				}
				i++;
			}
		}
		catch(Exception exp)
		{
			showNotification("Error to set find",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private Object addCaptionedItem(String caption, Object parent) 
	{
		final Object id = menuTree.addItem();
		final Item item = menuTree.getItem(id);
		final Property p = item.getItemProperty(CAPTION_PROPERTY);

		p.setValue(caption);

		if (parent != null) 
		{
			menuTree.setChildrenAllowed(parent, true);
			menuTree.setParent(id, parent);
			menuTree.setChildrenAllowed(id, false);
		}
		return id;
	}

	private AbsoluteLayout buildMainLayout() 
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setWidth("950px");
		mainLayout.setHeight("400px");
		mainLayout.setMargin(false);

		cmbUserName = new ComboBox();
		cmbUserName.setImmediate(true);
		cmbUserName.setWidth("210px");
		cmbUserName.setHeight("-1px");
		cmbUserName.setInputPrompt("Select username.");
		mainLayout.addComponent(new Label("Username :"), "top:20.0px; left:5.0px;");
		mainLayout.addComponent(cmbUserName, "top:18.0px; left:75.0px;");

		menuTree = new Tree();
		menuTree.setCaption("LIST OF MODULES");
		menuTree.setImmediate(true);
		menuTree.setWidth("100%");
		menuTree.setHeight("100%");
		menuTree.setStyleName("aa");

		panelUp.setHeight("305px");
		panelUp.setWidth("280px");
		panelUp.setScrollable(true);
		panelUp.addComponent(menuTree);
		mainLayout.addComponent(panelUp, "top:50.0px; left:5.0px;");

		table.setWidth("650px");
		table.setHeight("335px");
		table.setImmediate(true);
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("Menu Id", Label.class, new Label(),null,null,Table.ALIGN_LEFT);
		table.setColumnWidth("Menu Id", 20);
		table.setColumnCollapsed("Menu Id", true);

		table.addContainerProperty("Entry/Report Form Name", Label.class, new Label(),null,null,Table.ALIGN_LEFT);
		table.setColumnWidth("Entry/Report Form Name", 280);

		table.addContainerProperty("Block", CheckBox.class, new CheckBox(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Block", 45);

		table.addContainerProperty("Unblock", CheckBox.class, new CheckBox(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Unblock", 50);

		table.addContainerProperty("Save", CheckBox.class, new CheckBox(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Save", 37);

		table.addContainerProperty("Edit", CheckBox.class, new CheckBox(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Edit", 37);

		table.addContainerProperty("Delete", CheckBox.class, new CheckBox(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Delete", 43);

		table.addContainerProperty("Preview", CheckBox.class, new CheckBox(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Preview", 47);
		
		table.addContainerProperty("Module", Label.class, new Label(),null,null,Table.ALIGN_LEFT);
		table.setColumnWidth("Module", 50);
		table.setColumnCollapsed("Module", true);
		
		table.addContainerProperty("Type", Label.class, new Label(),null,null,Table.ALIGN_LEFT);
		table.setColumnWidth("Type", 50);
		table.setColumnCollapsed("Type", true);

		mainLayout.addComponent(table,"top:20.0px; right:5.0px;");
		tableInitialize();

		mainLayout.addComponent(cButton,"bottom:10px; left:350.0px;");

		permitForm.setVisible(false);
		permitForm.setImmediate(true);
		permitForm.setWidth("300px");
		permitForm.setHeight("-1px");
		mainLayout.addComponent(permitForm, "top:20.0px; right:5.0px;");
		
		permitFormModule.setVisible(false);
		permitFormModule.setImmediate(true);
		permitFormModule.setWidth("300px");
		permitFormModule.setHeight("-1px");
		mainLayout.addComponent(permitFormModule, "top:60.0px; right:5.0px;");
		
		permitFormType.setVisible(false);
		permitFormType.setImmediate(true);
		permitFormType.setWidth("300px");
		permitFormType.setHeight("-1px");
		mainLayout.addComponent(permitFormType, "top:100.0px; right:5.0px;");

		return mainLayout;
	}

	private void tableClear()
	{
		for(int i=0;i<tbLblMenuCaption.size();i++)
		{
			tbLblMenuId.get(i).setValue("");
			tbLblModule.get(i).setValue("");
			tbLblType.get(i).setValue("");
			tbLblMenuCaption.get(i).setValue("");
			tbChkBlock.get(i).setValue(false);
			tbChkUnBlock.get(i).setValue(false);
			tbChkSave.get(i).setValue(false);
			tbChkEdit.get(i).setValue(false);
			tbChkDelete.get(i).setValue(false);
			tbChkPreview.get(i).setValue(false);

			tbChkBlock.get(i).setEnabled(true);
			tbChkUnBlock.get(i).setEnabled(true);
			tbChkSave.get(i).setEnabled(true);
			tbChkEdit.get(i).setEnabled(true);
			tbChkDelete.get(i).setEnabled(true);
			tbChkPreview.get(i).setEnabled(true);
		}
	}

	private void tableInitialize()
	{
		for(int i=0; i<11; i++)
		{
			tableRowAdd(i);
		}
	}

	private void tableRowAdd(final int ar)
	{
		tbLblMenuId.add(ar,new Label());
		tbLblMenuId.get(ar).setImmediate(true);
		tbLblMenuId.get(ar).setWidth("100%");

		tbLblMenuCaption.add(ar,new Label());
		tbLblMenuCaption.get(ar).setImmediate(true);
		tbLblMenuCaption.get(ar).setWidth("100%");
		tbLblMenuCaption.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!tbLblMenuCaption.get(ar).getValue().toString().isEmpty())
				{
					for(int i=0; i<ar; i++)
					{
						if(i!=ar)
						{
							String a_caption = tbLblMenuCaption.get(ar).getValue().toString();
							String b_caption = tbLblMenuCaption.get(i).getValue().toString();
							if(a_caption.equals(b_caption))
							{
								showNotification("Warning!","Already Selected",Notification.TYPE_WARNING_MESSAGE);
								tbLblMenuCaption.get(ar).setValue("");
								tbLblMenuId.get(ar).setValue("");
								tbLblModule.get(ar).setValue("");
								tbLblType.get(ar).setValue("");
								break;
							}
							
						}
					}
					
					tbLblModule.get(ar).setValue(permitFormModule.getValue());
					tbLblType.get(ar).setValue(permitFormType.getValue());
				}
			}
		});

		tbChkBlock.add(ar,new CheckBox());
		tbChkBlock.get(ar).setWidth("100%");
		tbChkBlock.get(ar).setImmediate(true);
		tbChkBlock.get(ar).setDescription("Select to block entry form.");
		tbChkBlock.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!tbLblMenuCaption.get(ar).toString().isEmpty())
				{
					if(tbChkBlock.get(ar).booleanValue())
					{
						tbChkUnBlock.get(ar).setValue(false);
						setEnable(ar, false, 1);
					}
					else
					{
						setEnable(ar, true, 1);
					}
				}
				else
				{
					tbChkBlock.get(ar).setValue(false);
				}
			}
		});

		tbChkUnBlock.add(ar,new CheckBox());
		tbChkUnBlock.get(ar).setWidth("100%");
		tbChkUnBlock.get(ar).setImmediate(true);
		tbChkUnBlock.get(ar).setDescription("Select to unblock entry form.");
		tbChkUnBlock.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!tbLblMenuCaption.get(ar).toString().isEmpty())
				{
					if(tbChkUnBlock.get(ar).booleanValue())
					{
						tbChkBlock.get(ar).setValue(false);
						setEnable(ar, false, 1);
					}
					else
					{
						setEnable(ar, true, 1);
					}
				}
				else
				{
					tbChkUnBlock.get(ar).setValue(false);
				}
			}
		});

		tbChkSave.add(ar,new CheckBox());
		tbChkSave.get(ar).setWidth("100%");
		tbChkSave.get(ar).setImmediate(true);
		tbChkSave.get(ar).setDescription("Select to lock save entry form.");
		tbChkSave.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!tbLblMenuCaption.get(ar).toString().isEmpty())
				{
					if(tbChkSave.get(ar).booleanValue())
					{
						setEnable(ar, false, 2);
					}
					else
					{
						setEnable(ar, true, 2);
					}
				}
				else
				{
					tbChkSave.get(ar).setValue(false);
				}
			}
		});

		tbChkEdit.add(ar,new CheckBox());
		tbChkEdit.get(ar).setWidth("100%");
		tbChkEdit.get(ar).setImmediate(true);
		tbChkEdit.get(ar).setDescription("Select to lock edit entry form.");
		tbChkEdit.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!tbLblMenuCaption.get(ar).toString().isEmpty())
				{
					if(tbChkEdit.get(ar).booleanValue())
					{
						setEnable(ar, false, 2);
					}
					else
					{
						setEnable(ar, true, 2);
					}
				}
				else
				{
					tbChkEdit.get(ar).setValue(false);
				}
			}
		});

		tbChkDelete.add(ar,new CheckBox());
		tbChkDelete.get(ar).setWidth("100%");
		tbChkDelete.get(ar).setImmediate(true);
		tbChkDelete.get(ar).setDescription("Select to lock delete entry form.");
		tbChkDelete.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!tbLblMenuCaption.get(ar).toString().isEmpty())
				{
					if(tbChkDelete.get(ar).booleanValue())
					{
						setEnable(ar, false, 2);
					}
					else
					{
						setEnable(ar, true, 2);
					}
				}
				else
				{
					tbChkDelete.get(ar).setValue(false);
				}
			}
		});

		tbChkPreview.add(ar,new CheckBox());
		tbChkPreview.get(ar).setWidth("100%");
		tbChkPreview.get(ar).setImmediate(true);
		tbChkPreview.get(ar).setDescription("Select to lock preview entry form.");
		tbChkPreview.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!tbLblMenuCaption.get(ar).toString().isEmpty())
				{
					if(tbChkPreview.get(ar).booleanValue())
					{
						setEnable(ar, false, 2);
					}
					else
					{
						setEnable(ar, true, 2);
					}
				}
				else
				{
					tbChkPreview.get(ar).setValue(false);
				}
			}
		});

		tbLblModule.add(ar,new Label());
		tbLblModule.get(ar).setImmediate(true);
		tbLblModule.get(ar).setWidth("100%");
		
		
		tbLblType.add(ar,new Label());
		tbLblType.get(ar).setImmediate(true);
		tbLblType.get(ar).setWidth("100%");
		
		
		table.addItem(new Object[]{tbLblMenuId.get(ar),tbLblMenuCaption.get(ar),tbChkBlock.get(ar),tbChkUnBlock.get(ar),
				tbChkSave.get(ar),tbChkEdit.get(ar),tbChkDelete.get(ar),tbChkPreview.get(ar),tbLblModule.get(ar),tbLblType.get(ar)},ar);
	}

	private void setEnable(int ar, boolean t, int flag)
	{
		if(flag == 1)
		{
			tbChkSave.get(ar).setValue(false);
			tbChkEdit.get(ar).setValue(false);
			tbChkDelete.get(ar).setValue(false);
			tbChkPreview.get(ar).setValue(false);
			tbChkSave.get(ar).setEnabled(t);
			tbChkEdit.get(ar).setEnabled(t);
			tbChkDelete.get(ar).setEnabled(t);
			tbChkPreview.get(ar).setEnabled(t);
		}
		else
		{
			if(t)
			{
				if(!tbChkSave.get(ar).booleanValue() && !tbChkEdit.get(ar).booleanValue() &&
						!tbChkDelete.get(ar).booleanValue() && !tbChkPreview.get(ar).booleanValue())
				{
					tbChkBlock.get(ar).setValue(false);
					tbChkUnBlock.get(ar).setValue(false);
					tbChkBlock.get(ar).setEnabled(t);
					tbChkUnBlock.get(ar).setEnabled(t);
				}
			}
			else
			{
				tbChkBlock.get(ar).setValue(false);
				tbChkUnBlock.get(ar).setValue(false);
				tbChkBlock.get(ar).setEnabled(t);
				tbChkUnBlock.get(ar).setEnabled(t);
			}
		}
	}

	private void addTreeProperty()
	{
		menuTree.setDebugId("tre");
		menuTree.setImmediate(true);
		menuTree.addContainerProperty(CAPTION_PROPERTY, String.class, "");
		menuTree.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
		menuTree.setItemCaptionPropertyId(CAPTION_PROPERTY);
	}
}