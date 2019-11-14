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
public class RptEmployeeRequisitionForm extends Window
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
	private ComboBox cmbRequisition;
	private CheckBox chkRequisitionAll;
	ArrayList<Component> allComp = new ArrayList<Component>();

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");

	private static final String[] category = new String[] { "Permanent", "Temporary", "Provisionary", "Casual"};
	private ReportDate reportTime = new ReportDate();

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});
	private CommonMethod cm;
	private String menuId = "";
	public RptEmployeeRequisitionForm(SessionBean sessionBean,String menuId)
	{
		this.sessionBean=sessionBean;
		this.setCaption("EMPLOYEE REQUISITION :: "+sessionBean.getCompany());
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
			String query="select distinct epo.vUnitId,epo.vUnitName from tbEmployeeRequisitionForm erf inner join tbEmpOfficialPersonalInfo"+
					" epo on epo.vEmployeeId=erf.vRepEmployeeId where epo.bStatus=1 order by epo.vUnitName";
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
			
			String query="select distinct epo.vDepartmentId,epo.vDepartmentName from tbEmployeeRequisitionForm erf inner join tbEmpOfficialPersonalInfo "+
					" epo on epo.vEmployeeId=erf.vRepEmployeeId where epo.bStatus=1 "
					+ " and epo.vUnitId like '"+(cmbUnit.getValue()==null?"%":cmbUnit.getValue().toString())+"' "
					+ " order by epo.vDepartmentName"; 
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
			showNotification("cmbDepartment",exp+"",Notification.TYPE_ERROR_MESSAGE);
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
			String query="select distinct epo.vSectionId,epo.vSectionName from tbEmployeeRequisitionForm erf inner join tbEmpOfficialPersonalInfo "+
					" epo on epo.vEmployeeId=erf.vRepEmployeeId where epo.bStatus=1 "
					+ " and epo.vUnitId like '"+(cmbUnit.getValue()==null?"%":cmbUnit.getValue().toString())+"' "
					+ " and epo.vDepartmentId like '"+(chkDepartmentAll.booleanValue()?"%":cmbDepartment.getValue()==null?"%":cmbDepartment.getValue())+"' "
					+ " order by epo.vSectionName"; 
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
	public void cmbRequisitionDataLoad()
	{
		cmbRequisition.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			
			String query="select distinct erf.vReQuisitionNo,CONVERT(varchar,erf.dRequsitionDate,105)RequsitionDate,erf.dRequsitionDate"+ 
					" from tbEmployeeRequisitionForm erf  "+
					" inner join tbEmpOfficialPersonalInfo "+
					" epo on epo.vEmployeeId=erf.vRepEmployeeId "+ 
					" where epo.bStatus=1 "
				  + " and epo.vUnitId like '"+(cmbUnit.getValue()==null?"%":cmbUnit.getValue().toString())+"' "
				  + " and epo.vDepartmentId like '"+(chkDepartmentAll.booleanValue()?"%":cmbDepartment.getValue()==null?"%":cmbDepartment.getValue())+"' "
				  + " and epo.vSectionId like '"+(chkSectionAll.booleanValue()?"%":cmbSection.getValue()==null?"%":cmbSection.getValue())+"' "
				  + " order by erf.dRequsitionDate desc";
			System.out.println("cmbRequisitionDataLoad"+query);
			Iterator<?> iter=session.createSQLQuery(query).list().iterator();
			while(iter.hasNext())
			{
				Object element[]=(Object[])iter.next();
				cmbRequisition.addItem(element[0].toString());
				cmbRequisition.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("cmbRequisitionAddData",exp+"",Notification.TYPE_ERROR_MESSAGE);
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
				cmbSection.setEnabled(false);
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
				if(cmbDepartment.getValue()!=null)
				{
					chkDepartmentAll.setValue(false);
					cmbSection.setEnabled(true);
					cmbSectionAddData();
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
				cmbRequisition.removeAllItems();
				chkRequisitionAll.setValue(false);
				cmbRequisition.setEnabled(false);
				if(cmbUnit.getValue()!=null)
				{
					if(cmbSection.getValue()!=null)
					{
						chkSectionAll.setValue(false);
						cmbRequisitionDataLoad();
						cmbRequisition.setEnabled(true);
					}
					else
					{
						cmbRequisition.setEnabled(false);
					}
				}
			}
		});

		chkSectionAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				cmbRequisition.removeAllItems();
				chkRequisitionAll.setValue(false);
				cmbRequisition.setEnabled(false);
				if(cmbUnit.getValue()!=null || chkAllUnit.booleanValue())
				{
					if(chkSectionAll.booleanValue())
					{
						cmbSection.setValue(null);
						cmbSection.setEnabled(false);
						cmbRequisition.setEnabled(true);
						cmbRequisitionDataLoad();
					}
					else
					{
						cmbSection.setEnabled(true);
						cmbRequisition.setEnabled(false);
					}
				}
				else{
					chkSectionAll.setValue(false);
					showNotification("Warning..","Select Project Please!",Notification.TYPE_HUMANIZED_MESSAGE);
				}
			}
		});
		chkRequisitionAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
				{
					if(chkRequisitionAll.booleanValue())
					{
						cmbRequisition.setValue(null);
						cmbRequisition.setEnabled(false);
					}
					else
					{
						cmbRequisition.setEnabled(true);
					}
				}
				else{
					chkRequisitionAll.setValue(false);
					showNotification("Warning..","Select Department Please!",Notification.TYPE_HUMANIZED_MESSAGE);
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
							if(cmbRequisition.getValue()!=null)
							{
								reportShow();
							}
							else
							{
								showNotification("Warning","Select Requisition date",Notification.TYPE_WARNING_MESSAGE);
								cmbRequisition.focus();
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
		//ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		String joinDate= "",report="",query="";
		
		try
		{
				query="select vReQuisitionNo,epo.vDesignationName,vEmployeeNumber,epo.vUnitName,epo.vEmployeeType,vRequitmentType,"+
						" vRepEmployeeCode,vRepEmployeeName,vRepDesignationName,vRepDepartmentName,vReporttoWhom,dRequsitionDate,dRequirementDate,"+
						" erf.vUserName,erf.vUserIp,erf.dEntryTime,epo.vDepartmentId,epo.vDepartmentName "+
						" from tbEmployeeRequisitionForm erf  "+
						" inner join tbEmpOfficialPersonalInfo "+
						" epo on epo.vEmployeeId=erf.vRepEmployeeId "+ 
						" where epo.bStatus=1 "+
						" and epo.vUnitId like '"+(cmbUnit.getValue()==null?"%":cmbUnit.getValue().toString())+"' "+
						" and epo.vDepartmentId like '"+(chkDepartmentAll.booleanValue()?"%":cmbDepartment.getValue()==null?"%":cmbDepartment.getValue())+"' "+
						" and epo.vSectionId like '"+(chkSectionAll.booleanValue()?"%":cmbSection.getValue()==null?"%":cmbSection.getValue())+"' "+
						" and erf.vReQuisitionNo like '"+cmbRequisition.getValue()+"'";
				
				System.out.println("Report Query: "+query);
				report="report/account/hrmModule/RptEmployeeRequsitionForm.jasper";
			if(queryValueCheck(query))
			{	
				System.out.println("Report Query: "+query);
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone",sessionBean.getCompanyContact());
				hm.put("username", sessionBean.getUserName()+" "+sessionBean.getUserIp());
				hm.put("SysDate",reportTime.getTime);
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("sql", query);
				
				Window win = new ReportViewer(hm,report,
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
			this.getParent().showNotification("Error",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
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
		allComp.add(cmbRequisition);
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
		lblEmployeeType = new Label("Requisition Date :");
		lblEmployeeType.setImmediate(false);
		lblEmployeeType.setWidth("100.0%");
		lblEmployeeType.setHeight("-1px");
		mainLayout.addComponent(lblEmployeeType,"top:110px; left:30px;");

		// cmbRequisition
		cmbRequisition = new ComboBox();
		cmbRequisition.setImmediate(true);
		cmbRequisition.setWidth("150px");
		cmbRequisition.setHeight("-1px");
		cmbRequisition.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbRequisition, "top:108px; left:140px;");

		// chkRequisitionAll
		chkRequisitionAll = new CheckBox("All");
		chkRequisitionAll.setHeight("-1px");
		chkRequisitionAll.setWidth("-1px");
		chkRequisitionAll.setImmediate(true);
		mainLayout.addComponent(chkRequisitionAll, "top:110px; left:295px;");
		chkRequisitionAll.setVisible(false);

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
