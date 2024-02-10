package android.view;

public abstract class DisplayAddress {
    public static final class Physical extends DisplayAddress {
        public long getPhysicalDisplayId() {
            throw new RuntimeException("STUB");
        }
        
    }
    
}
