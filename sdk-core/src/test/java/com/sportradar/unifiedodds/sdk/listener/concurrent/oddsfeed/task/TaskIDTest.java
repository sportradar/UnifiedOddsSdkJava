package com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.task;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TaskIDTest {

  @Test
  public void should_have_id() {
    TaskID taskID = TaskID.valueOf(123);

    assertEquals(123, taskID.getId());
  }

  @Test
  public void should_have_toString() {
    TaskID taskID = TaskID.valueOf(123);

    assertEquals("TaskID(id=123)", taskID.toString());
  }

  @Test
  public void should_calculate_hash() {
    TaskID taskID = TaskID.valueOf(123);

    assertThat(taskID.hashCode(), greaterThan(0));
  }

  @Test
  public void should_calculate_same_hashes() {
    for (int i = 0; i < 100; i++) {
      TaskID taskID1 = TaskID.valueOf(i + 1);
      TaskID taskID2 = TaskID.valueOf(i + 1);
      assertEquals(taskID1.hashCode(), taskID2.hashCode());
    }
  }

  @Test
  public void should_calculate_unique_hashes() {
    Set<Integer> hashes = new HashSet<>();
    int totalTasks = 100;

    for (int i = 0; i < totalTasks; i++) {
      TaskID taskID = TaskID.valueOf(i + 1);
      hashes.add(taskID.hashCode());
    }

    assertThat(hashes.size(), equalTo(totalTasks));
  }

  @Test
  public void should_calculate_random_hash_when_no_event_id() {
    Set<Integer> hashes = new HashSet<>();
    int totalTasks = 100;

    for (int i = 0; i < totalTasks; i++) {
      TaskID taskID = TaskID.none();
      hashes.add(taskID.hashCode());
    }

    assertThat(hashes.size(), greaterThan(totalTasks / 2));
  }
}