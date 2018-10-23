package com.example.android.v;

public class Music {
    private int Id;
    private String Song_name;
    private String Author;
    private String BlurPic;

    public Music(int id,String song_name,String author){
        Id = id;
        Song_name = song_name;
        Author = author;
    }

    public Music(int id,String song_name,String author,String blurPic){
        Id = id;
        Song_name = song_name;
        Author = author;
        BlurPic = blurPic;
    }


    public int getId() {
        return Id;
    }

    public String getSong_name() {
        return Song_name;
    }

    public String getAuthor() {
        return Author;
    }

    public String getBlurPic() {
        return BlurPic;
    }
}
