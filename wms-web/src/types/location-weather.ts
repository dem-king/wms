export interface DashboardLocationWeatherResponse {
  province: string
  city: string
  district: string
  adcode: string
  weather: string
  temperature: string
  windDirection: string
  windPower: string
  humidity: string
  reportTime: string
}

export type LocationWeatherStatus = 'idle' | 'loading' | 'success' | 'error' | 'denied'

export interface LocationWeatherViewData {
  status: LocationWeatherStatus
  locationText: string
  weatherText: string
  detailText: string
}
