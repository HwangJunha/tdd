package com.around.tdd.vo;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class MemberAuthDictionary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberAuthDictionarySeq;

    private String authName;

    @Builder
    private MemberAuthDictionary(String authName){
        this.authName = authName;
    }

    @OneToMany(mappedBy = "memberAuthDictionary", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<MemberAuth> memberAuthList;

    public void setMemberAuthList(List<MemberAuth> memberAuthList) {
        this.memberAuthList = memberAuthList;
    }
}
