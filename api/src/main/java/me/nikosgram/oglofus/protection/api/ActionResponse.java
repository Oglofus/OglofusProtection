/*
 * Copyright 2014-2015 Nikos Grammatikos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://raw.githubusercontent.com/nikosgram13/OglofusProtection/master/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.nikosgram.oglofus.protection.api;

public enum ActionResponse {
    Successful,
    Failure;

    private String message = "";

    ActionResponse() {}

    public String getMessage() {
        return message;
    }

    public ActionResponse setMessage(String message) {
        this.message = message;
        return this;
    }

    @Override
    public String toString() {
        return name() + "{message='" + message + "'}";
    }
}
