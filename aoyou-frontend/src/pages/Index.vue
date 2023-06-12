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
        const userListData  =await myAxios.get('/user/recommend', {
            params: {
                pageNum:1,
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
            userList.value=userListData;
        }
    }
    loading.value=false;
}

watchEffect(()=>{
    loadData();
})

</script>

<template>
    <van-cell center title="标题">
        <template #right-icon>
            <van-switch v-model="idMatchMode" />
        </template>
    </van-cell>
    <user-card-list :user-list="userList" :loading="loading"></user-card-list>


    <van-empty image="search" description="数据为空"  v-if="userList.length<1||!userList"/>
</template>

<style scoped>

</style>