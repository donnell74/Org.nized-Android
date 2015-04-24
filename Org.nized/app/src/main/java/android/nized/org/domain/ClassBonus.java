package android.nized.org.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.android.gms.games.internal.constants.RequestStatus;
import com.loopj.android.http.RequestParams;

import java.io.Serializable;
import java.util.Date;

@JsonSerialize
@JsonDeserialize
public class ClassBonus implements Serializable {
    private String email;
	private int class_bonus_id;
	private String course_code;
	private String semester;
    private Date createdAt;
    private Date updatedAt;

    public int getClass_bonus_id() {
		return class_bonus_id;
	}

	public void setClass_bonus_id(int id) {
		this.class_bonus_id = id;
	}

	public String getCourseCode() {
		return course_code;
	}

	public void setCourseCode(String courseCode) {
		this.course_code = courseCode;
	}

	public String getSemester() {
		return semester;
	}

	public void setSemester(String semester) {
		this.semester = semester;
	}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCourse_code() {
        return course_code;
    }

    public void setCourse_code(String course_code) {
        this.course_code = course_code;
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((course_code == null) ? 0 : course_code.hashCode());
		result = prime * result + class_bonus_id;
		result = prime * result + ((semester == null) ? 0 : semester.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ClassBonus other = (ClassBonus) obj;
		if (course_code == null) {
			if (other.course_code != null) {
				return false;
			}
		} else if (!course_code.equals(other.course_code)) {
			return false;
		}
		if (class_bonus_id != other.class_bonus_id) {
			return false;
		}
		if (semester == null) {
			if (other.semester != null) {
				return false;
			}
		} else if (!semester.equals(other.semester)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "ClassBonus [id=" + class_bonus_id + ", courseCode=" + course_code + ", semester=" + semester
				+ "]";
	}

    public RequestParams getPersonRequestParams() {
        RequestParams requestParams = new RequestParams();
        requestParams.put("class_bonus_id", class_bonus_id);
        requestParams.put("email", email);

        return requestParams;
    }

    public RequestParams getRequestParams() {
        RequestParams requestParams = new RequestParams();
        requestParams.put("course_code", course_code);
        requestParams.put("semester", semester);

        return requestParams;
    }
}
