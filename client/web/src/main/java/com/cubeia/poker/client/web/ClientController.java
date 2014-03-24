package com.cubeia.poker.client.web;

import com.cubeia.backoffice.operator.api.OperatorConfigParamDTO;
import com.cubeia.backoffice.operator.client.OperatorServiceClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static com.cubeia.backoffice.operator.api.OperatorConfigParamDTO.CLIENT_TITLE;
import static com.cubeia.backoffice.operator.api.OperatorConfigParamDTO.CSS_URL;

@Controller
public class ClientController {

    @Value("${default.skin}")
    private String defaultSkin;
    
    @Value("${firebase.host:}")
    private String firebaseHost;

    @Value("${firebase.http-port:-1}")
    private int firebaseHttpPort;

    @Value("${google.analytics.id}")
    private String googleAnalyticsId;

    @Value("${uservoice.id}")
    private String userVoiceId;

    @Value("${operator-api.service.url}")
    private String operatorApiBaseUrl;

    @Value("${player-api.service.url}")
    private String playerApiBaseUrl;

    @Value("${addthis.pubid}")
    private String addThisPubId;

    @Value("${pure.token.enabled}")
    private boolean trueTokenEnabled;

    // @Value("${operator.config.cache-ttl}")
    // private Long configCacheTtl;
    
    @Resource(name = "operatorService")
    private OperatorServiceClient operatorService;
    
    private final String SAFE_PATTER = "[a-zA-Z0-9\\.\\-_]*";
    
    // TODO: Cache config (see below), we don't want to hit the 
    // oeprator config too hard /LJN
    /*private final LoadingCache<Long, Map<OperatorConfigParamDTO,String>> operatorConfig = 
    		CacheBuilder.newBuilder().expireAfterAccess(30000, MILLISECONDS).build(new CacheLoader<Long, Map<OperatorConfigParamDTO,String>>() {
				
				@Override
				public Map<OperatorConfigParamDTO,String> load(Long id) throws Exception {
					return operatorService.getConfig(id);
				}
			});*/



    @RequestMapping("/")
    public String handleDefault(HttpServletRequest request, ModelMap modelMap) {
        String servletPath = request.getServletPath();
        return "redirect:"+servletPath+"/"+defaultSkin;
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
        checkSetFirebaseAttributes(modelMap);
        return "index";
    }

    @RequestMapping(value = {"/{operatorId}/{skin}"})
    public String handleStartWithOperator(HttpServletRequest request, ModelMap modelMap,
                              @PathVariable("operatorId") Long operatorId, @PathVariable("skin") String skin ) {

        modelMap.addAttribute("operatorId",operatorId);
        return handleStart(request,modelMap,skin);
    }

	private void checkSetFirebaseAttributes(ModelMap modelMap) {
		if(firebaseHost != null && firebaseHost.length() > 0) {
        	modelMap.addAttribute("firebaseHost", firebaseHost);
        }
        if(firebaseHttpPort != -1) {
        	modelMap.addAttribute("firebaseHttpPort", firebaseHttpPort);
        }
        if(googleAnalyticsId != null) {
            modelMap.addAttribute("googleAnalyticsId", googleAnalyticsId);
        }
        if(userVoiceId != null) {
            modelMap.addAttribute("userVoiceId", userVoiceId);
        }
        if(playerApiBaseUrl !=null) {
            modelMap.addAttribute("playerApiBaseUrl",playerApiBaseUrl);
        }
        if(operatorApiBaseUrl!=null) {
            modelMap.addAttribute("operatorApiBaseUrl",operatorApiBaseUrl);
        }
        if(addThisPubId!=null) {
            modelMap.addAttribute("addThisPubId",addThisPubId);
        }
	}


    @RequestMapping(value = {"/skin/{skin}/operator/{operatorId}/token/{token}"})
    public String handleStartWithTokenURL(HttpServletResponse response,
                                          HttpServletRequest request,
                                          @PathVariable("skin") String skin,
                                          @PathVariable("operatorId") Long operatorId,
                                          @PathVariable("token") String token) {

        return setCookieAndRedirect(request,response, skin, operatorId, token);
    }

    private String setCookieAndRedirect(HttpServletRequest request,
                                        HttpServletResponse response,
                                        String skin,
                                        Long operatorId,
                                        String token) {

        addCookie(response, "token", token);
        String servletPath = request.getServletPath();
        return String.format("redirect:%s/skin/%s/operator/%s",servletPath,skin,operatorId.toString());
    }

    private void addCookie(HttpServletResponse response, String cookieName, String cookieValue) {
        Cookie tokenCookie = new Cookie(cookieName, cookieValue);
        tokenCookie.setPath("/");
        tokenCookie.setMaxAge(86400);
        response.addCookie(tokenCookie);
    }

    @RequestMapping(value = "/skin/{skin}/operator/{operatorId}")
    public String handleStartWithTokenCookie(HttpServletRequest request,
                                             ModelMap modelMap,
                                             @PathVariable("skin") String skin,
                                             @PathVariable("operatorId") Long operatorId,
                                             @CookieValue(value = "token",required = false) String token) {
        return doHandleStartWithToken(request, modelMap, skin, operatorId, token, trueTokenEnabled);
    }

    @RequestMapping(value = "/session/skin/{skin}/operator/{operatorId}")
    public String handleStartWithSessionCookie(HttpServletRequest request, ModelMap modelMap,
                                             @PathVariable("skin") String skin,
                                             @PathVariable("operatorId") Long operatorId,
                                             @CookieValue(value = "session") String session) {
        return doHandleStartWithToken(request, modelMap, skin, operatorId, session, trueTokenEnabled);
    }

    @RequestMapping(value = {"/skin/{skin}/operator/{operatorId}/session/{token}"})
    public String handleStartWithPureToken(HttpServletRequest request,
                                           HttpServletResponse response,
                                           @PathVariable("skin") String skin,
                                           @PathVariable("operatorId") Long operatorId,
                                           @PathVariable("session") String session) {

        response.addCookie(new Cookie("session",session));
        String servletPath = request.getServletPath();
        return String.format("redirect:%s/session/skin/%s/operator/%s",servletPath,skin,operatorId.toString());
    }

    private String doHandleStartWithToken(HttpServletRequest request, ModelMap modelMap, String skin, Long operatorId,
        String token, boolean pure) {
        modelMap.addAttribute("cp",request.getContextPath());
        modelMap.addAttribute("operatorId",operatorId);

        Map<OperatorConfigParamDTO, String> opConfig = safeGetOperatorConfig(operatorId);
        
        if(token==null || !token.matches(SAFE_PATTER)) {
            modelMap.addAttribute("token","");
        } else {
            modelMap.addAttribute("token",token);
        }
        if(skin==null || !skin.matches(SAFE_PATTER)) {
            modelMap.addAttribute("skin","");
        }
        if(opConfig != null && opConfig.get(CSS_URL) != null) {
            modelMap.addAttribute("cssOverride", opConfig.get(CSS_URL));
        }

        if(opConfig!=null && opConfig.get(CLIENT_TITLE) != null) {
            modelMap.addAttribute("clientTitle", opConfig.get(CLIENT_TITLE));
        }

        modelMap.addAttribute("pureToken", pure);
        
        checkSetFirebaseAttributes(modelMap);
        
        return "index";
    }
    
	private Map<OperatorConfigParamDTO, String> safeGetOperatorConfig(Long operatorId) {
		try {
			return operatorService.getConfig(operatorId); // operatorConfig.get(operatorId);
		} catch (Exception e) {
			Logger.getLogger(getClass()).error("failed to get operator config", e);
			return null;
		}
	}

    @RequestMapping(value = {"/operator/{operatorId}/token/{token}"})
    public String handleStartWithTokenAndDefaultSkin(HttpServletRequest request,
                                                     HttpServletResponse response,
                                                     @PathVariable("operatorId") Long operatorId,
                                                     @PathVariable("token") String token) {

        return setCookieAndRedirect(request, response, defaultSkin, operatorId, token);
    }
    
    @RequestMapping(value = {"/operator/{operatorId}/session/{token}"})
    public String handleStartWithPureTokenAndDefaultSkin(HttpServletRequest request, ModelMap modelMap,
                                       @PathVariable("operatorId") Long operatorId,
                                       @PathVariable("token") String token) {

        return handleStartWithTokenCookie(request, modelMap, defaultSkin, operatorId, token);
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
    @RequestMapping(value = {"/ping"})
    public @ResponseBody String ping() {
        return "";
    }

    public void setDefaultSkin(String defaultSkin) {
        this.defaultSkin = defaultSkin;
    }
}
