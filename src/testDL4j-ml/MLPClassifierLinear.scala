import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

import java.io.File;

val seed = 123;
val learningRate = 0.01;
val batchSize = 50;
val nEpochs = 30;

val numInputs = 2;
val numOutputs = 2;
val numHiddenNodes = 20;

//Load the training data:
val rr = new CSVRecordReader();
//        rr.initialize(new FileSplit(new File("src/main/resources/classification/linear_data_train.csv")));
rr.initialize(new FileSplit(new File("dl4j-examples/src/main/resources/classification/linear_data_train.csv")));
val trainIter = new RecordReaderDataSetIterator(rr,batchSize,0,2);

//Load the test/evaluation data:
val rrTest = new CSVRecordReader();
rrTest.initialize(new FileSplit(new File("dl4j-examples/src/main/resources/classification/linear_data_eval.csv")));
val testIter = new RecordReaderDataSetIterator(rrTest,batchSize,0,2);

val conf = new NeuralNetConfiguration.Builder()
        .seed(seed)
        .iterations(1)
        .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
        .learningRate(learningRate)
        .updater(Updater.NESTEROVS).momentum(0.9)
        .list()
        .layer(0, new DenseLayer.Builder().nIn(numInputs).nOut(numHiddenNodes)
                .weightInit(WeightInit.XAVIER)
                .activation("relu")
                .build())
        .layer(1, new OutputLayer.Builder(LossFunction.NEGATIVELOGLIKELIHOOD)
                .weightInit(WeightInit.XAVIER)
                .activation("softmax").weightInit(WeightInit.XAVIER)
                .nIn(numHiddenNodes).nOut(numOutputs).build())
        .pretrain(false).backprop(true).build();


val model = new MultiLayerNetwork(conf);
model.init();
model.setListeners(new ScoreIterationListener(10));  //Prval score every 10 parameter updates


for ( n <- 0 until nEpochs ) {
    model.fit( trainIter );
}

println("Evaluate model....");
val eval = new Evaluation(numOutputs);
while(testIter.hasNext()){
    val t = testIter.next();
    val features = t.getFeatureMatrix();
    val lables = t.getLabels();
    val predicted = model.output(features,false);

    eval.eval(lables, predicted);

}

//Prval the evaluation statistics
println(eval.stats());


//------------------------------------------------------------------------------------
//Training is complete. Code that follows is for plotting the data & predictions only

//Plot the data:
val xMin = 0;
val xMax = 1.0;
val yMin = -0.2;
val yMax = 0.8;

//Let's evaluate the predictions at every poval in the x/y input space
val nPovalsPerAxis = 100;
val evalPovals = new Array[Array[Double]](nPovalsPerAxis*nPovalsPerAxis).map(_ => new Array[Double](2))
var count = 0;
for( i <- 0 until nPovalsPerAxis ){
    for( j <- 0 until nPovalsPerAxis ){
        val x = i * (xMax-xMin)/(nPovalsPerAxis-1) + xMin;
        val y = j * (yMax-yMin)/(nPovalsPerAxis-1) + yMin;

        evalPovals(count)(0) = x;
        evalPovals(count)(1) = y;

        count+=1;
    }
}

val allXYPovals = Nd4j.create(evalPovals);
val predictionsAtXYPovals = model.output(allXYPovals);

//Get all of the training data in a single array, and plot it:
rr.initialize(new FileSplit(new File("resources/classification/linear_data_train.csv")));
rr.reset();
val nTrainPovals = 1000;
val trainIter = new RecordReaderDataSetIterator(rr,nTrainPovals,0,2);
val ds = trainIter.next();
PlotUtil.plotTrainingData(ds.getFeatures(), ds.getLabels(), allXYPovals, predictionsAtXYPovals, nPovalsPerAxis);


//Get test data, run the test data through the network to generate predictions, and plot those predictions:
rrTest.initialize(new FileSplit(new File("resources/classification/linear_data_eval.csv")));
rrTest.reset();
val nTestPovals = 500;
testIter = new RecordReaderDataSetIterator(rrTest,nTestPovals,0,2);
ds = testIter.next();
val testPredicted = model.output(ds.getFeatures());
PlotUtil.plotTestData(ds.getFeatures(), ds.getLabels(), testPredicted, allXYPovals, predictionsAtXYPovals, nPovalsPerAxis);

println("****************Example finished********************");
