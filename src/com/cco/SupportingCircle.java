package com.cco;

import java.util.HashMap;
import java.lang.Math;
import java.util.Random;

public class SupportingCircle {

    private double childRadiiRatio(double flowi, double flowj, double resistancei, double resistancej){
        return Math.pow((flowi * resistancei)/(flowj * resistancej), 0.25);
    }

    private double parentRadiiRatio(double childRatio){
        return Math.pow(1 + Math.pow(childRatio, 3), -1.0/3);
    }

    private double rootRadius(double rootResistance, double rootFlow, double pressDiff){
        return Math.pow(rootResistance * rootFlow / pressDiff, 1.0/4);
    }

    private double reducedResistance(double viscosity, double length, double leftRatio, double rightRatio, double leftResistance, double rightResistance){
        if(leftResistance == 0){
            return (8 * viscosity * length)/(Math.PI);
        }
        else return (8 * viscosity * length)/(Math.PI) +
                Math.pow(Math.pow(leftRatio,4) / (leftResistance) + Math.pow(rightRatio,4) / (rightResistance) ,-1);
    }

    private void segmentRescale(Segment segment, double viscosity, double termFlow){
        Segment left = segment.childLeft;
        Segment right = segment.childRight;

        if(left != null){
            segmentRescale(left, viscosity, termFlow);
            segmentRescale(right, viscosity, termFlow);
            segment.childRatio = childRadiiRatio(left.flow(termFlow), right.flow(termFlow), left.resistance, right.resistance);
            segment.leftRatio = parentRadiiRatio(1 / segment.childRatio);
            segment.rightRatio = parentRadiiRatio(segment.childRatio);
            segment.resistance = reducedResistance(viscosity, segment.length(), segment.leftRatio, segment.rightRatio, left.resistance, right.resistance);
        }
        else{
            segment.resistance = reducedResistance(viscosity, segment.length(),0,0,0,0);
        }
    }

    private void calculateRadii(Segment segment, double radius){
        if(segment.childLeft != null) {
            calculateRadii(segment.childLeft, segment.radius * segment.leftRatio);
            calculateRadii(segment.childRight, segment.radius * segment.rightRatio);
        }
        segment.radius = radius;
    }

    private void rescaleTree(HashMap<Long, Segment> Tree, TreeParams params){
        Segment root = Tree.get(1L);
        while(root.parent != null) root = root.parent;
        segmentRescale(root, params.viscosity, params.perfFlow/params.nTerminal);

        root.radius = rootRadius(root.resistance, root.flow(params.perfFlow/params.nTerminal), params.perfPress-params.termPress);
        calculateRadii(root.childLeft, root.radius * root.leftRatio);
        calculateRadii(root.childRight, root.radius * root.rightRatio);
    }



}
