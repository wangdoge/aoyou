-- auto-generated definition
create table user
(
    id           bigint auto_increment
        primary key,
    username     varchar(256)                       null comment '用户昵称',
    avatarUrl    varchar(1024)                      null comment '头像',
    userAccount  varchar(1024)                      null comment '账号',
    gender       tinyint                            null comment '性别',
    userPassword varchar(512)                       null comment '密码',
    phone        varchar(128)                       null comment '电话',
    email        varchar(512)                       null,
    userStatus   int      default 0                 null comment '状态(0=正常)',
    createTime   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null comment '更新时间',
    isDelete     tinyint  default 0                 null comment '是否删除',
    userRole     int      default 0                 null comment '用户角色 0-普通 1-管理员',
    planetCode   varchar(512)                       null comment '编号'
);

-- auto-generated definition
create table tag
(
    id         bigint auto_increment
        primary key,
    tagName    varchar(256)                       null comment '标签昵称',
    userId     bigint                             null comment '用户id',
    parentId   bigint                             null comment '父标签id',
    isParent   tinyint                            null comment '是否为父标签 0-不是，1-是',
    createTime datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP null comment '更新时间',
    isDelete   tinyint  default 0                 null comment '是否删除',
    constraint uniIdx_tagName
        unique (tagName)
)
    comment '标签';

-- create index idx_userId
--     on tag (userId);

-- //关联查询创建人用户信息
--         //select * from team t left join user u on t.userid =u.id
--     // SELECT * FROM team t
--               // JOIN team_name ut ON t.id=ut.teamid
--               // JOIN USER u ON ut.`userId`=u.`id`