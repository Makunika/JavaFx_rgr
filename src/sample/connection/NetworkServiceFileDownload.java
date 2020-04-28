package sample.connection;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import sample.client.DataClient;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class NetworkServiceFileDownload extends Service<NetworkData> {

    private File file;
    private final boolean isFile;
    private final String request;
    private final String nameFile;

    public NetworkServiceFileDownload(File file, boolean isFile, String request, String nameFile)
    {
        super();
        this.file = file;
        this.isFile = isFile;
        this.request = request;
        this.nameFile = nameFile;
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
                        if (isFile)
                        {
                            GetData.outMessage(oos,request);
                            networkData = GetData.inMessage(ois);
                            if (networkData.getCode() == 201)
                            {
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                file = new File(file.getAbsolutePath() + "\\" + nameFile);
                                file.createNewFile();
                                FileOutputStream fos=new FileOutputStream(file);
                                BufferedInputStream bis = new BufferedInputStream(ois);

                                byte[] buffer = new byte[8192];
                                byte[] sizeByte = new byte[8];
                                bis.read(sizeByte);
                                long size=GetData.bytesToLong(sizeByte);
                                long allSize = size;
                                System.out.println(allSize);
                                updateProgress(allSize - size,allSize);
                                while (size > 0) {
                                    int i = bis.read(buffer);
                                    fos.write(buffer, 0, i);
                                    size-= i;
                                    updateProgress(allSize - size,allSize);
                                }
                                updateProgress(allSize,allSize);
                                fos.close();
                                ois.close();
                                oos.close();
                                bis.close();
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

