#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#  http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

#
# Makefile for Red Hat Support Tool
#

# ex. export APP_VERSION=1.0.0
#RPM_VERSION:=$(shell echo $(APP_VERSION) | sed "s/-/_/")
RPM_VERSION:=1.0.0
# ex. export APP_RELEASE=1
#RPM_RELEASE:=$(shell echo $(APP_RELEASE) | sed "s/-/_/")
RPM_RELEASE:=1
RPMTOP=$(shell bash -c "pwd -P")/target/rpm/redhat-support-lib-java
SPEC_FILE_IN=$(RPMTOP)/SPECS/redhat-support-lib-java.spec
SPEC_FILE=redhat-support-lib-java.spec
TARBALL=redhat-support-lib-java-$(RPM_VERSION).tar.gz
RPM=$(RPMTOP)/RPMS/redhat-support-lib-java-$(RPM_VERSION)-$(RPM_RELEASE).rpm
SRPM=$(RPMTOP)/SRPMS/redhat-support-lib-java-$(RPM_VERSION)-$(RPM_RELEASE).src.rpm

all: srpm	

.PHONY: tarball

tarball: $(TARBALL)
$(TARBALL): Makefile #$(TESTS)
	 tar -czf $(TARBALL) src/main/java/

rpm: $(RPM)
$(RPM): $(TARBALL)
	mvn install

srpm: rpm $(SPEC_FILE_IN)
	sed "s/Summary: redhat-support-lib-java/& \n$/Source0:        redhat-support-lib-java-1.0.0.tar.gz/" $(SPEC_FILE_IN) > $(SPEC_FILE)
	rpmbuild -bs \
	    --define="_topdir $(RPMTOP)" \
	    --define="_sourcedir ." $(SPEC_FILE)

clean:
	mvn clean
	rm *.tar.gz *.spec
