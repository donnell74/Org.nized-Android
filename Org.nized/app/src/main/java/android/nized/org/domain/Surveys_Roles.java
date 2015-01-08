package android.nized.org.domain;

import java.util.Date;

/**
 * Created by greg on 1/7/15.
 */
public class Surveys_Roles {
    private Survey survey_id;
    private Role role_id;
    private Date createdAt;
    private Date updatedAt;

    public Survey getSurvey_id() {
        return survey_id;
    }

    public void setSurvey_id(Survey survey_id) {
        this.survey_id = survey_id;
    }

    public Role getRole_id() {
        return role_id;
    }

    public void setRole_id(Role role_id) {
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

        Surveys_Roles that = (Surveys_Roles) o;

        if (createdAt != null ? !createdAt.equals(that.createdAt) : that.createdAt != null)
            return false;
        if (role_id != null ? !role_id.equals(that.role_id) : that.role_id != null) return false;
        if (survey_id != null ? !survey_id.equals(that.survey_id) : that.survey_id != null)
            return false;
        if (updatedAt != null ? !updatedAt.equals(that.updatedAt) : that.updatedAt != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = survey_id != null ? survey_id.hashCode() : 0;
        result = 31 * result + (role_id != null ? role_id.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (updatedAt != null ? updatedAt.hashCode() : 0);
        return result;
    }
}
