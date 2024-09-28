package com.around.tdd.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;




@Embeddable
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Getter
public class MemberAuthId implements Serializable {
    @Column(name = "member_seq")
    private Long memberSeq;
    @Column(name = "member_auth_dictionary_seq")
    private Long memberAuthDictionarySeq;


}
