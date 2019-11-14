package com.common.share;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

public class UserCreateFindWindow extends Window
{
	/*private VerticalLayout mainLayout = new VerticalLayout();
	private FormLayout cmbLayout = new FormLayout();
	private HorizontalLayout btnLayout = new HorizontalLayout();*/
	
	private AbsoluteLayout mainLayout;
	
	private TextField txtReceiptSupplierId;
	private Table table = new Table();

	public String receiptUserId = "";

	private ArrayList<Label> lbUserID = new ArrayList<Label>();
	private ArrayList<Label> lbUserName = new ArrayList<Label>();
	private ArrayList<Label> lbUserType = new ArrayList<Label>();

	private String frmName;
	private SessionBean sessionBean;

	public UserCreateFindWindow(SessionBean sessionBean,TextField txtReceiptSupplierId,String frmName)
	{
		this.setCaption("FIND USER INFORMATION :: "+sessionBean.getCompany());
		this.setStyleName("cwindow");
		this.center();
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setModal(true);
		this.setResizable(false);
		this.sessionBean = sessionBean;
		this.frmName = frmName;
		this.txtReceiptSupplierId = txtReceiptSupplierId;
		
		buildMainLayout();
		setContent(mainLayout);

		tableInitialise();
		setEventAction();
		
		tableDataAdding();
	}
	
	public void tableInitialise()
	{
		for(int i=0;i<7;i++)
		{
			tableRowAdd(i);
		}
	}

	public void tableRowAdd(final int ar)
	{
		lbUserID.add(ar, new Label(""));
		lbUserID.get(ar).setWidth("100%");
		lbUserID.get(ar).setImmediate(true);
		lbUserID.get(ar).setHeight("14px");

		lbUserName.add(ar, new Label(""));
		lbUserName.get(ar).setWidth("100%");
		lbUserName.get(ar).setImmediate(true);

		lbUserType.add(ar, new Label(""));
		lbUserType.get(ar).setWidth("100%");
		lbUserType.get(ar).setImmediate(true);

		table.addItem(new Object[]{lbUserID.get(ar),lbUserName.get(ar),lbUserType.get(ar)},ar);
	}
	
	public void setEventAction()
	{
		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					receiptUserId = lbUserID.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					txtReceiptSupplierId.setValue(receiptUserId);
					windowClose();
				}
			}
		});
	}

	private void tableclear()
	{
		for(int i=0; i<lbUserID.size(); i++)
		{
			lbUserID.get(i).setValue("");
			lbUserName.get(i).setValue("");
			lbUserType.get(i).setValue("");
		}
	}

	private void tableDataAdding()
	{
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			
			String query = " Select userId,name,isAdmin,isSuperAdmin from tbLogin where userId not in('"+sessionBean.getUserId()+"') order by userId";
			System.out.println("Increment : "+query);

			List list = session.createSQLQuery(query).list();
			
			tableclear();

			if(!list.isEmpty())
			{
				int i=0;

				for(Iterator iter = list.iterator(); iter.hasNext();)
				{						  
					Object[] element = (Object[]) iter.next();

					lbUserID.get(i).setValue(element[0]);
					lbUserName.get(i).setValue(element[1]);

					if(element[2].toString().equals("1"))
					{
						lbUserType.get(i).setValue("Admin");
					}
					else
					{
						lbUserType.get(i).setValue("General");
					}

					if((i)==lbUserID.size()-1) 
					{
						tableRowAdd(i+1);
					}

					i++;
				}
			}
			else 
			{
				tableclear();
				this.getParent().showNotification("Data not Found !!", Notification.TYPE_WARNING_MESSAGE); 
			}
		}
		catch (Exception ex) 
		{
			this.getParent().showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void windowClose()
	{
		this.close();
	}
	
	private AbsoluteLayout buildMainLayout() 
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(false);
		
		// top-level component properties
		setWidth("480px");
		setHeight("400px");

		table.setSelectable(true);
		table.setWidth("98%");
		table.setHeight("320px");
		table.setImmediate(true); // react at once when something is selected
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);
		
		table.addContainerProperty("User ID", Label.class, new Label());
		table.setColumnWidth("User ID",40);
		table.addContainerProperty("User Name", Label.class, new Label());
		table.setColumnWidth("User Name",200);
		table.addContainerProperty("User Type", Label.class, new Label());
		table.setColumnWidth("User Type",150);
		
		mainLayout.addComponent(table,"top:20.0px; left:15.0px;");
		
		return mainLayout;
	}
}