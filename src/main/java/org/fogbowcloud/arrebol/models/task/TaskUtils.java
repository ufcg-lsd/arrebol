package org.fogbowcloud.arrebol.models.task;

public class TaskUtils {

    /**
    public static final String JSON_KEY_ID = "id";
    public static final String JSON_KEY_UUID = "uuid";
    public static final String JSON_KEY_RETRIES = "retries";
    public static final String JSON_KEY_SPEC = "spec";
    public static final String JSON_KEY_IS_FINISHED = "isFinished";
    public static final String JSON_KEY_IS_FAILED = "isFailed";
    public static final String JSON_KEY_COMMANDS = "commands";
    public static final String JSON_KEY_METADATA = "metadata";

    /*
    public static Task fromJSON(JSONObject taskJSON) {
        String taskId = taskJSON.optString(JSON_KEY_ID);
        String taskUuid = taskJSON.optString(JSON_KEY_UUID);
        int taskRetries = taskJSON.optInt(JSON_KEY_RETRIES);

        JSONObject specJSON = taskJSON.optJSONObject(JSON_KEY_SPEC);
        Specification specification = SpecificationUtils.fromJSON(specJSON);

        Task task = new Task(taskId, specification, taskUuid);
        task.setRetries(taskRetries);

        if (taskJSON.optBoolean(JSON_KEY_IS_FINISHED)) {
            task.finish();
        }
        if (taskJSON.optBoolean(JSON_KEY_IS_FAILED)) {
            task.fail();
        }

        JSONArray commands = taskJSON.optJSONArray(JSON_KEY_COMMANDS);
        for (int i = 0; i < commands.length(); i++) {
            task.addCommand(CommandsUtils.fromJSON(commands.optJSONObject(i)));
        }

        JSONObject metadata = taskJSON.optJSONObject(JSON_KEY_METADATA);
        Iterator<?> metadataKeys = metadata.keys();
        while (metadataKeys.hasNext()) {
            String key = (String) metadataKeys.next();
            task.putMetadata(key, metadata.optString(key));
        }
        return task;
    }

    public JSONObject toJSON(Task task) {
        try {
            JSONObject taskJSON = new JSONObject();
            taskJSON.put(JSON_KEY_IS_FINISHED, task.isFinished());
            taskJSON.put(JSON_KEY_IS_FAILED, task.isFailed());
            taskJSON.put(JSON_KEY_ID, task.getId());
            taskJSON.put(JSON_KEY_SPEC, task.getSpec().toJSON());
            taskJSON.put(JSON_KEY_RETRIES, task.getRetries());
            taskJSON.put(JSON_KEY_UUID, task.getUUID());
            JSONArray commands = new JSONArray();
            for (Command command : task.getAllCommands()) {
                commands.put(command.toJSON());
            }
            taskJSON.put(JSON_KEY_COMMANDS, commands);
            JSONObject metadata = new JSONObject();
            for (Map.Entry<String, String> entry : task.getAllMetadata().entrySet()) {
                metadata.put(entry.getKey(), entry.getValue());
            }
            taskJSON.put(JSON_KEY_METADATA, metadata);
            return taskJSON;
        } catch (JSONException e) {
            return null;
        }
    }
<<<<<<< HEAD
    */
}
