# Gorg
<img align="right" src="https://vignette.wikia.nocookie.net/muppet/images/3/38/JuniorGorg.jpg/revision/latest/scale-to-width-down/280?cb=20101120230645">

Gorg is a service that deletes projects and applications when they exists past their respective time-to-live labels.
The service is currently under development

The component is named after Junior Gorg from the TV-show Fraggle Rock (http://muppet.wikia.com/wiki/Junior_Gorg).


## How it works
 The component only removes rescources that have the label "removeAfter".
 removeAfter is epoch time that is declared in aurora-config as the property "ttl".
 The component boober labels openshift/kuberneetes objects with the removeAfter label.  
