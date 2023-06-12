<script setup lang="ts">

import {UserType} from "../models/user";
import {useRouter} from "vue-router";

interface UserCardListProps{
    userList:UserType[];
    loading:boolean;
}
const props=withDefaults(defineProps<UserCardListProps>(),{
    userList:[],
    loading:true
})
const router=useRouter()

console.log('prop',props.userList)
</script>

<template>

        <van-skeleton title avatar :row="3" :loading="props.loading" v-for="user in props.userList">
            <van-card
                @click="onClick"
                :desc="user.profile"
                :title="user.username"
                :thumb="user.avatarUrl"
                style="--van-card-title-size:15px"
            >
                <template #tags>
                    <van-tag plain type="danger" v-for="tag in user.tags" style="margin-right:7px;margin-top:5px  ">
                        {{tag}}
                    </van-tag>
                </template>
                <template #footer>
                    <van-button style="height: 35px;margin-right: 20px;">联系</van-button>
                </template>
            </van-card>
        </van-skeleton>


</template>

<style scoped>

</style>