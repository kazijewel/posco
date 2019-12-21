package hrm.common.reportform;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.ReportDate;
import com.common.share.ReportOption;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

public class RptEmployeeListShiftWise extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	ArrayList<Component> allComp = new ArrayList<Component>();

	private Label lblShift;
	private ComboBox cmbShift;
	private CheckBox ShiftAll;

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});

	public RptEmployeeListShiftWise(SessionBean sessionBean) 
	{
		this.sessionBean=sessionBean;
		this.setCaption("Employee List(Shift Wise) :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);

		addSectionName();
		setEventAction();
		focusMove();
	}

	public void setEventAction()
	{
		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				String section = "";
				if(cmbShift.getValue()!=null || ShiftAll.booleanValue()==true)
				{
					if(ShiftAll.booleanValue()==true)
					{section = "%";}
					else
					{section = cmbShift.getValue().toString();}

					reportShow(section);
				}
				else
				{
					showNotification("Warning","Select Shift",Notification.TYPE_WARNING_MESSAGE);
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

		ShiftAll.addListener(new Listener()
		{
			public void componentEvent(Event event)
			{
				if(ShiftAll.booleanValue()==true)
				{
					cmbShift.setValue(null);
					cmbShift.setEnabled(false);
				}
				else
				{
					cmbShift.setEnabled(true);
				}
			}
		});
	}

	public void addSectionName()
	{
		cmbShift.removeAllItems();
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			List list = session.createSQLQuery(" select vShiftId,vShiftName from tbGroupShiftInfo order by vShiftId ").list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbShift.addItem(element[0]);
				cmbShift.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void reportShow(Object shiftName)
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		String query=null;
		String activeFlag = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			HashMap hm = new HashMap();
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			//			hm.put("phone", sessionBean.getCompanyPhone());
			//			hm.put("email", sessionBean.getCompanyEmail());
			//			hm.put("fax", sessionBean.getCompanyFax());
			hm.put("username", sessionBean.getUserName()+" "+sessionBean.getUserIp());
			//			hm.put("userIp", sessionBean.getUserIp());
			hm.put("SysDate",reportTime.getTime);
			hm.put("logo", sessionBean.getCompanyLogo());

			query = "SELECT b.vShiftName,a.employeeCode,a.vEmployeeName,c.designationName,a.dJoiningDate,a.accountNo," +
					"a.mMonthlySalary,a.mHouseRent,a.mMedicalAllowance,a.mConAllowance,a.mProvidentFund,a.mKFund," +
					"a.mOthersAllowance,a.mSpecial,(a.mMonthlySalary+a.mHouseRent+a.mMedicalAllowance+a.mConAllowance+ " +
					"a.mProvidentFund+a.mKFund+a.mOthersAllowance+a.mSpecial) as totalSalary from tbEmployeeInfo as a inner " +
					"join tbGroupShiftInfo as b on a.vFloor=b.vGroupId inner join tbDesignationInfo as c on " +
					"a.vDesignationId=c.designationId where b.vShiftId like '"+shiftName+"' and iStatus='1' Group by b.vShiftName,a.employeeCode," +
					"a.vEmployeeName,c.designationName,a.dJoiningDate,a.accountNo,a.mMonthlySalary,a.mHouseRent," +
					"a.mMedicalAllowance,a.mConAllowance,a.mProvidentFund,a.mKFund,mSpecial,a.mOthersAllowance ";
			
			if(queryValueCheck(query))
			{
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/account/hrmModule/RptShiftWiseEmployee.jasper",
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
		catch(Exception exp)
		{
			this.getParent().showNotification("Error "+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private boolean queryValueCheck(String sql)
	{
		Transaction tx = null;

		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			Iterator iter = session.createSQLQuery(sql).list().iterator();

			if (iter.hasNext()) 
			{
				return true;
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		return false;
	}

	private void focusMove()
	{
		allComp.add(cmbShift);
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
		setWidth("400px");
		setHeight("200px");

		// lblCategory
		lblShift = new Label();
		lblShift.setImmediate(false);
		lblShift.setWidth("100.0%");
		lblShift.setHeight("-1px");
		lblShift.setValue("Shift Name :");
		mainLayout.addComponent(lblShift,"top:30.0px; left:30.0px;");

		// cmbShift
		cmbShift = new ComboBox();
		cmbShift.setImmediate(false);
		cmbShift.setWidth("200px");
		cmbShift.setHeight("-1px");
		cmbShift.setNullSelectionAllowed(true);
		cmbShift.setImmediate(true);
		mainLayout.addComponent(cmbShift, "top:28.0px; left:130.0px;");

		//ShiftAll
		ShiftAll = new CheckBox("All");
		ShiftAll.setHeight("-1px");
		ShiftAll.setWidth("-1px");
		ShiftAll.setImmediate(true);
		mainLayout.addComponent(ShiftAll, "top:30.0px; left:336.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:70.0px;left:130.0px;");

		mainLayout.addComponent(cButton,"top:100.opx; left:120.0px");

		return mainLayout;
	}
}
