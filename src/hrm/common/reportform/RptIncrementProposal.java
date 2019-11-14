package hrm.common.reportform;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

public class RptIncrementProposal extends Window{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;
	private InlineDateField date;
	String querySection="";
	
	private Label lblUnit,lblEmployeeName;
	private ComboBox cmbUnit;
	private Label lblSection;
	private CheckBox chkSectionAll,chkEmployeeAll,chkDepartmentAll,chkUnitAll;
	private ComboBox cmbSection,cmbEmployeeName,cmbDepartment;
	private SimpleDateFormat dDateFormat=new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dFormat=new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dfY=new SimpleDateFormat("yyyy");
	SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy");
	
	ArrayList<Component> allComp = new ArrayList<Component>();
	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});
	private CommonButton cButton = new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	TextField txtPath=new TextField();
	TextField txtAddress=new TextField();
	private CommonMethod cm;
	private String menuId = "";
	public RptIncrementProposal(SessionBean sessionBean,String menuId){
		this.sessionBean=sessionBean;
		this.setCaption("INCREMENT PROPOSAL"+sessionBean.getCompany());
		buildMainLayout();
		this.setContent(mainLayout);
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		btnAction();
		cmbUnitData();
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
	private void btnAction(){
		date.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbUnit.removeAllItems();
				if(date.getValue() != null)
				{
					cmbEmployeeName.removeAllItems();
					cmbUnitData();
				}
				else{
					cmbEmployeeName.removeAllItems();
				}
			}
		});
		cmbUnit.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				
				if(cmbUnit.getValue()!=null)
				{
					addDepartmentName();
				}
			}
		});
		cmbDepartment.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				
				if(cmbDepartment.getValue()!=null)
				{
					addSectionName();
				}
			}
		});

		chkDepartmentAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				
				if(cmbUnit.getValue()!=null)
				{
					if(chkDepartmentAll.booleanValue())
					{
						cmbDepartment.setEnabled(false);
						cmbDepartment.setValue(null);
						addSectionName();
					}
					else
					{
						cmbDepartment.setEnabled(true);
					}
				}
				else
				{
					chkDepartmentAll.setValue(false);
				}
			}
		});
		cmbSection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				
				if(cmbSection.getValue()!=null)
				{
					cmbEmployeeDataAdd();
				}
			}
		});
		chkSectionAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				
				if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
				{
					if(chkSectionAll.booleanValue())
					{
						cmbSection.setEnabled(false);
						cmbSection.setValue(null);
						cmbEmployeeDataAdd();
					}
					else
					{
						cmbSection.setEnabled(true);
					}
				}
				else
				{
					chkSectionAll.setValue(false);
				}
			}
		});
		chkEmployeeAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				
				if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
				{
					if(chkEmployeeAll.booleanValue())
					{
						cmbEmployeeName.setEnabled(false);
						cmbEmployeeName.setValue(null);
					}
					else
					{
						cmbEmployeeName.setEnabled(true);
					}
				}
				else
				{
					chkEmployeeAll.setValue(false);
				}
			}
		});
		cButton.btnPreview.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				if(cmbUnit.getValue()!=null)
				{
					if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
					{
						if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
						{
							if(cmbEmployeeName.getValue()!=null || chkEmployeeAll.booleanValue())
							{
								
								reportShow();
							}
							else{
								getParent().showNotification("Warning","Select Employee or All!",Notification.TYPE_WARNING_MESSAGE);
							}
						}
						else{
							getParent().showNotification("Warning","Select Section!",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else{
						getParent().showNotification("Warning","Select Department!",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else{
					getParent().showNotification("Warning","Select Project!",Notification.TYPE_WARNING_MESSAGE);
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
	public void cmbUnitData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> list = session.createSQLQuery(" SELECT distinct vUnitId,vUnitName from " +
					"tbEmployeeBonus where YEAR(dBonusDate)=YEAR('"+dFormat.format(date.getValue())+"') " +
					"order by vUnitName").list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbUnit.addItem(element[0]);
				cmbUnit.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbUnitData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void addDepartmentName()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql=" SELECT distinct vDepartmentID,vDepartmentName from tbEmployeeBonus " +
					"where YEAR(dBonusDate)=YEAR('"+dFormat.format(date.getValue())+"') " +
					"and vUnitId like '"+(cmbUnit.getValue()==null?"%":cmbUnit.getValue())+"' " +
					"order by vDepartmentName";
			List <?> list = session.createSQLQuery(sql).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbDepartment.addItem(element[0]);
				cmbDepartment.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("addDepartmentName",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	public void addSectionName()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql=" SELECT distinct vSectionID,vSectionName from tbEmployeeBonus " +
					" where YEAR(dBonusDate)=YEAR('"+dFormat.format(date.getValue())+"') " +
					" and vUnitId like '"+(cmbUnit.getValue()!=null?cmbUnit.getValue().toString():"%")+"' " +
					" and vDepartmentId like '"+(chkDepartmentAll.booleanValue()?"%":cmbDepartment.getValue()==null?"%":cmbDepartment.getValue())+"' " +
					" order by vSectionName";
			List <?> list = session.createSQLQuery(sql).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbSection.addItem(element[0]);
				cmbSection.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("addSectionName",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void cmbEmployeeDataAdd()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		cmbEmployeeName.removeAllItems();
		try
		{
			String query="select distinct vEmployeeId,vEmployeeCode,vEmployeeName from tbEmpOfficialPersonalInfo " +
					"where vUnitId='"+cmbUnit.getValue()+"' " +
					" and vDepartmentId like '"+(chkDepartmentAll.booleanValue()?"%":cmbDepartment.getValue()==null?"%":cmbDepartment.getValue())+"' "+
					" and vSectionId like '"+(chkSectionAll.booleanValue()?"%":cmbSection.getValue()==null?"%":cmbSection.getValue())+"' ";
			List <?> lst=session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				for(Iterator <?> itr=lst.iterator();itr.hasNext();)
				{
					Object [] element = (Object[]) itr.next();
					cmbEmployeeName.addItem(element[0]);
					cmbEmployeeName.setItemCaption(element[0], element[1].toString()+"-"+element[2]);
				}
			}
		}
		catch (Exception exp)
		{
			showNotification("cmbEmployeeTypeDataAdd", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void reportShow()
	{
		String query="";

		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		try
		{
			query="select a.vEmployeeId,a.vEmployeeName,a.employeeCode,a.dDate,a.mTotalSalary,a.mNewTotalSalary,"
					+ "a.vNewDesignationName,a.vSectionId,a.vSectionName,a.mIncrementPercentage,"
					+ "a.mIncrementAmount,a.mNewGross,a.dJoiningDate,a.vEmployeeType,a.vServiceLength,"
					+ "a.mTotalSalary,a.vIncrementType,(a.mNewGross-a.mTotalSalary) incAmount,"
					+ "round((((a.mNewGross-a.mTotalSalary)/a.mNewGross)*100),1) incPer,"
					+ "a.vRemarks,a.vUserName,a.vUserIP,a.dEntryTime,a.vDesignationName,a.vDepartmentId,a.vDepartmentName "
					+ "from tbSalaryIncrement a "
					+ "inner join tbDesignationInfo b on a.vDesignationId=b.vDesignationId "
					+ "where a.vUnitId like '"+(cmbUnit.getValue()!=null?cmbUnit.getValue().toString():"%")+"' "
					+ "and  a.vDepartmentId like '"+(chkDepartmentAll.booleanValue()?"%":cmbDepartment.getValue()==null?"%":cmbDepartment.getValue())+"' "
					+ "and a.vSectionID like '"+(chkSectionAll.booleanValue()?"%":cmbSection.getValue()==null?"%":cmbSection.getValue())+"' " 
					+ "and a.vEmployeeId like '"+(chkEmployeeAll.booleanValue()?"%":cmbEmployeeName.getValue()==null?"%":cmbEmployeeName.getValue())+"' "
					+ "and YEAR(dDate)=YEAR('"+dfY.format(date.getValue())+"') "
					+ "order by a.vDepartmentName,a.vSectionName,b.iRank,a.dJoiningDate ";

			
			HashMap <String,Object> hm = new HashMap <String,Object> ();
			hm.put("company", sessionBean.getCompany());
			hm.put("address", txtAddress.getValue().toString());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("username", sessionBean.getUserName()+" "+sessionBean.getUserIp());
			hm.put("SysDate",reportTime.getTime);
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("sql", query);
			hm.put("empYear", dateFormat.format(date.getValue()));

			Window win = new ReportViewer(hm,"report/account/hrmModule/RptIncrementProposal.jasper",
					this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
					this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
					this.getWindow().getApplication().getURL()+"VAADIN/applet",RadioBtn.Radio);
	
			win.setCaption("Project Report");
			this.getParent().getWindow().addWindow(win);
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error "+exp,Notification.TYPE_ERROR_MESSAGE);
		}
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

	private void buildMainLayout()
	{
		
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("480px");
		setHeight("290px");

		date=new InlineDateField();
		date.setResolution(InlineDateField.RESOLUTION_YEAR);
		date.setWidth("120px");
		date.setImmediate(true);
		date.setDateFormat("yyyy");
		date.setValue(new java.util.Date());
		mainLayout.addComponent(new Label("Year :"),"top:30px; left:30px");
		mainLayout.addComponent(date,"top:28px; left:130px");

		// lblCategory
		lblUnit = new Label();
		lblUnit.setImmediate(false);
		lblUnit.setWidth("100.0%");
		lblUnit.setHeight("-1px");
		lblUnit.setValue("Project :");
		mainLayout.addComponent(lblUnit,"top:60.0px; left:30.0px;");

		// cmbSection
		cmbUnit = new ComboBox();
		cmbUnit.setImmediate(false);
		cmbUnit.setWidth("250px");
		cmbUnit.setHeight("-1px");
		cmbUnit.setNullSelectionAllowed(true);
		cmbUnit.setImmediate(true);
		mainLayout.addComponent(cmbUnit, "top:58.0px; left:130px;");
		
		chkUnitAll = new CheckBox("All");
		chkUnitAll.setImmediate(true);
		chkUnitAll.setWidth("-1px");
		chkUnitAll.setHeight("-1px");
		//mainLayout.addComponent(chkUnitAll, "top:60.0px;lef390px;");
		
		mainLayout.addComponent(new Label("Department :"),"top:90px; left:30.0px;");

		// cmbDepartment
		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(false);
		cmbDepartment.setWidth("250px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setNullSelectionAllowed(true);
		cmbDepartment.setImmediate(true);
		mainLayout.addComponent(cmbDepartment, "top:88px; left:130px;");
		
		chkDepartmentAll  = new CheckBox("All");
		chkDepartmentAll.setImmediate(true);
		chkDepartmentAll.setWidth("-1px");
		chkDepartmentAll.setHeight("-1px");
		mainLayout.addComponent(chkDepartmentAll, "top:90.0px;left:383px;");
		
		// lblCategory
		lblSection = new Label();
		lblSection.setImmediate(false);
		lblSection.setWidth("100.0%");
		lblSection.setHeight("-1px");
		lblSection.setValue("Section :");
		mainLayout.addComponent(lblSection,"top:120px; left:30.0px;");

		// cmbSection
		cmbSection = new ComboBox();
		cmbSection.setImmediate(false);
		cmbSection.setWidth("250px");
		cmbSection.setHeight("-1px");
		cmbSection.setNullSelectionAllowed(true);
		cmbSection.setImmediate(true);
		mainLayout.addComponent(cmbSection, "top:118px; left:130px;");
		
		chkSectionAll  = new CheckBox("All");
		chkSectionAll.setImmediate(true);
		chkSectionAll.setWidth("-1px");
		chkSectionAll.setHeight("-1px");
		mainLayout.addComponent(chkSectionAll, "top:120px;left:383px;");

		
		lblEmployeeName=new Label("Employee ID :");
		cmbEmployeeName = new ComboBox();
		cmbEmployeeName.setImmediate(true);
		cmbEmployeeName.setWidth("250px");
		cmbEmployeeName.setHeight("-1px");
		cmbEmployeeName.setNullSelectionAllowed(true);
		cmbEmployeeName.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(lblEmployeeName, "top:150px; left:30px;");
		mainLayout.addComponent(cmbEmployeeName,"top:148px; left:130px");
		
		chkEmployeeAll=new CheckBox("All");
		chkEmployeeAll.setImmediate(true);
		mainLayout.addComponent(chkEmployeeAll,"top:150px; left:383px");		
		
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:155px;left:150px;");
		RadioBtnGroup.setVisible(false);

		mainLayout.addComponent(cButton,"bottom:15px; left:130px");
		
	}
}
