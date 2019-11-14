package com.common.share;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.query.JRHibernateQueryExecuterFactory;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.vaadin.applet.AppletIntegration;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;
public class ReportViewer extends Window{
	public ReportViewer(HashMap para,String jasper,String pdfOutPath,String url,Boolean isHqlSql) throws JRException, IOException{
		ReportPdf(para,jasper,pdfOutPath,url,isHqlSql); 
	}
	public ReportViewer(HashMap para,String jasper,String pdfOutPath,String url,Boolean isHqlSql,String appletCodebase,boolean isPdf) throws JRException, IOException{
		if(isPdf == true)
			ReportPdf(para,jasper,pdfOutPath,url,isHqlSql); 
		else
			ReportApplet(para,jasper,pdfOutPath,url,isHqlSql,appletCodebase);
	}
	public int ReportPdf(HashMap para,String jasper,String pdfOutPath,String url,Boolean isHqlSql) throws JRException, IOException
	{
		HashMap hm = para;
		Random r = new Random();
		//i++;
		//String fname = i+".pdf";//
		String fname =   r.nextInt(1000000)+".pdf";

		final String fpath = pdfOutPath+"/"+fname;
		System.out.println(fpath);
		FileOutputStream of = new FileOutputStream(fpath);
		Transaction tx = null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();
		if(isHqlSql)
		{
			hm.put(JRHibernateQueryExecuterFactory.PARAMETER_HIBERNATE_SESSION, session);
			JasperRunManager.runReportToPdfStream(getClass().getClassLoader().getResourceAsStream(jasper), of, hm);
		}
		else
		{
			JasperRunManager.runReportToPdfStream(getClass().getClassLoader().getResourceAsStream(jasper),of, hm, session.connection());
		}
		tx.commit();
		of.close();

		File f = new File(fpath);

		if(f.length()>1000)
		{
			Embedded e = new Embedded(null, new ExternalResource(url+"/"+fname));
			e.setType(Embedded.TYPE_BROWSER);
			e.setWidth("100%");
			e.setHeight("100%");

			this.getContent().setSizeFull();
			this.setWidth("100%");
			this.setHeight("700px");
			this.addComponent(e);
		}
		else
		{
			this.addComponent(new Label("<B>Data does not exist against in your given criteria.</B>",Label.CONTENT_XHTML));
			this.setWidth("400px");
			this.setResizable(false);
			f.delete();
		}
		this.setCloseShortcut(KeyCode.ESCAPE);
		//this.getContent().setStyleName(Reindeer.LAYOUT_BLUE);
		this.getContent().setStyleName("cwindow");
		this.center();
		this.addListener(new Window.CloseListener()
		{
			public void windowClose(CloseEvent e)
			{
				try
				{
					File file = new File(fpath);
					file.delete();
				}
				catch(Exception exp)
				{

				}
			}
		});
		return 0;
	}

	public int ReportApplet(HashMap para,String jasper,String pdfOutPath,final String url,Boolean isHqlSql,final String codeBase) throws JRException, IOException
	{
		HashMap hm = para;
		Random r = new Random();
		//i++;
		//String fname = i+".pdf";//
		final String fname =   r.nextInt(1000000)+".jrprint";

		final String fpath = pdfOutPath+"/"+fname;
		//System.out.println(fpath);
		FileOutputStream of = new FileOutputStream(fpath);
		Transaction tx = null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();
		if(isHqlSql)
		{
			hm.put(JRHibernateQueryExecuterFactory.PARAMETER_HIBERNATE_SESSION, session);
			JasperFillManager.fillReportToStream(getClass().getClassLoader().getResourceAsStream(jasper), of, hm);
		}
		else
			JasperFillManager.fillReportToStream(getClass().getClassLoader().getResourceAsStream(jasper),of, hm, session.connection());
		tx.commit();
		of.close();

		File f = new File(fpath);

		if(f.length()>1000)
		{
			/*Embedded e = new Embedded(null, new ExternalResource(url+"/"+fname));
			e.setType(Embedded.TYPE_BROWSER);
			e.setWidth("100%");
			e.setHeight("100%");
			 */
			AppletIntegration applet = new AppletIntegration() 
			{
				private static final long serialVersionUID = 1L;

				@Override
				public void attach() 
				{
					setAppletArchives(Arrays.asList(new String[] { "JRViewerApplet.jar" }));
					setCodebase(codeBase);
					setAppletClass("JRViewerApplet.class");
					setWidth("100%");
					setHeight("100%");
					setAppletParams("REPORT_URL", url+"/"+fname);
				}
			};
			applet.setWidth("100%");
			applet.setHeight("100%");
			this.getContent().setSizeFull();
			this.setWidth("100%");
			this.setHeight("700px");
			this.addComponent(applet);
		}
		else
		{
			this.addComponent(new Label("<B>Data does not exist against in your given criteria.</B>",Label.CONTENT_XHTML));
			this.setWidth("400px");
			this.setResizable(false);
			f.delete();
		}

		this.setCloseShortcut(KeyCode.ESCAPE);
		this.getContent().setStyleName(Reindeer.LAYOUT_BLUE);
		this.center();
		this.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				try
				{
					File file = new File(fpath);
					file.delete();
				}
				catch(Exception exp)
				{
				}
			}
		});
		return 1;
	}
}
