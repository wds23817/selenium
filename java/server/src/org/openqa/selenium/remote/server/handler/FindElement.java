// Licensed to the Software Freedom Conservancy (SFC) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The SFC licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.openqa.selenium.remote.server.handler;

import static java.util.logging.Level.SEVERE;

import com.google.common.collect.ImmutableMap;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.server.Session;

import java.util.Map;
import java.util.logging.Logger;

public class FindElement extends WebDriverHandler<Map<String, String>> {
  private static Logger log = Logger.getLogger(FindElement.class.getName());
  private volatile By by;

  public FindElement(Session session) {
    super(session);
  }

  @Override
  public void setJsonParameters(Map<String, Object> allParameters) throws Exception {
    super.setJsonParameters(allParameters);
    by = newBySelector().pickFromJsonParameters(allParameters);
  }

  @Override
  public Map<String, String> call() {
    try {
      WebElement element = getDriver().findElement(by);
      String elementId = getKnownElements().add(element);
      return ImmutableMap.of("ELEMENT", elementId);
    } catch (RuntimeException e) {
      // Add logging to detect when issue #1800 occurs
      if (!(e instanceof NoSuchElementException)) {
        log.log(SEVERE, "Unexpected exception during findElement", e);
      }
      throw e;
    }
  }

  @Override
  public String toString() {
    return String.format("[find element: %s]", by);
  }

}
