package com.ludumdare.evolution.domain.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MobiGenetics {

    private static final int GENETIC_MAP_SIZE = 3;

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

    public List<MobiGenetics> mateWith(MobiGenetics parentB) {

        MobiGenetics parentA = this;

        List<MobiGenetics> mobiGeneticList = new ArrayList<MobiGenetics>();

        Random random = new Random();

        for (int t = 0; t < 4; t++) {

            MobiGenetics newGenetics = new MobiGenetics();

            for (int i = 0; i < GENETIC_MAP_SIZE; i++) {
                for (int j = 0; j < GENETIC_MAP_SIZE; j++) {
                    if (random.nextBoolean()) {
                        newGenetics.setGeneticMapAt(i, j, parentB.geneticMap[i][j]);
                    } else {
                        newGenetics.setGeneticMapAt(i, j, parentA.geneticMap[i][j]);
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
                } else {
                    pixmap.setColor(Color.GRAY);
                    pixmap.fillRectangle(posXStart, posYStart, widthInc, heightInc);
                }
                posXStart += widthInc;
            }
            posYStart += heightInc;
        }

    }
}
