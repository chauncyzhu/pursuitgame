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
	 * 返回访问过的state的次数
	 * @param state 当前访问的state
	 * @return 访问的次数，如果没有访问则为0
	 */
	public int numberVisits(double[] state){
		return visitTable.getOrDefault(state, 0);
	}
	
    
    /**
     * 查看自己在当前state下是否可以请求advice
     * @param state 当前的状态
     * @return true即可以请求建议，false则不能
     */
    public boolean checkAsk(double[] state){
    	if(!advisedState.contains(state)){  //如果当前的game已经给这个提供建议了则不需要再次提供
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
//    	System.out.println("advisedAction:"+state.length+"---QS:"+Qs.toString());

    	int numberVisits = numberVisits(state);
    	if(numberVisits == 0){
    		return -1;  //返回不在范围内的action，可能不能用-1来表示，因为这里除了0，1，2，3其他的表示为stay
    	}
    	    	
        double maxQ = -Double.MAX_VALUE;
        double minQ = Double.MAX_VALUE;
        
        //如果Qs有多个object为什么只取第一个？
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
     * 设置自己可以询问的friend
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
     * 从多个advice中选择一个advice
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
    	int b = ibest.get(RNG.randomInt(ibest.size()));   //如果有多个最优值，则选取一个action，但是在原文中则是随机选取
        return b;
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
                curPot[o] = shaping(objectivesToUse[o]);   //noshaping只有一个元素0，return的也是0
            }
        }
        	
        //applies each time a different shaping to the base reward
        double[] delta = new double[nrObjectives];   //如果有多个不同的shaping
        //delta = r + gamma F(s') - F(s)
        for(int o=0; o<nrObjectives; o++){
            delta[o] = reward + gamma*curPot[o] - prevPot[o];  //对于noshaping，最终还是reward+0
        }
        
        //delta = r + gamma F(s') - F(s) - Q(s,a)
        for (int i = 0; i < prevFa.length; i++) {
            for(int o=0; o<nrObjectives; o++){
                delta[o] -= theta[o][prevFa[i]];   //prevFa存储的是上一个state-action的tilecoing，后面的相乘是计算Q值
            }
        }

        double[] state = getState();
        //添加该state到访问过的state中去
        visitTable.put(state, numberVisits(state)+1);
        
        
        //finds activated weights for each action
        int[][] Fas = new int[prob.getNumActions()][];
        for(int i=0; i<prob.getNumActions(); i++){
            Fas[i] = tileCoding(state, i);    //对于当前state的每一个state-action pair
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
                    Qs[o][i] += theta[o][Fas[i][j]];   //计算每个action对应的Q值，主要是这里的theta来计算Q值
                }
                if(Qs[o][i] > best[o]){
                    best[o] = Qs[o][i];   //每个Objectives最好的Q值
                }
            }
        }

        //delta = r + gamma F(s') - F(s) + gamma max_a Q(s',a) - Q(s,a)
        for(int o=0; o<nrObjectives; o++){
            delta[o] += gamma * best[o];   //这里实际上还是Q learning的形式，对于noshaping而言，gamma F(s') - F(s)这一部分为0
        }

        //update weights theta = alpha delta e
        for(int o=0; o<nrObjectives; o++){
            for (int i = 0; i < theta[o].length; i++) {
                theta[o][i] += alpha * delta[o] * es[o][i];   //es是Q(lambda)中的lambda部分
            }
        }
        
        
        Qs = new double[nrObjectives][prob.getNumActions()];  //更新theta后的Q值，实际上就是对每个action而言
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
        
        //action selection，agent先看自己是否需要进行请求advice，这里的QS应该用更新后的Qs
        int action = 0;    //初始化action
        
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
//            			advisedState.add(state);   //将已经advice过的state放进去
            			spentBudgetAsk += 1;
            			action = combineAdvice(advised);
            			
//            			System.out.println("action:"+action);
            			//只是把这个advice当作是探索的一部分？？
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
        
        if(action == 0){  //如果没有给出advice，则
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
                resetEs();   //重新计算es，似乎不把random action算进去，只算greedy action
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