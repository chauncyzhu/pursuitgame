/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package problem.predator;

import java.util.Arrays;
import problem.RNG;
import problem.learning.AgentType;
import problem.learning.Problem;

/**
 *
 * @author timbrys
 */
public class PredatorWorld extends Problem{
    private int size;
    private int nrPredators;
    private Animal[][] map;
    private Animal[] aPredators;
    private Animal[] aPreys;
    
    /**
     * 构造一个环境
     * @param size 环境的大小，注意这是一个grid world
     * @param nrPredators 捕食者的个数
     * @param type 捕食者的类型
     * @param objectives 
     */
    public PredatorWorld(int size, int nrPredators, AgentType type, int[] objectives){
    	
        this.size = size;
        this.nrPredators = nrPredators;
        
        this.map = new Animal[size][size];  //为什么可以直接这样子new？
        
        this.aPredators = new Animal[nrPredators];  //每个捕食者都是一个Animal，也就是一个agent
        this.aPreys = new Animal[nrPredators-1];   //猎物的个数总是比捕食者少一个吗？也是Q learning agent？
        for(int i=0; i<nrPredators; i++){
            if(i < nrPredators-1){
                Animal a;
                do{
                	//创建一个非空坐标的猎物，同时默认是agent为Q learning agent，同时仅接受环境的reward
                    a = new Prey(this, size, RNG.randomInt(size), RNG.randomInt(size));
                } while (occupied(a));
                this.aPreys[i] = a;               
                this.map[a.y][a.x] = a; //并放到map中
            }
            Animal a;
            do{
            	//创建捕食者，agent的reward类型由外部指定，
                a = new AdhocTD(this, type, objectives, size, RNG.randomInt(size), RNG.randomInt(size));
            } while (occupied(a));
            
            this.aPredators[i] = a;
            this.map[a.y][a.x] = a;
        }
        
        //设置agent的队友
        for(int i=0; i<nrPredators; i++){
        	this.aPredators[i].setupAdvising(i, this.aPredators);
        }
        
    }
    
    public int getSize(){
        return size;
    }
    
    //reinitializes the world, randomly placing the predators and prey
    public void reset(){
        this.map = new Animal[size][size];
        for(int i=0; i<nrPredators; i++){
            if(i < nrPredators-1){
                aPreys[i].x = -1;
                aPreys[i].y = -1;
            }
            aPredators[i].x = -1;
            aPredators[i].y = -1;
            ((Predator)aPredators[i]).resetEs();
        }
        for(int i=0; i<nrPredators; i++){
            if(i < nrPredators-1){
                do{
                    aPreys[i].x = RNG.randomInt(size);
                    aPreys[i].y = RNG.randomInt(size);
                } while (occupied(aPreys[i]));
                this.map[aPreys[i].y][aPreys[i].x] = aPreys[i];
            }
            do{
                aPredators[i].x = RNG.randomInt(size);
                aPredators[i].y = RNG.randomInt(size);
            } while (occupied(aPredators[i]));
            this.map[aPredators[i].y][aPredators[i].x] = aPredators[i];
            
            //将Predators的advisedStates重新设置
            aPredators[i].resetAdvisedStates();
        }
              
    }
    
    public boolean occupied(Animal a){
        return map[a.y][a.x] != null;
    }
    
    //moves a predator or prey, checking whether 
    public void move(Animal a){ 
        int x = a.x;
        int y = a.y;
        this.map[a.y][a.x] = null;
        int dir = a.act();
        
        switch(dir){
            case 0: //UP
                if(a.y > 0){
                    a.y--;
                }
                break;
            case 1: //DOWN
                if(a.y < size-1){
                    a.y++;
                }
                break;
            case 2: //LEFT
                if(a.x > 0){
                    a.x--;
                }
                break;
            case 3: //RIGHT
                if(a.x < size-1){
                    a.x++;
                }
                break;
        }
        //if available, move onto location
        if(this.map[a.y][a.x] == null){  //如果这里没有agent则可以移动
            this.map[a.y][a.x] = a;
            if(a.predator){  //判断是否为捕食者
                a.reward(0);
            }
        //if this is a predator, and the next location holds a prey, move onto location
        } else if(!this.map[a.y][a.x].predator && a.predator){  // 如果这里存在agent，同时不是猎人而a是猎人
            this.map[a.y][a.x] = a;
        } else {
            //otherwise, do not move predator，也就是如果这里是猎人，则不要移动
            a.y = y;
            a.x = x;
            this.map[a.y][a.x] = a;
            if(a.predator){
                a.reward(0);
            }
        }
    }
    
    public Animal[] getPredators(){
        return aPredators;
    }
    
    public Animal[] getPreys(){
        return aPreys;
    }

    @Override
    public void update() {
        for(int i=0; i<aPreys.length; i++){
            move(aPreys[i]);   //移动猎物
        }
        for(int i=0; i<aPredators.length; i++){
            move(aPredators[i]);   //移动捕食者
        }
    }
    
    public double episode(){
        int iteration = 0;
        while(!isGoalReached() && iteration < 5000){
            update();   //更新环境
            iteration++;
        }
        //if the prey was caught, reward the predators with a 1
        for(int i=0; i<aPredators.length; i++){
            if(isGoalReached()){
                aPredators[i].reward(1);
            }
            
            //取出消耗的budget
            System.out.print(aPredators[i].getUsedBudget()+"--");
        }
        System.out.println();
        return iteration;
    }
    
    //check if the prey is caught，同时应该调用clear方法
    public boolean isGoalReached(){
        for(int i=0; i<aPredators.length; i++){
            for(int j=0; j<aPreys.length; j++){
                if(aPredators[i].x == aPreys[j].x && aPredators[i].y == aPreys[j].y){  //如果发现有prey被捉住了
                    return true;
                }
            }
        }
        return false;
    }
    
    //print world
    public String toString(){
        String s = "";
        for(int i=0; i<size; i++){
            for(int j=0; j<size; j++){
                boolean set = false;
                for(int l=0; l<aPredators.length; l++){
                    if(aPredators[l] != null && aPredators[l].x == j && aPredators[l].y == i){
                        s += "x";
                        set = true;
                        break;
                    }
                    if(l < aPreys.length && aPreys[l] != null && aPreys[l].x == j && aPreys[l].y == i){
                        s += "O";
                        set = true;
                        break;
                    }
                }
                if(!set)
                    s += " ";
            }
            s += "\n";
        }
        return s;
    }
    
    @Override
    public int getNumActions() {
        return 5;
    }
    
}
