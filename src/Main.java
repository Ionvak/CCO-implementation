import com.cco.*;

public class Main {

    public static void main(String[] args) {
        double viscosity = 0;
        double bifurcationExponent = 0;
        double distalPressure = 0;
        double perfusionPressure = 0;
        double perfusionFlow = 0;
        double perfusionArea = 0;
        int N_terminal = 0;

        ArterialTree newTree = new ArterialTree(viscosity,bifurcationExponent,distalPressure,perfusionPressure,perfusionFlow,perfusionArea,N_terminal);
        newTree.displayTree();
        }
}
