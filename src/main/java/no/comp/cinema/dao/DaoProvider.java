package no.comp.cinema.dao;

import no.comp.cinema.dao.postgresql.PostgresqlDirectorDaoImpl;
import no.comp.cinema.dao.postgresql.PostgresqlFilmDaoImpl;

public class DaoProvider {

	private static final DirectorDao dirDao = new PostgresqlDirectorDaoImpl();
	private static final FilmDao fDao = new PostgresqlFilmDaoImpl();
	
	private DaoProvider() { }
	
	public static DirectorDao getDirectorDao() {
		return dirDao;
	}
	
	public static FilmDao getFilmDao() {
		return fDao;
	}
	
}
