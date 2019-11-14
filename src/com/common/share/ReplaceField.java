package com.common.share;

import java.util.LinkedHashMap;
import java.util.Map;
import org.vaadin.autoreplacefield.client.ui.VAutoReplaceField;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.ui.TextField;

public class ReplaceField extends TextField 
{
	private Map<String, String> searchPairs = new LinkedHashMap<String, String>();

	@Override
	public void paintContent(PaintTarget target) throws PaintException 
	{
		super.paintContent(target);

		String[] s = searchPairs.keySet().toArray(new String[searchPairs.size()]);
		String[] r = new String[s.length];
		
		for (int i = 0; i < r.length; i++) 
		{
			if(i == 3)
			{
				r[i] = ",";
				r[i] = searchPairs.get(s[i]);
			}
			else
				r[i] = searchPairs.get(s[i]);
		}
		target.addAttribute(VAutoReplaceField.VAR_SEARCH, s);
		target.addAttribute(VAutoReplaceField.VAR_REPLACE, r);

	}
	
	public void addReplaceRule(String search, String replace) 
	{
		searchPairs.put(search, replace);
	}
	
	protected String applyRules(String string) 
	{
		if (string != null && searchPairs != null) 
		{
			for (String key : searchPairs.keySet()) 
			{
				string = string.replaceAll(key, searchPairs.get(key));
			}
			return string;
		}
		return string;
	}

}
