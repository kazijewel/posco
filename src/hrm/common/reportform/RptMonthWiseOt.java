package hrm.common.reportform;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
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
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class RptMonthWiseOt extends Window
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblMonth;
	private Label lblSection;

	private PopupDateField dMonth;
	private ComboBox cmbSection;
	private CheckBox chkSectionAll;

	ArrayList<Component> allComp = new ArrayList<Component>();

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	private ReportDate reportTime = new ReportDate();

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});

	public RptMonthWiseOt(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setCaption("MONTH WISE OT :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		focusMove();
		setContent(mainLayout);
		setEventAction();
		cmbSectionAddData();
	}

	public void cmbSectionAddData()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select distinct ms.vSectionID,ms.vSectionName from tbMonthlySalary ms inner join tbEmpOfficialPersonalInfo ein "
					+ " on ms.vEmployeeID = ein.vEmployeeId where ein.iOtEnable = 1 and ms.vSalaryYear = YEAR('"+dateformat.format(dMonth.getValue())+"') "
					+ " and MONTH(ms.dSalaryDate) = MONTH('"+dateformat.format(dMonth.getValue())+"')";
			
			System.out.println("Section data add :" +query);
			List <?> list=session.createSQLQuery(query).list();

			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbSection.addItem(element[0]);
				cmbSection.setItemCaption(element[0],element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("cmbSectionAddData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void setEventAction()
	{
		dMonth.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSection.removeAllItems();
				if(dMonth.getValue() !=null)
				{
					cmbSectionAddData();
				}
			}
		});

		chkSectionAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(chkSectionAll.booleanValue())
				{	
					cmbSection.setValue(null);
					cmbSection.setEnabled(false);
				}
				else
				{
					cmbSection.setEnabled(true);
				}
			}
		});
		
		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
				{
					reportShow();
				}
				else
				{
					showNotification("Warniung!","Select Section Name",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		cButton.btnExit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});
	}

	private void reportShow()
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		try
		{
			String query = " select * from funMonthWiseOtStatement('"+dateformat.format(dMonth.getValue())+"',"
					+ " '"+(cmbSection.getValue()!=null?cmbSection.getValue().toString():"%")+"') where totalAmount>0 or totalAmountPre>0  order by "
					+ "vsectionname,vEmployeeName";
			
			System.out.println("Report Query :" + query);

			if(queryValueCheck(query))
			{
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("user", sessionBean.getUserName());
				hm.put("userIp", sessionBean.getUserIp());
				hm.put("SysDate",reportTime.getTime);
				hm.put("month",dMonth.getValue());
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"hrm/jasper/OT/RptMonthWiseOT.jasper",
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
		catch(Exception exp){
			this.getParent().showNotification("Error "+exp,Notification.TYPE_ERROR_MESSAGE);}
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

	private void focusMove()
	{
		allComp.add(dMonth);
		allComp.add(cmbSection);
		allComp.add(cButton.btnPreview);
		new FocusMoveByEnter(this,allComp);
	}

	private AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("450px");
		setHeight("230px");

		// lblMonth
		lblMonth = new Label("Month :");
		lblMonth.setImmediate(true);
		lblMonth.setWidth("100.0%");
		lblMonth.setHeight("-1px");
		mainLayout.addComponent(lblMonth,"top:30.0px; left:40.0px;");

		// dMonth
		dMonth = new PopupDateField();
		dMonth.setImmediate(true);
		dMonth.setWidth("130px");
		dMonth.setHeight("-1px");
		dMonth.setDateFormat("MMMMM-yyyy");
		dMonth.setValue(new Date());
		dMonth.setResolution(PopupDateField.RESOLUTION_MONTH);
		mainLayout.addComponent(dMonth, "top:28.0px; left:130.0px;");

		// lblSection
		lblSection = new Label("Section Name :");
		lblSection.setImmediate(true);
		lblSection.setWidth("100.0%");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection,"top:70.0px; left:30.0px;");

		// cmbSection
		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("250px");
		cmbSection.setHeight("-1px");
		cmbSection.setNullSelectionAllowed(true);
		cmbSection.setImmediate(true);
		mainLayout.addComponent(cmbSection, "top:68.0px; left:130.0px;");

		// chkSectionAll
		chkSectionAll = new CheckBox("All");
		chkSectionAll.setHeight("-1px");
		chkSectionAll.setWidth("-1px");
		chkSectionAll.setImmediate(true);
		mainLayout.addComponent(chkSectionAll, "top:70.0px; left:386.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:110.0px;left:130.0px;");
		mainLayout.addComponent(cButton,"top:130.opx; left:120.0px");
		return mainLayout;
	}
}
