package com.study.textgame.dto;

import com.study.textgame.entity.Grade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ItemRequest {
  @NotBlank(message = "Name cannot be blank")
  private String name;
  @NotNull(message = "Description cannot be null")
  private String description;

  @NotNull
  private Grade grade;

  // Getters and setters
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Grade getGrade() {
      return grade;
  }

  public void setGrade(Grade grade) {
      this.grade = grade;
  }
}
