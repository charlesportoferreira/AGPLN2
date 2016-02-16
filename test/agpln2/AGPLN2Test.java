/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agpln2;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author charles
 */
public class AGPLN2Test {
    
    public AGPLN2Test() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of criaPopulacaoInicial method, of class AGPLN2.
     */
    @Test
    public void testCriaPopulacaoInicial() {
        System.out.println("criaPopulacaoInicial");
        AGPLN2 instance = new AGPLN2();
        instance.criaPopulacaoInicial();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of cruza method, of class AGPLN2.
     */
    @Test
    public void testCruza() {
        System.out.println("cruza");
        AGPLN2 instance = new AGPLN2();
        instance.cruza();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of muta method, of class AGPLN2.
     */
    @Test
    public void testMuta() {
        System.out.println("muta");
        AGPLN2 instance = new AGPLN2();
        instance.muta();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

//    /**
//     * Test of seleciona method, of class AGPLN2.
//     */
//    @Test
//    public void testSeleciona() {
//        System.out.println("seleciona");
//        int tamanhoPopulacao = 0;
//        AGPLN2 instance = new AGPLN2();
//        instance.seleciona(tamanhoPopulacao);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

//    /**
//     * Test of printFile method, of class AGPLN2.
//     */
//    @Test
//    public void testPrintFile() throws Exception {
//        System.out.println("printFile");
//        String fileName = "";
//        String texto = "";
//        AGPLN2.printFile(fileName, texto);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
    
}
