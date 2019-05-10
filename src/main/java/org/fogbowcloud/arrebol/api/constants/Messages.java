package org.fogbowcloud.arrebol.api.constants;

public class Messages {

    public static class Exception {
        public static final String GENERIC_EXCEPTION = "Operation returned error: %s";
        public static final String JOB_NOT_FOUND = "Job id not found : %s";
        public static final String INVALID_JOB_SPEC = "JobSpec is invalid: %s";
    }

    public static class Info {
        public static class JobController {
            public static final String ADDING_NEW_JOB = "Adding new Job: %s.";
            public static final String ADDED_JOB = "Added %s with id %s.";
            public static final String GETTING_JOB = "Getting an job with id: %s";
        }

    }
}
