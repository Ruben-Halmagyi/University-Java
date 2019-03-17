import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    static JSONObject databases;
    static File DBFile;
    private static void initDBJSON(){
        databases = new JSONObject();
        databases.put("timestamp",0);
        JSONArray jsonArray = new JSONArray();
        databases.put("databases",jsonArray);
        try {
            Utils.saveDBFile();
        } catch (FileNotFoundException e) {
            System.out.println("InitJSON: File not found!");
            e.printStackTrace();
        }
    }

    public static void main(String arg[]) throws IOException {
        readStoredData();
        launchServer();
    }

    private static void readStoredData() throws IOException {
        DBFile = new File("databases.json");
        BufferedReader bufferedReader = new BufferedReader(new FileReader(DBFile));
        StringBuilder stringBuilder = new StringBuilder();
        String line;

        while((line= bufferedReader.readLine())!=null){
            stringBuilder.append(line);
        }

        line = stringBuilder.toString();

        if(line.equals("{}")) {
            initDBJSON();
        }else{
            databases = new JSONObject(line);
        }

        bufferedReader.close();
    }

    private static JSONObject processData(String request){
        JSONObject data = new JSONObject(request);
        int dataType = data.getInt("type");

        JSONObject result;

        switch(dataType){
            case Constants.CREATE_DB:
                System.out.println("Request: Database Create");
                result = ManageDatabase.createDatabase(data.getString("db_name"));
                break;
            case Constants.DROP_DB:
                System.out.println("Request: Database Create");
                result = ManageDatabase.dropDatabase(data.getString("db_name"));
                break;
            case Constants.GET_DATA:
                System.out.println("Request: Get Data");
                result = ManageDatabase.getDBData();
                break;
            default:
                result = new JSONObject();
                result.put("status",Constants.INVALID_REQUEST);
                break;
        }
        return result;
    }

    private static void sendMessageToClient(Socket socket, JSONObject msg){
        DataOutputStream toClient;
        try {
            toClient = new DataOutputStream(socket.getOutputStream());
            toClient.writeBytes(msg.toString());
            toClient.flush();
            toClient.close();
        } catch (IOException e) {
            System.out.println("SendMessageToClient: IOException()");
            e.printStackTrace();
        }
    }

    private static void launchServer(){
        String clientRequest;

        try{
            ServerSocket socket = new ServerSocket(5555);

            while(true) {
                //connect to Remote
                Socket connectionSocket = socket.accept();

                BufferedReader fromClient;
                fromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

                clientRequest = fromClient.readLine();
                System.out.println("Before dataprocess");
                JSONObject result = processData(clientRequest);
                System.out.println("After data");
                sendMessageToClient(connectionSocket,result);

                if(clientRequest.equals("exit")) {
                    System.out.println("Server: Server Closed!");
                    fromClient.close();
                    connectionSocket.close();
                    break;
                }else {
                    System.out.println(clientRequest);
                }
                fromClient.close();

                connectionSocket.close();

            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }


}
