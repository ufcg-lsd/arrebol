package org.fogbowcloud.arrebol.models.specification;

import org.fogbowcloud.arrebol.utils.AppUtil;
import org.json.JSONException;
import org.json.JSONObject;
import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
public class Specification implements Serializable {

    private static final long serialVersionUID = 3435814833254660531L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String image;

    @ElementCollection
    private Map<String, String> requirements;

    public Specification(String image, Map<String, String> requirements) {
        this.image = image;
        this.requirements = requirements;
    }

    public Specification(Map<String, String> requirements) {
        this.requirements = requirements;
    }

    Specification() {
        //Default constructor
    }

    public String getRequirementValue(String key) {
        return requirements.get(key);
    }

    public Map<String, String> getRequirements() {
        return requirements;
    }

    public String getImage() {
        return this.image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Specification that = (Specification) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(requirements, that.requirements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, requirements);
    }

    @Override
    public String toString() {
        return "Specification{" +
            "id='" + id + '\'' +
            ", requirements=" + requirements +
            '}';
    }

    public JSONObject toJSON() {
        try {
            JSONObject specification = new JSONObject();
            AppUtil.makeBodyField(specification, SpecificationConstants.REQUIREMENTS_MAP_STR,
                new JSONObject(getRequirements()));
            return specification;
        } catch (JSONException e) {
            return null;
        }
    }

}
