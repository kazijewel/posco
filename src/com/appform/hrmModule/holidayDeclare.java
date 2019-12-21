package com.appform.hrmModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import com.common.share.CommonButton;
import com.common.share.CommonMethod;
import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;


@SuppressWarnings("serial")
public class holidayDeclare extends Window{
	private SessionBean sessionBean;
	private CommonMethod cm;
	private String menuId = "";
	private VerticalLayout vl = new VerticalLayout();
	private VerticalLayout vl1 = new VerticalLayout();
	private VerticalLayout vlayout=new VerticalLayout();
	private HorizontalLayout hc[] = new HorizontalLayout[7];

	private Label day[] = new Label[49];
	private int isSelect[] = new int[49];

	private InlineDateField date = new InlineDateField();
	private HorizontalLayout hl = new HorizontalLayout();
	private Label ttld = new Label();

	CommonButton cButton=new CommonButton("New", "Save", "Edit", "", "Refresh", "", "", "", "", "Exit");

	private Table table=new Table();

	private HorizontalLayout btnL1 = new HorizontalLayout();
	private HorizontalLayout btnL2 = new HorizontalLayout();

	private SimpleDateFormat dayName = new SimpleDateFormat("EEEEE");
	private SimpleDateFormat year = new SimpleDateFormat("yyyy");
	private SimpleDateFormat month = new SimpleDateFormat("MM");
	private SimpleDateFormat dDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private boolean isUpdate = false;
	private boolean isNew=false;
	private ArrayList<Integer> isHoliday = new ArrayList<Integer>();

	private ArrayList<NativeButton> Del=new ArrayList<NativeButton>();
	private ArrayList<PopupDateField> holidaydate=new ArrayList<PopupDateField>();
	private ArrayList<TextField> txtOccasion=new ArrayList<TextField>();
	int index = 0;
	int c = 0;
	String Notify = "";
	public holidayDeclare(SessionBean sessionBean,String menuId)
	{
		this.sessionBean = sessionBean;
		this.setCaption("HOLYDAY DECLARATION :: "+this.sessionBean.getCompany());
		this.setResizable(false);
        cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		for(int i=0;i<49;i++)
		{
			isSelect[i] = 0;
		}
		this.sessionBean = sessionBean;
		this.setWidth("680px");
		vl1.addComponent(date);
		calInitialise();

		date.setResolution(InlineDateField.RESOLUTION_MONTH);
		date.setImmediate(true);

		date.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(chkDataexists() && isNew)
				{
					btnIni(true);
					txtEnable(false);
					showNotification("Warning", "Already Declared Holiday For this Month!!!", Notification.TYPE_WARNING_MESSAGE);
				}
				c=0;
				calReGen();
				tableValueChange();
			}
		});

		vl1.addComponent(vl);
		vl1.setComponentAlignment(vl, Alignment.MIDDLE_CENTER);
		hl.addComponent(vl1);
		hl.setComponentAlignment(vl1, Alignment.MIDDLE_CENTER);
		tableinitialize();
		hl.setSpacing(true);
		hl.addComponent(table);
		vl.addComponent(ttld);

		btnL1.setSpacing(true);
		btnL2.setSpacing(true);

		btnL1.addComponent(cButton);

		hl.setMargin(true);
		vlayout.addComponent(hl);
		vlayout.addComponent(btnL1);
		vlayout.setComponentAlignment(btnL1, Alignment.MIDDLE_CENTER);
		this.addComponent(vlayout);
		btnAction();
		btnIni(true);
		txtEnable(false);
		authenticationCheck();
	}
	private void authenticationCheck()
	{
		cm.checkFormAction(menuId);
		if(!sessionBean.isSuperAdmin())
		{
		if(!sessionBean.isAdmin())
		{
			if(!cm.isSave)
			{cButton.btnSave.setVisible(false);}
			if(!cm.isEdit)
			{cButton.btnEdit.setVisible(false);}
			if(!cm.isDelete)
			{cButton.btnDelete.setVisible(false);}
			if(!cm.isPreview)
			{cButton.btnPreview.setVisible(false);}
		}
		}
	}
	private void tableValueChange()
	{
		for(int lo=0;lo<=holidaydate.size()-1;lo++)
		{
			if(holidaydate.get(lo).getValue()!=null)
			{
				holidaydate.get(lo).setReadOnly(false);
				holidaydate.get(lo).setValue(null);
				holidaydate.get(lo).setReadOnly(true);
				txtOccasion.get(lo).setValue("");
			}
		}
		table.setColumnFooter("Holiday Date",Integer.toString(0));
		String sqlvalue="select dDate,vOccasion from tbHoliday where year(ddate) = '"+year.format(date.getValue())+"' " +
				"and month(ddate) = '"+month.format(date.getValue())+"'";
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> itr=session.createSQLQuery(sqlvalue).list();
			if(!itr.isEmpty())
			{
				Iterator <?> iter=itr.iterator();
				if(isNew)
					c=0;
				while(iter.hasNext())
				{
					Object [] element=(Object[])iter.next();
					holidaydate.get(c).setReadOnly(false);
					holidaydate.get(c).setValue(element[0]);
					holidaydate.get(c).setReadOnly(true);
					txtOccasion.get(c).setValue(element[1].toString());
					if(c==holidaydate.size()-1)
						tableRowAdd(c+1);
					c++;
				}
				table.setColumnFooter("Holiday Date",Integer.toString(c));
			}
		}
		catch(Exception exp)
		{
			showNotification("tableValueChange",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void tableinitialize()
	{
		table.setWidth("350.0px");
		table.setHeight("275.0px");

		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("Remove", NativeButton.class, new NativeButton());
		table.setColumnWidth("Remove", 50);

		table.addContainerProperty("Holiday Date", PopupDateField.class, new PopupDateField());
		table.setColumnWidth("Holiday Date", 90);

		table.addContainerProperty("Occasion", TextField.class, new TextField());
		table.setColumnWidth("Occasion", 160);

		table.setColumnFooter("Del", "Total = ");
		table.setFooterVisible(true);

		table.setColumnAlignments(new String[]{Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER});
		for(int i=0;i<7;i++)
			tableRowAdd(i);

	}

	private void tableRowAdd(final int ar)
	{

		Del.add(ar,new NativeButton());
		Del.get(ar).setImmediate(true);
		Del.get(ar).setWidth("30.0px");
		Del.get(ar).setHeight("24.0px");
		Del.get(ar).setStyleName("btnTransparent");
		Del.get(ar).setIcon(new ThemeResource("../icons/cancel.png"));
		Del.get(ar).addListener(new ClickListener()
		{

			public void buttonClick(ClickEvent event)
			{
				if(holidaydate.get(ar).getValue()!=null)
				{
					index--;
					holidaydate.get(ar).setReadOnly(false);
					holidaydate.get(ar).setValue(null);
					holidaydate.get(ar).setReadOnly(true);
					txtOccasion.get(ar).setValue("");
					tableInterChange(ar);
					table.setColumnFooter("Holiday Date",Integer.toString(index));
				}
			}
		});

		holidaydate.add(ar, new PopupDateField());
		holidaydate.get(ar).setDateFormat("dd-MM-yyyy");
		holidaydate.get(ar).setReadOnly(true);
		holidaydate.get(ar).setImmediate(true);
		holidaydate.get(ar).setWidth("110.0px");

		txtOccasion.add(ar, new TextField());
		txtOccasion.get(ar).setImmediate(true);
		txtOccasion.get(ar).setWidth("170.0px");

		table.addItem(new Object[]{Del.get(ar),holidaydate.get(ar),txtOccasion.get(ar)},ar);

	}

	private void tableInterChange(final int row)
	{
		for(int i=row; i<holidaydate.size(); i++)
		{
			if(i+1<=holidaydate.size()-1)
			{
				if(holidaydate.get(i+1).getValue()!=null)
				{
					holidaydate.get(i).setReadOnly(false);
					holidaydate.get(i).setValue(holidaydate.get(i+1).getValue());
					holidaydate.get(i).setReadOnly(true);

					txtOccasion.get(i).setValue(txtOccasion.get(i+1).getValue());

					holidaydate.get(i+1).setReadOnly(false);
					holidaydate.get(i+1).setValue(null);
					holidaydate.get(i+1).setReadOnly(true);
					txtOccasion.get(i+1).setValue("");

				}
			}
		}
	}

	private boolean chkDataexists()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sqlvalue="select dDate,vOccasion from tbHoliday where year(ddate) = '"+year.format(date.getValue())+"' and month(ddate) = '"+month.format(date.getValue())+"'";
			List <?> lst=session.createSQLQuery(sqlvalue).list();
			if(!lst.isEmpty())
			{
				return true;
			}
			else
				return false;
		}
		catch(Exception exp)
		{
			showNotification("CHKDATAEXISATS",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
		return false;
	}

	private void btnAction()
	{
		cButton.btnNew.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(!chkDataexists())
				{
					btnIni(false);
					txtEnable(true);
					isUpdate = false;
					isNew=true;
					index=0;
					tableValueChange();
					calReGen();
				}
				else
					showNotification("Warning", "Already Declared Holiday For this Month!!!", Notification.TYPE_WARNING_MESSAGE);
			}
		});

		cButton.btnEdit.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(holidaydate.get(0).getValue() != null)
				{
					btnIni(false);
					txtEnable(true);
					isUpdate = true;
					isNew=false;
					index = 0;
					for(int rowcount=0;rowcount<=holidaydate.size()-1;rowcount++)
					{
						if(holidaydate.get(rowcount).getValue()!=null)
						{
							index++;
						}
					}

					calReGen();
				}
				else
				{
					showNotification("Warning", "No Data Found!!!", Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		cButton.btnSave.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(tableCheck())
					btnSaveAction();
				else
					showNotification("Warning", Notify, Notification.TYPE_WARNING_MESSAGE);
			}
		});

		cButton.btnRefresh.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				isNew=false;
				btnIni(true);
				txtEnable(false);
				calReGen();
			}
		});
		cButton.btnExit.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				close();
			}
		});
	}
	
	private boolean tableCheck()
	{
		int count = 0;
		for(int in=0;in<=holidaydate.size()-1;in++)
		{
			if(holidaydate.get(in).getValue()!=null)
			{
				if(txtOccasion.get(in).getValue().toString().isEmpty())
				{
					txtOccasion.get(in).focus();
					Notify = "Please Provide Occasion Name!!!";
					return false;
				}
				else
					count++;
			}
		}
		if(count>0)
			return true;
		else
		{
			Notify = "Please Provide Holiday Date!!!";
			return false;
		}
	}

	private void btnSaveAction()
	{
		if(isUpdate)
		{
			MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to update information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.setStyleName("cwindowMB");
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						updateData();
						cButton.btnNew.focus();
					}
				}
			});
		}
		else
		{
			MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to save all information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.setStyleName("cwindowMB");
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						insertData();
						cButton.btnNew.focus();
					}
				}
			});
		}
	}

	private void updateData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{
			String holidate="";
			String sqlDel="DELETE FROM tbHoliday WHERE Month(dDate) = '"+month.format(date.getValue())+"' and year(dDate) = '"+year.format(date.getValue())+"'";
			session.createSQLQuery(sqlDel).executeUpdate();

			for(int in=0;in<=holidaydate.size()-1;in++)
			{
				if(holidaydate.get(in).getValue()!=null)
				{
					holidate=new SimpleDateFormat("yyyy-MM-dd").format(holidaydate.get(in).getValue());
					String sql="INSERT INTO tbHoliday(dDate,vOccasion,userId,userIp,entryTime,vUserName) VALUES('"+holidate+"','"+txtOccasion.get(in).getValue().toString()+"','"+sessionBean.getUserId()+
							"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP,'"+sessionBean.getUserName()+"')";
					
					System.out.println(sql);
					session.createSQLQuery(sql).executeUpdate();
				}
			}
			prcExecute(session);
			txtEnable(false);
			btnIni(true);
			this.showNotification("All Information Saved Successfully");
			tx.commit();
		}
		catch(Exception exp)
		{
			tx.rollback();
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void insertData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{
			String holidate="";
			for(int in=0;in<=holidaydate.size()-1;in++)
			{
				if(holidaydate.get(in).getValue()!=null)
				{
					holidate=new SimpleDateFormat("yyyy-MM-dd").format(holidaydate.get(in).getValue());
					String sql="INSERT INTO tbHoliday(dDate,vOccasion,userId,userIp,entryTime,vUserName) VALUES('"+holidate+"','"+txtOccasion.get(in).getValue().toString().replaceAll("'", "''")+"','"+sessionBean.getUserId()+
							"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP,'"+sessionBean.getUserName()+"')";
					
					System.out.println(sql);
					
					session.createSQLQuery(sql).executeUpdate();
					
					
				}
			}
			//prcExecute(session);
			txtEnable(false);
			btnIni(true);
			this.showNotification("All Information Saved Successfully");
			tx.commit();
		}
		catch(Exception exp)
		{
			tx.rollback();
			showNotification("Error1",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void prcExecute(Session session)
	{
		String query = "exec prcHolidayFrequency '"+month.format(date.getValue())+"','"+year.format(date.getValue())+"',"
				+ "'"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"'";
		System.out.println("query1 "+query);
		session.createSQLQuery(query).executeUpdate();
		query = "exec prcMonthlyAttendanceSummaryHoliday '"+dDateFormat.format(date.getValue())+"','%',"
				+ "'"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"'";
		session.createSQLQuery(query).executeUpdate();
		System.out.println("query2 "+query);
	}

	@SuppressWarnings("deprecation")
	private void calReGen()
	{
		isHoliday.clear();
		int y = Integer.valueOf(year.format(date.getValue()).trim());
		int m = Integer.valueOf(month.format(date.getValue()).trim());
		int md[]={31,28,31,30,31,30,31,31,30,31,30,31};
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select day(ddate) FROM TbHoliday  WHERE year(ddate) = "+y+" and month(ddate) = '"+m+"'";
			List <?> list = session.createSQLQuery(query).list();
			for(Iterator <?> iter = list.iterator(); iter.hasNext();)
			{
				isHoliday.add(Integer.valueOf(iter.next().toString()));
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("OK",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}

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
		for(int j=1;i<h;i++,j++)
		{
			day[i].setValue(j);
			day[i].setEnabled(true);
			for(int x=0;x<isHoliday.size();x++)
			{
				if(j==isHoliday.get(x))
				{
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
					if(i>6)
					{
						if(isSelect[i]==0)
						{
							if(day[i].getValue().toString().length()>0)
							{
								day[i].setStyleName("backred");
								String asd = new SimpleDateFormat("yyyy-MM").format(date.getValue())+"-"+day[i].getValue();
								dbdatequery(asd);
								isSelect[i] = 1;
							}
						}
						else
						{
							if(day[i].isEnabled())
							{ 
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
			day[i] = new Label();
			day[i].setWidth("25px");
			day[i].setHeight("20px");

			hc[i/7].addComponent(day[i]);
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

	public void dbdatequery(String asd)
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select convert(date,'"+asd+"')";
			Object objdate=session.createSQLQuery(query).list().iterator().next();
			boolean t=false;
			for(int cnt=0;cnt<=holidaydate.size()-1;cnt++)
			{
				if(holidaydate.get(cnt).getValue()!=null)
				{
					if(holidaydate.get(cnt).getValue().equals(objdate))
					{
						t=true;
						break;
					}
				}
			}
			if(!t)
			{
				holidaydate.get(index).setReadOnly(false);
				holidaydate.get(index).setValue(objdate);
				holidaydate.get(index).setReadOnly(true);
				if(dayName.format(holidaydate.get(index).getValue()).equalsIgnoreCase("Friday"))
					txtOccasion.get(index).setValue(dayName.format(holidaydate.get(index).getValue()));
				if(index==holidaydate.size()-1)
					tableRowAdd(index+1);
				index++;

				table.setColumnFooter("Holiday Date",Integer.toString(index));
			}
			else
				showNotification("Warning", "Date Already Exists!!!", Notification.TYPE_WARNING_MESSAGE);
		}
		catch(Exception exp)
		{

		}
		finally{session.close();}
	}

	private void txtEnable(boolean t)
	{
		vl.setEnabled(t);
		table.setEnabled(t);
	}

	private void btnIni(boolean t)
	{
		cButton.btnNew.setEnabled(t);
		cButton.btnEdit.setEnabled(t);
		cButton.btnSave.setEnabled(!t);
		cButton.btnRefresh.setEnabled(!t);
	}
}