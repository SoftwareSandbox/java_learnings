package ocp.problem;

import static java.lang.Math.PI;

public class AreaCalculator {

    public double calculatRectangleArea(Rectangle rectangle){
        return rectangle.getWidth() * rectangle.getLength();
    }

    public double calculateCircleArea(Circle circle){
        return PI * circle.getRadius() * circle.getRadius();
    }
}
