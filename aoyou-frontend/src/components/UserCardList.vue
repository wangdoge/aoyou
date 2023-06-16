<script setup lang="ts">

import {UserType} from "../models/user";
import {useRouter} from "vue-router";
import {ref} from "vue";

interface UserCardListProps{
    userList:UserType[];
    loading:boolean;
}
const props=withDefaults(defineProps<UserCardListProps>(),{
    userList:[],
    loading:true
})
const router=useRouter()
const showDialog=ref(false);

const onclick=()=>{
    showDialog.value=true;
}

const doConfirm=()=>{
    showDialog.value=false;
}
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
                <van-button style="height: 35px;margin-right: 20px;" @click="onclick">联系</van-button>
            </template>
        </van-card>
    </van-skeleton>
    <van-dialog v-model:show="showDialog" title="好友功能正在开发中~敬请期待"  @confirm="doConfirm">

    </van-dialog>


</template>

<style scoped>

</style>