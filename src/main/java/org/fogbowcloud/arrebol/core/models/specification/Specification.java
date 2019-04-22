package org.fogbowcloud.arrebol.core.models.specification;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.fogbowcloud.arrebol.core.utils.AppUtil;
import org.json.JSONException;
import org.json.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
public class Specification implements Serializable {

    private static final long serialVersionUID = 3435814833254660531L;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    String id;
    String cloudName;
    String image;
    String username;
    String privateKeyFilePath;

    @Column(columnDefinition="varchar(512)")
    String publicKey;
    String contextScript;

    @ElementCollection
    Map<String, String> requirements;

    public Specification(String image, String username, String publicKey, String privateKeyFilePath, Map<String, String> requirements) {
        this(image, username, publicKey, privateKeyFilePath, requirements, null);
    }

    public Specification(String image, String username, String publicKey, String privateKeyFilePath, Map<String, String> requirements, String cloudName) {
        this.image = image;
        this.username = username;
        this.publicKey = publicKey;
        this.privateKeyFilePath = privateKeyFilePath;
        this.requirements = requirements;
        this.cloudName = cloudName;
    }

    Specification(){
        //Default constructor
    }

    public String getRequirementValue(String key) {
        return requirements.get(key);
    }

    public Map<String, String> getRequirements() {
        return requirements;
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

    public String getContextScript() {
        return contextScript;
    }

    public String getCloudName(){
        return this.cloudName;
    }

    public void setContextScript(String contextScript) {
        this.contextScript = contextScript;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Specification that = (Specification) o;
        return Objects.equals(getCloudName(), that.getCloudName()) &&
                getImage().equals(that.getImage()) &&
                getPrivateKeyFilePath().equals(that.getPrivateKeyFilePath()) &&
                getPublicKey().equals(that.getPublicKey()) &&
                getRequirements().equals(that.getRequirements());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCloudName(), getImage(), getPrivateKeyFilePath(), getPublicKey(), getRequirements());
    }

    @Override
    public String toString() {
        return "Specification{" +
                "id='" + id + '\'' +
                ", cloudName='" + cloudName + '\'' +
                ", image='" + image + '\'' +
                ", username='" + username + '\'' +
                ", privateKeyFilePath='" + privateKeyFilePath + '\'' +
                ", publicKey='" + publicKey + '\'' +
                ", contextScript='" + contextScript + '\'' +
                ", requirements=" + requirements +
                '}';
    }

    public JSONObject toJSON() {
        try {
            JSONObject specification = new JSONObject();
            AppUtil.makeBodyField(specification, SpecificationConstants.CLOUD_NAME_STR, this.getCloudName());
            AppUtil.makeBodyField(specification, SpecificationConstants.IMAGE_STR, this.getImage());
            AppUtil.makeBodyField(specification, SpecificationConstants.USERNAME_STR, this.getUsername());
            AppUtil.makeBodyField(specification, SpecificationConstants.PUBLIC_KEY_STR, this.getPublicKey());
            AppUtil.makeBodyField(specification, SpecificationConstants.PRIVATE_KEY_FILE_PATH_STR, this.getPrivateKeyFilePath());
            AppUtil.makeBodyField(specification, SpecificationConstants.CONTEXT_SCRIPT_STR, this.getContextScript());
            AppUtil.makeBodyField(specification, SpecificationConstants.REQUIREMENTS_MAP_STR, new JSONObject(getRequirements()));
            return specification;
        } catch (JSONException e) {
            return null;
        }
    }

}
