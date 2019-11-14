package hrm.common.reportform;

import java.text.SimpleDateFormat;
import java.util.Arrays;
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
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class RptLeaveRegister extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblYear;
	private Label lblSectionName;

	private ComboBox cmbYear;
	private ComboBox cmbSectionName,cmbDepartment;
	private ComboBox cmbEmployeeName,cmbUnit;
	private CheckBox chkDepartmentAll,chkUnitAll,chkSectionAll;

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});
	SimpleDateFormat dfYear = new SimpleDateFormat("yyyy");
	SimpleDateFormat dformatSql = new SimpleDateFormat("yyyy-MM-dd");
	
	private CommonMethod cm;
	private String menuId = "";
	TextField txtPath=new TextField();
	public RptLeaveRegister(SessionBean sessionBean,String menuId) 
	{
		this.sessionBean=sessionBean;
		this.setCaption("LEAVE SUMMARY :: "+sessionBean.getCompany());
		this.setWidth("530px");
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		buildMainLayout();
		setContent(mainLayout);

		cmbAddYear();
		//cmbUnitDataLoad();

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

	private void cmbUnitDataLoad() {
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "select distinct b.vUnitId,b.vUnitName from tbEmpLeaveInfo a inner join tbEmpOfficialPersonalInfo b on a.vEmployeeId=b.vEmployeeId" +
					" where YEAR(vYear)=YEAR('"+cmbYear.getValue()+"') order by b.vUnitName ";
			System.out.println(sql);
			List<?> list = session.createSQLQuery(sql).list();
			cmbUnit.removeAllItems();
			for(Iterator<?> iter = list.iterator();iter.hasNext();)
			{
				Object element[] = (Object[]) iter.next();
				cmbUnit.addItem(element[0].toString());
				cmbUnit.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	
	private void cmbAddYear()
	{
		cmbYear.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String querySection = " SELECT distinct DATEPART(year,vYear ) as yearId,DATEPART(year, vYear) as balanceYear from tbEmpLeaveInfo " +
					"order by balanceYear";
			List <?> list = session.createSQLQuery(querySection).list();	
			for (Iterator <?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element =  (Object[]) iter.next();	
				cmbYear.addItem(element[0]);
				cmbYear.setItemCaption(element[0], element[1].toString());	
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbAddYear",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void cmbDepartmentDataAdd()
	{
		cmbDepartment.removeAllItems();
		
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String str = " SELECT distinct epo.vDepartmentId,epo.vDepartmentName from tbEmpLeaveInfo lbn inner join tbEmpOfficialPersonalInfo epo on epo.vEmployeeId=lbn.vEmployeeId "
					+ "where YEAR(vYear)='"+cmbYear.getValue().toString()+"' "
					+ "and epo.vUnitId='"+cmbUnit.getValue()+"' "
					+ "order by epo.vDepartmentName ";
			System.out.println("Section Add: "+str);
			
			List <?> list = session.createSQLQuery(str).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbDepartment.addItem(element[0]);
				cmbDepartment.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbDepartmentDataAdd",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void cmbSectionDataAdd()
	{
		cmbSectionName.removeAllItems();
		
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String str = " SELECT distinct epo.vSectionId,epo.vSectionName from tbEmpLeaveInfo lbn inner join tbEmpOfficialPersonalInfo epo on epo.vEmployeeId=lbn.vEmployeeId "
					+ "where YEAR(vYear)='"+cmbYear.getValue().toString()+"' "
					+ "and epo.vUnitId='"+cmbUnit.getValue()+"' and epo.vDepartmentId like '"+(chkDepartmentAll.booleanValue()==true?"%":(cmbDepartment.getValue()==null?"%":cmbDepartment.getValue()))+"' "
					+ "order by epo.vSectionName ";
			
			System.out.println("Section Add: "+str);
			
			List <?> list = session.createSQLQuery(str).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbSectionName.addItem(element[0]);
				cmbSectionName.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbSectionDataAdd",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}


	private void setEventAction()
	{
		cmbYear.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSectionName.removeAllItems();
				if(cmbYear.getValue()!=null)
				{
					cmbUnit.removeAllItems();
					cmbUnitDataLoad();
				}
			}
		});

		cmbUnit.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbYear.getValue()!=null)
				{
					if(cmbUnit.getValue()!=null)
					{
						cmbDepartment.removeAllItems();
						cmbDepartmentDataAdd();
					}
				}
			}
		});
		cmbDepartment.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
				if(cmbDepartment.getValue()!=null)
				{
					cmbSectionName.removeAllItems();
					cmbSectionDataAdd();
				}
			}
		});
		chkDepartmentAll.addListener(new ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				if(cmbUnit.getValue()!=null)
				{
					if(chkDepartmentAll.booleanValue())
					{
						cmbDepartment.setValue(null);
						cmbDepartment.setEnabled(false);
						cmbSectionName.removeAllItems();
						cmbSectionDataAdd();
					}
					else
					{
						cmbDepartment.setEnabled(true);
					}
				}
			}
		});
		chkSectionAll.addListener(new ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
				{
					if(chkSectionAll.booleanValue()){
						cmbSectionName.setValue(null);
						cmbSectionName.setEnabled(false);
					}
					else
					{
						cmbSectionName.setEnabled(true);
					}
				}
			}
		});
		cButton.btnPreview.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbUnit.getValue()!=null)
				{
					if(cmbYear.getValue()!=null)
					{
						if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
						{
							if(cmbSectionName.getValue()!=null || chkSectionAll.booleanValue())
							{
								reportpreview();
							}
							else
							{
								showNotification("Warning","Select Section Name",Notification.TYPE_WARNING_MESSAGE);
							}
						}
						else
						{
							showNotification("Warning","Select Department Name",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else
					{
						showNotification("Warning","Select Year",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					showNotification("Warning","Select Project",Notification.TYPE_WARNING_MESSAGE);
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

	private AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("500px");
		setHeight("250px");

		// lblYear
		lblYear = new Label("Year :");
		lblYear.setImmediate(false);
		lblYear.setWidth("100.0%");
		lblYear.setHeight("-1px");
		mainLayout.addComponent(lblYear,"top:10.0px; left:20.0px;");

		// cmbYear
		cmbYear = new ComboBox();
		cmbYear.setImmediate(false);
		cmbYear.setWidth("100px");
		cmbYear.setHeight("-1px");
		cmbYear.setNullSelectionAllowed(true);
		cmbYear.setImmediate(true);
		mainLayout.addComponent(cmbYear, "top:8.0px; left:130.0px;");

		cmbUnit=new ComboBox();
		cmbUnit.setWidth("250.0px");
		cmbUnit.setHeight("-1px");
		cmbUnit.setImmediate(true);
		mainLayout.addComponent(new Label("Project : "), "top:40.0px;left:20.0px;");
		mainLayout.addComponent(cmbUnit, "top:38.0px;left:130.0px");

		cmbDepartment = new ComboBox();
		cmbDepartment.setWidth("260px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setNullSelectionAllowed(true);
		cmbDepartment.setImmediate(true);
		mainLayout.addComponent(new Label("Department :"), "top:70px;left:20.0px;");
		mainLayout.addComponent(cmbDepartment, "top:68.0px; left:130.0px;");
		
		chkDepartmentAll=new CheckBox("All");
		chkDepartmentAll.setWidth("-1px");
		chkDepartmentAll.setHeight("-1px");
		chkDepartmentAll.setImmediate(true);
		mainLayout.addComponent(chkDepartmentAll,"top:70; left:393px");

		// lblSectionName
		lblSectionName = new Label("Section : ");
		lblSectionName.setImmediate(false);
		lblSectionName.setWidth("100.0%");
		lblSectionName.setHeight("-1px");
		mainLayout.addComponent(lblSectionName,"top:100px; left:20.0px;");

		// cmbSectionName
		cmbSectionName = new ComboBox();
		cmbSectionName.setWidth("260px");
		cmbSectionName.setHeight("-1px");
		cmbSectionName.setNullSelectionAllowed(true);
		cmbSectionName.setImmediate(true);
		mainLayout.addComponent(cmbSectionName, "top:98px; left:130.0px;");
		
		chkSectionAll=new CheckBox("All");
		chkSectionAll.setWidth("-1px");
		chkSectionAll.setHeight("-1px");
		chkSectionAll.setImmediate(true);
		mainLayout.addComponent(chkSectionAll,"top:100; left:393px");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:100.0px;left:130.0px;");
		RadioBtnGroup.setVisible(false);
		mainLayout.addComponent(cButton,"bottom:15px; left:125.0px");
		return mainLayout;
	}

	private void reportpreview()
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		try
		{
/*			String query=" select fun.*,sec.vSectionName from funLeaveBalanceDetails ('"+cmbSectionName.getValue().toString()+"'," +
					"'%','') fun inner join tbSectionInfo sec on sec.vSectionId=fun.vSectionId "
					+ "where vUnitId='"+cmbUnit.getValue().toString()+"'";*/

			
			String query="select * from funLeaveBalanceDetails('"+(chkDepartmentAll.booleanValue()==true?"%":(cmbDepartment.getValue()==null?"%":cmbDepartment.getValue()))+"','%','"+cmbYear.getValue()+"')  order by vDepartmentName ";

			
			
			System.out.println("Report Leave Register: "+query);
			
			
			if(queryValueCheck(query))
			{
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
				hm.put("year", cmbYear.getValue().toString());
				hm.put("SysDate",reportTime.getTime);
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("sql", query);
				

				Window win = new ReportViewer(hm,"report/account/hrmModule/RptLeaveRegister.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",true);

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
			showNotification("Error",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
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
}
