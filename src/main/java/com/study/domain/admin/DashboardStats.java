package com.study.domain.admin;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DashboardStats {
    private int totalMembers;
    private int todaySignups;
    private int totalPosts;
    private int totalComments;
}
