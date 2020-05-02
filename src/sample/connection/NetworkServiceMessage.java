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

public class NetworkServiceMessage extends Service<Response> {

    private final Request request;


    public NetworkServiceMessage(Request request)
    {
        super();
        this.request = request;
    }


    @Override
    protected Task<Response> createTask() {

        Task<Response> task = new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                Response response = new Response(request.getCode());
                try (Socket socket = new Socket(DataClient.SERVER, DataClient.PORT_MESSAGE);
                     DataOutputStream oos = new DataOutputStream(socket.getOutputStream());
                     DataInputStream ois = new DataInputStream(socket.getInputStream());)
                {
                    if (!socket.isOutputShutdown()) {
                        GetData.outMessage(oos,request);
                        response = GetData.inMessage(ois, request.getCode());
                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {

                    e.printStackTrace();
                    throw new ConnectException("Lost connection");
                }
                return response;
            }
        };



        return task;
    }
}
