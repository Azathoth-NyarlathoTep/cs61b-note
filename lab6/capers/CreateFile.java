package capers;

import java.io.File;
import java.io.IOException;

public class CreateFile {

    public static void main(String[] args) throws IOException {
        File f = new File("./capers/dummy.txt");
        f.createNewFile();
        if(f.exists()) System.out.println("File exists");
        else System.out.println("File does not exist");
        Utils.writeContents(f,"Hello World");

        System.out.println(System.getProperty("user.dir"));
        File d = new File("dummy");
        d.mkdir();
        if(d.exists()) System.out.println("File exists");
        else System.out.println("File does not exist");
    }
}
