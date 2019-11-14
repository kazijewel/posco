package com.appform.hrmModule;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.CommonMethod;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Button.ClickShortcut;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

@SuppressWarnings("serial")
public class NoticeInfo extends Window 
{
	CommonButton cButton = new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "","","Exit");

	private TextField txtFindId = new TextField();
	private TextField txtIdBack = new TextField();

	boolean isUpdate=false;
	boolean isNew=false;

	private AbsoluteLayout mainLayout;

	private Label lblCommon;

	private PopupDateField dDate;
	private TextField txtNoticeID,txtSubject;
	private TextArea txtDescription;

	private Label lblIsActive;
	private NativeSelect cmbIsActive;

	SessionBean sessionBean;

	ArrayList<Component> allComp = new ArrayList<Component>();
	private static final String[] status = new String[] { "Inactive", "Active" };

	private boolean isFind = false;

	private DecimalFormat dfAmount = new DecimalFormat("~0.00");
	private SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dfReference = new SimpleDateFormat("yy");
	

	private ImmediateUploadNote bpvUpload = new ImmediateUploadNote("");
	Button btnPreview;
	String imageLoc = "0" ;
	String filePathTmp = "";
	String bpvPdf = null;
	String tempimg="";
	private CommonMethod cm;
	private String menuId = "";
	public NoticeInfo(SessionBean sessionBean,String menuId)
	{
		this.sessionBean = sessionBean;
		this.setResizable(false);
		this.setCaption("NOTICE INFORMATION :: "+sessionBean.getCompany());
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		buildMainLayout();
		setContent(mainLayout);

		txtInit(true);
		btnIni(true);

		//focusEnter();
		btnAction();

		authenticationCheck();
		setButtonShortCut();

		cButton.btnNew.focus();
	}

	private void setButtonShortCut()
	{
		this.addAction(new ClickShortcut(cButton.btnSave, KeyCode.S, ModifierKey.ALT));
		this.addAction(new ClickShortcut(cButton.btnNew, KeyCode.N, ModifierKey.ALT));
		this.addAction(new ClickShortcut(cButton.btnRefresh, KeyCode.R, ModifierKey.ALT));
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

	public void btnAction()
	{
		cButton.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isFind = false;
				isUpdate = false;

				txtInit(false);
				btnIni(false);
				txtClear();

				txtNoticeID.setValue(selectMaxId());
				//txtMealCharge.focus();
			}
		});

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
				isFind = false;
				updateButtonEvent();
			}
		});

		cButton.btnRefresh.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isFind = false;
				isUpdate=false;
				refreshButtonEvent();
			}
		});

		cButton.btnFind.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isFind = true;
				findButtonEvent();
			}
		});

		cButton.btnExit.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				close();
			}
		});

		btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{	
				if(!isUpdate)
				{
					String link = getApplication().getURL().toString();
					
					System.out.println("link is:"+link);
					
					if(link.endsWith(""+sessionBean.getContextName()+"/"))
					{
						link = link.replaceAll(""+sessionBean.getContextName()+"", "report")+filePathTmp;
						link=imagePath(0,"");
						link=tempimg;
						
						link=link+"VAADIN/themes"+tempimg.substring(tempimg.lastIndexOf("/"));
						System.out.println(link);
						
					}
					getWindow().open(new ExternalResource(link),"_blank",500,200,Window.BORDER_NONE);
				}
				if(isUpdate)
				{
					if(!bpvUpload.actionCheck)
					{
						if(!imageLoc.equalsIgnoreCase("0"))
						{
							String link = getApplication().getURL().toString();

							if(link.endsWith(""+sessionBean.getContextName()+"/"))
							{
								link = link.replaceAll(""+sessionBean.getContextName()+"/", imageLoc.substring(22, imageLoc.length()));
							}
							getWindow().open(new ExternalResource(link),"_blank",500,200,Window.BORDER_NONE);
						}
						else
						{
							showNotification("There is no File",Notification.TYPE_HUMANIZED_MESSAGE);
						}
					}
					if(bpvUpload.actionCheck)
					{
						String link = getApplication().getURL().toString();

						if(link.endsWith(""+sessionBean.getContextName()+"/"))
						{
							link = link.replaceAll(""+sessionBean.getContextName()+"", "report")+filePathTmp;
						}
						getWindow().open(new ExternalResource(link),"_blank",500,200,Window.BORDER_NONE);
					}
				}
			}
		});

		bpvUpload.upload.addListener(new Upload.SucceededListener() 
		{
			public void uploadSucceeded(SucceededEvent event) 
			{
				imagePath(0,"");
				System.out.println("Done");
			}
		});
	}

	private void formValidation()
	{
		if(dDate.getValue()!=null)
		{
			if(!txtSubject.getValue().toString().isEmpty())
			{
				if(!txtDescription.getValue().toString().isEmpty())
				{
					saveButtonEvent();
				}
				else
				{
					showNotification("Warning!","Provide Description.", Notification.TYPE_WARNING_MESSAGE);
				}
			}
			else
			{
				showNotification("Warning!","Provide Subject.", Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else
		{
			showNotification("Warning!","Provide Date.", Notification.TYPE_WARNING_MESSAGE);
		}
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

	private String imagePath(int flag,String str)
	{
		String basePath = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/";
		System.out.println("basePath is:"+basePath+bpvUpload.fileName.trim());
		
		String stuImage = null;

		if(flag==0)
		{
			// image move
			if(bpvUpload.fileName.trim().length()>0)
				try {
					if(bpvUpload.fileName.toString().endsWith(".jpg")){
						String path = sessionBean.getUserId()+"CPV";
						fileMove(basePath+bpvUpload.fileName.trim(),SessionBean.imagePath+path+".jpg");
						tempimg=basePath+bpvUpload.fileName.trim();
						bpvPdf = SessionBean.imagePath+path+".jpg";
						filePathTmp = path+".jpg";
					}else{
						String path = sessionBean.getUserId()+"CPV";
						fileMove(basePath+bpvUpload.fileName.trim(),SessionBean.imagePath+path+".pdf");
						bpvPdf = SessionBean.imagePath+path+".pdf";
						filePathTmp = path+".pdf";
					}
				} catch (IOException e)
				{
					e.printStackTrace();
				}

			return bpvPdf;
		}

		if(flag==1)
		{
			// image move
			if(bpvUpload.fileName.trim().length()>0)
			{
				try
				{	
					if(bpvUpload.fileName.toString().endsWith(".jpg"))
					{
						String path = str;
						String projectName = sessionBean.getContextName();
						fileMove(basePath+bpvUpload.fileName.trim(),SessionBean.imagePath+projectName+"/Notice/"+path+".jpg");
						stuImage = SessionBean.imagePath+projectName+"/Notice/"+path+".jpg";
					}
					else
					{
						String path = str;
						String projectName = sessionBean.getContextName();
						fileMove(basePath+bpvUpload.fileName.trim(),SessionBean.imagePath+projectName+"/Notice/"+path+".pdf");
						stuImage = SessionBean.imagePath+projectName+"/Notice/"+path+".pdf";
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

	private String selectMaxId()
	{
		String maxId = "";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select isnull(max(cast(SUBSTRING(vNoticeId,4,LEN(vNoticeId)) as int)),0)+1 from tbNoticeInfo";
		
			Iterator <?> iter = session.createSQLQuery(query).list().iterator();
			if (iter.hasNext())
			{
				String srt = iter.next().toString();
				maxId = dfReference.format(dDate.getValue())+"-"+srt;
			}
		}
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		finally{session.close();}
		return maxId;
	}

	private void findButtonEvent() 
	{
		Window win = new NoticeInfoFind(sessionBean, txtFindId);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if (txtFindId.getValue().toString().length() > 0)
				{
					txtClear();
					txtIdBack.setValue(txtFindId.getValue().toString());
					findInitialise(txtFindId.getValue().toString());
				}
			}
		});
		this.getParent().addWindow(win);
	}

	private void findInitialise(String vNoticeId) 
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "select vNoticeId,dDate,vSubject,vDescription,vAttach,isActive from tbNoticeInfo where vNoticeId='"+vNoticeId+"'";
			System.out.println("findInitialise: "+sql);
			List <?> led = session.createSQLQuery(sql).list();

			if (led.iterator().hasNext()) 
			{
				Object[] element = (Object[]) led.iterator().next();

				txtNoticeID.setValue(element[0].toString());
				dDate.setValue(element[1]);
				txtSubject.setValue(element[2].toString().replaceAll("~", "'"));
				txtDescription.setValue(element[3].toString().replaceAll("~", "'"));
				imageLoc = element[4].toString();
				cmbIsActive.setValue(element[5]);
			}
		}
		catch (Exception exp) 
		{
			showNotification("Error", exp + "",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void refreshButtonEvent() 
	{
		txtInit(true);
		btnIni(true);
		txtClear();
		isNew = false;
	}

	private void updateButtonEvent()
	{
		if(!txtNoticeID.getValue().toString().isEmpty() && dDate.getValue()!=null 
				&& !txtSubject.getValue().toString().isEmpty() && !txtDescription.getValue().toString().isEmpty())
		{
			isUpdate = true;
			isFind = false;
			btnIni(false);
			txtInit(false);
		}
		else
		{
			showNotification("Update Failed","There are no data for update.",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void saveButtonEvent()
	{
		MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, isUpdate?"Do you want to Update?":"Do you want to Save?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
		mb.setStyleName("cwindowMB");
		mb.show(new EventListener()
		{
			public void buttonClicked(ButtonType buttonType)
			{
				if(buttonType == ButtonType.YES)
				{
					Session session = SessionFactoryUtil.getInstance().openSession();
					Transaction tx = session.beginTransaction();
					if(isUpdate)
					{
						insertData(session,tx);	
						btnIni(true);
						txtInit(true);
						txtClear();
					}
					else
					{
						insertData(session,tx);		
						btnIni(true);
						txtInit(true);
						txtClear();
					}
					
					Notification n=new Notification(isUpdate?"All Information Updated Successfully!":"All Information Saved Successfully!","",Notification.TYPE_TRAY_NOTIFICATION);
					n.setPosition(Notification.POSITION_TOP_RIGHT);
					showNotification(n);
					
					isUpdate=false;
					isFind = false;
				}
			}
		});

	}

	private void insertData(Session session,Transaction tx)
	{
		try
		{
			String maxId=selectMaxId();
			if(isUpdate)
			{
				maxId=txtNoticeID.getValue().toString();
				String sqlDel= " delete from tbNoticeInfo where vNoticeId='"+maxId+"'";
				System.out.println(sqlDel);
				session.createSQLQuery(sqlDel).executeUpdate();
			}
			
			String imagePath = imagePath(1,maxId)==null?imageLoc:imagePath(1,maxId);
			
			String Insert = " INSERT into tbNoticeInfo(dDate,vNoticeId,vSubject,vDescription,vAttach,isActive,UserId,UserIp,EntryTime,vUserName) values(" +
					" '"+dateFormat.format(dDate.getValue())+"'," +
					" '"+maxId+"'," +
					" '"+txtSubject.getValue().toString().trim().replaceAll("'", "#")+"'," +
					" '"+txtDescription.getValue().toString().trim().replaceAll("'", "#")+"','"+imagePath+"'," +
					" '"+cmbIsActive.getValue().toString()+"'," +
					" '"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP,'"+sessionBean.getUserName()+"') ";
			
			session.createSQLQuery(Insert).executeUpdate();
			tx.commit();
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
		finally{session.close();}
	}
	private void focusEnter()
	{
		allComp.add(dDate);
		allComp.add(txtSubject);
		allComp.add(txtDescription);
		allComp.add(cmbIsActive);

		allComp.add(cButton.btnSave);

		new FocusMoveByEnter(this,allComp);
	}

	private void txtClear()
	{
		dDate.setValue(new Date());
		txtNoticeID.setValue("");
		txtSubject.setValue("");
		txtDescription.setValue("");
		cmbIsActive.setValue(1);
		
		bpvUpload.fileName = "";
		bpvUpload.status.setValue(new Label("<font size=1px>(Select .pdf/.jpg Format)</font>",Label.CONTENT_XHTML));
		filePathTmp = "";
		bpvUpload.actionCheck = false;
		imageLoc = "0";
	}

	public void txtInit(boolean t)
	{
		dDate.setEnabled(!t);
		txtSubject.setEnabled(!t);
		txtDescription.setEnabled(!t);
		cmbIsActive.setEnabled(!t);
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
		mainLayout.setImmediate(false);
		mainLayout.setWidth("600px");
		mainLayout.setHeight("420px");
		mainLayout.setMargin(false);

		lblCommon = new Label("Date :");
		lblCommon.setImmediate(false);
		lblCommon.setWidth("-1px");
		lblCommon.setHeight("-1px");
		mainLayout.addComponent(lblCommon, "top:10.0px; left:10.0px;");
		dDate=new PopupDateField();
		dDate.setImmediate(true);
		dDate.setWidth("110px");
		dDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dDate.setDateFormat("dd-MM-yyyy");
		dDate.setValue(new Date());
		mainLayout.addComponent(dDate, "top:08.0px; left:80.5px;");

		lblCommon = new Label("Ref. No. :");
		mainLayout.addComponent(lblCommon, "top:10.0px; left:340.0px;");
		txtNoticeID = new TextField();
		txtNoticeID.setImmediate(true);
		txtNoticeID.setWidth("110px");
		txtNoticeID.setHeight("24px");
		mainLayout.addComponent(txtNoticeID, "top:08.0px; left:420.5px;");
		txtNoticeID.setEnabled(false);
		
		
		lblCommon = new Label("Subject :");
		mainLayout.addComponent(lblCommon, "top:40.0px; left:10.0px;");
		txtSubject = new TextField();
		txtSubject.setImmediate(true);
		txtSubject.setWidth("300px");
		txtSubject.setHeight("24px");
		mainLayout.addComponent(txtSubject, "top:38.0px;left:80.5px;");

		lblCommon = new Label("Description :");
		mainLayout.addComponent(lblCommon, "top:70.0px; left:10.0px;");
		txtDescription = new TextArea();
		txtDescription.setWidth("450px");
		txtDescription.setHeight("200px");
		txtDescription.setImmediate(true);
		mainLayout.addComponent(txtDescription, "top:68.0px;left:80.5px;");
		

		// bpvUpload
		lblCommon = new Label("File Upload :");
		mainLayout.addComponent(lblCommon, "top:300.0px; left:10.0px;");
		mainLayout.addComponent(bpvUpload, "top:275.0px;left:76.0px;");

		// btnPreview
		btnPreview = new Button("Note/Memo Preview");
		btnPreview.setStyleName(BaseTheme.BUTTON_LINK);
		btnPreview.addStyleName("icon-after-caption");
		btnPreview.setImmediate(true);
		btnPreview.setIcon(new ThemeResource("../icons/document-pdf.png"));
		mainLayout.addComponent(btnPreview, "top:300.0px;left:210.0px;");
		

		lblIsActive = new Label("Status :");
		lblIsActive.setImmediate(true);
		lblIsActive.setWidth("-1px");
		lblIsActive.setHeight("-1px");

		cmbIsActive = new NativeSelect();
		cmbIsActive.setNullSelectionAllowed(false);
		cmbIsActive.setImmediate(true);
		cmbIsActive.setWidth("80px");
		cmbIsActive.setHeight("-1px");
		for (int i = 0; i < status.length; i++)
		{
			cmbIsActive.addItem(i);
			cmbIsActive.setItemCaption(i, status[i]);
		}
		cmbIsActive.setValue(1);
		mainLayout.addComponent(lblIsActive, "top:330.0px; left:10.0px;");
		mainLayout.addComponent(cmbIsActive,"top:328.0px; left:80.0px;");

		mainLayout.addComponent(cButton,"bottom:15px; left:30px;");

		return mainLayout;
	}
}
