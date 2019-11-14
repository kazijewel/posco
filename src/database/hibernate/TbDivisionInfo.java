package database.hibernate;

// Generated Jul 31, 2016 4:54:32 PM by Hibernate Tools 3.4.0.CR1

import java.util.Date;

/**
 * TbDivisionInfo generated by hbm2java
 */
public class TbDivisionInfo implements java.io.Serializable {

	private String vdivisionId;
	private int iautoId;
	private String vdivisionName;
	private String vemployeeId;
	private String vemployeeName;
	private String vdesignation;
	private String groupId;
	private String userId;
	private String userIp;
	private Date entryTime;

	public TbDivisionInfo() {
	}

	public TbDivisionInfo(String vdivisionId, int iautoId) {
		this.vdivisionId = vdivisionId;
		this.iautoId = iautoId;
	}

	public TbDivisionInfo(String vdivisionId, int iautoId,
			String vdivisionName, String vemployeeId, String vemployeeName,
			String vdesignation, String groupId, String userId, String userIp,
			Date entryTime) {
		this.vdivisionId = vdivisionId;
		this.iautoId = iautoId;
		this.vdivisionName = vdivisionName;
		this.vemployeeId = vemployeeId;
		this.vemployeeName = vemployeeName;
		this.vdesignation = vdesignation;
		this.groupId = groupId;
		this.userId = userId;
		this.userIp = userIp;
		this.entryTime = entryTime;
	}

	public String getVdivisionId() {
		return this.vdivisionId;
	}

	public void setVdivisionId(String vdivisionId) {
		this.vdivisionId = vdivisionId;
	}

	public int getIautoId() {
		return this.iautoId;
	}

	public void setIautoId(int iautoId) {
		this.iautoId = iautoId;
	}

	public String getVdivisionName() {
		return this.vdivisionName;
	}

	public void setVdivisionName(String vdivisionName) {
		this.vdivisionName = vdivisionName;
	}

	public String getVemployeeId() {
		return this.vemployeeId;
	}

	public void setVemployeeId(String vemployeeId) {
		this.vemployeeId = vemployeeId;
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

	public String getGroupId() {
		return this.groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
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
