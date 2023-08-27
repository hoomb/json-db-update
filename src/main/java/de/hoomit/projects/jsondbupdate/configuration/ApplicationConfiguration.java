package de.hoomit.projects.jsondbupdate.configuration;

public class ApplicationConfiguration {
    private static final ApplicationConfiguration instance = new ApplicationConfiguration();

    private String basePackage;
    private String dbUrl;
    private String dbUser;
    private String dbPassword;

    private ApplicationConfiguration() {
        this.basePackage = null;
        this.dbUrl = null;
        this.dbUser = null;
        this.dbPassword = null;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public String getDbUser() {
        return dbUser;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    private void setBasePackage(final String basePackage) {
        this.basePackage = basePackage;
    }

    private void setDbUrl(final String dbUrl) {
        this.dbUrl = dbUrl;
    }

    private void setDbUser(final String dbUser) {
        this.dbUser = dbUser;
    }

    private void setDbPassword(final String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public void init(final String basePackage, final String dbUrl,
                     final String dbUser, final String dbPassword) {
        instance.setBasePackage(basePackage.endsWith(".") ? basePackage : basePackage + ".");
        instance.setDbUrl(dbUrl);
        instance.setDbUser(dbUser);
        instance.setDbPassword(dbPassword);
    }

    public static ApplicationConfiguration getInstance() {
        return instance;
    }
}
