package com.study.textgame.core.effect;

public sealed interface Effect permits TagBonus, MaterialBonus, DropGradeBonus, ApCostReduction, InfoReveal {
}
