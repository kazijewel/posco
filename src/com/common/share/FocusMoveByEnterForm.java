package com.common.share;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.event.ShortcutListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Component.Focusable;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Form;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

public class FocusMoveByEnterForm {
	@SuppressWarnings("serial")
	public FocusMoveByEnterForm(Form win,final ComponentContainer cparent[]){
		ArrayList<Component> c = new ArrayList<Component>();
		
		
		for(int i=0;i<cparent.length;i++){
            for (Iterator<Component> it = ((ComponentContainer) cparent[i])
                    .getComponentIterator(); it.hasNext();) {
               
                    Object next = it.next();
                    if (next instanceof Focusable) {
                    	c.add((Component)next);
                    }
            }
        }
		Component op[] = new Component[c.size()];
		c.toArray(op);
		new FocusMoveByEnterForm(win,op);
		/*win.addAction(new ShortcutListener("Next field", KeyCode.ENTER, null) {
            @Override
            public void handleAction(Object sender, Object target) {
            	// The panel is the sender, loop trough content
            	boolean bt = true;
            	for(int i=0;i<cparent.length&&bt;i++)
                for (Iterator<Component> it = ((ComponentContainer) cparent[i])
                        .getComponentIterator(); it.hasNext();) {
                    // target is the field we're currently in, focus the next
                    if (it.next() == target && it.hasNext()) {
                        Object next = it.next();
                        if (next instanceof Focusable) {
                            ((Focusable) next).focus();
                            bt = false;
                            break;
                        }
                    }else if(!it.hasNext()&&i<(cparent.length-1)){
                    	System.out.println("hello btn "+i);
                    	for (Iterator<Component> ito = ((ComponentContainer) cparent[(i+1)])
                                .getComponentIterator(); ito.hasNext();) {
                    		System.out.println("2nd btn "+i);
                    		Object next = ito.next();
                            if (next instanceof Focusable) {
                            	System.out.println("3rd btn "+i);
                                ((Focusable) next).focus();
                                bt = false;
                                break;
                            }
                         }
                    }
                }
            }
		});
		*/
	}
	public FocusMoveByEnterForm(Form win,ArrayList<Component> ob){
		Component[] c = new Component[ob.size()];
		ob.toArray(c);
		new FocusMoveByEnterForm(win,c);
	}
	public FocusMoveByEnterForm(Form win,final Component oparent[]){
		win.addAction(new ShortcutListener("Next field", KeyCode.ENTER, null) {
            @Override
            public void handleAction(Object sender, Object target) {
            	for (int i=0;i<oparent.length;i++) {
                    // target is the field we're currently in, focus the next
            		if(target instanceof Button){
            			Button b = (Button)target;
            			b.changeVariables(b,Collections.singletonMap("state", (Object)true));
            			break;
            		}
            		if (oparent[i] == target && i<(oparent.length-1)) {
                    	Component next = oparent[i+1];
                        if (next instanceof Focusable && next.isEnabled()) {
                            ((Focusable) next).focus();
                        }else{
                        	//i++;
                        	for(;i<(oparent.length-1);i++){
                        		next = oparent[i+1];
                        		if (next instanceof Focusable && oparent[i+1].isEnabled()) {
                                    ((Focusable) next).focus();
                                    break;
                                }
                        	}
                        }
                    }
                }
            }
		});
		int m[] = {ModifierKey.CTRL};
		win.addAction(new ShortcutListener("Next field",KeyCode.ARROW_UP,m) {
            @Override
            public void handleAction(Object sender, Object target) {
            	boolean tf = true;
            	for (int i=0;i<oparent.length&&tf;i++) {
                    if (oparent[i] == target && i>0) {
                        Component next = oparent[i-1];
                        if (next instanceof Focusable && next.isEnabled()) {
                            ((Focusable) next).focus();
                        }else{
                        	i--;
                        	if(i==0)
                        		break;
                        	for(;i>0;i--){
                        		next = oparent[i];
                        		if (next instanceof Focusable && next.isEnabled()) {
                                    ((Focusable) next).focus();
                                    tf = false;
                                    break;
                                }
                        	}
                        }
                    }
                }
            }
		});
		win.addAction(new ShortcutListener("Next field", KeyCode.ARROW_DOWN, m) {
            @Override
            public void handleAction(Object sender, Object target) {
            	for (int i=0;i<oparent.length;i++) {
            		if (oparent[i] == target && i<(oparent.length-1)) {
                    	Component next = oparent[i+1];
                        if (next instanceof Focusable && next.isEnabled()) {
                            ((Focusable) next).focus();
                        }else{
                        	//i++;
                        	for(;i<(oparent.length-1);i++){
                        		next = oparent[i+1];
                        		if (next instanceof Focusable && oparent[i+1].isEnabled()) {
                                    ((Focusable) next).focus();
                                    break;
                                }
                        	}
                        }
                    }
                }
            }
		});
	}
}
