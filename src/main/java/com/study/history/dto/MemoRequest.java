package com.study.history.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MemoRequest {
  @NotBlank(message = "Title cannot be blank")
  private String title;
  @NotNull(message = "Content cannot be null")
  private String content;

  // Getters and setters
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }
}
