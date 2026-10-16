package com.example.user;

import jakarta.persistence.*;

@Entity 
@Table(name = "users")
public class AppUser{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false, length = 80)
    private String name;

    @Column (nullable = false)
    private String password;

    @Column (nullable = false, unique=true, length = 160 )
    private String email;

    protected AppUser(){
        
    }

    public AppUser(String name, String email, String password){
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public Long getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public String getEmail(){
        return email;
    }

    public String getPassword(){
        return password;
    }

}