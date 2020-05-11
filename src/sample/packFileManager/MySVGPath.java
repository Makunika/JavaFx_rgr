package sample.packFileManager;

import javafx.scene.shape.SVGPath;
import sample.client.SVGIcons;

public class MySVGPath extends SVGPath {

    public MySVGPath(SVGIcons content)
    {
        super();
        setContent(content.getPath());
    }
}
