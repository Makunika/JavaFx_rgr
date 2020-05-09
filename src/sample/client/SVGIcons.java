package sample.client;

public enum SVGIcons {
    GET_APP("M19 9h-4V3H9v6H5l7 7 7-7zM5 18v2h14v-2H5z");


    private String path;

    SVGIcons(String s) {
        this.path = s;
    }

    public String getPath() {
        return path;
    }
}
