package kotlinx.coroutines.scheduling;

import java.util.concurrent.TimeUnit;
import kotlin.ranges.RangesKt___RangesKt;
import kotlinx.coroutines.internal.SystemPropsKt;
import kotlinx.coroutines.internal.SystemPropsKt__SystemProps_commonKt;

public abstract class TasksKt {
    public static final TaskContext BlockingContext;
    public static final int CORE_POOL_SIZE;
    public static final String DEFAULT_SCHEDULER_NAME = SystemPropsKt.systemProp("kotlinx.coroutines.scheduler.default.name", "DefaultDispatcher");
    public static final long IDLE_WORKER_KEEP_ALIVE_NS;
    public static final int MAX_POOL_SIZE;
    public static final TaskContext NonBlockingContext;
    public static final long WORK_STEALING_TIME_RESOLUTION_NS;
    public static SchedulerTimeSource schedulerTimeSource;

    static {
        long systemProp$default;
        int coerceAtLeast;
        int systemProp$default2;
        int systemProp$default3;
        long systemProp$default4;
        systemProp$default = SystemPropsKt__SystemProps_commonKt.systemProp$default("kotlinx.coroutines.scheduler.resolution.ns", 100000L, 0L, 0L, 12, (Object) null);
        WORK_STEALING_TIME_RESOLUTION_NS = systemProp$default;
        coerceAtLeast = RangesKt___RangesKt.coerceAtLeast(SystemPropsKt.getAVAILABLE_PROCESSORS(), 2);
        systemProp$default2 = SystemPropsKt__SystemProps_commonKt.systemProp$default("kotlinx.coroutines.scheduler.core.pool.size", coerceAtLeast, 1, 0, 8, (Object) null);
        CORE_POOL_SIZE = systemProp$default2;
        systemProp$default3 = SystemPropsKt__SystemProps_commonKt.systemProp$default("kotlinx.coroutines.scheduler.max.pool.size", 2097150, 0, 2097150, 4, (Object) null);
        MAX_POOL_SIZE = systemProp$default3;
        TimeUnit timeUnit = TimeUnit.SECONDS;
        systemProp$default4 = SystemPropsKt__SystemProps_commonKt.systemProp$default("kotlinx.coroutines.scheduler.keep.alive.sec", 60L, 0L, 0L, 12, (Object) null);
        IDLE_WORKER_KEEP_ALIVE_NS = timeUnit.toNanos(systemProp$default4);
        schedulerTimeSource = NanoTimeSource.INSTANCE;
        NonBlockingContext = new TaskContextImpl(0);
        BlockingContext = new TaskContextImpl(1);
    }
}
