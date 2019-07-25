package org.fogbowcloud.arrebol.utils;

import java.util.Objects;
import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.execution.WorkerTypes;
import org.fogbowcloud.arrebol.models.configuration.Configuration;

public class ConfValidator {

    private static final Logger LOGGER = Logger.getLogger(ConfValidator.class);
    private static final int FAIL_EXIT_CODE = 1;

    public static void validate(Configuration configuration){
        if(Objects.isNull(configuration)){
            throw new IllegalArgumentException("Configuration object cannot be null.");
        }
        validatePoolType(configuration);
        LOGGER.debug("Pool Type is set.");
    }

    private static void validatePoolType(Configuration configuration){
        String poolType = configuration.getPoolType();
        boolean valid = !Objects.isNull(poolType) && !poolType.trim().isEmpty() && isSomeWorkerType(poolType);
        if(!valid){
            String message = "Property poolType is empty or invalid.";
            LOGGER.error(message);
            System.exit(FAIL_EXIT_CODE);
        }
    }

    private static boolean isSomeWorkerType(String poolType){
        for(WorkerTypes w : WorkerTypes.values()){
            if(w.getType().equals(poolType)){
                return true;
            }
        }
        return false;
    }
}
