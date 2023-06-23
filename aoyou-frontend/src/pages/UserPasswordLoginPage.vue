<script setup lang="ts">
import {onMounted, ref} from 'vue';
  import myAxios from "../plugins/myAxios.ts";
  import {showFailToast, showSuccessToast, Toast} from 'vant';
  import {useRoute, useRouter} from "vue-router";


  const router=useRouter();
  const route=useRoute();

  const userAccount = ref(route.query.userAccount);
  const password = ref(route.query.password);
  const onSubmit = async () => {
      const res=await myAxios.post('user/login',{
          userAccount:userAccount.value,
          password:password.value
      })
      console.log(res)
      if(res.data&&res.code===0){
          showSuccessToast('登录成功')
          const redirectUrl=route.query?.redirect ?? '/';
          window.location.href=redirectUrl;
      }else {
      }
  };


</script>

<template>
    <van-form @submit="onSubmit">
        <div class="title">
            <p>请输入账号密码</p>
        </div>

        <van-cell-group id="textInput" inset>
            <van-field
                    v-model="userAccount"
                    name="账号"
                    label="账号"
                    placeholder="请输入账号"
                    :rules="[{ required: true, message: '请填写手机号' }]"
            />
            <van-field
                v-model="password"
                type="password"
                name="密码"
                label="密码"
                placeholder="请输入密码"
                :rules="[{ required: true, message: '请填写密码' }]"
            />
        </van-cell-group>
        <div style="margin: 22px 45px 35px 45px;">
            <van-button round block type="primary" native-type="submit">
                登录
            </van-button>
        </div>
        <a class="register" href="/user/register">用户注册</a>
        <a class="register" href="/user/login/phone">手机登录</a>
        <a class="register" href="/user/register">忘记密码</a>
    </van-form>

</template>

<style scoped>

.register{
    color: gray;
    font-size: 13px;
    margin-left: 52px;
    margin-top: 60px;
}

#textInput :deep(.van-field__label){
    margin-left: 15px;
    font-size: 16px;
}

.title{
    margin-left: 45px;
    margin-top: 90px;
    margin-bottom: 55px;
    font-family: "微軟正黑體 Light";
    font-size: 30px;
}

</style>