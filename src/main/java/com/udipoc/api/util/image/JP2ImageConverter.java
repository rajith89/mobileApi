package com.udipoc.api.util.image;

import io.mosip.kernel.core.util.CryptoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

@Component
@Slf4j
public class JP2ImageConverter {

    private String convert(byte[] photoBytes) throws IOException {
        String output = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(photoBytes);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            BufferedImage image = ImageIO.read(bis);
            ImageIO.write(image, "jpg", bos);
            byte[] data = bos.toByteArray();
            output = CryptoUtil.encodeBase64String(data);
        } catch (Exception ex) {
            log.error("Error in convert method-", ex.getMessage());
            throw ex;
        }
        return output;
    }

    private byte[] extract(byte[] decodedBioValue) throws IOException {

        try (DataInputStream din = new DataInputStream(new ByteArrayInputStream(decodedBioValue))) {

            byte[] format = new byte[4];
            din.read(format, 0, 4);
            byte[] version = new byte[4];
            din.read(version, 0, 4);
            int recordLength = din.readInt();
            short numberofRepresentionRecord = din.readShort();
            byte certificationFlag = din.readByte();
            byte[] temporalSequence = new byte[2];
            din.read(temporalSequence, 0, 2);
            int representationLength = din.readInt();
            byte[] representationData = new byte[representationLength - 4];
            din.read(representationData, 0, representationData.length);
            try (DataInputStream rdin = new DataInputStream(new ByteArrayInputStream(representationData))) {
                byte[] captureDetails = new byte[14];
                rdin.read(captureDetails, 0, 14);
                byte noOfQualityBlocks = rdin.readByte();
                if (noOfQualityBlocks > 0) {
                    byte[] qualityBlocks = new byte[noOfQualityBlocks * 5];
                    rdin.read(qualityBlocks, 0, qualityBlocks.length);
                }
                short noOfLandmarkPoints = rdin.readShort();
                byte[] facialInformation = new byte[15];
                rdin.read(facialInformation, 0, 15);
                if (noOfLandmarkPoints > 0) {
                    byte[] landmarkPoints = new byte[noOfLandmarkPoints * 8];
                    rdin.read(landmarkPoints, 0, landmarkPoints.length);
                }
                byte faceType = rdin.readByte();
                byte imageDataType = rdin.readByte();
                byte[] otherImageInformation = new byte[9];
                rdin.read(otherImageInformation, 0, otherImageInformation.length);
                int lengthOfImageData = rdin.readInt();

                byte[] image = new byte[lengthOfImageData];
                rdin.read(image, 0, lengthOfImageData);

                return image;
            }
        } catch (Exception ex) {
            log.error("Error in extract method-", ex.getMessage());
            throw ex;
        }
    }

    public String getjpegImageUrlByPhotoBytes(byte[] photoBytes) throws Exception {
        return convert(extract(photoBytes));
    }
}
