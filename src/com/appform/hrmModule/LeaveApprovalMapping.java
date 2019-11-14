package com.appform.hrmModule;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.*;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class LeaveApprovalMapping extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private CommonButton cButton = new CommonButton("New", "Save", "Edit","", "Refresh", "Find", "", "", "", "Exit");


	private ComboBox cmbDepartment,cmbDeignation,cmbEmployee,cmbDesignationPrimary,cmbDesignationFinal,cmbUnit,cmbFind,cmbDesignationHR;


	private boolean isUpdate=false;

	private TextField txtreceiptID = new TextField();
	int flag=1;
	private boolean isFind=false;
	Label labelTransaction=new Label();
	private CommonMethod cm;
	private String menuId = "";
	public LeaveApprovalMapping(SessionBean sessionBean,String menuId)
	{
		this.sessionBean = sessionBean;
		this.setCaption("LEAVE APPROVAL MAPPING :: "+sessionBean.getCompany());
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		this.setWidth("500px");
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);
		btnIni(true);
		componentIni(true);
		setEventAction();
		authenticationCheck();
		cmbUnit();
		cmbDepartment();
		cmbDesignation();
		cmbDesignationHR();
		cmbDesignationFinal();
		cmbTransaction();
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
	private void cmbUnit()
	{

		cmbUnit.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "select distinct vUnitId,vUnitName from tbEmpOfficialPersonalInfo where bStatus=1 order by vUnitName";
			List<?> list = session.createSQLQuery(sql).list();
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
	private void cmbDepartment()
	{

		cmbDepartment.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "select distinct vDepartmentId,vDepartmentName from tbEmpOfficialPersonalInfo "
					+ " where bStatus=1 "
					+ " and vUnitId like '"+(cmbUnit.getValue()==null?"%":cmbUnit.getValue())+"' "
					+ " order by vDepartmentName";
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator();iter.hasNext();)
			{
				Object element[] = (Object[]) iter.next();
				cmbDepartment.addItem(element[0].toString());
				cmbDepartment.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	
	}
	private void cmbDesignation()
	{
		cmbDesignationPrimary.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try{
			String sql="select distinct vDesignationId,vDesignationName from tbEmpOfficialPersonalInfo  "
					+ " where  vUnitId like '"+(cmbUnit.getValue()==null?"%":cmbUnit.getValue())+"' "
					+ " and vDepartmentId like '"+(cmbDepartment.getValue()==null?"%":cmbDepartment.getValue())+"'  "
					+ " and vDesignationId not in(select vDesignationIdPrimary from tbLeaveApprovalMapping) "
					+ " order by vDesignationName";
			Iterator<?> iter=session.createSQLQuery(sql).list().iterator();
			System.out.println("Designation :"+sql);
			while(iter.hasNext())
			{
				Object[] element=(Object[])iter.next();
				cmbDesignationPrimary.addItem(element[0]);
				cmbDesignationPrimary.setItemCaption(element[0], element[1].toString());
			}
		}catch(Exception exp)
		{
			showNotification("Error..to Designation Load",""+exp,Notification.TYPE_ERROR_MESSAGE);
		}
		finally{
			session.close();
		}
	}
	private void cmbDesignationFind()
	{
		cmbDesignationPrimary.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try{
			String sql="select distinct vDesignationId,vDesignationName from tbEmpOfficialPersonalInfo  "
					+ " where  vUnitId like '"+(cmbUnit.getValue()==null?"%":cmbUnit.getValue())+"' "
					+ " and vDepartmentId like '"+(cmbDepartment.getValue()==null?"%":cmbDepartment.getValue())+"'  "
					+ " order by vDesignationName";
			Iterator<?> iter=session.createSQLQuery(sql).list().iterator();
			System.out.println("Designation :"+sql);
			while(iter.hasNext())
			{
				Object[] element=(Object[])iter.next();
				cmbDesignationPrimary.addItem(element[0].toString());
				cmbDesignationPrimary.setItemCaption(element[0].toString(), element[1].toString());
			}
		}catch(Exception exp)
		{
			showNotification("Error..to Designation Load",""+exp,Notification.TYPE_ERROR_MESSAGE);
		}
		finally{
			session.close();
		}
	}
	private void cmbDesignationHR()
	{

		cmbDesignationHR.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try{
			String sql="select distinct vDesignationId,vDesignationName from tbEmpOfficialPersonalInfo order by vDesignationName";
			Iterator<?> iter=session.createSQLQuery(sql).list().iterator();
			System.out.println("cmbDesignationHR :"+sql);
			while(iter.hasNext())
			{
				Object[] element=(Object[])iter.next();
				cmbDesignationHR.addItem(element[0]);
				cmbDesignationHR.setItemCaption(element[0], element[1].toString());
			}
		}catch(Exception exp)
		{
			showNotification("Error..to cmbDesignationHR  Load",""+exp,Notification.TYPE_ERROR_MESSAGE);
		}
		finally{
			session.close();
		}
	
	}
	private void cmbDesignationFinal()
	{

		cmbDesignationFinal.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try{
			String sql="select distinct vDesignationId,vDesignationName from tbEmpOfficialPersonalInfo order by vDesignationName";
			Iterator<?> iter=session.createSQLQuery(sql).list().iterator();
			System.out.println("Designation :"+sql);
			while(iter.hasNext())
			{
				Object[] element=(Object[])iter.next();
				cmbDesignationFinal.addItem(element[0]);
				cmbDesignationFinal.setItemCaption(element[0], element[1].toString());
			}
		}catch(Exception exp)
		{
			showNotification("Error..to Designation Final Load",""+exp,Notification.TYPE_ERROR_MESSAGE);
		}
		finally{
			session.close();
		}
	
	}
	private void cmbTransaction()
	{

		cmbFind.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try{
			String sql="select distinct vTransactionId from tbLeaveApprovalMapping";
			Iterator<?> iter=session.createSQLQuery(sql).list().iterator();
			System.out.println("cmbTransaction :"+sql);
			while(iter.hasNext())
			{
				cmbFind.addItem(iter.next().toString());
			}
		}catch(Exception exp)
		{
			showNotification("Error..to cmbTransaction Final Load",""+exp,Notification.TYPE_ERROR_MESSAGE);
		}
		finally{
			session.close();
		}
	
	}

	private void setEventAction()
	{
		cButton.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isFind = false;
				isUpdate = false;

				componentIni(false);
				btnIni(false);
				labelTransaction.setValue(autoId());
				txtClear();
				cmbFind.setVisible(false);
			}
		});

		cButton.btnSave.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbUnit.getValue()!=null)
				{
					if(cmbDepartment.getValue()!=null)
					{
						if(cmbDesignationPrimary.getValue()!=null)
						{
							if(cmbDesignationFinal.getValue()!=null)
							{
								saveBtnAction();
							}
							else
							{
								showNotification("Please select Final Approval!");
							}
						}
						else
						{
							showNotification("Please select Primary Approval!");
						}
					}
					else
					{
						showNotification("Please select Department!");
					}
				}
				else
				{
					showNotification("Please select Project!");
				}
			}
		});

		cButton.btnEdit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isFind = false;
				isUpdate=true;
				componentIni(false);
				btnIni(false);
				cmbFind.setVisible(false);
			}
		});

		cButton.btnRefresh.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isFind = false;
				isUpdate=false;
				txtClear();
				componentIni(true);
				btnIni(true);
				cmbFind.setVisible(false);
			}
		});

		cButton.btnFind.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isFind = true;
				findbuttonEvent();
			}
		});

		cButton.btnExit.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				close();
			}
		});
		cmbUnit.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
				cmbDepartment();
			}
		});
		cmbDepartment.addListener(new ValueChangeListener() {
					
		public void valueChange(ValueChangeEvent event) {
				
			cmbDesignation();
			cmbDesignationFinal();
			if(isFind)
			{
				cmbDesignationFind();
			}
		   }
	    });
		
	}
	private void saveBtnAction()
	{
		MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, (isUpdate==true?"Do you want to update Information?":"Do you want to save Information?"), new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
		mb.setStyleName("cwindowMB");
		mb.show(new EventListener()
		{
			public void buttonClicked(ButtonType buttonType)
			{
				if(buttonType == ButtonType.YES)
				{
					Session session=SessionFactoryUtil.getInstance().openSession();
					Transaction tx=session.beginTransaction();
					insertData(session,tx);
					componentIni(true) ;
					btnIni(true);
					txtClear();
					isUpdate=false;
					isFind=false;
					cmbFind.setVisible(false);
					cmbTransaction();
					Notification n=new Notification("All Information "+(isUpdate?"Updated":"Save")+" Successfully!","",Notification.TYPE_TRAY_NOTIFICATION);
					n.setPosition(Notification.POSITION_TOP_RIGHT);
					showNotification(n);
				}
			}
		});
	
	}
	private void findbuttonEvent()
	{
		Window win = new LeaveApprovalMappingFind(sessionBean, txtreceiptID);
		win.addListener(new CloseListener()
		{
			public void windowClose(CloseEvent e)
			{
				if(!txtreceiptID.getValue().toString().trim().isEmpty())
				{
					txtClear();
					findData(txtreceiptID.getValue().toString());
				}
			}
		});
		this.getParent().addWindow(win);
	}
	private void findData(String findId)
	{

		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try{
			String sql="select vTransactionId,vUnitId,vDepartmentId,vDesignationIdPrimary,vDesignationIdFinal,vDesignationIdHR from tbLeaveApprovalMapping where vTransactionId='"+findId+"'";
			Iterator<?> iter=session.createSQLQuery(sql).list().iterator();
			System.out.println("findData :"+sql);
			while(iter.hasNext())
			{
				Object[] element=(Object[])iter.next();
				labelTransaction.setValue(element[0]);
				cmbUnit.setValue(element[1]);
				cmbDepartment.setValue(element[2]);
				cmbDesignationPrimary.setValue(element[3].toString());
				cmbDesignationFinal.setValue(element[4]);
				cmbDesignationHR.setValue(element[5]);
				
			}
		}catch(Exception exp)
		{
			showNotification("Error..to findData Final Load",""+exp,Notification.TYPE_ERROR_MESSAGE);
		}
		finally{
			session.close();
		}
	
	}
	private void insertData(Session session,Transaction tx)
	{
		try{
			
			String autoId=autoId();
			if(isUpdate)
			{
				autoId=labelTransaction.getValue().toString();
				String sql="delete from tbLeaveApprovalMapping where vDepartmentId='"+(cmbDepartment.getValue()==null?"":cmbDepartment.getValue())+"'";
				session.createSQLQuery(sql).executeUpdate();
			}
			
			String sql="insert into tbLeaveApprovalMapping(vTransactionId,vDepartmentId,vDepartmentName,vUnitId,vUnitName,vDesignationIdPrimary,"+
			" vDesignationNamePrimary,vDesignationIdFinal,vDesignationNameFinal,vUserId,vUserName,vUserIp,dEntryTime,vDesignationIdHR,vDesignationNameHR) "+
			" values('"+autoId+"','"+cmbDepartment.getValue()+"', "+
			" '"+cmbDepartment.getItemCaption(cmbDepartment.getValue())+"','"+cmbUnit.getValue()+"', "+
			" '"+cmbUnit.getItemCaption(cmbUnit.getValue())+"','"+cmbDesignationPrimary.getValue()+"', "+
			" '"+cmbDesignationPrimary.getItemCaption(cmbDesignationPrimary.getValue())+"','"+(cmbDesignationFinal.getValue()==null?"":cmbDesignationFinal.getValue())+"', "+
			" '"+(cmbDesignationFinal.getValue()==null?"":cmbDesignationFinal.getItemCaption(cmbDesignationFinal.getValue()))+"','"+sessionBean.getUserId()+"', "+
			" '"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',GETDATE(),"+
			" '"+cmbDesignationHR.getValue()+"','"+cmbDesignationHR.getItemCaption(cmbDesignationHR.getValue())+"')";
			
			session.createSQLQuery(sql).executeUpdate();
			tx.commit();
		}catch(Exception exp)
		{
			tx.rollback();
			showNotification(""+exp,Notification.TYPE_ERROR_MESSAGE);
		}
		finally{
			session.close();
		}
	}
	private String autoId()
	{
		String ret="";
		Session session=SessionFactoryUtil.getInstance().openSession();
		try{
			String sql="select ISNULL(MAX(CONVERT(int,vTransactionId)),CAST(CONVERT(varchar,RIGHT(YEAR(getDate()),2))+'0000' as bigint))+1 from tbLeaveApprovalMapping";
			Iterator<?> iter=session.createSQLQuery(sql).list().iterator();
			if(iter.hasNext())
			{
				ret=iter.next().toString();
			}
		
		}
		catch(Exception exp)
		{
			showNotification(""+exp,Notification.TYPE_ERROR_MESSAGE);
		}
		finally{
			session.close();
		}
		return ret;
	}
	private void componentIni(boolean b) 
	{
		cmbDepartment.setEnabled(!b);
		cmbUnit.setEnabled(!b);
		cmbDesignationPrimary.setEnabled(!b);
		cmbDesignationFinal.setEnabled(!b);
		cmbDesignationHR.setEnabled(!b);
		
	}
	private void txtClear()
	{
		cmbDepartment.setValue(null);
		cmbUnit.setValue(null);
		cmbDesignationPrimary.setValue(null);
		cmbDesignationFinal.setValue(null);
		cmbFind.setValue(null);
		cmbDesignationHR.setValue(null);
	}
	private void btnIni(boolean t) 
	{
		cButton.btnNew.setEnabled(t);
		cButton.btnEdit.setEnabled(t);
		cButton.btnSave.setEnabled(!t);
		cButton.btnRefresh.setEnabled(!t);
		cButton.btnDelete.setEnabled(t);
		cButton.btnFind.setEnabled(t);
	}

	private AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);

		setWidth("600px");
		setHeight("280px");

		cmbUnit=new ComboBox();
		cmbUnit.setWidth("250px");
		cmbUnit.setHeight("-1px");
		cmbUnit.setImmediate(true);
		cmbUnit.setNullSelectionAllowed(false);
		mainLayout.addComponent(new Label("Project :"),"top:20px; left:30px");
		mainLayout.addComponent(cmbUnit,"top:18px; left:150px");
		
		cmbFind=new ComboBox();
		cmbFind.setWidth("100px");
		cmbFind.setHeight("-1px");
		cmbFind.setImmediate(true);
		cmbFind.setNullSelectionAllowed(false);
		cmbFind.setCaption("Transaction :");
		mainLayout.addComponent(cmbFind,"top:18px; left:415px");
		cmbFind.setVisible(false);
		
		cmbDepartment=new ComboBox();
		cmbDepartment.setWidth("250px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setImmediate(true);
		cmbDepartment.setNullSelectionAllowed(false);
		mainLayout.addComponent(new Label("Department :"),"top:50px; left:30px");
		mainLayout.addComponent(cmbDepartment,"top:48px; left:150px");
		
		labelTransaction=new Label();
		labelTransaction.setWidth("-1px");
		labelTransaction.setHeight("-1px");
		mainLayout.addComponent(labelTransaction,"top:50px; left:420");
		labelTransaction.setVisible(false);
		
		cmbDesignationPrimary=new ComboBox();
		cmbDesignationPrimary.setWidth("250px");
		cmbDesignationPrimary.setHeight("-1px");
		cmbDesignationPrimary.setImmediate(true);
		cmbDesignationPrimary.setNullSelectionAllowed(false);
		mainLayout.addComponent(new Label("Primary Approve :"),"top:80px; left:30px");
		mainLayout.addComponent(cmbDesignationPrimary,"top:78px; left:150px");
		
		cmbDesignationHR=new ComboBox();
		cmbDesignationHR.setWidth("250px");
		cmbDesignationHR.setHeight("-1px");
		cmbDesignationHR.setImmediate(true);
		cmbDesignationHR.setNullSelectionAllowed(false);
		mainLayout.addComponent(new Label("HR Approve :"),"top:110px; left:30px");
		mainLayout.addComponent(cmbDesignationHR,"top:108px; left:150px");
		
		
		
		cmbDesignationFinal=new ComboBox();
		cmbDesignationFinal.setWidth("250px");
		cmbDesignationFinal.setHeight("-1px");
		cmbDesignationFinal.setImmediate(true);
		cmbDesignationFinal.setNullSelectionAllowed(false);
		mainLayout.addComponent(new Label("Final Approve :"),"top:140px; left:30px");
		mainLayout.addComponent(cmbDesignationFinal,"top:138px; left:150px");
		
		

		mainLayout.addComponent(cButton, "bottom:15px;left:40px;");

		return mainLayout;
	}
}
