package com.betterjr.modules.customer.constant;

public interface CustomerConstants {
    // 状态
    String ITEM_ENABLED = "1";
    String ITEM_DISABLED = "0";
    
    // 审核状态
    String NORMAL_STATUS = "0";
    String PASS_STATUS = "1";
    String REJECT_STATUS = "2";
    
    // 审核类型: 0开户审核 1代录申请审核 2代录审核 3变更审核 4客户关系审核
    String AUDIT_TYPE_OPENACCOUNT = "0";
    String AUDIT_TYPE_INSTEADAPPLY = "1";
    String AUDIT_TYPE_INSTEADRECORD = "2";
    String AUDIT_TYPE_CHANGEAPPLY = "3";
    String AUDIT_TYPE_CUSTRELATION = "4";
    
    // 审核结果: 0审核通过 1审核驳回
    String AUDIT_RESULT_PASS = "0";
    String AUDIT_RESULT_REJECT = "1";
    String AUDIT_RESULT_CANCEL = "2";

    // 流水使用状态: 0未使用  1使用中  2已使用
    String TMP_STATUS_NEW = "0";
    String TMP_STATUS_USEING = "1";
    String TMP_STATUS_USED = "2";

    // 流水类型:0 代录 1 变更 2 暂存  3 初始信息
    String TMP_TYPE_INSTEAD = "0";
    String TMP_TYPE_CHANGE = "1";
    String TMP_TYPE_TEMPSTORE = "2";
    String TMP_TYPE_INITDATA = "3";
    String TMP_TYPE_INSTEADSTORE = "4";

    // 流水操作类型:0 新增 1 修改 2 删除 3 未改变
    String TMP_OPER_TYPE_ADD = "0";
    String TMP_OPER_TYPE_MODIFY = "1";
    String TMP_OPER_TYPE_DELETE = "2";
    String TMP_OPER_TYPE_NORMAL = "3";

    // 变更类型: 0公司基本信息，1法人信息，2股东信息，3高管信息，4营业执照，5联系人信息，6银行账户
    // 代录项目: 0公司基本信息，1法人信息，2股东信息，3高管信息，4营业执照，5联系人信息，6银行账户 7开户代录
    String ITEM_BASE = "0";
    String ITEM_LAW = "1";
    String ITEM_SHAREHOLDER = "2";
    String ITEM_MANAGER = "3";
    String ITEM_BUSINLICENCE = "4";
    String ITEM_CONTACTER = "5";
    String ITEM_BANKACCOUNT = "6";
    String ITEM_OPENACCOUNT = "7";

    // 变更申请状态：0未审核 1审核通过 2审核驳回
    String CHANGE_APPLY_STATUS_NEW = "0";
    String CHANGE_APPLY_STATUS_AUDIT_PASS = "1";
    String CHANGE_APPLY_STATUS_AUDIT_REJECT = "2";
    String CHANGE_APPLY_STATUS_CANCEL = "3";

    // 申请类型：0开户代录，1变更代录
    String INSTEAD_APPLY_TYPE_OPENACCOUNT = "0";
    String INSTEAD_APPLY_TYPE_CHANGE = "1";

    // 代录申请状态：0未受理 1已审核待录入 2审核驳回 3已录入待复核 4已复核待确认 5复核驳回 6 确认通过 7 确认驳回 8 资料作废
    String INSTEAD_APPLY_STATUS_NEW = "0";
    String INSTEAD_APPLY_STATUS_AUDIT_PASS = "1";
    String INSTEAD_APPLY_STATUS_AUDIT_REJECT = "2";
    String INSTEAD_APPLY_STATUS_TYPE_IN = "3";
    String INSTEAD_APPLY_STATUS_REVIEW_PASS = "4";
    String INSTEAD_APPLY_STATUS_REVIEW_REJECT = "5";
    String INSTEAD_APPLY_STATUS_CONFIRM_PASS = "6";
    String INSTEAD_APPLY_STATUS_CONFIRM_REJECT = "7";
    String INSTEAD_APPLY_STATUS_CANCEL = "8";

    // 代录项目状态 ：0未录入 1已录入待复核 2已复核待确认 3复核驳回 4确认通过 5确认驳回 6资料作废
    String INSTEAD_RECORD_STATUS_NEW = "0";
    String INSTEAD_RECORD_STATUS_TYPE_IN = "1";
    String INSTEAD_RECORD_STATUS_REVIEW_PASS = "2";
    String INSTEAD_RECORD_STATUS_REVIEW_REJECT = "3";
    String INSTEAD_RECORD_STATUS_CONFIRM_PASS = "4";
    String INSTEAD_RECORD_STATUS_CONFIRM_REJECT = "5";
    String INSTEAD_RECORD_STATUS_CANCEL = "5";

    // 字段
    String ID = "id";
    String CUST_NO = "custNo";

    /**
     * 关联关系类型:0-供应商与保理公司;
     */
    String RELATE_TYPE_SUPPLIER_FACTOR = "0";
    /**
     * 关联关系类型:1-供应商与核心企业;
     */
    String RELATE_TYPE_SUPPLIER_CORE = "1";
    /**
     * 关联关系类型:2-核心企业与保理公司;
     */
    String RELATE_TYPE_CORE_FACTOR = "2";
    /**
     * 关联关系类型:3-经销商与保理公司;
     */
    String RELATE_TYPE_SELLER_FACTOR = "3";
    /**
     * 关联关系类型:4-经销商与核心企业;
     */
    String RELATE_TYPE_SELLER_CORE = "4";
    /**
     * 关联关系类型:5-客户(供应商,经销商,核心企业,保理机构)与电子合同服务商
     */
    String RELATE_TYPE_ELEC_CONTRACT = "5";
    /**
     * 客户白名单状态：0-未开通;
     */
    String RELATE_STATUS_UNDO = "0";
    /**
     * 客户白名单状态：1-已申请;
     */
    String RELATE_STATUS_APPLY = "1";
    /**
     * 客户白名单状态：2-已受理;
     */
    String RELATE_STATUS_ACCEPT = "2";
    /**
     * 客户白名单状态：3-已审核;
     */
    String RELATE_STATUS_AUDIT = "3";
    /**
     * 客户白名单状态：4-已驳回;
     */
    String RELATE_STATUS_REFUSE = "4";
    /**
     * 开通保理融资业务状态:0-未开通保理融资业务
     */
    String FACTOR_STATUS_UNDO = "0";
    /**
     * 开通保理融资业务状态:1-可开通保理融资业务
     */
    String FACTOR_STATUS_WAIT = "1";
    /**
     * 开通保理融资业务状态:2-已开通保理融资业务
     */
    String FACTOR_STATUS_DONE = "2";
    /**
     * 开通保理融资业务申请:0-申请提交成功;
     */
    String FACTOR_STATUS_SUCCESS = "0";
    /**
     * 开通保理融资业务申请:1-申请提交失败;
     */
    String FACTOR_STATUS_FAILD = "1";
}
