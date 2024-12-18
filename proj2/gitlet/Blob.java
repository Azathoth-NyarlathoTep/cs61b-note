package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Utils.*;

public class Blob implements Serializable {
    private String id;
    private String contents;

    public Blob(String fileName) {
        File f = new File(fileName);
        contents = readContentsAsString(f);
        id = getFileSha1(fileName);
    }

    public String getId() {
        return id;
    }

    public String getContents() {
        return contents;
    }

    public static Blob fromId(String id) {
        File filepath = join(Repository.OBJECTS_DIR ,id.substring(0, 2));
        Blob blob = readObject(join(filepath ,id.substring(2)) ,Blob.class);
        return blob;
    }
}
