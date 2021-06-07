package com.heaven7.study.utils;

import com.heaven7.java.base.util.FileUtils;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public final class ZipHelper {

    private final static String TAG = "ZipHelper";
    private final static int BUFF_SIZE = 2048;
    /**
     * 压缩文件
     *
     * @param fs          需要压缩的文件
     * @param zipFilePath 被压缩后存放的路径
     * @return 成功返回 true，否则 false
     */
    public static boolean zipFiles(List<File> fs, String zipFilePath) {
        if (fs == null) {
            throw new NullPointerException("fs == null");
        }
        File zipFile = new File(zipFilePath);
        if(!zipFile.getParentFile().exists()){
            zipFile.mkdirs();
        }else if(zipFile.exists()){
            zipFile.delete();
        }
        boolean result = false;
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFilePath)));
            for (File file : fs) {
                if (file == null || !file.exists()) {
                    continue;
                }
                if (file.isDirectory()) {
                    recursionZip(zos, file, file.getName() + File.separator);
                } else {
                    //recursionZip(zos, file, "");
                    String dir = FileUtils.getFileDir(file.getAbsolutePath(), 1, false);
                    recursionZip(zos, file, dir + "_");
                }
            }
            zos.flush();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (zos != null) {
                    zos.closeEntry();
                    zos.close();
                }
            } catch (IOException e1) {
            }
        }
        return result;
    }


    private static void recursionZip(ZipOutputStream zipOut, File file, String baseDir) throws Exception {
        if (file.isDirectory()) {
            //System.out.println("the file is dir name -->>" + file.getName() + " the baseDir-->>>" + baseDir);
            File[] files = file.listFiles();
            for (File fileSec : files) {
                if (fileSec == null) {
                    continue;
                }
                if (fileSec.isDirectory()) {
                    baseDir = file.getName() + File.separator + fileSec.getName() + File.separator;
                   // System.out.println("basDir111-->>" + baseDir);
                    recursionZip(zipOut, fileSec, baseDir);
                } else {
                   // System.out.println( "basDir222-->>" + baseDir);
                    recursionZip(zipOut, fileSec, baseDir);
                }
            }
        } else {
           // System.out.println("the file name is -->>" + file.getName() + " the base dir -->>" + baseDir);
            byte[] buf = new byte[BUFF_SIZE];
            InputStream input = new BufferedInputStream(new FileInputStream(file));
            zipOut.putNextEntry(new ZipEntry(baseDir + file.getName()));
            int len;
            while ((len = input.read(buf)) != -1) {
                zipOut.write(buf, 0, len);
            }
            input.close();
        }
    }

    /**
     * 解压文件
     *
     * @param unZipPath 解压后的目录
     * @param zipPath   压缩文件目录
     * @return 成功返回 true，否则 false
     */
    public static boolean unZipFile(String unZipPath, String zipPath) {
        unZipPath = createSeparator(unZipPath);
        BufferedOutputStream bos = null;
        ZipInputStream zis = null;

        boolean result = false;

        try {
            String filename;
            zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipPath)));
            ZipEntry ze;
            byte[] buffer = new byte[BUFF_SIZE];
            int count;

            while ((ze = zis.getNextEntry()) != null) {
                filename = ze.getName();
                createSubFolders(filename, unZipPath);
                if (ze.isDirectory()) {
                    File fmd = new File(unZipPath + filename);
                    fmd.mkdirs();
                    continue;
                }
                bos = new BufferedOutputStream(new FileOutputStream(unZipPath + filename));
                while ((count = zis.read(buffer)) != -1) {
                    bos.write(buffer, 0, count);
                }
                bos.flush();
                bos.close();
            }
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (zis != null) {
                    zis.closeEntry();
                    zis.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private static void createSubFolders(String filename, String path) {
        String[] subFolders = filename.split("/");
        if (subFolders.length <= 1) {
            return;
        }

        String pathNow = path;
        for (int i = 0; i < subFolders.length - 1; ++i) {
            pathNow = pathNow + subFolders[i] + "/";
            File fmd = new File(pathNow);
            if (fmd.exists()) {
                continue;
            }
            fmd.mkdirs();
        }
    }

    private static String createSeparator(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (path.endsWith("/")) {
            return path;
        }
        return path + '/';
    }


}