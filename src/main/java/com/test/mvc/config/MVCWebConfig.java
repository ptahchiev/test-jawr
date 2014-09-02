package com.test.mvc.config;

import javax.servlet.FilterRegistration;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.core.annotation.Order;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.util.HttpSessionMutexListener;

import com.test.mvc.filter.UrlEncoderFilter;


@Order(value = 0)
public class MVCWebConfig implements WebApplicationInitializer {

    @Override
    public void onStartup(final ServletContext servletContext) throws ServletException {

        final AnnotationConfigWebApplicationContext webCtx = new AnnotationConfigWebApplicationContext();
        webCtx.register(MVCConfig.class);

        servletContext.addListener(new RequestContextListener());
        servletContext.addListener(new HttpSessionMutexListener());
        servletContext.addListener(new ContextLoaderListener(webCtx));


        /* Spring Delegating Dispatcher Servlet */
        final Servlet dispatcherServlet = new DispatcherServlet(webCtx);
        final ServletRegistration.Dynamic dispatcherServletReg = servletContext.addServlet("dispatcherServlet", dispatcherServlet);
        dispatcherServletReg.setLoadOnStartup(1);
        dispatcherServletReg.setInitParameter("contextConfigLocation", "");
        dispatcherServletReg.addMapping("/");

        /* Character Encoding Filter */
        final FilterRegistration charEncodingfilterReg = servletContext.addFilter("characterEncodingFilter", CharacterEncodingFilter.class);
        charEncodingfilterReg.setInitParameter("encoding", "UTF-8");
        charEncodingfilterReg.setInitParameter("forceEncoding", "true");
        charEncodingfilterReg.addMappingForServletNames(null, false, dispatcherServletReg.getName());

        /* Storefront Delegating Filter */
        final FilterRegistration storefrontFilterChainReg = servletContext.addFilter(UrlEncoderFilter.NAME, DelegatingFilterProxy.class);
        storefrontFilterChainReg.addMappingForServletNames(null, false, dispatcherServletReg.getName());
        
        /* HiddenHttpMethodFilter Filter */
        final FilterRegistration hiddenHttpMethodFilterReg = servletContext.addFilter("hiddenHttpMethodFilter", HiddenHttpMethodFilter.class);
        hiddenHttpMethodFilterReg.addMappingForServletNames(null, false, dispatcherServletReg.getName());
    }
}
