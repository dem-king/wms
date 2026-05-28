import { ref } from 'vue'
import { getDashboardLocationWeather } from '@/api/dashboard'
import type {
  DashboardLocationWeatherResponse,
  LocationWeatherViewData,
} from '@/types/location-weather'

const initialState = (): LocationWeatherViewData => ({
  status: 'idle',
  locationText: '当前位置暂不可用',
  weatherText: '天气暂不可用',
  detailText: '等待定位后获取实时天气',
})

const buildLocationText = (weather: DashboardLocationWeatherResponse) => {
  const parts = [weather.province, weather.city, weather.district].filter(Boolean)
  return [...new Set(parts)].join(' · ')
}

const buildDetailText = (weather: DashboardLocationWeatherResponse) => {
  const parts = [
    weather.windDirection ? `${weather.windDirection}风` : '',
    weather.windPower ? `${weather.windPower}级` : '',
    weather.humidity ? `湿度 ${weather.humidity}%` : '',
    weather.reportTime ? `${weather.reportTime}发布` : '',
  ].filter(Boolean)

  return parts.join(' · ')
}

const getCurrentPosition = () => new Promise<GeolocationPosition>((resolve, reject) => {
  navigator.geolocation.getCurrentPosition(resolve, reject, {
    enableHighAccuracy: true,
    timeout: 10000,
    maximumAge: 300000,
  })
})

export function useDashboardLocationWeather() {
  const locationWeather = ref<LocationWeatherViewData>(initialState())

  const updateErrorState = (detailText: string, status: LocationWeatherViewData['status'] = 'error') => {
    locationWeather.value = {
      status,
      locationText: status === 'denied' ? '定位失败' : '当前位置暂不可用',
      weatherText: '天气暂不可用',
      detailText,
    }
  }

  const loadLocationWeather = async () => {
    if (!navigator.geolocation) {
      updateErrorState('当前浏览器不支持定位')
      return
    }

    locationWeather.value = {
      status: 'loading',
      locationText: '正在获取当前位置...',
      weatherText: '正在获取天气...',
      detailText: '请稍候',
    }

    try {
      const position = await getCurrentPosition()
      const response = await getDashboardLocationWeather({
        latitude: position.coords.latitude,
        longitude: position.coords.longitude,
      })
      const weather = response.data
      locationWeather.value = {
        status: 'success',
        locationText: buildLocationText(weather),
        weatherText: `${weather.weather} ${weather.temperature}°C`,
        detailText: buildDetailText(weather),
      }
    } catch (error) {
      if (error instanceof GeolocationPositionError) {
        if (error.code === error.PERMISSION_DENIED) {
          updateErrorState('请开启定位权限后重试', 'denied')
          return
        }
        if (error.code === error.TIMEOUT) {
          updateErrorState('定位超时，请稍后重试')
          return
        }
        updateErrorState('定位失败，请检查定位服务')
        return
      }

      updateErrorState('位置与天气服务暂时不可用')
    }
  }

  return {
    locationWeather,
    loadLocationWeather,
  }
}
