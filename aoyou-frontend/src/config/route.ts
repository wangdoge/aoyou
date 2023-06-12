
// @ts-ignore
import Index from "../pages/Index.vue";
// @ts-ignore
import Team from "../pages/TeamPage.vue";
// @ts-ignore
import User from "../pages/UserPage.vue";
import Search from "../pages/SearchPage.vue";
import UserEditPage from "../pages/UserEditPage.vue";
import SearchResultPage from "../pages/SearchResultPage.vue";
import UserLoginPage from "../pages/UserLoginPage.vue";
import TeamPage from "../pages/TeamPage.vue";
import UserPage from "../pages/UserPage.vue";
import SearchPage from "../pages/SearchPage.vue";
import TeamAddPage from "../pages/TeamAddPage.vue";
import TeamUpdatePage from "../pages/TeamUpdatePage.vue";
import UserUpdatePage from "../pages/UserUpdatePage.vue";
import UserTeamCreatePage from "../pages/UserTeamCreatePage.vue";
import UserTeamJoinPage from "../pages/UserTeamJoinPage.vue";
import UserRegisterPage from "../pages/UserRegisterPage.vue";
import TeamInfoPage from "../pages/TeamInfoPage.vue";


// =定义一些路由


const routes = [
    { path: '/',title:'遨游大厅',component: Index },
    { path: '/team', title:'队伍大厅', component: TeamPage },
    { path: '/team/update', title:'队伍信息', component: TeamUpdatePage },
    { path: '/user', title:'个人中心', component: UserPage },
    { path: '/user/register', title:'队伍注册',component: UserRegisterPage},
    { path: '/user/edit', title:'编辑信息', component: UserEditPage },
    { path: '/user/list', component: SearchResultPage},
    { path: '/user/update', title:'个人信息', component: UserUpdatePage},
    { path: '/search',title:'用户搜索',component: SearchPage},
    { path: '/user/login',title:'登录遨游',component: UserLoginPage},
    { path: '/team/add', title:'创建队伍',component: TeamAddPage},
    { path: '/team/info', title:'创建队伍',component: TeamInfoPage},
    { path: '/user/team/join', title:'我加入的队伍',component: UserTeamJoinPage},
    { path: '/user/team/create', title:'我的队伍',component: UserTeamCreatePage},


]

export default routes;