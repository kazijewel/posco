package com.appform.hrmModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class LoanAppFind extends Window 
{
	@SuppressWarnings("unused")
	private SessionBean sessionBean;	
	private AbsoluteLayout mainLayout;
	private Label lblUnit;
	private ComboBox cmbUnit;
	private ComboBox cmbEmpName,cmbDepartment ;
	private Label lblEmployeeName;
	private Label lblSectionname;
	private Label lblFormDate;
	private Label lblToDate;
	private ComboBox cmbSectionName;
	private PopupDateField dFromDate=new PopupDateField();
	private PopupDateField dtoDate=new PopupDateField();
	private Table table = new Table();
	private ArrayList<Label> lbSL = new ArrayList<Label>();
	private ArrayList<Label> lbLoanNo = new ArrayList<Label>();
	private ArrayList<Label> lbAppDate = new ArrayList<Label>();
	private ArrayList<Label> lbDesignation = new ArrayList<Label>();
	private ArrayList<Label> lbEmpName = new ArrayList<Label>();

	private CheckBox ChkSection,chkDepartmentAll;
	private CheckBox ChkEmployee;
	ArrayList<Component> allComp = new ArrayList<Component>();
	private CommonButton cButton = new CommonButton( "",  "",  "",  "",  "",  "Find", "", "","","");

	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	String autoId = "";
	String empId = "";
	String findDate = "a";

	private TextRead txtAutoId = new TextRead();
	private TextRead txtFindDate = new TextRead();

	public LoanAppFind(SessionBean sessionBean, TextRead txtAuto, TextRead txtFindDate)
	{		
		this.txtAutoId = txtAuto;
		this.txtFindDate = txtFindDate;
		this.sessionBean = sessionBean;
		this.setCaption("FIND LOAN INFORMATION :: "+sessionBean.getCompany());
		this.center();
		this.setWidth("570px");
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setModal(true);
		this.setResizable(false);
		this.setStyleName("cwindow");

		buildMainLayout();
		setContent(mainLayout);
		tableinitialization();
		UnitDataAdd();
		setEventActions();
		cmbUnit.focus();
		focusEnter();
	}

	private void focusEnter()
	{
		allComp.add(cmbUnit);
		allComp.add(cmbSectionName);
		allComp.add(cmbEmpName);
		allComp.add(dFromDate);
		allComp.add(dtoDate);
		allComp.add(cButton.btnFind);
		new FocusMoveByEnter(this,allComp);
	}

	private void tableinitialization()
	{
		table.setColumnCollapsingAllowed(true);
		table.setWidth("98%");
		table.setHeight("230px");
		table.setPageLength(0);

		table.addContainerProperty("SL #", Label.class , new Label());
		table.setColumnWidth("SL #",20);
		
		table.addContainerProperty("Loan No", Label.class , new Label());
		table.setColumnWidth("Loan No",45);

		table.addContainerProperty("App. Date", Label.class , new Label());
		table.setColumnWidth("App. Date",65);

		table.addContainerProperty("Emp Name", Label.class , new Label());
		table.setColumnWidth("Emp Name",150);

		table.addContainerProperty("Designation", Label.class , new Label());
		table.setColumnWidth("Designation",160);
		rowAddinTable();	
	}

	public void rowAddinTable()
	{
		for(int i=0; i<8; i++)
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
		
		lbSL.add(ar, new Label(""));
		lbSL.get(ar).setWidth("100%");
		lbSL.get(ar).setHeight("20px");
		lbSL.get(ar).setValue(ar+1);

		lbLoanNo.add(ar,new Label());
		lbLoanNo.get(ar).setWidth("100%");
		lbLoanNo.get(ar).setHeight("14px");

		lbAppDate.add(ar, new Label(""));
		lbAppDate.get(ar).setWidth("100%");
		lbAppDate.get(ar).setHeight("14px");
		lbAppDate.get(ar).setStyleName("appDate");

		lbDesignation.add(ar, new Label(""));
		lbDesignation.get(ar).setWidth("100%");
		
		lbEmpName.add(ar, new Label(""));
		lbEmpName.get(ar).setWidth("100%");
		table.addItem(new Object[]{lbSL.get(ar),lbLoanNo.get(ar),lbAppDate.get(ar),lbDesignation.get(ar),lbEmpName.get(ar)},ar);
	}
	
	private void UnitDataAdd()
	{
		cmbUnit.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select epo.vUnitId,epo.vUnitName from tbLoanApplication la inner join tbEmpOfficialPersonalInfo epo on epo.vEmployeeId=la.vAutoEmployeeId order by epo.vUnitName ";
			
			
			System.out.println("UNIT DATA :" + query);
			
			
			List <?> list = session.createSQLQuery(query).list();

			for (Iterator <?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element =  (Object[]) iter.next();	
				cmbUnit.addItem(element[0]);
				cmbUnit.setItemCaption(element[0], element[1].toString());	
			}
		}
		catch(Exception ex)
		{
			showNotification("UnitDataAdd", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}


	
	private void sectionDataAdd()
	{
		cmbSectionName.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select distinct epo.vSectionId,epo.vSectionName from tbLoanApplication la inner join tbEmpOfficialPersonalInfo epo on epo.vEmployeeId=la.vAutoEmployeeId where "
					+ " epo.vUnitId='"+cmbUnit.getValue().toString()+"' and epo.vDepartmentId like '"+(chkDepartmentAll.booleanValue()?"%":cmbDepartment.getValue()==null?"%":cmbDepartment.getValue())+"' order by epo.vSectionName ";
			
			
			System.out.println("sectionDataAdd :" + query);
			
			
			List <?> list = session.createSQLQuery(query).list();

			for (Iterator <?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element =  (Object[]) iter.next();	
				cmbSectionName.addItem(element[0]);
				cmbSectionName.setItemCaption(element[0], element[1].toString());	
			}
		}
		catch(Exception ex)
		{
			showNotification("sectionDataAdd", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void DepartmentDataAdd()
	{
		cmbDepartment.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select distinct epo.vDepartmentId,epo.vDepartmentName from tbLoanApplication la inner join tbEmpOfficialPersonalInfo epo on epo.vEmployeeId=la.vAutoEmployeeId where "
					+ " epo.vUnitId='"+cmbUnit.getValue().toString()+"' order by epo.vDepartmentName ";
			
			
			System.out.println("DepartmentDataAdd :" + query);
			
			
			List <?> list = session.createSQLQuery(query).list();

			for (Iterator <?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element =  (Object[]) iter.next();	
				cmbDepartment.addItem(element[0]);
				cmbDepartment.setItemCaption(element[0], element[1].toString());	
			}
		}
		catch(Exception ex)
		{
			showNotification("cmbDepartment", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	
	private void setEventActions()
	{
		cmbUnit.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbUnit.getValue()!=null)
				{
					
					DepartmentDataAdd();
										
				}
			}
		});
		cmbDepartment.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSectionName.removeAllItems();
				if(cmbDepartment.getValue()!=null)
				{
					sectionDataAdd();
				}
			}
		});

		chkDepartmentAll.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSectionName.removeAllItems();
				if(cmbUnit.getValue()!=null)
				{
					if(chkDepartmentAll.booleanValue()==true)
					{
						
						cmbDepartment.setValue(null);
						cmbDepartment.setEnabled(false);
						sectionDataAdd();
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
				cmbEmpName.removeAllItems();
				if(cmbSectionName.getValue()!=null)
				{
					addEmployeeData();
				}
			}
		});

		ChkSection.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmpName.removeAllItems();
				if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
				{
					if(ChkSection.booleanValue()==true)
					{
				
						cmbSectionName.setValue(null);
						cmbSectionName.setEnabled(false);
						cmbEmpName.focus();
						addEmployeeData();
					}
					else
					{
						cmbSectionName.setEnabled(true);
						cmbEmpName.removeAllItems();
					}
				}
			}
		});

		cButton.btnFind.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbUnit.getValue()==null )
				{
					showNotification("Please Select Unit Name ", Notification.TYPE_WARNING_MESSAGE);
					cmbUnit.focus();
				}
				
				if(cmbSectionName.getValue()==null && ChkSection.booleanValue()==false)
				{
					showNotification("Please Select Division Name ", Notification.TYPE_WARNING_MESSAGE);
					cmbSectionName.focus();
				}

				else if(cmbEmpName.getValue()==null &&ChkEmployee.booleanValue()==false)
				{
					showNotification("Please Select Employee Name ", Notification.TYPE_WARNING_MESSAGE);
					cmbEmpName.focus();
				}

				else 
				{
					findButtonEvent();
				}
			}
		});

		ChkEmployee.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event)
			{
				boolean bv = ChkEmployee.booleanValue();
				if(bv==true)
				{
					cmbEmpName.setValue(null);
					cmbEmpName.setEnabled(false);
					dFromDate.focus();
				}
				else
				{
					cmbEmpName.setEnabled(true);
				}
			}
		});

		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					autoId = lbLoanNo.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					txtAutoId.setValue(autoId);
					
					txtFindDate.setValue(lbAppDate.get(Integer.valueOf(event.getItemId().toString())).getValue());
					windowClose();
				}
			}
		});
	}

	private void findButtonEvent()
	{
		String Section = "";
		String Employee = "";
		// Section
		if(ChkSection.booleanValue() == true)
		{
			Section = "%";
		}
		else
		{
			Section = cmbSectionName.getValue().toString();
		}
		// Designation
		if(ChkEmployee.booleanValue() == true)
		{
			Employee = "%";
		}
		else
		{
			Employee = cmbEmpName.getValue().toString();
		}
		
		String from = dFormat.format(dFromDate.getValue())+" 00:00:00";
		String to = dFormat.format(dtoDate.getValue())+" 23:59:59";
		
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String Findquery = " select vLoanNo,la.dApplicationDate,epo.vEmployeeName,epo.vDesignationName from tbLoanApplication la inner join tbEmpOfficialPersonalInfo epo on epo.vEmployeeId=la.vAutoEmployeeId " +
					" where epo.vUnitId='"+cmbUnit.getValue().toString()+"' and epo.vDepartmentId like '"+(chkDepartmentAll.booleanValue()==true?"%":cmbDepartment.getValue())+"' and epo.vSectionId like '"+Section+"' and vAutoEmployeeId like '"+Employee+"' and" +
					" la.dApplicationDate between '"+from+"' and '"+to+"' order by la.dApplicationDate ";
			
			System.out.println("Find Query :" + Findquery);
			
			List <?> list = session.createSQLQuery(Findquery).list();
			
			if(!list.isEmpty())
			{
				tableclear();
				int i=0;
				for(Iterator <?> iter = list.iterator(); iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();
					lbLoanNo.get(i).setValue(element[0]);
					lbAppDate.get(i).setValue((element[1]));
					lbEmpName.get(i).setValue(element[3].toString());
					lbDesignation.get(i).setValue(element[2].toString());
					
					if((i)==lbAppDate.size()-1)
					{
						tableRowAdd(i+1);
					}
					i++;
				}
			}
			else
			{
				tableclear();
				showNotification("No data found!", Notification.TYPE_WARNING_MESSAGE); 
			}
		}
		catch (Exception ex)
		{
			showNotification("findButtonEvent", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void tableclear()
	{
		for(int i=0; i<lbSL.size(); i++)
		{
			lbLoanNo.get(i).setValue("");
			lbAppDate.get(i).setValue("");
			lbDesignation.get(i).setValue("");
		}
	}
	
	private void addEmployeeData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select epo.vEmployeeId,epo.vEmployeeName,epo.vEmployeeCode from tbLoanApplication la inner join tbEmpOfficialPersonalInfo epo on epo.vEmployeeId=la.vAutoEmployeeId where "
					+ " epo.vUnitId like '"+(cmbUnit.getValue()==null?"%":cmbUnit.getValue())+"' "
					+ " and epo.vDepartmentId like '"+(cmbDepartment.getValue()==null?"%":cmbDepartment.getValue())+"' "
					+ " and epo.vSectionId like '"+(cmbSectionName.getValue()==null?"%":cmbSectionName.getValue())+"' ";
		
			
			System.out.println("addEmployeeData :" + query);
			
			List <?> list = session.createSQLQuery(query).list();

			for (Iterator <?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element =  (Object[]) iter.next();	
				cmbEmpName.addItem(element[0]);
				cmbEmpName.setItemCaption(element[0],(String)element[2]+">>"+ element[1].toString());	
			}
		}
		catch(Exception ex)
		{
			showNotification("addEmployeeData", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void windowClose()
	{
		this.close();
	}

	private AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);

		setWidth("560px");
		setHeight("480px");
	
		
		lblUnit = new Label();
		lblUnit.setImmediate(true);
		lblUnit.setWidth("-1px");
		lblUnit.setHeight("-1px");
		lblUnit.setValue("Project Name  :");
		mainLayout.addComponent(lblUnit, "top:10.0px;left:15.0px;");

		cmbUnit = new ComboBox();
		cmbUnit.setImmediate(true);
		cmbUnit.setWidth("210px");
		cmbUnit.setHeight("24px");
		cmbUnit.setImmediate(true);
		cmbUnit.setNullSelectionAllowed(false);
		mainLayout.addComponent(cmbUnit, "top:8.0px;left:130.0px;");
		
		mainLayout.addComponent(new Label("Department :"), "top:35.0px;left:15.0px;");

		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("210px");
		cmbDepartment.setHeight("24px");
		cmbDepartment.setImmediate(true);
		cmbDepartment.setNullSelectionAllowed(false);
		mainLayout.addComponent(cmbDepartment, "top:33.0px;left:130.0px;");

		chkDepartmentAll = new CheckBox("All");
		chkDepartmentAll.setWidth("-1px");
		chkDepartmentAll.setHeight("24px");
		chkDepartmentAll.setImmediate(true);
		mainLayout.addComponent(chkDepartmentAll, "top:35.0px;left:345.0px;");

		
		lblSectionname = new Label();
		lblSectionname.setImmediate(true);
		lblSectionname.setWidth("-1px");
		lblSectionname.setHeight("-1px");
		lblSectionname.setValue("Section Name  :");
		mainLayout.addComponent(lblSectionname, "top:60px;left:15.0px;");

		cmbSectionName = new ComboBox();
		cmbSectionName.setImmediate(true);
		cmbSectionName.setWidth("210px");
		cmbSectionName.setHeight("24px");
		cmbSectionName.setImmediate(true);
		cmbSectionName.setNullSelectionAllowed(false);
		mainLayout.addComponent(cmbSectionName, "top:58px;left:130.0px;");

		ChkSection = new CheckBox("All");
		ChkSection.setWidth("-1px");
		ChkSection.setHeight("24px");
		ChkSection.setImmediate(true);
		mainLayout.addComponent(ChkSection, "top:60px;left:345.0px;");

		lblEmployeeName = new Label();
		lblEmployeeName.setImmediate(true);
		lblEmployeeName.setWidth("-1px");
		lblEmployeeName.setHeight("-1px");
		lblEmployeeName.setValue("Employee ID :");
		mainLayout.addComponent(lblEmployeeName, "top:85px;left:15.0px;");

		cmbEmpName = new ComboBox();
		cmbEmpName.setImmediate(true);
		cmbEmpName.setWidth("210px");
		cmbEmpName.setHeight("24px");
		cmbEmpName.setImmediate(true);
		cmbEmpName.setNullSelectionAllowed(false);
		mainLayout.addComponent(cmbEmpName, "top:83px;left:130.0px;");

		ChkEmployee = new CheckBox("All");
		ChkEmployee.setImmediate(false);
		ChkEmployee.setWidth("-1px");
		ChkEmployee.setHeight("24px");
		ChkEmployee.setImmediate(true);
		mainLayout.addComponent(ChkEmployee, "top:85px;left:345.0px;");

		lblFormDate = new Label("Application Date :");
		lblFormDate.setImmediate(true);
		lblFormDate.setWidth("-1px");
		lblFormDate.setHeight("-1px");
		mainLayout.addComponent(lblFormDate, "top:110px;left:15.0px;");

		dFromDate.setValue(new java.util.Date());
		dFromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dFromDate.setDateFormat("dd-MM-yyyy");
		dFromDate.setInvalidAllowed(false);
		dFromDate.setImmediate(true);
		dFromDate.setWidth("110");
		mainLayout.addComponent(dFromDate, "top:108px;left:130.0px;");

		lblToDate = new Label("To");
		lblToDate.setImmediate(true);
		lblToDate.setWidth("-1px");
		lblToDate.setHeight("-1px");
		mainLayout.addComponent(lblToDate, "top:110px;left:250.0px;");

		dtoDate.setValue(new java.util.Date());
		dtoDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dtoDate.setDateFormat("dd-MM-yyyy");
		dtoDate.setInvalidAllowed(false);
		dtoDate.setImmediate(true);
		dtoDate.setWidth("110");
		mainLayout.addComponent(dtoDate, "top:108px;left:275.0px;");

		cButton.btnFind.setWidth("80px");
		cButton.btnFind.setHeight("28px");
		mainLayout.addComponent(cButton.btnFind, "top:110px;left:430.0px;");

		mainLayout.addComponent(table, "top:155px;left:15.0px;");
		return mainLayout;
	}
}
