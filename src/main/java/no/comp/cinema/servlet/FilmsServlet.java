package no.comp.cinema.servlet;

import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
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

import no.comp.cinema.dao.DaoProvider;
import no.comp.cinema.entity.Film;
import no.comp.cinema.entity.Director;
import no.comp.cinema.servlet.command.CommandExecutor;

@WebServlet(urlPatterns = "/films/*")
public class FilmsServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private CommandExecutor getCommands = new CommandExecutor();

	@Override
	public void init() throws ServletException {
		super.init();

		/* pattern example: /cinema/films */
		
		this.getCommands.addCommand("\\/", (req, resp) -> {
			List<Film> films = DaoProvider.getFilmDao().read();
			if (!films.isEmpty()) {
				writeResponse(resp, films);
			} else {
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		});

		/* pattern example: /cinema/films/filmName */
		this.getCommands.addCommand("^\\/[A-ZА-Яa-zа-я0-9-]{1,}$", (req, resp) -> {
			String filmName = req.getPathInfo().replace("/", "").replace("-", " ").toLowerCase();
			List<Director> directors = DaoProvider.getDirectorDao().read();
			
			directors = directors.stream().filter(director -> {
				List<Film> films = director.getFilms();
				films = films.stream().filter(film -> {
					return film.getName().toLowerCase().contains(filmName);
				}).collect(Collectors.toList());
				director.setFilms(films);
				return !films.isEmpty();
			}).collect(Collectors.toList());
			
			if (!directors.isEmpty()) {
				writeResponse(resp, directors);
			} else {
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		});

		/* pattern example: /cinema/films/1992-01-01 */
		this.getCommands.addCommand("^\\/[0-9]{4}-[0-9]{2}-[0-9]{2}$", (req, resp) -> {
			String incomingDateString = req.getPathInfo().replace("/", "").replace("-00", "-01");
			
			List<Director> directorsWithFilms = DaoProvider.getDirectorDao().findAllFromDate(incomingDateString);

			if (!directorsWithFilms.isEmpty()) {
				writeResponse(resp, directorsWithFilms);
			} else {
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		});
	}

	private void writeResponse(HttpServletResponse response, Object obj) throws Exception {
		StringWriter writer = new StringWriter();
		new ObjectMapper().writeValue(writer, obj);
		ServletOutputStream out = response.getOutputStream();
		response.setStatus(HttpServletResponse.SC_OK);
		out.write(writer.toString().getBytes("UTF-8"));
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		getCommands.findAndExecute(req, resp);
	}

}
