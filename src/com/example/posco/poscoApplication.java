package com.example.posco;

import java.io.File;
import java.net.URL;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import com.common.share.LogIn;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.Application;
import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.URIHandler;
import com.vaadin.terminal.gwt.server.HttpServletRequestListener;
import com.vaadin.ui.Window;

public class poscoApplication extends Application implements HttpServletRequestListener
{
	private static final long serialVersionUID = 1L;
	String tempParameter;
	SessionBean sessionBean;
	File a;
	URL b;
	int flag = 1,i=0;
	public String contextname;
	URL context;

	
	public void init()
	{
		sessionBean = new SessionBean();
		sessionBean.setUserId("1");
		sessionBean.setCompanyId("1");
		sessionBean.setCompany("POSCO E&C CO. LTD.");
		
		dataInitialise();

		setMainWindow(new MainWindow());

		URIHandler uriHandler = new URIHandler()
		{
			public DownloadStream handleURI(URL context,String relativeUri)
			{
				return null;
			}
		};

		context=this.getURL();
		contextname =context.toString().substring(context.toString().indexOf("/",7)+1,context.toString().length()-1);
		System.out.println("url print "+context+"\t"+contextname);

		sessionBean.setContextName(contextname);
		getMainWindow().addURIHandler(uriHandler);

		 setTheme("poscoactheme");
		//setTheme(Runo.themeName());
		
	}

	private void dataInitialise()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "SELECT isnull(imageLoc,0),0 FROM tbCompanyInfo where companyId = '"+ sessionBean.getCompanyId() +"'";
			Iterator  iter = session.createSQLQuery(sql).list().iterator();
			if(iter.hasNext())
			{
				Object[] element = (Object[]) iter.next();
				sessionBean.setCompanyLogo(element[0].toString());
			}
		}
		catch(Exception exp)
		{
			//showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	/*public Window getWindow(String name)
	{
		Window w = super.getWindow(name);

		if (w == null)
		{
			w = new MainWindow();

			//System.out.println(name);
			w.setName(name);
			addWindow(w);
		}
		return w;
	}*/

	private class MainWindow extends Window  
	{
		MainWindow() 
		{
			this.setCaption("POSCO E&C CO. LTD.");		
			this.setStyleName("backcolor");

			poolStart();

			LogIn rm = new LogIn(sessionBean);
			this.getWindow().addWindow(rm);
			getWindow().addWindow(rm);
			
			

			if(sessionBean.getWar() == "1")
			{
				this.setCaption("POSCO E&C CO. LTD.");
			}

			//System.out.println(tempParameter+"haa");

			/*if( !tempParameter.equalsIgnoreCase("/UIDL") && tempParameter.trim().length()>3)
			{
				System.out.println(tempParameter+"ha");

				try
				{
					String query;
					HashMap hm = new HashMap();

					hm.put("clDate",sessionBean.getasOnDate());
					hm.put("comName",sessionBean.getCompanyName());
					hm.put("address", sessionBean.getCompanyAddress());
					hm.put("phoneFax",sessionBean.getCompanyContact());

					hm.put("userName", sessionBean.getUserName());
					hm.put("userIp", sessionBean.getUserIp());

					hm.put("url", sessionBean.getUrl()+"");
					System.out.println(tempParameter.substring(0, tempParameter.indexOf("="))+" full Parameter:"+tempParameter.substring(tempParameter.indexOf("=")+1,tempParameter.length()));
					System.out.println(tempParameter);

					if(tempParameter.substring(0, tempParameter.indexOf("=")).equalsIgnoreCase("/voucher"))
					{
						String voucher=tempParameter.substring(tempParameter.indexOf("=")+1,14);

						System.out.println(voucher);

						if(voucher.matches("JV-NO"))
						{
							System.out.println(voucher);

							String	sql = "SELECT * FROM vwJournalVoucher WHERE  Voucher_No in('"+tempParameter.substring(tempParameter.indexOf("=")+1,tempParameter.length())+"') And companyId = '"+ sessionBean.getCompanyId() +"' and company_Id = '"+ sessionBean.getCompanyId() +"' ORDER BY CAST(substring(VOucher_No,7,50) as int),DrAmount DESC";
							System.out.println(sql);

							hm.put("sql",sql);
							Window win;
							win=new ReportViewer(hm,"report/account/voucher/JournalVoucherWithoutLink.jasper",
									sessionBean.getP()+"".replace("\\","/")+"/VAADIN/rpttmp",
									sessionBean.getUrl()+"VAADIN/rpttmp",false,
									sessionBean.getUrl()+"VAADIN/applet",true);
							getWindow().addWindow(win);

							win.setCaption("JOURNAL VOUCHER :: "+sessionBean.getCompanyName());
							win.setClosable(false);
						}

						if(voucher.matches("CV-NO"))
						{
							System.out.println(voucher);

							String sql = "select * from [rptVoucherAll]('"+tempParameter.substring(tempParameter.indexOf("=")+1,tempParameter.length())+"','2015-07-01','"+sessionBean.getCompanyId()+"') order by VoucherDate desc";
							System.out.println(sql);

							hm.put("sql",sql);
							Window win;
							win=new ReportViewer(hm,"report/account/voucher/ContraVoucher.jasper",
									sessionBean.getP()+"".replace("\\","/")+"/VAADIN/rpttmp",
									sessionBean.getUrl()+"VAADIN/rpttmp",false,
									sessionBean.getUrl()+"VAADIN/applet",true);
							getWindow().addWindow(win);

							win.setCaption("CONTRA VOUCHER :: "+sessionBean.getCompanyName());
							win.setClosable(false);
						}

						else if(voucher.matches("AP-CR"))
						{
							System.out.println(voucher);

							String sql = "Select * from vwAssetPurchaseVoucher WHERE (VoucherNo IN ('"+tempParameter.substring(tempParameter.indexOf("=")+1,tempParameter.length())+"')) AND (companyId ='"+ sessionBean.getCompanyId() +"') AND (company_Id ='"+ sessionBean.getCompanyId() +"')";
							System.out.println(sql);

							hm.put("sql",sql);
							Window win;
							win=new ReportViewer(hm,"report/account/voucher/FixedAssetVoucherWithoutLink.jasper",
									sessionBean.getP()+"".replace("\\","/")+"/VAADIN/rpttmp",
									sessionBean.getUrl()+"VAADIN/rpttmp",false,
									sessionBean.getUrl()+"VAADIN/applet",true);
							getWindow().addWindow(win);

							win.setCaption("FIXED ASSET VOUCHER :: "+sessionBean.getCompanyName());
							win.setClosable(false);
						}

						if(voucher.matches("ASDEP"))
						{
							System.out.println(voucher);

							String	sql = "Select * from vwDepreciationVoucher WHERE (Voucher_No IN ('"+tempParameter.substring(tempParameter.indexOf("=")+1,tempParameter.length())+"')) AND (companyId ='"+ sessionBean.getCompanyId() +"') AND (company_Id ='"+ sessionBean.getCompanyId() +"')";
							System.out.println(sql);

							hm.put("sql",sql);
							Window win;
							win=new ReportViewer(hm,"report/account/voucher/DepriciationVoucher.jasper",
									sessionBean.getP()+"".replace("\\","/")+"/VAADIN/rpttmp",
									sessionBean.getUrl()+"VAADIN/rpttmp",false,
									sessionBean.getUrl()+"VAADIN/applet",true);
							getWindow().addWindow(win);

							win.setCaption("FIXED ASSET VOUCHER :: "+sessionBean.getCompanyName());
							win.setClosable(false);
						}

						if(voucher.matches("DR-CH"))
						{
							Session session = SessionFactoryUtil.getInstance().openSession();
							Transaction tx = session.beginTransaction();
							Window win;
							hm.put("url", sessionBean.getUrl()+"");

							System.out.println(sessionBean.getUrl());
							System.out.println(voucher);

							String sql = "SELECT * FROM vwCashVoucher WHERE Voucher_No IN ('"+tempParameter.substring(tempParameter.indexOf("=")+1,tempParameter.length())+"')  AND companyId = '"+ sessionBean.getCompanyId() +"' AND (company_Id = '"+ sessionBean.getCompanyId() +"') order by DrAmount desc";
							//String sql = "select * from [rptVoucherAll]('"+tempParameter.substring(tempParameter.indexOf("=")+1,tempParameter.length())+"','2015-07-01','"+sessionBean.getCompanyId()+"') order by VoucherDate desc";
							System.out.println(sql);
							hm.put("sql",sql);

							int voucherCount = 1;

							Iterator iter = session.createSQLQuery(" select COUNT(Voucher_No) from vwVoucher where Voucher_No = '"+tempParameter.substring(tempParameter.indexOf("=")+1,tempParameter.length())+"' ").list().iterator();
							if(iter.hasNext())
								voucherCount = Integer.valueOf(iter.next().toString());

							if(voucherCount>2)
							{
								String sql = "SELECT * FROM vwCashVoucher WHERE Voucher_No IN ('"+tempParameter.substring(tempParameter.indexOf("=")+1,tempParameter.length())+"')  AND companyId = '"+ sessionBean.getCompanyId() +"' AND (company_Id = '"+ sessionBean.getCompanyId() +"') order by DrAmount desc";

								System.out.println(sql);
								hm.put("sql",sql);
								win=new ReportViewer(hm,"report/account/voucher/CashPaymentVoucher.jasper",//CashPaymentVoucherWithoutLink.jasper",
										sessionBean.getP()+"".replace("\\","/")+"/VAADIN/rpttmp",
										sessionBean.getUrl()+"VAADIN/rpttmp",false,
										sessionBean.getUrl()+"VAADIN/applet",true);
							}
							else
							{
								String sql = "select * from [rptVoucherAll]('"+tempParameter.substring(tempParameter.indexOf("=")+1,tempParameter.length())+"','2015-07-01','"+sessionBean.getCompanyId()+"') order by VoucherDate desc";

								System.out.println(sql);
								hm.put("sql",sql);

								win=new ReportViewer(hm,"report/account/voucher/rptVoucherMZMCash.jasper",//CashPaymentVoucherWithoutLink.jasper",
										sessionBean.getP()+"".replace("\\","/")+"/VAADIN/rpttmp",
										sessionBean.getUrl()+"VAADIN/rpttmp",false,
										sessionBean.getUrl()+"VAADIN/applet",true);
							}
							getWindow().addWindow(win);

							win.setCaption("CASH PAYMENT VOUCHER :: "+sessionBean.getCompanyName());
							win.setClosable(false);
						}

						if(voucher.matches("CR-CH"))
						{
							System.out.println(voucher);

							//String sql = "SELECT * FROM vwCashVoucher WHERE Voucher_No IN ('"+tempParameter.substring(tempParameter.indexOf("=")+1,tempParameter.length())+"')  AND companyId = '"+ sessionBean.getCompanyId() +"' AND (company_Id = '"+ sessionBean.getCompanyId() +"') order by CrAmount desc";
							String sql = "select * from [rptVoucherAll]('"+tempParameter.substring(tempParameter.indexOf("=")+1,tempParameter.length())+"','2015-07-01','"+sessionBean.getCompanyId()+"') order by VoucherDate desc";

							System.out.println(sql);
							hm.put("sql",sql);

							Window win;

							win=new ReportViewer(hm,"report/account/voucher/rptVoucherMZMCashCredit.jasper",//CashReceipttVoucherLink.jasper",
									sessionBean.getP()+"".replace("\\","/")+"/VAADIN/rpttmp",
									sessionBean.getUrl()+"VAADIN/rpttmp",false,
									sessionBean.getUrl()+"VAADIN/applet",true);

							getWindow().addWindow(win);

							win.setCaption("CASH RECEIPT VOUCHER :: "+sessionBean.getCompanyName());
							win.setClosable(false);
						}

						if(voucher.matches("DR-BK"))
						{
							Session session = SessionFactoryUtil.getInstance().openSession();
							Transaction tx = session.beginTransaction();
							Window win;
							String sql = "";
							String sql = "SELECT * from vwBankVoucher WHERE Voucher_No in('"+tempParameter.substring(tempParameter.indexOf("=")+1,tempParameter.length())+"') AND companyId = '"+ sessionBean.getCompanyId() +"' ORDER BY CAST(substring(VOucher_No,7,50) as int),CrAmount DESC";
							//String sql = "select * from [rptVoucherAll]('"+tempParameter.substring(tempParameter.indexOf("=")+1,tempParameter.length())+"','2015-07-01','"+sessionBean.getCompanyId()+"') order by VoucherDate desc";

							int voucherCount = 1;

							Iterator iter = session.createSQLQuery(" select COUNT(Voucher_No) from vwVoucher where Voucher_No = '"+tempParameter.substring(tempParameter.indexOf("=")+1,tempParameter.length())+"' ").list().iterator();
							if(iter.hasNext())
								voucherCount = Integer.valueOf(iter.next().toString());

							if(voucherCount>2)
							{
								sql = "SELECT * from vwBankVoucher WHERE Voucher_No in('"+tempParameter.substring(tempParameter.indexOf("=")+1,tempParameter.length())+"') AND companyId = '"+ sessionBean.getCompanyId() +"' ORDER BY CAST(substring(VOucher_No,7,50) as int),CrAmount ASC";
								System.out.println(sql);
								hm.put("sql",sql);

								win = new ReportViewer(hm,"report/account/voucher/BankPaymentVoucher.jasper",
										sessionBean.getP()+"".replace("\\","/")+"/VAADIN/rpttmp",
										sessionBean.getUrl()+"VAADIN/rpttmp",false,
										sessionBean.getUrl()+"VAADIN/applet",true);
							}
							else
							{
								sql = "select * from [rptVoucherAll]('"+tempParameter.substring(tempParameter.indexOf("=")+1,tempParameter.length())+"','2015-07-01','"+sessionBean.getCompanyId()+"') order by VoucherDate desc";
								System.out.println(sql);
								hm.put("sql",sql);

								win = new ReportViewer(hm,"report/account/voucher/rptVoucherMZMBank.jasper",
										sessionBean.getP()+"".replace("\\","/")+"/VAADIN/rpttmp",
										sessionBean.getUrl()+"VAADIN/rpttmp",false,
										sessionBean.getUrl()+"VAADIN/applet",true);
							}

							getWindow().addWindow(win);

							win.setCaption("BANK PAYMENT VOUCHER :: "+sessionBean.getCompanyName());
							win.setClosable(false);
						}

						if(voucher.matches("CR-BK"))
						{
							//String sql = "SELECT * from vwBankVoucher WHERE Voucher_No in('"+tempParameter.substring(tempParameter.indexOf("=")+1,tempParameter.length())+"') AND companyId = '"+ sessionBean.getCompanyId() +"' ORDER BY CAST(substring(VOucher_No,7,50) as int),CrAmount DESC";

							String sql = "select * from [rptVoucherAll]('"+tempParameter.substring(tempParameter.indexOf("=")+1,tempParameter.length())+"','2015-07-01','"+sessionBean.getCompanyId()+"') order by VoucherDate desc";

							System.out.println(sql);
							hm.put("sql",sql);

							Window win;

							win=new ReportViewer(hm,"report/account/voucher/rptVoucherMZMBankCredit.jasper",
									sessionBean.getP()+"".replace("\\","/")+"/VAADIN/rpttmp",
									sessionBean.getUrl()+"VAADIN/rpttmp",false,
									sessionBean.getUrl()+"VAADIN/applet",true);

							getWindow().addWindow(win);

							win.setCaption("BANK RECEIVE VOUCHER :: "+sessionBean.getCompanyName());
							win.setClosable(false);
						}
					}

					Window win = null;

					if(tempParameter.substring(0, tempParameter.indexOf("=")).equalsIgnoreCase("/head"))
					{
						String sql="";
						if((tempParameter.substring(tempParameter.indexOf("=")+1,tempParameter.length())).toString().equals("A7"))
						{
							sql="SELECT * FROM dbo.funBalanceSheetDetails('"+new SimpleDateFormat("yyyy/MM/dd").format(sessionBean.getasOnDate()) +"', '"+sessionBean.getCompanyId()+"' )" +
									"where headId in ('"+tempParameter.substring(tempParameter.indexOf("=")+1,tempParameter.length())+"','A8') " +
									"order by Notes,SubGroupName,LedgerId";
						}
						else
						{
							sql="SELECT * FROM dbo.funBalanceSheetDetails('"+new SimpleDateFormat("yyyy/MM/dd").format(sessionBean.getasOnDate()) +"', '"+sessionBean.getCompanyId()+"' )" +
									"where headId='"+tempParameter.substring(tempParameter.indexOf("=")+1,tempParameter.length())+"' " +
									"order by Notes,SubGroupName,LedgerId";
						}

						hm.put("sql",sql);

						System.out.println(sql);

						System.out.println(	sessionBean.getP());

						win=new ReportViewer(hm,"report/account/balancesheet/balancesheetDetails(backnavigation).jasper",
								sessionBean.getP()+"".replace("\\","/")+"/VAADIN/rpttmp",
								sessionBean.getUrl()+"VAADIN/rpttmp",false,
								sessionBean.getUrl()+"VAADIN/applet",true);

						win.setCaption("Project Report");
						getWindow().addWindow(win);
						win.setClosable(false);
					}

					if(tempParameter.substring(0, tempParameter.indexOf("=")).equalsIgnoreCase("/headId"))
					{
						String sql = "select * from dbo.profitLossDetail('"+new SimpleDateFormat("yyyy/MM/dd").format(sessionBean.getFromDate())+"','"+new SimpleDateFormat("yyyy/MM/dd").format(sessionBean.getasOnDate())+"','"+sessionBean.getCompanyId()+"')  where substring(create_from,1,2) in ("+tempParameter.substring(tempParameter.indexOf("=")+1,tempParameter.length())+") order by isnull(NOTES,0),mainhead desc,PrimaryGroup ASC,MainGroup ASC,SubGroup ASC";
						hm.put("sql",sql);

						System.out.println(sql);
						hm.put("fromDate", sessionBean.getFromDate());
						hm.put("toDate", sessionBean.getasOnDate());

						System.out.println(	sessionBean.getP());

						win=new ReportViewer(hm,"report/account/profitloss/profitLossDetailBackNavigation.jasper",
								sessionBean.getP()+"".replace("\\","/")+"/VAADIN/rpttmp",
								sessionBean.getUrl()+"VAADIN/rpttmp",false,
								sessionBean.getUrl()+"VAADIN/applet",true);

						win.setCaption("PROFIT & LOSS STATEMENT(DETAILS) :: "+sessionBean.getCompanyName());
						win.setClosable(false);
						getWindow().addWindow(win);
					}

					if(tempParameter.substring(0, tempParameter.indexOf("=")).equalsIgnoreCase("/ledger"))
					{
						String msg = "";
						Session session = SessionFactoryUtil.getInstance().openSession();
						Transaction tx = session.beginTransaction();

						String tempLedger = tempParameter.substring(tempParameter.indexOf("=")+1,tempParameter.length());

						String fsl = session.createSQLQuery("Select [dbo].[VoucherSelect]('"+new SimpleDateFormat("yyyy-MM-dd").format( sessionBean.getasOnDate())+"')").list().iterator().next().toString();

						session.createSQLQuery("exec prcAlterVoucher " + fsl +"").executeUpdate();
						Date dt = (Date) session.createSQLQuery("Select op_date  from tbFiscal_Year where slNo = "+fsl+"").list().iterator().next();

						String ledgerName = " select * from tbLedger where Ledger_Id='"+tempLedger+"'";
						Iterator iterLedger=session.createSQLQuery(ledgerName).list().iterator();

						if(iterLedger.hasNext())
						{
							Object[] element = (Object[]) iterLedger.next();
							hm.put("ledgerName", element[1]);
						}	

						Iterator iter = session.createQuery("SELECT substring(id.r,1,1),id.h+isnull('\\'+id.g,'')+" +
								" isnull('\\'+id.s,'')+'\\'+id.l FROM VwLedgerList WHERE " +
								" id.ledgerId = '"+tempLedger+"'").list().iterator();

						Object[] element = (Object[]) iter.next();
						if(element[0].toString().equalsIgnoreCase("A"))
							msg = "Assets\\"+element[1];
						else if(element[0].toString().equalsIgnoreCase("I"))
							msg = "Income\\"+element[1];
						else if(element[0].toString().equalsIgnoreCase("E"))
							msg = "Expenses\\"+element[1];
						else 
							msg = "Liabilities\\"+element[1];

						tx.commit();

						hm.put("ledgerPath", msg);

						hm.put("fromTo",new SimpleDateFormat("dd-MM-yyyy").format( dt)+" To "+new SimpleDateFormat("dd-MM-yyyy").format(sessionBean.getasOnDate()));

						String sql = "SELECT * FROM dbo.rptCostLedger('"+dt+"','"+new SimpleDateFormat("yyyy-MM-dd").format( sessionBean.getasOnDate())+"','"+tempLedger+"','U-3','"+sessionBean.getCompanyId()+"') order by date,convert(Numeric,autoid)";
						hm.put("sql",sql);

						System.out.println(sql);

						win = new ReportViewer(hm,"report/account/book/GeneralLedger(backnavigation).jasper",
								sessionBean.getP()+"".replace("\\","/")+"/VAADIN/rpttmp",
								sessionBean.getUrl()+"VAADIN/rpttmp",false,
								sessionBean.getUrl()+"VAADIN/applet",true);

						win.setCaption("Project Report");
						win.setClosable(false);

						getWindow().addWindow(win);
					}
				}
				catch(Exception e)
				{
					System.out.println(e);
				}

				i=1;
			}*/
		}
	}

	public void poolStart()
	{
		try
		{
			class PoolStart extends Thread
			{
				public void run()
				{
					Session session = SessionFactoryUtil.getInstance().openSession();
					//Transaction tx = session.beginTransaction();
					Iterator  iter = session.createSQLQuery("SELECT Op_Date, Cl_Date,SlNo FROM tbFiscal_Year where Running_Flag = 1").list().iterator();

					if(iter.hasNext())
					{
						Object[] element = (Object[]) iter.next();
						System.out.println(element[0].toString());
						sessionBean.setFiscalOpenDate(element[0]);
						sessionBean.setFiscalCloseDate(element[1]);
						sessionBean.setFiscalRunningSerial(element[2].toString());
					}
					//tx.commit();
				}
			};
			new PoolStart().start(); 
		}
		catch(Exception ex)
		{

		}
	}

	public void onRequestStart(HttpServletRequest request,HttpServletResponse response)
	{
		tempParameter=request.getPathInfo().toString();
	}

	public void onRequestEnd(HttpServletRequest request,HttpServletResponse response)
	{
		// TODO Auto-generated method stub
	}

	public String tbVoucherName(String date)
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+date+"')").list().iterator().next().toString();
		String voucher =  "voucher"+fsl;
		
		return voucher;
	}


	public boolean isClosedFiscal(String date)
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();

		String flag =	session.createSQLQuery("Select isClosed from tbFiscal_Year where '"+date+"' between op_date and cl_date").list().iterator().next().toString();
		System.out.println(flag);
		if (flag.toString().equals("true"))
		return true;
		else
			return false;
	}
	
}