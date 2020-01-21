package com.appform.hrmModule;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

import com.common.share.*;
import com.vaadin.ui.CheckBox;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.themes.BaseTheme;

@SuppressWarnings("serial")
public class TabOfficialInfo extends VerticalLayout
{
	public AbsoluteLayout mainLayout;
	SessionBean sessionBean;

	public ComboBox cmbEmployeeType;
	public ComboBox cmbServiceType;

	public ComboBox cmbStatus;
	public ComboBox cmbReligion;

	public CheckBox chkPhysicallyDisable;

	public CheckBox chkOtEnable,checkFridayEnabled,checkHolidayEnabled;

	public ComboBox cmbSection;
	public ComboBox cmbUnitName;
	public ComboBox cmbDesignation;
	public ComboBox cmbEmployeeShiftGroup;
	public ComboBox cmbGrade;
	public ComboBox cmbDepartment;
	public ComboBox cmbShift;
	public ComboBox cmbLevel;
	public ComboBox cmbValidDuration;
	

	public PopupDateField dInterviewDate;
	public PopupDateField dDateOfBirth;
	public PopupDateField dApplicationDate;
	public PopupDateField dJoiningDate;
	public PopupDateField dConfirmationDate;
	public PopupDateField dStatusDate;
	public PopupDateField dValidDate;

	public TextRead txtEmployeeID;
	public TextRead txtImageBox;

	public TextField txtNationality;
	public TextField txtEmail;
	public TextField txtContact;
	public TextField txtEmployeeName;
	public TextField txtProximityId;
	public TextField txtFingerId;
	public TextField txtEmployeeCode;
	public TextField txtNid;
	public TextField txtFamilyName;
	public TextField txtGivenName;
	public TextField txtUnitName;
	public TextField txtCareerPeriod;

	public NativeButton btnPlusSection;
	public NativeButton btnPlusUnit;
	public NativeButton btnPlusDesignation;
	public NativeButton btnPlusGroup;
	public NativeButton btnPlusSalaryRegister;
	public NativeButton btnPlusPayScale;
	public NativeButton btnPlusShift;
	public NativeButton btnPlusDepartment;
	public Button btnBirthPreview;
	public Button btnNidPreview;
	public Button btnApplicationPreview;
	public Button btnJoinPreview;
	public Button btnConPreview;
	public Button btnServiceAgreementPreview;

	public FileUpload Image;

	public ImmediateFileUpload btnConfirmdate;
	public ImmediateFileUpload btnDateofBirth;
	public ImmediateFileUpload btnAppDate;
	public ImmediateFileUpload btnJoiningDate;
	public ImmediateFileUpload btnNid;
	public ImmediateFileUpload btnServiceAgreement;

	public String birthPdf = null;
	public String birthFilePathTmp = "";
	public String nidPdf = null;
	public String nidFilePathTmp = "";
	public String applicationPdf = null;
	public String applicationFilePathTmp = "";
	public String joinPdf = null;
	public String joinFilePathTmp = "";
	public String conPdf = null;
	public String conFilePathTmp = "";
	public String conServiceAgreementPdf=null;
	public String conServiceAgreementFilePathTmp="";

	public ArrayList<Component> allComp = new ArrayList<Component>();
	public OptionGroup RadioGender;

	public Label lblImage;
	public Label lblEmployeeStatus;

	private static final List<String> religion = Arrays.asList(new String[] {"Islam","Hindu","Buddism","Cristian","Other"});
	private static final List<String> level = Arrays.asList(new String[] {"Good","Medium","Low","No"});
	private static final List<String> sex = Arrays.asList(new String[] {"M", "F"});
	private static final List<String> employeeType =Arrays.asList( new String[] { "Regular","Temporary"});
	private static final List<String> status =Arrays.asList( new String[] {"On Duty", "Discontinue","Resignation", "Retired", "Dismiss", "Terminated","Retrenchment"});
	private static final List<String> ServiceType =Arrays.asList( new String[] {"Engineer/Officer", "Supervisor", "Worker"});
	public static final List<String> UnitType = Arrays.asList(new String[] {"CEEL Corporate Office Employees", "Corporate Office Employees" });

	String employeeImage = "0";
	String employeeBirth = "0";
	String employeeNid = "0";
	String employeeApplication = "0";
	String employeeJoin = "0";
	String employeeServiceAgreement="0";
	
	public AmountField txtValidYear;
	public Label lblYear;

	public ComboBox cmbProbationPeriod;
	public Label lblProbationMonth=new Label("Probation Period");
	private static final List<String> ProbationPeriod =Arrays.asList( new String[] {"0","1","2","3","4","5","6","7","8","9","10","11","12"});
	

	public TabOfficialInfo(SessionBean sessionBean) 
	{
		this.sessionBean = sessionBean;

		employeeImage = "D:/Tomcat 7.0/webapps/report/"+sessionBean.getContextName()+"/employee/";
		employeeBirth = "D:/Tomcat 7.0/webapps/report/"+sessionBean.getContextName()+"/employee/birth/";
		employeeNid = "D:/Tomcat 7.0/webapps/report/"+sessionBean.getContextName()+"/employee/nid/";
		employeeApplication = "D:/Tomcat 7.0/webapps/report/"+sessionBean.getContextName()+"/employee/application/";
		employeeJoin = "D:/Tomcat 7.0/webapps/report/"+sessionBean.getContextName()+"/employee/join/";
		employeeServiceAgreement="D:/Tomcat 7.0/webapps/report/"+sessionBean.getContextName()+"/employee/Service_Agreement/";

		buildMainLayout();
		addComponent(mainLayout);

		
		addDesignationInfo();
		addShiftGroupInfo();
		addSalaryRegister();
		setEventAction();
		addUnitInfo();
		addDepartmentInfo();
		addSectionInfo();
		addShiftInfo();
	}
	/*public void allTrueFalse()
	{
		checkFridayEnabled.setValue(false);
		checkHolidayEnabled.setValue(false);
		chkOtEnable.setValue(false);
	}
*/
	private void addMonth(){
		Date date1=(Date) dJoiningDate.getValue();
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(date1);
		calendar.add(calendar.MONTH, Integer.parseInt(cmbProbationPeriod.getValue().toString()));
		
		Date date=new Date();
		date=calendar.getTime();
		dConfirmationDate.setValue(date);
	}
	
	private void setEventAction()
	{

		cmbProbationPeriod.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbProbationPeriod.getValue()!=null){
					if(dJoiningDate.getValue()!=null)
					{
						addMonth();	
					}
				}
				else{
					dConfirmationDate.setValue(new java.util.Date());
				}
			}
		});

		dJoiningDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(dJoiningDate.getValue()!=null){
					if(cmbProbationPeriod.getValue()!=null)
					{
						addMonth();	
					}
				}
				else{
					dConfirmationDate.setValue(new java.util.Date());
				}
			}
		});
		checkFridayEnabled.addListener(new ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				//allTrueFalse();
				if(checkFridayEnabled.booleanValue())
				{
					checkFridayEnabled.setValue(true);
				}
				else
				{
					checkFridayEnabled.setValue(false);
				}
			}
		});
		checkHolidayEnabled.addListener(new ClickListener() {
					
			public void buttonClick(ClickEvent event) {
				//allTrueFalse();
				if(checkHolidayEnabled.booleanValue())
				{
					checkHolidayEnabled.setValue(true);
				}
				else
				{
					checkHolidayEnabled.setValue(false);
				}
			}
		});
		chkOtEnable.addListener(new ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				//allTrueFalse();
				if(chkOtEnable.booleanValue())
				{
					chkOtEnable.setValue(true);
					
				}
				else
				{
					chkOtEnable.setValue(false);
				}
			}
		});

		btnDateofBirth.upload.addListener(new Upload.SucceededListener() 
		{
			public void uploadSucceeded(SucceededEvent event) 
			{
				btnBirthPreview.setCaption("View");
				birthPath(0,"");
			}
		});

		btnNid.upload.addListener(new Upload.SucceededListener() 
		{
			public void uploadSucceeded(SucceededEvent event) 
			{
				btnNidPreview.setCaption("View");
				nidPath(0,"");
			}
		});

		btnAppDate.upload.addListener(new Upload.SucceededListener() 
		{
			public void uploadSucceeded(SucceededEvent event) 
			{
				btnApplicationPreview.setCaption("View");
				applicationPath(0,"");
			}
		});

		btnJoiningDate.upload.addListener(new Upload.SucceededListener() 
		{
			public void uploadSucceeded(SucceededEvent event) 
			{
				btnJoinPreview.setCaption("View");
				joinPath(0,"");
			}
		});

		btnConfirmdate.upload.addListener(new Upload.SucceededListener() 
		{
			public void uploadSucceeded(SucceededEvent event) 
			{
				btnConPreview.setCaption("View");
				conPath(0,"");
			}
		});
		btnServiceAgreement.upload.addListener(new Upload.SucceededListener() 
		{
			public void uploadSucceeded(SucceededEvent event) 
			{
				btnServiceAgreementPreview.setCaption("View");
				serviceAgreementPath(0,"");
			}
		});
	}

	public void addSectionInfo()
	{
		cmbSection.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> list = session.createSQLQuery("SELECT vSectionId,vSectionName FROM tbSectionInfo where isActive=1 order by vSectionName" +
					" where isActive = 1").list();

			for(Iterator <?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbSection.addItem(element[0]);
				cmbSection.setItemCaption(element[0], element[1].toString());
			}
		}
		
		catch (Exception e)
		{
			System.out.println("Unable to get addSectionInfo data"+e);
		}
		finally{session.close();}
	}
	public void addShiftInfo()
	{
		cmbShift.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try{
			String sql="select vShiftId,vShiftName from tbShiftInfo order by vShiftName ";
			Iterator<?> iter=session.createSQLQuery(sql).list().iterator();
			while(iter.hasNext())
			{
				Object[] element=(Object[])iter.next();
				cmbShift.addItem(element[0].toString());
				cmbShift.setItemCaption(element[0].toString(), element[1].toString());
			}
		}catch(Exception exp)
		{
			System.out.println("Unable to get addShift data :"+exp);
		}
	}
	public void addDesignationInfo()
	{
		cmbDesignation.removeAllItems();

		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> list = session.createSQLQuery("SELECT vDesignationId,vDesignation FROM tbDesignationInfo" +
					" where isActive = 1 order by vDesignation").list();

			for(Iterator <?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbDesignation.addItem(element[0]);
				cmbDesignation.setItemCaption(element[0], element[1].toString());
			}
		}
		catch (Exception e)
		{
			System.out.println("Unable to get cmbDesignation data"+e);
		}
		finally{session.close();}
	}

	public void addShiftGroupInfo()
	{
		cmbEmployeeShiftGroup.removeAllItems();

		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> list = session.createSQLQuery("SELECT vGradeId,vGrade FROM tbGradeInfo" +
					" where isActive = 1 order by vGrade").list();

			for(Iterator <?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbEmployeeShiftGroup.addItem(element[0]);
				cmbEmployeeShiftGroup.setItemCaption(element[0], element[1].toString());
			}
		}
		catch (Exception e)
		{
			System.out.println("Unable to get addShiftGradeInfo data"+e);
		}
		finally{session.close();}
	}

	public void addSalaryRegister()
	{
		cmbGrade.removeAllItems();

		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> list = session.createSQLQuery("SELECT vGradeId,vGrade FROM tbGradeInfo" +
					" where isActive = 1 order by vGrade").list();

			for(Iterator <?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbGrade.addItem(element[0]);
				cmbGrade.setItemCaption(element[0], element[1].toString());
			}
		}
		catch (Exception e)
		{
			System.out.println("Unable to get addGrade data"+e);
		}
		finally{session.close();}
	}
	
	public void addDepartmentInfo() {
		
		cmbDepartment.removeAllItems();

		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> list = session.createSQLQuery("SELECT vDepartmentId,vDepartmentName FROM tbDepartmentInfo where isActive=1 order by vDepartmentName").list();

			for(Iterator <?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbDepartment.addItem(element[0]);
				cmbDepartment.setItemCaption(element[0], element[1].toString());
			}
		}
		catch (Exception e)
		{
			System.out.println("Unable to get add Department data"+e);
		}
		finally{session.close();}
	}
	public void addUnitInfo() 
	{		
		cmbUnitName.removeAllItems();

		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> list = session.createSQLQuery("select vUnitId,vUnitName from tbUnitInfo where isActive=1 order by vUnitName").list();

			for(Iterator <?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbUnitName.addItem(element[0]);
				cmbUnitName.setItemCaption(element[0], element[1].toString());
			}
		}
		catch (Exception e)
		{
			System.out.println("Unable to get add Project data"+e);
		}
		finally{session.close();}
	}

	public String conPath(int flag,String str)
	{
		String basePath = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/";
		String conImage = null;

		if(flag==0)
		{
			if(btnConfirmdate.fileName.trim().length()>0)
			{
				try 
				{
					if(btnConfirmdate.fileName.toString().endsWith(".jpg"))
					{
						String path = sessionBean.getUserId()+"confirm";
						fileMove(basePath+btnConfirmdate.fileName.trim(),SessionBean.imagePath+path+".jpg");
						conPdf = SessionBean.imagePath+path+".jpg";
						conFilePathTmp = path+".jpg";
					}
					else
					{
						String path = sessionBean.getUserId()+"confirm";
						fileMove(basePath+btnConfirmdate.fileName.trim(),SessionBean.imagePath+path+".pdf");
						conPdf = SessionBean.imagePath+path+".pdf";
						conFilePathTmp = path+".pdf";
					}
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
			return conPdf;
		}

		if(flag==1)
		{
			if(btnConfirmdate.fileName.trim().length()>0)
			{
				try 
				{
					if(btnConfirmdate.fileName.toString().endsWith(".jpg"))
					{
						String path = str;
						String projectName = sessionBean.getContextName();
						fileMove(basePath+btnConfirmdate.fileName.trim(),SessionBean.imagePath+projectName+"/employee/confirm/"+path+".jpg");
						conImage = SessionBean.imagePath+projectName+"/employee/confirm/"+path+".jpg";
					}
					else
					{
						String path = str;
						String projectName = sessionBean.getContextName();
						fileMove(basePath+btnConfirmdate.fileName.trim(),SessionBean.imagePath+projectName+"/employee/confirm/"+path+".pdf");
						conImage = SessionBean.imagePath+projectName+"/employee/confirm/"+path+".pdf";
					}
				}
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
			return conImage;
		}

		return null;
	}

	public String joinPath(int flag,String str)
	{
		String basePath = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/";
		String joinImage = null;

		if(flag==0)
		{
			if(btnJoiningDate.fileName.trim().length()>0)
			{
				try 
				{
					if(btnJoiningDate.fileName.toString().endsWith(".jpg"))
					{
						String path = sessionBean.getUserId()+"join";
						fileMove(basePath+btnJoiningDate.fileName.trim(),SessionBean.imagePath+path+".jpg");
						joinPdf = SessionBean.imagePath+path+".jpg";
						joinFilePathTmp = path+".jpg";
					}
					else
					{
						String path = sessionBean.getUserId()+"join";
						fileMove(basePath+btnJoiningDate.fileName.trim(),SessionBean.imagePath+path+".pdf");
						joinPdf = SessionBean.imagePath+path+".pdf";
						joinFilePathTmp = path+".pdf";
					}
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
			return joinPdf;
		}

		if(flag==1)
		{
			if(btnJoiningDate.fileName.trim().length()>0)
			{
				try 
				{
					if(btnJoiningDate.fileName.toString().endsWith(".jpg"))
					{
						String path = str;
						String projectName = sessionBean.getContextName();
						fileMove(basePath+btnJoiningDate.fileName.trim(),SessionBean.imagePath+projectName+"/employee/join/"+path+".jpg");
						joinImage = SessionBean.imagePath+projectName+"/employee/join/"+path+".jpg";
					}
					else
					{
						String path = str;
						String projectName = sessionBean.getContextName();
						fileMove(basePath+btnJoiningDate.fileName.trim(),SessionBean.imagePath+projectName+"/employee/join/"+path+".pdf");
						joinImage = SessionBean.imagePath+projectName+"/employee/join/"+path+".pdf";
					}
				}
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
			return joinImage;
		}

		return null;
	}

	public String applicationPath(int flag,String str)
	{
		String basePath = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/";
		String applicationImage = null;

		if(flag==0)
		{
			if(btnAppDate.fileName.trim().length()>0)
			{
				try 
				{
					if(btnAppDate.fileName.toString().endsWith(".jpg"))
					{
						String path = sessionBean.getUserId()+"application";
						fileMove(basePath+btnAppDate.fileName.trim(),SessionBean.imagePath+path+".jpg");
						applicationPdf = SessionBean.imagePath+path+".jpg";
						applicationFilePathTmp = path+".jpg";
					}
					else
					{
						String path = sessionBean.getUserId()+"application";
						fileMove(basePath+btnAppDate.fileName.trim(),SessionBean.imagePath+path+".pdf");
						applicationPdf = SessionBean.imagePath+path+".pdf";
						applicationFilePathTmp = path+".pdf";
					}
				}
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
			return applicationPdf;
		}

		if(flag==1)
		{
			if(btnAppDate.fileName.trim().length()>0)
			{
				try 
				{
					if(btnAppDate.fileName.toString().endsWith(".jpg"))
					{
						String path = str;
						String projectName = sessionBean.getContextName();
						fileMove(basePath+btnAppDate.fileName.trim(),SessionBean.imagePath+projectName+"/employee/application/"+path+".jpg");
						applicationImage = SessionBean.imagePath+projectName+"/employee/application/"+path+".jpg";
					}
					else
					{
						String path = str;
						String projectName = sessionBean.getContextName();
						fileMove(basePath+btnAppDate.fileName.trim(),SessionBean.imagePath+projectName+"/employee/application/"+path+".pdf");
						applicationImage = SessionBean.imagePath+projectName+"/employee/application/"+path+".pdf";
					}
				}
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
			return applicationImage;
		}

		return null;
	}

	public String nidPath(int flag,String str)
	{
		String basePath = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/";
		String nidImage = null;

		if(flag==0)
		{
			if(btnNid.fileName.trim().length()>0)
			{
				try 
				{
					if(btnNid.fileName.toString().endsWith(".jpg"))
					{
						String path = sessionBean.getUserId()+"nid";
						fileMove(basePath+btnNid.fileName.trim(),SessionBean.imagePath+path+".jpg");
						nidPdf = SessionBean.imagePath+path+".jpg";
						nidFilePathTmp = path+".jpg";
					}
					else
					{
						String path = sessionBean.getUserId()+"nid";
						fileMove(basePath+btnNid.fileName.trim(),SessionBean.imagePath+path+".pdf");
						nidPdf = SessionBean.imagePath+path+".pdf";
						nidFilePathTmp = path+".pdf";
					}
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
			return nidPdf;
		}

		if(flag==1)
		{
			if(btnNid.fileName.trim().length()>0)
			{
				try 
				{
					if(btnNid.fileName.toString().endsWith(".jpg"))
					{
						String path = str;
						String projectName = sessionBean.getContextName();
						fileMove(basePath+btnNid.fileName.trim(),SessionBean.imagePath+projectName+"/employee/nid/"+path+".jpg");
						nidImage = SessionBean.imagePath+projectName+"/employee/nid/"+path+".jpg";
					}
					else
					{
						String path = str;
						String projectName = sessionBean.getContextName();
						fileMove(basePath+btnNid.fileName.trim(),SessionBean.imagePath+projectName+"/employee/nid/"+path+".pdf");
						nidImage = SessionBean.imagePath+projectName+"/employee/nid/"+path+".pdf";
					}
				}
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
			return nidImage;
		}

		return null;
	}

	public String birthPath(int flag,String str)
	{
		String basePath = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/";
		String stuImage = null;

		if(flag==0)
		{
			if(btnDateofBirth.fileName.trim().length()>0)
			{
				try 
				{
					if(btnDateofBirth.fileName.toString().endsWith(".jpg"))
					{
						String path = sessionBean.getUserId()+"birth";
						fileMove(basePath+btnDateofBirth.fileName.trim(),SessionBean.imagePath+path+".jpg");
						birthPdf = SessionBean.imagePath+path+".jpg";
						birthFilePathTmp = path+".jpg";
					}
					else
					{
						String path = sessionBean.getUserId()+"birth";
						fileMove(basePath+btnDateofBirth.fileName.trim(),SessionBean.imagePath+path+".pdf");
						birthPdf = SessionBean.imagePath+path+".pdf";
						birthFilePathTmp = path+".pdf";
					}
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
			return birthPdf;
		}

		if(flag==1)
		{
			if(btnDateofBirth.fileName.trim().length()>0)
			{
				try 
				{
					if(btnDateofBirth.fileName.toString().endsWith(".jpg"))
					{
						String path = str;
						String projectName = sessionBean.getContextName();
						fileMove(basePath+btnDateofBirth.fileName.trim(),SessionBean.imagePath+projectName+"/employee/birth/"+path+".jpg");
						stuImage = SessionBean.imagePath+projectName+"/employee/birth/"+path+".jpg";
					}
					else
					{
						String path = str;
						String projectName = sessionBean.getContextName();
						fileMove(basePath+btnDateofBirth.fileName.trim(),SessionBean.imagePath+projectName+"/employee/birth/"+path+".pdf");
						stuImage = SessionBean.imagePath+projectName+"/employee/birth/"+path+".pdf";
					}
				}
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
			return stuImage;
		}

		return null;
	}
	public String serviceAgreementPath(int flag,String str)
	{
		String basePath = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/";
		String stuImage = null;

		if(flag==0)
		{
			if(btnServiceAgreement.fileName.trim().length()>0)
			{
				try 
				{
					if(btnServiceAgreement.fileName.toString().endsWith(".jpg"))
					{
						String path = sessionBean.getUserId()+"Service_Agreement";
						fileMove(basePath+btnServiceAgreement.fileName.trim(),SessionBean.imagePath+path+".jpg");
						conServiceAgreementPdf = SessionBean.imagePath+path+".jpg";
						conServiceAgreementFilePathTmp = path+".jpg";
					}
					else
					{
						String path = sessionBean.getUserId()+"Service_Agreement";
						fileMove(basePath+btnDateofBirth.fileName.trim(),SessionBean.imagePath+path+".pdf");
						conServiceAgreementPdf = SessionBean.imagePath+path+".pdf";
						conServiceAgreementFilePathTmp = path+".pdf";
					}
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
			return conServiceAgreementPdf;
		}

		if(flag==1)
		{
			if(btnServiceAgreement.fileName.trim().length()>0)
			{
				try 
				{
					if(btnServiceAgreement.fileName.toString().endsWith(".jpg"))
					{
						String path = str;
						String projectName = sessionBean.getContextName();
						fileMove(basePath+btnServiceAgreement.fileName.trim(),SessionBean.imagePath+projectName+"/employee/Service_Agreement/"+path+".jpg");
						stuImage = SessionBean.imagePath+projectName+"/employee/Service_Agreement/"+path+".jpg";
					}
					else
					{
						String path = str;
						String projectName = sessionBean.getContextName();
						fileMove(basePath+btnServiceAgreement.fileName.trim(),SessionBean.imagePath+projectName+"/employee/Service_Agreement/"+path+".pdf");
						stuImage = SessionBean.imagePath+projectName+"/employee/Service_Agreement/"+path+".pdf";
					}
				}
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
			return stuImage;
		}

		return null;
	}

	private void fileMove(String fStr,String tStr) throws IOException
	{
		try
		{
			File f1 = new File(tStr);
			if(f1.isFile())
				f1.delete();
		}
		catch(Exception exp){}
		FileInputStream ff= new FileInputStream(fStr);

		File  ft = new File(tStr);
		FileOutputStream fos = new FileOutputStream(ft);

		while(ff.available()!=0)
		{
			fos.write(ff.read());
		}
		fos.close();
		ff.close();
	}

	@SuppressWarnings("deprecation")
	public AbsoluteLayout buildMainLayout() 
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);

		setWidth("100%");
		setHeight("100%");

		// txtEmpId
		txtEmployeeID = new TextRead();
		txtEmployeeID.setImmediate(true);
		txtEmployeeID.setWidth("140px");
		txtEmployeeID.setHeight("23px");

		// txtEmpId
		txtEmployeeCode = new TextField();
		txtEmployeeCode.setImmediate(true);
		txtEmployeeCode.setWidth("140px");
		txtEmployeeCode.setHeight("-1px");

		// txtFingerId
		txtFingerId = new TextField();
		txtFingerId.setImmediate(true);
		txtFingerId.setWidth("140px");
		txtFingerId.setHeight("-1px");
		txtFingerId.setMaxLength(20);

		// txtProximityId
		txtProximityId = new TextField();
		txtProximityId.setImmediate(true);
		txtProximityId.setWidth("140px");
		txtProximityId.setHeight("-1px");
		txtProximityId.setMaxLength(10);

		// txtFempName
		txtEmployeeName = new TextField();
		txtEmployeeName.setImmediate(true);
		txtEmployeeName.setWidth("200px");
		txtEmployeeName.setHeight("-1px");
		
		// txtFempName
		txtFamilyName = new TextField();
		txtFamilyName.setImmediate(true);
		txtFamilyName.setWidth("200px");
		txtFamilyName.setHeight("-1px");
		
		// txtFempName
		txtGivenName = new TextField();
		txtGivenName.setImmediate(true);
		txtGivenName.setWidth("200px");
		txtGivenName.setHeight("-1px");

		cmbReligion = new ComboBox("",religion);
		cmbReligion.setImmediate(true);
		cmbReligion.setWidth("140px");
		cmbReligion.setHeight("-1px");

		// txtContact
		txtContact = new TextField();
		txtContact.setImmediate(true);
		txtContact.setWidth("200px");
		txtContact.setHeight("-1px");
		txtContact.setMaxLength(23);

		// txtEmail
		txtEmail = new TextField();
		txtEmail.setImmediate(true);
		txtEmail.setWidth("200px");
		txtEmail.setHeight("-1px");

		// cmbGender
		RadioGender = new OptionGroup("",sex);
		RadioGender.setImmediate(true);
		RadioGender.setStyleName("horizontal");

		// dDateofBirth
		dDateOfBirth = new PopupDateField();
		dDateOfBirth.setImmediate(true);
		dDateOfBirth.setHeight("-1px");
		dDateOfBirth.setResolution(PopupDateField.RESOLUTION_DAY);
		dDateOfBirth.setDateFormat("dd-MM-yyyy");
		dDateOfBirth.setValue(new java.util.Date());

		// btnDateofBirth
		btnDateofBirth = new ImmediateFileUpload("","Birth");
		btnDateofBirth.setImmediate(true);
		btnDateofBirth.setStyleName(Button.STYLE_LINK);

		// btnBirthPreview
		btnBirthPreview = new Button();
		btnBirthPreview.setStyleName(Button.STYLE_LINK);
		btnBirthPreview.setImmediate(true);

		// txtNationality
		txtNationality = new TextField();
		txtNationality.setImmediate(true);
		txtNationality.setWidth("140px");
		txtNationality.setHeight("-1px");
		txtNationality.setValue("Bangladeshi");

		// txtNid
		txtNid = new TextField();
		txtNid.setImmediate(true);
		txtNid.setWidth("140px");
		txtNid.setHeight("-1px");
		txtNid.setMaxLength(17);

		// btnNidPreview
		btnNidPreview = new Button();
		btnNidPreview.setStyleName(Button.STYLE_LINK);
		btnNidPreview.setImmediate(true);

		// btnNid
		btnNid = new ImmediateFileUpload("","NID");
		btnNid.setImmediate(true);

		// cmbEmployeeType
		cmbEmployeeType = new ComboBox("",employeeType);
		cmbEmployeeType.setImmediate(true);
		cmbEmployeeType.setNullSelectionAllowed(false);
		cmbEmployeeType.setWidth("140px");
		cmbEmployeeType.setHeight("-1px");
		
		// cmbLevel
		cmbLevel = new ComboBox("",level);
		cmbLevel.setImmediate(true);
		cmbLevel.setWidth("140px");
		cmbLevel.setHeight("-1px");
		cmbLevel.setNewItemsAllowed(true);
		cmbLevel.setNullSelectionAllowed(false);
		
		txtCareerPeriod = new TextField();
		txtCareerPeriod.setImmediate(true);
		txtCareerPeriod.setWidth("140px");
		txtCareerPeriod.setHeight("-1px");
		txtCareerPeriod.setMaxLength(17);

		// cmbServiceType
		cmbServiceType = new ComboBox("",ServiceType);
		cmbServiceType.setImmediate(true);
		cmbServiceType.setNullSelectionAllowed(false);
		cmbServiceType.setWidth("140px");
		cmbServiceType.setHeight("-1px");

		chkPhysicallyDisable = new CheckBox("Select if yes.");
		chkPhysicallyDisable.setHeight("-1px");
		chkPhysicallyDisable.setWidth("-1px");

		// dAppDate
		dApplicationDate = new PopupDateField();
		dApplicationDate.setImmediate(true);
		dApplicationDate.setWidth("108px");
		dApplicationDate.setHeight("-1px");
		dApplicationDate.setInvalidAllowed(false);
		dApplicationDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dApplicationDate.setDateFormat("dd-MM-yyyy");
		dApplicationDate.setValue(new java.util.Date());

		// btnAppDate
		btnAppDate = new ImmediateFileUpload("","App");
		btnAppDate.setImmediate(true);

		// btnApplicationPreview
		btnApplicationPreview = new Button();
		btnApplicationPreview.setStyleName(Button.STYLE_LINK);
		btnApplicationPreview.setImmediate(true);

		// dInterviewDate
		dInterviewDate = new PopupDateField();
		dInterviewDate.setImmediate(true);
		dInterviewDate.setWidth("108px");
		dInterviewDate.setHeight("-1px");
		dInterviewDate.setInvalidAllowed(false);
		dInterviewDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dInterviewDate.setDateFormat("dd-MM-yyyy");
		dInterviewDate.setValue(new java.util.Date());

		// dJoiningDate
		dJoiningDate = new PopupDateField();
		dJoiningDate.setImmediate(true);
		dJoiningDate.setWidth("108px");
		dJoiningDate.setHeight("-1px");
		dJoiningDate.setInvalidAllowed(false);
		dJoiningDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dJoiningDate.setDateFormat("dd-MM-yyyy");
		dJoiningDate.setValue(new java.util.Date());

		// btnJoiningDate
		btnJoiningDate = new ImmediateFileUpload("","Join");
		btnJoiningDate.setImmediate(true);

		// btnJoinPreview
		btnJoinPreview = new Button();
		btnJoinPreview.setStyleName(BaseTheme.BUTTON_LINK);
		btnJoinPreview.setImmediate(true);

		// dConDate
		dConfirmationDate = new PopupDateField();
		dConfirmationDate.setImmediate(true);
		dConfirmationDate.setWidth("108px");
		dConfirmationDate.setHeight("-1px");
		dConfirmationDate.setInvalidAllowed(false);
		dConfirmationDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dConfirmationDate.setDateFormat("dd-MM-yyyy");

		// btnConfimdate
		btnConfirmdate = new ImmediateFileUpload("","Con");
		btnConfirmdate.setImmediate(true);

		// btnConPreview
		btnConPreview = new Button();
		btnConPreview.setStyleName(BaseTheme.BUTTON_LINK);
		btnConPreview.setImmediate(true);

		// cmbStatus
		cmbStatus = new ComboBox("",status);
		cmbStatus.setImmediate(true);
		cmbStatus.setWidth("108px");
		cmbStatus.setHeight("-1px");
		
		
		

		lblEmployeeStatus = new Label("",Label.CONTENT_XHTML);
		lblEmployeeStatus.setImmediate(true);
		lblEmployeeStatus.setHeight("-1px");
		lblEmployeeStatus.setWidth("-1px");

		// dDate	
		dStatusDate = new PopupDateField();
		dStatusDate.setImmediate(true);
		dStatusDate.setWidth("108px");
		dStatusDate.setHeight("-1px");
		dStatusDate.setInvalidAllowed(false);
		dStatusDate.setVisible(false);
		dStatusDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dStatusDate.setDateFormat("dd-MM-yyyy");

		// cmbSection
		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("164px");
		cmbSection.setHeight("-1px");

		// btnPlusSection
		btnPlusSection = new NativeButton();
		btnPlusSection.setIcon(new ThemeResource("../icons/add.png"));
		btnPlusSection.setImmediate(true);
		btnPlusSection.setWidth("28px");
		btnPlusSection.setHeight("24px");

		
		// cmbSection
		cmbUnitName = new ComboBox();
		cmbUnitName.setImmediate(true);
		cmbUnitName.setWidth("164px");
		cmbUnitName.setHeight("-1px");

		// btnPlusSection
		btnPlusUnit = new NativeButton();
		btnPlusUnit.setIcon(new ThemeResource("../icons/add.png"));
		btnPlusUnit.setImmediate(true);
		btnPlusUnit.setWidth("28px");
		btnPlusUnit.setHeight("24px");

		
		// cmbDesignation
		cmbDesignation = new ComboBox();
		cmbDesignation.setImmediate(true);
		cmbDesignation.setWidth("164px");
		cmbDesignation.setHeight("-1px");

		// btnDesignation
		btnPlusDesignation = new NativeButton();
		btnPlusDesignation.setIcon(new ThemeResource("../icons/add.png"));
		btnPlusDesignation.setImmediate(true);
		btnPlusDesignation.setWidth("28px");
		btnPlusDesignation.setHeight("24px");

		cmbEmployeeShiftGroup = new ComboBox();
		cmbEmployeeShiftGroup.setImmediate(true);
		cmbEmployeeShiftGroup.setWidth("164px");
		cmbEmployeeShiftGroup.setHeight("-1px");

		btnPlusGroup = new NativeButton();
		btnPlusGroup.setIcon(new ThemeResource("../icons/add.png"));
		btnPlusGroup.setImmediate(true);
		btnPlusGroup.setWidth("28px");
		btnPlusGroup.setHeight("24px");

		cmbGrade = new ComboBox();
		cmbGrade.setImmediate(true);
		cmbGrade.setWidth("164px");
		cmbGrade.setHeight("-1px");

		btnPlusSalaryRegister = new NativeButton();
		btnPlusSalaryRegister.setIcon(new ThemeResource("../icons/add.png"));
		btnPlusSalaryRegister.setImmediate(true);
		btnPlusSalaryRegister.setWidth("28px");
		btnPlusSalaryRegister.setHeight("24px");

		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("164px");
		cmbDepartment.setHeight("-1px");
		
		btnPlusDepartment = new NativeButton();
		btnPlusDepartment.setIcon(new ThemeResource("../icons/add.png"));
		btnPlusDepartment.setImmediate(true);
		btnPlusDepartment.setWidth("28px");
		btnPlusDepartment.setHeight("24px");
		

		btnPlusPayScale = new NativeButton();
		btnPlusPayScale.setIcon(new ThemeResource("../icons/add.png"));
		btnPlusPayScale.setImmediate(true);
		btnPlusPayScale.setWidth("28px");
		btnPlusPayScale.setHeight("24px");

		chkOtEnable = new CheckBox("OT Enable");
		chkOtEnable.setImmediate(true);
		chkOtEnable.setHeight("-1px");
		chkOtEnable.setWidth("-1px");
		
		checkFridayEnabled = new CheckBox("Friday Enable");
		checkFridayEnabled.setImmediate(true);
		checkFridayEnabled.setHeight("-1px");
		checkFridayEnabled.setWidth("-1px");
		
		checkHolidayEnabled = new CheckBox("Holiday Enable");
		checkHolidayEnabled.setImmediate(true);
		checkHolidayEnabled.setHeight("-1px");
		checkHolidayEnabled.setWidth("-1px");
		

		txtImageBox = new TextRead();
		txtImageBox.setImmediate(true);
		txtImageBox.setWidth("130px");
		txtImageBox.setHeight("150px");
		txtImageBox.setStyleName("txtborder");

		lblImage = new Label("<b><Font Color='#100676' size='6px' font-family= 'Arial, Helvetica, Tahoma, Verdana, sans-serif'>?</Font></b> ",Label.CONTENT_XHTML);
		lblImage.setImmediate(true);
		lblImage.setHeight("100px");

		//studentImage
		Image = new FileUpload("Picture");
		Image.upload.setButtonCaption("Employee Image");
		
		btnPlusShift=new NativeButton();
		btnPlusShift.setIcon(new ThemeResource("../icons/add.png"));
		btnPlusShift.setImmediate(true);
		btnPlusShift.setWidth("28px");
		btnPlusShift.setHeight("24px");
		
		cmbShift = new ComboBox();
		cmbShift.setImmediate(true);
		cmbShift.setWidth("164px");
		cmbShift.setHeight("-1px");
		
		// cmbProbationPeriod
		cmbValidDuration = new ComboBox("",ProbationPeriod);
		cmbValidDuration.setImmediate(true);
		cmbValidDuration.setWidth("108px");
		cmbValidDuration.setHeight("-1px");
		
		txtValidYear = new AmountField();
		txtValidYear.setImmediate(true);
		txtValidYear.setWidth("50px");
		txtValidYear.setHeight("-1px");
		
		dValidDate = new PopupDateField();
		dValidDate.setImmediate(true);
		dValidDate.setWidth("108px");
		dValidDate.setHeight("-1px");
		dValidDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dValidDate.setDateFormat("dd-MM-yyyy");
		
		btnServiceAgreement = new ImmediateFileUpload("","SA");
		btnServiceAgreement.setImmediate(true);
		btnServiceAgreement.setStyleName(Button.STYLE_LINK);

		// btnBirthPreview
		btnServiceAgreementPreview = new Button();
		btnServiceAgreementPreview.setStyleName(Button.STYLE_LINK);
		btnServiceAgreementPreview.setImmediate(true);
		

		mainLayout.addComponent(txtImageBox, "top:10.0px;left:708.0px;");
		mainLayout.addComponent(lblImage, "top:70.0px;left:770.0px;");
		mainLayout.addComponent(Image, "top:10.0px;left:718.0px;");

		mainLayout.addComponent(new Label("<b><Font Color='#CD0606' size='3px'>*</Font></b>System Id:",Label.CONTENT_XHTML), "top:25.0px;left:6.0px;");
		mainLayout.addComponent(txtEmployeeID, "top:23.0px;left:128.0px;");

		mainLayout.addComponent(new Label("<b><Font Color='#CD0606' size='3px'>*</Font></b>Employee Id:",Label.CONTENT_XHTML), "top:50.0px;left:6.0px;");
		mainLayout.addComponent(txtEmployeeCode, "top:48.0px;left:128.0px;");

		mainLayout.addComponent(new Label("<b><Font Color='#CD0606' size='3px'> </Font></b>Finger ID:",Label.CONTENT_XHTML), "top:75.0px;left:12.0px;");
		mainLayout.addComponent(txtFingerId, "top:73.0px;left:128.0px;");

		mainLayout.addComponent( new Label("<b><Font Color='#CD0606' size='3px'> </Font></b>Proximity ID:",Label.CONTENT_XHTML), "top:100.0px;left:12.0px;");
		mainLayout.addComponent(txtProximityId, "top:98.0px;left:128.0px;");

		mainLayout.addComponent(new Label("<b><Font Color='#CD0606' size='3px'>*</Font></b>Employee Name:",Label.CONTENT_XHTML), "top:125.0px;left:6.0px;");
		mainLayout.addComponent(txtEmployeeName, "top:123.0px;left:128.0px;");
		
		mainLayout.addComponent(new Label("<b><Font Color='#CD0606' size='3px'></Font></b>Family Name:",Label.CONTENT_XHTML), "top:150px;left:6.0px;");
		mainLayout.addComponent(txtFamilyName, "top:148px;left:128.0px;");
		
		mainLayout.addComponent(new Label("<b><Font Color='#CD0606' size='3px'>*</Font></b>Given Name:",Label.CONTENT_XHTML), "top:175px;left:6.0px;");
		mainLayout.addComponent(txtGivenName, "top:173px;left:128.0px;");

		mainLayout.addComponent(new Label("<b><Font Color='#CD0606' size='3px'>*</Font></b>Religion:",Label.CONTENT_XHTML), "top:200px;left:6.0px;");
		mainLayout.addComponent(cmbReligion, "top:198px;left:128.0px;");

		mainLayout.addComponent(new Label("<b><Font Color='#CD0606' size='3px'>*</Font></b>Contact:",Label.CONTENT_XHTML), "top:225px;left:6.0px;");
		mainLayout.addComponent(txtContact, "top:223px;left:128.0px;");

		mainLayout.addComponent(new Label("Email:"), "top:250px;left:12.0px;");
		mainLayout.addComponent(txtEmail, "top:248px;left:128.0px;");

		mainLayout.addComponent(new Label("<b><Font Color='#CD0606' size='3px'>*</Font></b>Gender:",Label.CONTENT_XHTML), "top:275px;left:6.0px;");
		mainLayout.addComponent(RadioGender, "top:273px;left:128.0px;");

		mainLayout.addComponent(new Label("Date of Birth:"), "top:300px;left:12.0px;");
		mainLayout.addComponent(dDateOfBirth, "top:298px;left:128.0px;");
		mainLayout.addComponent(btnDateofBirth, "top:292px;left:237.0px;");
		mainLayout.addComponent(btnBirthPreview, "top:302px;left:295.0px;");

		mainLayout.addComponent(new Label("Nationality:"), "top:325px;left:12.0px;");
		mainLayout.addComponent(txtNationality, "top:323px;left:128.0px;");

		mainLayout.addComponent( new Label("NID:"), "top:350px;left:12.0px;");
		mainLayout.addComponent(txtNid, "top:348px;left:128.0px;");
		mainLayout.addComponent(btnNid, "top:342px;left:270.0px;");
		mainLayout.addComponent(btnNidPreview, "top:350px;left:330.0px;");

		mainLayout.addComponent( new Label("<b><Font Color='#CD0606' size='3px'>*</Font></b>Employee Type:",Label.CONTENT_XHTML), "top:375px;left:6.0px;");
		mainLayout.addComponent(cmbEmployeeType, "top:373px;left:128.0px;");

		mainLayout.addComponent( new Label("<b><Font Color='#CD0606' size='3px'>*</Font></b>Level Of English:",Label.CONTENT_XHTML), "top:400px;left:12.0px;");
		mainLayout.addComponent(cmbLevel, "top:398px;left:128.0px;");
		
		mainLayout.addComponent(new Label("<b><Font Color='#CD0606' size='3px'>*</Font></b>Career Period:",Label.CONTENT_XHTML), "top:425px;left:12.0px;");
		mainLayout.addComponent(txtCareerPeriod, "top:423px;left:128.0px;");
		
		mainLayout.addComponent( new Label("<b><Font Color='#CD0606' size='3px'>*</Font></b>Service Type:",Label.CONTENT_XHTML), "top:450px;left:12.0px;");
		mainLayout.addComponent(cmbServiceType, "top:448px;left:128.0px;");

		mainLayout.addComponent( new Label("Physically Disable:"), "top:475px;left:12.0px;");
		mainLayout.addComponent(chkPhysicallyDisable, "top:473px;left:128.0px;");

		mainLayout.addComponent(new Label("Application Date:"), "top:25.0px;left:358.0px;");
		mainLayout.addComponent(dApplicationDate, "top:23.0px;left:480.0px;");
		mainLayout.addComponent(btnAppDate, "top:16.0px;left:585.0px;");
		mainLayout.addComponent(btnApplicationPreview, "top:22.0px;left:640.0px;");

		mainLayout.addComponent( new Label("Interview Date:"), "top:50.0px;left:358.0px;");
		mainLayout.addComponent(dInterviewDate, "top:48.0px;left:480.0px;");
		
		mainLayout.addComponent(new Label("Joining Date:"), "top:75.0px;left:358.0px;");		
		mainLayout.addComponent(dJoiningDate, "top:73.0px;left:480.0px;");
		mainLayout.addComponent(btnJoiningDate, "top:67.0px;left:585.0px;");
		mainLayout.addComponent(btnJoinPreview, "top:74.0px;left:640.0px;");
		
		
		// cmbProbationPeriod
		cmbProbationPeriod = new ComboBox("",ProbationPeriod);
		cmbProbationPeriod.setImmediate(true);
		cmbProbationPeriod.setWidth("108px");
		cmbProbationPeriod.setHeight("-1px");
		mainLayout.addComponent(lblProbationMonth, "top:100px;left:358.0px;");
		mainLayout.addComponent(cmbProbationPeriod, "top:98.0px;left:480.0px;");
		
		
		mainLayout.addComponent(new Label("Confirmation Date:"), "top:125.0px;left:358.0px;");
		mainLayout.addComponent(dConfirmationDate, "top:123.0px;left:480.0px;");
		mainLayout.addComponent(btnConfirmdate, "top:117px;left:585.0px;");
		mainLayout.addComponent(btnConPreview, "top:125.0px;left:640.0px;");
		
		lblYear=new Label();
		lblYear.setImmediate(true);
		
		mainLayout.addComponent(new Label("<b><Font Color='#CD0606' size='3px'></Font></b>Contract Period :",Label.CONTENT_XHTML), "top:150px;left:358.0px;");
		mainLayout.addComponent(txtValidYear, "top:148px;left:480.0px;");
		mainLayout.addComponent(lblYear, "top:150px;left:533px;");
		
		mainLayout.addComponent(new Label("Valid Date:"), "top:175px;left:358.0px;");
		mainLayout.addComponent(dValidDate, "top:173px;left:480.0px;");

		mainLayout.addComponent(new Label("<b><Font Color='#CD0606' size='3px'>*</Font></b>Project Name :",Label.CONTENT_XHTML), "top:200px; left:353.0px;");
		mainLayout.addComponent(cmbUnitName, "top:198px; left:480.0px;");
		mainLayout.addComponent(btnPlusUnit, "top:198px;left:648.0px;");
		
		mainLayout.addComponent(new Label("<b><Font Color='#CD0606' size='3px'>*</Font></b>Department :",Label.CONTENT_XHTML), "top:225px; left:353.0px;");
		mainLayout.addComponent(cmbDepartment, "top:223px; left:480.0px;");
		mainLayout.addComponent(btnPlusDepartment, "top:223px;left:648.0px;");
		
		mainLayout.addComponent(new Label("<b><Font Color='#CD0606' size='3px'></Font></b>Section :",Label.CONTENT_XHTML), "top:250px; left:358px;");
		mainLayout.addComponent(cmbSection, "top:248px; left:480.0px;");
		mainLayout.addComponent(btnPlusSection, "top:248px;left:648.0px;");

		mainLayout.addComponent(new Label("<b><Font Color='#CD0606' size='3px'>*</Font></b>Designation :",Label.CONTENT_XHTML), "top:275px; left:353.0px;");
		mainLayout.addComponent(cmbDesignation, "top:273px;left:480.0px;");
		mainLayout.addComponent(btnPlusDesignation, "top:273px;left:648.0px;");

	//	mainLayout.addComponent(new Label("<b><Font Color='#CD0606' size='3px'>*</Font></b>Grade : ",Label.CONTENT_XHTML), "top:300px; left:353.0px;");
		mainLayout.addComponent(cmbGrade, "top:298px; left:480.0px;"); // was salary register
		mainLayout.addComponent(btnPlusSalaryRegister, "top:298px;left:648.0px;");
		cmbGrade.setVisible(false);
		btnPlusSalaryRegister.setVisible(false);
		
		mainLayout.addComponent(new Label("<b><Font Color='#CD0606' size='3px'>*</Font></b>Status:",Label.CONTENT_XHTML), "top:325px; left:353.0px;");
		mainLayout.addComponent(cmbStatus, "top:323px; left:480.0px;");

		mainLayout.addComponent(lblEmployeeStatus, "top:350px;left:353.0px;");
		mainLayout.addComponent(dStatusDate, "top:348px;left:478.0px;");

		mainLayout.addComponent(chkOtEnable, "top:375px;left:490.0px;");
		mainLayout.addComponent(checkFridayEnabled,"top:400px; left:490px");
		
		mainLayout.addComponent(new Label("Service Agreement :"),"top:430px; left:353px;");
		mainLayout.addComponent(btnServiceAgreement, "top:425px;left:478px;");
		mainLayout.addComponent(btnServiceAgreementPreview, "top:435px;left:570px;");
		
		//mainLayout.addComponent(checkHolidayEnabled,"top:335px; left:490px");
		

		return mainLayout;
	}
}