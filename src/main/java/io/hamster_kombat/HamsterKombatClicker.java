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
import java.util.Scanner;

public class HamsterKombatClicker {

  private static final Scanner scanner = new Scanner(System.in);

  public static void main(String[] args) throws IOException, InterruptedException {
//    List<String> authorizationList = readAuthorizationTokens("authorization.csv");
    System.out.println("Input your token:");
    String token = scanner.nextLine();
    List<String> authorizationList = Arrays.asList(token);

    while (true) {
      for (String authorization : authorizationList) {
        runForAuthorization(authorization);
      }
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
    HamsterKombatClient.initAvailableTaps(authorization);
    HamsterKombatClient.checkTasks(authorization);

    while (true) {
      Optional<ClickResponse> optional = HamsterKombatClient.clickWithAPI(authorization);
      if (optional.isPresent() && optional.get().getClickerUser().getAvailableTaps() < 10) {
        System.out.println("You are out of energy... Let's sleep 5 minute");
        Thread.sleep(5 * 60 * 1000);
        break;
      }
    }
  }


}