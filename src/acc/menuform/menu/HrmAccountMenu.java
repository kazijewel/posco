package acc.menuform.menu;

import hrm.common.reportform.EmailLogIn;
import hrm.common.reportform.RptAuditMonthWiseOT;
import hrm.common.reportform.RptBankStatement;
import hrm.common.reportform.RptBonusNotesRequisition;
import hrm.common.reportform.RptBonusStatement;
import hrm.common.reportform.RptEditMonthlySalary;
import hrm.common.reportform.RptEmployeeBonus;
import hrm.common.reportform.RptIncomeTaxStatement;
import hrm.common.reportform.RptIndividualOt;
import hrm.common.reportform.RptIndivitualIncomeTax;
import hrm.common.reportform.RptMonthlyHouseAllowanceSheet;
import hrm.common.reportform.RptMonthlyMobileAllowanceSheet;
import hrm.common.reportform.RptMonthlyPaySlip;
import hrm.common.reportform.RptMonthlySalary;
import hrm.common.reportform.RptNotesRequisition;
import hrm.common.reportform.RptOverTimeStatement;
import hrm.common.reportform.RptSalaryIncomeTaxCertification;

import java.util.HashMap;
import java.util.Iterator;

import org.hibernate.Session;

import com.appform.hrmModule.CheckNoEntry;
import com.appform.hrmModule.DeleteMonthlySalary;
import com.appform.hrmModule.EditMonthlySalary;
import com.appform.hrmModule.FestivalBonus;
import com.appform.hrmModule.IncomeTax;
import com.appform.hrmModule.MonthlySalaryGenerateMachine;
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

public class HrmAccountMenu
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

	public HrmAccountMenu(Object hrmModule,Tree tree,SessionBean sessionBean,Component component)
	{
		this.tree = tree;
		this.sessionBean = sessionBean;
		this.component = component;

		treeAction();
		
		if(isValidMenu("employeeSalary"))
		{
			hrmSalary = addCaptionedItem("MONTHLY SALARY.", hrmModule);
			addSalaryChild(hrmSalary);
		}
		/*if(isValidMenu("employeeOT"))
		{
			hrmOT = addCaptionedItem("OVER TIME", hrmModule);
			addOTChild(hrmOT);
		}*/
		
		if(isValidMenu("employeeIncomeTax"))
		{
			hrmIncomeTax=addCaptionedItem("INCOME TAX", hrmModule);
			addIncomeTax(hrmIncomeTax);
		}
		if(isValidMenu("employeeOthers"))
		{
			hrmOthers=addCaptionedItem("OTHERS", hrmModule);
			addHrmOthers(hrmOthers);
		}
	}

	private void addSalaryChild(Object salary)
	{
		
		if(isValidMenu("salaryTransaction"))
		{
			salaryTransaction = addCaptionedItem("TRANSACTION", salary);
			addSalaryTransaction(salaryTransaction);
		}
		if(isValidMenu("salaryReport"))
		{
			salaryReport = addCaptionedItem("REPORT", salary);
			addsalaryReport(salaryReport);
		}
	}
	private void addSalaryTransaction(Object salaryTransaction) {
		/*if(isValidMenu("monthlySalaryGenerate"))
		{
			addCaptionedItem("GENERATE MONTHLY SALARY", salaryTransaction);
		}*/
		if(isValidMenu("monthlySalaryGenerateMachine"))
		{
			addCaptionedItem("GENERATE MONTHLY SALARY MACHINE", salaryTransaction);
		}
		if(isValidMenu("editMonthlySalary"))
		{
			addCaptionedItem("EDIT MONTHLY SALARY", salaryTransaction);
		}

		/*if(isValidMenu("editMonthlySalary(device)"))
		{
			addCaptionedItem("EDIT MONTHLY SALARY (MACHINE)",hrmTrsSalary);
		}*/
		if(isValidMenu("DeleteMonthlySalary"))
		{
			addCaptionedItem("DELETE MONTHLY SALARY",salaryTransaction);
		}
	}
	private void addsalaryReport(Object salaryReport) {
		if(isValidMenu("RptMonthlySalary"))
		{
			addCaptionedItem("MONTHLY SALARY", salaryReport);
		}
		if(isValidMenu("RptMonthlyHouseAllowanceSheet"))
		{
			addCaptionedItem("MONTHLY HOUSE ALLOWANCE SHEET", salaryReport);
		}
		if(isValidMenu("RptMonthlyMobileAllowanceSheet"))
		{
			addCaptionedItem("MONTHLY MOBILE ALLOWANCE SHEET", salaryReport);
		}
		/*if(isValidMenu("monthlySalary(device)"))
		{
			addCaptionedItem("MONTHLY SALRY(MACHINE)", reportSalary);
		}*/
		if(isValidMenu("editMonthlySalaryReport"))
		{
			addCaptionedItem("EDIT MONTHLY SALARY.", salaryReport);
		}
		/*if(isValidMenu("editMonthlySalaryReport(device)"))
		{
			addCaptionedItem("EDIT MONTHLY SALARY(MACHINE)", reportSalary);
		}*/
		if(isValidMenu("RptMonthlyPaySlip"))
		{
			addCaptionedItem("PAY SLIP", salaryReport);
		}
		if(isValidMenu("emailSendPaySlip"))
		{
			addCaptionedItem("EMAIL_SEND_PAY_SLIP", salaryReport);
		}
		if(isValidMenu("bankStatement"))
		{
			addCaptionedItem("BANK ADVICE WITH FORWARDING LETTER", salaryReport);
		}
		if(isValidMenu("RptNotesRequisition"))
		{
			addCaptionedItem("NOTES REQUISITION", salaryReport);
		}
		if(isValidMenu("RptSalaryAndIncomeTaxCertification"))
		{
			addCaptionedItem("SALARY AND INCOME TAX CERTIFICATION", salaryReport);
		}
		/*if(isValidMenu("RptEmployeeSeparationList"))
		{
			addCaptionedItem("EMPLOYEE SEPARATION LIST", salaryReport);
		}*/

	}
	/*private void addOTChild(Object ot)
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
	}*/
	/*private void addOTTransactionChild(Object otTransaction) {
		if(isValidMenu("OverTimeRequestForm"))
		{
			addCaptionedItem("OVER TIME REQUEST FORM", otTransaction);
		}
	}*/
	/*private void addOTReportChild(Object otReport) {
		
		if(isValidMenu("RptOverTimeRequest"))
		{
			addCaptionedItem("OVER TIME REQUEST", otReport);
		}
		if(isValidMenu("RptMonthWiseOtStatement"))
		{
			addCaptionedItem("MONTH WISE OT STATEMENT", otReport);
		}
		if(isValidMenu("RptShortViewOfOvertime"))
		{
			addCaptionedItem("SHORT VIEW OF OVERTIME", otReport);
		}
		
		if(isValidMenu("rptMonthlyFridayAllowance"))
		{
			addCaptionedItem("MONTHLY HOLIDAY ALLOWANCE", otReport);
		}
		if(isValidMenu("RptHolidayBankStatement"))
		{
			addCaptionedItem("HOLIDAY BANK STATEMENT", reportOt);
		}
	}*/
	private void addIncomeTax(Object hrmIncomeTax) {
		if(isValidMenu("incomeTaxTransaction"))
		{
			incomeTaxTransaction = addCaptionedItem("TRANSACTION", hrmIncomeTax);
			addIncomeTaxTransactionChild(incomeTaxTransaction);
		}
		if(isValidMenu("setupReport"))
		{
			incomeTaxReport = addCaptionedItem("REPORT", hrmIncomeTax);
			addIncomeTaxReportChild(incomeTaxReport);
		}
	}
	private void addIncomeTaxTransactionChild(Object incomeTaxTransaction) {
		if(isValidMenu("CheckDetailsEntry"))
		{
			addCaptionedItem("CHEQUE DETAILS ENTRY", incomeTaxTransaction);
		}
		if(isValidMenu("IncomeTax"))
		{
			addCaptionedItem("TAX ASSESSMENT", incomeTaxTransaction);
		}
	}
	private void addIncomeTaxReportChild(Object incomeTaxReport) {
		if(isValidMenu("RptIndivitualIncomeTax"))
		{
			addCaptionedItem("INCOME TAX INDIVITUAL",incomeTaxReport);
		}
		if(isValidMenu("RptIncomeTaxStatement"))
		{
			addCaptionedItem("INCOME TAX STATEMENT",incomeTaxReport);
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
		if(isValidMenu("FestivalBonus"))
		{
			addCaptionedItem("FESTIVAL BONUS",OthersTransaction);
		}
	}
	private void addOthersReportChild(Object OthersReport) {
		if(isValidMenu("RptBonusStatement"))
		{
			addCaptionedItem("BONUS STATEMENT",OthersReport);
		}
		if(isValidMenu("RptBonusNotesRequisition"))
		{
			addCaptionedItem("BONUS NOTES REQUISITION",OthersReport);
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
				/*if(event.getItem().toString().equalsIgnoreCase("GENERATE MONTHLY SALARY"))
				{
					showWindow(new MonthlySalaryGenerate(sessionBean,"monthlySalaryGenerate"),event.getItem(),"monthlySalaryGenerate","HRM MODULE","TRANSACTION");
				}*/
				
				if(event.getItem().toString().equalsIgnoreCase("GENERATE MONTHLY SALARY MACHINE"))
				{
					showWindow(new MonthlySalaryGenerateMachine(sessionBean,"MonthlySalaryGenerateMachine"),event.getItem(),"MonthlySalaryGenerateMachine","HRM MODULE","TRANSACTION");
				}
				if(event.getItem().toString().equalsIgnoreCase("EDIT MONTHLY SALARY"))
				{
					showWindow(new EditMonthlySalary(sessionBean,"editMonthlySalary"),event.getItem(),"editMonthlySalary","HRM MODULE","TRANSACTION");
				}
				if(event.getItem().toString().equalsIgnoreCase("DELETE MONTHLY SALARY"))
				{
					showWindow(new DeleteMonthlySalary(sessionBean,"DeleteMonthlySalary"),event.getItem(),"DeleteMonthlySalary","HRM MODULE","TRANSACTION");
				}


				/*if(event.getItem().toString().equalsIgnoreCase("SALARY INCREMENT PROCESS"))
				{
					showWindow(new IncrementProcessMultiple(sessionBean,"salaryIncrementProcess"),event.getItem(),"salaryIncrementProcess");
				}*/

				if(event.getItem().toString().equalsIgnoreCase("FESTIVAL BONUS"))
				{
					showWindow(new FestivalBonus(sessionBean,"FestivalBonus"),event.getItem(),"FestivalBonus","HRM MODULE","TRANSACTION");
				}

				
				
				//OT Report
				if(event.getItem().toString().equalsIgnoreCase("MONTH_WISE_OT"))
				{
					showWindow(new RptAuditMonthWiseOT(sessionBean,"auditmonthWiseOt"),event.getItem(),"auditmonthWiseOt","HRM MODULE","REPORT");
				}
				/*if(event.getItem().toString().equalsIgnoreCase("OVER TIME REQUEST FORM"))
				{
					showWindow(new OverTimeRequestForm(sessionBean,"OverTimeRequestForm",false),event.getItem(),"OverTimeRequestForm","HRM MODULE","TRANSACTION");
				}*/
				

				if(event.getItem().toString().equalsIgnoreCase("MONTHLY SALARY"))
				{
					showWindow(new RptMonthlySalary(sessionBean,"RptMonthlySalary"),event.getItem(),"RptMonthlySalary","HRM MODULE","REPORT");
				}
				if(event.getItem().toString().equalsIgnoreCase("MONTHLY HOUSE ALLOWANCE SHEET"))
				{
					showWindow(new RptMonthlyHouseAllowanceSheet(sessionBean,"RptMonthlyHouseAllowanceSheet"),event.getItem(),"RptMonthlyHouseAllowanceSheet","HRM MODULE","REPORT");
				}
				if(event.getItem().toString().equalsIgnoreCase("MONTHLY MOBILE ALLOWANCE SHEET"))
				{
					showWindow(new RptMonthlyMobileAllowanceSheet(sessionBean,"RptMonthlyMobileAllowanceSheet"),event.getItem(),"RptMonthlyMobileAllowanceSheet","HRM MODULE","REPORT");
				}

				if(event.getItem().toString().equalsIgnoreCase("EDIT MONTHLY SALARY."))
				{
					showWindow(new RptEditMonthlySalary(sessionBean,"editMonthlySalaryReport"),event.getItem(),"editMonthlySalaryReport","HRM MODULE","REPORT");
				}


				if(event.getItem().toString().equalsIgnoreCase("INDIVIDUAL OT STATEMENT"))
				{
					showWindow(new RptIndividualOt(sessionBean,"individualOTStatement"),event.getItem(),"individualOTStatement","HRM MODULE","REPORT");
				}
				if(event.getItem().toString().equalsIgnoreCase("EMAIL_SEND_PAY_SLIP"))
				{
					showWindow(new EmailLogIn(sessionBean, "emailSendPayslip"), event.getItem(), "emailSendPayslip","HRM MODULE","REPORT");
				}
				if(event.getItem().toString().equalsIgnoreCase("PAY SLIP"))
				{
					showWindow(new RptMonthlyPaySlip(sessionBean, "RptMonthlyPaySlip"), event.getItem(), "RptMonthlyPaySlip","HRM MODULE","REPORT");
				}
				if(event.getItem().toString().equalsIgnoreCase("SALARY AND INCOME TAX CERTIFICATION"))
				{
					showWindow(new RptSalaryIncomeTaxCertification(sessionBean, "RptSalaryAndIncomeTaxCertification"), event.getItem(), "RptSalaryAndIncomeTaxCertification","HRM MODULE","REPORT");
				}
				

            	
				if(event.getItem().toString().equalsIgnoreCase("BANK ADVICE WITH FORWARDING LETTER"))
				{
					showWindow(new RptBankStatement(sessionBean,"bankStatement"),event.getItem(),"bankStatement","HRM MODULE","REPORT");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("NOTES REQUISITION"))
				{
					showWindow(new RptNotesRequisition(sessionBean,"RptNotesRequisition"),event.getItem(),"RptNotesRequisition","HRM MODULE","REPORT");
				}
				if(event.getItem().toString().equalsIgnoreCase("BONUS STATEMENT"))
				{
					showWindow(new RptBonusStatement(sessionBean,"RptBonusStatement"),event.getItem(),"RptBonusStatement","HRM MODULE","REPORT");
				}
				if(event.getItem().toString().equalsIgnoreCase("BONUS NOTES REQUISITION"))
				{
					showWindow(new RptBonusNotesRequisition(sessionBean,"RptBonusNotesRequisition"),event.getItem(),"RptBonusNotesRequisition","HRM MODULE","REPORT");
				}
				
				//OT Report

				/*if(event.getItem().toString().equalsIgnoreCase("MONTH WISE OT STATEMENT"))
				{
					showWindow(new RptMonthWiseOtStatement(sessionBean,"RptMonthWiseOtStatement"),event.getItem(),"RptMonthWiseOtStatement","HRM MODULE","REPORT");
				}
				if(event.getItem().toString().equalsIgnoreCase("SHORT VIEW OF OVERTIME"))
				{
					showWindow(new RptShortViewOfOvertime(sessionBean,"RptShortViewOfOvertime"),event.getItem(),"RptShortViewOfOvertime","HRM MODULE","REPORT");
				}
				if(event.getItem().toString().equalsIgnoreCase("MONTHLY HOLIDAY ALLOWANCE"))
				{
					showWindow(new rptMonthlyFridayAllowance(sessionBean,"rptMonthlyFridayAllowance"),event.getItem(),"rptMonthlyFridayAllowance","HRM MODULE","REPORT");
				}
				if(event.getItem().toString().equalsIgnoreCase("OVER TIME REQUEST"))
				{
					showWindow(new RptOverTimeRequest(sessionBean,"RptOverTimeRequest"),event.getItem(),"RptOverTimeRequest","HRM MODULE","REPORT");
				}*/
				
				
				//IncomeTax
				if(event.getItem().toString().equalsIgnoreCase("CHEQUE DETAILS ENTRY"))
				{
					showWindow(new CheckNoEntry(sessionBean,"CheckDetailsEntry"),event.getItem(),"CheckDetailsEntry","HRM MODULE","TRANSACTION");
				}
				if(event.getItem().toString().equalsIgnoreCase("TAX ASSESSMENT"))
				{
					showWindow(new IncomeTax(sessionBean,"IncomeTax"),event.getItem(),"IncomeTax","HRM MODULE","TRANSACTION");
				}
				


				//Others
				if(event.getItem().toString().equalsIgnoreCase("FESTIVAL BONUS REPORT"))
				{
					showWindow(new RptEmployeeBonus(sessionBean, "RptEmployeeBonus"), event.getItem(), "RptEmployeeBonus","HRM MODULE","REPORT");
				}

				//IncomeTax Report
				if(event.getItem().toString().equalsIgnoreCase("INCOME TAX INDIVITUAL"))
				{
					showWindow(new RptIndivitualIncomeTax(sessionBean,"RptIndivitualIncomeTax"), event.getItem(),"RptIndivitualIncomeTax","HRM MODULE","REPORT");
				}
				if(event.getItem().toString().equalsIgnoreCase("INCOME TAX STATEMENT"))
				{
					showWindow(new RptIncomeTaxStatement(sessionBean,"RptIncomeTaxStatement"), event.getItem(),"RptIncomeTaxStatement","HRM MODULE","REPORT");
				}

				
				/*if(event.getItem().toString().equalsIgnoreCase("OT & FRIDAY ENABLE."))
				{
					showWindow(new hrm.common.reportform.RptOTNFridayEnable(sessionBean,"rptOtNFridayEnable"),event.getItem(),"rptOtNFridayEnable","HRM MODULE","REPORT");
				}*/
				
				
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