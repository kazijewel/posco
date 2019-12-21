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

public class SectionFindWindow extends Window
{
	private VerticalLayout mainLayout=new VerticalLayout();
	private FormLayout cmbLayout=new FormLayout();
	private HorizontalLayout btnLayout=new HorizontalLayout();
	private TextField txtReceiptSupplierId;
	private Table table=new Table();

	private String[] co=new String[]{"a","b"};
	public String receiptSupplierId = "";

	private ArrayList<Label> lbSectionID = new ArrayList<Label>();
	private ArrayList<Label> lbSectionName = new ArrayList<Label>();

	private String frmName;
	private com.common.share.SessionBean sessionBean;
	public SectionFindWindow(com.common.share.SessionBean sessionBean,TextField txtReceiptSupplierId,String frmName)
	{
		this.txtReceiptSupplierId = txtReceiptSupplierId;
		this.sessionBean=sessionBean;
		this.setCaption("FIND SECTION INFO :: "+sessionBean.getCompany());
		this.center();
		this.setWidth("550px");
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setModal(true);
		this.setResizable(false);
		this.frmName=frmName;
		this.setStyleName("cwindow");
		compInit();
		compAdd();
		tableInitialise();
		setEventAction();
		tableclear();
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
		lbSectionID.add(ar, new Label(""));
		lbSectionID.get(ar).setWidth("100%");
		lbSectionID.get(ar).setImmediate(true);
		lbSectionID.get(ar).setHeight("23px");

		lbSectionName.add(ar, new Label(""));
		lbSectionName.get(ar).setWidth("100%");
		lbSectionName.get(ar).setImmediate(true);
		lbSectionName.get(ar).setHeight("23px");


		table.addItem(new Object[]{lbSectionID.get(ar),lbSectionName.get(ar)},ar);
	}

	public void setEventAction()
	{
		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					receiptSupplierId = lbSectionID.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					txtReceiptSupplierId.setValue(receiptSupplierId);
					windowClose();
				}
			}
		});
	}

	private void tableclear()
	{
		for(int i=0; i<lbSectionID.size(); i++)
		{
			lbSectionID.get(i).setValue("");
			lbSectionName.get(i).setValue("");
		}
	}

	private void tableDataAdding()
	{
		try{
			Session session = com.common.share.SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			String query ="select vSectionId,vSectionName,isActive from tbSectionInfo order by vSectionName";
			System.out.println("Increment : "+query);
			List list = session.createSQLQuery(query).list();
			if(!list.isEmpty())
			{
				int i=0;
				for(Iterator iter = list.iterator(); iter.hasNext();)
				{						  
					Object[] element = (Object[]) iter.next();

					lbSectionID.get(i).setValue(element[0]);
					lbSectionName.get(i).setValue(element[1]);

					if((i)==lbSectionID.size()-1) {
						tableRowAdd(i+1);
					}
					i++;
				}
			}
			else {
				tableclear();
				showNotification("Data not Found !!", Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch (Exception exp) {
			
			showNotification("Error"+exp, Notification.TYPE_ERROR_MESSAGE);
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

		table.addContainerProperty("Section ID", Label.class, new Label());
		table.setColumnWidth("Section ID",100);

		table.addContainerProperty("Section Name", Label.class, new Label());
		table.setColumnWidth("Section Name",350);

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