package android.nized.org.domain;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
@JsonDeserialize
public class Person {

	private String email;
	private String password;
	private String first_name;
	private String last_name;
	private Date expire_date;
	private String mobile_number;
	private localPaidEnum is_local_paid;
	private boolean is_member;
	private classYearEnum class_year;
	private Date last_sync_date;
	private List<android.nized.org.domain.Checkin> checkins;
	private List<android.nized.org.domain.ClassBonus> class_bonuses;
	private List<android.nized.org.domain.Role> roles;
    @JsonIgnore
    private List<android.nized.org.domain.RoleName> _roles;
    @JsonIgnore
    private List<android.nized.org.domain.ClassBonus> _class_bonuses;
    @JsonIgnore
    private List<CheckinDate> _checkins;
    private Date createdAt;
    private Date updatedAt;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return first_name;
	}

	public void setFirstName(String firstName) {
		this.first_name = firstName;
	}

	public String getLastName() {
		return last_name;
	}

	public void setLastName(String lastName) {
		this.last_name = lastName;
	}

	public Date getExpireDate() {
		return expire_date;
	}

	public void setExpireDate(Date expireDate) {
		this.expire_date = expireDate;
	}

	public String getMobileNumber() {
		return mobile_number;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobile_number = mobileNumber;
	}

	public localPaidEnum isLocalPaid() {
		return is_local_paid;
	}

	public void setLocalPaid(localPaidEnum isLocalPaid) {
		this.is_local_paid = isLocalPaid;
	}

	public boolean isMember() {
		return is_member;
	}

	public void setMember(boolean isMember) {
		this.is_member = isMember;
	}

	public classYearEnum getClassYear() {
		return class_year;
	}

	public void setClassYear(classYearEnum classYear) {
		this.class_year = classYear;
	}

	public Date getLastSyncDate() {
		return last_sync_date;
	}

	public void setLastSyncDate(Date lastSyncDate) {
		this.last_sync_date = lastSyncDate;
	}

	public List<android.nized.org.domain.Checkin> getCheckins() {
		return checkins;
	}

	public void setCheckins(List<android.nized.org.domain.Checkin> checkins) {
		this.checkins = checkins;
	}

	public List<android.nized.org.domain.ClassBonus> getClassBonuses() {
		return class_bonuses;
	}

	public void setClassBonuses(List<android.nized.org.domain.ClassBonus> classBonuses) {
		this.class_bonuses = classBonuses;
	}

	public List<android.nized.org.domain.Role> getRoles() {
		return roles;
	}

	public void setRoles(List<android.nized.org.domain.Role> roles) {
		this.roles = roles;
	}

	public enum classYearEnum {
		FRESHMAN, SOPHOMORE, JUNIOR, SENIOR
	}

	public enum localPaidEnum {
		TRUE, FALSE, PENDING
	}

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public Date getExpire_date() {
        return expire_date;
    }

    public void setExpire_date(Date expire_date) {
        this.expire_date = expire_date;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }

    public localPaidEnum getIs_local_paid() {
        return is_local_paid;
    }

    public void setIs_local_paid(localPaidEnum is_local_paid) {
        this.is_local_paid = is_local_paid;
    }

    public boolean isIs_member() {
        return is_member;
    }

    public void setIs_member(boolean is_member) {
        this.is_member = is_member;
    }

    public classYearEnum getClass_year() {
        return class_year;
    }

    public void setClass_year(classYearEnum class_year) {
        this.class_year = class_year;
    }

    public Date getLast_sync_date() {
        return last_sync_date;
    }

    public void setLast_sync_date(Date last_sync_date) {
        this.last_sync_date = last_sync_date;
    }

    public List<ClassBonus> getClass_bonuses() {
        return class_bonuses;
    }

    public void setClass_bonuses(List<ClassBonus> class_bonuses) {
        this.class_bonuses = class_bonuses;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<ClassBonus> get_class_bonuses() {
        return _class_bonuses;
    }

    public void set_class_bonuses(List<ClassBonus> _class_bonuses) {
        this._class_bonuses = _class_bonuses;
    }



    public List<CheckinDate> get_checkins() {
        return _checkins;
    }

    public void set_checkins(List<CheckinDate> _checkins) {
        this._checkins = _checkins;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        if (is_member != person.is_member) return false;
        if (_checkins != null ? !_checkins.equals(person._checkins) : person._checkins != null)
            return false;
        if (_class_bonuses != null ? !_class_bonuses.equals(person._class_bonuses) : person._class_bonuses != null)
            return false;
        if (_roles != null ? !_roles.equals(person._roles) : person._roles != null) return false;
        if (checkins != null ? !checkins.equals(person.checkins) : person.checkins != null)
            return false;
        if (class_bonuses != null ? !class_bonuses.equals(person.class_bonuses) : person.class_bonuses != null)
            return false;
        if (class_year != person.class_year) return false;
        if (createdAt != null ? !createdAt.equals(person.createdAt) : person.createdAt != null)
            return false;
        if (email != null ? !email.equals(person.email) : person.email != null) return false;
        if (expire_date != null ? !expire_date.equals(person.expire_date) : person.expire_date != null)
            return false;
        if (first_name != null ? !first_name.equals(person.first_name) : person.first_name != null)
            return false;
        if (is_local_paid != person.is_local_paid) return false;
        if (last_name != null ? !last_name.equals(person.last_name) : person.last_name != null)
            return false;
        if (last_sync_date != null ? !last_sync_date.equals(person.last_sync_date) : person.last_sync_date != null)
            return false;
        if (mobile_number != null ? !mobile_number.equals(person.mobile_number) : person.mobile_number != null)
            return false;
        if (password != null ? !password.equals(person.password) : person.password != null)
            return false;
        if (roles != null ? !roles.equals(person.roles) : person.roles != null) return false;
        if (updatedAt != null ? !updatedAt.equals(person.updatedAt) : person.updatedAt != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = email != null ? email.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (first_name != null ? first_name.hashCode() : 0);
        result = 31 * result + (last_name != null ? last_name.hashCode() : 0);
        result = 31 * result + (expire_date != null ? expire_date.hashCode() : 0);
        result = 31 * result + (mobile_number != null ? mobile_number.hashCode() : 0);
        result = 31 * result + (is_local_paid != null ? is_local_paid.hashCode() : 0);
        result = 31 * result + (is_member ? 1 : 0);
        result = 31 * result + (class_year != null ? class_year.hashCode() : 0);
        result = 31 * result + (last_sync_date != null ? last_sync_date.hashCode() : 0);
        result = 31 * result + (checkins != null ? checkins.hashCode() : 0);
        result = 31 * result + (class_bonuses != null ? class_bonuses.hashCode() : 0);
        result = 31 * result + (roles != null ? roles.hashCode() : 0);
        result = 31 * result + (_roles != null ? _roles.hashCode() : 0);
        result = 31 * result + (_class_bonuses != null ? _class_bonuses.hashCode() : 0);
        result = 31 * result + (_checkins != null ? _checkins.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (updatedAt != null ? updatedAt.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Person{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", expire_date=" + expire_date +
                ", mobile_number='" + mobile_number + '\'' +
                ", is_local_paid=" + is_local_paid +
                ", is_member=" + is_member +
                ", class_year=" + class_year +
                ", last_sync_date=" + last_sync_date +
                ", checkins=" + checkins +
                ", class_bonuses=" + class_bonuses +
                ", roles=" + roles +
                ", _roles=" + _roles +
                ", _class_bonuses=" + _class_bonuses +
                ", _checkins=" + _checkins +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    ;

}
