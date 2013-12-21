package controllers;

import com.avaje.ebean.Ebean;
import models.S3File;
import models.jpeg.JpegImage;
import play.Logger;
import play.api.Play;
import play.mvc.*;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class Application extends Controller {
    private static final String PATH_FOR_JPEG = play.Play.application().getFile("public/images/jpeg").getAbsolutePath();
    private static final String PATH_FOR_DICOM = play.Play.application().getFile("public/images/dicom").getAbsolutePath();
    private static final String PATH_FOR_DICOM_CONVERTER = play.Play.application().getFile("public/software").getAbsolutePath();


    public static Result index() {

        return ok(views.html.index.render("Your new application is ready."));
    }


    public static Result gallery() {

        List<String> imageUrls = new ArrayList<String>();
        /*File[] jpegImageFiles = new File(PATH_FOR_JPEG).listFiles();
        if(jpegImageFiles != null) {
            for (File file : jpegImageFiles) {
                if (file.isFile()) {
                    jpegFilePaths.add(file.getName());
                }
            }
        }*/
        List<S3File> s3Files = null;
        s3Files = Ebean.find(S3File.class).findList();
        if(s3Files == null)
            s3Files = new ArrayList<S3File>();
        try {
            for(S3File s3File: s3Files){
                imageUrls.add(s3File.getUrl().toString());
            }
        } catch (MalformedURLException mue) {
            mue.printStackTrace();
        }

        /*List<JpegImage> jpegImages = new ArrayList<JpegImage>();
        List<Long> ids = new ArrayList<Long>();
        jpegImages = JpegImage.find.findList();
        for(JpegImage ji: jpegImages) {
            ids.add(ji.getId());
        }*/
        return ok(views.html.gallery.render("Gallery", imageUrls, PATH_FOR_DICOM_CONVERTER));
    }
}
