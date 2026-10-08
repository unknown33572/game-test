package com.study.textgame.exception;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(Long id) {
        super("아이템을 찾을 수 없습니다. id=" + id);
    }
}
