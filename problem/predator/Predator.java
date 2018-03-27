/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package problem.predator;

import java.util.List;

import problem.learning.AgentType;

/**
 *
 * @author timbrys
 */
public class Predator extends Animal{
    
    private PredatorWorld pw;
    private int size;
    public Predator(PredatorWorld pw, AgentType type, int[] objectives, int size, int x, int y){
        super(pw, type, objectives, x, y);
        predator = true;
        this.size = size;
        this.pw = pw;
        
        maxNrTiles = 4096;
        nrTiles = 32;
        
        alpha = 0.1 / nrTiles;
        if(type == AgentType.Random){
            epsilon = 1.0;
        } else {
            epsilon = 0.1;
        }
        
        gamma = 0.9;
        lambda = 0.9;
        initialize();
    }
    
    public int getStateSize(){
        return 4;
    }
    
    //relative x and y positions of the other predator and the prey
    //tile width of 10
    public double[] getState(){
        Animal[] predators = pw.getPredators();   //获取所有的捕食者
        Animal[] preys = pw.getPreys();  //获得所有的猎物
        double[] state = new double[4]; // state有4个元素，分别是与另一个捕食者的x/y距离，还有和猎物的x/y距离，不过注意如果存在>=3的捕食者就不行了
        for(int i=0; i<predators.length; i++){
            if(predators[i] != this){  
                state[0] = (x-predators[i].x)/10.0;
                state[1] = (y-predators[i].y)/10.0;
            }
        }
        
        state[2] = (x-preys[0].x)/10.0;
        state[3] = (y-preys[0].y)/10.0;
//        System.out.println("state:"+state[0]+state[1]+state[2]+state[3]+" size:"+state.length);
        return state;
    }
    
    //Euclidean distance
    public double distance(Animal a, Animal b){
        return Math.sqrt((a.x-b.x)*(a.x-b.x) + (a.y-b.y)*(a.y-b.y));
    }
    
    //calculates either of the three shapings
    public double shaping(int s){
        Animal[] predators = pw.getPredators();
        Animal[] preys = pw.getPreys();
        if(s == 1){
            //proximity
            return distanceToPrey(preys);
        } else if(s == 2){
            //angle
            return angleWithPartner(predators, preys);
        } else if(s == 3){
            //separation
            return distanceToPartner(predators);
        }
        return 0.0;
    }
    
    //normalizing constants for the shapings
    public double shapingNormalization(int s){
        if(s == 1 || s == 3){
            return size*2;
        } else if(s == 2){
            return Math.PI;
        }
        throw new ArrayIndexOutOfBoundsException("Illegal shaping function");
    }
    
    public double angleWithPartner(Animal[] predators, Animal[] preys){
        Animal partner = null;
        for(int i=0; i<predators.length; i++){
            if(predators[i] != this){
                partner = predators[i];
            }
        }
        
        //cos-1((P12^2 + P13^2 - P23^2)/(2 * P12 * P13))
        double dist12 = distance(preys[0], this);
        double dist13 = distance(preys[0], partner);
        double dist23 = distance(this, partner);
        //prey = 1, me = 2, partner = 3
        if(dist12 == 0 || dist13 == 0){
            return 1.0;
//            return Math.PI;
        } else {
            double sum = (dist12*dist12 + dist13*dist13 - dist23*dist23)/(2*dist12*dist13);
            if(sum > 1){
                sum = 1.0;
            } else if(sum < -1){
                sum = -1.0;
            }
            return (Math.acos(sum)/Math.PI);
//            return (Math.acos(sum));
        }
    }
    
    public double distanceToPrey(Animal[] preys){
        return (size*2.0 - (Math.abs(x-preys[0].x) + Math.abs(y-preys[0].y)))/(size*2.0);
//        return (size*2.0 - (Math.abs(x-preys[0].x) + Math.abs(y-preys[0].y)));
    }
    
    public double distanceToPartner(Animal[] predators){
        Animal partner = null;
        for(int i=0; i<predators.length; i++){
            if(predators[i] != this){
                partner = predators[i];
            }
        }
        return (Math.abs(x-partner.x) + Math.abs(y-partner.y))/(size*2.0);
//        return (Math.abs(x-partner.x) + Math.abs(y-partner.y));
    }

    
	@Override
	public int numberVisits(double[] state) {
		// TODO Auto-generated method stub
		return -1;
	}

	@Override
	public boolean checkAsk(double[] state) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int checkAdvise(double[] state, double[][] Qs) {
		// TODO Auto-generated method stub
        System.out.println("predator checkAdvise");

		return -1;
	}

	@Override
	public int adviseAction(double[] state, double[][] Qs, int adviseeAction) {
		// TODO Auto-generated method stub
		return -1;
	}

	@Override
	public int combineAdvice(List<Integer> advised) {
		// TODO Auto-generated method stub
		return -1;
	}

	@Override
	public void setupAdvising(int agentIndex, Animal[] allAgents) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resetAdvisedStates() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getUsedBudget() {
		// TODO Auto-generated method stub
		return 0;
	}
}
