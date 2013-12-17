package controllers.dicom;

import models.request.helper.DicomManager;
import models.response.MessageType;
import models.response.Message;
import org.dcm4che2.data.DicomObject;
import play.Play;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import java.awt.image.BufferedImage;
import java.io.File;

public class DicomController extends Controller {

    private static final String PATH_FOR_JPEG = Play.application().getFile("public/images/jpeg").getAbsolutePath();
    private static final String PATH_FOR_DICOM = Play.application().getFile("public/images/dicom").getAbsolutePath();

    public static Result uploadDicomAndWriteToDiskAsJpeg() {
        Http.MultipartFormData.FilePart dicomFilePart = null;
        File dicomFile = null;
        BufferedImage bufferedJpegImage = null;
        Http.MultipartFormData body = request().body().asMultipartFormData();
        if(body != null)
            dicomFilePart = body.getFile("dicomFC");
        if(dicomFilePart != null)
            dicomFile = dicomFilePart.getFile();
        if(dicomFile == null)
            return notFound(Json.toJson(new Message(404, "File not found.", MessageType.NOT_FOUND)));
        //CONVERTING DICOM FILE TO JPEG
        bufferedJpegImage = DicomManager.getJpegFromDicom(dicomFile);
        if(bufferedJpegImage == null)
            return internalServerError(Json.toJson(new Message(500, "Error in processing your request.", MessageType.INTERNAL_SERVER_ERROR)));
        else {
            //SAVING JPEG TO DISK
            boolean jpegSaved = DicomManager.writeJpegToDisk(bufferedJpegImage, PATH_FOR_JPEG);
            if(jpegSaved)
                return ok(Json.toJson(new Message(200, "Jpeg successfully saved to " + PATH_FOR_JPEG, MessageType.SUCCESSFUL)));
            else
                return internalServerError(Json.toJson(new Message(500, "Jpeg not saved!", MessageType.INTERNAL_SERVER_ERROR)));
        }
    }

    public static Result uploadJpegAndWriteToDisk() {
        Http.MultipartFormData.FilePart jpegFilePart = null;
        File jpegFile = null;
        DicomObject dicomObject = null;
        Http.MultipartFormData body = request().body().asMultipartFormData();
        if(body != null)
            jpegFilePart = body.getFile("jpegFC");
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
            boolean dicomSaved = DicomManager.writeDicomToDisk(jpegFile, dicomObject, PATH_FOR_DICOM);
            if(dicomSaved)
                return ok(Json.toJson(new Message(200, "Dicom successfully saved to " + PATH_FOR_DICOM, MessageType.SUCCESSFUL)));
            else
                return internalServerError(Json.toJson(new Message(500, "Jpeg not saved!", MessageType.INTERNAL_SERVER_ERROR)));
        }
    }
}
