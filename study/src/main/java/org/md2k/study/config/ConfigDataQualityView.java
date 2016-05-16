package org.md2k.study.config;

import org.md2k.datakitapi.source.datasource.DataSource;

/**
 * Created by monowar on 5/9/16.
 */
public class ConfigDataQualityView {
    public String name;
    public Plotter plotter;
    public Video video;
    public Message message;
    public class Plotter{
        public boolean enable;
        public DataSource datasource;
    }
    public class Video{
        public boolean enable;
        public String link;
    }
    public class Message{
        public boolean enable;
        public String text;
    }
}
