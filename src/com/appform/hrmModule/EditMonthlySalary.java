package com.appform.hrmModule;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.vaadin.ui.Window.Notification;
import com.vaadin.data.Property.ValueChangeListener;

@SuppressWarnings("serial")
public class EditMonthlySalary extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Table table= new Table();
	private ComboBox cmbSalaryMonth;

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
	private ArrayList<Label> lblAutoEmployeeID=new ArrayList<Label>();
	private ArrayList<Label> lblEmployeeID=new ArrayList<Label>();
	private ArrayList<Label> lblEmployeeName = new ArrayList<Label>();
	private ArrayList<Label> lblDesignation = new ArrayList<Label>();

	private ArrayList<Label> lblBasic = new ArrayList<Label>();
	private ArrayList<Label> lblHouseAll = new ArrayList<Label>();
	private ArrayList<Label> lblMobileAll = new ArrayList<Label>();
	private ArrayList<TextField> txtWorkingDays = new ArrayList<TextField>();
	private ArrayList<Label> lblPerDaySalary = new ArrayList<Label>();
	private ArrayList<Label> lblSalaryTaka = new ArrayList<Label>();
	private ArrayList<Label> lblHolidayNetOTHr = new ArrayList<Label>();
	private ArrayList<Label> lblWorkingDayNetOTHr = new ArrayList<Label>();
	private ArrayList<Label> lblOTTaka = new ArrayList<Label>();

	private ArrayList<TextField> txtOtherEarning = new ArrayList<TextField>();
	private ArrayList<TextField> txtOtherDeduction = new ArrayList<TextField>();
	
	private ArrayList<Label> lblTotalPayable = new ArrayList<Label>();	
	private ArrayList<Label> lblIncomeTax = new ArrayList<Label>();	
	private ArrayList<Label> lblNetPayableTaka = new ArrayList<Label>();

	ArrayList<Component> allComp = new ArrayList<Component>();	

	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat FMonthName = new SimpleDateFormat("MMMMM");
	private SimpleDateFormat FYear = new SimpleDateFormat("yyyy");

	CommonButton button = new CommonButton("New", "Save", "", "","Refresh","","","","","Exit");
	
	DecimalFormat dfZero=new DecimalFormat("#");

	private Boolean isUpdate= false;
	boolean t;
	int i = 0;

	//private CommaSeparator cs = new CommaSeparator();

	String empId="";
	String empCode="";
	String empFingerId="";
	String empProxId="";
	String empDesId="";

	String vMonthName="";
	String yearName="";
	
	String Notify="";
	private int ar;
	private CommonMethod cm;
	private String menuId = "";
	public EditMonthlySalary(SessionBean sessionBean,String menuId) 
	{
		this.sessionBean=sessionBean;
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		this.setCaption("MONTHLY SALARY EDIT :: " + sessionBean.getCompany());
		buildMainLayout();
		setContent(mainLayout);
		tableinitialise();
		componentIni(true);
		btnIni(true);
		SetEventAction();
		focusEnter();
		cmbSalaryMonthData();
		authenticationCheck();
		button.btnNew.focus();
	}


	private void cmbSalaryMonthData()
	{
		cmbSalaryMonth.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select vSalaryMonth,vSalaryYear,DATEADD(s,-1,DATEADD(mm, DATEDIFF(m,0,dSalaryDate)+1,0)) dSalaryDate from tbMonthlySalary  order by dSalaryDate desc";
			List <?> list=session.createSQLQuery(query).list();	

			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbSalaryMonth.addItem( element[2]);
				cmbSalaryMonth.setItemCaption( element[2], element[0].toString()+"-"+element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbSalaryMonthData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
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
				componentIni(false);
				btnIni(false);
				txtClear();
				cmbSalaryMonth.focus();
			}
		});

		button.btnRefresh.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate = false;
				componentIni(true);
				btnIni(true);
				txtClear();
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

		button.btnExit.addListener( new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				close();
			}
		});


		cmbSalaryMonth.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				tableClear();
				if(cmbSalaryMonth.getValue()!=null)
				{
					StringTokenizer strToken=new StringTokenizer(cmbSalaryMonth.getItemCaption(cmbSalaryMonth.getValue()), "- ");
					vMonthName=strToken.nextToken();
					yearName=strToken.nextToken();
					cmbUnit.removeAllItems();
					cmbUnitData();
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
	
	private void addTableData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String check = " SELECT vEmployeeID from tbMonthlySalary where vUnitId='"+cmbUnit.getValue().toString()+"' " +
						"and vSalaryYear='"+FYear.format(cmbSalaryMonth.getValue())+"' and vSalaryMonth='"+FMonthName.format(cmbSalaryMonth.getValue())+"' " +
						"and vSectionID like '"+(chkSectionAll.booleanValue()?"%":(cmbSection.getValue()==null?"%":cmbSection.getValue()))+"' " +
						"and vDepartmentId like '"+(chkDepartmentAll.booleanValue()?"%":(cmbDepartment.getValue()==null?"%":cmbDepartment.getValue()))+"' " +
						"and vEmployeeID like '"+(chkEmployeeAll.booleanValue()?"%":(cmbEmployee.getValue()==null?"%":cmbEmployee.getValue()))+"' ";
		
			System.out.println("addTableData :" +check);
			
			List <?> checkList = session.createSQLQuery(check).list();

			if(!checkList.isEmpty())
			{				
				String sql = "select vEmployeeID,vEmployeeCode,vEmployeeName,vDesignationName,mBasic,mHouseRent,mMobileAllowance,mSalaryTaka,"
						+ "mOtTaka,mOtherEarning,mOtherDeduction,mTotalPayable,mIncomeTax,mNetPayableTaka,iWorkingDay,iHolidayNetOTHr,"
						+ "iWorkingDayNetOTHr,mPerDaySalary "
						+ "from tbMonthlySalary "
						+ "where vUnitId='"+cmbUnit.getValue().toString()+"' "
						+ "and vSalaryYear='"+FYear.format(cmbSalaryMonth.getValue())+"' and vSalaryMonth='"+FMonthName.format(cmbSalaryMonth.getValue())+"' "
						+ "and vSectionID like '"+(chkSectionAll.booleanValue()?"%":(cmbSection.getValue()==null?"%":cmbSection.getValue()))+"' "
						+ "and vDepartmentId like '"+(chkDepartmentAll.booleanValue()?"%":(cmbDepartment.getValue()==null?"%":cmbDepartment.getValue()))+"' "
						+ "and vEmployeeID like '"+(chkEmployeeAll.booleanValue()?"%":(cmbEmployee.getValue()==null?"%":cmbEmployee.getValue()))+"' "
						+ "order by SUBSTRING(vEmployeeCode,3,50)";

				System.out.println("addTableData :" + sql);

				
				List <?> lst = session.createSQLQuery(sql).list();

				for (Iterator <?> iter = lst.iterator(); iter.hasNext();) 
				{
					if(i==lblEmployeeName.size()-1)
					{
						tableRowAdd(i+1);
					}

					Object[] element = (Object[]) iter.next();

					for(int j=0;j<lblAutoEmployeeID.size();j++)
					{
						if(lblAutoEmployeeID.get(j).getValue().toString().trim().isEmpty())
						{
							i=j;
							t=true;
							break;
						}
						if(!lblAutoEmployeeID.get(j).getValue().toString().trim().isEmpty())
						{
							if(lblAutoEmployeeID.get(j).getValue().toString().equals(element[0].toString()))
							{
								t=false;
								break;
							}
						}
					}

					if(t)
					{
						lblAutoEmployeeID.get(i).setValue(element[0]);
						lblEmployeeID.get(i).setValue(element[1]);
						lblEmployeeName.get(i).setValue(element[2]);
						lblDesignation.get(i).setValue(element[3]);
						
						lblBasic.get(i).setValue(dfZero.format(element[4]));
						lblHouseAll.get(i).setValue(dfZero.format(element[5]));
						lblMobileAll.get(i).setValue(dfZero.format(element[6]));
						txtWorkingDays.get(i).setValue(dfZero.format(element[14]));
						lblSalaryTaka.get(i).setValue(dfZero.format(element[7]));
						lblPerDaySalary.get(i).setValue(dfZero.format(element[17]));
						lblWorkingDayNetOTHr.get(i).setValue(dfZero.format(element[15]));
						lblOTTaka.get(i).setValue(dfZero.format(element[16]));
						lblOTTaka.get(i).setValue(dfZero.format(element[8]));
						txtOtherEarning.get(i).setValue(dfZero.format(element[9]));
						txtOtherDeduction.get(i).setValue(dfZero.format(element[10]));
						lblTotalPayable.get(i).setValue(dfZero.format(element[11]));
						lblIncomeTax.get(i).setValue(dfZero.format(element[12]));
						lblNetPayableTaka.get(i).setValue(dfZero.format(element[13]));
						
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
				showNotification("Warning!","No data found",Notification.TYPE_WARNING_MESSAGE);
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
			String query="select distinct vUnitId,vUnitName from tbMonthlySalary where vSalaryMonth='"+vMonthName+"' and vSalaryYear='"+yearName+"' " +
					"order by vUnitName";
			
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
			String query="select distinct vDepartmentId,vDepartmentName from tbMonthlySalary  where vUnitId='"+cmbUnit.getValue().toString()+"' " +
					"and vSalaryMonth='"+vMonthName+"' and vSalaryYear='"+yearName+"'  " +
					"order by vDepartmentName";
			
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
			String query="select distinct vSectionId,vSectionName from tbMonthlySalary where vUnitId='"+cmbUnit.getValue().toString()+"' " +
					"and vDepartmentId like '"+(cmbDepartment.getValue()==null?"%":cmbDepartment.getValue())+"' " +
					"and vSalaryMonth='"+vMonthName+"' and vSalaryYear='"+yearName+"' order by vSectionName";
			
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
			String query="select vEmployeeId,vEmployeeName,vEmployeeCode from tbMonthlySalary where vUnitId like '"+cmbUnit.getValue().toString()+"' " +
					"and vDepartmentId like '"+(cmbDepartment.getValue()==null?"%":cmbDepartment.getValue())+"' " +
					"and vSectionId like '"+(cmbSection.getValue()==null?"%":cmbSection.getValue())+"' " +
					"and vSalaryMonth='"+vMonthName+"' and vSalaryYear='"+yearName+"' " +
					"order by vEmployeeName";
	
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
		MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to update all information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
		mb.show(new EventListener()
		{
			public void buttonClicked(ButtonType buttonType)
			{
				if(buttonType == ButtonType.YES)
				{
					updateData();
					txtClear();
					componentIni(true);
					btnIni(true);
				}
			}
		});
	}

	private void updateData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{
			for(int i = 0;i<lblAutoEmployeeID.size();i++)
			{
				if(!lblAutoEmployeeID.get(i).getValue().toString().isEmpty())
				{
					String udquery="insert into tbUdMonthlySalary(" +
							"dGenerateDate,dSalaryDate,vSalaryMonth,vSalaryYear,vEmployeeID,vEmployeeCode,vFingerId,vProximityID,vEmployeeName," +
							"vDesignationId,vDesignationName,vEmployeeType,dJoiningDate,vUnitId,vUnitName,vDepartmentId,vDepartmentName,vSectionID,vSectionName," +
							"iTotalDay,iHoliday,iWorkingDay,iPresentDay,iAbsentDay,iLeaveDay,iLeaveWithoutPay,mBasic,mHouseRent,mMobileAllowance,mGrossSalary," +
							"mPerDaySalary,mSalaryTaka,iHolidayOTHr,iReplaceOTHr,iHolidayNetOTHr,iWorkingDayNetOTHr,mPerHrOTRate,mOtTaka,mTotalPayable," +
							"mIncomeTax,mNetPayableTaka,mRevenueStamp,vMoneyTransferType,vBranchId,vBranchName,vBankId,vBankName,vAccountNo,vRoutingNo," +
							"mOtherEarning,mOtherDeduction,vUDFlag,vUserId,vUserName,vUserIP,dEntryTime" +
							") " +
							"select dGenerateDate,dSalaryDate,vSalaryMonth,vSalaryYear,vEmployeeID,vEmployeeCode,vFingerId,vProximityID,vEmployeeName," +
							"vDesignationId,vDesignationName,vEmployeeType,dJoiningDate,vUnitId,vUnitName,vDepartmentId,vDepartmentName,vSectionID,vSectionName," +
							"iTotalDay,iHoliday,iWorkingDay,iPresentDay,iAbsentDay,iLeaveDay,iLeaveWithoutPay,mBasic,mHouseRent,mMobileAllowance,mGrossSalary," +
							"mPerDaySalary,mSalaryTaka,iHolidayOTHr,iReplaceOTHr,iHolidayNetOTHr,iWorkingDayNetOTHr,mPerHrOTRate,mOtTaka,mTotalPayable," +
							"mIncomeTax,mNetPayableTaka,mRevenueStamp,vMoneyTransferType,vBranchId,vBranchName,vBankId,vBankName,vAccountNo,vRoutingNo," +
							"mOtherEarning,mOtherDeduction,'OLD'," +
							"vUserId,vUserName,vUserIP,dEntryTime from tbMonthlySalary " +
							"where vEmployeeID='"+lblAutoEmployeeID.get(i).getValue().toString()+"' " +
							"and vSalaryMonth='"+FMonthName.format(cmbSalaryMonth.getValue())+"' and vSalaryYear='"+FYear.format(cmbSalaryMonth.getValue())+"'";

					System.out.println("Update Monthly Salary UD :" +udquery);
					
					session.createSQLQuery(udquery).executeUpdate();
					session.clear();
					
					String query = "update tbMonthlySalary " +
							"set iHolidayNetOTHr='"+(lblHolidayNetOTHr.get(i).getValue().toString().trim().isEmpty()?0:lblHolidayNetOTHr.get(i).getValue().toString().trim())+"'," + 
							"iPresentDay='"+(txtWorkingDays.get(i).getValue().toString().trim().isEmpty()?0:txtWorkingDays.get(i).getValue().toString().trim())+"'," + 
							"mPerDaySalary='"+(lblPerDaySalary.get(i).getValue().toString().trim().isEmpty()?0:lblPerDaySalary.get(i).getValue().toString().trim())+"'," + 
							"mSalaryTaka='"+(lblSalaryTaka.get(i).getValue().toString().trim().isEmpty()?0:lblSalaryTaka.get(i).getValue().toString().trim())+"'," + 
							"iWorkingDayNetOTHr='"+(lblWorkingDayNetOTHr.get(i).getValue().toString().trim().isEmpty()?0:lblWorkingDayNetOTHr.get(i).getValue().toString().trim())+"'," + 
							"mOtTaka='"+(lblOTTaka.get(i).getValue().toString().trim().isEmpty()?0:lblOTTaka.get(i).getValue().toString().trim())+"'," + 
							"mOtherEarning='"+(txtOtherEarning.get(i).getValue().toString().trim().isEmpty()?0:txtOtherEarning.get(i).getValue().toString().trim())+"'," + 
							"mOtherDeduction='"+(txtOtherDeduction.get(i).getValue().toString().trim().isEmpty()?0:txtOtherDeduction.get(i).getValue().toString().trim())+"'," + 
							"mTotalPayable='"+(lblTotalPayable.get(i).getValue().toString().trim().isEmpty()?0:lblTotalPayable.get(i).getValue().toString().trim())+"'," + 
							"mIncomeTax='"+(lblIncomeTax.get(i).getValue().toString().trim().isEmpty()?0:lblIncomeTax.get(i).getValue().toString().trim())+"'," + 
							"mNetPayableTaka='"+(lblNetPayableTaka.get(i).getValue().toString().trim().isEmpty()?0:lblNetPayableTaka.get(i).getValue().toString().trim())+"'," + 
							"vUserName='"+sessionBean.getUserName()+"',vUserIP='"+sessionBean.getUserIp()+"',dEntryTime=GETDATE() " +
							"where vEmployeeID = '"+lblAutoEmployeeID.get(i).getValue().toString()+"' " +
							"and vSalaryMonth='"+FMonthName.format(cmbSalaryMonth.getValue())+"' and vSalaryYear='"+FYear.format(cmbSalaryMonth.getValue())+"'";

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
		allComp.add(cmbSalaryMonth);
		allComp.add(cmbUnit);
		allComp.add(cmbDepartment);
		allComp.add(cmbSection);
		for(int i=0;i<lblAutoEmployeeID.size();i++)
		{
			allComp.add(txtOtherEarning.get(i));
			allComp.add(txtOtherDeduction.get(i));
		}
		allComp.add(button.btnSave);
		new FocusMoveByEnter(this,allComp);
	}

	private void componentIni(boolean b) 
	{
		cmbSalaryMonth.setEnabled(!b);
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
			lblAutoEmployeeID.get(i).setValue("");
			lblEmployeeID.get(i).setValue("");
			lblEmployeeName.get(i).setValue("");
			lblDesignation.get(i).setValue("");
			lblBasic.get(i).setValue("");
			lblHouseAll.get(i).setValue("");

			lblMobileAll.get(i).setValue("");
			txtWorkingDays.get(i).setValue("");
			lblPerDaySalary.get(i).setValue("");
			lblSalaryTaka.get(i).setValue("");
			lblHolidayNetOTHr.get(i).setValue("");
			lblWorkingDayNetOTHr.get(i).setValue("");
			lblOTTaka.get(i).setValue("");
			
			txtOtherEarning.get(i).setValue("");
			txtOtherDeduction.get(i).setValue("");
			lblTotalPayable.get(i).setValue("");
			
			lblIncomeTax.get(i).setValue("");
			lblNetPayableTaka.get(i).setValue("");
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
				lblAutoEmployeeID.get(ar).setValue("");
				lblEmployeeID.get(ar).setValue("");
				lblEmployeeName.get(ar).setValue("");
				lblDesignation.get(ar).setValue("");
				
				lblBasic.get(ar).setValue("");
				lblHouseAll.get(ar).setValue("");
				lblMobileAll.get(ar).setValue("");
				txtWorkingDays.get(ar).setValue("");
				lblPerDaySalary.get(ar).setValue("");
				lblSalaryTaka.get(ar).setValue("");
				lblHolidayNetOTHr.get(ar).setValue("");
				lblWorkingDayNetOTHr.get(ar).setValue("");
				lblOTTaka.get(ar).setValue("");
				txtOtherEarning.get(ar).setValue("");
				txtOtherDeduction.get(ar).setValue("");
				lblTotalPayable.get(ar).setValue("");
				lblIncomeTax.get(ar).setValue("");
				lblNetPayableTaka.get(ar).setValue("");

				for(int rowcount=ar;rowcount<=lblAutoEmployeeID.size()-1;rowcount++)
				{
					if(rowcount+1<=lblAutoEmployeeID.size()-1)
					{
						if(!lblAutoEmployeeID.get(rowcount+1).getValue().toString().equals(""))
						{
							lblAutoEmployeeID.get(rowcount).setValue(lblAutoEmployeeID.get(rowcount+1).getValue().toString());
							lblEmployeeID.get(rowcount).setValue(lblEmployeeID.get(rowcount+1).getValue().toString());
							lblEmployeeName.get(rowcount).setValue(lblEmployeeName.get(rowcount+1).getValue().toString());
							lblDesignation.get(rowcount).setValue(lblDesignation.get(rowcount+1).getValue().toString());
							
							lblBasic.get(rowcount).setValue(lblBasic.get(rowcount+1).getValue().toString());
							lblHouseAll.get(rowcount).setValue(lblHouseAll.get(rowcount+1).getValue().toString());
							lblMobileAll.get(rowcount).setValue(lblMobileAll.get(rowcount+1).getValue().toString());
							txtWorkingDays.get(rowcount).setValue(txtWorkingDays.get(rowcount+1).getValue().toString());
							lblPerDaySalary.get(rowcount).setValue(lblPerDaySalary.get(rowcount+1).getValue().toString());
							lblSalaryTaka.get(rowcount).setValue(lblSalaryTaka.get(rowcount+1).getValue().toString());
							lblHolidayNetOTHr.get(rowcount).setValue(lblHolidayNetOTHr.get(rowcount+1).getValue().toString());
							lblWorkingDayNetOTHr.get(rowcount).setValue(lblWorkingDayNetOTHr.get(rowcount+1).getValue().toString());

							lblOTTaka.get(rowcount).setValue(lblOTTaka.get(rowcount+1).getValue().toString());
							txtOtherEarning.get(rowcount).setValue(txtOtherEarning.get(rowcount+1).getValue().toString());
							txtOtherDeduction.get(rowcount).setValue(txtOtherDeduction.get(rowcount+1).getValue().toString());
							lblTotalPayable.get(rowcount).setValue(lblTotalPayable.get(rowcount+1).getValue().toString());
							
							lblIncomeTax.get(rowcount).setValue(lblIncomeTax.get(rowcount+1).getValue().toString());
							lblNetPayableTaka.get(rowcount).setValue(lblNetPayableTaka.get(rowcount+1).getValue().toString());

							lblAutoEmployeeID.get(rowcount+1).setValue("");
							lblEmployeeID.get(rowcount+1).setValue("");
							lblEmployeeName.get(rowcount+1).setValue("");
							lblDesignation.get(rowcount+1).setValue("");
							
							lblBasic.get(rowcount+1).setValue("");
							lblHouseAll.get(rowcount+1).setValue("");
							lblMobileAll.get(rowcount+1).setValue("");
							txtWorkingDays.get(rowcount+1).setValue("");
							lblPerDaySalary.get(rowcount+1).setValue("");
							lblSalaryTaka.get(rowcount+1).setValue("");
							lblHolidayNetOTHr.get(rowcount+1).setValue("");
							lblWorkingDayNetOTHr.get(rowcount+1).setValue("");
							lblOTTaka.get(rowcount+1).setValue("");
							txtOtherEarning.get(rowcount+1).setValue("");
							txtOtherDeduction.get(rowcount+1).setValue("");
							lblTotalPayable.get(rowcount+1).setValue("");
							
							lblIncomeTax.get(rowcount+1).setValue("");
							lblNetPayableTaka.get(rowcount+1).setValue("");
						}
					}
				}

			}
		});

		lblsa.add(ar,new Label());
		lblsa.get(ar).setWidth("100%");
		lblsa.get(ar).setHeight("16px");
		lblsa.get(ar).setValue(ar+1);

		lblAutoEmployeeID.add(ar, new Label());
		lblAutoEmployeeID.get(ar).setWidth("100%");

		lblEmployeeID.add(ar, new Label());
		lblEmployeeID.get(ar).setWidth("100%");

		lblEmployeeName.add(ar,new Label());
		lblEmployeeName.get(ar).setWidth("100%");

		lblDesignation.add(ar, new Label());
		lblDesignation.get(ar).setWidth("100%");

		lblBasic.add(ar, new Label());
		lblBasic.get(ar).setWidth("100%");

		lblHouseAll.add(ar, new Label());
		lblHouseAll.get(ar).setWidth("100%");

		lblMobileAll.add(ar, new Label());
		lblMobileAll.get(ar).setWidth("100%");

		txtWorkingDays.add(ar, new TextField());
		txtWorkingDays.get(ar).setWidth("100%");
		txtWorkingDays.get(ar).setImmediate(true);
		txtWorkingDays.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtWorkingDays.get(ar).getValue().toString().trim().equals(""))
				{
					TotalCalculation(ar);
				}			
			}
		});
		
		lblPerDaySalary.add(ar, new Label());
		lblPerDaySalary.get(ar).setWidth("100%");

		lblSalaryTaka.add(ar, new Label());
		lblSalaryTaka.get(ar).setWidth("100%");

		lblHolidayNetOTHr.add(ar, new Label());
		lblHolidayNetOTHr.get(ar).setWidth("100%");

		lblWorkingDayNetOTHr.add(ar, new Label());
		lblWorkingDayNetOTHr.get(ar).setWidth("100%");

		lblOTTaka.add(ar, new Label());
		lblOTTaka.get(ar).setWidth("100%");

		txtOtherEarning.add(ar, new TextField());
		txtOtherEarning.get(ar).setWidth("100%");
		txtOtherEarning.get(ar).setImmediate(true);
		txtOtherEarning.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtOtherEarning.get(ar).getValue().toString().trim().equals(""))
				{
					TotalCalculation(ar);
				}			
			}
		});

		txtOtherDeduction.add(ar, new TextField());
		txtOtherDeduction.get(ar).setWidth("100%");
		txtOtherDeduction.get(ar).setImmediate(true);
		txtOtherDeduction.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtOtherDeduction.get(ar).getValue().toString().trim().equals(""))
				{
					TotalCalculation(ar);
				}			
			}
		});

		lblTotalPayable.add(ar, new Label());
		lblTotalPayable.get(ar).setWidth("100%");

		lblIncomeTax.add(ar, new Label());
		lblIncomeTax.get(ar).setWidth("100%");
		lblIncomeTax.get(ar).setImmediate(true);
		/*lblIncomeTax.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!lblIncomeTax.get(ar).getValue().toString().trim().equals(""))
				{
					TotalCalculation(ar);
				}			
			}
		});*/

		lblNetPayableTaka.add(ar, new Label());
		lblNetPayableTaka.get(ar).setWidth("100%");
		lblNetPayableTaka.get(ar).setImmediate(true);

		//TotalCalculation(ar);

		table.addItem(new Object[]{btnDel.get(ar),lblsa.get(ar),lblAutoEmployeeID.get(ar),lblEmployeeID.get(ar),lblEmployeeName.get(ar),lblDesignation.get(ar),
				lblBasic.get(ar),lblHouseAll.get(ar),lblMobileAll.get(ar),txtWorkingDays.get(ar),lblPerDaySalary.get(ar),lblSalaryTaka.get(ar),lblHolidayNetOTHr.get(ar),lblWorkingDayNetOTHr.get(ar),
				lblOTTaka.get(ar),txtOtherEarning.get(ar),txtOtherDeduction.get(ar),
				lblTotalPayable.get(ar),lblIncomeTax.get(ar),lblNetPayableTaka.get(ar)},ar);
	}

	private void TotalCalculation(int ar)
	{

		double mBasic = Double.parseDouble("0"+lblBasic.get(ar).getValue().toString().replaceAll(",", ""));
		

		double mWorkingDays = Double.parseDouble("0"+txtWorkingDays.get(ar).getValue().toString().replaceAll(",", ""));
		
		double mPerDaySalary=mBasic/26;
		
		
		double mSalaryTaka=0;
		mSalaryTaka=Math.round((mPerDaySalary*(mWorkingDays)));
		
		double mPerHrOTRate=mBasic/260;
		

		double mHolidayNetOTHr = Double.parseDouble("0"+lblHolidayNetOTHr.get(ar).getValue().toString().replaceAll(",", ""));
		double mWorkingDayNetOTHr = Double.parseDouble("0"+lblWorkingDayNetOTHr .get(ar).getValue().toString().replaceAll(",", ""));
		
		double mOtTaka=(mPerHrOTRate*((mHolidayNetOTHr*2)+(mWorkingDayNetOTHr*2)));
		
		
		
		


		lblPerDaySalary.get(ar).setValue(mPerDaySalary);
		lblSalaryTaka.get(ar).setValue(mSalaryTaka);
		lblOTTaka.get(ar).setValue(mOtTaka);
		
		double mOtherEarning = Double.parseDouble("0"+txtOtherEarning.get(ar).getValue().toString().replaceAll(",", ""));
		double mOtherDeduction = Double.parseDouble("0"+txtOtherDeduction.get(ar).getValue().toString().replaceAll(",", ""));

		double totalPayable =mSalaryTaka+mOtTaka+mOtherEarning-mOtherDeduction;	
		double netPayableTaka =0;
		double mIncomeTax =0;

		// Calculate IncomeTax Start
		if (totalPayable>=130000){
			mIncomeTax=3500;
		}
		else if (totalPayable>=120000){
			mIncomeTax=3000 ;
		}
		else if (totalPayable>=100000){
			mIncomeTax=2500;
		}
		else if (totalPayable>=90000){
			mIncomeTax=2000;
		}
		else if (totalPayable>=80000){
			mIncomeTax=1500;
		}
		else if (totalPayable>=70000){
			mIncomeTax=1000;
		}
		else if (totalPayable>=40000){
			mIncomeTax=417;
		}
		else if (totalPayable>=25000){
			mIncomeTax=250;
		}

		netPayableTaka =totalPayable-mIncomeTax;
		
		// Calculate IncomeTax End

		lblTotalPayable.get(ar).setValue(totalPayable);
		lblIncomeTax.get(ar).setValue(mIncomeTax);
		lblNetPayableTaka.get(ar).setValue(netPayableTaka);
		
		//System.out.println("TotalCalculation: "+(totalPayable-mIncomeTax));
	}


	private AbsoluteLayout buildMainLayout() 
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);
		mainLayout.setWidth("1200px");
		mainLayout.setHeight("550px");

		lblDate = new Label("Salary Month :");
		lblDate.setImmediate(false);
		lblDate.setWidth("-1px");
		lblDate.setHeight("-1px");
		mainLayout.addComponent(lblDate, "top:20.0px; left:40.0px;");
		

		cmbSalaryMonth = new ComboBox();
		cmbSalaryMonth.setImmediate(true);
		cmbSalaryMonth.setWidth("150px");
		cmbSalaryMonth.setHeight("-1px");
		mainLayout.addComponent(cmbSalaryMonth, "top:18.0px; left:150.0px;");

		
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

		table.setWidth("1160px");
		table.setHeight("340px");
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("Del", NativeButton.class, new NativeButton());
		table.setColumnWidth("Del", 30);

		table.addContainerProperty("SL", Label.class, new Label());
		table.setColumnWidth("SL", 20);

		table.addContainerProperty("System ID", Label.class, new Label());
		table.setColumnWidth("System ID", 60);

		table.addContainerProperty("EMPLOYEE ID", Label.class, new Label());
		table.setColumnWidth("EMPLOYEE ID", 60);

		table.addContainerProperty("Employee Name", Label.class, new Label());
		table.setColumnWidth("Employee Name",  160);

		table.addContainerProperty("Designation", Label.class, new Label());
		table.setColumnWidth("Designation", 150);

		table.addContainerProperty("Gross Salary", Label.class, new Label());
		table.setColumnWidth("Gross Salary", 70);

		table.addContainerProperty("HR", Label.class, new Label());
		table.setColumnWidth("HR", 70);
		
		table.addContainerProperty("Mobile", Label.class, new Label());
		table.setColumnWidth("Mobile", 70);

		table.addContainerProperty("Working Days", TextField.class, new TextField());
		table.setColumnWidth("Working Days", 50);
		
		table.addContainerProperty("lblPerDaySalary", Label.class, new Label());
		table.setColumnWidth("lblPerDaySalary", 70);
		
		table.addContainerProperty("Salary Taka", Label.class, new Label());
		table.setColumnWidth("Salary Taka", 70);

		table.addContainerProperty("HolidayNetOTHr", Label.class, new Label());
		table.setColumnWidth("HolidayNetOTHr", 65);
		
		table.addContainerProperty("WorkingDayNetOTHr", Label.class, new Label());
		table.setColumnWidth("WorkingDayNetOTHr", 65);
		
		table.addContainerProperty("O.T Taka", Label.class, new Label());
		table.setColumnWidth("O.T Taka", 65);

		table.addContainerProperty("Other Earning", TextField.class, new TextField());
		table.setColumnWidth("Other Earning", 55);

		table.addContainerProperty("Other Deduction", TextField.class, new TextField());
		table.setColumnWidth("Other Deduction", 55);

		table.addContainerProperty("Total Payable", Label.class, new Label());
		table.setColumnWidth("Total Payable", 65);

		table.addContainerProperty("Tax/AIT Deduct", Label.class, new Label());
		table.setColumnWidth("Tax/AIT Deduct", 55);

		table.addContainerProperty("Net Payable Taka", Label.class, new Label());
		table.setColumnWidth("Net Payable Taka", 70);

		/*table.setColumnAlignments(new String[] {Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT, Table.ALIGN_LEFT,
				Table.ALIGN_LEFT, Table.ALIGN_RIGHT, Table.ALIGN_RIGHT,	Table.ALIGN_RIGHT, Table.ALIGN_RIGHT,Table.ALIGN_RIGHT,Table.ALIGN_RIGHT,
				Table.ALIGN_RIGHT,Table.ALIGN_RIGHT,Table.ALIGN_RIGHT,Table.ALIGN_RIGHT, 
				Table.ALIGN_CENTER, Table.ALIGN_CENTER, Table.ALIGN_CENTER, Table.ALIGN_CENTER,Table.ALIGN_LEFT,Table.ALIGN_CENTER,Table.ALIGN_RIGHT});*/

		table.setColumnCollapsed("System ID", true);
		table.setColumnCollapsed("HR", true);
		table.setColumnCollapsed("Mobile", true);
		table.setColumnCollapsed("lblPerDaySalary", true);
		table.setColumnCollapsed("HolidayNetOTHr", true);
		table.setColumnCollapsed("WorkingDayNetOTHr", true);

		mainLayout.addComponent(table,"top:150px; left:20.0px;");		
		table.setStyleName("wordwrap-headers");
		
		mainLayout.addComponent(button,"bottom:15px; left:440.0px");
		return mainLayout;
	}
}