package com.Kurs.Kurs.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.*;

@Entity
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    private String name;

    @ElementCollection
    @CollectionTable(name = "route_targetusers",
        joinColumns = @JoinColumn(name = "route_id"))
    private Set<Long> targetUser=new HashSet<>();

    private Integer targetReviewTime=0;

    private Integer alignersReviewTime=0;

    @Transient
    private Long targetUserId;
    @ManyToMany
    @JoinTable(name = "route_admittedUsers",
    joinColumns = @JoinColumn(name = "ROUTE_ID"),
    inverseJoinColumns = @JoinColumn(name = "USER_ID"))
    private Set<User> admittedUsers=new HashSet<>();

    @Transient
    private Long admittedUserId;


    @ElementCollection
    @CollectionTable(name = "route_stage_aligners",
            joinColumns = @JoinColumn(name = "route_id"))
    public List<Long> stageAligners=new ArrayList<>();


    @Transient
    private Long alignerForAdd;
    @Transient
    private List<User>tempAligners=new ArrayList<>();
    @Transient
    private Set<User>tempTarget=new HashSet<>();

    public Route() {
    }

    public Long getAlignerForAdd() {
        return alignerForAdd;
    }
    public void addAlignerToList(User user){
        stageAligners.add(user.getId());
    }

    public void setAlignerForAdd(Long alignerForAdd) {
        this.alignerForAdd = alignerForAdd;
    }




    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Long> getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(Set<Long> targetUser) {
        this.targetUser = targetUser;
    }

    public void setStageAligners(List<Long> stageAligners) {
        this.stageAligners = stageAligners;
    }

    public Long getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(Long targetUserId) {
        this.targetUserId = targetUserId;
    }

    public List<User> getTempAligners() {
        return tempAligners;
    }

    public void setTempAligners(List<User> tempAligners) {
        this.tempAligners = tempAligners;
    }

    public List<Long> getStageAligners() {
        return stageAligners;
    }

    public void setStageAligners(ArrayList<Long> stageAligners) {
        this.stageAligners = stageAligners;
    }

    public Set<User> getTempTarget() {
        return tempTarget;
    }

    public void setTempTarget(Set<User> tempTarget) {
        this.tempTarget = tempTarget;
    }
    public Integer getTargetReviewTime() {
        return targetReviewTime;
    }

    public void setTargetReviewTime(Integer targetReviewTime) {
        this.targetReviewTime = targetReviewTime;
    }

    public Integer getAlignersReviewTime() {
        return alignersReviewTime;
    }

    public void setAlignersReviewTime(Integer alignersReviewTime) {
        this.alignersReviewTime = alignersReviewTime;
    }

    public Set<User> getAdmittedUsers() {
        return admittedUsers;
    }

    public void setAdmittedUsers(Set<User> admittedUsers) {
        this.admittedUsers = admittedUsers;
    }

    public Long getAdmittedUserId() {
        return admittedUserId;
    }

    public void setAdmittedUserId(Long amittedUserId) {
        this.admittedUserId = amittedUserId;
    }
}
