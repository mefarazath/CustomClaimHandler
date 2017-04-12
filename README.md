# Custom Claim Handler
---
This component allows to manipulate claims that are returned by Identity Framework for an authenticated user.

## Building From Source

Clone this repository (`https://github.com/mefarazath/CustomClaimHandler.git`) 

Use maven install to build
`mvn clean install`.

## Deploying to IS 5.3.0

* Copy **org.wso2.custom.extensions.claim.handler-1.0.0-SNAPSHOT.jar** file to **wso2is-5.3.0/repository/components/dropins**
 folder
 
* Copy the **jsHook.js** file found under resources to  **wso2is-5.3.0/repository/conf**
 
* Update the <ClaimHandler> tag in wso2is-5.3.0/repository/conf/identity/application-authentication.xml as follows,
````xml
<ClaimHandler>com.wso2.sample.claim.handler.CustomClaimHandler</ClaimHandler>
````

With this custom claim handler I am trying to demonstrate few use cases,

1. Retrieving claims from an external claim store

2. Modifying/Transforming claims - For example take the first name and last name claims and create a new claim for 
full name

3. Modifying/Transforming claims using JavaScript - I am using JVM's inbuilt Nashorn JS Engine for this.