package com.appform.hrmModule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class Designation extends Window {

	private CommonButton button = new CommonButton( "New",  "Save",  "Edit",  "",  "Refresh",  "Find", "", "","","Exit");

	private AbsoluteLayout mainLayout;
	private TextField txtDesignationName;
	private Label lblDesignation;
	private TextRead txtDesignationId;
	private Label lblDesignationId;
	private TextField txtDesignationBan;
	private Label lblline= new Label("__________________________________________________________________________");
	private ArrayList<Component> allComp = new ArrayList<Component>(); 

	private boolean isUpdate=false;
	private TextField txtDesignationIdFromFind = new TextField();

	String username="";
	String userIp="";
	SessionBean sessionBean;
	public Designation(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setCaption("DESIGNATION INFORMATION :: "+sessionBean.getCompany());
		buildMainLayout();
		setContent(mainLayout);
		compInit(true);
		btnint(true);
		setEventAction();
		authenticationCheck();
	}

	private void authenticationCheck()
	{
		if(!sessionBean.isSubmitable()){
			button.btnSave.setVisible(false);
		}

		if(!sessionBean.isUpdateable()){
			button.btnEdit.setVisible(false);
		}

		if(!sessionBean.isDeleteable()){
			button.btnDelete.setVisible(false);
		}
	}

	private void setEventAction()
	{		
		button.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) {
				txtDesignationName.focus();
				newBtnAction();
				System.out.println("New");
			}
		});

		button.btnEdit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) {
				{
					isUpdate = true;
					if(sessionBean.isUpdateable())
					{
						if(!txtDesignationId.getValue().toString().isEmpty())
						{
							isUpdate = true;
							updateButtonEvent();
						}
						else
							showNotification(
									"Update Failed",
									"There is no data for update.",
									Notification.TYPE_WARNING_MESSAGE);	
					}
					else
					{
						showNotification("Warning,","You are not permitted to edit data.",Notification.TYPE_WARNING_MESSAGE);
					}	
				}
			}
		});

		button.btnSave.addListener( new Button.ClickListener() 
		{			
			public void buttonClick(ClickEvent event) {
				saveBtnAction(event);
			}
		});

		button.btnFind.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				findButtonEvent();
			}
		});

		button.btnRefresh.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) {
				txtClear();
				compInit(true);
				btnint(true);
			}
		});

		button.btnExit.addListener( new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) {
				close();
			}
		});
	}

	private void updateButtonEvent(){
		isUpdate = true;
		compInit(false);
		btnint(false);
	}

	private void findButtonEvent()
	{
		Window win=new DesignationFindWindow(sessionBean,txtDesignationIdFromFind,"designation");
		win.addListener(new Window.CloseListener()
		{
			public void windowClose(CloseEvent e)
			{
				if(txtDesignationIdFromFind.getValue().toString().length()>0)
				{
					txtClear();
					findInitialise(txtDesignationIdFromFind.toString());
				}
			}
		});
		this.getParent().addWindow(win);
	}

	private void findInitialise(String degId)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "select * from tbDesignationInfo where designationId = '"+degId+"'";
			List <?> lst = session.createSQLQuery(sql).list();
			if(lst.iterator().hasNext())
			{
				Object[] element = (Object[]) lst.iterator().next();
				txtDesignationId.setValue(element[1].toString());
				txtDesignationName.setValue(element[2].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void saveBtnAction(ClickEvent e)
	{
		if (sessionBean.isUpdateable())
		{	
			if (txtDesignationName.getValue().toString().trim().isEmpty())
			{
				this.getParent().showNotification("Warning :", "Please Enter Designation Name", Notification.TYPE_WARNING_MESSAGE);
			}
			else
			{
				if(isUpdate)
				{
					MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to update  information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
					mb.show(new EventListener()
					{
						public void buttonClicked(ButtonType buttonType)
						{
							if(buttonType == ButtonType.YES)
							{
								updateData();
							}
						}
					});
				}
				else
				{
					MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to save  information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
					mb.show(new EventListener()
					{
						public void buttonClicked(ButtonType buttonType)
						{
							if(buttonType == ButtonType.YES)
							{
								insertData();
							}
						}
					});
				}
			}
		}
		else
		{
			this.getParent().showNotification("Warning :", "You Are Not Permitted to Perform This Task", Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void insertData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{
			String query = "insert into tbDesignationInfo values('"+txtDesignationId.getValue().toString().trim()+"','"+txtDesignationName.getValue().toString().trim()+"'," +
					" N'Not Need','"+sessionBean.getUserIp()+"','"+sessionBean.getUserName()+"',CURRENT_TIMESTAMP)";
			session.createSQLQuery(query).executeUpdate();
			tx.commit();
			this.getParent().showNotification("All information save successfully.");
			txtClear();
			compInit(true);
			btnint(true);
			isUpdate = false;
		}
		catch(Exception ex)
		{
			tx.rollback();
			this.getParent().showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void updateData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{
			String query=" update tbDesignationInfo set designationName='"+txtDesignationName.getValue().toString().trim()+"'" +
					" where designationId = '"+txtDesignationId.getValue().toString().trim()+"' ";
			session.createSQLQuery(query).executeUpdate();
			tx.commit();
			this.getParent().showNotification("All information updated successfully.");
			txtClear();
			compInit(true);
			btnint(true);
			isUpdate=false;
			txtDesignationName.focus();

		}
		catch(Exception ex)
		{
			this.getParent().showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
		finally{session.close();}
	}

	private void focusEnter()
	{
		allComp.add(txtDesignationName);
		allComp.add(txtDesignationBan);
		allComp.add(button.btnNew);
		allComp.add(button.btnEdit);
		allComp.add(button.btnSave);
		allComp.add(button.btnRefresh);
		allComp.add(button.btnDelete);
		allComp.add(button.btnFind);

		new FocusMoveByEnter(this,allComp);
	}

	private void compInit(boolean t)
	{
		txtDesignationName.setEnabled(!t);
		txtDesignationId.setEnabled(!t);
	}

	private void btnint(boolean t)
	{
		button.btnNew.setEnabled(t);
		button.btnEdit.setEnabled(t);
		button.btnSave.setEnabled(!t);	
		button.btnRefresh.setEnabled(!t);
		button.btnDelete.setEnabled(t);
		button.btnFind.setEnabled(t);
	}

	private void txtClear()
	{
		txtDesignationName.setValue("");
		txtDesignationId.setValue("");
	}

	private void newBtnAction()
	{
		txtClear();
		compInit(false);
		btnint(false);
		focusEnter();
		txtDesignationId.setValue(autoIdGenerate());
	}

	private String autoIdGenerate()
	{
		String autoCode = "";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try 
		{
			String query = "Select isnull(max(designationId),0)+1 from tbDesignationInfo";
			Iterator <?> iter = session.createSQLQuery(query).list().iterator();
			if (iter.hasNext()) 
			{
				autoCode = iter.next().toString();
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		finally{session.close();}
		return autoCode;
	}

	private AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("545px");
		mainLayout.setHeight("180");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("570px");
		setHeight("220px");

		this.setResizable(false);

		// lblSecId
		lblDesignationId = new Label();
		lblDesignationId.setImmediate(false);
		lblDesignationId.setWidth("100.0%");
		lblDesignationId.setHeight("-1px");
		lblDesignationId.setValue("Designation ID :");
		mainLayout.addComponent(lblDesignationId,"top:30.0px; left:35.0px;");

		// txtSecId
		txtDesignationId = new TextRead();
		txtDesignationId.setImmediate(false);
		txtDesignationId.setWidth("80px");
		txtDesignationId.setHeight("24px");
		mainLayout.addComponent(txtDesignationId,"top:30.0px; left:180.0px;");

		// TxtSecName
		lblDesignation = new Label();
		lblDesignation.setImmediate(false);
		lblDesignation.setWidth("100.0%");
		lblDesignation.setHeight("-1px");
		lblDesignation.setValue("Designation Name :");
		mainLayout.addComponent(lblDesignation,"top:55.0px; left:35.0px;");

		// txtSecName
		txtDesignationName = new TextField();
		txtDesignationName.setImmediate(false);
		txtDesignationName.setWidth("220px");
		txtDesignationName.setHeight("-1px");
		mainLayout.addComponent(txtDesignationName,"top:55.0px; left:180.0px;");
		mainLayout.addComponent(lblline,"top:80.0px; left: 35.0px");
		mainLayout.addComponent(button, "top:120.0px;left:30.0px;");
		return mainLayout;
	}
}
