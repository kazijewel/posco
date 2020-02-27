package com.appform.hrmModule;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;


import com.common.share.CommaSeparator;
import com.common.share.CommonButton;
import com.common.share.CommonMethod;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.ReportDate;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.FileResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Window;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class EmployeeInformation extends Window
{
	private AbsoluteLayout mainLayout;

	String imageLoc = "0" ;
	String birthImageLoc = "0" ;
	String nidImageLoc = "0" ;
	String applicationImageLoc = "0";
	String joinImageLoc = "0" ;
	String conImageLoc = "0" ;
	String employeeImages = "0";
	String stAIctive="";
	String pfImageLoc="0";
	String serviceAgreementImageLoc="0";
	
	private SimpleDateFormat dfYear = new SimpleDateFormat("yyyy");

	TabSheet tabSheet = new TabSheet();
	TabOfficialInfo firstTab;
	TabPersonalInformation secondTab = new TabPersonalInformation();
	TabEducation thirdTab = new TabEducation();
	TabExperience fourthTab = new TabExperience();
	TabSalaryStructure fifthTab = new TabSalaryStructure();
	//TabPFInformation sixTab;

	CommonButton button = new CommonButton("New", "Save", "Edit", "","Refresh","","","","","Exit");
	private static final List<String> type=Arrays.asList(new String[]{"Active","Inactive"});

	boolean isEdit = false;
	boolean isFind = false;
	boolean isUpdate = false;
	public DecimalFormat df = new DecimalFormat("#0.00");
	public DecimalFormat dfZero = new DecimalFormat("#0");

	public ArrayList<Component> allComp = new ArrayList<Component>();

	SessionBean sessionBean;
	private CommonMethod cm;
	private String menuId = "";
	ListSelect ListSearch;
	TextField txtSearch;
	OptionGroup opgActiveInactive;
	private String employeeCode = "";
	public EmployeeInformation(SessionBean sessionBean,String menuId) 
	{
		this.sessionBean = sessionBean;
		this.setCaption("EMPLOYEE INFORMATION :: "+sessionBean.getCompany());
		cm = new CommonMethod(sessionBean);
		this.menuId = menuId;
		firstTab = new TabOfficialInfo(sessionBean);
		//sixTab=new TabPFInformation(sessionBean);
		employeeImages = "D:/Tomcat 7.0/webapps/report/"+sessionBean.getContextName()+"/employee/";
		addCmp();
		buildMainLayout();
		setContent(mainLayout);
		btnInit(true);
		tabInit(false);
		setEventActionMain();
		setEventActionFirstTab();
		setEventActionSixthTab();
		addFindEmployee("%");
		button.btnNew.focus();
		focusMove();
		authencationCheck();
	}
	private void setEventActionSixthTab()
	{
		/*sixTab.txtPFID.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
				if(!isFind)
				{
					if(checkPF())
					{
						sixTab.txtPFID.setValue("");
						showNotification("","PF ID Already Exist Try New One!");
					}
				}
			}
		});*/
	}
	private boolean checkPF()
	{
		/*Session session=SessionFactoryUtil.getInstance().openSession();
		try{
			String sql="select * from tbEmpNomineeInfo where vPfId like '"+(sixTab.txtPFID.getValue().toString().isEmpty()?"":sixTab.txtPFID.getValue().toString().trim())+"'";
			List list=session.createSQLQuery(sql).list();
			if(!list.isEmpty())
			{
				return true;
			}
		}catch(Exception e)
		{
			showNotification("Error..",""+e,Notification.TYPE_ERROR_MESSAGE);
		}*/
		return false;
	}
	private void authencationCheck()
	{
		cm.checkFormAction(menuId);
		if(!sessionBean.isSuperAdmin())
		{
		if(!sessionBean.isAdmin())
		{
			if(!cm.isSave)
			{button.btnSave.setVisible(false);}
			if(!cm.isEdit)
			{button.btnEdit.setVisible(false);}
			if(!cm.isDelete)
			{button.btnDelete.setVisible(false);}
			if(!cm.isPreview)
			{button.btnPreview.setVisible(false);}
		}
		}
	}

	private void btnInit(boolean t)
	{
		button.btnNew.setEnabled(t);
		button.btnEdit.setEnabled(t);
		button.btnSave.setEnabled(!t);
		button.btnRefresh.setEnabled(!t);
		button.btnDelete.setEnabled(t);
	}

	private void addCmp()
	{
		tabSheet.addTab(firstTab,"Official Information");
		tabSheet.addTab(secondTab,"Personal Information");
		tabSheet.addTab(thirdTab,"Education");
		tabSheet.addTab(fourthTab,"Experience");
		tabSheet.addTab(fifthTab,"Salary Structure");

		thirdTab.setImmediate(true);
	}

	private void addFindEmployee(String findString)
	{
		ListSearch.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		int i = 0;
		try
		{
			if(opgActiveInactive.getValue().equals("Active"))
			{
				String sql = " select vEmployeeId,vEmployeeName,vEmployeeCode,(select vDesignation from tbEmpDesignationInfo di where"+ 
						" di.vEmployeeId = opi.vEmployeeId) vDesignation from tbEmpOfficialPersonalInfo opi where bStatus='1' and (vEmployeeCode like" +
						"'%"+findString+"%' or vEmployeeName like '%"+findString+"%') order by SUBSTRING(vEmployeeCode,3,100) asc ";
				System.out.println("oooo"+sql);
				List<?> list = session.createSQLQuery(sql).list();
				for(Iterator<?> iter = list.iterator(); iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();
					ListSearch.addItem(element[0]);
					ListSearch.setItemCaption(element[0], element[2].toString()+" >> "+element[1].toString());
					i = 1;
				}
			}
			else
			{
				String sql = " select vEmployeeId,vEmployeeName,vEmployeeCode,(select vDesignation from tbEmpDesignationInfo di where"+ 
						" di.vEmployeeId = opi.vEmployeeId) vDesignation from tbEmpOfficialPersonalInfo opi where bStatus='0' and (vEmployeeCode like" +
						"'%"+findString+"%' or vEmployeeName like '%"+findString+"%') order by SUBSTRING(vEmployeeCode,3,100) asc ";
				System.out.println("oooo"+sql);
				List<?> list = session.createSQLQuery(sql).list();
				for(Iterator<?> iter = list.iterator(); iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();
					ListSearch.addItem(element[0]);
					ListSearch.setItemCaption(element[0], element[2].toString()+" >> "+element[1].toString());
					i = 1;
				}
			}
			//String findString = "%"+(txtSearch.getValue().toString().isEmpty()?"":txtSearch.getValue().toString())+"%";
		
			if(i==0)
			{
				showNotification("Warning!","No data found",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch (Exception e)
		{
			showNotification("Unable to get employee data",""+e,Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}

	private void setEventActionMain()
	{
		button.btnNew.addListener(new ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				textClear();
				btnInit(false);
				tabInit(true);
				firstTab.dJoiningDate.setEnabled(true);
				firstTab.chkOtEnable.setValue(true);
				isEdit = false;
				isFind = false;
				isUpdate = false;
				
				imageLoc = "0";
				birthImageLoc = "0";
				nidImageLoc = "0";
				applicationImageLoc = "0";
				joinImageLoc = "0";
				conImageLoc = "0";
				serviceAgreementImageLoc="0";
				firstTab.txtEmployeeID.setValue(selectMaxEmpId());
				
			}
		});
		
		opgActiveInactive.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				ListSearch.removeAllItems();
				if(event.getProperty().toString()=="Active")
				{
					stAIctive = "1";
					addEmployeeByStaus(stAIctive);
				}
				else
				{
					stAIctive = "0";
					addEmployeeByStaus(stAIctive);
				}
			}
		});

		button.btnSave.addListener(new ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(!firstTab.txtEmployeeCode.getValue().toString().isEmpty())
				{
					if(!firstTab.txtEmployeeName.getValue().toString().isEmpty())
						{
							if(firstTab.cmbReligion.getValue()!= null)
							{
								if(!firstTab.txtContact.getValue().toString().isEmpty())
								{
									if(firstTab.RadioGender.getValue()!= null)
									{
										if(!firstTab.txtNationality.getValue().toString().isEmpty())
										{
											if(firstTab.cmbEmployeeType.getValue()!=null)
											{
												if(firstTab.cmbLevel.getValue()!=null)
												{
													if(firstTab.cmbServiceType.getValue()!=null)
													{
														if(firstTab.cmbUnitName.getValue()!=null)
														{
															if(firstTab.cmbDepartment.getValue()!=null)
															{
																if(firstTab.cmbDesignation.getValue()!=null)
																{	
																	if(firstTab.cmbStatus.getValue()!=null)
																	{
																		if(checkBank())
																		{
																			SaveButtonAction();
																		}
																	}
																	else
																	{
																		showNotification("Warning!","Select Status at Official Information.",Notification.TYPE_WARNING_MESSAGE);
																		firstTab.cmbStatus.focus();
																	}
																
																}
																else
																{
																	showNotification("Warning!","Select Designation at Official Informationat.",Notification.TYPE_WARNING_MESSAGE);
																	firstTab.cmbDesignation.focus();
																}
															}
															else
															{
																showNotification("Warning!","Select Department at Official Information.",Notification.TYPE_WARNING_MESSAGE);
																firstTab.cmbDepartment.focus();
															}
														}
														else
														{
															showNotification("Warning!","Select Project Name at Official Information.",Notification.TYPE_WARNING_MESSAGE);
															firstTab.cmbUnitName.focus();
														}
													}
													else
													{
														showNotification("Warning!","Select Service Status at Official Information.",Notification.TYPE_WARNING_MESSAGE);
														firstTab.cmbServiceType.focus();
													}
												
												}
												else
												{
													showNotification("Warning!","Select Level Of English at Official Information.",Notification.TYPE_WARNING_MESSAGE);
													firstTab.cmbLevel.focus();
												}
											}
											else
											{
												showNotification("Warning!","Select Employee Type at Official Information.",Notification.TYPE_WARNING_MESSAGE);
												firstTab.cmbEmployeeType.focus();
											}
										}
										else
										{
											showNotification("Warning!","Provide Nationality at Official Information.",Notification.TYPE_WARNING_MESSAGE);
											firstTab.txtNid.focus();
										}
									}
									else
									{
										showNotification("Warning!","Select Gender at Official Information.",Notification.TYPE_WARNING_MESSAGE);
										firstTab.RadioGender.focus();
									}
								}
								else
								{
									showNotification("Warning!","Provide Contact at Official Information.",Notification.TYPE_WARNING_MESSAGE);
									firstTab.txtContact.focus();
								}
							}
							else
							{
								showNotification("Warning!","Select Religion at Official Information.",Notification.TYPE_WARNING_MESSAGE);
								firstTab.cmbReligion.focus();
							}
						}
						else
						{
							showNotification("Warning!","Provide Employee Name at Official Information.",Notification.TYPE_WARNING_MESSAGE);
							firstTab.txtEmployeeName.focus();
						}
					}
/*					else
					{
						showNotification("Warning!","Provide Finger Id or Proximity Id at Official Information.",Notification.TYPE_WARNING_MESSAGE);
						firstTab.txtFingerId.focus();
					}
				}
*/				else
				{
					showNotification("Warning!","Provide Employee Code at Official Information.",Notification.TYPE_WARNING_MESSAGE);
					firstTab.txtEmployeeCode.focus();
				}
			}
		});

		button.btnRefresh.addListener(new ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				textClear();
				btnInit(true);
				tabInit(false);
				firstTab.dJoiningDate.setEnabled(true);
				firstTab.chkOtEnable.setValue(false);

				isEdit = false;
				isFind = false;
				isUpdate = false;
				
			}
		});

		button.btnExit.addListener(new ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

		button.btnEdit.addListener(new ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(!firstTab.txtEmployeeID.getValue().toString().isEmpty())
				{
					isUpdate = true;
					btnInit(false);
					tabInit(true);
					firstTab.dJoiningDate.setEnabled(false);
				}
				else
				{
					showNotification("Warning!","Select Employee Name from left list.",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		txtSearch.addListener(new TextChangeListener()
		{
			public void textChange(TextChangeEvent event)
			{
				addFindEmployee(event.getText());
			}
		});

		ListSearch.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(ListSearch.getValue()!=null)
				{
					isFind = true;
					isEdit = true;


					secondTabClear();
					thirdTabClear();
					forthTabClear();
					fifthTabClear();
					sixTabClear();
					
					firstTab.setEnabled(true);
					secondTab.setEnabled(true);
					thirdTab.setEnabled(true);
					fourthTab.setEnabled(true);  
					fifthTab.setEnabled(true);

					selectEmployeeInformation(ListSearch.getValue().toString());
					firstTab.dJoiningDate.setEnabled(false);

					if(birthImageLoc.equals("0"))
					{firstTab.btnBirthPreview.setCaption("attach");}
					else
					{firstTab.btnBirthPreview.setCaption("Preview");}

					if(nidImageLoc.equals("0"))
					{firstTab.btnNidPreview.setCaption("attach");}
					else
					{firstTab.btnNidPreview.setCaption("Preview");}

					if(applicationImageLoc.equals("0"))
					{firstTab.btnApplicationPreview.setCaption("attach");}
					else
					{firstTab.btnApplicationPreview.setCaption("Preview");}

					if(joinImageLoc.equals("0"))
					{firstTab.btnJoinPreview.setCaption("attach");}
					else
					{firstTab.btnJoinPreview.setCaption("Preview");}

					if(conImageLoc.equals("0"))
					{firstTab.btnConPreview.setCaption("attach");}
					else
					{firstTab.btnConPreview.setCaption("Preview");}
					
					if(serviceAgreementImageLoc.equals("0"))
					{firstTab.btnServiceAgreementPreview.setCaption("attach");}
					else
					{firstTab.btnServiceAgreementPreview.setCaption("Preview");}
				}
			}
		});
	}
	private boolean checkBank()
	{
		if(!fifthTab.opgBank.getValue().toString().equals("Cash"))
		{
			if(fifthTab.cmbBankName.getValue()!=null)
			{
				if(fifthTab.cmbBranchName.getValue()!=null)
				{
					if(!fifthTab.txtAccountNo.getValue().toString().isEmpty())
					{
						return true;
					}
					else
					{
						showNotification("Warning..","Please select Account!",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					showNotification("Warning..","Please select Branch!",Notification.TYPE_WARNING_MESSAGE);
				}
			}
			else
			{
				showNotification("Warning..","Please select Bank!",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else if(fifthTab.opgBank.getValue().toString().equals("Cash"))
		{
			return true;
		}
		return false;
	}
	public void addEmployeeByStaus(String stAIctive)
	{
	ListSearch.removeAllItems();
	Session session = SessionFactoryUtil.getInstance().openSession();
	session.beginTransaction();
	int i = 0;
	try
	{
		//String findString = "%"+(txtSearch.getValue().toString().isEmpty()?"":txtSearch.getValue().toString())+"%";
		String sql = " select vEmployeeId,vEmployeeName,vEmployeeCode,(select vDesignation from tbEmpDesignationInfo di where"+ 
				" di.vEmployeeId = opi.vEmployeeId) vDesignation from tbEmpOfficialPersonalInfo opi where bStatus ='"+stAIctive+"' order by vEmployeeName ";
		
		System.out.println("empByStatus"+sql);
		List<?> list = session.createSQLQuery(sql).list();
		for(Iterator<?> iter = list.iterator(); iter.hasNext();)
		{
			Object[] element = (Object[]) iter.next();
			ListSearch.addItem(element[0]);
			ListSearch.setItemCaption(element[0], element[2].toString()+" >> "+element[1].toString());
			i = 1;
		}
		if(i==0)
		{
			showNotification("Warning!","No data found",Notification.TYPE_WARNING_MESSAGE);
		}
	}
	catch (Exception e)
	{
		showNotification("Unable to get employee data",""+e,Notification.TYPE_WARNING_MESSAGE);
	}
	finally{session.close();}
	}
	private String selectMaxEmpId()
	{
		String maxId = "";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try 
		{
			String query = "Select isnull(max(cast(SUBSTRING(vEmployeeId,5,100) as int)),0)+1 from tbEmpOfficialPersonalInfo";
			Iterator<?> iter = session.createSQLQuery(query).list().iterator();
			if(iter.hasNext())
			{
				String srt = iter.next().toString();
				maxId = "EMP-"+srt;
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		finally{session.close();}
		return maxId;
	}
	private String selectMaxPFId()
	{
		String maxId = "";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try 
		{
			String query = "select (ISNULL(MAX(CAST(SUBSTRING(vPfId,CHARINDEX('-',vPfId)+1,LEN(vPfId)-CHARINDEX('-',vPfId)) as int)),0)+1) as pfId from tbEmpNomineeInfo";

			Iterator <?> iter = session.createSQLQuery(query).list().iterator();

			if (iter.hasNext()) 
			{
				String srt = iter.next().toString();
				maxId = "PF-"+srt;
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		finally{session.close();}
		return maxId;
	}

	private void selectEmployeeInformation(String employeeId)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		employeeCode = "";
		firstTabClear();
		secondTabClear();
		thirdTabClear();
		forthTabClear();
		fifthTabClear();
		sixTabClear();
		

		firstTab.setEnabled(true);
		secondTab.setEnabled(true);
		thirdTab.setEnabled(true);
		fourthTab.setEnabled(true);  
		fifthTab.setEnabled(true);
		
		try
		{
			String sqlOfficial = "select vEmployeeId,vEmployeeCode,vFingerId,vProximityId,vEmployeeName," +
					" vReligion,vContactNo,vEmailAddress,vGender,dDateOfBirth,vNationality,vNationalIdNo,vEmployeeType," +
					" vServiceType,bPhysicallyDisable,dApplicationDate,dInterviewDate,dJoiningDate,vConfirmationDate," +
					" vPayScaleId,vEmployeeStatus,convert(date,vStatusDate)vStatusDate,vEmployeePhoto,vAttachBirth,vAttachNid," +
					" vAttachApplication,vAttachJoining,vAttachConfirmation,vFatherName,vMotherName,vPresentAddress," +
					" vPermanentAddress,vBloodGroup,vMaritalStatus,vMarriageDate,ISNULL(vSpouseName,''),ISNULL(vSpouseOccupation,''),"+
					" iNumberOfChild," +
					" vBankId,vBranchId,vAccountNo,iOtEnable,vUnitName,"+
					" iProbationPeriod,FridayStatus,vBankId,vBankName,"+
					" vBranchId,vBranchName,vMoneyTransferType,vFamilyName,"+
					" vGivenName,vLevelOfEnglish,vDepartmentId,vDepartmentName,"+
					" dValidDate,iContactPeriod, "+
					" vUnitId,vUnitName,ISNULL(vSectionId,'')vSectionId,ISNULL(vSectionName,'')vSectionName,"+
					" vDesignationId,vDesignationName,ISNULL(vGradeId,''),ISNULL(vGradeName,''),"+
					" ISNULL(vShiftId,''),ISNULL(vShiftName,''),ISNULL(vCareerPeriod,'0'),ISNULL(vAttachServiceAgreement,'0')vServiceAgreement," +
					" ISNULL(vRoutingNo,'')vRoutingNo "+
					" from tbEmpOfficialPersonalInfo where vEmployeeId = '"+employeeId+"' ";
			
			System.out.println("selectEmployeeInformation: "+sqlOfficial);
			
			List<?> listOfficial = session.createSQLQuery(sqlOfficial).list();	
			for(Iterator<?> iterEmployee = listOfficial.iterator(); iterEmployee.hasNext();)
			{
				Object[] element = (Object[]) iterEmployee.next();

				firstTab.txtEmployeeID.setValue(element[0].toString());
				firstTab.txtEmployeeCode.setValue(element[1].toString());
				firstTab.txtFingerId.setValue(element[2].toString());
				firstTab.txtProximityId.setValue(element[3].toString());
				firstTab.txtEmployeeName.setValue(element[4].toString());
				//firstTab.chkOtEnable.setValue(false);

				firstTab.cmbReligion.setValue(element[5]);
				firstTab.txtContact.setValue(element[6].toString());
				firstTab.txtEmail.setValue(element[7].toString());

				firstTab.RadioGender.setValue(element[8].toString());
				firstTab.dDateOfBirth.setValue(element[9]);
				firstTab.txtNationality.setValue(element[10].toString());
				firstTab.txtNid.setValue(element[11].toString());

				firstTab.cmbEmployeeType.setValue(element[12].toString());
				firstTab.cmbServiceType.setValue(element[13].toString());
				firstTab.chkPhysicallyDisable.setValue(element[14]);

				firstTab.dApplicationDate.setValue(element[15]);
				firstTab.dInterviewDate.setValue(element[16]);
				firstTab.dJoiningDate.setValue(element[17]);
				if(!element[18].toString().equals(""))
				{
					Date date = sessionBean.dfBd.parse(element[18].toString());
					firstTab.dConfirmationDate.setValue(date);
				}

				//firstTab.cmbPayScale.setValue(element[19]);
				firstTab.cmbStatus.setValue(element[20]);
				if(!element[21].equals(""))
				{
					/*Date date = sessionBean.dfBd.parse(element[21].toString());
					firstTab.dStatusDate.setValue(date);*/
					firstTab.dStatusDate.setValue(element[21]);
				}

				employeeImage(element[22].toString());
				imageLoc = element[22].toString();
				birthImageLoc = element[23].toString();
				nidImageLoc = element[24].toString();
				applicationImageLoc = element[25].toString();
				joinImageLoc = element[26].toString();
				conImageLoc = element[27].toString();

				if(element[41].toString().equals("1"))
				{
					firstTab.chkOtEnable.setValue(true);
				}
				else
				{
					firstTab.chkOtEnable.setValue(false);
				}

				if(element[44].toString().equals("1"))
				{
					firstTab.checkFridayEnabled.setValue(true);
				}
				else
				{
					firstTab.checkFridayEnabled.setValue(false);
				}
				firstTab.cmbProbationPeriod.setValue(element[43].toString());
				firstTab.txtFamilyName.setValue(element[50].toString());
				firstTab.txtGivenName.setValue(element[51].toString());
				firstTab.cmbLevel.setValue(element[52].toString());
				firstTab.cmbDepartment.setValue(element[53]);
				//firstTab.cmbDepartment.setItemCaption(element[53], element[54].toString());
				firstTab.dValidDate.setValue(element[55]);
				firstTab.txtValidYear.setValue(element[56]);
				firstTab.cmbUnitName.setValue(element[57]);
				//firstTab.cmbUnitName.setItemCaption(element[57], element[58].toString());
				firstTab.cmbSection.setValue(element[59]);
				firstTab.cmbDesignation.setValue(element[61]);
				//firstTab.cmbGrade.setValue(element[63]);
				//firstTab.cmbShift.setValue(element[65]);
				firstTab.txtCareerPeriod.setValue(element[67].toString());
				serviceAgreementImageLoc=element[68].toString();
				
				secondTab.txtFatherName.setValue(element[28].toString());
				secondTab.txtMotherName.setValue(element[29].toString());
				secondTab.txtMailing.setValue(element[30].toString().replaceAll("#", "'"));
				secondTab.txtPerAddress.setValue(element[31].toString().replaceAll("#", "'"));
				secondTab.cmbBloodGroup.setValue(element[32]);
				secondTab.ogMaritalStatus.setValue(element[33].toString());
				if(!element[34].toString().equals(""))
				{
					Date date = sessionBean.dfBd.parse(element[34].toString());
					secondTab.dMarriageDate.setValue(date);
				}
				secondTab.txtSpouseName.setValue(element[35].toString());
				secondTab.txtSpouseOccupation.setValue(element[36].toString());
				if(Double.parseDouble(element[37].toString())>0)
				{
					secondTab.ogStatus.setValue("Yes");
					secondTab.amntNumofChild.setValue(element[37].toString());
				}
				else
				{secondTab.ogStatus.setValue("No");}
				fifthTab.cmbBankName.setValue(element[45].toString());
				fifthTab.cmbBankName.setItemCaption(element[45].toString(), element[46].toString());
				fifthTab.cmbBranchName.setValue(element[47].toString());
				fifthTab.cmbBranchName.setItemCaption(element[47].toString(), element[48].toString());
				fifthTab.opgBank.setValue(element[49].toString());
				fifthTab.txtAccountNo.setValue(element[40]);
				fifthTab.txtRoutingNo.setValue(element[69]);
				
				
				
			}
			//Salary Structure
			String sqlSalary = "select vRegisterId,mBasic,mHouseRent,mMedicalAllowance,mClinicalAllowance," +
					" mNonPracticeAllowance,mOtherAllowance,mDearnessAllowance,mConveyanceAllowance,mAttendanceBonus," +
					" mSpecialAllowance,mRoomCharge,mIncomeTax,mProvidentFund,mKallanFund,mKhichuriMeal,mTiffinAllowance,mMobileAllowance" +
					" from tbEmpSalaryStructure where vEmployeeId = '"+employeeId+"' and isCurrent = 1";
			
			System.out.println("Salary :"+sqlSalary);
			List<?> ledSalary = session.createSQLQuery(sqlSalary).list();
            
			if (ledSalary.iterator().hasNext())
			{
				Object[] element = (Object[]) ledSalary.iterator().next();

				firstTab.cmbGrade.setValue(element[0]);

				fifthTab.txtBasicAdd.setValue(df.format(element[1]));
				fifthTab.txtHouseRentAdd.setValue(df.format(element[2]));
				fifthTab.txtMedicalAllowanceAdd.setValue(df.format(element[3]));
				//fifthTab.txtClinicalAllowanceAdd.setValue(df.format(element[4]));
			//	fifthTab.txtNonPracticeAllowanceAdd.setValue(df.format(element[5]));
				fifthTab.txtOtherAllowanceAdd.setValue(df.format(element[6]));
				//fifthTab.txtDearnessAllowanceAdd.setValue(df.format(element[7]));
				fifthTab.txtConveyanceAllowanceAdd.setValue(df.format(element[8]));
				fifthTab.txtAttendanceBonusAdd.setValue(df.format(element[9]));
				fifthTab.txtSpecialAllowanceAdd.setValue(df.format(element[10]));

				fifthTab.txtRoomChargeLess.setValue(df.format(element[11]));
				fifthTab.txtIncomeTaxLess.setValue(df.format(element[12]));
				fifthTab.txtProvidentFundLess.setValue(df.format(element[13]));
				//fifthTab.txtKallanFundLess.setValue(df.format(element[14]));
				//fifthTab.txtKhichuriMealLess.setValue(df.format(element[15]));

				fifthTab.txtTiffinAllowanceAdd.setValue(df.format(element[16]));
				fifthTab.txtMobileAllowanceAdd.setValue(df.format(element[17])+"");
				
				double basic = Double.parseDouble(fifthTab.txtBasicAdd.getValue().toString().isEmpty()?"0":fifthTab.txtBasicAdd.getValue().toString().replaceAll(",", "").trim());
				double house = Double.parseDouble(fifthTab.txtHouseRentAdd.getValue().toString().isEmpty()?"0":fifthTab.txtHouseRentAdd.getValue().toString().replaceAll(",", "").trim());
				double medical = Double.parseDouble(fifthTab.txtMedicalAllowanceAdd.getValue().toString().isEmpty()?"0":fifthTab.txtMedicalAllowanceAdd.getValue().toString().replaceAll(",", "").trim());
				double special = Double.parseDouble(fifthTab.txtSpecialAllowanceAdd.getValue().toString().isEmpty()?"0":fifthTab.txtSpecialAllowanceAdd.getValue().toString().replaceAll(",", "").trim());
				double other = Double.parseDouble(fifthTab.txtOtherAllowanceAdd.getValue().toString().isEmpty()?"0":fifthTab.txtOtherAllowanceAdd.getValue().toString().replaceAll(",", "").trim());
				double convence = Double.parseDouble(fifthTab.txtConveyanceAllowanceAdd.getValue().toString().isEmpty()?"0":fifthTab.txtConveyanceAllowanceAdd.getValue().toString().replaceAll(",", "").trim());
				double mobile = Double.parseDouble(fifthTab.txtMobileAllowanceAdd.getValue().toString().isEmpty()?"0":fifthTab.txtMobileAllowanceAdd.getValue().toString().replaceAll(",", "").trim());

				double gross = (basic+house+medical+special+other+convence+mobile);
				if(gross>0)
				{
					fifthTab.txtTotalGross.setValue(new CommaSeparator().setComma(gross));
				}
			}

			
			//Nominee
			String sqlNominee = "select vNomineeName,vNomineeRelation,iNomineeAge,mPercentage,vBasedOn,vPfId,iEntitlementYear,dPfStartDate" +
					" from tbEmpNomineeInfo where vEmployeeId = '"+employeeId+"'";
			
			System.out.println("Nominee :"+sqlNominee);
			List<?> ledNominee = session.createSQLQuery(sqlNominee).list();

			int i = 0;
			for (Iterator<?> iter = ledNominee.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				/*sixTab.tbltxtNomineeName.get(i).setValue(element[0].toString().replaceAll("#","'"));
				sixTab.tbltxtRelation.get(i).setValue(element[1].toString().replaceAll("#", "'"));
				sixTab.tblamtAge.get(i).setValue(element[2].toString());
				sixTab.tblamtPercent.get(i).setValue(dfZero.format(element[3]));
				sixTab.opgDateType.setValue(element[4]);
				sixTab.txtPFID.setValue(element[5]);
				sixTab.txtYear.setValue(element[6]);
				sixTab.dPfStartDate.setValue(element[7]);*/
				i++;
			}
			
			/*if(sixTab.txtPFID.getValue().toString().isEmpty())
			{
				sixTab.txtPFID.setValue(selectMaxPFId());
			}*/

			//Education
			String sqlEducation = "select vExamName,vGroupName,vInstituteName,vBoardName,vGradePoint,vPassingYear," +
					"vOtherQualification,vComputerSkill from tbEmpEducationInfo where vEmployeeId = '"+employeeId+"'";
			
			System.out.println("Education :"+sqlEducation);
			List<?> ledEducation = session.createSQLQuery(sqlEducation).list();

			i = 0;
			for (Iterator<?> iter = ledEducation.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				thirdTab.tblTxtExam.get(i).setValue(element[0].toString().replaceAll("#", "'"));
				thirdTab.tblTxtGroup.get(i).setValue(element[1].toString().replaceAll("#", "'"));
				thirdTab.tblTxtInstitute.get(i).setValue(element[2].toString().replaceAll("#", "'"));
				thirdTab.tblTxtBoard.get(i).setValue(element[3].toString().replaceAll("#", "'"));
				thirdTab.tblTxtDivision.get(i).setValue(element[4].toString().replaceAll("#", "'"));

				Date date = sessionBean.dfBd.parse("01-01-"+element[5].toString());
				thirdTab.tblDateYear.get(i).setValue(date);

				if(i==0)
				{
					thirdTab.txtOtherQualification.setValue(element[6].toString().replaceAll("#", "'"));
					thirdTab.txtComputerSkill.setValue(element[7].toString().replaceAll("#", "'"));
				}
				i++;
			}

			//Experience
			String sqlExperience = "select vPostName,vCompanyName,dDurationFrom,dDurationTo,vResponsibility" +
					" from tbEmpExperienceInfo where vEmployeeId = '"+employeeId+"'";
		
			System.out.println("Experience :"+sqlExperience);
			List<?> ledExperience = session.createSQLQuery(sqlExperience).list();

			i = 0;
			for (Iterator<?> iter = ledExperience.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				fourthTab.tblTxtPost.get(i).setValue(element[0].toString().replaceAll("#", "'"));
				fourthTab.tblTxtCompanyName.get(i).setValue(element[1].toString().replaceAll("#", "'"));
				fourthTab.tblDateFrom.get(i).setValue(element[2]);
				fourthTab.tblDateTo.get(i).setValue(element[3]);
				fourthTab.tblTxtMajorTask.get(i).setValue(element[4].toString().replaceAll("#", "'"));
				i++;
			}

			isEdit = false;
		}
		catch(Exception ex)
		{           
			showNotification("Error To Find Data ",ex.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	
	private void SaveButtonAction()
	{
		String caption="Do you want to save information?";
		if(isUpdate)
		{
			caption="Do you want to update information?";
		}
		MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, caption, new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
		mb.setStyleName("cwindowMB");
		mb.show(new EventListener()
		{
			public void buttonClicked(ButtonType buttonType)
			{
				if(buttonType == ButtonType.YES)
				{
					if(isUpdate)
					{
						updateEmployeeInfo();
						reportShow();
						addFindEmployee("%");
						textClear();
						tabInit(false);
						btnInit(true);
						isEdit = false;
						isUpdate = false;
						isFind=false;
						
						Notification n=new Notification("All Information Updated Successfully!","",Notification.TYPE_TRAY_NOTIFICATION);
						n.setPosition(Notification.POSITION_TOP_RIGHT);
						showNotification(n);
					}
					else
					{
						insertData();
						addFindEmployee("%");
						textClear();
						tabInit(false);
						btnInit(true);
						isEdit = false;
						isUpdate = false;
						isFind=false;
						
						Notification n=new Notification("All Information Save Successfully!","",Notification.TYPE_TRAY_NOTIFICATION);
						n.setPosition(Notification.POSITION_TOP_RIGHT);
						showNotification(n);
					}
				}
			}
		});
	
	}
	
	private void reportShow()
	{
		ReportDate reportTime = new ReportDate();

		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();

		TextField txtPath=new TextField();
		TextField txtAddress=new TextField();
		
		try
		{
			
			String query="select vEmployeeID,vEmployeeCode,vProximityId,vEmployeeName,vUnitId,vUnitName,vDepartmentId,vDepartmentName,  "
					+ "vSectionId,vSectionName,vDesignationId,vDesignationName,vGender,vEmployeeType,vEmployeeStatus,bStatus,iOtEnable,  "
					+ "(select mBasic from tbEmpSalaryStructure where vEmployeeId=ein.vEmployeeId)mBasic,  "
					+ "(select mHouseRent from tbEmpSalaryStructure where vEmployeeId=ein.vEmployeeId)mHouseRent,  "
					+ "(select mMedicalAllowance from tbEmpSalaryStructure where vEmployeeId=ein.vEmployeeId)mMedicalAllowance,  "
					+ "(select mConveyanceAllowance from tbEmpSalaryStructure where vEmployeeId=ein.vEmployeeId)mConveyanceAllowance,  "
					+ "(select mOtherAllowance from tbEmpSalaryStructure where vEmployeeId=ein.vEmployeeId)mOtherAllowance,  "
					+ "(select mSpecialAllowance from tbEmpSalaryStructure where vEmployeeId=ein.vEmployeeId)mSpecialAllowance,  "
					+ "(select mIncomeTax from tbEmpSalaryStructure where vEmployeeId=ein.vEmployeeId)mIncomeTax,  "
					+ "(select mProvidentFund from tbEmpSalaryStructure where vEmployeeId=ein.vEmployeeId)mProvidentFund,  "
					+ "vBankId,vBankName,vBranchId,vBranchName,vAccountNo,FridayStatus,'Present' as vUDFlag,vUserName,dEntryTime,vUserIp   "
					+ "from tbEmpOfficialPersonalInfo ein where vEmployeeId = '"+firstTab.txtEmployeeID.getValue()+"'   "
					+ "union all select vEmployeeID,vEmployeeCode,vProximityId,vEmployeeName,  "
					+ "(select vUnitId from tbEmpOfficialPersonalInfo  where vEmployeeId=uein.vEmployeeId)vUnitId,  "
					+ "(select vUnitName from tbEmpOfficialPersonalInfo  where vEmployeeId=uein.vEmployeeId)vUnitName,  "
					+ "(select vDepartmentId from tbEmpOfficialPersonalInfo  where vEmployeeId=uein.vEmployeeId)vDepartmentId,  "
					+ "(select vDepartmentName from tbEmpOfficialPersonalInfo  where vEmployeeId=uein.vEmployeeId)vDepartmentName,  "
					+ "(select vSectionId from tbEmpOfficialPersonalInfo  where vEmployeeId=uein.vEmployeeId)vSectionId,  "
					+ "(select vSectionName from tbEmpOfficialPersonalInfo  where vEmployeeId=uein.vEmployeeId)vSectionName,  "
					+ "(select vDesignationId from tbEmpOfficialPersonalInfo  where vEmployeeId=uein.vEmployeeId)vDesignationId,  "
					+ "(select vDesignationName from tbEmpOfficialPersonalInfo  where vEmployeeId=uein.vEmployeeId)vDesignationName,  "
					+ "vGender,vEmployeeType,vEmployeeStatus,bStatus,iOtEnable,mBasic,mHouseRent,mMedicalAllowance,mConveyanceAllowance,mOtherAllowance,  "
					+ "mSpecialAllowance,mIncomeTax,mProvidentFund,vBankId,vBankName,vBranchId,vBranchName,vAccountNo,  "
					+ "iFridayStatus as FridayStatus,'Old' vUDFlag,vUserName,dEntryTime,vUserIp "
					+ "from  tbUdEmployeeInformation uein   where vEmployeeId = '"+firstTab.txtEmployeeID.getValue()+"' "
					+ "order by  vEmployeeName,vEmployeeID,vUDFlag desc,dEntryTime desc";

			
			System.out.println("reportShow: "+query);
			
			if(queryValueCheck(query))
			{				
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				

				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("userName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
				
				hm.put("section",firstTab.cmbSection.getItemCaption(firstTab.cmbSection.getValue()));
				//hm.put("Department",firstTab.cmbDepartment.getItemCaption(firstTab.cmbDepartment.getValue()));
				hm.put("SysDate",reportTime.getTime);
				hm.put("sql", query);
				
				Window win = new ReportViewer(hm,"report/account/hrmModule/rptEditEmployeeInformation.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",true);

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
			showNotification("reportShow "+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private boolean queryValueCheck(String sql)
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
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

	private void insertData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();

		String masterEmployeeId = selectMaxEmpId();

		String imagePathEmployee = imagePath(1,masterEmployeeId)==null? imageLoc:imagePath(1,masterEmployeeId);
		String imagePathBirth = firstTab.birthPath(1,masterEmployeeId)==null? birthImageLoc:firstTab.birthPath(1,masterEmployeeId);
		String imagePathNid = firstTab.nidPath(1,masterEmployeeId)==null? nidImageLoc:firstTab.nidPath(1,masterEmployeeId);
		String imagePathApplication = firstTab.applicationPath(1,masterEmployeeId)==null? applicationImageLoc:firstTab.applicationPath(1,masterEmployeeId);
		String imagePathJoin = firstTab.joinPath(1,masterEmployeeId)==null? joinImageLoc:firstTab.joinPath(1,masterEmployeeId);
		String imagePathCon = firstTab.conPath(1,masterEmployeeId)==null? conImageLoc:firstTab.conPath(1,masterEmployeeId);
		String imagePathServiceAgreement = firstTab.serviceAgreementPath(1,masterEmployeeId)==null? serviceAgreementImageLoc:firstTab.serviceAgreementPath(1,masterEmployeeId);
		
		try
		{
			String officialQuery = "insert into tbEmpOfficialPersonalInfo(vEmployeeId,vEmployeeCode,vFingerId,vProximityId,vEmployeeName," +
					" vReligion,vContactNo,vEmailAddress,vGender,dDateOfBirth,vNationality,vNationalIdNo,vEmployeeType," +
					" vServiceType,bPhysicallyDisable,dApplicationDate,dInterviewDate,dJoiningDate,vConfirmationDate," +
					" vPayScaleId,vPayScaleName,vEmployeeStatus,bStatus,vStatusDate,vEmployeePhoto,vAttachBirth,vAttachNid," +
					" vAttachApplication,vAttachJoining,vAttachConfirmation,vFatherName,vMotherName,vPresentAddress," +
					" vPermanentAddress,vBloodGroup,vMaritalStatus,vMarriageDate,vSpouseName,vSpouseOccupation,iNumberOfChild," +
					" vBankId,vBankName,vBranchId,vBranchName,vAccountNo,iOtEnable,vUserName,vUserIp,dEntryTime,vGradeId,vGradeName,"+
					" vUnitId,vUnitName,vDepartmentId,vDepartmentName,FridayStatus,iHolidayStatus,vShiftId,vShiftName,"+
					" iProbationPeriod,vMoneyTransferType,vFamilyName,vGivenName,vLevelOfEnglish,vSectionId,vSectionName,dValidDate,"+
					" iContactPeriod,vDesignationId,vDesignationName,vCareerPeriod,vAttachServiceAgreement,vRoutingNo) values (" +
					" '"+masterEmployeeId+"', " +
					" '"+(firstTab.txtEmployeeCode.getValue().toString().isEmpty()?"":firstTab.txtEmployeeCode.getValue().toString())+"', " +
					" '"+(firstTab.txtFingerId.getValue().toString().isEmpty()?"":firstTab.txtFingerId.getValue().toString())+"', " +
					" '"+(firstTab.txtProximityId.getValue().toString().isEmpty()?"":firstTab.txtProximityId.getValue().toString())+"', " +
					" '"+firstTab.txtEmployeeName.getValue().toString().trim()+"', " +
					" '"+firstTab.cmbReligion.getValue().toString()+"', " +
					" '"+firstTab.txtContact.getValue().toString().trim()+"', " +
					" '"+(firstTab.txtEmail.getValue().toString().isEmpty()?"":firstTab.txtEmail.getValue().toString())+"', " +
					" '"+firstTab.RadioGender.getValue().toString()+"', "+
					" '"+(firstTab.dDateOfBirth.getValue()!=null?sessionBean.dfDb.format(firstTab.dDateOfBirth.getValue()):sessionBean.dfDb.format(new Date()))+"'," +
					" '"+(firstTab.txtNationality.getValue().toString().isEmpty()?"":firstTab.txtNationality.getValue().toString())+"', " +
					" '"+(firstTab.txtNid.getValue().toString().isEmpty()?"":firstTab.txtNid.getValue().toString())+"', " +
					" '"+(firstTab.cmbEmployeeType.getValue().toString())+"', " +
					" '"+(firstTab.cmbServiceType.getValue().toString())+"', " +
					" '"+(firstTab.chkPhysicallyDisable.booleanValue()?1:0)+"', " +
					" '"+(firstTab.dApplicationDate.getValue()!=null?sessionBean.dfDb.format(firstTab.dApplicationDate.getValue()):sessionBean.dfDb.format(new Date()))+"'," +
					" '"+(firstTab.dInterviewDate.getValue()!=null?sessionBean.dfDb.format(firstTab.dInterviewDate.getValue()):sessionBean.dfDb.format(new Date()))+"'," +
					" '"+(firstTab.dJoiningDate.getValue()!=null?sessionBean.dfDb.format(firstTab.dJoiningDate.getValue()):sessionBean.dfDb.format(new Date()))+"'," +
					" '"+(firstTab.dConfirmationDate.getValue()==null?"":sessionBean.dfDb.format(firstTab.dConfirmationDate.getValue()))+"', " +
					" '0', " +
					" '0', " +
					" '"+firstTab.cmbStatus.getValue().toString()+"', " +
					" '"+(firstTab.cmbStatus.getValue().toString().equals("On Duty")?1:0)+"', " +
					" '"+(firstTab.dStatusDate.getValue()==null?"":sessionBean.dfDb.format(firstTab.dStatusDate.getValue()))+"', " +
					" '"+imagePathEmployee+"', "+
					" '"+imagePathBirth+"', "+
					" '"+imagePathNid+"', "+
					" '"+imagePathApplication+"', "+
					" '"+imagePathJoin+"', "+
					" '"+imagePathCon+"', "+
					" '"+(secondTab.txtFatherName.getValue().toString().trim().isEmpty()?"":secondTab.txtFatherName.getValue().toString().trim())+"', " +
					" '"+(secondTab.txtMotherName.getValue().toString().trim().isEmpty()?"":secondTab.txtMotherName.getValue().toString().trim())+"', " +
					" '"+(secondTab.txtMailing.getValue().toString().trim().isEmpty()?"":secondTab.txtMailing.getValue().toString().replaceAll("'", "#").trim())+"', " +
					" '"+(secondTab.txtPerAddress.getValue().toString().trim().isEmpty()?"":secondTab.txtPerAddress.getValue().toString().replaceAll("'", "#").trim())+"', " +
					" '"+(secondTab.cmbBloodGroup.getValue()==null?"":(secondTab.cmbBloodGroup.getValue()))+"', " +
					" '"+secondTab.ogMaritalStatus.getValue().toString()+"', " +
					" '"+(secondTab.dMarriageDate.getValue()==null?"":sessionBean.dfDb.format(secondTab.dMarriageDate.getValue()))+"', " +
					" '"+(secondTab.txtSpouseName.getValue().toString().trim().isEmpty()?"":secondTab.txtSpouseName.getValue().toString().trim())+"', " +
					" '"+(secondTab.txtSpouseOccupation.getValue().toString().trim().isEmpty()?"":secondTab.txtSpouseOccupation.getValue().toString().trim())+"', " +
					" '"+(secondTab.amntNumofChild.getValue().toString().trim().isEmpty()?"0":secondTab.amntNumofChild.getValue().toString().trim())+"', " +
					" '"+(fifthTab.cmbBankName.getValue()==null?"":(fifthTab.cmbBankName.getValue()))+"', " +
					" '"+(fifthTab.cmbBankName.getValue()==null?"":(fifthTab.cmbBankName.getItemCaption(fifthTab.cmbBankName.getValue())))+"', " +
					" '"+(fifthTab.cmbBranchName.getValue()==null?"":(fifthTab.cmbBranchName.getValue()))+"', " +
					" '"+(fifthTab.cmbBranchName.getValue()==null?"":(fifthTab.cmbBranchName.getItemCaption(fifthTab.cmbBranchName.getValue())))+"', " +
					" '"+(fifthTab.txtAccountNo.getValue().toString().isEmpty()?"":(fifthTab.txtAccountNo.getValue()))+"', " +
					" '"+(firstTab.chkOtEnable.booleanValue()?"1":"0")+"', " +
					" '"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP," +
					" '"+(firstTab.cmbGrade.getValue()==null?"":firstTab.cmbGrade.getValue())+"', " +
					" '"+(firstTab.cmbGrade.getValue()==null?"":firstTab.cmbGrade.getItemCaption(firstTab.cmbGrade.getValue()).toString())+"', " +
					" '"+(firstTab.cmbUnitName.getValue()==null?"":firstTab.cmbUnitName.getValue())+"', " +
					" '"+(firstTab.cmbUnitName.getValue()==null?"":(firstTab.cmbUnitName.getItemCaption(firstTab.cmbUnitName.getValue())))+"' ," +
					" '"+(firstTab.cmbDepartment.getValue()==null?"":firstTab.cmbDepartment.getValue())+"', " +
					" '"+(firstTab.cmbDepartment.getValue()==null?"":(firstTab.cmbDepartment.getItemCaption(firstTab.cmbDepartment.getValue())))+"', " +	
					" '"+(firstTab.checkFridayEnabled.booleanValue()?"1":"0")+"'," +
					" '"+(firstTab.checkHolidayEnabled.booleanValue()?"1":"0")+"'," +
					" '"+(firstTab.cmbShift.getValue()==null?"":firstTab.cmbShift.getValue())+"'," +
					" '"+(firstTab.cmbShift.getValue()==null?"":(firstTab.cmbShift.getItemCaption(firstTab.cmbShift.getValue())))+"'," +
					" '"+(firstTab.cmbProbationPeriod.getValue()==null?"":(firstTab.cmbProbationPeriod.getItemCaption(firstTab.cmbProbationPeriod.getValue())))+"','"+fifthTab.opgBank.getValue().toString()+"'," +
			        " '"+(firstTab.txtFamilyName.getValue().toString().isEmpty()?"":firstTab.txtFamilyName.getValue().toString().replaceAll("'", "#"))+"'," +
			        " '"+(firstTab.txtGivenName.getValue().toString().isEmpty()?"":firstTab.txtGivenName.getValue().toString().replaceAll("'", "#"))+"'," +
			        " '"+(firstTab.cmbLevel.getValue()==null?"":(firstTab.cmbLevel.getItemCaption(firstTab.cmbLevel.getValue())))+"'," +
			        " '"+(firstTab.cmbSection.getValue()==null?"":(firstTab.cmbSection.getValue()))+"'," +
			        " '"+(firstTab.cmbSection.getValue()==null?"":(firstTab.cmbSection.getItemCaption(firstTab.cmbSection.getValue())))+"'," +
			        " '"+(firstTab.dValidDate.getValue()==null?"1900-01-01":sessionBean.dfDb.format(firstTab.dValidDate.getValue()))+"'," +
			        " '"+(firstTab.txtValidYear.getValue().toString().isEmpty()?"0":firstTab.txtValidYear.getValue().toString())+"'," +
			        " '"+(firstTab.cmbDesignation.getValue()==null?"":firstTab.cmbDesignation.getValue())+"'," +
			        " '"+(firstTab.cmbDesignation.getValue()==null?"":(firstTab.cmbDesignation.getItemCaption(firstTab.cmbDesignation.getValue())))+"'," +
					" '"+(firstTab.txtCareerPeriod.getValue().toString().isEmpty()?"":firstTab.txtCareerPeriod.getValue().toString().replaceAll("'", "#"))+"'," +
					" '"+imagePathServiceAgreement+"','"+(fifthTab.txtRoutingNo.getValue().toString().isEmpty()?"":(fifthTab.txtRoutingNo.getValue()))+"') ";
			
			
	
			System.out.println("officialQuery :"+officialQuery);
			
			session.createSQLQuery(officialQuery).executeUpdate();

			/*String sectionQuery = "insert into tbEmpSectionInfo (dChangeDate,vEmployeeId,vEmployeeName,vSectionId," +
					" vSectionName,isCurrent,vUserName,vUserIp,dEntryTime) VALUES (" +
					" CURRENT_TIMESTAMP, "+
					" '"+masterEmployeeId+"', " +
					" '"+firstTab.txtEmployeeName.getValue().toString().trim()+"', " +
					" '"+firstTab.cmbSection.getValue().toString()+"', " +
					" '"+firstTab.cmbSection.getItemCaption(firstTab.cmbSection.getValue()).toString()+"', " +
					" '1','"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP) ";
			System.out.println("sectionQuery :"+sectionQuery);

			session.createSQLQuery(sectionQuery).executeUpdate();

			String designationQuery = "insert into tbEmpDesignationInfo (dChangeDate,vEmployeeId,vEmployeeName,vDesignationId," +
					" vDesignation,isCurrent,vUserName,vUserIp,dEntryTime) VALUES (" +
					" CURRENT_TIMESTAMP, "+
					" '"+masterEmployeeId+"', " +
					" '"+firstTab.txtEmployeeName.getValue().toString().trim()+"', " +
					" '"+firstTab.cmbDesignation.getValue().toString()+"', " +
					" '"+firstTab.cmbDesignation.getItemCaption(firstTab.cmbDesignation.getValue()).toString()+"', " +
					" '1','"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP) ";
			System.out.println("designationQuery :"+designationQuery);
			session.createSQLQuery(designationQuery).executeUpdate();*/

			/**Insert table nominee**/
			/*for(int i = 0; i<sixTab.tbltxtNomineeName.size(); i++)
			{
				
				if(!sixTab.tbltxtNomineeName.get(i).getValue().toString().isEmpty() &&
						!sixTab.tbltxtRelation.get(i).getValue().toString().isEmpty() &&
						!sixTab.tblamtAge.get(i).getValue().toString().isEmpty() &&
						!sixTab.tblamtPercent.get(i).getValue().toString().isEmpty())
				{
					String imagePathPf = sixTab.nomineePath(1,masterEmployeeId+"#"+i,i)==null? pfImageLoc:sixTab.nomineePath(1,masterEmployeeId+"#"+i,i);
					String experienceQuery = "insert into tbEmpNomineeInfo " +
							"(vPfId,vEmployeeId,vEmployeeName,vNomineeName,vNomineeRelation,iNomineeAge,mPercentage,dPfStartDate," +
							"iEntitlementYear,vImage,vBasedOn,vUserId,vUserName,vUserIp,dEntryTime) " +
							"VALUES (" +
							" '"+sixTab.txtPFID.getValue()+"', " +
							" '"+masterEmployeeId+"', " +
							" '"+firstTab.txtEmployeeName.getValue().toString().trim()+"', " +
							" '"+sixTab.tbltxtNomineeName.get(i).getValue()+"', " +
							" '"+sixTab.tbltxtRelation.get(i).getValue().toString()+"', " +
							" '"+sixTab.tblamtAge.get(i).getValue()+"', " +
							" '"+sixTab.tblamtPercent.get(i).getValue()+"', " +
							" '"+sessionBean.dfDb.format(sixTab.dPfStartDate.getValue())+"', " +
							" '"+sixTab.txtYear.getValue()+"', " +
							" '"+imagePathPf+"', " +
							" '"+sixTab.opgDateType.getValue()+"', " +
							" '"+sessionBean.getUserId()+"', " +
							" '"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP) ";
					System.out.println("Nominee: "+experienceQuery);
					session.createSQLQuery(experienceQuery).executeUpdate();
				}
			}*/

			for(int i = 0; i<thirdTab.tblTxtExam.size(); i++)
			{
				if(!thirdTab.tblTxtExam.get(i).getValue().toString().isEmpty() &&
						!thirdTab.tblTxtGroup.get(i).getValue().toString().isEmpty() &&
						!thirdTab.tblTxtInstitute.get(i).getValue().toString().isEmpty() &&
						!thirdTab.tblTxtBoard.get(i).getValue().toString().isEmpty() &&
						!thirdTab.tblTxtDivision.get(i).getValue().toString().isEmpty())
				{
					String educationQuery = "insert into tbEmpEducationInfo (vEmployeeId,vEmployeeName,vExamName,vGroupName,vInstituteName,vBoardName,"+
							" vGradePoint,vPassingYear,vOtherQualification,vComputerSkill,vUserName,vUserIp,dEntryTime) VALUES (" +
							" '"+masterEmployeeId+"', " +
							" '"+firstTab.txtEmployeeName.getValue().toString().trim()+"', " +
							" '"+thirdTab.tblTxtExam.get(i).getValue().toString().replaceAll("'", "#")+"', " +
							" '"+thirdTab.tblTxtGroup.get(i).getValue().toString().replaceAll("'", "#")+"', " +
							" '"+thirdTab.tblTxtInstitute.get(i).getValue().toString().replaceAll("'", "#")+"', " +
							" '"+thirdTab.tblTxtBoard.get(i).getValue().toString().replaceAll("'", "#")+"', " +
							" '"+thirdTab.tblTxtDivision.get(i).getValue().toString().replaceAll("'", "#")+"', " +
							" '"+dfYear.format(thirdTab.tblDateYear.get(i).getValue())+"', " +
							" '"+thirdTab.txtOtherQualification.getValue().toString().replaceAll("'", "#")+"', " +
							" '"+thirdTab.txtComputerSkill.getValue().toString().replaceAll("'", "#")+"', " +
							" '"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP) ";
			
					System.out.println("educationQuery :"+educationQuery);
					
					session.createSQLQuery(educationQuery).executeUpdate();
				}
			}

			for(int i = 0; i<fourthTab.tblTxtCompanyName.size(); i++)
			{
				if(!fourthTab.tblTxtPost.get(i).getValue().toString().isEmpty() &&
						!fourthTab.tblTxtCompanyName.get(i).getValue().toString().isEmpty() &&
						fourthTab.tblDateFrom.get(i).getValue()!=null &&
						fourthTab.tblDateTo.get(i).getValue()!=null &&
						!fourthTab.tblTxtMajorTask.get(i).getValue().toString().isEmpty())
				{
					String experienceQuery = "insert into tbEmpExperienceInfo (vEmployeeId,vEmployeeName,vPostName,vCompanyName," +
							" dDurationFrom,dDurationTo,vResponsibility,vUserName,vUserIp,dEntryTime) VALUES (" +
							" '"+masterEmployeeId+"', " +
							" '"+firstTab.txtEmployeeName.getValue().toString().trim()+"', " +
							" '"+fourthTab.tblTxtPost.get(i).getValue().toString().replaceAll("'", "#")+"', " +
							" '"+fourthTab.tblTxtCompanyName.get(i).getValue().toString().replaceAll("'", "#")+"', " +
							" '"+sessionBean.dfDb.format(fourthTab.tblDateFrom.get(i).getValue())+"', " +
							" '"+sessionBean.dfDb.format(fourthTab.tblDateTo.get(i).getValue())+"', " +
							" '"+fourthTab.tblTxtMajorTask.get(i).getValue().toString().replaceAll("'", "#")+"', " +
							" '"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP) ";
	
					System.out.println("experienceQuery :"+experienceQuery);
					
					session.createSQLQuery(experienceQuery).executeUpdate();
				}
			}

			String salaryQuery = "insert into tbEmpSalaryStructure (dChangeDate,vEmployeeId,vEmployeeName," +
					" vRegisterId,vRegisterName,mBasic,mHouseRent,mMedicalAllowance,mClinicalAllowance,mNonPracticeAllowance," +
					" mSpecialAllowance,mOtherAllowance,mDearnessAllowance," +
					" mConveyanceAllowance,mAttendanceBonus,mRoomCharge,mIncomeTax,mProvidentFund,mKallanFund," +
					" mKhichuriMeal,mTiffinAllowance,isCurrent,vUserName,vUserIp,dEntryTime,mMobileAllowance) VALUES (" +
					" CURRENT_TIMESTAMP, "+
					" '"+masterEmployeeId+"', " +
					" '"+firstTab.txtEmployeeName.getValue().toString().trim()+"', " +
					" '0', " +
					" '0', " +
					" '"+(fifthTab.txtBasicAdd.getValue().toString().isEmpty()?"0":fifthTab.txtBasicAdd.getValue().toString().replaceAll(",", "").trim())+"'," +
					" '"+(fifthTab.txtHouseRentAdd.getValue().toString().isEmpty()?"0":fifthTab.txtHouseRentAdd.getValue().toString().replaceAll(",", "").trim())+"'," +
					" '"+(fifthTab.txtMedicalAllowanceAdd.getValue().toString().isEmpty()?"0":fifthTab.txtMedicalAllowanceAdd.getValue().toString().replaceAll(",", "").trim())+"'," +
					" '0'," +
				    " '"+0+"'," +
					" '"+(fifthTab.txtSpecialAllowanceAdd.getValue().toString().isEmpty()?"0":fifthTab.txtSpecialAllowanceAdd.getValue().toString().replaceAll(",", "").trim())+"'," +
					" '"+(fifthTab.txtOtherAllowanceAdd.getValue().toString().isEmpty()?"0":fifthTab.txtOtherAllowanceAdd.getValue().toString().replaceAll(",", "").trim())+"'," +
					" '0'," +
					" '"+(fifthTab.txtConveyanceAllowanceAdd.getValue().toString().isEmpty()?"0":fifthTab.txtConveyanceAllowanceAdd.getValue().toString().replaceAll(",", "").trim())+"'," +
					" '"+(fifthTab.txtAttendanceBonusAdd.getValue().toString().isEmpty()?"0":fifthTab.txtAttendanceBonusAdd.getValue().toString().replaceAll(",", "").trim())+"'," +
					" '"+(fifthTab.txtRoomChargeLess.getValue().toString().isEmpty()?"0":fifthTab.txtRoomChargeLess.getValue().toString().replaceAll(",", "").trim())+"'," +
					" '"+(fifthTab.txtIncomeTaxLess.getValue().toString().isEmpty()?"0":fifthTab.txtIncomeTaxLess.getValue().toString().replaceAll(",", "").trim())+"'," +
					" '"+(fifthTab.txtProvidentFundLess.getValue().toString().isEmpty()?"0":fifthTab.txtProvidentFundLess.getValue().toString().replaceAll(",", "").trim())+"'," +
					" '0'," +
					" '0'," +
					" '"+(fifthTab.txtTiffinAllowanceAdd.getValue().toString().isEmpty()?"0":fifthTab.txtTiffinAllowanceAdd.getValue().toString().replaceAll(",", "").trim())+"'," +
					" '1','"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP," +
					" '"+(fifthTab.txtMobileAllowanceAdd.getValue().toString().isEmpty()?"0":fifthTab.txtMobileAllowanceAdd.getValue().toString().replaceAll(",", "").trim())+"'" +
							") ";
			System.out.println("salaryQuery :"+salaryQuery);
			session.createSQLQuery(salaryQuery).executeUpdate();
			tx.commit();
		}
		catch(Exception ex)
		{
			tx.rollback();
			showNotification("Error to save "+ex,Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void updateEmployeeInfo()
	{
		
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();

		String masterEmployeeId = (ListSearch.getValue()!=null? ListSearch.getValue().toString() : "");
		//String PfId=sixTab.txtPFID.getValue().toString();
		
		String imagePathEmployee = imagePath(1,masterEmployeeId)==null? imageLoc:imagePath(1,masterEmployeeId);
		String imagePathBirth = firstTab.birthPath(1,masterEmployeeId)==null? birthImageLoc:firstTab.birthPath(1,masterEmployeeId);
		String imagePathNid = firstTab.nidPath(1,masterEmployeeId)==null? nidImageLoc:firstTab.nidPath(1,masterEmployeeId);
		String imagePathApplication = firstTab.applicationPath(1,masterEmployeeId)==null? applicationImageLoc:firstTab.applicationPath(1,masterEmployeeId);
		String imagePathJoin = firstTab.joinPath(1,masterEmployeeId)==null? joinImageLoc:firstTab.joinPath(1,masterEmployeeId);
		String imagePathCon = firstTab.conPath(1,masterEmployeeId)==null? conImageLoc:firstTab.conPath(1,masterEmployeeId);
		String imagePathServiceAgreement = firstTab.serviceAgreementPath(1,masterEmployeeId)==null? serviceAgreementImageLoc:firstTab.serviceAgreementPath(1,masterEmployeeId);
		try
		{
			String udFlag="New";
			if(isUpdate)
			{
				udFlag="Update";
			}
			String InsertOld = " INSERT into tbUdEmployeeInformation (" +
					" vEmployeeId,vEmployeeCode,vFingerId,vProximityId,vEmployeeName,vReligion,vGender,dDateOfBirth,vNationality,vNationalIdNo,vEmployeeType," +
					" vServiceType,bPhysicallyDisable,dApplicationDate,dInterviewDate,dJoiningDate,vConfirmationDate,vPayScaleId,vPayScaleName,vEmployeeStatus,bStatus," +
					" vStatusDate,vAccountNo,vSectionId,vGroupId," +
					" vRegisterId,vRegisterName,mBasic,mHouseRent,mMedicalAllowance,mClinicalAllowance,mNonPracticeAllowance," +
					" mSpecialAllowance,mOtherAllowance,mDearnessAllowance,mConveyanceAllowance,mAttendanceBonus," +
					" mRoomCharge,mIncomeTax,mProvidentFund,mKallanFund,mKhichuriMeal,vUdFlag,iOtEnable,vUserName,vUserIp,dEntryTime,vGradeId,vUnitId," +
					" iFridayStatus,iHolidayStatus,vShiftId,vShiftName,iProbationPeriod,vBankId,vBankName,vBranchId,vBranchName,vMoneyTransferType," +
					" vFamilyName,vGivenName,vLevelOfEnglish,vDepartmentId,vDepartmentName,dValidDate,iContactPeriod,vDesignationId,vDesignationName,vCareerPeriod,vAttachServiceAgreement) "+

					" select ei.vEmployeeId,vEmployeeCode,vFingerId,vProximityId,ei.vEmployeeName,vReligion,vGender,dDateOfBirth,vNationality,vNationalIdNo,vEmployeeType," +
					" vServiceType,bPhysicallyDisable,dApplicationDate,dInterviewDate,dJoiningDate,vConfirmationDate,vPayScaleId,vPayScaleName,vEmployeeStatus,bStatus," +
					" vStatusDate,vAccountNo,vSectionId,'"+0+"', "+
					" vRegisterId,vRegisterName,mBasic,mHouseRent,"+
					" mMedicalAllowance,mClinicalAllowance,'"+0+"',mSpecialAllowance,mOtherAllowance,"+
					" mDearnessAllowance,mConveyanceAllowance,mAttendanceBonus,mRoomCharge,mIncomeTax,"+
					" mProvidentFund,mKallanFund,mKhichuriMeal,'Update',iOtEnable,ei.vUserName,ei.vUserIp,ei.dEntryTime,vGradeId,vUnitId,FridayStatus," +
					" iHolidayStatus,vShiftId,vShiftName,iProbationPeriod,ei.vBankId,ei.vBankName,ei.vBranchId,ei.vBranchName,ei.vMoneyTransferType, "+
					" vFamilyName,vGivenName,vLevelOfEnglish,vDepartmentId,vDepartmentName,dValidDate,iContactPeriod,vDesignationId,vDesignationName,vCareerPeriod,vAttachServiceAgreement "+
					" from tbEmpOfficialPersonalInfo ei inner join "+
					" tbEmpSalaryStructure es on ei.vEmployeeId = es.vEmployeeId where ei.vEmployeeId = '"+masterEmployeeId+"' and" +
					" es.isCurrent = 1";

			System.out.println("InsertOld : "+InsertOld);
			session.createSQLQuery(InsertOld).executeUpdate();
			
			String updateOfficial = "update tbEmpOfficialPersonalInfo set " +
					" vEmployeeCode = '"+(firstTab.txtEmployeeCode.getValue().toString().isEmpty()?"":firstTab.txtEmployeeCode.getValue().toString())+"'," +
					" vFingerId = '"+(firstTab.txtFingerId.getValue().toString().isEmpty()?"":firstTab.txtFingerId.getValue().toString())+"'," +
					" vProximityId = '"+(firstTab.txtProximityId.getValue().toString().isEmpty()?"":firstTab.txtProximityId.getValue().toString())+"'," +
					" vEmployeeName = '"+firstTab.txtEmployeeName.getValue().toString().trim()+"'," +
					" vReligion = '"+firstTab.cmbReligion.getValue().toString()+"'," +
					" vContactNo = '"+firstTab.txtContact.getValue().toString().trim()+"'," +
					" vEmailAddress = '"+(firstTab.txtEmail.getValue().toString().isEmpty()?"":firstTab.txtEmail.getValue().toString())+"'," +
					" vGender = '"+firstTab.RadioGender.getValue().toString()+"'," +
					" dDateOfBirth = '"+(firstTab.dDateOfBirth.getValue()!=null?sessionBean.dfDb.format(firstTab.dDateOfBirth.getValue()):sessionBean.dfDb.format(new Date()))+"'," +
					" vNationality = '"+(firstTab.txtNationality.getValue().toString().isEmpty()?"":firstTab.txtNationality.getValue().toString())+"'," +
					" vNationalIdNo = '"+(firstTab.txtNid.getValue().toString().isEmpty()?"":firstTab.txtNid.getValue().toString())+"'," +
					" vEmployeeType = '"+(firstTab.cmbEmployeeType.getValue().toString())+"'," +
					" vServiceType = '"+(firstTab.cmbServiceType.getValue().toString())+"'," +
					" bPhysicallyDisable = '"+(firstTab.chkPhysicallyDisable.booleanValue()?1:0)+"'," +
					" dApplicationDate = '"+(firstTab.dApplicationDate.getValue()!=null?sessionBean.dfDb.format(firstTab.dApplicationDate.getValue()):sessionBean.dfDb.format(new Date()))+"'," +
					" dInterviewDate = '"+(firstTab.dInterviewDate.getValue()!=null?sessionBean.dfDb.format(firstTab.dInterviewDate.getValue()):sessionBean.dfDb.format(new Date()))+"'," +
					" dJoiningDate = '"+(firstTab.dJoiningDate.getValue()!=null?sessionBean.dfDb.format(firstTab.dJoiningDate.getValue()):sessionBean.dfDb.format(new Date()))+"'," +
					" vConfirmationDate = '"+(firstTab.dConfirmationDate.getValue()==null?"":sessionBean.dfDb.format(firstTab.dConfirmationDate.getValue()))+"'," +
					" vPayScaleId = '0'," +
					" vPayScaleName = '0'," +
					" vEmployeeStatus = '"+firstTab.cmbStatus.getValue().toString()+"'," +
					" bStatus = '"+(firstTab.cmbStatus.getValue().toString().equals("On Duty")?1:0)+"'," +
					" vStatusDate = '"+(firstTab.dStatusDate.getValue()==null?"":sessionBean.dfDb.format(firstTab.dStatusDate.getValue()))+"'," +
					" vEmployeePhoto = '"+imagePathEmployee+"'," +
					" vAttachBirth = '"+imagePathBirth+"'," +
					" vAttachNid = '"+imagePathNid+"'," +
					" vAttachApplication = '"+imagePathApplication+"'," +
					" vAttachJoining = '"+imagePathJoin+"'," +
					" vAttachConfirmation = '"+imagePathCon+"'," +
					" vFatherName = '"+(secondTab.txtFatherName.getValue().toString().trim().isEmpty()?"":secondTab.txtFatherName.getValue().toString().trim())+"'," +
					" vMotherName = '"+(secondTab.txtMotherName.getValue().toString().trim().isEmpty()?"":secondTab.txtMotherName.getValue().toString().trim())+"'," +
					" vPresentAddress = '"+(secondTab.txtMailing.getValue().toString().trim().isEmpty()?"":secondTab.txtMailing.getValue().toString().replaceAll("'", "#").trim())+"'," +
					" vPermanentAddress = '"+(secondTab.txtPerAddress.getValue().toString().trim().isEmpty()?"":secondTab.txtPerAddress.getValue().toString().replaceAll("'", "#").trim())+"'," +
					" vBloodGroup = '"+(secondTab.cmbBloodGroup.getValue()==null?"":(secondTab.cmbBloodGroup.getValue()))+"'," +
					" vMaritalStatus = '"+secondTab.ogMaritalStatus.getValue().toString()+"'," +
					" vMarriageDate = '"+(secondTab.dMarriageDate.getValue()==null?"":sessionBean.dfDb.format(secondTab.dMarriageDate.getValue()))+"'," +
					" vSpouseName = '"+(secondTab.txtSpouseName.getValue().toString().trim().isEmpty()?"":secondTab.txtSpouseName.getValue().toString().trim())+"'," +
					" vSpouseOccupation = '"+(secondTab.txtSpouseOccupation.getValue().toString().trim().isEmpty()?"":secondTab.txtSpouseOccupation.getValue().toString().trim())+"'," +
					" iNumberOfChild = '"+(secondTab.amntNumofChild.getValue().toString().trim().isEmpty()?"0":secondTab.amntNumofChild.getValue().toString().trim())+"'," +
					" vBankId = '"+(fifthTab.cmbBankName.getValue()==null?"":(fifthTab.cmbBankName.getValue()))+"'," +
					" vBankName = '"+(fifthTab.cmbBankName.getValue()==null?"":(fifthTab.cmbBankName.getItemCaption(fifthTab.cmbBankName.getValue())))+"'," +
					" vBranchId = '"+(fifthTab.cmbBranchName.getValue()==null?"":(fifthTab.cmbBranchName.getValue()))+"'," +
					" vBranchName = '"+(fifthTab.cmbBranchName.getValue()==null?"":(fifthTab.cmbBranchName.getItemCaption(fifthTab.cmbBranchName.getValue())))+"'," +
					" vAccountNo = '"+(fifthTab.txtAccountNo.getValue().toString().isEmpty()?"":(fifthTab.txtAccountNo.getValue()))+"'," +
					" vRoutingNo = '"+(fifthTab.txtRoutingNo.getValue().toString().isEmpty()?"":(fifthTab.txtRoutingNo.getValue()))+"'," +
					" iOtEnable = '"+(firstTab.chkOtEnable.booleanValue()?"1":"0")+"'," +
					" vUserName = '"+sessionBean.getUserName()+"'," +
					" vUserIp = '"+sessionBean.getUserIp()+"'," +
					" dEntryTime = CURRENT_TIMESTAMP," +
					" vGradeId = '"+(firstTab.cmbGrade.getValue()==null?"":firstTab.cmbGrade.getValue())+"'," +
					" vGradeName = '"+(firstTab.cmbGrade.getValue()==null?"":firstTab.cmbGrade.getItemCaption(firstTab.cmbGrade.getValue()).toString())+"'," +
					" vUnitId = '"+(firstTab.cmbUnitName.getValue()==null?"":firstTab.cmbUnitName.getValue())+"'," +
					" vUnitName = '"+(firstTab.cmbUnitName.getValue()==null?"":(firstTab.cmbUnitName.getItemCaption(firstTab.cmbUnitName.getValue())))+"'," +
					" vDepartmentId = '"+(firstTab.cmbDepartment.getValue()==null?"":firstTab.cmbDepartment.getValue())+"', " +
					" vDepartmentName = '"+(firstTab.cmbDepartment.getValue()==null?"":(firstTab.cmbDepartment.getItemCaption(firstTab.cmbDepartment.getValue())))+"', " +	
					" FridayStatus = '"+(firstTab.checkFridayEnabled.booleanValue()?"1":"0")+"'," +
					" iHolidayStatus = '"+(firstTab.chkOtEnable.booleanValue()?"1":"0")+"'," +
					" vShiftId = '"+(firstTab.cmbShift.getValue()==null?"":firstTab.cmbShift.getValue())+"'," +
					" vShiftName = '"+(firstTab.cmbShift.getValue()==null?"":(firstTab.cmbShift.getItemCaption(firstTab.cmbShift.getValue())))+"'," +
					" iProbationPeriod = '"+(firstTab.cmbProbationPeriod.getValue()==null?"":(firstTab.cmbProbationPeriod.getItemCaption(firstTab.cmbProbationPeriod.getValue())))+"'," +
					" vMoneyTransferType = '"+fifthTab.opgBank.getValue().toString()+"'," +
					" vFamilyName = '"+(firstTab.txtFamilyName.getValue().toString().isEmpty()?"":firstTab.txtFamilyName.getValue().toString().replaceAll("'", "#"))+"'," +
					" vGivenName = '"+(firstTab.txtGivenName.getValue().toString().isEmpty()?"":firstTab.txtGivenName.getValue().toString().replaceAll("'", "#"))+"'," +
					" vLevelOfEnglish = '"+(firstTab.cmbLevel.getValue()==null?"":(firstTab.cmbLevel.getItemCaption(firstTab.cmbLevel.getValue())))+"'," +
					" vSectionId = '"+(firstTab.cmbSection.getValue()==null?"":(firstTab.cmbSection.getValue()))+"'," +
					" vSectionName = '"+(firstTab.cmbSection.getValue()==null?"":(firstTab.cmbSection.getItemCaption(firstTab.cmbSection.getValue())))+"'," +
					" dValidDate = '"+(firstTab.dValidDate.getValue()==null?"1900-01-01":sessionBean.dfDb.format(firstTab.dValidDate.getValue()))+"'," +
					" iContactPeriod = '"+(firstTab.txtValidYear.getValue().toString().isEmpty()?"0":firstTab.txtValidYear.getValue().toString())+"'," +
					" vDesignationId = '"+(firstTab.cmbDesignation.getValue()==null?"":(firstTab.cmbDesignation.getValue()))+"'," +
					" vDesignationName = '"+(firstTab.cmbDesignation.getValue()==null?"":(firstTab.cmbDesignation.getItemCaption(firstTab.cmbDesignation.getValue())))+"'," +
					" vCareerPeriod = '"+(firstTab.txtCareerPeriod.getValue().toString().isEmpty()?"":firstTab.txtCareerPeriod.getValue().toString().replaceAll("'","#"))+"'," +
					" vAttachServiceAgreement = '"+imagePathServiceAgreement+"'" +
					" where vEmployeeId = '"+masterEmployeeId+"' ";

		
			
			System.out.println("updateOfficial : "+updateOfficial);
			session.createSQLQuery(updateOfficial).executeUpdate();

			/*String updateSection = "update tbEmpSectionInfo set " +
					" dChangeDate = CURRENT_TIMESTAMP," +
					" vEmployeeName = '"+firstTab.txtEmployeeName.getValue().toString().trim()+"'," +
					" vSectionId = '"+firstTab.cmbSection.getValue().toString()+"'," +
					" vSectionName = '"+firstTab.cmbSection.getItemCaption(firstTab.cmbSection.getValue()).toString()+"'," +
					" vUserName = '"+sessionBean.getUserName()+"'," +
					" vUserIp = '"+sessionBean.getUserIp()+"'," +
					" dEntryTime = CURRENT_TIMESTAMP"+
					" where vEmployeeId = '"+masterEmployeeId+"' and isCurrent = 1 ";

			System.out.println("updateSection :"+updateSection);
			session.createSQLQuery(updateSection).executeUpdate();

			String updateDesig = "update tbEmpDesignationInfo set " +
					" dChangeDate = CURRENT_TIMESTAMP," +
					" vEmployeeName = '"+firstTab.txtEmployeeName.getValue().toString().trim()+"'," +
					" vDesignationId = '"+firstTab.cmbDesignation.getValue().toString()+"'," +
					" vDesignation = '"+firstTab.cmbDesignation.getItemCaption(firstTab.cmbDesignation.getValue()).toString()+"'," +
					" vUserName = '"+sessionBean.getUserName()+"'," +
					" vUserIp = '"+sessionBean.getUserIp()+"'," +
					" dEntryTime = CURRENT_TIMESTAMP"+
					" where vEmployeeId = '"+masterEmployeeId+"' and isCurrent = 1 ";

			System.out.println("updateDesig :"+updateDesig);
			
			session.createSQLQuery(updateDesig).executeUpdate();*/

			String updateSalary = "update tbEmpSalaryStructure set" +
					" dChangeDate = CURRENT_TIMESTAMP," +
					" vEmployeeName = '"+firstTab.txtEmployeeName.getValue().toString().trim()+"'," +
					" vRegisterId = '0'," +
					" vRegisterName = '0'," +
					" mBasic = '"+(fifthTab.txtBasicAdd.getValue().toString().isEmpty()?"0":fifthTab.txtBasicAdd.getValue().toString().replaceAll(",", "").trim())+"'," +
					" mHouseRent = '"+(fifthTab.txtHouseRentAdd.getValue().toString().isEmpty()?"0":fifthTab.txtHouseRentAdd.getValue().toString().replaceAll(",", "").trim())+"'," +
					" mMedicalAllowance = '"+(fifthTab.txtMedicalAllowanceAdd.getValue().toString().isEmpty()?"0":fifthTab.txtMedicalAllowanceAdd.getValue().toString().replaceAll(",", "").trim())+"'," +
					" mClinicalAllowance = '0'," +
					" mNonPracticeAllowance = '"+0+"'," +
					" mSpecialAllowance = '"+(fifthTab.txtSpecialAllowanceAdd.getValue().toString().isEmpty()?"0":fifthTab.txtSpecialAllowanceAdd.getValue().toString().replaceAll(",", "").trim())+"'," +
					" mOtherAllowance = '"+(fifthTab.txtOtherAllowanceAdd.getValue().toString().isEmpty()?"0":fifthTab.txtOtherAllowanceAdd.getValue().toString().replaceAll(",", "").trim())+"'," +
					" mDearnessAllowance = '0'," +
					" mConveyanceAllowance = '"+(fifthTab.txtConveyanceAllowanceAdd.getValue().toString().isEmpty()?"0":fifthTab.txtConveyanceAllowanceAdd.getValue().toString().replaceAll(",", "").trim())+"'," +
					" mAttendanceBonus = '"+(fifthTab.txtAttendanceBonusAdd.getValue().toString().isEmpty()?"0":fifthTab.txtAttendanceBonusAdd.getValue().toString().replaceAll(",", "").trim())+"'," +
					" mRoomCharge = '"+(fifthTab.txtRoomChargeLess.getValue().toString().isEmpty()?"0":fifthTab.txtRoomChargeLess.getValue().toString().replaceAll(",", "").trim())+"'," +
					" mIncomeTax = '"+(fifthTab.txtIncomeTaxLess.getValue().toString().isEmpty()?"0":fifthTab.txtIncomeTaxLess.getValue().toString().replaceAll(",", "").trim())+"'," +
					" mProvidentFund = '"+(fifthTab.txtProvidentFundLess.getValue().toString().isEmpty()?"0":fifthTab.txtProvidentFundLess.getValue().toString().replaceAll(",", "").trim())+"'," +					
					" mKallanFund = '0'," +
					" mKhichuriMeal = '0'," +
					" mTiffinAllowance = '"+(fifthTab.txtTiffinAllowanceAdd.getValue().toString().isEmpty()?"0":fifthTab.txtTiffinAllowanceAdd.getValue().toString().replaceAll(",", "").trim())+"'," +
					" mMobileAllowance = '"+(fifthTab.txtMobileAllowanceAdd.getValue().toString().isEmpty()?"0":fifthTab.txtMobileAllowanceAdd.getValue().toString().replaceAll(",", "").trim())+"'," +
					" vUserName = '"+sessionBean.getUserName()+"'," +
					" vUserIp = '"+sessionBean.getUserIp()+"'," +
					" dEntryTime = CURRENT_TIMESTAMP" +
					" where vEmployeeId = '"+masterEmployeeId+"' and isCurrent = 1 ";

			System.out.println("updateSalary :"+updateSalary);
			
			session.createSQLQuery(updateSalary).executeUpdate();

			// Table data delete & insert
			String deleteNominee = "delete from tbEmpNomineeInfo where vEmployeeId = '"+masterEmployeeId+"'";
			//System.out.println("deleteNominee : "+deleteNominee);
			session.createSQLQuery(deleteNominee).executeUpdate();

			String deleteEducation = "delete from tbEmpEducationInfo where vEmployeeId = '"+masterEmployeeId+"'";
			//System.out.println("deleteEducation : "+deleteEducation);
			session.createSQLQuery(deleteEducation).executeUpdate();

			String deleteExperience = "delete from tbEmpExperienceInfo where vEmployeeId = '"+masterEmployeeId+"'";
			//System.out.println("deleteExperience : "+deleteExperience);
			session.createSQLQuery(deleteExperience).executeUpdate();
			
			

			/*for(int i = 0; i<sixTab.tbltxtNomineeName.size(); i++)
			{
				if(!sixTab.tbltxtNomineeName.get(i).getValue().toString().isEmpty())
				{
					String imagePathPf = sixTab.nomineePath(1,masterEmployeeId+"#"+i,i)==null? pfImageLoc:sixTab.nomineePath(1,masterEmployeeId+"#"+i,i);
					String udNomineeQuery = "insert into tbUdEmpNomineeInfo (vPfId,vEmployeeId,vEmployeeName,vNomineeName,vNomineeRelation," +
							" iNomineeAge,mPercentage,vFlag,dPfStartDate,iEntitlementYear,vImage,vBasedOn,vUserId,vUserName,vUserIp,dEntryTime) VALUES (" +
							" '"+sixTab.txtPFID.getValue().toString()+"', '"+masterEmployeeId+"'," +
							" '"+firstTab.txtEmployeeName.getValue().toString().trim().replaceAll("'", "#")+"', " +
							" '"+sixTab.tbltxtNomineeName.get(i).getValue().toString().replaceAll("'", "#")+"', " +
							" '"+sixTab.tbltxtRelation.get(i).getValue().toString().replaceAll("'", "#")+"', " +
							" '"+sixTab.tblamtAge.get(i).getValue().toString()+"', " +
							" '"+sixTab.tblamtPercent.get(i).getValue().toString()+"', " +
							" '"+udFlag+"', " +
							" '"+sessionBean.dfDb.format(sixTab.dPfStartDate.getValue())+"',"+
							" '"+(sixTab.txtYear.getValue().toString().isEmpty()?"0":sixTab.txtYear.getValue())+"',"+
							" '"+imagePathPf+"',"+
							" '"+sixTab.opgDateType.getValue()+"',"+
							" '"+sessionBean.getUserId()+"',"+
							" '"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP) ";
					System.out.println("UdnomineeQuery :"+udNomineeQuery);
					//session.createSQLQuery(udNomineeQuery).executeUpdate();
				}
			}*/
			
			//String deletePF = "delete from tbEmpNomineeInfo where vEmployeeId = '"+masterEmployeeId+"'";
			//System.out.println("deletePF : "+deletePF);
			//session.createSQLQuery(deletePF).executeUpdate();
			
			/*for(int i = 0; i<sixTab.tbltxtNomineeName.size(); i++)
			{
				
				if(!sixTab.tbltxtNomineeName.get(i).getValue().toString().isEmpty() &&
						!sixTab.tbltxtRelation.get(i).getValue().toString().isEmpty() &&
						!sixTab.tblamtAge.get(i).getValue().toString().isEmpty() &&
						!sixTab.tblamtPercent.get(i).getValue().toString().isEmpty())
				{
					String imagePathPf = sixTab.nomineePath(1,masterEmployeeId+"#"+i,i)==null? pfImageLoc:sixTab.nomineePath(1,masterEmployeeId+"#"+i,i);
					String experienceQuery = "insert into tbEmpNomineeInfo " +
							"(vPfId,vEmployeeId,vEmployeeName,vNomineeName,vNomineeRelation,iNomineeAge,mPercentage,dPfStartDate," +
							"iEntitlementYear,vImage,vBasedOn,vUserId,vUserName,vUserIp,dEntryTime) " +
							"VALUES (" +
							" '"+PfId+"', " +
							" '"+masterEmployeeId+"', " +
							" '"+firstTab.txtEmployeeName.getValue().toString().trim()+"', " +
							" '"+sixTab.tbltxtNomineeName.get(i).getValue()+"', " +
							" '"+sixTab.tbltxtRelation.get(i).getValue().toString()+"', " +
							" '"+sixTab.tblamtAge.get(i).getValue()+"', " +
							" '"+sixTab.tblamtPercent.get(i).getValue()+"', " +
							" '"+sessionBean.dfDb.format(sixTab.dPfStartDate.getValue())+"', " +
							" '"+sixTab.txtYear.getValue()+"', " +
							" '"+imagePathPf+"', " +
							" '"+sixTab.opgDateType.getValue()+"', " +
							" '"+sessionBean.getUserId()+"', " +
							" '"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP) ";
					System.out.println("Nominee: "+experienceQuery);
					session.createSQLQuery(experienceQuery).executeUpdate();
				}
			}*/
			for(int i = 0; i<thirdTab.tblTxtExam.size(); i++)
			{
				if(!thirdTab.tblTxtExam.get(i).getValue().toString().isEmpty() &&
						!thirdTab.tblTxtGroup.get(i).getValue().toString().isEmpty() &&
						!thirdTab.tblTxtInstitute.get(i).getValue().toString().isEmpty() &&
						!thirdTab.tblTxtBoard.get(i).getValue().toString().isEmpty() &&
						!thirdTab.tblTxtDivision.get(i).getValue().toString().isEmpty())
				{
					String educationQuery = "insert into tbEmpEducationInfo (vEmployeeId,vEmployeeName,vExamName,vGroupName,vInstituteName,vBoardName,"+
							" vGradePoint,vPassingYear,vOtherQualification,vComputerSkill,vUserName,vUserIp,dEntryTime) VALUES (" +
							" '"+masterEmployeeId+"', " +
							" '"+firstTab.txtEmployeeName.getValue().toString().trim()+"', " +
							" '"+thirdTab.tblTxtExam.get(i).getValue().toString().replaceAll("'", "#")+"', " +
							" '"+thirdTab.tblTxtGroup.get(i).getValue().toString().replaceAll("'", "#")+"', " +
							" '"+thirdTab.tblTxtInstitute.get(i).getValue().toString().replaceAll("'", "#")+"', " +
							" '"+thirdTab.tblTxtBoard.get(i).getValue().toString().replaceAll("'", "#")+"', " +
							" '"+thirdTab.tblTxtDivision.get(i).getValue().toString().replaceAll("'", "#")+"', " +
							" '"+dfYear.format(thirdTab.tblDateYear.get(i).getValue())+"', " +
							" '"+thirdTab.txtOtherQualification.getValue().toString().replaceAll("'", "#")+"', " +
							" '"+thirdTab.txtComputerSkill.getValue().toString().replaceAll("'", "#")+"', " +
							" '"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP) ";
					//System.out.println("educationQuery :"+educationQuery);
					session.createSQLQuery(educationQuery).executeUpdate();
				}
			}

			for(int i = 0; i<fourthTab.tblTxtCompanyName.size(); i++)
			{
				if(!fourthTab.tblTxtPost.get(i).getValue().toString().isEmpty() &&
						!fourthTab.tblTxtCompanyName.get(i).getValue().toString().isEmpty() &&
						fourthTab.tblDateFrom.get(i).getValue()!=null &&
						fourthTab.tblDateTo.get(i).getValue()!=null &&
						!fourthTab.tblTxtMajorTask.get(i).getValue().toString().isEmpty())
				{
					String experienceQuery = "insert into tbEmpExperienceInfo (vEmployeeId,vEmployeeName,vPostName,vCompanyName," +
							" dDurationFrom,dDurationTo,vResponsibility,vUserName,vUserIp,dEntryTime) VALUES (" +
							" '"+masterEmployeeId+"', " +
							" '"+firstTab.txtEmployeeName.getValue().toString().trim()+"', " +
							" '"+fourthTab.tblTxtPost.get(i).getValue().toString().replaceAll("'", "#")+"', " +
							" '"+fourthTab.tblTxtCompanyName.get(i).getValue().toString().replaceAll("'", "#")+"', " +
							" '"+sessionBean.dfDb.format(fourthTab.tblDateFrom.get(i).getValue())+"', " +
							" '"+sessionBean.dfDb.format(fourthTab.tblDateTo.get(i).getValue())+"', " +
							" '"+fourthTab.tblTxtMajorTask.get(i).getValue().toString().replaceAll("'", "#")+"', " +
							" '"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP) ";
					//System.out.println("experienceQuery :"+experienceQuery);
					session.createSQLQuery(experienceQuery).executeUpdate();
				}
			}

			tx.commit();
		}
		catch(Exception ex)
		{
			tx.rollback();
			showNotification("Error to update data "+ex,Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	public void employeeImage(String img)
	{
		File  fileStu_I = new File(img);

		Embedded eStu_I = new Embedded("",new FileResource(fileStu_I, getApplication()));
		eStu_I.requestRepaint();
		eStu_I.setWidth("110px");
		eStu_I.setHeight("140px");

		firstTab.Image.image.removeAllComponents();
		firstTab.Image.image.addComponent(eStu_I);
	}

	private String imagePath(int flag,String str)
	{
		String basePath = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/";
		String employeeImage = null;

		if(flag==1)
		{
			// image move
			if(firstTab.Image.fileName.trim().length()>0)
				try 
			{
					if(firstTab.Image.fileName.toString().endsWith(".jpg"))
					{
						String path = str;
						fileMove(basePath+firstTab.Image.fileName.trim(),employeeImages+path+".jpg");
						employeeImage = employeeImages+path+".jpg";
					}
			}
			catch(IOException e) 
			{
				e.printStackTrace();
			}
			return employeeImage;
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
		catch(Exception exp)
		{

		}
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

	public void sectionLink()
	{
		Window win = new SectionInformation(sessionBean,"SectionInformation");
		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				firstTab.addSectionInfo();
			}
		});
		this.getParent().addWindow(win);
	}

	public void DepartmentLink()
	{
		Window win = new DepartmentInformation(sessionBean,"departmentInformation");
		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				firstTab.addDepartmentInfo();
			}
		});
		this.getParent().addWindow(win);
	}
	public void UnitLink()
	{
		Window win = new ProjectInformation(sessionBean,"ProjectInformation");
		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				firstTab.addUnitInfo();			}
		});
		this.getParent().addWindow(win);
	}
	public void shiftLink()
	{
		Window win=new ShiftInformation(sessionBean,"ShiftInformation");
		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() {
			
			public void windowClose(CloseEvent e) {
				firstTab.addShiftInfo();
			}
		});
		this.getParent().addWindow(win);
	}
	public void designationLink()
	{
		Window win = new DesignationInformation(sessionBean,"designationInformation");
		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				firstTab.addDesignationInfo();
			}
		});
		this.getParent().addWindow(win);
	}

	public void groupShiftLink()
	{
		Window win = new GradeInformation(sessionBean,"GradeInformation");

		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				firstTab.addShiftGroupInfo();
			}
		});
		this.getParent().addWindow(win);
	}

	public void salaryRegisterLink()
	{
		Window win = new GradeInformation(sessionBean,"GradeInformation");
		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				firstTab.addSalaryRegister();
			}
		});
		this.getParent().addWindow(win);
	}


	private AbsoluteLayout buildMainLayout() 
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("1050px");
		setHeight("620px");

		this.setResizable(false);
		
		opgActiveInactive=new OptionGroup("",type);
		opgActiveInactive.select("Active");
		opgActiveInactive.setImmediate(true);
		opgActiveInactive.setStyleName("horizontal");
		mainLayout.addComponent(opgActiveInactive, "top:10.0px; left:5.0px;");

		txtSearch = new TextField();
		txtSearch.setImmediate(true);
		txtSearch.setWidth("190px");
		txtSearch.setHeight("-1px");
		txtSearch.setCaption("Type and hit enter to find employee");
		txtSearch.setDescription("Find Employee using FingerId or ProximityId or Name or Religion or Father's Name or Contact Number");
		mainLayout.addComponent(txtSearch, "top:50px;left:3px;");

		ListSearch = new ListSelect();
		ListSearch.setImmediate(true);
		ListSearch.setNullSelectionAllowed(false);
		ListSearch.setNewItemsAllowed(false);
		ListSearch.setWidth("190px");
		ListSearch.setHeight("86%");
		mainLayout.addComponent(ListSearch, "top:88px;left:3px;");

	//	mainLayout.addComponent(new Label("Note : Please avoid <b><Font Color='#CD0606' size='3px'> ' </Font></b> this sign.",Label.CONTENT_XHTML), "bottom:30.0px; left:17.0px;");
		mainLayout.addComponent(new Label("All <b><Font Color='#CD0606' size='3px'>*</Font></b> marks are mendatory.",Label.CONTENT_XHTML), "bottom:30.0px; left:17.0px;");

		mainLayout.addComponent(button, "top:543.0px;left:400.0px;");
		if(sessionBean.getUserId().equals("U-9"))
		{
			button.btnNew.setVisible(false);
		}

		tabSheet.setStyleName("tabsheet-content");
		tabSheet.setHeight("92%");
		mainLayout.addComponent(tabSheet, "top:0.0px;left:200.0px;");

		return mainLayout;
	}

	public void tabInit(boolean t)
	{
		txtSearch.setEnabled(!t);
		ListSearch.setEnabled(!t);
		firstTab.setEnabled(t);
		secondTab.setEnabled(t);
		thirdTab.setEnabled(t);
		fourthTab.setEnabled(t);  
		fifthTab.setEnabled(t);
		//sixTab.mainLayout.setEnabled(t);
		
	}

	private void textClear()
	{
		txtSearch.setValue("");
		ListSearch.setValue(null);
		employeeCode = "";
		
		firstTabClear();
		secondTabClear();
		thirdTabClear();
		forthTabClear();
		fifthTabClear();
		sixTabClear();
	}

	public void firstTabClear()
	{
		firstTab.btnDateofBirth.actionCheck = false;
		firstTab.btnBirthPreview.setCaption("attach");

		firstTab.btnNid.actionCheck = false;
		firstTab.btnNidPreview.setCaption("attach");

		firstTab.btnAppDate.actionCheck = false;
		firstTab.btnApplicationPreview.setCaption("attach");

		firstTab.btnJoiningDate.actionCheck = false;
		firstTab.btnJoinPreview.setCaption("attach");

		firstTab.btnConfirmdate.actionCheck = false;
		firstTab.btnConPreview.setCaption("attach");
		
		firstTab.btnServiceAgreement.actionCheck = false;
		firstTab.btnServiceAgreementPreview.setCaption("attach");
		

		firstTab.txtEmployeeID.setValue("");
		firstTab.txtEmployeeCode.setValue("");
		firstTab.txtFingerId.setValue("");
		firstTab.txtProximityId.setValue("");
		firstTab.txtEmployeeName.setValue("");
		//firstTab.chkOtEnable.setValue(false);

		firstTab.cmbReligion.setValue(null);
		firstTab.txtContact.setValue("");
		firstTab.txtEmail.setValue("");

		firstTab.RadioGender.setValue("Male");
		firstTab.dDateOfBirth.setValue(new java.util.Date());
		firstTab.txtNationality.setValue("Bangladeshi");
		firstTab.txtNid.setValue("");

		firstTab.cmbEmployeeType.setValue(null);
		//firstTab.cmbEmployeeShiftGroup.setValue(null);
		firstTab.cmbServiceType.setValue(null);
		firstTab.dApplicationDate.setValue(new java.util.Date());
		firstTab.dInterviewDate.setValue(new java.util.Date());
		firstTab.dJoiningDate.setValue(new java.util.Date());
		firstTab.dConfirmationDate.setValue(null);
		firstTab.dStatusDate.setValue(null);
		firstTab.cmbSection.setValue(null);
		firstTab.cmbDesignation.setValue(null);
		firstTab.cmbGrade.setValue(null);
		firstTab.cmbDepartment.setValue(null);
		firstTab.cmbStatus.setValue(null);
		firstTab.chkPhysicallyDisable.setValue(false);
		firstTab.cmbShift.setValue(null);
		firstTab.Image.image.removeAllComponents();
		firstTab.checkFridayEnabled.setValue(false);
		firstTab.checkHolidayEnabled.setValue(false);
		firstTab.chkOtEnable.setValue(false);
		firstTab.cmbProbationPeriod.setValue(null);
		firstTab.txtFamilyName.setValue("");
		firstTab.txtGivenName.setValue("");
		firstTab.txtValidYear.setValue("");
		firstTab.cmbLevel.setValue(null);
		firstTab.dValidDate.setValue(new java.util.Date());
		firstTab.cmbUnitName.setValue(null);
		firstTab.txtCareerPeriod.setValue("");
	}

	public void secondTabClear()
	{
		secondTab.txtFatherName.setValue("");
		secondTab.txtMotherName.setValue("");
		secondTab.txtPerAddress.setValue("");
		secondTab.txtMailing.setValue("");
		secondTab.cmbBloodGroup.setValue(null);
		secondTab.ogMaritalStatus.setValue("Unmarried");
		secondTab.dMarriageDate.setValue(null);
		secondTab.txtSpouseName.setValue("");
		secondTab.txtSpouseOccupation.setValue("");
		secondTab.amntNumofChild.setValue("");
	}

	public void thirdTabClear()
	{
		for(int i = 0; i<thirdTab.tblTxtExam.size(); i++)
		{
			thirdTab.tblTxtExam.get(i).setValue("");
			thirdTab.tblTxtGroup.get(i).setValue("");
			thirdTab.tblTxtInstitute.get(i).setValue("");
			thirdTab.tblTxtBoard.get(i).setValue("");
			thirdTab.tblTxtDivision.get(i).setValue("");
		}

		thirdTab.txtOtherQualification.setValue("");
		thirdTab.txtComputerSkill.setValue("");
	}

	public void forthTabClear()
	{
		for(int i = 0; i<fourthTab.tblTxtPost.size(); i++)
		{
			fourthTab.tblTxtPost.get(i).setValue("");
			fourthTab.tblTxtCompanyName.get(i).setValue("");
			fourthTab.tblDateFrom.get(i).setValue(new java.util.Date());
			fourthTab.tblDateTo.get(i).setValue(new java.util.Date());
			fourthTab.tblTxtMajorTask.get(i).setValue("");
		}
	}

	public void fifthTabClear()
	{
		fifthTab.txtBasicAdd.setValue("");
		fifthTab.txtHouseRentAdd.setValue("");
		fifthTab.txtMedicalAllowanceAdd.setValue("");
	    //fifthTab.txtClinicalAllowanceAdd.setValue("");
	    //fifthTab.txtNonPracticeAllowanceAdd.setValue("");
		fifthTab.txtOtherAllowanceAdd.setValue("");
		//fifthTab.txtDearnessAllowanceAdd.setValue("");
		fifthTab.txtConveyanceAllowanceAdd.setValue("");
		fifthTab.txtSpecialAllowanceAdd.setValue("");
		fifthTab.txtAttendanceBonusAdd.setValue("0");
		fifthTab.txtTiffinAllowanceAdd.setValue("0");

		fifthTab.txtRoomChargeLess.setValue("0");
		fifthTab.txtIncomeTaxLess.setValue("");
		fifthTab.txtProvidentFundLess.setValue("");
		//fifthTab.txtKallanFundLess.setValue("20");
		//fifthTab.txtKhichuriMealLess.setValue("100");
		fifthTab.txtMobileAllowanceAdd.setValue("0");

		fifthTab.cmbBankName.setValue(null);
		fifthTab.cmbBranchName.setValue(null);
		fifthTab.opgBank.setValue("Cash");
		fifthTab.txtAccountNo.setValue("");
		fifthTab.txtTotalGross.setValue("");
	}
	public void sixTabClear()
	{
		/*sixTab.txtPFID.setValue("");
		sixTab.txtYear.setValue("");
		for(int i = 0; i<sixTab.tbltxtNomineeName.size(); i++)
		{
			sixTab.tblamtAge.get(i).setValue("");
			sixTab.tblamtPercent.get(i).setValue("");
			sixTab.tbltxtNomineeName.get(i).setValue("");
			sixTab.tbltxtRelation.get(i).setValue("");
		}*/
	}
	public void focusMove()
	{
		allComp.add(firstTab.txtEmployeeCode);
		allComp.add(firstTab.txtFingerId);
		allComp.add(firstTab.txtProximityId);
		allComp.add(firstTab.txtEmployeeName);
		allComp.add(firstTab.txtFamilyName);
		allComp.add(firstTab.txtGivenName);
		allComp.add(firstTab.cmbReligion);
		allComp.add(firstTab.txtContact);
		allComp.add(firstTab.txtEmail);
		allComp.add(firstTab.RadioGender);
		allComp.add(firstTab.dDateOfBirth);
		allComp.add(firstTab.txtNationality);
		allComp.add(firstTab.txtNid);
		allComp.add(firstTab.cmbEmployeeType);
		allComp.add(firstTab.cmbLevel);
		allComp.add(firstTab.cmbServiceType);

		allComp.add(firstTab.dApplicationDate);
		allComp.add(firstTab.dInterviewDate);
		allComp.add(firstTab.dJoiningDate);
		allComp.add(firstTab.dConfirmationDate);
		allComp.add(firstTab.txtValidYear);
		allComp.add(firstTab.dValidDate);
		
		allComp.add(firstTab.cmbUnitName);
		allComp.add(firstTab.cmbDepartment);
		allComp.add(firstTab.cmbSection);
		allComp.add(firstTab.cmbDesignation);
		//allComp.add(firstTab.cmbEmployeeShiftGroup);
		allComp.add(firstTab.cmbGrade);
		allComp.add(firstTab.cmbStatus);
		allComp.add(firstTab.chkOtEnable);

		allComp.add(secondTab.txtFatherName);
		allComp.add(secondTab.txtMotherName);
		allComp.add(secondTab.txtPerAddress);
		allComp.add(secondTab.txtMailing);
		allComp.add(secondTab.cmbBloodGroup);
		allComp.add(secondTab.ogMaritalStatus);
		allComp.add(secondTab.dMarriageDate);
		allComp.add(secondTab.txtSpouseName);
		allComp.add(secondTab.txtSpouseOccupation);
		allComp.add(secondTab.amntNumofChild);

		for(int i=0;i<thirdTab.tblTxtExam.size();i++)
		{
			allComp.add(thirdTab.tblTxtExam.get(i));
			allComp.add(thirdTab.tblTxtGroup.get(i));
			allComp.add(thirdTab.tblTxtInstitute.get(i));
			allComp.add(thirdTab.tblTxtBoard.get(i));
			allComp.add(thirdTab.tblTxtDivision.get(i));
		}
		allComp.add(thirdTab.txtOtherQualification);
		allComp.add(thirdTab.txtComputerSkill);

		for(int i=0;i<fourthTab.tblTxtPost.size();i++)
		{
			allComp.add(fourthTab.tblTxtPost.get(i));
			allComp.add(fourthTab.tblTxtCompanyName.get(i));
			allComp.add(fourthTab.tblDateFrom.get(i));
			allComp.add(fourthTab.tblDateTo.get(i));
			allComp.add(fourthTab.tblTxtMajorTask.get(i));
		}
		allComp.add(fifthTab.txtBasicAdd);
		allComp.add(fifthTab.txtHouseRentAdd);
		allComp.add(fifthTab.txtMedicalAllowanceAdd);
		//allComp.add(fifthTab.txtClinicalAllowanceAdd);
		//allComp.add(fifthTab.txtNonPracticeAllowanceAdd);
			
		//allComp.add(fifthTab.txtDearnessAllowanceAdd);
		allComp.add(fifthTab.txtConveyanceAllowanceAdd);
		allComp.add(fifthTab.txtOtherAllowanceAdd);
		allComp.add(fifthTab.txtSpecialAllowanceAdd);
		//allComp.add(fifthTab.txtAttendanceBonusAdd);

		//allComp.add(fifthTab.txtTiffinAllowanceAdd);

		//allComp.add(fifthTab.txtRoomChargeLess);
		allComp.add(fifthTab.txtIncomeTaxLess);
		allComp.add(fifthTab.txtProvidentFundLess);
		//allComp.add(fifthTab.txtKallanFundLess);
		//allComp.add(fifthTab.txtKhichuriMealLess);

		allComp.add(fifthTab.cmbBankName);
		allComp.add(fifthTab.cmbBranchName);
		allComp.add(fifthTab.txtAccountNo);

		new FocusMoveByEnter(this,allComp);
	}

	private void setEventActionFirstTab()
	{

		firstTab.txtEmployeeName.addListener(new ValueChangeListener() {
			
			
			public void valueChange(ValueChangeEvent event) {
				empNameWork();
			}
		});
	
		/*sixTab.txtYear.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				addPfStartDate();
			}
		});*/
		firstTab.cmbProbationPeriod.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(firstTab.cmbProbationPeriod.getValue()!=null){
					if(firstTab.dJoiningDate.getValue()!=null)
					{
						addMonth();	
						addPfStartDate();
						addYearValid();
					}
				}
				else{
					firstTab.dConfirmationDate.setValue(new java.util.Date());
				}
			}
		});
		firstTab.txtValidYear.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				firstTab.lblYear.setValue("Year");
				if(!firstTab.txtValidYear.getValue().toString().isEmpty()){
					if(firstTab.dJoiningDate.getValue()!=null)
					{
						if(firstTab.txtValidYear.getValue().toString().length()>0){
							addYearValid();	
							if(firstTab.txtValidYear.getValue().toString().equals("1")){
								firstTab.lblYear.setValue("Year");
							}
							else
							{
								firstTab.lblYear.setValue("Years");
							}
						}
					}
					else
					{
						firstTab.txtValidYear.setValue("");
						firstTab.lblYear.setValue("");
					}
				}
				else{
					firstTab.dValidDate.setValue(new java.util.Date());
				}
			}
		});

		firstTab.dJoiningDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(firstTab.dJoiningDate.getValue()!=null){
					addYearValid();	
					if(firstTab.cmbProbationPeriod.getValue()!=null)
					{
						addMonth();	
						addPfStartDate();
						
					}
				}
				else{
					firstTab.dConfirmationDate.setValue(new java.util.Date());
				}
			}
		});
		firstTab.txtEmployeeCode.addListener(new ValueChangeListener() 
		{	
			public void valueChange(ValueChangeEvent event) 
			{
				if(!firstTab.txtEmployeeCode.getValue().toString().isEmpty() && !isFind)
				{
					checkDuplicateId("vEmployeeCode");
				}
			}
		});
		firstTab.txtFingerId.addListener(new ValueChangeListener() 
		{	
			public void valueChange(ValueChangeEvent event) 
			{
				if(!firstTab.txtFingerId.getValue().toString().isEmpty() && !isFind)
				{
					checkDuplicateId("vFingerId");
				}
			}
		});

		firstTab.txtProximityId.addListener(new ValueChangeListener() 
		{	
			public void valueChange(ValueChangeEvent event) 
			{
				if(!firstTab.txtProximityId.getValue().toString().isEmpty() && !isFind)
				{
					checkDuplicateId("vProximityId");
				}
			}
		});

		fifthTab.txtAccountNo.addListener(new ValueChangeListener() 
		{	
			public void valueChange(ValueChangeEvent event) 
			{
				if(!fifthTab.txtAccountNo.getValue().toString().isEmpty() && !isFind)
				{
					checkDuplicateId("vAccountNo");
				}
			}
		});

/*		firstTab.cmbGrade.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(firstTab.cmbGrade.getValue()!=null)
				{
					setSalaryRegisterValue(firstTab.cmbGrade.getValue().toString());
				}
			}
		});*/
		firstTab.btnPlusUnit.addListener(new ClickListener()
		{			
			public void buttonClick(ClickEvent event)
			{
				UnitLink();
			}
		});
		firstTab.btnPlusSection.addListener(new ClickListener()
		{			
			public void buttonClick(ClickEvent event)
			{
				sectionLink();
				
			}
		});
		firstTab.btnPlusDepartment.addListener(new ClickListener()
		{			
			public void buttonClick(ClickEvent event)
			{
				DepartmentLink();
			}
		});
		firstTab.btnPlusShift.addListener(new ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				shiftLink();
				
				showNotification("Say Hello!",Notification.TYPE_TRAY_NOTIFICATION);
			}
		});

		firstTab.btnPlusDesignation.addListener(new ClickListener()
		{			
			public void buttonClick(ClickEvent event)
			{
				designationLink();
			}
		});

		firstTab.btnPlusGroup.addListener(new ClickListener()
		{			
			public void buttonClick(ClickEvent event)
			{
				groupShiftLink();
			}
		});

		firstTab.btnPlusSalaryRegister.addListener(new ClickListener()
		{			
			public void buttonClick(ClickEvent event)
			{
				salaryRegisterLink();
			}
		});

		firstTab.cmbStatus.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(firstTab.cmbStatus.getValue()!=null)	
				{	
					if(!firstTab.cmbStatus.getValue().toString().equalsIgnoreCase("On Duty"))	
					{
						firstTab.dStatusDate.setVisible(true);
						firstTab.lblEmployeeStatus.setValue("<b><Font Color='#CD0606' size='3px'>*</Font></b> "+firstTab.cmbStatus.getValue().toString()+" Date :");
					}
					else
					{
						firstTab.dStatusDate.setVisible(false);
						firstTab.lblEmployeeStatus.setValue("");
					}
				}
			}
		});

		firstTab.btnBirthPreview.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				// Hyperlink to a given URL
				if(!isEdit)
				{
					if(!firstTab.btnDateofBirth.actionCheck)
					{
						showNotification("Warning","There is no file");
					}
					if(firstTab.btnDateofBirth.actionCheck)
					{
						String link = getApplication().getURL().toString();

						if(link.endsWith(""+sessionBean.getContextName()+"/"))
						{
							link = link.replaceAll(""+sessionBean.getContextName()+"", "report")+firstTab.birthFilePathTmp;
						}
						getWindow().open(new ExternalResource(link),"_blank",500,200,Window.BORDER_NONE);
					}
				}
				if(isEdit)
				{
					if(!firstTab.btnDateofBirth.actionCheck)
					{
						if(!birthImageLoc.equalsIgnoreCase("0"))
						{
							String link = getApplication().getURL().toString();
							if(link.endsWith(""+sessionBean.getContextName()+"/"))
							{
								link = link.replaceAll(""+sessionBean.getContextName()+"/", birthImageLoc.substring(22, birthImageLoc.length()));
							}
							getWindow().open(new ExternalResource(link),"_blank",500,200,Window.BORDER_NONE);
						}
						else
						{
							showNotification("Warning","There is no file",Notification.TYPE_HUMANIZED_MESSAGE);
						}
					}
					if(firstTab.btnDateofBirth.actionCheck)
					{
						String link = getApplication().getURL().toString();
						if(link.endsWith(""+sessionBean.getContextName()+"/"))
						{
							link = link.replaceAll(""+sessionBean.getContextName()+"", "report")+firstTab.birthFilePathTmp;
						}
						getWindow().open(new ExternalResource(link),"_blank",500,200,Window.BORDER_NONE);
					}
				}
			}
		});

		firstTab.btnNidPreview.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				// Hyperlink to a given URL
				if(!isEdit)
				{
					if(!firstTab.btnNid.actionCheck)
					{
						showNotification("Warning","There is no file");
					}
					if(firstTab.btnNid.actionCheck)
					{
						String link = getApplication().getURL().toString();
						if(link.endsWith(""+sessionBean.getContextName()+"/"))
						{
							link = link.replaceAll(""+sessionBean.getContextName()+"", "report")+firstTab.nidFilePathTmp;
						}
						getWindow().open(new ExternalResource(link),"_blank",500,200,Window.BORDER_NONE);
					}
				}
				if(isEdit)
				{
					if(!firstTab.btnNid.actionCheck)
					{
						if(!nidImageLoc.equalsIgnoreCase("0"))
						{
							String link = getApplication().getURL().toString();
							if(link.endsWith(""+sessionBean.getContextName()+"/"))
							{
								link = link.replaceAll(""+sessionBean.getContextName()+"/", nidImageLoc.substring(22, nidImageLoc.length()));
							}
							getWindow().open(new ExternalResource(link),"_blank",500,200,Window.BORDER_NONE);
						}
						else
						{
							showNotification("Warning","There is no file",Notification.TYPE_HUMANIZED_MESSAGE);
						}
					}
					if(firstTab.btnNid.actionCheck)
					{
						String link = getApplication().getURL().toString();
						if(link.endsWith(""+sessionBean.getContextName()+"/"))
						{
							link = link.replaceAll(""+sessionBean.getContextName()+"", "report")+firstTab.nidFilePathTmp;
						}
						getWindow().open(new ExternalResource(link),"_blank",500,200,Window.BORDER_NONE);
					}
				}
			}
		});

		firstTab.btnApplicationPreview.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				// Hyperlink to a given URL
				if(!isEdit)
				{
					if(!firstTab.btnAppDate.actionCheck)
					{
						showNotification("Warning","There is no file");
					}
					if(firstTab.btnAppDate.actionCheck)
					{
						String link = getApplication().getURL().toString();
						if(link.endsWith(""+sessionBean.getContextName()+"/"))
						{
							link = link.replaceAll(""+sessionBean.getContextName()+"", "report")+firstTab.applicationFilePathTmp;
						}
						getWindow().open(new ExternalResource(link),"_blank",500,200,Window.BORDER_NONE);
					}
				}
				if(isEdit)
				{
					if(!firstTab.btnAppDate.actionCheck)
					{
						if(!applicationImageLoc.equalsIgnoreCase("0"))
						{
							String link = getApplication().getURL().toString();
							if(link.endsWith(""+sessionBean.getContextName()+"/"))
							{
								link = link.replaceAll("uptd/", applicationImageLoc.substring(22, applicationImageLoc.length()));
							}
							getWindow().open(new ExternalResource(link),"_blank",500,200,Window.BORDER_NONE);
						}
						else
						{
							showNotification("Warning","There is no file",Notification.TYPE_HUMANIZED_MESSAGE);
						}
					}
					if(firstTab.btnAppDate.actionCheck)
					{
						String link = getApplication().getURL().toString();
						if(link.endsWith(""+sessionBean.getContextName()+"/"))
						{
							link = link.replaceAll(""+sessionBean.getContextName()+"", "report")+firstTab.applicationFilePathTmp;
						}
						getWindow().open(new ExternalResource(link),"_blank",500,200,Window.BORDER_NONE);
					}
				}
			}
		});

		firstTab.btnJoinPreview.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				// Hyperlink to a given URL
				if(!isEdit)
				{
					if(!firstTab.btnJoiningDate.actionCheck)
					{
						showNotification("Warning","There is no file");
					}
					if(firstTab.btnJoiningDate.actionCheck)
					{
						String link = getApplication().getURL().toString();
						if(link.endsWith(""+sessionBean.getContextName()+"/"))
						{
							link = link.replaceAll(""+sessionBean.getContextName()+"", "report")+firstTab.joinFilePathTmp;
						}
						getWindow().open(new ExternalResource(link),"_blank",500,200,Window.BORDER_NONE);
					}
				}
				if(isEdit)
				{
					if(!firstTab.btnJoiningDate.actionCheck)
					{
						if(!joinImageLoc.equalsIgnoreCase("0"))
						{
							String link = getApplication().getURL().toString();
							if(link.endsWith(""+sessionBean.getContextName()+"/"))
							{
								link = link.replaceAll(""+sessionBean.getContextName()+"/", joinImageLoc.substring(22, joinImageLoc.length()));
							}
							getWindow().open(new ExternalResource(link),"_blank",500,200,Window.BORDER_NONE);
						}
						else
						{
							showNotification("Warning","There is no file",Notification.TYPE_HUMANIZED_MESSAGE);
						}
					}
					if(firstTab.btnJoiningDate.actionCheck)
					{
						String link = getApplication().getURL().toString();
						if(link.endsWith(""+sessionBean.getContextName()+"/"))
						{
							link = link.replaceAll(""+sessionBean.getContextName()+"", "report")+firstTab.joinFilePathTmp;
						}
						getWindow().open(new ExternalResource(link),"_blank",500,200,Window.BORDER_NONE);
					}
				}
			}
		});

		firstTab.btnConPreview.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				// Hyperlink to a given URL
				if(!isEdit)
				{
					if(!firstTab.btnConfirmdate.actionCheck)
					{
						showNotification("Warning","There is no file");
					}
					if(firstTab.btnConfirmdate.actionCheck)
					{
						String link = getApplication().getURL().toString();
						if(link.endsWith(""+sessionBean.getContextName()+"/"))
						{
							link = link.replaceAll(""+sessionBean.getContextName()+"", "report")+firstTab.conFilePathTmp;
						}
						getWindow().open(new ExternalResource(link),"_blank",500,200,Window.BORDER_NONE);
					}
				}
				if(isEdit)
				{
					if(!firstTab.btnConfirmdate.actionCheck)
					{
						if(!conImageLoc.equalsIgnoreCase("0"))
						{
							String link = getApplication().getURL().toString();
							if(link.endsWith(""+sessionBean.getContextName()+"/"))
							{
								link = link.replaceAll(""+sessionBean.getContextName()+"/", conImageLoc.substring(22, conImageLoc.length()));
							}
							getWindow().open(new ExternalResource(link),"_blank",500,200,Window.BORDER_NONE);
						}
						else
						{
							showNotification("Warning","There is no file",Notification.TYPE_HUMANIZED_MESSAGE);
						}
					}
					if(firstTab.btnConfirmdate.actionCheck)
					{
						String link = getApplication().getURL().toString();
						if(link.endsWith(""+sessionBean.getContextName()+"/"))
						{
							link = link.replaceAll(""+sessionBean.getContextName()+"", "report")+firstTab.conFilePathTmp;
						}
						getWindow().open(new ExternalResource(link),"_blank",500,200,Window.BORDER_NONE);
					}
				}
			}
		});
		firstTab.btnServiceAgreementPreview.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				// Hyperlink to a given URL
				if(!isEdit)
				{
					System.out.println(1);
					if(!firstTab.btnServiceAgreement.actionCheck)
					{
						System.out.println(11);
						showNotification("Warning","There is no file");
					}
					if(firstTab.btnServiceAgreement.actionCheck)
					{
						System.out.println(111);
						String link = getApplication().getURL().toString();
						if(link.endsWith(""+sessionBean.getContextName()+"/"))
						{
							link = link.replaceAll(""+sessionBean.getContextName()+"", "report")+firstTab.conServiceAgreementFilePathTmp;
						}
						getWindow().open(new ExternalResource(link),"_blank",500,200,Window.BORDER_NONE);
					}
				}
				if(isEdit)
				{
					if(!firstTab.btnServiceAgreement.actionCheck)
					{
						System.out.println(2);
						if(!serviceAgreementImageLoc.equalsIgnoreCase("0"))
						{
							System.out.println(22);
							String link = getApplication().getURL().toString();
							if(link.endsWith(""+sessionBean.getContextName()+"/"))
							{
								System.out.println(222);
								link = link.replaceAll(""+sessionBean.getContextName()+"/", serviceAgreementImageLoc.substring(22, serviceAgreementImageLoc.length()));
							}
							System.out.println(link);
							getWindow().open(new ExternalResource(link),"_blank",500,200,Window.BORDER_NONE);
						}
						else
						{
							showNotification("Warning","There is no file",Notification.TYPE_HUMANIZED_MESSAGE);
						}
					}
					if(firstTab.btnServiceAgreement.actionCheck)
					{
						System.out.println(2222);
						String link = getApplication().getURL().toString();
						if(link.endsWith(""+sessionBean.getContextName()+"/"))
						{
							link = link.replaceAll(""+sessionBean.getContextName()+"", "report")+firstTab.conServiceAgreementFilePathTmp;
						}
						getWindow().open(new ExternalResource(link),"_blank",500,200,Window.BORDER_NONE);
					}
				}
			}
		});
	}
	private void addMonth(){
		Date date1=(Date) firstTab.dJoiningDate.getValue();
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(date1);
		calendar.add(calendar.MONTH, Integer.parseInt(firstTab.cmbProbationPeriod.getValue().toString()));
		
		Date date=new Date();
		date=calendar.getTime();
		firstTab.dConfirmationDate.setValue(date);
	}
	private void addYearValid(){
		Date date1=(Date) firstTab.dJoiningDate.getValue();
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(date1);
		calendar.add(calendar.YEAR, Integer.parseInt(firstTab.txtValidYear.getValue().toString().isEmpty()?"0":firstTab.txtValidYear.getValue().toString()));
		
		Date date=new Date();
		date=calendar.getTime();
		firstTab.dValidDate.setValue(date);
	}
	private void addPfStartDate(){
		/*if(!sixTab.txtYear.getValue().toString().isEmpty())
		{
			if(sixTab.opgDateType.getValue().equals("Joining Date"))
			{
				Date date1=(Date) firstTab.dJoiningDate.getValue();
				Calendar calendar=Calendar.getInstance();
				calendar.setTime(date1);
				calendar.add(calendar.YEAR, Integer.parseInt(sixTab.txtYear.getValue().toString()));
				
				Date date=new Date();
				date=calendar.getTime();
				sixTab.dPfStartDate.setValue(date);
			}
			else if(sixTab.opgDateType.getValue().equals("Confirmation Date"))
			{
				Date date1=(Date) firstTab.dConfirmationDate.getValue();
				Calendar calendar=Calendar.getInstance();
				calendar.setTime(date1);
				calendar.add(calendar.YEAR, Integer.parseInt(sixTab.txtYear.getValue().toString()));
				
				Date date=new Date();
				date=calendar.getTime();
				sixTab.dPfStartDate.setValue(date);
			}
		}*/
		
	}
	private void empNameWork()
	{
		if(isFind==false)
		{
			firstTab.txtFamilyName.setValue("");
			firstTab.txtGivenName.setValue("");
			if(!firstTab.txtEmployeeName.getValue().toString().trim().isEmpty())
			{
				String name=firstTab.txtEmployeeName.getValue().toString().trim();
				if(name.indexOf(' ')>=0)
				{
					StringTokenizer st=new StringTokenizer(firstTab.txtEmployeeName.getValue().toString()," ");
					if(st.hasMoreTokens())
					{
						firstTab.txtFamilyName.setValue(""+st.nextToken());
						firstTab.txtGivenName.setValue(""+st.nextToken());
					}
				}
				else
				{
					firstTab.txtFamilyName.setValue(firstTab.txtEmployeeName.getValue().toString().trim());
					firstTab.txtGivenName.setValue(firstTab.txtEmployeeName.getValue().toString().trim());
				}
					
			}
		}
	}
	

	private void checkDuplicateId(String hitFrom)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		String fieldName = "";
		if(hitFrom.equals("vEmployeeCode"))
		{
			fieldName = firstTab.txtEmployeeCode.getValue().toString().trim();
		}
		if(hitFrom.equals("vFingerId"))
		{
			fieldName = firstTab.txtFingerId.getValue().toString().trim();
		}
		if(hitFrom.equals("vProximityId"))
		{
			fieldName = firstTab.txtProximityId.getValue().toString().trim();
		}
		if(hitFrom.equals("vAccountNo"))
		{
			fieldName = fifthTab.txtAccountNo.getValue().toString().trim();
		}

		try 
		{
			String query = " select * from tbEmpOfficialPersonalInfo where "+hitFrom+" = '"+fieldName+"' and bStatus = 1 ";
			Iterator<?> iter = session.createSQLQuery(query).list().iterator();
			if(iter.hasNext())
			{
				showNotification("Warning!",hitFrom+" is already exist.", Notification.TYPE_WARNING_MESSAGE);
				if(hitFrom.equals("vEmployeeCode"))
				{
					firstTab.txtEmployeeCode.setValue("");
				}
				if(hitFrom.equals("vFingerId"))
				{
					firstTab.txtFingerId.setValue("");
				}
				if(hitFrom.equals("vProximityId"))
				{
					firstTab.txtProximityId.setValue("");
				}
				if(hitFrom.equals("vAccountNo"))
				{
					fifthTab.txtAccountNo.setValue("");
				}
			}
		}
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		finally{session.close();}
	}
}