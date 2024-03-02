/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

public class User {
    
    int id ;
    String name;
    String phone ;
    String password ;
    int role ;
    String role_name ;

    public User(int id, String name, String phone, String password, String role_name) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.role_name = role_name;
    }

    public User(int id, String name, int role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }

    public User(String phone, String password) {
        this.phone = phone;
        this.password = password;
    }
    
    

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getRole_name() {
        return role_name;
    }

    public void setRole_name(String role_name) {
        this.role_name = role_name;
    }
    
    
    
    
}
