package hrm.common.reportform;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class SendNotice extends Window {
	private SessionBean sessionBean;
	
	AbsoluteLayout mainLayout=new AbsoluteLayout();

	// private Panel vList = new Panel();
	public Table table = new Table();
	private ArrayList<CheckBox> vNo = new ArrayList<CheckBox>();
	public ArrayList<Label> tbMasterId = new ArrayList<Label>();
	public ArrayList<Label> tbMemberId = new ArrayList<Label>();
	public ArrayList<Label> tbMemerName = new ArrayList<Label>();
	public ArrayList<Label> tbEmailId = new ArrayList<Label>();
	public ArrayList<Label> tbUnit = new ArrayList<Label>();
	public ArrayList<Label> tbDepartment = new ArrayList<Label>();
	public ArrayList<Label> tbSection = new ArrayList<Label>();
	//private ComboBox cmbMemberType = new ComboBox("Employee Type :");
	private TextRead txtEmailId;
	private TextRead txtEmailPass = new TextRead();
	private TextField txtSubject;
	private TextField txtMessage;

	private NativeButton allBtn = new NativeButton("Select All");
	private ReportDate reportTime = new ReportDate();
	private SimpleDateFormat dDbFormat = new SimpleDateFormat("yyyy-MM-dd");

	private boolean isAllSelect = false;
	
	
	private FileWriter log;
	
	
	private ComboBox cmbSubject,cmbStatus;
	CheckBox chkStatusAll=new CheckBox("All");
	private Label lblFromDate;
	private PopupDateField dFromDate;

	private Label lblToDate;
	private PopupDateField dToDate;
	
	private Label lblMonth;
	private ComboBox dMonth;
	private PopupDateField dDate=new PopupDateField();

	private OptionGroup opgTimeSelect;
	private List<?> dateType = Arrays.asList(new String[]{"Monthly","Between Date"});
	
	private Label lblUnit;
	private Label lblDepartment;

	private ComboBox cmbUnit;
	private ComboBox cmbDepartment;
	private ComboBox cmbSection;
	private CheckBox chkDepartmentAll,chkUnitAll,chkSectionAll;

	TextField txtPath=new TextField();

	CommonButton button = new CommonButton("", "", "", "", "", "", "","Preview", "", "Exit");
	
	public SendNotice(SessionBean sessionBean, String EmailID, String EmailPass) {
		this.sessionBean = sessionBean;
		this.setCaption("SEND MAIL (NOTICE) :: "+ this.sessionBean.getCompany());
		this.setResizable(false);
		
		buildMainLayout();
		txtEmailId.setValue(EmailID);
		txtEmailPass.setValue(EmailPass);
		
		setContent(mainLayout);
		setEventAction();
		cmbStatusDataLoad();
		cmbUnitAddData();
		
	}
	private void cmbStatusDataLoad() {
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String status="Active";
			String sql = "select distinct 0,isActive from tbNoticeInfo";
			List<?> list = session.createSQLQuery(sql).list();
			cmbStatus.removeAllItems();
			for(Iterator<?> iter = list.iterator();iter.hasNext();)
			{
				Object element[] = (Object[]) iter.next();
				cmbStatus.addItem(element[1]);
				if(element[1].toString().equals("1"))
				{
					status="Active";
					cmbStatus.setItemCaption(element[1], status);
				}
				else if(element[1].toString().equals("0"))
				{
					status="Inactive";
					cmbStatus.setItemCaption(element[1], status);
				}
			}
		}
		catch(Exception exp)
		{
			showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void cmbSubjectDataLoad() {
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		String sql="";
		String status="%";
		if(!chkStatusAll.booleanValue())
		{
			status=cmbStatus.getValue().toString();
		}
		
		try
		{
			if(opgTimeSelect.getValue().toString()=="Monthly")
			{				
				sql="select distinct vNoticeId,REPLACE(vSubject,'~','''')vSubject from tbNoticeInfo where isActive like'"+status+"' " +
						"and MONTH(dDate) =MONTH('"+dMonth.getValue()+"') " +
						"and YEAR(dDate) =YEAR('"+dMonth.getValue()+"') ";
			}
			else
			{
				sql = "select distinct vNoticeId,REPLACE(vSubject,'~','''')vSubject from tbNoticeInfo where isActive like'"+status+"' " +
						"and dDate between '"+dDbFormat.format(dFromDate.getValue())+"' and '"+dDbFormat.format(dToDate.getValue())+"' ";
			}
			System.out.println("cmbSubjectDataLoad: "+sql);
			
			List<?> list = session.createSQLQuery(sql).list();
			cmbSubject.removeAllItems();
			for(Iterator<?> iter = list.iterator();iter.hasNext();)
			{
				Object element[] = (Object[]) iter.next();
				cmbSubject.addItem(element[0].toString());
				cmbSubject.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	public void opgActionWiseVisible(boolean b)
	{
		lblMonth.setVisible(b);
	    dMonth.setVisible(b);
	    lblFromDate.setVisible(!b);
	    dFromDate.setVisible(!b);
	    lblToDate.setVisible(!b);
	    dToDate.setVisible(!b);
	}
	private void opgTimeSelectAction()
	{
		if(opgTimeSelect.getValue().toString()=="Monthly")
		{
			opgActionWiseVisible(true);
			dFromDate.setValue(new Date());
			dToDate.setValue(new Date());
			
			cmbSubject.removeAllItems();
			if(dMonth.getValue()!=null)
			{
				tableClear();
				cmbSubjectDataLoad();
			}
		}
		else
		{
			opgActionWiseVisible(false);
			dFromDate.setValue(new Date());
			dToDate.setValue(new Date());
			
			cmbSubject.removeAllItems();
			if(dFromDate.getValue()!=null && dToDate.getValue()!=null)
			{
				tableClear();
				cmbSubjectDataLoad();
			}
		}
	}
	
	private void setEventAction() 
	{
		cmbStatus.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(cmbStatus.getValue()!=null)
				{
					tableClear();
					dMonth.removeAllItems();
					chkUnitAll.setValue(false);
					cmbUnit.setEnabled(true);
					chkDepartmentAll.setValue(false);
					cmbDepartment.setEnabled(true);
					cmbMonthDataLoad();
				}
			}
		});
		chkStatusAll.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				if(chkStatusAll.booleanValue())
				{
					cmbStatus.setValue(null);
					cmbStatus.setEnabled(false);

					dMonth.removeAllItems();
					chkUnitAll.setValue(false);
					cmbUnit.setEnabled(true);
					chkDepartmentAll.setValue(false);
					cmbDepartment.setEnabled(true);
					tableClear();
					cmbMonthDataLoad();
				}
				else{
					dMonth.removeAllItems();
					chkUnitAll.setValue(false);
					cmbUnit.setEnabled(true);
					chkDepartmentAll.setValue(false);
					cmbDepartment.setEnabled(true);
					cmbStatus.setEnabled(true);
					tableClear();
				}
			}
		});
		
		opgTimeSelect.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbStatus.getValue()!=null)
				{
					dMonth.removeAllItems();
					chkUnitAll.setValue(false);
					cmbUnit.setEnabled(true);
					chkDepartmentAll.setValue(false);
					cmbDepartment.setEnabled(true);
					tableClear();
					opgTimeSelectAction();
				}
			}
		});
		dMonth.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbStatus.getValue()!=null || chkStatusAll.booleanValue())
				{
					if(opgTimeSelect.getValue().toString()=="Monthly")
					{
						if(dMonth.getValue()!=null)
						{
							chkUnitAll.setValue(false);
							cmbUnit.setEnabled(true);
							chkDepartmentAll.setValue(false);
							cmbDepartment.setEnabled(true);
							cmbSubject.removeAllItems();
							tableClear();
							cmbSubjectDataLoad();
						}
					}
					else
					{
						if(dFromDate.getValue()!=null && dToDate.getValue()!=null)
						{
							chkUnitAll.setValue(false);
							cmbUnit.setEnabled(true);
							chkDepartmentAll.setValue(false);
							cmbDepartment.setEnabled(true);
							cmbSubject.removeAllItems();
							tableClear();
							cmbSubjectDataLoad();
						}
					}
				}
			}
		});
		dFromDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbStatus.getValue()!=null || chkStatusAll.booleanValue())
				{
					if(opgTimeSelect.getValue().toString()=="Monthly")
					{
						if(dMonth.getValue()!=null)
						{
							chkUnitAll.setValue(false);
							cmbUnit.setEnabled(true);
							chkDepartmentAll.setValue(false);
							cmbDepartment.setEnabled(true);
							cmbSubject.removeAllItems();
							tableClear();
							cmbSubjectDataLoad();
						}
					}
					else
					{
						if(dFromDate.getValue()!=null && dToDate.getValue()!=null)
						{
							chkUnitAll.setValue(false);
							cmbUnit.setEnabled(true);
							chkDepartmentAll.setValue(false);
							cmbDepartment.setEnabled(true);
							cmbSubject.removeAllItems();
							tableClear();
							cmbSubjectDataLoad();
						}
					}
				}
			}
		});
		dToDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbStatus.getValue()!=null || chkStatusAll.booleanValue())
				{
					if(opgTimeSelect.getValue().toString()=="Monthly")
					{
						if(dMonth.getValue()!=null)
						{
							chkUnitAll.setValue(false);
							cmbUnit.setEnabled(true);
							chkDepartmentAll.setValue(false);
							cmbDepartment.setEnabled(true);
							cmbSubject.removeAllItems();
							tableClear();
							cmbSubjectDataLoad();
						}
					}
					else
					{
						if(dFromDate.getValue()!=null && dToDate.getValue()!=null)
						{
							chkUnitAll.setValue(false);
							cmbUnit.setEnabled(true);
							chkDepartmentAll.setValue(false);
							cmbDepartment.setEnabled(true);
							cmbSubject.removeAllItems();
							tableClear();
							cmbSubjectDataLoad();
						}
					}
				}
			}
		});
		cmbSubject.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(cmbSubject.getValue()!=null)
				{
					noticeDataLoad(cmbSubject.getValue().toString());
				}
			}
		});
		
		cmbUnit.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				txtPath.setValue("");
				if(cmbUnit.getValue()!= null)
				{
					tableClear();
					cmbDepartment.removeAllItems();
					cmbDepartmentAddData(cmbUnit.getValue().toString());
					chkDepartmentAll.setValue(false);
					cmbDepartment.setEnabled(true);
				}
			}
		});
		
		chkUnitAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(chkUnitAll.booleanValue())
				{
					tableClear();
					cmbUnit.setValue("");
					cmbUnit.setEnabled(false);
					cmbDepartment.removeAllItems();
					cmbDepartmentAddData("%");
					chkDepartmentAll.setValue(false);
					cmbDepartment.setEnabled(true);
				}
				else
				{
					tableClear();
					cmbUnit.setEnabled(true);
					chkDepartmentAll.setValue(false);
					cmbDepartment.setEnabled(true);
				}
			}
		});

		chkDepartmentAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(chkDepartmentAll.booleanValue())
				{
					if(cmbUnit.getValue()!=null || chkUnitAll.booleanValue())
					{
						tableClear();
						cmbSection.removeAllItems();
						chkSectionAll.setValue(false);
						cmbDepartment.setValue(null);
						cmbDepartment.setEnabled(false);
						cmbSectionAddData(chkUnitAll.booleanValue()?"%":cmbUnit.getValue().toString(), "%");
					}
				}
				else
				{
					tableClear();
					cmbDepartment.setEnabled(true);
					cmbSection.removeAllItems();
					chkSectionAll.setValue(false);
				}
			}
		});

		cmbDepartment.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbUnit.getValue()!=null || chkUnitAll.booleanValue())
				{
					if(cmbDepartment.getValue()!=null)
					{
						tableClear();
						cmbSection.removeAllItems();
						chkSectionAll.setValue(false);
						cmbSectionAddData(chkUnitAll.booleanValue()?"%":cmbUnit.getValue().toString(), cmbDepartment.getValue().toString());
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
								insertData();
								
							}
						}
					});
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
	private void insertData()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		Transaction tx=session.beginTransaction();
		try 
		{
			for(int i=0;i<tbEmailId.size();i++)
			{
				if(!tbEmailId.isEmpty())
				{
					if(vNo.get(i).booleanValue())
					{
						String query = "insert into tbSendNotice(vNoticeId,vEmployeeID,vEmployeeCode,vEmployeeName,dDate,vSubject,UserId,UserIp,EntryTime) " +
								"values('"+cmbSubject.getValue()+"','"+tbMasterId.get(i).getValue()+"','"+tbMemberId.get(i).getValue()+"'," +
								"'"+tbMemerName.get(i).getValue()+"','"+dDbFormat.format(dDate.getValue())+"','"+txtSubject.getValue().toString().trim().replaceAll("'", "~")+"'," +
								"'"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP)";
						System.out.println("insertData: "+query);
						session.createSQLQuery(query).executeUpdate();
					}
				}
			}
			
			tx.commit();
			this.getParent().showNotification("All information save successfully.");
			//txtClear();
			//compInit(true);
		} 
		catch(Exception ex)
		{
			tx.rollback();
			this.getParent().showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void noticeDataLoad(String vNoticeId) 
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "select vSubject,vDescription from tbNoticeInfo where vNoticeId='"+vNoticeId+"'";
			System.out.println("findInitialise: "+sql);
			List <?> led = session.createSQLQuery(sql).list();

			if (led.iterator().hasNext()) 
			{
				Object[] element = (Object[]) led.iterator().next();
				txtSubject.setValue(element[0].toString().replaceAll("~", "'"));
				txtMessage.setValue(element[1].toString().replaceAll("~", "'"));
			}
		}
		catch (Exception exp) 
		{
			showNotification("Error", exp + "",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	public void cmbMonthDataLoad()
	{
		String status="%";
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		

		try
		{
			String query="select distinct convert(date,cast(cast(Year(dDate)as varchar(120))+'-'+cast(Month(dDate)as varchar(120))+'-'+'01' as Date),105) Id," +
					"cast(dateName(MM,dDate)as varchar(120))+'-'+cast(Year(dDate)as varchar(120)) Name,cast(MONTH(dDate) as int) dDeliveryDate," +
					"cast(year(dDate) as int) dDeliveryDateYear from tbNoticeInfo order by  dDeliveryDateYear desc,dDeliveryDate desc";
			System.out.println("cmbMonthDataLoad :"+query);
			
			List <?> list=session.createSQLQuery(query).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				dMonth.addItem(element[0]);
				dMonth.setItemCaption(element[0],(element[1].toString()));
				
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbMonthDataLoad",exp+"",Notification.TYPE_ERROR_MESSAGE);
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
			String query="select distinct vUnitId,vUnitName from tbEmpOfficialPersonalInfo where bStatus=1 order by vUnitName";
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
	private void txtPathDataLoad() {
		/*Session session = SessionFactoryUtil.getInstance().openSession();
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
		finally{session.close();}*/
	}
	public void cmbDepartmentAddData(String unit)
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();

		try
		{		
			String query="select distinct vDepartmentId,vDepartmentName from tbEmpOfficialPersonalInfo where bStatus=1 and vUnitId like '"+unit+"' order by vDepartmentName";
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
	public void cmbSectionAddData(String unit,String deptId)
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();

		try
		{		
			String query="select distinct vSectionId,vSectionName from tbEmpOfficialPersonalInfo where bStatus=1 and vUnitId like '"+unit+"' and vDepartmentId like '"+deptId+"' order by vSectionName";
			System.out.println("cmbDepartmentAddData: "+query);
			
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
	private void tableDataLoad() {
		Session session = SessionFactoryUtil.getInstance().openSession();
		String sql="";
		String unit="%",section="%",deptId="%";
		
		if(!chkDepartmentAll.booleanValue())
		{
			deptId=cmbDepartment.getValue().toString();
		}
		if(!chkUnitAll.booleanValue())
		{
			unit=cmbUnit.getValue().toString();
		}
		if(!chkSectionAll.booleanValue())
		{
			section=cmbSection.getValue().toString();
		}		
		try 
		{
			sql="select distinct a.vEmployeeId,vEmployeeCode,a.vEmployeeName,vEmailAddress,vUnitName,vDepartmentName,c.iRank,a.dJoiningDate,vSectionName " +
					"from tbEmpOfficialPersonalInfo a " +
					"inner join tbEmpDesignationInfo b on a.vEmployeeId=b.vEmployeeId " +
					"inner join tbDesignationInfo c on b.vDesignationId=c.vDesignationId  " +
					"where  vEmailAddress like '%@%' " +
					"and vDepartmentId like '"+deptId+"' " +
					"and vUnitId like'"+unit+"' and vSectionId like '"+section+"' " +
					"order by vUnitName,vDepartmentName,c.iRank,a.dJoiningDate";
			
			System.out.println("tableDataLoad: "+sql);
			
			List<?> list = session.createSQLQuery(sql).list();
			if (!list.isEmpty()) 
			{
				int index = 0;
				Iterator<?> iter = list.iterator();
				while (iter.hasNext()) 
				{
					Object[] element = (Object[]) iter.next();
					tbDepartment.get(index).setValue(element[5]);
					tbUnit.get(index).setValue(element[4]);
					tbEmailId.get(index).setValue(element[3]);
					tbMasterId.get(index).setValue(element[0]);
					tbMemberId.get(index).setValue(element[1]);
					tbMemerName.get(index).setValue(element[2]);
					tbSection.get(index).setValue(element[8]);

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
		vNo.add(ar, new CheckBox());
		vNo.get(ar).setWidth("100%");

		tbMemberId.add(ar, new Label());
		tbMemberId.get(ar).setWidth("100%");

		tbMemerName.add(ar, new Label());
		tbMemerName.get(ar).setWidth("100%");

		tbMasterId.add(ar, new Label());
		tbMasterId.get(ar).setWidth("100%");

		tbEmailId.add(ar, new Label());
		tbEmailId.get(ar).setWidth("100%");

		tbUnit.add(ar, new Label());
		tbUnit.get(ar).setWidth("100%");

		tbDepartment.add(ar, new Label());
		tbDepartment.get(ar).setWidth("100%");
		
		tbSection.add(ar, new Label());
		tbSection.get(ar).setWidth("100%");

		table.addItem(new Object[] { vNo.get(ar), tbMemberId.get(ar),tbMemerName.get(ar), tbMasterId.get(ar), tbEmailId.get(ar)
				,tbUnit.get(ar), tbDepartment.get(ar),tbSection.get(ar) },ar);

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
		for (int i = 0; i < tbMemberId.size(); i++) {
			tbMemberId.get(i).setValue("");
			tbMasterId.get(i).setValue("");
			tbMemerName.get(i).setValue("");
			tbEmailId.get(i).setValue("");
			tbUnit.get(i).setValue("");
			tbDepartment.get(i).setValue("");
			vNo.get(i).setValue(false);
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
			log = new FileWriter("D:/Tomcat 7.0/webapps/report/TechniPlex/tp/log.txt");
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

			for(int i=0;i<vNo.size();i++)
			{
				if(Boolean.valueOf(vNo.get(i).getValue().toString()))
				{
				javax.mail.Session esession = javax.mail.Session.getDefaultInstance(props, null);
				MasterId=tbMasterId.get(i).getValue().toString();
				/*for(int j=0;j<vNo.size();j++)
				{*/
					System.out.printf("4");
					System.out.printf("\n4.1"+MasterId);
					reportGenerate(MasterId,sessionBean.emailPath+"Email/"+MasterId+"_"+"_"+cmbSubject.getItemCaption(cmbSubject.getValue())+".pdf");
					
				
					String EmailTo=tbEmailId.get(i).getValue().toString();
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
					DataSource source = new FileDataSource(sessionBean.emailPath+"Email/"+MasterId+"_"+"_"+cmbSubject.getItemCaption(cmbSubject.getValue())+".pdf");
					messageBodyPart.setDataHandler( new DataHandler(source));
					messageBodyPart.setFileName(sessionBean.emailPath+"Email/"+MasterId+"_"+"_"+cmbSubject.getItemCaption(cmbSubject.getValue())+".pdf");
					multipart.addBodyPart(messageBodyPart);
					System.out.printf("9");
					// Put parts in message
					message.setContent(multipart);
					System.out.printf("10");
					Transport transport = esession.getTransport("smtp");
					System.out.println(sessionBean.emailPath+"Email/"+MasterId+"_"+"_"+cmbSubject.getItemCaption(cmbSubject.getValue())+".pdf");
					System.out.printf("11");
					System.out.printf("host "+host+" from "+from+" pass "+pass);
					transport.connect(host, from, pass);
					System.out.printf("12");
					transport.sendMessage(message, message.getAllRecipients());
					System.out.printf("13");
					transport.close();
					System.out.printf("14");
					//log.write("Info:"+"E-mail Send for client id: "+MasterId+"\n");
					System.out.printf("15");
				this.getParent().showNotification("E-mail Send Successfully.");	
			}
		}
		}
		catch(Exception exp){
			showNotification("mail send :"+exp,Notification.TYPE_ERROR_MESSAGE);
		}

	}

	private void reportGenerate(String iclientId, String fpath) throws HibernateException, JRException, IOException 
	{	
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx = session.beginTransaction();
		String query = "";

		query="select dDate,vNoticeId,REPLACE(vSubject,'~','''')vSubject,REPLACE(vDescription,'~','''')vDescription from tbNoticeInfo where vNoticeId='"+cmbSubject.getValue()+"'";
		
		if(queryValueCheck(query))
		{
			HashMap <String,Object> hm = new HashMap <String,Object> ();
			hm.put("company", "NK Group");
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("userName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			hm.put("SysDate",reportTime.getTime);
			hm.put("logo", txtPath.getValue().toString().isEmpty()?"0":txtPath.getValue().toString());
			hm.put("sql", query);
			
			FileOutputStream of = new FileOutputStream(fpath);

			JasperRunManager.runReportToPdfStream(getClass().getClassLoader().getResourceAsStream("report/account/hrmModule/RptNoticeInfo.jasper"), of, hm,session.connection());
			tx.commit();
			of.close();
		}		
	}
	
	private boolean queryValueCheck(String sql)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try 
		{
			Iterator <?> iter = session.createSQLQuery(sql).list().iterator();

			if (iter.hasNext()) 
			{
				return true;
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		finally{session.close();}
		return false;
	}

	private void allBtnAction() {
		if (isAllSelect) {
			allBtn.setCaption("Select All");
			for (int i = 0; i < tbMemerName.size(); i++)
				if (!tbMemerName.get(i).getValue().toString().isEmpty()) {
					vNo.get(i).setValue(false);
				}
		} else {
			allBtn.setCaption("Deselect All");
			for (int i = 0; i < tbMemerName.size(); i++)
				if (!tbMemerName.get(i).getValue().toString().isEmpty()) {
					vNo.get(i).setValue(true);
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
		setWidth("780px");
		setHeight("600px");
		
		cmbStatus=new ComboBox();
		cmbStatus.setWidth("110.0px");
		cmbStatus.setHeight("-1px");
		cmbStatus.setImmediate(true);
		cmbStatus.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Status : "), "top:20.0px;left:08.0px;");
		mainLayout.addComponent(cmbStatus, "top:18.0px;left:90px");
		chkStatusAll.setImmediate(true);
		mainLayout.addComponent(chkStatusAll, "top:20.0px;left:200px");
		
		opgTimeSelect=new OptionGroup("",dateType);
		opgTimeSelect.select("Monthly");
		opgTimeSelect.setImmediate(true);
		opgTimeSelect.setStyleName("horizontal");
		mainLayout.addComponent(opgTimeSelect, "top:50.0px; left:90px;");
		
		//lblMonth
		lblMonth = new Label("Month :");
		lblMonth.setImmediate(false);
		lblMonth.setWidth("100.0%");
		lblMonth.setHeight("-1px");
		mainLayout.addComponent(lblMonth,"top:80.0px; left:08.0px;");
		

		// dMonth
		dMonth = new ComboBox();
		dMonth.setImmediate(true);
		dMonth.setWidth("150px");
		dMonth.setHeight("-1px");
		dMonth.setNullSelectionAllowed(true);
		dMonth.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(dMonth, "top:78.0px; left:90px;");
		
		// lblFromDate
		lblFromDate = new Label("From :");
		lblFromDate.setImmediate(false);
		lblFromDate.setWidth("100.0%");
		lblFromDate.setHeight("-1px");
		mainLayout.addComponent(lblFromDate,"top:80.0px; left:08.0px;");
		lblFromDate.setVisible(false);
		// dFromDate
		dFromDate = new PopupDateField();
		dFromDate.setImmediate(true);
		dFromDate.setWidth("100px");
		dFromDate.setHeight("-1px");
		dFromDate.setDateFormat("dd-MM-yyyy");
		dFromDate.setValue(new java.util.Date());
		dFromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dFromDate, "top:78.0px; left:90px;");
		dFromDate.setVisible(false);
		
		// lblToDate
		lblToDate = new Label("To");
		lblToDate.setImmediate(false);
		lblToDate.setWidth("100.0%");
		lblToDate.setHeight("-1px");
		mainLayout.addComponent(lblToDate,"top:80.0px; left:175.0px;");
		lblToDate.setVisible(false);

		// dToDate
		dToDate = new PopupDateField();
		dToDate.setImmediate(true);
		dToDate.setWidth("100px");
		dToDate.setHeight("-1px");
		dToDate.setDateFormat("dd-MM-yyyy");
		dToDate.setValue(new java.util.Date());
		dToDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dToDate, "top:80.0px; left:190.0px;");
		dToDate.setVisible(false);
		
		cmbSubject=new ComboBox();
		cmbSubject.setWidth("220.0px");
		cmbSubject.setHeight("-1px");
		cmbSubject.setImmediate(true);
		cmbSubject.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Subject : "), "top:110.0px;left:08.0px;");
		mainLayout.addComponent(cmbSubject, "top:108.0px;left:90px");
		
		lblUnit = new Label("Project :");
		lblUnit.setImmediate(false);
		lblUnit.setHeight("-1px");
		mainLayout.addComponent(lblUnit,"top:140.0px; left:08.0px;");

		// cmbDepartment
		cmbUnit = new ComboBox();
		cmbUnit.setImmediate(true);
		cmbUnit.setWidth("220.0px");
		cmbUnit.setHeight("-1px");
		cmbUnit.setNullSelectionAllowed(true);
		cmbUnit.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbUnit, "top:138.0px; left:90px;");

		chkUnitAll = new CheckBox("All");
		chkUnitAll.setHeight("-1px");
		chkUnitAll.setWidth("-1px");
		chkUnitAll.setImmediate(true);
		mainLayout.addComponent(chkUnitAll, "top:140.0px; left:315px;");
		
		lblDepartment = new Label("Department :");
		lblDepartment.setImmediate(false);
		lblDepartment.setHeight("-1px");
		mainLayout.addComponent(lblDepartment,"top:170.0px; left:08.0px;");
		
		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("220.0px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setNullSelectionAllowed(true);
		cmbDepartment.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbDepartment, "top:168.0px; left:90px;");

		chkDepartmentAll = new CheckBox("All");
		chkDepartmentAll.setHeight("-1px");
		chkDepartmentAll.setWidth("-1px");
		chkDepartmentAll.setImmediate(true);
		mainLayout.addComponent(chkDepartmentAll, "top:170.0px; left:315px;");
		
		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("220.0px");
		cmbSection.setHeight("-1px");
		cmbSection.setNullSelectionAllowed(true);
		cmbSection.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Section :"),"top:200px; left: 08px");
		mainLayout.addComponent(cmbSection, "top:198px; left:90px;");

		chkSectionAll = new CheckBox("All");
		chkSectionAll.setHeight("-1px");
		chkSectionAll.setWidth("-1px");
		chkSectionAll.setImmediate(true);
		mainLayout.addComponent(chkSectionAll, "top:200px; left:315px;");
		

		dDate = new PopupDateField();
		dDate.setImmediate(true);
		dDate.setWidth("100px");
		dDate.setHeight("-1px");
		dDate.setDateFormat("dd-MM-yyyy");
		dDate.setValue(new java.util.Date());
		dDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(new Label("Date: "), "top:230px; left:08.0px;");
		mainLayout.addComponent(dDate, "top:228px; left:90px;");
		dDate.setEnabled(false);
		
		txtEmailId=new TextRead();
		txtEmailId.setImmediate(true);
		txtEmailId.setWidth("160px");
		mainLayout.addComponent(new Label("Email Id :"), "top:10.0px; left:360.0px;");
		mainLayout.addComponent(txtEmailId, "top:30.0px; left:360.0px;");
		
		txtEmailPass.setImmediate(true);
		txtEmailPass.setWidth("300.0px");
		txtEmailPass.setHeight("30.0px");
		txtEmailPass.setVisible(false);
		mainLayout.addComponent(txtEmailPass, "top:30.0px; left:360.0px;");
		
		txtSubject = new TextField();
		txtSubject.setImmediate(true);
		txtSubject.setWidth("200px");
		txtSubject.setEnabled(false);
		mainLayout.addComponent(new Label("Subject :"), "top:60.0px; left:360.0px;");
		mainLayout.addComponent(txtSubject, "top:80.0px; left:360.0px;");

		txtMessage = new TextField();
		txtMessage.setWidth("400px");
		txtMessage.setHeight("80px");
		txtMessage.setEnabled(false);
		mainLayout.addComponent(new Label("Message :"), "top:110.0px; left:360.0px;");
		mainLayout.addComponent(txtMessage, "top:130.0px; left:360.0px;");
		
		table.setHeight("200px");
		table.setColumnCollapsingAllowed(true);
		table.addContainerProperty("#", CheckBox.class, new CheckBox());
		table.setColumnWidth("#", 20);
		
		table.addContainerProperty("Emp. ID", Label.class, new Label());
		table.setColumnWidth("Emp. ID", 40);
		
		table.addContainerProperty("Employee Name", Label.class, new Label());
		table.setColumnWidth("Employee Name", 180);
		//table.setColumnCollapsed("Employee Name", true);
		
		table.addContainerProperty("Master Id", Label.class, new Label());
		table.setColumnWidth("Master Id", 65);
		table.setColumnCollapsed("Master Id", true);
		
		table.addContainerProperty("Email Id", Label.class, new Label());
		table.setColumnWidth("Email Id", 150);
		//table.setColumnCollapsed("Email Id", true);

		table.addContainerProperty("Project", Label.class, new Label());
		table.setColumnWidth("Project", 130);

		table.addContainerProperty("Department", Label.class, new Label());
		table.setColumnWidth("Department", 130);
		
		table.addContainerProperty("Section", Label.class, new Label());
		table.setColumnWidth("Section", 130);
		tableini();
		

		mainLayout.addComponent(table, "top:260px; left:15.0px;");
		allBtn.setStyleName(Button.STYLE_LINK);
		table.setColumnCollapsed("Section", true);

		button.btnPreview.setCaption("Send");
		mainLayout.addComponent(allBtn, "top:475px; left:15.0px;");
		mainLayout.addComponent(button, "top:500px; left:15.0px;");
		
		buttonActionAdd();
		Component comp[] = {txtEmailId, /*costCentre,*/ txtSubject, txtMessage, button.btnPreview};
		new FocusMoveByEnter(this, comp);
		
		return mainLayout;
	}

}
