package com.appform.hrmModule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Transaction;
import org.hibernate.classic.Session;

import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

public class ItemDistributionFindWindow extends Window{
	private SessionBean sessionBean;
	private TextField  txtReceiptId;
	AbsoluteLayout mainLayout;
	ComboBox cmbItemName;
	Table findTable;
	ArrayList<Label> tblblSl=new ArrayList<Label>();
	ArrayList<Label> tblblTransactionId=new ArrayList<Label>();
	ArrayList<Label> tblblSectionId=new ArrayList<Label>();
	ArrayList<Label> tblblSectionName=new ArrayList<Label>();
	ArrayList<Label> tblblDate=new  ArrayList<Label>();
	public ItemDistributionFindWindow(SessionBean sessionBean,TextField txtFindId)
	{
		this.txtReceiptId=txtFindId;
		this.sessionBean=sessionBean;
		this.center();
		this.setResizable(false);
		this.setModal(true);
		this.setCaption("ITEM DISTRIBUTION FIND WINDOW :: "+sessionBean.getCompany());
		buildMainLayout();
		this.setContent(mainLayout);
		this.setStyleName("cwindow");
		cmbItemNameDataLoad();
		tableRowadd();
		setEventAction();
	}
	private void setEventAction()
	{
		cmbItemName.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
				tableClear();
				if(cmbItemName.getValue()!=null)
				{
					tableDataLoad();
				}
			}
		});
		findTable.addListener(new ItemClickListener() {
			
			public void itemClick(ItemClickEvent event) {
				if(event.isDoubleClick())
				{
					txtReceiptId.setValue(tblblTransactionId.get(Integer.valueOf(event.getItemId().toString())).getValue().toString());
					close();
				}
			}
		});
	}
	private void tableClear()
	{
		for(int x=0;x<tblblTransactionId.size();x++)
		{
			tblblTransactionId.get(x).setValue("");
			tblblSectionId.get(x).setValue("");
			tblblSectionName.get(x).setValue("");
			tblblDate.get(x).setValue("");
		}
	}
	private void tableDataLoad()
	{
		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx=session.beginTransaction();
		try{
			String sql="select distinct iTransactionID,vSectionId,vSectionName,dDate from tbItemDistribution " +
			"where vItemName='"+cmbItemName.getItemCaption(cmbItemName.getValue())+"' ";
			List<?> list=session.createSQLQuery(sql).list();
			System.out.println("Table data :"+sql);
			if(!list.isEmpty())
			{
				int index=0;
				Iterator<?> iter=list.iterator();
				while(iter.hasNext())
				{
					Object element[]=(Object[])iter.next();
					tblblTransactionId.get(index).setValue(element[0]);
					tblblSectionId.get(index).setValue(element[1]);
					tblblSectionName.get(index).setValue(element[2].toString());
					tblblDate.get(index).setValue(element[3]);
					if(index==tblblSectionId.size()-1)
					{
						addRow(index+1);
					}
					index++;	
				}
			}
			else{showNotification("No data found!");}
		}catch(Exception exp)
		{
			showNotification("Table data Error.. "+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private void tableRowadd()
	{
		for(int a=0; a<12;a++)
		{
			addRow(a);
		}
	}
	private void addRow(int ar)
	{
		tblblSl.add(new Label(""+(ar+1)));
		tblblSl.get(ar).setWidth("100%");
		
		tblblTransactionId.add(new Label());
		tblblTransactionId.get(ar).setWidth("100%");
		
		tblblSectionId.add(new Label());
		tblblSectionId.get(ar).setWidth("100%");
		
		tblblSectionName.add(new Label());
		tblblSectionName.get(ar).setWidth("100%");
		
		tblblDate.add(new Label());
		tblblDate.get(ar).setWidth("100%");
		
		findTable.addItem(new Object[]{tblblSl.get(ar),tblblTransactionId.get(ar),tblblSectionId.get(ar),tblblSectionName.get(ar),tblblDate.get(ar)},ar);
		
	}
	private void cmbItemNameDataLoad()
	{
		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx=session.beginTransaction();
		try{
			String sql="select distinct vItemId,vItemName from tbItemDistribution order by vItemName";
			Iterator<?> iter=session.createSQLQuery(sql).list().iterator();
			while(iter.hasNext())
			{
				Object element[]=(Object[])iter.next();
				cmbItemName.addItem(element[0]);
				cmbItemName.setItemCaption(element[0], element[1].toString());
			}
		}catch(Exception exp)
		{
			showNotification("cmbItemNameDataLoad"+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private void buildMainLayout()
	{
		mainLayout=new AbsoluteLayout();
		mainLayout.setWidth("450px");
		mainLayout.setHeight("350px");
		
		cmbItemName=new ComboBox();
		cmbItemName.setWidth("250px");
		cmbItemName.setHeight("-1px");
		cmbItemName.setImmediate(true);
		mainLayout.addComponent(new Label("<b>Item Name :</b>",Label.CONTENT_XHTML),"top:30px; left:30px");
		mainLayout.addComponent(cmbItemName," top:28px; left:100px");
		
		
		findTable=new Table();
		findTable.setWidth("420px");
		findTable.setHeight("97%");
		findTable.setImmediate(true);
		findTable.setSelectable(true);
		findTable.addContainerProperty("Sl", Label.class, new Label());
		findTable.setColumnWidth("Sl", 20);
		
		findTable.addContainerProperty("ID", Label.class, new Label());
		findTable.setColumnWidth("ID", 40);
		
		findTable.addContainerProperty("Section ID", Label.class, new Label());
		findTable.setColumnWidth("Section ID", 50);
		
		findTable.addContainerProperty("Section Name", Label.class, new Label());
		findTable.setColumnWidth("Section Name", 165);
		
		findTable.addContainerProperty("Date", Label.class, new Label());
		findTable.setColumnWidth("Date", 75);
		
		findTable.setColumnAlignments(new String[]{findTable.ALIGN_CENTER,findTable.ALIGN_CENTER,findTable.ALIGN_CENTER,findTable.ALIGN_LEFT,findTable.ALIGN_LEFT});
		
		mainLayout.addComponent(findTable,"top:60px; left:15px");
		
		
	}
}
