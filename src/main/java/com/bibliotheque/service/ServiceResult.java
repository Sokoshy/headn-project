package com.bibliotheque.service;

/**
 * Classe générique pour encapsuler les résultats des opérations de service
 */
public class ServiceResult<T> {
    private boolean success;
    private T data;
    private String message;
    
    private ServiceResult(boolean success, T data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }
    
    /**
     * Crée un résultat réussi
     */
    public static <T> ServiceResult<T> success(T data, String message) {
        return new ServiceResult<>(true, data, message);
    }
    
    /**
     * Crée un résultat réussi sans message
     */
    public static <T> ServiceResult<T> success(T data) {
        return new ServiceResult<>(true, data, null);
    }
    
    /**
     * Crée un résultat d'erreur
     */
    public static <T> ServiceResult<T> error(String message) {
        return new ServiceResult<>(false, null, message);
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public T getData() {
        return data;
    }
    
    public String getMessage() {
        return message;
    }
    
    public boolean hasData() {
        return data != null;
    }
    
    public boolean hasMessage() {
        return message != null && !message.trim().isEmpty();
    }
}