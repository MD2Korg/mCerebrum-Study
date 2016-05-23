package org.md2k.study.config;

import java.util.ArrayList;

/**
 * Created by monowar on 5/19/16.
 */
public class ConfigDayStartEnd {
    private String by;
    private String base;
    private ArrayList<ConfigNotify> notify;

    public class ConfigNotify {
        private String type;
        private long offset;
        private ArrayList<String> parameters;

        public String getType() {
            return type;
        }

        public long getOffset() {
            return offset;
        }

        public ArrayList<String> getParameters() {
            return parameters;
        }
    }

    public String getBy() {
        return by;
    }

    public String getBase() {
        return base;
    }

    public ArrayList<ConfigNotify> getNotify() {
        return notify;
    }

    public ConfigNotify getNotify(String type) {
        if (notify == null) return null;
        for (int i = 0; i < notify.size(); i++)
            if (notify.get(i).getType().equals(type))
                return notify.get(i);
        return null;
    }
}
