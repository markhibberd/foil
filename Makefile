MODULE = foil
VERSION = 1.1 

GEN = gen

SRC_PROD = src/prod
SRC_TEST = src/test
SRC_DEMO = src/demo

CLS_PROD = gen/classes/prod
CLS_TEST = gen/classes/test
CLS_DEMO = gen/classes/demo

CP_BASE = lib/compile/\*:lib/run/\*:lib/test/\*
CP_PROD = ${CP_BASE}:${CLS_PROD}
CP_TEST = ${CP_PROD}:${CLS_TEST}

DOC_PROD = ${GEN}/doc/prod

ETC = etc
DIST = ${GEN}/dist
WWW = ${ETC}/www

JAR = ${DIST}/${MODULE}.jar
JAR_SRC = ${DIST}/${MODULE}-src.jar

TAR = ${DIST}/${MODULE}-${VERSION}.tar.gz
ZIP = ${DIST}/${MODULE}-${VERSION}.zip

HASH = ${ETC}/sha1
HASH_JAR = ${JAR}.sha1
HASH_JAR_SRC = ${JAR_SRC}.sha1
HASH_TAR = ${TAR}.sha1
HASH_ZIP = ${ZIP}.sha1

LICENSES = etc/licenses
MANIFEST = etc/MANIFEST.MF
DIST_MANIFEST = ${GEN}/MANIFEST.MF
TAR_IMAGE = ${GEN}/image/${MODULE}-${VERSION}
RELEASE = ${GEN}/release/${VERSION}
PUBLISH_WWW = web@mth.io:${MODULE}.mth.io/data
PUBLISH_RELEASE = web@mth.io:${MODULE}.mth.io/data/release/.


DIRECTORIES = ${GEN} ${GEN}/tmp ${CLS_DEMO} ${CLS_PROD} ${CLS_TEST} ${DIST} ${TAR_IMAGE} ${TAR_IMAGE}/lib ${DOC_PROD} ${RELEASE} ${TAR_IMAGE}/doc/xray ${DEMO_TARGET}


.PHONY: clean dist doc compile size repl 

default: test dist

compile: clean ${CLS_PROD} ${CLS_TEST} ${CLS_DEMO}
	find ${SRC_PROD} -name "*.scala" -o -name "*.java" | xargs -s 30000 scalac -classpath ${CP_BASE} -d ${CLS_PROD}
	find ${SRC_PROD} -name "*.java" | xargs -s 30000 javac -source 1.5 -target 1.5 -classpath ${CP_PROD} -d ${CLS_PROD}
	find ${SRC_DEMO} -name "*.scala" -o -name "*.java" | xargs -s 30000 scalac -classpath ${CP_PROD} -d ${CLS_DEMO}
	find ${SRC_DEMO} -name "*.java" | xargs -s 30000 javac -source 1.5 -target 1.5 -classpath ${CP_PROD} -d ${CLS_DEMO}
	find ${SRC_TEST} -name "*.scala" | xargs -s 30000 scalac -classpath ${CP_PROD} -d ${CLS_TEST} 

test: compile
	scala -cp ${CP_TEST} org.scalatest.tools.Runner -p ${CLS_TEST} -oDFW 

${JAR}: compile ${DIST_MANIFEST} ${DIST}
	jar cfm ${JAR} ${DIST_MANIFEST} -C ${CLS_PROD} .

${JAR_SRC}: ${DIST}
	jar cf ${JAR_SRC} -C ${SRC_PROD} .

${TAR}: doc ${JAR} ${JAR_SRC} ${TAR_IMAGE} ${TAR_IMAGE}/lib ${TAR_IMAGE}/doc/xray ${DEMO_TARGET}
	cp -r ${DOC_PROD} ${TAR_IMAGE}/doc/api && \
	cp -r ${SRC_DEMO} ${TAR_IMAGE}/. && \
	cp lib/run/*.jar ${TAR_IMAGE}/lib && \
	cp ${JAR} ${JAR_SRC} ${TAR_IMAGE} && \
	cp README LICENSE ${TAR_IMAGE} && \
	cp -r ${LICENSES} ${TAR_IMAGE} && \
	tar cfz ${TAR} -C ${GEN}/image . && \
	(cd ${GEN}/image && zip -q ../../${ZIP} -r .)

dist: clean ${TAR}

www:
	rsync -aH --stats --exclude \*~ ${WWW}/ ${PUBLISH_WWW}

release: dist ${RELEASE} ${HASH_TAR} ${HASH_JAR} ${HASH_ZIP} ${HASH_JAR_SRC}
	cp -r ${TAR_IMAGE}/doc ${RELEASE}
	cp ${TAR} ${HASH_TAR} ${JAR} ${HASH_JAR} ${ZIP} ${HASH_ZIP} ${JAR_SRC} ${HASH_JAR_SRC} ${RELEASE}

publish: release
	rsync -aH --stats --exclude \*~ ${RELEASE} ${PUBLISH_RELEASE}

doc: compile ${DOC_PROD}
	(cd ${SRC_PROD} && \
	find io -name "*.scala" | xargs -s 30000 \
		scaladoc \
			-doc-title "scaladoc for [${MODULE} ${VERSION}]" \
			-doc-version ${VERSION} \
			-classpath ../../lib/run/\*:../../${CLS_PROD} \
			-d ../../${DOC_PROD})

${HASH_ZIP}: ${ZIP}
	${HASH} ${ZIP} > ${HASH_ZIP}

${HASH_JAR_SRC}: ${JAR_SRC}
	${HASH} ${JAR_SRC} > ${HASH_JAR_SRC}

${HASH_JAR}: ${JAR}
	${HASH} ${JAR} > ${HASH_JAR}

${HASH_TAR}: ${TAR}
	${HASH} ${TAR} > ${HASH_TAR}

${DIST_MANIFEST}: ${GEN}
	sed -e 's/VERSION/${VERSION}/' ${MANIFEST} > ${DIST_MANIFEST}

repl: compile
	scala -classpath ${CP_BASE}:${CLS_PROD}:${CLS_TEST}

size: 
	find ${SRC_PROD} -name "*.scala" | xargs wc | sort -n

${DIRECTORIES}:
	mkdir -p $@

clean:
	rm -rf ${GEN}; find . -name "*~" -o -name "*.core" -print0 | xargs -0 rm -f
