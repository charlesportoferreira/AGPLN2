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
public class ReducaoStopWords1 {

    public List<String> filePaths = new ArrayList<>();
    private int id;
    private List<String> stoplists;

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        ArrayList<String> stoplists = new ArrayList<>();
        //stoplists.add("VB.xml");
        //stoplists.add("ingl.xml");
        for (int i = 0; i < 1; i++) {
            new ReducaoStopWords1().execute(i, stoplists);
        }
    }

    public void execute(int id, List<String> namesStoplists) {
        // System.out.println("Stopfiles: " + namesStoplists.toString());

        this.id = id;
         this.stoplists = namesStoplists;
        //stoplists = new ArrayList<>();
       // stoplists.add("ingl.xml");
//        stoplists.add("VB.xml");

        String diretorioStoplists = System.getProperty("user.dir");
        diretorioStoplists += "/stoplist";
        //   System.out.println("lendo stoplists...");
        List<String> stoplistPaths = fileTreePrinter(new File(diretorioStoplists), 0, ".xml");

        atualizaCaminhoDiretorioStoplist(stoplistPaths);
        //System.out.println(stoplistPaths.toString());
        //System.exit(0);

        String diretorioNames = System.getProperty("user.dir");
        diretorioNames += "/discover.names";
        String diretorioData = System.getProperty("user.dir");
        diretorioData += "/discover.data";

        List<String> stopwords = null;
        Map<String, List<Integer>> mapaNomes1 = new HashMap<>();
        Map<String, List<Integer>> mapaNomes2 = new HashMap<>();
        //  System.out.println("separando stopwords...");

        try {
            stopwords = lerStopWords(stoplists);
            lerNames(diretorioNames, mapaNomes1, mapaNomes2);
        } catch (IOException ex) {
            System.out.println("Erro na criacao das stopwords e do mapa de nomes");
            Logger.getLogger(ReducaoStopWords1.class.getName()).log(Level.SEVERE, null, ex);
        }

        List<Integer> posicoes = new ArrayList<>();
        atualizaListaPosicoes(stopwords, mapaNomes1, posicoes);
        atualizaListaPosicoes(stopwords, mapaNomes2, posicoes);

        //System.out.println(posicoes.toString());
        Collections.sort(posicoes);
        //System.out.println(posicoes.toString());

        // System.out.println("convertendo atributos");
        try {
            String names = convertNames(diretorioNames, posicoes);
            printFile(this.id + ".names", names);
        } catch (IOException ex) {
            System.out.println("Erro na criacao do arquivo .names");
            Logger.getLogger(ReducaoStopWords1.class.getName()).log(Level.SEVERE, null, ex);
        }
        // System.out.println("convertendo tabela");
        try {
            convertData(diretorioData, posicoes);
        } catch (IOException ex) {
            Logger.getLogger(ReducaoStopWords1.class.getName()).log(Level.SEVERE, null, ex);
        }

        limpaDados();

    }

    private void atualizaCaminhoDiretorioStoplist(List<String> stoplistPaths) {
        for (int i = 0; i < stoplists.size(); i++) {
            for (String stoplistPath : stoplistPaths) {
                if (stoplistPath.contains(stoplists.get(i))) {
                    stoplists.set(i, stoplistPath);
                }
            }
        }
    }

    private void limpaDados() {
        try {
            Runtime.getRuntime().exec("rm " + this.id + ".names");
            Runtime.getRuntime().exec("rm " + this.id + ".data");

        } catch (IOException ex) {
            System.out.println("erro ao tentar limpar os dados: " + this.id);
            Logger.getLogger(ReducaoStopWords1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void atualizaListaPosicoes(List<String> stopwords, Map<String, List<Integer>> mapaNomes, List<Integer> posicoes) {
        List<Integer> listaPosicoes;
        for (String stopword : stopwords) {
            if (mapaNomes.containsKey(stopword)) {
                listaPosicoes = mapaNomes.get(stopword);
                for (Integer posicao : listaPosicoes) {
                    if (!posicoes.contains(posicao)) {
                        posicoes.add(posicao);
                    }
                }
                mapaNomes.remove(stopword);
            }
        }
    }

    /**
     *
     * @param initialPath
     * @param initialDepth
     * @param filter
     * @return
     */
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
                    if (content.getName().contains(filter)) {
                        filePaths.add(content.toString());
                    }
                }
            }
        }
        return filePaths;
    }

    /**
     * Recebe como entrada uma lista de caminhos para stoplists no formato do Pretext e devolde uma unica lista contendo todas essas palavras.
     *
     * @param filePaths caminhos para as stoplists
     * @return Lista contendo as stopwords
     * @throws FileNotFoundException
     * @throws IOException
     */
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

    /**
     *
     * @param filePath
     * @param mapaNomes1
     * @param mapaNomes2
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void lerNames(String filePath, Map<String, List<Integer>> mapaNomes1, Map<String, List<Integer>> mapaNomes2) throws FileNotFoundException, IOException {
        int pos = 0;
        String linhaLida;
        String palavra1;
        String palavra2;

        try (FileReader fr = new FileReader(filePath); BufferedReader br = new BufferedReader(fr)) {
            while (br.ready()) {
                linhaLida = br.readLine();
                if (linhaLida.contains("real.")) {
                    linhaLida = linhaLida.replaceAll("\"|:real.", "");
                    if (linhaLida.contains("_")) {
                        palavra1 = linhaLida.split("_")[0];
                        atualizaMapaNomes(mapaNomes1, palavra1, pos);
                        palavra2 = linhaLida.split("_")[1];
                        atualizaMapaNomes(mapaNomes2, palavra2, pos);
                        pos++;
                    } else {
                        List<Integer> posicoesMapa = new ArrayList<>();
                        posicoesMapa.add(pos);
                        mapaNomes1.put(linhaLida, posicoesMapa);
                        pos++;
                    }
                }
            }
            br.close();
            fr.close();
        }
    }

    private void atualizaMapaNomes(Map<String, List<Integer>> mapaNomes, String palavra, int pos) {
        List<Integer> posicoesMapa;
        if (mapaNomes.containsKey(palavra)) {
            posicoesMapa = mapaNomes.get(palavra);
            posicoesMapa.add(pos);
        } else {
            posicoesMapa = new ArrayList<>();
            posicoesMapa.add(pos);
            mapaNomes.put(palavra, posicoesMapa);
        }
    }

    /**
     *
     * @param filePath
     * @param posicoes
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
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

    /**
     *
     * @param filePath
     * @param posicoes
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public String convertData(String filePath, List<Integer> posicoes) throws FileNotFoundException, IOException {
        String names = readNamesFiles(id + ".names");
        deletaArquivoExistente(id + ".names");
        deletaArquivoExistente(id + ".arff");
        printData(id + ".arff", names);
        printData(id + ".arff", "\n\n@Data");
        int j;
        String linhaLida;

        try (FileReader fr = new FileReader(filePath); BufferedReader br = new BufferedReader(fr)) {
            while (br.ready()) {
                j = 0;
                StringBuilder sb = new StringBuilder();
                // sb.append("{");
                linhaLida = br.readLine();
                int m = linhaLida.indexOf(",");
                linhaLida = linhaLida.substring(m + 1);
                String[] valores = linhaLida.split(",");
                for (int i = 0; i < valores.length; i++) {
                    if (j < posicoes.size()) {
                        if (posicoes.get(j) != i) {
                            sb.append(valores[i]).append(",");
                        } else {
                            j++;
                        }
                    } else {
                        sb.append(valores[i]).append(",");
                    }
                }
                int index = sb.lastIndexOf(",");
                sb.replace(index, index + 1, "");
                linhaLida = sb.toString();
                valores = linhaLida.split(",");
                sb = new StringBuilder();
                sb.append("{");
                for (int i = 0; i < valores.length; i++) {
                    if (isLastPosition(i, valores)) {//ultima posicao contem nome da classe
                        sb.append(i).append(" ").append(valores[i]).append("}");
                    } else {
                        addPalavra(valores, i, sb);
                    }
                }
                printData(id + ".arff", "\n" + sb.toString());
            }
            br.close();
            fr.close();
        }
        return "";
    }

    private void addPalavra(String[] valores, int i, StringBuilder sb) throws NumberFormatException {
        Double valorLido;
        valorLido = Double.parseDouble(valores[i]);
        if (valorLido > 0) {
            sb.append(i).append(" ").append(valores[i]).append(",");
        }
    }

    private static boolean isLastPosition(int i, String[] valores) {
        return i == valores.length - 1;
    }

    /**
     *
     * @param fileName
     * @param texto
     * @throws IOException
     */
    public void printData(String fileName, String texto) throws IOException {

        try (FileWriter fw = new FileWriter(fileName, true); BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(texto);
            // bw.newLine();
            bw.close();
            fw.close();
        }
    }

    /**
     *
     * @param fileName
     * @param texto
     * @throws IOException
     */
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

    

    /**
     *
     * @param nomeArquivo
     */
    public void deletaArquivoExistente(String nomeArquivo) {
        File f = new File(nomeArquivo);
        if (f.exists()) {
            f.delete();
        }
    }


    /**
     *
     * @param fileName
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public String readNamesFiles(String fileName) throws FileNotFoundException, IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("@RELATION ").append(fileName.replace(".names", "")).append("\n\n");
        String linha = "";
        try (FileReader fr = new FileReader(fileName); BufferedReader br = new BufferedReader(fr)) {
            while (br.ready()) {
                linha = br.readLine();
                if (!linha.contains(":real.")) {
                    if (linha.contains("att_class:nominal")) {
                        linha = linha.replaceAll("att_class:nominal\\(|\"|\\).", "");
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


}
