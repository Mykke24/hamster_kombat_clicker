package io.hamster_kombat.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hamster_kombat.client.requests.BuyBoostRequest;
import io.hamster_kombat.client.requests.ClickRequest;
import io.hamster_kombat.client.requests.ListTaskRequest;
import io.hamster_kombat.client.responses.BoostDetailResponse;
import io.hamster_kombat.client.responses.BoostsResponse;
import io.hamster_kombat.client.responses.ClickResponse;
import io.hamster_kombat.client.responses.TaskDetailResponse;
import io.hamster_kombat.client.responses.TaskResponse;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

public class HamsterKombatClient {

  private HamsterKombatClient() {}

  private static final String BASE_URL = "https://api.hamsterkombat.io";
  private static final HttpClient client = HttpClient.newBuilder().build();
  private static final ObjectMapper objectMapper = new ObjectMapper()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  public static Optional<ClickResponse> clickWithAPI(String authorization) throws IOException, InterruptedException {
    ClickRequest payload = ClickRequest.builder()
        .count(1)
        .availableTaps(7500)
        .timestamp(System.currentTimeMillis())
        .build();
    Optional<ClickResponse> optional = sendRequest(authorization, "/clicker/tap", payload, ClickResponse.class);
    if (optional.isPresent()) {
      System.out.println("Đang tap: " + optional.get().getClickerUser());
    } else {
      System.err.println("Không bấm được.");
    }
    return optional;
  }

  public static void checkTasks(String authorization) throws IOException, InterruptedException {
    Optional<TaskResponse> optionalTaskResponse = sendRequest(authorization, "/clicker/list-tasks", null, TaskResponse.class);

    if (optionalTaskResponse.isEmpty())
      return;
    for (TaskDetailResponse task : optionalTaskResponse.get().getTasks()) {
      if ("streak_days".equals(task.getId()) && Boolean.TRUE.equals(!task.getIsCompleted())) {
        ListTaskRequest listTaskRequest = new ListTaskRequest("streak_days");
        sendRequest(authorization, "/clicker/check-task", listTaskRequest, Object.class);
        System.out.println("Daily token attendance check complete " + authorization);
      }
    }

    Optional<BoostsResponse> optionalBoostsResponse = sendRequest(authorization, "/clicker/boosts-for-buy", null, BoostsResponse.class);

    if (optionalBoostsResponse.isPresent()) {
      Optional<BoostDetailResponse> optionalBoostDetailResponse = optionalBoostsResponse.get().getBoostsForBuy().stream()
          .filter(boost -> "BoostFullAvailableTaps".equals(boost.getId()) && boost.getCooldownSeconds() == 0)
          .findFirst();

      if (optionalBoostDetailResponse.isPresent()) {
        BuyBoostRequest buyBoostRequest = BuyBoostRequest.builder()
            .boostId("BoostFullAvailableTaps")
            .timestamp(System.currentTimeMillis() / 1000)
            .build();
        sendRequest(authorization, "/clicker/buy-boost", buyBoostRequest, Object.class);
        System.out.println("Full Energy purchase for token complete " + authorization);
      }
    } else {
      System.err.println("Failed to retrieve the list of boosts ");
    }
  }

  private static <T> Optional<T> sendRequest(String authorization, String urlSuffix, Object payload, Class<T> responseClass) throws IOException, InterruptedException {
    HttpRequest request = buildRequest(authorization, urlSuffix, payload);
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    if (response.statusCode() == 200) {
      return Optional.of(objectMapper.readValue(response.body(), responseClass));
    } else {
      System.err.println("Failed to retrieve data for " + urlSuffix + ". Status code: " + response.statusCode());
      return Optional.empty();
    }
  }

  private static HttpRequest buildRequest(String authorization, String urlSuffix, Object payload)
      throws JsonProcessingException {
    HttpRequest.Builder builder = HttpRequest.newBuilder()
        .uri(URI.create(BASE_URL + urlSuffix))
        .timeout(java.time.Duration.ofSeconds(10))
        .header("Content-Type", "application/json")
        .header("Authorization", "Bearer " + authorization);

    builder.POST(payload != null ? HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload)) : HttpRequest.BodyPublishers.noBody());

    return builder.build();
  }
}

