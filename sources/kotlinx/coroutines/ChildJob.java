package kotlinx.coroutines;

public interface ChildJob extends Job {
    void parentCancelled(ParentJob parentJob);
}
