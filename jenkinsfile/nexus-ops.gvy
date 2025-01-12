import groovy.json.*

node {

    deleteDir()

        stage('Clone Repository') {
                dir ('playbooks') {
                        git branch: "$branch", url: "https://github.com/pjitss/cassandra-cluster.git"
        }

        if (task == "upload") {
            stage("${task}ing zip file") {
                ansiblePlaybook(
                    playbook: "playbooks/App-Upload.yml",
                    extras: "-i \"playbooks/env/${envname}/${envname}.inv\" -e COMP=${task} -e app_name=${APPNAME} -e file_name=${FILENAME} -e ENVNAME=${ENVNAME} -e task=${TASK}"
                )
            }
        }

        if (task == "download") {
            stage("Verify checksum & ${task}ing zip file") {
                ansiblePlaybook(
                    playbook: "playbooks/Checksum-Verify.yml",
                    extras: " -e COMP=${task} -e app_name=${APPNAME} -e file_name=${FILENAME} -e ENVNAME=${ENVNAME} -e task=${TASK} -e jenkins_ws=${env.WORKSPACE} -e checksum=${CHECKSUM}"
                )

                ansiblePlaybook(
                    playbook: "playbooks/App-Download.yml",
                    extras: "-i \"playbooks/env/${envname}/${envname}.inv\" -e COMP=${task} -e app_name=${APPNAME} -e file_name=${FILENAME} -e ENVNAME=${ENVNAME} -e task=${TASK}"
                )
            }
        }
    }
}
