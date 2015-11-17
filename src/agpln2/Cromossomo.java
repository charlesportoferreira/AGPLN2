/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agpln2;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import reducaostopwords.ReducaoStopWords1;
import weka.classifiers.Evaluation;
import weka.classifiers.misc.HyperPipes;
import weka.core.Instances;

/**
 *
 * @author charleshenriqueportoferreira
 */
public class Cromossomo implements Callable<String> {

    private double probSelecao;
    private List<Gene> genes;
    private static int id = 0;
    private final int inId;
    private double fitness;
    private String configGenes;
    private double pctAcerto;
    private double microAverage;
    private double macroAverage;
    private int numAtributos;
    public Map<String, String> syncHashMap;


    public void resetFitness() {
        this.fitness = 0;
    }

    public int getInId() {
        return inId;
    }

    public int getNumAtributos() {
        return numAtributos;
    }

    public Cromossomo(int nrBits) {
        inId = id;
        id++;
        genes = new ArrayList<>(nrBits);
        criaGenes();
       
    }

    public String getConfigGenes() {
        configGenes = getStringConfiguracao();
        return configGenes;
    }

    public double getProbSelecao() {
        return probSelecao;
    }

    public void setProbSelecao(double probSelecao) {
        this.probSelecao = probSelecao;
    }

    public void setConfigGenes(String configuracaoGenes) {
        this.configGenes = configuracaoGenes;
    }

    public List<Gene> getGenes() {
        return genes;
    }

    public void setGenes(List<Gene> genes) {
        this.genes = genes;
    }

    public double getFitness() {
        if (fitness == 0) {
            String geneDecodificado = this.getGeneDecodificado();
            if (syncHashMap.containsKey(geneDecodificado)) {
                String[] res = syncHashMap.get(geneDecodificado).split("-");
                this.fitness = Double.parseDouble(res[0]);
                this.pctAcerto = Double.parseDouble(res[0]);
                this.microAverage = Double.parseDouble(res[1]);
                this.macroAverage = Double.parseDouble(res[2]);
                this.numAtributos = Integer.parseInt(res[3]);

                //System.out.print("calculado: " + geneDecodificado + " " + fitness);
            } else {
                calculaFitness();
            }
        }
        return fitness;
    }

    public void calculaFitness() {
        // Instant inicio = Instant.now();
        List<String> stoplists = decodificaCromossomo();
        //System.out.println(stoplists.toString());
        criaArff(stoplists);
        // Instant fim = Instant.now();
        // Duration duracao = Duration.between(inicio, fim);
        // System.out.println("Duracao construir tabela " + inId + " " + duracao + " segundos");
        // this.fitness = new Random().nextInt();
        classifica();
        limparDados();
    }
//************** Verificar as stoplists que estao sendo usadas ***********************

    private void criaGenes() {
        // String[] stopLists = {"CC.xml", "CD.xml", "DT.xml", "EX.xml", "FW.xml", "IN.xml",
        //     "JJ.xml", "JJR.xml", "JJS.xml", "LS.xml", "MD.xml", "NN.xml", "NNS.xml", "NNP.xml", "NNPS.xml",
        //     "PDT.xml", "POS.xml", "PRP.xml", "PRP$.xml", "RB.xml", "RBR.xml", "RBS.xml", "RP.xml", "SYM.xml",
        //     "TO.xml", "UH.xml", "VB.xml", "VBD.xml", "VBG.xml", "VBN.xml", "VBP.xml", "VBZ.xml", "WDT.xml",
        //     "WP.xml", "WP$.xml", "WRB.xml"};

        String[] stopLists = {"DT.xml", "FW.xml", "IN.xml",
            "JJ.xml", "JJR.xml", "JJS.xml", "NN.xml", "NNS.xml", "NNP.xml", "NNPS.xml", "RB.xml",
            "VB.xml", "VBD.xml", "VBG.xml", "VBN.xml", "VBP.xml", "VBZ.xml"};

        for (String stopList : stopLists) {
            genes.add(new Gene(stopList));
        }

    }

    //************** Verificar as stoplists que estao sendo usadas ***********************F
    @Override
    public String toString() {
        return "Cromossomo{" + "genes=" + genes + '}';
    }

    public String getStringConfiguracao() {
        return getGeneCodificado();
    }

    public String getGeneCodificado() {
        StringBuilder geneStoplist = new StringBuilder();
        genes.stream().forEach((gene) -> {
            geneStoplist.append(gene.getValor()).append(",");
        });

        int index = geneStoplist.lastIndexOf(",");
        geneStoplist.deleteCharAt(index);

        return geneStoplist.toString();
    }

    public String getGeneDecodificado() {
        StringBuilder geneStoplist = new StringBuilder();
        genes.stream().forEach((gene) -> {
            if (gene.getValor() == 1) {
                geneStoplist.append(gene.getNome().replace(".xml", "")).append(",");
            }
        });

        int index = geneStoplist.lastIndexOf(",");
        if (index != -1) {
            geneStoplist.deleteCharAt(index);
        } else {
            geneStoplist.append("-");
        }

        return geneStoplist.toString();
    }

    @Override
    public String call() throws Exception {
        getFitness();
        System.out.print(" - ");
        return String.valueOf("Meu fitness: " + fitness);
    }

    private void classifica() {
        // System.out.println("Executando a classificao do :" + inId);
        //SMO classifier = new SMO();

        HyperPipes classifier = new HyperPipes();
        BufferedReader datafile = readDataFile(inId + ".arff");

        Instances data;
        Evaluation eval;
        try {
            data = new Instances(datafile);
            data.setClassIndex(data.numAttributes() - 1);
            numAtributos = data.numAttributes();
            eval = new Evaluation(data);
            Random rand = new Random(1); // usando semente = 1
            int folds = 10;
            eval.crossValidateModel(classifier, data, folds, rand);
            //this.fitness = eval.pctCorrect();
            //fitness = new BigDecimal(fitness).setScale(2, RoundingMode.HALF_UP).doubleValue();//arredondamento para duas casas
            pctAcerto = eval.pctCorrect();
            pctAcerto = new BigDecimal(pctAcerto).setScale(2, RoundingMode.HALF_UP).doubleValue();
            microAverage = getMicroAverage(eval, data);
            microAverage = new BigDecimal(microAverage).setScale(2, RoundingMode.HALF_UP).doubleValue();
            macroAverage = getMacroAverage(eval, data);
            macroAverage = new BigDecimal(macroAverage).setScale(2, RoundingMode.HALF_UP).doubleValue();
            fitness = pctAcerto;
            syncHashMap.put(getGeneDecodificado(), fitness + "-" + microAverage + "-" + macroAverage + "-" + numAtributos);

            // System.out.println("id: " + inId + " acerto: " + fitness);
        } catch (Exception ex) {
            System.out.println("Erro ao tentar fazer a classificacao");
            System.out.println("Meu ID eh " + inId);
            Logger.getLogger(Cromossomo.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        }
    }

    private double getMacroAverage(Evaluation eval, Instances data) {
        double macroMeasure;
        double macroPrecision = 0;
        double macroRecall = 0;

        for (int i = 0; i < data.numClasses(); i++) {
            macroPrecision += eval.precision(i);
            macroRecall += eval.recall(i);
        }
        macroPrecision = macroPrecision / data.numClasses();
        macroRecall = macroRecall / data.numClasses();
        macroMeasure = (macroPrecision * macroRecall * 2) / (macroPrecision + macroRecall);
        //System.out.println("macroMeasure: " + macroMeasure);

        return macroMeasure;
    }

    private double getMicroAverage(Evaluation eval, Instances data) {
        double TP = 0;
        double TP_plus_FP = 0;
        double TP_plus_FN = 0;
        double microPrecision;
        double microRecall;
        double microMeasure;

        for (int i = 0; i < data.numClasses(); i++) {
            TP += eval.truePositiveRate(i);
            TP_plus_FP += eval.truePositiveRate(i) + eval.falsePositiveRate(i);
            TP_plus_FN += eval.truePositiveRate(i) + eval.falseNegativeRate(i);
        }
        microPrecision = TP / TP_plus_FP;
        microRecall = TP / TP_plus_FN;
        microMeasure = (microPrecision * microRecall * 2) / (microPrecision + microRecall);

        //System.out.println("microMeasure: " + microMeasure);
        return microMeasure;
    }

    public double getPctAcerto() {
        return pctAcerto;
    }

    public double getMicroAverage() {
        return microAverage;
    }

    public double getMacroAverage() {
        return macroAverage;
    }

    public BufferedReader readDataFile(String filename) {
        BufferedReader inputReader = null;

        try {
            inputReader = new BufferedReader(new FileReader(filename));
        } catch (FileNotFoundException ex) {
            System.err.println("File not found: " + filename);
        }

        return inputReader;
    }

    private List<String> decodificaCromossomo() {
        List<String> stoplists = new ArrayList<>();
        for (Gene gene : genes) {
            if (gene.getValor() == 1) {
                stoplists.add(gene.getNome());
            }
        }
        return stoplists;
    }

    private void criaArff(List<String> stoplists) {
        // new ReducaoStopWords().execute(this.inId, stoplists);
        new ReducaoStopWords1().execute(this.inId, stoplists);

    }

    private void limparDados() {
        try {
            Runtime.getRuntime().exec("rm " + this.inId + ".arff");
        } catch (IOException ex) {
            Logger.getLogger(ReducaoStopWords1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
