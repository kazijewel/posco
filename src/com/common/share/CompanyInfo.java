package com.common.share;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import org.hibernate.Session;
import org.hibernate.Transaction;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.common.share.FocusMoveByEnter;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Window.Notification;

import database.hibernate.*;

public class CompanyInfo extends Window 
{
	CommonButton button = new CommonButton("", "Save", "Edit", "", "Refresh", "", "", "", "", "Exit");
	private FormLayout formLayout = new FormLayout();
	private VerticalLayout mainLayout = new VerticalLayout();
	private HorizontalLayout btnLayout = new HorizontalLayout();
	private HorizontalLayout space = new HorizontalLayout();	
	private TextField companyName = new TextField("Company Name:");
	private TextField address = new TextField("Address:");
	private TextField phone = new TextField("Phone No:");
	private TextField fax = new TextField("Fax:");
	private TextField email = new TextField("E-Mail:");

	private String comWidth = "300px";
	private SessionBean sessionBean;
	private String comId ;
	String imageLoc = "0" ;

	private ImageUpload imageUp = new ImageUpload("");

	public CompanyInfo(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("COMPANY INFORMATION :: "+sessionBean.getCompany());
		this.setWidth("520px");
		this.setResizable(false);

		companyName.setWidth(comWidth);
		address.setWidth("230px");
		address.setRows(2);
		phone.setWidth("230px");;
		fax.setWidth("230px");
		email.setWidth("230px");

		formLayout.addComponent(companyName);
		formLayout.addComponent(address);
		formLayout.addComponent(phone);
		formLayout.addComponent(fax);
		formLayout.addComponent(email);

		imageUp.upload.setButtonCaption("Company Logo");
		imageUp.upload.setWidth("110px");
		formLayout.addComponent(imageUp);

		space.setWidth("40px");
		btnLayout.addComponent(space);
		btnLayout.addComponent(button);
		btnLayout.setSpacing(true);
		formLayout.setMargin(true);
		mainLayout.setMargin(true);
		mainLayout.addComponent(formLayout);
		mainLayout.addComponent(btnLayout);
		addComponent(mainLayout);
		buttonAction();
		initialise();
		Component ob[] = {companyName, address, phone, fax, email, button.btnSave, button.btnEdit};		
		new FocusMoveByEnter(this,ob);		
		dataInitialise();
		button.btnEdit.focus();
		System.out.println("Datetime : "+new Date());
	}

	private void buttonAction()
	{
		button.btnEdit.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				updateBtnAction();
			}
		});


		button.btnSave.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				saveBtnAction();
			}
		});

		button.btnRefresh.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{            	
				clearBtnAction();
			}
		});

		button.btnExit.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

		/*imageUp.upload.addListener(new Upload.SucceededListener() 
		{
			public void uploadSucceeded(SucceededEvent event) 
			{
				imagePath(1,"0");
				System.out.println("Done");
			}
		});*/
	}

	private void dataInitialise()
	{
		try
		{
			Transaction tx = null;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			Iterator iter = session.createSQLQuery("SELECT * FROM TbCompanyInfo where companyId = '"+ sessionBean.getCompanyId() +"'").list().iterator();
			if(iter.hasNext())
			{
				Object[] element = (Object[]) iter.next();
				comId = sessionBean.getCompanyId();
				companyName.setValue(element[1].toString());
				address.setValue(element[8].toString());
				phone.setValue(element[5].toString());
				fax.setValue(element[6].toString());
				email.setValue(element[7].toString());
			}
			tx.commit();
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void saveBtnAction()
	{
		MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to update company information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
		mb.show(new EventListener()
		{
			public void buttonClicked(ButtonType buttonType)
			{
				if(buttonType == ButtonType.YES)
				{
					button.btnSave.setEnabled(false);
					updateData();
				}
			}
		});
	}

	private void updateData()
	{
		Transaction tx = null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();
		
		String logo = imagePath(1,"0");//imagePath(1,sessionBean.getCompanyLogo())==null?imageLoc:
		
		System.out.println("imagePath: "+logo+"\nLogo "+sessionBean.getCompanyLogo());
		
		try
		{
			String query=" update tbCompanyInfo set " +
					" companyName = '"+companyName.getValue().toString()+"'," +
					" phoneNo = '"+phone.getValue().toString()+"'," +
					" fax = '"+fax.getValue().toString()+"'," +
					" email = '"+email.getValue().toString()+"'," +
					" address = '"+address.getValue().toString()+"'," +
					" userId = '"+sessionBean.getUserId()+"'," +
					" userIp = '"+sessionBean.getUserIp()+"'," +
					" entryTime = current_timestamp" +
					//" ,imageLoc = '"+logo+"' " +
					" where companyId = '"+comId+"' ";
			System.out.println(query);

			session.createSQLQuery(query).executeUpdate();
			tx.commit();
			initialise();
			this.getParent().showNotification("All information updated successfully.");
		}
		catch(Exception ex)
		{
			this.getParent().showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}

	private String imagePath(int flag,String str)
	{
		String basePath = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/";
		String stuImage = null;

		if(flag==1)
		{
			if(imageUp.fileName.trim().length()>0)
				try 
			{
					if(imageUp.fileName.toString().endsWith(".jpg"))
					{
						String path = str;
						fileMove(basePath+imageUp.fileName.trim(),SessionBean.imageLogo+path+".jpg");
						stuImage = SessionBean.imageLogo+path+".jpg";
					}
			}
			catch (IOException e)
			{
				e.printStackTrace();
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
				f1.delete();
		}
		catch(Exception exp){}
		FileInputStream ff= new FileInputStream(fStr);

		File  ft = new File(tStr);
		FileOutputStream fos = new FileOutputStream(ft);

		while(ff.available()!=0)
		{
			fos.write(ff.read());
		}
		fos.close();
		ff.close();
	}

	private void updateBtnAction()
	{
		if(sessionBean.isUpdateable())
		{
			button.btnEdit.setEnabled(false);
			setEditable(true);
			button.btnSave.setEnabled(true);
			button.btnRefresh.setEnabled(true);
			companyName.focus();
		}
		else
		{
			showNotification("Authentication Failed","You have not proper authentication for Edit.",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void clearBtnAction()
	{
		initialise();
	}

	private void initialise()
	{
		setEditable(false);
		button.btnEdit.setEnabled(true);
		button.btnSave.setEnabled(false);
		button.btnRefresh.setEnabled(false);
	}

	private void setEditable(boolean tf)
	{
		companyName.setEnabled(tf);
		address.setEnabled(tf);

		phone.setEnabled(tf);
		fax.setEnabled(tf);
		email.setEnabled(tf);
		imageUp.setEnabled(tf);
	}
}
