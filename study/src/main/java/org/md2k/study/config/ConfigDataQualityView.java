package org.md2k.study.config;

import org.md2k.datakitapi.source.datasource.DataSource;

/**
 * Created by monowar on 5/9/16.
 */
public class ConfigDataQualityView {
    private String name;
    private Plotter plotter;
    private Video video;
    private Message message;

    public class Plotter{
        private boolean enable;
        private DataSource datasource;

        public boolean isEnable() {
            return enable;
        }

        public DataSource getDatasource() {
            return datasource;
        }
    }

    public class Video{
        private boolean enable;
        private String link;

        public boolean isEnable() {
            return enable;
        }

        public String getLink() {
            return link;
        }
    }

    public class Message{
        private boolean enable;
        private String text;

        public boolean isEnable() {
            return enable;
        }

        public String getText() {
            return text;
        }
    }

    public String getName() {
        return name;
    }

    public Plotter getPlotter() {
        return plotter;
    }

    public Video getVideo() {
        return video;
    }

    public Message getMessage() {
        return message;
    }
}
