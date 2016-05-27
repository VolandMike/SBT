package ru.sberbank.web;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.sberbank.model.SystemLog;
import ru.sberbank.model.User;
import ru.sberbank.model.UserGroup;
import ru.sberbank.repositories.SystemLogRepository;
import ru.sberbank.repositories.UserRepository;
import ru.sberbank.services.UserGroupService;
import ru.sberbank.services.UserService;

import javax.annotation.Resource;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;

@Controller
public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private UserGroupService userGroupService;
    @Resource
    private SystemLogRepository logRepository;



    private String sha256(String input) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA-256");
        byte[] result = mDigest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    @RequestMapping(value = "/users/find", method = RequestMethod.GET)
    public String initSearchForm(User user, Map<String, Object> model) {
        Iterable<User> users = userService.findUsersByExample(user);
        model.put("searchResult", users);
        return "users/usersList";
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public String processSearchForm(User user, Map<String, Object> model) {
        Iterable<User> users = userService.findUsersByExample(user);
        model.put("searchResult", users);
        return "users/usersList";
    }

    @RequestMapping(value = "/users/add", method = RequestMethod.GET)
    public String initAddUserForm (User user, Map<String, Object> model){
        Iterable<UserGroup> userGroups = userGroupService.getAllUserGroup();
        model.put("userGroups", userGroups);
        return "users/addUser";
    }

    @RequestMapping(value = "/users/add", method = RequestMethod.POST)
    public String processAddUserForm (User user, Map<String, Object> model){
        if(user.getGroup() != null) {
            Long groupId = user.getGroup().getId();
            UserGroup userGroup = userGroupService.getUserGroup(groupId);
            user.setGroup(userGroup);
        }
        try{
            user.setPassword(sha256(user.getPassword()));
        }
        catch (NoSuchAlgorithmException e){
            return "users/addUser";
        }

        userService.addUser(user);
        Iterable<User> users = userService.findUsersByExample(user);
        model.put("searchResult", users);
        return "users/usersList";
    }

    @RequestMapping(value = "/users/delete", method = RequestMethod.GET)
    public String processAddUserForm (User user, @RequestParam("userId") long userId, Map<String, Object> model){
        try {

            //проверка требования на существование хотя бы одного администратора системы
            Long adminUsers = userService.countAdminUsers();
            if(adminUsers==1)
                throw new Exception("System must have at least one user with admin role!");

            //удаление самого себя запрещено
            org.springframework.security.core.userdetails.User springUser = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            ru.sberbank.model.User curUser = userService.getUser(springUser.getUsername());
            if(curUser.getId()==userId)
                throw new Exception("Don't try delete yourself!");

            //удаление ссылок на пользователя в логах
            Iterable<SystemLog> logRepositoryByUserId = logRepository.findByUserId(userId);
            Iterator<SystemLog> it = logRepositoryByUserId.iterator();
            while(it.hasNext()){
                SystemLog next=it.next();
                next.setMessage(next.getMessage()+"[Сообщение инициировано "+next.getUser().getUsername()+"]");
                next.setUser(null);
            }
            logRepository.save(logRepositoryByUserId);

            userService.deleteUser(userId);
        }
        catch (Exception e){
            System.out.println(e);
            Iterable<User> users = userService.findUsersByExample(user);
            model.put("searchResult", users);
            model.put("noDelete", "- can not be removed");
            model.put("userId", userId);
            return "users/usersList";
        }
        Iterable<User> users = userService.findUsersByExample(user);
        model.put("searchResult", users);
        return "users/usersList";
    }

}
