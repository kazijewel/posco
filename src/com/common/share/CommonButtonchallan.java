package com.common.share;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeButton;

public class CommonButtonchallan extends HorizontalLayout
{	
	//private HorizontalLayout  btnLayout=new HorizontalLayout();
	
	public NativeButton btnNew= new NativeButton("New");
	public NativeButton btnEdit= new NativeButton("Edit");
	public NativeButton btnSave= new NativeButton("Save");
	public NativeButton btnRefresh= new NativeButton("Refresh");
	public NativeButton btnDelete= new NativeButton("Delete");
	public NativeButton btnFind= new NativeButton("Find");
	public NativeButton btnCancel= new NativeButton("Cancel");
	public NativeButton btnDo= new NativeButton("DO");
	public NativeButton btnChallan= new NativeButton("Challan");
	public NativeButton btnbill= new NativeButton("Bill");
	public NativeButton btnExit= new NativeButton("Exit");
	
	
	
	public CommonButtonchallan(String New, String Save, String Edit, String Delete, String Refresh, String Find, String Cancel, String DO, String Challan, String Bill,String Exit)
	{
	   //setWidth("540px");
	   setSpacing(true);	   
		if (New.equals("New"))
		{
			buttonsize(btnNew);			
			btnNew.setIcon(new ThemeResource("../icons/document.png"));			
			addComponent(btnNew);			
		}
		
		if (Save.equals("Save"))
	    {
			buttonsize(btnSave);
	    	btnSave.setIcon(new ThemeResource("../icons/action_save.gif"));
	    	addComponent(btnSave);
	    }
		
		if (Edit.equals("Edit"))
		{
			buttonsize(btnEdit);	
			btnEdit.setIcon(new ThemeResource("../icons/reload.png"));
			addComponent(btnEdit);
		}
	   
		
		if (Delete.equals("Delete"))
		{
			buttonsize(btnDelete);
			btnDelete.setIcon(new ThemeResource("../icons/trash.png"));
			addComponent(btnDelete);
		}
	    
		if (Refresh.equals("Refresh"))
		{
			buttonsize(btnRefresh);
			btnRefresh.setIcon(new ThemeResource("../icons/refresh.png"));
			addComponent(btnRefresh);
		}
		
		
		if (Find.equals("Find"))
		{
			buttonsize(btnFind);
			btnFind.setIcon(new ThemeResource("../icons/Findicon.png"));
			addComponent(btnFind);
		}
		
		if (Cancel.equals("Cancel"))
		{
			buttonsize(btnCancel);
			btnCancel.setIcon(new ThemeResource("../icons/cancel.png"));
			addComponent(btnCancel);
		}
		
		if (DO.equals("DO"))
		{
			buttonsize(btnDo);
			btnDo.setIcon(new ThemeResource("../icons/print.png"));
			addComponent(btnDo);
		}
		
		if (Challan.equals("Challan"))
		{
			btnChallan.setWidth("90px");
			btnChallan.setHeight("28px");
			btnChallan.setIcon(new ThemeResource("../icons/print.png"));
			addComponent(btnChallan);
		}
		
		if (Bill.equals("Bill"))
		{
			btnbill.setWidth("90px");
			btnbill.setHeight("28px");
			btnbill.setIcon(new ThemeResource("../icons/print.png"));
			addComponent(btnbill);
		}
		
		if (Exit.equals("Exit"))
		{
			buttonsize(btnExit);
			btnExit.setIcon(new ThemeResource("../icons/Exit.png"));
			addComponent(btnExit);
		}		
		
	}
	
	private void buttonsize(Button btn)
	{
		btn.setWidth("80px");
		btn.setHeight("28px");
	}

}
