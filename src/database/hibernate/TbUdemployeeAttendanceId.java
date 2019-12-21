package database.hibernate;

// Generated Jul 31, 2016 4:54:32 PM by Hibernate Tools 3.4.0.CR1

import java.util.Date;

/**
 * TbUdemployeeAttendanceId generated by hbm2java
 */
public class TbUdemployeeAttendanceId implements java.io.Serializable {

	private int iautoId;
	private Date ddate;
	private String vemployeeId;
	private String ifingerId;
	private String vemployeeName;
	private String vdesignation;
	private String vsectionId;
	private String vsectionName;
	private Date dattDate;
	private Date dattInTime;
	private Date dattOutTime;
	private String permittedBy;
	private String vreason;
	private Integer vshiftId;
	private String udFlag;
	private String vpaflag;
	private String vuserId;
	private String vuserIp;
	private Date dentryTime;

	public TbUdemployeeAttendanceId() {
	}

	public TbUdemployeeAttendanceId(int iautoId) {
		this.iautoId = iautoId;
	}

	public TbUdemployeeAttendanceId(int iautoId, Date ddate,
			String vemployeeId, String ifingerId, String vemployeeName,
			String vdesignation, String vsectionId, String vsectionName,
			Date dattDate, Date dattInTime, Date dattOutTime,
			String permittedBy, String vreason, Integer vshiftId,
			String udFlag, String vpaflag, String vuserId, String vuserIp,
			Date dentryTime) {
		this.iautoId = iautoId;
		this.ddate = ddate;
		this.vemployeeId = vemployeeId;
		this.ifingerId = ifingerId;
		this.vemployeeName = vemployeeName;
		this.vdesignation = vdesignation;
		this.vsectionId = vsectionId;
		this.vsectionName = vsectionName;
		this.dattDate = dattDate;
		this.dattInTime = dattInTime;
		this.dattOutTime = dattOutTime;
		this.permittedBy = permittedBy;
		this.vreason = vreason;
		this.vshiftId = vshiftId;
		this.udFlag = udFlag;
		this.vpaflag = vpaflag;
		this.vuserId = vuserId;
		this.vuserIp = vuserIp;
		this.dentryTime = dentryTime;
	}

	public int getIautoId() {
		return this.iautoId;
	}

	public void setIautoId(int iautoId) {
		this.iautoId = iautoId;
	}

	public Date getDdate() {
		return this.ddate;
	}

	public void setDdate(Date ddate) {
		this.ddate = ddate;
	}

	public String getVemployeeId() {
		return this.vemployeeId;
	}

	public void setVemployeeId(String vemployeeId) {
		this.vemployeeId = vemployeeId;
	}

	public String getIfingerId() {
		return this.ifingerId;
	}

	public void setIfingerId(String ifingerId) {
		this.ifingerId = ifingerId;
	}

	public String getVemployeeName() {
		return this.vemployeeName;
	}

	public void setVemployeeName(String vemployeeName) {
		this.vemployeeName = vemployeeName;
	}

	public String getVdesignation() {
		return this.vdesignation;
	}

	public void setVdesignation(String vdesignation) {
		this.vdesignation = vdesignation;
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

	public Date getDattDate() {
		return this.dattDate;
	}

	public void setDattDate(Date dattDate) {
		this.dattDate = dattDate;
	}

	public Date getDattInTime() {
		return this.dattInTime;
	}

	public void setDattInTime(Date dattInTime) {
		this.dattInTime = dattInTime;
	}

	public Date getDattOutTime() {
		return this.dattOutTime;
	}

	public void setDattOutTime(Date dattOutTime) {
		this.dattOutTime = dattOutTime;
	}

	public String getPermittedBy() {
		return this.permittedBy;
	}

	public void setPermittedBy(String permittedBy) {
		this.permittedBy = permittedBy;
	}

	public String getVreason() {
		return this.vreason;
	}

	public void setVreason(String vreason) {
		this.vreason = vreason;
	}

	public Integer getVshiftId() {
		return this.vshiftId;
	}

	public void setVshiftId(Integer vshiftId) {
		this.vshiftId = vshiftId;
	}

	public String getUdFlag() {
		return this.udFlag;
	}

	public void setUdFlag(String udFlag) {
		this.udFlag = udFlag;
	}

	public String getVpaflag() {
		return this.vpaflag;
	}

	public void setVpaflag(String vpaflag) {
		this.vpaflag = vpaflag;
	}

	public String getVuserId() {
		return this.vuserId;
	}

	public void setVuserId(String vuserId) {
		this.vuserId = vuserId;
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

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof TbUdemployeeAttendanceId))
			return false;
		TbUdemployeeAttendanceId castOther = (TbUdemployeeAttendanceId) other;

		return (this.getIautoId() == castOther.getIautoId())
				&& ((this.getDdate() == castOther.getDdate()) || (this
						.getDdate() != null && castOther.getDdate() != null && this
						.getDdate().equals(castOther.getDdate())))
				&& ((this.getVemployeeId() == castOther.getVemployeeId()) || (this
						.getVemployeeId() != null
						&& castOther.getVemployeeId() != null && this
						.getVemployeeId().equals(castOther.getVemployeeId())))
				&& ((this.getIfingerId() == castOther.getIfingerId()) || (this
						.getIfingerId() != null
						&& castOther.getIfingerId() != null && this
						.getIfingerId().equals(castOther.getIfingerId())))
				&& ((this.getVemployeeName() == castOther.getVemployeeName()) || (this
						.getVemployeeName() != null
						&& castOther.getVemployeeName() != null && this
						.getVemployeeName()
						.equals(castOther.getVemployeeName())))
				&& ((this.getVdesignation() == castOther.getVdesignation()) || (this
						.getVdesignation() != null
						&& castOther.getVdesignation() != null && this
						.getVdesignation().equals(castOther.getVdesignation())))
				&& ((this.getVsectionId() == castOther.getVsectionId()) || (this
						.getVsectionId() != null
						&& castOther.getVsectionId() != null && this
						.getVsectionId().equals(castOther.getVsectionId())))
				&& ((this.getVsectionName() == castOther.getVsectionName()) || (this
						.getVsectionName() != null
						&& castOther.getVsectionName() != null && this
						.getVsectionName().equals(castOther.getVsectionName())))
				&& ((this.getDattDate() == castOther.getDattDate()) || (this
						.getDattDate() != null
						&& castOther.getDattDate() != null && this
						.getDattDate().equals(castOther.getDattDate())))
				&& ((this.getDattInTime() == castOther.getDattInTime()) || (this
						.getDattInTime() != null
						&& castOther.getDattInTime() != null && this
						.getDattInTime().equals(castOther.getDattInTime())))
				&& ((this.getDattOutTime() == castOther.getDattOutTime()) || (this
						.getDattOutTime() != null
						&& castOther.getDattOutTime() != null && this
						.getDattOutTime().equals(castOther.getDattOutTime())))
				&& ((this.getPermittedBy() == castOther.getPermittedBy()) || (this
						.getPermittedBy() != null
						&& castOther.getPermittedBy() != null && this
						.getPermittedBy().equals(castOther.getPermittedBy())))
				&& ((this.getVreason() == castOther.getVreason()) || (this
						.getVreason() != null && castOther.getVreason() != null && this
						.getVreason().equals(castOther.getVreason())))
				&& ((this.getVshiftId() == castOther.getVshiftId()) || (this
						.getVshiftId() != null
						&& castOther.getVshiftId() != null && this
						.getVshiftId().equals(castOther.getVshiftId())))
				&& ((this.getUdFlag() == castOther.getUdFlag()) || (this
						.getUdFlag() != null && castOther.getUdFlag() != null && this
						.getUdFlag().equals(castOther.getUdFlag())))
				&& ((this.getVpaflag() == castOther.getVpaflag()) || (this
						.getVpaflag() != null && castOther.getVpaflag() != null && this
						.getVpaflag().equals(castOther.getVpaflag())))
				&& ((this.getVuserId() == castOther.getVuserId()) || (this
						.getVuserId() != null && castOther.getVuserId() != null && this
						.getVuserId().equals(castOther.getVuserId())))
				&& ((this.getVuserIp() == castOther.getVuserIp()) || (this
						.getVuserIp() != null && castOther.getVuserIp() != null && this
						.getVuserIp().equals(castOther.getVuserIp())))
				&& ((this.getDentryTime() == castOther.getDentryTime()) || (this
						.getDentryTime() != null
						&& castOther.getDentryTime() != null && this
						.getDentryTime().equals(castOther.getDentryTime())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getIautoId();
		result = 37 * result
				+ (getDdate() == null ? 0 : this.getDdate().hashCode());
		result = 37
				* result
				+ (getVemployeeId() == null ? 0 : this.getVemployeeId()
						.hashCode());
		result = 37 * result
				+ (getIfingerId() == null ? 0 : this.getIfingerId().hashCode());
		result = 37
				* result
				+ (getVemployeeName() == null ? 0 : this.getVemployeeName()
						.hashCode());
		result = 37
				* result
				+ (getVdesignation() == null ? 0 : this.getVdesignation()
						.hashCode());
		result = 37
				* result
				+ (getVsectionId() == null ? 0 : this.getVsectionId()
						.hashCode());
		result = 37
				* result
				+ (getVsectionName() == null ? 0 : this.getVsectionName()
						.hashCode());
		result = 37 * result
				+ (getDattDate() == null ? 0 : this.getDattDate().hashCode());
		result = 37
				* result
				+ (getDattInTime() == null ? 0 : this.getDattInTime()
						.hashCode());
		result = 37
				* result
				+ (getDattOutTime() == null ? 0 : this.getDattOutTime()
						.hashCode());
		result = 37
				* result
				+ (getPermittedBy() == null ? 0 : this.getPermittedBy()
						.hashCode());
		result = 37 * result
				+ (getVreason() == null ? 0 : this.getVreason().hashCode());
		result = 37 * result
				+ (getVshiftId() == null ? 0 : this.getVshiftId().hashCode());
		result = 37 * result
				+ (getUdFlag() == null ? 0 : this.getUdFlag().hashCode());
		result = 37 * result
				+ (getVpaflag() == null ? 0 : this.getVpaflag().hashCode());
		result = 37 * result
				+ (getVuserId() == null ? 0 : this.getVuserId().hashCode());
		result = 37 * result
				+ (getVuserIp() == null ? 0 : this.getVuserIp().hashCode());
		result = 37
				* result
				+ (getDentryTime() == null ? 0 : this.getDentryTime()
						.hashCode());
		return result;
	}

}
