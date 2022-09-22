####
##
## Setup an ops4j environment for git-bash on Windows
##
##
## source ops4j/setup/git-bash-setup.sh
##
####

export JAVA_HOME='/c/Program Files/Java/jdk-16.0.2'
export JHOME='/c/Program\ Files/Java/jdk-16.0.2'

# Safeguard against continuously prepending to PATH.
if [ -z "${ORIGINAL_PATH}" ]
then
  export ORIGINAL_PATH="${PATH}"
fi

export OPS4J_HOME=/c/ws/ws1/ops4j
export PATH="${OPS4J_HOME}/setup/git-bash/bin:${JAVA_HOME}/bin:/c/tools/bin:${ORIGINAL_PATH}"

alias ops4j="cd $OPS4J_HOME"
export OPS4J_CLASSPATH=`find $OPS4J_HOME -name \*.jar | xargs echo | sed 's/ /:/g'`

export CLASSPATH=${OPS4J_CLASSPATH}
export JAVA_ARGS="-Xmx2g -server"
export JAVA="${JHOME}/bin/java ${JAVA_ARGS}"
alias ops="${JAVA} org.ops4j.CLI"

eval "$( ops env -node-op-namespace node: -is-ns in: -os-ns out:)"
