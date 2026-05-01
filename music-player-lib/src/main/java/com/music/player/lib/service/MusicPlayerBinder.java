package com.music.player.lib.service;

import android.app.Notification;
import android.os.Binder;

import com.music.player.lib.bean.BaseAudioInfo;
import com.music.player.lib.constants.MusicConstants;
import com.music.player.lib.iinterface.MusicPlayerPresenter;
import com.music.player.lib.listener.MusicPlayerEventListener;
import com.music.player.lib.listener.MusicPlayerInfoListener;

import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2019/3/8
 * Music Service Binder
 * MusicPlayerService 的中间代理人
 */

public class MusicPlayerBinder extends Binder {

    private final MusicPlayerPresenter mPresenter;

    public MusicPlayerBinder(MusicPlayerPresenter presenter) {
        this.mPresenter = presenter;
    }

    public void startPlayMusic(List<?> musicList, int position) {
        if (null != mPresenter) {
            mPresenter.startPlayMusic(musicList, position);
        }
    }

    public void setLockForeground(boolean enable) {
        if (null != mPresenter) {
            mPresenter.setLockForeground(enable);
        }
    }

    public void setNotificationEnable(boolean notificationEnable) {
        if (null != mPresenter) {
            mPresenter.setNotificationEnable(notificationEnable);
        }
    }

    public void setPlayerActivityName(String className) {
        if (null != mPresenter) {
            mPresenter.setPlayerActivityName(className);
        }
    }

    public void setLockActivityName(String className) {
        if (null != mPresenter) {
            mPresenter.setLockActivityName(className);
        }
    }

    public void startPlayMusic(int position) {
        if (null != mPresenter) {
            mPresenter.startPlayMusic(position);
        }
    }

    public void addPlayMusicToTop(BaseAudioInfo audioInfo) {
        if (null != mPresenter) {
            mPresenter.addPlayMusicToTop(audioInfo);
        }
    }

    public void playOrPause() {
        if (null != mPresenter) {
            mPresenter.playOrPause();
        }
    }

    public void pause() {
        if (null != mPresenter) {
            mPresenter.pause();
        }
    }

    public void play() {
        if (null != mPresenter) {
            mPresenter.play();
        }
    }

    public void setLoop(boolean loop) {
        if (null != mPresenter) {
            mPresenter.setLoop(loop);
        }
    }

    public void continuePlay(String sourcePath) {
        if (null != mPresenter) {
            mPresenter.continuePlay(sourcePath);
        }
    }

    public void continuePlay(String sourcePath, int position) {
        if (null != mPresenter) {
            mPresenter.continuePlay(sourcePath, position);
        }
    }

    public void onReset() {
        if (null != mPresenter) mPresenter.onReset();
    }

    public void onStop() {
        if (null != mPresenter) mPresenter.onStop();
    }

    public void updateMusicPlayerData(List<?> audios, int index) {
        if (null != mPresenter) {
            mPresenter.updateMusicPlayerData(audios, index);
        }
    }

    public int setPlayerModel(int model) {
        if (null != mPresenter) {
            return mPresenter.setPlayerModel(model);
        }
        return MusicConstants.MUSIC_MODEL_LOOP;
    }

    public int getPlayerModel() {
        if (null != mPresenter) {
            return mPresenter.getPlayerModel();
        }
        return MusicConstants.MUSIC_MODEL_LOOP;
    }

    public int setPlayerAlarmModel(int model) {
        if (null != mPresenter) {
            return mPresenter.setPlayerAlarmModel(model);
        }
        return MusicConstants.MUSIC_ALARM_MODEL_0;
    }

    public void onSeekTo(long currentTime) {
        if (null != mPresenter) mPresenter.seekTo(currentTime);
    }


    public void playLastMusic() {
        if (null != mPresenter) {
            mPresenter.playLastMusic();
        }
    }

    public void playNextMusic() {
        if (null != mPresenter) {
            mPresenter.playNextMusic();
        }
    }

    public void setPlayIndex(int index) {
        if (null != mPresenter) {
            mPresenter.setPlayIndex(index);
        }
    }

    public int playLastIndex() {
        if (null != mPresenter) {
            return mPresenter.playLastIndex();
        }
        return -1;
    }

    public int playNextIndex() {
        if (null != mPresenter) {
            return mPresenter.playNextIndex();
        }
        return -1;
    }

    public int playRandomNextIndex() {
        if (null != mPresenter) {
            return mPresenter.playRandomNextIndex();
        }
        return -1;
    }

    public boolean isPlaying() {
        if (null != mPresenter) {
            return mPresenter.isPlaying();
        }
        return false;
    }

    public long getDurtion() {
        if (null != mPresenter) {
            return mPresenter.getDurtion();
        }
        return 0;
    }

    public long getCurrentPlayerID() {
        if (null != mPresenter) {
            return mPresenter.getCurrentPlayerID();
        }
        return 0;
    }

    public BaseAudioInfo getCurrentPlayerMusic() {
        if (null != mPresenter) {
            return mPresenter.getCurrentPlayerMusic();
        }
        return null;
    }

    public String getCurrentPlayerHashKey() {
        if (null != mPresenter) {
            return mPresenter.getCurrentPlayerHashKey();
        }
        return "";
    }

    public List<?> getCurrentPlayList() {
        if (null != mPresenter) {
            return mPresenter.getCurrentPlayList();
        }
        return null;
    }

    public void setPlayingChannel(int channel) {
        if (null != mPresenter) {
            mPresenter.setPlayingChannel(channel);
        }
    }


    public int getPlayingChannel() {
        if (null != mPresenter) {
            return mPresenter.getPlayingChannel();
        }
        return MusicConstants.CHANNEL_NET;
    }


    public void onCheckedPlayerConfig() {
        if (null != mPresenter) mPresenter.onCheckedPlayerConfig();
    }

    public void onCheckedCurrentPlayTask() {
        if (null != mPresenter) mPresenter.onCheckedCurrentPlayTask();
    }

    public int getPlayerState() {
        if (null != mPresenter) {
            return mPresenter.getPlayerState();
        }
        return 0;
    }

    public void changedPlayerPlayModel() {
        if (null != mPresenter) {
            mPresenter.changedPlayerPlayModel();
        }
    }

    public void addOnPlayerEventListener(MusicPlayerEventListener listener) {
        if (null != mPresenter) mPresenter.addOnPlayerEventListener(listener);
    }

    public void removePlayerListener(MusicPlayerEventListener listener) {
        if (null != mPresenter) mPresenter.removePlayerListener(listener);
    }

    public void removeAllPlayerListener() {
        if (null != mPresenter) mPresenter.removeAllPlayerListener();
    }

    public void addPlayInfoListener(MusicPlayerInfoListener listener) {
        if (null != mPresenter) mPresenter.addPlayInfoListener(listener);
    }

    public void removePlayInfoListener(MusicPlayerInfoListener listener) {
        if (null != mPresenter) mPresenter.removePlayInfoListener(listener);
    }

    public int getPlayerAlarmModel() {
        if (null != mPresenter) {
            return mPresenter.getPlayerAlarmModel();
        }
        return MusicConstants.MUSIC_ALARM_MODEL_0;
    }

    public void createMiniJukeboxWindow() {
        if (null != mPresenter) {
            mPresenter.createMiniJukeboxWindow();
        }
    }

    public void startServiceForeground() {
        if (null != mPresenter) {
            mPresenter.startServiceForeground();
        }
    }

    public void startServiceForeground(Notification notification) {
        if (null != mPresenter) {
            mPresenter.startServiceForeground(notification);
        }
    }

    public void startServiceForeground(Notification notification, int notifiid) {
        if (null != mPresenter) {
            mPresenter.startServiceForeground(notification, notifiid);
        }
    }

    public void stopServiceForeground() {
        if (null != mPresenter) {
            mPresenter.stopServiceForeground();
        }
    }

    public void createWindowJukebox() {
        if (null != mPresenter) {
            mPresenter.createWindowJukebox();
        }
    }

    public void startNotification() {
        if (null != mPresenter) {
            mPresenter.startNotification();
        }
    }

    public void startNotification(Notification notification) {
        if (null != mPresenter) {
            mPresenter.startNotification(notification);
        }
    }

    public void startNotification(Notification notification, int notifiid) {
        if (null != mPresenter) {
            mPresenter.startNotification(notification, notifiid);
        }
    }

    public void updateNotification() {
        if (null != mPresenter) {
            mPresenter.updateNotification();
        }
    }

    public void cleanNotification() {
        if (null != mPresenter) {
            mPresenter.cleanNotification();
        }
    }

    public void setSpeed(float speed) {
        if (null != mPresenter) {
            mPresenter.setSpeed(speed);
        }
    }

    public List<?> getRandomPlayList() {
        if (null != mPresenter) {
            return mPresenter.getRandomPlayList();
        }
        return null;
    }

    public void setAuthList(ArrayList<String> authors) {
        if (null != mPresenter) {
            mPresenter.setAuthList(authors);
        }
    }

    public void setBookAllAudio(ArrayList<?> allAudio) {
        if (null != mPresenter) {
            mPresenter.setBookAllAudio(allAudio);
        }
    }

    public ArrayList<String> getAuthList() {
        if (null != mPresenter) {
            return mPresenter.getAuthList();
        }
        return null;
    }

    public ArrayList<?> getBookAllAudio() {
        if (null != mPresenter) {
            return mPresenter.getBookAllAudio();
        }
        return null;
    }

    public ArrayList<?> getAllBooks() {
        if (null != mPresenter) {
            return mPresenter.getAllBooks();
        }
        return null;
    }

    public void setAllBooks(ArrayList<?> rows) {
        if (null != mPresenter) {
            mPresenter.setAllBooks(rows);
        }
    }

    public void setBookIndex(int bookIndex) {
        if (null != mPresenter) {
            mPresenter.setBookIndex(bookIndex);
        }
    }

    public int getBookIndex() {
        if (null != mPresenter) {
            return mPresenter.getBookIndex();
        }
        return 0;
    }

    public void removeAllPlayInfoListener() {
        if (null != mPresenter) mPresenter.removeAllPlayInfoListener();
    }

    public void setCurrentAuthor(String author) {
        if (null != mPresenter) {
            mPresenter.setCurrentAuthor(author);
        }
    }

    public String getCurrentAuthor() {
        if (null != mPresenter) {
            return mPresenter.getCurrentAuthor();
        }
        return "";
    }

    public float getSpeed() {
        if (null != mPresenter) {
            return mPresenter.getSpeed();
        }
        return 1.0f;
    }

    public void setAllCate(Object audioCateData) {
        if (null != mPresenter) {
            mPresenter.setAllCate(audioCateData);
        }
    }

    public Object getAllCate() {
        if (null != mPresenter) {
            return mPresenter.getAllCate();
        }
        return null;
    }

    public void setCateIndex(int currentCateIndex) {
        if (null != mPresenter) {
            mPresenter.setCateIndex(currentCateIndex);
        }
    }

    public int getCateIndex() {
        if (null != mPresenter) {
            return mPresenter.getCateIndex();
        }
        return 0;
    }
}