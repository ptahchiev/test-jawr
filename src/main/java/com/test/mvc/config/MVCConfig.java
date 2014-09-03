package com.test.mvc.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;
import javax.servlet.ServletContext;

import net.jawr.web.servlet.JawrSpringController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.test.mvc.controller.HomepageController;

@Configuration
@EnableWebMvc
@EnableScheduling
@ComponentScan(basePackages = { "com.test.mvc" })
public class MVCConfig extends WebMvcConfigurerAdapter implements SchedulingConfigurer {

    @Autowired
    private ServletContext context; 

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**").addResourceLocations("/resources/**").setCachePeriod(31556926);
        registry.addResourceHandler("/webjars/**").addResourceLocations("/webjars/");
    }


    @Override
    public void configureTasks(final ScheduledTaskRegistrar paramScheduledTaskRegistrar) {
    	
    }
    
    @Bean
    public HomepageController homepageController() {
        return new HomepageController();
    }
	
	@Bean
    public HandlerMapping handlerMapping() {
        final SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        final Map<String, Object> urlMap = new HashMap<String, Object>();
		urlMap.put("/", homepageController());
        urlMap.put("/**/**.png", jawrImageController());
        urlMap.put("/**/**.jpg", jawrImageController());
        urlMap.put("/**/**.jpeg", jawrImageController());
        urlMap.put("/**/**.gif", jawrImageController());
        urlMap.put("/**/**.ico", jawrImageController());

        urlMap.put("/**/**.js", jawrJsController());
        urlMap.put("/**/**.css", jawrCSSController());
        mapping.setUrlMap(urlMap);
        mapping.setOrder(Integer.MIN_VALUE);

        return mapping;
    }
	
	@Bean
	protected ViewResolver defaultInternalResourceViewResolver() {
        final InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setViewClass(org.springframework.web.servlet.view.JstlView.class);
        viewResolver.setOrder(1);
        viewResolver.setPrefix("/");
        viewResolver.setSuffix(".jsp");
        viewResolver.setRedirectHttp10Compatible(false);

        return viewResolver;
    }
	
	/* JAWR */

    protected JawrSpringController defaultJawrSpringController(final String type) {
        final JawrSpringController controller = new JawrSpringController();
		controller.setServletContext(context);

        final Properties props = new Properties();

        props.setProperty("jawr.debug.on", "false");
        props.setProperty("jawr.gzip.on", "true");
        props.setProperty("jawr.gzip.ie6.on", "false");
        props.setProperty("jawr.charset.name", "UTF-8");

        props.setProperty("jawr.js.commonURLPrefix", "/resources/js/");
        props.setProperty("jawr.js.bundle.basedir", "/resources/theme");

        props.setProperty("jawr.css.commonURLPrefix", "/resources/css/");
        props.setProperty("jawr.css.bundle.basedir", "/resources/theme");
        //props.setProperty("jawr.css.bundle.factory.global.preprocessors", "smartsprites");

        //props.setProperty("jawr.url.contextpath.override", "/storefront");
        //props.setProperty("jawr.url.contextpath.ssl.override", "/storefront");
        props.setProperty("jawr.url.contextpath.override.used.in.debug.mode", "true");
		
		props.setProperty("jawr.css.bundle.common.global", "true");
        props.setProperty("jawr.css.bundle.common.composite", "true");
        props.setProperty("jawr.css.bundle.common.id", "/resources/theme/common/css/all.css");
		props.setProperty("jawr.css.bundle.common.child.names", "jqueryui,jqueryuimin");
		
		//JQUERY-UI
		props.setProperty("jawr.css.bundle.jqueryui.mappings", "webjars/jquery-ui/1.11.0/jquery-ui.theme.css");
        props.setProperty("jawr.css.bundle.jqueryuimin.debugnonly", "true");
		
        props.setProperty("jawr.css.bundle.jqueryuimin.mappings", "webjars/jquery-ui/1.11.0/jquery-ui.theme.min.css");
        props.setProperty("jawr.css.bundle.jqueryuimin.debugnever", "true");

        controller.setConfiguration(props);
        controller.setType(type);
        try {
			controller.afterPropertiesSet();
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
        return controller;
    }

    @Bean(name = "jawrJsController")
    @RequestMapping(value = "/**/*.js", method = RequestMethod.GET)
    protected JawrSpringController jawrJsController() {
        return defaultJawrSpringController("js");
    }

    @Bean(name = "jawrImageController")
    @RequestMapping(value = {"*.gif", "*.ico", "*.png", "*.jpg", "*.jpeg"})
    protected JawrSpringController jawrImageController() {
        return defaultJawrSpringController("img");
    }

    @DependsOn(value = "jawrImageController")
    @Bean(name = "jawrCSSController")
    @RequestMapping(value = "/**/*.css", method = RequestMethod.GET)
    protected JawrSpringController jawrCSSController() {
        return defaultJawrSpringController("css");
    }

}
