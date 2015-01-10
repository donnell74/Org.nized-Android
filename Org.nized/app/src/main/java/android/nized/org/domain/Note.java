package android.nized.org.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;

@JsonSerialize
@JsonDeserialize
public class Note {

	/*
	 * id person_email public_to_person title text
	 */

	private int id;
	private Person person_email; // from person_email in DB
	private boolean public_to_person;
	private String title;
	private String text;
    private Date createdAt;
    private Date updatedAt;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Person getPerson_email() {
		return person_email;
	}

	public void setPerson_email(Person person_email) {
		this.person_email = person_email;
	}

	public boolean getPublicToPerson() {
		return public_to_person;
	}

	public void setPublicToPerson(boolean publicToPerson) {
		this.public_to_person = publicToPerson;
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

    public boolean isPublic_to_person() {
        return public_to_person;
    }

    public void setPublic_to_person(boolean public_to_person) {
        this.public_to_person = public_to_person;
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
		result = prime * result + id;
		result = prime * result + ((person_email == null) ? 0 : person_email.hashCode());
		result = prime * result + (public_to_person ? 1231 : 1237);
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
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
		Note other = (Note) obj;
		if (id != other.id) {
			return false;
		}
		if (person_email == null) {
			if (other.person_email != null) {
				return false;
			}
		} else if (!person_email.equals(other.person_email)) {
			return false;
		}
		if (public_to_person != other.public_to_person) {
			return false;
		}
		if (text == null) {
			if (other.text != null) {
				return false;
			}
		} else if (!text.equals(other.text)) {
			return false;
		}
		if (title == null) {
			if (other.title != null) {
				return false;
			}
		} else if (!title.equals(other.title)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Note [id=" + id + ", person_email=" + person_email + ", publicToPerson=" + public_to_person
				+ ", title=" + title + ", text=" + text + "]";
	}

}
