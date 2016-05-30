package ru.sberbank.services;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import ru.sberbank.model.SystemLog;
import ru.sberbank.repositories.SystemLogRepository;
import ru.sberbank.repositories.UserRepository;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class SystemLogServiceImpl implements SystemLogService {
    @Resource
    private SystemLogRepository logRepository;

    @Resource
    private UserService userService;

    //логирование для эксепшенов
    @Override
    public void Log(String message, int code) {
        SystemLog log=new SystemLog();

        log.setMessage(message);
        log.setCode(String.valueOf(code));
        log.setDateTime(new Date());

        org.springframework.security.core.userdetails.User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ru.sberbank.model.User curUser = userService.getUser(user.getUsername());

        log.setUser(curUser);

        logRepository.save(log);
    }

    //информационное логирование
    @Override
    public void Log(int code, String... arg) {
        SystemLog log=new SystemLog();

        log.setMessage(codeToMessage(code,arg));
        log.setCode(String.valueOf(code));
        log.setDateTime(new Date());

        org.springframework.security.core.userdetails.User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ru.sberbank.model.User curUser = userService.getUser(user.getUsername());

        log.setUser(curUser);

        logRepository.save(log);
    }

    private String codeToMessage(int code, String... arg){
        switch (code){
            case 10 : {
                if(arg.length==1)
                    return "Был добавлен вопрос <"+arg[0]+">";
                else return "Был добавлен вопрос";
            }
            case 11 : return "Был изменен вопрос";
            case 12 : {
                if(arg.length==1)
                    return "Был удален вопрос <"+arg[0]+">";
                return "Был удален вопрос";
            }
            case 13 : return "Была добавлена глава";
            case 14 : return "Была изменена глава";
            case 15 : return "Была удалена глава";
            default: return "Неизвестное событие!";
        }
    }

    @Override
    public Iterable<SystemLog> findAll() { return logRepository.findAllByOrderByDateTimeDesc(); }

    @Override
    public void deleteById(Long id) { logRepository.delete(id); }
}