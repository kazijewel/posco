package com.appform.hrmModule;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.VerticalLayout;
import com.common.share.AmountField;
import com.common.share.FileUpload;
import com.common.share.ImmediateFileUpload;
import com.common.share.SessionBean;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.SucceededEvent;

@SuppressWarnings("serial")
public class TabPFInformation extends VerticalLayout
{
	public AbsoluteLayout mainLayout;
	
	
	public Label lblPFID;
	public Label lblNinfo;
	public Label lblPfStartDate;
	public TextField txtYear;
	public PopupDateField dPfStartDate;

	public TextField txtPFID;

	public OptionGroup opgDateType;
	public static final List<String> dateOption = Arrays.asList(new String[] {"Joining Date", "Confirmation Date"});
	
	
	
	public Table table;

	public FileUpload Image;
	
	public ArrayList<Label> tblblSl = new ArrayList<Label>();
	public ArrayList<TextField> tbltxtNomineeName = new ArrayList<TextField>();
	public ArrayList<TextField> tbltxtRelation = new ArrayList<TextField>();
	public ArrayList<AmountField> tblamtAge = new ArrayList<AmountField>();
	public ArrayList<AmountField> tblamtPercent = new ArrayList<AmountField>();
	private ArrayList<ImmediateFileUpload> tblImage = new ArrayList<ImmediateFileUpload>();
	public ArrayList<Label> tblblImagePath = new ArrayList<Label>();

	SessionBean sessionBean;
	//public FileUpload Image;
	public String nomineePdf = null;
	public String nomineeFilePathTmp = "";
	String nomineeImage = "0";
	public TabPFInformation(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		buildMainLayout();
		addComponent(mainLayout);
		tableinitialise();
		
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

		tblamtPercent.add(ar,new AmountField());
		tblamtPercent.get(ar).setWidth("100%");
		tblamtPercent.get(ar).setImmediate(true);

		tblImage.add(ar,new ImmediateFileUpload("","Upload"));
		tblImage.get(ar).setWidth("100%");
		tblImage.get(ar).setImmediate(true);
		tblImage.get(ar).upload.addListener(new Upload.SucceededListener() 
		{
			public void uploadSucceeded(SucceededEvent event) 
			{
				//btnBirthPreview.setCaption("View");
				nomineePath(0,"",ar);
			}
		});
		tblblImagePath.add(ar,new Label());
		tblblImagePath.get(ar).setWidth("100%");
		tblblImagePath.get(ar).setHeight("15px");

		table.addItem(new Object[]{tblblSl.get(ar),tbltxtNomineeName.get(ar),tbltxtRelation.get(ar),
				tblamtAge.get(ar),tblamtPercent.get(ar),tblImage.get(ar),tblblImagePath.get(ar)},ar);
	}
	public String nomineePath(int flag,String str,int x)
	{
		String basePath = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/";
		String stuImage = null;

		if(flag==0)
		{
			if(tblImage.get(x).fileName.trim().length()>0)
			{
				try 
				{
					if(tblImage.get(x).fileName.toString().endsWith(".jpg"))
					{
						String path = sessionBean.getUserId()+"nominee";
						fileMove(basePath+tblImage.get(x).fileName.trim(),SessionBean.imagePath+path+".jpg");
						nomineePdf = SessionBean.imagePath+path+".jpg";
						nomineeFilePathTmp = path+".jpg";
						tblblImagePath.get(x).setValue(nomineeFilePathTmp);
					}
					else
					{
						String path = sessionBean.getUserId()+"nominee";
						fileMove(basePath+tblImage.get(x).fileName.trim(),SessionBean.imagePath+path+".pdf");
						nomineePdf = SessionBean.imagePath+path+".pdf";
						nomineeFilePathTmp = path+".pdf";
						tblblImagePath.get(x).setValue(nomineeFilePathTmp);
					}
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
			return nomineePdf;
		}

		if(flag==1)
		{
			if(tblImage.get(x).fileName.trim().length()>0)
			{
				try 
				{
					if(tblImage.get(x).fileName.toString().endsWith(".jpg"))
					{
						String path = str;
						String projectName = sessionBean.getContextName();
						fileMove(basePath+tblImage.get(x).fileName.trim(),SessionBean.imagePath+projectName+"/employee/nominee/"+path+".jpg");
						stuImage = SessionBean.imagePath+projectName+"/employee/nominee/"+path+".jpg";
						tblblImagePath.get(x).setValue(stuImage);
					}
					else
					{
						String path = str;
						String projectName = sessionBean.getContextName();
						fileMove(basePath+tblImage.get(x).fileName.trim(),SessionBean.imagePath+projectName+"/employee/nominee/"+path+".pdf");
						stuImage = SessionBean.imagePath+projectName+"/employee/nominee/"+path+".pdf";
						tblblImagePath.get(x).setValue(stuImage);
					}
				}
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
			return stuImage;
		}

		return null;
	}
	private void fileMove(String fStr,String tStr) throws IOException
	{
		try
		{
			File f1 = new File(tStr);
			if(f1.isFile())
				f1.delete();
		}
		catch(Exception exp){}
		FileInputStream ff= new FileInputStream(fStr);

		File  ft = new File(tStr);
		FileOutputStream fos = new FileOutputStream(ft);

		while(ff.available()!=0)
		{
			fos.write(ff.read());
		}
		fos.close();
		ff.close();
	}
	public AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		setWidth("100%");
		setHeight("100%");
		
		opgDateType=new OptionGroup("",dateOption);
		opgDateType.setHeight("-1px");
		opgDateType.setImmediate(true);
		opgDateType.setStyleName("horizontal");
		opgDateType.setValue("Joining Date");
		mainLayout.addComponent(new Label("<b>PF start based on </b>",Label.CONTENT_XHTML),"top:28px; left:220px");
		mainLayout.addComponent(opgDateType,"top:50px; left:220px");

		lblPFID = new Label();
		lblPFID.setImmediate(true);
		lblPFID.setWidth("200px");
		lblPFID.setHeight("23px");
		mainLayout.addComponent(new Label("PF ID :"), "top:80px;left:220.0px;");
		
		txtPFID = new TextField();
		txtPFID.setImmediate(true);
		txtPFID.setWidth("120px");
		lblPFID.setHeight("23px");
		txtPFID.setVisible(true);
		mainLayout.addComponent(txtPFID, "top:78px;left:340px;");
		
		txtYear=new AmountField();
		txtYear.setWidth("40px");
		txtYear.setHeight("-1px");
		txtYear.setImmediate(true);
		mainLayout.addComponent(new Label("PF Entitlement after :"),"top:105px; left:220px");
		mainLayout.addComponent(txtYear,"top:103px; left:340px");
		mainLayout.addComponent(new Label("Years"),"top:105px; left:383px");

		lblPfStartDate=new Label("PF start Date :");
		lblPfStartDate.setVisible(true);
		mainLayout.addComponent(lblPfStartDate, "top:130px;left:220.0px;");
	
		dPfStartDate=new PopupDateField();
		dPfStartDate.setHeight("22px");
		dPfStartDate.setWidth("110px");
		dPfStartDate.setImmediate(true);
		dPfStartDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dPfStartDate.setDateFormat("dd-MM-yyyy");
		mainLayout.addComponent(dPfStartDate, "top:128px;left:340px;");

		//lblNominee
		lblNinfo =new Label();
		lblNinfo.setImmediate(true);
		lblNinfo.setWidth("500px");
		lblNinfo.setHeight("100");
		mainLayout.addComponent(new Label(" <b><u>PF Nominee :</u></b>",Label.CONTENT_XHTML), "top:180px;left:80px;");
		

		table = new Table();
		table.setWidth("740px");
		table.setHeight("230px");
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

		table.addContainerProperty("Percent(%)", AmountField.class, new AmountField());
		table.setColumnWidth("Percent(%)",60);

		table.addContainerProperty("Image", ImmediateFileUpload.class, new ImmediateFileUpload("","Upload"));
		table.setColumnWidth("Image",70);
		
		table.addContainerProperty("Image path", Label.class, new Label());
		table.setColumnWidth("Image path", 50);

		table.setColumnAlignments(new String[] {Table.ALIGN_CENTER, Table.ALIGN_LEFT, Table.ALIGN_LEFT ,
				Table.ALIGN_LEFT,Table.ALIGN_RIGHT,Table.ALIGN_CENTER,Table.ALIGN_CENTER});

		table.setColumnCollapsed("Image path", true);
		mainLayout.addComponent(table, "top:235.0px;left:30.0px;");
		return mainLayout;
	}

}
