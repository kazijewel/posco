package hrm.common.reportform;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

import com.common.share.CommonButton;
import com.common.share.CommonMethod;
import com.common.share.FocusMoveByEnter;
import com.common.share.ReportDate;
import com.common.share.ReportOption;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class RptIndividualOt extends Window
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblMonth;
	private PopupDateField dMonth;

	private Label lblEmployee;
	private ComboBox cmbEmployee;

	private Label lblSection;
	private ComboBox cmbSection;

	ArrayList<Component> allComp = new ArrayList<Component>();
	private List <?> lstOPG = Arrays.asList(new String [] {"Employee ID","Finger ID","Employee Name"});
	private OptionGroup opgEmployee;

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");

	private SimpleDateFormat datemonthformat = new SimpleDateFormat("MMMMM-yy");
	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});

	private ReportDate reportTime = new ReportDate();
	private CommonMethod cm;
	private String menuId = "";
	public RptIndividualOt(SessionBean sessionBean,String menuId)
	{
		this.sessionBean=sessionBean;
		this.setCaption("INDIVIDUAL OT STATEMENT :: "+sessionBean.getCompany());
		this.setResizable(false);
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		
		buildMainLayout();
		focusMove();
		cmbSectionAddData();
		setContent(mainLayout);
		setEventAction();
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
	public void setEventAction()
	{
		dMonth.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSection.removeAllItems();
				if(dMonth.getValue() != null)
				{
					cmbSectionAddData();
				}
			}
		});

		cmbSection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmployee.removeAllItems();
				if(cmbSection.getValue()!=null)
				{
					cmbEmployeeAddData();
				}
			}
		});
		
		opgEmployee.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmployee.removeAllItems();
				cmbEmployeeAddData();
			}
		});

		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbEmployee.getValue()!=null)
				{
					reportShow();
				}
				else
				{
					showNotification("Warniung!","Select Employee Name",Notification.TYPE_WARNING_MESSAGE);
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

	public void cmbSectionAddData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select distinct vSectionID,vSectionName from tbEmployeeAttendanceFinal where bOtStatus='1' order by vSectionName";
			System.out.println("OH"+query);
			List <?> list=session.createSQLQuery(query).list();

			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{

				Object[] element = (Object[]) iter.next();
				cmbSection.addItem(element[0]);
				cmbSection.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("cmbSectionAddData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void cmbEmployeeAddData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select distinct vEmployeeID,vEmployeeCode,vFingerId,vEmployeeName from tbEmployeeAttendanceFinal where bOtStatus='1' and vSectionId='"+cmbSection.getValue()+"'";
			List <?> list=session.createSQLQuery(query).list();

			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				if(opgEmployee.getValue()=="Employee ID")
				{
					lblEmployee.setValue("Employee ID :");
					cmbEmployee.addItem(element[0]);
					cmbEmployee.setItemCaption(element[0], element[1].toString());
				}
				else if(opgEmployee.getValue()=="Finger ID")
				{
					lblEmployee.setValue("Finger ID :");
					cmbEmployee.addItem(element[0]);
					cmbEmployee.setItemCaption(element[0], element[2].toString());
				}
				else if(opgEmployee.getValue()=="Employee Name")
				{
					lblEmployee.setValue("Employee Name :");
					cmbEmployee.addItem(element[0]);
					cmbEmployee.setItemCaption(element[0], element[3].toString());
				}
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("cmbEmployeeAddData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void reportShow()
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		try
		{
			String query= "select * from funIndividualOtStatement('"+dateformat.format(dMonth.getValue())+"','"+cmbEmployee.getValue()+"')";
			
			System.out.println("OH"+query);


			if(queryValueCheck(query))
			{
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
				hm.put("month",datemonthformat.format(dMonth.getValue()));
				hm.put("SysDate",reportTime.getTime);
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"hrm/jasper/OT/rptIndividualOt.jasper",
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
		{this.getParent().showNotification("Error "+exp,Notification.TYPE_ERROR_MESSAGE);}
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
			showNotification("queryValueCheck", ex.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
		return false;
	}

	private void focusMove()
	{
		allComp.add(dMonth);
		allComp.add(cmbSection);
		allComp.add(cmbEmployee);
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
		setWidth("450px");
		setHeight("250px");

		// lblMonth
		lblMonth = new Label("Month :");
		lblMonth.setImmediate(false);
		lblMonth.setWidth("100.0%");
		lblMonth.setHeight("-1px");
		mainLayout.addComponent(lblMonth,"top:10.0px; left:30.0px;");

		// dMonth
		dMonth = new PopupDateField();
		dMonth.setImmediate(true);
		dMonth.setWidth("150px");
		dMonth.setHeight("-1px");
		dMonth.setDateFormat("MMMMM-yyyy");
		dMonth.setValue(new java.util.Date());
		dMonth.setResolution(PopupDateField.RESOLUTION_MONTH);
		mainLayout.addComponent(dMonth, "top:08.0px; left:140.0px;");

		// lblSection
		lblSection = new Label("Section Name :");
		lblSection.setImmediate(false);
		lblSection.setWidth("100.0%");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection,"top:40.0px; left:30.0px;");

		// cmbSection
		cmbSection = new ComboBox();
		cmbSection.setImmediate(false);
		cmbSection.setWidth("270px");
		cmbSection.setHeight("-1px");
		cmbSection.setNullSelectionAllowed(true);
		cmbSection.setImmediate(true);
		mainLayout.addComponent(cmbSection, "top:38.0px; left:140.0px;");

		opgEmployee = new OptionGroup("",lstOPG);
		opgEmployee.setStyleName("horizontal");
		opgEmployee.setValue("Employee ID");
		opgEmployee.setImmediate(true);
		mainLayout.addComponent(opgEmployee, "top:70.0px; left:50.0px;");

		// lblEmployee
		lblEmployee = new Label("Employee ID :");
		lblEmployee.setImmediate(false);
		lblEmployee.setWidth("100.0%");
		lblEmployee.setHeight("-1px");
		mainLayout.addComponent(lblEmployee,"top:100.0px; left:30.0px;");

		// cmbEmployee
		cmbEmployee = new ComboBox();
		cmbEmployee.setImmediate(false);
		cmbEmployee.setWidth("270px");
		cmbEmployee.setHeight("-1px");
		cmbEmployee.setNullSelectionAllowed(true);
		cmbEmployee.setImmediate(true);
		mainLayout.addComponent(cmbEmployee, "top:98.0px; left:140.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:130.0px;left:140.0px;");

		mainLayout.addComponent(cButton,"top:150.opx; left:120.0px");

		return mainLayout;
	}
}
