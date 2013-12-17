package controllers.upload;

import models.request.helper.DicomToJpegConverter;
import models.response.MessageType;
import models.response.Message;
import org.dcm4che2.data.DicomObject;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import java.awt.image.BufferedImage;
import java.io.File;

public class UploadController extends Controller {

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
        String _userHomePath = System.getProperty("user.home");
        //CONVERTING DICOM FILE TO JPEG
        bufferedJpegImage = DicomToJpegConverter.getJpegFromDicom(dicomFile);
        if(bufferedJpegImage == null)
            return internalServerError(Json.toJson(new Message(500, "Error in processing your request.", MessageType.INTERNAL_SERVER_ERROR)));
        else {
            //SAVING JPEG TO DISK
            boolean jpegSaved = DicomToJpegConverter.writeJpegToDisk(bufferedJpegImage, _userHomePath);
            if(jpegSaved)
                return ok(Json.toJson(new Message(200, "Jpeg successfully saved to " + _userHomePath, MessageType.SUCCESSFUL)));
            else
                return internalServerError(Json.toJson(new Message(500, "Jpeg not saved!", MessageType.INTERNAL_SERVER_ERROR)));
        }
    }
}
