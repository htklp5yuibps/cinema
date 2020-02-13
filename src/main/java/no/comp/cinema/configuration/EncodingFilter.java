package no.comp.cinema.configuration;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import org.apache.catalina.filters.SetCharacterEncodingFilter;

@WebFilter(urlPatterns = "*")
public class EncodingFilter extends SetCharacterEncodingFilter {
	
	@Override
	public void setEncoding(String encoding) {
		super.setEncoding("UTF-8");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		response.setContentType("text/plain; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		super.doFilter(request, response, chain);
	}
	
//	@Override
//	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//			throws IOException, ServletException {
//		request.setCharacterEncoding("UTF-8");
//		response.setContentType("text/plain; charset=UTF-8");
//		response.setCharacterEncoding("UTF-8");
//		
//		chain.doFilter(request, response);
//	}
	
}
