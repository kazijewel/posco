package com.appform.hrmModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.AmountField;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

public class EmployeeRequsitionFromFindWindow extends Window
{
	private AbsoluteLayout mainLayout=new AbsoluteLayout();
	private TextField txtRequsitionId=new TextField();
	CheckBox chkUnitNameAll=new CheckBox("All");
	ComboBox  cmbUnit=new ComboBox();
	private Table table=new Table();
	public String requsition = "";

	private ArrayList<Label> lblRequisitionCode = new ArrayList<Label>();
	private ArrayList<Label> lblDesignation = new ArrayList<Label>();
	private ArrayList<Label> lblEmployeeNumber = new ArrayList<Label>();
	private ArrayList<Label> lblRequisitionDate = new ArrayList<Label>();
	private ArrayList<Label> lblRecruitmentDate = new ArrayList<Label>();
	private ArrayList<Label> lblEmployeeType = new ArrayList<Label>();
	private ArrayList<Label> lblRecruitmentType = new ArrayList<Label>();
	
	private com.common.share.SessionBean sessionBean;
	public EmployeeRequsitionFromFindWindow(SessionBean sessionBean,TextField txtRequsitionId)
	
	{
		this.txtRequsitionId = txtRequsitionId;
		this.sessionBean=sessionBean;
		this.setCaption("EMPLOYEE RECOSITION FIND :: "+sessionBean.getCompany());
		this.center();
		this.setWidth("720px");
		this.setHeight("450px");
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setModal(true);
		this.setResizable(false);
		this.setStyleName("cwindow");
		this.setContent(buildMainLayout());
		unitIdLoad();
		cmbUnit.focus();
		setEventAction();
	}
	
	public void setEventAction()
	{
		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					requsition = lblRequisitionCode.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					txtRequsitionId.setValue(requsition);
					close();
				}
			}
		});
		cmbUnit.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(cmbUnit.getValue()!=null){
					tableDataLoad(cmbUnit.getValue().toString());
				}
				else{
					if(!chkUnitNameAll.booleanValue())
					tableclear();
				}
			}
		});
		chkUnitNameAll.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				if(chkUnitNameAll.booleanValue()){
					tableDataLoad("%");
					cmbUnit.setEnabled(false);
					unitIdLoad();
				}
				else{
					tableclear();
					cmbUnit.setEnabled(true);
				}
			}
		});
	}
	
	private void tableclear()
	{
		for(int ar=0; ar<lblRequisitionCode.size(); ar++)
		{
			lblRequisitionCode.get(ar).setValue("");
			lblDesignation.get(ar).setValue("");
			lblEmployeeNumber.get(ar).setValue("");
			lblRequisitionDate.get(ar).setValue("");
			lblRecruitmentDate.get(ar).setValue("");
			lblEmployeeType.get(ar).setValue("");
			lblRecruitmentType.get(ar).setValue("");
		}
	}
	private void tableDataLoad(String unitId){
		tableclear();
		String sql="select vReQuisitionNo,vDesignationName,vEmployeeNumber,dRequsitionDate," +
				"dRequirementDate,vEmployeeType,vRequitmentType from tbEmployeeRequisitionForm " +
				"where vUnitId like '"+unitId+"' order by cast(SUBSTRING(vReQuisitionNo," +
				"CHARINDEX('-',vReQuisitionNo)+1,LEN(vReQuisitionNo)-CHARINDEX('-',vReQuisitionNo))" +
				"as int) desc";
		Iterator<?> iter=dbService(sql);
		int ar=0;
		while(iter.hasNext()){
			Object element[]=(Object[]) iter.next();
			lblRequisitionCode.get(ar).setValue(element[0].toString());
			lblDesignation.get(ar).setValue(element[1].toString());
			lblEmployeeNumber.get(ar).setValue(element[2].toString());
			lblRequisitionDate.get(ar).setValue(element[3].toString());
			lblRecruitmentDate.get(ar).setValue(element[4].toString());
			lblEmployeeType.get(ar).setValue(element[5].toString());
			lblRecruitmentType.get(ar).setValue(element[6].toString());
			if(ar==lblRequisitionCode.size()-1){
				tableRowAdd(ar+1);
			}
			ar++;
		}
	}
	private void unitIdLoad() {
		cmbUnit.removeAllItems();
		String sql="select vUnitId,vUnitName from tbEmployeeRequisitionForm order by vUnitName";
		Iterator<?> iter=dbService(sql);
		while(iter.hasNext()){
			Object element[]=(Object[]) iter.next();
			cmbUnit.addItem(element[0].toString());
			cmbUnit.setItemCaption(element[0].toString(), element[1].toString());
		}
	}
	private Iterator dbService(String sql){
		Session session=null;
		Iterator iter=null;
		try {
			System.out.println(sql);
			session=SessionFactoryUtil.getInstance().openSession();
			iter=session.createSQLQuery(sql).list().iterator();
		} 
		catch (Exception e) {
			showNotification(null,""+e,Notification.TYPE_ERROR_MESSAGE);
		}
		finally{
			if(session!=null){
				session.close();
			}
		}
		return iter;
	}
	private AbsoluteLayout buildMainLayout()
	{
		mainLayout.setImmediate(true);
		mainLayout.setHeight("100%");
		mainLayout.setWidth("100%");
		
		cmbUnit.setImmediate(true);
		cmbUnit.setHeight("-1px");
		cmbUnit.setWidth("270px");
		cmbUnit.setNewItemsAllowed(false);
		cmbUnit.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Project name : "),"top:20px;left:100px");
		mainLayout.addComponent(cmbUnit,"top:18px;left:180px");
		
		chkUnitNameAll.setImmediate(true);
		mainLayout.addComponent(chkUnitNameAll,"top:20px;left:450px");
		
		table.setSelectable(true);
		table.setWidth("100%");
		table.setHeight("330px");
		table.setImmediate(true);
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);	
		table.setStyleName("wordwrap-headers");

		table.addContainerProperty("Requisition Code", Label.class, new Label());
		table.setColumnWidth("Requisition Code",80);

		table.addContainerProperty("Designation", Label.class, new Label());
		table.setColumnWidth("Designation",150);
		
		table.addContainerProperty("Employee Number", Label.class, new Label());
		table.setColumnWidth("Employee Number",60);
		
		table.addContainerProperty("Requisition Date", Label.class, new Label());
		table.setColumnWidth("Requisition Date",70);
		
		table.addContainerProperty("Recruitment Date", Label.class, new Label());
		table.setColumnWidth("Recruitment Date",70);
		
		table.addContainerProperty("Employee Type", Label.class, new Label());
		table.setColumnWidth("Employee Type",80);
		
		table.addContainerProperty("Recruitment Type", Label.class, new Label());
		table.setColumnWidth("Recruitment Type",90);
		
		tableInitialise();
		mainLayout.addComponent(table,"top:60px;left:0px");
		
		return mainLayout;

	}
	public void tableInitialise()
	{
		for(int ar=0;ar<15;ar++)
		{
			tableRowAdd(ar);
		}
	}
	public void tableRowAdd(final int ar)
	{
		lblRequisitionCode.add(ar, new Label());
		lblRequisitionCode.get(ar).setWidth("100%");
		lblRequisitionCode.get(ar).setHeight("18px");
		lblRequisitionCode.get(ar).setImmediate(true);
		
		lblDesignation.add(ar, new Label());
		lblDesignation.get(ar).setWidth("100%");
		lblDesignation.get(ar).setHeight("-1px");
		lblDesignation.get(ar).setImmediate(true);
		
		lblEmployeeNumber.add(ar, new Label());
		lblEmployeeNumber.get(ar).setWidth("100%");
		lblEmployeeNumber.get(ar).setHeight("-1px");
		lblEmployeeNumber.get(ar).setImmediate(true);
		
		lblRequisitionDate.add(ar, new Label());
		lblRequisitionDate.get(ar).setWidth("100%");
		lblRequisitionDate.get(ar).setHeight("-1px");
		lblRequisitionDate.get(ar).setImmediate(true);
		
		lblRecruitmentDate.add(ar, new Label());
		lblRecruitmentDate.get(ar).setWidth("100%");
		lblRecruitmentDate.get(ar).setHeight("-1px");
		lblRecruitmentDate.get(ar).setImmediate(true);
		
		lblEmployeeType.add(ar, new Label());
		lblEmployeeType.get(ar).setWidth("100%");
		lblEmployeeType.get(ar).setHeight("-1px");
		lblEmployeeType.get(ar).setImmediate(true);
		
		lblRecruitmentType.add(ar, new Label());
		lblRecruitmentType.get(ar).setWidth("100%");
		lblRecruitmentType.get(ar).setHeight("-1px");
		lblRecruitmentType.get(ar).setImmediate(true);


		table.addItem(new Object[]{lblRequisitionCode.get(ar),lblDesignation.get(ar),lblEmployeeNumber.get(ar)
				,lblRequisitionDate.get(ar),lblRecruitmentDate.get(ar),lblEmployeeType.get(ar),lblRecruitmentType.get(ar)},ar);
	}
}