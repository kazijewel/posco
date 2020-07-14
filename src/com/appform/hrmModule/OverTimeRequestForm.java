package com.appform.hrmModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.CommonMethod;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TimeField;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class OverTimeRequestForm extends Window
{
	private CommonButton cButton = new CommonButton( "New",  "Save",  "Edit",  "Delete",  "Refresh",  "Find", "", "Preview","","Exit");
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblRequestDate;
	private PopupDateField dRequestDate;
	
	private PopupDateField dDateFrom,dDateTo;

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

	private ArrayList<Component> allComp = new ArrayList<Component>(); 
	private TextField txtTransactionID=new TextField();
	

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
	private OptionGroup opgOverTime;
	private ComboBox cmbJobSite,cmbManager;
	private OptionGroup opgNationality;
	private static final List<String> overTime = Arrays.asList(new String[]{"Holiday","Night Time"});
	private static final List<String> nationality = Arrays.asList(new String[]{"Bangladeshi","Korean"});
	
	private TextField txtOverTimeRequest;
	private boolean isFind=false;
	private boolean isUpdate=false;
	private CommonMethod cm;
	private String menuId = "";
	private SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dbMonthFormat = new SimpleDateFormat("MM");
	private SimpleDateFormat dbYearFormat = new SimpleDateFormat("yyyy");
    SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss aa");
    private TimeField tHrFrom=new TimeField();
	private TimeField tMinFrom=new TimeField();
	private TextField txtFrom=new TextField();
	private TimeField tHrTo=new TimeField();
	private TimeField tMinTo=new TimeField();
	private TextField txtTo=new TextField();
	
	private TextField txtTimeTotal=new TextField();
	
	boolean switchUser=false;
	
	public OverTimeRequestForm(SessionBean sessionBean,String menuId,boolean switchUser)
	{
		this.sessionBean=sessionBean;
		this.setCaption("OVER TIME REQUEST FORM::"+sessionBean.getCompany());
		this.setResizable(false);
		if(switchUser==false)
		{cButton = new CommonButton("New", "Save", "Edit", "Delete", "Refresh", "Find", "", "Preview", "", "Exit");}
		else{cButton = new CommonButton( "New",  "Save",  "",  "",  "Refresh",  "Find", "", "Preview","","Exit");}
		
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
		cmManagerDataLoad();
		cmbJobSiteAdd();
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
	private void setEventAction() 
	{
		dRequestDate.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) 
			{
				if(dRequestDate.getValue()!=null)
				{
					dDateFrom.setValue(dRequestDate.getValue());
					dDateTo.setValue(dRequestDate.getValue());
				}
			}
		});

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
		tHrFrom.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!tHrFrom.getValue().toString().isEmpty())	
				{
					timeValidation();
					if(Integer.parseInt(tHrFrom.getValue().toString())>12)
					{
						tHrFrom.setValue("");
						showNotification("Warning", "Provide From Hour!!!",Notification.TYPE_WARNING_MESSAGE);
					}
				}				
			}
		});

		tMinFrom.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!tMinFrom.getValue().toString().isEmpty())
				{
					timeValidation();
					if(Integer.parseInt(tMinFrom.getValue().toString())>59)
					{
						tMinFrom.setValue("");
						showNotification("Warning", "Provide From Min.!!!",Notification.TYPE_WARNING_MESSAGE);
					}
				}
			}
		});

		txtFrom.addListener(new TextChangeListener()
		{
			public void textChange(TextChangeEvent event)
			{

				if(event.getText().equalsIgnoreCase("a"))
				{
					txtFrom.setValue("AM");
					timeValidation();
				}

				if(event.getText().equalsIgnoreCase("p"))
				{
					txtFrom.setValue("PM");
					timeValidation();
				}
			}
		});

		tHrTo.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!tHrTo.getValue().toString().isEmpty())	
				{
					timeValidation();
					if(Integer.parseInt(tHrTo.getValue().toString())>24)
					{
						tHrTo.setValue("");
						showNotification("Warning", "Provide To Hour!!!",Notification.TYPE_WARNING_MESSAGE);
					}
				}				
			}
		});

		tMinTo.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!tMinTo.getValue().toString().isEmpty())
				{
					timeValidation();
					if(Integer.parseInt(tMinTo.getValue().toString())>59)
					{
						tMinTo.setValue("");
						showNotification("Warning", "Provide To Min.!!!",Notification.TYPE_WARNING_MESSAGE);
					}
				}
			}
		});

		txtTo.addListener(new TextChangeListener()
		{
			public void textChange(TextChangeEvent event)
			{
				if(event.getText().equalsIgnoreCase("a"))
				{
					txtTo.setValue("AM");
					timeValidation();
				}

				if(event.getText().equalsIgnoreCase("p"))
				{
					txtTo.setValue("PM");
					timeValidation();
				}
			}
		});
		
		
		

		dDateFrom.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(dDateFrom.getValue()!=null)	
				{
					timeValidation();
				}				
			}
		});
		dDateTo.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(dDateTo.getValue()!=null)	
				{
					timeValidation();
				}				
			}
		});

		cButton.btnNew.addListener(new ClickListener()
		{	
			public void buttonClick(ClickEvent event)
			{
				isFind = false;
				isUpdate = false;
				
				txtClear();
				componentIni(false);
				btnIni(false);
				dRequestDate.focus();
			}
		});

		cButton.btnSave.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				checkForm();
				cButton.btnEdit.setEnabled(false);
				cButton.btnDelete.setEnabled(false);
			}
		});

		cButton.btnEdit.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(cmbEmployee.getValue()!=null)
				{
					isUpdate = true;
					
					componentIni(false);
					btnIni(false);
				}
				else
					showNotification("Warning", "No Data Found!!!", Notification.TYPE_WARNING_MESSAGE);
			}
		});
		
		cButton.btnDelete.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(cmbEmployee.getValue()!=null)
				{
					if(cmbJobSite.getValue()!=null)
					{
						if(cmbManager.getValue()!=null)
						{
							if(dDateFrom.getValue()!=null)
							{
								if(dDateTo.getValue()!=null)
								{
									if(!txtTimeTotal.getValue().toString().isEmpty())
									{
										deleteData();
									}
								}
							}
						}
						else
						{
							showNotification("Warning", "Please select Manager!", Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else
					{
						showNotification("Warning", "Please select Job Site!", Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					cmbEmployee.focus();
					showNotification("Warning", "Please select Employee!", Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		cButton.btnRefresh.addListener(new ClickListener()
		{	
			public void buttonClick(ClickEvent event)
			{
				isFind = false;
				isUpdate = false;
				txtClear();
				componentIni(true);
				btnIni(true);
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
		cButton.btnExit.addListener(new ClickListener()
		{	
			public void buttonClick(ClickEvent event)
			{
				close();
			}
		});
	}
	public void deleteData()
	{
		String query = "select top(5) * from tbMonthlySalary " +
				"where vEmployeeID='"+cmbEmployee.getValue()+"' " +
				"and ((MONTH(dSalaryDate) = '"+dbMonthFormat.format(dRequestDate.getValue())+"' and YEAR(dSalaryDate) = '"+dbYearFormat.format(dRequestDate.getValue())+"') " +
				"or (MONTH(dSalaryDate) = '"+dbMonthFormat.format(dRequestDate.getValue())+"' and YEAR(dSalaryDate) = '"+dbYearFormat.format(dRequestDate.getValue())+"'))";
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
							
							String oldData = "insert into tbUDOTRequest(vTransactionId,vEmployeeId,vEmployeeName,vDesignationId,vDesignationName,vDepartmentId,"
									+ "vDepartmentName,vJobSite,dRequestDate,dTimeFrom,dTimeTo,mTotalTimeHR,vManger,vWorkRequest,vManPower,iHoliday,iNightTim,"
									+ "vUserId,vUserName,vUserIp,dEntryTime,dDateFrom,dDateTo,iFinal,vUdFlag,vApprovedBy) "
									+ "select vTransactionId,vEmployeeId,vEmployeeName,vDesignationId,vDesignationName,vDepartmentId,"
									+ "vDepartmentName,vJobSite,dRequestDate,dTimeFrom,dTimeTo,mTotalTimeHR,vManger,vWorkRequest,vManPower,iHoliday,iNightTim,"
									+ "vUserId,vUserName,vUserIp,dEntryTime,dDateFrom,dDateTo,iFinal,'UPDATE',vApprovedBy "
									+ "from tbOTRequest where vTransactionId ='"+transactionID+"' ";
							
							System.out.println("deleteData: "+oldData);
							session.createSQLQuery(oldData).executeUpdate();
							
							String deleteData = "insert into tbUDOTRequest(vTransactionId,vEmployeeId,vEmployeeName,vDesignationId,vDesignationName,vDepartmentId,"
									+ "vDepartmentName,vJobSite,dRequestDate,dTimeFrom,dTimeTo,mTotalTimeHR,vManger,vWorkRequest,vManPower,iHoliday,iNightTim,"
									+ "vUserId,vUserName,vUserIp,dEntryTime,dDateFrom,dDateTo,iFinal,vUdFlag,vApprovedBy) "
									+ "select vTransactionId,vEmployeeId,vEmployeeName,vDesignationId,vDesignationName,vDepartmentId,"
									+ "vDepartmentName,vJobSite,dRequestDate,dTimeFrom,dTimeTo,mTotalTimeHR,vManger,vWorkRequest,vManPower,iHoliday,iNightTim,"
									+ "'"+sessionBean.getUserId()+"','"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP,"
									+ "dDateFrom,dDateTo,iFinal,'DELETE',vApprovedBy "
									+ "from tbOTRequest where vTransactionId ='"+transactionID+"' ";
							
							System.out.println("deleteData: "+deleteData);
							session.createSQLQuery(deleteData).executeUpdate();
							
							String del="delete from tbOTRequest where vTransactionId='"+transactionID+"' ";
							System.out.println(del);
							
							session.createSQLQuery(del).executeUpdate();
							componentIni(true);
							txtClear();
							btnIni(true);
							isFind=false;
							isUpdate=false;
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
		
	}
	private void reportPreview()
	{
		try
		{
			HashMap <String,Object>  hm = new HashMap <String,Object> ();
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone",sessionBean.getCompanyContact());
			hm.put("userName", sessionBean.getUserName()+" "+sessionBean.getUserIp());
			hm.put("SysDate",new java.util.Date());
			hm.put("developer", sessionBean.getDeveloperAddress());
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("empList", "Employee's List ( "+cmbJobSite.getItemCaption(cmbJobSite.getValue())+" )");

			String query="select *,case when iHoliday=1 and mTotalTimeHR>=10 " +
			" then mTotalTimeHR-1 else mTotalTimeHR end hours, "+
			" (select vEmployeeCode from tbEmpOfficialPersonalInfo where vEmployeeId=ot.vEmployeeId)vEmployeeCode "+
			" from tbOTRequest ot "
			+ " where vEmployeeId='"+cmbEmployee.getValue()+"' "
			+ " and dRequestDate='"+sessionBean.dfDb.format(dRequestDate.getValue())+"' "
			+ " and vTransactionId like '"+txtTransactionID.getValue()+"' ";

			System.out.println("report :"+query);



			if(queryValueCheck(query))
			{
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/account/hrmModule/rptOverTimeRequestForm.jasper",
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
	public void timeValidation()
	{
		if(dDateFrom.getValue()!=null)
		{
			if(tHrFrom.getValue().toString().trim().length()>0)
			{
				if(tMinFrom.getValue().toString().trim().length()>0)
				{
					if(txtFrom.getValue().toString().trim().equals("AM") || txtFrom.getValue().toString().trim().equals("PM"))
					{
						if(dDateTo.getValue()!=null)
						{
							if(tHrTo.getValue().toString().trim().length()>0)
							{
								if(tMinTo.getValue().toString().trim().length()>0)
								{	
									if(txtTo.getValue().toString().trim().equals("AM") || txtTo.getValue().toString().trim().equals("PM"))
									{
										setTime();
									}
								}
							}
						}
					}
				}
			}
		}
	}
	private void setTime()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		try{
			
			String inhour=tHrFrom.getValue().toString().isEmpty()?"00":tHrFrom.getValue().toString().trim();
			String inMin=tMinFrom.getValue().toString().isEmpty()?"00":tMinFrom.getValue().toString().trim();
			String inTime="";
			String inTF=txtFrom.getValue().toString();
			inTime=inhour+":"+inMin+":00 "+inTF;
			
			String outhour=tHrTo.getValue().toString().isEmpty()?"00":tHrTo.getValue().toString().trim();
			String OutMin=tMinTo.getValue().toString().isEmpty()?"00":tMinTo.getValue().toString().trim();
			String outTime="";
			String outTF=txtTo.getValue().toString();
			outTime=outhour+":"+OutMin+":00 "+outTF;
			
			//System.out.println("inTime: "+inTime+" outTime: "+outTime+" Date: "+sessionBean.dfDb.format(dRequestDate.getValue()));
			
			String sql="select datediff"
					+ "(HH,"
					+ "'"+sessionBean.dfDb.format(dDateFrom.getValue())+" "+inTime+"',"
					+ "'"+sessionBean.dfDb.format(dDateTo.getValue())+" "+outTime+"'"
					+ ")";
			
			//String sql="select dbo.funTimeDurationUpdate('"+inTime+"','"+outTime+"','"+sessionBean.dfDb.format(dRequestDate.getValue())+"')";
			System.out.println(sql);
			
			Iterator<?> iter=session.createSQLQuery(sql).list().iterator();
			if(iter.hasNext())
			{
				txtTimeTotal.setValue(iter.next());
				
			}
		}catch(Exception exp)
		{
			System.out.println(""+exp);
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
			System.out.println(query);
			List <?> list = session.createSQLQuery(query).list();
			cmbEmployee.removeAllItems();
			cmbManager.removeAllItems();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbEmployee.addItem(element[0]);
				cmbEmployee.setItemCaption(element[0], element[1]+"->"+element[2]);
				cmbManager.addItem(element[2].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("addemployeeName",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	public void cmManagerDataLoad()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select distinct vEmployeeName from tbEmpOfficialPersonalInfo where vEmployeeCode like '%PK%' order by vEmployeeName";
			System.out.println(query);
			List <?> list = session.createSQLQuery(query).list();

			cmbManager.removeAllItems();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
		
				cmbManager.addItem(iter.next().toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("cmManagerDataLoad",exp+"",Notification.TYPE_ERROR_MESSAGE);
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
			String query="select distinct vUnitId,vUnitName from tbEmpOfficialPersonalInfo where bStatus='1' order by vUnitName";
			System.out.println(query);
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
			String query = "select distinct vUnitId,vSectionId,vDesignationId,vDepartmentId " +
					" from tbEmpOfficialPersonalInfo where bStatus='1' and  vEmployeeId='"+cmbEmployee.getValue()+"'";
			
			List <?> lst = session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				for(Iterator <?> itr = lst.iterator();itr.hasNext();)
				{
					Object [] element = (Object[])itr.next();
					cmbUnit.setValue(element[0]);
					cmbSection.setValue(element[1]);
					cmbDesignationID.setValue(element[2]);
					cmbDepartment.setValue(element[3]);
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
			String query="select distinct vDesignationId,vDesignationName from tbEmpOfficialPersonalInfo where bStatus='1' order by vDesignationName";
			System.out.println(query);
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
			String query="select distinct vDepartmentId,vDepartmentName from tbEmpOfficialPersonalInfo where bStatus='1' order by vDepartmentName";
			System.out.println(query);
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
			String query="select distinct vSectionId,vSectionName from tbEmpOfficialPersonalInfo where bStatus='1' order by vSectionName";
			System.out.println(query);
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
	private void cmbJobSiteAdd()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct 0,vJobSite from tbOTRequest";
			System.out.println(query);
			List <?> lst=session.createSQLQuery(query).list();
			cmbJobSite.removeAllItems();
			if(!lst.isEmpty())
			{
				for(Iterator <?> itr=lst.iterator();itr.hasNext();)
				{
					Object[] element=(Object[])itr.next();
					cmbJobSite.addItem(element[1].toString());
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbSectionValueAdd", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}


	private void checkForm()
	{
		String inhour=tHrFrom.getValue().toString().isEmpty()?"00":tHrFrom.getValue().toString().trim();
		String inMin=tMinFrom.getValue().toString().isEmpty()?"00":tMinFrom.getValue().toString().trim();
		String inTime="";
		if(txtFrom.getValue().toString().trim().equals("PM"))
			inTime=Integer.toString(Integer.parseInt(inhour)+12)+":"+inMin+":00";
		else
			inTime=inhour+":"+inMin+":00";

		String outhour=tHrTo.getValue().toString().isEmpty()?"00":tHrTo.getValue().toString().trim();
		String OutMin=tMinTo.getValue().toString().isEmpty()?"00":tMinTo.getValue().toString().trim();
		String outTime="";
		if(txtTo.getValue().toString().trim().equals("PM"))
			outTime=Integer.toString(Integer.parseInt(outhour)+12)+":"+OutMin+":00";
		else
			outTime=outhour+":"+OutMin+":00";
		
		
		if(cmbEmployee.getValue()!=null)
		{
			if(cmbJobSite.getValue()!=null)
			{
				if(cmbManager.getValue()!=null)
				{
					if(dDateFrom.getValue()!=null)
					{
						if(dDateTo.getValue()!=null)
						{
							if(!txtTimeTotal.getValue().toString().isEmpty())
							{
								if(!inTime.equalsIgnoreCase(outTime))
								{
									if(!txtTimeTotal.getValue().toString().isEmpty())
									{
										int totalHour=(int) txtTimeTotal.getValue();
										if(totalHour>0)
										{
											saveButtonEvent();
										}
										else
										{
											showNotification("Warning", "Total Hour Cannot be Minus Amount", Notification.TYPE_WARNING_MESSAGE);
										}							
									}
									else
									{
										showNotification("Warning", "From Time and To Time Cannot be same!", Notification.TYPE_WARNING_MESSAGE);
									}						
								}
								else
								{
									showNotification("Warning", "From Time and To Time Cannot be same!", Notification.TYPE_WARNING_MESSAGE);
								}
							
							}
						}
					}
				}
				else
				{
					showNotification("Warning", "Please select Manager!", Notification.TYPE_WARNING_MESSAGE);
					cmbManager.focus();
				}
			}
			else
			{
				showNotification("Warning", "Please select Job Site!", Notification.TYPE_WARNING_MESSAGE);
				cmbJobSite.focus();
			}
		}
		else
		{
			cmbEmployee.focus();
			showNotification("Warning", "Please select Employee!", Notification.TYPE_WARNING_MESSAGE);
		}
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
		String query = "select top(5) * from tbMonthlySalary " +
				"where vEmployeeID='"+cmbEmployee.getValue()+"' " +
				"and ((MONTH(dSalaryDate) = '"+dbMonthFormat.format(dRequestDate.getValue())+"' and YEAR(dSalaryDate) = '"+dbYearFormat.format(dRequestDate.getValue())+"') " +
				"or (MONTH(dSalaryDate) = '"+dbMonthFormat.format(dRequestDate.getValue())+"' and YEAR(dSalaryDate) = '"+dbYearFormat.format(dRequestDate.getValue())+"'))";
		System.out.println("Check Salary: "+query);
		
		if(!chkSalary(query))
		{
			if(isUpdate)
			{
				MessageBox mb = new MessageBox(getParent(),"Are You Sure?",MessageBox.Icon.QUESTION,"Do You Want to Update All Information?",new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"),new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
				mb.setStyleName("cwindowMB");
				mb.show(new EventListener()
				{
					public void buttonClicked(ButtonType buttonType)
					{
						if(buttonType==ButtonType.YES)
						{
							updateData();
							componentIni(true);
							btnIni(true);
							isFind=false;
							isUpdate=false;
							cButton.btnEdit.setEnabled(false);
							cButton.btnDelete.setEnabled(false);
							Notification n=new Notification("All Information Updated Successfully!","",Notification.TYPE_TRAY_NOTIFICATION);
							n.setPosition(Notification.POSITION_TOP_RIGHT);
							showNotification(n);
						}
					}
				});
			}
			else
			{
				MessageBox mb = new MessageBox(getParent(),"Are You Sure?",MessageBox.Icon.QUESTION,"Do You Want to save All Information?",new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"),new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
				mb.setStyleName("cwindowMB");
				mb.show(new EventListener()
				{
					public void buttonClicked(ButtonType buttonType)
					{
						if(buttonType==ButtonType.YES)
						{
							String sql="select * from tbOTRequest where dRequestDate='"+sessionBean.dfDb.format(dRequestDate.getValue())+"' and vEmployeeId='"+cmbEmployee.getValue()+"'";
							
							if(!chkSalary(sql))
							{
								insertData();
								componentIni(true);
								btnIni(true);
								isFind=false;
								isUpdate=false;
								Notification n=new Notification("All Information Saved Successfully!","",Notification.TYPE_TRAY_NOTIFICATION);
								n.setPosition(Notification.POSITION_TOP_RIGHT);
								showNotification(n);
							}
							else
							{
								showNotification("Warning", "Data already exist! Try new one!!!", Notification.TYPE_WARNING_MESSAGE);
							}
						}
					}
				});
			}
			
		}
		else
		{
			showNotification("Warning", "Salary Already Generated for this Month!!!", Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private String transactionIDGenerate()
	{
		String transactionID = "TRA-";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select ISNULL(MAX(CAST(SUBSTRING(vTransactionID,5,LEN(vTransactionID)) as int)),0)+1 from tbOTRequest";
			
			List <?> lst = session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				transactionID += lst.iterator().next().toString();
				System.out.println("transactionIDGenerate: "+transactionID);
			}
		}
		catch (Exception exp)
		{
			showNotification("transactionIDGenerate", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
		return transactionID;
	}

	private void insertData()
	{
		System.out.println("Hello Boss!! I'm from insertData()");
		
		Session session=SessionFactoryUtil.getInstance().openSession();
		Transaction tx=session.beginTransaction();
		try
		{
			String inhour=tHrFrom.getValue().toString().isEmpty()?"00":tHrFrom.getValue().toString().trim();
			String inMin=tMinFrom.getValue().toString().isEmpty()?"00":tMinFrom.getValue().toString().trim();
			String inTime="";
			String inTF=txtFrom.getValue().toString();
			inTime=inhour+":"+inMin+":00 "+inTF;
			
			String outhour=tHrTo.getValue().toString().isEmpty()?"00":tHrTo.getValue().toString().trim();
			String OutMin=tMinTo.getValue().toString().isEmpty()?"00":tMinTo.getValue().toString().trim();
			String outTime="";
			String outTF=txtTo.getValue().toString();
			outTime=outhour+":"+OutMin+":00 "+outTF;
			

			System.out.println("inTime: "+inTime);
			System.out.println("outTime: "+outTime);
			
			StringTokenizer strToken=new StringTokenizer(cmbEmployee.getItemCaption(cmbEmployee.getValue()), "->");
			String employeeCode=strToken.nextToken();
			String employeeName=strToken.nextToken(); 
			
			String transactionID = transactionIDGenerate();
			txtTransactionID.setValue(transactionID);
			
			String approved = "0";
			
			String query = "insert into tbOTRequest(vTransactionId,vEmployeeId,vEmployeeName,"
					+ " vDesignationId,vDesignationName,vDepartmentId,vDepartmentName, "
					+ " vJobSite,dRequestDate,dTimeFrom,dTimeTo,mTotalTimeHR,vManger,vWorkRequest,vManPower,"
					+ " iHoliday,iNightTim,vUserId,vUserName,vUserIp,dEntryTime,dDateFrom,dDateTo,iFinal)  "
			        + " values('"+transactionID+"','"+cmbEmployee.getValue()+"','"+employeeName+"','"+cmbDesignationID.getValue()+"',"
                    + " '"+cmbDesignationID.getItemCaption(cmbDesignationID.getValue())+"','"+cmbDepartment.getValue()+"',"
                    + " '"+cmbDepartment.getItemCaption(cmbDepartment.getValue())+"',"
                    + " '"+(cmbJobSite.getValue()==null?"":cmbJobSite.getItemCaption(cmbJobSite.getValue()))+"',"
                    + " '"+sessionBean.dfDb.format(dRequestDate.getValue())+"',"
                    + " '"+(sessionBean.dfDb.format(dRequestDate.getValue())+" "+inTime)+"',"
                    + " '"+(sessionBean.dfDb.format(dRequestDate.getValue())+" "+outTime)+"',"
                    + " '"+txtTimeTotal.getValue()+"',"
                    + " '"+(cmbManager.getValue()==null?"":cmbManager.getItemCaption(cmbManager.getValue()))+"',"
                    + " '"+(txtOverTimeRequest.getValue().toString().isEmpty()?"":txtOverTimeRequest.getValue().toString().trim().replaceAll("'","#"))+"',"
                    + " '"+opgNationality.getValue().toString()+"',"
                    + " '"+(opgOverTime.getValue().toString().equals("Holiday")?"1":"0")+"',"
                    + " '"+(opgOverTime.getValue().toString().equals("Night Time")?"1":"0")+"',"
                    + " '"+sessionBean.getUserId()+"',"
                    + " '"+sessionBean.getUserName()+"',"
                    + " '"+sessionBean.getUserIp()+"',GETDATE(),"
                    + " '"+sessionBean.dfDb.format(dDateFrom.getValue())+"','"+sessionBean.dfDb.format(dDateTo.getValue())+"','"+approved+"')";
			
			System.out.println("insertData: "+query);
			
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

	private void updateData()
	{
		System.out.println("Hello Boss!! I'm from updateData()");
		
		Session session=SessionFactoryUtil.getInstance().openSession();
		Transaction tx=session.beginTransaction();
		try
		{
			String inhour=tHrFrom.getValue().toString().isEmpty()?"00":tHrFrom.getValue().toString().trim();
			String inMin=tMinFrom.getValue().toString().isEmpty()?"00":tMinFrom.getValue().toString().trim();
			String inTime="";
			String inTF=txtFrom.getValue().toString();
			inTime=inhour+":"+inMin+":00 "+inTF;
			
			String outhour=tHrTo.getValue().toString().isEmpty()?"00":tHrTo.getValue().toString().trim();
			String OutMin=tMinTo.getValue().toString().isEmpty()?"00":tMinTo.getValue().toString().trim();
			String outTime="";
			String outTF=txtTo.getValue().toString();
			outTime=outhour+":"+OutMin+":00 "+outTF;
			
			

			System.out.println("inTime: "+inTime);
			System.out.println("outTime: "+outTime);
			
			StringTokenizer strToken=new StringTokenizer(cmbEmployee.getItemCaption(cmbEmployee.getValue()), "->");
			String employeeCode=strToken.nextToken();
			String employeeName=strToken.nextToken(); 
			
			String transactionID=txtTransactionID.getValue().toString();
			
			String updateData = "insert into tbUDOTRequest(vTransactionId,vEmployeeId,vEmployeeName,vDesignationId,vDesignationName,vDepartmentId,"
					+ "vDepartmentName,vJobSite,dRequestDate,dTimeFrom,dTimeTo,mTotalTimeHR,vManger,vWorkRequest,vManPower,iHoliday,iNightTim,"
					+ "vUserId,vUserName,vUserIp,dEntryTime,dDateFrom,dDateTo,iFinal,vUdFlag,vApprovedBy) "
					+ "select vTransactionId,vEmployeeId,vEmployeeName,vDesignationId,vDesignationName,vDepartmentId,"
					+ "vDepartmentName,vJobSite,dRequestDate,dTimeFrom,dTimeTo,mTotalTimeHR,vManger,vWorkRequest,vManPower,iHoliday,iNightTim,"
					+ "vUserId,vUserName,vUserIp,dEntryTime,dDateFrom,dDateTo,iFinal,'UPDATE',vApprovedBy "
					+ "from tbOTRequest where vTransactionId ='"+transactionID+"' ";
			
			System.out.println("updateData: "+updateData);
			session.createSQLQuery(updateData).executeUpdate();
			
			String approved = "0";
			
			String query = "update tbOTRequest "
					+ "set vEmployeeId='"+cmbEmployee.getValue()+"',"
					+ "vEmployeeName='"+employeeName+"',"
					+ "vDesignationId='"+cmbDesignationID.getValue()+"',"
					+ "vDesignationName='"+cmbDesignationID.getItemCaption(cmbDesignationID.getValue())+"',"
					+ "vDepartmentId='"+cmbDepartment.getValue()+"',"
					+ "vDepartmentName='"+cmbDepartment.getItemCaption(cmbDepartment.getValue())+"',"
					+ "vJobSite='"+(cmbJobSite.getValue()==null?"":cmbJobSite.getItemCaption(cmbJobSite.getValue()))+"',"
					+ "dRequestDate='"+sessionBean.dfDb.format(dRequestDate.getValue())+"',"
					+ "dTimeFrom='"+(sessionBean.dfDb.format(dRequestDate.getValue())+" "+inTime)+"',"
					+ "dTimeTo='"+(sessionBean.dfDb.format(dRequestDate.getValue())+" "+outTime)+"',"
					+ "mTotalTimeHR='"+txtTimeTotal.getValue()+"',"
					+ "vManger='"+(cmbManager.getValue()==null?"":cmbManager.getItemCaption(cmbManager.getValue()))+"',"
					+ "vWorkRequest='"+(txtOverTimeRequest.getValue().toString().isEmpty()?"":txtOverTimeRequest.getValue().toString().trim().replaceAll("'","#"))+"',"
					+ "vManPower='"+opgNationality.getValue().toString()+"',"
					+ "iHoliday='"+(opgOverTime.getValue().toString().equals("Holiday")?"1":"0")+"',"
					+ "iNightTim='"+(opgOverTime.getValue().toString().equals("Night Time")?"1":"0")+"',"
					+ "vUserId='"+sessionBean.getUserId()+"',"
					+ "vUserName='"+sessionBean.getUserName()+"',"
					+ "vUserIp='"+sessionBean.getUserIp()+"',"
					+ "dEntryTime=GETDATE(),"
					+ "dDateFrom='"+sessionBean.dfDb.format(dDateFrom.getValue())+"',"
					+ "dDateTo='"+sessionBean.dfDb.format(dDateTo.getValue())+"',"
					+ "iFinal='"+approved+"' where vTransactionId='"+transactionID+"' "; 
			
			System.out.println("updateData: "+query);
			
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
		Window win = new OverTimeRequestFormFind(sessionBean, txtTransactionID);
		win.addListener(new CloseListener()
		{
			public void windowClose(CloseEvent e)
			{
				if(!txtTransactionID.getValue().toString().trim().isEmpty())
				{
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
		int hrFrom=0,hrTo=0;
		try
		{
			String query = "select vEmployeeId,vJobSite,dRequestDate,DATEPART(HOUR,dTimeFrom) hours1,"
					+ " DATEPART(HOUR,dTimeTo) hours2,mTotalTimeHR,"
					+ " vManger,vWorkRequest,iHoliday,iNightTim,vManPower,DATEPART(MINUTE,CONVERT(time,dTimeFrom))min1,"
					+ " DATEPART(MINUTE,CONVERT(time,dTimeTo))min2,vTransactionId,dDateFrom,dDateTo,iHoliday,"
					+ "(case when DATEPART(HOUR,dTimeFrom)>11 then 'PM' else 'AM' end)tf1,"
					+ "(case when DATEPART(HOUR,dTimeTo)>11 then 'PM' else 'AM' end)tr2  "
					+ " from tbOTRequest where  vTransactionID = '"+TransID+"'";
			System.out.println("Find :"+query);
			List <?> lst = session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				for(Iterator <?> itr = lst.iterator();itr.hasNext();)
				{
					Object [] element = (Object [])itr.next();
					cmbEmployee.setValue(element[0]);
					cmbJobSite.setValue(element[1]);
					dRequestDate.setValue(element[2]);
					

					
					hrFrom=(int) element[3];
					if(hrFrom>12)
					{
						hrFrom=hrFrom-12;
					}
					
					dDateFrom.setValue(element[14]);
					tHrFrom.setValue(hrFrom);
					tMinFrom.setValue(element[11]);
					txtFrom.setValue(element[17]);					
					

					hrTo=(int) element[4];
					if(hrTo>12)
					{
						hrTo=hrTo-12;
					}
					
					System.out.println("hrFrom: "+hrFrom+" hrTo: "+hrTo);
					
					dDateTo.setValue(element[15]);
					tHrTo.setValue(hrTo);
					tMinTo.setValue(element[12]);
					txtTo.setValue(element[18]);
					
					
					txtTimeTotal.setValue(element[5]);
					cmbManager.setValue(element[6]);
					txtOverTimeRequest.setValue(element[7]);
					if(element[8].equals(1))
					{
						opgOverTime.setValue("Holiday");
					}
					else
					{
						opgOverTime.setValue("Night Time");
					}
					if(element[9].equals(1))
					{
						opgOverTime.setValue("Night Time");
					}
					else
					{
						opgOverTime.setValue("Holiday");
					}
					opgNationality.setValue(element[10].toString());
					txtTransactionID.setValue(element[13]);
					
				}
			}
		}
		catch (Exception exp)
		{
			showNotification("findInitialize", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}

		cButton.btnEdit.setEnabled(true);
		cButton.btnDelete.setEnabled(true);
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
		dRequestDate.setEnabled(!t);
		cmbEmployee.setEnabled(!t);
		tHrFrom.setEnabled(!t);
		tMinFrom.setEnabled(!t);
		tHrTo.setEnabled(!t);
		tMinTo.setEnabled(!t);
		txtFrom.setEnabled(!t);
		txtTo.setEnabled(!t);
		cmbManager.setEnabled(!t);
		cmbJobSite.setEnabled(!t);
		txtOverTimeRequest.setEnabled(!t);
		dRequestDate.setEnabled(!t);
		dDateFrom.setEnabled(false);
		dDateTo.setEnabled(!t);
	}


	private void txtClear()
	{
		dRequestDate.setValue(new Date());
		cmbEmployee.setValue(null);
		cmbUnit.setValue(null);
		cmbDepartment.setValue(null);
		cmbSection.setValue(null);
		cmbDesignationID.setValue(null);
		cmbManager.setValue(null);
		cmbJobSite.setValue(null);
		txtOverTimeRequest.setValue("");
		txtTimeTotal.setValue("");
		dRequestDate.setValue(new java.util.Date());
		opgNationality.setValue("Bangladeshi");
		opgOverTime.setValue("Holiday");
		dDateFrom.setValue(new java.util.Date());
		dDateTo.setValue(new java.util.Date());
		txtTransactionID.setValue("");
		tHrFrom.setValue("");
		tHrTo.setValue("");
		tMinFrom.setValue("");
		tMinTo.setValue("");
		
	}

	private void focusEnter()
	{
		allComp.add(dRequestDate);
		allComp.add(cmbEmployee);
		allComp.add(cmbJobSite);
		allComp.add(cmbManager);
		/*allComp.add(dTimeFrom);
		allComp.add(dTimeTo);*/
		allComp.add(dDateFrom);
		allComp.add(tHrFrom);
		allComp.add(tMinFrom);
		allComp.add(txtFrom);
		allComp.add(dDateTo);
		allComp.add(tHrTo);
		allComp.add(tMinTo);
		allComp.add(txtTo);
		
		allComp.add(txtTimeTotal);
		allComp.add(txtOverTimeRequest);
		
		allComp.add(cButton.btnSave);
		new FocusMoveByEnter(this,allComp);
	}

	public AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("790px");
		mainLayout.setHeight("340px");
		mainLayout.setMargin(false);

		// lblRequestDate
		lblRequestDate = new Label("Request Date : ");
		lblRequestDate.setImmediate(false);
		lblRequestDate.setWidth("-1px");
		lblRequestDate.setHeight("-1px");
		mainLayout.addComponent(lblRequestDate, "top:10.0px; left:30.0px;");

		// dRequestDate
		dRequestDate = new PopupDateField();
		dRequestDate.setImmediate(false);
		dRequestDate.setWidth("110px");
		dRequestDate.setHeight("-1px");
		dRequestDate.setValue(new Date());
		dRequestDate.setDateFormat("dd-MM-yyyy");
		dRequestDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dRequestDate, "top:08.0px; left:140.0px;");
		
		opgOverTime=new OptionGroup("",overTime);
		opgOverTime.setImmediate(true);
		opgOverTime.setHeight("-1px");
		opgOverTime.setValue("Holiday");
		mainLayout.addComponent(opgOverTime,"top:40px; left:140px");
		opgOverTime.setStyleName("horizontal");
		
		// lblEmployeeId
		lblEmployeeId = new Label("Employee :");
		lblEmployeeId.setImmediate(false);
		lblEmployeeId.setWidth("-1px");
		lblEmployeeId.setHeight("-1px");
		mainLayout.addComponent(lblEmployeeId, "top:70px; left:30.0px;");

		// cmbEmployee
		cmbEmployee = new ComboBox();
		cmbEmployee.setImmediate(true);
		cmbEmployee.setWidth("210px");
		cmbEmployee.setHeight("-1px");
		cmbEmployee.setNullSelectionAllowed(false);
		cmbEmployee.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbEmployee, "top:68px; left:140.0px;");

		// lblSection
		lblUnit = new Label("Project :");
		lblUnit.setImmediate(false);
		lblUnit.setWidth("-1px");
		lblUnit.setHeight("-1px");
		mainLayout.addComponent(lblUnit, "top:100px; left:30.0px;");

		// cmbSection
		cmbUnit = new ComboBox();
		cmbUnit.setImmediate(true);
		cmbUnit.setWidth("210px");
		cmbUnit.setHeight("-1px");
		cmbUnit.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbUnit, "top:98px; left:140.0px;");
		cmbUnit.setEnabled(false);
		
		// cmbSection
		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("210px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Department :"), "top:130px; left:30.0px;");
		mainLayout.addComponent(cmbDepartment, "top:128px; left:140.0px;");
		cmbDepartment.setEnabled(false);

		// lblSection
		lblSection = new Label("Section :");
		lblSection.setImmediate(false);
		lblSection.setWidth("-1px");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection, "top:160px; left:30.0px;");

		// cmbSection
		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("210px");
		cmbSection.setHeight("-1px");
		cmbSection.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbSection, "top:158px; left:140.0px;");
		cmbSection.setEnabled(false);

		// lblDesignation
		lblDesignation = new Label("Designation :");
		lblDesignation.setImmediate(false);
		lblDesignation.setWidth("210px");
		lblDesignation.setHeight("-1px");
		mainLayout.addComponent(lblDesignation, "top:190px; left:30.0px;");

		// cmbDesignationID
		cmbDesignationID = new ComboBox();
		cmbDesignationID.setImmediate(true);
		cmbDesignationID.setWidth("210px");
		cmbDesignationID.setHeight("-1px");
		mainLayout.addComponent(cmbDesignationID, "top:188px; left:141.0px;");
		cmbDesignationID.setEnabled(false);
		
		cmbJobSite = new ComboBox();
		cmbJobSite.setImmediate(true);
		cmbJobSite.setWidth("210px");
		cmbJobSite.setHeight("-1px");
		cmbJobSite.setNewItemsAllowed(true);
		mainLayout.addComponent(new Label("Job Site :"), "top:10px; left:425px;");
		mainLayout.addComponent(cmbJobSite, "top:08px; left:530px;");
		
		cmbManager = new ComboBox();
		cmbManager.setImmediate(true);
		cmbManager.setWidth("210px");
		cmbManager.setHeight("-1px");
		cmbManager.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Manager :"), "top:40px; left:425px;");
		mainLayout.addComponent(cmbManager, "top:38px; left:530px;");

		mainLayout.addComponent(new Label("<b><Font color='CD0606' size='2px'>Type a or p for AM/PM</Font></b>",Label.CONTENT_XHTML),"top:67px; left:530px");
		//mainLayout.addComponent(new Label("<b><Font color='CD0606' size='2px'> H : M : AM/PM</Font></b>",Label.CONTENT_XHTML),"top:80px; left:650px");

		dDateFrom=new PopupDateField();
		dDateFrom.setImmediate(true);
		dDateFrom.setDateFormat("dd-MM-yyyy");
		dDateFrom.setResolution(PopupDateField.RESOLUTION_DAY);
		dDateFrom.setWidth("110px");
		dDateFrom.setHeight("-1px");
		dDateFrom.setValue(new java.util.Date());
		mainLayout.addComponent(new Label("From Date:"),"top:100px; left:425px");
		mainLayout.addComponent(dDateFrom,"top:98px; left:530px");
		
		tHrFrom=new TimeField();
		tHrFrom.setWidth("25.0px");
		tHrFrom.setImmediate(true);
		tHrFrom.setInputPrompt("HH");
		mainLayout.addComponent(tHrFrom, "top:98px;left:650px;");
		mainLayout.addComponent(new Label(" : "),"top:98px;left:680px;");

		tMinFrom=new TimeField();
		tMinFrom.setWidth("25.0px");
		tMinFrom.setImmediate(true);
		tMinFrom.setInputPrompt("MM");
		mainLayout.addComponent(tMinFrom, "top:98px;left:690px;");

		txtFrom=new TextField();
		txtFrom.setWidth("28.0px");
		txtFrom.setImmediate(true);
		txtFrom.setTextChangeEventMode(TextChangeEventMode.EAGER);
		mainLayout.addComponent(txtFrom, "top:98px;left:720px;");
		
		dDateTo=new PopupDateField();
		dDateTo.setImmediate(true);
		dDateTo.setDateFormat("dd-MM-yyyy");
		dDateTo.setResolution(PopupDateField.RESOLUTION_DAY);
		dDateTo.setWidth("110px");
		dDateTo.setHeight("-1px");
		dDateTo.setValue(new java.util.Date());
		mainLayout.addComponent(new Label("To Date:"),"top:130px; left:425px");
		mainLayout.addComponent(dDateTo,"top:128px; left:530px");
		
		tHrTo=new TimeField();
		tHrTo.setWidth("25.0px");
		tHrTo.setImmediate(true);
		tHrTo.setInputPrompt("HH");
		mainLayout.addComponent(tHrTo, "top:130px;left:650px;");
		mainLayout.addComponent(new Label(" : "),"top:130px;left:680px;");

		tMinTo=new TimeField();
		tMinTo.setWidth("25.0px");
		tMinTo.setImmediate(true);
		tMinTo.setInputPrompt("MM");
		mainLayout.addComponent(tMinTo, "top:130px;left:690px;");

		txtTo=new TextField();
		txtTo.setWidth("28.0px");
		txtTo.setImmediate(true);
		txtTo.setTextChangeEventMode(TextChangeEventMode.EAGER);
		mainLayout.addComponent(txtTo, "top:130px;left:720px;");
		
		txtTimeTotal=new TimeField();
		txtTimeTotal.setWidth("40.0px");
		txtTimeTotal.setImmediate(true);
		txtTimeTotal.setInputPrompt("HH");
		mainLayout.addComponent(new Label("Total Hour :"), "top:160px; left:425px;");
		mainLayout.addComponent(txtTimeTotal, "top:158px; left:531px;");
		txtTimeTotal.setEnabled(false);
		
		txtOverTimeRequest=new TextField();
		txtOverTimeRequest.setWidth("210px");
		txtOverTimeRequest.setHeight("-1px");
		txtOverTimeRequest.setImmediate(true);
		mainLayout.addComponent(new Label("O.T Request For :"),"top:190px; left:425px");
		mainLayout.addComponent(txtOverTimeRequest,"top:188px; left:530px");
		
		opgNationality=new OptionGroup("",nationality);
		opgNationality.setWidth("-1px");
		opgNationality.setHeight("-1px");
		opgNationality.setImmediate(true);
		opgNationality.setValue("Bangladeshi");
		mainLayout.addComponent(new Label("Manpower :"),"top:220px; left:425px");
		mainLayout.addComponent(opgNationality,"top:218px; left:530px");
		opgNationality.setStyleName("horizontal");
		
		mainLayout.addComponent(cButton, "bottom:15px; left:50px;");
		return mainLayout;
	}
}
