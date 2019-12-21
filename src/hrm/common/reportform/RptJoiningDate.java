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
import com.common.share.FocusMoveByEnterForm;
import com.common.share.ReportDate;
import com.common.share.ReportOption;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.YesNoDialog;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

public class RptJoiningDate extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblSection;
	private Label lblCategory;
	private Label lblEmployeeName;

	private ComboBox cmbSection;
	private ComboBox cmbCategory;
	private ComboBox cmbEmployeeName;

	private CheckBox sectionAll;
	private CheckBox categoryAll;
	private CheckBox employeeAll;

	ArrayList<Component> allComp = new ArrayList<Component>();

	private static final String[] category = new String[] { "Permanent", "Temporary", "Provisionary", "Casual"};

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});

	public RptJoiningDate(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setCaption("EMPLOYEE JOINING DATE :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);

		cmbSectionAddData();

		setEventAction();
	}

	public void cmbSectionAddData()
	{
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery(" SELECT AutoID,SectionName from tbSectionInfo order by AutoID ").list();
			for(Iterator iter=list.iterator();iter.hasNext();){

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

	public void cmbEmployeeNameData(String Section)
	{
		cmbEmployeeName.removeAllItems();
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery(" select vEmployeeId,vEmployeeName from tbEmployeeInfo where vSectionId like '"+Section+"' and iStatus='1' ").list();	

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbEmployeeName.addItem(element[0]);
				cmbEmployeeName.setItemCaption(element[0], (String) element[1]);
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
				if(cmbSection.getValue()!=null || sectionAll.booleanValue()==true){
					if(cmbCategory.getValue()!=null || categoryAll.booleanValue()==true){
						if(cmbEmployeeName.getValue()!=null || employeeAll.booleanValue()==true){
							getAllData();
						}else{
							showNotification("Warning","Select Employee Name",Notification.TYPE_WARNING_MESSAGE);
						}
					}else{
						showNotification("Warning","Select Category Name",Notification.TYPE_WARNING_MESSAGE);
					}
				}else{
					showNotification("Warning","Select Section Name",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		cmbSection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbSection.getValue()!=null)
				{
					String Section= cmbSection.getValue().toString();
					cmbEmployeeNameData(Section);
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

		sectionAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(sectionAll.booleanValue()==true)
				{
					String Section= "%";
					cmbEmployeeNameData(Section);
					cmbSection.setValue(null);
					cmbSection.setEnabled(false);
				}
				else
				{
					cmbSection.setEnabled(true);
				}
			}
		});

		categoryAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(categoryAll.booleanValue()==true)
				{	
					cmbCategory.setValue(null);
					cmbCategory.setEnabled(false);
				}
				else
				{
					cmbCategory.setEnabled(true);
				}
			}
		});

		employeeAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(employeeAll.booleanValue()==true)
				{
					cmbEmployeeName.setValue(null);
					cmbEmployeeName.setEnabled(false);
				}
				else
				{
					cmbEmployeeName.setEnabled(true);
				}
			}
		});
	}

	private void getAllData()
	{
		String sectionValue = "";
		String categoryValue = "";
		String employeeValue = "";


		if(sectionAll.booleanValue()==true){
			sectionValue = "%";}
		else{
			sectionValue =cmbSection.getValue().toString();}

		if(categoryAll.booleanValue()==true){
			categoryValue = "%";}
		else{
			categoryValue = cmbCategory.getItemCaption(cmbCategory.getValue());}

		if(employeeAll.booleanValue()==true){
			employeeValue = "%";}
		else{
			employeeValue = cmbEmployeeName.getValue().toString();}

		reportShow(categoryValue,sectionValue,employeeValue);
	}

	private void reportShow(Object categoryValue,Object sectionValue,Object employeeValue){

		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		String query=null;
		String activeFlag = null;
		try{

			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			HashMap hm = new HashMap();
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			//hm.put("phone", sessionBean.getCompanyPhone());
			//hm.put("email", sessionBean.getCompanyEmail());
			//hm.put("fax", sessionBean.getCompanyFax());
			hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			//hm.put("userIp", sessionBean.getUserIp());
			hm.put("section",cmbSection.getItemCaption(cmbSection.getValue()));
			hm.put("SysDate",reportTime.getTime);
			hm.put("logo", sessionBean.getCompanyLogo());

			query =" select si.SectionName,ei.vEmployeeType," +
					"ei.vEmployeeName,di.designationName,ei.dJoiningDate from tbEmployeeInfo ei inner " +
					"join tbSectionInfo  as si on ei.vSectionId=si.AutoID inner join tbDesignationInfo" +
					" di on ei.vDesignationId=di.designationId where ei.vSectionId like '"+sectionValue+"'  and " +
					"ei.vEmployeeType like '"+categoryValue+"' and ei.vEmployeeId like '"+employeeValue+"' and iStatus='1' group by si.SectionName," +
					"ei.vEmployeeType,ei.vEmployeeName,di.designationName,ei.dJoiningDate";
					
					/*" SELECT b.SectionName+'-'+vEmployeeType as SectionName,b.SectionName as Section," +
					" vEmployeeType as employee,vEmployeeName,c.designationName,dJoiningDate " +
					" from tbEmployeeInfo as a inner join tbSectionInfo as b on a.vSectionId=b.AutoID" +
					" inner join tbDesignationInfo as c on a.vDesignationId=c.designationId where" +
					" vSectionId like '"+sectionValue+"' and" +
					" vEmployeeType like '"+categoryValue+"'" +
					" and vEmployeeId like '"+employeeValue+"' " +
					" Group by vSectionId,a.employeeCode,b.SectionName,vEmployeeType,vEmployeeName," +
					" c.designationName,dJoiningDate ";
*/
			if(queryValueCheck(query))
			{
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/account/hrmModule/RptJoiningDate.jasper",
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
			this.getParent().showNotification("Error "+exp,Notification.TYPE_ERROR_MESSAGE);
		}
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
		allComp.add(cmbSection);
		allComp.add(cmbCategory);
		allComp.add(cmbEmployeeName);
		allComp.add(cButton.btnPreview);

		new FocusMoveByEnter(this,allComp);
	}

	public AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);

		setWidth("420px");
		setHeight("220px");

		// lblSection
		lblSection = new Label("Division Name : ");
		lblSection.setImmediate(true);
		lblSection.setWidth("100px");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection, "top:20px; left:35px;");

		// cmbSection
		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("200px");
		cmbSection.setHeight("-1px");
		mainLayout.addComponent(cmbSection, "top:18.0px; left:140.0px;");

		// sectionAll
		sectionAll = new CheckBox("All");
		sectionAll.setImmediate(true);
		sectionAll.setWidth("-1px");
		sectionAll.setHeight("-1px");
		mainLayout.addComponent(sectionAll,"top:20px; left:345px;");

		// lblCategory
		lblCategory = new Label("Category : ");
		lblCategory.setImmediate(true);
		lblCategory.setWidth("100px");
		lblCategory.setHeight("-1px");
		mainLayout.addComponent(lblCategory, "top:50px; left:35px;");

		// cmbCategory
		cmbCategory = new ComboBox();
		cmbCategory.setImmediate(true);
		cmbCategory.setWidth("200px");
		cmbCategory.setHeight("-1px");
		mainLayout.addComponent(cmbCategory, "top:48.0px; left:140.0px;");
		for(int i=0; i<category.length; i++)
		{cmbCategory.addItem(category[i]);}

		// categoryAll
		categoryAll = new CheckBox("All");
		categoryAll.setImmediate(true);
		categoryAll.setWidth("-1px");
		categoryAll.setHeight("-1px");
		mainLayout.addComponent(categoryAll,"top:50px; left:345px;");

		// lblEmployeeName
		lblEmployeeName = new Label("Employee Name : ");
		lblEmployeeName.setImmediate(true);
		lblEmployeeName.setWidth("100px");
		lblEmployeeName.setHeight("-1px");
		mainLayout.addComponent(lblEmployeeName, "top:80px; left:35px;");

		// cmbEmployeeName
		cmbEmployeeName = new ComboBox();
		cmbEmployeeName.setImmediate(true);
		cmbEmployeeName.setWidth("200px");
		cmbEmployeeName.setHeight("-1px");
		mainLayout.addComponent(cmbEmployeeName, "top:78.0px; left:140.0px;");

		// employeeAll
		employeeAll = new CheckBox("All");
		employeeAll.setImmediate(true);
		employeeAll.setWidth("-1px");
		employeeAll.setHeight("-1px");
		mainLayout.addComponent(employeeAll,"top:80px; left:345px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:110.0px;left:140.0px;");

		mainLayout.addComponent(cButton, "top:140.0px;left:120.0px;");

		return mainLayout;
	}
}
