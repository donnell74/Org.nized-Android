package android.nized.org.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;
import java.util.List;

@JsonSerialize
@JsonDeserialize
public class Announcement {

	private int id;
    private String email;
	private String creator; // email of person who created it is what's passed
	private String title;
	private String text;
	private Date start_date;
	private Date end_date;
	private List<android.nized.org.domain.Role> roles;
    private Date createdAt;
    private Date updatedAt;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getStart_date() {
        return start_date;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    public Date getEnd_date() {
        return end_date;
    }

    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
    }

    public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
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

    public List<android.nized.org.domain.Role> getRoles() {
		return roles;
	}

	public void setRoles(List<android.nized.org.domain.Role> roles) {
		this.roles = roles;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Announcement that = (Announcement) o;

        if (id != that.id) return false;
        if (createdAt != null ? !createdAt.equals(that.createdAt) : that.createdAt != null)
            return false;
        if (creator != null ? !creator.equals(that.creator) : that.creator != null) return false;
        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        if (end_date != null ? !end_date.equals(that.end_date) : that.end_date != null)
            return false;
        if (roles != null ? !roles.equals(that.roles) : that.roles != null) return false;
        if (start_date != null ? !start_date.equals(that.start_date) : that.start_date != null)
            return false;
        if (text != null ? !text.equals(that.text) : that.text != null) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (updatedAt != null ? !updatedAt.equals(that.updatedAt) : that.updatedAt != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (creator != null ? creator.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (start_date != null ? start_date.hashCode() : 0);
        result = 31 * result + (end_date != null ? end_date.hashCode() : 0);
        result = 31 * result + (roles != null ? roles.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (updatedAt != null ? updatedAt.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Announcement{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", creator='" + creator + '\'' +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", start_date=" + start_date +
                ", end_date=" + end_date +
                ", roles=" + roles +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
