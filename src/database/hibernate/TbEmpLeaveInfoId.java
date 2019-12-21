package database.hibernate;

// Generated Jul 31, 2016 4:54:32 PM by Hibernate Tools 3.4.0.CR1

import java.util.Date;

/**
 * TbEmpLeaveInfoId generated by hbm2java
 */
public class TbEmpLeaveInfoId implements java.io.Serializable {

	private long iautoId;
	private String vleaveId;
	private String vyear;
	private String vfingerId;
	private String vemployeeName;
	private int icasualLeave;
	private int isickLeave;
	private int iearnLeave;
	private int imeternityLeave;
	private String vuserName;
	private String vuserIp;
	private Date dentryTime;

	public TbEmpLeaveInfoId() {
	}

	public TbEmpLeaveInfoId(long iautoId, String vleaveId, String vyear,
			String vfingerId, String vemployeeName, int icasualLeave,
			int isickLeave, int iearnLeave, int imeternityLeave) {
		this.iautoId = iautoId;
		this.vleaveId = vleaveId;
		this.vyear = vyear;
		this.vfingerId = vfingerId;
		this.vemployeeName = vemployeeName;
		this.icasualLeave = icasualLeave;
		this.isickLeave = isickLeave;
		this.iearnLeave = iearnLeave;
		this.imeternityLeave = imeternityLeave;
	}

	public TbEmpLeaveInfoId(long iautoId, String vleaveId, String vyear,
			String vfingerId, String vemployeeName, int icasualLeave,
			int isickLeave, int iearnLeave, int imeternityLeave,
			String vuserName, String vuserIp, Date dentryTime) {
		this.iautoId = iautoId;
		this.vleaveId = vleaveId;
		this.vyear = vyear;
		this.vfingerId = vfingerId;
		this.vemployeeName = vemployeeName;
		this.icasualLeave = icasualLeave;
		this.isickLeave = isickLeave;
		this.iearnLeave = iearnLeave;
		this.imeternityLeave = imeternityLeave;
		this.vuserName = vuserName;
		this.vuserIp = vuserIp;
		this.dentryTime = dentryTime;
	}

	public long getIautoId() {
		return this.iautoId;
	}

	public void setIautoId(long iautoId) {
		this.iautoId = iautoId;
	}

	public String getVleaveId() {
		return this.vleaveId;
	}

	public void setVleaveId(String vleaveId) {
		this.vleaveId = vleaveId;
	}

	public String getVyear() {
		return this.vyear;
	}

	public void setVyear(String vyear) {
		this.vyear = vyear;
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

	public int getIcasualLeave() {
		return this.icasualLeave;
	}

	public void setIcasualLeave(int icasualLeave) {
		this.icasualLeave = icasualLeave;
	}

	public int getIsickLeave() {
		return this.isickLeave;
	}

	public void setIsickLeave(int isickLeave) {
		this.isickLeave = isickLeave;
	}

	public int getIearnLeave() {
		return this.iearnLeave;
	}

	public void setIearnLeave(int iearnLeave) {
		this.iearnLeave = iearnLeave;
	}

	public int getImeternityLeave() {
		return this.imeternityLeave;
	}

	public void setImeternityLeave(int imeternityLeave) {
		this.imeternityLeave = imeternityLeave;
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

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof TbEmpLeaveInfoId))
			return false;
		TbEmpLeaveInfoId castOther = (TbEmpLeaveInfoId) other;

		return (this.getIautoId() == castOther.getIautoId())
				&& ((this.getVleaveId() == castOther.getVleaveId()) || (this
						.getVleaveId() != null
						&& castOther.getVleaveId() != null && this
						.getVleaveId().equals(castOther.getVleaveId())))
				&& ((this.getVyear() == castOther.getVyear()) || (this
						.getVyear() != null && castOther.getVyear() != null && this
						.getVyear().equals(castOther.getVyear())))
				&& ((this.getVfingerId() == castOther.getVfingerId()) || (this
						.getVfingerId() != null
						&& castOther.getVfingerId() != null && this
						.getVfingerId().equals(castOther.getVfingerId())))
				&& ((this.getVemployeeName() == castOther.getVemployeeName()) || (this
						.getVemployeeName() != null
						&& castOther.getVemployeeName() != null && this
						.getVemployeeName()
						.equals(castOther.getVemployeeName())))
				&& (this.getIcasualLeave() == castOther.getIcasualLeave())
				&& (this.getIsickLeave() == castOther.getIsickLeave())
				&& (this.getIearnLeave() == castOther.getIearnLeave())
				&& (this.getImeternityLeave() == castOther.getImeternityLeave())
				&& ((this.getVuserName() == castOther.getVuserName()) || (this
						.getVuserName() != null
						&& castOther.getVuserName() != null && this
						.getVuserName().equals(castOther.getVuserName())))
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

		result = 37 * result + (int) this.getIautoId();
		result = 37 * result
				+ (getVleaveId() == null ? 0 : this.getVleaveId().hashCode());
		result = 37 * result
				+ (getVyear() == null ? 0 : this.getVyear().hashCode());
		result = 37 * result
				+ (getVfingerId() == null ? 0 : this.getVfingerId().hashCode());
		result = 37
				* result
				+ (getVemployeeName() == null ? 0 : this.getVemployeeName()
						.hashCode());
		result = 37 * result + this.getIcasualLeave();
		result = 37 * result + this.getIsickLeave();
		result = 37 * result + this.getIearnLeave();
		result = 37 * result + this.getImeternityLeave();
		result = 37 * result
				+ (getVuserName() == null ? 0 : this.getVuserName().hashCode());
		result = 37 * result
				+ (getVuserIp() == null ? 0 : this.getVuserIp().hashCode());
		result = 37
				* result
				+ (getDentryTime() == null ? 0 : this.getDentryTime()
						.hashCode());
		return result;
	}

}
