package com.around.tdd.vo;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class MemberAuth {

    @EmbeddedId
    private MemberAuthId memberAuthId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("memberSeq")
    @JoinColumn(name = "member_seq")
    @JsonIgnore
    private Member member;


    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("memberAuthDictionarySeq")
    @JoinColumn(name = "member_auth_dictionary_seq")
    @JsonManagedReference
    private MemberAuthDictionary memberAuthDictionary;
}
