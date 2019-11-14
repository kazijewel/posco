package com.appform.hrmModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.CommonMethod;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.ReportDate;
import com.common.share.ReportViewer;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class ReplacementLeaveApplication extends Window
{
	private CommonButton cButton = new CommonButton( "New",  "Save",  "Edit",  "Delete",  "Refresh",  "Find", "", "Preview","","Exit");
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblApplicationDate;
	private PopupDateField dApplicationDate;

	private Label lblUnit;
	private ComboBox cmbUnit;

	private Label lblSection;
	private ComboBox cmbSection,cmbDepartment;

	private Label lblEmployeeId;
	private ComboBox cmbEmployee;

	/*private Label lblEmployeeId;
	private TextRead txtEmployeeId;*/

	private Label lblDesignation;
	private ComboBox cmbDesignationID;

	private Label lblTotalDays;
	private TextRead txtTotalDays;

	private Label lblLeaveType;
	private TextField txtLeaveType;

	private Label lblReplacementLeaveAddress;
	private TextField txtReplacementLeaveAddress;

	private Label lblMobileNo;
	private TextField txtMobileNo;

	private Label lblReplacementLeaveFrom;
	private PopupDateField dReplacementLeaveFrom;

	private Label lblReplacementLeaveTo;
	private PopupDateField dReplacementLeaveTo;

	private Label lblJoiningDate;
	private PopupDateField dJoiningDate;

	private ArrayList<Component> allComp = new ArrayList<Component>(); 
	private TextField txtTransactionID=new TextField();
	
	private CheckBox chkApproved;
	private CheckBox chkCancel;

	String sectionId = "";
	String designationId = "";
	String leaveType = "0";
	String totalDays = "";
	String CBalance = "";
	String SBalance = "";
	String ABalance = "";
	String MBalance = "";
	String CEnjoy = "";
	String SEnjoy = "";
	String AEnjoy = "";
	String MEnjoy = "";
	String findEmployeeId = "";
	String typeOfSearch="";
	String employee = "";
	String Notify = "";

	private boolean isFind=false;
	private boolean Update=false;
	private CommonMethod cm;
	private String menuId = "";
	private SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dbMonthFormat = new SimpleDateFormat("MM");
	private SimpleDateFormat dbYearFormat = new SimpleDateFormat("yyyy");
	public int count=0;
	boolean switchUser=false;


	private ReportDate reportTime = new ReportDate();
	
	public ReplacementLeaveApplication(SessionBean sessionBean,String menuId,boolean switchUser)
	{
		this.sessionBean=sessionBean;
		this.setCaption("REPLACEMENT LEAVE APPLICATION::"+sessionBean.getCompany());
		if(switchUser==false)
		{cButton = new CommonButton("New", "Save", "Edit", "Delete", "Refresh", "Find", "", "Preview", "", "Exit");}
		else{cButton = new CommonButton( "New",  "Save",  "",  "",  "Refresh",  "Find", "", "Preview","","Exit");}
		
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		buildMainLayout();
		setContent(mainLayout);
		btnIni(true);
		componentIni(true);
		setEventAction();
		cmbEmployeeValueAdd();
		cmbUnitValueAdd();
		cmbDepartmentValueAdd();
		cmbSectionValueAdd();
		cmbDesignationValueAdd();

		focusEnter();
		cButton.btnNew.focus();
		if(switchUser==false)
		{
			authenticationCheck();
		}
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
	
	public void deleteData()
	{
		String query = "select vEmployeeID from tbMonthlySalary " +
				"where vEmployeeID='"+cmbEmployee.getValue()+"' " +
				"and ((MONTH(dSalaryDate) = '"+dbMonthFormat.format(dReplacementLeaveFrom.getValue())+"' and YEAR(dSalaryDate) = '"+dbYearFormat.format(dReplacementLeaveFrom.getValue())+"') " +
				"or (MONTH(dSalaryDate) = '"+dbMonthFormat.format(dReplacementLeaveTo.getValue())+"' and YEAR(dSalaryDate) = '"+dbYearFormat.format(dReplacementLeaveTo.getValue())+"'))";
		System.out.println("Check Salary: "+query);
		
		if(!chkSalary(query))
		{
			MessageBox mb = new MessageBox(getParent(),"Are You Sure?",MessageBox.Icon.QUESTION,"Do You Want to delete All Information?",new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"),new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.setStyleName("cwindowMB");
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType==ButtonType.YES)
					{
						Session session=SessionFactoryUtil.getInstance().openSession();
						Transaction tx=session.beginTransaction();		
						try
						{
							String transactionID=txtTransactionID.getValue().toString();
							String del="delete from tbReplacementLeaveApplication where vTransactionId='"+transactionID+"' ";
							System.out.println(del);
							
							session.createSQLQuery(del).executeUpdate();
							componentIni(true);
							txtClear();
							btnIni(true);
							isFind=false;
							count=0;
							Notification n=new Notification("All Information Delete Successfully!","",Notification.TYPE_TRAY_NOTIFICATION);
							n.setPosition(Notification.POSITION_TOP_RIGHT);
							showNotification(n);
							
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
				}
			});
		}
		else
		{
			showNotification("Warning", "Salary Already Generated for this Month!!!", Notification.TYPE_WARNING_MESSAGE);
		}
		isFind = false;
		Update = false;
		
	}
	
	private void setEventAction() 
	{
		cmbEmployee.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbEmployee.getValue()!=null)
				{
					setEmployeeInfo();
				}
			}
		});
		cButton.btnNew.addListener(new ClickListener()
		{	
			public void buttonClick(ClickEvent event)
			{
				isFind = false;
				Update = false;
				chkApproved.setValue(true);
				txtClear();
				componentIni(false);
				btnIni(false);
				dApplicationDate.focus();
				count=0;
				txtTransactionID.setValue(transactionIDGenerate());
			}
		});

		cButton.btnSave.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				formValidation();
			}
		});
		cButton.btnDelete.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(cmbEmployee.getValue()!=null)
				{
					if(!txtTotalDays.getValue().toString().trim().isEmpty())
					{
						if(!txtLeaveType.getValue().toString().trim().isEmpty())
						{
							if(!txtReplacementLeaveAddress.getValue().toString().trim().isEmpty())
							{
								if(!txtMobileNo.getValue().toString().trim().isEmpty())
								{
									deleteData();
								}
								else
								{
									txtMobileNo.focus();
									Notify = "Provide Mobile Address!!!";
								}
							}
							else
							{
								txtReplacementLeaveAddress.focus();
								Notify = "Provide Contact Address!!!";
							}
						}
						else
						{
							txtLeaveType.focus();
							Notify = "Provide Leave of ReplacementLeave!!!";
						}
					}
					else
					{
						Notify = "Provide ReplacementLeave Days!!!";
					}
				}
				else
				{
					cmbEmployee.focus();
					Notify = "Please Select "+lblEmployeeId.getValue()+"!!!";
				}
			}
		});

		cButton.btnEdit.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				count=1;
				if(cmbEmployee.getValue()!=null)
				{
					Update = true;
					
					componentIni(false);
					btnIni(false);
				}
				else
					showNotification("Warning", "No Data Found!!!", Notification.TYPE_WARNING_MESSAGE);
			}
		});

		cButton.btnRefresh.addListener(new ClickListener()
		{	
			public void buttonClick(ClickEvent event)
			{
				isFind = false;
				Update = false;
				txtClear();
				componentIni(true);
				btnIni(true);
				count=0;
			}
		});

		cButton.btnFind.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				isFind = true;
				findbuttonEvent();
			}
		});

		cButton.btnExit.addListener(new ClickListener()
		{	
			public void buttonClick(ClickEvent event)
			{
				close();
			}
		});
		
		cButton.btnPreview.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(cmbEmployee.getValue()!=null)
				{
					reportPreview();
				}
				else
					showNotification("Select Employee!");
			}
		});
		
		chkApproved.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(chkApproved.booleanValue())
				{
					chkCancel.setValue(false);
				}
				else
				{
					chkCancel.setValue(true);
				}
			}
		});
		
		chkCancel.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(chkCancel.booleanValue())
				{
					chkApproved.setValue(false);
				}
				else
				{
					chkApproved.setValue(true);
				}
			}
		});
		dReplacementLeaveFrom.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
				if(dReplacementLeaveFrom.getValue()!=null)
				{
					setDays();
				}
			}
		});
	}
	
	private void reportPreview()
	{
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
			

			String query = " select vEmployeeID,vEmployeeCode,vEmployeeName, "
					+ " (select vDepartmentName from tbEmpOfficialPersonalInfo where vEmployeeID=rla.vEmployeeID)vDepartmentName, "
					+ "(select vSectionName from tbEmpOfficialPersonalInfo where vEmployeeID=rla.vEmployeeID)vSectionName, "
					+ "(select vDesignationName from tbEmpOfficialPersonalInfo where vEmployeeID=rla.vEmployeeID)vDesignationName, "
					+ "(select vUnitName from tbEmpOfficialPersonalInfo where vEmployeeID=rla.vEmployeeID)vUnitName, "
					+ "iTotalDays,vMobileNo,vVisitingAddress,vPurposeOfLeave,dApplicationDate,dReplacementLeaveFrom,dReplacementLeaveTo  "
					+ "from tbReplacementLeaveApplication rla where vTransactionId like '"+txtTransactionID.getValue()+"' ";

			System.out.println("report :"+query);



			if(queryValueCheck(query))
			{
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/account/hrmModule/rptReplacementLeaveApplicationFormPOSCO.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",true);

				
				this.getParent().getWindow().addWindow(win);
			}
			else
			{
				showNotification("Warning","There are no Data",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error "+exp,Notification.TYPE_ERROR_MESSAGE);
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
	
	public void setDays()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select DATEDIFF(DAY,'"+sessionBean.dfDb.format(dReplacementLeaveFrom.getValue())+"','"+sessionBean.dfDb.format(dReplacementLeaveFrom.getValue())+"')+1";

			List <?> list = session.createSQLQuery(query).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				txtTotalDays.setValue(iter.next()+"");
			}
		}
		catch(Exception exp)
		{
			showNotification("addemployeeName",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	public void cmbEmployeeValueAdd()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select distinct vEmployeeId,vEmployeeCode,vEmployeeName from tbEmpOfficialPersonalInfo where bStatus='1' order by vEmployeeCode";

			List <?> list = session.createSQLQuery(query).list();
			cmbEmployee.removeAllItems();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbEmployee.addItem(element[0]);
				cmbEmployee.setItemCaption(element[0], element[1]+"->"+element[2]);
			}
		}
		catch(Exception exp)
		{
			showNotification("addemployeeName",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void cmbUnitValueAdd()
	{
		cmbUnit.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vUnitId,vUnitName from tbEmpOfficialPersonalInfo order by vUnitName";
			List <?> lst=session.createSQLQuery(query).list();
			cmbUnit.removeAllItems();
			if(!lst.isEmpty())
			{
				for(Iterator <?> itr=lst.iterator();itr.hasNext();)
				{
					Object[] element=(Object[])itr.next();
					cmbUnit.addItem(element[0]);
					cmbUnit.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbUnitValueAdd", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void setEmployeeInfo()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select distinct a.vUnitId,a.vSectionId,a.vDesignationId,a.dJoiningDate,isnull(vContactNo,'')vContactNo,vDepartmentId " +
					"from tbEmpOfficialPersonalInfo a where a.vEmployeeId='"+cmbEmployee.getValue()+"'";
			System.out.println("setEmployeeInfo: "+query);
			
			List <?> lst = session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				for(Iterator <?> itr = lst.iterator();itr.hasNext();)
				{
					Object [] element = (Object[])itr.next();
					cmbUnit.setValue(element[0]);
					cmbSection.setValue(element[1]);
					cmbDesignationID.setValue(element[2]);
					dJoiningDate.setValue(element[3]);
					txtMobileNo.setValue(element[4]);
					cmbDepartment.setValue(element[5]);
				}
			}
		}
		catch (Exception exp)
		{
			showNotification("Warning",exp.toString(),Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}
	private void cmbDesignationValueAdd()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vDesignationId,vDesignation from tbDesignationInfo order by vDesignation";
			List <?> lst=session.createSQLQuery(query).list();
			cmbDesignationID.removeAllItems();
			if(!lst.isEmpty())
			{
				for(Iterator <?> itr=lst.iterator();itr.hasNext();)
				{
					Object[] element=(Object[])itr.next();
					cmbDesignationID.addItem(element[0]);
					cmbDesignationID.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbSectionValueAdd", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void cmbDepartmentValueAdd()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vDepartmentId,vDepartmentName from tbEmpOfficialPersonalInfo order by vDepartmentName";
			List <?> lst=session.createSQLQuery(query).list();
			cmbDepartment.removeAllItems();
			if(!lst.isEmpty())
			{
				for(Iterator <?> itr=lst.iterator();itr.hasNext();)
				{
					Object[] element=(Object[])itr.next();
					cmbDepartment.addItem(element[0]);
					cmbDepartment.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbDepartmentValueAdd", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void cmbSectionValueAdd()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vSectionId,vSectionName from tbEmpOfficialPersonalInfo order by vSectionName";
			List <?> lst=session.createSQLQuery(query).list();
			cmbSection.removeAllItems();
			if(!lst.isEmpty())
			{
				for(Iterator <?> itr=lst.iterator();itr.hasNext();)
				{
					Object[] element=(Object[])itr.next();
					cmbSection.addItem(element[0]);
					cmbSection.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbSectionValueAdd", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private boolean checkReplacement()
	{
		boolean ret=false;
		Session session=SessionFactoryUtil.getInstance().openSession();
		try{
			String sql="select vEmployeeId from tbReplacementLeaveApplication where vEmployeeId='"+cmbEmployee.getValue()+"' " +
					"and (" +
					"dReplacementLeaveFrom='"+dbDateFormat.format(dReplacementLeaveFrom.getValue())+"' or dReplacementLeaveTo='"+dbDateFormat.format(dReplacementLeaveTo.getValue())+"' or " +
					"dReplacementLeaveTo='"+dbDateFormat.format(dReplacementLeaveFrom.getValue())+"' or dReplacementLeaveFrom='"+dbDateFormat.format(dReplacementLeaveTo.getValue())+"' " +
					") ";
			List<?> list=session.createSQLQuery(sql).list();
			if(list.isEmpty())
			{
				ret=true;
			}
		}catch(Exception exp)
		{
			System.out.println("checkReplacement"+exp);
		}
		finally{
			session.close();
		}
		return ret;
	}
	private void formValidation()
	{
		Date fromDate=(Date)dReplacementLeaveFrom.getValue();
		Date toDate=(Date)dReplacementLeaveTo.getValue();
		Calendar fromCal = Calendar.getInstance();
		fromCal.setTime(fromDate);
		int fromMonth = fromCal.get(Calendar.MONTH);
		int fromDay=fromCal.get(Calendar.DATE);
		int fromYear=fromCal.get(Calendar.YEAR);
		
		Calendar toCal = Calendar.getInstance();
		toCal.setTime(toDate);
		int toMonth = toCal.get(Calendar.MONTH);
		int toDay=toCal.get(Calendar.DATE);
		int toYear=toCal.get(Calendar.YEAR);
		System.out.println("dayFrom :"+fromDay+" monthFrom:"+fromMonth+" yearFrom:"+fromYear);
		System.out.println("dayTo :"+toDay+" monthTo:"+toMonth+" yearTo:"+toYear);
		
		if(checkForm())
		{
			if((fromDate.compareTo(toDate)<0 && (fromMonth!=toMonth && fromYear==toYear))|| (fromMonth==toMonth && fromYear==toYear) || isFind)
			{
				if(!fromDate.equals(toDate) ||  isFind)
				{
					if(checkReplacement() || isFind)
					{
						saveButtonEvent();
					}
					else
					{
						showNotification("Warning", "Data already exist! Try new one!", Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					showNotification("Warning", "Replace To Working Day and Replace By Holiday Work is Same!", Notification.TYPE_WARNING_MESSAGE);
				}
			}
			else
			{
				showNotification("Warning", "Replace To Working Day"+"\n"+"Greater then Replace By Holiday Work", Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else
			showNotification("Warning", Notify, Notification.TYPE_WARNING_MESSAGE);
	}

	private boolean checkForm()
	{
		if(cmbEmployee.getValue()!=null)
		{
			if(!txtTotalDays.getValue().toString().trim().isEmpty())
			{
				if(!txtLeaveType.getValue().toString().trim().isEmpty())
				{
					if(!txtReplacementLeaveAddress.getValue().toString().trim().isEmpty())
					{
						if(!txtMobileNo.getValue().toString().trim().isEmpty())
						{
							return true;
						}
						else
						{
							txtMobileNo.focus();
							Notify = "Provide Mobile Address!!!";
						}
					}
					else
					{
						txtReplacementLeaveAddress.focus();
						Notify = "Provide Contact Address!!!";
					}
				}
				else
				{
					txtLeaveType.focus();
					Notify = "Provide Leave of ReplacementLeave!!!";
				}
			}
			else
			{
				Notify = "Provide ReplacementLeave Days!!!";
			}
		}
		else
		{
			cmbEmployee.focus();
			Notify = "Please Select "+lblEmployeeId.getValue()+"!!!";
		}
		return false;
	}

	private boolean chkSalary(String query)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> lst = session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				return true;
			}
		}
		catch (Exception exp)
		{
			showNotification("chkSalary", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
		return false;
	}

	private void saveButtonEvent()
	{
		String query = "select vEmployeeID from tbMonthlySalary " +
				"where vEmployeeID='"+cmbEmployee.getValue()+"' " +
				"and ((MONTH(dSalaryDate) = '"+dbMonthFormat.format(dReplacementLeaveFrom.getValue())+"' and YEAR(dSalaryDate) = '"+dbYearFormat.format(dReplacementLeaveFrom.getValue())+"') " +
				"or (MONTH(dSalaryDate) = '"+dbMonthFormat.format(dReplacementLeaveTo.getValue())+"' and YEAR(dSalaryDate) = '"+dbYearFormat.format(dReplacementLeaveTo.getValue())+"'))";
		System.out.println("Check Salary: "+query);
		
		if(!chkSalary(query))
		{
			MessageBox mb = new MessageBox(getParent(),"Are You Sure?",MessageBox.Icon.QUESTION,count==1?"Do You Want to Update All Information?":"Do You Want to save All Information?",new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"),new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.setStyleName("cwindowMB");
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType==ButtonType.YES)
					{
						Session session=SessionFactoryUtil.getInstance().openSession();
						Transaction tx=session.beginTransaction();
						
						if(count==1)
						{
							insertData(session,tx);
							//txtClear();
							componentIni(true);
							btnIni(true);
							Update=false;
							isFind=false;
							count=0;
							Notification n=new Notification("All Information Updated Successfully!","",Notification.TYPE_HUMANIZED_MESSAGE);
							n.setPosition(Notification.POSITION_TOP_RIGHT);
							showNotification(n);
							
						}
						else
						{
							insertData(session,tx);
							//txtClear();
							componentIni(true);
							btnIni(true);
							Update=false;
							isFind=false;
							count=0;
							Notification n=new Notification("All Information Save Successfully!","",Notification.TYPE_HUMANIZED_MESSAGE);
							n.setPosition(Notification.POSITION_TOP_RIGHT);
							showNotification(n);
						}
					}
				}
			});
		}
		else
		{
			showNotification("Warning", "Salary Already Generated for this Month!!!", Notification.TYPE_WARNING_MESSAGE);
		}
		isFind = false;
		Update = false;
	}

	private String transactionIDGenerate()
	{
		String transactionID = "TRA-";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select ISNULL(MAX(CAST(SUBSTRING(vTransactionID,5,LEN(vTransactionID)) as int)),0)+1 from tbReplacementLeaveApplication";
			List <?> lst = session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				transactionID += lst.iterator().next().toString();
			}
		}
		catch (Exception exp)
		{
			showNotification("transactionIDGenerate", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
		return transactionID;
	}

	private void insertData(Session session,Transaction tx)
	{
		try
		{
			String transactionID = transactionIDGenerate();
			
			if(count==1)
			{
				transactionID=txtTransactionID.getValue().toString();
				String del="delete from tbReplacementLeaveApplication where vTransactionId='"+transactionID+"' ";
				System.out.println(del);
				session.createSQLQuery(del).executeUpdate();
			}
			
			StringTokenizer strToken=new StringTokenizer(cmbEmployee.getItemCaption(cmbEmployee.getValue()), "->");
			String employeeCode=strToken.nextToken();
			String employeeName=strToken.nextToken(); 
			
			String approved = "1";
			if(chkCancel.booleanValue())
				approved = "0";
			
			String query = "insert into tbReplacementLeaveApplication (vTransactionID,dApplicationDate,vEmployeeID,vEmployeeCode,vEmployeeName," +
					"vDesignationID,vDesignationName,vDepartmentID,vDepartmentName,vSectionId,vSectionName,dJoiningDate,vMobileNo,dReplacementLeaveFrom," +
					"dReplacementLeaveTo,iTotalDays,vApprovedFlag,vPurposeOfLeave,vVisitingAddress,vUserName,vUserIP,dEntryTime) " +
					"values " +
					"('"+transactionID+"'," +
					"'"+dbDateFormat.format(dApplicationDate.getValue())+"'," +
					"'"+cmbEmployee.getValue()+"'," +
					"'"+employeeCode+"'," +
					"'"+employeeName+"'," +
					"'"+cmbDesignationID.getValue()+"'," +
					"'"+cmbDesignationID.getItemCaption(cmbDesignationID.getValue())+"'," +
					"'"+cmbUnit.getValue()+"'," +
					"'"+cmbUnit.getItemCaption(cmbUnit.getValue())+"'," +
					"'"+cmbSection.getValue()+"'," +
					"'"+cmbSection.getItemCaption(cmbSection.getValue())+"'," +
					"'"+dbDateFormat.format(dJoiningDate.getValue())+"'," +
					"'"+txtMobileNo.getValue().toString()+"'," +
					"'"+dbDateFormat.format(dReplacementLeaveFrom.getValue())+"'," +
					"'"+dbDateFormat.format(dReplacementLeaveTo.getValue())+"'," +
					"'"+txtTotalDays.getValue().toString().trim()+"'," +
					"'"+approved+"'," +
					"'"+txtLeaveType.getValue().toString().trim()+"'," +
					"'"+txtReplacementLeaveAddress.getValue().toString().replaceAll("'","#")+"'," +
					"'"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',GETDATE())";
			session.createSQLQuery(query).executeUpdate();
			
			
			
			tx.commit();
		}
		catch (Exception exp)
		{
			tx.rollback();
			showNotification("insertData", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally
		{
			session.close();
		}
	}

	private void findbuttonEvent()
	{
		Window win = new ReplacementLeaveFind(sessionBean, txtTransactionID);
		win.addListener(new CloseListener()
		{
			public void windowClose(CloseEvent e)
			{
				if(!txtTransactionID.getValue().toString().trim().isEmpty())
				{
					txtClear();
					findInitialize(txtTransactionID.getValue().toString());
				}
			}
		});
		this.getParent().addWindow(win);
	}

	private void findInitialize(String TransID)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select dApplicationDate,(select vUnitId from tbEmpOfficialPersonalInfo where vEmployeeId=la.vEmployeeId)vUnitId,"
					+ "(select vDepartmentId from tbEmpOfficialPersonalInfo where vEmployeeId=la.vEmployeeId)vSectionId,vEmployeeID,dReplacementLeaveFrom," +
					"dReplacementLeaveTo,vPurposeOfLeave,vVisitingAddress,vMobileNo,vApprovedFlag,vTransactionId, "
					+ "(select vDepartmentId from tbEmpOfficialPersonalInfo where vEmployeeId=la.vEmployeeId)vDepartmentId," +
					"(select vDesignationId from tbEmpOfficialPersonalInfo where vEmployeeId=la.vEmployeeId)vDesignationId  " +
					"from tbReplacementLeaveApplication la where vTransactionID = '"+TransID+"'";
			System.out.println("findInitialize: "+query);
			
			List <?> lst = session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				for(Iterator <?> itr = lst.iterator();itr.hasNext();)
				{
					Object [] element = (Object [])itr.next();
					dApplicationDate.setValue(element[0]);
					cmbUnit.setValue(element[1]);
					cmbDepartment.setValue(element[11]);
					cmbSection.setValue(element[2]);
					cmbDesignationID.setValue(element[12]);
					cmbEmployee.setValue(element[3]);
					dReplacementLeaveFrom.setValue(element[4]);
					dReplacementLeaveTo.setValue(element[5]);
					txtLeaveType.setValue(element[6]);
					txtReplacementLeaveAddress.setValue(element[7].toString().replaceAll("#","'"));
					txtMobileNo.setValue(element[8]);
					if(element[9].toString().equals("1"))
						chkApproved.setValue(true);
					else
						chkCancel.setValue(true);
					txtTransactionID.setValue(element[10].toString());
				}
			}
		}
		catch (Exception exp)
		{
			showNotification("findInitialize", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
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
	}

	private void componentIni(boolean t)
	{
		dApplicationDate.setEnabled(!t);
		cmbEmployee.setEnabled(!t);
		txtMobileNo.setEnabled(!t);
		dReplacementLeaveFrom.setEnabled(!t);
		dReplacementLeaveTo.setEnabled(!t);
		txtTotalDays.setEnabled(!t);
		txtLeaveType.setEnabled(!t);
		txtReplacementLeaveAddress.setEnabled(!t);
		chkApproved.setEnabled(!t);
		chkCancel.setEnabled(!t);
	}


	private void txtClear()
	{
		dApplicationDate.setValue(new Date());
		cmbEmployee.setValue(null);
		cmbUnit.setValue(null);
		cmbDepartment.setValue(null);
		cmbSection.setValue(null);
		cmbDesignationID.setValue("");
		dJoiningDate.setValue(new Date());
		cmbDesignationID.setValue(null);
		
		dReplacementLeaveTo.setValue(null);
		dReplacementLeaveFrom.setValue(null);
		txtLeaveType.setValue("");
		txtReplacementLeaveAddress.setValue("");
		txtMobileNo.setValue("");
		txtTotalDays.setValue("");
	}

	private void focusEnter()
	{
		allComp.add(dApplicationDate);
		allComp.add(cmbEmployee);
		allComp.add(txtLeaveType);
		allComp.add(dReplacementLeaveFrom);
		allComp.add(dReplacementLeaveTo);
		allComp.add(txtMobileNo);
		allComp.add(txtReplacementLeaveAddress);
		allComp.add(cButton.btnSave);
		new FocusMoveByEnter(this,allComp);
	}

	public AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("780px");
		mainLayout.setHeight("310px");
		mainLayout.setMargin(false);

		// lblApplicationDate
		lblApplicationDate = new Label("Application Date : ");
		lblApplicationDate.setImmediate(false);
		lblApplicationDate.setWidth("-1px");
		lblApplicationDate.setHeight("-1px");
		mainLayout.addComponent(lblApplicationDate, "top:10.0px; left:30.0px;");

		// dApplicationDate
		dApplicationDate = new PopupDateField();
		dApplicationDate.setImmediate(false);
		dApplicationDate.setWidth("110px");
		dApplicationDate.setHeight("-1px");
		dApplicationDate.setValue(new Date());
		dApplicationDate.setDateFormat("dd-MM-yyyy");
		dApplicationDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dApplicationDate, "top:08.0px; left:140.0px;");
		
		// lblEmployeeId
		lblEmployeeId = new Label("Employee ID :");
		lblEmployeeId.setImmediate(false);
		lblEmployeeId.setWidth("-1px");
		lblEmployeeId.setHeight("-1px");
		mainLayout.addComponent(lblEmployeeId, "top:40.0px; left:30.0px;");

		// cmbEmployee
		cmbEmployee = new ComboBox();
		cmbEmployee.setImmediate(true);
		cmbEmployee.setWidth("210px");
		cmbEmployee.setHeight("-1px");
		cmbEmployee.setNullSelectionAllowed(false);
		cmbEmployee.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbEmployee, "top:38.0px; left:140.0px;");

		// lblSection
		lblUnit = new Label("Project :");
		lblUnit.setImmediate(false);
		lblUnit.setWidth("-1px");
		lblUnit.setHeight("-1px");
		mainLayout.addComponent(lblUnit, "top:70.0px; left:30.0px;");

		// cmbSection
		cmbUnit = new ComboBox();
		cmbUnit.setImmediate(true);
		cmbUnit.setWidth("210px");
		cmbUnit.setHeight("-1px");
		cmbUnit.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbUnit, "top:68.0px; left:140.0px;");
		cmbUnit.setEnabled(false);
		
		// cmbSection
		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("210px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Department :"), "top:100.0px; left:30.0px;");
		mainLayout.addComponent(cmbDepartment, "top:98.0px; left:140.0px;");
		cmbDepartment.setEnabled(false);

		// lblSection
		lblSection = new Label("Section :");
		lblSection.setImmediate(false);
		lblSection.setWidth("-1px");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection, "top:130px; left:30.0px;");

		// cmbSection
		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("210px");
		cmbSection.setHeight("-1px");
		cmbSection.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbSection, "top:128px; left:140.0px;");
		cmbSection.setEnabled(false);

		// lblDesignation
		lblDesignation = new Label("Designation :");
		lblDesignation.setImmediate(false);
		lblDesignation.setWidth("210px");
		lblDesignation.setHeight("-1px");
		mainLayout.addComponent(lblDesignation, "top:160px; left:30.0px;");

		// cmbDesignationID
		cmbDesignationID = new ComboBox();
		cmbDesignationID.setImmediate(true);
		cmbDesignationID.setWidth("210px");
		cmbDesignationID.setHeight("22px");
		mainLayout.addComponent(cmbDesignationID, "top:158px; left:141.0px;");
		cmbDesignationID.setEnabled(false);
		
	//	mainLayout.addComponent(txtTransactionID,"top:190px; left:20px");

		// lblJoiningDate
		lblJoiningDate = new Label("Joining Date : ");
		lblJoiningDate.setImmediate(false);
		lblJoiningDate.setWidth("-1px");
		lblJoiningDate.setHeight("-1px");
		//mainLayout.addComponent(lblJoiningDate, "top:190px; left:30.0px;");

		// dJoiningDate
		dJoiningDate = new PopupDateField();
		dJoiningDate.setImmediate(true);
		dJoiningDate.setWidth("110px");
		dJoiningDate.setHeight("-1px");
		dJoiningDate.setValue(new Date());
		dJoiningDate.setDateFormat("dd-MM-yyyy");
		dJoiningDate.setEnabled(false);
		dJoiningDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dJoiningDate, "top:188px; left:140.0px;");
		dJoiningDate.setVisible(false);

		/*// lblMobileNo
		lblMobileNo = new Label("Mobile No :");
		lblMobileNo.setImmediate(false);
		lblMobileNo.setWidth("210px");
		lblMobileNo.setHeight("-1px");
		mainLayout.addComponent(lblMobileNo, "top:220px; left:30.0px;");

		// txtMobileNo
		txtMobileNo = new TextField();
		txtMobileNo.setImmediate(true);
		txtMobileNo.setWidth("150px");
		txtMobileNo.setHeight("-1px");
		mainLayout.addComponent(txtMobileNo, "top:218px; left:140.0px;");*/
		
		lblLeaveType = new Label("Leave Type :");
		lblLeaveType.setImmediate(true);
		lblLeaveType.setWidth("-1px");
		lblLeaveType.setHeight("-1px");
		mainLayout.addComponent(lblLeaveType, "top:10px; left:430.0px;");

		// txtLeaveType
		txtLeaveType = new TextField();
		txtLeaveType.setImmediate(true);
		txtLeaveType.setWidth("220px");
		mainLayout.addComponent(txtLeaveType, "top:08px; left:530.0px;");

		// lblReplacementLeaveFrom
		lblReplacementLeaveFrom = new Label("Replace by Holiday Work :");
		lblReplacementLeaveFrom.setImmediate(false);
		lblReplacementLeaveFrom.setWidth("-1px");
		lblReplacementLeaveFrom.setHeight("-1px");
		mainLayout.addComponent(lblReplacementLeaveFrom, "top:40px; left:390px;");

		// dReplacementLeaveFrom
		dReplacementLeaveFrom = new PopupDateField();
		dReplacementLeaveFrom.setImmediate(true);
		dReplacementLeaveFrom.setWidth("110px");
		dReplacementLeaveFrom.setHeight("-1px");
		dReplacementLeaveFrom.setValue(new Date());
		dReplacementLeaveFrom.setDateFormat("dd-MM-yyyy");
		dReplacementLeaveFrom.setValue(new java.util.Date());
		dReplacementLeaveFrom.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dReplacementLeaveFrom, "top:38px; left:530.0px;");

		// lblReplacementLeaveTo
		lblReplacementLeaveTo = new Label("Replace To Working Day :");
		lblReplacementLeaveTo.setImmediate(false);
		lblReplacementLeaveTo.setWidth("-1px");
		lblReplacementLeaveTo.setHeight("-1px");
		mainLayout.addComponent(lblReplacementLeaveTo, "top:70px; left:390px;");

		// dReplacementLeaveTo
		dReplacementLeaveTo = new PopupDateField();
		dReplacementLeaveTo.setImmediate(true);
		dReplacementLeaveTo.setWidth("110px");
		dReplacementLeaveTo.setHeight("-1px");
		dReplacementLeaveTo.setDateFormat("dd-MM-yyyy");
		dReplacementLeaveTo.setValue(new java.util.Date());
		dReplacementLeaveTo.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dReplacementLeaveTo, "top:68px; left:530.0px;");

		lblTotalDays = new Label("Total Days:");
		lblTotalDays.setImmediate(false);
		lblTotalDays.setWidth("-1px");
		lblTotalDays.setHeight("-1px");
		mainLayout.addComponent(lblTotalDays, "top:100px; left:430.0px;");

		txtTotalDays = new TextRead();
		txtTotalDays.setImmediate(true);
		txtTotalDays.setWidth("60px");
		txtTotalDays.setHeight("22px");
		mainLayout.addComponent(txtTotalDays, "top:98px; left:530.0px;");
		
		// lblMobileNo
		lblMobileNo = new Label("Mobile No :");
		lblMobileNo.setImmediate(false);
		lblMobileNo.setWidth("210px");
		lblMobileNo.setHeight("-1px");
		mainLayout.addComponent(lblMobileNo, "top:130px; left:430px;");

		// txtMobileNo
		txtMobileNo = new TextField();
		txtMobileNo.setImmediate(true);
		txtMobileNo.setWidth("150px");
		txtMobileNo.setHeight("-1px");
		mainLayout.addComponent(txtMobileNo, "top:128px; left:530px;");
		txtMobileNo.setMaxLength(11);
		

		/*// lblLeaveType
		lblLeaveType = new Label("Purpose :");
		lblLeaveType.setImmediate(true);
		lblLeaveType.setWidth("-1px");
		lblLeaveType.setHeight("-1px");
		mainLayout.addComponent(lblLeaveType, "top:100.0px; left:430.0px;");

		// txtLeaveType
		txtLeaveType = new TextField();
		txtLeaveType.setImmediate(true);
		txtLeaveType.setWidth("220px");
		txtLeaveType.setHeight("60px");
		mainLayout.addComponent(txtLeaveType, "top:98.0px; left:530.0px;");*/

		// lblReplacementLeaveAddress
		lblReplacementLeaveAddress = new Label("Contact address :");
		lblReplacementLeaveAddress.setImmediate(false);
		lblReplacementLeaveAddress.setWidth("-1px");
		lblReplacementLeaveAddress.setHeight("-1px");
		mainLayout.addComponent(lblReplacementLeaveAddress, "top:160px; left:430.0px;");

		// txtReplacementLeaveAddress
		txtReplacementLeaveAddress = new TextField();
		txtReplacementLeaveAddress.setImmediate(true);
		txtReplacementLeaveAddress.setWidth("220px");
		txtReplacementLeaveAddress.setHeight("60px");
		mainLayout.addComponent(txtReplacementLeaveAddress, "top:158px; left:530.0px;");
		
		chkApproved = new CheckBox("Approved");
		chkApproved.setImmediate(true);
		chkApproved.setValue(true);
		mainLayout.addComponent(chkApproved, "top:220px; left:530px;");
		
		chkCancel = new CheckBox("Cancel");
		chkCancel.setImmediate(true);
		mainLayout.addComponent(chkCancel, "top:220px; left:630px;");

		mainLayout.addComponent(cButton, "top:270.0px; left:55.0px;");
		return mainLayout;
	}
}
