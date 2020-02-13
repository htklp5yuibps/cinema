package no.comp.cinema.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import no.comp.cinema.entity.Entity;

@FunctionalInterface
public interface RowMapper<T> {
	T mapRow(ResultSet rs) throws SQLException;
}
