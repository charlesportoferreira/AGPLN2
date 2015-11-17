/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reducaostopwords;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author charles
 */
public class ReducaoStopWords {

    public List<String> filePaths = new ArrayList<>();
    private int id;
    private List<String> stoplists;

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        ArrayList<String> stoplists = new ArrayList<>();
//        stoplists.add("VB.xml");
//       stoplists.add("ingl.xml");
        for (int i = 0; i < 10; i++) {
             new ReducaoStopWords().execute(i, stoplists);
        }
       
    }

    public void execute(int id, List<String> namesStoplists) {
        System.out.println("Stopfiles: " + namesStoplists.toString());

        this.id = id;
        this.stoplists = namesStoplists;
        String diretorioStoplists = System.getProperty("user.dir");
        diretorioStoplists += "/stoplists";
        System.out.println("lendo stoplists...");
        List<String> stoplistPaths = fileTreePrinter(new File(diretorioStoplists), 0, ".xml");
        for (int i = 0; i < stoplists.size(); i++) {
            for (String stoplistPath : stoplistPaths) {
                if (stoplistPath.contains(stoplists.get(i))) {
                    stoplists.set(i, stoplistPath);
                }
            }
        }
        String diretorioNames = System.getProperty("user.dir");
        diretorioNames += "/discover.names";
        String diretorioData = System.getProperty("user.dir");
        diretorioData += "/discover.data";
        //List<String> StoplistPaths = fileTreePrinter(new File(diretorioStoplists), 0, ".xml");
        List<String> stopwords = null;
        Map<String, Integer> mapaNomes = null;
        System.out.println("separando stopwords...");
        try {
            stopwords = lerStopWords(stoplists);
            System.out.println("stopword: " + stopwords.size());
            mapaNomes = lerNames(diretorioNames);

        } catch (IOException ex) {
            System.out.println("Erro na criacao das stopwords e do mapa de nomes");
            Logger.getLogger(ReducaoStopWords.class.getName()).log(Level.SEVERE, null, ex);
        }

        List<Integer> posicoes = new ArrayList<>();
        for (String stopword : stopwords) {

            if (mapaNomes.containsKey(stopword)) {
//                System.out.println("pos: " + mapaNomes.get(stopword) + " valor: " + stopword);
                posicoes.add(mapaNomes.get(stopword));
                mapaNomes.remove(stopword);
            }
        }

        System.out.println(posicoes.toString());
        Collections.sort(posicoes);
        System.out.println(posicoes.toString());
        System.out.println("convertendo attributos");
        try {
            String names = convertNames(diretorioNames, posicoes);
            printFile(this.id + ".names", names);
//        System.out.println(stopwords.toString());
//        System.out.println(names.toString());
        } catch (IOException ex) {
            System.out.println("Erro na criacao do arquivo .names");
            Logger.getLogger(ReducaoStopWords.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("convertendo tabela");
        try {
            convertData(diretorioData, posicoes);
        } catch (IOException ex) {
            Logger.getLogger(ReducaoStopWords.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("reduzindo a tabela...");
        try {
            reduzArff(this.id + ".data", "R" + this.id + ".data");
        } catch (IOException ex) {
            System.out.println("Erro na criacao do arquivo .data");
            Logger.getLogger(ReducaoStopWords.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("criando arff");
        createArff(this.id + ".names", "R" + this.id + ".data", this.id + ".arff");

        try {
            Runtime.getRuntime().exec("rm " + this.id + ".names");
            Runtime.getRuntime().exec("rm " + this.id + ".data");
            Runtime.getRuntime().exec("rm R" + this.id + ".data");

        } catch (IOException ex) {
            Logger.getLogger(ReducaoStopWords.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public List<String> fileTreePrinter(File initialPath, int initialDepth, String filter) {

        int depth = initialDepth++;
        if (initialPath.exists()) {
            File[] contents = initialPath.listFiles();
            for (File content : contents) {
                if (content.isDirectory()) {
                    fileTreePrinter(content, initialDepth + 1, filter);
                } else {
                    char[] dpt = new char[initialDepth];
                    for (int j = 0; j < initialDepth; j++) {
                        dpt[j] = '+';
                    }
                    // System.out.println(new String(dpt) + content.getName() + " " + content.getPath() );
                    //System.out.println(content.toString());

                    if (content.getName().contains(filter)) {
                        filePaths.add(content.toString());
                    }

                }
            }
        }
        return filePaths;
    }

    public List<String> lerStopWords(List<String> filePaths) throws FileNotFoundException, IOException {
        List<String> stopwords = new ArrayList<>();
        String linhaLida;
        for (String filePath : filePaths) {
            try (FileReader fr = new FileReader(filePath); BufferedReader br = new BufferedReader(fr)) {
                while (br.ready()) {
                    linhaLida = br.readLine();
                    if (linhaLida.contains("<stopword>")) {
                        linhaLida = linhaLida.replaceAll("<stopword>|</stopword>|\t", "");
                        linhaLida = linhaLida.trim();
                        stopwords.add(linhaLida);
                    }
                }
                br.close();
                fr.close();
            }
        }
        return stopwords;
    }

    public Map<String, Integer> lerNames(String filePath) throws FileNotFoundException, IOException {
        int pos = 0;
        String linhaLida;
        Map<String, Integer> mapaNomes = new HashMap<>();
        try (FileReader fr = new FileReader(filePath); BufferedReader br = new BufferedReader(fr)) {
            while (br.ready()) {
                linhaLida = br.readLine();
                if (linhaLida.contains("real.")) {
                    linhaLida = linhaLida.replaceAll("\"|:real.", "");
                    mapaNomes.put(linhaLida, pos);
                    pos++;
//                    System.out.println(linhaLida);
                }
            }
            br.close();
            fr.close();
        }
        return mapaNomes;
    }

    public String convertNames(String filePath, List<Integer> posicoes) throws FileNotFoundException, IOException {
        int posAtual = 0;
        String linhaLida;
        StringBuilder sb = new StringBuilder();
        try (FileReader fr = new FileReader(filePath); BufferedReader br = new BufferedReader(fr)) {
            while (br.ready()) {
                linhaLida = br.readLine();
                if (linhaLida.contains("real.")) {
                    if (!posicoes.contains(posAtual)) {
                        //linhaLida = linhaLida.replaceAll("\"|:real.", "");
                        sb.append(linhaLida);
                        sb.append("\n");
                    }
                    posAtual++;
                } else {
                    if (linhaLida.contains("att_class:nominal")) {
                        sb.append(linhaLida);
                    }
                }
            }
            br.close();
            fr.close();
        }
        return sb.toString();
    }

    public String convertData(String filePath, List<Integer> posicoes) throws FileNotFoundException, IOException {
        File file = new File(id + ".data");
        if (file.exists()) {
            file.delete();
        }
        int posAtual = 0;
        String linhaLida;
        String primeiroValor = "";
        try (FileReader fr = new FileReader(filePath); BufferedReader br = new BufferedReader(fr)) {
            while (br.ready()) {
                StringBuilder sb = new StringBuilder();
                linhaLida = br.readLine();
                primeiroValor = linhaLida.split(",")[0];
                linhaLida = linhaLida.replace(primeiroValor, "");
                linhaLida = linhaLida.substring(1);
                String[] valores = linhaLida.split(",");
                sb.append(primeiroValor).append(",");
                for (int i = 0; i < valores.length; i++) {
                    if (!posicoes.contains(i)) {
                        sb.append(valores[i]);
                        sb.append(",");
                    }
                }
                int i = sb.lastIndexOf(",");
                sb.deleteCharAt(i);
                sb.append("\n");
                printData(id + ".data", sb.toString());
            }
            br.close();
            fr.close();
        }
        return "";
    }

    public void printData(String fileName, String texto) throws IOException {

        try (FileWriter fw = new FileWriter(fileName, true); BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(texto);
            // bw.newLine();
            bw.close();
            fw.close();
        }
    }

    public void printFile(String fileName, String texto) throws IOException {
        String header = "att_class." + "\n" + "filename:string:ignore.\n";
        String footnote = "";
        try (FileWriter fw = new FileWriter(fileName); BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(header + texto);
            // bw.newLine();
            bw.close();
            fw.close();
        }
    }

    public void reduzArff(String oldFile, String newFile) throws FileNotFoundException, IOException {
        deletaArquivoExistente(newFile);
        String linha;
        double valorLido;
        try (FileReader fr = new FileReader(oldFile); BufferedReader br = new BufferedReader(fr)) {
            while (br.ready()) {
                StringBuilder sb = new StringBuilder();
                sb.append("{");
                linha = br.readLine();
                linha = linha.replaceAll("\".*\",|", "");
                String[] dados = linha.split(",");
                for (int i = 0; i < dados.length; i++) {
                    try {
                        valorLido = Double.parseDouble(dados[i]);
                        if (valorLido > 0) {
                            sb.append(i).append(" ").append(dados[i]).append(",");
                        }
                    } catch (NumberFormatException ex) {
                        sb.append(i).append(" ").append(dados[i]).append(",");
//                        System.out.println("dados[i],1,linha" + dados[i] + " " + i + " " + linha);
                    }
                }
                if ("{".equals(String.valueOf(sb.charAt(sb.length() - 1)))) {
                    sb.append("}");
                } else {
                    sb.replace(sb.lastIndexOf(","), sb.lastIndexOf(",") + 1, "}\n");
                }

                printData(newFile, sb.toString());
            }
            br.close();
            fr.close();
        }
    }

    public void deletaArquivoExistente(String nomeArquivo) {
        File f = new File(nomeArquivo);
        if (f.exists()) {
            f.delete();
        }
    }

    public void createArff(String nameFile, String dataFile, String newFile) {
        try {
            String names = readNamesFiles(nameFile);
            //System.out.println(names);
            deletaArquivoExistente(id + ".arff");
            printData(id + ".arff", names);
        } catch (IOException ex) {
            Logger.getLogger(ReducaoStopWords.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            convertDataFiles(dataFile, newFile);
        } catch (IOException ex) {
            Logger.getLogger(ReducaoStopWords.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String readNamesFiles(String fileName) throws FileNotFoundException, IOException {
        //  @ATTRIBUTE sepallength  NUMERIC
        //  @ATTRIBUTE class        {Iris-setosa,Iris-versicolor,Iris-virginica}
        // att_class:nominal("wheat","corn").
        StringBuilder sb = new StringBuilder();
        sb.append("@RELATION ").append(fileName.replace(".names", "")).append("\n\n");
        String linha = "";
        try (FileReader fr = new FileReader(fileName); BufferedReader br = new BufferedReader(fr)) {
            while (br.ready()) {
                linha = br.readLine();
                if (!linha.contains(":real.")) {
                    if (linha.contains("att_class:nominal")) {
                        linha = linha.replaceAll("att_class:nominal\\(|\"|\\)|\\.", "");
                        sb.append("@ATTRIBUTE classesClassificacao        {").append(linha).append("}");
                    }
                    continue;
                }
                linha = linha.replaceAll("\"|:real.", "");
                sb.append("@ATTRIBUTE ").append(linha).append(" NUMERIC").append("\n");
            }

            br.close();
            fr.close();
        }
        return sb.toString();
    }

    public String convertDataFiles(String fileName, String newFile) throws FileNotFoundException, IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("\n\n@Data\n");
        printData(newFile, sb.toString());
        try (FileReader fr = new FileReader(fileName); BufferedReader br = new BufferedReader(fr)) {
            while (br.ready()) {
                printData(newFile, br.readLine() + "\n");
            }

            br.close();
            fr.close();
        }
        return "";
    }

}
