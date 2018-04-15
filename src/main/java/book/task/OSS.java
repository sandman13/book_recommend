package book.task;

import book.domain.exception.BusinessException;
import book.util.LoggerUtil;
import book.util.ValidateUtils;
import com.aliyun.oss.OSSClient;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
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
            ossClient.putObject(bucketName, ossKey, inputStream);
            LoggerUtil.info(LOGGER,"上传文件成功,ossKey:{0}",ossKey);
            return true;
        }catch (BusinessException ex){
            LoggerUtil.error(ex,LOGGER,"上传文件失败,ossKey:{0}",ossKey);
            return false;
        }finally {
            if (ossClient!=null) {
                ossClient.shutdown();
            }
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
}
