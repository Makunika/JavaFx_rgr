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
    public static void outMessage(DataOutputStream oos, String request) throws IOException {
        String loginpassword = DataClient.login + "/" + DataClient.password + "://";
        oos.writeUTF(loginpassword + request);
        oos.flush();
    }

    public static NetworkData inMessage(DataInputStream ois) throws IOException {
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

    public static void outFile(BufferedOutputStream bos, File file) throws IOException {
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


    public static byte[] longToBytes(long x) {
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte)(x & 0xFF);
            x >>= 8;
        }
        return result;
    }

}
