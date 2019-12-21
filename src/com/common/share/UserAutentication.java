package com.common.share;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import acc.menuform.menu.HrmMenu;

import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Window;

public class UserAutentication extends Window
{
	private SessionBean sessionBean;

	private AbsoluteLayout mainLayout;

	private Panel panelUp = new Panel();
	private Tree menuTree;
	private static final Object CAPTION_PROPERTY = "caption";

	private Label lblUserName;

	private ComboBox cmbUserName;

	public Table table = new Table();

	public ArrayList<CheckBox> tbBlock = new ArrayList<CheckBox>();
	public ArrayList<CheckBox> tbUnBlock = new ArrayList<CheckBox>();
	public ArrayList<Label> tbMenuCaption = new ArrayList<Label>();
	public ArrayList<Label> tbMenuId = new ArrayList<Label>();

	private ArrayList<Component> allComp = new ArrayList<Component>();

	private TextField permitForm  = new TextField("");

	private boolean isCheck = false;

	private boolean isSetup = false, isRawMeterials = false;
	private boolean isProduction = false, isFinishedGoods = false;
	private boolean isDoSales = false, isAccounts = false,isFixedAsset = false; 
	private boolean isHrm = false, isTransport = false, isLc = false;
	private boolean isRegistermodule =false;

	public UserAutentication(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("USER AUTHENTICATION :: "+sessionBean.getCompany());
		this.sessionBean.setAuthenticWindow(true);
		//this.sessionBean.setPermitForm("userAuthentication","USER AUTHENTICATION");
		this.sessionBean.setPermitFormTxt(permitForm);
		this.setResizable(false);
		this.setStyleName("cwindow");

		buildMainLayout();
		setContent(mainLayout);

		tbinitialize();
		btnAction();
		initialiseEmployee();
		addTree();
	}

	public void tbinitialize()
	{
		for(int i=0;i<10;i++)
		{
			tableRowAdd(i);
		}
	}

	public void tableRowAdd(final int ar)
	{	
		tbMenuCaption.add(ar,new Label());
		tbMenuCaption.get(ar).setWidth("100%");
		tbMenuCaption.get(ar).setImmediate(true);
		tbMenuCaption.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!tbMenuCaption.get(ar).getValue().toString().equals(""))
				{
					for(int i=0; i<ar; i++)
					{
						if(i!=ar)
						{
							String a_caption = tbMenuCaption.get(ar).getValue().toString();
							String b_caption = tbMenuCaption.get(i).getValue().toString();
							if(a_caption.equals(b_caption))
							{
								showNotification("Warning!","Already Selected",Notification.TYPE_WARNING_MESSAGE);
								tbMenuCaption.get(ar).setValue("");
								break;
							}
						}
					}
				}
			}
		});

		tbBlock.add(ar,new CheckBox());
		tbBlock.get(ar).setWidth("100%");
		tbBlock.get(ar).setValue(false);
		tbBlock.get(ar).setImmediate(true);

		tbBlock.get(ar).addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(tbBlock.get(ar).getValue().equals(true))
				{
					if(!isCheck)
					{
						blockBtnAction(ar);
						tbUnBlock.get(ar).setValue(false);
					}
				}
				else if(tbBlock.get(ar).getValue().equals(false))
				{

				}
			}
		});

		tbUnBlock.add(ar,new CheckBox());
		tbUnBlock.get(ar).setWidth("100%");
		tbUnBlock.get(ar).setValue(false);
		tbUnBlock.get(ar).setImmediate(true);

		tbUnBlock.get(ar).addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(tbUnBlock.get(ar).getValue().equals(true))
				{
					unBlockBtnAction(ar);
					tbBlock.get(ar).setValue(false);

					isCheck = false;
				}
				else if(tbUnBlock.get(ar).getValue().equals(false))
				{

				}
			}
		});

		tbMenuId.add(ar,new Label());
		tbMenuId.get(ar).setWidth("100%");
		tbMenuId.get(ar).setImmediate(true);

		table.addItem(new Object[] { tbMenuCaption.get(ar),tbBlock.get(ar),tbUnBlock.get(ar),tbMenuId.get(ar) },ar);
	}

	private void blockBtnAction(int ar)
	{
		if(sessionBean.isAdmin())
		{
			if(cmbUserName.getValue()==null)
			{
				this.getParent().showNotification("","Please Select User Name.",Notification.TYPE_WARNING_MESSAGE);
			}
			else if(!tbMenuCaption.get(ar).getValue().toString().isEmpty())
			{
				Transaction tx = null;
				try
				{
					Session session = SessionFactoryUtil.getInstance().getCurrentSession();
					tx = session.beginTransaction();

					String sql = "INSERT INTO tbAuthentication(userId,menuId,createBy, " +
							" menuCaption,insertTime) " +
							" VALUES('"+cmbUserName.getValue()+"','"+
							tbMenuId.get(ar).getValue()+"','"+sessionBean.getUserId()+"', " +
							" '"+tbMenuCaption.get(ar).getValue()+"',CURRENT_TIMESTAMP )";

					String udSql = "INSERT INTO tbUdAuthentication(userId,menuId,createBy, " +
							" menuCaption,insertTime,uId,uIp,entrytime,flag) " +
							" VALUES('"+cmbUserName.getValue()+"','"+
							tbMenuId.get(ar).getValue()+"','"+sessionBean.getUserId()+"', " +
							" '"+tbMenuCaption.get(ar).getValue()+"',CURRENT_TIMESTAMP, " +
							" '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP,'New' " +
							" )";

					session.createSQLQuery(sql).executeUpdate();
					session.createSQLQuery(udSql).executeUpdate();

					tx.commit();
					this.getParent().showNotification("Permission has been taken.");
				}
				catch(Exception exp)
				{
					tx.rollback();
					this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
				}
			}
			else
			{
				this.getParent().showNotification("","Permission already taken.",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else
		{
			this.getParent().showNotification("Authentication Failed",
					"You have not proper authentication for permission taken.",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void unBlockBtnAction(int ar)
	{
		if(sessionBean.isAdmin())
		{
			Transaction tx = null;
			try
			{
				Session session = SessionFactoryUtil.getInstance().getCurrentSession();
				tx = session.beginTransaction();

				String sql = " DELETE FROM tbAuthentication WHERE userId = '"+cmbUserName.getValue()+"' " +
						" AND menuId = '"+tbMenuId.get(ar).getValue()+"' ";

				String udSql = "INSERT INTO tbUdAuthentication(userId,menuId,createBy, " +
						" menuCaption,insertTime,uId,uIp,entrytime,flag) " +
						" VALUES('"+cmbUserName.getValue()+"','"+
						tbMenuId.get(ar).getValue()+"','"+sessionBean.getUserId()+"', " +
						" '"+tbMenuCaption.get(ar).getValue()+"',CURRENT_TIMESTAMP, " +
						" '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP,'Update' " +
						" )";

				session.createSQLQuery(sql).executeUpdate();
				session.createSQLQuery(udSql).executeUpdate();

				tbMenuCaption.get(ar).setValue("");
				tbMenuId.get(ar).setValue("");
				tbUnBlock.get(ar).setValue(false);

				tx.commit();
				this.getParent().showNotification("Permission granted.");
			}
			catch(Exception exp)
			{
				tx.rollback();
				this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			}
		}
		else
		{
			this.getParent().showNotification("Authentication Failed",
					"You have not proper authentication for permission granted.",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void btnAction()
	{
		cmbUserName.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				isCheck = true;
				if(cmbUserName.getValue()!=null)
				{
					isSetup = false;
					isHrm = false;

					menuTree.removeAllItems();
					addParents();
					setFindData();
				}
				else
				{
					tableClear();
				}
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
						if(!duplicateMenuCaptionCheck())
						{
							for(int i=0;i<tbMenuCaption.size();i++)
							{
								if(tbMenuCaption.get(i).getValue().toString().isEmpty())
								{
									if(tbMenuCaption.size()-1==i)
									{
										tableRowAdd(i+1);
									}

									tbMenuCaption.get(i).setValue(permitForm.getValue());
									tbMenuId.get(i).setValue(permitForm.getDebugId());

									isCheck = false;

									break;
								}
							}
						}
						else
						{
							showNotification("Warning!","Selected Entry Form Already Exits",Notification.TYPE_WARNING_MESSAGE);
						}
					}
				}
				else
				{
					showNotification("Warning","There is no User Name");
				}
			}
		});
	}

	private boolean duplicateMenuCaptionCheck()
	{
		boolean ret = false;
		for(int i=0; i<tbMenuCaption.size(); i++)
		{
			if(!tbMenuCaption.get(i).getValue().toString().isEmpty())
			{
				if(tbMenuCaption.get(i).getValue().toString().equals(permitForm.getValue().toString()))
				{
					ret = true;
					break;
				}
			}
			else
			{
				break;
			}
		}
		return ret;
	}

	private void initialiseEmployee()
	{
		cmbUserName.removeAllItems();
		String query="";

		try
		{
			Transaction tx;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();

			tx = session.beginTransaction();

			if(sessionBean.isAdmin())
			{
				query=" select userId,name from tbLogin where userId not in ('"+sessionBean.getUserId()+"') and isSuperAdmin = 0 and name!='admin' order by userId ";
			}
			if(!sessionBean.isAdmin())
			{
				query=" select userId,name from tbLogin where userId not in ('"+sessionBean.getUserId()+"') and isAdmin = 0 and isSuperAdmin = 0 and name!='admin' order by userId ";
			}
			if(sessionBean.isSuperAdmin())
			{
				query=" select userId,name from tbLogin where userId not in ('"+sessionBean.getUserId()+"') and name!='admin' order by userId ";
			}

			System.out.println("Query: "+query);

			List list = session.createSQLQuery(query).list();

			for(Iterator iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbUserName.addItem(element[0]);
				cmbUserName.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception ex)
		{
			System.out.print("Hi"+ex);
		}
	}

	private void setFindData()
	{
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			String sql = "SELECT * FROM tbAuthentication as c WHERE c.userId = '"+cmbUserName.getValue()+"' ";

			System.out.println(sql);

			List lst = session.createSQLQuery(sql).list();

			tableClear();

			int i = 0 ;

			if(!lst.isEmpty())
			{
				for (Iterator iter = lst.iterator(); iter.hasNext();) 
				{
					Object[] element = (Object[]) iter.next();

					tbMenuCaption.get(i).setValue(element[5]);
					tbBlock.get(i).setValue(true);
					tbUnBlock.get(i).setValue(false);
					tbMenuId.get(i).setValue(element[1]);

					if(tbMenuCaption.size()-1==i)
					{
						tableRowAdd(i+1);
					}

					i++;
				}
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void tableClear()
	{
		for(int i=0;i<tbMenuCaption.size();i++)
		{
			tbMenuCaption.get(i).setValue("");
			tbBlock.get(i).setValue(false);
			tbUnBlock.get(i).setValue(false);
			tbMenuId.get(i).setValue("");
		}
	}

	private AbsoluteLayout buildMainLayout() 
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("730px");
		setHeight("400px");

		// lblUserName
		lblUserName = new Label();
		lblUserName.setImmediate(false);
		lblUserName.setWidth("-1px");
		lblUserName.setHeight("-1px");
		lblUserName.setValue("User Name : ");
		mainLayout.addComponent(lblUserName,"top:20.0px;left:24.0px;");

		// cmbUserName
		cmbUserName = new ComboBox();
		cmbUserName.setImmediate(true);
		cmbUserName.setNewItemsAllowed(false);
		cmbUserName.setNullSelectionAllowed(true);
		cmbUserName.setWidth("180px");
		cmbUserName.setHeight("-1px");
		mainLayout.addComponent(cmbUserName, "top:19.0px;left:100.0px;");

		// menuTree
		menuTree = new Tree();
		menuTree.setCaption("LIST OF MODULES");
		menuTree.setImmediate(true);
		menuTree.setWidth("100%");
		menuTree.setHeight("100%");
		menuTree.setStyleName("aa");

		panelUp.setHeight("290px");
		panelUp.setWidth("270px");
		panelUp.setScrollable(true);
		panelUp.addComponent(menuTree);
		mainLayout.addComponent(panelUp, "top:55.0px;left:24.0px;");

		// table
		table.setWidth("400px");
		table.setHeight("290px");
		table.setImmediate(true);
		table.setColumnCollapsingAllowed(true);
		table.setColumnReorderingAllowed(false);
		table.setPageLength(0);
		table.setSortDisabled(true);

		table.addContainerProperty("Entry Form Name", Label.class, new Label(),null,null,Table.ALIGN_LEFT);
		table.setColumnWidth("Entry Form Name", 250);
		table.addContainerProperty("Block", CheckBox.class, new CheckBox(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Block", 45);
		table.addContainerProperty("Unblock", CheckBox.class, new CheckBox(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Unblock", 45);
		table.addContainerProperty("Menu Id", Label.class, new Label(),null,null,Table.ALIGN_LEFT);
		table.setColumnWidth("Menu Id", 20);

		table.setColumnCollapsed("Menu Id", true);

		mainLayout.addComponent(table,"top:55.0px; left:310.0px;");

		permitForm.setVisible(false);
		permitForm.setImmediate(true);
		permitForm.setWidth("300px");
		permitForm.setHeight("-1px");
		mainLayout.addComponent(permitForm, "top:20.0px;left:320.0px;");

		return mainLayout;
	}

	void addTree()
	{
		menuTree.setDebugId("tre");
		menuTree.setImmediate(true);
		menuTree.addContainerProperty(CAPTION_PROPERTY, String.class, "");
		menuTree.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
		menuTree.setItemCaptionPropertyId(CAPTION_PROPERTY);
	}

	void addParents()
	{	
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			String sql = " select moduleId,moduleName from tbLoginDetails where userId = '"+cmbUserName.getValue()+"' ";
			List lst = session.createSQLQuery(sql).list();

			int i = 0;
			if(!lst.isEmpty())
			{
				for (Iterator iter = lst.iterator(); iter.hasNext();) 
				{
					Object[] element = (Object[]) iter.next();

					if(Integer.parseInt(element[0].toString())==0)
					{	isSetup = true;		}

					
					if(Integer.parseInt(element[0].toString())==1)
					{	isHrm = true;	}

					i++;
				}
			}
		}

		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}

		Object setupModule = null,rawMeterials = null,productionModule = null;
		Object finishedGoodsModule = null,doSalesModule = null;
		Object accountsModule = null,fixedAssetModule = null,hrmModule = null;
		Object transportModule = null,lcModule = null;
		Object registermodule=null;

		if(isSetup)
		{
			setupModule = addCaptionedItem("SETUP MODULE", null);
		}
		if(isHrm)
		{
			hrmModule = addCaptionedItem("HRM MODULE", null);
		}

		addChild(setupModule, rawMeterials, productionModule,finishedGoodsModule,
				doSalesModule,accountsModule,fixedAssetModule,hrmModule,transportModule,lcModule,registermodule);
	}

	void addChild(Object setupModule, Object rawMeterials, Object productionModule, Object finishedGoodsModule,
			Object doSalesModule, Object accountsModule, Object fixedAssetModule, Object hrmModule,
			Object transportModule,Object lcModule,Object registermodule)
	{
		if(isHrm)
		{
			new HrmMenu(hrmModule,menuTree,sessionBean,panelUp);
		}
		
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
}
