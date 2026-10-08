package com.study.textgame.repository;

import com.study.textgame.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
  List<Item> findAllByOrderByCreatedAtDesc();
}
