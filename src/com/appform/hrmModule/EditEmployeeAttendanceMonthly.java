package com.appform.hrmModule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


import org.hibernate.Session;
import org.hibernate.Transaction;
import com.common.share.*;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;

import java.awt.event.FocusListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Window.Notification;
//Developed by Sabrina Alam
public class EditEmployeeAttendanceMonthly extends Window 
{
	private AbsoluteLayout mainLayout;
	private Label lblDate = new Label("date");
	private PopupDateField dDate = new PopupDateField();
	private Label lblShift = new Label("");
	private Label lblShiftTime = new Label("");
	private Label lblDeptName = new Label("");
	private ComboBox cmbDeptName = new ComboBox();
	private TextRead txtColor = new TextRead("");
	private Label lblCl= new Label("");

	private TextRead txtColorp = new TextRead("");
	private Label lblClp= new Label("");
	private CheckBox chkAll= new CheckBox("");
	int count = 0;
	Object[] tbelement ;
	private Label lblEmployee = new Label("");
	private Label lblProx = new Label("");
	private Label lblFinger = new Label("");
	private ComboBox cmbEmployeeName = new ComboBox();
	private OptionGroup RadioBtnGroup;
	private SimpleDateFormat time=new SimpleDateFormat("HH:mm:ss");
	private static final List<String> type2 = Arrays.asList(new String[] {"Employee ID","Finger ID","Employee Name"});

	private SessionBean sessionBean;
	String computerName = "";
	String userName = "";
	String year = "";
	String deptID = "";
	String strEmpDeptID ="";
	String strEmpID ="";
	String strDeptID ="";
	int i=0;
	int j=0;
	String FingerID ="";
	boolean t;
	java.util.Date addDate; 

	boolean isSave=false;
	boolean isRefresh=false;

	private boolean isUpdate=false;
	private boolean isFind= false;

	private TextRead trIDDeptID = new TextRead("");
	private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy");
	private SimpleDateFormat Hourformat = new SimpleDateFormat("HH");
	private SimpleDateFormat Minformat = new SimpleDateFormat("mm");
	private SimpleDateFormat Secformat = new SimpleDateFormat("ss");
	private SimpleDateFormat Dformat = new SimpleDateFormat("dd-MM-yyyy");
	private Table table = new Table();
	private ArrayList<Label> lbDate = new ArrayList<Label>();
	private ArrayList<TimeField> inTime1 = new ArrayList<TimeField>();
	private ArrayList<TimeField> inTime2 = new ArrayList<TimeField>();
	private ArrayList<TimeField> inTime3 = new ArrayList<TimeField>();
	private ArrayList<TimeField> outTime1 = new ArrayList<TimeField>();
	private ArrayList<TimeField> outTime2 = new ArrayList<TimeField>();
	private ArrayList<TimeField> outTime3 = new ArrayList<TimeField>();
	private ArrayList<TextField> inAmPm = new ArrayList<TextField>();
	private ArrayList<TextField> outAmPm = new ArrayList<TextField>();
	private ArrayList<ComboBox> cmbShiftStatus = new ArrayList<ComboBox>();
	private ArrayList<PopupDateField> inTime = new ArrayList<PopupDateField>();
	private ArrayList<PopupDateField> outTime = new ArrayList<PopupDateField>();
	private ArrayList<TextField> txtPermitBy = new ArrayList<TextField>();
	private ArrayList<TextField> txtReason = new ArrayList<TextField>();
	private ArrayList<Label> lbSecId = new ArrayList<Label>();
	private ArrayList<Label> lbSecName = new ArrayList<Label>();
	private ArrayList<Label> lbEmpId = new ArrayList<Label>();
	private ArrayList<Label> lbAttendDate = new ArrayList<Label>();
	private ArrayList<Label> lbDeg = new ArrayList<Label>();
	private ArrayList<Label> lbEmpName = new ArrayList<Label>();
	private ArrayList<Label> lbShiftId = new ArrayList<Label>();
	private ArrayList<PopupDateField> date = new ArrayList<PopupDateField>();
	private ArrayList<CheckBox> tbChk = new ArrayList<CheckBox>();

	private ArrayList<PopupDateField> inDate = new ArrayList<PopupDateField>();
	private ArrayList<PopupDateField> outDate = new ArrayList<PopupDateField>();


	private CommonButton button = new CommonButton("New", "Save", "", "","Refresh","","","","","Exit");
	ArrayList<Component> allComp = new ArrayList<Component>();
	private DecimalFormat Deci=new DecimalFormat("#00:00");
	private SimpleDateFormat time12=new SimpleDateFormat("hh:mm:ss aa");
	private SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat inOutDateTime=new SimpleDateFormat("dd-MM-yyyy hh:mm:ss aa");
	private SimpleDateFormat dform=new SimpleDateFormat("dd-MM-yyyy");


	public EditEmployeeAttendanceMonthly(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("EDIT MONTHLY EMPLOYEE ATTENDANCE :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);
		tableInitialize();

		btnIni(true);
		componentIni(true);
		buttonAction();

		SectionDataAdd();

		focusEnter();
		authenticationCheck();

		button.btnNew.focus();
	}

	private void authenticationCheck()
	{
		if(!sessionBean.isSubmitable()){
			button.btnSave.setVisible(false);
		}

		if(!sessionBean.isUpdateable()){
			button.btnEdit.setVisible(false);
		}

		if(!sessionBean.isDeleteable()){
			button.btnDelete.setVisible(false);
		}
	}

	private void buttonAction()
	{
		dDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(dDate.getValue()!=null)
				{
					cmbDeptName.setValue(null);
					cmbEmployeeName.setValue(null);
					tableclear();
				}
			}
		});

		button.btnNew.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isSave=false;
				isRefresh=false;
				isUpdate = false;
				isFind = false;
				newButtonEvent();
			}
		});

		button.btnFind.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isFind = true;
			}
		});

		button.btnEdit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate = true;
				updateAction();
			}
		});

		cmbDeptName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbDeptName.getValue()!=null)
				{
					tableclear();
					RadioBtnGroup.select(null);
					cmbEmployeeName.removeAllItems();
					RadioBtnGroup.select("Finger ID");
				}
			}
		});

		RadioBtnGroup.addListener(new ValueChangeListener()
		{

			
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbDeptName.getValue()!=null)
				{

					String sql="";
					if(RadioBtnGroup.getValue()=="Employee ID")
					{

						sql="select iFingerID,employeeCode from tbEmployeeInfo where vSectionId='"+cmbDeptName.getValue().toString()+"'  and iStatus='1'";
					}
					else if(RadioBtnGroup.getValue()=="Proximity ID")
					{

						sql="select iFingerID,vProximityId from tbEmployeeInfo where vSectionId='"+cmbDeptName.getValue().toString()+"'  and iStatus='1'";

					}

					else if(RadioBtnGroup.getValue()=="Finger ID")
					{

						sql="select iFingerID,iFingerID from tbEmployeeInfo where vSectionId='"+cmbDeptName.getValue().toString()+"'  and iStatus='1'";

					}
					else if(RadioBtnGroup.getValue()=="Employee Name")

					{

						sql="select iFingerID,vEmployeeName from tbEmployeeInfo where vSectionId='"+cmbDeptName.getValue().toString()+"'  and iStatus='1'";

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
									cmbEmployeeName.addItem(element[0]);
									cmbEmployeeName.setItemCaption(element[0], element[1].toString());
								}

							}
							else
								showNotification("Warning", "No Employee Found!!!", Notification.TYPE_WARNING_MESSAGE);

						}
						catch(Exception exp)
						{

							showNotification("RadioBtnGroup", exp.toString(), Notification.TYPE_ERROR_MESSAGE);

						}
					}

				}

				else
				{
					if(!isSave && !isRefresh)
					{
						RadioBtnGroup.select(null);
						showNotification("Warning", "Please Select Section Name!!!", Notification.TYPE_WARNING_MESSAGE);
					}
				}

			}
		});

		chkAll.addListener(new ValueChangeListener()
		{

			
			public void valueChange(ValueChangeEvent event)
			{
				if(chkAll.booleanValue())
				{
					for(int i=0;i<lbDate.size();i++)
					{
						if(cmbShiftStatus.get(i).getValue()!=null)
						{
							if(!inTime1.get(i).getValue().toString().isEmpty())
							{
								if(!inTime2.get(i).getValue().toString().isEmpty())
								{
									if(!inTime3.get(i).getValue().toString().isEmpty())
									{
										if(!inAmPm.get(i).getValue().toString().isEmpty())
										{
											if(!outTime1.get(i).getValue().toString().isEmpty())
											{
												if(!outTime2.get(i).getValue().toString().isEmpty())
												{
													if(!outTime3.get(i).getValue().toString().isEmpty())
													{
														if(!outAmPm.get(i).getValue().toString().isEmpty())
														{
															if(!txtReason.get(i).getValue().toString().isEmpty())
															{
																if(!txtPermitBy.get(i).getValue().toString().isEmpty())
																{
																	tbChk.get(i).setEnabled(true);
																	tbChk.get(i).setValue(true);
																}
																else
																{
																	tbChk.get(i).setEnabled(false);
																	tbChk.get(i).setValue(false);
																}
															}
															else
															{
																tbChk.get(i).setEnabled(false);
																tbChk.get(i).setValue(false);
															}
														}
														else
														{
															tbChk.get(i).setEnabled(false);
															tbChk.get(i).setValue(false);
														}
													}
													else
													{
														tbChk.get(i).setEnabled(false);
														tbChk.get(i).setValue(false);
													}
												}
												else
												{
													tbChk.get(i).setEnabled(false);
													tbChk.get(i).setValue(false);
												}
											}
											else
											{
												tbChk.get(i).setEnabled(false);
												tbChk.get(i).setValue(false);
											}
										}
										else
										{
											tbChk.get(i).setEnabled(false);
											tbChk.get(i).setValue(false);
										}
									}
									else
									{
										tbChk.get(i).setEnabled(false);
										tbChk.get(i).setValue(false);
									}
								}
								else
								{
									tbChk.get(i).setEnabled(false);
									tbChk.get(i).setValue(false);
								}
							}
							else
							{
								tbChk.get(i).setEnabled(false);
								tbChk.get(i).setValue(false);
							}
						}
						else
						{
							tbChk.get(i).setEnabled(false);
							tbChk.get(i).setValue(false);
						}
					}
				}
				else
				{

					for(int i=0;i<lbDate.size();i++)
					{
						if(cmbShiftStatus.get(i).getValue()!=null)
						{
							if(!inTime1.get(i).getValue().toString().isEmpty())
							{
								if(!inTime2.get(i).getValue().toString().isEmpty())
								{
									if(!inTime3.get(i).getValue().toString().isEmpty())
									{
										if(!inAmPm.get(i).getValue().toString().isEmpty())
										{
											if(!outTime1.get(i).getValue().toString().isEmpty())
											{
												if(!outTime2.get(i).getValue().toString().isEmpty())
												{
													if(!outTime3.get(i).getValue().toString().isEmpty())
													{
														if(!outAmPm.get(i).getValue().toString().isEmpty())
														{
															if(!txtReason.get(i).getValue().toString().isEmpty())
															{
																if(!txtPermitBy.get(i).getValue().toString().isEmpty())
																{
																	//tbChk.get(i).setEnabled(false);
																	tbChk.get(i).setValue(false);
																}
																else
																{
																	tbChk.get(i).setEnabled(false);
																	tbChk.get(i).setValue(false);
																}
															}
															else
															{
																tbChk.get(i).setEnabled(false);
																tbChk.get(i).setValue(false);
															}
														}
														else
														{
															tbChk.get(i).setEnabled(false);
															tbChk.get(i).setValue(false);
														}
													}
													else
													{
														tbChk.get(i).setEnabled(false);
														tbChk.get(i).setValue(false);
													}
												}
												else
												{
													tbChk.get(i).setEnabled(false);
													tbChk.get(i).setValue(false);
												}
											}
											else
											{
												tbChk.get(i).setEnabled(false);
												tbChk.get(i).setValue(false);
											}
										}
										else
										{
											tbChk.get(i).setEnabled(false);
											tbChk.get(i).setValue(false);
										}
									}
									else
									{
										tbChk.get(i).setEnabled(false);
										tbChk.get(i).setValue(false);
									}
								}
								else
								{
									tbChk.get(i).setEnabled(false);
									tbChk.get(i).setValue(false);
								}
							}
							else
							{
								tbChk.get(i).setEnabled(false);
								tbChk.get(i).setValue(false);
							}
						}
						else
						{
							tbChk.get(i).setEnabled(false);
							tbChk.get(i).setValue(false);
						}
					}

				}

			}
		});

		cmbEmployeeName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbEmployeeName.getValue()!=null)
				{
					tableclear();

					for(int i=0; i<lbDate.size(); i++)
					{
						addCmbShiftData(i);
					}
					SetMonthlyAttendance();
				}
			}
		});

		button.btnSave.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbDeptName.getValue()!=null && cmbEmployeeName.getValue()!=null)
				{
					if(count>0)
					{
						isSave=true;
						saveButtonAction();
					}

					else
					{
						showNotification("Warning", "Please Provide all Data!!!", Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					showNotification("Warning", "No Data Found in Table!!!", Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		button.btnRefresh.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				isRefresh=true;
				isUpdate = false;
				isFind=false;
				refreshButtonEvent();
			}
		});

		button.btnExit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

	}

	private void addCmbShiftData(final int ar)
	{
		String query = "select vShiftId,vShiftName,dShiftStart,dShiftEnd from tbGroupShiftInfo";
		System.out.println(query);

		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx;

		tx = session.beginTransaction();
		List list = session.createSQLQuery(query).list();

		for(Iterator iter = list.iterator(); iter.hasNext();)
		{
			Object[] element = (Object[]) iter.next();

			cmbShiftStatus.get(ar).addItem(element[0].toString());
			cmbShiftStatus.get(ar).setItemCaption(element[0].toString(), element[1].toString());
			//+" ("+time12.format(element[2])+"-"+time12.format(element[3])+" )");
		}
	}

	private void tableInitialize()
	{

		table.setColumnCollapsingAllowed(true);
		table.setWidth("98%");
		table.setHeight("430px");
		table.setPageLength(0);
		table.setImmediate(true);

		table.addContainerProperty("date", Label.class , new Label());
		table.setColumnWidth("date",70);

		table.addContainerProperty("Shift", ComboBox.class , new ComboBox());
		table.setColumnWidth("Shift",120);

		table.addContainerProperty("In date", PopupDateField.class , new PopupDateField());
		table.setColumnWidth("In date",100);

		table.addContainerProperty("In1", TimeField.class , new TimeField());
		table.setColumnWidth("In1",28);

		table.addContainerProperty("In2", TimeField.class , new TimeField());
		table.setColumnWidth("In2",28);

		table.addContainerProperty("In3", TimeField.class , new TimeField());
		table.setColumnWidth("In3",28);

		table.addContainerProperty("inT", TextField.class , new TextField());
		table.setColumnWidth("inT",28);

		table.addContainerProperty("Out date", PopupDateField.class , new PopupDateField());
		table.setColumnWidth("Out date",100);

		table.addContainerProperty("ou1", TimeField.class , new TimeField());
		table.setColumnWidth("ou1",28);

		table.addContainerProperty("ou2", TimeField.class , new TimeField());
		table.setColumnWidth("ou2",28);

		table.addContainerProperty("ou3", TimeField.class , new TimeField());
		table.setColumnWidth("ou3",28);

		table.addContainerProperty("ouT", TextField.class , new TextField());
		table.setColumnWidth("ouT",28);

		table.addContainerProperty("Reason", TextField.class , new TextField());
		table.setColumnWidth("Reason",163);

		table.addContainerProperty("Permitted By", TextField.class , new TextField());
		table.setColumnWidth("Permitted By",145);

		table.addContainerProperty("Emp ID", Label.class , new Label());
		table.setColumnWidth("Emp ID",70);

		table.addContainerProperty("EmpName", Label.class , new Label());
		table.setColumnWidth("EmpName",120);

		table.addContainerProperty("Desig", Label.class , new Label());
		table.setColumnWidth("Desig",50);

		table.addContainerProperty("ShiftId", Label.class , new Label());
		table.setColumnWidth("ShiftId",50);

		table.addContainerProperty("Intime", PopupDateField.class , new PopupDateField());
		table.setColumnWidth("Intime",100);

		table.addContainerProperty("Outtime", PopupDateField.class , new PopupDateField());
		table.setColumnWidth("Outtime",100);

		table.addContainerProperty("Attend date", Label.class , new Label());
		table.setColumnWidth("Attend date",100);

		table.addContainerProperty("EveryDate", PopupDateField.class , new PopupDateField());
		table.setColumnWidth("EveryDate",100);

		table.addContainerProperty("C", CheckBox.class , new CheckBox());
		table.setColumnWidth("C",20);



		//table.setColumnCollapsed("p", true);
		table.setColumnCollapsed("Emp ID", true);
		table.setColumnCollapsed("EmpName", true);
		table.setColumnCollapsed("Desig", true);
		table.setColumnCollapsed("ShiftId", true);
		table.setColumnCollapsed("Intime", true);
		table.setColumnCollapsed("Outtime", true);
		table.setColumnCollapsed("Attend date", true);
		table.setColumnCollapsed("EveryDate", true);

		table.setColumnAlignments(new String[] {
				Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_RIGHT,
				Table.ALIGN_RIGHT,Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,
				Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,
				Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER});

		rowAddinTable();
	}

	public void rowAddinTable()
	{
		for(int i=0;i<32;i++)
		{
			tableRowAdd(i);
		}
	}

	public void tableRowAdd(final int ar)
	{
		lbDate.add(ar, new Label(""));
		lbDate.get(ar).setWidth("100%");
		lbDate.get(ar).setHeight("20px");

		cmbShiftStatus.add(ar, new ComboBox(""));
		cmbShiftStatus.get(ar).setWidth("100%");
		cmbShiftStatus.get(ar).setHeight("20px");
		cmbShiftStatus.get(ar).setImmediate(true);
		cmbShiftStatus.get(ar).setReadOnly(true);
		cmbShiftStatus.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbShiftStatus.get(ar).getValue()!=null)
				{
					if(!inTime1.get(ar).getValue().toString().isEmpty())
					{
						if(!inTime2.get(ar).getValue().toString().isEmpty())
						{
							if(!inTime3.get(ar).getValue().toString().isEmpty())
							{
								if(!inAmPm.get(ar).getValue().toString().isEmpty())
								{
									if(!outTime1.get(ar).getValue().toString().isEmpty())
									{
										if(!outTime2.get(ar).getValue().toString().isEmpty())
										{
											if(!outTime3.get(ar).getValue().toString().isEmpty())
											{
												if(!outAmPm.get(ar).getValue().toString().isEmpty())
												{
													if(!txtReason.get(ar).getValue().toString().isEmpty())
													{
														if(!txtPermitBy.get(ar).getValue().toString().isEmpty())
														{
															tbChk.get(ar).setEnabled(true);
														}
														else
														{
															tbChk.get(ar).setEnabled(false);
															tbChk.get(ar).setValue(false);
														}
													}
													else
													{
														tbChk.get(ar).setEnabled(false);
														tbChk.get(ar).setValue(false);
													}
												}
												else
												{
													tbChk.get(ar).setEnabled(false);
													tbChk.get(ar).setValue(false);
												}
											}
											else
											{
												tbChk.get(ar).setEnabled(false);
												tbChk.get(ar).setValue(false);
											}
										}
										else
										{
											tbChk.get(ar).setEnabled(false);
											tbChk.get(ar).setValue(false);
										}
									}
									else
									{
										tbChk.get(ar).setEnabled(false);
										tbChk.get(ar).setValue(false);
									}
								}
								else
								{
									tbChk.get(ar).setEnabled(false);
									tbChk.get(ar).setValue(false);
								}
							}
							else
							{
								tbChk.get(ar).setEnabled(false);
								tbChk.get(ar).setValue(false);
							}
						}
						else
						{
							tbChk.get(ar).setEnabled(false);
							tbChk.get(ar).setValue(false);
						}
					}
					else
					{
						tbChk.get(ar).setEnabled(false);
						tbChk.get(ar).setValue(false);
					}
				}
				else
				{
					tbChk.get(ar).setEnabled(false);
					tbChk.get(ar).setValue(false);
				}
			}
		});

		inDate.add(ar, new PopupDateField());
		inDate.get(ar).setValue(new java.util.Date());
		inDate.get(ar).setWidth("100px");
		inDate.get(ar).setHeight("24px");
		inDate.get(ar).setResolution(PopupDateField.RESOLUTION_DAY);
		inDate.get(ar).setDateFormat("dd-MM-yyyy");
		inDate.get(ar).setInvalidAllowed(false);
		inDate.get(ar).setImmediate(true);

		inTime1.add(ar, new TimeField());
		inTime1.get(ar).setWidth("32px");
		inTime1.get(ar).setHeight("20px");
		inTime1.get(ar).setInputPrompt("hh");
		inTime1.get(ar).setImmediate(true);
		inTime1.get(ar).setStyleName("time");
		inTime1.get(ar).setMaxLength(2);
		inTime1.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!inTime1.get(ar).getValue().toString().isEmpty())
				{			
					if(Double.parseDouble(inTime1.get(ar).getValue().toString())>12)
					{
						inTime1.get(ar).setValue("");
						inAmPm.get(ar).setValue("PM");
					}
					else{
						inTime1.get(ar).setValue(inTime1.get(ar).getValue().toString());
					}
				}
				if(cmbShiftStatus.get(ar).getValue()!=null)
				{
					if(!inTime1.get(ar).getValue().toString().isEmpty())
					{
						if(!inTime2.get(ar).getValue().toString().isEmpty())
						{
							if(!inTime3.get(ar).getValue().toString().isEmpty())
							{
								if(!inAmPm.get(ar).getValue().toString().isEmpty())
								{
									if(!outTime1.get(ar).getValue().toString().isEmpty())
									{
										if(!outTime2.get(ar).getValue().toString().isEmpty())
										{
											if(!outTime3.get(ar).getValue().toString().isEmpty())
											{
												if(!outAmPm.get(ar).getValue().toString().isEmpty())
												{
													if(!txtReason.get(ar).getValue().toString().isEmpty())
													{
														if(!txtPermitBy.get(ar).getValue().toString().isEmpty())
														{
															tbChk.get(ar).setEnabled(true);
														}
														else
														{
															tbChk.get(ar).setEnabled(false);
															tbChk.get(ar).setValue(false);
														}
													}
													else
													{
														tbChk.get(ar).setEnabled(false);
														tbChk.get(ar).setValue(false);
													}
												}
												else
												{
													tbChk.get(ar).setEnabled(false);
													tbChk.get(ar).setValue(false);
												}
											}
											else
											{
												tbChk.get(ar).setEnabled(false);
												tbChk.get(ar).setValue(false);
											}
										}
										else
										{
											tbChk.get(ar).setEnabled(false);
											tbChk.get(ar).setValue(false);
										}
									}
									else
									{
										tbChk.get(ar).setEnabled(false);
										tbChk.get(ar).setValue(false);
									}
								}
								else
								{
									tbChk.get(ar).setEnabled(false);
									tbChk.get(ar).setValue(false);
								}
							}
							else
							{
								tbChk.get(ar).setEnabled(false);
								tbChk.get(ar).setValue(false);
							}
						}
						else
						{
							tbChk.get(ar).setEnabled(false);
							tbChk.get(ar).setValue(false);
						}
					}
					else
					{
						tbChk.get(ar).setEnabled(false);
						tbChk.get(ar).setValue(false);
					}
				}
				else
				{
					tbChk.get(ar).setEnabled(false);
					tbChk.get(ar).setValue(false);
				}
			}
		});

		inTime2.add(ar, new TimeField());
		inTime2.get(ar).setWidth("32px");
		inTime2.get(ar).setHeight("20px");
		inTime2.get(ar).setInputPrompt("mm");
		inTime2.get(ar).setImmediate(true);
		inTime2.get(ar).setStyleName("time");
		inTime2.get(ar).setMaxLength(2);
		inTime2.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!inTime2.get(ar).getValue().toString().isEmpty())
				{			
					if(Double.parseDouble(inTime2.get(ar).getValue().toString())>=60)
					{
						inTime2.get(ar).setValue("");
					}
					else{
						inTime2.get(ar).setValue(inTime2.get(ar).getValue().toString());
					}
				}
				cmbShiftStatus.get(ar).setReadOnly(false);

				if(cmbShiftStatus.get(ar).getValue()!=null)
				{
					cmbShiftStatus.get(ar).setReadOnly(false);

					if(!inTime1.get(ar).getValue().toString().isEmpty())
					{
						if(!inTime2.get(ar).getValue().toString().isEmpty())
						{
							if(!inTime3.get(ar).getValue().toString().isEmpty())
							{
								if(!inAmPm.get(ar).getValue().toString().isEmpty())
								{
									if(!outTime1.get(ar).getValue().toString().isEmpty())
									{
										if(!outTime2.get(ar).getValue().toString().isEmpty())
										{
											if(!outTime3.get(ar).getValue().toString().isEmpty())
											{
												if(!outAmPm.get(ar).getValue().toString().isEmpty())
												{
													if(!txtReason.get(ar).getValue().toString().isEmpty())
													{
														if(!txtPermitBy.get(ar).getValue().toString().isEmpty())
														{
															tbChk.get(ar).setEnabled(true);
														}
														else
														{
															tbChk.get(ar).setEnabled(false);
															tbChk.get(ar).setValue(false);
														}
													}
													else
													{
														tbChk.get(ar).setEnabled(false);
														tbChk.get(ar).setValue(false);
													}
												}
												else
												{
													tbChk.get(ar).setEnabled(false);
													tbChk.get(ar).setValue(false);
												}
											}
											else
											{
												tbChk.get(ar).setEnabled(false);
												tbChk.get(ar).setValue(false);
											}
										}
										else
										{
											tbChk.get(ar).setEnabled(false);
											tbChk.get(ar).setValue(false);
										}
									}
									else
									{
										tbChk.get(ar).setEnabled(false);
										tbChk.get(ar).setValue(false);
									}
								}
								else
								{
									tbChk.get(ar).setEnabled(false);
									tbChk.get(ar).setValue(false);
								}
							}
							else
							{
								tbChk.get(ar).setEnabled(false);
								tbChk.get(ar).setValue(false);
							}
						}
						else
						{
							tbChk.get(ar).setEnabled(false);
							tbChk.get(ar).setValue(false);
						}
					}
					else
					{
						tbChk.get(ar).setEnabled(false);
						tbChk.get(ar).setValue(false);
					}
				}
				else
				{
					tbChk.get(ar).setEnabled(false);
					tbChk.get(ar).setValue(false);
				}
			}
		});

		inTime3.add(ar, new TimeField());
		inTime3.get(ar).setWidth("32px");
		inTime3.get(ar).setHeight("20px");
		inTime3.get(ar).setInputPrompt("ss");
		inTime3.get(ar).setImmediate(true);
		inTime3.get(ar).setStyleName("time");
		inTime3.get(ar).setMaxLength(2);
		inTime3.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!inTime3.get(ar).getValue().toString().isEmpty())
				{			
					if(Double.parseDouble(inTime3.get(ar).getValue().toString())>=60)
					{
						inTime3.get(ar).setValue("");
					}
					else{
						inTime3.get(ar).setValue(inTime3.get(ar).getValue().toString());
					}
				}
				if(cmbShiftStatus.get(ar).getValue()!=null)
				{
					if(!inTime1.get(ar).getValue().toString().isEmpty())
					{
						if(!inTime2.get(ar).getValue().toString().isEmpty())
						{
							if(!inTime3.get(ar).getValue().toString().isEmpty())
							{
								if(!inAmPm.get(ar).getValue().toString().isEmpty())
								{
									if(!outTime1.get(ar).getValue().toString().isEmpty())
									{
										if(!outTime2.get(ar).getValue().toString().isEmpty())
										{
											if(!outTime3.get(ar).getValue().toString().isEmpty())
											{
												if(!outAmPm.get(ar).getValue().toString().isEmpty())
												{
													if(!txtReason.get(ar).getValue().toString().isEmpty())
													{
														if(!txtPermitBy.get(ar).getValue().toString().isEmpty())
														{
															tbChk.get(ar).setEnabled(true);
														}
														else
														{
															tbChk.get(ar).setEnabled(false);
															tbChk.get(ar).setValue(false);
														}
													}
													else
													{
														tbChk.get(ar).setEnabled(false);
														tbChk.get(ar).setValue(false);
													}
												}
												else
												{
													tbChk.get(ar).setEnabled(false);
													tbChk.get(ar).setValue(false);
												}
											}
											else
											{
												tbChk.get(ar).setEnabled(false);
												tbChk.get(ar).setValue(false);
											}
										}
										else
										{
											tbChk.get(ar).setEnabled(false);
											tbChk.get(ar).setValue(false);
										}
									}
									else
									{
										tbChk.get(ar).setEnabled(false);
										tbChk.get(ar).setValue(false);
									}
								}
								else
								{
									tbChk.get(ar).setEnabled(false);
									tbChk.get(ar).setValue(false);
								}
							}
							else
							{
								tbChk.get(ar).setEnabled(false);
								tbChk.get(ar).setValue(false);
							}
						}
						else
						{
							tbChk.get(ar).setEnabled(false);
							tbChk.get(ar).setValue(false);
						}
					}
					else
					{
						tbChk.get(ar).setEnabled(false);
						tbChk.get(ar).setValue(false);
					}
				}
				else
				{
					tbChk.get(ar).setEnabled(false);
					tbChk.get(ar).setValue(false);
				}
			}
		});

		inAmPm.add(ar, new TextField(""));
		inAmPm.get(ar).setWidth("28px");
		inAmPm.get(ar).setHeight("-1px");
		inAmPm.get(ar).setInputPrompt("AM");
		inAmPm.get(ar).setImmediate(true);
		inAmPm.get(ar).setMaxLength(2);
		inAmPm.get(ar).addListener(new TextChangeListener() {
			public void textChange(TextChangeEvent event)
			{
				if(event.getText().equalsIgnoreCase("A"))
				{
					inAmPm.get(ar).setValue("AM");
				}
				if(event.getText().equalsIgnoreCase("P"))
				{
					inAmPm.get(ar).setValue("PM");
				}

				if(cmbShiftStatus.get(ar).getValue()!=null)
				{
					if(!inTime1.get(ar).getValue().toString().isEmpty())
					{
						if(!inTime2.get(ar).getValue().toString().isEmpty())
						{
							if(!inTime3.get(ar).getValue().toString().isEmpty())
							{
								if(!inAmPm.get(ar).getValue().toString().isEmpty())
								{
									if(!outTime1.get(ar).getValue().toString().isEmpty())
									{
										if(!outTime2.get(ar).getValue().toString().isEmpty())
										{
											if(!outTime3.get(ar).getValue().toString().isEmpty())
											{
												if(!outAmPm.get(ar).getValue().toString().isEmpty())
												{
													if(!txtReason.get(ar).getValue().toString().isEmpty())
													{
														if(!txtPermitBy.get(ar).getValue().toString().isEmpty())
														{
															tbChk.get(ar).setEnabled(true);
														}
														else
														{
															tbChk.get(ar).setEnabled(false);
															tbChk.get(ar).setValue(false);
														}
													}
													else
													{
														tbChk.get(ar).setEnabled(false);
														tbChk.get(ar).setValue(false);
													}
												}
												else
												{
													tbChk.get(ar).setEnabled(false);
													tbChk.get(ar).setValue(false);
												}
											}
											else
											{
												tbChk.get(ar).setEnabled(false);
												tbChk.get(ar).setValue(false);
											}
										}
										else
										{
											tbChk.get(ar).setEnabled(false);
											tbChk.get(ar).setValue(false);
										}
									}
									else
									{
										tbChk.get(ar).setEnabled(false);
										tbChk.get(ar).setValue(false);
									}
								}
								else
								{
									tbChk.get(ar).setEnabled(false);
									tbChk.get(ar).setValue(false);
								}
							}
							else
							{
								tbChk.get(ar).setEnabled(false);
								tbChk.get(ar).setValue(false);
							}
						}
						else
						{
							tbChk.get(ar).setEnabled(false);
							tbChk.get(ar).setValue(false);
						}
					}
					else
					{
						tbChk.get(ar).setEnabled(false);
						tbChk.get(ar).setValue(false);
					}
				}
				else
				{
					tbChk.get(ar).setEnabled(false);
					tbChk.get(ar).setValue(false);
				}
			}

		});

		outDate.add(ar, new PopupDateField());
		outDate.get(ar).setValue(new java.util.Date());
		outDate.get(ar).setWidth("100px");
		outDate.get(ar).setHeight("24px");
		outDate.get(ar).setResolution(PopupDateField.RESOLUTION_DAY);
		outDate.get(ar).setDateFormat("dd-MM-yyyy");
		outDate.get(ar).setInvalidAllowed(false);
		outDate.get(ar).setImmediate(true);

		outTime1.add(ar, new TimeField());
		outTime1.get(ar).setWidth("32px");
		outTime1.get(ar).setHeight("20px");
		outTime1.get(ar).setInputPrompt("hh");
		outTime1.get(ar).setImmediate(true);
		outTime1.get(ar).setStyleName("time");
		outTime1.get(ar).setMaxLength(2);
		outTime1.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!outTime1.get(ar).getValue().toString().isEmpty())
				{			
					if(Double.parseDouble(outTime1.get(ar).getValue().toString())>=24)
					{
						outTime1.get(ar).setValue("");
					}
					else{
						outTime1.get(ar).setValue(outTime1.get(ar).getValue().toString());
					}
				}
				if(cmbShiftStatus.get(ar).getValue()!=null)
				{
					if(!inTime1.get(ar).getValue().toString().isEmpty())
					{
						if(!inTime2.get(ar).getValue().toString().isEmpty())
						{
							if(!inTime3.get(ar).getValue().toString().isEmpty())
							{
								if(!inAmPm.get(ar).getValue().toString().isEmpty())
								{
									if(!outTime1.get(ar).getValue().toString().isEmpty())
									{
										if(!outTime2.get(ar).getValue().toString().isEmpty())
										{
											if(!outTime3.get(ar).getValue().toString().isEmpty())
											{
												if(!outAmPm.get(ar).getValue().toString().isEmpty())
												{
													if(!txtReason.get(ar).getValue().toString().isEmpty())
													{
														if(!txtPermitBy.get(ar).getValue().toString().isEmpty())
														{
															tbChk.get(ar).setEnabled(true);
														}
														else
														{
															tbChk.get(ar).setEnabled(false);
															tbChk.get(ar).setValue(false);
														}
													}
													else
													{
														tbChk.get(ar).setEnabled(false);
														tbChk.get(ar).setValue(false);
													}
												}
												else
												{
													tbChk.get(ar).setEnabled(false);
													tbChk.get(ar).setValue(false);
												}
											}
											else
											{
												tbChk.get(ar).setEnabled(false);
												tbChk.get(ar).setValue(false);
											}
										}
										else
										{
											tbChk.get(ar).setEnabled(false);
											tbChk.get(ar).setValue(false);
										}
									}
									else
									{
										tbChk.get(ar).setEnabled(false);
										tbChk.get(ar).setValue(false);
									}
								}
								else
								{
									tbChk.get(ar).setEnabled(false);
									tbChk.get(ar).setValue(false);
								}
							}
							else
							{
								tbChk.get(ar).setEnabled(false);
								tbChk.get(ar).setValue(false);
							}
						}
						else
						{
							tbChk.get(ar).setEnabled(false);
							tbChk.get(ar).setValue(false);
						}
					}
					else
					{
						tbChk.get(ar).setEnabled(false);
						tbChk.get(ar).setValue(false);
					}
				}
				else
				{
					tbChk.get(ar).setEnabled(false);
					tbChk.get(ar).setValue(false);
				}
			}
		});

		outTime2.add(ar, new TimeField());
		outTime2.get(ar).setWidth("32px");
		outTime2.get(ar).setHeight("20px");
		outTime2.get(ar).setInputPrompt("mm");
		outTime2.get(ar).setImmediate(true);
		outTime2.get(ar).setStyleName("time");
		outTime2.get(ar).setMaxLength(2);
		outTime2.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!outTime2.get(ar).getValue().toString().isEmpty())
				{			
					if(Double.parseDouble(outTime2.get(ar).getValue().toString())>=60)
					{
						outTime2.get(ar).setValue("");
					}
					else{
						outTime2.get(ar).setValue(outTime2.get(ar).getValue().toString());
					}
				}
				if(cmbShiftStatus.get(ar).getValue()!=null)
				{
					if(!inTime1.get(ar).getValue().toString().isEmpty())
					{
						if(!inTime2.get(ar).getValue().toString().isEmpty())
						{
							if(!inTime3.get(ar).getValue().toString().isEmpty())
							{
								if(!inAmPm.get(ar).getValue().toString().isEmpty())
								{
									if(!outTime1.get(ar).getValue().toString().isEmpty())
									{
										if(!outTime2.get(ar).getValue().toString().isEmpty())
										{
											if(!outTime3.get(ar).getValue().toString().isEmpty())
											{
												if(!outAmPm.get(ar).getValue().toString().isEmpty())
												{
													if(!txtReason.get(ar).getValue().toString().isEmpty())
													{
														if(!txtPermitBy.get(ar).getValue().toString().isEmpty())
														{
															tbChk.get(ar).setEnabled(true);
														}
														else
														{
															tbChk.get(ar).setEnabled(false);
															tbChk.get(ar).setValue(false);
														}
													}
													else
													{
														tbChk.get(ar).setEnabled(false);
														tbChk.get(ar).setValue(false);
													}
												}
												else
												{
													tbChk.get(ar).setEnabled(false);
													tbChk.get(ar).setValue(false);
												}
											}
											else
											{
												tbChk.get(ar).setEnabled(false);
												tbChk.get(ar).setValue(false);
											}
										}
										else
										{
											tbChk.get(ar).setEnabled(false);
											tbChk.get(ar).setValue(false);
										}
									}
									else
									{
										tbChk.get(ar).setEnabled(false);
										tbChk.get(ar).setValue(false);
									}
								}
								else
								{
									tbChk.get(ar).setEnabled(false);
									tbChk.get(ar).setValue(false);
								}
							}
							else
							{
								tbChk.get(ar).setEnabled(false);
								tbChk.get(ar).setValue(false);
							}
						}
						else
						{
							tbChk.get(ar).setEnabled(false);
							tbChk.get(ar).setValue(false);
						}
					}
					else
					{
						tbChk.get(ar).setEnabled(false);
						tbChk.get(ar).setValue(false);
					}
				}
				else
				{
					tbChk.get(ar).setEnabled(false);
					tbChk.get(ar).setValue(false);
				}
			}
		});

		outTime3.add(ar, new TimeField());
		outTime3.get(ar).setWidth("32px");
		outTime3.get(ar).setHeight("20px");
		outTime3.get(ar).setInputPrompt("ss");
		outTime3.get(ar).setImmediate(true);
		outTime3.get(ar).setStyleName("time");
		outTime3.get(ar).setMaxLength(2);
		outTime3.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!outTime3.get(ar).getValue().toString().isEmpty())
				{			
					if(Double.parseDouble(outTime3.get(ar).getValue().toString())>=60)
					{
						outTime3.get(ar).setValue("");
					}
					else{
						outTime3.get(ar).setValue(outTime3.get(ar).getValue().toString());
					}
				}
				if(cmbShiftStatus.get(ar).getValue()!=null)
				{
					if(!inTime1.get(ar).getValue().toString().isEmpty())
					{
						if(!inTime2.get(ar).getValue().toString().isEmpty())
						{
							if(!inTime3.get(ar).getValue().toString().isEmpty())
							{
								if(!inAmPm.get(ar).getValue().toString().isEmpty())
								{
									if(!outTime1.get(ar).getValue().toString().isEmpty())
									{
										if(!outTime2.get(ar).getValue().toString().isEmpty())
										{
											if(!outTime3.get(ar).getValue().toString().isEmpty())
											{
												if(!outAmPm.get(ar).getValue().toString().isEmpty())
												{
													if(!txtReason.get(ar).getValue().toString().isEmpty())
													{
														if(!txtPermitBy.get(ar).getValue().toString().isEmpty())
														{
															tbChk.get(ar).setEnabled(true);
														}
														else
														{
															tbChk.get(ar).setEnabled(false);
															tbChk.get(ar).setValue(false);
														}
													}
													else
													{
														tbChk.get(ar).setEnabled(false);
														tbChk.get(ar).setValue(false);
													}
												}
												else
												{
													tbChk.get(ar).setEnabled(false);
													tbChk.get(ar).setValue(false);
												}
											}
											else
											{
												tbChk.get(ar).setEnabled(false);
												tbChk.get(ar).setValue(false);
											}
										}
										else
										{
											tbChk.get(ar).setEnabled(false);
											tbChk.get(ar).setValue(false);
										}
									}
									else
									{
										tbChk.get(ar).setEnabled(false);
										tbChk.get(ar).setValue(false);
									}
								}
								else
								{
									tbChk.get(ar).setEnabled(false);
									tbChk.get(ar).setValue(false);
								}
							}
							else
							{
								tbChk.get(ar).setEnabled(false);
								tbChk.get(ar).setValue(false);
							}
						}
						else
						{
							tbChk.get(ar).setEnabled(false);
							tbChk.get(ar).setValue(false);
						}
					}
					else
					{
						tbChk.get(ar).setEnabled(false);
						tbChk.get(ar).setValue(false);
					}
				}
				else
				{
					tbChk.get(ar).setEnabled(false);
					tbChk.get(ar).setValue(false);
				}
			}
		});

		outAmPm.add(ar, new TextField(""));
		outAmPm.get(ar).setWidth("28px");
		outAmPm.get(ar).setHeight("-1px");
		outAmPm.get(ar).setInputPrompt("PM");
		outAmPm.get(ar).setImmediate(true);
		outAmPm.get(ar).setStyleName("timeCombo");
		outAmPm.get(ar).setMaxLength(2);
		outAmPm.get(ar).addListener(new TextChangeListener() {
			public void textChange(TextChangeEvent event)
			{
				if(event.getText().equalsIgnoreCase("A"))
				{
					outAmPm.get(ar).setValue("AM");
				}
				if(event.getText().equalsIgnoreCase("P"))
				{
					outAmPm.get(ar).setValue("PM");
				}
				if(cmbShiftStatus.get(ar).getValue()!=null)
				{
					if(!inTime1.get(ar).getValue().toString().isEmpty())
					{
						if(!inTime2.get(ar).getValue().toString().isEmpty())
						{
							if(!inTime3.get(ar).getValue().toString().isEmpty())
							{
								if(!inAmPm.get(ar).getValue().toString().isEmpty())
								{
									if(!outTime1.get(ar).getValue().toString().isEmpty())
									{
										if(!outTime2.get(ar).getValue().toString().isEmpty())
										{
											if(!outTime3.get(ar).getValue().toString().isEmpty())
											{
												if(!outAmPm.get(ar).getValue().toString().isEmpty())
												{
													if(!txtReason.get(ar).getValue().toString().isEmpty())
													{
														if(!txtPermitBy.get(ar).getValue().toString().isEmpty())
														{
															tbChk.get(ar).setEnabled(true);
														}
														else
														{
															tbChk.get(ar).setEnabled(false);
															tbChk.get(ar).setValue(false);
														}
													}
													else
													{
														tbChk.get(ar).setEnabled(false);
														tbChk.get(ar).setValue(false);
													}
												}
												else
												{
													tbChk.get(ar).setEnabled(false);
													tbChk.get(ar).setValue(false);
												}
											}
											else
											{
												tbChk.get(ar).setEnabled(false);
												tbChk.get(ar).setValue(false);
											}
										}
										else
										{
											tbChk.get(ar).setEnabled(false);
											tbChk.get(ar).setValue(false);
										}
									}
									else
									{
										tbChk.get(ar).setEnabled(false);
										tbChk.get(ar).setValue(false);
									}
								}
								else
								{
									tbChk.get(ar).setEnabled(false);
									tbChk.get(ar).setValue(false);
								}
							}
							else
							{
								tbChk.get(ar).setEnabled(false);
								tbChk.get(ar).setValue(false);
							}
						}
						else
						{
							tbChk.get(ar).setEnabled(false);
							tbChk.get(ar).setValue(false);
						}
					}
					else
					{
						tbChk.get(ar).setEnabled(false);
						tbChk.get(ar).setValue(false);
					}
				}
				else
				{
					tbChk.get(ar).setEnabled(false);
					tbChk.get(ar).setValue(false);
				}
			}

		});

		txtReason.add(ar, new TextField(""));
		txtReason.get(ar).setWidth("100%");
		txtReason.get(ar).setImmediate(true);
		txtReason.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{

				if(cmbShiftStatus.get(ar).getValue()!=null)
				{
					if(!inTime1.get(ar).getValue().toString().isEmpty())
					{
						if(!inTime2.get(ar).getValue().toString().isEmpty())
						{
							if(!inTime3.get(ar).getValue().toString().isEmpty())
							{
								if(!inAmPm.get(ar).getValue().toString().isEmpty())
								{
									if(!outTime1.get(ar).getValue().toString().isEmpty())
									{
										if(!outTime2.get(ar).getValue().toString().isEmpty())
										{
											if(!outTime3.get(ar).getValue().toString().isEmpty())
											{
												if(!outAmPm.get(ar).getValue().toString().isEmpty())
												{
													if(!txtReason.get(ar).getValue().toString().isEmpty())
													{
														if(!txtPermitBy.get(ar).getValue().toString().isEmpty())
														{
															tbChk.get(ar).setEnabled(true);
														}
														else
														{
															tbChk.get(ar).setEnabled(false);
															tbChk.get(ar).setValue(false);
														}
													}
													else
													{
														tbChk.get(ar).setEnabled(false);
														tbChk.get(ar).setValue(false);
													}
												}
												else
												{
													tbChk.get(ar).setEnabled(false);
													tbChk.get(ar).setValue(false);
												}
											}
											else
											{
												tbChk.get(ar).setEnabled(false);
												tbChk.get(ar).setValue(false);
											}
										}
										else
										{
											tbChk.get(ar).setEnabled(false);
											tbChk.get(ar).setValue(false);
										}
									}
									else
									{
										tbChk.get(ar).setEnabled(false);
										tbChk.get(ar).setValue(false);
									}
								}
								else
								{
									tbChk.get(ar).setEnabled(false);
									tbChk.get(ar).setValue(false);
								}
							}
							else
							{
								tbChk.get(ar).setEnabled(false);
								tbChk.get(ar).setValue(false);
							}
						}
						else
						{
							tbChk.get(ar).setEnabled(false);
							tbChk.get(ar).setValue(false);
						}
					}
					else
					{
						tbChk.get(ar).setEnabled(false);
						tbChk.get(ar).setValue(false);
					}
				}
				else
				{
					tbChk.get(ar).setEnabled(false);
					tbChk.get(ar).setValue(false);
				}
			}
		});

		txtPermitBy.add(ar, new TextField(""));
		txtPermitBy.get(ar).setWidth("100%");
		txtPermitBy.get(ar).setImmediate(true);
		txtPermitBy.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbShiftStatus.get(ar).getValue()!=null)
				{
					if(!inTime1.get(ar).getValue().toString().isEmpty())
					{
						if(!inTime2.get(ar).getValue().toString().isEmpty())
						{
							if(!inTime3.get(ar).getValue().toString().isEmpty())
							{
								if(!inAmPm.get(ar).getValue().toString().isEmpty())
								{
									if(!outTime1.get(ar).getValue().toString().isEmpty())
									{
										if(!outTime2.get(ar).getValue().toString().isEmpty())
										{
											if(!outTime3.get(ar).getValue().toString().isEmpty())
											{
												if(!outAmPm.get(ar).getValue().toString().isEmpty())
												{
													if(!txtReason.get(ar).getValue().toString().isEmpty())
													{
														if(!txtPermitBy.get(ar).getValue().toString().isEmpty())
														{
															tbChk.get(ar).setEnabled(true);
														}
														else
														{
															tbChk.get(ar).setEnabled(false);
															tbChk.get(ar).setValue(false);
														}
													}
													else
													{
														tbChk.get(ar).setEnabled(false);
														tbChk.get(ar).setValue(false);
													}
												}
												else
												{
													tbChk.get(ar).setEnabled(false);
													tbChk.get(ar).setValue(false);
												}
											}
											else
											{
												tbChk.get(ar).setEnabled(false);
												tbChk.get(ar).setValue(false);
											}
										}
										else
										{
											tbChk.get(ar).setEnabled(false);
											tbChk.get(ar).setValue(false);
										}
									}
									else
									{
										tbChk.get(ar).setEnabled(false);
										tbChk.get(ar).setValue(false);
									}
								}
								else
								{
									tbChk.get(ar).setEnabled(false);
									tbChk.get(ar).setValue(false);
								}
							}
							else
							{
								tbChk.get(ar).setEnabled(false);
								tbChk.get(ar).setValue(false);
							}
						}
						else
						{
							tbChk.get(ar).setEnabled(false);
							tbChk.get(ar).setValue(false);
						}
					}
					else
					{
						tbChk.get(ar).setEnabled(false);
						tbChk.get(ar).setValue(false);
					}
				}
				else
				{
					tbChk.get(ar).setEnabled(false);
					tbChk.get(ar).setValue(false);
				}
			}
		});

		lbEmpId.add(ar, new Label(""));
		lbEmpId.get(ar).setWidth("100%");
		lbEmpId.get(ar).setImmediate(true);
		lbEmpId.get(ar).setHeight("-1px");

		lbEmpName.add(ar, new Label(""));
		lbEmpName.get(ar).setWidth("100%");
		lbEmpName.get(ar).setImmediate(true);
		lbEmpName.get(ar).setHeight("-1px");

		lbDeg.add(ar, new Label(""));
		lbDeg.get(ar).setWidth("100%");
		lbDeg.get(ar).setImmediate(true);
		lbDeg.get(ar).setHeight("-1px");

		lbShiftId.add(ar, new Label(""));
		lbShiftId.get(ar).setWidth("100%");
		lbShiftId.get(ar).setImmediate(true);
		lbShiftId.get(ar).setHeight("-1px");

		inTime.add(ar, new PopupDateField());
		inTime.get(ar).setWidth("100%");
		inTime.get(ar).setImmediate(true);
		inTime.get(ar).setDateFormat("dd-MM-yyyy hh:mm:ss aa");

		outTime.add(ar, new PopupDateField());
		outTime.get(ar).setWidth("100%");
		outTime.get(ar).setImmediate(true);
		outTime.get(ar).setDateFormat("dd-MM-yyyy hh:mm:ss aa");

		lbAttendDate.add(ar, new Label(""));
		lbAttendDate.get(ar).setWidth("100%");
		lbAttendDate.get(ar).setImmediate(true);

		date.add(ar, new PopupDateField());
		date.get(ar).setWidth("100%");
		date.get(ar).setImmediate(true);
		date.get(ar).setResolution(PopupDateField.RESOLUTION_DAY);
		date.get(ar).setDateFormat("yyyy-MM-dd");

		tbChk.add(ar, new CheckBox(""));
		tbChk.get(ar).setWidth("100%");
		tbChk.get(ar).setImmediate(true);
		tbChk.get(ar).setEnabled(false);
		tbChk.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(tbChk.get(ar).booleanValue())
				{
					count++;
				}
				else
				{
					count=0;
				}
			}
		});

		table.addItem(new Object[]{lbDate.get(ar),cmbShiftStatus.get(ar),inDate.get(ar),inTime1.get(ar),inTime2.get(ar),inTime3.get(ar),
				inAmPm.get(ar),outDate.get(ar),outTime1.get(ar),outTime2.get(ar),outTime3.get(ar),outAmPm.get(ar), //amtHour.get(ar),
				txtReason.get(ar),txtPermitBy.get(ar),lbEmpId.get(ar),lbEmpName.get(ar),lbDeg.get(ar),lbShiftId.get(ar),
				inTime.get(ar),outTime.get(ar),lbAttendDate.get(ar),date.get(ar),tbChk.get(ar)},ar);
	}

	private void btnIni(boolean t)
	{
		button.btnNew.setEnabled(t);
		button.btnEdit.setEnabled(t);
		button.btnSave.setEnabled(!t);
		button.btnRefresh.setEnabled(!t);
		button.btnDelete.setEnabled(t);
		button.btnFind.setEnabled(t);;
	}

	private void componentIni(boolean b) 
	{
		dDate.setEnabled(!b);
		cmbEmployeeName.setEnabled(!b);

		if(isFind==false)
		{
			cmbDeptName.setEnabled(!b);
		}
		else
		{
			cmbDeptName.setEnabled(b);
		}
		table.setEnabled(!b);
	}

	private void txtClear()
	{
		dDate.setValue(new java.util.Date());
		cmbDeptName.setValue(null);
		cmbEmployeeName.setValue(null);
		chkAll.setValue(false);
		lblShift.setValue("");
		lblShiftTime.setValue("");
		tableclear();
	}

	private void tableclear()
	{
		for(int i =0; i<lbDate.size(); i++)
		{
			lbDate.get(i).setValue(null);
			txtPermitBy.get(i).setValue("");
			txtReason.get(i).setValue("");
			inAmPm.get(i).setValue("");
			inTime1.get(i).setValue("");
			inTime2.get(i).setValue("");
			inTime3.get(i).setValue("");
			outAmPm.get(i).setValue("");
			outTime1.get(i).setValue("");
			outTime2.get(i).setValue("");
			outTime3.get(i).setValue("");
			lbEmpId.get(i).setValue("");
			lbEmpName.get(i).setValue("");
			cmbShiftStatus.get(i).setReadOnly(false);
			cmbShiftStatus.get(i).setValue(null);
			//inTime.get(i).setValue("");
			//outTime.get(i).setValue("");
			tbChk.get(i).setValue(false);
		}
	}

	private void focusEnter()
	{
		allComp.add(dDate);
		allComp.add(cmbDeptName);
		allComp.add(RadioBtnGroup);
		allComp.add(cmbEmployeeName);

		for(int i=0; i<lbDate.size();i++)
		{
			allComp.add(inTime1.get(i));
			allComp.add(inTime2.get(i));
			allComp.add(inTime3.get(i));
			allComp.add(inAmPm.get(i));
			allComp.add(outTime1.get(i));
			allComp.add(outTime2.get(i));
			allComp.add(outTime3.get(i));
			allComp.add(outAmPm.get(i));
			allComp.add(txtReason.get(i));
			allComp.add(txtPermitBy.get(i));
			allComp.add(tbChk.get(i));
		}

		allComp.add(button.btnSave);
		allComp.add(dDate);
		new FocusMoveByEnter(this,allComp);
	}

	private void SectionDataAdd()
	{
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			String query = "select AutoID, SectionName from tbSectionInfo";
			List list = session.createSQLQuery(query).list();

			for (Iterator iter = list.iterator(); iter.hasNext();)
			{
				Object[] element =  (Object[]) iter.next();	
				cmbDeptName.addItem(element[0]);
				cmbDeptName.setItemCaption(element[0], element[1].toString());	
			}
			tx.commit();
		}

		catch(Exception ex){
			showNotification("SectionDataAdd", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void addCmbEmployeeData(String query)
	{

		cmbEmployeeName.removeAllItems();

		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx;

		tx = session.beginTransaction();

		List list = session.createSQLQuery(query).list();

		for(Iterator iter = list.iterator(); iter.hasNext();)
		{
			Object[] element = (Object[]) iter.next();

			cmbEmployeeName.addItem(element[0].toString());
			cmbEmployeeName.setItemCaption(element[0], element[1].toString());
		}
	}

	private void updateAction() 
	{
		System.out.println("Update");
		if (!lbDate.get(0).getValue().toString().isEmpty()) 
		{
			btnIni(false);
			componentIni(false);
		} 
		else
		{
			this.getParent().showNotification("Update Failed","There are no data for update.",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void refreshButtonEvent() 
	{
		componentIni(true);
		btnIni(true);
		txtClear();	
	}

	private void SetMonthlyAttendance()
	{
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery("select * FROM [funMonthlyAttEdit]('"+df.format(dDate.getValue())+"','"+cmbEmployeeName.getValue()+"')  ").list();

			Iterator iter=list.iterator();

			int i = 0;
			for(; iter.hasNext();)
			{

				lblShift = new Label("<font color='#0C3C76'><b><Strong>Current Shift Time :<Strong></b></font>");
				lblShift.setImmediate(true);
				lblShift.setWidth("100%");
				lblShift.setVisible(true);
				lblShift.setContentMode(Label.CONTENT_XHTML);
				mainLayout.addComponent(lblShift, "top:45px;left:670px;");

				tbelement = (Object[]) iter.next();

				inTime.get(i).setValue((tbelement[21]));
				outTime.get(i).setValue(tbelement[22]);

				lblShiftTime.setVisible(true);
				lblShiftTime.setValue(tbelement[15]+" ("+time12.format(tbelement[16])+" - "+time12.format(tbelement[17])+") ");

				inTime1.get(i).setValue((tbelement[3]));
				inTime2.get(i).setValue(tbelement[4]);
				inTime3.get(i).setValue(tbelement[5]);
				inAmPm.get(i).setValue(tbelement[6]);

				outTime1.get(i).setValue(tbelement[7]);
				outTime2.get(i).setValue(tbelement[8]);
				outTime3.get(i).setValue(tbelement[9]);
				outAmPm.get(i).setValue(tbelement[10]);

				inTime.get(i).setValue(tbelement[21]);
				outTime.get(i).setValue(tbelement[22]);

				lbAttendDate.get(i).setValue(tbelement[23]);
				lbEmpId.get(i).setValue(tbelement[24]);
				lbEmpName.get(i).setValue(tbelement[2]);
				lbDeg.get(i).setValue(tbelement[25]);
				lbShiftId.get(i).setValue(tbelement[26]);
				date.get(i).setValue(tbelement[0]);
				inDate.get(i).setValue(tbelement[21]);
				outDate.get(i).setValue(tbelement[22]);

				if(tbelement[14].toString().equalsIgnoreCase("Friday") || 
						tbelement[14].toString().equalsIgnoreCase("Holiday") || 
						tbelement[14].toString().equalsIgnoreCase("Friday,Holiday")  )
				{
					lbDate.get(i).setValue(Dformat.format(tbelement[0]));
					lbDate.get(i).setContentMode(Label.CONTENT_XHTML);
					lbDate.get(i).setStyleName("dateRed");
				}

				else
				{
					if( tbelement[3].toString().equalsIgnoreCase("") &&
							tbelement[4].toString().equalsIgnoreCase("") && 
							tbelement[5].toString().equalsIgnoreCase("") && 
							tbelement[6].toString().equalsIgnoreCase("") && 
							tbelement[7].toString().equalsIgnoreCase("") && 
							tbelement[8].toString().equalsIgnoreCase("") &&
							tbelement[9].toString().equalsIgnoreCase("") && 
							tbelement[10].toString().equalsIgnoreCase("") )

					{
						lbDate.get(i).setValue(Dformat. format(tbelement[0]));
						lbDate.get(i).setContentMode(Label.CONTENT_XHTML);
						lbDate.get(i).setStyleName("dateBlue");
					}

					else if(tbelement[3].toString().equalsIgnoreCase("") &&
							tbelement[4].toString().equalsIgnoreCase("") && 
							tbelement[5].toString().equalsIgnoreCase("") && 
							tbelement[6].toString().equalsIgnoreCase("") || 
							tbelement[7].toString().equalsIgnoreCase("") && 
							tbelement[8].toString().equalsIgnoreCase("") &&
							tbelement[9].toString().equalsIgnoreCase("") && 
							tbelement[10].toString().equalsIgnoreCase("") )

					{
						lbDate.get(i).setValue(Dformat.format(tbelement[0]));
						lbDate.get(i).setContentMode(Label.CONTENT_XHTML);
						lbDate.get(i).setStyleName("dateBrown");
					}

					else
					{
						lbDate.get(i).setValue(Dformat.format(tbelement[0]));
						lbDate.get(i).setStyleName("dateGreen");
					}
				}

				if(tbelement[26].toString().isEmpty())
				{
					cmbShiftStatus.get(i).setReadOnly(false);
					cmbShiftStatus.get(i).setImmediate(true);
				}

				else{
					cmbShiftStatus.get(i).setReadOnly(false);
					cmbShiftStatus.get(i).setValue(tbelement[26]);
					cmbShiftStatus.get(i).setImmediate(true);
					cmbShiftStatus.get(i).setReadOnly(true);
				}

				if(cmbShiftStatus.get(i).getValue()==null)
				{
					inDate.get(i).setValue(tbelement[0]);
					outDate.get(i).setValue(tbelement[0]);
				}
				if(inTime1.get(i).getValue().equals("") && inTime2.get(i).getValue().equals("") && inTime3.get(i).getValue().equals(""))
				{
					inDate.get(i).setValue(tbelement[0]);
				}

				if(outTime1.get(i).getValue().equals("") && outTime2.get(i).getValue().equals("") && outTime3.get(i).getValue().equals(""))
				{
					outDate.get(i).setValue(tbelement[0]);
				}

				if(lbDate.size()-1==i)
				{
					tableRowAdd(i+1);
					addCmbShiftData(i+1);
				}
				i++;			
			}

		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void tablePermitDataAdd(String Permit )
	{
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			String query =  "select 0,ei.vEmployeeName from tbEmployeeInfo ei where ei.vEmployeeId='"+Permit+"' ";

			List list = session.createSQLQuery(query).list();

			if(!list.isEmpty())
			{
				for(Iterator iter = list.iterator(); iter.hasNext();)
				{				  
					Object[] element = (Object[]) iter.next();
					int i=0;
					for(int j=0;j<lbDate.size();j++)
					{
						if(txtPermitBy.get(j).getValue().toString().isEmpty()){

							i=j;
							t=true;
							break;
						}
					}
					if(t){
						txtPermitBy.get(i).setValue(element[1]);

						if((i)==lbDate.size()-1)
						{
							tableRowAdd(i+1);
						}
						i++;
					}
				}
			}
			else
			{
				tableclear();
				this.getParent().showNotification("Warning!","No employee found", Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch (Exception ex)
		{
			this.getParent().showNotification("Khan", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void newButtonEvent() 
	{
		dDate.focus();
		componentIni(false);
		btnIni(false);
		txtClear();
		count=0;
	}

	private void saveButtonAction()
	{
		try
		{
			MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to save information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						Transaction tx;
						Session session = SessionFactoryUtil.getInstance().getCurrentSession();
						tx = session.beginTransaction();
						insertData(tx,session);
					}
				}
			});

		}
		catch(Exception ex)
		{
			this.getParent().showNotification("Error.", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void insertData (Transaction tx, Session session)
	{
		try
		{
			String toDate = df.format(dDate.getValue());
			String chkquery = "";
			String queryEmployeeAttendanceInTime = "";
			String queryEmployeeAttendanceOutTime = "";
			String queryUDEmployeeAttendanceNew = "";
			String queryUDEmployeeAttendanceOutNew = "";
			String queryUDEmployeeAttendanceUpdate = "";
			String query1 = "";
			String query2 = "";
			String query3 = "";
			String pFlag = "";

			for(int i=0; i<lbDate.size(); i++)
			{
				if(tbChk.get(i).booleanValue())
				{
					String	inDateTimeInput=dform.format(inDate.get(i).getValue())+" "+inTime1.get(i).getValue()+':'+inTime2.get(i).getValue()+':'+inTime3.get(i).getValue()+" "+inAmPm.get(i).getValue();
					String  outDateTimeInput=dform.format(outDate.get(i).getValue())+" "+outTime1.get(i).getValue()+':'+outTime2.get(i).getValue()+':'+outTime3.get(i).getValue()+" "+outAmPm.get(i).getValue();

					String inDateTimeForm  = inOutDateTime.format(inTime.get(i).getValue());
					String outDateTimeForm = inOutDateTime.format(outTime.get(i).getValue());

					if(!inDateTimeInput.toString().equals(inDateTimeForm.toString()) )
					{

						System.out.println("InDateTimeFormat= "+ inOutDateTime.format(inTime.get(i).getValue()));
						System.out.println("InDateTimeInput=  "+ inDateTimeInput);

						System.out.println("OutDateTimeFormat= "+ inOutDateTime.format(outTime.get(i).getValue()));
						System.out.println("OutDateTimeInput= "+ outDateTimeInput);

						queryUDEmployeeAttendanceNew = " insert into tbUDEmployeeAttendance select GETDATE()," +
								" EmpID,vFigId,vEmployeeName,vDeginationName,sectionIdDb," +
								" sectionName,dAttDate,isnull(tIntime,''),isnull(tOutTime,''),'','',vShiftID,'New','Absent'," +
								"'"+sessionBean.getUserId()+"'," +
								"'"+sessionBean.getUserIp()+"',GETDATE() from [funAttendanceReport]" +
								" ('"+date.get(i).getValue()+"','"+cmbDeptName.getValue()+"','"+cmbEmployeeName.getValue()+"')";

						/*queryEmployeeAttendanceInTime="INSERT INTO tbEmployeeAttendance values( GETDATE(), " +
								" '"+cmbEmployeeName.getValue()+"', " +
								" '"+lbEmpName.get(i).getValue().toString()+"', " +
								" '"+lbDeg.get(i).getValue()+"', " +
								" '"+cmbDeptName.getValue()+"', " +
								" '"+cmbDeptName.getItemCaption(cmbDeptName.getValue()).toString()+"', " +
								" '"+date.get(i).getValue().toString()+"', " +
								" (select convert(datetime,'"+df.format(inDate.get(i).getValue())+"'+cast('"+inTime1.get(i).getValue()+"'+':'+'"+inTime2.get(i).getValue()+"'+':'+'"+inTime3.get(i).getValue()+"'+' '+'"+inAmPm.get(i).getValue()+"' as datetime))), " +
								" '1900-01-01 00:00:00.000', " +
								" '"+sessionBean.getUserId()+"', " +
								" '"+sessionBean.getUserIp()+"', CURRENT_TIMESTAMP,"+
								" 'Update' ,"+
								" '"+txtPermitBy.get(i).getValue().toString()+"', " +
								" '"+txtReason.get(i).getValue().toString()+"', " +
								" '"+cmbShiftStatus.get(i).getValue().toString()+"' ," +
								" (select OtStatus from tbEmployeeInfo where vSectionId='"+cmbDeptName.getValue()+"' and iFingerID='"+cmbEmployeeName.getValue().toString()+"'),"+
								" 'IN' )" ;*/

						queryEmployeeAttendanceInTime="INSERT INTO tbEmployeeAttendance values( GETDATE(), " +
								" '"+cmbEmployeeName.getValue()+"', " +
								" '"+lbEmpName.get(i).getValue().toString()+"', " +
								" '"+lbDeg.get(i).getValue()+"', " +
								" '"+cmbDeptName.getValue()+"', " +
								" '"+cmbDeptName.getItemCaption(cmbDeptName.getValue()).toString()+"', " +
								" '"+date.get(i).getValue().toString()+"', " +
								" (select convert(datetime,'"+df.format(inDate.get(i).getValue())+"'+cast('"+inTime1.get(i).getValue()+"'+':'+'"+inTime2.get(i).getValue()+"'+':'+'"+inTime3.get(i).getValue()+"'+'"+inAmPm.get(i).getValue()+"' as datetime))), " +
								" '1900-01-01 00:00:00.000', " +
								" '"+sessionBean.getUserId()+"', " +
								" '"+sessionBean.getUserIp()+"', CURRENT_TIMESTAMP,"+
								" 'Update' ,"+
								" '"+txtPermitBy.get(i).getValue().toString()+"', " +
								" '"+txtReason.get(i).getValue().toString()+"', " +
								" '"+cmbShiftStatus.get(i).getValue().toString()+"' ," +
								"(select OtStatus from tbEmployeeInfo where vSectionId='"+cmbDeptName.getValue()+"' and iFingerID='"+cmbEmployeeName.getValue().toString()+"'),"+
								" 'IN' )" ;

						queryUDEmployeeAttendanceUpdate="INSERT INTO tbUDEmployeeAttendance values(GETDATE(), " +
								" '"+lbEmpId.get(i).getValue()+"', " +
								" '"+cmbEmployeeName.getValue()+"', " +
								" '"+lbEmpName.get(i).getValue().toString()+"', " +
								" '"+lbDeg.get(i).getValue()+"', " +
								" '"+cmbDeptName.getValue()+"', " +
								" '"+cmbDeptName.getItemCaption(cmbDeptName.getValue()).toString()+"', " +
								" '"+date.get(i).getValue().toString()+"', " +
								" (select convert(datetime,'"+df.format(inDate.get(i).getValue())+"'+cast('"+inTime1.get(i).getValue()+"'+':'+'"+inTime2.get(i).getValue()+"'+':'+'"+inTime3.get(i).getValue()+"'+'"+inAmPm.get(i).getValue()+"' as datetime))), " +
								" (select convert(datetime,'"+df.format(outDate.get(i).getValue())+"'+cast('"+outTime1.get(i).getValue()+"'+':'+'"+outTime2.get(i).getValue()+"'+':'+'"+outTime3.get(i).getValue()+"'+'"+outAmPm.get(i).getValue()+"' as datetime))), " +
								" '"+txtPermitBy.get(i).getValue().toString()+"', " +
								" '"+txtReason.get(i).getValue().toString()+"', " +
								" '"+cmbShiftStatus.get(i).getValue().toString()+"', " +
								" 'Update',"+
								" 'Absent',"+
								" '"+sessionBean.getUserId()+"', " +
								" '"+sessionBean.getUserIp()+"', CURRENT_TIMESTAMP)";

						System.out.println("queryUDEmployeeAttendanceNew "+queryUDEmployeeAttendanceNew);
						session.createSQLQuery(queryUDEmployeeAttendanceNew).executeUpdate();

						System.out.println("queryEmployeeAttendanceInTime "+queryEmployeeAttendanceInTime);
						session.createSQLQuery(queryEmployeeAttendanceInTime).executeUpdate();

						System.out.println("queryUDEmployeeAttendanceUpdate "+queryUDEmployeeAttendanceUpdate);
						session.createSQLQuery(queryUDEmployeeAttendanceUpdate).executeUpdate();
					}

					if(!outDateTimeInput.toString().equalsIgnoreCase(outDateTimeForm.toString()) )
					{				

						queryUDEmployeeAttendanceNew = " insert into tbUDEmployeeAttendance select GETDATE()," +
								" EmpID,vFigId,vEmployeeName,vDeginationName,sectionIdDb," +
								" sectionName,dAttDate,isnull(tIntime,''),isnull(tOutTime,''),'','',vShiftID,'New','Absent','"+sessionBean.getUserId()+"'," +
								"'"+sessionBean.getUserIp()+"',GETDATE() from [funAttendanceReport]" +
								" ('"+date.get(i).getValue()+"','"+cmbDeptName.getValue()+"','"+cmbEmployeeName.getValue()+"')";

						queryEmployeeAttendanceOutTime="INSERT INTO tbEmployeeAttendanceFinal (dDate,vEmployeeID," +
								"vEmployeeCode,vProximityID,vFingerID,vEmployeeName,vSectionId,vSectionName," +
								"vDesignationID,vDesignationName,iDesignationSerial,vShiftID,vShiftName," +
								"dInTimeFirst,dOutTimeFirst,dInTimeSecond,dOutTimeSecond,dInTimeThird,dOutTimeThird," +
								"vEditFlag,vAttendFlag,bOtStatus,vUserId,vUserIp,dEntryTime) values( '"+df.format(lbAttendDate.get(i).getValue())+"', " +
								" '"+cmbEmployeeName.getValue()+"', " +
								" '"+lbEmpName.get(i).getValue().toString()+"', " +
								" '"+lbDeg.get(i).getValue()+"', " +
								" '"+cmbDeptName.getValue()+"', " +
								" '"+cmbDeptName.getItemCaption(cmbDeptName.getValue()).toString()+"', " +
								" '"+date.get(i).getValue().toString()+"', " +
								" (select convert(datetime,'"+df.format(outDate.get(i).getValue())+"'+cast('"+outTime1.get(i).getValue()+"'+':'+'"+outTime2.get(i).getValue()+"'+':'+'"+outTime3.get(i).getValue()+"'+'"+outAmPm.get(i).getValue()+"' as datetime))), " +
								" '1900-01-01 00:00:00.000', " +
								" '"+sessionBean.getUserId()+"', " +
								" '"+sessionBean.getUserIp()+"', CURRENT_TIMESTAMP,"+
								" 'Update' ,"+
								" '"+txtPermitBy.get(i).getValue().toString()+"', " +
								" '"+txtReason.get(i).getValue().toString()+"', " +
								" '"+cmbShiftStatus.get(i).getValue().toString()+"' ," +
								"(select OtStatus from tbEmployeeInfo where vSectionId='"+cmbDeptName.getValue()+"' and iFingerID='"+cmbEmployeeName.getValue().toString()+"'),"+
								" 'OUT' )" ;

						queryUDEmployeeAttendanceUpdate="INSERT INTO tbUDEmployeeAttendance values(GETDATE(), " +
								" '"+lbEmpId.get(i).getValue()+"', " +
								" '"+cmbEmployeeName.getValue()+"', " +
								" '"+lbEmpName.get(i).getValue().toString()+"', " +
								" '"+lbDeg.get(i).getValue()+"', " +
								" '"+cmbDeptName.getValue()+"', " +
								" '"+cmbDeptName.getItemCaption(cmbDeptName.getValue()).toString()+"', " +
								" '"+date.get(i).getValue().toString()+"', " +
								" (select convert(datetime,'"+df.format(inDate.get(i).getValue())+"'+cast('"+inTime1.get(i).getValue()+"'+':'+'"+inTime2.get(i).getValue()+"'+':'+'"+inTime3.get(i).getValue()+"'+'"+inAmPm.get(i).getValue()+"' as datetime))), " +
								" (select convert(datetime,'"+df.format(outDate.get(i).getValue())+"'+cast('"+outTime1.get(i).getValue()+"'+':'+'"+outTime2.get(i).getValue()+"'+':'+'"+outTime3.get(i).getValue()+"'+'"+outAmPm.get(i).getValue()+"' as datetime))), " +
								" '"+txtPermitBy.get(i).getValue().toString()+"', " +
								" '"+txtReason.get(i).getValue().toString()+"', " +
								" '"+cmbShiftStatus.get(i).getValue().toString()+"', " +
								" 'Update',"+
								" 'Absent',"+
								" '"+sessionBean.getUserId()+"', " +
								" '"+sessionBean.getUserIp()+"', CURRENT_TIMESTAMP)";

						System.out.println("queryUDEmployeeAttendanceNew "+queryUDEmployeeAttendanceNew);
						session.createSQLQuery(queryUDEmployeeAttendanceNew).executeUpdate();	

						System.out.println("queryEmployeeAttendanceOutTime "+queryEmployeeAttendanceOutTime);
						session.createSQLQuery(queryEmployeeAttendanceOutTime).executeUpdate();	

						System.out.println("queryUDEmployeeAttendanceUpdate "+queryUDEmployeeAttendanceUpdate);
						session.createSQLQuery(queryUDEmployeeAttendanceUpdate).executeUpdate();

					}
				}

			}

			tx.commit();
			this.getParent().showNotification("All Information Save Successfully");
			isUpdate=false;
			isFind = false;
			txtClear();
			componentIni(true);
			btnIni(true);
		}
		catch(Exception ex)
		{
			this.getParent().showNotification("Error1", ex.toString(), Notification.TYPE_WARNING_MESSAGE);
			tx.rollback();
		}
	}


	private boolean CheckIN(int ar)
	{
		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String query = " select dAttInTime from tbEmployeeAttendance where dAttInTime='"+inTime.get(ar).getValue()+"' " +
					" and vEmployeeId='"+cmbEmployeeName.getValue()+"' ";

			Iterator iter = session.createSQLQuery(query).list().iterator();

			if (iter.hasNext()) 
			{
				return true;
			}
		}
		catch (Exception ex) 
		{
			System.out.print(ex);
		}

		return false;
	}


	private boolean CheckOUT(int ar)
	{
		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String query = " select dAttInTime from tbEmployeeAttendance where  " +
					"  dAttInTime='"+outTime.get(ar).getValue()+"'  and vEmployeeId='"+cmbEmployeeName.getValue()+"' ";

			Iterator iter = session.createSQLQuery(query).list().iterator();

			if (iter.hasNext()) 
			{
				return true;
			}
		}
		catch (Exception ex) 
		{
			System.out.print(ex);
		}

		return false;
	}

	private void updateData()
	{
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			for(int i=0; i<lbDate.size(); i++)
			{
				if(!lbDate.get(i).getValue().toString().isEmpty())
				{
					if(!inTime1.get(i).getValue().toString().isEmpty() && !inTime2.get(i).getValue().toString().isEmpty() &&
							!inTime3.get(i).getValue().toString().isEmpty() && !inAmPm.get(i).getValue().toString().isEmpty() && 
							!outTime1.get(i).getValue().toString().isEmpty() && !outTime2.get(i).getValue().toString().isEmpty() &&
							!outTime3.get(i).getValue().toString().isEmpty() && !outAmPm.get(i).getValue().toString().isEmpty() &&
							!txtPermitBy.get(i).getValue().toString().isEmpty() && !txtReason.get(i).getValue().toString().isEmpty())

					{
						String updateData ="UPDATE tbEmployeeAttendance set" +
								" dAttInTime=(select cast('"+inTime1.get(i).getValue()+"'+':'+'"+inTime2.get(i).getValue()+"'+':'+'"+inTime3.get(i).getValue()+"'+'"+inAmPm.get(i).getValue()+"' as time)), " +
								" dAttOutTime=(select cast('"+outTime1.get(i).getValue()+"'+':'+'"+outTime2.get(i).getValue()+"'+':'+'"+outTime3.get(i).getValue()+"'+'"+outAmPm.get(i).getValue()+"' as time)), " +
								" vUserId='"+sessionBean.getUserId()+"', " +
								" vUserIP= '"+sessionBean.getUserIp()+"'," +
								" dEntryTime=CURRENT_TIMESTAMP," +
								" vStatus='Update'," +
								" vPermitBy='"+txtPermitBy.get(i).getValue().toString()+"', " +
								" vReason='"+txtReason.get(i).getValue().toString()+"'" +
								" where vEmployeeId='"+(lbDate.get(i).getValue().toString())+"' and " +
								"dAttDate='"+df.format(dDate.getValue())+"' ";

						System.out.println("UpdateProduct: "+updateData);
						session.createSQLQuery(updateData).executeUpdate();
					}
				}
			}
			//	}
			tx.commit();
			this.getParent().showNotification("All information update successfully.");
			componentIni(true);
			btnIni(true);
		}
		catch(Exception exp)
		{
			tx.rollback();
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);

		mainLayout.setWidth("1200px");
		mainLayout.setHeight("590px");

		lblDate = new Label();
		lblDate.setImmediate(true);
		lblDate.setWidth("-1px");
		lblDate.setHeight("-1px");
		lblDate.setValue("Month :");
		mainLayout.addComponent(lblDate, "top:20.0px;left:20.0px;");

		dDate = new PopupDateField();
		dDate.setValue(new java.util.Date());
		dDate.setWidth("130px");
		dDate.setHeight("24px");
		dDate.setResolution(PopupDateField.RESOLUTION_MONTH);
		dDate.setDateFormat("MMMM-yyyy");
		dDate.setInvalidAllowed(false);
		dDate.setInputPrompt("Month");
		dDate.setImmediate(true);
		mainLayout.addComponent(dDate, "top:18.0px;left:80.0px;");

		/*lblDeptName = new Label();
		lblDeptName.setImmediate(true);
		lblDeptName.setWidth("-1px");
		lblDeptName.setHeight("-1px");
		lblDeptName.setValue("Section Name :");
		mainLayout.addComponent(lblDeptName, "top:20.0px;left:220.0px;");
		 */
		cmbDeptName = new ComboBox();
		cmbDeptName.setImmediate(true);
		cmbDeptName.setWidth("210px");
		cmbDeptName.setHeight("24px");
		cmbDeptName.setImmediate(true);
		cmbDeptName.setInputPrompt("Section Name");
		cmbDeptName.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbDeptName, "top:18.0px;left:220.0px;");

		RadioBtnGroup = new OptionGroup("",type2);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		mainLayout.addComponent(RadioBtnGroup, "top:18.0px;left:450.0px;");

		/*	lblEmployee = new Label();
		lblEmployee.setImmediate(true);
		lblEmployee.setWidth("-1px");
		lblEmployee.setHeight("-1px");
		lblEmployee.setValue("Finger ID :");
		lblEmployee.setVisible(true);
		mainLayout.addComponent(lblEmployee, "top:20.0px;left:540.0px;");*/

		cmbEmployeeName = new ComboBox();
		cmbEmployeeName.setImmediate(true);
		cmbEmployeeName.setWidth("200px");
		cmbEmployeeName.setHeight("-1px");
		cmbEmployeeName.setInputPrompt("Employee ID");
		mainLayout.addComponent(cmbEmployeeName, "top:18.0px;left:750.0px;");

		lblShiftTime = new Label("");
		lblShiftTime.setImmediate(true);
		lblShiftTime.setWidth("100%");
		lblShiftTime.setContentMode(Label.CONTENT_XHTML);
		lblShiftTime.setVisible(false);
		mainLayout.addComponent(lblShiftTime,"top:45px;left:800px;");

		mainLayout.addComponent(table, "top:70.0px;left:20.0px;");

		txtColor = new TextRead();
		txtColor.setImmediate(true);
		txtColor.setWidth("274px");
		txtColor.setHeight("20px");
		txtColor.setStyleName("txtcolor");
		mainLayout.addComponent(txtColor, "top:71px;left:237px;");

		lblCl = new Label("<font color='#C1BF15'><b><Strong>In date & Time<Strong></b></font>");
		lblCl.setImmediate(true);
		lblCl.setWidth("130px");
		lblCl.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblCl, "top:74px;left:320px;");

		txtColor = new TextRead();
		txtColor.setImmediate(true);
		txtColor.setWidth("274px");
		txtColor.setHeight("20px");
		txtColor.setStyleName("txtcolor");
		mainLayout.addComponent(txtColor, "top:71px;left:515px;");

		lblCl = new Label("<font color='#C1BF15'><b><Strong>Out date & Time<Strong></b></font>");
		lblCl.setImmediate(true);
		lblCl.setWidth("130px");
		lblCl.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblCl, "top:74px;left:590px;");

		lblCl = new Label("<font color='#A00324'><b><Strong>Use 12-Hour Format<Strong></b></font>");
		lblCl.setImmediate(true);
		lblCl.setWidth("-1px");
		lblCl.setHeight("-1px");
		lblCl.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblCl, "top:520.0px;right:30px;");

		txtColorp = new TextRead();
		txtColorp.setImmediate(true);
		txtColorp.setWidth("164px");
		txtColorp.setHeight("20px");
		txtColorp.setVisible(false);
		txtColorp.setStyleName("txtcolor");
		mainLayout.addComponent(txtColorp, "top:101px;left:907px;");

		lblClp = new Label("<font color='#fff'><b><Strong>Permitted By<Strong></b></font>");
		lblClp.setImmediate(true);
		lblClp.setWidth("95px");
		lblClp.setVisible(false);
		lblClp.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblClp, "top:104px;left:940px;");

		/*lblPermitted = new Label();
		lblPermitted.setImmediate(true);
		lblPermitted.setWidth("-1px");
		lblPermitted.setHeight("-1px");
		lblPermitted.setValue("Permitted By:");
		lblPermitted.setVisible(true);
		mainLayout.addComponent(lblPermitted, "top:45.0px;left:420.0px;");

		cmbPermit = new ComboBox();
		cmbPermit.setImmediate(true);
		cmbPermit.setWidth("250px");
		cmbPermit.setHeight("-1px");
		mainLayout.addComponent(cmbPermit, "top:43.0px;left:520.0px;");
		 */
		lblClp = new Label("<font color='#216702' size='10px'><b><Strong>.<Strong></b></font>");
		lblClp.setImmediate(true);
		lblClp.setWidth("95px");
		lblClp.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblClp, "top:510px;left:10px;");

		lblClp = new Label("<font color='#0C3C76'>Present</font>");
		lblClp.setImmediate(true);
		lblClp.setWidth("95px");
		lblClp.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblClp, "top:522px;left:30px;");

		lblClp = new Label("<font color='#030F7B' size='10px'><b><Strong>.<Strong></b></font>");
		lblClp.setImmediate(true);
		lblClp.setWidth("95px");
		lblClp.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblClp, "top:510px;left:80px;");

		lblClp = new Label("<font color='#0C3C76'>Absent</font>");
		lblClp.setImmediate(true);
		lblClp.setWidth("95px");
		lblClp.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblClp, "top:522px;left:100px;");

		lblClp = new Label("<font color='#7A4201' size='10px'><b><Strong>.<Strong></b></font>");
		lblClp.setImmediate(true);
		lblClp.setWidth("95px");
		lblClp.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblClp, "top:510px;left:143px;");

		lblClp = new Label("<font color='#0C3C76'>Special Absents</font>");
		lblClp.setImmediate(true);
		lblClp.setWidth("95px");
		lblClp.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblClp, "top:522px;left:163px;");

		lblClp = new Label("<font color='#B2080C' size='10px'><b><Strong>.<Strong></b></font>");
		lblClp.setImmediate(true);
		lblClp.setWidth("95px");
		lblClp.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblClp, "top:510px;left:255px;");

		lblClp = new Label("<font color='#0C3C76'>Friday/Holiday</font>");
		lblClp.setImmediate(true);
		lblClp.setWidth("-1px");
		lblClp.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblClp, "top:522px;left:276px;");

		chkAll = new CheckBox();
		chkAll.setImmediate(true);
		mainLayout.addComponent(chkAll, "top:73px;left:1130px;");

		mainLayout.addComponent(button, "top:550.0px;left:350.0px;");

		return mainLayout;
	}
}
