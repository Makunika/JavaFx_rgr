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
        NetworkData networkData = new NetworkData();
            try (Socket socket = new Socket(DataClient.SERVER, DataClient.PORT_MESSAGE);
                 DataOutputStream oos = new DataOutputStream(socket.getOutputStream());
                 DataInputStream ois = new DataInputStream(socket.getInputStream());)
            {
                if (!socket.isOutputShutdown()) {
                    outMessage(oos,request);
                    networkData = inMessage(ois);
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
                throw new ConnectException("Lost connection");
            }
            return networkData;
    }

    public static NetworkData outDataFile(File file, boolean isFile, String request) throws ConnectException {
        NetworkData networkData = new NetworkData();
        try (Socket socket = new Socket(DataClient.SERVER, DataClient.PORT_MESSAGE);
             DataOutputStream oos = new DataOutputStream(socket.getOutputStream());
             DataInputStream ois = new DataInputStream(socket.getInputStream());
             BufferedInputStream oif = new BufferedInputStream(new FileInputStream(file)))
        {
            if (!socket.isOutputShutdown()) {
                if (isFile)
                {
                    outMessage(oos,request);
                    networkData = inMessage(ois);
                    if (networkData.getCode() == 200)
                    {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        outFile(new BufferedOutputStream(oos),file);
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
            throw new ConnectException("Lost connection");
        }
        return networkData;
    }


    private static void outMessage(DataOutputStream oos, String request) throws IOException {
        String loginpassword = DataClient.login + "/" + DataClient.password + "://";
        oos.writeUTF(loginpassword + request);
        oos.flush();
    }

    private static NetworkData inMessage(DataInputStream ois) throws IOException {
        NetworkData networkData = new NetworkData();
        int bytesLength = ois.readInt();
        byte[] bytes = new byte[bytesLength];
        ois.readNBytes(bytes,0,bytesLength);
        String result = new String(bytes, StandardCharsets.UTF_8);
        Pattern pattern = Pattern.compile("://");
        System.out.println("in: " + result);
        String[] strings = pattern.split(result);
        //Если то, что пришло, неверно, то ставим код 599
        if (strings.length != 4) networkData.setCode(599);
        else {
            //Если все верно, то код и текст записываем в нетворкдату
            networkData.setCode(Integer.parseInt(strings[3]));
            networkData.setText(strings[2]);
        }
        return networkData;
    }

    private static void outFile(BufferedOutputStream bos, File file) throws IOException {
        BufferedInputStream oif = new BufferedInputStream(new FileInputStream(file));
        bos.write(longToBytes(file.length()));
        byte[] buffer = new byte[8192];
        int i = 0;
        while ( (i = oif.read(buffer)) != -1)
        {
            bos.write(buffer,0,i);
        }
        bos.flush();
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
