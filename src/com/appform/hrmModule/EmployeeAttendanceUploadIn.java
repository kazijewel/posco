package com.appform.hrmModule;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Time;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.Format;
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
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;
import com.vaadin.data.Property.ValueChangeListener;

public class EmployeeAttendanceUploadIn extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Table table= new Table();
	private PopupDateField dWorkingDate ;

	public static final List lsttype=Arrays.asList(new String[]{"IN","OUT"});
	private OptionGroup opgType=new OptionGroup("",lsttype);

	private Label lblDate ;

	private ArrayList<Label> lblsl = new ArrayList<Label>();
	private ArrayList<Label> lblType = new ArrayList<Label>();
	private ArrayList<Label> lblEmpID = new ArrayList<Label>();
	private ArrayList<Label> lblEmployeeId = new ArrayList<Label>();
	private ArrayList<Label> lblProximityId = new ArrayList<Label>();
	private ArrayList<Label> lblEmployeeName = new ArrayList<Label>();
	private ArrayList<Label> lblDesignationID = new ArrayList<Label>();
	private ArrayList<Label> lblDesignation = new ArrayList<Label>();
	private ArrayList<Label> lblDepartmentId = new ArrayList<Label>();
	private ArrayList<Label> lblDepartment = new ArrayList<Label>();
	private ArrayList<Label> lblSectionId = new ArrayList<Label>();
	private ArrayList<Label> lblSection = new ArrayList<Label>();
	private ArrayList<PopupDateField> lblAttDate = new ArrayList<PopupDateField>();
	private ArrayList<Label> lblInTime = new ArrayList<Label>();

	ArrayList<Component> allComp = new ArrayList<Component>();	

	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");

	CommonButton button = new CommonButton("New", "Save", "", "","Refresh","","","","","Exit");
	String username="";

	private Boolean isUpdate= false;
	private Boolean isFind= false;

	private TextField txtSectionId = new TextField();
	private TextField txtOtDate = new TextField();


	private NativeButton btnShowFile;
	public BtUpload fileAttUpload;

	String empCode="";
	String empFingerId="";
	String empProxId="";
	String empDesId="";

	private String fileName = "";
	private int clickCount=0;

	private HashMap hmEmployeeID = new HashMap();
	private HashMap hmempID=new HashMap();
	private HashMap hmEmployeeName = new HashMap();
	private HashMap hmDesignationID = new HashMap();
	private HashMap hmDesignation = new HashMap();
	private HashMap hmDepartmentId = new HashMap();
	private HashMap hmDepartment= new HashMap();
	private HashMap hmSectionId = new HashMap();
	private HashMap hmSection = new HashMap();

	public EmployeeAttendanceUploadIn(SessionBean sessionBean) 
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
		focusEnter();

		saveDataToHm();
		authenticationCheck();

		dWorkingDate.focus();
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
				isFind = false;
				isUpdate = false;
				componentIni(false);
				btnIni(false);
				txtClear();
			}
		});

		button.btnRefresh.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				isFind = false;
				isUpdate = false;
				componentIni(true);
				btnIni(true);
				txtClear();
			}
		});

		button.btnSave.addListener( new Button.ClickListener() 
		{			
			public void buttonClick(ClickEvent event)
			{	
				if(!lblEmployeeName.get(0).toString().equals(""))
				{
					clickCount=0;
					saveBtnAction(event);
					isFind = false;
				}
				else
				{
					showNotification("Warning!","There are nothing to save",Notification.TYPE_WARNING_MESSAGE);
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
	}

	private void saveDataToHm()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{
			List list = session.createSQLQuery("select ei.vEmployeeId,ei.employeeCode,ei.vProximityId,ei.vEmployeeName," +
					" ei.vDesignationID,di.designationName,ei.vDepartmentID,dept.vDepartmentName," +
					" ei.vSectionId,si.SectionName from tbEmployeeInfo ei left join tbDesignationInfo di on" +
					" ei.vDesignationId=di.designationId left join tbSectionInfo si on ei.vSectionId=si.vSectionID inner " +
					" join tbDepartmentInfo dept on dept.vDepartmentID=ei.vDepartmentID").list();

			int count=0;
			for(Iterator iter = list.iterator();iter.hasNext();)
			{
				Object element[] = (Object[]) iter.next();

				hmEmployeeID.put(element[2], element[0].toString());
				hmempID.put(element[2], element[1].toString());
				hmEmployeeName.put(element[2], element[3].toString());
				hmDesignationID.put(element[2], element[4].toString());
				hmDesignation.put(element[2], element[5].toString());
				hmDepartmentId.put(element[2], element[6].toString());
				hmDepartment.put(element[2], element[7].toString());
				hmSectionId.put(element[2], element[8].toString());
				hmSection.put(element[2], element[9].toString());
				count++;
				System.out.println(count+" Hash Map: "+element[0].toString()+" "+element[1].toString()+" "+element[2].toString()+" "+element[3].toString()+" "+element[4].toString()+" "+element[5].toString());
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
		Session session=SessionFactoryUtil.getInstance().openSession();
		Transaction tx=session.beginTransaction();
		try
		{
			FileReader fr = new FileReader(getWindow().getApplication().getContext().getBaseDirectory()+""
					.replace("\\","/")+"/VAADIN/themes/"+fileAttUpload.fileName);
			fileName = fileAttUpload.fileName;
			BufferedReader br = new BufferedReader(fr);

			String s;
			StringTokenizer st;
			String strdate="";
			int i = 0;
			String dworkdate=dFormat.format(dWorkingDate.getValue()).toString().trim();
			String dworkdate1=dFormat.format(dWorkingDate.getValue()).toString().trim().replace("-", "/");

			String dworkdate2=session.createSQLQuery("Select convert(date,DATEADD(DD,1,'"+dworkdate+"'))").list().iterator().next().toString();

			while((s = br.readLine())!=null)
			{
				st = new StringTokenizer(s,"#");
				String str[]={"", "", "", "", ""};
				int j = 0;
				while (st.hasMoreTokens())
				{
					str[j] = st.nextToken();
					j++;
				}
				strdate=str[2].replace("/", "-");

				if(str[0].equals(opgType.getValue().toString()))
				{
					if(strdate.equals(dworkdate) || strdate.equals(dworkdate2))
					{
						if(hmEmployeeName.get(str[1])!=null)
						{
							lblType.get(i).setValue(str[0]);

							String proxId = str[1];
							lblProximityId.get(i).setValue(proxId);

							lblEmpID.get(i).setValue(hmEmployeeID.get(proxId));
							lblEmployeeId.get(i).setValue(hmempID.get(proxId));
							lblEmployeeName.get(i).setValue(hmEmployeeName.get(proxId));
							lblDesignationID.get(i).setValue(hmDesignationID.get(proxId));
							lblDesignation.get(i).setValue(hmDesignation.get(proxId));
							lblDepartmentId.get(i).setValue(hmDepartmentId.get(proxId));
							lblDepartment.get(i).setValue(hmDepartment.get(proxId));
							lblSectionId.get(i).setValue(hmSectionId.get(proxId));
							lblSection.get(i).setValue(hmSection.get(proxId));

							Date dt = new Date(str[2]);

							lblAttDate.get(i).setReadOnly(false);
							lblAttDate.get(i).setValue(dt);
							lblAttDate.get(i).setReadOnly(true);
							lblInTime.get(i).setReadOnly(false);
							lblInTime.get(i).setValue(str[3]);
							lblInTime.get(i).setReadOnly(true);

							if(i==lblProximityId.size()-1)
							{
								tableRowAdd(lblProximityId.size());
							}

							i++;
						}
					}
				}

				else
				{
					String strtype=opgType.getValue().toString();
					if(strtype.equals("IN"))
						strtype="In";
					else
						strtype="Out";

					showNotification("Warning","Please Select an "+strtype+" Type File!!!",Notification.TYPE_WARNING_MESSAGE);
					break;
				}
			}
			if(i==0)
				showNotification("Warning","No Data Found!!!",Notification.TYPE_WARNING_MESSAGE);
			fr.close();
		}
		catch(java.io.FileNotFoundException n)
		{
			showNotification("Warning!","Please first upload a valid attendance file.",Notification.TYPE_WARNING_MESSAGE);
		}
		catch(Exception exp)
		{
			showNotification("Unable to upload",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void tableClear()
	{
		for(int i=0; i<lblEmployeeName.size(); i++)
		{
			lblType.get(i).setValue("");
			lblEmpID.get(i).setValue("");
			lblEmployeeId.get(i).setValue("");
			lblEmployeeName.get(i).setValue("");
			lblProximityId.get(i).setValue("");
			lblDesignationID.get(i).setValue("");
			lblDesignation.get(i).setValue("");
			lblDepartmentId.get(i).setValue("");
			lblDepartment.get(i).setValue("");
			lblSectionId.get(i).setValue("");
			lblSection.get(i).setValue("");

			lblAttDate.get(i).setReadOnly(false);
			lblAttDate.get(i).setValue(null);
			lblAttDate.get(i).setReadOnly(true);

			lblInTime.get(i).setReadOnly(false);
			lblInTime.get(i).setValue(null);
			lblInTime.get(i).setReadOnly(true);
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
					if(clickCount==0)
					{
						clickCount++;
						insertData();
						componentIni(true);
						btnIni(true);
						txtClear();
					}
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
			for(int i = 0;i<lblEmpID.size();i++)
			{
				if(!lblEmpID.get(i).getValue().toString().isEmpty())
				{
					String time = lblInTime.get(i).getValue().toString();
					query = " insert into tbEmployeeAttendance"+opgType.getValue().toString()+" (dDate,vEmployeeId,vEmployeeCode,vProximityId,vEmployeeName,vDesignation," +
							" vSectionName,vSectionId,dAttDate,dAtt"+opgType.getValue().toString()+"Time,vUserId,vUserIP,dEntryTime,vShiftId,vEditFlag,vDesignationID,vDepartmentID,vDepartmentName) values ( " +
							" '"+dFormat.format(lblAttDate.get(i).getValue())+"', " +
							" '"+lblEmpID.get(i).getValue().toString().trim()+"', "+
							" '"+lblEmployeeId.get(i).getValue().toString().trim()+"', "+
							" '"+lblProximityId.get(i).getValue().toString().trim()+"', " +
							" '"+lblEmployeeName.get(i).getValue().toString().trim()+"', " +
							" '"+lblDesignation.get(i).getValue().toString().trim()+"', " +
							" '"+lblSection.get(i).getValue().toString().trim()+"', " +
							" '"+lblSectionId.get(i).getValue().toString().trim()+"', " +
							" '"+dFormat.format(lblAttDate.get(i).getValue())+"', " +
							" '"+dFormat.format(lblAttDate.get(i).getValue())+" "+time+"', " +
							" '"+sessionBean.getUserName()+"', " +
							" '"+sessionBean.getUserIp()+"'," +
							" CURRENT_TIMESTAMP, (select vFloor from tbEmployeeInfo where vEmployeeId='"+lblEmpID.get(i).getValue().toString().trim()+"'),''," +
							" '"+lblDesignationID.get(i).getValue()+"'," +
							" '"+lblDepartmentId.get(i).getValue()+"'," +
							" '"+lblDepartment.get(i).getValue()+"')" ;

					session.createSQLQuery(query).executeUpdate();
					session.clear();
				}	
			}
			attendanceMainTableData(session);

			tx.commit();
			showNotification("All information save successfully.");
		}
		catch(Exception ex)
		{
			showNotification("insertData", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
		finally
		{
			session.close();
		}
	}

	private void attendanceMainTableData(Session session)
	{
		String prcQuery="exec prcDailyEmployeeAttendance '"+dFormat.format(dWorkingDate.getValue())+"','%','%','%'";
		session.createSQLQuery(prcQuery).executeUpdate();
		System.out.println("prcQuery : "+prcQuery);
	}

	private void focusEnter()
	{
		allComp.add(dWorkingDate);
		for(int i=0;i<lblProximityId.size();i++)
		{
			allComp.add(lblInTime.get(i));
		}
		allComp.add(button.btnSave);

		new FocusMoveByEnter(this,allComp);
	}

	private void componentIni(boolean b) 
	{
		dWorkingDate.setEnabled(!b);
		opgType.setEnabled(!b);
		btnShowFile.setEnabled(!b);
		fileAttUpload.setEnabled(!b);
		table.setEnabled(!b);
	}

	private void btnIni(boolean t)
	{
		button.btnNew.setEnabled(t);
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
		mainLayout.setWidth("1085px");
		mainLayout.setHeight("400px");

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

		opgType.setImmediate(true);
		opgType.setWidth("-1px");
		opgType.setHeight("-1px");
		opgType.setStyleName("horizontal");
		opgType.select("IN");
		mainLayout.addComponent(new Label("Types : "),"top:20.0px;left:240.0px;");
		mainLayout.addComponent(opgType, "top:20.0px;left:280.0px;");

		fileAttUpload = new BtUpload("temp/attendanceFolder/c");
		fileAttUpload.setImmediate(true);
		mainLayout.addComponent(fileAttUpload, "top:44.0px; left:40.0px;");

		btnShowFile = new NativeButton("Show");
		btnShowFile.setImmediate(true);
		btnShowFile.setWidth("75px");
		btnShowFile.setHeight("28px");
		btnShowFile.setIcon(new ThemeResource("../icons/generate.png"));
		mainLayout.addComponent(btnShowFile, "top:42.0px; left:990.0px;");

		table.setWidth("1045px");
		table.setHeight("280px");
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("SL", Label.class, new Label());
		table.setColumnWidth("SL", 25);

		table.addContainerProperty("Type", Label.class, new Label());
		table.setColumnWidth("Type", 25);

		table.addContainerProperty("EMP ID", Label.class, new Label());
		table.setColumnWidth("EMP ID", 80);

		table.addContainerProperty("Employee ID", Label.class, new Label());
		table.setColumnWidth("Employee ID", 80);

		table.addContainerProperty("Proximity ID", Label.class, new Label());
		table.setColumnWidth("Proximity Id", 80);

		table.addContainerProperty("Employee Name", Label.class, new Label());
		table.setColumnWidth("Employee Name",  170);

		table.addContainerProperty("Designation ID", Label.class, new Label());
		table.setColumnWidth("Designation ID", 140);

		table.addContainerProperty("Designation", Label.class, new Label());
		table.setColumnWidth("Designation", 140);

		table.addContainerProperty("Department ID", Label.class, new Label());
		table.setColumnWidth("Department ID", 100);

		table.addContainerProperty("Department Name", Label.class, new Label());
		table.setColumnWidth("Department Name", 90);

		table.addContainerProperty("Section ID", Label.class, new Label());
		table.setColumnWidth("Section ID", 100);

		table.addContainerProperty("Section Name", Label.class, new Label());
		table.setColumnWidth("Section Name", 90);

		table.addContainerProperty("Att. Date", PopupDateField.class, new PopupDateField());
		table.setColumnWidth("Att. Date", 90);

		table.addContainerProperty("In/Out Time", Label.class, new Label());
		table.setColumnWidth("In Time", 90);

		table.setColumnAlignments(new String[] {Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, 
				Table.ALIGN_LEFT,Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, 
				Table.ALIGN_CENTER, Table.ALIGN_CENTER});

		table.setColumnCollapsingAllowed(true);
		table.setColumnCollapsed("Designation ID", true);
		table.setColumnCollapsed("Department ID", true);
		table.setColumnCollapsed("Section ID", true);
		table.setColumnCollapsed("EMP ID", true);

		mainLayout.addComponent(table,"top:80.0px; left:20.0px;");
		mainLayout.addComponent(button,"top:365.0px; left:365.0px");

		return mainLayout;
	}

	private void tableinitialise()
	{
		for(int i=0;i<10;i++)
		{
			tableRowAdd(i);
		}
	}

	private void tableRowAdd( final int ar)
	{
		lblsl.add(ar,new Label());
		lblsl.get(ar).setWidth("100%");
		lblsl.get(ar).setValue(ar+1);
		lblsl.get(ar).setHeight("16px");
		lblsl.get(ar).setStyleName("tableData");

		lblType.add(ar, new Label());
		lblType.get(ar).setWidth("100%");

		lblEmpID.add(ar, new Label());
		lblEmpID.get(ar).setWidth("100%");
		lblEmpID.get(ar).setHeight("16px");

		lblEmployeeId.add(ar, new Label());
		lblEmployeeId.get(ar).setWidth("100%");

		lblProximityId.add(ar, new Label());
		lblProximityId.get(ar).setWidth("100%");

		lblEmployeeName.add(ar,new Label());
		lblEmployeeName.get(ar).setWidth("100%");

		lblDesignationID.add(ar, new Label());
		lblDesignationID.get(ar).setWidth("100%");

		lblDesignation.add(ar, new Label());
		lblDesignation.get(ar).setWidth("100%");

		lblDepartmentId.add(ar, new Label());
		lblDepartmentId.get(ar).setWidth("100%");

		lblDepartment.add(ar, new Label());
		lblDepartment.get(ar).setWidth("100%");

		lblSectionId.add(ar, new Label());
		lblSectionId.get(ar).setWidth("100%");

		lblSection.add(ar, new Label());
		lblSection.get(ar).setWidth("100%");

		lblAttDate.add(ar, new PopupDateField());
		lblAttDate.get(ar).setWidth("100%");
		lblAttDate.get(ar).setDateFormat("dd-MM-yyyy");
		lblAttDate.get(ar).setResolution(PopupDateField.RESOLUTION_DAY);
		lblAttDate.get(ar).setReadOnly(true);

		lblInTime.add(ar, new Label());
		lblInTime.get(ar).setWidth("100%");
		lblInTime.get(ar).setImmediate(true);


		table.addItem(new Object[]{lblsl.get(ar),lblType.get(ar),lblEmpID.get(ar),lblEmployeeId.get(ar),lblProximityId.get(ar),
				lblEmployeeName.get(ar),lblDesignationID.get(ar),lblDesignation.get(ar),lblDepartmentId.get(ar),lblDepartment.get(ar),
				lblSectionId.get(ar),lblSection.get(ar),lblAttDate.get(ar),lblInTime.get(ar)},ar);
	}
}