package no.comp.cinema.servlet;

import no.comp.cinema.entity.Director;
import no.comp.cinema.helper.DateConverter;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.comp.cinema.servlet.command.CommandExecutor;
import no.comp.cinema.dao.DaoProvider;

@WebServlet(urlPatterns = "/directors/*")
public class DirectorsServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	CommandExecutor getCommands = new CommandExecutor();
	CommandExecutor postCommands = new CommandExecutor();

	@Override
	public void init() throws ServletException {
		super.init();
		
		/* pattern example: /cinema/directors */
		this.getCommands.addCommand("\\/", (req, resp) -> {
			writeResponse(resp, DaoProvider.getDirectorDao().read());
		});
		
		/* pattern example: /cinema/directors/10 */
		this.getCommands.addCommand("^\\/[0-9]{1,}$", (req, resp) -> {
			boolean wasFound = false;
			long directorId = Long.parseLong(req.getPathInfo().replace("/", ""));
			List<Director> directors = DaoProvider.getDirectorDao().read();
			
 			for (Director director: directors) {
 				if (director.getId() == directorId) {
 					writeResponse(resp, director);
 					wasFound = true;
 				}
 			}
 			
 			if (!wasFound) {
 				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
 			}
		});
		
		/* pattern example: /cinema/directors/directorName */
		this.getCommands.addCommand("^\\/[A-ZА-Яa-zа-я-]{1,}$", (req, resp) -> {
			List<Director> directors = DaoProvider.getDirectorDao().read();
			String incomingName = req.getPathInfo().replace("/", "").toLowerCase();
			
			directors = directors.stream().filter(director -> {
				String nameSurname = (director.getFirstName() + "-" + director.getLastName()).toLowerCase();
				String surnameName = (director.getLastName() + "-" + director.getFirstName()).toLowerCase();
				
				return nameSurname.contains(incomingName) || surnameName.contains(incomingName);
			}).collect(Collectors.toList());
		
			if (!directors.isEmpty()) {
				writeResponse(resp, directors);				
			} else {
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		});
		
		/* pattern example: /cinema/directors/{director id}/{date in format: yyyy-MM-dd} */
		this.getCommands.addCommand("\\/[0-9]{1,}\\/[0-9]{4}-[0-9]{2}-[0-9]{2}", (req, resp) -> {
			Long directorId = Long.parseLong(req.getPathInfo().replaceFirst("/", "").split("/")[0]);
			String incomingDate = req.getPathInfo().replaceFirst("/", "").split("/")[1].replace("-00", "-01");
			
			Director director = DaoProvider.getDirectorDao().findDirectorWithFilmsFromDate(directorId, incomingDate);
			
			if (director != null) {
				writeResponse(resp, director);
			} else {
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		});
		
		/* pattern example: /cinema/directors/{director name}/{date in format: yyyy-MM-dd} */
		this.getCommands.addCommand("\\/[A-ZА-Яa-zа-я-]{1,}/[0-9]{4}-[0-9]{2}-[0-9]{2}", (req, resp) -> {
			String directorName = req.getPathInfo().replaceFirst("/", "").split("/")[0].toLowerCase().replace("-", " ");
			String incomingDate = req.getPathInfo().replaceFirst("/", "").split("/")[1].replace("-00", "-01");
			
			List<Director> directors = DaoProvider.getDirectorDao().read();
			
			directors = directors.stream().filter(director -> {
				director.setFilms(director.getFilms().stream().filter(film -> {
					try {
						Date releaseDate = DateConverter.StringToDate(film.getReleaseDate());
						return releaseDate.compareTo(DateConverter.StringToDate(incomingDate)) > 0;						
					} catch (Exception exc) {
						throw new RuntimeException(exc.getMessage());
					}
				}).collect(Collectors.toList()));
				return !director.getFilms().isEmpty();
			}).filter(director -> {
				String nameSurname = director.getFirstName().toLowerCase() + " " + director.getLastName().toLowerCase();
				String surnameName = director.getLastName().toLowerCase() + " " + director.getFirstName().toLowerCase();
				return nameSurname.contains(directorName) || surnameName.contains(directorName);
			}).collect(Collectors.toList());
			
			if (!directors.isEmpty()) {
				writeResponse(resp, directors);
			} else {
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		});
	}
	
	private void writeResponse(HttpServletResponse response, Object obj) throws Exception {
		response.setStatus(HttpServletResponse.SC_OK);
		StringWriter writer = new StringWriter();
		new ObjectMapper().writeValue(writer, obj);
		ServletOutputStream out = response.getOutputStream();
		out.write(writer.toString().getBytes("UTF-8"));
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		getCommands.findAndExecute(req, resp);
	}
}
