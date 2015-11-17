/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agpln2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author charles
 */
public class Start {

    public static List<String> filePaths = new ArrayList<>();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//        String a = "1-2-3";
//        String b = "1-2-";
//
//        String[] c = a.split("-");
//        String[] d = b.split("-");
//        System.out.println("c:" + c.length);
//        System.out.println("d:" + d.length);
//        System.exit(0);

        int numGeracoes = 500;
        limpaDados();

        if (args.length > 0) {
            try {
                numGeracoes = Integer.parseInt(args[0]);
            } catch (NumberFormatException ex) {
                System.out.println("Digite o numero de geracoes");
            }
        }

        AGPLN2 agpln2 = new AGPLN2();
        try {
            atualizaMapaFitness("banco.txt", agpln2.synchronizedHashMap);
        } catch (IOException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        }
        agpln2.criaPopulacaoInicial();
        System.out.println("---------------------");
        for (int i = 0; i < numGeracoes; i++) {
            System.out.println("\n-------------   Geracao " + i + "   ---------------------\n");
            agpln2.cruza();
            agpln2.muta();
            agpln2.seleciona(12);
        }

        StringBuilder sb = new StringBuilder();
        Iterator it = agpln2.synchronizedHashMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            sb.append(pair.getKey()).append(" = ").append(pair.getValue()).append("\n");
            it.remove(); // avoids a ConcurrentModificationException
        }

        try {
            deletaArquivoExistente("banco.txt");
            printFile("banco.txt", sb.toString());
        } catch (IOException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static void limpaDados() {
        String diretorio = System.getProperty("user.dir");
        fileTreePrinter(new File(diretorio), 0);
        for (String filePath : filePaths) {
            System.out.println(filePath);
            new File(filePath).delete();
        }

    }

    public static List<String> fileTreePrinter(File initialPath, int initialDepth) {

        int depth = initialDepth++;
        if (initialPath.exists()) {
            File[] contents = initialPath.listFiles();
            for (File content : contents) {
                if (content.isDirectory()) {
                    fileTreePrinter(content, initialDepth + 1);
                } else {
                    char[] dpt = new char[initialDepth];
                    for (int j = 0; j < initialDepth; j++) {
                        dpt[j] = '+';
                    }
                    // System.out.println(new String(dpt) + content.getName() + " " + content.getPath() );
                    //System.out.println(content.toString());

                    if (content.getName().contains(".names")
                            || content.getName().contains(".data")
                            || content.getName().contains(".arff")) {
                        if (!content.getName().contains("discover")) {
                            filePaths.add(content.toString());
                        }
                    }

                }
            }
        }
        return filePaths;
    }

    public static void printFile(String fileName, String texto) throws IOException {

        try (FileWriter fw = new FileWriter(fileName); BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(texto);
            // bw.newLine();
            bw.close();
            fw.close();
        }
    }

    public static void deletaArquivoExistente(String nomeArquivo) {
        File f = new File(nomeArquivo);
        if (f.exists()) {
            f.delete();
        }
    }

    private static void atualizaMapaFitness(String fileName, Map<String, String> synchronizedHashMap) throws FileNotFoundException, IOException {
        if (!existeArquivo("banco.txt")) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("@RELATION ").append(fileName.replace(".names", "")).append("\n\n");
        String linha;
        String geneDecodificado;
        String resultados;
        try (FileReader fr = new FileReader(fileName); BufferedReader br = new BufferedReader(fr)) {
            while (br.ready()) {
                linha = br.readLine();
                geneDecodificado = linha.split("=")[0];
                resultados = linha.split("=")[1];
                synchronizedHashMap.put(geneDecodificado, resultados);
            }

            br.close();
            fr.close();
        }
    }

    private static boolean existeArquivo(String fileName) {
        File f = new File(fileName);
        return f.exists();
    }

}
