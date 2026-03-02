package com.lokomanako.hack_api.store.ent;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Kind {

    INC,
    EXP;

    @JsonValue
    public String v() {
        return name().toLowerCase();
    }

    @JsonCreator
    public static Kind p(String v) {
        if (v == null) {
            return null;
        }
        return Kind.valueOf(v.trim().toUpperCase());
    }
}
