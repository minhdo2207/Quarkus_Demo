package utils;

public enum PersonAttribute {
    NAME("name"),
    AGE("age"),
    EMAIL("email"),
    ID("id");

    private final String value;

    PersonAttribute(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
