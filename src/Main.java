import com.cco.ArterialTree;
import com.cco.TreeParams;

void main() {
    long startTime = System.nanoTime();
    //Build tree
    TreeParams params = new TreeParams(
            3.6e-3, //viscosity
            3,      //bifExponent
            1.33e4, //perfPress
            8.38e3, //termPress
            8.33e-6,//perfFlow
            0.05,   //perfRadius
            4      //numTerminal
    );

    ArterialTree arterialTree = new ArterialTree(params);
    arterialTree.buildTree();
    arterialTree.treeDetails();

    long endTime = System.nanoTime();
    long seconds = (endTime - startTime) / 1000000000;
    long minutes = 0;
    long hours = 0;
    if(seconds >= 60){
        minutes = seconds / 60;
        seconds = seconds % 60;
        if(minutes >= 60){
            hours = minutes / 60;
            minutes = minutes % 60;
        }
    }
    System.out.println("Execution Time: " + hours + "h " + minutes + "m " + seconds + "s");
}
