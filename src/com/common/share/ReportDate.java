package com.common.share;

import java.util.Date;
import java.util.Iterator;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

public class ReportDate extends Window
{
	public Date getTime;
	public ReportDate()
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx = null;
		try
		{
			tx = session.beginTransaction();
			String query="select CURRENT_TIMESTAMP";
			Iterator iter = session.createSQLQuery(query).list().iterator();
			if(iter.hasNext())
			{
				getTime = (Date) iter.next();
			}
		}
		catch(Exception ex)
		{
			this.getParent().showNotification("Error",ex+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
}
