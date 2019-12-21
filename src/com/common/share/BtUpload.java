package com.common.share;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Random;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.SucceededEvent;

@SuppressWarnings("serial")
public class BtUpload extends HorizontalLayout {

    public Label status = new Label("<font color='#A32904'><b><Strong>Please select a Text File to Upload<Strong></b></font>",Label.CONTENT_XHTML);

    private ProgressIndicator pi = new ProgressIndicator();
    
    private MyReceiver receiver = new MyReceiver();

    private HorizontalLayout progressLayout = new HorizontalLayout();

    private Upload upload = new Upload(null, receiver);
   // 
    
    public String fileName = "";
    private String coreName = "";
    private String ext = ".txt";
    
    public BtUpload(String fname,String ext,String st) {
    	this.ext = ext;
    	init(fname);
    	status.setValue(st);
    }

    public BtUpload(String fname) {
    	init(fname);
    }
    public void init(String fname){
    	coreName = fname;
    	fileName = fname;
    	setSpacing(true);
    	
        receiver.setSlow(true);
        addComponent(status);
        addComponent(upload);
        addComponent(progressLayout);

        // Make uploading start immediately when file is selected
        upload.setImmediate(true);
        upload.setButtonCaption("Select file");
        upload.setStyleName("UploadButton");

        progressLayout.setSpacing(true);
        progressLayout.setVisible(false);
        progressLayout.addComponent(pi);
        progressLayout.setComponentAlignment(pi, "middle");

        final Button cancelProcessing = new Button("Cancel");
        cancelProcessing.addListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                upload.interruptUpload();
            }
        });
        cancelProcessing.setStyleName("small");
        progressLayout.addComponent(cancelProcessing);

        /**
         * =========== Add needed listener for the upload component: start,
         * progress, finish, success, fail ===========
         */

        upload.addListener(new Upload.StartedListener() {
            public void uploadStarted(StartedEvent event) {
                // This method gets called immediatedly after upload is started
                upload.setVisible(false);
                progressLayout.setVisible(true);
                pi.setValue(0f);
                pi.setPollingInterval(500);
                status.setValue("Uploading file \"" + event.getFilename()
                        + "\"");
            }
        });

        upload.addListener(new Upload.ProgressListener() {
            public void updateProgress(long readBytes, long contentLength) {
                // This method gets called several times during the update
                pi.setValue(new Float(readBytes / (float) contentLength));
            }

        });

        upload.addListener(new Upload.SucceededListener() {
            public void uploadSucceeded(SucceededEvent event) {
                // This method gets called when the upload finished successfully
                status.setValue("Uploading file \"" + event.getFilename()
                        + "\" succeeded");
            }
        });

        upload.addListener(new Upload.FailedListener() {
            public void uploadFailed(FailedEvent event) {
                // This method gets called when the upload failed
                status.setValue("Uploading interrupted");
            }
        });

        upload.addListener(new Upload.FinishedListener() {
            public void uploadFinished(FinishedEvent event) {
                // This method gets called always when the upload finished,
                // either succeeding or failing
                progressLayout.setVisible(false);
                upload.setVisible(true);
               // upload.setCaption("Select another file");
            }
        });
    }
    public class MyReceiver implements Receiver {

      //  private String fileName;
        private String mtype;
        private boolean sleep;
        private int total = 0;
        
        Date d = new Date();
		

        public OutputStream receiveUpload(String f, String mimetype) {
        	String fname = d.getTime()+"";
        	try{
        		File f1 = new File(getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+
						"/VAADIN/themes/"+fileName);
        		if(f1.isFile())
        		 f1.delete();
        	}catch(Exception exp){
        		
        	}
        	fileName = coreName+fname+ext;
        	FileOutputStream fos = null; // Output stream to write to
        	File  file = new File(getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+
        						"/VAADIN/themes/"+ fileName);
            try {
                // Open the file for writing.
                fos = new FileOutputStream(file);
            } catch (final java.io.FileNotFoundException e) {
                // Error while opening the file. Not reported here.
                e.printStackTrace();
                return null;
            }
            return fos; // Return the output stream to write to

        }

        public String getFileName() {
            return fileName;
        }

        public String getMimeType() {
            return mtype;
        }

        public void setSlow(boolean value) {
            sleep = value;
        }

    }

}