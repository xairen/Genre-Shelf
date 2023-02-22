package com.example.servingwebcontent;

import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.util.*;

/**
 *
 * CHECKPOINT#3 CORE ALGORITHM
 * GenreShelf - basic code implementation
 * ISTE 612
 */
@Getter
@Setter
public class genre{

    private int numClasses;
    private int[] docCount;
    private int[] numToken;
    private ArrayList<ArrayList<Integer>> predict;
    private File[] docFiles;
    private HashMap<String, Double>[] probTerm;
    private double[] prob;
    private HashSet<String> vocab;

    public genre(File trainFolder)throws IOException{
        vocab = new HashSet<String>();
        File[] files = trainFolder.listFiles();
        docFiles = new File[files.length];

        for(int i=0, j=0; i<files.length; i++){
            if(files[i].isDirectory()){
                numClasses++;

                docFiles[j] = files[i];
                j++;
            }
        }

        probTerm = new HashMap[numClasses];
        docCount = new int[numClasses];
        numToken = new int[numClasses];
        prob = new double[numClasses];
        int totalTrainDoc=0;

        for(int i=0; i<numClasses; i++){
            docCount[i]= docFiles[i].listFiles().length;
            probTerm[i]= new HashMap<String,Double>();
            totalTrainDoc+=docCount[i];
            ArrayList<String> termTokens = preprocessing(docFiles[i], i);
            numToken[i] = termTokens.size();
        }

        int vocabSize = vocab.size();

        for(int i=0; i<numClasses;i++){
            for(String t : probTerm[i].keySet()){
                double freq = probTerm[i].get(t);
                double probability = (freq+1)/(numToken[i]+vocabSize);
                probTerm[i].put(t, probability);
            }
            prob[i]=(double)docCount[i]/totalTrainDoc;
        }
    }

    /**
     * Tokenization
     * @param fileName
     * @return
     * @throws IOException
     */
    public ArrayList<String> tokenize(File fileName)throws IOException{

        String[] tokens = null;
        ArrayList<String> pureTokens = new ArrayList<String>();
        Scanner sc = new Scanner(fileName);
        String allLines = new String();

        while(sc.hasNextLine()){
            allLines += sc.nextLine();
        }
        tokens = allLines.split("[\" ()_,?:;%&-]+");
        for(String token: tokens){
            pureTokens.add(token);
        }
        return pureTokens;
    }

    /**
     * function is being used to classify test dataset
     * @param termDoc
     * @return
     */
    public int testClassify(ArrayList<String> termDoc){
        double[] actual = new double[numClasses];

        for(String term : termDoc){
            for(int i=0; i<numClasses; i++){
                if(probTerm[i].containsKey(term)){
                    double current = probTerm[i].get(term);
                    actual[i]+=Math.log10(current);
                }
                else
                {
                    actual[i]+=Math.log10((double)1/(numToken[i]+vocab.size()));
                }
            }
        }

        for(int i=0;i<numClasses;i++){
            actual[i]+=Math.log10(prob[i]);
        }

        double max = actual[0];
        int temp=0;

        for(int i=1; i<numClasses; i++){
            if(actual[i] > max){
                max = actual[i];
                temp=i;
            }
        }

        return temp;
    }

    /**
     * preprocessing step to utilize and access training dataset
     * @param trainFolder
     * @param num
     * @return
     * @throws IOException
     */
    public ArrayList<String> preprocessing(File trainFolder, int num)throws IOException{
        File[] fileList = trainFolder.listFiles();
        ArrayList<String> fileToken = new ArrayList<String>();

        for(int i=0; i<fileList.length; i++){
            ArrayList<String> pureTokens = tokenize(fileList[i]);
            fileToken.addAll(pureTokens);
            for(String token: pureTokens){
                vocab.add(token);
                if(probTerm[num].containsKey(token))
                {
                    double count = probTerm[num].get(token);
                    probTerm[num].put(token, count+1);
                }
                else
                {
                    probTerm[num].put(token, 1.0);
                }
            }
        }

        return fileToken;
    }

    /**
     * at a later stage we will predict and give the genre's as output
     * Will be utilizing another dataset that contains the genre names to check the accuracy
     */
    public void genreName() {

    }

    /**
     * classification and calculations
     * @param testFolder
     * @return
     * @throws IOException
     */
    public double classifyGenre(File testFolder)throws IOException{
        int totalTestDocs =0, truePositive=0, trueNegative=0, falsePositive=0, falseNegative=0 ;
        predict = new ArrayList<ArrayList<Integer>>();
        File files[] = testFolder.listFiles();

        for(int i=0, j=0; i<files.length; i++){
            if(files[i].isDirectory())
            {
                ArrayList<Integer> genrePredict = new ArrayList<Integer>();
                File[] docs = files[i].listFiles();
                for(int k=0; k<docs.length; k++){
                    ArrayList<String> pureTokens = tokenize(docs[j]);
                    genrePredict.add(testClassify(pureTokens));
                }
                predict.add(genrePredict);
                j++;
            }
        }

        for(int i=0; i<predict.size(); i++){
            ArrayList<Integer> classPrediction = predict.get(i);
            totalTestDocs+=classPrediction.size();
            for(int value : classPrediction){
                if(value==i)
                {
                    if(i==0)
                    {
                        trueNegative++;
                    }
                    else{
                        truePositive++;
                    }
                }
                else
                {
                    if(value==0)
                    {
                        falseNegative++;
                    }
                    else
                    {
                        falsePositive++;
                    }
                }
            }
        }

        double accuracy= (double)(trueNegative+truePositive)/totalTestDocs;
        double precision = truePositive/(truePositive+falsePositive+0.0);
        double recall = truePositive/(truePositive+falseNegative+0.0);
        double fscore = 2*precision*recall/(precision+recall);
        System.out.println("Accuracy="+accuracy+"\nPrecision="+precision+ "\nRecall="+recall+"\nF-Score="+fscore);
        System.out.println("True Positive = "+truePositive+"\tTrue Negative = "+trueNegative+"\nFalse Positive = "+falsePositive+ "\tFalse Negative = "+falseNegative);

        return accuracy;

    }


    public static void main(String[] args)throws IOException{

    }
}

