<script setup lang="ts">
import {onMounted, ref} from 'vue';
  import myAxios from "../plugins/myAxios.ts";
  import {showFailToast, showSuccessToast, Toast} from 'vant';
  import {useRoute, useRouter} from "vue-router";


  const router=useRouter();
  const route=useRoute();

  const userAccount = ref(route.query.userAccount);
  const sms = ref();
  const telephone=ref();
  const onSubmit = async () => {
      const res=await myAxios.post('user/login/phone',{
          userAccount:telephone.value,
          code:sms.value
      })
      console.log(res)
      if(res.data&&res.code===0){
          showSuccessToast('登录成功')
          const redirectUrl=route.query?.redirect ?? '/';
          window.location.href=redirectUrl;
      }else if(res.data===null&&res.code===0){
          router.push({
            path:'/user/firstRegister',
            query:{
              tel:telephone.value
            }
          })
      }
  };

  const sendCode = async () =>{
    const res=await myAxios.post('/sms/sendCode',{
      phoneNumber:telephone.value,
    })
    if(res.data.result===1023){
      showFailToast("发送频率太快辣，过半分钟再试试吧~")
    }
    if(res.data.result===1024){
      showFailToast("今日短信登陆次数已达上限,麻烦切换账号密码登录")
    }
    if(res.data.result===1025){
      showFailToast("今日短信登陆次数已达上限,麻烦切换账号密码登录")
    }
    if(res.data.result===0){
      showSuccessToast("发送成功辣")
    }
    console.log('res',res)
  }


</script>

<template>
    <van-form @submit="onSubmit">
        <div class="title">
            <p>欢迎登录遨游</p>
        </div>

        <van-cell-group id="textInput" inset>
            <van-field
                    v-model="telephone"
                    name="手机"
                    label="手机号"
                    placeholder="请输入账号"
                    :rules="[{ required: true, message: '请填写手机号' }]"
            />
            <van-field
                v-model="sms"
                center
                clearable
                label="验证码"
                placeholder="请输入验证码"
            >
                <template #button>
                    <van-button  @click="sendCode" plain hairline size="small" type="primary" style="margin-right: 20px">发送</van-button>
                </template>
            </van-field>
        </van-cell-group>
        <div style="margin: 22px 45px 35px 45px;">
            <van-button round block type="primary" native-type="submit">
                登录
            </van-button>
        </div>
        <a class="register" href="/user/register">用户注册</a>
        <a class="register" href="/user/login/password">密码登录</a>
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