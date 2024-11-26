create table user
(
    id           bigint auto_increment comment 'id'
        primary key,
    userAccount  varchar(256) default ''                not null comment '账号',
    userPassword varchar(512) default ''                not null comment '密码',
    unionId      varchar(256)                           null comment '微信开放平台id',
    mpOpenId     varchar(256)                           null comment '公众号openId',
    userName     varchar(256)                           null comment '用户昵称',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userProfile  varchar(512)                           null comment '用户简介',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除'
)
    comment '用户' collate = utf8mb4_unicode_ci
                   row_format = DYNAMIC;

create table ums_comment
(
    id         bigint auto_increment
        primary key,
    content    varchar(255) default ''                not null,
    userId     bigint       default 0                 not null,
    postId     bigint       default 0                 not null,
    createTime datetime     default CURRENT_TIMESTAMP not null comment '创建时间'
)
    row_format = DYNAMIC;

create table diary
(
    id         bigint auto_increment comment 'id'
        primary key,
    content    text                               null comment '内容',
    tags       varchar(1024)                      null comment '标签列表（json 数组）',
    userId     bigint                             not null comment '创建用户 id',
    visible    tinyint  default 0                 not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除'
)
    comment '日记' collate = utf8mb4_unicode_ci
                   row_format = DYNAMIC;

create table ums_post_favour
(
    id         bigint auto_increment comment 'id'
        primary key,
    postId     bigint                             not null comment '帖子 id',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '帖子收藏' row_format = DYNAMIC;

create index idx_postId
    on ums_post_favour (postId);

create index idx_userId
    on ums_post_favour (userId);

create table ums_post_thumb
(
    id         bigint auto_increment comment 'id'
        primary key,
    postId     bigint                             not null comment '帖子 id',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '帖子点赞' row_format = DYNAMIC;

create index idx_postId
    on ums_post_thumb (postId);

create index idx_userId
    on ums_post_thumb (userId);

