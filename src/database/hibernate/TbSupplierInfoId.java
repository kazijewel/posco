package database.hibernate;

// Generated Jul 31, 2016 4:54:32 PM by Hibernate Tools 3.4.0.CR1

import java.util.Date;

/**
 * TbSupplierInfoId generated by hbm2java
 */
public class TbSupplierInfoId implements java.io.Serializable {

	private int autoId;
	private int supplierId;
	private String supplierName;
	private String address;
	private String telephone;
	private String mobile;
	private String fax;
	private String email;
	private String contactPerson;
	private String personDesignation;
	private String personMobile;
	private Integer isActive;
	private String stockCoverNorm;
	private String groupId;
	private String groupName;
	private String subGroupId;
	private String subGroupName;
	private String fileLoc;
	private String ledgerCode;
	private String userName;
	private String userIp;
	private Date entryTime;

	public TbSupplierInfoId() {
	}

	public TbSupplierInfoId(int autoId, int supplierId) {
		this.autoId = autoId;
		this.supplierId = supplierId;
	}

	public TbSupplierInfoId(int autoId, int supplierId, String supplierName,
			String address, String telephone, String mobile, String fax,
			String email, String contactPerson, String personDesignation,
			String personMobile, Integer isActive, String stockCoverNorm,
			String groupId, String groupName, String subGroupId,
			String subGroupName, String fileLoc, String ledgerCode,
			String userName, String userIp, Date entryTime) {
		this.autoId = autoId;
		this.supplierId = supplierId;
		this.supplierName = supplierName;
		this.address = address;
		this.telephone = telephone;
		this.mobile = mobile;
		this.fax = fax;
		this.email = email;
		this.contactPerson = contactPerson;
		this.personDesignation = personDesignation;
		this.personMobile = personMobile;
		this.isActive = isActive;
		this.stockCoverNorm = stockCoverNorm;
		this.groupId = groupId;
		this.groupName = groupName;
		this.subGroupId = subGroupId;
		this.subGroupName = subGroupName;
		this.fileLoc = fileLoc;
		this.ledgerCode = ledgerCode;
		this.userName = userName;
		this.userIp = userIp;
		this.entryTime = entryTime;
	}

	public int getAutoId() {
		return this.autoId;
	}

	public void setAutoId(int autoId) {
		this.autoId = autoId;
	}

	public int getSupplierId() {
		return this.supplierId;
	}

	public void setSupplierId(int supplierId) {
		this.supplierId = supplierId;
	}

	public String getSupplierName() {
		return this.supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTelephone() {
		return this.telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getMobile() {
		return this.mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
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

	public String getContactPerson() {
		return this.contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	public String getPersonDesignation() {
		return this.personDesignation;
	}

	public void setPersonDesignation(String personDesignation) {
		this.personDesignation = personDesignation;
	}

	public String getPersonMobile() {
		return this.personMobile;
	}

	public void setPersonMobile(String personMobile) {
		this.personMobile = personMobile;
	}

	public Integer getIsActive() {
		return this.isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	public String getStockCoverNorm() {
		return this.stockCoverNorm;
	}

	public void setStockCoverNorm(String stockCoverNorm) {
		this.stockCoverNorm = stockCoverNorm;
	}

	public String getGroupId() {
		return this.groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return this.groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getSubGroupId() {
		return this.subGroupId;
	}

	public void setSubGroupId(String subGroupId) {
		this.subGroupId = subGroupId;
	}

	public String getSubGroupName() {
		return this.subGroupName;
	}

	public void setSubGroupName(String subGroupName) {
		this.subGroupName = subGroupName;
	}

	public String getFileLoc() {
		return this.fileLoc;
	}

	public void setFileLoc(String fileLoc) {
		this.fileLoc = fileLoc;
	}

	public String getLedgerCode() {
		return this.ledgerCode;
	}

	public void setLedgerCode(String ledgerCode) {
		this.ledgerCode = ledgerCode;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
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
		if (!(other instanceof TbSupplierInfoId))
			return false;
		TbSupplierInfoId castOther = (TbSupplierInfoId) other;

		return (this.getAutoId() == castOther.getAutoId())
				&& (this.getSupplierId() == castOther.getSupplierId())
				&& ((this.getSupplierName() == castOther.getSupplierName()) || (this
						.getSupplierName() != null
						&& castOther.getSupplierName() != null && this
						.getSupplierName().equals(castOther.getSupplierName())))
				&& ((this.getAddress() == castOther.getAddress()) || (this
						.getAddress() != null && castOther.getAddress() != null && this
						.getAddress().equals(castOther.getAddress())))
				&& ((this.getTelephone() == castOther.getTelephone()) || (this
						.getTelephone() != null
						&& castOther.getTelephone() != null && this
						.getTelephone().equals(castOther.getTelephone())))
				&& ((this.getMobile() == castOther.getMobile()) || (this
						.getMobile() != null && castOther.getMobile() != null && this
						.getMobile().equals(castOther.getMobile())))
				&& ((this.getFax() == castOther.getFax()) || (this.getFax() != null
						&& castOther.getFax() != null && this.getFax().equals(
						castOther.getFax())))
				&& ((this.getEmail() == castOther.getEmail()) || (this
						.getEmail() != null && castOther.getEmail() != null && this
						.getEmail().equals(castOther.getEmail())))
				&& ((this.getContactPerson() == castOther.getContactPerson()) || (this
						.getContactPerson() != null
						&& castOther.getContactPerson() != null && this
						.getContactPerson()
						.equals(castOther.getContactPerson())))
				&& ((this.getPersonDesignation() == castOther
						.getPersonDesignation()) || (this
						.getPersonDesignation() != null
						&& castOther.getPersonDesignation() != null && this
						.getPersonDesignation().equals(
								castOther.getPersonDesignation())))
				&& ((this.getPersonMobile() == castOther.getPersonMobile()) || (this
						.getPersonMobile() != null
						&& castOther.getPersonMobile() != null && this
						.getPersonMobile().equals(castOther.getPersonMobile())))
				&& ((this.getIsActive() == castOther.getIsActive()) || (this
						.getIsActive() != null
						&& castOther.getIsActive() != null && this
						.getIsActive().equals(castOther.getIsActive())))
				&& ((this.getStockCoverNorm() == castOther.getStockCoverNorm()) || (this
						.getStockCoverNorm() != null
						&& castOther.getStockCoverNorm() != null && this
						.getStockCoverNorm().equals(
								castOther.getStockCoverNorm())))
				&& ((this.getGroupId() == castOther.getGroupId()) || (this
						.getGroupId() != null && castOther.getGroupId() != null && this
						.getGroupId().equals(castOther.getGroupId())))
				&& ((this.getGroupName() == castOther.getGroupName()) || (this
						.getGroupName() != null
						&& castOther.getGroupName() != null && this
						.getGroupName().equals(castOther.getGroupName())))
				&& ((this.getSubGroupId() == castOther.getSubGroupId()) || (this
						.getSubGroupId() != null
						&& castOther.getSubGroupId() != null && this
						.getSubGroupId().equals(castOther.getSubGroupId())))
				&& ((this.getSubGroupName() == castOther.getSubGroupName()) || (this
						.getSubGroupName() != null
						&& castOther.getSubGroupName() != null && this
						.getSubGroupName().equals(castOther.getSubGroupName())))
				&& ((this.getFileLoc() == castOther.getFileLoc()) || (this
						.getFileLoc() != null && castOther.getFileLoc() != null && this
						.getFileLoc().equals(castOther.getFileLoc())))
				&& ((this.getLedgerCode() == castOther.getLedgerCode()) || (this
						.getLedgerCode() != null
						&& castOther.getLedgerCode() != null && this
						.getLedgerCode().equals(castOther.getLedgerCode())))
				&& ((this.getUserName() == castOther.getUserName()) || (this
						.getUserName() != null
						&& castOther.getUserName() != null && this
						.getUserName().equals(castOther.getUserName())))
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

		result = 37 * result + this.getAutoId();
		result = 37 * result + this.getSupplierId();
		result = 37
				* result
				+ (getSupplierName() == null ? 0 : this.getSupplierName()
						.hashCode());
		result = 37 * result
				+ (getAddress() == null ? 0 : this.getAddress().hashCode());
		result = 37 * result
				+ (getTelephone() == null ? 0 : this.getTelephone().hashCode());
		result = 37 * result
				+ (getMobile() == null ? 0 : this.getMobile().hashCode());
		result = 37 * result
				+ (getFax() == null ? 0 : this.getFax().hashCode());
		result = 37 * result
				+ (getEmail() == null ? 0 : this.getEmail().hashCode());
		result = 37
				* result
				+ (getContactPerson() == null ? 0 : this.getContactPerson()
						.hashCode());
		result = 37
				* result
				+ (getPersonDesignation() == null ? 0 : this
						.getPersonDesignation().hashCode());
		result = 37
				* result
				+ (getPersonMobile() == null ? 0 : this.getPersonMobile()
						.hashCode());
		result = 37 * result
				+ (getIsActive() == null ? 0 : this.getIsActive().hashCode());
		result = 37
				* result
				+ (getStockCoverNorm() == null ? 0 : this.getStockCoverNorm()
						.hashCode());
		result = 37 * result
				+ (getGroupId() == null ? 0 : this.getGroupId().hashCode());
		result = 37 * result
				+ (getGroupName() == null ? 0 : this.getGroupName().hashCode());
		result = 37
				* result
				+ (getSubGroupId() == null ? 0 : this.getSubGroupId()
						.hashCode());
		result = 37
				* result
				+ (getSubGroupName() == null ? 0 : this.getSubGroupName()
						.hashCode());
		result = 37 * result
				+ (getFileLoc() == null ? 0 : this.getFileLoc().hashCode());
		result = 37
				* result
				+ (getLedgerCode() == null ? 0 : this.getLedgerCode()
						.hashCode());
		result = 37 * result
				+ (getUserName() == null ? 0 : this.getUserName().hashCode());
		result = 37 * result
				+ (getUserIp() == null ? 0 : this.getUserIp().hashCode());
		result = 37 * result
				+ (getEntryTime() == null ? 0 : this.getEntryTime().hashCode());
		return result;
	}

}
