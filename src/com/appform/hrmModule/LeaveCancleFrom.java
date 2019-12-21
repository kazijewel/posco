package com.appform.hrmModule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.CommonMethod;
import com.common.share.FocusMoveByEnter;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.MessageBox;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Table.HeaderClickEvent;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class LeaveCancleFrom extends Window
{
	private CommonButton cButton = new CommonButton( "New",  "Save",  "",  "",  "Refresh",  "", "", "","","Exit");

	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private OptionGroup radioButtonGroup;
	private List<String> lst = Arrays.asList(new String[]{"Employee Id","Finger Id","Employee Name"});

	public Table table = new Table();
	public ArrayList<Label> tbllblSerial = new ArrayList<Label>();
	public ArrayList<Label> tbllblEmployeeName = new ArrayList<Label>();
	public ArrayList<PopupDateField> tbldDate = new ArrayList<PopupDateField>();
	public ArrayList<Label> tbllblDayName = new ArrayList<Label>();
	public ArrayList<Label> tbllblAdjustedType = new ArrayList<Label>();
	public ArrayList<Label> tbllblPaymentType = new ArrayList<Label>();
	public ArrayList<CheckBox> tblChkCancle = new ArrayList<CheckBox>();

	HashMap <String,Object> hDateDayList = new HashMap <String,Object> ();

	private Label lblCancelDate;
	private PopupDateField dDateOfCancellation;
	private Label lblLeaveApplicationDate;
	private PopupDateField dFromDate;
	private Label lblTo;
	private PopupDateField dToDate;
	private Label lblUnit;
	private ComboBox cmbUnit;

	private Label lblSectionName;
	private ComboBox cmbSectionName,cmbDepartment;
	private Label lblReferenceNo;
	private ComboBox cmbReferenceNo;
	private Label lblEmployeeName;
	private ComboBox cmbEmployeeName;
	private TextField txtAuthorisedBy;
	private TextField txtRemarks;
	private CheckBox chkSectionAll,chkDepartmentAll;

	private ArrayList<Component> allComp = new ArrayList<Component>();
	private CommonMethod cm;
	private String menuId = "";
	private boolean click = true;

	public LeaveCancleFrom(SessionBean sessionBean,String menuId)
	{
		this.sessionBean = sessionBean;
		this.setCaption("LEAVE CANCEL FROM :: "+sessionBean.getCompany());
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		buildMainLayout();
		setContent(mainLayout);
		btnIni(true);
		cmbunitValueAdd();
		componentIni(false);
		focusEnter();
		setEventAction();
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
		cButton.btnNew.addListener(new ClickListener()
		{	
			public void buttonClick(ClickEvent event)
			{
				componentIni(true);
				btnIni(false);
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
				componentIni(false);
				btnIni(true);
			}
		});

		cButton.btnExit.addListener(new ClickListener()
		{	
			public void buttonClick(ClickEvent event)
			{
				close();
			}
		});

		dFromDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
			
				cmbunitValueAdd();
				
			}
		});

		dToDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
		
				cmbunitValueAdd();
			}
		});
		cmbUnit.addListener(new ValueChangeListener()
		{
			
			
			public void valueChange(ValueChangeEvent event)
			{
				if( cmbUnit.getValue() !=null)
				cmbDepartmentValueAdd();
			}
		});
		cmbDepartment.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if( cmbDepartment.getValue() !=null)
				{
					cmbSectionValueAdd();
				}
				
			}
		});

		chkDepartmentAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbUnit.getValue()!=null)
				{
					if(chkDepartmentAll.booleanValue())
					{
						cmbDepartment.setValue(null);
						cmbDepartment.setEnabled(false);

						cmbSectionValueAdd();
					}
					else
					{
						cmbDepartment.setEnabled(true);
					}
				}
			}
		});


		cmbSectionName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if( cmbSectionName.getValue() !=null)

				cmbEmployeeNameDataAdd();
			}
		});

		chkSectionAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
				{
					if(chkSectionAll.booleanValue())
					{
						cmbSectionName.setValue(null);
						cmbSectionName.setEnabled(false);

						cmbEmployeeNameDataAdd();
					}
					else
					{
						cmbSectionName.setEnabled(true);
					}
				}
			}
		});

		cmbEmployeeName.addListener(new ValueChangeListener()
		{	
			public void valueChange(ValueChangeEvent event)
			{
				cmbReferenceNo.removeAllItems();
				if(cmbEmployeeName.getValue()!=null)
				{
					addReferenceNo();
				}
			}
		});

		cmbReferenceNo.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				    tableClear();
				if(cmbReferenceNo.getValue()!=null)
				{
					setTableData();
				}
			}
		});

		radioButtonGroup.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmployeeNameDataAdd();
			}
		});

		table.addListener(new Table.HeaderClickListener() 
		{
			public void headerClick(HeaderClickEvent event) 
			{
				if(event.getPropertyId().toString() == "Cancel")
				{
					if(click)
					{
						cancelAll(true);
						click = false;
					}
					else
					{
						cancelAll(false);
						click = true;
					}
				}
			}
		});
	}

	public void cancelAll(boolean ret)
	{
		if(ret)
		{
			for(int i = 0; i<tbllblEmployeeName.size(); i++)
			{
				if(!tbllblEmployeeName.get(i).getValue().toString().isEmpty())
				{
					tblChkCancle.get(i).setValue(true);
				}
			}
		}
		else
		{
			for(int i = 0; i<tbllblEmployeeName.size(); i++)
			{
				tblChkCancle.get(i).setValue(false);
			}
		}
	}

	private void cmbunitValueAdd()
	{	
		cmbUnit.removeAllItems();
		
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
/*			String query = " select distinct vSectionId,(select vSectionName from tbSectionInfo si where si.vSectionId =" +
					" eli.vSectionId) vSectionName from tbEmpLeaveApplicationInfo eli where dApplicationDate between" +
					" '"+sessionBean.dfDb.format(dFromDate.getValue())+"' and '"+sessionBean.dfDb.format(dToDate.getValue())+"' order by vSectionName";

*/	
			String query = "select distinct epo.vUnitId,epo.vUnitName from tbEmpLeaveApplicationInfo eli inner join tbEmpOfficialPersonalInfo epo on epo.vEmployeeId=eli.vEmployeeId where "
					+ " eli.dApplicationDate between '"+sessionBean.dfDb.format(dFromDate.getValue())+"' "
					+ " and '"+sessionBean.dfDb.format(dToDate.getValue())+"' ";
			
			
			
			System.out.println("Unit"+query);
			
			
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
			showNotification("cmbunitValueAdd", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	
	private void cmbDepartmentValueAdd()
	{
		cmbDepartment.removeAllItems();
		
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = " select distinct epo.vDepartmentId,epo.vDepartmentName from tbEmpLeaveApplicationInfo eli inner join tbEmpOfficialPersonalInfo epo on epo.vEmployeeId=eli.vEmployeeId where eli.dApplicationDate between" +
					" '"+sessionBean.dfDb.format(dFromDate.getValue())+"' and '"+sessionBean.dfDb.format(dToDate.getValue())+"' "
							+ "and epo.vUnitId='"+cmbUnit.getValue().toString()+"' order by epo.vDepartmentName";
		
			System.out.println("section"+query);
			
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
			showNotification("cmbSectionValueAdd", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void cmbSectionValueAdd()
	{
		cmbSectionName.removeAllItems();
		
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = " select distinct epo.vSectionId,epo.vSectionName from tbEmpLeaveApplicationInfo eli inner join tbEmpOfficialPersonalInfo epo on epo.vEmployeeId=eli.vEmployeeId where eli.dApplicationDate between" +
					" '"+sessionBean.dfDb.format(dFromDate.getValue())+"' and '"+sessionBean.dfDb.format(dToDate.getValue())+"' "
							+ "and epo.vUnitId='"+cmbUnit.getValue().toString()+"' and epo.vDepartmentId like '"+(chkDepartmentAll.booleanValue()==true?"%":cmbDepartment.getValue().toString())+"' order by epo.vSectionName";
		
			System.out.println("section"+query);
			
			List <?> lst=session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				for(Iterator <?> itr=lst.iterator();itr.hasNext();)
				{
					Object [] element=(Object[])itr.next();
					cmbSectionName.addItem(element[0]);
					cmbSectionName.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch (Exception exp)
		{
			showNotification("cmbSectionValueAdd", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void cmbEmployeeNameDataAdd()
	{
		cmbEmployeeName.removeAllItems();
		
		if(cmbSectionName.getValue()!=null || chkSectionAll.booleanValue())
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String query = "select distinct opi.vEmployeeId,opi.vFingerId,opi.vProximityId,opi.vEmployeeName from" +
						" tbEmpLeaveApplicationInfo eli inner join tbEmpOfficialPersonalInfo opi on eli.vEmployeeId =" +
						" opi.vEmployeeId where eli.dApplicationDate between '"+sessionBean.dfDb.format(dFromDate.getValue())+"'" +
						" and '"+sessionBean.dfDb.format(dToDate.getValue())+"' and " +
						" opi.vSectionId like '"+(chkSectionAll.booleanValue()?"%":(cmbSectionName.getValue()==null?"":cmbSectionName.getValue().toString()))+"'"
					+ " and opi.vUnitId='"+cmbUnit.getValue().toString()+"' and opi.vDepartmentId like '"+(chkDepartmentAll.booleanValue()?"%":(cmbDepartment.getValue()==null?"":cmbDepartment.getValue().toString()))+"' order by opi.vEmployeeName";
			
				System.out.println("Employee"+query);
				
				
				List <?> lst = session.createSQLQuery(query).list();
				if(!lst.isEmpty())
				{
					if(radioButtonGroup.getValue().toString().equalsIgnoreCase("Finger Id"))
					{
						lblEmployeeName.setValue("Finger Id : ");
						for(Iterator <?> itr=lst.iterator();itr.hasNext();)
						{
							Object [] element=(Object[])itr.next();
							cmbEmployeeName.addItem(element[0]);
							cmbEmployeeName.setItemCaption(element[0], element[1].toString());
						}
					}
					if(radioButtonGroup.getValue().toString().equalsIgnoreCase("Proximity Id"))
					{
						lblEmployeeName.setValue("Proximity Id : ");
						for(Iterator <?> itr=lst.iterator();itr.hasNext();)
						{
							Object [] element=(Object[])itr.next();
							cmbEmployeeName.addItem(element[0]);
							cmbEmployeeName.setItemCaption(element[0], element[2].toString());
						}
					}
					if(radioButtonGroup.getValue().toString().equalsIgnoreCase("Employee Name"))
					{
						lblEmployeeName.setValue("Employee Name : ");
						for(Iterator <?> itr=lst.iterator();itr.hasNext();)
						{
							Object [] element=(Object[])itr.next();
							cmbEmployeeName.addItem(element[0]);
							cmbEmployeeName.setItemCaption(element[0], element[3].toString());
						}
					}
				}
			}
			catch (Exception exp)
			{
				showNotification("cmbEmployeeNameDataAdd", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}
	}

	private void addReferenceNo()
	{
		
		
		cmbReferenceNo.removeAllItems();
		
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = " select vLeaveId,vLeaveId from tbEmpLeaveApplicationInfo where dApplicationDate between" +
					" '"+sessionBean.dfDb.format(dFromDate.getValue())+"' and '"+sessionBean.dfDb.format(dToDate.getValue())+"' and" +
					" vEmployeeId = '"+(cmbEmployeeName.getValue()==null?"":cmbEmployeeName.getValue().toString())+"'";
			List <?> lst=session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				for(Iterator <?> itr = lst.iterator();itr.hasNext();)
				{
					Object [] element = (Object[])itr.next();
					cmbReferenceNo.addItem(element[1]);
					cmbReferenceNo.setItemCaption(element[1], element[1].toString());
				}
			}
		}
		catch (Exception exp)
		{
			showNotification("addReferenceNo", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void setTableData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = " select vEmployeeName,dLeaveDate,convert(varchar,DATENAME(DW,dLeaveDate)) vDayName,vAdjustedType"+
					" ,vPaymentFlag,iApprovedFlag from tbEmpLeaveApplicationDetails where vLeaveId = '"+cmbReferenceNo.getValue().toString()+"' order by dLeaveDate ";
			List <?> list = session.createSQLQuery(sql).list();

			if(!list.isEmpty())
			{
				int i = 0;
				for (Iterator <?> iter = list.iterator(); iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();
					tbllblEmployeeName.get(i).setValue(element[0].toString());
					tbldDate.get(i).setReadOnly(false);
					tbldDate.get(i).setValue(element[1]);
					tbldDate.get(i).setReadOnly(true);
					tbllblDayName.get(i).setValue(element[2].toString());
					tbllblAdjustedType.get(i).setValue(element[3].toString());
					tbllblPaymentType.get(i).setValue(element[4].toString());

					if(element[5].toString().equals("2"))
					{
						tblChkCancle.get(i).setValue(true);
						tblChkCancle.get(i).setEnabled(false);
					}
					else
					{
						tblChkCancle.get(i).setEnabled(true);
					}

					if(tbllblEmployeeName.size()-1==i)
					{
						tableRowAdd(i+1);
					}
					i++;
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("addData", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void formValidation()
	{
	if(cmbUnit.getValue()!=null)
	   {
		if(cmbSectionName.getValue()!=null || chkSectionAll.booleanValue())
		{
			if(cmbEmployeeName.getValue()!=null)
			{
				if(cmbReferenceNo.getValue()!=null)
				{
					if(!txtAuthorisedBy.getValue().toString().isEmpty())
					{
						if(checkTableSelect())
						{
							saveButtonAction();
						}
						else
						{
							showNotification("Warning!","Select leave to cancel from table.", Notification.TYPE_WARNING_MESSAGE);
							tblChkCancle.get(0).focus();
						}
					}
					else
					{
						showNotification("Warning!","Provide cancel authorised by.", Notification.TYPE_WARNING_MESSAGE);
						txtAuthorisedBy.focus();
					}
				}
				else
				{
					showNotification("Warning!","Select leave reference no.", Notification.TYPE_WARNING_MESSAGE);
					cmbReferenceNo.focus();
				}
			}
			else
			{
				showNotification("Warning!","Select employee name.", Notification.TYPE_WARNING_MESSAGE);
				cmbEmployeeName.focus();
			}
		}
		else
		{
			showNotification("Warning!","Select Division name.", Notification.TYPE_WARNING_MESSAGE);
			cmbSectionName.focus();
		}
		}
		else
		{
			showNotification("Warning!","Select Unit name.", Notification.TYPE_WARNING_MESSAGE);
			cmbUnit.focus();
		}
	}

	private boolean checkTableSelect()
	{
		boolean ret = false;

		for(int i = 0; i<tbllblEmployeeName.size(); i++)
		{
			if(tblChkCancle.get(i).booleanValue())
			{
				ret = true;
				break;
			}
		}

		return ret;
	}

	private void saveButtonAction()
	{
		MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to save information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
		mb.setStyleName("cwindowMB");
		mb.show(new EventListener()
		{
			public void buttonClicked(ButtonType buttonType)
			{
				if(buttonType == ButtonType.YES)
				{
					insertData();
					componentIni(false);
					btnIni(true);
					txtClear();
					Notification n=new Notification("All Information Save Successfully!","",Notification.TYPE_TRAY_NOTIFICATION);
					n.setPosition(Notification.POSITION_TOP_RIGHT);
					showNotification(n);
				}
			}
		});
	}

	private void insertData()
	{
		int cancelLeave = 0;
		int totalLeave = 0;

		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{ 
			for(int i = 0; i<tbllblEmployeeName.size(); i++)
			{
				if(tblChkCancle.get(i).booleanValue())
				{
					String updateQuery = " update tbEmpLeaveApplicationDetails set" +
							" iApprovedFlag = '2'," +
							" vAuthorisedBy = '"+txtAuthorisedBy.getValue().toString()+"'," +
							" vRemarks = '"+(txtRemarks.getValue().toString().isEmpty()?"":txtRemarks.getValue().toString())+"'," +
							" vCancelBy = '"+sessionBean.getUserName()+"'," +
							" vPcIp = '"+sessionBean.getUserIp()+"'," +
							" dCancelTime = CURRENT_TIMESTAMP " +
							" where vLeaveId = '"+cmbReferenceNo.getValue().toString()+"' and" +
							" dLeaveDate = '"+sessionBean.dfDb.format(tbldDate.get(i).getValue())+"'" +
							" and iApprovedFlag != 2 ";

					session.createSQLQuery(updateQuery).executeUpdate();
					
					System.out.println("UPDATE QUERY-1"+updateQuery);
					
					
				}
			}

			Iterator <?> iter = session.createSQLQuery("select COUNT(vLeaveId) from tbEmpLeaveApplicationDetails where" +
					" iApprovedFlag = 2 and vLeaveId = '"+cmbReferenceNo.getValue().toString()+"'").list().iterator();
			if(iter.hasNext())
				cancelLeave = Integer.valueOf(iter.next().toString());

			Iterator <?> iterTotal = session.createSQLQuery("select COUNT(vLeaveId) from tbEmpLeaveApplicationDetails where" +
					" vLeaveId = '"+cmbReferenceNo.getValue().toString()+"'").list().iterator();
			if(iterTotal.hasNext())
				totalLeave = Integer.valueOf(iterTotal.next().toString());

			if(cancelLeave == totalLeave)
			{
				String updateQuery = " update tbEmpLeaveApplicationInfo set" +
						" iApprovedFlag = '2' where vLeaveId = '"+cmbReferenceNo.getValue().toString()+"' ";

				session.createSQLQuery(updateQuery).executeUpdate();
			}

			String leaveAttendanceSummary = "update tbAttendanceSummary set iLeaveWithPay = (select COUNT(dLeaveDate)"
					+ " from tbEmpLeaveApplicationDetails where vEmployeeID = '"+cmbEmployeeName.getValue()+"' "
					+ " and MONTH(dLeaveDate) = MONTH('"+sessionBean.dfDb.format(dFromDate.getValue())+"') and "
					+ " YEAR(dLeaveDate) = YEAR('"+sessionBean.dfDb.format(dFromDate.getValue())+"') and "
					+ " iApprovedFlag = 1 and vPaymentFlag !='Without Pay'), iLeaveWithoutPay = (select COUNT(dLeaveDate)"
					+ " from tbEmpLeaveApplicationDetails where vEmployeeID = '"+cmbEmployeeName.getValue()+"' "
					+ " and MONTH(dLeaveDate) = MONTH('"+sessionBean.dfDb.format(dFromDate.getValue())+"') and "
					+ " YEAR(dLeaveDate) = YEAR('"+sessionBean.dfDb.format(dFromDate.getValue())+"') and "
					+ " iApprovedFlag = 1 and vPaymentFlag ='Without Pay') where "
					+ " vEmployeeID = '"+cmbEmployeeName.getValue()+"' and "
					+ " MONTH(dDate) = MONTH('"+sessionBean.dfDb.format(dFromDate.getValue())+"') and "
					+ " YEAR(dDate) = YEAR('"+sessionBean.dfDb.format(dFromDate.getValue())+"')";
		
			System.out.println("AttendanceSummary"+leaveAttendanceSummary);

			session.createSQLQuery(leaveAttendanceSummary).executeUpdate();
			
			tx.commit();
			
		}


		catch(Exception ex)
		{
			tx.rollback();
			this.showNotification("update data", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}

	}

	private void btnIni(boolean t) 
	{
		cButton.btnNew.setEnabled(t);
		cButton.btnEdit.setEnabled(t);
		cButton.btnSave.setEnabled(!t);
		cButton.btnRefresh.setEnabled(!t);
		cButton.btnDelete.setEnabled(t);
		cButton.btnFind.setEnabled(t);
		cButton.btnExit.setEnabled(t);
	}

	private void componentIni(boolean t)
	{
		dDateOfCancellation.setEnabled(t);
		dFromDate.setEnabled(t);
		dToDate.setEnabled(t);
		radioButtonGroup.setEnabled(t);
		cmbEmployeeName.setEnabled(t);
		cmbUnit.setEnabled(t);
		cmbSectionName.setEnabled(t);
		cmbDepartment.setEnabled(t);
		chkDepartmentAll.setEnabled(t);
		cmbReferenceNo.setEnabled(t);
		chkSectionAll.setEnabled(t);
		txtAuthorisedBy.setEnabled(t);
		txtRemarks.setEnabled(t);

		table.setEnabled(t);
	}

	private void txtClear()
	{
		dDateOfCancellation.setValue(new Date());
		dFromDate.setValue(new Date());
		dToDate.setValue(new Date());
		cmbUnit.setValue(null);
		cmbDepartment.setValue(null);
		cmbSectionName.setValue(null);
		cmbEmployeeName.setValue(null);
		cmbReferenceNo.setValue(null);
		chkSectionAll.setValue(false);
		txtAuthorisedBy.setValue("");
		txtRemarks.setValue("");

		tableClear();
	}

	private void tableClear()
	{
		for(int i = 0; i<tbllblEmployeeName.size(); i++)
		{
			tbllblEmployeeName.get(i).setValue("");
			tbldDate.get(i).setReadOnly(false);
			tbldDate.get(i).setValue(null);
			tbldDate.get(i).setReadOnly(true);
			tbllblDayName.get(i).setValue("");
			tbllblAdjustedType.get(i).setValue("");
			tbllblPaymentType.get(i).setValue("");
			tblChkCancle.get(i).setValue(false);
		}
	}

	private void focusEnter()
	{
		allComp.add(dDateOfCancellation);
		allComp.add(dFromDate);
		allComp.add(dToDate);
		allComp.add(cmbUnit);
		allComp.add(cmbSectionName);
		allComp.add(cmbEmployeeName);
		allComp.add(cButton.btnSave);

		new FocusMoveByEnter(this,allComp);
	}

	public AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("765px");
		mainLayout.setHeight("550px");
		mainLayout.setMargin(false);

		lblCancelDate = new Label("Cancel Date : ");
		lblCancelDate.setImmediate(false);
		lblCancelDate.setWidth("-1px");
		lblCancelDate.setHeight("-1px");
		mainLayout.addComponent(lblCancelDate, "top:20.0px; left:30.0px;");

		dDateOfCancellation = new PopupDateField();
		dDateOfCancellation.setImmediate(false);
		dDateOfCancellation.setWidth("110px");
		dDateOfCancellation.setHeight("-1px");
		dDateOfCancellation.setValue(new java.util.Date());
		dDateOfCancellation.setDateFormat("dd-MM-yyyy");
		dDateOfCancellation.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dDateOfCancellation, "top:18.0px; left:200.0px;");

		lblLeaveApplicationDate = new Label("Application Date : ");
		lblLeaveApplicationDate.setImmediate(false);
		lblLeaveApplicationDate.setWidth("-1px");
		lblLeaveApplicationDate.setHeight("-1px");
		mainLayout.addComponent(lblLeaveApplicationDate, "top:50.0px; left:30.0px;");

		dFromDate = new PopupDateField();
		dFromDate.setImmediate(false);
		dFromDate.setWidth("110px");
		dFromDate.setHeight("-1px");
		dFromDate.setValue(new java.util.Date());
		dFromDate.setDateFormat("dd-MM-yyyy");
		dFromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dFromDate, "top:48.0px; left:200.0px;");

		lblTo = new Label("To :");
		lblTo.setImmediate(false);
		lblTo.setWidth("-1px");
		lblTo.setHeight("-1px");
		mainLayout.addComponent(lblTo, "top:50.0px; left:320.0px;");

		dToDate = new PopupDateField();
		dToDate.setImmediate(false);
		dToDate.setWidth("110px");
		dToDate.setHeight("-1px");
		dToDate.setValue(new java.util.Date());
		dToDate.setDateFormat("dd-MM-yyyy");
		dToDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dToDate, "top:48.0px; left:343.0px;");

		lblUnit = new Label("Project :");
		lblUnit.setImmediate(false);
		lblUnit.setWidth("-1px");
		lblUnit.setHeight("-1px");
		mainLayout.addComponent(lblUnit, "top:80.0px; left:30.0px;");

		cmbUnit = new ComboBox();
		cmbUnit.setImmediate(true);
		cmbUnit.setWidth("255px");
		cmbUnit.setHeight("22px");
		mainLayout.addComponent(cmbUnit, "top:78.0px; left:200.0px;");
		
		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("255px");
		cmbDepartment.setHeight("22px");
		mainLayout.addComponent(new Label("Department :"), "top:110px; left:30px;");
		mainLayout.addComponent(cmbDepartment, "top:108.0px; left:200.0px;");


		chkDepartmentAll = new CheckBox("All");
		chkDepartmentAll.setHeight("-1px");
		chkDepartmentAll.setWidth("-1px");
		chkDepartmentAll.setImmediate(true);
		mainLayout.addComponent(chkDepartmentAll, "top:110.0px; left:460.0px;");
		
		lblSectionName = new Label("Section :");
		lblSectionName.setImmediate(false);
		lblSectionName.setWidth("-1px");
		lblSectionName.setHeight("-1px");
		mainLayout.addComponent(lblSectionName, "top:140px; left:30.0px;");

		cmbSectionName = new ComboBox();
		cmbSectionName.setImmediate(true);
		cmbSectionName.setWidth("255px");
		cmbSectionName.setHeight("22px");
		mainLayout.addComponent(cmbSectionName, "top:138px; left:200.0px;");


		chkSectionAll = new CheckBox("All");
		chkSectionAll.setHeight("-1px");
		chkSectionAll.setWidth("-1px");
		chkSectionAll.setImmediate(true);
		mainLayout.addComponent(chkSectionAll, "top:140px; left:460.0px;");

		radioButtonGroup = new OptionGroup("",lst);
		radioButtonGroup.setImmediate(true);
		radioButtonGroup.setStyleName("horizontal");
		radioButtonGroup.select("Employee Name");
		mainLayout.addComponent(radioButtonGroup, "top:170px;left:25.0px;");

		lblEmployeeName = new Label("Employee Name :");
		lblEmployeeName.setImmediate(false);
		lblEmployeeName.setWidth("-1px");
		lblEmployeeName.setHeight("-1px");
		mainLayout.addComponent(lblEmployeeName, "top:200px; left:30.0px;");

		cmbEmployeeName = new ComboBox();
		cmbEmployeeName.setImmediate(true);
		cmbEmployeeName.setWidth("255px");
		cmbEmployeeName.setHeight("-1px");
		cmbEmployeeName.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbEmployeeName, "top:198px; left:200.0px;");

		lblReferenceNo = new Label("Referene No :");
		lblReferenceNo.setImmediate(false);
		lblReferenceNo.setWidth("-1px");
		lblReferenceNo.setHeight("-1px");
		mainLayout.addComponent(lblReferenceNo, "top:230px; left:30.0px;");

		cmbReferenceNo = new ComboBox();
		cmbReferenceNo.setImmediate(true);
		cmbReferenceNo.setWidth("255px");
		cmbReferenceNo.setHeight("22px");
		mainLayout.addComponent(cmbReferenceNo, "top:228px; left:200.0px;");

		txtAuthorisedBy = new TextField();
		txtAuthorisedBy.setImmediate(true);
		txtAuthorisedBy.setHeight("-1px");
		txtAuthorisedBy.setWidth("210.0px");
		txtAuthorisedBy.setCaption("Cancel Authorised By :");
		mainLayout.addComponent(txtAuthorisedBy,"top:30.0px; left:510.0px;");

		txtRemarks = new TextField();
		txtRemarks.setImmediate(true);
		txtRemarks.setHeight("126.0px");
		txtRemarks.setWidth("210.0px");
		txtRemarks.setCaption("Reason of Cancellation :");
		mainLayout.addComponent(txtRemarks,"top:75.0px; left:510.0px;");

		table.setWidth("700px");
		table.setHeight("250px");
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("SL#", Label.class , new Label());
		table.setColumnWidth("SL#",20);

		table.addContainerProperty("Name", Label.class , new Label());
		table.setColumnWidth("Name",200);

		table.addContainerProperty("Date", PopupDateField.class , new PopupDateField());
		table.setColumnWidth("Date",82);

		table.addContainerProperty("Day", Label.class , new Label());
		table.setColumnWidth("Day",75);

		table.addContainerProperty("Adjusted Type", Label.class, new Label());
		table.setColumnWidth("Adjusted Type",88);

		table.addContainerProperty("Payment Type", Label.class, new Label());
		table.setColumnWidth("Payment Type",88);

		table.addContainerProperty("Cancel", CheckBox.class , new CheckBox() ,null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Cancel",32);

		tableInitialise();

		mainLayout.addComponent(table,"top:260px;left:30.0px;");
		mainLayout.addComponent(cButton, "bottom:10px; left:200.0px;");
		return mainLayout;
	}

	public void tableInitialise()
	{
		for(int i=0;i<10;i++)
		{
			tableRowAdd(i);
		}
	}

	public void tableRowAdd(final int ar)
	{
		tbllblSerial.add(ar,new Label());
		tbllblSerial.get(ar).setWidth("100%");
		tbllblSerial.get(ar).setValue(ar+1);

		tbllblEmployeeName.add(ar,new Label());
		tbllblEmployeeName.get(ar).setWidth("100%");

		tbldDate.add(ar,new PopupDateField());
		tbldDate.get(ar).setWidth("100%");
		tbldDate.get(ar).setDateFormat("dd-MM-yyyy");
		tbldDate.get(ar).setResolution(PopupDateField.RESOLUTION_DAY);
		tbldDate.get(ar).setImmediate(true);
		tbldDate.get(ar).setReadOnly(true);

		tbllblDayName.add(ar,new Label());
		tbllblDayName.get(ar).setWidth("100%");

		tbllblAdjustedType.add(ar,new Label());
		tbllblAdjustedType.get(ar).setWidth("100%");

		tbllblPaymentType.add(ar,new Label());
		tbllblPaymentType.get(ar).setWidth("100%");

		tblChkCancle.add(ar,new CheckBox());
		tblChkCancle.get(ar).setWidth("100%");
		tblChkCancle.get(ar).setImmediate(true);
		tblChkCancle.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(tblChkCancle.get(ar).booleanValue())
				{
					if(tbllblEmployeeName.get(ar).getValue().toString().isEmpty())
					{
						showNotification("Warning!","No data found.",Notification.TYPE_WARNING_MESSAGE);
						tblChkCancle.get(ar).setValue(false);
					}
				}
			}
		});

		table.addItem(new Object[]{tbllblSerial.get(ar), tbllblEmployeeName.get(ar), tbldDate.get(ar),
				tbllblDayName.get(ar), tbllblAdjustedType.get(ar), tbllblPaymentType.get(ar), tblChkCancle.get(ar)},ar);
	}
}