package com.aliahon.contactApi.Domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Table(name="contact")
public class Contact {
    @Id
    @UuidGenerator
    @Column(name="id", unique=true, updatable = false)
    private String id;
    private String name;
    private String title;
    private String email;
    private String phone;
    private String status;
    private String adresse;
    private String imgUrl;

}
