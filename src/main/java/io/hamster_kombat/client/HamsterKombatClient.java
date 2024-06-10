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
import io.hamster_kombat.utils.LoggerUtil;
import io.hamster_kombat.utils.RandomUtil;
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
  private static Integer AVAILABLE_TAPS = null;

  public static Optional<ClickResponse> clickWithAPI(String authorization) {
    ClickRequest payload = ClickRequest.builder()
        .count(RandomUtil.randomCount())
        .availableTaps(AVAILABLE_TAPS != null ? AVAILABLE_TAPS : 7500)
        .timestamp(System.currentTimeMillis())
        .build();
    Optional<ClickResponse> optional = sendRequest(authorization, "/clicker/tap", payload, ClickResponse.class);
    if (optional.isPresent()) {
      LoggerUtil.log("Tapping: " + optional.get().getClickerUser());
    } else {
      LoggerUtil.error("Cannot tap.");
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
        LoggerUtil.log("Daily token attendance check complete " + authorization);
      }
    }

  }

  public static boolean checkBuyBoost(String authorization) {

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
        LoggerUtil.log("Full Energy purchase for token complete " + authorization);
        return true;
      }
    } else {
      LoggerUtil.error("Failed to retrieve the list of boosts ");
    }
    return false;
  }

  private static <T> Optional<T> sendRequest(String authorization, String urlSuffix, Object payload, Class<T> responseClass) {
    try {
      HttpRequest request = buildRequest(authorization, urlSuffix, payload);
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() == 200) {
        return Optional.of(objectMapper.readValue(response.body(), responseClass));
      } else {
        LoggerUtil.error("Failed to retrieve data for " + urlSuffix + ". Status code: " + response.statusCode());
        return Optional.empty();
      }
    }
    catch (Exception ex) {
      LoggerUtil.error("Failed to retrieve data for " + urlSuffix + ". Error: " +ex);
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

  public static void initAvailableTaps(String authorization) {
    if (AVAILABLE_TAPS != null)
      return;
    Optional<ClickResponse> optional = sendRequest(authorization, "/clicker/sync", null, ClickResponse.class);
    optional.ifPresent(
        clickResponse -> AVAILABLE_TAPS = clickResponse.getClickerUser().getAvailableTaps());
  }
}

