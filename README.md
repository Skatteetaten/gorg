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
 - fix boober removeAfter -> ttl
 - Use ApplicationDeployment object
 - get to work with icinga
 - sjekke avhengigeter
 - Mokey har kode for å jobbe med ApplicationDeployment
 - Må også hente builds med ttl
 - convert fra 6d til Duration: https://docs.spring.io/spring-boot/docs/2.0.0.M5/api/org/springframework/boot/actuate/autoconfigure/metrics/export/StringToDurationConverter.html
 - Tweeke algoritme for å finne ut om noe skal slettes
 - Temporary(Application|EnvBuild) må kunne listes ut, markere hvem som skulle vært slettet
 - refactor DeleteApplication to just Delete ApplicationDeployment object
 - create deleteBuild
 - oc adm policy add-cluster-role-to-user system:aurora:aurora-deleter m79861 --as=system:admin
 - oc login $DOCKERURL
 - Check if roles are correct for aurora-deleter
 
### TODO Boober
 - removeAfter skal byttes ut med TTL. 
                 ?.let { StringToDurationConverter().convert(it) },
 - DeployMapper. Ikke lagre ttl eller env/ttl som Duration men som String
 - AuroraDeployEnvironment
 - Build må fikses i v6 av pipeline script. Trenger ikke gjøre nå.
