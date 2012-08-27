package com.ludumdare.evolution.domain.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MobiGenetics {

    public static final int GENETIC_MAP_SIZE = 3;

    char[][] geneticMap;

    public MobiGenetics() {
        geneticMap = new char[GENETIC_MAP_SIZE][GENETIC_MAP_SIZE];
    }

    public MobiGenetics(char[][] geneticMap) {
        this.geneticMap = geneticMap;
    }

    protected void setGeneticMapAt(int i, int j, char value) {
        geneticMap[i][j] = value;
    }

    public char[][] combineAll(MobiGenetics parentB) {

        char[][] map = new char[geneticMap.length][geneticMap.length];
        for (int i = 0; i < geneticMap.length; i++) {
            char[] chars = geneticMap[i];
            for (int j = 0; j < chars.length; j++) {
                char aChar = chars[j];
                if (parentB.geneticMap[i][j] == 1 && aChar == 1) {
                    map[i][j] = 1;
                }
                if (parentB.geneticMap[i][j] == 0 && aChar == 0) {
                    map[i][j] = 0;
                }
            }
        }
        return map;
    }

    public static boolean matchesCompleteMap(char[][] map) {
        for (int i = 0; i < map.length; i++) {
            char[] chars = map[i];
            for (int j = 0; j < chars.length; j++) {
                char aChar = chars[j];
                if (aChar != MobiGeneticsTypes.complete[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    public List<MobiGenetics> mateWith(MobiGenetics parentB) {

        MobiGenetics parentA = this;

        List<MobiGenetics> mobiGeneticList = new ArrayList<MobiGenetics>();

        Random random = new Random();

        for (int t = 0; t < 1; t++) {

            MobiGenetics newGenetics = new MobiGenetics();

            for (int i = 0; i < GENETIC_MAP_SIZE; i++) {
                for (int j = 0; j < GENETIC_MAP_SIZE; j++) {
                    if (parentB.geneticMap[i][j] == 1) {
                        newGenetics.setGeneticMapAt(i, j, parentB.geneticMap[i][j]);
                    } else if (parentA.geneticMap[i][j] == 1) {
                        newGenetics.setGeneticMapAt(i, j, parentA.geneticMap[i][j]);
                    } else if ((parentB.geneticMap[i][j] == 2 || parentA.geneticMap[i][j] == 2) && random.nextBoolean()) {
                        char value = parentB.geneticMap[i][j] == 2 ? 1 : parentB.geneticMap[i][j];
                        newGenetics.setGeneticMapAt(i, j, value);
                    } else {
                        char value = parentA.geneticMap[i][j] == 2 ? 1 : parentA.geneticMap[i][j];
                        newGenetics.setGeneticMapAt(i, j, value);
                    }
                }
            }

            mobiGeneticList.add(newGenetics);
        }

        return mobiGeneticList;
    }

    public void createTexture(Pixmap pixmap) {


        int posXStart = 0;
        int posYStart = 0;

        int widthInc = pixmap.getWidth() / 3;
        int heightInc = pixmap.getHeight() / 3;

        for (int i = 0; i < 3; i++) {
            char[] chars = geneticMap[i];
            for (int j = 0; j < chars.length; j++) {
                char aChar = chars[j];
                if (aChar == 1) {
                    pixmap.setColor(Color.GREEN);
                    pixmap.fillRectangle(posXStart, posYStart, widthInc, heightInc);
                }
                posXStart += widthInc;
            }
            posYStart += heightInc;
        }

    }
}
