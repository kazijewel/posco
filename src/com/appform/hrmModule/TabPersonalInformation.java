package com.appform.hrmModule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.VerticalLayout;
import com.common.share.AmountField;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

@SuppressWarnings("serial")
public class TabPersonalInformation extends VerticalLayout
{
	public AbsoluteLayout mainLayout;
	public Table table;

	public Label lblMarriageDate;
	public Label lblSpouseName;
	public Label lblSpouseOccupation;
	public Label lblNumberOfChild;
	public Label lblHaveChild;

	public AmountField amntNumofChild;

	public TextField txtSpouseOccupation;
	public TextField txtMotherName;
	public TextField txtFatherName;
	public TextField txtSpouseName;

	public PopupDateField dMarriageDate;

	public OptionGroup ogMaritalStatus;
	public OptionGroup ogStatus;

	public ComboBox cmbBloodGroup;

	public TextArea txtMailing;
	public TextArea txtPerAddress;

	public static final List<String> blood = Arrays.asList(new String[] {"A+", "B+", "AB+", "O+", "A-", "B-", "O-", "AB-"});
	public static final List<String> marital = Arrays.asList(new String[] {"Unmarried","Married"});
	public static final List<String> YesNo = Arrays.asList(new String[] {"Yes", "No"});

	public ArrayList<Label> tblblSl = new ArrayList<Label>();
	public ArrayList<TextField> tbltxtNomineeName = new ArrayList<TextField>();
	public ArrayList<TextField> tbltxtRelation = new ArrayList<TextField>();
	public ArrayList<AmountField> tblamtAge = new ArrayList<AmountField>();
	public ArrayList<AmountField> tblamtPercent = new ArrayList<AmountField>();
	/*private ArrayList<Upload> tblImage = new ArrayList<Upload>();*/

	public TabPersonalInformation()
	{
		buildMainLayout();
		addComponent(mainLayout);
		tableinitialise();

		setEventAction();
	}

	private void setEventAction()
	{
		ogMaritalStatus.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(ogMaritalStatus.getValue()!=null)	
				{	
					if(!ogMaritalStatus.getValue().toString().equalsIgnoreCase("Unmarried"))	
					{
						lblSpouseName.setVisible(true);
						lblSpouseOccupation.setVisible(true);
						lblMarriageDate.setVisible(true);
						lblHaveChild.setVisible(true);
						txtSpouseName.setVisible(true);
						txtSpouseOccupation.setVisible(true);
						dMarriageDate.setVisible(true);
						ogStatus.setVisible(true);
					}
					else
					{
						lblSpouseName.setVisible(false);
						lblSpouseOccupation.setVisible(false);
						lblMarriageDate.setVisible(false);
						lblHaveChild.setVisible(false);
						txtSpouseName.setVisible(false);
						txtSpouseOccupation.setVisible(false);
						dMarriageDate.setVisible(false);
						ogStatus.setVisible(false);
						ogStatus.setValue(null);
						lblNumberOfChild.setVisible(false);
						amntNumofChild.setVisible(false);
					}
				}
			}
		});

		ogStatus.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(ogStatus.getValue()!=null)
				{
					if(ogStatus.getValue().toString().equals("Yes"))	
					{	
						lblNumberOfChild.setVisible(true);
						amntNumofChild.setVisible(true);
					}
					else
					{
						lblNumberOfChild.setVisible(false);
						amntNumofChild.setVisible(false);
					}
				}
			}
		});
	}

	private void tableinitialise()
	{
		for(int i=0; i<5; i++)
		{
			tableRowAdd(i);
		}
	}

	private void tableRowAdd( final int ar)
	{
		tblblSl.add(ar,new Label());
		tblblSl.get(ar).setWidth("100%");
		tblblSl.get(ar).setHeight("15px");
		tblblSl.get(ar).setValue(ar+1);

		tbltxtNomineeName.add(ar,new TextField());
		tbltxtNomineeName.get(ar).setWidth("100%");
		tbltxtNomineeName.get(ar).setImmediate(true);

		tbltxtRelation.add(ar,new TextField());
		tbltxtRelation.get(ar).setWidth("100%");
		tbltxtRelation.get(ar).setImmediate(true);

		tblamtAge.add(ar,new AmountField());
		tblamtAge.get(ar).setWidth("100%");
		tblamtAge.get(ar).setImmediate(true);

		/*tblImage.add(ar, new Upload());
		tblImage.get(ar).setWidth("100%");
		tblImage.get(ar).setImmediate(true);*/

		tblamtPercent.add(ar,new AmountField());
		tblamtPercent.get(ar).setWidth("100%");
		tblamtPercent.get(ar).setImmediate(true);

		table.addItem(new Object[]{tblblSl.get(ar),tbltxtNomineeName.get(ar),tbltxtRelation.get(ar),
				tblamtAge.get(ar),/*tblImage.get(ar),*/tblamtPercent.get(ar)},ar);
	}

	public AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		setWidth("100%");
		setHeight("100%");

		txtFatherName = new TextField();
		txtFatherName.setImmediate(true);
		txtFatherName.setWidth("223px");
		txtFatherName.setHeight("-1px");

		txtMotherName = new TextField();
		txtMotherName.setImmediate(true);
		txtMotherName.setWidth("223px");
		txtMotherName.setHeight("-1px");

		txtPerAddress = new TextArea();
		txtPerAddress.setImmediate(true);
		txtPerAddress.setWidth("223px");
		txtPerAddress.setHeight("54px");

		txtMailing = new TextArea();
		txtMailing.setImmediate(true);
		txtMailing.setWidth("223px");
		txtMailing.setHeight("54px");

		cmbBloodGroup = new ComboBox("",blood);
		cmbBloodGroup.setImmediate(true);
		cmbBloodGroup.setWidth("123px");
		cmbBloodGroup.setHeight("-1px");

		ogMaritalStatus = new OptionGroup("",marital);
		ogMaritalStatus.setImmediate(true);
		ogMaritalStatus.setWidth("-1px");
		ogMaritalStatus.setHeight("-1px");
		ogMaritalStatus.setValue("Unmarried");
		ogMaritalStatus.setStyleName("horizontal");

		lblMarriageDate=new Label("Marriage Date :");
		lblMarriageDate.setVisible(false);

		dMarriageDate = new PopupDateField();
		dMarriageDate.setImmediate(true);
		dMarriageDate.setWidth("110px");
		dMarriageDate.setHeight("-1px");
		dMarriageDate.setInvalidAllowed(false);
		dMarriageDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dMarriageDate.setDateFormat("dd-MM-yyyy");
		dMarriageDate.setValue(null);
		dMarriageDate.setVisible(false);

		lblSpouseName=new Label("Spouse Name :");
		lblSpouseName.setVisible(false);

		txtSpouseName = new TextField();
		txtSpouseName.setImmediate(true);
		txtSpouseName.setWidth("217px");
		txtSpouseName.setHeight("-1px");
		txtSpouseName.setVisible(false);

		lblSpouseOccupation=new Label("Spouse Occupation :");
		lblSpouseOccupation.setVisible(false);

		txtSpouseOccupation = new TextField();
		txtSpouseOccupation.setImmediate(true);
		txtSpouseOccupation.setWidth("217px");
		txtSpouseOccupation.setHeight("-1px");
		txtSpouseOccupation.setVisible(false);

		lblHaveChild=new Label("Have Child ? ");
		lblHaveChild.setVisible(false);

		ogStatus = new OptionGroup("",YesNo);
		ogStatus.setImmediate(true);
		ogStatus.setHeight("-1px");
		ogStatus.setWidth("-1px");
		ogStatus.setStyleName("horizontal");
		ogStatus.setVisible(false);

		lblNumberOfChild=new Label("Number Of Child :");
		lblNumberOfChild.setVisible(false);

		amntNumofChild = new AmountField();
		amntNumofChild.setImmediate(true);
		amntNumofChild.setWidth("40px");
		amntNumofChild.setHeight("-1px");
		amntNumofChild.setVisible(false);

		mainLayout.addComponent(new Label("Father's Name :"), "top:25.0px;left:30.0px;");
		mainLayout.addComponent(txtFatherName, "top:23.0px;left:190.0px;");

		mainLayout.addComponent(new Label("Mother's Name :"), "top:50.0px;left:30.0px;");
		mainLayout.addComponent(txtMotherName, "top:48.0px;left:190.0px;");

		mainLayout.addComponent(new Label("Present Address :"), "top:75.0px;left:30.0px;");
		mainLayout.addComponent(txtPerAddress, "top:73.0px;left:190.0px;");

		mainLayout.addComponent(new Label("Permanent Address :"), "top:130.0px;left:30.0px;");
		mainLayout.addComponent(txtMailing, "top:128.0px;left:190.0px;");

		mainLayout.addComponent(new Label("Blood Group :"), "top:185.0px;left:30.0px;");
		mainLayout.addComponent(cmbBloodGroup, "top:183.0px;left:190.0px;");

		mainLayout.addComponent(new Label("Marital Status :"), "top:25.0px;left:440.0px;");
		mainLayout.addComponent(ogMaritalStatus,"top:23.0px;left:560.0px;");

		mainLayout.addComponent(lblMarriageDate, "top:50.0px;left:440.0px;");
		mainLayout.addComponent(dMarriageDate, "top:48.0px;left:565.0px;");

		mainLayout.addComponent(lblSpouseName, "top:75.0px;left:440.0px;");
		mainLayout.addComponent(txtSpouseName, "top:73.0px;left:565.0px;");

		mainLayout.addComponent(lblSpouseOccupation, "top:100.0px;left:440.0px;");
		mainLayout.addComponent(txtSpouseOccupation, "top:98.0px;left:565.0px;");

		mainLayout.addComponent(lblHaveChild, "top:125.0px;left:440.0px;");
		mainLayout.addComponent(ogStatus, "top:123.0px;left:565.0px;");

		mainLayout.addComponent(lblNumberOfChild, "top:150.0px;left:440.0px;");
		mainLayout.addComponent(amntNumofChild, "top:148.0px;left:565.0px;");

		Maintable();

		mainLayout.addComponent(table, "top:235.0px;left:30.0px;");
		table.setVisible(false);

		return mainLayout;
	}

	private void Maintable()
	{
		table = new Table();
		table.setWidth("610px");
		table.setHeight("200px");
		table.setImmediate(true);
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("SL#", Label.class, new Label());
		table.setColumnWidth("SL#", 20);

		table.addContainerProperty("Nominee Name", TextField.class, new TextField());
		table.setColumnWidth("Nominee Name", 250);

		table.addContainerProperty("Relation", TextField.class, new TextField());
		table.setColumnWidth("Relation",150);

		table.addContainerProperty("Age", AmountField.class, new AmountField());
		table.setColumnWidth("Age",40);

		/*table.addContainerProperty("Image", Upload.class, new Upload());
		table.setColumnWidth("Image",100);*/

		table.addContainerProperty("Percent(%)", AmountField.class, new AmountField());
		table.setColumnWidth("Percent(%)",60);

		table.setColumnAlignments(new String[] {Table.ALIGN_CENTER, Table.ALIGN_LEFT, Table.ALIGN_LEFT ,
				Table.ALIGN_LEFT /*,Table.ALIGN_RIGHT*/,Table.ALIGN_CENTER});
	}
}
