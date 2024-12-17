package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String id;
    private String message;
    private String timestamp;
    private String parent;
    private String parent2;
    private Map<String ,String> FileMap = new HashMap<String ,String>();
    /* TODO: fill in the rest of this class. */
    public Commit(Commit cm , String msg) {
        message = msg;
        if(cm == null) {        //init的情况
            Date date = new Date(0);
            timestamp = dateToTimeStamp(date);
            parent = null;
            List<String> ls = new ArrayList<>(FileMap.values());
            ls.add(message);
            ls.add(timestamp);
            id = sha1(ls);
        } else {
            Date date = new Date();
            timestamp = dateToTimeStamp(date);
            parent = cm.getId();
            List<String> ls = new ArrayList<>();
            ls.add(message);
            ls.add(timestamp);
            ls.add(parent);
            id = sha1(ls);
        }
    }

    public String getId() {
        return id;
    }

    public String getParentId() {
        return parent;
    }

    public String getMessage() {
        return message;
    }

    public void saveCommit() {
        createCmObjectFile(id , this);
    }

    public static Commit fromFile(File fileName) {
        String id = readContentsAsString(fileName);
        return fromId(id);
    }

    public static Commit fromId(String id) {
        if(id == null) {
            return null;
        }

        File file = join(Repository.COMMITS_DIR ,id);
        return readObject(file ,Commit.class);
    }

    public Map<String ,String> getFileMap() {
        return FileMap;
    }

    public void addFile(String fileName ,String blid) {
        FileMap.put(fileName, blid);
    }

    public void removeFile(String fileName) {
        FileMap.remove(fileName);
    }

    @Override
    public String toString() {
        StringBuilder SB = new StringBuilder("===\n");
        SB.append("commit " + id + "\n");
        SB.append("Date " + timestamp + "\n");
        SB.append(message + "\n");
        return SB.toString();
    }
}