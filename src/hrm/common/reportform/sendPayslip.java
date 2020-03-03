package hrm.common.reportform;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.common.share.ReportDate;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class sendPayslip extends Window {
	private SessionBean sessionBean;
	
	AbsoluteLayout mainLayout=new AbsoluteLayout();

	// private Panel vList = new Panel();
	public Table table = new Table();
	private ArrayList<CheckBox> tbchk = new ArrayList<CheckBox>();
	public ArrayList<Label> tbEmployeeId = new ArrayList<Label>();
	public ArrayList<Label> tbEmployeeCode = new ArrayList<Label>();
	public ArrayList<Label> tbEmployeeName = new ArrayList<Label>();
	public ArrayList<Label> tbEmailId = new ArrayList<Label>();
	//private ComboBox cmbMemberType = new ComboBox("Employee Type :");
	private TextRead txtEmailId;
	private TextRead txtEmailPass = new TextRead();
	private TextField txtSubject;
	private TextField txtMessage;

	private NativeButton allBtn = new NativeButton("Select All");
	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat fMonth = new SimpleDateFormat("MMMMM");
	private SimpleDateFormat fYear = new SimpleDateFormat("yyyy");
	private ReportDate reportTime = new ReportDate();

	private boolean isAllSelect = false;
	
	
	private Label lblUnit;
	private Label lblDepartment,lblSection;
	private Label lblSalaryMonth;

	private ComboBox cmbUnit;
	private ComboBox cmbDepartment,cmbSection;
	private ComboBox dSalaryMonth;
	//private ComboBox cmbEmployeeName;

	private CheckBox chkDepartmentAll,chkSectionAll;
	
	
	private FileWriter log;

	TextField txtPath=new TextField();
	

	CommonButton button = new CommonButton("", "", "", "", "", "", "","Preview", "", "Exit");
	
	public sendPayslip(SessionBean sessionBean, String EmailID, String EmailPass) {
		this.sessionBean = sessionBean;
		this.setCaption("SEND MAIL (PAYSLIP) :: "+ this.sessionBean.getCompany());
		this.setResizable(false);
		
		buildMainLayout();
		txtEmailId.setValue(EmailID);
		txtEmailPass.setValue(EmailPass);
		
		setContent(mainLayout);
		salaryDateLoad();
		setEventAction();
		
	}
	private void salaryDateLoad()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();

		try
		{
			String query="select distinct convert(date,DATEADD(s,-1,DATEADD(mm, DATEDIFF(m,0,dSalaryDate)+1,0)))dSalaryDate,vSalaryMonth,vSalaryYear " +
					"from tbMonthlySalary order by dSalaryDate desc";
			System.out.println("salaryDateLoad :"+query);
			
			List <?> list=session.createSQLQuery(query).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				dSalaryMonth.addItem(element[0]);
				dSalaryMonth.setItemCaption(element[0],element[1]+"-"+element[2]);
			}
		}
		catch(Exception exp)
		{
			showNotification("salaryDateLoad",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			session.close();
		}
	}
	
	public void cmbUnitAddData()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();

		try
		{
			String query="select vUnitId,vUnitName from tbMonthlySalary " +
					"where YEAR(dSalaryDate)=YEAR('"+dFormat.format(dSalaryMonth.getValue())+"') " +
					"and MONTH(dSalaryDate)=MONTH('"+dFormat.format(dSalaryMonth.getValue())+"') order by vUnitName";
			List <?> list=session.createSQLQuery(query).list();
			System.out.println("Unit data Load :"+query);
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbUnit.addItem(element[0]);
				cmbUnit.setItemCaption(element[0], (String) element[1]);
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbUnitAddData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			session.close();
		}
	}
	public void cmbDepartmentAddData()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();

		try
		{		
			String query="select vDepartmentId,vDepartmentName from tbMonthlySalary " +
					"where vUnitId ='"+cmbUnit.getValue().toString()+"' and YEAR(dSalaryDate)=YEAR('"+dFormat.format(dSalaryMonth.getValue())+"') " +
							"and MONTH(dSalaryDate)=MONTH('"+dFormat.format(dSalaryMonth.getValue())+"') order by vDepartmentName";
			System.out.println("cmbDepartmentAddData: "+query);
			
			List <?> list=session.createSQLQuery(query).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbDepartment.addItem(element[0]);
				cmbDepartment.setItemCaption(element[0], (String) element[1]);
			}
		}
		catch(Exception exp){
			showNotification("cmbDepartmentAddData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			session.close();
		}
	}
	public void cmbSectionAddData(String id)
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();

		try
		{		
			String query="select vSectionId,vSectionName from tbMonthlySalary " +
					"where vUnitId ='"+cmbUnit.getValue().toString()+"' and YEAR(dSalaryDate)=YEAR('"+dFormat.format(dSalaryMonth.getValue())+"') " +
					"and MONTH(dSalaryDate)=MONTH('"+dFormat.format(dSalaryMonth.getValue())+"') "
				  + "and vDepartmentId like '"+id+"' order by vSectionName";
			System.out.println("cmbSectionAddData: "+query);
			
			List <?> list=session.createSQLQuery(query).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbSection.addItem(element[0]);
				cmbSection.setItemCaption(element[0], (String) element[1]);
			}
		}
		catch(Exception exp){
			showNotification("cmbSectionAddData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			session.close();
		}
	}
	private void txtPathDataLoad() {
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		
		try
		{
			String sql = "select vUnitId,imageLoc from tbUnitInfo where vUnitId='"+cmbUnit.getValue().toString()+"' ";
			
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator();iter.hasNext();)
			{
				Object element[] = (Object[]) iter.next();
				txtPath.setValue(element[1].toString());
				
				System.out.println("Id : "+element[0]+" Path  :"+element[1]);
				
			}
		}
		catch(Exception exp)
		{
			showNotification(exp+" Image path set :",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	
	private void setEventAction() 
	{
		
		dSalaryMonth.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbUnit.removeAllItems();
				if(dSalaryMonth.getValue()!=null)
				{
					tableClear();
					cmbUnitAddData();
				}
			}
		});

		cmbUnit.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				txtPath.setValue("");
				if(dSalaryMonth.getValue()!= null)
				{
					if(cmbUnit.getValue()!= null)
					{
						tableClear();
						cmbDepartment.removeAllItems();
						cmbDepartmentAddData();
						txtPathDataLoad();
					}
				}
			}
		});

		cmbDepartment.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(dSalaryMonth.getValue()!=null)
				{
					if(cmbUnit.getValue()!=null)
					{
						if(cmbDepartment.getValue()!=null)
						{
							tableClear();
							cmbSectionAddData(cmbDepartment.getValue().toString());
						}
					}
				}
			}
		});

		chkDepartmentAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(chkDepartmentAll.booleanValue())
				{
					if(dSalaryMonth.getValue()!=null)
					{
						if(cmbUnit.getValue()!=null)
						{
							tableClear();
							cmbDepartment.setValue(null);
							cmbDepartment.setEnabled(false);
							cmbSectionAddData("%");
						}
					}
				}
				else
				{
					tableClear();
					cmbDepartment.setEnabled(true);
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
						tableClear();
						tableDataLoad();
					}
				}
			
			}
		});

		chkSectionAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(chkSectionAll.booleanValue())
				{

					if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
					{
						tableClear();
						cmbSection.setValue(null);
						cmbSection.setEnabled(false);
						tableDataLoad();
					}
				}
				else
				{
					tableClear();
					cmbSection.setEnabled(true);
				}
			}
		});

		button.btnPreview.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				if(dSalaryMonth.getValue()!=null)
				{
					if(cmbUnit.getValue()!=null)
					{
						if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
						{
							if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
							{
								if(txtSubject.getValue().toString()!="")
								{
									MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to Send?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
									mb.show(new EventListener()
									{
										public void buttonClicked(ButtonType buttonType)
										{
											if(buttonType == ButtonType.YES)
											{
												emailSend();
											}
										}
									});
								}
							}
						}
					}
				}
			}
		});
		button.btnExit.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				close();
			}
		});
		allBtn.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				allBtnAction();
			}
		});
	}

	private void tableDataLoad() {
		Session session = SessionFactoryUtil.getInstance().openSession();
		String sql="";
		String section="%";
		
		if(!chkDepartmentAll.booleanValue())
		{
			section=cmbDepartment.getValue().toString();
		}
		
		try 
		{
			
			/*String sql = "select vEmployeeId,vEmployeeCode,vEmployeeName,vEmailAddress " +
					"from tbEmpOfficialPersonalInfo where vEmailAddress!='' " +
					"and vUnitId ='"+cmbUnit.getValue().toString()+"' and YEAR(dSalaryDate)=YEAR('"+dFormat.format(dSalaryMonth.getValue())+"') " +
					"and MONTH(dSalaryDate)=MONTH('"+dFormat.format(dSalaryMonth.getValue())+"') and vEmployeeType like '" + empType + "' ";*/
			
			sql="select a.vEmployeeId,a.vEmployeeCode,a.vEmployeeName,vEmailAddress from tbMonthlySalary a " +
					"inner join tbEmpOfficialPersonalInfo b on a.vEmployeeID=b.vEmployeeId " +
					"where  vEmailAddress like '%@%' " +
					"and YEAR(dSalaryDate)=YEAR('"+dFormat.format(dSalaryMonth.getValue())+"') " +
					"and MONTH(dSalaryDate)=MONTH('"+dFormat.format(dSalaryMonth.getValue())+"') " +
					"and a.vDepartmentId like '"+section+"' " +
					"and a.vUnitId ='"+cmbUnit.getValue().toString()+"' " +
					"order by a.vEmployeeCode";
			
			System.out.println("tableDataLoad: "+sql);
			
			List<?> list = session.createSQLQuery(sql).list();
			if (!list.isEmpty()) 
			{
				int index = 0;
				Iterator<?> iter = list.iterator();
				while (iter.hasNext()) 
				{
					Object[] element = (Object[]) iter.next();
					tbEmailId.get(index).setValue(element[3]);
					tbEmployeeId.get(index).setValue(element[0]);
					tbEmployeeCode.get(index).setValue(element[1]);
					tbEmployeeName.get(index).setValue(element[2]);

					if (index == tbEmailId.size() - 1) 
					{
						tablerow(index + 1);
					}
					index++;
				}
			} 
			else
			{
				showNotification("", "data not found!",Notification.TYPE_WARNING_MESSAGE);
			}
		} 
		catch (Exception exp) 
		{
			showNotification("table data Load :" + exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public void tableini() {

		int i = 0;
		while (i < 11) {
			tablerow(i);
			i++;
		}
	}

	public void tablerow(int ar) {
		tbchk.add(ar, new CheckBox());
		tbchk.get(ar).setWidth("100%");

		tbEmployeeCode.add(ar, new Label());
		tbEmployeeCode.get(ar).setWidth("100%");
		tbEmployeeCode.get(ar).addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
				int x=0;
				table.setColumnFooter("#", "");
				for(int i=0;i<tbEmployeeCode.size();i++)
				{
					if(!tbEmployeeCode.get(i).getValue().toString().isEmpty())
					{
						table.setColumnFooter("#", new DecimalFormat("#").format(++x));
					}
				}
			}
		});

		tbEmployeeName.add(ar, new Label());
		tbEmployeeName.get(ar).setWidth("100%");

		tbEmployeeId.add(ar, new Label());
		tbEmployeeId.get(ar).setWidth("100%");

		tbEmailId.add(ar, new Label());
		tbEmailId.get(ar).setWidth("100%");

		table.addItem(new Object[] { tbchk.get(ar), tbEmployeeCode.get(ar),tbEmployeeName.get(ar), tbEmployeeId.get(ar), tbEmailId.get(ar) },ar);

	}

	private void buttonActionAdd() {
		/*cmbMemberType.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				// cmbListDataLoad();
				tableClear();
			}
		});*/
	}

	private void tableClear() {
		for (int i = 0; i < tbEmployeeCode.size(); i++) {
			tbEmployeeCode.get(i).setValue("");
			tbEmployeeId.get(i).setValue("");
			tbEmployeeName.get(i).setValue("");
			tbEmailId.get(i).setValue("");
			tbchk.get(i).setValue(false);
		}
	}

	private void emailSend() 
	{
		System.out.printf("1");
		//HashMap hm = new HashMap();
		try
		{
			System.out.printf("2");
			File f = new File(sessionBean.emailPath);
			f.mkdirs();
			System.out.printf("3");
			System.out.printf("f"+f);
			String MasterId="";
			log = new FileWriter(sessionBean.emailPath+"Email/"+"log.txt");
			System.out.printf("log"+log);
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			
			
			String host = "smtp.gmail.com";
			String from = "";
			String pass = "";
			
			
			from=txtEmailId.getValue().toString();
			pass=txtEmailPass.getValue().toString();
			System.out.printf("\nHost"+from);
			System.out.printf("\nPass"+pass);
			
			Properties props = System.getProperties();
			props.put("mail.smtp.starttls.enable", "true"); // added this line
			props.put("mail.smtp.host", host);
			props.put("mail.smtp.user", from);
			props.put("mail.smtp.password", pass);
			props.put("mail.smtp.port", "587");
			props.put("mail.smtp.auth", "true");
			String EmailTo="";
			for(int i=0;i<tbchk.size();i++)
			{
				if(Boolean.valueOf(tbchk.get(i).getValue().toString()) && !tbEmailId.get(i).getValue().toString().isEmpty() && isValid(tbEmailId.get(i).getValue().toString()))
				{
				//reportGenerate
				javax.mail.Session esession = javax.mail.Session.getDefaultInstance(props, null);
				MasterId=tbEmployeeId.get(i).getValue().toString();
				
					System.out.printf("4");
					System.out.printf("\n4.1"+MasterId);
					System.out.printf("\n4.2"+sessionBean.emailPath+"Email/"+MasterId+"_"+"_"+fMonth.format(dSalaryMonth.getValue())+"-"+fYear.format(dSalaryMonth.getValue())+".pdf");
					reportGenerate(MasterId,sessionBean.emailPath+"Email/"+MasterId+"_"+"_"+fMonth.format(dSalaryMonth.getValue())+"-"+fYear.format(dSalaryMonth.getValue())+".pdf");

				
					EmailTo=tbEmailId.get(i).getValue().toString();
					MimeMessage message = new MimeMessage(esession);
					message.setFrom(new InternetAddress(from));
					message.addRecipient(Message.RecipientType.TO, new InternetAddress(EmailTo));
					message.setSubject(txtSubject.getValue().toString());
					message.setText(txtMessage.getValue().toString());
					System.out.printf("7");
					// create the message part 
					MimeBodyPart messageBodyPart = new MimeBodyPart();
					//fill message
					messageBodyPart.setText(txtMessage.getValue().toString());
					Multipart multipart = new MimeMultipart();
					multipart.addBodyPart(messageBodyPart);
					System.out.printf("8");
					// Part two is attachment
					messageBodyPart = new MimeBodyPart();
					DataSource source = new FileDataSource(sessionBean.emailPath+"Email/"+MasterId+"_"+"_"+fMonth.format(dSalaryMonth.getValue())+"-"+fYear.format(dSalaryMonth.getValue())+".pdf");
					messageBodyPart.setDataHandler( new DataHandler(source));
					//messageBodyPart.setFileName(sessionBean.emailPath+"Email/"+MasterId+"_"+"_"+dLastDate.getValue().toString()+".pdf");
					messageBodyPart.setFileName(tbEmployeeCode.get(i).getValue()+","+"Pay Slip-"+fMonth.format(dSalaryMonth.getValue())+"-"+fYear.format(dSalaryMonth.getValue())+".pdf");
					multipart.addBodyPart(messageBodyPart);
					System.out.printf("9");
					// Put parts in message
					message.setContent(multipart);
					System.out.printf("10");
					Transport transport = esession.getTransport("smtp");
					System.out.printf("11");
					System.out.printf("host "+host+" from "+from+" pass "+pass);
					transport.connect(host, from, pass);
					System.out.printf("12");
					transport.sendMessage(message, message.getAllRecipients());
					System.out.printf("13");
					transport.close();
					System.out.printf("14");
					log.write("Info:"+"E-mail Send for client id: "+MasterId+"\n");
					System.out.printf("15");
				/*}*/
				this.getParent().showNotification("E-mail Send Successfully.");
				tbchk.get(i).setValue(false);	
			}
		}
		}
		catch(Exception exp){
			try {
				log.write("Error:"+exp+"\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.getParent().showNotification(
					"Error",
					exp+"",
					Notification.TYPE_ERROR_MESSAGE);
		}
		finally{
			try {
				log.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	public boolean isValid(String email) {
		String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
	   }
	
	private void reportGenerate(String iclientId,String fpath) throws HibernateException, JRException, IOException{
		System.out.printf("6");
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx = session.beginTransaction();
		HashMap <String,Object> hm = new HashMap <String,Object> ();
		hm.put("company", sessionBean.getCompany());
		hm.put("address", sessionBean.getCompanyAddress());
		hm.put("phone", sessionBean.getCompanyContact());
		hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
		hm.put("section",cmbDepartment.getItemCaption(cmbDepartment.getValue()));
		hm.put("month",fMonth.format(dSalaryMonth.getValue())+"-"+fYear.format(dSalaryMonth.getValue()));
		hm.put("year",fYear.format(dSalaryMonth.getValue()));
		hm.put("SysDate",reportTime.getTime);
		hm.put("logo", sessionBean.getCompanyLogo());
		
		hm.put("Unit", cmbUnit.getItemCaption(cmbUnit.getValue()));
		
		String query = "select * from funPaySlip ("
				+ "'"+dFormat.format(dSalaryMonth.getValue())+"',"
				+ "'"+cmbUnit.getValue().toString()+"',"
				+ "'"+(chkDepartmentAll.booleanValue()?"%":cmbDepartment.getValue()==null?"%":cmbDepartment.getValue())+"',"
				+ "'"+(chkSectionAll.booleanValue()?"%":cmbSection.getValue()==null?"%":cmbSection.getValue())+"',"
				+ "'"+iclientId+"') "
				+ "order by vEmployeeCode";
		
		System.out.println("Report :"+query);     
		hm.put("sql", query);
		System.out.printf("\npath"+fpath);
		FileOutputStream of = new FileOutputStream(fpath);
		
		JasperRunManager.runReportToPdfStream(getClass().getClassLoader().getResourceAsStream("report/account/hrmModule/rptpayslipEmail.jasper"),
				of, hm, session.connection());
		tx.commit();
		of.close();
		log.write("Info:"+"Report generated for client id: "+iclientId+"\n");
			/*}
		}*/
	}
	private void allBtnAction() {
		if (isAllSelect) {
			allBtn.setCaption("Select All");
			for (int i = 0; i < tbEmployeeName.size(); i++)
				if (!tbEmployeeName.get(i).getValue().toString().isEmpty()) {
					tbchk.get(i).setValue(false);
				}
		} else {
			allBtn.setCaption("Deselect All");
			for (int i = 0; i < tbEmployeeName.size(); i++)
				if (!tbEmployeeName.get(i).getValue().toString().isEmpty()) {
					tbchk.get(i).setValue(true);
				}
		}
		isAllSelect = !isAllSelect;
	}
	
	private AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("760px");
		setHeight("600px");
		
		
		
		// lblSalaryMonth
		lblSalaryMonth = new Label("Month :");
		lblSalaryMonth.setImmediate(false);
		lblSalaryMonth.setWidth("100.0%");
		lblSalaryMonth.setHeight("-1px");
		mainLayout.addComponent(lblSalaryMonth,"top:10.0px; left:10.0px;");

		// dSalaryMonth
		dSalaryMonth = new ComboBox();
		dSalaryMonth.setImmediate(true);
		dSalaryMonth.setWidth("160px");
		dSalaryMonth.setHeight("-1px");
		dSalaryMonth.setNullSelectionAllowed(true);
		dSalaryMonth.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(dSalaryMonth, "top:30.0px; left:10.0px;");

		lblUnit = new Label("Project :");
		lblUnit.setImmediate(false);
		lblUnit.setHeight("-1px");
		mainLayout.addComponent(lblUnit,"top:60.0px; left:10.0px;");

		// cmbDepartment
		cmbUnit = new ComboBox();
		cmbUnit.setImmediate(true);
		cmbUnit.setWidth("260px");
		cmbUnit.setHeight("-1px");
		cmbUnit.setNullSelectionAllowed(true);
		cmbUnit.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbUnit, "top:80.0px; left:10.0px;");

		// lblDepartment
		lblDepartment = new Label("Department :");
		lblDepartment.setImmediate(false);
		lblDepartment.setHeight("-1px");
		mainLayout.addComponent(lblDepartment,"top:110.0px; left:10.0px;");

		// cmbDepartment
		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("260px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setNullSelectionAllowed(true);
		cmbDepartment.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbDepartment, "top:130.0px; left:10.0px;");

		chkDepartmentAll = new CheckBox("All");
		chkDepartmentAll.setHeight("-1px");
		chkDepartmentAll.setWidth("-1px");
		chkDepartmentAll.setImmediate(true);
		mainLayout.addComponent(chkDepartmentAll, "top:132.0px; left:270.0px;");
		
		lblSection = new Label("Section :");
		lblSection.setImmediate(false);
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection,"top:160px; left:10.0px;");

		// cmbDepartment
		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("260px");
		cmbSection.setHeight("-1px");
		cmbSection.setNullSelectionAllowed(true);
		cmbSection.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbSection, "top:180px; left:10.0px;");

		chkSectionAll = new CheckBox("All");
		chkSectionAll.setHeight("-1px");
		chkSectionAll.setWidth("-1px");
		chkSectionAll.setImmediate(true);
		mainLayout.addComponent(chkSectionAll, "top:182px; left:270.0px;");
		
		
		txtEmailId=new TextRead();
		txtEmailId.setImmediate(true);
		txtEmailId.setWidth("160px");
		mainLayout.addComponent(new Label("Email Id :"), "top:10.0px; left:320.0px;");
		mainLayout.addComponent(txtEmailId, "top:30.0px; left:320.0px;");
		

		txtEmailPass.setImmediate(true);
		txtEmailPass.setWidth("300.0px");
		txtEmailPass.setHeight("30.0px");
		txtEmailPass.setVisible(false);
		mainLayout.addComponent(txtEmailPass, "top:30.0px; left:620.0px;");
		
		txtSubject = new TextField();
		txtSubject.setImmediate(true);
		txtSubject.setWidth("200px");
		txtSubject.setValue("PAY SLIP");
		mainLayout.addComponent(new Label("Subject :"), "top:60.0px; left:320.0px;");
		mainLayout.addComponent(txtSubject, "top:80.0px; left:320.0px;");

		txtMessage = new TextField();
		txtMessage.setWidth("400px");
		txtMessage.setHeight("80px");
		mainLayout.addComponent(new Label("Message :"), "top:110.0px; left:320.0px;");
		mainLayout.addComponent(txtMessage, "top:130.0px; left:320.0px;");
		
		
		table.setHeight("200px");
		table.setColumnCollapsingAllowed(true);
		table.addContainerProperty("#", CheckBox.class, new CheckBox());
		table.setColumnWidth("#", 20);
		table.addContainerProperty("Code", Label.class, new Label());
		table.setColumnWidth("Code", 70);
		table.addContainerProperty("Name", Label.class, new Label());
		table.setColumnWidth("Name", 260);
		table.addContainerProperty("Master Id", Label.class, new Label());
		table.setColumnWidth("Master Id", 235);
		table.setColumnCollapsed("Master Id", true);
		table.addContainerProperty("Email Id", Label.class, new Label());
		table.setColumnWidth("Email Id", 235);
		table.setFooterVisible(true);
		table.setColumnCollapsed("Email Id", true);
		tableini();

		mainLayout.addComponent(table, "top:230.0px; left:320.0px;");
		

		allBtn.setStyleName(Button.STYLE_LINK);

		button.btnPreview.setCaption("Send");
		mainLayout.addComponent(allBtn, "top:445.0px; left:320.0px;");
		mainLayout.addComponent(button, "top:470.0px; left:320.0px;");
		
		buttonActionAdd();
		Component comp[] = {txtEmailId, /*costCentre,*/ txtSubject, txtMessage, button.btnPreview};
		new FocusMoveByEnter(this, comp);
		
		return mainLayout;
	}

}
