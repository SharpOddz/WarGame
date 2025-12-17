package util;

import java.util.Random;

public class RandomUtil {
    public static long randomSeed() {
        return new Random().nextLong();
    }
}
