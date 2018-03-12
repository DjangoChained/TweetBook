/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

/**
 *
 *
 */
public class DAOConfigurationException extends RuntimeException {
    /*
     * Constructeurs
     */

    /**
     *
     * @param message
     */

    public DAOConfigurationException( String message ) {
        super( message );
    }

    /**
     *
     * @param message
     * @param cause
     */
    public DAOConfigurationException( String message, Throwable cause ) {
        super( message, cause );
    }

    /**
     *
     * @param cause
     */
    public DAOConfigurationException( Throwable cause ) {
        super( cause );
    }
}