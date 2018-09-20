package com.camerrow.camerrowproject;

public class CamerrowUser {

    private String email;
    private String name;
    private String username;
    private String profilePicture;
    private String databaseKey;


    public CamerrowUser() {
    }

    public CamerrowUser(String email, String name, String username, String profilePicture) {
        this.email = email;
        this.name = name;
        this.username = username;
        this.profilePicture = profilePicture;
    }

    public String getDatabaseKey() {
        return databaseKey;
    }

    public void setDatabaseKey(String databaseKey) {
        this.databaseKey = databaseKey;
    }




    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}
