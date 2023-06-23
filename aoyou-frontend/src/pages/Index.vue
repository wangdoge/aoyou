<script setup lang="ts">
import {useRoute, useRouter} from "vue-router";
import {onMounted, ref, watchEffect} from "vue";
import myAxios from "../plugins/myAxios.ts"
import qs from 'qs'
import UserCardList from "../components/UserCardList.vue";



const userList=ref([]);
const idMatchMode=ref<boolean>(false);
const route=useRoute();
const {tags}=route.query;
const loading =ref(false)

const loadData= async () =>{
    let userListData=[];
    loading.value=true;
    if(idMatchMode.value){
        const num =10;
        const userListData = await myAxios.get('/user/match', {
            params: {
                num,
            },
        })
            .then(function (response) {
                console.log('success',response);
                return response.data;
            })
            .catch(function (error) {
                console.log(error);
            })
        if(userListData){
            userListData.forEach(user=>{
                if(user.tags){
                    user.tags=JSON.parse(user.tags);
                }
            })
            userList.value=userListData;
        }
    }else {

    }
    loading.value=false;
}

watchEffect(()=>{
    loadData();
})

const list = ref([]);
const loadingBoolean = ref(false);
const finished = ref(false);
let pageNum=0 ;

const onLoad = async () => {
    pageNum=pageNum+1;
    console.log(pageNum)
    const userListData  =await myAxios.get('/user/recommend', {
        params: {
            pageNum:pageNum,
            pageSize:8
        },
        paramsSerializer: params => {
            return qs.stringify(params,{indices:false})
        }
    })
        .then(function (response) {

            console.log(response);
            return response.data.records;
        })
        .catch(function (error) {
            console.log(error);
        })
    if(userListData){
        userListData.forEach(user=>{
            if(user.tags){
                user.tags=JSON.parse(user.tags);
            }
        })
        if(pageNum===1){
            userList.value=userListData;
        }else {
            userList.value=userList.value.concat(userListData);
        }

        console.log(userList)
    }
    loadingBoolean.value=false;
};

</script>

<template>
    <van-notice-bar mode="closeable"
     left-icon="volume-o"
     text="欢迎来到遨游，点击下方按钮可匹配与你兴趣最相关的用户~"
    />

    <van-cell center title="心动匹配模式">
        <template #right-icon>
            <van-switch v-model="idMatchMode" />
        </template>
    </van-cell>

    <van-list
        v-model:loading="loadingBoolean"
        :finished="finished"
        finished-text="没有更多了"
        @load="onLoad"
        offset="300"
    >
        <user-card-list :user-list="userList" :loading="loading"></user-card-list>

    </van-list>

    <van-empty image="search" description="数据为空"  v-if="userList.length<1||!userList"/>
</template>

<style scoped>

</style>