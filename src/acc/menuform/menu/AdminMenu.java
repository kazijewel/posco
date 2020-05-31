package acc.menuform.menu;

import hrm.common.reportform.RptDataEntryStatus;

import java.util.HashMap;
import java.util.Iterator;

import org.hibernate.Session;

import com.appform.hrmModule.LeaveApprovalMapping;
import com.common.access.accessMasterSetup;
import com.common.access.accessSetupReports;
import com.common.share.ChangePass;
import com.common.share.CompanyInformation;
import com.common.share.RptUserAuthentication;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.UserAuthentication;
import com.common.share.UserCreate;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class AdminMenu 
{
	private Tree tree;
	private SessionBean sessionBean;
	private Component component;

	private HashMap<String, String> winMap = new HashMap<String, String>();
	private static final Object CAPTION_PROPERTY = "caption";

	private Object /*generalSetup = null,*/ masterSetup = null, setupReport = null;

	public AdminMenu(Object adminMenu,Tree tree,SessionBean sessionBean,Component component)
	{
		this.tree = tree;
		this.sessionBean = sessionBean;
		this.component = component;

		treeAction();

		if(isValidMenu("masterSetup"))
		{
			masterSetup = addCaptionedItem("MASTER SETUP", adminMenu);
			generalSetup(masterSetup);
		}
		if(isValidMenu("Report"))
		{
			setupReport = addCaptionedItem("SETUP REPORT", adminMenu);
			reportMenu(setupReport);
		}
	}

	private void generalSetup(Object masterSetup)
	{
		if(isValidMenu("companyInformation"))
		{
			addCaptionedItem("COMPANY INFORMATION", masterSetup);
		}
		if(isValidMenu("userCreate"))
		{
			addCaptionedItem("USER CREATE", masterSetup);
		}
		if(isValidMenu("ChangePassword"))
		{
			addCaptionedItem("CHANGE PASSWORD", masterSetup);
		}
		if(isValidMenu("userAuthentication"))
		{
			addCaptionedItem("USER AUTHENTICATION", masterSetup);
		}
		/*if(isValidMenu("LeaveApprovalMapping"))
		{
			addCaptionedItem("LEAVE APPROVAL MAPPING", masterSetup);
		}*/

	}

	private void reportMenu(Object setupReport)
	{
		
		if(isValidMenu("rptUserAuthentication"))
		{
			addCaptionedItem("USER AUTHENTICATION REPORT", setupReport);
		}
		if(isValidMenu("RptDataEntryStatus"))
		{
			addCaptionedItem("DATA ENTRY STATUS",setupReport);
		}
	}

	private Object addCaptionedItem(String caption, Object parent) 
	{
		final Object id = tree.addItem();
		final Item item = tree.getItem(id);
		final Property p = item.getItemProperty(CAPTION_PROPERTY);

		p.setValue(caption);
		if(parent != null)
		{
			tree.setChildrenAllowed(parent, true);
			tree.setParent(id, parent);
			tree.setChildrenAllowed(id, false);
		}
		return id;
	}

	@SuppressWarnings("serial")
	public void treeAction()
	{
		tree.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				// Control Access
				if(event.getItem().toString().equalsIgnoreCase("MASTER SETUP"))
				{
					showWindow(new accessMasterSetup(sessionBean),event.getItem(),"masterSetup","SETUP MODULE","SETUP");
				}
				if(event.getItem().toString().equalsIgnoreCase("REPORT"))
				{
					showWindow(new accessSetupReports(sessionBean),event.getItem(),"Report","SETUP MODULE","REPORT");
				}

				//GENERAL SETUP
				if(event.getItem().toString().equalsIgnoreCase("COMPANY INFORMATION"))
				{
					showWindow(new CompanyInformation(sessionBean,"companyInformation"),event.getItem(),"companyInformation","SETUP MODULE","SETUP");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("USER CREATE"))
				{
					showWindow(new UserCreate(sessionBean,"userCreate"),event.getItem(),"userCreate","SETUP MODULE","SETUP");
				}
				if(event.getItem().toString().equalsIgnoreCase("CHANGE PASSWORD"))
				{
					showWindow(new ChangePass(sessionBean),event.getItem(),"ChangePassword","SETUP MODULE","SETUP");
				}

				if(event.getItem().toString().equalsIgnoreCase("LEAVE APPROVAL MAPPING"))
				{
					showWindow(new LeaveApprovalMapping(sessionBean,"LeaveApprovalMapping"),event.getItem(),"LeaveApprovalMapping","HRM MODULE","TRANSACTION");
				}
				
				//REPORT
				
				if(event.getItem().toString().equalsIgnoreCase("USER AUTHENTICATION REPORT"))
				{
					showWindow(new RptUserAuthentication(sessionBean,"rptUserAuthentication"),event.getItem(),"rptUserAuthentication","SETUP MODULE","REPORT");
				}
				if(event.getItem().toString().equalsIgnoreCase("DATA ENTRY STATUS"))
				{
					showWindow(new RptDataEntryStatus(sessionBean,"RptDataEntryStatus"),event.getItem(),"RptDataEntryStatus","HRM MODULE","REPORT");
				}
				if(event.getItem().toString().equalsIgnoreCase("USER AUTHENTICATION"))
				{
					if(!sessionBean.getAuthenticWindow())
					{
						Window userAuthen = new UserAuthentication(sessionBean,"userAuthentication");
						userAuthen.center();
						component.getWindow().addWindow(userAuthen);
						userAuthen.setCloseShortcut(KeyCode.ESCAPE);
						userAuthen.addListener(new Window.CloseListener()
						{
							public void windowClose(CloseEvent e)
							{
								sessionBean.setAuthenticWindow(false);
							}
						});
					}
					else
					{
						sessionBean.setPermitForm("userAuthentication","USER AUTHENTICATION","SETUP MODULE","SETUP");
					}
				}
			}
		});
	}

	// check is valid menu for add menu bar
	private boolean isValidMenu(String id)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "SELECT vMenuId FROM dbo.tbUserAuthentication WHERE vMenuId = '"+id+"'"
					+ " and vUserId = '"+sessionBean.getUserId()+"'";
			Iterator<?> iter = session.createSQLQuery(sql).list().iterator();
			if(!iter.hasNext())
			{
				return true;
			}
		}
		catch(Exception exp)
		{
			System.out.println(exp);
		}
		finally{session.close();}
		return false;
	}

	@SuppressWarnings("serial")
	private void showWindow(Window win, Object selectedItem,String mid,String Module,String Type)
	{
		try
		{
			final String id = selectedItem+"";
			if(!sessionBean.getAuthenticWindow())
			{
				if(isOpen(id))
				{
					win.center();
					win.setStyleName("cwindow");
					component.getWindow().addWindow(win);
					win.setCloseShortcut(KeyCode.ESCAPE);
					winMap.put(id,id);
					win.addListener(new Window.CloseListener() 
					{
						public void windowClose(CloseEvent e) 
						{
							winMap.remove(id);                	
						}
					});
				}
			}
			else
			{
				sessionBean.setPermitForm(mid,selectedItem.toString(),Module,Type);
			}
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
	}

	private boolean isOpen(String id)
	{
		return !winMap.containsKey(id);
	}
}