import groovy.json.*

node {

    deleteDir()

        stage('Clone Repository') {
                dir ('playbooks') {
                    git branch: "$branch", url: "https://github.com/pjitss/cassandra-cluster.git"
        }

        stage('Determine Connection Type') {
        // Fetch inventory details for the specified group (based on task)
        def connectionType = sh(
        script: """
            if [ "${task}" = "upload" ] || [ "${task}" = "migrate" ]; then
                grep -A1 "\\[${task}\\]\" playbooks/env/${envname}/${appname}/${appname}.inv | grep ansible_connection | awk -F 'ansible_connection=' '{print $2}' | awk '{print $1}' | tr -d "'"
            else
                grep -A1 "\\[${entity_lower}_${apptype_lower}\\]" playbooks/env/${envname}/${appname}/${appname}.inv | grep ansible_connection | awk -F 'ansible_connection=' '{print $2}' | awk '{print $1}' | tr -d "'"
            fi
        """,
        returnStdout: true
        ).trim()

        print "connection type is : ${connectionType}"

        ansiblePlaybook(
            playbook: playbook,
            extras: "-i \"playbooks/env/${envname}/${APPNAME}/${APPNAME}.inv\" -e COMP=${task} -e app_name=${APPNAME} -e file_name=${FILENAME} -e ENVNAME=${ENVNAME} -e task=${task}"
        )
    }

    }
}
