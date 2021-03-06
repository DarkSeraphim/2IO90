




import java.util.Optional;
import java.util.Set;

/**
 * @author DarkSeraphim.
 */
public class AnnealingAlgorithm implements IAlgorithm
{

    @Override
    public Solution computePoints(Set<Point> points, int width, int height)
    {
        Solution solution = getRandomSolution(width, height, points);
        double temperature = getAppropiateTemperature(points);
        double decreaseRate = getAppropiateDecreaseRate(temperature);
        double minTemperature = getAppropiateMinTempreature(temperature, decreaseRate);
        Solution bestSolution = solution;
        while (annualSchedule(temperature, decreaseRate, minTemperature)){
            Solution neighborSolution = getNeighborSolutions(solution);
            double probability = getProbability(neighborSolution.getQuality(), solution.getQuality(), temperature);

            if( checkIfAccepted(probability) ){
                solution = neighborSolution;
            }

            if(solution.getQuality() > bestSolution.getQuality()){
                bestSolution = solution;
            }

            temperature = temperature - decreaseRate;

        }

        return bestSolution;
    }

    //notes: the = sign should stand for clone not a reference

//TODO: uitzoeken wat een goed annealig schedule is. (temperature en decrease rate en waneer we stoppen)

    //generates a random solution
    Solution getRandomSolution(int width, int height, Set<Point> problem){
        Solution solution = new Solution(width, height);

        for(Point point : problem) {
            Optional<Point> p = point.getRandomFreeLabel(solution);
            if (p.isPresent())
            {
                solution.add(p.get());
            }
        }

        return solution;
    }

    double getAppropiateTemperature(Set<Point> problem){
        //one way is just starting with the problem size
        return problem.size();

    }

    double getAppropiateDecreaseRate(double temperature){
        return temperature * 0.01; //decrease with one procent

    }

    double getAppropiateMinTempreature(double temperature, double decreaseRate){
        return 1;
    }

    //returns if we are done with the algorithm according to the schedule
    boolean annualSchedule(double temperature, double decreaseRate, double minTemperature){
        temperature = temperature - decreaseRate;
        return temperature > minTemperature;
    }


    Solution getNeighborSolutions(Solution solution){
        Solution neighborSolution = new Solution(solution);

        //get a random point that has an option to change the label position
        Point p = solution.getRandomPoint();

        //change the current label with a random other label that has no conflicts
        Optional<Point> point = p.getMutation(solution);
        if (point.isPresent())
        {
            neighborSolution.change(p, point.get());
        }
        else
        {
            neighborSolution.remove(p);
        }

        return neighborSolution;
    }

    boolean checkIfAccepted(double probability){
        double acceptedChange = Math.random();

        if(probability >= acceptedChange){
            return true;
        } else{
            return false;
        }
    }

    //could be the case that we have to switch the "<" sign because of the way we meassure the quality
    double getProbability(double neighborQuality, double currentQuality, double temperature){
        double chance = 0;
        if(neighborQuality > currentQuality){
            chance = 1;
        } else {
            //expression:  chance = e^((neighborQuality - currentQuality) / temperature)
            chance = Math.exp((neighborQuality - currentQuality) / temperature);
        }

        return chance;
    }

//nog meer general notes:
//quality of the solution should be updated every time a solution changed:
//how to determine quality (objective function)
// #of deleted labels
// #of obstructed labels
}
