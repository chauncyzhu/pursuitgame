/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package problem.predator;

import java.util.ArrayList;
import java.util.List;

import problem.learning.AgentType;
import problem.learning.QLearningAgent;

/**
 * ×¢Òâ¼Ì³Ð×ÔQ learning agent
 * @author timbrys
 */
public abstract class Animal extends QLearningAgent{
    public boolean predator;
    public int x;
    public int y;
    
    public Animal(PredatorWorld pw, AgentType type, int[] objectives, int x, int y){
        super(pw, type, objectives);
        this.predator = false;
        this.x = x;
        this.y = y;
    }
    
	public abstract int numberVisits(double[] state);
	
    public abstract boolean checkAsk(double[] state);
    
    public abstract int checkAdvise(double[] state, double[][] Qs);
    
    public abstract int adviseAction(double[] state, double[][] Qs, int adviseeAction);
    
    public abstract int combineAdvice(List<Integer> advised);
    
    public abstract void setupAdvising(int agentIndex, Animal[] allAgents);
    
}
