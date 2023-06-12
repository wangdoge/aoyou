/**
 * 用户类别
 *
 */
export type UserType = {
    id?: number;
    planetCode?: string;
    username?: string;
    avatarUrl?: string;
    userAccount?: string;
    gender?: number;
    phone?: string;
    email?: string;
    userStatus?: number;
    createTime?: Date;
    tags: string[];
    userRole?: number;
    profile?: string;
};