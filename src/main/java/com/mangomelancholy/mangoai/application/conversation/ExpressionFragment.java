package com.mangomelancholy.mangoai.application.conversation;

public record ExpressionFragment(String contentFragment, long sequenceNumber) {

  public ExpressionFragment {
    assert contentFragment != null;
    assert sequenceNumber > 0;
  }
}
