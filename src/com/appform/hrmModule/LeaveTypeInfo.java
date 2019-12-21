package com.appform.hrmModule;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.*;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class LeaveTypeInfo extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private CommonButton cButton = new CommonButton("", "Save", "Edit","", "Refresh", "Find", "", "", "", "Exit");

	private TextRead txtLeaveTypeId ;
	private TextField txLeaveTypeName ;

	private Label lblleaveTypeID;
	private Label lblleaveTypeName; 

	private boolean isUpdate=false;

	private TextField txtreceiptID = new TextField();
	int flag=1;
	private CommonMethod cm;
	private String menuId = "";
	public LeaveTypeInfo(SessionBean sessionBean,String menuId)
	{
		this.sessionBean = sessionBean;
		this.setCaption("LEAVE TYPE INFO. :: "+sessionBean.getCompany());

		this.setWidth("500px");
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		buildMainLayout();
		setContent(mainLayout);

		variousAction();

		componentIni(false) ;
		btnIni(false);
		authenticationCheck();
	}

	private void authenticationCheck()
	{
		cm.checkFormAction(menuId);
		if(!sessionBean.isSuperAdmin())
		{
		if(!sessionBean.isAdmin())
		{
			if(!cm.isSave)
			{cButton.btnSave.setVisible(false);}
			if(!cm.isEdit)
			{cButton.btnEdit.setVisible(false);}
			if(!cm.isDelete)
			{cButton.btnDelete.setVisible(false);}
			if(!cm.isPreview)
			{cButton.btnPreview.setVisible(false);}
		}
		}
	}

	private void variousAction()
	{
		cButton.btnEdit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) {
				updateBtnAction(event);
			}
		});

		cButton.btnFind.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				findButtonEvent();
			}
		});

		cButton.btnSave.addListener( new Button.ClickListener() 
		{			
			public void buttonClick(ClickEvent event) {
				saveBtnAction();
			}
		});

		cButton.btnRefresh.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) {

				clearBtnAction(event);
			}
		});

		cButton.btnExit.addListener( new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) {
				exitAction();
			}
		});
	}

	private void findButtonEvent() 
	{
		Window win = new LeaveTypeFindWindo(sessionBean, txtreceiptID);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if (txtreceiptID.getValue().toString().length() > 0)
				{
					txtClear();

					findInitialise(txtreceiptID.getValue().toString());
				}
			}
		});

		this.getParent().addWindow(win);
	}

	private void findInitialise( String txtid) 
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try 
		{
			String selectQuery = "select vLeaveTypeId,vLeaveTypeName from tbLeaveType" +
					" where vLeaveTypeId = '"+txtid+"'";

			List <?> list = session.createSQLQuery(selectQuery).list();

			if(!list.isEmpty())
			{
				if (list.iterator().hasNext()) 
				{
					Object[] element = (Object[]) list.iterator().next();

					txtLeaveTypeId.setValue(element[0]);
					txLeaveTypeName.setValue(element[1]);	
				}
			}
			else
			{
				this.getParent().showNotification("Warning!","No data found.",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch (Exception exp) 
		{
			this.getParent().showNotification("Error ", exp + "",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void exitAction()
	{
		this.close();
	}

	private void saveBtnAction()
	{
		if(!txLeaveTypeName.getValue().toString().trim().isEmpty())
		{
			if(isUpdate)
			{
				MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to update information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
				mb.setStyleName("cwindowMB");
				mb.show(new EventListener()
				{
					public void buttonClicked(ButtonType buttonType)
					{
						if(buttonType == ButtonType.YES)
						{
							cButton.btnSave.setEnabled(false);
							updateData();

							componentIni(false) ;
							btnIni(false);
							txtClear();

							isUpdate=false;
						}
					}
				});
			}
		}
		else
		{
			showNotification("Warning!", "Provide leave type.", Notification.TYPE_WARNING_MESSAGE);
		}
	}	

	private void clearBtnAction(ClickEvent e)
	{
		txtClear();
		componentIni(false) ;
		btnIni(false);
	}

	private void txtClear()
	{
		txLeaveTypeName.setValue("");
		txtLeaveTypeId.setValue("");
	}

	private void setEditable(boolean tf)
	{		
		txLeaveTypeName.setEnabled(tf);
	}

	private void updateBtnAction(ClickEvent e)
	{
		if (!txLeaveTypeName.getValue().toString().isEmpty())
		{
			componentIni(true) ;
			btnIni(true);
			setEditable(true);
			isUpdate=true;
		}
		else
		{
			showNotification("Warning!","There are no data to update.",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void updateData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{
			String query = " update tbLeaveType set" +
					" vLeaveTypeName = '"+txLeaveTypeName.getValue().toString().trim()+"'," +
					" vUserName = '"+sessionBean.getUserId()+"'," +
					" dEntryTime = current_timestamp," +
					" vUserIp = '"+sessionBean.getUserIp()+"'" +
					" where vLeaveTypeId = '"+txtLeaveTypeId.getValue().toString()+"' ";

			session.createSQLQuery(query).executeUpdate();
			tx.commit();

			Notification n=new Notification("All Information Updated Successfully!","",Notification.TYPE_TRAY_NOTIFICATION);
			n.setPosition(Notification.POSITION_TOP_RIGHT);
			showNotification(n);
		}
		catch(Exception ex)
		{
			tx.rollback();
			showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void componentIni(boolean b) 
	{
		txtLeaveTypeId.setEnabled(!b);
		txLeaveTypeName.setEnabled(b);
	}

	private void btnIni(boolean t) 
	{
		cButton.btnEdit.setEnabled(!t);
		cButton.btnSave.setEnabled(t);
		cButton.btnRefresh.setEnabled(t);
		cButton.btnFind.setEnabled(!t);
	}

	private AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);

		setWidth("580px");
		setHeight("200px");

		lblleaveTypeID = new Label();
		lblleaveTypeID.setImmediate(true);
		lblleaveTypeID.setWidth("-1px");
		lblleaveTypeID.setHeight("-1px");
		lblleaveTypeID.setValue("Leave Type Id :");
		mainLayout.addComponent(lblleaveTypeID, "top:30.0px;left:65.0px;");

		txtLeaveTypeId = new TextRead();
		txtLeaveTypeId.setImmediate(true);
		txtLeaveTypeId.setWidth("80px");
		txtLeaveTypeId.setHeight("22px");
		mainLayout.addComponent(txtLeaveTypeId, "top:28.0px;left:180.0px;");

		lblleaveTypeName = new Label();
		lblleaveTypeName.setImmediate(true);
		lblleaveTypeName.setWidth("-1px");
		lblleaveTypeName.setHeight("-1px");
		lblleaveTypeName.setValue("Leave Type Name :");
		mainLayout.addComponent(lblleaveTypeName, "top:55.0px;left:65.0px;");

		txLeaveTypeName = new TextField();
		txLeaveTypeName.setImmediate(true);
		txLeaveTypeName.setWidth("290px");
		txLeaveTypeName.setHeight("-1px");
		mainLayout.addComponent(txLeaveTypeName, "top:53.0px;left:180.0px;");

		mainLayout.addComponent(cButton, "top:100.0px;left:50.0px;");

		return mainLayout;
	}
}
