package com.study.history.repository;

import com.study.history.entity.Memo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemoRepository extends JpaRepository<Memo, Long> {
  List<Memo> findAllByOrderByCreatedAtDesc();
}
