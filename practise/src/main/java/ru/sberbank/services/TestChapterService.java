package ru.sberbank.services;

import ru.sberbank.model.TestChapter;

/**
 * Created by Михаил on 19.05.2016.
 */
public interface TestChapterService {
    void addTestChapter(TestChapter testChapter);

    Iterable<TestChapter> getAllUserGroup();
}
