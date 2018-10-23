# Gorg
<img align="right" src="https://vignette.wikia.nocookie.net/muppet/images/3/38/JuniorGorg.jpg/revision/latest/scale-to-width-down/280?cb=20101120230645">

Gorg is a service that deletes projects and applications when they exists past their respective time-to-live labels.
The service is currently under development

The component is named after Junior Gorg from the TV-show Fraggle Rock (http://muppet.wikia.com/wiki/Junior_Gorg).

## How it works
 The component only removes resources that have the label "ttl".
 ttl is a durationString "6d", "2h", that is declared in aurora-config as the property "ttl" or "env/ttl"
 The component boober labels openshift/kuberneetes objects with the ttl label.  


## TODO:
 - get to work with icinga (?)
 - ApplicationDeployment deletion logging (-)
 - Write tests / fix build (-)
 - Implement ttl in scripts (Jenkins/buildConfigs/ApplicationDeployments/Projects) (-)
 - fix boober removeAfter -> ttl (-)
 - convert fra 6d til Duration: (-)
 https://docs.spring.io/spring-boot/docs/2.0.0.M5/api/org/springframework/boot/actuate/autoconfigure/metrics/export/StringToDurationConverter.html
 
 - Check if roles are correct for aurora-deleter (1/2)
 
### TODO Boober
 - removeAfter skal byttes ut med TTL. (-)
                 ?.let { StringToDurationConverter().convert(it) },
 - DeployMapper. Ikke lagre ttl eller env/ttl som Duration men som String (-)
 - AuroraDeployEnvironment (?)
 - Build må fikses i v6 av pipeline script. Trenger ikke gjøre nå.

### CODE INFO
 - oc login $DOCKERURL