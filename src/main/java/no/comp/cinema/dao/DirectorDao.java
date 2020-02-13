package no.comp.cinema.dao;

import java.util.List;

import no.comp.cinema.entity.Director;

public interface DirectorDao extends Dao<Director> {
	List<Director> findAllFromDate(String date);
	Director findDirectorWithFilmsFromDate(long directorId, String date);
}
