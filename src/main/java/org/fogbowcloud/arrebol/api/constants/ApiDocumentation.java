package org.fogbowcloud.arrebol.api.constants;

public class ApiDocumentation {
	public static class ApiEndpoints {
		public static final String QUEUE_ENDPOINT = "queue";
		public static final String QUEUE_PATH = "/{queueId}";
		public static final String JOB_ENDPOINT = "jobs";

		public static final String JOB_PATH = QUEUE_PATH + "/" + JOB_ENDPOINT;
		public static final String JOB_BY_ID = JOB_PATH + "/{jobId}";
		public static final String VERSION_ENDPOINT = "version";
	}

	public static class ApiInfo {
		public static final String VERSION = "0.0.1";
	}

	public static class Job {
		public static final String API = "Manages jobs.";
	}

}
