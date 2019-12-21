package hrm.common.reportform;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

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
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class RptNoticeInfo extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	
	private ComboBox cmbSubject,cmbStatus;
	CheckBox chkStatusAll=new CheckBox("All");
	private Label lblFromDate;
	private PopupDateField dFromDate;

	private Label lblToDate;
	private PopupDateField dToDate;
	
	private Label lblMonth;
	private ComboBox dMonth;

	private OptionGroup opgTimeSelect;
	private List<?> dateType = Arrays.asList(new String[]{"Monthly","Between Date"});

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});

	private CommonButton cButton = new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	private SimpleDateFormat dDbFormat = new SimpleDateFormat("yyyy-MM-dd");
	private CommonMethod cm;
	private String menuId = "";

	TextField txtPath=new TextField();
	TextField txtAddress=new TextField();
	
	public RptNoticeInfo(SessionBean sessionBean,String menuId) 
	{
		this.sessionBean=sessionBean;
		this.setCaption("NOTICE INFORMATION. :: "+sessionBean.getCompany());
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		buildMainLayout();
		setContent(mainLayout);
		cmbStatusDataLoad();
		setEventAction();
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
	private void cmbStatusDataLoad() {
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String status="Active";
			String sql = "select distinct 0,isActive from tbNoticeInfo";
			List<?> list = session.createSQLQuery(sql).list();
			cmbStatus.removeAllItems();
			for(Iterator<?> iter = list.iterator();iter.hasNext();)
			{
				Object element[] = (Object[]) iter.next();
				cmbStatus.addItem(element[1]);
				if(element[1].toString().equals("1"))
				{
					status="Active";
					cmbStatus.setItemCaption(element[1], status);
				}
				else if(element[1].toString().equals("0"))
				{
					status="Inactive";
					cmbStatus.setItemCaption(element[1], status);
				}
			}
		}
		catch(Exception exp)
		{
			showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void cmbSubjectDataLoad() {
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		String sql="";
		String status="%";
		if(!chkStatusAll.booleanValue())
		{
			status=cmbStatus.getValue().toString();
		}
		
		try
		{
			if(opgTimeSelect.getValue().toString()=="Monthly")
			{				
				sql="select distinct vNoticeId,REPLACE(vSubject,'~','''')vSubject from tbNoticeInfo where isActive like'"+status+"' " +
						"and MONTH(dDate) =MONTH('"+dMonth.getValue()+"') " +
						"and YEAR(dDate) =YEAR('"+dMonth.getValue()+"') ";
			}
			else
			{
				sql = "select distinct vNoticeId,REPLACE(vSubject,'~','''')vSubject from tbNoticeInfo where isActive like'"+status+"' " +
						"and dDate between '"+dDbFormat.format(dFromDate.getValue())+"' and '"+dDbFormat.format(dToDate.getValue())+"' ";
			}
			System.out.println("cmbSubjectDataLoad: "+sql);
			
			List<?> list = session.createSQLQuery(sql).list();
			cmbSubject.removeAllItems();
			for(Iterator<?> iter = list.iterator();iter.hasNext();)
			{
				Object element[] = (Object[]) iter.next();
				cmbSubject.addItem(element[0].toString());
				cmbSubject.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	public void cmbMonthDataLoad()
	{
		String status="%";
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		

		try
		{
			String query="select distinct convert(date,cast(cast(Year(dDate)as varchar(120))+'-'+cast(Month(dDate)as varchar(120))+'-'+'01' as Date),105) Id," +
					"cast(dateName(MM,dDate)as varchar(120))+'-'+cast(Year(dDate)as varchar(120)) Name,cast(MONTH(dDate) as int) dDeliveryDate," +
					"cast(year(dDate) as int) dDeliveryDateYear from tbNoticeInfo order by  dDeliveryDateYear desc,dDeliveryDate desc";
			System.out.println("cmbMonthDataLoad :"+query);
			
			List <?> list=session.createSQLQuery(query).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				dMonth.addItem(element[0]);
				dMonth.setItemCaption(element[0],(element[1].toString()));
				
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbMonthDataLoad",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			session.close();
		}
	}
	private void setEventAction()
	{
		cmbStatus.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(cmbStatus.getValue()!=null)
				{
					dMonth.removeAllItems();
					cmbMonthDataLoad();
				}
			}
		});
		chkStatusAll.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				if(chkStatusAll.booleanValue())
				{
					cmbStatus.setValue(null);
					cmbStatus.setEnabled(false);

					dMonth.removeAllItems();
					cmbMonthDataLoad();
				}
				else{
					dMonth.removeAllItems();
					cmbStatus.setEnabled(true);
					
				}
			}
		});
		
		opgTimeSelect.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbStatus.getValue()!=null)
				{
					dMonth.removeAllItems();
					opgTimeSelectAction();
				}
			}
		});
		dMonth.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbStatus.getValue()!=null || chkStatusAll.booleanValue())
				{
					if(opgTimeSelect.getValue().toString()=="Monthly")
					{
						if(dMonth.getValue()!=null)
						{
							cmbSubject.removeAllItems();
							cmbSubjectDataLoad();
						}
					}
					else
					{
						if(dFromDate.getValue()!=null && dToDate.getValue()!=null)
						{
							cmbSubject.removeAllItems();
							cmbSubjectDataLoad();
						}
					}
				}
			}
		});
		dFromDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbStatus.getValue()!=null || chkStatusAll.booleanValue())
				{
					if(opgTimeSelect.getValue().toString()=="Monthly")
					{
						if(dMonth.getValue()!=null)
						{
							cmbSubject.removeAllItems();
							cmbSubjectDataLoad();
						}
					}
					else
					{
						if(dFromDate.getValue()!=null && dToDate.getValue()!=null)
						{
							cmbSubject.removeAllItems();
							cmbSubjectDataLoad();
						}
					}
				}
			}
		});
		dToDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbStatus.getValue()!=null || chkStatusAll.booleanValue())
				{
					if(opgTimeSelect.getValue().toString()=="Monthly")
					{
						if(dMonth.getValue()!=null)
						{
							cmbSubject.removeAllItems();
							cmbSubjectDataLoad();
						}
					}
					else
					{
						if(dFromDate.getValue()!=null && dToDate.getValue()!=null)
						{
							cmbSubject.removeAllItems();
							cmbSubjectDataLoad();
						}
					}
				}
			}
		});
		cButton.btnPreview.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbStatus.getValue()!=null || chkStatusAll.booleanValue())
				{
					if(cmbSubject.getValue()!=null)
					{
						if(opgTimeSelect.getValue().toString().equals("Monthly"))
						{
							if(dMonth.getValue()!=null)
							{
								reportpreview();
							}
						}
						else
						{
							if(dFromDate.getValue()!=null && dToDate.getValue()!=null)
							{
								reportpreview();
							}
							else
							{
								showNotification("Warning","Select Date Range",Notification.TYPE_WARNING_MESSAGE);
							}
						}
					}
					else
					{
						showNotification("Warning","Select Subject Name",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					showNotification("Warning","Select Status",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		cButton.btnExit.addListener(new Button.ClickListener() 
		{	
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});
	}
	public void opgActionWiseVisible(boolean b)
	{
		lblMonth.setVisible(b);
	    dMonth.setVisible(b);
	    lblFromDate.setVisible(!b);
	    dFromDate.setVisible(!b);
	    lblToDate.setVisible(!b);
	    dToDate.setVisible(!b);
	}
	private void opgTimeSelectAction()
	{
		if(opgTimeSelect.getValue().toString()=="Monthly")
		{
			opgActionWiseVisible(true);
			dFromDate.setValue(new Date());
			dToDate.setValue(new Date());
			
			cmbSubject.removeAllItems();
			if(dMonth.getValue()!=null)
			{
				cmbSubjectDataLoad();
			}
		}
		else
		{
			opgActionWiseVisible(false);
			dFromDate.setValue(new Date());
			dToDate.setValue(new Date());
			
			cmbSubject.removeAllItems();
			if(dFromDate.getValue()!=null && dToDate.getValue()!=null)
			{
				cmbSubjectDataLoad();
			}
		}
	}
	
	private void reportpreview()
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		String query="";
		try
		{
			query="select dDate,vNoticeId,REPLACE(vSubject,'~','''')vSubject,REPLACE(vDescription,'~','''')vDescription from tbNoticeInfo where vNoticeId='"+cmbSubject.getValue()+"'";
			
			System.out.println("Report Query: "+query);

			if(queryValueCheck(query))
			{
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("userName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
				hm.put("SysDate",reportTime.getTime);
				hm.put("logo",sessionBean.getCompanyLogo());
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/account/hrmModule/RptNoticeInfo.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",RadioBtn.Radio);

				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);
			}
			else
			{
				showNotification("Warning","There are no Data",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private boolean queryValueCheck(String sql)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try 
		{
			Iterator <?> iter = session.createSQLQuery(sql).list().iterator();

			if (iter.hasNext()) 
			{
				return true;
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		finally{session.close();}
		return false;
	}
	
	private AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("430px");
		setHeight("280px");
		
		
		cmbStatus=new ComboBox();
		cmbStatus.setWidth("110.0px");
		cmbStatus.setHeight("-1px");
		cmbStatus.setImmediate(true);
		cmbStatus.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Status : "), "top:10.0px;left:30.0px;");
		mainLayout.addComponent(cmbStatus, "top:18.0px;left:135.0px");
		chkStatusAll.setImmediate(true);
		mainLayout.addComponent(chkStatusAll, "top:20.0px;left:250.0px");
		

		opgTimeSelect=new OptionGroup("",dateType);
		opgTimeSelect.select("Monthly");
		opgTimeSelect.setImmediate(true);
		opgTimeSelect.setStyleName("horizontal");
		mainLayout.addComponent(opgTimeSelect, "top:50.0px; left:130.0px;");

		// lblFromDate
		lblFromDate = new Label("From Date :");
		lblFromDate.setImmediate(false);
		lblFromDate.setWidth("100.0%");
		lblFromDate.setHeight("-1px");
		mainLayout.addComponent(lblFromDate,"top:80.0px; left:30.0px;");
		lblFromDate.setVisible(false);
		// dFromDate
		dFromDate = new PopupDateField();
		dFromDate.setImmediate(true);
		dFromDate.setWidth("110px");
		dFromDate.setHeight("-1px");
		dFromDate.setDateFormat("dd-MM-yyyy");
		dFromDate.setValue(new java.util.Date());
		dFromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dFromDate, "top:78.0px; left:135.0px;");
		dFromDate.setVisible(false);
		//lblMonth
		lblMonth = new Label("Month :");
		lblMonth.setImmediate(false);
		lblMonth.setWidth("100.0%");
		lblMonth.setHeight("-1px");
		mainLayout.addComponent(lblMonth,"top:80.0px; left:30.0px;");
		
		// dMonth
		dMonth = new ComboBox();
		dMonth.setImmediate(true);
		dMonth.setWidth("150px");
		dMonth.setHeight("-1px");
		dMonth.setNullSelectionAllowed(true);
		dMonth.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(dMonth, "top:78.0px; left:135.0px;");
		
		// lblToDate
		lblToDate = new Label("To");
		lblToDate.setImmediate(false);
		lblToDate.setWidth("100.0%");
		lblToDate.setHeight("-1px");
		mainLayout.addComponent(lblToDate,"top:80.0px; left:250.0px;");
		lblToDate.setVisible(false);

		// dToDate
		dToDate = new PopupDateField();
		dToDate.setImmediate(true);
		dToDate.setWidth("110px");
		dToDate.setHeight("-1px");
		dToDate.setDateFormat("dd-MM-yyyy");
		dToDate.setValue(new java.util.Date());
		dToDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dToDate, "top:78.0px; left:268.0px;");
		dToDate.setVisible(false);
		
		cmbSubject=new ComboBox();
		cmbSubject.setWidth("250.0px");
		cmbSubject.setHeight("-1px");
		cmbSubject.setImmediate(true);
		cmbSubject.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Subject : "), "top:110.0px;left:30.0px;");
		mainLayout.addComponent(cmbSubject, "top:108.0px;left:135.0px");
		
		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:140px;left:150px;");

		
		mainLayout.addComponent(cButton,"top:170.opx; left:135.0px");

		return mainLayout;
	}
}
