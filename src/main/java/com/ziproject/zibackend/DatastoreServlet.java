/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ziproject.zibackend;

import com.ziproject.zibackend.common.GcsStorageService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;



public class DatastoreServlet extends HttpServlet {
  private static final String TYPE_METADATA = "metadata";
  private static final String TYPE_CONTENT = "content";

  private static final String STATUS_ATTR = "status";
  private static final String STATUS_FAIL = "fail";
  private static final String STATUS_SUCCESS = "success";

  private static final String REASON_ATTR = "reason";
  private static final String RESULT_ATTR = "result";


  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    String fileName = req.getParameter("name");
    String requestType = req.getParameter("type");
    if (fileName == null || requestType == null) {
      buildInvalidRequestResponse(resp);
      return;
    }

    switch (requestType) {
      case TYPE_CONTENT:
        setUpResponseFromGCSFileContent(resp, fileName);
        break;

      case TYPE_METADATA:
        setUpResponseFromGCSFileMetadata(resp, fileName);
        break;

      default:
        buildInvalidRequestResponse(
            resp, String.format("Request type not supported: %s", requestType));
        return;
    }

  }

  private void setUpResponseFromGCSFileMetadata(HttpServletResponse resp, String fileName)
      throws IOException {
    GcsStorageService gcsStorageService = GcsStorageService.getGcsStorageService();
    try {
      String metadataString = gcsStorageService.fetchTextFileMetadataString(fileName);
      buildSuccessResponse(resp, metadataString);
    } catch (FileNotFoundException ex) {
      buildInvalidRequestResponse(resp, "Specified file not found.");
    }
  }

  private void setUpResponseFromGCSFileContent(HttpServletResponse resp, String fileName)
      throws IOException {
    GcsStorageService gcsStorageService = GcsStorageService.getGcsStorageService();
    try {
      String fileContent = gcsStorageService.fetchTextFileContentString(fileName);
      buildSuccessResponse(resp, fileContent);
    } catch (FileNotFoundException ex) {
      buildInvalidRequestResponse(resp, "Specified file not found.");
    } catch (InvalidObjectException ex) {
      buildInvalidRequestResponse(resp, "Specified file is not a text file.");
    } // otherwise IOException comes from connection to GCS, just throw it
  }

  private void buildSuccessResponse(HttpServletResponse resp)
      throws IOException {
    buildSuccessResponse(resp, "Request successful but no result is returned.");
  }

  private void buildSuccessResponse(HttpServletResponse resp, String result)
      throws IOException {
    Map<String, String> responseBuilder = new LinkedHashMap<>();
    responseBuilder.put(STATUS_ATTR, STATUS_SUCCESS);
    responseBuilder.put(RESULT_ATTR, result);
    addJsonToResponse(resp, responseBuilder);
  }

  private void buildInvalidRequestResponse(HttpServletResponse resp)
      throws IOException {
    buildInvalidRequestResponse(
        resp, "Invalid request: name and type parameters are required.");
  }

  private void buildInvalidRequestResponse(HttpServletResponse resp, String reason)
      throws IOException {
    Map<String, String> responsePairs = new LinkedHashMap<>();
    responsePairs.put(STATUS_ATTR, STATUS_FAIL);
    responsePairs.put(REASON_ATTR, reason);
    addJsonToResponse(resp, responsePairs);
  }

  private void addJsonToResponse(HttpServletResponse resp, Map<String, String> response)
      throws IOException {
    resp.setContentType("text/plain");
    PrintWriter writer = resp.getWriter();
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("{ ");
    boolean firstTuple = true;
    for (String key : response.keySet()) {
      if (!firstTuple) {
        stringBuilder.append(", ");
      } else {
        firstTuple = false;
      }
      stringBuilder.append(String.format("\"%s\": \"%s\"", key, response.get(key)));
    }
    stringBuilder.append(" }");
    writer.println(stringBuilder.toString());
  }
}
