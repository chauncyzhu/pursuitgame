package problem.learning;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.ArrayList;
import org.apache.commons.math3.stat.inference.TTest;
import problem.RNG;

/**
 *
 * @author timbrys
 */
public abstract class LearningAgent{
    
    public static int nrObjectives;
    
    //learning params
    protected double alpha;
    protected double epsilon;
    protected double gamma;
    protected double lambda;
    
    //weights and traces for each 'objective'
    protected double[][] theta;
    protected double[][] es;
    
    //previous action, activated tiles and potential
    protected int prevAction;
    protected int[] prevFa;
    protected double[] prevPot;
    
    //tile coding params
    protected int nrTiles;
    protected int maxNrTiles;
    
    //which shapings are used and how they are combined
    protected AgentType type;
    protected int[] objectivesToUse;
    
    //weights for linear scalarization
    protected double[] ws;
    
    //problem class, in this case predatorworld
    protected Problem prob;
    
    public LearningAgent(Problem prob, AgentType type, int[] objectivesToUse){
        this.type = type;
        this.objectivesToUse = objectivesToUse;  //对于noshaping而言，只有一个0
        nrObjectives = objectivesToUse.length;   //对于noshaping，长度为1
        
        maxNrTiles = 4096;
        nrTiles = 14;

        this.prob = prob;
        
        alpha = 0.1 / nrTiles;
        if(type == AgentType.Random){
            epsilon = 1.0;
        } else {
            epsilon = 0.1;
        }
        
        gamma = 0.99;
        lambda = 0.9;
    }
    
    public void initialize(){
        prevAction = RNG.randomInt(prob.getNumActions());   //action是随机初始化的
        prevFa = null;
        prevPot = null;

        theta = new double[nrObjectives][maxNrTiles];
        es = new double[nrObjectives][maxNrTiles];
        
        ws = new double[nrObjectives];
        double totalt = 1.0;
        double totaln = 0.0;
        for(int o=0; o<nrObjectives; o++){
            if(type == AgentType.BestLinear){
                totalt *= shapingNormalization(objectivesToUse[o]);
                double tmp = 1.0;
                for(int j=0; j<nrObjectives; j++){
                    if(j != o){
                        tmp *= shapingNormalization(objectivesToUse[j]);
                    }
                }
                totaln += tmp;
            } else {
                ws[o] = 1.0/nrObjectives;
            }
        }
        
        if(type == AgentType.BestLinear){
            for(int o=0; o<nrObjectives; o++){
                ws[o] = (totalt/totaln)/shapingNormalization(objectivesToUse[o]);
            }
        }
    }
    
    public void resetEs(){
        es = new double[nrObjectives][maxNrTiles];
    }
    
    public void resetEs(int o){
        es[o] = new double[maxNrTiles];
    }
    
    protected void offpolicyAction(int o){
        resetEs(o);
    }
    
    public double v(int objective, double[] state){
        return 0.0;
    }
    public abstract double[] getState();
    public abstract int getStateSize();
    public abstract double shaping(int s);
    public abstract double shapingNormalization(int s);
    public void endEpisode(){
        
    }
    //Returns the greedy action wrt the objective that yields the highest 
    //confidence in the current state, given Qs[objective][action] and 
    //weights[objective][action][tile]. 
    //(Basically Qs[objective][action] = \SUM_{tile} weights[objective][action][tile])
    protected int adaptiveObjectiveSelection(double[][] Qs, double[][][] weights){
        
        //Will store best and worst Q-value per objective
        double[] best = new double[nrObjectives];
        double[] worst = new double[nrObjectives];
        
        //Will store best and worst actions per objective (multiple actions can have same Q-value)
        ArrayList<ArrayList<Integer>> ibest = new ArrayList<ArrayList<Integer>>();
        ArrayList<ArrayList<Integer>> iworst = new ArrayList<ArrayList<Integer>>();
        
        //initialize
        for(int o=0; o<nrObjectives; o++){
            best[o] = -Double.MAX_VALUE;
            worst[o] = Double.MAX_VALUE;
            ibest.add(new ArrayList<Integer>());
            iworst.add(new ArrayList<Integer>());
        }
        
        //For every action
        for(int i=0; i<Qs[0].length; i++){
            //For every objective
            for(int o=0; o<nrObjectives; o++){
                //If better than current best ....
                if(Qs[o][i] >= best[o]){
                    if(Qs[o][i] > best[o]){
                        ibest.get(o).clear();
                    }
                    ibest.get(o).add(i);
                    best[o] = Qs[o][i];
                }
                //If worse than current worst ....
                if(Qs[o][i] <= worst[o]){
                    if(Qs[o][i] < worst[o]){
                        iworst.get(o).clear();
                    }
                    iworst.get(o).add(i);
                    worst[o] = Qs[o][i];
                }
            }
        }
        
        //For each objective, randomly select a best and worst representative 
        //among the equally good best and worst actions
        int[] b = new int[nrObjectives];
        int[] w = new int[nrObjectives];
        for(int o=0; o<nrObjectives; o++){
            b[o] = ibest.get(o).get(RNG.randomInt(ibest.get(o).size()));
            w[o] = iworst.get(o).get(RNG.randomInt(iworst.get(o).size()));
        }
        
        //Adaptive objective selection
        if(type == AgentType.AOS){
            TTest test = new TTest();
            
            //Store a p-value for each objective, indicating confidence 
            //in that objective (lower p = higher confidence)
            double[] p = new double[nrObjectives];
            double bestp = Double.MAX_VALUE;
            ArrayList<Integer> ibestp = new ArrayList<Integer>();
            
            //For each objective
            for(int o=0; o<nrObjectives; o++){
                //Test confidence in that objective by applying a paired t-test
                //to the sets of weights of the best and worst action according
                //to that objective
                p[o] = test.pairedTTest(weights[o][b[o]], weights[o][w[o]]);
                
                //When weights are completely the same, the test returns null,
                //catch that and set to 1 (lowest confidence)
                if(Double.isNaN(p[o])){
                    p[o] = 1;
                }
                
                //Keep track of objective with highest confidence (lowest p)
                if(p[o] <= bestp){
                    if(p[o] < bestp){
                        ibestp.clear();
                    }
                    bestp = p[o];
                    ibestp.add(o);
                }
            }
            
            //Select a random objective among those with lowest p
            int tmp = ibestp.get(RNG.randomInt(ibestp.size()));
            
            //If necessary (in case of Q(lambda) e.g.) indicate that an off-policy
            //action will be taken wrt to an objective. Eligibility traces 
            //should be reset
            for(int o=0; o<nrObjectives; o++){
                boolean included = false;
                for(int i=0; i<ibest.get(o).size(); i++){
                    if(b[tmp] == ibest.get(o).get(i).intValue()){
                        included = true;
                    }
                }
                if(!included){
                    offpolicyAction(o);
                }
            }
            
            //Return the best action according to the most confident objective
            return b[tmp];
        // Random objective selection
        } else {
            //Randomly select an objective
            int tmp = RNG.randomInt(nrObjectives);
            
            //If necessary (in case of Q(lambda) e.g.) indicate that an off-policy
            //action will be taken wrt to an objective. Eligibility traces 
            //should be reset
            for(int o=0; o<nrObjectives; o++){
                boolean included = false;
                for(int i=0; i<ibest.get(o).size(); i++){
                    if(b[tmp] == ibest.get(o).get(i).intValue()){
                        included = true;
                    }
                }
                if(!included){
                    offpolicyAction(o);
                }
            }
            return b[tmp];
        }
    }
    
    
    /**
     * 这里就是直接选出最好的action，对应就是最大的Q值
     * @param Qs 每个action对应的最大Q值
     * @return
     */
    protected int actionSelection(double[][] Qs){
        double best = -Double.MAX_VALUE;
        ArrayList<Integer> ibest = new ArrayList <Integer>();
        
        for(int i=0; i<Qs[0].length; i++){
            if(Qs[0][i] >= best){
                if(Qs[0][i] > best){
                    ibest.clear();
                }
                ibest.add(i);
                best = Qs[0][i];
            }
        }
        
        int b = ibest.get(RNG.randomInt(ibest.size()));   //如果有多个最优值，则选取一个action
        return b;
    }
    
    public abstract int act();
    
    public double scalarizedShaping(){
        double scal = 0.0;
        for(int i=0; i<nrObjectives; i++){
            scal += ws[i]*shaping(objectivesToUse[i]);
        }
        return scal;
    }
    
    public abstract void reward(double reward);
    
    protected int[] tileCoding(double[] state, int action) {
        int extra1 = action;
        int extra2 = -1;
        int extra3 = -1;
        
        return TileCoding.GetTiles(nrTiles, state, maxNrTiles, extra1, extra2, extra3);
    }
}
