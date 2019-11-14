package database.hibernate;

// Generated Jul 31, 2016 4:54:32 PM by Hibernate Tools 3.4.0.CR1

import java.util.Date;

/**
 * TbLogin generated by hbm2java
 */
public class TbLogin implements java.io.Serializable {

	private int userId;
	private String name;
	private String pass;
	private Date createTime;
	private Integer createBy;
	private Integer isAdmin;
	private Integer isInsertable;
	private Integer isUpdateable;
	private Integer isDeleteable;
	private Integer isActive;
	private Integer isSuperAdmin;
	private String employeeId;

	public TbLogin() {
	}

	public TbLogin(int userId, String name) {
		this.userId = userId;
		this.name = name;
	}

	public TbLogin(int userId, String name, String pass, Date createTime,
			Integer createBy, Integer isAdmin, Integer isInsertable,
			Integer isUpdateable, Integer isDeleteable, Integer isActive,
			Integer isSuperAdmin, String employeeId) {
		this.userId = userId;
		this.name = name;
		this.pass = pass;
		this.createTime = createTime;
		this.createBy = createBy;
		this.isAdmin = isAdmin;
		this.isInsertable = isInsertable;
		this.isUpdateable = isUpdateable;
		this.isDeleteable = isDeleteable;
		this.isActive = isActive;
		this.isSuperAdmin = isSuperAdmin;
		this.employeeId = employeeId;
	}

	public int getUserId() {
		return this.userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPass() {
		return this.pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getCreateBy() {
		return this.createBy;
	}

	public void setCreateBy(Integer createBy) {
		this.createBy = createBy;
	}

	public Integer getIsAdmin() {
		return this.isAdmin;
	}

	public void setIsAdmin(Integer isAdmin) {
		this.isAdmin = isAdmin;
	}

	public Integer getIsInsertable() {
		return this.isInsertable;
	}

	public void setIsInsertable(Integer isInsertable) {
		this.isInsertable = isInsertable;
	}

	public Integer getIsUpdateable() {
		return this.isUpdateable;
	}

	public void setIsUpdateable(Integer isUpdateable) {
		this.isUpdateable = isUpdateable;
	}

	public Integer getIsDeleteable() {
		return this.isDeleteable;
	}

	public void setIsDeleteable(Integer isDeleteable) {
		this.isDeleteable = isDeleteable;
	}

	public Integer getIsActive() {
		return this.isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	public Integer getIsSuperAdmin() {
		return this.isSuperAdmin;
	}

	public void setIsSuperAdmin(Integer isSuperAdmin) {
		this.isSuperAdmin = isSuperAdmin;
	}

	public String getEmployeeId() {
		return this.employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

}
