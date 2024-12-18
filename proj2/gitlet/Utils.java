package gitlet;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import static gitlet.Repository.*;


/** Assorted utilities.
 *
 * Give this file a good read as it provides several useful utility functions
 * to save you some time.
 *
 *  @author P. N. Hilfinger
 */
class Utils {

    /** The length of a complete SHA-1 UID as a hexadecimal numeral. */
    static final int UID_LENGTH = 40;

    /* SHA-1 HASH VALUES. */

    /** Returns the SHA-1 hash of the concatenation of VALS, which may
     *  be any mixture of byte arrays and Strings. */
    static String sha1(Object... vals) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            for (Object val : vals) {
                if (val instanceof byte[]) {
                    md.update((byte[]) val);
                } else if (val instanceof String) {
                    md.update(((String) val).getBytes(StandardCharsets.UTF_8));
                } else {
                    throw new IllegalArgumentException("improper type to sha1");
                }
            }
            Formatter result = new Formatter();
            for (byte b : md.digest()) {
                result.format("%02x", b);
            }
            return result.toString();
        } catch (NoSuchAlgorithmException excp) {
            throw new IllegalArgumentException("System does not support SHA-1");
        }
    }

    /** Returns the SHA-1 hash of the concatenation of the strings in
     *  VALS. */
    static String sha1(List<? extends Object> vals) {
        return sha1(vals.toArray(new Object[vals.size()]));
    }

    static String getFileSha1(String filename) {
        File file = new File(filename);
        String contents = readContentsAsString(file);
        return sha1(filename, contents);
    }

    /* FILE DELETION */

    /** Deletes FILE if it exists and is not a directory.  Returns true
     *  if FILE was deleted, and false otherwise.  Refuses to delete FILE
     *  and throws IllegalArgumentException unless the directory designated by
     *  FILE also contains a directory named .gitlet. */
    static boolean restrictedDelete(File file) {
        if (!(new File(file.getParentFile(), ".gitlet")).isDirectory()) {
            throw new IllegalArgumentException("not .gitlet working directory");
        }
        if (!file.isDirectory()) {
            return file.delete();
        } else {
            return false;
        }
    }

    /** Deletes the file named FILE if it exists and is not a directory.
     *  Returns true if FILE was deleted, and false otherwise.  Refuses
     *  to delete FILE and throws IllegalArgumentException unless the
     *  directory designated by FILE also contains a directory named .gitlet. */
    static boolean restrictedDelete(String file) {
        return restrictedDelete(new File(file));
    }

    /* READING AND WRITING FILE CONTENTS */

    /** Return the entire contents of FILE as a byte array.  FILE must
     *  be a normal file.  Throws IllegalArgumentException
     *  in case of problems. */
    static byte[] readContents(File file) {
        if (!file.isFile()) {
            throw new IllegalArgumentException("must be a normal file");
        }
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /** Return the entire contents of FILE as a String.  FILE must
     *  be a normal file.  Throws IllegalArgumentException
     *  in case of problems. */
    static String readContentsAsString(File file) {
        return new String(readContents(file), StandardCharsets.UTF_8);
    }

    /** Write the result of concatenating the bytes in CONTENTS to FILE,
     *  creating or overwriting it as needed.  Each object in CONTENTS may be
     *  either a String or a byte array.  Throws IllegalArgumentException
     *  in case of problems. */
    static void writeContents(File file, Object... contents) {
        try {
            if (file.isDirectory()) {
                throw
                    new IllegalArgumentException("cannot overwrite directory");
            }
            BufferedOutputStream str =
                new BufferedOutputStream(Files.newOutputStream(file.toPath()));
            for (Object obj : contents) {
                if (obj instanceof byte[]) {
                    str.write((byte[]) obj);
                } else {
                    str.write(((String) obj).getBytes(StandardCharsets.UTF_8));
                }
            }
            str.close();
        } catch (IOException | ClassCastException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /** Return an object of type T read from FILE, casting it to EXPECTEDCLASS.
     *  Throws IllegalArgumentException in case of problems. */
    static <T extends Serializable> T readObject(File file,
                                                 Class<T> expectedClass) {
        try {
            ObjectInputStream in =
                new ObjectInputStream(new FileInputStream(file));
            T result = expectedClass.cast(in.readObject());
            in.close();
            return result;
        } catch (IOException | ClassCastException
                 | ClassNotFoundException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /** Write OBJ to FILE. */
    static void writeObject(File file, Serializable obj) {
        writeContents(file, serialize(obj));
    }

    /* DIRECTORIES */

    /** Filter out all but plain files. */
    private static final FilenameFilter PLAIN_FILES =
        new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return new File(dir, name).isFile();
            }
        };

    /** Returns a list of the names of all plain files in the directory DIR, in
     *  lexicographic order as Java Strings.  Returns null if DIR does
     *  not denote a directory. */
    static List<String> plainFilenamesIn(File dir) {
        String[] files = dir.list(PLAIN_FILES);
        if (files == null) {
            return null;
        } else {
            Arrays.sort(files);
            return Arrays.asList(files);
        }
    }

    /** Returns a list of the names of all plain files in the directory DIR, in
     *  lexicographic order as Java Strings.  Returns null if DIR does
     *  not denote a directory. */
    static List<String> plainFilenamesIn(String dir) {
        return plainFilenamesIn(new File(dir));
    }

    /* OTHER FILE UTILITIES */

    /** Return the concatentation of FIRST and OTHERS into a File designator,
     *  analogous to the {@link java.nio.file.Paths. #get(String, String[])}
     *  method. */
    static File join(String first, String... others) {
        return Paths.get(first, others).toFile();
    }

    /** Return the concatentation of FIRST and OTHERS into a File designator,
     *  analogous to the {@link java.nio.file.Paths. #get(String, String[])}
     *  method. */
    static File join(File first, String... others) {
        return Paths.get(first.getPath(), others).toFile();
    }


    /* SERIALIZATION UTILITIES */

    /** Returns a byte array containing the serialized contents of OBJ. */
    static byte[] serialize(Serializable obj) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ObjectOutputStream objectStream = new ObjectOutputStream(stream);
            objectStream.writeObject(obj);
            objectStream.close();
            return stream.toByteArray();
        } catch (IOException excp) {
            throw error("Internal error serializing commit.");
        }
    }



    /* MESSAGES AND ERROR REPORTING */

    /** Return a GitletException whose message is composed from MSG and ARGS as
     *  for the String.format method. */
    static GitletException error(String msg, Object... args) {
        return new GitletException(String.format(msg, args));
    }

    /** Print a message composed from MSG and ARGS as for the String.format
     *  method, followed by a newline. */
    static void message(String msg, Object... args) {
        System.out.printf(msg, args);
        System.out.println();
    }

    //以下代码都是自己添加的方法
    static void exitWithSuccess(String s) {
        if (!Objects.equals(s, "") && s != null) {
            System.out.println(s);
        }
        System.exit(0);
    }

    static String dateToTimeStamp(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT-8"));
        return dateFormat.format(date);
//        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
//        return dateFormat.format(date);
    }

    static void createObjectFile(String id, Serializable obj) {
        File filepath = join(Repository.OBJECTS_DIR, id.substring(0, 2));
        if (!filepath.exists()) {
            filepath.mkdirs();
        }
        File file = join(filepath, id.substring(2));
        try {
            file.createNewFile();
            writeObject(file, obj);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void createCmObjectFile(String id, Serializable obj) {
        File file = join(Repository.COMMITS_DIR, id);
        try {
            file.createNewFile();
            writeObject(file, obj);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void updateHEAD(String s) {
//          File file = join(Repository.HEADS_DIR,s);
//          File root = join(Repository.GITLET_DIR);
//          writeContents(Repository.HEAD_FILE,file.toPath().relativize(root.toPath()).toString());
        File branchFile = join(Repository.HEADS_DIR, s);
        writeContents(Repository.HEAD_FILE,
                Repository.GITLET_DIR.toURI().relativize(branchFile.toURI()).getPath());
    }

    static File getBranchFile(String... args) {
        //这里选用...来传入args数组，这是好的，因为这样可以传入0个参数也是可以的，比之String[]更灵活，String[]更适合明确要用的是字符串数组的情况
        switch (args.length) {
            case 0:
                return join(Repository.GITLET_DIR, readContentsAsString(HEAD_FILE));
            case 1:
                return join(HEADS_DIR, args[0]);
            default:
                return null;
        }
    }

    static String makeBlobId(String filename) {
        File file = new File(filename);
        String contents = readContentsAsString(file);
        return sha1(contents);
    }

    static void checkGitLet() {
        if (!Repository.GITLET_DIR.exists()) {
            exitWithSuccess("Not in an initialized GitLet directory.");
        }
    }

    static String getCurrentBranch() {
        String str = readContentsAsString(HEAD_FILE);
        String[] tmp = str.split("/");
        String cr = tmp[tmp.length - 1];
        return cr;
    }

    static void fileCheckout(String filename, String id) {
        id = getFullID(id);
        Commit cm = Commit.fromId(id);
        if (cm == null) {
            exitWithSuccess("No commit with that id exists.");
        }
        String blobId = cm.getFileMap().get(filename);
        if (blobId == null) {
            exitWithSuccess("File does not exist in that commit.");
        }
        Blob blob = Blob.fromId(blobId);
        File target = join(Repository.CWD, filename);
        writeContents(target, blob.getContents());
    }

    static void commitCheckout(Commit curCm, Commit targetCm) {
        for (String filename : curCm.getFileMap().keySet()) {
            if (!targetCm.getFileMap().containsKey(filename)) {
                File f = join(CWD, filename);
                f.delete();
            } else {
                if (!targetCm.getFileMap().get(filename).equals(curCm.getFileMap().get(filename))) {
                    fileCheckout(filename, targetCm.getId());
                }
            }
        }

        if (targetCm.getFileMap() != null) {
            for (String filename : targetCm.getFileMap().keySet()) {
                if (curCm.getFileMap().get(filename) == null) {
                    fileCheckout(filename, targetCm.getId());
                }
            }
        }
    }

    static void checkUntrackedOverwritten(Commit curCm, Commit targetCm) {
        //参考答案版本只是遍历了当前的头提交而未有遍历暂存区，而按个人理解应该都要遍历以确认是否满足“未被跟踪的条件”
//        Stage stage = Stage.fromFile(INDEX_FILE);
        for (String filename : targetCm.getFileMap().keySet()) {
            if (join(CWD, filename).exists() && !curCm.getFileMap().containsKey(filename)) {
                exitWithSuccess(
                        "There is an untracked file in the way;"
                                + " delete it, or add and commit it first.");
            }
        }
    }

    static void checkUntrackedLocal(Commit curCm, Commit targetCm) {
        Stage stage = Stage.fromFile(INDEX_FILE);

        List<String> allFiles = plainFilenamesIn(CWD);
        for (String filename : allFiles) {
            if (Objects.equals(filename, "gitlet-design.md")
                    || Objects.equals(filename, "Makefile")
                    || Objects.equals(filename, "pom.xml")) {
                continue;
            } //To be deleted.

            if (!curCm.getFileMap().containsKey(filename) && !stage.contains(filename)) {
                exitWithSuccess(
                        "There is an untracked file in the  way;"
                                + " delete it, or add and commit it first.");
            }
        }
    }

    static void checkStageClean() {
        Stage stage = Stage.fromFile(INDEX_FILE);
        if (!stage.empty()) {
            exitWithSuccess("You have uncommitted changes.");
        }
    }

    static void checkBranchExists(String branchName) {
        if (!join(HEADS_DIR, branchName).exists()) {
            exitWithSuccess("A branch with that name does not exist.");
        }
    }

    static Set<String> getAllParents(Commit cm) {
        Set<String> parents = new HashSet<>();
        while (cm != null) {
            parents.add(cm.getId());
            if (cm.getSecondParentId() != null) {
                parents.addAll(getAllParents(Commit.fromId(cm.getSecondParentId())));
            }
            cm = Commit.fromId(cm.getParentId());
        }
        return parents;
    }

    public static boolean dealConflict(Commit splitCm, Commit curCm,
                                       Commit targetCm, String fileName) {
        boolean conflictExist = false;
        if (!targetCm.getFileMap().containsKey(fileName)) {
            conflictExist = true;
        } else if (!curCm.getFileMap().containsKey(fileName)) {
            conflictExist = true;
        } else if (!curCm.getFileMap().get(fileName).equals(targetCm.getFileMap().get(fileName))) {
            conflictExist = true;
        }
        if (conflictExist) {
            Stage stage = Stage.fromFile(INDEX_FILE);
            String contents1 = "";
            String contents2 = "";
            if (curCm.getFileMap().containsKey(fileName)) {
                contents1 = readContentsAsString(join(CWD, fileName));
            }
            if (targetCm.getFileMap().containsKey(fileName)) {
                Blob bl = Blob.fromId(targetCm.getFileMap().get(fileName));
                contents2 = bl.getContents();
            }
            String contents = String.format(
                    "<<<<<<< HEAD\n%s=======\n%s>>>>>>>\n", contents1, contents2);
            writeContents(join(CWD, fileName), contents);
            String blid = makeBlobId(fileName);
            stage.getAddMap().put(fileName, blid);
            stage.saveStage();
        }
        return conflictExist;
    }

    public static String getFullID(String id) {
        if (id.length() == 40) {
            return id;
        }

        List<String> fileNames = plainFilenamesIn(COMMITS_DIR);
        for (String fileName : fileNames) {
            if (fileName.startsWith(id)) {
                return fileName;
            }
        }
        return null;
    }

    public static void mergeCommit(String branchName) {
        Stage stage = Stage.fromFile(INDEX_FILE);
        if (stage.empty()) {
            exitWithSuccess("No changes added to the commit.");
        }

        String msg = String.format("Merged %s into %s.", branchName, getCurrentBranch());
        Commit cm = Commit.fromFile(getBranchFile());
        Commit cm2 = Commit.fromFile(getBranchFile(branchName));
        Commit newCm = new Commit(cm, cm2, msg);

        //addition
        if (!stage.addEmpty()) {
            for (Map.Entry<String, String> entry : stage.getAddMap().entrySet()) {
                newCm.addFile(entry.getKey(), entry.getValue());
            }
        }
        //removal
        if (!stage.addEmpty()) {
            for (String fileName : stage.getRmList()) {
                newCm.removeFile(fileName);
            }
        }

        stage.clearAndSave();
        newCm.saveCommit();
        writeContents(getBranchFile(), newCm.getId());
    }
}
