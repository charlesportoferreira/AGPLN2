/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agpln2;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author debora
 */
public class Teste {

    public static void main(String args[]) {
        System.out.println("teste hashmap");
        Map<String, Double> mapa = new HashMap<>();
        Map<String, Double> synchronizedHashMap;
        synchronizedHashMap = Collections.synchronizedMap(new HashMap<String, Double>());

        synchronizedHashMap.put("ingl", 90.0);
        if (synchronizedHashMap.containsKey("ingl")) {
            System.out.println(synchronizedHashMap.get("ingl"));
        }

       // Resultado res = new Resultado();//
      //  res.numAtributos = 10000;
      //  res.microAverage = 20;
      //  res.macroAverage = 40;
      //  res.pctAcerto = 80;

        ObjectOutputStream outputStream;
        try {
            outputStream = new ObjectOutputStream(new FileOutputStream("testbanco.txt"));
         //   outputStream.writeObject(res);
           // res = new Resultado();
        //    res.macroAverage = 100000;
        //    outputStream.writeObject(res);
            outputStream.flush();
            outputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(Teste.class.getName()).log(Level.SEVERE, null, ex);
        }

        ObjectInputStream inputStream;

//        try {
//            inputStream = new ObjectInputStream(new FileInputStream("testbanco.txt"));
//         //   res = (Resultado) inputStream.readObject();
//            System.out.println("Dado lido");
//         //   System.out.println(res);
//         //   res = (Resultado) inputStream.readObject();
//            System.out.println("Dado lido");
//         //   System.out.println(res);
//            inputStream.close();
//        } catch (IOException | ClassNotFoundException ex) {
//            Logger.getLogger(Teste.class.getName()).log(Level.SEVERE, null, ex);
//        }

    }
}
