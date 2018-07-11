/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.ws.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * TODO
 */
public class StartupStatus {
    private static final Log log = LogFactory.getLog(StartupStatus.class);

    private static final StartupStatus instance = new StartupStatus();

    public static StartupStatus getInstance() {
        return instance;
    }

    public static void addError(String message, Throwable cause) {
        log.error(message, cause);
        getInstance().addError(new Error(message, cause));
    }

    // ----------------------------------------------------------------------
    // The instance
    // ----------------------------------------------------------------------

    private final List<Error> errors = new ArrayList<>();

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public List<Error> getErrors() {
        return errors;
    }

    private void addError(Error error) {
        errors.add(error);
    }

    public static class Error {
        public final String message;
        public final String cause;

        public Error(String message) {
            this.message = message;
            this.cause = "";
        }

        public Error(String message, Throwable cause) {
            this.message = message;
            this.cause = cause.getMessage();
        }
    }

}
