package com.mangomelancholy.mangoai.application.conversation;

public record ExpressionFragment(String contentFragment, ActorType actor, int fragmentNumber) {

  public enum ActorType {
    INITIAL_PROMPT, USER, SYSTEM, PAL
  }

  public ExpressionFragment {
    assert contentFragment != null;
    assert actor != null;
    assert fragmentNumber > 0;
  }
}
