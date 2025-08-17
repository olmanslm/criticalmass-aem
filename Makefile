SHELL := /bin/bash
AUTHOR_PATH := /Users/olmans/projects/TDP/AEM/author/6.6/
PUBLISH_PATH := /Users/olmans/projects/TDP/AEM/publish/6.6/
AUTHOR_JAR := cq-quickstart-6.6.0.jar
PUBLISH_JAR := cq-quickstart-6.6.0.jar


JAVA_OPTS_AUTHOR := -Xmx2048m -Xms2048m -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8765 -Dsling.run.modes=author,local,crx3,crx3tar,tdp
JAVA_OPTS_PUBLISH := -Xmx1536m -Xms1536m -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8766 -Dsling.run.modes=publish,local,development,tdp

maven:
	mvn clean install

maven-notest:
	mvn clean install -DskipTests=true

author:
	mvn clean install -U -PautoInstallPackage 

publish:
	mvn clean install -PautoInstallPackagePublish

author-fast: maven-notest
	mvn clean install -PautoInstallPackage -DskipTests=true

publish-fast: maven-notest
	mvn clean install -PautoInstallPackagePublish -DskipTests=true

apps:
	cd ui.apps && mvn install -PautoInstallPackage

config:
	cd ui.config && mvn install -PautoInstallPackage

content:
	cd ui.content && mvn clean install -PautoInstallPackage

bundles:
	cd core && mvn clean install -PautoInstallBundle -DskipTests=true

checkstyle:
	cd core && mvn checkstyle:check

check-java17:
	@command -v jenv >/dev/null 2>&1 || \
	( echo '❌ jenv no está instalado. Por favor, instalalo con:' && \
	  echo '   brew install jenv' && \
	  exit 1 )
	@jenv versions | grep -q '17' || \
	( echo '❌ Java 17 no está configurado en jenv. Agregalo con algo como:' && \
	  echo '   jenv add /Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home' && \
	  exit 1 )

aem-start: check-java17
	cd $(AUTHOR_PATH) && \
	jenv local 17 && \
	java -jar $(JAVA_OPTS_AUTHOR) $(AUTHOR_JAR) -port 4502 -gui

aem-publish-start: check-java17
	cd $(PUBLISH_PATH) && \
	jenv local 17 && \
	java -jar $(JAVA_OPTS_PUBLISH) $(PUBLISH_JAR) -port 4503 -gui





# 	mvn -B \
#   org.apache.maven.plugins:maven-archetype-plugin:3.3.1:generate \
#   -DarchetypeGroupId=com.adobe.aem \
#   -DarchetypeArtifactId=aem-project-archetype \
#   -DarchetypeVersion=53 \
#   -DappTitle="Critical Mass Multi-Site Project" \
#   -DappId="criticalmass" \
#   -DgroupId="com.criticalmass" \
#   -DartifactId="criticalmass-aem" \
#   -Dversion="1.0.0-SNAPSHOT" \
#   -Dpackage="com.criticalmass.core" \
#   -DcqVersion="6.6.0" \
#   -DjavaVersion="17" \
#   -DappsFolderName="criticalmass" \
#   -DartifactName="Critical Mass AEM Project" \
#   -DfrontendModule="general" \
#   -DincludeExamples="y" \
#   -DincludeErrorHandler="y" \
#   -DincludeDispatcherConfig="y" \
#   -DincludeAemSampleContent="y" \
#   -DincludeOffline="n" \
#   -DsingleCountry="n" \
#   -DlanguageCountry="en_US,de_DE,es_MX,fr_FR" \
#   -DincludeCommerce="n" \
#   -Ddatalayer="y" \
#   -Damp="n"