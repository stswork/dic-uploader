package models.request.helper;

import models.S3File;
import models.jpeg.JpegImage;
import org.dcm4che2.data.*;
import org.dcm4che2.imageio.plugins.dcm.DicomImageReadParam;
import org.dcm4che2.io.DicomInputStream;
import org.dcm4che2.io.DicomOutputStream;
import org.dcm4che2.util.UIDUtils;
import org.joda.time.DateTime;

import javax.imageio.*;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Logger;

public class DicomManager {

    private static final String FORMAT_DICOM = "DICOM";

    public static BufferedImage getJpegFromDicom(File dicomFile) {

        BufferedImage jpegImage = null;
        DicomInputStream dis = null;
        DicomObject dio = null;
        Iterator<ImageReader> imageReaderIterator = ImageIO.getImageReadersByFormatName(FORMAT_DICOM);
        while (imageReaderIterator.hasNext()){
            try {
                ImageReader imageReader = imageReaderIterator.next();
                DicomImageReadParam dirParam = (DicomImageReadParam) imageReader.getDefaultReadParam();
                if(dicomFile == null)
                    return  null;
                ImageInputStream iis = ImageIO.createImageInputStream(dicomFile);
                imageReader.setInput(iis, false);
                jpegImage = imageReader.read(0, dirParam);
                iis.close();
                if(jpegImage == null)
                    return null;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return jpegImage;
    }

    public static DicomObject getDicomObjectFromJpeg(File jpegFile) {
        try {
            BufferedImage jpegImage = ImageIO.read(jpegFile);
            if (jpegImage == null)
                return null;
            int colorComponents = jpegImage.getColorModel().getNumColorComponents();
            int bitsPerPixel = jpegImage.getColorModel().getPixelSize();
            int bitsAllocated = (bitsPerPixel / colorComponents);
            int samplesPerPixel = colorComponents;
            DicomObject dicomObject = new BasicDicomObject();
            dicomObject.putString(Tag.SpecificCharacterSet, VR.CS, "ISO_IR 100");
            dicomObject.putString(Tag.PhotometricInterpretation, VR.CS, samplesPerPixel == 3 ? "YBR_FULL_422" : "MONOCHROME2");
            dicomObject.putInt(Tag.SamplesPerPixel, VR.US, samplesPerPixel);
            dicomObject.putInt(Tag.Rows, VR.US, jpegImage.getHeight());
            dicomObject.putInt(Tag.Columns, VR.US, jpegImage.getWidth());
            dicomObject.putInt(Tag.BitsAllocated, VR.US, bitsAllocated);
            dicomObject.putInt(Tag.BitsStored, VR.US, bitsAllocated);
            dicomObject.putInt(Tag.HighBit, VR.US, bitsAllocated-1);
            dicomObject.putInt(Tag.PixelRepresentation, VR.US, 0);
            dicomObject.putDate(Tag.InstanceCreationDate, VR.DA, new Date());
            dicomObject.putDate(Tag.InstanceCreationTime, VR.TM, new Date());
            dicomObject.putString(Tag.StudyInstanceUID, VR.UI, UIDUtils.createUID());
            dicomObject.putString(Tag.SeriesInstanceUID, VR.UI, UIDUtils.createUID());
            dicomObject.putString(Tag.SOPInstanceUID, VR.UI, UIDUtils.createUID());
            dicomObject.initFileMetaInformation(UID.JPEGBaseline1);
            return dicomObject;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean writeDicomJpegToS3(BufferedImage dicomJpegImage, String fileName) {

        try {
            String tempDir = System.getProperty("user.home");
            String separator = File.separator;
            File dicomJpegFile = new File(tempDir + separator + fileName + new DateTime().millisOfDay() + ".jpg");
            ImageIO.write(dicomJpegImage, "jpeg", dicomJpegFile);
            S3File s3File = new S3File();
            s3File.name = dicomJpegFile.getName();
            s3File.file = dicomJpegFile;
            s3File.save();
            dicomJpegFile.delete();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean writeDicomToS3(File jpegFile, DicomObject dicomObject) {
        try {
            String tempDir = System.getProperty("user.home");
            String separator = File.separator;
            File dicomFile = new File(tempDir + separator + jpegFile.getName() + new DateTime().millisOfDay() + ".dcm");
            FileOutputStream fos = new FileOutputStream(dicomFile);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            DicomOutputStream dos = new DicomOutputStream(bos);
            dos.writeDicomFile(dicomObject);
            dos.writeHeader(Tag.PixelData, VR.OB, -1);
            dos.writeHeader(Tag.Item, null, 0);
            int jpgLen = (int) jpegFile.length();
            dos.writeHeader(Tag.Item, null, (jpgLen+1)&~1);
            FileInputStream fis = new FileInputStream(jpegFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            DataInputStream dis = new DataInputStream(bis);
            byte[] buffer = new byte[65536];
            int b;
            while ((b = dis.read(buffer)) > 0) {
                dos.write(buffer, 0, b);
            }
            if ((jpgLen&1) != 0)
                dos.write(0);
            dos.writeHeader(Tag.SequenceDelimitationItem, null, 0);
            dos.close();
            S3File s3File = new S3File();
            s3File.name = dicomFile.getName();
            s3File.file = dicomFile;
            s3File.save();
            dicomFile.delete();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean writeJpegToS3(File jpegFile) {
        try {
            String tempDir = System.getProperty("user.home");
            String separator = File.separator;
            File jFile = new File(tempDir + separator + jpegFile.getName() + new DateTime().millisOfDay() + ".jpg");
            BufferedImage bufferedImage = ImageIO.read(jpegFile);
            ImageIO.write(bufferedImage, "jpeg", jFile);
            S3File s3File = new S3File();
            s3File.name = jFile.getName();
            s3File.file = jFile;
            s3File.save();
            jFile.delete();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
