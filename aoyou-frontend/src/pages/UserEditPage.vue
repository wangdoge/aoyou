

<template>
    <van-form @submit="onSubmit">
        <van-cell-group inset>
            <van-field
                    v-model="editUser.currentValue"
                    :name="editUser.editKey"
                    :label="editUser.editName"
                    :placeholder="`请输入${editUser.editName}`"
                    :rules="[{ required: true, message: `请输入${editUser.editName}` }]"
            />
        </van-cell-group>
        <div style="margin: 16px;">
            <van-button round block type="primary" native-type="submit">
                提交
            </van-button>
        </div>
    </van-form>

</template>

<script setup lang="ts">

import {useRoute, useRouter} from "vue-router";
import {ref} from "vue";
import myAxios from "../plugins/myAxios.ts";
import qs from "qs";
import {showFailToast, showSuccessToast, showToast, Toast} from "vant";
import {getCurrentUser} from "../services/user.ts";
import {onMounted} from "vue/dist/vue";


  const route=useRoute();
    console.log(route.query);

  const editUser=ref({
      currentValue:route.query.currentValue,
      editKey:route.query.editKey,
      editName:route.query.editName
  })
  const router=useRouter();

  const onSubmit = async () => {
      const currentUser=await getCurrentUser();
      if(!currentUser){
        showFailToast('用户未登录')
      }

      const res=await myAxios.post('user/update',{
          'id':currentUser.id,
          [editUser.value.editKey]:editUser.value.currentValue,
      })

      if(res.data>0){
          router.back();
          showSuccessToast('修改成功')
      }else {
          showFailToast('修改失败')
      }

  };
</script>

<style scoped>

</style>