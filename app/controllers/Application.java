package controllers;

import play.mvc.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Application extends Controller {
  
    public static Result index() {

        return ok(views.html.index.render("Your new application is ready."));
    }


    public static Result gallery() {

     /*   List<String> results = new ArrayList<String>();
        File[] files = new File("../images/jpeg/").listFiles();

        for (File file : files) {
            if (file.isFile()) {
                results.add(file.getName());
            }
        }

*/

        return ok(views.html.gallery.render("Your new application is ready."));
    }
}
