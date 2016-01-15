package org.md2k.study;

import org.md2k.datakitapi.status.StatusCodes;

import java.io.Serializable;

/**
 * Copyright (c) 2015, The University of Memphis, MD2K Center
 * - Syed Monowar Hossain <monowar.hossain@gmail.com>
 * All rights reserved.
 * <p/>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p/>
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * <p/>
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <p/>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
public class Status implements Serializable{
    int statusCode;
    String statusMessage;

    public static final int SUCCESS = 0;
    public static final int APP_NOT_INSTALLED = 1;
    public static final int APP_UPDATE_AVAILABLE = 2;
    public static final int USERID_NOT_DEFINED = 3;
    public static final int SLEEPEND_NOT_DEFINED = 4;
    public static final int SLEEPSTART_NOT_DEFINED = 5;
    public static final int APP_NOT_RUNNING=6;
    public static final int APP_CONFIG_ERROR=7;
    public static final int CONFIG_FILE_NOT_EXIST=8;
    public static final int CLEAR_OLD_DATA=9;
    public static final int DATAKIT_NOT_INSTALLED=10;
    public static final String[] message = new String[]{
            "Status: OK",
            "Error: Application not installed properly",
            "Warning: Update available for application",
            "Error: UserID not defined",
            "Error: Wakeup time not defined",
            "Error: Sleep time not defined",
            "Error: Application not running",
            "Error: Application not configured properly",
            "Error: Missing configuration files",
            "Error: Clear Old Data",
            "Error: DataKit not installed"
    };

    public Status(int statusCode, String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public Status(int statusCode) {
        this.statusCode = statusCode;
        this.statusMessage = message[statusCode];
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public Status getStatus() {
        return this;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
