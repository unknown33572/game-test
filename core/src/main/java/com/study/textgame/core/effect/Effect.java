package com.study.textgame.core.effect;

public sealed interface Effect permits TagBonus, MaterialBonus, RareDropBonus, ApCostReduction, InfoReveal {
}
