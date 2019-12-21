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
import com.common.share.CommonButton;
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

public class IncrementFindWindow extends Window
{
	private VerticalLayout mainLayout=new VerticalLayout();
	private FormLayout cmbLayout=new FormLayout();
	private HorizontalLayout hLayout=new HorizontalLayout();
	private HorizontalLayout btnLayout=new HorizontalLayout();
	private PopupDateField dMonth=new PopupDateField();
	private Label lblFrom=new Label("Month:");
	private TextField txtReceiptSupplierId;
	private Table table=new Table();

	private String[] co=new String[]{"a","b"};
	public String receiptSupplierId = "";

	private ArrayList<Label> lbSlID = new ArrayList<Label>();
	private ArrayList<Label> lbSectionName = new ArrayList<Label>();
	private ArrayList<Label> lbDate = new ArrayList<Label>();
	
	private SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
	CommonButton cButton = new CommonButton("", "", "", "","","Find","","","","");
	private String frmName;
	private com.common.share.SessionBean sessionBean;
	public IncrementFindWindow(com.common.share.SessionBean sessionBean,TextField txtReceiptSupplierId,String frmName)
	{
		this.txtReceiptSupplierId = txtReceiptSupplierId;
		this.sessionBean=sessionBean;
		this.setCaption("FIND SALARY INCREMENT INFO :: "+sessionBean.getCompany());
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
		//tableDataAdding();
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
		lbSlID.add(ar, new Label(""));
		lbSlID.get(ar).setWidth("100%");
		lbSlID.get(ar).setImmediate(true);
		lbSlID.get(ar).setHeight("23px");
		
		lbDate.add(ar, new Label(""));
		lbDate.get(ar).setWidth("100%");
		lbDate.get(ar).setImmediate(true);
		lbDate.get(ar).setHeight("23px");

		lbSectionName.add(ar, new Label(""));
		lbSectionName.get(ar).setWidth("100%");
		lbSectionName.get(ar).setImmediate(true);
		lbSectionName.get(ar).setHeight("23px");

		table.addItem(new Object[]{lbSlID.get(ar),lbDate.get(ar),lbSectionName.get(ar)},ar);
	}

	public void setEventAction()
	{
		cButton.btnFind.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				tableDataAdding();
			}
		});
		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					receiptSupplierId = lbSlID.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					txtReceiptSupplierId.setValue(receiptSupplierId);
					windowClose();
				}
			}
		});
	}

	private void tableclear()
	{
		for(int i=0; i<lbSlID.size(); i++)
		{
			lbSlID.get(i).setValue("");
			lbSectionName.get(i).setValue("");
		}
	}

	private void tableDataAdding()
	{
		try{
			Session session = com.common.share.SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			String query = " select iAutoId,dInrementDate,vSectionName from tbSalaryIncrement " +
					       " where month(dInrementDate)=month('"+df.format(dMonth.getValue())+"') order by iAutoId";
			System.out.println("Increment : "+query);
			List list = session.createSQLQuery(query).list();
			if(!list.isEmpty())
			{
				int i=0;
				for(Iterator iter = list.iterator(); iter.hasNext();)
				{						  
					Object[] element = (Object[]) iter.next();

					lbSlID.get(i).setValue(element[0]);
					lbDate.get(i).setValue(element[1]);
					lbSectionName.get(i).setValue(element[2]);

					if((i)==lbSlID.size()-1) {
						tableRowAdd(i+1);
					}
					i++;
				}
			}
			else {
				tableclear();
				this.getParent().showNotification("Data not Found !!", Notification.TYPE_WARNING_MESSAGE); 
			}
		}
		catch (Exception ex) {
			this.getParent().showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void windowClose()
	{
		this.close();
	}

	private void compInit()
	{
		mainLayout.setSpacing(true);
		
		dMonth.setValue(new java.util.Date());
		dMonth.setResolution(PopupDateField.RESOLUTION_MONTH);
		dMonth.setDateFormat("MMM-yyyy");
		dMonth.setInvalidAllowed(false);
		dMonth.setImmediate(true);
		
		table.setSelectable(true);
		table.setWidth("100%");
		table.setHeight("250px");

		table.setImmediate(true); // react at once when something is selected
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);		

		table.addContainerProperty("ID", Label.class, new Label());
		table.setColumnWidth("ID",20); 
		
		table.addContainerProperty("Date", Label.class, new Label());
		table.setColumnWidth("Date",180);
	
		table.addContainerProperty("Section Name", Label.class, new Label());
		table.setColumnWidth("Section Name",230);
	}

	private void compAdd()
	{
		cmbLayout.setSpacing(true);
		hLayout.addComponent(lblFrom);
		hLayout.addComponent(dMonth);
		hLayout.addComponent(cButton.btnFind);
		mainLayout.addComponent(hLayout);
		mainLayout.addComponent(cmbLayout);
		mainLayout.addComponent(btnLayout);
		mainLayout.addComponent(table);
		addComponent(mainLayout);
	}
}