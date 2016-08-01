package com.betterjr.modules.document.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.betterjr.common.config.ParamNames;
import com.betterjr.common.data.KeyAndValueObject;
import com.betterjr.common.exception.BettjerIOException;
import com.betterjr.common.exception.BytterTradeException;
import com.betterjr.common.selectkey.SerialGenerator;
import com.betterjr.common.service.FreemarkerService;
import com.betterjr.common.service.SpringContextHolder;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.FileUtils;
import com.betterjr.common.utils.MimeTypesHelper;
import com.betterjr.modules.document.entity.CustFileItem;
import com.betterjr.modules.sys.service.SysConfigService;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.Pipeline;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CSSResolver;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;

import java.util.*;

public abstract class CustFileUtils {
    private static final Logger logger = LoggerFactory.getLogger(CustFileUtils.class);

    /**
     * 创建签名的文档信息
     * 
     * @param anFile
     *            签名的文件信息
     * @param anType
     *            文件类型
     * @param anFileName
     *            签名的文件名称
     * @param anFilePath
     *            签名的文件路径
     * @return
     */

    public static Long findBatchNo() {

        return SerialGenerator.getLongValue("CustFileInfo.id");
    }

    public static CustFileItem createSignDocFileItem(KeyAndValueObject anFileInfo, String anWorkType, String anFileName) {
        CustFileItem fileItem = createDefFileItem(anFileInfo, anWorkType, anFileName);
        fileItem.setBatchNo(findBatchNo());

        return fileItem;
    }

    private static CustFileItem createDefFileItem(KeyAndValueObject anFileInfo, String anWorkType, String anFileName) {
        CustFileItem fileItem = new CustFileItem();
        fileItem.setId(SerialGenerator.getLongValue("CustFileItem.id"));
        File tmpFile = (File) anFileInfo.getValue();
        fileItem.setAbsoFile(tmpFile);
        fileItem.setFileLength(tmpFile.length());
        fileItem.setFilePath(anFileInfo.getStrKey());
        fileItem.setFileInfoType(anWorkType);
        fileItem.setFileName(anFileName);
        fileItem.setBatchNo(0L);
        fileItem.setFileType(FileUtils.extractFileExt(anFileName));

        return fileItem;
    }
    
    public static CustFileItem createDefFileItemForStore(String filePath,Long fileLength, String anWorkType, String anFileName) {
        CustFileItem fileItem = new CustFileItem();
        fileItem.setId(SerialGenerator.getLongValue("CustFileItem.id"));
        fileItem.setFileLength(fileLength);
        fileItem.setFilePath(filePath);
        fileItem.setFileInfoType(anWorkType);
        fileItem.setFileName(anFileName);
        fileItem.setBatchNo(0L);
        fileItem.setFileType(FileUtils.extractFileExt(anFileName));

        return fileItem;
    }

    /**
     * 创建上传文件的信息
     * 
     * @param anFileInfo
     *            文件路径信息
     * @param anWorkType
     *            文档业务类型
     * @param anFileName
     *            文件名称
     * @param anFileType
     *            文件类型
     * @return
     */
    public static CustFileItem createUploadFileItem(KeyAndValueObject anFileInfo, String anWorkType, String anFileName) {
        CustFileItem fileItem = createDefFileItem(anFileInfo, anWorkType, anFileName);

        return fileItem;
    }
    
}