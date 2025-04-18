#!/bin/bash

if [[ "$(pwd)" =~ .*"openapi".* ]]; then
    cd ..
fi

jq '
  # Remove API descriptions
  walk(
    if type == "object" then
      del(.info.description, .requestBody.required, .exclusiveMinimum, .get.description, .post.description, .put.description, .delete.description)
    else . end
  ) |

  # Replace tag "openapi" from version "3.1.0" to "3.0.1" (required for OpEx)
  walk(
    if type == "object" and has("openapi") and .openapi == "3.1.0" then
      .openapi = "3.0.1"
    else . end
  )
' openapi/openapi.json  > infra/api/openapi.json

