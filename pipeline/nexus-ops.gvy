import groovy.json.*

node ("$ENV") {

    cleanWS ()

        stage('Clone Repository') {
                dir ('playbooks') {
                    withCredentials([string(credentialsId: 'myjenkins', variable: 'myjenkins')]) {
                        git branch: "$BRANCH",
                        credentialsId: 'myjenkins',
                        url: "https://pjitss:${myjenkins}@github.com/pjitss/nexus-upload.git"
                }
        }

        if (task = "upload") {
            stage("${task}ing zip file") {
                ansiblePlaybook(
                    playbook: "playbooks/App-Upload.yml",
                    extras: "-i \"playbooks/env/${envname}/${envname}.inv\" -e comp=${task} -e app_name=${APPNAME} -e file_name=${FILENAME} -e ENVNAME=${ENVNAME} -e task=${TASK}"
                )
            }
        }

        if (task = "download") {
            stage("Verify checksum & ${task}ing zip file") {
                ansiblePlaybook(
                    playbook: "playbooks/Checksum-Verify.yml",
                    extras: " -e comp=${task} -e app_name=${APPNAME} -e file_name=${FILENAME} -e ENVNAME=${ENVNAME} -e task=${TASK} -e jenkins_ws=${env.WORKSPACE} -e checksum=${CHECKSUM}"
                )

                ansiblePlaybook(
                    playbook: "playbooks/App-Download.yml",
                    extras: "-i \"playbooks/env/${envname}/${envname}.inv\" -e comp=${task} -e app_name=${APPNAME} -e file_name=${FILENAME} -e ENVNAME=${ENVNAME} -e task=${TASK}"
                )
            }
        }
    }
}