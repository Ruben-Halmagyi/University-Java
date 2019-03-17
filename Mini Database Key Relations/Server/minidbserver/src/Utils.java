import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Date;

public class Utils {
    static boolean databaseExists(String dbName){
        JSONObject dbs = Server.databases;

        if(dbs.isEmpty()){
            return true;
        }

        JSONArray content = (JSONArray) dbs.getJSONArray("databases");

        for(Object i : content){
            if(i instanceof JSONObject){
                JSONObject db = (JSONObject) i;
                if(dbName.equals(db.getString("dbName"))){
                    return true;
                }
            }
        }
        return false;
    }

    static void saveDBFile() throws FileNotFoundException {
        Date now = new Date();
        Server.databases.put("timestamp",now.getTime());

        PrintWriter printWriter = new PrintWriter(Server.DBFile);
        printWriter.write("");
        printWriter.write(Server.databases.toString());
        printWriter.close();
    }
}
