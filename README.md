# Gorg
<img align="right" src="https://vignette.wikia.nocookie.net/muppet/images/3/38/JuniorGorg.jpg/revision/latest/scale-to-width-down/280?cb=20101120230645">

Gorg is a service that deletes projects and applications when they exists past their respective time-to-live labels.
The service is currently under development

The component is named after Junior Gorg from the TV-show Fraggle Rock (http://muppet.wikia.com/wiki/Junior_Gorg).

 ## Setup
 
 In order to use this project you must set repositories in your `~/.gradle/init.gradle` file
 
     allprojects {
         ext.repos= {
             mavenCentral()
             jcenter()
         }
         repositories repos
         buildscript {
          repositories repos
         }
     }

We use a local repository for distributionUrl in our gradle-wrapper.properties, you need to change it to a public repo in order to use the gradlew command. `../gradle/wrapper/gradle-wrapper.properties`

    <...>
    distributionUrl=https\://services.gradle.org/distributions/gradle-<version>-bin.zip
    <...>

## How it works
 The component only removes BuildConfigs/ApplicationDeployments/Projects that have the label "removeAfter".
 removeAfter on Projects are calculated based on the human readable ttl durationString "env/ttl" in AuroraConfig
 removeAfter on ApplicationDeployments are calculated based on the human readable ttl durationString "ttl" in AuroraConfig
 The component boober labels openshift/kuberneetes objects with the removeAfter label.
 Jenkins labels buildConfigs with removeAfter label. A project can configure ttl as an override in their Jenkinsfile.  
