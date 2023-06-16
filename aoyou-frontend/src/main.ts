import { createApp } from 'vue'
// @ts-ignore
import App from './App.vue'
import {
    Button,
    CellGroup,
    Collapse,
    CollapseItem, DatePicker,
    Field,
    Form,
    Icon, List,
    NavBar,
    Search,
    Tabbar,
    TabbarItem, TimePicker,
    Toast
} from "vant";
import 'vant/es/toast/style';
import * as VueRouter from "vue-router";
import routes from "./config/route.ts";
import 'vant/lib/index.css';
import '../global.css'

const app=createApp(App);
app.use(Button);
app.use(NavBar);
app.use(Icon);
app.use(Tabbar);
app.use(TabbarItem);
app.use(Toast);
app.use(Search);
app.use(Collapse);
app.use(CollapseItem);
app.use(Form);
app.use(Field);
app.use(CellGroup);
app.use(DatePicker);
app.use(TimePicker);
app.use(List);
const router = VueRouter.createRouter({
    // 内部提供了 history 模式的实现。为了简单起见，我们在这里使用 hash 模式。
    history: VueRouter.createWebHistory(),
    routes, // `routes: routes` 的缩写
})

app.use(router);
app.mount('#app')


// 每个路由都需要映射到一个组件。
// 我们后面再讨论嵌套路由。

