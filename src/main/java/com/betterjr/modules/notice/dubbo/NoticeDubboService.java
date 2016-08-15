package com.betterjr.modules.notice.dubbo;

import java.util.Map;

import javax.annotation.Resource;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.modules.notice.INoticeService;
import com.betterjr.modules.notice.constant.NoticeConstants;
import com.betterjr.modules.notice.entity.Notice;
import com.betterjr.modules.notice.service.NoticeService;
import com.betterjr.modules.rule.service.RuleServiceDubboFilterInvoker;

@Service(interfaceClass = INoticeService.class)
public class NoticeDubboService implements INoticeService {
    @Resource
    private NoticeService noticeService;

    @Override
    public String webQueryUnreadNotice(Map<String, Object> anParam, int anFlag, int anPageNum, int anPageSize) {
        return AjaxObject.newOkWithPage("未读公告列表-查询成功", noticeService.queryUnreadNotice(anParam, anFlag, anPageNum, anPageSize)).toJson();
    }

    @Override
    public String webQueryReadNotice(Map<String, Object> anParam, int anFlag, int anPageNum, int anPageSize) {
        return AjaxObject.newOkWithPage("已读公告列表-查询成功", noticeService.queryReadNotice(anParam, anFlag, anPageNum, anPageSize)).toJson();
    }

    @Override
    public String webCountUnreadNotice() {
        return AjaxObject.newOk("未读公告数量-查询成功", noticeService.findCountUnreadNotice()).toJson();
    }

    @Override
    public String webFindNotice(Long anId) {
        return AjaxObject.newOk("公告详情-查询成功", noticeService.findNotice(anId)).toJson();
    }

    @Override
    public String webSetDeletedNotice(Long anId) {
        return AjaxObject.newOk("设置公告已删除状态-成功", noticeService.saveSetDeletedNotice(anId)).toJson();
    }
    
    @Override
    public String webSetReadNotice(Long anId) {
        return AjaxObject.newOk("设置公告已读状态-成功", noticeService.saveSetReadNotice(anId)).toJson();
    }

    @Override
    public String webQueryNotice(Map<String, Object> anParam, int anFlag, int anPageNum, int anPageSize) {
        Map<String, Object> param = RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOkWithPage("机构发布公告列表查询-成功", noticeService.queryNotice(param, anFlag, anPageNum, anPageSize)).toJson();
    }
    
    @Override
    public String webAddPublishNotice(Map<String, Object> anParam, String anTargetCust, String anFileList) {
        Notice notice = RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOk("添加发布公告-成功", noticeService.addNotice(notice, anTargetCust, anFileList, NoticeConstants.NOTICE_STATUS_PUBLISHED)).toJson();
    }
    
    @Override
    public String webAddStoreNotice(Map<String, Object> anParam, String anTargetCust, String anFileList) {
        Notice notice = RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOk("添加暂存公告-成功", noticeService.addNotice(notice, anTargetCust, anFileList, NoticeConstants.NOTICE_STATUS_STORED)).toJson();
    }

    @Override
    public String webSavePublishNotice(Map<String, Object> anParam, Long anId, String anTargetCust, String anFileList) {
        Notice notice = RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOk("修改发布公告-成功", noticeService.saveNotice(notice, anId, anTargetCust, anFileList, NoticeConstants.NOTICE_STATUS_PUBLISHED)).toJson();
    }
    
    @Override
    public String webSaveStoreNotice(Map<String, Object> anParam, Long anId, String anTargetCust, String anFileList) {
        Notice notice = RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOk("修改暂存公告-成功", noticeService.saveNotice(notice, anId, anTargetCust, anFileList, NoticeConstants.NOTICE_STATUS_STORED)).toJson();
    }

    @Override
    public String webPublishNotice(Long anId) {
        return AjaxObject.newOk("发布公告-成功", noticeService.savePublishNotice(anId)).toJson();
    }

    @Override
    public String webCancelNotice(Long anId) {
        return AjaxObject.newOk("撤销公告-成功", noticeService.saveCancelNotice(anId)).toJson();
    }

    @Override
    public String webDeleteNotice(Long anId) {
        return AjaxObject.newOk("删除公告-成功", noticeService.saveDeleteNotice(anId)).toJson();
    }
}
