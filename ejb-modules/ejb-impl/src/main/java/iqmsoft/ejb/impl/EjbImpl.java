package iqmsoft.ejb.impl;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.*;

import iqmsoft.api.ejb.local.EjbLocal;

import java.util.Date;

import static java.lang.String.format;

@Stateless
public class EjbImpl implements EjbLocal {

  static final String jobName = "TimerJob";

  // lifecycle

  @PostConstruct
  public void postConstruct() {
    System.out.println(format("%s EJB created.", getClass().getSimpleName()));
  }

  @PreDestroy
  public void preDestroy() {
    System.out.println(format("%s destroying...", getClass().getSimpleName()));
  }

  // impl

  @Resource
  SessionContext sessionContext;

  TimerHandle timerHandle;

  public void start() {
    System.out.println("EJB starting job...");

    if (null != timerHandle) {
      System.err.println("job already started!");
      return;
    }

    this.timerHandle = sessionContext.getTimerService()
                                     .createTimer(new Date(), 1500, jobName)
                                     .getHandle();
  }

  @Timeout
  public void invokeJob(final Timer timer) {
    if (null == timer) {
      System.err.println("timer cannot be null!");
      return;
    }

    if (timer.getInfo().equals(jobName)) {
      System.out.println(format("next at: %s", timer.getNextTimeout()));
    }
  }

  public void stop() {
    System.out.println("stopping job...");

    if (null == timerHandle) {
      System.err.println("job already stopped!");
      return;
    }

    timerHandle.getTimer().cancel();
    timerHandle = null;
  }
}
