package hrm.common.reportform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.ReportDate;
import com.common.share.ReportOption;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
//import com.sun.org.apache.bcel.internal.generic.NEW;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

public class RptEmployeeInformation extends Window
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private ComboBox cmbDynamic;
	private OptionGroup radioButtonGroup;

	private ComboBox cmbSection;

	private Label lblComboLabel;
	private CommonButton cButton= new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private static final List<String> groupButton=Arrays.asList(new String[]{"Employee ID","Finger ID","Proximity ID","Employee Name"});

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});
	private ReportDate reportTime = new ReportDate();

	boolean isPreview=false;


	public RptEmployeeInformation(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setCaption("EMPLOYEE INFORMATION :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setBtnAction();
		setContent(mainLayout);
		//initialCombo();
		cmbSectionDataLoad();
		cmbSection.focus();
	}

	private void setBtnAction()
	{
		cButton.btnPreview.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event)
			{
				if(cmbSection.getValue()!=null)
				{
					if(cmbDynamic.getValue()!=null)
					{
						reportView();
					}
					else
					{
						getParent().showNotification("Please Select "+lblComboLabel.getValue().toString()+"", Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					getParent().showNotification("Please Select Section", Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		cButton.btnExit.addListener(new ClickListener() 
		{	
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

		radioButtonGroup.addListener(new ValueChangeListener()
		{

	
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbSection.getValue()!=null)
				{
					cmbDynamic.removeAllItems();

					String sql="";
					if(radioButtonGroup.getValue()=="Employee ID")
					{
						cmbDynamic.removeAllItems();
						lblComboLabel.setValue("Employee ID");
						sql="select iFingerID,employeeCode from tbEmployeeInfo where vSectionId='"+cmbSection.getValue().toString()+"'  and iStatus='1'";
					}
					
					else if(radioButtonGroup.getValue()=="Proximity ID")
					{
						cmbDynamic.removeAllItems();
						lblComboLabel.setValue("Proximity ID");
						sql="select iFingerID,vProximityId from tbEmployeeInfo where vSectionId='"+cmbSection.getValue().toString()+"'  and iStatus='1'";
					}

					else if(radioButtonGroup.getValue()=="Finger ID")
					{
						cmbDynamic.removeAllItems();
						lblComboLabel.setValue("Finger ID");
						sql="select iFingerID,iFingerID from tbEmployeeInfo where vSectionId='"+cmbSection.getValue().toString()+"'  and iStatus='1'";
					}
					
					else if(radioButtonGroup.getValue()=="Employee Name")

					{
						cmbDynamic.removeAllItems();
						lblComboLabel.setValue("Employee Name");
						sql="select iFingerID,vEmployeeName from tbEmployeeInfo where vSectionId='"+cmbSection.getValue().toString()+"'  and iStatus='1'";
					}

					if(!sql.equals(""))
					{
						Transaction tx=null;
						try
						{

							Session session=SessionFactoryUtil.getInstance().getCurrentSession();
							tx=session.beginTransaction();
							List lst=session.createSQLQuery(sql).list();
							if(!lst.isEmpty())
							{

								Iterator itr=lst.iterator();
								while(itr.hasNext())
								{
									Object[] element=(Object[])itr.next();
									cmbDynamic.addItem(element[0]);
									cmbDynamic.setItemCaption(element[0], element[1].toString());
								}

							}
							else
								showNotification("Warning", "No Employee Found!!!", Notification.TYPE_WARNING_MESSAGE);

						}
						catch(Exception exp)
						{

							showNotification("OPGEmployee", exp.toString(), Notification.TYPE_ERROR_MESSAGE);

						}
					}

				}
			}
		});
	}

	/*private void selectCardNo()
	{
		cmbDynamic.removeAllItems();
		String query = "select iFingerID,employeeCode from tbEmployeeInfo where vSectionId='"+cmbSection.getValue().toString()+"'  and iStatus='1' order by iFingerID asc ";
		System.out.println(query);
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx;
		tx = session.beginTransaction();
		List list = session.createSQLQuery(query).list();		
		for(Iterator iter = list.iterator(); iter.hasNext();)
		{
			Object[] element = (Object[]) iter.next();
			cmbDynamic.addItem(element[0].toString());
			cmbDynamic.setItemCaption(element[0], element[1].toString());
		}
	}

	private void selectProximityId()
	{
		cmbDynamic.removeAllItems();
		String query ="select iFingerID,vProximityId from tbEmployeeInfo where vSectionId='"+cmbSection.getValue().toString()+"'  and iStatus='1' order by iFingerID asc";
		System.out.println(query);
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx;
		tx = session.beginTransaction();
		List list = session.createSQLQuery(query).list();		
		for(Iterator iter = list.iterator(); iter.hasNext();)
		{
			Object[] element = (Object[]) iter.next();
			cmbDynamic.addItem(element[0].toString());
			cmbDynamic.setItemCaption(element[0], element[1].toString());
		}
	}

	private void selectFingerID()
	{
		cmbDynamic.removeAllItems();
		String query ="select iFingerID,iFingerID from tbEmployeeInfo where vSectionId='"+cmbSection.getValue().toString()+"'  and iStatus='1' order by iFingerID asc";
		System.out.println(query);
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx;
		tx = session.beginTransaction();
		List list = session.createSQLQuery(query).list();		
		for(Iterator iter = list.iterator(); iter.hasNext();)
		{
			Object[] element = (Object[]) iter.next();
			cmbDynamic.addItem(element[0].toString());
			cmbDynamic.setItemCaption(element[0], element[1].toString());
		}
	}

	private void selectName()
	{
		cmbDynamic.removeAllItems();
		String query = "select iFingerID,vEmployeeName from tbEmployeeInfo where vSectionId='"+cmbSection.getValue().toString()+"'  and iStatus='1' order by iFingerID asc";;
		System.out.println(query);
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx;
		tx = session.beginTransaction();
		List list = session.createSQLQuery(query).list();		
		for(Iterator iter = list.iterator(); iter.hasNext();)
		{
			Object[] element = (Object[]) iter.next();
			cmbDynamic.addItem(element[0].toString());
			cmbDynamic.setItemCaption(element[0], element[1].toString());
		}
	}*/

	private AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("500px");
		setHeight("250px");

		cmbSection=new ComboBox();
		cmbSection.setWidth("290.0px");
		cmbSection.setHeight("-1px");
		cmbSection.setImmediate(true);
		mainLayout.addComponent(new Label("Division Name : "), "top:30.0px;left:30.0px;");
		mainLayout.addComponent(cmbSection, "top:28.0px;left:130.0px");

		// optionGroup
		radioButtonGroup = new OptionGroup("",groupButton);
		radioButtonGroup.setImmediate(true);
		radioButtonGroup.setStyleName("horizontal");
		radioButtonGroup.setValue("Card");
		mainLayout.addComponent(radioButtonGroup, "top:60.0px; left:50.0px;");

		//ComboLabel
		lblComboLabel=new Label("Employee :");
		lblComboLabel.setImmediate(true);
		lblComboLabel.setWidth("100px");
		mainLayout.addComponent(lblComboLabel, "top:90.0px; left:30.0px;");

		// comboBox_1
		cmbDynamic = new ComboBox();
		cmbDynamic.setImmediate(true);
		cmbDynamic.setWidth("195px");
		cmbDynamic.setHeight("-1px");
		mainLayout.addComponent(cmbDynamic, "top:88.0px; left:130.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:120.0px;left:130.0px;");

		cButton.btnPreview.setImmediate(true);
		mainLayout.addComponent(cButton, "top:150.0px; left:160.0px;");

		return mainLayout;
	}

	private void cmbSectionDataLoad()
	{
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="select AutoID,SectionName from tbSectionInfo";
			List lst=session.createSQLQuery(sql).list();
			if(!lst.isEmpty())
			{

				Iterator itr=lst.iterator();
				while(itr.hasNext())
				{
					Object[] element=(Object[])itr.next();
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

/*	private void initialCombo()
	{
		cmbDynamic.removeAllItems();
		String query = "select 0,vEmployeeId from tbEmployeeInfo order by iFingerID asc";
		System.out.println(query);
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx;
		tx = session.beginTransaction();
		List list = session.createSQLQuery(query).list();		
		for(Iterator iter = list.iterator(); iter.hasNext();)
		{
			Object[] element = (Object[]) iter.next();
			cmbDynamic.addItem(element[1].toString());
			cmbDynamic.setItemCaption(element[1], element[1].toString());
		}
	}*/

	private void reportView()
	{

		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		String query=null;
		if (cmbDynamic.getValue()!=null)
		{
			String	vEmployeeId= "";
			vEmployeeId= cmbDynamic.getValue().toString();
		}

		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			HashMap hm = new HashMap();
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("logo", sessionBean.getCompanyLogo());
			System.out.println(sessionBean.getCompanyContact());
			hm.put("UserName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			hm.put("SysDate",reportTime.getTime);

			query = " SELECT * from vw_rptEmployeeifo as a" +
					" left join tbEducation as b on a.vEmployeeId = b.vEmployeeId" +
					" Where a.iFingerID ='"+cmbDynamic.getValue().toString()+" '";
			System.out.println(query);
			hm.put("sql", query);

			Window win = new ReportViewer(hm,"report/account/hrmModule/RptEmployeeInfo.jasper",
					this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
					this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
					this.getWindow().getApplication().getURL()+"VAADIN/applet",RadioBtn.Radio);

			win.setCaption("Project Report");
			this.getParent().getWindow().addWindow(win);
		}

		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
			System.out.println(exp);
		}
	}
}
