package database.hibernate;

// Generated Jul 31, 2016 4:54:32 PM by Hibernate Tools 3.4.0.CR1

import java.util.Date;

/**
 * TbCompanyInfo generated by hbm2java
 */
public class TbCompanyInfo implements java.io.Serializable {

	private int companyId;
	private String companyName;
	private String dseMemNo;
	private String cseMemberNo;
	private String cdblParticularId;
	private String phoneNo;
	private String fax;
	private String email;
	private String address;
	private Integer userId;
	private String userIp;
	private Date entryTime;
	private String imageLoc;
	private String validTime;

	public TbCompanyInfo() {
	}

	public TbCompanyInfo(int companyId) {
		this.companyId = companyId;
	}

	public TbCompanyInfo(int companyId, String companyName, String dseMemNo,
			String cseMemberNo, String cdblParticularId, String phoneNo,
			String fax, String email, String address, Integer userId,
			String userIp, Date entryTime, String imageLoc, String validTime) {
		this.companyId = companyId;
		this.companyName = companyName;
		this.dseMemNo = dseMemNo;
		this.cseMemberNo = cseMemberNo;
		this.cdblParticularId = cdblParticularId;
		this.phoneNo = phoneNo;
		this.fax = fax;
		this.email = email;
		this.address = address;
		this.userId = userId;
		this.userIp = userIp;
		this.entryTime = entryTime;
		this.imageLoc = imageLoc;
		this.validTime = validTime;
	}

	public int getCompanyId() {
		return this.companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	public String getCompanyName() {
		return this.companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getDseMemNo() {
		return this.dseMemNo;
	}

	public void setDseMemNo(String dseMemNo) {
		this.dseMemNo = dseMemNo;
	}

	public String getCseMemberNo() {
		return this.cseMemberNo;
	}

	public void setCseMemberNo(String cseMemberNo) {
		this.cseMemberNo = cseMemberNo;
	}

	public String getCdblParticularId() {
		return this.cdblParticularId;
	}

	public void setCdblParticularId(String cdblParticularId) {
		this.cdblParticularId = cdblParticularId;
	}

	public String getPhoneNo() {
		return this.phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getFax() {
		return this.fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
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

	public String getImageLoc() {
		return this.imageLoc;
	}

	public void setImageLoc(String imageLoc) {
		this.imageLoc = imageLoc;
	}

	public String getValidTime() {
		return this.validTime;
	}

	public void setValidTime(String validTime) {
		this.validTime = validTime;
	}

}
