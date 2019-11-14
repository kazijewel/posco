package com.appform.hrmModule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.AmountField;
import com.common.share.CommonButton;
import com.common.share.MessageBox;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class DesignationSectionSerialInfo extends Window 
{
	SessionBean sessionbean;

	private AbsoluteLayout mainLayout;

	private Table tableDesignation = new Table();
	private Table tableSection = new Table();
	private Table tableGrade = new Table();

	private ArrayList<Label> tbDegSlNo = new ArrayList<Label>();
	private ArrayList<Label> tbDesignationID = new ArrayList<Label>();
	private ArrayList<Label> tbDesignationName = new ArrayList<Label>();
	private ArrayList<AmountField> tbDesignationSerial = new ArrayList<AmountField>();

	private ArrayList<Label> tbSecSlNo = new ArrayList<Label>();
	private ArrayList<Label> tbSectionID = new ArrayList<Label>();
	private ArrayList<Label> tbSectionName = new ArrayList<Label>();
	private ArrayList<AmountField> tbSectionSerial = new ArrayList<AmountField>();

	private ArrayList<Label> tbGradeSlNo = new ArrayList<Label>();
	private ArrayList<Label> tbGradeID = new ArrayList<Label>();
	private ArrayList<Label> tbGradeName = new ArrayList<Label>();
	private ArrayList<AmountField> tbGradeSerial = new ArrayList<AmountField>();

	List lstSerial = Arrays.asList(new String[]{"Section","Designation","Grade"});

	private OptionGroup opSerial=new OptionGroup("",lstSerial);

	private CommonButton cButton = new CommonButton("", "Save", "", "", "", "", "", "", "", "Exit");

	private String Notify = "";

	public DesignationSectionSerialInfo(SessionBean sessionBean) 
	{
		this.sessionbean = sessionBean;
		this.setResizable(false);

		this.setCaption("SERIALIZE DESIGNATION/SECTION/GRADE :: "+sessionBean.getCompany());

		this.setWidth("460px");
		this.setHeight("450px");
		sessionBean.getCompanyId();

		buildMainLayout();
		setContent(mainLayout);

		tableDesignationInitialize();
		btnAction();
		tableClear();
		tableDesignationDataAdd();
	}


	private void btnAction()
	{
		opSerial.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(opSerial.getValue()=="Designation")
				{
					tableDesignation.setVisible(true);
					tableSection.setVisible(false);
					tableGrade.setVisible(false);
					tableDesignationInitialize();
					tableDesignationDataAdd();
				}
				else if(opSerial.getValue()=="Section")
				{
					tableSection.setVisible(true);
					tableDesignation.setVisible(false);
					tableGrade.setVisible(false);
					tableSectionInitialize();
					tableSectionDataAdd();
				}

				else if(opSerial.getValue()=="Grade")
				{
					tableGrade.setVisible(true);
					tableDesignation.setVisible(false);
					tableSection.setVisible(false);
					tableGradeInitialize();
					tableGradeDataAdd();
				}
			}
		});

		cButton.btnSave.addListener(new ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{	
				if(sessionbean.isUpdateable())
				{
					if(chkDesignationTableData())
					{
						btnSaveEvent();
					}
					else
					{
						showNotification("Warning", Notify, Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					showNotification("Warning","You have not proper authentication for save!!!",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		cButton.btnExit.addListener(new ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{				
				close();
			}
		});
	}

	private boolean chkDesignationTableData()
	{
		boolean ret = false;
		int count=0;
		if(opSerial.getValue()=="Designation")
		{
			for(int ind=0;ind<tbDesignationID.size();ind++)
			{
				if(!tbDesignationID.get(ind).getValue().toString().isEmpty())
				{
					count++;
					if(!tbDesignationSerial.get(ind).getValue().toString().trim().isEmpty())
					{
						if(Integer.parseInt(tbDesignationSerial.get(ind).getValue().toString().trim())!=0)
						{
							ret=true;
						}
						else
						{
							Notify="Please Provide Designation Serial!!!";
							tbDesignationSerial.get(ind).setValue("");
							tbDesignationSerial.get(ind).focus();
							ret=false;
							break;
						}
					}
					else
					{
						Notify="Please Provide Designation Serial!!!";
						tbDesignationSerial.get(ind).focus();
						ret=false;
						break;
					}
				}
			}
		}

		else if(opSerial.getValue()=="Section")
		{
			for(int ind=0;ind<tbSectionID.size();ind++)
			{
				if(!tbSectionID.get(ind).getValue().toString().isEmpty())
				{
					count++;
					if(!tbSectionSerial.get(ind).getValue().toString().trim().isEmpty())
					{
						if(Integer.parseInt(tbSectionSerial.get(ind).getValue().toString().trim())!=0)
						{
							ret=true;
						}
						else
						{
							Notify="Please Provide Section Serial!!!";
							tbSectionSerial.get(ind).setValue("");
							tbSectionSerial.get(ind).focus();
							ret=false;
							break;
						}
					}
					else
					{
						Notify="Please Provide Section Serial!!!";
						tbSectionSerial.get(ind).focus();
						ret=false;
						break;
					}
				}
			}
		}

		if(opSerial.getValue()=="Grade")
		{
			for(int ind=0;ind<tbGradeID.size();ind++)
			{
				if(!tbGradeID.get(ind).getValue().toString().isEmpty())
				{
					count++;
					if(!tbGradeSerial.get(ind).getValue().toString().trim().isEmpty())
					{
						if(Integer.parseInt(tbGradeSerial.get(ind).getValue().toString().trim())!=0)
						{
							ret=true;
						}
						else
						{
							Notify="Please Provide Grade Serial!!!";
							tbGradeSerial.get(ind).setValue("");
							tbGradeSerial.get(ind).focus();
							ret=false;
							break;
						}
					}
					else
					{
						Notify="Please Provide Grade Serial!!!";
						tbGradeSerial.get(ind).focus();
						ret=false;
						break;
					}
				}
			}
		}

		if(count==0)
			showNotification("Warning", "No Data Found!!!", Notification.TYPE_WARNING_MESSAGE);
		return ret;
	}

	private void btnSaveEvent()
	{
		MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to save information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
		mb.show(new EventListener()
		{
			public void buttonClicked(ButtonType buttonType)
			{
				if(buttonType == ButtonType.YES)
				{
					insertData();
				}
			}
		});
	}

	private void insertData()
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx = session.beginTransaction();
		try
		{
			System.out.println("ok");
			if(opSerial.getValue().toString().equalsIgnoreCase("Designation"))
			{
				for(int i=0;i<tbDesignationName.size();i++)
				{
					if(!tbDesignationName.get(i).getValue().toString().trim().isEmpty())
					{
						String designationSlNo = tbDesignationSerial.get(i).getValue().toString().isEmpty()?"0":tbDesignationSerial.get(i).getValue().toString().replaceAll(",", "");

						String sql= " Update tbDesignationInfo set iDesignationSerial = '"+designationSlNo+"'  " +
								" where vDesignationId = '"+tbDesignationID.get(i).getValue()+"' ";
						session.createSQLQuery(sql).executeUpdate();
					}
				}
				tableClear();
				tableDesignationDataAdd();
			}
			else if(opSerial.getValue().toString().equalsIgnoreCase("Section"))
			{
				for(int i=0;i<tbSectionName.size();i++)
				{
					if(!tbSectionName.get(i).getValue().toString().trim().isEmpty())
					{
						String SectionSlNo = tbSectionSerial.get(i).getValue().toString().isEmpty()?"0":tbSectionSerial.get(i).getValue().toString().replaceAll(",", "");

						String sql= " Update tbSectionInfo set iSectionSerial = '"+SectionSlNo+"'  " +
								" where vSectionId = '"+tbSectionID.get(i).getValue()+"' ";
						session.createSQLQuery(sql).executeUpdate();
					}
				}
				tableClear();
				tableSectionDataAdd();
			}
			else if(opSerial.getValue().toString().equalsIgnoreCase("Grade"))
			{
				for(int i=0;i<tbGradeName.size();i++)
				{
					if(!tbGradeName.get(i).getValue().toString().trim().isEmpty())
					{
						String SectionSlNo = tbGradeSerial.get(i).getValue().toString().isEmpty()?"0":tbGradeSerial.get(i).getValue().toString().replaceAll(",", "");

						String sql= " Update tbPayScaleInfo set iGradeSerial = '"+SectionSlNo+"'  " +
								" where vGradeId = '"+tbGradeID.get(i).getValue()+"' ";
						session.createSQLQuery(sql).executeUpdate();
					}
				}
				tableClear();
				tableGradeDataAdd();
			}

			showNotification("All information save successfully.");

			tx.commit();
		}
		catch(Exception exp)
		{
			tx.rollback();
			showNotification("insertData ",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void tableDesignationDataAdd()
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx = session.beginTransaction();
		try
		{
			String sql = "select vDesignationId,vDesignation,iDesignationSerial from tbDesignationInfo where isActive=1 order by iDesignationSerial";

			List list = session.createSQLQuery(sql).list();
			System.out.println(sql);

			int i = 0;

			tableClear();

			if(!list.isEmpty())
			{
				for (Iterator iter = list.iterator(); iter.hasNext();)
				{
					if(tbDesignationSerial.size()-1==i)
					{
						tableDesignationRowAdd(i+1);
					}
					Object[] element = (Object[]) iter.next();
					tbDesignationID.get(i).setValue(element[0]);
					tbDesignationName.get(i).setValue(element[1]);
					tbDesignationSerial.get(i).setValue(Integer.parseInt(element[2].toString()));
					i++;
				}
			}
			else
			{
				showNotification("Warning","There are no Data!!!",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception exp)
		{
			showNotification("addData", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void tableSectionDataAdd()
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx = session.beginTransaction();
		try
		{
			String sql = "select vSectionId,vSectionName,iSectionSerial from tbSectionInfo where isActive=1 order by iSectionSerial";
			List list = session.createSQLQuery(sql).list();

			int i = 0;

			tableClear();

			if(!list.isEmpty())
			{
				for (Iterator iter = list.iterator(); iter.hasNext();)
				{
					if(tbSectionSerial.size()-1==i)
					{
						tableSectionRowAdd(i+1);
					}
					Object[] element = (Object[]) iter.next();
					tbSectionID.get(i).setValue(element[0]);
					tbSectionName.get(i).setValue(element[1]);
					tbSectionSerial.get(i).setValue(Integer.parseInt(element[2].toString()));
					i++;
				}
			}
			else
			{
				showNotification("Warning","There are no Data!!!",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception exp)
		{
			showNotification("addData", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void tableGradeDataAdd()
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx = session.beginTransaction();
		try
		{
			String sql = "select vGradeId,vGradeName,iGradeSerial from tbPayScaleInfo where isActive=1 order by iGradeSerial";

			List list = session.createSQLQuery(sql).list();
			System.out.println(sql);

			int i = 0;

			tableClear();

			if(!list.isEmpty())
			{
				for (Iterator iter = list.iterator(); iter.hasNext();)
				{
					if(tbGradeSerial.size()-1==i)
					{
						tableGradeRowAdd(i+1);
					}
					Object[] element = (Object[]) iter.next();
					tbGradeID.get(i).setValue(element[0]);
					tbGradeName.get(i).setValue(element[1]);
					tbGradeSerial.get(i).setValue(Integer.parseInt(element[2].toString()));
					i++;
				}
			}
			else
			{
				showNotification("Warning","There are no Data!!!",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception exp)
		{
			showNotification("addData", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void tableClear()
	{
		for(int i=0;i<tbDesignationName.size();i++)
		{
			tbDesignationID.get(i).setValue("");
			tbDesignationName.get(i).setValue("");
			tbDesignationSerial.get(i).setValue("");
		}
		for(int i=0;i<tbSectionName.size();i++)
		{
			tbSectionID.get(i).setValue("");
			tbSectionName.get(i).setValue("");
			tbSectionSerial.get(i).setValue("");
		}
		for(int i=0;i<tbGradeName.size();i++)
		{
			tbGradeID.get(i).setValue("");
			tbGradeName.get(i).setValue("");
			tbGradeSerial.get(i).setValue("");
		}
	}

	private void tableDesignationInitialize()
	{
		for(int i=0;i<10;i++)
		{
			tableDesignationRowAdd(i);
		}
	}

	private void tableDesignationRowAdd(final int ar)
	{
		tbDegSlNo.add(new Label());
		tbDegSlNo.get(ar).setWidth("100%");
		tbDegSlNo.get(ar).setHeight("20px");
		tbDegSlNo.get(ar).setImmediate(true);
		tbDegSlNo.get(ar).setValue(ar+1);

		tbDesignationID.add(new Label());
		tbDesignationID.get(ar).setWidth("100%");
		tbDesignationID.get(ar).setImmediate(true);

		tbDesignationName.add(new Label());
		tbDesignationName.get(ar).setWidth("100%");
		tbDesignationName.get(ar).setImmediate(true);

		tbDesignationSerial.add(new AmountField());
		tbDesignationSerial.get(ar).setWidth("100%");
		tbDesignationSerial.get(ar).setImmediate(true);
		tbDesignationSerial.get(ar).setStyleName("fright");

		tbDesignationSerial.get(ar).addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!tbDesignationSerial.get(ar).getValue().toString().isEmpty())
				{
					boolean fla = (doubleDegsignationEntryCheck(tbDesignationSerial.get(ar).getValue().toString().trim(),ar));

					if ( !tbDesignationSerial.get(ar).getValue().toString().isEmpty()  && fla) 
					{
						if(!tbDesignationName.get(ar+1).getValue().toString().isEmpty())
						{
							tbDesignationSerial.get(ar+1).focus();
						}
						else
						{
							tbDesignationSerial.get(ar).focus();
						}
					}
					else 
					{
						getParent().showNotification("Warning","Same Designation Serial Is Not Applicable!!!",Notification.TYPE_WARNING_MESSAGE);
					}
				}
			}
		});

		tableDesignation.addItem(new Object[]{tbDegSlNo.get(ar),tbDesignationID.get(ar),tbDesignationName.get(ar),tbDesignationSerial.get(ar)}, new Integer(ar));
	}

	private void tableSectionInitialize()
	{
		for(int i=0;i<10;i++)
		{
			tableSectionRowAdd(i);
		}
	}

	private void tableSectionRowAdd(final int ar)
	{
		tbSecSlNo.add(new Label());
		tbSecSlNo.get(ar).setWidth("100%");
		tbSecSlNo.get(ar).setHeight("20px");
		tbSecSlNo.get(ar).setImmediate(true);
		tbSecSlNo.get(ar).setValue(ar+1);

		tbSectionID.add(new Label());
		tbSectionID.get(ar).setWidth("100%");
		tbSectionID.get(ar).setImmediate(true);

		tbSectionName.add(new Label());
		tbSectionName.get(ar).setWidth("100%");
		tbSectionName.get(ar).setImmediate(true);

		tbSectionSerial.add(new AmountField());
		tbSectionSerial.get(ar).setWidth("100%");
		tbSectionSerial.get(ar).setImmediate(true);
		tbSectionSerial.get(ar).setStyleName("fright");
		tbSectionSerial.get(ar).addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!tbSectionSerial.get(ar).getValue().toString().isEmpty())
				{
					boolean fla = (doubleSectionEntryCheck(tbSectionSerial.get(ar).getValue().toString().trim(),ar));

					if ( !tbSectionSerial.get(ar).getValue().toString().isEmpty()  && fla) 
					{
						if(!tbSectionName.get(ar+1).getValue().toString().isEmpty())
						{
							tbSectionSerial.get(ar+1).focus();
						}
						else
						{
							tbSectionSerial.get(ar).focus();
						}
					}
					else 
					{
						getParent().showNotification("Warning","Same Section Serial Is Not Applicable!!!",Notification.TYPE_WARNING_MESSAGE);
					}
				}
			}
		});

		tableSection.addItem(new Object[]{tbSecSlNo.get(ar),tbSectionID.get(ar),tbSectionName.get(ar),tbSectionSerial.get(ar)}, new Integer(ar));
	}

	private void tableGradeInitialize()
	{
		for(int i=0;i<10;i++)
		{
			tableGradeRowAdd(i);
		}
	}

	private void tableGradeRowAdd(final int ar)
	{
		tbGradeSlNo.add(new Label());
		tbGradeSlNo.get(ar).setWidth("100%");
		tbGradeSlNo.get(ar).setHeight("20px");
		tbGradeSlNo.get(ar).setImmediate(true);
		tbGradeSlNo.get(ar).setValue(ar+1);

		tbGradeID.add(new Label());
		tbGradeID.get(ar).setWidth("100%");
		tbGradeID.get(ar).setImmediate(true);

		tbGradeName.add(new Label());
		tbGradeName.get(ar).setWidth("100%");
		tbGradeName.get(ar).setImmediate(true);

		tbGradeSerial.add(new AmountField());
		tbGradeSerial.get(ar).setWidth("100%");
		tbGradeSerial.get(ar).setImmediate(true);
		tbGradeSerial.get(ar).setStyleName("fright");


		tbGradeSerial.get(ar).addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!tbGradeSerial.get(ar).getValue().toString().isEmpty())
				{
					boolean fla = (doubleGradeEntryCheck(tbGradeSerial.get(ar).getValue().toString().trim(),ar));

					if(!tbGradeSerial.get(ar).getValue().toString().isEmpty()  && fla) 
					{
						if(!tbGradeName.get(ar+1).getValue().toString().isEmpty())
						{
							tbGradeSerial.get(ar+1).focus();
						}
						else
						{
							tbGradeSerial.get(ar).focus();
						}
					}
					else 
					{
						getParent().showNotification("Warning","Same Designation Serial Is Not Applicable!!!",Notification.TYPE_WARNING_MESSAGE);
					}
				}
			}
		});

		tableGrade.addItem(new Object[]{tbGradeSlNo.get(ar),tbGradeID.get(ar),tbGradeName.get(ar),tbGradeSerial.get(ar)}, new Integer(ar));
	}

	private boolean doubleGradeEntryCheck(String caption,int row)
	{
		for(int i=0;i<tbGradeName.size();i++)
		{
			if(i!=row && caption.equals(tbGradeSerial.get(i).getValue().toString().trim()))
			{
				tbGradeSerial.get(row).setValue("");
				return false;
			}	
		}
		return true;
	}

	private boolean doubleSectionEntryCheck(String caption,int row)
	{
		for(int i=0;i<tbSectionName.size();i++)
		{
			if(i!=row && caption.equals(tbSectionSerial.get(i).getValue().toString().trim()))
			{
				tbSectionSerial.get(row).setValue("");
				return false;
			}	
		}
		return true;
	}

	private boolean doubleDegsignationEntryCheck(String caption,int row)
	{
		for(int i=0;i<tbDesignationName.size();i++)
		{
			if(i!=row && caption.equals(tbDesignationSerial.get(i).getValue().toString().trim()))
			{
				tbDesignationSerial.get(row).setValue("");
				return false;
			}	
		}
		return true;
	}

	private AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(false);

		opSerial.setImmediate(true);
		opSerial.select("Section");
		opSerial.setStyleName("horizontal");
		mainLayout.addComponent(new Label("Type : "), "top: 20.0px; left: 20.0px;");
		mainLayout.addComponent(opSerial, "top:20.0px; left:70.0px;");

		tableDesignation.setWidth("410px");
		tableDesignation.setHeight("300px");
		tableDesignation.setImmediate(true);
		tableDesignation.setColumnCollapsingAllowed(true);
		tableDesignation.setPageLength(0);
		tableDesignation.setFooterVisible(false);

		tableDesignation.addContainerProperty("Sl#", Label.class, new Label(),null,null,Table.ALIGN_RIGHT);
		tableDesignation.setColumnWidth("SL#", 30);

		tableDesignation.addContainerProperty("DESIGNATION ID", Label.class, new Label(),null,null,Table.ALIGN_LEFT);
		tableDesignation.setColumnWidth("DESIGNATION ID", 60);

		tableDesignation.addContainerProperty("DESIGNATION NAME", Label.class, new Label(),null,null,Table.ALIGN_LEFT);
		tableDesignation.setColumnWidth("DESIGNATION NAME", 250);

		tableDesignation.addContainerProperty("DESIGNATION SERIAL", AmountField.class, new AmountField(),null,null,Table.ALIGN_CENTER);
		tableDesignation.setColumnWidth("DESIGNATION SERIAL", 60);

		tableDesignation.setColumnCollapsed("DESIGNATION ID", true);

		mainLayout.addComponent(tableDesignation,"top:50.0px;left:20.0px;");

		tableSection.setWidth("410px");
		tableSection.setHeight("300px");
		tableSection.setImmediate(true);
		tableSection.setColumnCollapsingAllowed(true);
		tableSection.setPageLength(0);
		tableSection.setFooterVisible(false);

		tableSection.addContainerProperty("Sl#", Label.class, new Label(),null,null,Table.ALIGN_RIGHT);
		tableSection.setColumnWidth("SL#", 30);

		tableSection.addContainerProperty("SECTION ID", Label.class, new Label(),null,null,Table.ALIGN_LEFT);
		tableSection.setColumnWidth("SECTION ID", 60);

		tableSection.addContainerProperty("SECTION NAME", Label.class, new Label(),null,null,Table.ALIGN_LEFT);
		tableSection.setColumnWidth("SECTION NAME", 250);

		tableSection.addContainerProperty("SECTION SERIAL", AmountField.class, new AmountField(),null,null,Table.ALIGN_CENTER);
		tableSection.setColumnWidth("SECTION SERIAL", 60);

		tableSection.setColumnCollapsed("SECTION ID", true);
		tableSection.setVisible(false);
		mainLayout.addComponent(tableSection,"top:50.0px;left:20.0px;");

		tableGrade.setWidth("410px");
		tableGrade.setHeight("300px");
		tableGrade.setImmediate(true);
		tableGrade.setColumnCollapsingAllowed(true);
		tableGrade.setPageLength(0);
		tableGrade.setFooterVisible(false);

		tableGrade.addContainerProperty("Sl#", Label.class, new Label(),null,null,Table.ALIGN_RIGHT);
		tableGrade.setColumnWidth("SL#", 30);

		tableGrade.addContainerProperty("GRADE ID", Label.class, new Label(),null,null,Table.ALIGN_LEFT);
		tableGrade.setColumnWidth("GRADE ID", 60);

		tableGrade.addContainerProperty("GRADE NAME", Label.class, new Label(),null,null,Table.ALIGN_LEFT);
		tableGrade.setColumnWidth("GRADE NAME", 250);

		tableGrade.addContainerProperty("GRADE SERIAL", AmountField.class, new AmountField(),null,null,Table.ALIGN_CENTER);
		tableGrade.setColumnWidth("GRADE SERIAL", 60);

		tableGrade.setColumnCollapsed("GRADE ID", true);
		tableGrade.setVisible(false);
		mainLayout.addComponent(tableGrade,"top:50.0px;left:20.0px;");
		mainLayout.addComponent(cButton,"top:360.0px;left:150.0px;");

		return mainLayout;
	}
}