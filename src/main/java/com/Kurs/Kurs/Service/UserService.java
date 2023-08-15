package com.Kurs.Kurs.Service;

import com.Kurs.Kurs.Models.Role;
import com.Kurs.Kurs.Models.User;
import com.Kurs.Kurs.Repository.RoleRepository;
import com.Kurs.Kurs.Repository.UserRepository;
import org.hibernate.event.spi.PersistContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user=userRepository.findUserByUsername(username);
        if (user==null){
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }
    public List<User> allUser(){
        return userRepository.findAll();
    }
    public boolean isLoginFree(User newUser){
        User userFromDB=userRepository.findUserByUsername(newUser.getLogin());
        if (userFromDB!=null){
            return false;
        }
        return true;
    }
    public boolean saveUser (User user){
        User userFromDB=userRepository.findUserByLastName(user.getLastName());
        if (userFromDB!=null && user.getFirstName().equals(userFromDB.getFirstName()) ){
            return false;
        }
        user.setRoles(Collections.singleton(new Role(1L,"ROLE_USER")));
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return true;
    }
    public void saveAdmin(User admin){
        Set<Role> roles=new HashSet<>();
        roles.add(roleRepository.findById(1L).orElseThrow());
        roles.add(roleRepository.findById(2L).orElseThrow());
        admin.setRoles(roles);
        admin.setPassword(bCryptPasswordEncoder.encode(admin.getPassword()));
        userRepository.save(admin);
    }
    public void creatRoles(){
        roleRepository.save(new Role(1L,"ROLE_USER"));
        roleRepository.save(new Role(2L,"ROLE_ADMIN"));
    }
    public int countOfAdmin(){
        System.out.println("считаем админов");
        List<User>users= userRepository.findAll();
        int result=0;
        for (User user:users){
            if (user.getRoles().contains(roleRepository.findById(2L).get())){
                result++;
            }
        }
        System.out.println("насчитали"+result);
        return result;
    }
    public int deleteUser (Long userId){
        if (userRepository.findById(userId).isPresent()){
            System.out.println("начинаем удалять");
            if (userRepository.findById(userId).get().getRoles().contains(roleRepository.findById(2L).get())){
                System.out.println("попытка удаления админа");
                if (countOfAdmin()<2){
                    return 2;
                }
            }
            userRepository.deleteById(userId);
            return 1;
        }
        return 3;
    }
    public boolean getAdmin(Long userId){
        if (userRepository.findById(userId).isPresent()){
            User user=userRepository.findById(userId).orElseThrow();
            if (!user.getRoles().contains(roleRepository.findById(2L))){
                user.addRole(roleRepository.findById(2L).orElseThrow());
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }
    public User findUserById(Long id){
        User userFromDB=userRepository.findById(id).orElse(null);
        return userFromDB;
    }
    public boolean isUsersExist(){
        return !allUser().isEmpty();
    }
    public boolean isRolesExist(){
        return !roleRepository.findAll().isEmpty();
    }
    public User findUserByUserName(String userName){
        return userRepository.findUserByUsername(userName);
    }
}
