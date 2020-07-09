package org.fogbowcloud.arrebol.execution;

public enum WorkerTypes {
    DOCKER("docker"),
    RAW("raw"),
    K8S("k8s");

    private String type;

    WorkerTypes(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
