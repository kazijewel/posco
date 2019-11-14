package com.appform.hrmModule;

import java.util.ArrayList;

import com.common.share.*;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class TabExperience extends Form
{
	SessionBean sessionBean;
	VerticalLayout experienceMainlayout = new VerticalLayout();
	VerticalLayout mainLayout = new VerticalLayout();
	VerticalLayout tableLayout=new VerticalLayout();
	VerticalLayout blankLayout=new VerticalLayout();
	HorizontalLayout lowerLayout=new HorizontalLayout();
	ArrayList<Component> allComp = new ArrayList<Component>();

	public Label lblExperience = new Label("Experience:");

	public Table table = new Table();
	public ArrayList<TextField> tblTxtPost = new ArrayList<TextField>();
	public ArrayList<TextField> tblTxtCompanyName = new ArrayList<TextField>();
	public ArrayList<PopupDateField> tblDateFrom = new ArrayList<PopupDateField>();
	public ArrayList<PopupDateField> tblDateTo = new ArrayList<PopupDateField>();
	public ArrayList<TextField> tblTxtMajorTask = new ArrayList<TextField>();

	String smallTextField="80px";
	String largeTextField="200px";
	String comboWidth="160px";

	public TabExperience()
	{
		init();
		addCmp();
		tableInitialise();
		//focusMove();
	}

	public void tableInitialise()
	{
		for(int i=0;i<5;i++)
		{
			tableRowAdd(i);
		}
	}

	public void tableRowAdd(final int ar)
	{
		try
		{
			tblTxtPost.add(ar,new TextField());
			tblTxtPost.get(ar).setWidth("100%");

			tblTxtCompanyName.add(ar,new TextField());
			tblTxtCompanyName.get(ar).setWidth("100%");

			tblDateFrom.add(ar,new PopupDateField());
			tblDateFrom.get(ar).setWidth("100%");
			tblDateFrom.get(ar).setValue(new java.util.Date());
			tblDateFrom.get(ar).setResolution(PopupDateField.RESOLUTION_DAY);
			tblDateFrom.get(ar).setDateFormat("dd-MM-yy");
			
			tblDateTo.add(ar,new PopupDateField());
			tblDateTo.get(ar).setWidth("100%");
			tblDateTo.get(ar).setValue(new java.util.Date());
			tblDateTo.get(ar).setResolution(PopupDateField.RESOLUTION_DAY);
			tblDateTo.get(ar).setDateFormat("dd-MM-yy");

			tblTxtMajorTask.add(ar,new TextField());
			tblTxtMajorTask.get(ar).setWidth("100%");

			table.addItem(new Object[]{tblTxtPost.get(ar),tblTxtCompanyName.get(ar),tblDateFrom.get(ar),tblDateTo.get(ar),tblTxtMajorTask.get(ar)},ar);
		}
		catch(Exception exp)
		{
			System.out.println(exp);
		}
	}

	public void init()
	{
		table.setWidth("840px");
		table.setHeight("170px");

		table.addContainerProperty("Post/Desig.", TextField.class , new TextField());
		table.setColumnWidth("Post/Desig.",100);

		table.addContainerProperty("Company Name", TextField.class , new TextField());
		table.setColumnWidth("Company Name",200);

		table.addContainerProperty("From", PopupDateField.class , new PopupDateField());
		table.setColumnWidth("From",100);

		table.addContainerProperty("To", PopupDateField.class , new PopupDateField());
		table.setColumnWidth("To",100);

		table.addContainerProperty("Major Responsibility", TextField.class , new TextField());
		table.setColumnWidth("Major Responsibility",260);

		table.setColumnCollapsingAllowed(true);

		tableLayout.addComponent(lblExperience);
		tableLayout.addComponent(table);

		mainLayout.setSpacing(true);
	}

	public void addCmp()
	{
		blankLayout.setHeight("30px");		

		mainLayout.addComponent(tableLayout);
		mainLayout.addComponent(blankLayout);
		mainLayout.addComponent(lowerLayout);
		mainLayout.setComponentAlignment(lowerLayout, Alignment.MIDDLE_CENTER);
		mainLayout.setSpacing(true);

		experienceMainlayout.addComponent(mainLayout);
		experienceMainlayout.setComponentAlignment(mainLayout, Alignment.MIDDLE_CENTER);
		getFooter().addComponent(experienceMainlayout);

		mainLayout.setWidth("846px");
		mainLayout.setHeight("290px");

		setWidth("820px");
		setHeight("320px");
	}
}
