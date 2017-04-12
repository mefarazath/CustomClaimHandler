package com.wso2.sample.claim.handler;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.application.authentication.framework.config.model.StepConfig;
import org.wso2.carbon.identity.application.authentication.framework.context.AuthenticationContext;
import org.wso2.carbon.identity.application.authentication.framework.exception.FrameworkException;
import org.wso2.carbon.identity.application.authentication.framework.handler.claims.impl.DefaultClaimHandler;
import org.wso2.carbon.identity.application.authentication.framework.model.AuthenticatedUser;
import org.wso2.carbon.utils.CarbonUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * This class should be configured in IS_HOME/repository/conf/security/application-authentication.xml at
 * ApplicationAuthentication.Extensions.ClaimHandler after putting at components/lib, to be effective.
 */
public class CustomClaimHandler extends DefaultClaimHandler {

    private static Log log = LogFactory.getLog(CustomClaimHandler.class);

    private static final String SCRIPT_NAME = "jsHook.js";
    private static final String SCRIPT_FUNCTION_VAR_NAME = "handle";


    public Map<String, String> handleClaimMappings(StepConfig stepConfig,
                                                   AuthenticationContext context,
                                                   Map<String, String> remoteAttributes,
                                                   boolean isFederatedClaims) throws FrameworkException {

        String serviceProviderName = context.getServiceProviderName();
        AuthenticatedUser authenticatedUser;

        if (stepConfig != null) {
            //calling from StepBasedSequenceHandler
            authenticatedUser = stepConfig.getAuthenticatedUser();
        } else {
            //calling from RequestPathBasedSequenceHandler
            authenticatedUser = context.getSequenceConfig().getAuthenticatedUser();
        }

        // let the super class give us the default claims
        Map<String, String> claims = super.handleClaimMappings(stepConfig, context, remoteAttributes, isFederatedClaims);

        if (claims == null) {
            claims = new HashMap<>();
        }

        // get some claims from an external store.
        claims.putAll(fetchExternalClaims(serviceProviderName, authenticatedUser));

        // transform / manipulate some claims
        transformClaims(serviceProviderName, authenticatedUser, claims);

        try {
            // transform claims with a JS Script
            transformClaimsWithScript(serviceProviderName, authenticatedUser, claims);
        } catch (FileNotFoundException | ScriptException | NoSuchMethodException e) {
            log.error("Error while modifying the claim map with JS Hook.... ", e);
        }

        return claims;
    }


    /**
     * Added method to retrieve claims from external sources. This results will be merged to the local claims when
     * returning final claim list, to be added to the SAML response, that is sent back to the SP.
     *
     * @param serviceProviderName : Service Provider name
     * @param authenticatedUser   : The user for whom we require claim values
     * @return
     */
    protected Map<String, String> fetchExternalClaims(String serviceProviderName,
                                                      AuthenticatedUser authenticatedUser) throws FrameworkException {

        // Call an external API an get the claims
        Map<String, String> externalClaims = new HashMap<String, String>();
        externalClaims.put("lucky_number", "734746475");
        externalClaims.put("status", "active");
        return externalClaims;
    }


    /**
     * Using this method we can play around with the claims. For example you can take two values and manipulate them
     * to create a new claim.
     *
     * @param serviceProvider
     * @param authenticatedUser
     * @return
     */
    protected void transformClaims(String serviceProvider,
                                   AuthenticatedUser authenticatedUser,
                                   Map<String, String> claimMap) {

        final String FIRST_NAME_CLAIM = "http://wso2.org/claims/givenname";
        final String LAST_NAME_CLAIM = "http://wso2.org/claims/lastname";

        final String FULL_NAME_CLAIM = "http://wso2.org/claims/fullname";


        /*
          As an example I am going to take the firstname and lastname claims and create a new claim for full name
         */
        String firstname = claimMap.get(FIRST_NAME_CLAIM);
        String lastname = claimMap.get(LAST_NAME_CLAIM);

        if (StringUtils.isEmpty(firstname)) {
            firstname = "Jimmy";
        }

        if (StringUtils.isEmpty(lastname)) {
            lastname = "Hooper";
        }

        claimMap.put(FULL_NAME_CLAIM, firstname + " " + lastname);
    }


    /**
     * Using this method we can play around with the claims using a JS Script. (A sample script can be found under
     * resources folder)
     *
     * @param serviceProvider
     * @param user
     * @param claimMap
     * @throws FileNotFoundException
     * @throws ScriptException
     * @throws NoSuchMethodException
     */
    protected void transformClaimsWithScript(String serviceProvider,
                                             AuthenticatedUser user,
                                             Map<String, String> claimMap)
                                             throws FileNotFoundException, ScriptException, NoSuchMethodException {

        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        engine.eval(new FileReader(getFilePath(SCRIPT_NAME)));

        Invocable invocable = (Invocable) engine;
        /*
            the js hook has a function with name 'handle' which takes the claimWrapper as the argument.
         */
        invocable.invokeFunction(SCRIPT_FUNCTION_VAR_NAME, serviceProvider, user, claimMap);
    }


    /*
        We are expecting our js hook at IS_HOME/repository/conf/<filename>
     */
    private String getFilePath(String fileName) {
        return CarbonUtils.getCarbonConfigDirPath() + File.separator + fileName;
    }


}
