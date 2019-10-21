package org.fogbowcloud.arrebol.api.constants;

public class ApiDocumentation {
	public static class ApiEndpoints {
		public static final String QUEUE_ENDPOINT = "queue";
		public static final String JOB_SUBMISSION_PATH = "/{queue}/jobs";
		public static final String JOB_ENDPOINT = "job";
		public static final String JOB_PATH = "/{id}";
		public static final String VERSION_ENDPOINT = "version";
	}

	public static class ApiInfo {
		public static final String VERSION = "0.0.1";
	}

	public static class Job {
		public static final String API = "Manages jobs.";
	}

}
