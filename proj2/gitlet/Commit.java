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
    private List<String> parents = new ArrayList<>();
    private Map<String ,String> FileMap = new HashMap<String ,String>();
    /* TODO: fill in the rest of this class. */
    public Commit(Commit cm , String msg) {
        message = msg;
        if(cm == null) {        //init的情况
            Date date = new Date(0);
            timestamp = dateToTimeStamp(date);
            List<String> ls = new ArrayList<>(FileMap.values());
            ls.add(message);
            ls.add(timestamp);
            id = sha1(ls);
        } else {
            FileMap = new HashMap<>(cm.getFileMap());
            Date date = new Date();
            timestamp = dateToTimeStamp(date);
            parents.add(cm.getId());
            List<String> ls = new ArrayList<>();
            ls.add(message);
            ls.add(timestamp);
            ls.add(parents.get(0));
            id = sha1(ls);
        }
    }

    public Commit(Commit cm1 ,Commit cm2 , String msg) {
        message = msg;
        Date date = new Date();
        timestamp = dateToTimeStamp(date);
        parents.add(cm1.getId());
        parents.add(cm2.getId());
        FileMap = new HashMap<>(cm1.getFileMap());
        List<String> ls = new ArrayList<>(FileMap.values());
        ls.add(message);
        ls.add(timestamp);
        id = sha1(ls);
    }

    public String getId() {
        return id;
    }

    public String getParentId() {
        if(parents.isEmpty()) {
            return null;
        }
        return parents.get(0);
    }

    public String getSecondParentId() {
        if(parents.size() < 2) {
            return null;
        }
        return parents.get(1);
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

        if(!join(Repository.COMMITS_DIR ,id).exists()) {
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
        if(parents.size() <= 1) {
            StringBuilder SB = new StringBuilder("===\n");
            SB.append("commit " + id + "\n");
            SB.append("Date: " + timestamp + "\n");
            SB.append(message + "\n");
            return SB.toString();
        }
        return String.format(
                "===\ncommit %s\nMerge: %s %s\nDate: %s\n%s\n",
                id, getParentId().substring(0, 7), getSecondParentId().substring(0, 7), timestamp, message);
    }
}