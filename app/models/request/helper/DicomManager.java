package models.request.helper;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import org.dcm4che2.data.*;
import org.dcm4che2.imageio.plugins.dcm.DicomImageReadParam;
import org.dcm4che2.io.DicomInputStream;
import org.dcm4che2.io.DicomOutputStream;
import org.dcm4che2.util.UIDUtils;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Date;
import java.util.Iterator;

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

    public static boolean writeJpegToDisk(BufferedImage dicomJpegImage, String userHomePath) {

        try {
            File myJpegFile = new File(userHomePath + "/DtoJ-" + new Date().getTime() + ".jpg");
            OutputStream output = new BufferedOutputStream(new FileOutputStream(myJpegFile));
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(output);
            encoder.encode(dicomJpegImage);
            output.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean writeDicomToDisk(File jpegFile, DicomObject dicomObject, String userHomePath) {
        try {
            File dicomFile = new File(userHomePath + "/JtoD-" + new Date().getTime() + ".dcm");
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
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
