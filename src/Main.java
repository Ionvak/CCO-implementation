import com.cco.ArterialTree;
import com.cco.TreeParams;
import java.io.FileWriter;
import java.io.IOException;

import java.awt.*;

void main() {

    //Build tree
    TreeParams params = new TreeParams(
            3.6e-3, //viscosity
            3,      //bifExponent
            1.33e4, //perfPress
            8.38e3, //termPress
            8.33e-6,//perfFlow
            0.05,   //perfRadius
            8     //numTerminal
    );
    ArterialTree arterialTree = new ArterialTree(params);
    arterialTree.buildTree();
    arterialTree.treeDetails();

    //Export tree
    double[][] series = arterialTree.getSeries();
    try (FileWriter exportWriter = new FileWriter("src/tree_data.txt")) {
        exportWriter.write(Arrays.toString(series[0]) +
                               "\n" +
                               Arrays.toString(series[1]));
        System.out.println("Successfully wrote to the file.");
    }
    catch (IOException e){
        System.out.println("An error occurred.");
        e.printStackTrace();
    }
}
