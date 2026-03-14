package io.github.andreytondo.contract;

import io.github.andreytondo.entity.BaseActor;

public interface EnemyBehavior {
    void execute(BaseActor target, BaseActor self, float delta);
}
