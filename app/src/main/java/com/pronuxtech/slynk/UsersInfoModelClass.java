package com.pronuxtech.slynk;

public class UsersInfoModelClass {
    private String uid, profile_img, first_name, last_name, email, lower_name, username, phone, password,
            university_name, current_course, course_level,
            graduation_year, gender, date_of_birth;
    private int block, online;

    public UsersInfoModelClass(){

    }


    public UsersInfoModelClass(String uid, String profile_img, String first_name, String last_name, String email,
                               String lower_name, String username, String phone, String password,
                               String university_name, String current_course, String course_level, String graduation_year,
                               String gender, String date_of_birth, int block, int online) {
        this.uid = uid;
        this.profile_img = profile_img;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.lower_name = lower_name;
        this.username = username;
        this.phone = phone;
        this.password = password;
        this.university_name = university_name;
        this.current_course = current_course;
        this.course_level = course_level;
        this.graduation_year = graduation_year;
        this.gender = gender;
        this.date_of_birth = date_of_birth;
        this.block = block;
        this.online = online;
    }


    public String getUid() {
        return uid;
    }

    public String getProfile_img() {
        return profile_img;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getEmail() {
        return email;
    }

    public String getLower_name() {
        return lower_name;
    }

    public String getUsername() {
        return username;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }

    public String getUniversity_name() {
        return university_name;
    }

    public String getCurrent_course() {
        return current_course;
    }

    public String getCourse_level() {
        return course_level;
    }

    public String getGraduation_year() {
        return graduation_year;
    }

    public String getGender() {
        return gender;
    }

    public String getDate_of_birth() {
        return date_of_birth;
    }

    public int getBlock() {
        return block;
    }

    public int getOnline() {
        return online;
    }


//    SETTERS

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setProfile_img(String profile_img) {
        this.profile_img = profile_img;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLower_name(String lower_name) {
        this.lower_name = lower_name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUniversity_name(String university_name) {
        this.university_name = university_name;
    }

    public void setCurrent_course(String current_course) {
        this.current_course = current_course;
    }

    public void setCourse_level(String course_level) {
        this.course_level = course_level;
    }

    public void setGraduation_year(String graduation_year) {
        this.graduation_year = graduation_year;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setDate_of_birth(String date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public void setBlock(int block) {
        this.block = block;
    }

    public void setOnline(int online) {
        this.online = online;
    }
}
