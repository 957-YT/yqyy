package com.example.android.v.service;


import android.app.Activity;
import android.view.View;
import android.widget.ProgressBar;
import com.example.android.v.Music;
import com.example.android.v.musicAdapter;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.List;
import okhttp3.Call;
import okhttp3.Response;

public class NetService {


    public static void getNetUrl(final List<Music> musicArrayList, final musicAdapter musicAdapter, String gd_id) {


        String url = "http://music.163.com/api/playlist/detail/?id=" + gd_id;
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject resp = new JSONObject(response);
                            JSONObject result = resp.getJSONObject("result");
                            JSONArray track = result.getJSONArray("tracks");
                            for (int i = 0; i < track.length(); i++) {
                                JSONObject currMusic = track.getJSONObject(i);
                                String songName = currMusic.getString("name");
                                int ids = currMusic.getInt("id");
                                JSONArray artist = currMusic.getJSONArray("artists");
                                JSONObject currauthor = artist.getJSONObject(0);
                                String author = currauthor.getString("name");
                                JSONObject curralbum = currMusic.getJSONObject("album");
                                String blurPic = curralbum.getString("picUrl");

                                Music music = new Music(ids, songName, author, blurPic);
                                musicArrayList.add(music);
                            }
                            musicAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    public static void getSearchResult(final Activity activity, final List<Music> musicArrayList, final musicAdapter musicAdapter, String searchContent, final ProgressBar progressBar) {
        final String searchUrl = "http://music.163.com/api/search/get/?s=" + searchContent + "&type=1&limit=60";
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = OkHttpUtils.get()
                            .url(searchUrl)
                            .build()
                            .execute();
                    JSONObject resp = new JSONObject(response.body().string());
                    JSONObject result = resp.getJSONObject("result");
                    JSONArray song = result.getJSONArray("songs");
                    for (int i = 0; i < song.length(); i++) {
                        JSONObject currSearch = song.getJSONObject(i);
                        final int ids = currSearch.getInt("id");
                        String songName = currSearch.getString("name");
                        JSONArray artist = currSearch.getJSONArray("artists");
                        JSONObject currauthor = artist.getJSONObject(0);
                        String author = currauthor.getString("name");
                        Response response2 = OkHttpUtils
                                .get()
                                .url("http://music.163.com/api/song/detail/?id=" + ids + "&ids=%5B" + ids + "%5D")
                                .tag(this)
                                .build()
                                .execute();
                        JSONObject respo = new JSONObject(response2.body().string());
                        JSONArray songs = respo.getJSONArray("songs");
                        JSONObject first = songs.getJSONObject(0);
                        JSONObject album = first.getJSONObject("album");
                        String blurPicUrl = album.getString("blurPicUrl");

                        Music music = new Music(ids, songName, author, blurPicUrl);
                        musicArrayList.add(music);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        musicAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        }).start();
//
//
//        final String pic;
//        OkHttpUtils
//                .get()
//                .url(searchUrl)
//                .build()
//                .execute(new StringCallback() {
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//
//                    }
//
//                    @Override
//                    public void onResponse(String response, int id) {
//                        try {
//                            JSONObject resp = new JSONObject(response);
//                            JSONObject result = resp.getJSONObject("result");
//                            JSONArray song = result.getJSONArray("songs");
//                            for (int i = 0; i < song.length(); i++) {
//                                JSONObject currSearch = song.getJSONObject(i);
//                                final int ids = currSearch.getInt("id");
//                                String songName = currSearch.getString("name");
//                                JSONArray artist = currSearch.getJSONArray("artists");
//                                JSONObject currauthor = artist.getJSONObject(0);
//                                String author = currauthor.getString("name");
////                                        try {
////                                            Response responses = OkHttpUtils
////                                                    .get()
////                                                    .url("http://music.163.com/api/song/detail/?id=553755659&ids=%5B553755659%5D")
////                                                    .tag(this)
////                                                    .build()
////                                                    .execute();
////
////                                            JSONObject respo = new JSONObject(String.valueOf(responses));
////                                            JSONArray songs = respo.getJSONArray("songs");
////                                            JSONObject first = songs.getJSONObject(0);
////                                            JSONObject album = first.getJSONObject("album");
////                                            String blurPicUrl = album.getString("blurPicUrl");
////                                            Log.e("---------",blurPicUrl);
////                                        } catch (IOException e) {
////                                            e.printStackTrace();
////                                        } catch (JSONException e) {
////                                            e.printStackTrace();
////                                        }
//
//
//                                Music music = new Music(ids, songName, author);
//                                musicArrayList.add(music);
//                            }
//                            musicAdapter.notifyDataSetChanged();
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
    }

//    public static void getblurPicUrla(int ids, final searchFragment searchFragment) {
//        OkHttpUtils
//                .get()
//                .url("http://music.163.com/api/song/detail/?id=" + ids + "&ids=%5B" + ids + "%5D")
//                .build()
//                .execute(new StringCallback() {
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//
//                    }
//
//                    @Override
//                    public void onResponse(String response, int id) {
//                        try {
//                            JSONObject respo = new JSONObject(response);
//                            JSONArray songs = respo.getJSONArray("songs");
//                            JSONObject first = songs.getJSONObject(0);
//                            String song_name = first.getString("name");
//                            int song_id = first.getInt("id");
//                            JSONArray artist = first.getJSONArray("artists");
//                            JSONObject author = artist.getJSONObject(0);
//                            String author_name = author.getString("name");
//                            JSONObject album = first.getJSONObject("album");
//                            String blurPicUrl = album.getString("blurPicUrl");
//                            Music music = new Music(song_id, song_name, author_name, blurPicUrl);
//                            OpenHelper openHelper = new OpenHelper(searchFragment.getContext());
//                            SQLiteDatabase sqLiteDatabase = openHelper.getWritableDatabase();
//                            SqlService sqlService = new SqlService(sqLiteDatabase);
//                            long i = sqlService.insert(music);
//                            if (i == -1) {
//                                Intent searchplayIntent = new Intent("android.intent.action.search_play");
//                                searchplayIntent.putExtra("id", music.getId());
//                                searchplayIntent.putExtra("song_name", music.getSong_name());
//                                searchplayIntent.putExtra("author", music.getAuthor());
//                                searchplayIntent.putExtra("pic", music.getBlurPic());
//                                searchFragment.getActivity().sendBroadcast(searchplayIntent);
//                            } else {
//                                playService.dialogMusicList.add(music);
//                                BottomDialog.dialogAdapter.notifyDataSetChanged();
//                                Intent searchplayIntent = new Intent("android.intent.action.search_play");
//                                searchplayIntent.putExtra("id", music.getId());
//                                searchplayIntent.putExtra("song_name", music.getSong_name());
//                                searchplayIntent.putExtra("author", music.getAuthor());
//                                searchplayIntent.putExtra("pic", music.getBlurPic());
//                                searchFragment.getActivity().sendBroadcast(searchplayIntent);
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });

//    }
}
