package com.appform.hrmModule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.*;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;

import java.text.SimpleDateFormat;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class EditEmployeeAttendanceMachine extends Window 
{
	private AbsoluteLayout mainLayout;
	
	private Label lblFromDate;
	private PopupDateField dDate;
	
	private Label lblMonth;
	private ComboBox dMonth;

	private Label lblUnitName = new Label("");
	private ComboBox cmbUnitName = new ComboBox();

	private Label lblDepartmentName = new Label("");
	private ComboBox cmbDepartmentName = new ComboBox();
	private CheckBox chkDepartmentAll;
	
	private ComboBox cmbSectionName = new ComboBox();
	private CheckBox chkSectionAll;

	private Label lblEmployeeID = new Label("");
	private ComboBox cmbEmployeeID = new ComboBox();
	private CheckBox chkEmployeeAll;

	private Label lblCl= new Label("");
	

	private SessionBean sessionBean;

	String computerName = "";
	String userName = "";
	String year = "";
	String deptID = "";
	String strEmpDeptID ="";
	String strEmpID ="";
	String strDeptID ="";
	int ind=0;
	int j=0;
	String FingerID ="";
	boolean t;
	String Notify="";

	private boolean isFind= false;

	private SimpleDateFormat DBdateformat = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat dfMonthYear = new SimpleDateFormat("MMMMM-yyyy");
	private SimpleDateFormat dDbFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	private OptionGroup opgTimeSelect;
	private List<?> timeSelect = Arrays.asList(new String[]{"Date","Monthly"});
	
	private Table table = new Table();
	private ArrayList<Label> lbSl = new ArrayList<Label>();
	private ArrayList<Label> lblAutoEmpID = new ArrayList<Label>();
	private ArrayList<Label> lblempID = new ArrayList<Label>();
	private ArrayList<Label> lbFingerID = new ArrayList<Label>();
	private ArrayList<Label> lbEmployeeName = new ArrayList<Label>();
	private ArrayList<Label> lblDesignationID = new ArrayList<Label>();
	private ArrayList<Label> lbDesignation = new ArrayList<Label>();
	private ArrayList<Label> lbAttendDate = new ArrayList<Label>();
	private ArrayList<AmountField> InTime1 = new ArrayList<AmountField>();
	private ArrayList<AmountField> InTime2 = new ArrayList<AmountField>();
	private ArrayList<AmountField> InTime3 = new ArrayList<AmountField>();
	private ArrayList<AmountField> OutTime1 = new ArrayList<AmountField>();
	private ArrayList<AmountField> OutTime2 = new ArrayList<AmountField>();
	private ArrayList<AmountField> OutTime3 = new ArrayList<AmountField>();
	private ArrayList<NativeButton> Delete = new ArrayList<NativeButton>();
	private ArrayList<TextField> txtPermitBy = new ArrayList<TextField>();
	private ArrayList<TextField> txtReason = new ArrayList<TextField>();
	private ArrayList<Label> lbUnitId = new ArrayList<Label>();
	private ArrayList<Label> lbUnitName = new ArrayList<Label>();
	private ArrayList<Label> lbDeptId = new ArrayList<Label>();
	private ArrayList<Label> lbDeptName = new ArrayList<Label>();
	private ArrayList<CheckBox> chkAll = new ArrayList<CheckBox>();

	private CommonButton button = new CommonButton("New", "Save", "", "","Refresh","","","","","Exit");
	ArrayList<Component> allComp = new ArrayList<Component>();
	private CommonMethod cm;
	private String menuId = "";
	public EditEmployeeAttendanceMachine(SessionBean sessionBean,String menuId)
	{
		this.sessionBean = sessionBean;
		this.setCaption("EDIT DELETE EMPLOYEE ATTENDANCE :: "+sessionBean.getCompany());
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		buildMainLayout();
		setContent(mainLayout);
		tableInitialize();
		btnIni(true);
		componentIni(true);
		buttonAction();
		cmbMonthDataLoad();
		cmbUnitDataLoad();
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

	private void cmbUnitDataLoad()
	{
		String sql="";
		cmbUnitName.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			if(opgTimeSelect.getValue().toString()=="Monthly")
			{
				sql = "select distinct vUnitId,vUnitName from tbEmployeeAttendanceFinal "
						+ "where MONTH(dDate) = MONTH('"+dMonth.getValue()+"') and YEAR(dDate) = YEAR('"+dMonth.getValue()+"') "
						+ "order by vUnitName";
			}
			else
			{
				sql = "select distinct vUnitId,vUnitName from tbEmployeeAttendanceFinal "
						+ "where dDate= '"+dDbFormat.format(dDate.getValue())+"' "
						+ "order by vUnitName";
			}
			System.out.println("cmbUnitDataLoad: "+sql);
			
			List <?> list = session.createSQLQuery(sql).list();
			if(!list.isEmpty())
			{
				for(Iterator <?> iter=list.iterator();iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();
					cmbUnitName.addItem(element[0]);
					cmbUnitName.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbUnitDataLoad",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			session.close();
		}
	}
	
	public void employeeDataLoad()
	{
		String sql="",section="%",deptId="%";
		cmbEmployeeID.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		if(!chkDepartmentAll.booleanValue())
		{
			deptId=cmbDepartmentName.getValue().toString();
		}
		if(!chkSectionAll.booleanValue())
		{
			section=cmbSectionName.getValue().toString();
		}
		
		try
		{
			if(opgTimeSelect.getValue().toString()=="Monthly")
			{
				sql = "select distinct vEmployeeID,vEmployeeCode,vEmployeeName from tbEmployeeAttendanceFinal "
						+" where MONTH(dDate) = MONTH('"+dMonth.getValue()+"') and YEAR(dDate) = YEAR('"+dMonth.getValue()+"') " 
						+" and vUnitId='"+cmbUnitName.getValue().toString()+"' and vDepartmentId like '"+deptId+"' and vSectionId like '"+section+"' "
						+" order by vEmployeeCode";
			}
			else
			{
				sql = "select distinct vEmployeeID,vEmployeeCode,vEmployeeName from tbEmployeeAttendanceFinal "
						+" where dDate= '"+dDbFormat.format(dDate.getValue())+"' " +
						" and vUnitId='"+cmbUnitName.getValue().toString()+"'  and vDepartmentId like '"+deptId+"' and vSectionId like '"+section+"' "
						+" order by vEmployeeCode";
			}
			
			System.out.println("employeeDataLoad: "+sql);
			
			
			List <?> list = session.createSQLQuery(sql).list();
			if(!list.isEmpty())
			{
				for(Iterator <?> iter=list.iterator();iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();
					cmbEmployeeID.addItem(element[0]);
					cmbEmployeeID.setItemCaption(element[0], element[1]+"-"+element[2]);
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("employeeDataLoad",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			session.close();
		}
	}
	public void cmbDepartmentData()
	{
		String sql="";
		cmbDepartmentName.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			if(opgTimeSelect.getValue().toString()=="Monthly")
			{
				sql = "select distinct vDepartmentId,vDepartmentName from tbEmployeeAttendanceFinal "
						+" where MONTH(dDate) = MONTH('"+dMonth.getValue()+"') and YEAR(dDate) = YEAR('"+dMonth.getValue()+"') " 
						+" and vUnitId like'"+cmbUnitName.getValue()+"' "
						+" order by vDepartmentName";
			}
			else
			{
				sql = "select distinct vDepartmentId,vDepartmentName from tbEmployeeAttendanceFinal "
						+" where dDate= '"+dDbFormat.format(dDate.getValue())+"' and vUnitId like '"+cmbUnitName.getValue()+"' "
						+" order by vDepartmentName";
			}
			
			System.out.println("cmbDepartmentData: "+sql);
			
			
			List <?> list = session.createSQLQuery(sql).list();
			if(!list.isEmpty())
			{
				for(Iterator <?> iter=list.iterator();iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();
					cmbDepartmentName.addItem(element[0]);
					cmbDepartmentName.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbDepartmentData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			session.close();
		}
	}
	public void cmbSectionData()
	{
		String sql="";
		cmbSectionName.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			if(opgTimeSelect.getValue().toString()=="Monthly")
			{
				sql = "select distinct vSectionId,vSectionName from tbEmployeeAttendanceFinal "
					  +"where MONTH(dDate) = MONTH('"+dMonth.getValue()+"') and YEAR(dDate) = YEAR('"+dMonth.getValue()+"') " 
					  +"and vUnitId='"+cmbUnitName.getValue().toString()+"' and vDepartmentId like '"+(chkDepartmentAll.booleanValue()?"%":cmbDepartmentName.getValue()==null?"%":cmbDepartmentName.getValue())+"' "
					  +"order by vSectionName";
			}
			else
			{
				sql = "select distinct vSectionId,vSectionName from tbEmployeeAttendanceFinal "
						+"where dDate= '"+dDbFormat.format(dDate.getValue())+"' "
						+" and vUnitId='"+cmbUnitName.getValue().toString()+"' "
						+" and vDepartmentId like '"+(chkDepartmentAll.booleanValue()?"%":cmbDepartmentName.getValue()==null?"%":cmbDepartmentName.getValue())+"' "
						+" order by vSectionName";
			}
			
			System.out.println("cmbSectionData: "+sql);
			
			
			List <?> list = session.createSQLQuery(sql).list();
			if(!list.isEmpty())
			{
				for(Iterator <?> iter=list.iterator();iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();
					cmbSectionName.addItem(element[0]);
					cmbSectionName.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbSectionData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			session.close();
		}
	}
	public void opgActionWiseVisible(boolean b)
	{
		lblMonth.setVisible(b);
	    dMonth.setVisible(b);
	    lblFromDate.setVisible(!b);
	    dDate.setVisible(!b);
	}
	private void opgTimeSelectAction()
	{
		if(opgTimeSelect.getValue().toString()=="Monthly")
		{
			opgActionWiseVisible(true);
			dDate.setValue(new Date());
			dMonth.setValue(new Date());
			
			cmbUnitName.removeAllItems();
			chkDepartmentAll.setValue(false);
			cmbDepartmentName.setEnabled(true);
			cmbDepartmentName.removeAllItems();
			chkEmployeeAll.setValue(false);
			chkEmployeeAll.setEnabled(false);
			if(dMonth.getValue()!=null)
			{
				cmbUnitDataLoad();
			}
		}
		else
		{
			opgActionWiseVisible(false);
			dDate.setValue(new Date());
			dMonth.setValue(new Date());
			
			cmbUnitName.removeAllItems();
			chkDepartmentAll.setValue(false);
			cmbDepartmentName.setEnabled(true);
			cmbDepartmentName.removeAllItems();
			chkEmployeeAll.setValue(false);
			chkEmployeeAll.setEnabled(true);
			if(dDate.getValue()!=null)
			{
				cmbUnitDataLoad();
			}
		}
	}

	private void cmbMonthDataLoad()
	{
		dMonth.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct 0,CONVERT(date, (SELECT DATEADD(s,-1,DATEADD(mm, DATEDIFF(m,0,dDate)+1,0)))) dDate from tbEmployeeAttendanceFinal " +
					"order by dDate desc";
			
			System.out.println("cmbAttandanceDateData: "+query);
			
			List <?> list = session.createSQLQuery(query).list();
			if(!list.isEmpty())
			{
				for(Iterator <?> iter=list.iterator();iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();
					dMonth.addItem(element[1]);
					dMonth.setItemCaption(element[1], dfMonthYear.format(element[1]));
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbAttandanceDateData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			session.close();
		}
	}
	
	private void buttonAction()
	{
		opgTimeSelect.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				tableclear();
				opgTimeSelectAction();
			}
		});
		
		dMonth.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(opgTimeSelect.getValue().toString()=="Monthly")
				{
					if(dMonth.getValue()!=null)
					{
						tableclear();
						cmbUnitName.removeAllItems();
						cmbDepartmentName.removeAllItems();
						cmbSectionName.removeAllItems();
						cmbUnitDataLoad();
					}
				}
				else
				{
					if(dDate.getValue()!=null )
					{
						tableclear();
						cmbUnitName.removeAllItems();
						cmbDepartmentName.removeAllItems();
						cmbSectionName.removeAllItems();
						cmbUnitDataLoad();
					}
				}
			}
		});
		dDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(opgTimeSelect.getValue().toString()=="Monthly")
				{
					if(dMonth.getValue()!=null)
					{
						cmbUnitName.removeAllItems();
						cmbDepartmentName.removeAllItems();
						cmbSectionName.removeAllItems();
						cmbUnitDataLoad();
						tableclear();
					}
				}
				else
				{
					if(dDate.getValue()!=null)
					{
						cmbUnitName.removeAllItems();
						cmbDepartmentName.removeAllItems();
						cmbSectionName.removeAllItems();
						cmbUnitDataLoad();
						tableclear();
					}
				}
			}
		});
		cmbUnitName.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				cmbDepartmentName.removeAllItems();
				chkDepartmentAll.setValue(false);
				cmbDepartmentName.setEnabled(true);

				cmbSectionName.removeAllItems();
				chkSectionAll.setValue(false);
				cmbSectionName.setEnabled(true);

				cmbEmployeeID.removeAllItems();				
				chkEmployeeAll.setValue(false);
				cmbEmployeeID.setEnabled(true);
				if(opgTimeSelect.getValue().toString()=="Monthly")
				{
					if(dMonth.getValue()!=null)
					{
						if(cmbUnitName.getValue()!=null)
						{
							cmbDepartmentData();
							tableclear();
						}
					}
				}
				else
				{
					if(dDate.getValue()!=null)
					{
						if(cmbUnitName.getValue()!=null)
						{
							cmbDepartmentData();
							tableclear();
						}
					}
				}
			}
		});
		cmbDepartmentName.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				tableclear();
				cmbSectionName.removeAllItems();
				chkSectionAll.setValue(false);
				cmbSectionName.setEnabled(true);

				cmbEmployeeID.removeAllItems();				
				chkEmployeeAll.setValue(false);
				cmbEmployeeID.setEnabled(true);
				if(opgTimeSelect.getValue().toString()=="Monthly")
				{
					if(dMonth.getValue()!=null)
					{
						if(cmbUnitName.getValue()!=null )
						{
							if(cmbDepartmentName.getValue()!=null)
							{
								tableclear();
								cmbSectionData();
							}
						}
					}
				}
				else
				{
					if(dDate.getValue()!=null)
					{
						if(cmbUnitName.getValue()!=null)
						{
							if(cmbDepartmentName.getValue()!=null)
							{
								tableclear();
								cmbSectionData();
							}
						}
					}
				}
			}
		});
		chkDepartmentAll.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				
				cmbSectionName.removeAllItems();
				chkSectionAll.setValue(false);
				cmbSectionName.setEnabled(true);
				cmbEmployeeID.removeAllItems();
				chkEmployeeAll.setValue(false);
				
				if(chkDepartmentAll.booleanValue())
				{
					cmbDepartmentName.setValue(null);
					cmbDepartmentName.setEnabled(false);
					if(opgTimeSelect.getValue().toString()=="Monthly")
					{
						if(dMonth.getValue()!=null)
						{
							if(cmbUnitName.getValue()!=null)
							{
								cmbDepartmentName.setValue(null);
								cmbDepartmentName.setEnabled(false);
								cmbSectionData();
							}
						}
					}
					else
					{
						if(dDate.getValue()!=null)
						{
							if(cmbUnitName.getValue()!=null)
							{
								cmbDepartmentName.setValue(null);
								cmbDepartmentName.setEnabled(false);
								cmbSectionData();
							}
						}
					}
				}
				else{
					cmbDepartmentName.setEnabled(true);
				}
			}
		});
		cmbSectionName.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				tableclear();	
				cmbEmployeeID.removeAllItems();
				chkEmployeeAll.setValue(false);
				cmbEmployeeID.setEnabled(true);
				if(opgTimeSelect.getValue().toString()=="Monthly")
				{
					if(dMonth.getValue()!=null)
					{
						if(cmbDepartmentName.getValue()!=null || chkDepartmentAll.booleanValue())
						{
							if(cmbSectionName.getValue()!=null)
							{
								cmbEmployeeID.removeAllItems();
								employeeDataLoad();
							}
						}
					}
				}
				else
				{
					if(dDate.getValue()!=null)
					{
						if(cmbDepartmentName.getValue()!=null  || chkDepartmentAll.booleanValue())
						{
							if(cmbSectionName.getValue()!=null)
							{
								tableclear();
								employeeDataLoad();
							}
						}
					}
				}
			}
		});
		chkSectionAll.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				cmbEmployeeID.removeAllItems();
				chkEmployeeAll.setValue(false);
				cmbEmployeeID.setEnabled(true);
				if(chkSectionAll.booleanValue())
				{
					cmbSectionName.setValue(null);
					cmbSectionName.setEnabled(false);
					if(opgTimeSelect.getValue().toString()=="Monthly")
					{
						if(dMonth.getValue()!=null)
						{
							if(cmbDepartmentName.getValue()!=null || chkDepartmentAll.booleanValue())
							{
								cmbSectionName.setValue(null);
								cmbSectionName.setEnabled(false);
								employeeDataLoad();
							}
						}
					}
					else
					{
						if(dDate.getValue()!=null)
						{
							if(cmbDepartmentName.getValue()!=null || chkDepartmentAll.booleanValue())
							{
								cmbSectionName.setValue(null);
								cmbSectionName.setEnabled(false);
								employeeDataLoad();
							}
						}
					}
				}
				else{
					cmbSectionName.setEnabled(true);
				}
			}
		});
		cmbEmployeeID.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				tableclear();				
				if(opgTimeSelect.getValue().toString()=="Monthly")
				{
					if(dMonth.getValue()!=null)
					{
						if(cmbUnitName.getValue()!=null)
						{
							if(cmbDepartmentName.getValue()!=null || chkDepartmentAll.booleanValue())
							{
								if(cmbSectionName.getValue()!=null || chkSectionAll.booleanValue())
								{
									if(cmbEmployeeID.getValue()!=null)
									{
										tableDataAdd();
									}
								}
							}
						}
					}
				}
				else
				{
					if(dDate.getValue()!=null)
					{
						if(cmbUnitName.getValue()!=null)
						{
							if(cmbDepartmentName.getValue()!=null || chkDepartmentAll.booleanValue())
							{
								if(cmbSectionName.getValue()!=null || chkSectionAll.booleanValue())
								{
									if(cmbEmployeeID.getValue()!=null)
									{
										tableDataAdd();
									}
								}
							}
						}
					}
				}
			}
		});
		chkEmployeeAll.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				
				if(chkEmployeeAll.booleanValue())
				{
					cmbEmployeeID.setValue(null);
					cmbEmployeeID.setEnabled(false);
					if(opgTimeSelect.getValue().toString()=="Monthly")
					{
						if(dMonth.getValue()!=null)
						{
							if(cmbUnitName.getValue()!=null)
							{
								if(cmbDepartmentName.getValue()!=null || chkDepartmentAll.booleanValue())
								{
									tableDataAdd();
								}
							}
						}
					}
					else
					{
						if(dDate.getValue()!=null)
						{
							if(cmbUnitName.getValue()!=null)
							{
								if(cmbDepartmentName.getValue()!=null || chkDepartmentAll.booleanValue())
								{
									tableDataAdd();
								}
							}
						}
					}
				}
				else{
					cmbEmployeeID.setEnabled(true);
				}
			}
		});


		
		button.btnNew.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isFind = false;
				newButtonEvent();
			}
		});

		/*button.btnFind.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isFind = true;
			}
		});*/

		/*button.btnEdit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				updateAction();
			}
		});*/
		button.btnSave.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				formValidation();
			}
		});

		button.btnRefresh.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				isFind=false;
				refreshButtonEvent();
			}
		});

		button.btnExit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});
	}

	private void tableDataAdd()
	{
		String Query="",section="%",employee="%",dateType="",deptId="%";
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		if(!chkDepartmentAll.booleanValue())
		{
			deptId=cmbDepartmentName.getValue().toString();
		}
		if(!chkSectionAll.booleanValue())
		{
			section=cmbSectionName.getValue().toString();
		}
		if(!chkEmployeeAll.booleanValue())
		{
			employee=cmbEmployeeID.getValue().toString();
		}
		try
		{
			if(opgTimeSelect.getValue().toString()=="Monthly")
			{
				dateType="Monthly";
				
				if(dMonth.getValue()!=null)
				{
					Query="select distinct vEmployeeID,vEmployeeCode,vFingerID,vEmployeeName,vDesignationID,vDesignationName,"+
							"dAttendanceDate,txtDay,vUnitID,vUnitName,vDepartmentID,vDepartmentName,inHour,"+
							"inMinute,inSecond,outHour,outMinute,outSecond,SUBSTRING(vEmployeeCode,3,100)code from funEditDailyOrMonthlyEmployeeAttendance" +
							"('"+dMonth.getValue()+"','"+cmbUnitName.getValue()+"','"+deptId+"','"+section+"','"+employee+"','"+dateType+"') " +
							"where dAttendanceDate<convert(date,getdate()) order by code asc";
				}
			}
			else
			{
				dateType="Date";
				
				if(dDate.getValue()!=null)
				{
					Query="select vEmployeeID,vEmployeeCode,vFingerID,vEmployeeName,vDesignationID,vDesignationName,"+
							"dAttendanceDate,txtDay,vUnitID,vUnitName,vDepartmentID,vDepartmentName,inHour,"+
							"inMinute,inSecond,outHour,outMinute,outSecond,SUBSTRING(vEmployeeCode,3,100)code from funEditDailyOrMonthlyEmployeeAttendance" +
							"('"+sessionBean.dfDb.format(dDate.getValue())+"','"+cmbUnitName.getValue()+"','"+deptId+"','"+section+"','"+employee+"','"+dateType+"') " +
							"where dAttendanceDate<convert(date,getdate()) order by code asc";
				}
			}
			
			System.out.println("tableDataAdd: "+Query);
			
			List <?> lst=session.createSQLQuery(Query).list();
			if(!lst.isEmpty())
			{
				int i=0;
				for(Iterator <?> itr=lst.iterator(); itr.hasNext();)
				{
					Object [] element=(Object[]) itr.next();
					lblAutoEmpID.get(i).setValue(element[0]);
					lblempID.get(i).setValue(element[1]);
					lbFingerID.get(i).setValue(element[2]);
					lbEmployeeName.get(i).setValue(element[3]);
					lblDesignationID.get(i).setValue(element[4]);
					lbDesignation.get(i).setValue(element[5]);
					lbAttendDate.get(i).setValue(element[6]);

					lbUnitId.get(i).setValue(element[8]);
					lbUnitName.get(i).setValue(element[9]);
					lbDeptId.get(i).setValue(element[10]);
					lbDeptName.get(i).setValue(element[11]);

					if(!element[12].toString().equals("0"))
					{
						InTime1.get(i).setValue(element[12]);
						InTime2.get(i).setValue(element[13]);
						InTime3.get(i).setValue(element[14]);
					}
					if(!element[15].toString().equals("0"))
					{
						OutTime1.get(i).setValue(element[15]);
						OutTime2.get(i).setValue(element[16]);
						OutTime3.get(i).setValue(element[17]);
					}

					if(i==lblAutoEmpID.size()-1)
						tableRowAdd(i+1);

					i++;
				}
			}
			else
			{
				showNotification("Warning", "No Data Found!!!", Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception exp)
		{
			showNotification("tableDataAdd", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			session.close();
		}
	}

	private void tableInitialize()
	{
		table.setColumnCollapsingAllowed(true);
		table.setWidth("99%");
		table.setHeight("330px");
		table.setPageLength(0);

		table.addContainerProperty("Delete", NativeButton.class , new NativeButton());
		table.setColumnWidth("Delete",45);

		table.addContainerProperty("SL #", Label.class , new Label());
		table.setColumnWidth("SL #",20);

		table.addContainerProperty("EMP ID", Label.class, new Label());
		table.setColumnWidth("EMP ID", 100);

		table.addContainerProperty("Employee ID", Label.class, new Label());
		table.setColumnWidth("Employee ID", 100);

		table.addContainerProperty("Finger ID", Label.class , new Label());
		table.setColumnWidth("Finger ID",80);

		table.addContainerProperty("Employee Name", Label.class , new Label());
		table.setColumnWidth("Employee Name",170);

		table.addContainerProperty("Designation ID", Label.class , new Label());
		table.setColumnWidth("Designation ID",110);

		table.addContainerProperty("Designation", Label.class , new Label());
		table.setColumnWidth("Designation",110);	

		table.addContainerProperty("Attend date", Label.class , new Label());
		table.setColumnWidth("Attend date",100);

		table.addContainerProperty("HH (IN)", AmountField.class , new AmountField());
		table.setColumnWidth("HH (IN)",30);

		table.addContainerProperty("Min (IN)", AmountField.class , new AmountField());
		table.setColumnWidth("Min (IN)",30);

		table.addContainerProperty("Dept (IN)", AmountField.class , new AmountField());
		table.setColumnWidth("Dept (IN)",30);

		table.addContainerProperty("HH (OUT)", AmountField.class , new AmountField());
		table.setColumnWidth("HH (OUT)",30);

		table.addContainerProperty("Min (OUT)", AmountField.class , new AmountField());
		table.setColumnWidth("Min (OUT)",30);

		table.addContainerProperty("Dept (OUT)", AmountField.class , new AmountField());
		table.setColumnWidth("Dept (OUT)",30);

		table.addContainerProperty("Permitted By", TextField.class , new TextField());
		table.setColumnWidth("Permitted By",120);

		table.addContainerProperty("Reason", TextField.class , new TextField());
		table.setColumnWidth("Reason",105);	

		table.addContainerProperty("Project Id", Label.class , new Label());
		table.setColumnWidth("Project Id",50);

		table.addContainerProperty("Project Name", Label.class , new Label());
		table.setColumnWidth("Project Name",120);

		table.addContainerProperty("Department Id", Label.class , new Label());
		table.setColumnWidth("Department Id",50);

		table.addContainerProperty("select", CheckBox.class , new CheckBox());
		table.setColumnWidth("select",50);

		table.addContainerProperty("Department Name", Label.class , new Label());
		table.setColumnWidth("Department Name",120);

		table.setColumnCollapsed("EMP ID", true);
		table.setColumnCollapsed("Finger ID", true);
		table.setColumnCollapsed("Designation ID", true);
		table.setColumnCollapsed("Dept (IN)", true);
		table.setColumnCollapsed("Dept (OUT)", true);
		table.setColumnCollapsed("p", true);
		table.setColumnCollapsed("Project Id", true);
		table.setColumnCollapsed("Project Name", true);
		table.setColumnCollapsed("Department Id", true);
		table.setColumnCollapsed("Department Name", true);
		
		table.setStyleName("wordwrap-headers");

		table.setColumnAlignments(new String[] {Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_LEFT,Table.ALIGN_CENTER,
				Table.ALIGN_CENTER,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_CENTER,
				Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,
				Table.ALIGN_CENTER,Table.ALIGN_CENTER ,Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,
				Table.ALIGN_CENTER});

		rowAddinTable();
	}

	public void rowAddinTable()
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
				MessageBox mb = new MessageBox(getParent(),"Are You Sure?",MessageBox.Icon.QUESTION,"Do you want to Delete this Row?",new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"),new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
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
								String del="delete from tbEmployeeAttendanceFinal where vEmployeeID='"+lblAutoEmpID.get(ar)+"' and dDate='"+lbAttendDate.get(ar)+"' ";
								System.out.println("DEL: "+del);
								session.createSQLQuery(del).executeUpdate(); 
								Notification n=new Notification("Row Delete Successfully!","",Notification.TYPE_TRAY_NOTIFICATION);
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
							
							//System.out.println("Date: "+lbAttendDate.get(ar));
							
							lblempID.get(ar).setValue("");
							lblAutoEmpID.get(ar).setValue("");
							lbFingerID.get(ar).setValue("");
							lbEmployeeName.get(ar).setValue("");
							lblDesignationID.get(ar).setValue("");
							lbDesignation.get(ar).setValue("");
							lbAttendDate.get(ar).setReadOnly(false);
							lbAttendDate.get(ar).setValue(null);
							lbAttendDate.get(ar).setReadOnly(true);
							txtPermitBy.get(ar).setValue("");
							txtReason.get(ar).setValue("");
							txtReason.get(ar).setValue("");
							InTime1.get(ar).setValue("");
							InTime2.get(ar).setValue("");
							InTime3.get(ar).setValue("");
							OutTime1.get(ar).setValue("");
							OutTime2.get(ar).setValue("");
							OutTime3.get(ar).setValue("");
							lbUnitId.get(ar).setValue("");
							lbUnitName.get(ar).setValue("");
							lbDeptId.get(ar).setValue("");
							chkAll.get(ar).setValue(false);
							lbDeptName.get(ar).setValue("");

							for(int rowcount=ar;rowcount<=lblAutoEmpID.size()-1;rowcount++)
							{
								if(rowcount+1<=lblAutoEmpID.size()-1)
								{
									if(!lblAutoEmpID.get(rowcount+1).getValue().toString().equals(""))
									{
										lblAutoEmpID.get(rowcount).setValue(lblAutoEmpID.get(rowcount+1).getValue().toString());
										lblempID.get(rowcount).setValue(lblempID.get(rowcount+1).getValue().toString());
										lbFingerID.get(rowcount).setValue(lbFingerID.get(rowcount+1).getValue().toString());
										lbEmployeeName.get(rowcount).setValue(lbEmployeeName.get(rowcount+1).getValue().toString());
										lblDesignationID.get(rowcount).setValue(lblDesignationID.get(rowcount+1).getValue().toString());
										lbDesignation.get(rowcount).setValue(lbDesignation.get(rowcount+1).getValue().toString());
										lbAttendDate.get(rowcount).setReadOnly(false);
										lbAttendDate.get(rowcount).setValue(lbAttendDate.get(rowcount+1).getValue());
										lbAttendDate.get(rowcount).setReadOnly(false);
										txtPermitBy.get(rowcount).setValue(txtPermitBy.get(rowcount+1).getValue().toString());
										txtReason.get(rowcount).setValue(txtReason.get(rowcount+1).getValue().toString());
										InTime1.get(rowcount).setValue(InTime1.get(rowcount+1).getValue().toString());
										InTime2.get(rowcount).setValue(InTime2.get(rowcount+1).getValue().toString());
										InTime3.get(rowcount).setValue(InTime3.get(rowcount+1).getValue().toString());
										OutTime1.get(rowcount).setValue(OutTime1.get(rowcount+1).getValue().toString());
										OutTime2.get(rowcount).setValue(OutTime2.get(rowcount+1).getValue().toString());
										OutTime3.get(rowcount).setValue(OutTime3.get(rowcount+1).getValue().toString());
										lbUnitId.get(rowcount).setValue(lbDeptId.get(rowcount+1).getValue().toString());
										lbUnitName.get(rowcount).setValue(lbUnitName.get(rowcount+1).getValue().toString());
										lbDeptId.get(rowcount).setValue(lbDeptId.get(rowcount+1).getValue().toString());
										chkAll.get(rowcount).setValue(chkAll.get(rowcount+1).getValue());
										lbDeptName.get(rowcount).setValue(lbDeptName.get(rowcount+1).getValue().toString());

										lblAutoEmpID.get(rowcount+1).setValue("");
										lblempID.get(rowcount+1).setValue("");
										lbFingerID.get(rowcount+1).setValue("");
										lbEmployeeName.get(rowcount+1).setValue("");
										lblDesignationID.get(rowcount+1).setValue("");
										lbDesignation.get(rowcount+1).setValue("");
										lbAttendDate.get(rowcount+1).setValue("");
										txtPermitBy.get(rowcount+1).setValue("");
										txtReason.get(rowcount+1).setValue("");
										InTime1.get(rowcount+1).setValue("");
										InTime2.get(rowcount+1).setValue("");
										InTime3.get(rowcount+1).setValue("");
										OutTime1.get(rowcount+1).setValue("");
										OutTime2.get(rowcount+1).setValue("");
										OutTime3.get(rowcount+1).setValue("");
										lbUnitId.get(rowcount+1).setValue("");
										lbUnitName.get(rowcount+1).setValue("");
										lbDeptId.get(rowcount+1).setValue("");
										chkAll.get(rowcount+1).setValue(false);
										lbDeptName.get(rowcount+1).setValue("");
									}
								}
							}
							
							
						}
					}
				});
			}
		});

		lbSl.add(ar, new Label(""));
		lbSl.get(ar).setWidth("100%");
		lbSl.get(ar).setHeight("20px");
		lbSl.get(ar).setValue(ar+1);

		lblAutoEmpID.add(ar, new Label());
		lblAutoEmpID.get(ar).setImmediate(true);
		lblAutoEmpID.get(ar).setWidth("100%");
		lblAutoEmpID.get(ar).setHeight("20px");

		lblempID.add(ar,new Label(""));
		lblempID.get(ar).setImmediate(true);
		lblempID.get(ar).setWidth("100%");
		lblempID.get(ar).setHeight("20px");

		lbFingerID.add(ar, new Label(""));
		lbFingerID.get(ar).setWidth("100%");
		lbFingerID.get(ar).setImmediate(true);
		lbFingerID.get(ar).setHeight("-1px");

		lbEmployeeName.add(ar, new Label(""));
		lbEmployeeName.get(ar).setWidth("100%");
		lbEmployeeName.get(ar).setImmediate(true);

		lblDesignationID.add(ar, new Label(""));
		lblDesignationID.get(ar).setWidth("100%");
		lblDesignationID.get(ar).setImmediate(true);

		lbDesignation.add(ar, new Label(""));
		lbDesignation.get(ar).setWidth("100%");
		lbDesignation.get(ar).setImmediate(true);

		lbAttendDate.add(ar, new Label());
		lbAttendDate.get(ar).setImmediate(true);
		

		InTime1.add(ar, new AmountField());
		InTime1.get(ar).setWidth("28px");
		InTime1.get(ar).setInputPrompt("hh");
		InTime1.get(ar).setImmediate(true);
		InTime1.get(ar).setStyleName("Intime");
		InTime1.get(ar).setMaxLength(2);
		InTime1.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!InTime1.get(ar).getValue().toString().isEmpty())
				{			
					if(Double.parseDouble(InTime1.get(ar).getValue().toString())>23)
					{
						InTime1.get(ar).setValue("");
					}
					else
					{
						InTime1.get(ar).setValue(InTime1.get(ar).getValue().toString());
					}
				}
			}
		});

		InTime2.add(ar, new AmountField());
		InTime2.get(ar).setWidth("28px");
		InTime2.get(ar).setInputPrompt("mm");
		InTime2.get(ar).setImmediate(true);
		InTime2.get(ar).setStyleName("Intime");
		InTime2.get(ar).setMaxLength(2);
		InTime2.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!InTime2.get(ar).getValue().toString().isEmpty())
				{			
					if(Double.parseDouble(InTime2.get(ar).getValue().toString())>59)
					{
						InTime2.get(ar).setValue("");
					}
					else{
						InTime2.get(ar).setValue(InTime2.get(ar).getValue().toString());
					}
				}
			}
		});

		InTime3.add(ar, new AmountField());
		InTime3.get(ar).setWidth("28px");
		InTime3.get(ar).setInputPrompt("ss");
		InTime3.get(ar).setImmediate(true);
		InTime3.get(ar).setStyleName("Intime");
		InTime3.get(ar).setMaxLength(2);
		InTime3.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!InTime3.get(ar).getValue().toString().isEmpty())
				{			
					if(Double.parseDouble(InTime3.get(ar).getValue().toString())>59)
					{
						InTime3.get(ar).setValue("");
					}
					else{
						InTime3.get(ar).setValue(InTime3.get(ar).getValue().toString());
					}
				}
			}
		});
		InTime3.get(ar).setEnabled(false);

		OutTime1.add(ar, new AmountField());
		OutTime1.get(ar).setWidth("28px");
		OutTime1.get(ar).setInputPrompt("hh");
		OutTime1.get(ar).setImmediate(true);
		OutTime1.get(ar).addStyleName("Outtime");
		OutTime1.get(ar).setMaxLength(2);
		OutTime1.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!OutTime1.get(ar).getValue().toString().isEmpty())
				{			
					if(Double.parseDouble(OutTime1.get(ar).getValue().toString())>23)
					{
						OutTime1.get(ar).setValue("");
					}
					else{
						OutTime1.get(ar).setValue(OutTime1.get(ar).getValue().toString());
					}
				}
			}
		});

		OutTime2.add(ar, new AmountField());
		OutTime2.get(ar).setWidth("28px");
		OutTime2.get(ar).setInputPrompt("mm");
		OutTime2.get(ar).setImmediate(true);
		OutTime2.get(ar).setStyleName("Outtime");
		OutTime2.get(ar).setMaxLength(2);
		OutTime2.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!OutTime2.get(ar).getValue().toString().isEmpty())
				{			
					if(Double.parseDouble(OutTime2.get(ar).getValue().toString())>59)
					{
						OutTime2.get(ar).setValue("");
					}
					else{
						OutTime2.get(ar).setValue(OutTime2.get(ar).getValue().toString());
					}
				}
			}
		});

		OutTime3.add(ar, new AmountField());
		OutTime3.get(ar).setWidth("28px");
		OutTime3.get(ar).setInputPrompt("ss");
		OutTime3.get(ar).setImmediate(true);
		OutTime3.get(ar).setStyleName("Outtime");
		OutTime3.get(ar).setMaxLength(2);
		OutTime3.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!OutTime3.get(ar).getValue().toString().isEmpty())
				{			
					if(Double.parseDouble(OutTime3.get(ar).getValue().toString())>59)
					{
						OutTime3.get(ar).setValue("");
					}
					else{
						OutTime3.get(ar).setValue(OutTime3.get(ar).getValue().toString());
					}
				}
			}
		});
		OutTime3.get(ar).setEnabled(false);

		txtPermitBy.add(ar, new TextField(""));
		txtPermitBy.get(ar).setWidth("100%");
		txtPermitBy.get(ar).setImmediate(true);
		txtPermitBy.get(ar).setHeight("-1px");

		txtReason.add(ar, new TextField(""));
		txtReason.get(ar).setWidth("100%");
		txtReason.get(ar).setImmediate(true);

		lbUnitId.add(ar, new Label(""));
		lbUnitId.get(ar).setWidth("100%");
		lbUnitId.get(ar).setImmediate(true);
		lbUnitId.get(ar).setHeight("-1px");

		lbUnitName.add(ar, new Label(""));
		lbUnitName.get(ar).setWidth("100%");
		lbUnitName.get(ar).setImmediate(true);
		lbUnitName.get(ar).setHeight("-1px");

		lbDeptId.add(ar, new Label(""));
		lbDeptId.get(ar).setWidth("100%");
		lbDeptId.get(ar).setImmediate(true);
		lbDeptId.get(ar).setHeight("-1px");

		chkAll.add(ar, new CheckBox());
		chkAll.get(ar).setWidth("100%");
		chkAll.get(ar).setImmediate(true);
		chkAll.get(ar).setHeight("-1px");

		lbDeptName.add(ar, new Label(""));
		lbDeptName.get(ar).setWidth("100%");
		lbDeptName.get(ar).setImmediate(true);
		lbDeptName.get(ar).setHeight("-1px");

		table.addItem(new Object[]{Delete.get(ar),lbSl.get(ar),lblAutoEmpID.get(ar),lblempID.get(ar),lbFingerID.get(ar),
				lbEmployeeName.get(ar),lblDesignationID.get(ar),lbDesignation.get(ar),lbAttendDate.get(ar),InTime1.get(ar),
				InTime2.get(ar),InTime3.get(ar),OutTime1.get(ar),OutTime2.get(ar),OutTime3.get(ar),txtPermitBy.get(ar),
				txtReason.get(ar),lbUnitId.get(ar),lbUnitName.get(ar),lbDeptId.get(ar),chkAll.get(ar),lbDeptName.get(ar)},ar);
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
		dMonth.setEnabled(!b);
		dDate.setEnabled(!b);

		if(isFind==false)
		{
			cmbUnitName.setEnabled(!b);
			cmbDepartmentName.setEnabled(!b);
			chkDepartmentAll.setEnabled(!b);
			cmbSectionName.setEnabled(!b);
			chkSectionAll.setEnabled(!b);

			cmbEmployeeID.setEnabled(!b);
			chkEmployeeAll.setEnabled(!b);
		}
		else
		{
			cmbUnitName.setEnabled(b);
			cmbDepartmentName.setEnabled(b);
			chkDepartmentAll.setEnabled(b);
			
			cmbSectionName.setEnabled(!b);
			chkSectionAll.setEnabled(!b);

			cmbEmployeeID.setEnabled(b);
			chkEmployeeAll.setEnabled(b);
		}
		table.setEnabled(!b);
	}

	private void txtClear()
	{
		cmbUnitName.setValue(null);
		cmbDepartmentName.setValue(null);
		chkDepartmentAll.setValue(false);
		cmbSectionName.setValue(null);
		chkSectionAll.setValue(false);

		cmbEmployeeID.setValue(null);
		chkEmployeeAll.setValue(false);
		tableclear();
	}

	private void tableclear()
	{
		for(int i =0; i<lbFingerID.size(); i++)
		{
			lblAutoEmpID.get(i).setValue("");
			lblempID.get(i).setValue("");
			lbFingerID.get(i).setValue("");
			lbEmployeeName.get(i).setValue("");
			lbDesignation.get(i).setValue("");
			txtPermitBy.get(i).setValue("");
			txtReason.get(i).setValue("");
			txtPermitBy.get(i).setValue("");
			InTime1.get(i).setValue("");
			InTime2.get(i).setValue("");
			InTime3.get(i).setValue("");
			OutTime1.get(i).setValue("");
			OutTime2.get(i).setValue("");
			OutTime3.get(i).setValue("");
			lbUnitName.get(i).setValue("");
			lbDeptId.get(i).setValue("");
			chkAll.get(i).setValue(false);
			lbDeptName.get(i).setValue("");
			lbAttendDate.get(i).setValue("");
		}
	}

	private void focusEnter()
	{
		allComp.add(dMonth);
		allComp.add(cmbUnitName);
		allComp.add(cmbDepartmentName);

		for(int i=0; i<lbEmployeeName.size();i++)
		{
			allComp.add(InTime1.get(i));
			allComp.add(InTime2.get(i));
			allComp.add(OutTime1.get(i));
			allComp.add(OutTime2.get(i));
			allComp.add(txtPermitBy.get(i));
			allComp.add(txtReason.get(i));
		}

		allComp.add(button.btnSave);

		new FocusMoveByEnter(this,allComp);
	}

	public boolean validTableSelect()
	{
		boolean ret=false;
		for(int i=0; i<lbFingerID.size(); i++)
		{
			if(!lblAutoEmpID.get(i).getValue().toString().isEmpty() && 
					!lbDesignation.get(i).getValue().toString().isEmpty() && !txtPermitBy.get(i).getValue().toString().isEmpty() &&
					!txtReason.get(i).getValue().toString().isEmpty())
			{
				if(	!InTime1.get(i).getValue().toString().isEmpty())
				{
					if(!InTime2.get(i).getValue().toString().isEmpty())
					{
						if(!OutTime1.get(i).getValue().toString().isEmpty())
						{
							if(!OutTime2.get(i).getValue().toString().isEmpty())
							{
								ret=true;
							}
							else
							{
								ret = false;
								OutTime2.get(i).focus();
								showNotification("Warning", "Provide OutTime!!!", Notification.TYPE_WARNING_MESSAGE);
							}
						}
						else
						{
							ret = false;
							OutTime1.get(i).focus();
							showNotification("Warning", "Provide OutTime!!!", Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else
					{
						ret = false;
						InTime2.get(i).focus();
						showNotification("Warning", "Provide InTime!!!", Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					ret = false;
					InTime1.get(i).focus();
					showNotification("Warning", "Provide InTime!!!", Notification.TYPE_WARNING_MESSAGE);
				}
			}

		}
		return ret;
	}

	private void updateAction() 
	{
		if (!lbFingerID.get(0).getValue().toString().isEmpty()) 
		{
			btnIni(false);
			componentIni(false);
		} 
		else
		{
			showNotification("Update Failed","There are no data for update.",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void refreshButtonEvent() 
	{
		componentIni(true);
		btnIni(true);
		txtClear();	
	}

	private void newButtonEvent() 
	{
		cmbUnitName.focus();
		componentIni(false);
		btnIni(false);
		txtClear();
	}

	private void saveButtonAction()
	{
			MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to save information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
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

	private void insertData ()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		Transaction tx=session.beginTransaction();
		try
		{
			String query = "";
			String query1 = "";
			String query2="";
			for(int i=0; i<lbFingerID.size(); i++)
			{
				if(!txtPermitBy.get(i).getValue().toString().trim().isEmpty() && !txtReason.get(i).getValue().toString().trim().isEmpty())
				{
					if(!InTime1.get(i).getValue().toString().isEmpty() && !InTime2.get(i).getValue().toString().isEmpty() &&
							!OutTime1.get(i).getValue().toString().isEmpty() && !OutTime2.get(i).getValue().toString().isEmpty())
					{

						int in_time=Integer.parseInt(InTime1.get(i).getValue().toString().trim());

						int out_time=Integer.parseInt(OutTime1.get(i).getValue().toString().trim());

						String intime=InTime1.get(i).getValue().toString()+":"+InTime2.get(i).getValue().toString()+":00";

						String outtime=OutTime1.get(i).getValue().toString()+":"+OutTime2.get(i).getValue().toString()+":00";

						List <?> lst=session.createSQLQuery("select * from tbEmployeeAttendanceFinal where dDate='"+lbAttendDate.get(i).getValue()+"' " +
								"and vEmployeeId='"+lblAutoEmpID.get(i).getValue().toString()+"'").list();
						session.clear();

						if(!lst.isEmpty())
						{
							query = "insert into tbUDEmployeeAttendance (dDate,vEmployeeID,vEmployeeCode,vFingerID,vEmployeeName,vDesignationID,"
									+ " vDesignation,vUnitID,vUnitName,vDepartmentID,vDepartmentName,dAttDate,dAttInTime,dAttOutTime,permittedBy,"
									+ " vReason,vShiftID,udFlag,vPAFlag,vUserID,vUserIP,dEntryTime) select dDate,vEmployeeID,vEmployeeCode,vFingerID,"
									+ " vEmployeeName,vDesignationID,vDesignationName,vUnitID,vUnitName,vDepartmentId,vDepartmentName,"
									+ " dDate,dInTimeFirst,dOutTimeFirst,'"+txtPermitBy.get(i).getValue()+"','"+txtReason.get(i).getValue()+"',"
									+ " vShiftID,'NEW',vAttendFlag,vUserID,vUserIp,dEntryTime from tbEmployeeAttendanceFinal where "
									+ " vEmployeeID='"+lblAutoEmpID.get(i).getValue().toString()+"' and dDate='"+lbAttendDate.get(i).getValue()+"'";
							System.out.println("query: "+query);
							
							session.createSQLQuery(query).executeUpdate();
							session.clear();


							query1="update tbEmployeeAttendanceFinal set " +
									/*"vShiftID=(select vShiftID from funEditDailyOrMonthlyEmployeeShift('"+lbAttendDate.get(i).getValue()+" "+intime+"'," +
									"'"+cmbUnitName.getValue()+"','"+cmbDepartmentName.getValue()+"'))," +
									"vShiftName=(select vShiftName from funEditDailyOrMonthlyEmployeeShift('"+lbAttendDate.get(i).getValue()+" "+intime+"'," +
									"'"+cmbUnitName.getValue()+"','"+cmbDepartmentName.getValue()+"'))," +*/
									"dInTimeFirst='"+lbAttendDate.get(i).getValue()+" "+intime+"'," +
									"dOutTimeFirst='"+lbAttendDate.get(i).getValue()+" "+outtime+"',vEditFlag='Edited'," +
									"vAttendFlag='',vUserID = '"+sessionBean.getUserName()+"',vUserIp = '"+sessionBean.getUserIp()+"'," +
									"dEntryTime = GETDATE() where vEmployeeID='"+lblAutoEmpID.get(i).getValue().toString()+"' and " +
									"dDate='"+lbAttendDate.get(i).getValue()+"'";
							System.out.println("query1: "+query1);
							
							if(in_time>out_time)
							{
								query1="update tbEmployeeAttendanceFinal set vShiftID=(select vShiftID from funEditDailyOrMonthlyEmployeeShift('"+lbAttendDate.get(i).getValue()+" "+intime+"'," +
										"'"+cmbUnitName.getValue()+"','"+cmbDepartmentName.getValue()+"')),vShiftName=(select vShiftName from funEditDailyOrMonthlyEmployeeShift('"+lbAttendDate.get(i).getValue()+" "+intime+"'," +
										"'"+cmbUnitName.getValue()+"','"+cmbDepartmentName.getValue()+"')),dInTimeFirst='"+lbAttendDate.get(i).getValue()+" "+intime+"'," +
										"dOutTimeFirst=DateAdd(dd,1,'"+lbAttendDate.get(i).getValue()+" "+outtime+"')," +
										"vEditFlag='Edited',vAttendFlag='',vUserID = '"+sessionBean.getUserName()+"',vUserIp = '"+sessionBean.getUserIp()+"'," +
										"dEntryTime = GETDATE() where vEmployeeID='"+lblAutoEmpID.get(i).getValue().toString()+"' " +
										"and dDate='"+lbAttendDate.get(i).getValue()+"'";
								System.out.println("query1 if in_time>out_time: "+query1);
							}

							query2 = "insert into tbUDEmployeeAttendance (dDate,vEmployeeID,vEmployeeCode,vFingerID,vEmployeeName,vDesignationID,"
									+ " vDesignation,vUnitID,vUnitName,vDepartmentID,vDepartmentName,dAttDate,dAttInTime,dAttOutTime,permittedBy,"
									+ " vReason,vShiftID,udFlag,vPAFlag,vUserID,vUserIP,dEntryTime) select dDate,vEmployeeID,vEmployeeCode,vFingerID,"
									+ " vEmployeeName,vDesignationID,vDesignationName,vUnitID,vUnitName,vDepartmentId,vDepartmentName,"
									+ " dDate,dInTimeFirst,dOutTimeFirst,'"+txtPermitBy.get(i).getValue()+"','"+txtReason.get(i).getValue()+"',"
									+ " vShiftID,'UPDATE',vAttendFlag,vUserID,vUserIp,dEntryTime from tbEmployeeAttendanceFinal where "
									+ " vEmployeeID='"+lblAutoEmpID.get(i).getValue().toString()+"' and dDate='"+lbAttendDate.get(i).getValue()+"'";
							System.out.println("query2: "+query2);
						}
						else
						{
							query1="insert into tbEmployeeAttendanceFinal (dDate,vEmployeeID,vEmployeeCode,vFingerID,vEmployeeName,vUnitID," +
									"vUnitName,vDepartmentId,vDepartmentName,vDesignationID,vDesignationName,iDesignationSerial,vShiftID,vShiftName," +
									"dInTimeFirst,dOutTimeFirst,vEditFlag,vAttendFlag,bOtStatus,vUserID,vUserIp,dEntryTime) values " +
									"('"+lbAttendDate.get(i).getValue()+"'," +
									"'"+lblAutoEmpID.get(i).getValue().toString()+"'," +
									"'"+lblempID.get(i).getValue().toString()+"'," +
									"'"+lbFingerID.get(i).getValue().toString()+"'," +
									"'"+lbEmployeeName.get(i).getValue().toString()+"'," +
									"'"+lbUnitId.get(i).getValue().toString()+"'," +
									"'"+lbUnitName.get(i).getValue().toString()+"'," +
									"'"+lbDeptId.get(i).getValue().toString()+"'," +
									"'"+lbDeptName.get(i).getValue().toString()+"'," +
									"'"+lblDesignationID.get(i).getValue().toString()+"'," +
									"'"+lbDesignation.get(i).getValue().toString()+"'," +
									"(select designationSerial from tbDesignationInfo where designationID='"+lblDesignationID.get(i).getValue().toString()+"')," +
									"(select vShiftID from funEditDailyOrMonthlyEmployeeShift('"+lbAttendDate.get(i).getValue()+" "+intime+"','"+cmbUnitName.getValue()+"','"+cmbDepartmentName.getValue()+"'))," +
									"(select vShiftName from funEditDailyOrMonthlyEmployeeShift('"+lbAttendDate.get(i).getValue()+" "+intime+"','"+cmbUnitName.getValue()+"','"+cmbDepartmentName.getValue()+"'))," +
									"'"+lbAttendDate.get(i).getValue()+" "+intime+"'," +
									"'"+lbAttendDate.get(i).getValue()+" "+outtime+"'," +
									"'Edited','',(select OtStatus from tbEmployeeInfo where " +
									"vEmployeeId='"+lblAutoEmpID.get(i).getValue().toString()+"'),'','"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"'," +
									"GETDATE())";	
							System.out.println("query1: "+query1);

							if(in_time>out_time)
							{
								query1="insert into tbEmployeeAttendanceFinal (dDate,vEmployeeID,vEmployeeCode,vFingerID,vEmployeeName,vUnitID," +
										"vUnitName,vDepartmentId,vDepartmentName,vDesignationID,vDesignationName,iDesignationSerial,vShiftID,vShiftName," +
										"dInTimeFirst,dOutTimeFirst,vEditFlag,vAttendFlag,bOtStatus,vUserID,vUserIp,dEntryTime) values " +
										"('"+lbAttendDate.get(i).getValue()+"'," +
										"'"+lblAutoEmpID.get(i).getValue().toString()+"'," +
										"'"+lblempID.get(i).getValue().toString()+"'," +
										"'"+lbFingerID.get(i).getValue().toString()+"'," +
										"'"+lbEmployeeName.get(i).getValue().toString()+"'," +
										"'"+lbUnitId.get(i).getValue().toString()+"'," +
										"'"+lbUnitName.get(i).getValue().toString()+"'," +
										"'"+lbDeptId.get(i).getValue().toString()+"'," +
										"'"+lbDeptName.get(i).getValue().toString()+"'," +
										"'"+lblDesignationID.get(i).getValue().toString()+"'," +
										"'"+lbDesignation.get(i).getValue().toString()+"'," +
										"(select designationSerial from tbDesignationInfo where designationID='"+lblDesignationID.get(i).getValue().toString()+"')," +
										"(select vShiftID from funEditDailyOrMonthlyEmployeeShift('"+lbAttendDate.get(i).getValue()+" "+intime+"','"+cmbUnitName.getValue()+"','"+cmbDepartmentName.getValue()+"'))," +
										"(select vShiftName from funEditDailyOrMonthlyEmployeeShift('"+lbAttendDate.get(i).getValue()+" "+intime+"','"+cmbUnitName.getValue()+"','"+cmbDepartmentName.getValue()+"'))," +
										"'"+lbAttendDate.get(i).getValue()+" "+intime+"'," +
										"DateAdd(dd,1,'"+lbAttendDate.get(i).getValue()+" "+outtime+"')," +
										"'Edited','',(select OtStatus from tbEmployeeInfo where " +
										"vEmployeeId='"+lblAutoEmpID.get(i).getValue().toString()+"'),'','"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"'," +
										"GETDATE())";
								System.out.println("query1: "+query1);
							}
							query2 = "insert into tbUDEmployeeAttendance (dDate,vEmployeeID,vEmployeeCode,vFingerID,vEmployeeName,vDesignationID,"
									+ " vDesignation,vUnitID,vUnitName,vDepartmentID,vDepartmentName,dAttDate,dAttInTime,dAttOutTime,permittedBy,"
									+ " vReason,vShiftID,udFlag,vPAFlag,vUserID,vUserIP,dEntryTime) select dDate,vEmployeeID,vEmployeeCode,vFingerID,"
									+ " vEmployeeName,vDesignationID,vDesignationName,vUnitID,vUnitName,vDepartmentId,vDepartmentName,"
									+ " dDate,dInTimeFirst,dOutTimeFirst,'"+txtPermitBy.get(i).getValue()+"','"+txtReason.get(i).getValue()+"',"
									+ " vShiftID,'UPDATE',vAttendFlag,vUserID,vUserIp,dEntryTime from tbEmployeeAttendanceFinal where "
									+ " vEmployeeID='"+lblAutoEmpID.get(i).getValue().toString()+"' and dDate='"+lbAttendDate.get(i).getValue()+"'";
							System.out.println("query2: "+query2);

						}
						session.createSQLQuery(query1).executeUpdate();
						session.createSQLQuery(query2).executeUpdate();
					}
				}
			}

			tx.commit();
			Notification n=new Notification("All Information Save Successfully!","",Notification.TYPE_TRAY_NOTIFICATION);
			n.setPosition(Notification.POSITION_TOP_RIGHT);
			showNotification(n);
			isFind = false;
			txtClear();
			componentIni(true);
			btnIni(true);
		}
		catch(Exception ex)
		{
			showNotification("insertData", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
		finally
		{
			session.close();
		}
	}

	private void formValidation()
	{
		if(cmbUnitName.getValue()!=null)
		{
			if(validTableSelect())
			{
				saveButtonAction();
			}
			else
			{
				showNotification("Warning!","Provide All Data To Table ",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else
		{
			showNotification("Warning,","Select Project Name.", Notification.TYPE_WARNING_MESSAGE);
			cmbUnitName.focus();
		}
	}

	private AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);

		setWidth("1180px");
		setHeight("530px");
		
		opgTimeSelect=new OptionGroup("",timeSelect);
		opgTimeSelect.select("Date");
		opgTimeSelect.setImmediate(true);
		opgTimeSelect.setStyleName("horizontal");
		mainLayout.addComponent(opgTimeSelect, "top:10.0px; left:130.0px;");

		// lblFromDate
		lblFromDate = new Label("Date :");
		lblFromDate.setImmediate(false);
		lblFromDate.setWidth("100.0%");
		lblFromDate.setHeight("-1px");
		mainLayout.addComponent(lblFromDate,"top:40.0px; left:20.0px;");
		lblFromDate.setVisible(true);
		// dDate
		dDate = new PopupDateField();
		dDate.setImmediate(true);
		dDate.setWidth("110px");
		dDate.setHeight("-1px");
		dDate.setDateFormat("dd-MM-yyyy");
		dDate.setValue(new java.util.Date());
		dDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dDate, "top:38.0px; left:130.0px;");
		dDate.setVisible(true);

		//lblMonth
		lblMonth = new Label("Month :");
		lblMonth.setImmediate(false);
		lblMonth.setWidth("100.0%");
		lblMonth.setHeight("-1px");
		mainLayout.addComponent(lblMonth,"top:40.0px; left:20.0px;");
		lblMonth.setVisible(false);
		
		// dMonth
		dMonth = new ComboBox();
		dMonth.setImmediate(true);
		dMonth.setWidth("150px");
		dMonth.setHeight("-1px");
		dMonth.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(dMonth, "top:38.0px; left:130.0px;");
		dMonth.setVisible(false);
		
		lblUnitName = new Label();
		lblUnitName.setImmediate(true);
		lblUnitName.setWidth("-1px");
		lblUnitName.setHeight("-1px");
		lblUnitName.setValue("Project :");
		mainLayout.addComponent(lblUnitName, "top:70.0px;left:20.0px;");

		cmbUnitName = new ComboBox();
		cmbUnitName.setImmediate(true);
		cmbUnitName.setWidth("300px");
		cmbUnitName.setHeight("24px");
		cmbUnitName.setImmediate(true);
		cmbUnitName.setNullSelectionAllowed(false);
		cmbUnitName.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbUnitName, "top:68.0px;left:130.0px;");

		lblDepartmentName = new Label();
		lblDepartmentName.setImmediate(true);
		lblDepartmentName.setWidth("-1px");
		lblDepartmentName.setHeight("-1px");
		lblDepartmentName.setValue("Department :");
		mainLayout.addComponent(lblDepartmentName, "top:10.0px;left:470.0px;");

		cmbDepartmentName = new ComboBox();
		cmbDepartmentName.setImmediate(true);
		cmbDepartmentName.setWidth("300px");
		cmbDepartmentName.setHeight("24px");
		cmbDepartmentName.setImmediate(true);
		cmbDepartmentName.setNullSelectionAllowed(false);
		cmbDepartmentName.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbDepartmentName, "top:08.0px;left:590.0px;");

		chkDepartmentAll = new CheckBox("All");
		chkDepartmentAll.setImmediate(true);
		mainLayout.addComponent(chkDepartmentAll, "top:10.0px; left:895.0px;");
		
		cmbSectionName = new ComboBox();
		cmbSectionName.setImmediate(true);
		cmbSectionName.setWidth("300px");
		cmbSectionName.setHeight("24px");
		cmbSectionName.setImmediate(true);
		cmbSectionName.setNullSelectionAllowed(false);
		cmbSectionName.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Section :"),"top:40px; left:470px");
		mainLayout.addComponent(cmbSectionName, "top:38px;left:590.0px;");

		chkSectionAll = new CheckBox("All");
		chkSectionAll.setImmediate(true);
		mainLayout.addComponent(chkSectionAll, "top:40px; left:895.0px;");
		
		lblEmployeeID = new Label();
		lblEmployeeID.setImmediate(true);
		lblEmployeeID.setWidth("-1px");
		lblEmployeeID.setHeight("-1px");
		lblEmployeeID.setValue("Employee ID: ");
		mainLayout.addComponent(lblEmployeeID, "top:70px;left:470.0px;");

		cmbEmployeeID = new ComboBox();
		cmbEmployeeID.setImmediate(true);
		cmbEmployeeID.setWidth("300px");
		cmbEmployeeID.setHeight("24px");
		cmbEmployeeID.setImmediate(true);
		cmbEmployeeID.setNullSelectionAllowed(false);
		cmbEmployeeID.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbEmployeeID, "top:68px;left:590.0px;");

		chkEmployeeAll = new CheckBox("All");
		chkEmployeeAll.setImmediate(true);
		mainLayout.addComponent(chkEmployeeAll, "top:70px; left:895.0px;");
		
		mainLayout.addComponent(table, "top:100.0px;left:20.0px;");

		lblCl = new Label("<font color='#A00324'><b><Strong>Use 24-Hour Format<Strong></b></font>");
		lblCl.setImmediate(true);
		lblCl.setWidth("-1px");
		lblCl.setHeight("-1px");
		lblCl.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblCl, "top:10px;left:1000px;");

		mainLayout.addComponent(button, "top:440.0px;left:420.0px;");
		return mainLayout;
	}
}
