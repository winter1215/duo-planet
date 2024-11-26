import {
	createSSRApp
} from "vue";
import App from "./App.vue";
import uviewPlus from 'uview-plus'

export function createApp() {
	const app = createSSRApp(App);
	app.use(uviewPlus)
	// 注入uview-plus的全局样式
	uni.$u = {
		config: {
			// 修改主题色
			color: {
				primary: '#2979ff'
			}
		}
	}
	return {
		app,
	};
}
