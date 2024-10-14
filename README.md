<?xml version="1.0" encoding="UTF-8"?>
<wls:weblogic-web-app
        xmlns:wls="http://xmlns.oracle.com/weblogic/weblogic-web-app"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://xmlns.oracle.com/weblogic/weblogic-web-app
http://xmlns.oracle.com/weblogic/weblogic-web-app/1.4/weblogic-web-app.xsd">
    <context-root>/StagingAgent</context-root>
    <wls:container-descriptor>
        <wls:prefer-application-packages>
            <wls:package-name>org.slf4j.*</wls:package-name>
            <wls:package-name>org.springframework.*</wls:package-name>
            <wls:package-name>org.hibernate.*</wls:package-name>
            <wls:package-name>javax.validation.*</wls:package-name>
        </wls:prefer-application-packages>
    </wls:container-descriptor>
</wls:weblogic-web-app>
