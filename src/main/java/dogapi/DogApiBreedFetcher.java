package dogapi;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */

    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        String b = Objects.requireNonNull(breed, "breed").trim().toLowerCase(Locale.ROOT);
        String url = "https://dog.ceo/api/breed/" + b + "/list";
        Request request = new Request.Builder().url(url).get().build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                throw new BreedNotFoundException("Failed to fetch sub-breeds for: " + b);
            }
            String body = response.body().string();
            JSONObject json = new JSONObject(body);
            if (!"success".equalsIgnoreCase(json.optString("status"))) {
                throw new BreedNotFoundException("Breed not found: " + b);
            }
            JSONArray arr = json.getJSONArray("message");
            List<String> result = new ArrayList<>(arr.length());
            for (int i = 0; i < arr.length(); i++) {
                result.add(arr.getString(i));
            }
            return result;
        } catch (IOException e) {
            throw new BreedNotFoundException("Error contacting Dog API for breed: " + b, e);
        } catch (RuntimeException e) {
            throw new BreedNotFoundException("Unexpected response for breed: " + b, e);
        }
    }

}