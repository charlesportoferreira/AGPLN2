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
public class Mutacao {

    public static Cromossomo muta(Cromossomo c, int posicao) {
        if (posicao >= c.getGenes().size() || posicao < 0) {
            throw new RuntimeException("Posicao para mutacao fora do range de genes");
        }
        Cromossomo clone = new Cromossomo(c.getGenes().size());
        for (int i = 0; i < c.getGenes().size(); i++) {
            clone.getGenes().get(i).setValor(c.getGenes().get(i).getValor());
        }

        if (clone.getGenes().get(posicao).getValor() == 1) {
            clone.getGenes().get(posicao).setValor(0);
        } else {
            clone.getGenes().get(posicao).setValor(1);
        }

        return clone;
        //c.getConfigGenes();
    }

    public static void mutaGene(Cromossomo cromossomo, int posGene) {
        if (posGene >= cromossomo.getGenes().size() || posGene < 0) {
            throw new RuntimeException("Posicao para mutacao fora do range de genes");
        }

        if (cromossomo.getGenes().get(posGene).getValor() == 1) {
            cromossomo.getGenes().get(posGene).setValor(0);
        } else {
            cromossomo.getGenes().get(posGene).setValor(1);
        }

    }

}
