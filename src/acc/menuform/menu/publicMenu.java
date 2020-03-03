package acc.menuform.menu;

import java.util.HashMap;
import java.util.Iterator;

import org.hibernate.Session;

import com.appform.hrmModule.LeaveApplicationForm;
import com.appform.hrmModule.LoanApplicationForm;
import com.appform.hrmModule.OverTimeRequestForm;
import com.appform.hrmModule.ReplacementLeaveApplication;
import com.appform.hrmModule.accessHrmTrans;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
//import hrm.common.setup.DesignationSerialInfo;

public class publicMenu
{
	private HashMap<Object, Object> winMap = new HashMap<Object, Object>();
	private static final Object CAPTION_PROPERTY = "caption";

	Tree tree;
	SessionBean sessionBean;
	Component component;


	Object hrmTransaction = null;
	Object hrmTrsLeave=null;
	Object hrmTrsOverTime=null;
	Object hrmTrsReplacementLeave=null;
	Object hrmTrsLoan=null;
	
	
	boolean isPreview=true;
	String typeOfPublic;

	public publicMenu(Object hrmModule,Tree tree,SessionBean sessionBean,Component component,String typeOfPublic)
	{
		this.tree = tree;
		this.sessionBean = sessionBean;
		this.component = component;
		this.typeOfPublic=typeOfPublic;
		treeAction();

		if(typeOfPublic.equals("Leave"))
		{
			if(isValidMenu("employeeLeave"))
			{
				hrmTrsLeave = addCaptionedItem("LEAVE", hrmTransaction);
				addTransactionLeave(hrmTrsLeave);
			}
		}
		if(typeOfPublic.equals("OverTime"))
		{
			if(isValidMenu("employeeOverTime"))
			{
				hrmTrsOverTime = addCaptionedItem("OVER TIME", hrmTransaction);
				addTransactionOverTime(hrmTrsOverTime);
			}
		}
		if(typeOfPublic.equals("ReplacementLeave"))
		{
			if(isValidMenu("employeeReplacementLeave"))
			{
				hrmTrsReplacementLeave = addCaptionedItem("REPLACEMENT LEAVE", hrmTransaction);
				addTransactionReplacementLeave(hrmTrsReplacementLeave);
			}
		}
	}



	private void addTransactionLeave(Object hrmTrsLeave)
	{
		if(isValidMenu("leaveApplication"))
		{
			addCaptionedItem("LEAVE APPLICATION", hrmTrsLeave);
		}		
	}
	private void addTransactionOverTime(Object hrmTrsOverTime)
	{
		if(isValidMenu("OverTimeRequestForm"))
		{
			addCaptionedItem("OVER TIME REQUEST FORM", hrmTrsOverTime);
		}		
	}
	private void addTransactionReplacementLeave(Object hrmTrsOverTime)
	{
		if(isValidMenu("ReplacementLeaveApplication"))
		{
			addCaptionedItem("REPLACEMENT LEAVE APPLICATION", hrmTrsOverTime);
		}		
	}
	
	private void addTransactionLoan(Object hrmTrsLoan)
	{
		if(isValidMenu("loanApplication"))
		{
			addCaptionedItem("LOAN APPLICATION FORM", hrmTrsLoan);
		}
	}
	
	private Object addCaptionedItem(String caption, Object parent) 
	{
		final Object id = tree.addItem();
		final Item item = tree.getItem(id);
		final Property p = item.getItemProperty(CAPTION_PROPERTY);

		p.setValue(caption);

		if (parent != null) 
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
				if(event.getItem().toString().equalsIgnoreCase("HRM TRANSACTION"))
				{
					showWindow(new accessHrmTrans(sessionBean),event.getItem(),"hrmTransaction","HRM MODULE","TRANSACTION");
				}
	
				//HRM TRANSACTION
				//Attendance
				if(event.getItem().toString().equalsIgnoreCase("LEAVE APPLICATION"))
				{
					showWindow(new LeaveApplicationForm(sessionBean,"LeaveApplication",true),event.getItem(),"leaveApplication","HRM MODULE","TRANSACTION");
				}
				
				//Loan
				if(event.getItem().toString().equalsIgnoreCase("LOAN APPLICATION FORM"))
				{
					showWindow(new LoanApplicationForm(sessionBean,isPreview,"LoanApplicationForm"),event.getItem(),"loanApplication","HRM MODULE","TRANSACTION");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("OVER TIME REQUEST FORM"))
				{
					showWindow(new OverTimeRequestForm(sessionBean,"OverTimeRequestForm",true),event.getItem(),"OverTimeRequestForm","HRM MODULE","TRANSACTION");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("REPLACEMENT LEAVE APPLICATION"))
				{
					showWindow(new ReplacementLeaveApplication(sessionBean,"ReplacementLeaveApplication",true),event.getItem(),"ReplacementLeaveApplication","HRM MODULE","TRANSACTION");
				}
			}
		});
	}

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

	private  boolean isOpen(String id)
	{
		return !winMap.containsKey(id);
	}
}