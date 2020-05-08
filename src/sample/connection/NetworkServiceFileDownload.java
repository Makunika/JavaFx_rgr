package sample.connection;

import javafx.concurrent.Task;
import sample.client.DataClient;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class NetworkServiceFileDownload extends GetData {

    private File file;
    private final boolean isFile;
    private final Request request;
    private final String nameFile;

    public NetworkServiceFileDownload(File file, boolean isFile, Request request, String nameFile)
    {
        super();
        this.file = file;
        this.isFile = isFile;
        this.request = request;
        this.nameFile = nameFile;
    }

    public NetworkServiceFileDownload(File file, boolean isFile, Request request)
    {
        super();
        this.file = file;
        this.isFile = isFile;
        this.request = request;
        this.nameFile = "null";
    }

    @Override
    protected Task<Response> createTask() {

        Task<Response> task = new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                updateProgress(0,10);
                Response response = new Response(request.getCode());
                try (Socket socket = new Socket(DataClient.SERVER, DataClient.PORT);
                     DataOutputStream oos = new DataOutputStream(socket.getOutputStream());
                     DataInputStream ois = new DataInputStream(socket.getInputStream());)
                {
                    if (!socket.isOutputShutdown()) {
                        outMessage(oos,request);
                        response = inMessage(ois, request.getCode());
                        if (response.isValidCode()) {
                            if (!isFile) updateMessage("Архивация");
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (!nameFile.equals("null"))
                            {
                                file = new File(file.getAbsolutePath() + "\\" + nameFile);
                                file.createNewFile();
                            }
                            FileOutputStream fos=new FileOutputStream(file);
                            BufferedInputStream bis = new BufferedInputStream(ois);
                            byte[] buffer = new byte[8192];
                            byte[] sizeByte = new byte[8];
                            bis.read(sizeByte);
                            updateMessage("Загрузка");
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

