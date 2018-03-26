package problem.predator;

import java.util.*;

public class AdviceUtil {
	private List<Animal> advisors = null;
	
	public void setupAdvisors(List<Animal> advisors){
		this.advisors = advisors;
	}
	
	public List<Integer> askAdvice(double[] state, double[][] Qs, int action){
		List<Integer> advice = new ArrayList<>();
		
		for(Animal ad:advisors){
			int a = ad.adviseAction(state, Qs, action);
//			System.out.println("adviseaction:"+a);
			if(a != -1){
				advice.add(a);
			}
		}
		return advice;
	}
		

}
