package com.wso2.sample.claim.handler.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.registry.core.service.RegistryService;
import org.wso2.carbon.user.core.service.RealmService;

/**
 * @scr.component name="carbon.custom.claim.handler.dscomponent" immediate="true"
 */
public class CarbonCustomClaimHandlerDSComponent {
    private static Log log = LogFactory.getLog(CarbonCustomClaimHandlerDSComponent.class);
    private static RealmService realmService;
    private static RegistryService registryService;

    protected void activate(ComponentContext ctxt) {
        try {
            log.info("Carbon Custom Claim Handler activated successfully.");
        } catch (Exception e) {
            log.error("Failed to activate Carbon Custom Claim Handler ", e);
        }
    }

    protected void deactivate(ComponentContext ctxt) {
        if (log.isDebugEnabled()) {
            log.debug("Carbon Custom Claim Handler is deactivated ");
        }
    }
}
