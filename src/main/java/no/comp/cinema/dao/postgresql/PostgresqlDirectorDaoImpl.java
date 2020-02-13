package no.comp.cinema.dao.postgresql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import no.comp.cinema.dao.ConnectionPool;
import no.comp.cinema.dao.DirectorDao;
import no.comp.cinema.dao.RowMapper;
import no.comp.cinema.entity.Director;
import no.comp.cinema.entity.Film;
import no.comp.cinema.helper.DateConverter;

public class PostgresqlDirectorDaoImpl implements DirectorDao {

	private RowMapper<Director> directorMapper = new RowMapper<Director>() {
		@Override
		public Director mapRow(ResultSet rs)
				throws SQLException {
			Director director = new Director();
			director.setId(rs.getLong(1));
			director.setFirstName(rs.getString(2));
			director.setLastName(rs.getString(3));
			director.setDob(rs.getString(4));
			return director;
		}
	};
	
	private RowMapper<Film> filmMapper = new RowMapper<Film>() {
		@Override
		public Film mapRow(ResultSet rs) throws SQLException {
			Film film = new Film();
			film.setId(rs.getLong(1));
			film.setName(rs.getString(2));
			film.setReleaseDate(rs.getString(3));
			film.setGenre(rs.getString(4));
			film.setDirectorId(rs.getLong(5));
			return film;
		}
	};
	
	private RowMapper<Map.Entry<Director, Film>> secondDirectorMapper = (rs) -> {
		Director director = new Director();
		director.setId(rs.getLong(3));
		director.setFirstName(rs.getString(1));
		director.setLastName(rs.getString(2));
		director.setDob(rs.getString(4));
		
		Film film = new Film();
		film.setId(rs.getLong(5));
		film.setName(rs.getString(6));
		film.setReleaseDate(rs.getString(7));
		film.setGenre(rs.getString(8));
		film.setDirectorId(rs.getLong(9));
		
		director.addFilm(film);
		
		return new AbstractMap.SimpleEntry<Director, Film>(director, film);
	};

	@Override
	public List<Director> read() {
		Connection connection = ConnectionPool.getInstance().takeConnection();
		
		try {
			PreparedStatement pst = connection.prepareStatement("select \"id\", \"firstName\", \"lastName\", \"dob\" from \"director\";");
			ResultSet rs = pst.executeQuery();
			List<Director> directors = new ArrayList<>();
			while(rs.next()) {
				directors.add(directorMapper.mapRow(rs));
			}
			for (Director director: directors) {
				getDirectorsFilms(director);
			}
			return directors;
		} catch (SQLException exc) {
			throw new RuntimeException(exc);
		} finally {
			ConnectionPool.closeConnection(connection);
		}
	}

	@Override
	public Director read(long type) {
		Connection connection = ConnectionPool.getInstance().takeConnection();
		
		try {
			PreparedStatement pst = connection.prepareStatement("select \"firstName\", \"lastName\", \"dob\" from \"director\" where \"id\" = ?");
			pst.setLong(1, type);
			ResultSet rs = pst.executeQuery();
			Director director = null;
			
			while(rs.next()) {
				director = directorMapper.mapRow(rs);
			}
			
			return director;
		} catch (SQLException exc) {
			throw new RuntimeException(exc);
		} finally {
			ConnectionPool.closeConnection(connection);
		}
	}
	
	private void getDirectorsFilms(Director dir) {
		Connection connection = ConnectionPool.getInstance().takeConnection();
		
		try {
			PreparedStatement pst =
					connection.prepareStatement("select \"id\", \"name\", \"releaseDate\", \"genre\", \"directorId\" from \"film\" where \"directorId\" = ?");
			pst.setLong(1, dir.getId());
			ResultSet rs = pst.executeQuery();
			
			while(rs.next()) {
				dir.addFilm(filmMapper.mapRow(rs));
			}
		} catch (Exception exc) {
			throw new RuntimeException(exc.getMessage());
		} finally {
			ConnectionPool.closeConnection(connection);
		}
	}

	@Override
	public List<Director> findAllFromDate(String date) {
		Connection connection = ConnectionPool.getInstance().takeConnection();
		
		System.out.println(date);
		
		try {
			PreparedStatement pst = connection.prepareStatement(
					"select \"firstName\", \"lastName\", \"director\".\"id\", \"dob\", \"film\".\"id\", \"name\", \"releaseDate\", \"genre\", \"directorId\""
					+ " from \"film\""
					+ " inner join \"director\""
					+ " on \"film\".\"directorId\" = \"director\".\"id\""
					+ " where \"releaseDate\" > ?");
			
			pst.setDate(1, java.sql.Date.valueOf(LocalDate.parse(date)));
			
			ResultSet rs = pst.executeQuery();
			
			Map<Director, List<Film>> resultMap = new HashMap<>();
			
			while(rs.next()) {
				Director director = new RowMapper<Director>() {
					@Override
					public Director mapRow(ResultSet rs) throws SQLException {
						Director director = new Director();
						director.setId(rs.getLong(3));
						director.setFirstName(rs.getString(1));
						director.setLastName(rs.getString(2));
						director.setDob(rs.getString(4));
						return director;
					}
				}.mapRow(rs);
				
				Film film = new RowMapper<Film>() {
					@Override
					public Film mapRow(ResultSet rs) throws SQLException {
						Film film = new Film();
						film.setId(rs.getLong(5));
						film.setName(rs.getString(6));
						film.setReleaseDate(rs.getString(7));
						film.setGenre(rs.getString(8));
						film.setDirectorId(rs.getLong(9));
						return film;
					}
				}.mapRow(rs);
				
				if (resultMap.containsKey(director)) {
					resultMap.get(director).add(film);
				} else {
					List<Film> films = new ArrayList<>();
					films.add(film);
					resultMap.put(director, films);
				}
			}
			
			List<Director> result = new ArrayList<>();
			
			resultMap.forEach((director, films) -> {
				director.setFilms(films);
				result.add(director);
			});
			
			return result;
		} catch (Exception exc) {
			throw new RuntimeException(exc.getMessage());
		} finally {
			ConnectionPool.closeConnection(connection);
		}
	}

	@Override
	public Director findDirectorWithFilmsFromDate(long directorId, String date) {
		Connection connection = ConnectionPool.getInstance().takeConnection();
		
		try {
			PreparedStatement pst = connection.prepareStatement(
					"select \"firstName\", \"lastName\", \"director\".\"id\", \"dob\", \"film\".\"id\", \"name\", \"releaseDate\", \"genre\", \"directorId\""
					+ " from \"film\""
					+ " inner join \"director\""
					+ " on \"film\".\"directorId\" = \"director\".\"id\""
					+ " where \"directorId\" = ? and \"releaseDate\" > ?");
			
			pst.setLong(1, directorId);
			pst.setDate(2, java.sql.Date.valueOf(LocalDate.parse(date)));
			
			ResultSet rs = pst.executeQuery();
			
			Director director = null;
			
			while (rs.next()) {
				if (director == null) {
					director = secondDirectorMapper.mapRow(rs).getKey();
				} else {
					director.addFilm(secondDirectorMapper.mapRow(rs).getKey().getFilms().get(0));					
				}
			}
			
			return director;
		} catch (Exception exc) {
			throw new RuntimeException(exc.getMessage());
		} finally {
			ConnectionPool.closeConnection(connection);
		}
	}

}
