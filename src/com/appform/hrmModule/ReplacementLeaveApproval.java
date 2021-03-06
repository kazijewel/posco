package com.appform.hrmModule;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Transaction;
import org.hibernate.classic.Session;

import com.common.share.CommonButton;
import com.common.share.CommonMethod;
import com.common.share.MessageBox;
import com.common.share.ReportDate;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table.HeaderClickEvent;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class ReplacementLeaveApproval extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;
	private ComboBox cmbUnit;
	private ComboBox cmbDepartment;
	private CheckBox chkDepartmentAll;
	private ComboBox cmbEmployee;
	private CheckBox chkEmployeeAll;

	CommonButton btnFind = new CommonButton("", "", "", "", "", "Find", "", "", "", "");

	private Table table = new Table();
	private ArrayList<Label> tbLblReference = new ArrayList<Label>();
	private ArrayList<Label> tbLblEmployeeId = new ArrayList<Label>();
	private ArrayList<Label> tbLblEmployeeCode = new ArrayList<Label>();
	private ArrayList<Label> tbLblEmployeeName = new ArrayList<Label>();
	private ArrayList<Label> tbLblDesignation = new ArrayList<Label>();
	private ArrayList<Label> tbLblUnitName = new ArrayList<Label>();
	private ArrayList<Label> tbLblDivisionName = new ArrayList<Label>();
	private ArrayList<PopupDateField> tbdLeaveFrom = new ArrayList<PopupDateField>();
	private ArrayList<PopupDateField> tbdLeaveTo = new ArrayList<PopupDateField>();
	private ArrayList<PopupDateField> tbdLeaveF = new ArrayList<PopupDateField>();
	private ArrayList<PopupDateField> tbdLeaveT = new ArrayList<PopupDateField>();
	private ArrayList<TextRead> tbTxtTotalDays = new ArrayList<TextRead>();
	private ArrayList<TextRead> tbTxtApproveDays = new ArrayList<TextRead>();
	private ArrayList<PopupDateField> tbdApplicationDate = new ArrayList<PopupDateField>();
	private ArrayList<NativeButton> tbBtnDetails = new ArrayList<NativeButton>();
	private ArrayList<CheckBox> tbChkSelect = new ArrayList<CheckBox>();

	private CheckBox chkAll=new CheckBox("Select All");
	CommonButton cButton = new CommonButton("New", "Save", "", "", "Refresh", "", "", "", "", "Exit");
	private CommonMethod cm;
	private String menuId = "";
	private boolean click = true;

	public ReplacementLeaveApproval( SessionBean sessionBean,String menuId)
	{
		this.sessionBean=sessionBean;
		this.setCaption("REPLACEMENT LEAVE APPROVAL :: "+sessionBean.getCompany());
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		Buildmainlayout();
		setContent(mainLayout);
		tableinitialise();
		txtInit(true);
		btnIni(true);
		setEventAction();
		cmbUnitDataAdd();
		authenticationCheck();
		cButton.btnNew.focus();
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

	private void setEventAction()
	{
		cmbUnit.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbUnit.getValue()!=null)
				{
					 cmbDepartmentDataAdd();
				}
				
			}
		});
		cmbDepartment.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbDepartment.getValue()!=null)
				{
					cmbEmployeeDataLoad();
				}
			}
		});
		chkDepartmentAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(chkDepartmentAll.booleanValue())
				{
					cmbEmployeeDataLoad();
					cmbDepartment.setValue(null);
					cmbDepartment.setEnabled(false);
				}
				else
				{
					cmbDepartment.setEnabled(true);
				}
			}
		});
		chkEmployeeAll.addListener(new ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(chkEmployeeAll.booleanValue())
				{
					if(cmbUnit.getValue()!=null)
					{
						if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
						{
							cmbEmployee.setValue(null);
							cmbEmployee.setEnabled(false);
						}
					}
				}
				else{
					cmbEmployee.setEnabled(true);
				}
			}
		});
		chkAll.addListener(new ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				if(chkAll.booleanValue())
				{
					for(int i=0;i<tbLblEmployeeId.size();i++)
					{
						if(!tbLblEmployeeId.get(i).getValue().toString().isEmpty())
						{
							tbChkSelect.get(i).setValue(true);
						}
					}
				}
				else
				{
					for(int i=0;i<tbLblEmployeeId.size();i++)
					{
						if(!tbLblEmployeeId.get(i).getValue().toString().isEmpty())
						{
							tbChkSelect.get(i).setValue(false);
						}
					}
				}
			}
		});

		btnFind.btnFind.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
				{
					addTableData();
				}
				
			}
		});

		table.addListener(new Table.HeaderClickListener() 
		{
			public void headerClick(HeaderClickEvent event) 
			{
				if(event.getPropertyId().toString() == "Approved")
				{
					if(click)
					{
						approveAll(true);
						click = false;
					}
					else
					{
						approveAll(false);
						click = true;
					}
				}
			}
		});

		cButton.btnNew.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				txtClear();
				txtInit(false);
				btnIni(false);			
				cmbUnit.focus();				
				cmbDepartment.focus();
				cmbUnitDataAdd();
				
			}
		});

		cButton.btnSave.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				formValidation();
			}
		});

		cButton.btnRefresh.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				txtClear();
				txtInit(true);
				btnIni(true);
				cmbUnit.setEnabled(false);
				cmbDepartment.setEnabled(false);
			}
		});

		cButton.btnExit.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				close();
			}
		});
	}
	
	private void cmbUnitDataAdd()
	{
		cmbUnit.removeAllItems();
	
		
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{

			
			String query=" select epo.vUnitId,epo.vUnitName from tbEmpOfficialPersonalInfo epo inner join "
					+ " tbReplacementLeaveApplication b on epo.vEmployeeId=b.vEmployeeId  where b.iFinal='0' "
					+ " order by epo.vUnitName";
			
			
					
					System.out.println("UNIT"+query);
			
			List <?> lst=session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				for(Iterator <?> itr=lst.iterator();itr.hasNext();)
				{
					Object [] element=(Object[])itr.next();
					cmbUnit.addItem(element[0]);
					cmbUnit.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch (Exception exp)
		{
			showNotification("cmbUnitDataAdd", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void cmbEmployeeDataLoad()
	{
	    cmbEmployee.removeAllItems();

		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select epo.vEmployeeId,epo.vEmployeeCode,epo.vEmployeeName from tbEmpOfficialPersonalInfo epo inner join tbReplacementLeaveApplication "
			+ " b on epo.vEmployeeId=b.vEmployeeId  where epo.vUnitId='"+cmbUnit.getValue().toString()+"' and  b.iFinal='0' "
			+ " order by epo.vEmployeeName";
	
		System.out.println("cmbEmployeeDataLoad: "+query);
		
		List <?> lst=session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				for(Iterator <?> itr=lst.iterator();itr.hasNext();)
				{
					Object [] element=(Object[])itr.next();
					cmbEmployee.addItem(element[0]);
					cmbEmployee.setItemCaption(element[0], element[1]+"-"+element[2]);
				}
			}
		}
		catch (Exception exp)
		{
			showNotification("cmbEmployeeDataLoad", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	
	private void cmbDepartmentDataAdd()
	{
	     cmbDepartment.removeAllItems();

		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query=" select epo.vDepartmentId,epo.vDepartmentName from tbEmpOfficialPersonalInfo epo inner join tbReplacementLeaveApplication "
			+ " b on epo.vEmployeeId=b.vEmployeeId  where epo.vUnitId='"+cmbUnit.getValue().toString()+"' and  b.iFinal='0' "
			+ " order by epo.vDepartmentName";
			
	
	System.out.println("Department"+query);
	
	List <?> lst=session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				for(Iterator <?> itr=lst.iterator();itr.hasNext();)
				{
					Object [] element=(Object[])itr.next();
					cmbDepartment.addItem(element[0]);
					cmbDepartment.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch (Exception exp)
		{
			showNotification("cmbDepartmentDataAdd", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void approveAll(boolean ret)
	{
		if(ret)
		{
			for(int i = 0; i<tbLblEmployeeName.size(); i++)
			{
				if(!tbLblEmployeeName.get(i).getValue().toString().isEmpty())
				{
					tbChkSelect.get(i).setValue(true);
				}
			}
		}
		else
		{
			for(int i = 0; i<tbLblEmployeeName.size(); i++)
			{
				tbChkSelect.get(i).setValue(false);
			}
		}
	}


	private void formValidation()
	{
		if(checkTableData())
		{
			MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to approve Replacement leave?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.setStyleName("cwindowMB");
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						updateData();
						txtInit(true);
						btnIni(true);
						
						Notification n=new Notification("Replacement Leave Approved Successfully!","",Notification.TYPE_TRAY_NOTIFICATION);
						n.setPosition(Notification.POSITION_TOP_RIGHT);
						showNotification(n);
						txtClear();
					}
				}
			});
		}
		else
		{
			showNotification("Warning!","Select data to approve in table.",Notification.TYPE_WARNING_MESSAGE);
		}
	}
	

	private void updateData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();

		try
		{
			for(int i = 0; i<tbLblEmployeeName.size(); i++)
			{
				if(tbChkSelect.get(i).booleanValue())
				{
					String transactionID=tbLblReference.get(i).getValue().toString();
					
					String queryUpdateData="insert into tbUDReplacementLeaveApplication ("
							+ "vTransactionID,dApplicationDate,vEmployeeID,vEmployeeCode,vEmployeeName,vDesignationID,vDesignationName,"
							+ "vDepartmentID,vDepartmentName,vSectionId,vSectionName,dJoiningDate,vMobileNo,dReplacementLeaveFrom,"
							+ "dReplacementLeaveTo,iTotalDays,vApprovedFlag,vPurposeOfLeave,vVisitingAddress,UDFlag,"
							+ "vUserName,vUserIP,dEntryTime"
							+ ") "
							+ "select vTransactionID,dApplicationDate,vEmployeeID,vEmployeeCode,vEmployeeName,vDesignationID,vDesignationName,"
							+ "vDepartmentID,vDepartmentName,vSectionId,vSectionName,dJoiningDate,vMobileNo,dReplacementLeaveFrom,"
							+ "dReplacementLeaveTo,iTotalDays,iFinal,vPurposeOfLeave,vVisitingAddress,'UPDATE',"
							+ "vUserName,vUserIP,dEntryTime from tbReplacementLeaveApplication "
							+ "where vTransactionID = '"+transactionID+"' ";
					session.createSQLQuery(queryUpdateData).executeUpdate();
					
					String updateInfo = " update tbReplacementLeaveApplication set " +
							" dReplacementLeaveFrom = '"+sessionBean.dfDb.format(tbdLeaveFrom.get(i).getValue())+"'," +
							" dReplacementLeaveTo = '"+sessionBean.dfDb.format(tbdLeaveTo.get(i).getValue())+"'," +
							" iTotalDays = '"+"0"+tbTxtApproveDays.get(i).getValue().toString()+"'," +
							" iFinal=1,"
							+ "vApprovedBy='"+sessionBean.getUserName()+"',"
							+ "vUserName='"+sessionBean.getUserName()+"',"
							+ "vUserIP='"+sessionBean.getUserIp()+"',dEntryTime=GETDATE() " +
							" where vTransactionID = '"+tbLblReference.get(i).getValue().toString()+"' ";
					session.createSQLQuery(updateInfo).executeUpdate();
				}
			}
			tx.commit();
		}
		catch (Exception e)
		{
			tx.rollback();
			showNotification("Warning!","Error to save application "+e,Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}

	private boolean checkTableData()
	{
		boolean ret = false;

		for(int i = 0; i<tbLblEmployeeName.size(); i++)
		{
			if(tbChkSelect.get(i).booleanValue())
			{
				ret = true;
				break;
			}
			else
			{
				ret = false;
			}
		}

		return ret;
	}

	private void addTableData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();

		try
		{
			String sql = "select vTransactionID,b.dApplicationDate,epo.vEmployeeName,epo.vDepartmentName,epo.vDesignationName,"
					+ "dReplacementLeaveFrom,dReplacementLeaveTo,iTotalDays,epo.vEmployeeId,epo.vEmployeeCode "
					+ "from tbReplacementLeaveApplication b "
					+ "inner join tbEmpOfficialPersonalInfo epo on epo.vEmployeeId=b.vEmployeeId "
					+" where epo.vUnitId like '"+(cmbUnit.getValue()==null?"%":cmbUnit.getValue().toString())+"' "
					+ "and epo.vDepartmentId like '"+(cmbDepartment.getValue()==null?"%":cmbDepartment.getValue().toString())+"'" 
					+ "and epo.vEmployeeId like '"+(cmbEmployee.getValue()==null?"%":cmbEmployee.getValue().toString())+"'" 
					+" and b.iFinal='0'  order by b.dApplicationDate";
			List <?> list = session.createSQLQuery(sql).list();
			System.out.println("Find Query :" + sql);
			tableClear();

			if(!list.isEmpty())
			{
				int i = 0;
				for (Iterator <?> iter = list.iterator(); iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();

					tbLblReference.get(i).setValue(element[0].toString());

					tbLblEmployeeId.get(i).setValue(element[8].toString());
					tbLblEmployeeCode.get(i).setValue(element[9].toString());
					tbLblEmployeeName.get(i).setValue(element[2].toString());
					tbLblDivisionName.get(i).setValue(element[3].toString());
					tbLblDesignation.get(i).setValue(element[4].toString());
					tbTxtTotalDays.get(i).setValue(sessionBean.dfInteger.format(element[7]));
					tbTxtApproveDays.get(i).setValue(sessionBean.dfInteger.format(element[7]));
					tbdLeaveFrom.get(i).setValue(element[5]);
					tbdLeaveTo.get(i).setValue(element[6]);
					tbdLeaveF.get(i).setValue(element[5]);
					tbdLeaveT.get(i).setValue(element[6]);
					tbdApplicationDate.get(i).setReadOnly(false);
					tbdApplicationDate.get(i).setValue(element[1]);
					tbdApplicationDate.get(i).setReadOnly(true);

					if(tbLblEmployeeName.size()-1==i)
					{
						tableRowAdd(i+1);
					}

					i++;
				}
			}
			else
			{
				showNotification("Warning!","There are no data.",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception exp)
		{
			showNotification("addData", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void tableinitialise()
	{
		for(int i=0;i<10;i++)
		{
			tableRowAdd(i);
		}
	}

	private void tableRowAdd( final int ar)
	{
		tbChkSelect.add(ar,new CheckBox());
		tbChkSelect.get(ar).setWidth("100%");
		tbChkSelect.get(ar).setImmediate(true);
		tbChkSelect.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(tbChkSelect.get(ar).booleanValue())
				{
					if(!tbLblEmployeeName.get(ar).getValue().toString().isEmpty())
					{
						tbdLeaveFrom.get(ar).setEnabled(true);
						tbdLeaveTo.get(ar).setEnabled(true);
					}
					else
					{
						showNotification("Warning!","No employee found.",Notification.TYPE_WARNING_MESSAGE);
						tbChkSelect.get(ar).setValue(false);
					}
				}
				else
				{
					tbdLeaveFrom.get(ar).setEnabled(false);
					tbdLeaveTo.get(ar).setEnabled(false);
				}
			}
		});

		tbLblEmployeeId.add(ar,new Label());
		tbLblEmployeeId.get(ar).setWidth("100%");

		tbLblEmployeeCode.add(ar,new Label());
		tbLblEmployeeCode.get(ar).setWidth("100%");

		tbLblEmployeeName.add(ar,new Label());
		tbLblEmployeeName.get(ar).setWidth("100%");

		tbLblDesignation.add(ar, new Label());
		tbLblDesignation.get(ar).setWidth("100%");

		tbLblUnitName.add(ar, new Label());
		tbLblUnitName.get(ar).setWidth("100%");

		tbLblDivisionName.add(ar, new Label());
		tbLblDivisionName.get(ar).setWidth("100%");

		tbdLeaveFrom.add(ar, new PopupDateField());
		tbdLeaveFrom.get(ar).setWidth("100%");
		tbdLeaveFrom.get(ar).setDateFormat("dd-MM-yy");
		tbdLeaveFrom.get(ar).setEnabled(false);
		tbdLeaveFrom.get(ar).setImmediate(true);
		tbdLeaveFrom.get(ar).setResolution(PopupDateField.RESOLUTION_DAY);
		

		tbdLeaveTo.add(ar, new PopupDateField());
		tbdLeaveTo.get(ar).setWidth("100%");
		tbdLeaveTo.get(ar).setDateFormat("dd-MM-yy");
		tbdLeaveTo.get(ar).setEnabled(false);
		tbdLeaveTo.get(ar).setImmediate(true);
		tbdLeaveTo.get(ar).setResolution(PopupDateField.RESOLUTION_DAY);
		

		tbdLeaveF.add(ar, new PopupDateField());
		tbdLeaveF.get(ar).setWidth("100%");
		tbdLeaveF.get(ar).setDateFormat("dd-MM-yy");
		tbdLeaveF.get(ar).setEnabled(false);
		tbdLeaveF.get(ar).setImmediate(true);
		tbdLeaveF.get(ar).setResolution(PopupDateField.RESOLUTION_DAY);

		tbdLeaveT.add(ar, new PopupDateField());
		tbdLeaveT.get(ar).setWidth("100%");
		tbdLeaveT.get(ar).setDateFormat("dd-MM-yy");
		tbdLeaveT.get(ar).setEnabled(false);
		tbdLeaveT.get(ar).setImmediate(true);
		tbdLeaveT.get(ar).setResolution(PopupDateField.RESOLUTION_DAY);

		tbTxtTotalDays.add(ar, new TextRead(1));
		tbTxtTotalDays.get(ar).setWidth("100%");

		tbTxtApproveDays.add(ar, new TextRead(1));
		tbTxtApproveDays.get(ar).setWidth("100%");

		tbdApplicationDate.add(ar, new PopupDateField());
		tbdApplicationDate.get(ar).setWidth("100%");
		tbdApplicationDate.get(ar).setDateFormat("dd-MM-yy");
		tbdApplicationDate.get(ar).setImmediate(true);
		tbdApplicationDate.get(ar).setResolution(PopupDateField.RESOLUTION_DAY);
		tbdApplicationDate.get(ar).setReadOnly(true);

		tbBtnDetails.add(ar, new NativeButton());
		tbBtnDetails.get(ar).setWidth("100%");
		tbBtnDetails.get(ar).setHeight("24px");
		tbBtnDetails.get(ar).setIcon(new ThemeResource("../icons/preview.png"));
		tbBtnDetails.get(ar).addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(!tbLblEmployeeName.get(ar).getValue().toString().isEmpty())
				{
					reportPreview(ar);
				}
				else
				{
					showNotification("Warning!","No employee found.",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		tbLblReference.add(ar, new Label());
		tbLblReference.get(ar).setWidth("100%");

		table.addItem(new Object[]{tbLblReference.get(ar), tbLblEmployeeId.get(ar), tbLblEmployeeCode.get(ar), 
				tbLblEmployeeName.get(ar), tbLblDesignation.get(ar), tbLblUnitName.get(ar),  
				tbLblDivisionName.get(ar), tbdLeaveFrom.get(ar), tbdLeaveTo.get(ar), tbdLeaveF.get(ar), 
				tbdLeaveT.get(ar), tbTxtTotalDays.get(ar), tbTxtApproveDays.get(ar), tbdApplicationDate.get(ar), 
				tbBtnDetails.get(ar), tbChkSelect.get(ar)},ar);
	}

	private void tableAdd()
	{
		table.setWidth("98%");
		table.setHeight("330px");
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("Ref.", Label.class, new Label());
		table.setColumnWidth("Ref.", 30);

		table.addContainerProperty("Emp ID", Label.class, new Label());
		table.setColumnWidth("Emp ID", 75);

		table.addContainerProperty("Employee Id", Label.class, new Label());
		table.setColumnWidth("Employee Id", 75);

		table.addContainerProperty("Employee Name", Label.class, new Label());
		table.setColumnWidth("Employee Name", 200);

		table.addContainerProperty("Designation", Label.class, new Label());
		table.setColumnWidth("Designation", 150);

		table.addContainerProperty("Unit Name", Label.class, new Label());
		table.setColumnWidth("Unit Name", 105);

		table.addContainerProperty("Department Name", Label.class, new Label());
		table.setColumnWidth("Department Name", 150);

		table.addContainerProperty("Leave From", PopupDateField.class, new PopupDateField());
		table.setColumnWidth("Leave From", 90);

		table.addContainerProperty("Leave To", PopupDateField.class, new PopupDateField());
		table.setColumnWidth("Leave To", 90);

		table.addContainerProperty("From", PopupDateField.class, new PopupDateField());
		table.setColumnWidth("From", 90);

		table.addContainerProperty("To", PopupDateField.class, new PopupDateField());
		table.setColumnWidth("To", 90);

		table.addContainerProperty("Total", TextRead.class, new TextRead());
		table.setColumnWidth("Total", 40);

		table.addContainerProperty("Apr. Days", TextRead.class, new TextRead());
		table.setColumnWidth("Apr. Days", 55);

		table.addContainerProperty("App. Date", PopupDateField.class, new PopupDateField());
		table.setColumnWidth("App. Date", 65);

		table.addContainerProperty("Application", NativeButton.class, new NativeButton());
		table.setColumnWidth("Application", 65);

		table.addContainerProperty("Approved", CheckBox.class, new CheckBox());
		table.setColumnWidth("Approved", 60);

		table.setColumnAlignments(new String[] {Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT,
				Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT,
				Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_CENTER, Table.ALIGN_CENTER,Table.ALIGN_LEFT,
				Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_CENTER});

		table.setColumnCollapsed("Emp ID", true);
		table.setColumnCollapsed("From", true);
		table.setColumnCollapsed("To", true);
		table.setColumnCollapsed("App. Date", true);
		table.setColumnCollapsed("Unit Name", true);

	}

	private void txtClear()
	{
		cmbUnit.setValue(null);
		cmbDepartment.setValue(null);
		chkDepartmentAll.setValue(false);

		chkEmployeeAll.setValue(false);
		cmbEmployee.setValue(null);
		chkAll.setValue(false);
		tableClear();
	}

	private void tableClear()
	{
		for(int i = 0; i<tbChkSelect.size(); i++)
		{
			tbLblReference.get(i).setValue("");
			tbLblEmployeeId.get(i).setValue("");
			tbLblEmployeeCode.get(i).setValue("");
			tbLblEmployeeName.get(i).setValue("");
			tbLblDesignation.get(i).setValue("");
			tbLblUnitName.get(i).setValue("");
			tbLblDivisionName.get(i).setValue("");
			tbdLeaveFrom.get(i).setValue(null);
			tbdLeaveTo.get(i).setValue(null);
			tbdLeaveF.get(i).setValue(null);
			tbdLeaveT.get(i).setValue(null);
			tbTxtTotalDays.get(i).setValue("");
			tbTxtApproveDays.get(i).setValue("");

			tbdApplicationDate.get(i).setReadOnly(false);
			tbdApplicationDate.get(i).setValue(null);
			tbdApplicationDate.get(i).setReadOnly(true);

			tbChkSelect.get(i).setValue(false);
		}
	}

	private void btnIni(boolean t)
	{
		cButton.btnNew.setEnabled(t);
		cButton.btnSave.setEnabled(!t);
		cButton.btnRefresh.setEnabled(!t);
	}

	public void txtInit(boolean t)
	{
		cmbUnit.setEnabled(!t);
		cmbDepartment.setEnabled(!t);
		chkDepartmentAll.setEnabled(!t);

		cmbEmployee.setEnabled(!t);
		chkEmployeeAll.setEnabled(!t);
		
		chkAll.setEnabled(!t);
		btnFind.setEnabled(!t);
		table.setEnabled(!t);
	}

	private void reportPreview(int ar)
	{
		ReportDate reportTime = new ReportDate();
		try
		{
			HashMap <String,Object> hm = new HashMap <String,Object> ();
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("developer", sessionBean.getDeveloperAddress());
			hm.put("userName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			hm.put("path", "report/account/hrmModule/");
			hm.put("SysDate",reportTime.getTime);
			hm.put("logo", sessionBean.getCompanyLogo());
			

			String str1 = " select vEmployeeID,vEmployeeCode,vEmployeeName, "
					+ " (select vDepartmentName from tbEmpOfficialPersonalInfo where vEmployeeID=rla.vEmployeeID)vDepartmentName, "
					+ "(select vSectionName from tbEmpOfficialPersonalInfo where vEmployeeID=rla.vEmployeeID)vSectionName, "
					+ "(select vDesignationName from tbEmpOfficialPersonalInfo where vEmployeeID=rla.vEmployeeID)vDesignationName, "
					+ "(select vUnitName from tbEmpOfficialPersonalInfo where vEmployeeID=rla.vEmployeeID)vUnitName, "
					+ "iTotalDays,vMobileNo,vVisitingAddress,vPurposeOfLeave,dApplicationDate,dReplacementLeaveFrom,dReplacementLeaveTo  "
					+ "from tbReplacementLeaveApplication rla where vTransactionId like '"+tbLblReference.get(ar).getValue()+"' ";
			
			if(queryValueCheck(str1))
			{
				hm.put("sql", str1);

				Window win = new ReportViewer(hm,"report/account/hrmModule/rptReplacementLeaveApplicationFormPOSCO.jasper",
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
	private void findDayDiffernce(int ar)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();

		try
		{
			if(tbdLeaveFrom.get(ar).getValue()!=null && tbdLeaveTo.get(ar).getValue()!=null)
			{
				String query = "select 0 as c, DATEDIFF(DAY,'"+sessionBean.dfDb.format(tbdLeaveFrom.get(ar).getValue())+"'," +
						"'"+sessionBean.dfDb.format(tbdLeaveTo.get(ar).getValue())+"')+1 as day";
				List <?> list = session.createSQLQuery(query).list();

				if(list.iterator().hasNext())
				{
					Object[] element = (Object[]) list.iterator().next();
					int totalDays = Integer.parseInt(element[1].toString());
					if(totalDays <= 0)
					{
						tbTxtApproveDays.get(ar).setValue("");
						showNotification("Warning!","Select valid date range.", Notification.TYPE_WARNING_MESSAGE);
						tbdLeaveTo.get(ar).setValue(null);
						tbdLeaveTo.get(ar).focus();
					}
					else if(totalDays>0)
					{
						tbTxtApproveDays.get(ar).setValue(totalDays);
						if(Integer.parseInt("0"+tbTxtApproveDays.get(ar).toString().replaceAll(" ", "")) > 
						Integer.parseInt("0"+tbTxtTotalDays.get(ar).toString().replaceAll(" ", "")))
						{
							showNotification("Warning!","Approve days is greater than application days.", Notification.TYPE_WARNING_MESSAGE);
							tbdLeaveFrom.get(ar).setValue(tbdLeaveF.get(ar).getValue());
							tbdLeaveTo.get(ar).setValue(tbdLeaveT.get(ar).getValue());
						}
					}
				}
			}
		}
		catch (Exception ex)
		{
			this.getParent().showNotification("findDayDiffernce", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private int fridayCount(Object fromDate, Object toDate)
	{
		int numFriday = 0;
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();

		try
		{
			String query = "Select 0 as nf, datediff(day, -3, '"+toDate+"')/7-datediff(day, -2, '"+fromDate+"')/7 as NF";
			List <?> list = session.createSQLQuery(query).list();
			if(list.iterator().hasNext())
			{
				Object[] element = (Object[]) list.iterator().next();
				numFriday = Integer.parseInt(element[1].toString());
			}
		}
		catch (Exception ex)
		{
			this.getParent().showNotification("fridayCount", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
		return numFriday;  
	}

	private AbsoluteLayout Buildmainlayout() 
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		setWidth("1200.0px");
		setHeight("480.0px");

		cmbUnit = new ComboBox();
		cmbUnit.setImmediate(true);
		cmbUnit.setWidth("200px");
		cmbUnit.setHeight("-1px");
		cmbUnit.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Project : "),"top:15.0px;left:15.0px");
		mainLayout.addComponent(cmbUnit,"top:12.0px;left:80.0px");
		
		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("200px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Department : "),"top:15.0px;left:300.0px");
		mainLayout.addComponent(cmbDepartment,"top:12.0px;left:380.0px");

		chkDepartmentAll = new CheckBox("All");
		chkDepartmentAll.setImmediate(true);
		chkDepartmentAll.setWidth("-1px");
		chkDepartmentAll.setHeight("-1px");
		mainLayout.addComponent(chkDepartmentAll,"top:15.0px;left:580.0px");
		
		cmbEmployee = new ComboBox();
		cmbEmployee.setImmediate(true);
		cmbEmployee.setWidth("230px");
		cmbEmployee.setHeight("-1px");
		cmbEmployee.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Employee : "),"top:15.0px;left:640.0px");
		mainLayout.addComponent(cmbEmployee,"top:12.0px;left:720.0px");

		chkEmployeeAll = new CheckBox("All");
		chkEmployeeAll.setImmediate(true);
		chkEmployeeAll.setWidth("-1px");
		chkEmployeeAll.setHeight("-1px");
		mainLayout.addComponent(chkEmployeeAll,"top:15.0px;left:950.0px");

		mainLayout.addComponent(btnFind,"top:11.0px;left:998.0px");
		
		chkAll.setImmediate(true);
		chkAll.setWidth("-1px");
		chkAll.setHeight("-1px");
		mainLayout.addComponent(chkAll,"top:15px; right:30px");
		
	
		tableAdd();

		mainLayout.addComponent(table,"top:55.0px; left:10.0px;");
		mainLayout.addComponent(new Label("<b><Font Color='#FFD6D6' size='2px'> APR. Days => Approved Days </Font></b>",Label.CONTENT_XHTML), "top:395.0px; left:10.0px;");

		cButton.btnSave.setCaption("Approve");
		mainLayout.addComponent(cButton,"top:400.0px; left:430.0px");

		return mainLayout;
	}
}