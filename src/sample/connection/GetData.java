package sample.connection;

import sample.DataClient;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class GetData {
    public static NetworkData getDataMessage(String request) throws ConnectException {
        String result = "";
        NetworkData networkData = new NetworkData();
        String loginpassword = DataClient.login + "/" + DataClient.password + "://";
            try (Socket socket = new Socket(DataClient.SERVER, DataClient.PORT_MESSAGE);
                 DataOutputStream oos = new DataOutputStream(socket.getOutputStream());
                 DataInputStream ois = new DataInputStream(socket.getInputStream());)
            {
                if (!socket.isOutputShutdown()) {
                    oos.writeUTF(loginpassword + request);
                    oos.flush();
                    //result = ois.readUTF();
                    int bytesLength = ois.readInt();
                    byte[] bytes = ois.readAllBytes();
                    result = new String(bytes, StandardCharsets.UTF_8);
                    Pattern pattern = Pattern.compile("://");

                    System.out.println("in: " + result);
                    String[] strings = pattern.split(result);
                    //Если то, что пришло, неверно, то ставим код 599
                    if (strings.length != 4) networkData.setCode(599);
                    //Если все верно, то код и текст записываем в нетворкдату
                    networkData.setCode(Integer.parseInt(strings[3]));
                    networkData.setText(strings[2]);
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
                throw new ConnectException("Lost connection");
            }
            return networkData;
    }

    public static int outDataFile(File file, boolean isFile, int port) throws InterruptedException {
        Thread.sleep(300);

        int result = -1;
        try (Socket socket = new Socket(DataClient.SERVER, port);
             BufferedOutputStream oos = new BufferedOutputStream(socket.getOutputStream());
             DataInputStream ois = new DataInputStream(socket.getInputStream());
             BufferedInputStream oif = new BufferedInputStream(new FileInputStream(file)))
        {
            if (!socket.isOutputShutdown()) {
                if (isFile)
                {
                    oos.write(longToBytes(file.length()));
                    byte[] buffer = new byte[8192];
                    int i = 0;
                    while ( (i = oif.read(buffer)) != -1)
                    {
                        oos.write(buffer,0,i);
                    }
                    oos.flush();

                }
                else
                {

                }
                result = ois.readInt();
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return result;
    }

    public static String getDataFile(String request) throws InterruptedException {
        String result = "";
        try (Socket socket = new Socket(DataClient.SERVER, DataClient.PORT_FILE);
             DataOutputStream oos = new DataOutputStream(socket.getOutputStream());
             DataInputStream ois = new DataInputStream(socket.getInputStream());)
        {
            if (!socket.isOutputShutdown()) {
                oos.writeUTF(request);
                oos.flush();
                result = ois.readUTF();
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return result;
    }


    private static byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }

}
