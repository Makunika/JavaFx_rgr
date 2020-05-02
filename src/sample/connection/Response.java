package sample.connection;

public class Response {
    private int code;
    private String text;
    private final int validCode;

    public Response(int validCode)
    {
        this.validCode = validCode;
        code = 0;
        text = "";
    }

    public boolean isValidCode()
    {
        return code == validCode;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public int getCode() {
        return code;
    }

}
