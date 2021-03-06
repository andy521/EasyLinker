package com.easylinker.proxy.server.app.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.easylinker.proxy.server.app.constants.result.ReturnResult;
import com.easylinker.proxy.server.app.dao.DeviceGroupRepository;
import com.easylinker.proxy.server.app.dao.DeviceRepository;
import com.easylinker.proxy.server.app.model.device.Device;
import com.easylinker.proxy.server.app.model.device.DeviceGroup;
import com.easylinker.proxy.server.app.model.user.AppUser;
import io.netty.handler.codec.json.JsonObjectDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

/**
 * 设备service
 */
@Service("DeviceService")
public class DeviceService {
    @Autowired

    DeviceRepository deviceRepository;


    @Autowired
    DeviceGroupRepository deviceGroupRepository;

    public void save(Device device) {
        deviceRepository.save(device);

    }

    public void delete(Device device) {
        deviceRepository.delete(device);

    }

    public Device findADevice(Long id) {
        return deviceRepository.findTopById(id);
    }


    public JSONObject getAllDevicesByAppUser(AppUser appUser, Pageable pageable) {
        JSONArray data = new JSONArray();
        JSONObject pageJson = new JSONObject();
        Page<Device> dataPage = deviceRepository.findAllByAppUser(appUser, pageable);

        pageJson.put("page", dataPage.getNumber());
        pageJson.put("totalElements", dataPage.getTotalElements());
        pageJson.put("totalPages", dataPage.getTotalPages());
        pageJson.put("size", dataPage.getSize());
        pageJson.put("isLast", dataPage.isLast());
        pageJson.put("isFirst", dataPage.isFirst());

        for (Device device : dataPage.getContent()) {
            JSONObject deviceJson = new JSONObject();
            deviceJson.put("id", device.getId());
            deviceJson.put("groupId", device.getDeviceGroup().getId());
            deviceJson.put("groupName", device.getDeviceGroup().getGroupName());
            deviceJson.put("isOnline", device.isOnline());
            deviceJson.put("name", device.getDeviceName());
            deviceJson.put("barCode", device.getBarCode());
            deviceJson.put("lastActiveDate", device.getLastActiveDate());
            deviceJson.put("location", device.getLocation().toString());
            deviceJson.put("describe", device.getDeviceDescribe());
            data.add(deviceJson);
        }
        pageJson.put("data", data);
        return pageJson;
    }


    public JSONObject getAllDevicesByAppUserAndGroup(AppUser appUser, DeviceGroup deviceGroup, Pageable pageable) {
        JSONArray data = new JSONArray();
        Page<Device> dataPage = deviceRepository.findAllByAppUserAndDeviceGroup(appUser, deviceGroup, pageable);


        JSONObject pageJson = new JSONObject();
        pageJson.put("page", dataPage.getNumber());
        pageJson.put("total", dataPage.getTotalPages());
        pageJson.put("size", dataPage.getSize());
        pageJson.put("isLast", dataPage.isLast());
        pageJson.put("isFirst", dataPage.isFirst());
        pageJson.put("totalPages", dataPage.getTotalPages());


        pageJson.put("totalElements", dataPage.getTotalElements());

        for (Device device : dataPage.getContent()) {
            JSONObject deviceJson = new JSONObject();
            deviceJson.put("id", device.getId());
            deviceJson.put("barCode", device.getBarCode());
            deviceJson.put("name", device.getDeviceName());
            deviceJson.put("isOnline", device.isOnline());
            deviceJson.put("lastActiveDate", device.getLastActiveDate());
            deviceJson.put("describe", device.getDeviceDescribe());
            deviceJson.put("location", device.getLocation().toString());
            data.add(deviceJson);
        }
        pageJson.put("data", data);
        return pageJson;
    }


    public JSONObject getAllDevices(Pageable pageable) {
        JSONArray data = new JSONArray();
        Page<Device> dataPage = deviceRepository.findAll(pageable);
        JSONObject pageJson = new JSONObject();

        pageJson.put("page", dataPage.getNumber());
        pageJson.put("total", dataPage.getTotalPages());
        pageJson.put("size", dataPage.getSize());
        pageJson.put("isLast", dataPage.isLast());
        pageJson.put("totalPages", dataPage.getTotalPages());
        pageJson.put("isFirst", dataPage.isFirst());
        pageJson.put("totalElements", dataPage.getTotalElements());
        for (Device device : dataPage.getContent()) {
            JSONObject deviceJson = new JSONObject();
            if (device.getAppUser() == null) {
                deviceJson.put("isBind", false);
            } else {
                deviceJson.put("isBind", true);
            }
            deviceJson.put("id", device.getId());
            deviceJson.put("isOnline", device.isOnline());
            deviceJson.put("barCode", device.getBarCode());
            deviceJson.put("openId", device.getOpenId());
            deviceJson.put("name", device.getDeviceName());
            deviceJson.put("describe", device.getDeviceDescribe());
            deviceJson.put("location", device.getLocation().getLocationDescribe());
            deviceJson.put("lastActiveDate", device.getLastActiveDate());
            data.add(deviceJson);
        }
        pageJson.put("data", data);
        return pageJson;
    }

    public List<Device> findAllDevice() {

        return deviceRepository.findAll();
    }

    /**
     * 获取当前用户的设备情况
     *
     * @param appUser
     * @return
     */

    public JSONObject getCurrentState(AppUser appUser) {
        JSONObject data = new JSONObject();
        int onLineCount = deviceRepository.findAllByAppUserAndIsOnline(appUser, true).size();
        int total = deviceRepository.findAllByAppUser(appUser).size();
        data.put("onLine", onLineCount);
        data.put("offLine", total - onLineCount);
        data.put("total", total);
        return data;

    }


    /**
     * 获取设备详情
     */
    public JSONObject getDeviceDetail(@PathVariable Long deviceId) {

        Device device = deviceRepository.findTopById(deviceId);
        if (device != null) {
            JSONObject deviceJson = new JSONObject();
            deviceJson.put("id", device.getId());
            if (device.getAppUser() != null) {
                deviceJson.put("userId", device.getAppUser().getId().intValue());
            } else {
                deviceJson.put("user", "暂未绑定用户");
            }

            DeviceGroup deviceGroup = device.getDeviceGroup();
            JSONObject groupJson = new JSONObject();
            groupJson.put("name", deviceGroup.getGroupName());
            groupJson.put("name", deviceGroup.getComment());
            groupJson.put("id", deviceGroup.getId());
            deviceJson.put("group", groupJson);
            deviceJson.put("isOnline", device.isOnline());
            deviceJson.put("barCode", device.getBarCode());
            deviceJson.put("openId", device.getOpenId());
            deviceJson.put("name", device.getDeviceName());
            deviceJson.put("describe", device.getDeviceDescribe());
            deviceJson.put("location", device.getLocation().getLocationDescribe());
            deviceJson.put("lastActiveDate", device.getLastActiveDate());
            return ReturnResult.returnDataMessage(1, "查询成功!", deviceJson);
        } else {
            return ReturnResult.returnTipMessage(0, "设备不存在!");
        }


    }
}
