package com.study.textgame.core.effect;

import com.study.textgame.core.monster.MonsterTag;

public record TagBonus(MonsterTag tag, int score) implements Effect {
}
