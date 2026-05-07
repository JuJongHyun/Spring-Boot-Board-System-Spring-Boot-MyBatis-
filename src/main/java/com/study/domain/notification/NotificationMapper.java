package com.study.domain.notification;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NotificationMapper {

    void save(NotificationResponse notification);

    List<NotificationResponse> findUnreadByReceiverId(@Param("receiverId") Long receiverId);

    void markAsRead(@Param("id") Long id);

    void markAllAsRead(@Param("receiverId") Long receiverId);

    int countUnread(@Param("receiverId") Long receiverId);
}
