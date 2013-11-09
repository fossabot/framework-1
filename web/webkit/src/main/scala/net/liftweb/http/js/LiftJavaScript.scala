/*
 * Copyright 2013 WorldWide Conferencing, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.liftweb
package http
package js

import common._
import util.Props
import http.js._
import http.js.jquery.JQueryArtifacts

import JsCmds._
import JE._

/**
  * Create a javascript command that will initialize lift.js using LiftRules.
  */
object LiftJavaScript {

  def settings: JsObj = JsObj(
    "liftPath" -> LiftRules.liftPath,
    "ajaxRetryCount" -> Num(LiftRules.ajaxRetryCount.openOr(3)),
    "ajaxPostTimeout" -> LiftRules.ajaxPostTimeout,
    "gcPollingInterval" -> LiftRules.liftGCPollingInterval,
    "gcFailureRetryTimeout" -> LiftRules.liftGCFailureRetryTimeout,
    "cometGetTimeout" -> LiftRules.cometGetTimeout,
    "cometFailureRetryTimeout" -> LiftRules.cometFailureRetryTimeout,
    "cometServer" -> LiftRules.cometServer(),
    "logError" -> LiftRules.jsLogFunc.map(fnc => AnonFunc("msg", fnc(JsVar("msg")))).openOr(AnonFunc("msg", Noop)),
    "ajaxOnFailure" -> LiftRules.ajaxDefaultFailure.map(fnc => AnonFunc(fnc())).openOr(AnonFunc(Noop)),
    "ajaxOnStart" -> LiftRules.ajaxStart.map(fnc => AnonFunc(fnc())).openOr(AnonFunc(Noop)),
    "ajaxOnEnd" -> LiftRules.ajaxEnd.map(fnc => AnonFunc(fnc())).openOr(AnonFunc(Noop))
  )

  def initCmd(settings: JsObj): JsCmd = {
    val extendCmd = LiftRules.jsArtifacts match {
      case jsa: JQueryArtifacts => Call("window.lift.extend", JsVar("window", "lift_settings"), JsVar("window", "liftJQuery"))
      case _ => Call("window.lift.extend", JsVar("window", "lift_settings"), JsVar("window", "liftVanilla"))
    }

    SetExp(JsVar("window", "lift_settings"), settings) &
    extendCmd &
    Call("window.lift.init", JsVar("window", "lift_settings"))

    /*val extendCmd = LiftRules.jsArtifacts match {
      case jsa: JQueryArtifacts => Call("window.lift.extend", settings, JsVar("window.liftJQuery"))
      case _ => Call("window.lift.extend", settings, JsVar("window.liftVanilla"))
    }

    Call("window.lift.init", extendCmd)*/
  }
}