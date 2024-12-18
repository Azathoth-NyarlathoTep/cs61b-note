package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static gitlet.Utils.*;


/** Represents a gitlet repository.
 *  does at a high level.
 *
 */
public class Repository {
    /**
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
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

    public static void init() {
        if (GITLET_DIR.exists()) {
            exitWithSuccess(
                    "A Gitlet version-control system already exists in the current directory.");
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

        Commit cm = new Commit(null, "initial commit");
        String id = cm.getId();
        cm.saveCommit();
        writeContents(MASTER_FILE, cm.getId());
        updateHEAD("master");
    }

    public static void add(String fileName) {
        checkGitLet();

        if (!join(CWD, fileName).exists()) {
            exitWithSuccess("File does not exist.");
        }
        Commit cm = Commit.fromFile(getBranchFile());

        if (cm.getFileMap() != null && cm.getFileMap().containsKey(fileName)) {
            Stage stage = Stage.fromFile(INDEX_FILE);
            String target = makeBlobId(fileName);
            Blob blob = new Blob(fileName);
            if (stage.getRmList().contains(fileName)) {
                stage.getRmList().remove(fileName);
                stage.saveStage();
                return;
            }
            if (cm.getFileMap().get(fileName).equals(target)) {
                exitWithSuccess("");
            } else {
                createObjectFile(target, blob);
                stage.addAndSave(fileName, target);
            }
        } else {
            String blobID = makeBlobId(fileName);
            Blob blob = new Blob(fileName);
            Stage stage = Stage.fromFile(INDEX_FILE);
            createObjectFile(blobID, blob);
            stage.addAndSave(fileName, blobID);
        }
    }

    public static void remove(String fileName) {
        checkGitLet();

        Stage stage = Stage.fromFile(INDEX_FILE);
        if (stage.getAddMap().containsKey(fileName)) {
            stage.removeFromAdd(fileName);
            return;
        }

        Commit cm = Commit.fromFile(getBranchFile());
        if (cm.getFileMap() != null && cm.getFileMap().containsKey(fileName)) {
            stage.removeAndSave(fileName);
            File file = new File(fileName);
            if (file.exists()) {
                file.delete();
            }
            return;
        }
        exitWithSuccess("No reason to remove the file.");
    }

    public static void commit(String[] args) {
        checkGitLet();

        if (args.length < 2 || args[1].isEmpty()) {
            exitWithSuccess("Please enter a commit message.");
        }
        String msg = args[1];

        Stage stage = Stage.fromFile(INDEX_FILE);
        if (stage.empty()) {
            exitWithSuccess("No changes added to the commit.");
        }

        Commit cm = Commit.fromFile(getBranchFile());
        Commit newcm = new Commit(cm, msg);

        //addition
        if (!stage.addEmpty()) {
            for (Map.Entry<String, String> entry : stage.getAddMap().entrySet()) {
                newcm.addFile(entry.getKey(), entry.getValue());
            }
        }
        //removal
        if (!stage.rmEmpty()) {
            for (String fileName : stage.getRmList()) {
                newcm.removeFile(fileName);
            }
        }

        stage.clearAndSave();
        newcm.saveCommit();
        writeContents(getBranchFile(), newcm.getId());
    }

    public static void log() {
        checkGitLet();

        Commit cm = Commit.fromFile(getBranchFile());
        while (cm != null) {
            System.out.println(cm);
            cm = Commit.fromId(cm.getParentId());
        }
    }

    public static void globalLog() {
        checkGitLet();

        List<String> ls = plainFilenamesIn(COMMITS_DIR);

        if (ls != null) {
            for (String filename : ls) {
                Commit cm = Commit.fromId(filename);
                System.out.println(cm);
            }
        }
    }

    public static void find(String[] args) {
        checkGitLet();

        List<String> ls = plainFilenamesIn(COMMITS_DIR);
        boolean found = false;
        String msg = args[1];

        if (ls != null) {
            for (String filename : ls) {
                Commit cm = Commit.fromId(filename);
                if (cm.getMessage().equals(msg)) {
                    System.out.println(cm.getId());
                    found = true;
                }
            }
        }

        if (!found) {
            exitWithSuccess("Found no commit with that message.");
        }
    }

    public static void status() {
        checkGitLet();

        String curBranch = getCurrentBranch();

        //Branches
        System.out.println("=== Branches ===");
        List<String> ls = plainFilenamesIn(HEADS_DIR);
        for (String filename : ls) {
            if (filename.equals(curBranch)) {
                System.out.println("*" + curBranch);
            } else {
                System.out.println(filename);
            }
        }
        System.out.println();

        //Staged Files
        System.out.println("=== Staged Files ===");
        Stage stage = Stage.fromFile(INDEX_FILE);
        for (String filename : stage.getAddMap().keySet()) {
            System.out.println(filename);
        }
        System.out.println();

        //Removed Files
        System.out.println("=== Removed Files ===");
        for (String filename : stage.getRmList()) {
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
                fileCheckout(args[2], readContentsAsString(getBranchFile()));
                break;
            case 4:
                fileCheckout(args[3], getFullID(args[1]));
                break;
            case 2:
                String branchName = args[1];
                if (!join(HEADS_DIR, branchName).exists()) {
                    exitWithSuccess("No such branch exists.");
                }
                if (getCurrentBranch().equals(branchName)) {
                    exitWithSuccess("No need to checkout the current branch.");
                }
                Commit cm = Commit.fromFile(getBranchFile());
                Commit targetCm = Commit.fromId(readContentsAsString(join(HEADS_DIR, branchName)));
//                checkUntrackedLocal(cm ,targetCm);
                checkUntrackedOverwritten(cm, targetCm);
                if (!readContentsAsString(getBranchFile())
                        .equals(readContentsAsString(getBranchFile(branchName)))) {
                    commitCheckout(cm, targetCm);
                }

                updateHEAD(branchName);
                break;
            default:
                break;
        }
    }

    public static void branch(String[] args) {
        checkGitLet();

        String branchName = args[1];
        if (join(HEADS_DIR, branchName).exists()) {
            exitWithSuccess("A branch with that name already exists.");
        }
        File newBranchFile = join(HEADS_DIR, branchName);
        try {
            newBranchFile.createNewFile();
            writeContents(newBranchFile, readContentsAsString(getBranchFile()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void rmBranch(String[] args) {
        checkGitLet();

        String branchName = args[1];
        if (!join(HEADS_DIR, branchName).exists()) {
            exitWithSuccess("A branch with that name does not exist.");
        }
        if (getCurrentBranch().equals(branchName)) {
            exitWithSuccess("Cannot remove the current branch.");
        }

        File branchFile = join(HEADS_DIR, branchName);
        branchFile.delete();
    }

    public static void reset(String[] args) {
        checkGitLet();

        String cmId = getFullID(args[1]);
        if (!join(COMMITS_DIR, cmId).exists()) {
            exitWithSuccess("No commit with that id exists.");
        }
        Commit curCm = Commit.fromFile(getBranchFile());
        Commit targetCm = Commit.fromId(cmId);
        checkUntrackedOverwritten(curCm, targetCm);
        commitCheckout(curCm, targetCm);
        writeContents(getBranchFile(), cmId);
        Stage stage = Stage.fromFile(INDEX_FILE);
        stage.clearAndSave();
    }

    public static void merge(String[] args) {
        checkGitLet();

        String branchName = args[1];
        checkStageClean();
        checkBranchExists(branchName);
        if (getCurrentBranch().equals(branchName)) {
            exitWithSuccess("Cannot merge a branch with itself.");
        }

        Commit curCm = Commit.fromFile(getBranchFile());
        Commit targetCm = Commit.fromFile(getBranchFile(branchName));
        checkUntrackedOverwritten(curCm, targetCm);

        Set<String> st = getAllParents(curCm);
        Commit splitCm = null;
        while (targetCm != null) {
            if (st.contains(targetCm.getId())) {
                splitCm = Commit.fromId(targetCm.getId());
                break;
            }
            targetCm = Commit.fromId(targetCm.getParentId());
        }
        targetCm = Commit.fromFile(getBranchFile(branchName));

        if (splitCm.getId().equals(curCm.getId())) {
            commitCheckout(curCm, targetCm);
            exitWithSuccess("Current branch fast-forwarded.");
        }
        if (splitCm.getId().equals(targetCm.getId())) {
            exitWithSuccess("Given branch is an ancestor of the current branch.");
        }

        boolean conflictExists = false;
        Set<String> allFiles = new HashSet<String>();
        Map<String, String> curCmMap = curCm.getFileMap();
        Map<String, String> targetCmMap = targetCm.getFileMap();
        Map<String, String> splitCmMap = splitCm.getFileMap();
        Stage stage = Stage.fromFile(INDEX_FILE);
        for (String s:curCmMap.keySet()) {
            allFiles.add(s);
        }
        for (String s:targetCmMap.keySet()) {
            allFiles.add(s);
        }
        for (String fileName : allFiles) {
            conflictExists = casesOutOfSplit(targetCmMap, curCmMap, fileName,
                    targetCm, conflictExists, curCm, splitCmMap, splitCm);
        }
        if (conflictExists) {
            System.out.println("Encountered a merge conflict.");
        }

        mergeCommit(branchName);
    }

    public static boolean casesOutOfSplit(
            Map<String, String> targetCmMap,
            Map<String, String> curCmMap, String fileName,
            Commit targetCm, boolean conflictExists, Commit curCm,
            Map<String, String> splitCmMap, Commit splitCm) {
        Stage stage = Stage.fromFile(INDEX_FILE);
        if (splitCmMap.containsKey(fileName)) {
            if (curCmMap.containsKey(fileName) && targetCmMap.containsKey(fileName)) {
                if (!splitCmMap.get(fileName).equals(targetCmMap.get(fileName))
                        && splitCmMap.get(fileName).equals(curCmMap.get(fileName))) { //case 1
                    stage.getAddMap().put(fileName, targetCmMap.get(fileName));
                    stage.saveStage();
                    fileCheckout(fileName, targetCm.getId());
                    return conflictExists;
                }
                if (!splitCmMap.get(fileName).equals(curCmMap.get(fileName))
                        && splitCmMap.get(fileName)
                        .equals(targetCmMap.get(fileName))) { //case 2
                    return conflictExists;
                }
                if (splitCmMap.get(fileName).equals(curCmMap.get(fileName))
                        && splitCmMap.get(fileName).equals(targetCmMap.get(fileName))) {
                    return conflictExists;
                }
            }
            if (curCmMap.containsKey(fileName)) {
                if (splitCmMap.get(fileName).equals(curCmMap.get(fileName))) { //case 6
                    stage.getRmList().add(fileName);
                    stage.saveStage();
                    join(CWD, fileName).delete();
                    return conflictExists;
                }
            } else {
                if (splitCmMap.get(fileName).equals(targetCmMap.get(fileName))
                        && !curCmMap.containsKey(fileName)) { //case 7
                    return conflictExists;
                }
            }
            conflictExists = dealConflict(splitCm, curCm, targetCm, fileName);
        } else {
            if (!targetCmMap.containsKey(fileName) && curCmMap.containsKey(fileName)) { //case 4
                return conflictExists;
            }
            if (!curCmMap.containsKey(fileName) && targetCmMap.containsKey(fileName)) { //case 5
                stage.getAddMap().put(fileName, targetCmMap.get(fileName));
                stage.saveStage();
                fileCheckout(fileName, targetCm.getId());
                return conflictExists;
            } else { //以相同或者不同方式修改
                if (curCmMap.get(fileName).equals(targetCmMap.get(fileName))) {
                    return conflictExists;
                } else {
                    conflictExists = true;
                    String contents1 = readContentsAsString(join(CWD, fileName));
                    Blob bl = Blob.fromId(targetCm.getFileMap().get(fileName));
                    String contents2 = bl.getContents();
                    String contents = String.format("<<<<<<< HEAD\n%s=======\n%s>>>>>>>\n",
                            contents1, contents2);
                    writeContents(join(CWD, fileName), contents);
                    String blid = makeBlobId(fileName);
                    stage.getAddMap().put(fileName, blid);
                }
            }
        }
        return conflictExists;
    }
}
