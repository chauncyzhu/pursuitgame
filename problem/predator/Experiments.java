/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package problem.predator;

import java.util.Arrays;
import problem.learning.AgentType;

/**
 *
 * @author timbrys
 */
public class Experiments {
    
    public static void main(String args[]){
        //run experiments with one of the possible variants
//        switch(new Integer(args[0])){
        switch(0){
            //���Ϊ��[2644.514, 2845.798, 2806.857, 2656.19, 2548.827, 2464.488, 2352.718, 2117.338, 2069.386, 1922.768, 1860.294, 1763.57, 1640.254, 1581.218, 1620.273, 1532.114, 1409.789, 1465.457, 1372.971, 1292.47, 1244.02, 1184.268, 1203.776, 1093.257, 1083.088, 1078.311, 1019.184, 1008.236, 999.901, 913.003, 859.397, 911.136, 843.039, 879.087, 814.784, 772.98, 845.056, 811.87, 764.336, 751.91, 829.372, 754.263, 655.436, 743.635, 700.525, 740.154, 717.917, 693.894, 643.509, 679.491, 665.736, 664.948, 619.216, 619.986, 598.144, 661.565, 642.53, 645.144, 588.611, 567.656, 539.171, 535.177, 572.912, 534.072, 531.648, 505.141, 532.088, 570.314, 539.821, 546.09, 523.786, 526.87, 510.185, 517.722, 480.368, 510.147, 475.493, 509.81, 461.938, 456.239, 471.498, 476.253, 460.376, 457.679, 462.166, 443.103, 435.87, 440.434, 453.748, 446.587, 442.252, 444.005, 409.707, 421.055, 435.274, 400.002, 434.337, 419.631, 390.56, 417.541, 411.089, 414.909, 411.017, 389.206, 399.021, 399.042, 384.52, 426.963, 402.696, 398.574, 371.48, 370.989, 400.82, 373.082, 368.123, 356.214, 386.912, 385.391, 397.438, 368.71, 364.355, 361.339, 359.305, 359.935, 389.326, 358.81, 353.403, 355.386, 343.978, 317.241, 355.134, 328.115, 338.062, 343.05, 358.418, 340.057, 328.653, 348.385, 319.319, 320.387, 338.805, 320.546, 354.147, 352.695, 330.019, 331.933, 311.979, 319.525, 320.87, 329.448, 337.327, 349.02, 305.146, 300.831, 315.132, 299.419, 311.93, 283.389, 324.197, 337.137, 319.229, 292.051, 297.396, 295.683, 299.78, 305.763, 324.75, 313.967, 288.227, 303.839, 287.895, 297.024, 267.239, 276.519, 275.881, 297.056, 256.518, 265.34, 297.135, 278.321, 279.651, 286.516, 291.687, 298.686, 256.195, 291.299, 266.68, 275.32, 285.048, 288.376, 311.723, 271.176, 276.445, 266.277, 251.898, 285.998, 254.712, 280.153, 281.944, 240.498, 258.633, 260.48, 289.188, 272.2, 278.83, 276.401, 262.823, 263.127, 274.984, 267.437, 264.1, 253.475, 247.49, 248.157, 257.73, 242.465, 248.846, 266.704, 271.747, 253.911, 254.08, 256.8, 263.287, 254.682, 263.94, 245.409, 250.543, 252.123, 235.591, 258.517, 241.745, 231.691, 240.622, 263.743, 236.039, 234.115, 251.743, 262.221, 262.73, 255.998, 229.912, 244.509, 241.627, 243.773, 250.548, 225.664, 246.144, 232.408, 254.601, 237.314, 243.421, 228.587, 238.459, 246.553, 254.28, 239.626, 248.108, 226.457, 248.58, 221.459, 216.153, 246.381, 228.398, 239.55, 251.33, 212.672, 217.318, 230.262, 232.894, 230.709, 239.159, 229.647, 230.882, 229.403, 225.257, 234.117, 227.148, 215.17, 214.675, 232.36, 216.339, 216.211, 224.404, 215.615, 209.52, 245.668, 224.31, 222.257, 233.252, 222.626, 218.295, 222.997, 222.028, 223.639, 225.599, 222.039, 233.634, 217.347, 213.59, 204.087, 213.079, 227.905, 219.999, 202.437, 216.589, 230.549, 214.173, 187.174, 225.433, 193.375, 225.257, 199.212, 224.468, 206.474, 206.104, 224.449, 209.09, 209.667, 196.847, 201.21, 213.949, 219.657, 206.363, 209.525, 202.754, 212.124, 200.188, 203.438, 213.878, 210.259, 223.58, 203.885, 197.411, 203.079, 195.663, 187.142, 211.567, 187.32, 208.177, 204.225, 201.537, 194.536, 199.099, 212.889, 204.481, 203.264, 204.509, 199.721, 205.359, 204.733, 195.679, 201.637, 193.803, 186.018, 198.14, 193.731, 185.706, 188.344, 198.526, 188.079, 202.349, 184.9, 199.201, 215.378, 180.13, 199.675, 181.701, 191.289, 182.064, 206.716, 203.161, 200.486, 198.535, 194.921, 192.263, 197.46, 203.084, 184.922, 197.697, 203.183, 181.877, 180.783, 187.9, 197.007, 189.814, 187.411, 203.13, 180.566, 186.108, 179.502, 178.478, 186.677, 190.1, 194.467, 192.117, 194.059, 175.396, 210.429, 192.525, 188.667, 186.666, 187.363, 170.841, 181.685, 172.542, 182.136, 187.374, 202.03, 197.651, 178.878, 174.521, 181.885, 181.673, 188.56, 179.619, 170.125, 183.894, 170.425, 181.053, 171.78, 193.027, 195.358, 183.772, 171.501, 184.948, 190.017, 162.421, 193.982, 161.146, 167.0, 185.728, 181.995, 180.411, 178.052, 190.859, 172.957, 174.658, 171.389, 190.815, 189.207, 172.397, 169.736, 173.423, 180.35, 175.445, 179.4, 168.283, 175.974, 179.696, 170.516, 201.702, 169.456, 180.405, 176.694, 180.841, 177.055, 163.118, 175.248, 176.954, 175.576, 176.986, 169.685, 174.996, 174.784, 164.379, 176.222, 177.612, 172.345, 171.519, 171.01, 170.494, 167.711, 170.855, 173.615, 173.89, 168.191, 180.84, 178.051, 165.066, 167.053, 173.226, 167.379, 167.792, 163.866, 176.544, 184.197, 171.464, 161.91, 159.732, 164.886, 159.346, 165.488, 171.004, 173.364, 173.809, 187.61, 178.431, 169.49, 175.67, 164.906, 171.078, 167.804, 165.292, 163.312, 168.529, 153.598, 156.38, 162.084, 160.5, 163.338, 166.412, 162.478, 159.795, 167.377, 164.906, 160.119, 158.887, 172.667, 171.011, 159.267, 171.521, 155.367, 163.259, 162.702, 169.139, 155.941, 153.695, 157.1, 164.711, 155.644, 160.933, 161.978, 162.425, 162.113, 149.324, 163.969, 164.267, 151.758, 175.294, 152.775, 160.55, 157.888, 166.429, 158.335, 159.188, 160.539, 171.671, 162.303, 150.918, 155.39, 159.649, 155.445, 165.182, 166.079, 159.175, 158.445, 163.026, 165.098, 151.148, 146.935, 162.232, 168.453, 156.801, 142.993, 153.899, 155.44, 153.191, 145.125, 157.634, 153.894, 163.487, 154.277, 150.177, 161.98, 155.639, 152.633, 146.251, 154.303, 156.321, 151.658, 155.504, 148.455, 165.428, 157.535, 153.754, 162.459, 148.371, 143.51, 157.942, 159.81, 158.681, 156.717, 164.404, 152.532, 150.334, 150.098, 141.483, 157.112, 152.453, 152.264, 152.817, 150.331, 149.836, 152.087, 151.446, 146.51, 152.403, 146.799, 159.672, 151.396, 146.485, 149.493, 138.536, 143.39, 145.701, 146.879, 165.696, 145.035, 150.681, 152.275, 141.544, 154.299, 150.117, 153.503, 148.349, 133.939, 146.588, 136.515, 150.825, 143.917, 145.164, 146.238, 148.033, 151.456, 149.654, 157.1, 145.27, 153.166, 138.983, 155.34, 136.599, 147.213, 147.638, 150.449, 149.249, 136.075, 146.973, 145.659, 140.521, 144.346, 140.838, 154.439, 160.1, 156.172, 137.295, 133.37, 143.247, 162.733, 140.795, 151.589, 150.912, 133.093, 141.935, 139.652, 157.563, 148.197, 148.01, 140.442, 145.36, 152.054, 141.002, 142.202, 138.072, 141.857, 145.977, 149.025, 155.113, 138.83, 144.37, 140.502, 142.048, 147.572, 146.129, 150.213, 145.853, 143.307, 144.968, 151.706, 141.173, 139.602, 140.13, 133.009, 148.065, 154.805, 138.779, 139.367, 130.36, 149.387, 138.049, 130.16, 154.055, 137.607, 147.456, 133.483, 136.216, 153.921, 157.653, 144.763, 149.937, 128.238, 148.433, 147.765, 146.997, 134.472, 140.433, 133.897, 138.844, 132.659, 156.427, 148.034, 137.119, 137.58, 148.068, 137.659, 131.76, 137.797, 140.118, 141.229, 145.245, 136.218, 142.191, 141.799, 135.703, 156.766, 139.171, 140.304, 132.104, 135.12, 139.472, 143.905, 137.384, 143.715, 131.028, 134.327, 122.348, 129.132, 143.294, 138.409, 136.311, 138.83, 135.711, 137.706, 120.875, 140.675, 132.729, 150.042, 133.586, 131.677, 133.527, 144.195, 130.302, 134.159, 143.559, 143.427, 138.613, 139.523, 135.929, 128.514, 133.594, 129.623, 124.948, 144.809, 131.424, 128.601, 132.661, 135.518, 130.272, 142.653, 134.227, 145.917, 141.122, 145.838, 130.563, 131.864, 132.451, 141.02, 128.96, 133.032, 139.076, 135.901, 135.871, 134.713, 146.103, 136.676, 139.103, 128.869, 147.757, 126.858, 129.156, 130.211, 129.965, 139.829, 121.337, 129.687, 141.557, 128.17, 131.116, 130.902, 136.034, 120.482, 134.412, 129.605, 136.974, 141.521, 132.9, 138.206, 143.243, 136.757, 136.028, 126.537, 131.475, 135.783, 119.5, 139.844, 138.095, 127.742, 127.504, 141.263, 129.554, 129.602, 131.964, 127.342, 127.931, 132.939, 137.118, 144.029, 122.962, 133.465, 130.633, 135.708, 129.976, 124.45, 133.758, 137.761, 126.03, 130.321, 125.819, 131.761, 124.65, 125.287, 137.889, 120.262, 147.156, 129.18, 126.649, 132.265, 137.478, 123.543, 125.137, 124.196, 114.322, 131.895, 132.404, 118.565, 133.43, 132.993, 135.721, 134.309, 131.243, 124.354, 125.92, 129.692, 118.675, 138.525, 126.387, 124.174, 132.344, 131.929, 135.813, 129.304, 120.848, 133.31, 136.511, 129.199, 133.751, 130.306, 130.221, 123.491, 128.202, 124.757, 130.543, 126.504, 130.595, 127.655, 129.869, 121.044, 124.035, 118.838, 129.579, 134.246, 117.209, 126.466, 124.689, 128.036, 125.666, 129.872, 120.721, 120.926, 121.462, 129.866, 126.743, 122.278, 136.333, 130.449, 125.237, 125.34, 138.582, 135.825, 118.826, 120.685, 124.799, 118.701, 126.21, 125.921, 124.449, 120.373, 117.346, 124.612, 133.567, 126.897, 123.948, 122.013, 122.591, 124.385, 133.742, 130.628, 122.679, 127.303, 120.403, 117.242, 114.227, 122.108, 114.524, 128.803, 127.657, 110.364, 122.514, 131.789, 130.464, 123.732, 121.749, 126.381, 116.729, 122.006, 136.475, 116.801, 117.685, 124.389, 125.672, 109.058, 118.621, 120.385, 118.914, 124.567, 125.287, 116.783, 120.969, 122.497, 112.62, 127.457, 122.915, 113.109, 118.964, 117.919, 117.696, 121.575, 128.366, 116.177, 127.661, 130.33, 125.659, 123.533, 120.168, 129.649, 130.382, 124.547, 126.194, 109.306, 128.943, 126.522, 131.407, 112.888, 115.068, 116.5, 121.591, 113.177, 122.369, 119.066, 121.244, 130.333]
            case 0: typeExperiment(AgentType.NoShaping, new int[]{0});
                break;
            case 1: typeExperiment(AgentType.SingleShaping, new int[]{1});
                break;
            case 2: typeExperiment(AgentType.SingleShaping, new int[]{2});
                break;
            case 3: typeExperiment(AgentType.SingleShaping, new int[]{3});
                break;
                
            case 4: typeExperiment(AgentType.Linear, new int[]{1,2,3});
                break;
            case 5: typeExperiment(AgentType.BestLinear, new int[]{1,2,3});
                break;
            case 6: typeExperiment(AgentType.ROS, new int[]{1,2,3});
                break;
            case 7: typeExperiment(AgentType.AOS, new int[]{1,2,3});
                break;
        }
    }
    
    public static double typeExperiment(AgentType type, int[] objectives){
        int experiments = 1;
        int episodes = 1000;
        double[][] results = new double[experiments][episodes];
        
        // ʵ�����1��
        for(int ex=0; ex<experiments; ex++){
        	// �½�һ��������20*20������ͬʱĬ����������Ȳ�ʳ����1��
            PredatorWorld p = new PredatorWorld(40, 2, type, objectives);
            System.out.println("now experiment:"+ex);
            
            // ÿ��ʵ����1000��episode
            for(int ep=0; ep<episodes; ep++){
                p.reset();  //ÿ��episode����Ҫ�������û���
                results[ex][ep] = p.episode();
//                System.out.println("now episode:"+ep+"--result:"+results[ex][ep]);
            }
        }
        double[] means = means(results);
        return mean(means);
    }
    
    //averages the results of a number of runs
    public static double[] means(double[][] stats){
        double[] means = new double[stats[0].length];
        for(int j=0; j<stats[0].length; j++){
            for(int i=0; i<stats.length; i++){
                means[j] += stats[i][j];
            }
            means[j] = 1.0*means[j]/(stats.length);
        }
        return means;
    }
    
    public static double mean(double[] stats){
        double means = 0.0;
        for(int i=0; i<stats.length; i++){
            means += stats[i];
        }
        means = 1.0*means/(stats.length);
        return means;
    }
    
}