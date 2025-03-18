package org.embeddedt.archaicfix.config;

/**
 * A really basic wrapper for config to simplify handling them in external code.
 */
public class ConfigException extends Exception {

    private static final long serialVersionUID = 1038100965627501494L;

    public ConfigException(String message) {
        super(message);
    }

    public ConfigException(Throwable cause) {
        super(cause);
    }
}
