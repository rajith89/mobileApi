package com.udipoc.api.util.image;

import io.mosip.kernel.core.cbeffutil.common.CbeffValidator;
import io.mosip.kernel.core.cbeffutil.entity.BIR;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.BIRType;
import io.mosip.kernel.core.cbeffutil.spi.CbeffUtil;

import java.util.List;
import java.util.Map;

public class CbeffImpl implements CbeffUtil {

    private byte[] xsd;

    /**
     * Method used for creating Cbeff XML.
     *
     * @param birList pass List of BIR for creating Cbeff data
     * @return return byte array of XML data
     * @throws Exception exception
     */
    @Override
    public byte[] createXML(List<BIR> birList) throws Exception {
        return null;
    }

    /**
     * Method used for creating Cbeff XML with xsd.
     *
     * @param birList pass List of BIR for creating Cbeff data
     * @param xsd     byte array of XSD data
     * @return return byte array of XML data
     * @throws Exception Exception
     */

    @Override
    public byte[] createXML(List<BIR> birList, byte[] xsd) throws Exception {
        return null;
    }

    /**
     * Method used for updating Cbeff XML.
     *
     * @param birList   pass List of BIR for creating Cbeff data
     * @param fileBytes the file bytes
     * @return return byte array of XML data
     * @throws Exception Exception
     */
    @Override
    public byte[] updateXML(List<BIR> birList, byte[] fileBytes) throws Exception {
        return null;
    }

    /**
     * Method used for validating XML against XSD.
     *
     * @param xmlBytes byte array of XML data
     * @param xsdBytes byte array of XSD data
     * @return boolean if data is valid or not
     * @throws Exception Exception
     */
    @Override
    public boolean validateXML(byte[] xmlBytes, byte[] xsdBytes) throws Exception {
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see io.mosip.kernel.core.cbeffutil.spi.CbeffUtil#validateXML(byte[])
     */
    @Override
    public boolean validateXML(byte[] xmlBytes) throws Exception {
        return validateXML(xmlBytes, xsd);
    }

    /**
     * Method used for validating XML against XSD.
     *
     * @param fileBytes byte array of XML data
     * @param type      to be searched
     * @param subType   to be searched
     * @return bdbMap Map of type and String of encoded biometric data
     * @throws Exception Exception
     */
    @Override
    public Map<String, String> getBDBBasedOnType(byte[] fileBytes, String type, String subType) throws Exception {
        BIRType bir = CbeffValidator.getBIRFromXML(fileBytes);
        return CbeffValidator.getBDBBasedOnTypeAndSubType(bir, type, subType);
    }

    /**
     * Method used for getting list of BIR from XML bytes.
     *
     * @param xmlBytes byte array of XML data
     * @return List of BIR data extracted from XML
     * @throws Exception Exception
     */
    @Override
    public List<BIRType> getBIRDataFromXML(byte[] xmlBytes) throws Exception {
        BIRType bir = CbeffValidator.getBIRFromXML(xmlBytes);
        return bir.getBIR();
    }

    /**
     * Method used for getting Map of BIR from XML bytes with type and subType.
     *
     * @param xmlBytes byte array of XML data
     * @param type     type
     * @param subType  subType
     * @return bdbMap Map of BIR data extracted from XML
     * @throws Exception Exception
     */
    @Override
    public Map<String, String> getAllBDBData(byte[] xmlBytes, String type, String subType) throws Exception {
        BIRType bir = CbeffValidator.getBIRFromXML(xmlBytes);
        return CbeffValidator.getAllBDBData(bir, type, subType);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * io.mosip.kernel.core.cbeffutil.spi.CbeffUtil#convertBIRTypeToBIR(java.util.
     * List)
     */
    @Override
    public List<BIR> convertBIRTypeToBIR(List<BIRType> birType) {
        return CbeffValidator.convertBIRTypeToBIR(birType);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * io.mosip.kernel.core.cbeffutil.spi.CbeffUtil#getBIRDataFromXMLType(byte[],
     * java.lang.String)
     */
    @Override
    public List<BIRType> getBIRDataFromXMLType(byte[] xmlBytes, String type) throws Exception {
        return CbeffValidator.getBIRDataFromXMLType(xmlBytes, type);
    }
}
