package controllers.dicom;

import models.jpeg.JpegImage;
import models.request.helper.DicomManager;
import models.response.MessageType;
import models.response.Message;
import org.dcm4che2.data.DicomObject;
import play.Play;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

public class DicomController extends Controller {

    private static final String PATH_FOR_JPEG = Play.application().getFile("public/images/jpeg").getAbsolutePath();
    private static final String PATH_FOR_DICOM = Play.application().getFile("public/images/dicom").getAbsolutePath();

    public static Result uploadDicomAndWriteToS3() {
        Http.MultipartFormData.FilePart dicomFilePart = null;
        File dicomFile = null;
        BufferedImage bufferedJpegImage = null;
        Http.MultipartFormData body = request().body().asMultipartFormData();
        if(body != null)
            dicomFilePart = body.getFile("dicomToJpegFC");
        if(dicomFilePart != null)
            dicomFile = dicomFilePart.getFile();
        if(dicomFile == null)
            return notFound(Json.toJson(new Message(404, "File not found.", MessageType.NOT_FOUND)));
        //CONVERTING DICOM FILE TO JPEG
        bufferedJpegImage = DicomManager.getJpegFromDicom(dicomFile);
        String _fileName = dicomFile.getName();
        if(bufferedJpegImage == null)
            return internalServerError(Json.toJson(new Message(500, "Error in processing your request.", MessageType.INTERNAL_SERVER_ERROR)));
        else {
            //SAVING JPEG TO DISK
            boolean jpegSaved = DicomManager.writeDicomJpegToS3(bufferedJpegImage, _fileName);
            if(jpegSaved)
                return redirect(controllers.routes.Application.gallery());
            else
                return redirect(controllers.routes.Application.index());
        }
    }

    public static Result uploadJpegAndWriteToS3AsDicom() {
        Http.MultipartFormData.FilePart jpegFilePart = null;
        File jpegFile = null;
        DicomObject dicomObject = null;
        Http.MultipartFormData body = request().body().asMultipartFormData();
        if(body != null)
            jpegFilePart = body.getFile("jpegToDicomFC");
        if(jpegFilePart != null)
            jpegFile = jpegFilePart.getFile();
        if(jpegFile == null)
            return notFound(Json.toJson(new Message(404, "File not found.", MessageType.NOT_FOUND)));
        //CONVERTING JPEG FILE TO DICOM
        dicomObject = DicomManager.getDicomObjectFromJpeg(jpegFile);
        if(dicomObject == null)
            return internalServerError(Json.toJson(new Message(500, "Error in processing your request.", MessageType.INTERNAL_SERVER_ERROR)));
        else {
            //SAVING DICOM FILE TO DISK
            boolean dicomSaved = DicomManager.writeDicomToS3(jpegFile, dicomObject);
            if(dicomSaved)
                return ok(Json.toJson(new Message(200, "Dicom successfully saved to " + PATH_FOR_DICOM, MessageType.SUCCESSFUL)));
            else
                return internalServerError(Json.toJson(new Message(500, "Jpeg not saved!", MessageType.INTERNAL_SERVER_ERROR)));
        }
    }

    public static Result uploadJpegAndWriteToS3() {
        Http.MultipartFormData.FilePart jpegFilePart = null;
        File jpegFile = null;
        Http.MultipartFormData body = request().body().asMultipartFormData();
        if(body != null)
            jpegFilePart = body.getFile("jpegFC");
        if(jpegFilePart != null)
            jpegFile = jpegFilePart.getFile();
        if(jpegFile == null)
            return notFound(Json.toJson(new Message(404, "File not found.", MessageType.NOT_FOUND)));
        Http.MultipartFormData formData = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart uploadFilePart = formData.getFile("upload");
        if (uploadFilePart != null) {
            /*S3File s3File = new S3File();
            s3File.name = uploadFilePart.getFilename();
            s3File.file = uploadFilePart.getFile();
            s3File.save();*/
            return ok();
        } else {
            return badRequest("File upload error");
        }
        /*boolean jpegWritten = DicomManager.writeJpegToS3(jpegFile, PATH_FOR_JPEG);
        if(jpegWritten)
            return redirect(controllers.routes.Application.gallery());
        else
            return redirect(controllers.routes.Application.index());*/
    }

    public static Result getJpegFromId(Long id) {
        JpegImage jpegImage = JpegImage.find.byId(id);
        InputStream in = new ByteArrayInputStream(jpegImage.getImageByteArray());
        BufferedImage bi = null;
        File tempFile = null;
        try {
            bi = ImageIO.read(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ok(jpegImage.getImageByteArray()).as("data:image/png;base64");
    }


/*
    public static Result upload() {
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart uploadFilePart = body.getFile("upload");
        if (uploadFilePart != null) {
            S3File s3File = new S3File();
            s3File.name = uploadFilePart.getFilename();
            s3File.file = uploadFilePart.getFile();
            s3File.save();
            return redirect(routes.Application.index());
        }
        else {
            return badRequest("File upload error");
        }
    }
*/

}
