package sample.connection;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import sample.client.DataClient;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class NetworkServiceFileUpload extends Service<Response> {

    private final File file;
    private final boolean isFile;
    private final Request request;

    public NetworkServiceFileUpload(File file, boolean isFile, Request request)
    {
        super();
        this.file = file;
        this.isFile = isFile;
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
                     DataInputStream ois = new DataInputStream(socket.getInputStream());
                     BufferedInputStream oif = new BufferedInputStream(new FileInputStream(file)))
                {
                    if (!socket.isOutputShutdown()) {
                        if (isFile)
                        {
                            GetData.outMessage(oos,request);
                            response = GetData.inMessage(ois, request.getCode());
                            if (response.getCode() == 200)
                            {
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                BufferedOutputStream bos = new BufferedOutputStream(oos);
                                long fileSize = file.length();
                                byte[] sizeas = GetData.longToBytes(fileSize);
                                bos.write(sizeas);
                                byte[] buffer = new byte[8192];
                                int i;
                                int size = 0;
                                updateProgress(size,fileSize);
                                while ( (i = oif.read(buffer)) != -1)
                                {
                                    bos.write(buffer,0,i);
                                    size += i;
                                    updateProgress(size,fileSize);
                                }
                                bos.flush();
                                updateProgress(fileSize,fileSize);
                                bos.close();
                                ois.close();
                                oos.close();
                                socket.close();
                            }
                        }
                        else
                        {

                        }
                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return response;
            }
        };

        return task;
    }
}
