package sample.connection;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import sample.client.DataClient;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class NetworkServiceFileUpload extends Service<NetworkData> {

    private final File file;
    private final boolean isFile;
    private final String request;

    public NetworkServiceFileUpload(File file, boolean isFile, String request)
    {
        super();
        this.file = file;
        this.isFile = isFile;
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
                     DataInputStream ois = new DataInputStream(socket.getInputStream());
                     BufferedInputStream oif = new BufferedInputStream(new FileInputStream(file)))
                {
                    if (!socket.isOutputShutdown()) {
                        if (isFile)
                        {
                            GetData.outMessage(oos,request);
                            networkData = GetData.inMessage(ois);
                            if (networkData.getCode() == 200)
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
                                int i = 0;
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
                return networkData;
            }
        };

        return task;
    }
}
