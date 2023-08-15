package com.Kurs.Kurs.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.*;

@Entity
public class UserTask {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Route route;

    @Transient
    private Long routeID;

    private String taskName;

    private String description;
    @ManyToOne
    private User masterTask;

    private boolean isActive=false;
    private boolean isFinish=false;

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(name = "usertask_fileDB",
    joinColumns = @JoinColumn(name = "usertask_id"),
    inverseJoinColumns = @JoinColumn(name = "fileDB_id"))
    private Set<FileDB> files=new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "usertask_signature",
            joinColumns = @JoinColumn(name = "usertask_id"),
            inverseJoinColumns = @JoinColumn(name = "signature")
    )
    private Set<User> signatures=new HashSet<>();

    @ManyToMany (cascade = CascadeType.ALL)
    @JoinTable(name = "usertask_currentaligners",
    joinColumns = @JoinColumn(name = "usertask_id"),
    inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> currentAligners;

    private Date timeCreation;
    private Date lastSignature;
    @Transient
    private String signingDescription;

    @ManyToOne
    private User interrupter;

    private String interruptDescription;

    public UserTask() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getMasterTask() {
        return masterTask;
    }

    public void setMasterTask(User masterTask) {
        this.masterTask = masterTask;
    }


    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Set<User> getSignatures() {
        return signatures;
    }

    public String getSigningDescription() {
        return signingDescription;
    }

    public void setSignatures(HashSet<User> signatures) {
        this.signatures = signatures;
    }

    public Date getTimeCreation() {
        return timeCreation;
    }

    public void setTimeCreation(Date timeCreation) {
        this.timeCreation = timeCreation;
    }

    public Date getLastSignature() {
        return lastSignature;
    }

    public void setLastSignature(Date lastSignature) {
        this.lastSignature = lastSignature;
    }

    public User getInterrupter() {
        return interrupter;
    }

    public void setInterrupter(User interrupter) {
        this.interrupter = interrupter;
    }

    public String getInterruptDescription() {
        return interruptDescription;
    }

    public void setInterruptDescription(String interruptDescription) {
        this.interruptDescription = interruptDescription;
    }

    public Long getRouteID() {
        return routeID;
    }

    public void setRouteID(Long routeID) {
        this.routeID = routeID;
    }

    public Set<FileDB> getFiles() {
        return files;
    }

    public void setFiles(Set<FileDB> files) {
        this.files = files;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public void setFinish(boolean finish) {
        isFinish = finish;
    }

    public void setSignatures(Set<User> signatures) {
        this.signatures = signatures;
    }

    public void setSigningDescription(String signingDescription) {
        this.signingDescription = signingDescription;
    }

    public Set<User> getCurrentAligners() {
        return currentAligners;
    }

    public void setCurrentAligners(Set<User> currentAligners) {
        this.currentAligners = currentAligners;
    }

    public String getsigningDescription() {
        return signingDescription;
    }

    public void setsigningDescription(String signingDescription) {
        this.signingDescription = signingDescription;
    }
}
