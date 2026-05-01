package com.read.scriptures.manager.alispeech.util;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import java.util.concurrent.LinkedBlockingQueue;

public class AudioPlayer{

    public enum PlayState{
        idle,
        playing,
        pause
    }

    private static String TAG = "AudioPlayer";
    private final int SAMPLE_RATE = 16000;
    private boolean isFinishSend = false;
    private AudioPlayerCallback audioPlayerCallback;
    private LinkedBlockingQueue<byte[]> audioQueue = new LinkedBlockingQueue();
    private PlayState playState ;

    // 初始化播放器
    private int iMinBufSize = AudioTrack.getMinBufferSize(SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT);

    private AudioTrack audioTrack=new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_MONO
            , AudioFormat.ENCODING_PCM_16BIT,
            iMinBufSize*10, AudioTrack.MODE_STREAM) ;
    private byte[] tempData;

    private Thread ttsPlayerThread;


    public AudioPlayer(AudioPlayerCallback callback){
        Log.i(TAG,"Audio Player init!");
        playState = PlayState.idle;
        audioTrack.play();
        audioPlayerCallback = callback;

        ttsPlayerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
//                    Log.i(TAG, "audioQueue-1-size:"+audioQueue.size());
                    if (playState == PlayState.playing) {
                        try {
                            tempData = audioQueue.take();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        audioTrack.write(tempData, 0, tempData.length);
//                        Log.i(TAG, "audioQueue-2-size:"+audioQueue.size());
                        if (isFinishSend && audioQueue.isEmpty()){
                            audioPlayerCallback.playOver();
                            isFinishSend = false;
                        }

                    }else {
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        ttsPlayerThread.start();
    }

    public void setAudioData(byte[] data){
        audioQueue.offer(data);
        //非阻塞
    }

    public void isFinishSend(boolean isFinish){
        isFinishSend = isFinish;
    }

    public boolean isFinishSend() {
        return isFinishSend;
    }

    public void play(){
        playState = PlayState.playing;
        audioTrack.play();
        audioPlayerCallback.playStart();

    }

    public void stop(){
        playState = PlayState.idle;
        audioQueue.clear();
        audioTrack.flush();
        audioTrack.pause();
        audioTrack.stop();
    }
    public void pause(){
        playState = PlayState.pause;
        audioTrack.pause();
    }
    public void resume(){
        audioTrack.play();
        playState = PlayState.playing;
    }

    public void finish(){
        if (isFinishSend && audioQueue.isEmpty()){
            audioPlayerCallback.playOver();
            isFinishSend = false;
        }
    }

}
