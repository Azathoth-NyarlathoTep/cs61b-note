package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                Repository.Init();
                break;
            case "add":
                Repository.Add(args[1]);
                break;
            case "commit":
                Repository.commit(args);
                break;
            case "checkout":
                Repository.checkout(args);
                break;
            case "log":
                Repository.Log();
                break;
            // TODO: FILL THE REST IN
        }
    }
}
