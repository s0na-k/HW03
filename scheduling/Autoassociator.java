import java.util.Random;

public class Autoassociator {
    private int weights[][];
    private int trainingCapacity;

    public Autoassociator(CourseArray courses) {
        // Initialize weights matrix with size equal to the number of courses
        int numCourses = courses.length();
        weights = new int[numCourses][numCourses];

        // Randomly initialize weights
        Random random = new Random();
        for (int i = 0; i < numCourses; i++) {
            for (int j = 0; j < numCourses; j++) {
                if (i != j) {
                    weights[i][j] = random.nextBoolean() ? 1 : -1;
                }
				
            }
        }

        // Set training capacity
        trainingCapacity = numCourses * (numCourses - 1) / 2;
    }

    public int getTrainingCapacity() {
        return trainingCapacity;
    }

    public void training(int pattern[]) {
        // Update weights based on the input pattern
        for (int i = 0; i < pattern.length; i++) {
            for (int j = 0; j < pattern.length; j++) {
                if (i != j) {
                    weights[i][j] += pattern[i] * pattern[j];
                }
            }
        }
    }

    public int unitUpdate(int neurons[]) {
        // Choose a random neuron to update
        Random random = new Random();
        int index = random.nextInt(neurons.length);
        int activation = 0;

        // Update the selected neuron
        for (int i = 0; i < neurons.length; i++) {
            activation += weights[index][i] * neurons[i];
        }
        neurons[index] = activation >= 0 ? 1 : -1;

        return index;
    }

    public void unitUpdate(int neurons[], int index) {
        // Update a single neuron specified by index
        int activation = 0;
        for (int i = 0; i < neurons.length; i++) {
            activation += weights[index][i] * neurons[i];
        }
        neurons[index] = activation >= 0 ? 1 : -1;
    }

	//
    public void chainUpdate(int neurons[], int steps) {
        // Update the specified number of steps
        for (int i = 0; i < steps; i++) {
            int index = unitUpdate(neurons);
            unitUpdate(neurons, index);
        }
    }

    public void fullUpdate(int neurons[]) {
        // Keep updating until the final state is achieved
        int[] previousNeurons = new int[neurons.length];
        do {
            System.arraycopy(neurons, 0, previousNeurons, 0, neurons.length);
            chainUpdate(neurons, neurons.length);
        } while (!hasConverged(neurons, previousNeurons));
    }

    private boolean hasConverged(int[] neurons1, int[] neurons2) {
        // Check if two neuron states are the same
        for (int i = 0; i < neurons1.length; i++) {
            if (neurons1[i] != neurons2[i]) {
                return false;
            }
        }
        return true;
    }
}
//noe object enq sarqum u ira vra train anum ()
//courseArray u H-network y inchpes en pokhkapalcvac
//course array y ogtagorcum enq ira iterate methodi hamar
//train aneluc heto anelu enq update(orinak unit update methody )

//vercnum enq timeslot eric meky u update enq anum, instance i updateenq kanchum timesloty tramadrelov, 
//stanum enq timeslot-i updated vichky vortegh mi vandakna update eghel

