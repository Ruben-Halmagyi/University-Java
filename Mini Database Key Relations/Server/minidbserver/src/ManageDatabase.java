import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;

public class ManageDatabase {
    static JSONObject getDBData() {
        JSONObject result = new JSONObject();

        result.put("status", Constants.OK);
        result.put("data", Server.databases.get("databases"));

        return result;
    }

    static JSONObject createDatabase(String dbName) {
        JSONObject result = new JSONObject();

        if (Utils.databaseExists(dbName)) {
            result.append("status", Constants.DB_NAME_TAKEN);
            System.out.println("Database Create: DB Name already exists!");
        } else {

            JSONObject newDB = new JSONObject();
            newDB.put("dbName", dbName);

            Server.databases.getJSONArray("databases").put(newDB);

            result.append("status", Constants.OK);
            System.out.println("Database Create: DB created with name " + dbName);
            try {
                Utils.saveDBFile();
                System.out.println("Database Created: Successfully saved DatabaseFile");
            } catch (FileNotFoundException e) {
                System.out.println("Database Create: Crashed while trying to save the Databases file");
                e.printStackTrace();
            }
        }
        return result;
    }

    static JSONObject dropDatabase(String dbName) {
        JSONObject result = new JSONObject();

        JSONArray DBs = Server.databases.getJSONArray("databases");
        for(int i = 0; i < Server.databases.getJSONArray("databases").length(); i++){
            JSONObject db = Server.databases.getJSONArray("databases").getJSONObject(i);
            if(db.getString("dbName").equals(dbName)){
                Server.databases.getJSONArray("databases").remove(i);
                result.put("status",Constants.OK);
                try {
                    Utils.saveDBFile();
                    System.out.println("Database Drop: Successfully saved DatabaseFile");
                } catch (FileNotFoundException e) {
                    System.out.println("Database Drop: Crashed while trying to save the Databases file");
                    e.printStackTrace();
                }
                return result;
            }
        }
        result.put("status",Constants.REQUEST_ERROR);
        return result;
    }
}
