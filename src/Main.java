import com.cco.ArterialTree;
import com.cco.TreeParams;

void main() {

    //Build tree
    TreeParams params = new TreeParams(
            3.6e-3, //viscosity
            3,      //bifExponent
            1.33e4, //perfPress
            8.38e3, //termPress
            8.33e-6,//perfFlow
            0.05,   //perfRadius
            4000      //numTerminal
    );

    ArterialTree arterialTree = new ArterialTree(params);
    arterialTree.buildTree();
    arterialTree.treeDetails();
}
