package hrm.common.reportform;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.CommonMethod;
import com.common.share.ReportDate;
import com.common.share.ReportOption;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class RptDataEntryStatus extends Window
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private DateField dFromDate = new DateField();
	private DateField dToDate = new DateField();
	
	private PopupDateField dToDate1=new PopupDateField();
	
	private Label lblFromdate,lblTodate,lblType,lblUser;

	private ComboBox cmbUser;
	private OptionGroup RadioBtnDate;
	private CheckBox checkAll;
	private static final List<String> optionDate = Arrays.asList(new String[]{"As On Date","Between Date"});
	
	private OptionGroup RadioBtnGroup;
	private static final List<String> option = Arrays.asList(new String[]{"PDF","Other"});
	
	
	private OptionGroup RadioBtnType;
	private static final List<String> Type = Arrays.asList(new String[]{"Summary","Details"});
	
	
	CommonButton button = new CommonButton("", "", "", "","","","","Preview","","Exit");
	
	private SimpleDateFormat dtfYMD = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
	
	private ReportDate reportTime = new ReportDate();
	private String  menuId = "";
	private CommonMethod cm;
	public RptDataEntryStatus(SessionBean sessionBean, String menuId)
	{
		this.sessionBean = sessionBean;
		this.menuId = menuId;
		cm = new CommonMethod(sessionBean);
		
		this.setCaption("DATA ENTRY STATUS :: "+this.sessionBean.getCompany());
				
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);
		
		btnAction();
		cmbAddGroupData();
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
			{button.btnSave.setVisible(false);}
			if(!cm.isEdit)
			{button.btnEdit.setVisible(false);}
			if(!cm.isDelete)
			{button.btnDelete.setVisible(false);}
			if(!cm.isPreview)
			{button.btnPreview.setVisible(false);}
		}
		}
	}

	private void btnAction()
	{
		RadioBtnType.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(RadioBtnType.getValue().toString().equalsIgnoreCase("Details"))
				{
					checkAll.setVisible(false);
					cmbUser.setEnabled(true);				
				}
				else
				{
					checkAll.setValue(false);
					cmbUser.setEnabled(true);
					checkAll.setVisible(true);
					
				}
			}
		});
		RadioBtnDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(RadioBtnDate.getValue().toString().equalsIgnoreCase("As On Date"))
				{
					lblFromdate.setVisible(false);
					dFromDate.setVisible(false);
					lblTodate.setValue("As on Date : ");
					dToDate.setEnabled(true);			
				}
				else
				{
					lblFromdate.setVisible(true);
					dFromDate.setVisible(true);
					lblTodate.setValue("To Date : ");
					dToDate.setEnabled(false);
					
				}
			}
		});
		dFromDate.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
				if(dFromDate.getValue()!=null)
				{
					Calendar cal = Calendar.getInstance();
					
					Date date1=(Date)dFromDate.getValue();
					
					cal.setTime(date1);
					Date today = cal.getTime();
					
					cal.add(Calendar.MONTH, 1); // to get previous year add -1
					Date nextMonth = cal.getTime();
					dToDate.setValue(nextMonth);
				}
			}
		});
		
		button.btnPreview.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
								
				previewBtnAction();
																	
			}
		});
		checkAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				
				if(checkAll.booleanValue())
				{
					cmbUser.setEnabled(false);
					cmbUser.setValue(null);
				}
				else
				{
					cmbUser.setEnabled(true);
				}

			}
		});
		button.btnExit.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});
	}

	public void cmbAddGroupData()
	{
		cmbUser.removeAllItems();
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery("select vUserId,vUserName from tbUserInfo  order by vUserName").list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbUser.addItem(element[0].toString());
				cmbUser.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	private void previewBtnAction()
	{
		ReportOption rptOption = new ReportOption(RadioBtnGroup.getValue().toString());
		String query = null,rptName="";
		try
		{
			String userId="%",userName="%";
			if(cmbUser.getValue()!=null)
			{
				userId=cmbUser.getValue().toString();
				userName=cmbUser.getItemCaption(cmbUser.getValue());
			}
			
			if(RadioBtnType.getValue().toString().equals("Summary"))
			{
				if(RadioBtnDate.getValue().toString().equals("As On Date"))
				{
					query="select * from funDataEntryStatusAsOnDate('"+userId+"','"+userName+"','"+sessionBean.dfDb.format(dToDate.getValue())+"')";
				}
				else if(RadioBtnDate.getValue().toString().equals("Between Date"))
				{
					query="select * from funDataEntryStatus('"+userId+"','"+userName+"','"+sessionBean.dfDb.format(dFromDate.getValue())+"','"+sessionBean.dfDb.format(dToDate.getValue())+"')";
				}
				rptName="report/account/hrmModule/RptDataEntrySummary.jasper";
			}
			else
			{
				if(RadioBtnDate.getValue().toString().equals("As On Date"))
				{
					query="select * from funDataEntryStatusDetailsAsOnDate('"+userId+"','"+userName+"','"+sessionBean.dfDb.format(dToDate.getValue())+"')";
				}
				else if(RadioBtnDate.getValue().toString().equals("Between Date"))
				{
					query="select * from funDataEntryStatusDetails('"+userId+"','"+userName+"','"+sessionBean.dfDb.format(dFromDate.getValue())+"','"+sessionBean.dfDb.format(dToDate.getValue())+"') ";
				}
				rptName="report/account/hrmModule/RptDataEntryStatusDetails.jasper";
			}
			System.out.println(query);
			if(queryCheckValue(query))
			{
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("UserIp", sessionBean.getUserIp());
				hm.put("UserName", sessionBean.getUserName());
				hm.put("developer",sessionBean.getDeveloperAddress());
				hm.put("SysDate",reportTime.getTime);
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("sql", query);
				if(RadioBtnType.getValue().toString().equals("Details"))
				{
					if(RadioBtnDate.getValue().toString().equals("Between Date"))
					{
						hm.put("dataEntryName", "User Name :"+cmbUser.getItemCaption(cmbUser.getValue())+"    From :"+sessionBean.dfBd.format(dFromDate.getValue())+" To :"+sessionBean.dfBd.format(dToDate.getValue()));
					}
					else if(RadioBtnDate.getValue().toString().equals("As On Date"))
					{
						hm.put("dataEntryName", "User Name :"+cmbUser.getItemCaption(cmbUser.getValue())+"    Date :"+sessionBean.dfBd.format(dFromDate.getValue()));
					}
				}
				
				
				Window win = new ReportViewer(hm,rptName,
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",true);

				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);
			}
			else
			{
				showNotification("No data found ",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception exp)
		{
			System.out.println(exp);
		}
	}

	private boolean queryCheckValue(String query)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> lst = session.createSQLQuery(query).list();
			if(!lst.isEmpty())
				return true;
		}
		catch (Exception exp)
		{
			showNotification("queryCheckValue", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		return false;
	}

	private AbsoluteLayout buildMainLayout() 
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("500px");
		setHeight("330px");
		
		lblType= new Label();
		lblType.setImmediate(true);
		lblType.setWidth("-1px");
		lblType.setHeight("-1px");
		lblType.setValue("Type: ");
		mainLayout.addComponent(lblType, "top:20.0px;left:50.0px;");
		
		RadioBtnType = new OptionGroup("",Type);
		RadioBtnType.setImmediate(true);
		RadioBtnType.setValue("Summary");
		RadioBtnType.setStyleName("horizontal");
		mainLayout.addComponent(RadioBtnType, "top:20.0px; left:150.0px;");
								
		RadioBtnDate = new OptionGroup("",optionDate);
		RadioBtnDate.setImmediate(true);
		RadioBtnDate.setValue("As On Date");
		RadioBtnDate.setStyleName("horizontal");
		mainLayout.addComponent(RadioBtnDate, "top:50.0px; left:150.0px;");
		
		lblFromdate = new Label();
		lblFromdate.setImmediate(true);
		lblFromdate.setWidth("-1px");
		lblFromdate.setHeight("-1px");
		lblFromdate.setValue("From Date: ");
		lblFromdate.setVisible(false);
		mainLayout.addComponent(lblFromdate, "top:80.0px;left:50.0px;");

		dFromDate.setDateFormat("dd-MM-yyyy");
		dFromDate.setValue(new Date());
		dFromDate.setWidth("110px");
		dFromDate.setImmediate(true);
		dFromDate.setVisible(false);
		dFromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dFromDate, "top:78.0px;left:150.0px;");
		
		lblTodate= new Label();
		lblTodate.setImmediate(true);
		lblTodate.setWidth("-1px");
		lblTodate.setHeight("-1px");
		lblTodate.setValue("As on Date : ");
		mainLayout.addComponent(lblTodate, "top:110.0px;left:50.0px;");

		dToDate.setDateFormat("dd-MM-yyyy");
		dToDate.setValue(new Date());
		dToDate.setWidth("110px");
		dToDate.setImmediate(true);
		dToDate.setResolution(PopupDateField.RESOLUTION_DAY); 
		mainLayout.addComponent(dToDate, "top:108.0px;left:150.0px;");
			
		lblUser = new Label("User :");
		lblUser.setImmediate(true);
		lblUser.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblUser, "top:140px;left:50.0px;");

		cmbUser=new ComboBox();
		cmbUser.setImmediate(true);
		cmbUser.setWidth("200px");
		cmbUser.setHeight("-1px");
		cmbUser.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbUser, "top:138px;left:150px;");
		
		checkAll=new CheckBox("All");
		checkAll.setImmediate(true);
		checkAll.setHeight("-1px");
		mainLayout.addComponent(checkAll,"top:140px; left:353px");
			
		RadioBtnGroup = new OptionGroup("",option);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setValue("PDF");
		RadioBtnGroup.setStyleName("horizontal");
		mainLayout.addComponent(RadioBtnGroup, "top:180.0px; left:150.0px;");
		RadioBtnGroup.setVisible(false);
		//CButton
		mainLayout.addComponent(button, "top:230.0px;left:150.0px;");

		return mainLayout;
	}
}
