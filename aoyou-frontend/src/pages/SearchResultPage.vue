<script setup lang="ts">
  import {useRoute, useRouter} from "vue-router";
  import {onMounted, ref} from "vue";
  import myAxios from "../plugins/myAxios.ts"
  import qs from 'qs'

  const userList=ref([]);

  const route=useRoute()
  const {tags}=route.query;

  onMounted(async() =>{
      const userListData  =await myAxios.get('/user/search/tags', {
          params: {
              tagNameList: tags
          },
          paramsSerializer: params => {
              return qs.stringify(params,{indices:false})
          }
      })
          .then(function (response) {
              console.log(response);
              return response?.data
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
  })


</script>

<template>
    <van-card
            v-for="user in userList"
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
    <van-empty image="search" description="暂时找不到该类玩家"  v-if="userList.length<1||!userList"/>
</template>

<style scoped>

</style>