package com.example.demo.exception.error;

public class UniqueFieldError extends RuntimeException {
    private String field;
    private Object value;

    public UniqueFieldError(String field, Object value) {
        super(String.format("'%s' утгатай '%s' талбар аль хэдийн байна\n", field, value));
        this.field = field;
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public Object getValue() {
        return value;
    }
}