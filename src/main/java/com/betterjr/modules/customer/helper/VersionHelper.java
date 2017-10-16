package com.betterjr.modules.customer.helper;

public final class VersionHelper {

    /**
     * 
     * @param anVersionMapper
     * @param anRefId
     * @return
     */
    public static Long generateVersion(IVersionMapper anVersionMapper, Long anRefId) {
        Long maxVersion = anVersionMapper.selectMaxVersion(anRefId);

        if (maxVersion == null) {
            maxVersion = 1L;
        } else {
            maxVersion += 1;
        }

        return maxVersion;
    }

}
