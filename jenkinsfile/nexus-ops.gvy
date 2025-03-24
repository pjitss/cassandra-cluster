import groovy.json.*

node {

    deleteDir()

        stage('Clone Repository') {
                dir ('playbooks') {
                    git branch: "$branch", url: "https://github.com/pjitss/cassandra-cluster.git"
        }

        print "task is ${task}"
        print "envname is ${envname}"
        print "appname is ${appname}"
        print "ENTITY is ${ENTITY}"
        print "APPTYPE is ${APPTYPE}"

        stage('Determine Connection Type') {
        // Fetch inventory details for the specified group (based on task)
            def connectionType = sh(
            script: """
                if [ "${task}" = "upload" ] || [ "${task}" = "migrate" ]; then
                    grep -A1 "\\[${task}\\]\" playbooks/env/${envname}/${appname}/${appname}.inv | grep ansible_connection | awk -F 'ansible_connection=' '{print $2}' | awk '{print $1}' | tr -d "'"
                else
                    grep -A1 "\\[${ENTITY}_${APPTYPE}\\]" playbooks/env/${envname}/${appname}/${appname}.inv | grep ansible_connection | awk -F 'ansible_connection=' '{print $2}' | awk '{print $1}' | tr -d "'"
                fi
            """,
            returnStdout: true
            ).trim()

            print "connection type is : ${connectionType}"
    }

    }
}
