package com.common.share;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import acc.menuform.menu.RootMenu;

import com.common.share.CommonButton;
import com.common.share.CommonMethod;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Button.ClickShortcut;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.themes.BaseTheme;

@SuppressWarnings("serial")
public class CompanyInformation extends Window 
{
	private SessionBean sessionBean;
	private CommonButton cButton = new CommonButton("", "Save", "Edit", "", "Refresh", "", "", "","","Exit");

	//private ListSelect ntvFind;

	public boolean isUpdate = false;
	public boolean isInsert = false;

	private AbsoluteLayout mainLayout;

	private Label lblCommon;
	private TextField txtCompanyName;

	private TextField txtAddress;
	private TextField txtMobile;
	private TextField txtWebSite;
	private TextField txtPhone;
	private TextField txtFax;
	private TextField txtEmail;

	private ImmediateFileUpload logoUpload;
	private Button btnlogoPreview;
	private String logoImageLoc = "0";
	
	public ImmediateFileUpload btnOrganogram;
	public Button btnOrganogramPreview;
	public String organogramLoc = "0";
	
	public String logoJpg = "";
	public String logoImagePath = "";
	public String filePathTmp = "";
	
	public String organoGramJPG = "";
	public String organogramImagePath = "";
	public String organogramFilePathTmp = "";

	private TextField txtRegistrationNo;
	private PopupDateField dRegistrationDate;
	private TextField txtTinNo;
	private PopupDateField dTinDate;
	private TextField txtVatNo;
	private PopupDateField dVatDate;
	private TextField txtTradeLicenseNo;
	private PopupDateField dTradeLicenseDate;
	//private NativeSelect cmbIsActive;

	private ArrayList<Component> allComp = new ArrayList<Component>();
	private static final String[] BranchStatus = new String[]{"Inactive", "Active"};

	private CommonMethod cm;
	private String menuId = "";

	public CompanyInformation(SessionBean sessionBean, String menuId)
	{
		this.sessionBean = sessionBean;
		this.setResizable(false);
		this.setCaption("COMPANY INFORMATION :: "+sessionBean.getCompany());
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;

		buildMainLayout();
		setContent(mainLayout);
		txtInit(true);
		btnIni(true);
		focusEnter();
		eventAction();
		setButtonShortCut();
		authenticationCheck();
		cButton.btnNew.focus();
		findInitialise();
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

	private void setButtonShortCut()
	{
		this.addAction(new ClickShortcut(cButton.btnSave, KeyCode.S, ModifierKey.ALT));
		this.addAction(new ClickShortcut(cButton.btnNew, KeyCode.N, ModifierKey.ALT));
		this.addAction(new ClickShortcut(cButton.btnRefresh, KeyCode.R, ModifierKey.ALT));
	}

	public void eventAction()
	{
		cButton.btnSave.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				formValidation();
			}
		});

		cButton.btnEdit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{

				if(sessionBean.isSuperAdmin())
				{
					updateButtonEvent();
				}
				else
				{
					showNotification("You are not authorized!");
				}
			
			}
		});

		cButton.btnRefresh.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate = false;
				refreshButtonEvent();
			}
		});

		cButton.btnExit.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				close();
			}
		});

		

		btnlogoPreview.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(isUpdate)
				{
					if(!logoUpload.actionCheck)
					{
						if(!logoImageLoc.equalsIgnoreCase("0"))
						{
							String link = getApplication().getURL().toString().replaceAll(sessionBean.getContextName()+"/", logoImageLoc.substring(22, logoImageLoc.length()));
							getWindow().open(new ExternalResource(link),"_blank",500,200,Window.BORDER_NONE);
						}
						else
						{
							showNotification("Warning!","No file found.",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					if(logoUpload.actionCheck)
					{
						String link = getApplication().getURL().toString().replaceAll(sessionBean.getContextName(), "report")+filePathTmp;
						getWindow().open(new ExternalResource(link),"_blank",500,200,Window.BORDER_NONE);
					}
				}
				else
				{
					if(!logoJpg.isEmpty())
					{
						String link = getApplication().getURL().toString().replaceAll(sessionBean.getContextName(), "report")+filePathTmp;
						System.out.println("link is:"+link);
						getWindow().open(new ExternalResource(link),"_blank",500,200,Window.BORDER_NONE);
					}
					else
					{
						showNotification("Warning!","No file found.",Notification.TYPE_WARNING_MESSAGE);
					}
				}
			}
		});
		btnOrganogramPreview.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(isUpdate)
				{
					if(!btnOrganogram.actionCheck)
					{
						if(!organogramLoc.equalsIgnoreCase("0"))
						{
							String link = getApplication().getURL().toString().replaceAll(sessionBean.getContextName()+"/", organogramLoc.substring(22, organogramLoc.length()));
							getWindow().open(new ExternalResource(link),"_blank",500,200,Window.BORDER_NONE);
						}
						else
						{
							showNotification("Warning!","No file found.",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					if(btnOrganogram.actionCheck)
					{
						String link = getApplication().getURL().toString().replaceAll(sessionBean.getContextName(), "report")+organogramFilePathTmp;
						getWindow().open(new ExternalResource(link),"_blank",500,200,Window.BORDER_NONE);
					}
				}
				else
				{
					if(!organoGramJPG.isEmpty())
					{
						String link = getApplication().getURL().toString().replaceAll(sessionBean.getContextName(), "report")+organogramFilePathTmp;
						System.out.println("link is:"+link);
						getWindow().open(new ExternalResource(link),"_blank",500,200,Window.BORDER_NONE);
					}
					else
					{
						showNotification("Warning!","No file found.",Notification.TYPE_WARNING_MESSAGE);
					}
				}
			}
		});

		btnOrganogram.upload.addListener(new Upload.SucceededListener() 
		{
			public void uploadSucceeded(SucceededEvent event) 
			{
				organogramImagePath(0);
			}
		});
	}

	private String logoImagePath(int flag)
	{
		String basePath = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/";
		
		System.out.println("base path is:"+basePath);
		
		String stuImage = "";
		if(flag == 0)
		{
			if(logoUpload.fileName.trim().length()>0)
			{
				try
				{
					if(logoUpload.fileName.toString().endsWith(".jpg"))
					{
						String path = sessionBean.getUserId()+"Logo";
						fileMove(basePath+logoUpload.fileName.trim(),SessionBean.imagePath+path+".jpg");
						logoJpg = SessionBean.imagePath+path+".jpg";
						filePathTmp = path+".jpg";
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			return logoJpg;
		}
		if(flag == 1)
		{
			if(logoUpload.fileName.trim().length()>0)
			{
				try
				{	
					if(logoUpload.fileName.toString().endsWith(".jpg"))
					{
						String projectName = sessionBean.getContextName();
						fileMove(basePath+logoUpload.fileName.trim(),SessionBean.imagePath+projectName+"/Logo.jpg");
						stuImage = SessionBean.imagePath+projectName+"/Logo.jpg";
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			return stuImage;
		}
		return null;
	}
	private String organogramImagePath(int flag)
	{
		String basePath = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/";
		
		System.out.println("base path is:"+basePath);
		
		String stuImage = "";
		if(flag == 0)
		{
			if(btnOrganogram.fileName.trim().length()>0)
			{
				try
				{
					if(btnOrganogram.fileName.toString().endsWith(".jpg"))
					{
						String path = sessionBean.getUserId()+"-OrganoGram";
						fileMove(basePath+btnOrganogram.fileName.trim(),SessionBean.imagePath+path+".jpg");
						organoGramJPG = SessionBean.imagePath+path+".jpg";
						organogramFilePathTmp = path+".jpg";
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			return organoGramJPG;
		}
		if(flag == 1)
		{
			if(btnOrganogram.fileName.trim().length()>0)
			{
				try
				{	
					if(btnOrganogram.fileName.toString().endsWith(".jpg"))
					{
						String projectName = sessionBean.getContextName();
						fileMove(basePath+btnOrganogram.fileName.trim(),SessionBean.imagePath+projectName+"/Organogram.jpg");
						stuImage = SessionBean.imagePath+projectName+"/Organogram.jpg";
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			return stuImage;
		}
		return null;
	}

	private void fileMove(String fStr,String tStr) throws IOException
	{
		try
		{
			File f1 = new File(tStr);
			if(f1.isFile())
			{
				f1.delete();
			}
		}
		catch(Exception exp){}
		FileInputStream ff = new FileInputStream(fStr);

		File  ft = new File(tStr);
		FileOutputStream fos = new FileOutputStream(ft);

		while(ff.available()!=0)
		{
			fos.write(ff.read());
		}
		fos.close();
		ff.close();
	}

	

	private void refreshButtonEvent()
	{
		txtInit(true);
		btnIni(true);
		txtClear();
		isUpdate = false;
		isInsert = false;
		cButton.btnNew.focus();
	}

	private void updateButtonEvent()
	{
		if(!txtCompanyName.getValue().toString().isEmpty())
		{
			isUpdate = true;
			btnIni(false);
			txtInit(false);
		}
		else
		{
			showNotification("Warning!","Find data to update.",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void formValidation()
	{
		if(!txtCompanyName.getValue().toString().isEmpty())
		{
			if(!txtAddress.getValue().toString().isEmpty())
			{
				if(!txtPhone.getValue().toString().isEmpty())
				{
					if(!txtRegistrationNo.getValue().toString().isEmpty())
					{
						if(!txtTinNo.getValue().toString().isEmpty())
						{
							if(!txtVatNo.getValue().toString().isEmpty())
							{
								if(!txtTradeLicenseNo.getValue().toString().isEmpty())
								{
									if(!txtMobile.getValue().toString().isEmpty())
									{
										if(!txtWebSite.getValue().toString().isEmpty())
										{
											saveButtonEvent();
										}
										else
										{
											showNotification("Warning!","Provide WebSite .", Notification.TYPE_WARNING_MESSAGE);
											txtWebSite.focus();
										}
									}
									else
									{
										showNotification("Warning!","Provide Mobile no.", Notification.TYPE_WARNING_MESSAGE);
										txtMobile.focus();
									}
								}
								else
								{
									showNotification("Warning!","Provide trade license no.", Notification.TYPE_WARNING_MESSAGE);
									txtTradeLicenseNo.focus();
								}
							}
							else
							{
								showNotification("Warning!","Provide VAT no.", Notification.TYPE_WARNING_MESSAGE);
								txtVatNo.focus();
							}
						}
						else
						{
							showNotification("Warning!","Provide TIN No.", Notification.TYPE_WARNING_MESSAGE);
							txtTinNo.focus();
						}
					}
					else
					{
						showNotification("Warning!","Provide registration no.", Notification.TYPE_WARNING_MESSAGE);
						txtRegistrationNo.focus();
					}
				}
				else
				{
					showNotification("Warning!","Provide company phone no.", Notification.TYPE_WARNING_MESSAGE);
					txtPhone.focus();
				}
			}
			else
			{
				showNotification("Warning!","Provide company address", Notification.TYPE_WARNING_MESSAGE);
				txtAddress.focus();
			}
		}
		else
		{
			showNotification("Warning!","Provide company name.", Notification.TYPE_WARNING_MESSAGE);
			txtCompanyName.focus();
		}
	}

	private void findInitialise()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try 
		{
			String sql = "select vCompanyName,vCompanyAddess,vCompanyPhone,isnull(vCompanyMobile,'')vCompanyMobile,vCompanyFax,vCompanyEmail,isnull(vWebSite,'')vWebSite,vCompanyLogo,"+
					" vRegistrationNo,dRegRenewDate,vTinNo,dTinRenewDate,vVatNo,dVatRenewDate,vTradeLicenseNo,dLicenseRenewDate,"+
					" iActive,vOrganogram from dbo.tbCompanyInformation where vCompanyId like  '%'";
			List<?> led = session.createSQLQuery(sql).list();
			if (led.iterator().hasNext()) 
			{
				Object[] element = (Object[]) led.iterator().next();
				txtCompanyName.setValue(element[0].toString());
				txtAddress.setValue(element[1].toString());
				txtPhone.setValue(element[2].toString());
				txtMobile.setValue(element[3].toString());
				txtFax.setValue(element[4].toString());
				txtEmail.setValue(element[5].toString());
				txtWebSite.setValue(element[6].toString());
				
				logoImageLoc = element[7].toString();

				txtRegistrationNo.setValue(element[8].toString());
				dRegistrationDate.setValue(element[9]);
				txtTinNo.setValue(element[10].toString());		
				dTinDate.setValue(element[11]);
				txtVatNo.setValue(element[12].toString());
				dVatDate.setValue(element[13]);
				txtTradeLicenseNo.setValue(element[14].toString());
				dTradeLicenseDate.setValue(element[15]);
				organogramLoc=element[17].toString();
			}
		}
		catch(Exception exp)
		{
			showNotification("Error to find", exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void saveButtonEvent()
	{
		if(isUpdate)
		{
			MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to update information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						updateData();
						findInitialise();
						showNotification("All information updated successfully.");
					}
				}
			});
		}
	}

	public void updateData() 
	{
		if(!isInsert)
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			Transaction tx = session.beginTransaction();
			try
			{
				logoImagePath = logoImagePath(1)==null?logoImageLoc:logoImagePath(1);
				organogramLoc = organogramImagePath(1)==null?organogramLoc:organogramImagePath(1);
				String updateQuery = "UPDATE dbo.tbCompanyInformation set "+
						" vCompanyName = '"+txtCompanyName.getValue().toString().trim()+"',"+
						" vCompanyAddess = '"+txtAddress.getValue().toString().trim()+"',"+
						" vCompanyPhone = '"+txtPhone.getValue().toString().trim()+"',"+
						" vCompanyMobile = '"+txtMobile.getValue().toString().trim()+"',"+
						" vCompanyFax = '"+txtFax.getValue().toString().trim()+"',"+
						" vCompanyEmail = '"+txtEmail.getValue().toString().trim()+"',"+
						" vWebSite = '"+txtWebSite.getValue().toString().trim()+"',"+
						" vCompanyLogo = '"+(logoImageLoc.isEmpty()?"0":logoImageLoc)+"',"+
						" vRegistrationNo = '"+txtRegistrationNo.getValue().toString().trim()+"',"+
						" dRegRenewDate = '"+cm.dfDb.format(dRegistrationDate.getValue())+"',"+
						" vTinNo = '"+txtTinNo.getValue().toString().trim()+"',"+
						" dTinRenewDate = '"+cm.dfDb.format(dTinDate.getValue())+"',"+
						" vVatNo = '"+txtVatNo.getValue().toString()+"',"+
						" dVatRenewDate = '"+cm.dfDb.format(dVatDate.getValue())+"',"+
						" vTradeLicenseNo = '"+txtTradeLicenseNo.getValue().toString()+"',"+
						" dLicenseRenewDate = '"+cm.dfDb.format(dTradeLicenseDate.getValue())+"',"+
						" iActive = '',"+
						" vUserName = '"+sessionBean.getUserName()+"',"+
						" vUserIp = '"+sessionBean.getUserIp()+"',"+
						" dEntryTime = CURRENT_TIMESTAMP ,"+
				        " vOrganogram = '"+(organogramLoc.isEmpty()?"0":organogramLoc)+"' " ;
				
						
				//System.out.println(updateQuery);
				session.createSQLQuery(updateQuery).executeUpdate();
				isInsert = true;
				tx.commit();
			}
			catch(Exception exp)
			{
				tx.rollback();
				showNotification("Error to update",exp+"",Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}
	}

	private void focusEnter()
	{
		allComp.add(txtCompanyName);
		allComp.add(txtAddress);
		allComp.add(txtPhone);
		allComp.add(txtFax);
		allComp.add(txtMobile);
		allComp.add(txtEmail);
		allComp.add(txtWebSite);
		allComp.add(logoUpload);
		allComp.add(txtRegistrationNo);
		allComp.add(dRegistrationDate);
		allComp.add(txtTinNo);
		allComp.add(dTinDate);
		allComp.add(txtVatNo);
		allComp.add(dVatDate);
		allComp.add(txtTradeLicenseNo);
		allComp.add(dTradeLicenseDate);
		allComp.add(cButton.btnSave);
		new FocusMoveByEnter(this,allComp);
	}

	private void txtClear()
	{
		txtCompanyName.setValue("");
		txtAddress.setValue("");
		txtPhone.setValue("");
		txtFax.setValue("");
		txtEmail.setValue("");
		txtMobile.setValue("");
		txtWebSite.setValue("");

		filePathTmp = "";
		logoJpg = "";
		logoUpload.fileName = "";
		//logoUpload.status.setValue(new Label("<font size=1px>(Select .jpg file)</font>",Label.CONTENT_XHTML));
		logoUpload.actionCheck = false;
		logoImageLoc = "0";

		txtRegistrationNo.setValue("");
		dRegistrationDate.setValue(new Date());
		txtTinNo.setValue("");		
		dTinDate.setValue(new Date());
		txtVatNo.setValue("");
		dVatDate.setValue(new Date());
		txtTradeLicenseNo.setValue("");
		dTradeLicenseDate.setValue(new Date());
		
	}

	public void txtInit(boolean t)
	{
		txtCompanyName.setEnabled(!t);
		txtAddress.setEnabled(!t);
		txtPhone.setEnabled(!t);
		txtFax.setEnabled(!t);
		txtMobile.setEnabled(!t);
		txtEmail.setEnabled(!t);
		txtWebSite.setEnabled(!t);
		logoUpload.setEnabled(!t);
		btnlogoPreview.setEnabled(!t);
		txtRegistrationNo.setEnabled(!t);
		dRegistrationDate.setEnabled(!t);
		txtTinNo.setEnabled(!t);
		dTinDate.setEnabled(!t);
		txtVatNo.setEnabled(!t);
		dVatDate.setEnabled(!t);
		txtTradeLicenseNo.setEnabled(!t);
		dTradeLicenseDate.setEnabled(!t);
		btnOrganogram.setEnabled(!t);
		btnOrganogramPreview.setEnabled(!t);
	}

	private void btnIni(boolean t)
	{
		cButton.btnNew.setEnabled(t);
		cButton.btnEdit.setEnabled(t);
		cButton.btnSave.setEnabled(!t);
		cButton.btnRefresh.setEnabled(!t);
		cButton.btnDelete.setEnabled(t);
		cButton.btnFind.setEnabled(t);
	}

	private AbsoluteLayout buildMainLayout() 
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setWidth("620px");
		mainLayout.setHeight("490px");

		lblCommon = new Label("Company Name :");
		mainLayout.addComponent(lblCommon, "top:20.0px; left:80.0px;");

		txtCompanyName = new TextField();
		txtCompanyName.setImmediate(true);
		txtCompanyName.setWidth("325px");
		txtCompanyName.setHeight("-1px");
		mainLayout.addComponent(txtCompanyName, "top:18.0px; left:200.0px;");

		lblCommon = new Label("Address :");
		mainLayout.addComponent(lblCommon, "top:45.0px; left:80.0px;");

		txtAddress = new TextField();
		txtAddress.setImmediate(true);
		txtAddress.setWidth("325px");
		txtAddress.setHeight("48px");
		mainLayout.addComponent(txtAddress, "top:43.0px; left:200.0px;");

		lblCommon = new Label("Phone :");
		mainLayout.addComponent(lblCommon, "top:95.0px; left:80.0px;");

		txtPhone = new TextField();
		//txtPhone.setMaxLength(11);
		txtPhone.setImmediate(true);
		txtPhone.setWidth("325px");
		txtPhone.setHeight("-1px");
		mainLayout.addComponent(txtPhone, "top:93.0px; left:200.0px;");

		lblCommon = new Label("Fax :");
		mainLayout.addComponent(lblCommon, "top:120.0px; left:80.0px;");

		txtFax = new TextField();
		txtFax.setImmediate(true);
		txtFax.setWidth("325px");
		txtFax.setHeight("-1px");
		mainLayout.addComponent(txtFax, "top:118.0px; left:200.0px;");

		lblCommon = new Label("Mobile :");
		mainLayout.addComponent(lblCommon, "top:145.0px; left:80.0px;");

		txtMobile = new TextField();
		//txtPhone.setMaxLength(11);
		txtMobile.setImmediate(true);
		txtMobile.setWidth("325px");
		txtMobile.setHeight("-1px");
		mainLayout.addComponent(txtMobile, "top:143.0px; left:200.0px;");

		lblCommon = new Label("Email :");
		mainLayout.addComponent(lblCommon, "top:170.0px; left:80.0px;");

		txtEmail = new TextField();
		txtEmail.setImmediate(true);
		txtEmail.setWidth("325px");
		txtEmail.setHeight("-1px");
		mainLayout.addComponent(txtEmail,"top:168.0px; left:200.0px");


		lblCommon = new Label("Website :");
		mainLayout.addComponent(lblCommon, "top:195.0px; left:80.0px;");

		txtWebSite = new TextField();
		txtWebSite.setImmediate(true);
		txtWebSite.setWidth("325px");
		txtWebSite.setHeight("-1px");
		mainLayout.addComponent(txtWebSite,"top:193.0px; left:200.0px");

		lblCommon = new Label("Company Logo :");
		mainLayout.addComponent(lblCommon, "top:240px; left:80.0px;");

		logoUpload = new ImmediateFileUpload("","LOGO");
		logoUpload.setImmediate(true);
		mainLayout.addComponent(logoUpload, "top:233.0px; left:200.0px;");

		btnlogoPreview = new Button("Logo Preview");
		btnlogoPreview.setStyleName(BaseTheme.BUTTON_LINK);
		btnlogoPreview.addStyleName("icon-after-caption");
		btnlogoPreview.setImmediate(true);
		btnlogoPreview.setIcon(new ThemeResource("../icons/document-pdf.png"));
		mainLayout.addComponent(btnlogoPreview, "top:240px; left:250px;");
		
		mainLayout.addComponent(new Label("Organogram :"),"top:240px; left:370px");
		btnOrganogram = new ImmediateFileUpload("","ORG");
		btnOrganogram.setImmediate(true);
		mainLayout.addComponent(btnOrganogram, "top:233.0px; left:450px;");

		btnOrganogramPreview = new Button("Preview");
		btnOrganogramPreview.setStyleName(BaseTheme.BUTTON_LINK);
		btnOrganogramPreview.addStyleName("icon-after-caption");
		btnOrganogramPreview.setImmediate(true);
		btnOrganogramPreview.setIcon(new ThemeResource("../icons/document-pdf.png"));
		mainLayout.addComponent(btnOrganogramPreview, "top:240px; left:500px;");
		
		

		lblCommon = new Label("Registration No :");
		mainLayout.addComponent(lblCommon, "top:275.0px; left:80.0px;");

		txtRegistrationNo = new TextField();
		txtRegistrationNo.setImmediate(true);
		txtRegistrationNo.setWidth("120px");
		txtRegistrationNo.setHeight("-1px");
		mainLayout.addComponent(txtRegistrationNo, "top:273.0px; left:200.0px;");

		lblCommon = new Label("Renew Date :");
		mainLayout.addComponent(lblCommon, "top:275.0px; left:3300.0px;");

		dRegistrationDate = new PopupDateField();
		dRegistrationDate.setImmediate(true);
		dRegistrationDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dRegistrationDate.setDateFormat("dd-MM-yyyy");
		dRegistrationDate.setValue(new Date());
		dRegistrationDate.setWidth("110px");
		dRegistrationDate.setHeight("-1px");
		mainLayout.addComponent(dRegistrationDate,"top:273.0px; left:410.0px;");

		lblCommon = new Label("TIN No :");
		mainLayout.addComponent(lblCommon, "top:300.0px; left:80.0px;");

		txtTinNo = new TextField();
		txtTinNo.setImmediate(true);
		txtTinNo.setWidth("120px");
		txtTinNo.setHeight("-1px");
		mainLayout.addComponent(txtTinNo, "top:298.0px; left:200.0px;");

		lblCommon = new Label("Renew Date :");
		mainLayout.addComponent(lblCommon, "top:300.0px; left:330.0px;");

		dTinDate = new PopupDateField();
		dTinDate.setImmediate(true);
		dTinDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dTinDate.setDateFormat("dd-MM-yyyy");
		dTinDate.setValue(new Date());
		dTinDate.setWidth("110px");
		dTinDate.setHeight("-1px");
		mainLayout.addComponent(dTinDate,"top:298.0px; left:410.0px;");

		lblCommon = new Label("VAT Reg. No :");
		mainLayout.addComponent(lblCommon, "top:325.0px; left:80.0px;");

		txtVatNo = new TextField();
		txtVatNo.setImmediate(true);
		txtVatNo.setWidth("120px");
		txtVatNo.setHeight("-1px");
		mainLayout.addComponent(txtVatNo, "top:323.0px; left:200.0px;");

		lblCommon = new Label("Renew Date :");
		mainLayout.addComponent(lblCommon, "top:325.0px; left:330.0px;");

		dVatDate = new PopupDateField();
		dVatDate.setImmediate(true);
		dVatDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dVatDate.setDateFormat("dd-MM-yyyy");
		dVatDate.setValue(new Date());
		dVatDate.setWidth("110px");
		dVatDate.setHeight("-1px");
		mainLayout.addComponent(dVatDate,"top:323.0px; left:410.0px;");

		lblCommon = new Label("Trade License No :");
		mainLayout.addComponent(lblCommon, "top:350.0px; left:80.0px;");

		txtTradeLicenseNo = new TextField();
		txtTradeLicenseNo.setImmediate(true);
		txtTradeLicenseNo.setWidth("120px");
		txtTradeLicenseNo.setHeight("-1px");
		mainLayout.addComponent(txtTradeLicenseNo, "top:348.0px; left:200.0px;");

		lblCommon = new Label("Renew Date :");
		mainLayout.addComponent(lblCommon, "top:350.0px; left:330.0px;");

		dTradeLicenseDate = new PopupDateField();
		dTradeLicenseDate.setImmediate(true);
		dTradeLicenseDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dTradeLicenseDate.setDateFormat("dd-MM-yyyy");
		dTradeLicenseDate.setValue(new Date());
		dTradeLicenseDate.setWidth("110px");
		dTradeLicenseDate.setHeight("-1px");
		mainLayout.addComponent(dTradeLicenseDate,"top:348.0px; left:410.0px;");

		
		mainLayout.addComponent(cButton,"top:420px; left:130.0px;");

		return mainLayout;
	}
}