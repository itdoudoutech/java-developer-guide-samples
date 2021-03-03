package com.doudou.user.web.controller;

import com.doudou.controller.PageController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/register")
public class RegisterSubmitController implements PageController {

    @POST
    @Path("/submit")
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        System.out.println(String.format("username = %s, password = %s", username, password));

        return "success.jsp";
    }
}
