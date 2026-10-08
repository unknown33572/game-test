package com.study.textgame.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

// M0 확인용: core 모듈에서 JUnit이 실행되는지만 본다. M1에서 실제 테스트가 생기면 지워도 된다.
class CoreSetupTest {

    @Test
    void junitRunsInCore() {
        assertEquals(2, 1 + 1);
    }
}
