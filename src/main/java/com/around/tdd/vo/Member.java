package com.around.tdd.vo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
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

    @Builder
    public Member(Long memberSeq, String id, String password, Integer state) {
        this.memberSeq = memberSeq;
        this.id = id;
        this.password = password;
        this.state = state;
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "memberSeq", referencedColumnName = "memberSeq")
    private MemberInfo memberInfo;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "memberSeq", referencedColumnName = "memberSeq")
    private List<MemberDeliveryInfo> memberDeliveryInfo;


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

    public void setMemberInfo(MemberInfo memberInfo) {
        this.memberInfo = memberInfo;
    }

    public void setMemberDeliveryInfo(List<MemberDeliveryInfo> memberDeliveryInfo) {
        this.memberDeliveryInfo = memberDeliveryInfo;
    }
}