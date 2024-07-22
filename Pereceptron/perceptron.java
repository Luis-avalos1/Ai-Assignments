import java.io.*;
import java.util.*;

public class perceptron {
    // weights, bias , learning rate
    private final double[] weights;
    private double bias;
    private final double learningRateForTraining;

    // constructor
    public perceptron(int inputSize, double learningRate) {
        this.weights = new double[inputSize];
        this.bias = 0.0;
        this.learningRateForTraining = learningRate;
        Arrays.fill(this.weights, 0);
    }

    // method for calculating the sum of weights && the bias
    private int prediction(int[] inputs) {

        double sumOfWeights = bias;
        for (int i = 0; i < weights.length; i++) 
        {
            sumOfWeights += weights[i] * inputs[i];
        }
        // bright or dim ???
        return sumOfWeights > 0 ? 1 : -1;
    }

    // trains perceptron using the provided data
    public void trainPerceptron(int[][] trainingData, int[] labels, int epochsData) {

        // iteratively adjusting the weights and bias based of the provided training data ..
        // over a num of epochs. 
        for (int currentEpoch = 0; currentEpoch < epochsData; currentEpoch++) 
        {

            for (int i = 0; i < trainingData.length; i++) 
            {
                int prediction = prediction(trainingData[i]);
                int error = labels[i] - prediction;
                
                for (int j = 0; j < weights.length; j++) 
                {
                    weights[j] += learningRateForTraining * error * trainingData[i][j];
                }

                bias += learningRateForTraining * error;
            }
            System.out.println("epoch" + (currentEpoch + 1) + ":");
        }
    }

    // evaluate trained model --> classify each instance of test data
    public void evaluateTrainedPerceptron(int[][] testingData, int[] dataLabels) {
        // variables
        int truthPos = 0;
        int truthNeg = 0;
        int falsePos = 0;
        int falseNeg = 0;
        int correctTruthCount = 0;

        for (int i = 0; i < testingData.length; i++) 
        {
            // predict to classify each instance --> assign classifciation 
            int prediction = prediction(testingData[i]);
            String classification = prediction == 1 ? "Bright" : "Dim";
            System.out.println(Arrays.toString(testingData[i]) + ", " + classification);

            // Update confusion matrix and value needed for accuarcy count
            if (dataLabels[i] == 1 && prediction == 1) 
            {
                truthPos++;
                correctTruthCount++;
            } 
            else if (dataLabels[i] == -1 && prediction == -1) 
            {
                truthNeg++;
                correctTruthCount++;
            } 
            else if (dataLabels[i] == 1 && prediction == -1) 
            {
                falseNeg++;
            } 
        
            else if (dataLabels[i] == -1 && prediction == 1) 
            {
            falsePos++;
            }
    }

    // required Ouput: print and calculate the accuracy
    double accuracyPercentage = 100.0 * correctTruthCount / testingData.length;
    System.out.println("\n"+testingData.length + " test samples evaluated, Result:");
    System.out.printf("Accuracy: %.2f%%\n", accuracyPercentage);

    // required Ouput: Confucion Matrix --> classified +/-
    System.out.println("\nConfusion Matrix:");
    System.out.println("True Positive (TP) = " + truthPos);
    System.out.println("False Negative (FN) = " + falseNeg);
    System.out.println("False Positive (FP) = " + falsePos);
    System.out.println("True Negative (TN) = " + truthNeg);

    }

    public static void main(String[] args) throws IOException {
        // we want an input size of 4 with a learning rate of .1
        perceptron perceptronObject = new perceptron(4, 0.1);

        // load training data from proivded csv files
        int[][] trainingData = loadDataFromCSV("train.csv", true);
        int[] labels = loadLabelsFromCSV("train.csv");

        // after loading --> train perceptron
        perceptronObject.trainPerceptron(trainingData, labels, 35);

        // load test data from provided csv files
        int[][] testData = loadDataFromCSV("test_with_truth.csv", true);
        int[] testLabels = loadLabelsFromCSV("test_with_truth.csv");

        // evaluate 'results'
        perceptronObject.evaluateTrainedPerceptron(testData, testLabels);
    }

    private static int[][] loadDataFromCSV(String filename, boolean hasLabel) throws IOException {
        List<int[]> dataList = new ArrayList<>();

        // read file
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filename))) 
        {
            // while line is not empty
            String line;
            while ((line = bufferedReader.readLine()) != null) 
            {
                String[] elements = line.split(",");
                int length = hasLabel ? elements.length - 1 : elements.length;
                int[] data = new int[length];

                // if int add into array
                for (int i = 0; i < length; i++) 
                {
                    data[i] = Integer.parseInt(elements[i].trim());
                }
                dataList.add(data);
            }
        }
        // return data
        return dataList.toArray(new int[0][0]);
    }

    private static int[] loadLabelsFromCSV(String filename) throws IOException {
        List<Integer> labels = new ArrayList<>();
        // read in file
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filename))) 
        {
            // while not empty
            String currentLine;
            while ((currentLine = bufferedReader.readLine()) != null) 
            {
                String[] elements = currentLine.split(",");
                String label = elements[elements.length - 1].trim();
                // brigjht or dim
                labels.add(label.equalsIgnoreCase("bright") ? 1 : -1);
            }
        }
        // return labes from csv
        return labels.stream().mapToInt(i -> i).toArray();
    }
}
