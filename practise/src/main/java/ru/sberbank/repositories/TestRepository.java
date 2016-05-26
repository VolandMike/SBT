/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.sberbank.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.sberbank.model.Question;
import ru.sberbank.model.Test;

import java.util.List;

/**
 *
 * @author Raim
 */
public interface TestRepository extends CrudRepository<Test, Long> {
  Iterable<Test> findByTitleLike (String testTitleLike);
  Iterable<Test> findByTitleLikeAndDescriptionLike (String testTitleLike, String testDescriptionLike);
  Iterable<Test> findByDescriptionLike (String testTitleLike);
}
