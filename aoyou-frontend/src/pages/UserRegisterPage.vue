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
  console.log(route.query)
  const onSubmit = async () => {
      if (password.value!==checkPassword.value){
          showFailToast('两次输入密码不一致');
          return false;
      }
      if(username.value===''){
          username.value=userAccount.value;
      }
      const res=await myAxios.post('user/register',{
          userAccount:userAccount.value,
          password:password.value,
          checkPassword:checkPassword.value,
          username:username.value
      })
      console.log('res',res)
      if(res.data&&res.code===0){
          showSuccessToast('注册成功')
          router.push({
              path:'/user/login',
              query:{
                  userAccount:userAccount.value,
                  password:password.value
              }
          })
          // const redirectUrl=route.query?.redirect ?? '/';
          // window.location.href=redirectUrl;
      }else {

      }
  };


</script>

<template>
    <van-form @submit="onSubmit">
        <van-cell-group inset>
            <van-field
                    v-model="userAccount"
                    name="账号"
                    label="账号"
                    placeholder="请输入手机号"
                    :rules="[{ required: true, message: '请填写用户名' }]"
            />
            <van-field
                    v-model="password"
                    type="password"
                    name="密码"
                    label="密码"
                    placeholder="请输入密码"
                    :rules="[{ required: true, message: '请填写密码' }]"
            />
            <van-field
                v-model="checkPassword"
                type="password"
                name="密码"
                label="确认密码"
                placeholder="请再次输入密码"
                :rules="[{ required: true, message: '请确认密码' }]"
            />
            <van-field
                v-model="username"
                name="用户名"
                label="用户名"
                placeholder="(选填)请输入昵称"
            />
        </van-cell-group>
        <div style="margin: 16px;">
            <van-button round block type="primary" native-type="submit">
                登录
            </van-button>
        </div>
    </van-form>

</template>

<style scoped>

</style>