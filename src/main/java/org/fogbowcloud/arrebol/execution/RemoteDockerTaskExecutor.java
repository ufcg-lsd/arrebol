package org.fogbowcloud.arrebol.execution;

import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.execution.remoteWorker.ExecInstanceResult;
import org.fogbowcloud.arrebol.execution.remoteWorker.WorkerDockerRequestHelper;
import org.fogbowcloud.arrebol.models.command.Command;


import static java.lang.Thread.sleep;

public class RemoteDockerTaskExecutor extends DockerTaskExecutorAbstract {

    private WorkerDockerRequestHelper workerDockerRequestHelper;
    private final Logger LOGGER = Logger.getLogger(RemoteDockerTaskExecutor.class);

    public RemoteDockerTaskExecutor(String imageId, String containerName, String address) {
        super(imageId, containerName);
        this.workerDockerRequestHelper = new WorkerDockerRequestHelper(address, containerName, imageId);
    }

    protected Integer start(){
        try {
            LOGGER.info("Starting RemoteDockerTaskExecutor " + super.getContainerName());
            this.workerDockerRequestHelper.start();
            return new Integer(0);
        } catch (Exception e) {
            return new Integer(127);
        }
    }

    protected Integer stop(){
        try {
            this.workerDockerRequestHelper.stop();
            return new Integer(0);
        } catch (Exception e) {
            e.printStackTrace();
            return new Integer(127);
        }
    }

    protected Integer executeCommand(Command command){
        try {
            String execId = this.workerDockerRequestHelper.createExecInstance(command.getCommand());
            this.workerDockerRequestHelper.startExecInstance(execId);
            ExecInstanceResult execInstanceResult = this.workerDockerRequestHelper.inspectExecInstance(execId);
            while(execInstanceResult.getExitCode() == null){
                execInstanceResult = this.workerDockerRequestHelper.inspectExecInstance(execId);
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return execInstanceResult.getExitCode();
        } catch(Exception e){
            e.printStackTrace();
            return new Integer(127);
        }
    }


}
