package com.tykj.wx.controller;

import com.tykj.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description TODO
 * @auther huran
 * @date
 **/
@RestController
@RequestMapping(value = "/rest/wx/latlng")
public class LatLongController {
    @GetMapping(value = "/latlngToAddress")
    public ApiResponse latlngToAddress(@RequestParam(value = "lat")String lat
    ,@RequestParam(value = "/lng")String lng) {
            return null;
    }
}
