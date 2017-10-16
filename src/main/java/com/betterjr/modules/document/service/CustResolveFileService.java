package com.betterjr.modules.document.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.modules.document.dao.CustResolveFileMapper;
import com.betterjr.modules.document.entity.CustResolveFile;

@Service
public class CustResolveFileService extends BaseService<CustResolveFileMapper, CustResolveFile> {

    /**
     * 根据文件ID号，查询单个解析记录信息
     *
     * @param id
     * @return
     */
    public CustResolveFile findOne(Long id) {

        BTAssert.notNull(id, "查询失败，原因：主键为空");
        return this.selectByPrimaryKey(id);
    }

    public CustResolveFile saveAddResolveFile(CustResolveFile anFile) {

        BTAssert.notNull(anFile, "插入记录失败，数据为空");
        anFile.initAddVlaue(UserUtils.getOperatorInfo());
        this.insert(anFile);
        return anFile;

    }

    public CustResolveFile saveUpdateResolveFile(CustResolveFile anFile) {

        BTAssert.notNull(anFile, "修改记录失败，数据为空");
        BTAssert.notNull(anFile.getId(), "修改记录失败，数据id为空");
        this.updateByPrimaryKeySelective(anFile);
        return this.selectByPrimaryKey(anFile.getId());
    }

    public void saveUpdateOnlyStatus(Map<String, Object> anResolveFileMap) {

        BTAssert.notNull(anResolveFileMap, "修改记录失败，数据为空");
        Object idObject = anResolveFileMap.get("id");
        BTAssert.notNull(idObject, "修改记录失败，数据为空");
        Long id = Long.parseLong(idObject.toString());
        CustResolveFile resolveFile = this.selectByPrimaryKey(id);
        resolveFile.setBusinStatus(anResolveFileMap.get("businStatus").toString());
        resolveFile.setShowMessage(anResolveFileMap.get("showMessage").toString());
        this.updateByPrimaryKey(resolveFile);
    }
}
