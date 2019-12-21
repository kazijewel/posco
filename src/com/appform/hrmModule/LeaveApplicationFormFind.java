package com.appform.hrmModule;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

public class LeaveApplicationFormFind extends Window 
{
	private CommonButton cButton = new CommonButton( "", "", "", "", "", "Find", "", "", "", "");
	@SuppressWarnings("unused")
	private SessionBean sessionBean;	
	private AbsoluteLayout mainLayout;

	private Label lblUnit; 
	private ComboBox cmbUnit;
	
	private Label lblDepartmentName; 
	private ComboBox cmbDepartmentName;

	private CheckBox chkDepartmentAll;

	private Label lblFormDate;
	private PopupDateField dFromDate = new PopupDateField();

	private Label lblToDate;
	private PopupDateField dToDate = new PopupDateField();

	private Table table = new Table();

	private ArrayList<Label> tblblSL = new ArrayList<Label>();
	private ArrayList<Label> tblblLeaveId = new ArrayList<Label>();
	private ArrayList<Label> tblblEmpName = new ArrayList<Label>();
	private ArrayList<Label> tblblDesignation = new ArrayList<Label>();
	private ArrayList<Label> tblblLeaveType = new ArrayList<Label>();
	private ArrayList<Label> tblblLeaveFrom = new ArrayList<Label>();
	private ArrayList<Label> tblblLeaveTo = new ArrayList<Label>();
	private ArrayList<Label> tblblDuration = new ArrayList<Label>();
	private ArrayList<Label> tblblStatus = new ArrayList<Label>();

	ArrayList<Component> allComp = new ArrayList<Component>();

	private SimpleDateFormat dtfYMD = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dfBd = new SimpleDateFormat("dd-MM-yyyy");

	private DecimalFormat df = new DecimalFormat("#0");

	private TextField txtFindId = new TextField();
	@SuppressWarnings("unused")
	private String fromName = "";
	private String leaveId = "";

	public LeaveApplicationFormFind(SessionBean sessionBean, TextField txtFindId, String fromName)
	{
		this.fromName = fromName;
		this.txtFindId = txtFindId;
		this.sessionBean = sessionBean;
		this.setCaption("FIND LEAVE APPLICATION :: "+sessionBean.getCompany());
		this.center();
		this.setWidth("570px");
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setModal(true);
		this.setResizable(false);
		this.setStyleName("cwindow");
		buildMainLayout();
		setContent(mainLayout);
		setEventActions();
		focusEnter();
		cmbUnitDataAdd();
	}

	private void focusEnter()
	{
		allComp.add(cmbUnit);	
		allComp.add(cmbDepartmentName);
		allComp.add(cmbDepartmentName);
		allComp.add(dFromDate);
		allComp.add(dToDate);

		new FocusMoveByEnter(this,allComp);
	}

	private void setEventActions()
	{
		dFromDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
					tableclear();
					if(dFromDate.getValue()!=null)
					{
						cmbUnitDataAdd();
					}
			}
		});

		dToDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				tableclear();
				if(dToDate.getValue()!=null)
				{
					cmbUnitDataAdd();
				}
			}
		});

		
		cmbUnit.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				tableclear();
				chkDepartmentAll.setValue(false);
				cmbDepartmentName.setEnabled(true);
				if(cmbUnit.getValue()!=null)
				{
					cmbDepartmentDataAdd();
				}
				else
				{
					showNotification("Warning!","Select Project",Notification.TYPE_WARNING_MESSAGE);
					cmbUnit.focus();
				}
			}
		});
	
		
		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(cmbUnit.getValue()!=null)
				{
					if(event.isDoubleClick())
					{
						leaveId = tblblLeaveId.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
						txtFindId.setValue(leaveId);
						close();
					}
				}
				else
				{
					showNotification("Warning!","Select Project",Notification.TYPE_WARNING_MESSAGE);
					cmbUnit.focus();
				}
			}
		});

		
		
		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(cmbDepartmentName.getValue()!=null || chkDepartmentAll.booleanValue())
				{
					if(event.isDoubleClick())
					{
						leaveId = tblblLeaveId.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
						txtFindId.setValue(leaveId);
						close();
					}
				}
				else
				{
					showNotification("Warning!","Select Department",Notification.TYPE_WARNING_MESSAGE);
					cmbDepartmentName.focus();
				}
			}
		});

		chkDepartmentAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				tableclear();
				if(chkDepartmentAll.booleanValue())
				{
					cmbDepartmentName.setValue(null);
					cmbDepartmentName.setEnabled(false);
				}
				else
				{
					cmbDepartmentName.setEnabled(true);
				}
			}
		});
//------------------------
		cButton.btnFind.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(cmbUnit.getValue()!=null )
				{
					tableclear();
					findEvent();
				}
				else
				{
					showNotification("Warning!","Select Project name.",Notification.TYPE_WARNING_MESSAGE);
					cmbUnit.focus();
				}
			}
		});
//-----------------------------
		
		cButton.btnFind.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(cmbDepartmentName.getValue()!=null || chkDepartmentAll.booleanValue())
				{
					tableclear();
					findEvent();
				}
				else
				{
					showNotification("Warning!","Select Department.",Notification.TYPE_WARNING_MESSAGE);
					cmbDepartmentName.focus();
				}
			}
		});
	}

	private void cmbUnitDataAdd()
	{
		cmbUnit.removeAllItems();

		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct (select vUnitId from tbEmpOfficialPersonalInfo where vEmployeeId=li.vEmployeeId)ui,(select vUnitName from tbEmpOfficialPersonalInfo where vEmployeeId=li.vEmployeeId)un "
					+ " from tbEmpLeaveApplicationInfo li where dApplicationDate between "
					+ "'"+dtfYMD.format(dFromDate.getValue())+"'  and "
					+ "'"+dtfYMD.format(dToDate.getValue())+"' ";


			
			System.out.println("unit"+query);
			
		//------------------------------------------------------------------------------------------
			
			List <?> lst=session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				for(Iterator <?> itr=lst.iterator();itr.hasNext();)
				{
					Object [] element=(Object[])itr.next();
					cmbUnit.addItem(element[0]);
					cmbUnit.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch (Exception exp)
		{
			showNotification("Project data :", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	
	
	private void cmbDepartmentDataAdd()
	{
		cmbDepartmentName.removeAllItems();

		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			
			/*String query=" select distinct vDepartmentId,(select vDepartmentName from tbDepartmentInfo si where" +
			" si.vDepartmentId=li.vDepartmentId) vDepartmentName from tbEmpLeaveApplicationInfo li" +
			" where dApplicationDate between '"+dFormat.format(dFromDate.getValue())+"' and" +
			" '"+dFormat.format(dToDate.getValue())+"' order by vDepartmentName ";*/

			String query=" select distinct (select vDepartmentId from tbEmpOfficialPersonalInfo where vEmployeeId=li.vEmployeeId)ui,(select vDepartmentName from tbEmpOfficialPersonalInfo where vEmployeeId=li.vEmployeeId)un from tbEmpLeaveApplicationInfo li" +
					" where dApplicationDate between '"+dtfYMD.format(dFromDate.getValue())+"' and" +
					" '"+dtfYMD.format(dToDate.getValue())+"' ";
		
			System.out.println("Department"+query);
			
			List <?> lst=session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				for(Iterator <?> itr=lst.iterator();itr.hasNext();)
				{
					Object [] element=(Object[])itr.next();
					cmbDepartmentName.addItem(element[0]);
					cmbDepartmentName.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch (Exception exp)
		{
			showNotification("cmbDepartmentDataAdd", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void findEvent()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String findQuery = "select vLeaveId,epo.vEmployeeName,epo.vUnitId,epo.vDesignationName,vLeaveType,dLeaveFrom,dLeaveTo,mTotalDays," +
					" iApprovedFlag from tbEmpLeaveApplicationInfo eli inner join tbEmpOfficialPersonalInfo epo on epo.vEmployeeId=eli.vEmployeeId where epo.vUnitId='"+cmbUnit.getValue().toString()+"' and epo.vDepartmentId like" +
					" '"+(chkDepartmentAll.booleanValue()?"%":cmbDepartmentName.getValue().toString())+"'" +
					" and eli.dApplicationDate between '"+dtfYMD.format(dFromDate.getValue())+"' and '"+dtfYMD.format(dToDate.getValue())+"'";

		
			
			System.out.println("Find"+findQuery);
			
			List <?> list = session.createSQLQuery(findQuery).list();

			if(!list.isEmpty())
			{
				tableclear();
				int i=0;
				for(Iterator <?> iter = list.iterator(); iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();
					tblblLeaveId.get(i).setValue(element[0].toString());
					tblblEmpName.get(i).setValue(element[1].toString());
					tblblDesignation.get(i).setValue(element[3].toString());
					tblblLeaveType.get(i).setValue(element[4].toString());
					tblblLeaveFrom.get(i).setValue((dfBd.format(element[5])).toString());
					tblblLeaveTo.get(i).setValue((dfBd.format(element[6])).toString());
					tblblDuration.get(i).setValue(df.format(element[7]));

					if(element[8].toString().equals("0"))
					{
						tblblStatus.get(i).setValue("Pending");
					}
					else if(element[8].toString().equals("1"))
					{
						tblblStatus.get(i).setValue("Approved");
					}
					else
					{
						tblblStatus.get(i).setValue("Canceled");
					}

					if((i)==tblblSL.size()-1)
					{
						tableRowAdd(i+1);
					}
					i++;
				}
			}
			else
			{
				tableclear();
				this.getParent().showNotification("No data found!", Notification.TYPE_WARNING_MESSAGE); 
			}
		}
		catch (Exception ex)
		{
			this.getParent().showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void tableclear()
	{
		for(int i=0; i<tblblSL.size(); i++)
		{
			tblblLeaveId.get(i).setValue("");
			tblblEmpName.get(i).setValue("");
			tblblDesignation.get(i).setValue("");
			tblblLeaveType.get(i).setValue("");
			tblblLeaveFrom.get(i).setValue("");
			tblblLeaveTo.get(i).setValue("");
			tblblDuration.get(i).setValue("");
			tblblStatus.get(i).setValue("");
		}
	}

	public void rowAddinTable()
	{
		for(int i=0; i<10; i++)
		{
			tableRowAdd(i);
		}
	}

	public void tableRowAdd(final int ar)
	{
		table.setSelectable(true);
		table.setImmediate(true);

		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);		

		tblblSL.add(ar, new Label(""));
		tblblSL.get(ar).setImmediate(true);
		tblblSL.get(ar).setWidth("100%");
		tblblSL.get(ar).setValue(ar+1);

		tblblLeaveId.add(ar, new Label(""));
		tblblLeaveId.get(ar).setImmediate(true);
		tblblLeaveId.get(ar).setWidth("100%");

		tblblEmpName.add(ar, new Label(""));
		tblblEmpName.get(ar).setImmediate(true);
		tblblEmpName.get(ar).setWidth("100%");

		tblblDesignation.add(ar, new Label(""));
		tblblDesignation.get(ar).setImmediate(true);
		tblblDesignation.get(ar).setWidth("100%");

		tblblLeaveType.add(ar, new Label(""));
		tblblLeaveType.get(ar).setImmediate(true);
		tblblLeaveType.get(ar).setWidth("100%");

		tblblLeaveFrom.add(ar, new Label(""));
		tblblLeaveFrom.get(ar).setImmediate(true);
		tblblLeaveFrom.get(ar).setWidth("100%");

		tblblLeaveTo.add(ar, new Label(""));
		tblblLeaveTo.get(ar).setImmediate(true);
		tblblLeaveTo.get(ar).setWidth("100%");

		tblblDuration.add(ar, new Label(""));
		tblblDuration.get(ar).setImmediate(true);
		tblblDuration.get(ar).setWidth("100%");

		tblblStatus.add(ar, new Label(""));
		tblblStatus.get(ar).setImmediate(true);
		tblblStatus.get(ar).setWidth("100%");

		table.addItem(new Object[]{tblblSL.get(ar),tblblLeaveId.get(ar),tblblEmpName.get(ar),tblblDesignation.get(ar),
				tblblLeaveType.get(ar),tblblLeaveFrom.get(ar),tblblLeaveTo.get(ar),tblblDuration.get(ar),tblblStatus.get(ar)},ar);
	}

	private AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);

		setWidth("800px");
		setHeight("400px");

		lblFormDate = new Label("From :");
		lblFormDate.setImmediate(true);
		lblFormDate.setWidth("-1px");
		lblFormDate.setHeight("-1px");
		mainLayout.addComponent(lblFormDate, "top:20.0px;left:10.0px;");

		dFromDate.setValue(new java.util.Date());
		dFromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dFromDate.setDateFormat("dd-MM-yy");
		//dFromDate.setInvalidAllowed(false);
		dFromDate.setImmediate(true);
		dFromDate.setWidth("95px");
		mainLayout.addComponent(dFromDate, "top:18.0px;left:49.0px;");

		lblToDate = new Label("To :");
		lblToDate.setImmediate(true);
		lblToDate.setWidth("-1px");
		lblToDate.setHeight("-1px");
		mainLayout.addComponent(lblToDate, "top:20.0px;left:149.0px;");

		dToDate.setValue(new java.util.Date());
		dToDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dToDate.setDateFormat("dd-MM-yy");
		//dToDate.setInvalidAllowed(false);
		dToDate.setImmediate(true);
		dToDate.setWidth("95px");
		mainLayout.addComponent(dToDate, "top:18.0px;left:170.0px;");
	
		cmbUnit = new ComboBox();
		cmbUnit.setImmediate(true);
		cmbUnit.setWidth("215px");
		cmbUnit.setHeight("-1px");
		cmbUnit.setInputPrompt("Select Project");
		cmbUnit.setNewItemsAllowed(false);
		mainLayout.addComponent(cmbUnit, "top:18.0px;left:280.0px;");

		cmbDepartmentName = new ComboBox();
		cmbDepartmentName.setImmediate(true);
		cmbDepartmentName.setWidth("200px");
		cmbDepartmentName.setHeight("-1px");
		cmbDepartmentName.setInputPrompt("Select Department");
		cmbDepartmentName.setNewItemsAllowed(false);
		mainLayout.addComponent(cmbDepartmentName, "top:18.0px;left:500.0px;");

		chkDepartmentAll = new CheckBox("All");
		chkDepartmentAll.setImmediate(true);
		chkDepartmentAll.setHeight("-1px");
		chkDepartmentAll.setWidth("-1px");
		mainLayout.addComponent(chkDepartmentAll, "top:20.0px;left:710.0px;");

		cButton.btnFind.setHeight("24px");
		cButton.btnFind.setWidth("40px");
		cButton.btnFind.setCaption("");
		cButton.btnFind.setDescription("Click to find data");
		mainLayout.addComponent(cButton, "top:18.0px;left:750.0px;");

		mainLayout.addComponent(table, "top:50.0px;left:10.0px;");

		table.setColumnCollapsingAllowed(true);
		table.setWidth("99%");
		table.setHeight("285px");
		table.setPageLength(0);

		table.addContainerProperty("SL#", Label.class , new Label());
		table.setColumnWidth("SL#",20);

		table.addContainerProperty("Leave Id", Label.class, new Label());
		table.setColumnWidth("Leave Id", 70);

		table.addContainerProperty("Employee Name", Label.class, new Label());
		table.setColumnWidth("Employee Name", 190);

		table.addContainerProperty("Designation", Label.class, new Label());
		table.setColumnWidth("Designation", 170);

		table.addContainerProperty("Type", Label.class, new Label());
		table.setColumnWidth("Type", 40);

		table.addContainerProperty("From", Label.class , new Label());
		table.setColumnWidth("From",65);

		table.addContainerProperty("To", Label.class , new Label());
		table.setColumnWidth("To",65);

		table.addContainerProperty("Days", Label.class , new Label());
		table.setColumnWidth("Days",30);

		table.addContainerProperty("Status", Label.class , new Label());
		table.setColumnWidth("Status",50);
		
		table.setStyleName("wordwrap-headers");

	//	table.setColumnCollapsed("Leave Id", true);

		rowAddinTable();

		lblUnit = new Label("N.B. : Update is not possible for approved/canceled leave.");
		mainLayout.addComponent(lblUnit, "top:340.0px;left:20.0px;");

/*		lblDepartmentName = new Label("N.B. : Update is not possible for approved/canceled leave.");
		mainLayout.addComponent(lblDepartmentName, "top:340.0px;left:20.0px;");
	
*/		
		
		return mainLayout;
	}
}
