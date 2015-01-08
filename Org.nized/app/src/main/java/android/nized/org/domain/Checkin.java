package android.nized.org.domain;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
@JsonDeserialize
public class Checkin {

	private Person person; // Based on email stored in DB
	private Date date_scanned;

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public Date getDateScanned() {
		return date_scanned;
	}

	public void setDateScanned(Date dateScanned) {
		this.date_scanned = dateScanned;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date_scanned == null) ? 0 : date_scanned.hashCode());
		result = prime * result + ((person == null) ? 0 : person.hashCode());
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
		Checkin other = (Checkin) obj;
		if (date_scanned == null) {
			if (other.date_scanned != null) {
				return false;
			}
		} else if (!date_scanned.equals(other.date_scanned)) {
			return false;
		}
		if (person == null) {
			if (other.person != null) {
				return false;
			}
		} else if (!person.equals(other.person)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Checkin [person=" + person + ", date_scanned=" + date_scanned + "]";
	}

}
