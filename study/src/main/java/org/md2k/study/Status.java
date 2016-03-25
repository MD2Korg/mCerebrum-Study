package org.md2k.study;

import android.os.Parcel;
import android.os.Parcelable;

import org.md2k.utilities.Report.Log;


/**
 * Copyright (c) 2015, The University of Memphis, MD2K Center
 * - Syed Monowar Hossain <monowar.hossain@gmail.com>
 * All rights reserved.
 * <p/>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p/>
 * * Redistributions of source status must retain the above copyright notice, this
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
    private static final String TAG = Status.class.getSimpleName();
    int status;
    int rank;
    String message;

    public static final int RANK_BEGIN = 8;
    public static final int RANK_CONFIG = 7;
    public static final int RANK_SYSTEM = 6;
    public static final int RANK_DATAKIT = 5;
    public static final int RANK_ADMIN_REQUIRED = 4;
    public static final int RANK_ADMIN_OPTIONAL = 3;
    public static final int RANK_USER_REQUIRED = 2;
    public static final int RANK_USER_OPTIONAL = 1;
    public static final int RANK_SUCCESS = 0;

    public static final int SUCCESS = 0;
    public static final int CONFIG_FILE_NOT_EXIST = 700;
    public static final int APP_NOT_INSTALLED = 699;
    public static final int APP_UPDATE_AVAILABLE = 698;
    public static final int APP_CONFIG_ERROR = 697;
    public static final int APP_NOT_ACTIVE = 696;
    public static final int DATAKIT_NOT_AVAILABLE = 599;
    public static final int DATABASE_NOT_AVAILABLE = 598;
    public static final int CLEAR_OLD_DATA = 499;
    public static final int USERID_NOT_DEFINED = 498;
    public static final int WAKEUP_NOT_DEFINED = 497;
    public static final int SLEEP_NOT_DEFINED = 496;
    public static final int DAY_TYPE_NOT_DEFINED = 495;
    public static final int APP_NOT_RUNNING = 299;
    public static final int DAY_START_NOT_AVAILABLE = 298;
    public static final int DAY_COMPLETED = 297;
    public static final int DAY_ERROR = 296;
    public static final int STUDY_START_NOT_AVAILABLE = 295;
    public static final int STUDY_RUNNING = 294;
    public static final int STUDY_COMPLETED = 293;

    public static final int PRIVACY_ACTIVE = 199;
    public static final int DATAQUALITY_GOOD = 198;
    public static final int DATAQUALITY_OFF = 197;
    public static final int DATAQUALITY_LOOSE = 196;
    public static final int DATAQUALITY_NOISY = 195;
    public static final int DATAQUALITY_NOT_WORN = 194;
    public static final int DATAQUALITY_BAD = 193;
    public static final int CONNECTION_ERROR = 22;
    public static final int DOWNLOAD_ERROR = 23;
    public static final int SYSTEM_ERROR = 27;
    public static final int NOT_DEFINED = 1;

    private String retrieveMessage(int id) {
        switch (id) {
            case SUCCESS:
                return "Status: OK";
            case APP_NOT_INSTALLED:
                return "Error: Application not installed properly";
            case APP_UPDATE_AVAILABLE:
                return "Warning: Update available for application";
            case USERID_NOT_DEFINED:
                return "Error: UserID not defined";
            case WAKEUP_NOT_DEFINED:
                return "Error: Wakeup time not defined";
            case SLEEP_NOT_DEFINED:
                return "Error: Sleep time not defined";
            case DAY_TYPE_NOT_DEFINED:
                return "Error: Pre/Post Quit Day is not defined";
            case APP_NOT_RUNNING:
                return "Error: Application not running";
            case APP_CONFIG_ERROR:
                return "Error: Application not configured properly";
            case CONFIG_FILE_NOT_EXIST:
                return "Error: Missing configuration files";
            case CLEAR_OLD_DATA:
                return "Error: Incorrect StudyName in Database. Clear old Data";
            case DATAKIT_NOT_AVAILABLE:
                return "Error: DataKit not available";
            case PRIVACY_ACTIVE:
                return "Status: Privacy control activated";
            case DATAQUALITY_GOOD:
                return "OK";
            case DATAQUALITY_OFF:
                return "Device is off/Not connected";
            case DATAQUALITY_LOOSE:
                return "Not properly worn";
            case DATAQUALITY_NOISY:
                return "Noisy";
            case DATAQUALITY_NOT_WORN:
                return "Not Worn";
            case DATAQUALITY_BAD:
                return "Bad Quality";
            case DAY_START_NOT_AVAILABLE:
                return "Warning: Day is not started";
            case DAY_COMPLETED:
                return "Status Ok: Day is completed";
            case DAY_ERROR:
                return "ERROR: System Error";
            case DATABASE_NOT_AVAILABLE:
                return "ERROR: Database not available";
            case CONNECTION_ERROR:
                return "ERROR: Internet Connection Error";
            case DOWNLOAD_ERROR:
                return "ERROR: File can't be downloaded properly";
            case STUDY_START_NOT_AVAILABLE:
                return "ERROR: Study not started";
            case STUDY_RUNNING:
                return "Study is running";
            case STUDY_COMPLETED:
                return "Study is completed";
            case SYSTEM_ERROR:
                return "ERROR: SYSTEM_ERROR, Download Configuration file";
            case NOT_DEFINED:
                return "ERROR: Not Defined";
            case APP_NOT_ACTIVE:
                return "ERROR: Application deactivated";
            default:
                Log.d(TAG, "id=" + id);
                return "Don't Know";
        }
    }

    public Status(int rank, int status) {
        this.status = status;
        this.rank = rank;
        this.message = retrieveMessage(status);
        log();
    }

    public Status(int rank, int status, String MESSAGE) {
        this.rank = rank;
        this.status = status;
        this.message = MESSAGE;
    }

    public String log() {
        return "(" + rank + ", " + status + ") - " + message;
    }

    protected Status(Parcel in) {
        rank = in.readInt();
        status = in.readInt();
        message = in.readString();
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

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public int getRank() {
        return rank;
    }

    public boolean equals(Status status) {
        return !(rank != status.rank || this.status != status.status);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(rank);
        dest.writeInt(status);
        dest.writeString(message);
    }
}
