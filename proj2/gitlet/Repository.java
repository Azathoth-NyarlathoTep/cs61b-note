package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));  //调用了这个方法可以得到CWD为当前工作目录并作为File对象
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File OBJECTS_DIR = join(GITLET_DIR, "Objects");
    public static final File COMMITS_DIR = join(OBJECTS_DIR, "Commits");
    public static final File REFS_DIR = join(GITLET_DIR, "refs");
    public static final File HEADS_DIR = join(REFS_DIR, "heads");
    public static final File REMOTES_DIR = join(REFS_DIR, "remotes");
    public static final File HEAD_FILE = join(GITLET_DIR, "head");
    public static final File INDEX_FILE = join(GITLET_DIR, "INDEX");
    public static final File MASTER_FILE = join(HEADS_DIR, "master");

    public static void Init(){
        if(GITLET_DIR.exists()){
            exitWithSuccess("A Gitlet version-control system already exists in the current directory.");
        }
        GITLET_DIR.mkdir();
        OBJECTS_DIR.mkdir();
        COMMITS_DIR.mkdir();
        REFS_DIR.mkdir();
        HEADS_DIR.mkdir();
        REMOTES_DIR.mkdir();
        Stage stage = new Stage();
        stage.saveStage();

        try {
            MASTER_FILE.createNewFile();
            HEAD_FILE.createNewFile();
            INDEX_FILE.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Commit cm = new Commit(null,"initial commit");
        String id = cm.getId();
        cm.saveCommit();
        writeContents(MASTER_FILE ,cm.getId());
        updateHEAD("master");
    }

    public static void Add(String FileName) {
        checkGitLet();

        if(!join(CWD ,FileName).exists()) {
            exitWithSuccess("File does not exist.");
        }
        Commit cm = Commit.fromFile(getBranchFile());

        if(cm.getFileMap()!=null && cm.getFileMap().containsKey(FileName)) {
            Stage stage = Stage.fromFile(INDEX_FILE);
            String target = makeBlobId(FileName);
            Blob blob = new Blob(FileName);
            if(stage.getRmList().contains(FileName)) {
                stage.getRmList().remove(FileName);
                stage.saveStage();
                return ;
            }
            if(cm.getFileMap().get(FileName).equals(target)) {
                exitWithSuccess("");
            } else {
                createObjectFile(target ,blob);
                stage.AddAndSave(FileName , target);
            }
        } else {
            String Blob_id = makeBlobId(FileName);
            Blob blob = new Blob(FileName);
            Stage stage = Stage.fromFile(INDEX_FILE);
            createObjectFile(Blob_id ,blob);
            stage.AddAndSave(FileName , Blob_id);
        }
    }

    public static void Remove(String FileName) {
        checkGitLet();

        Stage stage = Stage.fromFile(INDEX_FILE);
        if(stage.getAddMap().containsKey(FileName)) {
            stage.RemoveFromAdd(FileName);
            return ;
        }

        Commit cm = Commit.fromFile(getBranchFile());
        if(cm.getFileMap()!=null && cm.getFileMap().containsKey(FileName)) {
            stage.RemoveAndSave(FileName);
            File file = new File(FileName);
            if(file.exists()) {
                file.delete();
            }
            return ;
        }
        exitWithSuccess("No reason to remove the file.");
    }

    public static void commit(String[] args) {
        checkGitLet();

        if(args.length < 2 || args[1].isEmpty()) {
            exitWithSuccess("Please enter a commit message.");
        }
        String msg = args[1];

        Stage stage = Stage.fromFile(INDEX_FILE);
        if(stage.empty()) {
            exitWithSuccess("No changes added to the commit.");
        }

        Commit cm = Commit.fromFile(getBranchFile());
        Commit newcm = new Commit(cm ,msg);

        //addition
        if(!stage.addEmpty()) {
            for(Map.Entry<String ,String> entry : stage.getAddMap().entrySet()) {
                newcm.addFile(entry.getKey(), entry.getValue());
            }
        }
        //removal
        if(!stage.addEmpty()) {
            for(String Filename : stage.getRmList()) {
                newcm.removeFile(Filename);
            }
        }

        stage.clearAndSave();
        newcm.saveCommit();
        writeContents(getBranchFile() , newcm.getId());
    }

    public static void Log() {
        checkGitLet();

        Commit cm = Commit.fromFile(getBranchFile());
        while(cm != null) {
            System.out.println(cm);
            cm = Commit.fromId(cm.getParentId());
        }
    }

    public static void globalLog() {
        checkGitLet();

        List<String> ls = plainFilenamesIn(COMMITS_DIR);

        if (ls != null) {
            for(String filename : ls) {
                Commit cm = Commit.fromId(filename);
                System.out.println(cm);
            }
        }
    }

    public static void Find(String[] args) {
        checkGitLet();

        List<String> ls = plainFilenamesIn(COMMITS_DIR);
        boolean found = false;
        String msg = args[1];

        if(ls != null) {
            for(String filename : ls) {
                Commit cm = Commit.fromId(filename);
                if(cm.getMessage().equals(msg)) {
                    System.out.println(cm.getId());
                    found = true;
                }
            }
        }

        if(!found) {
            exitWithSuccess("Found no commit with that message.");
        }
    }

    public static void Status() {
        checkGitLet();

        String curBranch = getCurrentBranch();

        //Branches
        System.out.println("=== Branches ===");
        List<String> ls = plainFilenamesIn(HEADS_DIR);
        for(String filename : ls) {
            if(filename.equals(curBranch)) {
                System.out.println("*" + curBranch);
            } else {
                System.out.println(filename);
            }
        }
        System.out.println();

        //Staged Files
        System.out.println("=== Staged Files ===");
        Stage stage = Stage.fromFile(INDEX_FILE);
        for(String filename : stage.getAddMap().keySet()) {
            System.out.println(filename);
        }
        System.out.println();

        //Removed Files
        System.out.println("=== Removed Files ===");
        for(String filename : stage.getRmList()) {
            System.out.println(filename);
        }
        System.out.println();

        //TODO : 可先不写等写完再来完善
        System.out.println("=== Modifications Not Staged For Commit ===\n");
        System.out.println("=== Untracked Files ===\n");
    }

    public static void checkout(String[] args) {
        checkGitLet();
        if (args.length > 2 && !args[args.length - 2].equals("--")) {
            exitWithSuccess("Incorrect operands.");
        }

        switch (args.length) {
            case 3:
                fileCheckout(args[2] ,readContentsAsString(getBranchFile()));
                break;
            case 4:
                fileCheckout(args[3] ,getFullID(args[1]));
                break;
            case 2:
                String branchName = args[1];
                if(!join(HEADS_DIR ,branchName).exists()) {
                    exitWithSuccess("No such branch exists.h");
                }
                if(getCurrentBranch().equals(branchName)) {
                    exitWithSuccess("No need to checkout the current branch.");
                }
                Commit cm = Commit.fromFile(getBranchFile());
                Commit targetCm = Commit.fromId(readContentsAsString(join(HEADS_DIR ,branchName)));
//                checkUntrackedLocal(cm ,targetCm);
                checkUntrackedOverwritten(cm, targetCm);
                if(!readContentsAsString(getBranchFile()).equals(readContentsAsString(getBranchFile(branchName)))) {
                    commitCheckout(cm ,targetCm);
                }

                updateHEAD(branchName);
                break;
        }
    }

    public static void Branch(String[] args) {
        checkGitLet();

        String branchName = args[1];
        if(join(HEADS_DIR ,branchName).exists()) {
            exitWithSuccess("A branch with that name already exists.");
        }
        File newBranchFile = join(HEADS_DIR ,branchName);
        try {
            newBranchFile.createNewFile();
            writeContents(newBranchFile,readContentsAsString(getBranchFile()));
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void rmBranch(String[] args) {
        checkGitLet();

        String branchName = args[1];
        if(!join(HEADS_DIR ,branchName).exists()) {
            exitWithSuccess("A branch with that name does not exist.");
        }
        if(getCurrentBranch().equals(branchName)) {
            exitWithSuccess("Cannot remove the current branch.");
        }

        File BranchFile = join(HEADS_DIR ,branchName);
        BranchFile.delete();
    }

    public static void reset(String[] args) {
        checkGitLet();

        String cmId = getFullID(args[1]);
        if(!join(COMMITS_DIR ,cmId).exists()) {
            exitWithSuccess("No commit with that id exists.");
        }
        Commit curCm = Commit.fromFile(getBranchFile());
        Commit targetCm = Commit.fromId(cmId);
        checkUntrackedOverwritten(curCm ,targetCm);
        commitCheckout(curCm ,targetCm);
        writeContents(getBranchFile(),cmId);
        Stage stage = Stage.fromFile(INDEX_FILE);
        stage.clearAndSave();
    }

    public static void Merge(String[] args) {
        checkGitLet();

        String branchName = args[1];
        checkStageClean();
        checkBranchExists(branchName);
        if(getCurrentBranch().equals(branchName)) {
            exitWithSuccess("Cannot merge a branch with itself.");
        }

        Commit curCm = Commit.fromFile(getBranchFile());
        Commit targetCm = Commit.fromFile(getBranchFile(branchName));
        checkUntrackedOverwritten(curCm ,targetCm);

        Set<String> st = getAllParents(curCm);
        Commit splitCm = null;
        while(targetCm != null) {
            if(st.contains(targetCm.getId())) {
                splitCm = Commit.fromId(targetCm.getId());
                break;
            }
            targetCm = Commit.fromId(targetCm.getParentId());
        }
        targetCm = Commit.fromFile(getBranchFile(branchName));

        if(splitCm.getId().equals(curCm.getId())) {
            commitCheckout(curCm ,targetCm);
            exitWithSuccess("Current branch fast-forwarded");
        }
        if(splitCm.getId().equals(targetCm.getId())) {
            exitWithSuccess("Given branch is an ancestor of the current branch.");
        }

        boolean conflictExists = false;
        Set<String> allFiles = new HashSet<String>();
        Map<String ,String> curCmMap = curCm.getFileMap();
        Map<String ,String> targetCmMap = targetCm.getFileMap();
        Map<String ,String> splitCmMap = splitCm.getFileMap();
        Stage stage = Stage.fromFile(INDEX_FILE);
        for(String s:curCmMap.keySet()) {
            allFiles.add(s);
        }
        for(String s:targetCmMap.keySet()) {
            allFiles.add(s);
        }
        for(String fileName : allFiles) {
            if(splitCmMap.containsKey(fileName)) {
                if(curCmMap.containsKey(fileName) && targetCmMap.containsKey(fileName)) {
                    if(!splitCmMap.get(fileName).equals(targetCmMap.get(fileName)) && splitCmMap.get(fileName).equals(curCmMap.get(fileName))) { //case 3
                        stage.getAddMap().put(fileName,targetCmMap.get(fileName));
                        fileCheckout(fileName , targetCm.getId());
                        continue;
                    }
                    if(!splitCmMap.get(fileName).equals(curCmMap.get(fileName)) && splitCmMap.get(fileName).equals(targetCmMap.get(fileName))) { //case 4
                        continue;
                    }
                }
                if(curCmMap.containsKey(fileName)) {
                    if(splitCmMap.get(fileName).equals(curCmMap.get(fileName))) { //case 5
                        stage.getRmList().add(fileName);
                        join(CWD ,fileName).delete();
                        continue;
                    } else {
                        if(splitCmMap.get(fileName).equals(targetCmMap.get(fileName))) { //case 5
                            continue;
                        }
                    }
                } else {
                    if (splitCmMap.get(fileName).equals(targetCmMap.get(fileName)) && !curCmMap.containsKey(fileName)) {
                        continue;
                    }
                }
                conflictExists  = dealConflict(splitCm ,curCm ,targetCm ,fileName);
            } else {
                if(!targetCmMap.containsKey(fileName) && curCmMap.containsKey(fileName)) { //case 6
                    continue;
                }
                if(!curCmMap.containsKey(fileName) && targetCmMap.containsKey(fileName)) {
                    stage.getAddMap().put(fileName,targetCmMap.get(fileName));
                    fileCheckout(fileName , targetCm.getId());
                    continue;
                }
            }
        }
        if(conflictExists) {
            System.out.println("Encountered a merge conflict.");
        }

        mergeCommit(branchName);
    }
    /* TODO: fill in the rest of this class. */
}
