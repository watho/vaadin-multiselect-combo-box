package de.wathoserver.vaadin;

public class User {
  private String surename;
  private String forename;

  public User(String forename, String surename) {
    this.forename = forename;
    this.surename = surename;
  }

  public String getForename() {
    return forename;
  }

  public void setForename(String forename) {
    this.forename = forename;
  }

  public String getSurename() {
    return surename;
  }

  public void setSurename(String surename) {
    this.surename = surename;
  }

  @Override
  public String toString() {
    return "(" + surename + ", " + forename + ")";
  }
}
