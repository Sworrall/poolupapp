import java.util.concurrent.atomic.AtomicInteger;

public class ID {
    private final int ID;
    private static final AtomicInteger NEXT_ID = new AtomicInteger(1);

    private static final Logger log = LoggerFactory.getLogger(Functions.class);


    // --- CONSTRUCTORS ---
    public ID() {
        this.ID = NEXT_ID.getAndIncrement();
        log.info("Generated new ID: " + this.ID);
    }

    public int getID(){
        log.info("Retrieving ID: " + this.ID);
        return this.ID;
    }
}
