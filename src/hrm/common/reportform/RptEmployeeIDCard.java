package hrm.common.reportform;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.vaadin.data.Property.*;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.terminal.FileResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Button.ClickShortcut;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.BaseTheme;
import com.common.share.AmountCommaSeperator;
import com.common.share.AmountField;
import com.common.share.CommaSeparator;
import com.common.share.CommonButton;
import com.common.share.CommonMethod;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;


@SuppressWarnings("serial")
public class RptEmployeeIDCard extends Window
{
	private SessionBean sessionBean;

	private AbsoluteLayout mainLayout;
	private CommonButton cButton = new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");


	private ComboBox cmbUnit,cmbSection,cmbDepartment;
	private CheckBox chkDepartmentAll = new CheckBox("All");
	private CheckBox chkUnitAll = new CheckBox("All");
	private CheckBox chkSectionAll = new CheckBox("All");

	NativeButton btnSelectAll,btnDeselect;

	private boolean isAllSelect = false;

	private Table table = new Table();

	private ArrayList<CheckBox> tbchkSelect = new ArrayList<CheckBox>();
	private ArrayList<TextRead> tbtxtEmployeeId = new ArrayList<TextRead>();
	private ArrayList<TextRead> tbtxtEmployeeCode = new ArrayList<TextRead>();
	private ArrayList<TextRead> tbtxtEmployeeName = new ArrayList<TextRead>();
	private ArrayList<TextRead> tbtxtDesignation = new ArrayList<TextRead>();
	private ArrayList<TextRead> tbtxtEmployeeType = new ArrayList<TextRead>();	
	private ArrayList<TextRead> tbtxtBloodGroup= new ArrayList<TextRead>();

	private CommonMethod cm;
	private String menuId = "";
	public RptEmployeeIDCard(SessionBean sessionBean,String menuId)
	{
		this.sessionBean = sessionBean;
		this.setResizable(false);
		this.setCaption("EMPLOYEE ID CARD :: "+sessionBean.getCompany());
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;

		buildMainLayout();
		setContent(mainLayout);

		focusEnter();		
		btnAction();

		cmbUnitDataLoad();

		setButtonShortCut();

		tableInitialise();

		isAllSelect = false;

		cButton.btnNew.focus();
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


	private void focusEnter() 
	{
		ArrayList<Component> allComp = new ArrayList<Component>();

		allComp.add(cmbUnit);

		allComp.add(cButton.btnSave);
		new FocusMoveByEnter(this,allComp);
	}
	private void cmbUnitDataLoad() {
		cmbUnit.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "select distinct vUnitId,vUnitName from tbEmpOfficialPersonalInfo where bStatus=1 order by vUnitName";
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
	private void cmbDepartmentData()
	{
		cmbDepartment.removeAllItems();
		String unitId="%";
		if(!chkUnitAll.booleanValue())
		{
			unitId=cmbUnit.getValue().toString();
		}
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "select distinct vDepartmentId,vDepartmentName from tbEmpOfficialPersonalInfo where bStatus=1 and vUnitId like '"+unitId+"' order by vDepartmentName";
			List <?> lst=session.createSQLQuery(sql).list();

			if(!lst.isEmpty())
			{
				Iterator <?> itr=lst.iterator();
				while(itr.hasNext())
				{
					Object[] element=(Object[])itr.next();
					cmbDepartment.addItem(element[0]);
					cmbDepartment.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbDepartmentData", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void cmbSectionData()
	{
		cmbSection.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		String unitId="%",deptId="%";
		if(!chkUnitAll.booleanValue())
		{
			unitId=cmbUnit.getValue().toString();
		}
		if(!chkDepartmentAll.booleanValue())
		{
			deptId=cmbDepartment.getValue().toString();
		}
		try
		{
			String sql="select distinct vSectionId,vSectionName from tbEmpOfficialPersonalInfo where bStatus=1 and vUnitId like '"+unitId+"' and vDepartmentId like '"+deptId+"' "+
					" order by vSectionName";
			List <?> lst=session.createSQLQuery(sql).list();

			if(!lst.isEmpty())
			{
				Iterator <?> itr=lst.iterator();
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
			showNotification("cmbSectionData", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void allBtnAction()
	{
		if(isAllSelect)
		{
			for(int i=0;i<tbtxtEmployeeId.size();i++){
				if(!tbtxtEmployeeId.get(i).getValue().toString().isEmpty())
				{
					tbchkSelect.get(i).setValue(true);
				}
			}
		}
		else
		{
			for(int i=0;i<tbtxtEmployeeId.size();i++)
				tbchkSelect.get(i).setValue(false);
		}
	}
	private void tableClear()
	{
		for(int i=0; i<tbtxtEmployeeId.size(); i++)
		{
			tbchkSelect.get(i).setValue(false);
			tbtxtEmployeeId.get(i).setValue("");
			tbtxtEmployeeCode.get(i).setValue("");
			tbtxtEmployeeName.get(i).setValue("");			
			tbtxtDesignation.get(i).setValue("");
			tbtxtEmployeeType.get(i).setValue("");			
			tbtxtBloodGroup.get(i).setValue("");
			table.setColumnFooter("Employee Id","");
			table.setColumnFooter("Employee Name","");
			


		}
	}


	private void preBtnAction()
	{
		String con = "";
		String con1 = "";
		String sql = "";
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx = session.beginTransaction();
		boolean isVSelect = false;
		int tsl = 0;


		for(int i=0;i<tbchkSelect.size();i++)
		{
			if(Boolean.valueOf(tbchkSelect.get(i).getValue().toString()))
			{	
				isVSelect = true;

				String EmpId= tbtxtEmployeeId.get(i).getValue().toString();


				con1 = EmpId+"','"+con1;

				con = con1;

				//}
				tsl++;
			}
		}
		if(isVSelect)
		{
			showReport(con);
		}
		else
		{
			getParent().showNotification("You have to select at least one Employee Id  to view or print the ID Card.",Notification.TYPE_WARNING_MESSAGE);
		}			
	}

	private void showReport(String in)
	{
		String sql = "";

		String query="",secId="%",unitId="%";
		try
		{
			if(cmbUnit.getValue()!=null)
			{
				unitId=cmbUnit.getValue().toString();
			}
			if(cmbSection.getValue()!=null)
			{
				secId=cmbSection.getValue().toString();
			}
			HashMap <String,Object>  hm = new HashMap <String,Object> ();
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone",sessionBean.getCompanyContact());
			hm.put("username", sessionBean.getUserName()+" "+sessionBean.getUserIp());
			hm.put("logo", sessionBean.getCompanyLogo());
			//hm.put("developer", sessionBean.getDeveloperAddress());
			System.out.println("Logo Image: "+sessionBean.getCompanyLogo());

			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();


			
			query = "select epo.vEmployeeId,epo.vEmployeeCode,epo.vEmployeeName,di.vDesignation,epo.vEmployeeType,epo.vBloodGroup,epo.vEmployeePhoto "
					+ " from tbEmpOfficialPersonalInfo epo "
					+ " inner join tbDesignationInfo di on epo.vDesignationId=di.vDesignationId "
					+ " where epo.vEmployeeId in ('"+in+"') "
					+ " order by di.iRank,epo.dJoiningDate";
			System.out.println(query);

			hm.put("sql",query);

			Window win = new ReportViewer(hm,"report/account/hrmModule/id.jasper",
					getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
					getWindow().getApplication().getURL()+"VAADIN/rpttmp",false);

			getParent().getWindow().addWindow(win);
			win.setStyleName("cwindow");
			win.setCaption("ID CARD PRINT:: "+sessionBean.getCompany());

		}
		catch(Exception exp)
		{
			showNotification("Error ",exp+"",Notification.TYPE_ERROR_MESSAGE);
		} 
	}

	private void EmployeeDetails()
	{
		tableClear();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();

		String query="",secId="%",unitId="%";
		try
		{
			if(cmbUnit.getValue()!=null)
			{
				unitId=cmbUnit.getValue().toString();
			}
			if(cmbSection.getValue()!=null)
			{
				secId=cmbSection.getValue().toString();
			}
			query = "select epo.vEmployeeId,epo.vEmployeeCode,epo.vEmployeeName,edi.vDesignation,epo.vEmployeeType,epo.vBloodGroup "
					+ " from tbEmpOfficialPersonalInfo epo inner join tbEmpDesignationInfo edi on edi.vEmployeeId=epo.vEmployeeId "
					+ " inner join tbDesignationInfo di on edi.vDesignationId=di.vDesignationId "
					+ " where epo.vUnitId like '"+unitId+"' and epo.vSectionId like '"+secId+"' "
					+ " order by di.iRank,epo.dJoiningDate";


					System.out.println("FindLoanDataAdd : "+query);

			List <?> lst = session.createSQLQuery(query).list();

			if(!lst.isEmpty())
			{
				int i = 0;
				for(Iterator<?> itr = lst.iterator();itr.hasNext();)
				{
					if(i==tbtxtEmployeeId.size()-1)
					{
						tableRowAdd(i+1);
					}
					Object [] element = (Object [])itr.next();
					tbtxtEmployeeId.get(i).setValue(element[0]);
					tbtxtEmployeeCode.get(i).setValue(element[1]);
					tbtxtEmployeeName.get(i).setValue(element[2]);
					tbtxtDesignation.get(i).setValue(element[3]);
					tbtxtEmployeeType.get(i).setValue(element[4]);								
					tbtxtBloodGroup.get(i).setValue(element[5]);					

					i++;

				}
				table.setColumnFooter("Employee Id","Total Employee :");
				table.setColumnFooter("Employee Name", ""+i);
			}



		}
		catch(Exception exp)
		{
			showNotification("LoanDataLoad", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}


	private void btnAction(){


		btnSelectAll.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				isAllSelect=true;
				allBtnAction();
			}
		});
		btnDeselect.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				isAllSelect=false;
				allBtnAction();
			}
		});


		cButton.btnExit.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				close();
			}
		});


		cmbUnit.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbDepartment.removeAllItems();
				chkDepartmentAll.setValue(false);
				cmbDepartment.setEnabled(false);
				if(cmbUnit.getValue()!=null)
				{
					chkUnitAll.setValue(false);
					cmbDepartment.setEnabled(true);
					cmbDepartmentData();
				}
			}
		});
		chkUnitAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				cmbDepartment.removeAllItems();
				chkDepartmentAll.setValue(false);
				cmbDepartment.setEnabled(false);
				if(chkUnitAll.booleanValue())
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
				if(cmbUnit.getValue()!=null || chkUnitAll.booleanValue())
				{
					if(cmbDepartment.getValue()!=null)
					{
						chkDepartmentAll.setValue(false);
						cmbSectionData();
						cmbSection.setEnabled(true);
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
				if(cmbUnit.getValue()!=null || chkUnitAll.booleanValue())
				{
					if(chkDepartmentAll.booleanValue())
					{
						cmbDepartment.setValue(null);
						cmbDepartment.setEnabled(false);
						cmbSection.setEnabled(true);
						cmbSectionData();
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
				tableClear();
				if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
				{
					if(cmbSection.getValue()!=null)
					{
						chkSectionAll.setValue(false);
						EmployeeDetails();
					}
				}
			}
		});

		chkSectionAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				tableClear();
				if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
				{
					if(chkSectionAll.booleanValue())
					{
						cmbSection.setValue(null);
						cmbSection.setEnabled(false);
						EmployeeDetails();
					}
					else
					{
						cmbSection.setEnabled(true);
					}
				}
				else{
					chkSectionAll.setValue(false);
					showNotification("Warning..","Select Department Please!",Notification.TYPE_HUMANIZED_MESSAGE);
				}
			}
		});
		cButton.btnPreview.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				preBtnAction();
			}
		});


	}
	private void tableInitialise()
	{

		for(int i=0;i<12;i++)
		{
			tableRowAdd(i);	

		}
	}

	private void tableRowAdd(final int ar)
	{	
		try
		{
			tbchkSelect.add(ar,new CheckBox());
			tbchkSelect.get(ar).setWidth("100%");
			tbchkSelect.get(ar).setHeight("15px");
			tbchkSelect.get(ar).setImmediate(true);
			tbchkSelect.get(ar).setEnabled(true);

			tbtxtEmployeeId.add(ar, new TextRead());
			tbtxtEmployeeId.get(ar).setWidth("60px");
			tbtxtEmployeeId.get(ar).setHeight("20px");
			tbtxtEmployeeId.get(ar).setImmediate(true);
			
			tbtxtEmployeeCode.add(ar, new TextRead());
			tbtxtEmployeeCode.get(ar).setWidth("60px");
			tbtxtEmployeeCode.get(ar).setHeight("20px");
			tbtxtEmployeeCode.get(ar).setImmediate(true);


			tbtxtEmployeeName.add(ar, new TextRead());
			tbtxtEmployeeName.get(ar).setWidth("250px");
			tbtxtEmployeeName.get(ar).setHeight("20px");
			tbtxtEmployeeName.get(ar).setImmediate(true);

			tbtxtDesignation.add(ar, new TextRead());
			tbtxtDesignation.get(ar).setWidth("250px");
			tbtxtDesignation.get(ar).setHeight("20px");
			tbtxtDesignation.get(ar).setImmediate(true);

			tbtxtEmployeeType.add(ar, new TextRead());
			tbtxtEmployeeType.get(ar).setWidth("100%");
			tbtxtEmployeeType.get(ar).setHeight("20px");
			tbtxtEmployeeType.get(ar).setImmediate(true);

			tbtxtBloodGroup.add(ar, new TextRead());
			tbtxtBloodGroup.get(ar).setWidth("80px");
			tbtxtBloodGroup.get(ar).setHeight("20px");
			tbtxtBloodGroup.get(ar).setImmediate(true);




			table.addItem(new Object[]{tbchkSelect.get(ar),tbtxtEmployeeId.get(ar),tbtxtEmployeeCode.get(ar),tbtxtEmployeeName.get(ar),
					tbtxtDesignation.get(ar),tbtxtEmployeeType.get(ar),tbtxtBloodGroup.get(ar)},ar);
		}
		catch(Exception exp)
		{
			showNotification("Error ",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private AbsoluteLayout buildMainLayout(){

		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setWidth("950px");
		mainLayout.setHeight("540px");
		mainLayout.setMargin(false);

		cmbUnit = new ComboBox();
		cmbUnit.setImmediate(true);
		cmbUnit.setNewItemsAllowed(false);
		cmbUnit.setHeight("-1px");
		cmbUnit.setWidth("200px");
		cmbUnit.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Project :"),"top:20.0px; left:20.0px;");
		mainLayout.addComponent(cmbUnit,"top:18.0px;left:70px;");

		chkUnitAll=new CheckBox("All");
		chkUnitAll.setWidth("-1px");
		chkUnitAll.setHeight("-1px");
		chkUnitAll.setImmediate(true);
		mainLayout.addComponent(chkUnitAll,"top:20px; left:273px");
		chkUnitAll.setVisible(false);

		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setNewItemsAllowed(false);
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setWidth("200px");
		cmbDepartment.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Department :"),"top:20px; left:320px;");
		mainLayout.addComponent(cmbDepartment,"top:18px;left:390px;");

		chkDepartmentAll=new CheckBox("All");
		chkDepartmentAll.setWidth("-1px");
		chkDepartmentAll.setHeight("-1px");
		chkDepartmentAll.setImmediate(true);
		mainLayout.addComponent(chkDepartmentAll,"top:20px; left:593px");
		
		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setNewItemsAllowed(false);
		cmbSection.setHeight("-1px");
		cmbSection.setWidth("200px");
		cmbSection.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Section :"),"top:20px; left:640px;");
		mainLayout.addComponent(cmbSection,"top:18px;left:700px;");

		chkSectionAll=new CheckBox("All");
		chkSectionAll.setWidth("-1px");
		chkSectionAll.setHeight("-1px");
		chkSectionAll.setImmediate(true);
		mainLayout.addComponent(chkSectionAll,"top:20px; left:903px");

		btnSelectAll = new NativeButton();
		btnSelectAll.setImmediate(true);
		btnSelectAll.setWidth("90px");
		btnSelectAll.setHeight("32px");
		btnSelectAll.setIcon(new ThemeResource("./select_icon.png"));
		btnSelectAll.setStyleName(BaseTheme.BUTTON_LINK);	
		mainLayout.addComponent(btnSelectAll, "top:50px; left:20px;");

		btnDeselect = new NativeButton();
		btnDeselect.setImmediate(true);
		btnDeselect.setWidth("90px");
		btnDeselect.setHeight("32px");
		btnDeselect.setIcon(new ThemeResource("./deselect_icon.png"));
		btnDeselect.setStyleName(BaseTheme.BUTTON_LINK);	
		mainLayout.addComponent(btnDeselect, "top:50px; left:110px;");

		table.setFooterVisible(true);
		table.setWidth("98%");
		table.setHeight("382px");
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("Sel", CheckBox.class, new CheckBox());
		table.setColumnWidth("Sel", 20);
		
		table.addContainerProperty("System Employee Id", TextRead.class, new TextRead());
		table.setColumnWidth("System Employee Id", 50);

		table.addContainerProperty("Employee Id", TextRead.class, new TextRead(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Employee Id", 80);


		table.addContainerProperty("Employee Name", TextRead.class, new TextRead());
		table.setColumnWidth("Employee Name", 300);

		table.addContainerProperty("Designation", TextRead.class, new TextRead()/*,null,null,Table.ALIGN_CENTER*/);
		table.setColumnWidth("Designation", 220);

		table.addContainerProperty("Employee Type", TextRead.class, new TextRead());
		table.setColumnWidth("Employee Type", 90);

		table.addContainerProperty("Blood Group", TextRead.class, new TextRead(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Blood Group", 80);


		table.setFooterVisible(true);
		table.setColumnCollapsed("System Employee Id", true);
		table.setColumnAlignment("Total Employee", table.ALIGN_RIGHT);

		table.setFooterVisible(true);


		mainLayout.addComponent(table, "top:80px; left:20.0px;");


		Label lblLine = new Label("<b><font color='#fff'>===================================================================================================================================================================================================================================================</font></b>", Label.CONTENT_XHTML);
		mainLayout.addComponent(lblLine, "top:465px; left:0.0px;");
		mainLayout.addComponent(cButton, "bottom:20px; left:350px;");

		return mainLayout;
	}


	private void setButtonShortCut()
	{
		this.addAction(new ClickShortcut(cButton.btnSave, KeyCode.S, ModifierKey.ALT));
		this.addAction(new ClickShortcut(cButton.btnNew, KeyCode.N, ModifierKey.ALT));
		this.addAction(new ClickShortcut(cButton.btnRefresh, KeyCode.R, ModifierKey.ALT));
	}


	private Iterator<?> dbService(String sql)
	{
		Iterator<?> iter=null;
		Session session=null;
		try{
			session=SessionFactoryUtil.getInstance().openSession();
			iter=session.createSQLQuery(sql).list().iterator();
		}
		catch(Exception exp){
			showNotification(""+exp,Notification.TYPE_ERROR_MESSAGE);
		}
		finally{
			if(session!=null){
				session.close();
			}
		}
		return iter;
	}
}
