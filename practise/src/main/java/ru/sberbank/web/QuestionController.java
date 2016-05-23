package ru.sberbank.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.sberbank.model.Answer;
import ru.sberbank.model.Question;
import ru.sberbank.model.QuestionType;
import ru.sberbank.model.TestChapter;
import ru.sberbank.services.QuestionService;
import ru.sberbank.services.TestChapterService;

import javax.annotation.Resource;
import java.util.*;

@Controller
public class QuestionController {
    private Question tempQuestion;

    @Resource
    private QuestionService questionService;

    @Resource
    private TestChapterService testChapterService;

    @RequestMapping(value = "/questions/find", method = RequestMethod.GET)
    public String initViewForm(Question question, Map<String, Object> model){
        Iterable<TestChapter> allTestChapter = testChapterService.getAllTestChapter();


        model.put("allTestChapter", allTestChapter);
        return "questions/add-viewQuestion";
    }

    @RequestMapping(value = "/questions/find", method = RequestMethod.POST)
    public String processAddForm(Question question, Map<String, Object> model){
        Iterable<TestChapter> testChapterByTitle = testChapterService.getTestChapterByTitle(question.getTestChapter().getTitle());
        question.setTestChapter(testChapterByTitle.iterator().next());
        List<Answer> answers = question.getAnswers();
        Iterator<Answer> it=answers.iterator();
        while(it.hasNext()){
            Answer answer=it.next();
            if(answer.getText()==null||answer.getText().trim().length()==0)
                it.remove();
            else
                answer.setQuestion(question);
        }

        questionService.addQuestion(question);

        question.setText(null);
        question.setTestChapter(null);
        Iterable<TestChapter> allTestChapter = testChapterService.getAllTestChapter();
        model.put("allTestChapter", allTestChapter);
        return "questions/add-viewQuestion";
    }


    @RequestMapping(value = "/questions/find2", method = RequestMethod.POST)
    public String processViewForm(Question question, Map<String, Object> model){
        Iterable<Question> questions;
        if(question.getTestChapter().getTitle().compareTo("ALL")==0)
            questions=questionService.findQuestionByKeywordsAndTestChapter(question.getText(),null);
        else questions=questionService.findQuestionByKeywordsAndTestChapter(question.getText(), question.getTestChapter());

        model.put("searchQuestion", questions);
        Iterable<TestChapter> allTestChapter = testChapterService.getAllTestChapter();
        model.put("allTestChapter", allTestChapter);
        tempQuestion=new Question();
        tempQuestion.setText(question.getText());
        tempQuestion.setTestChapter(question.getTestChapter());
        return "questions/add-viewQuestion";
    }

    @RequestMapping(value = "/questions/add", method = RequestMethod.GET)
    public String initAddForm(Question question, Map<String, Object> model){
        Iterable<TestChapter> allTestChapter = testChapterService.getAllTestChapter();
        model.put("allTestChapter", allTestChapter);
        model.put("QuestionType", QuestionType.values());
        return "questions/addQuestion";
    }

    @RequestMapping(value = "/questions/delete/{questionID}", method = RequestMethod.GET)
    public String processDeleteQuestion(@PathVariable String questionID, Question question, Map<String, Object> model){
        Long id = Long.decode(questionID);

        questionService.deleteQuestion(id);

        question=tempQuestion;
        Iterable<Question> questions;
        if(question.getTestChapter().getTitle().compareTo("ALL")==0)
            questions=questionService.findQuestionByKeywordsAndTestChapter(question.getText(), null);
        else questions=questionService.findQuestionByKeywordsAndTestChapter(question.getText(), question.getTestChapter());

        model.put("searchQuestion", questions);
        Iterable<TestChapter> allTestChapter = testChapterService.getAllTestChapter();
        model.put("allTestChapter", allTestChapter);
        return "questions/add-viewQuestion";
    }

    @RequestMapping(value = "/questions/edit/{questionID}", method = RequestMethod.GET)
    public String processEditQuestion(@PathVariable String questionID, Map<String, Object> model){
        Long id = Long.decode(questionID);

        Iterable<TestChapter> allTestChapter = testChapterService.getAllTestChapter();
        model.put("allTestChapter", allTestChapter);
        model.put("QuestionType", QuestionType.values());

        Question editQuestion = questionService.findQuestionByID(id);
        model.put("question", editQuestion);
        model.put("isDelete", true);

        questionService.deleteQuestion(editQuestion.getId());

        return "/questions/addQuestion";
    }

}

