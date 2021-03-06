package org.md2k.study;

import android.os.Environment;

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
public class Constants{
    public static final String CONFIG_FILENAME = "config.json";

    public static final String CONFIG_DIRECTORY_BASE= Environment.getExternalStorageDirectory().getAbsolutePath() + "/mCerebrum/";
    public static final String CONFIG_DIRECTORY_ROOT= Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
    public static final String CONFIG_DIRECTORY= Environment.getExternalStorageDirectory().getAbsolutePath() + "/mCerebrum/org.md2k.study/";
    public static final String TEMP_DIRECTORY=Environment.getExternalStorageDirectory().getAbsolutePath()+"/mCerebrum/temp/";
    public static final String CONFIG_DOWNLOAD_LINK="https://github.com/MD2Korg/mCerebrum-Configuration/releases/download/";
    public static final String NOTIFICATION_FILENAME = "notification.json";
    public static final String VERSION_MIN_CONFIG="1.4.2";
    public static final String INTENT_RESTART="intent_restart";
    public static final String CONFIG_ZIP_FILENAME="config_zip_filename";
}
