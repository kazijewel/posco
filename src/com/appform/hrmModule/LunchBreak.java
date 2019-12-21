package com.appform.hrmModule;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.hibernate.Session;
import org.hibernate.Transaction;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.TimeField;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;


public class LunchBreak extends Window {

	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	//----------------------------------<Form Varriables>--------------------------------------------s

	private PopupDateField dDate ;
	private ComboBox cmbSection;
	private CheckBox chkAll;
	private TextRead txtColor = new TextRead("");
	private Label lblCl= new Label("");

	private Table table= new Table();

	private boolean isSave=false;
	private boolean isRefresh=false;
	private boolean isUpdate=false;
	private boolean isFind= false; 

	private ArrayList <Label> lblEmployeeID     = new ArrayList <Label>();
	private ArrayList <Label> lblFingerID       = new ArrayList <Label>();
	private ArrayList <Label> lblEmployeeName   = new ArrayList <Label>();
	private ArrayList <Label> lblDesignation    = new ArrayList <Label>();
	private ArrayList <TimeField> hIn           = new ArrayList <TimeField>();
	private ArrayList <TimeField> mIn           = new ArrayList <TimeField>();
	private ArrayList <TimeField> sIn           = new ArrayList<TimeField>();
	private ArrayList <TimeField> hOu           = new ArrayList <TimeField>();
	private ArrayList <TimeField> mOu           = new ArrayList <TimeField>();
	private ArrayList <TimeField> sOu           = new ArrayList<TimeField>();
	private ArrayList <TextField> Inap          = new ArrayList<TextField>();
	private ArrayList <TextField> Outap         = new ArrayList<TextField>();
	private ArrayList <Label> lblBreakDuration  = new ArrayList <Label>();
	private ArrayList<CheckBox> tbChk           = new ArrayList<CheckBox>();

	ArrayList<Component> allComp = new ArrayList<Component>();
	CommonButton button = new CommonButton("New", "Save", "Edit", "","Refresh","","","","","Exit");

	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat tF = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss aa");
	public DecimalFormat df = new DecimalFormat("#0.00"); 

	public LunchBreak(SessionBean sessionBean) 
	{
		this.sessionBean=sessionBean;
		this.setResizable(false);
		this.setCaption("EXTRA OT WITH LUNCH DEDUCTION :: " + sessionBean.getCompany());

		buildMainLayout();
		tableInitialize();
		setContent(mainLayout);
		buttonAction();
		cmbSectiondataAdd();
		componentIniRefresh(true);
		btnIni(true);
		focusMove();
	}
	
	private void authenticationCheck()
	{
		if(!sessionBean.isSubmitable())
		{
			button.btnSave.setVisible(false);
		}

		if(!sessionBean.isUpdateable())
		{
			button.btnEdit.setVisible(false);
		}

		if(!sessionBean.isDeleteable())
		{
			button.btnDelete.setVisible(false);
		}
	}
	
	//-------------------Button Initialiazation---------------------

	private void buttonAction()
	{
		dDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(dDate.getValue()!=null)
				{
					cmbSection.removeAllItems();
					cmbSectiondataAdd();
				}
			}
		});

		cmbSection.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) 
			{
				tableclear();
				DataAdd();
				AddAction();
			}
		});

		button.btnNew.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isSave=false;
				isRefresh=false;
				isUpdate = false;
				isFind = false;
				newButtonEvent();
			}
		});

		button.btnEdit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate = true;
				componentIni(true);
			}
		});

		button.btnRefresh.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate =false;
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
		
		button.btnSave.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				formValidation();
			}
		});

	}

	//--------------------------------------------Design---------------------------------------------------------------------------------

	private AbsoluteLayout buildMainLayout() 
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);
		mainLayout.setWidth("1050px");
		mainLayout.setHeight("470px");

		dDate = new PopupDateField();
		dDate.setValue(new java.util.Date());
		dDate.setWidth("140px");
		dDate.setHeight("24px");
		dDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dDate.setDateFormat("dd-MM-yyyy");
		dDate.setInvalidAllowed(false);
		dDate.setImmediate(true);
		mainLayout.addComponent(new Label("Month : "), "top:20.0px;left:30.0px;");
		mainLayout.addComponent(dDate, "top:18.0px; left:80.0px;");

		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("220px");
		cmbSection.setHeight("24px");
		cmbSection.setNullSelectionAllowed(true);
		cmbSection.setNewItemsAllowed(false);
		cmbSection.setInputPrompt("Section Name");
		mainLayout.addComponent(new Label("Section Name : "), "top:20.0px;left:250.0px;");
		mainLayout.addComponent(cmbSection, "top:18.0px; left:350.0px;");

		mainLayout.addComponent(table, "top:55.0px; left:20.0px;");

		/*		txtColor = new TextRead();
		txtColor.setImmediate(true);
		txtColor.setWidth("274px");
		txtColor.setHeight("20px");
		txtColor.setStyleName("txtcolor");
		mainLayout.addComponent(txtColor, "top:56px;left:420px;");

		lblCl = new Label("<font color='#C1BF15'><b><Strong>Break Out Time<Strong></b></font>");
		lblCl.setImmediate(true);
		lblCl.setWidth("130px");
		lblCl.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblCl, "top:58px;left:425px;");*/

		chkAll = new CheckBox();
		chkAll.setImmediate(true);
		mainLayout.addComponent(chkAll, "top:57px;left:978px;");

		mainLayout.addComponent(button, "top:410.0px; left:80.0px;");

		return mainLayout;
	}

	private void tableInitialize()

	{
		table.setColumnCollapsingAllowed(true);
		table.setWidth("98%");
		table.setHeight("330px");
		table.setPageLength(0);
		table.setImmediate(true);

		table.addContainerProperty("EMP ID", Label.class , new Label());
		table.setColumnWidth("EMP ID",50);

		table.addContainerProperty("Finger ID", Label.class , new Label());
		table.setColumnWidth("Finger ID",50);

		table.addContainerProperty("Employee Name", Label.class , new Label());
		table.setColumnWidth("Employee Name",180);

		table.addContainerProperty("Designation", Label.class , new Label());
		table.setColumnWidth("Designation",180);

		table.addContainerProperty("hOu", TimeField.class , new TimeField());
		table.setColumnWidth("hOu",28);

		table.addContainerProperty("mOu", TimeField.class , new TimeField());
		table.setColumnWidth("mOu",28);

		table.addContainerProperty("sOu", TimeField.class , new TimeField());
		table.setColumnWidth("sOu",28);

		table.addContainerProperty("Outap", TextField.class , new TextField());
		table.setColumnWidth("Outap",28);

		table.addContainerProperty("hIn", TimeField.class , new TimeField());
		table.setColumnWidth("hIn",28);

		table.addContainerProperty("mIn", TimeField.class , new TimeField());
		table.setColumnWidth("mIn",28);

		table.addContainerProperty("sIn", TimeField.class , new TimeField());
		table.setColumnWidth("sIn",28);

		table.addContainerProperty("Inap", TextField.class , new TextField());
		table.setColumnWidth("Inap",28);

		table.addContainerProperty("Break Duration", Label.class , new Label());
		table.setColumnWidth("Break Duration",100);

		table.addContainerProperty("c", CheckBox.class , new CheckBox());
		table.setColumnWidth("c",15);

		table.setColumnAlignments(new String[] {
				Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_CENTER,
				Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,
				Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER});

		rowAddinTable();
	}

	public void rowAddinTable()
	{
		for(int i=0;i<32;i++)
		{
			tableRowAdd(i);
		}
	}

	public void tableRowAdd(final int ar)
	{
		lblEmployeeID.add(ar, new Label(""));
		lblEmployeeID.get(ar).setWidth("100%");
		lblEmployeeID.get(ar).setHeight("20px");
		lblEmployeeID.get(ar).addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {

				AddAction();
			}
		});
		lblFingerID.add(ar, new Label(""));
		lblFingerID.get(ar).setWidth("100%");
		lblFingerID.get(ar).setHeight("20px");
		lblFingerID.get(ar).addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {

				AddAction();
			}
		});
		lblEmployeeName.add(ar, new Label(""));
		lblEmployeeName.get(ar).setWidth("100%");
		lblEmployeeName.get(ar).setHeight("20px");
		lblEmployeeName.get(ar).addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {

				AddAction();
			}
		});

		lblDesignation.add(ar, new Label(""));
		lblDesignation.get(ar).setWidth("100%");
		lblDesignation.get(ar).setHeight("20px");
		lblDesignation.get(ar).addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {

				AddAction();
			}
		});

		hOu.add(ar, new TimeField());
		hOu.get(ar).setWidth("32px");
		hOu.get(ar).setHeight("20px");
		hOu.get(ar).setInputPrompt("hh");
		hOu.get(ar).setImmediate(true);
		hOu.get(ar).setStyleName("time");
		hOu.get(ar).setMaxLength(2);
		hOu.get(ar).addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {

				AddAction();
			}
		});
		mOu.add(ar, new TimeField());
		mOu.get(ar).setWidth("32px");
		mOu.get(ar).setHeight("20px");
		mOu.get(ar).setInputPrompt("mm");
		mOu.get(ar).setImmediate(true);
		mOu.get(ar).setStyleName("time");
		mOu.get(ar).setMaxLength(2);
		mOu.get(ar).addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {

				AddAction();
			}
		});
		sOu.add(ar, new TimeField());
		sOu.get(ar).setWidth("32px");
		sOu.get(ar).setHeight("20px");
		sOu.get(ar).setInputPrompt("ss");
		sOu.get(ar).setImmediate(true);
		sOu.get(ar).setStyleName("time");
		sOu.get(ar).setMaxLength(2);
		sOu.get(ar).addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {

				AddAction();
			}
		});
		Outap.add(ar, new TextField(""));
		Outap.get(ar).setWidth("28px");
		Outap.get(ar).setHeight("-1px");
		Outap.get(ar).setInputPrompt("AM");
		Outap.get(ar).setImmediate(true);
		Outap.get(ar).setMaxLength(2);
		Outap.get(ar).addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {

				AddAction();
			}
		});
		hIn.add(ar, new TimeField());
		hIn.get(ar).setWidth("32px");
		hIn.get(ar).setHeight("20px");
		hIn.get(ar).setInputPrompt("hh");
		hIn.get(ar).setImmediate(true);
		hIn.get(ar).setStyleName("time");
		hIn.get(ar).setMaxLength(2);
		hIn.get(ar).addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {

				AddAction();
			}
		});
		mIn.add(ar, new TimeField());
		mIn.get(ar).setWidth("32px");
		mIn.get(ar).setHeight("20px");
		mIn.get(ar).setInputPrompt("mm");
		mIn.get(ar).setImmediate(true);
		mIn.get(ar).setStyleName("time");
		mIn.get(ar).setMaxLength(2);
		mIn.get(ar).addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {

				AddAction();
			}
		});
		sIn.add(ar, new TimeField());
		sIn.get(ar).setWidth("32px");
		sIn.get(ar).setHeight("20px");
		sIn.get(ar).setInputPrompt("ss");
		sIn.get(ar).setImmediate(true);
		sIn.get(ar).setStyleName("time");
		sIn.get(ar).setMaxLength(2);
		sIn.get(ar).addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {

				AddAction();
			}
		});
		Inap.add(ar, new TextField(""));
		Inap.get(ar).setWidth("28px");
		Inap.get(ar).setHeight("-1px");
		Inap.get(ar).setInputPrompt("AM");
		Inap.get(ar).setImmediate(true);
		Inap.get(ar).setMaxLength(2);
		Inap.get(ar).addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {

				AddAction();
			}
		});

		lblBreakDuration.add(ar, new Label(""));
		lblBreakDuration.get(ar).setWidth("100%");
		lblBreakDuration.get(ar).setHeight("20px");
		lblBreakDuration.get(ar).addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {

				AddAction();
			}
		});

		tbChk.add(ar, new CheckBox(""));
		tbChk.get(ar).setWidth("100%");
		tbChk.get(ar).setImmediate(true);
		tbChk.get(ar).setEnabled(false);
		tbChk.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(tbChk.get(ar).booleanValue())
				{
					//count++;
				}
				else
				{
					//count=0;
				}
			}
		});

		table.addItem(new Object[]{lblEmployeeID.get(ar),lblFingerID.get(ar),
				lblEmployeeName.get(ar),lblDesignation.get(ar),hOu.get(ar),mOu.get(ar),
				sOu.get(ar),Outap.get(ar),hIn.get(ar),mIn.get(ar),sIn.get(ar),Inap.get(ar),
				lblBreakDuration.get(ar),tbChk.get(ar)},ar);
	}

	//-----------------------------------------------------All Methods-------------------------------------------------------------

	private void newButtonEvent() 
	{
		dDate.focus();
		componentIni(false);
		btnIni(false);
		txtClear();
	}

	public void focusMove()
	{
		allComp.add(dDate);
		allComp.add(cmbSection);

		for(int i=0; i<lblEmployeeID.size();i++)
		{
			allComp.add(hOu.get(i));
			allComp.add(mOu.get(i));
			allComp.add(sOu.get(i));
			allComp.add(Outap.get(i));
			allComp.add(hIn.get(i));
			allComp.add(mIn.get(i));
			allComp.add(sIn.get(i));
			allComp.add(Inap.get(i));
		}

		allComp.add(button.btnSave);
		allComp.add(dDate);
		new FocusMoveByEnter(this,allComp);
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
		dDate.setEnabled(!b);
		cmbSection.setEnabled(!b);
		table.setEnabled(!b);
	}

	private void componentIniEdi(boolean b) 
	{
		dDate.setEnabled(b);
		cmbSection.setEnabled(b);
		table.setEnabled(!b);
	}

	private void componentIniRefresh(boolean b) 
	{
		dDate.setEnabled(!b);
		cmbSection.setEnabled(!b);
		table.setEnabled(!b);
		txtColor.setEnabled(!b);
		lblCl.setEnabled(!b);

	}

	private void txtClear()
	{
		dDate.setValue(new java.util.Date());
		cmbSection.setValue(null);
	}

	private void tableclear()
	{
		for(int i =0;i<lblEmployeeID.size(); i++)
		{

			lblEmployeeID.get(i).setValue("");
			lblFingerID.get(i).setValue("");
			lblEmployeeName.get(i).setValue("");
			lblDesignation.get(i).setValue("");
			hIn.get(i).setValue("");
			mIn.get(i).setValue("");
			sIn.get(i).setValue("");
			hOu.get(i).setValue("");
			mOu.get(i).setValue("");
			sOu .get(i).setValue("");
			Inap.get(i).setValue("");
			Outap.get(i).setValue("");
			lblBreakDuration.get(i).setValue("");
			tbChk.get(i).setEnabled(false);
			tbChk.get(i).setValue(false);
		}
	}

	private void refreshButtonEvent() 
	{
		componentIniRefresh(true);
		btnIni(true);
		txtClear();	
	}

	private void cmbSectiondataAdd()
	{
		try
		{
			cmbSection.removeAllItems();
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			List list=session.createSQLQuery(" select distinct vSectionId,vSectionName from tbEmployeeAttendance where " +
					" MONTH(dAttDate)=MONTH('"+dFormat.format(dDate.getValue())+"') " +
					" and YEAR(dAttDate)=YEAR('"+dFormat.format(dDate.getValue())+"')").list();

			for (Iterator iter = list.iterator(); iter.hasNext();)
			{
				Object[] element =  (Object[]) iter.next();	
				cmbSection.addItem(element[0]);
				cmbSection.setItemCaption(element[0], element[1].toString());	
			}
			tx.commit();
		}

		catch(Exception ex)
		{
			this.getParent().showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void DataAdd() 
	{
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery(" SELECT lunchDate,vEmployeeId,vFigerId,vEmployeeName,vDeginationName," +
					" lunchOutTimehh,lunchOutTimemm,lunchOutTimess,outFormat,lunchInTimehh,lunchInTimemm,lunchInTimess," +
					" inFormat,vDeginationName,lunchBreakDuration from [funEmployeeLunchBreak] ('"+dFormat.format(dDate.getValue())+"','"+cmbSection.getValue()+"') ").list();
			Iterator iter=list.iterator();
			int i = 0;
			for(; iter.hasNext();)
			{
				Object[] element =  (Object[]) iter.next();
				lblEmployeeID.get(i).setValue((element[1]));
				lblFingerID.get(i).setValue(element[2]);
				lblEmployeeName.get(i).setValue(element[3]);
				hOu.get(i).setValue(element[5]);
				mOu.get(i).setValue(element[6]);
				sOu.get(i).setValue(element[7]);
				Outap.get(i).setValue(element[8]);
				hIn.get(i).setValue(element[9]);
				mIn.get(i).setValue(element[10]);
				sIn.get(i).setValue(element[11]);
				Inap.get(i).setValue(element[12]);
				lblDesignation.get(i).setValue(element[13].toString());
				lblBreakDuration.get(i).setValue(element[14].toString());
				i++;
			}

			tx.commit();
		}

		catch(Exception ex)
		{
			this.getParent().showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void AddAction() 
	{
		for(int i=0;i<lblEmployeeID.size();i++)
		{
			if(!lblEmployeeID.get(i).getValue().toString().isEmpty()){
				if(!lblFingerID.get(i).getValue().toString().isEmpty()){
					if(!lblEmployeeName.get(i).getValue().toString().isEmpty()){
						if(!hIn.get(i).getValue().toString().isEmpty()){
							if(!mIn.get(i).getValue().toString().isEmpty()){
								if(!sIn.get(i).getValue().toString().isEmpty()){
									if(!Outap.get(i).getValue().toString().isEmpty()){
										if(!hOu.get(i).getValue().toString().isEmpty()){
											if(!mOu.get(i).getValue().toString().isEmpty()){
												if(!sOu.get(i).getValue().toString().isEmpty()){
													if(!Inap.get(i).getValue().toString().isEmpty()){

														tbChk.get(i).setEnabled(true);
														tbChk.get(i).setValue(true);

													}
													else
													{
														tbChk.get(i).setEnabled(false);
														tbChk.get(i).setValue(false);
													}
												}
												else
												{
													tbChk.get(i).setEnabled(false);
													tbChk.get(i).setValue(false);
												}
											}
											else
											{
												tbChk.get(i).setEnabled(false);
												tbChk.get(i).setValue(false);
											}
										}
										else
										{
											tbChk.get(i).setEnabled(false);
											tbChk.get(i).setValue(false);
										}
									}
									else
									{
										tbChk.get(i).setEnabled(false);
										tbChk.get(i).setValue(false);
									}
								}
								else
								{
									tbChk.get(i).setEnabled(false);
									tbChk.get(i).setValue(false);
								}
							}
							else
							{
								tbChk.get(i).setEnabled(false);
								tbChk.get(i).setValue(false);
							}
						}
						else
						{
							tbChk.get(i).setEnabled(false);
							tbChk.get(i).setValue(false);
						}
					}
					else
					{
						tbChk.get(i).setEnabled(false);
						tbChk.get(i).setValue(false);
					}
				}
				else
				{
					tbChk.get(i).setEnabled(false);
					tbChk.get(i).setValue(false);
				}

			}
			else
			{
				tbChk.get(i).setEnabled(false);
				tbChk.get(i).setValue(false);
			}
		}
	}
	

	public boolean validTableSelect()
	{
		boolean ret = false;
		for(int i=0; i<lblEmployeeID.size(); i++)
		{
			if(!lblEmployeeID.get(i).getValue().toString().isEmpty() || 
					!lblFingerID.get(i).getValue().toString().isEmpty() || 
					!lblEmployeeName.get(i).getValue().toString().isEmpty() || 
					!lblEmployeeName.get(i).getValue().toString().isEmpty() || 
					!lblDesignation.get(i).getValue().toString().isEmpty()|| 
					!hIn.get(i).getValue().toString().isEmpty() || 
					!mIn.get(i).getValue().toString().isEmpty() || 
					!sIn.get(i).getValue().toString().isEmpty() ||
					!Inap.get(i).getValue().toString().isEmpty() || 
					!hOu.get(i).getValue().toString().isEmpty()|| 
					!mOu.get(i).getValue().toString().isEmpty() ||
					!sOu.get(i).getValue().toString().isEmpty() || 
					!Outap.get(i).getValue().toString().isEmpty() || 
					!lblBreakDuration.get(i).getValue().toString().isEmpty() || 
					!tbChk.get(i).booleanValue())
			{
				ret = true;
				break;
			}
		}
		return ret;
	}
	
	private void formValidation()
	{
		if(dDate.getValue()!=null)
		{
			if(cmbSection.getValue()!=null)
			{
				if(validTableSelect())
				{
					saveButtonEvent();	
				}
				else
				{
					showNotification("Warning!","Provide All Data To Table ",Notification.TYPE_WARNING_MESSAGE);
				}
			}
			else
			{
				getParent().showNotification("Warning,","Select Section Name.", Notification.TYPE_WARNING_MESSAGE);
				cmbSection.focus();
			}
		}
		else
		{
			getParent().showNotification("Warning,","Select Date.", Notification.TYPE_WARNING_MESSAGE);
			dDate.focus();
		}
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
					componentIni(true);
					btnIni(true);
					txtClear();
				}

			}
		});
	}

	private void insertdata()
	{
		String sql="";
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			
			for(int i=0;i<lblFingerID.size();i++) 
			{
				if(tbChk.get(i).booleanValue())
				{
				sql= " insert into tbLunchBreak(vEmployeeCode,vFingerId,vEmployeeName,vDesignationName," +
					 " vSectionId,vSectionName,dLunchDate,dLunchBreakOut,dLunchBreakIn,vBreakDuration,vUserName," +
					 " vUserIp,dEntryTime) values('"+lblEmployeeID.get(i).getValue().toString()+"'," +
					 " '"+lblFingerID.get(i).getValue().toString()+"'," +
					 " '"+lblEmployeeName.get(i).getValue().toString()+"','"+lblDesignation.get(i).getValue().toString()+"', " +
					 " '"+cmbSection.getValue().toString()+"','"+cmbSection.getItemCaption(cmbSection.getValue()).toString()+"'," +
					 " '"+dFormat.format(dDate.getValue())+"',(select convert(datetime,'"+dFormat.format(dDate.getValue())+"'+" +
					 " cast('"+hOu.get(i).getValue()+"'+':'+'"+mOu.get(i).getValue()+"'+':'+'"+sOu.get(i).getValue()+"'+" +
					 " '"+Outap.get(i).getValue()+"' as datetime)))," +
					 " (select convert(datetime,'"+dFormat.format(dDate.getValue())+"'+" +
					 " cast('"+hIn.get(i).getValue()+"'+':'+'"+mIn.get(i).getValue()+"'+':'+'"+sIn.get(i).getValue()+"'+" +
					 " '"+Inap.get(i).getValue()+"' as datetime)))," +
				     " '"+lblBreakDuration.get(i).getValue()+"','"+sessionBean.getUserName()+"'," +
					 " '"+sessionBean.getUserIp()+"',getdate())";
			
			System.out.println("sql"+sql);
			session.createSQLQuery(sql).executeUpdate();
				}
			}
			showNotification("All Information Saved Successfully");
			tx.commit();
		}
		
		catch (Exception exp)
		{
			tx.rollback();
			showNotification("InsertData", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
	
		}

	}
	
	//------------------------------------------------------------------------------------------------------------------
}
