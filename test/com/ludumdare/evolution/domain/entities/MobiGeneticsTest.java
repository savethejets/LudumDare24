package com.ludumdare.evolution.domain.entities;

import junit.framework.TestCase;

import java.util.List;

public class MobiGeneticsTest extends TestCase {
    public void testMate() throws Exception {

        MobiGenetics parentA = new MobiGenetics(MobiGeneticsTypes.line);
        MobiGenetics parentB = new MobiGenetics(MobiGeneticsTypes.Three);

        List<MobiGenetics> children = parentA.mateWith(parentB);

        System.out.println("printing...");

        for (MobiGenetics child : children) {
            for (char[] chars : child.geneticMap) {
                for (char aChar : chars) {
                    System.out.print(aChar == 0 ? "0" : "1");
                }
                System.out.println("");
            }
            System.out.println("------");
        }

        children = children.get(0).mateWith(parentB);

        for (MobiGenetics child : children) {
            for (char[] chars : child.geneticMap) {
                for (char aChar : chars) {
                    System.out.print(aChar == 0 ? "0" : "1");
                }
                System.out.println("");
            }
            System.out.println("------");
        }
    }
}
