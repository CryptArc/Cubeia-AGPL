package com.cubeia.poker.client.web;

import static com.cubeia.backoffice.operator.api.OperatorConfigParamDTO.CSS_URL;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cubeia.backoffice.operator.api.OperatorConfigParamDTO;
import com.cubeia.backoffice.operator.client.OperatorServiceClient;

@Controller
public class ClientController {

    @Value("${default.skin}")
    private String defaultSkin;

    // @Value("${operator.config.cache-ttl}")
    // private Long configCacheTtl;
    
    @Resource(name = "operatorService")
    private OperatorServiceClient operatorService;
    
    private final String SAFE_PATTER = "[a-zA-Z0-9\\.-_]*";
    
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

        Map<OperatorConfigParamDTO, String> opConfig = safeGetOperatorConfig(operatorId);
        
        if(token==null || !token.matches(SAFE_PATTER)) {
            modelMap.addAttribute("token","");
        }
        if(skin==null || !skin.matches(SAFE_PATTER)) {
            modelMap.addAttribute("skin","");
        }
        if(opConfig != null && opConfig.get(CSS_URL) != null) {
        	modelMap.addAttribute("cssOverride", opConfig.get(CSS_URL));
        }
        
        return "index";
    }

	private Map<OperatorConfigParamDTO, String> safeGetOperatorConfig(Long operatorId) {
		// try {
			return operatorService.getConfig(operatorId); // operatorConfig.get(operatorId);
		/*} catch (ExecutionException e) {
			Logger.getLogger(getClass()).error("failed to retreive operator config", e);
			return null;
		}*/
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
