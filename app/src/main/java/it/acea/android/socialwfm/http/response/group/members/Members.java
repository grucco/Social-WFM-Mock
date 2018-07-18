package it.acea.android.socialwfm.http.response.group.members;

import it.acea.android.socialwfm.app.model.Profile;

/**
 * Created by Raphael on 19/11/2015.
 */
public class Members {

    private String MemberType;
    private Profile Member;

    public String getMemberType() {
        return MemberType;
    }

    public void setMemberType(String memberType) {
        MemberType = memberType;
    }

    public Profile getMember() {
        return Member;
    }

    public void setMember(Profile member) {
        Member = member;
    }
}
