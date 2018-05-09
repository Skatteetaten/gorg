# Grog
<img align="right" src="https://vignette.wikia.nocookie.net/muppet/images/3/38/JuniorGorg.jpg/revision/latest/scale-to-width-down/240?cb=20101120230645">

Gorg is under development.

The component is named after Gorg from the TV-show Fraggle Rock (http://muppet.wikia.com/wiki/Mokey_Fraggle).

<!---
## How to run locally
 - Log into your OpenShift cluster with `oc`
 - Start the Main class
 - If you want to turn off caching set the mokey.cache property to false
 
## Test locally
Create a file  src/main/http/rest-client.env.json 

 ```
 {
    "local":{
      "apiUrl":"http://localhost:8080",
      "token":""
    },
    "utv-dev": {
      "apiUrl": "http://url-to-mokey-on-your-cluster"
      "token": ""
    }
  }
 ```
  
Fill in the token value with a valid ocp token from `oc whoami -t`
Run the http commands from Intellij
-->
