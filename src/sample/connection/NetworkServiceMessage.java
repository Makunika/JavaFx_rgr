package sample.connection;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import sample.client.DataClient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

public class NetworkServiceMessage extends Service<NetworkData> {

    private String request;


    public NetworkServiceMessage(String request)
    {
        super();
        this.request = request;
    }


    @Override
    protected Task<NetworkData> createTask() {

        Task<NetworkData> task = new Task<NetworkData>() {
            @Override
            protected NetworkData call() throws Exception {
                NetworkData networkData = new NetworkData();
                try (Socket socket = new Socket(DataClient.SERVER, DataClient.PORT_MESSAGE);
                     DataOutputStream oos = new DataOutputStream(socket.getOutputStream());
                     DataInputStream ois = new DataInputStream(socket.getInputStream());)
                {
                    if (!socket.isOutputShutdown()) {
                        GetData.outMessage(oos,request);
                        networkData = GetData.inMessage(ois);
                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {

                    e.printStackTrace();
                    throw new ConnectException("Lost connection");
                }
                return networkData;
            }
        };



        return task;
    }
}
