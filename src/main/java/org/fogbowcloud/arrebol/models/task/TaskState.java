package org.fogbowcloud.arrebol.models.task;

public enum TaskState {

    PENDING(1), RUNNING(2), FINISHED(4), FAILED(8);

    private int id;

    TaskState(int id) {
        this.id = id;
    }

    public int getId(){
        return this.id;
    }
}
