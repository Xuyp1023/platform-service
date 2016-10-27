package com.betterjr.modules.wechat.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.modules.wechat.dao.ScfRelationMapper;
import com.betterjr.modules.wechat.entity.ScfRelation;

/**
 * 供应商和核心企业关系管理
 * 
 * @author zhoucy
 *
 */
@Service("scfRelationService")
public class ScfRelationService extends BaseService<ScfRelationMapper, ScfRelation> {

    private static final Logger logger = LoggerFactory.getLogger(ScfRelationService.class);
}