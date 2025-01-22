import groovy.json.*

node {

    deleteDir()

        stage('Clone Repository') {
                dir ('playbooks') {
                        git branch: "$branch", url: "https://github.com/pjitss/cassandra-cluster.git"
        }

        stage('Determine Connection Type') {
        // Fetch inventory details for the specified group (based on task)
        def inventoryGroupData = sh(
            script: """
                ansible-inventory -i playbooks/env/${envname}/${APPNAME}/${APPNAME}.inv --list | jq -r '.${task} | to_entries[] | .value.ansible_connection'
            """,
            returnStdout: true
        ).trim()

        // Detect connection type
        def connectionType = inventoryGroupData.contains('winrm') ? "winrm" : "ssh"
        echo "Connection type for group ${task}: ${connectionType}"

        // Determine the playbook dynamically
        def playbook = ""
        if (connectionType == "winrm") {
            if (task == "upload") {
                playbook = "playbooks/App-Upload_Windows.yml"
            } else {
                playbook = "playbooks/App-Download_Windows.yml"
            }
        } else {
            if (task == "upload") {
                playbook = "playbooks/App-Upload_Linux.yml"
            } else {
                playbook = "playbooks/App-Download_Linux.yml"
            }
        }

        echo "Selected playbook: ${playbook}"

        // Execute the selected playbook
        ansiblePlaybook(
            playbook: playbook,
            extras: "-i \"playbooks/env/${envname}/${APPNAME}/${APPNAME}.inv\" -e COMP=${task} -e app_name=${APPNAME} -e file_name=${FILENAME} -e ENVNAME=${ENVNAME} -e task=${task}"
        )
    }

    }
}
