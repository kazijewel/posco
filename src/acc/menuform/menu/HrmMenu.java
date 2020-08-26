package acc.menuform.menu;

import hrm.common.reportform.EmailLogInNotice;
import hrm.common.reportform.NewEmployeeList;
import hrm.common.reportform.RptAgeAsOnDate;
import hrm.common.reportform.RptAuditMonthWiseOT;
import hrm.common.reportform.RptDailyAbsentReport;
import hrm.common.reportform.RptDailyAttendance;
import hrm.common.reportform.RptDailyEarlyOut;
import hrm.common.reportform.RptDailyLateIn;
import hrm.common.reportform.RptDateAndMonthWiseLeave;
import hrm.common.reportform.RptDesignationList;
import hrm.common.reportform.RptEditDeleteOverTimeRequest;
import hrm.common.reportform.RptEditDeleteReplacementLeave;
import hrm.common.reportform.RptEditEmployeeInformation;
import hrm.common.reportform.RptEmployeeAgreement;
import hrm.common.reportform.RptEmployeeAnnualEvaluationSheet;
import hrm.common.reportform.RptEmployeeConfirmation;
import hrm.common.reportform.RptEmployeeIDCard;
import hrm.common.reportform.RptEmployeeJoiningLetter;
import hrm.common.reportform.RptEmployeeList;
import hrm.common.reportform.RptEmployeeSeparationform;
import hrm.common.reportform.RptHolidays;
import hrm.common.reportform.RptIncrementProposal;
import hrm.common.reportform.RptIndividualEmployeeDetails;
import hrm.common.reportform.RptItemDistribution;
import hrm.common.reportform.RptJoinConfirm;
import hrm.common.reportform.RptLeaveApplication;
import hrm.common.reportform.RptLeaveRegister;
import hrm.common.reportform.RptLeaveRegisterIndividual;
import hrm.common.reportform.RptManPower;
import hrm.common.reportform.RptMaterialReturnForm;
import hrm.common.reportform.RptMonthlyAttendanceManually;
import hrm.common.reportform.RptMonthlyAttendanceSummary;
import hrm.common.reportform.RptNoticeInfo;
import hrm.common.reportform.RptOfferLetter;
import hrm.common.reportform.RptOverTimeRequest;
import hrm.common.reportform.RptPFIndividualLoanStatement;
import hrm.common.reportform.RptPFLoanApplication;
import hrm.common.reportform.RptPFLoanRegister;
import hrm.common.reportform.RptPFMemberList;
import hrm.common.reportform.RptReplacementLeaveApplication;
import hrm.common.reportform.RptSalaryStructure;
import hrm.common.reportform.RptShiftInformation;
import hrm.common.reportform.RptShortViewOfAttendance;
import hrm.common.reportform.RptShortViewOfOvertime;

import java.util.HashMap;
import java.util.Iterator;

import org.hibernate.Session;

import com.appform.hrmModule.BankBranchInfo;
import com.appform.hrmModule.BankInfo;
import com.appform.hrmModule.DateBetweenAttendanceDelete;
import com.appform.hrmModule.DateBetweenAttendanceManuallyMachine;
import com.appform.hrmModule.DepartmentInformation;
import com.appform.hrmModule.DesignationInformation;
import com.appform.hrmModule.EditEmployeeAttendanceMachine;
import com.appform.hrmModule.EmployeeAttendanceUploadSingleDevice;
import com.appform.hrmModule.EmployeeInformation;
import com.appform.hrmModule.EmployeeRequsitionForm;
import com.appform.hrmModule.GetEmployeeAttendance;
import com.appform.hrmModule.GradeInformation;
import com.appform.hrmModule.IncrementProcessMultiple;
import com.appform.hrmModule.IncrementType;
import com.appform.hrmModule.ItemDistribution;
import com.appform.hrmModule.ItemTypeInformation;
import com.appform.hrmModule.LeaveApplicationForm;
import com.appform.hrmModule.LeaveApprove;
import com.appform.hrmModule.LeaveApproveFinal;
import com.appform.hrmModule.LeaveApproveHR;
import com.appform.hrmModule.LeaveApprovePrimary;
import com.appform.hrmModule.LeaveBalanceGenerate;
import com.appform.hrmModule.LeaveEntitlement;
import com.appform.hrmModule.LeaveTypeInfo;
import com.appform.hrmModule.MealChargeInformation;
import com.appform.hrmModule.NoticeInfo;
import com.appform.hrmModule.OverTimeRequestApproval;
import com.appform.hrmModule.OverTimeRequestForm;
import com.appform.hrmModule.PresentBonusInformation;
import com.appform.hrmModule.ProjectInformation;
import com.appform.hrmModule.ReplacementLeaveApplication;
import com.appform.hrmModule.ReplacementLeaveApproval;
import com.appform.hrmModule.SectionInformation;
import com.appform.hrmModule.ShiftInformation;
import com.appform.hrmModule.UnitAndDepartmentWiseShiftInformation;
import com.appform.hrmModule.UnitInfo;
import com.appform.hrmModule.accessEmployeeLeave;
import com.appform.hrmModule.accessHrmReports;
import com.appform.hrmModule.accessHrmSetup;
import com.appform.hrmModule.accessHrmTrans;
import com.appform.hrmModule.attendanceManually;
import com.appform.hrmModule.holidayDeclare;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class HrmMenu
{
	private HashMap<Object, Object> winMap = new HashMap<Object, Object>();
	private static final Object CAPTION_PROPERTY = "caption";

	Tree tree;
	SessionBean sessionBean;
	Component component;

	Object hrmSetup = null;
	Object hrmTransaction = null;
	Object hrmTrsLeave = null;
	Object hrmTrsAttendance = null;
	Object hrmTrsSalary = null;
	Object hrmTrsOthers = null;
	Object hrmTrsTax = null;
	Object hrmTrsPFLoan = null;
	Object hrmSalaryLoan = null;
	Object hrmAttendance = null;
	Object hrmSalary = null;
	Object hrmLeave = null;
	Object hrmTrsLoan = null;
	Object hrmProvidentFund = null;
	Object reportAttendance = null;
	Object reportLeave = null;
	Object reportSalary = null;
	Object reportOt = null;
	Object reportOT = null;
	Object reportsLoan = null;
	Object reportsPFLoan = null;
	Object reportOthers = null;
	Object reportTax = null;
	Object hrmIncomeTax=null;
	Object hrmOthers=null;
	Object hrmOT=null;
	
	Object setupTransaction=null;
	Object setupReport=null;
	
	Object leaveTransaction=null;
	Object leaveReport=null;
	
	Object salaryLoanTransaction=null;
	Object salaryLoanReport=null;
	
	Object attendanceTransaction=null;
	Object attendanceReport=null;
	
	Object PFTransaction=null;
	Object PFReport=null;
	
	Object salaryTransaction=null;
	Object salaryReport=null;
	
	Object bonusTransaction=null;
	Object bonusReport=null;
	
	Object incomeTaxTransaction=null;
	Object incomeTaxReport=null;
	
	Object othersTransaction=null;
	Object othersReport=null;

	Object otTransaction=null;
	Object otReport=null;
	
	Object hrmReport = null;
	Object reportGeneral = null;
	boolean isPreview=false;

	public HrmMenu(Object hrmModule,Tree tree,SessionBean sessionBean,Component component)
	{
		this.tree = tree;
		this.sessionBean = sessionBean;
		this.component = component;

		treeAction();
		
		if(isValidMenu("hrmSetup"))
		{
			hrmSetup = addCaptionedItem("MASTER SETUP", hrmModule);
			addSetupChild(hrmSetup);
		}
		if(isValidMenu("employeeAttendance"))
		{
			hrmAttendance = addCaptionedItem("ATTENDANCE", hrmModule);
			addAttendanceChild(hrmAttendance);
		}
		if(isValidMenu("employeeLeave"))
		{
			hrmLeave = addCaptionedItem("LEAVE", hrmModule);
			addLeaveChild(hrmLeave);
		}
		if(isValidMenu("employeeOT"))
		{
			hrmOT = addCaptionedItem("OVER TIME", hrmModule);
			addOTChild(hrmOT);
		}
		if(isValidMenu("employeeOthers"))
		{
			hrmOthers=addCaptionedItem("OTHERS", hrmModule);
			addHrmOthers(hrmOthers);
		}
	}

	private void addSetupChild(Object hrmSetup)
	{

		if(isValidMenu("setupTransaction"))
		{
			setupTransaction = addCaptionedItem("TRANSACTION", hrmSetup);
			addSetupTransactionChild(setupTransaction);
		}
		if(isValidMenu("setupReport"))
		{
			setupReport = addCaptionedItem("REPORT", hrmSetup);
			addSetupReportChild(setupReport);
		}

	}
	private void addSetupTransactionChild(Object setupTransaction) {
		if(isValidMenu("ProjectInformation"))
		{
			addCaptionedItem("PROJECT INFORMATION", setupTransaction);
		}
		if(isValidMenu("DepartmentInformation"))
		{
			addCaptionedItem("DEPARTMENT INFORMATION", setupTransaction);
		}
		if(isValidMenu("SectionInformation"))
		{
			addCaptionedItem("SECTION INFORMATION.", setupTransaction);
		}
		if(isValidMenu("designationInformation"))
		{
			addCaptionedItem("DESIGNATION INFORMATION", setupTransaction);
		}
		if(isValidMenu("employeeInformation"))
		{
			addCaptionedItem("EMPLOYEE INFORMATION", setupTransaction);
		}
		if(isValidMenu("PresentBonusEntry"))
		{
			addCaptionedItem("PRESENT BONUS ENTRY", setupTransaction);
		}
		if(isValidMenu("IncrementType"))
		{
			addCaptionedItem("INCREMENT TYPE", setupTransaction);
		}

		if(isValidMenu("ShiftInformation"))
		{
			addCaptionedItem("SHIFT INFORMATION", setupTransaction);
		}

		if(isValidMenu("UnitAndSectionWiseShiftInformation"))
		{
			addCaptionedItem("PROJECT/DEPARTMENT WISE SHIFT INFORMATION", setupTransaction);
		}
		if(isValidMenu("NoticeInfo"))
		{
			addCaptionedItem("NOTICE INFORMATION", setupTransaction);
		}
		if(isValidMenu("ItemTypeInformation"))
		{
			addCaptionedItem("ITEM INFORMATION", setupTransaction);
		}
		if(isValidMenu("ItemDistribution"))
		{
			addCaptionedItem("ITEM DISTRIBUTION", setupTransaction);
		}
		if(isValidMenu("EmployeeRequsitionForm"))
		{
			addCaptionedItem("EMPLOYEE REQUSITION FORM",setupTransaction);
		}
		if(isValidMenu("BankInfo"))
		{
			addCaptionedItem("BANK INFORMATION",setupTransaction);
		}
		if(isValidMenu("BankBranchInfo"))
		{
			addCaptionedItem("BRANCH INFORMATION",setupTransaction);
		}
	}
	private void addSetupReportChild(Object setupReport) {
		if(isValidMenu("DesignationList"))
		{
			addCaptionedItem("DESIGNATION LIST", setupReport);
		}
		if(isValidMenu("IndividualEmployeeDetails"))
		{
			addCaptionedItem("INDIVIDUAL EMPLOYEE DETAILS ", setupReport);
		}
		if(isValidMenu("RptManPower"))
		{
			addCaptionedItem("MAN POWER", setupReport);
		}
		if(isValidMenu("NewEmployeeList"))
		{
			addCaptionedItem("NEW EMPLOYEE LIST", setupReport);
		}
		if(isValidMenu("EmployeeList"))
		{
			addCaptionedItem("EMPLOYEE LIST", setupReport);
		}
		if(isValidMenu("LengthOfService"))
		{
			addCaptionedItem("LENGTH OF SERVICE", setupReport);
		}
		if(isValidMenu("EmployeeIdCard"))
		{
			addCaptionedItem("EMPLOYEE ID CARD", setupReport);
		}
		if(isValidMenu("EmployeeAgeAsOnDate"))
		{
			addCaptionedItem("EMPLOYEE AGE AS ON DATE", setupReport);
		}
		if(isValidMenu("Employeeseparation"))
		{
			addCaptionedItem("EMPLOYEE SEPARATION", setupReport);
		}
		if(isValidMenu("EmployeeConfirmation"))
		{
			addCaptionedItem("EMPLOYEE CONFIRMATION", setupReport);
		}
		if(isValidMenu("editemployeeinformation"))
		{
			addCaptionedItem("EDIT EMPLOYEE INFORMATION", setupReport);
		}
		if(isValidMenu("rptOtNFridayEnable"))
		{
			addCaptionedItem("OT & FRIDAY ENABLE.", setupReport);
		}
		if(isValidMenu("RptNoticeInfo"))
		{
			addCaptionedItem("NOTICE INFORMATION.", setupReport);
		}
		if(isValidMenu("EmailLogInNotice"))
		{
			addCaptionedItem("EMAIL NOTICE",setupReport);
		}
		if(isValidMenu("RptItemDistribution"))
		{
			addCaptionedItem("ITEM DISTRIBUTION REPORT",setupReport);
		}
		if(isValidMenu("RptSalaryStructure"))
		{
			addCaptionedItem("SALARY STRUCTURE",setupReport);
		}
		if(isValidMenu("RptEmployeeJoiningLetter"))
		{
			addCaptionedItem("EMPLOYEE JOINING LETTER", setupReport);
		}
		if(isValidMenu("RptOfferLetter"))
		{
			addCaptionedItem("OFFER LETTER", setupReport);
		}
		if(isValidMenu("RptMaterialReturnForm"))
		{
			addCaptionedItem("MATERIAL RETURN FORM(NOC)", setupReport);
		}
		if(isValidMenu("RptEmployeeAgreement"))
		{
			addCaptionedItem("EMPLOYMENT AGREEMENT", setupReport);
		}
		if(isValidMenu("RptEmployeeAnnualEvaluationSheet"))
		{
			addCaptionedItem("EMPLOYEE ANNUAL EVLUATION SHEET", setupReport);
		}

	}
	private void addAttendanceChild(Object attendance)
	{
		if(isValidMenu("attendanceTransaction"))
		{
			attendanceTransaction = addCaptionedItem("TRANSACTION", attendance);
			addAttendanceTransaction(attendanceTransaction);
		}
		if(isValidMenu("leaveReport"))
		{
			attendanceReport = addCaptionedItem("REPORT", attendance);
			addattendanceReport(attendanceReport);
		}
	}
	private void addAttendanceTransaction(Object attendanceTransaction) {
		if(isValidMenu("holidayDeclare"))
		{
			addCaptionedItem("HOLIDAY DECLARE", attendanceTransaction);
		}
		if(isValidMenu("attendanceManually"))
		{
			addCaptionedItem("MONTHLY ATTENDANCE", attendanceTransaction);
		}
		if(isValidMenu("GetEmployeeAttendance"))
		{
			addCaptionedItem("GENERATE EMPLOYEE ATTENDANCE(MACHINE)", attendanceTransaction);
		}
		if(isValidMenu("DateBetweenAttendanceManuallyMachine"))
		{
			addCaptionedItem("DATE BETWEEN ATTENDANCE MANUALLY", attendanceTransaction);
		}
		if(isValidMenu("EditEmployeeAttendanceMachine"))
		{
			addCaptionedItem("EDIT DELETE EMPLOYEE ATTENDANCE",attendanceTransaction);
		}
		if(isValidMenu("DateBetweenAttendanceDelete"))
		{
			addCaptionedItem("DATE BETWEEN ATTENDANCE DELETE",attendanceTransaction);
		}

	}
	private void addattendanceReport(Object attendanceReport) {
		if(isValidMenu("holidayreport"))
		{
			addCaptionedItem("HOLIDAY REPORT", attendanceReport);
		}
		if(isValidMenu("shiftInformationReport"))
		{
			addCaptionedItem("SHIFT INFORMATION REPORT", attendanceReport);
		}
		
		if(isValidMenu("RptDailyAttendance"))
		{
			addCaptionedItem("DAILY ATTENDANCE STATEMENT",attendanceReport);
		}
		if(isValidMenu("RptDailyAbsent"))
		{
			addCaptionedItem("DAILY ABSENT STATEMENT",attendanceReport);
		}
		if(isValidMenu("RptDailyLateInEmployeeList"))
		{
			addCaptionedItem("DAILY LATE IN EMPLOYEE LIST", attendanceReport);
		}
		if(isValidMenu("RptDailyEarlyOutEmployeeList"))
		{
			addCaptionedItem("DAILY EARLY OUT EMPLOYEE LIST",attendanceReport);
		}
		if(isValidMenu("rptMonthlyAttendanceSummary"))
		{
			addCaptionedItem("EMPLOYEE WISE MONTHLY REPORT", attendanceReport);
		}
		if(isValidMenu("RptShortViewOfAttendance"))
		{
			addCaptionedItem("SHORT VIEW OF ATTENDANCE", attendanceReport);
		}
	}
	private void addLeaveChild(Object leave)
	{
		if(isValidMenu("leaveTransaction"))
		{
			leaveTransaction = addCaptionedItem("TRANSACTION", leave);
			addLeaveTransaction(leaveTransaction);
		}
		if(isValidMenu("leaveReport"))
		{
			leaveReport = addCaptionedItem("REPORT", leave);
			addleaveReport(leaveReport);
		}
		
	}
	private void addLeaveTransaction(Object leaveTransaction) {
		if(isValidMenu("leaveTypeEntry"))
		{
			addCaptionedItem("LEAVE TYPE ENTRY", leaveTransaction);
		}

		if(isValidMenu("LeaveEntitlement"))
		{
			addCaptionedItem("LEAVE ENTITLEMENT", leaveTransaction);
		}
		if(isValidMenu("leaveApplication"))
		{
			addCaptionedItem("LEAVE APPLICATION", leaveTransaction);
		}
		if(isValidMenu("LeaveBalanceGenerate"))
		{
			addCaptionedItem("LEAVE BALANCE GENERATE", leaveTransaction);
		}
		/*if(isValidMenu("LeaveApprovalStep1"))
		{
			addCaptionedItem("LEAVE APPROVAL STEP 1", leaveTransaction);
		}
		if(isValidMenu("LeaveApprovalStep2"))
		{
			addCaptionedItem("LEAVE APPROVAL STEP 2", leaveTransaction);
		}
		if(isValidMenu("LeaveApprovalStep3"))
		{
			addCaptionedItem("LEAVE APPROVAL STEP 3", leaveTransaction);
		}*/
		if(isValidMenu("LeaveApprove"))
		{
			addCaptionedItem("LEAVE APPROVAL", leaveTransaction);
		}
		if(isValidMenu("ReplacementLeaveApplication"))
		{
			addCaptionedItem("REPLACEMENT LEAVE APPLICATION", leaveTransaction);
		}
		if(isValidMenu("ReplacementLeaveApproval"))
		{
			addCaptionedItem("REPLACEMENT LEAVE APPROVAL", leaveTransaction);
		}
	}
	private void addleaveReport(Object leaveReport) {
		if(isValidMenu("leaveapplicationform"))
		{
			addCaptionedItem("LEAVE APPLICATION ", leaveReport);
		}
		if(isValidMenu("individualleaveregister"))
		{
			addCaptionedItem("INDIVIDUAL LEAVE REGISTER", leaveReport);
		}
		if(isValidMenu("leaveregister"))
		{
			addCaptionedItem("LEAVE SUMMARY", leaveReport);
		}
		if(isValidMenu("DateAndMonthWiseLeave"))
		{
			addCaptionedItem("DATE AND MONTH WISE LEAVE", leaveReport);
		}
		if(isValidMenu("RptReplacementLeaveApplication"))
		{
			addCaptionedItem("REPLACEMENT LEAVE APPLICATION.", leaveReport);
		}
		if(isValidMenu("RptEditDeleteReplacementLeave"))
		{
			addCaptionedItem("EDIT DELETE REPLACEMENT LEAVE", leaveReport);
		}
		
	}
	private void addHrmOthers(Object hrmOthers) {
		if(isValidMenu("OthersTransaction"))
		{
			othersTransaction = addCaptionedItem("TRANSACTION", hrmOthers);
			addOthersTransactionChild(othersTransaction);
		}
		if(isValidMenu("OthersReport"))
		{
			othersReport = addCaptionedItem("REPORT", hrmOthers);
			addOthersReportChild(othersReport);
		}
	}

	private void addOthersTransactionChild(Object OthersTransaction) {
		if(isValidMenu("salaryIncrementProcess"))
		{
			addCaptionedItem("SALARY INCREMENT PROCESS", OthersTransaction);
		}
	}

	private void addOthersReportChild(Object OthersReport) {
		if(isValidMenu("RptIncrementProposal"))
		{
			addCaptionedItem("SALARY INCREMENT REPORT",OthersReport);
		}
	}
	private void addOTChild(Object ot)
	{
		
		if(isValidMenu("salaryTransaction"))
		{
			othersTransaction = addCaptionedItem("TRANSACTION", ot);
			addOTTransactionChild(othersTransaction);
		}
		if(isValidMenu("salaryReport"))
		{
			otReport = addCaptionedItem("REPORT", ot);
			addOTReportChild(otReport);
		}
	}
	private void addOTTransactionChild(Object otTransaction) {
		if(isValidMenu("OverTimeRequestForm"))
		{
			addCaptionedItem("OVER TIME REQUEST FORM", otTransaction);
		}
		if(isValidMenu("OverTimeRequestApproval"))
		{
			addCaptionedItem("OVER TIME REQUEST APPROVAL", otTransaction);
		}

	}
	private void addOTReportChild(Object otReport) {
		
		if(isValidMenu("RptOverTimeRequest"))
		{
			addCaptionedItem("OVER TIME REQUEST", otReport);
		}
		if(isValidMenu("RptShortViewOfOvertime"))
		{
			addCaptionedItem("SHORT VIEW OF OVERTIME", otReport);
		}
		if(isValidMenu("RptEditDeleteOverTimeRequest"))
		{
			addCaptionedItem("EDIT DELETE OVER TIME REQUEST", otReport);
		}
	}


	private Object addCaptionedItem(String caption, Object parent) 
	{
		final Object id = tree.addItem();
		final Item item = tree.getItem(id);
		final Property p = item.getItemProperty(CAPTION_PROPERTY);

		p.setValue(caption);

		if (parent != null) 
		{
			tree.setChildrenAllowed(parent, true);
			tree.setParent(id, parent);
			tree.setChildrenAllowed(id, false);
		}
		return id;
	}

	@SuppressWarnings("serial")
	public void treeAction()
	{
		tree.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.getItem().toString().equalsIgnoreCase("HRM SETUP"))
				{
					showWindow(new accessHrmSetup(sessionBean),event.getItem(),"hrmSetup","HRM MODULE","SETUP");
				}
				if(event.getItem().toString().equalsIgnoreCase("HRM TRANSACTION"))
				{
					showWindow(new accessHrmTrans(sessionBean),event.getItem(),"hrmTransaction","HRM MODULE","TRANSACTION");
				}
				if(event.getItem().toString().equalsIgnoreCase("HRM REPORT"))
				{
					showWindow(new accessHrmReports(sessionBean),event.getItem(),"hrmReports","HRM MODULE","REPORT");
				}

				//TRANSACTION ACCESS
				if(event.getItem().toString().equalsIgnoreCase("EMPLOYEE LEAVE"))
				{
					showWindow(new accessEmployeeLeave(sessionBean),event.getItem(),"employeeLeave","HRM MODULE","TRANSACTION");
				}
				//HRM SETUP
				if(event.getItem().toString().equalsIgnoreCase("PROJECT INFORMATION"))
				{
					showWindow(new ProjectInformation(sessionBean,"ProjectInformation"),event.getItem(),"ProjectInformation","HRM MODULE","TRANSACTION");
				}
				if(event.getItem().toString().equalsIgnoreCase("DEPARTMENT INFORMATION"))
				{
					showWindow(new DepartmentInformation(sessionBean,"departmentInformation"),event.getItem(),"departmentInformation","HRM MODULE","TRANSACTION");
				}
				if(event.getItem().toString().equalsIgnoreCase("Section INFORMATION."))
				{
					showWindow(new SectionInformation(sessionBean,"SectionInformation"),event.getItem(),"SectionInformation","HRM MODULE","TRANSACTION");
				}

				if(event.getItem().toString().equalsIgnoreCase("DESIGNATION INFORMATION"))
				{
					showWindow(new DesignationInformation(sessionBean,"designationInformation"),event.getItem(),"designationInformation","HRM MODULE","TRANSACTION");
				}

				if(event.getItem().toString().equalsIgnoreCase("EMPLOYEE INFORMATION"))
				{
					showWindow(new EmployeeInformation(sessionBean,"employeeInformation"),event.getItem(),"employeeInformation","HRM MODULE","TRANSACTION");
				}
				if(event.getItem().toString().equalsIgnoreCase("MEAL CHARGE ENTRY"))
				{
					showWindow(new MealChargeInformation(sessionBean,"mealChargeInformation"),event.getItem(),"mealChargeInformation","HRM MODULE","TRANSACTION");
				}
				if(event.getItem().toString().equalsIgnoreCase("PRESENT BONUS ENTRY"))
				{
					showWindow(new PresentBonusInformation(sessionBean,"PresentBonusEntry"),event.getItem(),"PresentBonusEntry","HRM MODULE","TRANSACTION");
				}
				if(event.getItem().toString().equalsIgnoreCase("INCREMENT TYPE"))
				{
					showWindow(new IncrementType(sessionBean,"IncrementType"),event.getItem(),"IncrementType","HRM MODULE","TRANSACTION");
				}
				if(event.getItem().toString().equalsIgnoreCase("GRADE INFORMATION"))
				{
					showWindow(new GradeInformation(sessionBean,"GradeInformation"),event.getItem(),"GradeInformation","HRM MODULE","TRANSACTION");
				}

				if(event.getItem().toString().equalsIgnoreCase("UNIT INFORMATION"))
				{
					showWindow(new UnitInfo(sessionBean,"UnitInformation"),event.getItem(),"UnitInformation","HRM MODULE","TRANSACTION");
				}
				if(event.getItem().toString().equalsIgnoreCase("ITEM INFORMATION"))
				{
					showWindow(new ItemTypeInformation(sessionBean,"ItemTypeInformation"),event.getItem(),"ItemTypeInformation","HRM MODULE","TRANSACTION");
				}
				if(event.getItem().toString().equalsIgnoreCase("ITEM DISTRIBUTION"))
				{
					showWindow(new ItemDistribution(sessionBean,"ItemDistribution"), event.getItem(),"ItemDistribution","HRM MODULE","TRANSACTION");
				}

				//HRM TRANSACTION
				//Attendance
				if(event.getItem().toString().equalsIgnoreCase("HOLIDAY DECLARE"))
				{
					showWindow(new holidayDeclare(sessionBean,"holidayDeclare"),event.getItem(),"holidayDeclare","HRM MODULE","TRANSACTION");
				}

				if(event.getItem().toString().equalsIgnoreCase("MONTHLY ATTENDANCE"))
				{
					showWindow(new attendanceManually(sessionBean,"attendanceManually"),event.getItem(),"attendanceManually","HRM MODULE","TRANSACTION");
				}
				//Leave
				if(event.getItem().toString().equalsIgnoreCase("LEAVE TYPE ENTRY"))
				{
					showWindow(new LeaveTypeInfo(sessionBean,"leaveTypeEntry"),event.getItem(),"leaveTypeEntry","HRM MODULE","TRANSACTION");
				}
				if(event.getItem().toString().equalsIgnoreCase("LEAVE ENTITLEMENT"))
				{
					showWindow(new LeaveEntitlement(sessionBean,"LeaveEntitlement"),event.getItem(),"LeaveEntitlement","HRM MODULE","TRANSACTION");
				}
				if(event.getItem().toString().equalsIgnoreCase("LEAVE APPLICATION"))
				{
					showWindow(new LeaveApplicationForm(sessionBean,"leaveApplication",false),event.getItem(),"leaveApplication","HRM MODULE","TRANSACTION");
				}
				if(event.getItem().toString().equalsIgnoreCase("LEAVE BALANCE GENERATE"))
				{
					showWindow(new LeaveBalanceGenerate(sessionBean,"LeaveBalanceGenerate"),event.getItem(),"LeaveBalanceGenerate","HRM MODULE","TRANSACTION");
				}
				
				
				/*if(event.getItem().toString().equalsIgnoreCase("LEAVE APPROVAL STEP 1"))
				{
					showWindow(new LeaveApprovePrimary(sessionBean,"LeaveApprovalStep1"),event.getItem(),"LeaveApprovalStep1","HRM MODULE","TRANSACTION");
				}
				if(event.getItem().toString().equalsIgnoreCase("LEAVE APPROVAL STEP 2"))
				{
					showWindow(new LeaveApproveHR(sessionBean,"LeaveApprovalStep2"),event.getItem(),"LeaveApprovalStep2","HRM MODULE","TRANSACTION");
				}
				if(event.getItem().toString().equalsIgnoreCase("LEAVE APPROVAL STEP 3"))
				{
					showWindow(new LeaveApproveFinal(sessionBean,"LeaveApprovalStep3"),event.getItem(),"LeaveApprovalStep3","HRM MODULE","TRANSACTION");
				}*/
				
				
				if(event.getItem().toString().equalsIgnoreCase("LEAVE APPROVAL"))
				{	/*30-05-2020 order by Nazim sir & sumon*/
					showWindow(new LeaveApprove(sessionBean,"LeaveApprove"),event.getItem(),"LeaveApprove","HRM MODULE","TRANSACTION");
				}
				
				
				if(event.getItem().toString().equalsIgnoreCase("REPLACEMENT LEAVE APPLICATION"))
				{
					showWindow(new ReplacementLeaveApplication(sessionBean,"ReplacementLeaveApplication",false),event.getItem(),"ReplacementLeaveApplication","HRM MODULE","TRANSACTION");
				}	

				if(event.getItem().toString().equalsIgnoreCase("REPLACEMENT LEAVE APPROVAL"))
				{
					showWindow(new ReplacementLeaveApproval(sessionBean,"ReplacementLeaveApproval"),event.getItem(),"ReplacementLeaveApproval","HRM MODULE","TRANSACTION");
				}
				if(event.getItem().toString().equalsIgnoreCase("EMPLOYEE REQUSITION FORM"))
				{
					showWindow(new EmployeeRequsitionForm(sessionBean, "EmployeeRequsitionForm"), event.getItem(), "EmployeeRequsitionForm","HRM MODULE","TRANSACTION");
				}
				
				
				
				

				if(event.getItem().toString().equalsIgnoreCase("BANK INFORMATION"))
				{
					showWindow(new BankInfo(sessionBean, "BankInfo"), event.getItem(), "BankInfo","HRM MODULE","TRANSACTION");
				}
				if(event.getItem().toString().equalsIgnoreCase("BRANCH INFORMATION"))
				{
					showWindow(new BankBranchInfo(sessionBean, "BankBranchInfo"), event.getItem(), "BankBranchInfo","HRM MODULE","TRANSACTION");
				}

				//GENERAL HRM REPORTS
				if(event.getItem().toString().equalsIgnoreCase("INDIVIDUAL EMPLOYEE DETAILS "))
				{
					showWindow(new RptIndividualEmployeeDetails(sessionBean,"IndividualEmployeeDetails"),event.getItem(),"IndividualEmployeeDetails","HRM MODULE","REPORT");
				}

				if(event.getItem().toString().equalsIgnoreCase("EMPLOYEE LIST"))
				{
					showWindow(new RptEmployeeList(sessionBean,"EmployeeList"),event.getItem(),"EmployeeList","HRM MODULE","REPORT");
				}

				if(event.getItem().toString().equalsIgnoreCase("EMPLOYEE JOINING/CONFIRMATION DATE"))
				{
					showWindow(new RptJoinConfirm(sessionBean,"EmployeeJoining/ConfirmationDate"),event.getItem(),"EmployeeJoining/ConfirmationDate","HRM MODULE","REPORT");
				}
				if(event.getItem().toString().equalsIgnoreCase("EMPLOYEE SEPARATION"))
				{
					showWindow(new RptEmployeeSeparationform(sessionBean,"Employeeseparation"),event.getItem(),"Employeeseparation","HRM MODULE","REPORT");
				}
				if(event.getItem().toString().equalsIgnoreCase("EMPLOYEE CONFIRMATION"))
				{
					showWindow(new RptEmployeeConfirmation(sessionBean,"EmployeeConfirmation"),event.getItem(),"EmployeeConfirmation","HRM MODULE","REPORT");
				}
				if(event.getItem().toString().equalsIgnoreCase("EDIT EMPLOYEE INFORMATION"))
				{
					showWindow(new RptEditEmployeeInformation(sessionBean,"editemployeeinformation"),event.getItem(),"editemployeeinformation","HRM MODULE","REPORT");
				}
				if(event.getItem().toString().equalsIgnoreCase("NOTICE INFORMATION."))
				{
					showWindow(new RptNoticeInfo(sessionBean,"RptNoticeInfo"),event.getItem(),"RptNoticeInfo","HRM MODULE","REPORT");
				}

				if(event.getItem().toString().equalsIgnoreCase("EMAIL NOTICE"))
				{
					showWindow(new EmailLogInNotice(sessionBean, "EmailLogInNotice"), event.getItem(), "EmailLogInNotice","HRM MODULE","REPORT");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("EMPLOYEE WISE MONTHLY REPORT"))
				{
					showWindow(new RptMonthlyAttendanceSummary(sessionBean,"rptMonthlyAttendanceSummary"),event.getItem(),"rptMonthlyAttendanceSummary","HRM MODULE","REPORT");
				}

				if(event.getItem().toString().equalsIgnoreCase("SHORT VIEW OF ATTENDANCE"))
				{
					showWindow(new RptShortViewOfAttendance(sessionBean,"RptShortViewOfAttendance"),event.getItem(),"RptShortViewOfAttendance","HRM MODULE","REPORT");
				}
				
				//OT Report
				if(event.getItem().toString().equalsIgnoreCase("MONTH_WISE_OT"))
				{
					showWindow(new RptAuditMonthWiseOT(sessionBean,"auditmonthWiseOt"),event.getItem(),"auditmonthWiseOt","HRM MODULE","REPORT");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("LEAVE APPLICATION "))
				{
					showWindow(new RptLeaveApplication(sessionBean,"leaveapplicationform"),event.getItem(),"leaveapplicationform","HRM MODULE","REPORT");
				}	
				if(event.getItem().toString().equalsIgnoreCase("REPLACEMENT LEAVE APPLICATION."))
				{
					showWindow(new RptReplacementLeaveApplication(sessionBean,"RptReplacementLeaveApplication"),event.getItem(),"RptReplacementLeaveApplication","HRM MODULE","REPORT");
				}	
				if(event.getItem().toString().equalsIgnoreCase("EDIT DELETE REPLACEMENT LEAVE"))
				{
					showWindow(new RptEditDeleteReplacementLeave(sessionBean,"RptEditDeleteReplacementLeave"),event.getItem(),"RptEditDeleteReplacementLeave","HRM MODULE","REPORT");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("INDIVIDUAL LEAVE REGISTER"))
				{
					showWindow(new RptLeaveRegisterIndividual(sessionBean,"individualleaveregister"),event.getItem(),"individualleaveregister","HRM MODULE","REPORT");
				}
				if(event.getItem().toString().equalsIgnoreCase("LEAVE SUMMARY"))
				{
					showWindow(new RptLeaveRegister(sessionBean,"leaveregister"),event.getItem(),"leaveregister","HRM MODULE","REPORT");
				}
				if(event.getItem().toString().equalsIgnoreCase("DATE AND MONTH WISE LEAVE"))
				{
					showWindow(new RptDateAndMonthWiseLeave(sessionBean, "DateAndMonthWiseLeave"), event.getItem(), "DateAndMonthWiseLeave","HRM MODULE","REPORT");
				}
				if(event.getItem().toString().equalsIgnoreCase("HOLIDAY REPORT"))
				{
					showWindow(new RptHolidays(sessionBean,"holidayreport"),event.getItem(),"holidayreport","HRM MODULE","REPORT");
				}
				if(event.getItem().toString().equalsIgnoreCase("SHIFT INFORMATION REPORT"))
				{
					showWindow(new RptShiftInformation(sessionBean,"shiftInformationReport"),event.getItem(),"shiftInformationReport","HRM MODULE","REPORT");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("SALARY INCREMENT PROCESS"))
				{
					showWindow(new IncrementProcessMultiple(sessionBean,"salaryIncrementProcess"),event.getItem(),"salaryIncrementProcess","HRM MODULE","TRANSACTION");
				}
				if(event.getItem().toString().equalsIgnoreCase("SALARY INCREMENT REPORT"))
				{
					showWindow(new RptIncrementProposal(sessionBean, "RptIncrementProposal"), event.getItem(), "RptIncrementProposal","HRM MODULE","REPORT");
				}
				
				//OT Transaction 
				if(event.getItem().toString().equalsIgnoreCase("OVER TIME REQUEST FORM"))
				{
					showWindow(new OverTimeRequestForm(sessionBean,"OverTimeRequestForm",false),event.getItem(),"OverTimeRequestForm","HRM MODULE","TRANSACTION");
				}
				if(event.getItem().toString().equalsIgnoreCase("OVER TIME REQUEST APPROVAL"))
				{
					showWindow(new OverTimeRequestApproval(sessionBean,"OverTimeRequestApproval"),event.getItem(),"OverTimeRequestApproval","HRM MODULE","TRANSACTION");
				}
				
				//OT Report
				if(event.getItem().toString().equalsIgnoreCase("OVER TIME REQUEST"))
				{
					showWindow(new RptOverTimeRequest(sessionBean,"RptOverTimeRequest"),event.getItem(),"RptOverTimeRequest","HRM MODULE","REPORT");
				}				
				if(event.getItem().toString().equalsIgnoreCase("SHORT VIEW OF OVERTIME"))
				{
					showWindow(new RptShortViewOfOvertime(sessionBean,"RptShortViewOfOvertime"),event.getItem(),"RptShortViewOfOvertime","HRM MODULE","REPORT");
				}
				if(event.getItem().toString().equalsIgnoreCase("EDIT DELETE OVER TIME REQUEST"))
				{
					showWindow(new RptEditDeleteOverTimeRequest(sessionBean,"RptEditDeleteOverTimeRequest"),event.getItem(),"RptEditDeleteOverTimeRequest","HRM MODULE","REPORT");
				}
				
				
				
				if(event.getItem().toString().equalsIgnoreCase("EMPLOYEE ATTENDANCE UPLOAD"))
				{
					showWindow(new EmployeeAttendanceUploadSingleDevice(sessionBean,"attendanceTimeEntry"),event.getItem(),"attendanceTimeEntry","HRM MODULE","TRANSACTION");
				}
				if(event.getItem().toString().equalsIgnoreCase("GENERATE EMPLOYEE ATTENDANCE(MACHINE)"))
				{
					showWindow(new GetEmployeeAttendance(sessionBean,"GetEmployeeAttendance"), event.getItem(),"GetEmployeeAttendance","HRM MODULE","TRANSACTION");
				}
				if(event.getItem().toString().equalsIgnoreCase("DATE BETWEEN ATTENDANCE MANUALLY"))
				{
					showWindow(new DateBetweenAttendanceManuallyMachine(sessionBean,"DateBetweenAttendanceManuallyMachine"), event.getItem(),"DateBetweenAttendanceManuallyMachine","HRM MODULE","TRANSACTION");
				}
				if(event.getItem().toString().equalsIgnoreCase("EDIT DELETE EMPLOYEE ATTENDANCE"))
				{
					showWindow(new EditEmployeeAttendanceMachine(sessionBean,"EditEmployeeAttendanceMachine"), event.getItem(),"EditEmployeeAttendanceMachine","HRM MODULE","TRANSACTION");
				}
				if(event.getItem().toString().equalsIgnoreCase("DATE BETWEEN ATTENDANCE DELETE"))
				{
					showWindow(new DateBetweenAttendanceDelete(sessionBean,"DateBetweenAttendanceDelete"), event.getItem(),"DateBetweenAttendanceDelete","HRM MODULE","TRANSACTION");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("SHIFT INFORMATION"))
				{
					showWindow(new ShiftInformation(sessionBean, "ShiftInformation"), event.getItem(), "ShiftInformation","HRM MODULE","TRANSACTION");
				}
				if(event.getItem().toString().equalsIgnoreCase("PROJECT/DEPARTMENT WISE SHIFT INFORMATION"))
				{
					showWindow(new UnitAndDepartmentWiseShiftInformation(sessionBean, "UnitAndSectionWiseShiftInformation"), event.getItem(), "UnitAndSectionWiseShiftInformation","HRM MODULE","TRANSACTION");
				}
				if(event.getItem().toString().equalsIgnoreCase("NOTICE INFORMATION"))
				{
					showWindow(new NoticeInfo(sessionBean, "NoticeInfo"), event.getItem(), "NoticeInfo","HRM MODULE","TRANSACTION");
				}
				//Attendance Report
				
				if(event.getItem().toString().equalsIgnoreCase("MONTHLY ATTENDANCE MANUALLY"))
				{
					
					showWindow(new RptMonthlyAttendanceManually(sessionBean,"rptMonthlyAttendanceManually"),event.getItem(),"rptMonthlyAttendanceManually","HRM MODULE","REPORT");
				}

				if(event.getItem().toString().equalsIgnoreCase("DAILY ATTENDANCE STATEMENT"))
				{
					showWindow(new RptDailyAttendance(sessionBean, "RptDailyAttendance"), event.getItem(), "RptDailyAttendance","HRM MODULE","REPORT");
				}
				if(event.getItem().toString().equalsIgnoreCase("DAILY ABSENT STATEMENT"))
				{
					showWindow(new RptDailyAbsentReport(sessionBean, "RptDailyAbsent"), event.getItem(), "RptDailyAbsent","HRM MODULE","REPORT");
				}
				if(event.getItem().toString().equalsIgnoreCase("DAILY LATE IN EMPLOYEE LIST"))
				{
					showWindow(new RptDailyLateIn(sessionBean, "RptDailyLateInEmployeeList"), event.getItem(), "RptDailyLateInEmployeeList","HRM MODULE","REPORT");
				}
				if(event.getItem().toString().equalsIgnoreCase("DAILY EARLY OUT EMPLOYEE LIST"))
				{
					showWindow(new RptDailyEarlyOut(sessionBean, "RptDailyEarlyOutEmployeeList"), event.getItem(), "RptDailyEarlyOutEmployeeList","HRM MODULE","REPORT");
				}

				if(event.getItem().toString().equalsIgnoreCase("INDIVIDUAL EMPLOYEE DETAILS "))
				{
					showWindow(new RptIndividualEmployeeDetails(sessionBean,"IndividualEmployeeDetails"),event.getItem(),"IndividualEmployeeDetails","HRM MODULE","REPORT");
				}
				if(event.getItem().toString().equalsIgnoreCase("DESIGNATION LIST"))
				{
					showWindow(new RptDesignationList(sessionBean,"DesignationList"), event.getItem(),"DesignationList","HRM MODULE","REPORT");
				}
				if(event.getItem().toString().equalsIgnoreCase("EMPLOYEE LIST"))
				{
					showWindow(new RptEmployeeList(sessionBean,"EmployeeList"),event.getItem(),"EmployeeList","HRM MODULE","REPORT");
				}
				if(event.getItem().toString().equalsIgnoreCase("MAN POWER"))
				{
					showWindow(new RptManPower(sessionBean,"RptManPower"),event.getItem(),"RptManPower","HRM MODULE","REPORT");
				}

				if(event.getItem().toString().equalsIgnoreCase("EMPLOYEE JOINING LETTER"))
				{
					showWindow(new RptEmployeeJoiningLetter(sessionBean,"RptEmployeeJoiningLetter"),event.getItem(),"RptEmployeeJoiningLetter","HRM MODULE","REPORT");
				}
				if(event.getItem().toString().equalsIgnoreCase("OFFER LETTER"))
				{
					showWindow(new RptOfferLetter(sessionBean,"RptOfferLetter"),event.getItem(),"RptOfferLetter","HRM MODULE","REPORT");
				}
				if(event.getItem().toString().equalsIgnoreCase("MATERIAL RETURN FORM(NOC)"))
				{
					showWindow(new RptMaterialReturnForm(sessionBean,"RptMaterialReturnForm"),event.getItem(),"RptMaterialReturnForm","HRM MODULE","REPORT");
				}
				if(event.getItem().toString().equalsIgnoreCase("EMPLOYMENT AGREEMENT"))
				{
					showWindow(new RptEmployeeAgreement(sessionBean,"RptEmployeeAgreement"),event.getItem(),"RptEmployeeAgreement","HRM MODULE","REPORT");
				}
				if(event.getItem().toString().equalsIgnoreCase("EMPLOYEE ANNUAL EVLUATION SHEET"))
				{
					showWindow(new RptEmployeeAnnualEvaluationSheet(sessionBean,"RptEmployeeAnnualEvaluationSheet"),event.getItem(),"RptEmployeeAnnualEvaluationSheet","HRM MODULE","REPORT");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("NEW EMPLOYEE LIST"))
				{
					showWindow(new NewEmployeeList(sessionBean,"NewEmployeeList"),event.getItem(),"NewEmployeeList","HRM MODULE","REPORT");
				}
				if(event.getItem().toString().equalsIgnoreCase("LENGTH OF SERVICE"))
				{
					showWindow(new RptJoinConfirm(sessionBean,"LengthOfService"),event.getItem(),"LengthOfService","HRM MODULE","REPORT");
				}
				if(event.getItem().toString().equalsIgnoreCase("EMPLOYEE ID CARD"))
				{
					showWindow(new RptEmployeeIDCard(sessionBean,"EmployeeIdCard"),event.getItem(),"EmployeeIdCard","HRM MODULE","REPORT");
				}
				if(event.getItem().toString().equalsIgnoreCase("EMPLOYEE AGE AS ON DATE"))
				{
					showWindow(new RptAgeAsOnDate(sessionBean,"EmployeeAgeAsOnDate"),event.getItem(),"EmployeeAgeAsOnDate","HRM MODULE","REPORT");
				}
				if(event.getItem().toString().equalsIgnoreCase("SALARY STRUCTURE"))
				{
					showWindow(new RptSalaryStructure(sessionBean,"RptSalaryStructure"), event.getItem(),"RptSalaryStructure","HRM MODULE","REPORT");
				}
				if(event.getItem().toString().equalsIgnoreCase("PF MEMBER LIST"))
				{
					showWindow(new RptPFMemberList(sessionBean,"RptPFMemberList"), event.getItem(),"RptPFMemberList","HRM MODULE","REPORT");
				}
				if(event.getItem().toString().equalsIgnoreCase("ITEM DISTRIBUTION REPORT"))
				{
					showWindow(new RptItemDistribution(sessionBean,"RptItemDistribution"), event.getItem(),"RptItemDistribution","HRM MODULE","REPORT");
				}
				if(event.getItem().toString().equalsIgnoreCase("OT & FRIDAY ENABLE."))
				{
					showWindow(new hrm.common.reportform.RptOTNFridayEnable(sessionBean,"rptOtNFridayEnable"),event.getItem(),"rptOtNFridayEnable","HRM MODULE","REPORT");
				}

				if(event.getItem().toString().equalsIgnoreCase("PF LOAN APPLICATION"))
				{
					showWindow(new RptPFLoanApplication(sessionBean,"PFlaonApplication"),event.getItem(),"PFlaonApplication","HRM MODULE","REPORT");
				}
				if(event.getItem().toString().equalsIgnoreCase("PF LOAN SUMMARY"))
				{
					showWindow(new RptPFLoanRegister(sessionBean,"PFloanRegister"),event.getItem(),"PFloanRegister","HRM MODULE","REPORT");
				}
				if(event.getItem().toString().equalsIgnoreCase("PF INDIVIDUAL LOAN STATEMENT"))
				{
					showWindow(new RptPFIndividualLoanStatement(sessionBean,"PFindividualLoanStatement"),event.getItem(),"PFindividualLoanStatement","HRM MODULE","REPORT");
				}
			}
		});
	}

	private boolean isValidMenu(String id)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "SELECT vMenuId FROM dbo.tbUserAuthentication WHERE vMenuId = '"+id+"'"
					+ " and vUserId = '"+sessionBean.getUserId()+"'";
			Iterator<?> iter = session.createSQLQuery(sql).list().iterator();
			if(!iter.hasNext())
			{
				return true;
			}
		}
		catch(Exception exp)
		{
			System.out.println(exp);
		}
		finally{session.close();}
		return false;
	}
	@SuppressWarnings("serial")
	private void showWindow(Window win, Object selectedItem,String mid,String Module,String Type)
	{
		try
		{
			final String id = selectedItem+"";

			if(!sessionBean.getAuthenticWindow())
			{
				if(isOpen(id))
				{
					win.center();
					win.setStyleName("cwindow");

					component.getWindow().addWindow(win);
					win.setCloseShortcut(KeyCode.ESCAPE);

					winMap.put(id,id);

					win.addListener(new Window.CloseListener() 
					{
						public void windowClose(CloseEvent e) 
						{
							winMap.remove(id);                	
						}
					});
				}
			}
			else
			{
				sessionBean.setPermitForm(mid,selectedItem.toString(),Module,Type);
			}
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
	}

	private  boolean isOpen(String id)
	{
		return !winMap.containsKey(id);
	}
}