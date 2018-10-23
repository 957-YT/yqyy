package com.example.android.v;

public class MainItem {

    private int Pic;
    private String SongListId;
    private int Bgid;

    public MainItem(int pic,String songListId,int bgid){
        this.Pic = pic;

        this.SongListId = songListId;
        this.Bgid = bgid;
    }

    public int getPic(){
        return Pic;
    }


    public String getSongListid() {
        return SongListId;
    }

    public int getBgid() {
        return Bgid;
    }
}
