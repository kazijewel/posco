package com.appform.hrmModule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import com.common.share.*;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class AddBirthDayCertificate extends Window 
{
	private VerticalLayout mainLayout = new VerticalLayout();
	private HorizontalLayout hrLayout = new HorizontalLayout();
	private HorizontalLayout btnLayout = new HorizontalLayout();

	public FileUploadBirthDate imageFileNew = new FileUploadBirthDate("Image");

	private SessionBean sessionBean;

	public AddBirthDayCertificate(SessionBean sessionBean)
	{		
		this.sessionBean = sessionBean;

		this.setWidth("500px");
		this.setHeight("690px");
		this.setResizable(false);

		cmpInitialize();
		cmpAddition();
	}

	private void cmpInitialize()
	{
		//imageFileNew.setStyleName("image");
	}

	private void cmpAddition()
	{
		btnLayout.setSpacing(true);

		hrLayout.addComponent(imageFileNew);

		mainLayout.addComponent(hrLayout);
		mainLayout.addComponent(btnLayout);
		mainLayout.setComponentAlignment(btnLayout, Alignment.BOTTOM_CENTER);
		
		addComponent(mainLayout);
	}

}
