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

public class GradeInfoFind extends Window
{
	private VerticalLayout mainLayout=new VerticalLayout();
	private FormLayout cmbLayout=new FormLayout();
	private HorizontalLayout btnLayout=new HorizontalLayout();
	private TextField txtReceiptSupplierId;
	private Table table=new Table();

	private String[] co=new String[]{"a","b"};
	public String receiptSupplierId = "";

	private ArrayList<Label> lbGradeId = new ArrayList<Label>();
	private ArrayList<Label> lbGrade = new ArrayList<Label>();
	private ArrayList<Label> lbGRADESerial=new ArrayList<Label>();

	
	private String frmName;
	private com.common.share.SessionBean sessionBean;
	public GradeInfoFind(com.common.share.SessionBean sessionBean,TextField txtReceiptSupplierId,String frmName)
	{
		this.txtReceiptSupplierId = txtReceiptSupplierId;
		this.sessionBean=sessionBean;
		this.setCaption("FIND GRADE INFO :: "+sessionBean.getCompany());
		this.center();
		this.setWidth("450px");
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
		lbGradeId.add(ar, new Label(""));
		lbGradeId.get(ar).setWidth("100%");
		lbGradeId.get(ar).setImmediate(true);
		lbGradeId.get(ar).setHeight("23px");

		lbGrade.add(ar, new Label(""));
		lbGrade.get(ar).setWidth("100%");
		lbGrade.get(ar).setImmediate(true);
		lbGrade.get(ar).setHeight("23px");
		
		lbGRADESerial.add(ar, new Label(""));
		lbGRADESerial.get(ar).setWidth("100%");
		lbGRADESerial.get(ar).setImmediate(true);
		lbGRADESerial.get(ar).setHeight("23px");
		
		table.addItem(new Object[]{lbGradeId.get(ar),lbGrade.get(ar),lbGRADESerial.get(ar)},ar);		
	}
	
	public void setEventAction()
	{

		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					receiptSupplierId = lbGradeId.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					txtReceiptSupplierId.setValue(receiptSupplierId);
					windowClose();
				}
			}
		});
	}
	private void tableclear()
	{
		for(int i=0; i<lbGradeId.size(); i++)
		{
			lbGradeId.get(i).setValue("");
			lbGrade.get(i).setValue("");
			lbGRADESerial.get(i).setValue("");
		}
	}

	
	
	private void tableDataAdding()
	{
		try{
					
			
			Session session = com.common.share.SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			String query ="Select vGradeId,vGrade,iGradeSerial From tbGradeInfo order by vGradeId";
	
			System.out.println("Increment : "+query);
			List list = session.createSQLQuery(query).list();
			if(!list.isEmpty())
			{
				int i=0;
				for(Iterator iter = list.iterator(); iter.hasNext();)
				{						  
					Object[] element = (Object[]) iter.next();
					lbGradeId.get(i).setValue(element[0]);
					lbGrade.get(i).setValue(element[1]);
					lbGRADESerial.get(i).setValue(element[2]);

					if((i)==lbGradeId.size()-1) {
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
				showNotification("Error table data add :"+ex,Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	private void windowClose()
	{
		this.close();
	}

	private void compInit()
	{
		mainLayout.setSpacing(false);
		table.setSelectable(true);
		table.setWidth("100%");
		table.setHeight("250px");

		table.setImmediate(true); // react at once when something is selected
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);		

		table.addContainerProperty("GRADE ID", Label.class, new Label());
		table.setColumnWidth("GRADE ID",100);

		table.addContainerProperty("GRADE", Label.class, new Label());
		table.setColumnWidth("GRADE",180);
	
		table.addContainerProperty("GRADE SERIAL", Label.class, new Label());
		table.setColumnWidth("GRADE SERIAL",50);
		
	}
	
	private void compAdd()
	{	cmbLayout.setSpacing(false);
		mainLayout.addComponent(cmbLayout);
		mainLayout.addComponent(btnLayout);
		mainLayout.addComponent(table);
		addComponent(mainLayout);
	}
}
