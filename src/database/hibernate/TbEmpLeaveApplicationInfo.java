package database.hibernate;

// Generated Jul 31, 2016 4:54:32 PM by Hibernate Tools 3.4.0.CR1

import java.math.BigDecimal;
import java.util.Date;

/**
 * TbEmpLeaveApplicationInfo generated by hbm2java
 */
public class TbEmpLeaveApplicationInfo implements java.io.Serializable {

	private String vleaveId;
	private long iautoId;
	private Date dapplicationDate;
	private String vsectionId;
	private String vsectionName;
	private String vdesignationId;
	private String vdesignation;
	private String vemployeeType;
	private String vfingerId;
	private String vemployeeName;
	private String vleaveTypeId;
	private String vleaveType;
	private Date dleaveFrom;
	private Date dleaveTo;
	private String vleavePupose;
	private String vleaveAddress;
	private String vmobileNo;
	private Date dsanctionFrom;
	private Date dsanctionTo;
	private BigDecimal mtotalDays;
	private BigDecimal mfridays;
	private BigDecimal madjustDays;
	private String vpaymentFlag;
	private int iapprovedFlag;
	private String vapprovedBy;
	private String vuserName;
	private String vuserIp;
	private Date dentryTime;

	public TbEmpLeaveApplicationInfo() {
	}

	public TbEmpLeaveApplicationInfo(String vleaveId, long iautoId,
			Date dapplicationDate, String vsectionId, String vsectionName,
			String vdesignationId, String vdesignation, String vemployeeType,
			String vfingerId, String vemployeeName, String vleaveTypeId,
			String vleaveType, Date dleaveFrom, Date dleaveTo,
			String vleavePupose, String vleaveAddress, String vmobileNo,
			Date dsanctionFrom, Date dsanctionTo, BigDecimal mtotalDays,
			BigDecimal mfridays, BigDecimal madjustDays, String vpaymentFlag,
			int iapprovedFlag, String vapprovedBy) {
		this.vleaveId = vleaveId;
		this.iautoId = iautoId;
		this.dapplicationDate = dapplicationDate;
		this.vsectionId = vsectionId;
		this.vsectionName = vsectionName;
		this.vdesignationId = vdesignationId;
		this.vdesignation = vdesignation;
		this.vemployeeType = vemployeeType;
		this.vfingerId = vfingerId;
		this.vemployeeName = vemployeeName;
		this.vleaveTypeId = vleaveTypeId;
		this.vleaveType = vleaveType;
		this.dleaveFrom = dleaveFrom;
		this.dleaveTo = dleaveTo;
		this.vleavePupose = vleavePupose;
		this.vleaveAddress = vleaveAddress;
		this.vmobileNo = vmobileNo;
		this.dsanctionFrom = dsanctionFrom;
		this.dsanctionTo = dsanctionTo;
		this.mtotalDays = mtotalDays;
		this.mfridays = mfridays;
		this.madjustDays = madjustDays;
		this.vpaymentFlag = vpaymentFlag;
		this.iapprovedFlag = iapprovedFlag;
		this.vapprovedBy = vapprovedBy;
	}

	public TbEmpLeaveApplicationInfo(String vleaveId, long iautoId,
			Date dapplicationDate, String vsectionId, String vsectionName,
			String vdesignationId, String vdesignation, String vemployeeType,
			String vfingerId, String vemployeeName, String vleaveTypeId,
			String vleaveType, Date dleaveFrom, Date dleaveTo,
			String vleavePupose, String vleaveAddress, String vmobileNo,
			Date dsanctionFrom, Date dsanctionTo, BigDecimal mtotalDays,
			BigDecimal mfridays, BigDecimal madjustDays, String vpaymentFlag,
			int iapprovedFlag, String vapprovedBy, String vuserName,
			String vuserIp, Date dentryTime) {
		this.vleaveId = vleaveId;
		this.iautoId = iautoId;
		this.dapplicationDate = dapplicationDate;
		this.vsectionId = vsectionId;
		this.vsectionName = vsectionName;
		this.vdesignationId = vdesignationId;
		this.vdesignation = vdesignation;
		this.vemployeeType = vemployeeType;
		this.vfingerId = vfingerId;
		this.vemployeeName = vemployeeName;
		this.vleaveTypeId = vleaveTypeId;
		this.vleaveType = vleaveType;
		this.dleaveFrom = dleaveFrom;
		this.dleaveTo = dleaveTo;
		this.vleavePupose = vleavePupose;
		this.vleaveAddress = vleaveAddress;
		this.vmobileNo = vmobileNo;
		this.dsanctionFrom = dsanctionFrom;
		this.dsanctionTo = dsanctionTo;
		this.mtotalDays = mtotalDays;
		this.mfridays = mfridays;
		this.madjustDays = madjustDays;
		this.vpaymentFlag = vpaymentFlag;
		this.iapprovedFlag = iapprovedFlag;
		this.vapprovedBy = vapprovedBy;
		this.vuserName = vuserName;
		this.vuserIp = vuserIp;
		this.dentryTime = dentryTime;
	}

	public String getVleaveId() {
		return this.vleaveId;
	}

	public void setVleaveId(String vleaveId) {
		this.vleaveId = vleaveId;
	}

	public long getIautoId() {
		return this.iautoId;
	}

	public void setIautoId(long iautoId) {
		this.iautoId = iautoId;
	}

	public Date getDapplicationDate() {
		return this.dapplicationDate;
	}

	public void setDapplicationDate(Date dapplicationDate) {
		this.dapplicationDate = dapplicationDate;
	}

	public String getVsectionId() {
		return this.vsectionId;
	}

	public void setVsectionId(String vsectionId) {
		this.vsectionId = vsectionId;
	}

	public String getVsectionName() {
		return this.vsectionName;
	}

	public void setVsectionName(String vsectionName) {
		this.vsectionName = vsectionName;
	}

	public String getVdesignationId() {
		return this.vdesignationId;
	}

	public void setVdesignationId(String vdesignationId) {
		this.vdesignationId = vdesignationId;
	}

	public String getVdesignation() {
		return this.vdesignation;
	}

	public void setVdesignation(String vdesignation) {
		this.vdesignation = vdesignation;
	}

	public String getVemployeeType() {
		return this.vemployeeType;
	}

	public void setVemployeeType(String vemployeeType) {
		this.vemployeeType = vemployeeType;
	}

	public String getVfingerId() {
		return this.vfingerId;
	}

	public void setVfingerId(String vfingerId) {
		this.vfingerId = vfingerId;
	}

	public String getVemployeeName() {
		return this.vemployeeName;
	}

	public void setVemployeeName(String vemployeeName) {
		this.vemployeeName = vemployeeName;
	}

	public String getVleaveTypeId() {
		return this.vleaveTypeId;
	}

	public void setVleaveTypeId(String vleaveTypeId) {
		this.vleaveTypeId = vleaveTypeId;
	}

	public String getVleaveType() {
		return this.vleaveType;
	}

	public void setVleaveType(String vleaveType) {
		this.vleaveType = vleaveType;
	}

	public Date getDleaveFrom() {
		return this.dleaveFrom;
	}

	public void setDleaveFrom(Date dleaveFrom) {
		this.dleaveFrom = dleaveFrom;
	}

	public Date getDleaveTo() {
		return this.dleaveTo;
	}

	public void setDleaveTo(Date dleaveTo) {
		this.dleaveTo = dleaveTo;
	}

	public String getVleavePupose() {
		return this.vleavePupose;
	}

	public void setVleavePupose(String vleavePupose) {
		this.vleavePupose = vleavePupose;
	}

	public String getVleaveAddress() {
		return this.vleaveAddress;
	}

	public void setVleaveAddress(String vleaveAddress) {
		this.vleaveAddress = vleaveAddress;
	}

	public String getVmobileNo() {
		return this.vmobileNo;
	}

	public void setVmobileNo(String vmobileNo) {
		this.vmobileNo = vmobileNo;
	}

	public Date getDsanctionFrom() {
		return this.dsanctionFrom;
	}

	public void setDsanctionFrom(Date dsanctionFrom) {
		this.dsanctionFrom = dsanctionFrom;
	}

	public Date getDsanctionTo() {
		return this.dsanctionTo;
	}

	public void setDsanctionTo(Date dsanctionTo) {
		this.dsanctionTo = dsanctionTo;
	}

	public BigDecimal getMtotalDays() {
		return this.mtotalDays;
	}

	public void setMtotalDays(BigDecimal mtotalDays) {
		this.mtotalDays = mtotalDays;
	}

	public BigDecimal getMfridays() {
		return this.mfridays;
	}

	public void setMfridays(BigDecimal mfridays) {
		this.mfridays = mfridays;
	}

	public BigDecimal getMadjustDays() {
		return this.madjustDays;
	}

	public void setMadjustDays(BigDecimal madjustDays) {
		this.madjustDays = madjustDays;
	}

	public String getVpaymentFlag() {
		return this.vpaymentFlag;
	}

	public void setVpaymentFlag(String vpaymentFlag) {
		this.vpaymentFlag = vpaymentFlag;
	}

	public int getIapprovedFlag() {
		return this.iapprovedFlag;
	}

	public void setIapprovedFlag(int iapprovedFlag) {
		this.iapprovedFlag = iapprovedFlag;
	}

	public String getVapprovedBy() {
		return this.vapprovedBy;
	}

	public void setVapprovedBy(String vapprovedBy) {
		this.vapprovedBy = vapprovedBy;
	}

	public String getVuserName() {
		return this.vuserName;
	}

	public void setVuserName(String vuserName) {
		this.vuserName = vuserName;
	}

	public String getVuserIp() {
		return this.vuserIp;
	}

	public void setVuserIp(String vuserIp) {
		this.vuserIp = vuserIp;
	}

	public Date getDentryTime() {
		return this.dentryTime;
	}

	public void setDentryTime(Date dentryTime) {
		this.dentryTime = dentryTime;
	}

}
