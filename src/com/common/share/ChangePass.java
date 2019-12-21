package com.common.share;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class ChangePass extends Window 
{
	private CommonButton button = new CommonButton("", "", "Edit", "", "", "", "", "", "", "Exit");
	private SessionBean sessionBean;
	private VerticalLayout mainLayout = new VerticalLayout();
	private FormLayout formLayout = new FormLayout();
	private HorizontalLayout btnLayout = new HorizontalLayout();

	private TextField txtUserName = new TextField("User Name:");

	private PasswordField presentPass = new PasswordField("Present Password:");
	private PasswordField password = new PasswordField("New Password:");
	private PasswordField confirmPassword = new PasswordField("Confirm New Password:");
	private String cw = "230px";

	public ChangePass(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("CHANGE PASSWORD :: "+sessionBean.getCompany());
		this.setWidth("450px");
		this.setResizable(false);

		formLayout.addComponent(txtUserName);
		txtUserName.setWidth(cw);

		presentPass.setWidth(cw);
		formLayout.addComponent(presentPass);

		password.setWidth(cw);
		formLayout.addComponent(password);
		confirmPassword.setWidth(cw);
		formLayout.addComponent(confirmPassword);

		btnLayout.addComponent(button);		

		btnLayout.setSpacing(true);
		formLayout.addComponent(btnLayout);

		formLayout.setMargin(true);
		mainLayout.addComponent(formLayout);

		this.addComponent(mainLayout);
		buttonActionAdd();
		userNameInitialise();

		/*txtUserName.setValue(this.sessionBean.getUserName());
		txtUserName.setReadOnly(true);*/
		
	}

	
	private void userNameInitialise()
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try
		{
			String sql = "select vUserId,vUserName,vPassword from tbUserInfo where vUserId = '"+sessionBean.getUserId()+"'"
					+ " and iActive='1' ";
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				txtUserName.setValue(element[1].toString());
				txtUserName.setDebugId(element[2].toString());
				txtUserName.setReadOnly(true);
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	private void buttonActionAdd()
	{
		button.btnEdit.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				if(!presentPass.getValue().toString().isEmpty())
				{
					if(!password.getValue().toString().isEmpty())
					{
						if(!confirmPassword.getValue().toString().isEmpty())
						{
							updateBtnAction();
						}
						else
						{
							showNotification("Warning!","Confirm new password.",Notification.TYPE_WARNING_MESSAGE);
							confirmPassword.focus();
						}
					}
					else
					{
						showNotification("Warning!","Provide new password.",Notification.TYPE_WARNING_MESSAGE);
						password.focus();
					}
				}
				else
				{
					showNotification("Warning!","Provide present password.",Notification.TYPE_WARNING_MESSAGE);
					presentPass.focus();
				}
			}
		});

		button.btnExit.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});
	}

	private void updateBtnAction()
	{
		MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to change password?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
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

	private void updateData()
	{
		if(!presentPass.getValue().toString().equals(txtUserName.getDebugId()))
		{
			showNotification("Warning!","Provide valid present password.",Notification.TYPE_WARNING_MESSAGE);
		}
		else if(!password.getValue().toString().equals(confirmPassword.getValue().toString()))
		{
			showNotification("Warning!","Mismatch new password & confirm new password.",Notification.TYPE_WARNING_MESSAGE);
		}
		else
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			try
			{
				String sql = "UPDATE dbo.tbUserInfo SET vPassword = '"+password.getValue()+"' WHERE vUserId ="
						+ " '"+sessionBean.getUserId()+"'";
				session.createSQLQuery(sql).executeUpdate();
				tx.commit();
				showNotification("Password update successfully.");
			}
			catch(Exception exp)
			{
				tx.rollback();
				showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			}
		}
	}
}