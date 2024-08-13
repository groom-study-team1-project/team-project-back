package deepdivers.commuity.domain.member.model;

import deepdivers.commuity.domain.member.model.vo.MemberStatus;

public class Member {

    private Long id;
    private String name;
    private String imageUrl;
    private String tel;
    private String githubAddr;
    private String blogAddr;
    private Integer postCount;
    private Integer commentCount;
    private MemberStatus status;

}
