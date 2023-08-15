package com.Kurs.Kurs.Service;

import com.Kurs.Kurs.Models.FileDB;
import com.Kurs.Kurs.Models.Route;
import com.Kurs.Kurs.Models.User;
import com.Kurs.Kurs.Models.UserTask;
import com.Kurs.Kurs.Repository.RouteRepository;
import com.Kurs.Kurs.Repository.TaskRepository;
import com.Kurs.Kurs.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;


@Service
public class TaskService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RouteRepository routeRepository;
    @Autowired
    TaskRepository taskRepository;

    public List<UserTask> allTask(){
        return taskRepository.findAll();
    }

    //Сохраняем задачу в первый раз
    public boolean saveTask(UserTask userTask){
        User currentUser=userRepository.findUserByUsername(currentUserName());
        userTask.setMasterTask(currentUser);
        currentUser.getTaskOfUser().add(userTask);

        Route route=routeRepository.findById(userTask.getRouteID()).get();
        userTask.setRoute(route);
        taskRepository.save(userTask);

        return true;
    }

    //Возвращаем логин текущего пользователя
    public String currentUserName(){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String currentUserName=null;
        if(!(authentication instanceof AnonymousAuthenticationToken)){
            currentUserName=authentication.getName();
        }
        return currentUserName;
    }

    public UserTask findById(Long id){
        return taskRepository.findById(id).get();
    }

    //Стартуем задачу
    public void startTask(Long id){
        UserTask userTask=taskRepository.findById(id).get();
        userTask.setActive(true);
        userTask.setTimeCreation(new Date());
        //Вносим в список подписей данной задачи ее инициатора
        userTask.getSignatures().add(userRepository.findUserByUsername(currentUserName()));
        userTask.setLastSignature(new Date());
        if(!userTask.getRoute().getStageAligners().isEmpty()){
            //Если маршрут задачи имеет дополнительные этапы согласования,
            //вносим в список текущих согласователей пользователя, согласующего задачу на первом этапе
            userTask.getCurrentAligners().add(userRepository.findById(userTask.getRoute().getStageAligners().get(0)).get());
            //вносим текущую задачу этому пользователю в перечень задач, ожидающих согласования
            //userRepository.findById(userTask.getRoute().getStageAligners().get(0)).get().getTaskIsUnderApproval().add(userTask);
        } else {
            //Если маршрут задачи не имеет дополнительных этапов согласования
            //вносим в список текущих согласователей всех адресатов задачи
            //и вносим каждому из них текущую задачу в перечень задач, ожидающих согласования
            Set<Long> targetUsersId= userTask.getRoute().getTargetUser();
            for (Long userId : targetUsersId){
                User user=userRepository.findById(userId).get();
                userTask.getCurrentAligners().add(user);
            }
        }
        taskRepository.save(userTask);
    }

    //Возвращаем список сохраненных но не запущенных задач пользователя
    public Set<UserTask> unStartedTasks(){
        User user=userRepository.findUserByUsername(currentUserName());
        Set<UserTask> result=new HashSet<>();
        for (UserTask userTask : user.getTaskOfUser()){
            if (!userTask.isActive() && !userTask.isFinish()){
                result.add(userTask);
            }
        }
        return result;
    }
    //Возвращаем список запущенных задач пользователя
    public Set<UserTask> startedTask(){
        User user=userRepository.findUserByUsername(currentUserName());
        Set<UserTask> result=new HashSet<>();
        for (UserTask userTask : user.getTaskOfUser()){
            if (userTask.isActive() && !userTask.isFinish()){
                result.add(userTask);
            }
        }
        return result;
    }
    //Возвращаем список задач пользователя, ждущих действий
    public Set<UserTask> tasksIsUnderApproval(){
        User user=userRepository.findUserByUsername(currentUserName());
        Set<UserTask>result=new HashSet<>();
        result.addAll(user.getTaskIsUnderApproval());
        return result;
    }

    //Прерываем задачу
    public void interruptTask(UserTask task){
        UserTask userTask=taskRepository.findById(task.getId()).get();
        userTask.setFinish(true);
        userTask.setActive(false);
        userTask.setInterrupter(userRepository.findUserByUsername(currentUserName()));
        userTask.getCurrentAligners().clear();
        userTask.setLastSignature(new Date());
        userRepository.findUserByUsername(currentUserName()).getInterruptedTasks().add(userTask);
        userTask.getMasterTask().getMessagesOfUser().add("Выполнение задачи "+userTask.getTaskName()+" прервано на этапе согласования пользователем "+currentUserName());
        taskRepository.save(userTask);
    }

    public void signingTask(UserTask task) {
        UserTask userTask=taskRepository.findById(task.getId()).get();
        userTask.setsigningDescription(task.getsigningDescription());
        //Добавляем комментарий текущего согласующего в описание задачи
        String newDescription = userTask.getDescription() + "\n" + currentUserName() + ": " + userTask.getsigningDescription();
        userTask.setDescription(newDescription);
        //Добавляем текущего согласователя в список подписей
        userTask.getSignatures().add(userRepository.findUserByUsername(currentUserName()));
        //Если текущий согласователь является конечным адресатом задачи - проверяем,
        // все ли адресаты согласовали задачу
        Set<Long> targetUsersID = userTask.getRoute().getTargetUser();
        if (targetUsersID.contains(userRepository.findUserByUsername(currentUserName()).getId())) {
            boolean isAllTargetUserSigned = true;
            for (Long id : targetUsersID) {
                if (!userTask.getSignatures().contains(userRepository.findById(id).get())) {
                    isAllTargetUserSigned = false;
                    break;
                }
            }
            //Если да - завершаем задачу
            if (isAllTargetUserSigned) {
                userTask.setFinish(true);
                userTask.setActive(false);
                userTask.setLastSignature(new Date());
                userTask.getCurrentAligners().clear();
                userTask.getMasterTask().getMessagesOfUser().add("Ваша задача "+userTask.getTaskName()+" успешно завершена");
                taskRepository.save(userTask);
                return;
                //иначе завершаем метод
            } else {
                System.out.println("не завершаем");
                taskRepository.save(userTask);
                return;
            }
        }
        //Если текущий пользователь не является адресатом задачи (а значит является
        //согласователем промежуточного этапа)
        //Устанавливаем время последней подписи
        userTask.setLastSignature(new Date());
        //Получаем индекс текущего пользователя в листе последователно
        // согласующих задачу на промежуточных этапах
        int countOfCurrentStageAligners = userTask.getRoute().getStageAligners().indexOf(userRepository.findUserByUsername(currentUserName()).getId());
        //Проверяем, есть ли в листе последователно согласующих задачу на промежуточных этапах
        //пользователи с большим индексом (должен ли еще кто-то предварительно согласовать задачу)
        if (userTask.getRoute().getStageAligners().size() > countOfCurrentStageAligners + 1) {
            //Если есть - очищаем список текущих согласователей
            userTask.getCurrentAligners().clear();
            //И вноси в список текущих согласователей следующего пользователя
            // из списка промежуточных согласователей маршрута задачи
            User newAligner = userRepository.findById(userTask.getRoute().getStageAligners().get(countOfCurrentStageAligners + 1)).get();
            userTask.getCurrentAligners().add(newAligner);
        } else {
            //Иначе (если больше промежуточных согласователей в маршруте нет)
            //Вносим в список текущих согласователей задачи всех адресатов маршрута
            userTask.getCurrentAligners().clear();
            Set<Long> targetUsersId = userTask.getRoute().getTargetUser();
            for (Long userId : targetUsersId) {
                User user = userRepository.findById(userId).get();
                userTask.getCurrentAligners().add(user);
            }
        }
        taskRepository.save(userTask);
    }

    //Возвращаем список завершенных задач
    public Set<UserTask> completedTask(){
        User user=userRepository.findUserByUsername(currentUserName());
        Set<UserTask> result=new HashSet<>();
        for (UserTask userTask : user.getUserTasks()){
            if (!userTask.isActive() && userTask.isFinish() && userTask.getMasterTask().getLogin().equals(currentUserName()) || !userTask.getMasterTask().getLogin().equals(currentUserName())){
                result.add(userTask);
            }
        }
        result.addAll(user.getInterruptedTasks());
        return result;
    }
    //Возвращаем список сообщений пользователя
    public List<String> messagesOfUser(){
        User user=userRepository.findUserByUsername(currentUserName());
        return user.getMessagesOfUser();
    }

    //Очистка списка сообщений пользователя
    public void clearMessagesOfUser(){
        User user=userRepository.findUserByUsername(currentUserName());
        user.getMessagesOfUser().clear();
        userRepository.save(user);
    }
    //Просто сохраняем задачу
    public void simpleSave(UserTask userTask){
        taskRepository.save(userTask);
    }
//Проверяем наличие просроченных задач
    public void overdueTaskCheck(){
        User user=userRepository.findUserByUsername(currentUserName());
        for (UserTask userTask : user.getTaskIsUnderApproval()){
            long currentTime=new Date().getTime();
            long lastSignatureTime=userTask.getLastSignature().getTime();
            long reviewTime=0;
            if (userTask.getRoute().getTargetUser().contains(user.getId())){
                reviewTime=userTask.getRoute().getTargetReviewTime();
            } else {
                reviewTime=userTask.getRoute().getAlignersReviewTime();
            }
            if (currentTime-lastSignatureTime>reviewTime*60000){
                user.getMessagesOfUser().add("ВНИМАНИЕ!!! Вы допустили просрочку выполнения задачи "+userTask.getTaskName());
            }
        }
    }
    public void readFileFromDB(FileDB fileDB){
        byte[] bytes=fileDB.getData();
        String fileName= fileDB.getName();
        String suffix=fileName.substring(fileName.indexOf("."));
        System.out.println(suffix);
        try {
            Path tempPath= Files.createTempFile("kurs",suffix);
            File tempFile=tempPath.toFile();
            FileOutputStream fos=new FileOutputStream(tempFile);
            fos.write(bytes);
            fos.close();
            Runtime.getRuntime().exec("cmd.exe /C start " + tempFile.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
