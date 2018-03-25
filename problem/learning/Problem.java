/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package problem.learning;

/**
 *
 * @author timbrys
 */
public abstract class Problem {

    public Problem() {
        
    }
    
    public abstract void reset();

    public abstract void update();
    
    public abstract boolean isGoalReached();
    
    public abstract int getNumActions();
    
    public abstract double episode();
    
}
