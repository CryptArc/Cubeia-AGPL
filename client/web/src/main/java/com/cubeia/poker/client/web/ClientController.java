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

    private final String SAFE_PATTER = "[a-zA-Z0-9\\.-_]*";

    @RequestMapping("/")
    public String handleDefault(HttpServletRequest request, ModelMap modelMap) {
        return "redirect:/poker/" + defaultSkin;
    }

    @RequestMapping(value = {"/{skin}"})
    public String handleStart(HttpServletRequest request, ModelMap modelMap,
                              @PathVariable("skin") String skin) {

        modelMap.addAttribute("cp",request.getContextPath());

        if(skin == null) {
            skin = defaultSkin;
        } else if(!skin.matches(SAFE_PATTER)) {
            modelMap.addAttribute("skin","");
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

        if(token==null || !token.matches(SAFE_PATTER)) {
            modelMap.addAttribute("token","");
        }
        if(skin==null || !skin.matches(SAFE_PATTER)) {
            modelMap.addAttribute("skin","");
        }

        return "index";
    }

    @RequestMapping(value = {"/operator/{operatorId}/token/{token}"})
    public String handleStartWithTokenAndDefaultSkin(HttpServletRequest request, ModelMap modelMap,
                                       @PathVariable("operatorId") Long operatorId,
                                       @PathVariable("token") String token) {

        return handleStartWithToken(request,modelMap,defaultSkin,operatorId,token);
    }

    @RequestMapping(value = {"/skin/{skin}/hand-history/{tableId}"})
    public String handleHansHistory(HttpServletRequest request, ModelMap modelMap,
                                                     @PathVariable("skin") String skin,
                                                     @PathVariable("tableId") Integer tableId) {

        if(skin==null || !skin.matches(SAFE_PATTER)) {
            modelMap.addAttribute("skin","");
        }
        modelMap.addAttribute("tableId",tableId);
        modelMap.addAttribute("cp",request.getContextPath());
        return "hand-history";
    }

    public void setDefaultSkin(String defaultSkin) {
        this.defaultSkin = defaultSkin;
    }
}
