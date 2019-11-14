package hrm.common.reportform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

import com.common.share.CommonButton;
import com.common.share.CommonMethod;
import com.common.share.FocusMoveByEnter;
import com.common.share.ReportDate;
import com.common.share.ReportOption;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class RptSalaryStructure extends Window
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblSection;
	private ComboBox cmbSection;
	private CheckBox chkAllUnit;
	private ComboBox cmbDepartment,cmbUnit;
	private CheckBox chkDepartmentAll;
	private CheckBox chkSectionAll;
	private Label lblEmployeeType;
	private ComboBox cmbEmployeeType;
	private CheckBox chkEmployeeTypeAll;
	ArrayList<Component> allComp = new ArrayList<Component>();

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");

	private static final String[] category = new String[] { "Permanent", "Temporary", "Provisionary", "Casual"};
	private ReportDate reportTime = new ReportDate();

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});
	private CommonMethod cm;
	private String menuId = "";
	public RptSalaryStructure(SessionBean sessionBean,String menuId)
	{
		this.sessionBean=sessionBean;
		this.setCaption("SALARY STRUCTURE :: "+sessionBean.getCompany());
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		buildMainLayout();
		setContent(mainLayout);

		cmbUnitAddData();
		setEventAction();

		focusMove();
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

	public void cmbUnitAddData()
	{
		cmbUnit.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct  vUnitId,vUnitName from tbEmpOfficialPersonalInfo "+
					" where bStatus=1 order by vUnitName";
			List <?> list=session.createSQLQuery(query).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbUnit.addItem(element[0]);
				cmbUnit.setItemCaption(element[0], (String) element[1]);
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbUnitAddData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	public void cmbDepartmentData()
	{
		cmbDepartment.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String branchId="%";
			if(cmbUnit.getValue()!=null)
			{
				branchId=cmbUnit.getValue().toString();
			}
			String query="select distinct  vDepartmentId,vDepartmentName from tbEmpOfficialPersonalInfo "+
					" where bStatus=1 and vUnitId like '"+branchId+"' order by vDepartmentName"; 
			List <?> list=session.createSQLQuery(query).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbDepartment.addItem(element[0]);
				cmbDepartment.setItemCaption(element[0], (String) element[1]);
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbUnitAddData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}


	public void cmbSectionAddData()
	{
		cmbSection.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String deptId="%",branchId="%";
			if(cmbUnit.getValue()!=null)
			{
				branchId=cmbUnit.getValue().toString();
			}
			if(cmbDepartment.getValue()!=null)
			{
				deptId=cmbDepartment.getValue().toString();
			}
			String query="select distinct  vSectionId,vSectionName from tbEmpOfficialPersonalInfo "+
					" where bStatus=1 and vUnitId like '"+branchId+"' and vDepartmentId like '"+deptId+"' order by vSectionName"; 
			List <?> list=session.createSQLQuery(query).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbSection.addItem(element[0]);
				cmbSection.setItemCaption(element[0], (String) element[1]);
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("cmbSectionAddData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	public void cmbEmployeeTypeDataLoad()
	{
		cmbEmployeeType.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String deptId="%",secId="%",branchId="%";
			if(cmbUnit.getValue()!=null)
			{
				branchId=cmbUnit.getValue().toString();
			}
			if(cmbDepartment.getValue()!=null)
			{
				deptId=cmbDepartment.getValue().toString();
			}
			if(cmbSection.getValue()!=null)
			{
				secId=cmbSection.getValue().toString();
			}
			String query="select distinct  0,vEmployeeType from tbEmpOfficialPersonalInfo "+
			"where bStatus=1 and vUnitId like '"+branchId+"' and vDepartmentId like '"+deptId+"' and vSectionId like '"+secId+"' ";
			List <?> list=session.createSQLQuery(query).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbEmployeeType.addItem(element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("cmbEmployeeTypeAddData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void setEventAction()
	{
		cmbUnit.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbDepartment.removeAllItems();
				chkDepartmentAll.setValue(false);
				cmbDepartment.setEnabled(false);
				if(cmbUnit.getValue()!=null)
				{
					chkAllUnit.setValue(false);
					cmbDepartment.setEnabled(true);
					cmbDepartmentData();
				}
			}
		});

		chkAllUnit.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				cmbDepartment.removeAllItems();
				chkDepartmentAll.setValue(false);
				cmbDepartment.setEnabled(false);
				if(chkAllUnit.booleanValue())
				{
					cmbUnit.setValue(null);
					cmbUnit.setEnabled(false);
					cmbDepartment.setEnabled(true);
					cmbDepartmentData();
				}
				else
				{
					cmbUnit.setEnabled(true);
					cmbDepartment.setEnabled(false);
				}
			

			}
		});
		
		cmbDepartment.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSection.removeAllItems();
				chkSectionAll.setValue(false);
				cmbSection.setEnabled(false);
				if(cmbUnit.getValue()!=null || chkAllUnit.booleanValue())
				{
					if(cmbDepartment.getValue()!=null)
					{
						chkDepartmentAll.setValue(false);
						cmbSectionAddData();
						cmbSection.setEnabled(true);
					}
					else
					{
						cmbSection.setEnabled(false);
					}
				}
			}
		});

		chkDepartmentAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				cmbSection.removeAllItems();
				chkSectionAll.setValue(false);
				cmbSection.setEnabled(false);
				if(cmbUnit.getValue()!=null || chkAllUnit.booleanValue())
				{
					if(chkDepartmentAll.booleanValue())
					{
						cmbDepartment.setValue(null);
						cmbDepartment.setEnabled(false);
						cmbSection.setEnabled(true);
						cmbSectionAddData();
					}
					else
					{
						cmbDepartment.setEnabled(true);
						cmbSection.setEnabled(false);
					}
				}
				else{
					chkDepartmentAll.setValue(false);
					showNotification("Warning..","Select Project Please!",Notification.TYPE_HUMANIZED_MESSAGE);
				}
			}
		});

		cmbSection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmployeeType.removeAllItems();
				chkEmployeeTypeAll.setValue(false);
				cmbEmployeeType.setEnabled(false);
				if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
				{
					if(cmbSection.getValue()!=null)
					{
						chkSectionAll.setValue(false);
						cmbEmployeeTypeDataLoad();
						cmbEmployeeType.setEnabled(true);
					}
					else
					{
						cmbEmployeeType.setEnabled(false);
					}
				}
			}
		});

		chkSectionAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				cmbEmployeeType.removeAllItems();
				chkEmployeeTypeAll.setValue(false);
				cmbEmployeeType.setEnabled(false);
				if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
				{
					if(chkSectionAll.booleanValue())
					{
						cmbSection.setValue(null);
						cmbSection.setEnabled(false);
						cmbEmployeeType.setEnabled(true);
						cmbEmployeeTypeDataLoad();
					}
					else
					{
						cmbSection.setEnabled(true);
						cmbEmployeeType.setEnabled(false);
					}
				}
				else{
					chkSectionAll.setValue(false);
					showNotification("Warning..","Select Department Please!",Notification.TYPE_HUMANIZED_MESSAGE);
				}
			}
		});
		chkEmployeeTypeAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
				{
					if(chkEmployeeTypeAll.booleanValue())
					{
						cmbEmployeeType.setValue(null);
						cmbEmployeeType.setEnabled(false);
					}
					else
					{
						cmbEmployeeType.setEnabled(true);
					}
				}
				else{
					chkEmployeeTypeAll.setValue(false);
					showNotification("Warning..","Select Section Please!",Notification.TYPE_HUMANIZED_MESSAGE);
				}
			}
		});

		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbUnit.getValue()!=null || chkAllUnit.booleanValue())
				{
					if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
					{
						if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
						{
							if(cmbEmployeeType.getValue()!=null || chkEmployeeTypeAll.booleanValue())
							{
								reportShow();
							}
							else
							{
								showNotification("Warning","Select Employee",Notification.TYPE_WARNING_MESSAGE);
								cmbEmployeeType.focus();
							}
						}
						else
						{
							showNotification("Warning","Select Section",Notification.TYPE_WARNING_MESSAGE);
							cmbSection.focus();
						}

					}
					else
					{
						showNotification("Warning","Select Department",Notification.TYPE_WARNING_MESSAGE);
						cmbDepartment.focus();
					}

				}
				else
				{
					showNotification("Warning","Select Project",Notification.TYPE_WARNING_MESSAGE);
					cmbUnit.focus();
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
			String deptId="%",secId="%",empType="%",branchId="%";
			if(!chkAllUnit.booleanValue())
			{
				branchId=cmbUnit.getValue().toString();
			}
			
			if(!chkDepartmentAll.booleanValue())
			{
				deptId=cmbDepartment.getValue().toString();
			}
			if(!chkSectionAll.booleanValue())
			{
				secId=cmbSection.getValue().toString();
			}
			if(!chkEmployeeTypeAll.booleanValue())
			{
				empType=cmbEmployeeType.getValue().toString();
			}

			String query = "select * from funEmployeeDetails('"+branchId+"','"+deptId+"','"+secId+"','%','"+empType+"','%','%','%','1') order by vUnitName,vDepartmentName,vSectionName,vEmployeeType,iRank,dJoiningDate";
			System.out.println(query);
			if(queryValueCheck(query))
			{
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("username", sessionBean.getUserName()+" "+sessionBean.getUserIp());
				hm.put("SysDate",reportTime.getTime);
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/account/hrmModule/RptSalaryStructure.jasper",
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
			showNotification("reportShow "+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private boolean queryValueCheck(String sql)
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
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
		allComp.add(cmbSection);
		allComp.add(cmbEmployeeType);
		allComp.add(cButton.btnPreview);
		new FocusMoveByEnter(this,allComp);
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

		cmbUnit=new ComboBox();
		cmbUnit.setImmediate(true);
		cmbUnit.setNullSelectionAllowed(false);
		cmbUnit.setWidth("250px");
		cmbUnit.setHeight("-1px");
		mainLayout.addComponent(new Label("Project : "), "top:20px; left:30.0px;");
		mainLayout.addComponent(cmbUnit, "top:18px; left:140.0px;");

		chkAllUnit = new CheckBox("All");
		chkAllUnit.setImmediate(true);
		mainLayout.addComponent(chkAllUnit,"top:20px; left:395px;");
		chkAllUnit.setVisible(false);

		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("250px");
		cmbDepartment.setNullSelectionAllowed(false);
		cmbDepartment.setHeight("-1px");
		mainLayout.addComponent(new Label("Department : "), "top:50px; left:30.0px;");
		mainLayout.addComponent(cmbDepartment, "top:48px; left:140.0px;");
		chkDepartmentAll = new CheckBox("All");
		chkDepartmentAll.setImmediate(true);
		mainLayout.addComponent(chkDepartmentAll,"top:50px; left:395px;");

		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("250px");
		cmbSection.setNullSelectionAllowed(false);
		cmbSection.setHeight("-1px");
		mainLayout.addComponent(new Label("Section : "), "top:80px; left:30.0px;");
		mainLayout.addComponent(cmbSection, "top:78px; left:140.0px;");

		chkSectionAll = new CheckBox("All");
		chkSectionAll.setImmediate(true);
		mainLayout.addComponent(chkSectionAll,"top:80px; left:395px;");

		// lblEmployeeType
		lblEmployeeType = new Label("Employee Type :");
		lblEmployeeType.setImmediate(false);
		lblEmployeeType.setWidth("100.0%");
		lblEmployeeType.setHeight("-1px");
		mainLayout.addComponent(lblEmployeeType,"top:110px; left:30px;");

		// cmbEmployeeType
		cmbEmployeeType = new ComboBox();
		cmbEmployeeType.setImmediate(true);
		cmbEmployeeType.setWidth("150px");
		cmbEmployeeType.setHeight("-1px");
		cmbEmployeeType.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbEmployeeType, "top:108px; left:140px;");

		// chkEmployeeTypeAll
		chkEmployeeTypeAll = new CheckBox("All");
		chkEmployeeTypeAll.setHeight("-1px");
		chkEmployeeTypeAll.setWidth("-1px");
		chkEmployeeTypeAll.setImmediate(true);
		mainLayout.addComponent(chkEmployeeTypeAll, "top:110px; left:295px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		//mainLayout.addComponent(RadioBtnGroup, "top:100.0px;left:130.0px;");

		//mainLayout.addComponent(new Label("___________________________________________________________________"), "top:120.0px;left:20.0px;right:20.0px;");
		mainLayout.addComponent(cButton,"bottom:20px; left:140.0px");
		return mainLayout;
	}
}
