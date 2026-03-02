package com.lokomanako.hack_api.store.ent;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Freq {

    DAY,
    WEEK,
    MONTH,
    YEAR;

    @JsonValue
    public String v() {
        return name().toLowerCase();
    }

    @JsonCreator
    public static Freq p(String v) {
        if (v == null) {
            return null;
        }
        return Freq.valueOf(v.trim().toUpperCase());
    }
}
