package hrm.common.reportform;

import hrm.common.reportform.SendNotice;

import com.common.share.CommonMethod;
import com.common.share.SessionBean;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.terminal.gwt.server.WebBrowser;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class EmailLogInNotice extends Window 
{
	private SessionBean sessionBean;
	private CommonMethod cm;
	private AbsoluteLayout mainLayout = new AbsoluteLayout();

	private Label lblCommon;
	private String menuId = "";
	private TextField txtEmailId;
	private PasswordField txtPassword;
	private NativeButton btnLogin,btnExit;
	
	public EmailLogInNotice(SessionBean sessionBean,String menuId)
	{
		this.sessionBean = sessionBean;
		cm = new CommonMethod(sessionBean);
		this.setCaption("EMAIL LOG IN NOTICE :: "+sessionBean.getCompany());
		this.setWidth("400px");
		this.setHeight("270px");
		this.setResizable(false);
		this.center();
		this.setClosable(false);
		this.setDraggable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		buildMainLayout();
		setContent(mainLayout);


		txtEmailId.focus();
		buttonActionAdd();

		//setStyleName("cwindow");
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
			{btnLogin.setVisible(false);}
			
		}
		}
	}
	private void buttonActionAdd()
	{
		btnLogin.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
					if(!txtEmailId.getValue().toString().isEmpty() && isValid(txtEmailId.getValue().toString().trim()))
					{
						if(!txtPassword.getValue().toString().isEmpty() && txtPassword.getValue().toString().length()>8)
						{
							if(isGmail(txtEmailId.getValue().toString()))
							{
								loginBtnAction();
							}
							else
							{
								showNotification("","Provide Only Gmail!",Notification.TYPE_WARNING_MESSAGE);
								txtEmailId.focus();
							}
						}
						else
						{
							showNotification("","Provide Valid Password At Least 8 Chars.",Notification.TYPE_WARNING_MESSAGE);
							txtPassword.focus();
						}
					}
					else
					{
						showNotification("","Provide Valid Email Address",Notification.TYPE_WARNING_MESSAGE);
						txtEmailId.focus();
					}
			}
		});
	txtEmailId.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtEmailId.getValue().toString().isEmpty())
				{
					txtPassword.focus();
				}
			}
		});

		txtPassword.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtPassword.getValue().toString().isEmpty())
				{
					btnLogin.focus();
				}
			}
		});
		btnExit.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
					close();
			}
		});
	}
	public boolean isValid(String email) {
		String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
	   }
	public boolean isGmail(String email)
	{
		CharSequence cs="gmail";
		if(email.contains(cs))
		{
			return true;
		}
		return false;
	}
	public void loginBtnAction()
	{
		String Email=txtEmailId.getValue().toString();
		String password=txtPassword.getValue().toString();
		if(Email.length()>0 && password.length()>0)
		{
			Window win = new SendNotice(sessionBean,Email,password);
			win.setStyleName("cwindow");
			win.setModal(true);
			this.getParent().addWindow(win);
		}
		
	}
	private String userIpSet()
	{
		String userIp = "";
		WebApplicationContext context = ((WebApplicationContext) getApplication().getContext());
		WebBrowser webBrowser = context.getBrowser();
		userIp = (webBrowser.getAddress());
		return userIp;
	}

	private AbsoluteLayout buildMainLayout() 
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(false);

		setWidth("400px");
		setHeight("250px");

		/*Embedded eStu_I = new Embedded("",new ThemeResource("../icons/0.png"));
		eStu_I.requestRepaint();
		eStu_I.setWidth("100px");
		eStu_I.setHeight("100px");
		mainLayout.addComponent(eStu_I, "top:50.0px;left:50.0px;");*/

		lblCommon = new Label("<b>Email ID :</b>",Label.CONTENT_XHTML);
		lblCommon.setImmediate(true);
		lblCommon.setHeight("-1px");
		lblCommon.setWidth("-1px");
		mainLayout.addComponent(lblCommon, "top:45.0px;left:30.0px;");

		txtEmailId = new TextField();
		txtEmailId.setHeight("-1px");
		txtEmailId.setWidth("250px");
		txtEmailId.setImmediate(true);
		txtEmailId.setInputPrompt("Email ID");
		mainLayout.addComponent(txtEmailId, "top:43.0px;left:100.0px;");

		lblCommon = new Label("<b>Password :</b>",Label.CONTENT_XHTML);
		lblCommon.setImmediate(true);
		lblCommon.setHeight("-1px");
		lblCommon.setWidth("-1px");
		mainLayout.addComponent(lblCommon, "top:80.0px;left:30.0px;");

		txtPassword = new PasswordField();
		txtPassword.setHeight("-1px");
		txtPassword.setWidth("250px");
		txtPassword.setImmediate(true);
		txtPassword.setInputPrompt("Password");
		mainLayout.addComponent(txtPassword, "top:78.0px;left:100.0px;");

		btnLogin = new NativeButton("Login");
		btnLogin.setStyleName("lgin");
		btnLogin.setWidth("80px");
		btnLogin.setHeight("30px");
		btnLogin.setImmediate(true);
		mainLayout.addComponent(btnLogin, "top:140.0px;left:100px;");
		
		btnExit = new NativeButton("Exit");
		btnExit.setStyleName("exit");
		btnExit.setWidth("80px");
		btnExit.setHeight("30px");
		btnExit.setImmediate(true);
		mainLayout.addComponent(btnExit, "top:140.0px;left:210px;");
		return mainLayout;
	}
}