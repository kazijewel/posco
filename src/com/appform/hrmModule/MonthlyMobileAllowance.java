package com.appform.hrmModule;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.AmountCommaSeperator;
import com.common.share.CommaSeparator;
import com.common.share.CommonButton;
import com.common.share.CommonMethod;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;
import com.vaadin.data.Property.ValueChangeListener;

@SuppressWarnings("serial")
public class MonthlyMobileAllowance extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Table table= new Table();
	private PopupDateField dDate;

	private Label lblDate ;
	private Label lblUnit;
	private ComboBox cmbUnit;
	private Label lblSection;
	private ComboBox cmbSection,cmbDepartment;

	private Label lblEmployee;
	private ComboBox cmbEmployee;
	private CheckBox chkEmployeeAll,chkDepartmentAll,chkSectionAll;

	private ArrayList<NativeButton> btnDel=new ArrayList<NativeButton>();
	private ArrayList<Label> lblsa = new ArrayList<Label>();
	private ArrayList<Label> lblEmployeeId=new ArrayList<Label>();
	private ArrayList<Label> lblEmployeeCode=new ArrayList<Label>();
	private ArrayList<Label> lblEmployeeName = new ArrayList<Label>();
	private ArrayList<Label> lblDesignation = new ArrayList<Label>();
	private ArrayList<Label> lblDepartmentId = new ArrayList<Label>();
	private ArrayList<Label> lblDepartmentName = new ArrayList<Label>();
	private ArrayList<Label> lblJoiningDate = new ArrayList<Label>();
	private ArrayList<Label> lblEmployeeType = new ArrayList<Label>();
	

	private ArrayList<TextField> txtMobileAll = new ArrayList<TextField>();

	ArrayList<Component> allComp = new ArrayList<Component>();	

	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat FMonthName = new SimpleDateFormat("MMMMM");
	private SimpleDateFormat FYear = new SimpleDateFormat("yyyy");

	CommonButton button = new CommonButton("New", "Save", "Edit", "Delete","Refresh","Find","","","","Exit");
	DecimalFormat dfZero=new DecimalFormat("#");

	private Boolean isUpdate= false;
	private Boolean isFind= false;
	boolean t;
	int i = 0;
	

	private TextRead findId = new TextRead();
	private TextRead EmpId = new TextRead();
	
	private CommonMethod cm;
	private String menuId = "";
	public MonthlyMobileAllowance(SessionBean sessionBean,String menuId) 
	{
		this.sessionBean=sessionBean;
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		this.setCaption("MONTHLY MOBILE ALLAWANCE :: " + sessionBean.getCompany());
		buildMainLayout();
		setContent(mainLayout);
		tableinitialise();
		componentIni(true);
		btnIni(true);
		SetEventAction();

		cmbUnitData();
		focusEnter();
		authenticationCheck();
		button.btnNew.focus();
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

	private void SetEventAction()
	{		
		button.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate = false;
				isFind = false;
				componentIni(false);
				btnIni(false);
				txtClear();
				dDate.focus();
			}
		});

		button.btnRefresh.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate = false;
				isFind = false;
				componentIni(true);
				btnIni(true);
				txtClear();
			}
		});
		button.btnEdit.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbUnit.getValue()!=null)
				{
					isUpdate = true;
					componentIni(false);
					btnIni(false);
					
					cmbUnit.setEnabled(false);
					cmbDepartment.setEnabled(false);
					chkDepartmentAll.setEnabled(false);
					
					cmbSection.setEnabled(false);
					chkSectionAll.setEnabled(false);
					
					cmbEmployee.setEnabled(false);
					chkEmployeeAll.setEnabled(false);
				}
				else
				{
					showNotification("Warning!","No data to update.",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		button.btnSave.addListener( new Button.ClickListener() 
		{			
			public void buttonClick(ClickEvent event)
			{
				if(cmbUnit.getValue()!=null )
				{
				if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
				{
					if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
					{
						if(!lblEmployeeName.get(0).toString().equals(""))
						{
							saveBtnAction(event);
						}
						else
						{
							showNotification("Warning","There are nothing to save!!!",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else
					{
						showNotification("Warning","Please Select Section!!!",Notification.TYPE_WARNING_MESSAGE);
					}			
				}
				else
				{
					showNotification("Warning","Please Select Department!!!",Notification.TYPE_WARNING_MESSAGE);
				}			
			}
			else
			{
				showNotification("Warning","Please select  Project!!!",Notification.TYPE_WARNING_MESSAGE);
			}
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
		
		button.btnExit.addListener( new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				close();
			}
		});


		dDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				tableClear();
				if(dDate.getValue()!=null)
				{
					cmbUnit.setValue(null);
					cmbDepartment.setValue(null);
					cmbSection.setValue(null);
					cmbEmployee.setValue(null);
					chkDepartmentAll.setValue(false);
					chkSectionAll.setValue(false);
					chkEmployeeAll.setValue(false);
				}
			}
		});
		
		cmbUnit.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				tableClear();
				if(cmbUnit.getValue()!=null)
				{
					cmbDepartmentData();
				}
			}
		});
		cmbDepartment.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				tableClear();
				if(cmbDepartment.getValue()!=null)
				{
					cmbSectionData();
				}
			}
		});

		chkDepartmentAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				tableClear();
				if(cmbUnit.getValue()!=null)
				{
					if(chkDepartmentAll.booleanValue())
					{
						cmbDepartment.setEnabled(false);
						cmbDepartment.setValue(null);
						cmbSectionData();
					}
					else
					{
						cmbDepartment.setEnabled(true);
					}
				}
				else
				{
					chkDepartmentAll.setValue(false);
				}
			}
		});
		cmbSection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				tableClear();
				if(cmbSection.getValue()!=null)
				{
					cmbEmployeeNameDataAdd();
				}
			}
		});
		chkSectionAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				tableClear();
				if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
				{
					if(chkSectionAll.booleanValue())
					{
						cmbSection.setEnabled(false);
						cmbSection.setValue(null);
						cmbEmployeeNameDataAdd();
					}
					else
					{
						cmbSection.setEnabled(true);
					}
				}
				else
				{
					chkSectionAll.setValue(false);
				}
			}
		});

		chkEmployeeAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				tableClear();
				if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
				{
					if(chkEmployeeAll.booleanValue())
					{
						cmbEmployee.setEnabled(false);
						cmbEmployee.setValue(null);
						addTableData();
					}
					else
					{
						cmbEmployee.setEnabled(true);
					}
				}
				else
				{
					chkEmployeeAll.setValue(false);
				}
			}
		});
		cmbEmployee.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
				//tableClear();
				if(cmbEmployee.getValue()!=null)
				{
					addTableData();
				}
			}
		});
	}
	private void findButtonEvent()
	{
		Window win = new MonthlyMobileAllowanceFind(sessionBean, findId, EmpId);
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
			String findQuery = "select vEmployeeId,vEmployeeCode,vEmployeeName,vDesignation,vUnitId," +
					"dJoiningDate,vSectionId," +
					"(select vDepartmentId from tbEmpOfficialPersonalInfo where vEmployeeId=eli.vEmployeeId)deptId,dEntryDate  " +
					"from tbLeaveEntitlement eli where vLeaveId = '"+findId+"' and vEmployeeId = '"+strEmpID+"'";
			
			System.out.println("FindQuery " + findQuery);
			List <?> list = session.createSQLQuery(findQuery).list();
			
			if(!list.isEmpty())
			{
				if(list.iterator().hasNext())
				{
					Object[] element = (Object[]) list.iterator().next();
					cmbUnit.setValue(element[0].toString());
					cmbSection.setValue(element[1]);
					for(int i=0; i<list.size(); i++)
					{
						lblEmployeeId.get(i).setValue(element[2].toString());
						lblEmployeeCode.get(i).setValue(element[3]);
						lblEmployeeName.get(i).setValue(element[4]);
						lblDesignation.get(i).setValue(element[5]);
						lblDepartmentId.get(i).setValue(element[5]);
						lblDepartmentName.get(i).setValue(element[5]);
						lblJoiningDate.get(i).setValue(element[6]);
						lblEmployeeType.get(i).setValue(element[6]);
						
						txtMobileAll.get(i).setValue(element[7]);
					}
					cmbDepartment.setValue(element[12]);
					dDate.setValue(element[13]);
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
	
	private void addTableData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String check = " SELECT vEmployeeID from tbMonthlyMobileAllowance where MONTH(dDate)=MONTH('"+dFormat.format(dDate.getValue())+"') "
					+ "and YEAR(dDate)=YEAR('"+dFormat.format(dDate.getValue())+"') and vUnitId='"+cmbUnit.getValue().toString()+"' "
					+ "and vSectionID like '"+(chkSectionAll.booleanValue()?"%":(cmbSection.getValue()==null?"%":cmbSection.getValue()))+"' "
					+ "and vDepartmentId like '"+(chkDepartmentAll.booleanValue()?"%":(cmbDepartment.getValue()==null?"%":cmbDepartment.getValue()))+"' "
					+ "and vEmployeeID like '"+(chkEmployeeAll.booleanValue()?"%":(cmbEmployee.getValue()==null?"%":cmbEmployee.getValue()))+"' ";
		
			System.out.println("addTableDataCheck :" +check);
			
			List <?> checkList = session.createSQLQuery(check).list();

			if(checkList.isEmpty())
			{				
				String sql = "select a.vEmployeeID,vEmployeeCode,a.vEmployeeName,vDesignationName,a.vDepartmentId,a.vDepartmentName,a.dJoiningDate,"
						+ "b.mMobileAllowance,vServiceType from tbEmpOfficialPersonalInfo a "
						+ "inner join tbEmpSalaryStructure b on a.vEmployeeId=b.vEmployeeId "
						+ "where vUnitId='"+cmbUnit.getValue().toString()+"' "
						+ "and vSectionID like '"+(chkSectionAll.booleanValue()?"%":(cmbSection.getValue()==null?"%":cmbSection.getValue()))+"' "
						+ "and vDepartmentId like '"+(chkDepartmentAll.booleanValue()?"%":(cmbDepartment.getValue()==null?"%":cmbDepartment.getValue()))+"' "
						+ "and a.vEmployeeID like '"+(chkEmployeeAll.booleanValue()?"%":(cmbEmployee.getValue()==null?"%":cmbEmployee.getValue()))+"' "
						+ "and mMobileAllowance>0 and bStatus=1 "
						+ "order by SUBSTRING(vEmployeeCode,3,50) ";
				
				System.out.println("addTableData :" + sql);
				List <?> lst = session.createSQLQuery(sql).list();
				for (Iterator <?> iter = lst.iterator(); iter.hasNext();) {
					if(i==lblEmployeeName.size()-1)	{
						tableRowAdd(i+1);
					}
					Object[] element = (Object[]) iter.next();
					for(int j=0;j<lblEmployeeId.size();j++) {
						if(lblEmployeeId.get(j).getValue().toString().trim().isEmpty())	{
							i=j;
							t=true;
							break;
						}
						if(!lblEmployeeId.get(j).getValue().toString().trim().isEmpty()) {
							if(lblEmployeeId.get(j).getValue().toString().equals(element[0].toString())) {
								t=false;
								break;
							}
						}
					}
					if(t)
					{
						lblEmployeeId.get(i).setValue(element[0]);
						lblEmployeeCode.get(i).setValue(element[1]);
						lblEmployeeName.get(i).setValue(element[2]);
						lblDesignation.get(i).setValue(element[3]);
						lblDepartmentId.get(i).setValue(element[4]);
						lblDepartmentName.get(i).setValue(element[5]);
						lblJoiningDate.get(i).setValue(element[6]);
						txtMobileAll.get(i).setValue(dfZero.format(element[7]));
						lblEmployeeType.get(i).setValue(element[8]);
						
						i++;
					}


				}
				if(i == 0)
				{
					showNotification("Warning!","No Data found",Notification.TYPE_WARNING_MESSAGE);
				}
			}
			else
			{
				tableClear();
				showNotification("Warning!","No data found or Employee is already exists for this month",Notification.TYPE_WARNING_MESSAGE);
			}
			if(!t)
			{
				showNotification("Warning","This Employee is already exists!!!",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception ex)
		{
			showNotification("addTableData",ex.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void cmbUnitData()
	{
		cmbUnit.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vUnitId,vUnitName from tbEmpOfficialPersonalInfo where bStatus=1 order by vUnitName";
			
			System.out.println("Unit"+query);
			
			List <?> list=session.createSQLQuery(query).list();	

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
			finally{session.close();
		}
		
	}
	private void cmbDepartmentData() 
	{
		cmbDepartment.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vDepartmentId,vDepartmentName from tbEmpOfficialPersonalInfo where vUnitId='"+cmbUnit.getValue()+"' "
					+ "and bStatus=1 order by vDepartmentName";
			
			System.out.println("Section"+query);			
			List <?> list=session.createSQLQuery(query).list();	

			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbDepartment.addItem(element[0]);
				cmbDepartment.setItemCaption(element[0], (String) element[1]);
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbSectionData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void cmbSectionData() 
	{
		cmbSection.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vSectionId,vSectionName from tbEmpOfficialPersonalInfo where vUnitId='"+cmbUnit.getValue().toString()+"' " +
					"and vDepartmentId like '"+(cmbDepartment.getValue()==null?"%":cmbDepartment.getValue())+"' and bStatus=1 order by vSectionName";
			
			System.out.println("Section"+query);
			
			List <?> list=session.createSQLQuery(query).list();	

			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbSection.addItem(element[0]);
				cmbSection.setItemCaption(element[0], (String) element[1]);
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbSectionData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void cmbEmployeeNameDataAdd()
	{
		cmbEmployee.removeAllItems();		
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select vEmployeeId,vEmployeeName,vEmployeeCode from tbEmpOfficialPersonalInfo where vUnitId like '"+cmbUnit.getValue().toString()+"' " +
					"and vDepartmentId like '"+(cmbDepartment.getValue()==null?"%":cmbDepartment.getValue())+"' " +
					"and vSectionId like '"+(cmbSection.getValue()==null?"%":cmbSection.getValue())+"' and bStatus=1 order by vEmployeeName";
	
			System.out.println("Employee"+query);			
			List <?> lst=session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				for(Iterator <?> itr=lst.iterator();itr.hasNext();)
				{
					Object [] element=(Object[]) itr.next();
					cmbEmployee.addItem(element[0]);
					cmbEmployee.setItemCaption(element[0], (String)element[2]+">>"+element[1].toString());
				}
			}
			else
			{
				showNotification("Warning", "No Employee Found!!!", Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbEmployeeNameDataAdd",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void saveBtnAction(ClickEvent e)
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
			for(int i = 0;i<lblEmployeeId.size();i++)
			{
				if(!lblEmployeeId.get(i).getValue().toString().isEmpty())
				{
					String query="insert into tbMonthlyMobileAllowance(" +
							"dDate,vEmployeeID,vEmployeeCode,vEmployeeName,vDesignationName,dJoiningDate,vUnitId,vUnitName,vDepartmentId,"
							+ "vDepartmentName,vSectionID,vSectionName,mMobileAllowance,vEmployeeType,vUserId,vUserName,vUserIP,dEntryTime" +
							") "
							+ "values ("
							+ "'"+dFormat.format(dDate.getValue())+"',"
							+ "'"+lblEmployeeId.get(i).getValue()+"',"
							+ "'"+lblEmployeeCode.get(i).getValue()+"',"
							+ "'"+lblEmployeeName.get(i).getValue()+"',"
							+ "'"+lblDesignation.get(i).getValue()+"',"
							+ "'"+lblJoiningDate.get(i).getValue()+"',"
							+ "'"+cmbUnit.getValue()+"',"
							+ "'"+cmbUnit.getItemCaption(cmbUnit.getValue())+"',"
							+ "'"+lblDepartmentId.get(i).getValue()+"',"
							+ "'"+lblDepartmentName.get(i).getValue()+"',"
							+ "'',"
							+ "'',"
							+ "'"+txtMobileAll.get(i).getValue()+"',"
							+ "'"+lblEmployeeType.get(i).getValue()+"',"
							+ "'"+sessionBean.getUserId()+"','"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',getdate())";

					System.out.println("insertData :" +query);
					
					session.createSQLQuery(query).executeUpdate();
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
		catch(Exception ex)
		{
			showNotification("insertData", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
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
			for(int i = 0;i<lblEmployeeId.size();i++)
			{
				if(!lblEmployeeId.get(i).getValue().toString().isEmpty())
				{
					String udquery="insert into tbMonthlyMobileAllowance(" +
							"dDate,vEmployeeID,vEmployeeCode,vEmployeeName,vDesignationName,dJoiningDate,vUnitId,vUnitName,vDepartmentId,"
							+ "vDepartmentName,vSectionID,vSectionName,mMobileAllowance,vUserId,vUserName,vUserIP,dEntryTime" +
							") "
							+ "values ()";

					System.out.println("Update Monthly Salary UD :" +udquery);
					
					session.createSQLQuery(udquery).executeUpdate();
					session.clear();
					
					String query = "update tbEmpOfficialPersonalInfo " +
							"set mMobileAllowance='"+(txtMobileAll.get(i).getValue().toString().trim().isEmpty()?0:txtMobileAll.get(i).getValue().toString().trim())+"'," + 
							"vUserName='"+sessionBean.getUserName()+"',vUserIP='"+sessionBean.getUserIp()+"',dEntryTime=GETDATE() " +
							"where vEmployeeID = '"+lblEmployeeId.get(i).getValue().toString()+"' " +
							"and vSalaryMonth='"+FMonthName.format(dDate.getValue())+"' and vSalaryYear='"+FYear.format(dDate.getValue())+"'";

					System.out.println("Update Monthly Salary :" +query);
					
					session.createSQLQuery(query).executeUpdate();
					session.clear();
				}
			}
			tx.commit();
			Notification n=new Notification("All Information Updated Successfully!","",Notification.TYPE_TRAY_NOTIFICATION);
			n.setPosition(Notification.POSITION_TOP_RIGHT);
			showNotification(n);
		}
		catch(Exception ex)
		{
			showNotification("updateData", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
		finally{session.close();}
	}

	private void focusEnter()
	{
		allComp.add(dDate);
		allComp.add(cmbUnit);
		allComp.add(cmbDepartment);
		allComp.add(cmbSection);
		for(int i=0;i<lblEmployeeId.size();i++)
		{
			allComp.add(txtMobileAll.get(i));
		}
		allComp.add(button.btnSave);
		new FocusMoveByEnter(this,allComp);
	}

	private void componentIni(boolean b) 
	{
		dDate.setEnabled(!b);
		cmbUnit.setEnabled(!b);
		cmbDepartment.setEnabled(!b);
		cmbSection.setEnabled(!b);
		cmbEmployee.setEnabled(!b);
		chkDepartmentAll.setEnabled(!b);
		chkSectionAll.setEnabled(!b);
		chkEmployeeAll.setEnabled(!b);
		table.setEnabled(!b);
		chkEmployeeAll.setValue(false);
		if(isUpdate)
		{cmbEmployee.setEnabled(false);}
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

	public void txtClear()
	{
		cmbUnit.setValue(null);
		cmbDepartment.setValue(null);
		cmbSection.setValue(null);
		cmbEmployee.setValue(null);
		chkDepartmentAll.setValue(false);
		chkSectionAll.setValue(false);
		chkEmployeeAll.setValue(false);
		tableClear();
	}

	private void tableClear()
	{
		for(int i=0; i<lblEmployeeName.size(); i++)
		{
			lblEmployeeId.get(i).setValue("");
			lblEmployeeCode.get(i).setValue("");
			lblEmployeeName.get(i).setValue("");
			lblDesignation.get(i).setValue("");
			lblDepartmentId.get(i).setValue("");
			lblDepartmentName.get(i).setValue("");
			lblJoiningDate.get(i).setValue("");
			lblEmployeeType.get(i).setValue("");
			
			txtMobileAll.get(i).setValue("");
		}
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
		btnDel.add(ar, new NativeButton());
		btnDel.get(ar).setWidth("100%");
		btnDel.get(ar).setImmediate(true);
		btnDel.get(ar).setIcon(new ThemeResource("../icons/cancel.png"));
		btnDel.get(ar).addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				lblEmployeeId.get(ar).setValue("");
				lblEmployeeCode.get(ar).setValue("");
				lblEmployeeName.get(ar).setValue("");
				lblDesignation.get(ar).setValue("");
				lblDepartmentId.get(ar).setValue("");
				lblDepartmentName.get(ar).setValue("");
				lblJoiningDate.get(ar).setValue("");
				lblEmployeeType.get(ar).setValue("");
				
				txtMobileAll.get(ar).setValue("");

				for(int rowcount=ar;rowcount<=lblEmployeeId.size()-1;rowcount++)
				{
					if(rowcount+1<=lblEmployeeId.size()-1)
					{
						if(!lblEmployeeId.get(rowcount+1).getValue().toString().equals(""))
						{
							lblEmployeeId.get(rowcount).setValue(lblEmployeeId.get(rowcount+1).getValue().toString());
							lblEmployeeCode.get(rowcount).setValue(lblEmployeeCode.get(rowcount+1).getValue().toString());
							lblEmployeeName.get(rowcount).setValue(lblEmployeeName.get(rowcount+1).getValue().toString());
							lblDesignation.get(rowcount).setValue(lblDesignation.get(rowcount+1).getValue().toString());
							
							lblDepartmentId.get(rowcount).setValue(lblDepartmentId.get(rowcount+1).getValue().toString());
							lblDepartmentName.get(rowcount).setValue(lblDepartmentName.get(rowcount+1).getValue().toString());
							lblJoiningDate.get(rowcount).setValue(lblJoiningDate.get(rowcount+1).getValue().toString());
							lblEmployeeType.get(rowcount).setValue(lblEmployeeType.get(rowcount+1).getValue().toString());
							
							txtMobileAll.get(rowcount).setValue(txtMobileAll.get(rowcount+1).getValue().toString());

							lblEmployeeId.get(rowcount+1).setValue("");
							lblEmployeeCode.get(rowcount+1).setValue("");
							lblEmployeeName.get(rowcount+1).setValue("");
							lblDesignation.get(rowcount+1).setValue("");
							
							lblDepartmentId.get(rowcount+1).setValue("");
							lblDepartmentName.get(rowcount+1).setValue("");
							lblJoiningDate.get(rowcount+1).setValue("");
							lblEmployeeType.get(rowcount+1).setValue("");
							
							txtMobileAll.get(rowcount+1).setValue("");
						}
					}
				}

			}
		});

		lblsa.add(ar,new Label());
		lblsa.get(ar).setWidth("100%");
		lblsa.get(ar).setHeight("16px");
		lblsa.get(ar).setValue(ar+1);

		lblEmployeeId.add(ar, new Label());
		lblEmployeeId.get(ar).setWidth("100%");
		lblEmployeeId.get(ar).setImmediate(true);

		lblEmployeeCode.add(ar, new Label());
		lblEmployeeCode.get(ar).setWidth("100%");
		lblEmployeeCode.get(ar).setImmediate(true);

		lblEmployeeName.add(ar,new Label());
		lblEmployeeName.get(ar).setWidth("100%");
		lblEmployeeName.get(ar).setImmediate(true);

		lblDesignation.add(ar, new Label());
		lblDesignation.get(ar).setWidth("100%");
		lblDesignation.get(ar).setImmediate(true);
		
		lblDepartmentId.add(ar, new Label());
		lblDepartmentId.get(ar).setWidth("100%");
		lblDepartmentId.get(ar).setImmediate(true);

		lblDepartmentName.add(ar, new Label());
		lblDepartmentName.get(ar).setWidth("100%");
		lblDepartmentName.get(ar).setImmediate(true);

		lblJoiningDate.add(ar, new Label());
		lblJoiningDate.get(ar).setWidth("100%");
		lblJoiningDate.get(ar).setImmediate(true);

		lblEmployeeType.add(ar, new Label());
		lblEmployeeType.get(ar).setWidth("100%");
		lblEmployeeType.get(ar).setImmediate(true);
		
		txtMobileAll.add(ar, new TextField());
		txtMobileAll.get(ar).setWidth("100%");
		txtMobileAll.get(ar).setImmediate(true);
		table.addItem(new Object[]{btnDel.get(ar),lblsa.get(ar),lblEmployeeId.get(ar),lblEmployeeCode.get(ar),lblEmployeeName.get(ar),
				lblDesignation.get(ar),lblDepartmentId.get(ar),lblDepartmentName.get(ar),lblJoiningDate.get(ar),lblEmployeeType.get(ar),
				txtMobileAll.get(ar)},ar);
	}
	
	private AbsoluteLayout buildMainLayout() 
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);
		mainLayout.setWidth("688px");
		mainLayout.setHeight("550px");

		lblDate = new Label("Date :");
		lblDate.setImmediate(false);
		lblDate.setWidth("-1px");
		lblDate.setHeight("-1px");
		mainLayout.addComponent(lblDate, "top:20.0px; left:40.0px;");

		dDate = new PopupDateField();
		dDate.setImmediate(true);
		dDate.setWidth("150px");
		dDate.setHeight("-1px");
		dDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dDate.setDateFormat("dd-MM-yyyy");
		dDate.setValue(new java.util.Date());
		mainLayout.addComponent(dDate, "top:18.0px; left:150.0px;");
		
		lblUnit = new Label("Project :");
		lblUnit.setImmediate(false); 
		lblUnit.setWidth("-1px");
		lblUnit.setHeight("-1px");

		cmbUnit = new ComboBox();
		cmbUnit.setImmediate(true);
		cmbUnit.setWidth("280px");
		cmbUnit.setHeight("24px");
		cmbUnit.setNullSelectionAllowed(true);
		cmbUnit.setNewItemsAllowed(false);
		cmbUnit.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(lblUnit, "top:45px; left:40.0px;");
		mainLayout.addComponent(cmbUnit, "top:43px; left:150.0px;");
		

		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("280px");
		cmbDepartment.setHeight("24px");
		cmbDepartment.setNullSelectionAllowed(true);
		cmbDepartment.setNewItemsAllowed(false);
		cmbDepartment.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Department :"), "top:70px; left:40.0px;");
		mainLayout.addComponent(cmbDepartment, "top:68px; left:150.0px;");
		
		chkDepartmentAll=new CheckBox("All");
		chkDepartmentAll.setImmediate(true);
		chkDepartmentAll.setWidth("-1px");
		chkDepartmentAll.setHeight("-1px");
		mainLayout.addComponent(chkDepartmentAll, "top:70px; left:435px;");
		
		lblSection = new Label("Section :");
		lblSection.setImmediate(false); 
		lblSection.setWidth("-1px");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection, "top:95px; left:40.0px;");

		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("280px");
		cmbSection.setHeight("24px");
		cmbSection.setNullSelectionAllowed(true);
		cmbSection.setNewItemsAllowed(false);
		cmbSection.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbSection, "top:93px; left:150.0px;");
		
		chkSectionAll=new CheckBox("All");
		chkSectionAll.setImmediate(true);
		chkSectionAll.setWidth("-1px");
		chkSectionAll.setHeight("-1px");
		mainLayout.addComponent(chkSectionAll, "top:95px; left:435px;");

		lblEmployee = new Label("Employee ID :");
		lblEmployee.setImmediate(false); 
		lblEmployee.setWidth("-1px");
		lblEmployee.setHeight("-1px");
		mainLayout.addComponent(lblEmployee, "top:118px; left:40.0px;");

		cmbEmployee = new ComboBox();
		cmbEmployee.setImmediate(true);
		cmbEmployee.setWidth("280px");
		cmbEmployee.setHeight("24px");
		cmbEmployee.setNullSelectionAllowed(true);
		cmbEmployee.setNewItemsAllowed(false);
		cmbEmployee.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbEmployee, "top:120px; left:150.0px;");

		chkEmployeeAll = new CheckBox("All");
		chkEmployeeAll.setImmediate(true);
		chkEmployeeAll.setHeight("-1px");
		chkEmployeeAll.setWidth("-1px");
		mainLayout.addComponent(chkEmployeeAll, "top:120px; left:435.0px;");

		table.setWidth("650px");
		table.setHeight("340px");
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("Del", NativeButton.class, new NativeButton());
		table.setColumnWidth("Del", 30);

		table.addContainerProperty("SL", Label.class, new Label());
		table.setColumnWidth("SL", 20);

		table.addContainerProperty("System ID", Label.class, new Label());
		table.setColumnWidth("System ID", 60);

		table.addContainerProperty("EMPLOYEE ID", Label.class, new Label());
		table.setColumnWidth("EMPLOYEE ID", 90);

		table.addContainerProperty("Employee Name", Label.class, new Label());
		table.setColumnWidth("Employee Name",  190);

		table.addContainerProperty("Designation", Label.class, new Label());
		table.setColumnWidth("Designation", 150);

		table.addContainerProperty("Department ID", Label.class, new Label());
		table.setColumnWidth("Department ID", 150);

		table.addContainerProperty("Department Name", Label.class, new Label());
		table.setColumnWidth("Department Name", 150);

		table.addContainerProperty("Joining Date", Label.class, new Label());
		table.setColumnWidth("Joining Date", 60);

		table.addContainerProperty("EmployeeType", Label.class, new Label());
		table.setColumnWidth("EmployeeType", 60);

		table.addContainerProperty("Mobile", TextField.class, new TextField());
		table.setColumnWidth("Mobile", 70);

		table.setColumnCollapsed("System ID", true);
		table.setColumnCollapsed("Department ID", true);
		table.setColumnCollapsed("Department Name", true);
		table.setColumnCollapsed("Joining Date", true);
		table.setColumnCollapsed("EmployeeType", true);

		mainLayout.addComponent(table,"top:150px; left:20.0px;");		
		table.setStyleName("wordwrap-headers");
		
		mainLayout.addComponent(button,"bottom:15px; left:40.0px");
		return mainLayout;
	}
}