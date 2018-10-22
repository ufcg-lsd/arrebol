package org.fogbowcloud.arrebol.core.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.*;

public class Specification {
    String image;
    String username;
    String privateKeyFilePath;
    String publicKey;
    String contextScript;
    String userDataFile;
    String userDataType;

    Map<String, String> requirements = new HashMap<String, String>();

    public Specification(String image, String username, String publicKey, String privateKeyFilePath) {
        this(image, username, publicKey, privateKeyFilePath, "", "");
    }

    public Specification(String image, String username, String publicKey, String privateKeyFilePath,
                         String userDataFile, String userDataType) {
        this.image = image;
        this.username = username;
        this.publicKey = publicKey;
        this.privateKeyFilePath = privateKeyFilePath;
        this.userDataFile = userDataFile;
        this.userDataType = userDataType;
    }

    public void addRequirement(String key, String value) {
        requirements.put(key, value);
    }

    public String getRequirementValue(String key) {
        return requirements.get(key);
    }

    public void putAllRequirements(Map<String, String> requirements) {
        for (Map.Entry<String, String> e : requirements.entrySet()) {

            this.requirements.put(e.getKey(), e.getValue());
        }
    }

    public Map<String, String> getAllRequirements() {
        return requirements;
    }

    public void removeAllRequirements() {
        requirements = new HashMap<String, String>();
    }

    public boolean parseToJsonFile(String jsonDestFilePath) {

        List<Specification> spec = new ArrayList<Specification>();
        spec.add(this);
        return SpecificationUtils.parseSpecsToJsonFile(spec, jsonDestFilePath);
    }

    public String getImage() {
        return image;
    }

    public String getUsername() {
        return username;
    }

    public String getPrivateKeyFilePath() {
        return privateKeyFilePath;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getContextScript() {
        return contextScript;
    }

    public void setContextScript(String contextScript) {
        this.contextScript = contextScript;
    }

    public String getUserDataFile() {
        return userDataFile;
    }

    public void setUserDataFile(String userDataFile) {
        this.userDataFile = userDataFile;
    }

    public String getUserDataType() {
        return userDataType;
    }

    public void setUserDataType(String userDataType) {
        this.userDataType = userDataType;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Image: " + image);
        sb.append(" PublicKey: " + publicKey);
        if (contextScript != null && !contextScript.isEmpty()) {
            sb.append("\nContextScript: " + contextScript);
        }
        if (userDataFile != null && !userDataFile.isEmpty()) {
            sb.append("\nUserDataFile:" + userDataFile);
        }
        if (userDataType != null && !userDataType.isEmpty()) {
            sb.append("\nUserDataType:" + userDataType);
        }
        if (requirements != null && !requirements.isEmpty()) {
            sb.append("\nRequirements:{");
            for (Map.Entry<String, String> entry : requirements.entrySet()) {
                sb.append("\n\t" + entry.getKey() + ": " + entry.getValue());
            }
            sb.append("\n}");
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((contextScript == null) ? 0 : contextScript.hashCode());
        result = prime * result + ((image == null) ? 0 : image.hashCode());
        result = prime * result + ((privateKeyFilePath == null) ? 0 : privateKeyFilePath.hashCode());
        result = prime * result + ((publicKey == null) ? 0 : publicKey.hashCode());
        result = prime * result + ((userDataFile == null) ? 0 : userDataFile.hashCode());
        result = prime * result + ((userDataType == null) ? 0 : userDataType.hashCode());
        result = prime * result + ((requirements == null) ? 0 : requirements.hashCode());
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Specification other = (Specification) obj;
        if (contextScript == null) {
            if (other.contextScript != null)
                return false;
        } else if (!contextScript.equals(other.contextScript))
            return false;
        if (image == null) {
            if (other.image != null)
                return false;
        } else if (!image.equals(other.image))
            return false;
        if (privateKeyFilePath == null) {
            if (other.privateKeyFilePath != null)
                return false;
        } else if (!privateKeyFilePath.equals(other.privateKeyFilePath))
            return false;
        if (publicKey == null) {
            if (other.publicKey != null)
                return false;
        } else if (!publicKey.equals(other.publicKey))
            return false;
        if (userDataFile == null) {
            if (other.userDataFile != null)
                return false;
        } else if (!userDataFile.equals(other.userDataFile))
            return false;
        if (userDataType == null) {
            if (other.userDataType != null)
                return false;
        } else if (!userDataType.equals(other.userDataType))
            return false;
        if (requirements == null) {
            if (other.requirements != null)
                return false;
        } else if (!requirements.equals(other.requirements))
            return false;
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        return true;
    }

    public Specification clone() {
        Specification cloneSpec = new Specification(this.image, this.username, this.publicKey, this.privateKeyFilePath,
                this.userDataFile, this.userDataType);
        cloneSpec.putAllRequirements(this.getAllRequirements());
        return cloneSpec;
    }

    public JSONObject toJSON() {
        try {
            JSONObject specification = new JSONObject();
            specification.put(SpecificationConstants.IMAGE_STR, this.getImage());
            specification.put(SpecificationConstants.USERNAME_STR, this.getUsername());
            specification.put(SpecificationConstants.PUBLIC_KEY_STR, this.getPublicKey());
            specification.put(SpecificationConstants.PRIVATE_KEY_FILE_PATH_STR, this.getPrivateKeyFilePath());
            specification.put(SpecificationConstants.CONTEXT_SCRIPT_STR, this.getContextScript());
            specification.put(SpecificationConstants.USER_DATA_FILE_STR, this.getUserDataFile());
            specification.put(SpecificationConstants.USER_DATA_TYPE_STR, this.getUserDataType());
            specification.put(SpecificationConstants.REQUIREMENTS_MAP_STR, getAllRequirements().toString());
            return specification;
        } catch (JSONException e) {
            return null;
        }
    }

}
