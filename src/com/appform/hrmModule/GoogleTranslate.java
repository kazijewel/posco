package com.appform.hrmModule;

import com.common.share.CommonButton;
import com.common.share.CommonMethod;
import com.common.share.SessionBean;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class GoogleTranslate extends Window 
{
	private AbsoluteLayout mainLayout;
	
	private TextField txtInput,txtOutput;
	private SessionBean sessionBean;	
	
	private CommonMethod cm;
	private String menuId = "";
	CommonButton cButton = new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "", "", "Exit");
	
	public GoogleTranslate(SessionBean sessionBean,String menuId)
	{
		this.sessionBean = sessionBean;
		this.setCaption("UNIT INFORMATION :: "+sessionBean.getCompany());
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		
		buildMainLayout();
		setContent(mainLayout);
		buttonAction();
		compInit(true);
		btnint(true);
		
		authenticationCheck();
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
	private void compInit(boolean t)
	{
		txtInput.setEnabled(!t);
		txtOutput.setEnabled(!t);		
	}
	private void btnint(boolean t)
	{
		cButton.btnNew.setEnabled(t);
		cButton.btnEdit.setEnabled(t);
		cButton.btnSave.setEnabled(!t);	
		cButton.btnRefresh.setEnabled(!t);
		cButton.btnDelete.setEnabled(t);
		cButton.btnFind.setEnabled(t);
	}
	private void txtClear()
	{
		txtInput.setValue("");
		txtOutput.setValue("");
	}
	private static String translate(String langFrom, String langTo, String text) throws IOException {
        // INSERT YOU URL HERE
        String urlStr = "https://your.google.script.url" +
                "?q=" + URLEncoder.encode(text, "UTF-8") +
                "&target=" + langTo +
                "&source=" + langFrom;
        URL url = new URL(urlStr);
        StringBuilder response = new StringBuilder();
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }
	private void buttonAction()
	{
		cButton.btnNew.addListener(new ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				txtClear();
				compInit(false);
				btnint(false);
			}
		});
		cButton.btnEdit.addListener(new ClickListener() {			
			public void buttonClick(ClickEvent event) {
				compInit(false);
				btnint(false);
			}
		});
		cButton.btnSave.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				if(!txtInput.getValue().toString().isEmpty())
				{
					try 
					{
						String text = "Hello world!";
				        //Translated text: Hallo Welt!
				        System.out.println("Translated text: " + translate("en", "de", text));
						txtOutput.setValue(translate("en", "de", txtInput.getValue().toString()));
					} 
					catch (IOException e) 
					{
						e.printStackTrace();
					}
				}
			}
		});
		cButton.btnRefresh.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{            	
				compInit(true);
				btnint(true);
				txtClear();
			}
		});
		cButton.btnExit.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});
	}

	private AbsoluteLayout buildMainLayout() 
	{
		this.setWidth("630px");
		
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("99%");
		mainLayout.setHeight("290px");
		mainLayout.setMargin(false);
		
		txtInput=new TextField();
		txtInput.setWidth("500px");
		txtInput.setRows(3);
		mainLayout.addComponent(new Label("Input: "),"top:20.0px;left:30.0px");
		mainLayout.addComponent(txtInput,"top:18.0px;left:110.0px");		

		txtOutput=new TextField();
		txtOutput.setWidth("500px");
		txtOutput.setRows(3);
		mainLayout.addComponent(new Label("Output: "),"top:110.0px;left:30.0px");
		mainLayout.addComponent(txtOutput,"top:108.0px;left:110.0px");
		
		mainLayout.addComponent(cButton, "bottom:15.0px; left:70.0px;");

		return mainLayout;
	}
}
