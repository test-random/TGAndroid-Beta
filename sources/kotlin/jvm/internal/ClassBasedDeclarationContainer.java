package kotlin.jvm.internal;

import kotlin.reflect.KDeclarationContainer;

public interface ClassBasedDeclarationContainer extends KDeclarationContainer {
    Class getJClass();
}
