package com.appform.hrmModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.SessionBean;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class NoticeInfoFind extends Window
{
	private VerticalLayout mainLayout=new VerticalLayout();
	private FormLayout cmbLayout=new FormLayout();
	private HorizontalLayout btnLayout=new HorizontalLayout();
	private TextField txtReceiptSupplierId;
	private Table table=new Table();

	private String[] co=new String[]{"a","b"};
	public String receiptSupplierId = "";

	private ArrayList<Label> tblblSl = new ArrayList<Label>();
	private ArrayList<Label> lblNoticeID = new ArrayList<Label>();
	private ArrayList<Label> dNoticeDate = new ArrayList<Label>();
	private ArrayList<Label> lblNoticeSubject = new ArrayList<Label>();
	//private ArrayList<Label> lblNoticeDescription = new ArrayList<Label>();
	private SimpleDateFormat dFormat=new SimpleDateFormat("dd-MM-yyyy");

	private SessionBean sessionBean=new SessionBean();
	public NoticeInfoFind(SessionBean sessionBean,TextField txtReceiptSupplierId)
	{
		this.txtReceiptSupplierId = txtReceiptSupplierId;
		this.sessionBean=sessionBean;
		this.setCaption("NOTICE INFORMATION FIND :: "+sessionBean.getCompany());
		this.center();
		this.setWidth("550px");
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setModal(true);
		this.setResizable(false);
		this.setStyleName("cwindow");
		compInit();
		compAdd();
		tableInitialise();
		setEventAction();
		tableclear();
		tableDataLoad();
	}

	public void tableInitialise()
	{
		for(int i=0;i<8;i++)
		{
			tableRowAdd(i);
		}
	}

	public void tableRowAdd(final int ar)
	{
		tblblSl.add(ar, new Label(""));
		tblblSl.get(ar).setWidth("100%");
		tblblSl.get(ar).setImmediate(true);
		tblblSl.get(ar).setValue(ar+1);
		tblblSl.get(ar).setHeight("20px");

		lblNoticeID.add(ar, new Label(""));
		lblNoticeID.get(ar).setWidth("100%");
		lblNoticeID.get(ar).setImmediate(true);

		dNoticeDate.add(ar, new Label(""));
		dNoticeDate.get(ar).setWidth("100%");
		dNoticeDate.get(ar).setImmediate(true);

		lblNoticeSubject.add(ar, new Label(""));
		lblNoticeSubject.get(ar).setWidth("100%");
		lblNoticeSubject.get(ar).setImmediate(true);

		table.addItem(new Object[]{tblblSl.get(ar),lblNoticeID.get(ar),dNoticeDate.get(ar),lblNoticeSubject.get(ar)},ar);
	}

	public void setEventAction()
	{
		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					receiptSupplierId = lblNoticeID.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					txtReceiptSupplierId.setValue(receiptSupplierId);
					windowClose();
				}
			}
		});
	}

	private void tableclear()
	{
		for(int i=0; i<lblNoticeID.size(); i++)
		{
			lblNoticeID.get(i).setValue("");
			lblNoticeSubject.get(i).setValue("");
		}
	}

	private void tableDataLoad()
	{

		Session session = com.common.share.SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx = session.beginTransaction();
		try{
			String query ="select vNoticeId,dDate,vSubject from tbNoticeInfo order by dDate desc";
	
			System.out.println("Increment : "+query);
			List list = session.createSQLQuery(query).list();
			if(!list.isEmpty())
			{
				int i=0;
				for(Iterator iter = list.iterator(); iter.hasNext();)
				{						  
					Object[] element = (Object[]) iter.next();

					lblNoticeID.get(i).setValue(element[0]);
					dNoticeDate.get(i).setValue(dFormat.format(element[1]));
					lblNoticeSubject.get(i).setValue(element[2].toString().replaceAll("~", "'"));

					if((i)==lblNoticeID.size()-1) {
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
		mainLayout.setSpacing(true);
		table.setSelectable(true);
		table.setWidth("100%");
		table.setHeight("250px");

		table.addContainerProperty("SL#", Label.class, new Label());
		table.setColumnWidth("SL#", 20);
		
		table.setImmediate(true); // react at once when something is selected
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);		

		table.addContainerProperty("Trans. ID", Label.class, new Label());
		table.setColumnWidth("Trans. ID",70);

		table.addContainerProperty("Date", Label.class, new Label());
		table.setColumnWidth("Date",80);

		table.addContainerProperty("Subject", Label.class, new Label());
		table.setColumnWidth("Subject",360);
		
		table.setColumnCollapsed("Trans. ID", true);
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