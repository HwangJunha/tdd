package com.around.tdd.vo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberSeq;
    @NotNull
    private String id;
    @NotNull
    private String password;
    @NotNull
    private Integer state;

    @Builder
    public Member(String id, String password, Integer state) {
        this.id = id;
        this.password = password;
        this.state = state;
    }

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private MemberInfo memberInfo;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MemberDeliveryInfo> memberDeliveryInfo;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MemberAuth> memberAuthList;

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

    public void setMemberAuthList(List<MemberAuth> memberAuthList) {
        this.memberAuthList = memberAuthList;
    }

    public void setPassword(@NotNull String password) {
        this.password = password;
    }

    public void setState(@NotNull Integer state) {
        this.state = state;
    }
}