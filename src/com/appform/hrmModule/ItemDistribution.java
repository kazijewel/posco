package com.appform.hrmModule;
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
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.ReportOption;
import com.common.share.ReportViewer;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.common.share.ReportDate;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class ItemDistribution extends Window 
{
	private ReportDate reportTime;
	
	private SessionBean sessionBean;
	private CommonMethod cm;
	private String menuId = "";
	
	public AbsoluteLayout mainLayout;

	private OptionGroup opgReportView;
	Table tableCustom;
	TextField txtTime,txtQty;
	TextField txtFindId=new TextField();
	
	private ComboBox cmbUnit,cmbItemType,cmbDepartment,cmbSection;
	private CheckBox chkDepartmentAll,chkSectionAll,chkUnitAll, chkTableAll;
	private TextField txtVenue;
	
	ArrayList<Label> tblblSl=new ArrayList<Label>();
	ArrayList<Label> tblblEmpCode=new ArrayList<Label>();
	ArrayList<CheckBox> tbchkSelect=new ArrayList<CheckBox>();
	ArrayList<Label> tblblEmpId=new ArrayList<Label>();
	ArrayList<Label> tblblEmpName=new ArrayList<Label>();
	ArrayList<Label> tblblDesignationID=new ArrayList<Label>();
	ArrayList<Label> tblblDesignationName=new ArrayList<Label>();
	ArrayList<Label> tbempCheckId=new ArrayList<Label>();

	ArrayList<TextField> tbtxtQty=new ArrayList<TextField>();
	ArrayList<Label> tblblUnit=new ArrayList<Label>();
	ArrayList<TextField> tbtxtRemarks=new ArrayList<TextField>();

	ArrayList<Label> tblblUnitId=new ArrayList<Label>();
	ArrayList<Label> tblblUnitName=new ArrayList<Label>();
	ArrayList<Label> tblblDepartmentId=new ArrayList<Label>();
	ArrayList<Label> tblblDepartmentName=new ArrayList<Label>();
	ArrayList<Label> tblblSectionId=new ArrayList<Label>();
	ArrayList<Label> tblblSectionName=new ArrayList<Label>();
	
	boolean isUpdate=false;
	boolean isFind=false;
	TextRead txtTransactionID;
	PopupDateField date;
	String strDes="";

	int index=0;
	
	
	private static final List<String> reportView = Arrays.asList(new String[]{"PDF","Other"});
	ArrayList<Component> allComp = new ArrayList<Component>();
	SimpleDateFormat dFormat=new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MM-yyyy");
	CommonButton cButton=new CommonButton("New","Save", "Edit", "","Refresh","Find","","Preview","","Exit");
	
	public ItemDistribution(SessionBean sessionBean,String menuId)
	{
		this.sessionBean = sessionBean;
		this.setCaption("ITEM DISTRIBUTION :: "+sessionBean.getCompany());
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;

		buildMainLayout();
		setContent(mainLayout);

		tableAdd();
		componentIni(true);
		btnIni(true);
		cmbItemTypeDataLoad();
		cmbBranchDataLoad();

		setEventAction();
		focusMoveByEnter();
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
	private void focusMoveByEnter()
	{
		allComp.add(cmbItemType);
		allComp.add(cmbUnit);
		allComp.add(cmbDepartment);
		allComp.add(cmbDepartment);
		allComp.add(date);
		allComp.add(txtVenue);
		allComp.add(txtTime);
		for(int z=0; z<tblblEmpCode.size();z++)
		{
			allComp.add(tbtxtQty.get(z));
			allComp.add(tbtxtRemarks.get(z));
		}
		new FocusMoveByEnter(this, allComp);
		
	}
	private void cmbItemTypeDataLoad()
	{
		try{
			Session session=SessionFactoryUtil.getInstance().openSession();
			String sql="select distinct vItemTypeId,vItemTypeName from tbItemTypeInfo where isActive=1 order by vItemTypeName";
			Iterator<?> iter=session.createSQLQuery(sql).list().iterator();
			while(iter.hasNext())
			{
				Object element[]=(Object[])iter.next();
				cmbItemType.addItem(element[0]);
				cmbItemType.setItemCaption(element[0], element[1].toString());
			}
		}catch(Exception exp)
		{
			showNotification("cmbItemTypeDataLoad"+exp);
		}
	}
	private void cmbBranchDataLoad()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();

		try
		{
			String query="select distinct vUnitId,vUnitName from tbEmpOfficialPersonalInfo where bStatus=1 order by vUnitName";
			List <?> list=session.createSQLQuery(query).list();
			System.out.println("cmbBranchDataLoad :"+query);
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbUnit.addItem(element[0]);
				cmbUnit.setItemCaption(element[0], (String) element[1]);
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbBranchDataLoad",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			session.close();
		}
	}
	private void newButtonEvent()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		try{
			Iterator<?> iter=session.createSQLQuery("select ISNULL(MAX(iTransactionID),0)+1 as transactionID " +
					"from tbItemDistribution").list().iterator();
			if(iter.hasNext())
			{
				txtTransactionID.setValue(Integer.parseInt(iter.next().toString()));
			}
		}catch(Exception exp)
		{
			showNotification("new Button event error.."+exp);
		}
	}
	private void btnIni(boolean a){
		cButton.btnNew.setEnabled(a);
		cButton.btnEdit.setEnabled(a);
		cButton.btnExit.setEnabled(a);
		cButton.btnRefresh.setEnabled(!a);
		cButton.btnFind.setEnabled(a);
		cButton.btnSave.setEnabled(!a);
	}
	private void componentIni(boolean b)
	{		
		cmbUnit.setEnabled(!b);
		cmbDepartment.setEnabled(!b);
		cmbSection.setEnabled(!b);
		cmbItemType.setEnabled(!b);

		chkUnitAll.setEnabled(!b);
		chkDepartmentAll.setEnabled(!b);
		chkSectionAll.setEnabled(!b);
		
		tableCustom.setEnabled(!b);
		chkTableAll.setEnabled(!b);
		
		date.setEnabled(!b);
		txtTime.setEnabled(!b);
		txtVenue.setEnabled(!b);
		txtQty.setEnabled(!b);
		txtTransactionID.setEnabled(!b);
	}

	private void tableAdd()
	{
		for(int x=0; x<15; x++)
		{
			addRow(x);
		}
	}
	private void addRow(final int ar)
	{
		tblblSl.add(new Label(""+(ar+1)));
		tblblSl.get(ar).setWidth("100%");

		tblblEmpId.add(new Label());
		tblblEmpId.get(ar).setWidth("100%");

		tblblEmpName.add(new Label());
		tblblEmpName.get(ar).setWidth("100%");

		tblblEmpCode.add(new Label());
		tblblEmpCode.get(ar).setWidth("100%");

		tblblDesignationID.add(new Label());
		tblblDesignationID.get(ar).setWidth("100%");

		tblblDesignationName.add(new Label());
		tblblDesignationName.get(ar).setWidth("100%");

		tbtxtQty.add(new TextField());
		tbtxtQty.get(ar).setWidth("100%");

		tblblUnit.add(new Label());
		tblblUnit.get(ar).setWidth("100%");
		
		tbtxtRemarks.add(new TextField());
		tbtxtRemarks.get(ar).setWidth("100%");

		tbempCheckId.add(ar, new Label());

		tbchkSelect.add(new CheckBox());
		tbchkSelect.get(ar).setWidth("100%");
		tbchkSelect.get(ar).addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				if(tbchkSelect.get(ar).booleanValue()==true)
				{
					tbempCheckId.get(ar).setValue(tblblEmpId.get(ar).toString());
				}
				else{
					tbempCheckId.get(ar).setValue("");
				}

			}
		});


		tblblUnitId.add(new Label());
		tblblUnitId.get(ar).setWidth("100%");

		tblblUnitName.add(new Label());
		tblblUnitName.get(ar).setWidth("100%");

		tblblDepartmentId.add(new Label());
		tblblDepartmentId.get(ar).setWidth("100%");

		tblblDepartmentName.add(new Label());
		tblblDepartmentName.get(ar).setWidth("100%");

		tblblSectionId.add(new Label());
		tblblSectionId.get(ar).setWidth("100%");

		tblblSectionName.add(new Label());
		tblblSectionName.get(ar).setWidth("100%");

		tableCustom.addItem(new Object[]{tblblSl.get(ar),tblblEmpId.get(ar),tblblEmpCode.get(ar),tblblEmpName.get(ar),
			tblblDesignationID.get(ar),tblblDesignationName.get(ar),tbtxtQty.get(ar),tblblUnit.get(ar),tbtxtRemarks.get(ar),tbchkSelect.get(ar),
			tblblDepartmentId.get(ar),tblblDepartmentName.get(ar),tblblSectionId.get(ar),tblblSectionName.get(ar)},ar);
	}
	public void cmbSectionAddData()
	{
		String unit="%",deptId="%";
		if(!chkUnitAll.booleanValue())
		{
			unit=cmbUnit.getValue().toString();
		}
		if(!chkDepartmentAll.booleanValue())
		{
			deptId=cmbDepartment.getValue().toString();
		}
		
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();

		try
		{		
			String query="select distinct vSectionId,vSectionName from tbEmpOfficialPersonalInfo where bStatus=1 " +
					"and vUnitId like '"+unit+"' and vDepartmentId like '"+deptId+"' order by vSectionName";
			System.out.println("cmbSection: "+query);
			
			List <?> list=session.createSQLQuery(query).list();
			cmbSection.removeAllItems();
			
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbSection.addItem(element[0]);
				cmbSection.setItemCaption(element[0], (String) element[1]);
			}
		}
		catch(Exception exp){
			showNotification("cmbSection",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			session.close();
		}
	}
	public void cmbDepartmentAddData()
	{
		String unit="%";
		if(!chkUnitAll.booleanValue())
		{
			unit=cmbUnit.getValue().toString();
		}
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();

		try
		{		
			String query="select distinct vDepartmentId,vDepartmentName from tbEmpOfficialPersonalInfo where bStatus=1 " +
					"and vUnitId like '"+unit+"' order by vDepartmentName";
			System.out.println("cmbDepartmentAddData: "+query);
			
			List <?> list=session.createSQLQuery(query).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbDepartment.addItem(element[0]);
				cmbDepartment.setItemCaption(element[0], (String) element[1]);
			}
		}
		catch(Exception exp){
			showNotification("cmbDepartmentAddData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			session.close();
		}
	}
	public void setEventAction()
	{
		
		cmbUnit.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbUnit.getValue()!= null)
				{
					tableClear();
					cmbDepartment.removeAllItems();
					cmbDepartmentAddData();
					chkDepartmentAll.setValue(false);
					cmbItemType.setValue(null);
				}
			}
		});
		
		chkUnitAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(chkUnitAll.booleanValue())
				{
					tableClear();
					cmbUnit.setValue("");
					cmbUnit.setEnabled(false);
					cmbDepartment.removeAllItems();
					cmbDepartmentAddData();
					chkDepartmentAll.setValue(false);
					cmbDepartment.setEnabled(true);
					cmbItemType.setValue(null);
				}
				else
				{
					tableClear();
					cmbUnit.setEnabled(true);
					chkDepartmentAll.setValue(false);
					cmbDepartment.setEnabled(true);
					cmbItemType.setValue(null);
				}
			}
		});

		cmbDepartment.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{

				if(cmbUnit.getValue()!=null || chkUnitAll.booleanValue())
				{
					if(cmbDepartment.getValue()!=null)
					{
						tableClear();
						cmbItemType.setValue(null);
						cmbSectionAddData();
					}
				}
			
			}
		});

		chkDepartmentAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(chkDepartmentAll.booleanValue())
				{
					if(cmbUnit.getValue()!=null || chkUnitAll.booleanValue())
					{

						tableClear();
						cmbDepartment.setValue(null);
						cmbDepartment.setEnabled(false);
						cmbSectionAddData();
					}
				}
				else
				{
					tableClear();
					cmbDepartment.setEnabled(true);
				}
			}
		});
		cmbSection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{

				if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
				{
					if(cmbSection.getValue()!=null)
					{
						tableClear();
						tableDataLoad();
					}
				}
			
			}
		});

		chkSectionAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(chkSectionAll.booleanValue())
				{
					if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
					{

						tableClear();
						cmbSection.setValue(null);
						cmbSection.setEnabled(false);
						cmbItemType.setValue(null);
						tableDataLoad();
					}
				}
				else
				{
					tableClear();
					cmbSection.setEnabled(true);
					cmbItemType.setValue(null);
				}
			}
		});

		cmbItemType.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(cmbUnit.getValue()!=null || chkUnitAll.booleanValue())
				{
					if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
					{
						if(cmbItemType.getValue()!=null)
						{
							tableItemDataSet();
						}
					}
				}
			}
		});
		txtQty.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(cmbUnit.getValue()!=null || chkUnitAll.booleanValue())
				{

					if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
					{
						if(!txtQty.getValue().toString().isEmpty())
						{
							for(int i=0;i<tableCustom.size();i++)
							{
								if(!tblblEmpId.get(i).getValue().toString().isEmpty())
								{
									tbtxtQty.get(i).setValue(txtQty.getValue());
								}
							}
						}
					}
				
				}
			}
		});		
		
		cButton.btnNew.addListener(new ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				componentIni(false);
				btnIni(false);
				newButtonEvent();

				index=0;  
				txtClear();
				tableClear();
			}
		});
		cButton.btnSave.addListener(new ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{	
				if(cmbItemType.getValue()!=null)
				{
					if(!duplicateCheck())
					{
						saveButtonEvent();
					}
					else{
						showNotification("Data  already exist!",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else{
					showNotification("Give Item Name!",Notification.TYPE_WARNING_MESSAGE);
				}
				
			}
		});
		cButton.btnRefresh.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				tableClear();
				txtClear();
				componentIni(true);
				btnIni(true);
			}
		});

		cButton.btnExit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});
		cButton.btnFind.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				isFind=true;
				componentIni(true);
				btnIni(true);
				findButtonEvent();
			}
		});
		cButton.btnEdit.addListener(new ClickListener() 
		{			
			public void buttonClick(ClickEvent event) 
			{
				isUpdate=true;

				boolean b=false;
				cmbUnit.setEnabled(b);
				cmbDepartment.setEnabled(b);
				cmbDepartment.setEnabled(b);
				cmbItemType.setEnabled(b);

				chkUnitAll.setEnabled(b);
				chkDepartmentAll.setEnabled(b);
				chkSectionAll.setEnabled(b);
				
				tableCustom.setEnabled(!b);
				chkTableAll.setEnabled(!b);
				
				date.setEnabled(b);
				
				txtTime.setEnabled(!b);
				txtVenue.setEnabled(!b);
				txtQty.setEnabled(!b);
				txtTransactionID.setEnabled(!b);
				
				btnIni(false);
				/*componentIni(false);
				btnIni(false);
				chkDepartmentAll.setEnabled(false);
				cmbDepartment.setEnabled(false);*/
			}
		});
		cButton.btnPreview.addListener(new ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(!txtTransactionID.getValue().toString().isEmpty())
				{
					reportpreview();
				}
			}
		});
		chkTableAll.addListener(new ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				if(chkTableAll.booleanValue())
				{
					for(int a=0; a<tblblEmpCode.size();a++)
					{
						if(!tblblEmpId.get(a).getValue().toString().isEmpty())
						{
							tbchkSelect.get(a).setValue(true);
						}
					}
				}
				else{
					for(int a=0; a<tblblEmpCode.size();a++)
					{
						tbchkSelect.get(a).setValue(false);
					}
				}
			}
		});
	
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
	private void reportpreview()
	{
		String item=txtTransactionID.getValue().toString();
		String report="";
		
		reportTime = new ReportDate();

		try
		{
			String query = "select id.vItemId,id.vItemName,id.vUnit,id.vUnitName,id.vVenue,id.iTransactionId,id.dDate,id.vTime,"+
					" id.vDepartmentId,id.vDepartmentName,epo.vEmployeeId,epo.vEmployeeCode,epo.vEmployeeName,di.vDesignationId,di.vDesignation,"+
					" epo.dJoiningDate,id.mQty,id.vRemarks,id.vSectionId,id.vSectionName from tbItemDistribution id "+
					" inner join tbEmpOfficialPersonalInfo epo on epo.vEmployeeId=id.vEmpId "+
					" inner join tbDesignationInfo di on di.vDesignationId=id.vDesignationId "+
					" where id.vUnitId like '"+(cmbUnit.getValue()==null?"%":cmbUnit.getValue().toString())+"' and id.iTransactionId like '"+txtTransactionID.getValue().toString()+"' and vFlag=1 order by id.vUnit,id.vDepartmentName,di.iRank,epo.dJoiningDate";
			
			//System.out.println("ShowReport: "+query);
			report="report/account/hrmModule/rptItemDistribution.jasper";
			
			if(queryValueCheck(query))
			{
				HashMap <String,Object>  hm = new HashMap <String,Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone",sessionBean.getCompanyContact());
				hm.put("username", sessionBean.getUserName()+" "+sessionBean.getUserIp());
				hm.put("SysDate",reportTime.getTime);
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("developer", "Software Solution by : E-Vision Software Ltd.|| helpline : 01755-506044 || www.eslctg.com");
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
			this.getParent().showNotification("Error "+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}
	public void tableItemDataSet()
	{
		String item="";
		Session session=SessionFactoryUtil.getInstance().openSession();
		try{
			String sql ="select distinct vUnit from tbItemTypeInfo where vItemTypeId='"+cmbItemType.getValue()+"'";
			Iterator<?> iter=session.createSQLQuery(sql).list().iterator();
			if(iter.hasNext())
			{
				item=iter.next().toString();
			}
		}
		catch(Exception exp)
		{
			showNotification("tableItemDataSet"+exp);
		}
		
		
		for(int i=0;i<tableCustom.size();i++)
		{
			if(!tblblEmpId.get(i).getValue().toString().isEmpty())
			{
				tblblUnit.get(i).setValue(item);
			}
		}
	}
	private boolean duplicateCheck()
	{
		if(!isUpdate){
			Session session=SessionFactoryUtil.getInstance().openSession();
			try{
				String sql="select * from tbItemDistribution where " +
				" vItemId like '"+cmbItemType.getValue().toString()+"' "
			  + " and vDepartmentId like '"+(cmbDepartment.getValue()==null?"%":chkDepartmentAll.booleanValue()?"%":cmbDepartment.getValue())+"' " 
			  + " and vSectionId like '"+(cmbSection.getValue()==null?"%":chkSectionAll.booleanValue()?"%":cmbSection.getValue())+"' " +
				" and dDate like '"+dFormat.format(date.getValue())+"' ";
				
				System.out.println("Duplicate Value"+sql);
				Iterator<?> iter=session.createSQLQuery(sql).list().iterator();
				if(iter.hasNext())
				{
					 iter.next();
					return true;
				}
			}catch(Exception exp)
			{
				showNotification("Duplicate Check :"+exp,Notification.TYPE_ERROR_MESSAGE);
			}
			return false;
		}
		return false;
	}
	
	private void findButtonEvent()
	{
		Window win=new ItemDistributionFindWindow(sessionBean, txtFindId);
		win.addListener(new Window.CloseListener() {

			public void windowClose(CloseEvent e) {
				if(!txtFindId.getValue().toString().isEmpty())
				{
					txtClear();
					findInit(txtFindId.getValue().toString());
				}
			}
		});
		this.getParent().addWindow(win);
	}
	private void findInit(String transactionID)
	{
		tableClear();
		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx=session.beginTransaction();
		try{
			String sql="select iTransactionID,vEmpId,vEmpName,vEmpCode,vUnitId,vUnitName,vDepartmentId,vDepartmentName,vSectionId,vSectionName, "+
					" vDesignationId,vDesignationName,vItemName,dDate,vVenue,vTime,mQty,vRemarks,vFlag,vUserIp,vUserName, "+
					" dEntryTime,vItemId from tbItemDistribution where iTransactionID='"+transactionID+"' ";
			System.out.println("Find Initialize data :"+sql);
			
			List list=session.createSQLQuery(sql).list();
			if(!list.isEmpty())
			{
				int index=0;
				Iterator<?> iter=list.iterator();
				boolean select=false;
				while(iter.hasNext())
				{
					Object element[]=(Object[])iter.next();
					
					if(element[18].toString().equalsIgnoreCase("1"))
					{
						select =true;
					}
					else{
						select=false;
					}
					if(index==tblblEmpId.size()-1)
						addRow(index+1);					
					txtTransactionID.setValue(element[0]);
					tblblEmpId.get(index).setValue(element[1]);
					tblblEmpName.get(index).setValue(element[2].toString());
					tblblEmpCode.get(index).setValue(element[3]);

					tblblUnitId.get(index).setValue(element[4]);
					tblblUnitName.get(index).setValue(element[5]);

					tblblDepartmentId.get(index).setValue(element[6]);
					tblblDepartmentName.get(index).setValue(element[7]);

					tblblSectionId.get(index).setValue(element[8]);
					tblblSectionName.get(index).setValue(element[9]);
					
					tblblDesignationID.get(index).setValue(element[10]);
					tblblDesignationName.get(index).setValue(element[11].toString());
					cmbItemType.setValue(element[22]);
					date.setValue(element[13]);
					txtVenue.setValue(element[14]);
					txtTime.setValue(element[15]);
					tbtxtQty.get(index).setValue(element[16]);
					tbtxtRemarks.get(index).setValue(element[17]);
					tbchkSelect.get(index).setValue(select);
					
					
					index++;
				}
			}else{
				showNotification("Data Not Found",Notification.TYPE_WARNING_MESSAGE);
			}
		}catch(Exception exp)
		{
			showNotification("Find data Error.."+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private void saveButtonEvent()
	{
		if(checkValidation())
		{
			MessageBox mb=new MessageBox(getParent(), "Are you sure?",MessageBox.Icon.QUESTION,
			isUpdate?"Do you want to Update?":"Do you want to Save", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"),
			new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.setStyleName("cwindowMB");
			mb.show(new EventListener() {

				public void buttonClicked(ButtonType buttonType) {
					if(buttonType==ButtonType.YES)
					{
						Session session=SessionFactoryUtil.getInstance().getCurrentSession();
						Transaction tx=session.beginTransaction();
						if(isUpdate)
						{
							if(deleteData(session,tx))
							{
								insertData(session, tx);
							}
							//txtClear();
							//tableClear();
							componentIni(true);
							btnIni(true);
						}
						else
						{
							insertData(session,tx);
							//txtClear();
							//tableClear();
							componentIni(true);
							btnIni(true);
						}
						Notification n=new Notification(isUpdate?"All Information Updated Successfully!":"All Information Saved Successfully",
						"",Notification.TYPE_TRAY_NOTIFICATION);
						n.setPosition(Notification.POSITION_TOP_RIGHT);
						showNotification(n);
						
					}
				}
			});
		}

	}
	private boolean deleteData(Session session,Transaction tx)
	{
		try{
			String sql="delete from tbItemDistribution where iTransactionID='"+txtTransactionID.getValue()+"' ";
			session.createSQLQuery(sql).executeUpdate();
			return true;
		}catch(Exception exp)
		{
			showNotification("Delete data error.."+exp);
		}
		return false;
	}
	private void insertData(Session session,Transaction tx)
	{
		String sql="",select="",caption="All Information Saved Successfully!";
		try{			
			for(int y=0;y<tblblEmpId.size();y++)
			{
				if(tbchkSelect.get(y).booleanValue())
				{
					select="1";
				}
				else{
					select="0";
				}
				if(isUpdate)
				{
					caption="All Information Updated Successfully!";
				}
				if(!tblblEmpId.get(y).getValue().toString().isEmpty())
				{
					sql="insert into tbItemDistribution(iTransactionId,vEmpId,vEmpName,vEmpCode,vUnitId,vUnitName,vDepartmentId,vDepartmentName,vSectionId,vSectionName," +
							" vDesignationId,vDesignationName,vItemId,vItemName,dDate,vVenue,vTime,mQty,vUnit,vRemarks,vFlag,vUserIp,vUserName,dEntryTime) "+
							" values('"+txtTransactionID.getValue()+"','"+tblblEmpId.get(y).getValue()+"','"+tblblEmpName.get(y).getValue().toString()+"', " +
							" '"+tblblEmpCode.get(y).getValue()+"'," +
							"'"+tblblUnitId.get(y).getValue()+"','"+tblblUnitName.get(y).getValue()+"', " +
							"'"+tblblDepartmentId.get(y).getValue()+"','"+tblblDepartmentName.get(y).getValue()+"', " +
							"'"+tblblSectionId.get(y).getValue()+"','"+tblblSectionName.get(y).getValue()+"', " +
							" '"+tblblDesignationID.get(y).getValue()+"','"+tblblDesignationName.get(y).getValue().toString()+"'," +
							"'"+cmbItemType.getValue().toString()+"'," +
							"'"+cmbItemType.getItemCaption(cmbItemType.getValue())+"'," +
							"'"+dFormat.format(date.getValue())+"', " +
							"'"+txtVenue.getValue()+"'," +
							"'"+txtTime.getValue()+"'," +
							" '"+tbtxtQty.get(y).getValue()+"'," +
							" '"+tblblUnit.get(y).getValue()+"'," +
							"'"+tbtxtRemarks.get(y).getValue()+"','"+select+"'," +
							" '"+sessionBean.getUserIp()+"','"+sessionBean.getUserName()+"',CURRENT_TIMESTAMP)";
					session.createSQLQuery(sql).executeUpdate();
					
					
					System.out.println("Insert data :"+sql);
				}
			}
			tx.commit();
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}
	private boolean checkValidation()
	{
		if(cmbItemType.getValue()!=null)
		{
			for(int z=0;z<tblblEmpId.size();z++)
			{
				if(!tblblEmpId.get(z).getValue().toString().isEmpty())
				{
					return true;
				}
				else{
					showNotification("No data found in this table");
				}
			}
		}
		else{
			showNotification("Please Select Item");
		}
		return false;
	}
	private void txtClear()
	{
		isUpdate=false;
		cmbItemType.setValue(null);
		cmbUnit.setValue(null);
		cmbDepartment.setValue(null);
		cmbSection.setValue(null);
		txtVenue.setValue("");
		txtQty.setValue("");
		txtTime.setValue("");
		chkTableAll.setValue(false);
		chkDepartmentAll.setValue(false);
		chkSectionAll.setValue(false);
	}
	private void tableClear()
	{
		for(int y=0;y<tblblEmpId.size();y++)
		{

			tblblDesignationID.get(y).setValue("");
			tblblDesignationName.get(y).setValue("");
			tblblEmpCode.get(y).setValue("");
			tblblEmpId.get(y).setValue("");
			tblblEmpName.get(y).setValue("");
			tbtxtQty.get(y).setValue("");
			tblblUnit.get(y).setValue("");
			tbtxtRemarks.get(y).setValue("");
			tbchkSelect.get(y).setValue(false);

			tblblDepartmentId.get(y).setValue("");
			tblblDepartmentName.get(y).setValue("");
		}
	}
	private void tableDataLoad()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		String branch="%",department="%",section="%";

		if(!chkUnitAll.booleanValue())
		{
			branch=cmbUnit.getValue().toString();
		}

		if(!chkDepartmentAll.booleanValue())
		{
			section=cmbDepartment.getValue().toString();
		}
		
		try
		{
			String sql="select distinct a.vEmployeeId,a.vEmployeeCode,a.vFingerId,a.vProximityId,a.vEmployeeName,"+
			" edi.vDesignationId,edi.vDesignation, "+
			" a.vDepartmentId,a.vDepartmentName,a.vUnitId,a.vUnitName,a.dJoiningDate,e.iRank,a.vSectionId,a.vSectionName  "+
			" from tbEmpOfficialPersonalInfo a  "+
			" inner join tbEmpDesignationInfo edi on edi.vEmployeeId=a.vEmployeeId "+
			" inner join tbDesignationInfo e on edi.vDesignationId=e.vDesignationId  "+
			" where vUnitId like '"+branch+"' and a.vDepartmentId like '"+section+"' and a.bStatus='1' order by e.iRank,a.dJoiningDate";
			
			System.out.println("tableDataLoad: "+sql);
			
			List <?> lst=session.createSQLQuery(sql).list();
			if(!lst.isEmpty())
			{
				Iterator <?> itr=lst.iterator();
				boolean checkData=false;
				while(itr.hasNext())
				{
					Object [] element=(Object[])itr.next();
					boolean check=false;
					for(int chkindex=0;chkindex<tblblEmpId.size();chkindex++)
					{
						if(tblblEmpId.get(chkindex).getValue().toString().equalsIgnoreCase(element[0].toString()))
						{
							check=true;
							index=chkindex;
							break;
						}
						else if(tblblEmpId.get(chkindex).getValue().toString().isEmpty())
						{
							check=false;
							index=chkindex;
							break;
						}
						else
						{
							check=true;
						}
					}
					if(!check)
					{
						if(index==tblblEmpId.size()-1)
							addRow(index+1);
						tblblEmpId.get(index).setValue(element[0]);
						tblblEmpCode.get(index).setValue(element[1]);
						tblblEmpName.get(index).setValue(element[4].toString());
						tblblDesignationID.get(index).setValue(element[5]);
						tblblDesignationName.get(index).setValue(element[6].toString());
						
						tblblDepartmentId.get(index).setValue(element[7]);
						tblblDepartmentName.get(index).setValue(element[8]);
						
						tblblUnitId.get(index).setValue(element[9]);
						tblblUnitName.get(index).setValue(element[10]);
						
						tblblSectionId.get(index).setValue(element[13]);
						tblblSectionName.get(index).setValue(element[14]);

						index++;
					}
					checkData=check;
				}
				if(checkData)
				{
					showNotification("Warning", "Employee is already Found in the list!!!", Notification.TYPE_WARNING_MESSAGE);
				}
			}
			else
				showNotification("Warning", "No Data Found!!!", Notification.TYPE_WARNING_MESSAGE);

		}
		catch(Exception exp)
		{
			showNotification("tableDataLoad", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	public AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);

		setWidth("900px");
		setHeight("680px");

		cmbUnit=new ComboBox();
		cmbUnit.setWidth("250px");
		cmbUnit.setImmediate(true);
		cmbUnit.setHeight("-1px");
		cmbUnit.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("<b>Project :</b>",Label.CONTENT_XHTML),"top:20px; left:20px");
		mainLayout.addComponent(cmbUnit,"top:18px; left:120px");

		chkUnitAll=new CheckBox();
		chkUnitAll.setImmediate(true);
		//mainLayout.addComponent(chkUnitAll,"top:20px; left:373px"); order by Nazim Sir
		
		txtTransactionID=new TextRead();
		txtTransactionID.setWidth("100px");
		txtTransactionID.setImmediate(true);
		txtTransactionID.setHeight("21px");
		mainLayout.addComponent(new Label("<b>Transaction ID:</b>",Label.CONTENT_XHTML),"top:20px; left:470px");
		mainLayout.addComponent(txtTransactionID,"top:18px; left:570px");


		date=new PopupDateField();
		date.setWidth("120px");
		date.setImmediate(true);
		date.setHeight("-1px");
		date.setDateFormat("dd-MM-yyyy");
		date.setValue(new java.util.Date());
		mainLayout.addComponent(new Label("<b>Date:</b>",Label.CONTENT_XHTML),"top:45px; left:470px");
		mainLayout.addComponent(date,"top:43px; left:570px");

		cmbDepartment=new ComboBox();
		cmbDepartment.setWidth("250px");
		cmbDepartment.setImmediate(true);
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("<b>Department :</b>",Label.CONTENT_XHTML),"top:45px; left:20px");
		mainLayout.addComponent(cmbDepartment,"top:43px; left:120px");

		chkDepartmentAll=new CheckBox();
		chkDepartmentAll.setImmediate(true);
		mainLayout.addComponent(chkDepartmentAll,"top:45px; left:373px");	
		
		cmbSection=new ComboBox();
		cmbSection.setWidth("250px");
		cmbSection.setImmediate(true);
		cmbSection.setHeight("-1px");
		cmbSection.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("<b>Section :</b>",Label.CONTENT_XHTML),"top:70px; left:20px");
		mainLayout.addComponent(cmbSection,"top:68px; left:120px");
		

		chkSectionAll=new CheckBox();
		chkSectionAll.setImmediate(true);
		mainLayout.addComponent(chkSectionAll,"top:70px; left:373px");
		
		
		txtVenue=new TextField();
		txtVenue.setWidth("260px");
		txtVenue.setHeight("-1px");
		mainLayout.addComponent(new Label("<b>Venue:</b>",Label.CONTENT_XHTML),"top:70px; left:470px");
		mainLayout.addComponent(txtVenue,"top:68px; left:570px");	


		cmbItemType=new ComboBox();
		cmbItemType.setWidth("250px");
		cmbItemType.setImmediate(true);
		cmbItemType.setHeight("-1px");
		cmbItemType.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("<b>Item Name :</b>",Label.CONTENT_XHTML),"top:95px; left:20px");
		mainLayout.addComponent(cmbItemType,"top:93px; left:120px");		
		

		txtTime=new TextField();
		txtTime.setWidth("70px");
		txtTime.setImmediate(true);
		txtTime.setHeight("-1px");
		mainLayout.addComponent(new Label("<b>Time:</b>",Label.CONTENT_XHTML),"top:95px; left:470px");
		mainLayout.addComponent(txtTime,"top:93px; left:570px");
		
		txtQty=new TextField();
		txtQty.setWidth("70px");
		txtQty.setImmediate(true);
		txtQty.setHeight("-1px");
		mainLayout.addComponent(new Label("Qty"),"top:127px; left:435px");
		mainLayout.addComponent(txtQty,"top:125px; left:470px");

		tableCustom=new Table();
		tableCustom.setWidth("880px");
		tableCustom.setHeight("400px");
		tableCustom.setImmediate(true);
		tableCustom.setColumnCollapsingAllowed(true);
		tableCustom.addContainerProperty("Sl", Label.class, new Label());
		tableCustom.setColumnWidth("Sl", 20);

		tableCustom.addContainerProperty("auto ID", Label.class, new Label());
		tableCustom.setColumnWidth("auto ID", 40);

		tableCustom.addContainerProperty("Emp ID", Label.class, new Label());
		tableCustom.setColumnWidth("Emp ID", 50);

		tableCustom.addContainerProperty("Emp Name",Label.class, new Label());
		tableCustom.setColumnWidth("Emp Name", 210);	

		tableCustom.addContainerProperty("Designation ID", Label.class, new Label());
		tableCustom.setColumnWidth("Designation ID", 50);

		tableCustom.addContainerProperty("Designation",Label.class, new Label());
		tableCustom.setColumnWidth("Designation", 120);

		tableCustom.addContainerProperty("Qty", TextField.class, new TextField(),null,null,Table.ALIGN_RIGHT);
		tableCustom.setColumnWidth("Qty", 70);

		tableCustom.addContainerProperty("Project",Label.class, new Label());
		tableCustom.setColumnWidth("Project", 60);

		tableCustom.addContainerProperty("Remarks", TextField.class, new TextField());
		tableCustom.setColumnWidth("Remarks", 185);

		tableCustom.addContainerProperty("Select",CheckBox.class, new CheckBox());
		tableCustom.setColumnWidth("Select", 30);
		

		tableCustom.addContainerProperty("Department ID", Label.class, new Label());
		tableCustom.setColumnWidth("Department ID", 50);

		tableCustom.addContainerProperty("Department Name", Label.class, new Label());
		tableCustom.setColumnWidth("Department Name", 150);		

		/*tableCustom.addContainerProperty("DepartmentID", Label.class, new Label());
		tableCustom.setColumnWidth("DepartmentID", 50);

		tableCustom.addContainerProperty("DepartmentName", Label.class, new Label());
		tableCustom.setColumnWidth("DepartmentName", 150);	*/	

		tableCustom.addContainerProperty("Section ID", Label.class, new Label());
		tableCustom.setColumnWidth("Section ID", 50);

		tableCustom.addContainerProperty("Section Name", Label.class, new Label());
		tableCustom.setColumnWidth("Section Name", 150);

		tableCustom.setColumnCollapsed("auto ID", true);
		tableCustom.setColumnCollapsed("Department ID", true);
		tableCustom.setColumnCollapsed("Designation ID", true);

		tableCustom.setColumnCollapsed("Department ID", true);
		tableCustom.setColumnCollapsed("Department Name", true);
		/*tableCustom.setColumnCollapsed("DepartmentID", true);
		tableCustom.setColumnCollapsed("DepartmentName", true);*/
		tableCustom.setColumnCollapsed("Section ID", true);
		tableCustom.setColumnCollapsed("Section Name", true);

		mainLayout.addComponent(tableCustom,"top:150px; left:10px");
		
		chkTableAll=new CheckBox("Apply");
		chkTableAll.setImmediate(true);
		mainLayout.addComponent(chkTableAll,"top:130px; right:20px");

		mainLayout.addComponent(cButton, "bottom:15px;left:130px;");

		return mainLayout;
	}



}