package com.Kurs.Kurs.Controller;

import com.Kurs.Kurs.Models.FileDB;
import com.Kurs.Kurs.Models.Route;
import com.Kurs.Kurs.Models.User;
import com.Kurs.Kurs.Models.UserTask;
import com.Kurs.Kurs.Service.FileStorageService;
import com.Kurs.Kurs.Service.RouteService;
import com.Kurs.Kurs.Service.TaskService;
import com.Kurs.Kurs.Service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.SocketHandler;

@Controller
@SessionAttributes ("userTask")
public class TaskController {
    @Autowired
    FileStorageService fileStorageService;
    @Autowired
    UserService userService;
    @Autowired
    RouteService routeService;
    @Autowired
    TaskService taskService;

    @GetMapping("/user")
    public String mainUser(Model model, HttpServletResponse response){
        //Настраиваем автоматическое обновление страницы /user раз в 200 секунд
        response.setIntHeader("Refresh",200);
        //Проверяем наличие просроченных заданий
        taskService.overdueTaskCheck();
        List<String>messages=taskService.messagesOfUser();
        if (!messages.isEmpty()){
            model.addAttribute("messages",messages);

            return "user/messages";
        }

        model.addAttribute("unstarted_task_of_user", taskService.unStartedTasks());
        model.addAttribute("started_task_of_user", taskService.startedTask());
        model.addAttribute("task_is_under_approval_of_user", taskService.tasksIsUnderApproval());
        model.addAttribute("completed_tasks_of_user",taskService.completedTask());
        return "/user/user";
    }

    @PostMapping("/user")
    public String messageOfUser(Model model){
        taskService.clearMessagesOfUser();
        model.addAttribute("unstarted_task_of_user", taskService.unStartedTasks());
        model.addAttribute("started_task_of_user", taskService.startedTask());
        model.addAttribute("task_is_under_approval_of_user", taskService.tasksIsUnderApproval());
        model.addAttribute("completed_tasks_of_user",taskService.completedTask());
        return "/user/user";

    }

    @GetMapping("/user/create_task")
        public String createTask(Model model){
        UserTask userTask=new UserTask();

        Set<Route>routes=routeService.routesByUser(taskService.currentUserName());

        model.addAttribute("userTask", userTask);
        model.addAttribute("routes", routes);
        return "user/create_task";
    }

    @PostMapping("/user/create_task")
    public String addTask(@ModelAttribute("userTask")  UserTask userTask,
                          @ModelAttribute("addfile") String addfile,
                          @ModelAttribute("exit") String exit,
                          @RequestParam ("file") MultipartFile file,
                          SessionStatus sessionStatus,
                          Model model) throws IOException {
        if (exit.equals("exit")){
            return "redirect:/user";
        }
        if (userTask.getTaskName().isBlank()){
            model.addAttribute("nameError", "Задача должна иметь название");
            model.addAttribute("routes",routeService.routesByUser(taskService.currentUserName()));
            model.addAttribute("userTask", userTask);
            return "user/create_task";
        }
        if(file!=null){
            fileStorageService.addFile(file,userTask);
        }
        if (addfile.equals("addfile")){
            model.addAttribute("routes",routeService.routesByUser(taskService.currentUserName()));
            model.addAttribute("userTask", userTask);
            return "user/create_task";
        }


        taskService.saveTask(userTask);
        sessionStatus.setComplete();
        return "redirect:/user";
    }

    @PostMapping("/user/start_task")
    public String startTask(@ModelAttribute("userTask") @Valid UserTask userTask,
                            @ModelAttribute("run") String run,
                            Model model){
        if (run.equals("run")){
            taskService.startTask(userTask.getId());
            return "redirect:/user";
        }
        return "redirect:/user";
    }

    @GetMapping("/user/{taskId}")
    public String prepearedTaskInfo(@PathVariable (value = "taskId") Long id,
                                    Model model){
        UserTask userTask=taskService.findById(id);
        Set<Route>routes=routeService.routesByUser(taskService.currentUserName());

        model.addAttribute("userTask", userTask);
        model.addAttribute("routes", routes);
        return "user/task_for_start";
    }

    @GetMapping("/user/{taskId}/started")
    public String startedTaskInfo(@PathVariable (value = "taskId") Long id,
                                  Model model){
        UserTask userTask=taskService.findById(id);
        model.addAttribute("userTask", userTask);
        ArrayList <User> stageAligners=new ArrayList<>();
        for (Long alignerId : userTask.getRoute().getStageAligners()){
            stageAligners.add(userService.findUserById(alignerId));
        }
        model.addAttribute("stageAligners", stageAligners);
        Set<User>targetUsers=new HashSet<>();
        for (Long targetUserId : userTask.getRoute().getTargetUser()){
            targetUsers.add(userService.findUserById(targetUserId));
        }
        model.addAttribute("targetUsers", targetUsers);
        return "/user/started_task";
    }
    @GetMapping("user/{taskId}/under_approval")
    public String underApprovalTaskInfo(@PathVariable (value = "taskId") Long id,
                                        Model model){
        UserTask userTask=taskService.findById(id);
        model.addAttribute("userTask", userTask);
        ArrayList <User> stageAligners_=new ArrayList<>();
        Route route=routeService.findRouteById(userTask.getRoute().getId());
        for (Long alignerId : route.getStageAligners()){
            stageAligners_.add(userService.findUserById(alignerId));
        }
        model.addAttribute("stageAligners", stageAligners_);
        Set<User>targetUsers=new HashSet<>();
        for (Long targetUserId : userTask.getRoute().getTargetUser()){
            targetUsers.add(userService.findUserById(targetUserId));
        }
        model.addAttribute("targetUsers", targetUsers);
        return "/user/under_approval_task";
    }

    @PostMapping("/user/task_signing")
    public String taskSigning(@ModelAttribute ("userTask") UserTask userTask,
                              @ModelAttribute ("interrupt") String interrupt,
                              @ModelAttribute ("signing") String signing,
                              @ModelAttribute ("exit") String exit,
                              SessionStatus sessionStatus,
                              Model model){
        if (exit.equals("exit")){
            sessionStatus.setComplete();
            return "redirect:/user";
        }

        if (interrupt.equals("interrupt")){
            if (userTask.getInterruptDescription().isBlank()){
                model.addAttribute("interruptException", "Нельзя прервать выполнение задачи без объяснения причин");
                return "/user/under_approval_task";
            }
            taskService.simpleSave(userTask);
            sessionStatus.setComplete();
            taskService.interruptTask(userTask);
            return "redirect:/user";
        }
        if (signing.equals("signing")){
            taskService.signingTask(userTask);
            sessionStatus.setComplete();
        }
        return "redirect:/user";
    }

    @GetMapping("user/info_task/{taskId}")
    public String taskInfo(@PathVariable (value = "taskId") Long id,
                           Model model){
        UserTask userTask=taskService.findById(id);
        model.addAttribute("userTask", userTask);

        Set<User>targetUsers=new HashSet<>();
        for (Long targetUserId : userTask.getRoute().getTargetUser()){
            targetUsers.add(userService.findUserById(targetUserId));
        }
        model.addAttribute("targetUsers", targetUsers);


        if (userTask.getInterruptDescription()!=null){
            model.addAttribute("interrupt", "Выполнение задачи прерванно пользователем "+
                    userTask.getInterrupter().getLogin()+" по следующей причине:"+ userTask.getInterruptDescription());
        }
        return "user/info_task";
    }

    @GetMapping("/user/file/{fileId}/{taskId}/{type}")
    public String readFile(@PathVariable(value = "fileId") Long id,
                           @PathVariable(value = "taskId") Long taskId,
                           @PathVariable(value = "type") int type,
                           Model model){
        taskService.readFileFromDB(fileStorageService.findFileById(id));
        switch (type) {
            case 4 -> {
                return "redirect:/user/info_task/" + taskId;
            }
            case 2 -> {
                return "redirect:/user/" + taskId + "/started";
            }
            case 1 -> {
                return "redirect:/user/" + taskId;
            }
            case 3 -> {
                return "redirect:/user/" + taskId + "/under_approval";
            }
        }

        return "redirect:/user";
    }

}
