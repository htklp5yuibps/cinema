package no.comp.cinema.servlet.command;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CommandExecutor {
	private Map<String, Command> commands = new HashMap<>();
	
	public void addCommand(String pattern, Command command) {
		this.commands.put(pattern, command);
	}
	
	public void findAndExecute(HttpServletRequest request, HttpServletResponse response) {
		try {
			String pathInfo = request.getPathInfo();
			boolean wasFound = false;
			
			if (pathInfo == null) {
				pathInfo = "/";
			}

			for (Map.Entry<String, Command> entry: commands.entrySet()) {
				if (pathInfo.matches(entry.getKey())) {
					entry.getValue().execute(request, response);
					wasFound = true;
				}
			}
			
			if (!wasFound) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		} catch (Exception exc) {
			exc.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}
}
