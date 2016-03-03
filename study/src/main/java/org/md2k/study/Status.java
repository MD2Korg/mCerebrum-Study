package org.md2k.study;

import android.os.Parcel;
import android.os.Parcelable;

import org.md2k.datakitapi.time.DateTime;


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
public class Status implements Parcelable {
    int statusCode;
    String statusMessage;
    long timestamp;

    public static final int SUCCESS = 0;
    public static final int APP_NOT_INSTALLED = 1;
    public static final int APP_UPDATE_AVAILABLE = 2;
    public static final int USERID_NOT_DEFINED = 3;
    public static final int WAKEUP_NOT_DEFINED = 4;
    public static final int SLEEP_NOT_DEFINED = 5;
    public static final int APP_NOT_RUNNING = 6;
    public static final int APP_CONFIG_ERROR = 7;
    public static final int CONFIG_FILE_NOT_EXIST = 8;
    public static final int CLEAR_OLD_DATA = 9;
    public static final int DATAKIT_NOT_AVAILABLE = 10;
    public static final int PRIVACY_ACTIVE = 11;
    public static final int DATAQUALITY_GOOD = 12;
    public static final int DATAQUALITY_OFF = 13;
    public static final int DATAQUALITY_LOOSE = 14;
    public static final int DATAQUALITY_NOISY = 15;
    public static final int DATAQUALITY_NOT_WORN = 16;
    public static final int DATAQUALITY_BAD = 17;
    public static final int DAY_START_NOT_AVAILABLE = 18;
    public static final int DAY_COMPLETED = 19;
    public static final int DAY_ERROR = 20;
    public static final int DATABASE_NOT_AVAILABLE = 21;
    public static final int CONNECTION_ERROR = 22;
    public static final int DOWNLOAD_ERROR = 23;
    public static final int STUDY_START_NOT_AVAILABLE = 24;
    public static final int STUDY_RUNNING = 25;
    public static final int STUDY_COMPLETED=26;
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
            "Error: Incorrect Study Name. Clear old Data",
            "Error: DataKit not available",
            "Status: Privacy control activated",
            "OK",
            "Device is off/Not connected",
            "Belt Loose",
            "Noisy",
            "Not Worn",
            "Bad Quality",
            "Warning: Day is not started",
            "Status Ok: Day is completed",
            "ERROR: System Error",
            "ERROR: Database not available",
            "ERROR: Internet Connection Error.",
            "ERROR: File can't be downloaded properly",
            "ERROR: Study not started",
            "Study is running",
            "Study is completed",
    };

    public Status(int statusCode) {
        this.statusCode = statusCode;
        this.statusMessage = message[statusCode];
        this.timestamp = DateTime.getDateTime();
    }

    public Status(int statusCode, String message) {
        this.statusCode = statusCode;
        this.statusMessage = message;
        this.timestamp = DateTime.getDateTime();
    }

    protected Status(Parcel in) {
        statusCode = in.readInt();
        statusMessage = in.readString();
        timestamp = in.readLong();
    }

    public static final Creator<Status> CREATOR = new Creator<Status>() {
        @Override
        public Status createFromParcel(Parcel in) {
            return new Status(in);
        }

        @Override
        public Status[] newArray(int size) {
            return new Status[size];
        }
    };

    public String getStatusMessage() {
        return statusMessage;
    }

    public Status getStatus() {
        return this;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(statusCode);
        dest.writeString(statusMessage);
        dest.writeLong(timestamp);
    }
}
