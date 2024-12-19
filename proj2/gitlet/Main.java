package gitlet;

import static gitlet.Utils.exitWithSuccess;

/** Driver class for Gitlet, a subset of the Git version-control system.
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            exitWithSuccess("Please enter a command.");
        }
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
            case "rm":
                Repository.Remove(args[1]);
                break;
            case "global-log":
                Repository.globalLog();
                break;
            case "find":
                Repository.Find(args);
                break;
            case "status":
                Repository.Status();
                break;
            case "branch":
                Repository.Branch(args);
                break;
            case "rm-branch":
                Repository.rmBranch(args);
                break;
            case "reset":
                Repository.reset(args);
                break;
            case "merge":
                Repository.Merge(args);
                break;
            default:
                exitWithSuccess("No command with that name exists.");
        }
    }
}
