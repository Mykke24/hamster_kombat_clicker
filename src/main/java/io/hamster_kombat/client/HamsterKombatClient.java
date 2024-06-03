package io.hamster_kombat.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import io.hamster_kombat.client.requests.ClickRequest;
import io.hamster_kombat.client.responses.BoostDetailResponse;
import io.hamster_kombat.client.responses.BoostsResponse;
import io.hamster_kombat.client.responses.ClickResponse;
import io.hamster_kombat.client.responses.ClickerUserResponse;
import io.hamster_kombat.client.responses.TaskDetailResponse;
import io.hamster_kombat.client.responses.TaskResponse;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HamsterKombatClient {

  private HamsterKombatClient() {}

  private static final String BASE_URL = "https://api.hamsterkombat.io";
  private static final HttpClient client = HttpClient.newBuilder().build();
  private static final ObjectMapper objectMapper = new ObjectMapper()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  public static ClickResponse clickWithAPI(String authorization) throws IOException, InterruptedException {
    ClickRequest payload = new ClickRequest(1, 7500, System.currentTimeMillis());
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(BASE_URL + "/clicker/tap"))
        .timeout(java.time.Duration.ofSeconds(10))
        .header("Content-Type", "application/json")
        .header("Authorization", "Bearer " + authorization)
        .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload)))
        .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    if (response.statusCode() == 200) {
      ClickResponse clickResponse = objectMapper.readValue(response.body(), ClickResponse.class);
      ClickerUserResponse clickerUser = clickResponse.getClickerUser();
      System.out.println("Đang tap: " + clickerUser);
      return clickResponse;
    } else {
      System.err.println("Không bấm được. Status code: " + response.statusCode());
      return null;
    }
  }

  public static void checkTasks(String authorization) throws IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(BASE_URL + "/clicker/list-tasks"))
        .timeout(java.time.Duration.ofSeconds(10))
        .header("Content-Type", "application/json")
        .header("Authorization", "Bearer " + authorization)
        .POST(HttpRequest.BodyPublishers.noBody())
        .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    if (response.statusCode() == 200) {
      TaskResponse taskResponse = objectMapper.readValue(response.body(), TaskResponse.class);
      for (TaskDetailResponse task : taskResponse.getTasks()) {
        if ("streak_days".equals(task.getId()) && !task.isCompleted()) {
          HttpRequest checkTaskRequest = HttpRequest.newBuilder()
              .uri(URI.create(BASE_URL + "/clicker/check-task"))
              .timeout(java.time.Duration.ofSeconds(10))
              .header("Content-Type", "application/json")
              .header("Authorization", "Bearer " + authorization)
              .POST(HttpRequest.BodyPublishers.ofString("{\"taskId\": \"streak_days\"}"))
              .build();

          client.send(checkTaskRequest, HttpResponse.BodyHandlers.ofString());
          System.out.println("Đã điểm danh hàng ngày cho token " + authorization);
        }
      }

      HttpRequest boostsRequest = HttpRequest.newBuilder()
          .uri(URI.create(BASE_URL + "/clicker/boosts-for-buy"))
          .timeout(java.time.Duration.ofSeconds(10))
          .header("Content-Type", "application/json")
          .header("Authorization", "Bearer " + authorization)
          .POST(HttpRequest.BodyPublishers.noBody())
          .build();

      HttpResponse<String> boostsResponse = client.send(boostsRequest, HttpResponse.BodyHandlers.ofString());

      if (boostsResponse.statusCode() == 200) {
        BoostsResponse boosts = objectMapper.readValue(boostsResponse.body(), BoostsResponse.class);
        BoostDetailResponse boostFullAvailableTaps = boosts.getBoostsForBuy().stream()
            .filter(boost -> "BoostFullAvailableTaps".equals(boost.getId()) && boost.getCooldownSeconds() == 0)
            .findFirst()
            .orElse(null);

        if (boostFullAvailableTaps != null) {
          HttpRequest buyBoostRequest = HttpRequest.newBuilder()
              .uri(URI.create(BASE_URL + "/clicker/buy-boost"))
              .timeout(java.time.Duration.ofSeconds(10))
              .header("Content-Type", "application/json")
              .header("Authorization", "Bearer " + authorization)
              .POST(HttpRequest.BodyPublishers.ofString("{\"boostId\": \"BoostFullAvailableTaps\", \"timestamp\": " + (System.currentTimeMillis() / 1000) + "}"))
              .build();

          client.send(buyBoostRequest, HttpResponse.BodyHandlers.ofString());
          System.out.println("Đã mua Full Energy cho token " + authorization);
        }
      } else {
        System.err.println("Không lấy được danh sách boosts. Status code: " + boostsResponse.statusCode());
      }
    } else {
      System.err.println("Không lấy được danh sách nhiệm vụ. Status code: " + response.statusCode());
    }
  }


}
