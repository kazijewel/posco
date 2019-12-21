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
//import com.sun.org.apache.bcel.internal.generic.NEW;
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

public class RptEmployeeListWithGross extends Window 
{
	private SessionBean sessionBean;
	public AbsoluteLayout mainLayout;

	private ComboBox cmbCategory;	
	private ComboBox cmbSection;
	private ComboBox cmbDesignation;
	private ComboBox cmbReligion;

	private Label lblCategory;
	private Label lblSection;
	private Label lblDesignation;
	private Label lblReligion;

	private OptionGroup ogaictive = new OptionGroup();
	private static final List<String> aictiveType = Arrays.asList(new String[]{"Active","Inactive","All"});
	private String stAIctive = "";

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});

	private CheckBox categoryAll = new CheckBox("All");
	private CheckBox sectionAll = new CheckBox("All");
	private CheckBox designationAll = new CheckBox("All");
	private CheckBox ReligionAll = new CheckBox("All");

	ArrayList<Component> allComp = new ArrayList<Component>();

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	private static final String[] category = new String[] {"Permanent", "Temporary", "Provisionary", "Casual"};
	private static final String[] religion = new String[] {"Islam","Hindu","Buddism","Cristian","Other"};

	public RptEmployeeListWithGross(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setCaption("EMPLOYEE LIST :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);

		cmbSectionAddData();
		cmbDesignationaddData();
		setEventAction();
		focusMove();
	}

	public void cmbSectionAddData()
	{
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery(" SELECT AutoID,SectionName from tbSectionInfo order by AutoID ").list();	

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

	public void cmbDesignationaddData()
	{
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery(" select designationId,designationName from tbDesignationInfo order by autoId ").list();	

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbDesignation.addItem(element[0]);
				cmbDesignation.setItemCaption(element[0], (String) element[1]);
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
				formValidation();
			}
		});

		cButton.btnExit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

		ogaictive.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event){
				System.out.println("event.getProperty().toString() :"+event.getProperty().toString());
				if(event.getProperty().toString()=="Active"){

					stAIctive = "1";
					System.out.println("value >>:"+stAIctive);
				}
				else if(event.getProperty().toString()=="Inactive"){
					stAIctive = "0";
					System.out.println("value >> :"+stAIctive);
				}
				else{
					stAIctive = "%";
					System.out.println("value >> :"+stAIctive);
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

		sectionAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(sectionAll.booleanValue()==true)
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

		designationAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(designationAll.booleanValue()==true)
				{
					cmbDesignation.setValue(null);
					cmbDesignation.setEnabled(false);
				}
				else
				{
					cmbDesignation.setEnabled(true);
				}
			}
		});

		ReligionAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(ReligionAll.booleanValue()==true)
				{
					cmbReligion.setValue(null);
					cmbReligion.setEnabled(false);
				}
				else
				{
					cmbReligion.setEnabled(true);
				}
			}
		});
	}

	private void formValidation()
	{
		if(cmbCategory.getValue()!=null || categoryAll.booleanValue()==true)
		{
			if(cmbSection.getValue()!=null || sectionAll.booleanValue()==true)
			{
				if(cmbDesignation.getValue()!=null || designationAll.booleanValue()==true)
				{
					if(cmbReligion.getValue()!=null || ReligionAll.booleanValue()==true)
					{
						if(!stAIctive.equals(""))
						{
							getAllData();
						}
						else
						{
							showNotification("Warning","Select Activity",Notification.TYPE_WARNING_MESSAGE);	
						}
					}else{
						showNotification("Warning","Select Religion",Notification.TYPE_WARNING_MESSAGE);
					}
				}else{
					showNotification("Warning","Select Designation",Notification.TYPE_WARNING_MESSAGE);
				}
			}else{
				showNotification("Warning","Select Section",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else
		{
			showNotification("Warning","Select Category",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void getAllData()
	{
		String categoryValue = "";
		String sectionValue = "";
		String designationValue = "";
		String religionValue = "";
		String typeValue = "";

		if(categoryAll.booleanValue()==true){
			categoryValue = "%";}
		else{
			categoryValue = cmbCategory.getItemCaption(cmbCategory.getValue().toString());}

		if(sectionAll.booleanValue()==true){
			sectionValue = "%";}
		else{
			sectionValue = cmbSection.getValue().toString();}

		if(designationAll.booleanValue()==true){
			designationValue = "%";}
		else{
			designationValue = cmbDesignation.getValue().toString();}

		if(ReligionAll.booleanValue()==true){
			religionValue = "%";}
		else{
			religionValue = cmbReligion.getItemCaption(cmbReligion.getValue().toString());}

		if(ogaictive.getValue().toString().equals("Active")){
			typeValue = "1";}

		else if(ogaictive.getValue().toString().equals("Inactive")){
			typeValue = "0";}

		else if(ogaictive.getValue().toString().equals("All")){
			typeValue = "%";}

		reportShow(categoryValue,sectionValue,designationValue,religionValue,typeValue);
	}

	private void reportShow(Object categoryValue,Object departmentValue,Object designationValue,Object religionvalue,Object activeValue)
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		String query=null;
		String activeFlag = null;
		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			HashMap hm = new HashMap();
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone",sessionBean.getCompanyContact());
			hm.put("username", sessionBean.getUserName()+" "+sessionBean.getUserIp());
			//			hm.put("userIp", sessionBean.getUserIp());
			hm.put("SysDate",reportTime.getTime);
			hm.put("logo", sessionBean.getCompanyLogo());

			/*query=" SELECT vEmployeeType,b.SectionName,employeeCode,iFingerID,vProximityId,vEmployeeName,c.designationName," +
					" vReligion,iStatus from tbEmployeeInfo as a inner join tbSectionInfo as b on a.vSectionId=b.AutoID inner" +
					" join tbDesignationInfo as c on a.vDesignationId=c.designationId where vEmployeeType like '"+categoryValue+"' and" +
					" vSectionId like '"+departmentValue+"' and vDesignationId like '"+designationValue+"' and" +
					" vReligion like '"+religionvalue+"' and iStatus like '"+activeValue+"' Group by " +
					" vEmployeeType,b.SectionName,employeeCode,iFingerID,vProximityId,vEmployeeName," +
					" c.designationName,vReligion,iStatus order by b.SectionName";*/
			
			 query="SELECT vEmployeeType,b.SectionName,employeeCode,iFingerID,vProximityId,vEmployeeName,c.designationName,vReligion,"
				  +"iStatus,a.dJoiningDate from tbEmployeeInfo as a inner join tbSectionInfo as b on a.vSectionId=b.AutoID inner join "
				  +"tbDesignationInfo as c on a.vDesignationId=c.designationId where a.vEmployeeType like '"+categoryValue+"' and b.AutoID like '"+departmentValue+"' "
				  +"and c.designationId like '"+designationValue+"' and a.vReligion like '"+religionvalue+"' and a.iStatus like '"+activeValue+"'"
				  +"order by b.SectionName,a.vEmployeeType,a.iStatus";
			
			System.out.println("Report Query:"+query);
			if(queryValueCheck(query))
			{
				hm.put("sql", query);

				System.out.println(RadioBtn.Radio+"");
				
				Window win = new ReportViewer(hm,"report/account/hrmModule/rptEmployeeList.jasper",
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
		allComp.add(cmbCategory);
		allComp.add(cmbSection);
		allComp.add(cmbDesignation);

		allComp.add(cmbReligion);
		allComp.add(cButton.btnPreview);

		new FocusMoveByEnter(this,allComp);
	}

	public AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);

		setWidth("400px");
		setHeight("280px");

		//lblCategory
		lblCategory = new Label("Category : ");
		lblCategory.setImmediate(true);
		lblCategory.setWidth("100px");
		lblCategory.setHeight("-1px");
		mainLayout.addComponent(lblCategory, "top:20px; left:40px;");

		// cmbCategory
		cmbCategory = new ComboBox();
		cmbCategory.setImmediate(true);
		cmbCategory.setWidth("200px");
		cmbCategory.setHeight("-1px");
		mainLayout.addComponent(cmbCategory, "top:18.0px; left:120.0px;");
		for(int i=0; i<category.length; i++)
		{cmbCategory.addItem(category[i]);}

		// categoryAll 
		categoryAll = new CheckBox("All");
		categoryAll.setImmediate(true);
		categoryAll.setWidth("-1px");
		categoryAll.setHeight("-1px");
		mainLayout.addComponent(categoryAll,"top:20px; left:325px;");

		// lblSection
		lblSection = new Label("Division : ");
		lblSection.setImmediate(true);
		lblSection.setWidth("100px");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection, "top:50px; left:40px;");

		// cmbSection
		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("200px");
		cmbSection.setHeight("-1px");
		mainLayout.addComponent(cmbSection, "top:48.0px; left:120.0px;");

		// sectionAll 
		sectionAll = new CheckBox("All");
		sectionAll.setImmediate(true);
		sectionAll.setWidth("-1px");
		sectionAll.setHeight("-1px");
		mainLayout.addComponent(sectionAll,"top:50px; left:325px;");

		// lblDesignation
		lblDesignation = new Label("Designation : ");
		lblDesignation.setImmediate(true);
		lblDesignation.setWidth("100px");
		lblDesignation.setHeight("-1px");
		mainLayout.addComponent(lblDesignation, "top:80px; left:40px;");

		// cmbDesignation
		cmbDesignation = new ComboBox();
		cmbDesignation.setImmediate(true);
		cmbDesignation.setWidth("200px");
		cmbDesignation.setHeight("-1px");
		mainLayout.addComponent(cmbDesignation, "top:78.0px; left:120.0px;");

		// designationAll 
		designationAll = new CheckBox("All");
		designationAll.setImmediate(true);
		designationAll.setWidth("-1px");
		designationAll.setHeight("-1px");
		mainLayout.addComponent(designationAll,"top:80px; left:325px;");

		// lblReligion
		lblReligion = new Label("Religion : ");
		lblReligion.setImmediate(true);
		lblReligion.setWidth("100px");
		lblReligion.setHeight("-1px");
		mainLayout.addComponent(lblReligion, "top:110px; left:40px;");

		// cmbReligion
		cmbReligion = new ComboBox();
		cmbReligion.setImmediate(true);
		cmbReligion.setWidth("200px");
		cmbReligion.setHeight("-1px");
		mainLayout.addComponent(cmbReligion, "top:108.0px; left:120.0px;");
		for(int i=0; i<religion.length; i++)
		{cmbReligion.addItem(religion[i]);}

		// ReligionAll 
		ReligionAll = new CheckBox("All");
		ReligionAll.setImmediate(true);
		ReligionAll.setWidth("-1px");
		ReligionAll.setHeight("-1px");
		mainLayout.addComponent(ReligionAll,"top:110px; left:325px;");

		// ogaictive
		ogaictive = new OptionGroup("",aictiveType);
		ogaictive.setImmediate(true);
		ogaictive.setWidth("250px");
		ogaictive.setHeight("-1px");
		ogaictive.setStyleName("horizontal");
		mainLayout.addComponent(ogaictive, "top:140px; left:120px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:170.0px;left:150.0px;");

		mainLayout.addComponent(cButton, "top:200.0px;left:120.0px;");

		return mainLayout;
	}
}
