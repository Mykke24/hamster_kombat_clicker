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
public class BoostDetailResponse {

  private String id;
  private int price;
  private int earnPerTap;
  private int maxTaps;
  private int cooldownSeconds;
  private int level;
  private int maxTapsDelta;
  private int earnPerTapDelta;
  private int maxLevel;

}
