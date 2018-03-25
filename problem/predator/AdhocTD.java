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
	 * ���ط��ʹ���state�Ĵ���
	 * @param state ��ǰ���ʵ�state
	 * @return ���ʵĴ��������û�з�����Ϊ0
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
     * �鿴�Լ��ڵ�ǰstate���Ƿ��������advice
     * @param state ��ǰ��״̬
     * @return true�����������飬false����
     */
    public boolean checkAsk(double[] state){
    	if(advisedState.contains(state)){  //�����ǰ��game�Ѿ�������ṩ����������Ҫ�ٴ��ṩ
    		int numberVisits =  numberVisits(state);
        	if(numberVisits == 0){
        		return true;
        	}
        	
            double param = 0.5;

            double prob = Math.pow((1+param), -1*Math.sqrt(numberVisits));  //����ѯ�ʵĸ���
            
            if(Math.random() < prob){
                return true;
            }
    	}
    	
    	return false;
    }   
    
    /**
     * ����Լ��Ƿ����ṩadvice������������ṩ���飬�������Ľ���ʵ���������Q value��Ӧ��advice
     * @param state
     */
    public int checkAdvise(double[] state, double[][] Qs){
    	int numberVisits = numberVisits(state);
    	if(numberVisits == 0){
    		return -1;  //���ز��ڷ�Χ�ڵ�action�����ܲ�����-1����ʾ����Ϊ�������0��1��2��3�����ı�ʾΪstay
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
        
        return -1;  //ͳͳ��-1��ʾNone action  	
    }
    
    
    /**
     * agent�����Լ��Ľ���action
     * @param state ��ǰ��state
     * @param Qs �����Qs value
     * @param adviseeAction �������agent�Լ���advice
     * @return �����Լ������Ľ���
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
     * agent��ÿ��ѡ��action��ʱ����Ҫ���Լ��Ƿ���Ҫask advice�����߱�����advice������
     * @param reward ��Ҫ���ݵ�reward
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
                delta[o] -= theta[o][prevFa[i]];   //������������²�������
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
                    Qs[o][i] += theta[o][Fas[i][j]];   //�������������Q learning
                }
                if(Qs[o][i] > best[o]){
                    best[o] = Qs[o][i];   //�����Ǹ�state������action������Qֵ
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
        
        //action selection��agent�ȿ��Լ��Ƿ���Ҫ��������advice
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