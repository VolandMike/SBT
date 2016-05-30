package ru.sberbank.services;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;

import ru.sberbank.model.Question;
import ru.sberbank.model.Test;
import ru.sberbank.repositories.TestRepository;


/**
 * Created by Notebook1 on 20.05.2016.
 */
@Service
public class TestServiceImpl implements TestService {
    @Resource
    private TestRepository testRepository;

    @Override
    public void addTest(Test test) {
        testRepository.save(test);
    }

    @Override
    public Iterable<Test> getAllTest() {
        return testRepository.findAll();
    }

    @Override
    public Test getTest(Long id) {
        return testRepository.findOne(id);
    }


    @Override
    public Iterable<Test> deleteTest(long id) {
        //id = null;
        testRepository.delete(id);
        return testRepository.findAll();
    }

    @Override
    public Iterable<Question> findAllQuestions() {
        return null;
    }

    @Override
    public Iterable<Question> getAllQuestions(Long id, Iterable<Question> questionIterable) {
        return null;
    }
}






