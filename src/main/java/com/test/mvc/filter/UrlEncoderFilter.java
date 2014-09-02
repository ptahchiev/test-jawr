package com.test.mvc.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.test.mvc.filter.wrapper.UrlEncodeHttpRequestWrapper;

@Component(value = UrlEncoderFilter.NAME)
public class UrlEncoderFilter extends OncePerRequestFilter {
    
    protected final Logger LOG = LogManager.getLogger(getClass());

    public static final String NAME = "urlEncoderFilter";
    
    private static final String URL_ENCODING_ATTRIBUTES = "encodingAttributes";
    
    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {
        if (LOG.isDebugEnabled()) {
            LOG.debug(" The incoming URL : [" + request.getRequestURL().toString() + "]");
        }

//        final HttpSession session = request.getSession(false);
//
//        session.setAttribute(URL_ENCODING_ATTRIBUTES, "en");
        final UrlEncodeHttpRequestWrapper wrappedRequest =
            new UrlEncodeHttpRequestWrapper(request, "en");
        wrappedRequest.setAttribute(URL_ENCODING_ATTRIBUTES, "en");
        wrappedRequest.setAttribute("originalContextPath", request.getContextPath());
        if (LOG.isDebugEnabled()) {
            LOG.debug("ContextPath=[" + wrappedRequest.getContextPath() + "]" + " Servlet Path= ["
                + wrappedRequest.getServletPath() + "]" + " Request Url= [" + wrappedRequest.getRequestURL() + "]");
        }
        filterChain.doFilter(wrappedRequest, response);
    }
}
