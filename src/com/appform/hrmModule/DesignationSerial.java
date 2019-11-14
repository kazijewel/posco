package com.appform.hrmModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.AmountField;
import com.common.share.CommonButton;
import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

public class DesignationSerial extends Window 
{
	
	CommonButton button = new CommonButton("", "Save", "Edit", "","","","","","","Exit");

	private VerticalLayout leftVertical = new VerticalLayout();
	private VerticalLayout rightVerticalLayout = new VerticalLayout();
	private HorizontalLayout topHorizontalLayout = new HorizontalLayout();

	private VerticalLayout mainLayout = new VerticalLayout();
	private HorizontalLayout btnLayout = new HorizontalLayout();
	private HorizontalLayout space = new HorizontalLayout();

	private Label lblComment = new Label("<br>Here you can update the sequence<br> of Designation.For this, you have to<br> reorder the SL# of Designation.",Label.CONTENT_XHTML);

	public Table table = new Table();
	public ArrayList<AmountField> tblTxtSerial = new ArrayList<AmountField>();
	public ArrayList<Label> tbllblDesignation = new ArrayList<Label>();
	public ArrayList<Label> tbMainSl = new ArrayList<Label>();

	private String comWidth = "230px";
	
	private boolean isUpdate = false;
	
	private SessionBean sessionBean;

	public DesignationSerial(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("DESIGNATION SERIAL :: "+sessionBean.getCompany());
		this.setWidth("620px");
		this.setResizable(false);
		
		init();
		tableInitialise();
		addComp();
		btnAction();
		authenticationCheck();
		button.btnEdit.focus();
		btnInit(false);
	}
	

	private void authenticationCheck()
	{
		if(!sessionBean.isSubmitable())
		{
			button.btnSave.setVisible(false);
		}

		if(!sessionBean.isUpdateable())
		{
			button.btnEdit.setVisible(false);
		}

		if(!sessionBean.isDeleteable())
		{
			button.btnDelete.setVisible(false);
		}
	}

	public void init()
	{
		lblComment.setHeight("120px");
		lblComment.setHeight("80px");
		lblComment.setStyleName("newLabel");

		table.setWidth("440");
		table.setHeight("230px");
		table.addContainerProperty("SL#", AmountField.class , new AmountField());
		table.setColumnWidth("SL#",70);
		table.addContainerProperty("Designation", Label.class , new Label());
		table.setColumnWidth("Designation",220);
		table.addContainerProperty("Main Sl", Label.class , new Label());
		table.setColumnWidth("Main Sl",30);
		
		table.setColumnCollapsingAllowed(true);
		table.setColumnCollapsed("Main Sl", true);
	}

	public void addComp()
	{
		leftVertical.addComponent(lblComment);
		rightVerticalLayout.addComponent(table);

		topHorizontalLayout.addComponent(leftVertical);
		topHorizontalLayout.setSpacing(true);
		topHorizontalLayout.addComponent(rightVerticalLayout);

		btnLayout.addComponent(button);	
		btnLayout.setSpacing(true);
		mainLayout.addComponent(topHorizontalLayout);
		mainLayout.addComponent(btnLayout);
		mainLayout.setSpacing(true);
		space.setWidth("80px");
		mainLayout.setMargin(true);
		mainLayout.setComponentAlignment(btnLayout, Alignment.BOTTOM_CENTER);
		addComponent(mainLayout);
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
		try
		{	
			tblTxtSerial.add(ar,new AmountField());
			tblTxtSerial.get(ar).setWidth("100%");
			
			tbllblDesignation.add(ar,new Label());
			tbllblDesignation.get(ar).setWidth("100%");
			
			tbMainSl.add(ar,new Label());
			tbMainSl.get(ar).setWidth("100%");

			table.addItem(new Object[]{tblTxtSerial.get(ar),tbllblDesignation.get(ar),tbMainSl.get(ar)},ar);
		}
		catch(Exception exp)
		{
			System.out.println(exp);
		}
	}

	private void btnAction()
	{
		button.btnEdit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate = true;
				btnInit(true);
				selectDesignationName();
			}
		});

		button.btnSave.addListener( new Button.ClickListener() 
		{			
			public void buttonClick(ClickEvent event)
			{
				saveBtnAction(event);
				isUpdate = false;
			}
		});

		button.btnRefresh.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate = false;
				tableClear();
				btnInit(false);
			}
		});
	}

	private void selectDesignationName()
	{
		try
		{
			tableClear();
	
			String query = "select ordersl,designationId,designationName,designationId from tbDesignationInfo";
			System.out.println(query);
			
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			
			List list = session.createSQLQuery(query).list();
			
			int i = 0;
			
			for(Iterator iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				
				if (element[0] != null)
				{
					tblTxtSerial.get(i).setValue(element[0]);					
				}

				if(element[2] != null)
				{
					tbllblDesignation.get(i).setValue(element[2]);
				}
				
				tbMainSl.get(i).setValue(element[3]);
				
				if((i+1)==tblTxtSerial.size())
				{
					tableRowAdd(i+1);
				}
				
				i++;
			}
		}
		catch(Exception ex)
		{
			this.getParent().showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void tableClear()
	{
		for(int i=0;i<tblTxtSerial.size();i++)
		{
			tblTxtSerial.get(i).setValue("");
			tbllblDesignation.get(i).setValue("");
			tbMainSl.get(i).setValue("");
		}
	}

	private void saveBtnAction(ClickEvent e)
	{
		try
		{
			MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to update information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType== ButtonType.YES)
					{
					
						if(duplicateCheck())
						{
							saveData();
							button.btnSave.setEnabled(false);
						}
					}
				}
			});
		}
		catch(Exception ex)
		{
			this.getParent().showNotification("Error.", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}	
	
	private boolean duplicateCheck()
	{
		for(int i=0;i<tblTxtSerial.size();i++)
		{
			for(int j=i+1;i<tblTxtSerial.size();j++)
			{
				if(tblTxtSerial.size()!=j)
				{
					if(tblTxtSerial.get(i).getValue().toString().equalsIgnoreCase(tblTxtSerial.get(j).getValue().toString()))
					{
						showNotification("Warning","Check Sl ",Notification.TYPE_WARNING_MESSAGE);
						return false;
					}
				}
				else
					break;
			}
		}
		
		return true;
	}

	private void btnInit(boolean t)
	{
		button.btnEdit.setEnabled(!t);
		button.btnSave.setEnabled(t);
		button.btnRefresh.setEnabled(!t);
	}

	private void saveData()
	{
		Transaction tx;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();

		try
		{	
			for(int i = 0;i<tblTxtSerial.size();i++)
			{
				if(!tblTxtSerial.get(i).getValue().toString().isEmpty() && !tbllblDesignation.get(i).getValue().toString().isEmpty())
				{
					String sql = " update tbDesignationInfo set orderSl = '"+tblTxtSerial.get(i)+"' where designationId = '"+tbMainSl.get(i)+"'  ";

					session.createSQLQuery(sql).executeUpdate();
					System.out.println(sql);
				}
			}

			tx.commit();
			this.getParent().showNotification("Information Updated Successfully.");

			tableClear();
			btnInit(false);
			isUpdate = false;
		}
		catch(Exception ex)
		{
			this.getParent().showNotification("Error in Update", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
			btnInit(true);
		}
	}
}
