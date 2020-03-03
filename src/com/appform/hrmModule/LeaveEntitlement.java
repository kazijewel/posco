package com.appform.hrmModule;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.AmountField;
import com.common.share.CommonButton;
import com.common.share.CommonMethod;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class LeaveEntitlement extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;
	private Label lblCommon;
	private ComboBox cmbUnitName,cmbDepartment,cmbSectioName,cmbLeaveType;
	private CheckBox chkDepartmentAll,chkSectionAll;
	
	private PopupDateField dEntryDate;

	private TextRead findId = new TextRead();
	private TextRead EmpId = new TextRead();

	private Table table = new Table();
	
	private ArrayList<Label> tblblSl = new ArrayList<Label>();
	private ArrayList<Label> tblblEmployeeID = new ArrayList<Label>();
	private ArrayList<Label> tblblEmployeeCode = new ArrayList<Label>();
	private ArrayList<Label> tblblEmployeeName = new ArrayList<Label>();
	private ArrayList<Label> tblblDesignation = new ArrayList<Label>();
	private ArrayList<Label> tblblUnitId = new ArrayList<Label>();
	private ArrayList<Label> tblblDepartmentId = new ArrayList<Label>();
	private ArrayList<Label> tblblSectionId = new ArrayList<Label>();
	private ArrayList<Label> tblblJoiningDate = new ArrayList<Label>();
	
	private ArrayList<Label> tblblEntitleFromDate = new ArrayList<Label>();
	private ArrayList<Label> tblblEntitleToDate = new ArrayList<Label>();
	private ArrayList<TextField> tbTxtEntitleDays = new ArrayList<TextField>();
	private ArrayList<Label> tblblUpdateDate = new ArrayList<Label>();

	private CommonButton button = new CommonButton("New", "Save", "Edit", "","Refresh","Find","","","","Exit");

	private ArrayList<Component> allComp = new ArrayList<Component>();
	private DecimalFormat decimal = new DecimalFormat("#0");
	private SimpleDateFormat dfYear = new SimpleDateFormat("yyyy");

	private CommonMethod cm;
	private String menuId = "";
	
	private boolean isUpdate = false;
	private boolean isRefresh = false;
	private boolean isFind = false;
	
	
	public LeaveEntitlement(SessionBean sessionBean,String menuId)
	{
		this.sessionBean = sessionBean;
		this.setCaption("LEAVE ENTITLEMENT :: "+sessionBean.getCompany());
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		buildMainLayout();
		setContent(mainLayout);
		btnIni(true);
		componentIni(true);
		addUnitData();
		leaveTypeDataLoad();
		buttonAction();
		focusEnter();
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
				{button.btnSave.setVisible(false);}
				if(!cm.isEdit)
				{button.btnEdit.setVisible(false);}
				if(!cm.isDelete)
				{button.btnDelete.setVisible(false);}
				if(!cm.isPreview)
				{button.btnPreview.setVisible(false);}
			}
		}
	}

	private void buttonAction()
	{
		button.btnNew.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isRefresh = false;
				isUpdate = false;
				isFind = false;
				newButtonEvent();
			}
		});

		button.btnSave.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				formValidation();
			}
		});

		button.btnEdit.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbUnitName.getValue()!=null)
				{
					isUpdate = true;
					componentIni(false);
					btnIni(false);
					
					cmbUnitName.setEnabled(false);
					cmbDepartment.setEnabled(false);
					chkDepartmentAll.setEnabled(false);
					cmbSectioName.setEnabled(false);
					chkSectionAll.setEnabled(false);
				}
				else
				{
					showNotification("Warning!","No data to update.",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		button.btnRefresh.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				isRefresh = true;
				isUpdate = false;
				isFind = false;
				refreshButtonEvent();
			}
		});

		button.btnFind.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isFind = true;
				findButtonEvent();
			}
		});

		button.btnExit.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

		dEntryDate.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(dEntryDate.getValue()!=null )
				{
					tableClear();
					cmbDepartment.setValue(null);
					cmbLeaveType.setValue(null);
				}
			}
		});

		cmbUnitName.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbUnitName.getValue()!=null )
				{
					tableClear();
					cmbDepartment.removeAllItems();
					cmbLeaveType.setValue(null);
					addDepartmentData();
				}
			}
		});
		
		cmbDepartment.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				tableClear();
				if(cmbDepartment.getValue()!=null )
				{
					cmbSectioName.removeAllItems();
					cmbLeaveType.setValue(null);
					addSectionData();
				}
				
			}
		});
		
		chkDepartmentAll.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				tableClear();
				if(cmbUnitName.getValue()!=null)
				{
					if(chkDepartmentAll.booleanValue() )
					{
						addSectionData();
						cmbDepartment.setValue(null);
						cmbDepartment.setEnabled(false);
						cmbLeaveType.setValue(null);
					}
					else
					{
						cmbDepartment.setEnabled(true);
					}
				}
			}
		});
		
		cmbSectioName.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				tableClear();
				cmbLeaveType.setValue(null);
			}
		});
		
		chkSectionAll.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				tableClear();
				if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
				{
					if(chkSectionAll.booleanValue() )
					{
						if(!isFind){
							cmbSectioName.setEnabled(false);
							cmbLeaveType.setValue(null);
							tableClear();
							//tableDataLoad();
						}
						cmbSectioName.setValue(null);
						cmbSectioName.setEnabled(false);
					}
					else
					{
						tableClear();
						cmbSectioName.setEnabled(true);
					}
				}
			}
		});
		
		cmbLeaveType.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				tableClear();
				if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
				{
					if(cmbSectioName.getValue()!=null || chkSectionAll.booleanValue())
					{
						if(cmbLeaveType.getValue()!=null )
						{
							if(!isFind)
							{
								tableClear();
								tableDataLoad();
							}
						}
					}
					else
					{
						tableClear();
					}
				}
				else
				{
					tableClear();
				}
			}
		});
	}

	private void formValidation()
	{
		if(cmbUnitName.getValue()!=null)
		{
			if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
			{
				if(cmbSectioName.getValue()!=null || chkSectionAll.booleanValue())
				{
					if(cmbLeaveType.getValue()!=null)
					{
						if(!tblblEmployeeName.get(0).getValue().toString().isEmpty())
						{
							saveButtonAction();
						}
						else
						{
							showNotification("Warning","No employee found!!!", Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else
					{
						cmbLeaveType.focus();
						showNotification("Warning","Select Leave type!!!", Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					cmbSectioName.focus();
					showNotification("Warning","Select Section!!!", Notification.TYPE_WARNING_MESSAGE);
				}
			}
			else
			{
				cmbDepartment.focus();
				showNotification("Warning","Select Department!!!", Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else
		{
			cmbSectioName.focus();
			showNotification("Warning","Select Project!!!", Notification.TYPE_WARNING_MESSAGE);
		}
	}
	
	private void addUnitData()
	{
		cmbUnitName.removeAllItems();
	
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{	
			String query = "select distinct vUnitId,vUnitName from tbEmpOfficialPersonalInfo order by vUnitName ";
			
			System.out.println("unit"+query);
			
			List <?> list = session.createSQLQuery(query).list();

			for (Iterator <?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element =  (Object[]) iter.next();	
				cmbUnitName.addItem(element[0]);
				cmbUnitName.setItemCaption(element[0], element[1].toString());	
			}
		}
		catch(Exception ex)
		{
			this.getParent().showNotification("addUnitData", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	
	private void leaveTypeDataLoad()
	{
		cmbLeaveType.removeAllItems();
	
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{	
			String query = "select vLeaveTypeId,vLeaveTypeName from tbLeaveType order by vLeaveTypeName ";
			
			System.out.println("leaveTypeDataLoad: "+query);
			
			List <?> list = session.createSQLQuery(query).list();

			for (Iterator <?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element =  (Object[]) iter.next();	
				cmbLeaveType.addItem(element[0]);
				cmbLeaveType.setItemCaption(element[0], element[1].toString());	
			}
		}
		catch(Exception ex)
		{
			this.getParent().showNotification("leaveTypeDataLoad", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void refreshButtonEvent() 
	{
		txtClear();	
		componentIni(true);
		btnIni(true);
	}
	private void addDepartmentData()
	{
		cmbDepartment.removeAllItems();
		
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select distinct vDepartmentId,vDepartmentName from tbEmpOfficialPersonalInfo "
					+ "where vUnitId ='"+cmbUnitName.getValue().toString()+"' and bStatus=1  order by vDepartmentName " ;
					
			System.out.println("section"+query);	
					
			List <?> list = session.createSQLQuery(query).list();

			for (Iterator <?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element =  (Object[]) iter.next();	
				cmbDepartment.addItem(element[0]);
				cmbDepartment.setItemCaption(element[0], element[1].toString());	
			}
		}
		catch(Exception ex)
		{
	       this.getParent().showNotification("addSectionData", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void addSectionData()
	{
		cmbSectioName.removeAllItems();
		
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select distinct vSectionId,vSectionName from tbEmpOfficialPersonalInfo "
					+ "where vUnitId ='"+cmbUnitName.getValue().toString()+"' and vDepartmentId like '"+(cmbDepartment.getValue()==null?"%":cmbDepartment.getValue().toString())+"'  order by vSectionName " ;
					
			System.out.println("section"+query);	
					
			List <?> list = session.createSQLQuery(query).list();

			for (Iterator <?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element =  (Object[]) iter.next();	
				cmbSectioName.addItem(element[0]);
				cmbSectioName.setItemCaption(element[0], element[1].toString());	
			}
		}
		catch(Exception ex)
		{
	       this.getParent().showNotification("addSectionData", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void tableDataLoad()
	{
		String unitId = cmbUnitName.getValue()!=null?cmbUnitName.getValue().toString():"";
		String deptId = cmbDepartment.getValue()!=null?cmbDepartment.getValue().toString():"%";
		String sectionId = cmbSectioName.getValue()!=null?cmbSectioName.getValue().toString():"%";
		String leaveType = cmbLeaveType.getValue()!=null?cmbLeaveType.getValue().toString():"";

		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{			
			String query="select vEmployeeId,vEmployeeCode,vEmployeeName,vDesignationName,dJoiningDate,vUnitId,vDepartmentId,vSectionId,"
					+ "dEntitleFromDate,dEntitleToDate,mEntitleDays,dUpdateDate "
					+ "from funLeaveEntitlement "
					+ "( "
					+ "'"+(dEntryDate.getValue()!=null?sessionBean.dfDb.format(dEntryDate.getValue()):sessionBean.dfDb.format(new Date()))+"',"
					+ "'"+unitId+"','"+deptId+"','"+sectionId+"','"+leaveType+"' "
					+ ")";
			
			System.out.println("tableDataLoad: "+query);	
			
			List <?> list = session.createSQLQuery(query).list();
	
			if(!list.isEmpty())
			{
				int i=0;
				for(Iterator <?> iter = list.iterator(); iter.hasNext();)
				{				  
					Object[] element = (Object[]) iter.next();

					tblblEmployeeID.get(i).setValue(element[0].toString());
					tblblEmployeeCode.get(i).setValue(element[1]);
					tblblEmployeeName.get(i).setValue(element[2].toString());
					tblblDesignation.get(i).setValue(element[3].toString());
					tblblJoiningDate.get(i).setValue(element[4].toString());
					tblblUnitId.get(i).setValue(element[5].toString());
					tblblDepartmentId.get(i).setValue(element[6].toString());
					tblblSectionId.get(i).setValue(element[7].toString());
					
					tblblEntitleFromDate.get(i).setValue(element[8].toString());
					tblblEntitleToDate.get(i).setValue(element[9].toString());
					tbTxtEntitleDays.get(i).setValue(element[10].toString());
					tblblUpdateDate.get(i).setValue(element[11].toString());

					if((i)==tblblEmployeeID.size()-1)
					{
						tableRowAdd(i+1);
					}
					i++;
				}
			}
			else
			{
				tableClear();
				this.getParent().showNotification("Warning","No employee found or data already exist!!!", Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch (Exception ex)
		{
			this.getParent().showNotification("tableDataLoad", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void newButtonEvent() 
	{
		cmbUnitName.focus();
		txtClear();
		componentIni(false);
		btnIni(false);
	}

	private void findButtonEvent()
	{
		Window win = new LeaveEntitlementFind(sessionBean, findId, EmpId);
		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if(findId.getValue().toString().length()>0)
				{
					findInitialize(findId.getValue().toString(),EmpId.getValue().toString());
				}
			}
		});
		this.getParent().addWindow(win);
	}

	private void findInitialize(String findId, String strEmpID)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String findQuery = "select vUnitId,vSectionId,vEmployeeId," +
					"(select vEmployeeCode from tbEmpOfficialPersonalInfo where vEmployeeId=eli.vEmployeeId)vEmployeeCode," +
					"vEmployeeName,(select vDesignation from tbEmpDesignationInfo where vEmployeeId=eli.vEmployeeId)vDesignation," +
					"dJoiningDate,iCasualLeave,iSickLeave,iEarnLeave,CONVERT(date,vYear) years,vSectionId," +
					"(select vDepartmentId from tbEmpOfficialPersonalInfo where vEmployeeId=eli.vEmployeeId)deptId,dEntryDate  " +
					"from tbLeaveEntitlement eli where vLeaveId = '"+findId+"' and vEmployeeId = '"+strEmpID+"'";
			
			System.out.println("FindQuery " + findQuery);
			List <?> list = session.createSQLQuery(findQuery).list();
			
			if(!list.isEmpty())
			{
				if(list.iterator().hasNext())
				{
					Object[] element = (Object[]) list.iterator().next();
					cmbUnitName.setValue(element[0].toString());
					cmbSectioName.setValue(element[1]);
					for(int i=0; i<list.size(); i++)
					{
						tblblEmployeeID.get(i).setValue(element[2].toString());
						tblblEmployeeCode.get(i).setValue(element[3]);
						tblblEmployeeName.get(i).setValue(element[4]);
						tblblDesignation.get(i).setValue(element[5]);
						tblblJoiningDate.get(i).setValue(element[6]);
						tbTxtEntitleDays.get(i).setValue(element[7]);
						tblblSectionId.get(i).setValue(element[11]);
					}
					cmbDepartment.setValue(element[12]);
					dEntryDate.setValue(element[13]);
				}
			}
			else
			{
				tableClear();
				this.getParent().showNotification("Warning!","Balance already exist.", Notification.TYPE_WARNING_MESSAGE); 
			}
		}
		catch (Exception ex)
		{
			this.getParent().showNotification("Khan", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void saveButtonAction()
	{
		if(isUpdate)
		{
			MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to update information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.setStyleName("cwindowMB");
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						updateData();
						isUpdate=false;
						isFind = false;
					}	
				}
			});
		}
		else
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
						isUpdate=false;
						isFind = false;
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
			String LeaveQuaryHeading = "insert into tbLeaveEntitlement (vLeaveId,vEmployeeId,vEmployeeCode,vEmployeeName,vDesignationName,vUnitId,"
					+ "vDepartmentId,vSectionId,dJoiningDate,dEntryDate,dEntitleFromDate,dEntitleToDate,vLeaveTypeId,vLeaveTypeName,mEntitleDays,"
					+ "dUpdateDate,vStatus,vUserId,vUserName,vUserIp,dEntryTime)values ";
		
			System.out.println("insert " +LeaveQuaryHeading);
			
			String insertLeave = "";
			int count = 0;
			for(int i=0; i<tblblEmployeeID.size(); i++)
			{
				if(!tblblEmployeeID.get(i).getValue().toString().isEmpty())
				{
					count++;
					insertLeave += "("
							+ "'"+selectMaxId()+"',"
							+ "'"+tblblEmployeeID.get(i).getValue().toString()+"',"
							+ "'"+tblblEmployeeCode.get(i).getValue().toString()+"',"
							+ "'"+tblblEmployeeName.get(i).getValue().toString()+"',"
							+ "'"+tblblDesignation.get(i).getValue().toString()+"',"
							+ "'"+tblblUnitId.get(i).getValue().toString()+"',"
							+ "'"+tblblDepartmentId.get(i).getValue().toString()+"',"
							+ "'"+tblblSectionId.get(i).getValue().toString()+"',"
							+ "'"+tblblJoiningDate.get(i).getValue().toString()+"',"
							+ "'"+sessionBean.dfDb.format(dEntryDate.getValue())+"',"
							+ "'"+tblblEntitleFromDate.get(i).getValue().toString()+"',"
							+ "'"+tblblEntitleToDate.get(i).getValue().toString()+"',"							
							+ "'"+cmbLeaveType.getValue().toString()+"',"
							+ "'"+cmbLeaveType.getItemCaption(cmbLeaveType.getValue()).toString()+"',"
							+ "'"+(tbTxtEntitleDays.get(i).getValue().toString().trim().isEmpty()?"0":tbTxtEntitleDays.get(i).getValue().toString().trim())+"'," 					
							+ "'"+tblblUpdateDate.get(i).getValue().toString()+"',"
							+ "'1',"
							+ "'"+sessionBean.getUserId()+"',"
							+ "'"+sessionBean.getUserName()+"',"
							+ "'"+sessionBean.getUserIp()+"',GETDATE()),";
					if(count%1000==0)
					{
						System.out.println("Insert Leave : "+LeaveQuaryHeading+insertLeave.substring(0, insertLeave.length()-1));
						session.createSQLQuery(LeaveQuaryHeading+insertLeave.substring(0, insertLeave.length()-1)).executeUpdate();
						insertLeave = "";
					}
				}
			}	
			
			if(insertLeave.length()>0)
			{	
				System.out.println("Insert Leave : "+LeaveQuaryHeading+insertLeave.substring(0, insertLeave.length()-1));
				session.createSQLQuery(LeaveQuaryHeading+insertLeave.substring(0, insertLeave.length()-1)).executeUpdate();
			}
			tx.commit();
		
			Notification n=new Notification("All Information Save Successfully!","",Notification.TYPE_TRAY_NOTIFICATION);
			n.setPosition(Notification.POSITION_TOP_RIGHT);
			showNotification(n);
			txtClear();
			componentIni(true);
			btnIni(true);
		}
		catch (Exception e)
		{
			showNotification("Warning!","Error to save balance "+e,Notification.TYPE_WARNING_MESSAGE);
			tx.rollback();
		}
		finally{session.close();}

	}

	private void updateData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{
			if(!tblblEmployeeID.get(0).getValue().toString().isEmpty())
			{
				String insertLeave = " update tbLeaveEntitlement set " +
						" iCasualLeave = '"+tbTxtEntitleDays.get(0).getValue().toString()+"'," +
						" dEntryDate = '"+sessionBean.dfDb.format(dEntryDate.getValue())+"'," +
						" vUserName = '"+sessionBean.getUserName()+"'," +
						" vUserIp = '"+sessionBean.getUserIp()+"'," +
						" dEntryTime = CURRENT_TIMESTAMP" +
						" where vLeaveId = '"+findId.getValue().toString()+"' and vEmployeeID = '"+tblblEmployeeID.get(0).getValue().toString()+"' ";
				System.out.println("updateData : "+insertLeave);
				
				session.createSQLQuery(insertLeave).executeUpdate();
			}
			else
			{
				showNotification("Warning!","There are no data to save.",Notification.TYPE_WARNING_MESSAGE);
			}
			
			Notification n=new Notification("All Information Updated Successfully!","",Notification.TYPE_TRAY_NOTIFICATION);
			n.setPosition(Notification.POSITION_TOP_RIGHT);
			showNotification(n);
			tx.commit();
			txtClear();
			componentIni(true);
			btnIni(true);
		}
		catch(Exception ex)
		{
			this.getParent().showNotification("Error", ex.toString(), Notification.TYPE_WARNING_MESSAGE);
			tx.rollback();
		}
		finally
		{
			session.close();
		}
	}

	private String selectMaxId()
	{
		String maxId = "";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = " Select isnull(max(cast(SUBSTRING(vLeaveId,5,LEN(vLeaveId)) as int)),0)+1 from tbLeaveEntitlement ";
			
			Iterator <?> iter = session.createSQLQuery(query).list().iterator();
			if (iter.hasNext())
			{
				String srt = iter.next().toString();
				maxId = "LVE-"+srt;
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		finally{session.close();}
		return maxId;
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
		tblblSl.add(ar, new Label());
		tblblSl.get(ar).setImmediate(true);
		tblblSl.get(ar).setWidth("100%");
		tblblSl.get(ar).setHeight("-1px");
		tblblSl.get(ar).setValue(ar+1);
		
		tblblEmployeeID.add(ar, new Label());
		tblblEmployeeID.get(ar).setImmediate(true);
		tblblEmployeeID.get(ar).setWidth("100%");

		tblblEmployeeCode.add(ar, new Label());
		tblblEmployeeCode.get(ar).setImmediate(true);
		tblblEmployeeCode.get(ar).setWidth("100%");

		tblblEmployeeName.add(ar, new Label());
		tblblEmployeeName.get(ar).setImmediate(true);
		tblblEmployeeName.get(ar).setWidth("100%");

		tblblDesignation.add(ar, new Label());
		tblblDesignation.get(ar).setImmediate(true);
		tblblDesignation.get(ar).setWidth("100%");

		tblblUnitId.add(ar, new Label());
		tblblUnitId.get(ar).setImmediate(true);
		tblblUnitId.get(ar).setWidth("100%");

		tblblDepartmentId.add(ar, new Label());
		tblblDepartmentId.get(ar).setImmediate(true);
		tblblDepartmentId.get(ar).setWidth("100%");

		tblblSectionId.add(ar, new Label());
		tblblSectionId.get(ar).setImmediate(true);
		tblblSectionId.get(ar).setWidth("100%");

		tblblJoiningDate.add(ar, new Label());
		tblblJoiningDate.get(ar).setImmediate(true);
		tblblJoiningDate.get(ar).setWidth("100%");

		tblblEntitleFromDate.add(ar, new Label());
		tblblEntitleFromDate.get(ar).setImmediate(true);
		tblblEntitleFromDate.get(ar).setWidth("100%");

		tblblEntitleToDate.add(ar, new Label());
		tblblEntitleToDate.get(ar).setImmediate(true);
		tblblEntitleToDate.get(ar).setWidth("100%");

		tbTxtEntitleDays.add(ar, new TextField());
		tbTxtEntitleDays.get(ar).setWidth("100%");
		tbTxtEntitleDays.get(ar).setImmediate(true);
		tbTxtEntitleDays.get(ar).setStyleName("amount");

		tblblUpdateDate.add(ar, new Label());
		tblblUpdateDate.get(ar).setImmediate(true);
		tblblUpdateDate.get(ar).setWidth("100%");

		table.addItem(new Object[]{tblblSl.get(ar),tblblEmployeeID.get(ar),tblblEmployeeCode.get(ar),tblblEmployeeName.get(ar),
				tblblDesignation.get(ar),tblblUnitId.get(ar),tblblDepartmentId.get(ar),tblblSectionId.get(ar),tblblJoiningDate.get(ar),
				tblblEntitleFromDate.get(ar),tblblEntitleToDate.get(ar),tbTxtEntitleDays.get(ar),tblblUpdateDate.get(ar)},ar);
	}

	private void btnIni(boolean t)
	{
		button.btnNew.setEnabled(t);
		button.btnEdit.setEnabled(t);
		button.btnSave.setEnabled(!t);
		button.btnRefresh.setEnabled(!t);
		button.btnDelete.setEnabled(t);
		button.btnFind.setEnabled(t);;
	}

	private void componentIni(boolean b) 
	{
		dEntryDate.setEnabled(!b);		
		cmbUnitName.setEnabled(!b);
		cmbDepartment.setEnabled(!b);
		cmbSectioName.setEnabled(!b);
		chkDepartmentAll.setEnabled(!b);
		chkSectionAll.setEnabled(!b);
		cmbLeaveType.setEnabled(!b);
		table.setEnabled(!b);
	}

	private void txtClear()
	{
		//dEntryDate.setValue(new java.util.Date());
		cmbUnitName.setValue(null);
		cmbDepartment.setValue(null);
		cmbSectioName.setValue(null);
		chkDepartmentAll.setValue(false);
		chkSectionAll.setValue(false);
		cmbLeaveType.setValue(null);

		tableClear();
	}

	private void tableClear()
	{
		for(int i=0; i<tblblEmployeeID.size(); i++)
		{
			tblblEmployeeID.get(i).setValue("");
			tblblEmployeeCode.get(i).setValue("");
			tblblEmployeeName.get(i).setValue("");
			tblblDesignation.get(i).setValue("");
			
			tblblUnitId.get(i).setValue("");
			tblblDepartmentId.get(i).setValue("");
			tblblSectionId.get(i).setValue("");
			tblblJoiningDate.get(i).setValue("");

			tblblEntitleFromDate.get(i).setValue("");
			tblblEntitleToDate.get(i).setValue("");
			tbTxtEntitleDays.get(i).setValue("");
			
			tblblUpdateDate.get(i).setValue("");
		}
	}

	private void focusEnter()
	{
		allComp.add(dEntryDate);
		allComp.add(cmbUnitName);
		allComp.add(cmbSectioName);
		allComp.add(tbTxtEntitleDays.get(0));

		for(int i=0;i<tblblEmployeeID.size();i++)
		{
			allComp.add(tbTxtEntitleDays.get(i));
		}
		allComp.add(button.btnSave);
		new FocusMoveByEnter(this,allComp);
	}
	private AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);

		setWidth("698px");
		setHeight("585px");
		
		lblCommon = new Label("Year :");
		lblCommon.setImmediate(true);
		lblCommon.setWidth("-1px");
		lblCommon.setHeight("-1px");
		
		lblCommon = new Label("Entry Date:");
		dEntryDate=new PopupDateField();
		dEntryDate.setImmediate(true);
		dEntryDate.setWidth("110px");
		dEntryDate.setHeight("-1px");
		dEntryDate.setValue(new java.util.Date());
		dEntryDate.setResolution(InlineDateField.RESOLUTION_DAY);
		dEntryDate.setDateFormat("dd-MM-yyyy");

		mainLayout.addComponent(lblCommon, "top:20.0px;left:20.0px;");
		mainLayout.addComponent(dEntryDate, "top:18.0px;left:130.0px;");

		lblCommon = new Label("Project Name :");
		cmbUnitName = new ComboBox();
		cmbUnitName.setImmediate(true);
		cmbUnitName.setWidth("250px");
		cmbUnitName.setHeight("-1px");
		mainLayout.addComponent(lblCommon, "top:45.0px;left:20.0px;");
		mainLayout.addComponent(cmbUnitName, "top:43.0px;left:130.0px;");
		
		lblCommon = new Label("Department :");
		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("250px");
		cmbDepartment.setHeight("-1px");
		mainLayout.addComponent(lblCommon, "top:70.0px;left:20.0px;");
		mainLayout.addComponent(cmbDepartment, "top:68.0px;left:130.0px;");
		
		chkDepartmentAll=new CheckBox("All");
		chkDepartmentAll.setImmediate(true);
		mainLayout.addComponent(chkDepartmentAll, "top:70px;left:380.0px;");

		lblCommon = new Label("Section :");
		cmbSectioName = new ComboBox();
		cmbSectioName.setImmediate(true);
		cmbSectioName.setWidth("250px");
		cmbSectioName.setHeight("-1px");
		mainLayout.addComponent(lblCommon, "top:95px;left:20.0px;");
		mainLayout.addComponent(cmbSectioName, "top:93px;left:130.0px;");
		
		chkSectionAll=new CheckBox("All");
		chkSectionAll.setImmediate(true);
		mainLayout.addComponent(chkSectionAll, "top:95px;left:380.0px;");

		lblCommon = new Label("Leave Type :");
		cmbLeaveType = new ComboBox();
		cmbLeaveType.setImmediate(true);
		cmbLeaveType.setWidth("250px");
		cmbLeaveType.setHeight("-1px");
		mainLayout.addComponent(lblCommon, "top:120px;left:20.0px;");
		mainLayout.addComponent(cmbLeaveType, "top:118px;left:130.0px;");

		table.setWidth("98%");
		table.setHeight("310px");
		table.setPageLength(0);
		table.setColumnCollapsingAllowed(true);
		
		table.setCaption("Use table for individual leave entry");

		table.addContainerProperty("SL#", Label.class , new Label());
		table.setColumnWidth("SL#",20);

		table.addContainerProperty("Emp Id", Label.class, new Label());
		table.setColumnWidth("Emp Id", 70);

		table.addContainerProperty("Emp. Id", Label.class, new Label());
		table.setColumnWidth("Emp. Id", 55);

		table.addContainerProperty("Employee Name", Label.class , new Label());
		table.setColumnWidth("Employee Name",200);

		table.addContainerProperty("Designation", Label.class , new Label());
		table.setColumnWidth("Designation",100);

		table.addContainerProperty("Project Id", Label.class, new Label());
		table.setColumnWidth("Project Id", 55);

		table.addContainerProperty("Department Id", Label.class, new Label());
		table.setColumnWidth("Department Id", 55);

		table.addContainerProperty("Section Id", Label.class, new Label());
		table.setColumnWidth("Section Id", 55);

		table.addContainerProperty("Joining Date", Label.class , new Label());
		table.setColumnWidth("Joining Date",70);

		table.addContainerProperty("Entitle From", Label.class , new Label());
		table.setColumnWidth("Entitle From",70);

		table.addContainerProperty("Entitle To", Label.class , new Label());
		table.setColumnWidth("Entitle To",70);

		table.addContainerProperty("Entitle Days", TextField.class , new TextField());
		table.setColumnWidth("Entitle Days",40);

		table.addContainerProperty("Update Date", Label.class , new Label());
		table.setColumnWidth("Update Date",70);
		

		//table.addContainerProperty("Joining Date", Label.class , new Label() ,null,null,table.ALIGN_CENTER);
		//table.setColumnAlignment("Joining Date", table.ALIGN_CENTER);

		table.setColumnCollapsed("Emp Id", true);
		table.setColumnCollapsed("Project Id", true);
		table.setColumnCollapsed("Department Id", true);
		table.setColumnCollapsed("Section Id", true);
		table.setColumnCollapsed("Entitle From", true);
		table.setColumnCollapsed("Update Date", true);

		/*table.setColumnAlignments(new String[]{Table.ALIGN_RIGHT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,
		 * Table.ALIGN_LEFT,Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER});*/
		
		mainLayout.addComponent(table, "top:170.0px;left:15.0px;");
		table.setStyleName("wordwrap-headers");

		tableInitialise();

		mainLayout.addComponent(button, "top:500.0px;left:90.0px;");

		return mainLayout;
	}
}