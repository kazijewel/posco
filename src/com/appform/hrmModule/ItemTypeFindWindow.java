package com.appform.hrmModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.hibernate.Session;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class ItemTypeFindWindow extends Window
{
	private VerticalLayout mainLayout=new VerticalLayout();
	private FormLayout cmbLayout=new FormLayout();
	private HorizontalLayout btnLayout=new HorizontalLayout();
	private TextField txtReceiptSupplierId;
	private Table table=new Table();
	public String receiptId = "";
	private TextField txtFindFilter = new TextField();
	private ArrayList<Label> tbLblId = new ArrayList<Label>();
	private ArrayList<Label> tbLblSl = new ArrayList<Label>();
	private ArrayList<Label> tbLblName = new ArrayList<Label>();

	SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss aa");

	private String frmNam;
	@SuppressWarnings("unused")
	private SessionBean sessionBean;

	public ItemTypeFindWindow(SessionBean sessionBean,TextField txtReceiptSupplierId,String frmName)
	{
		this.txtReceiptSupplierId = txtReceiptSupplierId;
		this.sessionBean=sessionBean;
		this.frmNam=frmName;
		this.setCaption("ITEM FIND WINDOW :: "+sessionBean.getCompany());
		this.center();
		this.setWidth("420px");
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setModal(true);
		this.setResizable(false);
		this.setStyleName("cwindow");
		
		if(frmNam.equals("Item"))
		{
			compInit();
			txtFindFilter.setCaption("Type here & hit enter to find "+frmName.toLowerCase()+"");
			txtFindFilter.setImmediate(true);
			txtFindFilter.setWidth("200px");
			mainLayout.addComponent(txtFindFilter);
			mainLayout.setComponentAlignment(txtFindFilter, Alignment.TOP_CENTER);
			compAdd();
			tableInitialise();
			setEventAction();
			tableDataItem("%");
		}
	}

	public void setEventAction()
	{		
		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					receiptId = tbLblId.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					txtReceiptSupplierId.setValue(receiptId);
					windowClose();
				}
			}
		});

		txtFindFilter.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtFindFilter.getValue().toString().isEmpty())
				{
					if(frmNam.equals("Item"))
					{
						tableDataItem("%"+txtFindFilter.getValue().toString()+"%");
					}
				}
			}
		});
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
		tbLblId.add(ar, new Label(""));
		tbLblId.get(ar).setWidth("100%");
		tbLblId.get(ar).setImmediate(true);

		tbLblSl.add(ar, new Label(""));
		tbLblSl.get(ar).setWidth("100%");
		tbLblSl.get(ar).setImmediate(true);
		tbLblSl.get(ar).setHeight("15px");
		tbLblSl.get(ar).setValue(ar+1);

		tbLblName.add(ar, new Label(""));
		tbLblName.get(ar).setWidth("100%");
		tbLblName.get(ar).setImmediate(true);

		table.addItem(new Object[]{tbLblId.get(ar),tbLblSl.get(ar),tbLblName.get(ar)},ar);
	}

	private void tableclear()
	{
		for(int i=0; i<tbLblSl.size(); i++)
		{
			tbLblSl.get(i).setValue("");
			tbLblId.get(i).setValue("");
			tbLblName.get(i).setValue("");
		}
	}

	private void tableDataItem(String Item)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query ="select vItemTypeId,vItemTypeName from tbItemTypeInfo where" +
					" vItemTypeName like '"+Item+"' ";
			List <?> list = session.createSQLQuery(query).list();

			tableclear();
			int i=0;
			for(Iterator <?> iter = list.iterator(); iter.hasNext();)
			{						  
				Object[] element = (Object[]) iter.next();
				tbLblSl.get(i).setValue(i+1);
				tbLblId.get(i).setValue(element[0]);
				tbLblName.get(i).setValue(element[1]);

				if((i)==tbLblId.size()-1)
				{
					tableRowAdd(i+1);
				}
				i++;
			}

			if(i==0)
			{
				showNotification("Warning!","No data found.",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch (Exception ex)
		{
			showNotification("tableDataItem", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			session.close();
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

		table.setImmediate(true);
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);		

		table.addContainerProperty(""+frmNam+" Id", Label.class, new Label());
		table.setColumnWidth(""+frmNam+" Id",20);

		table.addContainerProperty("SL#", Label.class, new Label());
		table.setColumnWidth("SL#",20);

		table.addContainerProperty(""+frmNam+" Name", Label.class, new Label());
		table.setColumnWidth(""+frmNam+" Name",310);

		table.setColumnCollapsed(""+frmNam+" Id", true);		
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