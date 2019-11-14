package hrm.common.reportform;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

import com.common.share.CommonButton;
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
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

@SuppressWarnings("serial")
public class RptServiceOfLength extends Window
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblSection;
	private Label lblCategory;
	private Label lblEmployeeName;
	private Label lblJobLength;
	private Label lblAsOnDate;

	private ComboBox cmbSection;
	private ComboBox cmbCategory;
	private ComboBox cmbEmployeeName;
	private ComboBox cmbLengthOfMeasure;

	private CheckBox sectionAll;
	private CheckBox categoryAll;
	private CheckBox employeeAll;
	private CheckBox lengthAsOnDateAll;

	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	private PopupDateField dAsOnDate;

	ArrayList<Component> allComp = new ArrayList<Component>();

	private static final String[] category = new String[] {"Permanent", "Temporary", "Provisionary", "Casual"};
	private static final String[] length = new String[] {"Confirmation Date","Joining Date"};

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");

	private ReportDate reportTime;

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1 = Arrays.asList(new String[]{"PDF","Other"});

	public RptServiceOfLength(SessionBean sessionBean)

	{
		this.sessionBean=sessionBean;
		this.setCaption("Length of Service :: "+sessionBean.getCompany());
		this.setResizable(false);;

		buildMainLayout();
		setContent(mainLayout);
		cmbSectionData();
		setEventAction();
	}

	private void cmbSectionData()
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try
		{
			String sql = "select distinct vSectionId,vSectionName from [dbo].[funEmployeeDetails]('%')";
			List<?> lst = session.createSQLQuery(sql).list();
			if(!lst.isEmpty())
			{
				Iterator<?> itr = lst.iterator();
				while(itr.hasNext())
				{
					Object[] element = (Object[])itr.next();
					cmbSection.addItem(element[0]);
					cmbSection.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("CmbSectionDataLoad", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public void cmbEmployeeNameData(String Section)
	{
		cmbEmployeeName.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try
		{
			List<?> list = session.createSQLQuery(" select vEmployeeId,vEmployeeName from tbEmployeeInfo where vSectionId like '"+Section+"' and iStatus='1' ").list();	
			for(Iterator<?> iter = list.iterator();iter.hasNext();)
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

		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbSection.getValue()!=null || sectionAll.booleanValue()==true){
					if(cmbCategory.getValue()!=null || categoryAll.booleanValue()==true){
						if(cmbEmployeeName.getValue()!=null || employeeAll.booleanValue()==true){
							if(cmbLengthOfMeasure.getValue()!=null){
								if(dAsOnDate.getValue()!=null){
									getAllData();
								}else{
									showNotification("Warning","Select Date",Notification.TYPE_WARNING_MESSAGE);
								}
							}else{
								showNotification("Warning","Select Measurement",Notification.TYPE_WARNING_MESSAGE);
							}
						}else{
							showNotification("Warning","Select Employee Name",Notification.TYPE_WARNING_MESSAGE);
						}
					}else{
						showNotification("Warning","Select Category",Notification.TYPE_WARNING_MESSAGE);
					}
				}else{
					showNotification("Warning","Select Section",Notification.TYPE_WARNING_MESSAGE);
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
					cmbSection.setValue(null);
					cmbSection.setEnabled(true);
					cmbEmployeeName.removeAllItems();
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

		Object asOnDate=dateFormat.format(dAsOnDate.getValue());

		reportShow(sectionValue,categoryValue,employeeValue,asOnDate);
	}

	private void reportShow(Object sectionValue,Object categoryValue,Object employeeValue,Object asOnDate)
	{
		reportTime = new ReportDate();
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		String query = "";
		try
		{
			HashMap<Object, Object> hm = new HashMap<Object, Object>();
			hm.put("section", cmbSection.getItemCaption(cmbSection.getValue()));
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("lengthMeasure", cmbLengthOfMeasure.getItemCaption(cmbLengthOfMeasure.getValue().toString()));
			hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			hm.put("SysDate", reportTime.getTime);
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("date", asOnDate);

			if(cmbLengthOfMeasure.getValue().toString().equals("Confirmation Date"))
			{
				query = " SELECT b.SectionName+'-'+vEmployeeType as SectionName,b.SectionName as Section," +
						" vEmployeeType,vEmployeeName,c.designationName,dJoiningDate,dConfirmationDate," +
						" ((DATEDIFF(DAY,dConfirmationDate,'"+asOnDate+"'))/365) as year," +
						" (((DATEDIFF(DAY,dConfirmationDate,'"+asOnDate+"'))%365)/30) as month," +
						" ((((DATEDIFF(DAY,dConfirmationDate,'"+asOnDate+"'))%365)%30)) as day from tbEmployeeInfo" +
						" as a inner join tbSectionInfo as b on a.vSectionId=b.AutoID inner join tbDesignationInfo as" +
						" c on a.vDesignationId=c.designationId where vSectionId like '"+sectionValue+"' and " +
						" vEmployeeType like '"+categoryValue+"' and vEmployeeId like '"+employeeValue+"' and iStatus='1'" +
						" and dJoiningDate<='"+asOnDate+"' order by vSectionId,a.employeeCode ";
			}
			else
			{
				query = " SELECT b.SectionName+'-'+vEmployeeType as SectionName,b.SectionName as Section," +
						" vEmployeeType,vEmployeeName,c.designationName,dJoiningDate,dConfirmationDate," +
						" ((DATEDIFF(DAY,dJoiningDate,'"+asOnDate+"'))/365) as year," +
						" (((DATEDIFF(DAY,dJoiningDate,'"+asOnDate+"'))%365)/30) as month," +
						" ((((DATEDIFF(DAY,dJoiningDate,'"+asOnDate+"'))%365)%30)) as day from tbEmployeeInfo" +
						" as a inner join tbSectionInfo as b on a.vSectionId=b.AutoID inner join tbDesignationInfo as" +
						" c on a.vDesignationId=c.designationId where vSectionId like '"+sectionValue+"' and " +
						" vEmployeeType like '"+categoryValue+"' and vEmployeeId like '"+employeeValue+"'  and iStatus='1' " +
						" and dJoiningDate<='"+asOnDate+"' order by vSectionId,a.employeeCode ";
			}
			if(queryValueCheck(query))
			{
				hm.put("sql", query);
				Window win = new ReportViewer(hm,"report/account/hrmModule/RptService_lenghth.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",RadioBtn.Radio);
				win.setStyleName("cwindow");
				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);
			}
			else
			{
				showNotification("Warning","There are no Data",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception exp){this.getParent().showNotification("Error "+exp,Notification.TYPE_ERROR_MESSAGE);}
	}

	private boolean queryValueCheck(String sql)
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try
		{
			Iterator<?> iter = session.createSQLQuery(sql).list().iterator();
			if(iter.hasNext())
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

	public AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);

		setWidth("430px");
		setHeight("280px");

		// lblSection
		lblSection = new Label("Section Name : ");
		lblSection.setImmediate(true);
		lblSection.setWidth("100px");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection, "top:20px; left:25px;");

		// cmbSection
		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("200px");
		cmbSection.setHeight("-1px");
		mainLayout.addComponent(cmbSection, "top:18.0px; left:160.0px;");

		// sectionAll
		sectionAll = new CheckBox("All");
		sectionAll.setImmediate(true);
		sectionAll.setWidth("-1px");
		sectionAll.setHeight("-1px");
		//		mainLayout.addComponent(sectionAll,"top:20px; left:365px;");

		// lblCategory
		lblCategory = new Label("Category : ");
		lblCategory.setImmediate(true);
		lblCategory.setWidth("100px");
		lblCategory.setHeight("-1px");
		mainLayout.addComponent(lblCategory, "top:50px; left:25px;");

		// cmbCategory
		cmbCategory = new ComboBox();
		cmbCategory.setImmediate(true);
		cmbCategory.setWidth("200px");
		cmbCategory.setHeight("-1px");
		mainLayout.addComponent(cmbCategory, "top:48.0px; left:160.0px;");
		for(int i=0; i<category.length; i++)
		{cmbCategory.addItem(category[i]);}

		// categoryAll
		categoryAll = new CheckBox("All");
		categoryAll.setImmediate(true);
		categoryAll.setWidth("-1px");
		categoryAll.setHeight("-1px");
		mainLayout.addComponent(categoryAll,"top:50px; left:365px;");

		// lblEmployeeName
		lblEmployeeName = new Label("Employee Name : ");
		lblEmployeeName.setImmediate(true);
		lblEmployeeName.setWidth("100px");
		lblEmployeeName.setHeight("-1px");
		mainLayout.addComponent(lblEmployeeName, "top:80px; left:25px;");

		// cmbEmployeeName
		cmbEmployeeName = new ComboBox();
		cmbEmployeeName.setImmediate(true);
		cmbEmployeeName.setWidth("200px");
		cmbEmployeeName.setHeight("-1px");
		mainLayout.addComponent(cmbEmployeeName, "top:78.0px; left:160.0px;");

		// employeeAll
		employeeAll = new CheckBox("All");
		employeeAll.setImmediate(true);
		employeeAll.setWidth("-1px");
		employeeAll.setHeight("-1px");
		mainLayout.addComponent(employeeAll,"top:80px; left:365px;");

		// lblJobLength
		lblJobLength = new Label("Length Measure From : ");
		lblJobLength.setImmediate(true);
		lblJobLength.setWidth("150px");
		lblJobLength.setHeight("-1px");
		mainLayout.addComponent(lblJobLength, "top:110px; left:25px;");

		// cmbLengthOfMeasure
		cmbLengthOfMeasure = new ComboBox();
		cmbLengthOfMeasure.setImmediate(true);
		cmbLengthOfMeasure.setWidth("200px");
		cmbLengthOfMeasure.setHeight("-1px");
		mainLayout.addComponent(cmbLengthOfMeasure, "top:108.0px; left:160.0px;");
		for(int i=0; i<length.length; i++)
		{cmbLengthOfMeasure.addItem(length[i]);}

		// lengthAsOnDateAll
		lengthAsOnDateAll = new CheckBox("All");
		lengthAsOnDateAll.setImmediate(true);
		lengthAsOnDateAll.setWidth("-1px");
		lengthAsOnDateAll.setHeight("-1px");
		//		mainLayout.addComponent(lengthAsOnDateAll,"top:110px; left:365px;");

		// lblAsOnDate
		lblAsOnDate = new Label("As on Date : ");
		lblAsOnDate.setImmediate(true);
		lblAsOnDate.setWidth("100px");
		lblAsOnDate.setHeight("-1px");
		mainLayout.addComponent(lblAsOnDate, "top:140px; left:25px;");

		// cmbLengthOfMeasure
		dAsOnDate = new PopupDateField();
		dAsOnDate.setImmediate(true);
		dAsOnDate.setWidth("110px");
		dAsOnDate.setHeight("-1px");
		dAsOnDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dAsOnDate.setDateFormat("dd-MM-yyyy");
		dAsOnDate.setValue(new java.util.Date());
		mainLayout.addComponent(dAsOnDate, "top:138.0px; left:160.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:170.0px;left:160.0px;");

		mainLayout.addComponent(cButton, "top:200.0px;left:130.0px;");

		return mainLayout;
	}
}