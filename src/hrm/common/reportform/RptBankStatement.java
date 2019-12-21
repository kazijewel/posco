package hrm.common.reportform;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

import com.common.share.CommaSeparator;
import com.common.share.CommonButton;
import com.common.share.CommonMethod;
import com.common.share.FocusMoveByEnter;
import com.common.share.GenerateExcelReport;
import com.common.share.ReportDate;
import com.common.share.ReportOption;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class RptBankStatement extends Window {

	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private PopupDateField dDate;
	private ComboBox cmbBankName;
	private ComboBox cmbBranchName;
	private ComboBox cmbMonthName;
	private TextField txtAccountNo;
	private TextField txtReferrenceNo;

	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dDayFormat = new SimpleDateFormat("dd");
	private SimpleDateFormat dMonthFormat1 = new SimpleDateFormat("MMMMM");
	private SimpleDateFormat dYearFormat = new SimpleDateFormat("yyyy");

	private CommaSeparator commaSeparator=new CommaSeparator();

	ArrayList<Component> allComp = new ArrayList<Component>();

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	private OptionGroup opgReportType;
	private static final List<String> reportType=Arrays.asList(new String[]{"Forwarding Letter","Encloser","Excel"});
	private OptionGroup opgMoneyTransferType;
	private static final List<String> accountType=Arrays.asList(new String[]{"Bank A/C","BFTN"/*,"Cash","Mobile A/C"*/});
	private CommonMethod cm;
	private String menuId = "";
	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other","Excel"});
	String getAccount="";

	public RptBankStatement(SessionBean sessionBean,String menuId)
	{
		this.sessionBean=sessionBean;
		this.setCaption("BANK ADVICE :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		setEventAction();
		focusMove();
		cmbMonthNameDataLoad();
		cmbBankNameAddData();
		authenticationCheck();
	}
	private void authenticationCheck()
	{
		cm.checkFormAction(menuId);
		if(!sessionBean.isSuperAdmin())
		{
		if(!sessionBean.isAdmin())
		{
			if(!cm.isSave)
			{cButton.btnSave.setVisible(false);}
			if(!cm.isEdit)
			{cButton.btnEdit.setVisible(false);}
			if(!cm.isDelete)
			{cButton.btnDelete.setVisible(false);}
			if(!cm.isPreview)
			{cButton.btnPreview.setVisible(false);}
		}
		}
	}
	private void cmbMonthNameDataLoad()
	{
		if(!opgMoneyTransferType.getValue().toString().isEmpty())
		{
			cmbMonthName.removeAllItems();
			Session session=SessionFactoryUtil.getInstance().openSession();
			try{
				String sql="select distinct CONVERT(date,DATEADD(s,-1,DATEADD(mm, DATEDIFF(m,0,ms.dSalaryDate)+1,0)))dates, "+
						" CONVERT(varchar,DATENAME(MONTH,ms.dSalaryDate))+'-'+CONVERT(varchar,DATENAME(YEAR,ms.dSalaryDate))monthYear,ms.dSalaryDate  "+
						" from tbMonthlySalary ms inner join tbEmpOfficialPersonalInfo epo on epo.vEmployeeId=ms.vEmployeeID "+
						" where epo.vMoneyTransferType='"+opgMoneyTransferType.getValue().toString()+"' order by ms.dSalaryDate desc";

				System.out.println(sql);
				Iterator<?> iter=session.createSQLQuery(sql).list().iterator();
				
				while(iter.hasNext())
				{
					Object element[]=(Object[])iter.next();
					cmbMonthName.addItem(element[0]);
					cmbMonthName.setItemCaption(element[0], element[1].toString());
				}
			}catch(Exception exp)
			{
				showNotification("","cmbMonthNameDataLoad :"+exp,Notification.TYPE_ERROR_MESSAGE);
			}
			finally{
				session.close();
			}
		}
	}
	public void cmbBankNameAddData()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vBankId,bankName from tbBankName order by bankName ";
			List <?> list=session.createSQLQuery(query).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbBankName.addItem(element[0]);
				cmbBankName.setItemCaption(element[0], (String) element[1]);
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbBankNameAddData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void cmbBranchNameAddData()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select id,branchName from tbBankBranch order by branchName";
			List <?> list=session.createSQLQuery(query).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbBranchName.addItem(element[0]);
				cmbBranchName.setItemCaption(element[0], (String) element[1]);
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbBranchNameAddData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void setEventAction()
	{

		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(dDate.getValue()!=null)
				{
					if(cmbMonthName.getValue()!=null)
					{
						if(cmbBankName.getValue()!=null)
						{
							if(cmbBranchName.getValue()!=null)
							{
								if(txtAccountNo.getValue().toString().length()>0)
								{
									reportPreview();
								}
								else
								{
									showNotification("Warning", "Select Account No!!!", Notification.TYPE_WARNING_MESSAGE);
								}
							}
							else
							{
								showNotification("Warning","Select Branch Name!!!",Notification.TYPE_WARNING_MESSAGE);
							}
						}
						else
						{
							showNotification("Warning","Select Bank Name!!!",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else
					{
						showNotification("Warning","Select Month!!!",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					showNotification("Warning","Select Date!!!",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});
		opgMoneyTransferType.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				cmbMonthName.removeAllItems();
				cmbBankName.removeAllItems();
				cmbBranchName.removeAllItems();
				if(opgMoneyTransferType.getValue()!=null)
				{
					cmbMonthNameDataLoad();
					cmbBankNameAddData();
				}
			}
		});
		cmbBankName.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				cmbBranchName.removeAllItems();
				if(cmbBankName.getValue()!=null)
				{
					cmbBranchNameAddData();
				}
			}
		});
		cButton.btnExit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});
	}
	private void reportPreview()
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		String query="",rptName="",amount="",amountInWords="",account="",sqlEmpCheck="",emp="";

		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			if(opgReportType.getValue().toString().equals("Excel"))
			{
				
				String bankId="%";
				if(opgMoneyTransferType.getValue().toString().equals("Bank A/C"))
				{
					bankId=cmbBankName.getValue().toString();
				}
				
				String loc = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/temp/attendanceFolder";
				String fname = "Salary_Advice_List_"+cmbMonthName.getItemCaption(cmbMonthName.getValue())+".xls";
				String url = getWindow().getApplication().getURL()+"VAADIN/themes/temp/attendanceFolder/"+fname;
				String strColName[]={"SL#","ID#","Name","A/C Number","Bank Name","Branch Name","Routing No.","Amount"};
				String exelSql="";
				String signature[]={""};
			
				exelSql="select vEmployeeCode,vEmployeeName,vAccountNo,vBankName,vBranchName,vRoutingNo,CONVERT(float,CONVERT(Decimal(18,0),mNetPayableTaka))mNetPayableTaka from tbMonthlySalary "+
						" where MONTH(dSalaryDate)=MONTH('"+cmbMonthName.getValue()+"') and YEAR(dSalaryDate)=YEAR('"+cmbMonthName.getValue()+"') and vBankId like '%' "+ 
						" and vMoneyTransferType='"+opgMoneyTransferType.getValue().toString()+"'";
				System.out.println(exelSql);
				List <?> lst1=session.createSQLQuery(exelSql).list();				
				String detailQuery[]=new String[lst1.size()];
				String [] signatureOption = new String []{"Financial Administration Manager","Project Manager"};
				String [] groupItem=new String[]{};
				//Object [][] GroupElement=new Object[][];
				String [] GroupColName=new String[]{};
				int countInd=0;
					for(Iterator<?> iter=lst1.iterator(); iter.hasNext();)
					{
					 Object [] element = (Object[])iter.next();	
					 detailQuery[countInd]="select vEmployeeCode,vEmployeeName,vAccountNo,vBankName,vBranchName,vRoutingNo,CONVERT(float,CONVERT(Decimal(18,0),mNetPayableTaka))mNetPayableTaka from tbMonthlySalary "+
								" where MONTH(dSalaryDate)=MONTH('"+cmbMonthName.getValue()+"') and YEAR(dSalaryDate)=YEAR('"+cmbMonthName.getValue()+"') and vBankId like '%' "+ 
								" and vMoneyTransferType='"+opgMoneyTransferType.getValue().toString()+"'";
						
						countInd++;
					 
					}
					
					 
					 System.out.println("Details query :"+countInd);
						new GenerateExcelReport(sessionBean, loc, url, fname, "Salary Advice List "+cmbMonthName.getItemCaption(cmbMonthName.getValue()), "Salary Advice List "+cmbMonthName.getItemCaption(cmbMonthName.getValue()),
								"", strColName, 2, groupItem, GroupColName, null, 1, detailQuery, 0, 0, "A4",
								"Landscape",signatureOption,sessionBean.getCompany());
						Window window = new Window();
						getApplication().addWindow(window);
						getWindow().open(new ExternalResource(url),"_blank",500,200,Window.BORDER_NONE);
						
						System.out.println(4);
			
			}
			else
			{
				if(opgReportType.getValue().toString().equals("Forwarding Letter"))
				{
					System.out.println(2);
					query="select vBankId,CONVERT(Decimal(18,0),mAmount)mAmount,vAmountInWords,vAccountNo,"+
							" vTransferType,vSalaryOfMonth,iEmployee from funBankStatement('"+cmbMonthName.getValue().toString()+"',"+
							" '"+opgMoneyTransferType.getValue().toString()+"','"+cmbBankName.getValue()+"','"+txtAccountNo.getValue()+"')";
					System.out.println(query);
					List list1=session.createSQLQuery(query).list();
					if(!list1.isEmpty())
					{
						Iterator<?> iter=list1.iterator();
						while(iter.hasNext())
						{
							Object[] element=(Object[])iter.next();
							amount=element[1].toString();
							amountInWords=element[2].toString();
							if(opgMoneyTransferType.getValue().toString().equals("BFTN"))
							{
								emp=element[6].toString();
							}
						}
					}
					else
					{
						showNotification("Warning..","No data found!",Notification.TYPE_WARNING_MESSAGE);
					}
					System.out.println(2);
					if(opgMoneyTransferType.getValue().toString().equals("Bank A/C"))
					{
						rptName="rptBankForwardingLetter.jasper";
					}
					else if(opgMoneyTransferType.getValue().toString().equals("BFTN"))
					{
						rptName="rptBankForwardingLetterBFTN.jasper";
					}
					System.out.println(2);
				}
				else if(opgReportType.getValue().toString().equals("Encloser"))
				{
					String bankId="%";
					if(opgMoneyTransferType.getValue().toString().equals("Bank A/C"))
					{
						bankId=cmbBankName.getValue().toString();
					}
					query="select vEmployeeID,vEmployeeCode,vEmployeeName,vBankName,vBranchName,vRoutingNo,mNetPayableTaka,vAccountNo from tbMonthlySalary "+
					" where MONTH(dSalaryDate)=MONTH('"+cmbMonthName.getValue()+"') and YEAR(dSalaryDate)=YEAR('"+cmbMonthName.getValue()+"') and vBankId like '%' "+ 
					" and vMoneyTransferType='"+opgMoneyTransferType.getValue().toString()+"'";
					
					System.out.println(query);
					
					rptName="RptBankAdviceSalaryStatement.jasper";
					
				}
				
				System.out.println(5);
				if(queryValueCheck(query))
				{

					HashMap <String,Object> hm = new HashMap <String,Object> ();
					hm.put("company", sessionBean.getCompany());
					hm.put("address", sessionBean.getCompanyAddress());
					hm.put("phone", sessionBean.getCompanyContact());
					hm.put("developer", "Software Solution by : E-Vision Software Ltd.|| helpline : 01755-506044 || www.eslctg.com");
					hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
					hm.put("Date",dDayFormat.format(dDate.getValue())+"th "+dMonthFormat1.format(dDate.getValue())+", "+dYearFormat.format(dDate.getValue()));
					hm.put("month", sessionBean.dfMonth.format(cmbMonthName.getValue())+" , "+sessionBean.dfYear.format(cmbMonthName.getValue()));
					hm.put("dDate", sessionBean.dfMonth.format(cmbMonthName.getValue())+" , "+sessionBean.dfYear.format(cmbMonthName.getValue()));
					hm.put("BankName",cmbBankName.getItemCaption(cmbBankName.getValue()));
					hm.put("BranchName", cmbBranchName.getItemCaption(cmbBranchName.getValue()));
					hm.put("SysDate",reportTime.getTime);
					hm.put("AccountNo", txtAccountNo.getValue().toString().isEmpty()?" ":txtAccountNo.getValue().toString());
					hm.put("amount", amount);
					hm.put("InWords", amountInWords);
					hm.put("tlEmployee", emp);
					hm.put("logo", sessionBean.getCompanyLogo());
					hm.put("sql", query);
					
					System.out.println(6);
					Window win = new ReportViewer(hm,"report/account/hrmModule/"+rptName,
							this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
							this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
							this.getWindow().getApplication().getURL()+"VAADIN/applet",RadioBtn.Radio);

					win.setCaption("Project Report");
					this.getParent().getWindow().addWindow(win);

				}
				else
				{
					showNotification("Warning","There are no Data",Notification.TYPE_WARNING_MESSAGE);
				}
			}
			


		}
		catch(Exception exp)
		{
			this.getParent().showNotification("reportShow "+exp,Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private boolean queryValueCheck(String sql)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try 
		{
			List <?> lst = session.createSQLQuery(sql).list();
			if (!lst.isEmpty()) 
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

	private void focusMove()
	{
		allComp.add(dDate);
		allComp.add(cmbMonthName);
		allComp.add(cmbBankName);
		allComp.add(cmbBranchName);
		allComp.add(txtAccountNo);
		allComp.add(txtReferrenceNo);
		allComp.add(cButton.btnPreview);

		new FocusMoveByEnter(this,allComp);
	}

	private AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("480px");
		setHeight("380px");

		opgMoneyTransferType = new OptionGroup("",accountType);
		opgMoneyTransferType.setImmediate(true);
		opgMoneyTransferType.setStyleName("horizontal");
		opgMoneyTransferType.setValue("Bank A/C");
		mainLayout.addComponent(opgMoneyTransferType, "top:30.0px;left:130.0px;");

		opgReportType = new OptionGroup("",reportType);
		opgReportType.setImmediate(true);
		opgReportType.setStyleName("horizontal");
		opgReportType.setValue("Forwarding Letter");
		mainLayout.addComponent(opgReportType, "top:60.0px;left:130.0px;");

		dDate = new PopupDateField();
		dDate.setImmediate(true);
		dDate.setWidth("110px");
		dDate.setHeight("-1px");
		dDate.setDateFormat("dd-MM-yyyy");
		dDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dDate.setValue(new java.util.Date());
		mainLayout.addComponent(new Label("Date : "), "top:90.0px; left:30.0px;");
		mainLayout.addComponent(dDate, "top:88.0px; left:130.0px;");

		// dMonthYear
		/*dMonthYear = new PopupDateField();
		dMonthYear.setImmediate(true);
		dMonthYear.setWidth("140px");
		dMonthYear.setHeight("-1px");
		dMonthYear.setDateFormat("MMMMM-yyyy");
		dMonthYear.setResolution(PopupDateField.RESOLUTION_MONTH);
		dMonthYear.setValue(new java.util.Date());
		mainLayout.addComponent(new Label("Month : "), "top:120.0px; left:30.0px;");
		mainLayout.addComponent(dMonthYear, "top:118.0px; left:130.0px;");*/


		cmbMonthName=new ComboBox();
		cmbMonthName.setImmediate(true);
		cmbMonthName.setWidth("140px");
		cmbMonthName.setHeight("-1px");
		cmbMonthName.setNullSelectionAllowed(false);
		mainLayout.addComponent(new Label("Month : "), "top:120.0px; left:30.0px;");
		mainLayout.addComponent(cmbMonthName,"top:120px; left:130px");



		cmbBankName=new ComboBox();
		cmbBankName.setImmediate(true);
		cmbBankName.setWidth("220px");
		cmbBankName.setHeight("-1px");
		cmbBankName.setNullSelectionAllowed(false);
		mainLayout.addComponent(new Label("Bank Name : "), "top:150.0px; left:30.0px;");
		mainLayout.addComponent(cmbBankName, "top:148.0px; left:130.0px;");

		cmbBranchName=new ComboBox();
		cmbBranchName.setImmediate(true);
		cmbBranchName.setWidth("220px");
		cmbBranchName.setHeight("-1px");
		cmbBranchName.setNullSelectionAllowed(false);
		mainLayout.addComponent(new Label("Branch Name : "), "top:180.0px; left:30.0px;");
		mainLayout.addComponent(cmbBranchName, "top:178.0px; left:130.0px;");

		txtAccountNo=new TextField();
		txtAccountNo.setImmediate(true);
		txtAccountNo.setWidth("160px");
		txtAccountNo.setHeight("-1px");
		mainLayout.addComponent(new Label("Account No : "), "top:210.0px; left:30.0px;");
		mainLayout.addComponent(txtAccountNo, "top:208.0px; left:130.0px;");

		txtReferrenceNo=new TextField();
		txtReferrenceNo.setImmediate(true);
		txtReferrenceNo.setWidth("160px");
		txtReferrenceNo.setHeight("-1px");
		mainLayout.addComponent(new Label("Referrence No : "), "top:240.0px; left:30.0px;");
		mainLayout.addComponent(txtReferrenceNo, "top:238.0px; left:130.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:270.0px;left:130.0px;");
		RadioBtnGroup.setVisible(false);

		//mainLayout.addComponent(new Label("_________________________________________________________________________________________"), "top:290.0px;right:20.0px;left:20.0px;");		
		mainLayout.addComponent(cButton,"bottom:20px; left:140.0px");
		return mainLayout;
	}
}