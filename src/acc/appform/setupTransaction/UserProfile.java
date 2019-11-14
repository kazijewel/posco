package acc.appform.setupTransaction;

import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class UserProfile extends Window {

	private VerticalLayout vl = new VerticalLayout();
	private HorizontalLayout btnLayout = new HorizontalLayout();
	private FormLayout fl = new FormLayout();
	private SessionBean sessionBean;
	private boolean isUpdate = false;

	private Button newBtn;
	private Button saveBtn;
	private Button editBtn;
	private Button deleteBtn;
	private Button clearBtn;
	private Button findBtn;

	public ComboBox fUserName = new ComboBox("Usesr Name");
	public TextField userName = new TextField("User Name:");
	public TextField pass = new TextField("Password:");
	public TextField conPass = new TextField("Confirm Password:");
	public ComboBox userGroup = new ComboBox("User Group:");

	private String w = "220px";

	public UserProfile(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("USER PROFILE :: "+sessionBean.getCompany());
		this.setWidth("450px");
		this.setResizable(false);


		newBtn = new Button("New",
				new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				newBtnAction(event);
			}
		});
		btnLayout.addComponent(newBtn);

		saveBtn = new Button("Save",
				new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				saveBtnAction(event);
			}
		});
		btnLayout.addComponent(saveBtn);

		editBtn = new Button("Update",
				new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				updateBtnAction(event);
			}
		});
		btnLayout.addComponent(editBtn);

		deleteBtn = new Button("Delete",
				new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				deleteBtnAction(event);
			}
		});
		btnLayout.addComponent(deleteBtn);

		clearBtn = new Button("Clear",
				new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				clearBtnAction(event);
			}
		});
		btnLayout.addComponent(clearBtn);

		findBtn  = new Button("Find",
				new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				findBtnAction(event);
			}
		});

		vl.setMargin(true);

		fl.addComponent(fUserName);
		fUserName.setWidth(w);
		fl.addComponent(findBtn);
		fl.addComponent(userName);
		userName.setWidth(w);
		fl.addComponent(pass);
		pass.setWidth(w);
		pass.setSecret(true);
		fl.addComponent(conPass);
		conPass.setWidth(w);
		conPass.setSecret(true);
		fl.addComponent(userGroup);
		userGroup.setWidth(w);

		btnLayout.setSpacing(true);
		btnLayout.setMargin(true);
		btnLayout.addComponent(newBtn);
		btnLayout.addComponent(saveBtn);
		btnLayout.addComponent(editBtn);
		btnLayout.addComponent(deleteBtn);
		btnLayout.addComponent(clearBtn);

		vl.addComponent(fl);
		vl.addComponent(btnLayout);
		this.addComponent(vl);
	}
	private void newBtnAction(ClickEvent e){
		setEditable(true);
		newBtn.setEnabled(false);
		saveBtn.setEnabled(true);
		//address.setValue("");
	}
	private void saveBtnAction(ClickEvent e){
		if(isUpdate)
		{
			MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to update address/Office"+"?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{

						saveBtn.setEnabled(false);
						updateData();
					}
				}
			});
		}else{
			MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to save all information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						saveBtn.setEnabled(false);
						insertData();
					}
				}
			});
		}
	}
	private void updateData(){
		try
		{
			/*DataHandler db = new DataHandler();
			db.insertData("UPDATE addressoffice SET name = '"+address.getValue()+"', insertTime = now() WHERE id = "+upaddressOfficeId);
			this.getParent().showNotification(
                    "",
                    "Department/Office "+address.getValue()+" save successfully.",
                    Notification.TYPE_HUMANIZED_MESSAGE);
			db.disconnectFromDatabase();
			 */
		}catch(Exception exp){
			this.getParent().showNotification(
					"Error",
					exp+"",
					Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private void insertData(){
		try
		{
			/*
			DataHandler db = new DataHandler();
			db.insertData("INSERT INTO addressoffice(name,insertTime) VALUES('"+address.getValue()+"',now())");
			this.getParent().showNotification(
                    "",
                    "Department/Office "+address.getValue()+" save successfully.",
                    Notification.TYPE_HUMANIZED_MESSAGE);
			db.disconnectFromDatabase();
			 */
		}catch(Exception exp){
			this.getParent().showNotification(
					"Error",
					exp+"",
					Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private void updateBtnAction(ClickEvent e){
		if(sessionBean.isAdmin()){
			isUpdate = true;
			//companyName.setEnabled(false);
			editBtn.setEnabled(false);
			//address.setEnabled(true);
			saveBtn.setEnabled(true);
		}else{
			this.getParent().showNotification(
					"Authentication Failed",
					"You have not proper authentication for update.",
					Notification.TYPE_ERROR_MESSAGE);
		}

	}
	private void deleteBtnAction(ClickEvent e)
	{
		MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to delete department/Office "+"?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
		mb.show(new EventListener()
		{
			public void buttonClicked(ButtonType buttonType)
			{
				if(buttonType == ButtonType.YES)
				{		
					deleteData();
				}
			}
		});
	}
	private void deleteData(){
		if(sessionBean.isAdmin()){
			try
			{
				/*
				DataHandler db = new DataHandler();
				db.insertData("DELETE FROM addressoffice WHERE id = "+upaddressOfficeId);
				this.getParent().showNotification(
	                    "",
	                    "Department/Office "+companyName.getItemCaption(upaddressOfficeId)+" delete successfully.",
	                    Notification.TYPE_HUMANIZED_MESSAGE);
				db.disconnectFromDatabase();
				 */
			}catch(Exception exp){
				this.getParent().showNotification(
						"Error",
						exp+"",
						Notification.TYPE_ERROR_MESSAGE);
			}
		}else{
			this.getParent().showNotification(
					"Authentication Failed",
					"You have not proper authentication for update.",
					Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private void clearBtnAction(ClickEvent e){
		initialise();
	}
	private void findBtnAction(ClickEvent e){

		isUpdate = false;
		editBtn.setEnabled(false);
		saveBtn.setEnabled(false);
		deleteBtn.setEnabled(false);
		newBtn.setEnabled(false);
		//companyName.setEnabled(true);
		//String sql = ;
		try
		{
			/*
			DataHandler db = new DataHandler();
			ResultSet rs = db.getStatement().executeQuery("SELECT id,name FROM addressoffice");
			while(rs.next()){
				companyName.addItem(rs.getObject(1).toString());
				companyName.setItemCaption(rs.getObject(1).toString(),rs.getObject(2).toString());
			}
			db.disconnectFromDatabase();
			 */
		}catch(Exception exp){
			this.getParent().showNotification(
					"Error",
					exp+"",
					Notification.TYPE_ERROR_MESSAGE);
		}
		//companyName.focus();

	}
	private void setFindData()
	{

		editBtn.setEnabled(true);
		deleteBtn.setEnabled(true);
		saveBtn.setEnabled(false);
	}
	private void initialise(){
		isUpdate = false;
		setEditable(false);
		//companyName.setEnabled(false);
		newBtn.setEnabled(true);
		saveBtn.setEnabled(false);
		editBtn.setEnabled(false);
		deleteBtn.setEnabled(false);
		//address.setValue("");
	}
	private void setEditable(boolean tf)
	{
		//address.setEnabled(tf);
	}
}
