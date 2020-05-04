package sample.connection;

import javafx.concurrent.Service;
import sample.client.DataClient;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public abstract class GetData extends Service<Response> {
    protected void outMessage(DataOutputStream oos, Request request) throws IOException {
        oos.writeUTF(request.toString());
        oos.flush();
    }

    protected Response inMessage(DataInputStream ois, int validCode) throws IOException {
        Response response = new Response(validCode);
        int bytesLength = ois.readInt();
        byte[] bytes = new byte[bytesLength];
        ois.readNBytes(bytes,0,bytesLength);
        String result = new String(bytes, StandardCharsets.UTF_8);
        Pattern pattern = Pattern.compile("://");
        System.out.println("in: " + result);
        String[] strings = pattern.split(result);
        //Если то, что пришло, неверно, то ставим код 599
        if (strings.length != 4) response.setCode(599);
        else {
            //Если все верно, то код и текст записываем в нетворкдату
            response.setCode(Integer.parseInt(strings[3]));
            response.setText(strings[2]);
        }
        return response;
    }


    protected static byte[] longToBytes(long x) {
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte)(x & 0xFF);
            x >>= 8;
        }
        return result;
    }

    protected static long bytesToLong(byte[] bytes) {
        long result = 0;
        for (int i = 0; i < Long.BYTES; i++) {
            result <<= Long.BYTES;
            result |= (bytes[i] & 0xFF);
        }
        return result;
    }

}
