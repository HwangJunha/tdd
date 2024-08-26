package com.around.tdd.vo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Member {

    @Id
    @GeneratedValue
    private Long memberSeq;
    @NotNull
    private String id;
    @NotNull
    private String password;
    @NotNull
    private Integer state;
}
