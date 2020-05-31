package com.appform.hrmModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Transaction;
import org.hibernate.classic.Session;

import com.common.share.CommonButton;
import com.common.share.CommonMethod;
import com.common.share.MessageBox;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.common.share.ReportDate;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.HeaderClickEvent;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class LeaveApprove extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;
	private ComboBox cmbUnit;
	private ComboBox cmbDepartment;
	private CheckBox chkDepartmentAll;

	CommonButton btnFind = new CommonButton("", "", "", "", "", "Find", "", "", "", "");

	private Table table = new Table();
	private ArrayList<NativeButton> Delete = new ArrayList<NativeButton>();
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

	CommonButton cButton = new CommonButton("New", "Save", "", "", "Refresh", "", "", "", "", "Exit");
	private CommonMethod cm;
	private String menuId = "";
	private boolean click = true;

	public LeaveApprove( SessionBean sessionBean,String menuId)
	{
		this.sessionBean=sessionBean;
		this.setCaption("LEAVE APPLICATION APPROVE :: "+sessionBean.getCompany());
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
		chkDepartmentAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(chkDepartmentAll.booleanValue())
				{
					cmbDepartment.setValue(null);
					cmbDepartment.setEnabled(false);
				}
				else
				{
					cmbDepartment.setEnabled(true);
				}
			}
		});
		
		
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

		btnFind.btnFind.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
				{
					addTableData();
				}
				else
				{
					showNotification("Warning!","Select Department",Notification.TYPE_WARNING_MESSAGE);
					cmbDepartment.focus();
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
					+ " tbEmpLeaveApplicationInfo eli on epo.vEmployeeId=eli.vEmployeeId where eli.iFinal=0 "
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


	private void cmbDepartmentDataAdd()
	{
	     cmbDepartment.removeAllItems();

		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query=" select epo.vDepartmentId,epo.vDepartmentName from tbEmpOfficialPersonalInfo epo "
			+ "inner join tbEmpLeaveApplicationInfo eli on epo.vEmployeeId=eli.vEmployeeId "
			+ "where epo.vUnitId='"+cmbUnit.getValue().toString()+"' and eli.iFinal=0 "
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
			MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to approve leave?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
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
						Notification n=new Notification("Leave Approved Successfully!","",Notification.TYPE_TRAY_NOTIFICATION);
						n.setPosition(Notification.POSITION_TOP_RIGHT);
						showNotification(n);
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
					int adjust = (Integer.parseInt("0"+tbTxtTotalDays.get(i).getValue().toString())-Integer.parseInt("0"+tbTxtApproveDays.get(i).getValue().toString()));

					String leaveId=tbLblReference.get(i).getValue().toString();
					
					String deleteData = "insert into tbUdEmpLeaveApplicationInfo (vLeaveId,vLeaveTypeId,vLeaveTypeName,vEmployeeId,vEmployeeName,"
							+ "vUnitId,vUnitName,dLeaveDate,vPaymentFlag,iApprovedFlag,vAdjustedType,vAuthorisedBy,vRemarks,vCancelBy,vPcIp,iPrimary,"
							+ "iFinal,iHR,dEntitleFromDate,dEntitleToDate,vELType,vUdFlag,vUserId,vUserName,vUserIp,dEntryTime) "
							+ "select vLeaveId,vLeaveTypeId,vLeaveTypeName,vEmployeeId,vEmployeeName,vUnitId,vUnitName,dLeaveDate,vPaymentFlag,"
							+ "iApprovedFlag,vAdjustedType,vAuthorisedBy,vRemarks,vCancelBy,vPcIp,iPrimary,iFinal,iHR,dEntitleFromDate,dEntitleToDate,"
							+ "vELType,'UPDATE','"+sessionBean.getUserId()+"','"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP "
							+ "from tbEmpLeaveApplicationDetails where vLeaveId ='"+leaveId+"' ";
					
					System.out.println("deleteData: "+deleteData);
					session.createSQLQuery(deleteData).executeUpdate();
					
					String updateInfo = " update tbEmpLeaveApplicationInfo "
							+ "set dSanctionFrom = '"+sessionBean.dfDb.format(tbdLeaveFrom.get(i).getValue())+"', "
							+ "dSanctionTo = '"+sessionBean.dfDb.format(tbdLeaveTo.get(i).getValue())+"', "
							+ "mTotalDays = '"+"0"+tbTxtApproveDays.get(i).getValue().toString()+"',"
							+ "mAdjustDays = '"+adjust+"',"
							+ "mFridays = '"+(fridayCount(sessionBean.dfDb.format(tbdLeaveFrom.get(i).getValue()), sessionBean.dfDb.format(tbdLeaveTo.get(i).getValue())))+"', "
							+ "iFinal = 1, "
							+ "iPrimary = 1, "
							+ "iHR = 1, "
							+ "iApprovedFlag=1," +
							" vApprovedBy = '"+sessionBean.getUserName()+"'" +
							" where vLeaveId = '"+tbLblReference.get(i).getValue().toString()+"' ";

					session.createSQLQuery(updateInfo).executeUpdate();

					String updateDetails = " update tbEmpLeaveApplicationDetails set " +
							" iFinal=1, iApprovedFlag=1, iPrimary=1, iHR=1 where vLeaveId = '"+tbLblReference.get(i).getValue().toString()+"' ";

					session.createSQLQuery(updateDetails).executeUpdate();

					if(Integer.parseInt("0"+tbTxtApproveDays.get(i).toString().replaceAll(" ", "")) != 
							Integer.parseInt("0"+tbTxtTotalDays.get(i).toString().replaceAll(" ", "")))
					{
						String deleteDetails = " delete from tbEmpLeaveApplicationDetails where dLeaveDate not between" +
								" '"+sessionBean.dfDb.format(tbdLeaveFrom.get(i).getValue())+"'" +
								" and '"+sessionBean.dfDb.format(tbdLeaveTo.get(i).getValue())+"'" +
								" and vLeaveId = '"+tbLblReference.get(i).getValue().toString()+"' ";

						session.createSQLQuery(deleteDetails).executeUpdate();
					}

					String leaveAttendanceSummary = "update tbAttendanceSummary set iLeaveWithPay = (select COUNT(dLeaveDate)"
							+ " from tbEmpLeaveApplicationDetails where vEmployeeID = '"+tbLblEmployeeId.get(i).getValue()+"' "
							+ " and MONTH(dLeaveDate) = MONTH('"+sessionBean.dfDb.format(tbdLeaveFrom.get(i).getValue())+"') and "
							+ " YEAR(dLeaveDate) = YEAR('"+sessionBean.dfDb.format(tbdLeaveFrom.get(i).getValue())+"') and "
							+ " iPrimary = 1 and vPaymentFlag !='Without Pay'), iLeaveWithoutPay = (select COUNT(dLeaveDate)"
							+ " from tbEmpLeaveApplicationDetails where vEmployeeID = '"+tbLblEmployeeId.get(i).getValue()+"' "
							+ " and MONTH(dLeaveDate) = MONTH('"+sessionBean.dfDb.format(tbdLeaveFrom.get(i).getValue())+"') and "
							+ " YEAR(dLeaveDate) = YEAR('"+sessionBean.dfDb.format(tbdLeaveFrom.get(i).getValue())+"') and "
							+ " iPrimary = 1 and vPaymentFlag ='Without Pay') where vEmployeeID = '"+tbLblEmployeeId.get(i).getValue()+"' and "
							+ " MONTH(dDate) = MONTH('"+sessionBean.dfDb.format(tbdLeaveFrom.get(i).getValue())+"') and "
							+ " YEAR(dDate) = YEAR('"+sessionBean.dfDb.format(tbdLeaveFrom.get(i).getValue())+"')";
					session.createSQLQuery(leaveAttendanceSummary).executeUpdate();
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
			String sql = "select vLeaveId,eli.dApplicationDate,epo.vEmployeeName,epo.vDepartmentName,epo.vDesignationName,dSanctionFrom,dSanctionTo,mTotalDays," 
					+" epo.vEmployeeId,epo.vEmployeeCode from tbEmpLeaveApplicationInfo eli inner join tbEmpOfficialPersonalInfo epo on epo.vEmployeeId=eli.vEmployeeId "
					+" where epo.vUnitId like '"+(cmbUnit.getValue()==null?"%":cmbUnit.getValue().toString())+"'" 
					+" and epo.vDepartmentId like '"+(cmbDepartment.getValue()==null?"%":cmbDepartment.getValue().toString())+"' and iFinal = 0 order by eli.dApplicationDate";
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
					
					if(i==tbLblEmployeeName.size()-1)
						tableRowAdd(i+1);

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
		table.setWidth("98%");
		table.setHeight("330px");
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("Delete", NativeButton.class , new NativeButton());
		table.setColumnWidth("Delete",45);
		
		table.addContainerProperty("Ref.", Label.class, new Label());
		table.setColumnWidth("Ref.", 30);

		table.addContainerProperty("Emp ID", Label.class, new Label());
		table.setColumnWidth("Emp ID", 75);

		table.addContainerProperty("Employee Id", Label.class, new Label());
		table.setColumnWidth("Employee Id", 75);

		table.addContainerProperty("Employee Name", Label.class, new Label());
		table.setColumnWidth("Employee Name", 170);

		table.addContainerProperty("Designation", Label.class, new Label());
		table.setColumnWidth("Designation", 130);

		table.addContainerProperty("Unit Name", Label.class, new Label());
		table.setColumnWidth("Unit Name", 105);

		table.addContainerProperty("Department Name", Label.class, new Label());
		table.setColumnWidth("Department Name", 135);

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

		table.setColumnAlignments(new String[] {Table.ALIGN_CENTER, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT,
				Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT,
				Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_CENTER, Table.ALIGN_CENTER,Table.ALIGN_LEFT,
				Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_CENTER});

		table.setColumnCollapsed("Emp ID", true);
		table.setColumnCollapsed("From", true);
		table.setColumnCollapsed("To", true);
		table.setColumnCollapsed("App. Date", true);
		table.setColumnCollapsed("Unit Name", true);
		

		rowAddinTable();
	}
	public void rowAddinTable()
	{
		for(int i=0;i<10;i++)
		{
			tableRowAdd(i);
		}
	}
	public void deleteData(int deleteId)
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		Transaction tx=session.beginTransaction();		
		try
		{
			String leaveId=tbLblReference.get(deleteId).getValue().toString();
			
			String deleteData = "insert into tbUdEmpLeaveApplicationInfo (vLeaveId,vLeaveTypeId,vLeaveTypeName,vEmployeeId,vEmployeeName,"
					+ "vUnitId,vUnitName,dLeaveDate,vPaymentFlag,iApprovedFlag,vAdjustedType,vAuthorisedBy,vRemarks,vCancelBy,vPcIp,iPrimary,"
					+ "iFinal,iHR,dEntitleFromDate,dEntitleToDate,vELType,vUdFlag,vUserId,vUserName,vUserIp,dEntryTime) "
					+ "select vLeaveId,vLeaveTypeId,vLeaveTypeName,vEmployeeId,vEmployeeName,vUnitId,vUnitName,dLeaveDate,vPaymentFlag,"
					+ "iApprovedFlag,vAdjustedType,vAuthorisedBy,vRemarks,vCancelBy,vPcIp,iPrimary,iFinal,iHR,dEntitleFromDate,dEntitleToDate,"
					+ "vELType,'DELETE','"+sessionBean.getUserId()+"','"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP "
					+ "from tbEmpLeaveApplicationDetails where vLeaveId ='"+leaveId+"' ";
			
			System.out.println("deleteData: "+deleteData);
			session.createSQLQuery(deleteData).executeUpdate();
			

			// delete data from main table to insert update data
			String deleteInfo = "delete from tbEmpLeaveApplicationInfo where vLeaveId = '"+leaveId+"'";
			session.createSQLQuery(deleteInfo).executeUpdate();
			String deleteDetails = "delete from tbEmpLeaveApplicationDetails where vLeaveId = '"+leaveId+"'";
			session.createSQLQuery(deleteDetails).executeUpdate();
			tx.commit();
			
		}
		catch (Exception exp)
		{
			tx.rollback();
			showNotification("deleteData", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally
		{
			session.close();
		}
		
	}
	private void tableRowAdd( final int ar)
	{
		Delete.add(ar, new NativeButton(""));
		Delete.get(ar).setWidth("100%");
		Delete.get(ar).setImmediate(true);
		Delete.get(ar).setIcon(new ThemeResource("../icons/trash.png"));
		Delete.get(ar).setStyleName("Transparent");
		Delete.get(ar).addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				MessageBox mb = new MessageBox(getParent(),"Are You Sure?",MessageBox.Icon.QUESTION,"Do you want to Delete this Row?",new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"),new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
				mb.setStyleName("cwindowMB");
				mb.show(new EventListener()
				{
					public void buttonClicked(ButtonType buttonType)
					{
						if(buttonType==ButtonType.YES)
						{
							deleteData(ar);
							
							tbLblReference.get(ar).setValue("");
							tbLblEmployeeId.get(ar).setValue("");
							tbLblEmployeeCode.get(ar).setValue("");
							tbLblEmployeeName.get(ar).setValue("");
							tbLblDesignation.get(ar).setValue("");
							tbLblUnitName.get(ar).setValue("");
							tbLblDivisionName.get(ar).setValue("");							
							tbdLeaveFrom.get(ar).setValue(null);
							tbdLeaveTo.get(ar).setValue(null);
							tbdLeaveF.get(ar).setValue(null);
							tbdLeaveT.get(ar).setValue(null);
							
							tbTxtTotalDays.get(ar).setValue("");
							tbTxtApproveDays.get(ar).setValue("");
							
							tbdApplicationDate.get(ar).setReadOnly(false);
							tbdApplicationDate.get(ar).setValue(null);
							tbChkSelect.get(ar).setValue(false);
							
							for(int rowcount=ar;rowcount<=tbLblEmployeeId.size()-1;rowcount++)
							{
								if(rowcount+1<=tbLblEmployeeId.size()-1)
								{
									if(!tbLblEmployeeId.get(rowcount+1).getValue().toString().equals(""))
									{
										tbLblReference.get(rowcount).setValue(tbLblReference.get(rowcount+1).getValue());
										tbLblEmployeeId.get(rowcount).setValue(tbLblEmployeeId.get(rowcount+1).getValue());
										tbLblEmployeeCode.get(rowcount).setValue(tbLblEmployeeCode.get(rowcount+1).getValue());
										tbLblEmployeeName.get(rowcount).setValue(tbLblEmployeeName.get(rowcount+1).getValue());
										tbLblDesignation.get(rowcount).setValue(tbLblDesignation.get(rowcount+1).getValue());
										tbLblUnitName.get(rowcount).setValue(tbLblUnitName.get(rowcount+1).getValue());
										tbLblDivisionName.get(rowcount).setValue(tbLblDivisionName.get(rowcount+1));
										tbdLeaveFrom.get(rowcount).setValue(tbdLeaveFrom.get(rowcount+1).getValue());
										tbdLeaveTo.get(rowcount).setValue(tbdLeaveTo.get(rowcount+1).getValue());
										tbdLeaveF.get(rowcount).setValue(tbdLeaveF.get(rowcount+1).getValue());
										tbdLeaveT.get(rowcount).setValue(tbdLeaveT.get(rowcount+1).getValue());
										tbTxtTotalDays.get(rowcount).setValue(tbTxtTotalDays.get(rowcount+1).getValue());
										tbTxtApproveDays.get(rowcount).setValue(tbTxtApproveDays.get(rowcount+1).getValue());
										tbdApplicationDate.get(ar).setReadOnly(false);
										tbdApplicationDate.get(rowcount).setValue(tbdApplicationDate.get(rowcount+1).getValue());
										tbChkSelect.get(rowcount).setValue(tbChkSelect.get(rowcount+1).getValue());

										tbLblReference.get(rowcount+1).setValue("");
										tbLblEmployeeId.get(rowcount+1).setValue("");
										tbLblEmployeeCode.get(rowcount+1).setValue("");
										tbLblEmployeeName.get(rowcount+1).setValue("");
										tbLblDesignation.get(rowcount+1).setValue("");
										tbLblUnitName.get(rowcount+1).setValue("");
										tbLblDivisionName.get(rowcount+1).setValue("");
										tbdLeaveFrom.get(rowcount+1).setValue(null);
										tbdLeaveTo.get(rowcount+1).setValue(null);
										tbdLeaveF.get(rowcount+1).setValue(null);
										tbdLeaveT.get(rowcount+1).setValue(null);
										tbTxtTotalDays.get(rowcount+1).setValue("");
										tbTxtApproveDays.get(rowcount+1).setValue("");
										tbdApplicationDate.get(rowcount+1).setReadOnly(false);
										tbdApplicationDate.get(rowcount+1).setValue(null);
										tbChkSelect.get(rowcount+1).setValue(false);
									}
								}
							}
							
							Notification n=new Notification("Row Data Delete Successfully!","",Notification.TYPE_TRAY_NOTIFICATION);
							n.setPosition(Notification.POSITION_TOP_RIGHT);
							showNotification(n);							
						}
					}
				});
			}
		});

		tbLblReference.add(ar, new Label());
		tbLblReference.get(ar).setImmediate(true);
		tbLblReference.get(ar).setWidth("100%");
		
		tbLblEmployeeId.add(ar,new Label());
		tbLblEmployeeId.get(ar).setImmediate(true);
		tbLblEmployeeId.get(ar).setWidth("100%");

		tbLblEmployeeCode.add(ar,new Label());
		tbLblEmployeeCode.get(ar).setImmediate(true);
		tbLblEmployeeCode.get(ar).setWidth("100%");

		tbLblEmployeeName.add(ar,new Label());
		tbLblEmployeeName.get(ar).setImmediate(true);
		tbLblEmployeeName.get(ar).setWidth("100%");

		tbLblDesignation.add(ar, new Label());
		tbLblDesignation.get(ar).setImmediate(true);
		tbLblDesignation.get(ar).setWidth("100%");

		tbLblUnitName.add(ar, new Label());
		tbLblUnitName.get(ar).setImmediate(true);
		tbLblUnitName.get(ar).setWidth("100%");

		tbLblDivisionName.add(ar, new Label());
		tbLblDivisionName.get(ar).setImmediate(true);
		tbLblDivisionName.get(ar).setWidth("100%");

		tbdLeaveFrom.add(ar, new PopupDateField());
		tbdLeaveFrom.get(ar).setWidth("100%");
		tbdLeaveFrom.get(ar).setDateFormat("dd-MM-yy");
		tbdLeaveFrom.get(ar).setEnabled(false);
		tbdLeaveFrom.get(ar).setImmediate(true);
		tbdLeaveFrom.get(ar).setResolution(PopupDateField.RESOLUTION_DAY);
		tbdLeaveFrom.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(tbdLeaveFrom.get(ar).getValue()!=null && tbdLeaveTo.get(ar).getValue()!=null)
				{
					if
					(
						!tbLblEmployeeName.get(ar).getValue().toString().isEmpty() && 
						!tbTxtTotalDays.get(ar).getValue().toString().isEmpty() && 
						!tbTxtApproveDays.get(ar).getValue().toString().isEmpty() 
					)
					{
						findDayDiffernce(ar);
					}
				}
			}
		});

		tbdLeaveTo.add(ar, new PopupDateField());
		tbdLeaveTo.get(ar).setWidth("100%");
		tbdLeaveTo.get(ar).setDateFormat("dd-MM-yy");
		tbdLeaveTo.get(ar).setEnabled(false);
		tbdLeaveTo.get(ar).setImmediate(true);
		tbdLeaveTo.get(ar).setResolution(PopupDateField.RESOLUTION_DAY);
		tbdLeaveTo.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(tbdLeaveFrom.get(ar).getValue()!=null && tbdLeaveTo.get(ar).getValue()!=null)
				{
					if
					(
						!tbLblEmployeeName.get(ar).getValue().toString().isEmpty() && 
						!tbTxtTotalDays.get(ar).getValue().toString().isEmpty() && 
						!tbTxtApproveDays.get(ar).getValue().toString().isEmpty() 
					)
					{
						findDayDiffernce(ar);
					}
				}
			}
		});

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
		tbTxtTotalDays.get(ar).setImmediate(true);
		tbTxtTotalDays.get(ar).setWidth("100%");

		tbTxtApproveDays.add(ar, new TextRead(1));
		tbTxtApproveDays.get(ar).setImmediate(true);
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

		table.addItem(new Object[]{Delete.get(ar),tbLblReference.get(ar), tbLblEmployeeId.get(ar), tbLblEmployeeCode.get(ar), 
				tbLblEmployeeName.get(ar), tbLblDesignation.get(ar), tbLblUnitName.get(ar),  
				tbLblDivisionName.get(ar), tbdLeaveFrom.get(ar), tbdLeaveTo.get(ar), tbdLeaveF.get(ar), 
				tbdLeaveT.get(ar), tbTxtTotalDays.get(ar), tbTxtApproveDays.get(ar), tbdApplicationDate.get(ar), 
				tbBtnDetails.get(ar), tbChkSelect.get(ar)},ar);
	}

	private AbsoluteLayout Buildmainlayout() 
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		setWidth("1200.0px");
		setHeight("480.0px");

		mainLayout.addComponent(new Label("Department : "),"top:15.0px;left:450.0px");

		cmbUnit = new ComboBox();
		cmbUnit.setImmediate(true);
		cmbUnit.setWidth("280px");
		cmbUnit.setHeight("-1px");
		mainLayout.addComponent(new Label("Project : "),"top:15.0px;left:30.0px");
		mainLayout.addComponent(cmbUnit,"top:12.0px;left:120.0px");
		
		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("280px");
		cmbDepartment.setHeight("-1px");
		mainLayout.addComponent(cmbDepartment,"top:12.0px;left:540.0px");

		chkDepartmentAll = new CheckBox("All");
		chkDepartmentAll.setImmediate(true);
		chkDepartmentAll.setWidth("-1px");
		chkDepartmentAll.setHeight("-1px");
		mainLayout.addComponent(chkDepartmentAll,"top:15.0px;left:825.0px");

		mainLayout.addComponent(btnFind,"top:11.0px;left:860.0px");

		mainLayout.addComponent(table,"top:55.0px; left:10.0px;");
		mainLayout.addComponent(new Label("<b><Font Color='brown' size='2px'> APR. Days => Approved Days </Font></b>",Label.CONTENT_XHTML), "top:395.0px; left:10.0px;");

		cButton.btnSave.setCaption("Approve");
		mainLayout.addComponent(cButton,"top:400.0px; left:430.0px");

		return mainLayout;
	}

	private void txtClear()
	{
		cmbUnit.setValue(null);
		cmbDepartment.setValue(null);
		chkDepartmentAll.setValue(false);
		tableClear();
	}

	private void tableClear()
	{
		for(int i = 0; i<tbLblReference.size(); i++)
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
		btnFind.setEnabled(!t);
		table.setEnabled(!t);
	}

	private void reportPreview(int ar)
	{
		ReportDate reportTime = new ReportDate();
		String sql = " select * from funLeaveApplicationReport('"+tbLblReference.get(ar).getValue()+"','"+tbLblEmployeeId.get(ar).getValue()+"') "
				+ "order by vEmployeeId ";
		System.out.println(sql);
		try
		{
			HashMap <String,Object> hm = new HashMap <String,Object> ();
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			//hm.put("appDate", dApplicationDate.getValue());
			hm.put("userName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			hm.put("path", "./report/account/hrmModule/");
			hm.put("SysDate",reportTime.getTime);
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("developer", sessionBean.getDeveloperAddress());
			hm.put("sql", sql);

			Window win = new ReportViewer(hm,"report/account/hrmModule/rptLeaveApplicationFormPOSCO.jasper",
					this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
					this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
					this.getWindow().getApplication().getURL()+"VAADIN/applet",true);

			win.setCaption("Project Report");
			win.setStyleName("cwindow");
			this.getParent().getWindow().addWindow(win);
		}
		catch(Exception exp)
		{
			showNotification("Error",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
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
				System.out.println("findDayDiffernce: "+query);
				
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
						int ApproveDays=0,TotalDays=0;
						ApproveDays=Integer.parseInt(tbTxtApproveDays.get(ar).getValue().toString().isEmpty()?"0":tbTxtApproveDays.get(ar).getValue().toString());
						TotalDays=Integer.parseInt(tbTxtTotalDays.get(ar).getValue().toString().isEmpty()?"0":tbTxtTotalDays.get(ar).getValue().toString());
						
						System.out.println("ApproveDays: "+ApproveDays+" TotalDays: "+TotalDays);
						
						if(ApproveDays>TotalDays)
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
}