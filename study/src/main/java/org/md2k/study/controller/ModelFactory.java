package org.md2k.study.controller;

import org.md2k.study.model_view.EMA_test.EMATestManager;
import org.md2k.study.model_view.Model;
import org.md2k.study.model_view.app_install.AppInstallManager;
import org.md2k.study.model_view.app_reset.AppResetManager;
import org.md2k.study.model_view.app_service.AppServiceManager;
import org.md2k.study.model_view.app_settings.AppSettingsManager;
import org.md2k.study.model_view.app_start.AppStartManager;
import org.md2k.study.model_view.app_stop.AppStopManager;
import org.md2k.study.model_view.clear_config.ClearConfigManager;
import org.md2k.study.model_view.clear_data.ClearDataManager;
import org.md2k.study.model_view.config_download.ConfigDownloadManager;
import org.md2k.study.model_view.config_info.ConfigInfoManager;
import org.md2k.study.model_view.data_quality.DataQualityManager;
import org.md2k.study.model_view.datakit_connect.DataKitConnectManager;
import org.md2k.study.model_view.day_start_end.DayStartEndInfoManager;
import org.md2k.study.model_view.day_type.DayTypeManager;
import org.md2k.study.model_view.intervention.InterventionManager;
import org.md2k.study.model_view.plotter.PlotterManager;
import org.md2k.study.model_view.post_quit.PostQuitManager;
import org.md2k.study.model_view.pre_quit.PreQuitManager;
import org.md2k.study.model_view.privacy_control.PrivacyControlManager;
import org.md2k.study.model_view.selfreport.SelfReportManager;
import org.md2k.study.model_view.sleep_info.SleepInfoManager;
import org.md2k.study.model_view.study_end.StudyEndManager;
import org.md2k.study.model_view.study_info.StudyInfoManager;
import org.md2k.study.model_view.study_start.StudyStartManager;
import org.md2k.study.model_view.study_start_end.StudyStartEndInfoManager;
import org.md2k.study.model_view.user_app.UserAppManager;
import org.md2k.study.model_view.user_app_external.UserAppExternalManager;
import org.md2k.study.model_view.user_info.UserInfoManager;
import org.md2k.study.model_view.user_status.UserStatusManager;
import org.md2k.study.model_view.wakeup_info.WakeupInfoManager;

/**
 * Created by monowar on 3/16/16.
 */
public class ModelFactory {
    public static final String MODEL_CONFIG_INFO = "config_info";
    public static final String MODEL_STUDY_INFO = "study_info";
    public static final String MODEL_APP_INSTALL = "app_install";
    public static final String MODEL_APP_SETTINGS = "app_settings";
    public static final String MODEL_APP_SERVICE = "app_service";
    public static final String MODEL_USER_INFO = "user_info";
    public static final String MODEL_WAKEUP_INFO = "wakeup_info";
    public static final String MODEL_SLEEP_INFO = "sleep_info";
    public static final String MODEL_CLEAR_CONFIG = "clear_config";
    private static final String MODEL_CONFIG_DOWNLOAD = "config_download";
    public static final String MODEL_DATA_QUALITY = "data_quality";
    public static final String MODEL_PRIVACY = "privacy";
    private static final String MODEL_INTERVENTION = "intervention";
    public static final String MODEL_SELF_REPORT = "self_report";
    public static final String MODEL_PLOTTER = "plotter";
    public static final String MODEL_USER_APP_EXTERNAL = "user_app_external";
    private static final String MODEL_EMA_TEST = "EMA_test";
    public static final String MODEL_DAY_START_END = "day_start_end";
    public static final String MODEL_STUDY_START_END = "study_start_end";
    private static final String MODEL_CLEAR_DATABASE = "clear_database";
    private static final String MODEL_DATAKIT_CONNECT = "datakit_connect";
    public static final String MODEL_DAY_TYPE = "day_type";
    public static final String MODEL_STUDY_START = "study_start";
    public static final String MODEL_STUDY_END = "study_end";
    public static final String MODEL_PRE_QUIT = "pre_quit";
    public static final String MODEL_POST_QUIT = "post_quit";
    public static final String MODEL_USER_APP = "user_app";
    public static final String MODEL_USER_STATUS = "user_status";
    public static final String MODEL_APP_RESET = "app_reset";
    public static final String MODEL_APP_START = "app_start";
    public static final String MODEL_APP_STOP = "app_stop";

    public static Model getModel(ModelManager modelManager, String id, int rank) {
        switch (id) {
            case MODEL_CONFIG_INFO:
                return new ConfigInfoManager(modelManager, id, rank);
            case MODEL_DATAKIT_CONNECT:
                return new DataKitConnectManager(modelManager, id, rank);
            case MODEL_STUDY_INFO:
                return new StudyInfoManager(modelManager, id, rank);
            case MODEL_APP_INSTALL:
                return new AppInstallManager(modelManager, id, rank);
            case MODEL_APP_SETTINGS:
                return new AppSettingsManager(modelManager, id, rank);
            case MODEL_USER_INFO:
                return new UserInfoManager(modelManager, id, rank);
            case MODEL_WAKEUP_INFO:
                return new WakeupInfoManager(modelManager, id, rank);
            case MODEL_SLEEP_INFO:
                return new SleepInfoManager(modelManager, id, rank);
            case MODEL_CLEAR_DATABASE:
                return new ClearDataManager(modelManager, id, rank);
            case MODEL_DAY_TYPE:
                return new DayTypeManager(modelManager, id, rank);
            case MODEL_STUDY_START:
                return new StudyStartManager(modelManager, id, rank);
            case MODEL_STUDY_END:
                return new StudyEndManager(modelManager, id, rank);
            case MODEL_PRE_QUIT:
                return new PreQuitManager(modelManager, id, rank);
            case MODEL_POST_QUIT:
                return new PostQuitManager(modelManager, id, rank);
            case MODEL_CLEAR_CONFIG:
                return new ClearConfigManager(modelManager, id, rank);
            case MODEL_CONFIG_DOWNLOAD:
                return new ConfigDownloadManager(modelManager, id, rank);
            case MODEL_DATA_QUALITY:
                return new DataQualityManager(modelManager, id, rank);
            case MODEL_USER_APP:
                return new UserAppManager(modelManager, id, rank);
            case MODEL_INTERVENTION:
                return new InterventionManager(modelManager, id, rank);
            case MODEL_SELF_REPORT:
                return new SelfReportManager(modelManager, id, rank);
            case MODEL_PLOTTER:
                return new PlotterManager(modelManager, id, rank);
            case MODEL_PRIVACY:
                return new PrivacyControlManager(modelManager, id, rank);
            case MODEL_EMA_TEST:
                return new EMATestManager(modelManager, id, rank);
            case MODEL_DAY_START_END:
                return new DayStartEndInfoManager(modelManager, id, rank);
            case MODEL_STUDY_START_END:
                return new StudyStartEndInfoManager(modelManager, id, rank);
            case MODEL_APP_SERVICE:
                return new AppServiceManager(modelManager, id, rank);
            case MODEL_USER_STATUS:
                return new UserStatusManager(modelManager, id, rank);
            case MODEL_APP_RESET:
                return new AppResetManager(modelManager, id, rank);
            case MODEL_APP_START:
                return new AppStartManager(modelManager, id, rank);
            case MODEL_APP_STOP:
                return new AppStopManager(modelManager, id, rank);
        }
        if (id.endsWith(MODEL_SELF_REPORT))
            return new SelfReportManager(modelManager, id, rank);
        else {
            return new UserAppExternalManager(modelManager, id, rank);
        }
    }

}
