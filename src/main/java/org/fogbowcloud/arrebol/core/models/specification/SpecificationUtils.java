package org.fogbowcloud.arrebol.core.models.specification;

import com.google.gson.Gson;
import org.json.JSONObject;

import java.io.*;
import java.util.*;


public class SpecificationUtils {
    /**

    public static boolean parseSpecsToJsonFile(List<Specification> specs, String jsonDestFilePath) {

        if (jsonDestFilePath != null && !jsonDestFilePath.isEmpty()) {

            BufferedWriter bw;
            try {
                bw = new BufferedWriter(new FileWriter(jsonDestFilePath));
                Gson gson = new Gson();
                String spectString = gson.toJson(specs);
                bw.write(spectString);
                bw.close();
                return true;
            } catch (IOException e) {
                return false;
            }
        } else {
            return false;
        }
    }

    public static List<Specification> getSpecificationsFromJSonFile(String jsonFilePath) throws IOException {

        List<Specification> specifications = new ArrayList<Specification>();
        if (jsonFilePath != null && !jsonFilePath.isEmpty()) {

            BufferedReader br = new BufferedReader(new FileReader(jsonFilePath));

            Gson gson = new Gson();
            specifications = Arrays.asList(gson.fromJson(br, Specification[].class));
            br.close();

            for (Specification spec : specifications) {

                File file = new File(spec.getPublicKey());
                if (file.exists()) {
                    StringBuilder sb = new StringBuilder();
                    BufferedReader brSpec = new BufferedReader(new FileReader(file));
                    String line = "";
                    while ((line = brSpec.readLine()) != null && !line.isEmpty()) {
                        sb.append(line);
                    }
                    spec.setPublicKey(sb.toString());

                    brSpec.close();
                }
            }

        }
        return specifications;
    }

    public static Specification fromJSON(JSONObject specJSON) {
        Specification specification = new Specification(
                specJSON.optString(SpecificationConstants.IMAGE_STR),
                specJSON.optString(SpecificationConstants.USERNAME_STR),
                specJSON.optString(SpecificationConstants.PUBLIC_KEY_STR),
                specJSON.optString(SpecificationConstants.PRIVATE_KEY_FILE_PATH_STR),
                specJSON.optString(SpecificationConstants.USER_DATA_FILE_STR),
                specJSON.optString(SpecificationConstants.USER_DATA_TYPE_STR));
        HashMap<String, String> reqMap =
                (HashMap<String, String>) toMap(specJSON.optString(SpecificationConstants.REQUIREMENTS_MAP_STR));
        specification.putAllRequirements(reqMap);
        return specification;
    }

    public static Map<String, String> toMap(String jsonStr) {
        Map<String, String> newMap = new HashMap<String, String>();
        jsonStr = jsonStr.replace("{", "").replace("}", "");
        String[] blocks = jsonStr.split(",");
        for (int i = 0; i < blocks.length; i++) {
            String block = blocks[i];
            int indexOfCarac = block.indexOf("=");
            if (indexOfCarac < 0) {
                continue;
            }
            String key = block.substring(0, indexOfCarac).trim();
            String value = block.substring(indexOfCarac + 1, block.length()).trim();
            newMap.put(key, value);
        }
        return newMap;
    }
*/
}
