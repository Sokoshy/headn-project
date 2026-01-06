package com.bibliotheque.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.security.SecureRandom;
import java.util.Base64;

public class CSRFUtil {
    private static final String CSRF_TOKEN_ATTR = "csrf_token";
    private static final String CSRF_PARAM_NAME = "csrf_token";
    private static final SecureRandom secureRandom = new SecureRandom();
    
    /**
     * Génère et stocke un token CSRF dans la session
     */
    public static String generateToken(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        byte[] token = new byte[32];
        secureRandom.nextBytes(token);
        String tokenString = Base64.getUrlEncoder().withoutPadding().encodeToString(token);
        session.setAttribute(CSRF_TOKEN_ATTR, tokenString);
        return tokenString;
    }
    
    /**
     * Récupère le token CSRF depuis la session
     */
    public static String getToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (String) session.getAttribute(CSRF_TOKEN_ATTR);
    }
    
    /**
     * Vérifie si le token CSRF est valide
     */
    public static boolean validateToken(HttpServletRequest request) {
        String sessionToken = getToken(request);
        String requestToken = request.getParameter(CSRF_PARAM_NAME);
        
        if (sessionToken == null || requestToken == null) {
            return false;
        }
        
        return sessionToken.equals(requestToken);
    }
    
    /**
     * Génère un champ input caché pour le token CSRF
     */
    public static String getHiddenField(HttpServletRequest request) {
        String token = getToken(request);
        if (token == null) {
            token = generateToken(request);
        }
        return "<input type=\"hidden\" name=\"" + CSRF_PARAM_NAME + "\" value=\"" + token + "\" />";
    }
    
    /**
     * Génère un paramètre pour les URLs
     */
    public static String getUrlParameter(HttpServletRequest request) {
        String token = getToken(request);
        if (token == null) {
            token = generateToken(request);
        }
        return CSRF_PARAM_NAME + "=" + token;
    }
}