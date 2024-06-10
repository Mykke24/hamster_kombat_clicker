package io.hamster_kombat;

import io.hamster_kombat.client.HamsterKombatClient;
import io.hamster_kombat.client.responses.ClickResponse;
import io.hamster_kombat.utils.LoggerUtil;
import io.hamster_kombat.utils.RandomUtil;
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
    LoggerUtil.log("Input your token:");
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
      boolean justBuyBoost = false;
      Optional<ClickResponse> optional = HamsterKombatClient.clickWithAPI(authorization);
      boolean isOutOfEnergy = optional.isPresent() && optional.get().getClickerUser().getAvailableTaps() < 10;
      if (isOutOfEnergy) {
        justBuyBoost = HamsterKombatClient.checkBuyBoost(authorization);
      }

      if (isOutOfEnergy && !justBuyBoost) {
        int sleepTime = RandomUtil.randomSleepTime();
        LoggerUtil.log("You are out of energy... Let's sleep " + sleepTime + " minutes");
        Thread.sleep(sleepTime * 60 * 1000);
        break;
      }
    }
  }


}