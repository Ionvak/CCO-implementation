package com.cco;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.lang.Math;

public class ArterialTree extends NelderMeadOptimizer{
    private static final int nToss = 1000; //Number of toss failures before the threshold distance increase.
    private final TreeParams params; //Physical parameters of the tree.
    private  final HashMap<Long, Segment> segments; //Hashmap storing all segments of the tree.
    private boolean isBuilt; //A check for the tree build status. False if tree is initialized but not built, true if tree is initialized and built.
    private  double threshDistance; //The threshold distance.
    private int kTerm; //The number of terminal segments in the tree.
    private int kTot; //The total number of segments in the tree.
    private Point movedPoint; //The shared midpoint in the bifurcation. Used in the bifurcation optimization process.

    public ArterialTree(TreeParams parameters) {
        params = parameters;
        segments = new HashMap<>();
        isBuilt = false;
        kTot = 1;
        kTerm = 1;
        threshDistance = Math.sqrt(Math.PI * Math.pow(parameters.perfRadius, 2) / kTerm);
    }

    /**
     * Calculates the critical distance between a point and a segment. If the
     * point lies along the segment (the projection distance is between 0 (inclusive)
     * and 1 (inclusive)) then the critical distance is the orthogonal distance,
     * otherwise it's the endpoint distance.
     * @param segment
     * The segment from which the critical distance is calculated.
     * @param point
     * The point to which the distance is calculated.
     * @return
     * The critical distance between the segment and the point.
     */
    private double findCritDistance(Segment segment, Point point){
        double dCrit;
        double dProjection = findProjection(segment, point);
        if(0 <= dProjection && dProjection <=1)
            dCrit = findOrthogonal(segment, point);
        else
            dCrit = findEndpoints(segment, point);
        return dCrit;
    }

    /**
     * Calculates the projection distance between a point and a segment.
     * @param segment
     * The segment from which the critical distance is calculated.
     * @param point
     * The point to which the distance is calculated.
     * @return
     * The projection distance between the segment and the point.
     */
    private double findProjection(Segment segment, Point point){
        return (
                ((segment.proximal.x - segment.distal.x) * (point.x - segment.distal.x)) +
                        ((segment.proximal.y - segment.distal.y) * (point.y - segment.distal.y))
        ) /
                Math.pow(segment.length(),2);
    }

    /**
     * Calculates the orthogonal distance between a point of interest and an existing
     * segment within the tree.
     * @param segment
     * The existing segment from which the critical distance is calculated.
     * @param point
     * The point to which the distance is calculated.
     * @return
     * The orthogonal distance between the segment and the point.
     */
    private double findOrthogonal(Segment segment, Point point){
        return Math.abs(
                ((-segment.proximal.y + segment.distal.y) * (point.x - segment.distal.x)) +
                        ((segment.proximal.x - segment.distal.x) * (point.y - segment.distal.y))
        ) /
                segment.length();
    }

    /**
     * Calculates the endpoint distance between a point and a
     * segment. Returns the smaller of the two distances between the segment's
     * two endpoints and the parameter.
     * @param segment
     * The segment from which the critical distance is calculated.
     * @param point
     * The point to which the distance is calculated.
     * @return
     * The orthogonal distance between the segment and the point.
     */
    private double findEndpoints(Segment segment, Point point){
        return Math.min(
                Math.sqrt( Math.pow(point.x - segment.distal.x, 2) +
                        Math.pow(point.y - segment.distal.y, 2) )
                ,
                Math.sqrt( Math.pow(point.x - segment.proximal.x, 2) +
                        Math.pow(point.y - segment.proximal.y, 2) )
        );
    }

    /**
     * Generates and returns a random point within the perfusion area.
     * @return
     * The generated random point.
     */
    private Point toss(){
        double radius = params.perfRadius;
        Random rand = new Random();
        double x = rand.nextDouble() * (2 * radius) -  radius;
        double circleBorder = Math.sqrt(Math.pow(radius, 2) - Math.pow(x, 2));
        double y = rand.nextDouble() * (2 * circleBorder) -  circleBorder;
        return new Point(x,y);
    }

    /**
     * Generates and returns a random point within the perfusion area, with
     * the additional criteria that the critical distance between the generated point
     * and any of the existing tree segments be more than the threshold distance. If the
     * generation fails nToss many times, the threshold distance is decreased by 10%, and the count
     * begins again from 0.
     * @return
     * The generated random point.
     */
    private Point newDistal(){
        boolean invalidDistal = true;
        int loopCount = 0;
        double critDistance;
        double threshDist = threshDistance;
        Point proposedDistal = toss();

        while(invalidDistal){
            proposedDistal = toss();
            invalidDistal = false;
            //check for the threshold distance criteria.
            for(Segment s: segments.values()){
                critDistance = findCritDistance(s, proposedDistal);
                if(critDistance < threshDist)
                    invalidDistal = true;
            }
            //check if number of tosses has reached the maximum number of failures.
            loopCount++;
            if(loopCount == nToss){
                threshDist -= threshDistance * 0.1;
                loopCount = 0;
            }
        }

        return proposedDistal;
    }

    /**
     * Initializes the root segment within the tree.
     */
    private void initRoot(){
        Random rand = new Random();
        double perfRadius = params.perfRadius;
        double x = rand.nextDouble() * (2 * perfRadius) -  perfRadius;
        double y = Math.sqrt(Math.pow(perfRadius, 2) - Math.pow(x, 2));
        if(rand.nextDouble() - 0.5 < 0) y *= -1; //randomly decide if the root should be in the upper or lower half-circle.
        Point rootProximal = new Point(x,y);
        Point rootDistal = newDistal();
        Segment root = new Segment(rootProximal, rootDistal);
        segments.put(root.index, root);
    }

    /**
     * Finds and returns the optimal segment for a bifurcation (one yielding the lowest target value)
     * from among the 20 closest segments. The bifurcation
     * in question is the one leading to a new segment with a distal endpoint at the method parameter.
     * If the tree has at least 20 segments, all the segments are possible candidates.
     * @param distal
     * The distal endpoint of the new segment to be added in the bifurcation.
     * @return
     * The index of the selected optimal segment.
     */
    private long findOptimalCandidate(Point distal){
        ArrayList<Double> distances = new ArrayList<>();
        List<Double> candidateDistances;
        ArrayList<Segment> candidates = new ArrayList<>();
        double minTarget = 1;
        long optimalSegment = 0;
        double target;

        //calculate all the segment distances and find the lowest 20
        for(Segment s: segments.values())
            distances.add(findCritDistance(s,distal));
        distances.sort(null);
        candidateDistances = (distances.size() > 20) ? distances.subList(0,20) : distances;

        //map the corresponding segments to the 20 lowest distances
        for(Segment s: segments.values())
            if(candidateDistances.contains(findCritDistance(s,distal)))
                candidates.add(s);

        //from among the 20 candidate segments, find the one that minimizes the target function
        for(Segment s: candidates)
            if((target = addBif(s.index,distal,false)) < minTarget){
                minTarget = target;
                optimalSegment = s.index;
            }

        return optimalSegment;
    }

    /**
     * Adds a bifurcation to the tree, and calculates the value of the target function
     * after adding the bifurcation.
     * @param where
     * The existing segment at which the bifurcation should be added.
     * @param iNewDistal
     * The distal endpoint of the new segment to be added in the bifurcation.
     * @param keepChanges
     * Decides whether the method should permanently alter the tree, or revert the changes
     * after the bifurcation is added.
     * @return
     * The target function value of the tree after adding the bifurcation.
     */
    private double addBif(Long where, Point iNewDistal, boolean keepChanges){
        Segment iConn = segments.get(where);
        boolean isLeftChild = false;

        //store a reference to the proximal point of iConn previous to the bifurcation
        Point iConnProxPrev = iConn.proximal;
        iConn.proximal = new Point(iConnProxPrev.x, iConnProxPrev.y);
        //shrink iConn by half towards its distal end
        iConn.proximal.x = iConn.proximal.x + 0.5 * (iConn.distal.x - iConn.proximal.x);
        iConn.proximal.y = iConn.proximal.y + 0.5 * (iConn.distal.y - iConn.proximal.y);

        //create, add, and link iBif and iNew to the tree
        Segment iBif = new Segment(iConnProxPrev, iConn.proximal); //find radius
        Segment iNew = new Segment(iBif.distal, iNewDistal);       //find radius
        if(iConn.parent != null){
            if(iConn.parent.childLeft == iConn){
                iConn.parent.childLeft = iBif;
                isLeftChild = true;
            }
            else iConn.parent.childRight = iBif;
        }
        iBif.parent = iConn.parent;
        iConn.parent = iBif;
        iNew.parent = iBif;
        iBif.childLeft = iBif.distal.x > iNew.distal.x ? iNew : iConn;  //check to decide which segment (iNew or iConn) should be
        iBif.childRight = iBif.distal.x <= iNew.distal.x ? iNew : iConn;//the left child of iBif
        segments.put(iBif.index, iBif);
        segments.put(iNew.index, iNew);

        //if the bifurcation should be kept permanently, update the tree attributes
        if(keepChanges) {
            kTot = kTot + 2;
            kTerm++;
            threshDistance = Math.sqrt(Math.PI * Math.pow(params.perfRadius, 2) / kTerm);
        }

        //optimize the bifurcation
        movedPoint = iBif.distal;
        iConn.proximal = movedPoint;
        iNew.proximal = movedPoint;
        double[] x0 = {movedPoint.x, movedPoint.y};
        double[] optimalPoint = fminsearch(x0);
        movedPoint.x = optimalPoint[0];
        movedPoint.y = optimalPoint[1];

        //rescale the tree and get the target function value before the changes are possibly reverted
        rescaleTree();
        double target = getTarget();
        //if the bifurcation should be reverted, undo all the previous changes
        if(!keepChanges) {
            iConn.parent = iBif.parent;
            if(iConn.parent != null){
                if(isLeftChild) iConn.parent.childLeft = iConn;
                else iConn.parent.childRight = iConn;
            }
            iConn.proximal = iConnProxPrev;
            segments.remove(iBif.index);
            segments.remove(iNew.index);
            rescaleTree();
        }
        return target;
    }

    /**
     * An extension of the addBif method wherein the bifurcation is always added
     * to the optimal segment (the one minimizing the target function) and the changes
     * are always permanent.
     * @param iNewDistal
     * The distal endpoint of the new segment to be added in the bifurcation.
     */
    private void addBifOptimal(Point iNewDistal){
        long optimal = findOptimalCandidate(iNewDistal);
        addBif(optimal,iNewDistal,true);
    }

    /**
     * Returns the root segment of the tree.
     * @return
     * The index of the root segment of the tree.
     */
    private long getRoot(){
        long root = 0;
        for(Segment s: segments.values()){
            if(s.parent == null){
                root = s.index;
                break;
            }
        }
        return root;
    }

    /**
     * Calculates and returns the child-to-child radius ratio of the two child segments. Used in the
     * tree rescaling process.
     * @param flowi
     * The flow of the child segment in the nominator.
     * @param flowj
     * The flow of the child segment in the denominator.
     * @param resistancei
     * The reduced resistance of the child segment in the nominator.
     * @param resistancej
     * The reduced resistance of the child segment in the denominator.
     * @return
     * The radius ratio of the two child segments.
     */
    private double childRadiiRatio(double flowi, double flowj, double resistancei, double resistancej){
        return Math.pow((flowi * resistancei)/(flowj * resistancej), 0.25);
    }

    /**
     * Calculates and returns the parent-to-child radius ratio of the segment and one of its child segments. The order
     * of the segments in the resulting ratio depends on their order in the parameter (ri/rj => rj/rp, where i,j
     * indicate child segments, and p indicates the parent segment). Used in the tree rescaling process.
     * @param childRatio
     * The radius ratio of the two child segments.
     * @return
     * The radius ratio of the segment and one of its child segments.
     */
    private double parentRadiiRatio(double childRatio){
        return Math.pow(1 + Math.pow(childRatio, 3), -1.0/3);
    }

    /**
     * Calculates and returns the radius of the root segment of the tree. Used in the tree rescaling process.
     * @param rootResistance
     * The reduced resistance of the root segment.
     * @param rootFlow
     * The flow of the root segment.
     * @return
     * The radius of the root segment of the tree.
     */
    private double rootRadius(double rootResistance, double rootFlow){
        double pressDiff = params.perfPress-params.termPress;
        return Math.pow(rootResistance * rootFlow / pressDiff, 1.0/4);
    }

    /**
     * Calculates and returns the reduced resistance value. Used in the tree rescaling process.
     * @param length
     * The length of the segment.
     * @param leftRatio
     * The radius ratio of the segment and its left child.
     * @param rightRatio
     * The radius ratio of the segment and its right child.
     * @param leftResistance
     * The reduced resistance value of the left child segment.
     * @param rightResistance
     * The reduced resistance value of the right child segment.
     * @return
     * The reduced resistance value of the segment.
     */
    private double reducedResistance(double length, double leftRatio, double rightRatio, double leftResistance, double rightResistance){
        double viscosity = params.viscosity;
        if(leftResistance == 0){
            return (8 * viscosity * length)/(Math.PI);
        }
        else return (8 * viscosity * length)/(Math.PI) +
                Math.pow(Math.pow(leftRatio,4) / (leftResistance) + Math.pow(rightRatio,4) / (rightResistance) ,-1);
    }

    /**
     * Goes through the tree and calculates and sets the values of the child-to-child radius ratios, the parent-to-child
     * radius ratios, and the reduced resistances. This method prepares the tree for the final step in the calculation
     * of the radii done by the {@code calculateRadii} method in which the actual segment radii are calculated.
     * Used in the tree rescaling process.
     * @param segment
     * The segment, starting from which, the top-down rescaling process should begin.
     */
    private void segmentRescale(Segment segment){
        Segment left = segment.childLeft;
        Segment right = segment.childRight;
        double termFlow = params.perfFlow / params.nTerminal;

        if(left != null){
            segmentRescale(left);
            segmentRescale(right);
            segment.childRatio = childRadiiRatio(left.flow(termFlow), right.flow(termFlow), left.resistance, right.resistance);
            segment.leftRatio = parentRadiiRatio(Math.pow(segment.childRatio, -1));
            segment.rightRatio = parentRadiiRatio(segment.childRatio);
            segment.resistance = reducedResistance(segment.length(), segment.leftRatio, segment.rightRatio, left.resistance, right.resistance);
        }
        else{
            segment.resistance = reducedResistance(segment.length(),0,0,0,0);
        }
    }

    /**
     * Goes through the tree and calculates and sets the values of the segment radii. Used in the tree rescaling process.
     * @param segment
     * The segment, starting from which, the top-down radii calculation process should begin.
     * @param radius
     * The radius to be assigned to the parameter segment.
     */
    private void calculateRadii(Segment segment, double radius){
        if(segment == null) return;
        segment.radius = radius;
        if(segment.childLeft != null) {
            calculateRadii(segment.childLeft, segment.radius * segment.leftRatio);
            calculateRadii(segment.childRight, segment.radius * segment.rightRatio);
        }
    }

    /**
     * A wrapper method combining {@code segmentRescale}, {@code rootRadius} and {@code calculateRadii} starting
     * from the root, which rescales the whole tree. The rescaling process is used for the recalculation of all
     * the quantitative segment class attributes after any change in the tree structure that would affect these
     * attributes, such as a bifurcation addition, a change in the topology, etc.
     */
    private void rescaleTree(){
        Segment root = segments.get(getRoot());
        if(root == null) return;

        segmentRescale(root);
        root.radius = rootRadius(root.resistance, root.flow(params.perfFlow/params.nTerminal));
        calculateRadii(root.childLeft, root.radius * root.leftRatio);
        calculateRadii(root.childRight, root.radius * root.rightRatio);
    }

    /**
     * Used by the {@code fminsearch} method of the {@code NelderMeadOptimizer} class to
     * find the value of the target/objective function after moving the optimized point to the location specified
     * by the parameter.
     * @param x
     * Vector indicating the desired coordinates for the optimized point.
     * @return
     * The target function value of the tree.
     */
    @Override
    protected double objectiveFunction(double[] x) {
        movedPoint.x = x[0];
        movedPoint.y = x[1];
        rescaleTree();
        return getTarget();
    }

    // End of internal methods. Beginning of tree interface.


    /**
     * Calculates and returns the target function value of the tree.
     * @return
     * The target function value of the tree.
     */
    public double getTarget(){
        double sum = 0;
        for(Segment s: segments.values()) {
            sum += s.volume();
        }
        return sum;
    }

    /**
     * Builds the tree by adding segments until the number of terminal
     * segments is equal to the number specified in the tree parameters (nTerminal value).
     * The segments are always added into the optimal locations.
     */
    public void buildTree(){
        initRoot();
        while(kTerm < params.nTerminal)
            addBifOptimal(newDistal());
        isBuilt = true;
    }

    /**
     * Prints a detailed summary of the tree content to the terminal.
     */
    public void treeDetails(){
        if(!isBuilt){
            System.out.println("Tree is not built. Nothing to display.");
            return;
        }

        System.out.println("Segments:");
        String segString;
        String result;
        for(Segment s: segments.values()){
            if(s.parent == null) System.out.println("(root)"); //add a marker at the top indicating that this is the root segment
            if(s.childLeft == null) System.out.println("(terminal)");//add a marker at the top indicating that this a terminal segment
            System.out.println("Segment " + s.index + ":");
            segString =
                    """
                    Proximal:   %s
                    Distal:     %s
                    Length:     %f
                    Radius:     %f""";
            result = String.format(segString,
                    s.proximal.toString(),
                    s.distal.toString(),
                    s.length(),
                    s.radius);

            System.out.println(result);
            if(s.parent != null)
                System.out.println("Parent index: " + s.parent.index);
            if(s.childLeft != null)
                System.out.println("Left child index: " + s.childLeft.index);
            if(s.childRight != null)
                System.out.println("Right child index: " + s.childRight.index);
            System.out.println("\n");
        }
        System.out.println("Target function value: " + getTarget() + "\n");
    }

    /**
     * Returns a 2D array containing information about all the segments within the tree.
     * The 2D array has the following form:
     * Row 1: s1_prox_x, s1_dist_x, s1_radius, s2_prox_x, s2_dist_x, s2_radius ...
     * ; Row 2: s1_prox_y, s1_dist_y, 0, s2_prox_y, s2_dist_y, 0 ...
     * (the 0 values are added for padding).
     * @return
     * A 2D array containing information about all the segments within the tree
     */
    public double[][] getSeries(){
        int count = 0;
        double[][] series = new double[2][3 * segments.size()];
        for(Segment s: segments.values()){
            series[0][count] = s.proximal.x;
            series[1][count] = s.proximal.y;
            count++;
            series[0][count] = s.distal.x;
            series[1][count] = s.distal.y;
            count++;
            series[0][count] = s.radius;
            series[1][count] = 0.0;
            count++;
        }
        return series;
    }

}
