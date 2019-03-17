import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class DBServer {
    private String host = "";
    private int port;
    private Socket socket;
    private DataOutputStream dataOutStream;

    public DBServer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public JSONObject sendData(JSONObject obj) throws IOException {
        socket = new Socket(host,port);
        System.out.println("Connected to DB");
        dataOutStream = new DataOutputStream(socket.getOutputStream());
        dataOutStream.writeBytes(obj.toString());
        dataOutStream.flush();


        System.out.println("data sent");

        BufferedReader response;
        response = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        JSONObject result = new JSONObject(response.readLine());

        dataOutStream.close();
        socket.close();
        return result;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

}
