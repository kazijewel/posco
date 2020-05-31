package com.common.share;

import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.common.share.AmountField;
import com.common.share.MessageBox.ButtonType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Button.ClickShortcut;
import com.vaadin.ui.Window.Notification;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;

@SuppressWarnings("serial")
public class UserCreate extends Window 
{
	private AbsoluteLayout mainLayout;
	private SessionBean sessionBean;
	private CommonButton cButton = new CommonButton("New", "Save", "Edit", "", "Refresh", "", "", "", "", "Exit");

	private ListSelect ntvFind;

	private TextField txtUserName;
	private TextField txtUserEmail;
	private TextField txtUserMobile;
	private PasswordField txtConfirmPassword;
	private PasswordField txtPassword;
	private ComboBox cmbUserType;
	private AmountField txtEditDeleteDays;
	private NativeSelect isActive;
	private ComboBox cmbEmployee;

	private Table table = new Table();
	private ArrayList<Label> tbId = new ArrayList<Label>();
	private ArrayList<CheckBox> tbCheck = new ArrayList<CheckBox>();
	private ArrayList<Label> tbModuleName = new ArrayList<Label>();
	private ArrayList<Component> allComp = new ArrayList<Component>();

	private boolean isUpdate = false;
	private CommonMethod cm;
	private String menuId = "";  

	public UserCreate(SessionBean sessionBean,String menuId)
	{
		this.sessionBean = sessionBean;
		this.setCaption("USER CREATE :: "+sessionBean.getCompany());
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;

		buildMainLayout();
		setContent(mainLayout);

		btnIni(true);
		clear();
		clearAll();
		txtEnable(false);
		cButton.btnNew.focus();

		buttonActionAdd();
		setButtonShortCut();
		addUserInfo();
		cmbEmployeeData();
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

	private void setButtonShortCut()
	{
		this.addAction(new ClickShortcut(cButton.btnSave, KeyCode.S, ModifierKey.ALT));
		this.addAction(new ClickShortcut(cButton.btnNew, KeyCode.N, ModifierKey.ALT));
		this.addAction(new ClickShortcut(cButton.btnRefresh, KeyCode.R, ModifierKey.ALT));
	}
	private void cmbEmployeeData()
	{
		cmbEmployee.removeAllItems();
		
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql="select distinct vEmployeeId,vEmployeeCode,vEmployeeName from tbEmpOfficialPersonalInfo where bStatus=1 order by vEmployeeName";
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator();iter.hasNext();)
			{
				Object element[] = (Object[]) iter.next();
				cmbEmployee.addItem(element[0]);
				cmbEmployee.setItemCaption(element[0], element[1].toString()+"-"+element[2].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification(exp+"cmbEmployeeData",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void buttonActionAdd()
	{
		cButton.btnNew.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate = false;
				newBtnAction();
				focusEnter();
			}
		});

		cButton.btnEdit.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				if(!txtUserName.getValue().toString().trim().isEmpty())
				{
					updateBtnAction();
					txtEnable(true);

					txtUserName.setEnabled(false);
					/*txtPassword.setEnabled(false);
					txtConfirmPassword.setEnabled(false);*/
				}
				else
				{
					showNotification("Warning!","There are nothing to edit.",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		cButton.btnSave.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				formValidation();
			}
		});

		cButton.btnRefresh.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				initialize();
				clear();
				clearAll();
			}
		});

		cButton.btnExit.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

		txtUserName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtUserName.getValue().toString().isEmpty())
				{
					if(duplicateNameCheck())
					{
						showNotification("Warning!","Username already exist.",Notification.TYPE_WARNING_MESSAGE);
						txtUserName.setValue("");
						txtUserName.focus();
					}
				}
			}
		});

		ntvFind.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(ntvFind.getValue()!=null)
				{
					isUpdate = true;
					clearAll();
					findInitialize(ntvFind.getValue().toString());
				}
			}
		});
	}

	private boolean duplicateNameCheck()
	{
		boolean ret = false;
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "";
			if(isUpdate)
			{
				query = " select vUserName from dbo.tbUserInfo where vUserId != '"+ntvFind.getValue().toString()+"' " +
						"and vUserName like '"+txtUserName.getValue().toString()+"'";
			}
			else
			{
				query = " Select vUserName from dbo.tbUserInfo where " +
						" vUserName like '"+txtUserName.getValue().toString()+"' ";
			}
			Iterator<?> iter = session.createSQLQuery(query).list().iterator();
			if(iter.hasNext())
			{
				ret = true;
			}
		}
		catch(Exception ex)
		{
			System.out.print(ex);
		}
		finally{session.close();}
		return ret;
	}

	private void findInitialize(String txtId) 
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try 
		{
			String sql = " select ui.vUserName,vPassword,vEmailId,vMobileNo,iUserType,iEditDeleteDays,iActive,"+
					" vModuleId,ui.vEmployeeId from dbo.tbUserInfo ui inner join dbo.tbUserDetails ud on ui.vUserId = ud.vUserId"+
					" where ui.vUserId = '"+txtId+"'";
			List<?> lst = session.createSQLQuery(sql).list();

			int i = 0;
			for(Iterator<?> iter = lst.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				if(i == 0)
				{
					txtUserName.setValue(element[0].toString());
					txtPassword.setValue(element[1].toString());
					txtConfirmPassword.setValue(element[1].toString());
					txtUserEmail.setValue(element[2].toString());
					txtUserMobile.setValue(element[3].toString());
					cmbUserType.setValue(element[4].toString());
					txtEditDeleteDays.setValue(element[5].toString());
					isActive.setValue(element[6].toString());
					cmbEmployee.setValue(element[8]);
				}
				tbCheck.get(Integer.parseInt(element[7].toString())).setValue(true);
				i = 1;
			}
		}
		catch (Exception exp)
		{
			showNotification("Error", exp + "",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void formValidation()
	{
		if(cmbEmployee.getValue()!=null)
		{

			if(!txtUserName.getValue().toString().trim().isEmpty())
			{
				if(!txtPassword.getValue().toString().trim().isEmpty())
				{
					if(!txtConfirmPassword.getValue().toString().trim().isEmpty())
					{
						if(!txtUserEmail.getValue().toString().trim().isEmpty())
						{
							if(!txtUserMobile.getValue().toString().trim().isEmpty())
							{
								if(!txtEditDeleteDays.getValue().toString().trim().isEmpty())
								{
									if(chkTable())
									{
										if(txtPassword.getValue().toString().length()>=2)
										{
											if(txtPassword.getValue().toString().equals(txtConfirmPassword.getValue().toString()))
											{
												saveBtnAction();
											}
											else
											{
												showNotification("Warning!","Password & confirm password mismatch.",Notification.TYPE_WARNING_MESSAGE);
												txtPassword.focus();
											}
										}
										else
										{
											showNotification("Warning!","Password length should be more than 2 digit.",Notification.TYPE_WARNING_MESSAGE);
											txtPassword.focus();
										}
									}
									else
									{
										showNotification("Warning!","Select at least one module.",Notification.TYPE_WARNING_MESSAGE);
										tbCheck.get(0).focus();
									}
								}
								else
								{
									showNotification("Warning!","Provide days limitaion for edit/delete data.", Notification.TYPE_WARNING_MESSAGE);
									txtEditDeleteDays.focus();
								}
							}
							else
							{
								showNotification("Warning!","Provide user mobile number.", Notification.TYPE_WARNING_MESSAGE);
								txtUserMobile.focus();
							}
						}
						else
						{
							showNotification("Warning!","Provide user email id.", Notification.TYPE_WARNING_MESSAGE);
							txtUserEmail.focus();
						}
					}
					else
					{
						showNotification("Warning!","Provide confirm password.", Notification.TYPE_WARNING_MESSAGE);
						txtConfirmPassword.focus();
					}
				}
				else
				{
					showNotification("Warning!","Provide password.", Notification.TYPE_WARNING_MESSAGE);
					txtPassword.focus();
				}
			}
			else
			{
				showNotification("Warning!","Provide user name.", Notification.TYPE_WARNING_MESSAGE);
				txtUserName.focus();
			}
		
		}
		else{
			showNotification("Warning!","Select Employee", Notification.TYPE_WARNING_MESSAGE);
			cmbEmployee.focus();
		}
	}

	public boolean chkTable()
	{
		for(int i=0; i<tbCheck.size(); i++)
		{
			if(tbCheck.get(i).booleanValue())
			{
				return true;
			}
		}
		return false;
	}

	public String maxUserId() 
	{
		String autoCode = "";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try 
		{
			String query = "Select cast(isnull(max(cast(substring(vUserId, 3, 10)as int))+1, 1)as varchar) from dbo.tbUserInfo";
			Iterator<?> iter = session.createSQLQuery(query).list().iterator();
			if(iter.hasNext()) 
			{
				autoCode = "U-"+iter.next().toString();
			}
		}
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		finally{session.close();}
		return autoCode;
	}

	private void saveBtnAction()
	{
		if(isUpdate)
		{
			MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to update all information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new MessageBox.EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						if(updateData())
						{
							if(insertData())
							{
								initialize();
								cButton.btnNew.focus();
								showNotification("All information updated successfully.");
								addUserInfo();
							}
						}
					}
				}
			});
		}
		else
		{
			MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to save all information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new MessageBox.EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						if(insertData())
						{
							initialize();
							cButton.btnNew.focus();
							clear();
							clearAll();
							showNotification("All information saved successfully.");
							addUserInfo();
						}
					}
				}
			});
		}
	}

	private boolean insertData()
	{
		boolean ret = false;
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		String userId = (!isUpdate?maxUserId():ntvFind.getValue().toString());
		try
		{
			StringTokenizer token=new StringTokenizer(cmbEmployee.getItemCaption(cmbEmployee.getValue()),"-");
			String empCode=token.nextToken();
			String empName=token.nextToken();
			
			String insertInfo = " INSERT INTO dbo.tbUserInfo(vCompanyId,vUserId,vUserName,vPassword,"+
					" vEmailId,vMobileNo,iUserType,vUserType,dLastLogin,iEditDeleteDays,vEmployeeId,vEmployeeName,"+
					" vCreateBy,vUserIp,dEntryTime,iActive) VALUES (" +
					" '1'," +
					" '"+userId+"'," +
					" '"+txtUserName.getValue().toString()+"'," +
					" '"+txtPassword.getValue().toString()+"'," +
					" '"+txtUserEmail.getValue().toString()+"'," +
					" '"+txtUserMobile.getValue().toString()+"'," +
					" '"+cmbUserType.getValue().toString()+"'," +
					" '"+cmbUserType.getItemCaption(cmbUserType.getValue()).toString()+"'," +
					" CURRENT_TIMESTAMP," +
					" '"+txtEditDeleteDays.getValue().toString()+"'," +
					" '"+cmbEmployee.getValue()+"', '"+empName+"', '"+sessionBean.getUserName()+"', '"+sessionBean.getUserIp()+"'," +
					" CURRENT_TIMESTAMP, '"+isActive.getValue().toString()+"')";
			session.createSQLQuery(insertInfo).executeUpdate();

			for(int i=0; i<tbCheck.size(); i++)
			{
				if(tbCheck.get(i).booleanValue()==true)
				{	
					String insertDetails = "Insert into dbo.tbUserDetails(vUserId,vUserName,vModuleId,"+
							" vModuleName) values (" +
							" '"+userId+"'," +
							" '"+txtUserName.getValue().toString()+"'," +
							" '"+i+"'," +
							" '"+tbModuleName.get(i).getValue()+"')";
					session.createSQLQuery(insertDetails).executeUpdate();
				}
			}
			tx.commit();
			ret = true;
		}
		catch(Exception exp)
		{
			tx.rollback();
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
		return ret;
	}

	private void initialize()
	{
		isUpdate = false;
		txtEnable(false);
		btnIni(true);
	}

	private boolean updateData()
	{
		boolean ret = false;
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{
			String userId = ntvFind.getValue().toString();
			StringTokenizer token=new StringTokenizer(cmbEmployee.getItemCaption(cmbEmployee.getValue()),"-");
			String empCode=token.nextToken();
			String empName=token.nextToken();
			
			Iterator<?> iter = session.createSQLQuery("select vUserId from dbo.tbUdUserInfo where vUserId =" +
					" '"+userId+"' and vFlag = 'New'").list().iterator();
			if(!iter.hasNext())
			{
				String udSqlInfoNew = " INSERT INTO dbo.tbUdUserInfo(vCompanyId,vUserId,vUserName,vPassword,"+
						" vEmailId,vMobileNo,iUserType,vUserType,dLastLogin,iEditDeleteDays,vEmployeeId,vEmployeeName,"+
						" vCreateBy,vUserIp,dEntryTime,iActive,vFlag) " +
						" select vCompanyId,vUserId,vUserName,vPassword,vEmailId,vMobileNo,iUserType,vUserType,"+
						" dLastLogin,iEditDeleteDays,vEmployeeId,vEmployeeName,vCreateBy,vUserIp,dEntryTime,iActive,'New'"+
						" vFlag from dbo.tbUserInfo where vUserId = '"+userId+"' ";
				session.createSQLQuery(udSqlInfoNew).executeUpdate();

				String udSqlDetailsNew = " Insert into dbo.tbUdUserDetails(vUserId,vUserName,vModuleId,vModuleName,vFlag) " +
						" select vUserId,vUserName,vModuleId,vModuleName,'New' vFlag from dbo.tbUserDetails where" +
						" vUserId = '"+userId+"' ";
				session.createSQLQuery(udSqlDetailsNew).executeUpdate();
			}

			String sql = " Delete from dbo.tbUserInfo where vUserId = '"+userId+"' ";
			session.createSQLQuery(sql).executeUpdate();

			String sqlDelete = " Delete dbo.tbUserDetails where vUserId = '"+userId+"' ";
			session.createSQLQuery(sqlDelete).executeUpdate();

			String udSqlInfo = " INSERT INTO tbUdUserInfo(vCompanyId,vUserId,vUserName,vPassword,"+
					" vEmailId,vMobileNo,iUserType,vUserType,dLastLogin,iEditDeleteDays,vEmployeeId,vEmployeeName,"+
					" vCreateBy,vUserIp,dEntryTime,iActive,vFlag) VALUES (" +
					" '1'," +
					" '"+userId+"'," +
					" '"+txtUserName.getValue().toString()+"'," +
					" '"+txtPassword.getValue().toString()+"'," +
					" '"+txtUserEmail.getValue().toString()+"'," +
					" '"+txtUserMobile.getValue().toString()+"'," +
					" '"+cmbUserType.getValue().toString()+"'," +
					" '"+cmbUserType.getItemCaption(cmbUserType.getValue()).toString()+"'," +
					" CURRENT_TIMESTAMP," +
					" '"+txtEditDeleteDays.getValue().toString()+"'," +
					" '"+cmbEmployee.getValue()+"', '"+empName+"', '"+sessionBean.getUserName()+"', '"+sessionBean.getUserIp()+"'," +
					" CURRENT_TIMESTAMP, '"+isActive.getValue().toString()+"','Update')";

			session.createSQLQuery(udSqlInfo).executeUpdate();

			for(int i=0;i<tbCheck.size();i++)
			{
				if(tbCheck.get(i).booleanValue()==true)
				{	
					String insertDetails = "Insert into dbo.tbUdUserDetails(vUserId,vUserName,vModuleId,"+
							" vModuleName,vFlag) values (" +
							" '"+userId+"'," +
							" '"+txtUserName.getValue().toString()+"'," +
							" '"+i+"'," +
							" '"+tbModuleName.get(i).getValue()+"','Update')";
					session.createSQLQuery(insertDetails).executeUpdate();
				}
			}
			tx.commit();
			ret = true;
		}
		catch(Exception exp)
		{
			tx.rollback();
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
		return ret;
	}

	private void updateBtnAction()
	{
		if(!txtUserName.getValue().toString().isEmpty())
		{
			isUpdate = true;
			btnIni(false);
		}
		else
		{
			showNotification("Edit Failed","There are no data for Edit.",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void addUserInfo()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			ntvFind.removeAllItems();
			String sql = "";
			if(sessionBean.isSuperAdmin())
			{
			     sql = "select vUserId,vUserName from tbUserInfo order by vUserName";
			}
			else if(sessionBean.isAdmin())
			{
				 sql = "select vUserId,vUserName from  tbUserInfo where iUserType!='2'  and  (iUserType='0' or vUserId='"+sessionBean.getUserId()+"') order by vUserName";	
			}
			else
			{
				 sql = "select vUserId,vUserName from  tbUserInfo where vUserId='"+sessionBean.getUserId()+"' order by vUserName";	
			}
			System.out.print("SQL:"+sql);
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				ntvFind.addItem(element[0]);
				ntvFind.setItemCaption(element[0], element[1].toString());
			}
		}
		catch (Exception e)
		{
			showNotification("Unable to get combo data",""+e,Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();};
	}

	private void newBtnAction()
	{
		btnIni(false);
		clear();
		clearAll();
		txtEnable(true);
	}

	private void clear()
	{
		ntvFind.setValue(null);
	}

	private void clearAll()
	{
		txtUserName.setValue("");
		txtPassword.setValue("");
		txtConfirmPassword.setValue("");
		txtUserEmail.setValue("");
		txtUserMobile.setValue("");
		cmbUserType.setValue("0");
		txtEditDeleteDays.setValue("");
		isActive.setValue("1");
		cmbEmployee.setValue(null);
		for(int i=0; i<tbCheck.size(); i++)
		{
			tbCheck.get(i).setValue(false);
		}
	}

	private void txtEnable(boolean t)
	{
		ntvFind.setEnabled(!t);

		table.setEnabled(t);
		txtUserName.setEnabled(t);
		txtPassword.setEnabled(t);
		txtConfirmPassword.setEnabled(t);
		txtUserEmail.setEnabled(t);
		txtUserMobile.setEnabled(t);
		cmbUserType.setEnabled(t);
		txtEditDeleteDays.setEnabled(t);
		isActive.setEnabled(t);
		cmbEmployee.setEnabled(t);
	}

	private void btnIni(boolean t)
	{
		cButton.btnNew.setEnabled(t);
		cButton.btnEdit.setEnabled(t);
		cButton.btnDelete.setEnabled(t);
		cButton.btnSave.setEnabled(!t);
		cButton.btnRefresh.setEnabled(!t);
		cButton.btnFind.setEnabled(t);
	}

	private void focusEnter()
	{
		allComp.add(txtUserName);
		allComp.add(txtPassword);
		allComp.add(txtConfirmPassword);
		allComp.add(txtUserEmail);
		allComp.add(txtUserMobile);
		allComp.add(cmbUserType);
		allComp.add(txtEditDeleteDays);
		allComp.add(isActive);
		allComp.add(cButton.btnSave);

		new FocusMoveByEnter(this,allComp);
	}

	private AbsoluteLayout buildMainLayout() 
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setWidth("800px");
		mainLayout.setHeight("320px");
		mainLayout.setMargin(false);

		ntvFind = new ListSelect();
		ntvFind.setImmediate(true);
		ntvFind.setNullSelectionAllowed(false);
		ntvFind.setNewItemsAllowed(false);
		ntvFind.setDescription("Select to update information.");
		ntvFind.setHeight("240px");
		ntvFind.setWidth("160px");
		mainLayout.addComponent(ntvFind,"top:20.0px; left:5.0px;");
		
		cmbEmployee=new ComboBox();
		cmbEmployee.setImmediate(true);
		cmbEmployee.setWidth("200px");
		cmbEmployee.setHeight("-1px");
		cmbEmployee.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Employee :"),"top:20px; left:180px");
		mainLayout.addComponent(cmbEmployee, "top:18.0px; left:290.0px;");
		

		txtUserName = new TextField();
		txtUserName.setImmediate(true);
		txtUserName.setHeight("-1px");
		txtUserName.setWidth("150px");
		mainLayout.addComponent(new Label("Username :"), "top:45px; left:180.0px;");
		mainLayout.addComponent(txtUserName, "top:43px; left:290.0px;");

		txtPassword = new PasswordField();
		txtPassword.setImmediate(true);
		txtPassword.setWidth("150px");
		txtPassword.setHeight("-1px");
		mainLayout.addComponent(new Label("Password :"), "top:70px; left:180.0px;");
		mainLayout.addComponent(txtPassword, "top:68px; left:290.0px;");

		txtConfirmPassword = new PasswordField();
		txtConfirmPassword.setImmediate(true);
		txtConfirmPassword.setWidth("150px");
		txtConfirmPassword.setHeight("-1px");
		mainLayout.addComponent(new Label("Conf. Password :"), "top:95px; left:180.0px;");
		mainLayout.addComponent(txtConfirmPassword, "top:93px; left:290.0px;");

		txtUserEmail = new TextField();
		txtUserEmail.setImmediate(true);
		txtUserEmail.setWidth("200px");
		txtUserEmail.setHeight("-1px");
		txtUserEmail.setInputPrompt("abcd123@gmail.com");
		mainLayout.addComponent(new Label("Email Id :"), "top:120px; left:180.0px;");
		mainLayout.addComponent(txtUserEmail, "top:118px; left:290.0px;");

		txtUserMobile = new TextField();
		txtUserMobile.setMaxLength(11);
		txtUserMobile.setImmediate(true);
		txtUserMobile.setWidth("150px");
		txtUserMobile.setHeight("-1px");
		txtUserMobile.setInputPrompt("+88017********");
		mainLayout.addComponent(new Label("Mobile No :"), "top:145px; left:180.0px;");
		mainLayout.addComponent(txtUserMobile, "top:143px; left:290.0px;");

		cmbUserType = new ComboBox();
		cmbUserType.setImmediate(true);
		cmbUserType.setWidth("150px");
		cmbUserType.setHeight("-1px");
		cmbUserType.addItem("0");
		cmbUserType.setItemCaption("0", "General");
		if(sessionBean.isAdmin() || sessionBean.isSuperAdmin())
		{
			cmbUserType.addItem("1");
			cmbUserType.setItemCaption("1", "Admin");
			cmbUserType.addItem("2");
			cmbUserType.setItemCaption("2", "Super Admin");
		}
		cmbUserType.setValue("0");
		cmbUserType.setNullSelectionAllowed(false);
		mainLayout.addComponent(new Label("User Type :"), "top:170px; left:180.0px;");
		mainLayout.addComponent(cmbUserType, "top:168px;left:290.0px;");

		txtEditDeleteDays = new AmountField();
		txtEditDeleteDays.setImmediate(true);
		txtEditDeleteDays.setWidth("40px");
		txtEditDeleteDays.setHeight("-1px");
		mainLayout.addComponent(new Label("Edit/Delete Limit :"), "top:195px; left:180.0px;");
		mainLayout.addComponent(txtEditDeleteDays, "top:193px; left:290.0px;");
		mainLayout.addComponent(new Label("Days"), "top:195px; left:335.0px;");

		isActive = new NativeSelect();
		isActive.setImmediate(true);
		isActive.setWidth("100px");
		isActive.setHeight("23px");
		isActive.addItem("1");
		isActive.addItem("0");
		isActive.setItemCaption("1", "Active");
		isActive.setItemCaption("0", "Inactive");
		isActive.setValue("1");
		isActive.setNullSelectionAllowed(false);
		mainLayout.addComponent(new Label("Active :"), "top:220px; left:180.0px;");
		mainLayout.addComponent(isActive,"top:218px; left:290px;");

		table.setWidth("280px");
		table.setHeight("200px");
		table.setImmediate(true);
		table.setColumnCollapsingAllowed(true);
		table.addContainerProperty("Id", Label.class, new Label(), null, null, Table.ALIGN_CENTER);
		table.setColumnWidth("Id", 50);
		table.addContainerProperty("Sel", CheckBox.class, new CheckBox(), null, null, Table.ALIGN_CENTER);
		table.setColumnWidth("Sel", 25);
		table.addContainerProperty("Module Name", Label.class, new Label(), null, null, Table.ALIGN_LEFT);
		table.setColumnWidth("Module Name", 210);
		table.setColumnCollapsed("Id", true);
		mainLayout.addComponent(table,"top:20.0px; right:5.0px;");

		mainLayout.addComponent(cButton,"bottom:15px; left:250px;");

		tableInitialize();
		return mainLayout;
	}

	public void tableInitialize()
	{
		for(int i=0; i<9; i++)
		{
			tableRowAdd(i);
		}
	}

	public void tableRowAdd(final int ar)
	{
		tbId.add(ar,new Label());
		tbId.get(ar).setWidth("100%");
		tbId.get(ar).setValue(ar);
		tbId.get(ar).setImmediate(true);

		tbCheck.add(ar,new CheckBox());
		tbCheck.get(ar).setWidth("100%");
		tbCheck.get(ar).setImmediate(true);
		tbCheck.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(tbCheck.get(ar).booleanValue())
				{
					if(tbModuleName.get(ar).getValue().toString().isEmpty())
					{
						tbCheck.get(ar).setValue(false);
					}
				}
			}
		});

		tbModuleName.add(ar,new Label());
		tbModuleName.get(ar).setWidth("100%");
		tbModuleName.get(ar).setImmediate(true);

		if(ar==0)
		{tbModuleName.get(ar).setValue("SETUP MODULE");}
		if(ar==1)
		{tbModuleName.get(ar).setValue("HR DEPARTMENT");}
		if(ar==2)
		{tbModuleName.get(ar).setValue("ACCOUNTS DEPARTMENT");}
		
		
		
		table.addItem(new Object[]{tbId.get(ar), tbCheck.get(ar), tbModuleName.get(ar)},ar);	
	}
}