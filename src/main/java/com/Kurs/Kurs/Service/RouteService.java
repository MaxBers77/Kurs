package com.Kurs.Kurs.Service;

import com.Kurs.Kurs.Models.Route;
import com.Kurs.Kurs.Models.User;
import com.Kurs.Kurs.Repository.RouteRepository;
import com.Kurs.Kurs.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class RouteService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RouteRepository routeRepository;

    public List<Route> allRoute(){
        return routeRepository.findAll();
    }
    public void addAlignerToList(Route route){
        Long id=route.getAlignerForAdd();
        User userFromDB=userRepository.findById(id).get();
        route.addAlignerToList(userFromDB);
    }
    public void addTargetUserToSet(Route route){
        route.getTargetUser().add(route.getTargetUserId());
    }

    public Route findRouteById(Long id){
        Route route=routeRepository.findById(id).get();
        return route;
    }

    public boolean saveRoute(Route route){

        Route routeFromDB=routeRepository.findByName(route.getName());
        if (routeFromDB!=null && routeFromDB.getId()!= route.getId()){
            return false;
        }
        routeRepository.save(route);


        return true;
    }
    public void createTempAligners(Route route){
        route.getTempAligners().clear();
        for (Long id:route.getStageAligners()){
            User user=userRepository.findById(id).get();
            route.getTempAligners().add(user);
        }
    }
    public void createTempTargets(Route route){
        route.getTempTarget().clear();
        for (Long id : route.getTargetUser()){
            User user=userRepository.findById(id).get();
            route.getTempTarget().add(user);
        }
    }

    public void addAdmittedUserToSet (Route route){
        Long id = route.getAdmittedUserId();
        User userFromDB=userRepository.findById(id).get();
        route.getAdmittedUsers().add(userFromDB);
    }

    public void info(){
        for (User user: userRepository.findAll()){
            System.out.println(user.getLogin());
            for (Route route: user.getUserRoutes()){
                System.out.println(route.getName());
            }
        }
    }

    public Set<Route> routesByUser(String userName){
        User user=userRepository.findUserByUsername(userName);
        return user.getUserRoutes();
    }


}
