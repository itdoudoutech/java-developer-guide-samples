package com.doudou.user.web.controller;

import com.doudou.controller.PageController;
import com.doudou.user.domain.User;
import com.doudou.user.repository.DatabaseUserRepository;
import com.doudou.user.service.UserService;
import com.doudou.user.service.UserServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.concurrent.atomic.AtomicLong;

@Path("/register")
public class RegisterSubmitController implements PageController {

    private UserService userService = new UserServiceImpl(new DatabaseUserRepository());

    private AtomicLong atomicLong = new AtomicLong(0L);

    @POST
    @Path("/submit")
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        User user = new User();
        user.setName(username);
        user.setPassword(password);
        user.setId(atomicLong.getAndIncrement());
        boolean result = userService.register(user);

        if (result) {
            User dbUser = userService.queryUserById(user.getId());
            request.setAttribute("name", dbUser.getName());
            System.out.println("RegisterSubmitController: " + dbUser);
        }

        return result ? "success.jsp" : "error.jsp";
    }
}
