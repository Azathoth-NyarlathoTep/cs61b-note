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
                Repository.init();
                break;
            case "add":
                Repository.add(args[1]);
                break;
            case "commit":
                Repository.commit(args);
                break;
            case "checkout":
                Repository.checkout(args);
                break;
            case "log":
                Repository.log();
                break;
            case "rm":
                Repository.remove(args[1]);
                break;
            case "global-log":
                Repository.globalLog();
                break;
            case "find":
                Repository.find(args);
                break;
            case "status":
                Repository.status();
                break;
            case "branch":
                Repository.branch(args);
                break;
            case "rm-branch":
                Repository.rmBranch(args);
                break;
            case "reset":
                Repository.reset(args);
                break;
            case "merge":
                Repository.merge(args);
                break;
            default:
                exitWithSuccess("No command with that name exists.");
        }
    }
}
