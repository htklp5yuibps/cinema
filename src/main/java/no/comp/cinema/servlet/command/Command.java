package no.comp.cinema.servlet.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@FunctionalInterface
public interface Command {
	void execute(HttpServletRequest request, HttpServletResponse response) throws Exception;
}
