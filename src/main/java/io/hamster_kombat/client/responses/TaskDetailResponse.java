package io.hamster_kombat.client.responses;

import java.util.Date;
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
public class TaskDetailResponse {

  private String id;
  private int rewardCoins;
  private String periodicity;
  private String link;
  private Boolean isCompleted;
  private Date completedAt;

}
