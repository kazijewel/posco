package com.common.share;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
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

@SuppressWarnings("serial")
public class FileUploadBirthDate extends VerticalLayout 
{
	public Label status = new Label("Select Your Expected Image");

	private ProgressIndicator pi = new ProgressIndicator();
	private MyReceiver receiver = new MyReceiver();

	private HorizontalLayout btnLayout = new HorizontalLayout();
	public HorizontalLayout image = new HorizontalLayout();

	public Upload upload = new Upload(null, receiver);
	private NativeButton clearBtn = new NativeButton("Clear");

	public String fileName = "";
	private String coreName = "";

	public FileUploadBirthDate(String fname) 
	{
		coreName = fname;
		fileName = fname;
		setSpacing(true);		
		cmpInitialise();
		cmpAddition();
		variousEventActions();
	}

	private void cmpInitialise()
	{
		image.setWidth("500px");
		image.setHeight("550px");
		receiver.setSlow(true);
		upload.setImmediate(true);
		btnLayout.setSpacing(true);
		clearBtn.setWidth("60px");
		clearBtn.setHeight("28px");
		clearBtn.setIcon(new ThemeResource("../icon/cancel.png"));
	}

	private void cmpAddition()
	{
		addComponent(image);
		addComponent(status);
		btnLayout.addComponent(upload);
		btnLayout.addComponent(clearBtn);	
		addComponent(btnLayout);
	}

	private void variousEventActions()
	{
		upload.addListener(new Upload.ProgressListener()
		{
			public void updateProgress(long readBytes, long contentLength) 
			{
				// This method gets called several times during the update
				pi.setValue(new Float(readBytes / (float) contentLength));
			}

		});

		upload.addListener(new Upload.SucceededListener() 
		{
			public void uploadSucceeded(SucceededEvent event) 
			{
				// This method gets called when the upload finished successfully
				status.setValue(" \"" + event.getFilename()+ "\" succeeded");
				Embedded emb = new Embedded("Upload image",new ThemeResource("../"+fileName));
				emb.requestRepaint();
				emb.setWidth("500px");
				emb.setHeight("700px");
				image.removeAllComponents();
				image.addComponent(emb);
			}
		});

		upload.addListener(new Upload.FailedListener() 
		{
			public void uploadFailed(FailedEvent event) 
			{
				// This method gets called when the upload failed
				status.setValue("Uploading interrupted");
			}
		});

		clearBtn.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				image.removeAllComponents();
				fileName = "";
				status.setValue("Select an image to upload");
			}
		});
		fileName = "";
	}

	public class MyReceiver implements Receiver 
	{
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
			}
			catch(Exception exp){
			}
			fileName = coreName+fname+".jpg";
			FileOutputStream fos = null; // Output stream to write to
			File  file = new File(getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/"+ fileName);
			try {
				fos = new FileOutputStream(file);
			} 
			catch (final java.io.FileNotFoundException e) 
			{
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