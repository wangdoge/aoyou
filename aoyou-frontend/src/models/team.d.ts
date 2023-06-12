import {UserType} from "./user";

/**
 * 用户类别
 *
 */
export type TeamType = {
    id?: number;
    name: string,
    description: string,
    expireTime?: Date,
    maxNum:string,
    password?: string,
    status:string,
    createTime: Date;
    updateTime: Date;
    createUser?:UserType;
    hasJoinNum?:number;
    userList?:UserType[];
};