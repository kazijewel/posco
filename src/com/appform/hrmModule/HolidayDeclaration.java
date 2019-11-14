package com.appform.hrmModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;


public class HolidayDeclaration extends Window{
	private SessionBean sessionBean;
	private VerticalLayout vl = new VerticalLayout();
	private HorizontalLayout hc[] = new HorizontalLayout[7];

	private Label day[] = new Label[49];
	private int isSelect[] = new int[49];

	private InlineDateField date = new InlineDateField();
	private HorizontalLayout hl = new HorizontalLayout();
	private Label ttld = new Label();

	private Button newBtn = new Button("New");
	private Button updateBtn = new Button("Update");
	private Button saveBtn = new Button("Save");
	private Button cancelBtn = new Button("Cancel");
	//private Button deleteBtn = new Button("Delete");
	//private Button findBtn = new Button("Find");

	private HorizontalLayout btnL1 = new HorizontalLayout();
	private HorizontalLayout btnL2 = new HorizontalLayout();

	private SimpleDateFormat year = new SimpleDateFormat("yyyy");
	private SimpleDateFormat month = new SimpleDateFormat("MM");
	private String cw = "75px";
	private boolean isUpdate = false;
	private ArrayList<Integer> isHoliday = new ArrayList<Integer>();

	public HolidayDeclaration(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("HOLYDAY DECLARATION"+this.sessionBean.getCompany());
		this.setResizable(false);
		for(int i=0;i<49;i++)
		{
			isSelect[i] = 0;
		}
		this.sessionBean = sessionBean;
		this.setWidth("280px");
		calInitialise();
		//vl.setMargin(true);
		//date.setValue(new java.util.Date());
		date.setResolution(InlineDateField.RESOLUTION_MONTH);
		date.setImmediate(true);

		date.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				calReGen();
			}
		});
		this.addComponent(date);

		hl.addComponent(vl);
		vl.addComponent(ttld);
		ttld.setValue("Total Holiday:");

		btnL1.setSpacing(true);
		btnL2.setSpacing(true);

		btnL1.addComponent(newBtn);
		newBtn.setWidth(cw);
		btnL1.addComponent(updateBtn);
		updateBtn.setWidth(cw);
		btnL1.addComponent(saveBtn);
		saveBtn.setWidth(cw);

		btnL2.addComponent(cancelBtn);
		cancelBtn.setWidth(cw);

		hl.setMargin(true);
		this.addComponent(hl);
		this.addComponent(btnL1);
		this.addComponent(btnL2);
		btnAction();
		btnIni(true);
		txtEnable(false);
	}

	private void btnAction()
	{
		newBtn.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				btnIni(false);
				txtEnable(true);
				isUpdate = false;
				calReGen();
			}
		});

		updateBtn.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				btnIni(false);
				txtEnable(true);
				isUpdate = true;
				calReGen();
			}
		});

		saveBtn.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				saveBtnAction();
			}
		});

		cancelBtn.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				btnIni(true);
				txtEnable(false);
				calReGen();
			}
		});

	}

	private void saveBtnAction()
	{
		if(isUpdate)
		{
			MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to update information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{

						updateData();
						newBtn.focus();
					}
				}
			});
		}
		else
		{
			MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to save all information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{

						insertData();
						newBtn.focus();
					}
				}
			});
		}
	}

	private void updateData()
	{
		if(sessionBean.isUpdateable())
		{
			Transaction tx = null;
			try
			{
				boolean c = true;
				Session session = SessionFactoryUtil.getInstance().getCurrentSession();
				tx = session.beginTransaction();
				session.createSQLQuery("DELETE FROM tbHoliday WHERE YEAR(ddate) = "+new SimpleDateFormat("yyyy").format(date.getValue())
						+" and MONTH(ddate) = "+new SimpleDateFormat("MM").format(date.getValue())).executeUpdate();
				String d = new SimpleDateFormat("yyyy-MM").format(date.getValue());
				System.out.println("1");
				for(int i=7;i<49;i++)
				{
					System.out.println("2");
					if(isSelect[i]==1&&day[i].isEnabled()&&day[i].getValue().toString().length()>0)
					{
						System.out.println("3");
						System.out.println(d+"-"+day[i].getValue());
						if(isValid(d+"-"+day[i].getValue()))
						{
							System.out.println("4");
							session.createSQLQuery("INSERT INTO tbHoliday(dDate,userId,userIp,entryTime) VALUES('"+d+"-"+day[i].getValue()+"','"+sessionBean.getUserId()+
									"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP)").executeUpdate();
						}
						else
						{
							c = false;
							this.getParent().showNotification("","Date is not valid.Because its a back dated date. Data already inserted after "+day[i].getValue()+"-"+new SimpleDateFormat("MM-yy").format(date.getValue()),Notification.TYPE_WARNING_MESSAGE);
							tx.rollback();
							break;
						}
					}
				}
				if(c)
				{
					tx.commit();
					this.getParent().showNotification("Holiday update successfully.");
					btnIni(true);
					calReGen();
				}
			}
			catch(Exception exp)
			{
				tx.rollback();
				this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			}
		}
		else
		{
			this.getParent().showNotification("Authentication Failed","You have not proper authentication for update.",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void insertData()
	{
		if(sessionBean.isSubmitable())
		{
			Transaction tx = null;
			try
			{
				boolean c = true;
				Session session = SessionFactoryUtil.getInstance().getCurrentSession();
				tx = session.beginTransaction();
				String d = new SimpleDateFormat("yyyy-MM").format(date.getValue());
				for(int i=7;i<49;i++)
				{
					if(isSelect[i]==1&&day[i].isEnabled()&&day[i].getValue().toString().length()>0)
					{
						if(isValid(d+"-"+day[i].getValue()))
						{
							session.createSQLQuery("INSERT INTO tbHoliday(dDate,userId,userIp,entryTime) VALUES('"+d+"-"+day[i].getValue()+"','"+sessionBean.getUserId()+
									"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP)").executeUpdate();
						}
						else
						{
							c = false;
							this.getParent().showNotification("","Date is not valid.Because its a back dated date. Data already inserted after "+day[i].getValue()+"-"+new SimpleDateFormat("MM-yy").format(date.getValue()),Notification.TYPE_WARNING_MESSAGE);
							tx.rollback();
							break;
						}
					}
				}
				if(c)
				{
					tx.commit();
					this.getParent().showNotification("Holiday set successfully.");
					btnIni(true);
					calReGen();
				}
			}
			catch(Exception exp)
			{
				tx.rollback();
				this.getParent().showNotification("Error1",exp+"",Notification.TYPE_ERROR_MESSAGE);
			}
		}
		else
		{
			this.getParent().showNotification("Authentication Failed","You have not proper authentication for save.",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void setHoliday()
	{
		String d = new SimpleDateFormat("yyyy-MM").format(date.getValue());
		for(int i=7;i<49;i++)
		{
			if(isSelect[i]==1)
			{
				if(day[i].isEnabled())
				{
					if(day[i].getValue().toString().length()>1)
						System.out.println(d+"-"+day[i].getValue());
					else
						System.out.println(d+"-0"+day[i].getValue());
				}
			}
		}
	}

	private void calReGen()
	{
		isHoliday.clear();
		int y = Integer.valueOf(year.format(date.getValue()).trim());
		int m = Integer.valueOf(month.format(date.getValue()).trim());

		int md[]={31,28,31,30,31,30,31,31,30,31,30,31};

		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			//Iterator it = session.createSQLQuery("select day(ddate) FROM TbHoliday  WHERE year(ddate) = "+y+" and month(ddate) = "+m).list().iterator();//session.createSQLQuery("select day(ddate) FROM TbHoliday  WHERE year(ddate) = "+y+" and month(ddate) = "+m).iterate();

			String query = "select day(ddate) FROM TbHoliday  WHERE year(ddate) = "+y+" and month(ddate) = '"+m+"'";
			System.out.println("A");

			List list = session.createSQLQuery(query).list();

			for(Iterator iter = list.iterator(); iter.hasNext();)
			{
				isHoliday.add(Integer.valueOf(iter.next().toString()));
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("OK",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}

		for(int i=0;i<isSelect.length;i++)
			isSelect[i]=0;

		if(y%4==0)
			md[1]=29;
		else
			md[1]=28;
		for(int i=7;i<49;i++)
		{
			day[i].setValue("");
			day[i].setStyleName("");
		}
		Date d = new Date(y+"/"+m+"/01");
		int i = d.getDay()+7;
		int h = md[m-1]+i;
		for(int j=1;i<h;i++,j++){
			day[i].setValue(j);
			day[i].setEnabled(true);
			for(int x=0;x<isHoliday.size();x++){
				if(j==isHoliday.get(x)){
					isSelect[i] = 1;
					day[i].setStyleName("backred");
					if(!isUpdate)
						day[i].setEnabled(false);
				}
			}
		}
	}

	private void calInitialise()
	{
		for(int i=0;i<7;i++)
		{
			hc[i] =  new HorizontalLayout();
			vl.addComponent(hc[i]);
			hc[i].addListener(new LayoutClickListener()
			{
				public void layoutClick(LayoutClickEvent event)
				{
					Component child = event.getChildComponent();
					int i = Integer.valueOf(child.getDebugId());
					if(i>6){
						if(isSelect[i]==0){
							if(day[i].getValue().toString().length()>0){
								day[i].setStyleName("backred");
								isSelect[i] = 1;
							}
						}else{
							if(day[i].isEnabled()){ 
								day[i].setStyleName("");
								isSelect[i] = 0;
							}
						}
					}
				}
			});
		}
		for(int i=0;i<49;i++)
		{
			//day[i].addListener( new ClickListener())
			day[i] = new Label();
			day[i].setWidth("25px");
			day[i].setHeight("20px");

			hc[i/7].addComponent(day[i]);
			//if(i>6)
			//day[i].setValue(i);
			day[i].setDebugId(i+"");
		}
		day[0].setValue("S");
		day[1].setValue("M");
		day[2].setValue("T");
		day[3].setValue("W");
		day[4].setValue("T");
		day[5].setValue("F");
		day[6].setValue("S");
	}

	private void txtEnable(boolean t)
	{
		vl.setEnabled(t);
	}

	private void btnIni(boolean t)
	{
		newBtn.setEnabled(t);
		updateBtn.setEnabled(t);
		saveBtn.setEnabled(!t);
		cancelBtn.setEnabled(!t);
		//findBtn.setEnabled(t);
	}

	private boolean isValid(String d)
	{
		/*Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx = session.beginTransaction();
		try
		{
			if(Integer.valueOf(session.createSQLQuery("SELECT count(*) FROM TbStockDetails v WHERE v.ddate >= '"+d+"'").list().iterator().next().toString())==0)
				return true;
			else
				return false;
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("OK",exp+"",Notification.TYPE_ERROR_MESSAGE);
			return false;
		}*/
		return true;
	}
}