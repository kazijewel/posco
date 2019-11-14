package database.hibernate;

// Generated Jul 31, 2016 4:54:32 PM by Hibernate Tools 3.4.0.CR1

import java.util.Date;

/**
 * TbDepoInformation generated by hbm2java
 */
public class TbDepoInformation implements java.io.Serializable {

	private String vdepoId;
	private int iautoId;
	private String vdepoName;
	private String vdepoAddress;
	private String vtelephone;
	private String vmobile;
	private String vemail;
	private String vdepoIncharge;
	private String vdesignation;
	private String userId;
	private String userIp;
	private Date entryTime;

	public TbDepoInformation() {
	}

	public TbDepoInformation(String vdepoId, int iautoId) {
		this.vdepoId = vdepoId;
		this.iautoId = iautoId;
	}

	public TbDepoInformation(String vdepoId, int iautoId, String vdepoName,
			String vdepoAddress, String vtelephone, String vmobile,
			String vemail, String vdepoIncharge, String vdesignation,
			String userId, String userIp, Date entryTime) {
		this.vdepoId = vdepoId;
		this.iautoId = iautoId;
		this.vdepoName = vdepoName;
		this.vdepoAddress = vdepoAddress;
		this.vtelephone = vtelephone;
		this.vmobile = vmobile;
		this.vemail = vemail;
		this.vdepoIncharge = vdepoIncharge;
		this.vdesignation = vdesignation;
		this.userId = userId;
		this.userIp = userIp;
		this.entryTime = entryTime;
	}

	public String getVdepoId() {
		return this.vdepoId;
	}

	public void setVdepoId(String vdepoId) {
		this.vdepoId = vdepoId;
	}

	public int getIautoId() {
		return this.iautoId;
	}

	public void setIautoId(int iautoId) {
		this.iautoId = iautoId;
	}

	public String getVdepoName() {
		return this.vdepoName;
	}

	public void setVdepoName(String vdepoName) {
		this.vdepoName = vdepoName;
	}

	public String getVdepoAddress() {
		return this.vdepoAddress;
	}

	public void setVdepoAddress(String vdepoAddress) {
		this.vdepoAddress = vdepoAddress;
	}

	public String getVtelephone() {
		return this.vtelephone;
	}

	public void setVtelephone(String vtelephone) {
		this.vtelephone = vtelephone;
	}

	public String getVmobile() {
		return this.vmobile;
	}

	public void setVmobile(String vmobile) {
		this.vmobile = vmobile;
	}

	public String getVemail() {
		return this.vemail;
	}

	public void setVemail(String vemail) {
		this.vemail = vemail;
	}

	public String getVdepoIncharge() {
		return this.vdepoIncharge;
	}

	public void setVdepoIncharge(String vdepoIncharge) {
		this.vdepoIncharge = vdepoIncharge;
	}

	public String getVdesignation() {
		return this.vdesignation;
	}

	public void setVdesignation(String vdesignation) {
		this.vdesignation = vdesignation;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
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

}
