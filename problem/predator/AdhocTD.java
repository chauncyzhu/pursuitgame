package problem.predator;

import java.util.*;

import problem.RNG;
import problem.learning.AgentType;
import problem.learning.Problem;

public class AdhocTD extends Predator{
	private int budgetAsk;
	private int budgetAdvise;
	private int spentBudgetAsk;
	private int spentBudgetAdvise;
	private Map<double[], Integer> visitTable = new HashMap<>();
	private List<double[]> advisedState = new ArrayList<>();
    
	/**
	 * 返回访问过的state的次数
	 * @param state 当前访问的state
	 * @return 访问的次数，如果没有访问则为0
	 */
	public int numberVisits(double[] state){
		return visitTable.getOrDefault(state, 0);
	}

    public AdhocTD(PredatorWorld pw, AgentType type, int[] objectives, int size, int x, int y){
        super(pw, type, objectives, size, x, y);
        
        budgetAsk = 0;
        budgetAdvise = 0;
        spentBudgetAsk = 0;
        spentBudgetAdvise = 0;
        
    }
    
    /**
     * 查看自己在当前state下是否可以请求advice
     * @param state 当前的状态
     * @return true即可以请求建议，false则不能
     */
    public boolean checkAsk(double[] state){
    	if(advisedState.contains(state)){  //如果当前的game已经给这个提供建议了则不需要再次提供
    		int numberVisits =  numberVisits(state);
        	if(numberVisits == 0){
        		return true;
        	}
        	
            double param = 0.5;

            double prob = Math.pow((1+param), -1*Math.sqrt(numberVisits));  //计算询问的概率
            
            if(Math.random() < prob){
                return true;
            }
    	}
    	
    	return false;
    }   
    
    /**
     * 检查自己是否能提供advice，如果可以则提供建议，所给出的建议实际上是最大Q value对应的advice
     * @param state
     */
    public int checkAdvise(double[] state, double[][] Qs){
    	int numberVisits = numberVisits(state);
    	if(numberVisits == 0){
    		return -1;  //返回不在范围内的action，可能不能用-1来表示，因为这里除了0，1，2，3其他的表示为stay
    	}
    	    	
        double maxQ = -Double.MAX_VALUE;
        double minQ = Double.MAX_VALUE;
        
        for(int i=0; i<Qs[0].length; i++){
            if(Qs[0][i] >= maxQ){
            	maxQ = Qs[0][i];
            }
            if(Qs[0][i] <= minQ){
            	minQ = Qs[0][i];
            }
        }
        
        double diffQ = Math.abs(maxQ - minQ);
        
        double param = 1.5;

        double value = (Math.sqrt(numberVisits) * diffQ);
        
        double prob = 1 - (Math.pow((1 + param),-value));

        if(Math.random() < prob){
            int advisedAction = actionSelection(Qs);
            return advisedAction;
        }            
        
        return -1;  //统统用-1表示None action  	
    }
    
    
    /**
     * agent给出自己的建议action
     * @param state 当前的state
     * @param Qs 传入的Qs value
     * @param adviseeAction 请求建议的agent自己的advice
     * @return 返回自己给出的建议
     */
    public int adviseAction(double[] state, double[][] Qs, int adviseeAction){
        if(spentBudgetAdvise < budgetAdvise){
        	int advisedAction = checkAdvise(state, Qs);
        	if(advisedAction != -1){
        		if(adviseeAction == -1 || advisedAction != adviseeAction){
        			spentBudgetAdvise += 1;
        			return advisedAction;
        		}
        	} 
        }	
        
        return -1;
    }
       
    
    
     /**
     * agent在每次选择action的时候，需要看自己是否需要ask advice，并具备给出advice的能力
     * @param reward 需要传递的reward
     */
    public void reward(double reward){
        
        //holds potential for each shaping
        double[] curPot = new double[nrObjectives];
        if(type == AgentType.Linear || type == AgentType.BestLinear){
            curPot[0] = scalarizedShaping();
        } else {
            for(int o=0; o<nrObjectives; o++){
                curPot[o] = shaping(objectivesToUse[o]);
            }
        }
        	
        //applies each time a different shaping to the base reward
        double[] delta = new double[nrObjectives];
        //delta = r + gamma F(s') - F(s)
        for(int o=0; o<nrObjectives; o++){
            delta[o] = reward + gamma*curPot[o] - prevPot[o];
        }
        
        //delta = r + gamma F(s') - F(s) - Q(s,a)
        for (int i = 0; i < prevFa.length; i++) {
            for(int o=0; o<nrObjectives; o++){
                delta[o] -= theta[o][prevFa[i]];   //这个是用来更新参数的吗？
            }
        }

        double[] state = getState();
        
        //finds activated weights for each action
        int[][] Fas = new int[prob.getNumActions()][];
        for(int i=0; i<prob.getNumActions(); i++){
            Fas[i] = tileCoding(state, i);
        }
        
        //will store Q-values for each objective-action pair (given current state)
        double Qs[][] = new double[nrObjectives][prob.getNumActions()];
        double[] best = new double[nrObjectives];
        for(int o=0; o<nrObjectives; o++){
            best[o] = -Double.MAX_VALUE;
        }
        
        //calculates Q-values and stores best for each objective
        for(int i=0; i<prob.getNumActions(); i++){
            for(int o=0; o<nrObjectives; o++){
                for (int j = 0; j < Fas[i].length; j++) {
                    Qs[o][i] += theta[o][Fas[i][j]];   //这个才是正常的Q learning
                }
                if(Qs[o][i] > best[o]){
                    best[o] = Qs[o][i];   //这里是该state下所有action中最大的Q值
                }
            }
        }

        //delta = r + gamma F(s') - F(s) + gamma max_a Q(s',a) - Q(s,a)
        for(int o=0; o<nrObjectives; o++){
            delta[o] += gamma * best[o];
        }

        //update weights theta = alpha delta e
        for(int o=0; o<nrObjectives; o++){
            for (int i = 0; i < theta[o].length; i++) {
                theta[o][i] += alpha * delta[o] * es[o][i];
            }
        }
        
        //action selection，agent先看自己是否需要进行请求advice
        boolean ask = checkAsk(state);
        if(ask){
        	
        }
        	
        
        
        int action = 0;
        //greedy
        if (RNG.randomDouble() > epsilon) {
            Qs = new double[nrObjectives][prob.getNumActions()];

            //each tile separately
            double weights[][][] = new double[nrObjectives][prob.getNumActions()][nrTiles];
            
            for(int i=0; i<prob.getNumActions(); i++){
                for (int j = 0; j < Fas[i].length; j++) {
                    for(int o=0; o<nrObjectives; o++){
                        Qs[o][i] += theta[o][Fas[i][j]];
                        weights[o][i][j] = theta[o][Fas[i][j]];
                    }
                }
            }
            //adaptive or random objective selection + action selection
            if(type == AgentType.AOS || type == AgentType.ROS){
                action = adaptiveObjectiveSelection(Qs, weights);
            //regular action selection
            } else if(type == AgentType.NoShaping || type == AgentType.SingleShaping || type == AgentType.Linear || type == AgentType.BestLinear){
                action = actionSelection(Qs);
            }
            
            //decay traces
            for(int o=0; o<nrObjectives; o++){
                for (int i = 0; i < es[o].length; i++) {
                    es[o][i] *= gamma * lambda;
                    if (es[o][i] < 0.000000001) {
                        es[o][i] = 0;
                    }
                }
            }
        //random
        } else {
            action = RNG.randomInt(prob.getNumActions());
            //resets all traces. we should check whether the random action 
            //happens to be greedy wrt to one of the objectives
            resetEs();
        }
        
        //s' = s
        prevFa = tileCoding(state, action);
        prevAction = action;
        
        //store previous potentials
        if(type == AgentType.Linear || type == AgentType.BestLinear){
            prevPot[0] = scalarizedShaping();
        } else {
            for(int o=0; o<nrObjectives; o++){
                prevPot[o] = shaping(objectivesToUse[o]);
            }
        }
        
        //update traces
        for(int o=0; o<nrObjectives; o++){
            for (int i = 0; i < prevFa.length; i++) {
                es[o][prevFa[i]] = 1;
            }
        }
        
    }
}