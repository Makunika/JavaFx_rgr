package sample.connection;

import sample.client.DataClient;

public class Request {
    private final String dataMessage;
    private final int code;
    private final String textMessage;

    public Request(String textMessage, String dataMessage, int code)
    {
        this.code = code;
        this.textMessage = textMessage;
        this.dataMessage = dataMessage;
    }

    public Request(String textMessage, int code)
    {
        this.code = code;
        this.textMessage = textMessage;
        this.dataMessage = " ";
    }

    public int getCode() {
        return code;
    }

    private String getRequest()
    {
        return DataClient.login + "/" + DataClient.password + "://" + textMessage + " /" + dataMessage + "://" + code;
    }

    @Override
    public String toString() {
        return getRequest();
    }
}
