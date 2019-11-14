package com.appform.hrmModule;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.BtUpload;
import com.common.share.CommonButton;
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
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.data.Property.ValueChangeListener;

@SuppressWarnings("serial")
public class EmployeeAttendanceUpload extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Table table= new Table();
	private PopupDateField dWorkingDate ;
	private Label lblDate ;

	private ArrayList <Label> lblsl = new ArrayList<Label>();
	private ArrayList <Label> lblType = new ArrayList<Label>();
	private ArrayList <Label> lblEmployeeId = new ArrayList<Label>(); 
	private ArrayList <Label> lblFingerId = new ArrayList<Label>();
	private ArrayList <Label> lblProximityId = new ArrayList<Label>();
	private ArrayList <Label> lblEmployeeName = new ArrayList<Label>();
	private ArrayList <Label> lblDesignationID = new ArrayList<Label>();
	private ArrayList <Label> lblDesignation = new ArrayList<Label>();
	private ArrayList <Label> lblSectionID = new ArrayList<Label>();
	private ArrayList <Label> lblSection = new ArrayList<Label>();
	private ArrayList <PopupDateField> lblAttDate = new ArrayList<PopupDateField>();
	private ArrayList <Label> lblInOutTime = new ArrayList<Label>();
	ArrayList<Component> allComp = new ArrayList<Component>();	

	private OptionGroup RadioBtnGroup;
	private static final List<String> type2 = Arrays.asList(new String[] {"IN","OUT"});

	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dFormatBd = new SimpleDateFormat("M/d/yyyy");

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
	private HashMap <String,Object> hmSectionID = new HashMap <String,Object> ();
	private HashMap <String,Object> hmSection = new HashMap <String,Object> ();

	public EmployeeAttendanceUpload(SessionBean sessionBean) 
	{
		this.sessionBean=sessionBean;
		this.setResizable(false);
		this.setCaption("EMPLOYEE ATTENDANCE UPLOAD :: " + sessionBean.getCompany());

		buildMainLayout();
		setContent(mainLayout);
		tableinitialise();

		componentIni(true);
		btnIni(true);

		SetEventAction();
		authenticationCheck();
		dWorkingDate.focus();
		RadioBtnGroup.select("IN");
		//dataSaveToHashMap();
	}

	private void authenticationCheck()
	{
		if(!sessionBean.isSubmitable())
		{
			button.btnSave.setVisible(false);
		}

		if(!sessionBean.isUpdateable())
		{
			button.btnEdit.setVisible(false);
		}

		if(!sessionBean.isDeleteable())
		{
			button.btnDelete.setVisible(false);
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
				tableClear();
				showData();
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

		RadioBtnGroup.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				tableClear();
			}
		});
	}

	private void dataSaveToHashMap()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> list = session.createSQLQuery("select ei.vEmployeeId,ei.iFingerID,ei.vProximityId," +
					"ei.vEmployeeName,ei.vDesignationID,di.designationName,ei.vSectionId,si.SectionName " +
					"from tbEmployeeInfo ei left join tbDesignationInfo di on ei.vDesignationId=di.designationId " +
					"left join tbSectionInfo si on ei.vSectionId=si.AutoID where ei.iStatus = 1 order by " +
					"CAST(SUBSTRING(vEmployeeId,5,LEN(vEmployeeId)) as int)").list();

			int count=0;
			for(Iterator <?> iter = list.iterator();iter.hasNext();)
			{
				Object element[] = (Object[]) iter.next();

				hmEmployeeID.put(element[1].toString(), element[0]);
				hmProximityID.put(element[1].toString(), element[2]);
				hmEmployeeName.put(element[1].toString(), element[3]);
				hmDesignationID.put(element[1].toString(), element[4]);
				hmDesignation.put(element[1].toString(), element[5]);
				hmSectionID.put(element[1].toString(), element[6]);
				hmSection.put(element[1].toString(), element[7]);
				count++;
				System.out.println(count+" Hash Map: "+element[0].toString()+" "+element[1].toString()+" "+element[2].toString()+" "+element[3].toString()+" "+element[4].toString()+" "+element[5].toString());
				System.out.println(count+" Hash Map: "+hmEmployeeID.get(element[1])+" "+hmProximityID.get(element[1])+" "+hmEmployeeName.get(element[1])+" "+hmDesignationID.get(element[1])+" "+hmDesignation.get(element[1])+" "+hmSectionID.get(element[1])+" "+hmSection.get(element[1]));
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
			String insertQuery = "";/*"INSERT INTO tbEmployeeAttendance"+RadioBtnGroup.getValue()+" (vEmployeeId,vEmployeeCode,vFingerId," +
					"vProximityId,vEmployeeName,vDesignationId,vDesignation,vSectionId," +
					"vSectionName,dAttDate,dAtt"+RadioBtnGroup.getValue()+"Time,vUserId,vUserIP,dEntryTime) values";
			String insertData="";*/
			String chkQuery = "";

			while((s = br.readLine())!=null)
			{

				//System.out.println("insertData: ");
				st = new StringTokenizer(s," ");
				String fingerId = st.nextToken().toString().trim();
				@SuppressWarnings("unused")
				String code = st.nextToken().toString().trim();
				String date = st.nextToken().toString().trim();
				String time = st.nextToken().toString().trim();
				String AMPM = st.nextToken().toString().trim();
				@SuppressWarnings("unused")
				String mode = st.nextToken().toString().trim();
				String machine = st.nextToken().toString().trim();
				//SELECT LTRIM(STR(MONTH(DATEADD(DD,1,'"+dFormat.format(dWorkingDate.getValue())+"')))) + '/' +LTRIM(STR(DAY(DATEADD(DD,1,'"+dFormat.format(dWorkingDate.getValue())+"')))) + '/' +STR(YEAR(DATEADD(DD,1,'"+dFormat.format(dWorkingDate.getValue())+"')), 4)
				String dworkdate2=session.createSQLQuery("SELECT LTRIM(STR(MONTH('"+dFormat.format(dWorkingDate.getValue())+"'))) + '/' +LTRIM(STR(DAY(DateAdd(dd,1,'"+dFormat.format(dWorkingDate.getValue())+"')))) + '/' +LTRIM(STR(YEAR('"+dFormat.format(dWorkingDate.getValue())+"')))").list().iterator().next().toString();
				System.out.println("dworkdate2= " +dworkdate2);
				System.out.println("date= " +date);

				if(date.equals(dFormatBd.format(dWorkingDate.getValue())) || date.equals(dworkdate2))	
				{
					if(machine.equals(RadioBtnGroup.getValue()))
					{
						System.out.println("Hello2");
						System.out.println("FingerId : "+fingerId+" Employee Name : "+hmEmployeeName.get(fingerId));
						if(hmEmployeeName.get(fingerId)!=null)
						{
							chkQuery = "select * from tbEmployeeAttendance"+RadioBtnGroup.getValue()+" where vEmployeeId = '"+hmEmployeeID.get(fingerId)+"' and dAtt"+RadioBtnGroup.getValue()+"Time = (select convert(datetime,convert(varchar,'"+date+" "+time+" "+AMPM+"',105)))";
							List <?> lst = session.createSQLQuery(chkQuery).list();
							if(lst.isEmpty())
							{
								insertQuery = "INSERT INTO tbEmployeeAttendance"+RadioBtnGroup.getValue()+" (vEmployeeId,vEmployeeCode,vFingerId," +
										"vProximityId,vEmployeeName,vDesignationId,vDesignation,vSectionId," +
										"vSectionName,dAttDate,dAtt"+RadioBtnGroup.getValue()+"Time,vUserId,vUserIP,dEntryTime) values('"+hmEmployeeID.get(fingerId)+"', " +
										"  '"+fingerId+"', " +
										"  '"+fingerId+"', " +
										"  '"+hmProximityID.get(fingerId)+"', " +
										"  '"+hmEmployeeName.get(fingerId)+"', " +
										"  '"+hmDesignationID.get(fingerId)+"', " +
										"  '"+hmDesignation.get(fingerId)+"', " +
										"  '"+hmSectionID.get(fingerId)+"', " +
										"  '"+hmSection.get(fingerId)+"', " +
										"  (select convert(date,convert(varchar,'"+date+"',105))), " +
										"  (select convert(datetime,convert(varchar,'"+date+" "+time+" "+AMPM+"',105))), " +
										" '"+sessionBean.getUserId()+"'," +
										" '"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP)";		

								session.createSQLQuery(insertQuery).executeUpdate();
							}
							lblType.get(i).setValue(machine);
							lblEmployeeId.get(i).setValue(hmEmployeeID.get(fingerId));
							lblFingerId.get(i).setValue(fingerId);
							lblProximityId.get(i).setValue(hmProximityID.get(fingerId));
							lblEmployeeName.get(i).setValue(hmEmployeeName.get(fingerId));
							lblDesignationID.get(i).setValue(hmDesignationID.get(fingerId));
							lblDesignation.get(i).setValue(hmDesignation.get(fingerId));
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
					else
					{
						showNotification("Warning","Please Upload "+RadioBtnGroup.getValue()+" File!!!",Notification.TYPE_WARNING_MESSAGE);
						break;
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
			showNotification("Warning","There are no Data",Notification.TYPE_WARNING_MESSAGE);
			tx.rollback();
		}
		finally{session.close();}
	}

	private void tableClear()
	{
		for(int i=0; i<lblEmployeeName.size(); i++)
		{
			lblType.get(i).setValue("");
			lblEmployeeId.get(i).setValue("");
			lblFingerId.get(i).setValue("");
			lblProximityId.get(i).setValue("");
			lblEmployeeName.get(i).setValue("");
			lblDesignationID.get(i).setValue("");
			lblDesignation.get(i).setValue("");
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
		String query= "";

		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{
			//query="EXEC [prcGenerateAttendance] '"+dFormat.format(dWorkingDate.getValue())+"','"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"','"+Flag+"' ";
			query="exec prcEmployeeAttendance '"+dFormat.format(dWorkingDate.getValue())+"','%','%','"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"'";
			session.createSQLQuery(query).executeUpdate();

			this.getParent().showNotification("All information save successfully.");

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
		mainLayout.setWidth("940px");
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

		RadioBtnGroup = new OptionGroup("",type2);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		mainLayout.addComponent(RadioBtnGroup, "top:14.0px;left:350.0px;");

		btnShowFile = new NativeButton("Show Data");
		btnShowFile.setIcon(new ThemeResource("../icons/find.png"));
		btnShowFile.setImmediate(true);
		btnShowFile.setWidth("75px");
		btnShowFile.setHeight("28px");
		btnShowFile.setStyleName("nButton");
		mainLayout.addComponent(btnShowFile, "top:42.0px; right:30.0px;");

		table.setWidth("900px");
		table.setHeight("370px");
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("SL", Label.class, new Label());
		table.setColumnWidth("SL", 25);

		table.addContainerProperty("Type", Label.class, new Label());
		table.setColumnWidth("Type", 25);

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

		table.addContainerProperty("Section ID", Label.class, new Label());
		table.setColumnWidth("Section ID", 100);

		table.addContainerProperty("Section Name", Label.class, new Label());
		table.setColumnWidth("Section Name", 150);

		table.addContainerProperty("Att. Date", PopupDateField.class, new PopupDateField());
		table.setColumnWidth("Att. Date", 90);

		table.addContainerProperty("Time ", Label.class, new Label());
		table.setColumnWidth("Time",70);

		table.setColumnAlignments(new String[] {Table.ALIGN_RIGHT, Table.ALIGN_CENTER, Table.ALIGN_LEFT, 
				Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, 
				Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_CENTER, Table.ALIGN_CENTER});

		table.setColumnCollapsingAllowed(true);
		table.setColumnCollapsed("Employee Id", true);
		table.setColumnCollapsed("Proximity Id", true);
		table.setColumnCollapsed("Designation ID", true);
		table.setColumnCollapsed("Section ID", true);

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

		lblType.add(ar, new Label());
		lblType.get(ar).setImmediate(true);
		lblType.get(ar).setWidth("100%");

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

		table.addItem(new Object[]{lblsl.get(ar),lblType.get(ar),lblEmployeeId.get(ar),lblFingerId.get(ar),
				lblProximityId.get(ar),lblEmployeeName.get(ar),lblDesignationID.get(ar),lblDesignation.get(ar),
				lblSectionID.get(ar),lblSection.get(ar),lblAttDate.get(ar),lblInOutTime.get(ar)},ar);
	}

}