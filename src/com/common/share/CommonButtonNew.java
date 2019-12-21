package com.common.share;


import com.vaadin.terminal.Resource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Window;

public class CommonButtonNew extends HorizontalLayout{
	
	//private HorizontalLayout  btnLayout=new HorizontalLayout();
	
	public NativeButton btnNew= new NativeButton("New");
	public NativeButton btnUpdate= new NativeButton("Edit");
	public NativeButton btnSave= new NativeButton("Save");
	public NativeButton btnRefresh= new NativeButton("Refresh");
	public NativeButton btnDelete= new NativeButton("Delete");
	public NativeButton btnFind= new NativeButton("Find");
	public NativeButton btnCancel= new NativeButton("Cancel");
	public NativeButton btnExit= new NativeButton("Exit");
	
	public NativeButton btnNext=new NativeButton("N");	
	public NativeButton btnPrev=new NativeButton("P");
	
	//public CommonButton(String New, String Update, String Save, String Refresh, String Delete, String Find, String Cancel, String Exit,String N,String P)
	
	public CommonButtonNew(String New, String Save, String Edit, String Delete, String Refresh, String Find, String Cancel, String Exit,String N,String P)
	{
	   //setWidth("540px");
	   setSpacing(true);	
	   System.out.println("ok"+ P);
		if (P.equals("P"))
		{
			
			buttonsize(btnPrev);			
			btnPrev.setIcon(new ThemeResource("../icons/arrow-left.png"));			
			addComponent(btnPrev);			
		}
		if (New.equals("New"))
		{
			buttonsize(btnNew);			
			btnNew.setIcon(new ThemeResource("../icons/new.png"));			
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
			buttonsize(btnUpdate);	
			btnUpdate.setIcon(new ThemeResource("../icons/update1.png"));
			addComponent(btnUpdate);
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
			btnRefresh.setIcon(new ThemeResource("../icons/refresh1.png"));
			addComponent(btnRefresh);
		}
		
		if (Find.equals("Find"))
		{
			buttonsize(btnFind);
			btnFind.setIcon(new ThemeResource("../icons/find.png"));
			addComponent(btnFind);
		}
		
		if (Cancel.equals("Cancel"))
		{
			buttonsize(btnCancel);
			btnCancel.setIcon(new ThemeResource("../icons/cancel.png"));
			addComponent(btnCancel);
		}
		
		if (Exit.equals("Exit"))
		{
			buttonsize(btnExit);
			btnExit.setIcon(new ThemeResource("../icons/exit1.png"));
			addComponent(btnExit);
		}		
		if (N.equals("N"))
		{
			buttonsize(btnNext);			
			btnNext.setIcon(new ThemeResource("../icons/arrow-right.png"));			
			addComponent(btnNext);			
		}
		
	}
	
	private void buttonsize(Button btn)
	{
		btn.setWidth("80px");
		btn.setHeight("28px");
	}

}
