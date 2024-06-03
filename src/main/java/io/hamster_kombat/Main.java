package io.hamster_kombat;

import io.hamster_kombat.client.HamsterKombatClient;
import io.hamster_kombat.client.responses.ClickResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Main {


  public static void main(String[] args) throws IOException, InterruptedException {
    List<String> authorizationList = readAuthorizationTokens("authorization.csv");

    while (true) {
      for (String authorization : authorizationList) {
        runForAuthorization(authorization);
      }
      System.out.println("Đã chạy xong tất cả các token, nghỉ 1 giây rồi chạy lại từ đầu...");
      Thread.sleep(1000);
    }
  }

  private static List<String> readAuthorizationTokens(String filePath) throws IOException {
    List<String> lines = Files.readAllLines(Paths.get(filePath));
    List<String> tokens = new ArrayList<>();
    for (String line : lines) {
      String trimmedLine = line.trim();
      if (!trimmedLine.isEmpty()) {
        tokens.add(trimmedLine);
      }
    }
    return tokens;
  }

  private static void runForAuthorization(String authorization) throws IOException, InterruptedException {
    HamsterKombatClient.checkTasks(authorization);

    while (true) {
      Optional<ClickResponse> optional = HamsterKombatClient.clickWithAPI(authorization);
      if (optional.isPresent() && optional.get().getClickerUser().getAvailableTaps() < 10) {
        System.out.println("Token " + authorization + " có năng lượng nhỏ hơn 10. Chuyển token tiếp theo...");
        break;
      }
      Thread.sleep(1000);
    }
  }


}