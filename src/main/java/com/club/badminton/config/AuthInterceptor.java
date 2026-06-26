package com.club.badminton.config;

import com.club.badminton.model.User;
import com.club.badminton.model.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Validates user sessions and handles role-bsed access control
 */
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath(); // Storing this to make the code cleaner

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;

        // Authentication Check
        if (user == null) {
            response.sendRedirect(contextPath + "/auth/login?error=unauthorized");
            return false;
        }

        // Role based access

        // Admin can access everything
        if (user.getRole() == Role.ADMIN) {
            return true;
        }

        // Coach access
        if (uri.startsWith(contextPath + "/coach")) {
            if (user.getRole() == Role.COACH) {
                return true;
            }
        }

        // Member area access
        if (uri.startsWith(contextPath + "/member")) {
            // Coaches and Members get access to all /member/ pages
            if (user.getRole() == Role.COACH || user.getRole() == Role.MEMBER) {
                return true;
            }

            // Guests access only events page
            if (user.getRole() == Role.GUEST) {
                if (uri.equals(contextPath + "/member/events")) {
                    return true;
                } else {
                    // If a Guest tries to access /member/profile, bounce them safely to events
                    response.sendRedirect(contextPath + "/member/events");
                    return false;
                }
            }
        }

        // No permission for the requested path
        response.sendRedirect(contextPath + "/auth/login?error=forbidden");
        return false;
    }
}