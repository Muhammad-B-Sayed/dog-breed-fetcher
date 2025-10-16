package dogapi;

import java.util.*;

/**
 * This BreedFetcher caches fetch request results to improve performance and
 * lessen the load on the underlying data source. An implementation of BreedFetcher
 * must be provided. The number of calls to the underlying fetcher are recorded.
 *
 * If a call to getSubBreeds produces a BreedNotFoundException, then it is NOT cached
 * in this implementation. The provided tests check for this behaviour.
 *
 * The cache maps the name of a breed to its list of sub breed names.
 */
public class CachingBreedFetcher implements BreedFetcher {
    private final BreedFetcher fetcher;
    private final Map<String, List<String>> cache = new HashMap<>();
    private int callsMade = 0;

    public CachingBreedFetcher(BreedFetcher fetcher) {
        this.fetcher = Objects.requireNonNull(fetcher, "fetcher");
    }

    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        String key = Objects.requireNonNull(breed, "breed").trim().toLowerCase(Locale.ROOT);

        List<String> cached = cache.get(key);
        if (cached != null) {
            return new ArrayList<>(cached);
        }

        callsMade++;
        List<String> result = fetcher.getSubBreeds(breed);

        List<String> toCache = Collections.unmodifiableList(new ArrayList<>(result));
        cache.put(key, toCache);

        return new ArrayList<>(toCache);
    }

    public int getCallsMade() {
        return callsMade;
    }
}