package ru.sberbank.model;

import javax.persistence.*;
import java.lang.String;
import java.util.Set;

@Entity
public class Question {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false,name = "answer_type")
    private AnswerType answerType;

    @ManyToOne(cascade = CascadeType.ALL)
    private TestChapter testChapter;

    @OneToMany
    private Set<Answer> answer;

    @Column(nullable = false)
    private String text;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "test_question", catalog = "db", joinColumns = {
            @JoinColumn(name = "question_id", nullable = false, updatable = false) },
            inverseJoinColumns = { @JoinColumn(name = "test_id",
                    nullable = false, updatable = false) })
    private Set<Test> tests;

    public Question() {
        answer=null;
        text=null;
        testChapter=null;
    }

    public Question(Set<Answer> answer, String text, TestChapter testChapter) {
        this.answer = answer;
        this.text = text;
        this.testChapter = testChapter;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AnswerType getAnswerType() {
        return answerType;
    }

    public void setAnswerType(AnswerType answerType) {
        this.answerType = answerType;
    }

    public TestChapter getTestChapter() {
        return testChapter;
    }

    public void setTestChapter(TestChapter testChapter) {
        this.testChapter = testChapter;
    }

    public Set<Answer> getAnswer() {
        return answer;
    }

    public void setAnswer(Set<Answer> answer) {
        this.answer = answer;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Set<Test> getTests() {
        return tests;
    }

    public void setTests(Set<Test> tests) {
        this.tests = tests;
    }
}