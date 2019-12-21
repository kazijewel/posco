package com.appform.hrmModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.MessageBox;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.common.share.CommonMethod;
import com.common.share.FocusMoveByEnter;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class FestivalBonus extends Window
{
	private AbsoluteLayout mainLayout;
	private SessionBean sessionBean;
	private Label lblBonusDate;
	private PopupDateField dBonusDate;
	private ComboBox cmbUnit;
	//private CheckBox chkUnitAll;
	private Label lblSection;
	private ComboBox cmbSection;
	private CheckBox chkSectionAll;
	private ComboBox cmbDepartment;
	private CheckBox chkDepartmentAll;
	private ComboBox cmbEmployeeType;
	private CheckBox chkEmployeeTypeAll;
	private Label lblBonusOccation;
	private ComboBox cmbBonusOccation;
	private ProgressIndicator PI;
	private Worker1 worker1;
	ArrayList<Component> allComp = new ArrayList<Component>();

	private NativeButton btnGenarate;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dCharMonth = new SimpleDateFormat("MMMMM");
	private SimpleDateFormat dYear = new SimpleDateFormat("yyyy");
	private CommonMethod cm;
	private String menuId = "";
	public FestivalBonus(SessionBean sessionBean,String menuId)
	{
		this.sessionBean= sessionBean;
		this.setCaption("FESTIVAL BONUS :: " +sessionBean.getCompany());
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		buildMainLayout();
		setContent(mainLayout);
		componentIni(true);
		cmbUnitData();
		setEventAction();
		authenticationCheck();
		focusEnter();
		dBonusDate.focus();
	}

	private void authenticationCheck()
	{
		cm.checkFormAction(menuId);
		if(!sessionBean.isSuperAdmin())
		{
		if(!sessionBean.isAdmin())
		{
			if(!cm.isSave)
			{btnGenarate.setVisible(false);}
			
		}
		}
	}

	private void setEventAction()
	{
		btnGenarate.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				formValidation();
			}
		});

		cmbUnit.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbUnit.getValue()!=null)
				{
					cmbDepartment.removeAllItems();
					cmbDepartmentData();
				}
			}
		});

		/*chkUnitAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSection.removeAllItems();
				if(chkUnitAll.booleanValue())
				{
					cmbUnit.setEnabled(false);
					cmbUnit.setValue(null);
					cmbSectionData();
				}
				else
				{
					cmbUnit.setEnabled(true);
				}
			}
		});*/
		cmbDepartment.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbUnit.getValue()!=null)
				{
					if(cmbDepartment.getValue()!=null)
					{
						cmbSectionData();
					}
				}
			}
		});

		chkDepartmentAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbUnit.getValue()!=null)
				{
					if(chkDepartmentAll.booleanValue())
					{
						cmbDepartment.setEnabled(false);
						cmbDepartment.setValue(null);
						cmbSectionData();
					}
					else
					{
						cmbDepartment.setEnabled(true);
					}
				}
			}
		});


		cmbSection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
				{
					if(cmbSection.getValue()!=null)
					{
						cmbEmployeeTypeDataAdd();
					}
				}
			}
		});

		chkSectionAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
				{
					if(chkSectionAll.booleanValue())
					{
						cmbSection.setEnabled(false);
						cmbSection.setValue(null);
						cmbEmployeeTypeDataAdd();
					}
					else
					{
						cmbSection.setEnabled(true);
					}
				}
			}
		});

		chkEmployeeTypeAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
				{
					if(chkEmployeeTypeAll.booleanValue())
					{
						cmbEmployeeType.setEnabled(false);
						cmbEmployeeType.setValue(null);
						
					}
					else
					{
						cmbEmployeeType.setEnabled(true);
					}
				}
			}
		});
	}

	private void cmbEmployeeTypeDataAdd()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vEmployeeType,vEmployeeType from tbEmpOfficialPersonalInfo " +
					" where vUnitId='"+cmbUnit.getValue()+"' " +
					" and vDepartmentId like '"+(cmbDepartment.getValue()!=null?cmbDepartment.getValue().toString():"%")+"' "+
					" and vSectionId like '"+(cmbSection.getValue()!=null?cmbSection.getValue().toString():"%")+"' ";
			List <?> lst=session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				for(Iterator <?> itr=lst.iterator();itr.hasNext();)
				{
					Object [] element = (Object[]) itr.next();
					cmbEmployeeType.addItem(element[0]);
					cmbEmployeeType.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch (Exception exp)
		{
			showNotification("cmbEmployeeTypeDataAdd", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void formValidation()
	{
		if(dBonusDate.getValue()!=null)
		{
			if(cmbUnit.getValue()!=null/* || chkUnitAll.booleanValue()*/)
			{
				if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
				{
					if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
					{
						if(cmbEmployeeType.getValue()!=null || chkEmployeeTypeAll.booleanValue())
						{
							if(cmbBonusOccation.getValue()!=null)
							{
								generateAction();
							}
							else
							{
								showNotification("Warning!","Provide Bonus Occasion!!!",Notification.TYPE_WARNING_MESSAGE);
								cmbBonusOccation.focus();
							}
						}
						else
						{
							showNotification("Warning!","Provide Employee Type!!!",Notification.TYPE_WARNING_MESSAGE);
							cmbEmployeeType.focus();
						}
					}
					else
					{
						showNotification("Warning!","Select Section!!!",Notification.TYPE_WARNING_MESSAGE);
						cmbSection.focus();
					}
				}
				else
				{
					showNotification("Warning!","Select Department!!!",Notification.TYPE_WARNING_MESSAGE);
					cmbSection.focus();
				}
			}
			else
			{
				showNotification("Warning!","Select Project!!!",Notification.TYPE_WARNING_MESSAGE);
				cmbUnit.focus();
			}
		}
		else
		{
			showNotification("Warning!","Select Bonus Date!!!",Notification.TYPE_WARNING_MESSAGE);
			dBonusDate.focus();
		}
	}

	private boolean bonusChk()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String bonusQuery = " select * from tbEmployeeBonus where MONTH(dBonusDate)= MONTH('"+dateFormat.format(dBonusDate.getValue())+"') " +
					"AND YEAR(dBonusDate)= YEAR('"+dateFormat.format(dBonusDate.getValue())+"') and vUnitId='"+cmbUnit.getValue()+"' " +
					"AND vSectionId like '"+(cmbSection.getValue()!=null?cmbSection.getValue().toString():"%")+"' " +
					"and vOccasion='"+cmbBonusOccation.getValue()+"' ";
			List <?> bonusList = session.createSQLQuery(bonusQuery).list();
			if(!bonusList.isEmpty())
				return true;
		}
		catch (Exception exp)
		{
			showNotification("Warning!",exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
		return false;
	}

	private void generateAction()
	{
		if(!bonusChk())
		{
			MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to generate bonus?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						generateBonus();
						worker1 = new Worker1();
						worker1.start();
						PI.setEnabled(true);
						PI.setValue(0f);
						btnGenarate.setEnabled(false);
						componentIni(false);
						
					}
				}
			});
		}
		else
		{
			showNotification("Warning!","Bonus already generate for month "+dCharMonth.format(dBonusDate.getValue())+"-"+dYear.format(dBonusDate.getValue())+" in this Section ", Notification.TYPE_WARNING_MESSAGE);
			txtClear();
			componentIni(true);
		}
	}

	private void generateBonus()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{
			String query = "exec prcEmployeeBonus '"+dateFormat.format(dBonusDate.getValue())+"'," +
					"'"+(cmbUnit.getValue()!=null?cmbUnit.getValue().toString():"%")+"'," +
					"'"+(cmbSection.getValue()!=null?cmbSection.getValue().toString():"%")+"'," +
					"'"+(cmbEmployeeType.getValue()!=null?cmbEmployeeType.getValue().toString():"%")+"'," +
					"'"+cmbBonusOccation.getValue().toString()+"','"+sessionBean.getUserName()+"'," +
					"'"+sessionBean.getUserIp()+"'";
			session.createSQLQuery(query).executeUpdate();
			tx.commit();
		}
		catch(Exception ex)
		{
			tx.rollback();
			showNotification("generateBonus", ex.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally
		{
			session.close();
		}
	}

	private void cmbUnitData() 
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql="select distinct vUnitId,vUnitName from tbEmpOfficialPersonalInfo order by vUnitName ";
			List <?> list=session.createSQLQuery(sql).list();

			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbUnit.addItem(element[0]);
				cmbUnit.setItemCaption(element[0], (String) element[1]);
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbUnitData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void cmbDepartmentData() 
	{
		cmbDepartment.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql="select distinct vDepartmentId,vDepartmentName from tbEmpOfficialPersonalInfo where vUnitId='"+(cmbUnit.getValue()==null?"%":cmbUnit.getValue())+"' order by vDepartmentName ";
			System.out.println("cmbDepartmentData: "+sql);
			List <?> list=session.createSQLQuery(sql).list();

			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbDepartment.addItem(element[0]);
				cmbDepartment.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Department",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void cmbSectionData() 
	{
		cmbSection.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql="select distinct vSectionId,vSectionName from tbEmpOfficialPersonalInfo where vUnitId='"+(cmbUnit.getValue()==null?"%":cmbUnit.getValue())+"' and vDepartmentId like '"+(cmbDepartment.getValue()==null?"%":cmbDepartment.getValue())+"' order by vSectionName ";
			System.out.println("cmbSectionData: "+sql);
			List <?> list=session.createSQLQuery(sql).list();

			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbSection.addItem(element[0]);
				cmbSection.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbSectionData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void focusEnter()
	{
		allComp.add(dBonusDate);
		allComp.add(cmbUnit);
		allComp.add(cmbSection);
		allComp.add(cmbEmployeeType);
		allComp.add(cmbBonusOccation);
		allComp.add(btnGenarate);
		new FocusMoveByEnter(this,allComp);
	}

	private void txtClear()
	{
		cmbUnit.setValue(null);
		//chkUnitAll.setValue(false);
		cmbSection.setValue(null);
		chkSectionAll.setValue(false);
		chkDepartmentAll.setValue(false);
		cmbDepartment.setValue(null);
		cmbEmployeeType.setValue(null);
		chkEmployeeTypeAll.setValue(false);
		cmbBonusOccation.setValue(null);
	}

	private void componentIni(boolean b) 
	{
		dBonusDate.setEnabled(b);
		cmbUnit.setEnabled(b);
		chkSectionAll.setEnabled(b);
		cmbSection.setEnabled(b);
		chkDepartmentAll.setEnabled(b);
		cmbDepartment.setEnabled(b);
		chkSectionAll.setEnabled(b);
		cmbEmployeeType.setEnabled(b);
		chkEmployeeTypeAll.setEnabled(b);
		cmbBonusOccation.setEnabled(b);
	}

	private AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("480px");
		setHeight("300px");

		// lblBonusDate
		lblBonusDate = new Label("Date :");
		lblBonusDate.setImmediate(false);
		lblBonusDate.setWidth("-1px");
		lblBonusDate.setHeight("-1px");
		mainLayout.addComponent(lblBonusDate, "top:20.0px;left:20.0px;");

		// dBonusDate
		dBonusDate = new PopupDateField();
		dBonusDate.setImmediate(true);
		dBonusDate.setWidth("-1px");
		dBonusDate.setHeight("-1px");
		dBonusDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dBonusDate.setDateFormat("dd-MM-yyyy");
		dBonusDate.setValue(new java.util.Date());
		mainLayout.addComponent(dBonusDate, "top:18.0px;left:130.0px;");

		// cmbSection
		cmbUnit = new ComboBox();
		cmbUnit.setInvalidAllowed(false);
		cmbUnit.setWidth("280px");
		cmbUnit.setHeight("-1px");
		cmbUnit.setImmediate(true);
		mainLayout.addComponent(new Label("Project : "), "top:50.0px; left:20.0px;");
		mainLayout.addComponent(cmbUnit, "top:48.0px;left:130.0px;");

		// chkSectionAll
		/*chkUnitAll = new CheckBox("All");
		chkUnitAll.setImmediate(true);
		chkUnitAll.setWidth("-1px");
		chkUnitAll.setHeight("-1px");
		mainLayout.addComponent(chkUnitAll, "top:50.0px;left:410.0px;");*/
		
		mainLayout.addComponent(new Label("Department :"), "top:80.0px;left:20.0px;");

		// cmbSection
		cmbDepartment= new ComboBox();
		cmbDepartment.setInvalidAllowed(false);
		cmbDepartment.setWidth("280px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setImmediate(true);
		mainLayout.addComponent(cmbDepartment, "top:78.0px;left:130.0px;");

		// chkSectionAll
		chkDepartmentAll= new CheckBox("All");
		chkDepartmentAll.setImmediate(true);
		chkDepartmentAll.setWidth("-1px");
		chkDepartmentAll.setHeight("-1px");
		mainLayout.addComponent(chkDepartmentAll, "top:80.0px;left:410.0px;");

		// lblSection
		lblSection = new Label("Section :");
		lblSection.setImmediate(false);
		lblSection.setWidth("-1px");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection, "top:110px;left:20.0px;");

		// cmbSection
		cmbSection = new ComboBox();
		cmbSection.setInvalidAllowed(false);
		cmbSection.setWidth("280px");
		cmbSection.setHeight("-1px");
		cmbSection.setImmediate(true);
		mainLayout.addComponent(cmbSection, "top:108px;left:130.0px;");

		// chkSectionAll
		chkSectionAll = new CheckBox("All");
		chkSectionAll.setImmediate(true);
		chkSectionAll.setWidth("-1px");
		chkSectionAll.setHeight("-1px");
		mainLayout.addComponent(chkSectionAll, "top:110px;left:410.0px;");

		cmbEmployeeType= new ComboBox();
		cmbEmployeeType.setInvalidAllowed(false);
		cmbEmployeeType.setWidth("200px");
		cmbEmployeeType.setHeight("-1px");
		cmbEmployeeType.setImmediate(true);
		mainLayout.addComponent(new Label("Employee Type : "), "top:140px; left:20.0px;");
		mainLayout.addComponent(cmbEmployeeType, "top:138px;left:130.0px;");

		// chkSectionAll
		chkEmployeeTypeAll = new CheckBox("All");
		chkEmployeeTypeAll.setImmediate(true);
		chkEmployeeTypeAll.setWidth("-1px");
		chkEmployeeTypeAll.setHeight("-1px");
		mainLayout.addComponent(chkEmployeeTypeAll, "top:140.0px;left:330.0px;");

		// lblBonusOccation
		lblBonusOccation = new Label("Occasion :");
		lblBonusOccation.setImmediate(false);
		lblBonusOccation.setWidth("-1px");
		lblBonusOccation.setHeight("-1px");
		mainLayout.addComponent(lblBonusOccation, "top:170px;left:20.0px;");

		// cmbBonusOccation
		cmbBonusOccation = new ComboBox();
		cmbBonusOccation.setWidth("240px");
		cmbBonusOccation.setImmediate(true);
		cmbBonusOccation.setHeight("25px");
		cmbBonusOccation.addItem("Eid-Ul-Fitr");
		cmbBonusOccation.addItem("Eid-Ul-Azaha");
		mainLayout.addComponent(cmbBonusOccation, "top:168px;left:130.0px;");

		// btnGenarate
		btnGenarate=new NativeButton("Generate");
		btnGenarate.setImmediate(true);
		btnGenarate.setWidth("100px");
		btnGenarate.setHeight("32px");
		btnGenarate.setIcon(new ThemeResource("../icons/generate.png"));
		mainLayout.addComponent(btnGenarate,"top:200px;left:130.0px;");
		btnGenarate.setStyleName("nButton");

		// PI
		PI=new ProgressIndicator();
		PI.setWidth("130px");
		PI.setHeight("20px");
		PI.setImmediate(true);
		PI.setEnabled(false);
		mainLayout.addComponent(PI,"top:209px;left:240.0px;");
		return mainLayout;
	}

	public class Worker1 extends Thread 
	{
		int current = 1;
		public final static int MAX = 10;
		public void run() 
		{
			for (; current <= MAX; current++) 
			{
				try
				{
					Thread.sleep(1000);
				}
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
				synchronized (getApplication()) 
				{
					prosessed();
				}
			}
			showNotification("Bonus Genarate Successfully");
			txtClear();
			componentIni(true);
		}
		public int getCurrent() 
		{
			return current;
		}
	}
	public void prosessed() 
	{
		int i = worker1.getCurrent();
		if (i == Worker1.MAX)
		{
			PI.setEnabled(false);
			btnGenarate.setEnabled(true);
			PI.setValue(1f);
		}
		else
		{
			PI.setValue((float) i / Worker1.MAX);
		}
	}
}
