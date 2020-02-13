package no.comp.cinema.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;

public class Director extends Entity {

	private String firstName;
	private String lastName;
	private String dob;
	private List<Film> films = new ArrayList<>();
	
	public Director() { }

	public Director(String firstName, String lastName, String dob) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.dob = dob;
	}
	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public void setFilms(List<Film> films) {
		this.films = films;
	}
	
	@JsonGetter
	public List<Film> getFilms() {
		return this.films;
	}
	
	public void addFilm(Film film) {
		this.films.add(film);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((dob == null) ? 0 : dob.hashCode());
		result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Director other = (Director) obj;
		if (dob == null) {
			if (other.dob != null)
				return false;
		} else if (!dob.equals(other.dob))
			return false;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Director [firstName=" + firstName + ", lastName=" + lastName + ", dob=" + dob + ", films=" + films + "]";
	}

}
