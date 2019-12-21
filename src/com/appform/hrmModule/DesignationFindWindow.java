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
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class DesignationFindWindow extends Window
{
	private VerticalLayout mainLayout=new VerticalLayout();
	private TextField txtReceiptId;
	private Table table=new Table();

	@SuppressWarnings("unused")
	private SessionBean sessionBean;
	public String receiptId;
	private ArrayList<Label> lblSl = new ArrayList<Label>();
	private ArrayList<Label> lblShiftId = new ArrayList<Label>();
	private ArrayList<Label> lblShiftName = new ArrayList<Label>();
	private String frmName;

	public DesignationFindWindow(SessionBean sessionBean,TextField txtReceiptId,String frmName)
	{
		this.txtReceiptId=txtReceiptId;
		this.sessionBean=sessionBean;
		this.setCaption("FIND WINDOW :: "+sessionBean.getCompany());
		this.center();
		this.frmName=frmName;
		this.setWidth("530px");
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setModal(true);
		this.setResizable(false);
		this.setStyleName("cwindow");
		compInit();
		compAdd();
		tableInitialise();
		setEventAction();
		dataLoad();
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
		lblSl.add(ar, new Label(""));
		lblSl.get(ar).setWidth("100%");
		lblSl.get(ar).setImmediate(true);
		lblSl.get(ar).setHeight("18px");

		lblShiftId.add(ar, new Label(""));
		lblShiftId.get(ar).setWidth("100%");
		lblShiftId.get(ar).setImmediate(true);
		lblShiftId.get(ar).setHeight("18px");

		lblShiftName.add(ar, new Label(""));
		lblShiftName.get(ar).setWidth("100%");
		lblShiftName.get(ar).setImmediate(true);
		lblShiftName.get(ar).setHeight("18px");
		table.addItem(new Object[]{lblSl.get(ar),lblShiftId.get(ar),lblShiftName.get(ar)},ar);
	}

	public void setEventAction()
	{
		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					receiptId=lblShiftId.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					System.out.println("Before:"+receiptId);
					txtReceiptId.setValue(receiptId);
					System.out.println("After:"+txtReceiptId);
					windowClose();
				}
			}
		});
	}

	private void windowClose()
	{
		this.close();
	}

	private void dataLoad()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			if(frmName.equals("designation"))
			{
				String sql = "select designationId,designationName from tbDesignationInfo";
				List <?> lst= session.createSQLQuery(sql).list();
				int i=0;
				if(!lst.isEmpty()){

					for (Iterator <?> iter = lst.iterator(); iter.hasNext();)
					{
						Object[] element = (Object[]) iter.next();

						lblSl.get(i).setValue(i+1);
						lblShiftId.get(i).setValue(element[0].toString());
						lblShiftName.get(i).setValue(element[1].toString());
						if((i)==lblShiftId.size()-1) 
						{
							tableRowAdd(i+1);
						}
						i++;
					}
				}
				else
				{
					getParent().showNotification("Warning: ","There are no Data.");
				}
			}
		}
		catch(Exception exp)
		{
			System.out.println(exp);
		}
		finally{session.close();}
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

		table.addContainerProperty("SL", Label.class, new Label());
		table.setColumnWidth("SL",30);

		table.addContainerProperty("Designation ID", Label.class, new Label());
		table.setColumnWidth("Shift ID",100);

		table.addContainerProperty("Designation Name", Label.class, new Label());
		table.setColumnWidth("Shift Name",300);

		table.setColumnAlignments(new String[] { Table.ALIGN_CENTER, Table.ALIGN_CENTER, Table.ALIGN_CENTER });

	}

	private void compAdd()
	{
		mainLayout.addComponent(table);
		addComponent(mainLayout);
	}
}