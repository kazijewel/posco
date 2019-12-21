package com.appform.hrmModule;

import java.awt.Button;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.AmountCommaSeperator;
import com.common.share.CommonButton;
import com.common.share.MessageBox;
import com.common.share.MessageBox.EventListener;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.MessageBox.ButtonType;
import com.common.share.TextRead;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Button.ClickEvent;

public class addExtraOrLessOTHour extends Window
{
	SessionBean sessionbean;
	private AbsoluteLayout mainlayout;
	private ComboBox cmbSection;
	private ComboBox cmbShift;
	private OptionGroup opgEmployee;
	private static final List<String> Optiontype=Arrays.asList(new String[]{"Employee ID","Finger ID","Employee Name"});
	private ComboBox cmbEmployee;
	private CheckBox chkAllEmp;
	private PopupDateField date;
	//private CheckBox chkFriAll;

	private TextRead txtSectionId=new TextRead();
	private TextRead txtFindDate=new TextRead();

	private Table table=new Table();
	private ArrayList<NativeButton> btnDel=new ArrayList<NativeButton>();
	private ArrayList<Label> lblSl=new ArrayList<Label>();
	private ArrayList<Label> lblAutoEmployeeID=new ArrayList<Label>();
	private ArrayList<Label> lblEmployeeID=new ArrayList<Label>();
	private ArrayList<Label> lblProximityID=new ArrayList<Label>();
	private ArrayList<Label> lblEmployeeName=new ArrayList<Label>();
	private ArrayList<Label> lblDesignationID=new ArrayList<Label>();
	private ArrayList<Label> lblDesignation=new ArrayList<Label>();
	private ArrayList<Label> lblSectionID=new ArrayList<Label>();
	private ArrayList<Label> lblSectionName=new ArrayList<Label>();
	private ArrayList<TextField> txtPermittedBy=new ArrayList<TextField>();
	private ArrayList<TextField> txtReason=new ArrayList<TextField>();
	private ArrayList<AmountCommaSeperator> txtExtraOT=new ArrayList<AmountCommaSeperator>();
	//private ArrayList<AmountCommaSeperator> txtLessOT=new ArrayList<AmountCommaSeperator>();
	//private ArrayList<CheckBox> chkFriday=new ArrayList<CheckBox>();

	private DecimalFormat intFormat=new DecimalFormat("#,##0");

	private SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
	private CommonButton cButton=new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "", "", "Exit");

	boolean isSave=false;
	boolean isUpdate=false;
	boolean isRefresh=false;
	boolean isFind=false;
	int index=0;
	String Noti="";

	public addExtraOrLessOTHour(SessionBean sessionBean)
	{

		this.sessionbean=sessionBean;
		this.setCaption("ADD EXTRA OT :: "+sessionbean.getCompany());
		buildMainLayout();
		this.setContent(mainlayout);
		this.setResizable(false);
		componentEnable(true);
		btnEnable(true);
		setEventAction();
		cmbSectionDataLoad();
		cmbShiftDataLoad();
		//addDataFindData();
	}

	private void cmbSectionDataLoad()
	{
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			String sql=" select Distinct vSectionId,vSectionName from tbEmployeeAttendance where MONTH(dAttDate)= " +
					" MONTH('"+dateFormat.format(date.getValue())+"') and YEAR(dAttDate)=" +
					" YEAR('"+dateFormat.format(date.getValue())+"') order by vSectionName ";

			List lst=session.createSQLQuery(sql).list();
			if(!lst.isEmpty())
			{

				Iterator itr=lst.iterator();
				while(itr.hasNext())
				{
					Object[] element=(Object[])itr.next();
					cmbSection.addItem(element[0]);
					cmbSection.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("CmbSectionDataLoad", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void cmbShiftDataLoad()
	{
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql= " select Distinct vShiftId,(select si.vShiftName from tbshiftInformation si where si.vShiftId=" +
					" ea.vShiftId) from tbEmployeeAttendance ea where vSectionid= '"+(cmbSection.getValue()!=null?cmbSection.getValue():"")+"' ";
			List lst=session.createSQLQuery(sql).list();
			if(!lst.isEmpty())
			{

				Iterator itr=lst.iterator();
				while(itr.hasNext())
				{
					Object[] element=(Object[])itr.next();
					cmbShift.addItem(element[0]);
					cmbShift.setItemCaption(element[0], element[1].toString());
				}

			}

		}
		catch(Exception exp)
		{
			showNotification("CmbSectionDataLoad", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void setEventAction()
	{

		date.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{

				chkAllEmp.setValue(false);
				cmbSectionDataLoad();
				//addDataFindData();

			}
		});

		cmbSection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbShiftDataLoad();
				cmbEmployee.setValue(null);
				opgEmployee.select(null);
				chkAllEmp.setValue(false);
				tableclear();

			}
		});

		opgEmployee.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbSection.getValue()!=null)
				{

					String sql="";
					if(opgEmployee.getValue()=="Employee ID")
					{

						sql= " select Distinct iFingerID,employeeCode from tbEmployeeInfo ei inner join tbEmployeeAttendance ea on ei.iFingerID=ea.vEmployeeId  where ea.vSectionId='"+cmbSection.getValue().toString()+"' and iStatus=1 and ea.OtStatus=1 and ISNULL(vProximityId,'')!='' and " +
								" MONTH(dAttDate)=MONTH('"+dateFormat.format(date.getValue())+"') and YEAR(dAttDate)=YEAR('"+dateFormat.format(date.getValue())+"')";
					}
					else if(opgEmployee.getValue()=="Finger ID")
					{

						sql= " select Distinct iFingerID,iFingerID from tbEmployeeInfo ei inner join tbEmployeeAttendance ea on ei.iFingerID=ea.vEmployeeId where ea.vSectionId='"+cmbSection.getValue().toString()+"' and iStatus=1 and ea.OtStatus=1 and ISNULL(vProximityId,'')!='' and " +
								" MONTH(dAttDate)=MONTH('"+dateFormat.format(date.getValue())+"') and YEAR(dAttDate)=YEAR('"+dateFormat.format(date.getValue())+"')";
					}
					else if(opgEmployee.getValue()=="Employee Name")
					{

						sql= " select Distinct iFingerID,ea.vEmployeeName from tbEmployeeInfo ei inner join tbEmployeeAttendance ea on ei.iFingerID=ea.vEmployeeId  where ea.vSectionId='"+cmbSection.getValue().toString()+"' and iStatus=1 and ea.OtStatus=1 and ISNULL(vProximityId,'')!='' and " +
								" MONTH(dAttDate)=MONTH('"+dateFormat.format(date.getValue())+"') and YEAR(dAttDate)=YEAR('"+dateFormat.format(date.getValue())+"')";
					}
					if(!sql.equals(""))
					{ 
						Transaction tx=null;
						try
						{
							Session session=SessionFactoryUtil.getInstance().getCurrentSession();
							tx=session.beginTransaction();
							List lst=session.createSQLQuery(sql).list();
							if(!lst.isEmpty())
							{

								Iterator itr=lst.iterator();
								while(itr.hasNext())
								{
									Object[] element=(Object[])itr.next();
									cmbEmployee.addItem(element[0]);
									cmbEmployee.setItemCaption(element[0], element[1].toString());
								}
							}
							else
								showNotification("Warning", "No Employee Found!!!", Notification.TYPE_WARNING_MESSAGE);
						}

						catch(Exception exp)
						{
							showNotification("OPGEmployee", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
						}
					}

				}

				else
				{
					if(!isSave && !isRefresh)
					{
						opgEmployee.select(null);
						showNotification("Warning", "Please Select Section Name!!!", Notification.TYPE_WARNING_MESSAGE);
					}
				}

			}
		});

		cmbEmployee.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{

				if(cmbEmployee.getValue()!=null)
					tableValueAdd(cmbEmployee.getValue().toString());

			}
		});

		chkAllEmp.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{

				if(cmbSection.getValue()!=null)
				{

					if(chkAllEmp.booleanValue())
					{

						opgEmployee.select(null);
						opgEmployee.setEnabled(false);
						cmbEmployee.removeAllItems();
						cmbEmployee.setEnabled(false);
						tableValueAdd("%");

					}
					else
					{
						opgEmployee.setEnabled(true);
						cmbEmployee.setEnabled(true);
					}

				}
				else
				{
					if(!isSave && !isRefresh)
					{
						chkAllEmp.setValue(false);
						showNotification("Warning", "Please Select Section Name!!!", Notification.TYPE_WARNING_MESSAGE);
					}
				}
			}
		});

		cButton.btnNew.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{

				isSave=false;
				isRefresh=false;
				txtclear();
				componentEnable(false);
				btnEnable(false);
				index=0;

			}
		});

		cButton.btnSave.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{

				if(chkTableData())
				{
					isSave=true;
					saveButtonEvent();
				}

				else
					showNotification("Warning", Noti, Notification.TYPE_WARNING_MESSAGE);

			}
		});

		cButton.btnEdit.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(chkTableData())
				{
					isUpdate=true;
					updateButtonEvent();
				}
				else
					showNotification("Warning", "Edit Failed!!!", Notification.TYPE_WARNING_MESSAGE);
			}
		});

		cButton.btnRefresh.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{

				isRefresh=true;
				txtclear();
				componentEnable(true);
				btnEnable(true);

			}
		});

		cButton.btnFind.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				isFind=true;
				//findButtonEvent();
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

	private void updateButtonEvent()
	{
		componentEnable(false);
		btnEnable(false);
	}

	/*	private void findButtonEvent() 
	{
		String as = "";
		Window win = new addExtraOrLessOTHourFind(sessionbean,txtSectionId,txtFindDate);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if (txtSectionId.getValue().toString().length() > 0)
				{
					txtclear();
					findInitialise(txtSectionId.getValue().toString(), txtFindDate.getValue());
				}
			}
		});

		this.getParent().addWindow(win);
	}*/

	private void findInitialise(String SectionId, Object findDate) 
	{		
		Transaction tx = null;
		String sql = "";
		System.out.println("ID: "+SectionId+" Date: "+findDate);
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();

		try 
		{
			System.out.println("K000");
			sql = "select dDate,vEmployeeID,vEmployeeCode,vProximityID,vEmployeeName,vSectionID,vSectionName,vDesignationID,vDesignationName," +
					"vPermittedBy,vReason,mExtraOTAdd,mLessOT from tbAddOrLessExtraOT  where Month(dDate) = Month('"+findDate+"') and YEAR(dDate)=YEAR('"+findDate+"')" +
					" and vSectionID='"+SectionId+"'";
			List list = session.createSQLQuery(sql).list();
			System.out.println(sql);
			isFind=true;

			int i=0;
			for(Iterator itr=list.iterator();itr.hasNext();)
			{
				Object[] element = (Object[]) itr.next();

				if(i==0)
				{
					date.setValue(element[0]);
					cmbSection.setValue(element[5]);
				}

				lblAutoEmployeeID.get(i).setValue(element[1]);
				lblEmployeeID.get(i).setValue(element[2]);
				lblProximityID.get(i).setValue(element[3]);
				lblEmployeeName.get(i).setValue(element[4]);


				lblSectionID.get(i).setValue(element[5]);
				lblSectionName.get(i).setValue(element[6]);
				lblDesignationID.get(i).setValue(element[7]);
				lblDesignation.get(i).setValue(element[8]);
				txtPermittedBy.get(i).setValue(element[9]);
				txtReason.get(i).setValue(element[10]);
				txtExtraOT.get(i).setValue(intFormat.format(element[11]));
				//txtLessOT.get(i).setValue(intFormat.format(element[12]));
				i++;
			}
		}
		catch (Exception exp)
		{
			this.getParent().showNotification("findInitialise", exp + "",Notification.TYPE_ERROR_MESSAGE);
		}
		isFind=false;
	}

	private boolean chkTableData()
	{
		boolean ret=false;
		for(int tbin=0;tbin<lblAutoEmployeeID.size();tbin++)
		{
			if(!lblAutoEmployeeID.get(tbin).getValue().toString().trim().isEmpty())
			{
				if(!txtPermittedBy.get(tbin).getValue().toString().trim().isEmpty())
				{
					ret=false;
					if(!txtReason.get(tbin).getValue().toString().trim().isEmpty())
					{
						if(!txtExtraOT.get(tbin).getValue().toString().trim().isEmpty() /*|| !txtLessOT.get(tbin).getValue().toString().trim().isEmpty()*/)
						{
							ret=true;
							break;
						}
						else
						{
							Noti="Provide Extra or Less OT!!!";
						}
					}
					else
					{
						txtReason.get(tbin).focus();
						Noti="Provide Reason!!!";
					}
				}
				else
				{
					txtPermittedBy.get(tbin).focus();
					Noti="Provide Permitted By!!!";
				}
			}
			else
			{
				Noti="No Data Found!!!";
			}
		}
		return ret;
	}

	private void saveButtonEvent()
	{
		if(isUpdate)
		{
			MessageBox msgbox=new MessageBox(getParent(), "Are You Sure?", MessageBox.Icon.QUESTION, "Do You Want to Update All Information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			msgbox.show(new EventListener()
			{

				
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType==ButtonType.YES)
					{
						Transaction tx=null;
						try
						{
							Session session=SessionFactoryUtil.getInstance().getCurrentSession();
							tx=session.beginTransaction();
							if(deleteData(session))
								insertdata(session);
							showNotification("All Information Updated Successfully");
							tx.commit();
							componentEnable(true);
							btnEnable(true);
							txtclear();
						}
						catch (Exception exp)
						{
							tx.rollback();
							showNotification("SaveButtonEvent", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
						}
					}

				}
			});
			isUpdate=false;
		}
		else
		{
			MessageBox msgbox=new MessageBox(getParent(), "Are You Sure?", MessageBox.Icon.QUESTION, "Do You Want to Save All Information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			msgbox.show(new EventListener()
			{

				
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType==ButtonType.YES)
					{
						Transaction tx=null;
						try
						{
							Session session=SessionFactoryUtil.getInstance().getCurrentSession();
							tx=session.beginTransaction();
							insertdata(session);
							showNotification("All Information Saved Successfully");
							tx.commit();
							componentEnable(true);
							btnEnable(true);
							txtclear();
						}
						catch (Exception exp)
						{
							tx.rollback();
							showNotification("SaveButtonEvent", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
						}
					}

				}
			});
		}
	}

	private boolean deleteData(Session session)
	{

		session.createSQLQuery("delete from tbAddOrLessExtraOT where Month(dDate)=MONTH('"+dateFormat.format(date.getValue())+"')" +
				" and Year(dDate)=YEAR('"+dateFormat.format(date.getValue())+"') and vSectionID='"+cmbSection.getValue()+"'").executeUpdate();
		return true;
	}

	private void insertdata(Session session)
	{
		String Sql="";
		for(int i=0;i<lblProximityID.size();i++) 
		{
			if(!lblProximityID.get(i).getValue().toString().isEmpty())
			{
				if(!txtPermittedBy.get(i).getValue().toString().isEmpty() && !txtPermittedBy.get(i).getValue().toString().isEmpty()
						&& (!txtExtraOT.get(i).getValue().toString().isEmpty() /*|| !txtLessOT.get(i).getValue().toString().isEmpty()*/))
				{
					Sql="insert into tbAddOrLessExtraOT (dDate,vEmployeeID,vEmployeeCode,vProximityID,vEmployeeName,vSectionID,vSectionName,vDesignationID,vDesignationName," +
							"vPermittedBy,vReason,mExtraOTAdd,mLessOT,vUserName,vUserIP,dEntryTime) values ('"+dateFormat.format(date.getValue())+"'," +
							"'"+lblAutoEmployeeID.get(i).getValue().toString()+"','"+lblEmployeeID.get(i).getValue().toString()+"','"+lblProximityID.get(i).getValue().toString()+"'," +
							"'"+lblEmployeeName.get(i).getValue().toString()+"','"+lblSectionID.get(i).getValue().toString()+"'," +
							"'"+lblSectionName.get(i).getValue().toString()+"','"+lblDesignationID.get(i).getValue().toString()+"'," +
							"'"+lblDesignation.get(i).getValue().toString()+"','"+txtPermittedBy.get(i).getValue()+"','"+txtReason.get(i).getValue()+"'," +
							"'"+(txtExtraOT.get(i).getValue().toString().trim().replaceAll(",", "").isEmpty()?"0":txtExtraOT.get(i).getValue().toString().trim().replaceAll(",", ""))+"'," +
							//	"'"+(txtLessOT.get(i).getValue().toString().trim().replaceAll(",", "").isEmpty()?"0":txtLessOT.get(i).getValue().toString().trim().replaceAll(",", ""))+"'," +
							"'"+sessionbean.getUserName()+"','"+sessionbean.getUserIp()+"',getdate())";
					session.createSQLQuery(Sql).executeUpdate();
				}
			}
		}
	}

	private void tableValueAdd(String emp)
	{

		Transaction tx=null;
		try
		{

			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql=" select Distinct (select employeeCode from tbEmployeeInfo ei where ei.iFingerID=ea.vEmployeeId),vEmployeeId,vEmployeeName," +
					   " vDesignation from tbEmployeeAttendance ea where vSectionId='"+cmbSection.getValue()+"' and " +
					   " MONTH(dAttDate)=MONTH('"+dateFormat.format(date.getValue())+"') " +
					   " and YEAR(dAttDate)=YEAR('"+dateFormat.format(date.getValue())+"') and vEmployeeId='"+cmbEmployee.getValue()+"' " +
					   " and OtStatus=1";

			List lst=session.createSQLQuery(sql).list();
			if(!lst.isEmpty())
			{
				Iterator itr=lst.iterator();
				boolean checkData=false;
				while(itr.hasNext())
				{

					Object [] element=(Object[])itr.next();
					boolean check=false;
					for(int chkindex=0;chkindex<lblEmployeeID.size();chkindex++)
					{

						if(lblAutoEmployeeID.get(chkindex).getValue().equals(element[0].toString()))
						{
							check=true;
							break;
						}
						if(lblProximityID.get(chkindex).getValue().toString().isEmpty())
						{
							index=chkindex;
							break;
						}

					}

					if(!check)
					{
						//lblAutoEmployeeID.get(index).setValue(element[0].toString());
						lblEmployeeID.get(index).setValue(element[1].toString());
						lblProximityID.get(index).setValue(element[2].toString());
						lblEmployeeName.get(index).setValue(element[3].toString());
						lblDesignationID.get(index).setValue(element[4].toString());
						lblDesignation.get(index).setValue(element[5].toString());
						lblSectionID.get(index).setValue(element[6].toString());
						lblSectionName.get(index).setValue(element[7].toString());

						if(index==lblEmployeeID.size()-1)
							tableRowAdd(index+1);

						index++;
					}
					checkData=check;
				}
				if(checkData)
				{
					showNotification("Warning", "Employee is already Found in the list!!!", Notification.TYPE_WARNING_MESSAGE);
				}
			}
			else
				showNotification("Warning", "No Data Found!!!", Notification.TYPE_WARNING_MESSAGE);

		}
		catch(Exception exp)
		{

			showNotification("TableValueAdd", exp.toString(), Notification.TYPE_ERROR_MESSAGE);

		}

	}

	private void txtclear()
	{

		cmbSection.setValue(null);
		cmbEmployee.setValue(null);
		opgEmployee.select(null);
		tableclear();

	}

	private void tableclear()
	{

		for(int i=0;i<lblEmployeeID.size();i++)
		{
			lblAutoEmployeeID.get(i).setValue("");
			lblEmployeeID.get(i).setValue("");
			lblProximityID.get(i).setValue("");
			lblEmployeeName.get(i).setValue("");
			lblDesignationID.get(i).setValue("");
			lblDesignation.get(i).setValue("");
			lblSectionID.get(i).setValue("");
			lblSectionName.get(i).setValue("");
			txtPermittedBy.get(i).setValue("");
			txtReason.get(i).setValue("");
			txtExtraOT.get(i).setValue("");
			//txtLessOT.get(i).setValue("");
		}

	}

	private void componentEnable(boolean b)
	{
		cmbSection.setEnabled(!b);
		opgEmployee.setEnabled(!b);
		cmbEmployee.setEnabled(!b);
		chkAllEmp.setEnabled(!b);
		date.setEnabled(!b);
		table.setEnabled(!b);
	}

	private void btnEnable(boolean b)
	{

		cButton.btnNew.setEnabled(b);
		cButton.btnSave.setEnabled(!b);
		cButton.btnEdit.setEnabled(b);
		cButton.btnDelete.setEnabled(b);
		cButton.btnRefresh.setEnabled(!b);
		cButton.btnFind.setEnabled(b);
	}

	public void tableinitialize()
	{

		for(int i=0;i<12;i++)
			tableRowAdd(i);

	}

	public void tableRowAdd(final int ar)
	{

		btnDel.add(ar, new NativeButton());
		btnDel.get(ar).setWidth("100%");
		btnDel.get(ar).setIcon(new ThemeResource("../icons/cancel.png"));
		btnDel.get(ar).addListener(new ClickListener()
		{

			
			public void buttonClick(ClickEvent event) 
			{

				lblAutoEmployeeID.get(ar).setValue("");
				lblEmployeeID.get(ar).setValue("");
				lblProximityID.get(ar).setValue("");
				lblEmployeeName.get(ar).setValue("");
				lblDesignationID.get(ar).setValue("");
				lblDesignation.get(ar).setValue("");
				lblSectionID.get(ar).setValue("");
				lblSectionName.get(ar).setValue("");
				txtPermittedBy.get(ar).setValue("");
				txtReason.get(ar).setValue("");
				txtExtraOT.get(ar).setValue("");
				//txtLessOT.get(ar).setValue("");

				for(int tbIndex=ar;tbIndex<lblProximityID.size();tbIndex++)
				{
					if(tbIndex+1<lblProximityID.size())
					{
						if(!lblProximityID.get(tbIndex+1).getValue().toString().trim().equals(""))
						{
							lblAutoEmployeeID.get(tbIndex).setValue(lblAutoEmployeeID.get(tbIndex+1).getValue().toString().trim());
							lblEmployeeID.get(tbIndex).setValue(lblEmployeeID.get(tbIndex+1).getValue().toString().trim());
							lblProximityID.get(tbIndex).setValue(lblProximityID.get(tbIndex+1).getValue().toString().trim());
							lblEmployeeName.get(tbIndex).setValue(lblEmployeeName.get(tbIndex+1).getValue().toString().trim());
							lblDesignationID.get(tbIndex).setValue(lblDesignationID.get(tbIndex+1).getValue().toString().trim());
							lblDesignation.get(tbIndex).setValue(lblDesignation.get(tbIndex+1).getValue().toString().trim());
							lblSectionID.get(tbIndex).setValue(lblSectionID.get(tbIndex+1).getValue().toString().trim());
							lblSectionName.get(tbIndex).setValue(lblSectionName.get(tbIndex+1).getValue().toString().trim());
							txtPermittedBy.get(tbIndex).setValue(txtPermittedBy.get(tbIndex+1).getValue().toString().trim());
							txtReason.get(tbIndex).setValue(txtReason.get(tbIndex+1).getValue().toString().trim());
							txtExtraOT.get(tbIndex).setValue(txtExtraOT.get(tbIndex+1).getValue().toString().trim());
							//	txtLessOT.get(tbIndex).setValue(txtLessOT.get(tbIndex+1).getValue().toString().trim());

							lblAutoEmployeeID.get(tbIndex+1).setValue("");
							lblEmployeeID.get(tbIndex+1).setValue("");
							lblProximityID.get(tbIndex+1).setValue("");
							lblEmployeeName.get(tbIndex+1).setValue("");
							lblDesignationID.get(tbIndex+1).setValue("");
							lblDesignation.get(tbIndex+1).setValue("");
							lblSectionID.get(tbIndex+1).setValue("");
							lblSectionName.get(tbIndex+1).setValue("");
							txtExtraOT.get(tbIndex+1).setValue("");
							//	txtLessOT.get(tbIndex+1).setValue("");
						}
					}
				}
			}
		});

		lblSl.add(ar, new Label());
		lblSl.get(ar).setWidth("100%");
		lblSl.get(ar).setValue(ar+1);

		lblAutoEmployeeID.add(ar, new Label());
		lblAutoEmployeeID.get(ar).setWidth("100%");

		lblEmployeeID.add(ar, new Label());
		lblEmployeeID.get(ar).setWidth("100%");

		lblProximityID.add(ar, new Label());
		lblProximityID.get(ar).setWidth("100%");

		lblEmployeeName.add(ar, new Label());
		lblEmployeeName.get(ar).setWidth("100%");

		lblDesignationID.add(ar, new Label());
		lblDesignationID.get(ar).setWidth("100%");

		lblDesignation.add(ar, new Label());
		lblDesignation.get(ar).setWidth("100%");

		lblSectionID.add(ar, new Label());
		lblSectionID.get(ar).setWidth("100%");

		lblSectionName.add(ar, new Label());
		lblSectionName.get(ar).setWidth("100%");

		txtPermittedBy.add(ar, new TextField());
		txtPermittedBy.get(ar).setWidth("100%");
		txtPermittedBy.get(ar).setImmediate(true);
		txtPermittedBy.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!lblAutoEmployeeID.get(ar).getValue().toString().isEmpty())
				{
					if(!txtPermittedBy.get(ar).getValue().toString().isEmpty())
					{
						txtReason.get(ar).focus();
					}
				}
			}
		});

		txtReason.add(ar, new TextField());
		txtReason.get(ar).setWidth("100%");
		txtReason.get(ar).setImmediate(true);
		txtReason.get(ar).addListener(new ValueChangeListener()
		{

			
			public void valueChange(ValueChangeEvent event)
			{
				if(!lblAutoEmployeeID.get(ar).getValue().toString().isEmpty())
				{
					if(!txtReason.get(ar).getValue().toString().isEmpty())
					{
						if(!lblAutoEmployeeID.get(ar+1).getValue().toString().isEmpty())
						{
							txtExtraOT.get(ar).focus();
						}
					}
				}
			}
		});

		txtExtraOT.add(ar, new AmountCommaSeperator());
		txtExtraOT.get(ar).setWidth("100%");
		txtExtraOT.get(ar).setImmediate(true);
		txtExtraOT.get(ar).addListener(new ValueChangeListener()
		{

			
			public void valueChange(ValueChangeEvent event)
			{
				if(!lblAutoEmployeeID.get(ar).getValue().toString().isEmpty())
				{
					if(!txtExtraOT.get(ar).getValue().toString().isEmpty())
					{
						//txtLessOT.get(ar).focus();
					}
				}
			}
		});

		/*	txtLessOT.add(ar, new AmountCommaSeperator());
		txtLessOT.get(ar).setWidth("100%");
		txtLessOT.get(ar).setImmediate(true);
		txtLessOT.get(ar).addListener(new ValueChangeListener()
		{

			
			public void valueChange(ValueChangeEvent event)
			{
				if(!lblAutoEmployeeID.get(ar).getValue().toString().isEmpty())
				{
					if(!txtLessOT.get(ar).getValue().toString().isEmpty())
					{
						txtPermittedBy.get(ar+1).focus();
					}
				}
			}
		});*/

		table.addItem(new Object[]{btnDel.get(ar),lblSl.get(ar),lblAutoEmployeeID.get(ar),lblEmployeeID.get(ar),lblProximityID.get(ar),
				lblEmployeeName.get(ar),lblDesignationID.get(ar),lblDesignation.get(ar),lblSectionID.get(ar),lblSectionName.get(ar),
				txtPermittedBy.get(ar),txtReason.get(ar),txtExtraOT.get(ar)/*,txtLessOT.get(ar)*/}, ar);

	}

	public AbsoluteLayout buildMainLayout()
	{

		mainlayout=new AbsoluteLayout();
		mainlayout.setWidth("1085.0px");
		mainlayout.setHeight("530.0px");

		date=new PopupDateField();
		date.setImmediate(true);
		date.setResolution(PopupDateField.RESOLUTION_MONTH);
		date.setDateFormat("MMMMM-yyyy");
		date.setValue(new Date());
		mainlayout.addComponent(new Label("Date : "), "top:20.0px;left:30.0px;");
		mainlayout.addComponent(date, "top:18.0px;left:120.0px;");

		cmbSection=new ComboBox();
		cmbSection.setWidth("290.0px");
		cmbSection.setHeight("-1px");
		cmbSection.setImmediate(true);
		mainlayout.addComponent(new Label("Section Name : "), "top:45.0px;left:30.0px;");
		mainlayout.addComponent(cmbSection, "top:43.0px;left:120.0px");

		cmbShift=new ComboBox();
		cmbShift.setWidth("290.0px");
		cmbShift.setHeight("-1px");
		cmbShift.setImmediate(true);
		mainlayout.addComponent(new Label("Shift : "), "top:70.0px;left:30.0px;");
		mainlayout.addComponent(cmbShift, "top:68.0px;left:120.0px");

		opgEmployee=new OptionGroup("",Optiontype);
		opgEmployee.setImmediate(true);
		opgEmployee.setStyleName("horizontal");
		mainlayout.addComponent(opgEmployee, "top:20.0px;left:450.0px;");

		cmbEmployee=new ComboBox();
		cmbEmployee.setWidth("290.0px");
		cmbEmployee.setHeight("-1px");
		cmbEmployee.setImmediate(true);
		mainlayout.addComponent(new Label("Employee : "), "top:45.0px;left:450.0px;");
		mainlayout.addComponent(cmbEmployee, "top:43.0px;left:540.0px;");

		chkAllEmp=new CheckBox("All");
		chkAllEmp.setImmediate(true);
		mainlayout.addComponent(chkAllEmp, "top:45.0px;left:840.0px");

		table.setWidth("98%");
		table.setHeight("360.0px");
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("Del", NativeButton.class, new NativeButton());
		table.setColumnWidth("Del", 30);

		table.addContainerProperty("SL", Label.class, new Label());
		table.setColumnWidth("SL", 25);

		table.addContainerProperty("Emp ID", Label.class, new Label());
		table.setColumnWidth("Emp ID", 70);

		table.addContainerProperty("Employee ID", Label.class, new Label());
		table.setColumnWidth("Employee ID",100);

		table.addContainerProperty("Finger ID", Label.class, new Label());
		table.setColumnWidth("Finger ID", 100);

		table.addContainerProperty("Employee Name", Label.class, new Label());
		table.setColumnWidth("Employee Name", 190);

		table.addContainerProperty("Designation ID", Label.class, new Label());
		table.setColumnWidth("Designation ID", 30);

		table.addContainerProperty("Designation", Label.class, new Label());
		table.setColumnWidth("Designation", 180);

		table.addContainerProperty("Section ID", Label.class, new Label());
		table.setColumnWidth("Section ID", 30);

		table.addContainerProperty("Section Name", Label.class, new Label());
		table.setColumnWidth("Section Name", 180);

		table.addContainerProperty("Permitted By", TextField.class, new TextField());
		table.setColumnWidth("Permitted By", 165);

		table.addContainerProperty("Reason", TextField.class, new TextField());
		table.setColumnWidth("Reason", 165);

		table.addContainerProperty("Extra OT", AmountCommaSeperator.class, new AmountCommaSeperator());
		table.setColumnWidth("Extra OT", 50);

		table.setColumnCollapsed("Emp ID", true);
		table.setColumnCollapsed("Designation ID", true);
		table.setColumnCollapsed("Designation", true);
		table.setColumnCollapsed("Section ID", true);
		table.setColumnCollapsed("Section Name", true);

		table.setColumnAlignments(new String[]{Table.ALIGN_CENTER,Table.ALIGN_RIGHT,Table.ALIGN_LEFT,
				Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_RIGHT,Table.ALIGN_LEFT,
				Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_CENTER,Table.ALIGN_CENTER,
				Table.ALIGN_CENTER/*,Table.ALIGN_CENTER*/});

		mainlayout.addComponent(table, "top:100.0px;left:30.0px");
		tableinitialize();

		mainlayout.addComponent(cButton, "top:490.0px;left:260.0px;");

		return mainlayout;
	}

}
