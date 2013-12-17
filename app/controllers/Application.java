package controllers;

import play.Logger;
import play.api.Play;
import play.mvc.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Application extends Controller {
    private static final String PATH_FOR_JPEG = play.Play.application().getFile("public/images/jpeg").getAbsolutePath();
    private static final String PATH_FOR_DICOM = play.Play.application().getFile("public/images/dicom").getAbsolutePath();


    public static Result index() {

        return ok(views.html.index.render("Your new application is ready."));
    }


    public static Result gallery() {

        List<String> results = new ArrayList<String>();
        File[] files = new File(PATH_FOR_JPEG).listFiles();

        if(files !=null)
            for (File file : files) {
                if (file.isFile()) {
                    results.add(file.getName());
                }
            }

        //String projectRoot = Play.getFile("/images/jpeg/",File[]);
        Logger.info("**********************");

        return ok(views.html.gallery.render("Your new application is ready.",results));
    }
}
