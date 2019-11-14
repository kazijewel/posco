package com.appform.hrmModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.AmountField;
import com.common.share.CommonButton;
import com.common.share.MessageBox;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.common.share.CommonMethod;
import com.common.share.FocusMoveByEnter;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")

/*********************************************************
Author		: <Mohammad Didaru Alam>
Create Date	: <29-09-2019>
Update Date	: <29-09-2019> by <Mohammad Didaru Alam>
Description	: <Get Attendance Data From Attendance Device>
**********************************************************/


public class GetEmployeeAttendance extends Window
{
	private AbsoluteLayout mainLayout;
	private SessionBean sessionBean;

	private Label lblGenerateDate;
	private PopupDateField dGenerateDate;
	
	private Label lblAttendanceDate;
	private PopupDateField dAttendanceDate;


	private ProgressIndicator PI;
	private Worker1 worker1;
	
	private ArrayList<Component> allComp = new ArrayList<Component>();	

	private CommonButton cButton = new CommonButton("", "Save", "", "", "", "", "", "", "", "");

	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
	private CommonMethod cm;
	private String menuId = "";
	public GetEmployeeAttendance(SessionBean sessionBean,String menuId) 
	{
		this.sessionBean= sessionBean;
		this.setCaption("GET EMPLOYEE ATTENDANCE :: " +sessionBean.getCompany());
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		buildMainLayout();
		setContent(mainLayout);

		componentIni(true);
		setEventAction();

		authenticationCheck();
		focusEnter();
		dGenerateDate.focus();
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
		cButton.btnSave.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(dGenerateDate.getValue()!=null)
				{
					if(dAttendanceDate.getValue()!=null)
					{
						generateAction();
					}
					else
					{
						showNotification("Warning!","Provide Attendance Date",Notification.TYPE_WARNING_MESSAGE);
						dAttendanceDate.focus();
					}
				}
				else
				{
					showNotification("Warning!","Provide Generate Date",Notification.TYPE_WARNING_MESSAGE);
					dGenerateDate.focus();
				}
			}
		});

	}

	private void generateAction()
	{
		MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to Get Employee Attendance?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
		mb.show(new EventListener()
		{
			public void buttonClicked(ButtonType buttonType)
			{
				if(buttonType == ButtonType.YES)
				{
					EmployeeAttendanceGenerate();
					worker1 = new Worker1();
					worker1.start();
					PI.setEnabled(true);
					PI.setValue(0f);
					cButton.btnSave.setEnabled(false);
					componentIni(false);
				}
			}
		});
	}

	private void EmployeeAttendanceGenerate()
	{
		String insertQuery="";
		
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		
		try
		{
			insertQuery = "exec prcGetEmployeeAttendance " +
					"'"+dFormat.format(dGenerateDate.getValue())+"'," +
					"'"+dFormat.format(dAttendanceDate.getValue())+"'," +
					"'"+sessionBean.getUserName()+"'," +
					"'"+sessionBean.getUserIp()+"'";
			System.out.println(insertQuery);
			
			session.createSQLQuery(insertQuery).executeUpdate();
			Notification n=new Notification("Generated Attendance Successfully!","",Notification.TYPE_TRAY_NOTIFICATION);
			n.setPosition(Notification.POSITION_TOP_RIGHT);
			showNotification(n);
			
			tx.commit();
		}
		catch(Exception ex)
		{
			tx.rollback();
			showNotification("EmployeeAttendanceGenerate", ex.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}

	private void focusEnter()
	{
		allComp.add(dGenerateDate);		
		allComp.add(dAttendanceDate);
		allComp.add(cButton.btnSave);

		new FocusMoveByEnter(this,allComp); 
	}

	private void txtClear()
	{
		dGenerateDate.setValue(new java.util.Date());
		//dAttendanceDate.setValue(new java.util.Date());
	}

	private void componentIni(boolean b) 
	{
		dGenerateDate.setEnabled(b);
		dAttendanceDate.setEnabled(b);
	}

	private AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("480px");
		setHeight("200px");

		// lblGenerateDate
		lblGenerateDate = new Label("Generate Date :");
		lblGenerateDate.setImmediate(false);
		lblGenerateDate.setWidth("-1px");
		lblGenerateDate.setHeight("-1px");
		mainLayout.addComponent(lblGenerateDate, "top:20.0px;left:30.0px;");

		// dGenerateDate
		dGenerateDate = new PopupDateField();
		dGenerateDate.setImmediate(true);
		dGenerateDate.setWidth("160px");
		dGenerateDate.setHeight("-1px");
		dGenerateDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dGenerateDate.setDateFormat("dd-MM-yyyy");
		dGenerateDate.setValue(new java.util.Date());
		mainLayout.addComponent(dGenerateDate, "top:18.0px;left:150.0px;");

		// lblAttendanceDate
		lblAttendanceDate = new Label("Date Of Attendance :");
		lblAttendanceDate.setImmediate(false);
		lblAttendanceDate.setWidth("-1px");
		lblAttendanceDate.setHeight("-1px");
		mainLayout.addComponent(lblAttendanceDate, "top:50.0px;left:30.0px;");

		// dAttendanceDate
		dAttendanceDate= new PopupDateField();
		dAttendanceDate.setImmediate(true);
		dAttendanceDate.setWidth("160px");
		dAttendanceDate.setHeight("-1px");
		dAttendanceDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dAttendanceDate.setDateFormat("dd-MM-yyyy");
		dAttendanceDate.setValue(new java.util.Date());
		mainLayout.addComponent(dAttendanceDate, "top:48.0px;left:150.0px;");

		// CommonButton
		cButton.btnSave.setCaption("Generate");
		cButton.btnSave.setWidth("100.0px");
		mainLayout.addComponent(cButton,"top:85.0px;left:150.0px;");

		// PI
		PI=new ProgressIndicator();
		PI.setWidth("130px");
		PI.setImmediate(true);
		PI.setEnabled(false);
		mainLayout.addComponent(PI,"top:95.0px;left:270.0px;");

		return mainLayout;
	}

	public class Worker1 extends Thread 
	{
		int current = 1;
		public final static int MAX = 10;
		public void run() 
		{
			for (; current <= MAX; current++) 
			{
				try
				{
					Thread.sleep(700);
				}
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
				synchronized (getApplication()) 
				{
					prosessed();
				}
			}
			txtClear();
			componentIni(true);
			showNotification("Attendance Data Inserted Successfully");
		}
		public int getCurrent() 
		{
			return current;
		}
	}
	public void prosessed() 
	{
		int i = worker1.getCurrent();
		if (i == Worker1.MAX)
		{
			PI.setEnabled(false);
			cButton.btnSave.setEnabled(true);
			PI.setValue(1f);
		}
		else
		{
			PI.setValue((float) i / Worker1.MAX);
		}
	}
}
