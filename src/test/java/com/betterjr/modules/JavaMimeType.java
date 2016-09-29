// Copyright (c) 2014-2016 Bytter. All rights reserved.
// ============================================================================
// CURRENT VERSION
// ============================================================================
// CHANGE LOG
// V2.0 : 2016年9月28日, liuwl, creation
// ============================================================================
package com.betterjr.modules;

import java.io.File;
import java.util.Collection;

import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;

import org.junit.Test;

import eu.medsea.mimeutil.MimeUtil;

/**
 * @author liuwl
 *
 */
public class JavaMimeType {

    @Test
    public void testMime() {
        final File f = new File("D:\\app\\workdata\\contract\\20160928\\6cd7b566cf3240e4aa2e0a497a3940db");
        final FileTypeMap fileTypeMap = MimetypesFileTypeMap.getDefaultFileTypeMap();
        System.out.println(f.getName() + " " + fileTypeMap.getContentType(f));

        MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
        final File f1 = new File ("D:\\\\app\\\\workdata\\\\contract\\\\20160928\\\\6cd7b566cf3240e4aa2e0a497a3940db");
        final Collection<?> mimeTypes = MimeUtil.getMimeTypes(f1);
        System.out.println(mimeTypes);

        final File f2 = new File ("C:\\Users\\liuwl\\Desktop\\abc");
        final Collection<?> mimeTypes2 = MimeUtil.getMimeTypes(f2);
        System.out.println(mimeTypes2);

        final File f3 = new File ("C:\\Users\\liuwl\\Desktop\\def");
        final Collection<?> mimeTypes3 = MimeUtil.getMimeTypes(f3);
        System.out.println(mimeTypes3);
    }

}
