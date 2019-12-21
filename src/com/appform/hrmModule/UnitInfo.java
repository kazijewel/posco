package com.appform.hrmModule;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.CommonMethod;
import com.common.share.FileUpload;
import com.common.share.ImageUpload;
import com.common.share.ImmediateFileUpload;
import com.common.share.MessageBox;
import com.common.share.TextRead;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.common.share.FocusMoveByEnter;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.FileResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Window.Notification;

import database.hibernate.*;

public class UnitInfo extends Window 
{
	private AbsoluteLayout mainLayout;
	
	private TextField txtUnitName;
	private TextField address;
	private TextField phone;
	private TextField fax;
	private TextField email;
	private TextRead txtUnitId;
	private ComboBox cmbUnitSearch=new ComboBox();
	
	private String comWidth = "300px";
	private SessionBean sessionBean;	

	public FileUpload Image;
		
	
	private String unitId ;

	public TextRead txtImageBox;
	public Label lblImage;
	String imageLoc = "0" ;
	String employeeImages = "0";
	
	boolean isUpdate=false;
	private CommonMethod cm;
	private String menuId = "";
	CommonButton cButton = new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "", "", "Exit");
	
	public UnitInfo(SessionBean sessionBean,String menuId)
	{
		this.sessionBean = sessionBean;
		this.setCaption("UNIT INFORMATION :: "+sessionBean.getCompany());
		this.setWidth("630px");
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		employeeImages = "D:/Tomcat 7.0/webapps/report/"+sessionBean.getContextName()+"/employee/unit/";
		
		buildMainLayout();
		setContent(mainLayout);
		//dataInitialise();
		buttonAction();
		compInit(true);
		btnint(true);
		cmbUnitSearch.setEnabled(false);
		cmbUnitSearchDataLoad();
		
		Component ob[] = {cmbUnitSearch,txtUnitId,txtUnitName, address, phone, fax, email,cButton.btnNew};		
		new FocusMoveByEnter(this,ob);
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
	private AbsoluteLayout buildMainLayout() 
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("99%");
		mainLayout.setHeight("330px");
		mainLayout.setMargin(false);
		
		cmbUnitSearch = new ComboBox();
		cmbUnitSearch.setImmediate(true);
		cmbUnitSearch.setWidth("250px");
		cmbUnitSearch.setHeight("-1px"); 
		mainLayout.addComponent(new Label("Select and seach unit: "),"top:20.0px;left:30.0px");
		mainLayout.addComponent(cmbUnitSearch,"top:18.0px;left:160.0px");
		
		txtImageBox = new TextRead();
		txtImageBox.setImmediate(true);
		txtImageBox.setWidth("130px");
		txtImageBox.setHeight("150px");
		txtImageBox.setStyleName("txtborder");
		lblImage = new Label("<b><Font Color='#100676' size='6px' font-family= 'Arial, Helvetica, Tahoma, Verdana, sans-serif'>?</Font></b> ",Label.CONTENT_XHTML);
		lblImage.setImmediate(true);
		lblImage.setHeight("100px");
		Image = new FileUpload("Picture");
		Image.upload.setButtonCaption("Unit Logo");
		mainLayout.addComponent(txtImageBox, "top:10.0px;left:430.0px;");
		mainLayout.addComponent(lblImage, "top:70.0px;left:495.0px;");
		mainLayout.addComponent(Image, "top:10.0px;left:450.0px;");
		
		
		txtUnitId = new TextRead();
		txtUnitId.setImmediate(true);
		txtUnitId.setWidth("100px");
		txtUnitId.setHeight("-1px");
		mainLayout.addComponent(new Label("Unit Id :"),"top:50.0px;left:30.0px");
		mainLayout.addComponent(txtUnitId,"top:49.0px;left:160.0px");
		
		txtUnitName = new TextField();
		txtUnitName.setImmediate(true);
		txtUnitName.setWidth("250px");
		txtUnitName.setHeight("-1px");
		mainLayout.addComponent(new Label("Unit Name :"),"top:80.0px;left:30.0px");
		mainLayout.addComponent(txtUnitName,"top:78.0px;left:160.0px");

		address=new TextField();
		address.setWidth("250px");
		address.setRows(2);
		mainLayout.addComponent(new Label("Address: "),"top:110.0px;left:30.0px");
		mainLayout.addComponent(address,"top:108.0px;left:160.0px");
		
		phone = new TextField();
		phone.setImmediate(true);
		phone.setWidth("250px");
		phone.setHeight("-1px");
		mainLayout.addComponent(new Label("Phone No: "),"top:180.0px;left:30.0px");
		mainLayout.addComponent(phone,"top:178.0px;left:160.0px");
		
		fax = new TextField();
		fax.setImmediate(true);
		fax.setWidth("250px");
		fax.setHeight("-1px");
		mainLayout.addComponent(new Label("Fax: "),"top:210.0px;left:30.0px");
		mainLayout.addComponent(fax,"top:208.0px;left:160.0px");
		
		email = new TextField();
		email.setImmediate(true);
		email.setWidth("250px");
		email.setHeight("-1px");
		mainLayout.addComponent(new Label("E-Mail: "),"top:240.0px;left:30.0px");
		mainLayout.addComponent(email,"top:238.0px;left:160.0px");
		
		mainLayout.addComponent(cButton, "top:280.0px; left:70.0px;");

		return mainLayout;
	}
	private void compInit(boolean t)
	{
		cmbUnitSearch.setEnabled(!t);
		txtUnitId.setEnabled(!t);
		txtUnitName.setEnabled(!t);
		fax.setEnabled(!t);
		email.setEnabled(!t);
		address.setEnabled(!t);
		phone.setEnabled(!t);
		
	}
	private void btnint(boolean t)
	{
		cButton.btnNew.setEnabled(t);
		cButton.btnEdit.setEnabled(t);
		cButton.btnSave.setEnabled(!t);	
		cButton.btnRefresh.setEnabled(!t);
		cButton.btnDelete.setEnabled(t);
		cButton.btnFind.setEnabled(t);
	}
	private void txtClear()
	{
		cmbUnitSearch.setValue(null);
		txtUnitName.setValue("");
		fax.setValue("");
		email.setValue("");
		address.setValue("");
		phone.setValue("");
		Image.image.removeAllComponents();
	}
	private void buttonAction()
	{
		/*button.btnEdit.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				updateBtnAction();
			}
		});*/
		cButton.btnNew.addListener(new ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				txtClear();
				autoId();
				compInit(false);
				btnint(false);
			}
		});
		cButton.btnEdit.addListener(new ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				isUpdate=true;
				compInit(false);
				btnint(false);
			}
		});

		cButton.btnSave.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				saveBtnAction();
			}
		});

		cButton.btnRefresh.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{            	
				compInit(true);
				btnint(true);
				txtClear();
				txtUnitId.setValue("");
			}
		});

		cButton.btnExit.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});
		cButton.btnFind.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				cmbUnitSearch.setEnabled(true);
			}
		});
		cmbUnitSearch.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(cmbUnitSearch.getValue()!=null)
				{
					findInit();
				}
			}
		});
	}
	private void autoId()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		try{
			String sql="select (ISNULL(MAX(cast(vUnitId as int)),0)+1) unitId from tbUnitInfo";
	
		Iterator<?> iter=session.createSQLQuery(sql).list().iterator();
		if(iter.hasNext())
		{
			txtUnitId.setValue(""+iter.next());
		}
		}catch(Exception exp)
		{
			showNotification("Auto Id :"+exp);
		}
	}
	private String selectUnitId()
	{

		String returnId = "";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try 
		{
			String query = "select (ISNULL(MAX(vUnitId),0)+1) unitId from tbUnitInfo";
			Iterator<?> iter = session.createSQLQuery(query).list().iterator();
			if(iter.hasNext())
			{
				String srt = iter.next().toString();
				returnId = srt;
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		finally{session.close();}
		return returnId;
	
	}

	private void dataInitialise()
	{
		
	}

	private void saveBtnAction()
	{
		String caption="Do you want to save?";
		if(isUpdate)
		{
			caption="Do you want to update?";
		}
		MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, caption, new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
		mb.show(new EventListener()
		{
			public void buttonClicked(ButtonType buttonType)
			{
				if(buttonType == ButtonType.YES)
				{
					Session session=SessionFactoryUtil.getInstance().getCurrentSession();
					Transaction tx=session.beginTransaction();
					if(isUpdate)
					{
						updateData(session, tx);
						compInit(true);
						btnint(true);
						txtClear();
						cmbUnitSearchDataLoad();
						showNotification("All information update successfully.");
					}
					else
					{
						if(insertData(session,tx))
						{	
							compInit(true);
							btnint(true);
							txtClear();
							cmbUnitSearchDataLoad();
							showNotification("All information save successfully.");
							
						}
					}				
				}
			}
		});
	}
	private boolean insertData(Session session,Transaction tx)
	{
		try{
			//String imageUnit = imagePath(1,selectUnitId())==null? imageLoc:imagePath(1,selectUnitId());
			String getUnitId = "";

			if(!isUpdate)
			{
				getUnitId = selectUnitId();
			}
			else
			{
				getUnitId = cmbUnitSearch.getValue()!=null?cmbUnitSearch.getValue().toString():"";
			}

			String imagePathUnitLogo = imagePath(1,getUnitId)==null? imageLoc:imagePath(1,getUnitId);
			
			String sql="insert into tbUnitInfo(vUnitId,vUnitName,dseMemNo,cseMemberNo,cdblParticularId,phoneNo,fax, "+
					" email,address,userId,userIp,entryTime,imageLoc,validTime)  "+
					" values('"+txtUnitId.getValue().toString()+"','"+txtUnitName.getValue()+"','','','','"+phone.getValue().toString()+"','"+fax.getValue().toString()+"','"+email.getValue().toString()+"'," +
					" '"+address.getValue().toString()+"','"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',getDate(),'"+imagePathUnitLogo+"','') ";
			
			session.createSQLQuery(sql).executeUpdate();
			System.out.println("Insert query :"+sql);
			tx.commit();
			return true;
		}catch(Exception exp)
		{
			tx.rollback();
			showNotification(""+exp);
		}
		return false;
	}
	private String imagePath(int flag,String str)
	{
		String basePath = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/";
		String employeeImage = null;

		if(flag==1)
		{
			// image move
			if(Image.fileName.trim().length()>0)
				try 
			{
					if(Image.fileName.toString().endsWith(".jpg"))
					{
						String path = str;
						fileMove(basePath+Image.fileName.trim(),employeeImages+path+".jpg");
						employeeImage = employeeImages+path+".jpg";
					}
			}
			catch(IOException e) 
			{
				e.printStackTrace();
			}
			return employeeImage;
		}
		return null;
	}
	private void updateData(Session session,Transaction tx)
	{
		if(deleteData(session,tx))
		{
			insertData(session, tx);
		}
	}
	private boolean deleteData(Session session,Transaction tx)
	{
		try{
			String sql="delete  from tbUnitInfo where vUnitId='"+txtUnitId.getValue().toString()+"'";
			session.createSQLQuery(sql).executeUpdate();
			return true;
		}catch(Exception exp)
		{
			showNotification("delete data :"+exp,Notification.TYPE_ERROR_MESSAGE);
		}
		return false;
	}
	private void findInit()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		try{
			String sql="select vUnitId,vUnitName,dseMemNo,cseMemberNo,cdblParticularId,phoneNo,fax,email, "+
					" address,userId,userIp,entryTime,imageLoc,validTime from tbUnitInfo "+
					" where vUnitId='"+cmbUnitSearch.getValue()+"' ";
			List<?> list=session.createSQLQuery(sql).list();
			if(!list.isEmpty())
			{
				Iterator<?> iter=list.iterator();
				while(iter.hasNext())
				{
					Object[] element=(Object[])iter.next();
					txtUnitId.setValue(element[0].toString());
					txtUnitName.setValue(element[1].toString());
					phone.setValue(element[5].toString());
					fax.setValue(element[6].toString());
					email.setValue(element[7].toString());
					address.setValue(element[8].toString());
					
					employeeImage(element[12].toString());
					imageLoc = element[12].toString();
					
				}
			}
			else
				showNotification("No data found!",Notification.TYPE_WARNING_MESSAGE);
		}catch(Exception exp)
		{
			showNotification(""+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private void cmbUnitSearchDataLoad()
	{
		cmbUnitSearch.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		try{
			String sql="select vUnitId,vUnitName from tbUnitInfo order by vUnitName";
			Iterator<?> iter=session.createSQLQuery(sql).list().iterator();
			while(iter.hasNext())
			{
				Object element[]=(Object[])iter.next();
				cmbUnitSearch.addItem(element[0].toString());
				cmbUnitSearch.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Unit search data :"+exp);
		}
	}
	public void employeeImage(String img)
	{
		
		File  fileStu_I = new File(img);

		Embedded eStu_I = new Embedded("",new FileResource(fileStu_I, getApplication()));
		eStu_I.requestRepaint();
		eStu_I.setWidth("110px");
		eStu_I.setHeight("140px");

		Image.image.removeAllComponents();
		Image.image.addComponent(eStu_I);
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
			cButton.btnEdit.setEnabled(false);
			setEditable(true);
			cButton.btnSave.setEnabled(true);
			cButton.btnRefresh.setEnabled(true);
			txtUnitName.focus();
		}
		else
		{
			showNotification("Authentication Failed","You have not proper authentication for Edit.",Notification.TYPE_ERROR_MESSAGE);
		}
	}



	private void initialise()
	{
		setEditable(false);
		cButton.btnEdit.setEnabled(true);
		cButton.btnSave.setEnabled(true);
		cButton.btnRefresh.setEnabled(false);
	}

	private void setEditable(boolean tf)
	{
		txtUnitName.setEnabled(tf);
		address.setEnabled(tf);

		phone.setEnabled(tf);
		fax.setEnabled(tf);
		email.setEnabled(tf);
	}
}
