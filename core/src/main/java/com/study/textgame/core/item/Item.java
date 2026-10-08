package com.study.textgame.core.item;

import com.study.textgame.core.effect.Effect;

import java.util.List;

public record Item(ItemType type, String name, Grade grade, List<Effect> effects) {
    public Item { effects = List.copyOf(effects); }
}
