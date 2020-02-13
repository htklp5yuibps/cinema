package no.comp.cinema.dao.postgresql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import no.comp.cinema.dao.ConnectionPool;
import no.comp.cinema.dao.FilmDao;
import no.comp.cinema.dao.RowMapper;
import no.comp.cinema.entity.Film;

public class PostgresqlFilmDaoImpl implements FilmDao {

	RowMapper<Film> filmMapper = (rs) -> {
		Film film = new Film();
		film.setId(rs.getLong(1));
		film.setName(rs.getString(2));
		film.setReleaseDate(rs.getString(3));
		film.setGenre(rs.getString(4));
		film.setDirectorId(rs.getLong(5));
		return film;
	};
	
	@Override
	public List<Film> read() {
		Connection connection = ConnectionPool.getInstance().takeConnection();
		try {
			List<Film> films = new ArrayList<>();
			PreparedStatement pst =
					connection.prepareStatement("select \"id\", \"name\", \"releaseDate\", \"genre\", \"directorId\" from \"film\"");
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				films.add(filmMapper.mapRow(rs));
			}
			return films;
		} catch (Exception exc) {
			throw new RuntimeException(exc.getMessage());
		} finally {
			ConnectionPool.closeConnection(connection);
		}
	}

	@Override
	public Film read(long id) {
		Connection connection = ConnectionPool.getInstance().takeConnection();
		try {
			Film film = null;
			PreparedStatement pst = connection.prepareStatement("select \"id\", \"name\", \"releaseDate\", \"genre\", \"directorId\" from \"film\" where \"id\" = ?");
			pst.setLong(1, id);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				film = filmMapper.mapRow(rs);
			}
			return film;
		} catch (Exception exc) {
			throw new RuntimeException(exc.getMessage());
		} finally {
			ConnectionPool.closeConnection(connection);
		}
	}

}
