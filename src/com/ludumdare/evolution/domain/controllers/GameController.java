package com.ludumdare.evolution.domain.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.ludumdare.evolution.domain.entities.Mobi;

import java.util.ArrayList;
import java.util.List;

public class GameController {
    private static GameController ourInstance = new GameController();

    private Mobi currentMobi;
    private List<Music> musics = new ArrayList<Music>();
    private int currentlyPlayingMusicIndex = -1;

    private List<Mobi> otherMobis;

    public static GameController getInstance() {
        return ourInstance;
    }

    private GameController() {
        otherMobis = new ArrayList<Mobi>();
    }

    public void initMusic() {
        musics.add(Gdx.audio.newMusic(Gdx.files.internal("music/song1.mp3")));
        musics.add(Gdx.audio.newMusic(Gdx.files.internal("music/song2.mp3")));
        musics.add(Gdx.audio.newMusic(Gdx.files.internal("music/song3.mp3")));
    }

    public void playMusic(int index) {
        if (!musics.isEmpty()) {
            musics.get(index).play();
            currentlyPlayingMusicIndex = index;
        }
    }

    public void playNextSong() {
        if (!musics.isEmpty()) {
            currentlyPlayingMusicIndex++;

            if (currentlyPlayingMusicIndex > musics.size() - 1) {
                currentlyPlayingMusicIndex = 0;
            }

            musics.get(currentlyPlayingMusicIndex).play();
        }
    }

    public boolean isMusicPlaying() {
        if (!musics.isEmpty()) {
            if (currentlyPlayingMusicIndex != -1) {
                return musics.get(currentlyPlayingMusicIndex).isPlaying();
            }
        }
        return false;
    }

    public Mobi getCurrentMobi() {
        return currentMobi;
    }

    public void setCurrentMobi(Mobi currentMobi) {
        this.currentMobi = currentMobi;
    }
}
