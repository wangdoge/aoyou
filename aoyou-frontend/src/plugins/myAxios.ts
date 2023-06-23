import axios from "axios";
import * as process from "process";
import {showFailToast, showToast} from "vant";

const isDev=process.env.NODE_ENV==='development';

const myAxios=axios.create({
    baseURL:isDev? 'http://localhost:8088/api':'http://aoyou-backend.xmmxff.com:8088/api/'
});
myAxios.defaults.withCredentials=true;


// 添加请求拦截器
myAxios.interceptors.request.use(function (config) {
    return config;
}, function (error) {
    // 对请求错误做些什么
    return Promise.reject(error);
});

// 添加响应拦截器
myAxios.interceptors.response.use(function (response) {
    // 2xx 范围内的状态码都会触发该函数。
    // 对响应数据做点什么
    //未登录跳转到登录页
    if(response.data.code!==0){
        if(response.data.code===40100){
            const redirectUrl=window.location.href;
            showFailToast('请先登录');
            window.location.href=`/user/login/phone?redirect=${redirectUrl}`;
        }
        showFailToast(response.data.description!=''?response.data.description:response.data.message);
    }
    return response.data;
}, function (error) {
    // 超出 2xx 范围的状态码都会触发该函数。
    // 对响应错误做点什么
    return Promise.reject(error);
});

export default myAxios