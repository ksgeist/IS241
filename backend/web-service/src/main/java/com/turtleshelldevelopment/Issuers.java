package com.turtleshelldevelopment;

public enum Issuers {
    MFA_LOGIN("mfa-auth"), AUTHENTICATION("covid-19-dash");

    String issue;

    Issuers(String issue) {
        this.issue = issue;
    }
    public String getIssuer() {
        return this.issue;
    }
}
