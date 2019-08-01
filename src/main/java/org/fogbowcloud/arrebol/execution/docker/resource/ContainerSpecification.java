package org.fogbowcloud.arrebol.execution.docker.resource;

import java.util.Map;

public class ContainerSpecification {
    private String imageId;
    private Map<String, String> requirements;

    public ContainerSpecification(String imageId, Map<String, String> requirements) {
        this.imageId = imageId;
        this.requirements = requirements;
    }

    public String getImageId() {
        return imageId;
    }

    public Map<String, String> getRequirements() {
        return requirements;
    }

    @Override
    public String toString() {
        return "{" + "imageId='" + imageId + '\'' + ", requirements="
            + toStringRequirements() + '}';
    }

    private String toStringRequirements() {
        StringBuilder mapAsString = new StringBuilder("{");
        for (String key : requirements.keySet()) {
            mapAsString.append(key + "=" + requirements.get(key) + ", ");
        }
        if(!requirements.isEmpty()){
            mapAsString.delete(mapAsString.length()-2, mapAsString.length());
        }
        mapAsString.append("}");
        return mapAsString.toString();
    }
}
