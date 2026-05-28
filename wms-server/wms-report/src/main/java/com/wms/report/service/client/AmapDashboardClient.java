package com.wms.report.service.client;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wms.common.exception.BizException;
import com.wms.report.config.AmapProperties;
import com.wms.report.service.client.dto.AmapDashboardGeocodeDto;
import com.wms.report.service.client.dto.AmapDashboardWeatherDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 高德首页位置天气客户端
 * 统一封装逆地理编码和实时天气接口的调用与响应解析
 */
@Component
@RequiredArgsConstructor
public class AmapDashboardClient {

    private final AmapProperties amapProperties;

    /**
     * 调用高德逆地理编码接口
     *
     * @param latitude 纬度
     * @param longitude 经度
     * @return 逆地理编码结果
     */
    public AmapDashboardGeocodeDto reverseGeocode(Double latitude, Double longitude) {
        ensureKeyConfigured();
        Map<String, Object> params = new HashMap<>();
        params.put("key", amapProperties.getKey());
        params.put("location", longitude + "," + latitude);
        params.put("extensions", "base");
        params.put("radius", 1000);

        JSONObject response = parseResponse(HttpUtil.get(amapProperties.getGeocodeUrl(), params));
        JSONObject regeocode = response.getJSONObject("regeocode");
        if (regeocode == null) {
            throw new BizException("高德逆地理编码返回为空");
        }
        JSONObject addressComponent = regeocode.getJSONObject("addressComponent");
        if (addressComponent == null) {
            throw new BizException("高德逆地理编码缺少行政区信息");
        }

        AmapDashboardGeocodeDto dto = new AmapDashboardGeocodeDto();
        dto.setProvince(addressComponent.getStr("province"));
        dto.setCity(resolveCity(addressComponent.get("city"), dto.getProvince()));
        dto.setDistrict(addressComponent.getStr("district"));
        dto.setAdcode(addressComponent.getStr("adcode"));
        if (!StringUtils.hasText(dto.getAdcode())) {
            throw new BizException("高德逆地理编码未返回行政区编码");
        }
        return dto;
    }

    /**
     * 调用高德实时天气接口
     *
     * @param adcode 行政区编码
     * @return 实时天气结果
     */
    public AmapDashboardWeatherDto getLiveWeather(String adcode) {
        ensureKeyConfigured();
        Map<String, Object> params = new HashMap<>();
        params.put("key", amapProperties.getKey());
        params.put("city", adcode);
        params.put("extensions", "base");

        JSONObject response = parseResponse(HttpUtil.get(amapProperties.getWeatherUrl(), params));
        JSONArray lives = response.getJSONArray("lives");
        if (lives == null || lives.isEmpty()) {
            throw new BizException("高德天气接口未返回实时天气");
        }

        JSONObject live = lives.getJSONObject(0);
        AmapDashboardWeatherDto dto = new AmapDashboardWeatherDto();
        dto.setWeather(live.getStr("weather"));
        dto.setTemperature(live.getStr("temperature"));
        dto.setWindDirection(live.getStr("winddirection"));
        dto.setWindPower(live.getStr("windpower"));
        dto.setHumidity(live.getStr("humidity"));
        dto.setReportTime(live.getStr("reporttime"));
        return dto;
    }

    private void ensureKeyConfigured() {
        if (!StringUtils.hasText(amapProperties.getKey())) {
            throw new BizException("高德天气服务未配置 key");
        }
    }

    private JSONObject parseResponse(String responseBody) {
        JSONObject response = JSONUtil.parseObj(responseBody);
        if (!"1".equals(response.getStr("status"))) {
            throw new BizException("高德接口调用失败: " + response.getStr("info"));
        }
        return response;
    }

    private String resolveCity(Object cityValue, String province) {
        if (cityValue instanceof JSONArray cityArray) {
            if (cityArray.isEmpty()) {
                return province;
            }
            return cityArray.getStr(0);
        }
        String city = cityValue == null ? "" : String.valueOf(cityValue);
        return StringUtils.hasText(city) ? city : province;
    }
}
