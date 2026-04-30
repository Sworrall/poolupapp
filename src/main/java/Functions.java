import java.math.BigDecimal;
import java.math.RoundingMode;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class Functions {
    private static final Logger log = LoggerFactory.getLogger(Functions.class);

    public static double roundToNDecimalPlaces(double value, int decimalPlaces) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(decimalPlaces, RoundingMode.HALF_UP);
        log.info("Rounded value: {}", bd.doubleValue());
        return bd.doubleValue();
    }

    public static double calcPercentage(int value, int total) {
        log.info("Calculating percentage: value={}, total={}", value, total);
        if (total == 0) return 0.0;
        return roundToNDecimalPlaces((value * 100.0) / total, 2);
    }

    public static boolean calcPowerOf2(int value) {
        log.info("Checking if value is a power of 2: value={}", value);
        if (value <= 0) return false;
        while (value > 1) {
            if (value % 2 != 0) {
                return false;
            }
            value = value / 2;
        }
        return true;
    }
}
