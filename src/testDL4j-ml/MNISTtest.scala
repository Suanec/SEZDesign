
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator;
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
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


val numRows = 28;
val numColumns = 28;
val outputNum = 10;
val batchSize = 64;
val rngSeed = 123;
val numEpochs = 15;
val rate = 0.0015;

//Get the DataSetIterators:
val mnistTrain = new MnistDataSetIterator(batchSize, true, rngSeed);
val mnistTest = new MnistDataSetIterator(batchSize, false, rngSeed);


val conf = new NeuralNetConfiguration.Builder()
    .seed(rngSeed)
    .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
    .iterations(1)
    .activation("relu")
    .weightInit(WeightInit.XAVIER)
    .learningRate(rate)
    .updater(Updater.NESTEROVS).momentum(0.98)
    .regularization(true).l2(rate * 0.005)
    .list()
    .layer(0, new DenseLayer.Builder()
            .nIn(numRows * numColumns)
            .nOut(500)
            .build())
    .layer(1,  new DenseLayer.Builder()
            .nIn(500)
            .nOut(100)
            .build())
    .layer(2, new OutputLayer.Builder(LossFunction.NEGATIVELOGLIKELIHOOD)
            .activation("softmax")
            .nIn(100)
            .nOut(outputNum)
            .build())
    .pretrain(false).backprop(true)
    .build();

val model = new MultiLayerNetwork(conf);
model.init();
model.setListeners(new ScoreIterationListener(5));

(0 until numEpochs).map{
    i =>
    println("Epoch " + i);
    model.fit(mnistTrain);
}


println("Evaluate model....");
val eval = new Evaluation(outputNum);
while(mnistTest.hasNext()){
    val next = mnistTest.next();
    val output = model.output(next.getFeatureMatrix());
    eval.eval(next.getLabels(), output);
}

println(eval.stats());
println("****************Example finished********************");

