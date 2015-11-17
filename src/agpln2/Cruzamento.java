/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agpln2;

/**
 *
 * @author charleshenriqueportoferreira
 */
public class Cruzamento {

    public static Cromossomo[] cruzaPMX(Cromossomo c1, Cromossomo c2, int inicio, int fim) {
        //System.out.println(inicio + " : " + fim);
        if (fim < inicio) {
            throw new RuntimeException("Valor de fim menor que inicio");
        }
        if (fim > c1.getGenes().size()) {
            throw new RuntimeException("Valor de fim maior que a capacidade do cromossomo: "
                     + " : " + c1.getGenes().size() + " : " + inicio + " : " + fim);
        }
        if (inicio < 0) {
            throw new RuntimeException("Valor de inicio menor que zero");
        }

        //arrumar a configuracao dos genes
        Cromossomo filho1 = new Cromossomo(c1.getGenes().size());

        for (int i = 0; i < filho1.getGenes().size(); i++) {
            if (i >= inicio && i <= fim) {
                filho1.getGenes().get(i).setValor(c1.getGenes().get(i).getValor());
            } else {
                filho1.getGenes().get(i).setValor(c2.getGenes().get(i).getValor());
            }
        }

        Cromossomo filho2 = new Cromossomo(c1.getGenes().size());

        for (int i = 0; i < filho2.getGenes().size(); i++) {
            if (i >= inicio && i <= fim) {
                filho2.getGenes().get(i).setValor(c2.getGenes().get(i).getValor());
            } else {
                filho2.getGenes().get(i).setValor(c1.getGenes().get(i).getValor());
            }
        }
        filho1.getConfigGenes();
        filho2.getConfigGenes();
        Cromossomo[] cr = new Cromossomo[2];
        cr[0] = filho1;
        cr[1] = filho2;

        return cr;
    }

}
