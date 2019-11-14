package com.appform.hrmModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
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
	
	private PopupDateField dReplaceHoliday,dReplaceWorking;

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
	private InlineDateField dTimeFrom;
	private InlineDateField dTimeTo;
	private InlineDateField dTimeTotal;
	private static final List<String> overTime = Arrays.asList(new String[]{"Holiday","Night Time"});
	private static final List<String> nationality = Arrays.asList(new String[]{"Bangladeshi","Korean"});
	
	private TextField txtOverTimeRequest;
	private boolean Find=false;
	private boolean Update=false;
	private CommonMethod cm;
	private String menuId = "";
	private SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dbMonthFormat = new SimpleDateFormat("MM");
	private SimpleDateFormat dbYearFormat = new SimpleDateFormat("yyyy");
    public int count=0;
    SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss aa");
    private TimeField tHrFrom=new TimeField();
	private TimeField tMinFrom=new TimeField();
	private TextField txtFrom=new TextField();
	private TimeField tHrTo=new TimeField();
	private TimeField tMinTo=new TimeField();
	private TextField txtTo=new TextField();
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
					dReplaceHoliday.setValue(dRequestDate.getValue());
					dReplaceWorking.setValue(dRequestDate.getValue());
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
		dTimeFrom.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
				
				dTimeTotal.setValue(new java.util.Date());
				if(dTimeFrom.getValue()!=null)
				{
					setTime();
				}
			}
		});
		dTimeTo.addListener(new ValueChangeListener() {
					
			public void valueChange(ValueChangeEvent event) {
				dTimeTotal.setValue(new java.util.Date());
				if(dTimeTo.getValue()!=null)
				{
					setTime();
				}
			}
		});
		tHrFrom.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!tHrFrom.getValue().toString().isEmpty())	
				{
					setTime();
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
					setTime();
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
					setTime();
				}

				if(event.getText().equalsIgnoreCase("p"))
				{
					txtFrom.setValue("PM");
					setTime();
				}
			}
		});

		tHrTo.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!tHrTo.getValue().toString().isEmpty())	
				{
					setTime();
					if(Integer.parseInt(tHrTo.getValue().toString())>12)
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
					setTime();
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
					setTime();
				}

				if(event.getText().equalsIgnoreCase("p"))
				{
					txtTo.setValue("PM");
					setTime();
				}
			}
		});

		cButton.btnNew.addListener(new ClickListener()
		{	
			public void buttonClick(ClickEvent event)
			{
				Find = false;
				txtClear();
				componentIni(false);
				btnIni(false);
				dRequestDate.focus();
				count=0;
				txtTransactionID.setValue(transactionIDGenerate());
			}
		});

		cButton.btnSave.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				checkForm();
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
							if(dTimeFrom.getValue()!=null)
							{
								if(dTimeTo.getValue()!=null)
								{
									if(dTimeTotal.getValue()!=null)
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
				Find = false;
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
				Find = true;
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
				"and ((MONTH(dSalaryDate) = '"+dbMonthFormat.format(dTimeTotal.getValue())+"' and YEAR(dSalaryDate) = '"+dbYearFormat.format(dTimeTotal.getValue())+"') " +
				"or (MONTH(dSalaryDate) = '"+dbMonthFormat.format(dTimeTotal.getValue())+"' and YEAR(dSalaryDate) = '"+dbYearFormat.format(dTimeTotal.getValue())+"'))";
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
							String del="delete from tbOTRequest where vTransactionId='"+transactionID+"' ";
							System.out.println(del);
							
							session.createSQLQuery(del).executeUpdate();
							componentIni(true);
							txtClear();
							btnIni(true);
							Find=false;
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
		Find = false;
		Update = false;
		
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

			String query="select *,case when iHoliday=1 and DATEPART(HOUR,CONVERT(time,dTimeTotal))>10 " +
			" then DATEPART(HOUR,CONVERT(time,dTimeTotal))-1 else DATEPART(HOUR,CONVERT(time,dTimeTotal)) end hours, "+
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
	private void setTime()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		try{
			
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
			
			
			String sql="select dbo.funTimeDuration('"+inTime+"','"+outTime+"','"+sessionBean.dfDb.format(dRequestDate.getValue())+"')";
			System.out.println(sql);
			Iterator<?> iter=session.createSQLQuery(sql).list().iterator();
			if(iter.hasNext())
			{
				dTimeTotal.setValue(iter.next());
				
			}
		}catch(Exception exp)
		{
			System.out.println(""+exp);
		}
		finally{session.close();}
	}
	
	private boolean checkData()
	{
		boolean ret=false;
		Session session=SessionFactoryUtil.getInstance().openSession();
		try{
			String sql="select * from tbOTRequest where dRequestDate='"+sessionBean.dfDb.format(dRequestDate.getValue())+"' and vEmployeeId='"+cmbEmployee.getValue()+"'";
			
			List<?> list=session.createSQLQuery(sql).list();
			if(list.isEmpty())
			{
				ret=true;
			}
		}catch(Exception exp)
		{
			System.out.println(""+exp);
		}
		finally{session.close();}
		return ret;
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
					if(dTimeFrom.getValue()!=null)
					{
						if(dTimeTo.getValue()!=null)
						{
							if(dTimeTotal.getValue()!=null)
							{
								if(!inTime.equalsIgnoreCase(outTime))
								{
									if(count==0)
									{
										if(checkData())
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
										saveButtonEvent();
									}
								
								}
								else
								{
									dTimeFrom.focus();
									showNotification("Warning", "From Time and To Time Cannot be same!", Notification.TYPE_WARNING_MESSAGE);
								}
							
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
		System.out.println(dTimeFrom);
		System.out.println(dTimeTo);
		System.out.println(inTime);
		System.out.println(outTime);
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
				"and ((MONTH(dSalaryDate) = '"+dbMonthFormat.format(dTimeTotal.getValue())+"' and YEAR(dSalaryDate) = '"+dbYearFormat.format(dTimeTotal.getValue())+"') " +
				"or (MONTH(dSalaryDate) = '"+dbMonthFormat.format(dTimeTotal.getValue())+"' and YEAR(dSalaryDate) = '"+dbYearFormat.format(dTimeTotal.getValue())+"'))";
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
						
						insertData(session,tx);
						componentIni(true);
						btnIni(true);
						Find=false;
						count=0;
						Notification n=new Notification("All Information "+(count==1?"Updated":"Saved")+" Successfully!","",Notification.TYPE_TRAY_NOTIFICATION);
						n.setPosition(Notification.POSITION_TOP_RIGHT);
						showNotification(n);
					}
				}
			});
		}
		else
		{
			showNotification("Warning", "Salary Already Generated for this Month!!!", Notification.TYPE_WARNING_MESSAGE);
		}
		Find = false;
		Update = false;
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
			
			if(count==1)
			{
				transactionID=txtTransactionID.getValue().toString();
				String del="delete from tbOTRequest where vTransactionId='"+transactionID+"' ";
				System.out.println(del);
				session.createSQLQuery(del).executeUpdate();
			}
			
			StringTokenizer strToken=new StringTokenizer(cmbEmployee.getItemCaption(cmbEmployee.getValue()), "->");
			String employeeCode=strToken.nextToken();
			String employeeName=strToken.nextToken(); 
			
			
			String query = "insert into tbOTRequest(vTransactionId,vEmployeeId,vEmployeeName,"
					+ " vDesignationId,vDesignationName,vDepartmentId,vDepartmentName, "
					+ " vJobSite,dRequestDate,dTimeFrom,dTimeTo,dTimeTotal,vManger,vWorkRequest,vManPower,"
					+ " iHoliday,iNightTim,vUserId,vUserName,vUserIp,dEntryTime,dReplaceHoliday,dReplaceWorking)  "
			        + " values('"+transactionID+"','"+cmbEmployee.getValue()+"','"+employeeName+"','"+cmbDesignationID.getValue()+"',"
                    + " '"+cmbDesignationID.getItemCaption(cmbDesignationID.getValue())+"','"+cmbDepartment.getValue()+"',"
                    + " '"+cmbDepartment.getItemCaption(cmbDepartment.getValue())+"',"
                    + " '"+(cmbJobSite.getValue()==null?"":cmbJobSite.getItemCaption(cmbJobSite.getValue()))+"',"
                    + " '"+sessionBean.dfDb.format(dRequestDate.getValue())+"',"
                    + " '"+(sessionBean.dfDb.format(dRequestDate.getValue())+" "+inTime)+"',"
                    + " '"+(sessionBean.dfDb.format(dRequestDate.getValue())+" "+outTime)+"',"
                    + " '"+sessionBean.dDateTimeFormat.format(dTimeTotal.getValue())+"',"
                    + " '"+(cmbManager.getValue()==null?"":cmbManager.getItemCaption(cmbManager.getValue()))+"',"
                    + " '"+(txtOverTimeRequest.getValue().toString().isEmpty()?"":txtOverTimeRequest.getValue().toString().trim().replaceAll("'","#"))+"',"
                    + " '"+opgNationality.getValue().toString()+"',"
                    + " '"+(opgOverTime.getValue().toString().equals("Holiday")?"1":"0")+"',"
                    + " '"+(opgOverTime.getValue().toString().equals("Night Time")?"1":"0")+"',"
                    + " '"+sessionBean.getUserId()+"',"
                    + " '"+sessionBean.getUserName()+"',"
                    + " '"+sessionBean.getUserIp()+"',GETDATE(),"
                    + " '"+sessionBean.dfDb.format(dReplaceHoliday.getValue())+"','"+sessionBean.dfDb.format(dReplaceWorking.getValue())+"')";
			
			
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
		try
		{
			String query = "select vEmployeeId,vJobSite,dRequestDate,DATEPART(HOUR,CONVERT(time,dTimeFrom))%12 hours1,"
					+ " DATEPART(HOUR,CONVERT(time,dTimeTo))%12 hours2,dTimeTotal,"
					+ " vManger,vWorkRequest,iHoliday,iNightTim,vManPower,DATEPART(MINUTE,CONVERT(time,dTimeFrom))min1,"
					+ " DATEPART(MINUTE,CONVERT(time,dTimeTo))min2,vTransactionId,dReplaceHoliday,dReplaceWorking,iHoliday "
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
					tHrFrom.setValue(element[3]);
					tHrTo.setValue(element[4]);
					tMinFrom.setValue(element[11]);
					tMinTo.setValue(element[12]);
					dTimeTotal.setValue(element[5]);
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
					dReplaceHoliday.setValue(element[14]);
					dReplaceWorking.setValue(element[15]);
					
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
		dTimeFrom.setEnabled(!t);
		dTimeTo.setEnabled(!t);
		dRequestDate.setEnabled(!t);
		dReplaceHoliday.setEnabled(!t);
		dReplaceWorking.setEnabled(!t);
		
		
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
		dTimeFrom.setValue(new java.util.Date());
		dTimeTo.setValue(new java.util.Date());
		dTimeTotal.setValue(new java.util.Date());
		dRequestDate.setValue(new java.util.Date());
		opgNationality.setValue("Bangladeshi");
		opgOverTime.setValue("Holiday");
		dReplaceHoliday.setValue(new java.util.Date());
		dReplaceWorking.setValue(new java.util.Date());
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
		allComp.add(dReplaceHoliday);
		allComp.add(dReplaceWorking);
		allComp.add(tHrFrom);
		allComp.add(tMinFrom);
		allComp.add(txtFrom);
		allComp.add(tHrTo);
		allComp.add(tMinTo);
		allComp.add(txtTo);
		
		allComp.add(dTimeTotal);
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
		mainLayout.addComponent(new Label("Job Site :"), "top:10px; left:430px;");
		mainLayout.addComponent(cmbJobSite, "top:08px; left:530px;");
		
		cmbManager = new ComboBox();
		cmbManager.setImmediate(true);
		cmbManager.setWidth("210px");
		cmbManager.setHeight("-1px");
		cmbManager.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Manager :"), "top:40px; left:430px;");
		mainLayout.addComponent(cmbManager, "top:38px; left:530px;");
		
		dReplaceHoliday=new PopupDateField();
		dReplaceHoliday.setImmediate(true);
		dReplaceHoliday.setDateFormat("dd-MM-yyyy");
		dReplaceHoliday.setResolution(PopupDateField.RESOLUTION_DAY);
		dReplaceHoliday.setWidth("110px");
		dReplaceHoliday.setHeight("-1px");
		dReplaceHoliday.setValue(new java.util.Date());
		mainLayout.addComponent(new Label("Replace by Holiday:"),"top:70px; left:400px");
		mainLayout.addComponent(dReplaceHoliday,"top:68px; left:530px");
		
		dReplaceWorking=new PopupDateField();
		dReplaceWorking.setImmediate(true);
		dReplaceWorking.setDateFormat("dd-MM-yyyy");
		dReplaceWorking.setResolution(PopupDateField.RESOLUTION_DAY);
		dReplaceWorking.setWidth("110px");
		dReplaceWorking.setHeight("-1px");
		dReplaceWorking.setValue(new java.util.Date());
		mainLayout.addComponent(new Label("Replace by Working Day:"),"top:100px; left:390px");
		mainLayout.addComponent(dReplaceWorking,"top:98px; left:530px");
		
		
		
		tHrFrom=new TimeField();
		tHrFrom.setWidth("25.0px");
		tHrFrom.setImmediate(true);
		mainLayout.addComponent(new Label("From Time : "), "top:130px;left:430px;");
		mainLayout.addComponent(tHrFrom, "top:128px;left:530px;");
		mainLayout.addComponent(new Label(" : "),"top:130px;left:557px;");
		

		tMinFrom=new TimeField();
		tMinFrom.setWidth("25.0px");
		tMinFrom.setImmediate(true);
		mainLayout.addComponent(tMinFrom, "top:128px;left:560px;");
		mainLayout.addComponent(new Label("<b><Font color='CD0606' size='2px'>H:M:AM/PM</Font></b>",Label.CONTENT_XHTML),"top:130px; left:630px");
		mainLayout.addComponent(new Label("<b><Font color='CD0606' size='2px'>Type a or p for AM/PM</Font></b>",Label.CONTENT_XHTML),"top:150px; left:630px");

		txtFrom=new TextField();
		txtFrom.setWidth("28.0px");
		txtFrom.setImmediate(true);
		txtFrom.setTextChangeEventMode(TextChangeEventMode.EAGER);
		mainLayout.addComponent(txtFrom, "top:128px;left:590px;");
		
		tHrTo=new TimeField();
		tHrTo.setWidth("25.0px");
		tHrTo.setImmediate(true);
		mainLayout.addComponent(new Label("To Time : "), "top:160px;left:430px;");
		mainLayout.addComponent(tHrTo, "top:158px;left:530px;");
		mainLayout.addComponent(new Label(" : "),"top:160px;left:557px;");

		tMinTo=new TimeField();
		tMinTo.setWidth("25.0px");
		tMinTo.setImmediate(true);
		mainLayout.addComponent(tMinTo, "top:158px;left:560px;");

		txtTo=new TextField();
		txtTo.setWidth("28.0px");
		txtTo.setImmediate(true);
		txtTo.setTextChangeEventMode(TextChangeEventMode.EAGER);
		mainLayout.addComponent(txtTo, "top:158px;left:590px;");
		
		dTimeTotal = new InlineDateField();
		dTimeTotal.setImmediate(true);
		dTimeTotal.setWidth("110px");
		dTimeTotal.setHeight("-1px");
		dTimeTotal.setDateFormat("hh:mm:ss");
		dTimeTotal.setValue(null);
		dTimeTotal.setResolution(InlineDateField.RESOLUTION_MIN);
		mainLayout.addComponent(new Label("Total Time :"), "top:190px; left:430px;");
		mainLayout.addComponent(dTimeTotal, "top:188px; left:531px;");
		dTimeTotal.setEnabled(false);
		
		/** Un use start**/
		dTimeFrom = new InlineDateField();
		dTimeFrom.setImmediate(true);
		dTimeFrom.setWidth("110px");
		dTimeFrom.setHeight("-1px");
		dTimeFrom.setDateFormat("hh:mm:ss");
		dTimeFrom.setValue(new java.util.Date());
		dTimeFrom.setResolution(InlineDateField.RESOLUTION_SEC);
		//mainLayout.addComponent(new Label("From Time :"), "top:70px; left:430px;");
		mainLayout.addComponent(dTimeFrom, "top:68px; left:531px;");
		dTimeFrom.setVisible(false);
		
		dTimeTo = new InlineDateField();
		dTimeTo.setImmediate(true);
		dTimeTo.setWidth("110px");
		dTimeTo.setHeight("-1px");
		dTimeTo.setDateFormat("hh:mm:ss");
		dTimeTo.setValue(new java.util.Date());
		dTimeTo.setResolution(InlineDateField.RESOLUTION_SEC);
		//mainLayout.addComponent(new Label("To Time :"), "top:100px; left:430px;");
		mainLayout.addComponent(dTimeTo, "top:98px; left:531px;");
		dTimeTo.setVisible(false);
		/** Un use end**/
		
		txtOverTimeRequest=new TextField();
		txtOverTimeRequest.setWidth("210px");
		txtOverTimeRequest.setHeight("-1px");
		txtOverTimeRequest.setImmediate(true);
		mainLayout.addComponent(new Label("Over Time Request For :"),"top:220px; left:395px");
		mainLayout.addComponent(txtOverTimeRequest,"top:218px; left:530px");
		

		opgNationality=new OptionGroup("",nationality);
		opgNationality.setWidth("-1px");
		opgNationality.setHeight("-1px");
		opgNationality.setImmediate(true);
		opgNationality.setValue("Bangladeshi");
		mainLayout.addComponent(new Label("Manpower :"),"top:250px; left:430px");
		mainLayout.addComponent(opgNationality,"top:248px; left:530px");
		opgNationality.setStyleName("horizontal");


		mainLayout.addComponent(cButton, "bottom:15px; left:50px;");
		return mainLayout;
	}
}
