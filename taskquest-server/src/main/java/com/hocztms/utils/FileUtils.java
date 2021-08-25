package com.hocztms.utils;


import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLEncoder;

@Component
@Data
public class FileUtils {

    //设置上传路径
    public String path = "/springboot/webapp/market/";
//    public String path = "D:\\test\\";

    public Integer uploadPicture(String fileName, MultipartFile file) {
        File uploadFile = new File(path);

        //如果文件夹不存在 创建文件夹
        if (!uploadFile.exists()) {
            uploadFile.mkdirs();
        }
        File upload = new File(path + fileName);
        try {
            file.transferTo(upload);
            BufferedImage bufferedImage = ImageIO.read(upload);
            bufferedImage.getWidth();
            bufferedImage.getHeight();
        } catch (Exception e) {
            upload.delete();
            return 0;
        }
        return 1;


    }

    public void uploadFile(String fileName, MultipartFile file) {
        File uploadFile = new File(path);

        //如果文件夹不存在 创建文件夹
        if (!uploadFile.exists()) {
            uploadFile.mkdirs();
        }
        File upload = new File(path + fileName);
        try {
            file.transferTo(upload);
        } catch (Exception e) {
            upload.delete();
        }
    }


    public void downloadPicture(String picturename, HttpServletResponse response) {
        try {
            String filePath = path + picturename;
            InputStream bis = new BufferedInputStream(new FileInputStream(filePath));
            picturename = URLEncoder.encode(picturename, "UTF-8");
            response.addHeader("Content-Disposition", "attachment;filename=" + picturename);
            response.setContentType("multipart/form-data");
            BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
            int len;
            while ((len = bis.read()) != -1) {
                out.write(len);
                out.flush();
            }
            out.close();
        } catch (Exception e) {
            throw new RuntimeException("下载失败");
        }
    }

    public void downloadFile(String filePath, HttpServletResponse response) {
        try {
            filePath = path + filePath;
            InputStream bis = new BufferedInputStream(new FileInputStream(filePath));

            filePath = URLEncoder.encode(filePath, "UTF-8");
            response.addHeader("Content-Disposition", "attachment;filename=" + filePath);
            response.setContentType("multipart/form-data");
            BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
            int len;
            while ((len = bis.read()) != -1) {
                out.write(len);
                out.flush();
            }
            out.close();
        } catch (Exception e) {
            throw new RuntimeException("下载失败");
        }
    }

    public void deletePicture(String picturename) {
        try {
            String picutrePath = path + picturename;
            File file = new File(picutrePath);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            throw new RuntimeException("图片本地删除失败");
        }
    }


    public boolean checkPictureSuffixName(String suffixName) {
        String suffixList = "JPG,JPEG,PNG,GIF,BMP,jpg,jpeg,png,gif,bmp";
        String suffix = suffixName.substring(suffixName.lastIndexOf(".") + 1);
        return suffixList.contains(suffix);
    }

    public boolean checkFileSize(Long len, int size, String unit) {
        double fileSize = 0;
        switch (unit.toUpperCase()) {
            case "B":
                fileSize = (double) len;
                break;
            case "K":
                fileSize = (double) len / 1024;
                break;
            case "M":
                fileSize = (double) len / 1048576;
                break;
            case "G":
                fileSize = (double) len / 1073741824;
                break;
        }
        return !(size > fileSize);
    }
}
