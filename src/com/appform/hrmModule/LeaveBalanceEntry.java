package com.appform.hrmModule;

import java.util.ArrayList;
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
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class LeaveBalanceEntry extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;
	private Label lblCommon;
	private InlineDateField dEntryYear;
	private ComboBox cmbSectioName,cmbDepartment;
	private CheckBox chkSectionAll;
	private CheckBox chkDepartmentAll;
	private PopupDateField dEntryDate;
	
	private ComboBox cmbUnitName;
	
	private boolean isUpdate = false;
	private boolean isRefresh = false;
	private boolean isFind = false;

	private TextRead findId = new TextRead();
	private TextRead EmpId = new TextRead();
	private AmountField txtCasualLeave;
	private AmountField txtSickLeave;
	private AmountField txtEarnLeave;

	private Table table = new Table();
	private ArrayList<Label> tblblSl = new ArrayList<Label>();
	
	private ArrayList<Label> tblblEmployeeID = new ArrayList<Label>();
	private ArrayList<Label> tblblEmployeeCode = new ArrayList<Label>();
	private ArrayList<Label> tblblEmployeeName = new ArrayList<Label>();
	private ArrayList<Label> tblblDesignation = new ArrayList<Label>();
	private ArrayList<Label> tblblJoiningDate = new ArrayList<Label>();
	private ArrayList<AmountField> tbTxtCasualLeave = new ArrayList<AmountField>();
	private ArrayList<AmountField> tbTxtSickLeave = new ArrayList<AmountField>();
	private ArrayList<AmountField> tbTxtEarnLeave = new ArrayList<AmountField>();
	private ArrayList<Label> tblblSection = new ArrayList<Label>();

	private CommonButton button = new CommonButton("New", "Save", "Edit", "","Refresh","Find","","","","Exit");

	private ArrayList<Component> allComp = new ArrayList<Component>();
	private DecimalFormat decimal = new DecimalFormat("#0");
	private SimpleDateFormat dfYear = new SimpleDateFormat("yyyy");

	double sumOfTable = 0;
	private CommonMethod cm;
	private String menuId = "";
	public LeaveBalanceEntry(SessionBean sessionBean,String menuId)
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

					dEntryYear.setEnabled(false);
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

		cmbUnitName.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbUnitName.getValue()!=null )
				{
						tableClear();
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
				if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
				{
					if(cmbSectioName.getValue()!=null )
					{
						if(!isFind){
							tableClear();
							tableDataAdd();
						}
					}
				}
				else
				{
					tableClear();
				}
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
							tableClear();
							tableDataAdd();
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
		
		txtCasualLeave.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				calculateLeave();
			}
		});
 
		txtSickLeave.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				calculateLeave();
			}
		});

		txtEarnLeave.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				calculateLeave();
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
					if(!tblblEmployeeName.get(0).getValue().toString().isEmpty())
					{
						if(sumOfTable>0)
						{
							saveButtonAction();
						}
						else
						{
							showNotification("Warning","Provide leave balance!!!", Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else
					{
						showNotification("Warning","No employee found!!!", Notification.TYPE_WARNING_MESSAGE);
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

	private void calculateLeave()
	{
		double sum = 0;

		sum = Double.parseDouble("0"+txtCasualLeave.getValue().toString()) + Double.parseDouble("0"+txtSickLeave.getValue().toString()) +
				Double.parseDouble("0"+txtEarnLeave.getValue().toString());

		if(!isRefresh)
		{
			if(sum>0)
			{
				setLeaveToAll();
			}
			else
			{
				LeaveClearAll();
			}
		}
	}

	private void setLeaveToAll()
	{
		for(int i = 0; i<tblblEmployeeID.size(); i++)
		{
			if(!tblblEmployeeID.get(i).getValue().toString().isEmpty())
			{
				tbTxtCasualLeave.get(i).setValue(decimal.format(Double.parseDouble("0"+txtCasualLeave.getValue().toString())));
				tbTxtSickLeave.get(i).setValue(decimal.format(Double.parseDouble("0"+txtSickLeave.getValue().toString())));
				tbTxtEarnLeave.get(i).setValue(decimal.format(Double.parseDouble("0"+txtEarnLeave.getValue().toString())));
			}
		}
	}

	private void LeaveClearAll()
	{
		for(int i = 0; i<tblblEmployeeID.size(); i++)
		{
			if(!tblblEmployeeID.get(i).getValue().toString().isEmpty())
			{
				if(txtCasualLeave.getValue().toString().trim().isEmpty() || Double.parseDouble(txtCasualLeave.getValue().toString().trim())<=0)
					tbTxtCasualLeave.get(i).setValue("");
				if(txtSickLeave.getValue().toString().trim().isEmpty() || Double.parseDouble(txtSickLeave.getValue().toString().trim())<=0)
					tbTxtSickLeave.get(i).setValue("");
				if(txtEarnLeave.getValue().toString().trim().isEmpty() || Double.parseDouble(txtEarnLeave.getValue().toString().trim())<=0)
					tbTxtEarnLeave.get(i).setValue("");
			}
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
					+ "where vUnitId ='"+cmbUnitName.getValue().toString()+"'  order by vDepartmentName " ;
					
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
	private void tableDataAdd()
	{
		String sectionId = cmbSectioName.getValue()!=null?cmbSectioName.getValue().toString():"%";
		String unitId = cmbUnitName.getValue()!=null?cmbUnitName.getValue().toString():"";
		String deptId = cmbDepartment.getValue()!=null?cmbDepartment.getValue().toString():"%";

		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{			
			String query="select vEmployeeId,vEmployeeCode,vEmployeeName,vDesignationName,dJoiningDate,vSectionId " +
					"from tbEmpOfficialPersonalInfo eoi " +
					"where vEmployeeId not in ( " +
						"select vEmployeeId from tbEmpLeaveInfo eli where eli.vEmployeeId = eoi.vEmployeeId " +
						"and eli.vYear = '"+dfYear.format(dEntryYear.getValue())+"' " +
						"and eli.vUnitId like '"+unitId+"' and eli.vSectionId like '"+sectionId+"' " +
					") " +
					"and bStatus=1 and vUnitId like '"+unitId+"' and vDepartmentId like '"+deptId+"' and vSectionId like '"+sectionId+"'";
			System.out.println("tableDataAdd: "+query);	
			
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
					tblblSection.get(i).setValue(element[5].toString());

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
			this.getParent().showNotification("tableDataAdd", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
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
					"from tbEmpLeaveInfo eli where vLeaveId = '"+findId+"' and vEmployeeId = '"+strEmpID+"'";
			
			System.out.println("FindQuery " + findQuery);
			List <?> list = session.createSQLQuery(findQuery).list();
			
			if(!list.isEmpty())
			{
				if(list.iterator().hasNext())
				{
					Object[] element = (Object[]) list.iterator().next();
					dEntryYear.setValue(element[10]);
					cmbUnitName.setValue(element[0].toString());
					cmbSectioName.setValue(element[1]);
					for(int i=0; i<list.size(); i++)
					{
						tblblEmployeeID.get(i).setValue(element[2].toString());
						tblblEmployeeCode.get(i).setValue(element[3]);
						tblblEmployeeName.get(i).setValue(element[4]);
						tblblDesignation.get(i).setValue(element[5]);
						tblblJoiningDate.get(i).setValue(element[6]);
						tbTxtCasualLeave.get(i).setValue(element[7]);
						tbTxtSickLeave.get(i).setValue(element[8]);
						tbTxtEarnLeave.get(i).setValue(element[9]);
						tblblSection.get(i).setValue(element[11]);
					}
					txtCasualLeave.setValue(element[7]);
					txtSickLeave.setValue(element[8]);
					txtEarnLeave.setValue(element[9]);
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
			String LeaveQuaryHeading = "insert into tbEmpLeaveInfo (vSectionId,vLeaveId,vYear,vEmployeeId,vEmployeeName,"
							+ "iCasualLeave,iSickLeave,iEarnLeave,vUserName,vUserIp,dEntryTime,vUnitId,dEntryDate,dJoiningDate) values ";
		
			System.out.println("insert " +LeaveQuaryHeading);
			
			String insertLeave = "";
			int count = 0;
			for(int i=0; i<tblblEmployeeID.size(); i++)
			{
				if(!tblblEmployeeID.get(i).getValue().toString().isEmpty() &&
				(Integer.parseInt(tbTxtCasualLeave.get(i).getValue().toString().trim().isEmpty()?"0":tbTxtCasualLeave.get(i).getValue().toString().trim())>0 ||
				Integer.parseInt(tbTxtSickLeave.get(i).getValue().toString().trim().isEmpty()?"0":tbTxtSickLeave.get(i).getValue().toString().trim())>0 ||
				Integer.parseInt(tbTxtEarnLeave.get(i).getValue().toString().trim().isEmpty()?"0":tbTxtEarnLeave.get(i).getValue().toString().trim())>0))
				{
					count++;
					insertLeave += "('"+tblblSection.get(i).getValue().toString()+"'," +
							" '"+selectMaxId()+"'," +
							" '"+dfYear.format(dEntryYear.getValue())+"'," +
							" '"+tblblEmployeeID.get(i).getValue().toString()+"'," +
							" '"+tblblEmployeeName.get(i).getValue().toString()+"'," +
							" '"+(tbTxtCasualLeave.get(i).getValue().toString().trim().isEmpty()?"0":tbTxtCasualLeave.get(i).getValue().toString().trim())+"'," +
							" '"+(tbTxtSickLeave.get(i).getValue().toString().trim().isEmpty()?"0":tbTxtSickLeave.get(i).getValue().toString().trim())+"'," +
							" '"+(tbTxtEarnLeave.get(i).getValue().toString().trim().isEmpty()?"0":tbTxtEarnLeave.get(i).getValue().toString().trim())+"'," +
							" '"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',GETDATE()," +
							" '"+cmbUnitName.getValue()+"','"+sessionBean.dfDb.format(dEntryDate.getValue())+"'," +
							" '"+tblblJoiningDate.get(i).getValue()+"'),";
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
				String insertLeave = " update tbEmpLeaveInfo set " +
						" iCasualLeave = '"+tbTxtCasualLeave.get(0).getValue().toString()+"'," +
						" iSickLeave = '"+tbTxtSickLeave.get(0).getValue().toString()+"'," +
						" iEarnLeave = '"+tbTxtEarnLeave.get(0).getValue().toString()+"'," +
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
			String query = " Select isnull(max(cast(SUBSTRING(vLeaveId,5,LEN(vLeaveId)) as int)),0)+1 from tbEmpLeaveInfo ";
			
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

		tblblJoiningDate.add(ar, new Label());
		tblblJoiningDate.get(ar).setImmediate(true);
		tblblJoiningDate.get(ar).setWidth("100%");

		tbTxtCasualLeave.add(ar, new AmountField());
		tbTxtCasualLeave.get(ar).setWidth("100%");
		tbTxtCasualLeave.get(ar).setImmediate(true);
		tbTxtCasualLeave.get(ar).setStyleName("amount");
		tbTxtCasualLeave.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				tbTxtSickLeave.get(ar).focus();
				calculateTableLeave();
			}
		});

		tbTxtSickLeave.add(ar, new AmountField());
		tbTxtSickLeave.get(ar).setWidth("100%");
		tbTxtSickLeave.get(ar).setImmediate(true);
		tbTxtSickLeave.get(ar).setStyleName("amount");
		tbTxtSickLeave.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				tbTxtEarnLeave.get(ar).focus();
				calculateTableLeave();
			}
		});

		tbTxtEarnLeave.add(ar, new AmountField());
		tbTxtEarnLeave.get(ar).setWidth("100%");
		tbTxtEarnLeave.get(ar).setImmediate(true);
		tbTxtEarnLeave.get(ar).setStyleName("amount");
		tbTxtEarnLeave.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				tbTxtCasualLeave.get(ar+1).focus();
				calculateTableLeave();
			}
		});


		tblblSection.add(ar, new Label());
		tblblSection.get(ar).setImmediate(true);
		tblblSection.get(ar).setWidth("100%");

		table.addItem(new Object[]{tblblSl.get(ar),tblblEmployeeID.get(ar),tblblEmployeeCode.get(ar),
				tblblEmployeeName.get(ar),tblblDesignation.get(ar),tblblJoiningDate.get(ar),tbTxtCasualLeave.get(ar),tbTxtSickLeave.get(ar),
				tbTxtEarnLeave.get(ar),tblblSection.get(ar)},ar);
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
		dEntryYear.setEnabled(!b);
		dEntryDate.setEnabled(!b);		
		cmbUnitName.setEnabled(!b);
		cmbDepartment.setEnabled(!b);
		cmbSectioName.setEnabled(!b);
		chkDepartmentAll.setEnabled(!b);
		chkSectionAll.setEnabled(!b);
		txtCasualLeave.setEnabled(!b);
		txtSickLeave.setEnabled(!b);
		txtEarnLeave.setEnabled(!b);
		table.setEnabled(!b);
	}

	private void txtClear()
	{
		sumOfTable = 0;
		dEntryDate.setValue(new java.util.Date());
		cmbUnitName.setValue(null);
		cmbDepartment.setValue(null);
		cmbSectioName.setValue(null);
		chkDepartmentAll.setValue(false);
		chkSectionAll.setValue(false);
		txtCasualLeave.setValue("");
		txtSickLeave.setValue("");
		txtEarnLeave.setValue("");

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
			tblblJoiningDate.get(i).setValue("");
			tbTxtCasualLeave.get(i).setValue("");
			tbTxtEarnLeave.get(i).setValue("");
			tbTxtSickLeave.get(i).setValue("");
			tblblSection.get(i).setValue("");
		}
	}

	private void focusEnter()
	{

		allComp.add(dEntryDate);
		allComp.add(cmbUnitName);
		allComp.add(cmbSectioName);
		allComp.add(txtCasualLeave);
		allComp.add(txtSickLeave);
		allComp.add(txtEarnLeave);
		allComp.add(tbTxtCasualLeave.get(0));

		new FocusMoveByEnter(this,allComp);
	}

	private void calculateTableLeave()
	{
		for(int i=0; i<tblblEmployeeID.size(); i++)
		{
			sumOfTable = sumOfTable + Double.parseDouble("0"+tbTxtCasualLeave.get(i).getValue().toString());
			sumOfTable = sumOfTable + Double.parseDouble("0"+tbTxtSickLeave.get(i).getValue().toString());
			sumOfTable = sumOfTable + Double.parseDouble("0"+tbTxtEarnLeave.get(i).getValue().toString());
		}
	}
	private AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);

		setWidth("700px");
		setHeight("585px");
		
		lblCommon = new Label("Year :");
		lblCommon.setImmediate(true);
		lblCommon.setWidth("-1px");
		lblCommon.setHeight("-1px");
		mainLayout.addComponent(lblCommon, "top:20.0px;left:20.0px;");

		dEntryYear = new InlineDateField();
		dEntryYear.setImmediate(true);
		dEntryYear.setWidth("110px");
		dEntryYear.setHeight("-1px");
		dEntryYear.setValue(new java.util.Date());
		dEntryYear.setResolution(InlineDateField.RESOLUTION_YEAR);
		mainLayout.addComponent(dEntryYear, "top:18.0px;left:130.0px;");

		lblCommon = new Label("Entry Date:");
		dEntryDate=new PopupDateField();
		dEntryDate.setImmediate(true);
		dEntryDate.setWidth("110px");
		dEntryDate.setHeight("-1px");
		dEntryDate.setValue(new java.util.Date());
		dEntryDate.setResolution(InlineDateField.RESOLUTION_DAY);
		dEntryDate.setDateFormat("dd-MM-yyyy");
		mainLayout.addComponent(lblCommon, "top:20.0px;left:245.0px;");
		mainLayout.addComponent(dEntryDate, "top:18.0px;left:310.0px;");

		lblCommon = new Label("Project Name :");
		mainLayout.addComponent(lblCommon, "top:45.0px;left:20.0px;");

		cmbUnitName = new ComboBox();
		cmbUnitName.setImmediate(true);
		cmbUnitName.setWidth("250px");
		cmbUnitName.setHeight("-1px");
		mainLayout.addComponent(cmbUnitName, "top:43.0px;left:130.0px;");
		
		lblCommon = new Label("Department :");
		mainLayout.addComponent(lblCommon, "top:70.0px;left:20.0px;");

		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("250px");
		cmbDepartment.setHeight("-1px");
		mainLayout.addComponent(cmbDepartment, "top:68.0px;left:130.0px;");
		
		chkDepartmentAll=new CheckBox("All");
		chkDepartmentAll.setImmediate(true);
		mainLayout.addComponent(chkDepartmentAll, "top:70px;left:380.0px;");

		lblCommon = new Label("Section :");
		mainLayout.addComponent(lblCommon, "top:95px;left:20.0px;");

		cmbSectioName = new ComboBox();
		cmbSectioName.setImmediate(true);
		cmbSectioName.setWidth("250px");
		cmbSectioName.setHeight("-1px");
		mainLayout.addComponent(cmbSectioName, "top:93px;left:130.0px;");
		
		chkSectionAll=new CheckBox("All");
		chkSectionAll.setImmediate(true);
		mainLayout.addComponent(chkSectionAll, "top:95px;left:380.0px;");

		lblCommon = new Label("CL :");

		txtCasualLeave = new AmountField();
		txtCasualLeave.setImmediate(true);
		txtCasualLeave.setWidth("35px");
		txtCasualLeave.setHeight("-1px");
		mainLayout.addComponent(txtCasualLeave, "top:110.0px;left:530.0px;");

		lblCommon = new Label("SL :");

		txtSickLeave = new AmountField();
		txtSickLeave.setImmediate(true);
		txtSickLeave.setWidth("35px");
		txtSickLeave.setHeight("-1px");
		mainLayout.addComponent(txtSickLeave, "top:110.0px;left:575.0px;");

		lblCommon = new Label("AL/EL :");

		txtEarnLeave = new AmountField();
		txtEarnLeave.setImmediate(true);
		txtEarnLeave.setWidth("35px");
		txtEarnLeave.setHeight("-1px");
		mainLayout.addComponent(txtEarnLeave, "top:110.0px;left:620.0px;");

		table.setColumnCollapsingAllowed(true);

		table.setWidth("98%");
		table.setHeight("325px");
		table.setPageLength(0);
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

		table.addContainerProperty("Joining Date", Label.class , new Label());
		table.setColumnWidth("Joining Date",70);

		table.addContainerProperty("CL", AmountField.class , new AmountField());
		table.setColumnWidth("CL",33);

		table.addContainerProperty("SL", AmountField.class , new AmountField());
		table.setColumnWidth("SL",33);

		table.addContainerProperty("EL/AL", AmountField.class , new AmountField());
		table.setColumnWidth("EL/AL",33);

		table.addContainerProperty("Section Id", Label.class, new Label());
		table.setColumnWidth("Section Id", 55);

		table.setColumnCollapsed("Emp Id", true);
		table.setColumnCollapsed("Section Id", true);

		table.setColumnAlignments(new String[]{Table.ALIGN_RIGHT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,
				Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER});

		mainLayout.addComponent(table, "top:135.0px;left:15.0px;");

		tableInitialise();

		lblCommon = new Label("Definition: CL=Casual Leave; SL=Sick Leave; EL/AL=Earn Leave/Anual Leave");
		mainLayout.addComponent(lblCommon, "top:470.0px;left:15.0px;");

		mainLayout.addComponent(button, "top:490.0px;left:90.0px;");

		return mainLayout;
	}
}