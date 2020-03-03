package com.appform.hrmModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.hibernate.Session;
import org.hibernate.Transaction;
import com.common.share.CommonButton;
import com.common.share.MessageBox;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.common.share.CommonMethod;
import com.common.share.FocusMoveByEnter;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")

/*********************************************************
Author		: <Mohammad Didaru Alam>
Create Date	: <29-01-2020>
Update Date	: <29-01-2020> by <Mohammad Didaru Alam>
Description	: <For Generate Employee Leave Balance>
**********************************************************/


public class LeaveBalanceGenerate extends Window
{
	private AbsoluteLayout mainLayout;
	private SessionBean sessionBean;

	private Label lblGenerateDate;
	private PopupDateField dGenerateDate;

	private ProgressIndicator PI;
	private Worker1 worker1;
	
	private ArrayList<Component> allComp = new ArrayList<Component>();	

	private CommonButton cButton = new CommonButton("", "Save", "", "", "", "", "", "", "", "");

	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
	private CommonMethod cm;
	private String menuId = "";
	public LeaveBalanceGenerate(SessionBean sessionBean,String menuId) 
	{
		this.sessionBean= sessionBean;
		this.setCaption("LEAVE BALANCE GENERATE :: " +sessionBean.getCompany());
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		buildMainLayout();
		setContent(mainLayout);
		
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
					generateAction();
				}
				else
				{
					showNotification("Warning!","Provide Generate Date",Notification.TYPE_WARNING_MESSAGE);
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
					EmployeeLeaveBalanceGenerate();
					worker1 = new Worker1();
					worker1.start();
					PI.setEnabled(true);
					PI.setValue(0f);
					//dGenerateDate.setEnabled(false);
					cButton.btnSave.setEnabled(false);
				}
			}
		});
	}

	private void EmployeeLeaveBalanceGenerate()
	{
		String insertQuery="";
		
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		
		try
		{
			insertQuery = "exec prcLeaveEntitlementGenerate " +
					"'"+dFormat.format(dGenerateDate.getValue())+"',"
					+ "'%','%','%','%'," +
					"'"+sessionBean.getUserId()+"'," +
					"'"+sessionBean.getUserName()+"'," +
					"'"+sessionBean.getUserIp()+"'";
			System.out.println(insertQuery);
			
			session.createSQLQuery(insertQuery).executeUpdate();
			Notification n=new Notification("Generated Leave Balance Successfully!","",Notification.TYPE_TRAY_NOTIFICATION);
			n.setPosition(Notification.POSITION_TOP_RIGHT);
			showNotification(n);
			
			tx.commit();
		}
		catch(Exception ex)
		{
			tx.rollback();
			showNotification("EmployeeLeaveBalanceGenerate", ex.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}

	private void focusEnter()
	{
		allComp.add(cButton.btnSave);

		new FocusMoveByEnter(this,allComp); 
	}

	private void txtClear()
	{
		dGenerateDate.setValue(new java.util.Date());
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
		//dGenerateDate.setEnabled(false);

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
