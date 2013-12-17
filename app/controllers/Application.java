package controllers;

import play.mvc.*;

public class Application extends Controller {
  
    public static Result index() {

        return ok(views.html.index.render("Your new application is ready."));
    }


    public static Result gallery() {

        return ok(views.html.gallery.render("Your new application is ready."));
    }
}
