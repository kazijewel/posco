package com.appform.hrmModule;

import java.awt.Button;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.MessageBox.EventListener;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.MessageBox.ButtonType;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Button.ClickEvent;

public class SectionWiseDeleteProximityID extends Window
{
	SessionBean sessionbean;
	private AbsoluteLayout mainlayout;
	private ComboBox cmbSection;
	private OptionGroup opgEmployee;
	private static final List<String> Optiontype=Arrays.asList(new String[]{"Employee ID","Proximity ID","Finger ID","Employee Name"});
	private ComboBox cmbEmployee;
	private CheckBox chkAllEmp;
	private CheckBox chkDeleteAll;
	private PopupDateField date;
	//private CheckBox chkFriAll;

	private Table table=new Table();;
	private ArrayList<Label> lblSl=new ArrayList<Label>();
	private ArrayList<Label> lblEmployeeID=new ArrayList<Label>();
	private ArrayList<Label> lblProximityID=new ArrayList<Label>();
	private ArrayList<Label> lblEmployeeName=new ArrayList<Label>();
	private ArrayList<Label> lblDesignationID=new ArrayList<Label>();
	private ArrayList<Label> lblDesignation=new ArrayList<Label>();
	private ArrayList<Label> lblSectionID=new ArrayList<Label>();
	private ArrayList<Label> lblSectionName=new ArrayList<Label>();
	private ArrayList<TextField> txtReason=new ArrayList<TextField>();
	private ArrayList<CheckBox> chkDelete=new ArrayList<CheckBox>();
	ArrayList<Component> allComp = new ArrayList<Component>();
	//private ArrayList<CheckBox> chkFriday=new ArrayList<CheckBox>();

	private SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
	private CommonButton cButton=new CommonButton("New", "Save", "", "", "Refresh", "", "", "", "", "Exit");

	boolean isSave=false;
	boolean isRefresh=false;
	int index=0;

	public SectionWiseDeleteProximityID(SessionBean sessionBean)
	{

		this.sessionbean=sessionBean;
		this.setCaption("DELETE PROXIMITY ID/CARD NO. :: "+sessionbean.getCompany());
		buildMainLayout();
		this.setContent(mainlayout);
		this.setResizable(false);
		componentEnable(true);
		btnEnable(true);
		setEventAction();
		cmbSectionDataLoad();
		focusMove();
		cButton.btnNew.focus();
	}

	private void cmbSectionDataLoad()
	{
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="select AutoID,SectionName from tbSectionInfo";
			List lst=session.createSQLQuery(sql).list();
			if(!lst.isEmpty())
			{

				Iterator itr=lst.iterator();
				while(itr.hasNext())
				{
					Object[] element=(Object[])itr.next();
					cmbSection.addItem(element[0]);
					cmbSection.setItemCaption(element[0], element[1].toString());
				}

			}

		}
		catch(Exception exp)
		{
			showNotification("CmbSectionDataLoad", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void setEventAction()
	{

		chkDeleteAll.addListener(new ValueChangeListener()
		{

			
			public void valueChange(ValueChangeEvent event)
			{
				for(int i=0;i<lblEmployeeID.size();i++)
				{
					if(chkDeleteAll.booleanValue())
					{
						if(!lblProximityID.get(i).getValue().toString().isEmpty())
						{
							if(!txtReason.get(i).getValue().toString().isEmpty())
							{
								chkDelete.get(i).setEnabled(true);
								chkDelete.get(i).setValue(true);
							}
						}
					}

					if(!chkDeleteAll.booleanValue())
					{
						if(!lblProximityID.get(i).getValue().toString().isEmpty())
						{
							if(!txtReason.get(i).getValue().toString().isEmpty())
							{
								chkDelete.get(i).setValue(false);
							}
						}
					}
				}
			}
		});

		cmbSection.addListener(new ValueChangeListener()
		{

			
			public void valueChange(ValueChangeEvent event)
			{

				opgEmployee.select(null);
				chkAllEmp.setValue(false);
				chkDeleteAll.setValue(false);
				cmbEmployee.removeAllItems();

			}
		});

		opgEmployee.addListener(new ValueChangeListener()
		{

			
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbSection.getValue()!=null)
				{

					String sql="";
					if(opgEmployee.getValue()=="Employee ID")
					{

						sql="select iFingerID,employeeCode from tbEmployeeInfo where vSectionId='"+cmbSection.getValue().toString()+"' and iStatus='1' ";
					}
					else if(opgEmployee.getValue()=="Proximity ID")
					{

						sql="select iFingerID,vProximityId from tbEmployeeInfo where vSectionId='"+cmbSection.getValue().toString()+"' and iStatus='1'";

					}
					else if(opgEmployee.getValue()=="Finger ID")
					{

						sql="select iFingerID,iFingerID from tbEmployeeInfo where vSectionId='"+cmbSection.getValue().toString()+"' and iStatus='1'";

					}
					else if(opgEmployee.getValue()=="Employee Name")
					{

						sql="select iFingerID,vEmployeeName from tbEmployeeInfo where vSectionId='"+cmbSection.getValue().toString()+"' and iStatus='1'";

					}
					if(!sql.equals(""))
					{
						Transaction tx=null;
						try
						{

							Session session=SessionFactoryUtil.getInstance().getCurrentSession();
							tx=session.beginTransaction();
							List lst=session.createSQLQuery(sql).list();
							if(!lst.isEmpty())
							{

								Iterator itr=lst.iterator();
								while(itr.hasNext())
								{
									Object[] element=(Object[])itr.next();
									cmbEmployee.addItem(element[0]);
									cmbEmployee.setItemCaption(element[0], element[1].toString());
								}

							}
							else
								showNotification("Warning", "No Employee Found!!!", Notification.TYPE_WARNING_MESSAGE);

						}
						catch(Exception exp)
						{

							showNotification("OPGEmployee", exp.toString(), Notification.TYPE_ERROR_MESSAGE);

						}
					}

				}

				else
				{
					if(!isSave && !isRefresh)
					{
						opgEmployee.select(null);
						showNotification("Warning", "Please Select Section Name!!!", Notification.TYPE_WARNING_MESSAGE);
					}
				}

			}
		});

		cmbEmployee.addListener(new ValueChangeListener()
		{

			
			public void valueChange(ValueChangeEvent event)
			{

				if(cmbEmployee.getValue()!=null)
					tableValueAdd(cmbEmployee.getValue().toString());

			}
		});

		chkAllEmp.addListener(new ValueChangeListener()
		{

			
			public void valueChange(ValueChangeEvent event)
			{

				if(cmbSection.getValue()!=null)
				{

					if(chkAllEmp.booleanValue())
					{

						opgEmployee.select(null);
						opgEmployee.setEnabled(false);
						cmbEmployee.removeAllItems();
						cmbEmployee.setEnabled(false);
						tableValueAdd("%");

					}
					else
					{
						opgEmployee.setEnabled(true);
						cmbEmployee.setEnabled(true);
					}

				}
				else
				{
					if(!isSave && !isRefresh)
					{
						chkAllEmp.setValue(false);
						showNotification("Warning", "Please Select Section Name!!!", Notification.TYPE_WARNING_MESSAGE);
					}
				}
			}
		});

		cButton.btnNew.addListener(new ClickListener()
		{

			
			public void buttonClick(ClickEvent event)
			{

				cmbSection.focus();
				isSave=false;
				isRefresh=false;
				componentEnable(false);
				btnEnable(false);
				index=0;

			}
		});

		cButton.btnSave.addListener(new ClickListener()
		{

			
			public void buttonClick(ClickEvent event)
			{

				if(!lblProximityID.get(0).getValue().toString().isEmpty())
				{
					isSave=true;
					saveButtonEvent();
				}

				else
					showNotification("Warning", "No Data Found in the Table!!!", Notification.TYPE_WARNING_MESSAGE);

			}
		});

		cButton.btnRefresh.addListener(new ClickListener()
		{

			
			public void buttonClick(ClickEvent event)
			{

				isRefresh=true;
				txtclear();
				componentEnable(true);
				btnEnable(true);

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

	private void saveButtonEvent()
	{
		MessageBox msgbox=new MessageBox(getParent(), "Are You Sure?", MessageBox.Icon.QUESTION, "Do You Want to Update All Information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
		msgbox.show(new EventListener()
		{

			
			public void buttonClicked(ButtonType buttonType)
			{
				if(buttonType==ButtonType.YES)
				{
					insertdata();
					componentEnable(true);
					btnEnable(true);
					txtclear();
				}

			}
		});
	}

	private void insertdata()
	{

		String sql="";
		String delProxSql="";
		String deleteDate=dateFormat.format(date.getValue());
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			for(int i=0;i<lblProximityID.size();i++) 
			{
				if(!lblProximityID.get(i).getValue().toString().isEmpty())
				{
					if(chkDelete.get(i).booleanValue())
					{
						if(!txtReason.get(i).getValue().toString().isEmpty())
						{
							delProxSql="insert into tbDeleteProximityID values('"+deleteDate+"','"+lblEmployeeID.get(i).getValue().toString()+"'," +
									"'"+lblProximityID.get(i).getValue().toString()+"','"+lblDesignationID.get(i).getValue().toString()+"'," +
									"'"+lblSectionID.get(i).getValue().toString()+"','"+txtReason.get(i).getValue().toString()+"'," +
									"'"+sessionbean.getUserName()+"','"+sessionbean.getUserIp()+"',getdate());";
							session.createSQLQuery(delProxSql).executeUpdate();

							sql="update tbEmployeeInfo set vProximityID='',vStatus='Discontinue',iStatus='0'"+
									" where employeeCode='"+lblEmployeeID.get(i).getValue().toString()+"' and vSectionId='"+lblSectionID.get(i).getValue().toString()+
									"' and vDesignationId='"+lblDesignationID.get(i).getValue().toString()+"'";
							session.createSQLQuery(sql).executeUpdate();
						}

					}

				}
			}
			showNotification("All Information Saved Successfully");
			tx.commit();
		}
		catch (Exception exp)
		{

			tx.rollback();
			showNotification("InsertDate", exp.toString(), Notification.TYPE_ERROR_MESSAGE);

		}

	}

	private void tableValueAdd(String emp)
	{

		Transaction tx=null;
		try
		{

			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="select employeeCode,vProximityId,vEmployeeName,vdesignationId,(select designationName from " +
					"tbDesignationInfo where designationId=ein.vdesignationId) designationName,vSectionID," +
					"(select SectionName from tbSectionInfo where AutoID=ein.vSectionId) sectionName " +
					"from tbEmployeeInfo ein where vSectionId='"+cmbSection.getValue().toString()+"' " +
					"and iFingerID like '"+emp+"'";

			List lst=session.createSQLQuery(sql).list();
			if(!lst.isEmpty())
			{
				Iterator itr=lst.iterator();
				while(itr.hasNext())
				{

					Object [] element=(Object[])itr.next();
					boolean check=false;
					for(int chkindex=0;chkindex<lblEmployeeID.size();chkindex++)
					{

						if(lblProximityID.get(chkindex).getValue().equals(element[1].toString()))
						{
							check=true;
							break;
						}

					}

					if(!check)
					{
						lblEmployeeID.get(index).setValue(element[0].toString());
						lblProximityID.get(index).setValue(element[1].toString());
						lblEmployeeName.get(index).setValue(element[2].toString());
						lblDesignationID.get(index).setValue(element[3].toString());
						lblDesignation.get(index).setValue(element[4].toString());
						lblSectionID.get(index).setValue(element[5].toString());
						lblSectionName.get(index).setValue(element[6].toString());

						/*if(element[7].toString().equals("1"))
							chkOT.get(index).setValue(true);
						if(element[8].toString().equals("1"))
							chkFriday.get(index).setValue(true);*/

						if(index==lblEmployeeID.size()-1)
						{
							tableRowAdd(index+1);
							focusMove();
						}
						index++;
					}
					else
						showNotification("Warning", "Employee is already Found in the list!!!", Notification.TYPE_WARNING_MESSAGE);

				}
			}
			else
				showNotification("Warning", "No Data Found!!!", Notification.TYPE_WARNING_MESSAGE);

		}
		catch(Exception exp)
		{

			showNotification("TableValueAdd", exp.toString(), Notification.TYPE_ERROR_MESSAGE);

		}

	}

	public void focusMove()
	{
		allComp.add(cmbSection);
		allComp.add(opgEmployee);
		allComp.add(cmbEmployee);
		allComp.add(date);

		for(int i=0; i<txtReason.size();i++)
		{
			allComp.add(txtReason.get(i));
		}
		allComp.add(cButton.btnSave);

		new FocusMoveByEnter(this,allComp);
	}

	private void txtclear()
	{

		cmbSection.setValue(null);
		cmbEmployee.setValue(null);
		opgEmployee.select(null);
		tableclear();

	}

	private void tableclear()
	{

		for(int i=0;i<lblEmployeeID.size();i++)
		{
			lblEmployeeID.get(i).setValue("");
			lblProximityID.get(i).setValue("");
			lblEmployeeName.get(i).setValue("");
			lblDesignationID.get(i).setValue("");
			lblDesignation.get(i).setValue("");
			lblSectionID.get(i).setValue("");
			lblSectionName.get(i).setValue("");
			txtReason.get(i).setValue("");
			chkDelete.get(i).setValue(false);
		}

	}

	private void componentEnable(boolean b)
	{
		cmbSection.setEnabled(!b);
		opgEmployee.setEnabled(!b);
		cmbEmployee.setEnabled(!b);
		chkAllEmp.setEnabled(!b);
		chkDeleteAll.setEnabled(!b);
		date.setEnabled(!b);
		//table.setEnabled(!b);

		for(int i=0;i<lblEmployeeID.size();i++)
		{
			lblSl.get(i).setEnabled(!b);
			lblEmployeeID.get(i).setEnabled(!b);
			lblProximityID.get(i).setEnabled(!b);
			lblEmployeeName.get(i).setEnabled(!b);
			lblSectionName.get(i).setEnabled(!b);
			chkDelete.get(i).setEnabled(b);
		}
	}

	private void btnEnable(boolean b)
	{
		cButton.btnNew.setEnabled(b);
		cButton.btnSave.setEnabled(!b);
		cButton.btnRefresh.setEnabled(!b);
	}

	public void tableinitialize()
	{
		for(int i=0;i<16;i++)
			tableRowAdd(i);
	}

	public void tableRowAdd(final int ar)
	{

		lblSl.add(ar, new Label());
		lblSl.get(ar).setWidth("100%");
		lblSl.get(ar).setValue(ar+1);

		lblEmployeeID.add(ar, new Label());
		lblEmployeeID.get(ar).setWidth("100%");

		lblProximityID.add(ar, new Label());
		lblProximityID.get(ar).setWidth("100%");

		lblEmployeeName.add(ar, new Label());
		lblEmployeeName.get(ar).setWidth("100%");

		lblDesignationID.add(ar, new Label());
		lblDesignationID.get(ar).setWidth("100%");

		lblDesignation.add(ar, new Label());
		lblDesignation.get(ar).setWidth("100%");

		lblSectionID.add(ar, new Label());
		lblSectionID.get(ar).setWidth("100%");

		lblSectionName.add(ar, new Label());
		lblSectionName.get(ar).setWidth("100%");

		txtReason.add(ar, new TextField());
		txtReason.get(ar).setWidth("100%");
		txtReason.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!lblProximityID.get(ar).getValue().toString().isEmpty())
				{
					if(!txtReason.get(ar).getValue().toString().isEmpty())
					{
						chkDelete.get(ar).setEnabled(true);
					}
					else
					{
						chkDelete.get(ar).setEnabled(false);
					}
				}
			}
		});


		chkDelete.add(ar, new CheckBox());
		chkDelete.get(ar).setWidth("100%");
		chkDelete.get(ar).setEnabled(false);

		table.addItem(new Object[]{lblSl.get(ar),lblEmployeeID.get(ar),lblProximityID.get(ar),
				lblEmployeeName.get(ar),lblDesignationID.get(ar),lblDesignation.get(ar),lblSectionID.get(ar),
				lblSectionName.get(ar),txtReason.get(ar),chkDelete.get(ar)}, ar);

	}

	public AbsoluteLayout buildMainLayout()
	{

		mainlayout=new AbsoluteLayout();
		mainlayout.setWidth("1150.0px");
		mainlayout.setHeight("530.0px");

		cmbSection=new ComboBox();
		cmbSection.setWidth("290.0px");
		cmbSection.setHeight("-1px");
		cmbSection.setImmediate(true);
		mainlayout.addComponent(new Label("Section Name : "), "top:30.0px;left:30.0px;");
		mainlayout.addComponent(cmbSection, "top:28.0px;left:120.0px");

		opgEmployee=new OptionGroup("",Optiontype);
		opgEmployee.setImmediate(true);
		opgEmployee.setStyleName("horizontal");
		mainlayout.addComponent(opgEmployee, "top:60.0px;left:30.0px;");

		cmbEmployee=new ComboBox();
		cmbEmployee.setWidth("290.0px");
		cmbEmployee.setHeight("-1px");
		cmbEmployee.setImmediate(true);
		mainlayout.addComponent(new Label("Employee : "), "top:90.0px;left:30.0px;");
		mainlayout.addComponent(cmbEmployee, "top:88.0px;left:120.0px;");

		chkAllEmp=new CheckBox("All");
		chkAllEmp.setImmediate(true);
		mainlayout.addComponent(chkAllEmp, "top:90.0px;left:420.0px");

		date=new PopupDateField();
		date.setImmediate(true);
		date.setResolution(PopupDateField.RESOLUTION_DAY);
		date.setDateFormat("dd-MM-yyyy");
		date.setValue(new Date());
		mainlayout.addComponent(new Label("Date : "), "top:90.0px;left:500.0px;");
		mainlayout.addComponent(date, "top:88.0px;left:540.0px;");

		chkDeleteAll=new CheckBox("Delete All");
		chkDeleteAll.setImmediate(true);
		mainlayout.addComponent(chkDeleteAll, "top:90.0px;left:1000.0px");

		table.setWidth("98%");
		table.setHeight("360.0px");
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("SL", Label.class, new Label());
		table.setColumnWidth("SL", 25);

		table.addContainerProperty("Employee ID", Label.class, new Label());
		table.setColumnWidth("Employee ID",100);

		table.addContainerProperty("Proximity ID", Label.class, new Label());
		table.setColumnWidth("Proximity ID", 100);

		table.addContainerProperty("Employee Name", Label.class, new Label());
		table.setColumnWidth("Employee Name", 180);

		table.addContainerProperty("Designation ID", Label.class, new Label());
		table.setColumnWidth("Designation ID", 30);

		table.addContainerProperty("Designation", Label.class, new Label());
		table.setColumnWidth("Designation", 180);

		table.addContainerProperty("Section ID", Label.class, new Label());
		table.setColumnWidth("Section ID", 30);

		table.addContainerProperty("Section Name", Label.class, new Label());
		table.setColumnWidth("Section Name", 180);

		table.addContainerProperty("Reason", TextField.class, new TextField());
		table.setColumnWidth("Reason", 180);

		table.addContainerProperty("Del", CheckBox.class, new CheckBox());
		table.setColumnWidth("Del", 20);

		table.setColumnCollapsed("Designation ID", true);
		table.setColumnCollapsed("Section ID", true);
		
		table.setColumnAlignments(new String[]{Table.ALIGN_RIGHT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,
				Table.ALIGN_LEFT,Table.ALIGN_RIGHT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,
				Table.ALIGN_LEFT,Table.ALIGN_CENTER});
		mainlayout.addComponent(table, "top:120.0px;left:30.0px");
		tableinitialize();

		mainlayout.addComponent(cButton, "top:490.0px;left:315.0px;");

		return mainlayout;

	}

}
