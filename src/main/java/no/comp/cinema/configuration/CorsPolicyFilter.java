package no.comp.cinema.configuration;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebFilter;

@WebFilter(urlPatterns = "*")
public class CorsPolicyFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String origin = ((HttpServletRequest)request).getHeader("Origin");
		
		((HttpServletResponse)response).addHeader("Access-Control-Allow-Origin", origin);
		((HttpServletResponse)response).addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
		((HttpServletResponse)response).addHeader("Access-Control-Allow-Credentials", "true");
		
		chain.doFilter(request, response);
	}

}
