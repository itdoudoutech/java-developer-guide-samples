package com.doudou.user.web.controller;

import com.doudou.controller.PageController;
import com.doudou.user.domain.User;
import com.doudou.user.service.UserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.logging.Logger;

@Path("/register")
public class RegisterSubmitController implements PageController {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Resource(name = "bean/UserService")
    private UserService userService;

    @POST
    @Path("/submit")
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String phone = request.getParameter("phone");
        logger.info(String.format("register info : [username = %s, phone = %s]", username, phone));

        User user = new User();
        user.setName(username);
        user.setPassword(password);
        user.setPhoneNumber(phone);
        user.setPhoneNumber(phone);
        try {
            if(userService.register(user)){
                User dbUser = userService.queryUserById(user.getId());
                request.setAttribute("name", dbUser.getName());
                logger.info("register success: " + dbUser);
            }
            return "success.jsp";
        }catch (Exception e){
            request.setAttribute("msg", e.getMessage());
        }
        return "error.jsp";
    }
}
