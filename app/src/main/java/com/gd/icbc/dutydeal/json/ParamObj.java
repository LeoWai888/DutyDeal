package com.gd.icbc.dutydeal.json;

public class ParamObj {

    /**
     * ON_TIME : 20:00:00
     * OFF_TIME : 08:30:00
     * FREE_TIME : 30
     * RING_TIME : 5
     */

    private String ON_TIME;
    private String OFF_TIME;
    private String FREE_TIME;
    private String RING_TIME;
    private String systimestamp;

    public String getSystimestamp() {return systimestamp; }

    public void setSystimestamp(String systimestamp) {this.systimestamp = systimestamp; }

    public String getON_TIME() {
        return ON_TIME;
    }

    public void setON_TIME(String ON_TIME) {
        this.ON_TIME = ON_TIME;
    }

    public String getOFF_TIME() {
        return OFF_TIME;
    }

    public void setOFF_TIME(String OFF_TIME) {
        this.OFF_TIME = OFF_TIME;
    }

    public String getFREE_TIME() {
        return FREE_TIME;
    }

    public void setFREE_TIME(String FREE_TIME) {
        this.FREE_TIME = FREE_TIME;
    }

    public String getRING_TIME() {
        return RING_TIME;
    }

    public void setRING_TIME(String RING_TIME) {
        this.RING_TIME = RING_TIME;
    }
}
