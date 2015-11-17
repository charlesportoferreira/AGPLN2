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
public class ComparadorCromossomo implements java.util.Comparator {

    @Override
    public int compare(Object o1, Object o2) {
        Cromossomo c1 = (Cromossomo) o1;
        Cromossomo c2 = (Cromossomo) o2;
        if (c1.getFitness() > c2.getFitness()) {
            return -1;
        }
        if (c1.getFitness() < c2.getFitness()) {
            return 1;
        }

        return 0;

    }
}
