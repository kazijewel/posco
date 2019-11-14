package com.appform.hrmModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Transaction;
import org.hibernate.classic.Session;

import com.common.share.AmountCommaSeperator;
import com.common.share.AmountField;
import com.common.share.CommaSeparator;
import com.common.share.CommaSeparatorRound;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

@SuppressWarnings("serial")
public class PayScaleInformation extends Window
{
	private AbsoluteLayout mainLayout;
	private SessionBean sessionbean;

	private CommonButton cButton=new CommonButton("New", "Save", "Edit", "", "Refresh", "", "", "", "", "Exit");

	private Label lblCommon;

	private ListSelect lstFind;

	private PopupDateField dStartYear;
	private TextRead txtGradeId;
	private TextField txtGradeName;
	private AmountField txtGradeSerial;

	private static final String[] status = new String[] { "Inactive", "Active" };
	private NativeSelect cmbIsActive;

	private TextArea txtGradeDescription;

	private Table table = new Table();
	private ArrayList<Label> tbLblSl = new ArrayList<Label>();
	private ArrayList<Label> tbLblYear = new ArrayList<Label>();
	private ArrayList<Label> tbLblYearSl = new ArrayList<Label>();
	private ArrayList<AmountCommaSeperator> tbtxtYearlyAmount = new ArrayList<AmountCommaSeperator>();

	boolean isUpdate = false;
	boolean isFind = false;

	private SimpleDateFormat dDfBd = new SimpleDateFormat("yyyy-MM-dd");

	private ArrayList<Component> allComp=new ArrayList<Component>();

	private String GradeDescription = "";

	private String findGrade = "";

	public PayScaleInformation(SessionBean sessionBean)
	{
		this.sessionbean = sessionBean;
		this.setCaption("PAY SCALE ENTRY :: "+sessionbean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);

		setEventAction();
		focusMove();

		componentIni(true);
		btnIni(true);

		lstFindAddValue();
		dStartYear.focus();
	}

	private void componentIni(boolean b)
	{
		lstFind.setEnabled(b);
		dStartYear.setEnabled(!b);
		table.setEnabled(!b);
		txtGradeDescription.setEnabled(!b);

		txtGradeId.setEnabled(!b);
		txtGradeSerial.setEnabled(!b);
		txtGradeName.setEnabled(!b);
		cmbIsActive.setEnabled(!b);
	}

	private void btnIni(boolean b)
	{
		cButton.btnNew.setEnabled(b);
		cButton.btnEdit.setEnabled(b);
		cButton.btnSave.setEnabled(!b);
		cButton.btnRefresh.setEnabled(!b);	
	}

	private void setEventAction()
	{
		cButton.btnNew.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				txtClear();
				componentIni(false);
				btnIni(false);
				isUpdate=false;

				txtGradeId.setValue(maxSerialId());
				lstFind.setValue(null);
			}
		});

		cButton.btnEdit.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(!txtGradeId.getValue().toString().isEmpty())
				{
					componentIni(false);
					btnIni(false);
					isUpdate=true;
				}
				else
				{
					showNotification("Warning!","There are no data to update.",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		cButton.btnSave.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				validationCheck();
			}
		});

		cButton.btnRefresh.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				txtClear();
				componentIni(true);
				btnIni(true);
				isUpdate=false;
				lstFind.setValue(null);
			}
		});

		cButton.btnExit.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				close();
			}
		});

		lstFind.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(lstFind.getValue()!=null)
				{
					isFind=true;
					findInitialize();
				}
			}
		});

		txtGradeName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtGradeName.getValue().toString().isEmpty())
				{
					if(duplicateGradeName() && !isFind)
					{
						showNotification("Warning!","Grade Name already exist.",Notification.TYPE_WARNING_MESSAGE);
						txtGradeName.setValue("");
						txtGradeName.focus();
					}
				}
			}
		});

		txtGradeSerial.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtGradeSerial.getValue().toString().isEmpty())
				{
					if(duplicateGradeSerial() && !isFind)
					{
						showNotification("Warning!","Grade Serial already exist.",Notification.TYPE_WARNING_MESSAGE);
						txtGradeSerial.setValue("");
						txtGradeSerial.focus();
					}
				}
			}
		});
	}

	private void findInitialize()
	{
		txtClear();

		findGrade = lstFind.getValue().toString();

		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();

		try
		{
			String sql = "select dStartFrom,psi.vGradeId,vGradeName,iGradeSerial,isActive,mIncreaseBasic" +
					" from tbPayScaleInfo psi inner join tbPayScaleDetails psd on psi.vGradeId=psd.vGradeId " +
					" where psi.vGradeId = '"+findGrade+"' ";

			List <?> findLst=session.createSQLQuery(sql).list();

			if(!findLst.isEmpty())
			{
				int i=0;
				for(Iterator <?> itr=findLst.iterator();itr.hasNext();)
				{
					Object [] element = (Object[])itr.next();

					if(i==0)
					{
						dStartYear.setValue(element[0]);
						txtGradeId.setValue(element[1].toString());
						txtGradeName.setValue(element[2].toString());
						txtGradeSerial.setValue(element[3].toString());

						cmbIsActive.setValue(element[4]);
					}

					tbtxtYearlyAmount.get(i).setValue(new CommaSeparator().setComma(Double.parseDouble(element[5].toString())));

					if(i==tbtxtYearlyAmount.size()-1)
					{
						tableRowAdd(i);
					}

					i++;
				}
			}

			isFind = false;
		}
		catch(Exception exp)
		{
			showNotification("findInitialize", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void validationCheck()
	{
		if(dStartYear.getValue()!=null)
		{
			if(!txtGradeId.getValue().toString().isEmpty())
			{
				if(!txtGradeName.getValue().toString().isEmpty())
				{
					if(!txtGradeSerial.getValue().toString().isEmpty())
					{
						if(chkTableData())
						{
							saveButtonEvent();
						}
						else
						{
							showNotification("Warning!", "Provide proposed basic in the table.", Notification.TYPE_WARNING_MESSAGE);
							tbtxtYearlyAmount.get(0).focus();
						}
					}
					else
					{
						showNotification("Warning!", "Provide grade serial.", Notification.TYPE_WARNING_MESSAGE);
						txtGradeSerial.focus();
					}
				}
				else
				{
					showNotification("Warning!", "Provide grade name.", Notification.TYPE_WARNING_MESSAGE);
					txtGradeName.focus();
				}
			}
			else
			{
				showNotification("Warning!", "Provide grade id.", Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else
		{
			showNotification("Warning!", "Select start from date.", Notification.TYPE_WARNING_MESSAGE);
			dStartYear.focus();
		}
	}

	private void saveButtonEvent()
	{
		if(!isUpdate)
		{
			MessageBox msgBox=new MessageBox(getParent(),"Are You Sure?",MessageBox.Icon.QUESTION,"Do you want to save information?",new MessageBox.ButtonConfig(MessageBox.ButtonType.YES,"Yes"),new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			msgBox.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType==ButtonType.YES)
					{
						Session session=SessionFactoryUtil.getInstance().openSession();
						Transaction tx=session.beginTransaction();
						try
						{
							insertData(session);
							isUpdate = false;
							txtClear();
							componentIni(true);
							btnIni(true);

							tx.commit();
							showNotification("All Information Saved Successfully.");
							lstFindAddValue();
						}
						catch(Exception exp)
						{
							tx.rollback();
							showNotification("deleteData", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
						}
						finally{session.close();}
					}
				}
			});
		}
		else
		{
			MessageBox msgBox=new MessageBox(getParent(),"Are You Sure?",MessageBox.Icon.QUESTION,"Do you want to update information?",new MessageBox.ButtonConfig(MessageBox.ButtonType.YES,"Yes"),new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			msgBox.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType==ButtonType.YES)
					{
						Session session = SessionFactoryUtil.getInstance().openSession();
						Transaction tx=session.beginTransaction();
						try
						{
							if(deleteData(session))
							{
								insertData(session);
							}

							isUpdate = false;
							txtClear();
							componentIni(true);
							btnIni(true);
							tx.commit();

							showNotification("All Information Updated Successfully.");
							lstFindAddValue();
						}
						catch(Exception exp)
						{
							tx.rollback();
							showNotification("deleteData", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
						}
						finally{session.close();}
					}
				}
			});
		}
	}

	private boolean deleteData(Session session)
	{
		boolean ret = false;
		try
		{
			//Check existing for of update
			String query = " Select * from tbUdPayScaleInfo where vGradeId = '"+findGrade+"'" +
					" and vUdFlag = 'New' ";
			Iterator <?> iter = session.createSQLQuery(query).list().iterator();

			if(!iter.hasNext())
			{
				//Insert new value for first time update
				String Insert = " INSERT into tbUdPayScaleInfo(dStartFrom,vGradeId,vGradeName,iGradeSerial," +
						" isActive,vUserName,vUserIp,dEntryTime,vUdFlag)" +
						" select dStartFrom,vGradeId,vGradeName,iGradeSerial," +
						" isActive,vUserName,vUserIp,dEntryTime,'New' from tbPayScaleInfo" +
						" where vGradeId = '"+findGrade+"' ";
				session.createSQLQuery(Insert).executeUpdate();

				String sqlDetails = "insert into tbUdPayScaleDetails(vGradeId,iYear,vYearSerial," +
						" mIncreaseBasic,vUdFlag) " +
						" select vGradeId,iYear,vYearSerial," +
						" mIncreaseBasic,'New' from tbPayScaleDetails" +
						" where vGradeId = '"+findGrade+"' ";
				session.createSQLQuery(sqlDetails).executeUpdate();
			}

			String deleteInfo = "delete from tbPayScaleInfo where vGradeId='"+findGrade+"'";
			session.createSQLQuery(deleteInfo).executeUpdate();

			String deleteDetails = "delete from tbPayScaleDetails where vGradeId='"+findGrade+"'";
			session.createSQLQuery(deleteDetails).executeUpdate();

			String sqlInfo = "insert into tbUdPayScaleInfo(dStartFrom,vGradeId,vGradeName,iGradeSerial," +
					" isActive,vUserName,vUserIp,dEntryTime,vUdFlag) values (" +
					" '"+dDfBd.format(dStartYear.getValue())+"'," +
					" '"+findGrade+"'," +
					" '"+txtGradeName.getValue().toString().trim()+"'," +
					" '"+txtGradeSerial.getValue().toString().trim()+"'," +
					" '"+cmbIsActive.getValue().toString()+"'," +
					" '"+sessionbean.getUserName()+"','"+sessionbean.getUserIp()+"'," +
					" CURRENT_TIMESTAMP,'Update') ";

			session.createSQLQuery(sqlInfo).executeUpdate();

			for(int i=0; i<tbtxtYearlyAmount.size(); i++)
			{
				if(!tbLblYearSl.get(i).getValue().toString().trim().isEmpty())
				{
					String sqlDetails = "insert into tbUdPayScaleDetails(vGradeId,iYear,vYearSerial," +
							" mIncreaseBasic,vUdFlag) values (" +
							" '"+findGrade+"'," +
							" '"+tbLblYearSl.get(i).getValue().toString()+"'," +
							" '"+tbLblYear.get(i).getValue().toString()+"'," +
							" '"+tbtxtYearlyAmount.get(i).getValue().toString().trim().replaceAll(",", "")+"'," +
							" 'Update')";

					session.createSQLQuery(sqlDetails).executeUpdate();
				}
			}

			ret=true;
		}
		catch(Exception exp)
		{
			showNotification("deleteData", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		return ret;
	}

	private boolean chkTableData()
	{
		boolean ret = false;

		for(int i=0; i<tbtxtYearlyAmount.size(); i++)
		{
			if(Double.parseDouble("0"+tbtxtYearlyAmount.get(i).getValue().toString().replaceAll(",",""))>0)
			{
				ret = true;
				break;
			}
		}

		return ret;
	}

	private void insertData(Session session)
	{
		String gradeId = "";

		if(!isUpdate)
		{
			gradeId = maxSerialId();
		}
		else
		{
			gradeId = findGrade;
		}

		try
		{
			String sqlInfo = "insert into tbPayScaleInfo(dStartFrom,vGradeId,vGradeName,iGradeSerial," +
					" isActive,vUserName,vUserIp,dEntryTime) values (" +
					" '"+dDfBd.format(dStartYear.getValue())+"'," +
					" '"+gradeId+"'," +
					" '"+txtGradeName.getValue().toString().trim()+"'," +
					" '"+txtGradeSerial.getValue().toString().trim()+"'," +
					" '"+cmbIsActive.getValue().toString()+"'," +
					" '"+sessionbean.getUserName()+"','"+sessionbean.getUserIp()+"',CURRENT_TIMESTAMP) ";

			session.createSQLQuery(sqlInfo).executeUpdate();

			for(int i=0; i<tbtxtYearlyAmount.size(); i++)
			{
				if(!tbLblYearSl.get(i).getValue().toString().trim().isEmpty())
				{
					String sqlDetails = "insert into tbPayScaleDetails(vGradeId,iYear,vYearSerial,mIncreaseBasic) values (" +
							" '"+gradeId+"'," +
							" '"+tbLblYearSl.get(i).getValue().toString()+"'," +
							" '"+tbLblYear.get(i).getValue().toString()+"'," +
							" '"+tbtxtYearlyAmount.get(i).getValue().toString().trim().replaceAll(",", "")+"')";

					session.createSQLQuery(sqlDetails).executeUpdate();
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("insertData", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void focusMove()
	{
		allComp.add(dStartYear);
		allComp.add(txtGradeName);
		allComp.add(txtGradeSerial);
		allComp.add(cmbIsActive);

		allComp.add(tbtxtYearlyAmount.get(0));

		new FocusMoveByEnter(this, allComp);
	}

	private void lstFindAddValue()
	{
		lstFind.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "select vGradeId,vGradeName,dStartFrom from tbPayScaleInfo order by iGradeSerial";

			List <?> lst = session.createSQLQuery(sql).list();

			if(!lst.isEmpty())
			{
				for(Iterator <?> itr=lst.iterator(); itr.hasNext();)
				{
					Object[] element = (Object[])itr.next();

					lstFind.addItem(element[0]);
					lstFind.setItemCaption(element[0], element[1].toString()+" > "+dDfBd.format(element[2]).toString());
				}
			}
			lstFind.setValue(null);
		}
		catch(Exception exp)
		{
			showNotification("lstFindAddValue", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private boolean duplicateGradeName()
	{
		boolean ret = false;
		if(!isUpdate)
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String query = "select * from tbPayScaleInfo where vGradeName" +
						" like '"+txtGradeName.getValue().toString()+"'";

				Iterator <?> iter = session.createSQLQuery(query).list().iterator();

				if (iter.hasNext()) 
				{
					ret = true;
				}
			}
			catch (Exception ex) 
			{
				System.out.print(ex);
			}
			finally{session.close();}
		}
		return ret;
	}

	private boolean duplicateGradeSerial()
	{
		boolean ret = false;
		if(!isUpdate)
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String query = "select * from tbPayScaleInfo where iGradeSerial" +
						" like '"+txtGradeSerial.getValue().toString()+"'";

				Iterator <?> iter = session.createSQLQuery(query).list().iterator();

				if (iter.hasNext()) 
				{
					ret = true;
				}
			}
			catch (Exception ex) 
			{
				System.out.print(ex);
			}
			finally{session.close();}
		}
		return ret;
	}

	private String maxSerialId()
	{
		String serialId = "";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try 
		{
			String query = "Select isnull(max(cast(SUBSTRING(vGradeId,4,10) as int)),0)+1 from tbPayScaleInfo";

			Iterator <?> iter = session.createSQLQuery(query).list().iterator();

			if (iter.hasNext())
			{
				String serial = iter.next().toString();

				serialId = "SCA"+serial;
				txtGradeSerial.setValue(serial);
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		finally{session.close();}
		return serialId;
	}

	private void txtClear()
	{
		findGrade = "";

		txtGradeId.setValue("");
		txtGradeSerial.setValue("");
		txtGradeName.setValue("");
		cmbIsActive.setValue(1);

		txtGradeDescription.setReadOnly(false);
		txtGradeDescription.setValue("");
		txtGradeDescription.setReadOnly(true);

		tableClear();
	}

	private void tableClear()
	{
		for(int i=0; i<tbtxtYearlyAmount.size(); i++)
		{
			tbtxtYearlyAmount.get(i).setValue("");
		}
	}

	private void tableInitialize()
	{
		for(int i=0; i<30; i++)
		{
			tableRowAdd(i);
		}
	}

	private void tableRowAdd(final int ar)
	{
		tbLblSl.add(ar, new Label());
		tbLblSl.get(ar).setImmediate(true);
		tbLblSl.get(ar).setWidth("100%");
		tbLblSl.get(ar).setHeight("18px");
		tbLblSl.get(ar).setValue(ar+1);

		tbLblYearSl.add(ar, new Label());
		tbLblYearSl.get(ar).setImmediate(true);
		tbLblYearSl.get(ar).setWidth("100%");

		tbLblYear.add(ar, new Label());
		tbLblYear.get(ar).setImmediate(true);
		tbLblYear.get(ar).setWidth("100%");
		if(ar == 0)
		{
			tbLblYear.get(ar).setValue("1st Year");
		}
		else if(ar == 1)
		{
			tbLblYear.get(ar).setValue("2nd Year");
		}
		else if(ar == 2)
		{
			tbLblYear.get(ar).setValue("3rd Year");
		}
		else if(ar >= 3)
		{
			tbLblYear.get(ar).setValue((ar+1)+"th Year");
		}

		tbtxtYearlyAmount.add(ar, new AmountCommaSeperator());
		tbtxtYearlyAmount.get(ar).setImmediate(true);
		tbtxtYearlyAmount.get(ar).setWidth("100%");
		tbtxtYearlyAmount.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				setDescription();

				if(ar==tbtxtYearlyAmount.size()-1)
				{
					tableRowAdd(ar+1);
				}

				tbtxtYearlyAmount.get(ar+1).focus();
			}
		});

		table.addItem(new Object[]{tbLblSl.get(ar),tbLblYearSl.get(ar),
				tbLblYear.get(ar),tbtxtYearlyAmount.get(ar)}, ar);
	}

	private void setDescription()
	{
		GradeDescription = "";

		for(int i=0;i<tbtxtYearlyAmount.size();i++)
		{
			if(!tbtxtYearlyAmount.get(i).getValue().toString().trim().isEmpty())
			{
				GradeDescription += new CommaSeparatorRound().setComma(Double.parseDouble("0"+tbtxtYearlyAmount.get(i).getValue().toString()))+" - ";

				tbLblYearSl.get(i).setValue(tbLblSl.get(i).getValue());
			}
			else
			{
				tbLblYearSl.get(i).setValue("");
			}
		}

		txtGradeDescription.setReadOnly(false);
		txtGradeDescription.setValue(GradeDescription.substring(0, (GradeDescription.length()>0?GradeDescription.length():3)-3));
		txtGradeDescription.setReadOnly(true);
	}

	private AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setWidth("510px");
		mainLayout.setHeight("540px");

		lstFind=new ListSelect();
		lstFind.setCaption("Select to find scale : ");
		lstFind.setImmediate(true);
		lstFind.setNullSelectionAllowed(false);
		lstFind.setNewItemsAllowed(false);
		lstFind.setWidth("170.0px");
		lstFind.setHeight("340.0px");
		mainLayout.addComponent(lstFind, "top:40.0px;left:18.0px;");

		lblCommon = new Label("Start From : ");
		lblCommon.setImmediate(true);
		lblCommon.setHeight("-1px");
		lblCommon.setWidth("-1px");
		mainLayout.addComponent(lblCommon,"top:20.0px; left:210.0px;");

		dStartYear=new PopupDateField();
		dStartYear.setWidth("110px");
		dStartYear.setImmediate(true);
		dStartYear.setResolution(PopupDateField.RESOLUTION_DAY);
		dStartYear.setDateFormat("dd-MM-yyyy");
		dStartYear.setValue(new Date());
		mainLayout.addComponent(dStartYear, "top:18.0px; left:290.0px;");

		lblCommon = new Label("Scale Id : ");
		mainLayout.addComponent(lblCommon,"top:50.0px; left:210.0px;");

		txtGradeId = new TextRead();
		txtGradeId.setImmediate(true);
		txtGradeId.setWidth("60px");
		txtGradeId.setHeight("23px");
		mainLayout.addComponent(txtGradeId, "top:48.0px;left:290.0px;");

		lblCommon = new Label("Scale Name : ");
		mainLayout.addComponent(lblCommon,"top:80.0px; left:210.0px;");

		txtGradeName = new TextField();
		txtGradeName.setImmediate(true);
		txtGradeName.setWidth("200px");
		txtGradeName.setHeight("-1px");
		mainLayout.addComponent(txtGradeName, "top:78.0px; left:290.0px;");

		lblCommon = new Label("Scale Serial : ");
		mainLayout.addComponent(lblCommon,"top:110.0px; left:210.0px;");

		txtGradeSerial = new AmountField();
		txtGradeSerial.setImmediate(true);
		txtGradeSerial.setWidth("60px");
		txtGradeSerial.setHeight("24px");
		mainLayout.addComponent(txtGradeSerial, "top:108.0px;left:290.0px;");

		lblCommon = new Label("Status : ");
		mainLayout.addComponent(lblCommon,"top:110.0px; left:370.0px;");

		cmbIsActive = new NativeSelect();
		cmbIsActive.setNullSelectionAllowed(false);
		cmbIsActive.setImmediate(true);
		cmbIsActive.setWidth("60px");
		cmbIsActive.setHeight("-1px");
		for (int i = 0; i < status.length; i++)
		{
			cmbIsActive.addItem(i);
			cmbIsActive.setItemCaption(i, status[i]);
		}
		cmbIsActive.setValue(1);
		mainLayout.addComponent(cmbIsActive,"top:108.0px; left:430.0px;");

		table.setHeight("240px");
		table.setWidth("278px");

		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("SL#", Label.class, new Label());
		table.setColumnWidth("SL#", 20);

		table.addContainerProperty("YEAR SL", Label.class, new Label());
		table.setColumnWidth("YEAR SL", 20);

		table.addContainerProperty("Year", Label.class, new Label());
		table.setColumnWidth("Year", 80);

		table.addContainerProperty("Proposed Basic", AmountCommaSeperator.class, new AmountCommaSeperator());
		table.setColumnWidth("Proposed Basic", 100);

		table.setColumnAlignments(new String[]{Table.ALIGN_RIGHT,Table.ALIGN_LEFT,
				Table.ALIGN_LEFT,Table.ALIGN_CENTER});

		table.setColumnCollapsed("YEAR SL", true);

		mainLayout.addComponent(table, "top:140.0px; left:210.0px;");

		tableInitialize();

		lblCommon = new Label("Grade Description : ");
		mainLayout.addComponent(lblCommon,"top:390.0px; left:30.0px;");

		txtGradeDescription=new TextArea();
		txtGradeDescription.setReadOnly(true);
		txtGradeDescription.setImmediate(true);
		txtGradeDescription.setWidth("470.0px");
		txtGradeDescription.setHeight("75.0px");
		txtGradeDescription.setStyleName("fontStyle");
		mainLayout.addComponent(txtGradeDescription, "top:410.0px; left:18.0px;");

		mainLayout.addComponent(cButton, "top:500.0px; left:40.0px;");

		return mainLayout;
	}
}
