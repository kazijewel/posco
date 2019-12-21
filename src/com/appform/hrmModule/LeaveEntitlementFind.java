package com.appform.hrmModule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.hibernate.Session;
import java.text.SimpleDateFormat;

import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class LeaveEntitlementFind extends Window 
{
	@SuppressWarnings("unused")
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lbYear;
	private InlineDateField dYear;

	private Label lbUnitName;
	private ComboBox cmbUnitName,cmbDepartment;

	private Label lbSectionName;
	private ComboBox cmbSectionName;

	private CheckBox chkDepartmentAll;
	
	private CheckBox chkSectionAll;

	String leaveId = "";

	private TextRead findId = new TextRead();
	private TextRead empID = new TextRead();

	private SimpleDateFormat dfYear = new SimpleDateFormat("yyyy");

	private Table table = new Table();

	private ArrayList<Label> lbSl = new ArrayList<Label>();
	private ArrayList<Label> lblLeaveId = new ArrayList<Label>();
	private ArrayList<Label> lbAutoEmployeeID = new ArrayList<Label>();
	private ArrayList<Label> lbEmployeeID = new ArrayList<Label>();
	private ArrayList<Label> lbEmployeeName = new ArrayList<Label>();
	private ArrayList<Label> amtCL = new ArrayList<Label>();
	private ArrayList<Label> amtSL = new ArrayList<Label>();
	private ArrayList<Label> amtAL = new ArrayList<Label>();

	public LeaveEntitlementFind(SessionBean sessionBean, TextRead findId, TextRead empID)
	{
		this.findId = findId;
		this.empID = empID;
		this.sessionBean = sessionBean;

		this.setCaption("LEAVE ENTITLEMENT FIND :: "+sessionBean.getCompany());
		this.setWidth("640px");
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);
		cmbUnitDataLoad();
		tableInitialize();
		eventAction();
	}

	private void eventAction()
	{
		dYear.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(dYear.getValue()!=null)
				{
					cmbUnitDataLoad();
				}
			}
		});
		cmbUnitName.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbUnitName.getValue()!=null )
				{
						tableclear();
						cmbDepartmentDataLoad();
				}
				/*else
				{
					showNotification("Warning","Provide Unit Name!!!", Notification.TYPE_WARNING_MESSAGE);
				}*/
			}
		});
		cmbDepartment.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbDepartment.getValue()!=null )
				{
					cmbSectionDataLoad();
				}
				
			}
		});
		chkDepartmentAll.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbUnitName.getValue()!=null)
				{
					if(chkDepartmentAll.booleanValue() )
					{
						cmbSectionDataLoad();
						cmbDepartment.setValue(null);
						cmbDepartment.setEnabled(false);
					}
					else
					{
						cmbDepartment.setEnabled(true);
					}
				}
			}
		});
		cmbSectionName.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				tableclear();
				if(cmbSectionName.getValue()!=null )
				{
					tableDataAdding();
				}
				else
				{
					tableclear();
				}
			}
		});
		chkSectionAll.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				tableclear();
				if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
				{
					if(chkSectionAll.booleanValue() )
					{
						tableDataAdding();
					
						cmbSectionName.setValue(null);
						cmbSectionName.setEnabled(false);
					}
					else
					{
						tableclear();
						cmbSectionName.setEnabled(true);
					}
				}
			}
		});

		table.addListener(new ItemClickListener()
		{
			public void itemClick(ItemClickEvent event)
			{
				if(cmbUnitName.getValue()!=null)
				{
					if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
					{
						if(cmbSectionName.getValue()!=null || chkSectionAll.booleanValue())
						{
							if(event.isDoubleClick())
							{
								leaveId = lblLeaveId.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();

								findId.setValue(leaveId);
								empID.setValue(lbAutoEmployeeID.get(Integer.valueOf(event.getItemId().toString())).getValue().toString());
								close();
							}
						}
						else
						{
							showNotification("Warning!","Select Section",Notification.TYPE_WARNING_MESSAGE);
							cmbSectionName.focus();
						}
					
						if(event.isDoubleClick())
						{
							leaveId = lblLeaveId.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();

							findId.setValue(leaveId);
							empID.setValue(lbAutoEmployeeID.get(Integer.valueOf(event.getItemId().toString())).getValue().toString());
							close();
						}
					}
					else
					{
						showNotification("Warning!","Select Department",Notification.TYPE_WARNING_MESSAGE);
						cmbDepartment.focus();
					}
				}
				else
				{
					showNotification("Warning!","Select Project",Notification.TYPE_WARNING_MESSAGE);
					cmbUnitName.focus();
				}
			}
		});
	}

	public void cmbDepartmentDataLoad()
	{
		cmbDepartment.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try {
			String sql="select distinct a.vDepartmentId,a.vDepartmentName from tbEmpOfficialPersonalInfo a " +
					"inner join tbEmpLeaveInfo b on a.vEmployeeId=b.vEmployeeId " +
					"where vYear='"+dfYear.format(dYear.getValue())+"' " +
					"and a.vUnitId like '"+(cmbUnitName.getValue()!=null?cmbUnitName.getValue():"%")+"' " +
					"order by a.vDepartmentName ";
			System.out.println("cmbSectionDataLoad: "+sql);
			List<?>list=session.createSQLQuery(sql).list();
			if(!list.isEmpty())
			{
				for(Iterator<?> iter=list.iterator();iter.hasNext();)
				{
					Object[] element =(Object[]) iter.next();
					cmbDepartment.addItem(element[0]);
					cmbDepartment.setItemCaption(element[0], element[1].toString());
				}
			}
			else { this.getParent().showNotification("Warning!","No data found.", Notification.TYPE_WARNING_MESSAGE);}
		} 
		catch (Exception exp) {
			this.getParent().showNotification(exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
		finally	{	session.close();}
	}
	public void cmbSectionDataLoad()
	{
		cmbSectionName.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try {
			String sql="select distinct a.vSectionId,a.vSectionName from tbEmpOfficialPersonalInfo a " +
					"inner join tbEmpLeaveInfo b on a.vSectionId=b.vSectionId " +
					"where vYear='"+dfYear.format(dYear.getValue())+"' " +
					"and a.vUnitId like '"+(cmbUnitName.getValue()!=null?cmbUnitName.getValue():"%")+"' " +
					"and a.vDepartmentId like '"+(cmbDepartment.getValue()!=null?cmbDepartment.getValue():"%")+"' " +
					"order by a.vSectionName ";
			System.out.println("cmbSectionDataLoad: "+sql);
			List<?>list=session.createSQLQuery(sql).list();
			if(!list.isEmpty())
			{
				for(Iterator<?> iter=list.iterator();iter.hasNext();)
				{
					Object[] element =(Object[]) iter.next();
					cmbSectionName.addItem(element[0]);
					cmbSectionName.setItemCaption(element[0], element[1].toString());
				}
			}
			else { this.getParent().showNotification("Warning!","No data found.", Notification.TYPE_WARNING_MESSAGE);}
		} 
		catch (Exception exp) {
			this.getParent().showNotification(exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
		finally	{	session.close();}
	}
	private void cmbUnitDataLoad()
	{
		cmbUnitName.removeAllItems();

		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();

		try
		{
			String query = "select distinct a.vUnitId,a.vUnitName from tbEmpOfficialPersonalInfo a " +
					"inner join tbEmpLeaveInfo b on a.vUnitId=b.vUnitId " +
					"where vYear='"+dfYear.format(dYear.getValue())+"' " +
					"order by a.vUnitName";
			List <?> list = session.createSQLQuery(query).list();
			for (Iterator <?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element =  (Object[]) iter.next();	
				cmbUnitName.addItem(element[0]);
				cmbUnitName.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception ex)
		{
			showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void tableclear()
	{
		for(int i=0; i<lbEmployeeID.size(); i++)
		{
			lblLeaveId.get(i).setValue("");
			lbAutoEmployeeID.get(i).setValue("");
			lbEmployeeID.get(i).setValue("");
			lbEmployeeName.get(i).setValue("");
			amtCL.get(i).setValue("");
			amtSL.get(i).setValue("");
			amtAL.get(i).setValue("");
		}
	}

	private void tableDataAdding()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select vLeaveId,a.vEmployeeId,vEmployeeCode,a.vEmployeeName,iCasualLeave,iSickLeave,iEarnLeave from tbEmpOfficialPersonalInfo a " +
					"inner join tbEmpLeaveInfo b on a.vEmployeeId=b.vEmployeeId " +
					"where vYear='"+dfYear.format(dYear.getValue())+"' " +
					"and a.vUnitId like '"+cmbUnitName.getValue()+"' " +
					"and a.vDepartmentId like '"+(cmbDepartment.getValue()!=null?cmbDepartment.getValue().toString():"%")+"' " +
			        "and a.vSectionId like '"+(cmbSectionName.getValue()!=null?cmbSectionName.getValue().toString():"%")+"' ";
			
			System.out.println("query : "+query);
			List <?> list = session.createSQLQuery(query).list();
			if(!list.isEmpty())
			{
				int i=0;
				for(Iterator <?> iter = list.iterator(); iter.hasNext();)
				{						  
					Object[] element = (Object[]) iter.next();
					lblLeaveId.get(i).setValue(element[0]);
					lbAutoEmployeeID.get(i).setValue(element[1]);
					lbEmployeeID.get(i).setValue(element[2]);
					lbEmployeeName.get(i).setValue(element[3]);
					amtCL.get(i).setValue(element[4]);
					amtSL.get(i).setValue(element[5]);
					amtAL.get(i).setValue(element[6]);

					if((i)==lbEmployeeID.size()-1) 
					{
						tableRowAdd(i+1);
					}
					i++;
				}
			}
			else
			{
				this.getParent().showNotification("Warning!","No data found.", Notification.TYPE_WARNING_MESSAGE); 
			}
		}
		catch (Exception ex)
		{
			this.getParent().showNotification(ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void tableInitialize()
	{
		table.setColumnCollapsingAllowed(true);
		table.setSelectable(true);

		table.setWidth("98%");
		table.setHeight("270px");

		table.addContainerProperty("SL #", Label.class , new Label());
		table.setColumnWidth("SL #",20);

		table.addContainerProperty("Leave Id", Label.class , new Label());
		table.setColumnWidth("Leave Id",70);

		table.addContainerProperty("Employee ID", Label.class, new Label());
		table.setColumnWidth("Employee ID",130);

		table.addContainerProperty("EMP Code", Label.class, new Label());
		table.setColumnWidth("EMP Code",120);

		table.addContainerProperty("Employee Name", Label.class , new Label());
		table.setColumnWidth("Employee Name",260);

		table.addContainerProperty("CL", Label.class , new Label());
		table.setColumnWidth("CL",33);	

		table.addContainerProperty("SL", Label.class , new Label());
		table.setColumnWidth("SL",33);

		table.addContainerProperty("AL", Label.class , new Label());
		table.setColumnWidth("AL",33);

		table.setColumnCollapsed("Leave Id", true);
		
		table.setColumnCollapsed("Employee ID", true);

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
		lbSl.add(ar, new Label(""));
		lbSl.get(ar).setWidth("100%");
		lbSl.get(ar).setHeight("14px");
		lbSl.get(ar).setValue(ar+1);

		lblLeaveId.add(ar, new Label(""));
		lblLeaveId.get(ar).setWidth("100%");

		lbAutoEmployeeID.add(ar, new Label(""));
		lbAutoEmployeeID.get(ar).setWidth("100%");
		lbAutoEmployeeID.get(ar).setImmediate(true);
		
		lbEmployeeID.add(ar, new Label(""));
		lbEmployeeID.get(ar).setWidth("100%");
		lbEmployeeID.get(ar).setImmediate(true);

		lbEmployeeName.add(ar, new Label(""));
		lbEmployeeName.get(ar).setWidth("100%");
		lbEmployeeName.get(ar).setImmediate(true);

		amtCL.add(ar, new Label());
		amtCL.get(ar).setWidth("100%");
		amtCL.get(ar).setImmediate(true);

		amtSL.add(ar, new Label());
		amtSL.get(ar).setWidth("100%");
		amtSL.get(ar).setImmediate(true);

		amtAL.add(ar, new Label());
		amtAL.get(ar).setWidth("100%");
		amtAL.get(ar).setImmediate(true);

		table.addItem(new Object[]{lbSl.get(ar),lblLeaveId.get(ar),lbAutoEmployeeID.get(ar),lbEmployeeID.get(ar),
				lbEmployeeName.get(ar),amtCL.get(ar),amtSL.get(ar),amtAL.get(ar)},ar);
	}


	private AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);

		setWidth("650px");
		setHeight("455px");

		lbYear = new Label("Year :");
		lbYear.setImmediate(true);
		lbYear.setWidth("-1px");
		lbYear.setHeight("-1px");
		mainLayout.addComponent(lbYear, "top:20.0px;left:20.0px;");

		dYear = new InlineDateField();
		dYear.setImmediate(true);
		dYear.setValue(new java.util.Date());
		dYear.setWidth("-1px");
		dYear.setHeight("-1px");
		dYear.setResolution(InlineDateField.RESOLUTION_YEAR);
		mainLayout.addComponent(dYear, "top:18.0px;left:100.0px;");
		
		lbUnitName = new Label("Project :");
		lbUnitName.setImmediate(true);
		lbUnitName.setWidth("-1px");
		lbUnitName.setHeight("-1px");
		mainLayout.addComponent(lbUnitName, "top:45.0px;left:20.0px;");

		cmbUnitName = new ComboBox();
		cmbUnitName.setImmediate(true);
		cmbUnitName.setWidth("220px");
		cmbUnitName.setHeight("-1px");
		cmbUnitName.setNewItemsAllowed(false);
		mainLayout.addComponent(cmbUnitName, "top:43.0px;left:100.0px;");
		
		mainLayout.addComponent(new Label("Department :"), "top:70.0px;left:20.0px;");

		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("220px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setNewItemsAllowed(false);
		mainLayout.addComponent(cmbDepartment, "top:68.0px;left:100.0px;");
		
		chkDepartmentAll =new CheckBox("All");
		chkDepartmentAll.setImmediate(true);
		mainLayout.addComponent(chkDepartmentAll,"top:70.0px;left:320.0px;");
		
		lbSectionName = new Label("Section :");
		lbSectionName.setImmediate(true);
		lbSectionName.setWidth("-1px");
		lbSectionName.setHeight("-1px");
		mainLayout.addComponent(lbSectionName, "top:95px;left:20.0px;");

		cmbSectionName = new ComboBox();
		cmbSectionName.setImmediate(true);
		cmbSectionName.setWidth("220px");
		cmbSectionName.setHeight("-1px");
		cmbSectionName.setNewItemsAllowed(false);
		mainLayout.addComponent(cmbSectionName, "top:93px;left:100.0px;");
		
		chkSectionAll =new CheckBox("All");
		chkSectionAll.setImmediate(true);
		mainLayout.addComponent(chkSectionAll,"top:95px;left:320.0px;");

		mainLayout.addComponent(table, "top:130px;left:20.0px;");

		return mainLayout;
	}
}
