package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static gitlet.Utils.*;

public class Stage implements Serializable {
    private final Map<String , String> addMap;
    private final List<String> rmList;

    public Stage() {
        addMap = new HashMap<>();
        rmList = new ArrayList<>();
    }

    public static Stage fromFile(File file) {
        return readObject(file ,Stage.class);
    }

    public  Map<String, String> getAddMap() {
        return addMap;
    }

    public  List<String> getRmList() {
        return rmList;
    }

   public void saveStage() {
       File filepath = Repository.INDEX_FILE;
       writeObject(filepath , this);
   }

    public void AddAndSave(String filename, String id) {
        addMap.put(filename, id);
        saveStage();
    }

    public void RemoveFromAdd(String filename) {
        addMap.remove(filename);
        saveStage();
    }

    public void RemoveAndSave(String filename) {
        rmList.add(filename);
        saveStage();
    }

    public void clearAndSave() {
        rmList.clear();
        addMap.clear();
        saveStage();
    }

    public boolean empty() {
        if(this.addMap.isEmpty() && this.rmList.isEmpty()){
            return true;
        } else {
            return false;
        }
    }

    public boolean addEmpty() {
        return addMap.isEmpty();
    }

    public boolean rmEmpty() {
        return rmList.isEmpty();
    }

    public boolean contains(String filename) {
        if(addMap.containsKey(filename)){
            return true;
        }
        return rmList.contains(filename);
    }
}
