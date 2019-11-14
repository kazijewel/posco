package database.hibernate;

// Generated Jul 31, 2016 4:54:32 PM by Hibernate Tools 3.4.0.CR1

import java.util.Date;

/**
 * TbBankNameId generated by hbm2java
 */
public class TbBankNameId implements java.io.Serializable {

	private int id;
	private String bankName;
	private Integer userId;
	private String userIp;
	private Date entryTime;

	public TbBankNameId() {
	}

	public TbBankNameId(int id) {
		this.id = id;
	}

	public TbBankNameId(int id, String bankName, Integer userId, String userIp,
			Date entryTime) {
		this.id = id;
		this.bankName = bankName;
		this.userId = userId;
		this.userIp = userIp;
		this.entryTime = entryTime;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getBankName() {
		return this.bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public Integer getUserId() {
		return this.userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUserIp() {
		return this.userIp;
	}

	public void setUserIp(String userIp) {
		this.userIp = userIp;
	}

	public Date getEntryTime() {
		return this.entryTime;
	}

	public void setEntryTime(Date entryTime) {
		this.entryTime = entryTime;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof TbBankNameId))
			return false;
		TbBankNameId castOther = (TbBankNameId) other;

		return (this.getId() == castOther.getId())
				&& ((this.getBankName() == castOther.getBankName()) || (this
						.getBankName() != null
						&& castOther.getBankName() != null && this
						.getBankName().equals(castOther.getBankName())))
				&& ((this.getUserId() == castOther.getUserId()) || (this
						.getUserId() != null && castOther.getUserId() != null && this
						.getUserId().equals(castOther.getUserId())))
				&& ((this.getUserIp() == castOther.getUserIp()) || (this
						.getUserIp() != null && castOther.getUserIp() != null && this
						.getUserIp().equals(castOther.getUserIp())))
				&& ((this.getEntryTime() == castOther.getEntryTime()) || (this
						.getEntryTime() != null
						&& castOther.getEntryTime() != null && this
						.getEntryTime().equals(castOther.getEntryTime())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getId();
		result = 37 * result
				+ (getBankName() == null ? 0 : this.getBankName().hashCode());
		result = 37 * result
				+ (getUserId() == null ? 0 : this.getUserId().hashCode());
		result = 37 * result
				+ (getUserIp() == null ? 0 : this.getUserIp().hashCode());
		result = 37 * result
				+ (getEntryTime() == null ? 0 : this.getEntryTime().hashCode());
		return result;
	}

}
