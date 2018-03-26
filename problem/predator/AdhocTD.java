package problem.predator;

import java.util.*;
import problem.predator.AdviceUtil;
import problem.RNG;
import problem.learning.AgentType;
import problem.learning.Problem;

public class AdhocTD extends Predator{
	private int budgetAsk;
	private int budgetAdvise;
	private int spentBudgetAsk;
	private int spentBudgetAdvise;
	
	private boolean informAction;
	private AdviceUtil adviceObject = null;
	private Map<double[], Integer> visitTable = new HashMap<>();
	private List<double[]> advisedState = new ArrayList<>();
    
	public AdhocTD(PredatorWorld pw, AgentType type, int[] objectives, int size, int x, int y){
        super(pw, type, objectives, size, x, y);
        
        budgetAsk = 100;
        budgetAdvise = 100;
        spentBudgetAsk = 0;
        spentBudgetAdvise = 0;
        informAction = false;
        
    }
	
	/**
	 * ���ط��ʹ���state�Ĵ���
	 * @param state ��ǰ���ʵ�state
	 * @return ���ʵĴ��������û�з�����Ϊ0
	 */
	public int numberVisits(double[] state){
		return visitTable.getOrDefault(state, 0);
	}
	
    
    /**
     * �鿴�Լ��ڵ�ǰstate���Ƿ��������advice
     * @param state ��ǰ��״̬
     * @return true�����������飬false����
     */
    public boolean checkAsk(double[] state){
    	if(!advisedState.contains(state)){  //�����ǰ��game�Ѿ�������ṩ����������Ҫ�ٴ��ṩ
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
//    	System.out.println("advisedAction:"+state.length+"---QS:"+Qs.toString());

    	int numberVisits = numberVisits(state);
    	if(numberVisits == 0){
    		return -1;  //���ز��ڷ�Χ�ڵ�action�����ܲ�����-1����ʾ����Ϊ�������0��1��2��3�����ı�ʾΪstay
    	}
    	    	
        double maxQ = -Double.MAX_VALUE;
        double minQ = Double.MAX_VALUE;
        
        //���Qs�ж��objectΪʲôֻȡ��һ����
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
        
        double now_prob = Math.random();
//        System.out.println("adhoctd checkAdvise");

        if(now_prob < prob){
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
//    	System.out.println("adhoctd advisedAction");

        if(spentBudgetAdvise < budgetAdvise){
//        	System.out.println("advisedAction:"+state+"---QS:"+Qs.toString());

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
     * �����Լ�����ѯ�ʵ�friend
     * @param agentIndex
     * @param allAgents
     */
    @Override
    public void setupAdvising(int agentIndex, Animal[] allAgents){
    	adviceObject = new AdviceUtil();
        
    	List<Animal> advisors = new ArrayList<Animal>();
    	for(int i=0;i<allAgents.length;i++){
    		if(i != agentIndex){
    			advisors.add(allAgents[i]);
    		}
    	}
    	adviceObject.setupAdvisors(advisors);
    }
    
    
    /**
     * �Ӷ��advice��ѡ��һ��advice
     * @param advised
     * @return
     */
    public int combineAdvice(List<Integer> advised){  	
    	int maxcount = -Integer.MAX_VALUE;
        ArrayList<Integer> ibest = new ArrayList <Integer>();

    	for(Integer value:advised){
    		int count = Collections.frequency(advised, value);
    		if(count >= maxcount){
    			if(count > maxcount){
        			ibest.clear();
    			}
        		ibest.add(value);
        		maxcount = count;
    		}
    	}
    	int b = ibest.get(RNG.randomInt(ibest.size()));   //����ж������ֵ����ѡȡһ��action��������ԭ�����������ѡȡ
        return b;
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
                curPot[o] = shaping(objectivesToUse[o]);   //noshapingֻ��һ��Ԫ��0��return��Ҳ��0
            }
        }
        	
        //applies each time a different shaping to the base reward
        double[] delta = new double[nrObjectives];   //����ж����ͬ��shaping
        //delta = r + gamma F(s') - F(s)
        for(int o=0; o<nrObjectives; o++){
            delta[o] = reward + gamma*curPot[o] - prevPot[o];  //����noshaping�����ջ���reward+0
        }
        
        //delta = r + gamma F(s') - F(s) - Q(s,a)
        for (int i = 0; i < prevFa.length; i++) {
            for(int o=0; o<nrObjectives; o++){
                delta[o] -= theta[o][prevFa[i]];   //prevFa�洢������һ��state-action��tilecoing�����������Ǽ���Qֵ
            }
        }

        double[] state = getState();
        //��Ӹ�state�����ʹ���state��ȥ
        visitTable.put(state, numberVisits(state)+1);
        
        
        //finds activated weights for each action
        int[][] Fas = new int[prob.getNumActions()][];
        for(int i=0; i<prob.getNumActions(); i++){
            Fas[i] = tileCoding(state, i);    //���ڵ�ǰstate��ÿһ��state-action pair
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
                    Qs[o][i] += theta[o][Fas[i][j]];   //����ÿ��action��Ӧ��Qֵ����Ҫ�������theta������Qֵ
                }
                if(Qs[o][i] > best[o]){
                    best[o] = Qs[o][i];   //ÿ��Objectives��õ�Qֵ
                }
            }
        }

        //delta = r + gamma F(s') - F(s) + gamma max_a Q(s',a) - Q(s,a)
        for(int o=0; o<nrObjectives; o++){
            delta[o] += gamma * best[o];   //����ʵ���ϻ���Q learning����ʽ������noshaping���ԣ�gamma F(s') - F(s)��һ����Ϊ0
        }

        //update weights theta = alpha delta e
        for(int o=0; o<nrObjectives; o++){
            for (int i = 0; i < theta[o].length; i++) {
                theta[o][i] += alpha * delta[o] * es[o][i];   //es��Q(lambda)�е�lambda����
            }
        }
        
        
        Qs = new double[nrObjectives][prob.getNumActions()];  //����theta���Qֵ��ʵ���Ͼ��Ƕ�ÿ��action����
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
        
        //action selection��agent�ȿ��Լ��Ƿ���Ҫ��������advice�������QSӦ���ø��º��Qs
        int action = 0;    //��ʼ��action
        
        if(spentBudgetAsk < budgetAsk){
        	boolean ask = checkAsk(state);
//            System.out.println("wether agent can ask:"+ask);
        	if(ask){
            	int normalAction;
            	if(informAction){
            		normalAction = super.actionSelection(Qs);
            	}else{
            		normalAction = -1;
            	}
            	
            	List<Integer> advised = adviceObject.askAdvice(state, Qs, normalAction);
//            	System.out.println("ask:"+ask+"---advised:"+advised);
            	if(advised.size() > 0){
            		try{
//            			advisedState.add(state);   //���Ѿ�advice����state�Ž�ȥ
            			spentBudgetAsk += 1;
            			action = combineAdvice(advised);
            			
//            			System.out.println("action:"+action);
            			//ֻ�ǰ����advice������̽����һ���֣���
                        resetEs();
            		}catch (Exception e) {
						System.out.println("Exception when combining the advice"+advised.toString());
					}
            	}
            	if(action == 0 && informAction){
            		action = normalAction;
            	}
            }
        }
        
        if(action == 0){  //���û�и���advice����
        	//greedy
            if (RNG.randomDouble() > epsilon) {
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
                resetEs();   //���¼���es���ƺ�����random action���ȥ��ֻ��greedy action
            }       	
        }
        
//        System.out.println("action:"+action);
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