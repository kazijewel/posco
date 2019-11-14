package com.common.share;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

public class YesNoDialog extends Window implements Button.ClickListener {

    Callback callback;
    NativeButton yes = new NativeButton("Yes", this);
    NativeButton no = new NativeButton("No", this);

    public YesNoDialog(String caption, String question, Callback callback) {
        super(caption);
        this.setWidth("300px");
        this.setResizable(false);
        setStyleName("cwindow");
        setModal(true);
        this.callback = callback;

        if (question != null) {
            addComponent(new Label(question));
        }

        HorizontalLayout hl = new HorizontalLayout();
        //hl.setWidth("400px");
        hl.setSpacing(true);
        hl.addComponent(yes);
        //hl.setComponentAlignment(yes, "middle");
        
        hl.addComponent(no);
        addComponent(hl);
       // ((OrderedLayout) getLayout()).setComponentAlignment(hl,
              //  OrderedLayout.ALIGNMENT_RIGHT, OrderedLayout.ALIGNMENT_BOTTOM);
        yes.focus();
    }

    public void buttonClick(ClickEvent event) {
        if (getParent() != null) {
            ((Window) getParent()).removeWindow(this);
        }
        callback.onDialogResult(event.getSource() == yes);
    }

    public interface Callback {

        public void onDialogResult(boolean resultIsYes);
    }

}