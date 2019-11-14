package com.appform.hrmModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.CommonMethod;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.ReportDate;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Button.ClickShortcut;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class UnitAndDepartmentWiseShiftInformation extends Window 
{
	CommonButton cButton = new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "","","Exit");

	private TextField txtDeptId = new TextField();
	private TextField txtSecID = new TextField();
	private TextField txtshiftID = new TextField();

	boolean isFind = false;
	boolean isUpdate = false;
	boolean isNew = false;

	private AbsoluteLayout mainLayout;

	private Label lblCommon;

	private ComboBox cmbUnit;
	private ComboBox cmbDepartment;
	private ComboBox cmbShiftName;

	private InlineDateField dStartTime;
	private InlineDateField dStartTimeLimit;

	private InlineDateField dEndTime;
	private InlineDateField dEndTimeLimit;

	SessionBean sessionBean;

	ArrayList<Component> allComp = new ArrayList<Component>();

	private Label lblIsActive;
	private NativeSelect cmbIsActive;
	private static final String[] status = new String[] { "Inactive", "Active" };

	SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss aa");
	ReportDate reportTime = new ReportDate();
	private CommonMethod cm;
	private String menuId = "";
	public UnitAndDepartmentWiseShiftInformation(SessionBean sessionBean,String menuId)
	{
		this.sessionBean=sessionBean;
		this.setResizable(false);
		this.setCaption("PROJECT/DEPARTMENT WISE SHIFT INFORMATION :: "+sessionBean.getCompany());
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		buildMainLayout();
		setContent(mainLayout);

		txtInit(true);
		btnIni(true);

		focusEnter();
		btnAction();

		authenticationCheck();
		setButtonShortCut();
		cmbUnitDataAdd();

		cButton.btnNew.focus();
	}

	private void setButtonShortCut()
	{
		this.addAction(new ClickShortcut(cButton.btnSave, KeyCode.S, ModifierKey.ALT));
		this.addAction(new ClickShortcut(cButton.btnNew, KeyCode.N, ModifierKey.ALT));
		this.addAction(new ClickShortcut(cButton.btnRefresh, KeyCode.R, ModifierKey.ALT));
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

	public void btnAction()
	{
		cmbUnit.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbDepartment.removeAllItems();
				if(cmbUnit.getValue()!=null)
				{
					cmbDepartmentDataAdd();
					cmbShiftName.removeAllItems();
					if(cmbDepartment.getValue()!=null)
						cmbShiftNameDataAdd();
				}
			}
		});

		cmbDepartment.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbShiftName.removeAllItems();
				if(cmbDepartment.getValue()!=null)
					cmbShiftNameDataAdd();
			}
		});

		cmbShiftName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbShiftName.getValue()!=null)
				{
					shiftTimeAdd();
				}
			}
		});

		cButton.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate = false;
				isFind = false;
				txtInit(false);
				btnIni(false);
				txtClear();

				cmbShiftName.focus();
			}
		});

		cButton.btnSave.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				formValidation();
			}
		});

		cButton.btnEdit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				updateButtonEvent();
			}
		});

		cButton.btnRefresh.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate = false;
				isFind = false;
				refreshButtonEvent();
			}
		});

		cButton.btnFind.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				txtDeptId.setValue("");
				txtSecID.setValue("");
				txtshiftID.setValue("");
				findButtonEvent();
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
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select distinct vUnitId,vUnitName from tbEmpOfficialPersonalInfo order by vUnitName";
			List <?> lstDept = session.createSQLQuery(query).list();
			if(!lstDept.isEmpty())
			{
				for(Iterator <?> itr = lstDept.iterator(); itr.hasNext(); )
				{
					Object [] element = (Object[])itr.next();
					cmbUnit.addItem(element[0]);
					cmbUnit.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbUnitDataAdd", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}

	private void cmbDepartmentDataAdd()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select distinct vDepartmentId,vDepartmentName from tbEmpOfficialPersonalInfo where vUnitId = '"+cmbUnit.getValue()+"' " +
					"order by vDepartmentName";
			List <?> lstDept = session.createSQLQuery(query).list();
			if(!lstDept.isEmpty())
			{
				for(Iterator <?> itr = lstDept.iterator(); itr.hasNext(); )
				{
					Object [] element = (Object[])itr.next();
					cmbDepartment.addItem(element[0]);
					cmbDepartment.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbDepartmentDataAdd", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}

	private void cmbShiftNameDataAdd()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "";
			if(!isFind)
			{
				query = "select vShiftId,vShiftName from tbShiftInfo where isActive = 1 and vShiftID " +
						" not in (select vShiftID from tbUnit_Section_Wise_ShiftInfo where vUnitID = '"+cmbUnit.getValue()+"' " +
						" and vDepartmentID = '"+cmbDepartment.getValue()+"') order by vShiftName";
			}
			else
			{
				query = "select vShiftId,vShiftName from tbShiftInfo where isActive = 1 order by vShiftName";
			}
			List <?> lstDept = session.createSQLQuery(query).list();
			if(!lstDept.isEmpty())
			{
				for(Iterator <?> itr = lstDept.iterator(); itr.hasNext(); )
				{
					Object [] element = (Object[])itr.next();
					cmbShiftName.addItem(element[0]);
					cmbShiftName.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbShiftNameDataAdd", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}

	private void formValidation()
	{
		if(cmbUnit.getValue()!=null)
		{
			if(cmbDepartment.getValue()!=null)
			{
				if(cmbShiftName.getValue()!=null)
				{
					saveButtonEvent();
				}
				else
				{
					getParent().showNotification("Warning","Select Shift Name!!!", Notification.TYPE_WARNING_MESSAGE);
					cmbShiftName.focus();
				}
			}
			else
			{
				getParent().showNotification("Warning","Select Division Name!!!", Notification.TYPE_WARNING_MESSAGE);
				cmbDepartment.focus();
			}
		}
		else
		{
			getParent().showNotification("Warning","Select Unit Name!!!", Notification.TYPE_WARNING_MESSAGE);
			cmbUnit.focus();
		}
	}

	private void shiftTimeAdd()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select dShiftStart,dShiftEnd,dLateInLimit,dEarlyOutLimit from tbShiftInfo where vShiftId = '"+cmbShiftName.getValue()+"' and isActive = 1";
			List <?> lstDept = session.createSQLQuery(query).list();
			if(!lstDept.isEmpty())
			{
				for(Iterator <?> itr = lstDept.iterator(); itr.hasNext(); )
				{
					Object [] element = (Object[])itr.next();
					dStartTime.setValue(element[0]);
					dEndTime.setValue(element[1]);
					dStartTimeLimit.setValue(element[2]);
					dEndTimeLimit.setValue(element[3]);
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbShiftNameDataAdd", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}

	private void findButtonEvent() 
	{
		isFind = true;
		Window win = new UnitAndDepartmentWiseShiftInformationFind(sessionBean, txtDeptId, txtSecID, txtshiftID);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if (txtDeptId.getValue().toString().length() > 0 && txtSecID.getValue().toString().length() > 0 && txtshiftID.getValue().toString().length() > 0)
				{
					txtClear();
					findInitialise(txtDeptId.getValue().toString(),txtSecID.getValue().toString(),txtshiftID.getValue().toString());
				}
			}
		});
		this.getParent().addWindow(win);	
	}

	private void findInitialise(String dept, String Department, String shift) 
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "select vUnitId,vDepartmentId,vShiftID,dShiftStart,dShiftEnd,dLateInLimit,dEarlyOutLimit,isActive from" +
					" tbUnit_Section_Wise_ShiftInfo Where vUnitID = '"+dept+"' and vDepartmentID = '"+Department+"' and vShiftID = '"+shift+"'";
			System.out.println("Hello"+sql);
			List <?> led = session.createSQLQuery(sql).list();

			if (led.iterator().hasNext()) 
			{
				Object[] element = (Object[]) led.iterator().next();

				cmbUnit.setValue(element[0]);
				cmbDepartment.setValue(element[1]);
				cmbShiftName.setValue(element[2]);
				dStartTime.setValue(element[3]);
				dEndTime.setValue(element[4]);
				dStartTimeLimit.setValue(element[5]);
				dEndTimeLimit.setValue(element[6]);
				cmbIsActive.setValue(element[7]);

			}
		}
		catch (Exception exp) 
		{
			showNotification("findInitialise", exp + "",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void refreshButtonEvent() 
	{
		txtInit(true);
		btnIni(true);
		txtClear();
		isNew=false;
	}

	private void updateButtonEvent()
	{
		isUpdate = true;
		btnIni(false);
		txtInit(false);
	}

	private boolean chkExistsData(String query)
	{
		boolean ret = false;
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> lst = session.createSQLQuery(query).list();
			if(!lst.isEmpty())
				ret = true;
		}
		catch(Exception exp)
		{
			showNotification("chkExistsData", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
		return ret;
	}
	
	private void saveButtonEvent()
	{
		if(isUpdate)
		{
			String checkExists = "select * from tbUnit_Section_Wise_ShiftInfo where vUnitID = '"+cmbUnit.getValue()+"' " +
					" and vDepartmentID = '"+cmbDepartment.getValue()+"' and vShiftId = '"+cmbShiftName.getValue().toString()+"' ";
			if(!chkExistsData(checkExists))
			{
				MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to update information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
				mb.show(new EventListener()
				{
					public void buttonClicked(ButtonType buttonType)
					{
						if(buttonType == ButtonType.YES)
						{
							if(isUpdate)
							{
								updateData();

								btnIni(true);
								txtInit(true);
								txtClear();
								cButton.btnNew.focus();
							}
							isUpdate = false;
							isFind = false;
						}
					}
				});
			}
			else
			{
				showNotification("Warning", "Shift name already exists!!!", Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else
		{
			MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to save information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						insertData();		
						btnIni(true);
						txtInit(true);
						txtClear();

						cButton.btnNew.focus();
					}
				}
			});
		}
	}

	private void insertData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{
			String Insert = " INSERT into tbUnit_Section_Wise_ShiftInfo (vUnitID,vUnitName,vDepartmentID,vDepartmentName,vShiftId,"
					+ "vShiftName,dShiftStart,dShiftEnd,dLateInLimit,dEarlyOutLimit,isActive,vUserName,vUserIp,dEntryTime) values (" +
					" '"+cmbUnit.getValue()+"'," +
					" '"+cmbUnit.getItemCaption(cmbUnit.getValue())+"'," +
					" '"+cmbDepartment.getValue()+"'," +
					" '"+cmbDepartment.getItemCaption(cmbDepartment.getValue())+"'," +
					" '"+cmbShiftName.getValue().toString()+"'," +
					" '"+cmbShiftName.getItemCaption(cmbShiftName.getValue())+"'," +
					" '"+timeFormat.format(dStartTime.getValue())+"'," +
					" '"+timeFormat.format(dEndTime.getValue())+"'," +
					" '"+timeFormat.format(dStartTimeLimit.getValue())+"'," +
					" '"+timeFormat.format(dEndTimeLimit.getValue())+"'," +
					" '"+cmbIsActive.getValue().toString()+"'," +
					" '"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP) ";
			System.out.println("Hello"+Insert);

			session.createSQLQuery(Insert).executeUpdate();

			tx.commit();
			Notification n=new Notification("All Information Save Successfully!","",Notification.TYPE_TRAY_NOTIFICATION);
			n.setPosition(Notification.POSITION_TOP_RIGHT);
			showNotification(n);
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
		finally{session.close();}
	}

	public boolean updateData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();

		try
		{
			//Check existing for of update
			String query = " Select * from tbUDUnit_Section_Wise_ShiftInfo where vUnitID =" +
					" '"+txtDeptId.getValue().toString()+"' and vDepartmentID = '"+txtSecID.getValue().toString()+"' " +
					" and vShiftID = '"+txtshiftID.getValue().toString()+"' and UdFlag = 'New' ";

			System.out.println("Find"+query);

			Iterator <?> iter = session.createSQLQuery(query).list().iterator();

			if(!iter.hasNext())
			{
				//Insert new value for first time update
				String Insert = " INSERT into tbUDUnit_Section_Wise_ShiftInfo (vUnitID,vUnitName,vDepartmentID,vDepartmentName,vShiftId," +
						" vShiftName,dShiftStart,dShiftEnd,dLateInLimit,dEarlyOutLimit,isActive,UDFlag,vUserName,vUserIp,dEntryTime)" +
						" select vUnitID,vUnitName,vDepartmentID,vDepartmentName,vShiftId,vShiftName,dShiftStart,dShiftEnd,dLateInLimit," +
						" dEarlyOutLimit,isActive,'New',vUserName,vUserIp,dEntryTime from tbUnit_Section_Wise_ShiftInfo where vUnitID = '"+cmbUnit.getValue()+"' " +
						" and vDepartmentID = '"+cmbDepartment.getValue()+"' and vShiftID = '"+txtshiftID.getValue().toString()+"'";

				System.out.println("Insert"+Insert);
				session.createSQLQuery(Insert).executeUpdate();
			}

			// Main table update
			String delete = "delete from tbUnit_Section_Wise_ShiftInfo where vUnitID = '"+cmbUnit.getValue()+"' " +
					" and vDepartmentID = '"+cmbDepartment.getValue()+"' and vShiftId = '"+txtshiftID.getValue().toString()+"' ";

			System.out.println("delete"+delete);
			session.createSQLQuery(delete).executeUpdate();

			String InsertNew = " INSERT into tbUnit_Section_Wise_ShiftInfo (vUnitID,vUnitName,vDepartmentID,vDepartmentName,vShiftId,"
					+ "vShiftName,dShiftStart,dShiftEnd,dLateInLimit,dEarlyOutLimit,isActive,vUserName,vUserIp,dEntryTime) values (" +
					" '"+cmbUnit.getValue()+"'," +
					" '"+cmbUnit.getItemCaption(cmbUnit.getValue())+"'," +
					" '"+cmbDepartment.getValue()+"'," +
					" '"+cmbDepartment.getItemCaption(cmbDepartment.getValue())+"'," +
					" '"+cmbShiftName.getValue().toString()+"'," +
					" '"+cmbShiftName.getItemCaption(cmbShiftName.getValue())+"'," +
					" '"+timeFormat.format(dStartTime.getValue())+"'," +
					" '"+timeFormat.format(dEndTime.getValue())+"'," +
					" '"+timeFormat.format(dStartTimeLimit.getValue())+"'," +
					" '"+timeFormat.format(dEndTimeLimit.getValue())+"'," +
					" '"+cmbIsActive.getValue().toString()+"'," +
					" '"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP) ";

			session.createSQLQuery(InsertNew).executeUpdate();

			//Insert update or changes data
			String Insert = " INSERT into tbUDUnit_Section_Wise_ShiftInfo (vUnitID,vUnitName,vDepartmentID,vDepartmentName,vShiftId," +
					" vShiftName,dShiftStart,dShiftEnd,dLateInLimit,dEarlyOutLimit,isActive,UDFlag,vUserName,vUserIp,dEntryTime)" +
					" select vUnitID,vUnitName,vDepartmentID,vDepartmentName,vShiftId,vShiftName,dShiftStart,dShiftEnd,dLateInLimit," +
					" dEarlyOutLimit,isActive,'Update',vUserName,vUserIp,dEntryTime from tbUnit_Section_Wise_ShiftInfo where vUnitID = '"+cmbUnit.getValue()+"' " +
					" and vDepartmentID = '"+cmbDepartment.getValue()+"' and vShiftID = '"+txtshiftID.getValue().toString()+"'";

			System.out.println("Insert"+Insert);
			session.createSQLQuery(Insert).executeUpdate();

			txtDeptId.setValue("");
			txtSecID.setValue("");
			txtshiftID.setValue("");

			Notification n=new Notification("All Information Updated Successfully!","",Notification.TYPE_TRAY_NOTIFICATION);
			n.setPosition(Notification.POSITION_TOP_RIGHT);
			showNotification(n);

			tx.commit();

			return true;
		}
		catch(Exception exp)
		{
			tx.rollback();
			this.getParent().showNotification("Error to Update",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
		return false;
	}

	private void focusEnter()
	{
		allComp.add(cmbShiftName);
		allComp.add(dStartTime);
		allComp.add(dEndTime);
		allComp.add(cmbIsActive);

		allComp.add(cButton.btnSave);

		new FocusMoveByEnter(this,allComp);
	}

	private void txtClear()
	{
		cmbUnit.setValue(null);
		cmbDepartment.setValue(null);
		cmbShiftName.setValue(null);
		cmbIsActive.setValue(1);
	}

	public void txtInit(boolean t)
	{
		cmbUnit.setEnabled(!t);
		cmbDepartment.setEnabled(!t);
		cmbShiftName.setEnabled(!t);
		dStartTime.setEnabled(false);
		dStartTimeLimit.setEnabled(false);
		dEndTime.setEnabled(false);
		dEndTimeLimit.setEnabled(false);
		cmbIsActive.setEnabled(!t);
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
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("630px");
		mainLayout.setHeight("270px");
		mainLayout.setMargin(false);

		lblCommon = new Label("Project Name : ");
		mainLayout.addComponent(lblCommon, "top:20.0px; left:20.0px;");

		cmbUnit = new ComboBox();
		cmbUnit.setImmediate(true);
		cmbUnit.setWidth("300.0px");
		mainLayout.addComponent(cmbUnit, "top:18.0px; left:130.0px;");

		lblCommon = new Label("Department Name : ");
		mainLayout.addComponent(lblCommon, "top:50.0px; left:20.0px;");

		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("300.0px");
		mainLayout.addComponent(cmbDepartment, "top:48.0px; left:130.0px;");

		lblCommon = new Label("Shift Name :");
		mainLayout.addComponent(lblCommon, "top:80.0px; left:20.0px;");

		cmbShiftName = new ComboBox();
		cmbShiftName.setImmediate(true);
		cmbShiftName.setWidth("250px");
		cmbShiftName.setHeight("-1px");
		mainLayout.addComponent(cmbShiftName, "top:78.0px; left:130px;");

		lblCommon = new Label("Shift Start Time :");
		mainLayout.addComponent(lblCommon, "top:110.0px; left:20.0px;");

		dStartTime = new InlineDateField();
		dStartTime.setImmediate(true);
		dStartTime.setWidth("110px");
		dStartTime.setHeight("-1px");
		dStartTime.setDateFormat("hh:mm:ss");
		dStartTime.setValue(reportTime.getTime);
		dStartTime.setResolution(InlineDateField.RESOLUTION_SEC);
		mainLayout.addComponent(dStartTime, "top:110.0px; left:130px;");

		lblCommon = new Label("Late In Time Limit :");
		mainLayout.addComponent(lblCommon, "top:110.0px; left:320.0px;");

		dStartTimeLimit = new InlineDateField();
		dStartTimeLimit.setImmediate(true);
		dStartTimeLimit.setWidth("110px");
		dStartTimeLimit.setHeight("-1px");
		dStartTimeLimit.setDateFormat("hh:mm:ss");
		dStartTimeLimit.setValue(reportTime.getTime);
		dStartTimeLimit.setResolution(InlineDateField.RESOLUTION_SEC);
		mainLayout.addComponent(dStartTimeLimit, "top:110.0px; left:440px;");

		lblCommon = new Label("Shift End Time :");
		mainLayout.addComponent(lblCommon, "top:140.0px; left:20.0px;");

		dEndTime = new InlineDateField();
		dEndTime.setImmediate(true);
		dEndTime.setWidth("110px");
		dEndTime.setHeight("-1px");
		dEndTime.setResolution(InlineDateField.RESOLUTION_SEC);
		dEndTime.setDateFormat("hh:mm:ss");
		dEndTime.setValue(reportTime.getTime);
		mainLayout.addComponent(dEndTime, "top:140.0px; left:130.0px;");

		lblCommon = new Label("Early Out Time Limit :");
		mainLayout.addComponent(lblCommon, "top:140.0px; left:320.0px;");

		dEndTimeLimit = new InlineDateField();
		dEndTimeLimit.setImmediate(true);
		dEndTimeLimit.setWidth("110px");
		dEndTimeLimit.setHeight("-1px");
		dEndTimeLimit.setResolution(InlineDateField.RESOLUTION_SEC);
		dEndTimeLimit.setDateFormat("hh:mm:ss");
		dEndTimeLimit.setValue(reportTime.getTime);
		mainLayout.addComponent(dEndTimeLimit, "top:140.0px; left:440.0px;");

		lblIsActive = new Label("Status :");
		lblIsActive.setImmediate(false);
		lblIsActive.setWidth("-1px");
		lblIsActive.setHeight("-1px");
		mainLayout.addComponent(lblIsActive, "top:170.0px; left:20.0px;");

		cmbIsActive = new NativeSelect();
		cmbIsActive.setNullSelectionAllowed(false);
		cmbIsActive.setImmediate(true);
		cmbIsActive.setWidth("80px");
		cmbIsActive.setHeight("-1px");
		for (int i = 0; i < status.length; i++)
		{
			cmbIsActive.addItem(i);
			cmbIsActive.setItemCaption(i, status[i]);
		}
		cmbIsActive.setValue(1);
		mainLayout.addComponent(cmbIsActive,"top:168.0px; left:130.0px;");
		mainLayout.addComponent(cButton,"top:210px; left:58px;");
		return mainLayout;
	}
}