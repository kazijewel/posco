package hrm.common.reportform;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

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
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Component.Listener;

public class RptMonthlyOt extends Window
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblMonth;
	private Label lblSection;

	private InlineDateField dMonth;
	private ComboBox cmbSection;
	private CheckBox chkSectionAll;

	ArrayList<Component> allComp = new ArrayList<Component>();

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	private ReportDate reportTime = new ReportDate();

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});

	public RptMonthlyOt(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setCaption("MONTHLY OT :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);
		cmbSectionAddData();
		setEventAction();
	}

	public void cmbSectionAddData()
	{
		cmbSection.removeAllItems();
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery(" select Distinct vSectionId,vSectionName from tbEmployeeAttendance" +
					" where MONTH(dAttDate)=MONTH('"+dateformat.format(dMonth.getValue())+"') and Year(dAttDate)=" +
					"Year('"+dateformat.format(dMonth.getValue())+"') and OtStatus='1'").list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{

				Object[] element = (Object[]) iter.next();
				cmbSection.addItem(element[0]);
				cmbSection.setItemCaption(element[0], (String) element[1]);
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public void setEventAction()
	{
		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbSection.getValue()!=null || chkSectionAll.booleanValue()==true)
				{
					getAllData();
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
		
		dMonth.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				cmbSectionAddData();
			}
		});

		chkSectionAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(chkSectionAll.booleanValue()==true)
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
	}

	private void getAllData()
	{
		String sectionValue = "";

		if(chkSectionAll.booleanValue()==true)
		{
			sectionValue = "%";
		}
		else
		{
			sectionValue = cmbSection.getValue().toString();
		}

		reportShow(sectionValue);
	}

	private void reportShow(String Section)
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		String query=null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			HashMap hm = new HashMap();

			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			hm.put("SysDate",reportTime.getTime);
			hm.put("month",dMonth.getValue());
			hm.put("logo", sessionBean.getCompanyLogo());

			query=" select * from funMonthWiseOtStatement('"+dateformat.format(dMonth.getValue())+"','"+Section+"') " +
				  " where totalAmountThis>0 or totalAmountPrev>0 order by sectionId";
			if(queryValueCheck(query))
			{
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/account/hrmModule/rptMonthWiseOtSignature.jasper",
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
		Transaction tx = null;

		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			Iterator iter = session.createSQLQuery(sql).list().iterator();

			if (iter.hasNext()) 
			{
				return true;
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		return false;
	}

	private void focusMove()
	{
		//allComp.add(dMonth);
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
		setWidth("400px");
		setHeight("200px");

		// lblMonth
		lblMonth = new Label("Month :");
		lblMonth.setImmediate(true);
		lblMonth.setWidth("100.0%");
		lblMonth.setHeight("-1px");
		mainLayout.addComponent(lblMonth,"top:30.0px; left:30.0px;");

		// dMonth
		dMonth = new InlineDateField();
		dMonth.setImmediate(true);
		dMonth.setWidth("150px");
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
		mainLayout.addComponent(lblSection,"top:60.0px; left:30.0px;");

		// cmbSection
		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("200px");
		cmbSection.setHeight("-1px");
		cmbSection.setNullSelectionAllowed(true);
		cmbSection.setImmediate(true);
		mainLayout.addComponent(cmbSection, "top:58.0px; left:130.0px;");

		//chkSectionAll
		chkSectionAll = new CheckBox("All");
		chkSectionAll.setHeight("-1px");
		chkSectionAll.setWidth("-1px");
		chkSectionAll.setImmediate(true);
		mainLayout.addComponent(chkSectionAll, "top:60.0px; left:336.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:90.0px;left:130.0px;");

		mainLayout.addComponent(cButton,"top:120.opx; left:120.0px");

		return mainLayout;
	}
}
