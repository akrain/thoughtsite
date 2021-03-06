/* Copyright 2010 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS.
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.google.ie.common.exception;

/**
 * An Object that extends the RuntimeException object.
 * The class is used to wrap the Runtime exceptions that can occur in Ideas
 * Exchange Project.
 * 
 * @author asirohi
 * 
 */
public class SystemException extends RuntimeException {

    /** A unique identifier for the class */
    private static final long serialVersionUID = -5912342563924088330L;

    /** An error code that is used to display locale specific error messages */
    private String errorCode;

    /**
     * Constructs a new {@link SystemException} object with the specified detail
     * message and an error code.
     * 
     * @param errorCode an error code of the exception.
     * @param message the detail message of the exception.
     */
    public SystemException(String errorCode, String message) {
        this(errorCode, message, null);
    }

    /**
     * Constructs a new {@link SystemException} object with the specified error
     * code and cause.
     * 
     * @param errorCode an error code of the exception.
     * @param message the detail message of the exception.
     */
    public SystemException(String errorCode, Throwable cause) {
        this(errorCode, null, cause);
    }

    /**
     * Constructs a new {@link SystemException} object with the specified error
     * code,message and cause.
     * 
     * @param errorCode an error code of the exception.
     * @param message the detail message of the exception.
     * @param cause an object of class {@link Throwable} representing the cause
     *        (which is saved for later retrieval by the Throwable.getCause()
     *        method).
     */

    public SystemException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * Get error code of the.
     * 
     * @return String
     */

    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Set the error code.
     * 
     * @param errorCode
     */

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        String s = super.toString();
        return (errorCode != null) ? (errorCode + " : " + s) : s;
    }

}

