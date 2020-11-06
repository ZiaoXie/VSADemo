package com.zzy.vsa.demo.appcase.filemanager;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.zzy.vsa.demo.appenv.AppEnv;
import com.zzy.vsa.demo.bean.FileBean;
import com.zzy.vsa.demo.appenv.FileType;
import com.zzy.vsa.demo.util.FileUtil;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class FileQueryHelper {


    private Context mContext;
    private ContentResolver mContentResolver;


    public FileQueryHelper(Context context) {
        mContext = context;
        mContentResolver = context.getContentResolver();
    }


    /**
     * 获取本机音乐列表
     *
     * @return
     */
    public List<FileBean> getMusics() {
        ArrayList<FileBean> musics = new ArrayList<>();
        Cursor c = null;
        try {
            c = mContentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                    MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

            while (c.moveToNext()) {
                String path = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));// 路径
                File f = new File(path);
                if (!f.exists()) {
                    continue;
                }

                FileBean fileBean = new FileBean();
                fileBean.setName(f.getName());
                fileBean.setPath(f.getAbsolutePath());
                fileBean.setFileType(FileUtil.getFileType(f));
                fileBean.setChildCount(FileUtil.getFileChildCount(f));
                fileBean.setSize(f.length());

                musics.add(fileBean);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return musics;
    }

    /**
     * 获取本机视频列表
     *
     * @return
     */
    public List<FileBean> getVideos() {

        List<FileBean> videos = new ArrayList<FileBean>();

        Cursor c = null;
        try {
            c = mContentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);
            while (c.moveToNext()) {
                String path = c.getString(c.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));// 路径
                File f = new File(path);
                if (!f.exists()) {
                    continue;
                }

                FileBean fileBean = new FileBean();
                fileBean.setName(f.getName());
                fileBean.setPath(f.getAbsolutePath());
                fileBean.setFileType(FileUtil.getFileType(f));
                fileBean.setChildCount(FileUtil.getFileChildCount(f));
                fileBean.setSize(f.length());

                videos.add(fileBean);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return videos;
    }

    // 获取视频缩略图
    public Bitmap getVideoThumbnail(int id) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        bitmap = MediaStore.Video.Thumbnails.getThumbnail(mContentResolver, id, MediaStore.Images.Thumbnails.MICRO_KIND, options);
        return bitmap;
    }

    /**
     * 得到图片文件夹集合
     */
    public List<FileBean> getImageFolders() {
        List<FileBean> folders = new ArrayList<FileBean>();
        HashMap<String,Integer> map =new HashMap<String,Integer>();//HaspMap统计出现次数

        Cursor c = null;
        try {
            c = mContentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
                    null, null, MediaStore.Images.Media.DATE_MODIFIED);
            while (c.moveToNext()) {
                String image = c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA));// 路径

//                File f = new File(image).getParentFile();
//                if (f == null || f.list() == null || !f.exists()){
//                    continue;
//                }
//                String path = f.getAbsolutePath();

                String path = image.substring(0,image.lastIndexOf(File.separator));

                Integer count = map.get(path);
                if(count==null){
                    map.put(path, 1);
                }else{
                    map.put(path, count+1);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }

        Set<String> foldersSet =map.keySet();
        for(String folder: foldersSet){
            File f = new File(folder);
            FileBean fileBean = new FileBean();
            fileBean.setName(f.getName());
            fileBean.setPath(f.getAbsolutePath());
            fileBean.setFileType( FileType.directory );
            fileBean.setChildCount( map.get(folder) );
            fileBean.setSize( f.length() );

            folders.add(fileBean);
        }

        return folders;
    }

    /**
     * 通过图片文件夹的路径获取该目录下的图片
     */
    public List<FileBean> getImgListByDir(String dir) {
        ArrayList<FileBean> imgPaths = new ArrayList<>();
        File directory = new File(dir);
        if (directory == null || !directory.exists()) {
            return imgPaths;
        }
        File[] files = directory.listFiles();
        for (File f : files) {
            String path = f.getAbsolutePath();
            if (FileUtil.getFileType(path) == FileType.image) {
                FileBean fileBean = new FileBean();
                fileBean.setName(f.getName());
                fileBean.setPath(f.getAbsolutePath());
                fileBean.setFileType(FileType.image);
                fileBean.setSize(f.length());
                imgPaths.add(fileBean);
            }
        }
        return imgPaths;
    }

    /*
    * 获取本机文档列表
    * */
    public List<FileBean> getDocuments(){
        List<FileBean> documents = new ArrayList<FileBean>();

        Cursor c = null;
        try {
            c = mContentResolver.query(MediaStore.Files.getContentUri("external"), null,
                    "(" + MediaStore.Files.FileColumns.MIME_TYPE + "=='text/plain')", null, null);
            while (c.moveToNext()) {
                String path = c.getString(c.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));// 路径
                File f = new File(path);
                if (!f.exists()) {
                    continue;
                }

                FileBean fileBean = new FileBean();
                fileBean.setName(f.getName());
                fileBean.setPath(f.getAbsolutePath());
                fileBean.setFileType(FileUtil.getFileType(f));
                fileBean.setChildCount(FileUtil.getFileChildCount(f));
                fileBean.setSize(f.length());

                documents.add(fileBean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return documents;
    }

    /*
     * 获取本机压缩包列表
     * */
    public List<FileBean> getArchives(){
        List<FileBean> archives = new ArrayList<FileBean>();

        Cursor c = null;
        try {
            c = mContentResolver.query(MediaStore.Files.getContentUri("external"), null,
                    "(" + MediaStore.Files.FileColumns.MIME_TYPE + "=='application/zip' or " + MediaStore.Files.FileColumns.MIME_TYPE + "=='application/rar' )", null, null);
            while (c.moveToNext()) {
                String path = c.getString(c.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));// 路径
                File f = new File(path);
                if (!f.exists()) {
                    continue;
                }

                FileBean fileBean = new FileBean();
                fileBean.setName(f.getName());
                fileBean.setPath(f.getAbsolutePath());
                fileBean.setFileType(FileUtil.getFileType(f));
                fileBean.setChildCount(FileUtil.getFileChildCount(f));
                fileBean.setSize(f.length());

                archives.add(fileBean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return archives;
    }

    /*
     * 获取本机所有按安装包
     * */
    public List<FileBean> getInstallions(){
        List<FileBean> installions = new ArrayList<FileBean>();

        Cursor c = null;
        try {
            c = mContentResolver.query(MediaStore.Files.getContentUri("external"), null,
                    "(" + MediaStore.Files.FileColumns.DATA + " LIKE '%.apk' )", null, null);
            while (c.moveToNext()) {
                String path = c.getString(c.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));// 路径
                File f = new File(path);
                if (!f.exists()) {
                    continue;
                }

                FileBean fileBean = new FileBean();
                fileBean.setName(f.getName());
                fileBean.setPath(f.getAbsolutePath());
                fileBean.setFileType(FileUtil.getFileType(f));
                fileBean.setChildCount(FileUtil.getFileChildCount(f));
                fileBean.setSize(f.length());

                installions.add(fileBean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return installions;
    }

    /*
    *
    * */
    public List<FileBean> getFilesFromLocalProvider(){
        List<FileBean> files = new ArrayList<FileBean>();

        Cursor c = null;
        try {
            c = mContentResolver.query(Uri.parse("content://com.zzy.vsa.demo.fileprovider/external_files/com.zzy.vsa.demo"), null,
                    null, null, null);
            while (c.moveToNext()) {
//                String path = c.getString(c.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));// 路径
//                File f = new File(path);
//                if (!f.exists()) {
//                    continue;
//                }
//
//                FileBean fileBean = new FileBean();
//                fileBean.setName(f.getName());
//                fileBean.setPath(f.getAbsolutePath());
//                fileBean.setFileType(FileUtil.getFileType(f));
//                fileBean.setChildCount(FileUtil.getFileChildCount(f));
//                fileBean.setSize(f.length());
//
//                files.add(fileBean);
                Log.i("provider",c.getString(0));
            }
            Log.i("provider",c.getCount()+"列数:"+c.getColumnCount());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return files;

    }


    /**
     * 通过文件类型得到相应文件的集合
     **/
    private List<FileBean> getFilesByType(FileType fileType) {
        List<FileBean> files = new ArrayList<FileBean>();
        // 扫描files文件库
        Cursor c = null;
        try {
            c = mContentResolver.query(MediaStore.Files.getContentUri("external"), new String[]{"_id", "_data", "_size"}, null, null, null);
            int dataindex = c.getColumnIndex(MediaStore.Files.FileColumns.DATA);
            int sizeindex = c.getColumnIndex(MediaStore.Files.FileColumns.SIZE);

            while (c.moveToNext()) {
                String path = c.getString(dataindex);

                if (FileUtil.getFileType(path) == fileType) {
                    if (!new File(path).exists()) {
                        continue;
                    }
                    long size = c.getLong(sizeindex);
                    FileBean fileBean = new FileBean();
                    fileBean.setPath(path);
                    files.add(fileBean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return files;
    }


}
