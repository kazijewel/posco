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
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class GradeInfo extends Window 
{
	CommonButton button = new CommonButton("New", "Save", "Edit", "","Refresh","","","","","Exit");
	private FormLayout formLayout = new FormLayout();
	private VerticalLayout mainLayout = new VerticalLayout();
	private HorizontalLayout btnLayout = new HorizontalLayout();
	private HorizontalLayout space = new HorizontalLayout();

	private ComboBox cmbdepartmentName = new ComboBox("Grade Search:");
	private TextRead txtGradeId = new TextRead("Grade ID:");
	private TextField txtGradeName = new TextField("Grade Name: ");
	private TextField txtPayScale = new TextField("Pay Scale:");

	private String comWidth = "230px";
	private boolean isUpdate=false;
	private SessionBean sessionBean;
	int flag=1;
	private int comId ;
	
	String computerName = "";
	String userName = "";

	public GradeInfo(SessionBean sessionBean)
	{
//		computerName = sessionBean.getComputerName();
		userName = sessionBean.getUserName();
		
		this.sessionBean = sessionBean;
		this.setCaption("GRADE INFORMATION :: "+sessionBean.getCompany());
		this.setWidth("500px");
		this.setResizable(false);
		
		cmpInitialize();
		cmpAdding();
		variousAction();
	}
	
	private void cmpInitialize()
	  {
		cmbdepartmentName.setWidth(comWidth);
		cmbdepartmentName.setNewItemsAllowed(true);
		cmbdepartmentName.setNullSelectionAllowed(false);
		cmbdepartmentName.setImmediate(true);
		txtPayScale.setWidth(comWidth);
		txtPayScale.setRows(2);
		txtGradeId.setWidth("80px");    	
		txtGradeName.setWidth(comWidth);    	


		formLayout.addComponent(cmbdepartmentName);
		formLayout.addComponent(txtGradeId);
		formLayout.addComponent(txtGradeName);
		formLayout.addComponent(txtPayScale);

		space.setWidth("80px");
	  }
	
	private void cmpAdding()
	  {
		btnLayout.addComponent(button);		

		btnLayout.setSpacing(true);
		formLayout.setMargin(true);		
		mainLayout.setMargin(true);

		mainLayout.addComponent(formLayout);
		mainLayout.addComponent(btnLayout);
		mainLayout.setComponentAlignment(btnLayout, Alignment.BOTTOM_CENTER);
		addComponent(mainLayout);
		addCmbDepartmentName();
		initialise(false);
		cmbdepartmentName.focus();

		Component ob[] = {cmbdepartmentName,txtGradeName,txtPayScale,button.btnNew,button.btnEdit,button.btnSave,button.btnRefresh,button.btnExit};
		new FocusMoveByEnter(this,ob);
		
	  }

	private void variousAction()
	{


		button.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) {
				cmbdepartmentName.focus();
				newBtnAction();
				System.out.println("New");

			}
		});

		txtPayScale.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {

				System.out.println(flag);
				int aRow=txtPayScale.getRows();
				if(aRow==flag){

					System.out.println(flag+" "+aRow);
					//contactNumber.focus();
				}
				flag++;

			}
		});
		button.btnEdit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) {
				updateBtnAction(event);
			}
		});

		button.btnSave.addListener( new Button.ClickListener() 
		{			
			public void buttonClick(ClickEvent event) {
				saveBtnAction(event);
			}
		});

		button.btnRefresh.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) {

				clearBtnAction(event);
			}
		});

		button.btnExit.addListener( new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) {
				exitAction();
			}
		});

		cmbdepartmentName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbdepartmentName.getValue()!=null){
                  dataAddFields();
				}
				else{
					if(!isUpdate)
						txtClear();
				}
			}
		});
	}
	
  private void dataAddFields()
	{
	  try
	   {
		 Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		 Transaction tx = session.beginTransaction();
		 String query = "select * from GradeInfo  where GradeID = '"+cmbdepartmentName.getValue()+"'";
		 System.out.println(query);
	
		 List list = session.createSQLQuery(query).list();
		
		 if(list.iterator().hasNext()) {
			Object[] element = (Object[]) list.iterator().next();
			
		    txtGradeId.setValue(element[1]);
			txtGradeName.setValue(element[2]);
			txtPayScale.setValue(element[3]);
			button.btnEdit.focus();
		  }	
	  }
	  catch (Exception ex) {
		this.getParent().showNotification("Error",ex.toString(), Notification.TYPE_ERROR_MESSAGE);
	    }
	}


	private void newBtnAction(){
		cmbdepartmentName.removeAllItems();
		cmbdepartmentName.setValue(null);
		button.btnNew.setEnabled(false);
		button.btnEdit.setEnabled(false);

		button.btnSave.setEnabled(true);
		button.btnRefresh.setEnabled(true);	
		setEditable(true);
		txtClear();
		txtGradeId.setValue(autoId());
		txtGradeName.focus();	
	}

	private void addCmbDepartmentName()
	{
		try
		{
			cmbdepartmentName.removeAllItems();

			String query = "select GradeID,GradeName from GradeInfo order by AutoID";
			System.out.println(query);
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			List list = session.createSQLQuery(query).list();
			for(Iterator iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				if (element[0] != null)
				{
					cmbdepartmentName.addItem(element[0]);
					cmbdepartmentName.setItemCaption(element[0], element[1].toString());					
				}
			}
		}
		catch(Exception ex)
		{
			this.getParent().showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
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
			txtGradeId.setValue("");
			String query = "Select isnull(max(GradeID)+1,1) as gid from GradeInfo";
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
				if (txtGradeName.getValue().toString().trim().isEmpty())
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
								button.btnSave.setEnabled(false);
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
									button.btnSave.setEnabled(false);
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
		cmbdepartmentName.setValue(null);
		txtClear();
		initialise(false);
		addCmbDepartmentName();
		cmbdepartmentName.focus();
	}

	private void txtClear()
	{
		//	cosigneeName.setValue(null);
		//cosigneeName.setValue("");
		txtGradeName.setValue("");
		txtGradeId.setValue("");
		txtPayScale.setValue("");
	}

	private void initialise(boolean stat)
	{
		setEditable(stat);
		button.btnEdit.setEnabled(!stat);
		button.btnNew.setEnabled(!stat);
		button.btnSave.setEnabled(stat);
		button.btnRefresh.setEnabled(stat);
		button.btnDelete.setEnabled(!stat);
	}

	private void setEditable(boolean tf)
	{		
		txtPayScale.setEnabled(tf);
		txtGradeName.setEnabled(tf);
		cmbdepartmentName.setEnabled(!tf);
	}

	private void updateBtnAction(ClickEvent e)
	{
		if (cmbdepartmentName.getValue() != null)
		{
			button.btnEdit.setEnabled(false);
			button.btnNew.setEnabled(false);
			button.btnDelete.setEnabled(false);
			setEditable(true);
			button.btnSave.setEnabled(true);
			button.btnRefresh.setEnabled(true);
			button.btnCancel.setEnabled(true);
			isUpdate=true;
			//button.btnNew.setEnabled(true);
			//address.focus();			
		}
		else
		{
			this.getParent().showNotification("Warning","Please Select Department Name.",Notification.TYPE_WARNING_MESSAGE);
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
				String orderNo = "SELECT max(convert(int,iorderby)+1) FROM SectionInfo";
				Iterator iter = session.createSQLQuery(orderNo).list().iterator();
				if (iter.hasNext()) {
					maxOrderNo = iter.next().toString();
				}

			} catch (Exception ex) {

				System.out.print(ex);
			}

			String sql = "Insert into GradeInfo values('"+txtGradeId.getValue().toString().trim()+"','"+txtGradeName.getValue().toString().trim()+"','"+txtPayScale.getValue().toString().trim()+"','"+userName+"',current_timestamp,'"+computerName+"')";
			System.out.println(sql);
			session.createSQLQuery(sql).executeUpdate();
			tx.commit();
			this.getParent().showNotification("All information save successfully.");
			initialise(false);
			txtClear();
			addCmbDepartmentName();
			cmbdepartmentName.focus();
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
			String query="delete from GradeInfo where GradeID='"+txtGradeId.getValue().toString()+"' ";
			System.out.println(query);
			session.createSQLQuery(query).executeUpdate();
			tx.commit();
			this.getParent().showNotification("Deletion Successful.");
			initialise(false);
			txtClear();
			addCmbDepartmentName();
			isUpdate=false;
			cmbdepartmentName.focus();

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
			String query="update GradeInfo set GradeName='"+txtGradeName.getValue().toString().trim()+"',PayScale='"+txtPayScale.getValue().toString().trim()+"' where GradeID='"+txtGradeId.getValue().toString()+"'";
			System.out.println(query);
			session.createSQLQuery(query).executeUpdate();
			tx.commit();
			this.getParent().showNotification("All information updated successfully.");
			initialise(false);
			txtClear();
			addCmbDepartmentName();
			isUpdate=false;
			cmbdepartmentName.focus();

		}
		catch(Exception ex)
		{
			this.getParent().showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
			initialise(true);
		}
	}
}
