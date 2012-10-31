/*
 * Copyright 2010 Grails Plugin Collective
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
grails.project.dependency.resolution = {
	inherits("global") {
		excludes "xml-apis"
	}
	log "warn"
	repositories {
		grailsCentral()
		grailsHome()
		mavenLocal()
		mavenCentral()
		mavenRepo "http://download.java.net/maven/2/"
	}
	plugins {
		build(":release:2.0.4", ":rest-client-builder:1.0.2") {
			export = false
		}
		compile (
			":hibernate:$grailsVersion",
			":build-test-data:0.2.3",
			":spock:0.7"
		) {
			export = false
		}
	}
	dependencies {
		test "hsqldb:hsqldb:1.8.0.10"
	}
}

grails.release.scm.enabled = false
