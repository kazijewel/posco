package com.appform.hrmModule;

import java.text.DecimalFormat;
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
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class LeaveEncashment extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;
	private Label lblCommon;
	private ComboBox cmbUnitName,cmbDepartment,cmbSectioName,cmbEmployeeId;
	private CheckBox chkDepartmentAll,chkSectionAll;

	private TextRead findId = new TextRead();
	private TextRead EmpId = new TextRead();

	private Table table = new Table();

	private ArrayList<NativeButton> Delete = new ArrayList<NativeButton>();
	private ArrayList<Label> tblblSl = new ArrayList<Label>();
	private ArrayList<Label> tblblEmployeeID = new ArrayList<Label>();
	private ArrayList<Label> tblblEmployeeCode = new ArrayList<Label>();
	private ArrayList<Label> tblblEmployeeName = new ArrayList<Label>();
	private ArrayList<Label> tblblDesignation = new ArrayList<Label>();
	private ArrayList<Label> tblblUnitId = new ArrayList<Label>();
	private ArrayList<Label> tblblUnitName= new ArrayList<Label>();
	private ArrayList<Label> tblblDepartmentId = new ArrayList<Label>();
	private ArrayList<Label> tblblDepartmentName = new ArrayList<Label>();
	private ArrayList<Label> tblblSectionId = new ArrayList<Label>();
	private ArrayList<Label> tblblSectionName = new ArrayList<Label>();
	private ArrayList<Label> tblblJoiningDate = new ArrayList<Label>();
	
	private ArrayList<Label> tblblEntitleFromDate = new ArrayList<Label>();
	private ArrayList<Label> tblblEntitleToDate = new ArrayList<Label>();
	private ArrayList<TextRead> tbTxtEntitleDays = new ArrayList<TextRead>();
	private ArrayList<TextRead> tbTxtEnjoy = new ArrayList<TextRead>();
	private ArrayList<TextRead> tbTxtBalance = new ArrayList<TextRead>();
	private ArrayList<TextRead> tbTxtEncashableDays = new ArrayList<TextRead>();
	private ArrayList<TextRead> tbTxtRemaining = new ArrayList<TextRead>();

	private CommonButton button = new CommonButton("New", "Save", "", "","Refresh","","","","","Exit");

	private ArrayList<Component> allComp = new ArrayList<Component>();
	private DecimalFormat decimal = new DecimalFormat("#0");
	private SimpleDateFormat dfYear = new SimpleDateFormat("yyyy");

	private CommonMethod cm;
	private String menuId = "";
	
	public LeaveEncashment(SessionBean sessionBean,String menuId)
	{
		this.sessionBean = sessionBean;
		this.setCaption("LEAVE ENCASHMENT :: "+sessionBean.getCompany());
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
				txtClear();	
				componentIni(true);
				btnIni(true);
			}
		});

		button.btnFind.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				//findButtonEvent();
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
					cmbDepartment.removeAllItems();
					cmbEmployeeId.setValue(null);
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
					cmbEmployeeId.setValue(null);
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
						cmbEmployeeId.setValue(null);
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
				cmbEmployeeId.setValue(null);
				cmbEmployeeDataLoad();
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
						cmbSectioName.setEnabled(false);
						cmbEmployeeId.setValue(null);
						tableClear();
						cmbEmployeeDataLoad();
					}
					else
					{
						tableClear();
						cmbSectioName.setEnabled(true);
					}
				}
			}
		});
		
		cmbEmployeeId.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				tableClear();
				if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
				{
					if(cmbSectioName.getValue()!=null || chkSectionAll.booleanValue())
					{
						if(cmbEmployeeId.getValue()!=null )
						{
							tableClear();
							tableDataLoad();
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
					if(cmbEmployeeId.getValue()!=null)
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
						cmbEmployeeId.focus();
						showNotification("Warning","Select Employee ID!!!", Notification.TYPE_WARNING_MESSAGE);
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
	
	private void addDepartmentData()
	{
		cmbDepartment.removeAllItems();
		
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select distinct vDepartmentId,(select vDepartmentName from tbDepartmentInfo "
					+ "where vDepartmentId=a.vDepartmentId)vDepartmentName from tbLeaveEntitlement a "
					+ "where vUnitId ='"+cmbUnitName.getValue().toString()+"' and vStatus='1' order by vDepartmentName " ;
					
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
			String query = "select distinct vSectionId,isnull((select vSectionName from tbSectionInfo where vSectionId=a.vSectionId),'')vSectionName "
					+ "from tbLeaveEntitlement a "
					+ "where vUnitId ='"+cmbUnitName.getValue().toString()+"' "
					+ "and vDepartmentId like '"+(cmbDepartment.getValue()==null?"%":cmbDepartment.getValue().toString())+"' "
					+ "and vStatus='1' order by vSectionName " ;
					
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
	private void cmbEmployeeDataLoad()
	{
		cmbEmployeeId.removeAllItems();
	
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{	
			String query = "select distinct vEmployeeId,vEmployeeCode,vEmployeeName from tbLeaveEntitlement "
					+ "where vUnitId ='"+cmbUnitName.getValue().toString()+"' "
					+ "and vDepartmentId like '"+(cmbDepartment.getValue()==null?"%":cmbDepartment.getValue().toString())+"' "
					+ "and vSectionId like '"+(cmbSectioName.getValue()==null?"%":cmbSectioName.getValue().toString())+"' "
					+ "and vStatus='1' order by vEmployeeCode ";
			
			System.out.println("cmbEmployeeDataLoad: "+query);
			
			List <?> list = session.createSQLQuery(query).list();

			for (Iterator <?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element =  (Object[]) iter.next();	
				cmbEmployeeId.addItem(element[0]);
				cmbEmployeeId.setItemCaption(element[0], element[1]+"-"+element[2]);	
			}
		}
		catch(Exception ex)
		{
			this.getParent().showNotification("cmbEmployeeDataLoad", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void tableDataLoad()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{			
			String query="select vEmployeeId,vEmployeeCode,vEmployeeName,dJoiningDate,vDesignation,vUnitId,vUnitName,"
					+ "vDepartmentId,vDepartmentName,vSectionId,vSectionName,dEntitleFromDate,dEntitleToDate,"
					+ "mEntitleDays,mEarnLeaveEnjoye,mBalance,mEncashableDays,mRemaining "
					+ "from funGetLeaveBalanceForLeaveEncashment('"+cmbEmployeeId.getValue()+"') ";
			
			System.out.println("tableDataLoad: "+query);
			
			List <?> list = session.createSQLQuery(query).list();
	
			if(!list.isEmpty())
			{
				int i=0;
				for(Iterator <?> iter = list.iterator(); iter.hasNext();)
				{				  
					Object[] element = (Object[]) iter.next();

					tblblEmployeeID.get(i).setValue(element[0]);
					tblblEmployeeCode.get(i).setValue(element[1]);
					tblblEmployeeName.get(i).setValue(element[2]);
					tblblJoiningDate.get(i).setValue(element[3]);
					tblblDesignation.get(i).setValue(element[4]);
					tblblUnitId.get(i).setValue(element[5]);
					tblblUnitName.get(i).setValue(element[6]);
					tblblDepartmentId.get(i).setValue(element[7]);
					tblblDepartmentName.get(i).setValue(element[8]);
					tblblSectionId.get(i).setValue(element[9]);
					tblblSectionName.get(i).setValue(element[10]);
					
					tblblEntitleFromDate.get(i).setValue(element[11]);
					tblblEntitleToDate.get(i).setValue(element[12]);
					tbTxtEntitleDays.get(i).setValue(element[13]);	
					tbTxtEnjoy.get(i).setValue(element[14]);
					tbTxtBalance.get(i).setValue(element[15]);
					tbTxtEncashableDays.get(i).setValue(element[16]);
					tbTxtRemaining.get(i).setValue(element[17]);

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
				}
			}
		});
	}

	private void insertData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{
			for(int i=0; i<tblblEmployeeID.size(); i++)
			{
				if(!tblblEmployeeID.get(i).getValue().toString().isEmpty())
				{
					String EncashQuaryHeading = "insert into tbLeaveEncashment"
					+ "("
						+ "vEmployeeId,vEmployeeCode,vEmployeeName,dJoiningDate,vDesignation,vUnitId,vUnitName,vDepartmentId,vDepartmentName,"
						+ "vSectionId,vSectionName,dEntitleFromDate,dEntitleToDate,mEntitleDays,mEarnLeaveEnjoye,mBalance,mEncashableDays,"
						+ "mRemaining,vUserId,vUserName,vUserIp,dEntryTime"
					+ ") "
					+ "values "
					+ "("
						+ "'"+tblblEmployeeID.get(i).getValue().toString()+"',"
						+ "'"+tblblEmployeeCode.get(i).getValue().toString()+"',"
						+ "'"+tblblEmployeeName.get(i).getValue().toString()+"',"
						+ "'"+tblblJoiningDate.get(i).getValue().toString()+"',"
								
						+ "'"+tblblDesignation.get(i).getValue().toString()+"',"
						+ "'"+tblblUnitId.get(i).getValue().toString()+"',"
						+ "'"+tblblUnitName.get(i).getValue().toString()+"',"
						+ "'"+tblblDepartmentId.get(i).getValue().toString()+"',"
						+ "'"+tblblDepartmentName.get(i).getValue().toString()+"',"
								
						+ "'"+tblblSectionId.get(i).getValue().toString()+"',"
						+ "'"+tblblSectionName.get(i).getValue().toString()+"',"
						
						+ "'"+tblblEntitleFromDate.get(i).getValue().toString()+"',"
						+ "'"+tblblEntitleToDate.get(i).getValue().toString()+"',"
						+ "'"+(tbTxtEntitleDays.get(i).getValue().toString().trim().isEmpty()?"0":tbTxtEntitleDays.get(i).getValue())+"'," 					
						+ "'"+(tbTxtEnjoy.get(i).getValue().toString().trim().isEmpty()?"0":tbTxtEnjoy.get(i).getValue())+"'," 					
						+ "'"+(tbTxtBalance.get(i).getValue().toString().trim().isEmpty()?"0":tbTxtBalance.get(i).getValue())+"'," 					
						+ "'"+(tbTxtEncashableDays.get(i).getValue().toString().trim().isEmpty()?"0":tbTxtEncashableDays.get(i).getValue())+"'," 					
						+ "'"+(tbTxtRemaining.get(i).getValue().toString().trim().isEmpty()?"0":tbTxtRemaining.get(i).getValue())+"'," 					
						
						+ "'"+sessionBean.getUserId()+"',"
						+ "'"+sessionBean.getUserName()+"',"
						+ "'"+sessionBean.getUserIp()+"',GETDATE()"
					+ ")";
					
					session.createSQLQuery(EncashQuaryHeading).executeUpdate();
					System.out.println("EncashQuaryHeading: "+EncashQuaryHeading); 
					
					/*session.createSQLQuery(entitlementUpdate).executeUpdate();
					System.out.println("entitlementUpdate: "+entitlementUpdate); */
				}
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
			showNotification("Warning!","Error to Save All Information "+e,Notification.TYPE_WARNING_MESSAGE);
			tx.rollback();
		}
		finally{session.close();}

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
		Delete.add(ar, new NativeButton(""));
		Delete.get(ar).setWidth("100%");
		Delete.get(ar).setImmediate(true);
		Delete.get(ar).setIcon(new ThemeResource("../icons/trash.png"));
		Delete.get(ar).setStyleName("Transparent");
		Delete.get(ar).addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				tblblEmployeeID.get(ar).setValue("");
				tblblEmployeeCode.get(ar).setValue("");
				tblblEmployeeName.get(ar).setValue("");
				tblblDesignation.get(ar).setValue("");
				tblblUnitId.get(ar).setValue("");
				tblblUnitName.get(ar).setValue("");
				tblblDepartmentId.get(ar).setValue("");
				tblblDepartmentName.get(ar).setValue("");
				tblblSectionId.get(ar).setValue("");
				tblblSectionName.get(ar).setValue("");
				tblblJoiningDate.get(ar).setValue("");
				tblblEntitleFromDate.get(ar).setValue("");
				tblblEntitleToDate.get(ar).setValue("");
				tbTxtEntitleDays.get(ar).setValue("");
				tbTxtEnjoy.get(ar).setValue("");
				tbTxtBalance.get(ar).setValue("");
				tbTxtEncashableDays.get(ar).setValue("");
				tbTxtRemaining.get(ar).setValue("");

				for(int rowcount=ar;rowcount<=tblblEmployeeID.size()-1;rowcount++)
				{
					if(rowcount+1<=tblblEmployeeID.size()-1)
					{
						if(!tblblEmployeeID.get(rowcount+1).getValue().toString().equals(""))
						{
							tblblEmployeeID.get(rowcount).setValue(tblblEmployeeID.get(rowcount+1).getValue().toString());
							tblblEmployeeCode.get(rowcount).setValue(tblblEmployeeCode.get(rowcount+1).getValue().toString());
							tblblEmployeeName.get(rowcount).setValue(tblblEmployeeName.get(rowcount+1).getValue().toString());
							tblblDesignation.get(rowcount).setValue(tblblDesignation.get(rowcount+1).getValue().toString());
							tblblUnitId.get(rowcount).setValue(tblblUnitId.get(rowcount+1).getValue().toString());
							tblblUnitName.get(rowcount).setValue(tblblUnitName.get(rowcount+1).getValue().toString());
							tblblDepartmentId.get(rowcount).setValue(tblblDepartmentId.get(rowcount+1).getValue().toString());
							tblblDepartmentName.get(rowcount).setValue(tblblDepartmentName.get(rowcount+1).getValue().toString());
							tblblSectionId.get(rowcount).setValue(tblblSectionId.get(rowcount+1).getValue().toString());
							tblblSectionName.get(rowcount).setValue(tblblSectionName.get(rowcount+1).getValue().toString());
							tblblJoiningDate.get(rowcount).setValue(tblblJoiningDate.get(rowcount+1).getValue().toString());
							tblblEntitleFromDate.get(rowcount).setValue(tblblEntitleFromDate.get(rowcount+1).getValue().toString());
							tblblEntitleToDate.get(rowcount).setValue(tblblEntitleToDate.get(rowcount+1).getValue().toString());
							tbTxtEntitleDays.get(rowcount).setValue(tbTxtEntitleDays.get(rowcount+1).getValue().toString());
							tbTxtEnjoy.get(rowcount).setValue(tbTxtEnjoy.get(rowcount+1).getValue().toString());
							tbTxtBalance.get(rowcount).setValue(tbTxtBalance.get(rowcount+1).getValue().toString());
							tbTxtEncashableDays.get(rowcount).setValue(tbTxtEncashableDays.get(rowcount+1).getValue().toString());
							tbTxtRemaining.get(rowcount).setValue(tbTxtRemaining.get(rowcount+1).getValue().toString());
														
							tblblEmployeeID.get(rowcount+1).setValue("");
							tblblEmployeeCode.get(rowcount+1).setValue("");
							tblblEmployeeName.get(rowcount+1).setValue("");
							tblblDesignation.get(rowcount+1).setValue("");
							tblblUnitId.get(rowcount+1).setValue("");
							tblblUnitName.get(rowcount+1).setValue("");
							tblblDepartmentId.get(rowcount+1).setValue("");
							tblblDepartmentName.get(rowcount+1).setValue("");
							tblblSectionId.get(rowcount+1).setValue("");
							tblblSectionName.get(rowcount+1).setValue("");
							tblblJoiningDate.get(rowcount+1).setValue("");
							tblblEntitleFromDate.get(rowcount+1).setValue("");
							tblblEntitleToDate.get(rowcount+1).setValue("");
							tbTxtEntitleDays.get(rowcount+1).setValue("");
							tbTxtEnjoy.get(rowcount+1).setValue("");
							tbTxtBalance.get(rowcount+1).setValue("");
							tbTxtEncashableDays.get(rowcount+1).setValue("");
							tbTxtRemaining.get(rowcount+1).setValue("");
						}
					}
				}
			}
		});
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

		tblblUnitName.add(ar, new Label());
		tblblUnitName.get(ar).setImmediate(true);
		tblblUnitName.get(ar).setWidth("100%");

		tblblDepartmentId.add(ar, new Label());
		tblblDepartmentId.get(ar).setImmediate(true);
		tblblDepartmentId.get(ar).setWidth("100%");

		tblblDepartmentName.add(ar, new Label());
		tblblDepartmentName.get(ar).setImmediate(true);
		tblblDepartmentName.get(ar).setWidth("100%");

		tblblSectionId.add(ar, new Label());
		tblblSectionId.get(ar).setImmediate(true);
		tblblSectionId.get(ar).setWidth("100%");

		tblblSectionName.add(ar, new Label());
		tblblSectionName.get(ar).setImmediate(true);
		tblblSectionName.get(ar).setWidth("100%");

		tblblJoiningDate.add(ar, new Label());
		tblblJoiningDate.get(ar).setImmediate(true);
		tblblJoiningDate.get(ar).setWidth("100%");

		tblblEntitleFromDate.add(ar, new Label());
		tblblEntitleFromDate.get(ar).setImmediate(true);
		tblblEntitleFromDate.get(ar).setWidth("100%");

		tblblEntitleToDate.add(ar, new Label());
		tblblEntitleToDate.get(ar).setImmediate(true);
		tblblEntitleToDate.get(ar).setWidth("100%");

		tbTxtEntitleDays.add(ar, new TextRead());
		tbTxtEntitleDays.get(ar).setWidth("100%");
		tbTxtEntitleDays.get(ar).setImmediate(true);
		tbTxtEntitleDays.get(ar).setStyleName("amount");

		tbTxtEnjoy.add(ar, new TextRead());
		tbTxtEnjoy.get(ar).setImmediate(true);
		tbTxtEnjoy.get(ar).setWidth("100%");

		tbTxtBalance.add(ar, new TextRead());
		tbTxtBalance.get(ar).setImmediate(true);
		tbTxtBalance.get(ar).setWidth("100%");

		tbTxtEncashableDays.add(ar, new TextRead());
		tbTxtEncashableDays.get(ar).setImmediate(true);
		tbTxtEncashableDays.get(ar).setWidth("100%");

		tbTxtRemaining.add(ar, new TextRead());
		tbTxtRemaining.get(ar).setImmediate(true);
		tbTxtRemaining.get(ar).setWidth("100%");

		table.addItem(new Object[]{Delete.get(ar),tblblSl.get(ar),tblblEmployeeID.get(ar),tblblEmployeeCode.get(ar),tblblEmployeeName.get(ar),
				tblblDesignation.get(ar),tblblUnitId.get(ar),tblblUnitName.get(ar),tblblDepartmentId.get(ar),tblblDepartmentName.get(ar),
				tblblSectionId.get(ar),tblblSectionName.get(ar),tblblJoiningDate.get(ar),tblblEntitleFromDate.get(ar),tblblEntitleToDate.get(ar),
				tbTxtEntitleDays.get(ar),tbTxtEnjoy.get(ar),tbTxtBalance.get(ar),tbTxtEncashableDays.get(ar),tbTxtRemaining.get(ar)},ar);
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
		cmbUnitName.setEnabled(!b);
		cmbDepartment.setEnabled(!b);
		cmbSectioName.setEnabled(!b);
		chkDepartmentAll.setEnabled(!b);
		chkSectionAll.setEnabled(!b);
		cmbEmployeeId.setEnabled(!b);
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
		cmbEmployeeId.setValue(null);

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
			tblblUnitName.get(i).setValue("");
			tblblDepartmentId.get(i).setValue("");
			tblblSectionId.get(i).setValue("");
			tblblJoiningDate.get(i).setValue("");

			tblblEntitleFromDate.get(i).setValue("");
			tblblEntitleToDate.get(i).setValue("");
			
			tbTxtEntitleDays.get(i).setValue("");			
			tbTxtEnjoy.get(i).setValue("");
			tbTxtBalance.get(i).setValue("");
			tbTxtEncashableDays.get(i).setValue("");
			tbTxtRemaining.get(i).setValue("");
		}
	}

	private void focusEnter()
	{
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

		setWidth("1000px");
		setHeight("585px");
		
		lblCommon = new Label("Year :");
		lblCommon.setImmediate(true);
		lblCommon.setWidth("-1px");
		lblCommon.setHeight("-1px");

		lblCommon = new Label("Project Name :");
		cmbUnitName = new ComboBox();
		cmbUnitName.setImmediate(true);
		cmbUnitName.setWidth("260px");
		cmbUnitName.setHeight("-1px");
		mainLayout.addComponent(lblCommon, "top:20.0px;left:20.0px;");
		mainLayout.addComponent(cmbUnitName, "top:18.0px;left:120.0px;");
		
		lblCommon = new Label("Department :");
		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("260px");
		cmbDepartment.setHeight("-1px");
		mainLayout.addComponent(lblCommon, "top:45.0px;left:20.0px;");
		mainLayout.addComponent(cmbDepartment, "top:43.0px;left:120.0px;");
		
		chkDepartmentAll=new CheckBox("All");
		chkDepartmentAll.setImmediate(true);
		mainLayout.addComponent(chkDepartmentAll, "top:45.0px;left:380.0px;");

		lblCommon = new Label("Section :");
		cmbSectioName = new ComboBox();
		cmbSectioName.setImmediate(true);
		cmbSectioName.setWidth("260px");
		cmbSectioName.setHeight("-1px");
		mainLayout.addComponent(lblCommon, "top:70px;left:20.0px;");
		mainLayout.addComponent(cmbSectioName, "top:68.0px;left:120.0px;");
		
		chkSectionAll=new CheckBox("All");
		chkSectionAll.setImmediate(true);
		mainLayout.addComponent(chkSectionAll, "top:70px;left:380.0px;");

		lblCommon = new Label("Employee ID :");
		cmbEmployeeId = new ComboBox();
		cmbEmployeeId.setImmediate(true);
		cmbEmployeeId.setWidth("260px");
		cmbEmployeeId.setHeight("-1px");
		mainLayout.addComponent(lblCommon, "top:95px;left:20.0px;");
		mainLayout.addComponent(cmbEmployeeId, "top:95px;left:120.0px;");
		
		table.setWidth("98%");
		table.setHeight("310px");
		table.setPageLength(0);
		table.setColumnCollapsingAllowed(true);
		
		table.setCaption("Use table for individual leave entry");

		table.addContainerProperty("Remove", NativeButton.class , new NativeButton());
		table.setColumnWidth("Remove",45);
		
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

		table.addContainerProperty("Project Name", Label.class, new Label());
		table.setColumnWidth("Project Name", 55);

		table.addContainerProperty("Department Id", Label.class, new Label());
		table.setColumnWidth("Department Id", 55);

		table.addContainerProperty("Department Name", Label.class, new Label());
		table.setColumnWidth("Department Name", 55);

		table.addContainerProperty("Section Id", Label.class, new Label());
		table.setColumnWidth("Section Id", 55);

		table.addContainerProperty("Section Name", Label.class, new Label());
		table.setColumnWidth("Section Name", 55);

		table.addContainerProperty("Joining Date", Label.class , new Label());
		table.setColumnWidth("Joining Date",63);

		table.addContainerProperty("Entitle From", Label.class , new Label());
		table.setColumnWidth("Entitle From",63);

		table.addContainerProperty("Entitle To", Label.class , new Label());
		table.setColumnWidth("Entitle To",63);

		table.addContainerProperty("Entitle Days", TextRead.class , new TextRead());
		table.setColumnWidth("Entitle Days",40);

		table.addContainerProperty("Enj", TextRead.class , new TextRead());
		table.setColumnWidth("Enj",25);

		table.addContainerProperty("Balance", TextRead.class , new TextRead());
		table.setColumnWidth("Balance",35);

		table.addContainerProperty("Encashable Days", TextRead.class , new TextRead());
		table.setColumnWidth("Encashable Days",70);

		table.addContainerProperty("Remaining", TextRead.class , new TextRead());
		table.setColumnWidth("Remaining",40);
		

		//table.addContainerProperty("Joining Date", Label.class , new Label() ,null,null,table.ALIGN_CENTER);
		//table.setColumnAlignment("Joining Date", table.ALIGN_CENTER);

		table.setColumnCollapsed("Emp Id", true);
		table.setColumnCollapsed("Project Id", true);
		table.setColumnCollapsed("Department Id", true);
		table.setColumnCollapsed("Section Id", true);
		table.setColumnCollapsed("Project Name", true);
		table.setColumnCollapsed("Department Name", true);
		table.setColumnCollapsed("Section Name", true);
		table.setColumnCollapsed("Remaining", true);
		//table.setColumnCollapsed("Entitle From", true);

		/*table.setColumnAlignments(new String[]{Table.ALIGN_RIGHT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,
		 * Table.ALIGN_LEFT,Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER});*/
		
		mainLayout.addComponent(table, "top:170.0px;left:15.0px;");
		table.setStyleName("wordwrap-headers");

		tableInitialise();

		mainLayout.addComponent(button, "top:500.0px;left:330.0px;");

		return mainLayout;
	}
}