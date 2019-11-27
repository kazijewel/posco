package com.common.share;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.vaadin.ui.Button;
import com.vaadin.ui.TextField;

public class SessionBean 
{
	private boolean isAdmin;
	private boolean isSuperAdmin;
	private String userId;
	private String userName;
	private String userIp = "";
	private boolean authenticWindow ;
	private TextField permitFormTxtField;
	
	private String companyId;
	private String companyName;
	private String companyLogo;
	private String companyAddress;
	private String phonefaxemail;
	private boolean submitable;
	private boolean updateable;
	private boolean deleteable;
	
	
	private int editDeleteDays;
	private String devloperAddress;



	private TextField permitFormTxtFieldModule;
	private TextField permitFormTxtFieldType;
	private Button permitBtn=new Button();
	private Button nopermitBtn=new Button();

	private String companyPhoneFaxEmail;
	private String companyRegNo;
	private String companyVatRegNo;
	private String companyTinNo;
	private String companyTradeLicenceNo;
	private String companyMobileNo;
	private String companyWebsite;
	

	private Date fiscalOpenDate;
	private Date fiscalCloseDate;
	private String fiscalRunningSerial, menuId;
	private Object url;

	private String contextName;
	

	public boolean adminModule;
	public boolean OthersModule;
	public boolean inventoryModule;
	public boolean POSModule;
	public boolean AssetModule;
	public boolean inventory;
	public boolean lcmodule;


	private String contextNname;
	private String war;
	private static Date asOnDate;
	private static Date asFromDate;
	private static Object p;

	public static String backPath = "D:/backup/";
	public static String projectName = "SamPolymer";
	public static String imagePath = "D:/Tomcat 7.0/webapps/report/";
	public static String imagePathTmp = "D:/Tomcat 7.0/webapps/report/";
	public static String IndentPathTmp = "D:/Tomcat 7.0/webapps/report/SamPolymer/IndentBill";
	public static String imageLogo = "D:/Tomcat 7.0/webapps/report/SamPolymer/";
	public static String supplierLogo = "D:/Tomcat 7.0/webapps/report/SamPolymer/supplier/";
	public static String employeeImage = "D:/Tomcat 7.0/webapps/report/SamPolymer/employee/";
	public static String ProductImage = "D:/Tomcat 7.0/webapps/report/SamPolymer/Product/";
	public static String employeeBirth = "D:/Tomcat 7.0/webapps/report/SamPolymer/employee/birth/";
	public static String employeeNid = "D:/Tomcat 7.0/webapps/report/SamPolymer/employee/nid/";
	public static String employeeApplication = "D:/Tomcat 7.0/webapps/report/SamPolymer/employee/application/";
	public static String employeeJoin = "D:/Tomcat 7.0/webapps/report/SamPolymer/employee/join/";
	public static String vehicleMaintenBillImage = "D:/Tomcat 7.0/webapps/report/SamPolymer/vehicleBill/";
	public static String catlogue ="D:/Tomcat 7.0/webapps/report/attachment/";
	public static String emailPath = "D:/Tomcat 7.0/webapps/report/posco/";
	public static String Purchase;
	public static String Requisition;
	public static String Attachment;

	public boolean setupModule;
	//public boolean rawMeterialModule;
	public boolean productionModule;
	public boolean finishGoodsModule;
	public boolean DoSalesModule;
	public boolean accountsModule;
	public boolean fixedAssetModule;
	public boolean hrmModule;
	public boolean transportModule;
	public boolean lcModule;
	public boolean rawMaterialModule;
	public boolean hrmAnother;
	public boolean emailModule;
	public boolean hrmAccountModule;

	public SimpleDateFormat dfBd = new SimpleDateFormat("dd-MM-yyyy");
	public SimpleDateFormat dfDb = new SimpleDateFormat("yyyy-MM-dd");
	public SimpleDateFormat dfYear = new SimpleDateFormat("yyyy");
	public SimpleDateFormat dfMonth = new SimpleDateFormat("MMMMM");
	public SimpleDateFormat dTimeFormat = new SimpleDateFormat("hh:mm:ss aa");
	public SimpleDateFormat dDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa");
	

	public DecimalFormat dfFloat = new DecimalFormat("#0.00");
	public DecimalFormat dfInteger = new DecimalFormat("#0");
	public String report;

	private String branchId;
	private String branchCode;
	private String branchName;
	private String branchAddress;
	private String branchPhoneFaxEmail;
	private boolean isCorporateBranch;
	public String developerAddress="Developed by: E-Vision Software Ltd. ||  Mob:01755-506044 || www.eslctg.com";
	public String EmployeeId="",DesignationId="";
	public String getEmployeeId()
	{
		return EmployeeId;
	}
	public String getDesignationId()
	{
		return DesignationId;
	}
	public Object getasOnDate()
	{
		return asOnDate;
	}

	public Object getFromDate()
	{
		return asFromDate;
	}

	public String getUserId()
	{
		return userId;
	}

	public String getUserName()
	{
		return userName;
	}

	public Object getUrl()
	{
		return url;
	}

	public String getCompanyId()
	{
		return companyId;
	}

	public String getCompany()
	{
		return companyName;
	}

	public String getCompanyLogo()
	{
		return companyLogo;
	}

	public String getCompanyAddress()
	{
		return companyAddress;
	}

	public String getCompanyContact()
	{
		return phonefaxemail;
	}

	public Date getFiscalOpenDate()
	{
		return fiscalOpenDate;
	}

	public Date getFiscalCloseDate()
	{
		return fiscalCloseDate;
	}

	public String getfiscalRunningSerial()
	{
		return fiscalRunningSerial;
	}

	public boolean getAuthenticWindow()
	{
		return authenticWindow;
	}

	public String getUserIp()
	{
		return userIp;
	}

	public boolean isUpdateable()
	{
		return updateable;
	}

	public boolean isDeleteable()
	{
		return deleteable;
	}

	public boolean isSubmitable()
	{
		return submitable;
	}

	public String getContextName()
	{
		return contextNname;
	}

	////////////// SET //////////////////
	public void setCompanyId(String companyId)
	{
		this.companyId = companyId;
	}
	public void setCompany(String companyname)
	{
		this.companyName = companyname;
	}
	public void setCompanyLogo(String companyLogo)
	{
		this.companyLogo = companyLogo;
	}
	public void setCompanyRegNo(String companyRegNo)
	{
		this.companyRegNo = companyRegNo;
	}
	public void setCompanyAddress(String companyAddress)
	{
		this.companyAddress = companyAddress;
	}	
	public void setCompanyContact(String phonefaxemail)
	{
		this.phonefaxemail = phonefaxemail;
	}
	public void setUrl(Object url)
	{
		this.url = url;
	}
	public void setUserName(String uname)
	{
		userName = uname;
	}
	public void setUserId(String uid)
	{
		userId = uid;
	}
	public void isAdmin(boolean ia)
	{
		isAdmin = ia;
	}
	public void isSuperAdmin(boolean ia)
	{
		isSuperAdmin = ia;
	}
	public void setAuthenticWindow(boolean authenticWindow)
	{
		this.authenticWindow = authenticWindow;
	}

	public void setPermitBtn(Button permitBtn)
	{
		this.permitBtn = permitBtn;
	}
	public void setNoPermitBtn(Button nopermitBtn)
	{
		this.nopermitBtn = nopermitBtn;
	}
	public void setPermitFormTxt(TextField permitFormTxtField)
	{
		this.permitFormTxtField = permitFormTxtField;
	}
	public void setUserEditDeleteDays(int editDeleteDays)
	{
		this.editDeleteDays = editDeleteDays;
	}
	public int getUserEditDeleteDays()
	{
		return editDeleteDays;
	}
	public void setCompanyMobileNo(String CompanyMobileNo)
	{
		this.companyMobileNo = CompanyMobileNo;
	}
	public String getCompanyMobileNo()
	{
		return companyMobileNo;
	}
	public void setCompanyWebsite(String CompanyWebsite)
	{
		this.companyWebsite = CompanyWebsite;
	}
	public String getCompanyWebsite()
	{
		return companyWebsite;
	}
	public void setPermitFormTxtModule(TextField permitFormTxtFieldModule)
	{
		this.permitFormTxtFieldModule = permitFormTxtFieldModule;
	}
	public void setPermitFormTxtType(TextField permitFormTxtFieldType)
	{
		this.permitFormTxtFieldType = permitFormTxtFieldType;
	}
	/*public void setPermitForm(String menuId,String txt)
	{
		//this.menuId = menuId;
		permitFormTxtField.setDebugId(menuId);
		permitFormTxtField.setReadOnly(false);
		permitFormTxtField.setValue(txt);
		permitFormTxtField.setReadOnly(true);
		permitBtn.setEnabled(false);
		nopermitBtn.setEnabled(true);
	}*/
	public void setPermitForm(String menuId, String txt,String Module,String Type)
	{
		this.menuId = menuId;
		permitFormTxtField.setDebugId(this.menuId);
		permitFormTxtField.setReadOnly(false);
		permitFormTxtField.setValue(txt);
		permitFormTxtField.setReadOnly(true);
		//permitFormTxtFieldModule.setDebugId(this.menuId);
		permitFormTxtFieldModule.setReadOnly(false);
		permitFormTxtFieldModule.setValue(Module);
		permitFormTxtFieldModule.setReadOnly(true);
		//permitFormTxtFieldType.setDebugId(this.menuId);
		permitFormTxtFieldType.setReadOnly(false);
		permitFormTxtFieldType.setValue(Type);
		permitFormTxtFieldType.setReadOnly(true);
		permitBtn.setEnabled(false);
		nopermitBtn.setEnabled(true);
	}

	public void setAsOnDate(Object asdate)
	{
		asOnDate = (Date)asdate;
	}
	public void setFromDate(Object asdate)
	{
		asFromDate = (Date)asdate;
	}


	public void setSubmitable(boolean sb)
	{
		submitable = sb;
	}
	public void setUpdateable(boolean sb)
	{
		updateable = sb;
	}

	public void setDeleteable(boolean sb)
	{
		deleteable = sb;
	}

	public void setFiscalOpenDate(Object fiscalOpenDate)
	{
		this.fiscalOpenDate = (Date) fiscalOpenDate;
	}
	public void setFiscalCloseDate(Object fiscalCloseDate)
	{
		this.fiscalCloseDate =(Date) fiscalCloseDate;
	}
	public void setFiscalRunningSerial(Object fiscalrunning)
	{
		this.fiscalRunningSerial = (String) fiscalrunning;
	}

	public void setUserIp(String userIp)
	{
		this.userIp = userIp;
	}
	public void setContextName(String contextname)
	{
		this.contextNname = contextname;
	}
	public void setAdmin(boolean ia)
	{
		isAdmin = ia;
	}
	public boolean isAdmin()
	{
		return isAdmin;
	}

	public void setSuperAdmin(boolean ia)
	{
		isSuperAdmin = ia;
	}
	public boolean isSuperAdmin()
	{
		return isSuperAdmin;
	}
	public void setWar(String war){
		this.war = war;
	}
	public String getWar(){
		return war;
	}
	public void setP(Object p){
		this.p = p;
	}

	public Object getP(){
		return p;
	}
	public void setBranchId(String branchId)
	{
		this.branchId = branchId;
	}
	public String getBranchId()
	{
		return branchId;
	}

	public void setBranchCode(String branchCode)
	{
		this.branchCode = branchCode;
	}
	public String getBranchCode()
	{
		return branchCode;
	}

	public void setPrincipalBranch(boolean branchType)
	{
		this.isCorporateBranch = branchType;
	}
	public boolean isPrincipalBranch()
	{
		return isCorporateBranch;
	}

	public void setBranchName(String branchName)
	{
		this.branchName = branchName;
	}
	public String getBranchName()
	{
		return branchName;
	}

	public void setBranchAddress(String branchAddress)
	{
		this.branchAddress = branchAddress;
	}
	public String getBranchAddress()
	{
		return branchAddress;
	}

	public void setBranchContact(String phonefaxemail)
	{
		this.branchPhoneFaxEmail = phonefaxemail;
	}
	public String getBranchContact()
	{
		return branchPhoneFaxEmail;
	}
	public void setDeveloperAddress(String dAddress)
	{
		this.developerAddress=dAddress;
	}
	public String getDeveloperAddress()
	{
		return developerAddress;
	}
	public void setDesignationId(String id) {
			
			this.DesignationId=id;
	}
	public void setEmployeeId(String string) {
		this.EmployeeId=string;
		
	}

}

