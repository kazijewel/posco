package com.appform.hrmModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class HrmSetupFindWindow extends Window
{
	private VerticalLayout mainLayout=new VerticalLayout();
	private FormLayout cmbLayout=new FormLayout();
	private HorizontalLayout btnLayout=new HorizontalLayout();
	private TextField txtReceiptSupplierId;
	private Table table=new Table();

	public String receiptId = "";

	private TextField txtFindFilter = new TextField();
	private ComboBox cmbFindFilter = new ComboBox();

	private ArrayList<Label> tbLblId = new ArrayList<Label>();
	private ArrayList<Label> tbLblSl = new ArrayList<Label>();
	private ArrayList<Label> tbLblName = new ArrayList<Label>();

	SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss aa");

	private String frmNam;
	private SessionBean sessionBean;
	String date="";

	public HrmSetupFindWindow(SessionBean sessionBean,TextField txtReceiptSupplierId,String frmName)
	{
		this.txtReceiptSupplierId = txtReceiptSupplierId;
		this.sessionBean=sessionBean;
		this.frmNam=frmName;

		this.setCaption(""+frmName.toUpperCase()+" FIND WINDOW :: "+sessionBean.getCompany());
		this.center();
		this.setWidth("420px");
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setModal(true);
		this.setResizable(false);

		this.setStyleName("cwindow");

		if(frmNam.equals("DEPARTMENT"))
		{
			compInit();

			txtFindFilter.setCaption("Type here & hit enter to find "+frmName.toLowerCase()+"");
			txtFindFilter.setImmediate(true);
			txtFindFilter.setWidth("200px");
			mainLayout.addComponent(txtFindFilter);
			mainLayout.setComponentAlignment(txtFindFilter, Alignment.TOP_CENTER);

			compAdd();
			tableInitialise();
			setEventAction();
			tableDataDepartment("%");
		}

		if(frmNam.equals("GRADE"))
		{
			compInit();

			txtFindFilter.setCaption("Type here & hit enter to find "+frmName.toLowerCase()+"");
			txtFindFilter.setImmediate(true);
			txtFindFilter.setWidth("200px");
			mainLayout.addComponent(txtFindFilter);
			mainLayout.setComponentAlignment(txtFindFilter, Alignment.TOP_CENTER);

			compAdd();
			tableInitialise();
			setEventAction();

			tableRank("%");
		}
		
		if(frmNam.equals("SECTION"))
		{
			compInit();

			txtFindFilter.setCaption("Type here & hit enter to find "+frmName.toLowerCase()+"");
			txtFindFilter.setImmediate(true);
			txtFindFilter.setWidth("200px");
			mainLayout.addComponent(txtFindFilter);
			mainLayout.setComponentAlignment(txtFindFilter, Alignment.TOP_CENTER);

			compAdd();
			tableInitialise();
			setEventAction();

			tableDataSection("%");
		}

		if(frmNam.equals("DESIGNATION"))
		{
			compInit();

			txtFindFilter.setCaption("Type here & hit enter to find "+frmName.toLowerCase()+"");
			txtFindFilter.setImmediate(true);
			txtFindFilter.setWidth("200px");
			mainLayout.addComponent(txtFindFilter);
			mainLayout.setComponentAlignment(txtFindFilter, Alignment.TOP_CENTER);

			compAdd();
			tableInitialise();
			setEventAction();

			tableDataDesignation("%");
		}

		if(frmNam.equals("GROUP"))
		{
			compInit();

			txtFindFilter.setCaption("Type here & hit enter to find "+frmName.toLowerCase()+"");
			txtFindFilter.setImmediate(true);
			txtFindFilter.setWidth("200px");
			mainLayout.addComponent(txtFindFilter);
			mainLayout.setComponentAlignment(txtFindFilter, Alignment.TOP_CENTER);

			compAdd();
			tableInitialise();
			setEventAction();

			tableDataGroup("%");
		}

		if(frmNam.equals("SHIFT"))
		{
			compInit();

			cmbFindFilter.setCaption("Select to find "+frmName.toLowerCase()+"");
			cmbFindFilter.setImmediate(true);
			cmbFindFilter.setWidth("200px");
			mainLayout.addComponent(cmbFindFilter);
			mainLayout.setComponentAlignment(cmbFindFilter, Alignment.TOP_CENTER);

			compAdd();
			tableInitialise();
			setEventAction();

			addShiftName();
		}

		if(frmNam.equals("SALARY REGISTER"))
		{
			compInit();

			compAdd();
			tableInitialise();
			setEventAction();

			tableDataSalaryRegister();
		}

		if(frmNam.equals("Disciplinary Action"))
		{
			compInit();

			cmbFindFilter.setCaption("Select Employee Code to find "+frmName.toLowerCase()+"");
			cmbFindFilter.setImmediate(true);
			cmbFindFilter.setWidth("300px");
			mainLayout.addComponent(cmbFindFilter);
			mainLayout.setComponentAlignment(cmbFindFilter, Alignment.TOP_CENTER);

			compAdd();
			tableInitialise();
			setEventAction();
			cmbEmployeeNameDataAdd();

		}
	}

	public void setEventAction()
	{		
		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					receiptId = tbLblId.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					txtReceiptSupplierId.setValue(receiptId);
					windowClose();
				}
			}
		});

		txtFindFilter.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtFindFilter.getValue().toString().isEmpty())
				{
					if(frmNam.equals("DEPARTMENT"))
					{
						tableDataDepartment("%"+txtFindFilter.getValue().toString()+"%");
					}

					if(frmNam.equals("DESIGNATION"))
					{
						tableDataDesignation("%"+txtFindFilter.getValue().toString()+"%");
					}

					if(frmNam.equals("SECTION"))
					{
						tableDataSection("%"+txtFindFilter.getValue().toString()+"%");
					}

					if(frmNam.equals("GROUP"))
					{
						tableDataGroup("%"+txtFindFilter.getValue().toString()+"%");
					}
				}
			}
		});

		cmbFindFilter.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbFindFilter.getValue()!=null)
				{
					if(frmNam.equals("SHIFT"))
					{
						tableDataShift("%"+cmbFindFilter.getValue().toString()+"%");
					}

					if(frmNam.equals("Disciplinary Action"))
					{
						tableDisciplinaryAction("%"+cmbFindFilter.getValue().toString()+"%");
					}
				}
			}
		});
	}

	public void tableInitialise()
	{
		for(int i=0;i<10;i++)
		{
			tableRowAdd(i);
		}
	}

	public void tableRowAdd(final int ar)
	{
		tbLblId.add(ar, new Label(""));
		tbLblId.get(ar).setWidth("100%");
		tbLblId.get(ar).setImmediate(true);

		tbLblSl.add(ar, new Label(""));
		tbLblSl.get(ar).setWidth("100%");
		tbLblSl.get(ar).setImmediate(true);
		tbLblSl.get(ar).setHeight("15px");
		tbLblSl.get(ar).setValue(ar+1);

		tbLblName.add(ar, new Label(""));
		tbLblName.get(ar).setWidth("100%");
		tbLblName.get(ar).setImmediate(true);

		table.addItem(new Object[]{tbLblId.get(ar),tbLblSl.get(ar),tbLblName.get(ar)/*,tbLblAddress.get(ar),
				tbLblContactPerson.get(ar)*/},ar);
	}

	private void tableclear()
	{
		for(int i=0; i<tbLblSl.size(); i++)
		{
			tbLblSl.get(i).setValue("");
			tbLblId.get(i).setValue("");
			tbLblName.get(i).setValue("");
		}
	}

	private void addShiftName()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> list = session.createSQLQuery("SELECT vShiftId,vShiftName FROM tbShiftInfo").list();

			for(Iterator <?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbFindFilter.addItem(element[0].toString());
				cmbFindFilter.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch (Exception e)
		{
			showNotification("Unable to get group data",""+e,Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}

	private void tableDataDepartment(String department)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query ="select vDepartmentId,vDepartmentName from tbDepartmentInfo where" +
					" vDepartmentName like '"+department+"' order by iDepartmentSerial";

			System.out.println("query : "+query);

			List <?> list = session.createSQLQuery(query).list();

			tableclear();
			int i=0;
			for(Iterator <?> iter = list.iterator(); iter.hasNext();)
			{						  
				Object[] element = (Object[]) iter.next();
				tbLblSl.get(i).setValue(i+1);
				tbLblId.get(i).setValue(element[0]);
				tbLblName.get(i).setValue(element[1]);

				if((i)==tbLblId.size()-1)
				{
					tableRowAdd(i+1);
				}
				i++;
			}

			if(i==0)
			{
				showNotification("Warning!","No data found.",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch (Exception ex)
		{
			this.getParent().showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void tableDataSection(String section)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query ="select vSectionId,vSectionName from tbSectionInfo where" +
					" vSectionName like '"+section+"' order by iSectionSerial";

			System.out.println("query : "+query);
			List <?> list = session.createSQLQuery(query).list();
			tableclear();
			int i=0;
			for(Iterator <?> iter = list.iterator(); iter.hasNext();)
			{						  
				Object[] element = (Object[]) iter.next();
				tbLblSl.get(i).setValue(i+1);
				tbLblId.get(i).setValue(element[0]);
				tbLblName.get(i).setValue(element[1]);

				if((i)==tbLblId.size()-1)
				{
					tableRowAdd(i+1);
				}
				i++;
			}

			if(i==0)
			{
				showNotification("Warning!","No data found.",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch (Exception ex)
		{
			this.getParent().showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void tableDataDesignation(String desigName)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query ="select vDesignationId,vDesignation from tbDesignationInfo where" +
					" vDesignation like '"+desigName+"' order by vDesignation";

			List <?> list = session.createSQLQuery(query).list();

			tableclear();
			int i=0;
			for(Iterator <?> iter = list.iterator(); iter.hasNext();)
			{						  
				Object[] element = (Object[]) iter.next();

				tbLblSl.get(i).setValue(i+1);
				tbLblId.get(i).setValue(element[0]);
				tbLblName.get(i).setValue(element[1]);

				if((i)==tbLblId.size()-1)
				{
					tableRowAdd(i+1);
				}
				i++;
			}

			if(i==0)
			{
				showNotification("Warning!","No data found.",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch (Exception ex)
		{
			this.getParent().showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void tableRank(String rank)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query ="select vRankId,iRankSerial,vRankName from tbDesignationRankInfo where" +
					" vRankId like '"+rank+"' or vRankName like '"+rank+"' order by iRankSerial";
			List <?> list = session.createSQLQuery(query).list();

			tableclear();
			int i=0;
			for(Iterator <?> iter = list.iterator(); iter.hasNext();)
			{						  
				Object[] element = (Object[]) iter.next();

				tbLblSl.get(i).setValue(i+1);
				tbLblId.get(i).setValue(element[0]);
				tbLblName.get(i).setValue(element[1]+"-"+element[2]);

				if((i)==tbLblId.size()-1)
				{
					tableRowAdd(i+1);
				}
				i++;
			}

			if(i==0)
			{
				showNotification("Warning!","No data found.",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch (Exception ex)
		{
			showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void tableDataGroup(String groupName)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query ="select vGroupId,vGroupName from tbGroupInfo where" +
					" vGroupName like '"+groupName+"' order by vGroupName";
			List <?> list = session.createSQLQuery(query).list();

			tableclear();
			int i=0;
			for(Iterator <?> iter = list.iterator(); iter.hasNext();)
			{						  
				Object[] element = (Object[]) iter.next();
				tbLblSl.get(i).setValue(i+1);
				tbLblId.get(i).setValue(element[0]);
				tbLblName.get(i).setValue(element[1]);

				if((i)==tbLblId.size()-1)
				{
					tableRowAdd(i+1);
				}
				i++;
			}

			if(i==0)
			{
				showNotification("Warning!","No data found.",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch (Exception ex)
		{
			this.getParent().showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void tableDataShift(String groupName)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query ="select vShiftId,vShiftName,dShiftStart,dShiftEnd from tbShiftInfo si" +
					" where vShiftId like '"+cmbFindFilter.getValue().toString()+"' order by vShiftName";
			List <?> list = session.createSQLQuery(query).list();
			tableclear();
			int i=0;
			for(Iterator <?> iter = list.iterator(); iter.hasNext();)
			{						  
				Object[] element = (Object[]) iter.next();
				tbLblSl.get(i).setValue(i+1);
				tbLblId.get(i).setValue(element[0]);
				tbLblName.get(i).setValue(element[1].toString()+" ("+timeFormat.format(element[2])+"-" +
						""+timeFormat.format(element[3])+")");

				if((i)==tbLblId.size()-1)
				{
					tableRowAdd(i+1);
				}
				i++;
			}

			if(i==0)
			{
				showNotification("Warning!","No data found.",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch (Exception ex)
		{
			this.getParent().showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void tableDisciplinaryAction(String employee)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query ="select vActionRef,dOccurrenceDate,vCourseOfAction,vNameOfAligation,vActionDate,vMemoNo " +
					"from tbEmpDisciplinaryAction where vEmployeeId like '"+employee+"' order by vActionRef ";
			System.out.println("query"+query);

			List <?> list = session.createSQLQuery(query).list();

			tableclear();
			int i=0;
			for(Iterator <?> iter = list.iterator(); iter.hasNext();)
			{						  
				Object[] element = (Object[]) iter.next();
				if(!element[4].toString().equals(""))
				{
					date =element[4].toString();
				}

				tbLblSl.get(i).setValue(i+1);
				tbLblId.get(i).setValue(element[0]);
				tbLblName.get(i).setValue(" On "+sessionBean.dfBd.format(element[1])+" gave warning and order to "+element[2].toString()+" due to "+
						element[3].toString()+" on "+date+" . "+" Memo No- "+element[5].toString());

				if((i)==tbLblId.size()-1)
				{
					tableRowAdd(i+1);
				}
				i++;
			}

			if(i==0)
			{
				showNotification("Warning!","No data found.",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch (Exception ex)
		{
			this.getParent().showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void tableDataSalaryRegister()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query ="select vRegisterId,vRegisterName from tbSalaryRegisterInfo order by vRegisterName";
			List <?> list = session.createSQLQuery(query).list();
			tableclear();
			int i=0;
			for(Iterator <?> iter = list.iterator(); iter.hasNext();)
			{						  
				Object[] element = (Object[]) iter.next();

				tbLblSl.get(i).setValue(i+1);
				tbLblId.get(i).setValue(element[0]);
				tbLblName.get(i).setValue(element[1].toString());

				if((i)==tbLblId.size()-1)
				{
					tableRowAdd(i+1);
				}
				i++;
			}

			if(i==0)
			{
				showNotification("Warning!","No data found.",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch (Exception ex)
		{
			this.getParent().showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void windowClose()
	{
		this.close();
	}

	private void compInit()
	{
		mainLayout.setSpacing(true);
		table.setSelectable(true);
		table.setWidth("100%");
		table.setHeight("250px");

		table.setImmediate(true); // react at once when something is selected
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);
		table.addContainerProperty(""+frmNam+" Id", Label.class, new Label());
		table.setColumnWidth(""+frmNam+" Id",20);

		table.addContainerProperty("SL#", Label.class, new Label());
		table.setColumnWidth("SL#",20);

		table.addContainerProperty(""+frmNam+" Name", Label.class, new Label());
		table.setColumnWidth(""+frmNam+" Name",310);

		table.setColumnCollapsed(""+frmNam+" Id", true);
	}

	private void compAdd()
	{
		cmbLayout.setSpacing(true);
		mainLayout.addComponent(cmbLayout);
		mainLayout.addComponent(btnLayout);
		mainLayout.addComponent(table);
		addComponent(mainLayout);
	}

	private void cmbEmployeeNameDataAdd()
	{
		cmbFindFilter.removeAllItems();
		String query=null;

		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();

		try
		{
			query= " select vEmployeeId,vEmployeeCode from funEmployeeDetails('%') ";
			List <?> lst=session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				for(Iterator <?> itr=lst.iterator();itr.hasNext();)
				{
					Object [] element=(Object[])itr.next();
					cmbFindFilter.addItem(element[0]);
					cmbFindFilter.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch (Exception exp)
		{
			showNotification("cmbEmployeeNameDataAdd", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}
}