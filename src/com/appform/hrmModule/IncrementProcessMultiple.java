package com.appform.hrmModule;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.vaadin.data.Property.ValueChangeEvent;
import com.common.share.AmountField;
import com.common.share.CommaSeparator;
import com.common.share.CommonButton;
import com.common.share.CommonMethod;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.data.Property.ValueChangeListener;


@SuppressWarnings({ "unused", "serial" })
public class IncrementProcessMultiple extends Window{
	private SessionBean sessionBean;
	private AbsoluteLayout layoutMain;
	private PopupDateField dIncrementDate;
	private ComboBox cmbIncrementType,cmbUnit,cmbDepartment,cmbEmployee;
	private Label lblEmployee;
	private boolean isUpdate;
	String mbUpdate="",mbSave="";

	private SimpleDateFormat dFormatBD=new SimpleDateFormat("dd-MM-yyyy");

	private SimpleDateFormat dFormatSql = new SimpleDateFormat("yyyy-MM-dd");

	private DecimalFormat df=new DecimalFormat("#0.00");
	CommonButton cButton=new CommonButton("New","Save","Edit","","Refresh","Find","","","","Exit");

	private boolean isFind = false;
	private NativeButton nbIncrementType;
	private Table table;
	private ArrayList<Component> allComp = new ArrayList<Component>();

	private ArrayList<NativeButton> btnDel = new ArrayList<NativeButton>();
	ArrayList<Label> tblblSl=new ArrayList<Label>();
	ArrayList<Label> tblblempHidden=new ArrayList<Label>();
	ArrayList<Label> tblblEmpID=new ArrayList<Label>();
	ArrayList<Label> tblblEmpName=new ArrayList<Label>();
	ArrayList<Label> tblblDesignation=new ArrayList<Label>();
	ArrayList<Label> tblblJoiningDate=new ArrayList<Label>();
	ArrayList<Label> tblblJoiningDateSql=new ArrayList<Label>();
	ArrayList<Label> tblblEmpType=new ArrayList<Label>();
	ArrayList<Label> tblblServiceLength=new ArrayList<Label>();
	ArrayList<Label> tblblBasic=new ArrayList<Label>();
	ArrayList<Label> tblblHouseRent=new ArrayList<Label>();
	ArrayList<Label> tblblMedicalAllowance=new ArrayList<Label>();
	ArrayList<Label> tblblMobile=new ArrayList<Label>();
	ArrayList<Label> tblblTotal=new ArrayList<Label>();
	/*ArrayList<Label> tblblPFpercentage=new ArrayList<Label>();
	ArrayList<Label> tblblPFAmount=new ArrayList<Label>();*/
	ArrayList<Label> tblblDesignationId=new ArrayList<Label>();
	ArrayList<Label> tblblNewEmpID=new ArrayList<Label>();
	ArrayList<Label> tblblNewEmpName=new ArrayList<Label>();
	//ArrayList<Label> tblblNewJoiningDate=new ArrayList<Label>();
	//ArrayList<Label> tblblNewPFAmount=new ArrayList<Label>();

	ArrayList<ComboBox> tbCmbNewDesignation=new ArrayList<ComboBox>();
	ArrayList<ComboBox> tbCmbNewEmpType=new ArrayList<ComboBox>();

	ArrayList<AmountField> tbtxtNewBasic=new ArrayList<AmountField>();
	ArrayList<AmountField> tbtxtNewHouseRent=new ArrayList<AmountField>();
	ArrayList<AmountField> tbtxtNewMedicalAllowance=new ArrayList<AmountField>();
	ArrayList<AmountField> tbtxtNewMobile=new ArrayList<AmountField>();
	ArrayList<AmountField> tbtxtIncAmount=new ArrayList<AmountField>();
	ArrayList<AmountField> tbtxtNewGross=new ArrayList<AmountField>();
	TextRead txtSerialNo;
	private CheckBox checkDepartmentAll,chkEmployeeAll;
	int index=0;
	String sectionId="",empId="";
	
	private Label IncrementDate = new Label();
	private Label DepartmentID = new Label();
	private Label IncrementType = new Label();
	private Label EmployeeName = new Label();
	private CommonMethod cm;
	private String menuId = "";
	public IncrementProcessMultiple(SessionBean sessionBean,String menuId){
		this.sessionBean=sessionBean;
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		this.setCaption("SALARY INCREMENT PROCESS ::"+sessionBean.getCompany());
		buildMainLayout();
		this.setContent(layoutMain);
		btnIni(true);
		btnAction();
		componentIni(true);
		cmbIncrementTypeDataLoad();
		cmbUnitData();
		focuMove();
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
	private void cmbUnitData() 
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql="select distinct vUnitId,vUnitName from tbEmpOfficialPersonalInfo order by vUnitName ";
			List <?> list=session.createSQLQuery(sql).list();

			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbUnit.addItem(element[0]);
				cmbUnit.setItemCaption(element[0], (String) element[1]);
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbUnitData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void tableinitialize()
	{
		for(int x=0;x<12;x++)
		{
			tableRowAdd(x);
		}
	}
	private void tableRowAdd(final int ar)
	{
		tblblSl.add(new Label(""+(ar+1)));
		tblblSl.get(ar).setWidth("100%");
		btnDel.add(ar, new NativeButton());
		btnDel.get(ar).setWidth("100%");
		btnDel.get(ar).setIcon(new ThemeResource("../icons/cancel.png"));
		btnDel.get(ar).setStyleName("Transparent");
		btnDel.get(ar).setImmediate(true);
		btnDel.get(ar).addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				tblblempHidden.get(ar).setValue("");
				tblblEmpID.get(ar).setValue("");
				tblblEmpName.get(ar).setValue("");
				tblblDesignationId.get(ar).setValue("");
				tblblDesignation.get(ar).setValue("");
				tblblJoiningDate.get(ar).setValue("");
				tblblJoiningDateSql.get(ar).setValue("");
				tblblEmpType.get(ar).setValue("");
				tblblServiceLength.get(ar).setValue("");
				tblblBasic.get(ar).setValue("");
				tblblHouseRent.get(ar).setValue("");
				tblblMedicalAllowance.get(ar).setValue("");
				tblblMobile.get(ar).setValue("");
				tblblTotal.get(ar).setValue("");
				tbtxtIncAmount.get(ar).setValue("");
				tbtxtNewGross.get(ar).setValue("");
				tblblNewEmpID.get(ar).setValue("");
				tblblNewEmpName.get(ar).setValue("");
				tbCmbNewDesignation.get(ar).setValue(null);
				tbCmbNewEmpType.get(ar).setValue(null);
				tbtxtNewBasic.get(ar).setValue("");
				tbtxtNewHouseRent.get(ar).setValue("");
				tbtxtNewMedicalAllowance.get(ar).setValue("");
				tbtxtNewMobile.get(ar).setValue("");
				

				for(int tbIndex=ar;tbIndex<tblblempHidden.size();tbIndex++)
				{
					if(tbIndex+1<tblblempHidden.size())
					{
						if(!tblblempHidden.get(tbIndex+1).getValue().toString().trim().equals(""))
						{
							tblblempHidden.get(tbIndex).setValue(tblblempHidden.get(tbIndex+1).getValue().toString().trim());
							tblblEmpID.get(tbIndex).setValue(tblblEmpID.get(tbIndex+1).getValue().toString().trim());
							tblblEmpName.get(tbIndex).setValue(tblblEmpName.get(tbIndex+1).getValue().toString().trim());
							tblblDesignationId.get(tbIndex).setValue(tblblDesignationId.get(tbIndex+1).getValue().toString().trim());
							tblblDesignation.get(tbIndex).setValue(tblblDesignation.get(tbIndex+1).getValue().toString().trim());
							tblblJoiningDate.get(tbIndex).setValue(tblblJoiningDate.get(tbIndex+1).getValue().toString().trim());
							tblblJoiningDateSql.get(tbIndex).setValue(tblblJoiningDateSql.get(tbIndex+1).getValue().toString().trim());
							tblblEmpType.get(tbIndex).setValue(tblblEmpType.get(tbIndex+1).getValue().toString().trim());
							tblblServiceLength.get(tbIndex).setValue(tblblServiceLength.get(tbIndex+1).getValue().toString().trim());
							tblblBasic.get(tbIndex).setValue(tblblBasic.get(tbIndex+1).getValue().toString().trim());
							tblblHouseRent.get(tbIndex).setValue(tblblHouseRent.get(tbIndex+1).getValue().toString().trim());
							tblblMedicalAllowance.get(tbIndex).setValue(tblblMedicalAllowance.get(tbIndex+1).getValue().toString().trim());
							tblblMobile.get(tbIndex).setValue(tblblMobile.get(tbIndex+1).getValue().toString().trim());
							tblblTotal.get(tbIndex).setValue(tblblTotal.get(tbIndex+1).getValue().toString().trim());
							
							tbtxtIncAmount.get(tbIndex).setValue(tbtxtIncAmount.get(tbIndex+1).getValue().toString().trim());
							tbtxtNewGross.get(tbIndex).setValue(tbtxtNewGross.get(tbIndex+1).getValue().toString().trim());
							tblblNewEmpID.get(tbIndex).setValue(tblblNewEmpID.get(tbIndex+1).getValue().toString().trim());						
							tblblNewEmpName.get(tbIndex).setValue(tblblNewEmpName.get(tbIndex+1).getValue().toString().trim());
							tbCmbNewDesignation.get(tbIndex).setValue(tbCmbNewDesignation.get(tbIndex+1).getValue());
							tbCmbNewEmpType.get(tbIndex).setValue(tbCmbNewEmpType.get(tbIndex+1).getValue());
							tbtxtNewBasic.get(tbIndex).setValue(tbtxtNewBasic.get(tbIndex+1).getValue());
							tbtxtNewHouseRent.get(tbIndex).setValue(tbtxtNewHouseRent.get(tbIndex+1).getValue());
							tbtxtNewMedicalAllowance.get(tbIndex).setValue(tbtxtNewMedicalAllowance.get(tbIndex+1).getValue());
							tbtxtNewMobile.get(tbIndex).setValue(tbtxtNewMobile.get(tbIndex+1).getValue());
							
							
							tblblempHidden.get(tbIndex+1).setValue("");
							tblblEmpID.get(tbIndex+1).setValue("");
							tblblEmpName.get(tbIndex+1).setValue("");
							tblblDesignationId.get(tbIndex+1).setValue("");
							tblblDesignation.get(tbIndex+1).setValue("");
							tblblJoiningDate.get(tbIndex+1).setValue("");
							tblblJoiningDateSql.get(tbIndex+1).setValue("");
							tblblEmpType.get(tbIndex+1).setValue("");
							tblblServiceLength.get(tbIndex+1).setValue("");
							tblblBasic.get(tbIndex+1).setValue("");
							tblblHouseRent.get(tbIndex+1).setValue("");
							tblblMedicalAllowance.get(tbIndex+1).setValue("");
							tblblMobile.get(tbIndex+1).setValue("");
							tblblTotal.get(tbIndex+1).setValue("");
							tbtxtIncAmount.get(tbIndex+1).setValue("");
							tbtxtNewGross.get(tbIndex+1).setValue("");
							tblblNewEmpID.get(tbIndex+1).setValue("");
							tblblNewEmpName.get(tbIndex+1).setValue("");
							tbCmbNewDesignation.get(tbIndex+1).setValue(null);
							tbCmbNewEmpType.get(tbIndex+1).setValue(null);
							tbtxtNewBasic.get(tbIndex+1).setValue("");
							tbtxtNewHouseRent.get(tbIndex+1).setValue("");
							tbtxtNewMedicalAllowance.get(tbIndex+1).setValue("");
							tbtxtNewMobile.get(tbIndex+1).setValue("");
							
							index--;
						}
					}
				}
			}
		});
		
		tblblempHidden.add(new Label());
		tblblempHidden.get(ar).setWidth("100%");

		tblblEmpID.add(new Label());
		tblblEmpID.get(ar).setWidth("100%");

		tblblEmpName.add(new Label());
		tblblEmpName.get(ar).setWidth("100%");

		tblblDesignationId.add(new Label());
		tblblDesignationId.get(ar).setWidth("100%");

		tblblDesignation.add(new Label());
		tblblDesignation.get(ar).setWidth("100%");

		tblblJoiningDate.add(new Label());
		tblblJoiningDate.get(ar).setWidth("100%");

		tblblJoiningDateSql.add(new Label());
		tblblJoiningDateSql.get(ar).setWidth("100%");

		tblblEmpType.add(new Label());
		tblblEmpType.get(ar).setWidth("100%");

		tblblServiceLength.add(new Label());
		tblblServiceLength.get(ar).setWidth("100%");

		tblblBasic.add(new Label());
		tblblBasic.get(ar).setWidth("100%");

		tblblHouseRent.add(new Label());
		tblblHouseRent.get(ar).setWidth("100%");

		tblblMedicalAllowance.add(new Label());
		tblblMedicalAllowance.get(ar).setWidth("100%");

		tblblMobile.add(new Label());
		tblblMobile.get(ar).setWidth("100%");

		tblblTotal.add(new Label());
		tblblTotal.get(ar).setWidth("100%");

		tbtxtIncAmount.add(new AmountField());
		tbtxtIncAmount.get(ar).setWidth("100%");
		tbtxtIncAmount.get(ar).setImmediate(true);
		
		tbtxtIncAmount.get(ar).addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(!tbtxtIncAmount.get(ar).getValue().toString().trim().isEmpty()){
					if(!isFind)
					{
						double prvGross=Double.parseDouble(tblblTotal.get(ar).getValue().toString().replaceAll(",",""));
						double prvBasic=Double.parseDouble(tblblBasic.get(ar).getValue().toString().replaceAll(",",""));
						double incAmount=Double.parseDouble(tbtxtIncAmount.get(ar).getValue().toString());

						tbtxtNewBasic.get(ar).setValue(df.format(Math.round(prvBasic+incAmount)));
						tbtxtNewGross.get(ar).setValue(df.format(Math.round(prvGross+incAmount)));
					}
				}
			}
		});

		tbtxtNewGross.add(new AmountField());
		tbtxtNewGross.get(ar).setWidth("100%");
		tbtxtNewGross.get(ar).setImmediate(true);

		tbtxtNewGross.get(ar).addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(!tbtxtNewGross.get(ar).getValue().toString().trim().isEmpty())
				{
					if(!isFind)
					{
						calIncrementSalary(ar);
					}
					
				}
			}
		});

		//New Status
		tblblNewEmpID.add(new Label());
		tblblNewEmpID.get(ar).setWidth("100%");

		tblblNewEmpName.add(new Label());
		tblblNewEmpName.get(ar).setWidth("100%");

		tbCmbNewDesignation.add(new ComboBox());
		tbCmbNewDesignation.get(ar).setWidth("100%");
		if(!isFind)
		{
			cmbDesignationDataLoad(ar);
		}

		tbCmbNewEmpType.add(new ComboBox());
		tbCmbNewEmpType.get(ar).setWidth("100%");
		cmbEmployeeTypeDataLoad(ar);
		

		tbtxtNewBasic.add(new AmountField());
		tbtxtNewBasic.get(ar).setWidth("100%");

		tbtxtNewHouseRent.add(new AmountField());
		tbtxtNewHouseRent.get(ar).setWidth("100%");

		tbtxtNewMedicalAllowance.add(new AmountField());
		tbtxtNewMedicalAllowance.get(ar).setWidth("100%");

		tbtxtNewMobile.add(new AmountField());
		tbtxtNewMobile.get(ar).setWidth("100%");

		table.addItem(new Object[]{tblblSl.get(ar),
				btnDel.get(ar),
				tblblempHidden.get(ar),tblblEmpID.get(ar),tblblEmpName.get(ar),
				tblblDesignationId.get(ar),tblblDesignation.get(ar),tblblJoiningDate.get(ar),tblblJoiningDateSql.get(ar),
				tblblEmpType.get(ar),tblblServiceLength.get(ar),tblblBasic.get(ar),
				tblblHouseRent.get(ar),tblblMedicalAllowance.get(ar),tblblMobile.get(ar),
				tblblTotal.get(ar),
				tbtxtIncAmount.get(ar),tbtxtNewGross.get(ar),
				tblblNewEmpID.get(ar),tblblNewEmpName.get(ar),
				tbCmbNewDesignation.get(ar),tbCmbNewEmpType.get(ar),
				tbtxtNewBasic.get(ar),tbtxtNewHouseRent.get(ar),tbtxtNewMedicalAllowance.get(ar),
				tbtxtNewMobile.get(ar)},ar);
	}
	public void cmbEmployeeTypeDataLoad(int ar){
		tbCmbNewEmpType.get(ar).addItem("Regular");
		tbCmbNewEmpType.get(ar).addItem("Temporary");
	}
	public void cmbDesignationDataLoad(int ar){
		tbCmbNewDesignation.get(ar).removeAllItems();
		String sql="select distinct vDesignationId,vDesignation from tbDesignationInfo order by vDesignation";
		Iterator<?> iter=dbService(sql);
		while(iter.hasNext()){
			Object[] element=(Object[])iter.next();
			tbCmbNewDesignation.get(ar).addItem(element[0]);
			tbCmbNewDesignation.get(ar).setItemCaption(element[0], element[1].toString());
		}
	}
	private void focuMove()
	{
		allComp.add(cmbUnit);
		allComp.add(cmbDepartment);
		allComp.add(cmbIncrementType);
		allComp.add(cmbEmployee);
		
		for(int i=0;i<tblblempHidden.size();i++){
			allComp.add(tbtxtIncAmount.get(i));
		}

		allComp.add(cButton.btnSave);
		new FocusMoveByEnter(this, allComp);
	}
	
	public void cmbEmployeeAddData()
	{
		cmbEmployee.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vEmployeeID,vEmployeeCode,vEmployeeName from tbEmpOfficialPersonalInfo " +
					"where vUnitId='"+(cmbUnit.getValue()!=null?cmbUnit.getValue().toString():"%")+"' " +
					"and vDepartmentId like '"+(cmbDepartment.getValue()!=null?cmbDepartment.getValue().toString():"%")+"' and bStatus='1'";
			
			System.out.println("cmbEmployeeAddData: "+query);
			
			List <?> list=session.createSQLQuery(query).list();

			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbEmployee.addItem(element[0]);
				cmbEmployee.setItemCaption(element[0], element[1].toString()+"-"+element[2]);
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbEmployeeAddData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	
	private void findbuttonEvent()
	{
		Window win = new IncrementProcessFind(sessionBean,IncrementDate,DepartmentID,IncrementType,EmployeeName);
		win.addListener(new CloseListener()
		{
			public void windowClose(CloseEvent e)
			{
				if(!IncrementDate.getValue().toString().trim().isEmpty() && !DepartmentID.getValue().toString().trim().isEmpty() && !IncrementType.getValue().toString().trim().isEmpty() && !EmployeeName.getValue().toString().trim().isEmpty() )
				{
					findInitialize(IncrementDate.getValue().toString().trim(), DepartmentID.getValue().toString().trim(),IncrementType.getValue().toString().trim(),EmployeeName.getValue().toString().trim());
					cmbIncrementType.setEnabled(false);
					cmbDepartment.setEnabled(false);
					cmbEmployee.setEnabled(false);
				}
			}
		});
		this.getParent().addWindow(win);
	}
	private void findInitialize(String incDate, String Department,String incType,String empId){
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		int i=0;
		try 
		{
			String sql = "select iSerialNo,dDate,vEmployeeId,employeeCode,vEmployeeName,vDepartmentId,vDepartmentName,vDesignationId,vDesignationName," +
					"mIncrementPercentage,mIncrementAmount,mNewGross,dJoiningDate,vEmployeeType,vServiceLength,mBasic,mHouseRent,mMedicalAllowance," +
					"mMobile,mTotalSalary,mPFPercentage,mPFAmount,vNewDesignationId,vNewDesignationName,vNewEmployeeType,mNewBasic,mNewHouseRent," +
					"mNewMedicalAllowance,mNewMobile,mNewTotalSalary,mNewPFPercentage,mNewPFAmount,vIncrementId,vIncrementType,vUnitId,vUnitName " +
					"from tbSalaryIncrement " +
					"where vEmployeeId='"+empId+"' and vIncrementId='"+incType+"' and YEAR(dDate)=YEAR('"+incDate+"')";
			
			System.out.println("FindInitializeByDidar: "+sql);
			
			Iterator iter=session.createSQLQuery(sql).list().iterator();
			if(iter.hasNext()){
				Object element[]=(Object[])iter.next();
				System.out.println(element[0]);
				if(i==0){
					txtSerialNo.setValue(element[0]);
					dIncrementDate.setValue(element[1]);
					cmbUnit.setValue(element[34].toString());
					cmbDepartment.setValue(element[5].toString());
					cmbIncrementType.setValue(element[32]);
					cmbIncrementType.setItemCaption(element[32], element[33].toString());
					cmbEmployee.setValue(element[2].toString());
				}
				
				tblblempHidden.get(i).setValue(element[2].toString());
				tblblEmpID.get(i).setValue(element[3].toString());
				tblblEmpName.get(i).setValue(element[4].toString());
				tblblEmpType.get(i).setValue(element[13].toString());
				tblblDesignationId.get(i).setValue(element[7].toString());
				tblblDesignation.get(i).setValue(element[8].toString());
				tblblBasic.get(i).setValue(df.format(element[15]).toString());
				tblblHouseRent.get(i).setValue(df.format(element[16]).toString());
				tblblMedicalAllowance.get(i).setValue(df.format(element[17]).toString());				
				tblblMobile.get(i).setValue(df.format(element[18]).toString());	
				tblblJoiningDate.get(i).setValue(dFormatBD.format(element[12]).toString());
				tblblJoiningDateSql.get(i).setValue(element[12]);
				tblblServiceLength.get(i).setValue(element[14].toString());
				tblblTotal.get(i).setValue(df.format(element[19]).toString());
				tblblNewEmpID.get(i).setValue(element[3].toString());
				tblblNewEmpName.get(i).setValue(element[4].toString());
				tbCmbNewDesignation.get(i).setValue(element[22].toString());
				tbCmbNewDesignation.get(i).setItemCaption(element[22],element[23].toString());
				tbCmbNewEmpType.get(i).setValue(element[24]);
				tbtxtNewMedicalAllowance.get(i).setValue(df.format(element[10]).toString());
				tbtxtNewBasic.get(i).setValue(df.format(element[25]).toString());
				tbtxtNewHouseRent.get(i).setValue(df.format(element[26]).toString());
				tbtxtNewMedicalAllowance.get(i).setValue(df.format(element[27]).toString());
				tbtxtNewMobile.get(i).setValue(df.format(element[28]).toString());
				tbtxtIncAmount.get(i).setValue(df.format(element[10]).toString());
				tbtxtNewGross.get(i).setValue(df.format(element[11]).toString());				
				i++;
			}

			isFind = false;
		}
		catch (Exception exp) 
		{
			showNotification("Error", exp + "",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private boolean existDataCheck(String query)
	{
		boolean ret = false;
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> lst=session.createSQLQuery(query).list();
			if(!lst.isEmpty())
				ret = true;
		}
		catch (Exception exp)
		{
			showNotification("saveButtonEvent", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
		return ret;
	}
	private boolean chkDuplicate(String empId)
	{
		boolean ret = false;
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select * from tbSalaryIncrement "
					+ "where vDepartmentId='"+cmbDepartment.getValue()+"' "
					+ "and vEmployeeId like'"+empId+"' "
					+ "and vIncrementId='"+cmbIncrementType.getValue()+"' "
					+ "and YEAR(dDate)=YEAR('"+dFormatSql.format(dIncrementDate.getValue())+"')";
			
			Iterator <?> iter = session.createSQLQuery(query).list().iterator();

			if (iter.hasNext()) 
			{
				ret = true;
			}
		}
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		finally{session.close();}

		return ret;
	}
	
	private void btnAction()
	{
		cmbUnit.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbUnit.getValue()!=null)
				{
					cmbDepartment.removeAllItems();
					cmbDepartmentData(cmbUnit.getValue().toString());
				}
			}
		});
		cmbDepartment.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				tableClear();
				cmbEmployee.setValue(null);
				chkEmployeeAll.setValue(false);
				cmbEmployee.setEnabled(true);
				if(cmbDepartment.getValue()!=null)
				{
					cmbEmployee.removeAllItems();
					cmbEmployeeAddData();
				}
				else {
					tableClear();
				}
				
			}
		});
		cmbEmployee.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				//tableClear();
				if(cmbIncrementType.getValue()!=null)
				{
					if(cmbDepartment.getValue()!=null)
					{
						if(cmbEmployee.getValue()!=null)
						{
							if(chkDuplicate(cmbEmployee.getValue().toString()) && !isFind )
							{
								showNotification("Warning!","Increment Data already exists!",Notification.TYPE_WARNING_MESSAGE);						
							}
							else{
								addTableData(cmbEmployee.getValue().toString());
							}
						}
					}
					else {
						tableClear();
					}
					
				}
				else {
					tableClear();
				}
			}
		});

		chkEmployeeAll.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				//tableClear();
				if(cmbUnit.getValue()!=null)
				{
					if(cmbDepartment.getValue()!=null)
					{
						if(cmbIncrementType.getValue()!=null)
						{
							if(chkEmployeeAll.booleanValue())
							{
								cmbEmployee.setValue(null);
								cmbEmployee.setEnabled(false);
								if(chkDuplicate("%") && !isFind )
								{
									showNotification("Warning!","Data already exists!",Notification.TYPE_WARNING_MESSAGE);						
								}
								else{
									addTableData("%");
								}
							}
							else{
								cmbEmployee.setEnabled(true);
							}
						}
						else {
							tableClear();
						}
						
					}
					else {
						tableClear();
					}
					
				}
				else {
					tableClear();
				}
			}
		});
		cmbIncrementType.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(!isFind){
					cmbEmployee.setValue(null);
					chkEmployeeAll.setValue(false);
					cmbEmployee.setEnabled(true);
					
					if(cmbIncrementType.getValue()!=null){
						tableClear();
					}
					else {
						tableClear();
					}
				}
			}
		});
		nbIncrementType.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				System.out.println("Group Form");
				incrementTypeLink();				
			}
		});
		cButton.btnNew.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				isFind = false;
				isUpdate = false;
				transactionNo();
				txtClear();
				btnIni(false);
				componentIni(false);
				index=0;
				cmbUnit.focus();
			}
		});
		cButton.btnSave.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				if(checkValidation())
				{
					saveButtonAction();
					index=0;
				}
			}
		});
		cButton.btnEdit.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				btnIni(false);
				
				isUpdate=true;
				
				table.setEnabled(true);
				
				cmbIncrementType.setEnabled(false);
				cmbDepartment.setEnabled(false);
				cmbEmployee.setEnabled(false);
				chkEmployeeAll.setEnabled(false);
			}
		});
		cButton.btnRefresh.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				btnIni(true);
				transactionNo();
				txtClear();
				tableClear();
				componentIni(true);

			}
		} );
		cButton.btnFind.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				isFind=true;
				index=0;
				findbuttonEvent();
				dIncrementDate.setEnabled(true);

			}
		});
		cButton.btnExit.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				close();
			}
		});

	}
	public void incrementTypeLink() 
	{
		Window win = new IncrementType(sessionBean,"IncrementType");

		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				System.out.println("Group Form");
				cmbIncrementTypeDataLoad();
			}
		});

		this.getParent().addWindow(win);

	}
	private void addTableData(String empId)
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql="select distinct a.vEmployeeId,a.vEmployeeCode,a.vFingerId,a.vProximityId,a.vEmployeeName,"+
					" a.vEmployeeType,c.vDesignationId,c.vDesignation,b.mBasic,b.mHouseRent, "+
					" b.mMedicalAllowance,b.mMobileAllowance, "+
					" b.mProvidentFund pf, "+
					" a.dJoiningDate, "+
					" DATEDIFF(DD,a.dJoiningDate,'"+dFormatSql.format(dIncrementDate.getValue())+"')/365 jdYear, "+
					" DATEDIFF(DD,a.dJoiningDate,'"+dFormatSql.format(dIncrementDate.getValue())+"')%365/30 jdMonth, "+
					" DATEDIFF(DD,a.dJoiningDate,'"+dFormatSql.format(dIncrementDate.getValue())+"')%365%30 jdDay, "+
					" (b.mBasic+b.mHouseRent+b.mMedicalAllowance+b.mMobileAllowance) as total,b.mProvidentFund," +
					" (select iDesignationSerial from tbDesignationInfo where vDesignationId=c.vDesignationId)iDesignationSerial," +
					" a.vUnitId,a.vUnitName,a.vDepartmentId,a.vDepartmentName "+
					" from tbEmpOfficialPersonalInfo a "+
					" inner join tbEmpSalaryStructure b on a.vEmployeeId=b.vEmployeeId "+
					" inner join tbEmpDesignationInfo c on a.vEmployeeId=c.vEmployeeId "+
					" inner join tbEmpOfficialPersonalInfo d on a.vEmployeeId=d.vEmployeeId "+
					" where d.vDepartmentId='"+cmbDepartment.getValue().toString()+"' "+
					" and a.vEmployeeId like '"+empId+"' "+
					" and a.bStatus='1' "+
					" order by iDesignationSerial,a.dJoiningDate";
			
			System.out.println("TableDataLoadByDidar: "+sql);
			
			List <?> lst=session.createSQLQuery(sql).list();
			if(!lst.isEmpty())
			{
				Iterator <?> itr=lst.iterator();
				boolean checkData=false;
				while(itr.hasNext())
				{
					Object[] element=(Object[]) itr.next();
					boolean check=false;
					for(int chkindex=0;chkindex<tblblempHidden.size();chkindex++)
					{
						if(tblblempHidden.get(chkindex).getValue().toString().equalsIgnoreCase(element[0].toString()))
						{
							check=true;
							index=chkindex;
							break;
						}
						else if(tblblempHidden.get(chkindex).getValue().toString().isEmpty())
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
						if(index==tblblEmpID.size()-1)
							tableRowAdd(index+1);
						tblblempHidden.get(index).setValue(element[0].toString());
						tblblEmpID.get(index).setValue(element[1].toString());
						tblblEmpName.get(index).setValue(element[4].toString());
						tblblEmpType.get(index).setValue(element[5].toString());
						tblblDesignationId.get(index).setValue(element[6].toString());
						tblblDesignation.get(index).setValue(element[7].toString());
						tblblBasic.get(index).setValue(Double.parseDouble(element[8].toString()));
						tblblHouseRent.get(index).setValue(Double.parseDouble(element[9].toString()));
						tblblMedicalAllowance.get(index).setValue(Double.parseDouble(element[10].toString()));
						tblblMobile.get(index).setValue(Double.parseDouble(element[11].toString()));
						tblblJoiningDate.get(index).setValue(dFormatBD.format(element[13]).toString());	
						tblblJoiningDateSql.get(index).setValue(element[13]);
						tblblServiceLength.get(index).setValue(element[14]+"y "+element[15]+"m "+element[16]+"d");
						tblblTotal.get(index).setValue(Double.parseDouble(element[17].toString()));

						tblblNewEmpID.get(index).setValue(element[1].toString());
						tblblNewEmpName.get(index).setValue(element[4].toString());
						tbCmbNewDesignation.get(index).setValue( element[6].toString());
						tbCmbNewEmpType.get(index).setValue(element[5].toString());
						
						index++;
					}
					checkData=check;
				}
				if(checkData)
				{
					showNotification("Warning", "Employee is already Found in the list!!!", Notification.TYPE_WARNING_MESSAGE);
				}
			}
			else{
				showNotification("Warning", "No Data Found!!!", Notification.TYPE_WARNING_MESSAGE);
			}
				
		}
		catch(Exception exp)
		{
			showNotification("TableValueAdd", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			session.close();
		}
	}
	private void tableClear()
	{
		for(int ar=0;ar<tblblempHidden.size();ar++)
		{
			tblblempHidden.get(ar).setValue("");
			tblblEmpID.get(ar).setValue("");
			tblblEmpName.get(ar).setValue("");
			tblblDesignationId.get(ar).setValue("");
			tblblDesignation.get(ar).setValue("");
			tblblJoiningDate.get(ar).setValue("");
			tblblJoiningDateSql.get(ar).setValue("");
			tblblEmpType.get(ar).setValue("");
			tblblServiceLength.get(ar).setValue("");
			tblblBasic.get(ar).setValue("");
			tblblHouseRent.get(ar).setValue("");
			tblblMedicalAllowance.get(ar).setValue("");
			tblblMobile.get(ar).setValue("");
			tbtxtIncAmount.get(ar).setValue("");
			tblblTotal.get(ar).setValue("");
			tbtxtNewGross.get(ar).setValue("");
			tblblNewEmpID.get(ar).setValue("");
			tblblNewEmpName.get(ar).setValue("");
			tbCmbNewDesignation.get(ar).setValue(null);
			tbCmbNewEmpType.get(ar).setValue(null);
			tbtxtNewBasic.get(ar).setValue("");
			tbtxtNewHouseRent.get(ar).setValue("");
			tbtxtNewMedicalAllowance.get(ar).setValue("");
			tbtxtNewMobile.get(ar).setValue("");
		}
	}
	private void saveButtonAction()
	{

		if(isUpdate){
			MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to update information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						if(isDelete()){
							insertAllData();
							btnIni(true);
							txtClear();
							tableClear();
							componentIni(true);
							isUpdate = false; 
							isFind=false;
							cButton.btnNew.focus();
							
							Notification n=new Notification("All Information Updated Successfully!","",Notification.TYPE_TRAY_NOTIFICATION);
							n.setPosition(Notification.POSITION_TOP_RIGHT);
							showNotification(n);
						}
					}
				}
			});
		}
		else{
			MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to Insert information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						insertAllData();
						btnIni(true);
						txtClear();
						tableClear();
						componentIni(true);
						isUpdate = false;
						isFind=false;
						cButton.btnNew.focus();
						
						Notification n=new Notification("All Information Save Successfully!","",Notification.TYPE_TRAY_NOTIFICATION);
						n.setPosition(Notification.POSITION_TOP_RIGHT);
						showNotification(n);
					}
				}
			});
		}

	}
	
	
	public void insertAllData()
	{
		String masterEmployeeId="";
		String employeeUdTableData="",sql="";
		
		String udFlagForUdEmployeeInfo="NEW Salary Increment";
		String uDFlag="NEW";
	
		if(isUpdate){
			udFlagForUdEmployeeInfo="UPDATE Salary Increment";
			uDFlag="UPDATE";
		}
		
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{
			for(int i=0;i<tblblempHidden.size();i++)
			{
				if(!tblblempHidden.get(i).getValue().toString().isEmpty())
				{

					/*tbUdEmployeeInformation Start*/
					masterEmployeeId=tblblempHidden.get(i).getValue().toString();
					
					employeeUdTableData ="INSERT into tbUdEmployeeInformation (vEmployeeId,vEmployeeCode,vFingerId,vProximityId," +
							" vEmployeeName,vReligion,vGender,dDateOfBirth,vNationality,vNationalIdNo,vEmployeeType,vServiceType," +
							" bPhysicallyDisable,dApplicationDate,dInterviewDate,dJoiningDate,vConfirmationDate,vPayScaleId," +
							" vPayScaleName,vEmployeeStatus,bStatus,vStatusDate,vAccountNo," +
							" vDesignationId,vGroupId," +
							" vRegisterId,vRegisterName,mBasic,mHouseRent,mMedicalAllowance,mClinicalAllowance,mNonPracticeAllowance," +
							" mSpecialAllowance,mOtherAllowance,mDearnessAllowance,mMobileAllowance,mAttendanceBonus," +
							" mRoomCharge,mIncomeTax,mProvidentFund,mKallanFund,mKhichuriMeal,vUdFlag,iOtEnable,vUserName,vUserIp,dEntryTime," +
							" iProbationPeriod,vUnitId,vUnitName,vDepartmentId,vDepartmentName,vSectionId,vSectionName )" +

							" select ei.vEmployeeId,vEmployeeCode,vFingerId,vProximityId,ei.vEmployeeName,vReligion,vGender,"+
							" dDateOfBirth,vNationality,vNationalIdNo,vEmployeeType,vServiceType,bPhysicallyDisable,"+
							" dApplicationDate,dInterviewDate,dJoiningDate,vConfirmationDate,vPayScaleId,vPayScaleName,"+
							" vEmployeeStatus,bStatus,vStatusDate,vAccountNo," + 
							" (select vDesignationId from tbEmpDesignationInfo where vEmployeeId = '"+masterEmployeeId+"' and isCurrent = 1)," +
							" '0'," +
							" vRegisterId,vRegisterName,mBasic,mHouseRent,"+
							" mMedicalAllowance,mClinicalAllowance,'"+0+"',mSpecialAllowance,mOtherAllowance,"+
							" mDearnessAllowance,mMobileAllowance,mAttendanceBonus,mRoomCharge,mIncomeTax,"+
							" mProvidentFund,mKallanFund,mKhichuriMeal,'"+udFlagForUdEmployeeInfo+"',iOtEnable,ei.vUserName,ei.vUserIp,ei.dEntryTime," +
							" ei.iProbationPeriod,ei.vUnitId,ei.vUnitName,ei.vDepartmentId,ei.vDepartmentName,ei.vSectionId,ei.vSectionName "+
							" from tbEmpOfficialPersonalInfo ei inner join "+
							" tbEmpSalaryStructure es on ei.vEmployeeId = es.vEmployeeId where ei.vEmployeeId = '"+masterEmployeeId+"' and" +
							" es.isCurrent = 1";
					
					System.out.println("employeeUdTableData: "+employeeUdTableData);
					
					session.createSQLQuery(employeeUdTableData).executeUpdate();
					
					/*tbUdEmployeeInformation End*/
					
					

					/*tbSalaryIncrement Start*/
					sql="insert into tbSalaryIncrement(iSerialNo,dDate,vEmployeeId,employeeCode,vEmployeeName,"+
							"vUnitId,vUnitName,vDepartmentId,vDepartmentName,vDesignationId,vDesignationName,mIncrementPercentage,"+
							"mIncrementAmount,mNewGross,dJoiningDate,vEmployeeType,vServiceLength,mBasic,mHouseRent,"+
							"mMedicalAllowance,mMobile,mTotalSalary,"+
							"vNewDesignationId,vNewDesignationName,vNewEmployeeType,"+
							"mNewBasic,mNewHouseRent,mNewMedicalAllowance,mNewMobile,"+
							"vIncrementId,vIncrementType,vRemarks,vUserIP,vUserName,dEntryTime," +
							"mPFPercentage,mPFAmount,mNewTotalSalary,mNewPFPercentage,mNewPFAmount) "+
							"values("+txtSerialNo.getValue()+","+
							"'"+dFormatSql.format(dIncrementDate.getValue())+"',"+
							"'"+tblblempHidden.get(i).getValue()+"',"+
							"'"+tblblEmpID.get(i).getValue().toString()+"',"+
							"'"+tblblEmpName.get(i).getValue().toString()+"',"+
							"'"+cmbUnit.getValue()+"',"+
							"'"+cmbUnit.getItemCaption(cmbUnit.getValue().toString())+"',"+
							"'"+cmbDepartment.getValue()+"',"+
							"'"+cmbDepartment.getItemCaption(cmbDepartment.getValue().toString())+"',"+
							"'"+tblblDesignationId.get(i).getValue()+"',"+
							"'"+tblblDesignation.get(i).getValue().toString()+"'," +
							"0,"+
							"'"+tbtxtIncAmount.get(i).getValue().toString().replaceAll(",","")+"'," +
							"'"+tbtxtNewGross.get(i).getValue().toString().replaceAll(",","")+"',"+
							"'"+tblblJoiningDateSql.get(i).getValue()+"',"+
							"'"+tblblEmpType.get(i).getValue().toString()+"',"+
							"'"+tblblServiceLength.get(i).getValue().toString()+"',"+
							"'"+tblblBasic.get(i).getValue().toString().replaceAll(",","")+"',"+
							"'"+tblblHouseRent.get(i).getValue().toString().replaceAll(",","")+"',"+
							"'"+tblblMedicalAllowance.get(i).getValue().toString().replaceAll(",","")+"',"+
							"'"+tblblMobile.get(i).getValue().toString().replaceAll(",","")+"',"+
							"'"+tblblTotal.get(i).getValue().toString().replaceAll(",","")+"',"+
							"'"+tbCmbNewDesignation.get(i).getValue()+"',"+
							"'"+tbCmbNewDesignation.get(i).getItemCaption(tbCmbNewDesignation.get(i).getValue())+"',"+
							"'"+tbCmbNewEmpType.get(i).getItemCaption(tbCmbNewEmpType.get(i).getValue())+"',"+
							"'"+tbtxtNewBasic.get(i).getValue().toString().replaceAll(",","")+"',"+
							"'"+tbtxtNewHouseRent.get(i).getValue().toString().replaceAll(",","")+"',"+
							"'"+tbtxtNewMedicalAllowance.get(i).getValue().toString().replaceAll(",","")+"',"+
							"'"+tbtxtNewMobile.get(i).getValue().toString().replaceAll(",","")+"',"+
							"'"+cmbIncrementType.getValue()+"',"+
							"'"+cmbIncrementType.getItemCaption(cmbIncrementType.getValue())+"',"+
							"'Remarks',"+
							"'"+sessionBean.getUserIp()+"',"+
							"'"+sessionBean.getUserName()+"',"+
							"CURRENT_TIMESTAMP,'0','0','0','0','0' )";
					System.out.println("InsertData: "+sql);
					session.createSQLQuery(sql).executeUpdate();
					
					/*tbSalaryIncrement End*/
					
					
					
					

					/*tbUdSalaryIncrement Start*/
					sql="insert into tbUdSalaryIncrement(iSerialNo,dDate,vEmployeeId,employeeCode,vEmployeeName,"+
							"vUnitId,vUnitName,vDepartmentId,vDepartmentName,vDesignationId,vDesignationName,mIncrementPercentage,"+
							"mIncrementAmount,mNewGross,dJoiningDate,vEmployeeType,vServiceLength,mBasic,mHouseRent,"+
							"mMedicalAllowance,mMobile,mTotalSalary,"+
							"vNewDesignationId,vNewDesignationName,vNewEmployeeType,"+
							"mNewBasic,mNewHouseRent,mNewMedicalAllowance,mNewMobile,"+
							"vIncrementId,vIncrementType,vRemarks,vUserIP,vUserName,dEntryTime,vUdFlag," +
							"mPFPercentage,mPFAmount,mNewTotalSalary,mNewPFPercentage,mNewPFAmount) "+
							"values("+txtSerialNo.getValue()+","+
							"'"+dFormatSql.format(dIncrementDate.getValue())+"',"+
							"'"+tblblempHidden.get(i).getValue()+"',"+
							"'"+tblblEmpID.get(i).getValue().toString()+"',"+
							"'"+tblblEmpName.get(i).getValue().toString()+"',"+
							"'"+cmbUnit.getValue()+"',"+
							"'"+cmbUnit.getItemCaption(cmbUnit.getValue().toString())+"',"+
							"'"+cmbDepartment.getValue()+"',"+
							"'"+cmbDepartment.getItemCaption(cmbDepartment.getValue().toString())+"',"+
							"'"+tblblDesignationId.get(i).getValue()+"',"+
							"'"+tblblDesignation.get(i).getValue().toString()+"'," +
							"0,"+
							"'"+tbtxtIncAmount.get(i).getValue().toString().replaceAll(",","")+"'," +
							"'"+tbtxtNewGross.get(i).getValue().toString().replaceAll(",","")+"',"+
							"'"+tblblJoiningDateSql.get(i).getValue()+"',"+
							"'"+tblblEmpType.get(i).getValue().toString()+"',"+
							"'"+tblblServiceLength.get(i).getValue().toString()+"',"+
							"'"+tblblBasic.get(i).getValue().toString().replaceAll(",","")+"',"+
							"'"+tblblHouseRent.get(i).getValue().toString().replaceAll(",","")+"',"+
							"'"+tblblMedicalAllowance.get(i).getValue().toString().replaceAll(",","")+"',"+
							"'"+tblblMobile.get(i).getValue().toString().replaceAll(",","")+"',"+
							"'"+tblblTotal.get(i).getValue().toString().replaceAll(",","")+"',"+
							"'"+tbCmbNewDesignation.get(i).getValue()+"',"+
							"'"+tbCmbNewDesignation.get(i).getItemCaption(tbCmbNewDesignation.get(i).getValue())+"',"+
							"'"+tbCmbNewEmpType.get(i).getItemCaption(tbCmbNewEmpType.get(i).getValue())+"',"+
							"'"+tbtxtNewBasic.get(i).getValue().toString().replaceAll(",","")+"',"+
							"'"+tbtxtNewHouseRent.get(i).getValue().toString().replaceAll(",","")+"',"+
							"'"+tbtxtNewMedicalAllowance.get(i).getValue().toString().replaceAll(",","")+"',"+
							"'"+tbtxtNewMobile.get(i).getValue().toString().replaceAll(",","")+"',"+
							"'"+cmbIncrementType.getValue()+"',"+
							"'"+cmbIncrementType.getItemCaption(cmbIncrementType.getValue())+"',"+
							"'Remarks',"+
							"'"+sessionBean.getUserIp()+"',"+
							"'"+sessionBean.getUserName()+"',"+
							"CURRENT_TIMESTAMP,'"+uDFlag+"','0','0','0','0','0' )";
					
					System.out.println("udDataInsert: "+sql);
					
					session.createSQLQuery(sql).executeUpdate();
					
					/*tbUdSalaryIncrement End*/
					
					

					/*tbEmpSalaryStructure Start*/

					String sqlUpdateSalaryTable="";
					sqlUpdateSalaryTable="update tbEmpSalaryStructure set "
							+ "dChangeDate='"+dFormatSql.format(dIncrementDate.getValue())+"',"
							+ "mBasic='"+tbtxtNewBasic.get(i).getValue().toString().replaceAll(",","")+"',"
							+ "mHouseRent='"+tbtxtNewHouseRent.get(i).getValue().toString().replaceAll(",","")+"',"
							+ "mMedicalAllowance='"+tbtxtNewMedicalAllowance.get(i).getValue().toString().replaceAll(",","")+"',"
							+ "mMobileAllowance='"+tbtxtNewMobile.get(i).getValue().toString().replaceAll(",","")+"',"
							+ "vUserName='"+sessionBean.getUserName()+"', "
							+ "vUserIp='"+sessionBean.getUserIp()+"', "
							+ "dEntryTime=CURRENT_TIMESTAMP "
							+ "where vEmployeeId='"+masterEmployeeId+"'";
					
					System.out.println("sqlUpdateSalaryTable: "+sqlUpdateSalaryTable);
					
					session.createSQLQuery(sqlUpdateSalaryTable).executeUpdate();

					/*tbEmpSalaryStructure End*/
					

					/*tbEmpDesignationInfo and EmployeeType Start*/
					
					
					
					String updateType = "update tbEmpOfficialPersonalInfo set " +
							" vEmployeeType = '"+tbCmbNewEmpType.get(i).getValue()+"' " +
							" where vEmployeeId = '"+masterEmployeeId+"'";
					
					System.out.println("updateType: "+updateType);
					
					session.createSQLQuery(updateType).executeUpdate();

					/*tbEmpDesignationInfo and EmployeeType End*/
					
				}
			}
			
			tx.commit();
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
		finally{session.close();}
	}

	public boolean isDelete(){
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		String iSerialNo = txtSerialNo.getValue().toString();
		try
		{
			for(int i=0;i<tblblempHidden.size();i++)
			{
				if(!tblblempHidden.get(i).getValue().toString().isEmpty())
				{
					String deleteInfo = "delete from tbSalaryIncrement "
							+ "where iSerialNo = '"+iSerialNo+"' "
							+ "and vEmployeeId='"+tblblempHidden.get(i).getValue()+"' "
							+ "and vIncrementId='"+cmbIncrementType.getValue()+"' "
							+ "and YEAR(dDate)= YEAR('"+dFormatSql.format(dIncrementDate.getValue())+"') ";
					
					System.out.println("From isDelete");
					
					session.createSQLQuery(deleteInfo).executeUpdate();
				}
			}
			

			tx.commit();
		}
		catch (Exception e)
		{
			tx.rollback();
			showNotification("isDelete "+e,"",Notification.TYPE_WARNING_MESSAGE);
			return false;
		}
		finally{session.close();}
		return true;
	}


	private void transactionNo()
	{
		Transaction tx=null;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="select ISNULL(max(iSerialNo)+1,1) as serialNo from tbSalaryIncrement ";
			Iterator<?> iter=session.createSQLQuery(sql).list().iterator();
			int num=0;
			if(iter.hasNext())
			{
				num=Integer.parseInt(iter.next().toString());
				txtSerialNo.setValue(num);
	
			}
		}catch(Exception exp)
		{
			showNotification("",exp+"");
		}
	}
	public void calIncrementSalary(int ar)
	{

		double basic=0,house=0,medical=0,mobile=0;
		
		double incAmount=Double.parseDouble(tbtxtIncAmount.get(ar).getValue().toString().trim().replaceAll(",","").isEmpty()?"0":tbtxtIncAmount.get(ar).getValue().toString().trim().replaceAll(",",""));
		basic=Double.parseDouble(tblblBasic.get(ar).getValue().toString().trim().replaceAll(",","").isEmpty()?"0":tblblBasic.get(ar).getValue().toString().trim().replaceAll(",",""));
		house=Double.parseDouble(tblblHouseRent.get(ar).getValue().toString().trim().replaceAll(",","").isEmpty()?"0":tblblHouseRent.get(ar).getValue().toString().trim().replaceAll(",",""));
		medical=Double.parseDouble(tblblMedicalAllowance.get(ar).getValue().toString().trim().replaceAll(",","").isEmpty()?"0":tblblMedicalAllowance.get(ar).getValue().toString().trim().replaceAll(",",""));
		mobile=Double.parseDouble(tblblMobile.get(ar).getValue().toString().trim().replaceAll(",","").isEmpty()?"0":tblblMobile.get(ar).getValue().toString().trim().replaceAll(",",""));
		if(incAmount>=0)
		{
			basic=basic+incAmount;
			
			tbtxtNewBasic.get(ar).setValue(new CommaSeparator().setComma(basic));
			tbtxtNewHouseRent.get(ar).setValue(new CommaSeparator().setComma(house));
			tbtxtNewMedicalAllowance.get(ar).setValue(new CommaSeparator().setComma(medical));
			tbtxtNewMobile.get(ar).setValue(new CommaSeparator().setComma(mobile));
		}
		
	}
	private int checkEmpty(){
		int count=0;
		for(int a=0;a<tblblempHidden.size();a++){
			if(!tblblempHidden.get(a).getValue().toString().isEmpty()){
				count=a;
			}
		}
		return count;
	}
	private boolean checkValidation(){
		boolean ret=false;
		if(cmbIncrementType.getValue()!=null)
		{
			if(cmbDepartment.getValue()!=null)
			{
				for(int i=0; i<tblblempHidden.size();i++)
				{
					if(!tblblempHidden.get(i).getValue().toString().isEmpty())
					{
						if(tbCmbNewDesignation.get(i).getValue()!=null)
						{
							if(tbCmbNewEmpType.get(i).getValue()!=null)
							{
								if(!tbtxtIncAmount.get(i).getValue().toString().isEmpty())
								{
									if(!tbtxtNewGross.get(i).getValue().toString().isEmpty())
									{
										if(i==checkEmpty()){
											ret= true;
											break;
										}
									}
									else
									{
										getParent().showNotification("Warning","Please insert New Gross!",Notification.TYPE_WARNING_MESSAGE);
										tbtxtNewGross.get(i).focus();
										break;
									}
								}
								else
								{
									getParent().showNotification("Warning","Please insert Increment Amount!",Notification.TYPE_WARNING_MESSAGE);
									tbtxtIncAmount.get(i).focus();
									break;
								}
							}
							else
							{
								getParent().showNotification("Warning","Please Select New Employee Type!",Notification.TYPE_WARNING_MESSAGE);
								tbCmbNewEmpType.get(i).focus();
								break;
							}
						}
						else
						{
							getParent().showNotification("Warning","Please Select New Designation!",Notification.TYPE_WARNING_MESSAGE);
							tbCmbNewDesignation.get(i).focus();
							break;
						}
					}
					else
					{
						getParent().showNotification("Warning","No data in this table!",Notification.TYPE_WARNING_MESSAGE);
						break;
					}
				}
				
			}
			else{
				getParent().showNotification("Warning","Please Select Department",Notification.TYPE_WARNING_MESSAGE);
				cmbDepartment.focus();
			}
		}
		else{
			getParent().showNotification("Warning","Please Select Increment Type",Notification.TYPE_WARNING_MESSAGE);
			cmbIncrementType.focus();
		}
		return ret;
	}
	private void cmbDepartmentData(String unitId) 
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql="select distinct vDepartmentId,vDepartmentName from tbEmpOfficialPersonalInfo where vUnitId='"+unitId+"' order by vDepartmentName ";
			System.out.println("cmbDepartmentData: "+sql);
			List <?> list=session.createSQLQuery(sql).list();

			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbDepartment.addItem(element[0]);
				cmbDepartment.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbDepartmentData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	
	private void cmbIncrementTypeDataLoad(){
		cmbIncrementType.removeAllItems();
		String sql="select vIncrementId,vIncrementType from tbIncrementName order by vIncrementType";
		Iterator<?> iter=dbService(sql);
		while(iter.hasNext()){
			Object[] element=(Object[])iter.next();
			cmbIncrementType.addItem(element[0]);
			cmbIncrementType.setItemCaption(element[0], element[1].toString());
		}
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
	private void txtClear(){
		cmbUnit.setValue(null);
		cmbDepartment.setValue(null);
		cmbIncrementType.setValue(null);
		cmbEmployee.setValue(null);
		chkEmployeeAll.setValue(false);

	}
	private void componentIni(boolean a){
		cmbUnit.setEnabled(!a);
		cmbDepartment.setEnabled(!a);
		cmbIncrementType.setEnabled(!a);
		dIncrementDate.setEnabled(!a);
		nbIncrementType.setEnabled(!a);
		table.setEnabled(!a);
		chkEmployeeAll.setEnabled(!a);
		checkDepartmentAll.setEnabled(!a);
		txtSerialNo.setEnabled(!a);
		cmbEmployee.setEnabled(!a);
	}

	private void btnIni(boolean b) {
		cButton.btnNew.setEnabled(b);
		cButton.btnEdit.setEnabled(b);
		cButton.btnSave.setEnabled(!b);
		cButton.btnRefresh.setEnabled(!b);
		cButton.btnFind.setEnabled(b);
		cButton.btnExit.setEnabled(b);

	}
	private void buildMainLayout(){
		layoutMain=new AbsoluteLayout();
		layoutMain.setWidth("1100px");
		layoutMain.setHeight("550px");
		
		dIncrementDate=new PopupDateField();
		dIncrementDate.setDateFormat("dd-MM-yyyy");
		dIncrementDate.setValue(new Date());
		dIncrementDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dIncrementDate.setImmediate(true);
		dIncrementDate.setWidth("110px");
		layoutMain.addComponent(new Label("Effective Date:"),"top:10px; left:25px");
		layoutMain.addComponent(dIncrementDate,"top:08px; left:120px");

		cmbUnit=new ComboBox();
		cmbUnit.setWidth("250px");
		cmbUnit.setImmediate(true);
		cmbUnit.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		layoutMain.addComponent(new Label("Project :"),"top:35px; left:25px");
		layoutMain.addComponent(cmbUnit,"top:33px; left:120px");

		cmbDepartment=new ComboBox();
		cmbDepartment.setWidth("250px");
		cmbDepartment.setImmediate(true);
		cmbDepartment.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		layoutMain.addComponent(new Label("Department :"),"top:60px; left:25px");
		layoutMain.addComponent(cmbDepartment,"top:58px; left:120px");
		checkDepartmentAll=new CheckBox("All");
		checkDepartmentAll.setImmediate(true);

		txtSerialNo = new TextRead();
		txtSerialNo.setImmediate(true);
		txtSerialNo.setWidth("60px");
		txtSerialNo.setHeight("24px");
		layoutMain.addComponent(new Label("TransactionNo :"),"top:20px; left:870px");
		layoutMain.addComponent(txtSerialNo, "top:18px;left:960px;");
		
		cmbIncrementType=new ComboBox();
		cmbIncrementType.setWidth("250px");
		cmbIncrementType.setImmediate(true);
		layoutMain.addComponent(new Label("Increment Type :"),"top:10px; left:398px");
		layoutMain.addComponent(cmbIncrementType,"top:08px; left:490px");

		nbIncrementType = new NativeButton();
		nbIncrementType.setIcon(new ThemeResource("../icons/add.png"));
		nbIncrementType.setImmediate(true);
		nbIncrementType.setWidth("32px");
		nbIncrementType.setHeight("21px");
		layoutMain.addComponent(nbIncrementType,"top:08px;left:745px;");

		lblEmployee = new Label("Employee :"); 
		cmbEmployee=new ComboBox();
		cmbEmployee.setImmediate(true);
		cmbEmployee.setWidth("250px");
		layoutMain.addComponent(lblEmployee,"top:35px; left:398px");
		layoutMain.addComponent(cmbEmployee,"top:33px; left:490px");
		
		chkEmployeeAll=new CheckBox("All");
		chkEmployeeAll.setImmediate(true);
		layoutMain.addComponent(chkEmployeeAll,"top:33px; left:745px");

		table=new Table();
		table.setWidth("99%");
		table.setHeight("400px");
		table.addContainerProperty("Sl", Label.class, new Label());
		table.setColumnWidth("Sl", 20);

		table.addContainerProperty("Remove", NativeButton.class, new NativeButton());
		table.setColumnWidth("Remove", 45);

		table.addContainerProperty("emp id", Label.class, new Label());
		table.setColumnWidth("emp id", 40);


		table.addContainerProperty("Employee ID", Label.class, new Label());
		table.setColumnWidth("Employee ID", 60);

		table.addContainerProperty("Name", Label.class, new Label());
		table.setColumnWidth("Name", 170);

		table.addContainerProperty("designation ID", Label.class, new Label());
		table.setColumnWidth("designation ID", 50);

		table.addContainerProperty("Designation", Label.class, new Label());
		table.setColumnWidth("Designation", 120);

		table.addContainerProperty("Joining Date", Label.class, new Label());
		table.setColumnWidth("Joining Date", 70);

		table.addContainerProperty("Joining", Label.class, new Label());
		table.setColumnWidth("Joining", 70);

		table.addContainerProperty("Employee Type", Label.class, new Label());
		table.setColumnWidth("Employee Type", 70);

		table.addContainerProperty("Service Length", Label.class, new Label());
		table.setColumnWidth("Service Length", 60);

		table.addContainerProperty("Basic", Label.class, new Label());
		table.setColumnWidth("Basic",60);

		table.addContainerProperty("HR", Label.class, new Label());
		table.setColumnWidth("HR", 60);

		table.addContainerProperty("Medical", Label.class, new Label());
		table.setColumnWidth("Medical", 45);

		table.addContainerProperty("Mobile", Label.class, new Label());
		table.setColumnWidth("Mobile",60);

		table.addContainerProperty("Present Gross", Label.class, new Label());
		table.setColumnWidth("Present Gross", 70);

		table.addContainerProperty("Increment Amount", AmountField.class, new AmountField());
		table.setColumnWidth("Increment Amount", 50);

		table.addContainerProperty("new Gross", AmountField.class, new AmountField());
		table.setColumnWidth("new Gross", 60);

		//New Status
		table.addContainerProperty("employee ID", Label.class, new Label());
		table.setColumnWidth("employee ID", 70);

		table.addContainerProperty("Employee Name", Label.class, new Label());
		table.setColumnWidth("Employee Name", 170);

		table.addContainerProperty("designation", ComboBox.class, new ComboBox());
		table.setColumnWidth("designation", 160);

		table.addContainerProperty("employee type", ComboBox.class, new ComboBox());
		table.setColumnWidth("employee type", 100);


		table.addContainerProperty("New Basic",  AmountField.class, new AmountField());
		table.setColumnWidth("New Basic", 60);

		table.addContainerProperty("New HR",  AmountField.class, new AmountField());
		table.setColumnWidth("New HR",60);

		table.addContainerProperty("New Med.",  AmountField.class, new AmountField());
		table.setColumnWidth("New Med.",60);

		table.addContainerProperty("New Conv.",  AmountField.class, new AmountField());
		table.setColumnWidth("New Conv.", 60);

		table.setColumnCollapsingAllowed(true);
		table.setColumnCollapsed("employee ID",true);
		table.setColumnCollapsed("Joining",true);
		table.setColumnCollapsed("Employee Name",true);
		table.setColumnCollapsed("emp id", true);
		table.setColumnCollapsed("designation ID", true);
		table.setStyleName("wordwrap-headers");
		layoutMain.addComponent(table,"top:100px; left:5px; right:5px");
		tableinitialize();
		layoutMain.addComponent(cButton,"top:510px; left:300px");
		cButton.setImmediate(true);
	}
}
