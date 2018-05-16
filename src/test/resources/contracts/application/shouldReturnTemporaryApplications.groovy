package application

import org.springframework.cloud.contract.spec.Contract

Contract.make {
  request {
    method 'GET'
    url '/api/apps'
    headers {
      contentType(applicationJson())
    }
  }
  response {
    status 200
    body(file('responses/applications.json'))
  }
}