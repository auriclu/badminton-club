package com.club.badminton.config;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * Replace web.xml file
 * Bootstraps the Spring context and maps DispatcherServlet
 */
public class AppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    // Load core application context (Database, DAOs, Services)
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[] { AppConfig.class };
    }

    // Load web context (Controllers, View Resolvers, Interceptors)
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[] { WebMvcConfig.class };
    }

    // Map the DispatcherServlet to the root URL
    @Override
    protected String[] getServletMappings() {
        return new String[] { "/" };
    }
}