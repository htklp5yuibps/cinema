package no.comp.cinema.dao;

import java.util.List;

import no.comp.cinema.entity.Entity;

public interface Dao<T extends Entity> {
	List<T> read();
	T read(long type);
}
