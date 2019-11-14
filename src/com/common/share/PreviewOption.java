package com.common.share;

import java.util.Arrays;
import java.util.List;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.OptionGroup;

public class PreviewOption extends HorizontalLayout{
	
	private static final List<String> type = Arrays.asList(new String[] {"PDF","Others",});
	
	public OptionGroup txtType = new OptionGroup("",type);
	
	public boolean actionCheck = true;
	
	public PreviewOption()
	{
		init();
		
		btnAction();
		
		addComponent(txtType);
	}
	
	private void btnAction()
	{
		txtType.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(txtType.getValue().toString().equalsIgnoreCase("PDF"))
				{
					actionCheck = true;
				}
				else if(txtType.getValue().toString().equalsIgnoreCase("Others"))
				{
					actionCheck = false;
				}
			}
		});
	}
	
	private void init()
	{
		txtType.setStyleName("horizontal");
		txtType.setImmediate(true);
		txtType.setValue("PDF");
	}
}
