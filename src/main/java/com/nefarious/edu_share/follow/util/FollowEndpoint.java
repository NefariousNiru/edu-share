package com.nefarious.edu_share.follow.util;

public class FollowEndpoint {
    public static final String FOLLOW = "/follow";
    public static final String USERNAME = "/{username}";
    public static final String FOLLOWERS = USERNAME + "/followers";
    public static final String FOLLOWING = USERNAME + "/following";
}
