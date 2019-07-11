package org.fogbowcloud.arrebol.models.configuration;

import java.util.List;

public class Configuration {

    private final String poolType;
    private final List<Property<?>> properties;

    public Configuration(String poolType, List<Property<?>> properties) {
        this.poolType = poolType;
        this.properties = properties;
    }

    public String getPoolType() {
        return poolType;
    }

    public List<Property<?>> getProperties() {
        return properties;
    }

    public Property getProperty(String key){
        for(Property p : this.getProperties()){
            if(p.getKey().equals(key)){
                return p;
            }
        }
        return null;
    }
}
