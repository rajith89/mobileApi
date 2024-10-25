package com.udipoc.api.util.image;

import io.mosip.kernel.core.cbeffutil.jaxbclasses.BIRType;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.SingleType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CbeffToBiometricUtil {
    public byte[] getPhotoByTypeAndSubType(List<BIRType> bIRTypeList, String type, List<String> subType) {
        byte[] photoBytes = null;
        for (BIRType birType : bIRTypeList) {
            if (birType.getBDBInfo() != null) {
                List<SingleType> singleTypeList = birType.getBDBInfo().getType();
                List<String> subTypeList = birType.getBDBInfo().getSubtype();

                boolean isType = isSingleType(type, singleTypeList);
                boolean isSubType = isSubType(subType, subTypeList);

                if (isType && isSubType) {
                    photoBytes = birType.getBDB();
                    break;
                }
            }
        }
        return photoBytes;
    }

    private boolean isSingleType(String type, List<SingleType> singleTypeList) {
        boolean isType = false;
        for (SingleType singletype : singleTypeList) {
            if (singletype.value().equalsIgnoreCase(type)) {
                isType = true;
            }
        }
        return isType;
    }

    private boolean isSubType(List<String> subType, List<String> subTypeList) {
        return subTypeList.equals(subType) ? Boolean.TRUE : Boolean.FALSE;
    }
}
