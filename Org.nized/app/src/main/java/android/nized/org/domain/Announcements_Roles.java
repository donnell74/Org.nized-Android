package android.nized.org.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by greg on 1/7/15.
 */
public class Announcements_Roles implements Serializable {
    private int announcement_id;
    @JsonIgnore
    private List<Integer> role_id;
    private Date createdAt;
    private Date updatedAt;

    public int getAnnouncement_id() {
        return announcement_id;
    }

    public void setAnnouncement_id(int announcement_id) {
        this.announcement_id = announcement_id;
    }

    public List<Integer> getRole_id() {
        return role_id;
    }

    public void setRole_id(List<Integer> role_id) {
        this.role_id = role_id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Announcements_Roles that = (Announcements_Roles) o;

        if (announcement_id != that.announcement_id) return false;
        if (createdAt != null ? !createdAt.equals(that.createdAt) : that.createdAt != null)
            return false;
        if (role_id != null ? !role_id.equals(that.role_id) : that.role_id != null) return false;
        if (updatedAt != null ? !updatedAt.equals(that.updatedAt) : that.updatedAt != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = announcement_id;
        result = 31 * result + (role_id != null ? role_id.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (updatedAt != null ? updatedAt.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Announcements_Roles{" +
                "announcement_id=" + announcement_id +
                ", role_id=" + role_id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
