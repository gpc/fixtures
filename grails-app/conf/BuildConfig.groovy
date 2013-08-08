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
grails.project.work.dir = 'target'

grails.project.dependency.resolution = {

	inherits 'global'
	log 'warn'

	repositories {
		grailsCentral()
	}
    dependencies {
        test "org.spockframework:spock-grails-support:0.7-groovy-2.0"
    }
	plugins {
		build ':release:2.2.1', ':rest-client-builder:1.0.3', {
			export = false
		}

		compile(":hibernate:$grailsVersion") {
			export = false
		}

		compile(":build-test-data:0.2.3") {
			export = false
		}

		test(":spock:0.7") {
            exclude "spock-grails-support"
			export = false
		}
	}
}
