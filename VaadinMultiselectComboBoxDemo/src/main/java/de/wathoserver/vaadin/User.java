package de.wathoserver.vaadin;

import java.util.Objects;

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

  @Override
  public int hashCode() {
    return Objects.hash(surename, forename);
  }

  @Override
  public boolean equals(Object obj) {
    final User user = (User) obj;
    return Objects.equals(surename, user.getSurename())
        && Objects.equals(forename, user.getForename());
  }
}
