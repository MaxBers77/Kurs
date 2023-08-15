package com.Kurs.Kurs.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Entity
@Table(name = "user")

public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank (message = "Имя не может быть пустым")
    private String firstName;
    @NotBlank (message = "Фамилия не может быть пустой")
    private String lastName;
    @NotBlank(message = "Должность не может быть пустой")
    private String  position;

    //Маршруты задач, доступные пользователю
    @ManyToMany
    @JoinTable(name = "route_admittedUsers",
            inverseJoinColumns = @JoinColumn(name = "ROUTE_ID"),
            joinColumns = @JoinColumn(name = "USER_ID"))
    private Set<Route> userRoutes=new HashSet<>();


    //Список задач, согласованных пользователем
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "usertask_signature",
           joinColumns = @JoinColumn(name = "signature",referencedColumnName = "id"),
           inverseJoinColumns = @JoinColumn(name = "usertask_id",referencedColumnName = "id" ))
    private List<UserTask> userTasks=new ArrayList<>();

    @NotBlank (message = "Логин не должен быть пустым")
    private String username;
    @NotNull(message = "Пароль не может быть пустым")
    @Size(min = 6, message = "Минимальная длинна пароля - 6 символов")
    private String password;
    @Transient
    private String passwordConfirm;
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles;

    @OneToMany
    //список задач, созданных пользователем
    private Set<UserTask> taskOfUser=new HashSet<>();
    @ManyToMany
    @JoinTable(name = "usertask_currentaligners",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "usertask_id"))
    //список задач, ожидающих действия пользователя (согласовать либо прервать)
    private Set<UserTask> TaskIsUnderApproval=new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "user_mesagges",
    joinColumns = @JoinColumn(name = "user_id"))
    private List<String> messagesOfUser=new ArrayList<>();

    @OneToMany
    private Set<UserTask> interruptedTasks=new HashSet<>();


    public User() {
    }

    public User(String firstName, String lastName, String username, String password,String position) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.position=position;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }





    public String getLogin() {
        return username;
    }

    public void setLogin(String login) {
        this.username = login;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public void addRole(Role role){
        roles.add(role);
    }

    public Set<Route> getUserRoutes() {
        return userRoutes;
    }

    public void setUserRoutes(Set<Route> userRoutes) {
        this.userRoutes = userRoutes;
    }

    public List<UserTask> getUserTasks() {
        return userTasks;
    }

    public void setUserTasks(List<UserTask> userTasks) {
        this.userTasks = userTasks;
    }

    public Set<UserTask> getTaskOfUser() {
        return taskOfUser;
    }

    public void setTaskOfUser(Set<UserTask> taskOfUser) {
        this.taskOfUser = taskOfUser;
    }

    public Set<UserTask> getTaskIsUnderApproval() {
        return TaskIsUnderApproval;
    }

    public void setTaskIsUnderApproval(Set<UserTask> taskIsUnderApproval) {
        TaskIsUnderApproval = taskIsUnderApproval;
    }

    public List<String> getMessagesOfUser() {
        return messagesOfUser;
    }

    public void setMessagesOfUser(List<String> messagesOfUser) {
        this.messagesOfUser = messagesOfUser;
    }

    public Set<UserTask> getInterruptedTasks() {
        return interruptedTasks;
    }

    public void setInterruptedTasks(Set<UserTask> interruptedTasks) {
        this.interruptedTasks = interruptedTasks;
    }
}
