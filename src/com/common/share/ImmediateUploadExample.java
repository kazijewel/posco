package com.common.share;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

//import acc.appform.transaction.CashVoucherPay;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class ImmediateUploadExample extends VerticalLayout {

    public Label status = new Label("<font size=1px>(Select .pdf/.jpg Format)</font>",Label.CONTENT_XHTML);

    private ProgressIndicator pi = new ProgressIndicator();

    private MyReceiver receiver = new MyReceiver();

    private HorizontalLayout progressLayout = new HorizontalLayout();

    public Upload upload = new Upload(null, receiver);
    
    public String fileName = "";
    private String coreName = "";
    
    public boolean actionCheck = false;

    SessionBean sessionBean;
    public ImmediateUploadExample(String fname) 
    {
    	coreName = fname;
    	fileName = fname;
        setSpacing(true);

        // Slow down the upload
        receiver.setSlow(true);

        addComponent(status);
        addComponent(upload);
       // addComponent(progressLayout);

        // Make uploading start immediately when file is selected
        upload.setImmediate(true);
        upload.setWidth("80px");
        upload.setHeight("20px");
        upload.setButtonCaption("Attach Bill");

        progressLayout.setSpacing(true);
        progressLayout.setVisible(false);
        progressLayout.addComponent(pi);
        progressLayout.setComponentAlignment(pi, Alignment.MIDDLE_LEFT);

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
                
                fileName = event.getFilename();
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
                status.setValue("Upload \"" + event.getFilename()
                        + "\" succeeded");
                actionCheck = true;
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
          //      upload.setCaption("Select another file");
            }
        });

    }
    
    public class MyReceiver implements Receiver {

        //  private String fileName;
          private String mtype;
          private boolean sleep;
          private int total = 0;
          
          Random r = new Random();
          public OutputStream receiveUpload(String f, String mimetype) {
          	String fname = r.nextInt(10000)+"";
          	try{
          		File f1 = new File(getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+
  						"/VAADIN/themes/"+fileName);
          		if(f1.isFile())
          		 f1.delete();
          	}catch(Exception exp){
          		
          	}

          	if(fileName.endsWith(".jpg")){
          		fileName = coreName+fname+".jpg";
          	}else{
          		fileName = coreName+fname+".pdf";
          	}
          	
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

          	/*fileName = filename;
              mtype = mimetype;
              return new OutputStream() {
                  @Override
                  public void write(int b) throws IOException {
                      total++;
                      if (sleep && total % 10000 == 0) {
                          try {
                              Thread.sleep(100);
                          } catch (InterruptedException e) {
                              // TODO Auto-generated catch block
                              e.printStackTrace();
                          }
                      }
                  }
              };*/
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

/*    public static class MyReceiver implements Receiver {

        private String fileName;
        private String mtype;
        private boolean sleep;
        private int total = 0;

        public OutputStream receiveUpload(String filename, String mimetype) {
            fileName = filename;
            mtype = mimetype;
            return new OutputStream() {
                @Override
                public void write(int b) throws IOException {
                    total++;
                    if (sleep && total % 10000 == 0) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            };
        }
        
        Random r = new Random();
        
        public OutputStream receiveUpload(String f, String mimetype) {
        	String fname = r.nextInt(10000)+"";
        	try{
        		File f1 = new File(getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+
						"/VAADIN/themes/"+fileName);
        		if(f1.isFile())
        		 f1.delete();
        	}catch(Exception exp){
        		
        	}
        	fileName = coreName+fname+".pdf";
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

        	fileName = filename;
            mtype = mimetype;
            return new OutputStream() {
                @Override
                public void write(int b) throws IOException {
                    total++;
                    if (sleep && total % 10000 == 0) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            };
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

    }*/

}
