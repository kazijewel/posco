package com.common.share;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

public class CommonMethod
{
	public SessionBean sessionBean;
	public SimpleDateFormat dfBd = new SimpleDateFormat("dd-MM-yyyy");
	public SimpleDateFormat dfDb = new SimpleDateFormat("yyyy-MM-dd");
	public SimpleDateFormat dfDbhms = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	public DecimalFormat deciInt = new DecimalFormat("#0");
	public DecimalFormat deciFloat = new DecimalFormat("#0.00");

	public boolean isSave = false, isEdit = false, isDelete = false, isPreview = false;

	public CommonMethod(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
	}

	public void checkFormAction(String menuId)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "SELECT iSave,iEdit,iDelete,iPreview FROM dbo.tbUserAccess where vUserId ="
					+ " '"+sessionBean.getUserId()+"' and vMenuId = '"+menuId+"'";
			System.out.println(sql);
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				if(Integer.parseInt(element[0].toString())==1)
				{isSave = true;}
				if(Integer.parseInt(element[1].toString())==1)
				{isEdit = true;}
				if(Integer.parseInt(element[2].toString())==1)
				{isDelete = true;}
				if(Integer.parseInt(element[3].toString())==1)
				{isPreview = true;}
			}
		}
		catch (Exception e)
		{
			System.out.println("Unable to get combo data"+e);
		}
		finally{session.close();};
	}

	public String fiscalYearIdTrans(Object VoucherDate)
	{
		String fiscalYearId = "";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			
			String sql = "Select vFiscalYearIdTrans from [dbo].[funFiscalYearInfo]('"+dfDb.format(VoucherDate)+"',"
					+ " '"+dfDb.format(VoucherDate)+"', '"+sessionBean.getCompanyId()+"')";
			Iterator<?> iter = session.createSQLQuery(sql).list().iterator();
			System.out.println("Fiscal Year:"+sql);
			if(iter.hasNext())
			{
				fiscalYearId = iter.next().toString();
			}
		}
		catch (Exception e)
		{
			System.out.println(e+" Common method 1");
		}
		finally{session.close();}
		return fiscalYearId;
	}

	//to get fiscal year id using from date for reports/finding
	public String fiscalYearIdReport(Object VoucherDate)
	{
		String fiscalYearId = "";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "Select vFiscalYearIdReport from [dbo].[funFiscalYearInfo]('"+dfDb.format(VoucherDate)+"',"
					+ " '"+dfDb.format(VoucherDate)+"', '"+sessionBean.getCompanyId()+"')";
			Iterator<?> iter = session.createSQLQuery(sql).list().iterator();
			if(iter.hasNext())
			{
				fiscalYearId = iter.next().toString();
			}
		}
		catch (Exception e)
		{
			System.out.println(e+" Common method 1");
		}
		finally{session.close();}
		return fiscalYearId;
	}

	//to check fiscal year id between from date and to date
	public String checkFindDate(Object FromDate, Object ToDate)
	{
		String chechYearId = "";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "";
			Iterator<?> iter = session.createSQLQuery(sql).list().iterator();
			if(iter.hasNext())
			{
				chechYearId = iter.next().toString();
			}
		}
		catch (Exception e)
		{
			System.out.println(e+" Common method 1");
		}
		finally{session.close();}
		return chechYearId;
	}

	//to check fiscal year is close or not
	public String checkYearClosed(Object Date)
	{
		String isClosed = "";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "";
			System.out.println(sql);
			Iterator<?> iter = session.createSQLQuery(sql).list().iterator();
			if(iter.hasNext())
			{
				isClosed = iter.next().toString();
			}
		}
		catch (Exception e)
		{
			System.out.println(e+" Common method 1");
		}
		finally{session.close();}
		return isClosed;
	}

	public String voucherNo(String voucherType, String voucherPrefix, Object voucherDate)
	{
		String getVoucherNo = "";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "";
			Iterator<?> iter = session.createSQLQuery(query).list().iterator();
			if(iter.hasNext())
			{
				getVoucherNo = voucherPrefix+iter.next().toString();
			}
		}
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		finally{session.close();}

		return getVoucherNo;
	}

	public String MasterNo()
	{
		String masterNo = "";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = " Select isnull(max(cast(SUBSTRING(vMasterNo,3,50) as int)),0)+1 from dbo.tbVoucher ";
			Iterator<?> iter = session.createSQLQuery(query).list().iterator();
			if(iter.hasNext())
			{
				masterNo = "V-"+iter.next().toString();
			}
		}
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		finally{session.close();}
		return masterNo;
	}

	public Object DateTime()
	{
		Object datetime = "";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = " Select CURRENT_TIMESTAMP ";
			datetime = session.createSQLQuery(query).list().iterator().next();
		}
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		finally{session.close();}
		return datetime;
	}
}