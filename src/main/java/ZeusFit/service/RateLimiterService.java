package ZeusFit.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterService {

    Bandwidth limit = Bandwidth.classic(10, Refill.greedy(10,Duration.ofSeconds(1)));

    private  final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    public Bucket resolveBucket(String user) {
        return cache.computeIfAbsent(user, this::newBucket);

    }


    private Bucket newBucket(String user){
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    public ConsumptionProbe bucket_exist(String user){
        Bucket bucket = resolveBucket(user);
        return bucket.tryConsumeAndReturnRemaining(1);
    }
}
