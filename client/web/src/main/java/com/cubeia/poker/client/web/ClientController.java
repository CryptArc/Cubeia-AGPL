package com.cubeia.poker.client.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ClientController {

    @Value("${default.skin}")
    private String defaultSkin;

    @RequestMapping("/")
    public String handleDefault(HttpServletRequest request, ModelMap modelMap) {
        return "redirect:/poker/" + defaultSkin;
    }

    @RequestMapping(value = {"/{skin}"})
    public String handleStart(HttpServletRequest request, ModelMap modelMap, @PathVariable("skin") String skin) {

        modelMap.addAttribute("cp",request.getContextPath());

        if(skin == null) {
            skin = defaultSkin;
        } else if(skin.matches("[a-zA-Z0-9]*")) {
            modelMap.addAttribute("skin",skin);
        }
        return "index";
    }


    @RequestMapping(value = {"/skin/{skin}/operator/{operatorId}/token/{token}"})
    public String handleStartWithToken(HttpServletRequest request, ModelMap modelMap,
                                       @PathVariable("skin") String skin,
                                       @PathVariable("operatorId") Long operatorId,
                                       @PathVariable("token") String token) {

        modelMap.addAttribute("cp",request.getContextPath());
        modelMap.addAttribute("operatorId",operatorId);
        modelMap.addAttribute("token",token);
        modelMap.addAttribute("skin",skin);

        return "index";
    }

    @RequestMapping(value = {"/operator/{operatorId}/token/{token}"})
    public String handleStartWithTokenAndDefaultSkin(HttpServletRequest request, ModelMap modelMap,
                                       @PathVariable("operatorId") Long operatorId,
                                       @PathVariable("token") String token) {

        return handleStartWithToken(request,modelMap,defaultSkin,operatorId,token);
    }

    public void setDefaultSkin(String defaultSkin) {
        this.defaultSkin = defaultSkin;
    }
}
