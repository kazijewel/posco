package database.hibernate;

// Generated Jul 31, 2016 4:54:32 PM by Hibernate Tools 3.4.0.CR1

import java.math.BigDecimal;
import java.util.Date;

/**
 * TbUdDeliveryChallanId generated by hbm2java
 */
public class TbUdDeliveryChallanId implements java.io.Serializable {

	private int iautoId;
	private String vdoNo;
	private String vchallanNo;
	private Date dchallanDate;
	private String vpartyId;
	private String vdriverName;
	private String vdriverMobile;
	private String vdriverTruckNo;
	private String vdestination;
	private String vremarks;
	private String vitemCode;
	private BigDecimal mitemChallanQty;
	private String vindividualRemarks;
	private String vuserId;
	private String vuserIp;
	private Date dentryTime;
	private String vflag;

	public TbUdDeliveryChallanId() {
	}

	public TbUdDeliveryChallanId(int iautoId) {
		this.iautoId = iautoId;
	}

	public TbUdDeliveryChallanId(int iautoId, String vdoNo, String vchallanNo,
			Date dchallanDate, String vpartyId, String vdriverName,
			String vdriverMobile, String vdriverTruckNo, String vdestination,
			String vremarks, String vitemCode, BigDecimal mitemChallanQty,
			String vindividualRemarks, String vuserId, String vuserIp,
			Date dentryTime, String vflag) {
		this.iautoId = iautoId;
		this.vdoNo = vdoNo;
		this.vchallanNo = vchallanNo;
		this.dchallanDate = dchallanDate;
		this.vpartyId = vpartyId;
		this.vdriverName = vdriverName;
		this.vdriverMobile = vdriverMobile;
		this.vdriverTruckNo = vdriverTruckNo;
		this.vdestination = vdestination;
		this.vremarks = vremarks;
		this.vitemCode = vitemCode;
		this.mitemChallanQty = mitemChallanQty;
		this.vindividualRemarks = vindividualRemarks;
		this.vuserId = vuserId;
		this.vuserIp = vuserIp;
		this.dentryTime = dentryTime;
		this.vflag = vflag;
	}

	public int getIautoId() {
		return this.iautoId;
	}

	public void setIautoId(int iautoId) {
		this.iautoId = iautoId;
	}

	public String getVdoNo() {
		return this.vdoNo;
	}

	public void setVdoNo(String vdoNo) {
		this.vdoNo = vdoNo;
	}

	public String getVchallanNo() {
		return this.vchallanNo;
	}

	public void setVchallanNo(String vchallanNo) {
		this.vchallanNo = vchallanNo;
	}

	public Date getDchallanDate() {
		return this.dchallanDate;
	}

	public void setDchallanDate(Date dchallanDate) {
		this.dchallanDate = dchallanDate;
	}

	public String getVpartyId() {
		return this.vpartyId;
	}

	public void setVpartyId(String vpartyId) {
		this.vpartyId = vpartyId;
	}

	public String getVdriverName() {
		return this.vdriverName;
	}

	public void setVdriverName(String vdriverName) {
		this.vdriverName = vdriverName;
	}

	public String getVdriverMobile() {
		return this.vdriverMobile;
	}

	public void setVdriverMobile(String vdriverMobile) {
		this.vdriverMobile = vdriverMobile;
	}

	public String getVdriverTruckNo() {
		return this.vdriverTruckNo;
	}

	public void setVdriverTruckNo(String vdriverTruckNo) {
		this.vdriverTruckNo = vdriverTruckNo;
	}

	public String getVdestination() {
		return this.vdestination;
	}

	public void setVdestination(String vdestination) {
		this.vdestination = vdestination;
	}

	public String getVremarks() {
		return this.vremarks;
	}

	public void setVremarks(String vremarks) {
		this.vremarks = vremarks;
	}

	public String getVitemCode() {
		return this.vitemCode;
	}

	public void setVitemCode(String vitemCode) {
		this.vitemCode = vitemCode;
	}

	public BigDecimal getMitemChallanQty() {
		return this.mitemChallanQty;
	}

	public void setMitemChallanQty(BigDecimal mitemChallanQty) {
		this.mitemChallanQty = mitemChallanQty;
	}

	public String getVindividualRemarks() {
		return this.vindividualRemarks;
	}

	public void setVindividualRemarks(String vindividualRemarks) {
		this.vindividualRemarks = vindividualRemarks;
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

	public String getVflag() {
		return this.vflag;
	}

	public void setVflag(String vflag) {
		this.vflag = vflag;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof TbUdDeliveryChallanId))
			return false;
		TbUdDeliveryChallanId castOther = (TbUdDeliveryChallanId) other;

		return (this.getIautoId() == castOther.getIautoId())
				&& ((this.getVdoNo() == castOther.getVdoNo()) || (this
						.getVdoNo() != null && castOther.getVdoNo() != null && this
						.getVdoNo().equals(castOther.getVdoNo())))
				&& ((this.getVchallanNo() == castOther.getVchallanNo()) || (this
						.getVchallanNo() != null
						&& castOther.getVchallanNo() != null && this
						.getVchallanNo().equals(castOther.getVchallanNo())))
				&& ((this.getDchallanDate() == castOther.getDchallanDate()) || (this
						.getDchallanDate() != null
						&& castOther.getDchallanDate() != null && this
						.getDchallanDate().equals(castOther.getDchallanDate())))
				&& ((this.getVpartyId() == castOther.getVpartyId()) || (this
						.getVpartyId() != null
						&& castOther.getVpartyId() != null && this
						.getVpartyId().equals(castOther.getVpartyId())))
				&& ((this.getVdriverName() == castOther.getVdriverName()) || (this
						.getVdriverName() != null
						&& castOther.getVdriverName() != null && this
						.getVdriverName().equals(castOther.getVdriverName())))
				&& ((this.getVdriverMobile() == castOther.getVdriverMobile()) || (this
						.getVdriverMobile() != null
						&& castOther.getVdriverMobile() != null && this
						.getVdriverMobile()
						.equals(castOther.getVdriverMobile())))
				&& ((this.getVdriverTruckNo() == castOther.getVdriverTruckNo()) || (this
						.getVdriverTruckNo() != null
						&& castOther.getVdriverTruckNo() != null && this
						.getVdriverTruckNo().equals(
								castOther.getVdriverTruckNo())))
				&& ((this.getVdestination() == castOther.getVdestination()) || (this
						.getVdestination() != null
						&& castOther.getVdestination() != null && this
						.getVdestination().equals(castOther.getVdestination())))
				&& ((this.getVremarks() == castOther.getVremarks()) || (this
						.getVremarks() != null
						&& castOther.getVremarks() != null && this
						.getVremarks().equals(castOther.getVremarks())))
				&& ((this.getVitemCode() == castOther.getVitemCode()) || (this
						.getVitemCode() != null
						&& castOther.getVitemCode() != null && this
						.getVitemCode().equals(castOther.getVitemCode())))
				&& ((this.getMitemChallanQty() == castOther
						.getMitemChallanQty()) || (this.getMitemChallanQty() != null
						&& castOther.getMitemChallanQty() != null && this
						.getMitemChallanQty().equals(
								castOther.getMitemChallanQty())))
				&& ((this.getVindividualRemarks() == castOther
						.getVindividualRemarks()) || (this
						.getVindividualRemarks() != null
						&& castOther.getVindividualRemarks() != null && this
						.getVindividualRemarks().equals(
								castOther.getVindividualRemarks())))
				&& ((this.getVuserId() == castOther.getVuserId()) || (this
						.getVuserId() != null && castOther.getVuserId() != null && this
						.getVuserId().equals(castOther.getVuserId())))
				&& ((this.getVuserIp() == castOther.getVuserIp()) || (this
						.getVuserIp() != null && castOther.getVuserIp() != null && this
						.getVuserIp().equals(castOther.getVuserIp())))
				&& ((this.getDentryTime() == castOther.getDentryTime()) || (this
						.getDentryTime() != null
						&& castOther.getDentryTime() != null && this
						.getDentryTime().equals(castOther.getDentryTime())))
				&& ((this.getVflag() == castOther.getVflag()) || (this
						.getVflag() != null && castOther.getVflag() != null && this
						.getVflag().equals(castOther.getVflag())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getIautoId();
		result = 37 * result
				+ (getVdoNo() == null ? 0 : this.getVdoNo().hashCode());
		result = 37
				* result
				+ (getVchallanNo() == null ? 0 : this.getVchallanNo()
						.hashCode());
		result = 37
				* result
				+ (getDchallanDate() == null ? 0 : this.getDchallanDate()
						.hashCode());
		result = 37 * result
				+ (getVpartyId() == null ? 0 : this.getVpartyId().hashCode());
		result = 37
				* result
				+ (getVdriverName() == null ? 0 : this.getVdriverName()
						.hashCode());
		result = 37
				* result
				+ (getVdriverMobile() == null ? 0 : this.getVdriverMobile()
						.hashCode());
		result = 37
				* result
				+ (getVdriverTruckNo() == null ? 0 : this.getVdriverTruckNo()
						.hashCode());
		result = 37
				* result
				+ (getVdestination() == null ? 0 : this.getVdestination()
						.hashCode());
		result = 37 * result
				+ (getVremarks() == null ? 0 : this.getVremarks().hashCode());
		result = 37 * result
				+ (getVitemCode() == null ? 0 : this.getVitemCode().hashCode());
		result = 37
				* result
				+ (getMitemChallanQty() == null ? 0 : this.getMitemChallanQty()
						.hashCode());
		result = 37
				* result
				+ (getVindividualRemarks() == null ? 0 : this
						.getVindividualRemarks().hashCode());
		result = 37 * result
				+ (getVuserId() == null ? 0 : this.getVuserId().hashCode());
		result = 37 * result
				+ (getVuserIp() == null ? 0 : this.getVuserIp().hashCode());
		result = 37
				* result
				+ (getDentryTime() == null ? 0 : this.getDentryTime()
						.hashCode());
		result = 37 * result
				+ (getVflag() == null ? 0 : this.getVflag().hashCode());
		return result;
	}

}
