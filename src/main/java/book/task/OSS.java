package book.task;

import book.domain.exception.BusinessException;
import book.util.LoggerUtil;
import book.util.ValidateUtils;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.GetObjectRequest;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Date;

/**
 * @author hui zhang
 * @date 2018-4-12
 */
public class OSS {
    private static final Logger LOGGER = LoggerFactory.getLogger(OSS.class);

    private static final int MAX_FILE_UPLOAD_SIZE=16*1024*1024;

    private static final String FILE_UPLOAD_TIME="upload_time";

    private  final String endPoint;

    private final String accessKeyId;

    private  final String accessKeySecret;

    private  final String bucketName;

    private static OSSClient ossClient;


    public OSS(String endPoint, String accessKeyId, String accessKeySecret, String bucketName) {
        this.endPoint = endPoint;
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
        this.bucketName = bucketName;
        ossClient=new OSSClient(endPoint,accessKeyId,accessKeySecret);
    }

    /**
     * 上传oss文件
     *
     * @param ossKey      文件名称
     * @param inputStream 输入流
     * @return
     */
    public boolean upload(String ossKey, InputStream inputStream) throws BusinessException,IOException {
        ValidateUtils.checkTrue(StringUtils.isNotEmpty(ossKey), "待上传的文件名为空");
        ValidateUtils.checkNotNull(inputStream,"待上传的文件为空");
        ValidateUtils.checkTrue(inputStream.available()<MAX_FILE_UPLOAD_SIZE,"待上传的文件超过16M");
        LoggerUtil.info(LOGGER,"START UPLOAD[ossKey:{0}]",ossKey);
        try {
            Image srcImg = ImageIO.read(inputStream);
            BufferedImage result = new BufferedImage(70, 100, BufferedImage.TYPE_INT_RGB);
            result.getGraphics().drawImage(srcImg.getScaledInstance(70, 100, java.awt.Image.SCALE_SMOOTH), 0, 0, null);
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            String format = achiveFormat(ossKey);
            ImageIO.write(result, format, bs);
            InputStream uploadInputStream = new ByteArrayInputStream(bs.toByteArray());

            ossClient.putObject(bucketName, ossKey, uploadInputStream);
            LoggerUtil.info(LOGGER,"上传文件成功,ossKey:{0}",ossKey);
            return true;
        }catch (BusinessException ex){
            LoggerUtil.error(ex,LOGGER,"上传文件失败,ossKey:{0}",ossKey);
            return false;
        }
    }

    /**
     * 获得OSS生成的图片的URL
     * @param ossKey
     * @return
     */
    public String getURL(String ossKey)
    {
        Date expiration = new Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 365 * 10);
        // 生成URL
        URL url = ossClient.generatePresignedUrl(bucketName, ossKey, expiration);
        if (url != null) {
            return url.toString();
        }
        return null;
    }
    public  String achiveFormat(String pictureName) {
        String[] temp = pictureName.split("\\.");
        if (!StringUtils.equals(temp[temp.length - 1], "png") && (StringUtils.equals(temp[temp.length - 1], "jpg")) && (StringUtils.equals(temp[temp.length - 1], "jpeg"))) {
            throw new BusinessException("格式不符合规范");
        }
        return temp[temp.length - 1];
    }

}


