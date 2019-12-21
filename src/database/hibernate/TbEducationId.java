package database.hibernate;

// Generated Jul 31, 2016 4:54:32 PM by Hibernate Tools 3.4.0.CR1

import java.util.Date;

/**
 * TbEducationId generated by hbm2java
 */
public class TbEducationId implements java.io.Serializable {

	private int iserialNo;
	private String vemployeeId;
	private String vnameOfExam;
	private String vgroupSubject;
	private String vnameOfInstitution;
	private String vboardUniversity;
	private String vdivisionClassGrade;
	private Date vyearOfPassing;
	private String vuserName;
	private Date ddateTime;
	private String vpcip;

	public TbEducationId() {
	}

	public TbEducationId(int iserialNo, String vuserName, Date ddateTime,
			String vpcip) {
		this.iserialNo = iserialNo;
		this.vuserName = vuserName;
		this.ddateTime = ddateTime;
		this.vpcip = vpcip;
	}

	public TbEducationId(int iserialNo, String vemployeeId, String vnameOfExam,
			String vgroupSubject, String vnameOfInstitution,
			String vboardUniversity, String vdivisionClassGrade,
			Date vyearOfPassing, String vuserName, Date ddateTime, String vpcip) {
		this.iserialNo = iserialNo;
		this.vemployeeId = vemployeeId;
		this.vnameOfExam = vnameOfExam;
		this.vgroupSubject = vgroupSubject;
		this.vnameOfInstitution = vnameOfInstitution;
		this.vboardUniversity = vboardUniversity;
		this.vdivisionClassGrade = vdivisionClassGrade;
		this.vyearOfPassing = vyearOfPassing;
		this.vuserName = vuserName;
		this.ddateTime = ddateTime;
		this.vpcip = vpcip;
	}

	public int getIserialNo() {
		return this.iserialNo;
	}

	public void setIserialNo(int iserialNo) {
		this.iserialNo = iserialNo;
	}

	public String getVemployeeId() {
		return this.vemployeeId;
	}

	public void setVemployeeId(String vemployeeId) {
		this.vemployeeId = vemployeeId;
	}

	public String getVnameOfExam() {
		return this.vnameOfExam;
	}

	public void setVnameOfExam(String vnameOfExam) {
		this.vnameOfExam = vnameOfExam;
	}

	public String getVgroupSubject() {
		return this.vgroupSubject;
	}

	public void setVgroupSubject(String vgroupSubject) {
		this.vgroupSubject = vgroupSubject;
	}

	public String getVnameOfInstitution() {
		return this.vnameOfInstitution;
	}

	public void setVnameOfInstitution(String vnameOfInstitution) {
		this.vnameOfInstitution = vnameOfInstitution;
	}

	public String getVboardUniversity() {
		return this.vboardUniversity;
	}

	public void setVboardUniversity(String vboardUniversity) {
		this.vboardUniversity = vboardUniversity;
	}

	public String getVdivisionClassGrade() {
		return this.vdivisionClassGrade;
	}

	public void setVdivisionClassGrade(String vdivisionClassGrade) {
		this.vdivisionClassGrade = vdivisionClassGrade;
	}

	public Date getVyearOfPassing() {
		return this.vyearOfPassing;
	}

	public void setVyearOfPassing(Date vyearOfPassing) {
		this.vyearOfPassing = vyearOfPassing;
	}

	public String getVuserName() {
		return this.vuserName;
	}

	public void setVuserName(String vuserName) {
		this.vuserName = vuserName;
	}

	public Date getDdateTime() {
		return this.ddateTime;
	}

	public void setDdateTime(Date ddateTime) {
		this.ddateTime = ddateTime;
	}

	public String getVpcip() {
		return this.vpcip;
	}

	public void setVpcip(String vpcip) {
		this.vpcip = vpcip;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof TbEducationId))
			return false;
		TbEducationId castOther = (TbEducationId) other;

		return (this.getIserialNo() == castOther.getIserialNo())
				&& ((this.getVemployeeId() == castOther.getVemployeeId()) || (this
						.getVemployeeId() != null
						&& castOther.getVemployeeId() != null && this
						.getVemployeeId().equals(castOther.getVemployeeId())))
				&& ((this.getVnameOfExam() == castOther.getVnameOfExam()) || (this
						.getVnameOfExam() != null
						&& castOther.getVnameOfExam() != null && this
						.getVnameOfExam().equals(castOther.getVnameOfExam())))
				&& ((this.getVgroupSubject() == castOther.getVgroupSubject()) || (this
						.getVgroupSubject() != null
						&& castOther.getVgroupSubject() != null && this
						.getVgroupSubject()
						.equals(castOther.getVgroupSubject())))
				&& ((this.getVnameOfInstitution() == castOther
						.getVnameOfInstitution()) || (this
						.getVnameOfInstitution() != null
						&& castOther.getVnameOfInstitution() != null && this
						.getVnameOfInstitution().equals(
								castOther.getVnameOfInstitution())))
				&& ((this.getVboardUniversity() == castOther
						.getVboardUniversity()) || (this.getVboardUniversity() != null
						&& castOther.getVboardUniversity() != null && this
						.getVboardUniversity().equals(
								castOther.getVboardUniversity())))
				&& ((this.getVdivisionClassGrade() == castOther
						.getVdivisionClassGrade()) || (this
						.getVdivisionClassGrade() != null
						&& castOther.getVdivisionClassGrade() != null && this
						.getVdivisionClassGrade().equals(
								castOther.getVdivisionClassGrade())))
				&& ((this.getVyearOfPassing() == castOther.getVyearOfPassing()) || (this
						.getVyearOfPassing() != null
						&& castOther.getVyearOfPassing() != null && this
						.getVyearOfPassing().equals(
								castOther.getVyearOfPassing())))
				&& ((this.getVuserName() == castOther.getVuserName()) || (this
						.getVuserName() != null
						&& castOther.getVuserName() != null && this
						.getVuserName().equals(castOther.getVuserName())))
				&& ((this.getDdateTime() == castOther.getDdateTime()) || (this
						.getDdateTime() != null
						&& castOther.getDdateTime() != null && this
						.getDdateTime().equals(castOther.getDdateTime())))
				&& ((this.getVpcip() == castOther.getVpcip()) || (this
						.getVpcip() != null && castOther.getVpcip() != null && this
						.getVpcip().equals(castOther.getVpcip())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getIserialNo();
		result = 37
				* result
				+ (getVemployeeId() == null ? 0 : this.getVemployeeId()
						.hashCode());
		result = 37
				* result
				+ (getVnameOfExam() == null ? 0 : this.getVnameOfExam()
						.hashCode());
		result = 37
				* result
				+ (getVgroupSubject() == null ? 0 : this.getVgroupSubject()
						.hashCode());
		result = 37
				* result
				+ (getVnameOfInstitution() == null ? 0 : this
						.getVnameOfInstitution().hashCode());
		result = 37
				* result
				+ (getVboardUniversity() == null ? 0 : this
						.getVboardUniversity().hashCode());
		result = 37
				* result
				+ (getVdivisionClassGrade() == null ? 0 : this
						.getVdivisionClassGrade().hashCode());
		result = 37
				* result
				+ (getVyearOfPassing() == null ? 0 : this.getVyearOfPassing()
						.hashCode());
		result = 37 * result
				+ (getVuserName() == null ? 0 : this.getVuserName().hashCode());
		result = 37 * result
				+ (getDdateTime() == null ? 0 : this.getDdateTime().hashCode());
		result = 37 * result
				+ (getVpcip() == null ? 0 : this.getVpcip().hashCode());
		return result;
	}

}
