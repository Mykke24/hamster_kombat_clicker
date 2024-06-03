package io.hamster_kombat.client.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ClickerUserResponse {

  private int balanceCoins;
  private int level;
  private int availableTaps;
  private int maxTaps;

}
