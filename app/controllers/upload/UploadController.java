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

    public static Result uploadDicomFile() {
        Http.MultipartFormData.FilePart dicomFilePart = null;
        File dicomFile = null;
        BufferedImage bufferedJpegImage = null;
        DicomObject dicomObject = null;
        Http.MultipartFormData body = request().body().asMultipartFormData();
        if(body != null)
            dicomFilePart = body.getFile("dicomFC");
        if(dicomFilePart != null)
            dicomFile = dicomFilePart.getFile();
        if(dicomFile == null)
            return notFound(Json.toJson(new Message(404, "File not found.", MessageType.NOT_FOUND)));
        bufferedJpegImage = DicomToJpegConverter.getJpegFromDicom(dicomFile);
        if(bufferedJpegImage == null)
            return internalServerError(Json.toJson(new Message(500, "Error in processing your request.", MessageType.INTERNAL_SERVER_ERROR)));
        else {
            //return ok(Json.toJson(new Message(200, "DICOM FILE META INFO : " + dicomObject.fileMetaInfo() + "\nDICOM BIG EDIAN : " + dicomObject.bigEndian() + "\nDICOM CACHE GET : " + dicomObject.cacheGet() + "\nDICOM SIZE : " + dicomObject.size(), MessageType.SUCCESSFUL)));
            return ok(Json.toJson(new Message(200, "Successfully uploaded!", MessageType.SUCCESSFUL)));
           /* boolean written = DicomToJpegConverter.writeJpegToDisk(bufferedJpegImage);
            if(written)
                return ok(Json.toJson(new Message(200, "Jpeg image written to disk.", MessageType.SUCCESSFUL)));
            else
                return internalServerError(Json.toJson(new Message(500, "Error in writing the file to disk.", MessageType.INTERNAL_SERVER_ERROR)));*/
        }
    }
}
