package com.appform.hrmModule;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.BtUpload;
import com.common.share.CommonButton;
import com.common.share.CommonMethod;
import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;
import com.vaadin.data.Property.ValueChangeListener;

@SuppressWarnings("serial")
public class EmployeeAttendanceUploadSingleDevice extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Table table= new Table();
	private PopupDateField dWorkingDate ;
	private Label lblDate ;

	private ArrayList <Label> lblsl = new ArrayList<Label>();
	private ArrayList <Label> lblEmployeeId = new ArrayList<Label>(); 
	private ArrayList <Label> lblFingerId = new ArrayList<Label>();
	private ArrayList <Label> lblProximityId = new ArrayList<Label>();
	private ArrayList <Label> lblEmployeeName = new ArrayList<Label>();
	private ArrayList <Label> lblDesignationID = new ArrayList<Label>();
	private ArrayList <Label> lblDesignation = new ArrayList<Label>();
	private ArrayList <Label> lblUnitID = new ArrayList<Label>();
	private ArrayList <Label> lblUnit = new ArrayList<Label>();
	private ArrayList <Label> lblDepartmentID = new ArrayList<Label>();
	private ArrayList <Label> lblDepartment = new ArrayList<Label>();
	private ArrayList <Label> lblSectionID = new ArrayList<Label>();
	private ArrayList <Label> lblSection = new ArrayList<Label>();
	private ArrayList <PopupDateField> lblAttDate = new ArrayList<PopupDateField>();
	private ArrayList <Label> lblInOutTime = new ArrayList<Label>();
	ArrayList<Component> allComp = new ArrayList<Component>();	

	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dFormatBd = new SimpleDateFormat("M/d/yyyy");
	private SimpleDateFormat dFormatTextFile = new SimpleDateFormat("dd-MMM-yy");

	CommonButton button = new CommonButton("New", "Save", "", "","Refresh","","","","","Exit");
	String username="";

	private NativeButton btnShowFile;
	public BtUpload fileAttUpload;

	String empCode="";
	String empFingerId="";
	String empProxId="";
	String empDesId="";
	String tableName="";
	String inOut="";
	String type="";

	@SuppressWarnings("unused")
	private String fileName = "";
	Object[] element;

	private HashMap <String,Object> hmEmployeeID = new HashMap <String,Object> ();
	private HashMap <String,Object> hmProximityID = new HashMap <String,Object> ();
	private HashMap <String,Object> hmEmployeeName = new HashMap <String,Object> ();
	private HashMap <String,Object> hmDesignationID = new HashMap <String,Object> ();
	private HashMap <String,Object> hmDesignation = new HashMap <String,Object> ();
	private HashMap <String,Object> hmUnitID = new HashMap <String,Object> ();
	private HashMap <String,Object> hmUnit= new HashMap <String,Object> ();
	private HashMap <String,Object> hmDepartmentID = new HashMap <String,Object> ();
	private HashMap <String,Object> hmDepartment = new HashMap <String,Object> ();
	private HashMap <String,Object> hmSectionID = new HashMap <String,Object> ();
	private HashMap <String,Object> hmSection = new HashMap <String,Object> ();
	private CommonMethod cm;
	private String menuId = "";
	public EmployeeAttendanceUploadSingleDevice(SessionBean sessionBean,String menuId) 
	{
		this.sessionBean=sessionBean;
		this.setResizable(false);
		this.setCaption("EMPLOYEE ATTENDANCE UPLOAD :: " + sessionBean.getCompany());
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		buildMainLayout();
		setContent(mainLayout);
		tableinitialise();

		componentIni(true);
		btnIni(true);

		SetEventAction();
		authenticationCheck();
		dWorkingDate.focus();
		dataSaveToHashMap();
	}

	private void authenticationCheck()
	{
		cm.checkFormAction(menuId);
		if(!sessionBean.isSuperAdmin())
		{
		if(!sessionBean.isAdmin())
		{
			if(!cm.isSave)
			{button.btnSave.setVisible(false);}
			if(!cm.isEdit)
			{button.btnEdit.setVisible(false);}
			if(!cm.isDelete)
			{button.btnDelete.setVisible(false);}
			if(!cm.isPreview)
			{button.btnPreview.setVisible(false);}
		}
		}
	}


	private void SetEventAction()
	{
		button.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				componentIni(false);
				btnIni(false);
				button.btnSave.setEnabled(false);
				txtClear();
			}
		});

		button.btnRefresh.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				componentIni(true);
				btnIni(true);
				txtClear();
			}
		});

		button.btnSave.addListener( new Button.ClickListener() 
		{			
			public void buttonClick(ClickEvent event)
			{	
				if(!lblEmployeeId.get(0).getValue().toString().isEmpty())
				{
					saveBtnAction(event);
				}
				else
				{
					showNotification("Warning", "No Data Found!!!", Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		button.btnExit.addListener( new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				close();
			}
		});

		btnShowFile.addListener( new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				button.btnSave.setEnabled(false);
				tableClear();
				showData();
				button.btnSave.setEnabled(true);
			}
		});

		dWorkingDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(dWorkingDate.getValue()!=null)
				{
					System.out.println("DateFormat = "+ dFormatBd.format(dWorkingDate.getValue()));
				}
			}
		});
	}

	private void dataSaveToHashMap()
	{
		hmEmployeeID.clear();
		hmProximityID.clear();
		hmEmployeeName.clear();
		hmDesignationID.clear();
		hmDesignation.clear();
		hmDepartmentID.clear();
		hmDepartment.clear();
		hmUnitID.clear();
		hmUnit.clear();
		hmSectionID.clear();
		hmSection.clear();
		
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql="select vEmployeeId,vFingerId,vProximityId,vEmployeeName,vDesignationID,vDesignationName,vUnitId,vUnitName,vDepartmentId,vDepartmentName," +
					"vSectionId,vSectionName from tbEmpOfficialPersonalInfo where bStatus = 1 " +
					"order by CAST(SUBSTRING(vEmployeeId,5,LEN(vEmployeeId)) as int)";
			
			System.out.println("dataSaveToHashMap: "+sql);
			
			List <?> list = session.createSQLQuery(sql).list();

			int count=0;
			for(Iterator <?> iter = list.iterator();iter.hasNext();)
			{
				Object element[] = (Object[]) iter.next();

				hmEmployeeID.put(element[1].toString(), element[0]);
				hmProximityID.put(element[1].toString(), element[2]);
				hmEmployeeName.put(element[1].toString(), element[3]);
				hmDesignationID.put(element[1].toString(), element[4]);
				hmDesignation.put(element[1].toString(), element[5]);
				hmUnitID.put(element[1].toString(), element[6]);
				hmUnit.put(element[1].toString(), element[7]);
				hmDepartmentID.put(element[1].toString(), element[8]);
				hmDepartment.put(element[1].toString(), element[9]);
				hmSectionID.put(element[1].toString(), element[10]);
				hmSection.put(element[1].toString(), element[11]);
				count++;
				//System.out.println(count+" Hash Map: "+element[0].toString()+" "+element[1].toString()+" "+element[2].toString()+" "+element[3].toString()+" "+element[4].toString()+" "+element[5].toString());
				//System.out.println(count+" Hash Map: "+hmEmployeeID.get(element[1])+" "+hmProximityID.get(element[1])+" "+hmEmployeeName.get(element[1])+" "+hmDesignationID.get(element[1])+" "+hmDesignation.get(element[1])+" "+hmSectionID.get(element[1])+" "+hmSection.get(element[1]));
			}
		}
		catch (Exception e)
		{
			showNotification("Unable to Initialize", e.toString()+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			session.close();
		}
	}

	private void showData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{
			FileReader fr = new FileReader(getWindow().getApplication().getContext().getBaseDirectory()+""
					.replace("\\","/")+"/VAADIN/themes/"+fileAttUpload.fileName);
			fileName = fileAttUpload.fileName;
			BufferedReader br = new BufferedReader(fr);

			String s;
			StringTokenizer st;
			
			int i = 0;
			String checkQuery="";
			String insertQuery="";
			
			while((s = br.readLine())!=null)
			{			
				st = new StringTokenizer(s," ");
				
				String fingerId = st.nextToken().trim();
				
				String code = fingerId;
				String date = st.nextToken().toString().trim();
				String time = st.nextToken().toString().trim();
				String AMPM = st.nextToken().toString().trim();
				//System.out.println("code: "+code+" date: "+date+" time: "+time+" AMPM: "+AMPM);
				
				//String dworkdate2=session.createSQLQuery("SELECT LTRIM(STR(MONTH(DateAdd(dd,1,'"+dFormat.format(dWorkingDate.getValue())+"')))) + '/' +LTRIM(STR(DAY(DateAdd(dd,1,'"+dFormat.format(dWorkingDate.getValue())+"')))) + '/' +LTRIM(STR(YEAR(DateAdd(dd,1,'"+dFormat.format(dWorkingDate.getValue())+"'))))").list().iterator().next().toString();

				//System.out.println("Date: "+date+" dWorkingDate: "+dFormatTextFile.format(dWorkingDate.getValue()));
				if(date.equals(dFormatTextFile.format(dWorkingDate.getValue()))/* || date.equals(dworkdate2)*/)	
				{
					if(hmEmployeeName.get(fingerId)!=null)
					{
						/*checkQuery="Select * from tbEmployeeAttendanceSingleDevice where vEmployeeId = '"+hmEmployeeID.get(fingerId)+"' and "
								+ "dAttTime=(select convert(datetime,convert(varchar,'"+date+" "+time+" "+AMPM+"',105)))";
						List <?> lst = session.createSQLQuery(checkQuery).list();
						if(lst.isEmpty())
						{
							insertQuery = "INSERT INTO tbEmployeeAttendanceSingleDevice (vEmployeeId,vEmployeeCode,vFingerId," +
									" vProximityId,vEmployeeName,vDesignationId,vDesignation,vUnitId,vUnitName,vDepartmentId,vDepartmentName,vSectionId," +
									" vSectionName,dAttDate,dAttTime,vUserId,vUserIP,dEntryTime) values('"+hmEmployeeID.get(fingerId)+"', " +
									"  '"+fingerId+"', " +
									"  '"+fingerId+"', " +
									"  '"+hmProximityID.get(fingerId)+"', " +
									"  '"+hmEmployeeName.get(fingerId)+"', " +
									"  '"+hmDesignationID.get(fingerId)+"', " +
									"  '"+hmDesignation.get(fingerId)+"', " +
									"  '"+hmUnitID.get(fingerId)+"', " +
									"  '"+hmUnit.get(fingerId)+"', " +
									"  '"+hmDepartmentID.get(fingerId)+"', " +
									"  '"+hmDepartment.get(fingerId)+"', " +
									"  '"+hmSectionID.get(fingerId)+"', " +
									"  '"+hmSection.get(fingerId)+"', " +
									"  (select convert(date,convert(varchar,'"+date+"',105))), " +
									"  (select convert(datetime,convert(varchar,'"+date+" "+time+" "+AMPM+"',105))), " +
									" '"+sessionBean.getUserId()+"'," +
									" '"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP)";

							session.createSQLQuery(insertQuery).executeUpdate();
						}*/
						lblEmployeeId.get(i).setValue(hmEmployeeID.get(fingerId));
						lblFingerId.get(i).setValue(fingerId);
						lblProximityId.get(i).setValue(hmProximityID.get(fingerId));
						lblEmployeeName.get(i).setValue(hmEmployeeName.get(fingerId));
						lblDesignationID.get(i).setValue(hmDesignationID.get(fingerId));
						lblDesignation.get(i).setValue(hmDesignation.get(fingerId));
						lblUnitID.get(i).setValue(hmUnitID.get(fingerId));
						lblUnit.get(i).setValue(hmUnit.get(fingerId));
						lblDepartmentID.get(i).setValue(hmDepartmentID.get(fingerId));
						lblDepartment.get(i).setValue(hmDepartment.get(fingerId));
						lblSectionID.get(i).setValue(hmSectionID.get(fingerId));
						lblSection.get(i).setValue(hmSection.get(fingerId));

						@SuppressWarnings("deprecation")
						Date dt = new Date(date);

						lblAttDate.get(i).setReadOnly(false);
						lblAttDate.get(i).setValue(dt);
						lblAttDate.get(i).setReadOnly(true);
						lblInOutTime.get(i).setValue(time+" "+AMPM);

						if(lblProximityId.size()-1==i)
						{
							tableRowAdd(i+1);
						}
						i++;
					}
				}
			}

			if(i == 0)
			{
				showNotification("Warning","No Data Found!!!",Notification.TYPE_WARNING_MESSAGE);
			}

			fr.close();	
			tx.commit();
		}
		catch (Exception exp)
		{
			showNotification("Warning",exp.toString(),Notification.TYPE_WARNING_MESSAGE);
			tx.rollback();
		}
		finally{session.close();}
	}

	private void tableClear()
	{
		for(int i=0; i<lblEmployeeName.size(); i++)
		{
			lblEmployeeId.get(i).setValue("");
			lblFingerId.get(i).setValue("");
			lblProximityId.get(i).setValue("");
			lblEmployeeName.get(i).setValue("");
			lblDesignationID.get(i).setValue("");
			lblDesignation.get(i).setValue("");
			lblUnitID.get(i).setValue("");
			lblUnit.get(i).setValue("");
			lblDepartmentID.get(i).setValue("");
			lblDepartment.get(i).setValue("");
			lblSectionID.get(i).setValue("");
			lblSection.get(i).setValue("");

			lblAttDate.get(i).setReadOnly(false);
			lblAttDate.get(i).setValue(null);
			lblAttDate.get(i).setReadOnly(true);
			lblInOutTime.get(i).setValue("");
		}
	}

	private void saveBtnAction(ClickEvent e)
	{
		MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to save information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
		mb.show(new EventListener()
		{
			public void buttonClicked(ButtonType buttonType)
			{
				if(buttonType == ButtonType.YES)
				{
					insertData();
					componentIni(true);
					btnIni(true);
					txtClear();
				}
			}
		});
	}

	private void insertData()
	{
		String query= "",checkQuery,insertQuery;

		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{
			for(int i=0;i<lblEmployeeId.size();i++) 
			{
				if(!lblEmployeeId.get(i).getValue().toString().isEmpty())
				{
					/*System.out.println(i+"->"+
							lblEmployeeId.get(i).getValue()+" "+lblEmployeeName.get(i).getValue()+" "+
							lblDesignationID.get(i).getValue()+" "+lblDesignation.get(i).getValue()+" "+
							lblUnitID.get(i).getValue()+" "+lblUnit.get(i).getValue()+" "+
							lblDepartmentID.get(i).getValue()+" "+lblDepartment.get(i).getValue()+" "+
							lblSectionID.get(i).getValue()+" "+lblSection.get(i).getValue()+" "+
							lblAttDate.get(i).getValue()+" "+lblInOutTime.get(i).getValue()
							);*/
					
					checkQuery="Select * from tbEmployeeAttendanceSingleDevice where vEmployeeId = '"+lblEmployeeId.get(i).getValue()+"' and "
					+ "dAttTime=(select convert(datetime,convert(varchar,'"+dFormat.format(lblAttDate.get(i).getValue())+" "+lblInOutTime.get(i).getValue()+"',105)))";
					System.out.println("checkQuery: "+checkQuery);
					
					List <?> lst = session.createSQLQuery(checkQuery).list();
					if(lst.isEmpty())
					{
						insertQuery = "INSERT INTO tbEmployeeAttendanceSingleDevice " +
								"(" +
									"vEmployeeId,vEmployeeCode,vFingerId,vProximityId,vEmployeeName,vDesignationId,vDesignation,vUnitId,vUnitName,vDepartmentId," +
									"vDepartmentName,vSectionId,vSectionName,dAttDate,dAttTime,vUserId,vUserIP,dEntryTime" +
								") " +
								"values(" +
								"  '"+lblEmployeeId.get(i).getValue()+"', " +
								"  '"+lblEmployeeId.get(i).getValue()+"', " +
								"  '"+lblEmployeeId.get(i).getValue()+"', " +
								"  '"+lblEmployeeId.get(i).getValue()+"', " +
								"  '"+lblEmployeeName.get(i).getValue()+"', " +
								"  '"+lblDesignationID.get(i).getValue()+"', " +
								"  '"+lblDesignation.get(i).getValue()+"', " +
								"  '"+lblUnitID.get(i).getValue()+"', " +
								"  '"+lblUnit.get(i).getValue()+"', " +
								"  '"+lblDepartmentID.get(i).getValue()+"', " +
								"  '"+lblDepartment.get(i).getValue()+"', " +
								"  '"+lblSectionID.get(i).getValue()+"', " +
								"  '"+lblSection.get(i).getValue()+"', " +
								"  '"+dFormat.format(lblAttDate.get(i).getValue())+"', " +
								"  (select convert(datetime,convert(varchar,'"+dFormat.format(lblAttDate.get(i).getValue())+" "+lblInOutTime.get(i).getValue()+"',105))), " +
								" '"+sessionBean.getUserId()+"'," +
								" '"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP)";
						System.out.println("insertQuery: "+insertQuery);
						session.createSQLQuery(insertQuery).executeUpdate();
					}
				}
			}
			
					
			query="exec prcEmployeeAttendanceSingleMachine '"+dFormat.format(dWorkingDate.getValue())+"','"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"'";
			session.createSQLQuery(query).executeUpdate();

			Notification n=new Notification("All Information Save Successfully!","",Notification.TYPE_TRAY_NOTIFICATION);
			n.setPosition(Notification.POSITION_TOP_RIGHT);
			showNotification(n);

			tx.commit();
		}
		catch(Exception ex)
		{
			this.getParent().showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
		finally{session.close();}
	}

	private void componentIni(boolean b) 
	{
		dWorkingDate.setEnabled(!b);
		btnShowFile.setEnabled(!b);
		fileAttUpload.setEnabled(!b);
		table.setEnabled(!b);
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

	public void txtClear()
	{
		tableClear();
	}

	private AbsoluteLayout buildMainLayout() 
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);
		mainLayout.setWidth("970px");
		mainLayout.setHeight("520px");

		lblDate = new Label("Date :");
		lblDate.setImmediate(false);
		lblDate.setWidth("-1px");
		lblDate.setHeight("-1px");
		mainLayout.addComponent(lblDate, "top:20.0px; left:40.0px;");

		dWorkingDate = new PopupDateField();
		dWorkingDate.setImmediate(true);
		dWorkingDate.setWidth("110px");
		dWorkingDate.setDateFormat("dd-MM-yyyy");
		dWorkingDate.setValue(new java.util.Date());
		dWorkingDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dWorkingDate, "top:18.0px; left:100.0px;");

		fileAttUpload = new BtUpload("temp/attendanceFolder/c");
		fileAttUpload.setImmediate(true);
		mainLayout.addComponent(fileAttUpload, "top:54.0px; left:40.0px;");

		btnShowFile = new NativeButton("Show Data");
		btnShowFile.setIcon(new ThemeResource("../icons/find.png"));
		btnShowFile.setImmediate(true);
		btnShowFile.setWidth("110px");
		btnShowFile.setHeight("28px");
		mainLayout.addComponent(btnShowFile, "top:42.0px; right:30.0px;");
		btnShowFile.setStyleName("nButton");
		table.setWidth("930px");
		table.setHeight("370px");
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("SL", Label.class, new Label());
		table.setColumnWidth("SL", 25);

		table.addContainerProperty("Employee Id", Label.class, new Label());
		table.setColumnWidth("Employee Id", 80);

		table.addContainerProperty("Finger Id", Label.class, new Label());
		table.setColumnWidth("Finger Id", 80);

		table.addContainerProperty("Proximity Id", Label.class, new Label());
		table.setColumnWidth("Proximity Id", 80);

		table.addContainerProperty("Employee Name", Label.class, new Label());
		table.setColumnWidth("Employee Name",200);

		table.addContainerProperty("Designation ID", Label.class, new Label());
		table.setColumnWidth("Designation ID", 160);

		table.addContainerProperty("Designation", Label.class, new Label());
		table.setColumnWidth("Designation", 160);

		table.addContainerProperty("Project ID", Label.class, new Label());
		table.setColumnWidth("Project ID", 100);

		table.addContainerProperty("Project Name", Label.class, new Label());
		table.setColumnWidth("Project Name", 150);

		table.addContainerProperty("Department ID", Label.class, new Label());
		table.setColumnWidth("Department ID", 100);

		table.addContainerProperty("Department Name", Label.class, new Label());
		table.setColumnWidth("Department Name", 150);

		table.addContainerProperty("Section ID", Label.class, new Label());
		table.setColumnWidth("Section ID", 100);

		table.addContainerProperty("Section Name", Label.class, new Label());
		table.setColumnWidth("Section Name", 150);
		
		table.addContainerProperty("Att. Date", PopupDateField.class, new PopupDateField());
		table.setColumnWidth("Att. Date", 90);

		table.addContainerProperty("Time ", Label.class, new Label());
		table.setColumnWidth("Time",70);

		table.setColumnAlignments(new String[] {Table.ALIGN_RIGHT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, 
				Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT,
				Table.ALIGN_CENTER, Table.ALIGN_CENTER});

		table.setColumnCollapsingAllowed(true);
		table.setColumnCollapsed("Employee Id", true);
		table.setColumnCollapsed("Proximity Id", true);
		table.setColumnCollapsed("Designation ID", true);

		table.setColumnCollapsed("Project ID", true);
		table.setColumnCollapsed("Project Name", true);
		table.setColumnCollapsed("Department ID", true);
		table.setColumnCollapsed("Section ID", true);
		table.setColumnCollapsed("Section Name", true);
		
		mainLayout.addComponent(table,"top:90.0px; left:20.0px;");
		mainLayout.addComponent(button,"top:470.0px; left:300.0px");

		return mainLayout;
	}

	private void tableinitialise()
	{
		for(int i=0;i<15;i++)
		{
			tableRowAdd(i);
		}
	}

	private void tableRowAdd( final int ar)
	{
		lblsl.add(ar,new Label());
		lblsl.get(ar).setWidth("100%");
		lblsl.get(ar).setValue(ar+1);
		lblsl.get(ar).setImmediate(true);
		lblsl.get(ar).setHeight("16px");

		lblEmployeeId.add(ar, new Label());
		lblEmployeeId.get(ar).setImmediate(true);
		lblEmployeeId.get(ar).setWidth("100%");

		lblFingerId.add(ar, new Label());
		lblFingerId.get(ar).setImmediate(true);
		lblFingerId.get(ar).setWidth("100%");

		lblProximityId.add(ar, new Label());
		lblProximityId.get(ar).setImmediate(true);
		lblProximityId.get(ar).setWidth("100%");

		lblEmployeeName.add(ar,new Label());
		lblEmployeeName.get(ar).setWidth("100%");
		lblEmployeeName.get(ar).setImmediate(true);

		lblDesignationID.add(ar, new Label());
		lblDesignationID.get(ar).setWidth("100%");
		lblDesignationID.get(ar).setImmediate(true);

		lblDesignation.add(ar, new Label());
		lblDesignation.get(ar).setWidth("100%");
		lblDesignation.get(ar).setImmediate(true);

		lblUnitID.add(ar, new Label());
		lblUnitID.get(ar).setWidth("100%");
		lblUnitID.get(ar).setImmediate(true);

		lblUnit.add(ar, new Label());
		lblUnit.get(ar).setWidth("100%");
		lblUnit.get(ar).setImmediate(true);

		lblDepartmentID.add(ar, new Label());
		lblDepartmentID.get(ar).setWidth("100%");
		lblDepartmentID.get(ar).setImmediate(true);

		lblDepartment.add(ar, new Label());
		lblDepartment.get(ar).setWidth("100%");
		lblDepartment.get(ar).setImmediate(true);

		lblSectionID.add(ar, new Label());
		lblSectionID.get(ar).setWidth("100%");
		lblSectionID.get(ar).setImmediate(true);

		lblSection.add(ar, new Label());
		lblSection.get(ar).setWidth("100%");
		lblSection.get(ar).setImmediate(true);

		lblAttDate.add(ar, new PopupDateField());
		lblAttDate.get(ar).setWidth("100%");
		lblAttDate.get(ar).setDateFormat("dd-MM-yyyy");
		lblAttDate.get(ar).setResolution(PopupDateField.RESOLUTION_DAY);
		lblAttDate.get(ar).setReadOnly(true);
		lblAttDate.get(ar).setImmediate(true);

		lblInOutTime.add(ar, new Label());
		lblInOutTime.get(ar).setWidth("100%");
		lblInOutTime.get(ar).setImmediate(true);

		lblEmployeeId.add(ar, new Label());
		lblEmployeeId.get(ar).setImmediate(true);

		table.addItem(new Object[]{lblsl.get(ar),lblEmployeeId.get(ar),lblFingerId.get(ar),lblProximityId.get(ar),lblEmployeeName.get(ar),
				lblDesignationID.get(ar),lblDesignation.get(ar),lblUnitID.get(ar),lblUnit.get(ar),lblDepartmentID.get(ar),lblDepartment.get(ar),
				lblSectionID.get(ar),lblSection.get(ar),lblAttDate.get(ar),lblInOutTime.get(ar)},ar);
	}

}