<script setup lang="ts">
  import { ref } from 'vue';
  import myAxios from "../plugins/myAxios.ts";
  import {showFailToast, showSuccessToast, Toast} from 'vant';
  import {useRoute, useRouter} from "vue-router";

  const userAccount = ref('');
  const password = ref('');
  const checkPassword =ref('');
  const username =ref('');

  const router=useRouter();
  const route=useRoute();
  const tel=route.query.tel;
  console.log(tel);
  const onSubmit = async () => {
      if (password.value!==checkPassword.value){
          showFailToast('两次输入密码不一致');
          return false;
      }
      if(username.value===''){
          username.value=userAccount.value;
      }
      const res=await myAxios.post('user/register',{
          userAccount:tel,
          password:password.value,
          checkPassword:checkPassword.value,
          username:username.value,
          autoLogin:true
      })
      console.log('res',res)
      if(res.data&&res.code===0){
          showSuccessToast('注册成功')
          router.push({
              path:'/',
          })
          // const redirectUrl=route.query?.redirect ?? '/';
          // window.location.href=redirectUrl;
      }else {

      }
  };


</script>

<template>
  <div class="title">
    <p>欢迎加入遨游~</p>
  </div>
  <div class="titleTwo">
    <p>请完善您的个人信息</p>
  </div>
  <van-form @submit="onSubmit">
      <van-cell-group inset>
        <van-field
            style="margin-left: 15px"
            v-model="username"
            name="用户名"
            label="昵称"
            placeholder="(选填)请输入昵称"
        />
        <van-field
            style="margin-left: 15px"
            v-model="password"
            type="password"
            name="密码"
            label="密码"
            placeholder="请输入密码"
            :rules="[{ required: true, message: '请填写密码' }]"
        />
        <van-field
            style="margin-left: 15px"
            v-model="checkPassword"
            type="password"
            name="密码"
            label="确认密码"
            placeholder="请再次输入密码"
            :rules="[{ required: true, message: '请确认密码' }]"
        />

      </van-cell-group>
      <div style="margin: 22px 45px 35px 45px">
          <van-button round block type="primary" native-type="submit">
              加入遨游
          </van-button>
      </div>
  </van-form>

</template>

<style scoped>
  .title{
    margin-left: 45px;
    margin-top: 90px;
    font-family: "微軟正黑體 Light";
    font-size: 30px;
  }

  .titleTwo{
    margin-left: 45px;
    margin-bottom: 55px;
    font-family: "微軟正黑體 Light";
    font-size: 18px;
  }
</style>