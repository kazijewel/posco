package com.common.share;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.BaseTheme;

@SuppressWarnings("serial")
public class ImmediateUploadExampleConfirmation extends VerticalLayout
{
	//public Label status = new Label("Select jpg/pdf file");
	private ProgressIndicator pi = new ProgressIndicator();
	private MyReceiver receiver = new MyReceiver();

	private HorizontalLayout progressLayout = new HorizontalLayout();
	private HorizontalLayout btnLayout = new HorizontalLayout();
	public HorizontalLayout image = new HorizontalLayout();

	public Upload upload = new Upload(null, receiver);
	public Button nbDOBPreview = new Button();

	public String fileName = "";
	public String coreName = "";
	public String	fileWithExtension = "";
	public String fileExtension = "";
	
	public boolean actionCheck = false;

	public ImmediateUploadExampleConfirmation(String fname)
	{
		coreName = fname;
		fileName = fname;
		setSpacing(true);

		initialCmp();
		addCmp();
		upLoadingEvents();
	}

	public void initialCmp()
	{
		/*image.setWidth("70px");
		image.setHeight("70px");*/
		
		receiver.setSlow(true);
		upload.setImmediate(true);		
		upload.setImmediate(true);
		upload.setWidth("125px");
		upload.setHeight("20px");
		upload.setButtonCaption("Confirmation Letter");
		
		nbDOBPreview.setStyleName(BaseTheme.BUTTON_LINK);
//		nbDOBPreview.addStyleName("icon-after-caption");
		nbDOBPreview.setImmediate(true);
		nbDOBPreview.setCaption("Preview");
		nbDOBPreview.setIcon(new ThemeResource("../icons/document-pdf.png"));
	
		progressLayout.setSpacing(true);
		progressLayout.setVisible(false);
		progressLayout.addComponent(pi);
		progressLayout.setComponentAlignment(pi, "middle");	
	}

	public void addCmp()
	{
		addComponent(image);
		//addComponent(status);

		btnLayout.addComponent(upload);
//		btnLayout.addComponent(nbDOBPreview);

		addComponent(btnLayout);
		addComponent(progressLayout);
	}

	public void upLoadingEvents()
	{
		final Button cancelProcessing = new Button("Cancel");
		cancelProcessing.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				upload.interruptUpload();
			}
		});
		cancelProcessing.setStyleName("small");
		progressLayout.addComponent(cancelProcessing);

		/*
		 * =========== Add needed listener for the upload component: start,
		 * progress, finish, success, fail ===========
		 */

		upload.addListener(new Upload.StartedListener() {
			public void uploadStarted(StartedEvent event) {
				try{
					// This method gets called immediatedly after upload is started
					upload.setVisible(false);
					progressLayout.setVisible(true);
					pi.setValue(0f);
					pi.setPollingInterval(500);
					//status.setValue(" \"" + event.getFilename() + "\"");
					fileWithExtension = event.getFilename();
					
					fileExtension =  fileWithExtension.toString().substring(fileWithExtension.toString().lastIndexOf("."), fileWithExtension.toString().length());
					
					System.out.println("fileExtension :"+fileExtension);
					
					System.out.println(fileWithExtension);
					if(fileExtension.equalsIgnoreCase(".jpg") || fileExtension.equalsIgnoreCase(".pdf")){
						fileName = fileWithExtension;
					}
					else{
						fileName = "Iamge";
						image.removeAllComponents();
						//status.setValue("Select BirthDay Certificate");
						getParent().getWindow().showNotification("<font size= "+4+"><i> .jpg/.pdf Files are allowed</i></font>",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				catch (Exception e) {
					// TODO: handle exception
				}
			}
		});
		
        upload.addListener(new Upload.SucceededListener() {
            public void uploadSucceeded(SucceededEvent event) {
                // This method gets called when the upload finished successfully
                actionCheck = true;
            }
        });

		upload.addListener(new Upload.ProgressListener() {
			public void updateProgress(long readBytes, long contentLength) {
				// This method gets called several times during the update
				pi.setValue(new Float(readBytes / (float) contentLength));
			}

		});

		upload.addListener(new Upload.FailedListener() {
			public void uploadFailed(FailedEvent event) {
				// This method gets called when the upload failed
				//status.setValue("Uploading interrupted");
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

	public class MyReceiver implements Receiver 
	{
		//  private String fileName;
		private String mtype;
		private boolean sleep;
		private int total = 0;
		Random r = new Random();

		public OutputStream receiveUpload(String f, String mimetype) 
		{
			String fname = r.nextInt(10000)+"";
			try{
				File f1 = new File(getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/"+fileName);

				if(f1.isFile()){
					f1.delete();
				}
			}catch(Exception exp){

			}

			System.out.println("Birthday Certificate : "+fileName);

			fileName = coreName+fname+fileExtension;


			FileOutputStream fos = null; // Output stream to write to
			File  file = new File(getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/"+ fileName);
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

		public void setSlow(boolean value){
			sleep = value;
		}
	}
}
