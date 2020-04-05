package sample.connection;

import sample.DataClient;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

public class GetData {
    public static int getDataMessage(String request) throws InterruptedException {
        String result = "";
        int code = 198;
        String loginpassword = DataClient.login + "/" + DataClient.password + "://";
            try (Socket socket = new Socket(DataClient.SERVER, DataClient.PORT_MESSAGE);
                 DataOutputStream oos = new DataOutputStream(socket.getOutputStream());
                 DataInputStream ois = new DataInputStream(socket.getInputStream());)
            {
                if (!socket.isOutputShutdown()) {
                    oos.writeUTF(loginpassword + request);
                    oos.flush();
                    result = ois.readUTF();
                    Pattern pattern = Pattern.compile("://");

                    String[] strings = pattern.split(result);

                    code = Integer.parseInt(strings[3]);
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
            }
            return code;
    }

    public static File getDataFile(String request) throws InterruptedException {
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
        return null;
    }

    public static String outDataFile(String request) throws InterruptedException {
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

}
