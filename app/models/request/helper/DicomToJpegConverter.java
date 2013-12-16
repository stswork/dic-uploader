package models.request.helper;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.imageio.plugins.dcm.DicomImageReadParam;
import org.dcm4che2.imageioimpl.plugins.dcm.DicomImageReader;
import org.dcm4che2.io.DicomInputStream;
import play.Logger;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Iterator;

public class DicomToJpegConverter {

    private static final String FORMAT_DICOM = "DICOM";

    public static BufferedImage getJpegFromDicom(File dicomFile) {

        BufferedImage jpegImage = null;
        DicomInputStream dis = null;
        DicomObject dio = null;
        Iterator<ImageReader> imageReaderIterator = ImageIO.getImageReadersByFormatName(FORMAT_DICOM);
        while (imageReaderIterator.hasNext()){
            try {
                dis = new DicomInputStream(dicomFile);
                dio = dis.readDicomObject();
                dis.close();
                Logger.info("DICOM FILE META INFO : " + dio.fileMetaInfo());
                Logger.info("DICOM BIG EDIAN : " + dio.bigEndian());
                Logger.info("DICOM CACHE GET : " + dio.cacheGet());
                Logger.info("DICOM SIZE : " + dio.size());

                ImageReader imageReader = imageReaderIterator.next();
                DicomImageReadParam dirParam = (DicomImageReadParam) imageReader.getDefaultReadParam();
                if(dicomFile == null)
                    return jpegImage;
                ImageInputStream iis = ImageIO.createImageInputStream(dicomFile);
                imageReader.setInput(iis, false);
                jpegImage = imageReader.read(0, dirParam);
                iis.close();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return jpegImage;
    }
/*
    public static boolean writeJpegToDisk(BufferedImage jpegImage) {

        try {
            File jpegFile = new File("c:/jpegImage_" + new Date().getTime() + ".jpg");
            OutputStream os = new BufferedOutputStream(new FileOutputStream(jpegFile));
            JPEGImageEncoder jpegImageEncoder = JPEGCodec.createJPEGEncoder(os);
            jpegImageEncoder.encode(jpegImage);
            os.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }*/
}
