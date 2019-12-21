package com.appform.hrmModule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class LeaveTypeFindWindo extends Window
{
	private VerticalLayout mainLayout=new VerticalLayout();

	private HorizontalLayout btnLayout=new HorizontalLayout();

	private Table table=new Table();
	private String receiptleaveId ="";
	private TextField txtReceiptLeaveId;

	public String receiptOpeningYear = "";

	private ArrayList<Label> lbSL = new ArrayList<Label>();
	private ArrayList<Label> lblLeaveTypeId = new ArrayList<Label>();
	private ArrayList<Label> lblLeaveTypeName = new ArrayList<Label>();

	@SuppressWarnings("unused")
	private SessionBean sessionBean;

	public LeaveTypeFindWindo(SessionBean sessionBean,TextField txtReceiptLeaveId)
	{
		this.txtReceiptLeaveId = txtReceiptLeaveId;
		this.sessionBean=sessionBean;
		this.setCaption("Leave Type Find Window :: "+sessionBean.getCompany());

		this.center();
		this.setWidth("450px");
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setModal(true);
		this.setResizable(false);
		this.setStyleName("cwindow");

		compInit();
		compAdd();
		tableInitialise();
		tableDataAdding();
		setEventAction();
	}

	public void tableInitialise()
	{
		for(int i=0;i<5;i++)
		{
			tableRowAdd(i);
		}
	}

	public void tableRowAdd(final int ar)
	{
		lbSL.add(ar, new Label(""));
		lbSL.get(ar).setWidth("100%");
		lbSL.get(ar).setImmediate(true);
		lbSL.get(ar).setValue(ar+1);

		lblLeaveTypeId.add(ar, new Label(""));
		lblLeaveTypeId.get(ar).setWidth("100%");
		lblLeaveTypeId.get(ar).setImmediate(true);

		lblLeaveTypeName.add(ar, new Label(""));
		lblLeaveTypeName.get(ar).setWidth("100%");
		lblLeaveTypeName.get(ar).setImmediate(true);

		table.addItem(new Object[]{lbSL.get(ar),lblLeaveTypeId.get(ar),lblLeaveTypeName.get(ar)},ar);
	}

	public void setEventAction()
	{
		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					System.out.println("Ypu are Wrong");
					receiptleaveId = lblLeaveTypeId.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					txtReceiptLeaveId.setValue(receiptleaveId);
					System.out.println("Kasu: "+receiptleaveId);
					close();
				}
			}
		});
	}

	private void tableDataAdding()
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try
		{
			String selectQuery = "select vLeaveTypeId, vLeaveTypeName from tbLeaveType order by vLeaveTypeId " ;

			System.out.println("selectQuery : "+selectQuery);
			List <?> list = session.createSQLQuery(selectQuery).list();
			if(!list.isEmpty())
			{
				int i=0;
				for(Iterator <?> iter = list.iterator(); iter.hasNext();)
				{						  
					Object[] element = (Object[]) iter.next();

					lblLeaveTypeId.get(i).setValue(element[0]);
					lblLeaveTypeName.get(i).setValue(element[1]);

					if((i)==lblLeaveTypeName.size()-1)
					{
						tableRowAdd(lblLeaveTypeName.size());
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

	private void tableclear()
	{
		for(int i=0; i<lblLeaveTypeId.size(); i++)
		{
			lblLeaveTypeId.get(i).setValue("");
			lblLeaveTypeName.get(i).setValue("");
		}
	}

	private void compInit()
	{
		mainLayout.setSpacing(true);

		table.setSelectable(true);
		table.setWidth("100%");
		table.setHeight("150px");

		table.setImmediate(true); // react at once when something is selected
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);		

		table.addContainerProperty("SL #", Label.class, new Label());
		table.setColumnWidth("SL #",20);

		table.addContainerProperty("Leave Type Id", Label.class, new Label());
		table.setColumnWidth("Leave Type Id",60);

		table.addContainerProperty("Leave Type Name", Label.class, new Label());
		table.setColumnWidth("Leave Type Name",260);
	}

	private void compAdd()
	{
		mainLayout.addComponent(table);
		mainLayout.addComponent(btnLayout);
		addComponent(mainLayout);
	}
}