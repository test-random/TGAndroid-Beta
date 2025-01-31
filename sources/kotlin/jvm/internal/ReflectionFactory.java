package kotlin.jvm.internal;

import kotlin.reflect.KClass;
import kotlin.reflect.KDeclarationContainer;
import kotlin.reflect.KFunction;
import kotlin.reflect.KProperty0;

public class ReflectionFactory {
    public KFunction function(FunctionReference functionReference) {
        return functionReference;
    }

    public KClass getOrCreateKotlinClass(Class cls) {
        return new ClassReference(cls);
    }

    public KDeclarationContainer getOrCreateKotlinPackage(Class cls, String str) {
        return new PackageReference(cls, str);
    }

    public KProperty0 property0(PropertyReference0 propertyReference0) {
        return propertyReference0;
    }

    public String renderLambdaToString(FunctionBase functionBase) {
        String obj = functionBase.getClass().getGenericInterfaces()[0].toString();
        return obj.startsWith("kotlin.jvm.functions.") ? obj.substring(21) : obj;
    }

    public String renderLambdaToString(Lambda lambda) {
        return renderLambdaToString((FunctionBase) lambda);
    }
}
