package com.Kurs.Kurs.Controller;

import com.Kurs.Kurs.Models.Role;
import com.Kurs.Kurs.Models.Route;
import com.Kurs.Kurs.Models.User;
import com.Kurs.Kurs.Repository.RoleRepository;
import com.Kurs.Kurs.Service.RouteService;
import com.Kurs.Kurs.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Controller
@SessionAttributes("route")

public class RegistrationController {
    @Autowired
    UserService userService;
    @Autowired
    RoleRepository roleRepository;

    @Autowired
    RouteService routeService;

    @GetMapping("/admin/registration")
    public String registration(Model model){
        model.addAttribute("user",new User());
        return "/admin/registration";
    }

    @PostMapping("/admin/registration")
    public String addUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult, Model model){
        if (bindingResult.hasErrors()){
            return "/admin/registration";
        }
        if (!user.getPassword().equals(user.getPasswordConfirm())){
            model.addAttribute("passwordError", "Пароли не совпадают!");
            return "/admin/registration";
        }
        if (!userService.isLoginFree(user)){
            model.addAttribute("loginError","Логин уже существует");
            return "/admin/registration";
        }
        if (!userService.saveUser(user)) {
            model.addAttribute("userNameError","Пользователь уже существует!");
            return "/admin/registration";
        }
        return "redirect:/admin";
    }

    @GetMapping("/admin")
    public String userList(Model model){
        model.addAttribute("allUsers",userService.allUser());
        return "/admin";
    }
    @GetMapping("/admin/{id}/remove")
    public String removeUser(@PathVariable(value = "id") Long id, Model model){
        int i=userService.deleteUser(id);
        if (i==2){
            return "redirect:/admin/{id}/userdetail/2";
        }
        return "redirect:/admin";
    }
    @GetMapping("/admin/{id}/getadmin")
    public String getAdmin(@PathVariable(value = "id") Long id, Model model){
        userService.getAdmin(id);
        return "redirect:/admin";
    }
    @GetMapping("/admin/{id}/userdetail/{err}")
    public String userDetail(@PathVariable (value = "id") Long id, @PathVariable (value = "err") int err, Model model){
        model.addAttribute("user",userService.findUserById(id));
        model.addAttribute("admin","true");
        if (err==2){
            model.addAttribute("qq","Нельзя удалить единственного администратора!");
        }
        if (!userService.findUserById(id).getRoles().contains(roleRepository.findById(2L).get())){
            model.addAttribute("admin","folse");
        }
        return "/userdetail";
    }

    @GetMapping("/admin/create_route")
    public String createRoute(Model model){
        Route route=new Route();
        model.addAttribute("route", route);
        model.addAttribute("users", userService.allUser());
        model.addAttribute("description", "Создание маршрута");

        return "/admin/create_route";
    }

    @PostMapping("/admin/create_route")
    public String addRoute(@ModelAttribute("route") @Valid Route route,
                           @ModelAttribute("adduser") String adduser,
                           @ModelAttribute("addtarget") String addtarget,
                           @ModelAttribute("addAdmittedUser") String addAdmittedUser,
                           BindingResult bindingResult,
                           SessionStatus sessionStatus,
                           Model model){
        if (bindingResult.hasErrors()){
            return "/admin/create_route";
        }
        if (route.getName().isBlank()){
            model.addAttribute("routeNameError","Имя маршрута не может быть пустым!");
            model.addAttribute("route", route);
            model.addAttribute("users", userService.allUser());
            return "/admin/create_route";
        }
        if (route.getTargetReviewTime()<=0){
            model.addAttribute("targetReviewTimeError","Время рассмотрения задачи адресатом заданно некоректно");
            model.addAttribute("route", route);
            model.addAttribute("users", userService.allUser());
            return "/admin/create_route";
        }

        if (adduser.equals("notsave")){
            sessionStatus.setComplete();
            return "redirect:/admin/allroutes";
        }
        if (addtarget.equals("addtarget")){
            if(route.getTargetUserId()!=-1L){
                if (!route.getTargetUser().contains(route.getTargetUserId())) {
                    routeService.addTargetUserToSet(route);
                    routeService.createTempTargets(route);
                    route.setTargetUserId(-1L);
                } else
                    model.addAttribute("targetuserexist","Этот пользователь уже является адресатом маршрута!");
            }
            model.addAttribute("route", route);
            model.addAttribute("users", userService.allUser());
            return "/admin/create_route";
        }
        if (addAdmittedUser.equals("addUser")){
            if (route.getAdmittedUserId()!=-1L) {
                    routeService.addAdmittedUserToSet(route);
                    route.setAdmittedUserId(-1L);
            }
            model.addAttribute("route", route);
            model.addAttribute("users", userService.allUser());
            return "/admin/create_route";
        }
        if (adduser.equals("adduser")){
            if (route.getAlignerForAdd()!=-1L) {
                if (!route.getStageAligners().contains(route.getAlignerForAdd())) {
                    routeService.addAlignerToList(route);
                    routeService.createTempAligners(route);
                    route.setAlignerForAdd(-1L);
                } else {
                    model.addAttribute("alignerexist","Этот пользователь уже согласует задачу!");
                }
            }
            if (route.getAlignersReviewTime()<=0){
                model.addAttribute("AlignersReviewTimeError","Время согласования промежуточных этапов заданно некоректно");
            }
            model.addAttribute("route", route);
            model.addAttribute("users", userService.allUser());
            return "/admin/create_route";
        }

        if (route.getAdmittedUserId()>0){
            routeService.addAdmittedUserToSet(route);
        }
        if (route.getTargetUserId()>0){
            routeService.addTargetUserToSet(route);
        }
        if (route.getAlignerForAdd()>0){
            routeService.addAlignerToList(route);
        }



        if(!routeService.saveRoute(route)){
            model.addAttribute("routeNameError", "Маршрут с таким именем уже существует");
            return "/admin/create_route";
        }
        sessionStatus.setComplete();
        return "redirect:/admin/allroutes";
    }

    @GetMapping("/admin/{id}/routedetails")
    public String updateRoute(@PathVariable(value = "id") Long id, Model model){
        Route route=routeService.findRouteById(id);
        routeService.createTempAligners(route);
        routeService.createTempTargets(route);
        model.addAttribute("route",route);
        model.addAttribute("users", userService.allUser());
        model.addAttribute("description", "Редактирование маршрута");


        return "/admin/create_route";
    }
    @GetMapping("/admin/allroutes")
    public String allRoutes(Model model){
        model.addAttribute("routes", routeService.allRoute());
        return "/admin/allroutes";
    }

}
