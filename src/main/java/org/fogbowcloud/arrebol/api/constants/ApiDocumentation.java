package org.fogbowcloud.arrebol.api.constants;

public class ApiDocumentation {
	public static class ApiEndpoints {
		public static final String VERSION_ENDPOINT = "version";
		public static final String QUEUES = "queues";

		private static final String JOB_ENDPOINT = "jobs";
		private static final String WORKERS_ENDPOINT = "workers";

		public static final String QUEUE = "/{queueId}";
		public static final String JOB_PATH = QUEUE + "/" + JOB_ENDPOINT;
		public static final String JOB_BY_ID = JOB_PATH + "/{jobId}";
		public static final String ADD_WORKERS = QUEUE + "/" + WORKERS_ENDPOINT;
	}

	public static class ApiInfo {
		public static final String VERSION = "0.0.1";
	}

	public static class Job {
		public static final String API = "Manages jobs.";
	}

}
