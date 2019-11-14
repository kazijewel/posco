package com.appform.hrmModule;

import java.util.Iterator;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import com.common.share.*;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.BaseTheme;

@SuppressWarnings("serial")
public class LeaveType extends Window 
{
	private CommonButton cButton = new CommonButton("", "Save", "Edit","", "Refresh", "Find", "", "", "", "Exit");
	
	private HorizontalLayout space = new HorizontalLayout();
	//private TextField txtLeaveType ;
	private TextRead txtLeaveTypeId ;
	private TextField txLeaveTypeName ;
	//private Label lblleaveTtypeSearch;
	private Label lblleaveTypeID;
	private Label lblleaveTypeName; 
	private AbsoluteLayout mainLayout;
	private String comWidth = "230px";
	private boolean isUpdate=false;
	private SessionBean sessionBean;
	private TextField txtreceiptID = new TextField();
	int flag=1;
	private int comId ;
	

	public LeaveType(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("LEAVE TYPE INFO. :: "+sessionBean.getCompany());
		this.setWidth("500px");
		this.setResizable(false);
		buildMainLayout();
		setContent(mainLayout);
		variousAction();
		//addcmbLeaveTypeSrch();
		componentIni(false) ;
		btnIni(false);
		authenticationCheck();
	}

	private void authenticationCheck()
	{
		if(!sessionBean.isSubmitable()){
			cButton.btnSave.setVisible(false);
		}
		
		if(!sessionBean.isUpdateable()){
			cButton.btnEdit.setVisible(false);
		}
		
		if(!sessionBean.isDeleteable()){
			cButton.btnDelete.setVisible(false);
		}
	}

	private void variousAction()
	{

		cButton.btnEdit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) {
				updateBtnAction(event);
			}
		});
				
		cButton.btnFind.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				findButtonEvent();
			}
		});
		
		cButton.btnSave.addListener( new Button.ClickListener() 
		{			
			public void buttonClick(ClickEvent event) {
				saveBtnAction(event);
			}
		});

		cButton.btnRefresh.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) {

				clearBtnAction(event);
			}
		});

		cButton.btnExit.addListener( new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) {
				exitAction();
			}
		});

	}
	
	private void findButtonEvent() 
	{
		Window win = new LeaveTypeFindWindo(sessionBean, txtreceiptID);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if (txtreceiptID.getValue().toString().length() > 0)
				{
					txtClear();
					
					findInitialise(txtreceiptID.getValue().toString());
				}
			}
		});

		this.getParent().addWindow(win);
	}
	
	private void findInitialise( String txtid) 
	{
		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			String selectQuery= "select iLeaveTypeId, vLeaveTypeName from tbLeaveType  where iLeaveTypeId='"+txtid+"'  order by AutoID  ";
			
			List list = session.createSQLQuery(selectQuery).list();

			if(!list.isEmpty()){
			if (list.iterator().hasNext()) 
			{
				Object[] element = (Object[]) list.iterator().next();

				//txtLeaveType.setValue(element[0]);
				txtLeaveTypeId.setValue(element[0]);
				txLeaveTypeName.setValue(element[1]);	
			}
			}
			else{
				this.getParent().showNotification("There is no Data",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch (Exception exp) 
		{
			this.getParent().showNotification("Error", exp + "",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}
	
	private void exitAction()
	{
		this.close();
	}

	private Object autoId(){
	    int Id = 0;
		Transaction tx=null;
		try
		{
			txtLeaveTypeId.setValue("");
			String query = "Select isnull(max(iLeaveTypeID)+1,1) as LTid from tbLeaveType";
			System.out.println(query);
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			int num = 0;
			Iterator iter = session.createSQLQuery(query).list().iterator();
			if (iter.hasNext()) {
				num = Integer.parseInt(iter.next().toString());
				Id = num;
			}	
		}
		catch(Exception ex)
		{
			this.getParent().showNotification("Erroroioioioi", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}

		return Id;
	}

	private void saveBtnAction(ClickEvent e)
	{
		try
		{

			if (sessionBean.isUpdateable())
			{	
				if (txLeaveTypeName.getValue().toString().trim().isEmpty())
				{
					this.getParent().showNotification("Warning :", "Please Enter Department.", Notification.TYPE_WARNING_MESSAGE);
					//address.focus();
				}
				else{
					if(isUpdate)
					{
						MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to update  information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
						mb.show(new EventListener()
						{
							public void buttonClicked(ButtonType buttonType)
							{
								cButton.btnSave.setEnabled(false);
								updateData();
							}
						});
					}
					else
					{
						MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to save  information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
						mb.show(new EventListener()
						{
							public void buttonClicked(ButtonType buttonType)
							{
								if(buttonType == ButtonType.YES)
								{
									cButton.btnSave.setEnabled(false);
									saveData();
								}
							}
						});
					}
				}

			}
			else
			{
				this.getParent().showNotification("Warning :", "You Are Not Permitted to Perform This Task", Notification.TYPE_WARNING_MESSAGE);
			}
		}



		catch(Exception ex)
		{
			this.getParent().showNotification("Error.", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}	

	private void clearBtnAction(ClickEvent e)
	{
		
		txtClear();
		componentIni(false) ;
		btnIni(false);
	}

	private void txtClear()
	{
		txLeaveTypeName.setValue("");
		txtLeaveTypeId.setValue("");
	}

	private void initialise(boolean stat)
	{
		setEditable(stat);
		cButton.btnEdit.setEnabled(!stat);
		cButton.btnNew.setEnabled(!stat);
		cButton.btnSave.setEnabled(stat);
		cButton.btnRefresh.setEnabled(stat);
		cButton.btnDelete.setEnabled(!stat);
	}

	private void setEditable(boolean tf)
	{		
		txLeaveTypeName.setEnabled(tf);
	}

	private void updateBtnAction(ClickEvent e)
	{
		if (!txLeaveTypeName.getValue().toString().isEmpty())
		{
			componentIni(true) ;
			btnIni(true);
			setEditable(true);
			isUpdate=true;
		}
		else
		{
			this.getParent().showNotification("Warning","There Is No Data To Update.",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void saveData()
	{
		Transaction tx;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();
		System.out.println("Here ok");
		try
		{
			String maxOrderNo="";

			try {
				String orderNo = "SELECT max(convert(int,iorderby)+1) FROM tbSectionInfo";
				Iterator iter = session.createSQLQuery(orderNo).list().iterator();
				if (iter.hasNext()) {
					maxOrderNo = iter.next().toString();
				}

			} catch (Exception ex) {

				System.out.print(ex);
			}

			String sql = "Insert into tbLeaveType values('"+txtLeaveTypeId.getValue().toString().trim()+"','"+txLeaveTypeName.getValue().toString().trim()+"','"+sessionBean.getUserId()+"',current_timestamp,'"+sessionBean.getUserIp()+"')";
			System.out.println(sql);
			session.createSQLQuery(sql).executeUpdate();
			tx.commit();
			this.getParent().showNotification("All information save successfully.");
			initialise(false);
			txtClear();
			//cmbLeaveTypeSrch.focus();
			isUpdate=false;
		}
		catch(Exception ex)
		{
			this.getParent().showNotification("Error in Insertion", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
			initialise(true);
		}
	}

	private void deleteAction()
	{
		Transaction tx;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();
		try
		{
			String query="delete from tbGradeInfo where GradeID='"+txtLeaveTypeId.getValue().toString()+"' ";
			System.out.println(query);
			session.createSQLQuery(query).executeUpdate();
			tx.commit();
			this.getParent().showNotification("Deletion Successful.");
			initialise(false);
			txtClear();
			//addcmbLeaveTypeSrch();
			isUpdate=false;
			txLeaveTypeName.focus();

		}
		catch(Exception ex)
		{
			this.getParent().showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
			initialise(true);
		}
	}

	private void updateData()
	{
		Transaction tx;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();
		try
		{
			String query = " update tbLeaveType set vLeaveTypeName='"+txLeaveTypeName.getValue().toString().trim()+"'," +
							" vUserName = '"+sessionBean.getUserId()+"',dDateTime=current_timestamp," +
							" vPCIP='"+sessionBean.getUserIp()+"' where iLeaveTypeID='"+txtLeaveTypeId.getValue().toString()+"' ";
			System.out.println(query);
			session.createSQLQuery(query).executeUpdate();
			tx.commit();
			this.getParent().showNotification("All information updated successfully.");
			//initialise(false);
			componentIni(false) ;
			btnIni(false);
			txtClear();
			//addcmbLeaveTypeSrch();
			isUpdate=false;
			txLeaveTypeName.focus();

		}
		catch(Exception ex)
		{
			this.getParent().showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
			initialise(true);
		}
	}
	
	private void componentIni(boolean b) 
	{
		//txtLeaveType.setEnabled(!b);
		txtLeaveTypeId.setEnabled(!b);
		txLeaveTypeName.setEnabled(b);
	}
	
	private void btnIni(boolean t) 
	{
		cButton.btnEdit.setEnabled(!t);
		cButton.btnSave.setEnabled(t);
		cButton.btnRefresh.setEnabled(t);
		cButton.btnFind.setEnabled(!t);
		//cButton.btnExit.setEnabled(!t);
	}
	
	private AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);
		
		setWidth("550px");
		setHeight("200px");
		
		lblleaveTypeID = new Label();
		lblleaveTypeID.setImmediate(true);
		lblleaveTypeID.setWidth("-1px");
		lblleaveTypeID.setHeight("-1px");
		lblleaveTypeID.setValue("Leave Type Id :");
		mainLayout.addComponent(lblleaveTypeID, "top:30.0px;left:65.0px;");
	
		txtLeaveTypeId = new TextRead();
		txtLeaveTypeId.setImmediate(true);
		txtLeaveTypeId.setWidth("80px");
		txtLeaveTypeId.setHeight("-1px");
		mainLayout.addComponent(txtLeaveTypeId, "top:28.0px;left:180.0px;");
		
		lblleaveTypeName = new Label();
		lblleaveTypeName.setImmediate(true);
		lblleaveTypeName.setWidth("-1px");
		lblleaveTypeName.setHeight("-1px");
		lblleaveTypeName.setValue("Leave Type Name :");
		mainLayout.addComponent(lblleaveTypeName, "top:55.0px;left:65.0px;");
		
		txLeaveTypeName = new TextField();
		txLeaveTypeName.setImmediate(true);
		txLeaveTypeName.setWidth("-1px");
		txLeaveTypeName.setHeight("-1px");
		mainLayout.addComponent(txLeaveTypeName, "top:53.0px;left:180.0px;");
		
		mainLayout.addComponent(cButton, "top:100.0px;left:50.0px;");
		
		return mainLayout;
	}
}
