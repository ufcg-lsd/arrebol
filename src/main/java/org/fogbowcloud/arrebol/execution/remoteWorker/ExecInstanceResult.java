package org.fogbowcloud.arrebol.execution.remoteWorker;

public class ExecInstanceResult {

    private String execInstanceId;
    private String exitCode;
    private boolean running;

    public ExecInstanceResult(String execInstanceId, String exitCode, boolean running) {
        this.execInstanceId = execInstanceId;
        this.exitCode = exitCode;
        this.running = running;
    }

    public String getExecInstanceId() {
        return execInstanceId;
    }

    public String getExitCode() {
        return exitCode;
    }

    public boolean getRunning() {
        return running;
    }
}
