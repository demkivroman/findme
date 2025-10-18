package org.demkiv.domain;

public enum Language {
    UKRAINIAN("uk"),
    ENGLISH("en"),
    RUSSIAN("ru");

    private final String code;

    Language(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static String getLanguageCodeByName(String name) {
        for (Language language : values()) {
            if (language.name().equalsIgnoreCase(name)) {
                return language.getCode();
            }
        }
        throw new IllegalArgumentException("Unknown language: " + name);
    }
}
