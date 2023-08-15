package com.Kurs.Kurs.Controller;

import com.Kurs.Kurs.Models.Role;
import com.Kurs.Kurs.Models.Route;
import com.Kurs.Kurs.Models.User;
import com.Kurs.Kurs.Service.RouteService;
import com.Kurs.Kurs.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashSet;
import java.util.Set;

@Controller
public class MainController {
    @Autowired
    UserService userService;
    @Autowired
    RouteService routeService;

    @GetMapping("/")
    public String start(){
        if (!userService.isRolesExist()){
            userService.creatRoles();
        }
        if (!userService.isUsersExist()){
            User admin=new User("admin","admin","admin","admin","admin");
            userService.saveAdmin(admin);
            /*
            User Bersenev = new User("Максим","Берсенев","Берсенев","111111","Директор");
            User Nastia = new User("Анастасия","Арестова","Арестова","111111","Жена");
            User Grey = new User("Грей","Берсенев","Грей","111111","Кот");
            userService.saveUser(Bersenev);
            userService.saveUser(Nastia);
            userService.saveUser(Grey);
            Route route=new Route();
            route.setName("Докладная директору");
            route.getTargetUser().add(Bersenev.getId());
            route.setTargetReviewTime(1);
            route.getAdmittedUsers().add(Nastia);
            routeService.saveRoute(route);

             */
        }
        return "start";
    }
}
