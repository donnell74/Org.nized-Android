package android.nized.org.domain;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
@JsonDeserialize
public class Question {

	/*
	 * id survey_id question_text type possible_answers roles
	 */

	private int id;
	private Survey survey; // From survey_id in DB
	private String question_text;
	private List<PossibleAnswer> possible_answers;
	private List<Role> roles;
	private typeEnum type;
    private Date createdAt;
    private Date updatedAt;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Survey getSurvey() {
		return survey;
	}

	public void setSurveyId(Survey survey) {
		this.survey = survey;
	}

	public String getQuestionText() {
		return question_text;
	}

	public void setQuestionText(String questionText) {
		this.question_text = questionText;
	}

	public List<PossibleAnswer> getPossibleAnswers() {
		return possible_answers;
	}

	public void setPossibleAnswers(List<PossibleAnswer> possible_answers) {
		this.possible_answers = possible_answers;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

    public void setSurvey(Survey survey) {
        this.survey = survey;
    }

    public String getQuestion_text() {
        return question_text;
    }

    public void setQuestion_text(String question_text) {
        this.question_text = question_text;
    }

    public List<PossibleAnswer> getPossible_answers() {
        return possible_answers;
    }

    public void setPossible_answers(List<PossibleAnswer> possible_answers) {
        this.possible_answers = possible_answers;
    }

    public typeEnum getType() {
        return type;
    }

    public void setType(typeEnum type) {
        this.type = type;
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

    public enum typeEnum {
		RADIO, CHECKBOX, TEXT
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((possible_answers == null) ? 0 : possible_answers.hashCode());
		result = prime * result + ((question_text == null) ? 0 : question_text.hashCode());
		result = prime * result + ((roles == null) ? 0 : roles.hashCode());
		result = prime * result + ((survey == null) ? 0 : survey.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		Question other = (Question) obj;
		if (id != other.id) {
			return false;
		}
		if (possible_answers == null) {
			if (other.possible_answers != null) {
				return false;
			}
		} else if (!possible_answers.equals(other.possible_answers)) {
			return false;
		}
		if (question_text == null) {
			if (other.question_text != null) {
				return false;
			}
		} else if (!question_text.equals(other.question_text)) {
			return false;
		}
		if (roles == null) {
			if (other.roles != null) {
				return false;
			}
		} else if (!roles.equals(other.roles)) {
			return false;
		}
		if (survey == null) {
			if (other.survey != null) {
				return false;
			}
		} else if (!survey.equals(other.survey)) {
			return false;
		}
		if (type != other.type) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Question [id=" + id + ", survey=" + survey + ", questionText=" + question_text
				+ ", possible_answers=" + possible_answers + ", roles=" + roles + ", type=" + type
				+ "]";
	}

}
