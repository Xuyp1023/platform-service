package com.betterjr.modules.workflow.utils;

import com.betterjr.mapper.pagehelper.Page;

public class SnakerPageUtils {

    public static org.snaker.engine.access.Page toSnakerPageForQuery(Page page) {
        org.snaker.engine.access.Page snakerPage = new org.snaker.engine.access.Page();
        snakerPage.setPageNo(page.getPageNum());
        snakerPage.setPageSize(page.getPageSize());
        snakerPage.setTotalCount(page.getTotal());
        return snakerPage;
    }
}
