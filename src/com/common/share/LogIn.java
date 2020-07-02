     package com.common.share;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import acc.menuform.menu.RootMenu;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.FileResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.terminal.gwt.server.WebBrowser;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.BaseTheme;

public class LogIn extends Window 
{
	private SessionBean sessionBean;

	private AbsoluteLayout mainLayout = new AbsoluteLayout();

	private Label lblCommon;
	private ComboBox cmbCompanyName;
	private TextField txtUserName;
	private PasswordField txtPassword;
	private NativeButton btnLogin,btnLeaveApplication,btnReplacementLeave,btnOverTime;
	private String warId;
	private String typeOfPublic;
	boolean switchUser=false;
	public LogIn(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("LOG IN :: "+sessionBean.getCompany());
		this.setWidth("340px");
		this.setHeight("480px");
		this.setResizable(false);
		this.center();
		this.setClosable(false);
		this.setDraggable(false);

		buildMainLayout();
		setContent(mainLayout);

		companyLoad();

		buttonActionAdd();

		setStyleName("cwindow");
		cmbCompanyName.focus();
		cmbCompanyName.setValue(sessionBean.getCompanyId());
		cmbCompanyName.setEnabled(false);
		copyReportFolder("D:\\Tomcat 7.0\\webapps\\report\\posco","D:\\ScheduleBackup\\posco\\");
	}

	//----------------------------------------------copyReportFolder Start----------------------------------------------
	
	
	/**
     * using this Method you can Copy File from One Location to Other Location
     * <pre class='code'>
     * @param src
     *            "D:\\Tomcat 7.0\\webapps\\report\\posco"
     * @param dst
     * 			  "D:\\ScheduleBackup\\posco\\"
     * 
     */
	public void copyReportFolder(String src,String dst)
	{
		SimpleDateFormat dFormatDDMMYY = new SimpleDateFormat("dd-MM-yyyy");
		String saveDate=dFormatDDMMYY.format(new Date()).toString().trim();
		
		File srcFolder = new File(src);
		File destFolder = new File(dst+saveDate);
		
		//make sure source exists
		if(!srcFolder.exists())
		{
		   System.out.println("Directory does not exist.");
		   //just exit
		   System.exit(0);
		}
		else
		{
			if(!destFolder.exists())
			{
				destFolder.mkdir();
				try
				{
					copyFolder(srcFolder,destFolder);
				}
				catch(IOException e)
				{
					e.printStackTrace();
					System.exit(0);
				}
			}
			else
			{
				System.out.println("No More Today");
			}
		}    	
		System.out.println("Done");
	}
	public static void copyFolder(File src, File dest) 	throws IOException
	{	    	
		if(src.isDirectory())
		{    		
			//if directory not exists, create it
			if(!dest.exists())
			{
			   dest.mkdir();
			   System.out.println("Directory copied from "+ src + "  to " + dest);
			}
			
			//list all the directory contents
			String files[] = src.list();
			
			for (String file : files) 
			{
			   //construct the src and dest file structure
			   File srcFile = new File(src, file);
			   File destFile = new File(dest, file);
			   //recursive copy
			   copyFolder(srcFile,destFile);
			}
		   
		}
		else
		{
			//if file, then copy it
			//Use bytes stream to support all file types
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dest); 
						 
			byte[] buffer = new byte[1024];	    
			int length;
			//copy the file content in bytes 
			while ((length = in.read(buffer)) > 0)
			{
			   out.write(buffer, 0, length);
			} 
			in.close();
			out.close();
			System.out.println("File copied from " + src + " to " + dest);
		}
	}	
	//----------------------------------------------copyReportFolder End----------------------------------------------

	private void buttonActionAdd()
	{
		btnLogin.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbCompanyName.getValue()!=null)
				{
					if(!txtUserName.getValue().toString().isEmpty())
					{
						if(!txtPassword.getValue().toString().isEmpty())
						{
							poolStart();
							loginBtnAction();
						}
						else
						{
							showNotification("Warning!","Provide Password",Notification.TYPE_WARNING_MESSAGE);
							txtPassword.focus();
						}
					}
					else
					{
						showNotification("Warning!","Provide UserName",Notification.TYPE_WARNING_MESSAGE);
						txtUserName.focus();
					}
				}
				else
				{
					showNotification("Warning!","Select Company Name",Notification.TYPE_WARNING_MESSAGE);
					cmbCompanyName.focus();
				}
			}
		});
		/*btnLoanApplication.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				btnLoanAction();
			}
		});*/
		btnLeaveApplication.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				btnLeaveAction();
			}
		});
		btnOverTime.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				btnOverTimeAction();
			}
		});
		btnReplacementLeave.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				btnReplacementLeaveAction();
			}
		});

		cmbCompanyName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbCompanyName.getValue()!=null)
				{
					sessionBean.setWar(warId);
					txtUserName.focus();
					//System.out.println("warId: "+warId);
				}
			}
		});

		txtUserName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtUserName.getValue().toString().isEmpty())
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
	}
	/*private void btnLoanAction()
	{
		sessionBean.setUserId("");
		sessionBean.setUserName("Public");
		Session session=SessionFactoryUtil.getInstance().openSession();
		String sql = "SELECT vCompanyName, vCompanyAddess, vCompanyPhone, vCompanyFax, vCompanyEmail, vCompanyLogo FROM tbCompanyInformation where vCompanyId = '"+ cmbCompanyName.getValue().toString() +"'";
		Iterator iter = session.createSQLQuery(sql).list().iterator();
		if(iter.hasNext())
		{
			Object[] elmnt = (Object[]) iter.next();
			sessionBean.setCompanyId(cmbCompanyName.getValue().toString());
			sessionBean.setCompany(elmnt[0].toString());
			sessionBean.setCompanyAddress(elmnt[1].toString());
			sessionBean.setCompanyContact("Phone: " + elmnt[2].toString()+", Fax: "+elmnt[3].toString()+", Email: "+elmnt[4].toString());
			sessionBean.setCompanyLogo(elmnt[5].toString());
		}
		userIpSet();
		RootMenu rm = new RootMenu(sessionBean,"Loan");
		File  fileLogo = new File(sessionBean.getCompanyLogo());
		rm.btnImage.setIcon(new FileResource(fileLogo,getApplication()));
		this.getParent().addComponent(rm);	    		
		this.close();
	}*/
	private void btnLeaveAction()
	{
		sessionBean.setUserId("");
		sessionBean.setUserName("Public");
		Session session=SessionFactoryUtil.getInstance().openSession();
		String sql = "SELECT vCompanyName, vCompanyAddess, vCompanyPhone, vCompanyFax, vCompanyEmail, vCompanyLogo FROM tbCompanyInformation where vCompanyId = '"+ cmbCompanyName.getValue().toString() +"'";
		Iterator iter = session.createSQLQuery(sql).list().iterator();
		if(iter.hasNext())
		{
			Object[] elmnt = (Object[]) iter.next();
			sessionBean.setCompanyId(cmbCompanyName.getValue().toString());
			sessionBean.setCompany(elmnt[0].toString());
			sessionBean.setCompanyAddress(elmnt[1].toString());
			sessionBean.setCompanyContact("Phone: " + elmnt[2].toString()+", Fax: "+elmnt[3].toString()+", Email: "+elmnt[4].toString());
			sessionBean.setCompanyLogo(elmnt[5].toString());
		}
		sessionBean.setDeleteable(true);
		sessionBean.setSubmitable(true);
		sessionBean.setUpdateable(true);
		
		switchUser=true;
		RootMenu rm = new RootMenu(sessionBean,"Leave");
		File  fileLogo = new File(sessionBean.getCompanyLogo());
		rm.btnImage.setIcon(new FileResource(fileLogo,getApplication()));
		this.getParent().addComponent(rm);	    		
		this.close();
	}
	
	private void btnReplacementLeaveAction()
	{
		sessionBean.setUserId("");
		sessionBean.setUserName("Public");
		sessionBean.setUserIp(userIpSet());
		Session session=SessionFactoryUtil.getInstance().openSession();
		String sql = "SELECT vCompanyName, vCompanyAddess, vCompanyPhone, vCompanyFax, vCompanyEmail, vCompanyLogo FROM tbCompanyInformation where vCompanyId = '"+ cmbCompanyName.getValue().toString() +"'";
		Iterator iter = session.createSQLQuery(sql).list().iterator();
		if(iter.hasNext())
		{
			Object[] elmnt = (Object[]) iter.next();
			sessionBean.setCompanyId(cmbCompanyName.getValue().toString());
			sessionBean.setCompany(elmnt[0].toString());
			sessionBean.setCompanyAddress(elmnt[1].toString());
			sessionBean.setCompanyContact("Phone: " + elmnt[2].toString()+", Fax: "+elmnt[3].toString()+", Email: "+elmnt[4].toString());
			sessionBean.setCompanyLogo(elmnt[5].toString());
		}
		sessionBean.setDeleteable(true);
		sessionBean.setSubmitable(true);
		sessionBean.setUpdateable(true);
		
		switchUser=true;
		RootMenu rm = new RootMenu(sessionBean,"ReplacementLeave");
		File  fileLogo = new File(sessionBean.getCompanyLogo());
		rm.btnImage.setIcon(new FileResource(fileLogo,getApplication()));
		this.getParent().addComponent(rm);	    		
		this.close();
	}
	
	private void btnOverTimeAction()
	{
		sessionBean.setUserId("");
		sessionBean.setUserName("Public");
		sessionBean.setUserIp(userIpSet());
		Session session=SessionFactoryUtil.getInstance().openSession();
		String sql = "SELECT vCompanyName, vCompanyAddess, vCompanyPhone, vCompanyFax, vCompanyEmail, vCompanyLogo FROM tbCompanyInformation where vCompanyId = '"+ cmbCompanyName.getValue().toString() +"'";
		Iterator iter = session.createSQLQuery(sql).list().iterator();
		if(iter.hasNext())
		{
			Object[] elmnt = (Object[]) iter.next();
			sessionBean.setCompanyId(cmbCompanyName.getValue().toString());
			sessionBean.setCompany(elmnt[0].toString());
			sessionBean.setCompanyAddress(elmnt[1].toString());
			sessionBean.setCompanyContact("Phone: " + elmnt[2].toString()+", Fax: "+elmnt[3].toString()+", Email: "+elmnt[4].toString());
			sessionBean.setCompanyLogo(elmnt[5].toString());
		}
		sessionBean.setDeleteable(true);
		sessionBean.setSubmitable(true);
		sessionBean.setUpdateable(true);
		
		switchUser=true;
		RootMenu rm = new RootMenu(sessionBean,"OverTime");
		File  fileLogo = new File(sessionBean.getCompanyLogo());
		rm.btnImage.setIcon(new FileResource(fileLogo,getApplication()));
		this.getParent().addComponent(rm);	    		
		this.close();
	}
	private boolean isExpired()
	{
		System.out.println("DateValid: "+new Date().getTime());
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			String sql="SELECT COUNT(*) FROM tbCompanyInformation "
					+ "WHERE cast(validTime as bigint)> "+(new Date().getTime()+" and vCompanyId = '"+cmbCompanyName.getValue().toString())+"' ";
			
			String s = session.createSQLQuery(sql).list().listIterator().next().toString();
			
			//System.out.println("isExpired: "+sql);
			
			if(s.trim().equals("1"))
			{
				return true;
			}
			else
			{
				session.createSQLQuery("UPDATE tbCompanyInformation SET validTime = 'a' where vCompanyId = '"+warId+"' ").executeUpdate();
				tx.commit();
				this.getParent().showNotification("Error","Error converting data type varchar to bigint.",Notification.TYPE_ERROR_MESSAGE);
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error2","Error converting data type varchar to bigint.",Notification.TYPE_ERROR_MESSAGE);
		}
		return false;
	}
	private void loginBtnAction()
	{
		if(true/*isExpired()*/) // For validation
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			Transaction tx = session.beginTransaction();
			try
			{
				String sql = "select co.vCompanyId,co.vCompanyName,co.vCompanyAddess,co.vCompanyPhone,co.vCompanyFax,co.vCompanyEmail, co.vCompanyLogo," +
						"co.vRegistrationNo,co.iActive,ui.vUserId,ui.vUserName,ui.vPassword,ui.iUserType,ui.iEditDeleteDays,ui.iActive ui,co.vCompanyMobile,co.vWebSite,ui.vEmployeeId from " +
						"dbo.tbCompanyInformation co inner join dbo.tbUserInfo ui on ui.vCompanyId = co.vCompanyId " +
						"where ui.vUserName = '"+txtUserName.getValue().toString()+"' and ui.vPassword = '"+txtPassword.getValue().toString()+"' ";
				System.out.println(sql);
				Iterator<?> iter = session.createSQLQuery(sql).list().iterator();
				if(iter.hasNext())
				{
					Object[] element = (Object[]) iter.next();
					if(element[10].toString().equals(txtUserName.getValue().toString().trim()) &&
							element[11].toString().equals(txtPassword.getValue().toString().trim()))
					{
								if(Integer.parseInt(element[14].toString())==1)
								{
									//Initialize primary data of company
									sessionBean.setCompanyId(element[0].toString());
									sessionBean.setCompany(element[1].toString());
									sessionBean.setCompanyAddress(element[2].toString());
									sessionBean.setCompanyContact(element[3].toString()+", "+element[4].toString()+", "+element[5].toString());
									sessionBean.setCompanyLogo(element[6].toString());
									sessionBean.setCompanyRegNo(element[7].toString());

									//Initialize primary data of branch
									//sessionBean.setBranchId(element[9].toString());
									//sessionBean.setBranchCode(element[10].toString());
									//sessionBean.setBranchName(element[11].toString());
									//sessionBean.setBranchAddress(element[12].toString());
									//sessionBean.setBranchContact(element[13].toString()+", "+element[14].toString()+", "+element[15].toString());
									//sessionBean.setPrincipalBranch(element[16].toString().equals("1")?true:false);

									//Initialize primary data of user
									sessionBean.setUserId(element[9].toString());
									sessionBean.setUserName(element[10].toString());
									sessionBean.setUserIp(userIpSet());

									sessionBean.setAdmin(element[12].toString().equals("1")?true:false);
									sessionBean.setSuperAdmin(element[12].toString().equals("2")?true:false);

									sessionBean.setUserEditDeleteDays(Integer.parseInt(element[13].toString()));
									
									sessionBean.setCompanyMobileNo(element[15].toString());
									sessionBean.setCompanyWebsite(element[16].toString());
									sessionBean.setEmployeeId(element[17].toString());
									
									DesignatonSet();
									
									try
									{
										String login = "update dbo.tbUserInfo set dLastLogin = CURRENT_TIMESTAMP where vUserId = '"+sessionBean.getUserId()+"'";
										session.createSQLQuery(login).executeUpdate();
										tx.commit();
									}
									catch(Exception e)
									{
										tx.rollback();
									}

									RootMenu rm = new RootMenu(sessionBean,"Private");
									if(!sessionBean.getCompanyLogo().toString().equals("0"))
									{
										File fileLogo = new File(sessionBean.getCompanyLogo());
										rm.btnImage.setIcon(new FileResource(fileLogo,getApplication()));
									}
									this.getParent().addComponent(rm);	    		
									this.close();
								}
								else
								{
									showNotification("Login Failed!","This account is inactive, please contact to admin.",Notification.TYPE_WARNING_MESSAGE);
									//cmbBranchName.focus();
								}
					}
					else
					{
						showNotification("Login Failed!","Provide valid username & password.",Notification.TYPE_WARNING_MESSAGE);
						///mbBranchName.focus();
					}
				}
				else
				{
					showNotification("Login Failed!","Provide valid username & password.",Notification.TYPE_WARNING_MESSAGE);
					//cmbBranchName.focus();
				}
			}
			catch(Exception exp)
			{
				showNotification("Error to login ",exp+"",Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}
	}
	private void DesignatonSet()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		try{
			
			String sql="select vDesignationId from tbEmpOfficialPersonalInfo where vEmployeeId='"+sessionBean.getEmployeeId()+"'";
			Iterator<?> iter=session.createSQLQuery(sql).list().iterator();
			if(iter.hasNext())
			{
				sessionBean.setDesignationId(iter.next().toString());
			}
			
		}catch(Exception exp)
		{
			showNotification("DesignatonSet "+exp);
		}
		finally{
			session.close();
		}
	}
	
	public void poolStart()
	{
		try
		{
			class PoolStart extends Thread
			{
				public void run()
				{
					Session session = SessionFactoryUtil.getInstance().getCurrentSession();
					Transaction tx = session.beginTransaction();
					Iterator<?> iter = session.createSQLQuery("select dOpeningDate,dClosingDate,iFiscalYearId from tbFiscalYear where iRunningFlag = 1 and vCompanyID = '1'").list().iterator();

					if(iter.hasNext())
					{
						Object[] element = (Object[]) iter.next();
						System.out.println(element[0].toString());
						sessionBean.setFiscalOpenDate(element[0]);
						sessionBean.setFiscalCloseDate(element[1]);
						sessionBean.setFiscalRunningSerial(element[2].toString());
					}
					tx.commit();
				}
			};
			new PoolStart().start(); 
		}
		catch(Exception ex)
		{

		}
	}
	/*private void userIpSet()
	{
		WebApplicationContext context = ((WebApplicationContext) getApplication().getContext());
		WebBrowser webBrowser = context.getBrowser();
		sessionBean.setUserIp(webBrowser.getAddress());
	}*/
	private String userIpSet()
	{
		String userIp = "";
		WebApplicationContext context = ((WebApplicationContext) getApplication().getContext());
		WebBrowser webBrowser = context.getBrowser();
		userIp = (webBrowser.getAddress());
		return userIp;
	}

	private void companyLoad()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{
			List list = session.createSQLQuery("select vCompanyId,vCompanyName from tbCompanyInformation order by vCompanyName").list();
			for(Iterator iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbCompanyName.addItem(element[0].toString());
				cmbCompanyName.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch (Exception e)
		{
			showNotification("Unable to get company data",""+e,Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}

	private AbsoluteLayout buildMainLayout() 
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(false);

		setWidth("330px");
		setHeight("480px");

		Embedded eStu_I = new Embedded("",new ThemeResource("../icons/0.png"));
		eStu_I.requestRepaint();
		eStu_I.setWidth("126px");
		eStu_I.setHeight("100px");
		mainLayout.addComponent(eStu_I, "top:20.0px;left:95.0px;");

		lblCommon = new Label("<b>Company Name :</b>",Label.CONTENT_XHTML);
		lblCommon.setImmediate(true);
		lblCommon.setHeight("-1px");
		lblCommon.setWidth("-1px");
		mainLayout.addComponent(lblCommon, "top:135.0px;left:15.0px;");

		cmbCompanyName = new ComboBox();
		cmbCompanyName.setImmediate(true);
		cmbCompanyName.setWidth("200px");
		cmbCompanyName.setNullSelectionAllowed(false);
		mainLayout.addComponent(cmbCompanyName, "top:133.0px;left:110.0px;");

		lblCommon = new Label("<b>User Name :</b>",Label.CONTENT_XHTML);
		lblCommon.setImmediate(true);
		lblCommon.setHeight("-1px");
		lblCommon.setWidth("-1px");
		mainLayout.addComponent(lblCommon, "top:165.0px;left:15.0px;");

		txtUserName = new TextField();
		txtUserName.setHeight("-1px");
		txtUserName.setWidth("150px");
		txtUserName.setImmediate(true);
		txtUserName.setInputPrompt("User Name");
		mainLayout.addComponent(txtUserName, "top:163.0px;left:110.0px;");

		lblCommon = new Label("<b>Password :</b>",Label.CONTENT_XHTML);
		lblCommon.setImmediate(true);
		lblCommon.setHeight("-1px");
		lblCommon.setWidth("-1px");
		mainLayout.addComponent(lblCommon, "top:195.0px;left:15.0px;");

		txtPassword = new PasswordField();
		txtPassword.setHeight("-1px");
		txtPassword.setWidth("150px");
		txtPassword.setImmediate(true);
		txtPassword.setInputPrompt("Password");
		mainLayout.addComponent(txtPassword, "top:193.0px;left:110.0px;");

		btnLogin = new NativeButton();
		btnLogin.setStyleName("btnLogin");
		btnLogin.setWidth("100px");
		btnLogin.setHeight("35px");
		btnLogin.setIcon(new ThemeResource("../icons/Logi.png"));
		btnLogin.setStyleName(BaseTheme.BUTTON_LINK);
		mainLayout.addComponent(btnLogin, "top:225.0px;left:110.0px;");

		//mainLayout.addComponent(btnLogin, "top:140.0px;left:265.0px;"); old

		btnLeaveApplication = new NativeButton();
		btnLeaveApplication.setStyleName("btnLogin");
		btnLeaveApplication.setIcon(new ThemeResource("../icons/LeaveApplication.png"));
		btnLeaveApplication.setStyleName(BaseTheme.BUTTON_LINK);
		mainLayout.addComponent(btnLeaveApplication, "top:288px;left:95.0px;");

		btnReplacementLeave = new NativeButton();
		btnReplacementLeave.setStyleName("btnLogin");
		btnReplacementLeave.setIcon(new ThemeResource("../icons/ReplaceLeave.png"));
		btnReplacementLeave.setStyleName(BaseTheme.BUTTON_LINK);
		mainLayout.addComponent(btnReplacementLeave, "top:333px;left:95.0px;");
		
		btnOverTime = new NativeButton();
		btnOverTime.setStyleName("btnLogin");
		btnOverTime.setIcon(new ThemeResource("../icons/OtRequest.png"));
		btnOverTime.setStyleName(BaseTheme.BUTTON_LINK);
		mainLayout.addComponent(btnOverTime, "top:378px;left:95.0px;");


		return mainLayout;
	}
}
