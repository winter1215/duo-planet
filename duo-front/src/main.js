import {
	createSSRApp
} from "vue";
import store from './store'
import App from "./App.vue";
import uviewPlus from 'uview-plus'

export function createApp() {
	const app = createSSRApp(App);
	app.use(store)
	app.use(uviewPlus)
	uni.$u = {
		config: {
			color: {
				primary: '#2979ff'
			}
		}
	}
	return {
		app
	};
}
