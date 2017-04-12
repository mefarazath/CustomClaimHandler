/**
 * Modify the claims with this script
 * @param service_provider Service Provider name
 * @param user Authenticated user object
 * @param claim_map String, String key value pair map of claims
 */
var handle = function(service_provider, user, claim_map) {

    var val1 = claim_map.get("http://wso2.org/claims/givenname");
    var val2 = claim_map.get("http://wso2.org/claims/lastname");

    var custom_claim = "http://wso2.org/claims/js_mod_claim";
    var custom_claim_value = val1 + "_" + val2;

    claim_map.put(custom_claim, custom_claim_value)
};
