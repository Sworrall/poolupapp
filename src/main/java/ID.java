import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class ID {
    private final int ID;
    private static final AtomicInteger NEXT_ID = new AtomicInteger(1);

    private static final Logger log = LoggerFactory.getLogger(ID.class);


    // --- CONSTRUCTORS ---
    public ID() {
        this.ID = NEXT_ID.getAndIncrement();
        log.info("Generated new ID: {}", this.ID);
    }

    public int getID(){
        log.info("Retrieving ID: {}", this.ID);
        return this.ID;
    }
}
