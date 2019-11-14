package acc.menuform.menu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.appform.hrmModule.addExtraOrLessOTHour;
import com.common.share.ChangePass;
import com.common.share.MessageBox.EventListener;
import com.common.share.CompanyInformation;
import com.common.share.DatabaseBackup;
import com.common.share.ImmediateFileUpload;
import com.common.share.LogIn;
import com.common.share.MessageBox;
import com.common.share.MessageBox.ButtonType;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.github.wolfie.refresher.Refresher;
import com.github.wolfie.refresher.Refresher.RefreshListener;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.BaseTheme;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class RootMenu extends HorizontalLayout 
{
	private Panel mainWindow = new Panel();

	private Panel panelTop = new Panel();
	private Panel panelUp = new Panel();
	private Panel panelMiddle = new Panel();

	private Tree txtTree;

	private FormLayout fmLayout = new FormLayout();

	private Label lblUserName = new Label();
	private Label lblUserDay = new Label();
	private Label lblResolution = new Label();
	public Button btnImage = new Button("");
	public Button btnLogout = new Button("Logout");
	public Button btnChangePassword = new Button("Change Password");

	private InlineDateField date = new InlineDateField();
	
	public Button btnOrganogramPreview;

	public String organogramPdf=null;
	public String organogramFilePathTmp="";
	
	String employeeOrganogram="0";
	public String organogramLoc="0";
	
	public HorizontalLayout hLayout = new HorizontalLayout();
	public HorizontalLayout image = new HorizontalLayout();

	private static final Object CAPTION_PROPERTY = "caption";

	private SessionBean sessionBean;
	private Label timeLabel;
	String typeOfPublic;
	public  RootMenu(SessionBean sessionBean,String typeOfPublic) 
	{	
		this.sessionBean = sessionBean;
		this.typeOfPublic=typeOfPublic;
		this.setWidth("100%");
		this.setHeight("600px");
		
		// btnBirthPreview
		btnOrganogramPreview = new Button();
		btnOrganogramPreview.setStyleName(Button.STYLE_LINK);
		btnOrganogramPreview.addStyleName("icon-after-caption");
		btnOrganogramPreview.setImmediate(true);
		btnOrganogramPreview.setIcon(new ThemeResource("../icons/document-pdf.png"));
		
		organogramLoc=organoGramImageLoc();
		if(organogramLoc.equals("0"))
		{
			btnOrganogramPreview.setCaption("Attach Organogram");
		}
		else
		{
			btnOrganogramPreview.setCaption("Preview Organogram");
		}
		
		
		mainWindow.setWidth("550px");
		mainWindow.setHeight("100%");
		mainWindow.setImmediate(true);
		mainWindow.setScrollable(true);

		if(typeOfPublic.equals("Private"))
		{moduleAccessCheck();}
		else{anotherAccessCheck();}

		// txtTree
		txtTree = new Tree();
		txtTree.setCaption("LIST OF MODULES");
		txtTree.setImmediate(true);
		txtTree.setWidth("100%");
		txtTree.setHeight("100%");
		txtTree.setStyleName("aa");

		btnImage.setStyleName(BaseTheme.BUTTON_LINK);
		btnLogout.setStyleName(BaseTheme.BUTTON_LINK);
		btnChangePassword.setStyleName(BaseTheme.BUTTON_LINK);
		// tree Item Add
		if(typeOfPublic.equals("Private")){addTree();}
		else{addAnotherTree();}

		getInfo();
		componentAdd();
		btnAction();

		mainWindow.addComponent(panelTop);
		mainWindow.addComponent(panelUp);
		
		timeLabel = new Label("<b><font size='10' color='#3B8FBA'></font></b>",Label.CONTENT_XHTML);
		timeLabel.setValue(getTime());
        Refresher refresher = new Refresher();
        refresher.setRefreshInterval(500);
        refresher.addListener(new RefreshListener(){
            public void refresh(Refresher source) {
                timeLabel.setValue("<b><font size='10' color='#3B8FBA'>"+getTime()+"</font></b>");
            }
        });
        
        
        fmLayout.addComponent(timeLabel); 
        fmLayout.addComponent(refresher);
        
		//fmLayout.addComponent(btnImage);
		fmLayout.addComponent(btnLogout);
		fmLayout.addComponent(btnChangePassword);
		fmLayout.addComponent(panelMiddle);
		date.setResolution(InlineDateField.RESOLUTION_DAY);
		fmLayout.addComponent(date);
		fmLayout.addComponent(btnOrganogramPreview);

		hLayout.addComponent(fmLayout);

		addComponent(mainWindow);
		addComponent(hLayout);
		setComponentAlignment(hLayout, Alignment.TOP_RIGHT);

		treeClickAction();
	}
	public String getTime(){
        DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss aa");
       // Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR,+0);
        String d = dateFormat.format(calendar.getTime());
        return d;
    }
	private void moduleAccessCheck()
	{
		sessionBean.setupModule = false;
		sessionBean.hrmModule = false;
		sessionBean.hrmAccountModule=false;
		Transaction tx;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String sql = " Select vModuleId, vModuleName from dbo.tbUserDetails where"
					+ " vUserId = '"+sessionBean.getUserId()+"'";

			System.out.println(sql);

			List lst = session.createSQLQuery(sql).list();

			for (Iterator iter = lst.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				int moduleId = Integer.parseInt(element[0].toString());

				if(moduleId==0)
				{
					sessionBean.setupModule = true;
				}

				if(moduleId==1)
				{
					sessionBean.hrmModule = true;
				}
				
				if(moduleId==2)
				{
					sessionBean.hrmAccountModule=true;
				}
			
			}
		}
		catch(Exception exp)
		{
			System.out.println(exp);
		}
	}
	private void anotherAccessCheck()
	{

		sessionBean.setupModule = false;
		sessionBean.hrmModule = false;
		sessionBean.hrmAccountModule=false;
	
	}
	public String organoGramImageLoc()
	{
		String imageLoc="0";
		Session session=SessionFactoryUtil.getInstance().openSession();
		try{
			String sql="select vOrganogram from tbCompanyInformation";
			System.out.println("company: "+sql);
			Iterator<?> iter=session.createSQLQuery(sql).list().iterator();
			if(iter.hasNext())
			{
				imageLoc=iter.next().toString();
			}
		}catch(Exception exp)
		{
			System.out.println("organoGramImageLoc"+exp);
		}
		return imageLoc;
	}
	private void btnAction()
	{
		btnLogout.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				userBackupMethod();
			}
		});
		btnChangePassword.addListener(new Button.ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				ChangePass cp=new ChangePass(sessionBean);
				cp.setStyleName("cwindow");
				cp.center();
				getWindow().addWindow(cp);
			}
		});
		btnOrganogramPreview.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(!organogramLoc.equalsIgnoreCase("0"))
				{
					String link = getApplication().getURL().toString().replaceAll(sessionBean.getContextName()+"/",organogramLoc.substring(22, organogramLoc.length()));
					getWindow().open(new ExternalResource(link),"_blank",500,200,Window.BORDER_NONE);
				}
				else
				{
					getWindow().showNotification("Warning!","No file found.",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});
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
	private void userBackupMethod()
	{
		final MessageBox mb = new MessageBox(getWindow(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to Log out?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
		mb.show(new EventListener()
		{
			public void buttonClicked(ButtonType buttonType)
			{
				if(buttonType == ButtonType.YES)
				{
					Window mainWindow = getWindow();
					Iterator iter = mainWindow.getChildWindows().iterator();

					while(iter.hasNext())
					{
						mainWindow.removeWindow((Window)iter.next());
						iter = mainWindow.getChildWindows().iterator();
					}

					mainWindow.removeAllComponents();
					LogIn login = new LogIn(sessionBean);
					mainWindow.getWindow().addWindow(login);
				}
				else
				{
					mb.close();
				}
			}
		});
	}

	private void getInfo()
	{
		String userName="";
		if(typeOfPublic.equals("Private"))
		{
			userName=sessionBean.getUserName();
		}
		else{
			userName="Public";
		}
		lblUserName.setValue("<b><font size=3px><i>User Name : "+userName+"</i></font></b>");
		lblUserName.setContentMode(Label.CONTENT_XHTML);

		String day = new SimpleDateFormat("EEEEE, dd MMMMM yyyy").format(new Date());
		lblUserDay.setValue("<b><i>Day :</i></b> "+day);
		lblUserDay.setContentMode(Label.CONTENT_XHTML);

		lblResolution.setValue("<b><i>Best Resolution : 1280*1024 </i></b> ");
		lblResolution.setContentMode(Label.CONTENT_XHTML);
	}

	private void componentAdd()
	{
		Embedded emb = new Embedded("",new ThemeResource("../icons/cb_14.png"));
		emb.requestRepaint();
		emb.setWidth("270px");
		emb.setHeight("100%");
		image.removeAllComponents();
		image.addComponent(emb);

		panelTop.addComponent(image);
		panelTop.setWidth("100%");
		panelTop.setHeight("100%");

		panelUp.setWidth("100%");
		panelUp.setHeight("100%");
		panelUp.addComponent(txtTree);
		panelUp.setScrollable(true);

		panelMiddle.setWidth("280px");
		panelMiddle.setHeight("100%");
		panelMiddle.addComponent(lblUserName);
		panelMiddle.addComponent(lblUserDay);
		panelMiddle.addComponent(lblResolution);
		panelMiddle.setStyleName("a");
	}

	private void treeClickAction()
	{
		txtTree.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{	
				if(event.getProperty().getValue()!=null)
				{
					System.out.println(txtTree.getValue());
				}
			}
		});
	}

	void addTree()
	{		
		txtTree.setDebugId("tree");
		txtTree.setImmediate(true);
		txtTree.addContainerProperty(CAPTION_PROPERTY, String.class, "");
		txtTree.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
		txtTree.setItemCaptionPropertyId(CAPTION_PROPERTY);

		addParents();
	}
	void addAnotherTree()
	{
		txtTree.setDebugId("tree");
		txtTree.setImmediate(true);
		txtTree.addContainerProperty(CAPTION_PROPERTY, String.class, "");
		txtTree.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
		txtTree.setItemCaptionPropertyId(CAPTION_PROPERTY);
		
		addAnotherParents();
	}
	void addAnotherParents()
	{
		Object hrmAnother=null;
		
		if(sessionBean.hrmModule)
		{
			hrmAnother=addCaptionedItem("HRM ANOTHER MODULE",null);
		}
	
		addAnotherChild(hrmAnother);
	}
	void addAnotherChild(Object hrmAnotherModule)
	{
		new publicMenu(hrmAnotherModule, txtTree, sessionBean,mainWindow,typeOfPublic);
	}
	void addParents()
	{
		Object setupModule = null,rawMaterialModule = null;
		Object productionModule = null,finishGoodsModule = null,demandOrderSalesModule = null;
		Object accountingMenu = null,fixedAssetMenu = null;
		Object hrmModule = null,transportModule = null,lcModule = null,emailModule=null;
		Object registermodule=null; 
		Object hrmAccountModule=null;
		
		

		if(sessionBean.setupModule)
		{
			setupModule = addCaptionedItem("SETUP MODULE", null);
		}
		if(sessionBean.hrmModule)
		{
			hrmModule = addCaptionedItem("HR DEPARTMENT", null);
		}
		if(sessionBean.hrmAccountModule)
		{
			hrmAccountModule=addCaptionedItem("ACCOUNTS DEPARTMENT", null);
		}
		
		
		

		addChild(setupModule,rawMaterialModule,productionModule,finishGoodsModule,demandOrderSalesModule,
				accountingMenu,fixedAssetMenu,hrmModule,transportModule,lcModule,emailModule,registermodule,hrmAccountModule);	
	}

	void addChild(Object setupModule,Object rawMaterialModule,Object productionModule, Object finishGoodsModule,
			Object demandOrderSalesModule,Object accountingMenu,Object fixedAssetMenu, Object hrmModule,
			Object transportModule, Object lcModule,Object emailModule,Object registermodule,Object hrmAccountModule)
	{
		if(sessionBean.setupModule)
		{new AdminMenu(setupModule,txtTree,sessionBean,mainWindow);}
		if(sessionBean.hrmModule)
		{new HrmMenu(hrmModule,txtTree,sessionBean,mainWindow);	}
		if(sessionBean.hrmAccountModule)
		{
			new HrmAccountMenu(hrmAccountModule, txtTree, sessionBean, mainWindow);
		}
		
	}

	private Object addCaptionedItem(String caption, Object parent) 
	{
		final Object id = txtTree.addItem();
		final Item item = txtTree.getItem(id);
		final Property p = item.getItemProperty(CAPTION_PROPERTY);

		p.setValue(caption);

		if (parent != null) 
		{
			txtTree.setChildrenAllowed(parent, true);
			txtTree.setParent(id, parent);
			txtTree.setChildrenAllowed(id, false);
		}
		return id;
	}
}

