package com.appform.hrmModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;


@SuppressWarnings("serial")
public class SectionWiseLeaveEntryForm extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Table table= new Table();
	private PopupDateField dApplicationDate ;

	private Label lblDate ;
	private Label lblSection;
	private ComboBox cmbSection;

	private Label lblEmployee;
	private ComboBox cmbEmployee;
	private CheckBox chkEmployeeAll;

	private ArrayList<NativeButton> tbBtnDel=new ArrayList<NativeButton>();
	private ArrayList<Label> tbLblAutoEmployeeID=new ArrayList<Label>();
	private ArrayList<Label> tbLblEmployeeCode = new ArrayList<Label>();
	private ArrayList<Label> tbLblFingerID = new ArrayList<Label>();
	private ArrayList<Label> tbLblEmployeeName = new ArrayList<Label>();
	private ArrayList<Label> tbLblDesignationID = new ArrayList<Label>();
	private ArrayList<Label> tbLblDesignation = new ArrayList<Label>();
	private ArrayList<Label> tbLblClBalance = new ArrayList<Label>();
	private ArrayList<Label> tbLblAlBalance = new ArrayList<Label>();
	private ArrayList<Label> tbLblSlBalance = new ArrayList<Label>();
	private ArrayList<Label> tbLblLeaveID = new ArrayList<Label>();
	private ArrayList<ComboBox> tbCmbLeaveType = new ArrayList<ComboBox>();
	private ArrayList<PopupDateField> tbDFromDate = new ArrayList<PopupDateField>();
	private ArrayList<PopupDateField> tbDToDate = new ArrayList<PopupDateField>();
	private ArrayList<Label> tbLblDays = new ArrayList<Label>();

	ArrayList<Component> allComp = new ArrayList<Component>();

	private List <?> lstEmployee = Arrays.asList(new String [] {"Employee ID","Finger ID","Employee Name"});
	private OptionGroup opgEmployee;

	private String [] leaveTypeList = new String []{"CL","SL","AL"};

	TextRead txtMonth=new TextRead("");
	TextRead txtSectionID=new TextRead("");

	private SimpleDateFormat dYearFormat = new SimpleDateFormat("yyyy");
	private SimpleDateFormat dDayFormat = new SimpleDateFormat("dd");
	private SimpleDateFormat dMonthFormat = new SimpleDateFormat("MM");
	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dNormalDateFormat = new SimpleDateFormat("dd-MM-yyyy");

	CommonButton button = new CommonButton("New", "Save", "Edit", "","Refresh","Find","","","","Exit");

	int index=0;
	String Notify="";
	boolean isUpdate=false;

	public SectionWiseLeaveEntryForm(SessionBean sessionBean) 
	{
		this.sessionBean=sessionBean;
		this.setResizable(false);
		this.setCaption("SECTION WISE LEAVE ENTRY FORM :: " + sessionBean.getCompany());

		buildMainLayout();
		setContent(mainLayout);
		tableinitialise();
		componentIni(true);
		btnIni(true);
		SetEventAction();
		addSectionName();
		button.btnNew.focus();
	}

	private void addSectionName() 
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select distinct ein.vSectionId,sinf.SectionName from tbEmployeeInfo " +
					"ein inner join tbSectionInfo sinf on ein.vSectionId=sinf.AutoID where ein.iStatus=1 " +
					"order by sinf.SectionName";
			List <?> lst = session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				for(Iterator <?> itr = lst.iterator(); itr.hasNext(); )
				{
					Object [] element = (Object [])itr.next();
					cmbSection.addItem(element[0]);
					cmbSection.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch (Exception exp)
		{
			showNotification("addSectionName", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void addEmployeeData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select distinct vEmployeeID,vEmployeeCode,cast(vFingerID as int) from tbEmpLeaveInfo " +
					"where vSectionID like '"+cmbSection.getValue()+"'" +
					"order by cast(vFingerID as int)";
			if(opgEmployee.getValue()=="Employee Name")
			{
				query = "select distinct vEmployeeID,vEmployeeName,cast(vFingerID as int) from tbEmpLeaveInfo " +
						"where vSectionID like '"+cmbSection.getValue()+"'" +
						"order by cast(vFingerID as int)";
			}
			else if(opgEmployee.getValue()=="Finger ID")
			{
				query = "select distinct vEmployeeID,vFingerID,cast(vFingerID as int) from tbEmpLeaveInfo " +
						"where vSectionID like '"+cmbSection.getValue()+"'" +
						"order by cast(vFingerID as int)";
			}

			List <?> lst = session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				for(Iterator <?> itr = lst.iterator(); itr.hasNext(); )
				{
					Object [] element = (Object [])itr.next();
					cmbEmployee.addItem(element[0]);
					cmbEmployee.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch (Exception exp)
		{
			showNotification("addEmployeeData", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void tableDataAdd()
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try
		{
			String query = "select ELI.vEmployeeId,ELI.vFingerId,ELI.vEmployeeCode,ELI.vEmployeeName," +
					"EIN.vDesignationId,DIN.designationName,ELI.iCasualLeave,ELI.iSickLeave,ELI.iEarnLeave," +
					"ELI.vLeaveID,CAST(ELI.vFingerId as int) Serialize from tbEmpLeaveInfo ELI inner join tbEmployeeInfo " +
					"EIN on ELI.vEmployeeID=EIN.vEmployeeId inner join tbDesignationInfo DIN on DIN.designationId = " +
					"EIN.vDesignationId where ELI.vSectionID like '"+cmbSection.getValue()+"' and ELI.vEmployeeId like " +
					"'"+(cmbEmployee.getValue()!=null?cmbEmployee.getValue():"%")+"' and ein.iStatus = 1 and " +
					"ELI.vYear = '"+dYearFormat.format(dApplicationDate.getValue())+"' order by CAST(ELI.vFingerId as int)";
			List<?> lst = session.createSQLQuery(query).list();

			if(!lst.isEmpty())
			{
				boolean checkData=false;
				for(Iterator <?> itr = lst.iterator(); itr.hasNext(); )
				{
					Object [] element = (Object [])itr.next();
					boolean check=false;
					for(int chkindex=0;chkindex<tbLblAutoEmployeeID.size();chkindex++)
					{
						if(tbLblAutoEmployeeID.get(chkindex).getValue().toString().equalsIgnoreCase(element[0].toString()))
						{
							check=true;
							index=chkindex;
							break;
						}
						else if(tbLblAutoEmployeeID.get(chkindex).getValue().toString().isEmpty())
						{
							check=false;
							index=chkindex;
							break;
						}
						else
						{
							check=true;
						}
					}
					if(!check)
					{
						tbLblAutoEmployeeID.get(index).setValue(element[0]);
						tbLblFingerID.get(index).setValue(element[1]);
						tbLblEmployeeCode.get(index).setValue(element[2]);
						tbLblEmployeeName.get(index).setValue(element[3]);
						tbLblDesignationID.get(index).setValue(element[4]);
						tbLblDesignation.get(index).setValue(element[5]);
						tbLblClBalance.get(index).setValue(element[6]);
						tbLblSlBalance.get(index).setValue(element[7]);
						tbLblAlBalance.get(index).setValue(element[8]);
						tbLblLeaveID.get(index).setValue(element[9]);
						if(index == tbLblAutoEmployeeID.size()-1)
							tableRowAdd(index+1);

						index++;
					}
					checkData=check;
				}
				if(checkData)
				{
					showNotification("Warning", "Employee is already Found in the list!!!", Notification.TYPE_WARNING_MESSAGE);
				}
			}
			else
			{
				showNotification("Warning", "No Employee Found!!!", Notification.TYPE_ERROR_MESSAGE);
			}
		}
		catch (Exception exp)
		{
			showNotification("tableDataAdd", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void SetEventAction()
	{
		cmbSection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				index=0;
				cmbEmployee.removeAllItems();
				chkEmployeeAll.setValue(false);
				tableClear();
				if(cmbSection.getValue()!=null)
				{
					addEmployeeData();
				}
			}
		});

		opgEmployee.addListener(new ValueChangeListener()
		{	
			public void valueChange(ValueChangeEvent event)
			{
				opgEmployeeValueSet();
			}
		});

		cmbEmployee.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbEmployee.getValue()!=null)
				{
					//if(tableDataCheck())
					tableDataAdd();
					//else
					//showNotification("Warning", Notify, Notification.TYPE_WARNING_MESSAGE);
				}
				else
				{
					tableClear();
					index = 0;
				}
			}
		});

		chkEmployeeAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				tableClear();
				index=0;
				if(chkEmployeeAll.booleanValue())
				{
					cmbEmployee.setValue(null);
					cmbEmployee.setEnabled(false);
					//if(tableDataCheck())
					tableDataAdd();
					//else
					//	showNotification("Warning", Notify, Notification.TYPE_WARNING_MESSAGE);
				}
				else
				{
					cmbEmployee.setEnabled(true);
				}
			}
		});

		button.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				componentIni(false);
				btnIni(false);
				txtClear();
				dApplicationDate.focus();
			}
		});

		button.btnSave.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				saveButtonEvent();
			}
		});

		button.btnEdit.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				//updateButtonEvent();
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
	}

	private void opgEmployeeValueSet()
	{
		if(opgEmployee.getValue()=="Employee ID")
		{
			lblEmployee.setValue("Employee ID : ");
		}
		else if(opgEmployee.getValue()=="Employee Name")
		{
			lblEmployee.setValue("Employee Name : ");
		}
		else
		{
			lblEmployee.setValue("Finger ID : ");
		}
		addEmployeeData();
	}

	private boolean formValidation()
	{
		boolean ret=false;
		int count = 0;
		for(int i=0;i<tbLblAutoEmployeeID.size();i++)
		{
			if(!tbLblAutoEmployeeID.get(i).getValue().toString().isEmpty())
			{
				if(tbCmbLeaveType.get(i).getValue()!=null)
				{
					if(Integer.parseInt(!tbLblDays.get(i).getValue().toString().isEmpty()?tbLblDays.get(i).getValue().toString():"0")>0)
					{
						ret=true;
						break;
					}
					else
					{
						Notify="Provide Leave Days!!!";
						ret=false;
					}
				}
				else
				{
					tbCmbLeaveType.get(i).focus();
					Notify="Provide Leave Type!!!";
					ret=false;
				}
				count++;
			}
		}
		if(count == 0)
			Notify="No Data Found!!!";

		return ret;
	}

	private void saveButtonEvent()
	{
		if(formValidation())
		{
			try
			{
				MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to save information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
				mb.show(new EventListener()
				{
					public void buttonClicked(ButtonType buttonType)
					{
						if(buttonType == ButtonType.YES)
						{
							insertData();
							txtClear();
							componentIni(true);
							btnIni(true);
							showNotification("All Information Saved Successfully");
						}
					}
				});
			}
			catch (Exception exp)
			{
				showNotification("saveButtonEvent", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
			}
		}
		else
		{
			showNotification("Warning", Notify, Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void insertData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{
			for(int i=0;i<tbLblAutoEmployeeID.size();i++)
			{
				if(!tbLblDays.get(i).getValue().toString().isEmpty())
				{
					String Query = "insert into tbEmployeeLeave(dApplicationDate,vEmployeeId,vEmployeeCode,vFingerID," +
							"vEmployeeName,vSectionID,vDesignationId,dJoiningDate,vLeaveID,vLeaveType,dApplyFrom,dApplyTo," +
							"vPurposeOfLeave,vLeaveAddress,vMobileNo,dSenctionFrom,dSenctionTo,iNoOfDays,iNoOfFridays," +
							"vRemarks,iApprove,vUserId,dEntryTime,vUserIp) values " +
							"('"+dFormat.format(dApplicationDate.getValue())+"'," +
							"'"+tbLblAutoEmployeeID.get(i).getValue()+"'," +
							"'"+tbLblEmployeeCode.get(i).getValue()+"'," +
							"'"+tbLblFingerID.get(i).getValue()+"'," +
							"'"+tbLblEmployeeName.get(i).getValue()+"'," +
							"'"+cmbSection.getValue()+"'," +
							"'"+tbLblDesignationID.get(i).getValue()+"'," +
							"(select dJoiningDate from tbEmployeeInfo where vEmployeeID = '"+tbLblAutoEmployeeID.get(i).getValue()+"')," +
							"'"+tbLblLeaveID.get(i).getValue()+"'," +
							"'"+tbCmbLeaveType.get(i).getValue()+"'," +
							"'"+dFormat.format(tbDFromDate.get(i).getValue())+"'," +
							"'"+dFormat.format(tbDToDate.get(i).getValue())+"'," +
							"'','','','"+dFormat.format(tbDFromDate.get(i).getValue())+"'," +
							"'"+dFormat.format(tbDToDate.get(i).getValue())+"'," +
							"'"+tbLblDays.get(i).getValue()+"','0'," +
							"'','1','"+sessionBean.getUserName()+"',GETDATE(),'"+sessionBean.getUserIp()+"')";
					session.createSQLQuery(Query).executeUpdate();
					LeaveProcedure(session,i);
				}
			}
			tx.commit();
		}
		catch (Exception exp)
		{
			tx.rollback();
			showNotification("insertData", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void LeaveProcedure(Session session,int columnIndex)
	{
		String query = "exec prcLeaveEntryTempTable '"+dFormat.format(dApplicationDate.getValue())+"'," +
				"'"+cmbSection.getValue()+"','"+tbLblAutoEmployeeID.get(columnIndex).getValue()+"'," +
				"'"+tbCmbLeaveType.get(columnIndex).getItemCaption(tbCmbLeaveType.get(columnIndex).getValue())+"'," +
				"'"+dFormat.format(tbDFromDate.get(columnIndex).getValue())+"'," +
				"'"+tbLblDays.get(columnIndex).getValue()+"','"+sessionBean.getUserName()+"'," +
				"'"+sessionBean.getUserIp()+"'";

		session.createSQLQuery(query).executeUpdate();
		System.out.println("PRC QUERY = "+query);
	}

	private void findButtonEvent()
	{
		Window win = new FindSectionWiseLeaveEntryForm(sessionBean, txtMonth, txtSectionID);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if (txtMonth.getValue().toString().length() > 0)
				{
					txtClear();
					findInitialise(txtMonth.getValue().toString(), txtSectionID.getValue().toString());
				}
			}
		});

		this.getParent().addWindow(win);
	}

	private void findInitialise(String strDate, String strSectionID)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{
			String query="select el.dApplicationDate,el.vSectionID,sinf.SectionName,el.vEmployeeId,el.vEmployeeCode," +
					"el.vFingerId,el.vEmployeeName,el.vDesignationId,din.designationName,eli.iCasualLeave,eli.iSickLeave," +
					"eli.iEarnLeave,el.vLeaveType,CONVERT(date,el.dSenctionFrom) dSenctionFrom,CONVERT(date,el.dSenctionTo) " +
					"dSenctionTo,el.iNoOfDays from tbEmployeeLeave el inner join tbDesignationInfo din on " +
					"el.vDesignationId = din.designationId inner join tbSectionInfo sinf on sinf.AutoID = el.vSectionID " +
					"inner join tbEmpLeaveInfo eli on eli.vEmployeeID = el.vEmployeeId where el.dApplicationDate = " +
					"'"+dFormat.format(dApplicationDate.getValue())+"' and vSectionID = '"+cmbSection.getValue()+"' " +
					"order by vEmployeeCode";
			System.out.println(query);
			List <?> lst=session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				/*int i=0;
				for(Iterator <?> itr=lst.iterator();itr.hasNext();)
				{
					Object [] element = (Object[])itr.next();
					if(i==0)
					{
						dApplicationDate.setValue(element[0]);
						cmbSection.setValue(element[1]);
					}
					tbLblAutoEmployeeID.get(i).setValue(element[1].toString());
					tbLblEmployeeCode.get(i).setValue(element[2].toString());
					tbLblFingerID.get(i).setValue(element[2].toString());
					tbLblEmployeeName.get(i).setValue(element[3].toString());
					tbLblDesignationID.get(i).setValue(element[4].toString());
					tbLblDesignation.get(i).setValue(element[5].toString());
					tbLblClBalance.get(i).setValue(decimalFormat.format(Double.parseDouble(element[10].toString())));
					tbLblSlBalance.get(i).setValue(decimalFormat.format(Double.parseDouble(element[11].toString())));
					tbLblAlBalance.get(i).setValue(decimalFormat.format(Double.parseDouble(element[12].toString())));
					tbCmbLeaveType.get(i).setValue(element[13]);
					tbDFromDate.get(i).setValue(element[14]);
					tbDToDate.get(i).setValue(element[14]);
					tbLblDays.get(i).setValue(element[15]);
					if(i==lblAutoEmployeeID.size()-1)
						tableRowAdd(i+1);
					i++;
				}*/
			}
		}
		catch (Exception exp)
		{
			showNotification("findInitialise",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void componentIni(boolean b) 
	{
		dApplicationDate.setEnabled(!b);
		cmbSection.setEnabled(!b);
		cmbEmployee.setEnabled(!b);
		chkEmployeeAll.setEnabled(!b);
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
		cmbSection.setValue(null);
		cmbEmployee.setValue(null);
		tableClear();
	}

	private void tableClear()
	{
		for(int i=0; i<tbLblEmployeeName.size(); i++)
		{
			tbLblAutoEmployeeID.get(i).setValue("");
			tbLblEmployeeCode.get(i).setValue("");
			tbLblFingerID.get(i).setValue("");
			tbLblEmployeeName.get(i).setValue("");
			tbLblDesignationID.get(i).setValue("");
			tbLblDesignation.get(i).setValue("");
			tbLblClBalance.get(i).setValue("");
			tbLblSlBalance.get(i).setValue("");
			tbLblAlBalance.get(i).setValue("");
			tbLblLeaveID.get(i).setValue("");
			tbCmbLeaveType.get(i).setValue(null);
			tbDFromDate.get(i).setValue(new Date());
			tbDToDate.get(i).setValue(new Date());
			tbLblDays.get(i).setValue("");
		}
	}

	private AbsoluteLayout buildMainLayout() 
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);
		mainLayout.setWidth("960px");
		mainLayout.setHeight("450px");

		lblDate = new Label("Application Date :");
		lblDate.setImmediate(false);
		lblDate.setWidth("-1px");
		lblDate.setHeight("-1px");
		mainLayout.addComponent(lblDate, "top:20.0px; left:20.0px;");

		dApplicationDate = new PopupDateField();
		dApplicationDate.setImmediate(true);
		dApplicationDate.setWidth("110px");
		dApplicationDate.setDateFormat("dd-MM-yyyy");
		dApplicationDate.setValue(new java.util.Date());
		dApplicationDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dApplicationDate, "top:18.0px; left:140.0px;");

		lblSection = new Label("Section Name :");
		lblSection.setImmediate(false); 
		lblSection.setWidth("-1px");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection, "top:45.0px; left:20.0px;");

		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("280px");
		cmbSection.setHeight("24px");
		cmbSection.setNullSelectionAllowed(true);
		cmbSection.setNewItemsAllowed(false);
		mainLayout.addComponent(cmbSection, "top:43.0px; left:140.0px;");

		opgEmployee = new OptionGroup("",lstEmployee);
		opgEmployee.setImmediate(true);
		opgEmployee.setStyleName("horizontal");
		opgEmployee.setValue("Employee ID");
		mainLayout.addComponent(opgEmployee, "top:20.0px; left:500.0px;");

		lblEmployee = new Label("Employee ID :");
		lblEmployee.setImmediate(false); 
		lblEmployee.setWidth("-1px");
		lblEmployee.setHeight("-1px");
		mainLayout.addComponent(lblEmployee, "top:45.0px; left:500.0px;");

		cmbEmployee = new ComboBox();
		cmbEmployee.setImmediate(true);
		cmbEmployee.setWidth("250px");
		cmbEmployee.setHeight("24px");
		cmbEmployee.setNewItemsAllowed(false);
		mainLayout.addComponent(cmbEmployee, "top:43.0px; left:610.0px;");

		chkEmployeeAll = new CheckBox("All");
		chkEmployeeAll.setImmediate(true);
		chkEmployeeAll.setHeight("-1px");
		chkEmployeeAll.setWidth("-1px");
		mainLayout.addComponent(chkEmployeeAll, "top:45.0px; left:865.0px;");

		table.setWidth("98%");
		table.setHeight("328px");
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("Del", NativeButton.class, new NativeButton());
		table.setColumnWidth("Del", 30);

		table.addContainerProperty("EMP ID", Label.class, new Label());
		table.setColumnWidth("EMP ID", 80);

		table.addContainerProperty("Employee ID", Label.class, new Label());
		table.setColumnWidth("Employee ID", 75);

		table.addContainerProperty("Finger ID", Label.class, new Label());
		table.setColumnWidth("Finger ID", 75);

		table.addContainerProperty("Employee Name", Label.class, new Label());
		table.setColumnWidth("Employee Name",  140);

		table.addContainerProperty("Designation ID", Label.class, new Label());
		table.setColumnWidth("Designation ID", 70);

		table.addContainerProperty("Designation", Label.class, new Label());
		table.setColumnWidth("Designation", 120);

		table.addContainerProperty("CL", Label.class, new Label());
		table.setColumnWidth("CL", 20);

		table.addContainerProperty("SL", Label.class, new Label());
		table.setColumnWidth("SL", 20);

		table.addContainerProperty("AL", Label.class, new Label());
		table.setColumnWidth("AL", 20);

		table.addContainerProperty("Leave ID", Label.class, new Label());
		table.setColumnWidth("Leave ID", 80);
		
		table.addContainerProperty("Type", ComboBox.class, new ComboBox());
		table.setColumnWidth("Type", 80);

		table.addContainerProperty("Leave From", PopupDateField.class, new PopupDateField());
		table.setColumnWidth("Leave From", 110);

		table.addContainerProperty("Leave To", PopupDateField.class, new PopupDateField());
		table.setColumnWidth("Leave To", 110);

		table.addContainerProperty("Days", Label.class, new Label());
		table.setColumnWidth("Days", 30);

		table.setColumnCollapsed("EMP ID", true);
		table.setColumnCollapsed("Finger ID", true);
		table.setColumnCollapsed("Designation ID", true);
		table.setColumnCollapsed("Leave ID", true);
		table.setColumnAlignments(new String[] {Table.ALIGN_CENTER,Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT,
				Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_RIGHT, Table.ALIGN_RIGHT, 
				Table.ALIGN_RIGHT, Table.ALIGN_LEFT, Table.ALIGN_CENTER, Table.ALIGN_CENTER, Table.ALIGN_CENTER, 
				Table.ALIGN_RIGHT});

		mainLayout.addComponent(table,"top:80.0px; left:20.0px;");
		mainLayout.addComponent(button,"top:415.0px; left:240.0px");
		return mainLayout;
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
		tbBtnDel.add(ar, new NativeButton());
		tbBtnDel.get(ar).setWidth("100%");
		tbBtnDel.get(ar).setImmediate(true);
		tbBtnDel.get(ar).setIcon(new ThemeResource("../icons/cancel.png"));
		tbBtnDel.get(ar).addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				tbLblAutoEmployeeID.get(ar).setValue("");
				tbLblEmployeeCode.get(ar).setValue("");
				tbLblFingerID.get(ar).setValue("");
				tbLblEmployeeName.get(ar).setValue("");
				tbLblDesignationID.get(ar).setValue("");
				tbLblDesignation.get(ar).setValue("");
				tbCmbLeaveType.get(ar).setValue(null);
				tbLblClBalance.get(ar).setValue("");
				tbLblSlBalance.get(ar).setValue("");
				tbLblAlBalance.get(ar).setValue("");
				tbLblLeaveID.get(ar).setValue("");
				tbCmbLeaveType.get(ar).setValue("");
				tbLblDays.get(ar).setValue("");

				for(int tbIndex=ar;tbIndex<tbLblAutoEmployeeID.size()-1;tbIndex++)
				{
					tbLblAutoEmployeeID.get(tbIndex).setValue(tbLblAutoEmployeeID.get(tbIndex+1).getValue().toString());
					tbLblEmployeeCode.get(tbIndex).setValue(tbLblEmployeeCode.get(tbIndex+1).getValue().toString());
					tbLblFingerID.get(tbIndex).setValue(tbLblFingerID.get(tbIndex+1).getValue().toString());
					tbLblEmployeeName.get(tbIndex).setValue(tbLblEmployeeName.get(tbIndex+1).getValue().toString());
					tbLblDesignationID.get(tbIndex).setValue(tbLblDesignationID.get(tbIndex+1).getValue().toString());
					tbLblDesignation.get(tbIndex).setValue(tbLblDesignation.get(tbIndex+1).getValue().toString());
					tbLblClBalance.get(tbIndex).setValue(tbLblClBalance.get(tbIndex+1).getValue().toString());
					tbLblSlBalance.get(tbIndex).setValue(tbLblSlBalance.get(tbIndex+1).getValue().toString());
					tbLblAlBalance.get(tbIndex).setValue(tbLblAlBalance.get(tbIndex+1).getValue().toString());
					tbLblLeaveID.get(tbIndex).setValue(tbLblLeaveID.get(tbIndex+1).getValue().toString());
					tbCmbLeaveType.get(tbIndex).setValue(tbCmbLeaveType.get(tbIndex+1).getValue());
					tbDFromDate.get(tbIndex).setValue(tbDFromDate.get(tbIndex+1).getValue());
					tbDToDate.get(tbIndex).setValue(tbDToDate.get(tbIndex+1).getValue());
					tbLblDays.get(tbIndex).setValue(tbLblDays.get(tbIndex+1).getValue().toString());

					tbLblAutoEmployeeID.get(tbIndex+1).setValue("");
					tbLblEmployeeCode.get(tbIndex+1).setValue("");
					tbLblFingerID.get(tbIndex+1).setValue("");
					tbLblEmployeeName.get(tbIndex+1).setValue("");
					tbLblDesignationID.get(tbIndex+1).setValue("");
					tbLblDesignation.get(tbIndex+1).setValue("");
					tbLblClBalance.get(tbIndex+1).setValue("");
					tbLblSlBalance.get(tbIndex+1).setValue("");
					tbLblAlBalance.get(tbIndex+1).setValue("");
					tbLblLeaveID.get(tbIndex+1).setValue("");
					tbCmbLeaveType.get(tbIndex+1).setValue(null);
					tbLblDays.get(tbIndex+1).setValue("");
					index--;
				}
			}
		});

		tbLblAutoEmployeeID.add(ar, new Label());
		tbLblAutoEmployeeID.get(ar).setWidth("100%");

		tbLblEmployeeCode.add(ar, new Label());
		tbLblEmployeeCode.get(ar).setWidth("100%");

		tbLblFingerID.add(ar, new Label());
		tbLblFingerID.get(ar).setWidth("100%");

		tbLblEmployeeName.add(ar, new Label());
		tbLblEmployeeName.get(ar).setWidth("100%");

		tbLblDesignationID.add(ar,new Label());
		tbLblDesignationID.get(ar).setWidth("100%");

		tbLblDesignation.add(ar, new Label());
		tbLblDesignation.get(ar).setWidth("100%");

		tbLblClBalance.add(ar, new Label());
		tbLblClBalance.get(ar).setWidth("100%");

		tbLblSlBalance.add(ar, new Label());
		tbLblSlBalance.get(ar).setWidth("100%");

		tbLblAlBalance.add(ar, new Label());
		tbLblAlBalance.get(ar).setWidth("100%");

		tbLblLeaveID.add(ar, new Label());
		tbLblLeaveID.get(ar).setWidth("100%");

		tbCmbLeaveType.add(ar, new ComboBox());
		tbCmbLeaveType.get(ar).setWidth("100%");
		tbCmbLeaveType.get(ar).setImmediate(true);
		for(int i = 0; i<leaveTypeList.length; i++)
		{
			tbCmbLeaveType.get(ar).addItem(i+1);
			tbCmbLeaveType.get(ar).setItemCaption(i+1, leaveTypeList[i]);
		}
		tbCmbLeaveType.get(ar).addListener(new ValueChangeListener()
		{	
			public void valueChange(ValueChangeEvent event)
			{
				if(tbCmbLeaveType.get(ar).getValue()==null)
				{
					tbDToDate.get(ar).setValue(tbDFromDate.get(ar).getValue());
					tbLblDays.get(ar).setValue("");
				}
				else
				{
					DaysCalculate(ar);
				}
			}
		});

		tbDFromDate.add(ar, new PopupDateField());
		tbDFromDate.get(ar).setWidth("100%");
		tbDFromDate.get(ar).setValue(new Date());
		tbDFromDate.get(ar).setResolution(PopupDateField.RESOLUTION_DAY);
		tbDFromDate.get(ar).setDateFormat("dd-MM-yyyy");
		tbDFromDate.get(ar).setImmediate(true);
		tbDFromDate.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(tbCmbLeaveType.get(ar).getValue()!=null)
					DaysCalculate(ar);
			}
		});

		tbDToDate.add(ar, new PopupDateField());
		tbDToDate.get(ar).setWidth("100%");
		tbDToDate.get(ar).setValue(new Date());
		tbDToDate.get(ar).setResolution(PopupDateField.RESOLUTION_DAY);
		tbDToDate.get(ar).setDateFormat("dd-MM-yyyy");
		tbDToDate.get(ar).setImmediate(true);
		tbDToDate.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(tbCmbLeaveType.get(ar).getValue()!=null)
					DaysCalculate(ar);
			}
		});

		tbLblDays.add(ar, new Label());
		tbLblDays.get(ar).setWidth("100%");

		table.addItem(new Object[]{tbBtnDel.get(ar),tbLblAutoEmployeeID.get(ar),tbLblEmployeeCode.get(ar),
				tbLblFingerID.get(ar),tbLblEmployeeName.get(ar),tbLblDesignationID.get(ar),tbLblDesignation.get(ar),
				tbLblClBalance.get(ar),tbLblSlBalance.get(ar),tbLblAlBalance.get(ar),tbLblLeaveID.get(ar),
				tbCmbLeaveType.get(ar),tbDFromDate.get(ar),tbDToDate.get(ar),tbLblDays.get(ar)},ar);
	}

	private void DaysCalculate(final int tbIndex)
	{
		if(!tbLblAutoEmployeeID.get(tbIndex).getValue().toString().isEmpty())
		{
			int startDay = Integer.parseInt(dDayFormat.format(tbDFromDate.get(tbIndex).getValue()));
			int endDay = Integer.parseInt(dDayFormat.format(tbDToDate.get(tbIndex).getValue()));
			int startMonth = Integer.parseInt(dMonthFormat.format(tbDFromDate.get(tbIndex).getValue()));
			int endMonth = Integer.parseInt(dMonthFormat.format(tbDToDate.get(tbIndex).getValue()));

			if(startMonth<endMonth)
			{
				showNotification("Warning","Please Select Same Month!!!",Notification.TYPE_WARNING_MESSAGE);
				tbDToDate.get(tbIndex).setValue(tbDFromDate.get(tbIndex).getValue());
				tbLblDays.get(tbIndex).setValue("");
			}

			else if(startMonth>endMonth)
			{
				showNotification("Warning","Invalid Date Range!!!",Notification.TYPE_WARNING_MESSAGE);
				tbDFromDate.get(tbIndex).setValue(tbDToDate.get(tbIndex).getValue());
				tbLblDays.get(tbIndex).setValue("");
			}

			else
			{
				int totalLeaveDays = (endDay-startDay)+1;

				if(totalLeaveDays>0)
				{
					if(tbCmbLeaveType.get(tbIndex).getValue().toString().equalsIgnoreCase("0"))
					{
						if(Integer.parseInt(tbLblClBalance.get(tbIndex).getValue().toString())>=totalLeaveDays)
						{
							tbLblDays.get(tbIndex).setValue(totalLeaveDays);
						}
						else
						{
							showNotification("Warning",tbCmbLeaveType.get(tbIndex).getItemCaption(tbCmbLeaveType.get(tbIndex).getValue())+" Balance Exceeded!!!",Notification.TYPE_WARNING_MESSAGE);
							tbDToDate.get(tbIndex).setValue(tbDFromDate.get(tbIndex).getValue());
							tbLblDays.get(tbIndex).setValue("");
						}
					}

					else if(tbCmbLeaveType.get(tbIndex).getValue().toString().equalsIgnoreCase("1"))
					{
						if(Integer.parseInt(tbLblSlBalance.get(tbIndex).getValue().toString())>=totalLeaveDays)
						{
							tbLblDays.get(tbIndex).setValue(totalLeaveDays);
						}
						else
						{
							showNotification("Warning",tbCmbLeaveType.get(tbIndex).getItemCaption(tbCmbLeaveType.get(tbIndex).getValue())+" Balance Exceeded!!!",Notification.TYPE_WARNING_MESSAGE);
							tbDToDate.get(tbIndex).setValue(tbDFromDate.get(tbIndex).getValue());
							tbLblDays.get(tbIndex).setValue("");
						}
					}

					else if(tbCmbLeaveType.get(tbIndex).getValue().toString().equalsIgnoreCase("2"))
					{
						if(Integer.parseInt(tbLblAlBalance.get(tbIndex).getValue().toString())>=totalLeaveDays)
						{
							tbLblDays.get(tbIndex).setValue(totalLeaveDays);
						}
						else
						{
							showNotification("Warning",tbCmbLeaveType.get(tbIndex).getItemCaption(tbCmbLeaveType.get(tbIndex).getValue())+" Balance Exceeded!!!",Notification.TYPE_WARNING_MESSAGE);
							tbDToDate.get(tbIndex).setValue(tbDFromDate.get(tbIndex).getValue());
							tbLblDays.get(tbIndex).setValue("");
						}
					}
				}
				else
				{
					showNotification("Warning","Invalid Date Range!!!",Notification.TYPE_WARNING_MESSAGE);
					tbDFromDate.get(tbIndex).setValue(tbDToDate.get(tbIndex).getValue());
					tbLblDays.get(tbIndex).setValue("");
				}
			}
		}
		else
		{
			tbCmbLeaveType.get(tbIndex).setValue(null);
		}
	}
}