package org.fogbowcloud.arrebol.models.configuration;

public class Property<T> {

    private String key;
    private T value;

    public Property(String key, T value){
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public T getValue() {
        return value;
    }
}
