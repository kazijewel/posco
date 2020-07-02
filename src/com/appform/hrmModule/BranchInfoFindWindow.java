package com.appform.hrmModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.AmountField;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
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
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

public class BranchInfoFindWindow extends Window
{
	private VerticalLayout mainLayout=new VerticalLayout();
	private FormLayout cmbLayout=new FormLayout();
	private HorizontalLayout btnLayout=new HorizontalLayout();
	private TextField txtReceiptSupplierId;
	private Table table=new Table();

	private String[] co=new String[]{"a","b"};
	public String receiptSupplierId = "";

	private ArrayList<Label> lbBranchID = new ArrayList<Label>();
	private ArrayList<Label> lbBranchName = new ArrayList<Label>();

	private com.common.share.SessionBean sessionBean;
	public BranchInfoFindWindow(SessionBean sessionBean,TextField txtReceiptSupplierId)
	{
		this.txtReceiptSupplierId = txtReceiptSupplierId;
		this.sessionBean=sessionBean;
		this.setCaption("FIND BRANCH INFORMATION :: "+sessionBean.getCompany());
		this.center();
		this.setWidth("550px");
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setModal(true);
		this.setResizable(false);
		this.setStyleName("cwindow");
		compInit();
		compAdd();
	
		setEventAction();
		tableInitialise();
		tableDataAdding();
	}

	public void tableInitialise()
	{
		for(int i=0;i<10;i++)
		{
			tableRowAdd(i);
		}
	}

	public void tableRowAdd(final int ar)
	{
		lbBranchID.add(ar, new Label(""));
		lbBranchID.get(ar).setWidth("100%");
		lbBranchID.get(ar).setImmediate(true);
		lbBranchID.get(ar).setHeight("23px");

		lbBranchName.add(ar, new Label(""));
		lbBranchName.get(ar).setWidth("100%");
		lbBranchName.get(ar).setImmediate(true);
		lbBranchName.get(ar).setHeight("23px");

		table.addItem(new Object[]{lbBranchID.get(ar),lbBranchName.get(ar)},ar);
	}

	public void setEventAction()
	{
		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					receiptSupplierId = lbBranchID.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					txtReceiptSupplierId.setValue(receiptSupplierId);
					windowClose();
				}
			}
		});
	}

	private void tableclear()
	{
		for(int i=0; i<lbBranchID.size(); i++)
		{
			lbBranchID.get(i).setValue("");
			lbBranchName.get(i).setValue("");
		}
	}

	
	public void tableDataAdding()
	{
				
		tableclear();		
		Transaction tx=null;
		try
		{  
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery("Select  cast(id as varchar(120))id,branchName From tbBankBranch order by id").list();

			int i=0;
			for(Iterator iter=list.iterator();iter.hasNext();)
			{
			   Object[]element=(Object[]) iter.next();
			   lbBranchID.get(i).setValue(element[0].toString());
			   lbBranchName.get(i).setValue(element[1].toString());
			   i++;
			   if(lbBranchID.size()-1==i)
					tableRowAdd(i+1);
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
			
	}
	private void windowClose()
	{
		this.close();
	}

	private void compInit()
	{
		mainLayout.setSpacing(true);
		table.setSelectable(true);
		table.setWidth("100%");
		table.setHeight("250px");

		table.setImmediate(true); // react at once when something is selected
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);		

		table.addContainerProperty("ID", Label.class, new Label());
		table.setColumnWidth("ID",100);

		table.addContainerProperty("Branch Name", Label.class, new Label());
		table.setColumnWidth("Branch Name",350);
	}

	private void compAdd()
	{
		cmbLayout.setSpacing(true);
		mainLayout.addComponent(cmbLayout);
		mainLayout.addComponent(btnLayout);
		mainLayout.addComponent(table);
		addComponent(mainLayout);
	}
}