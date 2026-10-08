package com.study.textgame.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
public class Item {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;
  private String description;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  @Enumerated(EnumType.STRING)
  private Grade grade;

  protected Item() {}

  @Builder
  protected Item(String name, String description, LocalDateTime createdAt, Grade grade) {
    this.name = name;
    this.description = description;
    this.createdAt = createdAt;
    this.grade = grade;
  }

  public void updateItem(String name, String description, Grade grade, LocalDateTime updatedAt) {
    this.name = name;
    this.description = description;
    this.grade = grade;
    this.updatedAt = updatedAt;
  }

}
