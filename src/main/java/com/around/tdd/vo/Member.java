package com.around.tdd.vo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "memberSeq", referencedColumnName = "memberSeq")
    private MemberInfo memberInfo;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return Objects.equals(memberSeq, member.memberSeq) && Objects.equals(id, member.id) && Objects.equals(password, member.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberSeq, id, password);
    }
}
