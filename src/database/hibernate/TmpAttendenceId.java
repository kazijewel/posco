package database.hibernate;

// Generated Jul 31, 2016 4:54:32 PM by Hibernate Tools 3.4.0.CR1

/**
 * TmpAttendenceId generated by hbm2java
 */
public class TmpAttendenceId implements java.io.Serializable {

	private String figId;
	private String vname;
	private String ddate;
	private String vmode;
	private String machine;
	private String excp;

	public TmpAttendenceId() {
	}

	public TmpAttendenceId(String figId, String vname, String ddate,
			String vmode, String machine, String excp) {
		this.figId = figId;
		this.vname = vname;
		this.ddate = ddate;
		this.vmode = vmode;
		this.machine = machine;
		this.excp = excp;
	}

	public String getFigId() {
		return this.figId;
	}

	public void setFigId(String figId) {
		this.figId = figId;
	}

	public String getVname() {
		return this.vname;
	}

	public void setVname(String vname) {
		this.vname = vname;
	}

	public String getDdate() {
		return this.ddate;
	}

	public void setDdate(String ddate) {
		this.ddate = ddate;
	}

	public String getVmode() {
		return this.vmode;
	}

	public void setVmode(String vmode) {
		this.vmode = vmode;
	}

	public String getMachine() {
		return this.machine;
	}

	public void setMachine(String machine) {
		this.machine = machine;
	}

	public String getExcp() {
		return this.excp;
	}

	public void setExcp(String excp) {
		this.excp = excp;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof TmpAttendenceId))
			return false;
		TmpAttendenceId castOther = (TmpAttendenceId) other;

		return ((this.getFigId() == castOther.getFigId()) || (this.getFigId() != null
				&& castOther.getFigId() != null && this.getFigId().equals(
				castOther.getFigId())))
				&& ((this.getVname() == castOther.getVname()) || (this
						.getVname() != null && castOther.getVname() != null && this
						.getVname().equals(castOther.getVname())))
				&& ((this.getDdate() == castOther.getDdate()) || (this
						.getDdate() != null && castOther.getDdate() != null && this
						.getDdate().equals(castOther.getDdate())))
				&& ((this.getVmode() == castOther.getVmode()) || (this
						.getVmode() != null && castOther.getVmode() != null && this
						.getVmode().equals(castOther.getVmode())))
				&& ((this.getMachine() == castOther.getMachine()) || (this
						.getMachine() != null && castOther.getMachine() != null && this
						.getMachine().equals(castOther.getMachine())))
				&& ((this.getExcp() == castOther.getExcp()) || (this.getExcp() != null
						&& castOther.getExcp() != null && this.getExcp()
						.equals(castOther.getExcp())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result
				+ (getFigId() == null ? 0 : this.getFigId().hashCode());
		result = 37 * result
				+ (getVname() == null ? 0 : this.getVname().hashCode());
		result = 37 * result
				+ (getDdate() == null ? 0 : this.getDdate().hashCode());
		result = 37 * result
				+ (getVmode() == null ? 0 : this.getVmode().hashCode());
		result = 37 * result
				+ (getMachine() == null ? 0 : this.getMachine().hashCode());
		result = 37 * result
				+ (getExcp() == null ? 0 : this.getExcp().hashCode());
		return result;
	}

}
