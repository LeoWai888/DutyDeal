package com.gd.icbc.dutydeal.json;

public class systemParam {
    /**
     * unDutyBegin : 08:30:00
     * unDutyEnd : 20:00:00
     * intervalMinute : 15
     * alterMinute : 5
     */

    private String unDutyBegin;
    private String unDutyEnd;
    private String intervalMinute;
    private String alterMinute;


    public String getUnDutyBegin() {
        return unDutyBegin;
    }

    public void setUnDutyBegin(String unDutyBegin) {
        this.unDutyBegin = unDutyBegin;
    }

    public String getUnDutyEnd() {
        return unDutyEnd;
    }

    public void setUnDutyEnd(String unDutyEnd) {
        this.unDutyEnd = unDutyEnd;
    }

    public String getIntervalMinute() {
        return intervalMinute;
    }

    public void setIntervalMinute(String intervalMinute) {
        this.intervalMinute = intervalMinute;
    }

    public String getAlterMinute() {
        return alterMinute;
    }

    public void setAlterMinute(String alterMinute) {
        this.alterMinute = alterMinute;
    }
}
